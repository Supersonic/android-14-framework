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
public class ClipDrawable extends DrawableWrapper {
    public static final int HORIZONTAL = 1;
    private static final int MAX_LEVEL = 10000;
    public static final int VERTICAL = 2;
    private ClipState mState;
    private final Rect mTmpRect;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ClipDrawable() {
        this(new ClipState(null, null), null);
    }

    public ClipDrawable(Drawable drawable, int gravity, int orientation) {
        this(new ClipState(null, null), null);
        this.mState.mGravity = gravity;
        this.mState.mOrientation = orientation;
        setDrawable(drawable);
    }

    @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs, Resources.Theme theme) throws XmlPullParserException, IOException {
        TypedArray a = obtainAttributes(r, theme, attrs, C4057R.styleable.ClipDrawable);
        super.inflate(r, parser, attrs, theme);
        updateStateFromTypedArray(a);
        verifyRequiredAttributes(a);
        a.recycle();
    }

    @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public void applyTheme(Resources.Theme t) {
        super.applyTheme(t);
        ClipState state = this.mState;
        if (state != null && state.mThemeAttrs != null) {
            TypedArray a = t.resolveAttributes(state.mThemeAttrs, C4057R.styleable.ClipDrawable);
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
                throw new XmlPullParserException(a.getPositionDescription() + ": <clip> tag requires a 'drawable' attribute or child tag defining a drawable");
            }
        }
    }

    private void updateStateFromTypedArray(TypedArray a) {
        ClipState state = this.mState;
        if (state == null) {
            return;
        }
        state.mChangingConfigurations |= a.getChangingConfigurations();
        state.mThemeAttrs = a.extractThemeAttrs();
        state.mOrientation = a.getInt(2, state.mOrientation);
        state.mGravity = a.getInt(0, state.mGravity);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public boolean onLevelChange(int level) {
        super.onLevelChange(level);
        invalidateSelf();
        return true;
    }

    @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public int getOpacity() {
        Drawable dr = getDrawable();
        int opacity = dr.getOpacity();
        if (opacity == -2 || dr.getLevel() == 0) {
            return -2;
        }
        int level = getLevel();
        if (level >= 10000) {
            return dr.getOpacity();
        }
        return -3;
    }

    @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        int w;
        int h;
        Drawable dr = getDrawable();
        if (dr.getLevel() == 0) {
            return;
        }
        Rect r = this.mTmpRect;
        Rect bounds = getBounds();
        int level = getLevel();
        int w2 = bounds.width();
        if ((this.mState.mOrientation & 1) == 0) {
            w = w2;
        } else {
            w = w2 - (((w2 + 0) * (10000 - level)) / 10000);
        }
        int h2 = bounds.height();
        if ((this.mState.mOrientation & 2) == 0) {
            h = h2;
        } else {
            h = h2 - (((h2 + 0) * (10000 - level)) / 10000);
        }
        int layoutDirection = getLayoutDirection();
        Gravity.apply(this.mState.mGravity, w, h, bounds, r, layoutDirection);
        if (w > 0 && h > 0) {
            canvas.save();
            canvas.clipRect(r);
            dr.draw(canvas);
            canvas.restore();
        }
    }

    @Override // android.graphics.drawable.DrawableWrapper
    DrawableWrapper.DrawableWrapperState mutateConstantState() {
        ClipState clipState = new ClipState(this.mState, null);
        this.mState = clipState;
        return clipState;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class ClipState extends DrawableWrapper.DrawableWrapperState {
        int mGravity;
        int mOrientation;
        private int[] mThemeAttrs;

        ClipState(ClipState orig, Resources res) {
            super(orig, res);
            this.mOrientation = 1;
            this.mGravity = 3;
            if (orig != null) {
                this.mOrientation = orig.mOrientation;
                this.mGravity = orig.mGravity;
            }
        }

        @Override // android.graphics.drawable.DrawableWrapper.DrawableWrapperState, android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources res) {
            return new ClipDrawable(this, res);
        }
    }

    private ClipDrawable(ClipState state, Resources res) {
        super(state, res);
        this.mTmpRect = new Rect();
        this.mState = state;
    }
}
