package dev.nifties.settings;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Holds references to objects bound using {@link SettingsManager}'s bind methods to support its unbind methods.
 */
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
