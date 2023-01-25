package dev.nifties.settings;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class SettingAccessor<O, F> {
    private final String name;
    private final Function<O, F> getter;
    private final BiConsumer<O, F> setter;

    public SettingAccessor(String name, Function<O, F> getter,
            BiConsumer<O, F> setter) {
        this.name = name;
        this.getter = getter;
        this.setter = setter;
    }

    public Function<O, F> getGetter() {
        return getter;
    }

    public String getName() {
        return name;
    }

    public BiConsumer<O, F> getSetter() {
        return setter;
    }
}