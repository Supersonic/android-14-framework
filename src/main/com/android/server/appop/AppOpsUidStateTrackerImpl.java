package com.android.server.appop;

import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.AppOpsManager;
import android.os.Handler;
import android.util.ArrayMap;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import android.util.TimeUtils;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.os.Clock;
import com.android.internal.util.function.HeptConsumer;
import com.android.internal.util.function.QuadConsumer;
import com.android.internal.util.function.QuintConsumer;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.appop.AppOpsService;
import com.android.server.appop.AppOpsUidStateTracker;
import com.android.server.appop.AppOpsUidStateTrackerImpl;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
/* loaded from: classes.dex */
public class AppOpsUidStateTrackerImpl implements AppOpsUidStateTracker {
    public ActivityManagerInternal mActivityManagerInternal;
    public SparseBooleanArray mAppWidgetVisible;
    public SparseIntArray mCapability;
    public final Clock mClock;
    public AppOpsService.Constants mConstants;
    public final EventLog mEventLog;
    public final DelayableExecutor mExecutor;
    public SparseBooleanArray mPendingAppWidgetVisible;
    public SparseIntArray mPendingCapability;
    public SparseLongArray mPendingCommitTime;
    public SparseBooleanArray mPendingGone;
    public SparseIntArray mPendingUidStates;
    public ArrayMap<AppOpsUidStateTracker.UidStateChangedCallback, Executor> mUidStateChangedCallbacks;
    public SparseIntArray mUidStates;

    @VisibleForTesting
    /* loaded from: classes.dex */
    public interface DelayableExecutor extends Executor {
        @Override // java.util.concurrent.Executor
        void execute(Runnable runnable);

        void executeDelayed(Runnable runnable, long j);
    }

    public final int getOpCapability(int i) {
        if (i == 0 || i == 1) {
            return 1;
        }
        if (i != 26) {
            if (i != 27) {
                if (i == 41 || i == 42) {
                    return 1;
                }
                return i != 121 ? 0 : 4;
            }
            return 4;
        }
        return 2;
    }

    /* renamed from: com.android.server.appop.AppOpsUidStateTrackerImpl$1 */
    /* loaded from: classes.dex */
    public class C04831 implements DelayableExecutor {
        public final /* synthetic */ Handler val$handler;
        public final /* synthetic */ Executor val$lockingExecutor;

        public C04831(Handler handler, Executor executor) {
            this.val$handler = handler;
            this.val$lockingExecutor = executor;
        }

        @Override // com.android.server.appop.AppOpsUidStateTrackerImpl.DelayableExecutor, java.util.concurrent.Executor
        public void execute(final Runnable runnable) {
            Handler handler = this.val$handler;
            final Executor executor = this.val$lockingExecutor;
            handler.post(new Runnable() { // from class: com.android.server.appop.AppOpsUidStateTrackerImpl$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    executor.execute(runnable);
                }
            });
        }

