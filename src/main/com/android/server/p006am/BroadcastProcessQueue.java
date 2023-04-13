package com.android.server.p006am;

import android.content.pm.ResolveInfo;
import android.os.SystemClock;
import android.os.Trace;
import android.os.UserHandle;
import android.util.IndentingPrintWriter;
import android.util.TimeUtils;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.os.SomeArgs;
import com.android.internal.util.Preconditions;
import dalvik.annotation.optimization.NeverCompile;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
/* renamed from: com.android.server.am.BroadcastProcessQueue */
/* loaded from: classes.dex */
public class BroadcastProcessQueue {
    public ProcessRecord app;
    public final BroadcastConstants constants;
    public long lastCpuDelayTime;
    public BroadcastRecord mActive;
    public int mActiveCountConsecutiveNormal;
    public int mActiveCountConsecutiveUrgent;
    public int mActiveCountSinceIdle;
    public int mActiveIndex;
    public boolean mActiveViaColdStart;
    public boolean mActiveWasStopped;
    public String mCachedToShortString;
    public String mCachedToString;
    public int mCountAlarm;
    public int mCountDeferred;
    public int mCountEnqueued;
    public int mCountForeground;
    public int mCountForegroundDeferred;
    public int mCountInstrumented;
    public int mCountInteractive;
    public int mCountManifest;
    public int mCountOrdered;
    public int mCountPrioritized;
    public int mCountPrioritizedDeferred;
    public int mCountResultTo;
    public long mForcedDelayedDurationMs;
    public final ArrayDeque<SomeArgs> mPending;
    public final ArrayDeque<SomeArgs> mPendingOffload;
    public final List<ArrayDeque<SomeArgs>> mPendingQueues;
    public final ArrayDeque<SomeArgs> mPendingUrgent;
    public boolean mPrioritizeEarliest;
    public boolean mProcessCached;
    public boolean mProcessInstrumented;
    public boolean mProcessPersistent;
    public long mRunnableAt;
    public boolean mRunnableAtInvalidated;
    public int mRunnableAtReason;
    public final String processName;
    public BroadcastProcessQueue processNameNext;
    public BroadcastProcessQueue runnableAtNext;
    public BroadcastProcessQueue runnableAtPrev;
    public boolean runningOomAdjusted;
    public String runningTraceTrackName;
    public final int uid;

    @FunctionalInterface
    /* renamed from: com.android.server.am.BroadcastProcessQueue$BroadcastConsumer */
    /* loaded from: classes.dex */
    public interface BroadcastConsumer {
        void accept(BroadcastRecord broadcastRecord, int i);
    }

    @FunctionalInterface
    /* renamed from: com.android.server.am.BroadcastProcessQueue$BroadcastPredicate */
    /* loaded from: classes.dex */
    public interface BroadcastPredicate {
        boolean test(BroadcastRecord broadcastRecord, int i);
    }

    public BroadcastProcessQueue(BroadcastConstants broadcastConstants, String str, int i) {
        ArrayDeque<SomeArgs> arrayDeque = new ArrayDeque<>();
        this.mPending = arrayDeque;
        ArrayDeque<SomeArgs> arrayDeque2 = new ArrayDeque<>(4);
        this.mPendingUrgent = arrayDeque2;
        ArrayDeque<SomeArgs> arrayDeque3 = new ArrayDeque<>(4);
        this.mPendingOffload = arrayDeque3;
        this.mPendingQueues = List.of(arrayDeque2, arrayDeque, arrayDeque3);
        this.mRunnableAt = Long.MAX_VALUE;
        this.mRunnableAtReason = 0;
        Objects.requireNonNull(broadcastConstants);
        this.constants = broadcastConstants;
        Objects.requireNonNull(str);
        this.processName = str;
        this.uid = i;
    }

    public final ArrayDeque<SomeArgs> getQueueForBroadcast(BroadcastRecord broadcastRecord) {
        if (broadcastRecord.isUrgent()) {
            return this.mPendingUrgent;
        }
        if (broadcastRecord.isOffload()) {
            return this.mPendingOffload;
        }
        return this.mPending;
    }

    public BroadcastRecord enqueueOrReplaceBroadcast(BroadcastRecord broadcastRecord, int i, boolean z) {
        BroadcastRecord replaceBroadcast;
        if (!broadcastRecord.isReplacePending() || (replaceBroadcast = replaceBroadcast(broadcastRecord, i, z)) == null) {
            SomeArgs obtain = SomeArgs.obtain();
            obtain.arg1 = broadcastRecord;
            obtain.argi1 = i;
            obtain.argi2 = z ? 1 : 0;
            getQueueForBroadcast(broadcastRecord).addLast(obtain);
            onBroadcastEnqueued(broadcastRecord, i, z);
            return null;
        }
        return replaceBroadcast;
    }

