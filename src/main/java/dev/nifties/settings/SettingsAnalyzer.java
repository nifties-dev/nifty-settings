package dev.nifties.settings;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dev.nifties.settings.annotation.Setting;

public class SettingsAnalyzer {

    public Collection<SettingAccessor> get(Class<?> clazz) {
        return Stream
                .concat(Arrays.stream(clazz.getDeclaredFields())
                        .map(f -> this.processField(clazz, f)),
                        Arrays.stream(clazz.getDeclaredMethods())
                                .map(m -> this.processMethod(clazz, m)))
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    protected SettingAccessor processField(Class<?> clazz, Field field) {
        Setting settingAnnotation = field.getAnnotation(Setting.class);
        if (settingAnnotation == null) {
            return null;
        }

        makeAccessible(field);
        return new SettingAccessor(clazz.getName() + '.' + field.getName(),
                o -> this.getter(field, o), (o, v) -> this.setter(field, o, v));
    }

    protected SettingAccessor processMethod(Class<?> clazz, Method method) {
        Setting settingAnnotation = method.getAnnotation(Setting.class);
        if (settingAnnotation == null) {
            return null;
        }
        if (!method.getName().startsWith("set")
                || method.getName().length() <= 4
                || method.getReturnType() != Void.TYPE
                || method.getParameterCount() != 1) {
            throw new IllegalArgumentException(
                    "@Settings not allowed on method: " + method + ","
                            + " only setters and fields can be marked with this annotation");
        }
        // makeAccessible(method);

        String fieldName = Character.toLowerCase(method.getName().charAt(3))
                + method.getName().substring(4);

        Function<Object, Object> getter;
        try {
            Field field = clazz.getField(fieldName);
            getter = o -> this.getter(field, o);
        } catch (NoSuchFieldException e) {
            try {
                String getterName = (Boolean.TYPE
                        .isAssignableFrom(method.getParameterTypes()[0])
                        || Boolean.class.isAssignableFrom(
                                method.getParameterTypes()[0]) ? "is" : "get")
                        + method.getName().substring(3);

                Method getterMethod = clazz.getMethod(getterName);
                getter = o -> this.getter(getterMethod, o);
            } catch (NoSuchMethodException e1) {
                getter = o -> null;
            }
        }
        return new SettingAccessor(clazz.getName() + '.' + fieldName, getter,
                (o, v) -> this.setter(method, o, v));
    }

    protected Object getter(Field field, Object object) {
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    protected Object getter(Method method, Object object) {
        try {
            return method.invoke(object);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    protected void setter(Field field, Object object, Object value) {
        try {
            field.set(object, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    protected void setter(Method method, Object object, Object value) {
        try {
            method.invoke(object, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
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
