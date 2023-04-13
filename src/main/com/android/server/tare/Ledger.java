package com.android.server.tare;

import android.util.IndentingPrintWriter;
import android.util.SparseLongArray;
import android.util.TimeUtils;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.backup.BackupManagerConstants;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes2.dex */
public class Ledger {
    @VisibleForTesting
    static final int MAX_TRANSACTION_COUNT = 50;
    @VisibleForTesting
    static final int NUM_REWARD_BUCKET_WINDOWS = 4;
    public long mCurrentBalance;
    public int mRewardBucketIndex;
    public final RewardBucket[] mRewardBuckets;
    public int mTransactionIndex;
    public final Transaction[] mTransactions;

    /* loaded from: classes2.dex */
    public static class Transaction {
        public final long ctp;
        public final long delta;
        public final long endTimeMs;
        public final int eventId;
        public final long startTimeMs;
        public final String tag;

        public Transaction(long j, long j2, int i, String str, long j3, long j4) {
            this.startTimeMs = j;
            this.endTimeMs = j2;
            this.eventId = i;
            this.tag = str;
            this.delta = j3;
            this.ctp = j4;
        }
    }

    /* loaded from: classes2.dex */
    public static class RewardBucket {
        public final SparseLongArray cumulativeDelta = new SparseLongArray();
        public long startTimeMs;

        public final void reset() {
            this.startTimeMs = 0L;
            this.cumulativeDelta.clear();
        }
    }

    public Ledger() {
        this.mCurrentBalance = 0L;
        this.mTransactions = new Transaction[50];
        this.mTransactionIndex = 0;
        this.mRewardBuckets = new RewardBucket[4];
        this.mRewardBucketIndex = 0;
    }

    public Ledger(long j, List<Transaction> list, List<RewardBucket> list2) {
        this.mTransactions = new Transaction[50];
        this.mTransactionIndex = 0;
        this.mRewardBuckets = new RewardBucket[4];
        this.mRewardBucketIndex = 0;
        this.mCurrentBalance = j;
        int size = list.size();
        for (int max = Math.max(0, size - 50); max < size; max++) {
            Transaction[] transactionArr = this.mTransactions;
            int i = this.mTransactionIndex;
            this.mTransactionIndex = i + 1;
            transactionArr[i] = list.get(max);
        }
        this.mTransactionIndex %= 50;
        int size2 = list2.size();
        if (size2 > 0) {
            this.mRewardBucketIndex = -1;
            for (int max2 = Math.max(0, size2 - 4); max2 < size2; max2++) {
                RewardBucket[] rewardBucketArr = this.mRewardBuckets;
                int i2 = this.mRewardBucketIndex + 1;
                this.mRewardBucketIndex = i2;
                rewardBucketArr[i2] = list2.get(max2);
            }
        }
    }

    public long getCurrentBalance() {
        return this.mCurrentBalance;
    }

    public Transaction getEarliestTransaction() {
        int i = 0;
        while (true) {
            Transaction[] transactionArr = this.mTransactions;
            if (i >= transactionArr.length) {
                return null;
            }
            Transaction transaction = transactionArr[(this.mTransactionIndex + i) % transactionArr.length];
            if (transaction != null) {
                return transaction;
            }
            i++;
        }
    }

    public List<RewardBucket> getRewardBuckets() {
        long currentTimeMillis = TareUtils.getCurrentTimeMillis() - BackupManagerConstants.DEFAULT_FULL_BACKUP_INTERVAL_MILLISECONDS;
        ArrayList arrayList = new ArrayList(4);
        for (int i = 1; i <= 4; i++) {
            RewardBucket rewardBucket = this.mRewardBuckets[(this.mRewardBucketIndex + i) % 4];
            if (rewardBucket != null) {
                if (currentTimeMillis <= rewardBucket.startTimeMs) {
                    arrayList.add(rewardBucket);
                } else {
                    rewardBucket.reset();
                }
            }
        }
        return arrayList;
    }

    public List<Transaction> getTransactions() {
        ArrayList arrayList = new ArrayList(50);
        for (int i = 0; i < 50; i++) {
            Transaction transaction = this.mTransactions[(this.mTransactionIndex + i) % 50];
            if (transaction != null) {
                arrayList.add(transaction);
            }
        }
        return arrayList;
    }

    public void recordTransaction(Transaction transaction) {
        Transaction[] transactionArr = this.mTransactions;
        int i = this.mTransactionIndex;
        transactionArr[i] = transaction;
        this.mCurrentBalance += transaction.delta;
        this.mTransactionIndex = (i + 1) % 50;
        if (EconomicPolicy.isReward(transaction.eventId)) {
            SparseLongArray sparseLongArray = getCurrentRewardBucket().cumulativeDelta;
            int i2 = transaction.eventId;
            sparseLongArray.put(i2, sparseLongArray.get(i2, 0L) + transaction.delta);
        }
    }

