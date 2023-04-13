package android.app;

import android.content.p001pm.SharedLibraryInfo;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Build;
import android.p008os.GraphicsEnvironment;
import android.p008os.Trace;
import android.util.ArrayMap;
import android.util.Log;
import android.util.NtpTrustedTime;
import com.android.internal.p028os.ClassLoaderFactory;
import dalvik.system.PathClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public class ApplicationLoaders {
    private static final String TAG = "ApplicationLoaders";
    private static final ApplicationLoaders gApplicationLoaders = new ApplicationLoaders();
    private final ArrayMap<String, ClassLoader> mLoaders = new ArrayMap<>();
    private Map<String, CachedClassLoader> mSystemLibsCacheMap = null;

    public static ApplicationLoaders getDefault() {
        return gApplicationLoaders;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ClassLoader getClassLoader(String zip, int targetSdkVersion, boolean isBundled, String librarySearchPath, String libraryPermittedPath, ClassLoader parent, String classLoaderName) {
        return getClassLoaderWithSharedLibraries(zip, targetSdkVersion, isBundled, librarySearchPath, libraryPermittedPath, parent, classLoaderName, null, null, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ClassLoader getClassLoaderWithSharedLibraries(String zip, int targetSdkVersion, boolean isBundled, String librarySearchPath, String libraryPermittedPath, ClassLoader parent, String classLoaderName, List<ClassLoader> sharedLibraries, List<String> nativeSharedLibraries, List<ClassLoader> sharedLibrariesLoadedAfterApp) {
        return getClassLoader(zip, targetSdkVersion, isBundled, librarySearchPath, libraryPermittedPath, parent, zip, classLoaderName, sharedLibraries, nativeSharedLibraries, sharedLibrariesLoadedAfterApp);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ClassLoader getSharedLibraryClassLoaderWithSharedLibraries(String zip, int targetSdkVersion, boolean isBundled, String librarySearchPath, String libraryPermittedPath, ClassLoader parent, String classLoaderName, List<ClassLoader> sharedLibraries, List<ClassLoader> sharedLibrariesAfter) {
        ClassLoader loader = getCachedNonBootclasspathSystemLib(zip, parent, classLoaderName, sharedLibraries);
        if (loader != null) {
            return loader;
        }
        List<String> nativeSharedLibraries = new ArrayList<>();
        nativeSharedLibraries.add("ALL");
        return getClassLoaderWithSharedLibraries(zip, targetSdkVersion, isBundled, librarySearchPath, libraryPermittedPath, parent, classLoaderName, sharedLibraries, nativeSharedLibraries, sharedLibrariesAfter);
    }

    private ClassLoader getClassLoader(String zip, int targetSdkVersion, boolean isBundled, String librarySearchPath, String libraryPermittedPath, ClassLoader parent, String cacheKey, String classLoaderName, List<ClassLoader> sharedLibraries, List<String> nativeSharedLibraries, List<ClassLoader> sharedLibrariesLoadedAfterApp) {
        ClassLoader parent2;
        ClassLoader baseParent = ClassLoader.getSystemClassLoader().getParent();
        synchronized (this.mLoaders) {
            if (parent != null) {
                parent2 = parent;
            } else {
                parent2 = baseParent;
            }
            try {
            } catch (Throwable th) {
                th = th;
            }
            if (parent2 == baseParent) {
                try {
                    ClassLoader loader = this.mLoaders.get(cacheKey);
                    if (loader == null) {
                        Trace.traceBegin(64L, zip);
                        try {
                            ClassLoader classloader = ClassLoaderFactory.createClassLoader(zip, librarySearchPath, libraryPermittedPath, parent2, targetSdkVersion, isBundled, classLoaderName, sharedLibraries, nativeSharedLibraries, sharedLibrariesLoadedAfterApp);
                            Trace.traceEnd(64L);
                            Trace.traceBegin(64L, "setLayerPaths");
                            GraphicsEnvironment.getInstance().setLayerPaths(classloader, librarySearchPath, libraryPermittedPath);
                            Trace.traceEnd(64L);
                            if (cacheKey != null) {
                                this.mLoaders.put(cacheKey, classloader);
                            }
                            return classloader;
                        } catch (Throwable th2) {
                            th = th2;
                        }
                    } else {
                        try {
                            return loader;
                        } catch (Throwable th3) {
                            th = th3;
                        }
                    }
                } catch (Throwable th4) {
                    th = th4;
                }
                throw th;
            }
            Trace.traceBegin(64L, zip);
            ClassLoader loader2 = ClassLoaderFactory.createClassLoader(zip, null, parent2, classLoaderName, sharedLibraries, null);
            Trace.traceEnd(64L);
            return loader2;
        }
    }

    public void createAndCacheNonBootclasspathSystemClassLoaders(SharedLibraryInfo[] libs) {
        if (this.mSystemLibsCacheMap != null) {
            throw new IllegalStateException("Already cached.");
        }
        this.mSystemLibsCacheMap = new HashMap();
        for (SharedLibraryInfo lib : libs) {
            createAndCacheNonBootclasspathSystemClassLoader(lib);
        }
    }

    private void createAndCacheNonBootclasspathSystemClassLoader(SharedLibraryInfo lib) {
        ArrayList<ClassLoader> sharedLibraries;
        String path = lib.getPath();
        List<SharedLibraryInfo> dependencies = lib.getDependencies();
        if (dependencies == null) {
            sharedLibraries = null;
        } else {
            ArrayList<ClassLoader> sharedLibraries2 = new ArrayList<>(dependencies.size());
            for (SharedLibraryInfo dependency : dependencies) {
                String dependencyPath = dependency.getPath();
                CachedClassLoader cached = this.mSystemLibsCacheMap.get(dependencyPath);
                if (cached == null) {
                    throw new IllegalStateException("Failed to find dependency " + dependencyPath + " of cachedlibrary " + path);
                }
                sharedLibraries2.add(cached.loader);
            }
            sharedLibraries = sharedLibraries2;
        }
        ClassLoader classLoader = getClassLoader(path, Build.VERSION.SDK_INT, true, null, null, null, null, null, sharedLibraries, null, null);
        if (classLoader == null) {
            throw new IllegalStateException("Failed to cache " + path);
        }
        CachedClassLoader cached2 = new CachedClassLoader();
        cached2.loader = classLoader;
        cached2.sharedLibraries = sharedLibraries;
        Log.m112d(TAG, "Created zygote-cached class loader: " + path);
        this.mSystemLibsCacheMap.put(path, cached2);
    }

    private static boolean sharedLibrariesEquals(List<ClassLoader> lhs, List<ClassLoader> rhs) {
        if (lhs == null) {
            return rhs == null;
        }
        return lhs.equals(rhs);
    }

    public ClassLoader getCachedNonBootclasspathSystemLib(String zip, ClassLoader parent, String classLoaderName, List<ClassLoader> sharedLibraries) {
        CachedClassLoader cached;
        Map<String, CachedClassLoader> map = this.mSystemLibsCacheMap;
        if (map != null && parent == null && classLoaderName == null && (cached = map.get(zip)) != null) {
            if (!sharedLibrariesEquals(sharedLibraries, cached.sharedLibraries)) {
                Log.m104w(TAG, "Unexpected environment loading cached library " + zip + " (real|cached): (" + sharedLibraries + NtpTrustedTime.NTP_SETTING_SERVER_NAME_DELIMITER + cached.sharedLibraries + NavigationBarInflaterView.KEY_CODE_END);
                return null;
            }
            Log.m112d(TAG, "Returning zygote-cached class loader: " + zip);
            return cached.loader;
        }
        return null;
    }

    public ClassLoader createAndCacheWebViewClassLoader(String packagePath, String libsPath, String cacheKey) {
        return getClassLoader(packagePath, Build.VERSION.SDK_INT, false, libsPath, null, null, cacheKey, null, null, null, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addPath(ClassLoader classLoader, String dexPath) {
        if (!(classLoader instanceof PathClassLoader)) {
            throw new IllegalStateException("class loader is not a PathClassLoader");
        }
        PathClassLoader baseDexClassLoader = (PathClassLoader) classLoader;
        baseDexClassLoader.addDexPath(dexPath);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addNative(ClassLoader classLoader, Collection<String> libPaths) {
        if (!(classLoader instanceof PathClassLoader)) {
            throw new IllegalStateException("class loader is not a PathClassLoader");
        }
        PathClassLoader baseDexClassLoader = (PathClassLoader) classLoader;
        baseDexClassLoader.addNativePath(libPaths);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class CachedClassLoader {
        ClassLoader loader;
        List<ClassLoader> sharedLibraries;

        private CachedClassLoader() {
        }
    }
}
