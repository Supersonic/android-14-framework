package com.android.server.people.data;

import com.android.internal.annotations.VisibleForTesting;
/* loaded from: classes2.dex */
public class AppUsageStatsData {
    public int mChosenCount;
    public int mLaunchCount;

    @VisibleForTesting
    public AppUsageStatsData(int i, int i2) {
        this.mChosenCount = i;
        this.mLaunchCount = i2;
    }

    public AppUsageStatsData() {
    }

    public int getLaunchCount() {
        return this.mLaunchCount;
    }

    public void incrementLaunchCountBy(int i) {
        this.mLaunchCount += i;
    }

    public int getChosenCount() {
        return this.mChosenCount;
    }

    public void incrementChosenCountBy(int i) {
        this.mChosenCount += i;
    }
}
