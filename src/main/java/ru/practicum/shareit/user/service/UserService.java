package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateRequestDto;

public interface UserService {

    UserResponseDto createUser(UserRequestDto newUser);


    UserResponseDto getUserById(Long userId);


    UserResponseDto updateUser(UserUpdateRequestDto userDataToUpdate, Long userId);

    void deleteUser(Long userId);
}
