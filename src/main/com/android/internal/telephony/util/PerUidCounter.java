package com.android.internal.telephony.util;

import android.util.SparseIntArray;
import com.android.net.module.annotation.GuardedBy;
import com.android.net.module.annotation.VisibleForTesting;
/* loaded from: classes.dex */
public class PerUidCounter {
    private final int mMaxCountPerUid;
    @VisibleForTesting
    @GuardedBy({"this"})
    final SparseIntArray mUidToCount = new SparseIntArray();

    public PerUidCounter(int i) {
        if (i <= 0) {
            throw new IllegalArgumentException("Maximum counter value must be positive");
        }
        this.mMaxCountPerUid = i;
    }

    public synchronized void incrementCountOrThrow(int i) {
        long j = this.mUidToCount.get(i, 0) + 1;
        if (j > this.mMaxCountPerUid) {
            throw new IllegalStateException("Uid " + i + " exceeded its allowed limit");
        }
        this.mUidToCount.put(i, (int) j);
    }

    public synchronized void decrementCountOrThrow(int i) {
        int i2 = this.mUidToCount.get(i, 0) - 1;
        if (i2 < 0) {
            throw new IllegalStateException("BUG: too small count " + i2 + " for UID " + i);
        } else if (i2 == 0) {
            this.mUidToCount.delete(i);
        } else {
            this.mUidToCount.put(i, i2);
        }
    }

    @VisibleForTesting
    public synchronized int get(int i) {
        return this.mUidToCount.get(i, 0);
    }
}
