package android.inputmethodservice.navigationbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CanvasProperty;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RecordingCanvas;
import android.graphics.drawable.Drawable;
import android.p008os.Handler;
import android.p008os.Trace;
import android.view.RenderNodeAnimator;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import java.util.ArrayList;
import java.util.HashSet;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class KeyButtonRipple extends Drawable {
    private static final Interpolator ALPHA_OUT_INTERPOLATOR = new PathInterpolator(0.0f, 0.0f, 0.8f, 1.0f);
    private static final int ANIMATION_DURATION_FADE = 450;
    private static final int ANIMATION_DURATION_SCALE = 350;
    private static final float GLOW_MAX_ALPHA = 0.2f;
    private static final float GLOW_MAX_ALPHA_DARK = 0.1f;
    private static final float GLOW_MAX_SCALE_FACTOR = 1.35f;
    private CanvasProperty<Float> mBottomProp;
    private boolean mDark;
    private boolean mDelayTouchFeedback;
    private boolean mDrawingHardwareGlow;
    private boolean mLastDark;
    private CanvasProperty<Float> mLeftProp;
    private int mMaxWidth;
    private final int mMaxWidthResource;
    private CanvasProperty<Paint> mPaintProp;
    private boolean mPressed;
    private CanvasProperty<Float> mRightProp;
    private Paint mRipplePaint;
    private CanvasProperty<Float> mRxProp;
    private CanvasProperty<Float> mRyProp;
    private boolean mSupportHardware;
    private final View mTargetView;
    private CanvasProperty<Float> mTopProp;
    private boolean mVisible;
    private float mGlowAlpha = 0.0f;
    private float mGlowScale = 1.0f;
    private final Interpolator mInterpolator = new LogInterpolator();
    private final Handler mHandler = new Handler();
    private final HashSet<Animator> mRunningAnimations = new HashSet<>();
    private final ArrayList<Animator> mTmpArray = new ArrayList<>();
    private final TraceAnimatorListener mExitHwTraceAnimator = new TraceAnimatorListener("exitHardware");
    private final TraceAnimatorListener mEnterHwTraceAnimator = new TraceAnimatorListener("enterHardware");
    private Type mType = Type.ROUNDED_RECT;
    private final AnimatorListenerAdapter mAnimatorListener = new AnimatorListenerAdapter() { // from class: android.inputmethodservice.navigationbar.KeyButtonRipple.1
        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            KeyButtonRipple.this.mRunningAnimations.remove(animation);
            if (KeyButtonRipple.this.mRunningAnimations.isEmpty() && !KeyButtonRipple.this.mPressed) {
                KeyButtonRipple.this.mVisible = false;
                KeyButtonRipple.this.mDrawingHardwareGlow = false;
                KeyButtonRipple.this.invalidateSelf();
            }
        }
    };

    /* loaded from: classes2.dex */
    public enum Type {
        OVAL,
        ROUNDED_RECT
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public KeyButtonRipple(Context ctx, View targetView, int maxWidthResource) {
        this.mMaxWidthResource = maxWidthResource;
        this.mMaxWidth = ctx.getResources().getDimensionPixelSize(maxWidthResource);
        this.mTargetView = targetView;
    }

    public void updateResources() {
        this.mMaxWidth = this.mTargetView.getContext().getResources().getDimensionPixelSize(this.mMaxWidthResource);
        invalidateSelf();
    }

    public void setDarkIntensity(float darkIntensity) {
        this.mDark = darkIntensity >= 0.5f;
    }

    public void setDelayTouchFeedback(boolean delay) {
        this.mDelayTouchFeedback = delay;
    }

    public void setType(Type type) {
        this.mType = type;
    }

    private Paint getRipplePaint() {
        if (this.mRipplePaint == null) {
            Paint paint = new Paint();
            this.mRipplePaint = paint;
            paint.setAntiAlias(true);
            this.mRipplePaint.setColor(this.mLastDark ? -16777216 : -1);
        }
        return this.mRipplePaint;
    }

    private void drawSoftware(Canvas canvas) {
        if (this.mGlowAlpha > 0.0f) {
            Paint p = getRipplePaint();
            p.setAlpha((int) (this.mGlowAlpha * 255.0f));
            float w = getBounds().width();
            float h = getBounds().height();
            boolean horizontal = w > h;
            float diameter = getRippleSize() * this.mGlowScale;
            float radius = diameter * 0.5f;
            float cx = w * 0.5f;
            float cy = h * 0.5f;
            float rx = horizontal ? radius : cx;
            float ry = horizontal ? cy : radius;
            float corner = horizontal ? cy : cx;
            if (this.mType == Type.ROUNDED_RECT) {
                canvas.drawRoundRect(cx - rx, cy - ry, cx + rx, cy + ry, corner, corner, p);
                return;
            }
            canvas.save();
            canvas.translate(cx, cy);
            float r = Math.min(rx, ry);
            canvas.drawOval(-r, -r, r, r, p);
            canvas.restore();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        boolean isHardwareAccelerated = canvas.isHardwareAccelerated();
        this.mSupportHardware = isHardwareAccelerated;
        if (isHardwareAccelerated) {
            drawHardware((RecordingCanvas) canvas);
        } else {
            drawSoftware(canvas);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    private boolean isHorizontal() {
        return getBounds().width() > getBounds().height();
    }

    private void drawHardware(RecordingCanvas c) {
        if (this.mDrawingHardwareGlow) {
            if (this.mType == Type.ROUNDED_RECT) {
                c.drawRoundRect(this.mLeftProp, this.mTopProp, this.mRightProp, this.mBottomProp, this.mRxProp, this.mRyProp, this.mPaintProp);
                return;
            }
            CanvasProperty<Float> cx = CanvasProperty.createFloat(getBounds().width() / 2);
            CanvasProperty<Float> cy = CanvasProperty.createFloat(getBounds().height() / 2);
            int d = Math.min(getBounds().width(), getBounds().height());
            CanvasProperty<Float> r = CanvasProperty.createFloat((d * 1.0f) / 2.0f);
            c.drawCircle(cx, cy, r, this.mPaintProp);
        }
    }

    public float getGlowAlpha() {
        return this.mGlowAlpha;
    }

    public void setGlowAlpha(float x) {
        this.mGlowAlpha = x;
        invalidateSelf();
    }

    public float getGlowScale() {
        return this.mGlowScale;
    }

    public void setGlowScale(float x) {
        this.mGlowScale = x;
        invalidateSelf();
    }

    private float getMaxGlowAlpha() {
        return this.mLastDark ? 0.1f : 0.2f;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.drawable.Drawable
    public boolean onStateChange(int[] state) {
        boolean pressed = false;
        int i = 0;
        while (true) {
            if (i >= state.length) {
                break;
            } else if (state[i] != 16842919) {
                i++;
            } else {
                pressed = true;
                break;
            }
        }
        if (pressed != this.mPressed) {
            setPressed(pressed);
            this.mPressed = pressed;
            return true;
        }
        return false;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean setVisible(boolean visible, boolean restart) {
        boolean changed = super.setVisible(visible, restart);
        if (changed) {
            jumpToCurrentState();
        }
        return changed;
    }

    @Override // android.graphics.drawable.Drawable
    public void jumpToCurrentState() {
        endAnimations("jumpToCurrentState", false);
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        return true;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean hasFocusStateSpecified() {
        return true;
    }

    public void setPressed(boolean pressed) {
        boolean z = this.mDark;
        if (z != this.mLastDark && pressed) {
            this.mRipplePaint = null;
            this.mLastDark = z;
        }
        if (this.mSupportHardware) {
            setPressedHardware(pressed);
        } else {
            setPressedSoftware(pressed);
        }
    }

    public void abortDelayedRipple() {
        this.mHandler.removeCallbacksAndMessages(null);
    }

    private void endAnimations(String reason, boolean cancel) {
        Trace.beginSection("KeyButtonRipple.endAnim: reason=" + reason + " cancel=" + cancel);
        Trace.endSection();
        this.mVisible = false;
        this.mTmpArray.addAll(this.mRunningAnimations);
        int size = this.mTmpArray.size();
        for (int i = 0; i < size; i++) {
            Animator a = this.mTmpArray.get(i);
            if (cancel) {
                a.cancel();
            } else {
                a.end();
            }
        }
        this.mTmpArray.clear();
        this.mRunningAnimations.clear();
        this.mHandler.removeCallbacksAndMessages(null);
    }

    private void setPressedSoftware(boolean pressed) {
        if (pressed) {
            if (this.mDelayTouchFeedback) {
                if (this.mRunningAnimations.isEmpty()) {
                    this.mHandler.removeCallbacksAndMessages(null);
                    this.mHandler.postDelayed(new Runnable() { // from class: android.inputmethodservice.navigationbar.KeyButtonRipple$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            KeyButtonRipple.this.enterSoftware();
                        }
                    }, ViewConfiguration.getTapTimeout());
                    return;
                } else if (this.mVisible) {
                    enterSoftware();
                    return;
                } else {
                    return;
                }
            }
            enterSoftware();
            return;
        }
        exitSoftware();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enterSoftware() {
        endAnimations("enterSoftware", true);
        this.mVisible = true;
        this.mGlowAlpha = getMaxGlowAlpha();
        ObjectAnimator scaleAnimator = ObjectAnimator.ofFloat(this, "glowScale", 0.0f, GLOW_MAX_SCALE_FACTOR);
        scaleAnimator.setInterpolator(this.mInterpolator);
        scaleAnimator.setDuration(350L);
        scaleAnimator.addListener(this.mAnimatorListener);
        scaleAnimator.start();
        this.mRunningAnimations.add(scaleAnimator);
        if (this.mDelayTouchFeedback && !this.mPressed) {
            exitSoftware();
        }
    }

    private void exitSoftware() {
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(this, "glowAlpha", this.mGlowAlpha, 0.0f);
        alphaAnimator.setInterpolator(ALPHA_OUT_INTERPOLATOR);
        alphaAnimator.setDuration(450L);
        alphaAnimator.addListener(this.mAnimatorListener);
        alphaAnimator.start();
        this.mRunningAnimations.add(alphaAnimator);
    }

    private void setPressedHardware(boolean pressed) {
        if (pressed) {
            if (this.mDelayTouchFeedback) {
                if (this.mRunningAnimations.isEmpty()) {
                    this.mHandler.removeCallbacksAndMessages(null);
                    this.mHandler.postDelayed(new Runnable() { // from class: android.inputmethodservice.navigationbar.KeyButtonRipple$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            KeyButtonRipple.this.enterHardware();
                        }
                    }, ViewConfiguration.getTapTimeout());
                    return;
                } else if (this.mVisible) {
                    enterHardware();
                    return;
                } else {
                    return;
                }
            }
            enterHardware();
            return;
        }
        exitHardware();
    }

    private void setExtendStart(CanvasProperty<Float> prop) {
        if (isHorizontal()) {
            this.mLeftProp = prop;
        } else {
            this.mTopProp = prop;
        }
    }

    private CanvasProperty<Float> getExtendStart() {
        return isHorizontal() ? this.mLeftProp : this.mTopProp;
    }

    private void setExtendEnd(CanvasProperty<Float> prop) {
        if (isHorizontal()) {
            this.mRightProp = prop;
        } else {
            this.mBottomProp = prop;
        }
    }

    private CanvasProperty<Float> getExtendEnd() {
        return isHorizontal() ? this.mRightProp : this.mBottomProp;
    }

    private int getExtendSize() {
        return isHorizontal() ? getBounds().width() : getBounds().height();
    }

    private int getRippleSize() {
        int size = isHorizontal() ? getBounds().width() : getBounds().height();
        return Math.min(size, this.mMaxWidth);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enterHardware() {
        endAnimations("enterHardware", true);
        this.mVisible = true;
        this.mDrawingHardwareGlow = true;
        setExtendStart(CanvasProperty.createFloat(getExtendSize() / 2));
        RenderNodeAnimator startAnim = new RenderNodeAnimator(getExtendStart(), (getExtendSize() / 2) - ((getRippleSize() * GLOW_MAX_SCALE_FACTOR) / 2.0f));
        startAnim.setDuration(350L);
        startAnim.setInterpolator(this.mInterpolator);
        startAnim.addListener(this.mAnimatorListener);
        startAnim.setTarget(this.mTargetView);
        setExtendEnd(CanvasProperty.createFloat(getExtendSize() / 2));
        RenderNodeAnimator endAnim = new RenderNodeAnimator(getExtendEnd(), (getExtendSize() / 2) + ((getRippleSize() * GLOW_MAX_SCALE_FACTOR) / 2.0f));
        endAnim.setDuration(350L);
        endAnim.setInterpolator(this.mInterpolator);
        endAnim.addListener(this.mAnimatorListener);
        endAnim.addListener(this.mEnterHwTraceAnimator);
        endAnim.setTarget(this.mTargetView);
        if (isHorizontal()) {
            this.mTopProp = CanvasProperty.createFloat(0.0f);
            this.mBottomProp = CanvasProperty.createFloat(getBounds().height());
            this.mRxProp = CanvasProperty.createFloat(getBounds().height() / 2);
            this.mRyProp = CanvasProperty.createFloat(getBounds().height() / 2);
        } else {
            this.mLeftProp = CanvasProperty.createFloat(0.0f);
            this.mRightProp = CanvasProperty.createFloat(getBounds().width());
            this.mRxProp = CanvasProperty.createFloat(getBounds().width() / 2);
            this.mRyProp = CanvasProperty.createFloat(getBounds().width() / 2);
        }
        this.mGlowScale = GLOW_MAX_SCALE_FACTOR;
        this.mGlowAlpha = getMaxGlowAlpha();
        Paint ripplePaint = getRipplePaint();
        this.mRipplePaint = ripplePaint;
        ripplePaint.setAlpha((int) (this.mGlowAlpha * 255.0f));
        this.mPaintProp = CanvasProperty.createPaint(this.mRipplePaint);
        startAnim.start();
        endAnim.start();
        this.mRunningAnimations.add(startAnim);
        this.mRunningAnimations.add(endAnim);
        invalidateSelf();
        if (this.mDelayTouchFeedback && !this.mPressed) {
            exitHardware();
        }
    }

    private void exitHardware() {
        this.mPaintProp = CanvasProperty.createPaint(getRipplePaint());
        RenderNodeAnimator opacityAnim = new RenderNodeAnimator(this.mPaintProp, 1, 0.0f);
        opacityAnim.setDuration(450L);
        opacityAnim.setInterpolator(ALPHA_OUT_INTERPOLATOR);
        opacityAnim.addListener(this.mAnimatorListener);
        opacityAnim.addListener(this.mExitHwTraceAnimator);
        opacityAnim.setTarget(this.mTargetView);
        opacityAnim.start();
        this.mRunningAnimations.add(opacityAnim);
        invalidateSelf();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static final class TraceAnimatorListener extends AnimatorListenerAdapter {
        private final String mName;

        TraceAnimatorListener(String name) {
            this.mName = name;
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animation) {
            Trace.beginSection("KeyButtonRipple.start." + this.mName);
            Trace.endSection();
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animation) {
            Trace.beginSection("KeyButtonRipple.cancel." + this.mName);
            Trace.endSection();
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            Trace.beginSection("KeyButtonRipple.end." + this.mName);
            Trace.endSection();
        }
    }

    /* loaded from: classes2.dex */
    private static final class LogInterpolator implements Interpolator {
        private LogInterpolator() {
        }

        @Override // android.animation.TimeInterpolator
        public float getInterpolation(float input) {
            return 1.0f - ((float) Math.pow(400.0d, (-input) * 1.4d));
        }
    }
}
