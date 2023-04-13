package com.android.server.compat;

import com.android.internal.compat.IPlatformCompatNative;
/* loaded from: classes.dex */
public class PlatformCompatNative extends IPlatformCompatNative.Stub {
    public final PlatformCompat mPlatformCompat;

    public PlatformCompatNative(PlatformCompat platformCompat) {
        this.mPlatformCompat = platformCompat;
    }

    public void reportChangeByPackageName(long j, String str, int i) {
        this.mPlatformCompat.reportChangeByPackageName(j, str, i);
    }

    public void reportChangeByUid(long j, int i) {
        this.mPlatformCompat.reportChangeByUid(j, i);
    }

    public boolean isChangeEnabledByPackageName(long j, String str, int i) {
        return this.mPlatformCompat.isChangeEnabledByPackageName(j, str, i);
    }

    public boolean isChangeEnabledByUid(long j, int i) {
        return this.mPlatformCompat.isChangeEnabledByUid(j, i);
    }
}
