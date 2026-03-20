package com.kkimhong.expensetracker.mapper;

import com.kkimhong.expensetracker.dtos.request.CategoryRequest;
import com.kkimhong.expensetracker.dtos.response.CategoryResponse;
import com.kkimhong.expensetracker.entities.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "isActive",  ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Category toEntity(CategoryRequest request);

    @Mapping(target = "active",  source = "active")
    CategoryResponse toResponse(Category category);

    List<CategoryResponse> toResponseList(List<Category> categories);
}
