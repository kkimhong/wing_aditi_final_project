package com.kkimhong.expensetracker.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;  // "Travel", "Meals", "Office Supplies", etc.

    @Column(columnDefinition = "TEXT")
    private String description;

    // Optional per-expense spend cap for this category
    @Column(name = "limit_per_submission", precision = 10, scale = 2)
    private BigDecimal limitPerSubmission;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
