package com.android.server.backup.utils;

import android.util.SparseArray;
import java.util.HashSet;
/* loaded from: classes.dex */
public final class SparseArrayUtils {
    public static <V> HashSet<V> union(SparseArray<HashSet<V>> sparseArray) {
        HashSet<V> hashSet = new HashSet<>();
        int size = sparseArray.size();
        for (int i = 0; i < size; i++) {
            HashSet<V> valueAt = sparseArray.valueAt(i);
            if (valueAt != null) {
                hashSet.addAll(valueAt);
            }
        }
        return hashSet;
    }
}
