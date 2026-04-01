package com.kkimhong.expensetracker.dtos.response;

import java.util.List;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String firstname,
        String lastname,
        String email,
        String departmentName,
        String roleName
//        List<String> permissions  // ["expenses:create", "expenses:read_own"]
) {}