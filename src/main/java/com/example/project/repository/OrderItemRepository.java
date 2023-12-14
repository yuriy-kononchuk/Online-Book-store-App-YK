package com.example.project.repository;

import com.example.project.model.OrderItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findAllByOrderId(Long orderId, Pageable pageable);

    Optional<OrderItem> findById(Long id);
}
