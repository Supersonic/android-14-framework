package com.android.server.permission.jarjar.kotlin;

import com.android.server.permission.jarjar.kotlin.jvm.internal.Intrinsics;
import java.io.Serializable;
/* compiled from: Tuples.kt */
/* loaded from: classes2.dex */
public final class Pair<A, B> implements Serializable {
    private final A first;
    private final B second;

    public final A component1() {
        return this.first;
    }

    public final B component2() {
        return this.second;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Pair) {
            Pair pair = (Pair) obj;
            return Intrinsics.areEqual(this.first, pair.first) && Intrinsics.areEqual(this.second, pair.second);
        }
        return false;
    }

    public int hashCode() {
        A a = this.first;
        int hashCode = (a == null ? 0 : a.hashCode()) * 31;
        B b = this.second;
        return hashCode + (b != null ? b.hashCode() : 0);
    }

    public Pair(A a, B b) {
        this.first = a;
        this.second = b;
    }

    public String toString() {
        return '(' + this.first + ", " + this.second + ')';
    }
}
