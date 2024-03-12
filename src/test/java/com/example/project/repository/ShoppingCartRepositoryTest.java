package com.example.project.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.example.project.model.ShoppingCart;
import com.example.project.model.User;
import com.example.project.testrepository.TestShoppingCartRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:database/shoppingcart/add-shopping-cart-to-shopping_cart-table.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/shoppingcart/remove-shopping-cart-from-shopping_cart-table.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class ShoppingCartRepositoryTest {
    @Autowired
    private TestShoppingCartRepository shoppingCartRepository;

    @Test
    @DisplayName("Get a shopping cart by user Id")
    void findShoppingCartByUserId_ValidUserId_ReturnsShoppingCart() {
        Long userId = 5L;
        User user = new User();
        user.setId(userId);
        user.setEmail("userone@test.com");
        user.setPassword("userone");
        user.setFirstName("User");
        user.setLastName("One");

        ShoppingCart expected = new ShoppingCart();
        expected.setId(3L);
        expected.setUser(user);
        user.setShoppingCart(expected);

        ShoppingCart actual = shoppingCartRepository.findShoppingCartByUserId(userId);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getUser().getId(), actual.getUser().getId());
    }

    @Test
    @DisplayName("Verify to find no shopping cart with wrong user Id")
    void findShoppingCartByUserId_NonValidUserId_ReturnsNull() {
        Long userId = 3L;

        ShoppingCart actual = shoppingCartRepository.findShoppingCartByUserId(userId);

        assertNull(actual);
    }
}
