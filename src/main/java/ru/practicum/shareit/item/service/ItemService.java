package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;

import java.util.Collection;

public interface ItemService {

    ItemResponseDto createItem(ItemRequestDto newItem, Long userId);

    ItemResponseDto updateItem(Long itemId, ItemUpdateRequestDto itemDataToUpdate, Long userId);

    ItemResponseDto getItemById(Long itemId);

    Collection<ItemResponseDto> getItemsByUser(Long userId);

    Collection<ItemResponseDto> searchItems(String searchString);
}
