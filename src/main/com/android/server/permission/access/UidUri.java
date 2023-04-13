package com.android.server.permission.access;

import android.os.UserHandle;
import com.android.server.permission.jarjar.kotlin.jvm.internal.DefaultConstructorMarker;
/* compiled from: AccessUri.kt */
/* loaded from: classes2.dex */
public final class UidUri extends AccessUri {
    public static final Companion Companion = new Companion(null);
    public final int uid;

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof UidUri) && this.uid == ((UidUri) obj).uid;
    }

    public int hashCode() {
        return Integer.hashCode(this.uid);
    }

    public final int getUserId() {
        return UserHandle.getUserId(this.uid);
    }

    public final int getAppId() {
        return UserHandle.getAppId(this.uid);
    }

    public String toString() {
        String scheme = getScheme();
        int i = this.uid;
        return scheme + ":///" + i;
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
