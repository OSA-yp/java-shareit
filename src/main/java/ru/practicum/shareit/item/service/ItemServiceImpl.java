package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dal.ItemStorage;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserService userService;

    @Override
    public ItemResponseDto createItem(ItemRequestDto newItemDto, Long ownerId) {

        userService.getUserById(ownerId); // проверка, а существует ли user

        Item newItem = ItemMapper.toItem(newItemDto);
        newItem.setOwner(ownerId);

        return ItemMapper.toItemResponseDto(itemStorage.addItem(newItem));
    }

    @Override
    public ItemResponseDto updateItem(Long itemId, ItemUpdateRequestDto itemDataToUpdate, Long userId) {

        userService.getUserById(userId); // проверка, а существует ли user

        Item item = checkAndGetItemById(itemId);

        // Редактировать вещь может только её владелец.
        if (!item.getOwner().equals(userId)) {

            String message = "Only owner can update item id=" + itemId;
            log.warn(message);
            throw new ForbiddenException(message);
        }

        return ItemMapper.toItemResponseDto(itemStorage.updateItem(ItemMapper.toItem(itemId, itemDataToUpdate)));
    }

    @Override
    public ItemResponseDto getItemById(Long itemId) {
        return ItemMapper.toItemResponseDto(checkAndGetItemById(itemId));
    }

    @Override
    public Collection<ItemResponseDto> getItemsByUser(Long userId) {

        userService.getUserById(userId);  // проверка, а существует ли user

        return itemStorage.getAllUserItems(userId).stream()
                .map(ItemMapper::toItemResponseDto)
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<ItemResponseDto> searchItems(String searchString) {

        if (searchString.isEmpty()) {
            return Set.of();
        }


        return itemStorage.searchItems(searchString).stream()
                .map(ItemMapper::toItemResponseDto)
                .collect(Collectors.toSet());
    }


    private Item checkAndGetItemById(Long itemId) {

        Optional<Item> maybeItem = itemStorage.getItemById(itemId);

        if (maybeItem.isEmpty()) {
            String message = "Item with id=" + itemId + " not found";
            log.warn(message);
            throw new NotFoundException(message);
        }
        return maybeItem.get();
    }
}
