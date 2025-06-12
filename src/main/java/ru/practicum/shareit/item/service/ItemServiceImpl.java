package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dal.ItemRepository;
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

// Логирование ошибок в ErrorResponse, логирование запросов - org.zalando
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final UserService userService;

    @Override
    public ItemResponseDto createItem(ItemRequestDto newItemDto, Long ownerId) {

        userService.getUserById(ownerId); // проверка, а существует ли user

        Item newItem = ItemMapper.toItem(newItemDto);
        newItem.setOwner(ownerId);

        return ItemMapper.toItemResponseDto(repository.save(newItem));
    }

    @Override
    public ItemResponseDto updateItem(Long itemId, ItemUpdateRequestDto itemDataToUpdate, Long userId) {

        userService.getUserById(userId); // проверка, а существует ли user

        Item existingItem = checkAndGetItemById(itemId);

        // Редактировать вещь может только её владелец.
        if (!existingItem.getOwner().equals(userId)) {
            throw new ForbiddenException("Only owner can update item id=" + itemId);
        }

        if (itemDataToUpdate.getName() != null) {
            existingItem.setName(itemDataToUpdate.getName());
        }

        if (itemDataToUpdate.getDescription() != null) {
            existingItem.setDescription(itemDataToUpdate.getDescription());
        }

        if (itemDataToUpdate.getAvailable() != null) {
            existingItem.setAvailable(itemDataToUpdate.getAvailable());
        }

        return ItemMapper.toItemResponseDto(repository.save(existingItem));
    }

    @Override
    public ItemResponseDto getItemById(Long itemId) {
        return ItemMapper.toItemResponseDto(checkAndGetItemById(itemId));
    }

    @Override
    public Collection<ItemResponseDto> getItemsByUser(Long userId) {

        userService.getUserById(userId);  // проверка, а существует ли user

        return repository.findByOwner(userId).stream()
                .map(ItemMapper::toItemResponseDto)
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<ItemResponseDto> searchItems(String searchString) {

        if (searchString.isEmpty()) {
            return Set.of();
        }

        return repository.searchItems(searchString).stream()
                .map(ItemMapper::toItemResponseDto)
                .collect(Collectors.toSet());
    }


    private Item checkAndGetItemById(Long itemId) {

        Optional<Item> maybeItem = repository.getItemById(itemId);

        if (maybeItem.isEmpty()) {
            throw new NotFoundException("Item with id=" + itemId + " not found");
        }
        return maybeItem.get();
    }
}
