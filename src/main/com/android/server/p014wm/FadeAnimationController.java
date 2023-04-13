package com.android.server.p014wm;

import android.content.Context;
import android.util.proto.ProtoOutputStream;
import android.view.SurfaceControl;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import com.android.server.p014wm.LocalAnimationAdapter;
import java.io.PrintWriter;
/* renamed from: com.android.server.wm.FadeAnimationController */
/* loaded from: classes2.dex */
public class FadeAnimationController {
    public final Context mContext;
    public final DisplayContent mDisplayContent;

    public FadeAnimationController(DisplayContent displayContent) {
        this.mDisplayContent = displayContent;
        this.mContext = displayContent.mWmService.mContext;
    }

    public Animation getFadeInAnimation() {
        return AnimationUtils.loadAnimation(this.mContext, 17432576);
    }

    public Animation getFadeOutAnimation() {
        return AnimationUtils.loadAnimation(this.mContext, 17432577);
    }

    public void fadeWindowToken(boolean z, WindowToken windowToken, int i) {
        if (windowToken == null || windowToken.getParent() == null) {
            return;
        }
        Animation fadeInAnimation = z ? getFadeInAnimation() : getFadeOutAnimation();
        FadeAnimationAdapter createAdapter = fadeInAnimation != null ? createAdapter(createAnimationSpec(fadeInAnimation), z, windowToken) : null;
        if (createAdapter == null) {
            return;
        }
        windowToken.startAnimation(windowToken.getPendingTransaction(), createAdapter, z, i, null);
    }

    public FadeAnimationAdapter createAdapter(LocalAnimationAdapter.AnimationSpec animationSpec, boolean z, WindowToken windowToken) {
        return new FadeAnimationAdapter(animationSpec, windowToken.getSurfaceAnimationRunner(), z, windowToken);
    }

    public LocalAnimationAdapter.AnimationSpec createAnimationSpec(final Animation animation) {
        return new LocalAnimationAdapter.AnimationSpec() { // from class: com.android.server.wm.FadeAnimationController.1
            public final Transformation mTransformation = new Transformation();

            @Override // com.android.server.p014wm.LocalAnimationAdapter.AnimationSpec
            public boolean getShowWallpaper() {
                return true;
            }

            @Override // com.android.server.p014wm.LocalAnimationAdapter.AnimationSpec
            public long getDuration() {
                return animation.getDuration();
            }

            @Override // com.android.server.p014wm.LocalAnimationAdapter.AnimationSpec
            public void apply(SurfaceControl.Transaction transaction, SurfaceControl surfaceControl, long j) {
                this.mTransformation.clear();
                animation.getTransformation(j, this.mTransformation);
                transaction.setAlpha(surfaceControl, this.mTransformation.getAlpha());
            }

            @Override // com.android.server.p014wm.LocalAnimationAdapter.AnimationSpec
            public void dump(PrintWriter printWriter, String str) {
                printWriter.print(str);
                printWriter.println(animation);
            }

            @Override // com.android.server.p014wm.LocalAnimationAdapter.AnimationSpec
            public void dumpDebugInner(ProtoOutputStream protoOutputStream) {
                long start = protoOutputStream.start(1146756268033L);
                protoOutputStream.write(1138166333441L, animation.toString());
                protoOutputStream.end(start);
            }
        };
    }

    /* renamed from: com.android.server.wm.FadeAnimationController$FadeAnimationAdapter */
    /* loaded from: classes2.dex */
    public static class FadeAnimationAdapter extends LocalAnimationAdapter {
        public final boolean mShow;
        public final WindowToken mToken;

        public FadeAnimationAdapter(LocalAnimationAdapter.AnimationSpec animationSpec, SurfaceAnimationRunner surfaceAnimationRunner, boolean z, WindowToken windowToken) {
            super(animationSpec, surfaceAnimationRunner);
            this.mShow = z;
            this.mToken = windowToken;
        }

        @Override // com.android.server.p014wm.AnimationAdapter
        public boolean shouldDeferAnimationFinish(Runnable runnable) {
            return !this.mShow;
        }
    }
}
