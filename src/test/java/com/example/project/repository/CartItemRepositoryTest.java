package com.example.project.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.project.model.CartItem;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:database/shoppingcart/add-test-user-and-shopping-cart-items-set.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/shoppingcart/delete-test-user-set.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class CartItemRepositoryTest {
    @Autowired
    private CartItemRepository cartItemRepository;

    @Test
    @DisplayName("Find all cart items with specific shopping cart Id")
    void findAllByShoppingCartId_ValidShoppingCartId_ReturnsListAllCartItems() {
        Long shoppingCartId = 2L;
        Pageable pageable = PageRequest.of(0, 10);
        List<CartItem> actualByShoppingCartId = cartItemRepository.findAllByShoppingCartId(
                shoppingCartId, pageable);

        assertEquals(3, actualByShoppingCartId.size());
        assertEquals(1, actualByShoppingCartId.get(0).getBook().getId());
        assertEquals(2, actualByShoppingCartId.get(1).getBook().getId());
        assertEquals(3, actualByShoppingCartId.get(2).getBook().getId());
    }

    @Test
    @DisplayName("Verify to find no cart items with wrong shopping cart Id")
    void findAllByShoppingCartId_NonValidShoppingCartId_ReturnsEmptyList() {
        Long shoppingCartId = 5L;
        Pageable pageable = PageRequest.of(0, 10);
        List<CartItem> actualByShoppingCartId = cartItemRepository.findAllByShoppingCartId(
                shoppingCartId, pageable);

        assertTrue(actualByShoppingCartId.isEmpty());
    }
}
