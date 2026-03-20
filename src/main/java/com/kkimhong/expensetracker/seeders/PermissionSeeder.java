package com.kkimhong.expensetracker.seeders;

import com.kkimhong.expensetracker.entities.Permission;
import com.kkimhong.expensetracker.entities.Role;
import com.kkimhong.expensetracker.entities.RolePermission;
import com.kkimhong.expensetracker.repositories.PermissionRepository;
import com.kkimhong.expensetracker.repositories.RolePermissionRepository;
import com.kkimhong.expensetracker.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(2)
public class PermissionSeeder implements ApplicationRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedPermissions();
        seedRolePermissions();
    }

    private void seedPermissions() {
        List<Map<String, String>> permissions = List.of(
                // expenses
                Map.of("module", "expenses", "action", "create"),
                Map.of("module", "expenses", "action", "read_own"),
                Map.of("module", "expenses", "action", "read_all"),
                Map.of("module", "expenses", "action", "approve"),
                Map.of("module", "expenses", "action", "reject"),

                // reports
                Map.of("module", "reports",  "action", "read"),
                Map.of("module", "reports",  "action", "export"),

                // users
                Map.of("module", "users",    "action", "create"),
                Map.of("module", "users",    "action", "read"),
                Map.of("module", "users",    "action", "update"),

                // settings
                Map.of("module", "settings", "action", "read"),
                Map.of("module", "settings", "action", "update"),

                // roles
                Map.of("module", "roles", "action", "create"),
                Map.of("module", "roles", "action", "read"),
                Map.of("module", "roles", "action", "update"),
                Map.of("module", "roles", "action", "delete"),

                // categories
                Map.of("module", "categories", "action", "create"),
                Map.of("module", "categories", "action", "read"),
                Map.of("module", "categories", "action", "update"),
                Map.of("module", "categories", "action", "delete"),

                // departments
                Map.of("module", "departments", "action", "create"),
                Map.of("module", "departments", "action", "read"),
                Map.of("module", "departments", "action", "update"),
                Map.of("module", "departments", "action", "delete")
        );

        permissions.forEach(p -> {
            String module = p.get("module");
            String action = p.get("action");
            if (permissionRepository.findByModuleAndAction(module, action).isEmpty()) {
                permissionRepository.save(Permission.builder()
                        .module(module)
                        .action(action)
                        .build());
                log.info("Seeded permission: {}:{}", module, action);
            }
        });
    }

    private void seedRolePermissions() {
        // What each role can do
        Map<String, List<String>> rolePermissions = Map.of(
                "Employee", List.of(
                        "expenses:create",
                        "expenses:read_own"
                ),
                "Manager", List.of(
                        "expenses:create",
                        "expenses:read_own",
                        "expenses:read_all",
                        "expenses:approve",
                        "expenses:reject",
                        "reports:read"
                ),
                "Admin", List.of(
                        "expenses:create",
                        "expenses:read_own",
                        "expenses:read_all",
                        "expenses:approve",
                        "expenses:reject",
                        "reports:read",
                        "reports:export",
                        "users:create",
                        "users:read",
                        "users:update",
                        "settings:read",
                        "settings:update",
                        "categories:create",
                        "categories:read",
                        "categories:update",
                        "categories:delete",
                        "roles:create",
                        "roles:read",
                        "roles:update",
                        "roles:delete",
                        "departments:create",
                        "departments:read",
                        "departments:update",
                        "departments:delete"

                ),
                "Auditor", List.of(
                        "expenses:read_all",
                        "reports:read",
                        "reports:export",
                        "categories:read"
                )
        );

        rolePermissions.forEach((roleName, permKeys) -> {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

            permKeys.forEach(key -> {
                String[] parts = key.split(":");
                String module = parts[0];
                String action = parts[1];

                Permission permission = permissionRepository.findByModuleAndAction(module, action)
                        .orElseThrow(() -> new RuntimeException("Permission not found: " + key));

                if (!rolePermissionRepository.existsByRoleAndPermission(role, permission)) {
                    rolePermissionRepository.save(RolePermission.builder()
                            .role(role)
                            .permission(permission)
                            .build());
                    log.info("Assigned {}:{} to role: {}", module, action, roleName);
                }
            });
        });
    }
}
