package dev.nifties.settings;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SimpleSettingsService implements SettingsService {
    private final Map<String, SettingValue> values = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Collection<SettingsListener>> listeners =
            new ConcurrentHashMap<>();
    @Override
    public SettingValue get(String key) {
        return values.get(key);
    }

    public void put(String key, Object value) {
        SettingValue settingValue = new SettingValue(value);
        values.put(key, settingValue);
        notifyListeners(key, settingValue);
    }

    public void remove(String key) {
        values.remove(key);
        notifyListeners(key, null);
    }

    @Override
    public void addListener(String key, SettingsListener listener) {
        listeners.computeIfAbsent(key, k -> new ConcurrentLinkedQueue<>()).add(listener);
    }

    @Override
    public void removeListener(SettingsListener listener) {
        listeners.values().forEach(c -> c.remove(listener));
    }

    protected void notifyListeners(String key, SettingValue settingValue) {
        listeners.getOrDefault(key, Collections.emptyList()).forEach(c -> c.onChange(settingValue));;
    }
}
