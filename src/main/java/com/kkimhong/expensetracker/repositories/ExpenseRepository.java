package com.kkimhong.expensetracker.repositories;

import com.kkimhong.expensetracker.entities.Expense;
import com.kkimhong.expensetracker.enums.ExpenseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    // My expenses — for Employee
    @Query("""
        SELECT e FROM Expense e
        LEFT JOIN FETCH e.category
        LEFT JOIN FETCH e.submitter
        LEFT JOIN FETCH e.department
        WHERE e.submitter.id = :submitterId
        ORDER BY e.createdAt DESC
    """)
    List<Expense> findBySubmitterIdWithDetails(@Param("submitterId") UUID submitterId);

    // All expenses — for Admin/Auditor
    @Query("""
        SELECT e FROM Expense e
        LEFT JOIN FETCH e.category
        LEFT JOIN FETCH e.submitter
        LEFT JOIN FETCH e.department
        LEFT JOIN FETCH e.approvedBy
        ORDER BY e.createdAt DESC
    """)
    List<Expense> findAllWithDetails();

    // Department expenses — for Manager
    @Query("""
        SELECT e FROM Expense e
        LEFT JOIN FETCH e.category
        LEFT JOIN FETCH e.submitter
        LEFT JOIN FETCH e.department
        LEFT JOIN FETCH e.approvedBy
        WHERE e.department.id = :departmentId
        ORDER BY e.createdAt DESC
    """)
    List<Expense> findByDepartmentIdWithDetails(@Param("departmentId") UUID departmentId);

    // Pending approvals — for Manager
    @Query("""
        SELECT e FROM Expense e
        LEFT JOIN FETCH e.category
        LEFT JOIN FETCH e.submitter
        LEFT JOIN FETCH e.department
        WHERE e.department.id = :departmentId
        AND e.status = 'SUBMITTED'
        ORDER BY e.createdAt ASC
    """)
    List<Expense> findPendingByDepartment(@Param("departmentId") UUID departmentId);

    // Filtered expenses — for Admin
    @Query("""
        SELECT e FROM Expense e
        LEFT JOIN FETCH e.category
        LEFT JOIN FETCH e.submitter
        LEFT JOIN FETCH e.department
        LEFT JOIN FETCH e.approvedBy
        WHERE (:status IS NULL OR e.status = :status)
        AND (:departmentId IS NULL OR e.department.id = :departmentId)
        AND (:categoryId IS NULL OR e.category.id = :categoryId)
        AND (:startDate IS NULL OR e.expenseDate >= :startDate)
        AND (:endDate IS NULL OR e.expenseDate <= :endDate)
        ORDER BY e.createdAt DESC
    """)
    List<Expense> findAllFiltered(
            @Param("status") ExpenseStatus status,
            @Param("departmentId") UUID departmentId,
            @Param("categoryId") UUID categoryId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // Single expense with all details
    @Query("""
        SELECT e FROM Expense e
        LEFT JOIN FETCH e.category
        LEFT JOIN FETCH e.submitter
        LEFT JOIN FETCH e.department
        LEFT JOIN FETCH e.approvedBy
        WHERE e.id = :id
    """)
    Optional<Expense> findByIdWithDetails(@Param("id") UUID id);
}
