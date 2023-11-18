package com.example.project.service;

import com.example.project.dto.user.UserRegistrationRequestDto;
import com.example.project.dto.user.UserResponseDto;
import com.example.project.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;
}
