package com.example.project.service.impl;

import com.example.project.dto.orderitem.CreateOrderItemRequestDto;
import com.example.project.dto.orderitem.OrderItemDto;
import com.example.project.mapper.OrderItemMapper;
import com.example.project.mapper.OrderMapper;
import com.example.project.model.OrderItem;
import com.example.project.model.User;
import com.example.project.repository.OrderItemRepository;
import com.example.project.service.OrderItemService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;
    private final OrderMapper orderMapper;

    @Override
    public OrderItemDto save(CreateOrderItemRequestDto requestDto) {
        OrderItem orderItem = orderItemMapper.toEntity(requestDto);
        return orderItemMapper.toDto(orderItemRepository.save(orderItem));
    }

    @Override
    public List<OrderItemDto> findAllItemsByOrderId(User user, Long orderId, Pageable pageable) {
        if (user.getOrders().contains(orderMapper.orderFromId(orderId))) {
            return orderItemRepository.findAllByOrderId(orderId, pageable).stream()
                    .map(orderItemMapper::toDto)
                    .toList();
        }
        throw new EntityNotFoundException("Can't find an order by id: " + orderId);
    }

    @Override
    public OrderItemDto findItemByIdByOrderId(User user, Long orderId, Long id) {
        if (user.getOrders().contains(orderMapper.orderFromId(orderId))) {
            OrderItem orderItemById = orderItemRepository.findById(id).orElseThrow(
                    () -> new EntityNotFoundException("Can't find an order item by id: " + id));
            if (orderItemById.getOrder().getId().equals(orderId)) {
                return orderItemMapper.toDto(orderItemById);
            }
        }
        throw new EntityNotFoundException("Can't find an order by id: " + orderId);
    }
}
