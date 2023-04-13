package com.android.server.p006am;

import android.app.ActivityManager;
import android.app.ActivityThread;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.net.INetd;
import android.net.NetworkPolicyManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManagerInternal;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Trace;
import android.p005os.IInstalld;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.CompositeRWLock;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.LocalServices;
import com.android.server.ServiceThread;
import com.android.server.p006am.ActivityManagerService;
import com.android.server.p006am.CachedAppOptimizer;
import com.android.server.p014wm.ActivityServiceConnectionsHolder;
import com.android.server.p014wm.WindowProcessController;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
/* renamed from: com.android.server.am.OomAdjuster */
/* loaded from: classes.dex */
public class OomAdjuster {
    @CompositeRWLock({"mService", "mProcLock"})
    public ActiveUids mActiveUids;
    public int mAdjSeq;
    public CacheOomRanker mCacheOomRanker;
    public CachedAppOptimizer mCachedAppOptimizer;
    public ActivityManagerConstants mConstants;
    public double mLastFreeSwapPercent;
    public PowerManagerInternal mLocalPowerManager;
    public int mNewNumAServiceProcs;
    public int mNewNumServiceProcs;
    public long mNextNoKillDebugMessageTime;
    public int mNumCachedHiddenProcs;
    public int mNumNonCachedProcs;
    public int mNumServiceProcs;
    public final int mNumSlots;
    @GuardedBy({"mService"})
    public boolean mOomAdjUpdateOngoing;
    @GuardedBy({"mService"})
    public boolean mPendingFullOomAdjUpdate;
    public final ArraySet<ProcessRecord> mPendingProcessSet;
    public final ActivityManagerGlobalLock mProcLock;
    public final Handler mProcessGroupHandler;
    public final ProcessList mProcessList;
    public final ArraySet<ProcessRecord> mProcessesInCycle;
    public final ActivityManagerService mService;
    public final ArrayList<UidRecord> mTmpBecameIdle;
    public final ComputeOomAdjWindowCallback mTmpComputeOomAdjWindowCallback;
    public final long[] mTmpLong;
    public final ArrayList<ProcessRecord> mTmpProcessList;
    public final ArrayDeque<ProcessRecord> mTmpQueue;
    public final int[] mTmpSchedGroup;
    public final ActiveUids mTmpUidRecords;

    public static final String oomAdjReasonToString(int i) {
        switch (i) {
            case 0:
                return "updateOomAdj_meh";
            case 1:
                return "updateOomAdj_activityChange";
            case 2:
                return "updateOomAdj_finishReceiver";
            case 3:
                return "updateOomAdj_startReceiver";
            case 4:
                return "updateOomAdj_bindService";
            case 5:
                return "updateOomAdj_unbindService";
            case 6:
                return "updateOomAdj_startService";
            case 7:
                return "updateOomAdj_getProvider";
            case 8:
                return "updateOomAdj_removeProvider";
            case 9:
                return "updateOomAdj_uiVisibility";
            case 10:
                return "updateOomAdj_allowlistChange";
            case 11:
                return "updateOomAdj_processBegin";
            case 12:
                return "updateOomAdj_processEnd";
            case 13:
                return "updateOomAdj_shortFgs";
            default:
                return "_unknown";
        }
    }

    @VisibleForTesting
    public boolean isChangeEnabled(int i, ApplicationInfo applicationInfo, boolean z) {
        PlatformCompatCache.getInstance();
        return PlatformCompatCache.isChangeEnabled(i, applicationInfo, z);
    }

    public OomAdjuster(ActivityManagerService activityManagerService, ProcessList processList, ActiveUids activeUids) {
        this(activityManagerService, processList, activeUids, createAdjusterThread());
    }

    public static ServiceThread createAdjusterThread() {
        ServiceThread serviceThread = new ServiceThread("OomAdjuster", -10, false);
        serviceThread.start();
        return serviceThread;
    }

    public OomAdjuster(ActivityManagerService activityManagerService, ProcessList processList, ActiveUids activeUids, ServiceThread serviceThread) {
        this.mTmpLong = new long[3];
        this.mAdjSeq = 0;
        this.mNumServiceProcs = 0;
        this.mNewNumAServiceProcs = 0;
        this.mNewNumServiceProcs = 0;
        this.mNumNonCachedProcs = 0;
        this.mNumCachedHiddenProcs = 0;
        this.mTmpSchedGroup = new int[1];
        this.mTmpProcessList = new ArrayList<>();
        this.mTmpBecameIdle = new ArrayList<>();
        this.mPendingProcessSet = new ArraySet<>();
        this.mProcessesInCycle = new ArraySet<>();
        this.mOomAdjUpdateOngoing = false;
        this.mPendingFullOomAdjUpdate = false;
        this.mLastFreeSwapPercent = 1.0d;
        this.mTmpComputeOomAdjWindowCallback = new ComputeOomAdjWindowCallback();
        this.mService = activityManagerService;
        this.mProcessList = processList;
        this.mProcLock = activityManagerService.mProcLock;
        this.mActiveUids = activeUids;
        this.mLocalPowerManager = (PowerManagerInternal) LocalServices.getService(PowerManagerInternal.class);
        this.mConstants = activityManagerService.mConstants;
        this.mCachedAppOptimizer = new CachedAppOptimizer(activityManagerService);
        this.mCacheOomRanker = new CacheOomRanker(activityManagerService);
        this.mProcessGroupHandler = new Handler(serviceThread.getLooper(), new Handler.Callback() { // from class: com.android.server.am.OomAdjuster$$ExternalSyntheticLambda3
            @Override // android.os.Handler.Callback
            public final boolean handleMessage(Message message) {
                boolean lambda$new$0;
                lambda$new$0 = OomAdjuster.lambda$new$0(message);
                return lambda$new$0;
            }
        });
        this.mTmpUidRecords = new ActiveUids(activityManagerService, false);
        this.mTmpQueue = new ArrayDeque<>(this.mConstants.CUR_MAX_CACHED_PROCESSES << 1);
        this.mNumSlots = 10;
    }

    public static /* synthetic */ boolean lambda$new$0(Message message) {
        int i = message.arg1;
        int i2 = message.arg2;
        if (i == ActivityManagerService.MY_PID) {
            return true;
        }
        if (Trace.isTagEnabled(64L)) {
            Trace.traceBegin(64L, "setProcessGroup " + message.obj + " to " + i2);
        }
        try {
            Process.setProcessGroup(i, i2);
        } catch (Exception unused) {
        } catch (Throwable th) {
            Trace.traceEnd(64L);
            throw th;
        }
        Trace.traceEnd(64L);
        return true;
    }

