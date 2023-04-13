package com.android.server.p014wm;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.proto.ProtoOutputStream;
import android.view.DisplayInfo;
import android.view.SurfaceControl;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ClipRectAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import com.android.server.p014wm.LocalAnimationAdapter;
import java.io.PrintWriter;
import java.util.function.Supplier;
/* renamed from: com.android.server.wm.WindowChangeAnimationSpec */
/* loaded from: classes2.dex */
public class WindowChangeAnimationSpec implements LocalAnimationAdapter.AnimationSpec {
    public Animation mAnimation;
    public final Rect mEndBounds;
    public final boolean mIsAppAnimation;
    public final boolean mIsThumbnail;
    public final Rect mStartBounds;
    public final ThreadLocal<TmpValues> mThreadLocalTmps = ThreadLocal.withInitial(new Supplier() { // from class: com.android.server.wm.WindowChangeAnimationSpec$$ExternalSyntheticLambda0
        @Override // java.util.function.Supplier
        public final Object get() {
            return WindowChangeAnimationSpec.$r8$lambda$vitfewbiFedy3AD_blhhfyU1ohA();
        }
    });
    public final Rect mTmpRect = new Rect();

    public static /* synthetic */ TmpValues $r8$lambda$vitfewbiFedy3AD_blhhfyU1ohA() {
        return new TmpValues();
    }

    @Override // com.android.server.p014wm.LocalAnimationAdapter.AnimationSpec
    public boolean canSkipFirstFrame() {
        return false;
    }

    @Override // com.android.server.p014wm.LocalAnimationAdapter.AnimationSpec
    public boolean getShowWallpaper() {
        return false;
    }

    public WindowChangeAnimationSpec(Rect rect, Rect rect2, DisplayInfo displayInfo, float f, boolean z, boolean z2) {
        this.mStartBounds = new Rect(rect);
        this.mEndBounds = new Rect(rect2);
        this.mIsAppAnimation = z;
        this.mIsThumbnail = z2;
        createBoundsInterpolator((int) (f * 336.0f), displayInfo);
    }

    @Override // com.android.server.p014wm.LocalAnimationAdapter.AnimationSpec
    public long getDuration() {
        return this.mAnimation.getDuration();
    }

    public final void createBoundsInterpolator(long j, DisplayInfo displayInfo) {
        boolean z = ((this.mEndBounds.width() - this.mStartBounds.width()) + this.mEndBounds.height()) - this.mStartBounds.height() >= 0;
        long j2 = ((float) j) * 0.7f;
        float width = ((this.mStartBounds.width() * 0.7f) / this.mEndBounds.width()) + 0.3f;
        float height = ((this.mStartBounds.height() * 0.7f) / this.mEndBounds.height()) + 0.3f;
        if (this.mIsThumbnail) {
            AnimationSet animationSet = new AnimationSet(true);
            AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
            alphaAnimation.setDuration(j2);
            if (!z) {
                alphaAnimation.setStartOffset(j - j2);
            }
            animationSet.addAnimation(alphaAnimation);
            float f = 1.0f / width;
            float f2 = 1.0f / height;
            ScaleAnimation scaleAnimation = new ScaleAnimation(f, f, f2, f2);
            scaleAnimation.setDuration(j);
            animationSet.addAnimation(scaleAnimation);
            this.mAnimation = animationSet;
            animationSet.initialize(this.mStartBounds.width(), this.mStartBounds.height(), this.mEndBounds.width(), this.mEndBounds.height());
            return;
        }
        AnimationSet animationSet2 = new AnimationSet(true);
        ScaleAnimation scaleAnimation2 = new ScaleAnimation(width, 1.0f, height, 1.0f);
        scaleAnimation2.setDuration(j2);
        if (!z) {
            scaleAnimation2.setStartOffset(j - j2);
        }
        animationSet2.addAnimation(scaleAnimation2);
        Rect rect = this.mStartBounds;
        Rect rect2 = this.mEndBounds;
        TranslateAnimation translateAnimation = new TranslateAnimation(rect.left, rect2.left, rect.top, rect2.top);
        translateAnimation.setDuration(j);
        animationSet2.addAnimation(translateAnimation);
        Rect rect3 = new Rect(this.mStartBounds);
        Rect rect4 = new Rect(this.mEndBounds);
        rect3.offsetTo(0, 0);
        rect4.offsetTo(0, 0);
        ClipRectAnimation clipRectAnimation = new ClipRectAnimation(rect3, rect4);
        clipRectAnimation.setDuration(j);
        animationSet2.addAnimation(clipRectAnimation);
        this.mAnimation = animationSet2;
        animationSet2.initialize(this.mStartBounds.width(), this.mStartBounds.height(), displayInfo.appWidth, displayInfo.appHeight);
    }

