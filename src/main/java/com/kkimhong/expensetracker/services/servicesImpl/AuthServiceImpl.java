package com.kkimhong.expensetracker.services.servicesImpl;

import com.kkimhong.expensetracker.configs.JwtService;
import com.kkimhong.expensetracker.dtos.request.LoginRequest;
import com.kkimhong.expensetracker.dtos.request.RegisterRequest;
import com.kkimhong.expensetracker.dtos.response.AuthResponse;
import com.kkimhong.expensetracker.dtos.response.UserResponse;
import com.kkimhong.expensetracker.entities.Department;
import com.kkimhong.expensetracker.entities.Role;
import com.kkimhong.expensetracker.entities.User;
import com.kkimhong.expensetracker.entities.UserRole;
import com.kkimhong.expensetracker.mapper.UserMapper;
import com.kkimhong.expensetracker.repositories.DepartmentRepository;
import com.kkimhong.expensetracker.repositories.RoleRepository;
import com.kkimhong.expensetracker.repositories.UserRepository;
import com.kkimhong.expensetracker.repositories.UserRoleRepository;
import com.kkimhong.expensetracker.services.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponse registerUser(RegisterRequest request) {

        if (userRepository.findByEmailAndIsActiveTrue(request.email()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setDepartment(department);
        User savedUser = userRepository.save(user);

        Role role = roleRepository.findById(request.roleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        UserRole userRole = UserRole.builder()
                .user(savedUser)
                .role(role)
                .department(department)
                .build();

        userRoleRepository.save(userRole);

        User userWithRoles = userRepository.findByEmailWithRoles(savedUser.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userMapper.toResponse(userWithRoles);
    }

    @Override
    public AuthResponse loginUser(LoginRequest request) {
//        long t1 = System.currentTimeMillis();
//
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(request.email(), request.password())
//        );
//        long t2 = System.currentTimeMillis();
//
//        User user = userRepository.findByEmailWithRoles(request.email()).orElseThrow();
//        long t3 = System.currentTimeMillis();
//
//        String token = jwtService.generateToken(user);
//        long t4 = System.currentTimeMillis();
//
//        log.info("BCrypt auth: {}ms | DB query: {}ms | JWT gen: {}ms | Total: {}ms",
//                t2 - t1, t3 - t2, t4 - t3, t4 - t1);
//
//        return new AuthResponse(token);
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        User user = userRepository.findByEmailWithRoles(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getEmail(), user.getPermissionKeys());
    }

//    @Override
//    public AuthResponse getUserById(Long id) {
//        return null;
//    }
//
//    @Override
//    public List<AuthResponse> getAllUser() {
//        return List.of();
//    }
//
//    @Override
//    public AuthResponse updateUser(Long id) {
//        return null;
//    }
//
//    @Override
//    public void deleteUser(Long id) {
//
//    }
}
