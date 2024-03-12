package com.example.project.dto.order;

import com.example.project.model.Order;

public record UpdateOrderStatusRequestDto(Order.Status status) {
}
