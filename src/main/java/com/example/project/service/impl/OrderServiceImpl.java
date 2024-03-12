package com.example.project.service.impl;

import com.example.project.dto.order.CreateOrderRequestDto;
import com.example.project.dto.order.OrderDto;
import com.example.project.dto.order.UpdateOrderStatusRequestDto;
import com.example.project.exception.DataNotFoundException;
import com.example.project.mapper.OrderMapper;
import com.example.project.model.CartItem;
import com.example.project.model.Order;
import com.example.project.model.OrderItem;
import com.example.project.model.ShoppingCart;
import com.example.project.model.User;
import com.example.project.repository.CartItemRepository;
import com.example.project.repository.OrderRepository;
import com.example.project.repository.ShoppingCartRepository;
import com.example.project.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public OrderDto saveOrder(User user, CreateOrderRequestDto requestDto) {
        ShoppingCart shoppingCartByUserId = shoppingCartRepository
                .findShoppingCartByUserId(user.getId());
        if (shoppingCartByUserId.getCartItems().isEmpty()) {
            throw new DataNotFoundException("Shopping cart is empty. Please, add new item");
        }
        Order order = new Order();
        order.setUser(user);
        order.setStatus(Order.Status.CREATED);
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(requestDto.shippingAddress());
        setOrderItems(order, shoppingCartByUserId);
        setTotalForItems(order, shoppingCartByUserId);

        return orderMapper.toDto(orderRepository.save(order));
    }

    private void setOrderItems(Order order, ShoppingCart shoppingCartByUserId) {
        Set<OrderItem> orderItems = shoppingCartByUserId.getCartItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setBook(cartItem.getBook());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPrice(cartItem.getBook().getPrice());
                    orderItem.setOrder(order);

                    cartItemRepository.deleteById(cartItem.getId());
                    return orderItem;
                })
                .collect(Collectors.toSet());
        order.setOrderItems(orderItems);
    }

    private void setTotalForItems(Order order, ShoppingCart shoppingCartByUserId) {
        Set<CartItem> cartItems = shoppingCartByUserId.getCartItems();
        BigDecimal total = cartItems.stream()
                .map(item -> item.getBook().getPrice().multiply(
                        BigDecimal.valueOf(item.getQuantity())))
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
    public OrderDto updateStatus(User user, Long id, UpdateOrderStatusRequestDto status) {
        Pageable pageable = PageRequest.of(0, 10);
        Optional<Order> orderRepositoryById = orderRepository.findById(id);
        List<Order> ordersByUserId = orderRepository.findAllByUserId(user.getId(), pageable);
        Optional<Order> orderByIdPresent = ordersByUserId.stream()
                .filter(order -> order.getId().equals(id))
                .findAny();
        if (orderByIdPresent.isPresent() && orderRepositoryById.isPresent()) {
            Order order = orderRepositoryById.get();
            order.setStatus(status.status());
            return orderMapper.toDto(orderRepository.save(order));
        }
        throw new EntityNotFoundException("Can't find an order by id: " + id);
    }
}
