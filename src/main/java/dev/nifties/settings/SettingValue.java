package dev.nifties.settings;

import lombok.Data;

import java.io.Serializable;

@Data
public class SettingValue implements Serializable {
    private final Object value;

    @Override
    public String toString() {
        return '[' + String.valueOf(value) + ']';
    }
}
