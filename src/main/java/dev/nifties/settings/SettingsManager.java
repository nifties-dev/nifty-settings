package dev.nifties.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

public class SettingsManager {

    private final SettingsAnalyzer analyzer;
    private final SettingsBinder binder;
    private final SettingsService service;

    public SettingsManager(SettingsAnalyzer analyzer, SettingsBinder binder, SettingsService service) {
        this.analyzer = analyzer;
        this.binder = binder;
        this.service = service;
    }

    public <O> void inject(O object) {
        Collection<SettingAccessor> mappings = analyzer.get(object.getClass());
        mappings.forEach(m -> this.apply(m, object));
    }

    protected void apply(SettingAccessor mapping, Object object) {
        SettingContainer<Object> settingContainer = service.get(mapping.getName());
        if (settingContainer != null) {
            mapping.getSetter().accept(object, settingContainer.getValue());
        }
    }

    public void bind(Object object) {
        Collection<SettingAccessor> mappings = analyzer.get(object.getClass());
        Collection<Consumer<SettingContainer<Object>>> appliers = binder == null ? null : new ArrayList<>(mappings.size());
        for (SettingAccessor mapping : mappings) {
            Object defaultValue = mapping.getGetter().apply(object);
            Consumer<SettingContainer<Object>> applier =
                    v -> mapping.getSetter().accept(object, v != null ? v.getValue() : defaultValue);
            SettingContainer settingContainer = service.get(mapping.getName());
            applier.accept(settingContainer);
            service.addListener(mapping.getName(), applier);
            if (binder != null) {
                appliers.add(applier);
            }
        }
        if (binder != null) {
            binder.add(object, appliers);
        }
    }

    public void unbind(Object object) {
        if (binder == null) {
            throw new UnsupportedOperationException("Unbind operation requires SettingsBinder to be set up");
        }
        Collection<Consumer<SettingContainer<Object>>> listeners = binder.remove(object);
        if (listeners != null) {
            listeners.forEach(service::removeListener);
            listeners.forEach(l -> l.accept(null)); // restore to original value
        }
    }
}
