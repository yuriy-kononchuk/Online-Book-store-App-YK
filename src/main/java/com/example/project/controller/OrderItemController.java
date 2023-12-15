package com.example.project.controller;

import com.example.project.dto.orderitem.OrderItemDto;
import com.example.project.model.User;
import com.example.project.service.OrderItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order items management", description = "Endpoints for mapping order items")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/orders")
public class OrderItemController {
    private final OrderItemService orderItemService;

    @GetMapping("/{id}/items")
    @Operation(summary = "Get a list of items by order ID",
            description = "Get a user's list of all items by order id")
    public List<OrderItemDto> getItemsByOrderId(@PathVariable Long orderId,
                                                Authentication authentication, Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        return orderItemService.findAllItemsByOrderId(user, orderId, pageable);
    }

    @GetMapping("/{orderId}/items/{id}")
    @Operation(summary = "Get an item by ID and order ID",
            description = "Get a user's item by id and by order id")
    public OrderItemDto getItemByIdByOrderId(@PathVariable Long orderId,
                                             @PathVariable Long id,
                                             Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return orderItemService.findItemByIdByOrderId(user, orderId, id);
    }
}
