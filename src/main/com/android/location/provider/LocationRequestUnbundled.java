package com.android.location.provider;

import android.location.LocationRequest;
@Deprecated
/* loaded from: classes.dex */
public final class LocationRequestUnbundled {
    @Deprecated
    public static final int ACCURACY_BLOCK = 102;
    @Deprecated
    public static final int ACCURACY_CITY = 104;
    @Deprecated
    public static final int ACCURACY_FINE = 100;
    @Deprecated
    public static final int POWER_HIGH = 203;
    @Deprecated
    public static final int POWER_LOW = 201;
    @Deprecated
    public static final int POWER_NONE = 200;
    private final LocationRequest delegate;

    /* JADX INFO: Access modifiers changed from: package-private */
    public LocationRequestUnbundled(LocationRequest delegate) {
        this.delegate = delegate;
    }

    public long getInterval() {
        return this.delegate.getIntervalMillis();
    }

    public long getFastestInterval() {
        return this.delegate.getMinUpdateIntervalMillis();
    }

    public int getQuality() {
        return this.delegate.getQuality();
    }

    public float getSmallestDisplacement() {
        return this.delegate.getMinUpdateDistanceMeters();
    }

    public boolean isLocationSettingsIgnored() {
        return this.delegate.isLocationSettingsIgnored();
    }

    public String toString() {
        return this.delegate.toString();
    }
}
