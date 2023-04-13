package com.android.server.location.contexthub;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class AuthStateDenialTimer {
    public static final long TIMEOUT_MS = TimeUnit.SECONDS.toMillis(60);
    public boolean mCancelled = false;
    public final ContextHubClientBroker mClient;
    public final Handler mHandler;
    public final long mNanoAppId;
    public long mStopTimeInFuture;

    public AuthStateDenialTimer(ContextHubClientBroker contextHubClientBroker, long j, Looper looper) {
        this.mClient = contextHubClientBroker;
        this.mNanoAppId = j;
        this.mHandler = new CountDownHandler(looper);
    }

    public synchronized void cancel() {
        this.mCancelled = true;
        this.mHandler.removeMessages(1);
    }

    public synchronized void start() {
        this.mCancelled = false;
        this.mStopTimeInFuture = SystemClock.elapsedRealtime() + TIMEOUT_MS;
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(1));
    }

    public void onFinish() {
        this.mClient.handleAuthStateTimerExpiry(this.mNanoAppId);
    }

    /* loaded from: classes.dex */
    public class CountDownHandler extends Handler {
        public CountDownHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            synchronized (AuthStateDenialTimer.this) {
                if (AuthStateDenialTimer.this.mCancelled) {
                    return;
                }
                long elapsedRealtime = AuthStateDenialTimer.this.mStopTimeInFuture - SystemClock.elapsedRealtime();
                if (elapsedRealtime <= 0) {
                    AuthStateDenialTimer.this.onFinish();
                } else {
                    sendMessageDelayed(obtainMessage(1), elapsedRealtime);
                }
            }
        }
    }
}
