package com.android.server.p014wm;

import android.os.SystemClock;
import android.util.proto.ProtoOutputStream;
import android.view.SurfaceControl;
import com.android.server.p014wm.SurfaceAnimator;
import java.io.PrintWriter;
/* renamed from: com.android.server.wm.LocalAnimationAdapter */
/* loaded from: classes2.dex */
public class LocalAnimationAdapter implements AnimationAdapter {
    public final SurfaceAnimationRunner mAnimator;
    public final AnimationSpec mSpec;

    public LocalAnimationAdapter(AnimationSpec animationSpec, SurfaceAnimationRunner surfaceAnimationRunner) {
        this.mSpec = animationSpec;
        this.mAnimator = surfaceAnimationRunner;
    }

    @Override // com.android.server.p014wm.AnimationAdapter
    public boolean getShowWallpaper() {
        return this.mSpec.getShowWallpaper();
    }

    @Override // com.android.server.p014wm.AnimationAdapter
    public boolean getShowBackground() {
        return this.mSpec.getShowBackground();
    }

    @Override // com.android.server.p014wm.AnimationAdapter
    public int getBackgroundColor() {
        return this.mSpec.getBackgroundColor();
    }

    @Override // com.android.server.p014wm.AnimationAdapter
    public void startAnimation(SurfaceControl surfaceControl, SurfaceControl.Transaction transaction, final int i, final SurfaceAnimator.OnAnimationFinishedCallback onAnimationFinishedCallback) {
        this.mAnimator.startAnimation(this.mSpec, surfaceControl, transaction, new Runnable() { // from class: com.android.server.wm.LocalAnimationAdapter$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                LocalAnimationAdapter.this.lambda$startAnimation$0(onAnimationFinishedCallback, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startAnimation$0(SurfaceAnimator.OnAnimationFinishedCallback onAnimationFinishedCallback, int i) {
        onAnimationFinishedCallback.onAnimationFinished(i, this);
    }

    @Override // com.android.server.p014wm.AnimationAdapter
    public void onAnimationCancelled(SurfaceControl surfaceControl) {
        this.mAnimator.onAnimationCancelled(surfaceControl);
    }

    @Override // com.android.server.p014wm.AnimationAdapter
    public long getDurationHint() {
        return this.mSpec.getDuration();
    }

    @Override // com.android.server.p014wm.AnimationAdapter
    public long getStatusBarTransitionsStartTime() {
        return this.mSpec.calculateStatusBarTransitionStartTime();
    }

    @Override // com.android.server.p014wm.AnimationAdapter
    public void dump(PrintWriter printWriter, String str) {
        this.mSpec.dump(printWriter, str);
    }

    @Override // com.android.server.p014wm.AnimationAdapter
    public void dumpDebug(ProtoOutputStream protoOutputStream) {
        long start = protoOutputStream.start(1146756268033L);
        this.mSpec.dumpDebug(protoOutputStream, 1146756268033L);
        protoOutputStream.end(start);
    }

    /* renamed from: com.android.server.wm.LocalAnimationAdapter$AnimationSpec */
    /* loaded from: classes2.dex */
    public interface AnimationSpec {
        void apply(SurfaceControl.Transaction transaction, SurfaceControl surfaceControl, long j);

        default WindowAnimationSpec asWindowAnimationSpec() {
            return null;
        }

        default boolean canSkipFirstFrame() {
            return false;
        }

        void dump(PrintWriter printWriter, String str);

        void dumpDebugInner(ProtoOutputStream protoOutputStream);

        default int getBackgroundColor() {
            return 0;
        }

        long getDuration();

        default boolean getShowBackground() {
            return false;
        }

        default boolean getShowWallpaper() {
            return false;
        }

        default long calculateStatusBarTransitionStartTime() {
            return SystemClock.uptimeMillis();
        }

        default float getFraction(float f) {
            float duration = (float) getDuration();
            if (duration > 0.0f) {
                return f / duration;
            }
            return 1.0f;
        }

        default void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
            long start = protoOutputStream.start(j);
            dumpDebugInner(protoOutputStream);
            protoOutputStream.end(start);
        }
    }
}
