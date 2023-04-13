package com.android.server.p006am;

import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.AlarmManagerInternal;
import com.android.server.LocalServices;
import dalvik.annotation.optimization.NeverCompile;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
/* renamed from: com.android.server.am.BroadcastDispatcher */
/* loaded from: classes.dex */
public class BroadcastDispatcher {
    public AlarmManagerInternal mAlarm;
    public final BroadcastConstants mConstants;
    public BroadcastRecord mCurrentBroadcast;
    public final Handler mHandler;
    public final Object mLock;
    public final BroadcastQueueImpl mQueue;
    public final SparseIntArray mAlarmUids = new SparseIntArray();
    public final AlarmManagerInternal.InFlightListener mAlarmListener = new AlarmManagerInternal.InFlightListener() { // from class: com.android.server.am.BroadcastDispatcher.1
        @Override // com.android.server.AlarmManagerInternal.InFlightListener
        public void broadcastAlarmPending(int i) {
            synchronized (BroadcastDispatcher.this.mLock) {
                int i2 = 0;
                BroadcastDispatcher.this.mAlarmUids.put(i, BroadcastDispatcher.this.mAlarmUids.get(i, 0) + 1);
                int size = BroadcastDispatcher.this.mDeferredBroadcasts.size();
                while (true) {
                    if (i2 >= size) {
                        break;
                    } else if (i == ((Deferrals) BroadcastDispatcher.this.mDeferredBroadcasts.get(i2)).uid) {
                        BroadcastDispatcher.this.mAlarmDeferrals.add((Deferrals) BroadcastDispatcher.this.mDeferredBroadcasts.remove(i2));
                        break;
                    } else {
                        i2++;
                    }
                }
            }
        }

        @Override // com.android.server.AlarmManagerInternal.InFlightListener
        public void broadcastAlarmComplete(int i) {
            synchronized (BroadcastDispatcher.this.mLock) {
                int i2 = 0;
                int i3 = BroadcastDispatcher.this.mAlarmUids.get(i, 0) - 1;
                if (i3 >= 0) {
                    BroadcastDispatcher.this.mAlarmUids.put(i, i3);
                } else {
                    Slog.wtf("BroadcastDispatcher", "Undercount of broadcast alarms in flight for " + i);
                    BroadcastDispatcher.this.mAlarmUids.put(i, 0);
                }
                if (i3 <= 0) {
                    int size = BroadcastDispatcher.this.mAlarmDeferrals.size();
                    while (true) {
                        if (i2 >= size) {
                            break;
                        } else if (i == ((Deferrals) BroadcastDispatcher.this.mAlarmDeferrals.get(i2)).uid) {
                            BroadcastDispatcher.insertLocked(BroadcastDispatcher.this.mDeferredBroadcasts, (Deferrals) BroadcastDispatcher.this.mAlarmDeferrals.remove(i2));
                            break;
                        } else {
                            i2++;
                        }
                    }
                }
            }
        }
    };
    public final Runnable mScheduleRunnable = new Runnable() { // from class: com.android.server.am.BroadcastDispatcher.2
        @Override // java.lang.Runnable
        public void run() {
            synchronized (BroadcastDispatcher.this.mLock) {
                BroadcastDispatcher.this.mQueue.scheduleBroadcastsLocked();
                BroadcastDispatcher.this.mRecheckScheduled = false;
            }
        }
    };
    public boolean mRecheckScheduled = false;
    public final ArrayList<BroadcastRecord> mOrderedBroadcasts = new ArrayList<>();
    public final ArrayList<Deferrals> mDeferredBroadcasts = new ArrayList<>();
    public final ArrayList<Deferrals> mAlarmDeferrals = new ArrayList<>();
    public final ArrayList<BroadcastRecord> mAlarmQueue = new ArrayList<>();
    public SparseArray<DeferredBootCompletedBroadcastPerUser> mUser2Deferred = new SparseArray<>();

    /* renamed from: com.android.server.am.BroadcastDispatcher$Deferrals */
    /* loaded from: classes.dex */
    public static class Deferrals {
        public int alarmCount;
        public final ArrayList<BroadcastRecord> broadcasts = new ArrayList<>();
        public long deferUntil;
        public long deferredAt;
        public long deferredBy;
        public final int uid;

        public Deferrals(int i, long j, long j2, int i2) {
            this.uid = i;
            this.deferredAt = j;
            this.deferredBy = j2;
            this.deferUntil = j + j2;
            this.alarmCount = i2;
        }

        public void add(BroadcastRecord broadcastRecord) {
            this.broadcasts.add(broadcastRecord);
        }

        public int size() {
            return this.broadcasts.size();
        }

        @NeverCompile
        public void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
            Iterator<BroadcastRecord> it = this.broadcasts.iterator();
            while (it.hasNext()) {
                it.next().dumpDebug(protoOutputStream, j);
            }
        }

