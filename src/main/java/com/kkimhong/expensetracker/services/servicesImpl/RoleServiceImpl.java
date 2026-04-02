package com.kkimhong.expensetracker.services.servicesImpl;

import com.kkimhong.expensetracker.dtos.request.RoleRequest;
import com.kkimhong.expensetracker.dtos.response.RoleResponse;
import com.kkimhong.expensetracker.entities.Permission;
import com.kkimhong.expensetracker.entities.Role;
import com.kkimhong.expensetracker.entities.RolePermission;
import com.kkimhong.expensetracker.exceptions.DuplicateResourceException;
import com.kkimhong.expensetracker.exceptions.ResourceNotFoundException;
import com.kkimhong.expensetracker.mapper.RoleMapper;
import com.kkimhong.expensetracker.repositories.PermissionRepository;
import com.kkimhong.expensetracker.repositories.RoleRepository;
import com.kkimhong.expensetracker.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;

    @Override
    public RoleResponse createRole(RoleRequest request) {
        if (roleRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("Role already exists: " + request.name());
        }

        Role role = roleMapper.toEntity(request);
        // We save the role first to get an ID
        roleRepository.save(role);

        // Assign permissions if provided
        if (request.permissionIds() != null && !request.permissionIds().isEmpty()) {
            updateRolePermissions(role, request.permissionIds());
        }

        return roleMapper.toResponse(role);
    }

    @Override
    public RoleResponse updateRole(UUID id, RoleRequest request) {
        // Use your specialized query to get the role + existing permissions in one go
        Role role = roleRepository.findByIdWithPermissions(id);

        // 1. Business Logic: Check name uniqueness
        if (!role.getName().equals(request.name()) && roleRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("Role name already taken: " + request.name());
        }

        // 2. Update basic fields
        role.setName(request.name());
        role.setDescription(request.description());
        role.setPriority(request.priority());

        // 3. Smart Permission Update
        // If permissionIds is NULL, the frontend didn't send it -> Keep existing perms.
        // If permissionIds is EMPTY [], the frontend wants to clear them.
        if (request.permissionIds() != null) {
            updateRolePermissions(role, request.permissionIds());
        }

        // Hibernate handles all DB sync automatically at the end of @Transactional
        return roleMapper.toResponse(role);
    }

    @Override
    public void deleteRole(UUID id) {
        Role role = findRoleById(id);
        // Because of CascadeType.ALL + orphanRemoval,
        // deleting the role automatically deletes its permissions.
        roleRepository.delete(role);
    }

    // --- High-Level Helpers ---

    private void updateRolePermissions(Role role, Set<UUID> permissionIds) {
        // Clear the memory list. Hibernate tracks this and generates DELETEs.
        role.getRolePermissions().clear();

        if (permissionIds.isEmpty()) return;

        // Fetch all requested permissions
        List<Permission> permissions = permissionRepository.findAllById(permissionIds);

        if (permissions.size() != permissionIds.size()) {
            throw new ResourceNotFoundException("Some permissions were not found");
        }

        // Map and add to the role's internal list
        permissions.forEach(permission -> {
            RolePermission rp = new RolePermission();
            rp.setRole(role);
            rp.setPermission(permission);
            role.getRolePermissions().add(rp);
        });
    }

    private Role findRoleById(UUID id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getRoleById(UUID id) {
        return roleMapper.toResponse(roleRepository.findByIdWithPermissions(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        return roleMapper.toResponseList(roleRepository.findAll());
    }
}