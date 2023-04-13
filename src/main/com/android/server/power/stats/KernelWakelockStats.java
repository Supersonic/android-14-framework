package com.android.server.power.stats;

import java.util.HashMap;
/* loaded from: classes2.dex */
public class KernelWakelockStats extends HashMap<String, Entry> {
    int kernelWakelockVersion;

    /* loaded from: classes2.dex */
    public static class Entry {
        public int mCount;
        public long mTotalTime;
        public int mVersion;

        public Entry(int i, long j, int i2) {
            this.mCount = i;
            this.mTotalTime = j;
            this.mVersion = i2;
        }
    }
}
