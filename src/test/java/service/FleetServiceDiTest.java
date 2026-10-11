package service;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import org.junit.jupiter.api.Test;
import domain.transport.KickScooter;
import testdoubles.RejectAllInspectionStub;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class FleetServiceDiTest {

    private static class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(FleetRepository.class)
                    .to(InMemoryFleetRepository.class)
                    .in(Singleton.class);

            bind(InspectionService.class)
                    .toInstance(new RejectAllInspectionStub());
        }
    }

    @Test
    void replacesInspectionServiceThroughDiContainer() {
        Injector injector = Guice.createInjector(new TestModule());

        FleetService fleetService = injector.getInstance(FleetService.class);
        FleetRepository repository = injector.getInstance(FleetRepository.class);

        var result = fleetService.acceptToPark(
                new KickScooter("Самокат", "SC-1001", 8, 1.0)
        );

        assertFalse(result.accepted());
        assertEquals(0, repository.size());
    }
}