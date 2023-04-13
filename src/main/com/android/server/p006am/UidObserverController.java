package com.android.server.p006am;

import android.app.ActivityManager;
import android.app.IUidObserver;
import android.os.Handler;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.SparseIntArray;
import android.util.proto.ProtoOutputStream;
import android.util.proto.ProtoUtils;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.jobs.XmlUtils;
import java.io.PrintWriter;
import java.util.ArrayList;
/* renamed from: com.android.server.am.UidObserverController */
/* loaded from: classes.dex */
public class UidObserverController {
    public final Handler mHandler;
    @GuardedBy({"mLock"})
    public int mUidChangeDispatchCount;
    public final Object mLock = new Object();
    @GuardedBy({"mLock"})
    public final RemoteCallbackList<IUidObserver> mUidObservers = new RemoteCallbackList<>();
    @GuardedBy({"mLock"})
    public final ArrayList<ChangeRecord> mPendingUidChanges = new ArrayList<>();
    @GuardedBy({"mLock"})
    public final ArrayList<ChangeRecord> mAvailUidChanges = new ArrayList<>();
    public ChangeRecord[] mActiveUidChanges = new ChangeRecord[5];
    public final Runnable mDispatchRunnable = new Runnable() { // from class: com.android.server.am.UidObserverController$$ExternalSyntheticLambda0
        @Override // java.lang.Runnable
        public final void run() {
            UidObserverController.this.dispatchUidsChanged();
        }
    };
    public final ActiveUids mValidateUids = new ActiveUids(null, false);

    @VisibleForTesting
    public static int mergeWithPendingChange(int i, int i2) {
        if ((i & 6) == 0) {
            i |= i2 & 6;
        }
        if ((i & 24) == 0) {
            i |= i2 & 24;
        }
        if ((i & 1) != 0) {
            i &= -13;
        }
        if ((i2 & 32) != 0) {
            i |= 32;
        }
        if ((i2 & Integer.MIN_VALUE) != 0) {
            i |= Integer.MIN_VALUE;
        }
        return (i2 & 64) != 0 ? i | 64 : i;
    }

    public UidObserverController(Handler handler) {
        this.mHandler = handler;
    }

    public void register(IUidObserver iUidObserver, int i, int i2, String str, int i3) {
        synchronized (this.mLock) {
            this.mUidObservers.register(iUidObserver, new UidObserverRegistration(i3, str, i, i2, ActivityManager.checkUidPermission("android.permission.INTERACT_ACROSS_USERS_FULL", i3) == 0));
        }
    }

    public void unregister(IUidObserver iUidObserver) {
        synchronized (this.mLock) {
            this.mUidObservers.unregister(iUidObserver);
        }
    }

    public int enqueueUidChange(ChangeRecord changeRecord, int i, int i2, int i3, long j, int i4, boolean z) {
        synchronized (this.mLock) {
            if (this.mPendingUidChanges.size() == 0) {
                this.mHandler.post(this.mDispatchRunnable);
            }
            if (changeRecord == null) {
                changeRecord = getOrCreateChangeRecordLocked();
            }
            if (!changeRecord.isPending) {
                changeRecord.isPending = true;
                this.mPendingUidChanges.add(changeRecord);
            } else {
                i2 = mergeWithPendingChange(i2, changeRecord.change);
            }
            changeRecord.uid = i;
            changeRecord.change = i2;
            changeRecord.procState = i3;
            changeRecord.procStateSeq = j;
            changeRecord.capability = i4;
            changeRecord.ephemeral = z;
        }
        return i2;
    }

    @GuardedBy({"mLock"})
    public final ChangeRecord getOrCreateChangeRecordLocked() {
        int size = this.mAvailUidChanges.size();
        if (size > 0) {
            return this.mAvailUidChanges.remove(size - 1);
        }
        return new ChangeRecord();
    }

