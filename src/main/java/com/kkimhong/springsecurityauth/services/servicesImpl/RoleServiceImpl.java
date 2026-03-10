package com.kkimhong.springsecurityauth.services.servicesImpl;

import com.kkimhong.springsecurityauth.dtos.request.RoleRequest;
import com.kkimhong.springsecurityauth.dtos.response.RoleResponse;
import com.kkimhong.springsecurityauth.entities.RoleEntity;
import com.kkimhong.springsecurityauth.mapper.RoleMapper;
import com.kkimhong.springsecurityauth.repositories.RoleRepository;
import com.kkimhong.springsecurityauth.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    public RoleResponse createRole(RoleRequest request) {
        // Check duplicate name
        if (roleRepository.existsByName(request.getName())) {
            throw new RuntimeException("Role already exists: " + request.getName());
        }

        RoleEntity role = roleMapper.toEntity(request);
        RoleEntity saved = roleRepository.save(role);
        return roleMapper.toResponse(saved);
    }

    @Override
    public RoleResponse getRoleById(Long id) {
        RoleEntity role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
        return roleMapper.toResponse(role);
    }

    @Override
    public List<RoleResponse> getAllRoles() {
        return roleMapper.toResponseList(roleRepository.findAll());
    }

    @Override
    public RoleResponse updateRole(Long id, RoleRequest request) {
        RoleEntity role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));

        // Check if new name conflicts with another role
        if (!role.getName().equals(request.getName()) &&
                roleRepository.existsByName(request.getName())) {
            throw new RuntimeException("Role name already taken: " + request.getName());
        }

        role.setName(request.getName());
        role.setDescription(request.getDescription());

        return roleMapper.toResponse(roleRepository.save(role));
    }

    @Override
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new RuntimeException("Role not found with id: " + id);
        }
        roleRepository.deleteById(id);
    }
}
