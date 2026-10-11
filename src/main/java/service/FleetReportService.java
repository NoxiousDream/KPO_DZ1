package service;

import com.google.inject.Inject;
import domain.EnergyConsumer;
import domain.InventoryItem;
import domain.transport.Transport;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class FleetReportService {
    private final FleetRepository repository;

    @Inject
    public FleetReportService(FleetRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public int countUnits() {
        return repository.size();
    }

    public double totalDailyEnergyConsumptionKwh() {
        return repository.findAll().stream()
                .filter(EnergyConsumer.class::isInstance)
                .map(EnergyConsumer.class::cast)
                .mapToDouble(EnergyConsumer::getDailyEnergyConsumptionKwh)
                .sum();
    }

    public List<InventoryItem> beginnerFriendlyDevices() {
        return repository.findAll().stream()
                .filter(item -> item instanceof Transport transport && transport.isSuitableForBeginners())
                .sorted(Comparator.comparing(InventoryItem::getInventoryNumber))
                .toList();
    }

    public List<InventoryItem> allItems() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(InventoryItem::getInventoryNumber))
                .toList();
    }
}