package com.kkimhong.expensetracker.services.servicesImpl;

import com.kkimhong.expensetracker.dtos.request.RoleRequest;
import com.kkimhong.expensetracker.dtos.response.RoleResponse;
import com.kkimhong.expensetracker.entities.Role;
import com.kkimhong.expensetracker.mapper.RoleMapper;
import com.kkimhong.expensetracker.repositories.RoleRepository;
import com.kkimhong.expensetracker.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    public RoleResponse createRole(RoleRequest request) {
        // Check duplicate name
        if (roleRepository.existsByName(request.name())) {
            throw new RuntimeException("Role already exists: " + request.name());
        }

        Role role = roleMapper.toEntity(request);
        Role saved = roleRepository.save(role);
        return roleMapper.toResponse(saved);
    }

    @Override
    public RoleResponse getRoleById(UUID id) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
        return roleMapper.toResponse(role);
    }

    @Override
    public List<RoleResponse> getAllRoles() {
        return roleMapper.toResponseList(roleRepository.findAll());
    }

    @Override
    public RoleResponse updateRole(UUID id, RoleRequest request) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found with id: " + id));

        // Check if new name conflicts with another role
        if (!role.getName().equals(request.name()) && roleRepository.existsByName(request.name())) {
            throw new RuntimeException("Role name already taken: " + request.name());
        }

        role.setName(request.name());
        role.setDescription(request.name());

        return roleMapper.toResponse(roleRepository.save(role));
    }

    @Override
    public void deleteRole(UUID id) {
        if (!roleRepository.existsById(id)) {
            throw new RuntimeException("Role not found with id: " + id);
        }
        roleRepository.deleteById(id);
    }
}
