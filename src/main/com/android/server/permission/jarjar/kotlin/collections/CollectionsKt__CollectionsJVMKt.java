package com.android.server.permission.jarjar.kotlin.collections;

import com.android.server.permission.jarjar.kotlin.jvm.internal.Intrinsics;
import java.util.Collections;
import java.util.List;
/* compiled from: CollectionsJVM.kt */
/* loaded from: classes2.dex */
public class CollectionsKt__CollectionsJVMKt {
    public static final <T> List<T> listOf(T t) {
        List<T> singletonList = Collections.singletonList(t);
        Intrinsics.checkNotNullExpressionValue(singletonList, "singletonList(element)");
        return singletonList;
    }
}
