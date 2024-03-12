package com.example.project.service.impl;

import com.example.project.dto.orderitem.OrderItemDto;
import com.example.project.mapper.OrderItemMapper;
import com.example.project.model.Order;
import com.example.project.model.OrderItem;
import com.example.project.model.User;
import com.example.project.repository.OrderItemRepository;
import com.example.project.repository.OrderRepository;
import com.example.project.service.OrderItemService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;
    private final OrderRepository orderRepository;

    @Override
    public List<OrderItemDto> findAllItemsByOrderId(User user, Long orderId, Pageable pageable) {
        Optional<Order> orderByIdPresent = getOrderByIdFromUserOrders(user, orderId, pageable);
        if (orderByIdPresent.isPresent()) {
            return orderItemRepository.findAllByOrderId(orderId, pageable).stream()
                    .map(orderItemMapper::toDto)
                    .toList();
        }
        throw new EntityNotFoundException("Can't find an order by id: " + orderId);
    }

    @Override
    public OrderItemDto findItemByIdByOrderId(User user, Long orderId, Long id) {
        Pageable pageable = PageRequest.of(0, 10);
        Optional<Order> orderByIdPresent = getOrderByIdFromUserOrders(user, orderId, pageable);
        if (orderByIdPresent.isPresent()) {
            OrderItem orderItemById = orderItemRepository.findById(id).orElseThrow(
                    () -> new EntityNotFoundException("Can't find an order item by id: " + id));
            if (orderItemById.getOrder().getId().equals(orderId)) {
                return orderItemMapper.toDto(orderItemById);
            }
        }
        throw new EntityNotFoundException("Can't find an order by id: " + orderId);
    }

    private Optional<Order> getOrderByIdFromUserOrders(User user, Long orderId, Pageable pageable) {
        List<Order> ordersByUserId = orderRepository.findAllByUserId(user.getId(), pageable);
        return ordersByUserId.stream()
                .filter(order -> order.getId().equals(orderId))
                .findAny();
    }
}
