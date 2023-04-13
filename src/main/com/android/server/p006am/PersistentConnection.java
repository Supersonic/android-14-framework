package com.android.server.p006am;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.Log;
import android.util.TimeUtils;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import java.io.PrintWriter;
/* renamed from: com.android.server.am.PersistentConnection */
/* loaded from: classes.dex */
public abstract class PersistentConnection<T> {
    @GuardedBy({"mLock"})
    public boolean mBound;
    public final ComponentName mComponentName;
    public final Context mContext;
    public final Handler mHandler;
    @GuardedBy({"mLock"})
    public boolean mIsConnected;
    @GuardedBy({"mLock"})
    public long mLastConnectedTime;
    public long mNextBackoffMs;
    @GuardedBy({"mLock"})
    public int mNumBindingDied;
    @GuardedBy({"mLock"})
    public int mNumConnected;
    @GuardedBy({"mLock"})
    public int mNumDisconnected;
    public final double mRebindBackoffIncrease;
    public final long mRebindBackoffMs;
    public final long mRebindMaxBackoffMs;
    @GuardedBy({"mLock"})
    public boolean mRebindScheduled;
    public long mReconnectTime;
    public final long mResetBackoffDelay;
    @GuardedBy({"mLock"})
    public T mService;
    @GuardedBy({"mLock"})
    public boolean mShouldBeBound;
    public final String mTag;
    public final int mUserId;
    public final Object mLock = new Object();
    public final ServiceConnection mServiceConnection = new ServiceConnection() { // from class: com.android.server.am.PersistentConnection.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            synchronized (PersistentConnection.this.mLock) {
                if (!PersistentConnection.this.mBound) {
                    String str = PersistentConnection.this.mTag;
                    Log.w(str, "Connected: " + PersistentConnection.this.mComponentName.flattenToShortString() + " u" + PersistentConnection.this.mUserId + " but not bound, ignore.");
                    return;
                }
                String str2 = PersistentConnection.this.mTag;
                Log.i(str2, "Connected: " + PersistentConnection.this.mComponentName.flattenToShortString() + " u" + PersistentConnection.this.mUserId);
                PersistentConnection persistentConnection = PersistentConnection.this;
                persistentConnection.mNumConnected = persistentConnection.mNumConnected + 1;
                PersistentConnection.this.mIsConnected = true;
                PersistentConnection persistentConnection2 = PersistentConnection.this;
                persistentConnection2.mLastConnectedTime = persistentConnection2.injectUptimeMillis();
                PersistentConnection persistentConnection3 = PersistentConnection.this;
                persistentConnection3.mService = persistentConnection3.asInterface(iBinder);
                PersistentConnection.this.scheduleStableCheckLocked();
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            synchronized (PersistentConnection.this.mLock) {
                String str = PersistentConnection.this.mTag;
                Log.i(str, "Disconnected: " + PersistentConnection.this.mComponentName.flattenToShortString() + " u" + PersistentConnection.this.mUserId);
                PersistentConnection persistentConnection = PersistentConnection.this;
                persistentConnection.mNumDisconnected = persistentConnection.mNumDisconnected + 1;
                PersistentConnection.this.cleanUpConnectionLocked();
            }
        }

