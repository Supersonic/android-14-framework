package android.filterfw.core;

import android.media.MediaMetrics;
import android.util.Log;
import dalvik.system.PathClassLoader;
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Iterator;
/* loaded from: classes.dex */
public class FilterFactory {
    private static FilterFactory mSharedFactory;
    private HashSet<String> mPackages = new HashSet<>();
    private static ClassLoader mCurrentClassLoader = Thread.currentThread().getContextClassLoader();
    private static HashSet<String> mLibraries = new HashSet<>();
    private static Object mClassLoaderGuard = new Object();
    private static final String TAG = "FilterFactory";
    private static boolean mLogVerbose = Log.isLoggable(TAG, 2);

    public static FilterFactory sharedFactory() {
        if (mSharedFactory == null) {
            mSharedFactory = new FilterFactory();
        }
        return mSharedFactory;
    }

    public static void addFilterLibrary(String libraryPath) {
        if (mLogVerbose) {
            Log.m106v(TAG, "Adding filter library " + libraryPath);
        }
        synchronized (mClassLoaderGuard) {
            if (mLibraries.contains(libraryPath)) {
                if (mLogVerbose) {
                    Log.m106v(TAG, "Library already added");
                }
                return;
            }
            mLibraries.add(libraryPath);
            mCurrentClassLoader = new PathClassLoader(libraryPath, mCurrentClassLoader);
        }
    }

    public void addPackage(String packageName) {
        if (mLogVerbose) {
            Log.m106v(TAG, "Adding package " + packageName);
        }
        this.mPackages.add(packageName);
    }

    public Filter createFilterByClassName(String className, String filterName) {
        if (mLogVerbose) {
            Log.m106v(TAG, "Looking up class " + className);
        }
        Class filterClass = null;
        Iterator<String> it = this.mPackages.iterator();
        while (it.hasNext()) {
            String packageName = it.next();
            try {
                if (mLogVerbose) {
                    Log.m106v(TAG, "Trying " + packageName + MediaMetrics.SEPARATOR + className);
                }
                synchronized (mClassLoaderGuard) {
                    filterClass = mCurrentClassLoader.loadClass(packageName + MediaMetrics.SEPARATOR + className);
                }
            } catch (ClassNotFoundException e) {
            }
            if (filterClass != null) {
                break;
            }
        }
        if (filterClass == null) {
            throw new IllegalArgumentException("Unknown filter class '" + className + "'!");
        }
        return createFilterByClass(filterClass, filterName);
    }

    public Filter createFilterByClass(Class filterClass, String filterName) {
        if (!Filter.class.isAssignableFrom(filterClass)) {
            throw new IllegalArgumentException("Attempting to allocate class '" + filterClass + "' which is not a subclass of Filter!");
        }
        try {
            Constructor filterConstructor = filterClass.getConstructor(String.class);
            Filter filter = null;
            try {
                filter = (Filter) filterConstructor.newInstance(filterName);
            } catch (Throwable th) {
            }
            if (filter == null) {
                throw new IllegalArgumentException("Could not construct the filter '" + filterName + "'!");
            }
            return filter;
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("The filter class '" + filterClass + "' does not have a constructor of the form <init>(String name)!");
        }
    }
}
