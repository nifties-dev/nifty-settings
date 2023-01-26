package dev.nifties.settings;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class SettingAccessor<O> {
    private final String name;
    private final Function<O, Object> getter;
    private final BiConsumer<O, Object> setter;

    public SettingAccessor(String name, Function<O, Object> getter,
            BiConsumer<O, Object> setter) {
        this.name = name;
        this.getter = getter;
        this.setter = setter;
    }

    public Function<O, Object> getGetter() {
        return getter;
    }

    public String getName() {
        return name;
    }

    public BiConsumer<O, Object> getSetter() {
        return setter;
    }
}