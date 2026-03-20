package com.kkimhong.expensetracker.dtos.response;

import java.util.List;

public record AuthResponse(
        String token,
        String email,
        List<String> permissions
) {}
