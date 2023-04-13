package android.view;

import android.view.SyncRtSurfaceTransactionApplier;
import android.view.WindowInsetsAnimation;
/* loaded from: classes4.dex */
public interface InsetsAnimationControlCallbacks {
    void applySurfaceParams(SyncRtSurfaceTransactionApplier.SurfaceParams... surfaceParamsArr);

    void notifyFinished(InsetsAnimationControlRunner insetsAnimationControlRunner, boolean z);

    void releaseSurfaceControlFromRt(SurfaceControl surfaceControl);

    void reportPerceptible(int i, boolean z);

    void scheduleApplyChangeInsets(InsetsAnimationControlRunner insetsAnimationControlRunner);

    <T extends InsetsAnimationControlRunner & InternalInsetsAnimationController> void startAnimation(T t, WindowInsetsAnimationControlListener windowInsetsAnimationControlListener, int i, WindowInsetsAnimation windowInsetsAnimation, WindowInsetsAnimation.Bounds bounds);
}
