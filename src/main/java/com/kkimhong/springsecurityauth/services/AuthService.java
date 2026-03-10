package com.kkimhong.springsecurityauth.services;

import com.kkimhong.springsecurityauth.dtos.request.LoginRequest;
import com.kkimhong.springsecurityauth.dtos.request.RegisterRequest;
import com.kkimhong.springsecurityauth.dtos.response.AuthResponse;

import java.util.List;

public interface AuthService {
    AuthResponse registerUser(RegisterRequest request);
    AuthResponse loginUser(LoginRequest request);
    AuthResponse getUserById(Long id);
    List<AuthResponse> getAllUser();
    AuthResponse updateUser(Long id);
    void deleteUser(Long id);
}
