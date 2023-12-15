package com.example.project.service.impl;

import com.example.project.dto.order.CreateOrderRequestDto;
import com.example.project.dto.order.OrderDto;
import com.example.project.mapper.OrderMapper;
import com.example.project.model.CartItem;
import com.example.project.model.Order;
import com.example.project.model.OrderItem;
import com.example.project.model.User;
import com.example.project.repository.OrderRepository;
import com.example.project.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderDto saveOrder(User user, CreateOrderRequestDto requestDto) {
        Order order = new Order();
        order.setUser(user);
        order.setStatus(Order.Status.CREATED);
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(requestDto.shippingAddress());
        setOrderItems(order, user);
        setTotalForItems(order, user);
        return orderMapper.toDto(orderRepository.save(order));
    }

    private void setOrderItems(Order order, User user) {
        Set<OrderItem> orderItems = user.getShoppingCart().getCartItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setBook(cartItem.getBook());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPrice(cartItem.getBook().getPrice());
                    orderItem.setOrder(order);
                    return orderItem;
                })
                .collect(Collectors.toSet());
        order.setOrderItems(orderItems);
    }

    private void setTotalForItems(Order order, User user) {
        Set<CartItem> cartItems = user.getShoppingCart().getCartItems();
        BigDecimal total = cartItems.stream()
                .map(item -> item.getBook().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotal(total);
    }

    @Override
    public List<OrderDto> findAllOrdersByUserId(Long userId, Pageable pageable) {
        return orderRepository.findAllByUserId(userId, pageable).stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public OrderDto updateStatus(User user, Long id, Order.Status status) {
        Optional<Order> orderRepositoryById = orderRepository.findById(id);
        if (user.getOrders().contains(orderMapper.orderFromId(id))
                && orderRepositoryById.isPresent()) {
            Order order = orderRepositoryById.get();
            order.setStatus(status);
            return orderMapper.toDto(orderRepository.save(order));
        }
        throw new EntityNotFoundException("Can't find an order by id: " + id);
    }
}
