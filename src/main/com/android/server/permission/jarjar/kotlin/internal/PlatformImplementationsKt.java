package com.android.server.permission.jarjar.kotlin.internal;

import com.android.server.permission.jarjar.kotlin.internal.jdk7.JDK7PlatformImplementations;
import com.android.server.permission.jarjar.kotlin.internal.jdk8.JDK8PlatformImplementations;
import com.android.server.permission.jarjar.kotlin.jvm.internal.Intrinsics;
/* compiled from: PlatformImplementations.kt */
/* loaded from: classes2.dex */
public final class PlatformImplementationsKt {
    public static final PlatformImplementations IMPLEMENTATIONS;

    static {
        PlatformImplementations platformImplementations;
        Object newInstance;
        try {
            newInstance = JDK8PlatformImplementations.class.newInstance();
            Intrinsics.checkNotNullExpressionValue(newInstance, "forName(\"kotlin.internal…entations\").newInstance()");
            try {
                try {
                } catch (ClassNotFoundException unused) {
                    Object newInstance2 = JDK7PlatformImplementations.class.newInstance();
                    Intrinsics.checkNotNullExpressionValue(newInstance2, "forName(\"kotlin.internal…entations\").newInstance()");
                    try {
                        try {
                            if (newInstance2 == null) {
                                throw new NullPointerException("null cannot be cast to non-null type kotlin.internal.PlatformImplementations");
                            }
                            platformImplementations = (PlatformImplementations) newInstance2;
                        } catch (ClassCastException e) {
                            ClassLoader classLoader = newInstance2.getClass().getClassLoader();
                            ClassLoader classLoader2 = PlatformImplementations.class.getClassLoader();
                            if (Intrinsics.areEqual(classLoader, classLoader2)) {
                                throw e;
                            }
                            throw new ClassNotFoundException("Instance class was loaded from a different classloader: " + classLoader + ", base type classloader: " + classLoader2, e);
                        }
                    } catch (ClassNotFoundException unused2) {
                        platformImplementations = new PlatformImplementations();
                    }
                }
            } catch (ClassCastException e2) {
                ClassLoader classLoader3 = newInstance.getClass().getClassLoader();
                ClassLoader classLoader4 = PlatformImplementations.class.getClassLoader();
                if (Intrinsics.areEqual(classLoader3, classLoader4)) {
                    throw e2;
                }
                throw new ClassNotFoundException("Instance class was loaded from a different classloader: " + classLoader3 + ", base type classloader: " + classLoader4, e2);
            }
        } catch (ClassNotFoundException unused3) {
            Object newInstance3 = Class.forName("com.android.server.permission.jarjar.kotlin.internal.JRE8PlatformImplementations").newInstance();
            Intrinsics.checkNotNullExpressionValue(newInstance3, "forName(\"kotlin.internal…entations\").newInstance()");
            try {
                try {
                    if (newInstance3 == null) {
                        throw new NullPointerException("null cannot be cast to non-null type kotlin.internal.PlatformImplementations");
                    }
                    platformImplementations = (PlatformImplementations) newInstance3;
                } catch (ClassCastException e3) {
                    ClassLoader classLoader5 = newInstance3.getClass().getClassLoader();
                    ClassLoader classLoader6 = PlatformImplementations.class.getClassLoader();
                    if (Intrinsics.areEqual(classLoader5, classLoader6)) {
                        throw e3;
                    }
                    throw new ClassNotFoundException("Instance class was loaded from a different classloader: " + classLoader5 + ", base type classloader: " + classLoader6, e3);
                }
            } catch (ClassNotFoundException unused4) {
                Object newInstance4 = Class.forName("com.android.server.permission.jarjar.kotlin.internal.JRE7PlatformImplementations").newInstance();
                Intrinsics.checkNotNullExpressionValue(newInstance4, "forName(\"kotlin.internal…entations\").newInstance()");
                try {
                    if (newInstance4 == null) {
                        throw new NullPointerException("null cannot be cast to non-null type kotlin.internal.PlatformImplementations");
                    }
                    platformImplementations = (PlatformImplementations) newInstance4;
                } catch (ClassCastException e4) {
                    ClassLoader classLoader7 = newInstance4.getClass().getClassLoader();
                    ClassLoader classLoader8 = PlatformImplementations.class.getClassLoader();
                    if (Intrinsics.areEqual(classLoader7, classLoader8)) {
                        throw e4;
                    }
                    throw new ClassNotFoundException("Instance class was loaded from a different classloader: " + classLoader7 + ", base type classloader: " + classLoader8, e4);
                }
            }
        }
        if (newInstance == null) {
            throw new NullPointerException("null cannot be cast to non-null type kotlin.internal.PlatformImplementations");
        }
        platformImplementations = (PlatformImplementations) newInstance;
        IMPLEMENTATIONS = platformImplementations;
    }
}
