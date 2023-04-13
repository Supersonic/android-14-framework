package com.android.server.p006am;

import android.app.ActivityManager;
import android.app.ActivityThread;
import android.app.AppGlobals;
import android.app.IApplicationThread;
import android.app.IProcessObserver;
import android.app.IUidObserver;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.ProcessInfo;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.net.NetworkPolicyManager;
import android.os.AppZygote;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.DropBoxManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.os.PowerManager;
import android.os.Process;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.StrictMode;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.UserHandle;
import android.os.storage.StorageManagerInternal;
import android.p005os.IInstalld;
import android.provider.Settings;
import android.system.Os;
import android.system.OsConstants;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.DebugUtils;
import android.util.EventLog;
import android.util.LongSparseArray;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.CompositeRWLock;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.ProcessMap;
import com.android.internal.os.Zygote;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.MemInfoReader;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.AppStateTracker;
import com.android.server.LocalServices;
import com.android.server.ServiceThread;
import com.android.server.SystemConfig;
import com.android.server.Watchdog;
import com.android.server.backup.BackupAgentTimeoutParameters;
import com.android.server.clipboard.ClipboardService;
import com.android.server.compat.PlatformCompat;
import com.android.server.p006am.ActivityManagerService;
import com.android.server.p006am.LmkdConnection;
import com.android.server.p006am.ProcessList;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.PackageStateInternal;
import com.android.server.p014wm.ActivityServiceConnectionsHolder;
import com.android.server.p014wm.WindowManagerService;
import dalvik.system.VMRuntime;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
/* renamed from: com.android.server.am.ProcessList */
/* loaded from: classes.dex */
public final class ProcessList {
    @VisibleForTesting
    static final int NETWORK_STATE_BLOCK = 1;
    @VisibleForTesting
    static final int NETWORK_STATE_NO_CHANGE = 0;
    @VisibleForTesting
    static final int NETWORK_STATE_UNBLOCK = 2;
    public static KillHandler sKillHandler;
    public static ServiceThread sKillThread;
    public static LmkdConnection sLmkdConnection;
    @CompositeRWLock({"mService", "mProcLock"})
    public ActiveUids mActiveUids;
    public ArrayList<String> mAppDataIsolationAllowlistedApps;
    public long mCachedRestoreLevel;
    public boolean mHaveDisplaySize;
    public ImperceptibleKillRunner mImperceptibleKillRunner;
    public final int[] mOomAdj;
    public final int[] mOomMinFree;
    public ActivityManagerGlobalLock mProcLock;
    public LocalSocket mSystemServerSocketForZygote;
    public final long mTotalMemMb;
    public static final int[] sProcStateToProcMem = {0, 0, 1, 2, 1, 2, 2, 2, 2, 2, 3, 4, 1, 2, 4, 4, 4, 4, 4, 4};
    public static final long[] sFirstAwakePssTimes = {30000, 10000, 20000, 20000, 20000};
    public static final long[] sSameAwakePssTimes = {600000, 60000, 600000, BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS, 600000};
    public static final long[] sFirstAsleepPssTimes = {60000, 20000, 30000, 30000, 60000};
    public static final long[] sSameAsleepPssTimes = {600000, 60000, 600000, BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS, 600000};
    public static final long[] sTestFirstPssTimes = {BackupAgentTimeoutParameters.DEFAULT_QUOTA_EXCEEDED_TIMEOUT_MILLIS, BackupAgentTimeoutParameters.DEFAULT_QUOTA_EXCEEDED_TIMEOUT_MILLIS, 5000, 5000, 5000};
    public static final long[] sTestSamePssTimes = {15000, 10000, 10000, 15000, 15000};
    public ActivityManagerService mService = null;
    public final int[] mOomMinFreeLow = {12288, 18432, 24576, 36864, 43008, 49152};
    public final int[] mOomMinFreeHigh = {73728, 92160, 110592, 129024, 147456, 184320};
    public boolean mOomLevelsSet = false;
    public boolean mAppDataIsolationEnabled = false;
    public boolean mVoldAppDataIsolationEnabled = false;
    @GuardedBy({"mService"})
    public final StringBuilder mStringBuilder = new StringBuilder(256);
    @VisibleForTesting
    volatile long mProcStateSeqCounter = 0;
    @GuardedBy({"mService"})
    public long mProcStartSeqCounter = 0;
    @GuardedBy({"mService"})
    public final LongSparseArray<ProcessRecord> mPendingStarts = new LongSparseArray<>();
    @CompositeRWLock({"mService", "mProcLock"})
    public final ArrayList<ProcessRecord> mLruProcesses = new ArrayList<>();
    @CompositeRWLock({"mService", "mProcLock"})
    public int mLruProcessActivityStart = 0;
    @CompositeRWLock({"mService", "mProcLock"})
    public int mLruProcessServiceStart = 0;
    @CompositeRWLock({"mService", "mProcLock"})
    public int mLruSeq = 0;
    @GuardedBy({"mService"})
    public final SparseArray<ProcessRecord> mIsolatedProcesses = new SparseArray<>();
    @GuardedBy({"mService"})
    public final ProcessMap<AppZygote> mAppZygotes = new ProcessMap<>();
    @GuardedBy({"mService"})
    public final SparseArray<ArrayList<ProcessRecord>> mSdkSandboxes = new SparseArray<>();
    @GuardedBy({"mAppExitInfoTracker"})
    public final AppExitInfoTracker mAppExitInfoTracker = new AppExitInfoTracker();
    @GuardedBy({"mService"})
    public final ArrayMap<AppZygote, ArrayList<ProcessRecord>> mAppZygoteProcesses = new ArrayMap<>();
    @GuardedBy({"mService"})
    public final ArraySet<ProcessRecord> mAppsInBackgroundRestricted = new ArraySet<>();
    public PlatformCompat mPlatformCompat = null;
    public final byte[] mZygoteUnsolicitedMessage = new byte[16];
    public final int[] mZygoteSigChldMessage = new int[3];
    @GuardedBy({"mService"})
    @VisibleForTesting
    IsolatedUidRange mGlobalIsolatedUids = new IsolatedUidRange(99000, 99999);
    @GuardedBy({"mService"})
    @VisibleForTesting
    IsolatedUidRangeAllocator mAppIsolatedUidRangeAllocator = new IsolatedUidRangeAllocator(90000, 98999, 100);
    @GuardedBy({"mService"})
    public final ArrayList<ProcessRecord> mRemovedProcesses = new ArrayList<>();
    @GuardedBy({"mService"})
    public final ProcessMap<ProcessRecord> mDyingProcesses = new ProcessMap<>();
    public final RemoteCallbackList<IProcessObserver> mProcessObservers = new RemoteCallbackList<>();
    public ActivityManagerService.ProcessChangeItem[] mActiveProcessChanges = new ActivityManagerService.ProcessChangeItem[5];
    @GuardedBy({"mProcessChangeLock"})
    public final ArrayList<ActivityManagerService.ProcessChangeItem> mPendingProcessChanges = new ArrayList<>();
    @GuardedBy({"mProcessChangeLock"})
    public final ArrayList<ActivityManagerService.ProcessChangeItem> mAvailProcessChanges = new ArrayList<>();
    public final Object mProcessChangeLock = new Object();
    @CompositeRWLock({"mService", "mProcLock"})
    public final MyProcessMap mProcessNames = new MyProcessMap();

    public static int makeProcStateProtoEnum(int i) {
        switch (i) {
            case -1:
                return 999;
            case 0:
                return 1000;
            case 1:
                return 1001;
            case 2:
                return 1002;
            case 3:
                return 1020;
            case 4:
                return 1003;
            case 5:
                return 1004;
            case 6:
                return 1005;
            case 7:
                return 1006;
            case 8:
                return 1007;
            case 9:
                return 1008;
            case 10:
                return 1009;
            case 11:
                return 1010;
            case 12:
                return 1011;
            case 13:
                return 1012;
            case 14:
                return 1013;
            case 15:
                return 1014;
            case 16:
                return 1015;
            case 17:
                return 1016;
            case 18:
                return 1017;
            case 19:
                return 1018;
            case 20:
                return 1019;
            default:
                return 998;
        }
    }

    public static long minTimeFromStateChange(boolean z) {
        return z ? 10000L : 15000L;
    }

    /* renamed from: com.android.server.am.ProcessList$IsolatedUidRange */
    /* loaded from: classes.dex */
    public final class IsolatedUidRange {
        @VisibleForTesting
        public final int mFirstUid;
        @VisibleForTesting
        public final int mLastUid;
        @GuardedBy({"ProcessList.this.mService"})
        public int mNextUid;
        @GuardedBy({"ProcessList.this.mService"})
        public final SparseBooleanArray mUidUsed = new SparseBooleanArray();

        public IsolatedUidRange(int i, int i2) {
            this.mFirstUid = i;
            this.mLastUid = i2;
            this.mNextUid = i;
        }

        @GuardedBy({"ProcessList.this.mService"})
        public int allocateIsolatedUidLocked(int i) {
            int i2 = (this.mLastUid - this.mFirstUid) + 1;
            for (int i3 = 0; i3 < i2; i3++) {
                int i4 = this.mNextUid;
                int i5 = this.mFirstUid;
                if (i4 < i5 || i4 > this.mLastUid) {
                    this.mNextUid = i5;
                }
                int uid = UserHandle.getUid(i, this.mNextUid);
                this.mNextUid++;
                if (!this.mUidUsed.get(uid, false)) {
                    this.mUidUsed.put(uid, true);
                    return uid;
                }
            }
            return -1;
        }

        @GuardedBy({"ProcessList.this.mService"})
        public void freeIsolatedUidLocked(int i) {
            this.mUidUsed.delete(i);
        }
    }

    /* renamed from: com.android.server.am.ProcessList$IsolatedUidRangeAllocator */
    /* loaded from: classes.dex */
    public final class IsolatedUidRangeAllocator {
        @GuardedBy({"ProcessList.this.mService"})
        public final ProcessMap<IsolatedUidRange> mAppRanges = new ProcessMap<>();
        @GuardedBy({"ProcessList.this.mService"})
        public final BitSet mAvailableUidRanges;
        public final int mFirstUid;
        public final int mNumUidRanges;
        public final int mNumUidsPerRange;

        public IsolatedUidRangeAllocator(int i, int i2, int i3) {
            this.mFirstUid = i;
            this.mNumUidsPerRange = i3;
            int i4 = ((i2 - i) + 1) / i3;
            this.mNumUidRanges = i4;
            BitSet bitSet = new BitSet(i4);
            this.mAvailableUidRanges = bitSet;
            bitSet.set(0, i4);
        }

        @GuardedBy({"ProcessList.this.mService"})
        public IsolatedUidRange getIsolatedUidRangeLocked(String str, int i) {
            return (IsolatedUidRange) this.mAppRanges.get(str, i);
        }

        @GuardedBy({"ProcessList.this.mService"})
        public IsolatedUidRange getOrCreateIsolatedUidRangeLocked(String str, int i) {
            IsolatedUidRange isolatedUidRangeLocked = getIsolatedUidRangeLocked(str, i);
            if (isolatedUidRangeLocked == null) {
                int nextSetBit = this.mAvailableUidRanges.nextSetBit(0);
                if (nextSetBit < 0) {
                    return null;
                }
                this.mAvailableUidRanges.clear(nextSetBit);
                int i2 = this.mFirstUid;
                int i3 = this.mNumUidsPerRange;
                int i4 = i2 + (nextSetBit * i3);
                IsolatedUidRange isolatedUidRange = new IsolatedUidRange(i4, (i3 + i4) - 1);
                this.mAppRanges.put(str, i, isolatedUidRange);
                return isolatedUidRange;
            }
            return isolatedUidRangeLocked;
        }

        @GuardedBy({"ProcessList.this.mService"})
        public void freeUidRangeLocked(ApplicationInfo applicationInfo) {
            IsolatedUidRange isolatedUidRange = (IsolatedUidRange) this.mAppRanges.get(applicationInfo.processName, applicationInfo.uid);
            if (isolatedUidRange != null) {
                this.mAvailableUidRanges.set((isolatedUidRange.mFirstUid - this.mFirstUid) / this.mNumUidsPerRange);
                this.mAppRanges.remove(applicationInfo.processName, applicationInfo.uid);
            }
        }
    }

    /* renamed from: com.android.server.am.ProcessList$MyProcessMap */
    /* loaded from: classes.dex */
    public final class MyProcessMap extends ProcessMap<ProcessRecord> {
        public MyProcessMap() {
        }

        public ProcessRecord put(String str, int i, ProcessRecord processRecord) {
            ProcessRecord processRecord2 = (ProcessRecord) super.put(str, i, processRecord);
            ProcessList.this.mService.mAtmInternal.onProcessAdded(processRecord2.getWindowProcessController());
            return processRecord2;
        }

        /* renamed from: remove */
        public ProcessRecord m1487remove(String str, int i) {
            ProcessRecord processRecord = (ProcessRecord) super.remove(str, i);
            ProcessList.this.mService.mAtmInternal.onProcessRemoved(str, i);
            return processRecord;
        }
    }

