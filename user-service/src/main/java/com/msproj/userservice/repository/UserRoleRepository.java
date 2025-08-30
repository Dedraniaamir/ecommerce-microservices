package com.msproj.userservice.repository;

import com.msproj.userservice.entity.RoleName;
import com.msproj.userservice.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional; /**
 * UserRole Repository for managing user-role relationships
 */
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    List<UserRole> findByUserIdAndIsActive(Long userId, Boolean isActive);

    List<UserRole> findByRoleNameAndIsActive(RoleName roleName, Boolean isActive);

    @Query("SELECT ur FROM UserRole ur WHERE ur.user.id = :userId AND ur.role.name = :roleName")
    Optional<UserRole> findByUserIdAndRoleName(@Param("userId") Long userId, @Param("roleName") RoleName roleName);

    @Modifying
    @Transactional
    @Query("UPDATE UserRole ur SET ur.isActive = false WHERE ur.user.id = :userId")
    int deactivateAllUserRoles(@Param("userId") Long userId);
}
