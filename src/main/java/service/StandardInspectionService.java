package service;

import domain.BeginnerFriendly;
import domain.EnergyConsumer;
import domain.InventoryItem;

import java.util.regex.Pattern;


public class StandardInspectionService implements InspectionService {
    private static final Pattern INVENTORY_NUMBER_PATTERN =
            Pattern.compile("^[A-Z]{2,10}-\\d{3,6}$");

    @Override
    public InspectionResult inspect(InventoryItem item) {
        if (item == null) {
            return InspectionResult.reject("Устройство не передано на техосмотр");
        }

        String inventoryNumber = item.getInventoryNumber();

        if (inventoryNumber == null || inventoryNumber.isBlank()) {
            return InspectionResult.reject("Инвентарный номер отсутствует");
        }

        if (!INVENTORY_NUMBER_PATTERN.matcher(inventoryNumber).matches()) {
            return InspectionResult.reject(
                    "Неверный формат инвентарного номера: " + inventoryNumber
                            + ". Ожидается формат вида SC-1001"
            );
        }

        if (item instanceof EnergyConsumer energyConsumer
                && energyConsumer.getDailyEnergyConsumptionKwh() < 0) {
            return InspectionResult.reject("Суточное потребление энергии не может быть отрицательным");
        }

        if (item instanceof BeginnerFriendly beginnerFriendly) {
            int score = beginnerFriendly.getBeginnerFriendliness();

            if (score < 1 || score > 10) {
                return InspectionResult.reject("Оценка простоты для новичка должна быть от 1 до 10");
            }
        }

        return InspectionResult.accept();
    }
}
