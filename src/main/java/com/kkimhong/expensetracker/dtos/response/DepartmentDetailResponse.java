package com.kkimhong.expensetracker.dtos.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record DepartmentDetailResponse(
        UUID id,
        String name,
        BigDecimal budgetLimit,
        int userCount,
        int expenseCount,
        Instant createdAt
) {
}
