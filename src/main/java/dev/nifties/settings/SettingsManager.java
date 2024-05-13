package dev.nifties.settings;

import lombok.Builder;
import lombok.Singular;

import java.util.*;

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
