package com.androidjsapi;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ObjectRegistryTest {
    ObjectRegistry registry;

    class A {
    }

    @Before
    public void setup() {
        registry = new ObjectRegistry();
    }

    @Test
    public void getAddedObject() {
        A a = new A();
        int id = registry.add(a);

        Object retrieved = registry.get(id);

        assertEquals(a, retrieved);
        assertEquals(1, id);
    }

    @Test
    public void uniqueIdPerObject() {
        A a = new A();
        int id = registry.add(a);
        int id2 = registry.add(a);

        assertNotEquals(id, id2);
    }

}
