package com.kkimhong.expensetracker.repositories;

import com.kkimhong.expensetracker.entities.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {
    Optional<Department> findByName(String name);

    boolean existsByName(String name);

    // Use this when you need userCount or expenseCount
    @Query("""
                SELECT DISTINCT d FROM Department d
                LEFT JOIN FETCH d.users
                LEFT JOIN FETCH d.expenses
            """)
    List<Department> findAllWithUsersAndExpenses();

    // Use this for simple list — no counts needed
    List<Department> findAllByOrderByNameAsc();
}
