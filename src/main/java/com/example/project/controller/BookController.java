package com.example.project.controller;

import com.example.project.dto.book.BookDto;
import com.example.project.dto.book.BookDtoWithoutCategoryIds;
import com.example.project.dto.book.BookSearchParameters;
import com.example.project.dto.book.CreateBookRequestDto;
import com.example.project.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Book management", description = "Endpoints for mapping books")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/books")
public class BookController {
    private final BookService bookService;

    @GetMapping
    @Operation(summary = "Get All Books", description = "Get a list of all available books")
    public List<BookDtoWithoutCategoryIds> getAll(Pageable pageable) {
        return bookService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a book by ID", description = "Get a book by id")
    public BookDto getBookById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    @Operation(summary = "Create a new book", description = "Create a new book")
    @ApiResponse(responseCode = "201", description = "New book successfully created")
    public BookDto createBook(@RequestBody @Valid CreateBookRequestDto bookDto) {
        return bookService.save(bookDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Book by ID", description = "Delete a book by id")
    @ApiResponse(responseCode = "204", description = "No book found to delete")
    public void deleteBookById(@PathVariable Long id) {
        bookService.deleteById(id);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update Book by ID", description = "Update a book by id")
    @ApiResponse(responseCode = "200", description = "Requested book was updated")
    public void updateBookById(@PathVariable Long id,
                               @RequestBody CreateBookRequestDto requestDto) {
        bookService.updateById(id, requestDto);
    }

    @GetMapping("/search")
    @Operation(summary = "Get All Books by Parameters", description = "Get a list of all available "
            + "books by user's parameters")
    public List<BookDto> searchForBooks(BookSearchParameters searchParameters, Pageable pageable) {
        return bookService.searchForBooks(searchParameters, pageable);
    }
}
