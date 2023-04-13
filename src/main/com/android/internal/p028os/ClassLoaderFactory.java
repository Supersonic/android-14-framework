package com.android.internal.p028os;

import android.p008os.Trace;
import dalvik.system.DelegateLastClassLoader;
import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;
import java.util.ArrayList;
import java.util.List;
/* renamed from: com.android.internal.os.ClassLoaderFactory */
/* loaded from: classes4.dex */
public class ClassLoaderFactory {
    private static final String PATH_CLASS_LOADER_NAME = PathClassLoader.class.getName();
    private static final String DEX_CLASS_LOADER_NAME = DexClassLoader.class.getName();
    private static final String DELEGATE_LAST_CLASS_LOADER_NAME = DelegateLastClassLoader.class.getName();

    private static native String createClassloaderNamespace(ClassLoader classLoader, int i, String str, String str2, boolean z, String str3, String str4);

    private ClassLoaderFactory() {
    }

    public static String getPathClassLoaderName() {
        return PATH_CLASS_LOADER_NAME;
    }

    public static boolean isValidClassLoaderName(String name) {
        return name != null && (isPathClassLoaderName(name) || isDelegateLastClassLoaderName(name));
    }

    public static boolean isPathClassLoaderName(String name) {
        return name == null || PATH_CLASS_LOADER_NAME.equals(name) || DEX_CLASS_LOADER_NAME.equals(name);
    }

    public static boolean isDelegateLastClassLoaderName(String name) {
        return DELEGATE_LAST_CLASS_LOADER_NAME.equals(name);
    }

    public static ClassLoader createClassLoader(String dexPath, String librarySearchPath, ClassLoader parent, String classloaderName, List<ClassLoader> sharedLibraries, List<ClassLoader> sharedLibrariesLoadedAfter) {
        ClassLoader[] arrayOfSharedLibraries;
        ClassLoader[] arrayOfSharedLibrariesLoadedAfterApp;
        if (sharedLibraries != null) {
            arrayOfSharedLibraries = (ClassLoader[]) sharedLibraries.toArray(new ClassLoader[sharedLibraries.size()]);
        } else {
            arrayOfSharedLibraries = null;
        }
        if (sharedLibrariesLoadedAfter == null) {
            arrayOfSharedLibrariesLoadedAfterApp = null;
        } else {
            arrayOfSharedLibrariesLoadedAfterApp = (ClassLoader[]) sharedLibrariesLoadedAfter.toArray(new ClassLoader[sharedLibrariesLoadedAfter.size()]);
        }
        if (isPathClassLoaderName(classloaderName)) {
            return new PathClassLoader(dexPath, librarySearchPath, parent, arrayOfSharedLibraries, arrayOfSharedLibrariesLoadedAfterApp);
        }
        if (isDelegateLastClassLoaderName(classloaderName)) {
            return new DelegateLastClassLoader(dexPath, librarySearchPath, parent, arrayOfSharedLibraries, arrayOfSharedLibrariesLoadedAfterApp);
        }
        throw new AssertionError("Invalid classLoaderName: " + classloaderName);
    }

    public static ClassLoader createClassLoader(String dexPath, String librarySearchPath, String libraryPermittedPath, ClassLoader parent, int targetSdkVersion, boolean isNamespaceShared, String classLoaderName) {
        List<String> nativeSharedLibraries = new ArrayList<>();
        nativeSharedLibraries.add("ALL");
        return createClassLoader(dexPath, librarySearchPath, libraryPermittedPath, parent, targetSdkVersion, isNamespaceShared, classLoaderName, null, nativeSharedLibraries, null);
    }

    public static ClassLoader createClassLoader(String dexPath, String librarySearchPath, String libraryPermittedPath, ClassLoader parent, int targetSdkVersion, boolean isNamespaceShared, String classLoaderName, List<ClassLoader> sharedLibraries, List<String> nativeSharedLibraries, List<ClassLoader> sharedLibrariesAfter) {
        String sonameList;
        ClassLoader classLoader = createClassLoader(dexPath, librarySearchPath, parent, classLoaderName, sharedLibraries, sharedLibrariesAfter);
        if (nativeSharedLibraries != null) {
            String sonameList2 = String.join(":", nativeSharedLibraries);
            sonameList = sonameList2;
        } else {
            sonameList = "";
        }
        Trace.traceBegin(64L, "createClassloaderNamespace");
        String errorMessage = createClassloaderNamespace(classLoader, targetSdkVersion, librarySearchPath, libraryPermittedPath, isNamespaceShared, dexPath, sonameList);
        Trace.traceEnd(64L);
        if (errorMessage != null) {
            throw new UnsatisfiedLinkError("Unable to create namespace for the classloader " + classLoader + ": " + errorMessage);
        }
        return classLoader;
    }
}
