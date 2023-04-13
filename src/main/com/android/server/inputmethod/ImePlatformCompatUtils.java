package com.android.server.inputmethod;

import android.os.RemoteException;
import android.os.ServiceManager;
import com.android.internal.compat.IPlatformCompat;
/* loaded from: classes.dex */
public final class ImePlatformCompatUtils {
    public final IPlatformCompat mPlatformCompat = IPlatformCompat.Stub.asInterface(ServiceManager.getService("platform_compat"));

    public boolean shouldUseSetInteractiveProtocol(int i) {
        return isChangeEnabledByUid(156215187L, i);
    }

    public boolean shouldClearShowForcedFlag(int i) {
        return isChangeEnabledByUid(214016041L, i);
    }

    public final boolean isChangeEnabledByUid(long j, int i) {
        try {
            return this.mPlatformCompat.isChangeEnabledByUid(j, i);
        } catch (RemoteException unused) {
            return false;
        }
    }
}
