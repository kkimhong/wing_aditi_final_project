package com.kkimhong.springsecurityauth.controllers;

import com.kkimhong.springsecurityauth.dtos.request.LoginRequest;
import com.kkimhong.springsecurityauth.dtos.request.RegisterRequest;
import com.kkimhong.springsecurityauth.dtos.response.AuthResponse;
import com.kkimhong.springsecurityauth.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(AuthController.BASE_URL)
@RequiredArgsConstructor
public class AuthController {
    public static final String BASE_URL = "/api/v1/auth";
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.registerUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.loginUser(request));
    }
}
