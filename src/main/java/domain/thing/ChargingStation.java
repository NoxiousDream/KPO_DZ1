package domain.thing;

import domain.EnergyConsumer;

public class ChargingStation extends Thing implements EnergyConsumer {
    private final double dailyEnergyConsumptionKwh;

    public ChargingStation(String name, String inventoryNumber, double dailyEnergyConsumptionKwh) {
        super(name, inventoryNumber);

        if (dailyEnergyConsumptionKwh < 0) {
            throw new IllegalArgumentException("Суточное потребление энергии не может быть отрицательным");
        }

        this.dailyEnergyConsumptionKwh = dailyEnergyConsumptionKwh;
    }

    @Override
    public double getDailyEnergyConsumptionKwh() {
        return dailyEnergyConsumptionKwh;
    }
}