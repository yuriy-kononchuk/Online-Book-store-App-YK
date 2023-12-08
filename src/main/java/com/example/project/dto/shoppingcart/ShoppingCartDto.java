package com.example.project.dto.shoppingcart;

import com.example.project.model.User;
import java.util.List;
import lombok.Data;

@Data
public class ShoppingCartDto {
    private Long id;
    private User user;
    private List<Long> cartItemsIds;
}
