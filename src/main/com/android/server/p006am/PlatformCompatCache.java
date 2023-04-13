package com.android.server.p006am;

import android.content.pm.ApplicationInfo;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.ArrayMap;
import android.util.LongSparseArray;
import android.util.Pair;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.compat.IPlatformCompat;
import com.android.server.compat.CompatChange;
import com.android.server.compat.PlatformCompat;
import java.lang.ref.WeakReference;
/* renamed from: com.android.server.am.PlatformCompatCache */
/* loaded from: classes.dex */
public final class PlatformCompatCache {
    public static final long[] CACHED_COMPAT_CHANGE_IDS_MAPPING = {136274596, 136219221, 183972877};
    public static PlatformCompatCache sPlatformCompatCache;
    public final boolean mCacheEnabled;
    public final LongSparseArray<CacheItem> mCaches = new LongSparseArray<>();
    public final IPlatformCompat mIPlatformCompatProxy;
    public final PlatformCompat mPlatformCompat;

    public PlatformCompatCache(long[] jArr) {
        IBinder service = ServiceManager.getService("platform_compat");
        if (service instanceof PlatformCompat) {
            this.mPlatformCompat = (PlatformCompat) ServiceManager.getService("platform_compat");
            for (long j : jArr) {
                this.mCaches.put(j, new CacheItem(this.mPlatformCompat, j));
            }
            this.mIPlatformCompatProxy = null;
            this.mCacheEnabled = true;
            return;
        }
        this.mIPlatformCompatProxy = IPlatformCompat.Stub.asInterface(service);
        this.mPlatformCompat = null;
        this.mCacheEnabled = false;
    }

    public static PlatformCompatCache getInstance() {
        if (sPlatformCompatCache == null) {
            sPlatformCompatCache = new PlatformCompatCache(new long[]{136274596, 136219221, 183972877});
        }
        return sPlatformCompatCache;
    }

    public final boolean isChangeEnabled(long j, ApplicationInfo applicationInfo, boolean z) {
        try {
            return this.mCacheEnabled ? this.mCaches.get(j).isChangeEnabled(applicationInfo) : this.mIPlatformCompatProxy.isChangeEnabled(j, applicationInfo);
        } catch (RemoteException e) {
            Slog.w("ActivityManager", "Error reading platform compat change " + j, e);
            return z;
        }
    }

    public static boolean isChangeEnabled(int i, ApplicationInfo applicationInfo, boolean z) {
        return getInstance().isChangeEnabled(CACHED_COMPAT_CHANGE_IDS_MAPPING[i], applicationInfo, z);
    }

    public void invalidate(ApplicationInfo applicationInfo) {
        for (int size = this.mCaches.size() - 1; size >= 0; size--) {
            this.mCaches.valueAt(size).invalidate(applicationInfo);
        }
    }

    public void onApplicationInfoChanged(ApplicationInfo applicationInfo) {
        for (int size = this.mCaches.size() - 1; size >= 0; size--) {
            this.mCaches.valueAt(size).onApplicationInfoChanged(applicationInfo);
        }
    }

    /* renamed from: com.android.server.am.PlatformCompatCache$CacheItem */
    /* loaded from: classes.dex */
    public static class CacheItem implements CompatChange.ChangeListener {
        public final long mChangeId;
        public final PlatformCompat mPlatformCompat;
        public final Object mLock = new Object();
        public final ArrayMap<String, Pair<Boolean, WeakReference<ApplicationInfo>>> mCache = new ArrayMap<>();

        public CacheItem(PlatformCompat platformCompat, long j) {
            this.mPlatformCompat = platformCompat;
            this.mChangeId = j;
            platformCompat.registerListener(j, this);
        }

        public boolean isChangeEnabled(ApplicationInfo applicationInfo) {
            synchronized (this.mLock) {
                int indexOfKey = this.mCache.indexOfKey(applicationInfo.packageName);
                if (indexOfKey < 0) {
                    return fetchLocked(applicationInfo, indexOfKey);
                }
                Pair<Boolean, WeakReference<ApplicationInfo>> valueAt = this.mCache.valueAt(indexOfKey);
                if (((WeakReference) valueAt.second).get() == applicationInfo) {
                    return ((Boolean) valueAt.first).booleanValue();
                }
                return fetchLocked(applicationInfo, indexOfKey);
            }
        }

        public void invalidate(ApplicationInfo applicationInfo) {
            synchronized (this.mLock) {
                this.mCache.remove(applicationInfo.packageName);
            }
        }

        @GuardedBy({"mLock"})
        public boolean fetchLocked(ApplicationInfo applicationInfo, int i) {
            Pair<Boolean, WeakReference<ApplicationInfo>> pair = new Pair<>(Boolean.valueOf(this.mPlatformCompat.isChangeEnabledInternalNoLogging(this.mChangeId, applicationInfo)), new WeakReference(applicationInfo));
            if (i >= 0) {
                this.mCache.setValueAt(i, pair);
            } else {
                this.mCache.put(applicationInfo.packageName, pair);
            }
            return ((Boolean) pair.first).booleanValue();
        }

        public void onApplicationInfoChanged(ApplicationInfo applicationInfo) {
            synchronized (this.mLock) {
                int indexOfKey = this.mCache.indexOfKey(applicationInfo.packageName);
                if (indexOfKey >= 0) {
                    fetchLocked(applicationInfo, indexOfKey);
                }
            }
        }

        @Override // com.android.server.compat.CompatChange.ChangeListener
        public void onCompatChange(String str) {
            synchronized (this.mLock) {
                int indexOfKey = this.mCache.indexOfKey(str);
                if (indexOfKey >= 0) {
                    ApplicationInfo applicationInfo = (ApplicationInfo) ((WeakReference) this.mCache.valueAt(indexOfKey).second).get();
                    if (applicationInfo != null) {
                        fetchLocked(applicationInfo, indexOfKey);
                    } else {
                        this.mCache.removeAt(indexOfKey);
                    }
                }
            }
        }
    }
}
