package com.example.project.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.project.dto.cartitem.CartItemDto;
import com.example.project.dto.cartitem.CreateCartItemRequestDto;
import com.example.project.dto.shoppingcart.ShoppingCartDto;
import com.example.project.model.Book;
import com.example.project.model.CartItem;
import com.example.project.model.ShoppingCart;
import com.example.project.repository.CartItemRepository;
import com.example.project.repository.ShoppingCartRepository;
import com.example.project.security.JwtUtil;
import com.example.project.testrepository.TestShoppingCartRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ShoppingCartControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private TestShoppingCartRepository testShoppingCartRepository;

    @WithUserDetails("testuser@test.com")
    @Test
    @Sql(scripts = "classpath:database/shoppingcart/add-test-user-and-shopping-cart-items-set.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/shoppingcart/delete-test-user-set.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Get all items from shopping cart")
    void getAllShoppingCartItems_ReturnsListCartItemDtos() throws Exception {
        List<CartItemDto> expected = new ArrayList<>();
        expected.add(new CartItemDto().setId(1L).setShoppingCartId(2L).setBookId(1L)
                .setBookTitle("FirstBook").setQuantity(1));
        expected.add(new CartItemDto().setId(2L).setShoppingCartId(2L).setBookId(2L)
                .setBookTitle("SecondBook").setQuantity(2));
        expected.add(new CartItemDto().setId(3L).setShoppingCartId(2L).setBookId(3L)
                .setBookTitle("ThirdBook").setQuantity(3));

        MvcResult result = mockMvc.perform(get("/cart")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        CartItemDto[] actualDtos = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), CartItemDto[].class);

        assertEquals(expected.size(), actualDtos.length);
        assertEquals(expected, Arrays.stream(actualDtos).toList());
    }

    @WithUserDetails("testuser@test.com")
    @Test
    @Sql(scripts = {
            "classpath:database/shoppingcart/delete-test-user-set.sql",
            "classpath:database/shoppingcart/add-test-user-set.sql"
            }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/shoppingcart/delete-test-user-set.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Add quantity to existing book in shopping cart is successful")
    void addBookToShoppingCart_ValidRequestDtoExistingItem_Success() throws Exception {
        CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto(
                2L, 2, 2L);
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(requestDto.shoppingCartId());

        Book book = new Book();
        book.setId(2L);
        book.setTitle("Ukraine wins");
        book.setAuthor("Zaluzhnyj");
        book.setIsbn("555-5-23-230872-5");
        book.setPrice(BigDecimal.valueOf(49.99));
        book.setDescription("victory for Ukraine");
        book.setCoverImage("Best buy");

        CartItem existingCartItem = new CartItem();
        existingCartItem.setId(2L);
        existingCartItem.setBook(book);
        existingCartItem.setQuantity(1);
        existingCartItem.setShoppingCart(shoppingCart);
        shoppingCart.setCartItems(Set.of(existingCartItem));

        String jsonRequestBody = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/cart")
                        .content(jsonRequestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ShoppingCartDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ShoppingCartDto.class);

        Long cartItemId = actual.getCartItemsIds().stream()
                .filter(id -> id.equals(requestDto.bookId()))
                .findAny().get();
        int actualQuantity = cartItemRepository.findById(cartItemId).get().getQuantity();
        int expectedQuantity = existingCartItem.getQuantity() + requestDto.quantity();
        ShoppingCartDto expected = new ShoppingCartDto().setUserId(3L)
                .setCartItemsIds(List.of(existingCartItem.getId()));

        assertEquals(expectedQuantity, actualQuantity);
        assertEquals(expected.getUserId(), actual.getUserId());
        assertEquals(expected.getCartItemsIds(), actual.getCartItemsIds());
        assertNotNull(actual);
        assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithUserDetails("testuser@test.com")
    @Test
    @Sql(scripts = "classpath:database/shoppingcart/add-test-user-set.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/shoppingcart/delete-test-user-set.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Add a new book to shopping cart is successful")
    void addBookToShoppingCart_ValidRequestDtoNewItem_Success() throws Exception {
        CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto(
                5L, 2, 2L);

        ShoppingCart shoppingCart = shoppingCartRepository
                .findById(requestDto.shoppingCartId()).orElse(null);

        ShoppingCartDto expected = new ShoppingCartDto().setUserId(shoppingCart.getUser().getId())
                .setCartItemsIds(List.of(3L, 2L));

        String jsonRequestBody = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/cart")
                        .content(jsonRequestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ShoppingCartDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ShoppingCartDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expected.getUserId(), actual.getUserId());
        assertEquals(expected.getCartItemsIds().size(), actual.getCartItemsIds().size());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithUserDetails("testuser@test.com")
    @Test
    @Sql(scripts = "classpath:database/shoppingcart/add-test-user-and-shopping-cart-items-set.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/shoppingcart/delete-test-user-set.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Delete a book from shopping cart is successful")
    void deleteBookFromShoppingCart_ValidCartItemId_Success() throws Exception {
        Long cartItemId = 2L;

        MvcResult result = mockMvc.perform(delete("/cart/cart-items/" + cartItemId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ShoppingCartDto actualDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartDto.class);

        CartItem deletedCartItem = cartItemRepository.findById(cartItemId).orElse(null);
        List<CartItem> actualItems = cartItemRepository.findAll();

        assertNull(deletedCartItem);
        assertEquals(2, actualDto.getCartItemsIds().size());
        assertEquals(2, actualItems.size());

    }

    @WithUserDetails("testuser@test.com")
    @Test
    @Sql(scripts = "classpath:database/shoppingcart/add-test-user-and-shopping-cart-items-set.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/shoppingcart/delete-test-user-set.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Delete a book from shopping with non valid cart item id throws exception")
    void deleteBookFromShoppingCart_NonValidCartItemId_ThrowsException() throws Exception {
        Long cartItemId = 5L;

        Exception exception = assertThrows(ServletException.class,
                () -> {
                    mockMvc.perform(delete("/cart/cart-items/" + cartItemId)
                                    .contentType(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andReturn();
                });
        assertTrue(exception.getCause() instanceof EntityNotFoundException);
    }

    @WithUserDetails("testuser@test.com")
    @Test
    @Sql(scripts = "classpath:database/shoppingcart/add-test-user-and-shopping-cart-items-set.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/shoppingcart/delete-test-user-set.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Update a book quantity in shopping cart by cart item id is successful")
    void updateBookQuantityInShoppingCart_ValidCartItemId_Success() throws Exception {
        Long cartItemId = 2L;
        Long userId = 3L;
        CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto(
                2L, 5, 2L);

        String jsonRequestBody = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put("/cart/cart-items/" + cartItemId)
                        .content(jsonRequestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ShoppingCartDto actualDto = objectMapper
                .readValue(result.getResponse().getContentAsString(),ShoppingCartDto.class);

        Long actualCartItemId = actualDto.getCartItemsIds().stream()
                .filter(id -> id.equals(requestDto.bookId()))
                .findAny().get();
        int actualQuantity = cartItemRepository.findById(actualCartItemId).get().getQuantity();
        ShoppingCart expectedDto = testShoppingCartRepository.findShoppingCartByUserId(userId);

        assertEquals(requestDto.quantity(), actualQuantity);
        assertEquals(expectedDto.getCartItems().size(), actualDto.getCartItemsIds().size());
        assertNotNull(actualDto);
        assertNotNull(actualDto.getId());
        EqualsBuilder.reflectionEquals(expectedDto, actualDto, "id");
    }

    @WithUserDetails("testuser@test.com")
    @Test
    @Sql(scripts = "classpath:database/shoppingcart/add-test-user-and-shopping-cart-items-set.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/shoppingcart/delete-test-user-set.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Update a book quantity in shopping cart by cart item id is successful")
    void updateBookQuantityInShoppingCart_NonValidCartItemId_ThrowsException() throws Exception {
        Long cartItemId = 5L;
        CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto(
                2L, 5, 2L);

        String jsonRequestBody = objectMapper.writeValueAsString(requestDto);

        Exception exception = assertThrows(ServletException.class,
                () -> {
                    mockMvc.perform(put("/cart/cart-items/" + cartItemId)
                                    .content(jsonRequestBody)
                                    .contentType(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andReturn();
                });
        assertTrue(exception.getCause() instanceof EntityNotFoundException);
    }
}
