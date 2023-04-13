package com.android.server.devicestate;

import android.os.IBinder;
/* loaded from: classes.dex */
public final class OverrideRequest {
    public final int mFlags;
    public final int mPid;
    public final int mRequestType;
    public final int mRequestedState;
    public final IBinder mToken;
    public final int mUid;

    public OverrideRequest(IBinder iBinder, int i, int i2, int i3, int i4, int i5) {
        this.mToken = iBinder;
        this.mPid = i;
        this.mUid = i2;
        this.mRequestedState = i3;
        this.mFlags = i4;
        this.mRequestType = i5;
    }

    public IBinder getToken() {
        return this.mToken;
    }

    public int getPid() {
        return this.mPid;
    }

    public int getUid() {
        return this.mUid;
    }

    public int getRequestedState() {
        return this.mRequestedState;
    }

    public int getFlags() {
        return this.mFlags;
    }

    public int getRequestType() {
        return this.mRequestType;
    }
}
