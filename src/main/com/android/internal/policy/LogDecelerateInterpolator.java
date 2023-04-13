package com.android.internal.policy;

import android.view.animation.Interpolator;
/* loaded from: classes4.dex */
public class LogDecelerateInterpolator implements Interpolator {
    private int mBase;
    private int mDrift;
    private final float mLogScale;

    public LogDecelerateInterpolator(int base, int drift) {
        this.mBase = base;
        this.mDrift = drift;
        this.mLogScale = 1.0f / computeLog(1.0f, base, drift);
    }

    private static float computeLog(float t, int base, int drift) {
        return ((float) (-Math.pow(base, -t))) + 1.0f + (drift * t);
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float t) {
        return computeLog(t, this.mBase, this.mDrift) * this.mLogScale;
    }
}
