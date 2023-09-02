package dev.nifties.settings;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class SimpleSettingsService implements SettingsService {
    private final ConcurrentHashMap<String, Object> values = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, Collection<Consumer<? super Object>>> listeners = new ConcurrentHashMap<>();

    @Override
    public <T> T get(String key, T defaultValue) {
        return (T) values.getOrDefault(key, defaultValue);
    }

    public void put(String key, Object value) {
        values.put(key, value);
        notifyListeners(key, value);
    }

    public void remove(String key) {
        values.remove(key);
        notifyListeners(key, null);
    }

    @Override
    public void addListener(String key, Consumer<Object> listener) {
        listeners.computeIfAbsent(key, k -> new ConcurrentLinkedQueue<>()).add(listener);
    }

    @Override
    public void removeListener(Consumer<Object> listener) {
        listeners.values().stream().forEach(c -> c.remove(listener));
    }

    protected void notifyListeners(String key, Object value) {
        listeners.getOrDefault(key, Collections.emptyList()).forEach(c -> c.accept(value));
    }
}
