package ru.practicum.shareit.server.user.dto;

import lombok.Data;

@Data
public class UserUpdateRequestDto {

    private String name;
    private String email;
}
