package com.android.server.p011pm.permission;

import android.util.ArrayMap;
/* renamed from: com.android.server.pm.permission.PermissionAllowlist */
/* loaded from: classes2.dex */
public final class PermissionAllowlist {
    public final ArrayMap<String, ArrayMap<String, Boolean>> mOemAppAllowlist = new ArrayMap<>();
    public final ArrayMap<String, ArrayMap<String, Boolean>> mPrivilegedAppAllowlist = new ArrayMap<>();
    public final ArrayMap<String, ArrayMap<String, Boolean>> mVendorPrivilegedAppAllowlist = new ArrayMap<>();
    public final ArrayMap<String, ArrayMap<String, Boolean>> mProductPrivilegedAppAllowlist = new ArrayMap<>();
    public final ArrayMap<String, ArrayMap<String, Boolean>> mSystemExtPrivilegedAppAllowlist = new ArrayMap<>();
    public final ArrayMap<String, ArrayMap<String, ArrayMap<String, Boolean>>> mApexPrivilegedAppAllowlists = new ArrayMap<>();

    public ArrayMap<String, ArrayMap<String, Boolean>> getOemAppAllowlist() {
        return this.mOemAppAllowlist;
    }

    public ArrayMap<String, ArrayMap<String, Boolean>> getPrivilegedAppAllowlist() {
        return this.mPrivilegedAppAllowlist;
    }

    public ArrayMap<String, ArrayMap<String, Boolean>> getVendorPrivilegedAppAllowlist() {
        return this.mVendorPrivilegedAppAllowlist;
    }

    public ArrayMap<String, ArrayMap<String, Boolean>> getProductPrivilegedAppAllowlist() {
        return this.mProductPrivilegedAppAllowlist;
    }

    public ArrayMap<String, ArrayMap<String, Boolean>> getSystemExtPrivilegedAppAllowlist() {
        return this.mSystemExtPrivilegedAppAllowlist;
    }

    public ArrayMap<String, ArrayMap<String, ArrayMap<String, Boolean>>> getApexPrivilegedAppAllowlists() {
        return this.mApexPrivilegedAppAllowlists;
    }

    public Boolean getOemAppAllowlistState(String str, String str2) {
        ArrayMap<String, Boolean> arrayMap = this.mOemAppAllowlist.get(str);
        if (arrayMap == null) {
            return null;
        }
        return arrayMap.get(str2);
    }

    public Boolean getPrivilegedAppAllowlistState(String str, String str2) {
        ArrayMap<String, Boolean> arrayMap = this.mPrivilegedAppAllowlist.get(str);
        if (arrayMap == null) {
            return null;
        }
        return arrayMap.get(str2);
    }

    public Boolean getVendorPrivilegedAppAllowlistState(String str, String str2) {
        ArrayMap<String, Boolean> arrayMap = this.mVendorPrivilegedAppAllowlist.get(str);
        if (arrayMap == null) {
            return null;
        }
        return arrayMap.get(str2);
    }

    public Boolean getProductPrivilegedAppAllowlistState(String str, String str2) {
        ArrayMap<String, Boolean> arrayMap = this.mProductPrivilegedAppAllowlist.get(str);
        if (arrayMap == null) {
            return null;
        }
        return arrayMap.get(str2);
    }

    public Boolean getSystemExtPrivilegedAppAllowlistState(String str, String str2) {
        ArrayMap<String, Boolean> arrayMap = this.mSystemExtPrivilegedAppAllowlist.get(str);
        if (arrayMap == null) {
            return null;
        }
        return arrayMap.get(str2);
    }

    public Boolean getApexPrivilegedAppAllowlistState(String str, String str2, String str3) {
        ArrayMap<String, Boolean> arrayMap;
        ArrayMap<String, ArrayMap<String, Boolean>> arrayMap2 = this.mApexPrivilegedAppAllowlists.get(str);
        if (arrayMap2 == null || (arrayMap = arrayMap2.get(str2)) == null) {
            return null;
        }
        return arrayMap.get(str3);
    }
}
