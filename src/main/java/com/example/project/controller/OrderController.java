package com.example.project.controller;

import com.example.project.dto.order.CreateOrderRequestDto;
import com.example.project.dto.order.OrderDto;
import com.example.project.model.Order;
import com.example.project.model.User;
import com.example.project.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order management", description = "Endpoints for mapping orders")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Place a user's order",
            description = "Create a user's new order")
    @ApiResponse(responseCode = "201", description = "New order was placed successfully")
    public OrderDto saveOrder(@RequestBody CreateOrderRequestDto requestDto,
                              Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return orderService.saveOrder(user, requestDto);
    }

    @GetMapping
    @Operation(summary = "Get a list of orders by user ID",
            description = "Get a user's list of all orders made by user id")
    public List<OrderDto> getOrdersByUserId(Authentication authentication, Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        return orderService.findAllOrdersByUserId(user.getId(), pageable);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{id}")
    @Operation(summary = "Update status of order", description = "Update status of order")
    @ApiResponse(responseCode = "200", description = "Order's status was successfully updated")
    public OrderDto updateStatusOrder(@PathVariable Long id, Order.Status status,
                                      Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return orderService.updateStatus(user, id, status);

    }
}
