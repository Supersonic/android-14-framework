package com.google.android.collect;

import java.util.ArrayList;
import java.util.Collections;
/* loaded from: classes5.dex */
public class Lists {
    public static <E> ArrayList<E> newArrayList() {
        return new ArrayList<>();
    }

    public static <E> ArrayList<E> newArrayList(E... elements) {
        int capacity = ((elements.length * 110) / 100) + 5;
        ArrayList<E> list = new ArrayList<>(capacity);
        Collections.addAll(list, elements);
        return list;
    }
}
