package com.android.server.p014wm;

import android.app.ActivityManagerInternal;
import android.os.Build;
import android.os.IBinder;
import android.os.Process;
import android.os.SystemClock;
import android.os.Trace;
import android.util.ArrayMap;
import android.util.Slog;
import android.util.SparseArray;
import android.view.InputApplicationHandle;
import com.android.internal.os.TimeoutRecord;
import com.android.server.SystemServerInitThreadPool$$ExternalSyntheticLambda0;
import com.android.server.criticalevents.CriticalEventLog;
import com.android.server.p006am.ActivityManagerService;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
/* renamed from: com.android.server.wm.AnrController */
/* loaded from: classes2.dex */
public class AnrController {
    public static final long PRE_DUMP_MIN_INTERVAL_MS;
    public static final long PRE_DUMP_MONITOR_TIMEOUT_MS;
    public volatile long mLastPreDumpTimeMs;
    public final WindowManagerService mService;
    public final SparseArray<ActivityRecord> mUnresponsiveAppByDisplay = new SparseArray<>();

    static {
        TimeUnit timeUnit = TimeUnit.SECONDS;
        PRE_DUMP_MIN_INTERVAL_MS = timeUnit.toMillis(20L);
        PRE_DUMP_MONITOR_TIMEOUT_MS = timeUnit.toMillis(1L);
    }

    public AnrController(WindowManagerService windowManagerService) {
        this.mService = windowManagerService;
    }

