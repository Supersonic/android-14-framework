package com.android.server;

import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.Looper;
import com.android.internal.annotations.GuardedBy;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public final class PermissionThread extends ServiceThread {
    public static Handler sHandler;
    public static HandlerExecutor sHandlerExecutor;
    @GuardedBy({"sLock"})
    public static PermissionThread sInstance;
    public static final Object sLock = new Object();

    public PermissionThread() {
        super("android.perm", 0, true);
    }

    @GuardedBy({"sLock"})
    public static void ensureThreadLocked() {
        if (sInstance != null) {
            return;
        }
        PermissionThread permissionThread = new PermissionThread();
        sInstance = permissionThread;
        permissionThread.start();
        Looper looper = sInstance.getLooper();
        looper.setTraceTag(524288L);
        looper.setSlowLogThresholdMs(100L, 200L);
        sHandler = new Handler(sInstance.getLooper());
        sHandlerExecutor = new HandlerExecutor(sHandler);
    }

    public static Handler getHandler() {
        Handler handler;
        synchronized (sLock) {
            ensureThreadLocked();
            handler = sHandler;
        }
        return handler;
    }

    public static Executor getExecutor() {
        HandlerExecutor handlerExecutor;
        synchronized (sLock) {
            ensureThreadLocked();
            handlerExecutor = sHandlerExecutor;
        }
        return handlerExecutor;
    }
}
