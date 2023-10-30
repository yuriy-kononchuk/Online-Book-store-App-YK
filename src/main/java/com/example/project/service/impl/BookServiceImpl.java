package com.example.project.service.impl;

import com.example.project.model.Book;
import com.example.project.repository.BookRepository;
import com.example.project.service.BookService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {
    @Autowired //?? or condtrucror
    private final BookRepository bookRepository;

    @Autowired //?? or field
    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }
}
