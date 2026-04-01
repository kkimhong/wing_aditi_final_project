package com.kkimhong.expensetracker.services;

import com.kkimhong.expensetracker.dtos.request.LoginRequest;
import com.kkimhong.expensetracker.dtos.request.RegisterRequest;
import com.kkimhong.expensetracker.dtos.response.AuthResponse;
import com.kkimhong.expensetracker.dtos.response.UserResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface AuthService {
    UserResponse registerUser(RegisterRequest request);
    AuthResponse loginUser(LoginRequest request, HttpServletResponse response);
//    AuthResponse getUserById(Long id);
    List<UserResponse> getAllUser();
//    AuthResponse updateUser(Long id);
//    void deleteUser(Long id);
}
