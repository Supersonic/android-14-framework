package android.graphics.drawable;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.Insets;
import android.graphics.LinearGradient;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import com.android.internal.C4057R;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class GradientDrawable extends Drawable {
    private static final float DEFAULT_INNER_RADIUS_RATIO = 3.0f;
    private static final float DEFAULT_THICKNESS_RATIO = 9.0f;
    public static final int LINE = 2;
    public static final int LINEAR_GRADIENT = 0;
    public static final int OVAL = 1;
    public static final int RADIAL_GRADIENT = 1;
    private static final int RADIUS_TYPE_FRACTION = 1;
    private static final int RADIUS_TYPE_FRACTION_PARENT = 2;
    private static final int RADIUS_TYPE_PIXELS = 0;
    public static final int RECTANGLE = 0;
    public static final int RING = 3;
    public static final int SWEEP_GRADIENT = 2;
    private int mAlpha;
    private BlendModeColorFilter mBlendModeColorFilter;
    private ColorFilter mColorFilter;
    private final Paint mFillPaint;
    private boolean mGradientIsDirty;
    private float mGradientRadius;
    private GradientState mGradientState;
    private Paint mLayerPaint;
    private boolean mMutated;
    private Rect mPadding;
    private final Path mPath;
    private boolean mPathIsDirty;
    private final RectF mRect;
    private Path mRingPath;
    private Paint mStrokePaint;
    public static boolean sWrapNegativeAngleMeasurements = true;
    private static final Orientation DEFAULT_ORIENTATION = Orientation.TOP_BOTTOM;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface GradientType {
    }

    /* loaded from: classes.dex */
    public enum Orientation {
        TOP_BOTTOM,
        TR_BL,
        RIGHT_LEFT,
        BR_TL,
        BOTTOM_TOP,
        BL_TR,
        LEFT_RIGHT,
        TL_BR
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface RadiusType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Shape {
    }

    public GradientDrawable() {
        this(new GradientState(DEFAULT_ORIENTATION, (int[]) null), (Resources) null);
    }

    public GradientDrawable(Orientation orientation, int[] colors) {
        this(new GradientState(orientation, colors), (Resources) null);
    }

    @Override // android.graphics.drawable.Drawable
    public boolean getPadding(Rect padding) {
        Rect rect = this.mPadding;
        if (rect != null) {
            padding.set(rect);
            return true;
        }
        return super.getPadding(padding);
    }

    public void setCornerRadii(float[] radii) {
        this.mGradientState.setCornerRadii(radii);
        this.mPathIsDirty = true;
        invalidateSelf();
    }

    public float[] getCornerRadii() {
        float[] radii = this.mGradientState.mRadiusArray;
        if (radii == null) {
            return null;
        }
        return (float[]) radii.clone();
    }

    public void setCornerRadius(float radius) {
        this.mGradientState.setCornerRadius(radius);
        this.mPathIsDirty = true;
        invalidateSelf();
    }

    public float getCornerRadius() {
        return this.mGradientState.mRadius;
    }

    public void setStroke(int width, int color) {
        setStroke(width, color, 0.0f, 0.0f);
    }

    public void setStroke(int width, ColorStateList colorStateList) {
        setStroke(width, colorStateList, 0.0f, 0.0f);
    }

    public void setStroke(int width, int color, float dashWidth, float dashGap) {
        this.mGradientState.setStroke(width, ColorStateList.valueOf(color), dashWidth, dashGap);
        setStrokeInternal(width, color, dashWidth, dashGap);
    }

    public void setStroke(int width, ColorStateList colorStateList, float dashWidth, float dashGap) {
        int color;
        this.mGradientState.setStroke(width, colorStateList, dashWidth, dashGap);
        if (colorStateList == null) {
            color = 0;
        } else {
            int[] stateSet = getState();
            color = colorStateList.getColorForState(stateSet, 0);
        }
        setStrokeInternal(width, color, dashWidth, dashGap);
    }

    private void setStrokeInternal(int width, int color, float dashWidth, float dashGap) {
        if (this.mStrokePaint == null) {
            Paint paint = new Paint(1);
            this.mStrokePaint = paint;
            paint.setStyle(Paint.Style.STROKE);
        }
        this.mStrokePaint.setStrokeWidth(width);
        this.mStrokePaint.setColor(color);
        DashPathEffect e = null;
        if (dashWidth > 0.0f) {
            e = new DashPathEffect(new float[]{dashWidth, dashGap}, 0.0f);
        }
        this.mStrokePaint.setPathEffect(e);
        this.mGradientIsDirty = true;
        invalidateSelf();
    }

    public void setSize(int width, int height) {
        this.mGradientState.setSize(width, height);
        this.mPathIsDirty = true;
        invalidateSelf();
    }

    public void setShape(int shape) {
        this.mRingPath = null;
        this.mPathIsDirty = true;
        this.mGradientState.setShape(shape);
        invalidateSelf();
    }

    public int getShape() {
        return this.mGradientState.mShape;
    }

    public void setGradientType(int gradient) {
        this.mGradientState.setGradientType(gradient);
        this.mGradientIsDirty = true;
        invalidateSelf();
    }

    public int getGradientType() {
        return this.mGradientState.mGradient;
    }

    public void setGradientCenter(float x, float y) {
        this.mGradientState.setGradientCenter(x, y);
        this.mGradientIsDirty = true;
        invalidateSelf();
    }

    public float getGradientCenterX() {
        return this.mGradientState.mCenterX;
    }

    public float getGradientCenterY() {
        return this.mGradientState.mCenterY;
    }

    public void setGradientRadius(float gradientRadius) {
        this.mGradientState.setGradientRadius(gradientRadius, 0);
        this.mGradientIsDirty = true;
        invalidateSelf();
    }

    public float getGradientRadius() {
        if (this.mGradientState.mGradient != 1) {
            return 0.0f;
        }
        ensureValidRect();
        return this.mGradientRadius;
    }

    public void setUseLevel(boolean useLevel) {
        this.mGradientState.mUseLevel = useLevel;
        this.mGradientIsDirty = true;
        invalidateSelf();
    }

    public boolean getUseLevel() {
        return this.mGradientState.mUseLevel;
    }

    private int modulateAlpha(int alpha) {
        int i = this.mAlpha;
        int scale = i + (i >> 7);
        return (alpha * scale) >> 8;
    }

    public Orientation getOrientation() {
        return this.mGradientState.mOrientation;
    }

    public void setOrientation(Orientation orientation) {
        this.mGradientState.mOrientation = orientation;
        this.mGradientIsDirty = true;
        invalidateSelf();
    }

    public void setColors(int[] colors) {
        setColors(colors, null);
    }

    public void setColors(int[] colors, float[] offsets) {
        this.mGradientState.setGradientColors(colors);
        this.mGradientState.mPositions = offsets;
        this.mGradientIsDirty = true;
        invalidateSelf();
    }

    public int[] getColors() {
        if (this.mGradientState.mGradientColors == null) {
            return null;
        }
        int[] colors = new int[this.mGradientState.mGradientColors.length];
        for (int i = 0; i < this.mGradientState.mGradientColors.length; i++) {
            if (this.mGradientState.mGradientColors[i] != null) {
                colors[i] = this.mGradientState.mGradientColors[i].getDefaultColor();
            }
        }
        return colors;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        ColorFilter colorFilter;
        Paint paint;
        if (!ensureValidRect()) {
            return;
        }
        int prevFillAlpha = this.mFillPaint.getAlpha();
        Paint paint2 = this.mStrokePaint;
        boolean z = false;
        int prevStrokeAlpha = paint2 != null ? paint2.getAlpha() : 0;
        int currFillAlpha = modulateAlpha(prevFillAlpha);
        int currStrokeAlpha = modulateAlpha(prevStrokeAlpha);
        boolean haveStroke = currStrokeAlpha > 0 && (paint = this.mStrokePaint) != null && paint.getStrokeWidth() > 0.0f;
        boolean haveFill = currFillAlpha > 0;
        GradientState st = this.mGradientState;
        ColorFilter colorFilter2 = this.mColorFilter;
        if (colorFilter2 == null) {
            colorFilter2 = this.mBlendModeColorFilter;
        }
        ColorFilter colorFilter3 = colorFilter2;
        if (haveStroke && haveFill && st.mShape != 2 && currStrokeAlpha < 255 && (this.mAlpha < 255 || colorFilter3 != null)) {
            z = true;
        }
        boolean useLayer = z;
        if (useLayer) {
            if (this.mLayerPaint == null) {
                this.mLayerPaint = new Paint();
            }
            this.mLayerPaint.setDither(st.mDither);
            this.mLayerPaint.setAlpha(this.mAlpha);
            this.mLayerPaint.setColorFilter(colorFilter3);
            float rad = this.mStrokePaint.getStrokeWidth();
            colorFilter = colorFilter3;
            canvas.saveLayer(this.mRect.left - rad, this.mRect.top - rad, this.mRect.right + rad, this.mRect.bottom + rad, this.mLayerPaint);
            this.mFillPaint.setColorFilter(null);
            this.mStrokePaint.setColorFilter(null);
        } else {
            colorFilter = colorFilter3;
            this.mFillPaint.setAlpha(currFillAlpha);
            this.mFillPaint.setDither(st.mDither);
            this.mFillPaint.setColorFilter(colorFilter);
            if (colorFilter != null && st.mSolidColors == null) {
                this.mFillPaint.setColor(this.mAlpha << 24);
            }
            if (haveStroke) {
                this.mStrokePaint.setAlpha(currStrokeAlpha);
                this.mStrokePaint.setDither(st.mDither);
                this.mStrokePaint.setColorFilter(colorFilter);
            }
        }
        switch (st.mShape) {
            case 0:
                if (st.mRadiusArray != null) {
                    buildPathIfDirty();
                    canvas.drawPath(this.mPath, this.mFillPaint);
                    if (haveStroke) {
                        canvas.drawPath(this.mPath, this.mStrokePaint);
                        break;
                    }
                } else if (st.mRadius > 0.0f) {
                    float rad2 = Math.min(st.mRadius, Math.min(this.mRect.width(), this.mRect.height()) * 0.5f);
                    canvas.drawRoundRect(this.mRect, rad2, rad2, this.mFillPaint);
                    if (haveStroke) {
                        canvas.drawRoundRect(this.mRect, rad2, rad2, this.mStrokePaint);
                        break;
                    }
                } else {
                    if (this.mFillPaint.getColor() != 0 || colorFilter != null || this.mFillPaint.getShader() != null) {
                        canvas.drawRect(this.mRect, this.mFillPaint);
                    }
                    if (haveStroke) {
                        canvas.drawRect(this.mRect, this.mStrokePaint);
                        break;
                    }
                }
                break;
            case 1:
                canvas.drawOval(this.mRect, this.mFillPaint);
                if (haveStroke) {
                    canvas.drawOval(this.mRect, this.mStrokePaint);
                    break;
                }
                break;
            case 2:
                RectF r = this.mRect;
                float y = r.centerY();
                if (!haveStroke) {
                    break;
                } else {
                    canvas.drawLine(r.left, y, r.right, y, this.mStrokePaint);
                    break;
                }
            case 3:
                Path path = buildRing(st);
                canvas.drawPath(path, this.mFillPaint);
                if (haveStroke) {
                    canvas.drawPath(path, this.mStrokePaint);
                    break;
                }
                break;
        }
        if (useLayer) {
            canvas.restore();
            return;
        }
        this.mFillPaint.setAlpha(prevFillAlpha);
        if (haveStroke) {
            this.mStrokePaint.setAlpha(prevStrokeAlpha);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setXfermode(Xfermode mode) {
        super.setXfermode(mode);
        this.mFillPaint.setXfermode(mode);
    }

    public void setAntiAlias(boolean aa) {
        this.mFillPaint.setAntiAlias(aa);
    }

    private void buildPathIfDirty() {
        GradientState st = this.mGradientState;
        if (this.mPathIsDirty) {
            ensureValidRect();
            this.mPath.reset();
            this.mPath.addRoundRect(this.mRect, st.mRadiusArray, Path.Direction.CW);
            this.mPathIsDirty = false;
        }
    }

    public void setInnerRadiusRatio(float innerRadiusRatio) {
        if (innerRadiusRatio <= 0.0f) {
            throw new IllegalArgumentException("Ratio must be greater than zero");
        }
        this.mGradientState.mInnerRadiusRatio = innerRadiusRatio;
        this.mPathIsDirty = true;
        invalidateSelf();
    }

    public float getInnerRadiusRatio() {
        return this.mGradientState.mInnerRadiusRatio;
    }

    public void setInnerRadius(int innerRadius) {
        this.mGradientState.mInnerRadius = innerRadius;
        this.mPathIsDirty = true;
        invalidateSelf();
    }

    public int getInnerRadius() {
        return this.mGradientState.mInnerRadius;
    }

    public void setThicknessRatio(float thicknessRatio) {
        if (thicknessRatio <= 0.0f) {
            throw new IllegalArgumentException("Ratio must be greater than zero");
        }
        this.mGradientState.mThicknessRatio = thicknessRatio;
        this.mPathIsDirty = true;
        invalidateSelf();
    }

    public float getThicknessRatio() {
        return this.mGradientState.mThicknessRatio;
    }

    public void setThickness(int thickness) {
        this.mGradientState.mThickness = thickness;
        this.mPathIsDirty = true;
        invalidateSelf();
    }

    public int getThickness() {
        return this.mGradientState.mThickness;
    }

    public void setPadding(int left, int top, int right, int bottom) {
        if (this.mGradientState.mPadding == null) {
            this.mGradientState.mPadding = new Rect();
        }
        this.mGradientState.mPadding.set(left, top, right, bottom);
        this.mPadding = this.mGradientState.mPadding;
        invalidateSelf();
    }

    private Path buildRing(GradientState st) {
        if (this.mRingPath == null || (st.mUseLevelForShape && this.mPathIsDirty)) {
            this.mPathIsDirty = false;
            float sweep = st.mUseLevelForShape ? (getLevel() * 360.0f) / 10000.0f : 360.0f;
            RectF bounds = new RectF(this.mRect);
            float x = bounds.width() / 2.0f;
            float y = bounds.height() / 2.0f;
            float thickness = st.mThickness != -1 ? st.mThickness : bounds.width() / st.mThicknessRatio;
            float radius = st.mInnerRadius != -1 ? st.mInnerRadius : bounds.width() / st.mInnerRadiusRatio;
            RectF innerBounds = new RectF(bounds);
            innerBounds.inset(x - radius, y - radius);
            RectF bounds2 = new RectF(innerBounds);
            bounds2.inset(-thickness, -thickness);
            Path path = this.mRingPath;
            if (path == null) {
                this.mRingPath = new Path();
            } else {
                path.reset();
            }
            Path ringPath = this.mRingPath;
            if (sweep < 360.0f && sweep > -360.0f) {
                ringPath.setFillType(Path.FillType.EVEN_ODD);
                ringPath.moveTo(x + radius, y);
                ringPath.lineTo(x + radius + thickness, y);
                ringPath.arcTo(bounds2, 0.0f, sweep, false);
                ringPath.arcTo(innerBounds, sweep, -sweep, false);
                ringPath.close();
            } else {
                ringPath.addOval(bounds2, Path.Direction.CW);
                ringPath.addOval(innerBounds, Path.Direction.CCW);
            }
            return ringPath;
        }
        return this.mRingPath;
    }

    public void setColor(int argb) {
        this.mGradientState.setSolidColors(ColorStateList.valueOf(argb));
        this.mFillPaint.setColor(argb);
        invalidateSelf();
    }

    public void setColor(ColorStateList colorStateList) {
        if (colorStateList == null) {
            setColor(0);
            return;
        }
        int[] stateSet = getState();
        int color = colorStateList.getColorForState(stateSet, 0);
        this.mGradientState.setSolidColors(colorStateList);
        this.mFillPaint.setColor(color);
        invalidateSelf();
    }

    public ColorStateList getColor() {
        return this.mGradientState.mSolidColors;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.drawable.Drawable
    public boolean onStateChange(int[] stateSet) {
        ColorStateList strokeColors;
        boolean invalidateSelf = false;
        GradientState s = this.mGradientState;
        ColorStateList solidColors = s.mSolidColors;
        if (solidColors != null) {
            int newColor = solidColors.getColorForState(stateSet, 0);
            int oldColor = this.mFillPaint.getColor();
            if (oldColor != newColor) {
                this.mFillPaint.setColor(newColor);
                invalidateSelf = true;
            }
        }
        Paint strokePaint = this.mStrokePaint;
        if (strokePaint != null && (strokeColors = s.mStrokeColors) != null) {
            int newColor2 = strokeColors.getColorForState(stateSet, 0);
            int oldColor2 = strokePaint.getColor();
            if (oldColor2 != newColor2) {
                strokePaint.setColor(newColor2);
                invalidateSelf = true;
            }
        }
        if (s.mTint != null && s.mBlendMode != null) {
            this.mBlendModeColorFilter = updateBlendModeFilter(this.mBlendModeColorFilter, s.mTint, s.mBlendMode);
            invalidateSelf = true;
        }
        if (!invalidateSelf) {
            return false;
        }
        invalidateSelf();
        return true;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        GradientState s = this.mGradientState;
        return super.isStateful() || (s.mSolidColors != null && s.mSolidColors.isStateful()) || ((s.mStrokeColors != null && s.mStrokeColors.isStateful()) || (s.mTint != null && s.mTint.isStateful()));
    }

    @Override // android.graphics.drawable.Drawable
    public boolean hasFocusStateSpecified() {
        GradientState s = this.mGradientState;
        return (s.mSolidColors != null && s.mSolidColors.hasFocusStateSpecified()) || (s.mStrokeColors != null && s.mStrokeColors.hasFocusStateSpecified()) || (s.mTint != null && s.mTint.hasFocusStateSpecified());
    }

    @Override // android.graphics.drawable.Drawable
    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | this.mGradientState.getChangingConfigurations();
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        if (alpha != this.mAlpha) {
            this.mAlpha = alpha;
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        return this.mAlpha;
    }

    @Override // android.graphics.drawable.Drawable
    public void setDither(boolean dither) {
        if (dither != this.mGradientState.mDither) {
            this.mGradientState.mDither = dither;
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public ColorFilter getColorFilter() {
        return this.mColorFilter;
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        if (colorFilter != this.mColorFilter) {
            this.mColorFilter = colorFilter;
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setTintList(ColorStateList tint) {
        this.mGradientState.mTint = tint;
        this.mBlendModeColorFilter = updateBlendModeFilter(this.mBlendModeColorFilter, tint, this.mGradientState.mBlendMode);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void setTintBlendMode(BlendMode blendMode) {
        this.mGradientState.mBlendMode = blendMode;
        this.mBlendModeColorFilter = updateBlendModeFilter(this.mBlendModeColorFilter, this.mGradientState.mTint, blendMode);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return (this.mAlpha == 255 && this.mGradientState.mOpaqueOverBounds && isOpaqueForState()) ? -1 : -3;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.drawable.Drawable
    public void onBoundsChange(Rect r) {
        super.onBoundsChange(r);
        this.mRingPath = null;
        this.mPathIsDirty = true;
        this.mGradientIsDirty = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.drawable.Drawable
    public boolean onLevelChange(int level) {
        super.onLevelChange(level);
        this.mGradientIsDirty = true;
        this.mPathIsDirty = true;
        invalidateSelf();
        return true;
    }

    private boolean ensureValidRect() {
        float radius;
        float x0;
        float y0;
        float x1;
        float y1;
        if (this.mGradientIsDirty) {
            this.mGradientIsDirty = false;
            Rect bounds = getBounds();
            float inset = 0.0f;
            Paint paint = this.mStrokePaint;
            if (paint != null) {
                inset = paint.getStrokeWidth() * 0.5f;
            }
            GradientState st = this.mGradientState;
            this.mRect.set(bounds.left + inset, bounds.top + inset, bounds.right - inset, bounds.bottom - inset);
            int[] gradientColors = null;
            if (st.mGradientColors != null) {
                gradientColors = new int[st.mGradientColors.length];
                for (int i = 0; i < gradientColors.length; i++) {
                    if (st.mGradientColors[i] != null) {
                        gradientColors[i] = st.mGradientColors[i].getDefaultColor();
                    }
                }
            }
            if (gradientColors != null) {
                RectF r = this.mRect;
                if (st.mGradient == 0) {
                    float level = st.mUseLevel ? getLevel() / 10000.0f : 1.0f;
                    switch (C08201.f88x8f1352bc[st.mOrientation.ordinal()]) {
                        case 1:
                            float x02 = r.left;
                            float y02 = r.top;
                            float y12 = r.bottom * level;
                            x0 = x02;
                            y0 = y02;
                            x1 = x02;
                            y1 = y12;
                            break;
                        case 2:
                            float x03 = r.right;
                            float y03 = r.top;
                            float x12 = r.left * level;
                            float y13 = r.bottom * level;
                            x0 = x03;
                            y0 = y03;
                            x1 = x12;
                            y1 = y13;
                            break;
                        case 3:
                            float x04 = r.right;
                            float y04 = r.top;
                            float x13 = r.left * level;
                            x0 = x04;
                            y0 = y04;
                            x1 = x13;
                            y1 = y04;
                            break;
                        case 4:
                            float x05 = r.right;
                            float y05 = r.bottom;
                            float x14 = r.left * level;
                            float y14 = r.top * level;
                            x0 = x05;
                            y0 = y05;
                            x1 = x14;
                            y1 = y14;
                            break;
                        case 5:
                            float x06 = r.left;
                            float y06 = r.bottom;
                            float y15 = r.top * level;
                            x0 = x06;
                            y0 = y06;
                            x1 = x06;
                            y1 = y15;
                            break;
                        case 6:
                            float x07 = r.left;
                            float y07 = r.bottom;
                            float x15 = r.right * level;
                            float y16 = r.top * level;
                            x0 = x07;
                            y0 = y07;
                            x1 = x15;
                            y1 = y16;
                            break;
                        case 7:
                            float x08 = r.left;
                            float y08 = r.top;
                            float x16 = r.right * level;
                            x0 = x08;
                            y0 = y08;
                            x1 = x16;
                            y1 = y08;
                            break;
                        default:
                            float x09 = r.left;
                            float y09 = r.top;
                            float x17 = r.right * level;
                            x0 = x09;
                            y0 = y09;
                            x1 = x17;
                            y1 = r.bottom * level;
                            break;
                    }
                    this.mFillPaint.setShader(new LinearGradient(x0, y0, x1, y1, gradientColors, st.mPositions, Shader.TileMode.CLAMP));
                } else if (st.mGradient == 1) {
                    float x010 = r.left + ((r.right - r.left) * st.mCenterX);
                    float y010 = r.top + ((r.bottom - r.top) * st.mCenterY);
                    float radius2 = st.mGradientRadius;
                    if (st.mGradientRadiusType == 1) {
                        float width = st.mWidth >= 0 ? st.mWidth : r.width();
                        float height = st.mHeight >= 0 ? st.mHeight : r.height();
                        radius2 *= Math.min(width, height);
                    } else if (st.mGradientRadiusType == 2) {
                        radius2 *= Math.min(r.width(), r.height());
                    }
                    if (st.mUseLevel) {
                        radius2 *= getLevel() / 10000.0f;
                    }
                    this.mGradientRadius = radius2;
                    if (radius2 > 0.0f) {
                        radius = radius2;
                    } else {
                        radius = 0.001f;
                    }
                    this.mFillPaint.setShader(new RadialGradient(x010, y010, radius, gradientColors, (float[]) null, Shader.TileMode.CLAMP));
                } else if (st.mGradient == 2) {
                    float x011 = r.left + ((r.right - r.left) * st.mCenterX);
                    float y011 = r.top + ((r.bottom - r.top) * st.mCenterY);
                    int[] tempColors = gradientColors;
                    float[] tempPositions = null;
                    if (st.mUseLevel) {
                        tempColors = st.mTempColors;
                        int length = gradientColors.length;
                        if (tempColors == null || tempColors.length != length + 1) {
                            int[] iArr = new int[length + 1];
                            st.mTempColors = iArr;
                            tempColors = iArr;
                        }
                        System.arraycopy(gradientColors, 0, tempColors, 0, length);
                        tempColors[length] = gradientColors[length - 1];
                        float[] tempPositions2 = st.mTempPositions;
                        float fraction = 1.0f / (length - 1);
                        if (tempPositions2 == null || tempPositions2.length != length + 1) {
                            float[] fArr = new float[length + 1];
                            st.mTempPositions = fArr;
                            tempPositions2 = fArr;
                        }
                        float level2 = getLevel() / 10000.0f;
                        for (int i2 = 0; i2 < length; i2++) {
                            tempPositions2[i2] = i2 * fraction * level2;
                        }
                        tempPositions2[length] = 1.0f;
                        tempPositions = tempPositions2;
                    }
                    this.mFillPaint.setShader(new SweepGradient(x011, y011, tempColors, tempPositions));
                }
                if (st.mSolidColors == null) {
                    this.mFillPaint.setColor(-16777216);
                }
            }
        }
        return !this.mRect.isEmpty();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.graphics.drawable.GradientDrawable$1 */
    /* loaded from: classes.dex */
    public static /* synthetic */ class C08201 {

        /* renamed from: $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation */
        static final /* synthetic */ int[] f88x8f1352bc;

        static {
            int[] iArr = new int[Orientation.values().length];
            f88x8f1352bc = iArr;
            try {
                iArr[Orientation.TOP_BOTTOM.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f88x8f1352bc[Orientation.TR_BL.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f88x8f1352bc[Orientation.RIGHT_LEFT.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f88x8f1352bc[Orientation.BR_TL.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f88x8f1352bc[Orientation.BOTTOM_TOP.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                f88x8f1352bc[Orientation.BL_TR.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                f88x8f1352bc[Orientation.LEFT_RIGHT.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs, Resources.Theme theme) throws XmlPullParserException, IOException {
        super.inflate(r, parser, attrs, theme);
        this.mGradientState.setDensity(Drawable.resolveDensity(r, 0));
        TypedArray a = obtainAttributes(r, theme, attrs, C4057R.styleable.GradientDrawable);
        updateStateFromTypedArray(a);
        a.recycle();
        inflateChildElements(r, parser, attrs, theme);
        updateLocalState(r);
    }

    @Override // android.graphics.drawable.Drawable
    public void applyTheme(Resources.Theme t) {
        super.applyTheme(t);
        GradientState state = this.mGradientState;
        if (state == null) {
            return;
        }
        state.setDensity(Drawable.resolveDensity(t.getResources(), 0));
        if (state.mThemeAttrs != null) {
            TypedArray a = t.resolveAttributes(state.mThemeAttrs, C4057R.styleable.GradientDrawable);
            updateStateFromTypedArray(a);
            a.recycle();
        }
        if (state.mTint != null && state.mTint.canApplyTheme()) {
            state.mTint = state.mTint.obtainForTheme(t);
        }
        if (state.mSolidColors != null && state.mSolidColors.canApplyTheme()) {
            state.mSolidColors = state.mSolidColors.obtainForTheme(t);
        }
        if (state.mStrokeColors != null && state.mStrokeColors.canApplyTheme()) {
            state.mStrokeColors = state.mStrokeColors.obtainForTheme(t);
        }
        if (state.mGradientColors != null) {
            for (int i = 0; i < state.mGradientColors.length; i++) {
                if (state.mGradientColors[i] != null && state.mGradientColors[i].canApplyTheme()) {
                    state.mGradientColors[i] = state.mGradientColors[i].obtainForTheme(t);
                }
            }
        }
        applyThemeChildElements(t);
        updateLocalState(t.getResources());
    }

    private void updateStateFromTypedArray(TypedArray a) {
        GradientState state = this.mGradientState;
        state.mChangingConfigurations |= a.getChangingConfigurations();
        state.mThemeAttrs = a.extractThemeAttrs();
        state.mShape = a.getInt(3, state.mShape);
        state.mDither = a.getBoolean(0, state.mDither);
        if (state.mShape == 3) {
            state.mInnerRadius = a.getDimensionPixelSize(7, state.mInnerRadius);
            if (state.mInnerRadius == -1) {
                state.mInnerRadiusRatio = a.getFloat(4, state.mInnerRadiusRatio);
            }
            state.mThickness = a.getDimensionPixelSize(8, state.mThickness);
            if (state.mThickness == -1) {
                state.mThicknessRatio = a.getFloat(5, state.mThicknessRatio);
            }
            state.mUseLevelForShape = a.getBoolean(6, state.mUseLevelForShape);
        }
        int tintMode = a.getInt(9, -1);
        if (tintMode != -1) {
            state.mBlendMode = Drawable.parseBlendMode(tintMode, BlendMode.SRC_IN);
        }
        ColorStateList tint = a.getColorStateList(1);
        if (tint != null) {
            state.mTint = tint;
        }
        int insetLeft = a.getDimensionPixelSize(10, state.mOpticalInsets.left);
        int insetTop = a.getDimensionPixelSize(11, state.mOpticalInsets.top);
        int insetRight = a.getDimensionPixelSize(12, state.mOpticalInsets.right);
        int insetBottom = a.getDimensionPixelSize(13, state.mOpticalInsets.bottom);
        state.mOpticalInsets = Insets.m186of(insetLeft, insetTop, insetRight, insetBottom);
    }

    @Override // android.graphics.drawable.Drawable
    public boolean canApplyTheme() {
        GradientState gradientState = this.mGradientState;
        return (gradientState != null && gradientState.canApplyTheme()) || super.canApplyTheme();
    }

    private void applyThemeChildElements(Resources.Theme t) {
        TypedArray a;
        GradientState st = this.mGradientState;
        if (st.mAttrSize != null) {
            TypedArray a2 = t.resolveAttributes(st.mAttrSize, C4057R.styleable.GradientDrawableSize);
            updateGradientDrawableSize(a2);
            a2.recycle();
        }
        if (st.mAttrGradient != null) {
            a = t.resolveAttributes(st.mAttrGradient, C4057R.styleable.GradientDrawableGradient);
            try {
                updateGradientDrawableGradient(t.getResources(), a);
                a.recycle();
            } finally {
                a.recycle();
            }
        }
        if (st.mAttrSolid != null) {
            TypedArray a3 = t.resolveAttributes(st.mAttrSolid, C4057R.styleable.GradientDrawableSolid);
            updateGradientDrawableSolid(a3);
            a3.recycle();
        }
        if (st.mAttrStroke != null) {
            TypedArray a4 = t.resolveAttributes(st.mAttrStroke, C4057R.styleable.GradientDrawableStroke);
            updateGradientDrawableStroke(a4);
            a4.recycle();
        }
        if (st.mAttrCorners != null) {
            a = t.resolveAttributes(st.mAttrCorners, C4057R.styleable.DrawableCorners);
            updateDrawableCorners(a);
        }
        if (st.mAttrPadding != null) {
            a = t.resolveAttributes(st.mAttrPadding, C4057R.styleable.GradientDrawablePadding);
            updateGradientDrawablePadding(a);
        }
    }

    private void inflateChildElements(Resources r, XmlPullParser parser, AttributeSet attrs, Resources.Theme theme) throws XmlPullParserException, IOException {
        int innerDepth = parser.getDepth() + 1;
        while (true) {
            int type = parser.next();
            if (type != 1) {
                int depth = parser.getDepth();
                if (depth >= innerDepth || type != 3) {
                    if (type == 2 && depth <= innerDepth) {
                        String name = parser.getName();
                        if (name.equals("size")) {
                            TypedArray a = obtainAttributes(r, theme, attrs, C4057R.styleable.GradientDrawableSize);
                            updateGradientDrawableSize(a);
                            a.recycle();
                        } else if (name.equals("gradient")) {
                            TypedArray a2 = obtainAttributes(r, theme, attrs, C4057R.styleable.GradientDrawableGradient);
                            updateGradientDrawableGradient(r, a2);
                            a2.recycle();
                        } else if (name.equals("solid")) {
                            TypedArray a3 = obtainAttributes(r, theme, attrs, C4057R.styleable.GradientDrawableSolid);
                            updateGradientDrawableSolid(a3);
                            a3.recycle();
                        } else if (name.equals("stroke")) {
                            TypedArray a4 = obtainAttributes(r, theme, attrs, C4057R.styleable.GradientDrawableStroke);
                            updateGradientDrawableStroke(a4);
                            a4.recycle();
                        } else if (name.equals("corners")) {
                            TypedArray a5 = obtainAttributes(r, theme, attrs, C4057R.styleable.DrawableCorners);
                            updateDrawableCorners(a5);
                            a5.recycle();
                        } else if (name.equals("padding")) {
                            TypedArray a6 = obtainAttributes(r, theme, attrs, C4057R.styleable.GradientDrawablePadding);
                            updateGradientDrawablePadding(a6);
                            a6.recycle();
                        } else {
                            Log.m104w("drawable", "Bad element under <shape>: " + name);
                        }
                    }
                } else {
                    return;
                }
            } else {
                return;
            }
        }
    }

    private void updateGradientDrawablePadding(TypedArray a) {
        GradientState st = this.mGradientState;
        st.mChangingConfigurations |= a.getChangingConfigurations();
        st.mAttrPadding = a.extractThemeAttrs();
        if (st.mPadding == null) {
            st.mPadding = new Rect();
        }
        Rect pad = st.mPadding;
        pad.set(a.getDimensionPixelOffset(0, pad.left), a.getDimensionPixelOffset(1, pad.top), a.getDimensionPixelOffset(2, pad.right), a.getDimensionPixelOffset(3, pad.bottom));
        this.mPadding = pad;
    }

    private void updateDrawableCorners(TypedArray a) {
        GradientState st = this.mGradientState;
        st.mChangingConfigurations |= a.getChangingConfigurations();
        st.mAttrCorners = a.extractThemeAttrs();
        int radius = a.getDimensionPixelSize(0, (int) st.mRadius);
        setCornerRadius(radius);
        int topLeftRadius = a.getDimensionPixelSize(1, radius);
        int topRightRadius = a.getDimensionPixelSize(2, radius);
        int bottomLeftRadius = a.getDimensionPixelSize(3, radius);
        int bottomRightRadius = a.getDimensionPixelSize(4, radius);
        if (topLeftRadius != radius || topRightRadius != radius || bottomLeftRadius != radius || bottomRightRadius != radius) {
            setCornerRadii(new float[]{topLeftRadius, topLeftRadius, topRightRadius, topRightRadius, bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius});
        }
    }

    private void updateGradientDrawableStroke(TypedArray a) {
        GradientState st = this.mGradientState;
        st.mChangingConfigurations |= a.getChangingConfigurations();
        st.mAttrStroke = a.extractThemeAttrs();
        int defaultStrokeWidth = Math.max(0, st.mStrokeWidth);
        int width = a.getDimensionPixelSize(0, defaultStrokeWidth);
        float dashWidth = a.getDimension(2, st.mStrokeDashWidth);
        ColorStateList colorStateList = a.getColorStateList(1);
        if (colorStateList == null) {
            colorStateList = st.mStrokeColors;
        }
        if (dashWidth != 0.0f) {
            float dashGap = a.getDimension(3, st.mStrokeDashGap);
            setStroke(width, colorStateList, dashWidth, dashGap);
            return;
        }
        setStroke(width, colorStateList);
    }

    private void updateGradientDrawableSolid(TypedArray a) {
        GradientState st = this.mGradientState;
        st.mChangingConfigurations |= a.getChangingConfigurations();
        st.mAttrSolid = a.extractThemeAttrs();
        ColorStateList colorStateList = a.getColorStateList(0);
        if (colorStateList != null) {
            setColor(colorStateList);
        }
    }

    private void updateGradientDrawableGradient(Resources r, TypedArray a) {
        boolean hasGradientColors;
        boolean hasCenterColor;
        float radius;
        int radiusType;
        GradientState st = this.mGradientState;
        st.mChangingConfigurations |= a.getChangingConfigurations();
        st.mAttrGradient = a.extractThemeAttrs();
        st.mCenterX = getFloatOrFraction(a, 5, st.mCenterX);
        st.mCenterY = getFloatOrFraction(a, 6, st.mCenterY);
        st.mUseLevel = a.getBoolean(2, st.mUseLevel);
        st.mGradient = a.getInt(4, st.mGradient);
        ColorStateList startCSL = a.getColorStateList(0);
        ColorStateList centerCSL = a.getColorStateList(8);
        ColorStateList endCSL = a.getColorStateList(1);
        if (st.mGradientColors == null) {
            hasGradientColors = false;
        } else {
            hasGradientColors = true;
        }
        boolean hasGradientCenter = st.hasCenterColor();
        int startColor = startCSL != null ? startCSL.getDefaultColor() : 0;
        int centerColor = centerCSL != null ? centerCSL.getDefaultColor() : 0;
        int endColor = endCSL != null ? endCSL.getDefaultColor() : 0;
        if (hasGradientColors && st.mGradientColors[0] != null) {
            startColor = st.mGradientColors[0].getDefaultColor();
        }
        if (hasGradientCenter && st.mGradientColors[1] != null) {
            centerColor = st.mGradientColors[1].getDefaultColor();
        }
        if (hasGradientCenter && st.mGradientColors[2] != null) {
            endColor = st.mGradientColors[2].getDefaultColor();
        } else if (hasGradientColors && st.mGradientColors[1] != null) {
            endColor = st.mGradientColors[1].getDefaultColor();
        }
        if (!a.hasValue(8) && !hasGradientCenter) {
            hasCenterColor = false;
        } else {
            hasCenterColor = true;
        }
        if (hasCenterColor) {
            st.mGradientColors = new ColorStateList[3];
            st.mGradientColors[0] = startCSL != null ? startCSL : ColorStateList.valueOf(startColor);
            st.mGradientColors[1] = centerCSL != null ? centerCSL : ColorStateList.valueOf(centerColor);
            st.mGradientColors[2] = endCSL != null ? endCSL : ColorStateList.valueOf(endColor);
            st.mPositions = new float[3];
            st.mPositions[0] = 0.0f;
            st.mPositions[1] = st.mCenterX != 0.5f ? st.mCenterX : st.mCenterY;
            st.mPositions[2] = 1.0f;
        } else {
            st.mGradientColors = new ColorStateList[2];
            st.mGradientColors[0] = startCSL != null ? startCSL : ColorStateList.valueOf(startColor);
            st.mGradientColors[1] = endCSL != null ? endCSL : ColorStateList.valueOf(endColor);
        }
        int angle = (int) a.getFloat(3, st.mAngle);
        if (sWrapNegativeAngleMeasurements) {
            st.mAngle = ((angle % 360) + 360) % 360;
        } else {
            st.mAngle = angle % 360;
        }
        if (st.mAngle >= 0) {
            switch (st.mAngle) {
                case 0:
                    st.mOrientation = Orientation.LEFT_RIGHT;
                    break;
                case 45:
                    st.mOrientation = Orientation.BL_TR;
                    break;
                case 90:
                    st.mOrientation = Orientation.BOTTOM_TOP;
                    break;
                case 135:
                    st.mOrientation = Orientation.BR_TL;
                    break;
                case 180:
                    st.mOrientation = Orientation.RIGHT_LEFT;
                    break;
                case 225:
                    st.mOrientation = Orientation.TR_BL;
                    break;
                case 270:
                    st.mOrientation = Orientation.TOP_BOTTOM;
                    break;
                case 315:
                    st.mOrientation = Orientation.TL_BR;
                    break;
            }
        } else {
            st.mOrientation = DEFAULT_ORIENTATION;
        }
        TypedValue tv = a.peekValue(7);
        if (tv != null) {
            if (tv.type == 6) {
                radius = tv.getFraction(1.0f, 1.0f);
                int unit = (tv.data >> 0) & 15;
                if (unit == 1) {
                    radiusType = 2;
                } else {
                    radiusType = 1;
                }
            } else if (tv.type == 5) {
                radius = tv.getDimension(r.getDisplayMetrics());
                radiusType = 0;
            } else {
                radius = tv.getFloat();
                radiusType = 0;
            }
            st.mGradientRadius = radius;
            st.mGradientRadiusType = radiusType;
        }
    }

    private void updateGradientDrawableSize(TypedArray a) {
        GradientState st = this.mGradientState;
        st.mChangingConfigurations |= a.getChangingConfigurations();
        st.mAttrSize = a.extractThemeAttrs();
        st.mWidth = a.getDimensionPixelSize(1, st.mWidth);
        st.mHeight = a.getDimensionPixelSize(0, st.mHeight);
    }

    private static float getFloatOrFraction(TypedArray a, int index, float defaultValue) {
        TypedValue tv = a.peekValue(index);
        if (tv == null) {
            return defaultValue;
        }
        boolean vIsFraction = tv.type == 6;
        float v = vIsFraction ? tv.getFraction(1.0f, 1.0f) : tv.getFloat();
        return v;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.mGradientState.mWidth;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.mGradientState.mHeight;
    }

    @Override // android.graphics.drawable.Drawable
    public Insets getOpticalInsets() {
        return this.mGradientState.mOpticalInsets;
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        this.mGradientState.mChangingConfigurations = getChangingConfigurations();
        return this.mGradientState;
    }

    private boolean isOpaqueForState() {
        Paint paint;
        if (this.mGradientState.mStrokeWidth < 0 || (paint = this.mStrokePaint) == null || isOpaque(paint.getColor())) {
            return this.mGradientState.mGradientColors != null || isOpaque(this.mFillPaint.getColor());
        }
        return false;
    }

    @Override // android.graphics.drawable.Drawable
    public void getOutline(Outline outline) {
        float f;
        Paint paint;
        GradientState st = this.mGradientState;
        Rect bounds = getBounds();
        boolean useFillOpacity = st.mOpaqueOverShape && (this.mGradientState.mStrokeWidth <= 0 || (paint = this.mStrokePaint) == null || paint.getAlpha() == this.mFillPaint.getAlpha());
        if (useFillOpacity) {
            f = modulateAlpha(this.mFillPaint.getAlpha()) / 255.0f;
        } else {
            f = 0.0f;
        }
        outline.setAlpha(f);
        switch (st.mShape) {
            case 0:
                if (st.mRadiusArray != null) {
                    buildPathIfDirty();
                    outline.setPath(this.mPath);
                    return;
                }
                float rad = 0.0f;
                if (st.mRadius > 0.0f) {
                    rad = Math.min(st.mRadius, Math.min(bounds.width(), bounds.height()) * 0.5f);
                }
                outline.setRoundRect(bounds, rad);
                return;
            case 1:
                outline.setOval(bounds);
                return;
            case 2:
                Paint paint2 = this.mStrokePaint;
                float halfStrokeWidth = paint2 == null ? 1.0E-4f : paint2.getStrokeWidth() * 0.5f;
                float centerY = bounds.centerY();
                int top = (int) Math.floor(centerY - halfStrokeWidth);
                int bottom = (int) Math.ceil(centerY + halfStrokeWidth);
                outline.setRect(bounds.left, top, bounds.right, bottom);
                return;
            default:
                return;
        }
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            this.mGradientState = new GradientState(this.mGradientState, (Resources) null);
            updateLocalState(null);
            this.mMutated = true;
        }
        return this;
    }

    @Override // android.graphics.drawable.Drawable
    public void clearMutated() {
        super.clearMutated();
        this.mMutated = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class GradientState extends Drawable.ConstantState {
        public int mAngle;
        int[] mAttrCorners;
        int[] mAttrGradient;
        int[] mAttrPadding;
        int[] mAttrSize;
        int[] mAttrSolid;
        int[] mAttrStroke;
        BlendMode mBlendMode;
        float mCenterX;
        float mCenterY;
        public int mChangingConfigurations;
        int mDensity;
        public boolean mDither;
        public int mGradient;
        public ColorStateList[] mGradientColors;
        float mGradientRadius;
        int mGradientRadiusType;
        public int mHeight;
        public int mInnerRadius;
        public float mInnerRadiusRatio;
        boolean mOpaqueOverBounds;
        boolean mOpaqueOverShape;
        public Insets mOpticalInsets;
        public Orientation mOrientation;
        public Rect mPadding;
        public float[] mPositions;
        public float mRadius;
        public float[] mRadiusArray;
        public int mShape;
        public ColorStateList mSolidColors;
        public ColorStateList mStrokeColors;
        public float mStrokeDashGap;
        public float mStrokeDashWidth;
        public int mStrokeWidth;
        public int[] mTempColors;
        public float[] mTempPositions;
        int[] mThemeAttrs;
        public int mThickness;
        public float mThicknessRatio;
        ColorStateList mTint;
        boolean mUseLevel;
        boolean mUseLevelForShape;
        public int mWidth;

        public GradientState(Orientation orientation, int[] gradientColors) {
            this.mShape = 0;
            this.mGradient = 0;
            this.mAngle = 0;
            this.mStrokeWidth = -1;
            this.mStrokeDashWidth = 0.0f;
            this.mStrokeDashGap = 0.0f;
            this.mRadius = 0.0f;
            this.mRadiusArray = null;
            this.mPadding = null;
            this.mWidth = -1;
            this.mHeight = -1;
            this.mInnerRadiusRatio = 3.0f;
            this.mThicknessRatio = GradientDrawable.DEFAULT_THICKNESS_RATIO;
            this.mInnerRadius = -1;
            this.mThickness = -1;
            this.mDither = false;
            this.mOpticalInsets = Insets.NONE;
            this.mCenterX = 0.5f;
            this.mCenterY = 0.5f;
            this.mGradientRadius = 0.5f;
            this.mGradientRadiusType = 0;
            this.mUseLevel = false;
            this.mUseLevelForShape = true;
            this.mTint = null;
            this.mBlendMode = Drawable.DEFAULT_BLEND_MODE;
            this.mDensity = 160;
            this.mOrientation = orientation;
            setGradientColors(gradientColors);
        }

        public GradientState(GradientState orig, Resources res) {
            this.mShape = 0;
            this.mGradient = 0;
            this.mAngle = 0;
            this.mStrokeWidth = -1;
            this.mStrokeDashWidth = 0.0f;
            this.mStrokeDashGap = 0.0f;
            this.mRadius = 0.0f;
            this.mRadiusArray = null;
            this.mPadding = null;
            this.mWidth = -1;
            this.mHeight = -1;
            this.mInnerRadiusRatio = 3.0f;
            this.mThicknessRatio = GradientDrawable.DEFAULT_THICKNESS_RATIO;
            this.mInnerRadius = -1;
            this.mThickness = -1;
            this.mDither = false;
            this.mOpticalInsets = Insets.NONE;
            this.mCenterX = 0.5f;
            this.mCenterY = 0.5f;
            this.mGradientRadius = 0.5f;
            this.mGradientRadiusType = 0;
            this.mUseLevel = false;
            this.mUseLevelForShape = true;
            this.mTint = null;
            this.mBlendMode = Drawable.DEFAULT_BLEND_MODE;
            this.mDensity = 160;
            this.mChangingConfigurations = orig.mChangingConfigurations;
            this.mShape = orig.mShape;
            this.mGradient = orig.mGradient;
            this.mAngle = orig.mAngle;
            this.mOrientation = orig.mOrientation;
            this.mSolidColors = orig.mSolidColors;
            ColorStateList[] colorStateListArr = orig.mGradientColors;
            if (colorStateListArr != null) {
                this.mGradientColors = (ColorStateList[]) colorStateListArr.clone();
            }
            float[] fArr = orig.mPositions;
            if (fArr != null) {
                this.mPositions = (float[]) fArr.clone();
            }
            this.mStrokeColors = orig.mStrokeColors;
            this.mStrokeWidth = orig.mStrokeWidth;
            this.mStrokeDashWidth = orig.mStrokeDashWidth;
            this.mStrokeDashGap = orig.mStrokeDashGap;
            this.mRadius = orig.mRadius;
            float[] fArr2 = orig.mRadiusArray;
            if (fArr2 != null) {
                this.mRadiusArray = (float[]) fArr2.clone();
            }
            if (orig.mPadding != null) {
                this.mPadding = new Rect(orig.mPadding);
            }
            this.mWidth = orig.mWidth;
            this.mHeight = orig.mHeight;
            this.mInnerRadiusRatio = orig.mInnerRadiusRatio;
            this.mThicknessRatio = orig.mThicknessRatio;
            this.mInnerRadius = orig.mInnerRadius;
            this.mThickness = orig.mThickness;
            this.mDither = orig.mDither;
            this.mOpticalInsets = orig.mOpticalInsets;
            this.mCenterX = orig.mCenterX;
            this.mCenterY = orig.mCenterY;
            this.mGradientRadius = orig.mGradientRadius;
            this.mGradientRadiusType = orig.mGradientRadiusType;
            this.mUseLevel = orig.mUseLevel;
            this.mUseLevelForShape = orig.mUseLevelForShape;
            this.mOpaqueOverBounds = orig.mOpaqueOverBounds;
            this.mOpaqueOverShape = orig.mOpaqueOverShape;
            this.mTint = orig.mTint;
            this.mBlendMode = orig.mBlendMode;
            this.mThemeAttrs = orig.mThemeAttrs;
            this.mAttrSize = orig.mAttrSize;
            this.mAttrGradient = orig.mAttrGradient;
            this.mAttrSolid = orig.mAttrSolid;
            this.mAttrStroke = orig.mAttrStroke;
            this.mAttrCorners = orig.mAttrCorners;
            this.mAttrPadding = orig.mAttrPadding;
            int resolveDensity = Drawable.resolveDensity(res, orig.mDensity);
            this.mDensity = resolveDensity;
            int i = orig.mDensity;
            if (i != resolveDensity) {
                applyDensityScaling(i, resolveDensity);
            }
        }

        public final void setDensity(int targetDensity) {
            if (this.mDensity != targetDensity) {
                int sourceDensity = this.mDensity;
                this.mDensity = targetDensity;
                applyDensityScaling(sourceDensity, targetDensity);
            }
        }

        public boolean hasCenterColor() {
            ColorStateList[] colorStateListArr = this.mGradientColors;
            return colorStateListArr != null && colorStateListArr.length == 3;
        }

        private void applyDensityScaling(int sourceDensity, int targetDensity) {
            int i = this.mInnerRadius;
            if (i > 0) {
                this.mInnerRadius = Drawable.scaleFromDensity(i, sourceDensity, targetDensity, true);
            }
            int i2 = this.mThickness;
            if (i2 > 0) {
                this.mThickness = Drawable.scaleFromDensity(i2, sourceDensity, targetDensity, true);
            }
            if (this.mOpticalInsets != Insets.NONE) {
                int left = Drawable.scaleFromDensity(this.mOpticalInsets.left, sourceDensity, targetDensity, true);
                int top = Drawable.scaleFromDensity(this.mOpticalInsets.top, sourceDensity, targetDensity, true);
                int right = Drawable.scaleFromDensity(this.mOpticalInsets.right, sourceDensity, targetDensity, true);
                int bottom = Drawable.scaleFromDensity(this.mOpticalInsets.bottom, sourceDensity, targetDensity, true);
                this.mOpticalInsets = Insets.m186of(left, top, right, bottom);
            }
            Rect rect = this.mPadding;
            if (rect != null) {
                rect.left = Drawable.scaleFromDensity(rect.left, sourceDensity, targetDensity, false);
                Rect rect2 = this.mPadding;
                rect2.top = Drawable.scaleFromDensity(rect2.top, sourceDensity, targetDensity, false);
                Rect rect3 = this.mPadding;
                rect3.right = Drawable.scaleFromDensity(rect3.right, sourceDensity, targetDensity, false);
                Rect rect4 = this.mPadding;
                rect4.bottom = Drawable.scaleFromDensity(rect4.bottom, sourceDensity, targetDensity, false);
            }
            float f = this.mRadius;
            if (f > 0.0f) {
                this.mRadius = Drawable.scaleFromDensity(f, sourceDensity, targetDensity);
            }
            float[] fArr = this.mRadiusArray;
            if (fArr != null) {
                fArr[0] = Drawable.scaleFromDensity((int) fArr[0], sourceDensity, targetDensity, true);
                float[] fArr2 = this.mRadiusArray;
                fArr2[1] = Drawable.scaleFromDensity((int) fArr2[1], sourceDensity, targetDensity, true);
                float[] fArr3 = this.mRadiusArray;
                fArr3[2] = Drawable.scaleFromDensity((int) fArr3[2], sourceDensity, targetDensity, true);
                float[] fArr4 = this.mRadiusArray;
                fArr4[3] = Drawable.scaleFromDensity((int) fArr4[3], sourceDensity, targetDensity, true);
            }
            int i3 = this.mStrokeWidth;
            if (i3 > 0) {
                this.mStrokeWidth = Drawable.scaleFromDensity(i3, sourceDensity, targetDensity, true);
            }
            if (this.mStrokeDashWidth > 0.0f) {
                this.mStrokeDashWidth = Drawable.scaleFromDensity(this.mStrokeDashGap, sourceDensity, targetDensity);
            }
            float f2 = this.mStrokeDashGap;
            if (f2 > 0.0f) {
                this.mStrokeDashGap = Drawable.scaleFromDensity(f2, sourceDensity, targetDensity);
            }
            if (this.mGradientRadiusType == 0) {
                this.mGradientRadius = Drawable.scaleFromDensity(this.mGradientRadius, sourceDensity, targetDensity);
            }
            int i4 = this.mWidth;
            if (i4 > 0) {
                this.mWidth = Drawable.scaleFromDensity(i4, sourceDensity, targetDensity, true);
            }
            int i5 = this.mHeight;
            if (i5 > 0) {
                this.mHeight = Drawable.scaleFromDensity(i5, sourceDensity, targetDensity, true);
            }
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public boolean canApplyTheme() {
            ColorStateList colorStateList;
            ColorStateList colorStateList2;
            ColorStateList colorStateList3;
            ColorStateList[] colorStateListArr = this.mGradientColors;
            boolean mGradientColorState = colorStateListArr != null;
            if (colorStateListArr != null) {
                int i = 0;
                while (true) {
                    ColorStateList[] colorStateListArr2 = this.mGradientColors;
                    if (i >= colorStateListArr2.length) {
                        break;
                    }
                    ColorStateList colorStateList4 = colorStateListArr2[i];
                    mGradientColorState |= colorStateList4 != null && colorStateList4.canApplyTheme();
                    i++;
                }
            }
            return (this.mThemeAttrs == null && this.mAttrSize == null && this.mAttrGradient == null && this.mAttrSolid == null && this.mAttrStroke == null && this.mAttrCorners == null && this.mAttrPadding == null && ((colorStateList = this.mTint) == null || !colorStateList.canApplyTheme()) && (((colorStateList2 = this.mStrokeColors) == null || !colorStateList2.canApplyTheme()) && (((colorStateList3 = this.mSolidColors) == null || !colorStateList3.canApplyTheme()) && !mGradientColorState && !super.canApplyTheme()))) ? false : true;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new GradientDrawable(this, null);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources res) {
            GradientState state;
            int density = Drawable.resolveDensity(res, this.mDensity);
            if (density != this.mDensity) {
                state = new GradientState(this, res);
            } else {
                state = this;
            }
            return new GradientDrawable(state, res);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            int i = this.mChangingConfigurations;
            ColorStateList colorStateList = this.mStrokeColors;
            int changingConfigurations = i | (colorStateList != null ? colorStateList.getChangingConfigurations() : 0);
            ColorStateList colorStateList2 = this.mSolidColors;
            int changingConfigurations2 = changingConfigurations | (colorStateList2 != null ? colorStateList2.getChangingConfigurations() : 0);
            ColorStateList colorStateList3 = this.mTint;
            return changingConfigurations2 | (colorStateList3 != null ? colorStateList3.getChangingConfigurations() : 0);
        }

        public void setShape(int shape) {
            this.mShape = shape;
            computeOpacity();
        }

        public void setGradientType(int gradient) {
            this.mGradient = gradient;
        }

        public void setGradientCenter(float x, float y) {
            this.mCenterX = x;
            this.mCenterY = y;
        }

        public Orientation getOrientation() {
            return this.mOrientation;
        }

        public void setGradientColors(int[] colors) {
            if (colors == null) {
                this.mGradientColors = null;
            } else {
                ColorStateList[] colorStateListArr = this.mGradientColors;
                if (colorStateListArr == null || colorStateListArr.length != colors.length) {
                    this.mGradientColors = new ColorStateList[colors.length];
                }
                for (int i = 0; i < colors.length; i++) {
                    this.mGradientColors[i] = ColorStateList.valueOf(colors[i]);
                }
            }
            this.mSolidColors = null;
            computeOpacity();
        }

        public void setSolidColors(ColorStateList colors) {
            this.mGradientColors = null;
            this.mSolidColors = colors;
            computeOpacity();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void computeOpacity() {
            boolean z = false;
            this.mOpaqueOverBounds = false;
            this.mOpaqueOverShape = false;
            if (this.mGradientColors != null) {
                int i = 0;
                while (true) {
                    ColorStateList[] colorStateListArr = this.mGradientColors;
                    if (i >= colorStateListArr.length) {
                        break;
                    }
                    ColorStateList colorStateList = colorStateListArr[i];
                    if (colorStateList == null || GradientDrawable.isOpaque(colorStateList.getDefaultColor())) {
                        i++;
                    } else {
                        return;
                    }
                }
            }
            if (this.mGradientColors == null && this.mSolidColors == null) {
                return;
            }
            this.mOpaqueOverShape = true;
            if (this.mShape == 0 && this.mRadius <= 0.0f && this.mRadiusArray == null) {
                z = true;
            }
            this.mOpaqueOverBounds = z;
        }

        public void setStroke(int width, ColorStateList colors, float dashWidth, float dashGap) {
            this.mStrokeWidth = width;
            this.mStrokeColors = colors;
            this.mStrokeDashWidth = dashWidth;
            this.mStrokeDashGap = dashGap;
            computeOpacity();
        }

        public void setCornerRadius(float radius) {
            if (radius < 0.0f) {
                radius = 0.0f;
            }
            this.mRadius = radius;
            this.mRadiusArray = null;
            computeOpacity();
        }

        public void setCornerRadii(float[] radii) {
            this.mRadiusArray = radii;
            if (radii == null) {
                this.mRadius = 0.0f;
            }
            computeOpacity();
        }

        public void setSize(int width, int height) {
            this.mWidth = width;
            this.mHeight = height;
        }

        public void setGradientRadius(float gradientRadius, int type) {
            this.mGradientRadius = gradientRadius;
            this.mGradientRadiusType = type;
        }
    }

    static boolean isOpaque(int color) {
        return ((color >> 24) & 255) == 255;
    }

    private GradientDrawable(GradientState state, Resources res) {
        this.mFillPaint = new Paint(1);
        this.mAlpha = 255;
        this.mPath = new Path();
        this.mRect = new RectF();
        this.mPathIsDirty = true;
        this.mGradientState = state;
        updateLocalState(res);
    }

    private void updateLocalState(Resources res) {
        GradientState state = this.mGradientState;
        if (state.mSolidColors != null) {
            int[] currentState = getState();
            int stateColor = state.mSolidColors.getColorForState(currentState, 0);
            this.mFillPaint.setColor(stateColor);
        } else if (state.mGradientColors == null) {
            this.mFillPaint.setColor(0);
        } else {
            this.mFillPaint.setColor(-16777216);
        }
        this.mPadding = state.mPadding;
        if (state.mStrokeWidth >= 0) {
            Paint paint = new Paint(1);
            this.mStrokePaint = paint;
            paint.setStyle(Paint.Style.STROKE);
            this.mStrokePaint.setStrokeWidth(state.mStrokeWidth);
            if (state.mStrokeColors != null) {
                int[] currentState2 = getState();
                int strokeStateColor = state.mStrokeColors.getColorForState(currentState2, 0);
                this.mStrokePaint.setColor(strokeStateColor);
            }
            if (state.mStrokeDashWidth != 0.0f) {
                DashPathEffect e = new DashPathEffect(new float[]{state.mStrokeDashWidth, state.mStrokeDashGap}, 0.0f);
                this.mStrokePaint.setPathEffect(e);
            }
        }
        this.mBlendModeColorFilter = updateBlendModeFilter(this.mBlendModeColorFilter, state.mTint, state.mBlendMode);
        this.mGradientIsDirty = true;
        state.computeOpacity();
    }
}
