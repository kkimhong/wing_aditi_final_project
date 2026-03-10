package com.kkimhong.springsecurityauth.seeders;
import com.kkimhong.springsecurityauth.entities.RoleEntity;
import com.kkimhong.springsecurityauth.entities.UserEntity;
import com.kkimhong.springsecurityauth.repositories.RoleRepository;
import com.kkimhong.springsecurityauth.repositories.UserRepository;
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
@Order(2)
public class UserSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        seedUsers();
    }

    private void seedUsers() {
        List<Map<String, String>> users = List.of(
                Map.of("firstname", "Super", "lastname", "Admin",
                        "email", "admin@gmail.com", "password", "Admin@123", "role", "ADMIN"),

                Map.of("firstname", "John", "lastname", "Doe",
                        "email", "john@gmail.com", "password", "User@123", "role", "USER"),

                Map.of("firstname", "Jane", "lastname", "Doe",
                        "email", "jane@gmail.com", "password", "Mod@123", "role", "MODERATOR")
        );

        users.forEach(userData -> {
            if (!userRepository.existsByEmail(userData.get("email"))) {

                RoleEntity role = roleRepository.findByName(userData.get("role"))
                        .orElseThrow(() -> new RuntimeException("Role not found: " + userData.get("role")));

                UserEntity user = UserEntity.builder()
                        .firstname(userData.get("firstname"))
                        .lastname(userData.get("lastname"))
                        .email(userData.get("email"))
                        .password(passwordEncoder.encode(userData.get("password")))
                        .role(role)
                        .build();

                userRepository.save(user);
                log.info("Seeded user: {}", userData.get("email"));
            } else {
                log.info("User already exists, skipping: {}", userData.get("email"));
            }
        });
    }
}
