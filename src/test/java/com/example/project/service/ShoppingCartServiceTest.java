package com.example.project.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.example.project.dto.cartitem.CartItemDto;
import com.example.project.dto.cartitem.CreateCartItemRequestDto;
import com.example.project.dto.shoppingcart.CreateShoppingCartRequestDto;
import com.example.project.dto.shoppingcart.ShoppingCartDto;
import com.example.project.mapper.CartItemMapper;
import com.example.project.mapper.ShoppingCartMapper;
import com.example.project.model.Book;
import com.example.project.model.CartItem;
import com.example.project.model.Category;
import com.example.project.model.ShoppingCart;
import com.example.project.model.User;
import com.example.project.repository.CartItemRepository;
import com.example.project.repository.ShoppingCartRepository;
import com.example.project.service.impl.ShoppingCartServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceTest {
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private CartItemMapper cartItemMapper;

    @Test
    @DisplayName("Verify save() method works correctly")
    void save_ValidCreateShoppingCartRequestDto_ReturnsShoppingCartDto() {
        ShoppingCart shoppingCart = new ShoppingCart();

        User user = new User();
        user.setId(1L);
        user.setEmail("firstuser@test.com");
        user.setPassword("userone");
        user.setFirstName("User");
        user.setLastName("First");
        user.setShoppingCart(shoppingCart);

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

        CartItem cartItem = new CartItem();
        cartItem.setId(2L);
        cartItem.setBook(book);
        cartItem.setQuantity(2);
        cartItem.setShoppingCart(shoppingCart);

        CreateShoppingCartRequestDto requestDto =
                new CreateShoppingCartRequestDto(user, List.of(2L));

        shoppingCart.setUser(requestDto.user());
        shoppingCart.setCartItems(Set.of(cartItem));

        ShoppingCartDto shoppingCartDto = new ShoppingCartDto()
                .setId(1L).setUserId(shoppingCart.getUser().getId())
                .setCartItemsIds(List.of(cartItem.getId()));

        when(shoppingCartMapper.toEntity(requestDto)).thenReturn(shoppingCart);
        when(shoppingCartRepository.save(shoppingCart)).thenReturn(shoppingCart);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(shoppingCartDto);

        ShoppingCartDto savedShoppingCartDto = shoppingCartService.save(requestDto);

        AssertionsForClassTypes.assertThat(savedShoppingCartDto).isEqualTo(shoppingCartDto);
        verify(shoppingCartRepository, times(1)).save(shoppingCart);
        verify(shoppingCartMapper, times(1)).toDto(shoppingCart);
        verifyNoMoreInteractions(shoppingCartRepository, shoppingCartMapper);
    }

    @Test
    @DisplayName("Verify findAllByShoppingCartId() method works correctly")
    void findAllByShoppingCartId_ValidPagebleAndShoppingCartId_ReturnsCartItemDtos() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);

        User user = new User();
        user.setId(1L);
        user.setEmail("firstuser@test.com");
        user.setPassword("userone");
        user.setFirstName("User");
        user.setLastName("First");
        user.setShoppingCart(shoppingCart);

        CartItem cartItem1 = new CartItem();
        cartItem1.setId(1L);
        cartItem1.setBook(new Book());
        cartItem1.setShoppingCart(shoppingCart);
        cartItem1.setQuantity(2);

        CartItem cartItem2 = new CartItem();
        cartItem2.setId(1L);
        cartItem2.setBook(new Book());
        cartItem2.setShoppingCart(shoppingCart);
        cartItem2.setQuantity(3);

        CartItemDto cartItemDto1 = new CartItemDto().setId(cartItem1.getId())
                .setBookId(cartItem1.getBook().getId()).setBookTitle(cartItem1.getBook().getTitle())
                .setShoppingCartId(cartItem1.getShoppingCart().getId())
                .setQuantity(cartItem1.getQuantity());

        CartItemDto cartItemDto2 = new CartItemDto().setId(cartItem2.getId())
                .setBookId(cartItem2.getBook().getId()).setBookTitle(cartItem2.getBook().getTitle())
                .setShoppingCartId(cartItem2.getShoppingCart().getId())
                .setQuantity(cartItem2.getQuantity());

        Pageable pageable = PageRequest.of(0, 10);
        List<CartItem> cartItems = List.of(cartItem1, cartItem2);

        when(cartItemRepository.findAllByShoppingCartId(shoppingCart.getId(), pageable))
                .thenReturn((cartItems));
        when(cartItemMapper.toDto(cartItem1)).thenReturn(cartItemDto1);
        when(cartItemMapper.toDto(cartItem2)).thenReturn(cartItemDto2);

        List<CartItemDto> cartItemDtos = shoppingCartService
                .findAllByShoppingCartId(user, pageable);

        AssertionsForClassTypes.assertThat(cartItemDtos.size()).isEqualTo(2);
        AssertionsForClassTypes.assertThat(cartItemDtos.get(0))
                .isEqualTo(cartItemDto1);
        AssertionsForClassTypes.assertThat(cartItemDtos.get(1))
                .isEqualTo(cartItemDto2);

        verify(cartItemRepository, times(1))
                .findAllByShoppingCartId(shoppingCart.getId(), pageable);
        verify(cartItemMapper, times(1)).toDto(cartItem1);
        verify(cartItemMapper, times(1)).toDto(cartItem2);
        verifyNoMoreInteractions(cartItemRepository, cartItemMapper);
    }

    @Test
    @DisplayName("Verify adBookToShoppingCart() method with new item works correctly")
    void addBookToShoppingCart_NewValidCreateCartItemRequestDto_ReturnsShoppingCartDto() {
        Book book = new Book();
        book.setId(2L);
        book.setTitle("Ukraine wins");
        book.setAuthor("Zaluzhnyj");
        book.setIsbn("555-5-23-230872-5");
        book.setPrice(BigDecimal.valueOf(49.99));
        book.setDescription("victory for Ukraine");
        book.setCoverImage("Best buy");

        User user = new User(1L);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(2L);
        shoppingCart.setCartItems(new HashSet<>());
        shoppingCart.setUser(user);

        CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto(
                2L, 2, 2L);

        CartItem cartItem = new CartItem();
        cartItem.setId(2L);
        cartItem.setBook(book);
        cartItem.setQuantity(requestDto.quantity());
        cartItem.setShoppingCart(shoppingCart);

        Long userId = 1L;

        ShoppingCartDto shoppingCartDto = new ShoppingCartDto()
                .setId(shoppingCart.getId()).setUserId(shoppingCart.getUser().getId())
                .setCartItemsIds(List.of(cartItem.getId()));

        when(shoppingCartRepository.findShoppingCartByUserId(userId)).thenReturn(shoppingCart);
        when(cartItemMapper.toEntity(requestDto)).thenReturn(cartItem);
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(shoppingCartDto);

        ShoppingCartDto actualDto = shoppingCartService.addBookToShoppingCart(userId, requestDto);

        assertNotNull(actualDto);
        AssertionsForClassTypes.assertThat(actualDto).isEqualTo(shoppingCartDto);
        assertEquals(shoppingCartDto.getId(), actualDto.getId());
        verify(shoppingCartRepository, times(1)).findShoppingCartByUserId(userId);
        verify(cartItemMapper, times(1)).toEntity(requestDto);
        verify(cartItemRepository, times(1)).save(cartItem);
        verify(shoppingCartRepository, times(1)).save(shoppingCart);
        verify(shoppingCartMapper, times(1)).toDto(shoppingCart);
        verifyNoMoreInteractions(shoppingCartRepository, shoppingCartMapper,
                cartItemRepository, cartItemMapper);
    }

    @Test
    @DisplayName("Verify adBookToShoppingCart() method with existing item updates quantity")
    void addBookToShoppingCart_WithExistingCartItemRequestDto_ShouldUpdateQuantity() {
        ShoppingCart shoppingCartByUserId = new ShoppingCart();
        shoppingCartByUserId.setId(1L);
        shoppingCartByUserId.setUser(new User());

        Book existingBook = new Book();
        existingBook.setId(1L);
        existingBook.setTitle("Ukraine wins");
        existingBook.setAuthor("Zaluzhnyj");
        existingBook.setIsbn("555-5-23-230872-5");
        existingBook.setPrice(BigDecimal.valueOf(49.99));
        existingBook.setDescription("victory for Ukraine");
        existingBook.setCoverImage("Best buy");

        CartItem existingCartItem = new CartItem();
        existingCartItem.setId(2L);
        existingCartItem.setBook(existingBook);
        existingCartItem.setQuantity(3);
        shoppingCartByUserId.setCartItems(Set.of(existingCartItem));

        Long userId = 1L;
        CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto(1L, 2, 3L);

        ShoppingCartDto shoppingCartDto = new ShoppingCartDto()
                .setId(shoppingCartByUserId.getId())
                .setUserId(shoppingCartByUserId.getUser().getId())
                .setCartItemsIds(List.of(existingCartItem.getId()));

        when(shoppingCartRepository.findShoppingCartByUserId(userId))
                .thenReturn(shoppingCartByUserId);
        when(shoppingCartRepository.save(shoppingCartByUserId)).thenReturn(shoppingCartByUserId);
        when(shoppingCartMapper.toDto(shoppingCartByUserId)).thenReturn(shoppingCartDto);

        ShoppingCartDto actualDto = shoppingCartService.addBookToShoppingCart(userId, requestDto);

        assertNotNull(actualDto);
        assertEquals(shoppingCartByUserId.getId(), actualDto.getId());
        assertEquals(5, existingCartItem.getQuantity());
        verify(shoppingCartRepository, times(1)).findShoppingCartByUserId(userId);
        verify(shoppingCartRepository, times(1)).save(shoppingCartByUserId);
        verify(shoppingCartMapper, times(1)).toDto(shoppingCartByUserId);
        verifyNoMoreInteractions(shoppingCartRepository, shoppingCartMapper);
    }

    @Test
    @DisplayName("Verify deleteBookByCartItemId() method works correctly")
    void deleteBookByCartItemId_ValidCartItemAndUserId_ReturnsShoppingCartDto() {
        Long cartItemId = 1L;

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        Set<CartItem> cartItems = new HashSet<>();
        CartItem cartItemToDelete = new CartItem();
        cartItemToDelete.setId(cartItemId);
        cartItemToDelete.setShoppingCart(shoppingCart);
        cartItems.add(cartItemToDelete);
        shoppingCart.setCartItems(cartItems);
        shoppingCart.setUser(new User());

        Long userId = 1L;

        ShoppingCartDto shoppingCartDto = new ShoppingCartDto()
                .setId(1L).setUserId(new User().getId()).setCartItemsIds(List.of(cartItemId));

        when(shoppingCartRepository.findShoppingCartByUserId(userId)).thenReturn(shoppingCart);
        doNothing().when(cartItemRepository).deleteById(cartItemToDelete.getId());
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(shoppingCartDto);

        ShoppingCartDto actualDto = shoppingCartService.deleteBookByCartItemId(cartItemId, userId);

        assertEquals(shoppingCartDto.getId(), actualDto.getId());
        verify(shoppingCartRepository, times(1)).findShoppingCartByUserId(userId);
        verify(cartItemRepository, times(1)).deleteById(cartItemToDelete.getId());
        verify(shoppingCartMapper, times(1)).toDto(shoppingCart);
        verifyNoMoreInteractions(shoppingCartRepository, shoppingCartMapper,
                cartItemRepository);
    }

    @Test
    @DisplayName("Verify deleteBookByCartItemId() with wrong Id throws exception")
    void deleteBookByCartItemId_NonValidCartItem_ThrowsException() {
        Long cartItemId = 1L;
        Long userId = 1L;

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);

        when(shoppingCartRepository.findShoppingCartByUserId(userId)).thenReturn(shoppingCart);

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.deleteBookByCartItemId(cartItemId, userId));

        String expected = "Can't find a cart item";
        String actual = exception.getMessage();
        assertEquals(expected, actual);
        verify(shoppingCartRepository, times(1)).findShoppingCartByUserId(userId);
        verify(cartItemRepository, never()).delete(any());
        verify(shoppingCartRepository, never()).save(any());
        verify(shoppingCartMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Verify updateBookQuantityByCartItemId() method works correctly")
    void updateBookQuantityByCartItemId_ValidCreateCartItemRequestDto_ReturnsShoppingCartDto() {
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
        shoppingCart.setUser(new User());

        CartItem cartItemToUpdateQuantity = new CartItem();
        cartItemToUpdateQuantity.setId(2L);
        cartItemToUpdateQuantity.setBook(book);
        cartItemToUpdateQuantity.setQuantity(2);
        cartItemToUpdateQuantity.setShoppingCart(shoppingCart);

        Set<CartItem> cartItems = new HashSet<>();
        cartItems.add(cartItemToUpdateQuantity);
        shoppingCart.setCartItems(cartItems);

        CartItem cartItemUpdatedQuantity = cartItemToUpdateQuantity;
        cartItemUpdatedQuantity.setQuantity(5);

        Long cartItemId = 2L;
        Long userId = 1L;
        CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto(
                2L, 5, 2L);

        ShoppingCartDto shoppingCartDto = new ShoppingCartDto()
                .setId(shoppingCart.getId()).setUserId(shoppingCart.getUser().getId())
                .setCartItemsIds(List.of(cartItemUpdatedQuantity.getId()));

        when(shoppingCartRepository.findShoppingCartByUserId(userId)).thenReturn(shoppingCart);
        when(cartItemRepository.save(cartItemUpdatedQuantity)).thenReturn(cartItemUpdatedQuantity);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(shoppingCartDto);

        ShoppingCartDto actualDto = shoppingCartService
                .updateBookQuantityByCartItemId(cartItemId, requestDto, userId);

        assertEquals(shoppingCartDto.getId(), actualDto.getId());
        assertEquals(requestDto.quantity(), cartItemUpdatedQuantity.getQuantity());
        verify(shoppingCartRepository, times(1)).findShoppingCartByUserId(userId);
        verify(cartItemRepository, times(1)).save(cartItemUpdatedQuantity);
        verify(shoppingCartMapper, times(1)).toDto(shoppingCart);
        verifyNoMoreInteractions(shoppingCartRepository, shoppingCartMapper,
                cartItemRepository);
    }

    @Test
    @DisplayName("Verify updateBookQuantityByCartItemId() with wrong Id throws exception")
    void updateBookQuantityByCartItemId_NonValidCartItemId_ThrowsException() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        Set<CartItem> cartItems = new HashSet<>();
        CartItem cartItemToUpdate = new CartItem();
        cartItemToUpdate.setId(1L);
        cartItemToUpdate.setQuantity(3);
        cartItems.add(cartItemToUpdate);
        shoppingCart.setCartItems(cartItems);

        Long cartItemId = 10L;
        Long userId = 1L;
        CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto(
                2L, 5, 2L);

        when(shoppingCartRepository.findShoppingCartByUserId(userId)).thenReturn(shoppingCart);

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService
                        .updateBookQuantityByCartItemId(cartItemId, requestDto, userId));

        String expected = "Can't find a cart item";
        String actual = exception.getMessage();
        assertEquals(expected, actual);
        verify(shoppingCartRepository, times(1)).findShoppingCartByUserId(userId);
        verify(cartItemRepository, never()).save(any());
        verify(shoppingCartMapper, never()).toDto(any());
    }
}
