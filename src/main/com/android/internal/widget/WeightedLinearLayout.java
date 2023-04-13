package com.android.internal.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import com.android.internal.C4057R;
/* loaded from: classes5.dex */
public class WeightedLinearLayout extends LinearLayout {
    private float mMajorWeightMax;
    private float mMajorWeightMin;
    private float mMinorWeightMax;
    private float mMinorWeightMin;

    public WeightedLinearLayout(Context context) {
        super(context);
    }

    public WeightedLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, C4057R.styleable.WeightedLinearLayout);
        this.mMajorWeightMin = a.getFloat(1, 0.0f);
        this.mMinorWeightMin = a.getFloat(3, 0.0f);
        this.mMajorWeightMax = a.getFloat(0, 0.0f);
        this.mMinorWeightMax = a.getFloat(2, 0.0f);
        a.recycle();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        boolean isPortrait = screenWidth < metrics.heightPixels;
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        boolean measure = false;
        int widthMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(width, 1073741824);
        float widthWeightMin = isPortrait ? this.mMinorWeightMin : this.mMajorWeightMin;
        float widthWeightMax = isPortrait ? this.mMinorWeightMax : this.mMajorWeightMax;
        if (widthMode == Integer.MIN_VALUE) {
            int weightedMin = (int) (screenWidth * widthWeightMin);
            int weightedMax = (int) (screenWidth * widthWeightMin);
            if (widthWeightMin > 0.0f && width < weightedMin) {
                widthMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(weightedMin, 1073741824);
                measure = true;
            } else if (widthWeightMax > 0.0f && width > weightedMax) {
                widthMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(weightedMax, 1073741824);
                measure = true;
            }
        }
        if (measure) {
            super.onMeasure(widthMeasureSpec2, heightMeasureSpec);
        }
    }
}
