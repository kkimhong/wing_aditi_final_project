package com.kkimhong.expensetracker.repositories;

import com.kkimhong.expensetracker.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {
}
