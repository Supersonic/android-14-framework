package com.android.server;

import android.annotation.SystemApi;
import android.util.ArrayMap;
import java.util.Map;
import java.util.Objects;
@SystemApi(client = SystemApi.Client.SYSTEM_SERVER)
/* loaded from: classes.dex */
public final class LocalManagerRegistry {
    public static final Map<Class<?>, Object> sManagers = new ArrayMap();

    public static <T> T getManager(Class<T> cls) {
        T t;
        Map<Class<?>, Object> map = sManagers;
        synchronized (map) {
            t = (T) map.get(cls);
        }
        return t;
    }

    public static <T> T getManagerOrThrow(Class<T> cls) throws ManagerNotFoundException {
        T t = (T) getManager(cls);
        if (t != null) {
            return t;
        }
        throw new ManagerNotFoundException(cls);
    }

    public static <T> void addManager(Class<T> cls, T t) {
        Objects.requireNonNull(cls, "managerClass");
        Objects.requireNonNull(t, "manager");
        Map<Class<?>, Object> map = sManagers;
        synchronized (map) {
            if (map.containsKey(cls)) {
                throw new IllegalStateException(cls.getName() + " is already registered");
            }
            map.put(cls, t);
        }
    }

    /* loaded from: classes.dex */
    public static class ManagerNotFoundException extends Exception {
        public <T> ManagerNotFoundException(Class<T> cls) {
            super("Local manager " + cls.getName() + " does not exist or is not ready");
        }
    }
}
