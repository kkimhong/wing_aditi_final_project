package com.kkimhong.expensetracker.repositories;

import com.kkimhong.expensetracker.entities.Permission;
import com.kkimhong.expensetracker.entities.Role;
import com.kkimhong.expensetracker.entities.RolePermission;
import com.kkimhong.expensetracker.entities.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermissionId> {
    boolean existsByRoleAndPermission(Role role, Permission permission);
    @Modifying
    @Query("DELETE FROM RolePermission rp WHERE rp.role.id = :roleId")
    void deleteAllByRoleId(@Param("roleId") UUID roleId);
}
