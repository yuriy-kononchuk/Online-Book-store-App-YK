package com.example.project.mapper;

import com.example.project.dto.category.CategoryDto;
import com.example.project.dto.category.CreateCategoryRequestDto;
import com.example.project.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapperImpl implements CategoryMapper {
    @Override
    public CategoryDto toDto(Category category) {
        if (category == null) {
            return null;
        }
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        return dto;
    }

    @Override
    public Category toEntity(CreateCategoryRequestDto requestDto) {
        if (requestDto == null) {
            return null;
        }
        Category category = new Category();
        category.setName(requestDto.name());
        category.setDescription(requestDto.description());
        return category;
    }
}
