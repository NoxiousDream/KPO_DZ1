package ui;

import com.google.inject.Inject;
import domain.EnergyConsumer;
import domain.InventoryItem;
import domain.thing.ChargingStation;
import domain.thing.Helmet;
import domain.thing.Thing;
import domain.transport.Bicycle;
import domain.transport.ElectricBicycle;
import domain.transport.KickScooter;
import domain.transport.Transport;
import service.FleetReportService;
import service.FleetService;
import service.InspectionResult;

public class ConsoleDemoRunner {
    private final FleetService fleetService;
    private final FleetReportService reportService;

    @Inject
    public ConsoleDemoRunner(FleetService fleetService, FleetReportService reportService) {
        this.fleetService = fleetService;
        this.reportService = reportService;
    }

    public void run() {
        System.out.println("=== ВышКат: приём техники и вещей ===");

        accept(new KickScooter("Самокат ВышКат S1", "SC-1001", 8, 1.2));
        accept(new Bicycle("Велосипед ВышКат City", "BI-1002", 9));
        accept(new ElectricBicycle("Электровелосипед ВышКат E1", "EB-1003", 5, 2.5));
        accept(new Helmet("Шлем ВышКат", "HM-2001"));
        accept(new ChargingStation("Зарядная станция ВышКат", "CH-3001", 7.5));

        accept(new KickScooter("Самокат с некорректным инвентарным номером", "BAD-01", 7, 1.0));

        printReports();
    }

    private void accept(InventoryItem item) {
        InspectionResult result = fleetService.acceptToPark(item);
        String status = result.accepted() ? "ПРИНЯТО" : "ОТКАЗ";

        System.out.printf("[%s] %s (%s)%n", status, item.getName(), item.getInventoryNumber());

        if (!result.accepted()) {
            System.out.println("    Причина: " + result.reason());
        }
    }

    private void printReports() {
        System.out.println();
        System.out.println("=== Отчёты парка ===");
        System.out.println("Всего единиц на балансе: " + reportService.countUnits());
        System.out.printf(
                "Суммарное суточное энергопотребление: %.2f кВт·ч%n",
                reportService.totalDailyEnergyConsumptionKwh()
        );

        System.out.println();
        System.out.println("Прокатная техника, доступная новичкам (простота >= 6):");

        var beginnerDevices = reportService.beginnerFriendlyDevices();

        if (beginnerDevices.isEmpty()) {
            System.out.println("Нет подходящей техники.");
        } else {
            for (var item : beginnerDevices) {
                if (item instanceof Transport transport) {
                    System.out.printf(
                            " - %s (%s), простота = %d%n",
                            item.getName(),
                            item.getInventoryNumber(),
                            transport.getBeginnerFriendliness()
                    );
                }
            }
        }

        System.out.println();
        System.out.println("Все предметы на балансе:");

        for (var item : reportService.allItems()) {
            String type = "Объект";

            if (item instanceof Transport) {
                type = "Транспорт";
            } else if (item instanceof Thing) {
                type = "Вещь";
            }

            String energyInfo = "";

            if (item instanceof EnergyConsumer energyConsumer) {
                energyInfo = String.format(", %.2f кВт·ч/сут", energyConsumer.getDailyEnergyConsumptionKwh());
            }

            System.out.printf(
                    " - [%s] %s (%s)%s%n",
                    type,
                    item.getName(),
                    item.getInventoryNumber(),
                    energyInfo
            );
        }
    }
}