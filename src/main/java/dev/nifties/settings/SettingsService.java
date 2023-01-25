package dev.nifties.settings;

public interface SettingsService {

    <T> T get(String key, T defaultValue);
}