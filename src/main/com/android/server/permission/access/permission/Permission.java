package com.android.server.permission.access.permission;

import android.content.pm.PermissionInfo;
import android.os.UserHandle;
import com.android.server.permission.jarjar.kotlin.jvm.internal.DefaultConstructorMarker;
import com.android.server.permission.jarjar.kotlin.jvm.internal.Intrinsics;
import java.util.Arrays;
import libcore.util.EmptyArray;
/* compiled from: Permission.kt */
/* loaded from: classes2.dex */
public final class Permission {
    public static final Companion Companion = new Companion(null);
    public final int appId;
    public final boolean areGidsPerUser;
    public final int[] gids;
    public final boolean isReconciled;
    public final PermissionInfo permissionInfo;
    public final int type;

    public static /* synthetic */ Permission copy$default(Permission permission, PermissionInfo permissionInfo, boolean z, int i, int i2, int[] iArr, boolean z2, int i3, Object obj) {
        if ((i3 & 1) != 0) {
            permissionInfo = permission.permissionInfo;
        }
        if ((i3 & 2) != 0) {
            z = permission.isReconciled;
        }
        boolean z3 = z;
        if ((i3 & 4) != 0) {
            i = permission.type;
        }
        int i4 = i;
        if ((i3 & 8) != 0) {
            i2 = permission.appId;
        }
        int i5 = i2;
        if ((i3 & 16) != 0) {
            iArr = permission.gids;
        }
        int[] iArr2 = iArr;
        if ((i3 & 32) != 0) {
            z2 = permission.areGidsPerUser;
        }
        return permission.copy(permissionInfo, z3, i4, i5, iArr2, z2);
    }

    public final Permission copy(PermissionInfo permissionInfo, boolean z, int i, int i2, int[] iArr, boolean z2) {
        return new Permission(permissionInfo, z, i, i2, iArr, z2);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Permission) {
            Permission permission = (Permission) obj;
            return Intrinsics.areEqual(this.permissionInfo, permission.permissionInfo) && this.isReconciled == permission.isReconciled && this.type == permission.type && this.appId == permission.appId && Intrinsics.areEqual(this.gids, permission.gids) && this.areGidsPerUser == permission.areGidsPerUser;
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public int hashCode() {
        int hashCode = this.permissionInfo.hashCode() * 31;
        boolean z = this.isReconciled;
        int i = z;
        if (z != 0) {
            i = 1;
        }
        int hashCode2 = (((((((hashCode + i) * 31) + Integer.hashCode(this.type)) * 31) + Integer.hashCode(this.appId)) * 31) + Arrays.hashCode(this.gids)) * 31;
        boolean z2 = this.areGidsPerUser;
        return hashCode2 + (z2 ? 1 : z2 ? 1 : 0);
    }

    public String toString() {
        PermissionInfo permissionInfo = this.permissionInfo;
        boolean z = this.isReconciled;
        int i = this.type;
        int i2 = this.appId;
        String arrays = Arrays.toString(this.gids);
        boolean z2 = this.areGidsPerUser;
        return "Permission(permissionInfo=" + permissionInfo + ", isReconciled=" + z + ", type=" + i + ", appId=" + i2 + ", gids=" + arrays + ", areGidsPerUser=" + z2 + ")";
    }

    public Permission(PermissionInfo permissionInfo, boolean z, int i, int i2, int[] iArr, boolean z2) {
        this.permissionInfo = permissionInfo;
        this.isReconciled = z;
        this.type = i;
        this.appId = i2;
        this.gids = iArr;
        this.areGidsPerUser = z2;
    }

    public final PermissionInfo getPermissionInfo() {
        return this.permissionInfo;
    }

    public final boolean isReconciled() {
        return this.isReconciled;
    }

    public final int getType() {
        return this.type;
    }

    public final int getAppId() {
        return this.appId;
    }

    public /* synthetic */ Permission(PermissionInfo permissionInfo, boolean z, int i, int i2, int[] iArr, boolean z2, int i3, DefaultConstructorMarker defaultConstructorMarker) {
        this(permissionInfo, z, i, i2, (i3 & 16) != 0 ? EmptyArray.INT : iArr, (i3 & 32) != 0 ? false : z2);
    }

    public final int[] getGids() {
        return this.gids;
    }

    public final boolean getAreGidsPerUser() {
        return this.areGidsPerUser;
    }

    public final int[] getGidsForUser(int i) {
        if (this.areGidsPerUser) {
            int length = this.gids.length;
            int[] iArr = new int[length];
            for (int i2 = 0; i2 < length; i2++) {
                iArr[i2] = UserHandle.getUid(i, this.gids[i2]);
            }
            return iArr;
        }
        int[] iArr2 = this.gids;
        int[] copyOf = Arrays.copyOf(iArr2, iArr2.length);
        Intrinsics.checkNotNullExpressionValue(copyOf, "copyOf(this, size)");
        return copyOf;
    }

    /* compiled from: Permission.kt */
    /* loaded from: classes2.dex */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        public Companion() {
        }
    }
}
