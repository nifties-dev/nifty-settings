package dev.nifties.settings;

import java.util.ArrayList;
import java.util.Collection;

public class SettingsManager {

    private final SettingsAnalyzer analyzer;
    private final SettingsBinder binder;
    private final SettingsService service;

    public SettingsManager(SettingsAnalyzer analyzer, SettingsBinder binder) {
        this(analyzer, binder, SettingsServiceFactory.getInstance());
    }

    public SettingsManager(SettingsAnalyzer analyzer, SettingsBinder binder, SettingsService service) {
        this.analyzer = analyzer;
        this.binder = binder;
        this.service = service;
    }

    public SettingsService getService() {
        return service;
    }

    public <O> void inject(O object) {
        Collection<SettingAccessor> mappings = analyzer.get(object.getClass());
        mappings.forEach(m -> this.apply(m, object));
    }

    protected void apply(SettingAccessor mapping, Object object) {
        SettingValue settingValue = service.get(mapping.getName());
        if (settingValue != null) {
            mapping.getSetter().accept(object, settingValue.getValue());
        }
    }

    public void bind(Object object) {
        Collection<SettingAccessor> mappings = analyzer.get(object.getClass());
        Collection<SettingsListener> listeners = binder == null ? null : new ArrayList<>(mappings.size());
        for (SettingAccessor mapping : mappings) {
            Object defaultValue = mapping.getGetter().apply(object);
            SettingsListener listener =
                    v -> mapping.getSetter().accept(object, v != null ? v.getValue() : defaultValue);
            SettingValue settingValue = service.get(mapping.getName());
            listener.onChange(settingValue);
            service.addListener(mapping.getName(), listener);
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
