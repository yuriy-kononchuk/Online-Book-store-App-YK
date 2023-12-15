package com.example.project.mapper;

import com.example.project.dto.order.OrderDto;
import com.example.project.model.Order;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class OrderMapperImpl implements OrderMapper {
    private final UserMapper userMapper;

    @Override
    public OrderDto toDto(Order order) {
        if (order == null) {
            return null;
        }
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setUserId(order.getUser().getId());
        dto.setStatus(order.getStatus());
        dto.setTotal(order.getTotal());
        dto.setOrderDate(order.getOrderDate());
        dto.setShippingAddress(order.getShippingAddress());

        setOrderItemIds(dto, order);

        return dto;
    }
}
