package com.kkimhong.expensetracker.services;

import com.kkimhong.expensetracker.dtos.request.LoginRequest;
import com.kkimhong.expensetracker.dtos.request.RegisterRequest;
import com.kkimhong.expensetracker.dtos.response.AuthResponse;
import com.kkimhong.expensetracker.dtos.response.UserResponse;

import java.util.List;

public interface AuthService {
    UserResponse registerUser(RegisterRequest request);
    AuthResponse loginUser(LoginRequest request);
//    AuthResponse getUserById(Long id);
//    List<AuthResponse> getAllUser();
//    AuthResponse updateUser(Long id);
//    void deleteUser(Long id);
}
