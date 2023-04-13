package com.android.server.p014wm;

import android.view.SurfaceControl;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import com.android.server.p014wm.FadeAnimationController;
import com.android.server.p014wm.LocalAnimationAdapter;
import com.android.server.p014wm.SurfaceAnimator;
/* renamed from: com.android.server.wm.NavBarFadeAnimationController */
/* loaded from: classes2.dex */
public class NavBarFadeAnimationController extends FadeAnimationController {
    public static final Interpolator FADE_IN_INTERPOLATOR = new PathInterpolator(0.0f, 0.0f, 0.0f, 1.0f);
    public static final Interpolator FADE_OUT_INTERPOLATOR = new PathInterpolator(0.2f, 0.0f, 1.0f, 1.0f);
    public Animation mFadeInAnimation;
    public SurfaceControl mFadeInParent;
    public Animation mFadeOutAnimation;
    public SurfaceControl mFadeOutParent;
    public final WindowState mNavigationBar;
    public boolean mPlaySequentially;

    public NavBarFadeAnimationController(DisplayContent displayContent) {
        super(displayContent);
        this.mPlaySequentially = false;
        this.mNavigationBar = displayContent.getDisplayPolicy().getNavigationBar();
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        this.mFadeInAnimation = alphaAnimation;
        alphaAnimation.setDuration(266L);
        this.mFadeInAnimation.setInterpolator(FADE_IN_INTERPOLATOR);
        AlphaAnimation alphaAnimation2 = new AlphaAnimation(1.0f, 0.0f);
        this.mFadeOutAnimation = alphaAnimation2;
        alphaAnimation2.setDuration(133L);
        this.mFadeOutAnimation.setInterpolator(FADE_OUT_INTERPOLATOR);
    }

    @Override // com.android.server.p014wm.FadeAnimationController
    public Animation getFadeInAnimation() {
        return this.mFadeInAnimation;
    }

    @Override // com.android.server.p014wm.FadeAnimationController
    public Animation getFadeOutAnimation() {
        return this.mFadeOutAnimation;
    }

    @Override // com.android.server.p014wm.FadeAnimationController
    public FadeAnimationController.FadeAnimationAdapter createAdapter(LocalAnimationAdapter.AnimationSpec animationSpec, boolean z, WindowToken windowToken) {
        return new NavFadeAnimationAdapter(animationSpec, windowToken.getSurfaceAnimationRunner(), z, windowToken, z ? this.mFadeInParent : this.mFadeOutParent);
    }

    public void fadeWindowToken(final boolean z) {
        AsyncRotationController asyncRotationController = this.mDisplayContent.getAsyncRotationController();
        Runnable runnable = new Runnable() { // from class: com.android.server.wm.NavBarFadeAnimationController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                NavBarFadeAnimationController.this.lambda$fadeWindowToken$0(z);
            }
        };
        if (asyncRotationController == null) {
            runnable.run();
        } else if (asyncRotationController.isTargetToken(this.mNavigationBar.mToken)) {
        } else {
            if (z) {
                asyncRotationController.setOnShowRunnable(runnable);
            } else {
                runnable.run();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$fadeWindowToken$0(boolean z) {
        fadeWindowToken(z, this.mNavigationBar.mToken, 64);
    }

    public void fadeOutAndInSequentially(long j, SurfaceControl surfaceControl, SurfaceControl surfaceControl2) {
        this.mPlaySequentially = true;
        if (j > 0) {
            long j2 = (2 * j) / 3;
            this.mFadeOutAnimation.setDuration(j - j2);
            this.mFadeInAnimation.setDuration(j2);
        }
        this.mFadeOutParent = surfaceControl;
        this.mFadeInParent = surfaceControl2;
        fadeWindowToken(false);
    }

    /* renamed from: com.android.server.wm.NavBarFadeAnimationController$NavFadeAnimationAdapter */
    /* loaded from: classes2.dex */
    public class NavFadeAnimationAdapter extends FadeAnimationController.FadeAnimationAdapter {
        public SurfaceControl mParent;

        public NavFadeAnimationAdapter(LocalAnimationAdapter.AnimationSpec animationSpec, SurfaceAnimationRunner surfaceAnimationRunner, boolean z, WindowToken windowToken, SurfaceControl surfaceControl) {
            super(animationSpec, surfaceAnimationRunner, z, windowToken);
            this.mParent = surfaceControl;
        }

        @Override // com.android.server.p014wm.LocalAnimationAdapter, com.android.server.p014wm.AnimationAdapter
        public void startAnimation(SurfaceControl surfaceControl, SurfaceControl.Transaction transaction, int i, SurfaceAnimator.OnAnimationFinishedCallback onAnimationFinishedCallback) {
            super.startAnimation(surfaceControl, transaction, i, onAnimationFinishedCallback);
            SurfaceControl surfaceControl2 = this.mParent;
            if (surfaceControl2 == null || !surfaceControl2.isValid()) {
                return;
            }
            transaction.reparent(surfaceControl, this.mParent);
            transaction.setLayer(surfaceControl, Integer.MAX_VALUE);
        }

        @Override // com.android.server.p014wm.FadeAnimationController.FadeAnimationAdapter, com.android.server.p014wm.AnimationAdapter
        public boolean shouldDeferAnimationFinish(Runnable runnable) {
            if (NavBarFadeAnimationController.this.mPlaySequentially) {
                if (this.mShow) {
                    return false;
                }
                NavBarFadeAnimationController.this.fadeWindowToken(true);
                return false;
            }
            return super.shouldDeferAnimationFinish(runnable);
        }
    }
}
