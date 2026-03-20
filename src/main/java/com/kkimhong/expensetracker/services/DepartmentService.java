package com.kkimhong.expensetracker.services;

import com.kkimhong.expensetracker.dtos.request.DepartmentRequest;
import com.kkimhong.expensetracker.dtos.response.DepartmentResponse;

import java.util.List;
import java.util.UUID;

public interface DepartmentService {
    List<DepartmentResponse> getAllDepartments();
    DepartmentResponse getDepartmentById(UUID id);
    DepartmentResponse createDepartment(DepartmentRequest request);
    DepartmentResponse updateDepartment(UUID id, DepartmentRequest request);
    void deleteDepartment(UUID id);
}
