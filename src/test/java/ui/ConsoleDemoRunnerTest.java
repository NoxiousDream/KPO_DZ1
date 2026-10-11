package ui;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;
import di.ProductionModule;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleDemoRunnerTest {

    @Test
    void demoScenarioShowsAcceptanceRejectionAndReports() {
        Injector injector = Guice.createInjector(new ProductionModule());
        ConsoleDemoRunner runner = injector.getInstance(ConsoleDemoRunner.class);

        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try {
            System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));
            runner.run();
        } finally {
            System.setOut(originalOut);
        }

        String output = captured.toString(StandardCharsets.UTF_8);

        assertTrue(output.contains("Всего единиц на балансе: 5"));
        assertTrue(output.contains("[ОТКАЗ]"));
        assertTrue(output.contains("Прокатная техника, доступная новичкам"));
    }
}