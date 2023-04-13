package com.android.server.display;

import android.util.MathUtils;
/* loaded from: classes.dex */
public class BrightnessUtils {
    public static final float convertGammaToLinear(float f) {
        float exp;
        if (f <= 0.5f) {
            exp = MathUtils.sq(f / 0.5f);
        } else {
            exp = MathUtils.exp((f - 0.5599107f) / 0.17883277f) + 0.28466892f;
        }
        return MathUtils.constrain(exp, 0.0f, 12.0f) / 12.0f;
    }

    public static final float convertLinearToGamma(float f) {
        float f2 = f * 12.0f;
        if (f2 <= 1.0f) {
            return MathUtils.sqrt(f2) * 0.5f;
        }
        return (MathUtils.log(f2 - 0.28466892f) * 0.17883277f) + 0.5599107f;
    }
}
