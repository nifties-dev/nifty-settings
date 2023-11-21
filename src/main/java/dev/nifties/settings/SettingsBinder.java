package dev.nifties.settings;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class SettingsBinder {

    private final Map<Object, Collection<SettingsListener>> bindings =
            Collections.synchronizedMap(new IdentityHashMap<>());

    public void add(Object object, Collection<SettingsListener> listeners) {
        bindings.put(object, listeners);
    }

    public Collection<SettingsListener> remove(Object object) {
        return bindings.remove(object);
    }
}
