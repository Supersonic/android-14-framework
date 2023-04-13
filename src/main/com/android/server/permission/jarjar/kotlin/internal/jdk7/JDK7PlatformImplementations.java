package com.android.server.permission.jarjar.kotlin.internal.jdk7;

import com.android.server.permission.jarjar.kotlin.internal.PlatformImplementations;
import com.android.server.permission.jarjar.kotlin.jvm.internal.Intrinsics;
/* compiled from: JDK7PlatformImplementations.kt */
/* loaded from: classes2.dex */
public class JDK7PlatformImplementations extends PlatformImplementations {

    /* compiled from: JDK7PlatformImplementations.kt */
    /* loaded from: classes2.dex */
    public static final class ReflectSdkVersion {
        public static final ReflectSdkVersion INSTANCE = new ReflectSdkVersion();
        public static final Integer sdkVersion;

        /* JADX WARN: Removed duplicated region for block: B:9:0x0022  */
        static {
            Integer num;
            Object obj;
            Integer num2 = null;
            try {
                obj = Class.forName("android.os.Build$VERSION").getField("SDK_INT").get(null);
            } catch (Throwable unused) {
            }
            if (obj instanceof Integer) {
                num = (Integer) obj;
                if (num != null) {
                    if (num.intValue() > 0) {
                        num2 = num;
                    }
                }
                sdkVersion = num2;
            }
            num = null;
            if (num != null) {
            }
            sdkVersion = num2;
        }
    }

    public final boolean sdkIsNullOrAtLeast(int i) {
        Integer num = ReflectSdkVersion.sdkVersion;
        return num == null || num.intValue() >= i;
    }

    @Override // com.android.server.permission.jarjar.kotlin.internal.PlatformImplementations
    public void addSuppressed(Throwable th, Throwable th2) {
        Intrinsics.checkNotNullParameter(th, "cause");
        Intrinsics.checkNotNullParameter(th2, "exception");
        if (sdkIsNullOrAtLeast(19)) {
            th.addSuppressed(th2);
        } else {
            super.addSuppressed(th, th2);
        }
    }
}
