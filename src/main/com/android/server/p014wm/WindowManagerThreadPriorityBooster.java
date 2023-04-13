package com.android.server.p014wm;

import android.os.Process;
import com.android.internal.annotations.GuardedBy;
import com.android.server.AnimationThread;
import com.android.server.ThreadPriorityBooster;
/* renamed from: com.android.server.wm.WindowManagerThreadPriorityBooster */
/* loaded from: classes2.dex */
public class WindowManagerThreadPriorityBooster extends ThreadPriorityBooster {
    public final int mAnimationThreadId;
    @GuardedBy({"mLock"})
    public boolean mAppTransitionRunning;
    @GuardedBy({"mLock"})
    public boolean mBoundsAnimationRunning;
    public final Object mLock;
    public final int mSurfaceAnimationThreadId;

    public WindowManagerThreadPriorityBooster() {
        super(-4, 5);
        this.mLock = new Object();
        this.mAnimationThreadId = AnimationThread.get().getThreadId();
        this.mSurfaceAnimationThreadId = SurfaceAnimationThread.get().getThreadId();
    }

    @Override // com.android.server.ThreadPriorityBooster
    public void boost() {
        int myTid = Process.myTid();
        if (myTid == this.mAnimationThreadId || myTid == this.mSurfaceAnimationThreadId) {
            return;
        }
        super.boost();
    }

    @Override // com.android.server.ThreadPriorityBooster
    public void reset() {
        int myTid = Process.myTid();
        if (myTid == this.mAnimationThreadId || myTid == this.mSurfaceAnimationThreadId) {
            return;
        }
        super.reset();
    }

    public void setAppTransitionRunning(boolean z) {
        synchronized (this.mLock) {
            if (this.mAppTransitionRunning != z) {
                this.mAppTransitionRunning = z;
                updatePriorityLocked();
            }
        }
    }

    @GuardedBy({"mLock"})
    public final void updatePriorityLocked() {
        int i = (this.mAppTransitionRunning || this.mBoundsAnimationRunning) ? -10 : -4;
        setBoostToPriority(i);
        Process.setThreadPriority(this.mAnimationThreadId, i);
        Process.setThreadPriority(this.mSurfaceAnimationThreadId, i);
    }
}
