package dev.nifties.settings;

public interface SettingsService {

    <T> T get(String key, T defaultValue);

    default <T> T get(String name) {
        return get(name, (T) null);
    }
}