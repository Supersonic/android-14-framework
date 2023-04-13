package com.android.server.p011pm.pkg.mutate;

import android.util.ArraySet;
/* renamed from: com.android.server.pm.pkg.mutate.PackageStateWrite */
/* loaded from: classes2.dex */
public interface PackageStateWrite {
    void onChanged();

    PackageStateWrite setCategoryOverride(int i);

    PackageStateWrite setHiddenUntilInstalled(boolean z);

    PackageStateWrite setInstaller(String str, int i);

    PackageStateWrite setLoadingProgress(float f);

    PackageStateWrite setMimeGroup(String str, ArraySet<String> arraySet);

    PackageStateWrite setOverrideSeInfo(String str);

    PackageStateWrite setRequiredForSystemUser(boolean z);

    PackageStateWrite setUpdateAvailable(boolean z);

    PackageStateWrite setUpdateOwner(String str);

    PackageUserStateWrite userState(int i);
}
