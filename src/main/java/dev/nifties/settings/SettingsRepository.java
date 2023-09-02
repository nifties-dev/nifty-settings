package dev.nifties.settings;

import java.util.function.Consumer;

public interface SettingsRepository<I> {

    void fetch(I lastUpdateIdentifier, Consumer<SettingsUpdate<I>> consumer);

    interface SettingsUpdate<I> {
        I getIdentifier();

        Change getOperation();

        String getKey();

        Object getValue();
    }

    public enum Change {
        UPDATE, DELETE
    }
}
