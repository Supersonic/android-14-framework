package com.android.server.appop;

import android.os.RemoteException;
/* loaded from: classes.dex */
public abstract class OnOpModeChangedListener {
    public int mCallingPid;
    public int mCallingUid;
    public int mFlags;
    public int mWatchedOpCode;
    public int mWatchingUid;

    public abstract void onOpModeChanged(int i, int i2, String str) throws RemoteException;

    public abstract String toString();

    public OnOpModeChangedListener(int i, int i2, int i3, int i4, int i5) {
        this.mWatchingUid = i;
        this.mFlags = i2;
        this.mWatchedOpCode = i3;
        this.mCallingUid = i4;
        this.mCallingPid = i5;
    }

    public int getWatchingUid() {
        return this.mWatchingUid;
    }

    public int getFlags() {
        return this.mFlags;
    }

    public int getWatchedOpCode() {
        return this.mWatchedOpCode;
    }

    public int getCallingUid() {
        return this.mCallingUid;
    }

    public int getCallingPid() {
        return this.mCallingPid;
    }

    public boolean isWatchingUid(int i) {
        int i2;
        return i == -2 || (i2 = this.mWatchingUid) < 0 || i2 == i;
    }
}
