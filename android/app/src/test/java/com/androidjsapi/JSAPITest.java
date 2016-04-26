package com.androidjsapi;

import android.content.Context;
import android.support.annotation.NonNull;

import com.androidjsapi.test.TestObject;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.JavaOnlyMap;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;

import java.util.ArrayList;
import java.util.List;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import static com.androidjsapi.TypedArray.of;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;

@PrepareForTest(Arguments.class)
@PowerMockIgnore({"org.mockito.*", "android.*"})
@RunWith(JUnitParamsRunner.class)
public class JSAPITest {
    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    Context mockContext;

    @InjectMocks
    JSAPI jsAPI;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        PowerMockito.mockStatic(Arguments.class);
        PowerMockito.when(Arguments.createArray()).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return new TypedArray();
            }
        });
        PowerMockito.when(Arguments.createMap()).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return new JavaOnlyMap();
            }
        });
    }

    @Test
    @Parameters(method = "values")
    public void createParameters(Object value) {
        ReadableArray arguments = of(of(null, value));

        Object[] parameters = jsAPI.createParameters(arguments);

        assertArrayEquals(parameters, new Object[]{value});
    }

    private Object[] values() {
        return new Object[]{
                new Object[]{12},
                new Object[]{"hello"},
                new Object[]{true},
                new Object[]{null},
        };
    }

    @Test
    public void createParameters_context() {
        ReadableArray arguments = of(of("android.content.Context", "*context"));

        Object[] parameters = jsAPI.createParameters(arguments);

        assertArrayEquals(parameters, new Object[]{mockContext});
    }

    @Test
    @Parameters(method = "types")
    public void createParameterTypes(String type, Class expectedType) throws ClassNotFoundException {
        ReadableArray arguments = of(of(type, null));

        Class<?>[] parameterTypes = jsAPI.createParameterTypes(arguments);

        assertArrayEquals(parameterTypes, new Object[]{expectedType});
    }

    private Object[] types() {
        return new Object[]{
                new Object[]{"int", int.class},
                new Object[]{"java.lang.String", String.class},
                new Object[]{"java.lang.Boolean", Boolean.class},
        };
    }

    @Test
    public void reflectStatic() throws Exception {
        ReadableMap result = jsAPI.reflect(TestObject.class);

        List<JavaOnlyMap> methodList = getMapList(result, "methods");
        assertThat(methodList, containsInAnyOrder(
                JavaOnlyMap.of("name", "staticA", "static", true),
                JavaOnlyMap.of("name", "staticA", "static", true, "arguments", of("int")),
                JavaOnlyMap.of("name", "staticB", "static", true, "arguments", of("java.lang.String"))
        ));
        List<JavaOnlyMap> fieldList = getMapList(result, "fields");
        assertThat(fieldList, containsInAnyOrder(
                JavaOnlyMap.of("name", "STATIC_INT", "static", true, "value", 0),
                JavaOnlyMap.of("name", "STATIC_STRING", "static", true, "value", "abc"),
                JavaOnlyMap.of("name", "STATIC_BOOLEAN", "static", true, "value", true),
                JavaOnlyMap.of("name", "STATIC_DOUBLE", "static", true, "value", 10.1)
        ));

        assertThat(result.getString("className"), is("com.androidjsapi.test.TestObject"));
    }

    @Test
    public void reflectInstance() throws Exception {
        TestObject instance = new TestObject();
        ReadableMap result = jsAPI.reflect(instance);

        List<JavaOnlyMap> methodList = getMapList(result, "methods");
        assertThat(methodList, containsInAnyOrder(
                JavaOnlyMap.of("name", "staticA", "static", true),
                JavaOnlyMap.of("name", "staticA", "static", true, "arguments", of("int")),
                JavaOnlyMap.of("name", "staticB", "static", true, "arguments", of("java.lang.String")),

                JavaOnlyMap.of("name", "a"),
                JavaOnlyMap.of("name", "a", "arguments", of("int")),
                JavaOnlyMap.of("name", "b", "arguments", of("java.lang.String"))
        ));
        List<JavaOnlyMap> fieldList = getMapList(result, "fields");
        assertThat(fieldList, containsInAnyOrder(
                JavaOnlyMap.of("name", "STATIC_INT", "static", true, "value", 0),
                JavaOnlyMap.of("name", "STATIC_STRING", "static", true, "value", "abc"),
                JavaOnlyMap.of("name", "STATIC_BOOLEAN", "static", true, "value", true),
                JavaOnlyMap.of("name", "STATIC_DOUBLE", "static", true, "value", 10.1),

                JavaOnlyMap.of("name", "INT", "value", 0),
                JavaOnlyMap.of("name", "STRING", "value", "abc"),
                JavaOnlyMap.of("name", "BOOLEAN", "value", true),
                JavaOnlyMap.of("name", "DOUBLE", "value", 10.1)
        ));

        assertThat(result.getString("className"), is("com.androidjsapi.test.TestObject"));
    }

    @NonNull
    private List<JavaOnlyMap> getMapList(ReadableMap reflection, String key) {
        ReadableArray array = reflection.getArray(key);
        List<JavaOnlyMap> maps = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            maps.add((JavaOnlyMap) array.getMap(i));
        }
        return maps;
    }

}
