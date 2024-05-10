package dev.nifties.settings;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class SettingsManagerBuilderTest {

    @Test
    public void defaultBuilder() {
        SettingsManager settingsManager = SettingsManager.builder().build();
        assertNotNull(settingsManager);
        assertNotNull(settingsManager.getAnalyzer());
        assertEquals(SettingsAnalyzer.class, settingsManager.getAnalyzer().getClass());
        assertNotNull(settingsManager.getBinder());
        assertEquals(SettingsBinder.class, settingsManager.getBinder().getClass());
        assertNotNull(settingsManager.getService());
        assertEquals(SimpleSettingsService.class, settingsManager.getService().getClass());
    }

    @Test
    public void customAnalyzer() {
        SettingsAnalyzer customAnalyzer = Mockito.mock(SettingsAnalyzer.class);
        SettingsManager settingsManager = SettingsManager.builder().analyzer(customAnalyzer).build();
        assertNotNull(settingsManager);
        assertNotNull(settingsManager.getAnalyzer());
        assertEquals(customAnalyzer, settingsManager.getAnalyzer());
        assertNotNull(settingsManager.getBinder());
        assertEquals(SettingsBinder.class, settingsManager.getBinder().getClass());
        assertNotNull(settingsManager.getService());
        assertEquals(SimpleSettingsService.class, settingsManager.getService().getClass());
    }

    @Test
    public void customBinder() {
        SettingsBinder customBinder = Mockito.mock(SettingsBinder.class);
        SettingsManager settingsManager = SettingsManager.builder().binder(customBinder).build();
        assertNotNull(settingsManager);
        assertNotNull(settingsManager.getAnalyzer());
        assertEquals(SettingsAnalyzer.class, settingsManager.getAnalyzer().getClass());
        assertNotNull(settingsManager.getBinder());
        assertEquals(customBinder, settingsManager.getBinder());
        assertNotNull(settingsManager.getService());
        assertEquals(SimpleSettingsService.class, settingsManager.getService().getClass());
    }

    @Test
    public void noBinder() {
        SettingsManager settingsManager = SettingsManager.builder().noBinder(true).build();
        assertNotNull(settingsManager);
        assertNotNull(settingsManager.getAnalyzer());
        assertEquals(SettingsAnalyzer.class, settingsManager.getAnalyzer().getClass());
        assertNull(settingsManager.getBinder());
        assertNotNull(settingsManager.getService());
        assertEquals(SimpleSettingsService.class, settingsManager.getService().getClass());
    }

    @Test
    public void customService() {
        SimpleSettingsService customService = new SimpleSettingsService();
        SettingsManager settingsManager = SettingsManager.builder().service(customService).build();
        assertNotNull(settingsManager);
        assertNotNull(settingsManager.getAnalyzer());
        assertEquals(SettingsAnalyzer.class, settingsManager.getAnalyzer().getClass());
        assertNotNull(settingsManager.getBinder());
        assertEquals(SettingsBinder.class, settingsManager.getBinder().getClass());
        assertEquals(customService, settingsManager.getService());
    }

    @Test
    public void singleSource() {
        SettingsSource source = Mockito.mock(SettingsSource.class);
        SettingsManager settingsManager = SettingsManager.builder().source(source).build();
        assertNotNull(settingsManager);
        assertNotNull(settingsManager.getAnalyzer());
        assertEquals(SettingsAnalyzer.class, settingsManager.getAnalyzer().getClass());
        assertNotNull(settingsManager.getBinder());
        assertEquals(SettingsBinder.class, settingsManager.getBinder().getClass());
        assertNotNull(settingsManager.getService());
        assertEquals(MultiSourceSettingsService.class, settingsManager.getService().getClass());
        assertEquals(Arrays.asList(source), ((MultiSourceSettingsService)settingsManager.getService()).getSettingsSources());
    }

    @Test
    public void multiSource() {
        SettingsSource source1 = Mockito.mock(SettingsSource.class);
        SettingsSource source2 = Mockito.mock(SettingsSource.class);
        SettingsSource source3 = Mockito.mock(SettingsSource.class);
        SettingsManager settingsManager = SettingsManager.builder()
                .source(source1).source(source2).source(source3)
                .build();
        assertNotNull(settingsManager);
        assertNotNull(settingsManager.getAnalyzer());
        assertEquals(SettingsAnalyzer.class, settingsManager.getAnalyzer().getClass());
        assertNotNull(settingsManager.getBinder());
        assertEquals(SettingsBinder.class, settingsManager.getBinder().getClass());
        assertNotNull(settingsManager.getService());
        assertEquals(MultiSourceSettingsService.class, settingsManager.getService().getClass());
        assertEquals(Arrays.asList(source1, source2, source3),
                ((MultiSourceSettingsService)settingsManager.getService()).getSettingsSources());
    }

    @Test
    public void noBinder() {
        SettingsManager settingsManager = SettingsManager.builder().noBinder(true).build();
        assertNotNull(settingsManager);
        assertNotNull(settingsManager.getAnalyzer());
        assertEquals(SettingsAnalyzer.class, settingsManager.getAnalyzer().getClass());
        assertNull(settingsManager.getBinder());
        assertNotNull(settingsManager.getService());
        assertEquals(SimpleSettingsService.class, settingsManager.getService().getClass());
    }

    @Test
    public void serviceAndSourceConflict() {
        SettingsSource source = Mockito.mock(SettingsSource.class);
        SettingsManager.SettingsManagerBuilder builder =
                SettingsManager.builder().service(new SimpleSettingsService()).source(source);
        assertThrows(IllegalArgumentException.class, () -> builder.build());
    }
}
