package com.androidjsapi.test;

public class TestObject {
    public final int INT = 0;
    public final boolean BOOLEAN = true;
    public final double DOUBLE = 10.1;
    public final String STRING = "abc";

    public static final int STATIC_INT = 0;
    public static final boolean STATIC_BOOLEAN = true;
    public static final double STATIC_DOUBLE = 10.1;
    public static final String STATIC_STRING = "abc";

    public void a() {
    }

    public void a(int i) {
    }

    public void b(String x) {
    }


    public static void staticA() {
    }

    public static void staticA(int i) {
    }

    public static void staticB(String x) {
    }

    /* following should not appear in js reflection */
    static final int PACKAGE_STATIC_INT = 1;
    protected static final String PROTECTED_STATIC_STRING = "abc";
    private static final int PRIVATE_STATIC_INT = 1;

    protected final String PROTECTED_STRING = "abc";
    private final String PRIVATE_STRING = "abc";

    void packageA() {
    }
    protected void protectedA() {
    }
    private void privateA() {
    }

    static void packageStaticA() {
    }
    protected static void protectedStaticA() {
    }
    private static void privateStaticA() {
    }

}
