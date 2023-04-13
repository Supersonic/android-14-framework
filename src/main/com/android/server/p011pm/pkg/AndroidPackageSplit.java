package com.android.server.p011pm.pkg;

import android.annotation.SystemApi;
import java.util.List;
@SystemApi(client = SystemApi.Client.SYSTEM_SERVER)
/* renamed from: com.android.server.pm.pkg.AndroidPackageSplit */
/* loaded from: classes2.dex */
public interface AndroidPackageSplit {
    String getClassLoaderName();

    List<AndroidPackageSplit> getDependencies();

    String getName();

    String getPath();

    int getRevisionCode();

    boolean isHasCode();
}
