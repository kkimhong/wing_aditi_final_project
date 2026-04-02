package com.kkimhong.expensetracker.services.servicesImpl;

import com.kkimhong.expensetracker.dtos.response.PermissionResponse;
import com.kkimhong.expensetracker.mapper.PermissionMapper;
import com.kkimhong.expensetracker.repositories.PermissionRepository;
import com.kkimhong.expensetracker.services.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    @Override
    public List<PermissionResponse> getAllPermissions() {
        return permissionMapper.toResponseList(permissionRepository.findAll());
    }
}
