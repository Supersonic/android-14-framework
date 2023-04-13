package com.android.server.p011pm.pkg;

import android.annotation.SystemApi;
import android.content.pm.VersionedPackage;
import java.util.List;
@SystemApi(client = SystemApi.Client.SYSTEM_SERVER)
/* renamed from: com.android.server.pm.pkg.SharedLibrary */
/* loaded from: classes2.dex */
public interface SharedLibrary {
    List<String> getAllCodePaths();

    VersionedPackage getDeclaringPackage();

    List<SharedLibrary> getDependencies();

    List<VersionedPackage> getDependentPackages();

    String getName();

    String getPackageName();

    String getPath();

    int getType();

    long getVersion();

    boolean isNative();
}
