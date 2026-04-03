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
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

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

        if (userRepository.findByEmailAndActiveTrue(request.email()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        Role role = roleRepository.findById(request.roleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setDepartment(department);
        User savedUser = userRepository.save(user);

        UserRole userRole = UserRole.builder()
                .user(savedUser)
                .role(role)
                .department(department)
                .build();
        userRoleRepository.save(userRole);

        savedUser.setUserRoles(Set.of(userRole));
        return userMapper.toResponse(savedUser);
    }

    @Override
    public AuthResponse loginUser(LoginRequest request, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmailWithRoles(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String expenseScope;
        UUID scopeDepartmentId;

        List<UserRole> activeRoles = user.getUserRoles().stream()
                .filter(UserRole::isActive)
                .toList();

        boolean isCompanyWide = activeRoles.stream()
                .anyMatch(ur -> ur.getDepartment() == null);

        if (isCompanyWide) {
            expenseScope = "COMPANY";
            scopeDepartmentId = null;
        } else {
            expenseScope = "DEPARTMENT";
            scopeDepartmentId = activeRoles.stream()
                    .map(UserRole::getDepartment)
                    .filter(Objects::nonNull)
                    .map(Department::getId)
                    .findFirst()
                    .orElse(null);
        }

        String token = jwtService.generateToken(user, expenseScope, scopeDepartmentId);

        ResponseCookie cookie = ResponseCookie.from("access_token", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return new AuthResponse(
                token,
                user.getEmail(),
                user.getFirstname(),
                user.getLastname(),
                user.getRole().getId(),
                user.getRoleName(),
                user.getId(),
                user.getDepartmentName(),
                user.getPermissionKeys(),
                expenseScope,
                scopeDepartmentId
        );
    }

    @Override
    public void logoutUser(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    @Override
    public List<UserResponse> getAllUser() {
        return userMapper.toResponseList(
                userRepository.findAllWithRolesAndDepartments()
        );
    }
}