    public final BroadcastRecord replaceBroadcast(BroadcastRecord broadcastRecord, int i, boolean z) {
        return replaceBroadcastInQueue(getQueueForBroadcast(broadcastRecord), broadcastRecord, i, z);
    }

    public final BroadcastRecord replaceBroadcastInQueue(ArrayDeque<SomeArgs> arrayDeque, BroadcastRecord broadcastRecord, int i, boolean z) {
        Iterator<SomeArgs> descendingIterator = arrayDeque.descendingIterator();
        Object obj = broadcastRecord.receivers.get(i);
        while (descendingIterator.hasNext()) {
            SomeArgs next = descendingIterator.next();
            BroadcastRecord broadcastRecord2 = (BroadcastRecord) next.arg1;
            int i2 = next.argi1;
            boolean z2 = next.argi2 == 1;
            Object obj2 = broadcastRecord2.receivers.get(i2);
            if (broadcastRecord.callingUid == broadcastRecord2.callingUid && broadcastRecord.userId == broadcastRecord2.userId && broadcastRecord.intent.filterEquals(broadcastRecord2.intent) && BroadcastRecord.isReceiverEquals(obj, obj2) && broadcastRecord2.allReceiversPending()) {
                next.arg1 = broadcastRecord;
                next.argi1 = i;
                next.argi2 = z ? 1 : 0;
                broadcastRecord.copyEnqueueTimeFrom(broadcastRecord2);
                onBroadcastDequeued(broadcastRecord2, i2, z2);
                onBroadcastEnqueued(broadcastRecord, i, z);
                return broadcastRecord2;
            }
        }
        return null;
    }

    public boolean forEachMatchingBroadcast(BroadcastPredicate broadcastPredicate, BroadcastConsumer broadcastConsumer, boolean z) {
        return forEachMatchingBroadcastInQueue(this.mPendingOffload, broadcastPredicate, broadcastConsumer, z) | forEachMatchingBroadcastInQueue(this.mPending, broadcastPredicate, broadcastConsumer, z) | false | forEachMatchingBroadcastInQueue(this.mPendingUrgent, broadcastPredicate, broadcastConsumer, z);
    }

    public final boolean forEachMatchingBroadcastInQueue(ArrayDeque<SomeArgs> arrayDeque, BroadcastPredicate broadcastPredicate, BroadcastConsumer broadcastConsumer, boolean z) {
        Iterator<SomeArgs> it = arrayDeque.iterator();
        boolean z2 = false;
        while (it.hasNext()) {
            SomeArgs next = it.next();
            BroadcastRecord broadcastRecord = (BroadcastRecord) next.arg1;
            int i = next.argi1;
            boolean z3 = next.argi2 == 1;
            if (broadcastPredicate.test(broadcastRecord, i)) {
                broadcastConsumer.accept(broadcastRecord, i);
                if (z) {
                    next.recycle();
                    it.remove();
                    onBroadcastDequeued(broadcastRecord, i, z3);
                }
                z2 = true;
            }
        }
        return z2;
    }

    public void setProcess(ProcessRecord processRecord) {
        this.app = processRecord;
        if (processRecord != null) {
            setProcessInstrumented(processRecord.getActiveInstrumentation() != null);
            setProcessPersistent(processRecord.isPersistent());
            return;
        }
        setProcessInstrumented(false);
        setProcessPersistent(false);
    }

    public void setProcessCached(boolean z) {
        if (this.mProcessCached != z) {
            this.mProcessCached = z;
            invalidateRunnableAt();
        }
    }

    public void setProcessInstrumented(boolean z) {
        if (this.mProcessInstrumented != z) {
            this.mProcessInstrumented = z;
            invalidateRunnableAt();
        }
    }

    public void setProcessPersistent(boolean z) {
        if (this.mProcessPersistent != z) {
            this.mProcessPersistent = z;
            invalidateRunnableAt();
        }
    }

    public boolean isProcessWarm() {
        ProcessRecord processRecord = this.app;
        return (processRecord == null || processRecord.getOnewayThread() == null || this.app.isKilled()) ? false : true;
    }

    public int getPreferredSchedulingGroupLocked() {
        if (this.mCountForeground > this.mCountForegroundDeferred) {
            return 2;
        }
        BroadcastRecord broadcastRecord = this.mActive;
        if (broadcastRecord == null || !broadcastRecord.isForeground()) {
            return !isIdle() ? 0 : Integer.MIN_VALUE;
        }
        return 2;
    }

