package testdoubles;

import domain.InventoryItem;
import service.InspectionResult;
import service.InspectionService;

public class RejectAllInspectionStub implements InspectionService {
    @Override
    public InspectionResult inspect(InventoryItem item) {
        return InspectionResult.reject("Тестовый отказ техосмотра");
    }
}