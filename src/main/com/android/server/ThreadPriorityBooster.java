package com.android.server;

import android.os.Process;
/* loaded from: classes.dex */
public class ThreadPriorityBooster {
    public volatile int mBoostToPriority;
    public final int mLockGuardIndex;
    public final ThreadLocal<PriorityState> mThreadState = new ThreadLocal<PriorityState>() { // from class: com.android.server.ThreadPriorityBooster.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.lang.ThreadLocal
        public PriorityState initialValue() {
            return new PriorityState();
        }
    };

    public ThreadPriorityBooster(int i, int i2) {
        this.mBoostToPriority = i;
        this.mLockGuardIndex = i2;
    }

    public void boost() {
        int threadPriority;
        PriorityState priorityState = this.mThreadState.get();
        if (priorityState.regionCounter == 0 && (threadPriority = Process.getThreadPriority(priorityState.tid)) > this.mBoostToPriority) {
            Process.setThreadPriority(priorityState.tid, this.mBoostToPriority);
            priorityState.prevPriority = threadPriority;
        }
        priorityState.regionCounter++;
    }

    public void reset() {
        int i;
        PriorityState priorityState = this.mThreadState.get();
        int i2 = priorityState.regionCounter - 1;
        priorityState.regionCounter = i2;
        if (i2 != 0 || (i = priorityState.prevPriority) == Integer.MAX_VALUE) {
            return;
        }
        Process.setThreadPriority(priorityState.tid, i);
        priorityState.prevPriority = Integer.MAX_VALUE;
    }

    public void setBoostToPriority(int i) {
        this.mBoostToPriority = i;
        PriorityState priorityState = this.mThreadState.get();
        if (priorityState.regionCounter == 0 || Process.getThreadPriority(priorityState.tid) == i) {
            return;
        }
        Process.setThreadPriority(priorityState.tid, i);
    }

    /* loaded from: classes.dex */
    public static class PriorityState {
        public int prevPriority;
        public int regionCounter;
        public final int tid;

        public PriorityState() {
            this.tid = Process.myTid();
            this.prevPriority = Integer.MAX_VALUE;
        }
    }
}
