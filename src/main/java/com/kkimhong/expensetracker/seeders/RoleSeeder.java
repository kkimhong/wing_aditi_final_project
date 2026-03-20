package com.kkimhong.expensetracker.seeders;

import com.kkimhong.expensetracker.entities.Role;
import com.kkimhong.expensetracker.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class RoleSeeder implements ApplicationRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(ApplicationArguments args) {
        List<Map<String, Object>> roles = List.of(
                Map.of("name", "Employee", "priority", 10),
                Map.of("name", "Manager", "priority", 30),
                Map.of("name", "Admin", "priority", 50),
                Map.of("name", "Auditor", "priority", 35)
        );

        roles.forEach(data -> {
            String name = (String) data.get("name");
            if (roleRepository.findByName(name).isEmpty()) {
                roleRepository.save(Role.builder()
                        .name(name)
                        .priority((int) data.get("priority"))
                        .build());
                log.info("Seeded role: {}", name);
            }
        });
    }
}