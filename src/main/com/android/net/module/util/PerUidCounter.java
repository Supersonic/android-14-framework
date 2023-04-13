package com.android.net.module.util;

import android.util.SparseIntArray;
/* loaded from: classes5.dex */
public class PerUidCounter {
    private final int mMaxCountPerUid;
    final SparseIntArray mUidToCount = new SparseIntArray();

    public PerUidCounter(int maxCountPerUid) {
        if (maxCountPerUid <= 0) {
            throw new IllegalArgumentException("Maximum counter value must be positive");
        }
        this.mMaxCountPerUid = maxCountPerUid;
    }

    public synchronized void incrementCountOrThrow(int uid) {
        long newCount = this.mUidToCount.get(uid, 0) + 1;
        if (newCount > this.mMaxCountPerUid) {
            throw new IllegalStateException("Uid " + uid + " exceeded its allowed limit");
        }
        this.mUidToCount.put(uid, (int) newCount);
    }

    public synchronized void decrementCountOrThrow(int uid) {
        int newCount = this.mUidToCount.get(uid, 0) - 1;
        if (newCount < 0) {
            throw new IllegalStateException("BUG: too small count " + newCount + " for UID " + uid);
        }
        if (newCount == 0) {
            this.mUidToCount.delete(uid);
        } else {
            this.mUidToCount.put(uid, newCount);
        }
    }

    public synchronized int get(int uid) {
        return this.mUidToCount.get(uid, 0);
    }
}
