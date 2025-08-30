package com.msproj.userservice.service;

import com.msproj.userservice.dto.CreateUserRequestDto;
import com.msproj.userservice.dto.UserResponseDto;
import com.msproj.userservice.dto.UserSummaryDto;
import com.msproj.userservice.entity.RoleName;
import com.msproj.userservice.entity.UserStatus;

import java.util.List; /**
 * User Service Interface defining contract
 * Demonstrates SOLID principles - Interface Segregation
 */
public interface UserService {
    UserResponseDto createUser(CreateUserRequestDto requestDto);
    UserResponseDto getUserById(Long id);
    UserResponseDto getUserByUsername(String username);
    List<UserResponseDto> getAllUsers();
    List<UserResponseDto> getUsersByStatus(UserStatus status);
    UserResponseDto updateUserStatus(Long userId, UserStatus status);
    void addLoyaltyPoints(Long customerId, Integer points);
    void assignRole(Long userId, RoleName roleName);
    List<UserSummaryDto> getActiveUsersSummary();
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
