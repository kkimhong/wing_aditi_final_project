package com.kkimhong.springsecurityauth.mapper;

import com.kkimhong.springsecurityauth.dtos.request.RegisterRequest;
import com.kkimhong.springsecurityauth.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "password", ignore = true) // handled separately (encoded)
    UserEntity toEntity(RegisterRequest request);
}
