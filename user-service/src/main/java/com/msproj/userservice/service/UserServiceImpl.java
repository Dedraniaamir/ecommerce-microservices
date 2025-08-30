package com.msproj.userservice.service;



import com.msproj.userservice.entity.*;
import com.msproj.userservice.exception.UserAlreadyExistsException;
import com.msproj.userservice.exception.UserNotFoundException;
import com.msproj.userservice.service.*;
import com.msproj.userservice.dto.*;
import com.msproj.userservice.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * User Service Implementation demonstrating Service Layer Pattern
 *
 * Concepts Covered:
 * 1. @Service annotation for service layer
 * 2. @Transactional for database transactions
 * 3. Exception handling with custom exceptions
 * 4. SOLID principles - Single Responsibility
 * 5. Dependency Injection with @Autowired
 */
@Service
@Transactional  // All methods in this class will be transactional by default
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    // DEPENDENCY INJECTION through constructor (preferred over @Autowired fields)
    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    @Transactional  // Explicit transaction for create operation
    public UserResponseDto createUser(CreateUserRequestDto requestDto) {
        logger.info("Creating new user with username: {}", requestDto.getUsername());

        // Business validation
        validateUserCreation(requestDto);

        // Convert DTO to Entity using factory method
        User user = requestDto.toEntity();

        // Save user
        User savedUser = userRepository.save(user);

        // Assign default role based on user type
        assignDefaultRole(savedUser);

        logger.info("User created successfully with ID: {}", savedUser.getId());
        return UserResponseDto.fromUser(savedUser);
    }

    @Override
    @Transactional(readOnly = true)  // Read-only transaction for better performance
    public UserResponseDto getUserById(Long id) {
        logger.debug("Fetching user by ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        return UserResponseDto.fromUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserByUsername(String username) {
        logger.debug("Fetching user by username: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

        return UserResponseDto.fromUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        logger.debug("Fetching all users");

        return userRepository.findAll()
                .stream()
                .map(UserResponseDto::fromUser)  // Method reference
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getUsersByStatus(UserStatus status) {
        logger.debug("Fetching users by status: {}", status);

        return userRepository.findByStatus(status)
                .stream()
                .map(UserResponseDto::fromUser)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserResponseDto updateUserStatus(Long userId, UserStatus status) {
        logger.info("Updating user status. UserID: {}, New Status: {}", userId, status);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        user.setStatus(status);
        User updatedUser = userRepository.save(user);

        logger.info("User status updated successfully for ID: {}", userId);
        return UserResponseDto.fromUser(updatedUser);
    }

    @Override
    @Transactional
    public void addLoyaltyPoints(Long customerId, Integer points) {
        logger.info("Adding loyalty points. CustomerID: {}, Points: {}", customerId, points);

        User user = userRepository.findById(customerId)
                .orElseThrow(() -> new UserNotFoundException("Customer not found with ID: " + customerId));

        // POLYMORPHISM - Check if user is actually a Customer
        if (user instanceof Customer customer) {
            customer.addLoyaltyPoints(points);
            userRepository.save(customer);
            logger.info("Loyalty points added successfully. New total: {}", customer.getLoyaltyPoints());
        } else {
            throw new IllegalArgumentException("User is not a customer");
        }
    }

    @Override
    @Transactional
    public void assignRole(Long userId, RoleName roleName) {
        logger.info("Assigning role to user. UserID: {}, Role: {}", userId, roleName);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        // Check if user already has this role
        boolean hasRole = userRoleRepository.findByUserIdAndRoleName(userId, roleName).isPresent();
        if (!hasRole) {
            UserRole userRole = new UserRole(user, role);
            userRoleRepository.save(userRole);
            logger.info("Role assigned successfully");
        } else {
            logger.warn("User already has role: {}", roleName);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSummaryDto> getActiveUsersSummary() {
        logger.debug("Fetching active users summary");
        return userRepository.findActiveUsersSummary();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // Private helper methods

    private void validateUserCreation(CreateUserRequestDto requestDto) {
        // Check for existing username
        if (userRepository.existsByUsername(requestDto.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists: " + requestDto.getUsername());
        }

        // Check for existing email
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists: " + requestDto.getEmail());
        }
    }

    private void assignDefaultRole(User user) {
        RoleName defaultRole = user instanceof Customer ? RoleName.CUSTOMER : RoleName.ADMIN;

        Role role = roleRepository.findByName(defaultRole)
                .orElseGet(() -> createDefaultRole(defaultRole));

        UserRole userRole = new UserRole(user, role);
        userRoleRepository.save(userRole);
    }

    private Role createDefaultRole(RoleName roleName) {
        logger.info("Creating default role: {}", roleName);
        Role role = new Role(roleName, "Default " + roleName.getDisplayName() + " role");
        return roleRepository.save(role);
    }
}

