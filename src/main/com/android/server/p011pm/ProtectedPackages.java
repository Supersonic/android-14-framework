package com.android.server.p011pm;

import android.content.Context;
import android.util.ArraySet;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import java.util.List;
import java.util.Set;
/* renamed from: com.android.server.pm.ProtectedPackages */
/* loaded from: classes2.dex */
public class ProtectedPackages {
    public final Context mContext;
    @GuardedBy({"this"})
    public String mDeviceOwnerPackage;
    @GuardedBy({"this"})
    public int mDeviceOwnerUserId;
    @GuardedBy({"this"})
    public final String mDeviceProvisioningPackage;
    @GuardedBy({"this"})
    public final SparseArray<Set<String>> mOwnerProtectedPackages = new SparseArray<>();
    @GuardedBy({"this"})
    public SparseArray<String> mProfileOwnerPackages;

    public ProtectedPackages(Context context) {
        this.mContext = context;
        this.mDeviceProvisioningPackage = context.getResources().getString(17039914);
    }

    public synchronized void setDeviceAndProfileOwnerPackages(int i, String str, SparseArray<String> sparseArray) {
        this.mDeviceOwnerUserId = i;
        SparseArray<String> sparseArray2 = null;
        if (i == -10000) {
            str = null;
        }
        this.mDeviceOwnerPackage = str;
        if (sparseArray != null) {
            sparseArray2 = sparseArray.clone();
        }
        this.mProfileOwnerPackages = sparseArray2;
    }

    public synchronized void setOwnerProtectedPackages(int i, List<String> list) {
        if (list == null) {
            this.mOwnerProtectedPackages.remove(i);
        } else {
            this.mOwnerProtectedPackages.put(i, new ArraySet(list));
        }
    }

    public final synchronized boolean hasDeviceOwnerOrProfileOwner(int i, String str) {
        if (str == null) {
            return false;
        }
        String str2 = this.mDeviceOwnerPackage;
        if (str2 != null && this.mDeviceOwnerUserId == i && str.equals(str2)) {
            return true;
        }
        SparseArray<String> sparseArray = this.mProfileOwnerPackages;
        if (sparseArray != null) {
            if (str.equals(sparseArray.get(i))) {
                return true;
            }
        }
        return false;
    }

    public synchronized String getDeviceOwnerOrProfileOwnerPackage(int i) {
        if (this.mDeviceOwnerUserId == i) {
            return this.mDeviceOwnerPackage;
        }
        SparseArray<String> sparseArray = this.mProfileOwnerPackages;
        if (sparseArray == null) {
            return null;
        }
        return sparseArray.get(i);
    }

    /* JADX WARN: Code restructure failed: missing block: B:7:0x000f, code lost:
        if (isOwnerProtectedPackage(r2, r3) != false) goto L13;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final synchronized boolean isProtectedPackage(int i, String str) {
        boolean z;
        if (str != null) {
            if (!str.equals(this.mDeviceProvisioningPackage)) {
            }
            z = true;
        }
        z = false;
        return z;
    }

    public final synchronized boolean isOwnerProtectedPackage(int i, String str) {
        boolean isPackageProtectedForUser;
        if (hasProtectedPackages(i)) {
            isPackageProtectedForUser = isPackageProtectedForUser(i, str);
        } else {
            isPackageProtectedForUser = isPackageProtectedForUser(-1, str);
        }
        return isPackageProtectedForUser;
    }

    public final synchronized boolean isPackageProtectedForUser(int i, String str) {
        boolean z;
        int indexOfKey = this.mOwnerProtectedPackages.indexOfKey(i);
        if (indexOfKey >= 0) {
            z = this.mOwnerProtectedPackages.valueAt(indexOfKey).contains(str);
        }
        return z;
    }

    public final synchronized boolean hasProtectedPackages(int i) {
        return this.mOwnerProtectedPackages.indexOfKey(i) >= 0;
    }

    public boolean isPackageStateProtected(int i, String str) {
        return hasDeviceOwnerOrProfileOwner(i, str) || isProtectedPackage(i, str);
    }

    public boolean isPackageDataProtected(int i, String str) {
        return hasDeviceOwnerOrProfileOwner(i, str) || isProtectedPackage(i, str);
    }
}
