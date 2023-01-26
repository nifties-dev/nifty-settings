package dev.nifties.settings;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class SettingAccessor {
    private final String name;
    private final Function<Object, Object> getter;
    private final BiConsumer<Object, Object> setter;

    public SettingAccessor(String name, Function<Object, Object> getter,
            BiConsumer<Object, Object> setter) {
        this.name = name;
        this.getter = getter;
        this.setter = setter;
    }

    public Function<Object, Object> getGetter() {
        return getter;
    }

    public String getName() {
        return name;
    }

    public BiConsumer<Object, Object> getSetter() {
        return setter;
    }
}