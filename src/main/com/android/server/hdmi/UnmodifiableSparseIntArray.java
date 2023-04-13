package com.android.server.hdmi;

import android.util.SparseIntArray;
/* loaded from: classes.dex */
public final class UnmodifiableSparseIntArray {
    public final SparseIntArray mArray;

    public UnmodifiableSparseIntArray(SparseIntArray sparseIntArray) {
        this.mArray = sparseIntArray;
    }

    public int get(int i, int i2) {
        return this.mArray.get(i, i2);
    }

    public String toString() {
        return this.mArray.toString();
    }
}