    public final RewardBucket getCurrentRewardBucket() {
        RewardBucket rewardBucket = this.mRewardBuckets[this.mRewardBucketIndex];
        long currentTimeMillis = TareUtils.getCurrentTimeMillis();
        if (rewardBucket == null) {
            RewardBucket rewardBucket2 = new RewardBucket();
            rewardBucket2.startTimeMs = currentTimeMillis;
            this.mRewardBuckets[this.mRewardBucketIndex] = rewardBucket2;
            return rewardBucket2;
        } else if (currentTimeMillis - rewardBucket.startTimeMs < 21600000) {
            return rewardBucket;
        } else {
            int i = (this.mRewardBucketIndex + 1) % 4;
            this.mRewardBucketIndex = i;
            RewardBucket rewardBucket3 = this.mRewardBuckets[i];
            if (rewardBucket3 == null) {
                rewardBucket3 = new RewardBucket();
                this.mRewardBuckets[this.mRewardBucketIndex] = rewardBucket3;
            }
            rewardBucket3.reset();
            rewardBucket3.startTimeMs = currentTimeMillis;
            return rewardBucket3;
        }
    }

    public long get24HourSum(int i, long j) {
        long j2 = j - BackupManagerConstants.DEFAULT_FULL_BACKUP_INTERVAL_MILLISECONDS;
        int i2 = 0;
        long j3 = 0;
        while (true) {
            RewardBucket[] rewardBucketArr = this.mRewardBuckets;
            if (i2 >= rewardBucketArr.length) {
                return j3;
            }
            RewardBucket rewardBucket = rewardBucketArr[i2];
            if (rewardBucket != null) {
                long j4 = rewardBucket.startTimeMs;
                if (j4 >= j2 && j4 < j) {
                    j3 += rewardBucket.cumulativeDelta.get(i, 0L);
                }
            }
            i2++;
        }
    }

    public Transaction removeOldTransactions(long j) {
        long currentTimeMillis = TareUtils.getCurrentTimeMillis() - j;
        int i = 0;
        while (true) {
            Transaction[] transactionArr = this.mTransactions;
            if (i >= transactionArr.length) {
                return null;
            }
            int length = (this.mTransactionIndex + i) % transactionArr.length;
            Transaction transaction = transactionArr[length];
            if (transaction != null) {
                if (transaction.endTimeMs > currentTimeMillis) {
                    return transaction;
                }
                transactionArr[length] = null;
            }
            i++;
        }
    }

    public void dump(IndentingPrintWriter indentingPrintWriter, int i) {
        indentingPrintWriter.print("Current balance", TareUtils.cakeToString(getCurrentBalance())).println();
        indentingPrintWriter.println();
        boolean z = false;
        for (int i2 = 0; i2 < Math.min(50, i); i2++) {
            Transaction transaction = this.mTransactions[(this.mTransactionIndex + i2) % 50];
            if (transaction != null) {
                if (!z) {
                    indentingPrintWriter.println("Transactions:");
                    indentingPrintWriter.increaseIndent();
                    z = true;
                }
                TimeUtils.dumpTime(indentingPrintWriter, transaction.startTimeMs);
                indentingPrintWriter.print("--");
                TimeUtils.dumpTime(indentingPrintWriter, transaction.endTimeMs);
                indentingPrintWriter.print(": ");
                indentingPrintWriter.print(EconomicPolicy.eventToString(transaction.eventId));
                if (transaction.tag != null) {
                    indentingPrintWriter.print("(");
                    indentingPrintWriter.print(transaction.tag);
                    indentingPrintWriter.print(")");
                }
                indentingPrintWriter.print(" --> ");
                indentingPrintWriter.print(TareUtils.cakeToString(transaction.delta));
                indentingPrintWriter.print(" (ctp=");
                indentingPrintWriter.print(TareUtils.cakeToString(transaction.ctp));
                indentingPrintWriter.println(")");
            }
        }
        if (z) {
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.println();
        }
        long currentTimeMillis = TareUtils.getCurrentTimeMillis();
        boolean z2 = false;
        for (int i3 = 0; i3 < 4; i3++) {
            RewardBucket rewardBucket = this.mRewardBuckets[((this.mRewardBucketIndex - i3) + 4) % 4];
            if (rewardBucket != null && rewardBucket.startTimeMs != 0) {
                if (!z2) {
                    indentingPrintWriter.println("Reward buckets:");
                    indentingPrintWriter.increaseIndent();
                    z2 = true;
                }
                TimeUtils.dumpTime(indentingPrintWriter, rewardBucket.startTimeMs);
                indentingPrintWriter.print(" (");
                TimeUtils.formatDuration(currentTimeMillis - rewardBucket.startTimeMs, indentingPrintWriter);
                indentingPrintWriter.println(" ago):");
                indentingPrintWriter.increaseIndent();
                for (int i4 = 0; i4 < rewardBucket.cumulativeDelta.size(); i4++) {
                    indentingPrintWriter.print(EconomicPolicy.eventToString(rewardBucket.cumulativeDelta.keyAt(i4)));
                    indentingPrintWriter.print(": ");
                    indentingPrintWriter.println(TareUtils.cakeToString(rewardBucket.cumulativeDelta.valueAt(i4)));
                }
                indentingPrintWriter.decreaseIndent();
            }
        }
        if (z2) {
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.println();
        }
    }
}
