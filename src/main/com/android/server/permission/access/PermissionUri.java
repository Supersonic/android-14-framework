package com.android.server.permission.access;

import com.android.server.permission.jarjar.kotlin.jvm.internal.DefaultConstructorMarker;
import com.android.server.permission.jarjar.kotlin.jvm.internal.Intrinsics;
/* compiled from: AccessUri.kt */
/* loaded from: classes2.dex */
public final class PermissionUri extends AccessUri {
    public static final Companion Companion = new Companion(null);
    public final String permissionName;

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof PermissionUri) && Intrinsics.areEqual(this.permissionName, ((PermissionUri) obj).permissionName);
    }

    public int hashCode() {
        return this.permissionName.hashCode();
    }

    public final String getPermissionName() {
        return this.permissionName;
    }

    public String toString() {
        String scheme = getScheme();
        String str = this.permissionName;
        return scheme + ":///" + str;
    }

    /* compiled from: AccessUri.kt */
    /* loaded from: classes2.dex */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        public Companion() {
        }
    }
}
