package com.example.project.repository.book;

import com.example.project.dto.book.BookSearchParameters;
import com.example.project.model.Book;
import com.example.project.repository.SpecificationBuilder;
import com.example.project.repository.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_ISBN = "isbn";
    private static final String KEY_TITLE = "title";
    private SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParameters searchParameters) {
        Specification<Book> spec = Specification.where(null);
        if (searchParameters.titles() != null && searchParameters.titles().length > 0) {
            spec.and(bookSpecificationProviderManager.getSpecificationProvider(KEY_TITLE)
                    .getSpecification(searchParameters.titles()));
        }
        if (searchParameters.authors() != null && searchParameters.authors().length > 0) {
            spec.and(bookSpecificationProviderManager.getSpecificationProvider(KEY_AUTHOR)
                    .getSpecification(searchParameters.authors()));
        }
        if (searchParameters.isbnNumbers() != null && searchParameters.isbnNumbers().length > 0) {
            spec.and(bookSpecificationProviderManager.getSpecificationProvider(KEY_ISBN)
                    .getSpecification(searchParameters.isbnNumbers()));
        }
        return spec;
    }
}
