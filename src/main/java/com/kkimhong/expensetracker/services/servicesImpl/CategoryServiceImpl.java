package com.kkimhong.expensetracker.services.servicesImpl;

import com.kkimhong.expensetracker.dtos.request.CategoryRequest;
import com.kkimhong.expensetracker.dtos.response.CategoryResponse;
import com.kkimhong.expensetracker.entities.Category;
import com.kkimhong.expensetracker.mapper.CategoryMapper;
import com.kkimhong.expensetracker.repositories.CategoryRepository;
import com.kkimhong.expensetracker.services.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryMapper.toResponseList(
                categoryRepository.findAll(Sort.by("name").ascending()));
    }

    @Override
    public List<CategoryResponse> getActiveCategories() {
        return categoryMapper.toResponseList(
                categoryRepository.findByIsActiveTrueOrderByNameAsc());
    }

    @Override
    public CategoryResponse getCategoryById(UUID id) {
        return categoryMapper.toResponse(findOrThrow(id));
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.name())) {
            throw new IllegalArgumentException(
                    "Category already exists: " + request.name());
        }
        return categoryMapper.toResponse(
                categoryRepository.save(categoryMapper.toEntity(request)));
    }

    @Override
    public CategoryResponse updateCategory(UUID id, CategoryRequest request) {
        Category category = findOrThrow(id);

        // Check name conflict only if name changed
        if (!category.getName().equalsIgnoreCase(request.name()) &&
                categoryRepository.existsByNameIgnoreCase(request.name())) {
            throw new IllegalArgumentException(
                    "Category name already exists: " + request.name());
        }

        category.setName(request.name());
        category.setDescription(request.description());
        category.setLimitPerSubmission(request.limitPerSubmission());
        return categoryMapper.toResponse(category);
    }

    @Override
    public void deleteCategory(UUID id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Category not found");
        }
        categoryRepository.deleteById(id);
    }

    @Override
    public CategoryResponse toggleActive(UUID id) {
        Category category = findOrThrow(id);
        category.setActive(!category.isActive());
        return categoryMapper.toResponse(category);
    }

    private Category findOrThrow(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
    }
}
