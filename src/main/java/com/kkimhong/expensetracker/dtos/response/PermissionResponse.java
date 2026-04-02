package com.kkimhong.expensetracker.dtos.response;

import java.util.UUID;

public record PermissionResponse(
        UUID id,
        String module,
        String action,
        String key   // "module:action"
) {}
