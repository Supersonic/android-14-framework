package com.android.server.permission.access.util;

import android.content.ApexEnvironment;
import android.os.UserHandle;
import java.io.File;
/* compiled from: PermissionApex.kt */
/* loaded from: classes2.dex */
public final class PermissionApex {
    public static final PermissionApex INSTANCE = new PermissionApex();

    public final File getSystemDataDirectory() {
        return getApexEnvironment().getDeviceProtectedDataDir();
    }

    public final File getUserDataDirectory(int i) {
        return getApexEnvironment().getDeviceProtectedDataDirForUser(UserHandle.of(i));
    }

    public final ApexEnvironment getApexEnvironment() {
        return ApexEnvironment.getApexEnvironment("com.android.permission");
    }
}
