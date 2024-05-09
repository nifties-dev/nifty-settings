package dev.nifties.settings;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public abstract class SettingsChannelBase implements SettingsChannel {

    private final Set<Consumer<String>> subscribers = ConcurrentHashMap.newKeySet();

    @Override
    public void subscribe(Consumer<String> consumer) {
        subscribers.add(consumer);
    }

    @Override
    public void unsubscribe(Consumer<String> consumer) {
        subscribers.remove(consumer);
    }

    protected void notifySubscribers(final String key) {
        this.subscribers.forEach(s -> s.accept(key));
    }
}