    public int getActiveCountSinceIdle() {
        return this.mActiveCountSinceIdle;
    }

    public void setActiveViaColdStart(boolean z) {
        this.mActiveViaColdStart = z;
    }

    public void setActiveWasStopped(boolean z) {
        this.mActiveWasStopped = z;
    }

    public boolean getActiveViaColdStart() {
        return this.mActiveViaColdStart;
    }

    public boolean getActiveWasStopped() {
        return this.mActiveWasStopped;
    }

    public String getPackageName() {
        ProcessRecord processRecord = this.app;
        if (processRecord == null) {
            return null;
        }
        return processRecord.getApplicationInfo().packageName;
    }

    public void makeActiveNextPending() {
        SomeArgs removeNextBroadcast = removeNextBroadcast();
        this.mActive = (BroadcastRecord) removeNextBroadcast.arg1;
        this.mActiveIndex = removeNextBroadcast.argi1;
        boolean z = removeNextBroadcast.argi2 == 1;
        this.mActiveCountSinceIdle++;
        this.mActiveViaColdStart = false;
        this.mActiveWasStopped = false;
        removeNextBroadcast.recycle();
        onBroadcastDequeued(this.mActive, this.mActiveIndex, z);
    }

    public void makeActiveIdle() {
        this.mActive = null;
        this.mActiveIndex = 0;
        this.mActiveCountSinceIdle = 0;
        this.mActiveViaColdStart = false;
        invalidateRunnableAt();
    }

    public final void onBroadcastEnqueued(BroadcastRecord broadcastRecord, int i, boolean z) {
        this.mCountEnqueued++;
        if (broadcastRecord.deferUntilActive) {
            this.mCountDeferred++;
        }
        if (broadcastRecord.isForeground()) {
            if (broadcastRecord.deferUntilActive) {
                this.mCountForegroundDeferred++;
            }
            this.mCountForeground++;
        }
        if (broadcastRecord.ordered) {
            this.mCountOrdered++;
        }
        if (broadcastRecord.alarm) {
            this.mCountAlarm++;
        }
        if (broadcastRecord.prioritized) {
            if (broadcastRecord.deferUntilActive) {
                this.mCountPrioritizedDeferred++;
            }
            this.mCountPrioritized++;
        }
        if (broadcastRecord.interactive) {
            this.mCountInteractive++;
        }
        if (broadcastRecord.resultTo != null) {
            this.mCountResultTo++;
        }
        if (broadcastRecord.callerInstrumented) {
            this.mCountInstrumented++;
        }
        if (!z && (broadcastRecord.receivers.get(i) instanceof ResolveInfo)) {
            this.mCountManifest++;
        }
        invalidateRunnableAt();
    }

    public final void onBroadcastDequeued(BroadcastRecord broadcastRecord, int i, boolean z) {
        this.mCountEnqueued--;
        if (broadcastRecord.deferUntilActive) {
            this.mCountDeferred--;
        }
        if (broadcastRecord.isForeground()) {
            if (broadcastRecord.deferUntilActive) {
                this.mCountForegroundDeferred--;
            }
            this.mCountForeground--;
        }
        if (broadcastRecord.ordered) {
            this.mCountOrdered--;
        }
        if (broadcastRecord.alarm) {
            this.mCountAlarm--;
        }
        if (broadcastRecord.prioritized) {
            if (broadcastRecord.deferUntilActive) {
                this.mCountPrioritizedDeferred--;
            }
            this.mCountPrioritized--;
        }
        if (broadcastRecord.interactive) {
            this.mCountInteractive--;
        }
        if (broadcastRecord.resultTo != null) {
            this.mCountResultTo--;
        }
        if (broadcastRecord.callerInstrumented) {
            this.mCountInstrumented--;
        }
        if (!z && (broadcastRecord.receivers.get(i) instanceof ResolveInfo)) {
            this.mCountManifest--;
        }
        invalidateRunnableAt();
    }

    public void traceProcessStartingBegin() {
        String str = this.runningTraceTrackName;
        Trace.asyncTraceForTrackBegin(64L, str, toShortString() + " starting", hashCode());
    }

    public void traceProcessRunningBegin() {
        String str = this.runningTraceTrackName;
        Trace.asyncTraceForTrackBegin(64L, str, toShortString() + " running", hashCode());
    }

    public void traceProcessEnd() {
        Trace.asyncTraceForTrackEnd(64L, this.runningTraceTrackName, hashCode());
    }

