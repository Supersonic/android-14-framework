package com.android.server.biometrics.sensors;

import android.util.Slog;
import android.util.SparseIntArray;
/* loaded from: classes.dex */
public class LockoutCache implements LockoutTracker {
    public final SparseIntArray mUserLockoutStates = new SparseIntArray();

    public void setLockoutModeForUser(int i, int i2) {
        Slog.d("LockoutCache", "Lockout for user: " + i + " is " + i2);
        synchronized (this) {
            this.mUserLockoutStates.put(i, i2);
        }
    }

    @Override // com.android.server.biometrics.sensors.LockoutTracker
    public int getLockoutModeForUser(int i) {
        int i2;
        synchronized (this) {
            i2 = this.mUserLockoutStates.get(i, 0);
        }
        return i2;
    }
}
