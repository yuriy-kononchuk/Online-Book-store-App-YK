package com.example.project.service.impl;

import com.example.project.dto.book.BookDto;
import com.example.project.dto.book.BookDtoWithoutCategoryIds;
import com.example.project.dto.book.BookSearchParameters;
import com.example.project.dto.book.CreateBookRequestDto;
import com.example.project.mapper.BookMapper;
import com.example.project.model.Book;
import com.example.project.model.Category;
import com.example.project.repository.book.BookRepository;
import com.example.project.repository.book.BookSpecificationBuilder;
import com.example.project.service.BookService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookSpecificationBuilder bookSpecificationBuilder;
    private final BookMapper bookMapper;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toEntity(requestDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public List<BookDtoWithoutCategoryIds> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable).stream()
                .map(book -> bookMapper.toDtoWithoutCategories(book))
                .toList();
    }

    @Override
    public BookDto findById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find a book by id: " + id));
        return bookMapper.toDto(book);
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public void updateById(Long id, CreateBookRequestDto requestDto) {
        Book bookToUpdate = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find a book by id: " + id));
        Set<Category> categories = requestDto.getCategoryIds().stream()
                .map(Category::new)
                .collect(Collectors.toSet());

        bookToUpdate.setTitle(requestDto.getTitle());
        bookToUpdate.setAuthor(requestDto.getAuthor());
        bookToUpdate.setIsbn(requestDto.getIsbn());
        bookToUpdate.setPrice(requestDto.getPrice());
        bookToUpdate.setDescription(requestDto.getDescription());
        bookToUpdate.setCoverImage(requestDto.getCoverImage());
        bookToUpdate.setCategories(categories);
        bookRepository.save(bookToUpdate);
    }

    @Override
    public List<BookDto> searchForBooks(BookSearchParameters searchParameters, Pageable pageable) {
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(searchParameters);
        return bookRepository.findAll(bookSpecification, pageable).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public List<BookDto> findAllByCategoryId(Long categoryId) {
        return bookRepository.findAllByCategoriesId(categoryId).stream()
                .map(bookMapper::toDto)
                .toList();
    }
}
