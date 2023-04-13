package com.android.server.p011pm.permission;

import android.annotation.NonNull;
import com.android.internal.util.AnnotationValidations;
/* renamed from: com.android.server.pm.permission.CompatibilityPermissionInfo */
/* loaded from: classes2.dex */
public class CompatibilityPermissionInfo {
    public static final CompatibilityPermissionInfo[] COMPAT_PERMS = {new CompatibilityPermissionInfo("android.permission.POST_NOTIFICATIONS", 33), new CompatibilityPermissionInfo("android.permission.WRITE_EXTERNAL_STORAGE", 4), new CompatibilityPermissionInfo("android.permission.READ_PHONE_STATE", 4)};
    public final String mName;
    public final int mSdkVersion;

    public CompatibilityPermissionInfo(String str, int i) {
        this.mName = str;
        AnnotationValidations.validate(NonNull.class, (NonNull) null, str);
        this.mSdkVersion = i;
    }

    public String getName() {
        return this.mName;
    }

    public int getSdkVersion() {
        return this.mSdkVersion;
    }
}
