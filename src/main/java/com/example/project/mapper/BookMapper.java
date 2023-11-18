package com.example.project.mapper;

import com.example.project.config.MapperConfig;
import com.example.project.dto.book.BookDto;
import com.example.project.dto.book.CreateBookRequestDto;
import com.example.project.model.Book;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto convertToDto(Book book);

    Book convertToModel(CreateBookRequestDto booktDto);
}
