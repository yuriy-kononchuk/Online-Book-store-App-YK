package com.example.project.repository.book;

import com.example.project.model.Book;
import com.example.project.repository.SpecificationProvider;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class isbnSpecificationProvider implements SpecificationProvider<Book> {
    @Override
    public Specification<Book> getSpecification(String[] params) {
        return ((root, query, criteriaBuilder) -> root.get("isbn").in(Arrays.stream(params)
                .toArray()));
    }

    @Override
    public String getKey() {
        return "isbn";
    }
}
