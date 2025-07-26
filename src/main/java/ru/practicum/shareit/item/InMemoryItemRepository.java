package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE)
@Qualifier("InMemoryStorage")
public class InMemoryItemRepository implements ItemRepository {
    final List<Item> items = new ArrayList<>();

    @Override
    public Item save(Item item) {
        item.setId(getId());
        items.add(item);
        return item;
    }

    @Override
    public Item update(Item item) {
        Optional<Item> existingItemOpt = findItemById(item.getId());
        if (existingItemOpt.isPresent()) {
            Item existingItem = existingItemOpt.get();
            int index = items.indexOf(existingItem);
            items.set(index, item);
            return item;
        }
        return null;
    }

    @Override
    public List<Item> findItemsByUserId(Long userId) {
        return items.stream()
                .filter(item -> item.getOwner().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> findItemById(Long id) {
        for (Item item : items) {
            if (item.getId().equals(id)) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }

    public List<Item> findItemsByTextAndAvailable(String text) {
        String lowerText = text.toLowerCase();
        return items.stream()
                .filter(item -> item.getAvailable().equals(true) &&
                        (item.getName().toLowerCase().contains(lowerText) ||
                                item.getDescription().toLowerCase().contains(lowerText)))
                .collect(Collectors.toList());
    }

    private long getId() {
        long lastId = items.stream()
                .mapToLong(Item::getId)
                .max()
                .orElse(0);
        return lastId + 1;
    }
}
