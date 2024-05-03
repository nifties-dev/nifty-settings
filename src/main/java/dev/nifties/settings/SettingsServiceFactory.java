package dev.nifties.settings;

import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class SettingsServiceFactory {
    public static SettingsService getInstance() {
        ServiceLoader<SettingsSource> settingsSources = ServiceLoader.load(SettingsSource.class);
        List<SettingsSource> sources = settingsSources.stream()
                .filter(p -> !p.type().getPackage().equals(SettingsServiceFactory.class.getPackage()))
                .map(ServiceLoader.Provider::get)
                .collect(Collectors.toList());
        if (sources.isEmpty()) {
            return new SimpleSettingsService();
        } else {
            return new MultiSourceSettingsService(sources);
        }
    }
}
