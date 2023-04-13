package com.android.server.permission.jarjar.kotlin.collections;

import com.android.server.permission.jarjar.kotlin.jvm.internal.Intrinsics;
import java.util.List;
/* compiled from: _ArraysJvm.kt */
/* loaded from: classes2.dex */
public class ArraysKt___ArraysJvmKt extends ArraysKt__ArraysKt {
    public static final <T> List<T> asList(T[] tArr) {
        Intrinsics.checkNotNullParameter(tArr, "<this>");
        List<T> asList = ArraysUtilJVM.asList(tArr);
        Intrinsics.checkNotNullExpressionValue(asList, "asList(this)");
        return asList;
    }
}
