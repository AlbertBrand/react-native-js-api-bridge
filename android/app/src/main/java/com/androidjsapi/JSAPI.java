package com.androidjsapi;

import android.content.Context;
import android.support.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class JSAPI {
    private final Context context;

    public JSAPI(Context context) {
        this.context = context;
    }

    public Object[] createParameters(ReadableArray arguments) {
        Object[] parameters = new Object[arguments.size()];
        for (int i = 0; i < arguments.size(); i++) {
            ReadableArray argument = arguments.getArray(i);
            parameters[i] = transformToValue(argument);
        }
        return parameters;
    }

    public Class<?>[] createParameterTypes(ReadableArray arguments) throws ClassNotFoundException {
        Class<?>[] parameterTypes = new Class<?>[arguments.size()];
        for (int i = 0; i < arguments.size(); i++) {
            ReadableArray argument = arguments.getArray(i);
            parameterTypes[i] = ClassUtils.getClass(argument.getString(0));
        }
        return parameterTypes;
    }

    private Object transformToValue(ReadableArray argument) {
        switch (argument.getType(1)) {
            case Number:
                return argument.getInt(1);
            case String:
                String value = argument.getString(1);
                if ("*context".equals(value)) {
                    return context;
                } else {
                    return value;
                }
            case Boolean:
                return argument.getBoolean(1);
            case Null:
                return null;
            default:
                // TODO
                throw new UnsupportedOperationException("other types not implemented yet!");
        }
    }

    public WritableMap reflect(Object object) throws Exception {
        return reflect(object.getClass(), object);
    }

    public WritableMap reflect(Class<?> clazz) throws Exception {
        return reflect(clazz, null);
    }

    public WritableMap reflect(Class<?> clazz, @Nullable Object objectOrNull) throws Exception {
        WritableMap reflection = Arguments.createMap();

        // add methods
        WritableArray methods = Arguments.createArray();

        for (Method method : clazz.getDeclaredMethods()) {
            int modifiers = method.getModifiers();
            boolean isStatic = Modifier.isStatic(modifiers);
            boolean isPublic = Modifier.isPublic(modifiers);
            if (isPublic && (isStatic || objectOrNull != null)) {
                WritableMap map = Arguments.createMap();
                map.putString("name", method.getName());
                if (method.getParameterTypes().length > 0) {
                    WritableArray arguments = Arguments.createArray();
                    for (Class<?> parameterType : method.getParameterTypes()) {
                        arguments.pushString(parameterType.getName());
                    }
                    map.putArray("arguments", arguments);
                }
                if (Modifier.isStatic(modifiers)) {
                    map.putBoolean("static", true);
                }
                methods.pushMap(map);
            }
        }

        // add fields
        WritableArray fields = Arguments.createArray();

        for (Field field : clazz.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            boolean isStatic = Modifier.isStatic(modifiers);
            boolean isPublic = Modifier.isPublic(modifiers);
            if (isPublic && (isStatic || objectOrNull != null)) {
                WritableMap map = Arguments.createMap();
                map.putString("name", field.getName());
                if (field.getType().isAssignableFrom(int.class)) {
                    map.putInt("value", field.getInt(objectOrNull));
                } else if (field.getType().isAssignableFrom(boolean.class)) {
                    map.putBoolean("value", field.getBoolean(objectOrNull));
                } else if (field.getType().isAssignableFrom(double.class)) {
                    map.putDouble("value", field.getDouble(objectOrNull));
                } else if (field.getType().isAssignableFrom(String.class)) {
                    map.putString("value", (String) field.get(objectOrNull));
                }
                // TODO add more conversions
                if (isStatic) {
                    map.putBoolean("static", true);
                }
                fields.pushMap(map);
            }
        }

        reflection.putArray("methods", methods);
        reflection.putArray("fields", fields);
        reflection.putString("className", clazz.getCanonicalName());

        return reflection;
    }
}
