package dev.nifties.settings;

import java.io.Closeable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class MultiSourceSettingsService implements SettingsService, Consumer<String>, Closeable {
    private final List<SettingsSource> settingsSources;

    private final ConcurrentHashMap<String, Collection<SettingsListener>> listeners =
            new ConcurrentHashMap<>();

    public MultiSourceSettingsService(List<SettingsSource> settingsSources) {
        this.settingsSources = settingsSources;
        settingsSources.stream()
                .filter(o -> SettingsChannel.class.isAssignableFrom(o.getClass()))
                .map(SettingsChannel.class::cast)
                .forEach(s -> s.subscribe(this));
    }

    public List<SettingsSource> getSettingsSources() {
        return settingsSources;
    }

    @Override
    public SettingValue get(String key) {
        for (SettingsSource settingsSource : settingsSources) {
            SettingValue settingValue = settingsSource.get(key);
            if (settingValue != null) {
                return settingValue;
            }
        }
        return null;
    }

    @Override
    public void addListener(String key, SettingsListener listener) {
        listeners.computeIfAbsent(key, k -> new ConcurrentLinkedQueue<>()).add(listener);
    }

    @Override
    public void removeListener(SettingsListener listener) {
        listeners.values().forEach(c -> c.remove(listener));
    }

    @Override
    public void accept(String key) {
        notifyListeners(key, get(key));
    }

    @Override
    public void close() {
        settingsSources.stream()
                .filter(o -> SettingsChannel.class.isAssignableFrom(o.getClass()))
                .map(SettingsChannel.class::cast)
                .forEach(s -> s.unsubscribe(this));
    }

    protected void notifyListeners(String key, SettingValue settingValue) {
        listeners.getOrDefault(key, Collections.emptyList()).forEach(c -> c.onChange(settingValue));
    }
}
