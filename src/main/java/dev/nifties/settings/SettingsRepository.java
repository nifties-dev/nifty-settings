package dev.nifties.settings;

import java.util.function.Consumer;

public interface SettingsRepository<I> {

    void fetch(I lastUpdateIdentifier, Consumer<SettingsUpdate<I>> consumer);

    interface SettingsUpdate<I> {
        I getIdentifier();

        String getKey();

        Object getValue();
    }
}
