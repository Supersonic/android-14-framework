package com.android.commands.monkey;

import android.app.IActivityManager;
import android.view.IWindowManager;
/* loaded from: classes.dex */
public class MonkeyThrottleEvent extends MonkeyEvent {
    private long mThrottle;

    public MonkeyThrottleEvent(long throttle) {
        super(6);
        this.mThrottle = throttle;
    }

    @Override // com.android.commands.monkey.MonkeyEvent
    public int injectEvent(IWindowManager iwm, IActivityManager iam, int verbose) {
        if (verbose > 1) {
            Logger.out.println("Sleeping for " + this.mThrottle + " milliseconds");
        }
        try {
            Thread.sleep(this.mThrottle);
            return 1;
        } catch (InterruptedException e) {
            Logger.out.println("** Monkey interrupted in sleep.");
            return 0;
        }
    }
}
