package com.example.project.mapper;

import com.example.project.config.MapperConfig;
import com.example.project.dto.orderitem.CreateOrderItemRequestDto;
import com.example.project.dto.orderitem.OrderItemDto;
import com.example.project.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = {OrderMapper.class, BookMapper.class})
public interface OrderItemMapper {

    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "book.id", target = "bookId")
    OrderItemDto toDto(OrderItem orderItem);

    @Mapping(target = "order", source = "orderId", qualifiedByName = "orderFromId")
    @Mapping(target = "book", source = "bookId", qualifiedByName = "bookFromId")
    OrderItem toEntity(CreateOrderItemRequestDto requestDto);
}
