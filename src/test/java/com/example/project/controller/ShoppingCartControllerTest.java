package com.example.project.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.project.dto.cartitem.CartItemDto;
import com.example.project.dto.cartitem.CreateCartItemRequestDto;
import com.example.project.dto.shoppingcart.ShoppingCartDto;
import com.example.project.model.Book;
import com.example.project.model.CartItem;
import com.example.project.model.Role;
import com.example.project.model.ShoppingCart;
import com.example.project.model.User;
import com.example.project.repository.CartItemRepository;
import com.example.project.repository.ShoppingCartRepository;
import com.example.project.security.JwtUtil;
import com.example.project.testrepository.TestShoppingCartRepository;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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

    @WithMockUser(username = "admin", roles = {"ADMIN"})
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

        String testUser = jwtUtil.generateToken("testuser@test.com");

        MvcResult result = mockMvc.perform(get("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + testUser))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        CartItemDto[] actualDtos = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), CartItemDto[].class);

        assertEquals(expected.size(), actualDtos.length);
        assertEquals(expected, Arrays.stream(actualDtos).toList());
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @Sql(scripts = "classpath:database/shoppingcart/add-test-user-and-shopping-cart-items-set.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/shoppingcart/delete-test-user-set.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Delete a book from shopping cart is successful")
    void deleteBookFromShoppingCart_ValidCartItemId_Success() throws Exception {
        Long cartItemId = 2L;

        String testUser = jwtUtil.generateToken("testuser@test.com");

        mockMvc.perform(delete("/cart/cart-items/" + cartItemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + testUser))
                .andExpect(status().isNoContent()) //SERVER_ERROR, "Internal Server Error"
                //.andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        List<CartItem> actualItems = cartItemRepository.findAll();
        ShoppingCart actualShoppingCart = testShoppingCartRepository.findShoppingCartByUserId(3L);

        assertEquals(2, actualShoppingCart.getCartItems().size());
        assertEquals(2, actualItems.size());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(scripts = "classpath:database/shoppingcart/add-test-user-set.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
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

        ShoppingCartDto expected = new ShoppingCartDto().setUser_id(3L)
                .setCartItemsIds(List.of(existingCartItem.getId()));

        int expectedQuantity = existingCartItem.getQuantity() + requestDto.quantity();

        String jsonRequestBody = objectMapper.writeValueAsString(requestDto);

        // Authentication authentication = new UsernamePasswordAuthenticationToken("testuser@test.com", "userone");
        //Principal principal = new UsernamePasswordAuthenticationToken("testuser@test.com", "userone");

        String testUser = jwtUtil.generateToken("testuser@test.com");

        MvcResult result = mockMvc.perform(post("/cart")
                        .content(jsonRequestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        /*.with(request -> {
                            request.setUserPrincipal(authentication);
                            //request.setRemoteUser("testuser@test.com");
                            return request;
                        }))*/
                        .header("Authorization", "Bearer " + testUser))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ShoppingCartDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ShoppingCartDto.class);

        Long cartItemId = actual.getCartItemsIds().stream()
                .filter(id -> id.equals(requestDto.bookId()))
                .findAny().get();
        int actualQuantity = cartItemRepository.findById(cartItemId).get().getQuantity();

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expected.getUser_id(), actual.getUser_id());
        assertEquals(expected.getCartItemsIds(), actual.getCartItemsIds());
        assertEquals(expectedQuantity, actualQuantity);
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(scripts = "classpath:database/shoppingcart/add-test-user-set.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/shoppingcart/delete-test-user-set.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Add a new book to shopping cart is successful")
    void addBookToShoppingCart_ValidRequestDtoNewItem_Success() throws Exception {
        CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto(
                5L, 2, 2L);

        ShoppingCart shoppingCart = shoppingCartRepository.findById(requestDto.shoppingCartId()).orElse(null);

        ShoppingCartDto expected = new ShoppingCartDto().setUser_id(shoppingCart.getUser().getId())
                .setCartItemsIds(List.of(3L, 2L));

        String jsonRequestBody = objectMapper.writeValueAsString(requestDto);

        String testUser = jwtUtil.generateToken("testuser@test.com");

        MvcResult result = mockMvc.perform(post("/cart")
                        .content(jsonRequestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + testUser))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ShoppingCartDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ShoppingCartDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expected.getUser_id(), actual.getUser_id());
        assertEquals(expected.getCartItemsIds(), actual.getCartItemsIds());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    void updateBookQuantityInShoppingCart() {
    }

}