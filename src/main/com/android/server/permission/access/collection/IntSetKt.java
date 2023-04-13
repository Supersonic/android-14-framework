package com.android.server.permission.access.collection;
/* compiled from: IntSet.kt */
/* loaded from: classes2.dex */
public final class IntSetKt {
    public static final IntSet IntSet(int[] iArr) {
        IntSet intSet = new IntSet();
        plusAssign(intSet, iArr);
        return intSet;
    }

    public static final void plusAssign(IntSet intSet, IntSet intSet2) {
        int size = intSet2.getSize();
        for (int i = 0; i < size; i++) {
            intSet.add(intSet2.elementAt(i));
        }
    }

    public static final void plusAssign(IntSet intSet, int[] iArr) {
        for (int i : iArr) {
            intSet.add(i);
        }
    }
}
