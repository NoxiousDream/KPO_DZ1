import com.google.inject.Guice;
import com.google.inject.Injector;
import di.ProductionModule;
import ui.ConsoleDemoRunner;

public class Main {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new ProductionModule());
        ConsoleDemoRunner runner = injector.getInstance(ConsoleDemoRunner.class);
        runner.run();
    }
}