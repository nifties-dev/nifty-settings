package dev.nifties.settings;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import dev.nifties.settings.annotation.Setting;

public class SettingsAnalyzer {

    public <O> Collection<SettingAccessor<O, ?>> get(Class<O> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .map(f -> (SettingAccessor<O, ?>) this.processField(clazz, f))
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    protected <O, F> SettingAccessor<O, F> processField(Class<O> clazz,
            Field field) {
        Setting settingAnnotation = field.getAnnotation(Setting.class);
        if (settingAnnotation == null) {
            return null;
        }

        makeAccessible(field);
        return new SettingAccessor<O, F>(
                clazz.getName() + '.' + field.getName(),
                o -> this.getter(field, o),
                (O o, F v) -> this.setter(field, o, v));
    }

    protected <O, F> F getter(Field field, O object) {
        try {
            return (F) field.get(object);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    protected <O, F> void setter(Field field, O object, F value) {
        try {
            field.set(object, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    // copy from Spring's ReflectionUtilss
    private static void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers())
                || !Modifier.isPublic(field.getDeclaringClass().getModifiers())
                || Modifier.isFinal(field.getModifiers()))
                && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }
}
