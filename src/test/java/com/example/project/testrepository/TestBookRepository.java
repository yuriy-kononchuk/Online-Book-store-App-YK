package com.example.project.testrepository;

import com.example.project.model.Book;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestBookRepository extends JpaRepository<Book, Long> {

    @EntityGraph(attributePaths = {"categories"})
    Optional<Book> findById(Long id);
}
