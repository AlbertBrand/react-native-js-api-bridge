package com.androidjsapi;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.Method;

public class AndroidJSAPIModule extends ReactContextBaseJavaModule {
    private final JSAPI jsAPI;
    private final ObjectRegistry registry = new ObjectRegistry();

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
            ReadableMap classReflection = jsAPI.reflect(clazz);
            promise.resolve(classReflection);
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void methodCall(Integer objectId, String className, String staticMethod, ReadableArray arguments, Promise promise) {
        try {
            Object receiver = objectId > 0 ? registry.get(objectId) : null;
            Class<?> clazz = ClassUtils.getClass(className);
            Method method = clazz.getMethod(staticMethod, jsAPI.createParameterTypes(arguments));
            Object result = method.invoke(receiver, jsAPI.createParameters(arguments));

            // TODO check if primitive, don't store/reflect in that case
            int resultObjectId = registry.add(result);
            WritableMap classReflection = jsAPI.reflect(result);
            classReflection.putInt("objectId", resultObjectId);
            promise.resolve(classReflection);

        } catch (Exception e) {
            promise.reject(e);
        }
    }


}
