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

    /**
     * @throws RuntimeException if any of subscribers fails to accept the signal, exception is thrown. There is no
     *                          special handling in {@link SettingsChannelBase} class, but subclasses might want to do
     *                          some logging or maybe propagate error back to the system where the change originated
     *                          from.
     */
    protected void notifySubscribers(final String key) {
        this.subscribers.forEach(s -> s.accept(key));
    }
}
