package com.android.server;

import android.util.ArrayMap;
/* loaded from: classes5.dex */
public final class LocalServices {
    private static final ArrayMap<Class<?>, Object> sLocalServiceObjects = new ArrayMap<>();

    private LocalServices() {
    }

    public static <T> T getService(Class<T> type) {
        T t;
        ArrayMap<Class<?>, Object> arrayMap = sLocalServiceObjects;
        synchronized (arrayMap) {
            t = (T) arrayMap.get(type);
        }
        return t;
    }

    public static <T> void addService(Class<T> type, T service) {
        ArrayMap<Class<?>, Object> arrayMap = sLocalServiceObjects;
        synchronized (arrayMap) {
            if (arrayMap.containsKey(type)) {
                throw new IllegalStateException("Overriding service registration");
            }
            arrayMap.put(type, service);
        }
    }

    public static <T> void removeServiceForTest(Class<T> type) {
        ArrayMap<Class<?>, Object> arrayMap = sLocalServiceObjects;
        synchronized (arrayMap) {
            arrayMap.remove(type);
        }
    }
}
