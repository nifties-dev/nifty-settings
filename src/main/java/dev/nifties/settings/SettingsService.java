package dev.nifties.settings;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class SettingsService implements Consumer<String>, Closeable {
    private final List<SettingsSource> settingsSources;

    private final ConcurrentHashMap<String, Collection<Consumer<SettingContainer<Object>>>> listeners =
            new ConcurrentHashMap<>();

    public SettingsService(List<SettingsSource> settingsSources) {
        this.settingsSources = settingsSources;
        settingsSources.stream()
                .filter(o -> SettingsStream.class.isAssignableFrom(o.getClass()))
                .map(SettingsStream.class::cast)
                .forEach(s -> s.subscribe(this));
    }

    public SettingContainer<Object> get(String key) {
        for (SettingsSource settingsSource : settingsSources) {
            SettingContainer<Object> settingContainer = settingsSource.get(key);
            if (settingContainer != null) {
                return settingContainer;
            }
        }
        return null;
    }

    public void addListener(String key, Consumer<SettingContainer<Object>> listener) {
        listeners.computeIfAbsent(key, k -> new ConcurrentLinkedQueue<>()).add(listener);
    }

    public void removeListener(Consumer<SettingContainer<Object>> listener) {
        listeners.values().stream().forEach(c -> c.remove(listener));
    }

    @Override
    public void accept(String key) {
        notifyListeners(key, get(key));
    }

    @Override
    public void close() throws IOException {
        settingsSources.stream()
                .filter(o -> SettingsStream.class.isAssignableFrom(o.getClass()))
                .map(SettingsStream.class::cast)
                .forEach(s -> s.unsubscribe(this));
    }

    protected void notifyListeners(String key, SettingContainer<Object> settingContainer) {
        listeners.getOrDefault(key, Collections.emptyList()).forEach(c -> c.accept(settingContainer));
    }
}
