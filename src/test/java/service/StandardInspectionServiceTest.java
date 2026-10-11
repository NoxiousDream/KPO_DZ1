package service;

import org.junit.jupiter.api.Test;
import domain.transport.KickScooter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StandardInspectionServiceTest {
    private final StandardInspectionService service = new StandardInspectionService();

    @Test
    void acceptsValidDevice() {
        var result = service.inspect(new KickScooter("Самокат", "SC-1001", 8, 1.0));

        assertTrue(result.accepted());
    }

    @Test
    void rejectsInvalidInventoryNumberFormat() {
        var result = service.inspect(new KickScooter("Самокат", "BAD-01", 8, 1.0));

        assertFalse(result.accepted());
        assertTrue(result.reason().contains("формат"));
    }

    @Test
    void rejectsNull() {
        var result = service.inspect(null);

        assertFalse(result.accepted());
    }
}