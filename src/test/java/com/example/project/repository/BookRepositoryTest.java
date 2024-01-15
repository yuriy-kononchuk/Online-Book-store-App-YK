package com.example.project.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.project.model.Book;
import com.example.project.repository.book.BookRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {
        "classpath:database/categories/remove-test-categories.sql",
        "classpath:database/books/add-test-books-to-books-table.sql",
        "classpath:database/categories/add-test-categories.sql",
        "classpath:database/books/add-test-books-to-books-categories-table.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {
        "classpath:database/books/remove-test-books-from-books-categories-table.sql",
        "classpath:database/categories/remove-test-categories.sql",
        "classpath:database/books/remove-test-books-from-books-table.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("Find all books with specific category Id")
    void findAllByCategoryId_ValidCategoryId_ReturnsAllBooks() {
        Long categoryId = 1L;
        List<Book> actual = bookRepository.findAllByCategoriesId(categoryId);
        assertEquals(2, actual.size());
        assertEquals("FirstBook", actual.get(0).getTitle());
        assertEquals("ThirdBook", actual.get(1).getTitle());
    }

    @Test
    @DisplayName("Verify to find no books with wrong category Id")
    void findAllByCategoryId_NonValidCategoryId_ReturnsEmptyList() {
        Long categoryId = 3L;
        List<Book> actual = bookRepository.findAllByCategoriesId(categoryId);
        assertEquals(0, actual.size());
    }
}
