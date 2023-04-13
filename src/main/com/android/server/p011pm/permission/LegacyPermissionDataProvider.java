package com.android.server.p011pm.permission;

import java.util.List;
import java.util.Map;
import java.util.Set;
/* renamed from: com.android.server.pm.permission.LegacyPermissionDataProvider */
/* loaded from: classes2.dex */
public interface LegacyPermissionDataProvider {
    Map<String, Set<String>> getAllAppOpPermissionPackages();

    int[] getGidsForUid(int i);

    LegacyPermissionState getLegacyPermissionState(int i);

    List<LegacyPermission> getLegacyPermissions();

    void writeLegacyPermissionStateTEMP();
}
