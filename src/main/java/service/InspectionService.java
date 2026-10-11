package service;

import domain.InventoryItem;

public interface InspectionService {
    InspectionResult inspect(InventoryItem item);
}