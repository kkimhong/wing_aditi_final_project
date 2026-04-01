package com.kkimhong.expensetracker.dtos.request;

import jakarta.validation.constraints.NotBlank;

public record PresignedUrlRequest(
        @NotBlank(message = "Filename is required")
        String filename,

        @NotBlank(message = "Content type is required")
        String contentType
) {}
