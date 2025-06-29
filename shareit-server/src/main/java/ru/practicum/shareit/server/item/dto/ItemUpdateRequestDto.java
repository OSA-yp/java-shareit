package ru.practicum.shareit.server.item.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ItemUpdateRequestDto {

    private String name;
    private String description;
    private Boolean available;
}
