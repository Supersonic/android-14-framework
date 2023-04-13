package com.android.server.p011pm.pkg;

import android.content.pm.SigningDetails;
import android.util.ArraySet;
import com.android.server.p011pm.permission.LegacyPermissionState;
import java.util.List;
/* renamed from: com.android.server.pm.pkg.SharedUserApi */
/* loaded from: classes2.dex */
public interface SharedUserApi {
    String getName();

    ArraySet<? extends PackageStateInternal> getPackageStates();

    List<AndroidPackage> getPackages();

    int getSeInfoTargetSdkVersion();

    LegacyPermissionState getSharedUserLegacyPermissionState();

    SigningDetails getSigningDetails();

    boolean isPrivileged();
}
