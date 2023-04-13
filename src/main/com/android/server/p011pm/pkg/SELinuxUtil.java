package com.android.server.p011pm.pkg;
/* renamed from: com.android.server.pm.pkg.SELinuxUtil */
/* loaded from: classes2.dex */
public final class SELinuxUtil {
    public static String getSeinfoUser(PackageUserState packageUserState) {
        return packageUserState.isInstantApp() ? ":ephemeralapp:complete" : ":complete";
    }
}
