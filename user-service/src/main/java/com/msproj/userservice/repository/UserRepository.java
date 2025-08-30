package com.msproj.userservice.repository;

import com.msproj.userservice.dto.UserSummaryDto;
import com.msproj.userservice.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * User Repository demonstrating JPA Queries and Custom Methods
 *
 * JPA Concepts Covered:
 * 1. Method name queries (Spring Data magic)
 * 2. @Query with JPQL
 * 3. @Query with native SQL
 * 4. @Modifying for update/delete operations
 * 5. Custom repository methods
 * 6. Polymorphic queries (inheritance)
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 1. METHOD NAME QUERIES (Spring Data JPA magic)
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByStatus(UserStatus status);
    List<User> findByFirstNameContainingIgnoreCase(String firstName);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // 2. JPQL QUERIES with @Query annotation
    @Query("SELECT u FROM User u WHERE u.firstName = :firstName AND u.lastName = :lastName")
    List<User> findByFullName(@Param("firstName") String firstName, @Param("lastName") String lastName);

    @Query("SELECT u FROM User u JOIN u.roles ur JOIN ur.role r WHERE r.name = :roleName AND ur.isActive = true")
    List<User> findActiveUsersByRole(@Param("roleName") RoleName roleName);

    @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate ORDER BY u.createdAt DESC")
    List<User> findUsersCreatedBetween(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    // 3. POLYMORPHIC QUERIES - Working with inheritance
    @Query("SELECT c FROM Customer c WHERE c.loyaltyPoints >= :minPoints")
    List<Customer> findCustomersWithMinimumLoyaltyPoints(@Param("minPoints") Integer minPoints);

    @Query("SELECT a FROM Admin a WHERE a.department = :department")
    List<Admin> findAdminsByDepartment(@Param("department") String department);

    // 4. AGGREGATE QUERIES
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = :status")
    Long countUsersByStatus(@Param("status") UserStatus status);

    @Query("SELECT u.status, COUNT(u) FROM User u GROUP BY u.status")
    List<Object[]> getUserCountByStatus();

    // 5. NATIVE SQL QUERIES (when JPQL is not enough)
    @Query(value = "SELECT * FROM users u WHERE u.email LIKE %:domain% AND u.user_type = 'CUSTOMER'",
            nativeQuery = true)
    List<User> findCustomersByEmailDomain(@Param("domain") String domain);

    // 6. MODIFYING QUERIES for updates/deletes
    @Modifying
    @Transactional  // Required for modifying queries
    @Query("UPDATE User u SET u.status = :status WHERE u.id = :userId")
    int updateUserStatus(@Param("userId") Long userId, @Param("status") UserStatus status);

    @Modifying
    @Transactional
    @Query("UPDATE Customer c SET c.loyaltyPoints = c.loyaltyPoints + :points WHERE c.id = :customerId")
    int addLoyaltyPoints(@Param("customerId") Long customerId, @Param("points") Integer points);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserRole ur WHERE ur.user.id = :userId AND ur.role.name = :roleName")
    int removeUserRole(@Param("userId") Long userId, @Param("roleName") RoleName roleName);

    // 7. COMPLEX QUERIES with multiple joins
    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN u.roles ur " +
            "JOIN ur.role r " +
            "WHERE r.name IN :roleNames " +
            "AND u.status = 'ACTIVE' " +
            "AND ur.isActive = true " +
            "ORDER BY u.createdAt DESC")
    List<User> findActiveUsersWithAnyRole(@Param("roleNames") List<RoleName> roleNames);

    // 8. PROJECTION QUERIES - Return specific fields only
    @Query("SELECT new com.msproj.userservice.dto.UserSummaryDto(u.id, u.username, u.email, u.firstName, u.lastName) " +
            "FROM User u WHERE u.status = 'ACTIVE'")
    List<UserSummaryDto> findActiveUsersSummary();

    // 9. EXISTS QUERIES for efficient checking
    @Query("SELECT CASE WHEN COUNT(ur) > 0 THEN true ELSE false END " +
            "FROM UserRole ur WHERE ur.user.id = :userId AND ur.role.name = :roleName AND ur.isActive = true")
    boolean hasActiveRole(@Param("userId") Long userId, @Param("roleName") RoleName roleName);

    // 10. CUSTOM FINDER with Optional for better null handling
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.status = 'ACTIVE'")
    Optional<User> findActiveUserByUsername(@Param("username") String username);
}

