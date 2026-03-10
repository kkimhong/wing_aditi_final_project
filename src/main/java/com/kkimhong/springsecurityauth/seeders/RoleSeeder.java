package com.kkimhong.springsecurityauth.seeders;

import com.kkimhong.springsecurityauth.entities.RoleEntity;
import com.kkimhong.springsecurityauth.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class RoleSeeder implements ApplicationRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(ApplicationArguments args) {
        seedRoles();
    }

    private void seedRoles() {
        List<String> roles = List.of("ADMIN", "USER", "MODERATOR");

        roles.forEach(roleName -> {
            if (!roleRepository.existsByName(roleName)) {
                RoleEntity role = RoleEntity.builder()
                        .name(roleName)
                        .build();
                roleRepository.save(role);
                log.info("Seeded role: {}", roleName);
            } else {
                log.info("Role already exists, skipping: {}", roleName);
            }
        });
    }
}
