package com.android.server.p011pm.pkg;

import android.content.pm.SigningDetails;
import android.util.SparseArray;
import com.android.server.p011pm.InstallSource;
import com.android.server.p011pm.PackageKeySetData;
import com.android.server.p011pm.parsing.pkg.AndroidPackageInternal;
import com.android.server.p011pm.permission.LegacyPermissionState;
import java.util.UUID;
/* renamed from: com.android.server.pm.pkg.PackageStateInternal */
/* loaded from: classes2.dex */
public interface PackageStateInternal extends PackageState {
    UUID getDomainSetId();

    int getFlags();

    InstallSource getInstallSource();

    PackageKeySetData getKeySetData();

    LegacyPermissionState getLegacyPermissionState();

    float getLoadingProgress();

    String getPathString();

    AndroidPackageInternal getPkg();

    @Deprecated
    String getPrimaryCpuAbiLegacy();

    int getPrivateFlags();

    String getRealName();

    String getSecondaryCpuAbiLegacy();

    SigningDetails getSigningDetails();

    PackageStateUnserialized getTransientState();

    @Override // com.android.server.p011pm.pkg.PackageState
    SparseArray<? extends PackageUserStateInternal> getUserStates();

    boolean isLoading();

    @Override // com.android.server.p011pm.pkg.PackageState
    default PackageUserStateInternal getUserStateOrDefault(int i) {
        PackageUserStateInternal packageUserStateInternal = getUserStates().get(i);
        return packageUserStateInternal == null ? PackageUserStateInternal.DEFAULT : packageUserStateInternal;
    }
}
