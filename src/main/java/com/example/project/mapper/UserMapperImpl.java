package com.example.project.mapper;

import com.example.project.dto.user.UserRegistrationRequestDto;
import com.example.project.dto.user.UserResponseDto;
import com.example.project.model.ShoppingCart;
import com.example.project.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapperImpl implements UserMapper {
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto toUserResponse(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setShippingAddress(user.getShippingAddress());

        dto.setShoppingCartId(user.getShoppingCart().getId()); // added

        return dto;
    }

    @Override
    public User toModel(UserRegistrationRequestDto userDto) {
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword())); // added
        user.setPassword(userDto.getPassword());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setShippingAddress(userDto.getShippingAddress());

        ShoppingCart shoppingCart = new ShoppingCart(userDto.getShoppingCartId()); // added
        user.setShoppingCart(shoppingCart);

        return user;
    }
}
