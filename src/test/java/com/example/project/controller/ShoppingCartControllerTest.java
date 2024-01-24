package com.example.project.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.project.dto.cartitem.CreateCartItemRequestDto;
import com.example.project.dto.shoppingcart.ShoppingCartDto;
import com.example.project.model.Book;
import com.example.project.model.CartItem;
import com.example.project.model.Role;
import com.example.project.model.ShoppingCart;
import com.example.project.model.User;
import com.example.project.repository.ShoppingCartRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WithMockUser(username = "admin", roles = {"ADMIN"})
@AutoConfigureMockMvc
class ShoppingCartControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    @DisplayName("Add a new book to shopping cart is successful")
    void addBookToShoppingCart_ValidRequestDto_Success() throws Exception {
        CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto(
                2L, 2, 2L);
        Book book = new Book();
        book.setId(2L);
        book.setTitle("Ukraine wins");
        book.setAuthor("Zaluzhnyj");
        book.setIsbn("555-5-23-230872-5");
        book.setPrice(BigDecimal.valueOf(49.99));
        book.setDescription("victory for Ukraine");
        book.setCoverImage("Best buy");

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(2L);

        CartItem cartItem = new CartItem();
        cartItem.setId(2L);
        cartItem.setBook(book);
        cartItem.setQuantity(requestDto.quantity());
        cartItem.setShoppingCart(shoppingCart);
        shoppingCart.setCartItems(Set.of(cartItem));

        ShoppingCartDto expected = new ShoppingCartDto()
                .setId(shoppingCart.getId()).setUser(shoppingCart.getUser())
                .setCartItemsIds(List.of(cartItem.getId()));

        User user = new User();
        user.setId(1L);
        user.setEmail("firstuser@test.com");
        user.setPassword("userone");
        user.setFirstName("User");
        user.setLastName("First");
        user.setShoppingCart(shoppingCart);
        user.setRoles(Set.of(new Role(Role.RoleName.ADMIN)));
        shoppingCart.setUser(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(user, "userone");

        String jsonRequestBody = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/cart")
                        .content(jsonRequestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(request -> {
                            request.setRemoteUser("firstuser@test.com");
                            request.setUserPrincipal(authentication);
                            return request;
                        }))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ShoppingCartDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ShoppingCartDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getUser(), actual.getUser());
        assertEquals(expected.getCartItemsIds(), actual.getCartItemsIds());
    }

    @Test
    void getAllShoppingCartItems() {
    }

    @Test
    void updateBookQuantityInShoppingCart() {
    }

    @Test
    void deleteBookFromShoppingCart() {
    }
}