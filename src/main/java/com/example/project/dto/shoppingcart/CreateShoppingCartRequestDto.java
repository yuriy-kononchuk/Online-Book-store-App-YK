package com.example.project.dto.shoppingcart;

import com.example.project.model.User;
import java.util.List;

public record CreateShoppingCartRequestDto(User user, List<Long> cartItemsIds) {
}
