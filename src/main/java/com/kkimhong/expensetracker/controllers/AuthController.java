package com.kkimhong.expensetracker.controllers;

import com.kkimhong.expensetracker.dtos.request.LoginRequest;
import com.kkimhong.expensetracker.dtos.request.RegisterRequest;
import com.kkimhong.expensetracker.dtos.response.AuthResponse;
import com.kkimhong.expensetracker.dtos.response.UserResponse;
import com.kkimhong.expensetracker.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(AuthController.BASE_URL)
@RequiredArgsConstructor
@Validated
public class AuthController {
    public static final String BASE_URL = "/api/v1";
    private final AuthService authService;

    @PostMapping("/users/register")
    @PreAuthorize("hasAuthority('users:create')")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.registerUser(request));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.loginUser(request));
    }
}
