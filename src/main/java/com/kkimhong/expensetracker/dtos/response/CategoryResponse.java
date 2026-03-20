package com.kkimhong.expensetracker.dtos.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        String description,
        BigDecimal limitPerSubmission,
        boolean active,
        Instant createdAt
) {
}
