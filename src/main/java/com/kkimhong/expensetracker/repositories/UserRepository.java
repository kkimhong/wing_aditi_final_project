package com.kkimhong.expensetracker.repositories;

import com.kkimhong.expensetracker.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmail(String email);

    Optional<User> findByEmailAndActiveTrue(String email);

    List<User> findByDepartmentId(UUID departmentId);

    @Query("""
                SELECT u FROM User u
                LEFT JOIN FETCH u.userRoles ur
                LEFT JOIN FETCH ur.role r
                LEFT JOIN FETCH r.rolePermissions rp
                LEFT JOIN FETCH rp.permission
                WHERE u.email = :email
                AND u.active = true
            """)
    Optional<User> findByEmailWithRoles(@Param("email") String email);

    @Query("""
    SELECT DISTINCT u FROM User u
    LEFT JOIN FETCH u.department d
    LEFT JOIN FETCH u.userRoles ur
    LEFT JOIN FETCH ur.role r
    LEFT JOIN FETCH r.rolePermissions rp
    LEFT JOIN FETCH rp.permission
    ORDER BY u.firstname ASC
""")
    List<User> findAllWithRolesAndDepartments();

    @Query("""
                SELECT u FROM User u
                LEFT JOIN FETCH u.userRoles ur
                LEFT JOIN FETCH ur.role r
                LEFT JOIN FETCH r.rolePermissions rp
                LEFT JOIN FETCH rp.permission
                WHERE u.id = :id
                AND u.active = true
            """)
    Optional<User> findByIdWithRoles(@Param("id") UUID id);

}