    public void notifyAppUnresponsive(InputApplicationHandle inputApplicationHandle, TimeoutRecord timeoutRecord) {
        WindowState windowState;
        try {
            Trace.traceBegin(64L, "notifyAppUnresponsive()");
            preDumpIfLockTooSlow();
            timeoutRecord.mLatencyTracker.waitingOnGlobalLockStarted();
            synchronized (this.mService.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                timeoutRecord.mLatencyTracker.waitingOnGlobalLockEnded();
                ActivityRecord forTokenLocked = ActivityRecord.forTokenLocked(inputApplicationHandle.token);
                if (forTokenLocked == null) {
                    Slog.e(StartingSurfaceController.TAG, "Unknown app appToken:" + inputApplicationHandle.name + ". Dropping notifyNoFocusedWindowAnr request");
                } else if (forTokenLocked.mAppStopped) {
                    Slog.d(StartingSurfaceController.TAG, "App is in stopped state:" + inputApplicationHandle.name + ". Dropping notifyNoFocusedWindowAnr request");
                } else {
                    DisplayContent displayContent = this.mService.mRoot.getDisplayContent(forTokenLocked.getDisplayId());
                    IBinder iBinder = displayContent != null ? displayContent.getInputMonitor().mInputFocus : null;
                    InputTarget inputTargetFromToken = this.mService.getInputTargetFromToken(iBinder);
                    boolean z = false;
                    if (inputTargetFromToken != null) {
                        windowState = inputTargetFromToken.getWindowState();
                        if (SystemClock.uptimeMillis() - displayContent.getInputMonitor().mInputFocusRequestTimeMillis >= ActivityTaskManagerService.getInputDispatchingTimeoutMillisLocked(windowState.getActivityRecord())) {
                            z = true;
                        }
                    } else {
                        windowState = null;
                    }
                    if (!z) {
                        Slog.i(StartingSurfaceController.TAG, "ANR in " + forTokenLocked.getName() + ".  Reason: " + timeoutRecord.mReason);
                        dumpAnrStateLocked(forTokenLocked, null, timeoutRecord.mReason);
                        this.mUnresponsiveAppByDisplay.put(forTokenLocked.getDisplayId(), forTokenLocked);
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                    if (z && notifyWindowUnresponsive(iBinder, timeoutRecord)) {
                        Slog.i(StartingSurfaceController.TAG, "Blamed " + windowState.getName() + " using pending focus request. Focused activity: " + forTokenLocked.getName());
                    } else {
                        forTokenLocked.inputDispatchingTimedOut(timeoutRecord, -1);
                    }
                    return;
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            }
        } finally {
            Trace.traceEnd(64L);
        }
    }

    public void notifyWindowUnresponsive(IBinder iBinder, OptionalInt optionalInt, TimeoutRecord timeoutRecord) {
        try {
            Trace.traceBegin(64L, "notifyWindowUnresponsive()");
            if (notifyWindowUnresponsive(iBinder, timeoutRecord)) {
                return;
            }
            if (!optionalInt.isPresent()) {
                Slog.w(StartingSurfaceController.TAG, "Failed to notify that window token=" + iBinder + " was unresponsive.");
                return;
            }
            notifyWindowUnresponsive(optionalInt.getAsInt(), timeoutRecord);
        } finally {
            Trace.traceEnd(64L);
        }
    }

    public final boolean notifyWindowUnresponsive(IBinder iBinder, TimeoutRecord timeoutRecord) {
        preDumpIfLockTooSlow();
        timeoutRecord.mLatencyTracker.waitingOnGlobalLockStarted();
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                timeoutRecord.mLatencyTracker.waitingOnGlobalLockEnded();
                InputTarget inputTargetFromToken = this.mService.getInputTargetFromToken(iBinder);
                if (inputTargetFromToken == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return false;
                }
                WindowState windowState = inputTargetFromToken.getWindowState();
                int pid = inputTargetFromToken.getPid();
                ActivityRecord activityRecord = windowState.mInputChannelToken == iBinder ? windowState.mActivityRecord : null;
                Slog.i(StartingSurfaceController.TAG, "ANR in " + inputTargetFromToken + ". Reason:" + timeoutRecord.mReason);
                boolean isWindowAboveSystem = isWindowAboveSystem(windowState);
                dumpAnrStateLocked(activityRecord, windowState, timeoutRecord.mReason);
                WindowManagerService.resetPriorityAfterLockedSection();
                if (activityRecord != null) {
                    activityRecord.inputDispatchingTimedOut(timeoutRecord, pid);
                    return true;
                }
                this.mService.mAmInternal.inputDispatchingTimedOut(pid, isWindowAboveSystem, timeoutRecord);
                return true;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public final void notifyWindowUnresponsive(int i, TimeoutRecord timeoutRecord) {
        Slog.i(StartingSurfaceController.TAG, "ANR in input window owned by pid=" + i + ". Reason: " + timeoutRecord.mReason);
        timeoutRecord.mLatencyTracker.waitingOnGlobalLockStarted();
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                timeoutRecord.mLatencyTracker.waitingOnGlobalLockEnded();
                dumpAnrStateLocked(null, null, timeoutRecord.mReason);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        this.mService.mAmInternal.inputDispatchingTimedOut(i, true, timeoutRecord);
    }

    public void notifyWindowResponsive(IBinder iBinder, OptionalInt optionalInt) {
        if (notifyWindowResponsive(iBinder)) {
            return;
        }
        if (!optionalInt.isPresent()) {
            Slog.w(StartingSurfaceController.TAG, "Failed to notify that window token=" + iBinder + " was responsive.");
            return;
        }
        notifyWindowResponsive(optionalInt.getAsInt());
    }

    public final boolean notifyWindowResponsive(IBinder iBinder) {
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                InputTarget inputTargetFromToken = this.mService.getInputTargetFromToken(iBinder);
                if (inputTargetFromToken == null) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return false;
                }
                int pid = inputTargetFromToken.getPid();
                WindowManagerService.resetPriorityAfterLockedSection();
                this.mService.mAmInternal.inputDispatchingResumed(pid);
                return true;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public final void notifyWindowResponsive(int i) {
        this.mService.mAmInternal.inputDispatchingResumed(i);
    }

    public void onFocusChanged(WindowState windowState) {
        synchronized (this.mService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityRecord activityRecord = this.mUnresponsiveAppByDisplay.get(windowState.getDisplayId());
                if (activityRecord != null && activityRecord == windowState.mActivityRecord) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    this.mService.mAmInternal.inputDispatchingResumed(activityRecord.getPid());
                    this.mUnresponsiveAppByDisplay.remove(windowState.getDisplayId());
                    return;
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public final void preDumpIfLockTooSlow() {
        if (Build.IS_DEBUGGABLE) {
            final long uptimeMillis = SystemClock.uptimeMillis();
            if (this.mLastPreDumpTimeMs <= 0 || uptimeMillis - this.mLastPreDumpTimeMs >= PRE_DUMP_MIN_INTERVAL_MS) {
                Trace.traceBegin(64L, "preDumpIfLockTooSlow()");
                try {
                    final boolean[] zArr = {true};
                    ArrayMap arrayMap = new ArrayMap(2);
                    final WindowManagerService windowManagerService = this.mService;
                    Objects.requireNonNull(windowManagerService);
                    arrayMap.put(StartingSurfaceController.TAG, new Runnable() { // from class: com.android.server.wm.AnrController$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            WindowManagerService.this.monitor();
                        }
                    });
                    final ActivityManagerInternal activityManagerInternal = this.mService.mAmInternal;
                    Objects.requireNonNull(activityManagerInternal);
                    arrayMap.put("ActivityManager", new Runnable() { // from class: com.android.server.wm.AnrController$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            activityManagerInternal.monitor();
                        }
                    });
                    CountDownLatch countDownLatch = new CountDownLatch(arrayMap.size());
                    int i = 0;
                    while (i < arrayMap.size()) {
                        final String str = (String) arrayMap.keyAt(i);
                        final Runnable runnable = (Runnable) arrayMap.valueAt(i);
                        final CountDownLatch countDownLatch2 = countDownLatch;
                        int i2 = i;
                        CountDownLatch countDownLatch3 = countDownLatch;
                        ArrayMap arrayMap2 = arrayMap;
                        new Thread() { // from class: com.android.server.wm.AnrController.1
                            @Override // java.lang.Thread, java.lang.Runnable
                            public void run() {
                                runnable.run();
                                countDownLatch2.countDown();
                                long uptimeMillis2 = SystemClock.uptimeMillis() - uptimeMillis;
                                if (uptimeMillis2 > AnrController.PRE_DUMP_MONITOR_TIMEOUT_MS) {
                                    Slog.i(StartingSurfaceController.TAG, "Pre-dump acquired " + str + " in " + uptimeMillis2 + "ms");
                                } else if (StartingSurfaceController.TAG.equals(str)) {
                                    zArr[0] = false;
                                }
                            }
                        }.start();
                        i = i2 + 1;
                        countDownLatch = countDownLatch3;
                        arrayMap = arrayMap2;
                    }
                    try {
                        if (countDownLatch.await(PRE_DUMP_MONITOR_TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
                            return;
                        }
                    } catch (InterruptedException unused) {
                    }
                    this.mLastPreDumpTimeMs = uptimeMillis;
                    Slog.i(StartingSurfaceController.TAG, "Pre-dump for unresponsive");
                    ArrayList arrayList = new ArrayList(1);
                    arrayList.add(Integer.valueOf(WindowManagerService.MY_PID));
                    ArrayList arrayList2 = null;
                    int[] pidsForCommands = zArr[0] ? Process.getPidsForCommands(new String[]{"/system/bin/surfaceflinger"}) : null;
                    if (pidsForCommands != null) {
                        arrayList2 = new ArrayList(1);
                        for (int i3 : pidsForCommands) {
                            arrayList2.add(Integer.valueOf(i3));
                        }
                    }
                    File dumpStackTraces = ActivityManagerService.dumpStackTraces(arrayList, null, null, CompletableFuture.completedFuture(arrayList2), null, "Pre-dump", CriticalEventLog.getInstance().logLinesForSystemServerTraceFile(), new SystemServerInitThreadPool$$ExternalSyntheticLambda0(), null);
                    if (dumpStackTraces != null) {
                        dumpStackTraces.renameTo(new File(dumpStackTraces.getParent(), dumpStackTraces.getName() + "_pre"));
                    }
                } finally {
                    Trace.traceEnd(64L);
                }
            }
        }
    }

    public final void dumpAnrStateLocked(ActivityRecord activityRecord, WindowState windowState, String str) {
        try {
            Trace.traceBegin(64L, "dumpAnrStateLocked()");
            this.mService.saveANRStateLocked(activityRecord, windowState, str);
            this.mService.mAtmService.saveANRState(str);
        } finally {
            Trace.traceEnd(64L);
        }
    }

    public final boolean isWindowAboveSystem(WindowState windowState) {
        return windowState.mBaseLayer > this.mService.mPolicy.getWindowLayerFromTypeLw(2038, windowState.mOwnerCanAddInternalSystemWindow);
    }
}
