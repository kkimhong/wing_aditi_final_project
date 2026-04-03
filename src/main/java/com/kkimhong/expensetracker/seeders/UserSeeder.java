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
                        "role", "Admin"),

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

        if (userRepository.findByEmailAndActiveTrue(email).isPresent()) {
            log.info("User already exists, skipping: {}", email);
            return;
        }

        String deptName = userData.get("department");
        Department department = null;

        if (deptName != null) {
            department = departmentRepository.findByName(deptName)
                    .orElseThrow(() -> new RuntimeException("Department not found: " + deptName));
        }

        Role role = roleRepository.findByName(userData.get("role"))
                .orElseThrow(() -> new RuntimeException("Role not found: " + userData.get("role")));

        User user = User.builder()
                .firstname(userData.get("firstname"))
                .lastname(userData.get("lastname"))
                .email(email)
                .password(passwordEncoder.encode(userData.get("password")))
                .department(department)
                .active(true)
                .build();

        User savedUser = userRepository.save(user);

        UserRole userRole = UserRole.builder()
                .user(savedUser)
                .role(role)
                .department(department) // null for Admin, dept for everyone else
                .build();

        userRoleRepository.save(userRole);

        log.info("Seeded user: {} with role: {} | scope: {}",
                email, role.getName(), department == null ? "COMPANY" : department.getName());
    }
}
