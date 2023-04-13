package com.android.server.p011pm.parsing.pkg;

import android.content.pm.ApplicationInfo;
/* renamed from: com.android.server.pm.parsing.pkg.AndroidPackageHidden */
/* loaded from: classes2.dex */
public interface AndroidPackageHidden {
    String getPrimaryCpuAbi();

    String getSecondaryCpuAbi();

    boolean isOdm();

    boolean isOem();

    boolean isPrivileged();

    boolean isProduct();

    boolean isSystem();

    boolean isSystemExt();

    boolean isVendor();

    ApplicationInfo toAppInfoWithoutState();
}