    public void traceActiveBegin() {
        String str = this.runningTraceTrackName;
        Trace.asyncTraceForTrackBegin(64L, str, this.mActive.toShortString() + " scheduled", hashCode());
    }

    public void traceActiveEnd() {
        Trace.asyncTraceForTrackEnd(64L, this.runningTraceTrackName, hashCode());
    }

    public BroadcastRecord getActive() {
        BroadcastRecord broadcastRecord = this.mActive;
        Objects.requireNonNull(broadcastRecord);
        return broadcastRecord;
    }

    public int getActiveIndex() {
        Objects.requireNonNull(this.mActive);
        return this.mActiveIndex;
    }

    public boolean isEmpty() {
        return this.mPending.isEmpty() && this.mPendingUrgent.isEmpty() && this.mPendingOffload.isEmpty();
    }

    public boolean isActive() {
        return this.mActive != null;
    }

    public void forceDelayBroadcastDelivery(long j) {
        this.mForcedDelayedDurationMs = j;
    }

    public final SomeArgs removeNextBroadcast() {
        ArrayDeque<SomeArgs> queueForNextBroadcast = queueForNextBroadcast();
        if (queueForNextBroadcast == this.mPendingUrgent) {
            this.mActiveCountConsecutiveUrgent++;
        } else if (queueForNextBroadcast == this.mPending) {
            this.mActiveCountConsecutiveUrgent = 0;
            this.mActiveCountConsecutiveNormal++;
        } else if (queueForNextBroadcast == this.mPendingOffload) {
            this.mActiveCountConsecutiveUrgent = 0;
            this.mActiveCountConsecutiveNormal = 0;
        }
        if (isQueueEmpty(queueForNextBroadcast)) {
            return null;
        }
        return queueForNextBroadcast.removeFirst();
    }

    public ArrayDeque<SomeArgs> queueForNextBroadcast() {
        return queueForNextBroadcast(this.mPendingUrgent, queueForNextBroadcast(this.mPending, this.mPendingOffload, this.mActiveCountConsecutiveNormal, this.constants.MAX_CONSECUTIVE_NORMAL_DISPATCHES), this.mActiveCountConsecutiveUrgent, this.constants.MAX_CONSECUTIVE_URGENT_DISPATCHES);
    }

    public final ArrayDeque<SomeArgs> queueForNextBroadcast(ArrayDeque<SomeArgs> arrayDeque, ArrayDeque<SomeArgs> arrayDeque2, int i, int i2) {
        if (isQueueEmpty(arrayDeque)) {
            return arrayDeque2;
        }
        if (isQueueEmpty(arrayDeque2)) {
            return arrayDeque;
        }
        SomeArgs peekFirst = arrayDeque2.peekFirst();
        BroadcastRecord broadcastRecord = (BroadcastRecord) peekFirst.arg1;
        int i3 = peekFirst.argi1;
        BroadcastRecord broadcastRecord2 = (BroadcastRecord) arrayDeque.peekFirst().arg1;
        boolean z = false;
        if ((this.mPrioritizeEarliest || i >= i2) && broadcastRecord.enqueueTime <= broadcastRecord2.enqueueTime && !blockedOnOrderedDispatch(broadcastRecord, i3)) {
            z = true;
        }
        return z ? arrayDeque2 : arrayDeque;
    }

    public static boolean isQueueEmpty(ArrayDeque<SomeArgs> arrayDeque) {
        return arrayDeque == null || arrayDeque.isEmpty();
    }

    @VisibleForTesting
    public void setPrioritizeEarliest(boolean z) {
        this.mPrioritizeEarliest = z;
    }

    public SomeArgs peekNextBroadcast() {
        ArrayDeque<SomeArgs> queueForNextBroadcast = queueForNextBroadcast();
        if (isQueueEmpty(queueForNextBroadcast)) {
            return null;
        }
        return queueForNextBroadcast.peekFirst();
    }

    @VisibleForTesting
    public BroadcastRecord peekNextBroadcastRecord() {
        ArrayDeque<SomeArgs> queueForNextBroadcast = queueForNextBroadcast();
        if (isQueueEmpty(queueForNextBroadcast)) {
            return null;
        }
        return (BroadcastRecord) queueForNextBroadcast.peekFirst().arg1;
    }

    public boolean isPendingManifest() {
        return this.mCountManifest > 0;
    }

    public boolean isPendingOrdered() {
        return this.mCountOrdered > 0;
    }

    public boolean isPendingResultTo() {
        return this.mCountResultTo > 0;
    }

