package com.example.project.service;

import com.example.project.dto.order.CreateOrderRequestDto;
import com.example.project.dto.order.OrderDto;
import com.example.project.dto.order.UpdateOrderStatusRequestDto;
import com.example.project.model.User;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDto saveOrder(User user, CreateOrderRequestDto requestDto);

    List<OrderDto> findAllOrdersByUserId(Long userId, Pageable pageable);

    OrderDto updateStatus(User user, Long id, UpdateOrderStatusRequestDto status);
}
