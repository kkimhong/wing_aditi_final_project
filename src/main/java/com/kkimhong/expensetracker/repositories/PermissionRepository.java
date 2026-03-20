package com.kkimhong.expensetracker.repositories;

import com.kkimhong.expensetracker.entities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    Optional<Permission> findByModuleAndAction(String module, String action);

    // All permissions for a list of role IDs
    @Query("""
        SELECT p FROM Permission p
        JOIN p.rolePermissions rp
        WHERE rp.role.id IN :roleIds
    """)
    List<Permission> findByRoleIds(@Param("roleIds") List<UUID> roleIds);

    // Check if permission exists
    boolean existsByModuleAndAction(String module, String action);
}
