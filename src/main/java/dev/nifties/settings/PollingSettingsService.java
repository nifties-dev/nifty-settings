package dev.nifties.settings;

import java.util.concurrent.ConcurrentHashMap;

public class PollingSettingsService<I> implements SettingsService {
    private final ConcurrentHashMap<String, Object> values = new ConcurrentHashMap<>();

    private final SettingsRepository<I> repository;
    private volatile I lastIdentifier;

    public PollingSettingsService(SettingsRepository<I> repository) {
        this.repository = repository;
    }

    @Override
    public <T> T get(String key, T defaultValue) {
        return (T) values.getOrDefault(key, defaultValue);
    }

    public synchronized void update() {
        repository.fetch(lastIdentifier, this::add);
    }

    public synchronized void add(SettingsRepository.SettingsUpdate<I> update) {
        values.put(update.getKey(), update.getValue());
        lastIdentifier = update.getIdentifier();
    }
}
