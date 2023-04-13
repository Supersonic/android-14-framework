package com.android.server.p011pm.permission;

import android.util.SparseArray;
/* renamed from: com.android.server.pm.permission.DevicePermissionState */
/* loaded from: classes2.dex */
public final class DevicePermissionState {
    public final SparseArray<UserPermissionState> mUserStates = new SparseArray<>();

    public UserPermissionState getUserState(int i) {
        return this.mUserStates.get(i);
    }

    public UserPermissionState getOrCreateUserState(int i) {
        UserPermissionState userPermissionState = this.mUserStates.get(i);
        if (userPermissionState == null) {
            UserPermissionState userPermissionState2 = new UserPermissionState();
            this.mUserStates.put(i, userPermissionState2);
            return userPermissionState2;
        }
        return userPermissionState;
    }

    public void removeUserState(int i) {
        this.mUserStates.delete(i);
    }

    public int[] getUserIds() {
        int size = this.mUserStates.size();
        int[] iArr = new int[size];
        for (int i = 0; i < size; i++) {
            iArr[i] = this.mUserStates.keyAt(i);
        }
        return iArr;
    }
}
