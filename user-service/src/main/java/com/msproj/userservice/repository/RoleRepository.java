package com.msproj.userservice.repository;

import com.msproj.userservice.entity.Role;
import com.msproj.userservice.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional; /**
 * Role Repository with simpler queries
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
    List<Role> findAllByOrderByName();
}
