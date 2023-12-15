package com.example.project.mapper;

import com.example.project.config.MapperConfig;
import com.example.project.dto.order.OrderDto;
import com.example.project.model.Order;
import com.example.project.model.OrderItem;
import java.util.List;
import java.util.Optional;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface OrderMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(target = "orderItemsIds", ignore = true)
    OrderDto toDto(Order order);

    @AfterMapping
    default void setOrderItemIds(@MappingTarget OrderDto orderDto, Order order) {
        List<Long> orderItemIds = order.getOrderItems().stream()
                .map(OrderItem::getId)
                .toList();
        orderDto.setOrderItemsIds(orderItemIds);
    }

    @Named("orderFromId")
    default Order orderFromId(Long id) {
        return Optional.ofNullable(id)
                .map(Order::new)
                .orElse(null);
    }
}
