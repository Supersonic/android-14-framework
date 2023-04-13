package com.android.server.p014wm;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.view.SurfaceControl;
import android.view.inputmethod.ImeTracker;
import com.android.internal.annotations.Keep;
@Keep
/* renamed from: com.android.server.wm.DisplayAreaGroup */
/* loaded from: classes2.dex */
class DisplayAreaGroup extends RootDisplayArea {
    @Override // com.android.server.p014wm.InsetsControlTarget
    public /* bridge */ /* synthetic */ boolean canShowTransient() {
        return super.canShowTransient();
    }

    @Override // com.android.server.p014wm.InsetsControlTarget
    public /* bridge */ /* synthetic */ int getRequestedVisibleTypes() {
        return super.getRequestedVisibleTypes();
    }

    @Override // com.android.server.p014wm.InsetsControlTarget
    public /* bridge */ /* synthetic */ WindowState getWindow() {
        return super.getWindow();
    }

    @Override // com.android.server.p014wm.InsetsControlTarget
    public /* bridge */ /* synthetic */ void hideInsets(int i, boolean z, ImeTracker.Token token) {
        super.hideInsets(i, z, token);
    }

    @Override // com.android.server.p014wm.InsetsControlTarget
    public /* bridge */ /* synthetic */ boolean isRequestedVisible(int i) {
        return super.isRequestedVisible(i);
    }

    @Override // com.android.server.p014wm.InsetsControlTarget
    public /* bridge */ /* synthetic */ void notifyInsetsControlChanged() {
        super.notifyInsetsControlChanged();
    }

    @Override // com.android.server.p014wm.SurfaceAnimator.Animatable
    public /* bridge */ /* synthetic */ void onLeashAnimationStarting(SurfaceControl.Transaction transaction, SurfaceControl surfaceControl) {
        super.onLeashAnimationStarting(transaction, surfaceControl);
    }

    @Override // com.android.server.p014wm.SurfaceAnimator.Animatable
    public /* bridge */ /* synthetic */ boolean shouldDeferAnimationFinish(Runnable runnable) {
        return super.shouldDeferAnimationFinish(runnable);
    }

    @Override // com.android.server.p014wm.InsetsControlTarget
    public /* bridge */ /* synthetic */ void showInsets(int i, boolean z, ImeTracker.Token token) {
        super.showInsets(i, z, token);
    }

    public DisplayAreaGroup(WindowManagerService windowManagerService, String str, int i) {
        super(windowManagerService, str, i);
    }

    @Override // com.android.server.p014wm.RootDisplayArea
    public boolean isOrientationDifferentFromDisplay() {
        return isOrientationDifferentFromDisplay(getBounds());
    }

    private boolean isOrientationDifferentFromDisplay(Rect rect) {
        DisplayContent displayContent = this.mDisplayContent;
        if (displayContent == null) {
            return false;
        }
        Rect bounds = displayContent.getBounds();
        return (rect.width() < rect.height()) != (bounds.width() < bounds.height());
    }

    @Override // com.android.server.p014wm.DisplayArea, com.android.server.p014wm.WindowContainer
    public int getOrientation(int i) {
        int orientation = super.getOrientation(i);
        return isOrientationDifferentFromDisplay() ? ActivityInfo.reverseOrientation(orientation) : orientation;
    }

    @Override // com.android.server.p014wm.DisplayArea, com.android.server.p014wm.ConfigurationContainer
    public void resolveOverrideConfiguration(Configuration configuration) {
        super.resolveOverrideConfiguration(configuration);
        Configuration resolvedOverrideConfiguration = getResolvedOverrideConfiguration();
        if (resolvedOverrideConfiguration.orientation != 0) {
            return;
        }
        Rect bounds = resolvedOverrideConfiguration.windowConfiguration.getBounds();
        if (bounds.isEmpty()) {
            bounds = configuration.windowConfiguration.getBounds();
        }
        if (isOrientationDifferentFromDisplay(bounds)) {
            int i = configuration.orientation;
            if (i == 1) {
                resolvedOverrideConfiguration.orientation = 2;
            } else if (i == 2) {
                resolvedOverrideConfiguration.orientation = 1;
            }
        }
    }
}
