package com.android.location.provider;

import android.annotation.SystemApi;
@SystemApi
@Deprecated
/* loaded from: classes.dex */
public class GmsFusedBatchOptions {

    /* loaded from: classes.dex */
    public static final class BatchFlags {
        public static int WAKEUP_ON_FIFO_FULL = 1;
        public static int CALLBACK_ON_LOCATION_FIX = 2;
    }

    /* loaded from: classes.dex */
    public static final class SourceTechnologies {
        public static int GNSS = 1;
        public static int WIFI = 2;
        public static int SENSORS = 4;
        public static int CELL = 8;
        public static int BLUETOOTH = 16;
    }

    public void setMaxPowerAllocationInMW(double value) {
    }

    public double getMaxPowerAllocationInMW() {
        return 0.0d;
    }

    public void setPeriodInNS(long value) {
    }

    public long getPeriodInNS() {
        return 0L;
    }

    public void setSmallestDisplacementMeters(float value) {
    }

    public float getSmallestDisplacementMeters() {
        return 0.0f;
    }

    public void setSourceToUse(int source) {
    }

    public void resetSourceToUse(int source) {
    }

    public boolean isSourceToUseSet(int source) {
        return false;
    }

    public int getSourcesToUse() {
        return 0;
    }

    public void setFlag(int flag) {
    }

    public void resetFlag(int flag) {
    }

    public boolean isFlagSet(int flag) {
        return false;
    }

    public int getFlags() {
        return 0;
    }
}
