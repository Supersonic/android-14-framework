package com.android.server.autofill.p007ui;

import android.content.Context;
import android.graphics.Point;
import android.provider.DeviceConfig;
import android.util.AttributeSet;
import android.util.Slog;
import android.util.TypedValue;
import android.view.View;
import android.widget.ScrollView;
import com.android.server.autofill.Helper;
/* renamed from: com.android.server.autofill.ui.CustomScrollView */
/* loaded from: classes.dex */
public class CustomScrollView extends ScrollView {
    public int mAttrBasedMaxHeightPercent;
    public int mHeight;
    public int mMaxLandscapeBodyHeightPercent;
    public int mMaxPortraitBodyHeightPercent;
    public int mWidth;

    public CustomScrollView(Context context) {
        super(context);
        this.mWidth = -1;
        this.mHeight = -1;
        setMaxBodyHeightPercent(context);
    }

    public CustomScrollView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mWidth = -1;
        this.mHeight = -1;
        setMaxBodyHeightPercent(context);
    }

    public CustomScrollView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mWidth = -1;
        this.mHeight = -1;
        setMaxBodyHeightPercent(context);
    }

    public CustomScrollView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mWidth = -1;
        this.mHeight = -1;
        setMaxBodyHeightPercent(context);
    }

    public final void setMaxBodyHeightPercent(Context context) {
        int attrBasedMaxHeightPercent = getAttrBasedMaxHeightPercent(context);
        this.mAttrBasedMaxHeightPercent = attrBasedMaxHeightPercent;
        this.mMaxPortraitBodyHeightPercent = DeviceConfig.getInt("autofill", "autofill_save_dialog_portrait_body_height_max_percent", attrBasedMaxHeightPercent);
        this.mMaxLandscapeBodyHeightPercent = DeviceConfig.getInt("autofill", "autofill_save_dialog_landscape_body_height_max_percent", this.mAttrBasedMaxHeightPercent);
    }

    @Override // android.widget.ScrollView, android.widget.FrameLayout, android.view.View
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (getChildCount() == 0) {
            Slog.e("CustomScrollView", "no children");
            return;
        }
        this.mWidth = View.MeasureSpec.getSize(i);
        calculateDimensions();
        setMeasuredDimension(this.mWidth, this.mHeight);
    }

    public final void calculateDimensions() {
        int i;
        if (this.mHeight != -1) {
            return;
        }
        Point point = new Point();
        getContext().getDisplayNoVerify().getSize(point);
        int measuredHeight = getChildAt(0).getMeasuredHeight();
        int i2 = point.y;
        if (getResources().getConfiguration().orientation == 2) {
            i = (this.mMaxLandscapeBodyHeightPercent * i2) / 100;
        } else {
            i = (this.mMaxPortraitBodyHeightPercent * i2) / 100;
        }
        this.mHeight = Math.min(measuredHeight, i);
        if (Helper.sDebug) {
            Slog.d("CustomScrollView", "calculateDimensions(): mMaxPortraitBodyHeightPercent=" + this.mMaxPortraitBodyHeightPercent + ", mMaxLandscapeBodyHeightPercent=" + this.mMaxLandscapeBodyHeightPercent + ", mAttrBasedMaxHeightPercent=" + this.mAttrBasedMaxHeightPercent + ", maxHeight=" + i + ", contentHeight=" + measuredHeight + ", w=" + this.mWidth + ", h=" + this.mHeight);
        }
    }

    public final int getAttrBasedMaxHeightPercent(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(17956885, typedValue, true);
        return (int) typedValue.getFraction(100.0f, 100.0f);
    }
}
