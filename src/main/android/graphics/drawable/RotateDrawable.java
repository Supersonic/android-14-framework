package android.graphics.drawable;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.DrawableWrapper;
import android.util.AttributeSet;
import android.util.MathUtils;
import android.util.TypedValue;
import com.android.internal.C4057R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class RotateDrawable extends DrawableWrapper {
    private static final int MAX_LEVEL = 10000;
    private RotateState mState;

    public RotateDrawable() {
        this(new RotateState(null, null), null);
    }

    @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs, Resources.Theme theme) throws XmlPullParserException, IOException {
        TypedArray a = obtainAttributes(r, theme, attrs, C4057R.styleable.RotateDrawable);
        super.inflate(r, parser, attrs, theme);
        updateStateFromTypedArray(a);
        verifyRequiredAttributes(a);
        a.recycle();
    }

    @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public void applyTheme(Resources.Theme t) {
        super.applyTheme(t);
        RotateState state = this.mState;
        if (state != null && state.mThemeAttrs != null) {
            TypedArray a = t.resolveAttributes(state.mThemeAttrs, C4057R.styleable.RotateDrawable);
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
    }

    private void verifyRequiredAttributes(TypedArray a) throws XmlPullParserException {
        if (getDrawable() == null) {
            if (this.mState.mThemeAttrs == null || this.mState.mThemeAttrs[1] == 0) {
                throw new XmlPullParserException(a.getPositionDescription() + ": <rotate> tag requires a 'drawable' attribute or child tag defining a drawable");
            }
        }
    }

    private void updateStateFromTypedArray(TypedArray a) {
        RotateState state = this.mState;
        if (state == null) {
            return;
        }
        state.mChangingConfigurations |= a.getChangingConfigurations();
        state.mThemeAttrs = a.extractThemeAttrs();
        if (a.hasValue(4)) {
            TypedValue tv = a.peekValue(4);
            state.mPivotXRel = tv.type == 6;
            state.mPivotX = state.mPivotXRel ? tv.getFraction(1.0f, 1.0f) : tv.getFloat();
        }
        if (a.hasValue(5)) {
            TypedValue tv2 = a.peekValue(5);
            state.mPivotYRel = tv2.type == 6;
            state.mPivotY = state.mPivotYRel ? tv2.getFraction(1.0f, 1.0f) : tv2.getFloat();
        }
        state.mFromDegrees = a.getFloat(2, state.mFromDegrees);
        state.mToDegrees = a.getFloat(3, state.mToDegrees);
        state.mCurrentDegrees = state.mFromDegrees;
    }

    @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        Drawable d = getDrawable();
        Rect bounds = d.getBounds();
        int w = bounds.right - bounds.left;
        int h = bounds.bottom - bounds.top;
        RotateState st = this.mState;
        float px = st.mPivotXRel ? w * st.mPivotX : st.mPivotX;
        float py = st.mPivotYRel ? h * st.mPivotY : st.mPivotY;
        int saveCount = canvas.save();
        canvas.rotate(st.mCurrentDegrees, bounds.left + px, bounds.top + py);
        d.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    public void setFromDegrees(float fromDegrees) {
        if (this.mState.mFromDegrees != fromDegrees) {
            this.mState.mFromDegrees = fromDegrees;
            invalidateSelf();
        }
    }

    public float getFromDegrees() {
        return this.mState.mFromDegrees;
    }

    public void setToDegrees(float toDegrees) {
        if (this.mState.mToDegrees != toDegrees) {
            this.mState.mToDegrees = toDegrees;
            invalidateSelf();
        }
    }

    public float getToDegrees() {
        return this.mState.mToDegrees;
    }

    public void setPivotX(float pivotX) {
        if (this.mState.mPivotX != pivotX) {
            this.mState.mPivotX = pivotX;
            invalidateSelf();
        }
    }

    public float getPivotX() {
        return this.mState.mPivotX;
    }

    public void setPivotXRelative(boolean relative) {
        if (this.mState.mPivotXRel != relative) {
            this.mState.mPivotXRel = relative;
            invalidateSelf();
        }
    }

    public boolean isPivotXRelative() {
        return this.mState.mPivotXRel;
    }

    public void setPivotY(float pivotY) {
        if (this.mState.mPivotY != pivotY) {
            this.mState.mPivotY = pivotY;
            invalidateSelf();
        }
    }

    public float getPivotY() {
        return this.mState.mPivotY;
    }

    public void setPivotYRelative(boolean relative) {
        if (this.mState.mPivotYRel != relative) {
            this.mState.mPivotYRel = relative;
            invalidateSelf();
        }
    }

    public boolean isPivotYRelative() {
        return this.mState.mPivotYRel;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public boolean onLevelChange(int level) {
        super.onLevelChange(level);
        float value = level / 10000.0f;
        float degrees = MathUtils.lerp(this.mState.mFromDegrees, this.mState.mToDegrees, value);
        this.mState.mCurrentDegrees = degrees;
        invalidateSelf();
        return true;
    }

    @Override // android.graphics.drawable.DrawableWrapper
    DrawableWrapper.DrawableWrapperState mutateConstantState() {
        RotateState rotateState = new RotateState(this.mState, null);
        this.mState = rotateState;
        return rotateState;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class RotateState extends DrawableWrapper.DrawableWrapperState {
        float mCurrentDegrees;
        float mFromDegrees;
        float mPivotX;
        boolean mPivotXRel;
        float mPivotY;
        boolean mPivotYRel;
        private int[] mThemeAttrs;
        float mToDegrees;

        RotateState(RotateState orig, Resources res) {
            super(orig, res);
            this.mPivotXRel = true;
            this.mPivotX = 0.5f;
            this.mPivotYRel = true;
            this.mPivotY = 0.5f;
            this.mFromDegrees = 0.0f;
            this.mToDegrees = 360.0f;
            this.mCurrentDegrees = 0.0f;
            if (orig != null) {
                this.mPivotXRel = orig.mPivotXRel;
                this.mPivotX = orig.mPivotX;
                this.mPivotYRel = orig.mPivotYRel;
                this.mPivotY = orig.mPivotY;
                this.mFromDegrees = orig.mFromDegrees;
                this.mToDegrees = orig.mToDegrees;
                this.mCurrentDegrees = orig.mCurrentDegrees;
            }
        }

        @Override // android.graphics.drawable.DrawableWrapper.DrawableWrapperState, android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources res) {
            return new RotateDrawable(this, res);
        }
    }

    private RotateDrawable(RotateState state, Resources res) {
        super(state, res);
        this.mState = state;
    }
}
