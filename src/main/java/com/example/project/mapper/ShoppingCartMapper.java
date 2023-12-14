package com.example.project.mapper;

import com.example.project.config.MapperConfig;
import com.example.project.dto.shoppingcart.CreateShoppingCartRequestDto;
import com.example.project.dto.shoppingcart.ShoppingCartDto;
import com.example.project.model.CartItem;
import com.example.project.model.ShoppingCart;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface ShoppingCartMapper {

    @Mapping(target = "cartItemsIds", ignore = true)
    ShoppingCartDto toDto(ShoppingCart shoppingCart);

    @AfterMapping
    default void setCartItemsIds(@MappingTarget ShoppingCartDto shoppingCartDto,
                                 ShoppingCart shoppingCart) {
        List<Long> cartItemsIds = shoppingCart.getCartItems().stream()
                .map(CartItem::getId)
                .toList();
        shoppingCartDto.setCartItemsIds(cartItemsIds);
    }

    @Mapping(target = "cartItems", ignore = true)
    ShoppingCart toEntity(CreateShoppingCartRequestDto requestDto);

    @AfterMapping
    default void setCartItems(@MappingTarget ShoppingCart shoppingCart,
                              CreateShoppingCartRequestDto requestDto) {
        Set<CartItem> cartItems = requestDto.cartItemsIds().stream()
                .map(CartItem::new)
                .collect(Collectors.toSet());
        shoppingCart.setCartItems(cartItems);
    }

    @Named("shoppingCartFromId")
    default ShoppingCart shoppingCartFromId(Long id) { // added
        return Optional.ofNullable(id)
                .map(ShoppingCart::new)
                .orElse(null);
    }
}
