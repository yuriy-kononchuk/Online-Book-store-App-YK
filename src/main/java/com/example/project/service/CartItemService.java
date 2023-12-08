package com.example.project.service;

import com.example.project.dto.cartitem.CartItemDto;
import com.example.project.dto.cartitem.CreateCartItemRequestDto;
import com.example.project.model.CartItem;

public interface CartItemService {
    CartItemDto save(CreateCartItemRequestDto requestDto);

    CartItemDto getById(Long id);

    CartItem updateQuantityByCartItemId(Long id, CreateCartItemRequestDto requestDto);

    void deleteById(Long id);
}
