package com.android.server.p011pm.permission;

import com.android.internal.annotations.GuardedBy;
/* renamed from: com.android.server.pm.permission.PermissionState */
/* loaded from: classes2.dex */
public final class PermissionState {
    @GuardedBy({"mLock"})
    public int mFlags;
    @GuardedBy({"mLock"})
    public boolean mGranted;
    public final Object mLock;
    public final Permission mPermission;

    public PermissionState(Permission permission) {
        this.mLock = new Object();
        this.mPermission = permission;
    }

    public PermissionState(PermissionState permissionState) {
        this(permissionState.mPermission);
        this.mGranted = permissionState.mGranted;
        this.mFlags = permissionState.mFlags;
    }

    public Permission getPermission() {
        return this.mPermission;
    }

    public String getName() {
        return this.mPermission.getName();
    }

    public int[] computeGids(int i) {
        return this.mPermission.computeGids(i);
    }

    public boolean isGranted() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mGranted;
        }
        return z;
    }

    public boolean grant() {
        synchronized (this.mLock) {
            if (this.mGranted) {
                return false;
            }
            this.mGranted = true;
            UidPermissionState.invalidateCache();
            return true;
        }
    }

    public boolean revoke() {
        synchronized (this.mLock) {
            if (this.mGranted) {
                this.mGranted = false;
                UidPermissionState.invalidateCache();
                return true;
            }
            return false;
        }
    }

    public int getFlags() {
        int i;
        synchronized (this.mLock) {
            i = this.mFlags;
        }
        return i;
    }

    public boolean updateFlags(int i, int i2) {
        boolean z;
        synchronized (this.mLock) {
            int i3 = i2 & i;
            UidPermissionState.invalidateCache();
            int i4 = this.mFlags;
            int i5 = ((~i) & i4) | i3;
            this.mFlags = i5;
            z = i5 != i4;
        }
        return z;
    }

    public boolean isDefault() {
        boolean z;
        synchronized (this.mLock) {
            z = !this.mGranted && this.mFlags == 0;
        }
        return z;
    }
}
