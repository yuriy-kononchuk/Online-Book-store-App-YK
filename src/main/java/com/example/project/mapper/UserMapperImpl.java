package com.example.project.mapper;

import com.example.project.dto.user.UserRegistrationRequestDto;
import com.example.project.dto.user.UserResponseDto;
import com.example.project.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper {
    @Override
    public UserResponseDto toUserResponse(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setShippingAddress(user.getShippingAddress());
        return dto;
    }

    @Override
    public User convertToModel(UserRegistrationRequestDto userDto) {
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setShippingAddress(userDto.getShippingAddress());
        return user;
    }
}
