package com.android.server.p006am;

import android.app.BroadcastOptions;
import android.app.IApplicationThread;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.content.res.CompatibilityInfo;
import android.net.INetd;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Trace;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.EventLog;
import android.util.IndentingPrintWriter;
import android.util.Slog;
import android.util.SparseIntArray;
import android.util.proto.ProtoOutputStream;
import com.android.internal.os.TimeoutRecord;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.LocalServices;
import com.android.server.p006am.ProcessList;
import com.android.server.p011pm.UserManagerInternal;
import dalvik.annotation.optimization.NeverCompile;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BooleanSupplier;
/* renamed from: com.android.server.am.BroadcastQueueImpl */
/* loaded from: classes.dex */
public class BroadcastQueueImpl extends BroadcastQueue {
    public boolean mBroadcastsScheduled;
    public final BroadcastConstants mConstants;
    public final boolean mDelayBehindServices;
    public final BroadcastDispatcher mDispatcher;
    public final BroadcastHandler mHandler;
    public boolean mLogLatencyMetrics;
    public int mNextToken;
    public final ArrayList<BroadcastRecord> mParallelBroadcasts;
    public BroadcastRecord mPendingBroadcast;
    public int mPendingBroadcastRecvIndex;
    public boolean mPendingBroadcastTimeoutMessage;
    public final int mSchedGroup;
    public final SparseIntArray mSplitRefcounts;

    /* renamed from: com.android.server.am.BroadcastQueueImpl$BroadcastHandler */
    /* loaded from: classes.dex */
    public final class BroadcastHandler extends Handler {
        public BroadcastHandler(Looper looper) {
            super(looper, null);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 200) {
                BroadcastQueueImpl.this.processNextBroadcast(true);
            } else if (i != 201) {
            } else {
                synchronized (BroadcastQueueImpl.this.mService) {
                    try {
                        ActivityManagerService.boostPriorityForLockedSection();
                        BroadcastQueueImpl.this.broadcastTimeoutLocked(true);
                    } catch (Throwable th) {
                        ActivityManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
                ActivityManagerService.resetPriorityAfterLockedSection();
            }
        }
    }

    public BroadcastQueueImpl(ActivityManagerService activityManagerService, Handler handler, String str, BroadcastConstants broadcastConstants, boolean z, int i) {
        this(activityManagerService, handler, str, broadcastConstants, new BroadcastSkipPolicy(activityManagerService), new BroadcastHistory(broadcastConstants), z, i);
    }

    public BroadcastQueueImpl(ActivityManagerService activityManagerService, Handler handler, String str, BroadcastConstants broadcastConstants, BroadcastSkipPolicy broadcastSkipPolicy, BroadcastHistory broadcastHistory, boolean z, int i) {
        super(activityManagerService, handler, str, broadcastSkipPolicy, broadcastHistory);
        this.mParallelBroadcasts = new ArrayList<>();
        this.mSplitRefcounts = new SparseIntArray();
        this.mNextToken = 0;
        this.mBroadcastsScheduled = false;
        this.mPendingBroadcast = null;
        this.mLogLatencyMetrics = true;
        BroadcastHandler broadcastHandler = new BroadcastHandler(handler.getLooper());
        this.mHandler = broadcastHandler;
        this.mConstants = broadcastConstants;
        this.mDelayBehindServices = z;
        this.mSchedGroup = i;
        this.mDispatcher = new BroadcastDispatcher(this, broadcastConstants, broadcastHandler, this.mService);
    }

    @Override // com.android.server.p006am.BroadcastQueue
    public void start(ContentResolver contentResolver) {
        this.mDispatcher.start();
        this.mConstants.startObserving(this.mHandler, contentResolver);
    }

    @Override // com.android.server.p006am.BroadcastQueue
    public boolean isDelayBehindServices() {
        return this.mDelayBehindServices;
    }

    public BroadcastRecord getPendingBroadcastLocked() {
        return this.mPendingBroadcast;
    }

    public BroadcastRecord getActiveBroadcastLocked() {
        return this.mDispatcher.getActiveBroadcastLocked();
    }

    @Override // com.android.server.p006am.BroadcastQueue
    public int getPreferredSchedulingGroupLocked(ProcessRecord processRecord) {
        BroadcastRecord activeBroadcastLocked = getActiveBroadcastLocked();
        if (activeBroadcastLocked != null && activeBroadcastLocked.curApp == processRecord) {
            return this.mSchedGroup;
        }
        BroadcastRecord pendingBroadcastLocked = getPendingBroadcastLocked();
        if (pendingBroadcastLocked == null || pendingBroadcastLocked.curApp != processRecord) {
            return Integer.MIN_VALUE;
        }
        return this.mSchedGroup;
    }

    @Override // com.android.server.p006am.BroadcastQueue
    public void enqueueBroadcastLocked(BroadcastRecord broadcastRecord) {
        BroadcastRecord broadcastRecord2;
        broadcastRecord.applySingletonPolicy(this.mService);
        boolean z = false;
        boolean z2 = (broadcastRecord.intent.getFlags() & 536870912) != 0;
        boolean z3 = broadcastRecord.ordered;
        if (!z3) {
            List<Object> list = broadcastRecord.receivers;
            int size = list != null ? list.size() : 0;
            int i = 0;
            while (true) {
                if (i >= size) {
                    break;
                } else if (broadcastRecord.receivers.get(i) instanceof ResolveInfo) {
                    z3 = true;
                    break;
                } else {
                    i++;
                }
            }
        }
        if (z3) {
            BroadcastRecord replaceOrderedBroadcastLocked = z2 ? replaceOrderedBroadcastLocked(broadcastRecord) : null;
            if (replaceOrderedBroadcastLocked != null) {
                IIntentReceiver iIntentReceiver = replaceOrderedBroadcastLocked.resultTo;
                if (iIntentReceiver == null) {
                    return;
                }
                try {
                    replaceOrderedBroadcastLocked.mIsReceiverAppRunning = true;
                    broadcastRecord2 = replaceOrderedBroadcastLocked;
                    try {
                        performReceiveLocked(replaceOrderedBroadcastLocked.resultToApp, iIntentReceiver, replaceOrderedBroadcastLocked.intent, 0, null, null, false, false, replaceOrderedBroadcastLocked.shareIdentity, replaceOrderedBroadcastLocked.userId, replaceOrderedBroadcastLocked.callingUid, broadcastRecord.callingUid, broadcastRecord.callerPackage, SystemClock.uptimeMillis() - replaceOrderedBroadcastLocked.enqueueTime, 0L);
                    } catch (RemoteException e) {
                        e = e;
                        Slog.w("BroadcastQueue", "Failure [" + this.mQueueName + "] sending broadcast result of " + broadcastRecord2.intent, e);
                    }
                } catch (RemoteException e2) {
                    e = e2;
                    broadcastRecord2 = replaceOrderedBroadcastLocked;
                }
            } else {
                enqueueOrderedBroadcastLocked(broadcastRecord);
                scheduleBroadcastsLocked();
            }
        } else {
            if (z2 && replaceParallelBroadcastLocked(broadcastRecord) != null) {
                z = true;
            }
            if (z) {
                return;
            }
            enqueueParallelBroadcastLocked(broadcastRecord);
            scheduleBroadcastsLocked();
        }
    }

    public void enqueueParallelBroadcastLocked(BroadcastRecord broadcastRecord) {
        broadcastRecord.enqueueClockTime = System.currentTimeMillis();
        broadcastRecord.enqueueTime = SystemClock.uptimeMillis();
        broadcastRecord.enqueueRealTime = SystemClock.elapsedRealtime();
        this.mParallelBroadcasts.add(broadcastRecord);
        enqueueBroadcastHelper(broadcastRecord);
    }

    public void enqueueOrderedBroadcastLocked(BroadcastRecord broadcastRecord) {
        broadcastRecord.enqueueClockTime = System.currentTimeMillis();
        broadcastRecord.enqueueTime = SystemClock.uptimeMillis();
        broadcastRecord.enqueueRealTime = SystemClock.elapsedRealtime();
        this.mDispatcher.enqueueOrderedBroadcastLocked(broadcastRecord);
        enqueueBroadcastHelper(broadcastRecord);
    }

    public final void enqueueBroadcastHelper(BroadcastRecord broadcastRecord) {
        if (Trace.isTagEnabled(64L)) {
            Trace.asyncTraceBegin(64L, createBroadcastTraceTitle(broadcastRecord, 0), System.identityHashCode(broadcastRecord));
        }
    }

    public final BroadcastRecord replaceParallelBroadcastLocked(BroadcastRecord broadcastRecord) {
        return replaceBroadcastLocked(this.mParallelBroadcasts, broadcastRecord, "PARALLEL");
    }

    public final BroadcastRecord replaceOrderedBroadcastLocked(BroadcastRecord broadcastRecord) {
        return this.mDispatcher.replaceBroadcastLocked(broadcastRecord, "ORDERED");
    }

    public final BroadcastRecord replaceBroadcastLocked(ArrayList<BroadcastRecord> arrayList, BroadcastRecord broadcastRecord, String str) {
        Intent intent = broadcastRecord.intent;
        for (int size = arrayList.size() - 1; size > 0; size--) {
            BroadcastRecord broadcastRecord2 = arrayList.get(size);
            if (broadcastRecord2.userId == broadcastRecord.userId && intent.filterEquals(broadcastRecord2.intent)) {
                arrayList.set(size, broadcastRecord);
                return broadcastRecord2;
            }
        }
        return null;
    }

