package com.android.internal.p028os;

import android.p008os.Handler;
import android.p008os.HandlerExecutor;
import android.p008os.HandlerThread;
import android.p008os.Looper;
import java.util.concurrent.Executor;
/* renamed from: com.android.internal.os.BackgroundThread */
/* loaded from: classes4.dex */
public final class BackgroundThread extends HandlerThread {
    private static final long SLOW_DELIVERY_THRESHOLD_MS = 30000;
    private static final long SLOW_DISPATCH_THRESHOLD_MS = 10000;
    private static Handler sHandler;
    private static HandlerExecutor sHandlerExecutor;
    private static BackgroundThread sInstance;

    private BackgroundThread() {
        super("android.bg", 10);
    }

    private static void ensureThreadLocked() {
        if (sInstance == null) {
            BackgroundThread backgroundThread = new BackgroundThread();
            sInstance = backgroundThread;
            backgroundThread.start();
            Looper looper = sInstance.getLooper();
            looper.setTraceTag(524288L);
            looper.setSlowLogThresholdMs(10000L, 30000L);
            Handler handler = new Handler(sInstance.getLooper(), null, false, true);
            sHandler = handler;
            sHandlerExecutor = new HandlerExecutor(handler);
        }
    }

    public static BackgroundThread get() {
        BackgroundThread backgroundThread;
        synchronized (BackgroundThread.class) {
            ensureThreadLocked();
            backgroundThread = sInstance;
        }
        return backgroundThread;
    }

    public static Handler getHandler() {
        Handler handler;
        synchronized (BackgroundThread.class) {
            ensureThreadLocked();
            handler = sHandler;
        }
        return handler;
    }

    public static Executor getExecutor() {
        HandlerExecutor handlerExecutor;
        synchronized (BackgroundThread.class) {
            ensureThreadLocked();
            handlerExecutor = sHandlerExecutor;
        }
        return handlerExecutor;
    }
}
