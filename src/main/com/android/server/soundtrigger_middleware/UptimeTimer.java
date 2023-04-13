package com.android.server.soundtrigger_middleware;

import android.os.Handler;
import android.os.HandlerThread;
/* loaded from: classes2.dex */
public class UptimeTimer {
    public final Handler mHandler;
    public final HandlerThread mHandlerThread;

    /* loaded from: classes2.dex */
    public interface Task {
        void cancel();
    }

    public UptimeTimer(String str) {
        HandlerThread handlerThread = new HandlerThread(str);
        this.mHandlerThread = handlerThread;
        handlerThread.start();
        this.mHandler = new Handler(handlerThread.getLooper());
    }

    public Task createTask(Runnable runnable, long j) {
        Object obj = new Object();
        TaskImpl taskImpl = new TaskImpl(this.mHandler, obj);
        this.mHandler.postDelayed(runnable, obj, j);
        return taskImpl;
    }

    public void quit() {
        this.mHandlerThread.quitSafely();
    }

    /* loaded from: classes2.dex */
    public static class TaskImpl implements Task {
        public final Handler mHandler;
        public final Object mToken;

        public TaskImpl(Handler handler, Object obj) {
            this.mHandler = handler;
            this.mToken = obj;
        }

        @Override // com.android.server.soundtrigger_middleware.UptimeTimer.Task
        public void cancel() {
            this.mHandler.removeCallbacksAndMessages(this.mToken);
        }
    }
}
