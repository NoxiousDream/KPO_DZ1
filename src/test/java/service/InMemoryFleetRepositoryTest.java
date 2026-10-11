package service;

import org.junit.jupiter.api.Test;
import domain.transport.Bicycle;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryFleetRepositoryTest {

    @Test
    void storesItemsAndReturnsCopy() {
        var repository = new InMemoryFleetRepository();
        var bicycle = new Bicycle("Велосипед", "BI-1001", 8);

        repository.add(bicycle);

        assertEquals(1, repository.size());
        assertEquals(List.of(bicycle), repository.findAll());
    }

    @Test
    void rejectsDuplicateInventoryNumber() {
        var repository = new InMemoryFleetRepository();

        repository.add(new Bicycle("Велосипед 1", "BI-1001", 8));

        assertThrows(
                IllegalArgumentException.class,
                () -> repository.add(new Bicycle("Велосипед 2", "BI-1001", 9))
        );
    }
}