package com.kkimhong.expensetracker.dtos.response;

public record PresignedUrlResponse(
        String uploadUrl,
        String publicUrl,
        String filePath
) {
}
