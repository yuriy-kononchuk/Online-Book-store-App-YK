package com.example.project.service.impl;

import com.example.project.dto.user.UserRegistrationRequestDto;
import com.example.project.dto.user.UserResponseDto;
import com.example.project.exception.RegistrationException;
import com.example.project.mapper.UserMapper;
import com.example.project.model.User;
import com.example.project.repository.UserRepository;
import com.example.project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RegistrationException("Can't register a user");
        }
        User user = userMapper.toModel(requestDto);
        return userMapper.toUserResponse(userRepository.save(user));
    }
}
