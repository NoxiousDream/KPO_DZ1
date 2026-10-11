package service;

import org.junit.jupiter.api.Test;
import domain.transport.Bicycle;
import domain.transport.KickScooter;
import testdoubles.AcceptAllInspectionStub;
import testdoubles.RejectAllInspectionStub;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FleetServiceTest {

    @Test
    void addsDeviceWhenInspectionAccepts() {
        var repository = new InMemoryFleetRepository();
        var service = new FleetService(repository, new AcceptAllInspectionStub());

        var scooter = new KickScooter("Самокат", "SC-1001", 7, 1.2);

        var result = service.acceptToPark(scooter);

        assertTrue(result.accepted());
        assertEquals(1, repository.size());
        assertSame(scooter, repository.findAll().get(0));
    }

    @Test
    void doesNotAddDeviceWhenInspectionRejects() {
        var repository = new InMemoryFleetRepository();
        var service = new FleetService(repository, new RejectAllInspectionStub());

        var bicycle = new Bicycle("Велосипед", "BI-1002", 8);

        var result = service.acceptToPark(bicycle);

        assertFalse(result.accepted());
        assertEquals(0, repository.size());
    }

    @Test
    void rejectsDuplicateInventoryNumberEvenIfInspectionAccepts() {
        var repository = new InMemoryFleetRepository();
        var service = new FleetService(repository, new AcceptAllInspectionStub());

        service.acceptToPark(new Bicycle("Велосипед 1", "BI-1002", 8));

        var result = service.acceptToPark(new Bicycle("Велосипед 2", "BI-1002", 9));

        assertFalse(result.accepted());
        assertEquals(1, repository.size());
    }
}