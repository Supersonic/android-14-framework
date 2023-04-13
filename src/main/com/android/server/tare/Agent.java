package com.android.server.tare;

import android.content.Context;
import android.content.pm.UserPackage;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.ArraySet;
import android.util.IndentingPrintWriter;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArrayMap;
import android.util.SparseSetArray;
import android.util.TimeUtils;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.LocalServices;
import com.android.server.backup.BackupManagerConstants;
import com.android.server.p011pm.UserManagerInternal;
import com.android.server.tare.EconomicPolicy;
import com.android.server.tare.EconomyManagerInternal;
import com.android.server.tare.Ledger;
import com.android.server.usage.AppStandbyInternal;
import com.android.server.utils.AlarmQueue;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
/* loaded from: classes2.dex */
public class Agent {
    public static final boolean DEBUG;
    public static final String TAG;
    public final Analyst mAnalyst;
    @GuardedBy({"mLock"})
    public final BalanceThresholdAlarmQueue mBalanceThresholdAlarmQueue;
    public final InternalResourceService mIrs;
    public final Object mLock;
    public final Scribe mScribe;
    @GuardedBy({"mLock"})
    public final SparseArrayMap<String, SparseArrayMap<String, OngoingEvent>> mCurrentOngoingEvents = new SparseArrayMap<>();
    @GuardedBy({"mLock"})
    public final SparseArrayMap<String, ArraySet<ActionAffordabilityNote>> mActionAffordabilityNotes = new SparseArrayMap<>();
    @GuardedBy({"mLock"})
    public final TotalDeltaCalculator mTotalDeltaCalculator = new TotalDeltaCalculator();
    @GuardedBy({"mLock"})
    public final TrendCalculator mTrendCalculator = new TrendCalculator();
    public final OngoingEventUpdater mOngoingEventUpdater = new OngoingEventUpdater();
    public final Handler mHandler = new AgentHandler(TareHandlerThread.get().getLooper());
    public final AppStandbyInternal mAppStandbyInternal = (AppStandbyInternal) LocalServices.getService(AppStandbyInternal.class);

    static {
        String str = "TARE-" + Agent.class.getSimpleName();
        TAG = str;
        DEBUG = InternalResourceService.DEBUG || Log.isLoggable(str, 3);
    }

    public Agent(InternalResourceService internalResourceService, Scribe scribe, Analyst analyst) {
        this.mLock = internalResourceService.getLock();
        this.mIrs = internalResourceService;
        this.mScribe = scribe;
        this.mAnalyst = analyst;
        this.mBalanceThresholdAlarmQueue = new BalanceThresholdAlarmQueue(internalResourceService.getContext(), TareHandlerThread.get().getLooper());
    }

    /* loaded from: classes2.dex */
    public class TotalDeltaCalculator implements Consumer<OngoingEvent> {
        public Ledger mLedger;
        public long mNow;
        public long mNowElapsed;
        public long mTotal;

        public TotalDeltaCalculator() {
        }

        public void reset(Ledger ledger, long j, long j2) {
            this.mLedger = ledger;
            this.mNowElapsed = j;
            this.mNow = j2;
            this.mTotal = 0L;
        }

        @Override // java.util.function.Consumer
        public void accept(OngoingEvent ongoingEvent) {
            this.mTotal += Agent.this.getActualDeltaLocked(ongoingEvent, this.mLedger, this.mNowElapsed, this.mNow).price;
        }
    }

    @GuardedBy({"mLock"})
    public long getBalanceLocked(int i, String str) {
        Ledger ledgerLocked = this.mScribe.getLedgerLocked(i, str);
        long currentBalance = ledgerLocked.getCurrentBalance();
        SparseArrayMap sparseArrayMap = (SparseArrayMap) this.mCurrentOngoingEvents.get(i, str);
        if (sparseArrayMap != null) {
            this.mTotalDeltaCalculator.reset(ledgerLocked, SystemClock.elapsedRealtime(), TareUtils.getCurrentTimeMillis());
            sparseArrayMap.forEach(this.mTotalDeltaCalculator);
            return currentBalance + this.mTotalDeltaCalculator.mTotal;
        }
        return currentBalance;
    }

    @GuardedBy({"mLock"})
    public final boolean isAffordableLocked(long j, long j2, long j3) {
        return j >= j2 && this.mScribe.getRemainingConsumableCakesLocked() >= j3;
    }

    @GuardedBy({"mLock"})
    public void noteInstantaneousEventLocked(int i, String str, int i2, String str2) {
        if (this.mIrs.isSystem(i, str)) {
            return;
        }
        long currentTimeMillis = TareUtils.getCurrentTimeMillis();
        Ledger ledgerLocked = this.mScribe.getLedgerLocked(i, str);
        CompleteEconomicPolicy completeEconomicPolicyLocked = this.mIrs.getCompleteEconomicPolicyLocked();
        int eventType = EconomicPolicy.getEventType(i2);
        if (eventType == Integer.MIN_VALUE) {
            EconomicPolicy.Reward reward = completeEconomicPolicyLocked.getReward(i2);
            if (reward != null) {
                recordTransactionLocked(i, str, ledgerLocked, new Ledger.Transaction(currentTimeMillis, currentTimeMillis, i2, str2, Math.max(0L, Math.min(reward.maxDailyReward - ledgerLocked.get24HourSum(i2, currentTimeMillis), reward.instantReward)), 0L), true);
            }
        } else if (eventType == 1073741824) {
            EconomicPolicy.Cost costOfAction = completeEconomicPolicyLocked.getCostOfAction(i2, i, str);
            recordTransactionLocked(i, str, ledgerLocked, new Ledger.Transaction(currentTimeMillis, currentTimeMillis, i2, str2, -costOfAction.price, costOfAction.costToProduce), true);
        } else {
            String str3 = TAG;
            Slog.w(str3, "Unsupported event type: " + eventType);
        }
        scheduleBalanceCheckLocked(i, str);
    }

    @GuardedBy({"mLock"})
    public void noteOngoingEventLocked(int i, String str, int i2, String str2, long j) {
        noteOngoingEventLocked(i, str, i2, str2, j, true);
    }

    @GuardedBy({"mLock"})
    public final void noteOngoingEventLocked(int i, String str, int i2, String str2, long j, boolean z) {
        if (this.mIrs.isSystem(i, str)) {
            return;
        }
        SparseArrayMap sparseArrayMap = (SparseArrayMap) this.mCurrentOngoingEvents.get(i, str);
        if (sparseArrayMap == null) {
            sparseArrayMap = new SparseArrayMap();
            this.mCurrentOngoingEvents.add(i, str, sparseArrayMap);
        }
        OngoingEvent ongoingEvent = (OngoingEvent) sparseArrayMap.get(i2, str2);
        CompleteEconomicPolicy completeEconomicPolicyLocked = this.mIrs.getCompleteEconomicPolicyLocked();
        int eventType = EconomicPolicy.getEventType(i2);
        if (eventType == Integer.MIN_VALUE) {
            EconomicPolicy.Reward reward = completeEconomicPolicyLocked.getReward(i2);
            if (reward != null) {
                if (ongoingEvent == null) {
                    sparseArrayMap.add(i2, str2, new OngoingEvent(i2, str2, j, reward));
                } else {
                    ongoingEvent.refCount++;
                }
            }
        } else if (eventType == 1073741824) {
            EconomicPolicy.Cost costOfAction = completeEconomicPolicyLocked.getCostOfAction(i2, i, str);
            if (ongoingEvent == null) {
                sparseArrayMap.add(i2, str2, new OngoingEvent(i2, str2, j, costOfAction));
            } else {
                ongoingEvent.refCount++;
            }
        } else {
            Slog.w(TAG, "Unsupported event type: " + eventType);
        }
        if (z) {
            scheduleBalanceCheckLocked(i, str);
        }
    }

