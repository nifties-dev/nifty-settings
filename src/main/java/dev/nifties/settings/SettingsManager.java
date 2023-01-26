package dev.nifties.settings;

import java.util.Collection;

public class SettingsManager {

    private final SettingsAnalyzer analyzer;
    private final SettingsService service;

    public SettingsManager(SettingsAnalyzer analyzer, SettingsService service) {
        this.analyzer = analyzer;
        this.service = service;
    }

    public <O> void inject(O object) {
        Collection<SettingAccessor<O>> mappings = analyzer
                .get((Class<O>) object.getClass());
        mappings.forEach(m -> this.apply(m, object));
    }

    protected <O> void apply(SettingAccessor<O> mapping, O object) {
        Object defaultValue = mapping.getGetter().apply(object);
        Object value = service.get(mapping.getName(), defaultValue);
        mapping.getSetter().accept(object, value);
    }

    public void bind(Object object) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void unbind(Object object) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
