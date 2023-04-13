package com.android.server.permission.access;

import android.content.pm.PermissionGroupInfo;
import android.util.ArrayMap;
import android.util.SparseArray;
import com.android.server.SystemConfig;
import com.android.server.p011pm.permission.PermissionAllowlist;
import com.android.server.p011pm.pkg.PackageState;
import com.android.server.permission.access.collection.IndexedListSet;
import com.android.server.permission.access.collection.IntSet;
import com.android.server.permission.access.permission.Permission;
import com.android.server.permission.jarjar.kotlin.collections.MapsKt__MapsKt;
import java.util.Map;
/* compiled from: AccessState.kt */
/* loaded from: classes2.dex */
public final class SystemState extends WritableState {
    public final SparseArray<IndexedListSet<String>> appIds;
    public Map<String, SystemConfig.PermissionEntry> configPermissions;
    public SparseArray<String> deviceAndProfileOwners;
    public Map<String, ? extends PackageState> disabledSystemPackageStates;
    public ArrayMap<String, IndexedListSet<String>> implicitToSourcePermissions;
    public boolean isLeanback;
    public boolean isSystemReady;
    public SparseArray<String[]> knownPackages;
    public Map<String, ? extends PackageState> packageStates;
    public PermissionAllowlist permissionAllowlist;
    public final ArrayMap<String, PermissionGroupInfo> permissionGroups;
    public final ArrayMap<String, Permission> permissionTrees;
    public final ArrayMap<String, Permission> permissions;
    public IndexedListSet<String> privilegedPermissionAllowlistPackages;
    public final IntSet userIds;

    public final IntSet getUserIds() {
        return this.userIds;
    }

    public final Map<String, PackageState> getPackageStates() {
        return this.packageStates;
    }

    public final void setPackageStates(Map<String, ? extends PackageState> map) {
        this.packageStates = map;
    }

    public final Map<String, PackageState> getDisabledSystemPackageStates() {
        return this.disabledSystemPackageStates;
    }

    public final void setDisabledSystemPackageStates(Map<String, ? extends PackageState> map) {
        this.disabledSystemPackageStates = map;
    }

    public final SparseArray<IndexedListSet<String>> getAppIds() {
        return this.appIds;
    }

    public final SparseArray<String[]> getKnownPackages() {
        return this.knownPackages;
    }

    public final void setKnownPackages(SparseArray<String[]> sparseArray) {
        this.knownPackages = sparseArray;
    }

    public final boolean isLeanback() {
        return this.isLeanback;
    }

    public final void setLeanback(boolean z) {
        this.isLeanback = z;
    }

    public final Map<String, SystemConfig.PermissionEntry> getConfigPermissions() {
        return this.configPermissions;
    }

    public final void setConfigPermissions(Map<String, SystemConfig.PermissionEntry> map) {
        this.configPermissions = map;
    }

    public final IndexedListSet<String> getPrivilegedPermissionAllowlistPackages() {
        return this.privilegedPermissionAllowlistPackages;
    }

    public final void setPrivilegedPermissionAllowlistPackages(IndexedListSet<String> indexedListSet) {
        this.privilegedPermissionAllowlistPackages = indexedListSet;
    }

    public final PermissionAllowlist getPermissionAllowlist() {
        return this.permissionAllowlist;
    }

    public final void setPermissionAllowlist(PermissionAllowlist permissionAllowlist) {
        this.permissionAllowlist = permissionAllowlist;
    }

    public final ArrayMap<String, IndexedListSet<String>> getImplicitToSourcePermissions() {
        return this.implicitToSourcePermissions;
    }

    public final void setImplicitToSourcePermissions(ArrayMap<String, IndexedListSet<String>> arrayMap) {
        this.implicitToSourcePermissions = arrayMap;
    }

    public final boolean isSystemReady() {
        return this.isSystemReady;
    }

    public final void setSystemReady(boolean z) {
        this.isSystemReady = z;
    }

    public final SparseArray<String> getDeviceAndProfileOwners() {
        return this.deviceAndProfileOwners;
    }

    public final ArrayMap<String, PermissionGroupInfo> getPermissionGroups() {
        return this.permissionGroups;
    }

