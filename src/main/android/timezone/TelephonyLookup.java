package android.timezone;

import java.util.Objects;
/* loaded from: classes3.dex */
public final class TelephonyLookup {
    private static TelephonyLookup sInstance;
    private static final Object sLock = new Object();
    private final com.android.i18n.timezone.TelephonyLookup mDelegate;

    public static TelephonyLookup getInstance() {
        TelephonyLookup telephonyLookup;
        synchronized (sLock) {
            if (sInstance == null) {
                sInstance = new TelephonyLookup(com.android.i18n.timezone.TelephonyLookup.getInstance());
            }
            telephonyLookup = sInstance;
        }
        return telephonyLookup;
    }

    private TelephonyLookup(com.android.i18n.timezone.TelephonyLookup delegate) {
        this.mDelegate = (com.android.i18n.timezone.TelephonyLookup) Objects.requireNonNull(delegate);
    }

    public TelephonyNetworkFinder getTelephonyNetworkFinder() {
        com.android.i18n.timezone.TelephonyNetworkFinder telephonyNetworkFinderDelegate = this.mDelegate.getTelephonyNetworkFinder();
        if (telephonyNetworkFinderDelegate != null) {
            return new TelephonyNetworkFinder(telephonyNetworkFinderDelegate);
        }
        return null;
    }
}
