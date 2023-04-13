package com.android.server.p011pm;
/* renamed from: com.android.server.pm.PackageSessionProvider */
/* loaded from: classes2.dex */
public interface PackageSessionProvider {
    PackageInstallerSession getSession(int i);

    PackageSessionVerifier getSessionVerifier();
}
