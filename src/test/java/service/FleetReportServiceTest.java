package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import domain.thing.ChargingStation;
import domain.thing.Helmet;
import domain.transport.Bicycle;
import domain.transport.ElectricBicycle;
import domain.transport.KickScooter;
import testdoubles.AcceptAllInspectionStub;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FleetReportServiceTest {
    private FleetRepository repository;
    private FleetService fleetService;
    private FleetReportService reportService;

    @BeforeEach
    void setUp() {
        repository = new InMemoryFleetRepository();
        fleetService = new FleetService(repository, new AcceptAllInspectionStub());
        reportService = new FleetReportService(repository);
    }

    @Test
    void calculatesTotalEnergyOnlyForEnergyConsumers() {
        fleetService.acceptToPark(new KickScooter("Самокат", "SC-1001", 7, 1.25));
        fleetService.acceptToPark(new ElectricBicycle("Электровелосипед", "EB-1002", 5, 2.25));
        fleetService.acceptToPark(new Bicycle("Велосипед", "BI-1003", 9));
        fleetService.acceptToPark(new Helmet("Шлем", "HM-1004"));
        fleetService.acceptToPark(new ChargingStation("Зарядная станция", "CH-1005", 4.5));

        assertEquals(8.0, reportService.totalDailyEnergyConsumptionKwh(), 1e-9);
        assertEquals(5, reportService.countUnits());
    }

    @Test
    void returnsOnlyTransportSuitableForBeginners() {
        fleetService.acceptToPark(new KickScooter("Самокат", "SC-1001", 8, 1.0));
        fleetService.acceptToPark(new ElectricBicycle("Электровелосипед", "EB-1002", 5, 2.0));
        fleetService.acceptToPark(new Bicycle("Велосипед", "BI-1003", 4));
        fleetService.acceptToPark(new Helmet("Шлем", "HM-1004"));

        var beginnerDevices = reportService.beginnerFriendlyDevices();

        assertEquals(1, beginnerDevices.size());
        assertEquals("SC-1001", beginnerDevices.get(0).getInventoryNumber());
    }
}