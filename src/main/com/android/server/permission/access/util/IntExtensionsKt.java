package com.android.server.permission.access.util;
/* compiled from: IntExtensions.kt */
/* loaded from: classes2.dex */
public final class IntExtensionsKt {
    public static final int andInv(int i, int i2) {
        return i & (~i2);
    }

    public static final boolean hasAnyBit(int i, int i2) {
        return (i & i2) != 0;
    }

    public static final boolean hasBits(int i, int i2) {
        return (i & i2) == i2;
    }
}
