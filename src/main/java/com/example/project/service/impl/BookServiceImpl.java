package com.example.project.service.impl;

import com.example.project.dto.BookDto;
import com.example.project.dto.BookSearchParameters;
import com.example.project.dto.CreateBookRequestDto;
import com.example.project.mapper.BookMapper;
import com.example.project.model.Book;
import com.example.project.repository.book.BookRepository;
import com.example.project.repository.book.BookSpecificationBuilder;
import com.example.project.service.BookService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private BookSpecificationBuilder bookSpecificationBuilder;
    private BookMapper bookMapper;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.convertToModel(requestDto);
        return bookMapper.convertToDto(bookRepository.save(book));
    }

    @Override
    public List<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable).stream()
                .map(book -> bookMapper.convertToDto(book))
                .toList();
    }

    @Override
    public BookDto findById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find a book by id: " + id));
        return bookMapper.convertToDto(book);
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public void updateById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find a book by id: " + id));
        bookRepository.save(book);
    }

    @Override
    public List<BookDto> searchForBooks(BookSearchParameters searchParameters, Pageable pageable) {
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(searchParameters);
        return bookRepository.findAll(bookSpecification, pageable).stream()
                .map(bookMapper::convertToDto)
                .toList();
    }
}
