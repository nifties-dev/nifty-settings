package dev.nifties.settings;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class SettingsBinder {

    private final Map<Object, Collection<Consumer<SettingContainer<Object>>>> bindings = new ConcurrentHashMap<>();

    public void add(Object object, Collection<Consumer<SettingContainer<Object>>> listeners) {
        bindings.put(object, listeners);
    }

    public Collection<Consumer<SettingContainer<Object>>> remove(Object object) {
        return bindings.remove(object);
    }
}
