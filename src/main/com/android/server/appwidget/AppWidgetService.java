package com.android.server.appwidget;

import android.content.Context;
import com.android.server.AppWidgetBackupBridge;
import com.android.server.SystemService;
/* loaded from: classes.dex */
public class AppWidgetService extends SystemService {
    public final AppWidgetServiceImpl mImpl;

    public AppWidgetService(Context context) {
        super(context);
        this.mImpl = new AppWidgetServiceImpl(context);
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        this.mImpl.onStart();
        publishBinderService("appwidget", this.mImpl);
        AppWidgetBackupBridge.register(this.mImpl);
    }

    @Override // com.android.server.SystemService
    public void onBootPhase(int i) {
        if (i == 550) {
            this.mImpl.setSafeMode(isSafeMode());
            this.mImpl.systemServicesReady();
        }
    }

    @Override // com.android.server.SystemService
    public void onUserStopping(SystemService.TargetUser targetUser) {
        this.mImpl.onUserStopped(targetUser.getUserIdentifier());
    }

    @Override // com.android.server.SystemService
    public void onUserSwitching(SystemService.TargetUser targetUser, SystemService.TargetUser targetUser2) {
        this.mImpl.reloadWidgetsMaskedStateForGroup(targetUser2.getUserIdentifier());
    }
}
