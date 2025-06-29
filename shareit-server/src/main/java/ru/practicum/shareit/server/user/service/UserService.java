package ru.practicum.shareit.server.user.service;

import ru.practicum.shareit.server.user.dto.UserRequestDto;
import ru.practicum.shareit.server.user.dto.UserResponseDto;
import ru.practicum.shareit.server.user.dto.UserUpdateRequestDto;

public interface UserService {

    UserResponseDto createUser(UserRequestDto newUser);


    UserResponseDto getUserById(Long userId);


    UserResponseDto updateUser(UserUpdateRequestDto userDataToUpdate, Long userId);

    void deleteUser(Long userId);
}
