package dev.nifties.settings;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import dev.nifties.settings.annotation.Setting;

public class NiftySettingsUsecaseTests {

    private SimpleSettingsService settingsService = new SimpleSettingsService();
    private SettingsManager settingsManager = new SettingsManager(
            new SettingsAnalyzer(), settingsService);

    public static class MyService1 {
        @Setting
        private boolean enabled;

        public boolean isEnabled() {
            return enabled;
        }

        public void process(Collection<?> entries) {
            if (!enabled) {
                return;
            }
            // ...process...
        }
    }

    @Test
    public void simpleToggleService() {
        settingsService.put(MyService1.class.getName() + ".enabled",
                Boolean.TRUE);

        MyService1 myService = new MyService1();
        settingsManager.inject(myService);
        assertTrue(myService.isEnabled());

        myService = new MyService1();
        settingsService.remove(MyService1.class.getName() + ".enabled");
        settingsManager.inject(myService);
        assertFalse(myService.isEnabled());
    }
}
