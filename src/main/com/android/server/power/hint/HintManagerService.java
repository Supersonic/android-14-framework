package com.android.server.power.hint;

import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.IUidObserver;
import android.app.StatsManager;
import android.content.Context;
import android.os.Binder;
import android.os.IBinder;
import android.os.IHintManager;
import android.os.IHintSession;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.SparseArray;
import android.util.StatsEvent;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.Preconditions;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.FgThread;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.power.hint.HintManagerService;
import com.android.server.utils.Slogf;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class HintManagerService extends SystemService {
    @GuardedBy({"mLock"})
    public final ArrayMap<Integer, ArrayMap<IBinder, ArraySet<AppHintSession>>> mActiveSessions;
    public final ActivityManagerInternal mAmInternal;
    public final Context mContext;
    @VisibleForTesting
    final long mHintSessionPreferredRate;
    public final Object mLock;
    public final NativeWrapper mNativeWrapper;
    @VisibleForTesting
    final IHintManager.Stub mService;
    @VisibleForTesting
    final UidObserver mUidObserver;

    public HintManagerService(Context context) {
        this(context, new Injector());
    }

    @VisibleForTesting
    public HintManagerService(Context context, Injector injector) {
        super(context);
        this.mLock = new Object();
        this.mService = new BinderService();
        this.mContext = context;
        this.mActiveSessions = new ArrayMap<>();
        NativeWrapper createNativeWrapper = injector.createNativeWrapper();
        this.mNativeWrapper = createNativeWrapper;
        createNativeWrapper.halInit();
        this.mHintSessionPreferredRate = createNativeWrapper.halGetHintSessionPreferredRate();
        this.mUidObserver = new UidObserver();
        ActivityManagerInternal activityManagerInternal = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
        Objects.requireNonNull(activityManagerInternal);
        this.mAmInternal = activityManagerInternal;
    }

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public static class Injector {
        public NativeWrapper createNativeWrapper() {
            return new NativeWrapper();
        }
    }

    public final boolean isHalSupported() {
        return this.mHintSessionPreferredRate != -1;
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishBinderService("performance_hint", this.mService);
    }

    @Override // com.android.server.SystemService
    public void onBootPhase(int i) {
        if (i == 500) {
            systemReady();
        }
        if (i == 1000) {
            registerStatsCallbacks();
        }
    }

    public final void systemReady() {
        Slogf.m18v("HintManagerService", "Initializing HintManager service...");
        try {
            ActivityManager.getService().registerUidObserver(this.mUidObserver, 3, -1, (String) null);
        } catch (RemoteException unused) {
        }
    }

    public final void registerStatsCallbacks() {
        ((StatsManager) this.mContext.getSystemService(StatsManager.class)).setPullAtomCallback((int) FrameworkStatsLog.ADPF_SYSTEM_COMPONENT_INFO, (StatsManager.PullAtomMetadata) null, BackgroundThread.getExecutor(), new StatsManager.StatsPullAtomCallback() { // from class: com.android.server.power.hint.HintManagerService$$ExternalSyntheticLambda0
            public final int onPullAtom(int i, List list) {
                int onPullAtom;
                onPullAtom = HintManagerService.this.onPullAtom(i, list);
                return onPullAtom;
            }
        });
    }

    public final int onPullAtom(int i, List<StatsEvent> list) {
        if (i == 10173) {
            list.add(FrameworkStatsLog.buildStatsEvent((int) FrameworkStatsLog.ADPF_SYSTEM_COMPONENT_INFO, SystemProperties.getBoolean("debug.sf.enable_adpf_cpu_hint", false), SystemProperties.getBoolean("debug.hwui.use_hint_manager", false)));
        }
        return 0;
    }

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public static class NativeWrapper {
        private static native void nativeCloseHintSession(long j);

        private static native long nativeCreateHintSession(int i, int i2, int[] iArr, long j);

        private static native long nativeGetHintSessionPreferredRate();

        private native void nativeInit();

        private static native void nativePauseHintSession(long j);

        private static native void nativeReportActualWorkDuration(long j, long[] jArr, long[] jArr2);

        private static native void nativeResumeHintSession(long j);

        private static native void nativeSendHint(long j, int i);

        private static native void nativeSetThreads(long j, int[] iArr);

        private static native void nativeUpdateTargetWorkDuration(long j, long j2);

        public void halInit() {
            nativeInit();
        }

        public long halCreateHintSession(int i, int i2, int[] iArr, long j) {
            return nativeCreateHintSession(i, i2, iArr, j);
        }

        public void halPauseHintSession(long j) {
            nativePauseHintSession(j);
        }

        public void halResumeHintSession(long j) {
            nativeResumeHintSession(j);
        }

        public void halCloseHintSession(long j) {
            nativeCloseHintSession(j);
        }

        public void halUpdateTargetWorkDuration(long j, long j2) {
            nativeUpdateTargetWorkDuration(j, j2);
        }

        public void halReportActualWorkDuration(long j, long[] jArr, long[] jArr2) {
            nativeReportActualWorkDuration(j, jArr, jArr2);
        }

        public void halSendHint(long j, int i) {
            nativeSendHint(j, i);
        }

        public long halGetHintSessionPreferredRate() {
            return nativeGetHintSessionPreferredRate();
        }

        public void halSetThreads(long j, int[] iArr) {
            nativeSetThreads(j, iArr);
        }
    }

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public final class UidObserver extends IUidObserver.Stub {
        public final SparseArray<Integer> mProcStatesCache = new SparseArray<>();

        public void onUidActive(int i) {
        }

        public void onUidCachedChanged(int i, boolean z) {
        }

        public void onUidIdle(int i, boolean z) {
        }

        public void onUidProcAdjChanged(int i) {
        }

        public UidObserver() {
        }

        public boolean isUidForeground(int i) {
            boolean z;
            synchronized (HintManagerService.this.mLock) {
                z = this.mProcStatesCache.get(i, 6).intValue() <= 6;
            }
            return z;
        }

        public void onUidGone(final int i, boolean z) {
            FgThread.getHandler().post(new Runnable() { // from class: com.android.server.power.hint.HintManagerService$UidObserver$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    HintManagerService.UidObserver.this.lambda$onUidGone$0(i);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onUidGone$0(int i) {
            synchronized (HintManagerService.this.mLock) {
                ArrayMap arrayMap = (ArrayMap) HintManagerService.this.mActiveSessions.get(Integer.valueOf(i));
                if (arrayMap == null) {
                    return;
                }
                for (int size = arrayMap.size() - 1; size >= 0; size--) {
                    ArraySet arraySet = (ArraySet) arrayMap.valueAt(size);
                    for (int size2 = arraySet.size() - 1; size2 >= 0; size2--) {
                        ((AppHintSession) arraySet.valueAt(size2)).close();
                    }
                }
                this.mProcStatesCache.delete(i);
            }
        }

        public void onUidStateChanged(final int i, final int i2, long j, int i3) {
            FgThread.getHandler().post(new Runnable() { // from class: com.android.server.power.hint.HintManagerService$UidObserver$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    HintManagerService.UidObserver.this.lambda$onUidStateChanged$1(i, i2);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onUidStateChanged$1(int i, int i2) {
            synchronized (HintManagerService.this.mLock) {
                this.mProcStatesCache.put(i, Integer.valueOf(i2));
                ArrayMap arrayMap = (ArrayMap) HintManagerService.this.mActiveSessions.get(Integer.valueOf(i));
                if (arrayMap == null) {
                    return;
                }
                for (ArraySet arraySet : arrayMap.values()) {
                    Iterator it = arraySet.iterator();
                    while (it.hasNext()) {
                        ((AppHintSession) it.next()).onProcStateChanged();
                    }
                }
            }
        }
    }

    @VisibleForTesting
    public IHintManager.Stub getBinderServiceInstance() {
        return this.mService;
    }

    public final boolean checkTidValid(int i, int i2, int[] iArr) {
        List isolatedProcesses = i != 1000 ? this.mAmInternal.getIsolatedProcesses(i) : null;
        if (isolatedProcesses == null) {
            isolatedProcesses = new ArrayList();
        }
        isolatedProcesses.add(Integer.valueOf(i2));
        for (int i3 : iArr) {
            long[] jArr = new long[2];
            Process.readProcLines("/proc/" + i3 + "/status", new String[]{"Uid:", "Tgid:"}, jArr);
            int i4 = (int) jArr[0];
            if (!isolatedProcesses.contains(Integer.valueOf((int) jArr[1])) && i4 != i) {
                return false;
            }
        }
        return true;
    }

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public final class BinderService extends IHintManager.Stub {
        public BinderService() {
        }

        public IHintSession createHintSession(IBinder iBinder, int[] iArr, long j) {
            if (HintManagerService.this.isHalSupported()) {
                Objects.requireNonNull(iBinder);
                Objects.requireNonNull(iArr);
                Preconditions.checkArgument(iArr.length != 0, "tids should not be empty.");
                int callingUid = Binder.getCallingUid();
                int threadGroupLeader = Process.getThreadGroupLeader(Binder.getCallingPid());
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    if (!HintManagerService.this.checkTidValid(callingUid, threadGroupLeader, iArr)) {
                        throw new SecurityException("Some tid doesn't belong to the application");
                    }
                    long halCreateHintSession = HintManagerService.this.mNativeWrapper.halCreateHintSession(threadGroupLeader, callingUid, iArr, j);
                    if (halCreateHintSession == 0) {
                        return null;
                    }
                    AppHintSession appHintSession = new AppHintSession(callingUid, threadGroupLeader, iArr, iBinder, halCreateHintSession, j);
                    logPerformanceHintSessionAtom(callingUid, halCreateHintSession, j, iArr);
                    synchronized (HintManagerService.this.mLock) {
                        ArrayMap arrayMap = (ArrayMap) HintManagerService.this.mActiveSessions.get(Integer.valueOf(callingUid));
                        if (arrayMap == null) {
                            arrayMap = new ArrayMap(1);
                            HintManagerService.this.mActiveSessions.put(Integer.valueOf(callingUid), arrayMap);
                        }
                        ArraySet arraySet = (ArraySet) arrayMap.get(iBinder);
                        if (arraySet == null) {
                            arraySet = new ArraySet(1);
                            arrayMap.put(iBinder, arraySet);
                        }
                        arraySet.add(appHintSession);
                    }
                    return appHintSession;
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            return null;
        }

        public long getHintSessionPreferredRate() {
            return HintManagerService.this.mHintSessionPreferredRate;
        }

        public void setHintSessionThreads(IHintSession iHintSession, int[] iArr) {
            ((AppHintSession) iHintSession).setThreads(iArr);
        }

        public int[] getHintSessionThreadIds(IHintSession iHintSession) {
            return ((AppHintSession) iHintSession).getThreadIds();
        }

        public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            if (DumpUtils.checkDumpPermission(HintManagerService.this.getContext(), "HintManagerService", printWriter)) {
                synchronized (HintManagerService.this.mLock) {
                    printWriter.println("HintSessionPreferredRate: " + HintManagerService.this.mHintSessionPreferredRate);
                    printWriter.println("HAL Support: " + HintManagerService.this.isHalSupported());
                    printWriter.println("Active Sessions:");
                    for (int i = 0; i < HintManagerService.this.mActiveSessions.size(); i++) {
                        printWriter.println("Uid " + ((Integer) HintManagerService.this.mActiveSessions.keyAt(i)).toString() + XmlUtils.STRING_ARRAY_SEPARATOR);
                        ArrayMap arrayMap = (ArrayMap) HintManagerService.this.mActiveSessions.valueAt(i);
                        for (int i2 = 0; i2 < arrayMap.size(); i2++) {
                            ArraySet arraySet = (ArraySet) arrayMap.valueAt(i2);
                            for (int i3 = 0; i3 < arraySet.size(); i3++) {
                                printWriter.println("  Session:");
                                ((AppHintSession) arraySet.valueAt(i3)).dump(printWriter, "    ");
                            }
                        }
                    }
                }
            }
        }

        public final void logPerformanceHintSessionAtom(int i, long j, long j2, int[] iArr) {
            FrameworkStatsLog.write((int) FrameworkStatsLog.PERFORMANCE_HINT_SESSION_REPORTED, i, j, j2, iArr.length);
        }
    }

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public final class AppHintSession extends IHintSession.Stub implements IBinder.DeathRecipient {
        public long mHalSessionPtr;
        public int[] mNewThreadIds;
        public final int mPid;
        public long mTargetDurationNanos;
        public int[] mThreadIds;
        public final IBinder mToken;
        public final int mUid;
        public boolean mUpdateAllowed = true;

        public AppHintSession(int i, int i2, int[] iArr, IBinder iBinder, long j, long j2) {
            this.mUid = i;
            this.mPid = i2;
            this.mToken = iBinder;
            this.mThreadIds = iArr;
            this.mHalSessionPtr = j;
            this.mTargetDurationNanos = j2;
            updateHintAllowed();
            try {
                iBinder.linkToDeath(this, 0);
            } catch (RemoteException e) {
                HintManagerService.this.mNativeWrapper.halCloseHintSession(this.mHalSessionPtr);
                throw new IllegalStateException("Client already dead", e);
            }
        }

        @VisibleForTesting
        public boolean updateHintAllowed() {
            boolean isUidForeground;
            synchronized (HintManagerService.this.mLock) {
                isUidForeground = HintManagerService.this.mUidObserver.isUidForeground(this.mUid);
                if (isUidForeground && !this.mUpdateAllowed) {
                    resume();
                }
                if (!isUidForeground && this.mUpdateAllowed) {
                    pause();
                }
                this.mUpdateAllowed = isUidForeground;
            }
            return isUidForeground;
        }

        public void updateTargetWorkDuration(long j) {
            synchronized (HintManagerService.this.mLock) {
                if (this.mHalSessionPtr != 0 && updateHintAllowed()) {
                    Preconditions.checkArgument(j > 0, "Expected the target duration to be greater than 0.");
                    HintManagerService.this.mNativeWrapper.halUpdateTargetWorkDuration(this.mHalSessionPtr, j);
                    this.mTargetDurationNanos = j;
                }
            }
        }

        public void reportActualWorkDuration(long[] jArr, long[] jArr2) {
            synchronized (HintManagerService.this.mLock) {
                if (this.mHalSessionPtr != 0 && updateHintAllowed()) {
                    Preconditions.checkArgument(jArr.length != 0, "the count of hint durations shouldn't be 0.");
                    Preconditions.checkArgument(jArr.length == jArr2.length, "The length of durations and timestamps should be the same.");
                    for (int i = 0; i < jArr.length; i++) {
                        if (jArr[i] <= 0) {
                            throw new IllegalArgumentException(String.format("durations[%d]=%d should be greater than 0", Integer.valueOf(i), Long.valueOf(jArr[i])));
                        }
                    }
                    HintManagerService.this.mNativeWrapper.halReportActualWorkDuration(this.mHalSessionPtr, jArr, jArr2);
                }
            }
        }

        public void close() {
            synchronized (HintManagerService.this.mLock) {
                if (this.mHalSessionPtr == 0) {
                    return;
                }
                HintManagerService.this.mNativeWrapper.halCloseHintSession(this.mHalSessionPtr);
                this.mHalSessionPtr = 0L;
                this.mToken.unlinkToDeath(this, 0);
                ArrayMap arrayMap = (ArrayMap) HintManagerService.this.mActiveSessions.get(Integer.valueOf(this.mUid));
                if (arrayMap == null) {
                    Slogf.m12w("HintManagerService", "UID %d is not present in active session map", Integer.valueOf(this.mUid));
                    return;
                }
                ArraySet arraySet = (ArraySet) arrayMap.get(this.mToken);
                if (arraySet == null) {
                    Slogf.m12w("HintManagerService", "Token %s is not present in token map", this.mToken.toString());
                    return;
                }
                arraySet.remove(this);
                if (arraySet.isEmpty()) {
                    arrayMap.remove(this.mToken);
                }
                if (arrayMap.isEmpty()) {
                    HintManagerService.this.mActiveSessions.remove(Integer.valueOf(this.mUid));
                }
            }
        }

        public void sendHint(int i) {
            synchronized (HintManagerService.this.mLock) {
                if (this.mHalSessionPtr != 0 && updateHintAllowed()) {
                    Preconditions.checkArgument(i >= 0, "the hint ID the hint value should be greater than zero.");
                    HintManagerService.this.mNativeWrapper.halSendHint(this.mHalSessionPtr, i);
                }
            }
        }

        public void setThreads(int[] iArr) {
            synchronized (HintManagerService.this.mLock) {
                if (this.mHalSessionPtr == 0) {
                    return;
                }
                if (iArr.length == 0) {
                    throw new IllegalArgumentException("Thread id list can't be empty.");
                }
                int callingUid = Binder.getCallingUid();
                int threadGroupLeader = Process.getThreadGroupLeader(Binder.getCallingPid());
                long clearCallingIdentity = Binder.clearCallingIdentity();
                if (!HintManagerService.this.checkTidValid(callingUid, threadGroupLeader, iArr)) {
                    throw new SecurityException("Some tid doesn't belong to the application.");
                }
                Binder.restoreCallingIdentity(clearCallingIdentity);
                if (!updateHintAllowed()) {
                    Slogf.m18v("HintManagerService", "update hint not allowed, storing tids.");
                    this.mNewThreadIds = iArr;
                    return;
                }
                HintManagerService.this.mNativeWrapper.halSetThreads(this.mHalSessionPtr, iArr);
                this.mThreadIds = iArr;
            }
        }

        public int[] getThreadIds() {
            return this.mThreadIds;
        }

        public final void onProcStateChanged() {
            updateHintAllowed();
        }

        public final void pause() {
            synchronized (HintManagerService.this.mLock) {
                if (this.mHalSessionPtr == 0) {
                    return;
                }
                HintManagerService.this.mNativeWrapper.halPauseHintSession(this.mHalSessionPtr);
            }
        }

        public final void resume() {
            synchronized (HintManagerService.this.mLock) {
                if (this.mHalSessionPtr == 0) {
                    return;
                }
                HintManagerService.this.mNativeWrapper.halResumeHintSession(this.mHalSessionPtr);
                if (this.mNewThreadIds != null) {
                    HintManagerService.this.mNativeWrapper.halSetThreads(this.mHalSessionPtr, this.mNewThreadIds);
                    this.mThreadIds = this.mNewThreadIds;
                    this.mNewThreadIds = null;
                }
            }
        }

        public final void dump(PrintWriter printWriter, String str) {
            synchronized (HintManagerService.this.mLock) {
                printWriter.println(str + "SessionPID: " + this.mPid);
                printWriter.println(str + "SessionUID: " + this.mUid);
                printWriter.println(str + "SessionTIDs: " + Arrays.toString(this.mThreadIds));
                printWriter.println(str + "SessionTargetDurationNanos: " + this.mTargetDurationNanos);
                printWriter.println(str + "SessionAllowed: " + updateHintAllowed());
            }
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            close();
        }
    }
}
