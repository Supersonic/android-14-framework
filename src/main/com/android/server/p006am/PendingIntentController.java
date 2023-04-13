package com.android.server.p006am;

import android.app.ActivityManagerInternal;
import android.app.AppGlobals;
import android.app.PendingIntent;
import android.app.PendingIntentStats;
import android.content.IIntentSender;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.os.IResultReceiver;
import com.android.internal.util.RingBuffer;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.AlarmManagerInternal;
import com.android.server.LocalServices;
import com.android.server.p006am.PendingIntentRecord;
import com.android.server.p014wm.ActivityTaskManagerInternal;
import com.android.server.p014wm.SafeActivityOptions;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
/* renamed from: com.android.server.am.PendingIntentController */
/* loaded from: classes.dex */
public class PendingIntentController {
    public ActivityManagerInternal mAmInternal;
    public final ActivityManagerConstants mConstants;

    /* renamed from: mH */
    public final Handler f1120mH;
    public final UserController mUserController;
    public final Object mLock = new Object();
    public final HashMap<PendingIntentRecord.Key, WeakReference<PendingIntentRecord>> mIntentSenderRecords = new HashMap<>();
    @GuardedBy({"mLock"})
    public final SparseIntArray mIntentsPerUid = new SparseIntArray();
    @GuardedBy({"mLock"})
    public final SparseArray<RingBuffer<String>> mRecentIntentsPerUid = new SparseArray<>();
    public final ActivityTaskManagerInternal mAtmInternal = (ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class);

    public PendingIntentController(Looper looper, UserController userController, ActivityManagerConstants activityManagerConstants) {
        this.f1120mH = new Handler(looper);
        this.mUserController = userController;
        this.mConstants = activityManagerConstants;
    }

    public void onActivityManagerInternalAdded() {
        synchronized (this.mLock) {
            this.mAmInternal = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
        }
    }