        @Override // android.content.ServiceConnection
        public void onBindingDied(ComponentName componentName) {
            synchronized (PersistentConnection.this.mLock) {
                if (!PersistentConnection.this.mBound) {
                    String str = PersistentConnection.this.mTag;
                    Log.w(str, "Binding died: " + PersistentConnection.this.mComponentName.flattenToShortString() + " u" + PersistentConnection.this.mUserId + " but not bound, ignore.");
                    return;
                }
                String str2 = PersistentConnection.this.mTag;
                Log.w(str2, "Binding died: " + PersistentConnection.this.mComponentName.flattenToShortString() + " u" + PersistentConnection.this.mUserId);
                PersistentConnection persistentConnection = PersistentConnection.this;
                persistentConnection.mNumBindingDied = persistentConnection.mNumBindingDied + 1;
                PersistentConnection.this.scheduleRebindLocked();
            }
        }
    };
    public final Runnable mBindForBackoffRunnable = new Runnable() { // from class: com.android.server.am.PersistentConnection$$ExternalSyntheticLambda0
        @Override // java.lang.Runnable
        public final void run() {
            PersistentConnection.this.lambda$new$0();
        }
    };
    public final Runnable mStableCheck = new Runnable() { // from class: com.android.server.am.PersistentConnection$$ExternalSyntheticLambda1
        @Override // java.lang.Runnable
        public final void run() {
            PersistentConnection.this.stableConnectionCheck();
        }
    };

    public abstract T asInterface(IBinder iBinder);

    public abstract int getBindFlags();

    public PersistentConnection(String str, Context context, Handler handler, int i, ComponentName componentName, long j, double d, long j2, long j3) {
        this.mTag = str;
        this.mContext = context;
        this.mHandler = handler;
        this.mUserId = i;
        this.mComponentName = componentName;
        long j4 = j * 1000;
        this.mRebindBackoffMs = j4;
        this.mRebindBackoffIncrease = d;
        this.mRebindMaxBackoffMs = j2 * 1000;
        this.mResetBackoffDelay = j3 * 1000;
        this.mNextBackoffMs = j4;
    }

    public final ComponentName getComponentName() {
        return this.mComponentName;
    }

    public final int getUserId() {
        return this.mUserId;
    }

    public final boolean isBound() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mBound;
        }
        return z;
    }

    public final boolean isConnected() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mIsConnected;
        }
        return z;
    }

    public final void bind() {
        synchronized (this.mLock) {
            this.mShouldBeBound = true;
            bindInnerLocked(true);
        }
    }

    public long getNextBackoffMs() {
        long j;
        synchronized (this.mLock) {
            j = this.mNextBackoffMs;
        }
        return j;
    }

    public int getNumConnected() {
        int i;
        synchronized (this.mLock) {
            i = this.mNumConnected;
        }
        return i;
    }

    public int getNumDisconnected() {
        int i;
        synchronized (this.mLock) {
            i = this.mNumDisconnected;
        }
        return i;
    }

    public int getNumBindingDied() {
        int i;
        synchronized (this.mLock) {
            i = this.mNumBindingDied;
        }
        return i;
    }

    @GuardedBy({"mLock"})
    public final void resetBackoffLocked() {
        long j = this.mNextBackoffMs;
        long j2 = this.mRebindBackoffMs;
        if (j != j2) {
            this.mNextBackoffMs = j2;
            String str = this.mTag;
            Log.i(str, "Backoff reset to " + this.mNextBackoffMs);
        }
    }

    @GuardedBy({"mLock"})
    public final void bindInnerLocked(boolean z) {
        unscheduleRebindLocked();
        if (this.mBound) {
            return;
        }
        this.mBound = true;
        unscheduleStableCheckLocked();
        if (z) {
            resetBackoffLocked();
        }
        Intent component = new Intent().setComponent(this.mComponentName);
        if (this.mContext.bindServiceAsUser(component, this.mServiceConnection, getBindFlags() | 1, this.mHandler, UserHandle.of(this.mUserId))) {
            return;
        }
        String str = this.mTag;
        Log.e(str, "Binding: " + component.getComponent() + " u" + this.mUserId + " failed.");
    }

    /* renamed from: bindForBackoff */
    public final void lambda$new$0() {
        synchronized (this.mLock) {
            if (this.mShouldBeBound) {
                bindInnerLocked(false);
            }
        }
    }

    @GuardedBy({"mLock"})
    public final void cleanUpConnectionLocked() {
        this.mIsConnected = false;
        this.mService = null;
        unscheduleStableCheckLocked();
    }

    public final void unbind() {
        synchronized (this.mLock) {
            this.mShouldBeBound = false;
            unbindLocked();
            unscheduleStableCheckLocked();
        }
    }

    @GuardedBy({"mLock"})
    public final void unbindLocked() {
        unscheduleRebindLocked();
        if (this.mBound) {
            String str = this.mTag;
            Log.i(str, "Stopping: " + this.mComponentName.flattenToShortString() + " u" + this.mUserId);
            this.mBound = false;
            this.mContext.unbindService(this.mServiceConnection);
            cleanUpConnectionLocked();
        }
    }

    @GuardedBy({"mLock"})
    public void unscheduleRebindLocked() {
        injectRemoveCallbacks(this.mBindForBackoffRunnable);
        this.mRebindScheduled = false;
    }

    @GuardedBy({"mLock"})
    public void scheduleRebindLocked() {
        unbindLocked();
        if (this.mRebindScheduled) {
            return;
        }
        String str = this.mTag;
        Log.i(str, "Scheduling to reconnect in " + this.mNextBackoffMs + " ms (uptime)");
        long injectUptimeMillis = injectUptimeMillis() + this.mNextBackoffMs;
        this.mReconnectTime = injectUptimeMillis;
        injectPostAtTime(this.mBindForBackoffRunnable, injectUptimeMillis);
        this.mNextBackoffMs = Math.min(this.mRebindMaxBackoffMs, (long) (((double) this.mNextBackoffMs) * this.mRebindBackoffIncrease));
        this.mRebindScheduled = true;
    }

    public final void stableConnectionCheck() {
        synchronized (this.mLock) {
            long injectUptimeMillis = (this.mLastConnectedTime + this.mResetBackoffDelay) - injectUptimeMillis();
            if (this.mBound && this.mIsConnected && injectUptimeMillis <= 0) {
                resetBackoffLocked();
            }
        }
    }

    @GuardedBy({"mLock"})
    public final void unscheduleStableCheckLocked() {
        injectRemoveCallbacks(this.mStableCheck);
    }

    @GuardedBy({"mLock"})
    public final void scheduleStableCheckLocked() {
        unscheduleStableCheckLocked();
        injectPostAtTime(this.mStableCheck, injectUptimeMillis() + this.mResetBackoffDelay);
    }

    public void dump(String str, PrintWriter printWriter) {
        synchronized (this.mLock) {
            printWriter.print(str);
            printWriter.print(this.mComponentName.flattenToShortString());
            printWriter.print(" u");
            printWriter.print(this.mUserId);
            printWriter.print(this.mBound ? " [bound]" : " [not bound]");
            printWriter.print(this.mIsConnected ? " [connected]" : " [not connected]");
            if (this.mRebindScheduled) {
                printWriter.print(" reconnect in ");
                TimeUtils.formatDuration(this.mReconnectTime - injectUptimeMillis(), printWriter);
            }
            printWriter.println();
            printWriter.print(str);
            printWriter.print("  Next backoff(sec): ");
            printWriter.print(this.mNextBackoffMs / 1000);
            printWriter.println();
            printWriter.print(str);
            printWriter.print("  Connected: ");
            printWriter.print(this.mNumConnected);
            printWriter.print("  Disconnected: ");
            printWriter.print(this.mNumDisconnected);
            printWriter.print("  Died: ");
            printWriter.print(this.mNumBindingDied);
            if (this.mIsConnected) {
                printWriter.print("  Duration: ");
                TimeUtils.formatDuration(injectUptimeMillis() - this.mLastConnectedTime, printWriter);
            }
            printWriter.println();
        }
    }

    @VisibleForTesting
    public void injectRemoveCallbacks(Runnable runnable) {
        this.mHandler.removeCallbacks(runnable);
    }

    @VisibleForTesting
    public void injectPostAtTime(Runnable runnable, long j) {
        this.mHandler.postAtTime(runnable, j);
    }

    @VisibleForTesting
    public long injectUptimeMillis() {
        return SystemClock.uptimeMillis();
    }

    @VisibleForTesting
    public long getNextBackoffMsForTest() {
        return this.mNextBackoffMs;
    }

    @VisibleForTesting
    public long getReconnectTimeForTest() {
        return this.mReconnectTime;
    }

    @VisibleForTesting
    public ServiceConnection getServiceConnectionForTest() {
        return this.mServiceConnection;
    }

    @VisibleForTesting
    public Runnable getBindForBackoffRunnableForTest() {
        return this.mBindForBackoffRunnable;
    }

    @VisibleForTesting
    public Runnable getStableCheckRunnableForTest() {
        return this.mStableCheck;
    }

    @VisibleForTesting
    public boolean shouldBeBoundForTest() {
        return this.mShouldBeBound;
    }
}
