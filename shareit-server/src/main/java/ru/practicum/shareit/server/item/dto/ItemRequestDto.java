package ru.practicum.shareit.server.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ItemRequestDto {

    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}
