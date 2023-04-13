package com.android.internal.telephony;

import android.p008os.Handler;
import android.p008os.Looper;
/* loaded from: classes3.dex */
public class ExponentialBackoff {
    private long mCurrentDelayMs;
    private final Handler mHandler;
    private HandlerAdapter mHandlerAdapter;
    private long mMaximumDelayMs;
    private int mMultiplier;
    private int mRetryCounter;
    private final Runnable mRunnable;
    private long mStartDelayMs;

    /* loaded from: classes3.dex */
    public interface HandlerAdapter {
        boolean postDelayed(Runnable runnable, long j);

        void removeCallbacks(Runnable runnable);
    }

    public ExponentialBackoff(long initialDelayMs, long maximumDelayMs, int multiplier, Looper looper, Runnable runnable) {
        this(initialDelayMs, maximumDelayMs, multiplier, new Handler(looper), runnable);
    }

    public ExponentialBackoff(long initialDelayMs, long maximumDelayMs, int multiplier, Handler handler, Runnable runnable) {
        this.mHandlerAdapter = new HandlerAdapter() { // from class: com.android.internal.telephony.ExponentialBackoff.1
            @Override // com.android.internal.telephony.ExponentialBackoff.HandlerAdapter
            public boolean postDelayed(Runnable runnable2, long delayMillis) {
                return ExponentialBackoff.this.mHandler.postDelayed(runnable2, delayMillis);
            }

            @Override // com.android.internal.telephony.ExponentialBackoff.HandlerAdapter
            public void removeCallbacks(Runnable runnable2) {
                ExponentialBackoff.this.mHandler.removeCallbacks(runnable2);
            }
        };
        this.mRetryCounter = 0;
        this.mStartDelayMs = initialDelayMs;
        this.mMaximumDelayMs = maximumDelayMs;
        this.mMultiplier = multiplier;
        this.mHandler = handler;
        this.mRunnable = runnable;
    }

    public void start() {
        this.mRetryCounter = 0;
        this.mCurrentDelayMs = this.mStartDelayMs;
        this.mHandlerAdapter.removeCallbacks(this.mRunnable);
        this.mHandlerAdapter.postDelayed(this.mRunnable, this.mCurrentDelayMs);
    }

    public void stop() {
        this.mRetryCounter = 0;
        this.mHandlerAdapter.removeCallbacks(this.mRunnable);
    }

    public void notifyFailed() {
        int i = this.mRetryCounter + 1;
        this.mRetryCounter = i;
        long temp = Math.min(this.mMaximumDelayMs, (long) (this.mStartDelayMs * Math.pow(this.mMultiplier, i)));
        this.mCurrentDelayMs = (long) (((Math.random() + 1.0d) / 2.0d) * temp);
        this.mHandlerAdapter.removeCallbacks(this.mRunnable);
        this.mHandlerAdapter.postDelayed(this.mRunnable, this.mCurrentDelayMs);
    }

    public long getCurrentDelay() {
        return this.mCurrentDelayMs;
    }

    public void setHandlerAdapter(HandlerAdapter a) {
        this.mHandlerAdapter = a;
    }
}
