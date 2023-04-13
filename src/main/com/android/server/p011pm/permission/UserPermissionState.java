package com.android.server.p011pm.permission;

import android.os.UserHandle;
import android.util.ArraySet;
import android.util.SparseArray;
/* renamed from: com.android.server.pm.permission.UserPermissionState */
/* loaded from: classes2.dex */
public final class UserPermissionState {
    public final ArraySet<String> mInstallPermissionsFixed = new ArraySet<>();
    public final SparseArray<UidPermissionState> mUidStates = new SparseArray<>();

    public boolean areInstallPermissionsFixed(String str) {
        return this.mInstallPermissionsFixed.contains(str);
    }

    public void setInstallPermissionsFixed(String str, boolean z) {
        if (z) {
            this.mInstallPermissionsFixed.add(str);
        } else {
            this.mInstallPermissionsFixed.remove(str);
        }
    }

    public UidPermissionState getUidState(int i) {
        checkAppId(i);
        return this.mUidStates.get(i);
    }

    public UidPermissionState getOrCreateUidState(int i) {
        checkAppId(i);
        UidPermissionState uidPermissionState = this.mUidStates.get(i);
        if (uidPermissionState == null) {
            UidPermissionState uidPermissionState2 = new UidPermissionState();
            this.mUidStates.put(i, uidPermissionState2);
            return uidPermissionState2;
        }
        return uidPermissionState;
    }

    public UidPermissionState createUidStateWithExisting(int i, UidPermissionState uidPermissionState) {
        checkAppId(i);
        UidPermissionState uidPermissionState2 = new UidPermissionState(uidPermissionState);
        this.mUidStates.put(i, uidPermissionState2);
        return uidPermissionState2;
    }

    public void removeUidState(int i) {
        checkAppId(i);
        this.mUidStates.delete(i);
    }

    public final void checkAppId(int i) {
        if (UserHandle.getUserId(i) == 0) {
            return;
        }
        throw new IllegalArgumentException("Invalid app ID " + i);
    }
}
