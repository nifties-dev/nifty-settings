package dev.nifties.settings;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class SimpleSettingsHolder implements SettingsSource, SettingsStream {
    private final Map<String, Object> values = new ConcurrentHashMap<>();

    private final Set<Consumer<String>> subscribers = ConcurrentHashMap.newKeySet();

    @Override
    public boolean containsKey(String key) {
        return values.containsKey(key);
    }

    public <T> T get(String key, T defaultValue) {
        return (T) values.getOrDefault(key, defaultValue);
    }

    public void put(String key, Object value) {
        values.put(key, value);
        notifyListeners(key);
    }

    public void remove(String key) {
        values.remove(key);
        notifyListeners(key);
    }

    public void subscribe(Consumer<String> subscriber) {
        subscribers.add(subscriber);
    }

    public void unsubscribe(Consumer<String> subscriber) {
        subscribers.remove(subscriber);
    }

    protected void notifyListeners(String key) {
        subscribers.forEach(s -> s.accept(key));
    }
}
