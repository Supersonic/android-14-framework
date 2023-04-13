package com.android.server.p006am;

import android.app.BroadcastOptions;
import android.app.IApplicationThread;
import android.app.UidObserver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.content.res.CompatibilityInfo;
import android.net.INetd;
import android.os.Bundle;
import android.os.BundleMerger;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.ArraySet;
import android.util.IndentingPrintWriter;
import android.util.MathUtils;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.os.SomeArgs;
import com.android.internal.os.TimeoutRecord;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.p006am.BroadcastProcessQueue;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import dalvik.annotation.optimization.NeverCompile;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;
/* renamed from: com.android.server.am.BroadcastQueueModernImpl */
/* loaded from: classes.dex */
public class BroadcastQueueModernImpl extends BroadcastQueue {
    public final BroadcastConstants mBgConstants;
    public final BroadcastProcessQueue.BroadcastConsumer mBroadcastConsumerDefer;
    public final BroadcastProcessQueue.BroadcastConsumer mBroadcastConsumerSkip;
    public final BroadcastProcessQueue.BroadcastConsumer mBroadcastConsumerSkipAndCanceled;
    public final BroadcastProcessQueue.BroadcastConsumer mBroadcastConsumerUndoDefer;
    public final BroadcastConstants mConstants;
    public final BroadcastConstants mFgConstants;
    public long mLastTestFailureTime;
    public final Handler.Callback mLocalCallback;
    public final Handler mLocalHandler;
    @GuardedBy({"mService"})
    public final SparseArray<BroadcastProcessQueue> mProcessQueues;
    @GuardedBy({"mService"})
    public final AtomicReference<ArraySet<BroadcastRecord>> mReplacedBroadcastsCache;
    public BroadcastProcessQueue mRunnableHead;
    @GuardedBy({"mService"})
    public final BroadcastProcessQueue[] mRunning;
    @GuardedBy({"mService"})
    public BroadcastProcessQueue mRunningColdStart;
    @GuardedBy({"mService"})
    public final ArrayList<Pair<BooleanSupplier, CountDownLatch>> mWaitingFor;
    public static final Predicate<BroadcastProcessQueue> QUEUE_PREDICATE_ANY = new Predicate() { // from class: com.android.server.am.BroadcastQueueModernImpl$$ExternalSyntheticLambda13
        @Override // java.util.function.Predicate
        public final boolean test(Object obj) {
            boolean lambda$static$8;
            lambda$static$8 = BroadcastQueueModernImpl.lambda$static$8((BroadcastProcessQueue) obj);
            return lambda$static$8;
        }
    };
    public static final BroadcastProcessQueue.BroadcastPredicate BROADCAST_PREDICATE_ANY = new BroadcastProcessQueue.BroadcastPredicate() { // from class: com.android.server.am.BroadcastQueueModernImpl$$ExternalSyntheticLambda14
        @Override // com.android.server.p006am.BroadcastProcessQueue.BroadcastPredicate
        public final boolean test(BroadcastRecord broadcastRecord, int i) {
            boolean lambda$static$9;
            lambda$static$9 = BroadcastQueueModernImpl.lambda$static$9(broadcastRecord, i);
            return lambda$static$9;
        }
    };

    public static /* synthetic */ boolean lambda$static$8(BroadcastProcessQueue broadcastProcessQueue) {
        return true;
    }

    public static /* synthetic */ boolean lambda$static$9(BroadcastRecord broadcastRecord, int i) {
        return true;
    }

    @Override // com.android.server.p006am.BroadcastQueue
    public void backgroundServicesFinishedLocked(int i) {
    }

    @Override // com.android.server.p006am.BroadcastQueue
    public boolean isDelayBehindServices() {
        return false;
    }

    public BroadcastQueueModernImpl(ActivityManagerService activityManagerService, Handler handler, BroadcastConstants broadcastConstants, BroadcastConstants broadcastConstants2) {
        this(activityManagerService, handler, broadcastConstants, broadcastConstants2, new BroadcastSkipPolicy(activityManagerService), new BroadcastHistory(broadcastConstants));
    }

    public BroadcastQueueModernImpl(ActivityManagerService activityManagerService, Handler handler, BroadcastConstants broadcastConstants, BroadcastConstants broadcastConstants2, BroadcastSkipPolicy broadcastSkipPolicy, BroadcastHistory broadcastHistory) {
        super(activityManagerService, handler, "modern", broadcastSkipPolicy, broadcastHistory);
        this.mProcessQueues = new SparseArray<>();
        this.mRunnableHead = null;
        this.mWaitingFor = new ArrayList<>();
        this.mReplacedBroadcastsCache = new AtomicReference<>();
        Handler.Callback callback = new Handler.Callback() { // from class: com.android.server.am.BroadcastQueueModernImpl$$ExternalSyntheticLambda4
            @Override // android.os.Handler.Callback
            public final boolean handleMessage(Message message) {
                boolean lambda$new$0;
                lambda$new$0 = BroadcastQueueModernImpl.this.lambda$new$0(message);
                return lambda$new$0;
            }
        };
        this.mLocalCallback = callback;
        this.mBroadcastConsumerSkip = new BroadcastProcessQueue.BroadcastConsumer() { // from class: com.android.server.am.BroadcastQueueModernImpl$$ExternalSyntheticLambda5
            @Override // com.android.server.p006am.BroadcastProcessQueue.BroadcastConsumer
            public final void accept(BroadcastRecord broadcastRecord, int i) {
                BroadcastQueueModernImpl.this.lambda$new$10(broadcastRecord, i);
            }
        };
        this.mBroadcastConsumerSkipAndCanceled = new BroadcastProcessQueue.BroadcastConsumer() { // from class: com.android.server.am.BroadcastQueueModernImpl$$ExternalSyntheticLambda6
            @Override // com.android.server.p006am.BroadcastProcessQueue.BroadcastConsumer
            public final void accept(BroadcastRecord broadcastRecord, int i) {
                BroadcastQueueModernImpl.this.lambda$new$11(broadcastRecord, i);
            }
        };
        this.mBroadcastConsumerDefer = new BroadcastProcessQueue.BroadcastConsumer() { // from class: com.android.server.am.BroadcastQueueModernImpl$$ExternalSyntheticLambda7
            @Override // com.android.server.p006am.BroadcastProcessQueue.BroadcastConsumer
            public final void accept(BroadcastRecord broadcastRecord, int i) {
                BroadcastQueueModernImpl.this.lambda$new$12(broadcastRecord, i);
            }
        };
        this.mBroadcastConsumerUndoDefer = new BroadcastProcessQueue.BroadcastConsumer() { // from class: com.android.server.am.BroadcastQueueModernImpl$$ExternalSyntheticLambda8
            @Override // com.android.server.p006am.BroadcastProcessQueue.BroadcastConsumer
            public final void accept(BroadcastRecord broadcastRecord, int i) {
                BroadcastQueueModernImpl.this.lambda$new$13(broadcastRecord, i);
            }
        };
        Objects.requireNonNull(broadcastConstants);
        this.mConstants = broadcastConstants;
        this.mFgConstants = broadcastConstants;
        Objects.requireNonNull(broadcastConstants2);
        this.mBgConstants = broadcastConstants2;
        this.mLocalHandler = new Handler(handler.getLooper(), callback);
        this.mRunning = new BroadcastProcessQueue[broadcastConstants.getMaxRunningQueues()];
    }

