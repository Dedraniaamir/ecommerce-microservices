package com.msproj.userservice.controller;


import com.msproj.userservice.dto.CreateUserRequestDto;
import com.msproj.userservice.dto.UserResponseDto;
import com.msproj.userservice.dto.UserSummaryDto;
import com.msproj.userservice.entity.RoleName;
import com.msproj.userservice.entity.UserStatus;
import com.msproj.userservice.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User Controller demonstrating REST API best practices
 *
 * Concepts Covered:
 * 1. RESTful API design
 * 2. HTTP status codes
 * 3. Request/Response DTOs
 * 4. Validation with @Valid
 * 5. Exception handling (will be handled by global handler)
 * 6. Logging for request tracing
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")  // For development - remove in production
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Create a new user (Customer or Admin)
     * POST /api/users
     */
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody CreateUserRequestDto requestDto) {
        logger.info("POST /api/users - Creating user with username: {}", requestDto.getUsername());

        UserResponseDto response = userService.createUser(requestDto);

        logger.info("User created successfully with ID: {}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get user by ID
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        logger.debug("GET /api/users/{} - Fetching user", id);

        UserResponseDto response = userService.getUserById(id);

        return ResponseEntity.ok(response);
    }

    /**
     * Get user by username
     * GET /api/users/username/{username}
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponseDto> getUserByUsername(@PathVariable String username) {
        logger.debug("GET /api/users/username/{} - Fetching user", username);

        UserResponseDto response = userService.getUserByUsername(username);

        return ResponseEntity.ok(response);
    }

    /**
     * Get all users
     * GET /api/users
     */
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        logger.debug("GET /api/users - Fetching all users");

        List<UserResponseDto> users = userService.getAllUsers();

        logger.debug("Found {} users", users.size());
        return ResponseEntity.ok(users);
    }

    /**
     * Get users by status
     * GET /api/users/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<UserResponseDto>> getUsersByStatus(@PathVariable UserStatus status) {
        logger.debug("GET /api/users/status/{} - Fetching users by status", status);

        List<UserResponseDto> users = userService.getUsersByStatus(status);

        return ResponseEntity.ok(users);
    }

    /**
     * Update user status
     * PATCH /api/users/{id}/status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponseDto> updateUserStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatusRequestDto requestDto) {
        logger.info("PATCH /api/users/{}/status - Updating status to: {}", id, requestDto.getStatus());

        UserResponseDto response = userService.updateUserStatus(id, requestDto.getStatus());

        return ResponseEntity.ok(response);
    }

    /**
     * Add loyalty points to customer
     * POST /api/users/{id}/loyalty-points
     */
    @PostMapping("/{id}/loyalty-points")
    public ResponseEntity<Void> addLoyaltyPoints(
            @PathVariable Long id,
            @RequestBody AddLoyaltyPointsRequestDto requestDto) {
        logger.info("POST /api/users/{}/loyalty-points - Adding {} points", id, requestDto.getPoints());

        userService.addLoyaltyPoints(id, requestDto.getPoints());

        return ResponseEntity.ok().build();
    }

    /**
     * Assign role to user
     * POST /api/users/{id}/roles
     */
    @PostMapping("/{id}/roles")
    public ResponseEntity<Void> assignRole(
            @PathVariable Long id,
            @RequestBody AssignRoleRequestDto requestDto) {
        logger.info("POST /api/users/{}/roles - Assigning role: {}", id, requestDto.getRoleName());

        userService.assignRole(id, requestDto.getRoleName());

        return ResponseEntity.ok().build();
    }

    /**
     * Get active users summary
     * GET /api/users/summary
     */
    @GetMapping("/summary")
    public ResponseEntity<List<UserSummaryDto>> getActiveUsersSummary() {
        logger.debug("GET /api/users/summary - Fetching active users summary");

        List<UserSummaryDto> summary = userService.getActiveUsersSummary();

        return ResponseEntity.ok(summary);
    }

    /**
     * Check if username exists
     * GET /api/users/check/username/{username}
     */
    @GetMapping("/check/username/{username}")
    public ResponseEntity<Boolean> checkUsernameExists(@PathVariable String username) {
        logger.debug("GET /api/users/check/username/{} - Checking username availability", username);

        boolean exists = userService.existsByUsername(username);

        return ResponseEntity.ok(exists);
    }

    /**
     * Check if email exists
     * GET /api/users/check/email/{email}")
     */
    @GetMapping("/check/email/{email:.+}")  // .+ to handle email with dots
    public ResponseEntity<Boolean> checkEmailExists(@PathVariable String email) {
        logger.debug("GET /api/users/check/email/{} - Checking email availability", email);

        boolean exists = userService.existsByEmail(email);

        return ResponseEntity.ok(exists);
    }
}

/**
 * Additional DTOs for specific operations
 */
class UpdateStatusRequestDto {
    private UserStatus status;

    public UpdateStatusRequestDto() {}

    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }
}

class AddLoyaltyPointsRequestDto {
    private Integer points;

    public AddLoyaltyPointsRequestDto() {}

    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }
}

class AssignRoleRequestDto {
    private RoleName roleName;

    public AssignRoleRequestDto() {}

    public RoleName getRoleName() { return roleName; }
    public void setRoleName(RoleName roleName) { this.roleName = roleName; }
}
