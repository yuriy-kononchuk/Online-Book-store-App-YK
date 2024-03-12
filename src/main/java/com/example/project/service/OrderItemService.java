package com.example.project.service;

import com.example.project.dto.orderitem.OrderItemDto;
import com.example.project.model.User;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface OrderItemService {

    List<OrderItemDto> findAllItemsByOrderId(User user, Long orderId, Pageable pageable);

    OrderItemDto findItemByIdByOrderId(User user, Long orderId, Long id);
}
