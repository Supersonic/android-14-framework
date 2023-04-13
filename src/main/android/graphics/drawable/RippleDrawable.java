package android.graphics.drawable;

import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.CanvasProperty;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RecordingCanvas;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleAnimationSession;
import android.p008os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import com.android.internal.C4057R;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class RippleDrawable extends LayerDrawable {
    private static final int BACKGROUND_OPACITY_DURATION = 80;
    private static final int DEFAULT_EFFECT_COLOR = -1912602625;
    private static final boolean FORCE_PATTERNED_STYLE = true;
    private static final LinearInterpolator LINEAR_INTERPOLATOR = new LinearInterpolator();
    private static final int MASK_CONTENT = 1;
    private static final int MASK_EXPLICIT = 2;
    private static final int MASK_NONE = 0;
    private static final int MASK_UNKNOWN = -1;
    private static final int MAX_RIPPLES = 10;
    public static final int RADIUS_AUTO = -1;
    public static final int STYLE_PATTERNED = 1;
    public static final int STYLE_SOLID = 0;
    private static final String TAG = "RippleDrawable";
    private boolean mAddRipple;
    private RippleBackground mBackground;
    private ValueAnimator mBackgroundAnimation;
    private float mBackgroundOpacity;
    private int mDensity;
    private final Rect mDirtyBounds;
    private final Rect mDrawingBounds;
    private boolean mExitingAnimation;
    private RippleForeground[] mExitingRipples;
    private int mExitingRipplesCount;
    private PorterDuffColorFilter mFocusColorFilter;
    private boolean mForceSoftware;
    private boolean mHasPending;
    private boolean mHasValidMask;
    private final Rect mHotspotBounds;
    private Drawable mMask;
    private Bitmap mMaskBuffer;
    private Canvas mMaskCanvas;
    private PorterDuffColorFilter mMaskColorFilter;
    private Matrix mMaskMatrix;
    private BitmapShader mMaskShader;
    private boolean mOverrideBounds;
    private float mPendingX;
    private float mPendingY;
    private RippleForeground mRipple;
    private boolean mRippleActive;
    private Paint mRipplePaint;
    private boolean mRunBackgroundAnimation;
    private ArrayList<RippleAnimationSession> mRunningAnimations;
    private RippleState mState;
    private float mTargetBackgroundOpacity;
    private final Rect mTempRect;

    @Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.LOCAL_VARIABLE, ElementType.FIELD})
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface RippleStyle {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RippleDrawable() {
        this(new RippleState(null, null, null), null);
    }

    public RippleDrawable(ColorStateList color, Drawable content, Drawable mask) {
        this(new RippleState(null, null, null), null);
        if (color == null) {
            throw new IllegalArgumentException("RippleDrawable requires a non-null color");
        }
        if (content != null) {
            addLayer(content, null, 0, 0, 0, 0, 0);
        }
        if (mask != null) {
            addLayer(mask, null, 16908334, 0, 0, 0, 0);
        }
        setColor(color);
        ensurePadding();
        refreshPadding();
        updateLocalState();
    }

    @Override // android.graphics.drawable.LayerDrawable, android.graphics.drawable.Drawable
    public void jumpToCurrentState() {
        super.jumpToCurrentState();
        RippleForeground rippleForeground = this.mRipple;
        if (rippleForeground != null) {
            rippleForeground.end();
        }
        RippleBackground rippleBackground = this.mBackground;
        if (rippleBackground != null) {
            rippleBackground.jumpToFinal();
        }
        cancelExitingRipples();
        endPatternedAnimations();
    }

    private void endPatternedAnimations() {
        for (int i = 0; i < this.mRunningAnimations.size(); i++) {
            RippleAnimationSession session = this.mRunningAnimations.get(i);
            session.end();
        }
        this.mRunningAnimations.clear();
    }

    private void cancelExitingRipples() {
        int count = this.mExitingRipplesCount;
        RippleForeground[] ripples = this.mExitingRipples;
        for (int i = 0; i < count; i++) {
            ripples[i].end();
        }
        if (ripples != null) {
            Arrays.fill(ripples, 0, count, (Object) null);
        }
        this.mExitingRipplesCount = 0;
        invalidateSelf(false);
    }

    @Override // android.graphics.drawable.LayerDrawable, android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.drawable.LayerDrawable, android.graphics.drawable.Drawable
    public boolean onStateChange(int[] stateSet) {
        boolean changed = super.onStateChange(stateSet);
        boolean enabled = false;
        boolean pressed = false;
        boolean focused = false;
        boolean hovered = false;
        boolean windowFocused = false;
        boolean z = false;
        for (int state : stateSet) {
            if (state == 16842910) {
                enabled = true;
            } else if (state == 16842908) {
                focused = true;
            } else if (state == 16842919) {
                pressed = true;
            } else if (state == 16843623) {
                hovered = true;
            } else if (state == 16842909) {
                windowFocused = true;
            }
        }
        if (enabled && pressed) {
            z = true;
        }
        setRippleActive(z);
        setBackgroundActive(hovered, focused, pressed, windowFocused);
        return changed;
    }

    private void setRippleActive(boolean active) {
        if (this.mRippleActive != active) {
            this.mRippleActive = active;
            if (this.mState.mRippleStyle == 0) {
                if (active) {
                    tryRippleEnter();
                } else {
                    tryRippleExit();
                }
            } else if (active) {
                startPatternedAnimation();
            } else {
                exitPatternedAnimation();
            }
        }
    }

    public void setBackgroundActive(boolean hovered, boolean focused, boolean pressed, boolean windowFocused) {
        if (this.mState.mRippleStyle == 0) {
            if (this.mBackground == null && (hovered || focused)) {
                RippleBackground rippleBackground = new RippleBackground(this, this.mHotspotBounds, isBounded());
                this.mBackground = rippleBackground;
                rippleBackground.setup(this.mState.mMaxRadius, this.mDensity);
            }
            RippleBackground rippleBackground2 = this.mBackground;
            if (rippleBackground2 != null) {
                rippleBackground2.setState(focused, hovered, pressed);
            }
        } else if (focused || hovered) {
            if (!pressed) {
                enterPatternedBackgroundAnimation(focused, hovered, windowFocused);
            }
        } else {
            exitPatternedBackgroundAnimation();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.drawable.LayerDrawable, android.graphics.drawable.Drawable
    public void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        if (!this.mOverrideBounds) {
            this.mHotspotBounds.set(bounds);
            onHotspotBoundsChanged();
        }
        int count = this.mExitingRipplesCount;
        RippleForeground[] ripples = this.mExitingRipples;
        for (int i = 0; i < count; i++) {
            ripples[i].onBoundsChange();
        }
        RippleBackground rippleBackground = this.mBackground;
        if (rippleBackground != null) {
            rippleBackground.onBoundsChange();
        }
        RippleForeground rippleForeground = this.mRipple;
        if (rippleForeground != null) {
            rippleForeground.onBoundsChange();
        }
        invalidateSelf();
    }

    @Override // android.graphics.drawable.LayerDrawable, android.graphics.drawable.Drawable
    public boolean setVisible(boolean visible, boolean restart) {
        boolean changed = super.setVisible(visible, restart);
        if (!visible) {
            clearHotspots();
        } else if (changed) {
            if (this.mRippleActive) {
                if (this.mState.mRippleStyle == 0) {
                    tryRippleEnter();
                } else {
                    invalidateSelf();
                }
            }
            jumpToCurrentState();
        }
        return changed;
    }

    @Override // android.graphics.drawable.LayerDrawable, android.graphics.drawable.Drawable
    public boolean isProjected() {
        if (isBounded()) {
            return false;
        }
        int radius = this.mState.mMaxRadius;
        Rect drawableBounds = getBounds();
        Rect hotspotBounds = this.mHotspotBounds;
        if (radius == -1 || radius > hotspotBounds.width() / 2 || radius > hotspotBounds.height() / 2) {
            return true;
        }
        return (drawableBounds.equals(hotspotBounds) || drawableBounds.contains(hotspotBounds)) ? false : true;
    }

    private boolean isBounded() {
        return getNumberOfLayers() > 0;
    }

    @Override // android.graphics.drawable.LayerDrawable, android.graphics.drawable.Drawable
    public boolean isStateful() {
        return true;
    }

    @Override // android.graphics.drawable.LayerDrawable, android.graphics.drawable.Drawable
    public boolean hasFocusStateSpecified() {
        return true;
    }

    public void setColor(ColorStateList color) {
        if (color == null) {
            throw new IllegalArgumentException("color cannot be null");
        }
        this.mState.mColor = color;
        invalidateSelf(false);
    }

    public void setEffectColor(ColorStateList color) {
        if (color == null) {
            throw new IllegalArgumentException("color cannot be null");
        }
        this.mState.mEffectColor = color;
        invalidateSelf(false);
    }

    public ColorStateList getEffectColor() {
        return this.mState.mEffectColor;
    }

    public void setRadius(int radius) {
        this.mState.mMaxRadius = radius;
        invalidateSelf(false);
    }

    public int getRadius() {
        return this.mState.mMaxRadius;
    }

    @Override // android.graphics.drawable.LayerDrawable, android.graphics.drawable.Drawable
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs, Resources.Theme theme) throws XmlPullParserException, IOException {
        TypedArray a = obtainAttributes(r, theme, attrs, C4057R.styleable.RippleDrawable);
        setPaddingMode(1);
        super.inflate(r, parser, attrs, theme);
        updateStateFromTypedArray(a);
        verifyRequiredAttributes(a);
        a.recycle();
        updateLocalState();
    }

    @Override // android.graphics.drawable.LayerDrawable
    public boolean setDrawableByLayerId(int id, Drawable drawable) {
        if (super.setDrawableByLayerId(id, drawable)) {
            if (id == 16908334) {
                this.mMask = drawable;
                this.mHasValidMask = false;
                return true;
            }
            return true;
        }
        return false;
    }

    @Override // android.graphics.drawable.LayerDrawable
    public void setPaddingMode(int mode) {
        super.setPaddingMode(mode);
    }

    private void updateStateFromTypedArray(TypedArray a) throws XmlPullParserException {
        RippleState state = this.mState;
        state.mChangingConfigurations |= a.getChangingConfigurations();
        state.mTouchThemeAttrs = a.extractThemeAttrs();
        ColorStateList color = a.getColorStateList(0);
        if (color != null) {
            this.mState.mColor = color;
        }
        ColorStateList effectColor = a.getColorStateList(2);
        if (effectColor != null) {
            this.mState.mEffectColor = effectColor;
        }
        RippleState rippleState = this.mState;
        rippleState.mMaxRadius = a.getDimensionPixelSize(1, rippleState.mMaxRadius);
    }

    private void verifyRequiredAttributes(TypedArray a) throws XmlPullParserException {
        if (this.mState.mColor == null) {
            if (this.mState.mTouchThemeAttrs == null || this.mState.mTouchThemeAttrs[0] == 0) {
                throw new XmlPullParserException(a.getPositionDescription() + ": <ripple> requires a valid color attribute");
            }
        }
    }

    @Override // android.graphics.drawable.LayerDrawable, android.graphics.drawable.Drawable
    public void applyTheme(Resources.Theme t) {
        super.applyTheme(t);
        RippleState state = this.mState;
        if (state == null) {
            return;
        }
        if (state.mTouchThemeAttrs != null) {
            TypedArray a = t.resolveAttributes(state.mTouchThemeAttrs, C4057R.styleable.RippleDrawable);
            try {
                try {
                    updateStateFromTypedArray(a);
                    verifyRequiredAttributes(a);
                } catch (XmlPullParserException e) {
                    rethrowAsRuntimeException(e);
                }
            } finally {
                a.recycle();
            }
        }
        if (state.mColor != null && state.mColor.canApplyTheme()) {
            state.mColor = state.mColor.obtainForTheme(t);
        }
        updateLocalState();
    }

    @Override // android.graphics.drawable.LayerDrawable, android.graphics.drawable.Drawable
    public boolean canApplyTheme() {
        RippleState rippleState = this.mState;
        return (rippleState != null && rippleState.canApplyTheme()) || super.canApplyTheme();
    }

    @Override // android.graphics.drawable.LayerDrawable, android.graphics.drawable.Drawable
    public void setHotspot(float x, float y) {
        this.mPendingX = x;
        this.mPendingY = y;
        RippleForeground rippleForeground = this.mRipple;
        if (rippleForeground == null || this.mBackground == null) {
            this.mHasPending = true;
        }
        if (rippleForeground != null) {
            rippleForeground.move(x, y);
        }
    }

    private void tryRippleEnter() {
        float x;
        float y;
        if (this.mExitingRipplesCount >= 10) {
            return;
        }
        if (this.mRipple == null) {
            if (this.mHasPending) {
                this.mHasPending = false;
                x = this.mPendingX;
                y = this.mPendingY;
            } else {
                x = this.mHotspotBounds.exactCenterX();
                y = this.mHotspotBounds.exactCenterY();
            }
            this.mRipple = new RippleForeground(this, this.mHotspotBounds, x, y, this.mForceSoftware);
        }
        this.mRipple.setup(this.mState.mMaxRadius, this.mDensity);
        this.mRipple.enter();
    }

    private void tryRippleExit() {
        RippleForeground rippleForeground = this.mRipple;
        if (rippleForeground != null) {
            if (this.mExitingRipples == null) {
                this.mExitingRipples = new RippleForeground[10];
            }
            RippleForeground[] rippleForegroundArr = this.mExitingRipples;
            int i = this.mExitingRipplesCount;
            this.mExitingRipplesCount = i + 1;
            rippleForegroundArr[i] = rippleForeground;
            rippleForeground.exit();
            this.mRipple = null;
        }
    }

    private void clearHotspots() {
        RippleForeground rippleForeground = this.mRipple;
        if (rippleForeground != null) {
            rippleForeground.end();
            this.mRipple = null;
            this.mRippleActive = false;
        }
        RippleBackground rippleBackground = this.mBackground;
        if (rippleBackground != null) {
            rippleBackground.setState(false, false, false);
        }
        cancelExitingRipples();
        endPatternedAnimations();
    }

    @Override // android.graphics.drawable.LayerDrawable, android.graphics.drawable.Drawable
    public void setHotspotBounds(int left, int top, int right, int bottom) {
        this.mOverrideBounds = true;
        this.mHotspotBounds.set(left, top, right, bottom);
        onHotspotBoundsChanged();
    }

    @Override // android.graphics.drawable.LayerDrawable, android.graphics.drawable.Drawable
    public void getHotspotBounds(Rect outRect) {
        outRect.set(this.mHotspotBounds);
    }

    private void onHotspotBoundsChanged() {
        int count = this.mExitingRipplesCount;
        RippleForeground[] ripples = this.mExitingRipples;
        for (int i = 0; i < count; i++) {
            ripples[i].onHotspotBoundsChanged();
        }
        RippleForeground rippleForeground = this.mRipple;
        if (rippleForeground != null) {
            rippleForeground.onHotspotBoundsChanged();
        }
        RippleBackground rippleBackground = this.mBackground;
        if (rippleBackground != null) {
            rippleBackground.onHotspotBoundsChanged();
        }
        float newRadius = getComputedRadius();
        for (int i2 = 0; i2 < this.mRunningAnimations.size(); i2++) {
            RippleAnimationSession s = this.mRunningAnimations.get(i2);
            s.setRadius(newRadius);
            s.getProperties().getShader().setResolution(this.mHotspotBounds.width(), this.mHotspotBounds.height());
            float cx = this.mHotspotBounds.centerX();
            float cy = this.mHotspotBounds.centerY();
            s.getProperties().getShader().setOrigin(cx, cy);
            s.getProperties().setOrigin(Float.valueOf(cx), Float.valueOf(cy));
            if (!s.isForceSoftware()) {
                s.getCanvasProperties().setOrigin(CanvasProperty.createFloat(cx), CanvasProperty.createFloat(cy));
            }
        }
    }

    @Override // android.graphics.drawable.LayerDrawable, android.graphics.drawable.Drawable
    public void getOutline(Outline outline) {
        LayerDrawable.LayerState state = this.mLayerState;
        LayerDrawable.ChildDrawable[] children = state.mChildren;
        int N = state.mNumChildren;
        for (int i = 0; i < N; i++) {
            if (children[i].mId != 16908334) {
                children[i].mDrawable.getOutline(outline);
                if (!outline.isEmpty()) {
                    return;
                }
            }
        }
    }

    @Override // android.graphics.drawable.LayerDrawable, android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        if (this.mState.mRippleStyle == 0) {
            drawSolid(canvas);
        } else {
            drawPatterned(canvas);
        }
    }

    private void drawSolid(Canvas canvas) {
        pruneRipples();
        Rect bounds = getDirtyBounds();
        int saveCount = canvas.save(2);
        if (isBounded()) {
            canvas.clipRect(bounds);
        }
        drawContent(canvas);
        drawBackgroundAndRipples(canvas);
        canvas.restoreToCount(saveCount);
    }

    private void exitPatternedBackgroundAnimation() {
        this.mTargetBackgroundOpacity = 0.0f;
        ValueAnimator valueAnimator = this.mBackgroundAnimation;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.mRunBackgroundAnimation = true;
        invalidateSelf(false);
    }

    private void startPatternedAnimation() {
        this.mAddRipple = true;
        invalidateSelf(false);
    }

    private void exitPatternedAnimation() {
        this.mExitingAnimation = true;
        invalidateSelf(false);
    }

    public float getTargetBackgroundOpacity() {
        return this.mTargetBackgroundOpacity;
    }

    private void enterPatternedBackgroundAnimation(boolean focused, boolean hovered, boolean windowFocused) {
        float f = 0.0f;
        this.mBackgroundOpacity = 0.0f;
        if (focused) {
            this.mTargetBackgroundOpacity = windowFocused ? 0.6f : 0.2f;
        } else {
            if (hovered) {
                f = 0.2f;
            }
            this.mTargetBackgroundOpacity = f;
        }
        ValueAnimator valueAnimator = this.mBackgroundAnimation;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.mRunBackgroundAnimation = true;
        invalidateSelf(false);
    }

    private void startBackgroundAnimation() {
        this.mRunBackgroundAnimation = false;
        if (Looper.myLooper() == null) {
            Log.m104w(TAG, "Thread doesn't have a looper. Skipping animation.");
            return;
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(this.mBackgroundOpacity, this.mTargetBackgroundOpacity);
        this.mBackgroundAnimation = ofFloat;
        ofFloat.setInterpolator(LINEAR_INTERPOLATOR);
        this.mBackgroundAnimation.setDuration(80L);
        this.mBackgroundAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: android.graphics.drawable.RippleDrawable$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                RippleDrawable.this.lambda$startBackgroundAnimation$0(valueAnimator);
            }
        });
        this.mBackgroundAnimation.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startBackgroundAnimation$0(ValueAnimator update) {
        this.mBackgroundOpacity = ((Float) update.getAnimatedValue()).floatValue();
        invalidateSelf(false);
    }

    private void drawPatterned(Canvas canvas) {
        boolean shouldExit;
        float x;
        float y;
        Rect bounds = this.mHotspotBounds;
        int saveCount = canvas.save(2);
        boolean useCanvasProps = !this.mForceSoftware;
        if (isBounded()) {
            canvas.clipRect(getDirtyBounds());
        }
        boolean addRipple = this.mAddRipple;
        float cx = bounds.centerX();
        float cy = bounds.centerY();
        boolean shouldExit2 = this.mExitingAnimation;
        this.mExitingAnimation = false;
        this.mAddRipple = false;
        if (this.mRunningAnimations.size() > 0 && !addRipple) {
            updateRipplePaint();
        }
        drawContent(canvas);
        drawPatternedBackground(canvas, cx, cy);
        if (!addRipple || this.mRunningAnimations.size() > 10) {
            shouldExit = shouldExit2;
        } else {
            if (this.mHasPending) {
                float x2 = this.mPendingX;
                float y2 = this.mPendingY;
                this.mHasPending = false;
                x = x2;
                y = y2;
            } else {
                float x3 = bounds.exactCenterX();
                x = x3;
                y = bounds.exactCenterY();
            }
            float h = bounds.height();
            float w = bounds.width();
            shouldExit = shouldExit2;
            RippleAnimationSession.AnimationProperties<Float, Paint> properties = createAnimationProperties(x, y, cx, cy, w, h);
            this.mRunningAnimations.add(new RippleAnimationSession(properties, !useCanvasProps).setOnAnimationUpdated(new Runnable() { // from class: android.graphics.drawable.RippleDrawable$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    RippleDrawable.this.lambda$drawPatterned$1();
                }
            }).setOnSessionEnd(new Consumer() { // from class: android.graphics.drawable.RippleDrawable$$ExternalSyntheticLambda2
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    RippleDrawable.this.lambda$drawPatterned$2((RippleAnimationSession) obj);
                }
            }).setForceSoftwareAnimation(useCanvasProps ? false : true).enter(canvas));
        }
        if (shouldExit) {
            for (int i = 0; i < this.mRunningAnimations.size(); i++) {
                this.mRunningAnimations.get(i).exit(canvas);
            }
        }
        int i2 = 0;
        while (true) {
            if (i2 >= this.mRunningAnimations.size()) {
                break;
            }
            RippleAnimationSession s = this.mRunningAnimations.get(i2);
            if (!canvas.isHardwareAccelerated()) {
                Log.m110e(TAG, "The RippleDrawable.STYLE_PATTERNED animation is not supported for a non-hardware accelerated Canvas. Skipping animation.");
                break;
            }
            if (useCanvasProps) {
                RippleAnimationSession.AnimationProperties<CanvasProperty<Float>, CanvasProperty<Paint>> p = s.getCanvasProperties();
                RecordingCanvas can = (RecordingCanvas) canvas;
                can.drawRipple(p.getX(), p.getY(), p.getMaxRadius(), p.getPaint(), p.getProgress(), p.getNoisePhase(), p.getColor(), p.getShader());
            } else {
                RippleAnimationSession.AnimationProperties<Float, Paint> p2 = s.getProperties();
                float radius = p2.getMaxRadius().floatValue();
                canvas.drawCircle(p2.getX().floatValue(), p2.getY().floatValue(), radius, p2.getPaint());
            }
            i2++;
        }
        canvas.restoreToCount(saveCount);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$drawPatterned$1() {
        invalidateSelf(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$drawPatterned$2(RippleAnimationSession session) {
        this.mRunningAnimations.remove(session);
    }

    private void drawPatternedBackground(Canvas c, float cx, float cy) {
        if (this.mRunBackgroundAnimation) {
            startBackgroundAnimation();
        }
        if (this.mBackgroundOpacity == 0.0f) {
            return;
        }
        Paint p = updateRipplePaint();
        float newOpacity = this.mBackgroundOpacity;
        int origAlpha = p.getAlpha();
        int alpha = Math.min((int) ((origAlpha * newOpacity) + 0.5f), 255);
        if (alpha > 0) {
            ColorFilter origFilter = p.getColorFilter();
            p.setColorFilter(this.mFocusColorFilter);
            p.setAlpha(alpha);
            c.drawCircle(cx, cy, getComputedRadius(), p);
            p.setAlpha(origAlpha);
            p.setColorFilter(origFilter);
        }
    }

    private float computeRadius() {
        float halfWidth = this.mHotspotBounds.width() / 2.0f;
        float halfHeight = this.mHotspotBounds.height() / 2.0f;
        return (float) Math.sqrt((halfWidth * halfWidth) + (halfHeight * halfHeight));
    }

    private int getComputedRadius() {
        return this.mState.mMaxRadius >= 0 ? this.mState.mMaxRadius : (int) computeRadius();
    }

    private RippleAnimationSession.AnimationProperties<Float, Paint> createAnimationProperties(float x, float y, float cx, float cy, float w, float h) {
        int color;
        Paint p = new Paint(updateRipplePaint());
        float radius = getComputedRadius();
        RippleShader shader = new RippleShader();
        PorterDuffColorFilter porterDuffColorFilter = this.mMaskColorFilter;
        if (porterDuffColorFilter == null) {
            color = this.mState.mColor.getColorForState(getState(), -16777216);
        } else {
            color = porterDuffColorFilter.getColor();
        }
        int color2 = color;
        int effectColor = this.mState.mEffectColor.getColorForState(getState(), Color.MAGENTA);
        float noisePhase = (float) AnimationUtils.currentAnimationTimeMillis();
        shader.setColor(color2, effectColor);
        shader.setOrigin(cx, cy);
        shader.setTouch(x, y);
        shader.setResolution(w, h);
        shader.setNoisePhase(noisePhase);
        shader.setRadius(radius);
        shader.setProgress(0.0f);
        RippleAnimationSession.AnimationProperties<Float, Paint> properties = new RippleAnimationSession.AnimationProperties<>(Float.valueOf(cx), Float.valueOf(cy), Float.valueOf(radius), Float.valueOf(noisePhase), p, Float.valueOf(0.0f), color2, shader);
        BitmapShader bitmapShader = this.mMaskShader;
        if (bitmapShader == null) {
            shader.setShader(null);
        } else {
            shader.setShader(bitmapShader);
        }
        p.setShader(shader);
        p.setColorFilter(null);
        p.setColor(color2);
        return properties;
    }

    @Override // android.graphics.drawable.Drawable
    public void invalidateSelf() {
        invalidateSelf(true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void invalidateSelf(boolean invalidateMask) {
        super.invalidateSelf();
        if (invalidateMask) {
            this.mHasValidMask = false;
        }
    }

    private void pruneRipples() {
        int remaining = 0;
        RippleForeground[] ripples = this.mExitingRipples;
        int count = this.mExitingRipplesCount;
        for (int i = 0; i < count; i++) {
            if (!ripples[i].hasFinishedExit()) {
                ripples[remaining] = ripples[i];
                remaining++;
            }
        }
        for (int i2 = remaining; i2 < count; i2++) {
            ripples[i2] = null;
        }
        this.mExitingRipplesCount = remaining;
    }

    private void updateMaskShaderIfNeeded() {
        int maskType;
        if (this.mHasValidMask || (maskType = getMaskType()) == -1) {
            return;
        }
        this.mHasValidMask = true;
        Rect bounds = getBounds();
        if (maskType == 0 || bounds.isEmpty()) {
            Bitmap bitmap = this.mMaskBuffer;
            if (bitmap != null) {
                bitmap.recycle();
                this.mMaskBuffer = null;
                this.mMaskShader = null;
                this.mMaskCanvas = null;
            }
            this.mMaskMatrix = null;
            this.mMaskColorFilter = null;
            return;
        }
        Bitmap bitmap2 = this.mMaskBuffer;
        if (bitmap2 == null || bitmap2.getWidth() != bounds.width() || this.mMaskBuffer.getHeight() != bounds.height()) {
            Bitmap bitmap3 = this.mMaskBuffer;
            if (bitmap3 != null) {
                bitmap3.recycle();
            }
            Bitmap createBitmap = Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ALPHA_8);
            this.mMaskBuffer = createBitmap;
            this.mMaskShader = new BitmapShader(createBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            this.mMaskCanvas = new Canvas(this.mMaskBuffer);
        } else {
            this.mMaskBuffer.eraseColor(0);
        }
        Matrix matrix = this.mMaskMatrix;
        if (matrix == null) {
            this.mMaskMatrix = new Matrix();
        } else {
            matrix.reset();
        }
        if (this.mMaskColorFilter == null) {
            this.mMaskColorFilter = new PorterDuffColorFilter(0, PorterDuff.Mode.SRC_IN);
            this.mFocusColorFilter = new PorterDuffColorFilter(0, PorterDuff.Mode.SRC_IN);
        }
        int saveCount = this.mMaskCanvas.save();
        int left = bounds.left;
        int top = bounds.top;
        this.mMaskCanvas.translate(-left, -top);
        if (maskType == 2) {
            drawMask(this.mMaskCanvas);
        } else if (maskType == 1) {
            drawContent(this.mMaskCanvas);
        }
        this.mMaskCanvas.restoreToCount(saveCount);
    }

    private int getMaskType() {
        RippleBackground rippleBackground;
        if (this.mRipple != null || this.mExitingRipplesCount > 0 || (((rippleBackground = this.mBackground) != null && rippleBackground.isVisible()) || this.mState.mRippleStyle != 0)) {
            Drawable drawable = this.mMask;
            if (drawable != null) {
                return drawable.getOpacity() == -1 ? 0 : 2;
            }
            LayerDrawable.ChildDrawable[] array = this.mLayerState.mChildren;
            int count = this.mLayerState.mNumChildren;
            for (int i = 0; i < count; i++) {
                if (array[i].mDrawable.getOpacity() != -1) {
                    return 1;
                }
            }
            return 0;
        }
        return -1;
    }

    private void drawContent(Canvas canvas) {
        LayerDrawable.ChildDrawable[] array = this.mLayerState.mChildren;
        int count = this.mLayerState.mNumChildren;
        for (int i = 0; i < count; i++) {
            if (array[i].mId != 16908334) {
                array[i].mDrawable.draw(canvas);
            }
        }
    }

    private void drawBackgroundAndRipples(Canvas canvas) {
        RippleForeground active = this.mRipple;
        RippleBackground background = this.mBackground;
        int count = this.mExitingRipplesCount;
        if (active == null && count <= 0 && (background == null || !background.isVisible())) {
            return;
        }
        float x = this.mHotspotBounds.exactCenterX();
        float y = this.mHotspotBounds.exactCenterY();
        canvas.translate(x, y);
        Paint p = updateRipplePaint();
        if (background != null && background.isVisible()) {
            background.draw(canvas, p);
        }
        if (count > 0) {
            RippleForeground[] ripples = this.mExitingRipples;
            for (int i = 0; i < count; i++) {
                ripples[i].draw(canvas, p);
            }
        }
        if (active != null) {
            active.draw(canvas, p);
        }
        canvas.translate(-x, -y);
    }

    private void drawMask(Canvas canvas) {
        this.mMask.draw(canvas);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Paint updateRipplePaint() {
        if (this.mRipplePaint == null) {
            Paint paint = new Paint();
            this.mRipplePaint = paint;
            paint.setAntiAlias(true);
            this.mRipplePaint.setStyle(Paint.Style.FILL);
        }
        float x = this.mHotspotBounds.exactCenterX();
        float y = this.mHotspotBounds.exactCenterY();
        updateMaskShaderIfNeeded();
        if (this.mMaskShader != null) {
            Rect bounds = getBounds();
            if (this.mState.mRippleStyle == 1) {
                this.mMaskMatrix.setTranslate(bounds.left, bounds.top);
            } else {
                this.mMaskMatrix.setTranslate(bounds.left - x, bounds.top - y);
            }
            this.mMaskShader.setLocalMatrix(this.mMaskMatrix);
            if (this.mState.mRippleStyle == 1) {
                for (int i = 0; i < this.mRunningAnimations.size(); i++) {
                    this.mRunningAnimations.get(i).getProperties().getShader().setShader(this.mMaskShader);
                }
            }
        }
        int color = this.mState.mColor.getColorForState(getState(), -16777216);
        Paint p = this.mRipplePaint;
        if (this.mMaskColorFilter != null) {
            int maskColor = this.mState.mRippleStyle == 1 ? color : color | (-16777216);
            if (this.mMaskColorFilter.getColor() != maskColor) {
                this.mMaskColorFilter = new PorterDuffColorFilter(maskColor, this.mMaskColorFilter.getMode());
                this.mFocusColorFilter = new PorterDuffColorFilter(color | (-16777216), this.mFocusColorFilter.getMode());
            }
            p.setColor((-16777216) & color);
            p.setColorFilter(this.mMaskColorFilter);
            p.setShader(this.mMaskShader);
        } else {
            p.setColor(color);
            p.setColorFilter(null);
            p.setShader(null);
        }
        return p;
    }

    @Override // android.graphics.drawable.Drawable
    public Rect getDirtyBounds() {
        if (!isBounded()) {
            Rect drawingBounds = this.mDrawingBounds;
            Rect dirtyBounds = this.mDirtyBounds;
            dirtyBounds.set(drawingBounds);
            drawingBounds.setEmpty();
            int cX = (int) this.mHotspotBounds.exactCenterX();
            int cY = (int) this.mHotspotBounds.exactCenterY();
            Rect rippleBounds = this.mTempRect;
            RippleForeground[] activeRipples = this.mExitingRipples;
            int N = this.mExitingRipplesCount;
            for (int i = 0; i < N; i++) {
                activeRipples[i].getBounds(rippleBounds);
                rippleBounds.offset(cX, cY);
                drawingBounds.union(rippleBounds);
            }
            RippleBackground background = this.mBackground;
            if (background != null) {
                background.getBounds(rippleBounds);
                rippleBounds.offset(cX, cY);
                drawingBounds.union(rippleBounds);
            }
            dirtyBounds.union(drawingBounds);
            dirtyBounds.union(super.getDirtyBounds());
            return dirtyBounds;
        }
        return getBounds();
    }

    public void setForceSoftware(boolean forceSoftware) {
        this.mForceSoftware = forceSoftware;
    }

    @Override // android.graphics.drawable.LayerDrawable, android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        return this.mState;
    }

    @Override // android.graphics.drawable.LayerDrawable, android.graphics.drawable.Drawable
    public Drawable mutate() {
        super.mutate();
        this.mState = (RippleState) this.mLayerState;
        this.mMask = findDrawableByLayerId(16908334);
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.graphics.drawable.LayerDrawable
    public RippleState createConstantState(LayerDrawable.LayerState state, Resources res) {
        return new RippleState(state, this, res);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class RippleState extends LayerDrawable.LayerState {
        ColorStateList mColor;
        ColorStateList mEffectColor;
        int mMaxRadius;
        int mRippleStyle;
        int[] mTouchThemeAttrs;

        public RippleState(LayerDrawable.LayerState orig, RippleDrawable owner, Resources res) {
            super(orig, owner, res);
            this.mColor = ColorStateList.valueOf(Color.MAGENTA);
            this.mEffectColor = ColorStateList.valueOf(RippleDrawable.DEFAULT_EFFECT_COLOR);
            this.mMaxRadius = -1;
            this.mRippleStyle = 1;
            if (orig != null && (orig instanceof RippleState)) {
                RippleState origs = (RippleState) orig;
                this.mTouchThemeAttrs = origs.mTouchThemeAttrs;
                this.mColor = origs.mColor;
                this.mMaxRadius = origs.mMaxRadius;
                this.mRippleStyle = origs.mRippleStyle;
                this.mEffectColor = origs.mEffectColor;
                if (origs.mDensity != this.mDensity) {
                    applyDensityScaling(orig.mDensity, this.mDensity);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.graphics.drawable.LayerDrawable.LayerState
        public void onDensityChanged(int sourceDensity, int targetDensity) {
            super.onDensityChanged(sourceDensity, targetDensity);
            applyDensityScaling(sourceDensity, targetDensity);
        }

        private void applyDensityScaling(int sourceDensity, int targetDensity) {
            int i = this.mMaxRadius;
            if (i != -1) {
                this.mMaxRadius = Drawable.scaleFromDensity(i, sourceDensity, targetDensity, true);
            }
        }

        @Override // android.graphics.drawable.LayerDrawable.LayerState, android.graphics.drawable.Drawable.ConstantState
        public boolean canApplyTheme() {
            ColorStateList colorStateList;
            return this.mTouchThemeAttrs != null || ((colorStateList = this.mColor) != null && colorStateList.canApplyTheme()) || super.canApplyTheme();
        }

        @Override // android.graphics.drawable.LayerDrawable.LayerState, android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new RippleDrawable(this, (Resources) null);
        }

        @Override // android.graphics.drawable.LayerDrawable.LayerState, android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources res) {
            return new RippleDrawable(this, res);
        }

        @Override // android.graphics.drawable.LayerDrawable.LayerState, android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            int changingConfigurations = super.getChangingConfigurations();
            ColorStateList colorStateList = this.mColor;
            return changingConfigurations | (colorStateList != null ? colorStateList.getChangingConfigurations() : 0);
        }
    }

    private RippleDrawable(RippleState state, Resources res) {
        this.mTempRect = new Rect();
        this.mHotspotBounds = new Rect();
        this.mDrawingBounds = new Rect();
        this.mDirtyBounds = new Rect();
        this.mExitingRipplesCount = 0;
        this.mAddRipple = false;
        this.mRunningAnimations = new ArrayList<>();
        RippleState rippleState = new RippleState(state, this, res);
        this.mState = rippleState;
        this.mLayerState = rippleState;
        this.mDensity = Drawable.resolveDensity(res, this.mState.mDensity);
        if (this.mState.mNumChildren > 0) {
            ensurePadding();
            refreshPadding();
        }
        updateLocalState();
    }

    private void updateLocalState() {
        this.mMask = findDrawableByLayerId(16908334);
    }
}