    @Override // com.android.server.p014wm.LocalAnimationAdapter.AnimationSpec
    public void apply(SurfaceControl.Transaction transaction, SurfaceControl surfaceControl, long j) {
        TmpValues tmpValues = this.mThreadLocalTmps.get();
        if (this.mIsThumbnail) {
            this.mAnimation.getTransformation(j, tmpValues.mTransformation);
            transaction.setMatrix(surfaceControl, tmpValues.mTransformation.getMatrix(), tmpValues.mFloats);
            transaction.setAlpha(surfaceControl, tmpValues.mTransformation.getAlpha());
            return;
        }
        this.mAnimation.getTransformation(j, tmpValues.mTransformation);
        Matrix matrix = tmpValues.mTransformation.getMatrix();
        transaction.setMatrix(surfaceControl, matrix, tmpValues.mFloats);
        float[] fArr = tmpValues.mVecs;
        fArr[2] = 0.0f;
        fArr[1] = 0.0f;
        fArr[3] = 1.0f;
        fArr[0] = 1.0f;
        matrix.mapVectors(fArr);
        float[] fArr2 = tmpValues.mVecs;
        fArr2[0] = 1.0f / fArr2[0];
        fArr2[3] = 1.0f / fArr2[3];
        Rect clipRect = tmpValues.mTransformation.getClipRect();
        Rect rect = this.mTmpRect;
        float[] fArr3 = tmpValues.mVecs;
        float f = fArr3[0];
        rect.left = (int) ((clipRect.left * f) + 0.5f);
        rect.right = (int) ((clipRect.right * f) + 0.5f);
        float f2 = fArr3[3];
        rect.top = (int) ((clipRect.top * f2) + 0.5f);
        rect.bottom = (int) ((clipRect.bottom * f2) + 0.5f);
        transaction.setWindowCrop(surfaceControl, rect);
    }

    @Override // com.android.server.p014wm.LocalAnimationAdapter.AnimationSpec
    public long calculateStatusBarTransitionStartTime() {
        long uptimeMillis = SystemClock.uptimeMillis();
        return Math.max(uptimeMillis, ((((float) this.mAnimation.getDuration()) * 0.99f) + uptimeMillis) - 120);
    }

    @Override // com.android.server.p014wm.LocalAnimationAdapter.AnimationSpec
    public void dump(PrintWriter printWriter, String str) {
        printWriter.print(str);
        printWriter.println(this.mAnimation.getDuration());
    }

    @Override // com.android.server.p014wm.LocalAnimationAdapter.AnimationSpec
    public void dumpDebugInner(ProtoOutputStream protoOutputStream) {
        long start = protoOutputStream.start(1146756268033L);
        protoOutputStream.write(1138166333441L, this.mAnimation.toString());
        protoOutputStream.end(start);
    }

    /* renamed from: com.android.server.wm.WindowChangeAnimationSpec$TmpValues */
    /* loaded from: classes2.dex */
    public static class TmpValues {
        public final float[] mFloats;
        public final Transformation mTransformation;
        public final float[] mVecs;

        public TmpValues() {
            this.mTransformation = new Transformation();
            this.mFloats = new float[9];
            this.mVecs = new float[4];
        }
    }
}
