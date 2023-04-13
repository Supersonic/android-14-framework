package com.android.server.p014wm;

import android.view.Display;
import com.android.internal.annotations.VisibleForTesting;
/* renamed from: com.android.server.wm.DisplayRotationCoordinator */
/* loaded from: classes2.dex */
public class DisplayRotationCoordinator {
    public int mDefaultDisplayCurrentRotation;
    public int mDefaultDisplayDefaultRotation;
    @VisibleForTesting
    Runnable mDefaultDisplayRotationChangedCallback;

    public void onDefaultDisplayRotationChanged(int i) {
        this.mDefaultDisplayCurrentRotation = i;
        Runnable runnable = this.mDefaultDisplayRotationChangedCallback;
        if (runnable != null) {
            runnable.run();
        }
    }

    public void setDefaultDisplayDefaultRotation(int i) {
        this.mDefaultDisplayDefaultRotation = i;
    }

    public int getDefaultDisplayCurrentRotation() {
        return this.mDefaultDisplayCurrentRotation;
    }

    public void setDefaultDisplayRotationChangedCallback(Runnable runnable) {
        if (this.mDefaultDisplayRotationChangedCallback != null) {
            throw new UnsupportedOperationException("Multiple clients unsupported");
        }
        this.mDefaultDisplayRotationChangedCallback = runnable;
        if (this.mDefaultDisplayCurrentRotation != this.mDefaultDisplayDefaultRotation) {
            runnable.run();
        }
    }

    public void removeDefaultDisplayRotationChangedCallback() {
        this.mDefaultDisplayRotationChangedCallback = null;
    }

    public static boolean isSecondaryInternalDisplay(DisplayContent displayContent) {
        Display display;
        return (displayContent.isDefaultDisplay || (display = displayContent.mDisplay) == null || display.getType() != 1) ? false : true;
    }
}
