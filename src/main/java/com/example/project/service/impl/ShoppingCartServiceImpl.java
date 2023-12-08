package com.example.project.service.impl;

import com.example.project.dto.cartitem.CartItemDto;
import com.example.project.dto.cartitem.CreateCartItemRequestDto;
import com.example.project.dto.shoppingcart.CreateShoppingCartRequestDto;
import com.example.project.dto.shoppingcart.ShoppingCartDto;
import com.example.project.mapper.CartItemMapper;
import com.example.project.mapper.ShoppingCartMapper;
import com.example.project.model.CartItem;
import com.example.project.model.ShoppingCart;
import com.example.project.repository.CartItemRepository;
import com.example.project.repository.ShoppingCartRepository;
import com.example.project.service.CartItemService;
import com.example.project.service.ShoppingCartService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemService cartItemService;
    private final CartItemMapper cartItemMapper;

    @Override
    public ShoppingCartDto save(CreateShoppingCartRequestDto requestDto) {
        ShoppingCart shoppingCart = shoppingCartMapper.toEntity(requestDto);
        return shoppingCartMapper.toDto(shoppingCartRepository.save(shoppingCart));
    }

    @Override
    public List<CartItemDto> findAllByUserId(Long userId, Pageable pageable) {
        return cartItemRepository.findAllByUsersId(userId, pageable).stream()
                .map(cartItemMapper::toDto)
                .toList();
    }

    @Override
    public ShoppingCartDto addBookToShoppingCart(Long userId,
                                                 CreateCartItemRequestDto cartItemRequestDto) {
        ShoppingCart shoppingCartByUserId = shoppingCartRepository.findShoppingCartByUserId(userId);
        Set<CartItem> cartItems = shoppingCartByUserId.getCartItems();
        cartItems.add(cartItemMapper.toEntity(cartItemRequestDto));
        shoppingCartByUserId.setCartItems(cartItems);
        return shoppingCartMapper.toDto(shoppingCartByUserId);
    }

    @Override
    public ShoppingCartDto deleteBookByCartItemId(Long cartItemId, Long userId) {
        ShoppingCart shoppingCartByUserId = shoppingCartRepository.findShoppingCartByUserId(userId);
        Set<CartItem> cartItems = shoppingCartByUserId.getCartItems();
        CartItem cartItemToDelete = shoppingCartByUserId.getCartItems().stream()
                .filter(id -> id.equals(cartItemId))
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException("Can't find a cart item"));
        cartItems.remove(cartItemToDelete);
        shoppingCartByUserId.setCartItems(cartItems);
        return shoppingCartMapper.toDto(shoppingCartByUserId);
    }

    @Override
    public ShoppingCartDto updateBookQuantityByCartItemId(
            Long cartItemId, CreateCartItemRequestDto cartItemRequestDto, Long userId) {
        CartItem cartItemUpdated = cartItemService.updateQuantityByCartItemId(cartItemId,
                cartItemRequestDto);
        ShoppingCart shoppingCartByUserId = shoppingCartRepository.findShoppingCartByUserId(userId);
        Set<CartItem> cartItems = shoppingCartByUserId.getCartItems();
        CartItem cartItemToUpdate = shoppingCartByUserId.getCartItems().stream()
                .filter(id -> id.equals(cartItemId))
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException("Can't find a cart item"));
        cartItems.remove(cartItemToUpdate);
        cartItems.add(cartItemUpdated);
        shoppingCartByUserId.setCartItems(cartItems);
        return shoppingCartMapper.toDto(shoppingCartByUserId);
    }
}
