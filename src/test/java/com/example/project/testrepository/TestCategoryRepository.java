package com.example.project.testrepository;

import com.example.project.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestCategoryRepository extends JpaRepository<Category, Long> {
}