    @GuardedBy({"mLock"})
    public void onDeviceStateChangedLocked() {
        onPricingChangedLocked();
    }

    @GuardedBy({"mLock"})
    public void onPricingChangedLocked() {
        onAnythingChangedLocked(true);
    }

    /* JADX WARN: Removed duplicated region for block: B:21:0x0094  */
    /* JADX WARN: Removed duplicated region for block: B:32:0x009e A[SYNTHETIC] */
    @GuardedBy({"mLock"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void onAppStatesChangedLocked(int i, ArraySet<String> arraySet) {
        long j;
        int i2;
        ActionAffordabilityNote actionAffordabilityNote;
        boolean z;
        long currentTimeMillis = TareUtils.getCurrentTimeMillis();
        long elapsedRealtime = SystemClock.elapsedRealtime();
        CompleteEconomicPolicy completeEconomicPolicyLocked = this.mIrs.getCompleteEconomicPolicyLocked();
        int i3 = 0;
        while (i3 < arraySet.size()) {
            String valueAt = arraySet.valueAt(i3);
            boolean isVip = this.mIrs.isVip(i, valueAt, elapsedRealtime);
            SparseArrayMap sparseArrayMap = (SparseArrayMap) this.mCurrentOngoingEvents.get(i, valueAt);
            if (sparseArrayMap != null) {
                j = currentTimeMillis;
                this.mOngoingEventUpdater.reset(i, valueAt, currentTimeMillis, elapsedRealtime);
                sparseArrayMap.forEach(this.mOngoingEventUpdater);
                ArraySet arraySet2 = (ArraySet) this.mActionAffordabilityNotes.get(i, valueAt);
                if (arraySet2 != null) {
                    int size = arraySet2.size();
                    long currentBalance = this.mScribe.getLedgerLocked(i, valueAt).getCurrentBalance();
                    for (int i4 = 0; i4 < size; i4 = i2 + 1) {
                        ActionAffordabilityNote actionAffordabilityNote2 = (ActionAffordabilityNote) arraySet2.valueAt(i4);
                        actionAffordabilityNote2.recalculateCosts(completeEconomicPolicyLocked, i, valueAt);
                        if (isVip) {
                            i2 = i4;
                            actionAffordabilityNote = actionAffordabilityNote2;
                        } else {
                            i2 = i4;
                            actionAffordabilityNote = actionAffordabilityNote2;
                            if (!isAffordableLocked(currentBalance, actionAffordabilityNote2.getCachedModifiedPrice(), actionAffordabilityNote2.getStockLimitHonoringCtp())) {
                                z = false;
                                if (actionAffordabilityNote.isCurrentlyAffordable() == z) {
                                    ActionAffordabilityNote actionAffordabilityNote3 = actionAffordabilityNote;
                                    actionAffordabilityNote3.setNewAffordability(z);
                                    this.mIrs.postAffordabilityChanged(i, valueAt, actionAffordabilityNote3);
                                }
                            }
                        }
                        z = true;
                        if (actionAffordabilityNote.isCurrentlyAffordable() == z) {
                        }
                    }
                }
                scheduleBalanceCheckLocked(i, valueAt);
            } else {
                j = currentTimeMillis;
            }
            i3++;
            currentTimeMillis = j;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:19:0x0084  */
    /* JADX WARN: Removed duplicated region for block: B:25:0x008e A[SYNTHETIC] */
    @GuardedBy({"mLock"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void onVipStatusChangedLocked(int i, String str) {
        int i2;
        ActionAffordabilityNote actionAffordabilityNote;
        boolean z;
        long currentTimeMillis = TareUtils.getCurrentTimeMillis();
        long elapsedRealtime = SystemClock.elapsedRealtime();
        CompleteEconomicPolicy completeEconomicPolicyLocked = this.mIrs.getCompleteEconomicPolicyLocked();
        boolean isVip = this.mIrs.isVip(i, str, elapsedRealtime);
        SparseArrayMap sparseArrayMap = (SparseArrayMap) this.mCurrentOngoingEvents.get(i, str);
        if (sparseArrayMap != null) {
            this.mOngoingEventUpdater.reset(i, str, currentTimeMillis, elapsedRealtime);
            sparseArrayMap.forEach(this.mOngoingEventUpdater);
        }
        ArraySet arraySet = (ArraySet) this.mActionAffordabilityNotes.get(i, str);
        if (arraySet != null) {
            int size = arraySet.size();
            long currentBalance = this.mScribe.getLedgerLocked(i, str).getCurrentBalance();
            for (int i3 = 0; i3 < size; i3 = i2 + 1) {
                ActionAffordabilityNote actionAffordabilityNote2 = (ActionAffordabilityNote) arraySet.valueAt(i3);
                actionAffordabilityNote2.recalculateCosts(completeEconomicPolicyLocked, i, str);
                if (isVip) {
                    i2 = i3;
                    actionAffordabilityNote = actionAffordabilityNote2;
                } else {
                    i2 = i3;
                    actionAffordabilityNote = actionAffordabilityNote2;
                    if (!isAffordableLocked(currentBalance, actionAffordabilityNote2.getCachedModifiedPrice(), actionAffordabilityNote2.getStockLimitHonoringCtp())) {
                        z = false;
                        if (actionAffordabilityNote.isCurrentlyAffordable() == z) {
                            ActionAffordabilityNote actionAffordabilityNote3 = actionAffordabilityNote;
                            actionAffordabilityNote3.setNewAffordability(z);
                            this.mIrs.postAffordabilityChanged(i, str, actionAffordabilityNote3);
                        }
                    }
                }
                z = true;
                if (actionAffordabilityNote.isCurrentlyAffordable() == z) {
                }
            }
        }
        scheduleBalanceCheckLocked(i, str);
    }

    @GuardedBy({"mLock"})
    public void onVipStatusChangedLocked(SparseSetArray<String> sparseSetArray) {
        for (int size = sparseSetArray.size() - 1; size >= 0; size--) {
            int keyAt = sparseSetArray.keyAt(size);
            for (int sizeAt = sparseSetArray.sizeAt(size) - 1; sizeAt >= 0; sizeAt--) {
                onVipStatusChangedLocked(keyAt, (String) sparseSetArray.valueAt(size, sizeAt));
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:33:0x00ef  */
    /* JADX WARN: Removed duplicated region for block: B:47:0x00f9 A[SYNTHETIC] */
    @GuardedBy({"mLock"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void onAnythingChangedLocked(boolean z) {
        int i;
        ActionAffordabilityNote actionAffordabilityNote;
        ArraySet arraySet;
        int i2;
        boolean z2;
        int i3;
        long j;
        String str;
        long currentTimeMillis = TareUtils.getCurrentTimeMillis();
        long elapsedRealtime = SystemClock.elapsedRealtime();
        CompleteEconomicPolicy completeEconomicPolicyLocked = this.mIrs.getCompleteEconomicPolicyLocked();
        int i4 = 1;
        int numMaps = this.mCurrentOngoingEvents.numMaps() - 1;
        while (numMaps >= 0) {
            int keyAt = this.mCurrentOngoingEvents.keyAt(numMaps);
            int numElementsForKey = this.mCurrentOngoingEvents.numElementsForKey(keyAt) - i4;
            while (numElementsForKey >= 0) {
                String str2 = (String) this.mCurrentOngoingEvents.keyAt(numMaps, numElementsForKey);
                SparseArrayMap sparseArrayMap = (SparseArrayMap) this.mCurrentOngoingEvents.valueAt(numMaps, numElementsForKey);
                if (sparseArrayMap != null) {
                    if (z) {
                        long j2 = currentTimeMillis;
                        i3 = numElementsForKey;
                        j = currentTimeMillis;
                        str = str2;
                        this.mOngoingEventUpdater.reset(keyAt, str2, j2, elapsedRealtime);
                        sparseArrayMap.forEach(this.mOngoingEventUpdater);
                    } else {
                        i3 = numElementsForKey;
                        j = currentTimeMillis;
                        str = str2;
                    }
                    scheduleBalanceCheckLocked(keyAt, str);
                } else {
                    i3 = numElementsForKey;
                    j = currentTimeMillis;
                }
                numElementsForKey = i3 - 1;
                currentTimeMillis = j;
            }
            numMaps--;
            i4 = 1;
        }
        for (int numMaps2 = this.mActionAffordabilityNotes.numMaps() - 1; numMaps2 >= 0; numMaps2--) {
            int keyAt2 = this.mActionAffordabilityNotes.keyAt(numMaps2);
            for (int numElementsForKey2 = this.mActionAffordabilityNotes.numElementsForKey(keyAt2) - 1; numElementsForKey2 >= 0; numElementsForKey2--) {
                String str3 = (String) this.mActionAffordabilityNotes.keyAt(numMaps2, numElementsForKey2);
                ArraySet arraySet2 = (ArraySet) this.mActionAffordabilityNotes.valueAt(numMaps2, numElementsForKey2);
                if (arraySet2 != null) {
                    long balanceLocked = getBalanceLocked(keyAt2, str3);
                    boolean isVip = this.mIrs.isVip(keyAt2, str3, elapsedRealtime);
                    int i5 = 0;
                    for (int size = arraySet2.size(); i5 < size; size = i2) {
                        ActionAffordabilityNote actionAffordabilityNote2 = (ActionAffordabilityNote) arraySet2.valueAt(i5);
                        actionAffordabilityNote2.recalculateCosts(completeEconomicPolicyLocked, keyAt2, str3);
                        if (isVip) {
                            i = i5;
                            actionAffordabilityNote = actionAffordabilityNote2;
                            arraySet = arraySet2;
                            i2 = size;
                        } else {
                            i = i5;
                            actionAffordabilityNote = actionAffordabilityNote2;
                            arraySet = arraySet2;
                            i2 = size;
                            if (!isAffordableLocked(balanceLocked, actionAffordabilityNote2.getCachedModifiedPrice(), actionAffordabilityNote2.getStockLimitHonoringCtp())) {
                                z2 = false;
                                if (actionAffordabilityNote.isCurrentlyAffordable() == z2) {
                                    ActionAffordabilityNote actionAffordabilityNote3 = actionAffordabilityNote;
                                    actionAffordabilityNote3.setNewAffordability(z2);
                                    this.mIrs.postAffordabilityChanged(keyAt2, str3, actionAffordabilityNote3);
                                }
                                i5 = i + 1;
                                arraySet2 = arraySet;
                            }
                        }
                        z2 = true;
                        if (actionAffordabilityNote.isCurrentlyAffordable() == z2) {
                        }
                        i5 = i + 1;
                        arraySet2 = arraySet;
                    }
                }
            }
        }
    }

    @GuardedBy({"mLock"})
    public void stopOngoingActionLocked(int i, String str, int i2, String str2, long j, long j2) {
        stopOngoingActionLocked(i, str, i2, str2, j, j2, true, true);
    }

    @GuardedBy({"mLock"})
    public final void stopOngoingActionLocked(int i, String str, int i2, String str2, long j, long j2, boolean z, boolean z2) {
        String str3;
        if (this.mIrs.isSystem(i, str)) {
            return;
        }
        Ledger ledgerLocked = this.mScribe.getLedgerLocked(i, str);
        SparseArrayMap sparseArrayMap = (SparseArrayMap) this.mCurrentOngoingEvents.get(i, str);
        if (sparseArrayMap == null) {
            Slog.w(TAG, "No ongoing transactions for " + TareUtils.appToString(i, str));
            return;
        }
        OngoingEvent ongoingEvent = (OngoingEvent) sparseArrayMap.get(i2, str2);
        if (ongoingEvent == null) {
            String str4 = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Nonexistent ongoing transaction ");
            sb.append(EconomicPolicy.eventToString(i2));
            if (str2 == null) {
                str3 = "";
            } else {
                str3 = XmlUtils.STRING_ARRAY_SEPARATOR + str2;
            }
            sb.append(str3);
            sb.append(" for ");
            sb.append(TareUtils.appToString(i, str));
            sb.append(" ended");
            Slog.w(str4, sb.toString());
            return;
        }
        int i3 = ongoingEvent.refCount - 1;
        ongoingEvent.refCount = i3;
        if (i3 <= 0) {
            EconomicPolicy.Cost actualDeltaLocked = getActualDeltaLocked(ongoingEvent, ledgerLocked, j, j2);
            recordTransactionLocked(i, str, ledgerLocked, new Ledger.Transaction(j2 - (j - ongoingEvent.startTimeElapsed), j2, i2, str2, actualDeltaLocked.price, actualDeltaLocked.costToProduce), z2);
            sparseArrayMap.delete(i2, str2);
        }
        if (z) {
            scheduleBalanceCheckLocked(i, str);
        }
    }

    @GuardedBy({"mLock"})
    public final EconomicPolicy.Cost getActualDeltaLocked(OngoingEvent ongoingEvent, Ledger ledger, long j, long j2) {
        long j3 = (j - ongoingEvent.startTimeElapsed) / 1000;
        long deltaPerSec = ongoingEvent.getDeltaPerSec() * j3;
        if (ongoingEvent.reward == null) {
            return new EconomicPolicy.Cost(j3 * ongoingEvent.getCtpPerSec(), deltaPerSec);
        }
        return new EconomicPolicy.Cost(0L, Math.max(0L, Math.min(ongoingEvent.reward.maxDailyReward - ledger.get24HourSum(ongoingEvent.eventId, j2), deltaPerSec)));
    }

    /* JADX WARN: Removed duplicated region for block: B:42:0x0151  */
    /* JADX WARN: Removed duplicated region for block: B:50:0x015b A[SYNTHETIC] */
    @GuardedBy({"mLock"})
    @VisibleForTesting
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void recordTransactionLocked(int i, String str, Ledger ledger, Ledger.Transaction transaction, boolean z) {
        Ledger.Transaction transaction2;
        Ledger ledger2;
        ArraySet arraySet;
        int i2;
        ActionAffordabilityNote actionAffordabilityNote;
        boolean z2;
        Ledger.Transaction transaction3 = transaction;
        if (DEBUG || transaction3.delta != 0) {
            if (this.mIrs.isSystem(i, str)) {
                Slog.wtfStack(TAG, "Tried to adjust system balance for " + TareUtils.appToString(i, str));
                return;
            }
            boolean isVip = this.mIrs.isVip(i, str);
            if (isVip) {
                transaction3 = new Ledger.Transaction(transaction3.startTimeMs, transaction3.endTimeMs, transaction3.eventId, transaction3.tag, 0L, transaction3.ctp);
            }
            CompleteEconomicPolicy completeEconomicPolicyLocked = this.mIrs.getCompleteEconomicPolicyLocked();
            long currentBalance = ledger.getCurrentBalance();
            long maxSatiatedBalance = completeEconomicPolicyLocked.getMaxSatiatedBalance(i, str);
            long j = transaction3.delta;
            if (j <= 0 || j + currentBalance <= maxSatiatedBalance) {
                transaction2 = transaction3;
                ledger2 = ledger;
            } else {
                long max = Math.max(0L, maxSatiatedBalance - currentBalance);
                String str2 = TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Would result in becoming too rich. Decreasing transaction ");
                sb.append(EconomicPolicy.eventToString(transaction3.eventId));
                sb.append(transaction3.tag == null ? "" : XmlUtils.STRING_ARRAY_SEPARATOR + transaction3.tag);
                sb.append(" for ");
                sb.append(TareUtils.appToString(i, str));
                sb.append(" by ");
                sb.append(TareUtils.cakeToString(transaction3.delta - max));
                Slog.i(str2, sb.toString());
                ledger2 = ledger;
                transaction2 = new Ledger.Transaction(transaction3.startTimeMs, transaction3.endTimeMs, transaction3.eventId, transaction3.tag, max, transaction3.ctp);
            }
            ledger2.recordTransaction(transaction2);
            this.mScribe.adjustRemainingConsumableCakesLocked(-transaction2.ctp);
            this.mAnalyst.noteTransaction(transaction2);
            if (transaction2.delta != 0 && z && (arraySet = (ArraySet) this.mActionAffordabilityNotes.get(i, str)) != null) {
                long currentBalance2 = ledger.getCurrentBalance();
                for (int i3 = 0; i3 < arraySet.size(); i3 = i2 + 1) {
                    ActionAffordabilityNote actionAffordabilityNote2 = (ActionAffordabilityNote) arraySet.valueAt(i3);
                    if (isVip) {
                        i2 = i3;
                        actionAffordabilityNote = actionAffordabilityNote2;
                    } else {
                        i2 = i3;
                        actionAffordabilityNote = actionAffordabilityNote2;
                        if (!isAffordableLocked(currentBalance2, actionAffordabilityNote2.getCachedModifiedPrice(), actionAffordabilityNote2.getStockLimitHonoringCtp())) {
                            z2 = false;
                            if (actionAffordabilityNote.isCurrentlyAffordable() == z2) {
                                ActionAffordabilityNote actionAffordabilityNote3 = actionAffordabilityNote;
                                actionAffordabilityNote3.setNewAffordability(z2);
                                this.mIrs.postAffordabilityChanged(i, str, actionAffordabilityNote3);
                            }
                        }
                    }
                    z2 = true;
                    if (actionAffordabilityNote.isCurrentlyAffordable() == z2) {
                    }
                }
            }
            if (transaction2.ctp != 0) {
                this.mHandler.sendEmptyMessage(0);
                this.mIrs.maybePerformQuantitativeEasingLocked();
            }
        }
    }

    @GuardedBy({"mLock"})
    public void reclaimAllAssetsLocked(int i, String str, int i2) {
        Ledger ledgerLocked = this.mScribe.getLedgerLocked(i, str);
        long currentBalance = ledgerLocked.getCurrentBalance();
        if (currentBalance <= 0) {
            return;
        }
        if (DEBUG) {
            String str2 = TAG;
            Slog.i(str2, "Reclaiming " + TareUtils.cakeToString(currentBalance) + " from " + TareUtils.appToString(i, str) + " because of " + EconomicPolicy.eventToString(i2));
        }
        long currentTimeMillis = TareUtils.getCurrentTimeMillis();
        recordTransactionLocked(i, str, ledgerLocked, new Ledger.Transaction(currentTimeMillis, currentTimeMillis, i2, null, -currentBalance, 0L), true);
    }

    @GuardedBy({"mLock"})
    public void reclaimUnusedAssetsLocked(double d, long j, boolean z) {
        int i;
        int i2;
        int i3;
        long minBalanceLocked;
        CompleteEconomicPolicy completeEconomicPolicyLocked = this.mIrs.getCompleteEconomicPolicyLocked();
        SparseArrayMap<String, Ledger> ledgersLocked = this.mScribe.getLedgersLocked();
        long currentTimeMillis = TareUtils.getCurrentTimeMillis();
        int i4 = 0;
        while (i4 < ledgersLocked.numMaps()) {
            int keyAt = ledgersLocked.keyAt(i4);
            int i5 = 0;
            while (i5 < ledgersLocked.numElementsForKey(keyAt)) {
                Ledger ledger = (Ledger) ledgersLocked.valueAt(i4, i5);
                long currentBalance = ledger.getCurrentBalance();
                if (currentBalance > 0) {
                    String str = (String) ledgersLocked.keyAt(i4, i5);
                    if (this.mAppStandbyInternal.getTimeSinceLastUsedByUser(str, keyAt) >= j) {
                        if (!z) {
                            minBalanceLocked = completeEconomicPolicyLocked.getMinSatiatedBalance(keyAt, str);
                        } else {
                            minBalanceLocked = this.mIrs.getMinBalanceLocked(keyAt, str);
                        }
                        long j2 = (long) (currentBalance * d);
                        if (currentBalance - j2 < minBalanceLocked) {
                            j2 = currentBalance - minBalanceLocked;
                        }
                        if (j2 > 0) {
                            if (DEBUG) {
                                Slog.i(TAG, "Reclaiming unused wealth! Taking " + TareUtils.cakeToString(j2) + " from " + TareUtils.appToString(keyAt, str));
                            }
                            i = i5;
                            i2 = keyAt;
                            i3 = i4;
                            recordTransactionLocked(keyAt, str, ledger, new Ledger.Transaction(currentTimeMillis, currentTimeMillis, 2, null, -j2, 0L), true);
                            i5 = i + 1;
                            keyAt = i2;
                            i4 = i3;
                        }
                    }
                }
                i = i5;
                i2 = keyAt;
                i3 = i4;
                i5 = i + 1;
                keyAt = i2;
                i4 = i3;
            }
            i4++;
        }
    }

    @GuardedBy({"mLock"})
    public void onAppUnexemptedLocked(int i, String str) {
        long balanceLocked = getBalanceLocked(i, str);
        long minBalanceLocked = this.mIrs.getMinBalanceLocked(i, str);
        if (balanceLocked <= minBalanceLocked) {
            return;
        }
        long timeSinceLastUsedByUser = this.mAppStandbyInternal.getTimeSinceLastUsedByUser(str, i);
        long j = (long) ((balanceLocked - minBalanceLocked) * (timeSinceLastUsedByUser < BackupManagerConstants.DEFAULT_FULL_BACKUP_INTERVAL_MILLISECONDS ? 0.25d : timeSinceLastUsedByUser < 172800000 ? 0.5d : timeSinceLastUsedByUser < 259200000 ? 0.75d : 1.0d));
        if (j > 0) {
            if (DEBUG) {
                String str2 = TAG;
                Slog.i(str2, "Reclaiming bonus wealth! Taking " + j + " from " + TareUtils.appToString(i, str));
            }
            long currentTimeMillis = TareUtils.getCurrentTimeMillis();
            recordTransactionLocked(i, str, this.mScribe.getLedgerLocked(i, str), new Ledger.Transaction(currentTimeMillis, currentTimeMillis, 4, null, -j, 0L), true);
        }
    }

    @GuardedBy({"mLock"})
    public void onAppRestrictedLocked(int i, String str) {
        reclaimAllAssetsLocked(i, str, 5);
    }

    @GuardedBy({"mLock"})
    public void onAppUnrestrictedLocked(int i, String str) {
        Ledger ledgerLocked = this.mScribe.getLedgerLocked(i, str);
        if (ledgerLocked.getCurrentBalance() > 0) {
            String str2 = TAG;
            Slog.wtf(str2, "App " + str + " had credits while it was restricted");
            return;
        }
        long currentTimeMillis = TareUtils.getCurrentTimeMillis();
        recordTransactionLocked(i, str, ledgerLocked, new Ledger.Transaction(currentTimeMillis, currentTimeMillis, 6, null, this.mIrs.getMinBalanceLocked(i, str), 0L), true);
    }

    public final boolean shouldGiveCredits(InstalledPackageInfo installedPackageInfo) {
        if (installedPackageInfo.hasCode) {
            int userId = UserHandle.getUserId(installedPackageInfo.uid);
            return (this.mIrs.isSystem(userId, installedPackageInfo.packageName) || this.mIrs.isPackageRestricted(userId, installedPackageInfo.packageName)) ? false : true;
        }
        return false;
    }

    public void onCreditSupplyChanged() {
        this.mHandler.sendEmptyMessage(0);
    }

    @GuardedBy({"mLock"})
    public void distributeBasicIncomeLocked(int i) {
        int i2;
        int i3;
        int i4;
        SparseArrayMap<String, InstalledPackageInfo> installedPackages = this.mIrs.getInstalledPackages();
        long currentTimeMillis = TareUtils.getCurrentTimeMillis();
        int numMaps = installedPackages.numMaps() - 1;
        while (numMaps >= 0) {
            int keyAt = installedPackages.keyAt(numMaps);
            int numElementsForKeyAt = installedPackages.numElementsForKeyAt(numMaps) - 1;
            while (numElementsForKeyAt >= 0) {
                InstalledPackageInfo installedPackageInfo = (InstalledPackageInfo) installedPackages.valueAt(numMaps, numElementsForKeyAt);
                if (shouldGiveCredits(installedPackageInfo)) {
                    String str = installedPackageInfo.packageName;
                    Ledger ledgerLocked = this.mScribe.getLedgerLocked(keyAt, str);
                    double d = i / 100.0d;
                    long minBalanceLocked = this.mIrs.getMinBalanceLocked(keyAt, str) - ledgerLocked.getCurrentBalance();
                    if (minBalanceLocked > 0) {
                        i2 = numElementsForKeyAt;
                        i3 = keyAt;
                        i4 = numMaps;
                        recordTransactionLocked(keyAt, str, ledgerLocked, new Ledger.Transaction(currentTimeMillis, currentTimeMillis, 0, null, (long) (d * minBalanceLocked), 0L), true);
                        numElementsForKeyAt = i2 - 1;
                        keyAt = i3;
                        numMaps = i4;
                    }
                }
                i2 = numElementsForKeyAt;
                i3 = keyAt;
                i4 = numMaps;
                numElementsForKeyAt = i2 - 1;
                keyAt = i3;
                numMaps = i4;
            }
            numMaps--;
        }
    }

    @GuardedBy({"mLock"})
    public void grantBirthrightsLocked() {
        for (int i : ((UserManagerInternal) LocalServices.getService(UserManagerInternal.class)).getUserIds()) {
            grantBirthrightsLocked(i);
        }
    }

    @GuardedBy({"mLock"})
    public void grantBirthrightsLocked(int i) {
        int i2;
        List<InstalledPackageInfo> installedPackages = this.mIrs.getInstalledPackages(i);
        long currentTimeMillis = TareUtils.getCurrentTimeMillis();
        int i3 = 0;
        while (i3 < installedPackages.size()) {
            InstalledPackageInfo installedPackageInfo = installedPackages.get(i3);
            if (shouldGiveCredits(installedPackageInfo)) {
                String str = installedPackageInfo.packageName;
                Ledger ledgerLocked = this.mScribe.getLedgerLocked(i, str);
                if (ledgerLocked.getCurrentBalance() > 0) {
                    Slog.wtf(TAG, "App " + str + " had credits before economy was set up");
                } else {
                    i2 = i3;
                    recordTransactionLocked(i, str, ledgerLocked, new Ledger.Transaction(currentTimeMillis, currentTimeMillis, 1, null, this.mIrs.getMinBalanceLocked(i, str), 0L), true);
                    i3 = i2 + 1;
                }
            }
            i2 = i3;
            i3 = i2 + 1;
        }
    }

    @GuardedBy({"mLock"})
    public void grantBirthrightLocked(int i, String str) {
        Ledger ledgerLocked = this.mScribe.getLedgerLocked(i, str);
        if (ledgerLocked.getCurrentBalance() > 0) {
            String str2 = TAG;
            Slog.wtf(str2, "App " + str + " had credits as soon as it was installed");
            return;
        }
        long currentTimeMillis = TareUtils.getCurrentTimeMillis();
        recordTransactionLocked(i, str, ledgerLocked, new Ledger.Transaction(currentTimeMillis, currentTimeMillis, 1, null, this.mIrs.getMinBalanceLocked(i, str), 0L), true);
    }

    @GuardedBy({"mLock"})
    public void onAppExemptedLocked(int i, String str) {
        long minBalanceLocked = this.mIrs.getMinBalanceLocked(i, str) - getBalanceLocked(i, str);
        if (minBalanceLocked <= 0) {
            return;
        }
        Ledger ledgerLocked = this.mScribe.getLedgerLocked(i, str);
        long currentTimeMillis = TareUtils.getCurrentTimeMillis();
        recordTransactionLocked(i, str, ledgerLocked, new Ledger.Transaction(currentTimeMillis, currentTimeMillis, 3, null, minBalanceLocked, 0L), true);
    }

    @GuardedBy({"mLock"})
    public void onPackageRemovedLocked(int i, String str) {
        this.mScribe.discardLedgerLocked(i, str);
        this.mCurrentOngoingEvents.delete(i, str);
        this.mBalanceThresholdAlarmQueue.removeAlarmForKey(UserPackage.of(i, str));
    }

    @GuardedBy({"mLock"})
    public void onUserRemovedLocked(int i) {
        this.mCurrentOngoingEvents.delete(i);
        this.mBalanceThresholdAlarmQueue.removeAlarmsForUserId(i);
    }

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public static class TrendCalculator implements Consumer<OngoingEvent> {
        public long mCtpThreshold;
        public long mCurBalance;
        public long mLowerThreshold;
        public long mMaxDeltaPerSecToCtpThreshold;
        public long mMaxDeltaPerSecToLowerThreshold;
        public long mMaxDeltaPerSecToUpperThreshold;
        public long mRemainingConsumableCredits;
        public long mUpperThreshold;

        public void reset(long j, long j2, ArraySet<ActionAffordabilityNote> arraySet) {
            this.mCurBalance = j;
            this.mRemainingConsumableCredits = j2;
            this.mMaxDeltaPerSecToLowerThreshold = 0L;
            this.mMaxDeltaPerSecToUpperThreshold = 0L;
            this.mMaxDeltaPerSecToCtpThreshold = 0L;
            this.mUpperThreshold = Long.MIN_VALUE;
            this.mLowerThreshold = Long.MAX_VALUE;
            this.mCtpThreshold = 0L;
            if (arraySet != null) {
                for (int i = 0; i < arraySet.size(); i++) {
                    ActionAffordabilityNote valueAt = arraySet.valueAt(i);
                    long cachedModifiedPrice = valueAt.getCachedModifiedPrice();
                    if (cachedModifiedPrice <= this.mCurBalance) {
                        long j3 = this.mLowerThreshold;
                        if (j3 != Long.MAX_VALUE) {
                            cachedModifiedPrice = Math.max(j3, cachedModifiedPrice);
                        }
                        this.mLowerThreshold = cachedModifiedPrice;
                    } else {
                        long j4 = this.mUpperThreshold;
                        if (j4 != Long.MIN_VALUE) {
                            cachedModifiedPrice = Math.min(j4, cachedModifiedPrice);
                        }
                        this.mUpperThreshold = cachedModifiedPrice;
                    }
                    long stockLimitHonoringCtp = valueAt.getStockLimitHonoringCtp();
                    if (stockLimitHonoringCtp <= this.mRemainingConsumableCredits) {
                        this.mCtpThreshold = Math.max(this.mCtpThreshold, stockLimitHonoringCtp);
                    }
                }
            }
        }

        public long getTimeToCrossLowerThresholdMs() {
            long j = this.mMaxDeltaPerSecToLowerThreshold;
            if (j == 0 && this.mMaxDeltaPerSecToCtpThreshold == 0) {
                return -1L;
            }
            long j2 = j != 0 ? (this.mLowerThreshold - this.mCurBalance) / j : Long.MAX_VALUE;
            long j3 = this.mMaxDeltaPerSecToCtpThreshold;
            if (j3 != 0) {
                j2 = Math.min(j2, (this.mCtpThreshold - this.mRemainingConsumableCredits) / j3);
            }
            return j2 * 1000;
        }

        public long getTimeToCrossUpperThresholdMs() {
            long j = this.mMaxDeltaPerSecToUpperThreshold;
            if (j == 0) {
                return -1L;
            }
            return ((this.mUpperThreshold - this.mCurBalance) / j) * 1000;
        }

        @Override // java.util.function.Consumer
        public void accept(OngoingEvent ongoingEvent) {
            long deltaPerSec = ongoingEvent.getDeltaPerSec();
            long j = this.mCurBalance;
            if (j >= this.mLowerThreshold && deltaPerSec < 0) {
                this.mMaxDeltaPerSecToLowerThreshold += deltaPerSec;
            } else if (j < this.mUpperThreshold && deltaPerSec > 0) {
                this.mMaxDeltaPerSecToUpperThreshold += deltaPerSec;
            }
            long ctpPerSec = ongoingEvent.getCtpPerSec();
            if (this.mRemainingConsumableCredits < this.mCtpThreshold || deltaPerSec >= 0) {
                return;
            }
            this.mMaxDeltaPerSecToCtpThreshold -= ctpPerSec;
        }
    }

    @GuardedBy({"mLock"})
    public final void scheduleBalanceCheckLocked(int i, String str) {
        SparseArrayMap sparseArrayMap = (SparseArrayMap) this.mCurrentOngoingEvents.get(i, str);
        if (sparseArrayMap == null || this.mIrs.isVip(i, str)) {
            this.mBalanceThresholdAlarmQueue.removeAlarmForKey(UserPackage.of(i, str));
            return;
        }
        this.mTrendCalculator.reset(getBalanceLocked(i, str), this.mScribe.getRemainingConsumableCakesLocked(), (ArraySet) this.mActionAffordabilityNotes.get(i, str));
        sparseArrayMap.forEach(this.mTrendCalculator);
        long timeToCrossLowerThresholdMs = this.mTrendCalculator.getTimeToCrossLowerThresholdMs();
        long timeToCrossUpperThresholdMs = this.mTrendCalculator.getTimeToCrossUpperThresholdMs();
        if (timeToCrossLowerThresholdMs != -1) {
            if (timeToCrossUpperThresholdMs != -1) {
                timeToCrossLowerThresholdMs = Math.min(timeToCrossLowerThresholdMs, timeToCrossUpperThresholdMs);
            }
            timeToCrossUpperThresholdMs = timeToCrossLowerThresholdMs;
        } else if (timeToCrossUpperThresholdMs == -1) {
            this.mBalanceThresholdAlarmQueue.removeAlarmForKey(UserPackage.of(i, str));
            return;
        }
        this.mBalanceThresholdAlarmQueue.addAlarm(UserPackage.of(i, str), SystemClock.elapsedRealtime() + timeToCrossUpperThresholdMs);
    }

    @GuardedBy({"mLock"})
    public void tearDownLocked() {
        this.mCurrentOngoingEvents.clear();
        this.mBalanceThresholdAlarmQueue.removeAllAlarms();
    }

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public static class OngoingEvent {
        public final EconomicPolicy.Cost actionCost;
        public final int eventId;
        public int refCount;
        public final EconomicPolicy.Reward reward;
        public final long startTimeElapsed;
        public final String tag;

        public OngoingEvent(int i, String str, long j, EconomicPolicy.Reward reward) {
            this.startTimeElapsed = j;
            this.eventId = i;
            this.tag = str;
            this.reward = reward;
            this.actionCost = null;
            this.refCount = 1;
        }

        public OngoingEvent(int i, String str, long j, EconomicPolicy.Cost cost) {
            this.startTimeElapsed = j;
            this.eventId = i;
            this.tag = str;
            this.reward = null;
            this.actionCost = cost;
            this.refCount = 1;
        }

        public long getDeltaPerSec() {
            EconomicPolicy.Cost cost = this.actionCost;
            if (cost != null) {
                return -cost.price;
            }
            EconomicPolicy.Reward reward = this.reward;
            if (reward != null) {
                return reward.ongoingRewardPerSecond;
            }
            Slog.wtfStack(Agent.TAG, "No action or reward in ongoing event?!??!");
            return 0L;
        }

        public long getCtpPerSec() {
            EconomicPolicy.Cost cost = this.actionCost;
            if (cost != null) {
                return cost.costToProduce;
            }
            return 0L;
        }
    }

    /* loaded from: classes2.dex */
    public class OngoingEventUpdater implements Consumer<OngoingEvent> {
        public long mNow;
        public long mNowElapsed;
        public String mPkgName;
        public int mUserId;

        public OngoingEventUpdater() {
        }

        public final void reset(int i, String str, long j, long j2) {
            this.mUserId = i;
            this.mPkgName = str;
            this.mNow = j;
            this.mNowElapsed = j2;
        }

        @Override // java.util.function.Consumer
        public void accept(OngoingEvent ongoingEvent) {
            Agent.this.stopOngoingActionLocked(this.mUserId, this.mPkgName, ongoingEvent.eventId, ongoingEvent.tag, this.mNowElapsed, this.mNow, false, false);
            Agent.this.noteOngoingEventLocked(this.mUserId, this.mPkgName, ongoingEvent.eventId, ongoingEvent.tag, this.mNowElapsed, false);
        }
    }

    /* loaded from: classes2.dex */
    public class BalanceThresholdAlarmQueue extends AlarmQueue<UserPackage> {
        public BalanceThresholdAlarmQueue(Context context, Looper looper) {
            super(context, looper, "*tare.affordability_check*", "Affordability check", true, 15000L);
        }

        @Override // com.android.server.utils.AlarmQueue
        public boolean isForUser(UserPackage userPackage, int i) {
            return userPackage.userId == i;
        }

        @Override // com.android.server.utils.AlarmQueue
        public void processExpiredAlarms(ArraySet<UserPackage> arraySet) {
            for (int i = 0; i < arraySet.size(); i++) {
                UserPackage valueAt = arraySet.valueAt(i);
                Agent.this.mHandler.obtainMessage(1, valueAt.userId, 0, valueAt.packageName).sendToTarget();
            }
        }
    }

    @GuardedBy({"mLock"})
    public void registerAffordabilityChangeListenerLocked(int i, String str, EconomyManagerInternal.AffordabilityChangeListener affordabilityChangeListener, EconomyManagerInternal.ActionBill actionBill) {
        ArraySet arraySet = (ArraySet) this.mActionAffordabilityNotes.get(i, str);
        if (arraySet == null) {
            arraySet = new ArraySet();
            this.mActionAffordabilityNotes.add(i, str, arraySet);
        }
        CompleteEconomicPolicy completeEconomicPolicyLocked = this.mIrs.getCompleteEconomicPolicyLocked();
        ActionAffordabilityNote actionAffordabilityNote = new ActionAffordabilityNote(actionBill, affordabilityChangeListener, completeEconomicPolicyLocked);
        if (arraySet.add(actionAffordabilityNote)) {
            boolean z = true;
            if (this.mIrs.getEnabledMode() == 0) {
                actionAffordabilityNote.setNewAffordability(true);
                return;
            }
            boolean isVip = this.mIrs.isVip(i, str);
            actionAffordabilityNote.recalculateCosts(completeEconomicPolicyLocked, i, str);
            if (!isVip && !isAffordableLocked(getBalanceLocked(i, str), actionAffordabilityNote.getCachedModifiedPrice(), actionAffordabilityNote.getStockLimitHonoringCtp())) {
                z = false;
            }
            actionAffordabilityNote.setNewAffordability(z);
            this.mIrs.postAffordabilityChanged(i, str, actionAffordabilityNote);
            scheduleBalanceCheckLocked(i, str);
        }
    }

    @GuardedBy({"mLock"})
    public void unregisterAffordabilityChangeListenerLocked(int i, String str, EconomyManagerInternal.AffordabilityChangeListener affordabilityChangeListener, EconomyManagerInternal.ActionBill actionBill) {
        ArraySet arraySet = (ArraySet) this.mActionAffordabilityNotes.get(i, str);
        if (arraySet == null || !arraySet.remove(new ActionAffordabilityNote(actionBill, affordabilityChangeListener, this.mIrs.getCompleteEconomicPolicyLocked()))) {
            return;
        }
        scheduleBalanceCheckLocked(i, str);
    }

    /* loaded from: classes2.dex */
    public static final class ActionAffordabilityNote {
        public final EconomyManagerInternal.ActionBill mActionBill;
        public boolean mIsAffordable;
        public final EconomyManagerInternal.AffordabilityChangeListener mListener;
        public long mModifiedPrice;
        public long mStockLimitHonoringCtp;

        @VisibleForTesting
        public ActionAffordabilityNote(EconomyManagerInternal.ActionBill actionBill, EconomyManagerInternal.AffordabilityChangeListener affordabilityChangeListener, EconomicPolicy economicPolicy) {
            this.mActionBill = actionBill;
            List<EconomyManagerInternal.AnticipatedAction> anticipatedActions = actionBill.getAnticipatedActions();
            for (int i = 0; i < anticipatedActions.size(); i++) {
                EconomyManagerInternal.AnticipatedAction anticipatedAction = anticipatedActions.get(i);
                if (economicPolicy.getAction(anticipatedAction.actionId) == null) {
                    if ((anticipatedAction.actionId & 805306368) == 0) {
                        throw new IllegalArgumentException("Invalid action id: " + anticipatedAction.actionId);
                    }
                    String str = Agent.TAG;
                    Slog.w(str, "Tracking disabled policy's action? " + anticipatedAction.actionId);
                }
            }
            this.mListener = affordabilityChangeListener;
        }

        public EconomyManagerInternal.ActionBill getActionBill() {
            return this.mActionBill;
        }

        public EconomyManagerInternal.AffordabilityChangeListener getListener() {
            return this.mListener;
        }

        public final long getCachedModifiedPrice() {
            return this.mModifiedPrice;
        }

        public final long getStockLimitHonoringCtp() {
            return this.mStockLimitHonoringCtp;
        }

        @VisibleForTesting
        public void recalculateCosts(EconomicPolicy economicPolicy, int i, String str) {
            EconomicPolicy economicPolicy2 = economicPolicy;
            List<EconomyManagerInternal.AnticipatedAction> anticipatedActions = this.mActionBill.getAnticipatedActions();
            long j = 0;
            int i2 = 0;
            long j2 = 0;
            while (i2 < anticipatedActions.size()) {
                EconomyManagerInternal.AnticipatedAction anticipatedAction = anticipatedActions.get(i2);
                EconomicPolicy.Action action = economicPolicy2.getAction(anticipatedAction.actionId);
                EconomicPolicy.Cost costOfAction = economicPolicy2.getCostOfAction(anticipatedAction.actionId, i, str);
                long j3 = costOfAction.price;
                int i3 = anticipatedAction.numInstantaneousCalls;
                List<EconomyManagerInternal.AnticipatedAction> list = anticipatedActions;
                long j4 = anticipatedAction.ongoingDurationMs;
                j += (i3 * j3) + (j3 * (j4 / 1000));
                if (action.respectsStockLimit) {
                    long j5 = costOfAction.costToProduce;
                    j2 += (i3 * j5) + (j5 * (j4 / 1000));
                }
                i2++;
                economicPolicy2 = economicPolicy;
                anticipatedActions = list;
            }
            this.mModifiedPrice = j;
            this.mStockLimitHonoringCtp = j2;
        }

        public boolean isCurrentlyAffordable() {
            return this.mIsAffordable;
        }

        public final void setNewAffordability(boolean z) {
            this.mIsAffordable = z;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof ActionAffordabilityNote) {
                ActionAffordabilityNote actionAffordabilityNote = (ActionAffordabilityNote) obj;
                return this.mActionBill.equals(actionAffordabilityNote.mActionBill) && this.mListener.equals(actionAffordabilityNote.mListener);
            }
            return false;
        }

        public int hashCode() {
            return ((0 + Objects.hash(this.mListener)) * 31) + this.mActionBill.hashCode();
        }
    }

    /* loaded from: classes2.dex */
    public final class AgentHandler extends Handler {
        public AgentHandler(Looper looper) {
            super(looper);
        }

        /* JADX WARN: Removed duplicated region for block: B:26:0x0074 A[Catch: all -> 0x008c, TryCatch #1 {, blocks: (B:9:0x001b, B:11:0x0029, B:13:0x002f, B:14:0x0040, B:16:0x0046, B:18:0x004f, B:24:0x006e, B:26:0x0074, B:27:0x0082, B:28:0x0085, B:29:0x008a), top: B:44:0x001b }] */
        /* JADX WARN: Removed duplicated region for block: B:48:0x0082 A[SYNTHETIC] */
        @Override // android.os.Handler
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public void handleMessage(Message message) {
            int i;
            ActionAffordabilityNote actionAffordabilityNote;
            boolean z;
            int i2 = message.what;
            if (i2 == 0) {
                synchronized (Agent.this.mLock) {
                    removeMessages(0);
                    Agent.this.onAnythingChangedLocked(false);
                }
            } else if (i2 == 1) {
                int i3 = message.arg1;
                String str = (String) message.obj;
                synchronized (Agent.this.mLock) {
                    ArraySet arraySet = (ArraySet) Agent.this.mActionAffordabilityNotes.get(i3, str);
                    if (arraySet != null && arraySet.size() > 0) {
                        long balanceLocked = Agent.this.getBalanceLocked(i3, str);
                        boolean isVip = Agent.this.mIrs.isVip(i3, str);
                        for (int i4 = 0; i4 < arraySet.size(); i4 = i + 1) {
                            ActionAffordabilityNote actionAffordabilityNote2 = (ActionAffordabilityNote) arraySet.valueAt(i4);
                            if (isVip) {
                                i = i4;
                                actionAffordabilityNote = actionAffordabilityNote2;
                            } else {
                                i = i4;
                                actionAffordabilityNote = actionAffordabilityNote2;
                                if (!Agent.this.isAffordableLocked(balanceLocked, actionAffordabilityNote2.getCachedModifiedPrice(), actionAffordabilityNote2.getStockLimitHonoringCtp())) {
                                    z = false;
                                    if (actionAffordabilityNote.isCurrentlyAffordable() == z) {
                                        ActionAffordabilityNote actionAffordabilityNote3 = actionAffordabilityNote;
                                        actionAffordabilityNote3.setNewAffordability(z);
                                        Agent.this.mIrs.postAffordabilityChanged(i3, str, actionAffordabilityNote3);
                                    }
                                }
                            }
                            z = true;
                            if (actionAffordabilityNote.isCurrentlyAffordable() == z) {
                            }
                        }
                    }
                    Agent.this.scheduleBalanceCheckLocked(i3, str);
                }
            }
        }
    }

    @GuardedBy({"mLock"})
    public void dumpLocked(IndentingPrintWriter indentingPrintWriter) {
        this.mBalanceThresholdAlarmQueue.dump(indentingPrintWriter);
        indentingPrintWriter.println();
        indentingPrintWriter.println("Ongoing events:");
        indentingPrintWriter.increaseIndent();
        long elapsedRealtime = SystemClock.elapsedRealtime();
        int i = 1;
        int numMaps = this.mCurrentOngoingEvents.numMaps() - 1;
        boolean z = false;
        while (numMaps >= 0) {
            int keyAt = this.mCurrentOngoingEvents.keyAt(numMaps);
            int numElementsForKey = this.mCurrentOngoingEvents.numElementsForKey(keyAt) - i;
            while (numElementsForKey >= 0) {
                String str = (String) this.mCurrentOngoingEvents.keyAt(numMaps, numElementsForKey);
                SparseArrayMap sparseArrayMap = (SparseArrayMap) this.mCurrentOngoingEvents.get(keyAt, str);
                int numMaps2 = sparseArrayMap.numMaps() - i;
                int i2 = 0;
                while (numMaps2 >= 0) {
                    int numElementsForKey2 = sparseArrayMap.numElementsForKey(sparseArrayMap.keyAt(numMaps2)) - i;
                    while (numElementsForKey2 >= 0) {
                        if (i2 == 0) {
                            indentingPrintWriter.println(TareUtils.appToString(keyAt, str));
                            indentingPrintWriter.increaseIndent();
                            i2 = i;
                        }
                        OngoingEvent ongoingEvent = (OngoingEvent) sparseArrayMap.valueAt(numMaps2, numElementsForKey2);
                        indentingPrintWriter.print(EconomicPolicy.eventToString(ongoingEvent.eventId));
                        if (ongoingEvent.tag != null) {
                            indentingPrintWriter.print("(");
                            indentingPrintWriter.print(ongoingEvent.tag);
                            indentingPrintWriter.print(")");
                        }
                        indentingPrintWriter.print(" runtime=");
                        TimeUtils.formatDuration(elapsedRealtime - ongoingEvent.startTimeElapsed, indentingPrintWriter);
                        indentingPrintWriter.print(" delta/sec=");
                        indentingPrintWriter.print(TareUtils.cakeToString(ongoingEvent.getDeltaPerSec()));
                        if (ongoingEvent.getCtpPerSec() != 0) {
                            indentingPrintWriter.print(" ctp/sec=");
                            indentingPrintWriter.print(TareUtils.cakeToString(ongoingEvent.getCtpPerSec()));
                        }
                        indentingPrintWriter.print(" refCount=");
                        indentingPrintWriter.print(ongoingEvent.refCount);
                        indentingPrintWriter.println();
                        numElementsForKey2--;
                        i = 1;
                        z = true;
                    }
                    numMaps2--;
                    i = 1;
                }
                if (i2 != 0) {
                    indentingPrintWriter.decreaseIndent();
                }
                numElementsForKey--;
                i = 1;
            }
            numMaps--;
            i = 1;
        }
        if (!z) {
            indentingPrintWriter.print("N/A");
        }
        indentingPrintWriter.decreaseIndent();
    }
}
