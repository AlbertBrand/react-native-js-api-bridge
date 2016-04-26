package com.androidjsapi;

import java.util.HashMap;
import java.util.Map;

public class ObjectRegistry {
    private final Map<Integer, Object> registry = new HashMap<>();
    private int nextId = 0;

    public int add(Object o) {
        int i = ++nextId;
        registry.put(i, o);
        return i;
    }

    public Object get(int i) {
        return registry.get(i);
    }

    // TODO clean out of scope objects at some moment
}
