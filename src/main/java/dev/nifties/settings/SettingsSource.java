package dev.nifties.settings;

import java.io.Serializable;

public interface SettingsSource {

    SettingContainer<Object> get(String key);
}