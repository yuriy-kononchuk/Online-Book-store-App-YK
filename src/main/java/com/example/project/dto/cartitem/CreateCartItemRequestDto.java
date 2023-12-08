package com.example.project.dto.cartitem;

public record CreateCartItemRequestDto(Long bookId, int quantity, Long shoppingCartId) {
}
