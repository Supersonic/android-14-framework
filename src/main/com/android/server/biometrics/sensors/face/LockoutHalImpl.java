package com.android.server.biometrics.sensors.face;

import com.android.server.biometrics.sensors.LockoutTracker;
/* loaded from: classes.dex */
public class LockoutHalImpl implements LockoutTracker {
    public int mCurrentUserLockoutMode;

    @Override // com.android.server.biometrics.sensors.LockoutTracker
    public int getLockoutModeForUser(int i) {
        return this.mCurrentUserLockoutMode;
    }

    public void setCurrentUserLockoutMode(int i) {
        this.mCurrentUserLockoutMode = i;
    }
}