    public PendingIntentRecord getIntentSender(int i, String str, String str2, int i2, int i3, IBinder iBinder, String str3, int i4, Intent[] intentArr, String[] strArr, int i5, Bundle bundle) {
        synchronized (this.mLock) {
            if (intentArr != null) {
                for (Intent intent : intentArr) {
                    intent.setDefusable(true);
                }
            }
            Bundle.setDefusable(bundle, true);
            boolean z = (i5 & 536870912) != 0;
            boolean z2 = (i5 & 268435456) != 0;
            boolean z3 = (i5 & 134217728) != 0;
            PendingIntentRecord.Key key = new PendingIntentRecord.Key(i, str, str2, iBinder, str3, i4, intentArr, strArr, i5 & (-939524097), SafeActivityOptions.fromBundle(bundle), i3);
            WeakReference<PendingIntentRecord> weakReference = this.mIntentSenderRecords.get(key);
            PendingIntentRecord pendingIntentRecord = weakReference != null ? weakReference.get() : null;
            if (pendingIntentRecord != null) {
                if (!z2) {
                    if (z3) {
                        Intent intent2 = pendingIntentRecord.key.requestIntent;
                        if (intent2 != null) {
                            intent2.replaceExtras(intentArr != null ? intentArr[intentArr.length - 1] : null);
                        }
                        if (intentArr != null) {
                            PendingIntentRecord.Key key2 = pendingIntentRecord.key;
                            intentArr[intentArr.length - 1] = key2.requestIntent;
                            key2.allIntents = intentArr;
                            key2.allResolvedTypes = strArr;
                        } else {
                            PendingIntentRecord.Key key3 = pendingIntentRecord.key;
                            key3.allIntents = null;
                            key3.allResolvedTypes = null;
                        }
                    }
                    return pendingIntentRecord;
                }
                makeIntentSenderCanceled(pendingIntentRecord);
                this.mIntentSenderRecords.remove(key);
                decrementUidStatLocked(pendingIntentRecord);
            }
            if (z) {
                return pendingIntentRecord;
            }
            PendingIntentRecord pendingIntentRecord2 = new PendingIntentRecord(this, key, i2);
            this.mIntentSenderRecords.put(key, pendingIntentRecord2.ref);
            incrementUidStatLocked(pendingIntentRecord2);
            return pendingIntentRecord2;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:108:0x0062 A[Catch: all -> 0x0088, TryCatch #0 {, blocks: (B:76:0x0003, B:78:0x000c, B:80:0x000e, B:81:0x0018, B:83:0x001e, B:85:0x0026, B:86:0x002a, B:88:0x0032, B:90:0x0038, B:106:0x0060, B:108:0x0062, B:110:0x0071, B:93:0x003f, B:98:0x004b, B:101:0x0052, B:111:0x0086), top: B:116:0x0003 }] */
    /* JADX WARN: Removed duplicated region for block: B:127:0x0060 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean removePendingIntentsForPackage(String str, int i, int i2, boolean z) {
        synchronized (this.mLock) {
            boolean z2 = false;
            if (this.mIntentSenderRecords.size() <= 0) {
                return false;
            }
            Iterator<WeakReference<PendingIntentRecord>> it = this.mIntentSenderRecords.values().iterator();
            while (it.hasNext()) {
                WeakReference<PendingIntentRecord> next = it.next();
                if (next == null) {
                    it.remove();
                } else {
                    PendingIntentRecord pendingIntentRecord = next.get();
                    if (pendingIntentRecord == null) {
                        it.remove();
                    } else if (str == null) {
                        if (pendingIntentRecord.key.userId == i) {
                            z2 = true;
                            if (z) {
                                return true;
                            }
                            it.remove();
                            makeIntentSenderCanceled(pendingIntentRecord);
                            decrementUidStatLocked(pendingIntentRecord);
                            if (pendingIntentRecord.key.activity != null) {
                                this.f1120mH.sendMessage(PooledLambda.obtainMessage(new PendingIntentController$$ExternalSyntheticLambda0(), this, pendingIntentRecord.key.activity, pendingIntentRecord.ref));
                            }
                        }
                    } else if (UserHandle.getAppId(pendingIntentRecord.uid) == i2 && (i == -1 || pendingIntentRecord.key.userId == i)) {
                        if (pendingIntentRecord.key.packageName.equals(str)) {
                            z2 = true;
                            if (z) {
                            }
                        }
                    }
                }
            }
            return z2;
        }
    }

    public void cancelIntentSender(IIntentSender iIntentSender) {
        if (iIntentSender instanceof PendingIntentRecord) {
            synchronized (this.mLock) {
                PendingIntentRecord pendingIntentRecord = (PendingIntentRecord) iIntentSender;
                try {
                    if (!UserHandle.isSameApp(AppGlobals.getPackageManager().getPackageUid(pendingIntentRecord.key.packageName, 268435456L, UserHandle.getCallingUserId()), Binder.getCallingUid())) {
                        String str = "Permission Denial: cancelIntentSender() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " is not allowed to cancel package " + pendingIntentRecord.key.packageName;
                        Slog.w("ActivityManager", str);
                        throw new SecurityException(str);
                    }
                    cancelIntentSender(pendingIntentRecord, true);
                } catch (RemoteException e) {
                    throw new SecurityException(e);
                }
            }
        }
    }

    public void cancelIntentSender(PendingIntentRecord pendingIntentRecord, boolean z) {
        synchronized (this.mLock) {
            makeIntentSenderCanceled(pendingIntentRecord);
            this.mIntentSenderRecords.remove(pendingIntentRecord.key);
            decrementUidStatLocked(pendingIntentRecord);
            if (z && pendingIntentRecord.key.activity != null) {
                this.f1120mH.sendMessage(PooledLambda.obtainMessage(new PendingIntentController$$ExternalSyntheticLambda0(), this, pendingIntentRecord.key.activity, pendingIntentRecord.ref));
            }
        }
    }

    public boolean registerIntentSenderCancelListener(IIntentSender iIntentSender, IResultReceiver iResultReceiver) {
        if (!(iIntentSender instanceof PendingIntentRecord)) {
            Slog.w("ActivityManager", "registerIntentSenderCancelListener called on non-PendingIntentRecord");
            return true;
        }
        synchronized (this.mLock) {
            PendingIntentRecord pendingIntentRecord = (PendingIntentRecord) iIntentSender;
            if (pendingIntentRecord.canceled) {
                return false;
            }
            pendingIntentRecord.registerCancelListenerLocked(iResultReceiver);
            return true;
        }
    }

    public void unregisterIntentSenderCancelListener(IIntentSender iIntentSender, IResultReceiver iResultReceiver) {
        if (iIntentSender instanceof PendingIntentRecord) {
            synchronized (this.mLock) {
                ((PendingIntentRecord) iIntentSender).unregisterCancelListenerLocked(iResultReceiver);
            }
        }
    }

    public void setPendingIntentAllowlistDuration(IIntentSender iIntentSender, IBinder iBinder, long j, int i, int i2, String str) {
        if (!(iIntentSender instanceof PendingIntentRecord)) {
            Slog.w("ActivityManager", "markAsSentFromNotification(): not a PendingIntentRecord: " + iIntentSender);
            return;
        }
        synchronized (this.mLock) {
            ((PendingIntentRecord) iIntentSender).setAllowlistDurationLocked(iBinder, j, i, i2, str);
        }
    }

    public int getPendingIntentFlags(IIntentSender iIntentSender) {
        int i;
        if (!(iIntentSender instanceof PendingIntentRecord)) {
            Slog.w("ActivityManager", "markAsSentFromNotification(): not a PendingIntentRecord: " + iIntentSender);
            return 0;
        }
        synchronized (this.mLock) {
            i = ((PendingIntentRecord) iIntentSender).key.flags;
        }
        return i;
    }

    public final void makeIntentSenderCanceled(PendingIntentRecord pendingIntentRecord) {
        pendingIntentRecord.canceled = true;
        RemoteCallbackList<IResultReceiver> detachCancelListenersLocked = pendingIntentRecord.detachCancelListenersLocked();
        if (detachCancelListenersLocked != null) {
            this.f1120mH.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.am.PendingIntentController$$ExternalSyntheticLambda1
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    ((PendingIntentController) obj).handlePendingIntentCancelled((RemoteCallbackList) obj2);
                }
            }, this, detachCancelListenersLocked));
        }
        ((AlarmManagerInternal) LocalServices.getService(AlarmManagerInternal.class)).remove(new PendingIntent(pendingIntentRecord));
    }

    public final void handlePendingIntentCancelled(RemoteCallbackList<IResultReceiver> remoteCallbackList) {
        int beginBroadcast = remoteCallbackList.beginBroadcast();
        for (int i = 0; i < beginBroadcast; i++) {
            try {
                remoteCallbackList.getBroadcastItem(i).send(0, (Bundle) null);
            } catch (RemoteException unused) {
            }
        }
        remoteCallbackList.finishBroadcast();
        remoteCallbackList.kill();
    }

    public final void clearPendingResultForActivity(IBinder iBinder, WeakReference<PendingIntentRecord> weakReference) {
        this.mAtmInternal.clearPendingResultForActivity(iBinder, weakReference);
    }

    public void dumpPendingIntents(PrintWriter printWriter, boolean z, String str) {
        boolean z2;
        synchronized (this.mLock) {
            printWriter.println("ACTIVITY MANAGER PENDING INTENTS (dumpsys activity intents)");
            if (this.mIntentSenderRecords.size() > 0) {
                ArrayMap arrayMap = new ArrayMap();
                ArrayList arrayList = new ArrayList();
                Iterator<WeakReference<PendingIntentRecord>> it = this.mIntentSenderRecords.values().iterator();
                while (it.hasNext()) {
                    WeakReference<PendingIntentRecord> next = it.next();
                    PendingIntentRecord pendingIntentRecord = next != null ? next.get() : null;
                    if (pendingIntentRecord == null) {
                        arrayList.add(next);
                    } else if (str == null || str.equals(pendingIntentRecord.key.packageName)) {
                        ArrayList arrayList2 = (ArrayList) arrayMap.get(pendingIntentRecord.key.packageName);
                        if (arrayList2 == null) {
                            arrayList2 = new ArrayList();
                            arrayMap.put(pendingIntentRecord.key.packageName, arrayList2);
                        }
                        arrayList2.add(pendingIntentRecord);
                    }
                }
                int i = 0;
                z2 = false;
                while (i < arrayMap.size()) {
                    ArrayList arrayList3 = (ArrayList) arrayMap.valueAt(i);
                    printWriter.print("  * ");
                    printWriter.print((String) arrayMap.keyAt(i));
                    printWriter.print(": ");
                    printWriter.print(arrayList3.size());
                    printWriter.println(" items");
                    for (int i2 = 0; i2 < arrayList3.size(); i2++) {
                        printWriter.print("    #");
                        printWriter.print(i2);
                        printWriter.print(": ");
                        printWriter.println(arrayList3.get(i2));
                        if (z) {
                            ((PendingIntentRecord) arrayList3.get(i2)).dump(printWriter, "      ");
                        }
                    }
                    i++;
                    z2 = true;
                }
                if (arrayList.size() > 0) {
                    printWriter.println("  * WEAK REFS:");
                    for (int i3 = 0; i3 < arrayList.size(); i3++) {
                        printWriter.print("    #");
                        printWriter.print(i3);
                        printWriter.print(": ");
                        printWriter.println(arrayList.get(i3));
                    }
                    z2 = true;
                }
            } else {
                z2 = false;
            }
            int size = this.mIntentsPerUid.size();
            if (size > 0) {
                for (int i4 = 0; i4 < size; i4++) {
                    printWriter.print("  * UID: ");
                    printWriter.print(this.mIntentsPerUid.keyAt(i4));
                    printWriter.print(" total: ");
                    printWriter.println(this.mIntentsPerUid.valueAt(i4));
                }
            }
            if (!z2) {
                printWriter.println("  (nothing)");
            }
        }
    }

    public List<PendingIntentStats> dumpPendingIntentStatsForStatsd() {
        ArrayList arrayList = new ArrayList();
        synchronized (this.mLock) {
            if (this.mIntentSenderRecords.size() > 0) {
                SparseIntArray sparseIntArray = new SparseIntArray();
                SparseIntArray sparseIntArray2 = new SparseIntArray();
                for (WeakReference<PendingIntentRecord> weakReference : this.mIntentSenderRecords.values()) {
                    if (weakReference != null && weakReference.get() != null) {
                        PendingIntentRecord pendingIntentRecord = weakReference.get();
                        int indexOfKey = sparseIntArray.indexOfKey(pendingIntentRecord.uid);
                        if (indexOfKey < 0) {
                            sparseIntArray.put(pendingIntentRecord.uid, 1);
                            sparseIntArray2.put(pendingIntentRecord.uid, pendingIntentRecord.key.requestIntent.getExtrasTotalSize());
                        } else {
                            sparseIntArray.put(pendingIntentRecord.uid, sparseIntArray.valueAt(indexOfKey) + 1);
                            sparseIntArray2.put(pendingIntentRecord.uid, sparseIntArray2.valueAt(indexOfKey) + pendingIntentRecord.key.requestIntent.getExtrasTotalSize());
                        }
                    }
                }
                int size = sparseIntArray.size();
                for (int i = 0; i < size; i++) {
                    arrayList.add(new PendingIntentStats(sparseIntArray.keyAt(i), sparseIntArray.valueAt(i), sparseIntArray2.valueAt(i) / 1024));
                }
            }
        }
        return arrayList;
    }

    @GuardedBy({"mLock"})
    public void incrementUidStatLocked(PendingIntentRecord pendingIntentRecord) {
        int i;
        RingBuffer<String> ringBuffer;
        int i2 = pendingIntentRecord.uid;
        int indexOfKey = this.mIntentsPerUid.indexOfKey(i2);
        if (indexOfKey >= 0) {
            i = this.mIntentsPerUid.valueAt(indexOfKey) + 1;
            this.mIntentsPerUid.setValueAt(indexOfKey, i);
        } else {
            this.mIntentsPerUid.put(i2, 1);
            i = 1;
        }
        int i3 = this.mConstants.PENDINGINTENT_WARNING_THRESHOLD;
        int i4 = (i3 - 10) + 1;
        if (i == i4) {
            ringBuffer = new RingBuffer<>(String.class, 10);
            this.mRecentIntentsPerUid.put(i2, ringBuffer);
        } else {
            ringBuffer = (i <= i4 || i > i3) ? null : this.mRecentIntentsPerUid.get(i2);
        }
        if (ringBuffer == null) {
            return;
        }
        ringBuffer.append(pendingIntentRecord.key.toString());
        if (i == this.mConstants.PENDINGINTENT_WARNING_THRESHOLD) {
            Slog.wtf("ActivityManager", "Too many PendingIntent created for uid " + i2 + ", recent 10: " + Arrays.toString(ringBuffer.toArray()));
            this.mRecentIntentsPerUid.remove(i2);
        }
    }

    @GuardedBy({"mLock"})
    public void decrementUidStatLocked(PendingIntentRecord pendingIntentRecord) {
        int i = pendingIntentRecord.uid;
        int indexOfKey = this.mIntentsPerUid.indexOfKey(i);
        if (indexOfKey >= 0) {
            int valueAt = this.mIntentsPerUid.valueAt(indexOfKey) - 1;
            if (valueAt == this.mConstants.PENDINGINTENT_WARNING_THRESHOLD - 10) {
                this.mRecentIntentsPerUid.delete(i);
            }
            if (valueAt == 0) {
                this.mIntentsPerUid.removeAt(indexOfKey);
            } else {
                this.mIntentsPerUid.setValueAt(indexOfKey, valueAt);
            }
        }
    }
}
