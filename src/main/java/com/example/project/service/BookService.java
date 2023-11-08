package com.example.project.service;

import com.example.project.dto.BookDto;
import com.example.project.dto.CreateBookRequestDto;
import com.example.project.dto.BookSearchParameters;
import java.util.List;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll();

    BookDto findById(Long id);

    void deleteById(Long id);

    void updateById(Long id);

    List<BookDto> searchForBooks(BookSearchParameters searchParameters);
}
