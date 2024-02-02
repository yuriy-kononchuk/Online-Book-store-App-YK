package com.example.project.controller;

import com.example.project.dto.cartitem.CartItemDto;
import com.example.project.dto.cartitem.CreateCartItemRequestDto;
import com.example.project.dto.shoppingcart.ShoppingCartDto;
import com.example.project.model.User;
import com.example.project.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart management", description = "Endpoints for mapping shopping cart")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a new book to shopping cart",
            description = "Add a new book to shopping cart")
    @ApiResponse(responseCode = "201", description = "New book was added successfully")
    public ShoppingCartDto addBookToShoppingCart(
            @RequestBody @Valid CreateCartItemRequestDto cartItemRequestDto,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.addBookToShoppingCart(user.getId(), cartItemRequestDto);
    }

    @GetMapping
    @Operation(summary = "Get a list of books by user ID",
            description = "Get a user's shopping cart list of all books by user id")
    public List<CartItemDto> getAllShoppingCartItems(Authentication authentication,
                                                     Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.findAllByShoppingCartId(user, pageable);
    }

    @PutMapping("/cart-items/{id}")
    @Operation(summary = "Update a quantity for a book",
            description = "Update a quantity for a book in shopping cart")
    @ApiResponse(responseCode = "200", description = "Quantity for requested book was updated")
    public ShoppingCartDto updateBookQuantityInShoppingCart(
            @PathVariable Long id,
            @RequestBody @Valid CreateCartItemRequestDto cartItemRequestDto,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.updateBookQuantityByCartItemId(
                id, cartItemRequestDto, user.getId());
    }

    @DeleteMapping("/cart-items/{id}")
    @Operation(summary = "Delete a book from shopping cart",
            description = "Delete a book from shopping cart")
    @ApiResponse(responseCode = "200",
            description = "Requested book was deleted from shopping cart")
    public ShoppingCartDto deleteBookFromShoppingCart(@PathVariable Long id,
                                                      Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.deleteBookByCartItemId(id, user.getId());
    }
}
