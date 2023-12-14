package com.example.project.service.impl;

import com.example.project.dto.cartitem.CartItemDto;
import com.example.project.dto.cartitem.CreateCartItemRequestDto;
import com.example.project.dto.shoppingcart.CreateShoppingCartRequestDto;
import com.example.project.dto.shoppingcart.ShoppingCartDto;
import com.example.project.mapper.CartItemMapper;
import com.example.project.mapper.ShoppingCartMapper;
import com.example.project.model.CartItem;
import com.example.project.model.ShoppingCart;
import com.example.project.model.User;
import com.example.project.repository.CartItemRepository;
import com.example.project.repository.ShoppingCartRepository;
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
    private final CartItemMapper cartItemMapper;

    @Override
    public ShoppingCartDto save(CreateShoppingCartRequestDto requestDto) {
        ShoppingCart shoppingCart = shoppingCartMapper.toEntity(requestDto);
        return shoppingCartMapper.toDto(shoppingCartRepository.save(shoppingCart));
    }

    @Override
    public List<CartItemDto> findAllByShoppingCartId(User user, Pageable pageable) {
        Long shoppingCartId = user.getShoppingCart().getId();
        return cartItemRepository.findAllByShoppingCartId(shoppingCartId, pageable).stream()
                .map(cartItemMapper::toDto)
                .toList();
    }

    @Override
    public ShoppingCartDto addBookToShoppingCart(Long userId,
                                                 CreateCartItemRequestDto cartItemRequestDto) {
        ShoppingCart shoppingCartByUserId = shoppingCartRepository.findShoppingCartByUserId(userId);
        Set<CartItem> cartItems = shoppingCartByUserId.getCartItems();
        CartItem cartItem = cartItemMapper.toEntity(cartItemRequestDto);
        if (!cartItems.contains(cartItem)) {
            cartItems.add(cartItem);
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + cartItemRequestDto.quantity());
        }
        cartItemRepository.save(cartItem);
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
        cartItemRepository.delete(cartItemToDelete);
        return shoppingCartMapper.toDto(shoppingCartByUserId);
    }

    @Override
    public ShoppingCartDto updateBookQuantityByCartItemId(
            Long cartItemId, CreateCartItemRequestDto cartItemRequestDto, Long userId) {
        ShoppingCart shoppingCartByUserId = shoppingCartRepository.findShoppingCartByUserId(userId);
        CartItem cartItemToUpdate = shoppingCartByUserId.getCartItems().stream()
                .filter(id -> id.equals(cartItemId))
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException("Can't find a cart item"));
        cartItemToUpdate.setQuantity(cartItemRequestDto.quantity());
        cartItemRepository.save(cartItemToUpdate);
        return shoppingCartMapper.toDto(shoppingCartByUserId);
    }
}
