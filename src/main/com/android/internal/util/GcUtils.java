package com.android.internal.util;

import android.util.Slog;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
/* loaded from: classes3.dex */
public final class GcUtils {
    private static final String TAG = GcUtils.class.getSimpleName();

    public static void runGcAndFinalizersSync() {
        Runtime.getRuntime().gc();
        Runtime.getRuntime().runFinalization();
        CountDownLatch fence = new CountDownLatch(1);
        createFinalizationObserver(fence);
        do {
            try {
                Runtime.getRuntime().gc();
                Runtime.getRuntime().runFinalization();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        } while (!fence.await(100L, TimeUnit.MILLISECONDS));
        Slog.m92v(TAG, "Running gc and finalizers");
    }

    private static void createFinalizationObserver(final CountDownLatch fence) {
        new Object() { // from class: com.android.internal.util.GcUtils.1
            protected void finalize() throws Throwable {
                try {
                    fence.countDown();
                } finally {
                    super.finalize();
                }
            }
        };
    }

    private GcUtils() {
    }
}
