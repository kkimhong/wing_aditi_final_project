package com.kkimhong.expensetracker.dtos.response;

import java.util.List;
import java.util.UUID;

public record AuthResponse(
        String token,
        String email,
        String lastname,
        String firstname,
        UUID department,
        List<String> permissions
) {}
