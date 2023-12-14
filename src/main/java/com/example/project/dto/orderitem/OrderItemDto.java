package com.example.project.dto.orderitem;

import lombok.Data;

@Data
public class OrderItemDto {
    private Long id;
    private Long orderId;
    private Long bookId;
    private int quantity;
}
