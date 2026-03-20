package com.kkimhong.expensetracker.services.servicesImpl;

import com.kkimhong.expensetracker.dtos.request.DepartmentRequest;
import com.kkimhong.expensetracker.dtos.response.DepartmentResponse;
import com.kkimhong.expensetracker.entities.Department;
import com.kkimhong.expensetracker.mapper.DepartmentMapper;
import com.kkimhong.expensetracker.repositories.DepartmentRepository;
import com.kkimhong.expensetracker.services.DepartmentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    public List<DepartmentResponse> getAllDepartments() {
        // Use eager fetch query to avoid lazy load in mapper
        return departmentMapper.toResponseList(
                departmentRepository.findAllWithUsersAndExpenses()
        );
    }

    public DepartmentResponse getDepartmentById(UUID id) {
        Department dept = departmentRepository.findAllWithUsersAndExpenses()
                .stream()
                .filter(d -> d.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Department not found"));
        return departmentMapper.toResponse(dept);
    }

    public DepartmentResponse createDepartment(DepartmentRequest request) {
        if (departmentRepository.existsByName(request.name())) {
            throw new IllegalArgumentException(
                    "Department already exists: " + request.name());
        }
        Department saved = departmentRepository.save(
                departmentMapper.toEntity(request));
        return departmentMapper.toResponse(saved);  // new dept has no users — safe
    }

    public DepartmentResponse updateDepartment(UUID id, DepartmentRequest request) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department not found"));
        dept.setName(request.name());
        dept.setBudgetLimit(request.budgetLimit());
        return departmentMapper.toResponse(dept);
    }

    public void deleteDepartment(UUID id) {
        if (!departmentRepository.existsById(id)) {
            throw new EntityNotFoundException("Department not found");
        }
        departmentRepository.deleteById(id);
    }

}