        @NeverCompile
        public void dumpLocked(Dumper dumper) {
            Iterator<BroadcastRecord> it = this.broadcasts.iterator();
            while (it.hasNext()) {
                dumper.dump(it.next());
            }
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(128);
            sb.append("Deferrals{uid=");
            sb.append(this.uid);
            sb.append(", deferUntil=");
            sb.append(this.deferUntil);
            sb.append(", #broadcasts=");
            sb.append(this.broadcasts.size());
            sb.append("}");
            return sb.toString();
        }
    }

    /* renamed from: com.android.server.am.BroadcastDispatcher$Dumper */
    /* loaded from: classes.dex */
    public class Dumper {
        public final String mDumpPackage;
        public String mHeading;
        public String mLabel;
        public int mOrdinal;
        public final PrintWriter mPw;
        public final String mQueueName;
        public final SimpleDateFormat mSdf;
        public boolean mPrinted = false;
        public boolean mNeedSep = true;

        public Dumper(PrintWriter printWriter, String str, String str2, SimpleDateFormat simpleDateFormat) {
            this.mPw = printWriter;
            this.mQueueName = str;
            this.mDumpPackage = str2;
            this.mSdf = simpleDateFormat;
        }

        public void setHeading(String str) {
            this.mHeading = str;
            this.mPrinted = false;
        }

        public void setLabel(String str) {
            this.mLabel = "  " + str + " " + this.mQueueName + " #";
            this.mOrdinal = 0;
        }

        public boolean didPrint() {
            return this.mPrinted;
        }

        @NeverCompile
        public void dump(BroadcastRecord broadcastRecord) {
            String str = this.mDumpPackage;
            if (str == null || str.equals(broadcastRecord.callerPackage)) {
                if (!this.mPrinted) {
                    if (this.mNeedSep) {
                        this.mPw.println();
                    }
                    this.mPrinted = true;
                    this.mNeedSep = true;
                    PrintWriter printWriter = this.mPw;
                    printWriter.println("  " + this.mHeading + " [" + this.mQueueName + "]:");
                }
                PrintWriter printWriter2 = this.mPw;
                printWriter2.println(this.mLabel + this.mOrdinal + XmlUtils.STRING_ARRAY_SEPARATOR);
                this.mOrdinal = this.mOrdinal + 1;
                broadcastRecord.dump(this.mPw, "    ", this.mSdf);
            }
        }
    }

    /* renamed from: com.android.server.am.BroadcastDispatcher$DeferredBootCompletedBroadcastPerUser */
    /* loaded from: classes.dex */
    public static class DeferredBootCompletedBroadcastPerUser {
        @VisibleForTesting
        boolean mBootCompletedBroadcastReceived;
        @VisibleForTesting
        boolean mLockedBootCompletedBroadcastReceived;
        public int mUserId;
        @VisibleForTesting
        SparseBooleanArray mUidReadyForLockedBootCompletedBroadcast = new SparseBooleanArray();
        @VisibleForTesting
        SparseBooleanArray mUidReadyForBootCompletedBroadcast = new SparseBooleanArray();
        @VisibleForTesting
        SparseArray<BroadcastRecord> mDeferredLockedBootCompletedBroadcasts = new SparseArray<>();
        @VisibleForTesting
        SparseArray<BroadcastRecord> mDeferredBootCompletedBroadcasts = new SparseArray<>();

        public DeferredBootCompletedBroadcastPerUser(int i) {
            this.mUserId = i;
        }

        public void updateUidReady(int i) {
            if (!this.mLockedBootCompletedBroadcastReceived || this.mDeferredLockedBootCompletedBroadcasts.size() != 0) {
                this.mUidReadyForLockedBootCompletedBroadcast.put(i, true);
            }
            if (this.mBootCompletedBroadcastReceived && this.mDeferredBootCompletedBroadcasts.size() == 0) {
                return;
            }
            this.mUidReadyForBootCompletedBroadcast.put(i, true);
        }

        public void enqueueBootCompletedBroadcasts(String str, SparseArray<BroadcastRecord> sparseArray) {
            if ("android.intent.action.LOCKED_BOOT_COMPLETED".equals(str)) {
                enqueueBootCompletedBroadcasts(sparseArray, this.mDeferredLockedBootCompletedBroadcasts, this.mUidReadyForLockedBootCompletedBroadcast);
                this.mLockedBootCompletedBroadcastReceived = true;
            } else if ("android.intent.action.BOOT_COMPLETED".equals(str)) {
                enqueueBootCompletedBroadcasts(sparseArray, this.mDeferredBootCompletedBroadcasts, this.mUidReadyForBootCompletedBroadcast);
                this.mBootCompletedBroadcastReceived = true;
            }
        }

        public final void enqueueBootCompletedBroadcasts(SparseArray<BroadcastRecord> sparseArray, SparseArray<BroadcastRecord> sparseArray2, SparseBooleanArray sparseBooleanArray) {
            for (int size = sparseBooleanArray.size() - 1; size >= 0; size--) {
                if (sparseArray.indexOfKey(sparseBooleanArray.keyAt(size)) < 0) {
                    sparseBooleanArray.removeAt(size);
                }
            }
            int size2 = sparseArray.size();
            for (int i = 0; i < size2; i++) {
                int keyAt = sparseArray.keyAt(i);
                sparseArray2.put(keyAt, sparseArray.valueAt(i));
                if (sparseBooleanArray.indexOfKey(keyAt) < 0) {
                    sparseBooleanArray.put(keyAt, false);
                }
            }
        }

        public BroadcastRecord dequeueDeferredBootCompletedBroadcast(boolean z) {
            BroadcastRecord dequeueDeferredBootCompletedBroadcast = dequeueDeferredBootCompletedBroadcast(this.mDeferredLockedBootCompletedBroadcasts, this.mUidReadyForLockedBootCompletedBroadcast, z);
            return dequeueDeferredBootCompletedBroadcast == null ? dequeueDeferredBootCompletedBroadcast(this.mDeferredBootCompletedBroadcasts, this.mUidReadyForBootCompletedBroadcast, z) : dequeueDeferredBootCompletedBroadcast;
        }

        public final BroadcastRecord dequeueDeferredBootCompletedBroadcast(SparseArray<BroadcastRecord> sparseArray, SparseBooleanArray sparseBooleanArray, boolean z) {
            int size = sparseArray.size();
            for (int i = 0; i < size; i++) {
                int keyAt = sparseArray.keyAt(i);
                if (z || sparseBooleanArray.get(keyAt)) {
                    BroadcastRecord valueAt = sparseArray.valueAt(i);
                    sparseArray.removeAt(i);
                    if (sparseArray.size() == 0) {
                        sparseBooleanArray.clear();
                    }
                    return valueAt;
                }
            }
            return null;
        }

        public final SparseArray<BroadcastRecord> getDeferredList(String str) {
            if (str.equals("android.intent.action.LOCKED_BOOT_COMPLETED")) {
                return this.mDeferredLockedBootCompletedBroadcasts;
            }
            if (str.equals("android.intent.action.BOOT_COMPLETED")) {
                return this.mDeferredBootCompletedBroadcasts;
            }
            return null;
        }

        public final int getBootCompletedBroadcastsUidsSize(String str) {
            SparseArray<BroadcastRecord> deferredList = getDeferredList(str);
            if (deferredList != null) {
                return deferredList.size();
            }
            return 0;
        }

        public final int getBootCompletedBroadcastsReceiversSize(String str) {
            SparseArray<BroadcastRecord> deferredList = getDeferredList(str);
            if (deferredList == null) {
                return 0;
            }
            int size = deferredList.size();
            int i = 0;
            for (int i2 = 0; i2 < size; i2++) {
                i += deferredList.valueAt(i2).receivers.size();
            }
            return i;
        }

        @NeverCompile
        public void dump(Dumper dumper, String str) {
            SparseArray<BroadcastRecord> deferredList = getDeferredList(str);
            if (deferredList == null) {
                return;
            }
            int size = deferredList.size();
            for (int i = 0; i < size; i++) {
                dumper.dump(deferredList.valueAt(i));
            }
        }

        @NeverCompile
        public void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
            int size = this.mDeferredLockedBootCompletedBroadcasts.size();
            for (int i = 0; i < size; i++) {
                this.mDeferredLockedBootCompletedBroadcasts.valueAt(i).dumpDebug(protoOutputStream, j);
            }
            int size2 = this.mDeferredBootCompletedBroadcasts.size();
            for (int i2 = 0; i2 < size2; i2++) {
                this.mDeferredBootCompletedBroadcasts.valueAt(i2).dumpDebug(protoOutputStream, j);
            }
        }
    }

    public final DeferredBootCompletedBroadcastPerUser getDeferredPerUser(int i) {
        if (this.mUser2Deferred.contains(i)) {
            return this.mUser2Deferred.get(i);
        }
        DeferredBootCompletedBroadcastPerUser deferredBootCompletedBroadcastPerUser = new DeferredBootCompletedBroadcastPerUser(i);
        this.mUser2Deferred.put(i, deferredBootCompletedBroadcastPerUser);
        return deferredBootCompletedBroadcastPerUser;
    }

    public void updateUidReadyForBootCompletedBroadcastLocked(int i) {
        getDeferredPerUser(UserHandle.getUserId(i)).updateUidReady(i);
    }

    public final BroadcastRecord dequeueDeferredBootCompletedBroadcast() {
        boolean z = this.mQueue.mService.mConstants.mDeferBootCompletedBroadcast == 0;
        int size = this.mUser2Deferred.size();
        BroadcastRecord broadcastRecord = null;
        for (int i = 0; i < size; i++) {
            broadcastRecord = this.mUser2Deferred.valueAt(i).dequeueDeferredBootCompletedBroadcast(z);
            if (broadcastRecord != null) {
                break;
            }
        }
        return broadcastRecord;
    }

    public BroadcastDispatcher(BroadcastQueueImpl broadcastQueueImpl, BroadcastConstants broadcastConstants, Handler handler, Object obj) {
        this.mQueue = broadcastQueueImpl;
        this.mConstants = broadcastConstants;
        this.mHandler = handler;
        this.mLock = obj;
    }

    public void start() {
        AlarmManagerInternal alarmManagerInternal = (AlarmManagerInternal) LocalServices.getService(AlarmManagerInternal.class);
        this.mAlarm = alarmManagerInternal;
        alarmManagerInternal.registerInFlightListener(this.mAlarmListener);
    }

    public boolean isEmpty() {
        boolean z;
        synchronized (this.mLock) {
            z = isIdle() && getBootCompletedBroadcastsUidsSize("android.intent.action.LOCKED_BOOT_COMPLETED") == 0 && getBootCompletedBroadcastsUidsSize("android.intent.action.BOOT_COMPLETED") == 0;
        }
        return z;
    }

    public boolean isIdle() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mCurrentBroadcast == null && this.mOrderedBroadcasts.isEmpty() && this.mAlarmQueue.isEmpty() && isDeferralsListEmpty(this.mDeferredBroadcasts) && isDeferralsListEmpty(this.mAlarmDeferrals);
        }
        return z;
    }

    public static boolean isDeferralsBeyondBarrier(ArrayList<Deferrals> arrayList, long j) {
        for (int i = 0; i < arrayList.size(); i++) {
            if (!isBeyondBarrier(arrayList.get(i).broadcasts, j)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isBeyondBarrier(ArrayList<BroadcastRecord> arrayList, long j) {
        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).enqueueTime <= j) {
                return false;
            }
        }
        return true;
    }

    public boolean isBeyondBarrier(long j) {
        synchronized (this.mLock) {
            BroadcastRecord broadcastRecord = this.mCurrentBroadcast;
            boolean z = false;
            if (broadcastRecord == null || broadcastRecord.enqueueTime > j) {
                if (isBeyondBarrier(this.mOrderedBroadcasts, j) && isBeyondBarrier(this.mAlarmQueue, j) && isDeferralsBeyondBarrier(this.mDeferredBroadcasts, j) && isDeferralsBeyondBarrier(this.mAlarmDeferrals, j)) {
                    z = true;
                }
                return z;
            }
            return false;
        }
    }

    public static int pendingInDeferralsList(ArrayList<Deferrals> arrayList) {
        int size = arrayList.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            i += arrayList.get(i2).size();
        }
        return i;
    }

    public static boolean isDeferralsListEmpty(ArrayList<Deferrals> arrayList) {
        return pendingInDeferralsList(arrayList) == 0;
    }

    public String describeStateLocked() {
        StringBuilder sb = new StringBuilder(128);
        if (this.mCurrentBroadcast != null) {
            sb.append("1 in flight, ");
        }
        sb.append(this.mOrderedBroadcasts.size());
        sb.append(" ordered");
        int size = this.mAlarmQueue.size();
        if (size > 0) {
            sb.append(", ");
            sb.append(size);
            sb.append(" alarms");
        }
        int pendingInDeferralsList = pendingInDeferralsList(this.mAlarmDeferrals);
        if (pendingInDeferralsList > 0) {
            sb.append(", ");
            sb.append(pendingInDeferralsList);
            sb.append(" deferrals in alarm recipients");
        }
        int pendingInDeferralsList2 = pendingInDeferralsList(this.mDeferredBroadcasts);
        if (pendingInDeferralsList2 > 0) {
            sb.append(", ");
            sb.append(pendingInDeferralsList2);
            sb.append(" deferred");
        }
        int bootCompletedBroadcastsUidsSize = getBootCompletedBroadcastsUidsSize("android.intent.action.LOCKED_BOOT_COMPLETED");
        if (bootCompletedBroadcastsUidsSize > 0) {
            sb.append(", ");
            sb.append(bootCompletedBroadcastsUidsSize);
            sb.append(" deferred LOCKED_BOOT_COMPLETED/");
            sb.append(getBootCompletedBroadcastsReceiversSize("android.intent.action.LOCKED_BOOT_COMPLETED"));
            sb.append(" receivers");
        }
        int bootCompletedBroadcastsUidsSize2 = getBootCompletedBroadcastsUidsSize("android.intent.action.BOOT_COMPLETED");
        if (bootCompletedBroadcastsUidsSize2 > 0) {
            sb.append(", ");
            sb.append(bootCompletedBroadcastsUidsSize2);
            sb.append(" deferred BOOT_COMPLETED/");
            sb.append(getBootCompletedBroadcastsReceiversSize("android.intent.action.BOOT_COMPLETED"));
            sb.append(" receivers");
        }
        return sb.toString();
    }

    public void enqueueOrderedBroadcastLocked(BroadcastRecord broadcastRecord) {
        ArrayList<BroadcastRecord> arrayList;
        if (broadcastRecord.alarm && this.mQueue.mService.mConstants.mPrioritizeAlarmBroadcasts) {
            arrayList = this.mAlarmQueue;
        } else {
            arrayList = this.mOrderedBroadcasts;
        }
        List<Object> list = broadcastRecord.receivers;
        if (list == null || list.isEmpty()) {
            arrayList.add(broadcastRecord);
        } else if ("android.intent.action.LOCKED_BOOT_COMPLETED".equals(broadcastRecord.intent.getAction())) {
            ActivityManagerService activityManagerService = this.mQueue.mService;
            getDeferredPerUser(broadcastRecord.userId).enqueueBootCompletedBroadcasts("android.intent.action.LOCKED_BOOT_COMPLETED", broadcastRecord.splitDeferredBootCompletedBroadcastLocked(activityManagerService.mInternal, activityManagerService.mConstants.mDeferBootCompletedBroadcast));
            if (broadcastRecord.receivers.isEmpty()) {
                return;
            }
            this.mOrderedBroadcasts.add(broadcastRecord);
        } else if ("android.intent.action.BOOT_COMPLETED".equals(broadcastRecord.intent.getAction())) {
            ActivityManagerService activityManagerService2 = this.mQueue.mService;
            getDeferredPerUser(broadcastRecord.userId).enqueueBootCompletedBroadcasts("android.intent.action.BOOT_COMPLETED", broadcastRecord.splitDeferredBootCompletedBroadcastLocked(activityManagerService2.mInternal, activityManagerService2.mConstants.mDeferBootCompletedBroadcast));
            if (broadcastRecord.receivers.isEmpty()) {
                return;
            }
            this.mOrderedBroadcasts.add(broadcastRecord);
        } else {
            arrayList.add(broadcastRecord);
        }
    }

    public final int getBootCompletedBroadcastsUidsSize(String str) {
        int size = this.mUser2Deferred.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            i += this.mUser2Deferred.valueAt(i2).getBootCompletedBroadcastsUidsSize(str);
        }
        return i;
    }

    public final int getBootCompletedBroadcastsReceiversSize(String str) {
        int size = this.mUser2Deferred.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            i += this.mUser2Deferred.valueAt(i2).getBootCompletedBroadcastsReceiversSize(str);
        }
        return i;
    }

    public BroadcastRecord replaceBroadcastLocked(BroadcastRecord broadcastRecord, String str) {
        BroadcastRecord replaceBroadcastLocked = replaceBroadcastLocked(this.mOrderedBroadcasts, broadcastRecord, str);
        if (replaceBroadcastLocked == null) {
            replaceBroadcastLocked = replaceBroadcastLocked(this.mAlarmQueue, broadcastRecord, str);
        }
        if (replaceBroadcastLocked == null) {
            replaceBroadcastLocked = replaceDeferredBroadcastLocked(this.mAlarmDeferrals, broadcastRecord, str);
        }
        return replaceBroadcastLocked == null ? replaceDeferredBroadcastLocked(this.mDeferredBroadcasts, broadcastRecord, str) : replaceBroadcastLocked;
    }

    public final BroadcastRecord replaceDeferredBroadcastLocked(ArrayList<Deferrals> arrayList, BroadcastRecord broadcastRecord, String str) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            BroadcastRecord replaceBroadcastLocked = replaceBroadcastLocked(arrayList.get(i).broadcasts, broadcastRecord, str);
            if (replaceBroadcastLocked != null) {
                return replaceBroadcastLocked;
            }
        }
        return null;
    }

    public final BroadcastRecord replaceBroadcastLocked(ArrayList<BroadcastRecord> arrayList, BroadcastRecord broadcastRecord, String str) {
        Intent intent = broadcastRecord.intent;
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            BroadcastRecord broadcastRecord2 = arrayList.get(size);
            if (broadcastRecord2.userId == broadcastRecord.userId && intent.filterEquals(broadcastRecord2.intent)) {
                broadcastRecord.deferred = broadcastRecord2.deferred;
                arrayList.set(size, broadcastRecord);
                return broadcastRecord2;
            }
        }
        return null;
    }

    public boolean cleanupDisabledPackageReceiversLocked(String str, Set<String> set, int i, boolean z) {
        BroadcastRecord broadcastRecord;
        boolean cleanupBroadcastListDisabledReceiversLocked = cleanupBroadcastListDisabledReceiversLocked(this.mOrderedBroadcasts, str, set, i, z);
        if (z || !cleanupBroadcastListDisabledReceiversLocked) {
            cleanupBroadcastListDisabledReceiversLocked = cleanupBroadcastListDisabledReceiversLocked(this.mAlarmQueue, str, set, i, z);
        }
        if (z || !cleanupBroadcastListDisabledReceiversLocked) {
            ArrayList<BroadcastRecord> arrayList = new ArrayList<>();
            int size = this.mUser2Deferred.size();
            for (int i2 = 0; i2 < size; i2++) {
                SparseArray<BroadcastRecord> sparseArray = this.mUser2Deferred.valueAt(i2).mDeferredLockedBootCompletedBroadcasts;
                int size2 = sparseArray.size();
                for (int i3 = 0; i3 < size2; i3++) {
                    arrayList.add(sparseArray.valueAt(i3));
                }
            }
            cleanupBroadcastListDisabledReceiversLocked = cleanupBroadcastListDisabledReceiversLocked(arrayList, str, set, i, z);
        }
        if (z || !cleanupBroadcastListDisabledReceiversLocked) {
            ArrayList<BroadcastRecord> arrayList2 = new ArrayList<>();
            int size3 = this.mUser2Deferred.size();
            for (int i4 = 0; i4 < size3; i4++) {
                SparseArray<BroadcastRecord> sparseArray2 = this.mUser2Deferred.valueAt(i4).mDeferredBootCompletedBroadcasts;
                int size4 = sparseArray2.size();
                for (int i5 = 0; i5 < size4; i5++) {
                    arrayList2.add(sparseArray2.valueAt(i5));
                }
            }
            cleanupBroadcastListDisabledReceiversLocked = cleanupBroadcastListDisabledReceiversLocked(arrayList2, str, set, i, z);
        }
        if (z || !cleanupBroadcastListDisabledReceiversLocked) {
            cleanupBroadcastListDisabledReceiversLocked |= cleanupDeferralsListDisabledReceiversLocked(this.mAlarmDeferrals, str, set, i, z);
        }
        if (z || !cleanupBroadcastListDisabledReceiversLocked) {
            cleanupBroadcastListDisabledReceiversLocked |= cleanupDeferralsListDisabledReceiversLocked(this.mDeferredBroadcasts, str, set, i, z);
        }
        return ((z || !cleanupBroadcastListDisabledReceiversLocked) && (broadcastRecord = this.mCurrentBroadcast) != null) ? cleanupBroadcastListDisabledReceiversLocked | broadcastRecord.cleanupDisabledPackageReceiversLocked(str, set, i, z) : cleanupBroadcastListDisabledReceiversLocked;
    }

    public final boolean cleanupDeferralsListDisabledReceiversLocked(ArrayList<Deferrals> arrayList, String str, Set<String> set, int i, boolean z) {
        Iterator<Deferrals> it = arrayList.iterator();
        boolean z2 = false;
        while (it.hasNext()) {
            z2 = cleanupBroadcastListDisabledReceiversLocked(it.next().broadcasts, str, set, i, z);
            if (!z && z2) {
                return true;
            }
        }
        return z2;
    }

    public final boolean cleanupBroadcastListDisabledReceiversLocked(ArrayList<BroadcastRecord> arrayList, String str, Set<String> set, int i, boolean z) {
        Iterator<BroadcastRecord> it = arrayList.iterator();
        boolean z2 = false;
        while (it.hasNext()) {
            z2 |= it.next().cleanupDisabledPackageReceiversLocked(str, set, i, z);
            if (!z && z2) {
                return true;
            }
        }
        return z2;
    }

    @NeverCompile
    public void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
        BroadcastRecord broadcastRecord = this.mCurrentBroadcast;
        if (broadcastRecord != null) {
            broadcastRecord.dumpDebug(protoOutputStream, j);
        }
        Iterator<Deferrals> it = this.mAlarmDeferrals.iterator();
        while (it.hasNext()) {
            it.next().dumpDebug(protoOutputStream, j);
        }
        Iterator<BroadcastRecord> it2 = this.mOrderedBroadcasts.iterator();
        while (it2.hasNext()) {
            it2.next().dumpDebug(protoOutputStream, j);
        }
        Iterator<BroadcastRecord> it3 = this.mAlarmQueue.iterator();
        while (it3.hasNext()) {
            it3.next().dumpDebug(protoOutputStream, j);
        }
        Iterator<Deferrals> it4 = this.mDeferredBroadcasts.iterator();
        while (it4.hasNext()) {
            it4.next().dumpDebug(protoOutputStream, j);
        }
        int size = this.mUser2Deferred.size();
        for (int i = 0; i < size; i++) {
            this.mUser2Deferred.valueAt(i).dumpDebug(protoOutputStream, j);
        }
    }

    public BroadcastRecord getActiveBroadcastLocked() {
        return this.mCurrentBroadcast;
    }

    public BroadcastRecord getNextBroadcastLocked(long j) {
        BroadcastRecord broadcastRecord = this.mCurrentBroadcast;
        if (broadcastRecord != null) {
            return broadcastRecord;
        }
        BroadcastRecord remove = !this.mAlarmQueue.isEmpty() ? this.mAlarmQueue.remove(0) : null;
        if (remove == null) {
            remove = dequeueDeferredBootCompletedBroadcast();
        }
        if (remove == null && !this.mAlarmDeferrals.isEmpty()) {
            remove = popLocked(this.mAlarmDeferrals);
        }
        boolean z = !this.mOrderedBroadcasts.isEmpty();
        if (remove == null && !this.mDeferredBroadcasts.isEmpty()) {
            int i = 0;
            while (true) {
                if (i >= this.mDeferredBroadcasts.size()) {
                    break;
                }
                Deferrals deferrals = this.mDeferredBroadcasts.get(i);
                if (j < deferrals.deferUntil && z) {
                    break;
                } else if (deferrals.broadcasts.size() > 0) {
                    remove = deferrals.broadcasts.remove(0);
                    this.mDeferredBroadcasts.remove(i);
                    long calculateDeferral = calculateDeferral(deferrals.deferredBy);
                    deferrals.deferredBy = calculateDeferral;
                    deferrals.deferUntil += calculateDeferral;
                    insertLocked(this.mDeferredBroadcasts, deferrals);
                    break;
                } else {
                    i++;
                }
            }
        }
        if (remove == null && z) {
            remove = this.mOrderedBroadcasts.remove(0);
        }
        this.mCurrentBroadcast = remove;
        return remove;
    }

    public void retireBroadcastLocked(BroadcastRecord broadcastRecord) {
        if (broadcastRecord != this.mCurrentBroadcast) {
            Slog.wtf("BroadcastDispatcher", "Retiring broadcast " + broadcastRecord + " doesn't match current outgoing " + this.mCurrentBroadcast);
        }
        this.mCurrentBroadcast = null;
    }

    public boolean isDeferringLocked(int i) {
        Deferrals findUidLocked = findUidLocked(i);
        if (findUidLocked == null || !findUidLocked.broadcasts.isEmpty() || SystemClock.uptimeMillis() < findUidLocked.deferUntil) {
            return findUidLocked != null;
        }
        removeDeferral(findUidLocked);
        return false;
    }

    public void startDeferring(int i) {
        synchronized (this.mLock) {
            Deferrals findUidLocked = findUidLocked(i);
            if (findUidLocked == null) {
                Deferrals deferrals = new Deferrals(i, SystemClock.uptimeMillis(), this.mConstants.DEFERRAL, this.mAlarmUids.get(i, 0));
                if (deferrals.alarmCount == 0) {
                    insertLocked(this.mDeferredBroadcasts, deferrals);
                    scheduleDeferralCheckLocked(true);
                } else {
                    this.mAlarmDeferrals.add(deferrals);
                }
            } else {
                findUidLocked.deferredBy = this.mConstants.DEFERRAL;
            }
        }
    }

    public void addDeferredBroadcast(int i, BroadcastRecord broadcastRecord) {
        synchronized (this.mLock) {
            Deferrals findUidLocked = findUidLocked(i);
            if (findUidLocked == null) {
                Slog.wtf("BroadcastDispatcher", "Adding deferred broadcast but not tracking " + i);
            } else if (broadcastRecord == null) {
                Slog.wtf("BroadcastDispatcher", "Deferring null broadcast to " + i);
            } else {
                broadcastRecord.deferred = true;
                findUidLocked.add(broadcastRecord);
            }
        }
    }

    public void scheduleDeferralCheckLocked(boolean z) {
        if ((z || !this.mRecheckScheduled) && !this.mDeferredBroadcasts.isEmpty()) {
            Deferrals deferrals = this.mDeferredBroadcasts.get(0);
            if (deferrals.broadcasts.isEmpty()) {
                return;
            }
            this.mHandler.removeCallbacks(this.mScheduleRunnable);
            this.mHandler.postAtTime(this.mScheduleRunnable, deferrals.deferUntil);
            this.mRecheckScheduled = true;
        }
    }

    public void cancelDeferralsLocked() {
        zeroDeferralTimes(this.mAlarmDeferrals);
        zeroDeferralTimes(this.mDeferredBroadcasts);
    }

    public static void zeroDeferralTimes(ArrayList<Deferrals> arrayList) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            Deferrals deferrals = arrayList.get(i);
            deferrals.deferredBy = 0L;
            deferrals.deferUntil = 0L;
        }
    }

    public final Deferrals findUidLocked(int i) {
        Deferrals findUidLocked = findUidLocked(i, this.mDeferredBroadcasts);
        return findUidLocked == null ? findUidLocked(i, this.mAlarmDeferrals) : findUidLocked;
    }

    public final boolean removeDeferral(Deferrals deferrals) {
        boolean remove = this.mDeferredBroadcasts.remove(deferrals);
        return !remove ? this.mAlarmDeferrals.remove(deferrals) : remove;
    }

    public static Deferrals findUidLocked(int i, ArrayList<Deferrals> arrayList) {
        int size = arrayList.size();
        for (int i2 = 0; i2 < size; i2++) {
            Deferrals deferrals = arrayList.get(i2);
            if (i == deferrals.uid) {
                return deferrals;
            }
        }
        return null;
    }

    public static BroadcastRecord popLocked(ArrayList<Deferrals> arrayList) {
        Deferrals deferrals = arrayList.get(0);
        if (deferrals.broadcasts.isEmpty()) {
            return null;
        }
        return deferrals.broadcasts.remove(0);
    }

    public static void insertLocked(ArrayList<Deferrals> arrayList, Deferrals deferrals) {
        int size = arrayList.size();
        int i = 0;
        while (i < size && deferrals.deferUntil >= arrayList.get(i).deferUntil) {
            i++;
        }
        arrayList.add(i, deferrals);
    }

    public final long calculateDeferral(long j) {
        BroadcastConstants broadcastConstants = this.mConstants;
        return Math.max(broadcastConstants.DEFERRAL_FLOOR, ((float) j) * broadcastConstants.DEFERRAL_DECAY_FACTOR);
    }

    @NeverCompile
    public boolean dumpLocked(PrintWriter printWriter, String str, String str2, SimpleDateFormat simpleDateFormat) {
        Dumper dumper = new Dumper(printWriter, str2, str, simpleDateFormat);
        dumper.setHeading("Currently in flight");
        dumper.setLabel("In-Flight Ordered Broadcast");
        BroadcastRecord broadcastRecord = this.mCurrentBroadcast;
        if (broadcastRecord != null) {
            dumper.dump(broadcastRecord);
        } else {
            printWriter.println("  (null)");
        }
        boolean didPrint = dumper.didPrint() | false;
        dumper.setHeading("Active alarm broadcasts");
        dumper.setLabel("Active Alarm Broadcast");
        Iterator<BroadcastRecord> it = this.mAlarmQueue.iterator();
        while (it.hasNext()) {
            dumper.dump(it.next());
        }
        boolean didPrint2 = didPrint | dumper.didPrint();
        dumper.setHeading("Active ordered broadcasts");
        dumper.setLabel("Active Ordered Broadcast");
        Iterator<Deferrals> it2 = this.mAlarmDeferrals.iterator();
        while (it2.hasNext()) {
            it2.next().dumpLocked(dumper);
        }
        Iterator<BroadcastRecord> it3 = this.mOrderedBroadcasts.iterator();
        while (it3.hasNext()) {
            dumper.dump(it3.next());
        }
        boolean didPrint3 = didPrint2 | dumper.didPrint();
        dumper.setHeading("Deferred ordered broadcasts");
        dumper.setLabel("Deferred Ordered Broadcast");
        Iterator<Deferrals> it4 = this.mDeferredBroadcasts.iterator();
        while (it4.hasNext()) {
            it4.next().dumpLocked(dumper);
        }
        boolean didPrint4 = didPrint3 | dumper.didPrint();
        dumper.setHeading("Deferred LOCKED_BOOT_COMPLETED broadcasts");
        dumper.setLabel("Deferred LOCKED_BOOT_COMPLETED Broadcast");
        int size = this.mUser2Deferred.size();
        for (int i = 0; i < size; i++) {
            this.mUser2Deferred.valueAt(i).dump(dumper, "android.intent.action.LOCKED_BOOT_COMPLETED");
        }
        boolean didPrint5 = didPrint4 | dumper.didPrint();
        dumper.setHeading("Deferred BOOT_COMPLETED broadcasts");
        dumper.setLabel("Deferred BOOT_COMPLETED Broadcast");
        int size2 = this.mUser2Deferred.size();
        for (int i2 = 0; i2 < size2; i2++) {
            this.mUser2Deferred.valueAt(i2).dump(dumper, "android.intent.action.BOOT_COMPLETED");
        }
        return dumper.didPrint() | didPrint5;
    }
}
