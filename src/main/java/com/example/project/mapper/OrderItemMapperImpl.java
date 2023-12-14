package com.example.project.mapper;

import com.example.project.dto.orderitem.CreateOrderItemRequestDto;
import com.example.project.dto.orderitem.OrderItemDto;
import com.example.project.model.OrderItem;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class OrderItemMapperImpl implements OrderItemMapper {
    private final OrderMapper orderMapper;
    private final BookMapper bookMapper;

    @Override
    public OrderItemDto toDto(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }
        OrderItemDto dto = new OrderItemDto();
        dto.setId(orderItem.getId());
        dto.setOrderId(orderItem.getId());
        dto.setBookId(orderItem.getBook().getId());
        dto.setQuantity(orderItem.getQuantity());
        return dto;
    }

    @Override
    public OrderItem toEntity(CreateOrderItemRequestDto requestDto) {
        if (requestDto == null) {
            return null;
        }
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(orderMapper.orderFromId(requestDto.orderId()));
        orderItem.setBook(bookMapper.bookFromId(requestDto.bookId()));
        orderItem.setQuantity(requestDto.quantity());
        orderItem.setPrice(bookMapper.bookFromId(requestDto.bookId()).getPrice());
        return orderItem;
    }
}
