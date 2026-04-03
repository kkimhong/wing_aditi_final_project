package com.kkimhong.expensetracker.services.servicesImpl;

import com.kkimhong.expensetracker.dtos.request.RoleRequest;
import com.kkimhong.expensetracker.dtos.response.RoleResponse;
import com.kkimhong.expensetracker.entities.Permission;
import com.kkimhong.expensetracker.entities.Role;
import com.kkimhong.expensetracker.entities.RolePermission;
import com.kkimhong.expensetracker.entities.RolePermissionId;
import com.kkimhong.expensetracker.exceptions.DuplicateResourceException;
import com.kkimhong.expensetracker.exceptions.ResourceNotFoundException;
import com.kkimhong.expensetracker.mapper.RoleMapper;
import com.kkimhong.expensetracker.repositories.PermissionRepository;
import com.kkimhong.expensetracker.repositories.RolePermissionRepository;
import com.kkimhong.expensetracker.repositories.RoleRepository;
import com.kkimhong.expensetracker.services.RoleService;
import jakarta.persistence.EntityManager;
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
    private final EntityManager entityManager;
    private final RoleMapper roleMapper;

    @Override
    public RoleResponse createRole(RoleRequest request) {
        if (roleRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("Role already exists: " + request.name());
        }

        Role role = roleMapper.toEntity(request);
        roleRepository.save(role);

        return roleMapper.toResponse(role);
    }

    @Override
    public RoleResponse updateRole(UUID id, RoleRequest request) {
        Role role = findByIdWithPermissionsOrThrow(id);

        if (!role.getName().equals(request.name()) && roleRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("Role name already taken: " + request.name());
        }

        role.setName(request.name());
        role.setDescription(request.description());
        role.setPriority(request.priority());

        return roleMapper.toResponse(role);
    }

    @Override
    public RoleResponse updatePermissions(UUID id, Set<UUID> permissionIds) {
        Role role = findByIdWithPermissionsOrThrow(id);
        updateRolePermissions(role, permissionIds);
        return roleMapper.toResponse(role);
    }

    @Override
    public void deleteRole(UUID id) {
        Role role = findRoleByIdOrThrow(id);
        roleRepository.delete(role);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getRoleById(UUID id) {
        return roleMapper.toResponse(findByIdWithPermissionsOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        return roleMapper.toResponseList(roleRepository.findAll());
    }

    // ─── Private Helpers ──────────────────────────────────────────────────────

    private void updateRolePermissions(Role role, Set<UUID> permissionIds) {

        role.getRolePermissions().clear();

        entityManager.flush(); // 💥 CRITICAL LINE

        if (permissionIds == null || permissionIds.isEmpty()) return;

        List<Permission> permissions = permissionRepository.findAllById(permissionIds);

        if (permissions.size() != permissionIds.size()) {
            throw new ResourceNotFoundException("Some permissions were not found");
        }

        permissions.forEach(permission -> {
            RolePermission rp = new RolePermission();

            RolePermissionId id = new RolePermissionId(
                    role.getId(),
                    permission.getId()
            );

            rp.setId(id);
            rp.setRole(role);
            rp.setPermission(permission);

            role.getRolePermissions().add(rp);
        });
    }

    // For operations that need permissions loaded (update, get)
    private Role findByIdWithPermissionsOrThrow(UUID id) {
        Role role = roleRepository.findByIdWithPermissions(id);
        if (role == null) {
            throw new ResourceNotFoundException("Role not found with id: " + id);
        }
        return role;
    }

    // For operations that don't need permissions loaded (delete)
    private Role findRoleByIdOrThrow(UUID id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
    }
}