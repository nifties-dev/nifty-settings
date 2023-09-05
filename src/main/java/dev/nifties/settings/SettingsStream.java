package dev.nifties.settings;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface SettingsStream extends SettingsSource {

    void subscribe(Consumer<String> listener);

    void unsubscribe(Consumer<String> listener);
}