package android.graphics.drawable;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.DrawableWrapper;
import android.p008os.SystemClock;
import android.util.AttributeSet;
import android.util.TypedValue;
import com.android.internal.C4057R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class AnimatedRotateDrawable extends DrawableWrapper implements Animatable {
    private float mCurrentDegrees;
    private float mIncrement;
    private final Runnable mNextFrame;
    private boolean mRunning;
    private AnimatedRotateState mState;

    public AnimatedRotateDrawable() {
        this(new AnimatedRotateState(null, null), null);
    }

    @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        Drawable drawable = getDrawable();
        Rect bounds = drawable.getBounds();
        int w = bounds.right - bounds.left;
        int h = bounds.bottom - bounds.top;
        AnimatedRotateState st = this.mState;
        float px = st.mPivotXRel ? w * st.mPivotX : st.mPivotX;
        float py = st.mPivotYRel ? h * st.mPivotY : st.mPivotY;
        int saveCount = canvas.save();
        canvas.rotate(this.mCurrentDegrees, bounds.left + px, bounds.top + py);
        drawable.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    @Override // android.graphics.drawable.Animatable
    public void start() {
        if (!this.mRunning) {
            this.mRunning = true;
            nextFrame();
        }
    }

    @Override // android.graphics.drawable.Animatable
    public void stop() {
        this.mRunning = false;
        unscheduleSelf(this.mNextFrame);
    }

    @Override // android.graphics.drawable.Animatable
    public boolean isRunning() {
        return this.mRunning;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void nextFrame() {
        unscheduleSelf(this.mNextFrame);
        scheduleSelf(this.mNextFrame, SystemClock.uptimeMillis() + this.mState.mFrameDuration);
    }

    @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public boolean setVisible(boolean visible, boolean restart) {
        boolean changed = super.setVisible(visible, restart);
        if (visible) {
            if (changed || restart) {
                this.mCurrentDegrees = 0.0f;
                nextFrame();
            }
        } else {
            unscheduleSelf(this.mNextFrame);
        }
        return changed;
    }

    @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs, Resources.Theme theme) throws XmlPullParserException, IOException {
        TypedArray a = obtainAttributes(r, theme, attrs, C4057R.styleable.AnimatedRotateDrawable);
        super.inflate(r, parser, attrs, theme);
        updateStateFromTypedArray(a);
        verifyRequiredAttributes(a);
        a.recycle();
        updateLocalState();
    }

    @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public void applyTheme(Resources.Theme t) {
        super.applyTheme(t);
        AnimatedRotateState state = this.mState;
        if (state == null) {
            return;
        }
        if (state.mThemeAttrs != null) {
            TypedArray a = t.resolveAttributes(state.mThemeAttrs, C4057R.styleable.AnimatedRotateDrawable);
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
        updateLocalState();
    }

    private void verifyRequiredAttributes(TypedArray a) throws XmlPullParserException {
        if (getDrawable() == null) {
            if (this.mState.mThemeAttrs == null || this.mState.mThemeAttrs[1] == 0) {
                throw new XmlPullParserException(a.getPositionDescription() + ": <animated-rotate> tag requires a 'drawable' attribute or child tag defining a drawable");
            }
        }
    }

    private void updateStateFromTypedArray(TypedArray a) {
        AnimatedRotateState state = this.mState;
        if (state == null) {
            return;
        }
        state.mChangingConfigurations |= a.getChangingConfigurations();
        state.mThemeAttrs = a.extractThemeAttrs();
        if (a.hasValue(2)) {
            TypedValue tv = a.peekValue(2);
            state.mPivotXRel = tv.type == 6;
            state.mPivotX = state.mPivotXRel ? tv.getFraction(1.0f, 1.0f) : tv.getFloat();
        }
        if (a.hasValue(3)) {
            TypedValue tv2 = a.peekValue(3);
            state.mPivotYRel = tv2.type == 6;
            state.mPivotY = state.mPivotYRel ? tv2.getFraction(1.0f, 1.0f) : tv2.getFloat();
        }
        setFramesCount(a.getInt(5, state.mFramesCount));
        setFramesDuration(a.getInt(4, state.mFrameDuration));
    }

    public void setFramesCount(int framesCount) {
        this.mState.mFramesCount = framesCount;
        this.mIncrement = 360.0f / this.mState.mFramesCount;
    }

    public void setFramesDuration(int framesDuration) {
        this.mState.mFrameDuration = framesDuration;
    }

    @Override // android.graphics.drawable.DrawableWrapper
    DrawableWrapper.DrawableWrapperState mutateConstantState() {
        AnimatedRotateState animatedRotateState = new AnimatedRotateState(this.mState, null);
        this.mState = animatedRotateState;
        return animatedRotateState;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class AnimatedRotateState extends DrawableWrapper.DrawableWrapperState {
        int mFrameDuration;
        int mFramesCount;
        float mPivotX;
        boolean mPivotXRel;
        float mPivotY;
        boolean mPivotYRel;
        private int[] mThemeAttrs;

        public AnimatedRotateState(AnimatedRotateState orig, Resources res) {
            super(orig, res);
            this.mPivotXRel = false;
            this.mPivotX = 0.0f;
            this.mPivotYRel = false;
            this.mPivotY = 0.0f;
            this.mFrameDuration = 150;
            this.mFramesCount = 12;
            if (orig != null) {
                this.mPivotXRel = orig.mPivotXRel;
                this.mPivotX = orig.mPivotX;
                this.mPivotYRel = orig.mPivotYRel;
                this.mPivotY = orig.mPivotY;
                this.mFramesCount = orig.mFramesCount;
                this.mFrameDuration = orig.mFrameDuration;
            }
        }

        @Override // android.graphics.drawable.DrawableWrapper.DrawableWrapperState, android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources res) {
            return new AnimatedRotateDrawable(this, res);
        }
    }

    private AnimatedRotateDrawable(AnimatedRotateState state, Resources res) {
        super(state, res);
        this.mNextFrame = new Runnable() { // from class: android.graphics.drawable.AnimatedRotateDrawable.1
            @Override // java.lang.Runnable
            public void run() {
                AnimatedRotateDrawable.this.mCurrentDegrees += AnimatedRotateDrawable.this.mIncrement;
                if (AnimatedRotateDrawable.this.mCurrentDegrees > 360.0f - AnimatedRotateDrawable.this.mIncrement) {
                    AnimatedRotateDrawable.this.mCurrentDegrees = 0.0f;
                }
                AnimatedRotateDrawable.this.invalidateSelf();
                AnimatedRotateDrawable.this.nextFrame();
            }
        };
        this.mState = state;
        updateLocalState();
    }

    private void updateLocalState() {
        AnimatedRotateState state = this.mState;
        this.mIncrement = 360.0f / state.mFramesCount;
        Drawable drawable = getDrawable();
        if (drawable != null) {
            drawable.setFilterBitmap(true);
            if (drawable instanceof BitmapDrawable) {
                ((BitmapDrawable) drawable).setAntiAlias(true);
            }
        }
    }
}
