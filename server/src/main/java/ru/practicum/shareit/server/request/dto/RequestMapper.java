package ru.practicum.shareit.server.request.dto;

import ru.practicum.shareit.server.item.dto.ItemInRequestResponseDto;
import ru.practicum.shareit.server.request.model.Request;

import java.util.Collection;

public class RequestMapper {

    public static Request toRequest(RequestRequestDto dto) {

        Request request = new Request();
        request.setDescription(dto.getDescription());

        return request;
    }

    public static RequestResponseDto toRequestResponseDto(Request request) {

        RequestResponseDto dto = new RequestResponseDto();

        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());

        return dto;
    }

    public static RequestWithItemsResponseDto toRequestWithItemsResponseDto(Request request, Collection<ItemInRequestResponseDto> itemsDto) {

        RequestWithItemsResponseDto dto = new RequestWithItemsResponseDto();

        dto.setId(request.getId());
        dto.setCreated(request.getCreated());
        dto.setDescription(request.getDescription());
        dto.setItems(itemsDto);

        return dto;

    }
}
