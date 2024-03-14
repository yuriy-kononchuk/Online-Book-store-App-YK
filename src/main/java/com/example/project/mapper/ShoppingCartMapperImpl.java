package com.example.project.mapper;

import com.example.project.dto.shoppingcart.CreateShoppingCartRequestDto;
import com.example.project.dto.shoppingcart.ShoppingCartDto;
import com.example.project.model.ShoppingCart;
import org.springframework.stereotype.Component;

@Component
public class ShoppingCartMapperImpl implements ShoppingCartMapper {
    @Override
    public ShoppingCartDto toDto(ShoppingCart shoppingCart) {
        if (shoppingCart == null) {
            return null;
        }
        ShoppingCartDto dto = new ShoppingCartDto();
        dto.setId(shoppingCart.getId());
        dto.setUserId(shoppingCart.getUser().getId());
        setCartItemsIds(dto, shoppingCart);

        return dto;
    }

    @Override
    public ShoppingCart toEntity(CreateShoppingCartRequestDto requestDto) {
        if (requestDto == null) {
            return null;
        }
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(requestDto.user());
        setCartItems(shoppingCart, requestDto);

        return shoppingCart;
    }
}
