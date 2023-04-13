package com.android.server.p014wm;

import com.android.server.p014wm.StartingSurfaceController;
/* renamed from: com.android.server.wm.StartingData */
/* loaded from: classes2.dex */
public abstract class StartingData {
    public Task mAssociatedTask;
    public boolean mIsDisplayed;
    public boolean mIsTransitionForward;
    public final WindowManagerService mService;
    public final int mTypeParams;

    public abstract StartingSurfaceController.StartingSurface createStartingSurface(ActivityRecord activityRecord);

    public boolean hasImeSurface() {
        return false;
    }

    public abstract boolean needRevealAnimation();

    public StartingData(WindowManagerService windowManagerService, int i) {
        this.mService = windowManagerService;
        this.mTypeParams = i;
    }
}
