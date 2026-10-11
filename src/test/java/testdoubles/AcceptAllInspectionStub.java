package testdoubles;

import domain.InventoryItem;
import service.InspectionResult;
import service.InspectionService;

public class AcceptAllInspectionStub implements InspectionService {
    @Override
    public InspectionResult inspect(InventoryItem item) {
        return InspectionResult.accept();
    }
}