package com.kkimhong.expensetracker.dtos.request;

import java.util.Set;
import java.util.UUID;

public record AssignPermissionsRequest(Set<UUID> permissionIds) {
}
