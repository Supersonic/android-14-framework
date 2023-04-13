package com.android.internal.policy;

import android.content.Context;
import android.content.p001pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.p008os.UserHandle;
import android.util.ArrayMap;
import android.util.LruCache;
import android.util.SparseArray;
/* loaded from: classes4.dex */
public final class AttributeCache {
    private static final int CACHE_SIZE = 4;
    private static AttributeCache sInstance = null;
    private final Context mContext;
    private final LruCache<String, Package> mPackages = new LruCache<>(4);
    private final Configuration mConfiguration = new Configuration();

    /* loaded from: classes4.dex */
    public static final class Package {
        public final Context context;
        private final SparseArray<ArrayMap<int[], Entry>> mMap = new SparseArray<>();

        public Package(Context c) {
            this.context = c;
        }
    }

    /* loaded from: classes4.dex */
    public static final class Entry {
        public final TypedArray array;
        public final Context context;

        public Entry(Context c, TypedArray ta) {
            this.context = c;
            this.array = ta;
        }

        void recycle() {
            TypedArray typedArray = this.array;
            if (typedArray != null) {
                typedArray.recycle();
            }
        }
    }

    public static void init(Context context) {
        if (sInstance == null) {
            sInstance = new AttributeCache(context);
        }
    }

    public static AttributeCache instance() {
        return sInstance;
    }

    public AttributeCache(Context context) {
        this.mContext = context;
    }

    public void removePackage(String packageName) {
        synchronized (this) {
            Package pkg = this.mPackages.remove(packageName);
            if (pkg != null) {
                for (int i = 0; i < pkg.mMap.size(); i++) {
                    ArrayMap<int[], Entry> map = (ArrayMap) pkg.mMap.valueAt(i);
                    for (int j = 0; j < map.size(); j++) {
                        map.valueAt(j).recycle();
                    }
                }
                Resources res = pkg.context.getResources();
                res.flushLayoutCache();
            }
        }
    }

    public void updateConfiguration(Configuration config) {
        synchronized (this) {
            int changes = this.mConfiguration.updateFrom(config);
            if (((-1073741985) & changes) != 0) {
                this.mPackages.evictAll();
            }
        }
    }

    public Entry get(String packageName, int resId, int[] styleable) {
        return get(packageName, resId, styleable, -2);
    }

    public Entry get(String packageName, int resId, int[] styleable, int userId) {
        Entry ent;
        synchronized (this) {
            Package pkg = this.mPackages.get(packageName);
            ArrayMap<int[], Entry> map = null;
            if (pkg != null) {
                map = (ArrayMap) pkg.mMap.get(resId);
                if (map != null && (ent = map.get(styleable)) != null) {
                    return ent;
                }
            } else {
                try {
                    Context context = this.mContext.createPackageContextAsUser(packageName, 0, new UserHandle(userId));
                    if (context == null) {
                        return null;
                    }
                    pkg = new Package(context);
                    this.mPackages.put(packageName, pkg);
                } catch (PackageManager.NameNotFoundException e) {
                    return null;
                }
            }
            if (map == null) {
                map = new ArrayMap<>();
                pkg.mMap.put(resId, map);
            }
            try {
                Entry ent2 = new Entry(pkg.context, pkg.context.obtainStyledAttributes(resId, styleable));
                map.put(styleable, ent2);
                return ent2;
            } catch (Resources.NotFoundException e2) {
                return null;
            }
        }
    }
}
