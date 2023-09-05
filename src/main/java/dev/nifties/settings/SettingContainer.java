package dev.nifties.settings;

import lombok.Data;

import java.io.Serializable;

@Data
public class SettingContainer<T> implements Serializable {
    private final T value;

    @Override
    public String toString() {
        return '[' + String.valueOf(value) + ']';
    }
}
