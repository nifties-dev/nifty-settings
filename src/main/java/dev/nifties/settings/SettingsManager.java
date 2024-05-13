package dev.nifties.settings;

import lombok.Builder;
import lombok.Singular;

import java.util.*;

/**
 * Entry point for the client applications, acts like a Facade for the underlying components. Provides inject methods
 * for applying settings to an object once (suitable for short-lived objects), bind methods for creating a link between
 * settings service and the bound object, so it would be updated with settings continuously (designed for long-living
 * objects like singletons) and, optionally, unbind methods, which will only work if SettingsManager was set up with the
 * {@link SettingsBinder} (this comes at a cost of having explicit references to all bound objects - may be unnecessary
 * if you generally keep them until all the program is shutdown anyway).
 */
public class SettingsManager {

    private final SettingsAnalyzer analyzer;
    private final SettingsBinder binder;
    private final SettingsService service;

    @Builder
    protected SettingsManager(SettingsAnalyzer analyzer, SettingsBinder binder, boolean noBinder, SettingsService service,
                           @Singular List<SettingsSource> sources) {
        this.analyzer = analyzer != null ? analyzer : new SettingsAnalyzer();
        this.binder = binder != null || noBinder ? binder : new SettingsBinder();
        if (service != null) {
            if (sources != null && !sources.isEmpty()) {
                throw new IllegalArgumentException("service and sources are mutually exclusive");
            }
            this.service = service;
        } else {
            if (sources == null || sources.isEmpty()) {
                this.service = new SimpleSettingsService();
            } else {
                this.service = new MultiSourceSettingsService(sources);
            }
        }
    }

    public SettingsAnalyzer getAnalyzer() {
        return analyzer;
    }

    public SettingsBinder getBinder() {
        return binder;
    }

    public SettingsService getService() {
        return service;
    }

    public <O> void inject(O object) {
        inject(object.getClass().getName(), object);
    }

    public <O> void inject(String objectName, O object) {
        Collection<SettingAccessor> mappings = analyzer.get(object.getClass());
        mappings.forEach(m -> this.apply(objectName, object, m));
    }

    protected void apply(String objectName, Object object, SettingAccessor mapping) {
        SettingValue settingValue = service.get(objectName + '.' + mapping.getName());
        if (settingValue != null) {
            mapping.getSetter().accept(object, settingValue.getValue());
        }
    }

    public void bind(Object object) {
        bind(object.getClass().getName(), object);
    }

    public void bind(String objectName, Object object) {
        Collection<SettingAccessor> mappings = analyzer.get(object.getClass());
        Collection<SettingsListener> listeners = binder == null ? null : new ArrayList<>(mappings.size());
        for (SettingAccessor mapping : mappings) {
            String key = objectName + '.' + mapping.getName();
            Object defaultValue = mapping.getGetter().apply(object);
            SettingsListener listener =
                    v -> mapping.getSetter().accept(object, v != null ? v.getValue() : defaultValue);
            SettingValue settingValue = service.get(key);
            listener.onChange(settingValue);
            service.addListener(key, listener);
            if (binder != null) {
                listeners.add(listener);
            }
        }
        if (binder != null) {
            binder.add(object, listeners);
        }
    }

    public void unbind(Object object) {
        if (binder == null) {
            throw new UnsupportedOperationException("Unbind operation requires SettingsBinder to be set up");
        }
        Collection<SettingsListener> listeners = binder.remove(object);
        if (listeners != null) {
            listeners.forEach(service::removeListener);
            listeners.forEach(l -> l.onChange(null)); // restore to original value
        }
    }
}