        @Override // com.android.server.appop.AppOpsUidStateTrackerImpl.DelayableExecutor
        public void executeDelayed(final Runnable runnable, long j) {
            Handler handler = this.val$handler;
            final Executor executor = this.val$lockingExecutor;
            handler.postDelayed(new Runnable() { // from class: com.android.server.appop.AppOpsUidStateTrackerImpl$1$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    executor.execute(runnable);
                }
            }, j);
        }
    }

    public AppOpsUidStateTrackerImpl(ActivityManagerInternal activityManagerInternal, Handler handler, Executor executor, Clock clock, AppOpsService.Constants constants) {
        this(activityManagerInternal, new C04831(handler, executor), clock, constants, handler.getLooper().getThread());
    }

    @VisibleForTesting
    public AppOpsUidStateTrackerImpl(ActivityManagerInternal activityManagerInternal, DelayableExecutor delayableExecutor, Clock clock, AppOpsService.Constants constants, Thread thread) {
        this.mUidStates = new SparseIntArray();
        this.mPendingUidStates = new SparseIntArray();
        this.mCapability = new SparseIntArray();
        this.mPendingCapability = new SparseIntArray();
        this.mAppWidgetVisible = new SparseBooleanArray();
        this.mPendingAppWidgetVisible = new SparseBooleanArray();
        this.mPendingCommitTime = new SparseLongArray();
        this.mPendingGone = new SparseBooleanArray();
        this.mUidStateChangedCallbacks = new ArrayMap<>();
        this.mActivityManagerInternal = activityManagerInternal;
        this.mExecutor = delayableExecutor;
        this.mClock = clock;
        this.mConstants = constants;
        this.mEventLog = new EventLog(delayableExecutor, thread);
    }

    @Override // com.android.server.appop.AppOpsUidStateTracker
    public int getUidState(int i) {
        return getUidStateLocked(i);
    }

    public final int getUidStateLocked(int i) {
        updateUidPendingStateIfNeeded(i);
        return this.mUidStates.get(i, 700);
    }

    @Override // com.android.server.appop.AppOpsUidStateTracker
    public int evalMode(int i, int i2, int i3) {
        if (i3 != 4) {
            return i3;
        }
        int uidState = getUidState(i);
        int uidCapability = getUidCapability(i);
        int evalModeInternal = evalModeInternal(i, i2, uidState, uidCapability);
        this.mEventLog.logEvalForegroundMode(i, uidState, uidCapability, i2, evalModeInternal);
        return evalModeInternal;
    }

    public final int evalModeInternal(int i, int i2, int i3, int i4) {
        if (!getUidAppWidgetVisible(i) && !this.mActivityManagerInternal.isPendingTopUid(i) && !this.mActivityManagerInternal.isTempAllowlistedForFgsWhileInUse(i)) {
            int opCapability = getOpCapability(i2);
            if (opCapability != 0) {
                return (opCapability & i4) == 0 ? 1 : 0;
            } else if (i3 > AppOpsManager.resolveFirstUnrestrictedUidState(i2)) {
                return 1;
            }
        }
        return 0;
    }

    @Override // com.android.server.appop.AppOpsUidStateTracker
    public boolean isUidInForeground(int i) {
        return evalMode(i, -1, 4) == 0;
    }

    @Override // com.android.server.appop.AppOpsUidStateTracker
    public void addUidStateChangedCallback(Executor executor, AppOpsUidStateTracker.UidStateChangedCallback uidStateChangedCallback) {
        if (this.mUidStateChangedCallbacks.containsKey(uidStateChangedCallback)) {
            throw new IllegalStateException("Callback is already registered.");
        }
        this.mUidStateChangedCallbacks.put(uidStateChangedCallback, executor);
    }

    @Override // com.android.server.appop.AppOpsUidStateTracker
    public void updateAppWidgetVisibility(SparseArray<String> sparseArray, boolean z) {
        int size = sparseArray.size();
        for (int i = 0; i < size; i++) {
            int keyAt = sparseArray.keyAt(i);
            this.mPendingAppWidgetVisible.put(keyAt, z);
            commitUidPendingState(keyAt);
        }
    }

    @Override // com.android.server.appop.AppOpsUidStateTracker
    public void updateUidProcState(int i, int i2, int i3) {
        long j;
        int processStateToUidState = AppOpsUidStateTracker.processStateToUidState(i2);
        int i4 = this.mUidStates.get(i, 700);
        int i5 = this.mCapability.get(i, 0);
        int i6 = this.mPendingUidStates.get(i, 700);
        int i7 = this.mPendingCapability.get(i, 0);
        int i8 = (this.mPendingCommitTime.get(i, 0L) > 0L ? 1 : (this.mPendingCommitTime.get(i, 0L) == 0L ? 0 : -1));
        if (i8 != 0 || (processStateToUidState == i4 && i3 == i5)) {
            if (i8 == 0) {
                return;
            }
            if (processStateToUidState == i6 && i3 == i7) {
                return;
            }
        }
        this.mEventLog.logUpdateUidProcState(i, i2, i3);
        this.mPendingUidStates.put(i, processStateToUidState);
        this.mPendingCapability.put(i, i3);
        if (i2 == 20) {
            this.mPendingGone.put(i, true);
            commitUidPendingState(i);
        } else if (processStateToUidState < i4 || (processStateToUidState <= 500 && i4 > 500)) {
            commitUidPendingState(i);
        } else if (processStateToUidState == i4 && i3 != i5) {
            commitUidPendingState(i);
        } else if (processStateToUidState <= 500) {
            commitUidPendingState(i);
        } else if (i8 == 0) {
            if (i4 <= 200) {
                j = this.mConstants.TOP_STATE_SETTLE_TIME;
            } else if (i4 <= 400) {
                j = this.mConstants.FG_SERVICE_STATE_SETTLE_TIME;
            } else {
                j = this.mConstants.BG_STATE_SETTLE_TIME;
            }
            this.mPendingCommitTime.put(i, this.mClock.elapsedRealtime() + j);
            this.mExecutor.executeDelayed(PooledLambda.obtainRunnable(new BiConsumer() { // from class: com.android.server.appop.AppOpsUidStateTrackerImpl$$ExternalSyntheticLambda0
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    ((AppOpsUidStateTrackerImpl) obj).updateUidPendingStateIfNeeded(((Integer) obj2).intValue());
                }
            }, this, Integer.valueOf(i)), j + 1);
        }
    }

    @Override // com.android.server.appop.AppOpsUidStateTracker
    public void dumpUidState(PrintWriter printWriter, int i, long j) {
        int i2 = this.mUidStates.get(i, 700);
        int i3 = this.mPendingUidStates.get(i, i2);
        printWriter.print("    state=");
        printWriter.println(AppOpsManager.getUidStateName(i2));
        if (i2 != i3) {
            printWriter.print("    pendingState=");
            printWriter.println(AppOpsManager.getUidStateName(i3));
        }
        int i4 = this.mCapability.get(i, 0);
        int i5 = this.mPendingCapability.get(i, i4);
        printWriter.print("    capability=");
        ActivityManager.printCapabilitiesFull(printWriter, i4);
        printWriter.println();
        if (i4 != i5) {
            printWriter.print("    pendingCapability=");
            ActivityManager.printCapabilitiesFull(printWriter, i5);
            printWriter.println();
        }
        boolean z = this.mAppWidgetVisible.get(i, false);
        boolean z2 = this.mPendingAppWidgetVisible.get(i, z);
        printWriter.print("    appWidgetVisible=");
        printWriter.println(z);
        if (z != z2) {
            printWriter.print("    pendingAppWidgetVisible=");
            printWriter.println(z2);
        }
        long j2 = this.mPendingCommitTime.get(i, 0L);
        if (j2 != 0) {
            printWriter.print("    pendingStateCommitTime=");
            TimeUtils.formatDuration(j2, j, printWriter);
            printWriter.println();
        }
    }

    @Override // com.android.server.appop.AppOpsUidStateTracker
    public void dumpEvents(PrintWriter printWriter) {
        this.mEventLog.dumpEvents(printWriter);
    }

    public final void updateUidPendingStateIfNeeded(int i) {
        updateUidPendingStateIfNeededLocked(i);
    }

    public final void updateUidPendingStateIfNeededLocked(int i) {
        if (this.mPendingCommitTime.get(i, 0L) == 0 || this.mClock.elapsedRealtime() < this.mPendingCommitTime.get(i)) {
            return;
        }
        commitUidPendingState(i);
    }

    public final void commitUidPendingState(int i) {
        int i2 = this.mPendingUidStates.get(i, this.mUidStates.get(i, 700));
        int i3 = this.mPendingCapability.get(i, this.mCapability.get(i, 0));
        boolean z = this.mPendingAppWidgetVisible.get(i, this.mAppWidgetVisible.get(i, false));
        int i4 = this.mUidStates.get(i, 700);
        int i5 = this.mCapability.get(i, 0);
        boolean z2 = this.mAppWidgetVisible.get(i, false);
        if (i4 != i2 || i5 != i3 || z2 != z) {
            boolean z3 = ((i4 <= 500) == (i2 <= 500) && i5 == i3 && z2 == z) ? false : true;
            if (z3) {
                this.mEventLog.logCommitUidState(i, i2, i3, z, z2 != z);
            }
            for (int i6 = 0; i6 < this.mUidStateChangedCallbacks.size(); i6++) {
                this.mUidStateChangedCallbacks.valueAt(i6).execute(PooledLambda.obtainRunnable(new QuadConsumer() { // from class: com.android.server.appop.AppOpsUidStateTrackerImpl$$ExternalSyntheticLambda1
                    public final void accept(Object obj, Object obj2, Object obj3, Object obj4) {
                        ((AppOpsUidStateTracker.UidStateChangedCallback) obj).onUidStateChanged(((Integer) obj2).intValue(), ((Integer) obj3).intValue(), ((Boolean) obj4).booleanValue());
                    }
                }, this.mUidStateChangedCallbacks.keyAt(i6), Integer.valueOf(i), Integer.valueOf(i2), Boolean.valueOf(z3)));
            }
        }
        if (this.mPendingGone.get(i, false)) {
            this.mUidStates.delete(i);
            this.mCapability.delete(i);
            this.mAppWidgetVisible.delete(i);
            this.mPendingGone.delete(i);
        } else {
            this.mUidStates.put(i, i2);
            this.mCapability.put(i, i3);
            this.mAppWidgetVisible.put(i, z);
        }
        this.mPendingUidStates.delete(i);
        this.mPendingCapability.delete(i);
        this.mPendingAppWidgetVisible.delete(i);
        this.mPendingCommitTime.delete(i);
    }

    public final int getUidCapability(int i) {
        return this.mCapability.get(i, 0);
    }

    public final boolean getUidAppWidgetVisible(int i) {
        return this.mAppWidgetVisible.get(i, false);
    }

    /* loaded from: classes.dex */
    public static class EventLog {
        public int[][] mCommitUidStateLog;
        public int mCommitUidStateLogHead;
        public int mCommitUidStateLogSize;
        public long[] mCommitUidStateLogTimestamps;
        public int[][] mEvalForegroundModeLog;
        public int mEvalForegroundModeLogHead;
        public int mEvalForegroundModeLogSize;
        public long[] mEvalForegroundModeLogTimestamps;
        public final DelayableExecutor mExecutor;
        public final Thread mExecutorThread;
        public int[][] mUpdateUidProcStateLog;
        public int mUpdateUidProcStateLogHead;
        public int mUpdateUidProcStateLogSize;
        public long[] mUpdateUidProcStateLogTimestamps;

        public EventLog(DelayableExecutor delayableExecutor, Thread thread) {
            Class cls = Integer.TYPE;
            this.mUpdateUidProcStateLog = (int[][]) Array.newInstance(cls, 200, 3);
            this.mUpdateUidProcStateLogTimestamps = new long[200];
            this.mUpdateUidProcStateLogSize = 0;
            this.mUpdateUidProcStateLogHead = 0;
            this.mCommitUidStateLog = (int[][]) Array.newInstance(cls, 200, 4);
            this.mCommitUidStateLogTimestamps = new long[200];
            this.mCommitUidStateLogSize = 0;
            this.mCommitUidStateLogHead = 0;
            this.mEvalForegroundModeLog = (int[][]) Array.newInstance(cls, 200, 5);
            this.mEvalForegroundModeLogTimestamps = new long[200];
            this.mEvalForegroundModeLogSize = 0;
            this.mEvalForegroundModeLogHead = 0;
            this.mExecutor = delayableExecutor;
            this.mExecutorThread = thread;
        }

        public void logUpdateUidProcState(int i, int i2, int i3) {
            this.mExecutor.execute(PooledLambda.obtainRunnable(new QuintConsumer() { // from class: com.android.server.appop.AppOpsUidStateTrackerImpl$EventLog$$ExternalSyntheticLambda1
                public final void accept(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                    ((AppOpsUidStateTrackerImpl.EventLog) obj).logUpdateUidProcStateAsync(((Long) obj2).longValue(), ((Integer) obj3).intValue(), ((Integer) obj4).intValue(), ((Integer) obj5).intValue());
                }
            }, this, Long.valueOf(System.currentTimeMillis()), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3)));
        }

        public void logUpdateUidProcStateAsync(long j, int i, int i2, int i3) {
            int i4 = this.mUpdateUidProcStateLogHead;
            int i5 = this.mUpdateUidProcStateLogSize;
            int i6 = (i4 + i5) % 200;
            if (i5 == 200) {
                this.mUpdateUidProcStateLogHead = (i4 + 1) % 200;
            } else {
                this.mUpdateUidProcStateLogSize = i5 + 1;
            }
            int[] iArr = this.mUpdateUidProcStateLog[i6];
            iArr[0] = i;
            iArr[1] = i2;
            iArr[2] = i3;
            this.mUpdateUidProcStateLogTimestamps[i6] = j;
        }

        public void logCommitUidState(int i, int i2, int i3, boolean z, boolean z2) {
            this.mExecutor.execute(PooledLambda.obtainRunnable(new HeptConsumer() { // from class: com.android.server.appop.AppOpsUidStateTrackerImpl$EventLog$$ExternalSyntheticLambda2
                public final void accept(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7) {
                    ((AppOpsUidStateTrackerImpl.EventLog) obj).logCommitUidStateAsync(((Long) obj2).longValue(), ((Integer) obj3).intValue(), ((Integer) obj4).intValue(), ((Integer) obj5).intValue(), ((Boolean) obj6).booleanValue(), ((Boolean) obj7).booleanValue());
                }
            }, this, Long.valueOf(System.currentTimeMillis()), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Boolean.valueOf(z), Boolean.valueOf(z2)));
        }

        public void logCommitUidStateAsync(long j, int i, int i2, int i3, boolean z, boolean z2) {
            int i4 = this.mCommitUidStateLogHead;
            int i5 = this.mCommitUidStateLogSize;
            int i6 = (i4 + i5) % 200;
            if (i5 == 200) {
                this.mCommitUidStateLogHead = (i4 + 1) % 200;
            } else {
                this.mCommitUidStateLogSize = i5 + 1;
            }
            int[] iArr = this.mCommitUidStateLog[i6];
            iArr[0] = i;
            iArr[1] = i2;
            iArr[2] = i3;
            iArr[3] = 0;
            if (z) {
                iArr[3] = 0 + 1;
            }
            if (z2) {
                iArr[3] = iArr[3] + 2;
            }
            this.mCommitUidStateLogTimestamps[i6] = j;
        }

        public void logEvalForegroundMode(int i, int i2, int i3, int i4, int i5) {
            this.mExecutor.execute(PooledLambda.obtainRunnable(new HeptConsumer() { // from class: com.android.server.appop.AppOpsUidStateTrackerImpl$EventLog$$ExternalSyntheticLambda0
                public final void accept(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7) {
                    ((AppOpsUidStateTrackerImpl.EventLog) obj).logEvalForegroundModeAsync(((Long) obj2).longValue(), ((Integer) obj3).intValue(), ((Integer) obj4).intValue(), ((Integer) obj5).intValue(), ((Integer) obj6).intValue(), ((Integer) obj7).intValue());
                }
            }, this, Long.valueOf(System.currentTimeMillis()), Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4), Integer.valueOf(i5)));
        }

        public void logEvalForegroundModeAsync(long j, int i, int i2, int i3, int i4, int i5) {
            int i6 = this.mEvalForegroundModeLogHead;
            int i7 = this.mEvalForegroundModeLogSize;
            int i8 = (i6 + i7) % 200;
            if (i7 == 200) {
                this.mEvalForegroundModeLogHead = (i6 + 1) % 200;
            } else {
                this.mEvalForegroundModeLogSize = i7 + 1;
            }
            int[] iArr = this.mEvalForegroundModeLog[i8];
            iArr[0] = i;
            iArr[1] = i2;
            iArr[2] = i3;
            iArr[3] = i4;
            iArr[4] = i5;
            this.mEvalForegroundModeLogTimestamps[i8] = j;
        }

        public void dumpEvents(PrintWriter printWriter) {
            int i = 0;
            int i2 = 0;
            int i3 = 0;
            while (true) {
                int i4 = this.mUpdateUidProcStateLogSize;
                if (i >= i4 && i2 >= this.mCommitUidStateLogSize && i3 >= this.mEvalForegroundModeLogSize) {
                    return;
                }
                int i5 = (this.mUpdateUidProcStateLogHead + i) % 200;
                int i6 = (this.mCommitUidStateLogHead + i2) % 200;
                int i7 = (this.mEvalForegroundModeLogHead + i3) % 200;
                long j = i < i4 ? this.mUpdateUidProcStateLogTimestamps[i5] : Long.MAX_VALUE;
                long j2 = i2 < this.mCommitUidStateLogSize ? this.mCommitUidStateLogTimestamps[i6] : Long.MAX_VALUE;
                long j3 = i3 < this.mEvalForegroundModeLogSize ? this.mEvalForegroundModeLogTimestamps[i7] : Long.MAX_VALUE;
                if (j <= j2 && j <= j3) {
                    dumpUpdateUidProcState(printWriter, i5);
                    i++;
                } else if (j2 <= j3) {
                    dumpCommitUidState(printWriter, i6);
                    i2++;
                } else {
                    dumpEvalForegroundMode(printWriter, i7);
                    i3++;
                }
            }
        }

        public void dumpUpdateUidProcState(PrintWriter printWriter, int i) {
            long j = this.mUpdateUidProcStateLogTimestamps[i];
            int[] iArr = this.mUpdateUidProcStateLog[i];
            int i2 = iArr[0];
            int i3 = iArr[1];
            int i4 = iArr[2];
            TimeUtils.dumpTime(printWriter, j);
            printWriter.print(" UPDATE_UID_PROC_STATE");
            printWriter.print(" uid=");
            printWriter.print(String.format("%-8d", Integer.valueOf(i2)));
            printWriter.print(" procState=");
            printWriter.print(String.format("%-30s", ActivityManager.procStateToString(i3)));
            printWriter.print(" capability=");
            printWriter.print(ActivityManager.getCapabilitiesSummary(i4) + " ");
            printWriter.println();
        }

        public void dumpCommitUidState(PrintWriter printWriter, int i) {
            long j = this.mCommitUidStateLogTimestamps[i];
            int[] iArr = this.mCommitUidStateLog[i];
            int i2 = iArr[0];
            int i3 = iArr[1];
            int i4 = iArr[2];
            int i5 = iArr[3];
            boolean z = (i5 & 1) != 0;
            boolean z2 = (i5 & 2) != 0;
            TimeUtils.dumpTime(printWriter, j);
            printWriter.print(" COMMIT_UID_STATE     ");
            printWriter.print(" uid=");
            printWriter.print(String.format("%-8d", Integer.valueOf(i2)));
            printWriter.print(" uidState=");
            printWriter.print(String.format("%-30s", AppOpsManager.uidStateToString(i3)));
            printWriter.print(" capability=");
            printWriter.print(ActivityManager.getCapabilitiesSummary(i4) + " ");
            printWriter.print(" appWidgetVisible=");
            printWriter.print(z);
            if (z2) {
                printWriter.print(" (changed)");
            }
            printWriter.println();
        }

        public void dumpEvalForegroundMode(PrintWriter printWriter, int i) {
            long j = this.mEvalForegroundModeLogTimestamps[i];
            int[] iArr = this.mEvalForegroundModeLog[i];
            int i2 = iArr[0];
            int i3 = iArr[1];
            int i4 = iArr[2];
            int i5 = iArr[3];
            int i6 = iArr[4];
            TimeUtils.dumpTime(printWriter, j);
            printWriter.print(" EVAL_FOREGROUND_MODE ");
            printWriter.print(" uid=");
            printWriter.print(String.format("%-8d", Integer.valueOf(i2)));
            printWriter.print(" uidState=");
            printWriter.print(String.format("%-30s", AppOpsManager.uidStateToString(i3)));
            printWriter.print(" capability=");
            printWriter.print(ActivityManager.getCapabilitiesSummary(i4) + " ");
            printWriter.print(" code=");
            printWriter.print(String.format("%-20s", AppOpsManager.opToName(i5)));
            printWriter.print(" result=");
            printWriter.print(AppOpsManager.modeToName(i6));
            printWriter.println();
        }
    }
}
