package com.android.internal.org.bouncycastle.util;
/* loaded from: classes4.dex */
public interface Selector<T> extends Cloneable {
    Object clone();

    boolean match(T t);
}
