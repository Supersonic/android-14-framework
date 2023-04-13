package com.android.server.p006am;

import android.app.ActivityManager;
import android.app.AnrController;
import android.app.ApplicationErrorReport;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.ApplicationInfo;
import android.content.pm.IncrementalStatesInfo;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.incremental.IIncrementalService;
import android.os.incremental.IncrementalManager;
import android.os.incremental.IncrementalMetrics;
import android.provider.Settings;
import android.util.EventLog;
import android.util.Slog;
import android.util.SparseBooleanArray;
import com.android.internal.annotations.CompositeRWLock;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.expresslog.Counter;
import com.android.internal.os.ProcessCpuTracker;
import com.android.internal.os.TimeoutRecord;
import com.android.internal.os.anr.AnrLatencyTracker;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.ResourcePressureUtil;
import com.android.server.Watchdog;
import com.android.server.criticalevents.CriticalEventLog;
import com.android.server.p006am.AppNotRespondingDialog;
import com.android.server.p014wm.WindowProcessController;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
/* renamed from: com.android.server.am.ProcessErrorStateRecord */
/* loaded from: classes.dex */
public class ProcessErrorStateRecord {
    @GuardedBy({"mService"})
    public String mAnrAnnotation;
    @CompositeRWLock({"mService", "mProcLock"})
    public AppNotRespondingDialog.Data mAnrData;
    public final ProcessRecord mApp;
    @CompositeRWLock({"mService", "mProcLock"})
    public boolean mBad;
    @CompositeRWLock({"mService", "mProcLock"})
    public Runnable mCrashHandler;
    @CompositeRWLock({"mService", "mProcLock"})
    public boolean mCrashing;
    @CompositeRWLock({"mService", "mProcLock"})
    public ActivityManager.ProcessErrorStateInfo mCrashingReport;
    @CompositeRWLock({"mService", "mProcLock"})
    public final ErrorDialogController mDialogController;
    @CompositeRWLock({"mService", "mProcLock"})
    public ComponentName mErrorReportReceiver;
    @CompositeRWLock({"mService", "mProcLock"})
    public boolean mForceCrashReport;
    @CompositeRWLock({"mService", "mProcLock"})
    public boolean mNotResponding;
    @CompositeRWLock({"mService", "mProcLock"})
    public ActivityManager.ProcessErrorStateInfo mNotRespondingReport;
    public final ActivityManagerGlobalLock mProcLock;
    public final ActivityManagerService mService;

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public boolean isBad() {
        return this.mBad;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setBad(boolean z) {
        this.mBad = z;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public boolean isCrashing() {
        return this.mCrashing;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setCrashing(boolean z) {
        this.mCrashing = z;
        this.mApp.getWindowProcessController().setCrashing(z);
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public boolean isForceCrashReport() {
        return this.mForceCrashReport;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setForceCrashReport(boolean z) {
        this.mForceCrashReport = z;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public boolean isNotResponding() {
        return this.mNotResponding;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setNotResponding(boolean z) {
        this.mNotResponding = z;
        this.mApp.getWindowProcessController().setNotResponding(z);
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public Runnable getCrashHandler() {
        return this.mCrashHandler;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setCrashHandler(Runnable runnable) {
        this.mCrashHandler = runnable;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public ActivityManager.ProcessErrorStateInfo getCrashingReport() {
        return this.mCrashingReport;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setCrashingReport(ActivityManager.ProcessErrorStateInfo processErrorStateInfo) {
        this.mCrashingReport = processErrorStateInfo;
    }

    @GuardedBy({"mService"})
    public String getAnrAnnotation() {
        return this.mAnrAnnotation;
    }

    @GuardedBy({"mService"})
    public void setAnrAnnotation(String str) {
        this.mAnrAnnotation = str;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public ActivityManager.ProcessErrorStateInfo getNotRespondingReport() {
        return this.mNotRespondingReport;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setNotRespondingReport(ActivityManager.ProcessErrorStateInfo processErrorStateInfo) {
        this.mNotRespondingReport = processErrorStateInfo;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public ComponentName getErrorReportReceiver() {
        return this.mErrorReportReceiver;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public ErrorDialogController getDialogController() {
        return this.mDialogController;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setAnrData(AppNotRespondingDialog.Data data) {
        this.mAnrData = data;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public AppNotRespondingDialog.Data getAnrData() {
        return this.mAnrData;
    }

    public ProcessErrorStateRecord(ProcessRecord processRecord) {
        this.mApp = processRecord;
        ActivityManagerService activityManagerService = processRecord.mService;
        this.mService = activityManagerService;
        this.mProcLock = activityManagerService.mProcLock;
        this.mDialogController = new ErrorDialogController(processRecord);
    }

    @GuardedBy({"mService"})
    public boolean skipAnrLocked(String str) {
        if (this.mService.mAtmInternal.isShuttingDown()) {
            Slog.i("ActivityManager", "During shutdown skipping ANR: " + this + " " + str);
            return true;
        } else if (isNotResponding()) {
            Slog.i("ActivityManager", "Skipping duplicate ANR: " + this + " " + str);
            return true;
        } else if (isCrashing()) {
            Slog.i("ActivityManager", "Crashing app skipping ANR: " + this + " " + str);
            return true;
        } else if (this.mApp.isKilledByAm()) {
            Slog.i("ActivityManager", "App already killed by AM skipping ANR: " + this + " " + str);
            return true;
        } else if (this.mApp.isKilled()) {
            Slog.i("ActivityManager", "Skipping died app ANR: " + this + " " + str);
            return true;
        } else {
            return false;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:102:0x03af  */
    /* JADX WARN: Removed duplicated region for block: B:103:0x03b5  */
    /* JADX WARN: Removed duplicated region for block: B:106:0x03bb  */
    /* JADX WARN: Removed duplicated region for block: B:110:0x03cb  */
    /* JADX WARN: Removed duplicated region for block: B:113:0x03d5  */
    /* JADX WARN: Removed duplicated region for block: B:114:0x03d8  */
    /* JADX WARN: Removed duplicated region for block: B:117:0x03e6  */
    /* JADX WARN: Removed duplicated region for block: B:118:0x03ed  */
    /* JADX WARN: Removed duplicated region for block: B:121:0x03f3  */
    /* JADX WARN: Removed duplicated region for block: B:122:0x03f6  */
    /* JADX WARN: Removed duplicated region for block: B:124:0x03fa  */
    /* JADX WARN: Removed duplicated region for block: B:125:0x0401  */
    /* JADX WARN: Removed duplicated region for block: B:128:0x0406  */
    /* JADX WARN: Removed duplicated region for block: B:129:0x040d  */
    /* JADX WARN: Removed duplicated region for block: B:131:0x0411  */
    /* JADX WARN: Removed duplicated region for block: B:132:0x0416  */
    /* JADX WARN: Removed duplicated region for block: B:139:0x0426  */
    /* JADX WARN: Removed duplicated region for block: B:140:0x042d  */
    /* JADX WARN: Removed duplicated region for block: B:142:0x0431  */
    /* JADX WARN: Removed duplicated region for block: B:143:0x0438  */
    /* JADX WARN: Removed duplicated region for block: B:145:0x043c  */
    /* JADX WARN: Removed duplicated region for block: B:146:0x0443  */
    /* JADX WARN: Removed duplicated region for block: B:148:0x0447  */
    /* JADX WARN: Removed duplicated region for block: B:149:0x044e  */
    /* JADX WARN: Removed duplicated region for block: B:151:0x0452  */
    /* JADX WARN: Removed duplicated region for block: B:154:0x045a  */
    /* JADX WARN: Removed duplicated region for block: B:155:0x0461  */
    /* JADX WARN: Removed duplicated region for block: B:157:0x0465  */
    /* JADX WARN: Removed duplicated region for block: B:158:0x046c  */
    /* JADX WARN: Removed duplicated region for block: B:160:0x0470  */
    /* JADX WARN: Removed duplicated region for block: B:161:0x0477  */
    /* JADX WARN: Removed duplicated region for block: B:164:0x048a  */
    /* JADX WARN: Removed duplicated region for block: B:165:0x0490  */
    /* JADX WARN: Removed duplicated region for block: B:168:0x04dd A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:169:0x04de  */
    /* JADX WARN: Removed duplicated region for block: B:99:0x0396  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void appNotResponding(String str, ApplicationInfo applicationInfo, String str2, WindowProcessController windowProcessController, boolean z, TimeoutRecord timeoutRecord, ExecutorService executorService, final boolean z2, boolean z3) {
        UUID uuid;
        AnrController anrController;
        StringBuilder sb;
        long j;
        long j2;
        StringBuilder sb2;
        IncrementalMetrics incrementalMetrics;
        StringBuilder sb3;
        String str3;
        final String str4 = timeoutRecord.mReason;
        final AnrLatencyTracker anrLatencyTracker = timeoutRecord.mLatencyTracker;
        final ArrayList arrayList = new ArrayList(5);
        final SparseBooleanArray sparseBooleanArray = new SparseBooleanArray(20);
        this.mApp.getWindowProcessController().appEarlyNotResponding(str4, new Runnable() { // from class: com.android.server.am.ProcessErrorStateRecord$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                ProcessErrorStateRecord.this.lambda$appNotResponding$0(anrLatencyTracker, str4);
            }
        });
        long uptimeMillis = SystemClock.uptimeMillis();
        Future<?> submit = isMonitorCpuUsage() ? executorService.submit(new Runnable() { // from class: com.android.server.am.ProcessErrorStateRecord$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                ProcessErrorStateRecord.this.lambda$appNotResponding$1(anrLatencyTracker);
            }
        }) : null;
        final int pid = this.mApp.getPid();
        anrLatencyTracker.waitingOnAMSLockStarted();
        synchronized (this.mService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                anrLatencyTracker.waitingOnAMSLockEnded();
                setAnrAnnotation(str4);
                Counter.logIncrement("stability_anr.value_total_anrs");
                if (skipAnrLocked(str4)) {
                    anrLatencyTracker.anrSkippedProcessErrorStateRecordAppNotResponding();
                    Counter.logIncrement("stability_anr.value_skipped_anrs");
                    return;
                }
                anrLatencyTracker.waitingOnProcLockStarted();
                synchronized (this.mProcLock) {
                    ActivityManagerService.boostPriorityForProcLockedSection();
                    anrLatencyTracker.waitingOnProcLockEnded();
                    setNotResponding(true);
                }
                ActivityManagerService.resetPriorityAfterProcLockedSection();
                ProcessRecord processRecord = this.mApp;
                EventLog.writeEvent(30008, Integer.valueOf(this.mApp.userId), Integer.valueOf(pid), processRecord.processName, Integer.valueOf(processRecord.info.flags), str4);
                TraceErrorLogger traceErrorLogger = this.mService.mTraceErrorLogger;
                if (traceErrorLogger == null || !traceErrorLogger.isAddErrorIdEnabled()) {
                    uuid = null;
                } else {
                    UUID generateErrorId = this.mService.mTraceErrorLogger.generateErrorId();
                    this.mService.mTraceErrorLogger.addErrorIdToTrace(this.mApp.processName, generateErrorId);
                    this.mService.mTraceErrorLogger.addSubjectToTrace(str4, generateErrorId);
                    uuid = generateErrorId;
                }
                FrameworkStatsLog.write((int) FrameworkStatsLog.ANR_OCCURRED_PROCESSING_STARTED, this.mApp.processName);
                arrayList.add(Integer.valueOf(pid));
                final boolean isSilentAnr = isSilentAnr();
                if (!isSilentAnr && !z2) {
                    final int pid2 = (windowProcessController == null || windowProcessController.getPid() <= 0) ? pid : windowProcessController.getPid();
                    if (pid2 != pid) {
                        arrayList.add(Integer.valueOf(pid2));
                    }
                    int i = ActivityManagerService.MY_PID;
                    if (i != pid && i != pid2) {
                        arrayList.add(Integer.valueOf(i));
                    }
                    this.mService.mProcessList.forEachLruProcessesLOSP(false, new Consumer() { // from class: com.android.server.am.ProcessErrorStateRecord$$ExternalSyntheticLambda2
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            ProcessErrorStateRecord.lambda$appNotResponding$2(pid, pid2, arrayList, sparseBooleanArray, (ProcessRecord) obj);
                        }
                    });
                }
                ActivityManagerService.resetPriorityAfterLockedSection();
                anrLatencyTracker.criticalEventLogStarted();
                CriticalEventLog criticalEventLog = CriticalEventLog.getInstance();
                int processClassEnum = this.mApp.getProcessClassEnum();
                ProcessRecord processRecord2 = this.mApp;
                String logLinesForTraceFile = criticalEventLog.logLinesForTraceFile(processClassEnum, processRecord2.processName, processRecord2.uid);
                anrLatencyTracker.criticalEventLogEnded();
                CriticalEventLog criticalEventLog2 = CriticalEventLog.getInstance();
                int processClassEnum2 = this.mApp.getProcessClassEnum();
                ProcessRecord processRecord3 = this.mApp;
                criticalEventLog2.logAnr(str4, processClassEnum2, processRecord3.processName, processRecord3.uid, processRecord3.mPid);
                StringBuilder sb4 = new StringBuilder();
                sb4.setLength(0);
                sb4.append("ANR in ");
                sb4.append(this.mApp.processName);
                if (str != null) {
                    sb4.append(" (");
                    sb4.append(str);
                    sb4.append(")");
                }
                sb4.append("\n");
                sb4.append("PID: ");
                sb4.append(pid);
                sb4.append("\n");
                if (str4 != null) {
                    sb4.append("Reason: ");
                    sb4.append(str4);
                    sb4.append("\n");
                }
                if (str2 != null && str2.equals(str)) {
                    sb4.append("Parent: ");
                    sb4.append(str2);
                    sb4.append("\n");
                }
                if (uuid != null) {
                    sb4.append("ErrorId: ");
                    sb4.append(uuid.toString());
                    sb4.append("\n");
                }
                sb4.append("Frozen: ");
                sb4.append(this.mApp.mOptRecord.isFrozen());
                sb4.append("\n");
                AnrController anrController2 = this.mService.mActivityTaskManager.getAnrController(applicationInfo);
                if (anrController2 != null) {
                    String str5 = applicationInfo.packageName;
                    int i2 = applicationInfo.uid;
                    sb = sb4;
                    j = anrController2.getAnrDelayMillis(str5, i2);
                    anrController2.onAnrDelayStarted(str5, i2);
                    StringBuilder sb5 = new StringBuilder();
                    anrController = anrController2;
                    sb5.append("ANR delay of ");
                    sb5.append(j);
                    sb5.append("ms started for ");
                    sb5.append(str5);
                    Slog.i("ActivityManager", sb5.toString());
                } else {
                    anrController = anrController2;
                    sb = sb4;
                    j = 0;
                }
                StringBuilder sb6 = new StringBuilder();
                anrLatencyTracker.currentPsiStateCalled();
                String currentPsiState = ResourcePressureUtil.currentPsiState();
                anrLatencyTracker.currentPsiStateReturned();
                sb6.append(currentPsiState);
                ProcessCpuTracker processCpuTracker = new ProcessCpuTracker(true);
                Future submit2 = executorService.submit(new Callable() { // from class: com.android.server.am.ProcessErrorStateRecord$$ExternalSyntheticLambda3
                    @Override // java.util.concurrent.Callable
                    public final Object call() {
                        ArrayList lambda$appNotResponding$3;
                        lambda$appNotResponding$3 = ProcessErrorStateRecord.this.lambda$appNotResponding$3(anrLatencyTracker, isSilentAnr, z2);
                        return lambda$appNotResponding$3;
                    }
                });
                StringWriter stringWriter = new StringWriter();
                long j3 = j;
                AtomicLong atomicLong = new AtomicLong(-1L);
                File dumpStackTraces = ActivityManagerService.dumpStackTraces(arrayList, isSilentAnr ? null : processCpuTracker, isSilentAnr ? null : sparseBooleanArray, submit2, stringWriter, atomicLong, str4, logLinesForTraceFile, executorService, anrLatencyTracker);
                if (isMonitorCpuUsage()) {
                    try {
                        submit.get();
                    } catch (InterruptedException e) {
                        Slog.w("ActivityManager", "Interrupted while updating the CPU stats", e);
                    } catch (ExecutionException e2) {
                        Slog.w("ActivityManager", "Failed to update the CPU stats", e2.getCause());
                    }
                    this.mService.updateCpuStatsNow();
                    j2 = uptimeMillis;
                    this.mService.mAppProfiler.printCurrentCpuState(sb6, j2);
                    sb2 = sb;
                    sb2.append(processCpuTracker.printCurrentLoad());
                    sb2.append((CharSequence) sb6);
                } else {
                    j2 = uptimeMillis;
                    sb2 = sb;
                }
                sb6.append(stringWriter.getBuffer());
                sb2.append(processCpuTracker.printCurrentState(j2));
                Slog.e("ActivityManager", sb2.toString());
                if (dumpStackTraces == null) {
                    Process.sendSignal(pid, 3);
                } else if (atomicLong.get() > 0) {
                    long j4 = atomicLong.get();
                    AppExitInfoTracker appExitInfoTracker = this.mService.mProcessList.mAppExitInfoTracker;
                    ProcessRecord processRecord4 = this.mApp;
                    appExitInfoTracker.scheduleLogAnrTrace(pid, processRecord4.uid, processRecord4.getPackageList(), dumpStackTraces, 0L, j4);
                }
                PackageManagerInternal packageManagerInternal = this.mService.getPackageManagerInternal();
                if (this.mApp.info != null && this.mApp.info.packageName != null && packageManagerInternal != null) {
                    IncrementalStatesInfo incrementalStatesInfo = packageManagerInternal.getIncrementalStatesInfo(this.mApp.info.packageName, 1000, this.mApp.userId);
                    r4 = incrementalStatesInfo != null ? incrementalStatesInfo.getProgress() : 1.0f;
                    String codePath = this.mApp.info.getCodePath();
                    if (codePath != null && !codePath.isEmpty() && IncrementalManager.isIncrementalPath(codePath)) {
                        Slog.e("ActivityManager", "App ANR on incremental package " + this.mApp.info.packageName + " which is " + ((int) (r4 * 100.0f)) + "% loaded.");
                        IBinder service = ServiceManager.getService("incremental");
                        if (service != null) {
                            incrementalMetrics = new IncrementalManager(IIncrementalService.Stub.asInterface(service)).getMetrics(codePath);
                            if (incrementalMetrics != null) {
                                sb2.append("Package is ");
                                sb2.append((int) (100.0f * r4));
                                sb2.append("% loaded.\n");
                            }
                            ProcessRecord processRecord5 = this.mApp;
                            FrameworkStatsLog.write(79, processRecord5.uid, processRecord5.processName, str != null ? "unknown" : str, str4, processRecord5.info == null ? this.mApp.info.isInstantApp() ? 2 : 1 : 0, !this.mApp.isInterestingToUserLocked() ? 2 : 1, this.mApp.getProcessClassEnum(), this.mApp.info == null ? this.mApp.info.packageName : "", incrementalMetrics == null, r4, incrementalMetrics == null ? incrementalMetrics.getMillisSinceOldestPendingRead() : -1L, incrementalMetrics == null ? incrementalMetrics.getStorageHealthStatusCode() : -1, incrementalMetrics == null ? incrementalMetrics.getDataLoaderStatusCode() : -1, incrementalMetrics == null && incrementalMetrics.getReadLogsEnabled(), incrementalMetrics == null ? incrementalMetrics.getMillisSinceLastDataLoaderBind() : -1L, incrementalMetrics == null ? incrementalMetrics.getDataLoaderBindDelayMillis() : -1L, incrementalMetrics == null ? incrementalMetrics.getTotalDelayedReads() : -1, incrementalMetrics == null ? incrementalMetrics.getTotalFailedReads() : -1, incrementalMetrics != null ? incrementalMetrics.getLastReadErrorUid() : -1, incrementalMetrics == null ? incrementalMetrics.getMillisSinceLastReadError() : -1L, incrementalMetrics == null ? incrementalMetrics.getLastReadErrorNumber() : 0, incrementalMetrics == null ? incrementalMetrics.getTotalDelayedReadsDurationMillis() : -1L);
                            ProcessRecord processRecord6 = windowProcessController == null ? (ProcessRecord) windowProcessController.mOwner : null;
                            ActivityManagerService activityManagerService = this.mService;
                            ProcessRecord processRecord7 = this.mApp;
                            AnrController anrController3 = anrController;
                            sb3 = sb2;
                            activityManagerService.addErrorToDropBox("anr", processRecord7, processRecord7.processName, str, str2, processRecord6, null, sb6.toString(), dumpStackTraces, null, new Float(r4), incrementalMetrics, uuid);
                            if (this.mApp.getWindowProcessController().appNotResponding(sb3.toString(), new Runnable() { // from class: com.android.server.am.ProcessErrorStateRecord$$ExternalSyntheticLambda4
                                @Override // java.lang.Runnable
                                public final void run() {
                                    ProcessErrorStateRecord.this.lambda$appNotResponding$4();
                                }
                            }, new Runnable() { // from class: com.android.server.am.ProcessErrorStateRecord$$ExternalSyntheticLambda5
                                @Override // java.lang.Runnable
                                public final void run() {
                                    ProcessErrorStateRecord.this.lambda$appNotResponding$5();
                                }
                            })) {
                                synchronized (this.mService) {
                                    try {
                                        ActivityManagerService.boostPriorityForLockedSection();
                                        BatteryStatsService batteryStatsService = this.mService.mBatteryStatsService;
                                        if (batteryStatsService != null) {
                                            ProcessRecord processRecord8 = this.mApp;
                                            batteryStatsService.noteProcessAnr(processRecord8.processName, processRecord8.uid);
                                        }
                                        if (isSilentAnr() && !this.mApp.isDebugging()) {
                                            this.mApp.killLocked("bg anr", 6, true);
                                            return;
                                        }
                                        synchronized (this.mProcLock) {
                                            ActivityManagerService.boostPriorityForProcLockedSection();
                                            if (str4 != null) {
                                                str3 = "ANR " + str4;
                                            } else {
                                                str3 = "ANR";
                                            }
                                            makeAppNotRespondingLSP(str, str3, sb3.toString());
                                            this.mDialogController.setAnrController(anrController3);
                                        }
                                        ActivityManagerService.resetPriorityAfterProcLockedSection();
                                        if (this.mService.mUiHandler != null) {
                                            Message obtain = Message.obtain();
                                            obtain.what = 2;
                                            obtain.obj = new AppNotRespondingDialog.Data(this.mApp, applicationInfo, z, z3);
                                            this.mService.mUiHandler.sendMessageDelayed(obtain, j3);
                                        }
                                        ActivityManagerService.resetPriorityAfterLockedSection();
                                        return;
                                    } finally {
                                        ActivityManagerService.resetPriorityAfterLockedSection();
                                    }
                                }
                            }
                            return;
                        }
                    }
                }
                incrementalMetrics = null;
                if (incrementalMetrics != null) {
                }
                ProcessRecord processRecord52 = this.mApp;
                FrameworkStatsLog.write(79, processRecord52.uid, processRecord52.processName, str != null ? "unknown" : str, str4, processRecord52.info == null ? this.mApp.info.isInstantApp() ? 2 : 1 : 0, !this.mApp.isInterestingToUserLocked() ? 2 : 1, this.mApp.getProcessClassEnum(), this.mApp.info == null ? this.mApp.info.packageName : "", incrementalMetrics == null, r4, incrementalMetrics == null ? incrementalMetrics.getMillisSinceOldestPendingRead() : -1L, incrementalMetrics == null ? incrementalMetrics.getStorageHealthStatusCode() : -1, incrementalMetrics == null ? incrementalMetrics.getDataLoaderStatusCode() : -1, incrementalMetrics == null && incrementalMetrics.getReadLogsEnabled(), incrementalMetrics == null ? incrementalMetrics.getMillisSinceLastDataLoaderBind() : -1L, incrementalMetrics == null ? incrementalMetrics.getDataLoaderBindDelayMillis() : -1L, incrementalMetrics == null ? incrementalMetrics.getTotalDelayedReads() : -1, incrementalMetrics == null ? incrementalMetrics.getTotalFailedReads() : -1, incrementalMetrics != null ? incrementalMetrics.getLastReadErrorUid() : -1, incrementalMetrics == null ? incrementalMetrics.getMillisSinceLastReadError() : -1L, incrementalMetrics == null ? incrementalMetrics.getLastReadErrorNumber() : 0, incrementalMetrics == null ? incrementalMetrics.getTotalDelayedReadsDurationMillis() : -1L);
                if (windowProcessController == null) {
                }
                ActivityManagerService activityManagerService2 = this.mService;
                ProcessRecord processRecord72 = this.mApp;
                AnrController anrController32 = anrController;
                sb3 = sb2;
                activityManagerService2.addErrorToDropBox("anr", processRecord72, processRecord72.processName, str, str2, processRecord6, null, sb6.toString(), dumpStackTraces, null, new Float(r4), incrementalMetrics, uuid);
                if (this.mApp.getWindowProcessController().appNotResponding(sb3.toString(), new Runnable() { // from class: com.android.server.am.ProcessErrorStateRecord$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        ProcessErrorStateRecord.this.lambda$appNotResponding$4();
                    }
                }, new Runnable() { // from class: com.android.server.am.ProcessErrorStateRecord$$ExternalSyntheticLambda5
                    @Override // java.lang.Runnable
                    public final void run() {
                        ProcessErrorStateRecord.this.lambda$appNotResponding$5();
                    }
                })) {
                }
            } finally {
                ActivityManagerService.resetPriorityAfterLockedSection();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$appNotResponding$0(AnrLatencyTracker anrLatencyTracker, String str) {
        anrLatencyTracker.waitingOnAMSLockStarted();
        synchronized (this.mService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                anrLatencyTracker.waitingOnAMSLockEnded();
                setAnrAnnotation(str);
                this.mApp.killLocked("anr", 6, true);
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$appNotResponding$1(AnrLatencyTracker anrLatencyTracker) {
        anrLatencyTracker.updateCpuStatsNowCalled();
        this.mService.updateCpuStatsNow();
        anrLatencyTracker.updateCpuStatsNowReturned();
    }

    public static /* synthetic */ void lambda$appNotResponding$2(int i, int i2, ArrayList arrayList, SparseBooleanArray sparseBooleanArray, ProcessRecord processRecord) {
        int pid;
        if (processRecord == null || processRecord.getThread() == null || (pid = processRecord.getPid()) <= 0 || pid == i || pid == i2 || pid == ActivityManagerService.MY_PID) {
            return;
        }
        if (processRecord.isPersistent()) {
            arrayList.add(Integer.valueOf(pid));
        } else if (processRecord.mServices.isTreatedLikeActivity()) {
            arrayList.add(Integer.valueOf(pid));
        } else {
            sparseBooleanArray.put(pid, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ ArrayList lambda$appNotResponding$3(AnrLatencyTracker anrLatencyTracker, boolean z, boolean z2) throws Exception {
        String[] strArr;
        anrLatencyTracker.nativePidCollectionStarted();
        ArrayList arrayList = null;
        if (z || z2) {
            int i = 0;
            while (true) {
                String[] strArr2 = Watchdog.NATIVE_STACKS_OF_INTEREST;
                if (i >= strArr2.length) {
                    strArr = null;
                    break;
                } else if (strArr2[i].equals(this.mApp.processName)) {
                    strArr = new String[]{this.mApp.processName};
                    break;
                } else {
                    i++;
                }
            }
        } else {
            strArr = Watchdog.NATIVE_STACKS_OF_INTEREST;
        }
        int[] pidsForCommands = strArr == null ? null : Process.getPidsForCommands(strArr);
        if (pidsForCommands != null) {
            arrayList = new ArrayList(pidsForCommands.length);
            for (int i2 : pidsForCommands) {
                arrayList.add(Integer.valueOf(i2));
            }
        }
        anrLatencyTracker.nativePidCollectionEnded();
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$appNotResponding$4() {
        synchronized (this.mService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                this.mApp.killLocked("anr", 6, true);
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$appNotResponding$5() {
        synchronized (this.mService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                this.mService.mServices.scheduleServiceTimeoutLocked(this.mApp);
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
    }

    @GuardedBy({"mService", "mProcLock"})
    public final void makeAppNotRespondingLSP(String str, String str2, String str3) {
        setNotResponding(true);
        AppErrors appErrors = this.mService.mAppErrors;
        if (appErrors != null) {
            this.mNotRespondingReport = appErrors.generateProcessError(this.mApp, 2, str, str2, str3, null);
        }
        startAppProblemLSP();
        this.mApp.getWindowProcessController().stopFreezingActivities();
    }

    @GuardedBy({"mService", "mProcLock"})
    public void startAppProblemLSP() {
        int[] currentProfileIds;
        this.mErrorReportReceiver = null;
        for (int i : this.mService.mUserController.getCurrentProfileIds()) {
            ProcessRecord processRecord = this.mApp;
            if (processRecord.userId == i) {
                this.mErrorReportReceiver = ApplicationErrorReport.getErrorReportReceiver(this.mService.mContext, processRecord.info.packageName, this.mApp.info.flags);
            }
        }
        for (BroadcastQueue broadcastQueue : this.mService.mBroadcastQueues) {
            broadcastQueue.onApplicationProblemLocked(this.mApp);
        }
    }

    @GuardedBy({"mService"})
    public final boolean isInterestingForBackgroundTraces() {
        if (this.mApp.getPid() == ActivityManagerService.MY_PID || this.mApp.isInterestingToUserLocked()) {
            return true;
        }
        return (this.mApp.info != null && "com.android.systemui".equals(this.mApp.info.packageName)) || this.mApp.mState.hasTopUi() || this.mApp.mState.hasOverlayUi();
    }

    public final boolean getShowBackground() {
        ContentResolver contentResolver = this.mService.mContext.getContentResolver();
        return Settings.Secure.getIntForUser(contentResolver, "anr_show_background", 0, contentResolver.getUserId()) != 0;
    }

    @GuardedBy({"mService"})
    @VisibleForTesting
    public boolean isSilentAnr() {
        return (getShowBackground() || isInterestingForBackgroundTraces()) ? false : true;
    }

    @VisibleForTesting
    public boolean isMonitorCpuUsage() {
        AppProfiler appProfiler = this.mService.mAppProfiler;
        return true;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void onCleanupApplicationRecordLSP() {
        getDialogController().clearAllErrorDialogs();
        setCrashing(false);
        setNotResponding(false);
    }

    public void dump(PrintWriter printWriter, String str, long j) {
        synchronized (this.mProcLock) {
            try {
                ActivityManagerService.boostPriorityForProcLockedSection();
                if (this.mCrashing || this.mDialogController.hasCrashDialogs() || this.mNotResponding || this.mDialogController.hasAnrDialogs() || this.mBad) {
                    printWriter.print(str);
                    printWriter.print(" mCrashing=" + this.mCrashing);
                    printWriter.print(" " + this.mDialogController.getCrashDialogs());
                    printWriter.print(" mNotResponding=" + this.mNotResponding);
                    printWriter.print(" " + this.mDialogController.getAnrDialogs());
                    printWriter.print(" bad=" + this.mBad);
                    if (this.mErrorReportReceiver != null) {
                        printWriter.print(" errorReportReceiver=");
                        printWriter.print(this.mErrorReportReceiver.flattenToShortString());
                    }
                    printWriter.println();
                }
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        ActivityManagerService.resetPriorityAfterProcLockedSection();
    }
}
