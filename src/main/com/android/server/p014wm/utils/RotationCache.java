package com.android.server.p014wm.utils;

import android.util.SparseArray;
/* renamed from: com.android.server.wm.utils.RotationCache */
/* loaded from: classes2.dex */
public class RotationCache<T, R> {
    public final SparseArray<R> mCache = new SparseArray<>(4);
    public T mCachedFor;
    public final RotationDependentComputation<T, R> mComputation;

    @FunctionalInterface
    /* renamed from: com.android.server.wm.utils.RotationCache$RotationDependentComputation */
    /* loaded from: classes2.dex */
    public interface RotationDependentComputation<T, R> {
        R compute(T t, int i);
    }

    public RotationCache(RotationDependentComputation<T, R> rotationDependentComputation) {
        this.mComputation = rotationDependentComputation;
    }

    public R getOrCompute(T t, int i) {
        if (t != this.mCachedFor) {
            this.mCache.clear();
            this.mCachedFor = t;
        }
        int indexOfKey = this.mCache.indexOfKey(i);
        if (indexOfKey >= 0) {
            return this.mCache.valueAt(indexOfKey);
        }
        R compute = this.mComputation.compute(t, i);
        this.mCache.put(i, compute);
        return compute;
    }
}
