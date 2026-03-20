package com.kkimhong.expensetracker.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record DepartmentRequest(
        @NotBlank(message = "Department name is required")
        @Size(max = 255, message = "Name too long")
        String name,

        @DecimalMin(value = "0.00", message = "Budget cannot be negative")
        @Digits(integer = 10, fraction = 2, message = "Invalid budget format")
        BigDecimal budgetLimit
) {
}
