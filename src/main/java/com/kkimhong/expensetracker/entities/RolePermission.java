package com.kkimhong.expensetracker.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "role_permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolePermission {

    @EmbeddedId
    private RolePermissionId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleId") // must match field name
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("permissionId") // must match field name
    private Permission permission;
}
