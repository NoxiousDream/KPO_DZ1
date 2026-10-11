package service;

import com.google.inject.Inject;
import domain.InventoryItem;

import java.util.Objects;


public class FleetService {
    private final FleetRepository repository;
    private final InspectionService inspectionService;

    @Inject
    public FleetService(FleetRepository repository, InspectionService inspectionService) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.inspectionService = Objects.requireNonNull(inspectionService, "inspectionService");
    }

    public InspectionResult acceptToPark(InventoryItem item) {
        Objects.requireNonNull(item, "item");

        InspectionResult result = inspectionService.inspect(item);

        if (!result.accepted()) {
            return result;
        }

        try {
            repository.add(item);
            return result;
        } catch (IllegalArgumentException ex) {
            return InspectionResult.reject(ex.getMessage());
        }
    }
}