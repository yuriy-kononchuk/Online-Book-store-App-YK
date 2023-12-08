package com.example.project.mapper;

import com.example.project.dto.cartitem.CartItemDto;
import com.example.project.dto.cartitem.CreateCartItemRequestDto;
import com.example.project.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = {ShoppingCartMapper.class, BookMapper.class})
public interface CartItemMapper {

    @Mapping(source = "shoppingCart.id", target = "shoppingCartId")
    @Mapping(source = "book.id", target = "bookId")
    CartItemDto toDto(CartItem cartItem);

    @Mapping(target = "shoppingCart", source = "shoppingCartId",
            qualifiedByName = "shoppingCartById")
    @Mapping(target = "book", source = "bookId", qualifiedByName = "bookFromId")
    CartItem toEntity(CreateCartItemRequestDto requestDto);
}