    public boolean isPendingUrgent() {
        BroadcastRecord peekNextBroadcastRecord = peekNextBroadcastRecord();
        if (peekNextBroadcastRecord != null) {
            return peekNextBroadcastRecord.isUrgent();
        }
        return false;
    }

    public boolean isIdle() {
        return (!isActive() && isEmpty()) || isDeferredUntilActive();
    }

    public boolean isBeyondBarrierLocked(long j) {
        SomeArgs peekFirst = this.mPending.peekFirst();
        SomeArgs peekFirst2 = this.mPendingUrgent.peekFirst();
        SomeArgs peekFirst3 = this.mPendingOffload.peekFirst();
        BroadcastRecord broadcastRecord = this.mActive;
        return ((broadcastRecord == null || (broadcastRecord.enqueueTime > j ? 1 : (broadcastRecord.enqueueTime == j ? 0 : -1)) > 0) && (peekFirst == null || (((BroadcastRecord) peekFirst.arg1).enqueueTime > j ? 1 : (((BroadcastRecord) peekFirst.arg1).enqueueTime == j ? 0 : -1)) > 0) && (peekFirst2 == null || (((BroadcastRecord) peekFirst2.arg1).enqueueTime > j ? 1 : (((BroadcastRecord) peekFirst2.arg1).enqueueTime == j ? 0 : -1)) > 0) && (peekFirst3 == null || (((BroadcastRecord) peekFirst3.arg1).enqueueTime > j ? 1 : (((BroadcastRecord) peekFirst3.arg1).enqueueTime == j ? 0 : -1)) > 0)) || isDeferredUntilActive();
    }

    public boolean isRunnable() {
        if (this.mRunnableAtInvalidated) {
            updateRunnableAt();
        }
        return this.mRunnableAt != Long.MAX_VALUE;
    }

    public boolean isDeferredUntilActive() {
        if (this.mRunnableAtInvalidated) {
            updateRunnableAt();
        }
        return this.mRunnableAtReason == 8;
    }

    public boolean hasDeferredBroadcasts() {
        return this.mCountDeferred > 0;
    }

    public long getRunnableAt() {
        if (this.mRunnableAtInvalidated) {
            updateRunnableAt();
        }
        return this.mRunnableAt;
    }

    public int getRunnableAtReason() {
        if (this.mRunnableAtInvalidated) {
            updateRunnableAt();
        }
        return this.mRunnableAtReason;
    }

    public void invalidateRunnableAt() {
        this.mRunnableAtInvalidated = true;
    }

    public static String reasonToString(int i) {
        switch (i) {
            case 0:
                return "EMPTY";
            case 1:
                return "CACHED";
            case 2:
                return "NORMAL";
            case 3:
                return "MAX_PENDING";
            case 4:
                return "BLOCKED";
            case 5:
                return "INSTRUMENTED";
            case 6:
                return "PERSISTENT";
            case 7:
                return "FORCE_DELAYED";
            case 8:
                return "INFINITE_DEFER";
            case 9:
            default:
                return Integer.toString(i);
            case 10:
                return "CONTAINS_FOREGROUND";
            case 11:
                return "CONTAINS_ORDERED";
            case 12:
                return "CONTAINS_ALARM";
            case 13:
                return "CONTAINS_PRIORITIZED";
            case 14:
                return "CONTAINS_INTERACTIVE";
            case 15:
                return "CONTAINS_RESULT_TO";
            case 16:
                return "CONTAINS_INSTRUMENTED";
            case 17:
                return "CONTAINS_MANIFEST";
        }
    }

    public final boolean blockedOnOrderedDispatch(BroadcastRecord broadcastRecord, int i) {
        int i2;
        int i3 = broadcastRecord.blockedUntilTerminalCount[i];
        if (broadcastRecord.deferUntilActive) {
            i2 = 0;
            for (int i4 = 0; i4 < i; i4++) {
                if (broadcastRecord.deferredUntilActive[i4]) {
                    i2++;
                }
            }
        } else {
            i2 = 0;
        }
        return broadcastRecord.terminalCount + i2 < i3 && !BroadcastRecord.isDeliveryStateTerminal(broadcastRecord.getDeliveryState(i));
    }

