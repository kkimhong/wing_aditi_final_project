package com.kkimhong.springsecurityauth.mapper;

import com.kkimhong.springsecurityauth.dtos.request.RoleRequest;
import com.kkimhong.springsecurityauth.dtos.response.RoleResponse;
import com.kkimhong.springsecurityauth.entities.RoleEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleResponse toResponse(RoleEntity role);
    RoleEntity toEntity(RoleRequest request);
    List<RoleResponse> toResponseList(List<RoleEntity> roles);
}