    public final ArrayMap<String, Permission> getPermissionTrees() {
        return this.permissionTrees;
    }

    public final ArrayMap<String, Permission> getPermissions() {
        return this.permissions;
    }

    public SystemState(IntSet intSet, Map<String, ? extends PackageState> map, Map<String, ? extends PackageState> map2, SparseArray<IndexedListSet<String>> sparseArray, SparseArray<String[]> sparseArray2, boolean z, Map<String, SystemConfig.PermissionEntry> map3, IndexedListSet<String> indexedListSet, PermissionAllowlist permissionAllowlist, ArrayMap<String, IndexedListSet<String>> arrayMap, boolean z2, SparseArray<String> sparseArray3, ArrayMap<String, PermissionGroupInfo> arrayMap2, ArrayMap<String, Permission> arrayMap3, ArrayMap<String, Permission> arrayMap4) {
        this.userIds = intSet;
        this.packageStates = map;
        this.disabledSystemPackageStates = map2;
        this.appIds = sparseArray;
        this.knownPackages = sparseArray2;
        this.isLeanback = z;
        this.configPermissions = map3;
        this.privilegedPermissionAllowlistPackages = indexedListSet;
        this.permissionAllowlist = permissionAllowlist;
        this.implicitToSourcePermissions = arrayMap;
        this.isSystemReady = z2;
        this.deviceAndProfileOwners = sparseArray3;
        this.permissionGroups = arrayMap2;
        this.permissionTrees = arrayMap3;
        this.permissions = arrayMap4;
    }

    public SystemState() {
        this(new IntSet(), MapsKt__MapsKt.emptyMap(), MapsKt__MapsKt.emptyMap(), new SparseArray(), new SparseArray(), false, MapsKt__MapsKt.emptyMap(), new IndexedListSet(), new PermissionAllowlist(), new ArrayMap(), false, new SparseArray(), new ArrayMap(), new ArrayMap(), new ArrayMap());
    }

    public final SystemState copy() {
        IntSet copy = this.userIds.copy();
        Map<String, ? extends PackageState> map = this.packageStates;
        Map<String, ? extends PackageState> map2 = this.disabledSystemPackageStates;
        SparseArray<IndexedListSet<String>> clone = this.appIds.clone();
        int size = clone.size();
        for (int i = 0; i < size; i++) {
            clone.setValueAt(i, clone.valueAt(i).copy());
        }
        SparseArray<String[]> sparseArray = this.knownPackages;
        boolean z = this.isLeanback;
        Map<String, SystemConfig.PermissionEntry> map3 = this.configPermissions;
        IndexedListSet<String> indexedListSet = this.privilegedPermissionAllowlistPackages;
        PermissionAllowlist permissionAllowlist = this.permissionAllowlist;
        ArrayMap<String, IndexedListSet<String>> arrayMap = this.implicitToSourcePermissions;
        boolean z2 = this.isSystemReady;
        SparseArray<String> sparseArray2 = this.deviceAndProfileOwners;
        ArrayMap arrayMap2 = new ArrayMap(this.permissionGroups);
        int i2 = 0;
        for (int size2 = arrayMap2.size(); i2 < size2; size2 = size2) {
            arrayMap2.setValueAt(i2, (PermissionGroupInfo) arrayMap2.valueAt(i2));
            i2++;
        }
        ArrayMap arrayMap3 = new ArrayMap(this.permissionTrees);
        int i3 = 0;
        for (int size3 = arrayMap3.size(); i3 < size3; size3 = size3) {
            arrayMap3.setValueAt(i3, (Permission) arrayMap3.valueAt(i3));
            i3++;
        }
        ArrayMap arrayMap4 = new ArrayMap(this.permissions);
        int i4 = 0;
        for (int size4 = arrayMap4.size(); i4 < size4; size4 = size4) {
            arrayMap4.setValueAt(i4, (Permission) arrayMap4.valueAt(i4));
            i4++;
        }
        return new SystemState(copy, map, map2, clone, sparseArray, z, map3, indexedListSet, permissionAllowlist, arrayMap, z2, sparseArray2, arrayMap2, arrayMap3, arrayMap4);
    }
}
