package dev.nifties.settings;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class SimpleSettingsHolder implements SettingsSource, SettingsStream {
    private final Map<String, SettingValue<Object>> values = new ConcurrentHashMap<>();

    private final Set<Consumer<String>> subscribers = ConcurrentHashMap.newKeySet();

    @Override
    public SettingValue<Object> get(String key) {
        return values.get(key);
    }

    public void put(String key, Object value) {
        values.put(key, new SettingValue<>(value));
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
