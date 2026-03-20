package com.kkimhong.expensetracker.seeders;

import com.kkimhong.expensetracker.entities.Department;
import com.kkimhong.expensetracker.repositories.DepartmentRepository;
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
@Order(0)
public class DepartmentSeeder implements ApplicationRunner {

    private final DepartmentRepository departmentRepository;

    @Override
    public void run(ApplicationArguments args) {
        List<String> departments = List.of("Finance", "Engineering", "Operations", "HR");

        departments.forEach(name -> {
            if (departmentRepository.findByName(name).isEmpty()) {
                departmentRepository.save(Department.builder().name(name).build());
                log.info("Seeded department: {}", name);
            }
        });
    }
}
