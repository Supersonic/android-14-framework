package com.android.internal.util;

import android.p008os.Debug;
import android.p008os.StrictMode;
/* loaded from: classes3.dex */
public final class MemInfoReader {
    final long[] mInfos = new long[26];

    public void readMemInfo() {
        StrictMode.ThreadPolicy savedPolicy = StrictMode.allowThreadDiskReads();
        try {
            Debug.getMemInfo(this.mInfos);
        } finally {
            StrictMode.setThreadPolicy(savedPolicy);
        }
    }

    public long getTotalSize() {
        return this.mInfos[0] * 1024;
    }

    public long getFreeSize() {
        return this.mInfos[1] * 1024;
    }

    public long getCachedSize() {
        return getCachedSizeKb() * 1024;
    }

    public long getKernelUsedSize() {
        return getKernelUsedSizeKb() * 1024;
    }

    public long getTotalSizeKb() {
        return this.mInfos[0];
    }

    public long getFreeSizeKb() {
        return this.mInfos[1];
    }

    public long getCachedSizeKb() {
        long[] jArr = this.mInfos;
        long kReclaimable = jArr[15];
        if (kReclaimable == 0) {
            kReclaimable = jArr[6];
        }
        return ((jArr[2] + kReclaimable) + jArr[3]) - jArr[11];
    }

    public long getKernelUsedSizeKb() {
        long[] jArr = this.mInfos;
        long size = jArr[4] + jArr[7] + jArr[12] + jArr[13];
        if (!Debug.isVmapStack()) {
            return size + this.mInfos[14];
        }
        return size;
    }

    public long getSwapTotalSizeKb() {
        return this.mInfos[8];
    }

    public long getSwapFreeSizeKb() {
        return this.mInfos[9];
    }

    public long getZramTotalSizeKb() {
        return this.mInfos[10];
    }

    public long[] getRawInfo() {
        return this.mInfos;
    }
}
