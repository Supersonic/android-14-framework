package com.android.server.autofill.p007ui;

import android.app.AppOpsManager;
import android.content.Context;
import android.os.Binder;
import android.os.IBinder;
/* renamed from: com.android.server.autofill.ui.OverlayControl */
/* loaded from: classes.dex */
public class OverlayControl {
    public final AppOpsManager mAppOpsManager;
    public final IBinder mToken = new Binder();

    public OverlayControl(Context context) {
        this.mAppOpsManager = (AppOpsManager) context.getSystemService(AppOpsManager.class);
    }

    public void hideOverlays() {
        setOverlayAllowed(false);
    }

    public void showOverlays() {
        setOverlayAllowed(true);
    }

    public final void setOverlayAllowed(boolean z) {
        AppOpsManager appOpsManager = this.mAppOpsManager;
        if (appOpsManager != null) {
            appOpsManager.setUserRestrictionForUser(24, !z, this.mToken, null, -1);
            this.mAppOpsManager.setUserRestrictionForUser(45, !z, this.mToken, null, -1);
        }
    }
}
