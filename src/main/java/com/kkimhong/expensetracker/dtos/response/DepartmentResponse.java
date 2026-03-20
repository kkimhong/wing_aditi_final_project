package com.kkimhong.expensetracker.dtos.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record DepartmentResponse(
        UUID id,
        String name,
        BigDecimal budgetLimit,
        int userCount,        // just the count — not the full list
        Instant createdAt
) {
}
