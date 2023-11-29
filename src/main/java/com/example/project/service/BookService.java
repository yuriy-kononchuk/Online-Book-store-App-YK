package com.example.project.service;

import com.example.project.dto.book.BookDto;
import com.example.project.dto.book.BookDtoWithoutCategoryIds;
import com.example.project.dto.book.BookSearchParameters;
import com.example.project.dto.book.CreateBookRequestDto;
import com.example.project.model.Book;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDtoWithoutCategoryIds> findAll(Pageable pageable);

    BookDto findById(Long id);

    void deleteById(Long id);

    void updateById(Long id);

    List<BookDto> searchForBooks(BookSearchParameters searchParameters, Pageable pageable);

    List<Book> findAllByCategoryId(Long categoryId); // added
}
