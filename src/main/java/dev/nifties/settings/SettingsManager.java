package dev.nifties.settings;

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
        Object value = service.get(mapping.getName());
        if (value != null) {
            mapping.getSetter().accept(object, value);
        }
    }

    public void bind(Object object) {
        Collection<SettingAccessor> mappings = analyzer.get(object.getClass());
        Collection<Consumer<Object>> appliers = new ArrayList<>(mappings.size());
        for (SettingAccessor mapping : mappings) {
            Object defaultValue = mapping.getGetter().apply(object);
            Consumer<Object> applier = v -> mapping.getSetter().accept(object, v != null ? v : defaultValue);
            Object value = service.get(mapping.getName());
            applier.accept(value);
            service.addListener(mapping.getName(), applier);
        }
        if (binder != null) {
            binder.add(object, appliers);
        }
    }

    public void unbind(Object object) {
        if (binder == null) {
            throw new UnsupportedOperationException("Unbind operation requires SettingsBinder to be set up");
        }
        Collection<Consumer<Object>> listeners = binder.remove(object);
        if (listeners != null) {
            listeners.forEach(service::removeListener);
        }
    }
}
