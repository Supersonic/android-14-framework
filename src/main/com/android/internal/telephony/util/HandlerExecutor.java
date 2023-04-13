package com.android.internal.telephony.util;

import android.p008os.Handler;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
/* loaded from: classes3.dex */
public class HandlerExecutor implements Executor {
    private final Handler mHandler;

    public HandlerExecutor(Handler handler) {
        if (handler == null) {
            throw new NullPointerException();
        }
        this.mHandler = handler;
    }

    @Override // java.util.concurrent.Executor
    public void execute(Runnable command) {
        if (!this.mHandler.post(command)) {
            throw new RejectedExecutionException(this.mHandler + " is shutting down");
        }
    }
}
