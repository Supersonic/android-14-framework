package com.android.server.p011pm.pkg.component;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* renamed from: com.android.server.pm.pkg.component.ParsedUsesPermission */
/* loaded from: classes2.dex */
public interface ParsedUsesPermission {

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: com.android.server.pm.pkg.component.ParsedUsesPermission$UsesPermissionFlags */
    /* loaded from: classes2.dex */
    public @interface UsesPermissionFlags {
    }

    String getName();

    int getUsesPermissionFlags();
}
