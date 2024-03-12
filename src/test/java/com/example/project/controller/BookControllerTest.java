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
import com.example.project.dto.book.BookDtoWithoutCategoryIds;
import com.example.project.dto.book.BookSearchParameters;
import com.example.project.dto.book.CreateBookRequestDto;
import com.example.project.model.Book;
import com.example.project.model.Category;
import com.example.project.testrepository.TestBookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TestBookRepository testBookRepository;

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @Sql(scripts = "classpath:database/books/remove-test-books-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Create a new book is successful")
    void createBook_ValidRequestDto_Success() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("New Book").setAuthor("Mr.Author").setIsbn("555-555")
                .setPrice(BigDecimal.valueOf(49.99)).setDescription("about new book")
                .setCoverImage("new cover").setCategoryIds(List.of(1L));
        BookDto expected = new BookDto()
                .setTitle(requestDto.getTitle()).setAuthor(requestDto.getAuthor())
                .setIsbn(requestDto.getIsbn()).setPrice(requestDto.getPrice())
                .setDescription(requestDto.getDescription())
                .setCoverImage(requestDto.getCoverImage())
                .setCategoryIds(requestDto.getCategoryIds());

        String jsonRequestBody = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/books")
                        .content(jsonRequestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getAuthor(), actual.getAuthor());
        assertEquals(expected.getIsbn(), actual.getIsbn());
        assertEquals(expected.getPrice(), actual.getPrice());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getCoverImage(), actual.getCoverImage());
        assertEquals(expected.getCategoryIds(), actual.getCategoryIds());
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @Sql(scripts = "classpath:database/books/add-test-books-to-books-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/remove-test-books-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Get All books")
    void getAll_ReturnsListAllBookDtos() throws Exception {
        List<BookDtoWithoutCategoryIds> expected = new ArrayList<>();
        expected.add(new BookDtoWithoutCategoryIds().setId(1L)
                .setTitle("FirstBook").setAuthor("Mr.First").setIsbn("555-111")
                .setPrice(BigDecimal.valueOf(49.99)).setDescription("about first book")
                .setCoverImage("first cover"));
        expected.add(new BookDtoWithoutCategoryIds().setId(2L)
                .setTitle("SecondBook").setAuthor("Mr.Second").setIsbn("555-222")
                .setPrice(BigDecimal.valueOf(39.99)).setDescription("about second book")
                .setCoverImage("second cover"));
        expected.add(new BookDtoWithoutCategoryIds().setId(3L)
                .setTitle("ThirdBook").setAuthor("Mr.Third").setIsbn("555-333")
                .setPrice(BigDecimal.valueOf(59.99)).setDescription("about third book")
                .setCoverImage("third cover"));

        MvcResult result = mockMvc.perform(get("/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDtoWithoutCategoryIds[] actual = objectMapper
                .readValue(result.getResponse().getContentAsByteArray(),
                BookDtoWithoutCategoryIds[].class);
        assertEquals(expected.size(), actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @Sql(scripts = {
            "classpath:database/books/add-test-books-to-books-table.sql",
            "classpath:database/categories/add-test-categories.sql",
            "classpath:database/books/add-test-books-to-books-categories-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/books/remove-test-books-from-books-table.sql",
            "classpath:database/categories/remove-test-categories.sql",
            "classpath:database/books/remove-test-books-from-books-categories-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Get book by Id")
    void getBookById_GivenValidId_ReturnsBookDto() throws Exception {
        Long bookId = 3L;

        MvcResult result = mockMvc.perform(get("/books/" + bookId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals("ThirdBook", actual.getTitle());
        assertEquals("Mr.Third", actual.getAuthor());
        assertEquals("555-333", actual.getIsbn());
        assertEquals(BigDecimal.valueOf(59.99), actual.getPrice());
        assertEquals("about third book", actual.getDescription());
        assertEquals("third cover", actual.getCoverImage());
        assertEquals(List.of(1L), actual.getCategoryIds());
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @Sql(scripts = "classpath:database/books/add-test-books-to-books-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/remove-test-books-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Delete book by Id is successful")
    void deleteBookById_ValidBookId_Success() throws Exception {
        Long bookId = 3L;

        mockMvc.perform(delete("/books/" + bookId))
                .andExpect(status().isNoContent())
                .andReturn();

        Book actual = testBookRepository.findById(bookId).orElse(null);
        List<Book> books = testBookRepository.findAll();

        assertNull(actual);
        assertEquals(2, books.size());
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @Sql(scripts = {
            "classpath:database/books/add-test-books-to-books-table.sql",
            "classpath:database/categories/add-test-categories.sql",
            "classpath:database/books/add-test-books-to-books-categories-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/books/remove-test-books-from-books-table.sql",
            "classpath:database/categories/remove-test-categories.sql",
            "classpath:database/books/remove-test-books-from-books-categories-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Update book by Id is successful")
    void updateBookById_ValidCreateBookDto_Success() throws Exception {
        Long bookId = 3L;
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("Update").setAuthor("Mr.Update").setIsbn("555-000")
                .setPrice(BigDecimal.valueOf(49.99)).setDescription("about updated book")
                .setCoverImage("updated cover").setCategoryIds(List.of(2L));
        String jsonRequestBody = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/books/" + bookId)
                        .content(jsonRequestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Book updatedBook = testBookRepository.findById(bookId).get();
        List<Long> categoryIds = updatedBook.getCategories().stream()
                .map(Category::getId)
                .toList();

        assertNotNull(updatedBook);
        assertNotNull(updatedBook.getId());
        assertEquals(requestDto.getCategoryIds(), categoryIds);
        assertEquals(requestDto.getTitle(), updatedBook.getTitle());
        assertEquals(requestDto.getAuthor(), updatedBook.getAuthor());
        assertEquals(requestDto.getIsbn(), updatedBook.getIsbn());
        assertEquals(requestDto.getPrice(), updatedBook.getPrice());
        assertEquals(requestDto.getDescription(), updatedBook.getDescription());
        assertEquals(requestDto.getCoverImage(), updatedBook.getCoverImage());
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @Sql(scripts = {
            "classpath:database/books/add-test-books-to-books-table.sql",
            "classpath:database/categories/add-test-categories.sql",
            "classpath:database/books/add-test-books-to-books-categories-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/books/remove-test-books-from-books-table.sql",
            "classpath:database/categories/remove-test-categories.sql",
            "classpath:database/books/remove-test-books-from-books-categories-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Get All books by search parameters")
    void searchForBooks_ValidParameters_ReturnsListBookDtos() throws Exception {
        String[] titles = {"SecondBook"};
        String[] authors = {"Mr.First"};
        String[] isbnNumbers = {"555-333"};
        BookSearchParameters searchParameters = new BookSearchParameters(
                titles, authors, isbnNumbers);

        List<BookDto> expected = new ArrayList<>();
        expected.add(new BookDto().setId(1L)
                .setTitle("FirstBook").setAuthor("Mr.First").setIsbn("555-111")
                .setPrice(BigDecimal.valueOf(49.99)).setDescription("about first book")
                .setCoverImage("first cover").setCategoryIds(List.of(1L)));
        expected.add(new BookDto().setId(2L)
                .setTitle("SecondBook").setAuthor("Mr.Second").setIsbn("555-222")
                .setPrice(BigDecimal.valueOf(39.99)).setDescription("about second book")
                .setCoverImage("second cover").setCategoryIds(List.of(2L)));
        expected.add(new BookDto().setId(3L)
                .setTitle("ThirdBook").setAuthor("Mr.Third").setIsbn("555-333")
                .setPrice(BigDecimal.valueOf(59.99)).setDescription("about third book")
                .setCoverImage("third cover").setCategoryIds(List.of(1L)));

        String jsonRequestBody = objectMapper.writeValueAsString(searchParameters);

        MvcResult result = mockMvc.perform(get("/books/search")
                        .content(jsonRequestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        BookDto[] actual = objectMapper.readValue(result.getResponse().getContentAsByteArray(),
                BookDto[].class);
        assertEquals(3, expected.size());
        assertEquals(expected, Arrays.stream(actual).toList());
    }
}
