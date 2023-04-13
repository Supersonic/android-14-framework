package com.android.server.location.gnss;

import java.util.Arrays;
/* loaded from: classes.dex */
public class GnssPositionMode {
    public final boolean mLowPowerMode;
    public final int mMinInterval;
    public final int mMode;
    public final int mPreferredAccuracy;
    public final int mPreferredTime;
    public final int mRecurrence;

    public GnssPositionMode(int i, int i2, int i3, int i4, int i5, boolean z) {
        this.mMode = i;
        this.mRecurrence = i2;
        this.mMinInterval = i3;
        this.mPreferredAccuracy = i4;
        this.mPreferredTime = i5;
        this.mLowPowerMode = z;
    }

    public boolean equals(Object obj) {
        if (obj instanceof GnssPositionMode) {
            GnssPositionMode gnssPositionMode = (GnssPositionMode) obj;
            return this.mMode == gnssPositionMode.mMode && this.mRecurrence == gnssPositionMode.mRecurrence && this.mMinInterval == gnssPositionMode.mMinInterval && this.mPreferredAccuracy == gnssPositionMode.mPreferredAccuracy && this.mPreferredTime == gnssPositionMode.mPreferredTime && this.mLowPowerMode == gnssPositionMode.mLowPowerMode && getClass() == gnssPositionMode.getClass();
        }
        return false;
    }

    public int hashCode() {
        return Arrays.hashCode(new Object[]{Integer.valueOf(this.mMode), Integer.valueOf(this.mRecurrence), Integer.valueOf(this.mMinInterval), Integer.valueOf(this.mPreferredAccuracy), Integer.valueOf(this.mPreferredTime), Boolean.valueOf(this.mLowPowerMode), getClass()});
    }
}
