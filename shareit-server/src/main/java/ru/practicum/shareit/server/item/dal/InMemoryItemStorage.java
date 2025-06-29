package ru.practicum.shareit.server.item.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.server.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class InMemoryItemStorage implements ItemStorage {

    private final HashMap<Long, Item> items = new HashMap<>();
    private Long itemsId = 0L;


    public Optional<Item> getItemById(Long itemId) {
        if (items.containsKey(itemId)) {
            return Optional.of(items.get(itemId));
        } else {
            return Optional.empty();
        }
    }

    public Collection<Item> getAllUserItems(Long userId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwner().equals(userId))
                .collect(Collectors.toList());
    }

    public Item addItem(Item newItem) {
        itemsId++;
        newItem.setId(itemsId);

        items.put(itemsId, newItem);

        return newItem;
    }

    public Item updateItem(Item itemToUpdate) {
        Item item = items.get(itemToUpdate.getId());

        if (itemToUpdate.getName() != null) {
            item.setName(itemToUpdate.getName());
        }

        if (itemToUpdate.getDescription() != null) {
            item.setDescription(itemToUpdate.getDescription());
        }

        if (itemToUpdate.getAvailable() != null) {
            item.setAvailable(itemToUpdate.getAvailable());
        }

        return item;
    }

    public void deleteItem(Long itemId) {
        items.remove(itemId);
    }

    @Override
    public Collection<Item> searchItems(String searchString) {
        searchString = searchString.toLowerCase();
        // поиск возвращает только доступные для аренды вещи.
        String finalSearchString = searchString;
        return items.values()
                .stream()
                .filter(item ->
                        item.getAvailable() && ((item.getName().toLowerCase().contains(finalSearchString)) ||
                                (item.getDescription().toLowerCase().contains(finalSearchString))))
                .toList();

    }

}
