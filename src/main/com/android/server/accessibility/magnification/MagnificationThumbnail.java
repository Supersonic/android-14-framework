package com.android.server.accessibility.magnification;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
/* loaded from: classes.dex */
public class MagnificationThumbnail {
    public final WindowManager.LayoutParams mBackgroundParams;
    public final Context mContext;
    public final Handler mHandler;
    public boolean mIsFadingIn;
    public ObjectAnimator mThumbNailAnimator;
    public final View mThumbNailView;
    @VisibleForTesting
    public final FrameLayout mThumbnailLayout;
    public boolean mVisible = false;
    public Rect mWindowBounds;
    public final WindowManager mWindowManager;

    public MagnificationThumbnail(Context context, WindowManager windowManager, Handler handler) {
        this.mContext = context;
        this.mWindowManager = windowManager;
        this.mHandler = handler;
        this.mWindowBounds = windowManager.getCurrentWindowMetrics().getBounds();
        FrameLayout frameLayout = (FrameLayout) LayoutInflater.from(context).inflate(17367359, (ViewGroup) null);
        this.mThumbnailLayout = frameLayout;
        this.mThumbNailView = frameLayout.findViewById(16908701);
        this.mBackgroundParams = createLayoutParams();
    }

    public void setThumbNailBounds(final Rect rect, final float f, final float f2, final float f3) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.accessibility.magnification.MagnificationThumbnail$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                MagnificationThumbnail.this.lambda$setThumbNailBounds$0(rect, f, f2, f3);
            }
        });
    }

    public /* synthetic */ void lambda$setThumbNailBounds$0(Rect rect, float f, float f2, float f3) {
        this.mWindowBounds = rect;
        setBackgroundBounds();
        if (this.mVisible) {
            lambda$updateThumbNail$1(f, f2, f3);
        }
    }

    public final void setBackgroundBounds() {
        Point magnificationThumbnailPadding = getMagnificationThumbnailPadding(this.mContext);
        int height = (int) (this.mWindowBounds.height() / 14.0f);
        int i = magnificationThumbnailPadding.x;
        int i2 = magnificationThumbnailPadding.y;
        WindowManager.LayoutParams layoutParams = this.mBackgroundParams;
        layoutParams.width = (int) (this.mWindowBounds.width() / 14.0f);
        layoutParams.height = height;
        layoutParams.x = i;
        layoutParams.y = i2;
    }

    public final void showThumbNail() {
        animateThumbnail(true);
    }

    public void hideThumbNail() {
        this.mHandler.post(new MagnificationThumbnail$$ExternalSyntheticLambda0(this));
    }

    public final void hideThumbNailMainThread() {
        if (this.mVisible) {
            animateThumbnail(false);
        }
    }

    public final void animateThumbnail(final boolean z) {
        this.mHandler.removeCallbacks(new MagnificationThumbnail$$ExternalSyntheticLambda0(this));
        if (z) {
            this.mHandler.postDelayed(new MagnificationThumbnail$$ExternalSyntheticLambda0(this), 500L);
        }
        if (z == this.mIsFadingIn) {
            return;
        }
        this.mIsFadingIn = z;
        if (z && !this.mVisible) {
            this.mWindowManager.addView(this.mThumbnailLayout, this.mBackgroundParams);
            this.mVisible = true;
        }
        ObjectAnimator objectAnimator = this.mThumbNailAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
        FrameLayout frameLayout = this.mThumbnailLayout;
        float[] fArr = new float[1];
        fArr[0] = z ? 1.0f : 0.0f;
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(frameLayout, "alpha", fArr);
        this.mThumbNailAnimator = ofFloat;
        ofFloat.setDuration(z ? 200L : 1000L);
        this.mThumbNailAnimator.addListener(new Animator.AnimatorListener() { // from class: com.android.server.accessibility.magnification.MagnificationThumbnail.1
            public boolean mIsCancelled;

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(@NonNull Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationStart(@NonNull Animator animator) {
            }

            {
                MagnificationThumbnail.this = this;
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(@NonNull Animator animator) {
                if (this.mIsCancelled || z || !MagnificationThumbnail.this.mVisible) {
                    return;
                }
                MagnificationThumbnail.this.mWindowManager.removeView(MagnificationThumbnail.this.mThumbnailLayout);
                MagnificationThumbnail.this.mVisible = false;
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationCancel(@NonNull Animator animator) {
                this.mIsCancelled = true;
            }
        });
        this.mThumbNailAnimator.start();
    }

    public void updateThumbNail(final float f, final float f2, final float f3) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.accessibility.magnification.MagnificationThumbnail$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                MagnificationThumbnail.this.lambda$updateThumbNail$1(f, f2, f3);
            }
        });
    }

    /* renamed from: updateThumbNailMainThread */
    public final void lambda$updateThumbNail$1(float f, float f2, float f3) {
        showThumbNail();
        float scaleX = Float.isNaN(f) ? this.mThumbNailView.getScaleX() : 1.0f / f;
        if (!Float.isNaN(f)) {
            this.mThumbNailView.setScaleX(scaleX);
            this.mThumbNailView.setScaleY(scaleX);
        }
        if (Float.isNaN(f2)) {
            return;
        }
        float height = (f3 * 0.071428575f) - (this.mThumbNailView.getHeight() / 2.0f);
        this.mThumbNailView.setTranslationX((f2 * 0.071428575f) - (this.mThumbNailView.getWidth() / 2.0f));
        this.mThumbNailView.setTranslationY(height);
    }

    public final WindowManager.LayoutParams createLayoutParams() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-2, -2, 2027, 24, -2);
        layoutParams.inputFeatures = 1;
        layoutParams.gravity = 83;
        layoutParams.setFitInsetsTypes(WindowInsets.Type.ime() | WindowInsets.Type.navigationBars());
        return layoutParams;
    }

    public final Point getMagnificationThumbnailPadding(Context context) {
        Point point = new Point(0, 0);
        int dimensionPixelSize = this.mContext.getResources().getDimensionPixelSize(17104910);
        point.x = dimensionPixelSize;
        point.y = dimensionPixelSize;
        return point;
    }
}
