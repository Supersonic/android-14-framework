package com.android.server;

import android.os.Handler;
import com.android.internal.annotations.VisibleForTesting;
/* loaded from: classes.dex */
public final class DisplayThread extends ServiceThread {
    public static Handler sHandler;
    public static DisplayThread sInstance;

    public DisplayThread() {
        super("android.display", -3, false);
    }

    public static void ensureThreadLocked() {
        if (sInstance == null) {
            DisplayThread displayThread = new DisplayThread();
            sInstance = displayThread;
            displayThread.start();
            sInstance.getLooper().setTraceTag(524288L);
            sHandler = ServiceThread.makeSharedHandler(sInstance.getLooper());
        }
    }

    public static DisplayThread get() {
        DisplayThread displayThread;
        synchronized (DisplayThread.class) {
            ensureThreadLocked();
            displayThread = sInstance;
        }
        return displayThread;
    }

    public static Handler getHandler() {
        Handler handler;
        synchronized (DisplayThread.class) {
            ensureThreadLocked();
            handler = sHandler;
        }
        return handler;
    }

    @VisibleForTesting
    public static void dispose() {
        synchronized (DisplayThread.class) {
            if (sInstance == null) {
                return;
            }
            getHandler().runWithScissors(new Runnable() { // from class: com.android.server.DisplayThread$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    DisplayThread.lambda$dispose$0();
                }
            }, 0L);
            sInstance = null;
        }
    }

    public static /* synthetic */ void lambda$dispose$0() {
        sInstance.quit();
    }
}
