package service;

import domain.InventoryItem;

import java.util.List;

public interface FleetRepository {
    void add(InventoryItem item);

    List<InventoryItem> findAll();

    int size();
}