package com.android.server.p011pm.pkg.component;

import java.util.Set;
/* renamed from: com.android.server.pm.pkg.component.ParsedPermission */
/* loaded from: classes2.dex */
public interface ParsedPermission extends ParsedComponent {
    String getBackgroundPermission();

    String getGroup();

    Set<String> getKnownCerts();

    ParsedPermissionGroup getParsedPermissionGroup();

    int getProtectionLevel();

    int getRequestRes();

    boolean isTree();
}
