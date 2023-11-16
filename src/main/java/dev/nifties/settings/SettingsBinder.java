package dev.nifties.settings;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class SettingsBinder {

    private final Map<Object, Collection<Consumer<SettingValue>>> bindings = new ConcurrentHashMap<>();

    public void add(Object object, Collection<Consumer<SettingValue>> listeners) {
        bindings.put(object, listeners);
    }

    public Collection<Consumer<SettingValue>> remove(Object object) {
        return bindings.remove(object);
    }
}
