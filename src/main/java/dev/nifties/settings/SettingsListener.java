package dev.nifties.settings;

public interface SettingsListener<T> {

    void onChange(T value);
}
