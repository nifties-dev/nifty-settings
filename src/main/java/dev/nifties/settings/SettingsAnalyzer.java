package dev.nifties.settings;

import dev.nifties.settings.annotation.Setting;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SettingsAnalyzer {

    public Collection<SettingAccessor> get(Class<?> clazz) {
        Collection<SettingAccessor> inherited = clazz.getSuperclass() == null
                ? Collections.emptyList() : get(clazz.getSuperclass());
        return Stream.of(
                inherited.stream(),
                Arrays.stream(clazz.getInterfaces()).map(c -> get(c)).flatMap(Collection::stream),
                Arrays.stream(clazz.getDeclaredFields())
                        .map(f -> this.processField(clazz, f)),
                Arrays.stream(clazz.getDeclaredMethods())
                        .map(m -> this.processMethod(clazz, m)))
        .flatMap(Function.identity())
        .filter(Objects::nonNull)
//        .distinct()
        .collect(Collectors.toList());
    }

    protected SettingAccessor processField(Class<?> clazz, Field field) {
        Setting settingAnnotation = field.getAnnotation(Setting.class);
        if (settingAnnotation == null) {
            return null;
        }

        // find setter
        BiConsumer<Object, Object> setter;
        try {
            // try to find setter method
            String setterName = "set" + Character.toUpperCase(field.getName().charAt(0));
            if (field.getName().length() > 1) {
                setterName += field.getName().substring(1);
            }
            Method setterMethod = clazz.getMethod(setterName, field.getType());
            setter = (o, v) -> this.setter(setterMethod, o, v);
        } catch (NoSuchMethodException e) {
            // setter not found, will set field directly
            makeAccessible(field);
            setter = (o, v) -> this.setter(field, o, v);
        }

        // find getter
        Function<Object, Object> getter;
        try {
            String getterName = Boolean.class.isAssignableFrom(
                    field.getType()) ? "is" : "get";
            getterName += Character.toUpperCase(field.getName().charAt(0));
            if (field.getName().length() > 1) {
                getterName += field.getName().substring(1);
            }
            Method getterMethod = clazz.getMethod(getterName);
            getter = o -> this.getter(getterMethod, o);
        } catch (NoSuchMethodException e) {
            // getter not found, will get field directly
            makeAccessible(field);
            getter = o -> this.getter(field, o);
        }

        return new SettingAccessor(field.getName(), getter, setter);
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
            // try to fiend field
            Field field = clazz.getField(fieldName);
            makeAccessible(field);
            getter = o -> this.getter(field, o);
        } catch (NoSuchFieldException e) {
            try {
                // field not found, try to find getter
                Method getterMethod = findGetterBySetter(clazz, method);
                getter = o -> this.getter(getterMethod, o);
            } catch (NoSuchMethodException e1) {
                // no field and no getter, means that we can't read initial value, well, whatever..
                getter = o -> null;
            }
        }
        return new SettingAccessor(fieldName, getter, (o, v) -> this.setter(method, o, v));
    }

    protected Method findGetterBySetter(Class<?> clazz, Method method) throws NoSuchMethodException {
        String getterName = (Boolean.TYPE
                .isAssignableFrom(method.getParameterTypes()[0])
                || Boolean.class.isAssignableFrom(
                        method.getParameterTypes()[0]) ? "is" : "get")
                + method.getName().substring(3);

        return clazz.getMethod(getterName);
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
            field.set(object, convert(field.getType(), value));
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    protected void setter(Method method, Object object, Object value) {
        try {
            method.invoke(object, convert(method.getParameterTypes()[0], value));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Sure, we could come up with all sorts of clever conversion, but it would fall out of the scope of this library.
     * There is no need to reinvent it, better to make use of some existing converters like Spring's ConverterService.
     */
    protected Object convert(Class<?> destinationType, Object value) {
        if (value == null && destinationType.isPrimitive()) {
            // a clever way to get default value for the primitive
            return Array.get(Array.newInstance(destinationType, 1), 0);
        } else {
            return value;
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
