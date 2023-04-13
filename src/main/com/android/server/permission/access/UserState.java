package com.android.server.permission.access;

import android.util.ArrayMap;
import android.util.SparseArray;
/* compiled from: AccessState.kt */
/* loaded from: classes2.dex */
public final class UserState extends WritableState {
    public final ArrayMap<String, ArrayMap<String, Integer>> packageAppOpModes;
    public final SparseArray<ArrayMap<String, Integer>> uidAppOpModes;
    public final SparseArray<ArrayMap<String, Integer>> uidPermissionFlags;

    public final SparseArray<ArrayMap<String, Integer>> getUidPermissionFlags() {
        return this.uidPermissionFlags;
    }

    public final SparseArray<ArrayMap<String, Integer>> getUidAppOpModes() {
        return this.uidAppOpModes;
    }

    public final ArrayMap<String, ArrayMap<String, Integer>> getPackageAppOpModes() {
        return this.packageAppOpModes;
    }

    public UserState(SparseArray<ArrayMap<String, Integer>> sparseArray, SparseArray<ArrayMap<String, Integer>> sparseArray2, ArrayMap<String, ArrayMap<String, Integer>> arrayMap) {
        this.uidPermissionFlags = sparseArray;
        this.uidAppOpModes = sparseArray2;
        this.packageAppOpModes = arrayMap;
    }

    public UserState() {
        this(new SparseArray(), new SparseArray(), new ArrayMap());
    }

    public final UserState copy() {
        SparseArray<ArrayMap<String, Integer>> clone = this.uidPermissionFlags.clone();
        int size = clone.size();
        for (int i = 0; i < size; i++) {
            ArrayMap<String, Integer> arrayMap = new ArrayMap<>(clone.valueAt(i));
            int size2 = arrayMap.size();
            for (int i2 = 0; i2 < size2; i2++) {
                arrayMap.setValueAt(i2, Integer.valueOf(arrayMap.valueAt(i2).intValue()));
            }
            clone.setValueAt(i, arrayMap);
        }
        SparseArray<ArrayMap<String, Integer>> clone2 = this.uidAppOpModes.clone();
        int size3 = clone2.size();
        for (int i3 = 0; i3 < size3; i3++) {
            ArrayMap<String, Integer> arrayMap2 = new ArrayMap<>(clone2.valueAt(i3));
            int size4 = arrayMap2.size();
            for (int i4 = 0; i4 < size4; i4++) {
                arrayMap2.setValueAt(i4, Integer.valueOf(arrayMap2.valueAt(i4).intValue()));
            }
            clone2.setValueAt(i3, arrayMap2);
        }
        ArrayMap arrayMap3 = new ArrayMap(this.packageAppOpModes);
        int size5 = arrayMap3.size();
        for (int i5 = 0; i5 < size5; i5++) {
            ArrayMap arrayMap4 = new ArrayMap((ArrayMap) arrayMap3.valueAt(i5));
            int size6 = arrayMap4.size();
            for (int i6 = 0; i6 < size6; i6++) {
                arrayMap4.setValueAt(i6, Integer.valueOf(((Number) arrayMap4.valueAt(i6)).intValue()));
            }
            arrayMap3.setValueAt(i5, arrayMap4);
        }
        return new UserState(clone, clone2, arrayMap3);
    }
}
