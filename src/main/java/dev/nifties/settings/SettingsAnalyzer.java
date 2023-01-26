package dev.nifties.settings;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import dev.nifties.settings.annotation.Setting;

public class SettingsAnalyzer {

    public <O> Collection<SettingAccessor<O>> get(Class<O> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .map(f -> (SettingAccessor<O>) this.processField(clazz, f))
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    protected <O> SettingAccessor<O> processField(Class<O> clazz, Field field) {
        Setting settingAnnotation = field.getAnnotation(Setting.class);
        if (settingAnnotation == null) {
            return null;
        }

        makeAccessible(field);
        return new SettingAccessor<O>(clazz.getName() + '.' + field.getName(),
                o -> this.getter(field, o), (o, v) -> this.setter(field, o, v));
    }

    protected <O> Object getter(Field field, O object) {
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    protected <O> void setter(Field field, O object, Object value) {
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
