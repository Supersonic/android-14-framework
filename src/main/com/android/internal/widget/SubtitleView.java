package com.android.internal.widget;

import android.C0001R;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.CaptioningManager;
import com.android.internal.C4057R;
/* loaded from: classes5.dex */
public class SubtitleView extends View {
    private static final int COLOR_BEVEL_DARK = Integer.MIN_VALUE;
    private static final int COLOR_BEVEL_LIGHT = -2130706433;
    private static final float INNER_PADDING_RATIO = 0.125f;
    private Layout.Alignment mAlignment;
    private int mBackgroundColor;
    private final float mCornerRadius;
    private int mEdgeColor;
    private int mEdgeType;
    private int mForegroundColor;
    private boolean mHasMeasurements;
    private int mInnerPaddingX;
    private int mLastMeasuredWidth;
    private StaticLayout mLayout;
    private final RectF mLineBounds;
    private final float mOutlineWidth;
    private Paint mPaint;
    private final float mShadowOffsetX;
    private final float mShadowOffsetY;
    private final float mShadowRadius;
    private float mSpacingAdd;
    private float mSpacingMult;
    private final SpannableStringBuilder mText;
    private TextPaint mTextPaint;

    public SubtitleView(Context context) {
        this(context, null);
    }

    public SubtitleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SubtitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SubtitleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs);
        this.mLineBounds = new RectF();
        this.mText = new SpannableStringBuilder();
        this.mAlignment = Layout.Alignment.ALIGN_CENTER;
        this.mSpacingMult = 1.0f;
        this.mSpacingAdd = 0.0f;
        this.mInnerPaddingX = 0;
        TypedArray a = context.obtainStyledAttributes(attrs, C0001R.styleable.TextView, defStyleAttr, defStyleRes);
        CharSequence text = "";
        int textSize = 15;
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case 0:
                    textSize = a.getDimensionPixelSize(attr, textSize);
                    break;
                case 18:
                    text = a.getText(attr);
                    break;
                case 53:
                    this.mSpacingAdd = a.getDimensionPixelSize(attr, (int) this.mSpacingAdd);
                    break;
                case 54:
                    this.mSpacingMult = a.getFloat(attr, this.mSpacingMult);
                    break;
            }
        }
        a.recycle();
        Resources res = getContext().getResources();
        this.mCornerRadius = res.getDimensionPixelSize(C4057R.dimen.subtitle_corner_radius);
        this.mOutlineWidth = res.getDimensionPixelSize(C4057R.dimen.subtitle_outline_width);
        this.mShadowRadius = res.getDimensionPixelSize(C4057R.dimen.subtitle_shadow_radius);
        float dimensionPixelSize = res.getDimensionPixelSize(C4057R.dimen.subtitle_shadow_offset);
        this.mShadowOffsetX = dimensionPixelSize;
        this.mShadowOffsetY = dimensionPixelSize;
        TextPaint textPaint = new TextPaint();
        this.mTextPaint = textPaint;
        textPaint.setAntiAlias(true);
        this.mTextPaint.setSubpixelText(true);
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setAntiAlias(true);
        setText(text);
        setTextSize(textSize);
    }

    public void setText(int resId) {
        CharSequence text = getContext().getText(resId);
        setText(text);
    }

    public void setText(CharSequence text) {
        this.mText.clear();
        this.mText.append(text);
        this.mHasMeasurements = false;
        requestLayout();
        invalidate();
    }

    public void setForegroundColor(int color) {
        this.mForegroundColor = color;
        invalidate();
    }

    @Override // android.view.View
    public void setBackgroundColor(int color) {
        this.mBackgroundColor = color;
        invalidate();
    }

    public void setEdgeType(int edgeType) {
        this.mEdgeType = edgeType;
        invalidate();
    }

    public void setEdgeColor(int color) {
        this.mEdgeColor = color;
        invalidate();
    }

    public void setTextSize(float size) {
        if (this.mTextPaint.getTextSize() != size) {
            this.mTextPaint.setTextSize(size);
            this.mInnerPaddingX = (int) ((INNER_PADDING_RATIO * size) + 0.5f);
            this.mHasMeasurements = false;
            requestLayout();
            invalidate();
        }
    }

    public void setTypeface(Typeface typeface) {
        if (this.mTextPaint.getTypeface() != typeface) {
            this.mTextPaint.setTypeface(typeface);
            this.mHasMeasurements = false;
            requestLayout();
            invalidate();
        }
    }

    public void setAlignment(Layout.Alignment textAlignment) {
        if (this.mAlignment != textAlignment) {
            this.mAlignment = textAlignment;
            this.mHasMeasurements = false;
            requestLayout();
            invalidate();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpec = View.MeasureSpec.getSize(widthMeasureSpec);
        if (computeMeasurements(widthSpec)) {
            StaticLayout layout = this.mLayout;
            int paddingX = this.mPaddingLeft + this.mPaddingRight + (this.mInnerPaddingX * 2);
            int width = layout.getWidth() + paddingX;
            int height = layout.getHeight() + this.mPaddingTop + this.mPaddingBottom;
            setMeasuredDimension(width, height);
            return;
        }
        setMeasuredDimension(16777216, 16777216);
    }

    @Override // android.view.View
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = r - l;
        computeMeasurements(width);
    }

    private boolean computeMeasurements(int maxWidth) {
        if (this.mHasMeasurements && maxWidth == this.mLastMeasuredWidth) {
            return true;
        }
        int paddingX = this.mPaddingLeft + this.mPaddingRight + (this.mInnerPaddingX * 2);
        int maxWidth2 = maxWidth - paddingX;
        if (maxWidth2 <= 0) {
            return false;
        }
        this.mHasMeasurements = true;
        this.mLastMeasuredWidth = maxWidth2;
        SpannableStringBuilder spannableStringBuilder = this.mText;
        this.mLayout = StaticLayout.Builder.obtain(spannableStringBuilder, 0, spannableStringBuilder.length(), this.mTextPaint, maxWidth2).setAlignment(this.mAlignment).setLineSpacing(this.mSpacingAdd, this.mSpacingMult).setUseLineSpacingFromFallbacks(true).build();
        return true;
    }

    public void setStyle(int styleId) {
        CaptioningManager.CaptionStyle style;
        Context context = this.mContext;
        ContentResolver cr = context.getContentResolver();
        if (styleId == -1) {
            style = CaptioningManager.CaptionStyle.getCustomStyle(cr);
        } else {
            style = CaptioningManager.CaptionStyle.PRESETS[styleId];
        }
        CaptioningManager.CaptionStyle defStyle = CaptioningManager.CaptionStyle.DEFAULT;
        this.mForegroundColor = style.hasForegroundColor() ? style.foregroundColor : defStyle.foregroundColor;
        this.mBackgroundColor = style.hasBackgroundColor() ? style.backgroundColor : defStyle.backgroundColor;
        this.mEdgeType = style.hasEdgeType() ? style.edgeType : defStyle.edgeType;
        this.mEdgeColor = style.hasEdgeColor() ? style.edgeColor : defStyle.edgeColor;
        this.mHasMeasurements = false;
        Typeface typeface = style.getTypeface();
        setTypeface(typeface);
        requestLayout();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDraw(Canvas c) {
        int colorUp;
        StaticLayout layout = this.mLayout;
        if (layout == null) {
            return;
        }
        int saveCount = c.save();
        int innerPaddingX = this.mInnerPaddingX;
        c.translate(this.mPaddingLeft + innerPaddingX, this.mPaddingTop);
        int lineCount = layout.getLineCount();
        Paint textPaint = this.mTextPaint;
        Paint paint = this.mPaint;
        RectF bounds = this.mLineBounds;
        if (Color.alpha(this.mBackgroundColor) > 0) {
            float cornerRadius = this.mCornerRadius;
            float previousBottom = layout.getLineTop(0);
            paint.setColor(this.mBackgroundColor);
            paint.setStyle(Paint.Style.FILL);
            for (int i = 0; i < lineCount; i++) {
                bounds.left = layout.getLineLeft(i) - innerPaddingX;
                bounds.right = layout.getLineRight(i) + innerPaddingX;
                bounds.top = previousBottom;
                bounds.bottom = layout.getLineBottom(i);
                previousBottom = bounds.bottom;
                c.drawRoundRect(bounds, cornerRadius, cornerRadius, paint);
            }
        }
        int edgeType = this.mEdgeType;
        if (edgeType == 1) {
            textPaint.setStrokeJoin(Paint.Join.ROUND);
            textPaint.setStrokeWidth(this.mOutlineWidth);
            textPaint.setColor(this.mEdgeColor);
            textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            for (int i2 = 0; i2 < lineCount; i2++) {
                layout.drawText(c, i2, i2);
            }
        } else if (edgeType == 2) {
            textPaint.setShadowLayer(this.mShadowRadius, this.mShadowOffsetX, this.mShadowOffsetY, this.mEdgeColor);
        } else if (edgeType == 3 || edgeType == 4) {
            boolean raised = edgeType == 3;
            int colorDown = -1;
            if (!raised) {
                colorUp = this.mEdgeColor;
            } else {
                colorUp = -1;
            }
            if (raised) {
                colorDown = this.mEdgeColor;
            }
            float offset = this.mShadowRadius / 2.0f;
            textPaint.setColor(this.mForegroundColor);
            textPaint.setStyle(Paint.Style.FILL);
            textPaint.setShadowLayer(this.mShadowRadius, -offset, -offset, colorUp);
            for (int i3 = 0; i3 < lineCount; i3++) {
                layout.drawText(c, i3, i3);
            }
            textPaint.setShadowLayer(this.mShadowRadius, offset, offset, colorDown);
        }
        textPaint.setColor(this.mForegroundColor);
        textPaint.setStyle(Paint.Style.FILL);
        for (int i4 = 0; i4 < lineCount; i4++) {
            layout.drawText(c, i4, i4);
        }
        textPaint.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
        c.restoreToCount(saveCount);
    }
}
