package dev.nifties.settings;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SettingsServiceFactoryTest {

    @Test
    public void simpleSettingsServiceProducedByDefault() {
        final SettingsService settingsService = SettingsServiceFactory.getInstance();
        assertNotNull(settingsService);
        assertEquals(SimpleSettingsService.class, settingsService.getClass());
    }
}
