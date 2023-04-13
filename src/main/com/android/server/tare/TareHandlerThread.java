package com.android.server.tare;

import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.HandlerThread;
import java.util.concurrent.Executor;
/* loaded from: classes2.dex */
public final class TareHandlerThread extends HandlerThread {
    public static Handler sHandler;
    public static Executor sHandlerExecutor;
    public static TareHandlerThread sInstance;

    public TareHandlerThread() {
        super("tare");
    }

    public static void ensureThreadLocked() {
        if (sInstance == null) {
            TareHandlerThread tareHandlerThread = new TareHandlerThread();
            sInstance = tareHandlerThread;
            tareHandlerThread.start();
            sInstance.getLooper().setTraceTag(524288L);
            sHandler = new Handler(sInstance.getLooper());
            sHandlerExecutor = new HandlerExecutor(sHandler);
        }
    }

    public static TareHandlerThread get() {
        synchronized (TareHandlerThread.class) {
            ensureThreadLocked();
        }
        return sInstance;
    }

    public static Executor getExecutor() {
        Executor executor;
        synchronized (TareHandlerThread.class) {
            ensureThreadLocked();
            executor = sHandlerExecutor;
        }
        return executor;
    }

    public static Handler getHandler() {
        synchronized (TareHandlerThread.class) {
            ensureThreadLocked();
        }
        return sHandler;
    }
}
