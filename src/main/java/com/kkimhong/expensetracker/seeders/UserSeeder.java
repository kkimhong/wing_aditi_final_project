package com.kkimhong.expensetracker.seeders;
import com.kkimhong.expensetracker.entities.Department;
import com.kkimhong.expensetracker.entities.Role;
import com.kkimhong.expensetracker.entities.User;
import com.kkimhong.expensetracker.entities.UserRole;
import com.kkimhong.expensetracker.repositories.DepartmentRepository;
import com.kkimhong.expensetracker.repositories.RoleRepository;
import com.kkimhong.expensetracker.repositories.UserRepository;
import com.kkimhong.expensetracker.repositories.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(3)
public class UserSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        seedUsers();
    }

    private void seedUsers() {
        List<Map<String, String>> users = List.of(
                Map.of("firstname", "Admin",
                        "lastname", "Super",
                        "email", "admin@company.com",
                        "password", "Admin@123",
                        "role", "Admin",
                        "department", "Finance"),

                Map.of("firstname", "John",
                        "lastname", "Doe",
                        "email", "john@company.com",
                        "password", "Manager@123",
                        "role", "Manager",
                        "department", "Engineering"),

                Map.of("firstname", "Jane",
                        "lastname", "Doe",
                        "email", "jane@company.com",
                        "password", "Employee@123",
                        "role", "Employee",
                        "department", "Engineering"),

                Map.of("firstname", "Tom",
                        "lastname", "Auditor",
                        "email", "tom@company.com",
                        "password", "Auditor@123",
                        "role", "Auditor",
                        "department", "Finance")
        );

        users.forEach(this::seedSingleUser);
    }

    private void seedSingleUser(Map<String, String> userData) {
        String email = userData.get("email");

        if (userRepository.findByEmailAndIsActiveTrue(email).isPresent()) {
            log.info("User already exists, skipping: {}", email);
            return;
        }

        // Resolve department
        Department department = departmentRepository.findByName(userData.get("department"))
                .orElseThrow(() -> new RuntimeException("Department not found: " + userData.get("department")));

        // Resolve role
        Role role = roleRepository.findByName(userData.get("role"))
                .orElseThrow(() -> new RuntimeException("Role not found: " + userData.get("role")));

        // Build and save user
        User user = User.builder()
                .firstname(userData.get("firstname"))
                .lastname(userData.get("lastname"))
                .email(email)
                .password(passwordEncoder.encode(userData.get("password")))
                .department(department)
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);

        // Assign role via UserRole — not user.setRole()
        UserRole userRole = UserRole.builder()
                .user(savedUser)
                .role(role)
                .department(department)  // department-scoped
                .build();

        userRoleRepository.save(userRole);

        log.info("Seeded user: {} with role: {}", email, role.getName());
    }
}
