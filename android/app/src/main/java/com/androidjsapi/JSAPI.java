package com.androidjsapi;

import android.content.Context;

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

    public ReadableArray reflect(Class<?> clazz) throws Exception {
        WritableArray refl = Arguments.createArray();

        // add static methods
        for (Method method : clazz.getMethods()) {
            if (Modifier.isStatic(method.getModifiers())) {
                WritableMap map = Arguments.createMap();
                map.putString("name", method.getName());
                map.putString("type", "staticMethod");
                if (method.getParameterTypes().length > 0) {
                    WritableArray arguments = Arguments.createArray();
                    for (Class<?> parameterType : method.getParameterTypes()) {
                        arguments.pushString(parameterType.getName());
                    }
                    map.putArray("arguments", arguments);
                }
                refl.pushMap(map);
            }
        }

        // add static fields
        for (Field field : clazz.getFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                WritableMap map = Arguments.createMap();
                map.putString("name", field.getName());
                map.putString("type", "staticField");
                map.putInt("value", field.getInt(null));
                refl.pushMap(map);
            }
        }

        return refl;
    }
}
