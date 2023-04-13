package com.android.server.p006am;

import android.os.SystemClock;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import java.util.function.BiConsumer;
/* renamed from: com.android.server.am.FgsTempAllowList */
/* loaded from: classes.dex */
public class FgsTempAllowList<E> {
    public final SparseArray<Pair<Long, E>> mTempAllowList = new SparseArray<>();
    public int mMaxSize = 100;
    public final Object mLock = new Object();

    public void add(int i, long j, E e) {
        synchronized (this.mLock) {
            if (j <= 0) {
                Slog.e("ActivityManager", "FgsTempAllowList bad duration:" + j + " key: " + i);
                return;
            }
            long elapsedRealtime = SystemClock.elapsedRealtime();
            int size = this.mTempAllowList.size();
            if (size > this.mMaxSize) {
                Slog.w("ActivityManager", "FgsTempAllowList length:" + size + " exceeds maxSize" + this.mMaxSize);
                for (int i2 = size + (-1); i2 >= 0; i2--) {
                    if (((Long) this.mTempAllowList.valueAt(i2).first).longValue() < elapsedRealtime) {
                        this.mTempAllowList.removeAt(i2);
                    }
                }
            }
            Pair<Long, E> pair = this.mTempAllowList.get(i);
            long j2 = elapsedRealtime + j;
            if (pair == null || ((Long) pair.first).longValue() < j2) {
                this.mTempAllowList.put(i, new Pair<>(Long.valueOf(j2), e));
            }
        }
    }

    public Pair<Long, E> get(int i) {
        synchronized (this.mLock) {
            int indexOfKey = this.mTempAllowList.indexOfKey(i);
            if (indexOfKey < 0) {
                return null;
            }
            if (((Long) this.mTempAllowList.valueAt(indexOfKey).first).longValue() < SystemClock.elapsedRealtime()) {
                this.mTempAllowList.removeAt(indexOfKey);
                return null;
            }
            return this.mTempAllowList.valueAt(indexOfKey);
        }
    }

    public boolean isAllowed(int i) {
        return get(i) != null;
    }

    public void removeUid(int i) {
        synchronized (this.mLock) {
            this.mTempAllowList.remove(i);
        }
    }

    public void forEach(BiConsumer<Integer, Pair<Long, E>> biConsumer) {
        synchronized (this.mLock) {
            for (int i = 0; i < this.mTempAllowList.size(); i++) {
                int keyAt = this.mTempAllowList.keyAt(i);
                Pair<Long, E> valueAt = this.mTempAllowList.valueAt(i);
                if (valueAt != null) {
                    biConsumer.accept(Integer.valueOf(keyAt), valueAt);
                }
            }
        }
    }
}
