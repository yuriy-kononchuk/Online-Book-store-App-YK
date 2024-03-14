package com.example.project.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.example.project.dto.cartitem.CartItemDto;
import com.example.project.dto.cartitem.CreateCartItemRequestDto;
import com.example.project.mapper.CartItemMapper;
import com.example.project.model.Book;
import com.example.project.model.CartItem;
import com.example.project.model.Category;
import com.example.project.model.ShoppingCart;
import com.example.project.model.User;
import com.example.project.repository.CartItemRepository;
import com.example.project.service.impl.CartItemServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CartItemServiceTest {
    @InjectMocks
    private CartItemServiceImpl cartItemService;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private CartItemMapper cartItemMapper;

    @Test
    @DisplayName("Verify save() method works correctly")
    void save_ValidCreateCartItemRequestDto_ReturnsCartItemDto() {
        User user = new User();
        user.setId(1L);
        user.setEmail("firstuser@test.com");
        user.setPassword("userone");
        user.setFirstName("User");
        user.setLastName("First");
        user.setShoppingCart(new ShoppingCart());

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(2L);
        shoppingCart.setUser(user);
        shoppingCart.setCartItems(new HashSet<>());

        Category category = new Category();
        category.setId(2L);
        category.setName("historical");
        category.setDescription("history researches");

        Book book = new Book();
        book.setId(2L);
        book.setTitle("Ukraine wins");
        book.setAuthor("Zaluzhnyj");
        book.setIsbn("555-5-23-230872-5");
        book.setPrice(BigDecimal.valueOf(49.99));
        book.setDescription("victory for Ukraine");
        book.setCoverImage("Best buy");
        book.setCategories(Set.of(category));

        CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto(
                2L, 2, 2L);

        CartItem cartItem = new CartItem();
        cartItem.setBook(book);
        cartItem.setQuantity(requestDto.quantity());
        cartItem.setShoppingCart(shoppingCart);

        CartItemDto cartItemDto = new CartItemDto()
                .setId(1L).setBookId(cartItem.getBook().getId())
                .setBookTitle(cartItem.getBook().getTitle())
                .setQuantity(cartItem.getQuantity())
                .setShoppingCartId(cartItem.getShoppingCart().getId());

        when(cartItemMapper.toEntity(requestDto)).thenReturn(cartItem);
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);
        when(cartItemMapper.toDto(cartItem)).thenReturn(cartItemDto);

        CartItemDto savedCartItemDto = cartItemService.save(requestDto);

        AssertionsForClassTypes.assertThat(savedCartItemDto).isEqualTo(cartItemDto);
        verify(cartItemRepository, times(1)).save(cartItem);
        verify(cartItemMapper, times(1)).toDto(cartItem);
        verifyNoMoreInteractions(cartItemRepository, cartItemMapper);
    }

    @Test
    @DisplayName("Verify getById() method works correctly")
    void getById_ValidCartItemId_ReturnsValidCartItemDto() {
        User user = new User();
        user.setId(1L);
        user.setEmail("firstuser@test.com");
        user.setPassword("userone");
        user.setFirstName("User");
        user.setLastName("First");
        user.setShoppingCart(new ShoppingCart());

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(2L);
        shoppingCart.setUser(user);
        shoppingCart.setCartItems(new HashSet<>());

        Category category = new Category();
        category.setId(2L);
        category.setName("historical");
        category.setDescription("history researches");

        Book book = new Book();
        book.setId(2L);
        book.setTitle("Ukraine wins");
        book.setAuthor("Zaluzhnyj");
        book.setIsbn("555-5-23-230872-5");
        book.setPrice(BigDecimal.valueOf(49.99));
        book.setDescription("victory for Ukraine");
        book.setCoverImage("Best buy");
        book.setCategories(Set.of(category));

        Long cartItemId = 1L;

        CartItem cartItem = new CartItem();
        cartItem.setId(cartItemId);
        cartItem.setBook(book);
        cartItem.setQuantity(2);
        cartItem.setShoppingCart(shoppingCart);

        CartItemDto cartItemDto = new CartItemDto()
                .setId(1L).setBookId(cartItem.getBook().getId())
                .setBookTitle(cartItem.getBook().getTitle())
                .setQuantity(cartItem.getQuantity())
                .setShoppingCartId(cartItem.getShoppingCart().getId());

        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem));
        when(cartItemMapper.toDto(cartItem)).thenReturn(cartItemDto);

        CartItemDto cartItemDtoById = cartItemService.getById(cartItemId);

        AssertionsForClassTypes.assertThat(cartItemDtoById).isEqualTo(cartItemDto);
        verify(cartItemRepository, times(1)).findById(cartItemId);
        verify(cartItemMapper, times(1)).toDto(cartItem);
        verifyNoMoreInteractions(cartItemRepository, cartItemMapper);
    }

    @Test
    @DisplayName("Verify getById() method with wrong Id throws exception")
    void getById_NonValidCartItemId_ThrowsException() {
        Long cartItemId = 1L;

        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> cartItemService.getById(cartItemId));

        String expected = "Can't find a cart item by id: " + cartItemId;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify updateQuantity method by CartItemId works correctly")
    void updateQuantityByCartItemId_ValidCartItemId_ReturnsCartItemUpdated() {
        User user = new User();
        user.setId(1L);
        user.setEmail("firstuser@test.com");
        user.setPassword("userone");
        user.setFirstName("User");
        user.setLastName("First");
        user.setShoppingCart(new ShoppingCart());

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(2L);
        shoppingCart.setUser(user);
        shoppingCart.setCartItems(new HashSet<>());

        Category category = new Category();
        category.setId(2L);
        category.setName("historical");
        category.setDescription("history researches");

        Book book = new Book();
        book.setId(2L);
        book.setTitle("Ukraine wins");
        book.setAuthor("Zaluzhnyj");
        book.setIsbn("555-5-23-230872-5");
        book.setPrice(BigDecimal.valueOf(49.99));
        book.setDescription("victory for Ukraine");
        book.setCoverImage("Best buy");
        book.setCategories(Set.of(category));

        Long cartItemId = 1L;

        CartItem oldCartItem = new CartItem();
        oldCartItem.setId(cartItemId);
        oldCartItem.setBook(book);
        oldCartItem.setShoppingCart(shoppingCart);
        oldCartItem.setQuantity(2);

        CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto(
                2L, 5, 2L);

        CartItem updatedCartItem = new CartItem();
        updatedCartItem.setId(cartItemId);
        updatedCartItem.setBook(book);
        updatedCartItem.setShoppingCart(shoppingCart);
        updatedCartItem.setQuantity(requestDto.quantity());

        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(oldCartItem));
        when(cartItemRepository.save(updatedCartItem)).thenReturn(updatedCartItem);

        CartItem newCartItem = cartItemService.updateQuantityByCartItemId(cartItemId, requestDto);

        AssertionsForClassTypes.assertThat(newCartItem).isEqualTo(updatedCartItem);
        verify(cartItemRepository, times(1)).findById(cartItemId);
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
        verifyNoMoreInteractions(cartItemRepository);
    }

    @Test
    @DisplayName("Verify updateQuantity method with wrong CartItemId throws exception")
    void updateQuantityByCartItemId_NonValidCartItemId_ThrowsException() {
        CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto(
                2L, 2, 2L);
        Long cartItemId = 5L;

        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> cartItemService.updateQuantityByCartItemId(cartItemId, requestDto));

        String expected = "Can't find a cart item by id: " + cartItemId;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify deleteById() method works correctly")
    void deleteById_ValiCattItemId_CartItemIsDeleted() {
        User user = new User();
        user.setId(1L);
        user.setEmail("firstuser@test.com");
        user.setPassword("userone");
        user.setFirstName("User");
        user.setLastName("First");
        user.setShoppingCart(new ShoppingCart());

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(2L);
        shoppingCart.setUser(user);
        shoppingCart.setCartItems(new HashSet<>());

        Category category = new Category();
        category.setId(2L);
        category.setName("historical");
        category.setDescription("history researches");

        Book book = new Book();
        book.setId(2L);
        book.setTitle("Ukraine wins");
        book.setAuthor("Zaluzhnyj");
        book.setIsbn("555-5-23-230872-5");
        book.setPrice(BigDecimal.valueOf(49.99));
        book.setDescription("victory for Ukraine");
        book.setCoverImage("Best buy");
        book.setCategories(Set.of(category));

        Long cartItemId = 1L;
        CartItem cartItem = new CartItem();
        cartItem.setId(cartItemId);
        cartItem.setBook(book);
        cartItem.setQuantity(2);
        cartItem.setShoppingCart(shoppingCart);

        cartItemRepository.save(cartItem);
        cartItemService.deleteById(cartItemId);

        verify(cartItemRepository, times(1)).save(cartItem);
        verify(cartItemRepository, times(1)).deleteById(cartItemId);
        verifyNoMoreInteractions(cartItemRepository);
    }

    @Test
    @DisplayName("Verify deleteById() method with Id is null calls exception")
    void deleteById_WithCartItemIdIsNull_ThrowsException() {
        Long cartItemId = null;

        doThrow(IllegalArgumentException.class).when(cartItemRepository).deleteById(cartItemId);

        assertThrows(IllegalArgumentException.class, () -> cartItemService.deleteById(cartItemId));

        verify(cartItemRepository, times(1)).deleteById(cartItemId);
    }
}
