package com.android.server.display.utils;

import android.content.res.Resources;
import android.util.TypedValue;
import com.android.server.display.utils.AmbientFilter;
/* loaded from: classes.dex */
public class AmbientFilterFactory {
    public static AmbientFilter createAmbientFilter(String str, int i, float f) {
        if (!Float.isNaN(f)) {
            return new AmbientFilter.WeightedMovingAverageAmbientFilter(str, i, f);
        }
        throw new IllegalArgumentException("missing configurations: expected config_displayWhiteBalanceBrightnessFilterIntercept");
    }

    public static AmbientFilter createBrightnessFilter(String str, Resources resources) {
        return createAmbientFilter(str, resources.getInteger(17694815), getFloat(resources, 17105072));
    }

    public static AmbientFilter createColorTemperatureFilter(String str, Resources resources) {
        return createAmbientFilter(str, resources.getInteger(17694818), getFloat(resources, 17105073));
    }

    public static float getFloat(Resources resources, int i) {
        TypedValue typedValue = new TypedValue();
        resources.getValue(i, typedValue, true);
        if (typedValue.type != 4) {
            return Float.NaN;
        }
        return typedValue.getFloat();
    }
}
