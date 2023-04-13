package android.timezone;

import java.util.Objects;
/* loaded from: classes3.dex */
public final class ZoneInfoDb {
    private static ZoneInfoDb sInstance;
    private static final Object sLock = new Object();
    private final com.android.i18n.timezone.ZoneInfoDb mDelegate;

    public static ZoneInfoDb getInstance() {
        synchronized (sLock) {
            if (sInstance == null) {
                sInstance = new ZoneInfoDb(com.android.i18n.timezone.ZoneInfoDb.getInstance());
            }
        }
        return sInstance;
    }

    private ZoneInfoDb(com.android.i18n.timezone.ZoneInfoDb delegate) {
        this.mDelegate = (com.android.i18n.timezone.ZoneInfoDb) Objects.requireNonNull(delegate);
    }

    public String getVersion() {
        return this.mDelegate.getVersion();
    }
}
