package com.example.project.repository;

import com.example.project.model.CartItem;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findAllByUsersId(Long userId, Pageable pageable);
}
