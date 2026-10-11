package domain;

import org.junit.jupiter.api.Test;
import domain.transport.Bicycle;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BeginnerRuleTest {

    @Test
    void scoreSixIsSuitableForBeginners() {
        var bicycle = new Bicycle("Велосипед", "BI-1001", 6);

        assertTrue(bicycle.isSuitableForBeginners());
    }

    @Test
    void scoreFiveIsNotSuitableForBeginners() {
        var bicycle = new Bicycle("Велосипед", "BI-1002", 5);

        assertFalse(bicycle.isSuitableForBeginners());
    }

    @Test
    void invalidScoreThrowsException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Bicycle("Велосипед", "BI-1003", 11)
        );
    }
}