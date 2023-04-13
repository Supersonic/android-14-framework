package android.content.p001pm.pkg;

import android.content.p001pm.overlay.OverlayPaths;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
@Deprecated
/* renamed from: android.content.pm.pkg.FrameworkPackageUserStateDefault */
/* loaded from: classes.dex */
class FrameworkPackageUserStateDefault implements FrameworkPackageUserState {
    @Override // android.content.p001pm.pkg.FrameworkPackageUserState
    public int getEnabledState() {
        return 0;
    }

    @Override // android.content.p001pm.pkg.FrameworkPackageUserState
    public int getInstallReason() {
        return 0;
    }

    @Override // android.content.p001pm.pkg.FrameworkPackageUserState
    public Map<String, OverlayPaths> getSharedLibraryOverlayPaths() {
        return Collections.emptyMap();
    }

    @Override // android.content.p001pm.pkg.FrameworkPackageUserState
    public int getUninstallReason() {
        return 0;
    }

    @Override // android.content.p001pm.pkg.FrameworkPackageUserState
    public boolean isInstalled() {
        return true;
    }

    @Override // android.content.p001pm.pkg.FrameworkPackageUserState
    public Set<String> getDisabledComponents() {
        return Collections.emptySet();
    }

    @Override // android.content.p001pm.pkg.FrameworkPackageUserState
    public Set<String> getEnabledComponents() {
        return Collections.emptySet();
    }

    @Override // android.content.p001pm.pkg.FrameworkPackageUserState
    public long getCeDataInode() {
        return 0L;
    }

    @Override // android.content.p001pm.pkg.FrameworkPackageUserState
    public int getDistractionFlags() {
        return 0;
    }

    @Override // android.content.p001pm.pkg.FrameworkPackageUserState
    public String getHarmfulAppWarning() {
        return null;
    }

    @Override // android.content.p001pm.pkg.FrameworkPackageUserState
    public String getLastDisableAppCaller() {
        return null;
    }

    @Override // android.content.p001pm.pkg.FrameworkPackageUserState
    public OverlayPaths getOverlayPaths() {
        return null;
    }

    @Override // android.content.p001pm.pkg.FrameworkPackageUserState
    public boolean isHidden() {
        return false;
    }

    @Override // android.content.p001pm.pkg.FrameworkPackageUserState
    public boolean isInstantApp() {
        return false;
    }

    @Override // android.content.p001pm.pkg.FrameworkPackageUserState
    public boolean isNotLaunched() {
        return false;
    }

    @Override // android.content.p001pm.pkg.FrameworkPackageUserState
    public boolean isStopped() {
        return false;
    }

    @Override // android.content.p001pm.pkg.FrameworkPackageUserState
    public boolean isSuspended() {
        return false;
    }

    @Override // android.content.p001pm.pkg.FrameworkPackageUserState
    public boolean isVirtualPreload() {
        return false;
    }

    @Override // android.content.p001pm.pkg.FrameworkPackageUserState
    public String getSplashScreenTheme() {
        return null;
    }

    @Override // android.content.p001pm.pkg.FrameworkPackageUserState
    public boolean isComponentEnabled(String componentName) {
        return false;
    }

    @Override // android.content.p001pm.pkg.FrameworkPackageUserState
    public boolean isComponentDisabled(String componentName) {
        return false;
    }

    @Override // android.content.p001pm.pkg.FrameworkPackageUserState
    public OverlayPaths getAllOverlayPaths() {
        return null;
    }
}
