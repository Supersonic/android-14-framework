package com.android.server.p011pm.permission;

import android.content.pm.PackageManager;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.IntArray;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
/* renamed from: com.android.server.pm.permission.UidPermissionState */
/* loaded from: classes2.dex */
public final class UidPermissionState {
    public boolean mMissing;
    public ArrayMap<String, PermissionState> mPermissions;

    public UidPermissionState() {
    }

    public UidPermissionState(UidPermissionState uidPermissionState) {
        this.mMissing = uidPermissionState.mMissing;
        if (uidPermissionState.mPermissions != null) {
            this.mPermissions = new ArrayMap<>();
            int size = uidPermissionState.mPermissions.size();
            for (int i = 0; i < size; i++) {
                this.mPermissions.put(uidPermissionState.mPermissions.keyAt(i), new PermissionState(uidPermissionState.mPermissions.valueAt(i)));
            }
        }
    }

    public void reset() {
        this.mMissing = false;
        this.mPermissions = null;
        invalidateCache();
    }

    public boolean isMissing() {
        return this.mMissing;
    }

    public void setMissing(boolean z) {
        this.mMissing = z;
    }

    @Deprecated
    public boolean hasPermissionState(String str) {
        ArrayMap<String, PermissionState> arrayMap = this.mPermissions;
        return arrayMap != null && arrayMap.containsKey(str);
    }

    @Deprecated
    public boolean hasPermissionState(ArraySet<String> arraySet) {
        if (this.mPermissions == null) {
            return false;
        }
        int size = arraySet.size();
        for (int i = 0; i < size; i++) {
            if (this.mPermissions.containsKey(arraySet.valueAt(i))) {
                return true;
            }
        }
        return false;
    }

    public PermissionState getPermissionState(String str) {
        ArrayMap<String, PermissionState> arrayMap = this.mPermissions;
        if (arrayMap == null) {
            return null;
        }
        return arrayMap.get(str);
    }

    public final PermissionState getOrCreatePermissionState(Permission permission) {
        if (this.mPermissions == null) {
            this.mPermissions = new ArrayMap<>();
        }
        String name = permission.getName();
        PermissionState permissionState = this.mPermissions.get(name);
        if (permissionState == null) {
            PermissionState permissionState2 = new PermissionState(permission);
            this.mPermissions.put(name, permissionState2);
            return permissionState2;
        }
        return permissionState;
    }

    public List<PermissionState> getPermissionStates() {
        if (this.mPermissions == null) {
            return Collections.emptyList();
        }
        return new ArrayList(this.mPermissions.values());
    }

    public void putPermissionState(Permission permission, boolean z, int i) {
        String name = permission.getName();
        ArrayMap<String, PermissionState> arrayMap = this.mPermissions;
        if (arrayMap == null) {
            this.mPermissions = new ArrayMap<>();
        } else {
            arrayMap.remove(name);
        }
        PermissionState permissionState = new PermissionState(permission);
        if (z) {
            permissionState.grant();
        }
        permissionState.updateFlags(i, i);
        this.mPermissions.put(name, permissionState);
    }

    public boolean removePermissionState(String str) {
        ArrayMap<String, PermissionState> arrayMap = this.mPermissions;
        if (arrayMap == null) {
            return false;
        }
        boolean z = arrayMap.remove(str) != null;
        if (z && this.mPermissions.isEmpty()) {
            this.mPermissions = null;
        }
        return z;
    }

    public boolean isPermissionGranted(String str) {
        PermissionState permissionState = getPermissionState(str);
        return permissionState != null && permissionState.isGranted();
    }

    public Set<String> getGrantedPermissions() {
        if (this.mPermissions == null) {
            return Collections.emptySet();
        }
        ArraySet arraySet = new ArraySet(this.mPermissions.size());
        int size = this.mPermissions.size();
        for (int i = 0; i < size; i++) {
            PermissionState valueAt = this.mPermissions.valueAt(i);
            if (valueAt.isGranted()) {
                arraySet.add(valueAt.getName());
            }
        }
        return arraySet;
    }

    public boolean grantPermission(Permission permission) {
        return getOrCreatePermissionState(permission).grant();
    }

    public boolean revokePermission(Permission permission) {
        String name = permission.getName();
        PermissionState permissionState = getPermissionState(name);
        if (permissionState == null) {
            return false;
        }
        boolean revoke = permissionState.revoke();
        if (revoke && permissionState.isDefault()) {
            removePermissionState(name);
        }
        return revoke;
    }

    public int getPermissionFlags(String str) {
        PermissionState permissionState = getPermissionState(str);
        if (permissionState == null) {
            return 0;
        }
        return permissionState.getFlags();
    }

    public boolean updatePermissionFlags(Permission permission, int i, int i2) {
        if (i == 0) {
            return false;
        }
        PermissionState orCreatePermissionState = getOrCreatePermissionState(permission);
        boolean updateFlags = orCreatePermissionState.updateFlags(i, i2);
        if (updateFlags && orCreatePermissionState.isDefault()) {
            removePermissionState(permission.getName());
        }
        return updateFlags;
    }

    public boolean updatePermissionFlagsForAllPermissions(int i, int i2) {
        ArrayMap<String, PermissionState> arrayMap;
        boolean z = false;
        if (i == 0 || (arrayMap = this.mPermissions) == null) {
            return false;
        }
        for (int size = arrayMap.size() - 1; size >= 0; size--) {
            PermissionState valueAt = this.mPermissions.valueAt(size);
            boolean updateFlags = valueAt.updateFlags(i, i2);
            if (updateFlags && valueAt.isDefault()) {
                this.mPermissions.removeAt(size);
            }
            z |= updateFlags;
        }
        return z;
    }

    public boolean isPermissionsReviewRequired() {
        ArrayMap<String, PermissionState> arrayMap = this.mPermissions;
        if (arrayMap == null) {
            return false;
        }
        int size = arrayMap.size();
        for (int i = 0; i < size; i++) {
            if ((this.mPermissions.valueAt(i).getFlags() & 64) != 0) {
                return true;
            }
        }
        return false;
    }

    public int[] computeGids(int[] iArr, int i) {
        IntArray wrap = IntArray.wrap(iArr);
        ArrayMap<String, PermissionState> arrayMap = this.mPermissions;
        if (arrayMap == null) {
            return wrap.toArray();
        }
        int size = arrayMap.size();
        for (int i2 = 0; i2 < size; i2++) {
            PermissionState valueAt = this.mPermissions.valueAt(i2);
            if (valueAt.isGranted()) {
                int[] computeGids = valueAt.computeGids(i);
                if (computeGids.length != 0) {
                    wrap.addAll(computeGids);
                }
            }
        }
        return wrap.toArray();
    }

    public static void invalidateCache() {
        PackageManager.invalidatePackageInfoCache();
    }
}
