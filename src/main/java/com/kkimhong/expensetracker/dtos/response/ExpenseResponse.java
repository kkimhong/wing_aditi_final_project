package com.kkimhong.expensetracker.dtos.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ExpenseResponse(
        UUID id,
        String title,
        BigDecimal amount,
        String currency,
        String category,
        UUID categoryId,
        LocalDate expenseDate,
        String notes,
        String receiptUrl,
        String status,
        String submittedBy,
        UUID submitterId,
        String departmentName,
        String approvedBy,
        Instant approvedAt,
        Instant createdAt,
        Instant updatedAt
) {
}
