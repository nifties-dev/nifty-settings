package dev.nifties.settings;

import java.util.function.Consumer;

public interface SettingsService {

    <T> T get(String key, T defaultValue);

    default <T> T get(String name) {
        return get(name, (T) null);
    }

    void addListener(String key, Consumer<Object> listener);

    void removeListener(Consumer<Object> listener);
}