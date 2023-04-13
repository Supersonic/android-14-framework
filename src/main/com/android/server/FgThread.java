package com.android.server;

import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.Looper;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public final class FgThread extends ServiceThread {
    public static Handler sHandler;
    public static HandlerExecutor sHandlerExecutor;
    public static FgThread sInstance;

    public FgThread() {
        super("android.fg", 0, true);
    }

    public static void ensureThreadLocked() {
        if (sInstance == null) {
            FgThread fgThread = new FgThread();
            sInstance = fgThread;
            fgThread.start();
            Looper looper = sInstance.getLooper();
            looper.setTraceTag(524288L);
            looper.setSlowLogThresholdMs(100L, 200L);
            sHandler = ServiceThread.makeSharedHandler(sInstance.getLooper());
            sHandlerExecutor = new HandlerExecutor(sHandler);
        }
    }

    public static FgThread get() {
        FgThread fgThread;
        synchronized (FgThread.class) {
            ensureThreadLocked();
            fgThread = sInstance;
        }
        return fgThread;
    }

    public static Handler getHandler() {
        Handler handler;
        synchronized (FgThread.class) {
            ensureThreadLocked();
            handler = sHandler;
        }
        return handler;
    }

    public static Executor getExecutor() {
        HandlerExecutor handlerExecutor;
        synchronized (FgThread.class) {
            ensureThreadLocked();
            handlerExecutor = sHandlerExecutor;
        }
        return handlerExecutor;
    }
}