    public final void processCurBroadcastLocked(BroadcastRecord broadcastRecord, ProcessRecord processRecord) throws RemoteException {
        ProcessReceiverRecord processReceiverRecord;
        IApplicationThread thread = processRecord.getThread();
        if (thread == null) {
            throw new RemoteException();
        }
        if (processRecord.isInFullBackup()) {
            skipReceiverLocked(broadcastRecord);
            return;
        }
        broadcastRecord.curApp = processRecord;
        ProcessReceiverRecord processReceiverRecord2 = processRecord.mReceivers;
        processReceiverRecord2.addCurReceiver(broadcastRecord);
        processRecord.mState.forceProcessStateUpTo(11);
        if (this.mService.mInternal.getRestrictionLevel(processRecord.info.packageName, processRecord.userId) < 40) {
            this.mService.updateLruProcessLocked(processRecord, false, null);
        }
        this.mService.enqueueOomAdjTargetLocked(processRecord);
        this.mService.updateOomAdjPendingTargetsLocked(3);
        maybeReportBroadcastDispatchedEventLocked(broadcastRecord, broadcastRecord.curReceiver.applicationInfo.uid);
        broadcastRecord.intent.setComponent(broadcastRecord.curComponent);
        try {
            this.mService.notifyPackageUse(broadcastRecord.intent.getComponent().getPackageName(), 3);
            processReceiverRecord = processReceiverRecord2;
            try {
                thread.scheduleReceiver(prepareReceiverIntent(broadcastRecord.intent, broadcastRecord.curFilteredExtras), broadcastRecord.curReceiver, (CompatibilityInfo) null, broadcastRecord.resultCode, broadcastRecord.resultData, broadcastRecord.resultExtras, broadcastRecord.ordered, false, broadcastRecord.userId, broadcastRecord.shareIdentity ? broadcastRecord.callingUid : -1, processRecord.mState.getReportedProcState(), broadcastRecord.shareIdentity ? broadcastRecord.callerPackage : null);
                if (processRecord.isKilled()) {
                    throw new RemoteException("app gets killed during broadcasting");
                }
            } catch (Throwable th) {
                th = th;
                broadcastRecord.curApp = null;
                processReceiverRecord.removeCurReceiver(broadcastRecord);
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            processReceiverRecord = processReceiverRecord2;
        }
    }

    public void updateUidReadyForBootCompletedBroadcastLocked(int i) {
        this.mDispatcher.updateUidReadyForBootCompletedBroadcastLocked(i);
        scheduleBroadcastsLocked();
    }

    @Override // com.android.server.p006am.BroadcastQueue
    public boolean onApplicationAttachedLocked(ProcessRecord processRecord) {
        updateUidReadyForBootCompletedBroadcastLocked(processRecord.uid);
        BroadcastRecord broadcastRecord = this.mPendingBroadcast;
        if (broadcastRecord == null || broadcastRecord.curApp != processRecord) {
            return false;
        }
        return sendPendingBroadcastsLocked(processRecord);
    }

    @Override // com.android.server.p006am.BroadcastQueue
    public void onApplicationTimeoutLocked(ProcessRecord processRecord) {
        skipCurrentOrPendingReceiverLocked(processRecord);
    }

    @Override // com.android.server.p006am.BroadcastQueue
    public void onApplicationProblemLocked(ProcessRecord processRecord) {
        skipCurrentOrPendingReceiverLocked(processRecord);
    }

    @Override // com.android.server.p006am.BroadcastQueue
    public void onApplicationCleanupLocked(ProcessRecord processRecord) {
        skipCurrentOrPendingReceiverLocked(processRecord);
    }

    public boolean sendPendingBroadcastsLocked(ProcessRecord processRecord) {
        BroadcastRecord broadcastRecord = this.mPendingBroadcast;
        if (broadcastRecord == null || broadcastRecord.curApp.getPid() <= 0 || broadcastRecord.curApp.getPid() != processRecord.getPid()) {
            return false;
        }
        if (broadcastRecord.curApp != processRecord) {
            Slog.e("BroadcastQueue", "App mismatch when sending pending broadcast to " + processRecord.processName + ", intended target is " + broadcastRecord.curApp.processName);
            return false;
        }
        try {
            this.mPendingBroadcast = null;
            broadcastRecord.mIsReceiverAppRunning = false;
            processCurBroadcastLocked(broadcastRecord, processRecord);
            return true;
        } catch (Exception e) {
            Slog.w("BroadcastQueue", "Exception in new application when starting receiver " + broadcastRecord.curComponent.flattenToShortString(), e);
            logBroadcastReceiverDiscardLocked(broadcastRecord);
            finishReceiverLocked(broadcastRecord, broadcastRecord.resultCode, broadcastRecord.resultData, broadcastRecord.resultExtras, broadcastRecord.resultAbort, false);
            scheduleBroadcastsLocked();
            broadcastRecord.state = 0;
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean skipCurrentOrPendingReceiverLocked(ProcessRecord processRecord) {
        BroadcastRecord broadcastRecord;
        BroadcastRecord activeBroadcastLocked = this.mDispatcher.getActiveBroadcastLocked();
        activeBroadcastLocked = (activeBroadcastLocked == null || activeBroadcastLocked.curApp != processRecord) ? null : null;
        if (activeBroadcastLocked == null && (broadcastRecord = this.mPendingBroadcast) != null && broadcastRecord.curApp == processRecord) {
            activeBroadcastLocked = broadcastRecord;
        }
        if (activeBroadcastLocked != null) {
            skipReceiverLocked(activeBroadcastLocked);
            return true;
        }
        return false;
    }

    public final void skipReceiverLocked(BroadcastRecord broadcastRecord) {
        logBroadcastReceiverDiscardLocked(broadcastRecord);
        finishReceiverLocked(broadcastRecord, broadcastRecord.resultCode, broadcastRecord.resultData, broadcastRecord.resultExtras, broadcastRecord.resultAbort, false);
        scheduleBroadcastsLocked();
    }

    public void scheduleBroadcastsLocked() {
        if (this.mBroadcastsScheduled) {
            return;
        }
        BroadcastHandler broadcastHandler = this.mHandler;
        broadcastHandler.sendMessage(broadcastHandler.obtainMessage(200, this));
        this.mBroadcastsScheduled = true;
    }

    public BroadcastRecord getMatchingOrderedReceiver(ProcessRecord processRecord) {
        BroadcastRecord activeBroadcastLocked = this.mDispatcher.getActiveBroadcastLocked();
        if (activeBroadcastLocked == null) {
            Slog.w("BroadcastQueue", "getMatchingOrderedReceiver [" + this.mQueueName + "] no active broadcast");
            return null;
        } else if (activeBroadcastLocked.curApp != processRecord) {
            Slog.w("BroadcastQueue", "getMatchingOrderedReceiver [" + this.mQueueName + "] active broadcast " + activeBroadcastLocked.curApp + " doesn't match " + processRecord);
            return null;
        } else {
            return activeBroadcastLocked;
        }
    }

    public final int nextSplitTokenLocked() {
        int i = this.mNextToken + 1;
        int i2 = i > 0 ? i : 1;
        this.mNextToken = i2;
        return i2;
    }

    public final void postActivityStartTokenRemoval(final ProcessRecord processRecord, final BroadcastRecord broadcastRecord) {
        String intern = (processRecord.toShortString() + broadcastRecord.toString()).intern();
        this.mHandler.removeCallbacksAndMessages(intern);
        this.mHandler.postAtTime(new Runnable() { // from class: com.android.server.am.BroadcastQueueImpl$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                BroadcastQueueImpl.this.lambda$postActivityStartTokenRemoval$0(processRecord, broadcastRecord);
            }
        }, intern, broadcastRecord.receiverTime + this.mConstants.ALLOW_BG_ACTIVITY_START_TIMEOUT);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$postActivityStartTokenRemoval$0(ProcessRecord processRecord, BroadcastRecord broadcastRecord) {
        synchronized (this.mService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                processRecord.removeBackgroundStartPrivileges(broadcastRecord);
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
    }

    @Override // com.android.server.p006am.BroadcastQueue
    public boolean finishReceiverLocked(ProcessRecord processRecord, int i, String str, Bundle bundle, boolean z, boolean z2) {
        BroadcastRecord matchingOrderedReceiver = getMatchingOrderedReceiver(processRecord);
        if (matchingOrderedReceiver != null) {
            return finishReceiverLocked(matchingOrderedReceiver, i, str, bundle, z, z2);
        }
        return false;
    }

    public boolean finishReceiverLocked(BroadcastRecord broadcastRecord, int i, String str, Bundle bundle, boolean z, boolean z2) {
        ActivityInfo activityInfo;
        ProcessRecord processRecord;
        ProcessRecord processRecord2;
        int i2 = broadcastRecord.state;
        ActivityInfo activityInfo2 = broadcastRecord.curReceiver;
        long uptimeMillis = SystemClock.uptimeMillis();
        long j = uptimeMillis - broadcastRecord.receiverTime;
        broadcastRecord.state = 0;
        int i3 = broadcastRecord.nextReceiver - 1;
        int i4 = broadcastRecord.mWasReceiverAppStopped ? 2 : 1;
        if (i3 >= 0 && i3 < broadcastRecord.receivers.size() && broadcastRecord.curApp != null) {
            Object obj = broadcastRecord.receivers.get(i3);
            int i5 = broadcastRecord.curApp.uid;
            int i6 = broadcastRecord.callingUid;
            int i7 = i6 == -1 ? 1000 : i6;
            String action = broadcastRecord.intent.getAction();
            int i8 = obj instanceof BroadcastFilter ? 1 : 2;
            int i9 = broadcastRecord.mIsReceiverAppRunning ? 1 : 3;
            long j2 = broadcastRecord.dispatchTime;
            long j3 = j2 - broadcastRecord.enqueueTime;
            long j4 = broadcastRecord.receiverTime;
            FrameworkStatsLog.write((int) FrameworkStatsLog.BROADCAST_DELIVERY_EVENT_REPORTED, i5, i7, action, i8, i9, j3, j4 - j2, uptimeMillis - j4, i4, broadcastRecord.curApp.info.packageName, broadcastRecord.callerPackage);
        }
        if (i2 == 0) {
            Slog.w("BroadcastQueue", "finishReceiver [" + this.mQueueName + "] called but state is IDLE");
        }
        if (broadcastRecord.mBackgroundStartPrivileges.allowsAny() && (processRecord2 = broadcastRecord.curApp) != null) {
            if (j > this.mConstants.ALLOW_BG_ACTIVITY_START_TIMEOUT) {
                processRecord2.removeBackgroundStartPrivileges(broadcastRecord);
            } else {
                postActivityStartTokenRemoval(processRecord2, broadcastRecord);
            }
        }
        int i10 = broadcastRecord.nextReceiver;
        if (i10 > 0) {
            broadcastRecord.terminalTime[i10 - 1] = uptimeMillis;
        }
        if (!broadcastRecord.timeoutExempt && (processRecord = broadcastRecord.curApp) != null) {
            long j5 = this.mConstants.SLOW_TIME;
            if (j5 > 0 && j > j5 && !UserHandle.isCore(processRecord.uid)) {
                this.mDispatcher.startDeferring(broadcastRecord.curApp.uid);
            }
        }
        broadcastRecord.intent.setComponent(null);
        ProcessRecord processRecord3 = broadcastRecord.curApp;
        if (processRecord3 != null && processRecord3.mReceivers.hasCurReceiver(broadcastRecord)) {
            broadcastRecord.curApp.mReceivers.removeCurReceiver(broadcastRecord);
            this.mService.enqueueOomAdjTargetLocked(broadcastRecord.curApp);
        }
        BroadcastFilter broadcastFilter = broadcastRecord.curFilter;
        if (broadcastFilter != null) {
            broadcastFilter.receiverList.curBroadcast = null;
        }
        broadcastRecord.curFilter = null;
        broadcastRecord.curReceiver = null;
        broadcastRecord.curApp = null;
        broadcastRecord.curFilteredExtras = null;
        broadcastRecord.mWasReceiverAppStopped = false;
        this.mPendingBroadcast = null;
        broadcastRecord.resultCode = i;
        broadcastRecord.resultData = str;
        broadcastRecord.resultExtras = bundle;
        if (z && (broadcastRecord.intent.getFlags() & 134217728) == 0) {
            broadcastRecord.resultAbort = z;
        } else {
            broadcastRecord.resultAbort = false;
        }
        if (z2 && broadcastRecord.curComponent != null && broadcastRecord.queue.isDelayBehindServices() && ((BroadcastQueueImpl) broadcastRecord.queue).getActiveBroadcastLocked() == broadcastRecord) {
            if (broadcastRecord.nextReceiver < broadcastRecord.receivers.size()) {
                Object obj2 = broadcastRecord.receivers.get(broadcastRecord.nextReceiver);
                if (obj2 instanceof ActivityInfo) {
                    activityInfo = (ActivityInfo) obj2;
                    if ((activityInfo2 != null || activityInfo == null || activityInfo2.applicationInfo.uid != activityInfo.applicationInfo.uid || !activityInfo2.processName.equals(activityInfo.processName)) && this.mService.mServices.hasBackgroundServicesLocked(broadcastRecord.userId)) {
                        Slog.i("BroadcastQueue", "Delay finish: " + broadcastRecord.curComponent.flattenToShortString());
                        broadcastRecord.state = 4;
                        return false;
                    }
                }
            }
            activityInfo = null;
            if (activityInfo2 != null) {
            }
            Slog.i("BroadcastQueue", "Delay finish: " + broadcastRecord.curComponent.flattenToShortString());
            broadcastRecord.state = 4;
            return false;
        }
        broadcastRecord.curComponent = null;
        boolean z3 = i2 == 1 || i2 == 3;
        if (z3) {
            processNextBroadcastLocked(false, true);
        }
        return z3;
    }

    @Override // com.android.server.p006am.BroadcastQueue
    public void backgroundServicesFinishedLocked(int i) {
        BroadcastRecord activeBroadcastLocked = this.mDispatcher.getActiveBroadcastLocked();
        if (activeBroadcastLocked != null && activeBroadcastLocked.userId == i && activeBroadcastLocked.state == 4) {
            Slog.i("BroadcastQueue", "Resuming delayed broadcast");
            activeBroadcastLocked.curComponent = null;
            activeBroadcastLocked.state = 0;
            processNextBroadcastLocked(false, false);
        }
    }

    public void performReceiveLocked(ProcessRecord processRecord, IIntentReceiver iIntentReceiver, Intent intent, int i, String str, Bundle bundle, boolean z, boolean z2, boolean z3, int i2, int i3, int i4, String str2, long j, long j2) throws RemoteException {
        int i5;
        if (z3) {
            this.mService.mPackageManagerInt.grantImplicitAccess(i2, intent, UserHandle.getAppId(i3), i4, true);
        }
        if (processRecord != null) {
            IApplicationThread thread = processRecord.getThread();
            if (thread != null) {
                try {
                    i5 = -1;
                    thread.scheduleRegisteredReceiver(iIntentReceiver, intent, i, str, bundle, z, z2, !z, i2, processRecord.mState.getReportedProcState(), z3 ? i4 : -1, z3 ? str2 : null);
                } catch (RemoteException e) {
                    synchronized (this.mService) {
                        try {
                            ActivityManagerService.boostPriorityForLockedSection();
                            Slog.w("BroadcastQueue", "Failed to schedule " + intent + " to " + iIntentReceiver + " via " + processRecord + ": " + e);
                            processRecord.killLocked("Can't deliver broadcast", 13, 26, true);
                            ActivityManagerService.resetPriorityAfterLockedSection();
                            throw e;
                        } catch (Throwable th) {
                            ActivityManagerService.resetPriorityAfterLockedSection();
                            throw th;
                        }
                    }
                }
            } else {
                throw new RemoteException("app.thread must not be null");
            }
        } else {
            i5 = -1;
            iIntentReceiver.performReceive(intent, i, str, bundle, z, z2, i2);
        }
        if (z) {
            return;
        }
        int i6 = i3;
        int i7 = i4;
        if (i6 == i5) {
            i6 = 1000;
        }
        if (i7 == i5) {
            i7 = 1000;
        }
        FrameworkStatsLog.write((int) FrameworkStatsLog.BROADCAST_DELIVERY_EVENT_REPORTED, i6, i7, intent.getAction(), 1, 1, j, j2, 0L, 1, processRecord != null ? processRecord.info.packageName : null, str2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:100:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:101:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:74:0x014a A[Catch: RemoteException -> 0x0146, TRY_LEAVE, TryCatch #6 {RemoteException -> 0x0146, blocks: (B:54:0x0113, B:74:0x014a, B:70:0x0142), top: B:98:0x008c }] */
    /* JADX WARN: Removed duplicated region for block: B:80:0x0170  */
    /* JADX WARN: Removed duplicated region for block: B:84:0x0187  */
    /* JADX WARN: Type inference failed for: r1v0 */
    /* JADX WARN: Type inference failed for: r1v3 */
    /* JADX WARN: Type inference failed for: r1v4 */
    /* JADX WARN: Type inference failed for: r1v5 */
    /* JADX WARN: Type inference failed for: r1v7 */
    /* JADX WARN: Type inference failed for: r2v0 */
    /* JADX WARN: Type inference failed for: r2v1, types: [android.os.Bundle] */
    /* JADX WARN: Type inference failed for: r2v12 */
    /* JADX WARN: Type inference failed for: r2v19 */
    /* JADX WARN: Type inference failed for: r2v20 */
    /* JADX WARN: Type inference failed for: r2v21 */
    /* JADX WARN: Type inference failed for: r2v22 */
    /* JADX WARN: Type inference failed for: r2v23 */
    /* JADX WARN: Type inference failed for: r2v24 */
    /* JADX WARN: Type inference failed for: r2v4 */
    /* JADX WARN: Type inference failed for: r2v6, types: [com.android.server.am.BroadcastRecord] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void deliverToRegisteredReceiverLocked(BroadcastRecord broadcastRecord, BroadcastFilter broadcastFilter, boolean z, int i) {
        ?? r2;
        BroadcastFilter broadcastFilter2;
        BroadcastRecord broadcastRecord2;
        BroadcastQueueImpl broadcastQueueImpl;
        ProcessRecord processRecord;
        BroadcastFilter broadcastFilter3;
        Bundle extras;
        boolean shouldSkip = this.mSkipPolicy.shouldSkip(broadcastRecord, broadcastFilter);
        BroadcastFilter broadcastFilter4 = 1;
        if (shouldSkip || broadcastRecord.filterExtrasForReceiver == null || (extras = broadcastRecord.intent.getExtras()) == null) {
            r2 = 0;
        } else {
            Bundle apply = broadcastRecord.filterExtrasForReceiver.apply(Integer.valueOf(broadcastFilter.receiverList.uid), extras);
            r2 = apply;
            if (apply == null) {
                shouldSkip = true;
                r2 = apply;
            }
        }
        if (shouldSkip) {
            broadcastRecord.delivery[i] = 2;
            return;
        }
        broadcastRecord.delivery[i] = 1;
        if (z) {
            broadcastRecord.curFilter = broadcastFilter;
            ReceiverList receiverList = broadcastFilter.receiverList;
            receiverList.curBroadcast = broadcastRecord;
            broadcastRecord.state = 2;
            ProcessRecord processRecord2 = receiverList.app;
            if (processRecord2 != null) {
                broadcastRecord.curApp = processRecord2;
                processRecord2.mReceivers.addCurReceiver(broadcastRecord);
                this.mService.enqueueOomAdjTargetLocked(broadcastRecord.curApp);
                this.mService.updateOomAdjPendingTargetsLocked(3);
            }
        } else {
            ProcessRecord processRecord3 = broadcastFilter.receiverList.app;
            if (processRecord3 != null) {
                this.mService.mOomAdjuster.mCachedAppOptimizer.unfreezeTemporarily(processRecord3, 3);
            }
        }
        try {
            ProcessRecord processRecord4 = broadcastFilter.receiverList.app;
            broadcastQueueImpl = (processRecord4 == null || !processRecord4.isInFullBackup()) ? null : 1;
            ProcessRecord processRecord5 = broadcastFilter.receiverList.app;
            if (processRecord5 == null || !processRecord5.isKilled()) {
                broadcastFilter4 = 0;
            }
            try {
                if (broadcastQueueImpl == null && broadcastFilter4 == 0) {
                    long uptimeMillis = SystemClock.uptimeMillis();
                    broadcastRecord.receiverTime = uptimeMillis;
                    broadcastRecord.scheduledTime[i] = uptimeMillis;
                    maybeAddBackgroundStartPrivileges(broadcastFilter.receiverList.app, broadcastRecord);
                    maybeScheduleTempAllowlistLocked(broadcastFilter.owningUid, broadcastRecord, broadcastRecord.options);
                    maybeReportBroadcastDispatchedEventLocked(broadcastRecord, broadcastFilter.owningUid);
                    ReceiverList receiverList2 = broadcastFilter.receiverList;
                    ProcessRecord processRecord6 = receiverList2.app;
                    IIntentReceiver iIntentReceiver = receiverList2.receiver;
                    Intent prepareReceiverIntent = prepareReceiverIntent(broadcastRecord.intent, r2);
                    int i2 = broadcastRecord.resultCode;
                    String str = broadcastRecord.resultData;
                    Bundle bundle = broadcastRecord.resultExtras;
                    boolean z2 = broadcastRecord.ordered;
                    boolean z3 = broadcastRecord.initialSticky;
                    boolean z4 = broadcastRecord.shareIdentity;
                    int i3 = broadcastRecord.userId;
                    int i4 = broadcastFilter.receiverList.uid;
                    int i5 = broadcastRecord.callingUid;
                    try {
                        String str2 = broadcastRecord.callerPackage;
                        try {
                            long j = broadcastRecord.dispatchTime;
                            try {
                                performReceiveLocked(processRecord6, iIntentReceiver, prepareReceiverIntent, i2, str, bundle, z2, z3, z4, i3, i4, i5, str2, j - broadcastRecord.enqueueTime, broadcastRecord.receiverTime - j);
                                broadcastFilter4 = broadcastFilter;
                                try {
                                    if (broadcastFilter4.receiverList.app != null) {
                                        BroadcastRecord broadcastRecord3 = broadcastRecord;
                                        try {
                                            if (!broadcastRecord3.mBackgroundStartPrivileges.allowsAny() || broadcastRecord3.ordered) {
                                                broadcastQueueImpl = this;
                                                r2 = broadcastRecord3;
                                            } else {
                                                broadcastQueueImpl = this;
                                                broadcastQueueImpl.postActivityStartTokenRemoval(broadcastFilter4.receiverList.app, broadcastRecord3);
                                                r2 = broadcastRecord3;
                                            }
                                        } catch (RemoteException e) {
                                            e = e;
                                            broadcastQueueImpl = this;
                                            broadcastFilter2 = broadcastFilter4;
                                            broadcastRecord2 = broadcastRecord3;
                                            Slog.w("BroadcastQueue", "Failure sending broadcast " + broadcastRecord2.intent, e);
                                            processRecord = broadcastFilter2.receiverList.app;
                                            if (processRecord != null) {
                                            }
                                            if (z) {
                                            }
                                        }
                                    } else {
                                        broadcastQueueImpl = this;
                                        r2 = broadcastRecord;
                                    }
                                    if (z) {
                                        return;
                                    }
                                    r2.state = 3;
                                    return;
                                } catch (RemoteException e2) {
                                    e = e2;
                                    broadcastQueueImpl = this;
                                    broadcastRecord2 = broadcastRecord;
                                    broadcastFilter2 = broadcastFilter4;
                                }
                            } catch (RemoteException e3) {
                                e = e3;
                                broadcastQueueImpl = this;
                                broadcastRecord2 = broadcastRecord;
                                broadcastFilter2 = broadcastFilter;
                            }
                        } catch (RemoteException e4) {
                            e = e4;
                            broadcastQueueImpl = this;
                            broadcastFilter3 = broadcastFilter;
                            broadcastRecord2 = broadcastRecord;
                            broadcastFilter2 = broadcastFilter3;
                            Slog.w("BroadcastQueue", "Failure sending broadcast " + broadcastRecord2.intent, e);
                            processRecord = broadcastFilter2.receiverList.app;
                            if (processRecord != null) {
                                processRecord.removeBackgroundStartPrivileges(broadcastRecord2);
                                if (z) {
                                    broadcastFilter2.receiverList.app.mReceivers.removeCurReceiver(broadcastRecord2);
                                    broadcastQueueImpl.mService.enqueueOomAdjTargetLocked(broadcastRecord2.curApp);
                                }
                            }
                            if (z) {
                                return;
                            }
                            broadcastRecord2.curFilter = null;
                            broadcastFilter2.receiverList.curBroadcast = null;
                            return;
                        }
                    } catch (RemoteException e5) {
                        e = e5;
                        broadcastQueueImpl = this;
                        broadcastFilter3 = broadcastFilter;
                    }
                }
                broadcastFilter4 = broadcastFilter;
                BroadcastRecord broadcastRecord4 = broadcastRecord;
                broadcastQueueImpl = this;
                r2 = broadcastRecord4;
                if (z) {
                    skipReceiverLocked(broadcastRecord);
                    r2 = broadcastRecord4;
                }
                if (z) {
                }
            } catch (RemoteException e6) {
                e = e6;
                broadcastFilter2 = broadcastFilter4;
                broadcastRecord2 = r2;
            }
        } catch (RemoteException e7) {
            e = e7;
            broadcastFilter2 = broadcastFilter;
            broadcastRecord2 = broadcastRecord;
            broadcastQueueImpl = this;
        }
    }

    public void maybeScheduleTempAllowlistLocked(int i, BroadcastRecord broadcastRecord, BroadcastOptions broadcastOptions) {
        if (broadcastOptions == null || broadcastOptions.getTemporaryAppAllowlistDuration() <= 0) {
            return;
        }
        long temporaryAppAllowlistDuration = broadcastOptions.getTemporaryAppAllowlistDuration();
        int temporaryAppAllowlistType = broadcastOptions.getTemporaryAppAllowlistType();
        int temporaryAppAllowlistReasonCode = broadcastOptions.getTemporaryAppAllowlistReasonCode();
        String temporaryAppAllowlistReason = broadcastOptions.getTemporaryAppAllowlistReason();
        long j = temporaryAppAllowlistDuration > 2147483647L ? 2147483647L : temporaryAppAllowlistDuration;
        StringBuilder sb = new StringBuilder();
        sb.append("broadcast:");
        UserHandle.formatUid(sb, broadcastRecord.callingUid);
        sb.append(XmlUtils.STRING_ARRAY_SEPARATOR);
        if (broadcastRecord.intent.getAction() != null) {
            sb.append(broadcastRecord.intent.getAction());
        } else if (broadcastRecord.intent.getComponent() != null) {
            broadcastRecord.intent.getComponent().appendShortString(sb);
        } else if (broadcastRecord.intent.getData() != null) {
            sb.append(broadcastRecord.intent.getData());
        }
        sb.append(",reason:");
        sb.append(temporaryAppAllowlistReason);
        this.mService.tempAllowlistUidLocked(i, j, temporaryAppAllowlistReasonCode, sb.toString(), temporaryAppAllowlistType, broadcastRecord.callingUid);
    }

    public final void processNextBroadcast(boolean z) {
        synchronized (this.mService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                processNextBroadcastLocked(z, false);
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
    }

    public static Intent prepareReceiverIntent(Intent intent, Bundle bundle) {
        Intent intent2 = new Intent(intent);
        if (bundle != null) {
            intent2.replaceExtras(bundle);
        }
        return intent2;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:113:0x025c  */
    /* JADX WARN: Removed duplicated region for block: B:143:0x0313  */
    /* JADX WARN: Removed duplicated region for block: B:161:0x0399  */
    /* JADX WARN: Removed duplicated region for block: B:164:0x03af  */
    /* JADX WARN: Removed duplicated region for block: B:175:0x03d4  */
    /* JADX WARN: Removed duplicated region for block: B:231:0x05eb A[LOOP:2: B:43:0x00f9->B:231:0x05eb, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:254:0x0346 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:255:0x01c8 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:79:0x01c9  */
    /* JADX WARN: Type inference failed for: r11v0 */
    /* JADX WARN: Type inference failed for: r11v1, types: [int, boolean] */
    /* JADX WARN: Type inference failed for: r11v10 */
    /* JADX WARN: Type inference failed for: r14v0 */
    /* JADX WARN: Type inference failed for: r14v1, types: [int, boolean] */
    /* JADX WARN: Type inference failed for: r14v12 */
    /* JADX WARN: Type inference failed for: r3v17 */
    /* JADX WARN: Type inference failed for: r3v18, types: [int, boolean] */
    /* JADX WARN: Type inference failed for: r3v28 */
    /* JADX WARN: Type inference failed for: r9v3 */
    /* JADX WARN: Type inference failed for: r9v4, types: [int, boolean] */
    /* JADX WARN: Type inference failed for: r9v5 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void processNextBroadcastLocked(boolean z, boolean z2) {
        ?? r11;
        boolean z3;
        BroadcastRecord broadcastRecord;
        IIntentReceiver iIntentReceiver;
        int i;
        BroadcastQueueImpl broadcastQueueImpl;
        long j;
        BroadcastRecord broadcastRecord2;
        boolean z4;
        ProcessRecord processRecord;
        IIntentReceiver iIntentReceiver2;
        Intent intent;
        int i2;
        String str;
        Bundle bundle;
        boolean z5;
        int i3;
        ?? r3;
        ?? r9;
        Object obj;
        Bundle bundle2;
        ComponentName componentName;
        Bundle extras;
        int i4;
        boolean z6;
        BroadcastQueueImpl broadcastQueueImpl2 = this;
        broadcastQueueImpl2.mService.updateCpuStats();
        ?? r14 = 0;
        if (z) {
            broadcastQueueImpl2.mBroadcastsScheduled = false;
        }
        while (true) {
            r11 = 1;
            if (broadcastQueueImpl2.mParallelBroadcasts.size() <= 0) {
                break;
            }
            BroadcastRecord remove = broadcastQueueImpl2.mParallelBroadcasts.remove(0);
            remove.dispatchTime = SystemClock.uptimeMillis();
            remove.dispatchRealTime = SystemClock.elapsedRealtime();
            remove.dispatchClockTime = System.currentTimeMillis();
            remove.mIsReceiverAppRunning = true;
            if (Trace.isTagEnabled(64L)) {
                Trace.asyncTraceEnd(64L, broadcastQueueImpl2.createBroadcastTraceTitle(remove, 0), System.identityHashCode(remove));
                Trace.asyncTraceBegin(64L, broadcastQueueImpl2.createBroadcastTraceTitle(remove, 1), System.identityHashCode(remove));
            }
            int size = remove.receivers.size();
            for (int i5 = 0; i5 < size; i5++) {
                broadcastQueueImpl2.deliverToRegisteredReceiverLocked(remove, (BroadcastFilter) remove.receivers.get(i5), false, i5);
            }
            broadcastQueueImpl2.addBroadcastToHistoryLocked(remove);
        }
        BroadcastRecord broadcastRecord3 = broadcastQueueImpl2.mPendingBroadcast;
        IIntentReceiver iIntentReceiver3 = null;
        if (broadcastRecord3 != null) {
            if (broadcastRecord3.curApp.getPid() > 0) {
                synchronized (broadcastQueueImpl2.mService.mPidsSelfLocked) {
                    ProcessRecord processRecord2 = broadcastQueueImpl2.mService.mPidsSelfLocked.get(broadcastQueueImpl2.mPendingBroadcast.curApp.getPid());
                    z6 = processRecord2 == null || processRecord2.mErrorState.isCrashing();
                }
            } else {
                ProcessList.MyProcessMap processNamesLOSP = broadcastQueueImpl2.mService.mProcessList.getProcessNamesLOSP();
                ProcessRecord processRecord3 = broadcastQueueImpl2.mPendingBroadcast.curApp;
                ProcessRecord processRecord4 = (ProcessRecord) processNamesLOSP.get(processRecord3.processName, processRecord3.uid);
                z6 = processRecord4 == null || !processRecord4.isPendingStart();
            }
            if (!z6) {
                return;
            }
            Slog.w("BroadcastQueue", "pending app  [" + broadcastQueueImpl2.mQueueName + "]" + broadcastQueueImpl2.mPendingBroadcast.curApp + " died before responding to broadcast");
            BroadcastRecord broadcastRecord4 = broadcastQueueImpl2.mPendingBroadcast;
            broadcastRecord4.state = 0;
            broadcastRecord4.nextReceiver = broadcastQueueImpl2.mPendingBroadcastRecvIndex;
            broadcastQueueImpl2.mPendingBroadcast = null;
        }
        boolean z7 = false;
        while (true) {
            long uptimeMillis = SystemClock.uptimeMillis();
            BroadcastRecord nextBroadcastLocked = broadcastQueueImpl2.mDispatcher.getNextBroadcastLocked(uptimeMillis);
            if (nextBroadcastLocked == null) {
                broadcastQueueImpl2.mDispatcher.scheduleDeferralCheckLocked(r14);
                synchronized (broadcastQueueImpl2.mService.mAppProfiler.mProfilerLock) {
                    broadcastQueueImpl2.mService.mAppProfiler.scheduleAppGcsLPf();
                }
                if (z7 && !z2) {
                    broadcastQueueImpl2.mService.updateOomAdjPendingTargetsLocked(3);
                }
                if (broadcastQueueImpl2.mService.mUserController.mBootCompleted && broadcastQueueImpl2.mLogLatencyMetrics) {
                    broadcastQueueImpl2.mLogLatencyMetrics = r14;
                    return;
                }
                return;
            }
            List<Object> list = nextBroadcastLocked.receivers;
            int size2 = list != null ? list.size() : r14;
            if (broadcastQueueImpl2.mService.mProcessesReady && !nextBroadcastLocked.timeoutExempt) {
                long j2 = nextBroadcastLocked.dispatchTime;
                if (j2 > 0 && size2 > 0 && uptimeMillis > j2 + (broadcastQueueImpl2.mConstants.TIMEOUT * 2 * size2)) {
                    Slog.w("BroadcastQueue", "Hung broadcast [" + broadcastQueueImpl2.mQueueName + "] discarded after timeout failure: now=" + uptimeMillis + " dispatchTime=" + nextBroadcastLocked.dispatchTime + " startTime=" + nextBroadcastLocked.receiverTime + " intent=" + nextBroadcastLocked.intent + " numReceivers=" + size2 + " nextReceiver=" + nextBroadcastLocked.nextReceiver + " state=" + nextBroadcastLocked.state);
                    broadcastQueueImpl2.broadcastTimeoutLocked(r14);
                    nextBroadcastLocked.state = r14;
                    z3 = r11;
                    if (nextBroadcastLocked.state == 0) {
                        return;
                    }
                    List<Object> list2 = nextBroadcastLocked.receivers;
                    if (list2 == null || (i4 = nextBroadcastLocked.nextReceiver) >= size2 || nextBroadcastLocked.resultAbort || z3) {
                        if (nextBroadcastLocked.resultTo != null) {
                            int i6 = nextBroadcastLocked.splitToken;
                            if (i6 != 0) {
                                int i7 = broadcastQueueImpl2.mSplitRefcounts.get(i6) - r11;
                                if (i7 == 0) {
                                    broadcastQueueImpl2.mSplitRefcounts.delete(nextBroadcastLocked.splitToken);
                                } else {
                                    broadcastQueueImpl2.mSplitRefcounts.put(nextBroadcastLocked.splitToken, i7);
                                    z4 = r14;
                                    if (z4) {
                                        ProcessRecord processRecord5 = nextBroadcastLocked.callerApp;
                                        if (processRecord5 != null) {
                                            broadcastQueueImpl2.mService.mOomAdjuster.mCachedAppOptimizer.unfreezeTemporarily(processRecord5, 2);
                                        }
                                        try {
                                            if (nextBroadcastLocked.dispatchTime == 0) {
                                                nextBroadcastLocked.dispatchTime = uptimeMillis;
                                            }
                                            nextBroadcastLocked.mIsReceiverAppRunning = r11;
                                            processRecord = nextBroadcastLocked.resultToApp;
                                            iIntentReceiver2 = nextBroadcastLocked.resultTo;
                                            intent = new Intent(nextBroadcastLocked.intent);
                                            i2 = nextBroadcastLocked.resultCode;
                                            str = nextBroadcastLocked.resultData;
                                            bundle = nextBroadcastLocked.resultExtras;
                                            try {
                                                z5 = nextBroadcastLocked.shareIdentity;
                                                i3 = nextBroadcastLocked.userId;
                                            } catch (RemoteException e) {
                                                e = e;
                                                broadcastRecord = nextBroadcastLocked;
                                                i = 2;
                                                iIntentReceiver = null;
                                            }
                                        } catch (RemoteException e2) {
                                            e = e2;
                                            broadcastRecord = nextBroadcastLocked;
                                            iIntentReceiver = iIntentReceiver3;
                                            i = 2;
                                        }
                                        try {
                                            int i8 = nextBroadcastLocked.callingUid;
                                            String str2 = nextBroadcastLocked.callerPackage;
                                            long j3 = nextBroadcastLocked.dispatchTime;
                                            j = 64;
                                            i = 2;
                                            try {
                                                performReceiveLocked(processRecord, iIntentReceiver2, intent, i2, str, bundle, false, false, z5, i3, i8, i8, str2, j3 - nextBroadcastLocked.enqueueTime, uptimeMillis - j3);
                                                logBootCompletedBroadcastCompletionLatencyIfPossible(nextBroadcastLocked);
                                                broadcastRecord = nextBroadcastLocked;
                                                iIntentReceiver = null;
                                                try {
                                                    broadcastRecord.resultTo = null;
                                                    broadcastQueueImpl = this;
                                                } catch (RemoteException e3) {
                                                    e = e3;
                                                    broadcastRecord.resultTo = iIntentReceiver;
                                                    StringBuilder sb = new StringBuilder();
                                                    sb.append("Failure [");
                                                    broadcastQueueImpl = this;
                                                    sb.append(broadcastQueueImpl.mQueueName);
                                                    sb.append("] sending broadcast result of ");
                                                    sb.append(broadcastRecord.intent);
                                                    Slog.w("BroadcastQueue", sb.toString(), e);
                                                    cancelBroadcastTimeoutLocked();
                                                    broadcastQueueImpl.addBroadcastToHistoryLocked(broadcastRecord);
                                                    if (broadcastRecord.intent.getComponent() == null) {
                                                    }
                                                    broadcastQueueImpl.mDispatcher.retireBroadcastLocked(broadcastRecord);
                                                    broadcastRecord2 = iIntentReceiver;
                                                    z7 = true;
                                                    if (broadcastRecord2 != null) {
                                                    }
                                                }
                                            } catch (RemoteException e4) {
                                                e = e4;
                                                broadcastRecord = nextBroadcastLocked;
                                                iIntentReceiver = null;
                                            }
                                        } catch (RemoteException e5) {
                                            e = e5;
                                            broadcastRecord = nextBroadcastLocked;
                                            iIntentReceiver = null;
                                            i = 2;
                                            j = 64;
                                            broadcastRecord.resultTo = iIntentReceiver;
                                            StringBuilder sb2 = new StringBuilder();
                                            sb2.append("Failure [");
                                            broadcastQueueImpl = this;
                                            sb2.append(broadcastQueueImpl.mQueueName);
                                            sb2.append("] sending broadcast result of ");
                                            sb2.append(broadcastRecord.intent);
                                            Slog.w("BroadcastQueue", sb2.toString(), e);
                                            cancelBroadcastTimeoutLocked();
                                            broadcastQueueImpl.addBroadcastToHistoryLocked(broadcastRecord);
                                            if (broadcastRecord.intent.getComponent() == null) {
                                            }
                                            broadcastQueueImpl.mDispatcher.retireBroadcastLocked(broadcastRecord);
                                            broadcastRecord2 = iIntentReceiver;
                                            z7 = true;
                                            if (broadcastRecord2 != null) {
                                            }
                                        }
                                        cancelBroadcastTimeoutLocked();
                                        broadcastQueueImpl.addBroadcastToHistoryLocked(broadcastRecord);
                                        if (broadcastRecord.intent.getComponent() == null && broadcastRecord.intent.getPackage() == null && (broadcastRecord.intent.getFlags() & 1073741824) == 0) {
                                            broadcastQueueImpl.mService.addBroadcastStatLocked(broadcastRecord.intent.getAction(), broadcastRecord.callerPackage, broadcastRecord.manifestCount, broadcastRecord.manifestSkipCount, broadcastRecord.finishTime - broadcastRecord.dispatchTime);
                                        }
                                        broadcastQueueImpl.mDispatcher.retireBroadcastLocked(broadcastRecord);
                                        broadcastRecord2 = iIntentReceiver;
                                        z7 = true;
                                    }
                                }
                            }
                            z4 = r11;
                            if (z4) {
                            }
                        }
                        broadcastRecord = nextBroadcastLocked;
                        iIntentReceiver = iIntentReceiver3;
                        i = 2;
                        broadcastQueueImpl = broadcastQueueImpl2;
                        j = 64;
                        cancelBroadcastTimeoutLocked();
                        broadcastQueueImpl.addBroadcastToHistoryLocked(broadcastRecord);
                        if (broadcastRecord.intent.getComponent() == null) {
                            broadcastQueueImpl.mService.addBroadcastStatLocked(broadcastRecord.intent.getAction(), broadcastRecord.callerPackage, broadcastRecord.manifestCount, broadcastRecord.manifestSkipCount, broadcastRecord.finishTime - broadcastRecord.dispatchTime);
                        }
                        broadcastQueueImpl.mDispatcher.retireBroadcastLocked(broadcastRecord);
                        broadcastRecord2 = iIntentReceiver;
                        z7 = true;
                    } else {
                        if (!nextBroadcastLocked.deferred) {
                            int receiverUid = BroadcastRecord.getReceiverUid(list2.get(i4));
                            if (broadcastQueueImpl2.mDispatcher.isDeferringLocked(receiverUid)) {
                                int i9 = nextBroadcastLocked.nextReceiver;
                                if (i9 + 1 == size2) {
                                    broadcastQueueImpl2.mDispatcher.retireBroadcastLocked(nextBroadcastLocked);
                                } else {
                                    BroadcastRecord splitRecipientsLocked = nextBroadcastLocked.splitRecipientsLocked(receiverUid, i9);
                                    if (nextBroadcastLocked.resultTo != null) {
                                        int i10 = nextBroadcastLocked.splitToken;
                                        if (i10 == 0) {
                                            int nextSplitTokenLocked = nextSplitTokenLocked();
                                            splitRecipientsLocked.splitToken = nextSplitTokenLocked;
                                            nextBroadcastLocked.splitToken = nextSplitTokenLocked;
                                            broadcastQueueImpl2.mSplitRefcounts.put(nextSplitTokenLocked, 2);
                                        } else {
                                            broadcastQueueImpl2.mSplitRefcounts.put(i10, broadcastQueueImpl2.mSplitRefcounts.get(i10) + r11);
                                        }
                                    }
                                    nextBroadcastLocked = splitRecipientsLocked;
                                }
                                broadcastQueueImpl2.mDispatcher.addDeferredBroadcast(receiverUid, nextBroadcastLocked);
                                iIntentReceiver = iIntentReceiver3;
                                z7 = r11;
                                i = 2;
                                broadcastQueueImpl = broadcastQueueImpl2;
                                j = 64;
                                broadcastRecord2 = iIntentReceiver3;
                            }
                        }
                        iIntentReceiver = iIntentReceiver3;
                        i = 2;
                        broadcastQueueImpl = broadcastQueueImpl2;
                        j = 64;
                        broadcastRecord2 = nextBroadcastLocked;
                    }
                    if (broadcastRecord2 != null) {
                        int i11 = broadcastRecord2.nextReceiver;
                        broadcastRecord2.nextReceiver = i11 + 1;
                        long uptimeMillis2 = SystemClock.uptimeMillis();
                        broadcastRecord2.receiverTime = uptimeMillis2;
                        broadcastRecord2.scheduledTime[i11] = uptimeMillis2;
                        if (i11 == 0) {
                            broadcastRecord2.dispatchTime = uptimeMillis2;
                            broadcastRecord2.dispatchRealTime = SystemClock.elapsedRealtime();
                            long currentTimeMillis = System.currentTimeMillis();
                            broadcastRecord2.dispatchClockTime = currentTimeMillis;
                            if (broadcastQueueImpl.mLogLatencyMetrics) {
                                FrameworkStatsLog.write(142, currentTimeMillis - broadcastRecord2.enqueueClockTime);
                            }
                            if (Trace.isTagEnabled(j)) {
                                r9 = 0;
                                long j4 = j;
                                Trace.asyncTraceEnd(j4, broadcastQueueImpl.createBroadcastTraceTitle(broadcastRecord2, 0), System.identityHashCode(broadcastRecord2));
                                r3 = 1;
                                Trace.asyncTraceBegin(j4, broadcastQueueImpl.createBroadcastTraceTitle(broadcastRecord2, 1), System.identityHashCode(broadcastRecord2));
                                if (!broadcastQueueImpl.mPendingBroadcastTimeoutMessage) {
                                    broadcastQueueImpl.setBroadcastTimeoutLocked(broadcastRecord2.receiverTime + broadcastQueueImpl.mConstants.TIMEOUT);
                                }
                                BroadcastOptions broadcastOptions = broadcastRecord2.options;
                                obj = broadcastRecord2.receivers.get(i11);
                                if (!(obj instanceof BroadcastFilter)) {
                                    BroadcastFilter broadcastFilter = (BroadcastFilter) obj;
                                    broadcastRecord2.mIsReceiverAppRunning = r3;
                                    broadcastQueueImpl.deliverToRegisteredReceiverLocked(broadcastRecord2, broadcastFilter, broadcastRecord2.ordered, i11);
                                    if ((broadcastRecord2.curReceiver == null && broadcastRecord2.curFilter == null) || !broadcastRecord2.ordered) {
                                        broadcastRecord2.state = r9;
                                        scheduleBroadcastsLocked();
                                        return;
                                    }
                                    ReceiverList receiverList = broadcastFilter.receiverList;
                                    if (receiverList != null) {
                                        broadcastQueueImpl.maybeAddBackgroundStartPrivileges(receiverList.app, broadcastRecord2);
                                        return;
                                    }
                                    return;
                                }
                                ResolveInfo resolveInfo = (ResolveInfo) obj;
                                ActivityInfo activityInfo = resolveInfo.activityInfo;
                                ComponentName componentName2 = new ComponentName(activityInfo.applicationInfo.packageName, activityInfo.name);
                                ActivityInfo activityInfo2 = resolveInfo.activityInfo;
                                int i12 = activityInfo2.applicationInfo.uid;
                                String str3 = activityInfo2.processName;
                                ProcessRecord processRecordLocked = broadcastQueueImpl.mService.getProcessRecordLocked(str3, i12);
                                boolean shouldSkip = broadcastQueueImpl.mSkipPolicy.shouldSkip(broadcastRecord2, resolveInfo);
                                if (shouldSkip || broadcastRecord2.filterExtrasForReceiver == null || (extras = broadcastRecord2.intent.getExtras()) == null) {
                                    bundle2 = iIntentReceiver;
                                } else {
                                    bundle2 = broadcastRecord2.filterExtrasForReceiver.apply(Integer.valueOf(i12), extras);
                                    if (bundle2 == null) {
                                        shouldSkip = r3;
                                    }
                                }
                                if (shouldSkip) {
                                    broadcastRecord2.delivery[i11] = i;
                                    broadcastRecord2.curFilter = iIntentReceiver;
                                    broadcastRecord2.state = r9;
                                    broadcastRecord2.manifestSkipCount += r3;
                                    scheduleBroadcastsLocked();
                                    return;
                                }
                                broadcastRecord2.manifestCount += r3;
                                broadcastRecord2.delivery[i11] = r3;
                                broadcastRecord2.state = r3;
                                broadcastRecord2.curComponent = componentName2;
                                broadcastRecord2.curReceiver = resolveInfo.activityInfo;
                                broadcastRecord2.curFilteredExtras = bundle2;
                                int i13 = (broadcastOptions == null || broadcastOptions.getTemporaryAppAllowlistDuration() <= 0) ? r9 : r3;
                                broadcastQueueImpl.maybeScheduleTempAllowlistLocked(i12, broadcastRecord2, broadcastOptions);
                                if (broadcastRecord2.intent.getComponent() != null && (componentName = broadcastRecord2.curComponent) != null && !TextUtils.equals(componentName.getPackageName(), broadcastRecord2.callerPackage)) {
                                    broadcastQueueImpl.mService.mUsageStatsService.reportEvent(broadcastRecord2.curComponent.getPackageName(), broadcastRecord2.userId, 31);
                                }
                                try {
                                    broadcastQueueImpl.mService.mPackageManagerInt.setPackageStoppedState(broadcastRecord2.curComponent.getPackageName(), r9, broadcastRecord2.userId);
                                } catch (IllegalArgumentException e6) {
                                    Slog.w("BroadcastQueue", "Failed trying to unstop package " + broadcastRecord2.curComponent.getPackageName() + ": " + e6);
                                }
                                if (processRecordLocked != null && processRecordLocked.getThread() != null && !processRecordLocked.isKilled()) {
                                    try {
                                        ActivityInfo activityInfo3 = resolveInfo.activityInfo;
                                        processRecordLocked.addPackage(activityInfo3.packageName, activityInfo3.applicationInfo.longVersionCode, broadcastQueueImpl.mService.mProcessStats);
                                        broadcastQueueImpl.maybeAddBackgroundStartPrivileges(processRecordLocked, broadcastRecord2);
                                        broadcastRecord2.mIsReceiverAppRunning = r3;
                                        broadcastQueueImpl.processCurBroadcastLocked(broadcastRecord2, processRecordLocked);
                                        return;
                                    } catch (RemoteException e7) {
                                        Slog.w("BroadcastQueue", "Failed to schedule " + broadcastRecord2.intent + " to " + resolveInfo + " via " + processRecordLocked + ": " + e7);
                                        processRecordLocked.killLocked("Can't deliver broadcast", 13, 26, r3);
                                    } catch (RuntimeException e8) {
                                        Slog.wtf("BroadcastQueue", "Failed sending broadcast to " + broadcastRecord2.curComponent + " with " + broadcastRecord2.intent, e8);
                                        broadcastQueueImpl.logBroadcastReceiverDiscardLocked(broadcastRecord2);
                                        finishReceiverLocked((BroadcastRecord) broadcastRecord2, broadcastRecord2.resultCode, broadcastRecord2.resultData, broadcastRecord2.resultExtras, broadcastRecord2.resultAbort, false);
                                        scheduleBroadcastsLocked();
                                        broadcastRecord2.state = r9;
                                        return;
                                    }
                                }
                                ApplicationInfo applicationInfo = resolveInfo.activityInfo.applicationInfo;
                                broadcastRecord2.mWasReceiverAppStopped = (applicationInfo.flags & 2097152) != 0 ? r3 : r9;
                                ProcessRecord startProcessLocked = broadcastQueueImpl.mService.startProcessLocked(str3, applicationInfo, true, broadcastRecord2.intent.getFlags() | 4, new HostingRecord(INetd.IF_FLAG_BROADCAST, broadcastRecord2.curComponent, broadcastRecord2.intent.getAction(), broadcastRecord2.getHostingRecordTriggerType()), i13, (broadcastRecord2.intent.getFlags() & 33554432) != 0 ? r3 : r9, false);
                                broadcastRecord2.curApp = startProcessLocked;
                                if (startProcessLocked == null) {
                                    Slog.w("BroadcastQueue", "Unable to launch app " + resolveInfo.activityInfo.applicationInfo.packageName + "/" + i12 + " for broadcast " + broadcastRecord2.intent + ": process is bad");
                                    broadcastQueueImpl.logBroadcastReceiverDiscardLocked(broadcastRecord2);
                                    finishReceiverLocked((BroadcastRecord) broadcastRecord2, broadcastRecord2.resultCode, broadcastRecord2.resultData, broadcastRecord2.resultExtras, broadcastRecord2.resultAbort, false);
                                    scheduleBroadcastsLocked();
                                    broadcastRecord2.state = r9;
                                    return;
                                }
                                broadcastQueueImpl.maybeAddBackgroundStartPrivileges(startProcessLocked, broadcastRecord2);
                                broadcastQueueImpl.mPendingBroadcast = broadcastRecord2;
                                broadcastQueueImpl.mPendingBroadcastRecvIndex = i11;
                                return;
                            }
                        }
                        r3 = 1;
                        r9 = 0;
                        if (!broadcastQueueImpl.mPendingBroadcastTimeoutMessage) {
                        }
                        BroadcastOptions broadcastOptions2 = broadcastRecord2.options;
                        obj = broadcastRecord2.receivers.get(i11);
                        if (!(obj instanceof BroadcastFilter)) {
                        }
                    } else {
                        iIntentReceiver3 = iIntentReceiver;
                        broadcastQueueImpl2 = broadcastQueueImpl;
                        r11 = 1;
                        r14 = 0;
                    }
                }
            }
            z3 = r14;
            if (nextBroadcastLocked.state == 0) {
            }
        }
    }

    public final String getTargetPackage(BroadcastRecord broadcastRecord) {
        Intent intent = broadcastRecord.intent;
        if (intent == null) {
            return null;
        }
        if (intent.getPackage() != null) {
            return broadcastRecord.intent.getPackage();
        }
        if (broadcastRecord.intent.getComponent() != null) {
            return broadcastRecord.intent.getComponent().getPackageName();
        }
        return null;
    }

    /* JADX WARN: Removed duplicated region for block: B:18:0x002e  */
    /* JADX WARN: Removed duplicated region for block: B:29:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static void logBootCompletedBroadcastCompletionLatencyIfPossible(BroadcastRecord broadcastRecord) {
        int i;
        int i2;
        List<Object> list = broadcastRecord.receivers;
        int size = list != null ? list.size() : 0;
        if (broadcastRecord.nextReceiver < size) {
            return;
        }
        String action = broadcastRecord.intent.getAction();
        if ("android.intent.action.LOCKED_BOOT_COMPLETED".equals(action)) {
            i2 = 1;
        } else if (!"android.intent.action.BOOT_COMPLETED".equals(action)) {
            i = 0;
            if (i == 0) {
                int i3 = (int) (broadcastRecord.dispatchTime - broadcastRecord.enqueueTime);
                int uptimeMillis = (int) (SystemClock.uptimeMillis() - broadcastRecord.enqueueTime);
                int i4 = (int) (broadcastRecord.dispatchRealTime - broadcastRecord.enqueueRealTime);
                int elapsedRealtime = (int) (SystemClock.elapsedRealtime() - broadcastRecord.enqueueRealTime);
                UserManagerInternal userManagerInternal = (UserManagerInternal) LocalServices.getService(UserManagerInternal.class);
                UserInfo userInfo = userManagerInternal != null ? userManagerInternal.getUserInfo(broadcastRecord.userId) : null;
                int userTypeForStatsd = userInfo != null ? UserManager.getUserTypeForStatsd(userInfo.userType) : 0;
                StringBuilder sb = new StringBuilder();
                sb.append("BOOT_COMPLETED_BROADCAST_COMPLETION_LATENCY_REPORTED action:");
                sb.append(action);
                sb.append(" dispatchLatency:");
                sb.append(i3);
                sb.append(" completeLatency:");
                sb.append(uptimeMillis);
                sb.append(" dispatchRealLatency:");
                sb.append(i4);
                sb.append(" completeRealLatency:");
                sb.append(elapsedRealtime);
                sb.append(" receiversSize:");
                sb.append(size);
                sb.append(" userId:");
                sb.append(broadcastRecord.userId);
                sb.append(" userType:");
                sb.append(userInfo != null ? userInfo.userType : null);
                Slog.i("BroadcastQueue", sb.toString());
                FrameworkStatsLog.write((int) FrameworkStatsLog.BOOT_COMPLETED_BROADCAST_COMPLETION_LATENCY_REPORTED, i, i3, uptimeMillis, i4, elapsedRealtime, broadcastRecord.userId, userTypeForStatsd);
                return;
            }
            return;
        } else {
            i2 = 2;
        }
        i = i2;
        if (i == 0) {
        }
    }

    public final void maybeReportBroadcastDispatchedEventLocked(BroadcastRecord broadcastRecord, int i) {
        String targetPackage;
        BroadcastOptions broadcastOptions = broadcastRecord.options;
        if (broadcastOptions == null || broadcastOptions.getIdForResponseEvent() <= 0 || (targetPackage = getTargetPackage(broadcastRecord)) == null) {
            return;
        }
        this.mService.mUsageStatsService.reportBroadcastDispatched(broadcastRecord.callingUid, targetPackage, UserHandle.of(broadcastRecord.userId), broadcastRecord.options.getIdForResponseEvent(), SystemClock.elapsedRealtime(), this.mService.getUidStateLocked(i));
    }

    public final void maybeAddBackgroundStartPrivileges(ProcessRecord processRecord, BroadcastRecord broadcastRecord) {
        if (broadcastRecord == null || processRecord == null || !broadcastRecord.mBackgroundStartPrivileges.allowsAny()) {
            return;
        }
        this.mHandler.removeCallbacksAndMessages((processRecord.toShortString() + broadcastRecord.toString()).intern());
        processRecord.addOrUpdateBackgroundStartPrivileges(broadcastRecord, broadcastRecord.mBackgroundStartPrivileges);
    }

    public final void setBroadcastTimeoutLocked(long j) {
        if (this.mPendingBroadcastTimeoutMessage) {
            return;
        }
        this.mHandler.sendMessageAtTime(this.mHandler.obtainMessage(201, this), j);
        this.mPendingBroadcastTimeoutMessage = true;
    }

    public final void cancelBroadcastTimeoutLocked() {
        if (this.mPendingBroadcastTimeoutMessage) {
            this.mHandler.removeMessages(201, this);
            this.mPendingBroadcastTimeoutMessage = false;
        }
    }

    public final void broadcastTimeoutLocked(boolean z) {
        Object obj;
        ProcessRecord processRecord;
        boolean z2 = false;
        if (z) {
            this.mPendingBroadcastTimeoutMessage = false;
        }
        if (this.mDispatcher.isEmpty() || this.mDispatcher.getActiveBroadcastLocked() == null) {
            return;
        }
        Trace.traceBegin(64L, "broadcastTimeoutLocked()");
        try {
            long uptimeMillis = SystemClock.uptimeMillis();
            BroadcastRecord activeBroadcastLocked = this.mDispatcher.getActiveBroadcastLocked();
            if (z) {
                if (!this.mService.mProcessesReady) {
                    return;
                }
                if (activeBroadcastLocked.timeoutExempt) {
                    return;
                }
                long j = activeBroadcastLocked.receiverTime + this.mConstants.TIMEOUT;
                if (j > uptimeMillis) {
                    setBroadcastTimeoutLocked(j);
                    return;
                }
            }
            if (activeBroadcastLocked.state == 4) {
                StringBuilder sb = new StringBuilder();
                sb.append("Waited long enough for: ");
                ComponentName componentName = activeBroadcastLocked.curComponent;
                sb.append(componentName != null ? componentName.flattenToShortString() : "(null)");
                Slog.i("BroadcastQueue", sb.toString());
                activeBroadcastLocked.curComponent = null;
                activeBroadcastLocked.state = 0;
                processNextBroadcastLocked(false, false);
                return;
            }
            ProcessRecord processRecord2 = activeBroadcastLocked.curApp;
            if (processRecord2 != null && processRecord2.isDebugging()) {
                z2 = true;
            }
            long j2 = uptimeMillis - activeBroadcastLocked.receiverTime;
            Slog.w("BroadcastQueue", "Timeout of broadcast " + activeBroadcastLocked + " - curFilter=" + activeBroadcastLocked.curFilter + " curReceiver=" + activeBroadcastLocked.curReceiver + ", started " + j2 + "ms ago");
            activeBroadcastLocked.receiverTime = uptimeMillis;
            if (!z2) {
                activeBroadcastLocked.anrCount++;
            }
            int i = activeBroadcastLocked.nextReceiver;
            if (i > 0) {
                obj = activeBroadcastLocked.receivers.get(i - 1);
                activeBroadcastLocked.delivery[activeBroadcastLocked.nextReceiver - 1] = 3;
            } else {
                obj = activeBroadcastLocked.curReceiver;
            }
            Slog.w("BroadcastQueue", "Receiver during timeout of " + activeBroadcastLocked + " : " + obj);
            logBroadcastReceiverDiscardLocked(activeBroadcastLocked);
            TimeoutRecord forBroadcastReceiver = TimeoutRecord.forBroadcastReceiver(activeBroadcastLocked.intent, j2);
            if (obj == null || !(obj instanceof BroadcastFilter)) {
                processRecord = activeBroadcastLocked.curApp;
            } else {
                BroadcastFilter broadcastFilter = (BroadcastFilter) obj;
                int i2 = broadcastFilter.receiverList.pid;
                if (i2 == 0 || i2 == ActivityManagerService.MY_PID) {
                    processRecord = null;
                } else {
                    forBroadcastReceiver.mLatencyTracker.waitingOnPidLockStarted();
                    synchronized (this.mService.mPidsSelfLocked) {
                        forBroadcastReceiver.mLatencyTracker.waitingOnPidLockEnded();
                        processRecord = this.mService.mPidsSelfLocked.get(broadcastFilter.receiverList.pid);
                    }
                }
            }
            if (this.mPendingBroadcast == activeBroadcastLocked) {
                this.mPendingBroadcast = null;
            }
            finishReceiverLocked(activeBroadcastLocked, activeBroadcastLocked.resultCode, activeBroadcastLocked.resultData, activeBroadcastLocked.resultExtras, activeBroadcastLocked.resultAbort, false);
            scheduleBroadcastsLocked();
            if (!z2 && processRecord != null) {
                this.mService.appNotResponding(processRecord, forBroadcastReceiver);
            }
        } finally {
            Trace.traceEnd(64L);
        }
    }

    public final void addBroadcastToHistoryLocked(BroadcastRecord broadcastRecord) {
        if (broadcastRecord.callingUid < 0) {
            return;
        }
        broadcastRecord.finishTime = SystemClock.uptimeMillis();
        if (Trace.isTagEnabled(64L)) {
            Trace.asyncTraceEnd(64L, createBroadcastTraceTitle(broadcastRecord, 1), System.identityHashCode(broadcastRecord));
        }
        this.mService.notifyBroadcastFinishedLocked(broadcastRecord);
        this.mHistory.addBroadcastToHistoryLocked(broadcastRecord);
    }

    @Override // com.android.server.p006am.BroadcastQueue
    public boolean cleanupDisabledPackageReceiversLocked(String str, Set<String> set, int i) {
        boolean z = false;
        for (int size = this.mParallelBroadcasts.size() - 1; size >= 0; size--) {
            z |= this.mParallelBroadcasts.get(size).cleanupDisabledPackageReceiversLocked(str, set, i, true);
        }
        return this.mDispatcher.cleanupDisabledPackageReceiversLocked(str, set, i, true) | z;
    }

    public final void logBroadcastReceiverDiscardLocked(BroadcastRecord broadcastRecord) {
        int i = broadcastRecord.nextReceiver - 1;
        if (i >= 0 && i < broadcastRecord.receivers.size()) {
            Object obj = broadcastRecord.receivers.get(i);
            if (obj instanceof BroadcastFilter) {
                BroadcastFilter broadcastFilter = (BroadcastFilter) obj;
                EventLog.writeEvent(30024, Integer.valueOf(broadcastFilter.owningUserId), Integer.valueOf(System.identityHashCode(broadcastRecord)), broadcastRecord.intent.getAction(), Integer.valueOf(i), Integer.valueOf(System.identityHashCode(broadcastFilter)));
                return;
            }
            ResolveInfo resolveInfo = (ResolveInfo) obj;
            EventLog.writeEvent(30025, Integer.valueOf(UserHandle.getUserId(resolveInfo.activityInfo.applicationInfo.uid)), Integer.valueOf(System.identityHashCode(broadcastRecord)), broadcastRecord.intent.getAction(), Integer.valueOf(i), resolveInfo.toString());
            return;
        }
        if (i < 0) {
            Slog.w("BroadcastQueue", "Discarding broadcast before first receiver is invoked: " + broadcastRecord);
        }
        EventLog.writeEvent(30025, -1, Integer.valueOf(System.identityHashCode(broadcastRecord)), broadcastRecord.intent.getAction(), Integer.valueOf(broadcastRecord.nextReceiver), "NONE");
    }

    public final String createBroadcastTraceTitle(BroadcastRecord broadcastRecord, int i) {
        Object[] objArr = new Object[4];
        objArr[0] = i == 0 ? "in queue" : "dispatched";
        String str = broadcastRecord.callerPackage;
        if (str == null) {
            str = "";
        }
        objArr[1] = str;
        ProcessRecord processRecord = broadcastRecord.callerApp;
        objArr[2] = processRecord == null ? "process unknown" : processRecord.toShortString();
        Intent intent = broadcastRecord.intent;
        objArr[3] = intent != null ? intent.getAction() : "";
        return TextUtils.formatSimple("Broadcast %s from %s (%s) %s", objArr);
    }

    @Override // com.android.server.p006am.BroadcastQueue
    /* renamed from: isIdleLocked */
    public boolean lambda$waitForIdle$1() {
        return this.mParallelBroadcasts.isEmpty() && this.mDispatcher.isIdle() && this.mPendingBroadcast == null;
    }

    /* renamed from: isBeyondBarrierLocked */
    public boolean lambda$waitForBarrier$2(long j) {
        if (lambda$waitForIdle$1()) {
            return true;
        }
        for (int i = 0; i < this.mParallelBroadcasts.size(); i++) {
            if (this.mParallelBroadcasts.get(i).enqueueTime <= j) {
                return false;
            }
        }
        BroadcastRecord pendingBroadcastLocked = getPendingBroadcastLocked();
        if (pendingBroadcastLocked == null || pendingBroadcastLocked.enqueueTime > j) {
            return this.mDispatcher.isBeyondBarrier(j);
        }
        return false;
    }

    @Override // com.android.server.p006am.BroadcastQueue
    public void waitForIdle(PrintWriter printWriter) {
        waitFor(new BooleanSupplier() { // from class: com.android.server.am.BroadcastQueueImpl$$ExternalSyntheticLambda2
            @Override // java.util.function.BooleanSupplier
            public final boolean getAsBoolean() {
                boolean lambda$waitForIdle$1;
                lambda$waitForIdle$1 = BroadcastQueueImpl.this.lambda$waitForIdle$1();
                return lambda$waitForIdle$1;
            }
        }, printWriter, "idle");
    }

    @Override // com.android.server.p006am.BroadcastQueue
    public void waitForBarrier(PrintWriter printWriter) {
        final long uptimeMillis = SystemClock.uptimeMillis();
        waitFor(new BooleanSupplier() { // from class: com.android.server.am.BroadcastQueueImpl$$ExternalSyntheticLambda1
            @Override // java.util.function.BooleanSupplier
            public final boolean getAsBoolean() {
                boolean lambda$waitForBarrier$2;
                lambda$waitForBarrier$2 = BroadcastQueueImpl.this.lambda$waitForBarrier$2(uptimeMillis);
                return lambda$waitForBarrier$2;
            }
        }, printWriter, "barrier");
    }

    public final void waitFor(BooleanSupplier booleanSupplier, PrintWriter printWriter, String str) {
        long j = 0;
        while (true) {
            synchronized (this.mService) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    if (booleanSupplier.getAsBoolean()) {
                        break;
                    }
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
            long uptimeMillis = SystemClock.uptimeMillis();
            if (uptimeMillis >= 1000 + j) {
                String str2 = "Queue [" + this.mQueueName + "] waiting for " + str + " condition; state is " + describeStateLocked();
                Slog.v("BroadcastQueue", str2);
                if (printWriter != null) {
                    printWriter.println(str2);
                    printWriter.flush();
                }
                j = uptimeMillis;
            }
            cancelDeferrals();
            SystemClock.sleep(100L);
        }
        String str3 = "Queue [" + this.mQueueName + "] reached " + str + " condition";
        Slog.v("BroadcastQueue", str3);
        if (printWriter != null) {
            printWriter.println(str3);
            printWriter.flush();
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
    }

    public void cancelDeferrals() {
        synchronized (this.mService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                this.mDispatcher.cancelDeferralsLocked();
                scheduleBroadcastsLocked();
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
    }

    @Override // com.android.server.p006am.BroadcastQueue
    public String describeStateLocked() {
        return this.mParallelBroadcasts.size() + " parallel; " + this.mDispatcher.describeStateLocked();
    }

    @Override // com.android.server.p006am.BroadcastQueue
    @NeverCompile
    public void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
        long start = protoOutputStream.start(j);
        protoOutputStream.write(1138166333441L, this.mQueueName);
        for (int size = this.mParallelBroadcasts.size() - 1; size >= 0; size--) {
            this.mParallelBroadcasts.get(size).dumpDebug(protoOutputStream, 2246267895810L);
        }
        this.mDispatcher.dumpDebug(protoOutputStream, 2246267895811L);
        BroadcastRecord broadcastRecord = this.mPendingBroadcast;
        if (broadcastRecord != null) {
            broadcastRecord.dumpDebug(protoOutputStream, 1146756268036L);
        }
        this.mHistory.dumpDebug(protoOutputStream);
        protoOutputStream.end(start);
    }

    @Override // com.android.server.p006am.BroadcastQueue
    @NeverCompile
    public boolean dumpLocked(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr, int i, boolean z, boolean z2, boolean z3, String str, boolean z4) {
        boolean z5;
        BroadcastRecord broadcastRecord;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        if (this.mParallelBroadcasts.isEmpty() && this.mDispatcher.isEmpty() && this.mPendingBroadcast == null) {
            z5 = z4;
        } else {
            boolean z6 = false;
            boolean z7 = z4;
            for (int size = this.mParallelBroadcasts.size() - 1; size >= 0; size--) {
                BroadcastRecord broadcastRecord2 = this.mParallelBroadcasts.get(size);
                if (str == null || str.equals(broadcastRecord2.callerPackage)) {
                    if (!z6) {
                        if (z7) {
                            printWriter.println();
                        }
                        printWriter.println("  Active broadcasts [" + this.mQueueName + "]:");
                        z7 = true;
                        z6 = true;
                    }
                    printWriter.println("  Active Broadcast " + this.mQueueName + " #" + size + XmlUtils.STRING_ARRAY_SEPARATOR);
                    broadcastRecord2.dump(printWriter, "    ", simpleDateFormat);
                }
            }
            this.mDispatcher.dumpLocked(printWriter, str, this.mQueueName, simpleDateFormat);
            if (str == null || ((broadcastRecord = this.mPendingBroadcast) != null && str.equals(broadcastRecord.callerPackage))) {
                printWriter.println();
                printWriter.println("  Pending broadcast [" + this.mQueueName + "]:");
                BroadcastRecord broadcastRecord3 = this.mPendingBroadcast;
                if (broadcastRecord3 != null) {
                    broadcastRecord3.dump(printWriter, "    ", simpleDateFormat);
                } else {
                    printWriter.println("    (null)");
                }
                z5 = true;
            } else {
                z5 = z7;
            }
        }
        if (z) {
            this.mConstants.dump(new IndentingPrintWriter(printWriter));
        }
        return z2 ? this.mHistory.dumpLocked(printWriter, str, this.mQueueName, simpleDateFormat, z3, z5) : z5;
    }
}
