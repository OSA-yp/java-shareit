package ru.practicum.shareit.server.item.dto;

import lombok.Data;

@Data
public class ItemResponseDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
}
