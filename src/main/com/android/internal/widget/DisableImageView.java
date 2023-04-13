package com.android.internal.widget;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RemoteViews;
@RemoteViews.RemoteView
/* loaded from: classes5.dex */
public class DisableImageView extends ImageView {
    public DisableImageView(Context context) {
        this(context, null, 0, 0);
    }

    public DisableImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

    public DisableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public DisableImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        ColorMatrix brightnessMatrix = new ColorMatrix();
        int brightnessI = (int) (255.0f * 0.5f);
        float scale = 1.0f - 0.5f;
        float[] mat = brightnessMatrix.getArray();
        mat[0] = scale;
        mat[6] = scale;
        mat[12] = scale;
        mat[4] = brightnessI;
        mat[9] = brightnessI;
        mat[14] = brightnessI;
        ColorMatrix filterMatrix = new ColorMatrix();
        filterMatrix.setSaturation(0.0f);
        filterMatrix.preConcat(brightnessMatrix);
        setColorFilter(new ColorMatrixColorFilter(filterMatrix));
    }
}
