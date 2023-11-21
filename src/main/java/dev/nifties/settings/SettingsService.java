package dev.nifties.settings;

public interface SettingsService extends SettingsSource {

    void addListener(String key, SettingsListener listener);

    void removeListener(SettingsListener listener);
}
