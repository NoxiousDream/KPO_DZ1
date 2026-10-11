package domain.transport;

import domain.EnergyConsumer;

public class KickScooter extends Transport implements EnergyConsumer {
    private final double dailyEnergyConsumptionKwh;

    public KickScooter(
            String name,
            String inventoryNumber,
            int beginnerFriendliness,
            double dailyEnergyConsumptionKwh
    ) {
        super(name, inventoryNumber, beginnerFriendliness);

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