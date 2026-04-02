package com.kkimhong.expensetracker.controllers;

import com.kkimhong.expensetracker.dtos.request.LoginRequest;
import com.kkimhong.expensetracker.dtos.request.RegisterRequest;
import com.kkimhong.expensetracker.dtos.response.AuthResponse;
import com.kkimhong.expensetracker.dtos.response.UserResponse;
import com.kkimhong.expensetracker.services.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(AuthController.BASE_URL)
@RequiredArgsConstructor
@Validated
public class AuthController {
    public static final String BASE_URL = "/api/v1";

    private final AuthService authService;

    @PostMapping("/users/register")
    @PreAuthorize("hasAuthority('users:create')")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.registerUser(request));
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('users:read')")
    public ResponseEntity<List<UserResponse>> gerAllUsers() {
        return ResponseEntity.ok(authService.getAllUser());
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody @Valid LoginRequest request,
            HttpServletResponse response
    ) {
        AuthResponse authResponse = authService.loginUser(request, response);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        authService.logoutUser(response);
        return ResponseEntity.noContent().build(); // 204
    }
}
