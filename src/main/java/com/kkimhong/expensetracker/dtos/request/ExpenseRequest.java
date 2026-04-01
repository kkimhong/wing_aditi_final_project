package com.kkimhong.expensetracker.dtos.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ExpenseRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title too long")
        String title,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        @Digits(integer = 10, fraction = 2, message = "Invalid amount format")
        BigDecimal amount,

        @Size(min = 3, max = 3, message = "Currency must be 3-letter ISO code")
        String currency,

        @NotNull(message = "Category is required")
        UUID categoryId,

        @NotNull(message = "Expense date is required")
        @PastOrPresent(message = "Expense date cannot be in the future")
        LocalDate expenseDate,

        String notes,

        String receiptUrl
) {
}
