package com.android.internal.graphics.palette;

import android.graphics.Color;
import android.graphics.ColorSpace;
/* loaded from: classes4.dex */
public class LABPointProvider implements PointProvider {
    final ColorSpace.Connector mRgbToLab = ColorSpace.connect(ColorSpace.get(ColorSpace.Named.SRGB), ColorSpace.get(ColorSpace.Named.CIE_LAB));
    final ColorSpace.Connector mLabToRgb = ColorSpace.connect(ColorSpace.get(ColorSpace.Named.CIE_LAB), ColorSpace.get(ColorSpace.Named.SRGB));

    @Override // com.android.internal.graphics.palette.PointProvider
    public float[] fromInt(int color) {
        float r = Color.red(color) / 255.0f;
        float g = Color.green(color) / 255.0f;
        float b = Color.blue(color) / 255.0f;
        float[] transform = this.mRgbToLab.transform(r, g, b);
        return transform;
    }

    @Override // com.android.internal.graphics.palette.PointProvider
    public int toInt(float[] centroid) {
        float[] rgb = this.mLabToRgb.transform(centroid);
        int color = Color.rgb(rgb[0], rgb[1], rgb[2]);
        return color;
    }

    @Override // com.android.internal.graphics.palette.PointProvider
    public float distance(float[] a, float[] b) {
        double dL = a[0] - b[0];
        double dA = a[1] - b[1];
        double dB = a[2] - b[2];
        return (float) ((dL * dL) + (dA * dA) + (dB * dB));
    }
}
