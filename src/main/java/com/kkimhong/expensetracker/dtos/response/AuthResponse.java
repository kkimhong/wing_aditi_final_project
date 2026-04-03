package com.kkimhong.expensetracker.dtos.response;

import java.util.List;
import java.util.UUID;

public record AuthResponse(
        String token,
        String email,
        String firstname,
        String lastname,
        UUID roleId,
        String roleName,
        UUID userId,
        String departmentName,
        List<String> permissions,
        String expenseScope,
        UUID scopeDepartmentId
) {}
