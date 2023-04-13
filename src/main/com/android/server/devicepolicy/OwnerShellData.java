package com.android.server.devicepolicy;

import android.content.ComponentName;
import com.android.internal.util.Preconditions;
import java.util.Objects;
/* loaded from: classes.dex */
public final class OwnerShellData {
    public final ComponentName admin;
    public boolean isAffiliated;
    public final boolean isDeviceOwner;
    public final boolean isManagedProfileOwner;
    public final boolean isProfileOwner;
    public final int parentUserId;
    public final int userId;

    public OwnerShellData(int i, int i2, ComponentName componentName, boolean z, boolean z2, boolean z3) {
        Preconditions.checkArgument(i != -10000, "userId cannot be USER_NULL");
        this.userId = i;
        this.parentUserId = i2;
        Objects.requireNonNull(componentName, "admin must not be null");
        this.admin = componentName;
        this.isDeviceOwner = z;
        this.isProfileOwner = z2;
        this.isManagedProfileOwner = z3;
        if (z3) {
            Preconditions.checkArgument(i2 != -10000, "parentUserId cannot be USER_NULL for managed profile owner");
            Preconditions.checkArgument(i2 != i, "cannot be parent of itself (%d)", new Object[]{Integer.valueOf(i)});
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(OwnerShellData.class.getSimpleName());
        sb.append("[userId=");
        sb.append(this.userId);
        sb.append(",admin=");
        sb.append(this.admin.flattenToShortString());
        if (this.isDeviceOwner) {
            sb.append(",deviceOwner");
        }
        if (this.isProfileOwner) {
            sb.append(",isProfileOwner");
        }
        if (this.isManagedProfileOwner) {
            sb.append(",isManagedProfileOwner");
        }
        if (this.parentUserId != -10000) {
            sb.append(",parentUserId=");
            sb.append(this.parentUserId);
        }
        if (this.isAffiliated) {
            sb.append(",isAffiliated");
        }
        sb.append(']');
        return sb.toString();
    }

    public static OwnerShellData forDeviceOwner(int i, ComponentName componentName) {
        return new OwnerShellData(i, -10000, componentName, true, false, false);
    }

    public static OwnerShellData forUserProfileOwner(int i, ComponentName componentName) {
        return new OwnerShellData(i, -10000, componentName, false, true, false);
    }

    public static OwnerShellData forManagedProfileOwner(int i, int i2, ComponentName componentName) {
        return new OwnerShellData(i, i2, componentName, false, false, true);
    }
}
