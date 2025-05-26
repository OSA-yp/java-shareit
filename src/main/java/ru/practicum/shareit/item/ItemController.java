package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;


@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemResponseDto createItem(
            @RequestBody
            @Valid
            ItemRequestDto newItem,
            @RequestHeader("X-Sharer-User-Id")
            Long userId) {
        return itemService.createItem(newItem, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto updateItem(
            @PathVariable long itemId,
            @RequestBody
            @Valid
            ItemUpdateRequestDto itemDataToUpdate,
            @RequestHeader("X-Sharer-User-Id")
            Long userId) {
        return itemService.updateItem(itemId, itemDataToUpdate, userId);
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItem(@PathVariable
                                   Long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public Collection<ItemResponseDto> getItemsByUser(@RequestHeader("X-Sharer-User-Id")
                                                      Long userId) {
        return itemService.getItemsByUser(userId);
    }

    @GetMapping("/search")
    public Collection<ItemResponseDto> searchItems(
            @RequestHeader("X-Sharer-User-Id")
            Long userId,
            @RequestParam("text")
            String searchString) {
        return itemService.searchItems(searchString);
    }


}
