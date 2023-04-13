package android.app.compat;

import android.app.PropertyInvalidatedCache;
import android.content.Context;
import android.p008os.Binder;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import com.android.internal.compat.IPlatformCompat;
/* loaded from: classes.dex */
public final class ChangeIdStateCache extends PropertyInvalidatedCache<ChangeIdStateQuery, Boolean> {
    private static final String CACHE_KEY = "cache_key.is_compat_change_enabled";
    private static final int MAX_ENTRIES = 64;
    private static boolean sDisabled = false;
    private volatile IPlatformCompat mPlatformCompat;

    public ChangeIdStateCache() {
        super(64, CACHE_KEY);
    }

    public static void disable() {
        sDisabled = true;
    }

    public static void invalidate() {
        if (!sDisabled) {
            PropertyInvalidatedCache.invalidateCache(CACHE_KEY);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public IPlatformCompat getPlatformCompatService() {
        IPlatformCompat platformCompat = this.mPlatformCompat;
        if (platformCompat == null) {
            synchronized (this) {
                platformCompat = this.mPlatformCompat;
                if (platformCompat == null) {
                    platformCompat = IPlatformCompat.Stub.asInterface(ServiceManager.getService(Context.PLATFORM_COMPAT_SERVICE));
                    if (platformCompat == null) {
                        throw new RuntimeException("Could not get PlatformCompatService instance!");
                    }
                    this.mPlatformCompat = platformCompat;
                }
            }
        }
        return platformCompat;
    }

    @Override // android.app.PropertyInvalidatedCache
    public Boolean recompute(ChangeIdStateQuery query) {
        long token = Binder.clearCallingIdentity();
        try {
            try {
                if (query.type == 0) {
                    return Boolean.valueOf(getPlatformCompatService().isChangeEnabledByPackageName(query.changeId, query.packageName, query.userId));
                }
                if (query.type == 1) {
                    return Boolean.valueOf(getPlatformCompatService().isChangeEnabledByUid(query.changeId, query.uid));
                }
                throw new IllegalArgumentException("Invalid query type: " + query.type);
            } catch (RemoteException e) {
                e.rethrowFromSystemServer();
                Binder.restoreCallingIdentity(token);
                throw new IllegalStateException("Could not recompute value!");
            }
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }
}
