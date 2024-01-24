package com.example.project.service.impl;

import com.example.project.dto.cartitem.CartItemDto;
import com.example.project.dto.cartitem.CreateCartItemRequestDto;
import com.example.project.mapper.CartItemMapper;
import com.example.project.model.CartItem;
import com.example.project.repository.CartItemRepository;
import com.example.project.service.CartItemService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;

    @Override
    public CartItemDto save(CreateCartItemRequestDto requestDto) {
        CartItem cartItem = cartItemMapper.toEntity(requestDto);
        return cartItemMapper.toDto(cartItemRepository.save(cartItem));
    }

    @Override
    public CartItemDto getById(Long id) {
        CartItem cartItem = cartItemRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find a cart item by id: " + id));
        return cartItemMapper.toDto(cartItem);
    }

    @Override
    public CartItem updateQuantityByCartItemId(Long id, CreateCartItemRequestDto requestDto) {
        CartItem cartItem = cartItemRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find a cart item by id: " + id));
        cartItem.setQuantity(requestDto.quantity());
        return cartItemRepository.save(cartItem);
    }

    @Override
    public void deleteById(Long id) {
        cartItemRepository.deleteById(id);
    }
}
