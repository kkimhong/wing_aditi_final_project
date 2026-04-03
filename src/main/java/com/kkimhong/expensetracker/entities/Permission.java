package com.kkimhong.expensetracker.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "permissions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"module", "action"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // "expenses" | "reports" | "users" | "settings"
    @Column(nullable = false, length = 100)
    private String module;

    // "create" | "read_own" | "read_all" | "approve" | "reject" | "export" | "update"
    @Column(nullable = false, length = 50)
    private String action;

    @OneToMany(mappedBy = "permission", fetch = FetchType.LAZY)
    @Builder.Default
    private List<RolePermission> rolePermissions = new ArrayList<>();

    public String toKey() {
        return module + ":" + action;
    }
}