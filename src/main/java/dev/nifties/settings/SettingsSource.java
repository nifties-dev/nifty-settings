package dev.nifties.settings;

public interface SettingsSource {

    SettingValue get(String key);
}