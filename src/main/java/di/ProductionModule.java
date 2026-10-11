package di;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import service.FleetRepository;
import service.InMemoryFleetRepository;
import service.InspectionService;
import service.StandardInspectionService;

public class ProductionModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(FleetRepository.class)
                .to(InMemoryFleetRepository.class)
                .in(Singleton.class);

        bind(InspectionService.class)
                .to(StandardInspectionService.class)
                .in(Singleton.class);
    }
}