    /* renamed from: com.android.server.am.ProcessList$KillHandler */
    /* loaded from: classes.dex */
    public final class KillHandler extends Handler {
        public KillHandler(Looper looper) {
            super(looper, null, true);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 4000) {
                Trace.traceBegin(64L, "killProcessGroup");
                Process.killProcessGroup(message.arg1, message.arg2);
                Trace.traceEnd(64L);
            } else if (i == 4001) {
                if (ProcessList.sLmkdConnection.connect()) {
                    return;
                }
                Slog.i("ActivityManager", "Failed to connect to lmkd, retry after 1000 ms");
                KillHandler killHandler = ProcessList.sKillHandler;
                killHandler.sendMessageDelayed(killHandler.obtainMessage(4001), 1000L);
            } else {
                super.handleMessage(message);
            }
        }
    }

    public ProcessList() {
        int[] iArr = {0, 100, 200, 250, 900, 950};
        this.mOomAdj = iArr;
        this.mOomMinFree = new int[iArr.length];
        MemInfoReader memInfoReader = new MemInfoReader();
        memInfoReader.readMemInfo();
        this.mTotalMemMb = memInfoReader.getTotalSize() / 1048576;
        updateOomLevels(0, 0, false);
    }

    public void init(ActivityManagerService activityManagerService, ActiveUids activeUids, PlatformCompat platformCompat) {
        this.mService = activityManagerService;
        this.mActiveUids = activeUids;
        this.mPlatformCompat = platformCompat;
        this.mProcLock = activityManagerService.mProcLock;
        this.mAppDataIsolationEnabled = SystemProperties.getBoolean("persist.zygote.app_data_isolation", true);
        this.mVoldAppDataIsolationEnabled = SystemProperties.getBoolean("persist.sys.vold_app_data_isolation_enabled", false);
        this.mAppDataIsolationAllowlistedApps = new ArrayList<>(SystemConfig.getInstance().getAppDataIsolationWhitelistedApps());
        if (sKillHandler == null) {
            ServiceThread serviceThread = new ServiceThread("ActivityManager:kill", 10, true);
            sKillThread = serviceThread;
            serviceThread.start();
            sKillHandler = new KillHandler(sKillThread.getLooper());
            sLmkdConnection = new LmkdConnection(sKillThread.getLooper().getQueue(), new LmkdConnection.LmkdConnectionListener() { // from class: com.android.server.am.ProcessList.1
                @Override // com.android.server.p006am.LmkdConnection.LmkdConnectionListener
                public boolean onConnect(OutputStream outputStream) {
                    Slog.i("ActivityManager", "Connection with lmkd established");
                    return ProcessList.this.onLmkdConnect(outputStream);
                }

                @Override // com.android.server.p006am.LmkdConnection.LmkdConnectionListener
                public void onDisconnect() {
                    Slog.w("ActivityManager", "Lost connection to lmkd");
                    KillHandler killHandler = ProcessList.sKillHandler;
                    killHandler.sendMessageDelayed(killHandler.obtainMessage(4001), 1000L);
                }

                @Override // com.android.server.p006am.LmkdConnection.LmkdConnectionListener
                public boolean isReplyExpected(ByteBuffer byteBuffer, ByteBuffer byteBuffer2, int i) {
                    return i == byteBuffer.array().length && byteBuffer2.getInt(0) == byteBuffer.getInt(0);
                }

                @Override // com.android.server.p006am.LmkdConnection.LmkdConnectionListener
                public boolean handleUnsolicitedMessage(DataInputStream dataInputStream, int i) {
                    if (i < 4) {
                        return false;
                    }
                    try {
                        int readInt = dataInputStream.readInt();
                        if (readInt == 6) {
                            if (i != 12) {
                                return false;
                            }
                            ProcessList.this.mAppExitInfoTracker.scheduleNoteLmkdProcKilled(dataInputStream.readInt(), dataInputStream.readInt());
                            return true;
                        } else if (readInt != 8) {
                            if (readInt == 9 && i == 8) {
                                LmkdStatsReporter.logStateChanged(dataInputStream.readInt());
                                return true;
                            }
                            return false;
                        } else if (i < 80) {
                            return false;
                        } else {
                            Pair<Integer, Integer> pair = ActiveServices.sNumForegroundServices.get();
                            LmkdStatsReporter.logKillOccurred(dataInputStream, ((Integer) pair.first).intValue(), ((Integer) pair.second).intValue());
                            return true;
                        }
                    } catch (IOException unused) {
                        Slog.e("ActivityManager", "Invalid buffer data. Failed to log LMK_KILL_OCCURRED");
                        return false;
                    }
                }
            });
            LocalSocket createSystemServerSocketForZygote = createSystemServerSocketForZygote();
            this.mSystemServerSocketForZygote = createSystemServerSocketForZygote;
            if (createSystemServerSocketForZygote != null) {
                sKillHandler.getLooper().getQueue().addOnFileDescriptorEventListener(this.mSystemServerSocketForZygote.getFileDescriptor(), 1, new MessageQueue.OnFileDescriptorEventListener() { // from class: com.android.server.am.ProcessList$$ExternalSyntheticLambda4
                    @Override // android.os.MessageQueue.OnFileDescriptorEventListener
                    public final int onFileDescriptorEvents(FileDescriptor fileDescriptor, int i) {
                        int handleZygoteMessages;
                        handleZygoteMessages = ProcessList.this.handleZygoteMessages(fileDescriptor, i);
                        return handleZygoteMessages;
                    }
                });
            }
            this.mAppExitInfoTracker.init(this.mService);
            this.mImperceptibleKillRunner = new ImperceptibleKillRunner(sKillThread.getLooper());
        }
    }

    public void onSystemReady() {
        this.mAppExitInfoTracker.onSystemReady();
    }

    public void applyDisplaySize(WindowManagerService windowManagerService) {
        int i;
        if (this.mHaveDisplaySize) {
            return;
        }
        Point point = new Point();
        windowManagerService.getBaseDisplaySize(0, point);
        int i2 = point.x;
        if (i2 == 0 || (i = point.y) == 0) {
            return;
        }
        updateOomLevels(i2, i, true);
        this.mHaveDisplaySize = true;
    }

    public Map<Integer, String> getProcessesWithPendingBindMounts(int i) {
        HashMap hashMap = new HashMap();
        synchronized (this.mProcLock) {
            try {
                ActivityManagerService.boostPriorityForProcLockedSection();
                for (int size = this.mLruProcesses.size() - 1; size >= 0; size--) {
                    ProcessRecord processRecord = this.mLruProcesses.get(size);
                    if (processRecord.userId == i && processRecord.isBindMountPending()) {
                        int pid = processRecord.getPid();
                        if (pid == 0) {
                            throw new IllegalStateException("Pending process is not started yet,retry later");
                        }
                        hashMap.put(Integer.valueOf(pid), processRecord.info.packageName);
                    }
                }
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        ActivityManagerService.resetPriorityAfterProcLockedSection();
        return hashMap;
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x0026, code lost:
        if (r0 > 1.0f) goto L6;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void updateOomLevels(int i, int i2, boolean z) {
        int[] iArr;
        int[] iArr2;
        int[] iArr3;
        float f = ((float) (this.mTotalMemMb - 350)) / 350.0f;
        int i3 = i * i2;
        float f2 = (i3 - 384000) / 640000;
        if (f <= f2) {
            f = f2;
        }
        float f3 = f >= 0.0f ? 1.0f : 0.0f;
        f = f3;
        int integer = Resources.getSystem().getInteger(17694876);
        int integer2 = Resources.getSystem().getInteger(17694875);
        boolean z2 = Build.SUPPORTED_64_BIT_ABIS.length > 0;
        for (int i4 = 0; i4 < this.mOomAdj.length; i4++) {
            int i5 = this.mOomMinFreeLow[i4];
            int i6 = this.mOomMinFreeHigh[i4];
            if (z2) {
                if (i4 == 4) {
                    i6 = (i6 * 3) / 2;
                } else if (i4 == 5) {
                    i6 = (i6 * 7) / 4;
                }
            }
            this.mOomMinFree[i4] = (int) (i5 + ((i6 - i5) * f));
        }
        if (integer2 >= 0) {
            int i7 = 0;
            while (true) {
                if (i7 >= this.mOomAdj.length) {
                    break;
                }
                this.mOomMinFree[i7] = (int) ((integer2 * iArr3[i7]) / iArr3[iArr2.length - 1]);
                i7++;
            }
        }
        if (integer != 0) {
            int i8 = 0;
            while (true) {
                if (i8 >= this.mOomAdj.length) {
                    break;
                }
                int[] iArr4 = this.mOomMinFree;
                int i9 = iArr4[i8];
                int i10 = i9 + ((int) ((integer * i9) / iArr4[iArr.length - 1]));
                iArr4[i8] = i10;
                if (i10 < 0) {
                    iArr4[i8] = 0;
                }
                i8++;
            }
        }
        this.mCachedRestoreLevel = (getMemLevel(999) / 1024) / 3;
        int i11 = ((i3 * 4) * 3) / 1024;
        int integer3 = Resources.getSystem().getInteger(17694843);
        int integer4 = Resources.getSystem().getInteger(17694842);
        if (integer4 >= 0) {
            i11 = integer4;
        }
        if (integer3 != 0 && (i11 = i11 + integer3) < 0) {
            i11 = 0;
        }
        if (z) {
            ByteBuffer allocate = ByteBuffer.allocate(((this.mOomAdj.length * 2) + 1) * 4);
            allocate.putInt(0);
            for (int i12 = 0; i12 < this.mOomAdj.length; i12++) {
                allocate.putInt((this.mOomMinFree[i12] * 1024) / IInstalld.FLAG_USE_QUOTA);
                allocate.putInt(this.mOomAdj[i12]);
            }
            writeLmkd(allocate, null);
            SystemProperties.set("sys.sysctl.extra_free_kbytes", Integer.toString(i11));
            this.mOomLevelsSet = true;
        }
    }

    public static String buildOomTag(String str, String str2, String str3, int i, int i2, boolean z) {
        int i3 = i - i2;
        if (i3 == 0) {
            if (z) {
                return str2;
            }
            if (str3 == null) {
                return str;
            }
            return str + str3;
        }
        if (i3 < 10) {
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append(z ? "+" : "+ ");
            sb.append(Integer.toString(i3));
            return sb.toString();
        }
        return str + "+" + Integer.toString(i3);
    }

    public static String makeOomAdjString(int i, boolean z) {
        if (i >= 900) {
            return buildOomTag("cch", "cch", "   ", i, 900, z);
        }
        if (i >= 800) {
            return buildOomTag("svcb  ", "svcb", null, i, 800, z);
        }
        if (i >= 700) {
            return buildOomTag("prev  ", "prev", null, i, 700, z);
        }
        if (i >= 600) {
            return buildOomTag("home  ", "home", null, i, 600, z);
        }
        if (i >= 500) {
            return buildOomTag("svc   ", "svc", null, i, 500, z);
        }
        if (i >= 400) {
            return buildOomTag("hvy   ", "hvy", null, i, FrameworkStatsLog.APP_PROCESS_DIED__IMPORTANCE__IMPORTANCE_BACKGROUND, z);
        }
        if (i >= 300) {
            return buildOomTag("bkup  ", "bkup", null, i, 300, z);
        }
        if (i >= 250) {
            return buildOomTag("prcl  ", "prcl", null, i, 250, z);
        }
        if (i >= 225) {
            return buildOomTag("prcm  ", "prcm", null, i, 225, z);
        }
        if (i >= 200) {
            return buildOomTag("prcp  ", "prcp", null, i, 200, z);
        }
        if (i >= 100) {
            return buildOomTag("vis", "vis", "   ", i, 100, z);
        }
        if (i >= 0) {
            return buildOomTag("fg ", "fg ", "   ", i, 0, z);
        }
        if (i >= -700) {
            return buildOomTag("psvc  ", "psvc", null, i, -700, z);
        }
        if (i >= -800) {
            return buildOomTag("pers  ", "pers", null, i, -800, z);
        }
        if (i >= -900) {
            return buildOomTag("sys   ", "sys", null, i, -900, z);
        }
        if (i >= -1000) {
            return buildOomTag("ntv  ", "ntv", null, i, -1000, z);
        }
        return Integer.toString(i);
    }

    public static String makeProcStateString(int i) {
        return ActivityManager.procStateToString(i);
    }

    public static void appendRamKb(StringBuilder sb, long j) {
        int i = 0;
        int i2 = 10;
        while (i < 6) {
            if (j < i2) {
                sb.append(' ');
            }
            i++;
            i2 *= 10;
        }
        sb.append(j);
    }

    /* renamed from: com.android.server.am.ProcessList$ProcStateMemTracker */
    /* loaded from: classes.dex */
    public static final class ProcStateMemTracker {
        public int mPendingHighestMemState;
        public int mPendingMemState;
        public float mPendingScalingFactor;
        public final int[] mHighestMem = new int[5];
        public final float[] mScalingFactor = new float[5];
        public int mTotalHighestMem = 4;

        public ProcStateMemTracker() {
            for (int i = 0; i < 5; i++) {
                this.mHighestMem[i] = 5;
                this.mScalingFactor[i] = 1.0f;
            }
            this.mPendingMemState = -1;
        }

        public void dumpLine(PrintWriter printWriter) {
            printWriter.print("best=");
            printWriter.print(this.mTotalHighestMem);
            printWriter.print(" (");
            boolean z = false;
            for (int i = 0; i < 5; i++) {
                if (this.mHighestMem[i] < 5) {
                    if (z) {
                        printWriter.print(", ");
                    }
                    printWriter.print(i);
                    printWriter.print("=");
                    printWriter.print(this.mHighestMem[i]);
                    printWriter.print(" ");
                    printWriter.print(this.mScalingFactor[i]);
                    printWriter.print("x");
                    z = true;
                }
            }
            printWriter.print(")");
            if (this.mPendingMemState >= 0) {
                printWriter.print(" / pending state=");
                printWriter.print(this.mPendingMemState);
                printWriter.print(" highest=");
                printWriter.print(this.mPendingHighestMemState);
                printWriter.print(" ");
                printWriter.print(this.mPendingScalingFactor);
                printWriter.print("x");
            }
            printWriter.println();
        }
    }

    public static boolean procStatesDifferForMem(int i, int i2) {
        int[] iArr = sProcStateToProcMem;
        return iArr[i] != iArr[i2];
    }

    public static long computeNextPssTime(int i, ProcStateMemTracker procStateMemTracker, boolean z, boolean z2, long j) {
        long[] jArr;
        int i2 = sProcStateToProcMem[i];
        float f = 1.0f;
        if (procStateMemTracker != null) {
            int i3 = procStateMemTracker.mTotalHighestMem;
            if (i2 < i3) {
                i3 = i2;
            }
            r1 = i3 < procStateMemTracker.mHighestMem[i2];
            procStateMemTracker.mPendingMemState = i2;
            procStateMemTracker.mPendingHighestMemState = i3;
            if (r1) {
                procStateMemTracker.mPendingScalingFactor = 1.0f;
            } else {
                f = procStateMemTracker.mScalingFactor[i2];
                procStateMemTracker.mPendingScalingFactor = 1.5f * f;
            }
        }
        if (z) {
            if (r1) {
                jArr = sTestFirstPssTimes;
            } else {
                jArr = sTestSamePssTimes;
            }
        } else if (r1) {
            jArr = z2 ? sFirstAsleepPssTimes : sFirstAwakePssTimes;
        } else {
            jArr = z2 ? sSameAsleepPssTimes : sSameAwakePssTimes;
        }
        long j2 = ((float) jArr[i2]) * f;
        if (j2 > ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS) {
            j2 = 3600000;
        }
        return j + j2;
    }

    public long getMemLevel(int i) {
        int i2;
        int i3 = 0;
        while (true) {
            int[] iArr = this.mOomAdj;
            if (i3 < iArr.length) {
                if (i <= iArr[i3]) {
                    i2 = this.mOomMinFree[i3];
                    break;
                }
                i3++;
            } else {
                i2 = this.mOomMinFree[iArr.length - 1];
                break;
            }
        }
        return i2 * 1024;
    }

    public long getCachedRestoreThresholdKb() {
        return this.mCachedRestoreLevel;
    }

    public static void setOomAdj(int i, int i2, int i3) {
        if (i > 0 && i3 != 1001) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            ByteBuffer allocate = ByteBuffer.allocate(16);
            allocate.putInt(1);
            allocate.putInt(i);
            allocate.putInt(i2);
            allocate.putInt(i3);
            writeLmkd(allocate, null);
            long elapsedRealtime2 = SystemClock.elapsedRealtime() - elapsedRealtime;
            if (elapsedRealtime2 > 250) {
                Slog.w("ActivityManager", "SLOW OOM ADJ: " + elapsedRealtime2 + "ms for pid " + i + " = " + i3);
            }
        }
    }

    public static final void remove(int i) {
        if (i <= 0) {
            return;
        }
        ByteBuffer allocate = ByteBuffer.allocate(8);
        allocate.putInt(2);
        allocate.putInt(i);
        writeLmkd(allocate, null);
    }

    public static final Integer getLmkdKillCount(int i, int i2) {
        ByteBuffer allocate = ByteBuffer.allocate(12);
        ByteBuffer allocate2 = ByteBuffer.allocate(8);
        allocate.putInt(4);
        allocate.putInt(i);
        allocate.putInt(i2);
        allocate2.putInt(4);
        allocate2.rewind();
        if (writeLmkd(allocate, allocate2) && allocate2.getInt() == 4) {
            return new Integer(allocate2.getInt());
        }
        return null;
    }

    public boolean onLmkdConnect(OutputStream outputStream) {
        try {
            ByteBuffer allocate = ByteBuffer.allocate(4);
            allocate.putInt(3);
            outputStream.write(allocate.array(), 0, allocate.position());
            if (this.mOomLevelsSet) {
                ByteBuffer allocate2 = ByteBuffer.allocate(((this.mOomAdj.length * 2) + 1) * 4);
                allocate2.putInt(0);
                for (int i = 0; i < this.mOomAdj.length; i++) {
                    allocate2.putInt((this.mOomMinFree[i] * 1024) / IInstalld.FLAG_USE_QUOTA);
                    allocate2.putInt(this.mOomAdj[i]);
                }
                outputStream.write(allocate2.array(), 0, allocate2.position());
            }
            ByteBuffer allocate3 = ByteBuffer.allocate(8);
            allocate3.putInt(5);
            allocate3.putInt(0);
            outputStream.write(allocate3.array(), 0, allocate3.position());
            ByteBuffer allocate4 = ByteBuffer.allocate(8);
            allocate4.putInt(5);
            allocate4.putInt(1);
            outputStream.write(allocate4.array(), 0, allocate4.position());
            return true;
        } catch (IOException unused) {
            return false;
        }
    }

    public static boolean writeLmkd(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) {
        if (!sLmkdConnection.isConnected()) {
            KillHandler killHandler = sKillHandler;
            killHandler.sendMessage(killHandler.obtainMessage(4001));
            if (!sLmkdConnection.waitForConnection(BackupAgentTimeoutParameters.DEFAULT_QUOTA_EXCEEDED_TIMEOUT_MILLIS)) {
                return false;
            }
        }
        return sLmkdConnection.exchange(byteBuffer, byteBuffer2);
    }

    public static void killProcessGroup(int i, int i2) {
        KillHandler killHandler = sKillHandler;
        if (killHandler != null) {
            killHandler.sendMessage(killHandler.obtainMessage(4000, i, i2));
            return;
        }
        Slog.w("ActivityManager", "Asked to kill process group before system bringup!");
        Process.killProcessGroup(i, i2);
    }

    @GuardedBy({"mService"})
    public ProcessRecord getProcessRecordLocked(String str, int i) {
        if (i == 1000) {
            SparseArray sparseArray = (SparseArray) this.mProcessNames.getMap().get(str);
            if (sparseArray == null) {
                return null;
            }
            int size = sparseArray.size();
            for (int i2 = 0; i2 < size; i2++) {
                int keyAt = sparseArray.keyAt(i2);
                if (UserHandle.isCore(keyAt) && UserHandle.isSameUser(keyAt, i)) {
                    return (ProcessRecord) sparseArray.valueAt(i2);
                }
            }
        }
        return (ProcessRecord) this.mProcessNames.get(str, i);
    }

    public void getMemoryInfo(ActivityManager.MemoryInfo memoryInfo) {
        long memLevel = getMemLevel(600);
        long memLevel2 = getMemLevel(900);
        memoryInfo.advertisedMem = Process.getAdvertisedMem();
        memoryInfo.availMem = Process.getFreeMemory();
        memoryInfo.totalMem = Process.getTotalMemory();
        memoryInfo.threshold = memLevel;
        memoryInfo.lowMemory = memoryInfo.availMem < memLevel + ((memLevel2 - memLevel) / 2);
        memoryInfo.hiddenAppThreshold = memLevel2;
        memoryInfo.secondaryServerThreshold = getMemLevel(500);
        memoryInfo.visibleAppThreshold = getMemLevel(100);
        memoryInfo.foregroundAppThreshold = getMemLevel(0);
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public ProcessRecord findAppProcessLOSP(IBinder iBinder, String str) {
        int size = this.mProcessNames.getMap().size();
        for (int i = 0; i < size; i++) {
            SparseArray sparseArray = (SparseArray) this.mProcessNames.getMap().valueAt(i);
            int size2 = sparseArray.size();
            for (int i2 = 0; i2 < size2; i2++) {
                ProcessRecord processRecord = (ProcessRecord) sparseArray.valueAt(i2);
                IApplicationThread thread = processRecord.getThread();
                if (thread != null && thread.asBinder() == iBinder) {
                    return processRecord;
                }
            }
        }
        Slog.w("ActivityManager", "Can't find mystery application for " + str + " from pid=" + Binder.getCallingPid() + " uid=" + Binder.getCallingUid() + ": " + iBinder);
        return null;
    }

    public final void checkSlow(long j, String str) {
        long uptimeMillis = SystemClock.uptimeMillis() - j;
        if (uptimeMillis > 50) {
            Slog.w("ActivityManager", "Slow operation: " + uptimeMillis + "ms so far, now at " + str);
        }
    }

    public final int[] computeGidsForProcess(int i, int i2, int[] iArr, boolean z) {
        ArrayList arrayList = new ArrayList(iArr.length + 5);
        int sharedAppGid = UserHandle.getSharedAppGid(UserHandle.getAppId(i2));
        int cacheAppGid = UserHandle.getCacheAppGid(UserHandle.getAppId(i2));
        int userGid = UserHandle.getUserGid(UserHandle.getUserId(i2));
        for (int i3 : iArr) {
            arrayList.add(Integer.valueOf(i3));
        }
        if (sharedAppGid != -1) {
            arrayList.add(Integer.valueOf(sharedAppGid));
        }
        if (cacheAppGid != -1) {
            arrayList.add(Integer.valueOf(cacheAppGid));
        }
        if (userGid != -1) {
            arrayList.add(Integer.valueOf(userGid));
        }
        if (i == 4 || i == 3) {
            arrayList.add(Integer.valueOf(UserHandle.getUid(UserHandle.getUserId(i2), 1015)));
            arrayList.add(1078);
            arrayList.add(1079);
        }
        if (i == 2) {
            arrayList.add(1079);
        }
        if (i == 3) {
            arrayList.add(1023);
        }
        if (z) {
            arrayList.add(1077);
        }
        int size = arrayList.size();
        int[] iArr2 = new int[size];
        for (int i4 = 0; i4 < size; i4++) {
            iArr2[i4] = ((Integer) arrayList.get(i4)).intValue();
        }
        return iArr2;
    }

    @GuardedBy({"mService"})
    public boolean startProcessLocked(ProcessRecord processRecord, HostingRecord hostingRecord, int i, boolean z, boolean z2, String str) {
        boolean z3;
        String str2;
        int i2;
        long j;
        int externalStorageMountMode;
        int[] computeGidsForProcess;
        ArraySet arraySet;
        int i3;
        String str3;
        String str4;
        ApplicationInfo applicationInfo;
        ApplicationInfo clientInfoForSdkSandbox;
        String str5 = "";
        if (processRecord.isPendingStart()) {
            return true;
        }
        long uptimeMillis = SystemClock.uptimeMillis();
        long elapsedRealtime = SystemClock.elapsedRealtime();
        boolean z4 = false;
        if (processRecord.getPid() > 0 && processRecord.getPid() != ActivityManagerService.MY_PID) {
            checkSlow(uptimeMillis, "startProcess: removing from pids map");
            this.mService.removePidLocked(processRecord.getPid(), processRecord);
            processRecord.setBindMountPending(false);
            this.mService.mHandler.removeMessages(20, processRecord);
            checkSlow(uptimeMillis, "startProcess: done removing from pids map");
            processRecord.setPid(0);
            processRecord.setStartSeq(0L);
        }
        processRecord.unlinkDeathRecipient();
        processRecord.setDyingPid(0);
        this.mService.mProcessesOnHold.remove(processRecord);
        checkSlow(uptimeMillis, "startProcess: starting to update cpu stats");
        this.mService.updateCpuStats();
        checkSlow(uptimeMillis, "startProcess: done updating cpu stats");
        try {
            int userId = UserHandle.getUserId(processRecord.uid);
            try {
                try {
                    AppGlobals.getPackageManager().checkPackageStartable(processRecord.info.packageName, userId);
                    i2 = processRecord.uid;
                } catch (RemoteException e) {
                    throw e.rethrowAsRuntimeException();
                }
            } catch (RuntimeException e2) {
                e = e2;
            }
            try {
                if (processRecord.isolated) {
                    j = uptimeMillis;
                    computeGidsForProcess = null;
                    externalStorageMountMode = 0;
                } else {
                    try {
                        checkSlow(uptimeMillis, "startProcess: getting gids from package manager");
                        IPackageManager packageManager = AppGlobals.getPackageManager();
                        j = uptimeMillis;
                        int[] packageGids = packageManager.getPackageGids(processRecord.info.packageName, 268435456L, processRecord.userId);
                        StorageManagerInternal storageManagerInternal = (StorageManagerInternal) LocalServices.getService(StorageManagerInternal.class);
                        externalStorageMountMode = storageManagerInternal.getExternalStorageMountMode(i2, processRecord.info.packageName);
                        boolean hasExternalStorageAccess = storageManagerInternal.hasExternalStorageAccess(i2, processRecord.info.packageName);
                        if (this.mService.isAppFreezerExemptInstPkg() && packageManager.checkPermission("android.permission.INSTALL_PACKAGES", processRecord.info.packageName, userId) == 0) {
                            Slog.i("ActivityManager", processRecord.info.packageName + " is exempt from freezer");
                            processRecord.mOptRecord.setFreezeExempt(true);
                        }
                        ProcessInfo processInfo = processRecord.processInfo;
                        if (processInfo != null && (arraySet = processInfo.deniedPermissions) != null) {
                            for (int size = arraySet.size() - 1; size >= 0; size--) {
                                int[] permissionGids = this.mService.mPackageManagerInt.getPermissionGids((String) processRecord.processInfo.deniedPermissions.valueAt(size), processRecord.userId);
                                if (permissionGids != null) {
                                    for (int i4 : permissionGids) {
                                        packageGids = ArrayUtils.removeInt(packageGids, i4);
                                    }
                                }
                            }
                        }
                        computeGidsForProcess = computeGidsForProcess(externalStorageMountMode, i2, packageGids, hasExternalStorageAccess);
                    } catch (RemoteException e3) {
                        throw e3.rethrowAsRuntimeException();
                    }
                }
                processRecord.setMountMode(externalStorageMountMode);
                long j2 = j;
                checkSlow(j2, "startProcess: building args");
                int i5 = processRecord.getWindowProcessController().isFactoryTestProcess() ? 0 : i2;
                boolean z5 = (processRecord.info.flags & 2) != 0;
                boolean isProfileableByShell = processRecord.info.isProfileableByShell();
                boolean isProfileable = processRecord.info.isProfileable();
                if (processRecord.isSdkSandbox && (clientInfoForSdkSandbox = processRecord.getClientInfoForSdkSandbox()) != null) {
                    z5 |= (clientInfoForSdkSandbox.flags & 2) != 0;
                    isProfileableByShell |= clientInfoForSdkSandbox.isProfileableByShell();
                    isProfileable |= clientInfoForSdkSandbox.isProfileable();
                }
                if (!z5) {
                    i3 = 0;
                } else if (Settings.Global.getInt(this.mService.mContext.getContentResolver(), "art_verifier_verify_debuggable", 1) == 0) {
                    Slog.w("ActivityManager", processRecord + ": ART verification disabled");
                    i3 = 771;
                } else {
                    i3 = 259;
                }
                if ((processRecord.info.flags & 16384) != 0 || this.mService.mSafeMode) {
                    i3 |= 8;
                }
                if (isProfileableByShell) {
                    i3 |= 32768;
                }
                if (isProfileable) {
                    i3 |= 16777216;
                }
                if ("1".equals(SystemProperties.get("debug.checkjni"))) {
                    i3 |= 2;
                }
                String str6 = SystemProperties.get("debug.generate-debug-info");
                if ("1".equals(str6) || "true".equals(str6)) {
                    i3 |= 32;
                }
                String str7 = SystemProperties.get("dalvik.vm.minidebuginfo");
                if ("1".equals(str7) || "true".equals(str7)) {
                    i3 |= IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES;
                }
                if ("1".equals(SystemProperties.get("debug.jni.logging"))) {
                    i3 |= 16;
                }
                if ("1".equals(SystemProperties.get("debug.assert"))) {
                    i3 |= 4;
                }
                if ("1".equals(SystemProperties.get("debug.ignoreappsignalhandler"))) {
                    i3 |= IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES;
                }
                String str8 = this.mService.mNativeDebuggingApp;
                if (str8 == null || !str8.equals(processRecord.processName)) {
                    str3 = null;
                } else {
                    i3 = i3 | 64 | 32 | 128;
                    str3 = null;
                    this.mService.mNativeDebuggingApp = null;
                }
                if (processRecord.info.isEmbeddedDexUsed()) {
                    i3 |= 1024;
                }
                if (!z && !this.mService.mHiddenApiBlacklist.isDisabled()) {
                    processRecord.info.maybeUpdateHiddenApiEnforcementPolicy(this.mService.mHiddenApiBlacklist.getPolicy());
                    int hiddenApiEnforcementPolicy = processRecord.info.getHiddenApiEnforcementPolicy();
                    int i6 = hiddenApiEnforcementPolicy << Zygote.API_ENFORCEMENT_POLICY_SHIFT;
                    if ((i6 & 12288) != i6) {
                        throw new IllegalStateException("Invalid API policy: " + hiddenApiEnforcementPolicy);
                    }
                    i3 |= i6;
                    if (z2) {
                        i3 |= 262144;
                    }
                }
                String str9 = SystemProperties.get("persist.device_config.runtime_native.use_app_image_startup_cache", "");
                if (!TextUtils.isEmpty(str9) && !str9.equals("false")) {
                    i3 |= 65536;
                }
                if (z5) {
                    String str10 = processRecord.info.nativeLibraryDir + "/wrap.sh";
                    StrictMode.ThreadPolicy allowThreadDiskReads = StrictMode.allowThreadDiskReads();
                    String str11 = new File(str10).exists() ? "/system/bin/logwrapper " + str10 : str3;
                    StrictMode.setThreadPolicy(allowThreadDiskReads);
                    str4 = str11;
                } else {
                    str4 = str3;
                }
                String str12 = str != null ? str : processRecord.info.primaryCpuAbi;
                if (str12 == null) {
                    z4 = false;
                    str12 = Build.SUPPORTED_ABIS[0];
                } else {
                    z4 = false;
                }
                String str13 = str12;
                String instructionSet = processRecord.info.primaryCpuAbi != null ? VMRuntime.getInstructionSet(str13) : str3;
                processRecord.setGids(computeGidsForProcess);
                processRecord.setRequiredAbi(str13);
                processRecord.setInstructionSet(instructionSet);
                if (hostingRecord.getDefiningPackageName() != null) {
                    applicationInfo = new ApplicationInfo(processRecord.info);
                    applicationInfo.packageName = hostingRecord.getDefiningPackageName();
                    applicationInfo.uid = i5;
                } else {
                    applicationInfo = processRecord.info;
                }
                int memorySafetyRuntimeFlags = i3 | Zygote.getMemorySafetyRuntimeFlags(applicationInfo, processRecord.processInfo, instructionSet, this.mPlatformCompat);
                if (TextUtils.isEmpty(processRecord.info.seInfoUser)) {
                    Slog.wtf("ActivityManager", "SELinux tag not defined", new IllegalStateException("SELinux tag not defined for " + processRecord.info.packageName + " (uid " + processRecord.uid + ")"));
                }
                StringBuilder sb = new StringBuilder();
                sb.append(processRecord.info.seInfo);
                if (!TextUtils.isEmpty(processRecord.info.seInfoUser)) {
                    str5 = processRecord.info.seInfoUser;
                }
                sb.append(str5);
                return startProcessLocked(hostingRecord, "android.app.ActivityThread", processRecord, i5, computeGidsForProcess, memorySafetyRuntimeFlags, i, externalStorageMountMode, sb.toString(), str13, instructionSet, str4, j2, elapsedRealtime);
            } catch (RuntimeException e4) {
                e = e4;
                str2 = "ActivityManager";
                z3 = false;
                Slog.e(str2, "Failure starting process " + processRecord.processName, e);
                this.mService.forceStopPackageLocked(processRecord.info.packageName, UserHandle.getAppId(processRecord.uid), false, false, true, false, false, processRecord.userId, "start failure");
                return z3;
            }
        } catch (RuntimeException e5) {
            e = e5;
            z3 = z4;
            str2 = "ActivityManager";
        }
    }

    @GuardedBy({"mService"})
    public boolean startProcessLocked(HostingRecord hostingRecord, final String str, final ProcessRecord processRecord, int i, final int[] iArr, final int i2, final int i3, final int i4, String str2, final String str3, final String str4, final String str5, long j, long j2) {
        processRecord.setPendingStart(true);
        processRecord.setRemoved(false);
        synchronized (this.mProcLock) {
            try {
                ActivityManagerService.boostPriorityForProcLockedSection();
                processRecord.setKilledByAm(false);
                processRecord.setKilled(false);
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        ActivityManagerService.resetPriorityAfterProcLockedSection();
        if (processRecord.getStartSeq() != 0) {
            Slog.wtf("ActivityManager", "startProcessLocked processName:" + processRecord.processName + " with non-zero startSeq:" + processRecord.getStartSeq());
        }
        if (processRecord.getPid() != 0) {
            Slog.wtf("ActivityManager", "startProcessLocked processName:" + processRecord.processName + " with non-zero pid:" + processRecord.getPid());
        }
        processRecord.setDisabledCompatChanges(null);
        PlatformCompat platformCompat = this.mPlatformCompat;
        if (platformCompat != null) {
            processRecord.setDisabledCompatChanges(platformCompat.getDisabledChanges(processRecord.info));
        }
        final long j3 = this.mProcStartSeqCounter + 1;
        this.mProcStartSeqCounter = j3;
        processRecord.setStartSeq(j3);
        processRecord.setStartParams(i, hostingRecord, str2, j, j2);
        processRecord.setUsingWrapper((str5 == null && Zygote.getWrapProperty(processRecord.processName) == null) ? false : true);
        this.mPendingStarts.put(j3, processRecord);
        ActivityManagerService activityManagerService = this.mService;
        if (activityManagerService.mConstants.FLAG_PROCESS_START_ASYNC) {
            activityManagerService.mProcStartHandler.post(new Runnable() { // from class: com.android.server.am.ProcessList$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    ProcessList.this.lambda$startProcessLocked$0(processRecord, str, iArr, i2, i3, i4, str3, str4, str5, j3);
                }
            });
            return true;
        }
        try {
            Process.ProcessStartResult startProcess = startProcess(hostingRecord, str, processRecord, i, iArr, i2, i3, i4, str2, str3, str4, str5, j);
            handleProcessStartedLocked(processRecord, startProcess.pid, startProcess.usingWrapper, j3, false);
        } catch (RuntimeException e) {
            Slog.e("ActivityManager", "Failure starting process " + processRecord.processName, e);
            processRecord.setPendingStart(false);
            this.mService.forceStopPackageLocked(processRecord.info.packageName, UserHandle.getAppId(processRecord.uid), false, false, true, false, false, processRecord.userId, "start failure");
        }
        return processRecord.getPid() > 0;
    }

    /* renamed from: handleProcessStart */
    public final void lambda$startProcessLocked$0(final ProcessRecord processRecord, final String str, final int[] iArr, final int i, final int i2, final int i3, final String str2, final String str3, final String str4, final long j) {
        Runnable runnable = new Runnable() { // from class: com.android.server.am.ProcessList$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                ProcessList.this.lambda$handleProcessStart$1(processRecord, str, iArr, i, i2, i3, str2, str3, str4, j);
            }
        };
        ProcessRecord processRecord2 = processRecord.mPredecessor;
        if (processRecord2 != null && processRecord2.getDyingPid() > 0) {
            handleProcessStartWithPredecessor(processRecord2, runnable);
        } else {
            runnable.run();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:47:0x0062 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r3v0 */
    /* JADX WARN: Type inference failed for: r3v3, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r3v4 */
    /* JADX WARN: Type inference failed for: r4v0 */
    /* JADX WARN: Type inference failed for: r4v4, types: [com.android.server.am.ProcessRecord] */
    /* JADX WARN: Type inference failed for: r4v5 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public /* synthetic */ void lambda$handleProcessStart$1(ProcessRecord processRecord, String str, int[] iArr, int i, int i2, int i3, String str2, String str3, String str4, long j) {
        long j2;
        ProcessList processList;
        ProcessRecord processRecord2;
        Process.ProcessStartResult startProcess;
        try {
            processList = this;
            processRecord2 = str;
            j2 = processRecord;
            try {
                startProcess = processList.startProcess(processRecord.getHostingRecord(), processRecord2, j2, processRecord.getStartUid(), iArr, i, i2, i3, processRecord.getSeInfo(), str2, str3, str4, processRecord.getStartTime());
                try {
                } catch (RuntimeException e) {
                    e = e;
                    processRecord2 = processRecord;
                    j2 = j;
                    synchronized (processList.mService) {
                        try {
                            ActivityManagerService.boostPriorityForLockedSection();
                            Slog.e("ActivityManager", "Failure starting process " + processRecord2.processName, e);
                            processList.mPendingStarts.remove(j2);
                            processRecord2.setPendingStart(false);
                            processList.mService.forceStopPackageLocked(processRecord2.info.packageName, UserHandle.getAppId(processRecord2.uid), false, false, true, false, false, processRecord2.userId, "start failure");
                        } finally {
                            ActivityManagerService.resetPriorityAfterLockedSection();
                        }
                    }
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    return;
                }
            } catch (RuntimeException e2) {
                e = e2;
                processList = this;
            }
            try {
                try {
                    synchronized (processList.mService) {
                        try {
                            ActivityManagerService.boostPriorityForLockedSection();
                            processList.handleProcessStartedLocked(processRecord, startProcess, j);
                            ActivityManagerService.resetPriorityAfterLockedSection();
                        } catch (Throwable th) {
                            th = th;
                            throw th;
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (RuntimeException e3) {
                e = e3;
                synchronized (processList.mService) {
                }
            }
        } catch (RuntimeException e4) {
            e = e4;
            j2 = j;
            processList = this;
            processRecord2 = processRecord;
        }
    }

    public final void handleProcessStartWithPredecessor(ProcessRecord processRecord, Runnable runnable) {
        if (processRecord.mSuccessorStartRunnable != null) {
            Slog.wtf("ActivityManager", "We've been watching for the death of " + processRecord);
            return;
        }
        processRecord.mSuccessorStartRunnable = runnable;
        ProcStartHandler procStartHandler = this.mService.mProcStartHandler;
        procStartHandler.sendMessageDelayed(procStartHandler.obtainMessage(2, processRecord), this.mService.mConstants.mProcessKillTimeoutMs);
    }

    /* renamed from: com.android.server.am.ProcessList$ProcStartHandler */
    /* loaded from: classes.dex */
    public static final class ProcStartHandler extends Handler {
        public final ActivityManagerService mService;

        public ProcStartHandler(ActivityManagerService activityManagerService, Looper looper) {
            super(looper);
            this.mService = activityManagerService;
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                this.mService.mProcessList.handlePredecessorProcDied((ProcessRecord) message.obj);
            } else if (i != 2) {
            } else {
                synchronized (this.mService) {
                    try {
                        ActivityManagerService.boostPriorityForLockedSection();
                        this.mService.handleProcessStartOrKillTimeoutLocked((ProcessRecord) message.obj, true);
                    } catch (Throwable th) {
                        ActivityManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
                ActivityManagerService.resetPriorityAfterLockedSection();
            }
        }
    }

    public final void handlePredecessorProcDied(ProcessRecord processRecord) {
        Runnable runnable = processRecord.mSuccessorStartRunnable;
        if (runnable != null) {
            processRecord.mSuccessorStartRunnable = null;
            runnable.run();
        }
    }

    @GuardedBy({"mService"})
    public void killAppZygoteIfNeededLocked(AppZygote appZygote, boolean z) {
        ApplicationInfo appInfo = appZygote.getAppInfo();
        ArrayList<ProcessRecord> arrayList = this.mAppZygoteProcesses.get(appZygote);
        if (arrayList != null) {
            if (z || arrayList.size() == 0) {
                this.mAppZygotes.remove(appInfo.processName, appInfo.uid);
                this.mAppZygoteProcesses.remove(appZygote);
                this.mAppIsolatedUidRangeAllocator.freeUidRangeLocked(appInfo);
                appZygote.stopZygote();
            }
        }
    }

    @GuardedBy({"mService"})
    public final void removeProcessFromAppZygoteLocked(ProcessRecord processRecord) {
        IsolatedUidRange isolatedUidRangeLocked = this.mAppIsolatedUidRangeAllocator.getIsolatedUidRangeLocked(processRecord.info.processName, processRecord.getHostingRecord().getDefiningUid());
        if (isolatedUidRangeLocked != null) {
            isolatedUidRangeLocked.freeIsolatedUidLocked(processRecord.uid);
        }
        AppZygote appZygote = (AppZygote) this.mAppZygotes.get(processRecord.info.processName, processRecord.getHostingRecord().getDefiningUid());
        if (appZygote != null) {
            ArrayList<ProcessRecord> arrayList = this.mAppZygoteProcesses.get(appZygote);
            arrayList.remove(processRecord);
            if (arrayList.size() == 0) {
                this.mService.mHandler.removeMessages(71);
                if (processRecord.isRemoved()) {
                    killAppZygoteIfNeededLocked(appZygote, false);
                    return;
                }
                Message obtainMessage = this.mService.mHandler.obtainMessage(71);
                obtainMessage.obj = appZygote;
                this.mService.mHandler.sendMessageDelayed(obtainMessage, 5000L);
            }
        }
    }

    public final AppZygote createAppZygoteForProcessIfNeeded(ProcessRecord processRecord) {
        AppZygote appZygote;
        ArrayList<ProcessRecord> arrayList;
        synchronized (this.mService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                int definingUid = processRecord.getHostingRecord().getDefiningUid();
                appZygote = (AppZygote) this.mAppZygotes.get(processRecord.info.processName, definingUid);
                if (appZygote == null) {
                    IsolatedUidRange isolatedUidRangeLocked = this.mAppIsolatedUidRangeAllocator.getIsolatedUidRangeLocked(processRecord.info.processName, processRecord.getHostingRecord().getDefiningUid());
                    int userId = UserHandle.getUserId(definingUid);
                    int uid = UserHandle.getUid(userId, isolatedUidRangeLocked.mFirstUid);
                    int uid2 = UserHandle.getUid(userId, isolatedUidRangeLocked.mLastUid);
                    ApplicationInfo applicationInfo = new ApplicationInfo(processRecord.info);
                    applicationInfo.packageName = processRecord.getHostingRecord().getDefiningPackageName();
                    applicationInfo.uid = definingUid;
                    AppZygote appZygote2 = new AppZygote(applicationInfo, processRecord.processInfo, definingUid, uid, uid2);
                    this.mAppZygotes.put(processRecord.info.processName, definingUid, appZygote2);
                    arrayList = new ArrayList<>();
                    this.mAppZygoteProcesses.put(appZygote2, arrayList);
                    appZygote = appZygote2;
                } else {
                    this.mService.mHandler.removeMessages(71, appZygote);
                    arrayList = this.mAppZygoteProcesses.get(appZygote);
                }
                arrayList.add(processRecord);
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
        return appZygote;
    }

    public final Map<String, Pair<String, Long>> getPackageAppDataInfoMap(PackageManagerInternal packageManagerInternal, String[] strArr, int i) {
        ArrayMap arrayMap = new ArrayMap(strArr.length);
        int userId = UserHandle.getUserId(i);
        for (String str : strArr) {
            PackageStateInternal packageStateInternal = packageManagerInternal.getPackageStateInternal(str);
            if (packageStateInternal == null) {
                Slog.w("ActivityManager", "Unknown package:" + str);
            } else {
                String volumeUuid = packageStateInternal.getVolumeUuid();
                long ceDataInode = packageStateInternal.getUserStateOrDefault(userId).getCeDataInode();
                if (ceDataInode == 0) {
                    Slog.w("ActivityManager", str + " inode == 0 (b/152760674)");
                    return null;
                }
                arrayMap.put(str, Pair.create(volumeUuid, Long.valueOf(ceDataInode)));
            }
        }
        return arrayMap;
    }

    public final boolean needsStorageDataIsolation(StorageManagerInternal storageManagerInternal, ProcessRecord processRecord) {
        int mountMode = processRecord.getMountMode();
        return (!this.mVoldAppDataIsolationEnabled || !UserHandle.isApp(processRecord.uid) || storageManagerInternal.isExternalStorageService(processRecord.uid) || mountMode == 4 || mountMode == 3 || mountMode == 2 || mountMode == 0) ? false : true;
    }

    /* JADX WARN: Code restructure failed: missing block: B:84:0x0287, code lost:
        r28.prepareStorageDirs(r31, r26.keySet(), r36.processName);
     */
    /* JADX WARN: Removed duplicated region for block: B:50:0x00e7  */
    /* JADX WARN: Removed duplicated region for block: B:51:0x00ec  */
    /* JADX WARN: Removed duplicated region for block: B:54:0x00f8 A[Catch: all -> 0x02a2, TryCatch #1 {all -> 0x02a2, blocks: (B:30:0x009b, B:31:0x00a3, B:36:0x00b5, B:40:0x00c0, B:43:0x00d5, B:48:0x00e3, B:52:0x00f0, B:54:0x00f8, B:56:0x0106, B:57:0x0108, B:60:0x0112, B:66:0x011c, B:67:0x0121, B:69:0x0127, B:46:0x00de, B:58:0x0109, B:59:0x0111), top: B:96:0x009b }] */
    /* JADX WARN: Removed duplicated region for block: B:69:0x0127 A[Catch: all -> 0x02a2, TRY_LEAVE, TryCatch #1 {all -> 0x02a2, blocks: (B:30:0x009b, B:31:0x00a3, B:36:0x00b5, B:40:0x00c0, B:43:0x00d5, B:48:0x00e3, B:52:0x00f0, B:54:0x00f8, B:56:0x0106, B:57:0x0108, B:60:0x0112, B:66:0x011c, B:67:0x0121, B:69:0x0127, B:46:0x00de, B:58:0x0109, B:59:0x0111), top: B:96:0x009b }] */
    /* JADX WARN: Removed duplicated region for block: B:73:0x018b A[Catch: all -> 0x02a0, TryCatch #4 {all -> 0x02a0, blocks: (B:71:0x0183, B:78:0x0252, B:81:0x025d, B:82:0x0284, B:84:0x0287, B:85:0x0294, B:73:0x018b, B:75:0x0199, B:76:0x01fa), top: B:100:0x0125 }] */
    /* JADX WARN: Removed duplicated region for block: B:78:0x0252 A[Catch: all -> 0x02a0, TryCatch #4 {all -> 0x02a0, blocks: (B:71:0x0183, B:78:0x0252, B:81:0x025d, B:82:0x0284, B:84:0x0287, B:85:0x0294, B:73:0x018b, B:75:0x0199, B:76:0x01fa), top: B:100:0x0125 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final Process.ProcessStartResult startProcess(HostingRecord hostingRecord, String str, ProcessRecord processRecord, int i, int[] iArr, int i2, int i3, int i4, String str2, String str3, String str4, String str5, long j) {
        long j2;
        boolean isTopApp;
        String[] sharedUserPackagesForPackage;
        Map<String, Pair<String, Long>> packageAppDataInfoMap;
        Map<String, Pair<String, Long>> packageAppDataInfoMap2;
        boolean z;
        int userId;
        StorageManagerInternal storageManagerInternal;
        boolean z2;
        Map<String, Pair<String, Long>> map;
        Map<String, Pair<String, Long>> map2;
        AppStateTracker appStateTracker;
        boolean z3;
        StorageManagerInternal storageManagerInternal2;
        int i5;
        long j3;
        Process.ProcessStartResult start;
        boolean z4;
        try {
            Trace.traceBegin(64L, "Start proc: " + processRecord.processName);
            checkSlow(j, "startProcess: asking zygote to start proc");
            isTopApp = hostingRecord.isTopApp();
            if (isTopApp) {
                processRecord.mState.setHasForegroundActivities(true);
            }
            boolean z5 = this.mAppDataIsolationEnabled && (UserHandle.isApp(processRecord.uid) || UserHandle.isIsolated(processRecord.uid) || processRecord.isSdkSandbox) && this.mPlatformCompat.isChangeEnabled(143937733L, processRecord.info);
            PackageManagerInternal packageManagerInternal = this.mService.getPackageManagerInternal();
            if (processRecord.isSdkSandbox) {
                sharedUserPackagesForPackage = new String[]{processRecord.sdkSandboxClientAppPackage};
            } else {
                sharedUserPackagesForPackage = packageManagerInternal.getSharedUserPackagesForPackage(processRecord.info.packageName, processRecord.userId);
                if (sharedUserPackagesForPackage.length == 0) {
                    sharedUserPackagesForPackage = new String[]{processRecord.info.packageName};
                }
            }
            boolean hasAppStorage = hasAppStorage(packageManagerInternal, processRecord.info.packageName);
            packageAppDataInfoMap = getPackageAppDataInfoMap(packageManagerInternal, sharedUserPackagesForPackage, i);
            if (packageAppDataInfoMap == null) {
                z5 = false;
            }
            ArraySet arraySet = new ArraySet(this.mAppDataIsolationAllowlistedApps);
            for (String str6 : sharedUserPackagesForPackage) {
                try {
                    arraySet.remove(str6);
                } catch (Throwable th) {
                    th = th;
                    j2 = 64;
                    Trace.traceEnd(j2);
                    throw th;
                }
            }
            packageAppDataInfoMap2 = getPackageAppDataInfoMap(packageManagerInternal, (String[]) arraySet.toArray(new String[0]), i);
            if (packageAppDataInfoMap2 == null) {
                z5 = false;
            }
            if (hasAppStorage || processRecord.isSdkSandbox) {
                z = z5;
            } else {
                packageAppDataInfoMap2 = null;
                packageAppDataInfoMap = null;
                z = false;
            }
            userId = UserHandle.getUserId(i);
            storageManagerInternal = (StorageManagerInternal) LocalServices.getService(StorageManagerInternal.class);
        } catch (Throwable th2) {
            th = th2;
            j2 = 64;
        }
        try {
            if (needsStorageDataIsolation(storageManagerInternal, processRecord)) {
                if (packageAppDataInfoMap == null || !storageManagerInternal.isFuseMounted(userId)) {
                    processRecord.setBindMountPending(true);
                } else {
                    z2 = true;
                    if (processRecord.isolated) {
                        map = packageAppDataInfoMap2;
                        map2 = packageAppDataInfoMap;
                    } else {
                        map = null;
                        map2 = null;
                    }
                    appStateTracker = this.mService.mServices.mAppStateTracker;
                    if (appStateTracker != null) {
                        boolean isAppBackgroundRestricted = appStateTracker.isAppBackgroundRestricted(processRecord.info.uid, processRecord.info.packageName);
                        if (isAppBackgroundRestricted) {
                            synchronized (this.mService) {
                                ActivityManagerService.boostPriorityForLockedSection();
                                this.mAppsInBackgroundRestricted.add(processRecord);
                            }
                            ActivityManagerService.resetPriorityAfterLockedSection();
                        }
                        processRecord.mState.setBackgroundRestricted(isAppBackgroundRestricted);
                    }
                    if (!hostingRecord.usesWebviewZygote()) {
                        z3 = false;
                        storageManagerInternal2 = storageManagerInternal;
                        j3 = 64;
                        i5 = userId;
                        start = Process.startWebView(str, processRecord.processName, i, i, iArr, i2, i4, processRecord.info.targetSdkVersion, str2, str3, str4, processRecord.info.dataDir, null, processRecord.info.packageName, processRecord.getDisabledCompatChanges(), new String[]{"seq=" + processRecord.getStartSeq()});
                    } else {
                        z3 = false;
                        storageManagerInternal2 = storageManagerInternal;
                        i5 = userId;
                        j3 = 64;
                        if (hostingRecord.usesAppZygote()) {
                            start = createAppZygoteForProcessIfNeeded(processRecord).getProcess().start(str, processRecord.processName, i, i, iArr, i2, i4, processRecord.info.targetSdkVersion, str2, str3, str4, processRecord.info.dataDir, (String) null, processRecord.info.packageName, 0, isTopApp, processRecord.getDisabledCompatChanges(), map2, map, false, false, new String[]{"seq=" + processRecord.getStartSeq()});
                        } else {
                            start = Process.start(str, processRecord.processName, i, i, iArr, i2, i4, processRecord.info.targetSdkVersion, str2, str3, str4, processRecord.info.dataDir, str5, processRecord.info.packageName, i3, isTopApp, processRecord.getDisabledCompatChanges(), map2, map, z, z2, new String[]{"seq=" + processRecord.getStartSeq()});
                            z4 = true;
                            if (!z4 && Process.createProcessGroup(i, start.pid) < 0) {
                                throw new AssertionError("Unable to create process group for " + processRecord.processName + " (" + start.pid + ")");
                            }
                            checkSlow(j, "startProcess: returned from zygote!");
                            Trace.traceEnd(j3);
                            return start;
                        }
                    }
                    z4 = z3;
                    if (!z4) {
                        throw new AssertionError("Unable to create process group for " + processRecord.processName + " (" + start.pid + ")");
                    }
                    checkSlow(j, "startProcess: returned from zygote!");
                    Trace.traceEnd(j3);
                    return start;
                }
            }
            if (!hostingRecord.usesWebviewZygote()) {
            }
            z4 = z3;
            if (!z4) {
            }
            checkSlow(j, "startProcess: returned from zygote!");
            Trace.traceEnd(j3);
            return start;
        } catch (Throwable th3) {
            th = th3;
            Trace.traceEnd(j2);
            throw th;
        }
        z2 = false;
        if (processRecord.isolated) {
        }
        appStateTracker = this.mService.mServices.mAppStateTracker;
        if (appStateTracker != null) {
        }
    }

    public final boolean hasAppStorage(PackageManagerInternal packageManagerInternal, String str) {
        AndroidPackage androidPackage = packageManagerInternal.getPackage(str);
        if (androidPackage == null) {
            Slog.w("ActivityManager", "Unknown package " + str);
            return false;
        }
        PackageManager.Property property = androidPackage.getProperties().get("android.internal.PROPERTY_NO_APP_DATA_STORAGE");
        return property == null || !property.getBoolean();
    }

    @GuardedBy({"mService"})
    public void startProcessLocked(ProcessRecord processRecord, HostingRecord hostingRecord, int i) {
        startProcessLocked(processRecord, hostingRecord, i, null);
    }

    @GuardedBy({"mService"})
    public boolean startProcessLocked(ProcessRecord processRecord, HostingRecord hostingRecord, int i, String str) {
        return startProcessLocked(processRecord, hostingRecord, i, false, false, str);
    }

    @GuardedBy({"mService"})
    public ProcessRecord startProcessLocked(String str, ApplicationInfo applicationInfo, boolean z, int i, HostingRecord hostingRecord, int i2, boolean z2, boolean z3, int i3, boolean z4, int i4, String str2, String str3, String str4, String[] strArr, Runnable runnable) {
        ProcessRecord processRecord;
        ProcessRecord processRecord2;
        ProcessRecord processRecord3;
        long uptimeMillis = SystemClock.uptimeMillis();
        if (z3) {
            processRecord = null;
        } else {
            processRecord = getProcessRecordLocked(str, applicationInfo.uid);
            checkSlow(uptimeMillis, "startProcess: after getProcessRecord");
            if ((i & 4) != 0) {
                if (this.mService.mAppErrors.isBadProcess(str, applicationInfo.uid)) {
                    return null;
                }
            } else {
                this.mService.mAppErrors.resetProcessCrashTime(str, applicationInfo.uid);
                if (this.mService.mAppErrors.isBadProcess(str, applicationInfo.uid)) {
                    EventLog.writeEvent(30016, Integer.valueOf(UserHandle.getUserId(applicationInfo.uid)), Integer.valueOf(applicationInfo.uid), applicationInfo.processName);
                    this.mService.mAppErrors.clearBadProcess(str, applicationInfo.uid);
                    if (processRecord != null) {
                        processRecord.mErrorState.setBad(false);
                    }
                }
            }
        }
        if (processRecord == null || processRecord.getPid() <= 0) {
            if (z3) {
                processRecord2 = null;
            } else {
                ProcessRecord processRecord4 = (ProcessRecord) this.mDyingProcesses.get(str, applicationInfo.uid);
                if (processRecord4 != null) {
                    if (processRecord != null) {
                        processRecord.mPredecessor = processRecord4;
                        processRecord4.mSuccessor = processRecord;
                    }
                    Slog.w("ActivityManager", processRecord4.toString() + " is attached to a previous process " + processRecord4.getDyingPid());
                }
                processRecord2 = processRecord4;
            }
        } else if ((!z && !processRecord.isKilled()) || processRecord.getThread() == null) {
            processRecord.addPackage(applicationInfo.packageName, applicationInfo.longVersionCode, this.mService.mProcessStats);
            checkSlow(uptimeMillis, "startProcess: done, added package to proc");
            return processRecord;
        } else {
            checkSlow(uptimeMillis, "startProcess: bad proc running, killing");
            killProcessGroup(processRecord.uid, processRecord.getPid());
            checkSlow(uptimeMillis, "startProcess: done killing old proc");
            if (!processRecord.isKilled()) {
                Slog.wtf("ActivityManager", processRecord.toString() + " is attached to a previous process");
            } else {
                Slog.w("ActivityManager", processRecord.toString() + " is attached to a previous process");
            }
            processRecord2 = processRecord;
            processRecord = null;
        }
        if (processRecord == null) {
            checkSlow(uptimeMillis, "startProcess: creating new process record");
            ProcessRecord processRecord5 = processRecord2;
            processRecord = newProcessRecordLocked(applicationInfo, str, z3, i3, z4, i4, str2, hostingRecord);
            if (processRecord == null) {
                Slog.w("ActivityManager", "Failed making new process record for " + str + "/" + applicationInfo.uid + " isolated=" + z3);
                return null;
            }
            processRecord3 = null;
            processRecord.mErrorState.setCrashHandler(runnable);
            processRecord.setIsolatedEntryPoint(str4);
            processRecord.setIsolatedEntryPointArgs(strArr);
            if (processRecord5 != null) {
                processRecord.mPredecessor = processRecord5;
                processRecord5.mSuccessor = processRecord;
            }
            checkSlow(uptimeMillis, "startProcess: done creating new process record");
        } else {
            processRecord3 = null;
            processRecord.addPackage(applicationInfo.packageName, applicationInfo.longVersionCode, this.mService.mProcessStats);
            checkSlow(uptimeMillis, "startProcess: added package to existing proc");
        }
        if (!this.mService.mProcessesReady && !this.mService.isAllowedWhileBooting(applicationInfo) && !z2) {
            if (!this.mService.mProcessesOnHold.contains(processRecord)) {
                this.mService.mProcessesOnHold.add(processRecord);
            }
            checkSlow(uptimeMillis, "startProcess: returning with proc on hold");
            return processRecord;
        }
        checkSlow(uptimeMillis, "startProcess: stepping in to startProcess");
        boolean startProcessLocked = startProcessLocked(processRecord, hostingRecord, i2, str3);
        checkSlow(uptimeMillis, "startProcess: done starting proc!");
        return startProcessLocked ? processRecord : processRecord3;
    }

    /* JADX WARN: Removed duplicated region for block: B:32:0x009c  */
    /* JADX WARN: Removed duplicated region for block: B:37:? A[RETURN, SYNTHETIC] */
    @GuardedBy({"mService"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public String isProcStartValidLocked(ProcessRecord processRecord, long j) {
        StringBuilder sb;
        if (processRecord.isKilledByAm()) {
            sb = new StringBuilder();
            sb.append("killedByAm=true;");
        } else {
            sb = null;
        }
        if (this.mProcessNames.get(processRecord.processName, processRecord.uid) != processRecord) {
            if (sb == null) {
                sb = new StringBuilder();
            }
            sb.append("No entry in mProcessNames;");
        }
        if (!processRecord.isPendingStart()) {
            if (sb == null) {
                sb = new StringBuilder();
            }
            sb.append("pendingStart=false;");
        }
        if (processRecord.getStartSeq() > j) {
            if (sb == null) {
                sb = new StringBuilder();
            }
            sb.append("seq=" + processRecord.getStartSeq() + ",expected=" + j + ";");
        }
        try {
            AppGlobals.getPackageManager().checkPackageStartable(processRecord.info.packageName, processRecord.userId);
        } catch (RemoteException unused) {
            if (sb != null) {
                return null;
            }
            return sb.toString();
        } catch (SecurityException e) {
            if (this.mService.mConstants.FLAG_PROCESS_START_ASYNC) {
                if (sb == null) {
                    sb = new StringBuilder();
                }
                sb.append("Package is frozen;");
                if (sb != null) {
                }
            } else {
                throw e;
            }
        }
    }

    @GuardedBy({"mService"})
    public final boolean handleProcessStartedLocked(ProcessRecord processRecord, Process.ProcessStartResult processStartResult, long j) {
        if (this.mPendingStarts.get(j) == null) {
            if (processRecord.getPid() == processStartResult.pid) {
                processRecord.setUsingWrapper(processStartResult.usingWrapper);
                return false;
            }
            return false;
        }
        return handleProcessStartedLocked(processRecord, processStartResult.pid, processStartResult.usingWrapper, j, false);
    }

    @GuardedBy({"mService"})
    public boolean handleProcessStartedLocked(ProcessRecord processRecord, int i, boolean z, long j, boolean z2) {
        ProcessRecord processRecord2;
        this.mPendingStarts.remove(j);
        String isProcStartValidLocked = isProcStartValidLocked(processRecord, j);
        if (isProcStartValidLocked != null) {
            Slog.w("ActivityManager", processRecord + " start not valid, killing pid=" + i + ", " + isProcStartValidLocked);
            processRecord.setPendingStart(false);
            Process.killProcessQuiet(i);
            Process.killProcessGroup(processRecord.uid, processRecord.getPid());
            noteAppKill(processRecord, 13, 13, isProcStartValidLocked);
            return false;
        }
        this.mService.mBatteryStatsService.noteProcessStart(processRecord.processName, processRecord.info.uid);
        checkSlow(processRecord.getStartTime(), "startProcess: done updating battery stats");
        Object[] objArr = new Object[6];
        objArr[0] = Integer.valueOf(UserHandle.getUserId(processRecord.getStartUid()));
        objArr[1] = Integer.valueOf(i);
        objArr[2] = Integer.valueOf(processRecord.getStartUid());
        objArr[3] = processRecord.processName;
        objArr[4] = processRecord.getHostingRecord().getType();
        objArr[5] = processRecord.getHostingRecord().getName() != null ? processRecord.getHostingRecord().getName() : "";
        EventLog.writeEvent(30014, objArr);
        try {
            AppGlobals.getPackageManager().logAppProcessStartIfNeeded(processRecord.info.packageName, processRecord.processName, processRecord.uid, processRecord.getSeInfo(), processRecord.info.sourceDir, i);
        } catch (RemoteException unused) {
        }
        Watchdog.getInstance().processStarted(processRecord.processName, i);
        checkSlow(processRecord.getStartTime(), "startProcess: building log message");
        StringBuilder sb = this.mStringBuilder;
        sb.setLength(0);
        sb.append("Start proc ");
        sb.append(i);
        sb.append(':');
        sb.append(processRecord.processName);
        sb.append('/');
        UserHandle.formatUid(sb, processRecord.getStartUid());
        if (processRecord.getIsolatedEntryPoint() != null) {
            sb.append(" [");
            sb.append(processRecord.getIsolatedEntryPoint());
            sb.append("]");
        }
        sb.append(" for ");
        sb.append(processRecord.getHostingRecord().getType());
        if (processRecord.getHostingRecord().getName() != null) {
            sb.append(" ");
            sb.append(processRecord.getHostingRecord().getName());
        }
        this.mService.reportUidInfoMessageLocked("ActivityManager", sb.toString(), processRecord.getStartUid());
        synchronized (this.mProcLock) {
            try {
                ActivityManagerService.boostPriorityForProcLockedSection();
                processRecord.setPid(i);
                processRecord.setUsingWrapper(z);
                processRecord.setPendingStart(false);
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        ActivityManagerService.resetPriorityAfterProcLockedSection();
        checkSlow(processRecord.getStartTime(), "startProcess: starting to update pids map");
        synchronized (this.mService.mPidsSelfLocked) {
            processRecord2 = this.mService.mPidsSelfLocked.get(i);
        }
        if (processRecord2 != null && !processRecord.isolated) {
            Slog.wtf("ActivityManager", "handleProcessStartedLocked process:" + processRecord.processName + " startSeq:" + processRecord.getStartSeq() + " pid:" + i + " belongs to another existing app:" + processRecord2.processName + " startSeq:" + processRecord2.getStartSeq());
            this.mService.cleanUpApplicationRecordLocked(processRecord2, i, false, false, -1, true, false);
        }
        this.mService.addPidLocked(processRecord);
        synchronized (this.mService.mPidsSelfLocked) {
            if (!z2) {
                Message obtainMessage = this.mService.mHandler.obtainMessage(20);
                obtainMessage.obj = processRecord;
                this.mService.mHandler.sendMessageDelayed(obtainMessage, z ? 1200000L : ActivityManagerService.PROC_START_TIMEOUT);
            }
        }
        checkSlow(processRecord.getStartTime(), "startProcess: done updating pids map");
        return true;
    }

    @GuardedBy({"mService"})
    public void removeLruProcessLocked(ProcessRecord processRecord) {
        int lastIndexOf = this.mLruProcesses.lastIndexOf(processRecord);
        if (lastIndexOf >= 0) {
            synchronized (this.mProcLock) {
                try {
                    ActivityManagerService.boostPriorityForProcLockedSection();
                    if (!processRecord.isKilled()) {
                        if (processRecord.isPersistent()) {
                            Slog.w("ActivityManager", "Removing persistent process that hasn't been killed: " + processRecord);
                        } else {
                            Slog.wtfStack("ActivityManager", "Removing process that hasn't been killed: " + processRecord);
                            if (processRecord.getPid() > 0) {
                                Process.killProcessQuiet(processRecord.getPid());
                                killProcessGroup(processRecord.uid, processRecord.getPid());
                                noteAppKill(processRecord, 13, 16, "hasn't been killed");
                            } else {
                                processRecord.setPendingStart(false);
                            }
                        }
                    }
                    int i = this.mLruProcessActivityStart;
                    if (lastIndexOf < i) {
                        this.mLruProcessActivityStart = i - 1;
                    }
                    int i2 = this.mLruProcessServiceStart;
                    if (lastIndexOf < i2) {
                        this.mLruProcessServiceStart = i2 - 1;
                    }
                    this.mLruProcesses.remove(lastIndexOf);
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterProcLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterProcLockedSection();
        }
        this.mService.removeOomAdjTargetLocked(processRecord, true);
    }

    @GuardedBy({"mService", "mProcLock"})
    public boolean killPackageProcessesLSP(String str, int i, int i2, int i3, int i4, int i5, String str2) {
        return killPackageProcessesLSP(str, i, i2, i3, false, true, true, false, false, false, i4, i5, str2);
    }

    @GuardedBy({"mService"})
    public void killAppZygotesLocked(String str, int i, int i2, boolean z) {
        ArrayList arrayList = new ArrayList();
        for (SparseArray sparseArray : this.mAppZygotes.getMap().values()) {
            for (int i3 = 0; i3 < sparseArray.size(); i3++) {
                int keyAt = sparseArray.keyAt(i3);
                if ((i2 == -1 || UserHandle.getUserId(keyAt) == i2) && (i < 0 || UserHandle.getAppId(keyAt) == i)) {
                    AppZygote appZygote = (AppZygote) sparseArray.valueAt(i3);
                    if (str == null || str.equals(appZygote.getAppInfo().packageName)) {
                        arrayList.add(appZygote);
                    }
                }
            }
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            killAppZygoteIfNeededLocked((AppZygote) it.next(), z);
        }
    }

    public static boolean freezePackageCgroup(int i, boolean z) {
        try {
            Process.freezeCgroupUid(i, z);
            return true;
        } catch (RuntimeException e) {
            String str = z ? "freeze" : "unfreeze";
            Slog.e("ActivityManager", "Unable to " + str + " cgroup uid: " + i + ": " + e);
            return false;
        }
    }

    public static void freezeBinderAndPackageCgroup(ArrayList<Pair<ProcessRecord, Boolean>> arrayList, int i) {
        int freezeBinder;
        int size = arrayList.size();
        for (int i2 = 0; i2 < size; i2++) {
            int i3 = ((ProcessRecord) arrayList.get(i2).first).uid;
            int pid = ((ProcessRecord) arrayList.get(i2).first).getPid();
            if (pid > 0 && i3 == i) {
                int i4 = 0;
                while (true) {
                    try {
                        freezeBinder = CachedAppOptimizer.freezeBinder(pid, true, 10);
                        if (freezeBinder != (-OsConstants.EAGAIN)) {
                            break;
                        }
                        int i5 = i4 + 1;
                        if (i4 >= 1) {
                            break;
                        }
                        i4 = i5;
                    } catch (RuntimeException e) {
                        Slog.e("ActivityManager", "Unable to freeze binder for " + pid + ": " + e);
                    }
                }
                if (freezeBinder != 0) {
                    Slog.e("ActivityManager", "Unable to freeze binder for " + pid + ": " + freezeBinder);
                }
            }
        }
        freezePackageCgroup(i, true);
    }

    /* JADX WARN: Code restructure failed: missing block: B:37:0x00a0, code lost:
        if (r6.userId != r22) goto L9;
     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x00ab, code lost:
        if (android.os.UserHandle.getAppId(r6.uid) != r21) goto L9;
     */
    /* JADX WARN: Removed duplicated region for block: B:105:0x00fd A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:73:0x00ff  */
    @GuardedBy({"mService", "mProcLock"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean killPackageProcessesLSP(String str, int i, int i2, int i3, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, int i4, int i5, String str2) {
        boolean containsKey;
        boolean z7;
        PackageManagerInternal packageManagerInternal = this.mService.getPackageManagerInternal();
        ArrayList arrayList = new ArrayList();
        int size = this.mProcessNames.getMap().size();
        for (int i6 = 0; i6 < size; i6++) {
            SparseArray sparseArray = (SparseArray) this.mProcessNames.getMap().valueAt(i6);
            int size2 = sparseArray.size();
            for (int i7 = 0; i7 < size2; i7++) {
                ProcessRecord processRecord = (ProcessRecord) sparseArray.valueAt(i7);
                if (!processRecord.isPersistent() || z4) {
                    if (!processRecord.isRemoved()) {
                        if (processRecord.mState.getSetAdj() >= i3) {
                            if (str != null) {
                                boolean z8 = processRecord.getPkgDeps() != null && processRecord.getPkgDeps().contains(str);
                                if ((z8 || UserHandle.getAppId(processRecord.uid) == i) && ((i2 == -1 || processRecord.userId == i2) && ((containsKey = processRecord.getPkgList().containsKey(str)) || z8))) {
                                    if (!containsKey && z8 && !z6 && processRecord.info != null && !packageManagerInternal.isPackageFrozen(processRecord.info.packageName, processRecord.uid, processRecord.userId)) {
                                        z7 = true;
                                        if (!z3) {
                                        }
                                    }
                                    z7 = false;
                                    if (!z3) {
                                    }
                                }
                            } else {
                                if (i2 != -1) {
                                }
                                if (i >= 0) {
                                }
                                z7 = false;
                                if (!z3) {
                                    return true;
                                }
                                if (z5) {
                                    processRecord.setRemoved(true);
                                }
                                arrayList.add(new Pair(processRecord, Boolean.valueOf(z7)));
                            }
                        }
                    } else if (z3) {
                        arrayList.add(new Pair(processRecord, Boolean.valueOf((z6 || str == null || processRecord.getPkgList().containsKey(str) || processRecord.getPkgDeps() == null || !processRecord.getPkgDeps().contains(str) || processRecord.info == null || packageManagerInternal.isPackageFrozen(processRecord.info.packageName, processRecord.uid, processRecord.userId)) ? false : true)));
                    }
                }
            }
        }
        int uid = UserHandle.getUid(i2, i);
        boolean z9 = i >= 10000 && i <= 19999;
        if (z9) {
            freezeBinderAndPackageCgroup(arrayList, uid);
        }
        int size3 = arrayList.size();
        int i8 = 0;
        while (i8 < size3) {
            Pair pair = (Pair) arrayList.get(i8);
            removeProcessLocked((ProcessRecord) pair.first, z, z2 || ((Boolean) pair.second).booleanValue(), i4, i5, str2, !z9);
            i8++;
            size3 = size3;
        }
        int i9 = size3;
        killAppZygotesLocked(str, i, i2, false);
        this.mService.updateOomAdjLocked(12);
        if (z9) {
            freezePackageCgroup(uid, false);
        }
        return i9 > 0;
    }

    @GuardedBy({"mService"})
    public boolean removeProcessLocked(ProcessRecord processRecord, boolean z, boolean z2, int i, String str) {
        return removeProcessLocked(processRecord, z, z2, i, 0, str, true);
    }

    @GuardedBy({"mService"})
    public boolean removeProcessLocked(ProcessRecord processRecord, boolean z, boolean z2, int i, int i2, String str) {
        return removeProcessLocked(processRecord, z, z2, i, i2, str, true);
    }

    @GuardedBy({"mService"})
    public boolean removeProcessLocked(ProcessRecord processRecord, boolean z, boolean z2, int i, int i2, String str, boolean z3) {
        boolean z4;
        boolean z5;
        String str2 = processRecord.processName;
        int i3 = processRecord.uid;
        if (((ProcessRecord) this.mProcessNames.get(str2, i3)) != processRecord) {
            Slog.w("ActivityManager", "Ignoring remove of inactive process: " + processRecord);
            return false;
        }
        removeProcessNameLocked(str2, i3);
        this.mService.mAtmInternal.clearHeavyWeightProcessIfEquals(processRecord.getWindowProcessController());
        int pid = processRecord.getPid();
        if ((pid > 0 && pid != ActivityManagerService.MY_PID) || (pid == 0 && processRecord.isPendingStart())) {
            if (pid > 0) {
                this.mService.removePidLocked(pid, processRecord);
                processRecord.setBindMountPending(false);
                this.mService.mHandler.removeMessages(20, processRecord);
                this.mService.mBatteryStatsService.noteProcessFinish(processRecord.processName, processRecord.info.uid);
                if (processRecord.isolated) {
                    this.mService.mBatteryStatsService.removeIsolatedUid(processRecord.uid, processRecord.info.uid);
                    this.mService.getPackageManagerInternal().removeIsolatedUid(processRecord.uid);
                }
            }
            if (!processRecord.isPersistent() || processRecord.isolated) {
                z4 = false;
                z5 = false;
            } else if (z) {
                z5 = true;
                z4 = false;
            } else {
                z4 = true;
                z5 = false;
            }
            processRecord.killLocked(str, i, i2, true, z3);
            this.mService.handleAppDiedLocked(processRecord, pid, z4, z2, false);
            if (z4) {
                removeLruProcessLocked(processRecord);
                this.mService.addAppLocked(processRecord.info, null, false, null, 0);
            }
            return z5;
        }
        this.mRemovedProcesses.add(processRecord);
        return false;
    }

    @GuardedBy({"mService"})
    public void addProcessNameLocked(ProcessRecord processRecord) {
        synchronized (this.mProcLock) {
            try {
                ActivityManagerService.boostPriorityForProcLockedSection();
                ProcessRecord removeProcessNameLocked = removeProcessNameLocked(processRecord.processName, processRecord.uid);
                if (removeProcessNameLocked == processRecord && processRecord.isPersistent()) {
                    Slog.w("ActivityManager", "Re-adding persistent process " + processRecord);
                } else if (removeProcessNameLocked != null) {
                    if (removeProcessNameLocked.isKilled()) {
                        Slog.w("ActivityManager", "Existing proc " + removeProcessNameLocked + " was killed " + (SystemClock.uptimeMillis() - removeProcessNameLocked.getKillTime()) + "ms ago when adding " + processRecord);
                    } else {
                        Slog.wtf("ActivityManager", "Already have existing proc " + removeProcessNameLocked + " when adding " + processRecord);
                    }
                }
                UidRecord uidRecord = this.mActiveUids.get(processRecord.uid);
                if (uidRecord == null) {
                    uidRecord = new UidRecord(processRecord.uid, this.mService);
                    if (Arrays.binarySearch(this.mService.mDeviceIdleTempAllowlist, UserHandle.getAppId(processRecord.uid)) >= 0 || this.mService.mPendingTempAllowlist.indexOfKey(processRecord.uid) >= 0) {
                        uidRecord.setCurAllowListed(true);
                        uidRecord.setSetAllowListed(true);
                    }
                    uidRecord.updateHasInternetPermission();
                    this.mActiveUids.put(processRecord.uid, uidRecord);
                    EventLogTags.writeAmUidRunning(uidRecord.getUid());
                    this.mService.noteUidProcessState(uidRecord.getUid(), uidRecord.getCurProcState(), uidRecord.getCurCapability());
                }
                processRecord.setUidRecord(uidRecord);
                uidRecord.addProcess(processRecord);
                processRecord.setRenderThreadTid(0);
                this.mProcessNames.put(processRecord.processName, processRecord.uid, processRecord);
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        ActivityManagerService.resetPriorityAfterProcLockedSection();
        if (processRecord.isolated) {
            this.mIsolatedProcesses.put(processRecord.uid, processRecord);
        }
        if (processRecord.isSdkSandbox) {
            ArrayList<ProcessRecord> arrayList = this.mSdkSandboxes.get(processRecord.uid);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
            }
            arrayList.add(processRecord);
            this.mSdkSandboxes.put(Process.getAppUidForSdkSandboxUid(processRecord.uid), arrayList);
        }
    }

    @GuardedBy({"mService"})
    public final IsolatedUidRange getOrCreateIsolatedUidRangeLocked(ApplicationInfo applicationInfo, HostingRecord hostingRecord) {
        if (hostingRecord == null || !hostingRecord.usesAppZygote()) {
            return this.mGlobalIsolatedUids;
        }
        return this.mAppIsolatedUidRangeAllocator.getOrCreateIsolatedUidRangeLocked(applicationInfo.processName, hostingRecord.getDefiningUid());
    }

    public ProcessRecord getSharedIsolatedProcess(String str, int i, String str2) {
        int size = this.mIsolatedProcesses.size();
        for (int i2 = 0; i2 < size; i2++) {
            ProcessRecord valueAt = this.mIsolatedProcesses.valueAt(i2);
            if (valueAt.info.uid == i && valueAt.info.packageName.equals(str2) && valueAt.processName.equals(str)) {
                return valueAt;
            }
        }
        return null;
    }

    @GuardedBy({"mService"})
    public List<Integer> getIsolatedProcessesLocked(int i) {
        int size = this.mIsolatedProcesses.size();
        ArrayList arrayList = null;
        for (int i2 = 0; i2 < size; i2++) {
            ProcessRecord valueAt = this.mIsolatedProcesses.valueAt(i2);
            if (valueAt.info.uid == i) {
                if (arrayList == null) {
                    arrayList = new ArrayList();
                }
                arrayList.add(Integer.valueOf(valueAt.getPid()));
            }
        }
        return arrayList;
    }

    @GuardedBy({"mService"})
    public List<ProcessRecord> getSdkSandboxProcessesForAppLocked(int i) {
        return this.mSdkSandboxes.get(i);
    }

    @GuardedBy({"mService"})
    public ProcessRecord newProcessRecordLocked(ApplicationInfo applicationInfo, String str, boolean z, int i, boolean z2, int i2, String str2, HostingRecord hostingRecord) {
        String str3 = str != null ? str : applicationInfo.processName;
        int userId = UserHandle.getUserId(applicationInfo.uid);
        int i3 = applicationInfo.uid;
        if (z2) {
            i3 = i2;
        }
        if (z) {
            if (i == 0) {
                IsolatedUidRange orCreateIsolatedUidRangeLocked = getOrCreateIsolatedUidRangeLocked(applicationInfo, hostingRecord);
                if (orCreateIsolatedUidRangeLocked == null || (i3 = orCreateIsolatedUidRangeLocked.allocateIsolatedUidLocked(userId)) == -1) {
                    return null;
                }
            } else {
                i3 = i;
            }
            this.mAppExitInfoTracker.mIsolatedUidRecords.addIsolatedUid(i3, applicationInfo.uid);
            this.mService.getPackageManagerInternal().addIsolatedUid(i3, applicationInfo.uid);
            this.mService.mBatteryStatsService.addIsolatedUid(i3, applicationInfo.uid);
            FrameworkStatsLog.write(43, applicationInfo.uid, i3, 1);
        }
        ProcessRecord processRecord = new ProcessRecord(this.mService, applicationInfo, str3, i3, str2, hostingRecord.getDefiningUid(), hostingRecord.getDefiningProcessName());
        ProcessStateRecord processStateRecord = processRecord.mState;
        if (!z && !z2 && userId == 0 && (applicationInfo.flags & 9) == 9 && TextUtils.equals(str3, applicationInfo.processName)) {
            processStateRecord.setCurrentSchedulingGroup(2);
            processStateRecord.setSetSchedGroup(2);
            processRecord.setPersistent(true);
            processStateRecord.setMaxAdj(-800);
        }
        if (z && i != 0) {
            processStateRecord.setMaxAdj(-700);
        }
        addProcessNameLocked(processRecord);
        return processRecord;
    }

    @GuardedBy({"mService"})
    public ProcessRecord removeProcessNameLocked(String str, int i) {
        return removeProcessNameLocked(str, i, null);
    }

    @GuardedBy({"mService"})
    public ProcessRecord removeProcessNameLocked(String str, int i, ProcessRecord processRecord) {
        int appUidForSdkSandboxUid;
        ArrayList<ProcessRecord> arrayList;
        UidRecord uidRecord;
        ProcessRecord processRecord2 = (ProcessRecord) this.mProcessNames.get(str, i);
        ProcessRecord processRecord3 = processRecord != null ? processRecord : processRecord2;
        synchronized (this.mProcLock) {
            try {
                ActivityManagerService.boostPriorityForProcLockedSection();
                if (processRecord == null || processRecord2 == processRecord) {
                    this.mProcessNames.m1487remove(str, i);
                }
                if (processRecord3 != null && (uidRecord = processRecord3.getUidRecord()) != null) {
                    uidRecord.removeProcess(processRecord3);
                    if (uidRecord.getNumOfProcs() == 0) {
                        this.mService.enqueueUidChangeLocked(uidRecord, -1, -2147483647);
                        EventLogTags.writeAmUidStopped(i);
                        this.mActiveUids.remove(i);
                        this.mService.mFgsStartTempAllowList.removeUid(processRecord3.info.uid);
                        this.mService.noteUidProcessState(i, 20, 0);
                    }
                    processRecord3.setUidRecord(null);
                }
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        ActivityManagerService.resetPriorityAfterProcLockedSection();
        this.mIsolatedProcesses.remove(i);
        this.mGlobalIsolatedUids.freeIsolatedUidLocked(i);
        if (processRecord3 != null && processRecord3.appZygote) {
            removeProcessFromAppZygoteLocked(processRecord3);
        }
        if (processRecord3 != null && processRecord3.isSdkSandbox && (arrayList = this.mSdkSandboxes.get((appUidForSdkSandboxUid = Process.getAppUidForSdkSandboxUid(i)))) != null) {
            arrayList.remove(processRecord3);
            if (arrayList.size() == 0) {
                this.mSdkSandboxes.remove(appUidForSdkSandboxUid);
            }
        }
        this.mAppsInBackgroundRestricted.remove(processRecord3);
        return processRecord2;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public void updateCoreSettingsLOSP(Bundle bundle) {
        for (int size = this.mLruProcesses.size() - 1; size >= 0; size--) {
            IApplicationThread thread = this.mLruProcesses.get(size).getThread();
            if (thread != null) {
                try {
                    thread.setCoreSettings(bundle);
                } catch (RemoteException unused) {
                }
            }
        }
    }

    @GuardedBy({"mService", "mProcLock"})
    public void killAllBackgroundProcessesExceptLSP(int i, int i2) {
        ArrayList arrayList = new ArrayList();
        int size = this.mProcessNames.getMap().size();
        for (int i3 = 0; i3 < size; i3++) {
            SparseArray sparseArray = (SparseArray) this.mProcessNames.getMap().valueAt(i3);
            int size2 = sparseArray.size();
            for (int i4 = 0; i4 < size2; i4++) {
                ProcessRecord processRecord = (ProcessRecord) sparseArray.valueAt(i4);
                if (processRecord.isRemoved() || ((i < 0 || processRecord.info.targetSdkVersion < i) && (i2 < 0 || processRecord.mState.getSetProcState() > i2))) {
                    arrayList.add(processRecord);
                }
            }
        }
        int size3 = arrayList.size();
        for (int i5 = 0; i5 < size3; i5++) {
            removeProcessLocked((ProcessRecord) arrayList.get(i5), false, true, 13, 10, "kill all background except");
        }
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public void updateAllTimePrefsLOSP(int i) {
        ProcessRecord processRecord;
        for (int size = this.mLruProcesses.size() - 1; size >= 0; size--) {
            IApplicationThread thread = this.mLruProcesses.get(size).getThread();
            if (thread != null) {
                try {
                    thread.updateTimePrefs(i);
                } catch (RemoteException unused) {
                    Slog.w("ActivityManager", "Failed to update preferences for: " + processRecord.info.processName);
                }
            }
        }
    }

    public void setAllHttpProxy() {
        synchronized (this.mProcLock) {
            try {
                ActivityManagerService.boostPriorityForProcLockedSection();
                for (int size = this.mLruProcesses.size() - 1; size >= 0; size--) {
                    ProcessRecord processRecord = this.mLruProcesses.get(size);
                    IApplicationThread thread = processRecord.getThread();
                    if (processRecord.getPid() != ActivityManagerService.MY_PID && thread != null && !processRecord.isolated) {
                        try {
                            thread.updateHttpProxy();
                        } catch (RemoteException unused) {
                            Slog.w("ActivityManager", "Failed to update http proxy for: " + processRecord.info.processName);
                        }
                    }
                }
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterProcLockedSection();
                throw th;
            }
        }
        ActivityManagerService.resetPriorityAfterProcLockedSection();
        ActivityThread.updateHttpProxy(this.mService.mContext);
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public void clearAllDnsCacheLOSP() {
        ProcessRecord processRecord;
        for (int size = this.mLruProcesses.size() - 1; size >= 0; size--) {
            IApplicationThread thread = this.mLruProcesses.get(size).getThread();
            if (thread != null) {
                try {
                    thread.clearDnsCache();
                } catch (RemoteException unused) {
                    Slog.w("ActivityManager", "Failed to clear dns cache for: " + processRecord.info.processName);
                }
            }
        }
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public void handleAllTrustStorageUpdateLOSP() {
        ProcessRecord processRecord;
        for (int size = this.mLruProcesses.size() - 1; size >= 0; size--) {
            IApplicationThread thread = this.mLruProcesses.get(size).getThread();
            if (thread != null) {
                try {
                    thread.handleTrustStorageUpdate();
                } catch (RemoteException unused) {
                    Slog.w("ActivityManager", "Failed to handle trust storage update for: " + processRecord.info.processName);
                }
            }
        }
    }

    @GuardedBy({"mService", "mProcLock"})
    public final int updateLruProcessInternalLSP(ProcessRecord processRecord, long j, int i, int i2, String str, Object obj, ProcessRecord processRecord2) {
        processRecord.setLastActivityTime(j);
        if (processRecord.hasActivitiesOrRecentTasks()) {
            return i;
        }
        int lastIndexOf = this.mLruProcesses.lastIndexOf(processRecord);
        if (lastIndexOf < 0) {
            Slog.wtf("ActivityManager", "Adding dependent process " + processRecord + " not on LRU list: " + str + " " + obj + " from " + processRecord2);
            return i;
        } else if (lastIndexOf >= i) {
            return i;
        } else {
            int i3 = this.mLruProcessActivityStart;
            if (lastIndexOf < i3 || i >= i3) {
                this.mLruProcesses.remove(lastIndexOf);
                if (i > 0) {
                    i--;
                }
                this.mLruProcesses.add(i, processRecord);
                processRecord.setLruSeq(i2);
                return i;
            }
            return i;
        }
    }

    @GuardedBy({"mService", "mProcLock"})
    public final void updateClientActivitiesOrderingLSP(ProcessRecord processRecord, int i, int i2, int i3) {
        int connectionGroup;
        boolean z;
        ProcessServiceRecord processServiceRecord = processRecord.mServices;
        if (processRecord.hasActivitiesOrRecentTasks() || processServiceRecord.isTreatedLikeActivity() || !processServiceRecord.hasClientActivities()) {
            return;
        }
        int i4 = processRecord.info.uid;
        int connectionGroup2 = processServiceRecord.getConnectionGroup();
        if (connectionGroup2 > 0) {
            int connectionImportance = processServiceRecord.getConnectionImportance();
            int i5 = i3;
            while (i3 >= i2) {
                ProcessRecord processRecord2 = this.mLruProcesses.get(i3);
                ProcessServiceRecord processServiceRecord2 = processRecord2.mServices;
                int connectionGroup3 = processServiceRecord2.getConnectionGroup();
                int connectionImportance2 = processServiceRecord2.getConnectionImportance();
                if (processRecord2.info.uid == i4 && connectionGroup3 == connectionGroup2) {
                    if (i3 != i5 || connectionImportance2 < connectionImportance) {
                        int i6 = i;
                        while (true) {
                            if (i6 <= i5) {
                                z = false;
                                break;
                            } else if (connectionImportance2 <= this.mLruProcesses.get(i6).mServices.getConnectionImportance()) {
                                this.mLruProcesses.remove(i3);
                                this.mLruProcesses.add(i6, processRecord2);
                                i5--;
                                z = true;
                                break;
                            } else {
                                i6--;
                            }
                        }
                        if (!z) {
                            this.mLruProcesses.remove(i3);
                            this.mLruProcesses.add(i5, processRecord2);
                        }
                    }
                    i5--;
                    connectionImportance = connectionImportance2;
                }
                i3--;
            }
            i3 = i5;
        }
        int i7 = i3;
        while (i3 >= i2) {
            ProcessRecord processRecord3 = this.mLruProcesses.get(i3);
            ProcessServiceRecord processServiceRecord3 = processRecord3.mServices;
            int connectionGroup4 = processServiceRecord3.getConnectionGroup();
            if (processRecord3.info.uid != i4) {
                if (i3 < i7) {
                    boolean z2 = false;
                    int i8 = 0;
                    int i9 = 0;
                    while (i3 >= i2) {
                        this.mLruProcesses.remove(i3);
                        this.mLruProcesses.add(i7, processRecord3);
                        i3--;
                        if (i3 < i2) {
                            break;
                        }
                        processRecord3 = this.mLruProcesses.get(i3);
                        if (!processRecord3.hasActivitiesOrRecentTasks() && !processServiceRecord3.isTreatedLikeActivity()) {
                            if (!processServiceRecord3.hasClientActivities()) {
                                continue;
                            } else if (z2) {
                                if (i8 == 0 || i8 != processRecord3.info.uid || i9 == 0 || i9 != connectionGroup4) {
                                    break;
                                }
                            } else {
                                i8 = processRecord3.info.uid;
                                z2 = true;
                                i9 = connectionGroup4;
                            }
                            i7--;
                        } else if (z2) {
                            break;
                        } else {
                            z2 = true;
                            i7--;
                        }
                    }
                }
                do {
                    i7--;
                    if (i7 < i2) {
                        break;
                    }
                } while (this.mLruProcesses.get(i7).info.uid != i4);
                if (i7 >= i2) {
                    int connectionGroup5 = this.mLruProcesses.get(i7).mServices.getConnectionGroup();
                    do {
                        i7--;
                        if (i7 < i2) {
                            break;
                        }
                        ProcessRecord processRecord4 = this.mLruProcesses.get(i7);
                        connectionGroup = processRecord4.mServices.getConnectionGroup();
                        if (processRecord4.info.uid != i4) {
                            break;
                        }
                    } while (connectionGroup == connectionGroup5);
                }
                i3 = i7;
            } else {
                i3--;
            }
        }
    }

    @GuardedBy({"mService"})
    public void updateLruProcessLocked(ProcessRecord processRecord, boolean z, ProcessRecord processRecord2) {
        ProcessServiceRecord processServiceRecord = processRecord.mServices;
        boolean z2 = processRecord.hasActivitiesOrRecentTasks() || processServiceRecord.hasClientActivities() || processServiceRecord.isTreatedLikeActivity();
        if (z || !z2) {
            if (processRecord.getPid() != 0 || processRecord.isPendingStart()) {
                synchronized (this.mProcLock) {
                    try {
                        ActivityManagerService.boostPriorityForProcLockedSection();
                        updateLruProcessLSP(processRecord, processRecord2, z2, false);
                    } catch (Throwable th) {
                        ActivityManagerService.resetPriorityAfterProcLockedSection();
                        throw th;
                    }
                }
                ActivityManagerService.resetPriorityAfterProcLockedSection();
            }
        }
    }

    @GuardedBy({"mService", "mProcLock"})
    public final void updateLruProcessLSP(ProcessRecord processRecord, ProcessRecord processRecord2, boolean z, boolean z2) {
        int i;
        int i2;
        ServiceRecord serviceRecord;
        ProcessRecord processRecord3;
        int i3;
        this.mLruSeq++;
        long uptimeMillis = SystemClock.uptimeMillis();
        ProcessServiceRecord processServiceRecord = processRecord.mServices;
        processRecord.setLastActivityTime(uptimeMillis);
        if (z) {
            int size = this.mLruProcesses.size();
            if (size > 0 && this.mLruProcesses.get(size - 1) == processRecord) {
                return;
            }
        } else {
            int i4 = this.mLruProcessServiceStart;
            if (i4 > 0 && this.mLruProcesses.get(i4 - 1) == processRecord) {
                return;
            }
        }
        int lastIndexOf = this.mLruProcesses.lastIndexOf(processRecord);
        if (!processRecord.isPersistent() || lastIndexOf < 0) {
            if (lastIndexOf >= 0) {
                int i5 = this.mLruProcessActivityStart;
                if (lastIndexOf < i5) {
                    this.mLruProcessActivityStart = i5 - 1;
                }
                int i6 = this.mLruProcessServiceStart;
                if (lastIndexOf < i6) {
                    this.mLruProcessServiceStart = i6 - 1;
                }
                this.mLruProcesses.remove(lastIndexOf);
            }
            if (z) {
                int size2 = this.mLruProcesses.size();
                i2 = this.mLruProcessServiceStart;
                if (!processRecord.hasActivitiesOrRecentTasks() && !processServiceRecord.isTreatedLikeActivity() && this.mLruProcessActivityStart < (i3 = size2 - 1)) {
                    while (i3 > this.mLruProcessActivityStart && this.mLruProcesses.get(i3).info.uid != processRecord.info.uid) {
                        i3--;
                    }
                    this.mLruProcesses.add(i3, processRecord);
                    i = i3 - 1;
                    int i7 = this.mLruProcessActivityStart;
                    if (i < i7) {
                        i = i7;
                    }
                    updateClientActivitiesOrderingLSP(processRecord, i3, i7, i);
                } else {
                    this.mLruProcesses.add(processRecord);
                    i = this.mLruProcesses.size() - 1;
                }
            } else {
                i = -1;
                if (z2) {
                    this.mLruProcesses.add(this.mLruProcessActivityStart, processRecord);
                    i2 = this.mLruProcessServiceStart;
                    this.mLruProcessActivityStart++;
                } else {
                    int i8 = this.mLruProcessServiceStart;
                    if (processRecord2 != null) {
                        int lastIndexOf2 = this.mLruProcesses.lastIndexOf(processRecord2);
                        if (lastIndexOf2 > lastIndexOf) {
                            lastIndexOf = lastIndexOf2;
                        }
                        if (lastIndexOf >= 0 && i8 > lastIndexOf) {
                            i8 = lastIndexOf;
                        }
                    }
                    this.mLruProcesses.add(i8, processRecord);
                    i2 = i8 - 1;
                    this.mLruProcessActivityStart++;
                    int i9 = this.mLruProcessServiceStart + 1;
                    this.mLruProcessServiceStart = i9;
                    if (i8 > 1) {
                        updateClientActivitiesOrderingLSP(processRecord, i9 - 1, 0, i2);
                    }
                }
            }
            processRecord.setLruSeq(this.mLruSeq);
            int i10 = i2;
            int i11 = i;
            for (int numberOfConnections = processServiceRecord.numberOfConnections() - 1; numberOfConnections >= 0; numberOfConnections--) {
                ConnectionRecord connectionAt = processServiceRecord.getConnectionAt(numberOfConnections);
                AppBindRecord appBindRecord = connectionAt.binding;
                if (appBindRecord != null && !connectionAt.serviceDead && (serviceRecord = appBindRecord.service) != null && (processRecord3 = serviceRecord.app) != null && processRecord3.getLruSeq() != this.mLruSeq && connectionAt.notHasFlag(1073742128) && !connectionAt.binding.service.app.isPersistent()) {
                    if (!connectionAt.binding.service.app.mServices.hasClientActivities()) {
                        i10 = updateLruProcessInternalLSP(connectionAt.binding.service.app, uptimeMillis, i10, this.mLruSeq, "service connection", connectionAt, processRecord);
                    } else if (i11 >= 0) {
                        i11 = updateLruProcessInternalLSP(connectionAt.binding.service.app, uptimeMillis, i11, this.mLruSeq, "service connection", connectionAt, processRecord);
                    }
                }
            }
            ProcessProviderRecord processProviderRecord = processRecord.mProviders;
            int i12 = i10;
            for (int numberOfProviderConnections = processProviderRecord.numberOfProviderConnections() - 1; numberOfProviderConnections >= 0; numberOfProviderConnections--) {
                ContentProviderRecord contentProviderRecord = processProviderRecord.getProviderConnectionAt(numberOfProviderConnections).provider;
                ProcessRecord processRecord4 = contentProviderRecord.proc;
                if (processRecord4 != null && processRecord4.getLruSeq() != this.mLruSeq && !contentProviderRecord.proc.isPersistent()) {
                    i12 = updateLruProcessInternalLSP(contentProviderRecord.proc, uptimeMillis, i12, this.mLruSeq, "provider reference", contentProviderRecord, processRecord);
                }
            }
        }
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public ProcessRecord getLRURecordForAppLOSP(IApplicationThread iApplicationThread) {
        if (iApplicationThread == null) {
            return null;
        }
        return getLRURecordForAppLOSP(iApplicationThread.asBinder());
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public ProcessRecord getLRURecordForAppLOSP(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        for (int size = this.mLruProcesses.size() - 1; size >= 0; size--) {
            ProcessRecord processRecord = this.mLruProcesses.get(size);
            IApplicationThread thread = processRecord.getThread();
            if (thread != null && thread.asBinder() == iBinder) {
                return processRecord;
            }
        }
        return null;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public boolean haveBackgroundProcessLOSP() {
        for (int size = this.mLruProcesses.size() - 1; size >= 0; size--) {
            ProcessRecord processRecord = this.mLruProcesses.get(size);
            if (processRecord.getThread() != null && processRecord.mState.getSetProcState() >= 16) {
                return true;
            }
        }
        return false;
    }

    public static int procStateToImportance(int i, int i2, ActivityManager.RunningAppProcessInfo runningAppProcessInfo, int i3) {
        int procStateToImportanceForTargetSdk = ActivityManager.RunningAppProcessInfo.procStateToImportanceForTargetSdk(i, i3);
        if (procStateToImportanceForTargetSdk == 400) {
            runningAppProcessInfo.lru = i2;
        } else {
            runningAppProcessInfo.lru = 0;
        }
        return procStateToImportanceForTargetSdk;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public void fillInProcMemInfoLOSP(ProcessRecord processRecord, ActivityManager.RunningAppProcessInfo runningAppProcessInfo, int i) {
        runningAppProcessInfo.pid = processRecord.getPid();
        runningAppProcessInfo.uid = processRecord.info.uid;
        if (processRecord.getWindowProcessController().isHeavyWeightProcess()) {
            runningAppProcessInfo.flags |= 1;
        }
        if (processRecord.isPersistent()) {
            runningAppProcessInfo.flags |= 2;
        }
        if (processRecord.hasActivities()) {
            runningAppProcessInfo.flags |= 4;
        }
        runningAppProcessInfo.lastTrimLevel = processRecord.mProfile.getTrimMemoryLevel();
        ProcessStateRecord processStateRecord = processRecord.mState;
        int curAdj = processStateRecord.getCurAdj();
        int curProcState = processStateRecord.getCurProcState();
        runningAppProcessInfo.importance = procStateToImportance(curProcState, curAdj, runningAppProcessInfo, i);
        runningAppProcessInfo.importanceReasonCode = processStateRecord.getAdjTypeCode();
        runningAppProcessInfo.processState = curProcState;
        runningAppProcessInfo.isFocused = processRecord == this.mService.getTopApp();
        runningAppProcessInfo.lastActivityTime = processRecord.getLastActivityTime();
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public List<ActivityManager.RunningAppProcessInfo> getRunningAppProcessesLOSP(boolean z, int i, boolean z2, int i2, int i3) {
        int activityPid;
        ArrayList arrayList = null;
        for (int size = this.mLruProcesses.size() - 1; size >= 0; size--) {
            ProcessRecord processRecord = this.mLruProcesses.get(size);
            ProcessStateRecord processStateRecord = processRecord.mState;
            ProcessErrorStateRecord processErrorStateRecord = processRecord.mErrorState;
            if ((z || processRecord.userId == i) && ((z2 || processRecord.uid == i2) && processRecord.getThread() != null && !processErrorStateRecord.isCrashing() && !processErrorStateRecord.isNotResponding())) {
                ActivityManager.RunningAppProcessInfo runningAppProcessInfo = new ActivityManager.RunningAppProcessInfo(processRecord.processName, processRecord.getPid(), processRecord.getPackageList());
                if (processRecord.getPkgDeps() != null) {
                    runningAppProcessInfo.pkgDeps = (String[]) processRecord.getPkgDeps().toArray(new String[processRecord.getPkgDeps().size()]);
                }
                fillInProcMemInfoLOSP(processRecord, runningAppProcessInfo, i3);
                if (processStateRecord.getAdjSource() instanceof ProcessRecord) {
                    runningAppProcessInfo.importanceReasonPid = ((ProcessRecord) processStateRecord.getAdjSource()).getPid();
                    runningAppProcessInfo.importanceReasonImportance = ActivityManager.RunningAppProcessInfo.procStateToImportance(processStateRecord.getAdjSourceProcState());
                } else if ((processStateRecord.getAdjSource() instanceof ActivityServiceConnectionsHolder) && (activityPid = ((ActivityServiceConnectionsHolder) processStateRecord.getAdjSource()).getActivityPid()) != -1) {
                    runningAppProcessInfo.importanceReasonPid = activityPid;
                }
                if (processStateRecord.getAdjTarget() instanceof ComponentName) {
                    runningAppProcessInfo.importanceReasonComponent = (ComponentName) processStateRecord.getAdjTarget();
                }
                if (arrayList == null) {
                    arrayList = new ArrayList();
                }
                arrayList.add(runningAppProcessInfo);
            }
        }
        return arrayList;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public int getLruSizeLOSP() {
        return this.mLruProcesses.size();
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public ArrayList<ProcessRecord> getLruProcessesLOSP() {
        return this.mLruProcesses;
    }

    @GuardedBy({"mService", "mProcLock"})
    public ArrayList<ProcessRecord> getLruProcessesLSP() {
        return this.mLruProcesses;
    }

    @GuardedBy({"mService", "mProcLock"})
    @VisibleForTesting
    public void setLruProcessServiceStartLSP(int i) {
        this.mLruProcessServiceStart = i;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public int getLruProcessServiceStartLOSP() {
        return this.mLruProcessServiceStart;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public void forEachLruProcessesLOSP(boolean z, Consumer<ProcessRecord> consumer) {
        if (z) {
            int size = this.mLruProcesses.size();
            for (int i = 0; i < size; i++) {
                consumer.accept(this.mLruProcesses.get(i));
            }
            return;
        }
        for (int size2 = this.mLruProcesses.size() - 1; size2 >= 0; size2--) {
            consumer.accept(this.mLruProcesses.get(size2));
        }
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public <R> R searchEachLruProcessesLOSP(boolean z, Function<ProcessRecord, R> function) {
        if (z) {
            int size = this.mLruProcesses.size();
            for (int i = 0; i < size; i++) {
                R apply = function.apply(this.mLruProcesses.get(i));
                if (apply != null) {
                    return apply;
                }
            }
            return null;
        }
        for (int size2 = this.mLruProcesses.size() - 1; size2 >= 0; size2--) {
            R apply2 = function.apply(this.mLruProcesses.get(size2));
            if (apply2 != null) {
                return apply2;
            }
        }
        return null;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public boolean isInLruListLOSP(ProcessRecord processRecord) {
        return this.mLruProcesses.contains(processRecord);
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public int getLruSeqLOSP() {
        return this.mLruSeq;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public MyProcessMap getProcessNamesLOSP() {
        return this.mProcessNames;
    }

    @GuardedBy({"mService"})
    public void dumpLruListHeaderLocked(PrintWriter printWriter) {
        printWriter.print("  Process LRU list (sorted by oom_adj, ");
        printWriter.print(this.mLruProcesses.size());
        printWriter.print(" total, non-act at ");
        printWriter.print(this.mLruProcesses.size() - this.mLruProcessActivityStart);
        printWriter.print(", non-svc at ");
        printWriter.print(this.mLruProcesses.size() - this.mLruProcessServiceStart);
        printWriter.println("):");
    }

    @GuardedBy({"mService"})
    public final void dumpLruEntryLocked(PrintWriter printWriter, int i, ProcessRecord processRecord, String str) {
        printWriter.print(str);
        printWriter.print('#');
        if (i < 10) {
            printWriter.print(' ');
        }
        printWriter.print(i);
        printWriter.print(": ");
        boolean z = false;
        printWriter.print(makeOomAdjString(processRecord.mState.getSetAdj(), false));
        printWriter.print(' ');
        printWriter.print(makeProcStateString(processRecord.mState.getCurProcState()));
        printWriter.print(' ');
        ActivityManager.printCapabilitiesSummary(printWriter, processRecord.mState.getCurCapability());
        printWriter.print(' ');
        printWriter.print(processRecord.toShortString());
        ProcessServiceRecord processServiceRecord = processRecord.mServices;
        if (processRecord.hasActivitiesOrRecentTasks() || processServiceRecord.hasClientActivities() || processServiceRecord.isTreatedLikeActivity()) {
            printWriter.print(" act:");
            boolean z2 = true;
            if (processRecord.hasActivities()) {
                printWriter.print("activities");
                z = true;
            }
            if (processRecord.hasRecentTasks()) {
                if (z) {
                    printWriter.print("|");
                }
                printWriter.print("recents");
                z = true;
            }
            if (processServiceRecord.hasClientActivities()) {
                if (z) {
                    printWriter.print("|");
                }
                printWriter.print("client");
            } else {
                z2 = z;
            }
            if (processServiceRecord.isTreatedLikeActivity()) {
                if (z2) {
                    printWriter.print("|");
                }
                printWriter.print("treated");
            }
        }
        printWriter.println();
    }

    @GuardedBy({"mService"})
    public boolean dumpLruLocked(PrintWriter printWriter, String str, String str2) {
        boolean z;
        int size = this.mLruProcesses.size();
        String str3 = "  ";
        if (str2 == null) {
            printWriter.println("ACTIVITY MANAGER LRU PROCESSES (dumpsys activity lru)");
        } else {
            for (int i = size - 1; i >= 0; i--) {
                ProcessRecord processRecord = this.mLruProcesses.get(i);
                if (str == null || processRecord.getPkgList().containsKey(str)) {
                    z = true;
                    break;
                }
            }
            z = false;
            if (!z) {
                return false;
            }
            printWriter.print(str2);
            printWriter.println("Raw LRU list (dumpsys activity lru):");
            str3 = str2 + "  ";
        }
        int i2 = size - 1;
        boolean z2 = true;
        while (i2 >= this.mLruProcessActivityStart) {
            ProcessRecord processRecord2 = this.mLruProcesses.get(i2);
            if (str == null || processRecord2.getPkgList().containsKey(str)) {
                if (z2) {
                    printWriter.print(str3);
                    printWriter.println("Activities:");
                    z2 = false;
                }
                dumpLruEntryLocked(printWriter, i2, processRecord2, str3);
            }
            i2--;
        }
        boolean z3 = true;
        while (i2 >= this.mLruProcessServiceStart) {
            ProcessRecord processRecord3 = this.mLruProcesses.get(i2);
            if (str == null || processRecord3.getPkgList().containsKey(str)) {
                if (z3) {
                    printWriter.print(str3);
                    printWriter.println("Services:");
                    z3 = false;
                }
                dumpLruEntryLocked(printWriter, i2, processRecord3, str3);
            }
            i2--;
        }
        boolean z4 = true;
        while (i2 >= 0) {
            ProcessRecord processRecord4 = this.mLruProcesses.get(i2);
            if (str == null || processRecord4.getPkgList().containsKey(str)) {
                if (z4) {
                    printWriter.print(str3);
                    printWriter.println("Other:");
                    z4 = false;
                }
                dumpLruEntryLocked(printWriter, i2, processRecord4, str3);
            }
            i2--;
        }
        return true;
    }

    @GuardedBy({"mService", "mProcLock"})
    public void dumpProcessesLSP(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr, int i, boolean z, String str, int i2) {
        boolean z2;
        int i3;
        boolean z3;
        printWriter.println("ACTIVITY MANAGER RUNNING PROCESSES (dumpsys activity processes)");
        if (z || str != null) {
            int size = this.mProcessNames.getMap().size();
            z2 = false;
            int i4 = 0;
            for (int i5 = 0; i5 < size; i5++) {
                SparseArray sparseArray = (SparseArray) this.mProcessNames.getMap().valueAt(i5);
                int size2 = sparseArray.size();
                for (int i6 = 0; i6 < size2; i6++) {
                    ProcessRecord processRecord = (ProcessRecord) sparseArray.valueAt(i6);
                    if (str == null || processRecord.getPkgList().containsKey(str)) {
                        if (!z2) {
                            printWriter.println("  All known processes:");
                            z2 = true;
                        }
                        printWriter.print(processRecord.isPersistent() ? "  *PERS*" : "  *APP*");
                        printWriter.print(" UID ");
                        printWriter.print(sparseArray.keyAt(i6));
                        printWriter.print(" ");
                        printWriter.println(processRecord);
                        processRecord.dump(printWriter, "    ");
                        if (processRecord.isPersistent()) {
                            i4++;
                        }
                    }
                }
            }
            i3 = i4;
        } else {
            z2 = false;
            i3 = 0;
        }
        if (this.mIsolatedProcesses.size() > 0) {
            int size3 = this.mIsolatedProcesses.size();
            boolean z4 = false;
            for (int i7 = 0; i7 < size3; i7++) {
                ProcessRecord valueAt = this.mIsolatedProcesses.valueAt(i7);
                if (str == null || valueAt.getPkgList().containsKey(str)) {
                    if (!z4) {
                        if (z2) {
                            printWriter.println();
                        }
                        printWriter.println("  Isolated process list (sorted by uid):");
                        z4 = true;
                        z2 = true;
                    }
                    printWriter.print("    Isolated #");
                    printWriter.print(i7);
                    printWriter.print(": ");
                    printWriter.println(valueAt);
                }
            }
        }
        boolean dumpActiveInstruments = this.mService.dumpActiveInstruments(printWriter, str, z2);
        if (dumpOomLocked(fileDescriptor, printWriter, dumpActiveInstruments, strArr, i, z, str, false)) {
            dumpActiveInstruments = true;
        }
        if (this.mActiveUids.size() > 0) {
            dumpActiveInstruments |= this.mActiveUids.dump(printWriter, str, i2, "UID states:", dumpActiveInstruments);
        }
        if (z) {
            dumpActiveInstruments |= this.mService.mUidObserverController.dumpValidateUids(printWriter, str, i2, "UID validation:", dumpActiveInstruments);
        }
        if (dumpActiveInstruments) {
            printWriter.println();
        }
        if (dumpLruLocked(printWriter, str, "  ")) {
            dumpActiveInstruments = true;
        }
        if (getLruSizeLOSP() > 0) {
            if (dumpActiveInstruments) {
                printWriter.println();
            }
            dumpLruListHeaderLocked(printWriter);
            dumpProcessOomList(printWriter, this.mService, this.mLruProcesses, "    ", "Proc", "PERS", false, str);
            z3 = true;
        } else {
            z3 = dumpActiveInstruments;
        }
        this.mService.dumpOtherProcessesInfoLSP(fileDescriptor, printWriter, z, str, i2, i3, z3);
    }

    @GuardedBy({"mService", "mProcLock"})
    public void writeProcessesToProtoLSP(ProtoOutputStream protoOutputStream, String str) {
        int size = this.mProcessNames.getMap().size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            SparseArray sparseArray = (SparseArray) this.mProcessNames.getMap().valueAt(i2);
            int size2 = sparseArray.size();
            for (int i3 = 0; i3 < size2; i3++) {
                ProcessRecord processRecord = (ProcessRecord) sparseArray.valueAt(i3);
                if (str == null || processRecord.getPkgList().containsKey(str)) {
                    processRecord.dumpDebug(protoOutputStream, 2246267895809L, this.mLruProcesses.indexOf(processRecord));
                    if (processRecord.isPersistent()) {
                        i++;
                    }
                }
            }
        }
        int size3 = this.mIsolatedProcesses.size();
        for (int i4 = 0; i4 < size3; i4++) {
            ProcessRecord valueAt = this.mIsolatedProcesses.valueAt(i4);
            if (str == null || valueAt.getPkgList().containsKey(str)) {
                valueAt.dumpDebug(protoOutputStream, 2246267895810L, this.mLruProcesses.indexOf(valueAt));
            }
        }
        int appId = this.mService.getAppId(str);
        this.mActiveUids.dumpProto(protoOutputStream, str, appId, 2246267895812L);
        if (getLruSizeLOSP() > 0) {
            long start = protoOutputStream.start(1146756268038L);
            int lruSizeLOSP = getLruSizeLOSP();
            protoOutputStream.write(1120986464257L, lruSizeLOSP);
            protoOutputStream.write(1120986464258L, lruSizeLOSP - this.mLruProcessActivityStart);
            protoOutputStream.write(1120986464259L, lruSizeLOSP - this.mLruProcessServiceStart);
            writeProcessOomListToProto(protoOutputStream, 2246267895812L, this.mService, this.mLruProcesses, true, str);
            protoOutputStream.end(start);
        }
        this.mService.writeOtherProcessesInfoToProtoLSP(protoOutputStream, str, appId, i);
    }

    public static ArrayList<Pair<ProcessRecord, Integer>> sortProcessOomList(List<ProcessRecord> list, String str) {
        ArrayList<Pair<ProcessRecord, Integer>> arrayList = new ArrayList<>(list.size());
        int size = list.size();
        for (int i = 0; i < size; i++) {
            ProcessRecord processRecord = list.get(i);
            if (str == null || processRecord.getPkgList().containsKey(str)) {
                arrayList.add(new Pair<>(list.get(i), Integer.valueOf(i)));
            }
        }
        Collections.sort(arrayList, new Comparator<Pair<ProcessRecord, Integer>>() { // from class: com.android.server.am.ProcessList.2
            @Override // java.util.Comparator
            public int compare(Pair<ProcessRecord, Integer> pair, Pair<ProcessRecord, Integer> pair2) {
                int setAdj = ((ProcessRecord) pair2.first).mState.getSetAdj() - ((ProcessRecord) pair.first).mState.getSetAdj();
                if (setAdj != 0) {
                    return setAdj;
                }
                int setProcState = ((ProcessRecord) pair2.first).mState.getSetProcState() - ((ProcessRecord) pair.first).mState.getSetProcState();
                if (setProcState != 0) {
                    return setProcState;
                }
                int intValue = ((Integer) pair2.second).intValue() - ((Integer) pair.second).intValue();
                if (intValue != 0) {
                    return intValue;
                }
                return 0;
            }
        });
        return arrayList;
    }

    /* JADX WARN: Type inference failed for: r7v0 */
    /* JADX WARN: Type inference failed for: r7v1, types: [int, boolean] */
    /* JADX WARN: Type inference failed for: r7v9 */
    public static boolean writeProcessOomListToProto(ProtoOutputStream protoOutputStream, long j, ActivityManagerService activityManagerService, List<ProcessRecord> list, boolean z, String str) {
        int i;
        ProcessRecord processRecord;
        ArrayList<Pair<ProcessRecord, Integer>> arrayList;
        ArrayList<Pair<ProcessRecord, Integer>> sortProcessOomList = sortProcessOomList(list, str);
        if (sortProcessOomList.isEmpty()) {
            return false;
        }
        long uptimeMillis = SystemClock.uptimeMillis();
        ?? r7 = 1;
        int size = sortProcessOomList.size() - 1;
        while (size >= 0) {
            ProcessRecord processRecord2 = (ProcessRecord) sortProcessOomList.get(size).first;
            ProcessStateRecord processStateRecord = processRecord2.mState;
            ProcessServiceRecord processServiceRecord = processRecord2.mServices;
            long start = protoOutputStream.start(j);
            String makeOomAdjString = makeOomAdjString(processStateRecord.getSetAdj(), r7);
            protoOutputStream.write(1133871366145L, processRecord2.isPersistent());
            protoOutputStream.write(1120986464258L, (list.size() - r7) - ((Integer) sortProcessOomList.get(size).second).intValue());
            protoOutputStream.write(1138166333443L, makeOomAdjString);
            int setSchedGroup = processStateRecord.getSetSchedGroup();
            if (setSchedGroup != 0) {
                i = 2;
                if (setSchedGroup == 2) {
                    i = r7;
                } else if (setSchedGroup != 3) {
                    i = setSchedGroup != 4 ? -1 : 3;
                }
            } else {
                i = 0;
            }
            if (i != -1) {
                processRecord = processRecord2;
                protoOutputStream.write(1159641169924L, i);
            } else {
                processRecord = processRecord2;
            }
            if (processStateRecord.hasForegroundActivities()) {
                protoOutputStream.write(1133871366149L, true);
            } else if (processServiceRecord.hasForegroundServices()) {
                protoOutputStream.write(1133871366150L, true);
            }
            protoOutputStream.write(1159641169927L, makeProcStateProtoEnum(processStateRecord.getCurProcState()));
            ProcessRecord processRecord3 = processRecord;
            long j2 = uptimeMillis;
            protoOutputStream.write(1120986464264L, processRecord3.mProfile.getTrimMemoryLevel());
            processRecord3.dumpDebug(protoOutputStream, 1146756268041L);
            protoOutputStream.write(1138166333450L, processStateRecord.getAdjType());
            if (processStateRecord.getAdjSource() != null || processStateRecord.getAdjTarget() != null) {
                if (processStateRecord.getAdjTarget() instanceof ComponentName) {
                    ((ComponentName) processStateRecord.getAdjTarget()).dumpDebug(protoOutputStream, 1146756268043L);
                } else if (processStateRecord.getAdjTarget() != null) {
                    protoOutputStream.write(1138166333452L, processStateRecord.getAdjTarget().toString());
                }
                if (processStateRecord.getAdjSource() instanceof ProcessRecord) {
                    ((ProcessRecord) processStateRecord.getAdjSource()).dumpDebug(protoOutputStream, 1146756268045L);
                } else if (processStateRecord.getAdjSource() != null) {
                    protoOutputStream.write(1138166333454L, processStateRecord.getAdjSource().toString());
                }
            }
            if (z) {
                long start2 = protoOutputStream.start(1146756268047L);
                protoOutputStream.write(1120986464257L, processStateRecord.getMaxAdj());
                protoOutputStream.write(1120986464258L, processStateRecord.getCurRawAdj());
                protoOutputStream.write(1120986464259L, processStateRecord.getSetRawAdj());
                protoOutputStream.write(1120986464260L, processStateRecord.getCurAdj());
                protoOutputStream.write(1120986464261L, processStateRecord.getSetAdj());
                protoOutputStream.write(1159641169927L, makeProcStateProtoEnum(processStateRecord.getCurProcState()));
                protoOutputStream.write(1159641169928L, makeProcStateProtoEnum(processStateRecord.getSetProcState()));
                protoOutputStream.write(1138166333449L, DebugUtils.sizeValueToString(processRecord3.mProfile.getLastPss() * 1024, new StringBuilder()));
                protoOutputStream.write(1138166333450L, DebugUtils.sizeValueToString(processRecord3.mProfile.getLastSwapPss() * 1024, new StringBuilder()));
                protoOutputStream.write(1138166333451L, DebugUtils.sizeValueToString(processRecord3.mProfile.getLastCachedPss() * 1024, new StringBuilder()));
                protoOutputStream.write(1133871366156L, processStateRecord.isCached());
                protoOutputStream.write(1133871366157L, processStateRecord.isEmpty());
                protoOutputStream.write(1133871366158L, processServiceRecord.hasAboveClient());
                if (processStateRecord.getSetProcState() >= 10) {
                    long j3 = processRecord3.mProfile.mLastCpuTime.get();
                    long j4 = j2 - activityManagerService.mLastPowerCheckUptime;
                    if (j3 != 0 && j4 > 0) {
                        long j5 = processRecord3.mProfile.mCurCpuTime.get() - j3;
                        long start3 = protoOutputStream.start(1146756268047L);
                        arrayList = sortProcessOomList;
                        protoOutputStream.write(1112396529665L, j4);
                        protoOutputStream.write(1112396529666L, j5);
                        protoOutputStream.write(1108101562371L, (j5 * 100.0d) / j4);
                        protoOutputStream.end(start3);
                        protoOutputStream.end(start2);
                    }
                }
                arrayList = sortProcessOomList;
                protoOutputStream.end(start2);
            } else {
                arrayList = sortProcessOomList;
            }
            protoOutputStream.end(start);
            size--;
            sortProcessOomList = arrayList;
            uptimeMillis = j2;
            r7 = 1;
        }
        return r7;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static boolean dumpProcessOomList(PrintWriter printWriter, ActivityManagerService activityManagerService, List<ProcessRecord> list, String str, String str2, String str3, boolean z, String str4) {
        char c;
        char c2;
        ArrayList<Pair<ProcessRecord, Integer>> sortProcessOomList = sortProcessOomList(list, str4);
        boolean z2 = false;
        if (sortProcessOomList.isEmpty()) {
            return false;
        }
        long uptimeMillis = SystemClock.uptimeMillis() - activityManagerService.mLastPowerCheckUptime;
        int i = 1;
        int size = sortProcessOomList.size() - 1;
        while (size >= 0) {
            ProcessRecord processRecord = (ProcessRecord) sortProcessOomList.get(size).first;
            ProcessStateRecord processStateRecord = processRecord.mState;
            ProcessServiceRecord processServiceRecord = processRecord.mServices;
            String makeOomAdjString = makeOomAdjString(processStateRecord.getSetAdj(), z2);
            int setSchedGroup = processStateRecord.getSetSchedGroup();
            char c3 = setSchedGroup != 0 ? setSchedGroup != i ? setSchedGroup != 2 ? setSchedGroup != 3 ? setSchedGroup != 4 ? '?' : 'B' : 'T' : 'F' : 'R' : 'b';
            if (processStateRecord.hasForegroundActivities()) {
                c = 'A';
            } else {
                c = processServiceRecord.hasForegroundServices() ? 'S' : ' ';
            }
            String makeProcStateString = makeProcStateString(processStateRecord.getCurProcState());
            printWriter.print(str);
            printWriter.print(processRecord.isPersistent() ? str3 : str2);
            printWriter.print(" #");
            int size2 = (list.size() - i) - ((Integer) sortProcessOomList.get(size).second).intValue();
            if (size2 < 10) {
                c2 = ' ';
                printWriter.print(' ');
            } else {
                c2 = ' ';
            }
            printWriter.print(size2);
            printWriter.print(": ");
            printWriter.print(makeOomAdjString);
            printWriter.print(c2);
            printWriter.print(c3);
            printWriter.print('/');
            printWriter.print(c);
            printWriter.print('/');
            printWriter.print(makeProcStateString);
            printWriter.print(c2);
            ActivityManager.printCapabilitiesSummary(printWriter, processStateRecord.getCurCapability());
            printWriter.print(c2);
            printWriter.print(" t:");
            if (processRecord.mProfile.getTrimMemoryLevel() < 10) {
                printWriter.print(c2);
            }
            printWriter.print(processRecord.mProfile.getTrimMemoryLevel());
            printWriter.print(c2);
            printWriter.print(processRecord.toShortString());
            printWriter.print(" (");
            printWriter.print(processStateRecord.getAdjType());
            printWriter.println(')');
            if (processStateRecord.getAdjSource() != null || processStateRecord.getAdjTarget() != null) {
                printWriter.print(str);
                printWriter.print("    ");
                if (processStateRecord.getAdjTarget() instanceof ComponentName) {
                    printWriter.print(((ComponentName) processStateRecord.getAdjTarget()).flattenToShortString());
                } else if (processStateRecord.getAdjTarget() != null) {
                    printWriter.print(processStateRecord.getAdjTarget().toString());
                } else {
                    printWriter.print("{null}");
                }
                printWriter.print("<=");
                if (processStateRecord.getAdjSource() instanceof ProcessRecord) {
                    printWriter.print("Proc{");
                    printWriter.print(((ProcessRecord) processStateRecord.getAdjSource()).toShortString());
                    printWriter.println("}");
                } else if (processStateRecord.getAdjSource() != null) {
                    printWriter.println(processStateRecord.getAdjSource().toString());
                } else {
                    printWriter.println("{null}");
                }
            }
            if (z) {
                printWriter.print(str);
                printWriter.print("    ");
                printWriter.print("oom: max=");
                printWriter.print(processStateRecord.getMaxAdj());
                printWriter.print(" curRaw=");
                printWriter.print(processStateRecord.getCurRawAdj());
                printWriter.print(" setRaw=");
                printWriter.print(processStateRecord.getSetRawAdj());
                printWriter.print(" cur=");
                printWriter.print(processStateRecord.getCurAdj());
                printWriter.print(" set=");
                printWriter.println(processStateRecord.getSetAdj());
                printWriter.print(str);
                printWriter.print("    ");
                printWriter.print("state: cur=");
                printWriter.print(makeProcStateString(processStateRecord.getCurProcState()));
                printWriter.print(" set=");
                printWriter.print(makeProcStateString(processStateRecord.getSetProcState()));
                printWriter.print(" lastPss=");
                DebugUtils.printSizeValue(printWriter, processRecord.mProfile.getLastPss() * 1024);
                printWriter.print(" lastSwapPss=");
                DebugUtils.printSizeValue(printWriter, processRecord.mProfile.getLastSwapPss() * 1024);
                printWriter.print(" lastCachedPss=");
                DebugUtils.printSizeValue(printWriter, processRecord.mProfile.getLastCachedPss() * 1024);
                printWriter.println();
                printWriter.print(str);
                printWriter.print("    ");
                printWriter.print("cached=");
                printWriter.print(processStateRecord.isCached());
                printWriter.print(" empty=");
                printWriter.print(processStateRecord.isEmpty());
                printWriter.print(" hasAboveClient=");
                printWriter.println(processServiceRecord.hasAboveClient());
                if (processStateRecord.getSetProcState() >= 10) {
                    long j = processRecord.mProfile.mLastCpuTime.get();
                    if (j != 0 && uptimeMillis > 0) {
                        long j2 = processRecord.mProfile.mCurCpuTime.get() - j;
                        printWriter.print(str);
                        printWriter.print("    ");
                        printWriter.print("run cpu over ");
                        TimeUtils.formatDuration(uptimeMillis, printWriter);
                        printWriter.print(" used ");
                        TimeUtils.formatDuration(j2, printWriter);
                        printWriter.print(" (");
                        printWriter.print((j2 * 100) / uptimeMillis);
                        printWriter.println("%)");
                    }
                }
            }
            size--;
            z2 = false;
            i = 1;
        }
        return i;
    }

    public final void printOomLevel(PrintWriter printWriter, String str, int i) {
        printWriter.print("    ");
        if (i >= 0) {
            printWriter.print(' ');
            if (i < 10) {
                printWriter.print(' ');
            }
        } else if (i > -10) {
            printWriter.print(' ');
        }
        printWriter.print(i);
        printWriter.print(": ");
        printWriter.print(str);
        printWriter.print(" (");
        printWriter.print(ActivityManagerService.stringifySize(getMemLevel(i), 1024));
        printWriter.println(")");
    }

    @GuardedBy({"mService"})
    public boolean dumpOomLocked(FileDescriptor fileDescriptor, PrintWriter printWriter, boolean z, String[] strArr, int i, boolean z2, String str, boolean z3) {
        boolean z4;
        if (getLruSizeLOSP() > 0) {
            if (z) {
                printWriter.println();
            }
            printWriter.println("  OOM levels:");
            printOomLevel(printWriter, "SYSTEM_ADJ", -900);
            printOomLevel(printWriter, "PERSISTENT_PROC_ADJ", -800);
            printOomLevel(printWriter, "PERSISTENT_SERVICE_ADJ", -700);
            printOomLevel(printWriter, "FOREGROUND_APP_ADJ", 0);
            printOomLevel(printWriter, "VISIBLE_APP_ADJ", 100);
            printOomLevel(printWriter, "PERCEPTIBLE_APP_ADJ", 200);
            printOomLevel(printWriter, "PERCEPTIBLE_MEDIUM_APP_ADJ", 225);
            printOomLevel(printWriter, "PERCEPTIBLE_LOW_APP_ADJ", 250);
            printOomLevel(printWriter, "BACKUP_APP_ADJ", 300);
            printOomLevel(printWriter, "HEAVY_WEIGHT_APP_ADJ", FrameworkStatsLog.APP_PROCESS_DIED__IMPORTANCE__IMPORTANCE_BACKGROUND);
            printOomLevel(printWriter, "SERVICE_ADJ", 500);
            printOomLevel(printWriter, "HOME_APP_ADJ", 600);
            printOomLevel(printWriter, "PREVIOUS_APP_ADJ", 700);
            printOomLevel(printWriter, "SERVICE_B_ADJ", 800);
            printOomLevel(printWriter, "CACHED_APP_MIN_ADJ", 900);
            printOomLevel(printWriter, "CACHED_APP_MAX_ADJ", 999);
            printWriter.println();
            printWriter.print("  Process OOM control (");
            printWriter.print(getLruSizeLOSP());
            printWriter.print(" total, non-act at ");
            printWriter.print(getLruSizeLOSP() - this.mLruProcessActivityStart);
            printWriter.print(", non-svc at ");
            printWriter.print(getLruSizeLOSP() - this.mLruProcessServiceStart);
            printWriter.println("):");
            dumpProcessOomList(printWriter, this.mService, this.mLruProcesses, "    ", "Proc", "PERS", true, str);
            z4 = true;
        } else {
            z4 = z;
        }
        synchronized (this.mService.mAppProfiler.mProfilerLock) {
            this.mService.mAppProfiler.dumpProcessesToGc(printWriter, z4, str);
        }
        printWriter.println();
        this.mService.mAtmInternal.dumpForOom(printWriter);
        return true;
    }

    public void registerProcessObserver(IProcessObserver iProcessObserver) {
        this.mProcessObservers.register(iProcessObserver);
    }

    public void unregisterProcessObserver(IProcessObserver iProcessObserver) {
        this.mProcessObservers.unregister(iProcessObserver);
    }

    public void dispatchProcessesChanged() {
        int size;
        int i;
        synchronized (this.mProcessChangeLock) {
            size = this.mPendingProcessChanges.size();
            if (this.mActiveProcessChanges.length < size) {
                this.mActiveProcessChanges = new ActivityManagerService.ProcessChangeItem[size];
            }
            this.mPendingProcessChanges.toArray(this.mActiveProcessChanges);
            this.mPendingProcessChanges.clear();
        }
        int beginBroadcast = this.mProcessObservers.beginBroadcast();
        while (true) {
            i = 0;
            if (beginBroadcast <= 0) {
                break;
            }
            beginBroadcast--;
            IProcessObserver broadcastItem = this.mProcessObservers.getBroadcastItem(beginBroadcast);
            if (broadcastItem != null) {
                while (i < size) {
                    try {
                        ActivityManagerService.ProcessChangeItem processChangeItem = this.mActiveProcessChanges[i];
                        if ((processChangeItem.changes & 1) != 0) {
                            broadcastItem.onForegroundActivitiesChanged(processChangeItem.pid, processChangeItem.uid, processChangeItem.foregroundActivities);
                        }
                        if ((processChangeItem.changes & 2) != 0) {
                            broadcastItem.onForegroundServicesChanged(processChangeItem.pid, processChangeItem.uid, processChangeItem.foregroundServiceTypes);
                        }
                        i++;
                    } catch (RemoteException unused) {
                    }
                }
            }
        }
        this.mProcessObservers.finishBroadcast();
        synchronized (this.mProcessChangeLock) {
            while (i < size) {
                this.mAvailProcessChanges.add(this.mActiveProcessChanges[i]);
                i++;
            }
        }
    }

    @GuardedBy({"mService"})
    public ActivityManagerService.ProcessChangeItem enqueueProcessChangeItemLocked(int i, int i2) {
        ActivityManagerService.ProcessChangeItem processChangeItem;
        ActivityManagerService.ProcessChangeItem processChangeItem2;
        synchronized (this.mProcessChangeLock) {
            int size = this.mPendingProcessChanges.size() - 1;
            processChangeItem = null;
            while (size >= 0) {
                processChangeItem = this.mPendingProcessChanges.get(size);
                if (processChangeItem.pid == i) {
                    break;
                }
                size--;
            }
            if (size < 0) {
                int size2 = this.mAvailProcessChanges.size();
                if (size2 > 0) {
                    processChangeItem2 = this.mAvailProcessChanges.remove(size2 - 1);
                } else {
                    processChangeItem2 = new ActivityManagerService.ProcessChangeItem();
                }
                processChangeItem = processChangeItem2;
                processChangeItem.changes = 0;
                processChangeItem.pid = i;
                processChangeItem.uid = i2;
                if (this.mPendingProcessChanges.size() == 0) {
                    this.mService.mUiHandler.obtainMessage(31).sendToTarget();
                }
                this.mPendingProcessChanges.add(processChangeItem);
            }
        }
        return processChangeItem;
    }

    @GuardedBy({"mService"})
    public void scheduleDispatchProcessDiedLocked(int i, int i2) {
        synchronized (this.mProcessChangeLock) {
            for (int size = this.mPendingProcessChanges.size() - 1; size >= 0; size--) {
                ActivityManagerService.ProcessChangeItem processChangeItem = this.mPendingProcessChanges.get(size);
                if (i > 0 && processChangeItem.pid == i) {
                    this.mPendingProcessChanges.remove(size);
                    this.mAvailProcessChanges.add(processChangeItem);
                }
            }
            this.mService.mUiHandler.obtainMessage(32, i, i2, null).sendToTarget();
        }
    }

    public void dispatchProcessDied(int i, int i2) {
        int beginBroadcast = this.mProcessObservers.beginBroadcast();
        while (beginBroadcast > 0) {
            beginBroadcast--;
            IProcessObserver broadcastItem = this.mProcessObservers.getBroadcastItem(beginBroadcast);
            if (broadcastItem != null) {
                try {
                    broadcastItem.onProcessDied(i, i2);
                } catch (RemoteException unused) {
                }
            }
        }
        this.mProcessObservers.finishBroadcast();
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public ArrayList<ProcessRecord> collectProcessesLOSP(int i, boolean z, String[] strArr) {
        int i2;
        if (strArr != null && strArr.length > i && strArr[i].charAt(0) != '-') {
            ArrayList<ProcessRecord> arrayList = new ArrayList<>();
            try {
                i2 = Integer.parseInt(strArr[i]);
            } catch (NumberFormatException unused) {
                i2 = -1;
            }
            for (int size = this.mLruProcesses.size() - 1; size >= 0; size--) {
                ProcessRecord processRecord = this.mLruProcesses.get(size);
                if (processRecord.getPid() > 0 && processRecord.getPid() == i2) {
                    arrayList.add(processRecord);
                } else if (z && processRecord.getPkgList() != null && processRecord.getPkgList().containsKey(strArr[i])) {
                    arrayList.add(processRecord);
                } else if (processRecord.processName.equals(strArr[i])) {
                    arrayList.add(processRecord);
                }
            }
            if (arrayList.size() <= 0) {
                return null;
            }
            return arrayList;
        }
        return new ArrayList<>(this.mLruProcesses);
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public void updateApplicationInfoLOSP(final List<String> list, int i, final boolean z) {
        final ArrayList arrayList = new ArrayList();
        for (int size = this.mLruProcesses.size() - 1; size >= 0; size--) {
            final ProcessRecord processRecord = this.mLruProcesses.get(size);
            if (processRecord.getThread() != null && (i == -1 || processRecord.userId == i)) {
                processRecord.getPkgList().forEachPackage(new Consumer() { // from class: com.android.server.am.ProcessList$$ExternalSyntheticLambda2
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ProcessList.lambda$updateApplicationInfoLOSP$2(z, list, processRecord, arrayList, (String) obj);
                    }
                });
            }
        }
        this.mService.mActivityTaskManager.updateAssetConfiguration(arrayList, z);
    }

    public static /* synthetic */ void lambda$updateApplicationInfoLOSP$2(boolean z, List list, ProcessRecord processRecord, ArrayList arrayList, String str) {
        if (z || list.contains(str)) {
            try {
                ApplicationInfo applicationInfo = AppGlobals.getPackageManager().getApplicationInfo(str, 1024L, processRecord.userId);
                if (applicationInfo != null) {
                    if (applicationInfo.packageName.equals(processRecord.info.packageName)) {
                        processRecord.info = applicationInfo;
                        PlatformCompatCache.getInstance().onApplicationInfoChanged(applicationInfo);
                    }
                    processRecord.getThread().scheduleApplicationInfoChanged(applicationInfo);
                    arrayList.add(processRecord.getWindowProcessController());
                }
            } catch (RemoteException unused) {
                Slog.w("ActivityManager", String.format("Failed to update %s ApplicationInfo for %s", str, processRecord));
            }
        }
    }

    @GuardedBy({"mService"})
    public void sendPackageBroadcastLocked(int i, String[] strArr, int i2) {
        boolean z = false;
        for (int size = this.mLruProcesses.size() - 1; size >= 0; size--) {
            ProcessRecord processRecord = this.mLruProcesses.get(size);
            IApplicationThread thread = processRecord.getThread();
            if (thread != null && (i2 == -1 || processRecord.userId == i2)) {
                try {
                    for (int length = strArr.length - 1; length >= 0 && !z; length--) {
                        if (strArr[length].equals(processRecord.info.packageName)) {
                            z = true;
                        }
                    }
                    thread.dispatchPackageBroadcast(i, strArr);
                } catch (RemoteException unused) {
                }
            }
        }
        if (z) {
            return;
        }
        try {
            AppGlobals.getPackageManager().notifyPackagesReplacedReceived(strArr);
        } catch (RemoteException unused2) {
        }
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public int getUidProcStateLOSP(int i) {
        UidRecord uidRecord = this.mActiveUids.get(i);
        if (uidRecord == null) {
            return 20;
        }
        return uidRecord.getCurProcState();
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public int getUidProcessCapabilityLOSP(int i) {
        UidRecord uidRecord = this.mActiveUids.get(i);
        if (uidRecord == null) {
            return 0;
        }
        return uidRecord.getCurCapability();
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    public UidRecord getUidRecordLOSP(int i) {
        return this.mActiveUids.get(i);
    }

    @GuardedBy({"mService"})
    public void doStopUidForIdleUidsLocked() {
        int size = this.mActiveUids.size();
        for (int i = 0; i < size; i++) {
            if (!UserHandle.isCore(this.mActiveUids.keyAt(i))) {
                UidRecord valueAt = this.mActiveUids.valueAt(i);
                if (valueAt.isIdle()) {
                    this.mService.doStopUidLocked(valueAt.getUid(), valueAt);
                }
            }
        }
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    @VisibleForTesting
    public int getBlockStateForUid(UidRecord uidRecord) {
        boolean z = NetworkPolicyManager.isProcStateAllowedWhileIdleOrPowerSaveMode(uidRecord.getCurProcState(), uidRecord.getCurCapability()) || NetworkPolicyManager.isProcStateAllowedWhileOnRestrictBackground(uidRecord.getCurProcState());
        boolean z2 = NetworkPolicyManager.isProcStateAllowedWhileIdleOrPowerSaveMode(uidRecord.getSetProcState(), uidRecord.getSetCapability()) || NetworkPolicyManager.isProcStateAllowedWhileOnRestrictBackground(uidRecord.getSetProcState());
        if (z2 || !z) {
            return (!z2 || z) ? 0 : 2;
        }
        return 1;
    }

    @GuardedBy(anyOf = {"mService", "mProcLock"})
    @VisibleForTesting
    public void incrementProcStateSeqAndNotifyAppsLOSP(ActiveUids activeUids) {
        UidRecord uidRecordLOSP;
        int blockStateForUid;
        for (int size = activeUids.size() - 1; size >= 0; size--) {
            activeUids.valueAt(size).curProcStateSeq = getNextProcStateSeq();
        }
        if (this.mService.mConstants.mNetworkAccessTimeoutMs <= 0) {
            return;
        }
        ArrayList arrayList = null;
        for (int size2 = activeUids.size() - 1; size2 >= 0; size2--) {
            UidRecord valueAt = activeUids.valueAt(size2);
            if (this.mService.mInjector.isNetworkRestrictedForUid(valueAt.getUid()) && UserHandle.isApp(valueAt.getUid()) && valueAt.hasInternetPermission && ((valueAt.getSetProcState() != valueAt.getCurProcState() || valueAt.getSetCapability() != valueAt.getCurCapability()) && (blockStateForUid = getBlockStateForUid(valueAt)) != 0)) {
                synchronized (valueAt.networkStateLock) {
                    if (blockStateForUid == 1) {
                        if (arrayList == null) {
                            arrayList = new ArrayList();
                        }
                        arrayList.add(Integer.valueOf(valueAt.getUid()));
                    } else if (valueAt.procStateSeqWaitingForNetwork != 0) {
                        valueAt.networkStateLock.notifyAll();
                    }
                }
            }
        }
        if (arrayList == null) {
            return;
        }
        for (int size3 = this.mLruProcesses.size() - 1; size3 >= 0; size3--) {
            ProcessRecord processRecord = this.mLruProcesses.get(size3);
            if (arrayList.contains(Integer.valueOf(processRecord.uid))) {
                IApplicationThread thread = processRecord.getThread();
                if (!processRecord.isKilledByAm() && thread != null && (uidRecordLOSP = getUidRecordLOSP(processRecord.uid)) != null) {
                    try {
                        thread.setNetworkBlockSeq(uidRecordLOSP.curProcStateSeq);
                    } catch (RemoteException unused) {
                    }
                }
            }
        }
    }

    public long getNextProcStateSeq() {
        long j = this.mProcStateSeqCounter + 1;
        this.mProcStateSeqCounter = j;
        return j;
    }

    public final LocalSocket createSystemServerSocketForZygote() {
        LocalSocket localSocket;
        File file = new File("/data/system/unsolzygotesocket");
        if (file.exists()) {
            file.delete();
        }
        try {
            localSocket = new LocalSocket(1);
            try {
                localSocket.bind(new LocalSocketAddress("/data/system/unsolzygotesocket", LocalSocketAddress.Namespace.FILESYSTEM));
                Os.chmod("/data/system/unsolzygotesocket", 438);
            } catch (Exception unused) {
                if (localSocket != null) {
                    try {
                        localSocket.close();
                        return null;
                    } catch (IOException unused2) {
                        return null;
                    }
                }
                return localSocket;
            }
        } catch (Exception unused3) {
            localSocket = null;
        }
        return localSocket;
    }

    public final int handleZygoteMessages(FileDescriptor fileDescriptor, int i) {
        fileDescriptor.getInt$();
        if ((i & 1) != 0) {
            try {
                byte[] bArr = this.mZygoteUnsolicitedMessage;
                int read = Os.read(fileDescriptor, bArr, 0, bArr.length);
                if (read > 0) {
                    int[] iArr = this.mZygoteSigChldMessage;
                    if (iArr.length == Zygote.nativeParseSigChld(this.mZygoteUnsolicitedMessage, read, iArr)) {
                        AppExitInfoTracker appExitInfoTracker = this.mAppExitInfoTracker;
                        int[] iArr2 = this.mZygoteSigChldMessage;
                        appExitInfoTracker.handleZygoteSigChld(iArr2[0], iArr2[1], iArr2[2]);
                    }
                }
            } catch (Exception e) {
                Slog.w("ActivityManager", "Exception in reading unsolicited zygote message: " + e);
            }
        }
        return 1;
    }

    @GuardedBy({"mService"})
    public boolean handleDyingAppDeathLocked(ProcessRecord processRecord, int i) {
        if (this.mProcessNames.get(processRecord.processName, processRecord.uid) == processRecord || this.mDyingProcesses.get(processRecord.processName, processRecord.uid) != processRecord) {
            return false;
        }
        Slog.v("ActivityManager", "Got obituary of " + i + XmlUtils.STRING_ARRAY_SEPARATOR + processRecord.processName);
        processRecord.unlinkDeathRecipient();
        this.mDyingProcesses.remove(processRecord.processName, processRecord.uid);
        processRecord.setDyingPid(0);
        handlePrecedingAppDiedLocked(processRecord);
        return true;
    }

    @GuardedBy({"mService"})
    public boolean handlePrecedingAppDiedLocked(ProcessRecord processRecord) {
        if (processRecord.mSuccessor != null) {
            if (processRecord.isPersistent() && !processRecord.isRemoved() && this.mService.mPersistentStartingProcesses.indexOf(processRecord.mSuccessor) < 0) {
                this.mService.mPersistentStartingProcesses.add(processRecord.mSuccessor);
            }
            processRecord.mSuccessor.mPredecessor = null;
            processRecord.mSuccessor = null;
            this.mService.mProcStartHandler.removeMessages(2, processRecord);
            this.mService.mProcStartHandler.obtainMessage(1, processRecord).sendToTarget();
            return false;
        }
        return true;
    }

    @GuardedBy({"mService"})
    public void updateBackgroundRestrictedForUidPackageLocked(int i, final String str, final boolean z) {
        UidRecord uidRecordLOSP = getUidRecordLOSP(i);
        if (uidRecordLOSP != null) {
            final long elapsedRealtime = SystemClock.elapsedRealtime();
            uidRecordLOSP.forEachProcess(new Consumer() { // from class: com.android.server.am.ProcessList$$ExternalSyntheticLambda5
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ProcessList.this.lambda$updateBackgroundRestrictedForUidPackageLocked$3(str, z, elapsedRealtime, (ProcessRecord) obj);
                }
            });
            this.mService.updateOomAdjPendingTargetsLocked(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateBackgroundRestrictedForUidPackageLocked$3(String str, boolean z, long j, ProcessRecord processRecord) {
        if (TextUtils.equals(processRecord.info.packageName, str)) {
            processRecord.mState.setBackgroundRestricted(z);
            if (z) {
                this.mAppsInBackgroundRestricted.add(processRecord);
                long lambda$killAppIfBgRestrictedAndCachedIdleLocked$4 = lambda$killAppIfBgRestrictedAndCachedIdleLocked$4(processRecord, j);
                if (lambda$killAppIfBgRestrictedAndCachedIdleLocked$4 > 0 && !this.mService.mHandler.hasMessages(58)) {
                    this.mService.mHandler.sendEmptyMessageDelayed(58, lambda$killAppIfBgRestrictedAndCachedIdleLocked$4 - j);
                }
            } else {
                this.mAppsInBackgroundRestricted.remove(processRecord);
            }
            if (processRecord.isKilledByAm()) {
                return;
            }
            this.mService.enqueueOomAdjTargetLocked(processRecord);
        }
    }

    @GuardedBy({"mService"})
    /* renamed from: killAppIfBgRestrictedAndCachedIdleLocked */
    public long lambda$killAppIfBgRestrictedAndCachedIdleLocked$4(ProcessRecord processRecord, long j) {
        UidRecord uidRecord = processRecord.getUidRecord();
        long lastCanKillOnBgRestrictedAndIdleTime = processRecord.mState.getLastCanKillOnBgRestrictedAndIdleTime();
        if (!this.mService.mConstants.mKillBgRestrictedAndCachedIdle || processRecord.isKilled() || processRecord.getThread() == null || uidRecord == null || !uidRecord.isIdle() || !processRecord.isCached() || processRecord.mState.shouldNotKillOnBgRestrictedAndIdle() || !processRecord.mState.isBackgroundRestricted() || lastCanKillOnBgRestrictedAndIdleTime == 0) {
            return 0L;
        }
        long j2 = lastCanKillOnBgRestrictedAndIdleTime + this.mService.mConstants.mKillBgRestrictedAndCachedIdleSettleTimeMs;
        if (j2 <= j) {
            processRecord.killLocked("cached idle & background restricted", 13, 18, true);
            return 0L;
        }
        return j2;
    }

    @GuardedBy({"mService"})
    public void killAppIfBgRestrictedAndCachedIdleLocked(UidRecord uidRecord) {
        final long elapsedRealtime = SystemClock.elapsedRealtime();
        uidRecord.forEachProcess(new Consumer() { // from class: com.android.server.am.ProcessList$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ProcessList.this.lambda$killAppIfBgRestrictedAndCachedIdleLocked$4(elapsedRealtime, (ProcessRecord) obj);
            }
        });
    }

    @GuardedBy({"mService"})
    public void noteProcessDiedLocked(ProcessRecord processRecord) {
        Watchdog.getInstance().processDied(processRecord.processName, processRecord.getPid());
        if (processRecord.getDeathRecipient() == null && this.mDyingProcesses.get(processRecord.processName, processRecord.uid) == processRecord) {
            this.mDyingProcesses.remove(processRecord.processName, processRecord.uid);
            processRecord.setDyingPid(0);
        }
        this.mAppExitInfoTracker.scheduleNoteProcessDied(processRecord);
    }

    @GuardedBy({"mService"})
    public void noteAppKill(ProcessRecord processRecord, int i, int i2, String str) {
        if (processRecord.getPid() > 0 && !processRecord.isolated && processRecord.getDeathRecipient() != null) {
            this.mDyingProcesses.put(processRecord.processName, processRecord.uid, processRecord);
            processRecord.setDyingPid(processRecord.getPid());
        }
        this.mAppExitInfoTracker.scheduleNoteAppKill(processRecord, i, i2, str);
    }

    @GuardedBy({"mService"})
    public void noteAppKill(int i, int i2, int i3, int i4, String str) {
        ProcessRecord processRecord;
        synchronized (this.mService.mPidsSelfLocked) {
            processRecord = this.mService.mPidsSelfLocked.get(i);
        }
        if (processRecord != null && processRecord.uid == i2 && !processRecord.isolated && processRecord.getDeathRecipient() != null) {
            this.mDyingProcesses.put(processRecord.processName, i2, processRecord);
            processRecord.setDyingPid(processRecord.getPid());
        }
        this.mAppExitInfoTracker.scheduleNoteAppKill(i, i2, i3, i4, str);
    }

    public void killProcessesWhenImperceptible(int[] iArr, String str, int i) {
        ProcessRecord processRecord;
        if (ArrayUtils.isEmpty(iArr)) {
            return;
        }
        synchronized (this.mService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                for (int i2 : iArr) {
                    synchronized (this.mService.mPidsSelfLocked) {
                        processRecord = this.mService.mPidsSelfLocked.get(i2);
                    }
                    if (processRecord != null) {
                        this.mImperceptibleKillRunner.enqueueLocked(processRecord, str, i);
                    }
                }
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
    }

    public Pair<Integer, Integer> getNumForegroundServices() {
        int i;
        int i2;
        synchronized (this.mService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                int size = this.mLruProcesses.size();
                i = 0;
                i2 = 0;
                for (int i3 = 0; i3 < size; i3++) {
                    int numForegroundServices = this.mLruProcesses.get(i3).mServices.getNumForegroundServices();
                    if (numForegroundServices > 0) {
                        i += numForegroundServices;
                        i2++;
                    }
                }
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
        return new Pair<>(Integer.valueOf(i), Integer.valueOf(i2));
    }

    /* renamed from: com.android.server.am.ProcessList$ImperceptibleKillRunner */
    /* loaded from: classes.dex */
    public final class ImperceptibleKillRunner extends IUidObserver.Stub {
        public Handler mHandler;
        public volatile boolean mIdle;
        public IdlenessReceiver mReceiver;
        public boolean mUidObserverEnabled;
        public SparseArray<List<Bundle>> mWorkItems = new SparseArray<>();
        public ProcessMap<Long> mLastProcessKillTimes = new ProcessMap<>();

        public void onUidActive(int i) {
        }

        public void onUidCachedChanged(int i, boolean z) {
        }

        public void onUidIdle(int i, boolean z) {
        }

        public void onUidProcAdjChanged(int i) {
        }

        /* renamed from: com.android.server.am.ProcessList$ImperceptibleKillRunner$H */
        /* loaded from: classes.dex */
        public final class HandlerC0406H extends Handler {
            public HandlerC0406H(Looper looper) {
                super(looper);
            }

            @Override // android.os.Handler
            public void handleMessage(Message message) {
                int i = message.what;
                if (i == 0) {
                    ImperceptibleKillRunner.this.handleDeviceIdle();
                } else if (i == 1) {
                    ImperceptibleKillRunner.this.handleUidGone(message.arg1);
                } else if (i != 2) {
                } else {
                    ImperceptibleKillRunner.this.handleUidStateChanged(message.arg1, message.arg2);
                }
            }
        }

        /* renamed from: com.android.server.am.ProcessList$ImperceptibleKillRunner$IdlenessReceiver */
        /* loaded from: classes.dex */
        public final class IdlenessReceiver extends BroadcastReceiver {
            public IdlenessReceiver() {
            }

            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                PowerManager powerManager = (PowerManager) ProcessList.this.mService.mContext.getSystemService(PowerManager.class);
                String action = intent.getAction();
                action.hashCode();
                if (action.equals("android.os.action.LIGHT_DEVICE_IDLE_MODE_CHANGED")) {
                    ImperceptibleKillRunner.this.notifyDeviceIdleness(powerManager.isLightDeviceIdleMode());
                } else if (action.equals("android.os.action.DEVICE_IDLE_MODE_CHANGED")) {
                    ImperceptibleKillRunner.this.notifyDeviceIdleness(powerManager.isDeviceIdleMode());
                }
            }
        }

        public ImperceptibleKillRunner(Looper looper) {
            this.mHandler = new HandlerC0406H(looper);
        }

        @GuardedBy({"mService"})
        public boolean enqueueLocked(ProcessRecord processRecord, String str, int i) {
            Long l = processRecord.isolated ? null : (Long) this.mLastProcessKillTimes.get(processRecord.processName, processRecord.uid);
            if (l == null || SystemClock.uptimeMillis() >= l.longValue() + ActivityManagerConstants.MIN_CRASH_INTERVAL) {
                Bundle bundle = new Bundle();
                bundle.putInt("pid", processRecord.getPid());
                bundle.putInt("uid", processRecord.uid);
                bundle.putLong("timestamp", processRecord.getStartTime());
                bundle.putString("reason", str);
                bundle.putInt("requester", i);
                List<Bundle> list = this.mWorkItems.get(processRecord.uid);
                if (list == null) {
                    list = new ArrayList<>();
                    this.mWorkItems.put(processRecord.uid, list);
                }
                list.add(bundle);
                if (this.mReceiver == null) {
                    this.mReceiver = new IdlenessReceiver();
                    IntentFilter intentFilter = new IntentFilter("android.os.action.LIGHT_DEVICE_IDLE_MODE_CHANGED");
                    intentFilter.addAction("android.os.action.DEVICE_IDLE_MODE_CHANGED");
                    ProcessList.this.mService.mContext.registerReceiver(this.mReceiver, intentFilter);
                    return true;
                }
                return true;
            }
            return false;
        }

        public void notifyDeviceIdleness(boolean z) {
            boolean z2 = this.mIdle != z;
            this.mIdle = z;
            if (z2 && z) {
                synchronized (ProcessList.this.mService) {
                    try {
                        ActivityManagerService.boostPriorityForLockedSection();
                        if (this.mWorkItems.size() > 0) {
                            this.mHandler.sendEmptyMessage(0);
                        }
                    } catch (Throwable th) {
                        ActivityManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
                ActivityManagerService.resetPriorityAfterLockedSection();
            }
        }

        public final void handleDeviceIdle() {
            DropBoxManager dropBoxManager = (DropBoxManager) ProcessList.this.mService.mContext.getSystemService(DropBoxManager.class);
            synchronized (ProcessList.this.mService) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    for (int size = this.mWorkItems.size() - 1; this.mIdle && size >= 0; size--) {
                        List<Bundle> valueAt = this.mWorkItems.valueAt(size);
                        for (int size2 = valueAt.size() - 1; this.mIdle && size2 >= 0; size2--) {
                            Bundle bundle = valueAt.get(size2);
                            if (killProcessLocked(bundle.getInt("pid"), bundle.getInt("uid"), bundle.getLong("timestamp"), bundle.getString("reason"), bundle.getInt("requester"), dropBoxManager, false)) {
                                valueAt.remove(size2);
                            }
                        }
                        if (valueAt.size() == 0) {
                            this.mWorkItems.removeAt(size);
                        }
                    }
                    registerUidObserverIfNecessaryLocked();
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }

        @GuardedBy({"mService"})
        public final void registerUidObserverIfNecessaryLocked() {
            if (!this.mUidObserverEnabled && this.mWorkItems.size() > 0) {
                this.mUidObserverEnabled = true;
                ProcessList.this.mService.registerUidObserver(this, 3, -1, PackageManagerShellCommandDataLoader.PACKAGE);
            } else if (this.mUidObserverEnabled && this.mWorkItems.size() == 0) {
                this.mUidObserverEnabled = false;
                ProcessList.this.mService.unregisterUidObserver(this);
            }
        }

        @GuardedBy({"mService"})
        public final boolean killProcessLocked(int i, int i2, long j, String str, int i3, DropBoxManager dropBoxManager, boolean z) {
            ProcessRecord processRecord;
            synchronized (ProcessList.this.mService.mPidsSelfLocked) {
                processRecord = ProcessList.this.mService.mPidsSelfLocked.get(i);
            }
            if (processRecord != null && processRecord.getPid() == i && processRecord.uid == i2 && processRecord.getStartTime() == j && processRecord.getPkgList().searchEachPackage(new Function() { // from class: com.android.server.am.ProcessList$ImperceptibleKillRunner$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    Boolean lambda$killProcessLocked$0;
                    lambda$killProcessLocked$0 = ProcessList.ImperceptibleKillRunner.this.lambda$killProcessLocked$0((String) obj);
                    return lambda$killProcessLocked$0;
                }
            }) == null) {
                if (ProcessList.this.mService.mConstants.IMPERCEPTIBLE_KILL_EXEMPT_PROC_STATES.contains(Integer.valueOf(processRecord.mState.getReportedProcState()))) {
                    return false;
                }
                processRecord.killLocked(str, 13, 15, true);
                if (!processRecord.isolated) {
                    this.mLastProcessKillTimes.put(processRecord.processName, processRecord.uid, Long.valueOf(SystemClock.uptimeMillis()));
                }
                if (z) {
                    SystemClock.elapsedRealtime();
                    StringBuilder sb = new StringBuilder();
                    ProcessList.this.mService.appendDropBoxProcessHeaders(processRecord, processRecord.processName, sb);
                    sb.append("Reason: " + str);
                    sb.append("\n");
                    sb.append("Requester UID: " + i3);
                    sb.append("\n");
                    dropBoxManager.addText("imperceptible_app_kill", sb.toString());
                }
                return true;
            }
            return true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ Boolean lambda$killProcessLocked$0(String str) {
            if (ProcessList.this.mService.mConstants.IMPERCEPTIBLE_KILL_EXEMPT_PACKAGES.contains(str)) {
                return Boolean.TRUE;
            }
            return null;
        }

        public final void handleUidStateChanged(int i, int i2) {
            List<Bundle> list;
            DropBoxManager dropBoxManager = (DropBoxManager) ProcessList.this.mService.mContext.getSystemService(DropBoxManager.class);
            boolean z = dropBoxManager != null && dropBoxManager.isTagEnabled("imperceptible_app_kill");
            synchronized (ProcessList.this.mService) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    if (this.mIdle && !ProcessList.this.mService.mConstants.IMPERCEPTIBLE_KILL_EXEMPT_PROC_STATES.contains(Integer.valueOf(i2)) && (list = this.mWorkItems.get(i)) != null) {
                        for (int size = list.size() - 1; this.mIdle && size >= 0; size--) {
                            Bundle bundle = list.get(size);
                            if (killProcessLocked(bundle.getInt("pid"), bundle.getInt("uid"), bundle.getLong("timestamp"), bundle.getString("reason"), bundle.getInt("requester"), dropBoxManager, z)) {
                                list.remove(size);
                            }
                        }
                        if (list.size() == 0) {
                            this.mWorkItems.remove(i);
                        }
                        registerUidObserverIfNecessaryLocked();
                    }
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }

        public final void handleUidGone(int i) {
            synchronized (ProcessList.this.mService) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    this.mWorkItems.remove(i);
                    registerUidObserverIfNecessaryLocked();
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }

        public void onUidGone(int i, boolean z) {
            this.mHandler.obtainMessage(1, i, 0).sendToTarget();
        }

        public void onUidStateChanged(int i, int i2, long j, int i3) {
            this.mHandler.obtainMessage(2, i, i2).sendToTarget();
        }
    }
}
