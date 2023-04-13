package com.android.server.p011pm.pkg;

import android.content.ComponentName;
import android.content.pm.overlay.OverlayPaths;
import android.util.ArraySet;
import android.util.Pair;
import com.android.server.utils.WatchedArrayMap;
import com.android.server.utils.WatchedArraySet;
import java.util.Collections;
import java.util.Map;
/* renamed from: com.android.server.pm.pkg.PackageUserStateDefault */
/* loaded from: classes2.dex */
public class PackageUserStateDefault implements PackageUserStateInternal {
    @Override // com.android.server.p011pm.pkg.PackageUserState
    public OverlayPaths getAllOverlayPaths() {
        return null;
    }

    @Override // com.android.server.p011pm.pkg.PackageUserState
    public long getCeDataInode() {
        return 0L;
    }

    @Override // com.android.server.p011pm.pkg.PackageUserStateInternal
    public WatchedArraySet<String> getDisabledComponentsNoCopy() {
        return null;
    }

    @Override // com.android.server.p011pm.pkg.PackageUserState
    public int getDistractionFlags() {
        return 0;
    }

    @Override // com.android.server.p011pm.pkg.PackageUserStateInternal
    public WatchedArraySet<String> getEnabledComponentsNoCopy() {
        return null;
    }

    @Override // com.android.server.p011pm.pkg.PackageUserState
    public int getEnabledState() {
        return 0;
    }

    @Override // com.android.server.p011pm.pkg.PackageUserState
    public long getFirstInstallTimeMillis() {
        return 0L;
    }

    @Override // com.android.server.p011pm.pkg.PackageUserState
    public String getHarmfulAppWarning() {
        return null;
    }

    @Override // com.android.server.p011pm.pkg.PackageUserState
    public int getInstallReason() {
        return 0;
    }

    @Override // com.android.server.p011pm.pkg.PackageUserState
    public String getLastDisableAppCaller() {
        return null;
    }

    @Override // com.android.server.p011pm.pkg.PackageUserState
    public OverlayPaths getOverlayPaths() {
        return null;
    }

    @Override // com.android.server.p011pm.pkg.PackageUserStateInternal
    public Pair<String, Integer> getOverrideLabelIconForComponent(ComponentName componentName) {
        return null;
    }

    @Override // com.android.server.p011pm.pkg.PackageUserState
    public String getSplashScreenTheme() {
        return null;
    }

    @Override // com.android.server.p011pm.pkg.PackageUserStateInternal
    public WatchedArrayMap<String, SuspendParams> getSuspendParams() {
        return null;
    }

    @Override // com.android.server.p011pm.pkg.PackageUserState
    public int getUninstallReason() {
        return 0;
    }

    @Override // com.android.server.p011pm.pkg.PackageUserState
    public boolean isComponentDisabled(String str) {
        return false;
    }

    @Override // com.android.server.p011pm.pkg.PackageUserState
    public boolean isComponentEnabled(String str) {
        return false;
    }

    @Override // com.android.server.p011pm.pkg.PackageUserState
    public boolean isHidden() {
        return false;
    }

    @Override // com.android.server.p011pm.pkg.PackageUserState
    public boolean isInstalled() {
        return true;
    }

    @Override // com.android.server.p011pm.pkg.PackageUserState
    public boolean isInstantApp() {
        return false;
    }

    @Override // com.android.server.p011pm.pkg.PackageUserState
    public boolean isNotLaunched() {
        return false;
    }

    @Override // com.android.server.p011pm.pkg.PackageUserState
    public boolean isStopped() {
        return false;
    }

    @Override // com.android.server.p011pm.pkg.PackageUserState
    public boolean isSuspended() {
        return false;
    }

    @Override // com.android.server.p011pm.pkg.PackageUserState
    public boolean isVirtualPreload() {
        return false;
    }

    @Override // com.android.server.p011pm.pkg.PackageUserState
    public Map<String, OverlayPaths> getSharedLibraryOverlayPaths() {
        return Collections.emptyMap();
    }

    @Override // com.android.server.p011pm.pkg.PackageUserState
    /* renamed from: getDisabledComponents */
    public ArraySet<String> m5817getDisabledComponents() {
        return new ArraySet<>();
    }

    @Override // com.android.server.p011pm.pkg.PackageUserState
    /* renamed from: getEnabledComponents */
    public ArraySet<String> m5818getEnabledComponents() {
        return new ArraySet<>();
    }
}
