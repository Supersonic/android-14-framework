package com.android.server.oemlock;
/* loaded from: classes2.dex */
public abstract class OemLock {
    public abstract String getLockName();

    public abstract boolean isOemUnlockAllowedByCarrier();

    public abstract boolean isOemUnlockAllowedByDevice();

    public abstract void setOemUnlockAllowedByCarrier(boolean z, byte[] bArr);

    public abstract void setOemUnlockAllowedByDevice(boolean z);
}
