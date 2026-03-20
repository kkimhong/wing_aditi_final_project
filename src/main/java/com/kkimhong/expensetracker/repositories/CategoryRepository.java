package com.kkimhong.expensetracker.repositories;

import com.kkimhong.expensetracker.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findByNameIgnoreCase(String name);
    List<Category> findByIsActiveTrueOrderByNameAsc();
    boolean existsByNameIgnoreCase(String name);
}
