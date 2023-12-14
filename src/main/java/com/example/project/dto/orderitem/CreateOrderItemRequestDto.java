package com.example.project.dto.orderitem;

public record CreateOrderItemRequestDto(Long bookId, int quantity, Long orderId) {
}
