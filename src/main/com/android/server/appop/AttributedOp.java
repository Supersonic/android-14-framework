package com.android.server.appop;

import android.app.AppOpsManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.ArrayMap;
import android.util.LongSparseArray;
import android.util.Pools;
import android.util.Slog;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.appop.AppOpsService;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.function.BiConsumer;
/* loaded from: classes.dex */
public final class AttributedOp {
    public LongSparseArray<AppOpsManager.NoteOpEvent> mAccessEvents;
    public final AppOpsService mAppOpsService;
    public ArrayMap<IBinder, InProgressStartOpEvent> mInProgressEvents;
    public ArrayMap<IBinder, InProgressStartOpEvent> mPausedInProgressEvents;
    public LongSparseArray<AppOpsManager.NoteOpEvent> mRejectEvents;
    public final AppOpsService.C0464Op parent;
    public final String tag;

    public AttributedOp(AppOpsService appOpsService, String str, AppOpsService.C0464Op c0464Op) {
        this.mAppOpsService = appOpsService;
        this.tag = str;
        this.parent = c0464Op;
    }

    public void accessed(int i, String str, String str2, int i2, int i3) {
        long currentTimeMillis = System.currentTimeMillis();
        accessed(currentTimeMillis, -1L, i, str, str2, i2, i3);
        HistoricalRegistry historicalRegistry = this.mAppOpsService.mHistoricalRegistry;
        AppOpsService.C0464Op c0464Op = this.parent;
        historicalRegistry.incrementOpAccessedCount(c0464Op.f1125op, c0464Op.uid, c0464Op.packageName, this.tag, i2, i3, currentTimeMillis, 0, -1);
    }

    public void accessed(long j, long j2, int i, String str, String str2, int i2, int i3) {
        long makeKey = AppOpsManager.makeKey(i2, i3);
        if (this.mAccessEvents == null) {
            this.mAccessEvents = new LongSparseArray<>(1);
        }
        AppOpsManager.OpEventProxyInfo acquire = i != -1 ? this.mAppOpsService.mOpEventProxyInfoPool.acquire(i, str, str2) : null;
        AppOpsManager.NoteOpEvent noteOpEvent = this.mAccessEvents.get(makeKey);
        if (noteOpEvent != null) {
            noteOpEvent.reinit(j, j2, acquire, this.mAppOpsService.mOpEventProxyInfoPool);
        } else {
            this.mAccessEvents.put(makeKey, new AppOpsManager.NoteOpEvent(j, j2, acquire));
        }
    }

    public void rejected(int i, int i2) {
        rejected(System.currentTimeMillis(), i, i2);
        HistoricalRegistry historicalRegistry = this.mAppOpsService.mHistoricalRegistry;
        AppOpsService.C0464Op c0464Op = this.parent;
        historicalRegistry.incrementOpRejected(c0464Op.f1125op, c0464Op.uid, c0464Op.packageName, this.tag, i, i2);
    }

    public void rejected(long j, int i, int i2) {
        long makeKey = AppOpsManager.makeKey(i, i2);
        if (this.mRejectEvents == null) {
            this.mRejectEvents = new LongSparseArray<>(1);
        }
        AppOpsManager.NoteOpEvent noteOpEvent = this.mRejectEvents.get(makeKey);
        if (noteOpEvent != null) {
            noteOpEvent.reinit(j, -1L, (AppOpsManager.OpEventProxyInfo) null, this.mAppOpsService.mOpEventProxyInfoPool);
        } else {
            this.mRejectEvents.put(makeKey, new AppOpsManager.NoteOpEvent(j, -1L, (AppOpsManager.OpEventProxyInfo) null));
        }
    }

    public void started(IBinder iBinder, int i, String str, String str2, int i2, int i3, int i4, int i5) throws RemoteException {
        started(iBinder, i, str, str2, i2, i3, true, i4, i5);
    }

    public final void started(IBinder iBinder, int i, String str, String str2, int i2, int i3, boolean z, int i4, int i5) throws RemoteException {
        startedOrPaused(iBinder, i, str, str2, i2, i3, z, true, i4, i5);
    }

