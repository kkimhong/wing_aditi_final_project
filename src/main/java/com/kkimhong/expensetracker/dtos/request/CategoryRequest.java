package com.kkimhong.expensetracker.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CategoryRequest(
        @NotBlank(message = "Department name is required")
        @Size(max = 255, message = "Name too long")
        String name,

        String description,

        @DecimalMin(value = "0.01", message = "Limit must be greater than 0")
        @Digits(integer = 8, fraction = 2)
        BigDecimal limitPerSubmission
) {
}
