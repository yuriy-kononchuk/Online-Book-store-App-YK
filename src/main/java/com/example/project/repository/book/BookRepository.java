package com.example.project.repository.book;

import com.example.project.model.Book;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    @EntityGraph(attributePaths = {"categories"})
    List<Book> findAllByCategoriesId(Long categoryId);

    @EntityGraph(attributePaths = {"categories"})
    Optional<Book> findById(Long id);

    @EntityGraph(attributePaths = {"categories"})
    Page<Book> findAll(Specification<Book> bookSpecification, Pageable pageable);
}
