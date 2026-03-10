package com.kkimhong.springsecurityauth.services.servicesImpl;

import com.kkimhong.springsecurityauth.configs.JwtService;
import com.kkimhong.springsecurityauth.dtos.request.LoginRequest;
import com.kkimhong.springsecurityauth.dtos.request.RegisterRequest;
import com.kkimhong.springsecurityauth.dtos.response.AuthResponse;
import com.kkimhong.springsecurityauth.entities.RoleEntity;
import com.kkimhong.springsecurityauth.entities.UserEntity;
import com.kkimhong.springsecurityauth.mapper.UserMapper;
import com.kkimhong.springsecurityauth.repositories.RoleRepository;
import com.kkimhong.springsecurityauth.repositories.UserRepository;
import com.kkimhong.springsecurityauth.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @Override
    public AuthResponse registerUser(RegisterRequest request) {
        RoleEntity role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));
        UserEntity user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public AuthResponse loginUser(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public AuthResponse getUserById(Long id) {
        return null;
    }

    @Override
    public List<AuthResponse> getAllUser() {
        return List.of();
    }

    @Override
    public AuthResponse updateUser(Long id) {
        return null;
    }

    @Override
    public void deleteUser(Long id) {

    }
}
