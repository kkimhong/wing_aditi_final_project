package com.kkimhong.springsecurityauth.services;

import com.kkimhong.springsecurityauth.dtos.request.RoleRequest;
import com.kkimhong.springsecurityauth.dtos.response.RoleResponse;

import java.util.List;

public interface RoleService {
    RoleResponse createRole(RoleRequest request);
    RoleResponse getRoleById(Long id);
    List<RoleResponse> getAllRoles();
    RoleResponse updateRole(Long id, RoleRequest request);
    void deleteRole(Long id);
}
