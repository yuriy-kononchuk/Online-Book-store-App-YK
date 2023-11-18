package com.example.project.mapper;

import com.example.project.dto.user.UserRegistrationRequestDto;
import com.example.project.dto.user.UserResponseDto;
import com.example.project.model.User;

public interface UserMapper {
    UserResponseDto toUserResponse(User user);

    User convertToModel(UserRegistrationRequestDto userDto);
}
