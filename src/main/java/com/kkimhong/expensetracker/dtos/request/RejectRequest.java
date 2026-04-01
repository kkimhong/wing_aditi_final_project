package com.kkimhong.expensetracker.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RejectRequest(
        @NotBlank(message = "Rejection reason is required")
        @Size(max = 500, message = "Comment too long")
        String comment
) {}
