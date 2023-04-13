package com.android.server.p014wm;

import com.android.server.p014wm.StartingSurfaceController;
/* renamed from: com.android.server.wm.SplashScreenStartingData */
/* loaded from: classes2.dex */
public class SplashScreenStartingData extends StartingData {
    public final int mTheme;

    @Override // com.android.server.p014wm.StartingData
    public boolean needRevealAnimation() {
        return true;
    }

    public SplashScreenStartingData(WindowManagerService windowManagerService, int i, int i2) {
        super(windowManagerService, i2);
        this.mTheme = i;
    }

    @Override // com.android.server.p014wm.StartingData
    public StartingSurfaceController.StartingSurface createStartingSurface(ActivityRecord activityRecord) {
        return this.mService.mStartingSurfaceController.createSplashScreenStartingSurface(activityRecord, this.mTheme);
    }
}
