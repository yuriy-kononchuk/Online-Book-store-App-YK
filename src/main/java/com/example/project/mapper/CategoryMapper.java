package com.example.project.mapper;

import com.example.project.config.MapperConfig;
import com.example.project.dto.category.CategoryDto;
import com.example.project.dto.category.CreateCategoryRequestDto;
import com.example.project.model.Category;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toEntity(CreateCategoryRequestDto requestDto);
}


