package com.android.server.permission.jarjar.kotlin.text;

import com.android.server.permission.jarjar.kotlin.jvm.functions.Function1;
import com.android.server.permission.jarjar.kotlin.jvm.internal.Intrinsics;
/* compiled from: Appendable.kt */
/* loaded from: classes2.dex */
public class StringsKt__AppendableKt {
    public static final <T> void appendElement(Appendable appendable, T t, Function1<? super T, ? extends CharSequence> function1) {
        Intrinsics.checkNotNullParameter(appendable, "<this>");
        if (function1 != null) {
            appendable.append(function1.invoke(t));
            return;
        }
        if (t == null ? true : t instanceof CharSequence) {
            appendable.append((CharSequence) t);
        } else if (t instanceof Character) {
            appendable.append(((Character) t).charValue());
        } else {
            appendable.append(String.valueOf(t));
        }
    }
}
