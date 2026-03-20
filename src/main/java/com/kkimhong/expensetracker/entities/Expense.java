package com.kkimhong.expensetracker.entities;

import com.kkimhong.expensetracker.enums.ExpenseStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "expenses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitter_id", nullable = false)
    private User submitter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    @Builder.Default
    private String currency = "USD";

    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

    @Column(name = "receipt_url")
    private String receiptUrl;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ExpenseStatus status = ExpenseStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    // ── Lifecycle helpers ──────────────────────────────────

    public void approve(User approver) {
        this.status = ExpenseStatus.APPROVED;
        this.approvedBy = approver;
        this.approvedAt = Instant.now();
    }

    public void reject(User approver) {
        this.status = ExpenseStatus.REJECTED;
        this.approvedBy = approver;
        this.approvedAt = Instant.now();
    }

    public boolean isOwnedBy(UUID userId) {
        return this.submitter.getId().equals(userId);
    }

    // Validates amount doesn't exceed category limit if one is set
    public boolean exceedsCategoryLimit() {
        if (category == null || category.getLimitPerSubmission() == null) return false;
        return this.amount.compareTo(category.getLimitPerSubmission()) > 0;
    }
}