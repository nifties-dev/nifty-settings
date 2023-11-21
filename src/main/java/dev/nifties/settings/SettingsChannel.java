package dev.nifties.settings;

import java.util.function.Consumer;

public interface SettingsChannel extends SettingsSource {

    void subscribe(Consumer<String> listener);

    void unsubscribe(Consumer<String> listener);
}