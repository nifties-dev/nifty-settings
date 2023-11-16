package dev.nifties.settings;

public interface SettingsSource {

    SettingValue<Object> get(String key);
}