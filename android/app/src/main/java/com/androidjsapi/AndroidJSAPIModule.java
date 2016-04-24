package com.androidjsapi;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;

import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.Method;

public class AndroidJSAPIModule extends ReactContextBaseJavaModule {
    private final JSAPI jsAPI;

    @Override
    public String getName() {
        return "AndroidJSAPI";
    }

    public AndroidJSAPIModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.jsAPI = new JSAPI(reactContext);
    }

    @ReactMethod
    public void reflect(String className, Promise promise) {
        try {
            Class<?> clazz = ClassUtils.getClass(className);
            ReadableArray classReflection = jsAPI.reflect(clazz);
            promise.resolve(classReflection);
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void staticMethodCall(String className, String staticMethod, ReadableArray arguments, Promise promise) {
        try {
            Class<?> clazz = ClassUtils.getClass(className);
            Method method = clazz.getMethod(staticMethod, jsAPI.createParameterTypes(arguments));
            Object result = method.invoke(null, jsAPI.createParameters(arguments));

            // TODO return reflected result in case of object instance
            promise.resolve("jejeah!");

        } catch (Exception e) {
            promise.reject(e);
        }
    }


}