    public final void updateRunnableAt() {
        SomeArgs peekNextBroadcast = peekNextBroadcast();
        this.mRunnableAtInvalidated = false;
        if (peekNextBroadcast != null) {
            BroadcastRecord broadcastRecord = (BroadcastRecord) peekNextBroadcast.arg1;
            int i = peekNextBroadcast.argi1;
            long j = broadcastRecord.enqueueTime;
            if (blockedOnOrderedDispatch(broadcastRecord, i)) {
                this.mRunnableAt = Long.MAX_VALUE;
                this.mRunnableAtReason = 4;
                return;
            }
            long j2 = this.mForcedDelayedDurationMs;
            if (j2 > 0) {
                this.mRunnableAt = j2 + j;
                this.mRunnableAtReason = 7;
            } else if (this.mCountForeground > this.mCountForegroundDeferred) {
                this.mRunnableAt = this.constants.DELAY_URGENT_MILLIS + j;
                this.mRunnableAtReason = 10;
            } else if (this.mCountInteractive > 0) {
                this.mRunnableAt = this.constants.DELAY_URGENT_MILLIS + j;
                this.mRunnableAtReason = 14;
            } else if (this.mCountInstrumented > 0) {
                this.mRunnableAt = this.constants.DELAY_URGENT_MILLIS + j;
                this.mRunnableAtReason = 16;
            } else if (this.mProcessInstrumented) {
                this.mRunnableAt = this.constants.DELAY_URGENT_MILLIS + j;
                this.mRunnableAtReason = 5;
            } else if (this.mCountOrdered > 0) {
                this.mRunnableAt = j;
                this.mRunnableAtReason = 11;
            } else if (this.mCountAlarm > 0) {
                this.mRunnableAt = j;
                this.mRunnableAtReason = 12;
            } else if (this.mCountPrioritized > this.mCountPrioritizedDeferred) {
                this.mRunnableAt = j;
                this.mRunnableAtReason = 13;
            } else if (this.mCountManifest > 0) {
                this.mRunnableAt = j;
                this.mRunnableAtReason = 17;
            } else if (this.mProcessPersistent) {
                this.mRunnableAt = j;
                this.mRunnableAtReason = 6;
            } else if (this.mProcessCached) {
                if (broadcastRecord.deferUntilActive) {
                    if (this.mCountDeferred == this.mCountEnqueued) {
                        this.mRunnableAt = Long.MAX_VALUE;
                        this.mRunnableAtReason = 8;
                    } else if (broadcastRecord.isForeground()) {
                        this.mRunnableAt = this.constants.DELAY_URGENT_MILLIS + j;
                        this.mRunnableAtReason = 10;
                    } else if (broadcastRecord.prioritized) {
                        this.mRunnableAt = j;
                        this.mRunnableAtReason = 13;
                    } else if (broadcastRecord.resultTo != null) {
                        this.mRunnableAt = j;
                        this.mRunnableAtReason = 15;
                    } else {
                        this.mRunnableAt = this.constants.DELAY_CACHED_MILLIS + j;
                        this.mRunnableAtReason = 1;
                    }
                } else {
                    this.mRunnableAt = this.constants.DELAY_CACHED_MILLIS + j;
                    this.mRunnableAtReason = 1;
                }
            } else if (this.mCountResultTo > 0) {
                this.mRunnableAt = j;
                this.mRunnableAtReason = 15;
            } else {
                this.mRunnableAt = this.constants.DELAY_NORMAL_MILLIS + j;
                this.mRunnableAtReason = 2;
            }
            if (this.mPending.size() + this.mPendingUrgent.size() + this.mPendingOffload.size() >= this.constants.MAX_PENDING_BROADCASTS) {
                this.mRunnableAt = Math.min(this.mRunnableAt, j);
                this.mRunnableAtReason = 3;
                return;
            }
            return;
        }
        this.mRunnableAt = Long.MAX_VALUE;
        this.mRunnableAtReason = 0;
    }

    public void checkHealthLocked() {
        if (this.mRunnableAtReason == 4) {
            SomeArgs peekNextBroadcast = peekNextBroadcast();
            Objects.requireNonNull(peekNextBroadcast, "peekNextBroadcast");
            Preconditions.checkState(SystemClock.uptimeMillis() - ((BroadcastRecord) peekNextBroadcast.arg1).enqueueTime < 600000, "waitingTime");
        }
    }

