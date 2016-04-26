package com.androidjsapi;

import com.facebook.react.bridge.JavaOnlyArray;
import com.facebook.react.bridge.JavaOnlyMap;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of JavaOnlyArray with support for getType
 */
public class TypedArray extends JavaOnlyArray {
    private final List<Object> backingValueList;
    private final List<ReadableType> backingTypeList;

    public static TypedArray of(Object... values) {
        return new TypedArray(values);
    }

    private TypedArray(Object... values) {
        backingValueList = Arrays.asList(values);
        backingTypeList = new ArrayList<>();
        for (Object value : values) {
            if (value instanceof String) {
                backingTypeList.add(ReadableType.String);
            } else if (value instanceof Integer) {
                backingTypeList.add(ReadableType.Number);
            } else if (value instanceof Double) {
                backingTypeList.add(ReadableType.Number);
            } else if (value instanceof Boolean) {
                backingTypeList.add(ReadableType.Boolean);
            } else if (value instanceof ReadableArray) {
                backingTypeList.add(ReadableType.Array);
            } else if (value instanceof ReadableMap) {
                backingTypeList.add(ReadableType.Map);
            } else if (value == null) {
                backingTypeList.add(ReadableType.Null);
            }
        }
    }

    public TypedArray() {
        backingValueList = new ArrayList<>();
        backingTypeList = new ArrayList<>();
    }

    @Override
    public int size() {
        return backingValueList.size();
    }

    @Override
    public boolean isNull(int index) {
        return backingValueList.get(index) == null;
    }

    @Override
    public double getDouble(int index) {
        return (Double) backingValueList.get(index);
    }

    @Override
    public int getInt(int index) {
        return (Integer) backingValueList.get(index);
    }

    @Override
    public String getString(int index) {
        return (String) backingValueList.get(index);
    }

    @Override
    public JavaOnlyArray getArray(int index) {
        return (JavaOnlyArray) backingValueList.get(index);
    }

    @Override
    public boolean getBoolean(int index) {
        return (Boolean) backingValueList.get(index);
    }

    @Override
    public JavaOnlyMap getMap(int index) {
        return (JavaOnlyMap) backingValueList.get(index);
    }

    @Override
    public ReadableType getType(int index) {
        return backingTypeList.get(index);
    }

    @Override
    public String toString() {
        return backingValueList.toString();
    }

    @Override
    public void pushNull() {
        backingValueList.add(null);
        backingTypeList.add(ReadableType.Null);
    }

    @Override
    public void pushBoolean(boolean value) {
        backingValueList.add(value);
        backingTypeList.add(ReadableType.Boolean);
    }

    @Override
    public void pushDouble(double value) {
        backingValueList.add(value);
        backingTypeList.add(ReadableType.Number);
    }

    @Override
    public void pushInt(int value) {
        backingValueList.add(value);
        backingTypeList.add(ReadableType.Number); // TODO
    }

    @Override
    public void pushString(String value) {
        backingValueList.add(value);
        backingTypeList.add(ReadableType.String);
    }

    @Override
    public void pushArray(WritableArray array) {
        backingValueList.add(array);
        backingTypeList.add(ReadableType.Array);
    }

    @Override
    public void pushMap(WritableMap map) {
        backingValueList.add(map);
        backingTypeList.add(ReadableType.Map);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TypedArray that = (TypedArray) o;

        if (backingValueList != null ? !backingValueList.equals(that.backingValueList) : that.backingValueList != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return backingValueList != null ? backingValueList.hashCode() : 0;
    }

}
