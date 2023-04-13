package com.android.server.permission.access;

import com.android.server.permission.jarjar.kotlin.jvm.internal.DefaultConstructorMarker;
import com.android.server.permission.jarjar.kotlin.jvm.internal.Intrinsics;
/* compiled from: AccessUri.kt */
/* loaded from: classes2.dex */
public final class PackageUri extends AccessUri {
    public static final Companion Companion = new Companion(null);
    public final String packageName;
    public final int userId;

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof PackageUri) {
            PackageUri packageUri = (PackageUri) obj;
            return Intrinsics.areEqual(this.packageName, packageUri.packageName) && this.userId == packageUri.userId;
        }
        return false;
    }

    public int hashCode() {
        return (this.packageName.hashCode() * 31) + Integer.hashCode(this.userId);
    }

    public final String getPackageName() {
        return this.packageName;
    }

    public final int getUserId() {
        return this.userId;
    }

    public String toString() {
        String scheme = getScheme();
        String str = this.packageName;
        int i = this.userId;
        return scheme + ":///" + str + "/" + i;
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
