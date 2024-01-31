package com.example.project.mapper;

import com.example.project.dto.cartitem.CartItemDto;
import com.example.project.dto.cartitem.CreateCartItemRequestDto;
import com.example.project.model.CartItem;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CartItemMapperImpl implements CartItemMapper {
    private final ShoppingCartMapper shoppingCartMapper;
    private final BookMapper bookMapper;

    @Override
    public CartItemDto toDto(CartItem cartItem) {
        if (cartItem == null) {
            return null;
        }
        CartItemDto dto = new CartItemDto();
        dto.setId(cartItem.getId());
        dto.setBookId(cartItem.getBook().getId());
        dto.setBookTitle(cartItem.getBook().getTitle());
        dto.setQuantity(cartItem.getQuantity());
        dto.setShoppingCartId(cartItem.getShoppingCart().getId());
        return dto;
    }

    @Override
    public CartItem toEntity(CreateCartItemRequestDto requestDto) {
        if (requestDto == null) {
            return null;
        }
        CartItem cartItem = new CartItem();
        cartItem.setBook(bookMapper.bookFromId(requestDto.bookId()));
        cartItem.setQuantity(requestDto.quantity());
        cartItem.setShoppingCart(shoppingCartMapper.shoppingCartFromId(
                requestDto.shoppingCartId()));
        return cartItem;
    }
}
