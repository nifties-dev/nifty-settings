package dev.nifties.settings;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import dev.nifties.settings.annotation.Setting;

public class QuickletSettingsApplicationTests {

    private SimpleSettingsService settingsService = new SimpleSettingsService();
    private SettingsManager settingsManager = new SettingsManager(
            new SettingsAnalyzer(), settingsService);

    public static class MyService {
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
        settingsService.put(MyService.class.getName() + ".enabled",
                Boolean.TRUE);

        MyService myService = new MyService();
        settingsManager.inject(myService);

        assertTrue(myService.isEnabled());
    }
}
