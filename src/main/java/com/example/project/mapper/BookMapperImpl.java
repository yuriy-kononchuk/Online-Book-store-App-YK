package com.example.project.mapper;

import com.example.project.dto.book.BookDto;
import com.example.project.dto.book.BookDtoWithoutCategoryIds;
import com.example.project.dto.book.CreateBookRequestDto;
import com.example.project.model.Book;
import com.example.project.model.Category;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class BookMapperImpl implements BookMapper {
    @Override
    public BookDto toDto(Book book) {
        if (book == null) {
            return null;
        }
        BookDto dto = new BookDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setIsbn(book.getIsbn());
        dto.setPrice(book.getPrice());
        dto.setDescription(book.getDescription());
        dto.setCoverImage(book.getCoverImage());

        setcategoryIds(dto, book); // added with private method

        return dto;
    }

    private void setcategoryIds(BookDto bookDto, Book book) {
        List<Long> categoryIds = book.getCategories().stream()
                .map(Category::getId)
                .toList();
        bookDto.setCategoryIds(categoryIds);
    }

    @Override
    public Book toEntity(CreateBookRequestDto requestDto) {
        if (requestDto == null) {
            return null;
        }
        Book book = new Book();
        book.setTitle(requestDto.getTitle());
        book.setAuthor(requestDto.getAuthor());
        book.setIsbn(requestDto.getIsbn());
        book.setPrice(requestDto.getPrice());
        book.setDescription(requestDto.getDescription());
        book.setCoverImage(requestDto.getCoverImage());

        setCategories(requestDto.getCategoryIds(), book); // added with private method

        return book;
    }

    private void setCategories(List<Long> categoryIds, Book book) {
        Set<Category> categories = categoryIds.stream()
                .map(Category::new)
                .collect(Collectors.toSet());
        book.setCategories(categories);
    }

    @Override
    public BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book) { // added
        if (book == null) {
            return null;
        }
        BookDtoWithoutCategoryIds dto = new BookDtoWithoutCategoryIds();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setIsbn(book.getIsbn());
        dto.setPrice(book.getPrice());
        dto.setDescription(book.getDescription());
        dto.setCoverImage(book.getCoverImage());
        return dto;
    }
}
