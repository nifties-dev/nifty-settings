package dev.nifties.settings;

import java.util.concurrent.ConcurrentHashMap;

public class SimpleSettingsService implements SettingsService {
    private final ConcurrentHashMap<String, Object> values = new ConcurrentHashMap<>();

    @Override
    public <T> T get(String key, T defaultValue) {
        return (T) values.getOrDefault(key, defaultValue);
    }

    public void put(String key, Object value) {
        values.put(key, value);
    }

    public void remove(String key) {
        values.remove(key);
    }
}
