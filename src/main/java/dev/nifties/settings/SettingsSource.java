package dev.nifties.settings;

public interface SettingsSource {

    boolean containsKey(String key);

    <T> T get(String key, T defaultValue);

    default <T> T get(String name) {
        return get(name, (T) null);
    }
}