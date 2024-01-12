package com.example.project.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.example.project.dto.category.CategoryDto;
import com.example.project.dto.category.CreateCategoryRequestDto;
import com.example.project.mapper.CategoryMapper;
import com.example.project.model.Category;
import com.example.project.repository.CategoryRepository;
import com.example.project.service.impl.CategoryServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("Verify save() method works correctly")
    void save_ValidCreateCategoryRequestDto_ReturnsCategoryDto() {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto("motivational",
                "motivation and self-development");

        Category category = new Category();
        category.setName(requestDto.name());
        category.setDescription(requestDto.description());

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName(category.getName());
        categoryDto.setDescription(category.getDescription());

        when(categoryMapper.toEntity(requestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto savedCategoryDto = categoryService.save(requestDto);

        AssertionsForClassTypes.assertThat(savedCategoryDto).isEqualTo(categoryDto);
        verify(categoryRepository, times(1)).save(category);
        verify(categoryMapper, times(1)).toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Verify findAll() method works correctly")
    void findAll_ValidPageable_ReturnsAllCategoryDtos() {
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("motivational");
        category1.setDescription("motivation and self-development");
        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("historical");
        category2.setDescription("history researches");

        CategoryDto categoryDto1 = new CategoryDto();
        categoryDto1.setId(category1.getId());
        categoryDto1.setName(category1.getName());
        categoryDto1.setDescription(category1.getDescription());
        CategoryDto categoryDto2 = new CategoryDto();
        categoryDto2.setId(category2.getId());
        categoryDto2.setName(category2.getName());
        categoryDto2.setDescription(category2.getDescription());

        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categories = List.of(category1, category2);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDto(category1)).thenReturn(categoryDto1);
        when(categoryMapper.toDto(category2)).thenReturn(categoryDto2);

        List<CategoryDto> categoryDtos = categoryService.findAll(pageable);

        AssertionsForClassTypes.assertThat(categoryDtos.size()).isEqualTo(2);
        AssertionsForClassTypes.assertThat(categoryDtos.get(0)).isEqualTo(categoryDto1);
        AssertionsForClassTypes.assertThat(categoryDtos.get(1)).isEqualTo(categoryDto2);

        verify(categoryRepository, times(1)).findAll(pageable);
        verify(categoryMapper,times(1)).toDto(category1);
        verify(categoryMapper,times(1)).toDto(category2);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Verify getById() method works correctly")
    void get_ValidCategoryId_ReturnsValidCategoryDto() {
        Long categoryId = 1L;
        Category category = new Category();
        category.setId(categoryId);
        category.setName("motivational");
        category.setDescription("motivation and self-development");

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName(category.getName());
        categoryDto.setDescription(category.getDescription());

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto categoryDtoById = categoryService.getById(categoryId);

        AssertionsForClassTypes.assertThat(categoryDtoById).isEqualTo(categoryDto);
        verify(categoryRepository, times(1)).findById(categoryId);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Verify getById() method with wrong Id throws exception")
    void get_WithNonValidCategoryId_ThrowsException() {
        Long categoryId = 100L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> categoryService.getById(categoryId));

        String expected = "Can't find a category by id: " + categoryId;
        String actual = exception.getMessage();
        assertEquals(expected,actual);
    }

    @Test
    @DisplayName("Verify update() method works correctly")
    void update_ValidCategoryIdAndCreateCategoryRequestDto_ReturnsCategoryDto() {
        Long categoryId = 1L;

        Category category = new Category();
        category.setId(categoryId);
        category.setName("oldName");
        category.setDescription("oldDescription");

        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto("historical",
                "history researches");

        Category updatedCategory = new Category();
        updatedCategory.setId(categoryId);
        updatedCategory.setName(requestDto.name());
        updatedCategory.setDescription(requestDto.description());

        CategoryDto updatedCategoryDto = new CategoryDto();
        updatedCategoryDto.setId(categoryId);
        updatedCategoryDto.setName(updatedCategory.getName());
        updatedCategoryDto.setDescription(updatedCategory.getDescription());

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);
        when(categoryMapper.toDto(updatedCategory)).thenReturn(updatedCategoryDto);

        CategoryDto expectedDto = categoryService.update(categoryId, requestDto);

        AssertionsForClassTypes.assertThat(expectedDto).isEqualTo(updatedCategoryDto);
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).save(any(Category.class));
        verify(categoryMapper, times(1)).toDto(updatedCategory);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Verify update() method with wrong Id throws exception")
    void update_WithNonValidCategoryId_ThrowsException() {
        Long categoryId = 100L;
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto("historical",
                "history researches");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> categoryService.update(categoryId, requestDto));

        String expected = "Can't find a category by id: " + categoryId;
        String actual = exception.getMessage();
        assertEquals(expected,actual);
    }

    @Test
    @DisplayName("Verify deleteById() method works correctly")
    void deleteById_ValidCategoryId_CategoryIsDeleted() {
        Long categoryId = 1L;
        Category category = new Category();
        category.setId(categoryId);
        category.setName("motivational");
        category.setDescription("motivation and self-development");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        categoryService.deleteById(categoryId);

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).delete(category);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Verify deleteById() method with wrong Id throws exception")
    void deleteById_WithNonValidCategoryId_ThrowsException() {
        Long categoryId = 100L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> categoryService.deleteById(categoryId));

        String expected = "Can't find a category by id: " + categoryId;
        String actual = exception.getMessage();
        assertEquals(expected,actual);
    }
}
