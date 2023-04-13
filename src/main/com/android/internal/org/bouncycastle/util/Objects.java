package com.android.internal.org.bouncycastle.util;
/* loaded from: classes4.dex */
public class Objects {
    public static boolean areEqual(Object a, Object b) {
        return a == b || !(a == null || b == null || !a.equals(b));
    }

    public static int hashCode(Object obj) {
        if (obj == null) {
            return 0;
        }
        return obj.hashCode();
    }
}
