package com.android.server.p011pm;

import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.p011pm.PackageManagerService;
@VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
/* renamed from: com.android.server.pm.ComputerLocked */
/* loaded from: classes2.dex */
public final class ComputerLocked extends ComputerEngine {
    public ComputerLocked(PackageManagerService.Snapshot snapshot) {
        super(snapshot, -1);
    }

    @Override // com.android.server.p011pm.ComputerEngine
    public ComponentName resolveComponentName() {
        return this.mService.getResolveComponentName();
    }

    @Override // com.android.server.p011pm.ComputerEngine
    public ActivityInfo instantAppInstallerActivity() {
        return this.mService.mInstantAppInstallerActivity;
    }

    @Override // com.android.server.p011pm.ComputerEngine
    public ApplicationInfo androidApplication() {
        return this.mService.getCoreAndroidApplication();
    }
}
