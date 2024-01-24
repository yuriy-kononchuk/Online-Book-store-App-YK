package com.example.project.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.project.dto.book.BookDto;
import com.example.project.dto.category.CategoryDto;
import com.example.project.dto.category.CreateCategoryRequestDto;
import com.example.project.model.Category;
import com.example.project.repository.CategoryRepository;
import com.example.project.testrepository.TestCategoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Sql(scripts = "classpath:database/categories/add-test-categories.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/categories/remove-test-categories.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class CategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TestCategoryRepository testCategoryRepository;

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Create a new category is successful")
    void createCategory_ValidRequestDto_Success() throws Exception {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto("motivational",
                "motivation and self-development");
        CategoryDto expected = new CategoryDto()
                .setName(requestDto.name())
                .setDescription(requestDto.description());
        String jsonRequestBody = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/books/categories")
                        .content(jsonRequestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CategoryDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Get All categories")
    void getAll_ReturnsListAllCategoryDtos() throws Exception {
        List<CategoryDto> expected = new ArrayList<>();
        expected.add(new CategoryDto().setId(1L).setName("motivational").setDescription(
                "motivation and self-development"));
        expected.add(new CategoryDto().setId(2L).setName("historical").setDescription(
                "history researches"));
        expected.add(new CategoryDto().setId(3L).setName("programming").setDescription(
                "programming languages"));

        MvcResult result = mockMvc.perform(get("/books/categories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto[] actual = objectMapper.readValue(result.getResponse().getContentAsByteArray(),
                CategoryDto[].class);
        assertEquals(3, expected.size());
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Get category by Id")
    void getCategoryById_GivenValidId_ReturnsCategory() throws Exception {
        Long categoryId = 3L;

        MvcResult result = mockMvc.perform(get("/books/categories/" + categoryId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CategoryDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals("programming", actual.getName());
        assertEquals("programming languages", actual.getDescription());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Update category by Id is successful")
    void updateCategory_ValidCreateCategoryDto_Success() throws Exception {
        Long categoryId = 3L;
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto("Update",
                "category updated");
        String jsonRequestBody = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put("/books/categories/" + categoryId)
                        .content(jsonRequestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CategoryDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(requestDto.name(), actual.getName());
        assertEquals(requestDto.description(), actual.getDescription());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Delete category by Id is successful")
    void deleteCategory_ValidCategoryId_Success() throws Exception {
        Long categoryId = 3L;

        mockMvc.perform(delete("/books/categories/" + categoryId))
                .andExpect(status().isNoContent())
                .andReturn();

        Category actual = testCategoryRepository.findById(categoryId).orElse(null);
        List<Category> categories = testCategoryRepository.findAll();

        assertNull(actual);
        assertEquals(2, categories.size());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(scripts = {
            "classpath:database/categories/add-test-categories.sql",
            "classpath:database/books/add-test-books-to-books-table.sql",
            "classpath:database/books/add-test-books-to-books-categories-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/books/remove-test-books-from-books-table.sql",
            "classpath:database/books/remove-test-books-from-books-categories-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Get all books by category Id")
    void getBooksByCategoryId_GivenValidId_ReturnsListOfBookDtos() throws Exception {
        Long categoryId = 1L;
        List<BookDto> expected = new ArrayList<>();
        expected.add(new BookDto().setId(1L).setTitle("FirstBook").setAuthor("Mr.First")
                .setIsbn("555-111").setPrice(BigDecimal.valueOf(49.99))
                .setDescription("about first book").setCoverImage("first cover")
                .setCategoryIds(List.of(1L)));
        expected.add(new BookDto().setId(3L).setTitle("ThirdBook").setAuthor("Mr.Third")
                .setIsbn("555-333").setPrice(BigDecimal.valueOf(59.99))
                .setDescription("about third book").setCoverImage("third cover")
                .setCategoryIds(List.of(1L)));

        MvcResult result = mockMvc.perform(get("/books/categories/" + categoryId + "/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        BookDto[] actual = objectMapper.readValue(result.getResponse().getContentAsByteArray(),
                BookDto[].class);
        assertEquals(2, expected.size());
        assertEquals(expected, Arrays.stream(actual).toList());
    }
}
