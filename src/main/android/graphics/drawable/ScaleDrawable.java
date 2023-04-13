package android.graphics.drawable;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.DrawableWrapper;
import android.util.AttributeSet;
import android.view.Gravity;
import com.android.internal.C4057R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class ScaleDrawable extends DrawableWrapper {
    private static final int MAX_LEVEL = 10000;
    private ScaleState mState;
    private final Rect mTmpRect;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ScaleDrawable() {
        this(new ScaleState(null, null), null);
    }

    public ScaleDrawable(Drawable drawable, int gravity, float scaleWidth, float scaleHeight) {
        this(new ScaleState(null, null), null);
        this.mState.mGravity = gravity;
        this.mState.mScaleWidth = scaleWidth;
        this.mState.mScaleHeight = scaleHeight;
        setDrawable(drawable);
    }

    @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs, Resources.Theme theme) throws XmlPullParserException, IOException {
        TypedArray a = obtainAttributes(r, theme, attrs, C4057R.styleable.ScaleDrawable);
        super.inflate(r, parser, attrs, theme);
        updateStateFromTypedArray(a);
        verifyRequiredAttributes(a);
        a.recycle();
        updateLocalState();
    }

    @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public void applyTheme(Resources.Theme t) {
        super.applyTheme(t);
        ScaleState state = this.mState;
        if (state == null) {
            return;
        }
        if (state.mThemeAttrs != null) {
            TypedArray a = t.resolveAttributes(state.mThemeAttrs, C4057R.styleable.ScaleDrawable);
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
            if (this.mState.mThemeAttrs == null || this.mState.mThemeAttrs[0] == 0) {
                throw new XmlPullParserException(a.getPositionDescription() + ": <scale> tag requires a 'drawable' attribute or child tag defining a drawable");
            }
        }
    }

    private void updateStateFromTypedArray(TypedArray a) {
        ScaleState state = this.mState;
        if (state == null) {
            return;
        }
        state.mChangingConfigurations |= a.getChangingConfigurations();
        state.mThemeAttrs = a.extractThemeAttrs();
        state.mScaleWidth = getPercent(a, 1, state.mScaleWidth);
        state.mScaleHeight = getPercent(a, 2, state.mScaleHeight);
        state.mGravity = a.getInt(3, state.mGravity);
        state.mUseIntrinsicSizeAsMin = a.getBoolean(4, state.mUseIntrinsicSizeAsMin);
        state.mInitialLevel = a.getInt(5, state.mInitialLevel);
    }

    private static float getPercent(TypedArray a, int index, float defaultValue) {
        int type = a.getType(index);
        if (type == 6 || type == 0) {
            return a.getFraction(index, 1, 1, defaultValue);
        }
        String s = a.getString(index);
        if (s != null && s.endsWith("%")) {
            String f = s.substring(0, s.length() - 1);
            return Float.parseFloat(f) / 100.0f;
        }
        return defaultValue;
    }

    @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        Drawable d = getDrawable();
        if (d != null && d.getLevel() != 0) {
            d.draw(canvas);
        }
    }

    @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public int getOpacity() {
        Drawable d = getDrawable();
        if (d.getLevel() == 0) {
            return -2;
        }
        int opacity = d.getOpacity();
        if (opacity == -1 && d.getLevel() < 10000) {
            return -3;
        }
        return opacity;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public boolean onLevelChange(int level) {
        super.onLevelChange(level);
        onBoundsChange(getBounds());
        invalidateSelf();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public void onBoundsChange(Rect bounds) {
        int w;
        int h;
        Drawable d = getDrawable();
        Rect r = this.mTmpRect;
        boolean min = this.mState.mUseIntrinsicSizeAsMin;
        int level = getLevel();
        int w2 = bounds.width();
        if (this.mState.mScaleWidth <= 0.0f) {
            w = w2;
        } else {
            int iw = min ? d.getIntrinsicWidth() : 0;
            w = w2 - ((int) ((((w2 - iw) * (10000 - level)) * this.mState.mScaleWidth) / 10000.0f));
        }
        int h2 = bounds.height();
        if (this.mState.mScaleHeight <= 0.0f) {
            h = h2;
        } else {
            int ih = min ? d.getIntrinsicHeight() : 0;
            h = h2 - ((int) ((((h2 - ih) * (10000 - level)) * this.mState.mScaleHeight) / 10000.0f));
        }
        int layoutDirection = getLayoutDirection();
        Gravity.apply(this.mState.mGravity, w, h, bounds, r, layoutDirection);
        if (w > 0 && h > 0) {
            d.setBounds(r.left, r.top, r.right, r.bottom);
        }
    }

    @Override // android.graphics.drawable.DrawableWrapper
    DrawableWrapper.DrawableWrapperState mutateConstantState() {
        ScaleState scaleState = new ScaleState(this.mState, null);
        this.mState = scaleState;
        return scaleState;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class ScaleState extends DrawableWrapper.DrawableWrapperState {
        private static final float DO_NOT_SCALE = -1.0f;
        int mGravity;
        int mInitialLevel;
        float mScaleHeight;
        float mScaleWidth;
        private int[] mThemeAttrs;
        boolean mUseIntrinsicSizeAsMin;

        ScaleState(ScaleState orig, Resources res) {
            super(orig, res);
            this.mScaleWidth = -1.0f;
            this.mScaleHeight = -1.0f;
            this.mGravity = 3;
            this.mUseIntrinsicSizeAsMin = false;
            this.mInitialLevel = 0;
            if (orig != null) {
                this.mScaleWidth = orig.mScaleWidth;
                this.mScaleHeight = orig.mScaleHeight;
                this.mGravity = orig.mGravity;
                this.mUseIntrinsicSizeAsMin = orig.mUseIntrinsicSizeAsMin;
                this.mInitialLevel = orig.mInitialLevel;
            }
        }

        @Override // android.graphics.drawable.DrawableWrapper.DrawableWrapperState, android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources res) {
            return new ScaleDrawable(this, res);
        }
    }

    private ScaleDrawable(ScaleState state, Resources res) {
        super(state, res);
        this.mTmpRect = new Rect();
        this.mState = state;
        updateLocalState();
    }

    private void updateLocalState() {
        setLevel(this.mState.mInitialLevel);
    }
}