    @VisibleForTesting
    public static BroadcastProcessQueue insertIntoRunnableList(BroadcastProcessQueue broadcastProcessQueue, BroadcastProcessQueue broadcastProcessQueue2) {
        if (broadcastProcessQueue == null) {
            return broadcastProcessQueue2;
        }
        long runnableAt = broadcastProcessQueue2.getRunnableAt();
        BroadcastProcessQueue broadcastProcessQueue3 = null;
        BroadcastProcessQueue broadcastProcessQueue4 = broadcastProcessQueue;
        while (broadcastProcessQueue4 != null) {
            if (broadcastProcessQueue4.getRunnableAt() >= runnableAt) {
                broadcastProcessQueue2.runnableAtNext = broadcastProcessQueue4;
                broadcastProcessQueue2.runnableAtPrev = broadcastProcessQueue4.runnableAtPrev;
                broadcastProcessQueue4.runnableAtPrev = broadcastProcessQueue2;
                BroadcastProcessQueue broadcastProcessQueue5 = broadcastProcessQueue2.runnableAtPrev;
                if (broadcastProcessQueue5 != null) {
                    broadcastProcessQueue5.runnableAtNext = broadcastProcessQueue2;
                }
                return broadcastProcessQueue4 == broadcastProcessQueue ? broadcastProcessQueue2 : broadcastProcessQueue;
            }
            broadcastProcessQueue3 = broadcastProcessQueue4;
            broadcastProcessQueue4 = broadcastProcessQueue4.runnableAtNext;
        }
        broadcastProcessQueue2.runnableAtPrev = broadcastProcessQueue3;
        broadcastProcessQueue3.runnableAtNext = broadcastProcessQueue2;
        return broadcastProcessQueue;
    }

    @VisibleForTesting
    public static BroadcastProcessQueue removeFromRunnableList(BroadcastProcessQueue broadcastProcessQueue, BroadcastProcessQueue broadcastProcessQueue2) {
        if (broadcastProcessQueue == broadcastProcessQueue2) {
            broadcastProcessQueue = broadcastProcessQueue2.runnableAtNext;
        }
        BroadcastProcessQueue broadcastProcessQueue3 = broadcastProcessQueue2.runnableAtNext;
        if (broadcastProcessQueue3 != null) {
            broadcastProcessQueue3.runnableAtPrev = broadcastProcessQueue2.runnableAtPrev;
        }
        BroadcastProcessQueue broadcastProcessQueue4 = broadcastProcessQueue2.runnableAtPrev;
        if (broadcastProcessQueue4 != null) {
            broadcastProcessQueue4.runnableAtNext = broadcastProcessQueue3;
        }
        broadcastProcessQueue2.runnableAtNext = null;
        broadcastProcessQueue2.runnableAtPrev = null;
        return broadcastProcessQueue;
    }

    public String toString() {
        if (this.mCachedToString == null) {
            this.mCachedToString = "BroadcastProcessQueue{" + Integer.toHexString(System.identityHashCode(this)) + " " + this.processName + "/" + UserHandle.formatUid(this.uid) + "}";
        }
        return this.mCachedToString;
    }

    public String toShortString() {
        if (this.mCachedToShortString == null) {
            this.mCachedToShortString = this.processName + "/" + UserHandle.formatUid(this.uid);
        }
        return this.mCachedToShortString;
    }

    @NeverCompile
    public void dumpLocked(long j, IndentingPrintWriter indentingPrintWriter) {
        if (this.mActive == null && isEmpty()) {
            return;
        }
        indentingPrintWriter.print(toShortString());
        if (isRunnable()) {
            indentingPrintWriter.print(" runnable at ");
            TimeUtils.formatDuration(getRunnableAt(), j, indentingPrintWriter);
        } else {
            indentingPrintWriter.print(" not runnable");
        }
        indentingPrintWriter.print(" because ");
        indentingPrintWriter.print(reasonToString(this.mRunnableAtReason));
        indentingPrintWriter.println();
        indentingPrintWriter.increaseIndent();
        dumpProcessState(indentingPrintWriter);
        dumpBroadcastCounts(indentingPrintWriter);
        BroadcastRecord broadcastRecord = this.mActive;
        if (broadcastRecord != null) {
            dumpRecord("ACTIVE", j, indentingPrintWriter, broadcastRecord, this.mActiveIndex);
        }
        Iterator<SomeArgs> it = this.mPendingUrgent.iterator();
        while (it.hasNext()) {
            SomeArgs next = it.next();
            dumpRecord("URGENT", j, indentingPrintWriter, (BroadcastRecord) next.arg1, next.argi1);
        }
        Iterator<SomeArgs> it2 = this.mPending.iterator();
        while (it2.hasNext()) {
            SomeArgs next2 = it2.next();
            dumpRecord(null, j, indentingPrintWriter, (BroadcastRecord) next2.arg1, next2.argi1);
        }
        Iterator<SomeArgs> it3 = this.mPendingOffload.iterator();
        while (it3.hasNext()) {
            SomeArgs next3 = it3.next();
            dumpRecord("OFFLOAD", j, indentingPrintWriter, (BroadcastRecord) next3.arg1, next3.argi1);
        }
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println();
    }