    @VisibleForTesting
    public void dispatchUidsChanged() {
        int size;
        synchronized (this.mLock) {
            size = this.mPendingUidChanges.size();
            if (this.mActiveUidChanges.length < size) {
                this.mActiveUidChanges = new ChangeRecord[size];
            }
            for (int i = 0; i < size; i++) {
                ChangeRecord changeRecord = this.mPendingUidChanges.get(i);
                this.mActiveUidChanges[i] = getOrCreateChangeRecordLocked();
                changeRecord.copyTo(this.mActiveUidChanges[i]);
                changeRecord.isPending = false;
            }
            this.mPendingUidChanges.clear();
            this.mUidChangeDispatchCount += size;
        }
        int beginBroadcast = this.mUidObservers.beginBroadcast();
        while (true) {
            int i2 = beginBroadcast - 1;
            if (beginBroadcast <= 0) {
                break;
            }
            dispatchUidsChangedForObserver(this.mUidObservers.getBroadcastItem(i2), (UidObserverRegistration) this.mUidObservers.getBroadcastCookie(i2), size);
            beginBroadcast = i2;
        }
        this.mUidObservers.finishBroadcast();
        if (this.mUidObservers.getRegisteredCallbackCount() > 0) {
            for (int i3 = 0; i3 < size; i3++) {
                ChangeRecord changeRecord2 = this.mActiveUidChanges[i3];
                if ((changeRecord2.change & 1) != 0) {
                    this.mValidateUids.remove(changeRecord2.uid);
                } else {
                    UidRecord uidRecord = this.mValidateUids.get(changeRecord2.uid);
                    if (uidRecord == null) {
                        uidRecord = new UidRecord(changeRecord2.uid, null);
                        this.mValidateUids.put(changeRecord2.uid, uidRecord);
                    }
                    int i4 = changeRecord2.change;
                    if ((i4 & 2) != 0) {
                        uidRecord.setIdle(true);
                    } else if ((i4 & 4) != 0) {
                        uidRecord.setIdle(false);
                    }
                    uidRecord.setSetProcState(changeRecord2.procState);
                    uidRecord.setCurProcState(changeRecord2.procState);
                    uidRecord.setSetCapability(changeRecord2.capability);
                    uidRecord.setCurCapability(changeRecord2.capability);
                }
            }
        }
        synchronized (this.mLock) {
            for (int i5 = 0; i5 < size; i5++) {
                ChangeRecord changeRecord3 = this.mActiveUidChanges[i5];
                changeRecord3.isPending = false;
                this.mAvailUidChanges.add(changeRecord3);
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:67:0x00e0, code lost:
        if (r12.procState != 20) goto L75;
     */
    /* JADX WARN: Removed duplicated region for block: B:72:0x00ed A[Catch: RemoteException -> 0x0149, TryCatch #0 {RemoteException -> 0x0149, blocks: (B:8:0x000f, B:10:0x0029, B:15:0x0036, B:20:0x0043, B:23:0x004c, B:25:0x0050, B:27:0x0058, B:33:0x0071, B:35:0x0079, B:37:0x007d, B:38:0x0083, B:40:0x0087, B:41:0x008c, B:43:0x0092, B:45:0x009a, B:46:0x00a1, B:48:0x00a5, B:88:0x0130, B:90:0x013a, B:92:0x013e, B:50:0x00af, B:52:0x00b6, B:54:0x00bc, B:56:0x00c7, B:60:0x00d0, B:66:0x00de, B:70:0x00e5, B:72:0x00ed, B:76:0x00f4, B:78:0x00f7, B:80:0x00fb, B:81:0x0102, B:83:0x011f, B:85:0x0127, B:87:0x012b, B:28:0x0060, B:30:0x0064, B:32:0x006c), top: B:96:0x000f }] */
    /* JADX WARN: Removed duplicated region for block: B:78:0x00f7 A[Catch: RemoteException -> 0x0149, TryCatch #0 {RemoteException -> 0x0149, blocks: (B:8:0x000f, B:10:0x0029, B:15:0x0036, B:20:0x0043, B:23:0x004c, B:25:0x0050, B:27:0x0058, B:33:0x0071, B:35:0x0079, B:37:0x007d, B:38:0x0083, B:40:0x0087, B:41:0x008c, B:43:0x0092, B:45:0x009a, B:46:0x00a1, B:48:0x00a5, B:88:0x0130, B:90:0x013a, B:92:0x013e, B:50:0x00af, B:52:0x00b6, B:54:0x00bc, B:56:0x00c7, B:60:0x00d0, B:66:0x00de, B:70:0x00e5, B:72:0x00ed, B:76:0x00f4, B:78:0x00f7, B:80:0x00fb, B:81:0x0102, B:83:0x011f, B:85:0x0127, B:87:0x012b, B:28:0x0060, B:30:0x0064, B:32:0x006c), top: B:96:0x000f }] */
    /* JADX WARN: Removed duplicated region for block: B:82:0x011a  */
    /* JADX WARN: Removed duplicated region for block: B:85:0x0127 A[Catch: RemoteException -> 0x0149, TryCatch #0 {RemoteException -> 0x0149, blocks: (B:8:0x000f, B:10:0x0029, B:15:0x0036, B:20:0x0043, B:23:0x004c, B:25:0x0050, B:27:0x0058, B:33:0x0071, B:35:0x0079, B:37:0x007d, B:38:0x0083, B:40:0x0087, B:41:0x008c, B:43:0x0092, B:45:0x009a, B:46:0x00a1, B:48:0x00a5, B:88:0x0130, B:90:0x013a, B:92:0x013e, B:50:0x00af, B:52:0x00b6, B:54:0x00bc, B:56:0x00c7, B:60:0x00d0, B:66:0x00de, B:70:0x00e5, B:72:0x00ed, B:76:0x00f4, B:78:0x00f7, B:80:0x00fb, B:81:0x0102, B:83:0x011f, B:85:0x0127, B:87:0x012b, B:28:0x0060, B:30:0x0064, B:32:0x006c), top: B:96:0x000f }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void dispatchUidsChangedForObserver(IUidObserver iUidObserver, UidObserverRegistration uidObserverRegistration, int i) {
        boolean z;
        int i2;
        if (iUidObserver == null) {
            return;
        }
        boolean z2 = false;
        int i3 = 0;
        while (i3 < i) {
            try {
                ChangeRecord changeRecord = this.mActiveUidChanges[i3];
                long uptimeMillis = SystemClock.uptimeMillis();
                int i4 = changeRecord.change;
                if ((UserHandle.getUserId(changeRecord.uid) == UserHandle.getUserId(uidObserverRegistration.mUid) || uidObserverRegistration.mCanInteractAcrossUsers) && ((i4 != Integer.MIN_VALUE || (uidObserverRegistration.mWhich & 1) != 0) && (i4 != 64 || (uidObserverRegistration.mWhich & 64) != 0))) {
                    if ((i4 & 2) != 0) {
                        if ((uidObserverRegistration.mWhich & 4) != 0) {
                            iUidObserver.onUidIdle(changeRecord.uid, changeRecord.ephemeral);
                        }
                    } else if ((i4 & 4) != 0 && (uidObserverRegistration.mWhich & 8) != 0) {
                        iUidObserver.onUidActive(changeRecord.uid);
                    }
                    if ((uidObserverRegistration.mWhich & 16) != 0) {
                        if ((i4 & 8) != 0) {
                            iUidObserver.onUidCachedChanged(changeRecord.uid, true);
                        } else if ((i4 & 16) != 0) {
                            iUidObserver.onUidCachedChanged(changeRecord.uid, z2);
                        }
                    }
                    if ((i4 & 1) != 0) {
                        if ((uidObserverRegistration.mWhich & 2) != 0) {
                            iUidObserver.onUidGone(changeRecord.uid, changeRecord.ephemeral);
                        }
                        SparseIntArray sparseIntArray = uidObserverRegistration.mLastProcStates;
                        if (sparseIntArray != null) {
                            sparseIntArray.delete(changeRecord.uid);
                        }
                        i2 = 20;
                    } else {
                        if ((uidObserverRegistration.mWhich & 1) != 0) {
                            if (uidObserverRegistration.mCutpoint >= 0) {
                                int i5 = uidObserverRegistration.mLastProcStates.get(changeRecord.uid, -1);
                                if (i5 != -1) {
                                    if ((i5 <= uidObserverRegistration.mCutpoint ? true : z2) != (changeRecord.procState <= uidObserverRegistration.mCutpoint ? true : z2)) {
                                    }
                                }
                                if ((uidObserverRegistration.mWhich & 32) != 0) {
                                    z |= (i4 & 32) != 0 ? true : z2;
                                }
                                if (z) {
                                    SparseIntArray sparseIntArray2 = uidObserverRegistration.mLastProcStates;
                                    if (sparseIntArray2 != null) {
                                        sparseIntArray2.put(changeRecord.uid, changeRecord.procState);
                                    }
                                    i2 = 20;
                                    iUidObserver.onUidStateChanged(changeRecord.uid, changeRecord.procState, changeRecord.procStateSeq, changeRecord.capability);
                                } else {
                                    i2 = 20;
                                }
                                if ((uidObserverRegistration.mWhich & 64) != 0 && (i4 & 64) != 0) {
                                    iUidObserver.onUidProcAdjChanged(changeRecord.uid);
                                }
                            }
                            z = true;
                            if ((uidObserverRegistration.mWhich & 32) != 0) {
                            }
                            if (z) {
                            }
                            if ((uidObserverRegistration.mWhich & 64) != 0) {
                                iUidObserver.onUidProcAdjChanged(changeRecord.uid);
                            }
                        }
                        z = z2;
                        if ((uidObserverRegistration.mWhich & 32) != 0) {
                        }
                        if (z) {
                        }
                        if ((uidObserverRegistration.mWhich & 64) != 0) {
                        }
                    }
                    int uptimeMillis2 = (int) (SystemClock.uptimeMillis() - uptimeMillis);
                    if (uidObserverRegistration.mMaxDispatchTime < uptimeMillis2) {
                        uidObserverRegistration.mMaxDispatchTime = uptimeMillis2;
                    }
                    if (uptimeMillis2 >= i2) {
                        uidObserverRegistration.mSlowDispatchCount++;
                    }
                }
                i3++;
                z2 = false;
            } catch (RemoteException unused) {
                return;
            }
        }
    }

    public UidRecord getValidateUidRecord(int i) {
        return this.mValidateUids.get(i);
    }

    public void dump(PrintWriter printWriter, String str) {
        synchronized (this.mLock) {
            int registeredCallbackCount = this.mUidObservers.getRegisteredCallbackCount();
            boolean z = false;
            for (int i = 0; i < registeredCallbackCount; i++) {
                UidObserverRegistration uidObserverRegistration = (UidObserverRegistration) this.mUidObservers.getRegisteredCallbackCookie(i);
                if (str == null || str.equals(uidObserverRegistration.mPkg)) {
                    if (!z) {
                        printWriter.println("  mUidObservers:");
                        z = true;
                    }
                    uidObserverRegistration.dump(printWriter, this.mUidObservers.getRegisteredCallbackItem(i));
                }
            }
            printWriter.println();
            printWriter.print("  mUidChangeDispatchCount=");
            printWriter.print(this.mUidChangeDispatchCount);
            printWriter.println();
            printWriter.println("  Slow UID dispatches:");
            for (int i2 = 0; i2 < registeredCallbackCount; i2++) {
                UidObserverRegistration uidObserverRegistration2 = (UidObserverRegistration) this.mUidObservers.getRegisteredCallbackCookie(i2);
                printWriter.print("    ");
                printWriter.print(this.mUidObservers.getRegisteredCallbackItem(i2).getClass().getTypeName());
                printWriter.print(": ");
                printWriter.print(uidObserverRegistration2.mSlowDispatchCount);
                printWriter.print(" / Max ");
                printWriter.print(uidObserverRegistration2.mMaxDispatchTime);
                printWriter.println("ms");
            }
        }
    }

    public void dumpDebug(ProtoOutputStream protoOutputStream, String str) {
        synchronized (this.mLock) {
            int registeredCallbackCount = this.mUidObservers.getRegisteredCallbackCount();
            for (int i = 0; i < registeredCallbackCount; i++) {
                UidObserverRegistration uidObserverRegistration = (UidObserverRegistration) this.mUidObservers.getRegisteredCallbackCookie(i);
                if (str == null || str.equals(uidObserverRegistration.mPkg)) {
                    uidObserverRegistration.dumpDebug(protoOutputStream, 2246267895831L);
                }
            }
        }
    }

    public boolean dumpValidateUids(PrintWriter printWriter, String str, int i, String str2, boolean z) {
        return this.mValidateUids.dump(printWriter, str, i, str2, z);
    }

    public void dumpValidateUidsProto(ProtoOutputStream protoOutputStream, String str, int i, long j) {
        this.mValidateUids.dumpProto(protoOutputStream, str, i, j);
    }

    /* renamed from: com.android.server.am.UidObserverController$ChangeRecord */
    /* loaded from: classes.dex */
    public static final class ChangeRecord {
        public int capability;
        public int change;
        public boolean ephemeral;
        public boolean isPending;
        public int procState;
        public long procStateSeq;
        public int uid;

        public void copyTo(ChangeRecord changeRecord) {
            changeRecord.isPending = this.isPending;
            changeRecord.uid = this.uid;
            changeRecord.change = this.change;
            changeRecord.procState = this.procState;
            changeRecord.capability = this.capability;
            changeRecord.ephemeral = this.ephemeral;
            changeRecord.procStateSeq = this.procStateSeq;
        }
    }

    /* renamed from: com.android.server.am.UidObserverController$UidObserverRegistration */
    /* loaded from: classes.dex */
    public static final class UidObserverRegistration {
        public static final int[] ORIG_ENUMS = {4, 8, 2, 1, 32, 64};
        public static final int[] PROTO_ENUMS = {3, 4, 2, 1, 6, 7};
        public final boolean mCanInteractAcrossUsers;
        public final int mCutpoint;
        public final SparseIntArray mLastProcStates;
        public int mMaxDispatchTime;
        public final String mPkg;
        public int mSlowDispatchCount;
        public final int mUid;
        public final int mWhich;

        public UidObserverRegistration(int i, String str, int i2, int i3, boolean z) {
            this.mUid = i;
            this.mPkg = str;
            this.mWhich = i2;
            this.mCutpoint = i3;
            this.mCanInteractAcrossUsers = z;
            this.mLastProcStates = i3 >= 0 ? new SparseIntArray() : null;
        }

        public void dump(PrintWriter printWriter, IUidObserver iUidObserver) {
            printWriter.print("    ");
            UserHandle.formatUid(printWriter, this.mUid);
            printWriter.print(" ");
            printWriter.print(this.mPkg);
            printWriter.print(" ");
            printWriter.print(iUidObserver.getClass().getTypeName());
            printWriter.print(XmlUtils.STRING_ARRAY_SEPARATOR);
            if ((this.mWhich & 4) != 0) {
                printWriter.print(" IDLE");
            }
            if ((this.mWhich & 8) != 0) {
                printWriter.print(" ACT");
            }
            if ((this.mWhich & 2) != 0) {
                printWriter.print(" GONE");
            }
            if ((this.mWhich & 32) != 0) {
                printWriter.print(" CAP");
            }
            if ((this.mWhich & 1) != 0) {
                printWriter.print(" STATE");
                printWriter.print(" (cut=");
                printWriter.print(this.mCutpoint);
                printWriter.print(")");
            }
            printWriter.println();
            SparseIntArray sparseIntArray = this.mLastProcStates;
            if (sparseIntArray != null) {
                int size = sparseIntArray.size();
                for (int i = 0; i < size; i++) {
                    printWriter.print("      Last ");
                    UserHandle.formatUid(printWriter, this.mLastProcStates.keyAt(i));
                    printWriter.print(": ");
                    printWriter.println(this.mLastProcStates.valueAt(i));
                }
            }
        }

        public void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
            long start = protoOutputStream.start(j);
            protoOutputStream.write(1120986464257L, this.mUid);
            protoOutputStream.write(1138166333442L, this.mPkg);
            ProtoUtils.writeBitWiseFlagsToProtoEnum(protoOutputStream, 2259152797699L, this.mWhich, ORIG_ENUMS, PROTO_ENUMS);
            protoOutputStream.write(1120986464260L, this.mCutpoint);
            SparseIntArray sparseIntArray = this.mLastProcStates;
            if (sparseIntArray != null) {
                int size = sparseIntArray.size();
                for (int i = 0; i < size; i++) {
                    long start2 = protoOutputStream.start(2246267895813L);
                    protoOutputStream.write(1120986464257L, this.mLastProcStates.keyAt(i));
                    protoOutputStream.write(1120986464258L, this.mLastProcStates.valueAt(i));
                    protoOutputStream.end(start2);
                }
            }
            protoOutputStream.end(start);
        }
    }
}
