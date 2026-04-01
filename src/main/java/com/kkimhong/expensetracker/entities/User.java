package com.kkimhong.expensetracker.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User manager;

    @Column(nullable = false)
    private String firstname;

    @Column(nullable = false)
    private String lastname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private Set<UserRole> userRoles = new HashSet<>();

    @OneToMany(mappedBy = "submitter", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Expense> submittedExpenses = new ArrayList<>();

    // ── UserDetails implementation ─────────────────────────

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userRoles.stream()
                .filter(UserRole::isActive)
                .flatMap(ur -> ur.getRole().getRolePermissions().stream())
                .map(rp -> new SimpleGrantedAuthority(rp.getPermission().toKey()))
                .distinct()
                .toList();
    }

    @Override
    public String getUsername() {
        return this.email;  // email is the login identifier
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.active;
    }

    // ── Permission helper ──────────────────────────────────

    public Role getRole() {
        return userRoles.stream()
                .filter(UserRole::isActive)
                .findFirst()
                .map(UserRole::getRole)
                .orElse(null);
    }

    public String getRoleName() {
        Role role = getRole();
        return role != null ? role.getName() : null;
    }

    public String getDepartmentName() {
        return department != null ? department.getName() : null;
    }

    public List<String> getPermissionKeys() {
        return getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }
}