package com.example.project.service;

import com.example.project.dto.cartitem.CartItemDto;
import com.example.project.dto.cartitem.CreateCartItemRequestDto;
import com.example.project.dto.shoppingcart.CreateShoppingCartRequestDto;
import com.example.project.dto.shoppingcart.ShoppingCartDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface ShoppingCartService {

    ShoppingCartDto save(CreateShoppingCartRequestDto requestDto);

    List<CartItemDto> findAllByUserId(Long userId, Pageable pageable);

    ShoppingCartDto addBookToShoppingCart(Long userId, CreateCartItemRequestDto cartItemRequestDto);

    ShoppingCartDto updateBookQuantityByCartItemId(
            Long cartItemId,CreateCartItemRequestDto cartItemRequestDto, Long userId);

    ShoppingCartDto deleteBookByCartItemId(Long cartItemId, Long userId);
}
