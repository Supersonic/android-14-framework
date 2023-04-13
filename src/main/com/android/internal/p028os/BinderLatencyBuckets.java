package com.android.internal.p028os;

import android.util.Slog;
import java.util.Arrays;
/* renamed from: com.android.internal.os.BinderLatencyBuckets */
/* loaded from: classes4.dex */
public class BinderLatencyBuckets {
    private static final String TAG = "BinderLatencyBuckets";
    private final int[] mBuckets;

    public BinderLatencyBuckets(int bucketCount, int firstBucketSize, float scaleFactor) {
        int[] buffer = new int[bucketCount - 1];
        buffer[0] = firstBucketSize;
        double lastTarget = firstBucketSize;
        for (int i = 1; i < bucketCount - 1; i++) {
            double nextTarget = scaleFactor * lastTarget;
            if (nextTarget > 2.147483647E9d) {
                Slog.m90w(TAG, "Attempted to create a bucket larger than maxint");
                this.mBuckets = Arrays.copyOfRange(buffer, 0, i);
                return;
            }
            if (((int) nextTarget) > buffer[i - 1]) {
                buffer[i] = (int) nextTarget;
            } else {
                buffer[i] = buffer[i - 1] + 1;
            }
            lastTarget = nextTarget;
        }
        this.mBuckets = buffer;
    }

    public int sampleToBucket(int sample) {
        int[] iArr = this.mBuckets;
        if (sample >= iArr[iArr.length - 1]) {
            return iArr.length;
        }
        int searchResult = Arrays.binarySearch(iArr, sample);
        int i = searchResult + 1;
        return searchResult < 0 ? -i : i;
    }

    public int[] getBuckets() {
        return this.mBuckets;
    }
}
