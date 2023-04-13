package com.android.server.notification;

import java.util.concurrent.TimeUnit;
/* loaded from: classes2.dex */
public abstract class RankingReconsideration implements Runnable {
    public long mDelay;
    public String mKey;
    public int mState = 0;

    public abstract void applyChangesLocked(NotificationRecord notificationRecord);

    public abstract void work();

    public RankingReconsideration(String str, long j) {
        this.mDelay = j;
        this.mKey = str;
    }

    public String getKey() {
        return this.mKey;
    }

    @Override // java.lang.Runnable
    public void run() {
        if (this.mState == 0) {
            this.mState = 1;
            work();
            this.mState = 2;
            synchronized (this) {
                notifyAll();
            }
        }
    }

    public long getDelay(TimeUnit timeUnit) {
        return timeUnit.convert(this.mDelay, TimeUnit.MILLISECONDS);
    }
}
