package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;

public class UserMapper {

    public static User toUser(UserRequestDto userDto) {

        User user = new User();

        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());

        return user;
    }

    public static User toUser(UserResponseDto userDto) {

        User user = new User();
        user.setId(userDto.getId());
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());

        return user;
    }


    public static User toUser(UserUpdateRequestDto userDto, Long userId) {

        User user = new User();

        user.setId(userId);
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());

        return user;
    }

    public static UserResponseDto toUserResponseDto(User user) {
        UserResponseDto userResponseDto = new UserResponseDto();

        userResponseDto.setId(user.getId());
        userResponseDto.setName(user.getName());
        userResponseDto.setEmail(user.getEmail());

        return userResponseDto;
    }

    public static ShortUserResponseDto toShortUserResponseDto(User user) {
        ShortUserResponseDto shortUserResponseDto = new ShortUserResponseDto();

        shortUserResponseDto.setId(user.getId());
        shortUserResponseDto.setName(user.getName());

        return shortUserResponseDto;
    }
}
