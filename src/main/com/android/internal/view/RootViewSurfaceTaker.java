package com.android.internal.view;

import android.view.InputQueue;
import android.view.PendingInsetsController;
import android.view.SurfaceHolder;
/* loaded from: classes2.dex */
public interface RootViewSurfaceTaker {
    void onRootViewScrollYChanged(int i);

    PendingInsetsController providePendingInsetsController();

    void setSurfaceFormat(int i);

    void setSurfaceKeepScreenOn(boolean z);

    void setSurfaceType(int i);

    InputQueue.Callback willYouTakeTheInputQueue();

    SurfaceHolder.Callback2 willYouTakeTheSurface();
}
