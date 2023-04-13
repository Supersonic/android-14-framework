package com.android.server.timezonedetector;

import android.util.ArraySet;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
/* loaded from: classes2.dex */
public class OrdinalGenerator<T> {
    public final Function<T, T> mCanonicalizationFunction;
    public final ArraySet<T> mKnownIds = new ArraySet<>();

    public OrdinalGenerator(Function<T, T> function) {
        Objects.requireNonNull(function);
        this.mCanonicalizationFunction = function;
    }

    public int ordinal(T t) {
        T apply = this.mCanonicalizationFunction.apply(t);
        int indexOf = this.mKnownIds.indexOf(apply);
        if (indexOf < 0) {
            int size = this.mKnownIds.size();
            this.mKnownIds.add(apply);
            return size;
        }
        return indexOf;
    }

    public int[] ordinals(List<T> list) {
        int size = list.size();
        int[] iArr = new int[size];
        for (int i = 0; i < size; i++) {
            iArr[i] = ordinal(list.get(i));
        }
        return iArr;
    }
}