    public final void startedOrPaused(IBinder iBinder, int i, String str, String str2, int i2, int i3, boolean z, boolean z2, int i4, int i5) throws RemoteException {
        AttributedOp attributedOp;
        if (z && !this.parent.isRunning() && z2) {
            AppOpsService appOpsService = this.mAppOpsService;
            AppOpsService.C0464Op c0464Op = this.parent;
            appOpsService.scheduleOpActiveChangedIfNeededLocked(c0464Op.f1125op, c0464Op.uid, c0464Op.packageName, this.tag, true, i4, i5);
        }
        if (z2 && this.mInProgressEvents == null) {
            this.mInProgressEvents = new ArrayMap<>(1);
        } else if (!z2 && this.mPausedInProgressEvents == null) {
            this.mPausedInProgressEvents = new ArrayMap<>(1);
        }
        ArrayMap<IBinder, InProgressStartOpEvent> arrayMap = z2 ? this.mInProgressEvents : this.mPausedInProgressEvents;
        long currentTimeMillis = System.currentTimeMillis();
        InProgressStartOpEvent inProgressStartOpEvent = arrayMap.get(iBinder);
        if (inProgressStartOpEvent == null) {
            inProgressStartOpEvent = this.mAppOpsService.mInProgressStartOpEventPool.acquire(currentTimeMillis, SystemClock.elapsedRealtime(), iBinder, this.tag, PooledLambda.obtainRunnable(new BiConsumer() { // from class: com.android.server.appop.AttributedOp$$ExternalSyntheticLambda0
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    AppOpsService.onClientDeath((AttributedOp) obj, (IBinder) obj2);
                }
            }, this, iBinder), i, str, str2, i2, i3, i4, i5);
            arrayMap.put(iBinder, inProgressStartOpEvent);
            attributedOp = this;
        } else if (i2 != inProgressStartOpEvent.getUidState()) {
            attributedOp = this;
            attributedOp.onUidStateChanged(i2);
        } else {
            attributedOp = this;
        }
        inProgressStartOpEvent.mNumUnfinishedStarts++;
        if (z2) {
            HistoricalRegistry historicalRegistry = attributedOp.mAppOpsService.mHistoricalRegistry;
            AppOpsService.C0464Op c0464Op2 = attributedOp.parent;
            historicalRegistry.incrementOpAccessedCount(c0464Op2.f1125op, c0464Op2.uid, c0464Op2.packageName, attributedOp.tag, i2, i3, currentTimeMillis, i4, i5);
        }
    }

    public void finished(IBinder iBinder) {
        finished(iBinder, true);
    }

    public final void finished(IBinder iBinder, boolean z) {
        finishOrPause(iBinder, z, false);
    }

    public final void finishOrPause(IBinder iBinder, boolean z, boolean z2) {
        int indexOfKey = isRunning() ? this.mInProgressEvents.indexOfKey(iBinder) : -1;
        if (indexOfKey < 0) {
            finishPossiblyPaused(iBinder, z2);
            return;
        }
        InProgressStartOpEvent valueAt = this.mInProgressEvents.valueAt(indexOfKey);
        if (!z2) {
            valueAt.mNumUnfinishedStarts--;
        }
        if (valueAt.mNumUnfinishedStarts == 0 || z2) {
            if (!z2) {
                valueAt.finish();
                this.mInProgressEvents.removeAt(indexOfKey);
            }
            if (this.mAccessEvents == null) {
                this.mAccessEvents = new LongSparseArray<>(1);
            }
            AppOpsManager.NoteOpEvent noteOpEvent = new AppOpsManager.NoteOpEvent(valueAt.getStartTime(), SystemClock.elapsedRealtime() - valueAt.getStartElapsedTime(), valueAt.getProxy() != null ? new AppOpsManager.OpEventProxyInfo(valueAt.getProxy()) : null);
            this.mAccessEvents.put(AppOpsManager.makeKey(valueAt.getUidState(), valueAt.getFlags()), noteOpEvent);
            HistoricalRegistry historicalRegistry = this.mAppOpsService.mHistoricalRegistry;
            AppOpsService.C0464Op c0464Op = this.parent;
            historicalRegistry.increaseOpAccessDuration(c0464Op.f1125op, c0464Op.uid, c0464Op.packageName, this.tag, valueAt.getUidState(), valueAt.getFlags(), noteOpEvent.getNoteTime(), noteOpEvent.getDuration(), valueAt.getAttributionFlags(), valueAt.getAttributionChainId());
            if (z2) {
                return;
            }
            this.mAppOpsService.mInProgressStartOpEventPool.release(valueAt);
            if (this.mInProgressEvents.isEmpty()) {
                this.mInProgressEvents = null;
                if (!z || this.parent.isRunning()) {
                    return;
                }
                AppOpsService appOpsService = this.mAppOpsService;
                AppOpsService.C0464Op c0464Op2 = this.parent;
                appOpsService.scheduleOpActiveChangedIfNeededLocked(c0464Op2.f1125op, c0464Op2.uid, c0464Op2.packageName, this.tag, false, valueAt.getAttributionFlags(), valueAt.getAttributionChainId());
            }
        }
    }

    public final void finishPossiblyPaused(IBinder iBinder, boolean z) {
        if (!isPaused()) {
            Slog.wtf("AppOps", "No ops running or paused");
            return;
        }
        int indexOfKey = this.mPausedInProgressEvents.indexOfKey(iBinder);
        if (indexOfKey < 0) {
            Slog.wtf("AppOps", "No op running or paused for the client");
        } else if (z) {
        } else {
            InProgressStartOpEvent valueAt = this.mPausedInProgressEvents.valueAt(indexOfKey);
            int i = valueAt.mNumUnfinishedStarts - 1;
            valueAt.mNumUnfinishedStarts = i;
            if (i == 0) {
                this.mPausedInProgressEvents.removeAt(indexOfKey);
                this.mAppOpsService.mInProgressStartOpEventPool.release(valueAt);
                if (this.mPausedInProgressEvents.isEmpty()) {
                    this.mPausedInProgressEvents = null;
                }
            }
        }
    }

    public void createPaused(IBinder iBinder, int i, String str, String str2, int i2, int i3, int i4, int i5) throws RemoteException {
        startedOrPaused(iBinder, i, str, str2, i2, i3, true, false, i4, i5);
    }

    public void pause() {
        if (isRunning()) {
            if (this.mPausedInProgressEvents == null) {
                this.mPausedInProgressEvents = new ArrayMap<>(1);
            }
            for (int i = 0; i < this.mInProgressEvents.size(); i++) {
                InProgressStartOpEvent valueAt = this.mInProgressEvents.valueAt(i);
                this.mPausedInProgressEvents.put(valueAt.getClientId(), valueAt);
                finishOrPause(valueAt.getClientId(), true, true);
                AppOpsService appOpsService = this.mAppOpsService;
                AppOpsService.C0464Op c0464Op = this.parent;
                appOpsService.scheduleOpActiveChangedIfNeededLocked(c0464Op.f1125op, c0464Op.uid, c0464Op.packageName, this.tag, false, valueAt.getAttributionFlags(), valueAt.getAttributionChainId());
            }
            this.mInProgressEvents = null;
        }
    }

    public void resume() {
        if (isPaused()) {
            if (this.mInProgressEvents == null) {
                this.mInProgressEvents = new ArrayMap<>(this.mPausedInProgressEvents.size());
            }
            boolean z = !this.mPausedInProgressEvents.isEmpty() && this.mInProgressEvents.isEmpty();
            long currentTimeMillis = System.currentTimeMillis();
            for (int i = 0; i < this.mPausedInProgressEvents.size(); i++) {
                InProgressStartOpEvent valueAt = this.mPausedInProgressEvents.valueAt(i);
                this.mInProgressEvents.put(valueAt.getClientId(), valueAt);
                valueAt.setStartElapsedTime(SystemClock.elapsedRealtime());
                valueAt.setStartTime(currentTimeMillis);
                HistoricalRegistry historicalRegistry = this.mAppOpsService.mHistoricalRegistry;
                AppOpsService.C0464Op c0464Op = this.parent;
                historicalRegistry.incrementOpAccessedCount(c0464Op.f1125op, c0464Op.uid, c0464Op.packageName, this.tag, valueAt.getUidState(), valueAt.getFlags(), currentTimeMillis, valueAt.getAttributionFlags(), valueAt.getAttributionChainId());
                if (z) {
                    AppOpsService appOpsService = this.mAppOpsService;
                    AppOpsService.C0464Op c0464Op2 = this.parent;
                    appOpsService.scheduleOpActiveChangedIfNeededLocked(c0464Op2.f1125op, c0464Op2.uid, c0464Op2.packageName, this.tag, true, valueAt.getAttributionFlags(), valueAt.getAttributionChainId());
                }
                AppOpsService appOpsService2 = this.mAppOpsService;
                AppOpsService.C0464Op c0464Op3 = this.parent;
                appOpsService2.scheduleOpStartedIfNeededLocked(c0464Op3.f1125op, c0464Op3.uid, c0464Op3.packageName, this.tag, valueAt.getFlags(), 0, 2, valueAt.getAttributionFlags(), valueAt.getAttributionChainId());
            }
            this.mPausedInProgressEvents = null;
        }
    }

    public void onClientDeath(IBinder iBinder) {
        synchronized (this.mAppOpsService) {
            if (isPaused() || isRunning()) {
                InProgressStartOpEvent inProgressStartOpEvent = (isPaused() ? this.mPausedInProgressEvents : this.mInProgressEvents).get(iBinder);
                if (inProgressStartOpEvent != null) {
                    inProgressStartOpEvent.mNumUnfinishedStarts = 1;
                }
                finished(iBinder);
            }
        }
    }

    public void onUidStateChanged(int i) {
        int i2;
        ArrayMap<IBinder, InProgressStartOpEvent> arrayMap;
        int i3;
        if (isPaused() || isRunning()) {
            boolean isRunning = isRunning();
            ArrayMap<IBinder, InProgressStartOpEvent> arrayMap2 = isRunning ? this.mInProgressEvents : this.mPausedInProgressEvents;
            int size = arrayMap2.size();
            ArrayList arrayList = new ArrayList(arrayMap2.keySet());
            boolean z = false;
            ArrayMap<IBinder, InProgressStartOpEvent> arrayMap3 = arrayMap2;
            int i4 = 0;
            while (i4 < size) {
                InProgressStartOpEvent inProgressStartOpEvent = arrayMap3.get(arrayList.get(i4));
                if (inProgressStartOpEvent == null || inProgressStartOpEvent.getUidState() == i) {
                    i2 = i4;
                    arrayMap = arrayMap3;
                } else {
                    try {
                        int i5 = inProgressStartOpEvent.mNumUnfinishedStarts;
                        inProgressStartOpEvent.mNumUnfinishedStarts = 1;
                        AppOpsManager.OpEventProxyInfo proxy = inProgressStartOpEvent.getProxy();
                        finished(inProgressStartOpEvent.getClientId(), z);
                        if (proxy != null) {
                            i3 = i5;
                            i2 = i4;
                            arrayMap = arrayMap3;
                            try {
                                startedOrPaused(inProgressStartOpEvent.getClientId(), proxy.getUid(), proxy.getPackageName(), proxy.getAttributionTag(), i, inProgressStartOpEvent.getFlags(), false, isRunning, inProgressStartOpEvent.getAttributionFlags(), inProgressStartOpEvent.getAttributionChainId());
                            } catch (RemoteException unused) {
                            }
                        } else {
                            i3 = i5;
                            i2 = i4;
                            startedOrPaused(inProgressStartOpEvent.getClientId(), -1, null, null, i, inProgressStartOpEvent.getFlags(), false, isRunning, inProgressStartOpEvent.getAttributionFlags(), inProgressStartOpEvent.getAttributionChainId());
                        }
                        arrayMap3 = isRunning ? this.mInProgressEvents : this.mPausedInProgressEvents;
                        try {
                            InProgressStartOpEvent inProgressStartOpEvent2 = arrayMap3.get(arrayList.get(i2));
                            if (inProgressStartOpEvent2 != null) {
                                inProgressStartOpEvent2.mNumUnfinishedStarts += i3 - 1;
                            }
                        } catch (RemoteException unused2) {
                        }
                    } catch (RemoteException unused3) {
                        i2 = i4;
                    }
                    i4 = i2 + 1;
                    z = false;
                }
                arrayMap3 = arrayMap;
                i4 = i2 + 1;
                z = false;
            }
        }
    }

    public final LongSparseArray<AppOpsManager.NoteOpEvent> add(LongSparseArray<AppOpsManager.NoteOpEvent> longSparseArray, LongSparseArray<AppOpsManager.NoteOpEvent> longSparseArray2) {
        if (longSparseArray == null) {
            return longSparseArray2;
        }
        if (longSparseArray2 == null) {
            return longSparseArray;
        }
        int size = longSparseArray2.size();
        for (int i = 0; i < size; i++) {
            long keyAt = longSparseArray2.keyAt(i);
            AppOpsManager.NoteOpEvent valueAt = longSparseArray2.valueAt(i);
            AppOpsManager.NoteOpEvent noteOpEvent = longSparseArray.get(keyAt);
            if (noteOpEvent == null || valueAt.getNoteTime() > noteOpEvent.getNoteTime()) {
                longSparseArray.put(keyAt, valueAt);
            }
        }
        return longSparseArray;
    }

    public void add(AttributedOp attributedOp) {
        if (attributedOp.isRunning() || attributedOp.isPaused()) {
            ArrayMap<IBinder, InProgressStartOpEvent> arrayMap = attributedOp.isRunning() ? attributedOp.mInProgressEvents : attributedOp.mPausedInProgressEvents;
            Slog.w("AppOps", "Ignoring " + arrayMap.size() + " app-ops, running: " + attributedOp.isRunning());
            int size = arrayMap.size();
            for (int i = 0; i < size; i++) {
                InProgressStartOpEvent valueAt = arrayMap.valueAt(i);
                valueAt.finish();
                this.mAppOpsService.mInProgressStartOpEventPool.release(valueAt);
            }
        }
        this.mAccessEvents = add(this.mAccessEvents, attributedOp.mAccessEvents);
        this.mRejectEvents = add(this.mRejectEvents, attributedOp.mRejectEvents);
    }

    public boolean isRunning() {
        ArrayMap<IBinder, InProgressStartOpEvent> arrayMap = this.mInProgressEvents;
        return (arrayMap == null || arrayMap.isEmpty()) ? false : true;
    }

    public boolean isPaused() {
        ArrayMap<IBinder, InProgressStartOpEvent> arrayMap = this.mPausedInProgressEvents;
        return (arrayMap == null || arrayMap.isEmpty()) ? false : true;
    }

    public boolean hasAnyTime() {
        LongSparseArray<AppOpsManager.NoteOpEvent> longSparseArray;
        LongSparseArray<AppOpsManager.NoteOpEvent> longSparseArray2 = this.mAccessEvents;
        return (longSparseArray2 != null && longSparseArray2.size() > 0) || ((longSparseArray = this.mRejectEvents) != null && longSparseArray.size() > 0);
    }

    public final LongSparseArray<AppOpsManager.NoteOpEvent> deepClone(LongSparseArray<AppOpsManager.NoteOpEvent> longSparseArray) {
        if (longSparseArray == null) {
            return longSparseArray;
        }
        int size = longSparseArray.size();
        LongSparseArray<AppOpsManager.NoteOpEvent> longSparseArray2 = new LongSparseArray<>(size);
        for (int i = 0; i < size; i++) {
            longSparseArray2.put(longSparseArray.keyAt(i), new AppOpsManager.NoteOpEvent(longSparseArray.valueAt(i)));
        }
        return longSparseArray2;
    }

    public AppOpsManager.AttributedOpEntry createAttributedOpEntryLocked() {
        LongSparseArray<AppOpsManager.NoteOpEvent> deepClone = deepClone(this.mAccessEvents);
        if (isRunning()) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            int size = this.mInProgressEvents.size();
            if (deepClone == null) {
                deepClone = new LongSparseArray<>(size);
            }
            for (int i = 0; i < size; i++) {
                InProgressStartOpEvent valueAt = this.mInProgressEvents.valueAt(i);
                deepClone.append(AppOpsManager.makeKey(valueAt.getUidState(), valueAt.getFlags()), new AppOpsManager.NoteOpEvent(valueAt.getStartTime(), elapsedRealtime - valueAt.getStartElapsedTime(), valueAt.getProxy()));
            }
        }
        return new AppOpsManager.AttributedOpEntry(this.parent.f1125op, isRunning(), deepClone, deepClone(this.mRejectEvents));
    }

    /* loaded from: classes.dex */
    public static final class InProgressStartOpEvent implements IBinder.DeathRecipient {
        public int mAttributionChainId;
        public int mAttributionFlags;
        public String mAttributionTag;
        public IBinder mClientId;
        public int mFlags;
        public int mNumUnfinishedStarts;
        public Runnable mOnDeath;
        public AppOpsManager.OpEventProxyInfo mProxy;
        public long mStartElapsedTime;
        public long mStartTime;
        public int mUidState;

        public InProgressStartOpEvent(long j, long j2, IBinder iBinder, String str, Runnable runnable, int i, AppOpsManager.OpEventProxyInfo opEventProxyInfo, int i2, int i3, int i4) throws RemoteException {
            this.mStartTime = j;
            this.mStartElapsedTime = j2;
            this.mClientId = iBinder;
            this.mAttributionTag = str;
            this.mOnDeath = runnable;
            this.mUidState = i;
            this.mProxy = opEventProxyInfo;
            this.mFlags = i2;
            this.mAttributionFlags = i3;
            this.mAttributionChainId = i4;
            iBinder.linkToDeath(this, 0);
        }

        public void finish() {
            try {
                this.mClientId.unlinkToDeath(this, 0);
            } catch (NoSuchElementException unused) {
            }
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            this.mOnDeath.run();
        }

        public void reinit(long j, long j2, IBinder iBinder, String str, Runnable runnable, int i, int i2, AppOpsManager.OpEventProxyInfo opEventProxyInfo, int i3, int i4, Pools.Pool<AppOpsManager.OpEventProxyInfo> pool) throws RemoteException {
            this.mStartTime = j;
            this.mStartElapsedTime = j2;
            this.mClientId = iBinder;
            this.mAttributionTag = str;
            this.mOnDeath = runnable;
            this.mUidState = i;
            this.mFlags = i2;
            AppOpsManager.OpEventProxyInfo opEventProxyInfo2 = this.mProxy;
            if (opEventProxyInfo2 != null) {
                pool.release(opEventProxyInfo2);
            }
            this.mProxy = opEventProxyInfo;
            this.mAttributionFlags = i3;
            this.mAttributionChainId = i4;
            iBinder.linkToDeath(this, 0);
        }

        public long getStartTime() {
            return this.mStartTime;
        }

        public long getStartElapsedTime() {
            return this.mStartElapsedTime;
        }

        public IBinder getClientId() {
            return this.mClientId;
        }

        public int getUidState() {
            return this.mUidState;
        }

        public AppOpsManager.OpEventProxyInfo getProxy() {
            return this.mProxy;
        }

        public int getFlags() {
            return this.mFlags;
        }

        public int getAttributionFlags() {
            return this.mAttributionFlags;
        }

        public int getAttributionChainId() {
            return this.mAttributionChainId;
        }

        public void setStartTime(long j) {
            this.mStartTime = j;
        }

        public void setStartElapsedTime(long j) {
            this.mStartElapsedTime = j;
        }
    }

    /* loaded from: classes.dex */
    public static class InProgressStartOpEventPool extends Pools.SimplePool<InProgressStartOpEvent> {
        public OpEventProxyInfoPool mOpEventProxyInfoPool;

        public InProgressStartOpEventPool(OpEventProxyInfoPool opEventProxyInfoPool, int i) {
            super(i);
            this.mOpEventProxyInfoPool = opEventProxyInfoPool;
        }

        public InProgressStartOpEvent acquire(long j, long j2, IBinder iBinder, String str, Runnable runnable, int i, String str2, String str3, int i2, int i3, int i4, int i5) throws RemoteException {
            InProgressStartOpEvent inProgressStartOpEvent = (InProgressStartOpEvent) acquire();
            AppOpsManager.OpEventProxyInfo acquire = i != -1 ? this.mOpEventProxyInfoPool.acquire(i, str2, str3) : null;
            if (inProgressStartOpEvent != null) {
                inProgressStartOpEvent.reinit(j, j2, iBinder, str, runnable, i2, i3, acquire, i4, i5, this.mOpEventProxyInfoPool);
                return inProgressStartOpEvent;
            }
            return new InProgressStartOpEvent(j, j2, iBinder, str, runnable, i2, acquire, i3, i4, i5);
        }
    }

    /* loaded from: classes.dex */
    public static class OpEventProxyInfoPool extends Pools.SimplePool<AppOpsManager.OpEventProxyInfo> {
        public OpEventProxyInfoPool(int i) {
            super(i);
        }

        public AppOpsManager.OpEventProxyInfo acquire(int i, String str, String str2) {
            AppOpsManager.OpEventProxyInfo opEventProxyInfo = (AppOpsManager.OpEventProxyInfo) acquire();
            if (opEventProxyInfo != null) {
                opEventProxyInfo.reinit(i, str, str2);
                return opEventProxyInfo;
            }
            return new AppOpsManager.OpEventProxyInfo(i, str, str2);
        }
    }
}
