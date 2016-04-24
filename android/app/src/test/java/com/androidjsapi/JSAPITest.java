package com.androidjsapi;

import android.content.Context;
import android.support.annotation.NonNull;

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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import static com.androidjsapi.TypedArray.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

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
    public void reflect() throws Exception {
        ReadableArray result = jsAPI.reflect(TestClass.class);

        List<JavaOnlyMap> mapList = getMapList(result);
        assertThat(mapList, containsInAnyOrder(
                JavaOnlyMap.of("name", "staticB", "type", "staticMethod", "arguments", of("java.lang.String")),
                JavaOnlyMap.of("name", "staticA", "type", "staticMethod"),
                JavaOnlyMap.of("name", "staticA", "type", "staticMethod", "arguments", of("int")),
                JavaOnlyMap.of("name", "STATIC_INT", "type", "staticField", "value", 0))
        );
    }

    @NonNull
    private List<JavaOnlyMap> getMapList(ReadableArray result) {
        List<JavaOnlyMap> maps = new ArrayList<>();
        for (int i = 0; i < result.size(); i++) {
            maps.add((JavaOnlyMap) result.getMap(i));
        }
        return maps;
    }

}
