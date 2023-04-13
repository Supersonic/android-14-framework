package com.android.internal.widget;

import android.content.Context;
import android.text.BoringLayout;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.view.RemotableViewMethod;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;
@RemoteViews.RemoteView
/* loaded from: classes5.dex */
public class ImageFloatingTextView extends TextView {
    private boolean mHasImage;
    private int mImageEndMargin;
    private int mIndentLines;
    private int mLayoutMaxLines;
    private int mMaxLinesForHeight;
    private int mResolvedDirection;

    public ImageFloatingTextView(Context context) {
        this(context, null);
    }

    public ImageFloatingTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageFloatingTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ImageFloatingTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mIndentLines = 0;
        this.mHasImage = false;
        this.mResolvedDirection = -1;
        this.mMaxLinesForHeight = -1;
        this.mLayoutMaxLines = -1;
    }

    @Override // android.widget.TextView
    protected Layout makeSingleLayout(int wantWidth, BoringLayout.Metrics boring, int ellipsisWidth, Layout.Alignment alignment, boolean shouldEllipsize, TextUtils.TruncateAt effectiveEllipsize, boolean useSaved) {
        int maxLines;
        int i;
        TransformationMethod transformationMethod = getTransformationMethod();
        CharSequence text = getText();
        if (transformationMethod != null) {
            text = transformationMethod.getTransformation(text, this);
        }
        CharSequence text2 = text == null ? "" : text;
        StaticLayout.Builder builder = StaticLayout.Builder.obtain(text2, 0, text2.length(), getPaint(), wantWidth).setAlignment(alignment).setTextDirection(getTextDirectionHeuristic()).setLineSpacing(getLineSpacingExtra(), getLineSpacingMultiplier()).setIncludePad(getIncludeFontPadding()).setUseLineSpacingFromFallbacks(true).setBreakStrategy(1).setHyphenationFrequency(2);
        if (this.mMaxLinesForHeight > 0) {
            maxLines = this.mMaxLinesForHeight;
        } else {
            int maxLines2 = getMaxLines();
            maxLines = maxLines2 >= 0 ? getMaxLines() : Integer.MAX_VALUE;
        }
        builder.setMaxLines(maxLines);
        this.mLayoutMaxLines = maxLines;
        if (shouldEllipsize) {
            builder.setEllipsize(effectiveEllipsize).setEllipsizedWidth(ellipsisWidth);
        }
        int[] margins = null;
        if (this.mHasImage && (i = this.mIndentLines) > 0) {
            margins = new int[i + 1];
            for (int i2 = 0; i2 < this.mIndentLines; i2++) {
                margins[i2] = this.mImageEndMargin;
            }
        }
        int i3 = this.mResolvedDirection;
        if (i3 == 1) {
            builder.setIndents(margins, null);
        } else {
            builder.setIndents(null, margins);
        }
        return builder.build();
    }

    @RemotableViewMethod
    public void setImageEndMargin(int imageEndMargin) {
        if (this.mImageEndMargin != imageEndMargin) {
            this.mImageEndMargin = imageEndMargin;
            invalidateTextIfIndenting();
        }
    }

    @RemotableViewMethod
    public void setImageEndMarginDp(float imageEndMarginDp) {
        setImageEndMargin((int) (getResources().getDisplayMetrics().density * imageEndMarginDp));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.TextView, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int availableHeight = (View.MeasureSpec.getSize(heightMeasureSpec) - this.mPaddingTop) - this.mPaddingBottom;
        if (getLayout() != null && getLayout().getHeight() != availableHeight) {
            this.mMaxLinesForHeight = -1;
            nullLayouts();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Layout layout = getLayout();
        if (layout.getHeight() > availableHeight) {
            int maxLines = layout.getLineCount();
            while (maxLines > 1 && layout.getLineBottom(maxLines - 1) > availableHeight) {
                maxLines--;
            }
            if (getMaxLines() > 0) {
                maxLines = Math.min(getMaxLines(), maxLines);
            }
            if (maxLines != this.mLayoutMaxLines) {
                this.mMaxLinesForHeight = maxLines;
                nullLayouts();
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        }
    }

    @Override // android.widget.TextView, android.view.View
    public void onRtlPropertiesChanged(int layoutDirection) {
        super.onRtlPropertiesChanged(layoutDirection);
        if (layoutDirection != this.mResolvedDirection && isLayoutDirectionResolved()) {
            this.mResolvedDirection = layoutDirection;
            invalidateTextIfIndenting();
        }
    }

    private void invalidateTextIfIndenting() {
        if (this.mHasImage && this.mIndentLines > 0) {
            nullLayouts();
            requestLayout();
        }
    }

    @RemotableViewMethod
    public void setHasImage(boolean hasImage) {
        setHasImageAndNumIndentLines(hasImage, this.mIndentLines);
    }

    @RemotableViewMethod
    public void setNumIndentLines(int lines) {
        setHasImageAndNumIndentLines(this.mHasImage, lines);
    }

    private void setHasImageAndNumIndentLines(boolean hasImage, int lines) {
        int oldEffectiveLines = this.mHasImage ? this.mIndentLines : 0;
        int newEffectiveLines = hasImage ? lines : 0;
        this.mIndentLines = lines;
        this.mHasImage = hasImage;
        if (oldEffectiveLines != newEffectiveLines) {
            nullLayouts();
            requestLayout();
        }
    }
}
