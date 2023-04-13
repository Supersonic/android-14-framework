package com.android.server.p011pm.utils;

import android.os.Handler;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
/* renamed from: com.android.server.pm.utils.RequestThrottle */
/* loaded from: classes2.dex */
public class RequestThrottle {
    public final int mBackoffBase;
    public final Supplier<Boolean> mBlock;
    public final AtomicInteger mCurrentRetry;
    public final int mFirstDelay;
    public final Handler mHandler;
    public final AtomicInteger mLastCommitted;
    public final AtomicInteger mLastRequest;
    public final int mMaxAttempts;
    public final Runnable mRunnable;

    public RequestThrottle(Handler handler, Supplier<Boolean> supplier) {
        this(handler, 5, 1000, 2, supplier);
    }

    public RequestThrottle(Handler handler, int i, int i2, int i3, Supplier<Boolean> supplier) {
        this.mLastRequest = new AtomicInteger(0);
        this.mLastCommitted = new AtomicInteger(-1);
        this.mCurrentRetry = new AtomicInteger(0);
        this.mHandler = handler;
        this.mBlock = supplier;
        this.mMaxAttempts = i;
        this.mFirstDelay = i2;
        this.mBackoffBase = i3;
        this.mRunnable = new Runnable() { // from class: com.android.server.pm.utils.RequestThrottle$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                RequestThrottle.this.runInternal();
            }
        };
    }

    public void schedule() {
        this.mLastRequest.incrementAndGet();
        this.mHandler.post(this.mRunnable);
    }

    public boolean runNow() {
        this.mLastRequest.incrementAndGet();
        return runInternal();
    }

    public final boolean runInternal() {
        int i = this.mLastRequest.get();
        if (i == this.mLastCommitted.get()) {
            return true;
        }
        if (this.mBlock.get().booleanValue()) {
            this.mCurrentRetry.set(0);
            this.mLastCommitted.set(i);
            return true;
        }
        int andIncrement = this.mCurrentRetry.getAndIncrement();
        if (andIncrement < this.mMaxAttempts) {
            this.mHandler.postDelayed(this.mRunnable, (long) (this.mFirstDelay * Math.pow(this.mBackoffBase, andIncrement)));
        } else {
            this.mCurrentRetry.set(0);
        }
        return false;
    }
}
