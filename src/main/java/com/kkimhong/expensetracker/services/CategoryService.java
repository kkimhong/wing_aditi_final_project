package com.kkimhong.expensetracker.services;

import com.kkimhong.expensetracker.dtos.request.CategoryRequest;
import com.kkimhong.expensetracker.dtos.response.CategoryResponse;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();
    List<CategoryResponse> getActiveCategories();
    CategoryResponse getCategoryById(UUID id);
    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse updateCategory(UUID id, CategoryRequest request);
    void deleteCategory(UUID id);
    CategoryResponse toggleActive(UUID id);
}
