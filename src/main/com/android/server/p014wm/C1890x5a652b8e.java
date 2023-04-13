package com.android.server.p014wm;

import com.android.server.p014wm.ScreenRotationAnimation;
import com.android.server.p014wm.SurfaceAnimator;
/* compiled from: R8$$SyntheticClass */
/* renamed from: com.android.server.wm.ScreenRotationAnimation$SurfaceRotationAnimationController$$ExternalSyntheticLambda0 */
/* loaded from: classes2.dex */
public final /* synthetic */ class C1890x5a652b8e implements SurfaceAnimator.OnAnimationFinishedCallback {
    public final /* synthetic */ ScreenRotationAnimation.SurfaceRotationAnimationController f$0;

    public /* synthetic */ C1890x5a652b8e(ScreenRotationAnimation.SurfaceRotationAnimationController surfaceRotationAnimationController) {
        this.f$0 = surfaceRotationAnimationController;
    }

    @Override // com.android.server.p014wm.SurfaceAnimator.OnAnimationFinishedCallback
    public final void onAnimationFinished(int i, AnimationAdapter animationAdapter) {
        ScreenRotationAnimation.SurfaceRotationAnimationController.m8285$r8$lambda$953esEamEY0ik0NmGjvtRbkZo(this.f$0, i, animationAdapter);
    }
}
