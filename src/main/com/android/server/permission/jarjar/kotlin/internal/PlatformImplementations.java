package com.android.server.permission.jarjar.kotlin.internal;

import com.android.server.permission.jarjar.kotlin.collections.ArraysKt___ArraysKt;
import com.android.server.permission.jarjar.kotlin.jvm.internal.Intrinsics;
import java.lang.reflect.Method;
/* compiled from: PlatformImplementations.kt */
/* loaded from: classes2.dex */
public class PlatformImplementations {

    /* compiled from: PlatformImplementations.kt */
    /* loaded from: classes2.dex */
    public static final class ReflectThrowable {
        public static final ReflectThrowable INSTANCE = new ReflectThrowable();
        public static final Method addSuppressed;
        public static final Method getSuppressed;

        /* JADX WARN: Removed duplicated region for block: B:13:0x0040 A[LOOP:0: B:3:0x0016->B:13:0x0040, LOOP_END] */
        /* JADX WARN: Removed duplicated region for block: B:24:0x0044 A[EDGE_INSN: B:24:0x0044->B:15:0x0044 ?: BREAK  , SYNTHETIC] */
        static {
            Method method;
            Method method2;
            boolean z;
            Method[] methods = Throwable.class.getMethods();
            Intrinsics.checkNotNullExpressionValue(methods, "throwableMethods");
            int length = methods.length;
            int i = 0;
            int i2 = 0;
            while (true) {
                method = null;
                if (i2 >= length) {
                    method2 = null;
                    break;
                }
                method2 = methods[i2];
                if (Intrinsics.areEqual(method2.getName(), "addSuppressed")) {
                    Class<?>[] parameterTypes = method2.getParameterTypes();
                    Intrinsics.checkNotNullExpressionValue(parameterTypes, "it.parameterTypes");
                    if (Intrinsics.areEqual(ArraysKt___ArraysKt.singleOrNull(parameterTypes), Throwable.class)) {
                        z = true;
                        if (!z) {
                            break;
                        }
                        i2++;
                    }
                }
                z = false;
                if (!z) {
                }
            }
            addSuppressed = method2;
            int length2 = methods.length;
            while (true) {
                if (i >= length2) {
                    break;
                }
                Method method3 = methods[i];
                if (Intrinsics.areEqual(method3.getName(), "getSuppressed")) {
                    method = method3;
                    break;
                }
                i++;
            }
            getSuppressed = method;
        }
    }

    public void addSuppressed(Throwable th, Throwable th2) {
        Intrinsics.checkNotNullParameter(th, "cause");
        Intrinsics.checkNotNullParameter(th2, "exception");
        Method method = ReflectThrowable.addSuppressed;
        if (method != null) {
            method.invoke(th, th2);
        }
    }
}
