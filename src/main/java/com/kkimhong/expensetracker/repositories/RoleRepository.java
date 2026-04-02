package com.kkimhong.expensetracker.repositories;

import com.kkimhong.expensetracker.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    boolean existsByName(String name);

    Optional<Role> findByName(String name);

    @Query("""
            SELECT r FROM Role r
            LEFT JOIN FETCH r.rolePermissions rp
            LEFT JOIN FETCH rp.permission
            WHERE r.id = :id
            """)
    Role findByIdWithPermissions(UUID id);
}
