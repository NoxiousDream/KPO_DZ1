package service;


import domain.InventoryItem;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InMemoryFleetRepository implements FleetRepository {
    private final Map<String, InventoryItem> itemsByInventoryNumber = new LinkedHashMap<>();

    @Override
    public synchronized void add(InventoryItem item) {
        Objects.requireNonNull(item, "item");

        if (itemsByInventoryNumber.containsKey(item.getInventoryNumber())) {
            throw new IllegalArgumentException(
                    "Инвентарный номер уже зарегистрирован: " + item.getInventoryNumber()
            );
        }

        itemsByInventoryNumber.put(item.getInventoryNumber(), item);
    }

    @Override
    public synchronized List<InventoryItem> findAll() {
        return List.copyOf(itemsByInventoryNumber.values());
    }

    @Override
    public synchronized int size() {
        return itemsByInventoryNumber.size();
    }
}