    public final void enqueueUpdateRunningList() {
        this.mLocalHandler.removeMessages(1);
        this.mLocalHandler.sendEmptyMessage(1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$0(Message message) {
        int i = message.what;
        if (i == 1) {
            synchronized (this.mService) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    updateRunningListLocked();
                } finally {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
            return true;
        } else if (i == 2) {
            synchronized (this.mService) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    deliveryTimeoutSoftLocked((BroadcastProcessQueue) message.obj, message.arg1);
                } finally {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
            return true;
        } else if (i == 3) {
            synchronized (this.mService) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    deliveryTimeoutHardLocked((BroadcastProcessQueue) message.obj);
                } finally {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
            return true;
        } else if (i != 4) {
            if (i != 5) {
                return false;
            }
            synchronized (this.mService) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    checkHealthLocked();
                } finally {
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
            return true;
        } else {
            synchronized (this.mService) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    SomeArgs someArgs = (SomeArgs) message.obj;
                    someArgs.recycle();
                    ((ProcessRecord) someArgs.arg1).removeBackgroundStartPrivileges((BroadcastRecord) someArgs.arg2);
                } finally {
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
            return true;
        }
    }

    public final int getRunningSize() {
        int i = 0;
        int i2 = 0;
        while (true) {
            BroadcastProcessQueue[] broadcastProcessQueueArr = this.mRunning;
            if (i >= broadcastProcessQueueArr.length) {
                return i2;
            }
            if (broadcastProcessQueueArr[i] != null) {
                i2++;
            }
            i++;
        }
    }

    public final int getRunningUrgentCount() {
        int i = 0;
        int i2 = 0;
        while (true) {
            BroadcastProcessQueue[] broadcastProcessQueueArr = this.mRunning;
            if (i >= broadcastProcessQueueArr.length) {
                return i2;
            }
            BroadcastProcessQueue broadcastProcessQueue = broadcastProcessQueueArr[i];
            if (broadcastProcessQueue != null && broadcastProcessQueue.getActive().isUrgent()) {
                i2++;
            }
            i++;
        }
    }

    public final int getRunningIndexOf(BroadcastProcessQueue broadcastProcessQueue) {
        int i = 0;
        while (true) {
            BroadcastProcessQueue[] broadcastProcessQueueArr = this.mRunning;
            if (i >= broadcastProcessQueueArr.length) {
                return -1;
            }
            if (broadcastProcessQueueArr[i] == broadcastProcessQueue) {
                return i;
            }
            i++;
        }
    }

    @GuardedBy({"mService"})
    public final void updateRunnableList(BroadcastProcessQueue broadcastProcessQueue) {
        if (getRunningIndexOf(broadcastProcessQueue) >= 0) {
            return;
        }
        boolean isRunnable = broadcastProcessQueue.isRunnable();
        BroadcastProcessQueue broadcastProcessQueue2 = this.mRunnableHead;
        boolean z = false;
        boolean z2 = (broadcastProcessQueue != broadcastProcessQueue2 && broadcastProcessQueue.runnableAtPrev == null && broadcastProcessQueue.runnableAtNext == null) ? false : true;
        if (isRunnable) {
            if (z2) {
                BroadcastProcessQueue broadcastProcessQueue3 = broadcastProcessQueue.runnableAtPrev;
                boolean z3 = broadcastProcessQueue3 == null || broadcastProcessQueue3.getRunnableAt() <= broadcastProcessQueue.getRunnableAt();
                BroadcastProcessQueue broadcastProcessQueue4 = broadcastProcessQueue.runnableAtNext;
                if (broadcastProcessQueue4 == null || broadcastProcessQueue4.getRunnableAt() >= broadcastProcessQueue.getRunnableAt()) {
                    z = true;
                }
                if (!z3 || !z) {
                    BroadcastProcessQueue removeFromRunnableList = BroadcastProcessQueue.removeFromRunnableList(this.mRunnableHead, broadcastProcessQueue);
                    this.mRunnableHead = removeFromRunnableList;
                    this.mRunnableHead = BroadcastProcessQueue.insertIntoRunnableList(removeFromRunnableList, broadcastProcessQueue);
                }
            } else {
                this.mRunnableHead = BroadcastProcessQueue.insertIntoRunnableList(broadcastProcessQueue2, broadcastProcessQueue);
            }
        } else if (z2) {
            this.mRunnableHead = BroadcastProcessQueue.removeFromRunnableList(broadcastProcessQueue2, broadcastProcessQueue);
        }
        if (broadcastProcessQueue.isEmpty() && !broadcastProcessQueue.isActive() && !broadcastProcessQueue.isProcessWarm()) {
            removeProcessQueue(broadcastProcessQueue.processName, broadcastProcessQueue.uid);
        } else {
            updateQueueDeferred(broadcastProcessQueue);
        }
    }

    @GuardedBy({"mService"})
    public final void updateRunningListLocked() {
        int length = (this.mRunning.length - getRunningSize()) - Math.min(getRunningUrgentCount(), this.mConstants.EXTRA_RUNNING_URGENT_PROCESS_QUEUES);
        if (length == 0) {
            return;
        }
        int traceBegin = BroadcastQueue.traceBegin("updateRunningList");
        long uptimeMillis = SystemClock.uptimeMillis();
        boolean z = !this.mWaitingFor.isEmpty();
        this.mLocalHandler.removeMessages(1);
        BroadcastProcessQueue broadcastProcessQueue = this.mRunnableHead;
        boolean z2 = false;
        while (true) {
            if (broadcastProcessQueue == null || length <= 0) {
                break;
            }
            BroadcastProcessQueue broadcastProcessQueue2 = broadcastProcessQueue.runnableAtNext;
            long runnableAt = broadcastProcessQueue.getRunnableAt();
            if (broadcastProcessQueue.isRunnable() && (getRunningSize() < this.mConstants.MAX_RUNNING_PROCESS_QUEUES || broadcastProcessQueue.isPendingUrgent())) {
                if (runnableAt > uptimeMillis && !z) {
                    this.mLocalHandler.sendEmptyMessageAtTime(1, runnableAt);
                    break;
                }
                updateWarmProcess(broadcastProcessQueue);
                boolean isProcessWarm = broadcastProcessQueue.isProcessWarm();
                if (!isProcessWarm) {
                    if (this.mRunningColdStart == null) {
                        this.mRunningColdStart = broadcastProcessQueue;
                    }
                }
                int runningIndexOf = getRunningIndexOf(null);
                this.mRunning[runningIndexOf] = broadcastProcessQueue;
                length--;
                this.mRunnableHead = BroadcastProcessQueue.removeFromRunnableList(this.mRunnableHead, broadcastProcessQueue);
                broadcastProcessQueue.runningTraceTrackName = "BroadcastQueue.mRunning[" + runningIndexOf + "]";
                broadcastProcessQueue.runningOomAdjusted = broadcastProcessQueue.isPendingManifest() || broadcastProcessQueue.isPendingOrdered() || broadcastProcessQueue.isPendingResultTo();
                if (isProcessWarm) {
                    notifyStartedRunning(broadcastProcessQueue);
                    z2 |= broadcastProcessQueue.runningOomAdjusted;
                }
                broadcastProcessQueue.makeActiveNextPending();
                if (isProcessWarm) {
                    broadcastProcessQueue.traceProcessRunningBegin();
                    scheduleReceiverWarmLocked(broadcastProcessQueue);
                } else {
                    broadcastProcessQueue.traceProcessStartingBegin();
                    scheduleReceiverColdLocked(broadcastProcessQueue);
                }
            }
            broadcastProcessQueue = broadcastProcessQueue2;
        }
        if (z2) {
            this.mService.updateOomAdjPendingTargetsLocked(3);
        }
        checkAndRemoveWaitingFor();
        BroadcastQueue.traceEnd(traceBegin);
    }

    @Override // com.android.server.p006am.BroadcastQueue
    public boolean onApplicationAttachedLocked(ProcessRecord processRecord) {
        BroadcastProcessQueue processQueue = getProcessQueue(processRecord);
        if (processQueue != null) {
            processQueue.setProcess(processRecord);
        }
        BroadcastProcessQueue broadcastProcessQueue = this.mRunningColdStart;
        if (broadcastProcessQueue == null || broadcastProcessQueue != processQueue) {
            return false;
        }
        this.mRunningColdStart = null;
        notifyStartedRunning(processQueue);
        this.mService.updateOomAdjPendingTargetsLocked(3);
        processQueue.traceProcessEnd();
        processQueue.traceProcessRunningBegin();
        scheduleReceiverWarmLocked(processQueue);
        enqueueUpdateRunningList();
        return true;
    }

    @Override // com.android.server.p006am.BroadcastQueue
    public void onApplicationTimeoutLocked(ProcessRecord processRecord) {
        onApplicationCleanupLocked(processRecord);
    }

    @Override // com.android.server.p006am.BroadcastQueue
    public void onApplicationProblemLocked(ProcessRecord processRecord) {
        onApplicationCleanupLocked(processRecord);
    }

    @Override // com.android.server.p006am.BroadcastQueue
    public void onApplicationCleanupLocked(ProcessRecord processRecord) {
        BroadcastProcessQueue processQueue = getProcessQueue(processRecord);
        if (processQueue != null) {
            processQueue.setProcess(null);
        }
        BroadcastProcessQueue broadcastProcessQueue = this.mRunningColdStart;
        if (broadcastProcessQueue != null && broadcastProcessQueue == processQueue) {
            this.mRunningColdStart = null;
            processQueue.traceProcessEnd();
            enqueueUpdateRunningList();
        }
        if (processQueue != null) {
            if (processQueue.isActive()) {
                finishReceiverActiveLocked(processQueue, 5, "onApplicationCleanupLocked");
            }
            if (processQueue.forEachMatchingBroadcast(new BroadcastProcessQueue.BroadcastPredicate() { // from class: com.android.server.am.BroadcastQueueModernImpl$$ExternalSyntheticLambda3
                @Override // com.android.server.p006am.BroadcastProcessQueue.BroadcastPredicate
                public final boolean test(BroadcastRecord broadcastRecord, int i) {
                    boolean lambda$onApplicationCleanupLocked$1;
                    lambda$onApplicationCleanupLocked$1 = BroadcastQueueModernImpl.lambda$onApplicationCleanupLocked$1(broadcastRecord, i);
                    return lambda$onApplicationCleanupLocked$1;
                }
            }, this.mBroadcastConsumerSkip, true) || processQueue.isEmpty()) {
                updateRunnableList(processQueue);
                enqueueUpdateRunningList();
            }
        }
    }

    public static /* synthetic */ boolean lambda$onApplicationCleanupLocked$1(BroadcastRecord broadcastRecord, int i) {
        return broadcastRecord.receivers.get(i) instanceof BroadcastFilter;
    }

    @Override // com.android.server.p006am.BroadcastQueue
    public int getPreferredSchedulingGroupLocked(ProcessRecord processRecord) {
        BroadcastProcessQueue processQueue = getProcessQueue(processRecord);
        if (processQueue == null || getRunningIndexOf(processQueue) < 0) {
            return Integer.MIN_VALUE;
        }
        return processQueue.getPreferredSchedulingGroupLocked();
    }

    @Override // com.android.server.p006am.BroadcastQueue
    public void enqueueBroadcastLocked(BroadcastRecord broadcastRecord) {
        boolean z;
        BroadcastProcessQueue broadcastProcessQueue;
        int traceBegin = BroadcastQueue.traceBegin("enqueueBroadcast");
        broadcastRecord.applySingletonPolicy(this.mService);
        applyDeliveryGroupPolicy(broadcastRecord);
        broadcastRecord.enqueueTime = SystemClock.uptimeMillis();
        broadcastRecord.enqueueRealTime = SystemClock.elapsedRealtime();
        broadcastRecord.enqueueClockTime = System.currentTimeMillis();
        this.mHistory.onBroadcastEnqueuedLocked(broadcastRecord);
        ArraySet<BroadcastRecord> andSet = this.mReplacedBroadcastsCache.getAndSet(null);
        if (andSet == null) {
            andSet = new ArraySet<>();
        }
        ArraySet<BroadcastRecord> arraySet = andSet;
        boolean z2 = false;
        for (int i = 0; i < broadcastRecord.receivers.size(); i++) {
            Object obj = broadcastRecord.receivers.get(i);
            BroadcastProcessQueue orCreateProcessQueue = getOrCreateProcessQueue(BroadcastRecord.getReceiverProcessName(obj), BroadcastRecord.getReceiverUid(obj));
            if (obj instanceof ResolveInfo) {
                z = this.mSkipPolicy.shouldSkipMessage(broadcastRecord, obj) != null;
                if (z && orCreateProcessQueue.app == null && !orCreateProcessQueue.getActiveViaColdStart()) {
                    setDeliveryState(null, null, broadcastRecord, i, obj, 2, "skipped by policy to avoid cold start");
                }
            } else {
                z = false;
            }
            BroadcastRecord enqueueOrReplaceBroadcast = orCreateProcessQueue.enqueueOrReplaceBroadcast(broadcastRecord, i, z);
            if (enqueueOrReplaceBroadcast != null) {
                arraySet.add(enqueueOrReplaceBroadcast);
            }
            if (broadcastRecord.isDeferUntilActive() && orCreateProcessQueue.isDeferredUntilActive()) {
                broadcastProcessQueue = orCreateProcessQueue;
                setDeliveryState(orCreateProcessQueue, null, broadcastRecord, i, obj, 6, "deferred at enqueue time");
            } else {
                broadcastProcessQueue = orCreateProcessQueue;
            }
            updateRunnableList(broadcastProcessQueue);
            enqueueUpdateRunningList();
            z2 = true;
        }
        skipAndCancelReplacedBroadcasts(arraySet);
        arraySet.clear();
        this.mReplacedBroadcastsCache.compareAndSet(null, arraySet);
        if (broadcastRecord.receivers.isEmpty() || !z2) {
            scheduleResultTo(broadcastRecord);
            notifyFinishBroadcast(broadcastRecord);
        }
        BroadcastQueue.traceEnd(traceBegin);
    }

    public final void skipAndCancelReplacedBroadcasts(ArraySet<BroadcastRecord> arraySet) {
        for (int i = 0; i < arraySet.size(); i++) {
            BroadcastRecord valueAt = arraySet.valueAt(i);
            valueAt.resultCode = 0;
            valueAt.resultData = null;
            valueAt.resultExtras = null;
            scheduleResultTo(valueAt);
            notifyFinishBroadcast(valueAt);
        }
    }

    public final void applyDeliveryGroupPolicy(final BroadcastRecord broadcastRecord) {
        BroadcastProcessQueue.BroadcastConsumer broadcastConsumer;
        if (this.mService.shouldIgnoreDeliveryGroupPolicy(broadcastRecord.intent.getAction())) {
            return;
        }
        BroadcastOptions broadcastOptions = broadcastRecord.options;
        int deliveryGroupPolicy = broadcastOptions != null ? broadcastOptions.getDeliveryGroupPolicy() : 0;
        if (deliveryGroupPolicy != 0) {
            if (deliveryGroupPolicy == 1) {
                broadcastConsumer = this.mBroadcastConsumerSkipAndCanceled;
            } else if (deliveryGroupPolicy == 2) {
                final BundleMerger deliveryGroupExtrasMerger = broadcastRecord.options.getDeliveryGroupExtrasMerger();
                if (deliveryGroupExtrasMerger == null) {
                    return;
                }
                broadcastConsumer = new BroadcastProcessQueue.BroadcastConsumer() { // from class: com.android.server.am.BroadcastQueueModernImpl$$ExternalSyntheticLambda9
                    @Override // com.android.server.p006am.BroadcastProcessQueue.BroadcastConsumer
                    public final void accept(BroadcastRecord broadcastRecord2, int i) {
                        BroadcastQueueModernImpl.this.lambda$applyDeliveryGroupPolicy$2(broadcastRecord, deliveryGroupExtrasMerger, broadcastRecord2, i);
                    }
                };
            } else {
                BroadcastQueue.logw("Unknown delivery group policy: " + deliveryGroupPolicy);
                return;
            }
            forEachMatchingBroadcast(QUEUE_PREDICATE_ANY, new BroadcastProcessQueue.BroadcastPredicate() { // from class: com.android.server.am.BroadcastQueueModernImpl$$ExternalSyntheticLambda10
                @Override // com.android.server.p006am.BroadcastProcessQueue.BroadcastPredicate
                public final boolean test(BroadcastRecord broadcastRecord2, int i) {
                    boolean lambda$applyDeliveryGroupPolicy$3;
                    lambda$applyDeliveryGroupPolicy$3 = BroadcastQueueModernImpl.lambda$applyDeliveryGroupPolicy$3(BroadcastRecord.this, broadcastRecord2, i);
                    return lambda$applyDeliveryGroupPolicy$3;
                }
            }, broadcastConsumer, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$applyDeliveryGroupPolicy$2(BroadcastRecord broadcastRecord, BundleMerger bundleMerger, BroadcastRecord broadcastRecord2, int i) {
        broadcastRecord.intent.mergeExtras(broadcastRecord2.intent, bundleMerger);
        this.mBroadcastConsumerSkipAndCanceled.accept(broadcastRecord2, i);
    }

    public static /* synthetic */ boolean lambda$applyDeliveryGroupPolicy$3(BroadcastRecord broadcastRecord, BroadcastRecord broadcastRecord2, int i) {
        return broadcastRecord.callingUid == broadcastRecord2.callingUid && broadcastRecord.userId == broadcastRecord2.userId && broadcastRecord.matchesDeliveryGroup(broadcastRecord2);
    }

    public final void scheduleReceiverColdLocked(BroadcastProcessQueue broadcastProcessQueue) {
        BroadcastQueue.checkState(broadcastProcessQueue.isActive(), "isActive");
        broadcastProcessQueue.setActiveViaColdStart(true);
        BroadcastRecord active = broadcastProcessQueue.getActive();
        int activeIndex = broadcastProcessQueue.getActiveIndex();
        Object obj = active.receivers.get(activeIndex);
        if (obj instanceof BroadcastFilter) {
            this.mRunningColdStart = null;
            finishReceiverActiveLocked(broadcastProcessQueue, 2, "BroadcastFilter for cold app");
        } else if (maybeSkipReceiver(broadcastProcessQueue, active, activeIndex)) {
            this.mRunningColdStart = null;
        } else {
            ActivityInfo activityInfo = ((ResolveInfo) obj).activityInfo;
            ApplicationInfo applicationInfo = activityInfo.applicationInfo;
            ComponentName componentName = activityInfo.getComponentName();
            if ((applicationInfo.flags & 2097152) != 0) {
                broadcastProcessQueue.setActiveWasStopped(true);
            }
            int flags = active.intent.getFlags() | 4;
            HostingRecord hostingRecord = new HostingRecord(INetd.IF_FLAG_BROADCAST, componentName, active.intent.getAction(), active.getHostingRecordTriggerType());
            BroadcastOptions broadcastOptions = active.options;
            ProcessRecord startProcessLocked = this.mService.startProcessLocked(broadcastProcessQueue.processName, applicationInfo, true, flags, hostingRecord, (broadcastOptions == null || broadcastOptions.getTemporaryAppAllowlistDuration() <= 0) ? 0 : 1, (active.intent.getFlags() & 33554432) != 0, false);
            broadcastProcessQueue.app = startProcessLocked;
            if (startProcessLocked == null) {
                this.mRunningColdStart = null;
                finishReceiverActiveLocked(broadcastProcessQueue, 5, "startProcessLocked failed");
            }
        }
    }

    @GuardedBy({"mService"})
    public final void scheduleReceiverWarmLocked(BroadcastProcessQueue broadcastProcessQueue) {
        BroadcastQueue.checkState(broadcastProcessQueue.isActive(), "isActive");
        BroadcastRecord active = broadcastProcessQueue.getActive();
        int activeIndex = broadcastProcessQueue.getActiveIndex();
        if (active.terminalCount == 0) {
            active.dispatchTime = SystemClock.uptimeMillis();
            active.dispatchRealTime = SystemClock.elapsedRealtime();
            active.dispatchClockTime = System.currentTimeMillis();
        }
        if (maybeSkipReceiver(broadcastProcessQueue, active, activeIndex)) {
            return;
        }
        dispatchReceivers(broadcastProcessQueue, active, activeIndex);
    }

    public final boolean maybeSkipReceiver(BroadcastProcessQueue broadcastProcessQueue, BroadcastRecord broadcastRecord, int i) {
        String shouldSkipReceiver = shouldSkipReceiver(broadcastProcessQueue, broadcastRecord, i);
        if (shouldSkipReceiver != null) {
            finishReceiverActiveLocked(broadcastProcessQueue, 2, shouldSkipReceiver);
            return true;
        }
        return false;
    }

    public final String shouldSkipReceiver(BroadcastProcessQueue broadcastProcessQueue, BroadcastRecord broadcastRecord, int i) {
        int deliveryState = getDeliveryState(broadcastRecord, i);
        ProcessRecord processRecord = broadcastProcessQueue.app;
        Object obj = broadcastRecord.receivers.get(i);
        if (BroadcastRecord.isDeliveryStateTerminal(deliveryState)) {
            return "already terminal state";
        }
        if (processRecord == null || !processRecord.isInFullBackup()) {
            String shouldSkipMessage = this.mSkipPolicy.shouldSkipMessage(broadcastRecord, obj);
            if (shouldSkipMessage != null) {
                return shouldSkipMessage;
            }
            if (broadcastRecord.getReceiverIntent(obj) == null) {
                return "getReceiverIntent";
            }
            if (!(obj instanceof BroadcastFilter) || ((BroadcastFilter) obj).receiverList.pid == processRecord.getPid()) {
                return null;
            }
            return "BroadcastFilter for mismatched PID";
        }
        return "isInFullBackup";
    }

    public final boolean isAssumedDelivered(BroadcastRecord broadcastRecord, int i) {
        return (broadcastRecord.receivers.get(i) instanceof BroadcastFilter) && !broadcastRecord.ordered && broadcastRecord.resultTo == null;
    }

    public final void dispatchReceivers(BroadcastProcessQueue broadcastProcessQueue, BroadcastRecord broadcastRecord, int i) {
        ProcessRecord processRecord = broadcastProcessQueue.app;
        Object obj = broadcastRecord.receivers.get(i);
        boolean isAssumedDelivered = isAssumedDelivered(broadcastRecord, i);
        if (this.mService.mProcessesReady && !broadcastRecord.timeoutExempt && !isAssumedDelivered) {
            broadcastProcessQueue.lastCpuDelayTime = broadcastProcessQueue.app.getCpuDelayTime();
            int i2 = (int) (broadcastRecord.isForeground() ? this.mFgConstants.TIMEOUT : this.mBgConstants.TIMEOUT);
            Handler handler = this.mLocalHandler;
            handler.sendMessageDelayed(Message.obtain(handler, 2, i2, 0, broadcastProcessQueue), i2);
        }
        if (broadcastRecord.mBackgroundStartPrivileges.allowsAny()) {
            processRecord.addOrUpdateBackgroundStartPrivileges(broadcastRecord, broadcastRecord.mBackgroundStartPrivileges);
            long j = broadcastRecord.isForeground() ? this.mFgConstants.ALLOW_BG_ACTIVITY_START_TIMEOUT : this.mBgConstants.ALLOW_BG_ACTIVITY_START_TIMEOUT;
            SomeArgs obtain = SomeArgs.obtain();
            obtain.arg1 = processRecord;
            obtain.arg2 = broadcastRecord;
            Handler handler2 = this.mLocalHandler;
            handler2.sendMessageDelayed(Message.obtain(handler2, 4, obtain), j);
        }
        BroadcastOptions broadcastOptions = broadcastRecord.options;
        if (broadcastOptions != null && broadcastOptions.getTemporaryAppAllowlistDuration() > 0) {
            this.mService.tempAllowlistUidLocked(broadcastProcessQueue.uid, broadcastRecord.options.getTemporaryAppAllowlistDuration(), broadcastRecord.options.getTemporaryAppAllowlistReasonCode(), broadcastRecord.toShortString(), broadcastRecord.options.getTemporaryAppAllowlistType(), broadcastRecord.callingUid);
        }
        setDeliveryState(broadcastProcessQueue, processRecord, broadcastRecord, i, obj, 4, "scheduleReceiverWarmLocked");
        Intent receiverIntent = broadcastRecord.getReceiverIntent(obj);
        IApplicationThread onewayThread = processRecord.getOnewayThread();
        if (onewayThread != null) {
            try {
                if (broadcastRecord.shareIdentity) {
                    this.mService.mPackageManagerInt.grantImplicitAccess(broadcastRecord.userId, broadcastRecord.intent, UserHandle.getAppId(processRecord.uid), broadcastRecord.callingUid, true);
                }
                if (obj instanceof BroadcastFilter) {
                    notifyScheduleRegisteredReceiver(processRecord, broadcastRecord, (BroadcastFilter) obj);
                    IIntentReceiver iIntentReceiver = ((BroadcastFilter) obj).receiverList.receiver;
                    int i3 = broadcastRecord.resultCode;
                    String str = broadcastRecord.resultData;
                    Bundle bundle = broadcastRecord.resultExtras;
                    boolean z = broadcastRecord.ordered;
                    boolean z2 = broadcastRecord.initialSticky;
                    int i4 = broadcastRecord.userId;
                    int reportedProcState = processRecord.mState.getReportedProcState();
                    boolean z3 = broadcastRecord.shareIdentity;
                    onewayThread.scheduleRegisteredReceiver(iIntentReceiver, receiverIntent, i3, str, bundle, z, z2, isAssumedDelivered, i4, reportedProcState, z3 ? broadcastRecord.callingUid : -1, z3 ? broadcastRecord.callerPackage : null);
                    if (isAssumedDelivered) {
                        finishReceiverActiveLocked(broadcastProcessQueue, 1, "assuming delivered");
                        return;
                    }
                    return;
                }
                notifyScheduleReceiver(processRecord, broadcastRecord, (ResolveInfo) obj);
                ActivityInfo activityInfo = ((ResolveInfo) obj).activityInfo;
                int i5 = broadcastRecord.resultCode;
                String str2 = broadcastRecord.resultData;
                Bundle bundle2 = broadcastRecord.resultExtras;
                boolean z4 = broadcastRecord.ordered;
                int i6 = broadcastRecord.userId;
                int reportedProcState2 = processRecord.mState.getReportedProcState();
                boolean z5 = broadcastRecord.shareIdentity;
                onewayThread.scheduleReceiver(receiverIntent, activityInfo, (CompatibilityInfo) null, i5, str2, bundle2, z4, isAssumedDelivered, i6, reportedProcState2, z5 ? broadcastRecord.callingUid : -1, z5 ? broadcastRecord.callerPackage : null);
                return;
            } catch (RemoteException e) {
                BroadcastQueue.logw("Failed to schedule " + broadcastRecord + " to " + obj + " via " + processRecord + ": " + e);
                processRecord.killLocked("Can't deliver broadcast", 13, 26, true);
                finishReceiverActiveLocked(broadcastProcessQueue, 5, "remote app");
                return;
            }
        }
        finishReceiverActiveLocked(broadcastProcessQueue, 5, "missing IApplicationThread");
    }

    public final void scheduleResultTo(BroadcastRecord broadcastRecord) {
        int i;
        if (broadcastRecord.resultTo == null) {
            return;
        }
        ProcessRecord processRecord = broadcastRecord.resultToApp;
        IApplicationThread onewayThread = processRecord != null ? processRecord.getOnewayThread() : null;
        if (onewayThread != null) {
            this.mService.mOomAdjuster.mCachedAppOptimizer.unfreezeTemporarily(processRecord, 2);
            if (broadcastRecord.shareIdentity && (i = processRecord.uid) != broadcastRecord.callingUid) {
                this.mService.mPackageManagerInt.grantImplicitAccess(broadcastRecord.userId, broadcastRecord.intent, UserHandle.getAppId(i), broadcastRecord.callingUid, true);
            }
            try {
                IIntentReceiver iIntentReceiver = broadcastRecord.resultTo;
                Intent intent = broadcastRecord.intent;
                int i2 = broadcastRecord.resultCode;
                String str = broadcastRecord.resultData;
                Bundle bundle = broadcastRecord.resultExtras;
                boolean z = broadcastRecord.initialSticky;
                int i3 = broadcastRecord.userId;
                int reportedProcState = processRecord.mState.getReportedProcState();
                boolean z2 = broadcastRecord.shareIdentity;
                onewayThread.scheduleRegisteredReceiver(iIntentReceiver, intent, i2, str, bundle, false, z, true, i3, reportedProcState, z2 ? broadcastRecord.callingUid : -1, z2 ? broadcastRecord.callerPackage : null);
            } catch (RemoteException e) {
                BroadcastQueue.logw("Failed to schedule result of " + broadcastRecord + " via " + processRecord + ": " + e);
                processRecord.killLocked("Can't deliver broadcast", 13, 26, true);
            }
        }
        broadcastRecord.resultTo = null;
    }

    public final void deliveryTimeoutSoftLocked(BroadcastProcessQueue broadcastProcessQueue, int i) {
        ProcessRecord processRecord = broadcastProcessQueue.app;
        if (processRecord != null) {
            long constrain = MathUtils.constrain(processRecord.getCpuDelayTime() - broadcastProcessQueue.lastCpuDelayTime, 0L, i);
            Handler handler = this.mLocalHandler;
            handler.sendMessageDelayed(Message.obtain(handler, 3, broadcastProcessQueue), constrain);
            return;
        }
        deliveryTimeoutHardLocked(broadcastProcessQueue);
    }

    public final void deliveryTimeoutHardLocked(BroadcastProcessQueue broadcastProcessQueue) {
        finishReceiverActiveLocked(broadcastProcessQueue, 3, "deliveryTimeoutHardLocked");
    }

    @Override // com.android.server.p006am.BroadcastQueue
    public boolean finishReceiverLocked(ProcessRecord processRecord, int i, String str, Bundle bundle, boolean z, boolean z2) {
        BroadcastProcessQueue processQueue = getProcessQueue(processRecord);
        if (processQueue == null || !processQueue.isActive()) {
            BroadcastQueue.logw("Ignoring finish; no active broadcast for " + processQueue);
            return false;
        }
        BroadcastRecord active = processQueue.getActive();
        if (active.ordered) {
            active.resultCode = i;
            active.resultData = str;
            active.resultExtras = bundle;
            if (!active.isNoAbort()) {
                active.resultAbort = z;
            }
            if (active.resultAbort) {
                for (int i2 = active.terminalCount + 1; i2 < active.receivers.size(); i2++) {
                    setDeliveryState(null, null, active, i2, active.receivers.get(i2), 2, "resultAbort");
                }
            }
        }
        return finishReceiverActiveLocked(processQueue, 1, "remote app");
    }

    public final boolean shouldContinueScheduling(BroadcastProcessQueue broadcastProcessQueue) {
        return broadcastProcessQueue.isRunnable() && broadcastProcessQueue.isProcessWarm() && !(broadcastProcessQueue.getActiveCountSinceIdle() >= this.mConstants.MAX_RUNNING_ACTIVE_BROADCASTS);
    }

    public final boolean finishReceiverActiveLocked(BroadcastProcessQueue broadcastProcessQueue, int i, String str) {
        if (!broadcastProcessQueue.isActive()) {
            BroadcastQueue.logw("Ignoring finish; no active broadcast for " + broadcastProcessQueue);
            return false;
        }
        int traceBegin = BroadcastQueue.traceBegin("finishReceiver");
        ProcessRecord processRecord = broadcastProcessQueue.app;
        BroadcastRecord active = broadcastProcessQueue.getActive();
        int activeIndex = broadcastProcessQueue.getActiveIndex();
        setDeliveryState(broadcastProcessQueue, processRecord, active, activeIndex, active.receivers.get(activeIndex), i, str);
        if (i == 3) {
            active.anrCount++;
            if (processRecord != null && !processRecord.isDebugging()) {
                this.mService.appNotResponding(broadcastProcessQueue.app, TimeoutRecord.forBroadcastReceiver(active.intent));
            }
        } else {
            this.mLocalHandler.removeMessages(2, broadcastProcessQueue);
            this.mLocalHandler.removeMessages(3, broadcastProcessQueue);
        }
        checkAndRemoveWaitingFor();
        boolean shouldContinueScheduling = shouldContinueScheduling(broadcastProcessQueue);
        if (shouldContinueScheduling) {
            broadcastProcessQueue.makeActiveNextPending();
            scheduleReceiverWarmLocked(broadcastProcessQueue);
        } else {
            broadcastProcessQueue.makeActiveIdle();
            broadcastProcessQueue.traceProcessEnd();
            this.mRunning[getRunningIndexOf(broadcastProcessQueue)] = null;
            updateRunnableList(broadcastProcessQueue);
            enqueueUpdateRunningList();
            notifyStoppedRunning(broadcastProcessQueue);
        }
        BroadcastQueue.traceEnd(traceBegin);
        return shouldContinueScheduling;
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x002b  */
    /* JADX WARN: Removed duplicated region for block: B:24:0x0054  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void setDeliveryState(BroadcastProcessQueue broadcastProcessQueue, ProcessRecord processRecord, BroadcastRecord broadcastRecord, int i, Object obj, int i2, String str) {
        boolean z;
        int traceBegin = BroadcastQueue.traceBegin("setDeliveryState");
        int deliveryState = getDeliveryState(broadcastRecord, i);
        if (!BroadcastRecord.isDeliveryStateTerminal(deliveryState)) {
            broadcastRecord.setDeliveryState(i, i2, str);
            if (deliveryState == 6) {
                broadcastRecord.deferredCount--;
            } else if (i2 == 6) {
                broadcastRecord.deferredCount++;
                z = true;
                if (broadcastProcessQueue != null) {
                    if (i2 == 4) {
                        broadcastProcessQueue.traceActiveBegin();
                    } else if (deliveryState == 4 && BroadcastRecord.isDeliveryStateTerminal(i2)) {
                        broadcastProcessQueue.traceActiveEnd();
                    }
                }
                if (!BroadcastRecord.isDeliveryStateTerminal(deliveryState) && BroadcastRecord.isDeliveryStateTerminal(i2)) {
                    broadcastRecord.terminalCount++;
                    notifyFinishReceiver(broadcastProcessQueue, processRecord, broadcastRecord, i, obj);
                    z = true;
                }
                if (z) {
                    if (broadcastRecord.terminalCount + broadcastRecord.deferredCount == broadcastRecord.receivers.size()) {
                        scheduleResultTo(broadcastRecord);
                    }
                    if (broadcastRecord.ordered || broadcastRecord.prioritized) {
                        for (int i3 = 0; i3 < broadcastRecord.receivers.size(); i3++) {
                            if (!BroadcastRecord.isDeliveryStateTerminal(getDeliveryState(broadcastRecord, i3)) || i3 == i) {
                                Object obj2 = broadcastRecord.receivers.get(i3);
                                BroadcastProcessQueue processQueue = getProcessQueue(BroadcastRecord.getReceiverProcessName(obj2), BroadcastRecord.getReceiverUid(obj2));
                                if (processQueue != null) {
                                    processQueue.invalidateRunnableAt();
                                    updateRunnableList(processQueue);
                                }
                            }
                        }
                        enqueueUpdateRunningList();
                    }
                }
                BroadcastQueue.traceEnd(traceBegin);
            }
        }
        z = false;
        if (broadcastProcessQueue != null) {
        }
        if (!BroadcastRecord.isDeliveryStateTerminal(deliveryState)) {
            broadcastRecord.terminalCount++;
            notifyFinishReceiver(broadcastProcessQueue, processRecord, broadcastRecord, i, obj);
            z = true;
        }
        if (z) {
        }
        BroadcastQueue.traceEnd(traceBegin);
    }

    public final int getDeliveryState(BroadcastRecord broadcastRecord, int i) {
        return broadcastRecord.getDeliveryState(i);
    }

    @Override // com.android.server.p006am.BroadcastQueue
    public boolean cleanupDisabledPackageReceiversLocked(final String str, final Set<String> set, final int i) {
        Predicate<BroadcastProcessQueue> predicate;
        BroadcastProcessQueue.BroadcastPredicate broadcastPredicate;
        if (str != null) {
            final int packageUid = this.mService.mPackageManagerInt.getPackageUid(str, 8192L, i);
            predicate = new Predicate() { // from class: com.android.server.am.BroadcastQueueModernImpl$$ExternalSyntheticLambda15
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$cleanupDisabledPackageReceiversLocked$4;
                    lambda$cleanupDisabledPackageReceiversLocked$4 = BroadcastQueueModernImpl.lambda$cleanupDisabledPackageReceiversLocked$4(packageUid, (BroadcastProcessQueue) obj);
                    return lambda$cleanupDisabledPackageReceiversLocked$4;
                }
            };
            if (set != null) {
                broadcastPredicate = new BroadcastProcessQueue.BroadcastPredicate() { // from class: com.android.server.am.BroadcastQueueModernImpl$$ExternalSyntheticLambda16
                    @Override // com.android.server.p006am.BroadcastProcessQueue.BroadcastPredicate
                    public final boolean test(BroadcastRecord broadcastRecord, int i2) {
                        boolean lambda$cleanupDisabledPackageReceiversLocked$5;
                        lambda$cleanupDisabledPackageReceiversLocked$5 = BroadcastQueueModernImpl.lambda$cleanupDisabledPackageReceiversLocked$5(str, set, broadcastRecord, i2);
                        return lambda$cleanupDisabledPackageReceiversLocked$5;
                    }
                };
            } else {
                broadcastPredicate = new BroadcastProcessQueue.BroadcastPredicate() { // from class: com.android.server.am.BroadcastQueueModernImpl$$ExternalSyntheticLambda17
                    @Override // com.android.server.p006am.BroadcastProcessQueue.BroadcastPredicate
                    public final boolean test(BroadcastRecord broadcastRecord, int i2) {
                        boolean lambda$cleanupDisabledPackageReceiversLocked$6;
                        lambda$cleanupDisabledPackageReceiversLocked$6 = BroadcastQueueModernImpl.lambda$cleanupDisabledPackageReceiversLocked$6(str, broadcastRecord, i2);
                        return lambda$cleanupDisabledPackageReceiversLocked$6;
                    }
                };
            }
        } else {
            predicate = new Predicate() { // from class: com.android.server.am.BroadcastQueueModernImpl$$ExternalSyntheticLambda18
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$cleanupDisabledPackageReceiversLocked$7;
                    lambda$cleanupDisabledPackageReceiversLocked$7 = BroadcastQueueModernImpl.lambda$cleanupDisabledPackageReceiversLocked$7(i, (BroadcastProcessQueue) obj);
                    return lambda$cleanupDisabledPackageReceiversLocked$7;
                }
            };
            broadcastPredicate = BROADCAST_PREDICATE_ANY;
        }
        return forEachMatchingBroadcast(predicate, broadcastPredicate, this.mBroadcastConsumerSkip, true);
    }

    public static /* synthetic */ boolean lambda$cleanupDisabledPackageReceiversLocked$4(int i, BroadcastProcessQueue broadcastProcessQueue) {
        return broadcastProcessQueue.uid == i;
    }

    public static /* synthetic */ boolean lambda$cleanupDisabledPackageReceiversLocked$5(String str, Set set, BroadcastRecord broadcastRecord, int i) {
        Object obj = broadcastRecord.receivers.get(i);
        if (obj instanceof ResolveInfo) {
            ActivityInfo activityInfo = ((ResolveInfo) obj).activityInfo;
            return str.equals(activityInfo.packageName) && set.contains(activityInfo.name);
        }
        return false;
    }

    public static /* synthetic */ boolean lambda$cleanupDisabledPackageReceiversLocked$6(String str, BroadcastRecord broadcastRecord, int i) {
        return str.equals(BroadcastRecord.getReceiverPackageName(broadcastRecord.receivers.get(i)));
    }

    public static /* synthetic */ boolean lambda$cleanupDisabledPackageReceiversLocked$7(int i, BroadcastProcessQueue broadcastProcessQueue) {
        return UserHandle.getUserId(broadcastProcessQueue.uid) == i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$10(BroadcastRecord broadcastRecord, int i) {
        setDeliveryState(null, null, broadcastRecord, i, broadcastRecord.receivers.get(i), 2, "mBroadcastConsumerSkip");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$11(BroadcastRecord broadcastRecord, int i) {
        setDeliveryState(null, null, broadcastRecord, i, broadcastRecord.receivers.get(i), 2, "mBroadcastConsumerSkipAndCanceled");
        broadcastRecord.resultCode = 0;
        broadcastRecord.resultData = null;
        broadcastRecord.resultExtras = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$12(BroadcastRecord broadcastRecord, int i) {
        setDeliveryState(null, null, broadcastRecord, i, broadcastRecord.receivers.get(i), 6, "mBroadcastConsumerDefer");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$13(BroadcastRecord broadcastRecord, int i) {
        setDeliveryState(null, null, broadcastRecord, i, broadcastRecord.receivers.get(i), 0, "mBroadcastConsumerUndoDefer");
    }

    public final boolean testAllProcessQueues(Predicate<BroadcastProcessQueue> predicate, String str, PrintWriter printWriter) {
        for (int i = 0; i < this.mProcessQueues.size(); i++) {
            for (BroadcastProcessQueue valueAt = this.mProcessQueues.valueAt(i); valueAt != null; valueAt = valueAt.processNameNext) {
                if (!predicate.test(valueAt)) {
                    long uptimeMillis = SystemClock.uptimeMillis();
                    if (uptimeMillis > this.mLastTestFailureTime + 1000) {
                        this.mLastTestFailureTime = uptimeMillis;
                        BroadcastQueue.logv("Test " + str + " failed due to " + valueAt.toShortString(), printWriter);
                    }
                    return false;
                }
            }
        }
        BroadcastQueue.logv("Test " + str + " passed", printWriter);
        return true;
    }

    public final boolean forEachMatchingBroadcast(Predicate<BroadcastProcessQueue> predicate, BroadcastProcessQueue.BroadcastPredicate broadcastPredicate, BroadcastProcessQueue.BroadcastConsumer broadcastConsumer, boolean z) {
        boolean z2 = false;
        for (int size = this.mProcessQueues.size() - 1; size >= 0; size--) {
            for (BroadcastProcessQueue valueAt = this.mProcessQueues.valueAt(size); valueAt != null; valueAt = valueAt.processNameNext) {
                if (predicate.test(valueAt) && valueAt.forEachMatchingBroadcast(broadcastPredicate, broadcastConsumer, z)) {
                    updateRunnableList(valueAt);
                    z2 = true;
                }
            }
        }
        if (z2) {
            enqueueUpdateRunningList();
        }
        return z2;
    }

    public final void forEachMatchingQueue(Predicate<BroadcastProcessQueue> predicate, Consumer<BroadcastProcessQueue> consumer) {
        for (int size = this.mProcessQueues.size() - 1; size >= 0; size--) {
            for (BroadcastProcessQueue valueAt = this.mProcessQueues.valueAt(size); valueAt != null; valueAt = valueAt.processNameNext) {
                if (predicate.test(valueAt)) {
                    consumer.accept(valueAt);
                    updateRunnableList(valueAt);
                }
            }
        }
    }

    public final void updateQueueDeferred(BroadcastProcessQueue broadcastProcessQueue) {
        if (broadcastProcessQueue.isDeferredUntilActive()) {
            broadcastProcessQueue.forEachMatchingBroadcast(new BroadcastProcessQueue.BroadcastPredicate() { // from class: com.android.server.am.BroadcastQueueModernImpl$$ExternalSyntheticLambda11
                @Override // com.android.server.p006am.BroadcastProcessQueue.BroadcastPredicate
                public final boolean test(BroadcastRecord broadcastRecord, int i) {
                    boolean lambda$updateQueueDeferred$14;
                    lambda$updateQueueDeferred$14 = BroadcastQueueModernImpl.lambda$updateQueueDeferred$14(broadcastRecord, i);
                    return lambda$updateQueueDeferred$14;
                }
            }, this.mBroadcastConsumerDefer, false);
        } else if (broadcastProcessQueue.hasDeferredBroadcasts()) {
            broadcastProcessQueue.forEachMatchingBroadcast(new BroadcastProcessQueue.BroadcastPredicate() { // from class: com.android.server.am.BroadcastQueueModernImpl$$ExternalSyntheticLambda12
                @Override // com.android.server.p006am.BroadcastProcessQueue.BroadcastPredicate
                public final boolean test(BroadcastRecord broadcastRecord, int i) {
                    boolean lambda$updateQueueDeferred$15;
                    lambda$updateQueueDeferred$15 = BroadcastQueueModernImpl.lambda$updateQueueDeferred$15(broadcastRecord, i);
                    return lambda$updateQueueDeferred$15;
                }
            }, this.mBroadcastConsumerUndoDefer, false);
        }
    }

    public static /* synthetic */ boolean lambda$updateQueueDeferred$14(BroadcastRecord broadcastRecord, int i) {
        return broadcastRecord.deferUntilActive && broadcastRecord.getDeliveryState(i) == 0;
    }

    public static /* synthetic */ boolean lambda$updateQueueDeferred$15(BroadcastRecord broadcastRecord, int i) {
        return broadcastRecord.deferUntilActive && broadcastRecord.getDeliveryState(i) == 6;
    }

    @Override // com.android.server.p006am.BroadcastQueue
    public void start(ContentResolver contentResolver) {
        this.mFgConstants.startObserving(this.mHandler, contentResolver);
        this.mBgConstants.startObserving(this.mHandler, contentResolver);
        this.mService.registerUidObserver(new UidObserver() { // from class: com.android.server.am.BroadcastQueueModernImpl.1
            public void onUidCachedChanged(int i, boolean z) {
                synchronized (BroadcastQueueModernImpl.this.mService) {
                    try {
                        ActivityManagerService.boostPriorityForLockedSection();
                        for (BroadcastProcessQueue broadcastProcessQueue = (BroadcastProcessQueue) BroadcastQueueModernImpl.this.mProcessQueues.get(i); broadcastProcessQueue != null; broadcastProcessQueue = broadcastProcessQueue.processNameNext) {
                            broadcastProcessQueue.setProcessCached(z);
                            BroadcastQueueModernImpl.this.updateQueueDeferred(broadcastProcessQueue);
                            BroadcastQueueModernImpl.this.updateRunnableList(broadcastProcessQueue);
                        }
                        BroadcastQueueModernImpl.this.enqueueUpdateRunningList();
                    } catch (Throwable th) {
                        ActivityManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
                ActivityManagerService.resetPriorityAfterLockedSection();
            }
        }, 16, 0, PackageManagerShellCommandDataLoader.PACKAGE);
        checkHealthLocked();
    }

    @Override // com.android.server.p006am.BroadcastQueue
    public boolean isIdleLocked() {
        return lambda$waitForIdle$18(null);
    }

    /* renamed from: isIdleLocked */
    public boolean lambda$waitForIdle$18(PrintWriter printWriter) {
        return testAllProcessQueues(new Predicate() { // from class: com.android.server.am.BroadcastQueueModernImpl$$ExternalSyntheticLambda24
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean isIdle;
                isIdle = ((BroadcastProcessQueue) obj).isIdle();
                return isIdle;
            }
        }, "idle", printWriter);
    }

    /* renamed from: isBeyondBarrierLocked */
    public boolean lambda$waitForBarrier$19(final long j, PrintWriter printWriter) {
        return testAllProcessQueues(new Predicate() { // from class: com.android.server.am.BroadcastQueueModernImpl$$ExternalSyntheticLambda21
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean isBeyondBarrierLocked;
                isBeyondBarrierLocked = ((BroadcastProcessQueue) obj).isBeyondBarrierLocked(j);
                return isBeyondBarrierLocked;
            }
        }, "barrier", printWriter);
    }

    @Override // com.android.server.p006am.BroadcastQueue
    public void waitForIdle(final PrintWriter printWriter) {
        waitFor(new BooleanSupplier() { // from class: com.android.server.am.BroadcastQueueModernImpl$$ExternalSyntheticLambda2
            @Override // java.util.function.BooleanSupplier
            public final boolean getAsBoolean() {
                boolean lambda$waitForIdle$18;
                lambda$waitForIdle$18 = BroadcastQueueModernImpl.this.lambda$waitForIdle$18(printWriter);
                return lambda$waitForIdle$18;
            }
        });
    }

    @Override // com.android.server.p006am.BroadcastQueue
    public void waitForBarrier(final PrintWriter printWriter) {
        final long uptimeMillis = SystemClock.uptimeMillis();
        waitFor(new BooleanSupplier() { // from class: com.android.server.am.BroadcastQueueModernImpl$$ExternalSyntheticLambda0
            @Override // java.util.function.BooleanSupplier
            public final boolean getAsBoolean() {
                boolean lambda$waitForBarrier$19;
                lambda$waitForBarrier$19 = BroadcastQueueModernImpl.this.lambda$waitForBarrier$19(uptimeMillis, printWriter);
                return lambda$waitForBarrier$19;
            }
        });
    }

    public final void waitFor(BooleanSupplier booleanSupplier) {
        Predicate<BroadcastProcessQueue> predicate;
        CountDownLatch countDownLatch = new CountDownLatch(1);
        synchronized (this.mService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                this.mWaitingFor.add(Pair.create(booleanSupplier, countDownLatch));
                predicate = QUEUE_PREDICATE_ANY;
                forEachMatchingQueue(predicate, new Consumer() { // from class: com.android.server.am.BroadcastQueueModernImpl$$ExternalSyntheticLambda22
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ((BroadcastProcessQueue) obj).setPrioritizeEarliest(true);
                    }
                });
            } finally {
                ActivityManagerService.resetPriorityAfterLockedSection();
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
        enqueueUpdateRunningList();
        try {
            try {
                countDownLatch.await();
                synchronized (this.mService) {
                    try {
                        ActivityManagerService.boostPriorityForLockedSection();
                        if (this.mWaitingFor.isEmpty()) {
                            forEachMatchingQueue(predicate, new Consumer() { // from class: com.android.server.am.BroadcastQueueModernImpl$$ExternalSyntheticLambda23
                                @Override // java.util.function.Consumer
                                public final void accept(Object obj) {
                                    ((BroadcastProcessQueue) obj).setPrioritizeEarliest(false);
                                }
                            });
                        }
                    } finally {
                    }
                }
                ActivityManagerService.resetPriorityAfterLockedSection();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } catch (Throwable th) {
            synchronized (this.mService) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    if (this.mWaitingFor.isEmpty()) {
                        forEachMatchingQueue(QUEUE_PREDICATE_ANY, new Consumer() { // from class: com.android.server.am.BroadcastQueueModernImpl$$ExternalSyntheticLambda23
                            @Override // java.util.function.Consumer
                            public final void accept(Object obj) {
                                ((BroadcastProcessQueue) obj).setPrioritizeEarliest(false);
                            }
                        });
                    }
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                } finally {
                }
            }
        }
    }

    public final void checkAndRemoveWaitingFor() {
        if (this.mWaitingFor.isEmpty()) {
            return;
        }
        this.mWaitingFor.removeIf(new Predicate() { // from class: com.android.server.am.BroadcastQueueModernImpl$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$checkAndRemoveWaitingFor$22;
                lambda$checkAndRemoveWaitingFor$22 = BroadcastQueueModernImpl.lambda$checkAndRemoveWaitingFor$22((Pair) obj);
                return lambda$checkAndRemoveWaitingFor$22;
            }
        });
    }

    public static /* synthetic */ boolean lambda$checkAndRemoveWaitingFor$22(Pair pair) {
        if (((BooleanSupplier) pair.first).getAsBoolean()) {
            ((CountDownLatch) pair.second).countDown();
            return true;
        }
        return false;
    }

    @Override // com.android.server.p006am.BroadcastQueue
    public void forceDelayBroadcastDelivery(final String str, final long j) {
        synchronized (this.mService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                forEachMatchingQueue(new Predicate() { // from class: com.android.server.am.BroadcastQueueModernImpl$$ExternalSyntheticLambda19
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$forceDelayBroadcastDelivery$23;
                        lambda$forceDelayBroadcastDelivery$23 = BroadcastQueueModernImpl.lambda$forceDelayBroadcastDelivery$23(str, (BroadcastProcessQueue) obj);
                        return lambda$forceDelayBroadcastDelivery$23;
                    }
                }, new Consumer() { // from class: com.android.server.am.BroadcastQueueModernImpl$$ExternalSyntheticLambda20
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ((BroadcastProcessQueue) obj).forceDelayBroadcastDelivery(j);
                    }
                });
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
    }

    public static /* synthetic */ boolean lambda$forceDelayBroadcastDelivery$23(String str, BroadcastProcessQueue broadcastProcessQueue) {
        return str.equals(broadcastProcessQueue.getPackageName());
    }

    @Override // com.android.server.p006am.BroadcastQueue
    public String describeStateLocked() {
        return getRunningSize() + " running";
    }

    @VisibleForTesting
    public void checkHealthLocked() {
        boolean z;
        int i;
        BroadcastProcessQueue[] broadcastProcessQueueArr;
        try {
            BroadcastProcessQueue broadcastProcessQueue = this.mRunnableHead;
            BroadcastProcessQueue broadcastProcessQueue2 = null;
            while (true) {
                z = true;
                if (broadcastProcessQueue == null) {
                    break;
                }
                BroadcastQueue.checkState(broadcastProcessQueue.runnableAtPrev == broadcastProcessQueue2, "runnableAtPrev");
                BroadcastQueue.checkState(broadcastProcessQueue.isRunnable(), "isRunnable " + broadcastProcessQueue);
                if (broadcastProcessQueue2 != null) {
                    if (broadcastProcessQueue.getRunnableAt() < broadcastProcessQueue2.getRunnableAt()) {
                        z = false;
                    }
                    BroadcastQueue.checkState(z, "getRunnableAt " + broadcastProcessQueue + " vs " + broadcastProcessQueue2);
                }
                broadcastProcessQueue2 = broadcastProcessQueue;
                broadcastProcessQueue = broadcastProcessQueue.runnableAtNext;
            }
            for (BroadcastProcessQueue broadcastProcessQueue3 : this.mRunning) {
                if (broadcastProcessQueue3 != null) {
                    BroadcastQueue.checkState(broadcastProcessQueue3.isActive(), "isActive " + broadcastProcessQueue3);
                }
            }
            BroadcastProcessQueue broadcastProcessQueue4 = this.mRunningColdStart;
            if (broadcastProcessQueue4 != null) {
                if (getRunningIndexOf(broadcastProcessQueue4) < 0) {
                    z = false;
                }
                BroadcastQueue.checkState(z, "isOrphaned " + this.mRunningColdStart);
            }
            for (i = 0; i < this.mProcessQueues.size(); i++) {
                for (BroadcastProcessQueue valueAt = this.mProcessQueues.valueAt(i); valueAt != null; valueAt = valueAt.processNameNext) {
                    valueAt.checkHealthLocked();
                }
            }
            this.mLocalHandler.sendEmptyMessageDelayed(5, 60000L);
        } catch (Exception e) {
            Slog.wtf("BroadcastQueue", e);
            dumpToDropBoxLocked(e.toString());
        }
    }

    public final void updateWarmProcess(BroadcastProcessQueue broadcastProcessQueue) {
        if (broadcastProcessQueue.isProcessWarm()) {
            return;
        }
        broadcastProcessQueue.setProcess(this.mService.getProcessRecordLocked(broadcastProcessQueue.processName, broadcastProcessQueue.uid));
    }

    public final void notifyStartedRunning(BroadcastProcessQueue broadcastProcessQueue) {
        ProcessRecord processRecord = broadcastProcessQueue.app;
        if (processRecord != null) {
            processRecord.mReceivers.incrementCurReceivers();
            if (this.mService.mInternal.getRestrictionLevel(broadcastProcessQueue.uid) < 40) {
                this.mService.updateLruProcessLocked(broadcastProcessQueue.app, false, null);
            }
            this.mService.mOomAdjuster.mCachedAppOptimizer.unfreezeTemporarily(broadcastProcessQueue.app, 3);
            if (broadcastProcessQueue.runningOomAdjusted) {
                broadcastProcessQueue.app.mState.forceProcessStateUpTo(11);
                this.mService.enqueueOomAdjTargetLocked(broadcastProcessQueue.app);
            }
        }
    }

    public final void notifyStoppedRunning(BroadcastProcessQueue broadcastProcessQueue) {
        ProcessRecord processRecord = broadcastProcessQueue.app;
        if (processRecord != null) {
            processRecord.mReceivers.decrementCurReceivers();
            if (broadcastProcessQueue.runningOomAdjusted) {
                this.mService.enqueueOomAdjTargetLocked(broadcastProcessQueue.app);
            }
        }
    }

    public final void notifyScheduleRegisteredReceiver(ProcessRecord processRecord, BroadcastRecord broadcastRecord, BroadcastFilter broadcastFilter) {
        reportUsageStatsBroadcastDispatched(processRecord, broadcastRecord);
    }

    public final void notifyScheduleReceiver(ProcessRecord processRecord, BroadcastRecord broadcastRecord, ResolveInfo resolveInfo) {
        reportUsageStatsBroadcastDispatched(processRecord, broadcastRecord);
        ActivityInfo activityInfo = resolveInfo.activityInfo;
        String str = activityInfo.packageName;
        processRecord.addPackage(str, activityInfo.applicationInfo.longVersionCode, this.mService.mProcessStats);
        boolean z = broadcastRecord.intent.getComponent() != null;
        boolean equals = Objects.equals(broadcastRecord.callerPackage, str);
        if (z && !equals) {
            this.mService.mUsageStatsService.reportEvent(str, broadcastRecord.userId, 31);
        }
        this.mService.notifyPackageUse(str, 3);
        this.mService.mPackageManagerInt.setPackageStoppedState(str, false, broadcastRecord.userId);
    }

    public final void reportUsageStatsBroadcastDispatched(ProcessRecord processRecord, BroadcastRecord broadcastRecord) {
        String packageName;
        BroadcastOptions broadcastOptions = broadcastRecord.options;
        long idForResponseEvent = broadcastOptions != null ? broadcastOptions.getIdForResponseEvent() : 0L;
        if (idForResponseEvent <= 0) {
            return;
        }
        if (broadcastRecord.intent.getPackage() != null) {
            packageName = broadcastRecord.intent.getPackage();
        } else {
            packageName = broadcastRecord.intent.getComponent() != null ? broadcastRecord.intent.getComponent().getPackageName() : null;
        }
        String str = packageName;
        if (str == null) {
            return;
        }
        this.mService.mUsageStatsService.reportBroadcastDispatched(broadcastRecord.callingUid, str, UserHandle.of(broadcastRecord.userId), idForResponseEvent, SystemClock.elapsedRealtime(), this.mService.getUidStateLocked(processRecord.uid));
    }

    public final void notifyFinishReceiver(BroadcastProcessQueue broadcastProcessQueue, ProcessRecord processRecord, BroadcastRecord broadcastRecord, int i, Object obj) {
        if (broadcastRecord.wasDeliveryAttempted(i)) {
            logBroadcastDeliveryEventReported(broadcastProcessQueue, processRecord, broadcastRecord, i, obj);
        }
        if (broadcastRecord.terminalCount == broadcastRecord.receivers.size()) {
            notifyFinishBroadcast(broadcastRecord);
        }
    }

    public final void logBroadcastDeliveryEventReported(BroadcastProcessQueue broadcastProcessQueue, ProcessRecord processRecord, BroadcastRecord broadcastRecord, int i, Object obj) {
        int i2;
        int receiverUid = BroadcastRecord.getReceiverUid(obj);
        int i3 = broadcastRecord.callingUid;
        if (i3 == -1) {
            i3 = 1000;
        }
        String action = broadcastRecord.intent.getAction();
        int i4 = obj instanceof BroadcastFilter ? 1 : 2;
        if (broadcastProcessQueue == null) {
            i2 = 0;
        } else {
            i2 = broadcastProcessQueue.getActiveViaColdStart() ? 3 : 1;
        }
        long j = broadcastRecord.scheduledTime[i];
        long j2 = j - broadcastRecord.enqueueTime;
        long j3 = broadcastRecord.terminalTime[i] - j;
        if (broadcastProcessQueue != null) {
            FrameworkStatsLog.write((int) FrameworkStatsLog.BROADCAST_DELIVERY_EVENT_REPORTED, receiverUid, i3, action, i4, i2, j2, 0L, j3, broadcastProcessQueue.getActiveWasStopped() ? 2 : 1, processRecord != null ? processRecord.info.packageName : null, broadcastRecord.callerPackage);
        }
    }

    public final void notifyFinishBroadcast(BroadcastRecord broadcastRecord) {
        this.mService.notifyBroadcastFinishedLocked(broadcastRecord);
        broadcastRecord.finishTime = SystemClock.uptimeMillis();
        broadcastRecord.nextReceiver = broadcastRecord.receivers.size();
        this.mHistory.onBroadcastFinishedLocked(broadcastRecord);
        BroadcastQueueImpl.logBootCompletedBroadcastCompletionLatencyIfPossible(broadcastRecord);
        if (broadcastRecord.intent.getComponent() == null && broadcastRecord.intent.getPackage() == null && (broadcastRecord.intent.getFlags() & 1073741824) == 0) {
            int i = 0;
            int i2 = 0;
            for (int i3 = 0; i3 < broadcastRecord.receivers.size(); i3++) {
                if (broadcastRecord.receivers.get(i3) instanceof ResolveInfo) {
                    i++;
                    if (broadcastRecord.delivery[i3] == 2) {
                        i2++;
                    }
                }
            }
            this.mService.addBroadcastStatLocked(broadcastRecord.intent.getAction(), broadcastRecord.callerPackage, i, i2, SystemClock.uptimeMillis() - broadcastRecord.enqueueTime);
        }
    }

    @VisibleForTesting
    public BroadcastProcessQueue getOrCreateProcessQueue(ProcessRecord processRecord) {
        return getOrCreateProcessQueue(processRecord.processName, processRecord.info.uid);
    }

    @VisibleForTesting
    public BroadcastProcessQueue getOrCreateProcessQueue(String str, int i) {
        BroadcastProcessQueue broadcastProcessQueue = this.mProcessQueues.get(i);
        while (broadcastProcessQueue != null) {
            if (Objects.equals(broadcastProcessQueue.processName, str)) {
                return broadcastProcessQueue;
            }
            BroadcastProcessQueue broadcastProcessQueue2 = broadcastProcessQueue.processNameNext;
            if (broadcastProcessQueue2 == null) {
                break;
            }
            broadcastProcessQueue = broadcastProcessQueue2;
        }
        BroadcastProcessQueue broadcastProcessQueue3 = new BroadcastProcessQueue(this.mConstants, str, i);
        broadcastProcessQueue3.setProcess(this.mService.getProcessRecordLocked(str, i));
        if (broadcastProcessQueue == null) {
            this.mProcessQueues.put(i, broadcastProcessQueue3);
        } else {
            broadcastProcessQueue.processNameNext = broadcastProcessQueue3;
        }
        return broadcastProcessQueue3;
    }

    @VisibleForTesting
    public BroadcastProcessQueue getProcessQueue(ProcessRecord processRecord) {
        return getProcessQueue(processRecord.processName, processRecord.info.uid);
    }

    @VisibleForTesting
    public BroadcastProcessQueue getProcessQueue(String str, int i) {
        for (BroadcastProcessQueue broadcastProcessQueue = this.mProcessQueues.get(i); broadcastProcessQueue != null; broadcastProcessQueue = broadcastProcessQueue.processNameNext) {
            if (Objects.equals(broadcastProcessQueue.processName, str)) {
                return broadcastProcessQueue;
            }
        }
        return null;
    }

    @VisibleForTesting
    public BroadcastProcessQueue removeProcessQueue(ProcessRecord processRecord) {
        return removeProcessQueue(processRecord.processName, processRecord.info.uid);
    }

    @VisibleForTesting
    public BroadcastProcessQueue removeProcessQueue(String str, int i) {
        BroadcastProcessQueue broadcastProcessQueue = null;
        for (BroadcastProcessQueue broadcastProcessQueue2 = this.mProcessQueues.get(i); broadcastProcessQueue2 != null; broadcastProcessQueue2 = broadcastProcessQueue2.processNameNext) {
            if (Objects.equals(broadcastProcessQueue2.processName, str)) {
                if (broadcastProcessQueue != null) {
                    broadcastProcessQueue.processNameNext = broadcastProcessQueue2.processNameNext;
                } else {
                    BroadcastProcessQueue broadcastProcessQueue3 = broadcastProcessQueue2.processNameNext;
                    if (broadcastProcessQueue3 != null) {
                        this.mProcessQueues.put(i, broadcastProcessQueue3);
                    } else {
                        this.mProcessQueues.remove(i);
                    }
                }
                return broadcastProcessQueue2;
            }
            broadcastProcessQueue = broadcastProcessQueue2;
        }
        return null;
    }

    @Override // com.android.server.p006am.BroadcastQueue
    @NeverCompile
    public void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
        long start = protoOutputStream.start(j);
        protoOutputStream.write(1138166333441L, this.mQueueName);
        this.mHistory.dumpDebug(protoOutputStream);
        protoOutputStream.end(start);
    }

    @Override // com.android.server.p006am.BroadcastQueue
    @NeverCompile
    public boolean dumpLocked(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr, int i, boolean z, boolean z2, boolean z3, String str, boolean z4) {
        BroadcastProcessQueue[] broadcastProcessQueueArr;
        long uptimeMillis = SystemClock.uptimeMillis();
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter);
        indentingPrintWriter.increaseIndent();
        indentingPrintWriter.println();
        indentingPrintWriter.println("📋 Per-process queues:");
        indentingPrintWriter.increaseIndent();
        for (int i2 = 0; i2 < this.mProcessQueues.size(); i2++) {
            for (BroadcastProcessQueue valueAt = this.mProcessQueues.valueAt(i2); valueAt != null; valueAt = valueAt.processNameNext) {
                valueAt.dumpLocked(uptimeMillis, indentingPrintWriter);
            }
        }
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println();
        indentingPrintWriter.println("\u1f9cd Runnable:");
        indentingPrintWriter.increaseIndent();
        BroadcastProcessQueue broadcastProcessQueue = this.mRunnableHead;
        if (broadcastProcessQueue == null) {
            indentingPrintWriter.println("(none)");
        } else {
            while (broadcastProcessQueue != null) {
                TimeUtils.formatDuration(broadcastProcessQueue.getRunnableAt(), uptimeMillis, indentingPrintWriter);
                indentingPrintWriter.print(' ');
                indentingPrintWriter.print(BroadcastProcessQueue.reasonToString(broadcastProcessQueue.getRunnableAtReason()));
                indentingPrintWriter.print(' ');
                indentingPrintWriter.print(broadcastProcessQueue.toShortString());
                indentingPrintWriter.println();
                broadcastProcessQueue = broadcastProcessQueue.runnableAtNext;
            }
        }
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println();
        indentingPrintWriter.println("🏃 Running:");
        indentingPrintWriter.increaseIndent();
        for (BroadcastProcessQueue broadcastProcessQueue2 : this.mRunning) {
            if (broadcastProcessQueue2 != null && broadcastProcessQueue2 == this.mRunningColdStart) {
                indentingPrintWriter.print("\u1f976 ");
            } else {
                indentingPrintWriter.print("\u3000 ");
            }
            if (broadcastProcessQueue2 != null) {
                indentingPrintWriter.println(broadcastProcessQueue2.toShortString());
            } else {
                indentingPrintWriter.println("(none)");
            }
        }
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println();
        indentingPrintWriter.println(" Broadcasts with ignored delivery group policies:");
        indentingPrintWriter.increaseIndent();
        this.mService.dumpDeliveryGroupPolicyIgnoredActions(indentingPrintWriter);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println();
        if (z) {
            this.mConstants.dump(indentingPrintWriter);
        }
        if (z2) {
            return this.mHistory.dumpLocked(indentingPrintWriter, str, this.mQueueName, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"), z3, z4);
        }
        return z4;
    }
}
