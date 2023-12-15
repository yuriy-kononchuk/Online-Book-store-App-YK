package com.example.project.service.impl;

import com.example.project.dto.category.CategoryDto;
import com.example.project.dto.category.CreateCategoryRequestDto;
import com.example.project.mapper.CategoryMapper;
import com.example.project.model.Category;
import com.example.project.repository.CategoryRepository;
import com.example.project.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDto save(CreateCategoryRequestDto requestDto) {
        Category category = categoryMapper.toEntity(requestDto);
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public List<CategoryDto> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).stream()
                .map(category -> categoryMapper.toDto(category))
                .toList();
    }

    @Override
    public CategoryDto getById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find a category by id: " + id));
        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDto update(Long id, CreateCategoryRequestDto requestDto) {
        Category categoryToUpdate = categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find a category by id: " + id));
        categoryToUpdate.setName(requestDto.name());
        categoryToUpdate.setDescription(requestDto.description());
        /*Category category = categoryMapper.toEntity(requestDto);
        Category categoryToUpdate = categoryRepository.findById(category.getId()).orElseThrow(
                () -> new EntityNotFoundException("Can't find a category by id: " + id));*/
        return categoryMapper.toDto(categoryRepository.save(categoryToUpdate));
    }

    @Override
    public void deleteById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find a category by id: " + id));
        categoryRepository.delete(category);
    }
}
