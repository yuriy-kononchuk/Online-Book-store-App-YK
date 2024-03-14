package com.example.project.repository.book.specififcation;

import com.example.project.model.Book;
import com.example.project.repository.SpecificationProvider;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TitleSpecificationProvider implements SpecificationProvider<Book> {
    private static final String KEY_TITLE = "title";

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return ((root, query, criteriaBuilder) -> root.get(KEY_TITLE).in(Arrays.stream(params)
                .toArray()));
    }

    @Override
    public String getKey() {
        return KEY_TITLE;
    }
}
