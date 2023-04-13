package com.android.server.permission.access;

import com.android.server.permission.jarjar.kotlin.jvm.internal.DefaultConstructorMarker;
import com.android.server.permission.jarjar.kotlin.jvm.internal.Intrinsics;
/* compiled from: AccessUri.kt */
/* loaded from: classes2.dex */
public final class AppOpUri extends AccessUri {
    public static final Companion Companion = new Companion(null);
    public final String appOpName;

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof AppOpUri) && Intrinsics.areEqual(this.appOpName, ((AppOpUri) obj).appOpName);
    }

    public int hashCode() {
        return this.appOpName.hashCode();
    }

    public final String getAppOpName() {
        return this.appOpName;
    }

    public String toString() {
        String scheme = getScheme();
        String str = this.appOpName;
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
