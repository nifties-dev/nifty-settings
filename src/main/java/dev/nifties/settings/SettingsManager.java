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
        SettingValue<Object> settingValue = service.get(mapping.getName());
        if (settingValue != null) {
            mapping.getSetter().accept(object, settingValue.getValue());
        }
    }

    public void bind(Object object) {
        Collection<SettingAccessor> mappings = analyzer.get(object.getClass());
        Collection<Consumer<SettingValue<Object>>> appliers = binder == null ? null : new ArrayList<>(mappings.size());
        for (SettingAccessor mapping : mappings) {
            Object defaultValue = mapping.getGetter().apply(object);
            Consumer<SettingValue<Object>> applier =
                    v -> mapping.getSetter().accept(object, v != null ? v.getValue() : defaultValue);
            SettingValue settingValue = service.get(mapping.getName());
            applier.accept(settingValue);
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
        Collection<Consumer<SettingValue<Object>>> listeners = binder.remove(object);
        if (listeners != null) {
            listeners.forEach(service::removeListener);
            listeners.forEach(l -> l.accept(null)); // restore to original value
        }
    }
}
