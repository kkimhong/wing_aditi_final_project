package com.kkimhong.expensetracker.repositories;

import com.kkimhong.expensetracker.entities.Permission;
import com.kkimhong.expensetracker.entities.Role;
import com.kkimhong.expensetracker.entities.RolePermission;
import com.kkimhong.expensetracker.entities.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermissionId> {
    boolean existsByRoleAndPermission(Role role, Permission permission);
}
