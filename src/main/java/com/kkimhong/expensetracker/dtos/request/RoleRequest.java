package com.kkimhong.expensetracker.dtos.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

public record RoleRequest(
        @NotBlank(message = "Role name is required")
        @Size(min = 3, message = "Role name must be at least 3 characters")
        String name,
        String description,
        @Min(0) int priority
) {
}