    public void initSettings() {
        this.mCachedAppOptimizer.init();
        this.mCacheOomRanker.init(ActivityThread.currentApplication().getMainExecutor());
        if (this.mService.mConstants.KEEP_WARMING_SERVICES.size() > 0) {
            this.mService.mContext.registerReceiverForAllUsers(new BroadcastReceiver() { // from class: com.android.server.am.OomAdjuster.1
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context, Intent intent) {
                    synchronized (OomAdjuster.this.mService) {
                        try {
                            ActivityManagerService.boostPriorityForLockedSection();
                            OomAdjuster.this.handleUserSwitchedLocked();
                        } catch (Throwable th) {
                            ActivityManagerService.resetPriorityAfterLockedSection();
                            throw th;
                        }
                    }
                    ActivityManagerService.resetPriorityAfterLockedSection();
                }
            }, new IntentFilter("android.intent.action.USER_SWITCHED"), null, this.mService.mHandler);
        }
    }

    @GuardedBy({"mService"})
    @VisibleForTesting
    public void handleUserSwitchedLocked() {
        this.mProcessList.forEachLruProcessesLOSP(false, new Consumer() { // from class: com.android.server.am.OomAdjuster$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                OomAdjuster.this.updateKeepWarmIfNecessaryForProcessLocked((ProcessRecord) obj);
            }
        });
    }

    @GuardedBy({"mService"})
    public final void updateKeepWarmIfNecessaryForProcessLocked(ProcessRecord processRecord) {
        boolean z;
        ArraySet<ComponentName> arraySet = this.mService.mConstants.KEEP_WARMING_SERVICES;
        PackageList pkgList = processRecord.getPkgList();
        int size = arraySet.size() - 1;
        while (true) {
            if (size < 0) {
                z = false;
                break;
            } else if (pkgList.containsKey(arraySet.valueAt(size).getPackageName())) {
                z = true;
                break;
            } else {
                size--;
            }
        }
        if (z) {
            ProcessServiceRecord processServiceRecord = processRecord.mServices;
            for (int numberOfRunningServices = processServiceRecord.numberOfRunningServices() - 1; numberOfRunningServices >= 0; numberOfRunningServices--) {
                processServiceRecord.getRunningServiceAt(numberOfRunningServices).updateKeepWarmLocked();
            }
        }
    }

    @GuardedBy({"mService", "mProcLock"})
    public final boolean performUpdateOomAdjLSP(ProcessRecord processRecord, int i, ProcessRecord processRecord2, long j, int i2) {
        if (processRecord.getThread() == null) {
            return false;
        }
        processRecord.mState.resetCachedInfo();
        processRecord.mState.setCurBoundByNonBgRestrictedApp(false);
        UidRecord uidRecord = processRecord.getUidRecord();
        if (uidRecord != null) {
            uidRecord.reset();
        }
        this.mPendingProcessSet.remove(processRecord);
        this.mProcessesInCycle.clear();
        computeOomAdjLSP(processRecord, i, processRecord2, false, j, false, true);
        if (!this.mProcessesInCycle.isEmpty()) {
            for (int size = this.mProcessesInCycle.size() - 1; size >= 0; size--) {
                this.mProcessesInCycle.valueAt(size).mState.setCompletedAdjSeq(this.mAdjSeq - 1);
            }
            return true;
        }
        if (uidRecord != null) {
            uidRecord.forEachProcess(new Consumer() { // from class: com.android.server.am.OomAdjuster$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    OomAdjuster.this.updateAppUidRecIfNecessaryLSP((ProcessRecord) obj);
                }
            });
            if (uidRecord.getCurProcState() != 20 && (uidRecord.getSetProcState() != uidRecord.getCurProcState() || uidRecord.getSetCapability() != uidRecord.getCurCapability() || uidRecord.isSetAllowListed() != uidRecord.isCurAllowListed())) {
                ActiveUids activeUids = this.mTmpUidRecords;
                activeUids.clear();
                activeUids.put(uidRecord.getUid(), uidRecord);
                updateUidsLSP(activeUids, SystemClock.elapsedRealtime());
            }
        }
        return applyOomAdjLSP(processRecord, false, j, SystemClock.elapsedRealtime(), i2);
    }

    @GuardedBy({"mService"})
    public void updateOomAdjLocked(int i) {
        synchronized (this.mProcLock) {
            try {
                ActivityManagerService.boostPriorityForProcLockedSection();
                updateOomAdjLSP(i);
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        ActivityManagerService.resetPriorityAfterProcLockedSection();
    }

    @GuardedBy({"mService", "mProcLock"})
    public final void updateOomAdjLSP(int i) {
        if (checkAndEnqueueOomAdjTargetLocked(null)) {
            return;
        }
        try {
            this.mOomAdjUpdateOngoing = true;
            performUpdateOomAdjLSP(i);
        } finally {
            this.mOomAdjUpdateOngoing = false;
            updateOomAdjPendingTargetsLocked(i);
        }
    }

    @GuardedBy({"mService", "mProcLock"})
    public final void performUpdateOomAdjLSP(int i) {
        ProcessRecord topApp = this.mService.getTopApp();
        this.mPendingProcessSet.clear();
        AppProfiler appProfiler = this.mService.mAppProfiler;
        appProfiler.mHasHomeProcess = false;
        appProfiler.mHasPreviousProcess = false;
        updateOomAdjInnerLSP(i, topApp, null, null, true, true);
    }

    @GuardedBy({"mService"})
    public boolean updateOomAdjLocked(ProcessRecord processRecord, int i) {
        boolean updateOomAdjLSP;
        synchronized (this.mProcLock) {
            try {
                ActivityManagerService.boostPriorityForProcLockedSection();
                updateOomAdjLSP = updateOomAdjLSP(processRecord, i);
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        ActivityManagerService.resetPriorityAfterProcLockedSection();
        return updateOomAdjLSP;
    }

    @GuardedBy({"mService", "mProcLock"})
    public final boolean updateOomAdjLSP(ProcessRecord processRecord, int i) {
        if (processRecord == null || !this.mConstants.OOMADJ_UPDATE_QUICK) {
            updateOomAdjLSP(i);
            return true;
        } else if (checkAndEnqueueOomAdjTargetLocked(processRecord)) {
            return true;
        } else {
            try {
                this.mOomAdjUpdateOngoing = true;
                return performUpdateOomAdjLSP(processRecord, i);
            } finally {
                this.mOomAdjUpdateOngoing = false;
                updateOomAdjPendingTargetsLocked(i);
            }
        }
    }

    @GuardedBy({"mService", "mProcLock"})
    public final boolean performUpdateOomAdjLSP(ProcessRecord processRecord, int i) {
        ProcessRecord topApp = this.mService.getTopApp();
        Trace.traceBegin(64L, oomAdjReasonToString(i));
        this.mService.mOomAdjProfiler.oomAdjStarted();
        this.mAdjSeq++;
        ProcessStateRecord processStateRecord = processRecord.mState;
        boolean isCached = processStateRecord.isCached();
        int curRawAdj = processStateRecord.getCurRawAdj();
        int i2 = curRawAdj >= 900 ? curRawAdj : 1001;
        boolean isProcStateBackground = ActivityManager.isProcStateBackground(processStateRecord.getSetProcState());
        int setCapability = processStateRecord.getSetCapability();
        processStateRecord.setContainsCycle(false);
        processStateRecord.setProcStateChanged(false);
        processStateRecord.resetCachedInfo();
        processStateRecord.setCurBoundByNonBgRestrictedApp(false);
        this.mPendingProcessSet.remove(processRecord);
        processRecord.mOptRecord.setLastOomAdjChangeReason(i);
        boolean performUpdateOomAdjLSP = performUpdateOomAdjLSP(processRecord, i2, topApp, SystemClock.uptimeMillis(), i);
        if (!performUpdateOomAdjLSP || (isCached == processStateRecord.isCached() && curRawAdj != -10000 && this.mProcessesInCycle.isEmpty() && setCapability == processStateRecord.getCurCapability() && isProcStateBackground == ActivityManager.isProcStateBackground(processStateRecord.getSetProcState()))) {
            this.mProcessesInCycle.clear();
            this.mService.mOomAdjProfiler.oomAdjEnded();
            Trace.traceEnd(64L);
            return performUpdateOomAdjLSP;
        }
        ArrayList<ProcessRecord> arrayList = this.mTmpProcessList;
        ActiveUids activeUids = this.mTmpUidRecords;
        this.mPendingProcessSet.add(processRecord);
        for (int size = this.mProcessesInCycle.size() - 1; size >= 0; size--) {
            this.mPendingProcessSet.add(this.mProcessesInCycle.valueAt(size));
        }
        this.mProcessesInCycle.clear();
        boolean collectReachableProcessesLocked = collectReachableProcessesLocked(this.mPendingProcessSet, arrayList, activeUids);
        this.mPendingProcessSet.clear();
        if (!collectReachableProcessesLocked) {
            processStateRecord.setReachable(false);
            arrayList.remove(processRecord);
        }
        if (arrayList.size() > 0) {
            this.mAdjSeq--;
            updateOomAdjInnerLSP(i, topApp, arrayList, activeUids, collectReachableProcessesLocked, false);
        } else if (processStateRecord.getCurRawAdj() == 1001) {
            arrayList.add(processRecord);
            assignCachedAdjIfNecessary(arrayList);
            applyOomAdjLSP(processRecord, false, SystemClock.uptimeMillis(), SystemClock.elapsedRealtime(), i);
        }
        this.mTmpProcessList.clear();
        this.mService.mOomAdjProfiler.oomAdjEnded();
        Trace.traceEnd(64L);
        return true;
    }

    @GuardedBy({"mService"})
    public final boolean collectReachableProcessesLocked(ArraySet<ProcessRecord> arraySet, ArrayList<ProcessRecord> arrayList, ActiveUids activeUids) {
        ArrayDeque<ProcessRecord> arrayDeque = this.mTmpQueue;
        arrayDeque.clear();
        arrayList.clear();
        int size = arraySet.size();
        for (int i = 0; i < size; i++) {
            ProcessRecord valueAt = arraySet.valueAt(i);
            valueAt.mState.setReachable(true);
            arrayDeque.offer(valueAt);
        }
        activeUids.clear();
        boolean z = false;
        for (ProcessRecord poll = arrayDeque.poll(); poll != null; poll = arrayDeque.poll()) {
            arrayList.add(poll);
            UidRecord uidRecord = poll.getUidRecord();
            if (uidRecord != null) {
                activeUids.put(uidRecord.getUid(), uidRecord);
            }
            ProcessServiceRecord processServiceRecord = poll.mServices;
            for (int numberOfConnections = processServiceRecord.numberOfConnections() - 1; numberOfConnections >= 0; numberOfConnections--) {
                ConnectionRecord connectionAt = processServiceRecord.getConnectionAt(numberOfConnections);
                ProcessRecord processRecord = connectionAt.hasFlag(2) ? connectionAt.binding.service.isolationHostProc : connectionAt.binding.service.app;
                if (processRecord != null && processRecord != poll && (processRecord.mState.getMaxAdj() < -900 || processRecord.mState.getMaxAdj() >= 0)) {
                    z |= processRecord.mState.isReachable();
                    if (!processRecord.mState.isReachable() && (!connectionAt.hasFlag(32) || !connectionAt.notHasFlag(134217856))) {
                        arrayDeque.offer(processRecord);
                        processRecord.mState.setReachable(true);
                    }
                }
            }
            ProcessProviderRecord processProviderRecord = poll.mProviders;
            for (int numberOfProviderConnections = processProviderRecord.numberOfProviderConnections() - 1; numberOfProviderConnections >= 0; numberOfProviderConnections--) {
                ProcessRecord processRecord2 = processProviderRecord.getProviderConnectionAt(numberOfProviderConnections).provider.proc;
                if (processRecord2 != null && processRecord2 != poll && (processRecord2.mState.getMaxAdj() < -900 || processRecord2.mState.getMaxAdj() >= 0)) {
                    z |= processRecord2.mState.isReachable();
                    if (!processRecord2.mState.isReachable()) {
                        arrayDeque.offer(processRecord2);
                        processRecord2.mState.setReachable(true);
                    }
                }
            }
            List<ProcessRecord> sdkSandboxProcessesForAppLocked = this.mProcessList.getSdkSandboxProcessesForAppLocked(poll.uid);
            for (int size2 = (sdkSandboxProcessesForAppLocked != null ? sdkSandboxProcessesForAppLocked.size() : 0) - 1; size2 >= 0; size2--) {
                ProcessRecord processRecord3 = sdkSandboxProcessesForAppLocked.get(size2);
                z |= processRecord3.mState.isReachable();
                if (!processRecord3.mState.isReachable()) {
                    arrayDeque.offer(processRecord3);
                    processRecord3.mState.setReachable(true);
                }
            }
            if (poll.isSdkSandbox) {
                for (int numberOfRunningServices = processServiceRecord.numberOfRunningServices() - 1; numberOfRunningServices >= 0; numberOfRunningServices--) {
                    ArrayMap<IBinder, ArrayList<ConnectionRecord>> connections = processServiceRecord.getRunningServiceAt(numberOfRunningServices).getConnections();
                    for (int size3 = connections.size() - 1; size3 >= 0; size3--) {
                        ArrayList<ConnectionRecord> valueAt2 = connections.valueAt(size3);
                        for (int size4 = valueAt2.size() - 1; size4 >= 0; size4--) {
                            ProcessRecord processRecord4 = valueAt2.get(size4).binding.attributedClient;
                            if (processRecord4 != null && processRecord4 != poll && ((processRecord4.mState.getMaxAdj() < -900 || processRecord4.mState.getMaxAdj() >= 0) && !processRecord4.mState.isReachable())) {
                                arrayDeque.offer(processRecord4);
                                processRecord4.mState.setReachable(true);
                            }
                        }
                    }
                }
            }
        }
        int size5 = arrayList.size();
        if (size5 > 0) {
            int i2 = 0;
            for (int i3 = size5 - 1; i2 < i3; i3--) {
                arrayList.set(i2, arrayList.get(i3));
                arrayList.set(i3, arrayList.get(i2));
                i2++;
            }
        }
        return z;
    }

    @GuardedBy({"mService"})
    public void enqueueOomAdjTargetLocked(ProcessRecord processRecord) {
        if (processRecord != null) {
            this.mPendingProcessSet.add(processRecord);
        }
    }

    @GuardedBy({"mService"})
    public void removeOomAdjTargetLocked(ProcessRecord processRecord, boolean z) {
        if (processRecord != null) {
            this.mPendingProcessSet.remove(processRecord);
            if (z) {
                PlatformCompatCache.getInstance().invalidate(processRecord.info);
            }
        }
    }

    @GuardedBy({"mService"})
    public final boolean checkAndEnqueueOomAdjTargetLocked(ProcessRecord processRecord) {
        if (this.mOomAdjUpdateOngoing) {
            if (processRecord != null) {
                this.mPendingProcessSet.add(processRecord);
            } else {
                this.mPendingFullOomAdjUpdate = true;
            }
            return true;
        }
        return false;
    }

    @GuardedBy({"mService"})
    public void updateOomAdjPendingTargetsLocked(int i) {
        if (this.mPendingFullOomAdjUpdate) {
            this.mPendingFullOomAdjUpdate = false;
            this.mPendingProcessSet.clear();
            updateOomAdjLocked(i);
        } else if (this.mPendingProcessSet.isEmpty() || this.mOomAdjUpdateOngoing) {
        } else {
            try {
                this.mOomAdjUpdateOngoing = true;
                performUpdateOomAdjPendingTargetsLocked(i);
            } finally {
                this.mOomAdjUpdateOngoing = false;
                updateOomAdjPendingTargetsLocked(i);
            }
        }
    }

    @GuardedBy({"mService"})
    public final void performUpdateOomAdjPendingTargetsLocked(int i) {
        ProcessRecord topApp = this.mService.getTopApp();
        Trace.traceBegin(64L, oomAdjReasonToString(i));
        this.mService.mOomAdjProfiler.oomAdjStarted();
        ArrayList<ProcessRecord> arrayList = this.mTmpProcessList;
        ActiveUids activeUids = this.mTmpUidRecords;
        collectReachableProcessesLocked(this.mPendingProcessSet, arrayList, activeUids);
        this.mPendingProcessSet.clear();
        synchronized (this.mProcLock) {
            try {
                ActivityManagerService.boostPriorityForProcLockedSection();
                updateOomAdjInnerLSP(i, topApp, arrayList, activeUids, true, false);
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        ActivityManagerService.resetPriorityAfterProcLockedSection();
        arrayList.clear();
        this.mService.mOomAdjProfiler.oomAdjEnded();
        Trace.traceEnd(64L);
    }

    @GuardedBy({"mService", "mProcLock"})
    public final void updateOomAdjInnerLSP(int i, ProcessRecord processRecord, ArrayList<ProcessRecord> arrayList, ActiveUids activeUids, boolean z, boolean z2) {
        ActiveUids activeUids2;
        ArrayList<ProcessRecord> arrayList2;
        int i2;
        int i3;
        int i4;
        ActiveUids activeUids3;
        ArrayList<ProcessRecord> arrayList3;
        boolean z3;
        if (z2) {
            Trace.traceBegin(64L, oomAdjReasonToString(i));
            this.mService.mOomAdjProfiler.oomAdjStarted();
        }
        long uptimeMillis = SystemClock.uptimeMillis();
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long j = uptimeMillis - this.mConstants.mMaxEmptyTimeMillis;
        boolean z4 = false;
        boolean z5 = arrayList == null;
        ArrayList<ProcessRecord> lruProcessesLOSP = z5 ? this.mProcessList.getLruProcessesLOSP() : arrayList;
        int size = lruProcessesLOSP.size();
        if (activeUids == null) {
            int size2 = this.mActiveUids.size();
            ActiveUids activeUids4 = this.mTmpUidRecords;
            activeUids4.clear();
            for (int i5 = 0; i5 < size2; i5++) {
                UidRecord valueAt = this.mActiveUids.valueAt(i5);
                activeUids4.put(valueAt.getUid(), valueAt);
            }
            activeUids2 = activeUids4;
        } else {
            activeUids2 = activeUids;
        }
        for (int size3 = activeUids2.size() - 1; size3 >= 0; size3--) {
            activeUids2.valueAt(size3).reset();
        }
        this.mAdjSeq++;
        if (z5) {
            this.mNewNumServiceProcs = 0;
            this.mNewNumAServiceProcs = 0;
        }
        boolean z6 = z5 || z;
        int i6 = size - 1;
        for (int i7 = i6; i7 >= 0; i7--) {
            ProcessStateRecord processStateRecord = lruProcessesLOSP.get(i7).mState;
            processStateRecord.setReachable(false);
            if (processStateRecord.getAdjSeq() != this.mAdjSeq) {
                processStateRecord.setContainsCycle(false);
                processStateRecord.setCurRawProcState(19);
                processStateRecord.setCurRawAdj(1001);
                processStateRecord.setSetCapability(0);
                processStateRecord.resetCachedInfo();
                processStateRecord.setCurBoundByNonBgRestrictedApp(false);
            }
        }
        this.mProcessesInCycle.clear();
        int i8 = i6;
        boolean z7 = 0;
        while (i8 >= 0) {
            ProcessRecord processRecord2 = lruProcessesLOSP.get(i8);
            ProcessStateRecord processStateRecord2 = processRecord2.mState;
            if (processRecord2.isKilledByAm() || processRecord2.getThread() == null) {
                i3 = i8;
                i4 = size;
                activeUids3 = activeUids2;
                arrayList3 = lruProcessesLOSP;
                z3 = z4;
            } else {
                processStateRecord2.setProcStateChanged(z4);
                processRecord2.mOptRecord.setLastOomAdjChangeReason(i);
                i3 = i8;
                i4 = size;
                arrayList3 = lruProcessesLOSP;
                activeUids3 = activeUids2;
                z3 = z4;
                computeOomAdjLSP(processRecord2, 1001, processRecord, z5, uptimeMillis, false, z6);
                processStateRecord2.setCompletedAdjSeq(this.mAdjSeq);
                z7 |= processStateRecord2.containsCycle();
            }
            i8 = i3 - 1;
            activeUids2 = activeUids3;
            size = i4;
            z4 = z3;
            lruProcessesLOSP = arrayList3;
        }
        int i9 = size;
        ActiveUids activeUids5 = activeUids2;
        ArrayList<ProcessRecord> arrayList4 = lruProcessesLOSP;
        boolean z8 = z4;
        if (this.mCacheOomRanker.useOomReranking()) {
            this.mCacheOomRanker.reRankLruCachedAppsLSP(this.mProcessList.getLruProcessesLSP(), this.mProcessList.getLruProcessServiceStartLOSP());
        }
        assignCachedAdjIfNecessary(this.mProcessList.getLruProcessesLOSP());
        if (z6) {
            int i10 = z8 ? 1 : 0;
            while (z7 != 0 && i10 < 10) {
                int i11 = i10 + 1;
                int i12 = z8 ? 1 : 0;
                while (i12 < i9) {
                    ArrayList<ProcessRecord> arrayList5 = arrayList4;
                    ProcessRecord processRecord3 = arrayList5.get(i12);
                    ProcessStateRecord processStateRecord3 = processRecord3.mState;
                    if (!processRecord3.isKilledByAm() && processRecord3.getThread() != null && processStateRecord3.containsCycle()) {
                        processStateRecord3.decAdjSeq();
                        processStateRecord3.decCompletedAdjSeq();
                    }
                    i12++;
                    arrayList4 = arrayList5;
                }
                ArrayList<ProcessRecord> arrayList6 = arrayList4;
                int i13 = z8 ? 1 : 0;
                z7 = i13;
                while (i13 < i9) {
                    ProcessRecord processRecord4 = arrayList6.get(i13);
                    ProcessStateRecord processStateRecord4 = processRecord4.mState;
                    if (processRecord4.isKilledByAm() || processRecord4.getThread() == null || !processStateRecord4.containsCycle()) {
                        arrayList2 = arrayList6;
                        i2 = i13;
                    } else {
                        arrayList2 = arrayList6;
                        i2 = i13;
                        if (computeOomAdjLSP(processRecord4, processStateRecord4.getCurRawAdj(), processRecord, true, uptimeMillis, true, true)) {
                            z7 = 1;
                        }
                    }
                    i13 = i2 + 1;
                    arrayList6 = arrayList2;
                }
                arrayList4 = arrayList6;
                i10 = i11;
            }
        }
        this.mProcessesInCycle.clear();
        this.mNumNonCachedProcs = z8 ? 1 : 0;
        this.mNumCachedHiddenProcs = z8 ? 1 : 0;
        boolean updateAndTrimProcessLSP = updateAndTrimProcessLSP(uptimeMillis, elapsedRealtime, j, activeUids5, i);
        this.mNumServiceProcs = this.mNewNumServiceProcs;
        ActivityManagerService activityManagerService = this.mService;
        if (activityManagerService.mAlwaysFinishActivities) {
            activityManagerService.mAtmInternal.scheduleDestroyAllActivities("always-finish");
        }
        if (updateAndTrimProcessLSP) {
            ActivityManagerService activityManagerService2 = this.mService;
            activityManagerService2.mAppProfiler.requestPssAllProcsLPr(uptimeMillis, z8, activityManagerService2.mProcessStats.isMemFactorLowered());
        }
        updateUidsLSP(activeUids5, elapsedRealtime);
        synchronized (this.mService.mProcessStats.mLock) {
            long uptimeMillis2 = SystemClock.uptimeMillis();
            if (this.mService.mProcessStats.shouldWriteNowLocked(uptimeMillis2)) {
                ActivityManagerService activityManagerService3 = this.mService;
                activityManagerService3.mHandler.post(new ActivityManagerService.ProcStatsRunnable(activityManagerService3, activityManagerService3.mProcessStats));
            }
            this.mService.mProcessStats.updateTrackingAssociationsLocked(this.mAdjSeq, uptimeMillis2);
        }
        if (z2) {
            this.mService.mOomAdjProfiler.oomAdjEnded();
            Trace.traceEnd(64L);
        }
    }

    @GuardedBy({"mService", "mProcLock"})
    public final void assignCachedAdjIfNecessary(ArrayList<ProcessRecord> arrayList) {
        int i;
        boolean z;
        int i2;
        int i3;
        ArrayList<ProcessRecord> arrayList2 = arrayList;
        int size = arrayList.size();
        ActivityManagerConstants activityManagerConstants = this.mConstants;
        int i4 = 1001;
        if (activityManagerConstants.USE_TIERED_CACHED_ADJ) {
            long uptimeMillis = SystemClock.uptimeMillis();
            for (int i5 = size - 1; i5 >= 0; i5--) {
                ProcessRecord processRecord = arrayList2.get(i5);
                ProcessStateRecord processStateRecord = processRecord.mState;
                ProcessCachedOptimizerRecord processCachedOptimizerRecord = processRecord.mOptRecord;
                if (!processRecord.isKilledByAm() && processRecord.getThread() != null && processStateRecord.getCurAdj() >= 1001) {
                    ProcessServiceRecord processServiceRecord = processRecord.mServices;
                    if (processCachedOptimizerRecord == null || !processCachedOptimizerRecord.isFreezeExempt()) {
                        i3 = (processStateRecord.getSetAdj() < 900 || processStateRecord.getLastStateTime() + this.mConstants.TIERED_CACHED_ADJ_DECAY_TIME >= uptimeMillis) ? 910 : 950;
                    } else {
                        i3 = 900;
                    }
                    processStateRecord.setCurRawAdj(i3);
                    processStateRecord.setCurAdj(processServiceRecord.modifyRawOomAdj(i3));
                }
            }
            return;
        }
        int i6 = activityManagerConstants.CUR_MAX_CACHED_PROCESSES - activityManagerConstants.CUR_MAX_EMPTY_PROCESSES;
        int i7 = this.mNumCachedHiddenProcs;
        int i8 = (size - this.mNumNonCachedProcs) - i7;
        if (i8 <= i6) {
            i6 = i8;
        }
        int i9 = i7 > 0 ? (i7 + this.mNumSlots) - 1 : 1;
        int i10 = this.mNumSlots;
        int i11 = i9 / i10;
        if (i11 < 1) {
            i11 = 1;
        }
        int i12 = ((i6 + i10) - 1) / i10;
        if (i12 < 1) {
            i12 = 1;
        }
        int i13 = -1;
        int i14 = 915;
        int i15 = 0;
        int i16 = 0;
        int i17 = 0;
        int i18 = 0;
        int i19 = 905;
        int i20 = 900;
        int i21 = 910;
        int i22 = size - 1;
        int i23 = -1;
        while (i22 >= 0) {
            ProcessRecord processRecord2 = arrayList2.get(i22);
            ProcessStateRecord processStateRecord2 = processRecord2.mState;
            if (processRecord2.isKilledByAm() || processRecord2.getThread() == null || processStateRecord2.getCurAdj() < i4) {
                i = i22;
            } else {
                ProcessServiceRecord processServiceRecord2 = processRecord2.mServices;
                switch (processStateRecord2.getCurProcState()) {
                    case 16:
                    case 17:
                    case 18:
                        int connectionGroup = processServiceRecord2.getConnectionGroup();
                        i = i22;
                        if (connectionGroup != 0) {
                            int connectionImportance = processServiceRecord2.getConnectionImportance();
                            int i24 = processRecord2.uid;
                            if (i15 == i24 && i16 == connectionGroup) {
                                if (connectionImportance > i17) {
                                    if (i20 < i21 && i20 < 999) {
                                        i18++;
                                    }
                                    i17 = connectionImportance;
                                }
                                z = true;
                                if (!z || i20 == i21) {
                                    i2 = i21;
                                    i21 = i20;
                                } else {
                                    i23++;
                                    if (i23 >= i11) {
                                        int i25 = i21 + 10;
                                        if (i25 > 999) {
                                            i23 = 0;
                                            i18 = 0;
                                            i2 = 999;
                                        } else {
                                            i2 = i25;
                                            i23 = 0;
                                        }
                                    } else {
                                        i2 = i21;
                                        i21 = i20;
                                    }
                                    i18 = 0;
                                }
                                int i26 = i21 + i18;
                                processStateRecord2.setCurRawAdj(i26);
                                processStateRecord2.setCurAdj(processServiceRecord2.modifyRawOomAdj(i26));
                                i20 = i21;
                                i21 = i2;
                                continue;
                            } else {
                                i15 = i24;
                                i16 = connectionGroup;
                                i17 = connectionImportance;
                            }
                        }
                        z = false;
                        if (z) {
                        }
                        i2 = i21;
                        i21 = i20;
                        int i262 = i21 + i18;
                        processStateRecord2.setCurRawAdj(i262);
                        processStateRecord2.setCurAdj(processServiceRecord2.modifyRawOomAdj(i262));
                        i20 = i21;
                        i21 = i2;
                        continue;
                    default:
                        i = i22;
                        if (i19 != i14 && (i13 = i13 + 1) >= i12) {
                            int i27 = i14 + 10;
                            i19 = i14;
                            if (i27 > 999) {
                                i13 = 0;
                                i14 = 999;
                            } else {
                                i14 = i27;
                                i13 = 0;
                            }
                        }
                        processStateRecord2.setCurRawAdj(i19);
                        processStateRecord2.setCurAdj(processServiceRecord2.modifyRawOomAdj(i19));
                        continue;
                }
            }
            i22 = i - 1;
            arrayList2 = arrayList;
            i4 = 1001;
        }
    }

    public static double getFreeSwapPercent() {
        return CachedAppOptimizer.getFreeSwapPercent();
    }

    @GuardedBy({"mService", "mProcLock"})
    public final boolean updateAndTrimProcessLSP(long j, long j2, long j3, ActiveUids activeUids, int i) {
        double d;
        int i2;
        double d2;
        int i3;
        double d3;
        boolean z;
        boolean z2;
        int i4;
        ProcessRecord processRecord;
        int i5;
        int i6;
        int i7;
        int i8;
        ProcessRecord processRecord2;
        int i9;
        ArrayList<ProcessRecord> lruProcessesLOSP = this.mProcessList.getLruProcessesLOSP();
        int size = lruProcessesLOSP.size();
        boolean shouldKillExcessiveProcesses = shouldKillExcessiveProcesses(j);
        if (!shouldKillExcessiveProcesses && this.mNextNoKillDebugMessageTime < j) {
            Slog.d("OomAdjuster", "Not killing cached processes");
            this.mNextNoKillDebugMessageTime = j + 5000;
        }
        int i10 = shouldKillExcessiveProcesses ? this.mConstants.CUR_MAX_EMPTY_PROCESSES : Integer.MAX_VALUE;
        int i11 = shouldKillExcessiveProcesses ? this.mConstants.CUR_MAX_CACHED_PROCESSES - i10 : Integer.MAX_VALUE;
        boolean z3 = ActivityManagerConstants.PROACTIVE_KILLS_ENABLED;
        double d4 = ActivityManagerConstants.LOW_SWAP_THRESHOLD_PERCENT;
        double freeSwapPercent = z3 ? getFreeSwapPercent() : 1.0d;
        boolean z4 = true;
        int i12 = size - 1;
        ProcessRecord processRecord3 = null;
        int i13 = 0;
        int i14 = 0;
        int i15 = 0;
        int i16 = 0;
        int i17 = 0;
        int i18 = 0;
        while (i12 >= 0) {
            ProcessRecord processRecord4 = lruProcessesLOSP.get(i12);
            ArrayList<ProcessRecord> arrayList = lruProcessesLOSP;
            ProcessStateRecord processStateRecord = processRecord4.mState;
            if (processRecord4.isKilledByAm() || processRecord4.getThread() == null || processRecord4.isPendingFinishAttach()) {
                i2 = i12;
                d2 = freeSwapPercent;
                i3 = i11;
                d3 = d4;
                int i19 = i13;
                z = z4;
                z2 = shouldKillExcessiveProcesses;
                i17 = i17;
                i16 = i16;
                i15 = i15;
                i4 = i19;
                processRecord3 = processRecord3;
            } else {
                int i20 = i14;
                if (processStateRecord.getCompletedAdjSeq() == this.mAdjSeq) {
                    i6 = i20;
                    d3 = d4;
                    i8 = i15;
                    i7 = i13;
                    z2 = shouldKillExcessiveProcesses;
                    processRecord2 = processRecord4;
                    i2 = i12;
                    d2 = freeSwapPercent;
                    processRecord = processRecord3;
                    i5 = i11;
                    z = true;
                    applyOomAdjLSP(processRecord4, true, j, j2, i);
                } else {
                    i2 = i12;
                    processRecord = processRecord3;
                    d2 = freeSwapPercent;
                    i5 = i11;
                    d3 = d4;
                    i6 = i20;
                    z = true;
                    i7 = i13;
                    i8 = i15;
                    z2 = shouldKillExcessiveProcesses;
                    processRecord2 = processRecord4;
                }
                ProcessServiceRecord processServiceRecord = processRecord2.mServices;
                int curProcState = processStateRecord.getCurProcState();
                if (curProcState == 16 || curProcState == 17) {
                    this.mNumCachedHiddenProcs += z ? 1 : 0;
                    i4 = i7 + 1;
                    int connectionGroup = processServiceRecord.getConnectionGroup();
                    if (connectionGroup != 0) {
                        int i21 = i16;
                        if (i21 == processRecord2.info.uid && (i9 = i17) == connectionGroup) {
                            i18++;
                            i17 = i9;
                            i16 = i21;
                        } else {
                            i17 = connectionGroup;
                            i16 = processRecord2.info.uid;
                        }
                    } else {
                        i16 = 0;
                        i17 = 0;
                    }
                    i3 = i5;
                    if (i4 - i18 > i3) {
                        processRecord2.killLocked("cached #" + i4, "too many cached", 13, 2, true);
                    } else if (z3) {
                        processRecord3 = processRecord2;
                        i15 = i8;
                    }
                    i15 = i8;
                    processRecord3 = processRecord;
                } else {
                    if (curProcState == 19) {
                        if (i8 <= this.mConstants.CUR_TRIM_EMPTY_PROCESSES || processRecord2.getLastActivityTime() >= j3) {
                            i15 = i8 + 1;
                            if (i15 > i10) {
                                processRecord2.killLocked("empty #" + i15, "too many empty", 13, 3, true);
                            } else if (z3) {
                                processRecord3 = processRecord2;
                                i4 = i7;
                                i3 = i5;
                            }
                            i4 = i7;
                            processRecord3 = processRecord;
                            i3 = i5;
                        } else {
                            processRecord2.killLocked("empty for " + ((j - processRecord2.getLastActivityTime()) / 1000) + "s", "empty for too long", 13, 4, true);
                        }
                    } else {
                        this.mNumNonCachedProcs += z ? 1 : 0;
                    }
                    i15 = i8;
                    i4 = i7;
                    processRecord3 = processRecord;
                    i3 = i5;
                }
                if (processRecord2.isolated && processServiceRecord.numberOfRunningServices() <= 0 && processRecord2.getIsolatedEntryPoint() == null) {
                    processRecord2.killLocked("isolated not needed", 13, 17, z);
                } else if (processRecord2.isSdkSandbox && processServiceRecord.numberOfRunningServices() <= 0 && processRecord2.getActiveInstrumentation() == null) {
                    processRecord2.killLocked("sandbox not needed", 13, 28, z);
                } else {
                    updateAppUidRecLSP(processRecord2);
                }
                i14 = (processStateRecord.getCurProcState() < 14 || processRecord2.isKilledByAm()) ? i6 : i6 + 1;
            }
            i12 = i2 - 1;
            shouldKillExcessiveProcesses = z2;
            i13 = i4;
            z4 = z;
            lruProcessesLOSP = arrayList;
            d4 = d3;
            i11 = i3;
            freeSwapPercent = d2;
        }
        ProcessRecord processRecord5 = processRecord3;
        double d5 = freeSwapPercent;
        boolean z5 = z4;
        double d6 = d4;
        int i22 = i13;
        int i23 = i15;
        boolean z6 = shouldKillExcessiveProcesses;
        if (z3 && z6) {
            d = d5;
            if (d < d6 && processRecord5 != null && d < this.mLastFreeSwapPercent) {
                processRecord5.killLocked("swap low and too many cached", 13, 2, z5);
            }
        } else {
            d = d5;
        }
        this.mLastFreeSwapPercent = d;
        return this.mService.mAppProfiler.updateLowMemStateLSP(i22, i23, i14);
    }

    @GuardedBy({"mService", "mProcLock"})
    public final void updateAppUidRecIfNecessaryLSP(ProcessRecord processRecord) {
        if (processRecord.isKilledByAm() || processRecord.getThread() == null) {
            return;
        }
        if (processRecord.isolated && processRecord.mServices.numberOfRunningServices() <= 0 && processRecord.getIsolatedEntryPoint() == null) {
            return;
        }
        updateAppUidRecLSP(processRecord);
    }

    @GuardedBy({"mService", "mProcLock"})
    public final void updateAppUidRecLSP(ProcessRecord processRecord) {
        UidRecord uidRecord = processRecord.getUidRecord();
        if (uidRecord != null) {
            ProcessStateRecord processStateRecord = processRecord.mState;
            uidRecord.setEphemeral(processRecord.info.isInstantApp());
            if (uidRecord.getCurProcState() > processStateRecord.getCurProcState()) {
                uidRecord.setCurProcState(processStateRecord.getCurProcState());
            }
            if (processRecord.mServices.hasForegroundServices()) {
                uidRecord.setForegroundServices(true);
            }
            uidRecord.setCurCapability(uidRecord.getCurCapability() | processStateRecord.getCurCapability());
        }
    }

    @GuardedBy({"mService", "mProcLock"})
    public final void updateUidsLSP(ActiveUids activeUids, long j) {
        int i;
        this.mProcessList.incrementProcStateSeqAndNotifyAppsLOSP(activeUids);
        ArrayList<UidRecord> arrayList = this.mTmpBecameIdle;
        arrayList.clear();
        PowerManagerInternal powerManagerInternal = this.mLocalPowerManager;
        if (powerManagerInternal != null) {
            powerManagerInternal.startUidChanges();
        }
        for (int size = activeUids.size() - 1; size >= 0; size--) {
            UidRecord valueAt = activeUids.valueAt(size);
            if (valueAt.getCurProcState() != 20 && (valueAt.getSetProcState() != valueAt.getCurProcState() || valueAt.getSetCapability() != valueAt.getCurCapability() || valueAt.isSetAllowListed() != valueAt.isCurAllowListed() || valueAt.getProcAdjChanged())) {
                if (ActivityManager.isProcStateBackground(valueAt.getCurProcState()) && !valueAt.isCurAllowListed()) {
                    if (!ActivityManager.isProcStateBackground(valueAt.getSetProcState()) || valueAt.isSetAllowListed()) {
                        valueAt.setLastBackgroundTime(j);
                        if (!this.mService.mHandler.hasMessages(58)) {
                            this.mService.mHandler.sendEmptyMessageDelayed(58, this.mConstants.BACKGROUND_SETTLE_TIME);
                        }
                    }
                    if (!valueAt.isIdle() || valueAt.isSetIdle()) {
                        i = 0;
                    } else {
                        arrayList.add(valueAt);
                        i = 2;
                    }
                } else {
                    if (valueAt.isIdle()) {
                        EventLogTags.writeAmUidActive(valueAt.getUid());
                        valueAt.setIdle(false);
                        i = 4;
                    } else {
                        i = 0;
                    }
                    valueAt.setLastBackgroundTime(0L);
                }
                boolean z = valueAt.getSetProcState() > 11;
                boolean z2 = valueAt.getCurProcState() > 11;
                if (z != z2 || valueAt.getSetProcState() == 20) {
                    i |= z2 ? 8 : 16;
                }
                if (valueAt.getSetCapability() != valueAt.getCurCapability()) {
                    i |= 32;
                }
                if (valueAt.getSetProcState() != valueAt.getCurProcState()) {
                    i |= Integer.MIN_VALUE;
                }
                if (valueAt.getProcAdjChanged()) {
                    i |= 64;
                }
                valueAt.setSetProcState(valueAt.getCurProcState());
                valueAt.setSetCapability(valueAt.getCurCapability());
                valueAt.setSetAllowListed(valueAt.isCurAllowListed());
                valueAt.setSetIdle(valueAt.isIdle());
                valueAt.clearProcAdjChanged();
                int i2 = i & Integer.MIN_VALUE;
                if (i2 != 0 || (i & 32) != 0) {
                    this.mService.mAtmInternal.onUidProcStateChanged(valueAt.getUid(), valueAt.getSetProcState());
                }
                if (i != 0) {
                    this.mService.enqueueUidChangeLocked(valueAt, -1, i);
                }
                if (i2 != 0 || (i & 32) != 0) {
                    this.mService.noteUidProcessState(valueAt.getUid(), valueAt.getCurProcState(), valueAt.getCurCapability());
                }
                if (valueAt.hasForegroundServices()) {
                    this.mService.mServices.foregroundServiceProcStateChangedLocked(valueAt);
                }
            }
            this.mService.mInternal.deletePendingTopUid(valueAt.getUid(), j);
        }
        PowerManagerInternal powerManagerInternal2 = this.mLocalPowerManager;
        if (powerManagerInternal2 != null) {
            powerManagerInternal2.finishUidChanges();
        }
        int size2 = arrayList.size();
        if (size2 > 0) {
            for (int i3 = size2 - 1; i3 >= 0; i3--) {
                this.mService.mServices.stopInBackgroundLocked(arrayList.get(i3).getUid());
            }
        }
    }

    public final boolean shouldKillExcessiveProcesses(long j) {
        long lastUserUnlockingUptime = this.mService.mUserController.getLastUserUnlockingUptime();
        if (lastUserUnlockingUptime == 0) {
            return !this.mConstants.mNoKillCachedProcessesUntilBootCompleted;
        }
        return lastUserUnlockingUptime + this.mConstants.mNoKillCachedProcessesPostBootCompletedDurationMillis <= j;
    }

    /* renamed from: com.android.server.am.OomAdjuster$ComputeOomAdjWindowCallback */
    /* loaded from: classes.dex */
    public final class ComputeOomAdjWindowCallback implements WindowProcessController.ComputeOomAdjCallback {
        public int adj;
        public ProcessRecord app;
        public int appUid;
        public boolean foregroundActivities;
        public int logUid;
        public boolean mHasVisibleActivities;
        public ProcessStateRecord mState;
        public int procState;
        public int processStateCurTop;
        public int schedGroup;

        public ComputeOomAdjWindowCallback() {
        }

        public void initialize(ProcessRecord processRecord, int i, boolean z, boolean z2, int i2, int i3, int i4, int i5, int i6) {
            this.app = processRecord;
            this.adj = i;
            this.foregroundActivities = z;
            this.mHasVisibleActivities = z2;
            this.procState = i2;
            this.schedGroup = i3;
            this.appUid = i4;
            this.logUid = i5;
            this.processStateCurTop = i6;
            this.mState = processRecord.mState;
        }

        @Override // com.android.server.p014wm.WindowProcessController.ComputeOomAdjCallback
        public void onVisibleActivity() {
            if (this.adj > 100) {
                this.adj = 100;
                this.mState.setAdjType("vis-activity");
                if (this.logUid == this.appUid) {
                    OomAdjuster oomAdjuster = OomAdjuster.this;
                    oomAdjuster.reportOomAdjMessageLocked("ActivityManager", "Raise adj to vis-activity: " + this.app);
                }
            }
            int i = this.procState;
            int i2 = this.processStateCurTop;
            if (i > i2) {
                this.procState = i2;
                this.mState.setAdjType("vis-activity");
                if (this.logUid == this.appUid) {
                    OomAdjuster oomAdjuster2 = OomAdjuster.this;
                    oomAdjuster2.reportOomAdjMessageLocked("ActivityManager", "Raise procstate to vis-activity (top): " + this.app);
                }
            }
            if (this.schedGroup < 2) {
                this.schedGroup = 2;
            }
            this.mState.setCached(false);
            this.mState.setEmpty(false);
            this.foregroundActivities = true;
            this.mHasVisibleActivities = true;
        }

        @Override // com.android.server.p014wm.WindowProcessController.ComputeOomAdjCallback
        public void onPausedActivity() {
            if (this.adj > 200) {
                this.adj = 200;
                this.mState.setAdjType("pause-activity");
                if (this.logUid == this.appUid) {
                    OomAdjuster oomAdjuster = OomAdjuster.this;
                    oomAdjuster.reportOomAdjMessageLocked("ActivityManager", "Raise adj to pause-activity: " + this.app);
                }
            }
            int i = this.procState;
            int i2 = this.processStateCurTop;
            if (i > i2) {
                this.procState = i2;
                this.mState.setAdjType("pause-activity");
                if (this.logUid == this.appUid) {
                    OomAdjuster oomAdjuster2 = OomAdjuster.this;
                    oomAdjuster2.reportOomAdjMessageLocked("ActivityManager", "Raise procstate to pause-activity (top): " + this.app);
                }
            }
            if (this.schedGroup < 2) {
                this.schedGroup = 2;
            }
            this.mState.setCached(false);
            this.mState.setEmpty(false);
            this.foregroundActivities = true;
            this.mHasVisibleActivities = false;
        }

        @Override // com.android.server.p014wm.WindowProcessController.ComputeOomAdjCallback
        public void onStoppingActivity(boolean z) {
            if (this.adj > 200) {
                this.adj = 200;
                this.mState.setAdjType("stop-activity");
                if (this.logUid == this.appUid) {
                    OomAdjuster oomAdjuster = OomAdjuster.this;
                    oomAdjuster.reportOomAdjMessageLocked("ActivityManager", "Raise adj to stop-activity: " + this.app);
                }
            }
            if (!z && this.procState > 15) {
                this.procState = 15;
                this.mState.setAdjType("stop-activity");
                if (this.logUid == this.appUid) {
                    OomAdjuster oomAdjuster2 = OomAdjuster.this;
                    oomAdjuster2.reportOomAdjMessageLocked("ActivityManager", "Raise procstate to stop-activity: " + this.app);
                }
            }
            this.mState.setCached(false);
            this.mState.setEmpty(false);
            this.foregroundActivities = true;
            this.mHasVisibleActivities = false;
        }

        @Override // com.android.server.p014wm.WindowProcessController.ComputeOomAdjCallback
        public void onOtherActivity() {
            if (this.procState > 16) {
                this.procState = 16;
                this.mState.setAdjType("cch-act");
                if (this.logUid == this.appUid) {
                    OomAdjuster oomAdjuster = OomAdjuster.this;
                    oomAdjuster.reportOomAdjMessageLocked("ActivityManager", "Raise procstate to cached activity: " + this.app);
                }
            }
            this.mHasVisibleActivities = false;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:291:0x06ab, code lost:
        if (r66 < (r11.lastActivity + r61.mConstants.MAX_SERVICE_INACTIVITY)) goto L663;
     */
    /* JADX WARN: Code restructure failed: missing block: B:463:0x09ab, code lost:
        if (r1 >= 200) goto L579;
     */
    /* JADX WARN: Removed duplicated region for block: B:169:0x0407  */
    /* JADX WARN: Removed duplicated region for block: B:177:0x0429  */
    /* JADX WARN: Removed duplicated region for block: B:182:0x0446 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:187:0x0460  */
    /* JADX WARN: Removed duplicated region for block: B:191:0x047f  */
    /* JADX WARN: Removed duplicated region for block: B:205:0x04cd  */
    /* JADX WARN: Removed duplicated region for block: B:219:0x051f  */
    /* JADX WARN: Removed duplicated region for block: B:232:0x055e  */
    /* JADX WARN: Removed duplicated region for block: B:237:0x0584  */
    /* JADX WARN: Removed duplicated region for block: B:243:0x05a5  */
    /* JADX WARN: Removed duplicated region for block: B:246:0x05da  */
    /* JADX WARN: Removed duplicated region for block: B:267:0x0644 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:273:0x0655  */
    /* JADX WARN: Removed duplicated region for block: B:302:0x06e4  */
    /* JADX WARN: Removed duplicated region for block: B:307:0x06f7  */
    /* JADX WARN: Removed duplicated region for block: B:328:0x0733  */
    /* JADX WARN: Removed duplicated region for block: B:374:0x084f  */
    /* JADX WARN: Removed duplicated region for block: B:375:0x0856  */
    /* JADX WARN: Removed duplicated region for block: B:378:0x0865  */
    /* JADX WARN: Removed duplicated region for block: B:417:0x0911  */
    /* JADX WARN: Removed duplicated region for block: B:484:0x09dd  */
    /* JADX WARN: Removed duplicated region for block: B:486:0x09e3  */
    /* JADX WARN: Removed duplicated region for block: B:492:0x09ff  */
    /* JADX WARN: Removed duplicated region for block: B:522:0x0a5e  */
    /* JADX WARN: Removed duplicated region for block: B:532:0x0a76  */
    /* JADX WARN: Removed duplicated region for block: B:537:0x0a85  */
    /* JADX WARN: Removed duplicated region for block: B:539:0x0a8c  */
    /* JADX WARN: Removed duplicated region for block: B:545:0x0a98  */
    /* JADX WARN: Removed duplicated region for block: B:549:0x0aa6  */
    /* JADX WARN: Removed duplicated region for block: B:553:0x0afe  */
    /* JADX WARN: Removed duplicated region for block: B:556:0x0b0c  */
    /* JADX WARN: Removed duplicated region for block: B:563:0x0b31  */
    /* JADX WARN: Removed duplicated region for block: B:564:0x0b37  */
    /* JADX WARN: Removed duplicated region for block: B:589:0x0c49  */
    /* JADX WARN: Removed duplicated region for block: B:685:0x0ee3  */
    /* JADX WARN: Removed duplicated region for block: B:689:0x0ef6  */
    /* JADX WARN: Removed duplicated region for block: B:693:0x0f1b  */
    /* JADX WARN: Removed duplicated region for block: B:696:0x0f21  */
    /* JADX WARN: Removed duplicated region for block: B:703:0x0f44  */
    /* JADX WARN: Removed duplicated region for block: B:712:0x0f67  */
    /* JADX WARN: Removed duplicated region for block: B:732:0x0fc8  */
    /* JADX WARN: Removed duplicated region for block: B:738:0x0fd7  */
    /* JADX WARN: Removed duplicated region for block: B:743:0x0fe8  */
    /* JADX WARN: Removed duplicated region for block: B:746:0x0fef  */
    /* JADX WARN: Removed duplicated region for block: B:749:0x0ff9  */
    /* JADX WARN: Removed duplicated region for block: B:752:0x1024  */
    /* JADX WARN: Removed duplicated region for block: B:762:0x0ed0 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:771:0x0bf5 A[SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r7v10, types: [int, boolean] */
    /* JADX WARN: Type inference failed for: r7v21 */
    /* JADX WARN: Type inference failed for: r7v9 */
    @GuardedBy({"mService", "mProcLock"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final boolean computeOomAdjLSP(ProcessRecord processRecord, int i, ProcessRecord processRecord2, boolean z, long j, boolean z2, boolean z3) {
        int i2;
        int i3;
        int i4;
        boolean z4;
        boolean z5;
        int i5;
        int i6;
        ProcessServiceRecord processServiceRecord;
        int i7;
        int i8;
        String str;
        int i9;
        int i10;
        int i11;
        int i12;
        int i13;
        int i14;
        int i15;
        int i16;
        int i17;
        BackupRecord backupRecord;
        boolean isCurBoundByNonBgRestrictedApp;
        int i18;
        int numberOfRunningServices;
        boolean z6;
        boolean z7;
        ProcessServiceRecord processServiceRecord2;
        ProcessStateRecord processStateRecord;
        String str2;
        int i19;
        int i20;
        int i21;
        int i22;
        String str3;
        String str4;
        ProcessProviderRecord processProviderRecord;
        boolean z8;
        int numberOfProviders;
        int i23;
        ProcessProviderRecord processProviderRecord2;
        ProcessServiceRecord processServiceRecord3;
        String str5;
        boolean z9;
        int i24;
        ProcessStateRecord processStateRecord2;
        int i25;
        int i26;
        int i27;
        ProcessServiceRecord processServiceRecord4;
        boolean z10;
        int i28;
        ProcessStateRecord processStateRecord3;
        int i29;
        int i30;
        int i31;
        String str6;
        ProcessServiceRecord processServiceRecord5;
        String str7;
        String str8;
        ?? r7;
        int i32;
        ContentProviderRecord contentProviderRecord;
        int i33;
        ProcessStateRecord processStateRecord4;
        ContentProviderConnection contentProviderConnection;
        int i34;
        int i35;
        int i36;
        int i37;
        ContentProviderRecord contentProviderRecord2;
        int i38;
        String str9;
        String str10;
        ProcessStateRecord processStateRecord5;
        String str11;
        ProcessRecord processRecord3;
        ProcessStateRecord processStateRecord6;
        String str12;
        int i39;
        String str13;
        int i40;
        String str14;
        String str15;
        ContentProviderRecord contentProviderRecord3;
        ServiceRecord runningServiceAt;
        ProcessServiceRecord processServiceRecord6;
        String str16;
        int i41;
        int i42;
        ArrayMap<IBinder, ArrayList<ConnectionRecord>> connections;
        int i43;
        int size;
        int i44;
        String str17;
        ProcessStateRecord processStateRecord7;
        int i45;
        String str18;
        int i46;
        int i47;
        ProcessServiceRecord processServiceRecord7;
        int i48;
        int i49;
        int i50;
        ArrayList<ConnectionRecord> arrayList;
        int i51;
        String str19;
        ProcessStateRecord processStateRecord8;
        ArrayMap<IBinder, ArrayList<ConnectionRecord>> arrayMap;
        int i52;
        int i53;
        int i54;
        ProcessServiceRecord processServiceRecord8;
        int i55;
        int i56;
        String str20;
        ProcessRecord processRecord4;
        ProcessStateRecord processStateRecord9;
        boolean z11;
        ProcessRecord processRecord5;
        boolean z12;
        ConnectionRecord connectionRecord;
        String str21;
        ProcessStateRecord processStateRecord10;
        int i57;
        int i58;
        String str22;
        boolean z13;
        int i59;
        ProcessServiceRecord processServiceRecord9;
        int i60;
        ConnectionRecord connectionRecord2;
        String str23;
        int i61;
        int i62;
        boolean z14;
        int i63;
        int curCapability;
        int i64;
        int i65;
        int i66;
        ProcessRecord processRecord6;
        int i67;
        int i68;
        int i69;
        int i70;
        ProcessStateRecord processStateRecord11 = processRecord.mState;
        if (this.mAdjSeq == processStateRecord11.getAdjSeq()) {
            if (processStateRecord11.getAdjSeq() == processStateRecord11.getCompletedAdjSeq()) {
                return false;
            }
            processStateRecord11.setContainsCycle(true);
            this.mProcessesInCycle.add(processRecord);
            return false;
        } else if (processRecord.getThread() == null) {
            processStateRecord11.setAdjSeq(this.mAdjSeq);
            processStateRecord11.setCurrentSchedulingGroup(0);
            processStateRecord11.setCurProcState(19);
            processStateRecord11.setCurAdj(999);
            processStateRecord11.setCurRawAdj(999);
            processStateRecord11.setCompletedAdjSeq(processStateRecord11.getAdjSeq());
            processStateRecord11.setCurCapability(0);
            return false;
        } else {
            processStateRecord11.setAdjTypeCode(0);
            processStateRecord11.setAdjSource(null);
            processStateRecord11.setAdjTarget(null);
            processStateRecord11.setEmpty(false);
            processStateRecord11.setCached(false);
            if (!z2) {
                processStateRecord11.setNoKillOnBgRestrictedAndIdle(false);
                UidRecord uidRecord = processRecord.getUidRecord();
                processRecord.mOptRecord.setShouldNotFreeze(uidRecord != null && uidRecord.isCurAllowListed());
            }
            int i71 = processRecord.info.uid;
            int i72 = this.mService.mCurOomAdjUid;
            int curAdj = processStateRecord11.getCurAdj();
            int curProcState = processStateRecord11.getCurProcState();
            int curCapability2 = processStateRecord11.getCurCapability();
            ProcessServiceRecord processServiceRecord10 = processRecord.mServices;
            String str24 = "ActivityManager";
            if (processStateRecord11.getMaxAdj() <= 0) {
                if (i72 == i71) {
                    reportOomAdjMessageLocked("ActivityManager", "Making fixed: " + processRecord);
                }
                processStateRecord11.setAdjType("fixed");
                processStateRecord11.setAdjSeq(this.mAdjSeq);
                processStateRecord11.setCurRawAdj(processStateRecord11.getMaxAdj());
                processStateRecord11.setHasForegroundActivities(false);
                processStateRecord11.setCurrentSchedulingGroup(2);
                processStateRecord11.setCurCapability(31);
                processStateRecord11.setCurProcState(0);
                processStateRecord11.setSystemNoUi(true);
                if (processRecord == processRecord2) {
                    processStateRecord11.setSystemNoUi(false);
                    processStateRecord11.setCurrentSchedulingGroup(3);
                    processStateRecord11.setAdjType("pers-top-activity");
                } else if (processStateRecord11.hasTopUi()) {
                    processStateRecord11.setSystemNoUi(false);
                    processStateRecord11.setAdjType("pers-top-ui");
                } else if (processStateRecord11.getCachedHasVisibleActivities()) {
                    processStateRecord11.setSystemNoUi(false);
                }
                if (!processStateRecord11.isSystemNoUi()) {
                    if (this.mService.mWakefulness.get() == 1 || processStateRecord11.isRunningRemoteAnimation()) {
                        processStateRecord11.setCurProcState(1);
                        processStateRecord11.setCurrentSchedulingGroup(3);
                    } else {
                        processStateRecord11.setCurProcState(5);
                        processStateRecord11.setCurrentSchedulingGroup(1);
                    }
                }
                processStateRecord11.setCurRawProcState(processStateRecord11.getCurProcState());
                processStateRecord11.setCurAdj(processStateRecord11.getMaxAdj());
                processStateRecord11.setCompletedAdjSeq(processStateRecord11.getAdjSeq());
                return processStateRecord11.getCurAdj() < curAdj || processStateRecord11.getCurProcState() < curProcState;
            }
            processStateRecord11.setSystemNoUi(false);
            int topProcessState = this.mService.mAtmInternal.getTopProcessState();
            int curCapability3 = z2 ? processRecord.mState.getCurCapability() : 0;
            if (processRecord == processRecord2 && topProcessState == 2) {
                if (this.mService.mAtmInternal.useTopSchedGroupForTopProcess()) {
                    processStateRecord11.setAdjType("top-activity");
                    i70 = 3;
                } else {
                    processStateRecord11.setAdjType("intermediate-top-activity");
                    i70 = 2;
                }
                i2 = curProcState;
                if (i72 == i71) {
                    StringBuilder sb = new StringBuilder();
                    i3 = curAdj;
                    sb.append("Making top: ");
                    sb.append(processRecord);
                    reportOomAdjMessageLocked("ActivityManager", sb.toString());
                } else {
                    i3 = curAdj;
                }
                i6 = i70;
                i4 = 0;
                z4 = true;
                z5 = true;
                i5 = 2;
            } else {
                i2 = curProcState;
                i3 = curAdj;
                if (processStateRecord11.isRunningRemoteAnimation()) {
                    processStateRecord11.setAdjType("running-remote-anim");
                    if (i72 == i71) {
                        reportOomAdjMessageLocked("ActivityManager", "Making running remote anim: " + processRecord);
                    }
                    i5 = topProcessState;
                    i6 = 3;
                    i4 = 100;
                } else if (processRecord.getActiveInstrumentation() != null) {
                    processStateRecord11.setAdjType("instrumentation");
                    curCapability3 |= 16;
                    if (i72 == i71) {
                        reportOomAdjMessageLocked("ActivityManager", "Making instrumentation: " + processRecord);
                    }
                    i4 = 0;
                    z4 = false;
                    z5 = false;
                    i5 = 4;
                    i6 = 2;
                } else if (processStateRecord11.getCachedIsReceivingBroadcast(this.mTmpSchedGroup)) {
                    int i73 = this.mTmpSchedGroup[0];
                    processStateRecord11.setAdjType(INetd.IF_FLAG_BROADCAST);
                    if (i72 == i71) {
                        reportOomAdjMessageLocked("ActivityManager", "Making broadcast: " + processRecord);
                    }
                    i6 = i73;
                    i5 = 11;
                    i4 = 0;
                } else if (processServiceRecord10.numberOfExecutingServices() > 0) {
                    int i74 = processServiceRecord10.shouldExecServicesFg() ? 2 : 0;
                    processStateRecord11.setAdjType("exec-service");
                    if (i72 == i71) {
                        reportOomAdjMessageLocked("ActivityManager", "Making exec-service: " + processRecord);
                    }
                    i6 = i74;
                    i4 = 0;
                    z4 = false;
                    z5 = false;
                    i5 = 10;
                } else {
                    if (processRecord == processRecord2) {
                        processStateRecord11.setAdjType("top-sleeping");
                        if (i72 == i71) {
                            reportOomAdjMessageLocked("ActivityManager", "Making top (sleeping): " + processRecord);
                        }
                        i5 = topProcessState;
                        i4 = 0;
                        z4 = true;
                        z5 = false;
                    } else {
                        if (!processStateRecord11.containsCycle()) {
                            processStateRecord11.setCached(true);
                            processStateRecord11.setEmpty(true);
                            processStateRecord11.setAdjType("cch-empty");
                        }
                        if (i72 == i71) {
                            reportOomAdjMessageLocked("ActivityManager", "Making empty: " + processRecord);
                        }
                        i4 = i;
                        z4 = false;
                        z5 = false;
                        i5 = 19;
                    }
                    i6 = 0;
                }
                z4 = false;
                z5 = false;
            }
            if (z4 || !processStateRecord11.getCachedHasActivities()) {
                processServiceRecord = processServiceRecord10;
                i7 = curCapability2;
            } else {
                processServiceRecord = processServiceRecord10;
                i7 = curCapability2;
                processStateRecord11.computeOomAdjFromActivitiesIfNecessary(this.mTmpComputeOomAdjWindowCallback, i4, z4, z5, i5, i6, i71, i72, topProcessState);
                i4 = processStateRecord11.getCachedAdj();
                z4 = processStateRecord11.getCachedForegroundActivities();
                z5 = processStateRecord11.getCachedHasVisibleActivities();
                i5 = processStateRecord11.getCachedProcState();
                i6 = processStateRecord11.getCachedSchedGroup();
            }
            int i75 = i4;
            boolean z15 = z4;
            boolean z16 = z5;
            int i76 = i5;
            int i77 = i7;
            if (i76 > 18 && processStateRecord11.getCachedHasRecentTasks()) {
                processStateRecord11.setAdjType("cch-rec");
                if (i72 == i71) {
                    reportOomAdjMessageLocked("ActivityManager", "Raise procstate to cached recent: " + processRecord);
                }
                i76 = 18;
            }
            boolean hasForegroundServices = processServiceRecord.hasForegroundServices();
            boolean hasNonShortForegroundServices = processServiceRecord.hasNonShortForegroundServices();
            boolean z17 = hasForegroundServices && !processServiceRecord.areAllShortForegroundServicesProcstateTimedOut(j);
            String str25 = "Raise to ";
            if (i75 <= 200) {
                i8 = 4;
                if (i76 <= 4) {
                    i12 = 0;
                    if (processServiceRecord.hasForegroundServices() || i75 <= 50) {
                        i13 = i75;
                        i14 = i12;
                    } else {
                        i13 = i75;
                        i14 = i12;
                        if (processStateRecord11.getLastTopTime() + this.mConstants.TOP_TO_FGS_GRACE_DURATION > j || processStateRecord11.getSetProcState() <= 2) {
                            if (processServiceRecord.hasNonShortForegroundServices()) {
                                processStateRecord11.setAdjType("fg-service-act");
                                i15 = 50;
                            } else {
                                processStateRecord11.setAdjType("fg-service-short-act");
                                i15 = 51;
                            }
                            if (i72 == i71) {
                                reportOomAdjMessageLocked("ActivityManager", "Raise to recent fg: " + processRecord);
                            }
                            if (processServiceRecord.hasTopStartedAlmostPerceptibleServices() && i15 > 52) {
                                i16 = i15;
                                if (processStateRecord11.getLastTopTime() + this.mConstants.TOP_TO_ALMOST_PERCEPTIBLE_GRACE_DURATION <= j || processStateRecord11.getSetProcState() <= 2) {
                                    processStateRecord11.setAdjType("top-ej-act");
                                    if (i72 == i71) {
                                        reportOomAdjMessageLocked("ActivityManager", "Raise to recent fg for EJ: " + processRecord);
                                    }
                                    i15 = 52;
                                }
                                if ((i16 <= 200 || i76 > 8) && processStateRecord11.getForcingToImportant() != null) {
                                    processStateRecord11.setCached(false);
                                    processStateRecord11.setAdjType("force-imp");
                                    processStateRecord11.setAdjSource(processStateRecord11.getForcingToImportant());
                                    if (i72 == i71) {
                                        reportOomAdjMessageLocked("ActivityManager", "Raise to force imp: " + processRecord);
                                    }
                                    i76 = 8;
                                    i16 = 200;
                                    i6 = 2;
                                }
                                if (processStateRecord11.getCachedIsHeavyWeight()) {
                                    if (i16 > 400) {
                                        processStateRecord11.setCached(false);
                                        processStateRecord11.setAdjType("heavy");
                                        if (i72 == i71) {
                                            reportOomAdjMessageLocked("ActivityManager", "Raise adj to heavy: " + processRecord);
                                        }
                                        i16 = FrameworkStatsLog.APP_PROCESS_DIED__IMPORTANCE__IMPORTANCE_BACKGROUND;
                                        i6 = 0;
                                    }
                                    if (i76 > 13) {
                                        processStateRecord11.setAdjType("heavy");
                                        if (i72 == i71) {
                                            reportOomAdjMessageLocked("ActivityManager", "Raise procstate to heavy: " + processRecord);
                                        }
                                        i76 = 13;
                                    }
                                }
                                if (processStateRecord11.getCachedIsHomeProcess()) {
                                    if (i16 > 600) {
                                        processStateRecord11.setCached(false);
                                        processStateRecord11.setAdjType("home");
                                        if (i72 == i71) {
                                            reportOomAdjMessageLocked("ActivityManager", "Raise adj to home: " + processRecord);
                                        }
                                        i16 = 600;
                                        i6 = 0;
                                    }
                                    if (i76 > 14) {
                                        processStateRecord11.setAdjType("home");
                                        if (i72 == i71) {
                                            reportOomAdjMessageLocked("ActivityManager", "Raise procstate to home: " + processRecord);
                                        }
                                        i76 = 14;
                                    }
                                }
                                String str26 = ": ";
                                if (processStateRecord11.getCachedIsPreviousProcess() && processStateRecord11.getCachedHasActivities()) {
                                    if (i76 < 15 && processStateRecord11.getSetProcState() == 15 && processStateRecord11.getLastStateTime() + ActivityManagerConstants.MAX_PREVIOUS_TIME < j) {
                                        processStateRecord11.setAdjType("previous-expired");
                                        if (i72 == i71) {
                                            reportOomAdjMessageLocked("ActivityManager", "Expire prev adj: " + processRecord);
                                        }
                                        i76 = 15;
                                        i17 = 0;
                                        i16 = 900;
                                        if (z2) {
                                            i76 = Math.min(i76, processStateRecord11.getCurRawProcState());
                                            i16 = Math.min(i16, processStateRecord11.getCurRawAdj());
                                            i17 = Math.max(i17, processStateRecord11.getCurrentSchedulingGroup());
                                        }
                                        processStateRecord11.setCurRawAdj(i16);
                                        processStateRecord11.setCurRawProcState(i76);
                                        processStateRecord11.setHasStartedServices(false);
                                        processStateRecord11.setAdjSeq(this.mAdjSeq);
                                        backupRecord = this.mService.mBackupTargets.get(processRecord.userId);
                                        if (backupRecord == null && processRecord == backupRecord.app) {
                                            if (i16 > 300) {
                                                if (i76 > 8) {
                                                    i76 = 8;
                                                }
                                                processStateRecord11.setAdjType("backup");
                                                if (i72 == i71) {
                                                    reportOomAdjMessageLocked("ActivityManager", "Raise adj to backup: " + processRecord);
                                                }
                                                processStateRecord11.setCached(false);
                                                i16 = 300;
                                            }
                                            if (i76 > 9) {
                                                processStateRecord11.setAdjType("backup");
                                                if (i72 == i71) {
                                                    reportOomAdjMessageLocked("ActivityManager", "Raise procstate to backup: " + processRecord);
                                                }
                                                i76 = 9;
                                            }
                                        }
                                        isCurBoundByNonBgRestrictedApp = processStateRecord11.isCurBoundByNonBgRestrictedApp();
                                        i18 = i16;
                                        numberOfRunningServices = processServiceRecord.numberOfRunningServices() - 1;
                                        z6 = false;
                                        while (numberOfRunningServices >= 0 && (i18 > 0 || i17 == 0 || i76 > 2)) {
                                            runningServiceAt = processServiceRecord.getRunningServiceAt(numberOfRunningServices);
                                            int i78 = i17;
                                            if (runningServiceAt.startRequested) {
                                                processStateRecord11.setHasStartedServices(true);
                                                if (i76 > 10) {
                                                    processStateRecord11.setAdjType("started-services");
                                                    if (i72 == i71) {
                                                        reportOomAdjMessageLocked(str24, "Raise procstate to started service: " + processRecord);
                                                    }
                                                    i76 = 10;
                                                }
                                                if (runningServiceAt.mKeepWarming || !processStateRecord11.hasShownUi() || processStateRecord11.getCachedIsHomeProcess()) {
                                                    if (runningServiceAt.mKeepWarming) {
                                                        i68 = i76;
                                                        processServiceRecord6 = processServiceRecord;
                                                        str16 = str25;
                                                        i41 = numberOfRunningServices;
                                                    } else {
                                                        processServiceRecord6 = processServiceRecord;
                                                        i68 = i76;
                                                        str16 = str25;
                                                        i41 = numberOfRunningServices;
                                                    }
                                                    if (i18 > 500) {
                                                        processStateRecord11.setAdjType("started-services");
                                                        if (i72 == i71) {
                                                            reportOomAdjMessageLocked(str24, "Raise adj to started service: " + processRecord);
                                                        }
                                                        i42 = 0;
                                                        processStateRecord11.setCached(false);
                                                        i69 = 500;
                                                        i18 = 500;
                                                        if (i18 > i69) {
                                                            processStateRecord11.setAdjType("cch-started-services");
                                                        }
                                                        i76 = i68;
                                                        if (runningServiceAt.isForeground) {
                                                            int i79 = runningServiceAt.foregroundServiceType;
                                                            if (runningServiceAt.mAllowWhileInUsePermissionInFgs) {
                                                                int i80 = i14 | ((i79 & 8) != 0 ? 1 : i42);
                                                                if (processStateRecord11.getCachedCompatChange(1)) {
                                                                    i67 = ((i79 & 128) != 0 ? 4 : i42) | i80 | ((i79 & 64) != 0 ? 2 : i42);
                                                                } else {
                                                                    i67 = i80 | 6;
                                                                }
                                                                i14 = i67;
                                                            }
                                                        }
                                                        connections = runningServiceAt.getConnections();
                                                        i43 = curCapability3;
                                                        size = connections.size() - 1;
                                                        i17 = i78;
                                                        while (true) {
                                                            if (size >= 0) {
                                                                i44 = i72;
                                                                str17 = str24;
                                                                processStateRecord7 = processStateRecord11;
                                                                i45 = i77;
                                                                str18 = str26;
                                                                i46 = i2;
                                                                i47 = i3;
                                                                processServiceRecord7 = processServiceRecord6;
                                                                break;
                                                            } else if (i18 <= 0 && i17 != 0 && i76 <= 2) {
                                                                i44 = i72;
                                                                str17 = str24;
                                                                processStateRecord7 = processStateRecord11;
                                                                i45 = i77;
                                                                str18 = str26;
                                                                i46 = i2;
                                                                i47 = i3;
                                                                processServiceRecord7 = processServiceRecord6;
                                                                break;
                                                            } else {
                                                                ArrayList<ConnectionRecord> valueAt = connections.valueAt(size);
                                                                boolean z18 = z6;
                                                                boolean z19 = isCurBoundByNonBgRestrictedApp;
                                                                int i81 = i43;
                                                                int i82 = i17;
                                                                int i83 = i76;
                                                                int i84 = 0;
                                                                while (i84 < valueAt.size()) {
                                                                    if (i18 <= 0 && i82 != 0) {
                                                                        if (i83 <= 2) {
                                                                            break;
                                                                        }
                                                                    }
                                                                    ConnectionRecord connectionRecord3 = valueAt.get(i84);
                                                                    int i85 = i83;
                                                                    AppBindRecord appBindRecord = connectionRecord3.binding;
                                                                    int i86 = i18;
                                                                    ProcessRecord processRecord7 = appBindRecord.client;
                                                                    if (processRecord7 == processRecord) {
                                                                        i57 = i72;
                                                                        i50 = i84;
                                                                        arrayList = valueAt;
                                                                        i51 = size;
                                                                        processStateRecord10 = processStateRecord11;
                                                                        arrayMap = connections;
                                                                        i52 = i77;
                                                                        str21 = str26;
                                                                        i83 = i85;
                                                                        i53 = i2;
                                                                        i54 = i3;
                                                                        processServiceRecord9 = processServiceRecord6;
                                                                        i18 = i86;
                                                                        i58 = i71;
                                                                        str22 = str24;
                                                                    } else {
                                                                        ProcessRecord processRecord8 = (!processRecord.isSdkSandbox || (processRecord6 = appBindRecord.attributedClient) == null) ? processRecord7 : processRecord6;
                                                                        ProcessStateRecord processStateRecord12 = processRecord8.mState;
                                                                        if (z3) {
                                                                            i53 = i2;
                                                                            i52 = i77;
                                                                            i54 = i3;
                                                                            i55 = i86;
                                                                            processRecord4 = processRecord8;
                                                                            i48 = i72;
                                                                            i49 = i71;
                                                                            i50 = i84;
                                                                            str20 = null;
                                                                            arrayList = valueAt;
                                                                            i51 = size;
                                                                            processServiceRecord8 = processServiceRecord6;
                                                                            str19 = str24;
                                                                            arrayMap = connections;
                                                                            i56 = 2;
                                                                            processStateRecord8 = processStateRecord11;
                                                                            computeOomAdjLSP(processRecord8, i, processRecord2, z, j, z2, true);
                                                                            processStateRecord9 = processStateRecord12;
                                                                        } else {
                                                                            i48 = i72;
                                                                            i49 = i71;
                                                                            i50 = i84;
                                                                            arrayList = valueAt;
                                                                            i51 = size;
                                                                            str19 = str24;
                                                                            processStateRecord8 = processStateRecord11;
                                                                            arrayMap = connections;
                                                                            i52 = i77;
                                                                            i53 = i2;
                                                                            i54 = i3;
                                                                            processServiceRecord8 = processServiceRecord6;
                                                                            i55 = i86;
                                                                            i56 = 2;
                                                                            str20 = null;
                                                                            processRecord4 = processRecord8;
                                                                            processStateRecord9 = processStateRecord12;
                                                                            processStateRecord9.setCurRawAdj(processStateRecord12.getCurAdj());
                                                                            processStateRecord9.setCurRawProcState(processStateRecord9.getCurProcState());
                                                                        }
                                                                        int curRawAdj = processStateRecord9.getCurRawAdj();
                                                                        int curRawProcState = processStateRecord9.getCurRawProcState();
                                                                        boolean z20 = curRawProcState < i56;
                                                                        if (!processStateRecord9.isCurBoundByNonBgRestrictedApp() && curRawProcState > 3) {
                                                                            if (curRawProcState != 4 || processStateRecord9.isBackgroundRestricted()) {
                                                                                z11 = false;
                                                                                boolean z21 = z19 | z11;
                                                                                processRecord5 = processRecord4;
                                                                                if (processRecord5.mOptRecord.shouldNotFreeze()) {
                                                                                    z12 = true;
                                                                                } else {
                                                                                    z12 = true;
                                                                                    processRecord.mOptRecord.setShouldNotFreeze(true);
                                                                                }
                                                                                int bfslCapabilityFromClient = i81 | getBfslCapabilityFromClient(processRecord5);
                                                                                if (connectionRecord3.notHasFlag(32)) {
                                                                                    connectionRecord = connectionRecord3;
                                                                                    str21 = str26;
                                                                                    processStateRecord10 = processStateRecord8;
                                                                                    i18 = i55;
                                                                                    i57 = i48;
                                                                                    i58 = i49;
                                                                                    str22 = str19;
                                                                                    if (curRawAdj < 900) {
                                                                                        z13 = true;
                                                                                        processRecord.mOptRecord.setShouldNotFreeze(true);
                                                                                    } else {
                                                                                        z13 = true;
                                                                                    }
                                                                                    i59 = bfslCapabilityFromClient;
                                                                                    i83 = i85;
                                                                                } else {
                                                                                    if (connectionRecord3.hasFlag(IInstalld.FLAG_USE_QUOTA)) {
                                                                                        bfslCapabilityFromClient |= processStateRecord9.getCurCapability();
                                                                                    }
                                                                                    if ((processStateRecord9.getCurCapability() & 8) != 0 && (curRawProcState > 5 || connectionRecord3.hasFlag(IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES))) {
                                                                                        bfslCapabilityFromClient |= 8;
                                                                                    }
                                                                                    i59 = bfslCapabilityFromClient;
                                                                                    boolean z22 = z12;
                                                                                    if (shouldSkipDueToCycle(processRecord, processStateRecord9, i85, i55, z2)) {
                                                                                        i81 = i59;
                                                                                        str21 = str26;
                                                                                        i83 = i85;
                                                                                        processStateRecord10 = processStateRecord8;
                                                                                        z19 = z21;
                                                                                        i18 = i55;
                                                                                        i57 = i48;
                                                                                        i58 = i49;
                                                                                        processServiceRecord9 = processServiceRecord8;
                                                                                        str22 = str19;
                                                                                    } else {
                                                                                        if (curRawProcState >= 16) {
                                                                                            curRawProcState = 19;
                                                                                        }
                                                                                        if (connectionRecord3.hasFlag(16)) {
                                                                                            i60 = 900;
                                                                                            if (curRawAdj < 900) {
                                                                                                processRecord.mOptRecord.setShouldNotFreeze(z22);
                                                                                            }
                                                                                            if (processStateRecord8.hasShownUi() && !processStateRecord8.getCachedIsHomeProcess()) {
                                                                                                i18 = i55;
                                                                                                str23 = i18 > curRawAdj ? "cch-bound-ui-services" : str20;
                                                                                                processStateRecord10 = processStateRecord8;
                                                                                                processStateRecord10.setCached(false);
                                                                                                curRawAdj = i18;
                                                                                                connectionRecord2 = connectionRecord3;
                                                                                                curRawProcState = i85;
                                                                                            } else {
                                                                                                processStateRecord10 = processStateRecord8;
                                                                                                i18 = i55;
                                                                                                connectionRecord2 = connectionRecord3;
                                                                                                if (j >= runningServiceAt.lastActivity + this.mConstants.MAX_SERVICE_INACTIVITY) {
                                                                                                    str23 = i18 > curRawAdj ? "cch-bound-services" : str20;
                                                                                                    curRawAdj = i18;
                                                                                                }
                                                                                            }
                                                                                            if (i18 > curRawAdj) {
                                                                                                if (!processStateRecord10.hasShownUi() || processStateRecord10.getCachedIsHomeProcess() || curRawAdj <= 200) {
                                                                                                    connectionRecord = connectionRecord2;
                                                                                                    if (!connectionRecord.hasFlag(72)) {
                                                                                                        if (!connectionRecord.hasFlag(256) || curRawAdj > 200) {
                                                                                                            i64 = 100;
                                                                                                        } else {
                                                                                                            i64 = 250;
                                                                                                            if (i18 >= 250) {
                                                                                                                i64 = 250;
                                                                                                                i62 = i82;
                                                                                                                i61 = 4;
                                                                                                                z14 = false;
                                                                                                                if (!processStateRecord9.isCached()) {
                                                                                                                }
                                                                                                                if (i18 > i64) {
                                                                                                                }
                                                                                                                i83 = i85;
                                                                                                                if (!connectionRecord.notHasFlag(8388612)) {
                                                                                                                }
                                                                                                                if (i62 < i63) {
                                                                                                                    i62 = 3;
                                                                                                                    z18 = true;
                                                                                                                }
                                                                                                                if (!z14) {
                                                                                                                }
                                                                                                                if (i83 > curRawProcState) {
                                                                                                                }
                                                                                                                if (i83 < 7) {
                                                                                                                    processRecord.setPendingUiClean(true);
                                                                                                                }
                                                                                                                if (str23 == null) {
                                                                                                                }
                                                                                                                str22 = str19;
                                                                                                                i82 = i62;
                                                                                                                z13 = true;
                                                                                                            }
                                                                                                        }
                                                                                                        if (connectionRecord.hasFlag(65536)) {
                                                                                                            i61 = 4;
                                                                                                            if (connectionRecord.notHasFlag(4) && curRawAdj < 200) {
                                                                                                                if (i18 >= 200) {
                                                                                                                    i64 = 201;
                                                                                                                    i62 = i82;
                                                                                                                    z14 = false;
                                                                                                                    if (!processStateRecord9.isCached()) {
                                                                                                                    }
                                                                                                                    if (i18 > i64) {
                                                                                                                    }
                                                                                                                    i83 = i85;
                                                                                                                    if (!connectionRecord.notHasFlag(8388612)) {
                                                                                                                    }
                                                                                                                    if (i62 < i63) {
                                                                                                                    }
                                                                                                                    if (!z14) {
                                                                                                                    }
                                                                                                                    if (i83 > curRawProcState) {
                                                                                                                    }
                                                                                                                    if (i83 < 7) {
                                                                                                                    }
                                                                                                                    if (str23 == null) {
                                                                                                                    }
                                                                                                                    str22 = str19;
                                                                                                                    i82 = i62;
                                                                                                                    z13 = true;
                                                                                                                } else {
                                                                                                                    i64 = 200;
                                                                                                                }
                                                                                                            }
                                                                                                        } else {
                                                                                                            i61 = 4;
                                                                                                        }
                                                                                                        if (connectionRecord.hasFlag(65536) && connectionRecord.hasFlag(i61) && curRawAdj < 200) {
                                                                                                            i64 = FrameworkStatsLog.CAMERA_ACTION_EVENT;
                                                                                                            if (i18 >= 227) {
                                                                                                                i64 = FrameworkStatsLog.CAMERA_ACTION_EVENT;
                                                                                                                i62 = i82;
                                                                                                                z14 = false;
                                                                                                                if (!processStateRecord9.isCached()) {
                                                                                                                }
                                                                                                                if (i18 > i64) {
                                                                                                                }
                                                                                                                i83 = i85;
                                                                                                                if (!connectionRecord.notHasFlag(8388612)) {
                                                                                                                }
                                                                                                                if (i62 < i63) {
                                                                                                                }
                                                                                                                if (!z14) {
                                                                                                                }
                                                                                                                if (i83 > curRawProcState) {
                                                                                                                }
                                                                                                                if (i83 < 7) {
                                                                                                                }
                                                                                                                if (str23 == null) {
                                                                                                                }
                                                                                                                str22 = str19;
                                                                                                                i82 = i62;
                                                                                                                z13 = true;
                                                                                                            }
                                                                                                        }
                                                                                                        if (connectionRecord.hasFlag(1073741824)) {
                                                                                                            i65 = 200;
                                                                                                            if (curRawAdj < 200) {
                                                                                                                i64 = 200;
                                                                                                            }
                                                                                                        } else {
                                                                                                            i65 = 200;
                                                                                                        }
                                                                                                        if (curRawAdj < i65) {
                                                                                                            if (connectionRecord.hasFlag(268435456)) {
                                                                                                                i66 = 100;
                                                                                                                if (curRawAdj <= 100 && i18 > 100) {
                                                                                                                    i64 = 100;
                                                                                                                    i62 = i82;
                                                                                                                    z14 = false;
                                                                                                                    if (!processStateRecord9.isCached()) {
                                                                                                                    }
                                                                                                                    if (i18 > i64) {
                                                                                                                    }
                                                                                                                    i83 = i85;
                                                                                                                    if (!connectionRecord.notHasFlag(8388612)) {
                                                                                                                    }
                                                                                                                    if (i62 < i63) {
                                                                                                                    }
                                                                                                                    if (!z14) {
                                                                                                                    }
                                                                                                                    if (i83 > curRawProcState) {
                                                                                                                    }
                                                                                                                    if (i83 < 7) {
                                                                                                                    }
                                                                                                                    if (str23 == null) {
                                                                                                                    }
                                                                                                                    str22 = str19;
                                                                                                                    i82 = i62;
                                                                                                                    z13 = true;
                                                                                                                }
                                                                                                            } else {
                                                                                                                i66 = 100;
                                                                                                            }
                                                                                                            i64 = i18 > i66 ? Math.max(curRawAdj, i64) : i18;
                                                                                                            i62 = i82;
                                                                                                            z14 = false;
                                                                                                            if (!processStateRecord9.isCached()) {
                                                                                                            }
                                                                                                            if (i18 > i64) {
                                                                                                            }
                                                                                                            i83 = i85;
                                                                                                            if (!connectionRecord.notHasFlag(8388612)) {
                                                                                                            }
                                                                                                            if (i62 < i63) {
                                                                                                            }
                                                                                                            if (!z14) {
                                                                                                            }
                                                                                                            if (i83 > curRawProcState) {
                                                                                                            }
                                                                                                            if (i83 < 7) {
                                                                                                            }
                                                                                                            if (str23 == null) {
                                                                                                            }
                                                                                                            str22 = str19;
                                                                                                            i82 = i62;
                                                                                                            z13 = true;
                                                                                                        }
                                                                                                    } else if (curRawAdj >= -700) {
                                                                                                        i61 = 4;
                                                                                                    } else {
                                                                                                        connectionRecord.trackProcState(0, this.mAdjSeq);
                                                                                                        i64 = -700;
                                                                                                        i62 = 2;
                                                                                                        i61 = 4;
                                                                                                        z14 = true;
                                                                                                        i85 = 0;
                                                                                                        if (!processStateRecord9.isCached()) {
                                                                                                            processStateRecord10.setCached(false);
                                                                                                        }
                                                                                                        if (i18 > i64) {
                                                                                                            processStateRecord10.setCurRawAdj(i64);
                                                                                                            str23 = "service";
                                                                                                            i18 = i64;
                                                                                                        }
                                                                                                        i83 = i85;
                                                                                                        if (!connectionRecord.notHasFlag(8388612)) {
                                                                                                            int currentSchedulingGroup = processStateRecord9.getCurrentSchedulingGroup();
                                                                                                            if (currentSchedulingGroup > i62) {
                                                                                                                i62 = connectionRecord.hasFlag(64) ? currentSchedulingGroup : 2;
                                                                                                            }
                                                                                                            if (curRawProcState < 2) {
                                                                                                                if (connectionRecord.hasFlag(268435456)) {
                                                                                                                    curRawProcState = i61;
                                                                                                                } else {
                                                                                                                    curRawProcState = (connectionRecord.hasFlag(67108864) || (this.mService.mWakefulness.get() == 1 && connectionRecord.hasFlag(33554432))) ? 5 : 6;
                                                                                                                }
                                                                                                            } else if (curRawProcState == 2) {
                                                                                                                if (processStateRecord9.getCachedCompatChange(0)) {
                                                                                                                    if (connectionRecord.hasFlag(IInstalld.FLAG_USE_QUOTA)) {
                                                                                                                        curCapability = processStateRecord9.getCurCapability();
                                                                                                                    }
                                                                                                                    curRawProcState = 3;
                                                                                                                } else {
                                                                                                                    curCapability = processStateRecord9.getCurCapability();
                                                                                                                }
                                                                                                                i59 |= curCapability;
                                                                                                                curRawProcState = 3;
                                                                                                            }
                                                                                                            i63 = 3;
                                                                                                        } else {
                                                                                                            if (connectionRecord.notHasFlag(8388608)) {
                                                                                                                if (curRawProcState < 8) {
                                                                                                                    curRawProcState = 8;
                                                                                                                }
                                                                                                            } else if (curRawProcState < 7) {
                                                                                                                curRawProcState = 7;
                                                                                                            }
                                                                                                            i63 = 3;
                                                                                                        }
                                                                                                        if (i62 < i63 && connectionRecord.hasFlag(524288) && z20) {
                                                                                                            i62 = 3;
                                                                                                            z18 = true;
                                                                                                        }
                                                                                                        if (!z14) {
                                                                                                            connectionRecord.trackProcState(curRawProcState, this.mAdjSeq);
                                                                                                        }
                                                                                                        if (i83 > curRawProcState) {
                                                                                                            processStateRecord10.setCurRawProcState(curRawProcState);
                                                                                                            if (str23 == null) {
                                                                                                                str23 = "service";
                                                                                                            }
                                                                                                            i83 = curRawProcState;
                                                                                                        }
                                                                                                        if (i83 < 7 && connectionRecord.hasFlag(536870912)) {
                                                                                                            processRecord.setPendingUiClean(true);
                                                                                                        }
                                                                                                        if (str23 == null) {
                                                                                                            processStateRecord10.setAdjType(str23);
                                                                                                            processStateRecord10.setAdjTypeCode(2);
                                                                                                            processStateRecord10.setAdjSource(processRecord5);
                                                                                                            processStateRecord10.setAdjSourceProcState(curRawProcState);
                                                                                                            processStateRecord10.setAdjTarget(runningServiceAt.instanceName);
                                                                                                            i57 = i48;
                                                                                                            i58 = i49;
                                                                                                            if (i57 == i58) {
                                                                                                                StringBuilder sb2 = new StringBuilder();
                                                                                                                sb2.append(str16);
                                                                                                                sb2.append(str23);
                                                                                                                str21 = str26;
                                                                                                                sb2.append(str21);
                                                                                                                sb2.append(processRecord);
                                                                                                                sb2.append(", due to ");
                                                                                                                sb2.append(processRecord5);
                                                                                                                sb2.append(" adj=");
                                                                                                                sb2.append(i18);
                                                                                                                sb2.append(" procState=");
                                                                                                                sb2.append(ProcessList.makeProcStateString(i83));
                                                                                                                String sb3 = sb2.toString();
                                                                                                                str22 = str19;
                                                                                                                reportOomAdjMessageLocked(str22, sb3);
                                                                                                                i82 = i62;
                                                                                                                z13 = true;
                                                                                                            } else {
                                                                                                                str21 = str26;
                                                                                                            }
                                                                                                        } else {
                                                                                                            str21 = str26;
                                                                                                            i57 = i48;
                                                                                                            i58 = i49;
                                                                                                        }
                                                                                                        str22 = str19;
                                                                                                        i82 = i62;
                                                                                                        z13 = true;
                                                                                                    }
                                                                                                    i64 = curRawAdj;
                                                                                                    i62 = i82;
                                                                                                    z14 = false;
                                                                                                    if (!processStateRecord9.isCached()) {
                                                                                                    }
                                                                                                    if (i18 > i64) {
                                                                                                    }
                                                                                                    i83 = i85;
                                                                                                    if (!connectionRecord.notHasFlag(8388612)) {
                                                                                                    }
                                                                                                    if (i62 < i63) {
                                                                                                    }
                                                                                                    if (!z14) {
                                                                                                    }
                                                                                                    if (i83 > curRawProcState) {
                                                                                                    }
                                                                                                    if (i83 < 7) {
                                                                                                    }
                                                                                                    if (str23 == null) {
                                                                                                    }
                                                                                                    str22 = str19;
                                                                                                    i82 = i62;
                                                                                                    z13 = true;
                                                                                                } else if (i18 >= i60) {
                                                                                                    str23 = "cch-bound-ui-services";
                                                                                                    i62 = i82;
                                                                                                    i83 = i85;
                                                                                                    connectionRecord = connectionRecord2;
                                                                                                    i61 = 4;
                                                                                                    z14 = false;
                                                                                                    if (!connectionRecord.notHasFlag(8388612)) {
                                                                                                    }
                                                                                                    if (i62 < i63) {
                                                                                                    }
                                                                                                    if (!z14) {
                                                                                                    }
                                                                                                    if (i83 > curRawProcState) {
                                                                                                    }
                                                                                                    if (i83 < 7) {
                                                                                                    }
                                                                                                    if (str23 == null) {
                                                                                                    }
                                                                                                    str22 = str19;
                                                                                                    i82 = i62;
                                                                                                    z13 = true;
                                                                                                }
                                                                                            }
                                                                                            connectionRecord = connectionRecord2;
                                                                                            i61 = 4;
                                                                                            i62 = i82;
                                                                                            i83 = i85;
                                                                                            z14 = false;
                                                                                            if (!connectionRecord.notHasFlag(8388612)) {
                                                                                            }
                                                                                            if (i62 < i63) {
                                                                                            }
                                                                                            if (!z14) {
                                                                                            }
                                                                                            if (i83 > curRawProcState) {
                                                                                            }
                                                                                            if (i83 < 7) {
                                                                                            }
                                                                                            if (str23 == null) {
                                                                                            }
                                                                                            str22 = str19;
                                                                                            i82 = i62;
                                                                                            z13 = true;
                                                                                        } else {
                                                                                            processStateRecord10 = processStateRecord8;
                                                                                            i18 = i55;
                                                                                            i60 = 900;
                                                                                            connectionRecord2 = connectionRecord3;
                                                                                        }
                                                                                        str23 = str20;
                                                                                        if (i18 > curRawAdj) {
                                                                                        }
                                                                                        connectionRecord = connectionRecord2;
                                                                                        i61 = 4;
                                                                                        i62 = i82;
                                                                                        i83 = i85;
                                                                                        z14 = false;
                                                                                        if (!connectionRecord.notHasFlag(8388612)) {
                                                                                        }
                                                                                        if (i62 < i63) {
                                                                                        }
                                                                                        if (!z14) {
                                                                                        }
                                                                                        if (i83 > curRawProcState) {
                                                                                        }
                                                                                        if (i83 < 7) {
                                                                                        }
                                                                                        if (str23 == null) {
                                                                                        }
                                                                                        str22 = str19;
                                                                                        i82 = i62;
                                                                                        z13 = true;
                                                                                    }
                                                                                }
                                                                                if (connectionRecord.hasFlag(134217728)) {
                                                                                    processServiceRecord9 = processServiceRecord8;
                                                                                } else {
                                                                                    processServiceRecord9 = processServiceRecord8;
                                                                                    processServiceRecord9.setTreatLikeActivity(z13);
                                                                                }
                                                                                ActivityServiceConnectionsHolder<ConnectionRecord> activityServiceConnectionsHolder = connectionRecord.activity;
                                                                                if (!connectionRecord.hasFlag(128) && activityServiceConnectionsHolder != null && i18 > 0 && activityServiceConnectionsHolder.isActivityVisible()) {
                                                                                    processStateRecord10.setCurRawAdj(0);
                                                                                    if (connectionRecord.notHasFlag(4)) {
                                                                                        i82 = connectionRecord.hasFlag(64) ? 4 : 2;
                                                                                    }
                                                                                    processStateRecord10.setCached(false);
                                                                                    processStateRecord10.setAdjType("service");
                                                                                    processStateRecord10.setAdjTypeCode(2);
                                                                                    processStateRecord10.setAdjSource(activityServiceConnectionsHolder);
                                                                                    processStateRecord10.setAdjSourceProcState(i83);
                                                                                    processStateRecord10.setAdjTarget(runningServiceAt.instanceName);
                                                                                    if (i57 == i58) {
                                                                                        reportOomAdjMessageLocked(str22, "Raise to service w/activity: " + processRecord);
                                                                                    }
                                                                                    i18 = 0;
                                                                                }
                                                                                i81 = i59;
                                                                                z19 = z21;
                                                                            }
                                                                        }
                                                                        z11 = true;
                                                                        boolean z212 = z19 | z11;
                                                                        processRecord5 = processRecord4;
                                                                        if (processRecord5.mOptRecord.shouldNotFreeze()) {
                                                                        }
                                                                        int bfslCapabilityFromClient2 = i81 | getBfslCapabilityFromClient(processRecord5);
                                                                        if (connectionRecord3.notHasFlag(32)) {
                                                                        }
                                                                        if (connectionRecord.hasFlag(134217728)) {
                                                                        }
                                                                        ActivityServiceConnectionsHolder<ConnectionRecord> activityServiceConnectionsHolder2 = connectionRecord.activity;
                                                                        if (!connectionRecord.hasFlag(128)) {
                                                                        }
                                                                        i81 = i59;
                                                                        z19 = z212;
                                                                    }
                                                                    str26 = str21;
                                                                    processServiceRecord6 = processServiceRecord9;
                                                                    str24 = str22;
                                                                    i72 = i57;
                                                                    size = i51;
                                                                    connections = arrayMap;
                                                                    i2 = i53;
                                                                    i3 = i54;
                                                                    i77 = i52;
                                                                    i84 = i50 + 1;
                                                                    i71 = i58;
                                                                    processStateRecord11 = processStateRecord10;
                                                                    valueAt = arrayList;
                                                                }
                                                                str26 = str26;
                                                                processServiceRecord6 = processServiceRecord6;
                                                                i71 = i71;
                                                                str24 = str24;
                                                                i76 = i83;
                                                                connections = connections;
                                                                i2 = i2;
                                                                i3 = i3;
                                                                i77 = i77;
                                                                size--;
                                                                processStateRecord11 = processStateRecord11;
                                                                i17 = i82;
                                                                i43 = i81;
                                                                isCurBoundByNonBgRestrictedApp = z19;
                                                                z6 = z18;
                                                                i42 = 0;
                                                                i72 = i72;
                                                            }
                                                        }
                                                        str26 = str18;
                                                        str24 = str17;
                                                        i72 = i44;
                                                        str25 = str16;
                                                        i2 = i46;
                                                        i3 = i47;
                                                        i77 = i45;
                                                        numberOfRunningServices = i41 - 1;
                                                        i71 = i71;
                                                        processStateRecord11 = processStateRecord7;
                                                        processServiceRecord = processServiceRecord7;
                                                        curCapability3 = i43;
                                                    }
                                                    i42 = 0;
                                                    i69 = 500;
                                                    if (i18 > i69) {
                                                    }
                                                    i76 = i68;
                                                    if (runningServiceAt.isForeground) {
                                                    }
                                                    connections = runningServiceAt.getConnections();
                                                    i43 = curCapability3;
                                                    size = connections.size() - 1;
                                                    i17 = i78;
                                                    while (true) {
                                                        if (size >= 0) {
                                                        }
                                                        str26 = str26;
                                                        processServiceRecord6 = processServiceRecord6;
                                                        i71 = i71;
                                                        str24 = str24;
                                                        i76 = i83;
                                                        connections = connections;
                                                        i2 = i2;
                                                        i3 = i3;
                                                        i77 = i77;
                                                        size--;
                                                        processStateRecord11 = processStateRecord11;
                                                        i17 = i82;
                                                        i43 = i81;
                                                        isCurBoundByNonBgRestrictedApp = z19;
                                                        z6 = z18;
                                                        i42 = 0;
                                                        i72 = i72;
                                                    }
                                                    str26 = str18;
                                                    str24 = str17;
                                                    i72 = i44;
                                                    str25 = str16;
                                                    i2 = i46;
                                                    i3 = i47;
                                                    i77 = i45;
                                                    numberOfRunningServices = i41 - 1;
                                                    i71 = i71;
                                                    processStateRecord11 = processStateRecord7;
                                                    processServiceRecord = processServiceRecord7;
                                                    curCapability3 = i43;
                                                } else if (i18 > 500) {
                                                    processStateRecord11.setAdjType("cch-started-ui-services");
                                                }
                                            }
                                            processServiceRecord6 = processServiceRecord;
                                            str16 = str25;
                                            i41 = numberOfRunningServices;
                                            i42 = 0;
                                            if (runningServiceAt.isForeground) {
                                            }
                                            connections = runningServiceAt.getConnections();
                                            i43 = curCapability3;
                                            size = connections.size() - 1;
                                            i17 = i78;
                                            while (true) {
                                                if (size >= 0) {
                                                }
                                                str26 = str26;
                                                processServiceRecord6 = processServiceRecord6;
                                                i71 = i71;
                                                str24 = str24;
                                                i76 = i83;
                                                connections = connections;
                                                i2 = i2;
                                                i3 = i3;
                                                i77 = i77;
                                                size--;
                                                processStateRecord11 = processStateRecord11;
                                                i17 = i82;
                                                i43 = i81;
                                                isCurBoundByNonBgRestrictedApp = z19;
                                                z6 = z18;
                                                i42 = 0;
                                                i72 = i72;
                                            }
                                            str26 = str18;
                                            str24 = str17;
                                            i72 = i44;
                                            str25 = str16;
                                            i2 = i46;
                                            i3 = i47;
                                            i77 = i45;
                                            numberOfRunningServices = i41 - 1;
                                            i71 = i71;
                                            processStateRecord11 = processStateRecord7;
                                            processServiceRecord = processServiceRecord7;
                                            curCapability3 = i43;
                                        }
                                        z7 = i17;
                                        int i87 = i72;
                                        processServiceRecord2 = processServiceRecord;
                                        processStateRecord = processStateRecord11;
                                        str2 = str25;
                                        i19 = i77;
                                        i20 = i2;
                                        i21 = i3;
                                        i22 = i71;
                                        str3 = str24;
                                        str4 = str26;
                                        processProviderRecord = processRecord.mProviders;
                                        z8 = isCurBoundByNonBgRestrictedApp;
                                        int i88 = curCapability3;
                                        numberOfProviders = processProviderRecord.numberOfProviders() - 1;
                                        i23 = i88;
                                        while (true) {
                                            if (numberOfProviders < 0) {
                                                processProviderRecord2 = processProviderRecord;
                                                processServiceRecord3 = processServiceRecord2;
                                                str5 = str3;
                                                z9 = false;
                                                i24 = i22;
                                                processStateRecord2 = processStateRecord;
                                                break;
                                            }
                                            if (i18 <= 0 && z7 != 0) {
                                                processStateRecord3 = processStateRecord;
                                                if (i76 <= 2) {
                                                    processProviderRecord2 = processProviderRecord;
                                                    processServiceRecord3 = processServiceRecord2;
                                                    str5 = str3;
                                                    z9 = false;
                                                    i24 = i22;
                                                    processStateRecord2 = processStateRecord3;
                                                    break;
                                                }
                                            } else {
                                                processStateRecord3 = processStateRecord;
                                            }
                                            ContentProviderRecord providerAt = processProviderRecord.getProviderAt(numberOfProviders);
                                            ProcessProviderRecord processProviderRecord3 = processProviderRecord;
                                            int i89 = i23;
                                            boolean z23 = z8;
                                            int i90 = i18;
                                            int i91 = z7;
                                            int i92 = i76;
                                            int size2 = providerAt.connections.size() - 1;
                                            int i93 = i92;
                                            while (true) {
                                                if (size2 < 0) {
                                                    i29 = i91;
                                                    i18 = i90;
                                                    i30 = i93;
                                                    i31 = numberOfProviders;
                                                    str6 = str4;
                                                    processServiceRecord5 = processServiceRecord2;
                                                    str7 = str3;
                                                    str8 = str2;
                                                    r7 = 0;
                                                    i32 = 2;
                                                    contentProviderRecord = providerAt;
                                                    i33 = i22;
                                                    processStateRecord4 = processStateRecord3;
                                                    break;
                                                } else if (i90 <= 0 && i91 != 0 && i93 <= 2) {
                                                    i29 = i91;
                                                    i18 = i90;
                                                    i31 = numberOfProviders;
                                                    str6 = str4;
                                                    processServiceRecord5 = processServiceRecord2;
                                                    str7 = str3;
                                                    r7 = 0;
                                                    i32 = 2;
                                                    i30 = i93;
                                                    contentProviderRecord = providerAt;
                                                    i33 = i22;
                                                    processStateRecord4 = processStateRecord3;
                                                    str8 = str2;
                                                    break;
                                                } else {
                                                    ContentProviderConnection contentProviderConnection2 = providerAt.connections.get(size2);
                                                    int i94 = i22;
                                                    ProcessRecord processRecord9 = contentProviderConnection2.client;
                                                    ProcessServiceRecord processServiceRecord11 = processServiceRecord2;
                                                    ProcessStateRecord processStateRecord13 = processRecord9.mState;
                                                    if (processRecord9 == processRecord) {
                                                        i34 = i91;
                                                        i35 = i90;
                                                        i36 = i93;
                                                        i37 = size2;
                                                        contentProviderRecord2 = providerAt;
                                                        i38 = numberOfProviders;
                                                        str9 = str4;
                                                        str10 = str3;
                                                        processStateRecord5 = processStateRecord3;
                                                        str11 = str2;
                                                    } else {
                                                        if (z3) {
                                                            contentProviderConnection = contentProviderConnection2;
                                                            i34 = i91;
                                                            i35 = i90;
                                                            str11 = str2;
                                                            i36 = i93;
                                                            i37 = size2;
                                                            contentProviderRecord2 = providerAt;
                                                            i38 = numberOfProviders;
                                                            processStateRecord5 = processStateRecord3;
                                                            str10 = str3;
                                                            str9 = str4;
                                                            processRecord3 = processRecord9;
                                                            computeOomAdjLSP(processRecord9, i, processRecord2, z, j, z2, true);
                                                        } else {
                                                            contentProviderConnection = contentProviderConnection2;
                                                            i34 = i91;
                                                            i35 = i90;
                                                            i36 = i93;
                                                            i37 = size2;
                                                            contentProviderRecord2 = providerAt;
                                                            i38 = numberOfProviders;
                                                            str9 = str4;
                                                            str10 = str3;
                                                            processStateRecord5 = processStateRecord3;
                                                            str11 = str2;
                                                            processRecord3 = processRecord9;
                                                            processStateRecord13.setCurRawAdj(processStateRecord13.getCurAdj());
                                                            processStateRecord13.setCurRawProcState(processStateRecord13.getCurProcState());
                                                        }
                                                        if (!shouldSkipDueToCycle(processRecord, processStateRecord13, i36, i35, z2)) {
                                                            int curRawAdj2 = processStateRecord13.getCurRawAdj();
                                                            int curRawProcState2 = processStateRecord13.getCurRawProcState();
                                                            i89 |= getBfslCapabilityFromClient(processRecord3);
                                                            if (curRawProcState2 >= 16) {
                                                                curRawProcState2 = 19;
                                                            }
                                                            if (processRecord3.mOptRecord.shouldNotFreeze()) {
                                                                processRecord.mOptRecord.setShouldNotFreeze(true);
                                                            }
                                                            z23 |= processStateRecord13.isCurBoundByNonBgRestrictedApp() || curRawProcState2 <= 3 || (curRawProcState2 == 4 && !processStateRecord13.isBackgroundRestricted());
                                                            int i95 = i35;
                                                            if (i95 > curRawAdj2) {
                                                                if (processStateRecord5.hasShownUi() && !processStateRecord5.getCachedIsHomeProcess()) {
                                                                    if (curRawAdj2 > 200) {
                                                                        str12 = "cch-ui-provider";
                                                                        i90 = i95;
                                                                        processStateRecord6 = processStateRecord5;
                                                                        processStateRecord6.setCached(processStateRecord6.isCached() & processStateRecord13.isCached());
                                                                    }
                                                                }
                                                                i90 = Math.max(curRawAdj2, 0);
                                                                processStateRecord6 = processStateRecord5;
                                                                processStateRecord6.setCurRawAdj(i90);
                                                                str12 = "provider";
                                                                processStateRecord6.setCached(processStateRecord6.isCached() & processStateRecord13.isCached());
                                                            } else {
                                                                processStateRecord6 = processStateRecord5;
                                                                i90 = i95;
                                                                str12 = null;
                                                            }
                                                            if (curRawProcState2 <= 4) {
                                                                if (str12 == null) {
                                                                    str12 = "provider";
                                                                }
                                                                i39 = 2;
                                                                curRawProcState2 = curRawProcState2 == 2 ? 3 : 5;
                                                            } else {
                                                                i39 = 2;
                                                            }
                                                            contentProviderConnection.trackProcState(curRawProcState2, this.mAdjSeq);
                                                            int i96 = i36;
                                                            if (i96 > curRawProcState2) {
                                                                processStateRecord6.setCurRawProcState(curRawProcState2);
                                                                i96 = curRawProcState2;
                                                            }
                                                            if (processStateRecord13.getCurrentSchedulingGroup() > i34) {
                                                                i34 = i39;
                                                            }
                                                            if (str12 != null) {
                                                                processStateRecord6.setAdjType(str12);
                                                                processStateRecord6.setAdjTypeCode(1);
                                                                processStateRecord6.setAdjSource(processRecord3);
                                                                processStateRecord6.setAdjSourceProcState(curRawProcState2);
                                                                contentProviderRecord3 = contentProviderRecord2;
                                                                processStateRecord6.setAdjTarget(contentProviderRecord3.name);
                                                                i40 = i94;
                                                                if (i87 == i40) {
                                                                    StringBuilder sb4 = new StringBuilder();
                                                                    str15 = str11;
                                                                    sb4.append(str15);
                                                                    sb4.append(str12);
                                                                    str13 = str9;
                                                                    sb4.append(str13);
                                                                    sb4.append(processRecord);
                                                                    sb4.append(", due to ");
                                                                    sb4.append(processRecord3);
                                                                    sb4.append(" adj=");
                                                                    sb4.append(i90);
                                                                    sb4.append(" procState=");
                                                                    sb4.append(ProcessList.makeProcStateString(i96));
                                                                    String sb5 = sb4.toString();
                                                                    str14 = str10;
                                                                    reportOomAdjMessageLocked(str14, sb5);
                                                                } else {
                                                                    str13 = str9;
                                                                    str14 = str10;
                                                                    str15 = str11;
                                                                }
                                                            } else {
                                                                str13 = str9;
                                                                i40 = i94;
                                                                str14 = str10;
                                                                str15 = str11;
                                                                contentProviderRecord3 = contentProviderRecord2;
                                                            }
                                                            i36 = i96;
                                                            i91 = i34;
                                                            str2 = str15;
                                                            str4 = str13;
                                                            providerAt = contentProviderRecord3;
                                                            processStateRecord3 = processStateRecord6;
                                                            i22 = i40;
                                                            str3 = str14;
                                                            i93 = i36;
                                                            numberOfProviders = i38;
                                                            processServiceRecord2 = processServiceRecord11;
                                                            size2 = i37 - 1;
                                                        }
                                                    }
                                                    i91 = i34;
                                                    i90 = i35;
                                                    processStateRecord6 = processStateRecord5;
                                                    str13 = str9;
                                                    i40 = i94;
                                                    str14 = str10;
                                                    str15 = str11;
                                                    contentProviderRecord3 = contentProviderRecord2;
                                                    str2 = str15;
                                                    str4 = str13;
                                                    providerAt = contentProviderRecord3;
                                                    processStateRecord3 = processStateRecord6;
                                                    i22 = i40;
                                                    str3 = str14;
                                                    i93 = i36;
                                                    numberOfProviders = i38;
                                                    processServiceRecord2 = processServiceRecord11;
                                                    size2 = i37 - 1;
                                                }
                                            }
                                            if (contentProviderRecord.hasExternalProcessHandles()) {
                                                if (i18 > 0) {
                                                    processStateRecord4.setCurRawAdj(r7);
                                                    processStateRecord4.setCached(r7);
                                                    processStateRecord4.setAdjType("ext-provider");
                                                    processStateRecord4.setAdjTarget(contentProviderRecord.name);
                                                    if (i87 == i33) {
                                                        reportOomAdjMessageLocked(str7, "Raise adj to external provider: " + processRecord);
                                                    }
                                                    i18 = r7;
                                                    i29 = i32;
                                                }
                                                if (i30 > 6) {
                                                    processStateRecord4.setCurRawProcState(6);
                                                    if (i87 == i33) {
                                                        reportOomAdjMessageLocked(str7, "Raise procstate to external provider: " + processRecord);
                                                    }
                                                    i30 = 6;
                                                }
                                            }
                                            z7 = i29;
                                            numberOfProviders = i31 - 1;
                                            str2 = str8;
                                            str4 = str6;
                                            processStateRecord = processStateRecord4;
                                            i22 = i33;
                                            str3 = str7;
                                            processProviderRecord = processProviderRecord3;
                                            z8 = z23;
                                            processServiceRecord2 = processServiceRecord5;
                                            i76 = i30;
                                            i23 = i89;
                                        }
                                        if (processProviderRecord2.getLastProviderTime() > 0 && processProviderRecord2.getLastProviderTime() + this.mConstants.CONTENT_PROVIDER_RETAIN_TIME > j) {
                                            if (i18 <= 700) {
                                                processStateRecord2.setCached(z9);
                                                processStateRecord2.setAdjType("recent-provider");
                                                i28 = i87;
                                                if (i28 == i24) {
                                                    reportOomAdjMessageLocked(str5, "Raise adj to recent provider: " + processRecord);
                                                }
                                                i18 = 700;
                                                z7 = z9;
                                            } else {
                                                i28 = i87;
                                                z7 = z7;
                                            }
                                            if (i76 > 15) {
                                                processStateRecord2.setAdjType("recent-provider");
                                                if (i28 == i24) {
                                                    reportOomAdjMessageLocked(str5, "Raise procstate to recent provider: " + processRecord);
                                                }
                                                i25 = 15;
                                                if (i25 >= 19) {
                                                    if (processServiceRecord3.hasClientActivities()) {
                                                        processStateRecord2.setAdjType("cch-client-act");
                                                        i26 = 17;
                                                        i27 = 500;
                                                        if (i18 == i27) {
                                                            if (z && !z2) {
                                                                processStateRecord2.setServiceB(this.mNewNumAServiceProcs > this.mNumServiceProcs / 3 ? true : z9);
                                                                this.mNewNumServiceProcs++;
                                                                if (!processStateRecord2.isServiceB()) {
                                                                    if (!this.mService.mAppProfiler.isLastMemoryLevelNormal() && processRecord.mProfile.getLastPss() >= this.mProcessList.getCachedRestoreThresholdKb()) {
                                                                        processStateRecord2.setServiceHighRam(true);
                                                                        processStateRecord2.setServiceB(true);
                                                                    } else {
                                                                        this.mNewNumAServiceProcs++;
                                                                    }
                                                                } else {
                                                                    processStateRecord2.setServiceHighRam(z9);
                                                                }
                                                            }
                                                            if (processStateRecord2.isServiceB()) {
                                                                i18 = 800;
                                                            }
                                                        }
                                                        processStateRecord2.setCurRawAdj(i18);
                                                        processServiceRecord4 = processServiceRecord3;
                                                        int modifyRawOomAdj = processServiceRecord4.modifyRawOomAdj(i18);
                                                        int i97 = (modifyRawOomAdj > processStateRecord2.getMaxAdj() || (modifyRawOomAdj = processStateRecord2.getMaxAdj()) > 250) ? z7 : 2;
                                                        if (i26 < 5) {
                                                            z10 = true;
                                                            if (this.mService.mWakefulness.get() != 1 && !z6 && i97 > 1) {
                                                                i97 = 1;
                                                            }
                                                        } else {
                                                            z10 = true;
                                                        }
                                                        if (processServiceRecord4.hasForegroundServices()) {
                                                            i23 |= i14;
                                                        }
                                                        int defaultCapability = i23 | getDefaultCapability(processRecord, i26);
                                                        if (i26 > 5) {
                                                            defaultCapability &= -17;
                                                        }
                                                        processStateRecord2.setCurAdj(modifyRawOomAdj);
                                                        processStateRecord2.setCurCapability(defaultCapability);
                                                        processStateRecord2.setCurrentSchedulingGroup(i97);
                                                        processStateRecord2.setCurProcState(i26);
                                                        processStateRecord2.setCurRawProcState(i26);
                                                        processStateRecord2.updateLastInvisibleTime(z16);
                                                        processStateRecord2.setHasForegroundActivities(z15);
                                                        processStateRecord2.setCompletedAdjSeq(this.mAdjSeq);
                                                        processStateRecord2.setCurBoundByNonBgRestrictedApp(z8);
                                                        return (processStateRecord2.getCurAdj() >= i21 || processStateRecord2.getCurProcState() < i20 || processStateRecord2.getCurCapability() != i19) ? z10 : z9;
                                                    } else if (processServiceRecord3.isTreatedLikeActivity()) {
                                                        processStateRecord2.setAdjType("cch-as-act");
                                                        i27 = 500;
                                                        i26 = 16;
                                                        if (i18 == i27) {
                                                        }
                                                        processStateRecord2.setCurRawAdj(i18);
                                                        processServiceRecord4 = processServiceRecord3;
                                                        int modifyRawOomAdj2 = processServiceRecord4.modifyRawOomAdj(i18);
                                                        if (modifyRawOomAdj2 > processStateRecord2.getMaxAdj()) {
                                                        }
                                                        if (i26 < 5) {
                                                        }
                                                        if (processServiceRecord4.hasForegroundServices()) {
                                                        }
                                                        int defaultCapability2 = i23 | getDefaultCapability(processRecord, i26);
                                                        if (i26 > 5) {
                                                        }
                                                        processStateRecord2.setCurAdj(modifyRawOomAdj2);
                                                        processStateRecord2.setCurCapability(defaultCapability2);
                                                        processStateRecord2.setCurrentSchedulingGroup(i97);
                                                        processStateRecord2.setCurProcState(i26);
                                                        processStateRecord2.setCurRawProcState(i26);
                                                        processStateRecord2.updateLastInvisibleTime(z16);
                                                        processStateRecord2.setHasForegroundActivities(z15);
                                                        processStateRecord2.setCompletedAdjSeq(this.mAdjSeq);
                                                        processStateRecord2.setCurBoundByNonBgRestrictedApp(z8);
                                                        if (processStateRecord2.getCurAdj() >= i21) {
                                                        }
                                                    }
                                                }
                                                i26 = i25;
                                                i27 = 500;
                                                if (i18 == i27) {
                                                }
                                                processStateRecord2.setCurRawAdj(i18);
                                                processServiceRecord4 = processServiceRecord3;
                                                int modifyRawOomAdj22 = processServiceRecord4.modifyRawOomAdj(i18);
                                                if (modifyRawOomAdj22 > processStateRecord2.getMaxAdj()) {
                                                }
                                                if (i26 < 5) {
                                                }
                                                if (processServiceRecord4.hasForegroundServices()) {
                                                }
                                                int defaultCapability22 = i23 | getDefaultCapability(processRecord, i26);
                                                if (i26 > 5) {
                                                }
                                                processStateRecord2.setCurAdj(modifyRawOomAdj22);
                                                processStateRecord2.setCurCapability(defaultCapability22);
                                                processStateRecord2.setCurrentSchedulingGroup(i97);
                                                processStateRecord2.setCurProcState(i26);
                                                processStateRecord2.setCurRawProcState(i26);
                                                processStateRecord2.updateLastInvisibleTime(z16);
                                                processStateRecord2.setHasForegroundActivities(z15);
                                                processStateRecord2.setCompletedAdjSeq(this.mAdjSeq);
                                                processStateRecord2.setCurBoundByNonBgRestrictedApp(z8);
                                                if (processStateRecord2.getCurAdj() >= i21) {
                                                }
                                            }
                                        }
                                        i25 = i76;
                                        if (i25 >= 19) {
                                        }
                                        i26 = i25;
                                        i27 = 500;
                                        if (i18 == i27) {
                                        }
                                        processStateRecord2.setCurRawAdj(i18);
                                        processServiceRecord4 = processServiceRecord3;
                                        int modifyRawOomAdj222 = processServiceRecord4.modifyRawOomAdj(i18);
                                        if (modifyRawOomAdj222 > processStateRecord2.getMaxAdj()) {
                                        }
                                        if (i26 < 5) {
                                        }
                                        if (processServiceRecord4.hasForegroundServices()) {
                                        }
                                        int defaultCapability222 = i23 | getDefaultCapability(processRecord, i26);
                                        if (i26 > 5) {
                                        }
                                        processStateRecord2.setCurAdj(modifyRawOomAdj222);
                                        processStateRecord2.setCurCapability(defaultCapability222);
                                        processStateRecord2.setCurrentSchedulingGroup(i97);
                                        processStateRecord2.setCurProcState(i26);
                                        processStateRecord2.setCurRawProcState(i26);
                                        processStateRecord2.updateLastInvisibleTime(z16);
                                        processStateRecord2.setHasForegroundActivities(z15);
                                        processStateRecord2.setCompletedAdjSeq(this.mAdjSeq);
                                        processStateRecord2.setCurBoundByNonBgRestrictedApp(z8);
                                        if (processStateRecord2.getCurAdj() >= i21) {
                                        }
                                    }
                                    if (i16 > 700) {
                                        processStateRecord11.setCached(false);
                                        processStateRecord11.setAdjType("previous");
                                        if (i72 == i71) {
                                            reportOomAdjMessageLocked("ActivityManager", "Raise adj to prev: " + processRecord);
                                        }
                                        i16 = 700;
                                        i6 = 0;
                                    }
                                    if (i76 > 15) {
                                        processStateRecord11.setAdjType("previous");
                                        if (i72 == i71) {
                                            reportOomAdjMessageLocked("ActivityManager", "Raise procstate to prev: " + processRecord);
                                        }
                                        i76 = 15;
                                    }
                                }
                                i17 = i6;
                                if (z2) {
                                }
                                processStateRecord11.setCurRawAdj(i16);
                                processStateRecord11.setCurRawProcState(i76);
                                processStateRecord11.setHasStartedServices(false);
                                processStateRecord11.setAdjSeq(this.mAdjSeq);
                                backupRecord = this.mService.mBackupTargets.get(processRecord.userId);
                                if (backupRecord == null) {
                                }
                                isCurBoundByNonBgRestrictedApp = processStateRecord11.isCurBoundByNonBgRestrictedApp();
                                i18 = i16;
                                numberOfRunningServices = processServiceRecord.numberOfRunningServices() - 1;
                                z6 = false;
                                while (numberOfRunningServices >= 0) {
                                    runningServiceAt = processServiceRecord.getRunningServiceAt(numberOfRunningServices);
                                    int i782 = i17;
                                    if (runningServiceAt.startRequested) {
                                    }
                                    processServiceRecord6 = processServiceRecord;
                                    str16 = str25;
                                    i41 = numberOfRunningServices;
                                    i42 = 0;
                                    if (runningServiceAt.isForeground) {
                                    }
                                    connections = runningServiceAt.getConnections();
                                    i43 = curCapability3;
                                    size = connections.size() - 1;
                                    i17 = i782;
                                    while (true) {
                                        if (size >= 0) {
                                        }
                                        str26 = str26;
                                        processServiceRecord6 = processServiceRecord6;
                                        i71 = i71;
                                        str24 = str24;
                                        i76 = i83;
                                        connections = connections;
                                        i2 = i2;
                                        i3 = i3;
                                        i77 = i77;
                                        size--;
                                        processStateRecord11 = processStateRecord11;
                                        i17 = i82;
                                        i43 = i81;
                                        isCurBoundByNonBgRestrictedApp = z19;
                                        z6 = z18;
                                        i42 = 0;
                                        i72 = i72;
                                    }
                                    str26 = str18;
                                    str24 = str17;
                                    i72 = i44;
                                    str25 = str16;
                                    i2 = i46;
                                    i3 = i47;
                                    i77 = i45;
                                    numberOfRunningServices = i41 - 1;
                                    i71 = i71;
                                    processStateRecord11 = processStateRecord7;
                                    processServiceRecord = processServiceRecord7;
                                    curCapability3 = i43;
                                }
                                z7 = i17;
                                int i872 = i72;
                                processServiceRecord2 = processServiceRecord;
                                processStateRecord = processStateRecord11;
                                str2 = str25;
                                i19 = i77;
                                i20 = i2;
                                i21 = i3;
                                i22 = i71;
                                str3 = str24;
                                str4 = str26;
                                processProviderRecord = processRecord.mProviders;
                                z8 = isCurBoundByNonBgRestrictedApp;
                                int i882 = curCapability3;
                                numberOfProviders = processProviderRecord.numberOfProviders() - 1;
                                i23 = i882;
                                while (true) {
                                    if (numberOfProviders < 0) {
                                    }
                                    z7 = i29;
                                    numberOfProviders = i31 - 1;
                                    str2 = str8;
                                    str4 = str6;
                                    processStateRecord = processStateRecord4;
                                    i22 = i33;
                                    str3 = str7;
                                    processProviderRecord = processProviderRecord3;
                                    z8 = z23;
                                    processServiceRecord2 = processServiceRecord5;
                                    i76 = i30;
                                    i23 = i89;
                                }
                                if (processProviderRecord2.getLastProviderTime() > 0) {
                                    if (i18 <= 700) {
                                    }
                                    if (i76 > 15) {
                                    }
                                }
                                i25 = i76;
                                if (i25 >= 19) {
                                }
                                i26 = i25;
                                i27 = 500;
                                if (i18 == i27) {
                                }
                                processStateRecord2.setCurRawAdj(i18);
                                processServiceRecord4 = processServiceRecord3;
                                int modifyRawOomAdj2222 = processServiceRecord4.modifyRawOomAdj(i18);
                                if (modifyRawOomAdj2222 > processStateRecord2.getMaxAdj()) {
                                }
                                if (i26 < 5) {
                                }
                                if (processServiceRecord4.hasForegroundServices()) {
                                }
                                int defaultCapability2222 = i23 | getDefaultCapability(processRecord, i26);
                                if (i26 > 5) {
                                }
                                processStateRecord2.setCurAdj(modifyRawOomAdj2222);
                                processStateRecord2.setCurCapability(defaultCapability2222);
                                processStateRecord2.setCurrentSchedulingGroup(i97);
                                processStateRecord2.setCurProcState(i26);
                                processStateRecord2.setCurRawProcState(i26);
                                processStateRecord2.updateLastInvisibleTime(z16);
                                processStateRecord2.setHasForegroundActivities(z15);
                                processStateRecord2.setCompletedAdjSeq(this.mAdjSeq);
                                processStateRecord2.setCurBoundByNonBgRestrictedApp(z8);
                                if (processStateRecord2.getCurAdj() >= i21) {
                                }
                            }
                            i16 = i15;
                            if (i16 <= 200) {
                            }
                            processStateRecord11.setCached(false);
                            processStateRecord11.setAdjType("force-imp");
                            processStateRecord11.setAdjSource(processStateRecord11.getForcingToImportant());
                            if (i72 == i71) {
                            }
                            i76 = 8;
                            i16 = 200;
                            i6 = 2;
                            if (processStateRecord11.getCachedIsHeavyWeight()) {
                            }
                            if (processStateRecord11.getCachedIsHomeProcess()) {
                            }
                            String str262 = ": ";
                            if (processStateRecord11.getCachedIsPreviousProcess()) {
                                if (i76 < 15) {
                                }
                                if (i16 > 700) {
                                }
                                if (i76 > 15) {
                                }
                            }
                            i17 = i6;
                            if (z2) {
                            }
                            processStateRecord11.setCurRawAdj(i16);
                            processStateRecord11.setCurRawProcState(i76);
                            processStateRecord11.setHasStartedServices(false);
                            processStateRecord11.setAdjSeq(this.mAdjSeq);
                            backupRecord = this.mService.mBackupTargets.get(processRecord.userId);
                            if (backupRecord == null) {
                            }
                            isCurBoundByNonBgRestrictedApp = processStateRecord11.isCurBoundByNonBgRestrictedApp();
                            i18 = i16;
                            numberOfRunningServices = processServiceRecord.numberOfRunningServices() - 1;
                            z6 = false;
                            while (numberOfRunningServices >= 0) {
                            }
                            z7 = i17;
                            int i8722 = i72;
                            processServiceRecord2 = processServiceRecord;
                            processStateRecord = processStateRecord11;
                            str2 = str25;
                            i19 = i77;
                            i20 = i2;
                            i21 = i3;
                            i22 = i71;
                            str3 = str24;
                            str4 = str262;
                            processProviderRecord = processRecord.mProviders;
                            z8 = isCurBoundByNonBgRestrictedApp;
                            int i8822 = curCapability3;
                            numberOfProviders = processProviderRecord.numberOfProviders() - 1;
                            i23 = i8822;
                            while (true) {
                                if (numberOfProviders < 0) {
                                }
                                z7 = i29;
                                numberOfProviders = i31 - 1;
                                str2 = str8;
                                str4 = str6;
                                processStateRecord = processStateRecord4;
                                i22 = i33;
                                str3 = str7;
                                processProviderRecord = processProviderRecord3;
                                z8 = z23;
                                processServiceRecord2 = processServiceRecord5;
                                i76 = i30;
                                i23 = i89;
                            }
                            if (processProviderRecord2.getLastProviderTime() > 0) {
                            }
                            i25 = i76;
                            if (i25 >= 19) {
                            }
                            i26 = i25;
                            i27 = 500;
                            if (i18 == i27) {
                            }
                            processStateRecord2.setCurRawAdj(i18);
                            processServiceRecord4 = processServiceRecord3;
                            int modifyRawOomAdj22222 = processServiceRecord4.modifyRawOomAdj(i18);
                            if (modifyRawOomAdj22222 > processStateRecord2.getMaxAdj()) {
                            }
                            if (i26 < 5) {
                            }
                            if (processServiceRecord4.hasForegroundServices()) {
                            }
                            int defaultCapability22222 = i23 | getDefaultCapability(processRecord, i26);
                            if (i26 > 5) {
                            }
                            processStateRecord2.setCurAdj(modifyRawOomAdj22222);
                            processStateRecord2.setCurCapability(defaultCapability22222);
                            processStateRecord2.setCurrentSchedulingGroup(i97);
                            processStateRecord2.setCurProcState(i26);
                            processStateRecord2.setCurRawProcState(i26);
                            processStateRecord2.updateLastInvisibleTime(z16);
                            processStateRecord2.setHasForegroundActivities(z15);
                            processStateRecord2.setCompletedAdjSeq(this.mAdjSeq);
                            processStateRecord2.setCurBoundByNonBgRestrictedApp(z8);
                            if (processStateRecord2.getCurAdj() >= i21) {
                            }
                        }
                    }
                    i15 = i13;
                    if (processServiceRecord.hasTopStartedAlmostPerceptibleServices()) {
                        i16 = i15;
                        if (processStateRecord11.getLastTopTime() + this.mConstants.TOP_TO_ALMOST_PERCEPTIBLE_GRACE_DURATION <= j) {
                        }
                        processStateRecord11.setAdjType("top-ej-act");
                        if (i72 == i71) {
                        }
                        i15 = 52;
                    }
                    i16 = i15;
                    if (i16 <= 200) {
                    }
                    processStateRecord11.setCached(false);
                    processStateRecord11.setAdjType("force-imp");
                    processStateRecord11.setAdjSource(processStateRecord11.getForcingToImportant());
                    if (i72 == i71) {
                    }
                    i76 = 8;
                    i16 = 200;
                    i6 = 2;
                    if (processStateRecord11.getCachedIsHeavyWeight()) {
                    }
                    if (processStateRecord11.getCachedIsHomeProcess()) {
                    }
                    String str2622 = ": ";
                    if (processStateRecord11.getCachedIsPreviousProcess()) {
                    }
                    i17 = i6;
                    if (z2) {
                    }
                    processStateRecord11.setCurRawAdj(i16);
                    processStateRecord11.setCurRawProcState(i76);
                    processStateRecord11.setHasStartedServices(false);
                    processStateRecord11.setAdjSeq(this.mAdjSeq);
                    backupRecord = this.mService.mBackupTargets.get(processRecord.userId);
                    if (backupRecord == null) {
                    }
                    isCurBoundByNonBgRestrictedApp = processStateRecord11.isCurBoundByNonBgRestrictedApp();
                    i18 = i16;
                    numberOfRunningServices = processServiceRecord.numberOfRunningServices() - 1;
                    z6 = false;
                    while (numberOfRunningServices >= 0) {
                    }
                    z7 = i17;
                    int i87222 = i72;
                    processServiceRecord2 = processServiceRecord;
                    processStateRecord = processStateRecord11;
                    str2 = str25;
                    i19 = i77;
                    i20 = i2;
                    i21 = i3;
                    i22 = i71;
                    str3 = str24;
                    str4 = str2622;
                    processProviderRecord = processRecord.mProviders;
                    z8 = isCurBoundByNonBgRestrictedApp;
                    int i88222 = curCapability3;
                    numberOfProviders = processProviderRecord.numberOfProviders() - 1;
                    i23 = i88222;
                    while (true) {
                        if (numberOfProviders < 0) {
                        }
                        z7 = i29;
                        numberOfProviders = i31 - 1;
                        str2 = str8;
                        str4 = str6;
                        processStateRecord = processStateRecord4;
                        i22 = i33;
                        str3 = str7;
                        processProviderRecord = processProviderRecord3;
                        z8 = z23;
                        processServiceRecord2 = processServiceRecord5;
                        i76 = i30;
                        i23 = i89;
                    }
                    if (processProviderRecord2.getLastProviderTime() > 0) {
                    }
                    i25 = i76;
                    if (i25 >= 19) {
                    }
                    i26 = i25;
                    i27 = 500;
                    if (i18 == i27) {
                    }
                    processStateRecord2.setCurRawAdj(i18);
                    processServiceRecord4 = processServiceRecord3;
                    int modifyRawOomAdj222222 = processServiceRecord4.modifyRawOomAdj(i18);
                    if (modifyRawOomAdj222222 > processStateRecord2.getMaxAdj()) {
                    }
                    if (i26 < 5) {
                    }
                    if (processServiceRecord4.hasForegroundServices()) {
                    }
                    int defaultCapability222222 = i23 | getDefaultCapability(processRecord, i26);
                    if (i26 > 5) {
                    }
                    processStateRecord2.setCurAdj(modifyRawOomAdj222222);
                    processStateRecord2.setCurCapability(defaultCapability222222);
                    processStateRecord2.setCurrentSchedulingGroup(i97);
                    processStateRecord2.setCurProcState(i26);
                    processStateRecord2.setCurRawProcState(i26);
                    processStateRecord2.updateLastInvisibleTime(z16);
                    processStateRecord2.setHasForegroundActivities(z15);
                    processStateRecord2.setCompletedAdjSeq(this.mAdjSeq);
                    processStateRecord2.setCurBoundByNonBgRestrictedApp(z8);
                    if (processStateRecord2.getCurAdj() >= i21) {
                    }
                }
            } else {
                i8 = 4;
            }
            if (hasForegroundServices && hasNonShortForegroundServices) {
                str = "fg-service";
                i10 = i8;
                i9 = 200;
                i11 = 16;
            } else {
                if (z17) {
                    str = "fg-service-short";
                    i9 = 226;
                    i10 = i8;
                } else if (processStateRecord11.hasOverlayUi()) {
                    str = "has-overlay-ui";
                    i9 = 200;
                    i10 = 6;
                } else {
                    str = null;
                    i9 = 0;
                    i10 = 0;
                }
                i11 = 0;
            }
            if (str != null) {
                processStateRecord11.setAdjType(str);
                processStateRecord11.setCached(false);
                if (i72 == i71) {
                    reportOomAdjMessageLocked("ActivityManager", "Raise to " + str + ": " + processRecord + " ");
                }
                i75 = i9;
                i76 = i10;
                i12 = i11;
                i6 = 2;
            } else {
                i12 = i11;
            }
            if (processServiceRecord.hasForegroundServices()) {
            }
            i13 = i75;
            i14 = i12;
            i15 = i13;
            if (processServiceRecord.hasTopStartedAlmostPerceptibleServices()) {
            }
            i16 = i15;
            if (i16 <= 200) {
            }
            processStateRecord11.setCached(false);
            processStateRecord11.setAdjType("force-imp");
            processStateRecord11.setAdjSource(processStateRecord11.getForcingToImportant());
            if (i72 == i71) {
            }
            i76 = 8;
            i16 = 200;
            i6 = 2;
            if (processStateRecord11.getCachedIsHeavyWeight()) {
            }
            if (processStateRecord11.getCachedIsHomeProcess()) {
            }
            String str26222 = ": ";
            if (processStateRecord11.getCachedIsPreviousProcess()) {
            }
            i17 = i6;
            if (z2) {
            }
            processStateRecord11.setCurRawAdj(i16);
            processStateRecord11.setCurRawProcState(i76);
            processStateRecord11.setHasStartedServices(false);
            processStateRecord11.setAdjSeq(this.mAdjSeq);
            backupRecord = this.mService.mBackupTargets.get(processRecord.userId);
            if (backupRecord == null) {
            }
            isCurBoundByNonBgRestrictedApp = processStateRecord11.isCurBoundByNonBgRestrictedApp();
            i18 = i16;
            numberOfRunningServices = processServiceRecord.numberOfRunningServices() - 1;
            z6 = false;
            while (numberOfRunningServices >= 0) {
            }
            z7 = i17;
            int i872222 = i72;
            processServiceRecord2 = processServiceRecord;
            processStateRecord = processStateRecord11;
            str2 = str25;
            i19 = i77;
            i20 = i2;
            i21 = i3;
            i22 = i71;
            str3 = str24;
            str4 = str26222;
            processProviderRecord = processRecord.mProviders;
            z8 = isCurBoundByNonBgRestrictedApp;
            int i882222 = curCapability3;
            numberOfProviders = processProviderRecord.numberOfProviders() - 1;
            i23 = i882222;
            while (true) {
                if (numberOfProviders < 0) {
                }
                z7 = i29;
                numberOfProviders = i31 - 1;
                str2 = str8;
                str4 = str6;
                processStateRecord = processStateRecord4;
                i22 = i33;
                str3 = str7;
                processProviderRecord = processProviderRecord3;
                z8 = z23;
                processServiceRecord2 = processServiceRecord5;
                i76 = i30;
                i23 = i89;
            }
            if (processProviderRecord2.getLastProviderTime() > 0) {
            }
            i25 = i76;
            if (i25 >= 19) {
            }
            i26 = i25;
            i27 = 500;
            if (i18 == i27) {
            }
            processStateRecord2.setCurRawAdj(i18);
            processServiceRecord4 = processServiceRecord3;
            int modifyRawOomAdj2222222 = processServiceRecord4.modifyRawOomAdj(i18);
            if (modifyRawOomAdj2222222 > processStateRecord2.getMaxAdj()) {
            }
            if (i26 < 5) {
            }
            if (processServiceRecord4.hasForegroundServices()) {
            }
            int defaultCapability2222222 = i23 | getDefaultCapability(processRecord, i26);
            if (i26 > 5) {
            }
            processStateRecord2.setCurAdj(modifyRawOomAdj2222222);
            processStateRecord2.setCurCapability(defaultCapability2222222);
            processStateRecord2.setCurrentSchedulingGroup(i97);
            processStateRecord2.setCurProcState(i26);
            processStateRecord2.setCurRawProcState(i26);
            processStateRecord2.updateLastInvisibleTime(z16);
            processStateRecord2.setHasForegroundActivities(z15);
            processStateRecord2.setCompletedAdjSeq(this.mAdjSeq);
            processStateRecord2.setCurBoundByNonBgRestrictedApp(z8);
            if (processStateRecord2.getCurAdj() >= i21) {
            }
        }
    }

    public final int getDefaultCapability(ProcessRecord processRecord, int i) {
        int i2;
        int defaultProcessNetworkCapabilities = NetworkPolicyManager.getDefaultProcessNetworkCapabilities(i);
        if (i == 0 || i == 1 || i == 2) {
            i2 = 31;
        } else if (i != 3) {
            i2 = 0;
            if (i == 4 && processRecord.getActiveInstrumentation() != null) {
                i2 = 6;
            }
        } else {
            i2 = 16;
        }
        return defaultProcessNetworkCapabilities | i2;
    }

    public int getBfslCapabilityFromClient(ProcessRecord processRecord) {
        if (processRecord.mState.getCurProcState() < 4) {
            return 16;
        }
        return processRecord.mState.getCurCapability() & 16;
    }

    public final boolean shouldSkipDueToCycle(ProcessRecord processRecord, ProcessStateRecord processStateRecord, int i, int i2, boolean z) {
        if (processStateRecord.containsCycle()) {
            processRecord.mState.setContainsCycle(true);
            this.mProcessesInCycle.add(processRecord);
            if (processStateRecord.getCompletedAdjSeq() < this.mAdjSeq) {
                return !z || (processStateRecord.getCurRawProcState() >= i && processStateRecord.getCurRawAdj() >= i2);
            }
            return false;
        }
        return false;
    }

    @GuardedBy({"mService"})
    public final void reportOomAdjMessageLocked(String str, String str2) {
        Slog.d(str, str2);
        synchronized (this.mService.mOomAdjObserverLock) {
            ActivityManagerService activityManagerService = this.mService;
            if (activityManagerService.mCurOomAdjObserver != null) {
                activityManagerService.mUiHandler.obtainMessage(70, str2).sendToTarget();
            }
        }
    }

    public void onWakefulnessChanged(int i) {
        this.mCachedAppOptimizer.onWakefulnessChanged(i);
    }

    /* JADX WARN: Removed duplicated region for block: B:191:0x0285 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:87:0x022c  */
    /* JADX WARN: Removed duplicated region for block: B:88:0x0238  */
    /* JADX WARN: Removed duplicated region for block: B:91:0x0249  */
    @GuardedBy({"mService", "mProcLock"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final boolean applyOomAdjLSP(final ProcessRecord processRecord, boolean z, long j, long j2, int i) {
        boolean z2;
        int i2;
        int i3;
        boolean z3;
        long j3;
        long j4;
        ProcessStateRecord processStateRecord = processRecord.mState;
        UidRecord uidRecord = processRecord.getUidRecord();
        if (processStateRecord.getCurRawAdj() != processStateRecord.getSetRawAdj()) {
            processStateRecord.setSetRawAdj(processStateRecord.getCurRawAdj());
        }
        int i4 = 5;
        if (this.mCachedAppOptimizer.useCompaction() && this.mService.mBooted) {
            if (processStateRecord.getCurAdj() != processStateRecord.getSetAdj()) {
                this.mCachedAppOptimizer.onOomAdjustChanged(processStateRecord.getSetAdj(), processStateRecord.getCurAdj(), processRecord);
            } else if (this.mService.mWakefulness.get() != 1) {
                if (processStateRecord.getSetAdj() < 0 && !processStateRecord.isRunningRemoteAnimation() && this.mCachedAppOptimizer.shouldCompactPersistent(processRecord, j)) {
                    this.mCachedAppOptimizer.compactApp(processRecord, CachedAppOptimizer.CompactProfile.FULL, CachedAppOptimizer.CompactSource.PERSISTENT, false);
                } else if (processStateRecord.getCurProcState() == 5 && this.mCachedAppOptimizer.shouldCompactBFGS(processRecord, j)) {
                    this.mCachedAppOptimizer.compactApp(processRecord, CachedAppOptimizer.CompactProfile.FULL, CachedAppOptimizer.CompactSource.BFGS, false);
                }
            }
        }
        if (processStateRecord.getCurAdj() != processStateRecord.getSetAdj()) {
            ProcessList.setOomAdj(processRecord.getPid(), processRecord.uid, processStateRecord.getCurAdj());
            if (this.mService.mCurOomAdjUid == processRecord.info.uid) {
                reportOomAdjMessageLocked("ActivityManager", "Set " + processRecord.getPid() + " " + processRecord.processName + " adj " + processStateRecord.getCurAdj() + ": " + processStateRecord.getAdjType());
            }
            processStateRecord.setSetAdj(processStateRecord.getCurAdj());
            if (uidRecord != null) {
                uidRecord.noteProcAdjChanged();
            }
            processStateRecord.setVerifiedAdj(-10000);
        }
        int currentSchedulingGroup = processStateRecord.getCurrentSchedulingGroup();
        if (processStateRecord.getSetSchedGroup() != currentSchedulingGroup) {
            int setSchedGroup = processStateRecord.getSetSchedGroup();
            processStateRecord.setSetSchedGroup(currentSchedulingGroup);
            if (this.mService.mCurOomAdjUid == processRecord.uid) {
                reportOomAdjMessageLocked("ActivityManager", "Setting sched group of " + processRecord.processName + " to " + currentSchedulingGroup + ": " + processStateRecord.getAdjType());
            }
            if (processRecord.getWaitingToKill() != null && processRecord.mReceivers.numberOfCurReceivers() == 0 && ActivityManager.isProcStateBackground(processStateRecord.getSetProcState())) {
                processRecord.killLocked(processRecord.getWaitingToKill(), 10, 22, true);
                z2 = false;
                if (processStateRecord.hasRepForegroundActivities() == processStateRecord.hasForegroundActivities()) {
                    processStateRecord.setRepForegroundActivities(processStateRecord.hasForegroundActivities());
                    i2 = i;
                    i3 = 1;
                } else {
                    i2 = i;
                    i3 = 0;
                }
                updateAppFreezeStateLSP(processRecord, i2);
                if (processStateRecord.getReportedProcState() != processStateRecord.getCurProcState()) {
                    processStateRecord.setReportedProcState(processStateRecord.getCurProcState());
                    if (processRecord.getThread() != null) {
                        try {
                            processRecord.getThread().setProcessState(processStateRecord.getReportedProcState());
                        } catch (RemoteException unused) {
                        }
                    }
                }
                if (processStateRecord.getSetProcState() != 20 || ProcessList.procStatesDifferForMem(processStateRecord.getCurProcState(), processStateRecord.getSetProcState())) {
                    processStateRecord.setLastStateTime(j);
                    z3 = true;
                } else {
                    z3 = false;
                }
                synchronized (this.mService.mAppProfiler.mProfilerLock) {
                    try {
                        try {
                            processRecord.mProfile.updateProcState(processRecord.mState);
                            this.mService.mAppProfiler.updateNextPssTimeLPf(processStateRecord.getCurProcState(), processRecord.mProfile, j, z3);
                            if (processStateRecord.getSetProcState() != processStateRecord.getCurProcState()) {
                                if (this.mService.mCurOomAdjUid == processRecord.uid) {
                                    reportOomAdjMessageLocked("ActivityManager", "Proc state change of " + processRecord.processName + " to " + ProcessList.makeProcStateString(processStateRecord.getCurProcState()) + " (" + processStateRecord.getCurProcState() + "): " + processStateRecord.getAdjType());
                                }
                                boolean z4 = processStateRecord.getSetProcState() < 10;
                                boolean z5 = processStateRecord.getCurProcState() < 10;
                                if (z4 && !z5) {
                                    processStateRecord.setWhenUnimportant(j);
                                    processRecord.mProfile.mLastCpuTime.set(0L);
                                }
                                maybeUpdateUsageStatsLSP(processRecord, j2);
                                maybeUpdateLastTopTime(processStateRecord, j);
                                processStateRecord.setSetProcState(processStateRecord.getCurProcState());
                                if (processStateRecord.getSetProcState() >= 14) {
                                    processStateRecord.setNotCachedSinceIdle(false);
                                }
                                if (!z) {
                                    synchronized (this.mService.mProcessStats.mLock) {
                                        ActivityManagerService activityManagerService = this.mService;
                                        activityManagerService.setProcessTrackerStateLOSP(processRecord, activityManagerService.mProcessStats.getMemFactorLocked());
                                    }
                                } else {
                                    processStateRecord.setProcStateChanged(true);
                                }
                            } else if (processStateRecord.hasReportedInteraction()) {
                                if (processStateRecord.getCachedCompatChange(2)) {
                                    j4 = this.mConstants.USAGE_STATS_INTERACTION_INTERVAL_POST_S;
                                } else {
                                    j4 = this.mConstants.USAGE_STATS_INTERACTION_INTERVAL_PRE_S;
                                }
                                if (j2 - processStateRecord.getInteractionEventTime() > j4) {
                                    maybeUpdateUsageStatsLSP(processRecord, j2);
                                }
                            } else {
                                if (processStateRecord.getCachedCompatChange(2)) {
                                    j3 = this.mConstants.SERVICE_USAGE_INTERACTION_TIME_POST_S;
                                } else {
                                    j3 = this.mConstants.SERVICE_USAGE_INTERACTION_TIME_PRE_S;
                                }
                                if (j2 - processStateRecord.getFgInteractionTime() > j3) {
                                    maybeUpdateUsageStatsLSP(processRecord, j2);
                                }
                            }
                            if (processStateRecord.getCurCapability() != processStateRecord.getSetCapability()) {
                                i3 |= 4;
                                processStateRecord.setSetCapability(processStateRecord.getCurCapability());
                            }
                            boolean isCurBoundByNonBgRestrictedApp = processStateRecord.isCurBoundByNonBgRestrictedApp();
                            if (isCurBoundByNonBgRestrictedApp != processStateRecord.isSetBoundByNonBgRestrictedApp()) {
                                processStateRecord.setSetBoundByNonBgRestrictedApp(isCurBoundByNonBgRestrictedApp);
                                if (!isCurBoundByNonBgRestrictedApp && processStateRecord.isBackgroundRestricted()) {
                                    this.mService.mHandler.post(new Runnable() { // from class: com.android.server.am.OomAdjuster$$ExternalSyntheticLambda1
                                        @Override // java.lang.Runnable
                                        public final void run() {
                                            OomAdjuster.this.lambda$applyOomAdjLSP$1(processRecord);
                                        }
                                    });
                                }
                            }
                            if (i3 != 0) {
                                ActivityManagerService.ProcessChangeItem enqueueProcessChangeItemLocked = this.mProcessList.enqueueProcessChangeItemLocked(processRecord.getPid(), processRecord.info.uid);
                                enqueueProcessChangeItemLocked.changes |= i3;
                                enqueueProcessChangeItemLocked.foregroundActivities = processStateRecord.hasRepForegroundActivities();
                                enqueueProcessChangeItemLocked.capability = processStateRecord.getSetCapability();
                            }
                            if (processStateRecord.isCached() && !processStateRecord.shouldNotKillOnBgRestrictedAndIdle() && (!processStateRecord.isSetCached() || processStateRecord.isSetNoKillOnBgRestrictedAndIdle())) {
                                processStateRecord.setLastCanKillOnBgRestrictedAndIdleTime(j2);
                                if (!this.mService.mHandler.hasMessages(58)) {
                                    this.mService.mHandler.sendEmptyMessageDelayed(58, this.mConstants.mKillBgRestrictedAndCachedIdleSettleTimeMs);
                                }
                            }
                            processStateRecord.setSetCached(processStateRecord.isCached());
                            processStateRecord.setSetNoKillOnBgRestrictedAndIdle(processStateRecord.shouldNotKillOnBgRestrictedAndIdle());
                            return z2;
                        } catch (Throwable th) {
                            th = th;
                            throw th;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                    }
                }
            } else {
                if (currentSchedulingGroup == 0) {
                    i4 = 0;
                } else if (currentSchedulingGroup == 1) {
                    i4 = 7;
                } else if (currentSchedulingGroup != 3 && currentSchedulingGroup != 4) {
                    i4 = -1;
                }
                Handler handler = this.mProcessGroupHandler;
                handler.sendMessage(handler.obtainMessage(0, processRecord.getPid(), i4, processRecord.processName));
                try {
                    int renderThreadTid = processRecord.getRenderThreadTid();
                    if (currentSchedulingGroup == 3) {
                        if (setSchedGroup != 3) {
                            processRecord.getWindowProcessController().onTopProcChanged();
                            if (this.mService.mUseFifoUiScheduling) {
                                processStateRecord.setSavedPriority(Process.getThreadPriority(processRecord.getPid()));
                                ActivityManagerService.scheduleAsFifoPriority(processRecord.getPid(), true);
                                if (renderThreadTid != 0) {
                                    ActivityManagerService.scheduleAsFifoPriority(renderThreadTid, true);
                                }
                            } else {
                                Process.setThreadPriority(processRecord.getPid(), -10);
                                if (renderThreadTid != 0) {
                                    Process.setThreadPriority(renderThreadTid, -10);
                                }
                            }
                        }
                    } else if (setSchedGroup == 3 && currentSchedulingGroup != 3) {
                        processRecord.getWindowProcessController().onTopProcChanged();
                        if (this.mService.mUseFifoUiScheduling) {
                            try {
                                Process.setThreadScheduler(processRecord.getPid(), 0, 0);
                                Process.setThreadPriority(processRecord.getPid(), processStateRecord.getSavedPriority());
                                if (renderThreadTid != 0) {
                                    Process.setThreadScheduler(renderThreadTid, 0, 0);
                                }
                            } catch (IllegalArgumentException e) {
                                Slog.w("OomAdjuster", "Failed to set scheduling policy, thread does not exist:\n" + e);
                            } catch (SecurityException e2) {
                                Slog.w("OomAdjuster", "Failed to set scheduling policy, not allowed:\n" + e2);
                            }
                        } else {
                            Process.setThreadPriority(processRecord.getPid(), 0);
                        }
                        if (renderThreadTid != 0) {
                            Process.setThreadPriority(renderThreadTid, -4);
                        }
                    }
                } catch (IllegalArgumentException | Exception unused2) {
                }
            }
        }
        z2 = true;
        if (processStateRecord.hasRepForegroundActivities() == processStateRecord.hasForegroundActivities()) {
        }
        updateAppFreezeStateLSP(processRecord, i2);
        if (processStateRecord.getReportedProcState() != processStateRecord.getCurProcState()) {
        }
        if (processStateRecord.getSetProcState() != 20) {
        }
        processStateRecord.setLastStateTime(j);
        z3 = true;
        synchronized (this.mService.mAppProfiler.mProfilerLock) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$applyOomAdjLSP$1(ProcessRecord processRecord) {
        synchronized (this.mService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                this.mService.mServices.stopAllForegroundServicesLocked(processRecord.uid, processRecord.info.packageName);
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setAttachingSchedGroupLSP(ProcessRecord processRecord) {
        int i;
        ProcessStateRecord processStateRecord = processRecord.mState;
        if (processStateRecord.hasForegroundActivities()) {
            try {
                processRecord.getWindowProcessController().onTopProcChanged();
                Process.setThreadPriority(processRecord.getPid(), -10);
                i = 3;
            } catch (Exception e) {
                Slog.w("OomAdjuster", "Failed to pre-set top priority to " + processRecord + " " + e);
            }
            processStateRecord.setSetSchedGroup(i);
            processStateRecord.setCurrentSchedulingGroup(i);
        }
        i = 2;
        processStateRecord.setSetSchedGroup(i);
        processStateRecord.setCurrentSchedulingGroup(i);
    }

    @VisibleForTesting
    public void maybeUpdateUsageStats(ProcessRecord processRecord, long j) {
        synchronized (this.mService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                synchronized (this.mProcLock) {
                    ActivityManagerService.boostPriorityForProcLockedSection();
                    maybeUpdateUsageStatsLSP(processRecord, j);
                }
                ActivityManagerService.resetPriorityAfterProcLockedSection();
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
    }

    /* JADX WARN: Code restructure failed: missing block: B:18:0x0047, code lost:
        if (r14 > (r0.getFgInteractionTime() + r8)) goto L8;
     */
    @GuardedBy({"mService", "mProcLock"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void maybeUpdateUsageStatsLSP(ProcessRecord processRecord, long j) {
        long j2;
        long j3;
        ProcessStateRecord processStateRecord = processRecord.mState;
        if (this.mService.mUsageStatsService == null) {
            return;
        }
        boolean cachedCompatChange = processStateRecord.getCachedCompatChange(2);
        if (ActivityManager.isProcStateConsideredInteraction(processStateRecord.getCurProcState())) {
            processStateRecord.setFgInteractionTime(0L);
        } else if (processStateRecord.getCurProcState() <= 4) {
            if (processStateRecord.getFgInteractionTime() == 0) {
                processStateRecord.setFgInteractionTime(j);
            } else if (cachedCompatChange) {
                j2 = this.mConstants.SERVICE_USAGE_INTERACTION_TIME_POST_S;
            } else {
                j2 = this.mConstants.SERVICE_USAGE_INTERACTION_TIME_PRE_S;
            }
            r7 = false;
        } else {
            r7 = processStateRecord.getCurProcState() <= 6;
            processStateRecord.setFgInteractionTime(0L);
        }
        if (cachedCompatChange) {
            j3 = this.mConstants.USAGE_STATS_INTERACTION_INTERVAL_POST_S;
        } else {
            j3 = this.mConstants.USAGE_STATS_INTERACTION_INTERVAL_PRE_S;
        }
        if (r7 && (!processStateRecord.hasReportedInteraction() || j - processStateRecord.getInteractionEventTime() > j3)) {
            processStateRecord.setInteractionEventTime(j);
            String[] packageList = processRecord.getPackageList();
            if (packageList != null) {
                for (String str : packageList) {
                    this.mService.mUsageStatsService.reportEvent(str, processRecord.userId, 6);
                }
            }
        }
        processStateRecord.setReportedInteraction(r7);
        if (r7) {
            return;
        }
        processStateRecord.setInteractionEventTime(0L);
    }

    public final void maybeUpdateLastTopTime(ProcessStateRecord processStateRecord, long j) {
        if (processStateRecord.getSetProcState() > 2 || processStateRecord.getCurProcState() <= 2) {
            return;
        }
        processStateRecord.setLastTopTime(j);
    }

    @GuardedBy({"mService"})
    public void idleUidsLocked() {
        int size = this.mActiveUids.size();
        if (size <= 0) {
            return;
        }
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long j = elapsedRealtime - this.mConstants.BACKGROUND_SETTLE_TIME;
        PowerManagerInternal powerManagerInternal = this.mLocalPowerManager;
        if (powerManagerInternal != null) {
            powerManagerInternal.startUidChanges();
        }
        long j2 = 0;
        for (int i = size - 1; i >= 0; i--) {
            UidRecord valueAt = this.mActiveUids.valueAt(i);
            long lastBackgroundTime = valueAt.getLastBackgroundTime();
            if (lastBackgroundTime > 0 && !valueAt.isIdle()) {
                if (lastBackgroundTime <= j) {
                    EventLogTags.writeAmUidIdle(valueAt.getUid());
                    synchronized (this.mProcLock) {
                        try {
                            ActivityManagerService.boostPriorityForProcLockedSection();
                            valueAt.setIdle(true);
                            valueAt.setSetIdle(true);
                        } catch (Throwable th) {
                            ActivityManagerService.resetPriorityAfterProcLockedSection();
                            throw th;
                        }
                    }
                    ActivityManagerService.resetPriorityAfterProcLockedSection();
                    this.mService.doStopUidLocked(valueAt.getUid(), valueAt);
                } else if (j2 == 0 || j2 > lastBackgroundTime) {
                    j2 = lastBackgroundTime;
                }
            }
        }
        PowerManagerInternal powerManagerInternal2 = this.mLocalPowerManager;
        if (powerManagerInternal2 != null) {
            powerManagerInternal2.finishUidChanges();
        }
        if (this.mService.mConstants.mKillBgRestrictedAndCachedIdle) {
            ArraySet<ProcessRecord> arraySet = this.mProcessList.mAppsInBackgroundRestricted;
            int size2 = arraySet.size();
            for (int i2 = 0; i2 < size2; i2++) {
                long lambda$killAppIfBgRestrictedAndCachedIdleLocked$4 = this.mProcessList.lambda$killAppIfBgRestrictedAndCachedIdleLocked$4(arraySet.valueAt(i2), elapsedRealtime) - this.mConstants.BACKGROUND_SETTLE_TIME;
                if (lambda$killAppIfBgRestrictedAndCachedIdleLocked$4 > 0 && (j2 == 0 || j2 > lambda$killAppIfBgRestrictedAndCachedIdleLocked$4)) {
                    j2 = lambda$killAppIfBgRestrictedAndCachedIdleLocked$4;
                }
            }
        }
        if (j2 > 0) {
            this.mService.mHandler.removeMessages(58);
            this.mService.mHandler.sendEmptyMessageDelayed(58, (j2 + this.mConstants.BACKGROUND_SETTLE_TIME) - elapsedRealtime);
        }
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setAppIdTempAllowlistStateLSP(int i, boolean z) {
        boolean z2 = false;
        for (int size = this.mActiveUids.size() - 1; size >= 0; size--) {
            UidRecord valueAt = this.mActiveUids.valueAt(size);
            if (valueAt.getUid() == i && valueAt.isCurAllowListed() != z) {
                valueAt.setCurAllowListed(z);
                z2 = true;
            }
        }
        if (z2) {
            updateOomAdjLSP(10);
        }
    }

    @GuardedBy({"mService", "mProcLock"})
    public void setUidTempAllowlistStateLSP(int i, boolean z) {
        UidRecord uidRecord = this.mActiveUids.get(i);
        if (uidRecord == null || uidRecord.isCurAllowListed() == z) {
            return;
        }
        uidRecord.setCurAllowListed(z);
        updateOomAdjLSP(10);
    }

    @GuardedBy({"mService"})
    public void dumpProcessListVariablesLocked(ProtoOutputStream protoOutputStream) {
        protoOutputStream.write(1120986464305L, this.mAdjSeq);
        protoOutputStream.write(1120986464306L, this.mProcessList.getLruSeqLOSP());
        protoOutputStream.write(1120986464307L, this.mNumNonCachedProcs);
        protoOutputStream.write(1120986464309L, this.mNumServiceProcs);
        protoOutputStream.write(1120986464310L, this.mNewNumServiceProcs);
    }

    @GuardedBy({"mService"})
    public void dumpSequenceNumbersLocked(PrintWriter printWriter) {
        printWriter.println("  mAdjSeq=" + this.mAdjSeq + " mLruSeq=" + this.mProcessList.getLruSeqLOSP());
    }

    @GuardedBy({"mService"})
    public void dumpProcCountsLocked(PrintWriter printWriter) {
        printWriter.println("  mNumNonCachedProcs=" + this.mNumNonCachedProcs + " (" + this.mProcessList.getLruSizeLOSP() + " total) mNumCachedHiddenProcs=" + this.mNumCachedHiddenProcs + " mNumServiceProcs=" + this.mNumServiceProcs + " mNewNumServiceProcs=" + this.mNewNumServiceProcs);
    }

    @GuardedBy({"mProcLock"})
    public void dumpCachedAppOptimizerSettings(PrintWriter printWriter) {
        this.mCachedAppOptimizer.dump(printWriter);
    }

    @GuardedBy({"mService"})
    public void dumpCacheOomRankerSettings(PrintWriter printWriter) {
        this.mCacheOomRanker.dump(printWriter);
    }

    @GuardedBy({"mService", "mProcLock"})
    public final void updateAppFreezeStateLSP(ProcessRecord processRecord, int i) {
        if (this.mCachedAppOptimizer.useFreezer() && !processRecord.mOptRecord.isFreezeExempt()) {
            ProcessCachedOptimizerRecord processCachedOptimizerRecord = processRecord.mOptRecord;
            if (processCachedOptimizerRecord.isFrozen() && processCachedOptimizerRecord.shouldNotFreeze()) {
                this.mCachedAppOptimizer.unfreezeAppLSP(processRecord, i);
                return;
            }
            ProcessStateRecord processStateRecord = processRecord.mState;
            if (processStateRecord.getCurAdj() >= 900 && !processCachedOptimizerRecord.isFrozen() && !processCachedOptimizerRecord.shouldNotFreeze()) {
                this.mCachedAppOptimizer.freezeAppAsyncLSP(processRecord);
            } else if (processStateRecord.getSetAdj() < 900) {
                this.mCachedAppOptimizer.unfreezeAppLSP(processRecord, i);
            }
        }
    }
}
