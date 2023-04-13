package com.android.internal.p028os;

import android.p008os.Build;
import android.util.ArrayMap;
import dalvik.system.PathClassLoader;
/* renamed from: com.android.internal.os.SystemServerClassLoaderFactory */
/* loaded from: classes4.dex */
public final class SystemServerClassLoaderFactory {
    private static final ArrayMap<String, PathClassLoader> sLoadedPaths = new ArrayMap<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    public static PathClassLoader createClassLoader(String path, ClassLoader parent) {
        ArrayMap<String, PathClassLoader> arrayMap = sLoadedPaths;
        if (arrayMap.containsKey(path)) {
            throw new IllegalStateException("A ClassLoader for " + path + " already exists");
        }
        PathClassLoader pathClassLoader = (PathClassLoader) ClassLoaderFactory.createClassLoader(path, null, null, parent, Build.VERSION.SDK_INT, true, null);
        arrayMap.put(path, pathClassLoader);
        return pathClassLoader;
    }

    public static PathClassLoader getOrCreateClassLoader(String path, ClassLoader parent, boolean isTestOnly) {
        PathClassLoader pathClassLoader = sLoadedPaths.get(path);
        if (pathClassLoader != null) {
            return pathClassLoader;
        }
        if (!allowClassLoaderCreation(path, isTestOnly)) {
            throw new RuntimeException("Creating a ClassLoader from " + path + " is not allowed. Please make sure that the jar is listed in `PRODUCT_APEX_STANDALONE_SYSTEM_SERVER_JARS` in the Makefile and added as a `standalone_contents` of a `systemserverclasspath_fragment` in `Android.bp`.");
        }
        return createClassLoader(path, parent);
    }

    private static boolean allowClassLoaderCreation(String path, boolean isTestOnly) {
        return !path.startsWith("/apex/") || isTestOnly || ZygoteInit.shouldProfileSystemServer();
    }
}