    @NeverCompile
    public final void dumpProcessState(IndentingPrintWriter indentingPrintWriter) {
        StringBuilder sb = new StringBuilder();
        if (this.mProcessCached) {
            sb.append("CACHED");
        }
        if (this.mProcessInstrumented) {
            if (sb.length() > 0) {
                sb.append("|");
            }
            sb.append("INSTR");
        }
        if (this.mProcessPersistent) {
            if (sb.length() > 0) {
                sb.append("|");
            }
            sb.append("PER");
        }
        if (sb.length() > 0) {
            indentingPrintWriter.print("state:");
            indentingPrintWriter.println(sb);
        }
        if (this.runningOomAdjusted) {
            indentingPrintWriter.print("runningOomAdjusted:");
            indentingPrintWriter.println(this.runningOomAdjusted);
        }
    }

    @NeverCompile
    public final void dumpBroadcastCounts(IndentingPrintWriter indentingPrintWriter) {
        indentingPrintWriter.print("e:");
        indentingPrintWriter.print(this.mCountEnqueued);
        indentingPrintWriter.print(" d:");
        indentingPrintWriter.print(this.mCountDeferred);
        indentingPrintWriter.print(" f:");
        indentingPrintWriter.print(this.mCountForeground);
        indentingPrintWriter.print(" fd:");
        indentingPrintWriter.print(this.mCountForegroundDeferred);
        indentingPrintWriter.print(" o:");
        indentingPrintWriter.print(this.mCountOrdered);
        indentingPrintWriter.print(" a:");
        indentingPrintWriter.print(this.mCountAlarm);
        indentingPrintWriter.print(" p:");
        indentingPrintWriter.print(this.mCountPrioritized);
        indentingPrintWriter.print(" pd:");
        indentingPrintWriter.print(this.mCountPrioritizedDeferred);
        indentingPrintWriter.print(" int:");
        indentingPrintWriter.print(this.mCountInteractive);
        indentingPrintWriter.print(" rt:");
        indentingPrintWriter.print(this.mCountResultTo);
        indentingPrintWriter.print(" ins:");
        indentingPrintWriter.print(this.mCountInstrumented);
        indentingPrintWriter.print(" m:");
        indentingPrintWriter.print(this.mCountManifest);
        indentingPrintWriter.print(" csi:");
        indentingPrintWriter.print(this.mActiveCountSinceIdle);
        indentingPrintWriter.print(" ccu:");
        indentingPrintWriter.print(this.mActiveCountConsecutiveUrgent);
        indentingPrintWriter.print(" ccn:");
        indentingPrintWriter.print(this.mActiveCountConsecutiveNormal);
        indentingPrintWriter.println();
    }

    @NeverCompile
    public final void dumpRecord(String str, long j, IndentingPrintWriter indentingPrintWriter, BroadcastRecord broadcastRecord, int i) {
        TimeUtils.formatDuration(broadcastRecord.enqueueTime, j, indentingPrintWriter);
        indentingPrintWriter.print(' ');
        indentingPrintWriter.println(broadcastRecord.toShortString());
        indentingPrintWriter.print("    ");
        int i2 = broadcastRecord.delivery[i];
        indentingPrintWriter.print(BroadcastRecord.deliveryStateToString(i2));
        if (i2 == 4) {
            indentingPrintWriter.print(" at ");
            TimeUtils.formatDuration(broadcastRecord.scheduledTime[i], j, indentingPrintWriter);
        }
        if (str != null) {
            indentingPrintWriter.print(' ');
            indentingPrintWriter.print(str);
        }
        Object obj = broadcastRecord.receivers.get(i);
        if (obj instanceof BroadcastFilter) {
            indentingPrintWriter.print(" for registered ");
            indentingPrintWriter.print(Integer.toHexString(System.identityHashCode((BroadcastFilter) obj)));
        } else {
            indentingPrintWriter.print(" for manifest ");
            indentingPrintWriter.print(((ResolveInfo) obj).activityInfo.name);
        }
        indentingPrintWriter.println();
        int i3 = broadcastRecord.blockedUntilTerminalCount[i];
        if (i3 != -1) {
            indentingPrintWriter.print("    blocked until ");
            indentingPrintWriter.print(i3);
            indentingPrintWriter.print(", currently at ");
            indentingPrintWriter.print(broadcastRecord.terminalCount);
            indentingPrintWriter.print(" of ");
            indentingPrintWriter.println(broadcastRecord.receivers.size());
        }
    }
}
