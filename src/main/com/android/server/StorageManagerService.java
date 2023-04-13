package com.android.server;

import android.annotation.EnforcePermission;
import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.ActivityOptions;
import android.app.AnrController;
import android.app.AppOpsManager;
import android.app.IActivityManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.app.admin.SecurityLog;
import android.app.usage.StorageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.IPackageMoveObserver;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.UserInfo;
import android.content.res.ObbInfo;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.DropBoxManager;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.ParcelableException;
import android.os.PersistableBundle;
import android.os.Process;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.ServiceSpecificException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.DiskInfo;
import android.os.storage.IObbActionListener;
import android.os.storage.IStorageEventListener;
import android.os.storage.IStorageManager;
import android.os.storage.IStorageShutdownObserver;
import android.os.storage.StorageManager;
import android.os.storage.StorageManagerInternal;
import android.os.storage.StorageVolume;
import android.os.storage.VolumeInfo;
import android.os.storage.VolumeRecord;
import android.p005os.IInstalld;
import android.p005os.IStoraged;
import android.p005os.IVold;
import android.p005os.IVoldListener;
import android.p005os.IVoldMountCallback;
import android.p005os.IVoldTaskListener;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.DataUnit;
import android.util.EventLog;
import android.util.Log;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.TimeUtils;
import android.util.Xml;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.app.IAppOpsService;
import com.android.internal.content.PackageMonitor;
import com.android.internal.os.AppFuseMount;
import com.android.internal.os.BackgroundThread;
import com.android.internal.os.FuseUnavailableMountException;
import com.android.internal.os.SomeArgs;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.HexDump;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.Preconditions;
import com.android.internal.util.XmlUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.server.SystemService;
import com.android.server.Watchdog;
import com.android.server.p011pm.Installer;
import com.android.server.p011pm.UserManagerInternal;
import com.android.server.p014wm.ActivityTaskManagerInternal;
import com.android.server.storage.AppFuseBridge;
import com.android.server.storage.StorageSessionController;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import libcore.io.IoUtils;
import libcore.util.EmptyArray;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class StorageManagerService extends IStorageManager.Stub implements Watchdog.Monitor, ActivityTaskManagerInternal.ScreenObserver {
    public static String sMediaStoreAuthorityProcessName;
    public static StorageManagerService sSelf;
    public final Callbacks mCallbacks;
    public volatile boolean mChargingRequired;
    public final Context mContext;
    public volatile float mDirtyReclaimRate;
    public final Handler mHandler;
    public IAppOpsService mIAppOpsService;
    public IPackageManager mIPackageManager;
    public final Installer mInstaller;
    public long mLastMaintenance;
    public final File mLastMaintenanceFile;
    public volatile int mLifetimePercentThreshold;
    public volatile float mLowBatteryLevel;
    public volatile int mMaxWriteRecords;
    public volatile int mMinGCSleepTime;
    public volatile int mMinSegmentsThreshold;
    @GuardedBy({"mLock"})
    public IPackageMoveObserver mMoveCallback;
    @GuardedBy({"mLock"})
    public String mMoveTargetUuid;
    public final ObbActionHandler mObbActionHandler;
    public volatile boolean mPassedLifetimeThresh;
    public PackageManagerInternal mPmInternal;
    @GuardedBy({"mLock"})
    public String mPrimaryStorageUuid;
    public volatile float mSegmentReclaimWeight;
    public final AtomicFile mSettingsFile;
    public final StorageSessionController mStorageSessionController;
    public volatile int[] mStorageWriteRecords;
    public volatile IStoraged mStoraged;
    public volatile int mTargetDirtyRatio;
    public volatile IVold mVold;
    public final boolean mVoldAppDataIsolationEnabled;
    public final AtomicFile mWriteRecordFile;
    public static final boolean LOCAL_LOGV = Log.isLoggable("StorageManagerService", 2);
    public static volatile int sSmartIdleMaintPeriod = 60;
    public static final Pattern KNOWN_APP_DIR_PATHS = Pattern.compile("(?i)(^/storage/[^/]+/(?:([0-9]+)/)?Android/(?:data|media|obb|sandbox)/)([^/]+)(/.*)?");
    @GuardedBy({"mLock"})
    public final Set<Integer> mFuseMountedUser = new ArraySet();
    @GuardedBy({"mLock"})
    public final Set<Integer> mCeStoragePreparedUsers = new ArraySet();
    public volatile boolean mNeedGC = true;
    public final Object mLock = LockGuard.installNewLock(4);
    @GuardedBy({"mLock"})
    public WatchedLockedUsers mLocalUnlockedUsers = new WatchedLockedUsers();
    @GuardedBy({"mLock"})
    public int[] mSystemUnlockedUsers = EmptyArray.INT;
    @GuardedBy({"mLock"})
    public ArrayMap<String, DiskInfo> mDisks = new ArrayMap<>();
    @GuardedBy({"mLock"})
    public final ArrayMap<String, VolumeInfo> mVolumes = new ArrayMap<>();
    @GuardedBy({"mLock"})
    public ArrayMap<String, VolumeRecord> mRecords = new ArrayMap<>();
    @GuardedBy({"mLock"})
    public ArrayMap<String, CountDownLatch> mDiskScanLatches = new ArrayMap<>();
    @GuardedBy({"mCloudMediaProviders"})
    public final SparseArray<String> mCloudMediaProviders = new SparseArray<>();
    public volatile int mMediaStoreAuthorityAppId = -1;
    public volatile int mDownloadsAuthorityAppId = -1;
    public volatile int mExternalStorageAuthorityAppId = -1;
    public volatile int mCurrentUserId = 0;
    public volatile boolean mRemountCurrentUserVolumesOnUnlock = false;
    public final Object mAppFuseLock = new Object();
    @GuardedBy({"mAppFuseLock"})
    public int mNextAppFuseName = 0;
    @GuardedBy({"mAppFuseLock"})
    public AppFuseBridge mAppFuseBridge = null;
    public final SparseIntArray mUserSharesMediaWith = new SparseIntArray();
    public volatile boolean mBootCompleted = false;
    public volatile boolean mDaemonConnected = false;
    public volatile boolean mSecureKeyguardShowing = true;
    public final Map<IBinder, List<ObbState>> mObbMounts = new HashMap();
    public final Map<String, ObbState> mObbPathToStateMap = new HashMap();
    public final StorageManagerInternalImpl mStorageManagerInternal = new StorageManagerInternalImpl();
    @GuardedBy({"mLock"})
    public final Set<Integer> mUidsWithLegacyExternalStorage = new ArraySet();
    public final SparseArray<PackageMonitor> mPackageMonitorsForUser = new SparseArray<>();
    public BroadcastReceiver mUserReceiver = new BroadcastReceiver() { // from class: com.android.server.StorageManagerService.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int intExtra = intent.getIntExtra("android.intent.extra.user_handle", -1);
            Preconditions.checkArgument(intExtra >= 0);
            try {
                if ("android.intent.action.USER_ADDED".equals(action)) {
                    UserManager userManager = (UserManager) StorageManagerService.this.mContext.getSystemService(UserManager.class);
                    int userSerialNumber = userManager.getUserSerialNumber(intExtra);
                    UserInfo userInfo = userManager.getUserInfo(intExtra);
                    if (userInfo.isCloneProfile()) {
                        StorageManagerService.this.mVold.onUserAdded(intExtra, userSerialNumber, userInfo.profileGroupId);
                    } else {
                        StorageManagerService.this.mVold.onUserAdded(intExtra, userSerialNumber, -1);
                    }
                } else if ("android.intent.action.USER_REMOVED".equals(action)) {
                    synchronized (StorageManagerService.this.mLock) {
                        int size = StorageManagerService.this.mVolumes.size();
                        for (int i = 0; i < size; i++) {
                            VolumeInfo volumeInfo = (VolumeInfo) StorageManagerService.this.mVolumes.valueAt(i);
                            if (volumeInfo.mountUserId == intExtra) {
                                volumeInfo.mountUserId = -10000;
                                StorageManagerService.this.mHandler.obtainMessage(8, volumeInfo).sendToTarget();
                            }
                        }
                    }
                    StorageManagerService.this.mVold.onUserRemoved(intExtra);
                }
            } catch (Exception e) {
                Slog.wtf("StorageManagerService", e);
            }
        }
    };
    public final IVoldListener mListener = new IVoldListener.Stub() { // from class: com.android.server.StorageManagerService.3
        /* JADX WARN: Removed duplicated region for block: B:17:0x0035  */
        /* JADX WARN: Removed duplicated region for block: B:20:0x003b A[Catch: all -> 0x004d, TryCatch #0 {, blocks: (B:4:0x0007, B:21:0x003d, B:22:0x004b, B:19:0x0038, B:20:0x003b, B:9:0x001e, B:12:0x0028), top: B:27:0x0007 }] */
        @Override // android.p005os.IVoldListener
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public void onDiskCreated(String str, int i) {
            char c;
            synchronized (StorageManagerService.this.mLock) {
                String str2 = SystemProperties.get("persist.sys.adoptable");
                int hashCode = str2.hashCode();
                if (hashCode != 464944051) {
                    if (hashCode == 1528363547 && str2.equals("force_off")) {
                        c = 1;
                        if (c != 0) {
                            i |= 1;
                        } else if (c == 1) {
                            i &= -2;
                        }
                        StorageManagerService.this.mDisks.put(str, new DiskInfo(str, i));
                    }
                    c = 65535;
                    if (c != 0) {
                    }
                    StorageManagerService.this.mDisks.put(str, new DiskInfo(str, i));
                } else {
                    if (str2.equals("force_on")) {
                        c = 0;
                        if (c != 0) {
                        }
                        StorageManagerService.this.mDisks.put(str, new DiskInfo(str, i));
                    }
                    c = 65535;
                    if (c != 0) {
                    }
                    StorageManagerService.this.mDisks.put(str, new DiskInfo(str, i));
                }
            }
        }

        @Override // android.p005os.IVoldListener
        public void onDiskScanned(String str) {
            synchronized (StorageManagerService.this.mLock) {
                DiskInfo diskInfo = (DiskInfo) StorageManagerService.this.mDisks.get(str);
                if (diskInfo != null) {
                    StorageManagerService.this.onDiskScannedLocked(diskInfo);
                }
            }
        }

        @Override // android.p005os.IVoldListener
        public void onDiskMetadataChanged(String str, long j, String str2, String str3) {
            synchronized (StorageManagerService.this.mLock) {
                DiskInfo diskInfo = (DiskInfo) StorageManagerService.this.mDisks.get(str);
                if (diskInfo != null) {
                    diskInfo.size = j;
                    diskInfo.label = str2;
                    diskInfo.sysPath = str3;
                }
            }
        }

        @Override // android.p005os.IVoldListener
        public void onDiskDestroyed(String str) {
            synchronized (StorageManagerService.this.mLock) {
                DiskInfo diskInfo = (DiskInfo) StorageManagerService.this.mDisks.remove(str);
                if (diskInfo != null) {
                    StorageManagerService.this.mCallbacks.notifyDiskDestroyed(diskInfo);
                }
            }
        }

        @Override // android.p005os.IVoldListener
        public void onVolumeCreated(String str, int i, String str2, String str3, int i2) {
            synchronized (StorageManagerService.this.mLock) {
                VolumeInfo volumeInfo = new VolumeInfo(str, i, (DiskInfo) StorageManagerService.this.mDisks.get(str2), str3);
                volumeInfo.mountUserId = i2;
                StorageManagerService.this.mVolumes.put(str, volumeInfo);
                StorageManagerService.this.onVolumeCreatedLocked(volumeInfo);
            }
        }

        @Override // android.p005os.IVoldListener
        public void onVolumeStateChanged(String str, int i) {
            synchronized (StorageManagerService.this.mLock) {
                VolumeInfo volumeInfo = (VolumeInfo) StorageManagerService.this.mVolumes.get(str);
                if (volumeInfo != null) {
                    int i2 = volumeInfo.state;
                    volumeInfo.state = i;
                    VolumeInfo volumeInfo2 = new VolumeInfo(volumeInfo);
                    SomeArgs obtain = SomeArgs.obtain();
                    obtain.arg1 = volumeInfo2;
                    obtain.argi1 = i2;
                    obtain.argi2 = i;
                    StorageManagerService.this.mHandler.obtainMessage(15, obtain).sendToTarget();
                    StorageManagerService.this.onVolumeStateChangedLocked(volumeInfo2, i);
                }
            }
        }

        @Override // android.p005os.IVoldListener
        public void onVolumeMetadataChanged(String str, String str2, String str3, String str4) {
            synchronized (StorageManagerService.this.mLock) {
                VolumeInfo volumeInfo = (VolumeInfo) StorageManagerService.this.mVolumes.get(str);
                if (volumeInfo != null) {
                    volumeInfo.fsType = str2;
                    volumeInfo.fsUuid = str3;
                    volumeInfo.fsLabel = str4;
                }
            }
        }

        @Override // android.p005os.IVoldListener
        public void onVolumePathChanged(String str, String str2) {
            synchronized (StorageManagerService.this.mLock) {
                VolumeInfo volumeInfo = (VolumeInfo) StorageManagerService.this.mVolumes.get(str);
                if (volumeInfo != null) {
                    volumeInfo.path = str2;
                }
            }
        }

        @Override // android.p005os.IVoldListener
        public void onVolumeInternalPathChanged(String str, String str2) {
            synchronized (StorageManagerService.this.mLock) {
                VolumeInfo volumeInfo = (VolumeInfo) StorageManagerService.this.mVolumes.get(str);
                if (volumeInfo != null) {
                    volumeInfo.internalPath = str2;
                }
            }
        }

        @Override // android.p005os.IVoldListener
        public void onVolumeDestroyed(String str) {
            VolumeInfo volumeInfo;
            synchronized (StorageManagerService.this.mLock) {
                volumeInfo = (VolumeInfo) StorageManagerService.this.mVolumes.remove(str);
            }
            if (volumeInfo != null) {
                StorageManagerService.this.mStorageSessionController.onVolumeRemove(volumeInfo);
                try {
                    if (volumeInfo.type == 1) {
                        StorageManagerService.this.mInstaller.onPrivateVolumeRemoved(volumeInfo.getFsUuid());
                    }
                } catch (Installer.InstallerException e) {
                    Slog.i("StorageManagerService", "Failed when private volume unmounted " + volumeInfo, e);
                }
            }
        }
    };

    @Override // com.android.server.p014wm.ActivityTaskManagerInternal.ScreenObserver
    public void onAwakeStateChanged(boolean z) {
    }

    /* loaded from: classes.dex */
    public static class Lifecycle extends SystemService {
        public StorageManagerService mStorageManagerService;

        public Lifecycle(Context context) {
            super(context);
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r0v0, types: [com.android.server.StorageManagerService, android.os.IBinder] */
        @Override // com.android.server.SystemService
        public void onStart() {
            ?? storageManagerService = new StorageManagerService(getContext());
            this.mStorageManagerService = storageManagerService;
            publishBinderService("mount", storageManagerService);
            this.mStorageManagerService.start();
        }

        @Override // com.android.server.SystemService
        public void onBootPhase(int i) {
            if (i == 500) {
                this.mStorageManagerService.servicesReady();
            } else if (i == 550) {
                this.mStorageManagerService.systemReady();
            } else if (i == 1000) {
                this.mStorageManagerService.bootCompleted();
            }
        }

        @Override // com.android.server.SystemService
        public void onUserSwitching(SystemService.TargetUser targetUser, SystemService.TargetUser targetUser2) {
            int userIdentifier = targetUser2.getUserIdentifier();
            this.mStorageManagerService.mCurrentUserId = userIdentifier;
            if (((UserManagerInternal) LocalServices.getService(UserManagerInternal.class)).isUserUnlocked(userIdentifier)) {
                Slog.d("StorageManagerService", "Attempt remount volumes for user: " + userIdentifier);
                this.mStorageManagerService.maybeRemountVolumes(userIdentifier);
                this.mStorageManagerService.mRemountCurrentUserVolumesOnUnlock = false;
                return;
            }
            Slog.d("StorageManagerService", "Attempt remount volumes for user: " + userIdentifier + " on unlock");
            this.mStorageManagerService.mRemountCurrentUserVolumesOnUnlock = true;
        }

        @Override // com.android.server.SystemService
        public void onUserUnlocking(SystemService.TargetUser targetUser) {
            this.mStorageManagerService.onUnlockUser(targetUser.getUserIdentifier());
        }

        @Override // com.android.server.SystemService
        public void onUserStopped(SystemService.TargetUser targetUser) {
            this.mStorageManagerService.onCleanupUser(targetUser.getUserIdentifier());
        }

        @Override // com.android.server.SystemService
        public void onUserStopping(SystemService.TargetUser targetUser) {
            this.mStorageManagerService.onStopUser(targetUser.getUserIdentifier());
        }

        @Override // com.android.server.SystemService
        public void onUserStarting(SystemService.TargetUser targetUser) {
            this.mStorageManagerService.snapshotAndMonitorLegacyStorageAppOp(targetUser.getUserHandle());
        }
    }

    /* loaded from: classes.dex */
    public static class WatchedLockedUsers {
        public int[] users = EmptyArray.INT;

        public WatchedLockedUsers() {
            invalidateIsUserUnlockedCache();
        }

        public void append(int i) {
            this.users = ArrayUtils.appendInt(this.users, i);
            invalidateIsUserUnlockedCache();
        }

        public void appendAll(int[] iArr) {
            for (int i : iArr) {
                this.users = ArrayUtils.appendInt(this.users, i);
            }
            invalidateIsUserUnlockedCache();
        }

        public void remove(int i) {
            this.users = ArrayUtils.removeInt(this.users, i);
            invalidateIsUserUnlockedCache();
        }

        public boolean contains(int i) {
            return ArrayUtils.contains(this.users, i);
        }

        public String toString() {
            return Arrays.toString(this.users);
        }

        public final void invalidateIsUserUnlockedCache() {
            UserManager.invalidateIsUserUnlockedCache();
        }
    }

    public final VolumeInfo findVolumeByIdOrThrow(String str) {
        synchronized (this.mLock) {
            VolumeInfo volumeInfo = this.mVolumes.get(str);
            if (volumeInfo != null) {
                return volumeInfo;
            }
            throw new IllegalArgumentException("No volume found for ID " + str);
        }
    }

    public final VolumeRecord findRecordForPath(String str) {
        synchronized (this.mLock) {
            for (int i = 0; i < this.mVolumes.size(); i++) {
                VolumeInfo valueAt = this.mVolumes.valueAt(i);
                String str2 = valueAt.path;
                if (str2 != null && str.startsWith(str2)) {
                    return this.mRecords.get(valueAt.fsUuid);
                }
            }
            return null;
        }
    }

    public final String scrubPath(String str) {
        if (str.startsWith(Environment.getDataDirectory().getAbsolutePath())) {
            return "internal";
        }
        VolumeRecord findRecordForPath = findRecordForPath(str);
        if (findRecordForPath == null || findRecordForPath.createdMillis == 0) {
            return "unknown";
        }
        return "ext:" + ((int) ((System.currentTimeMillis() - findRecordForPath.createdMillis) / 604800000)) + "w";
    }

    public final VolumeInfo findStorageForUuidAsUser(String str, int i) {
        StorageManager storageManager = (StorageManager) this.mContext.getSystemService(StorageManager.class);
        if (Objects.equals(StorageManager.UUID_PRIVATE_INTERNAL, str)) {
            return storageManager.findVolumeById("emulated;" + i);
        } else if ("primary_physical".equals(str)) {
            return storageManager.getPrimaryPhysicalVolume();
        } else {
            VolumeInfo findVolumeByUuid = storageManager.findVolumeByUuid(str);
            if (findVolumeByUuid == null) {
                Slog.w("StorageManagerService", "findStorageForUuidAsUser cannot find volumeUuid:" + str);
                return null;
            }
            return storageManager.findVolumeById(findVolumeByUuid.getId().replace("private", "emulated") + ";" + i);
        }
    }

    public final CountDownLatch findOrCreateDiskScanLatch(String str) {
        CountDownLatch countDownLatch;
        synchronized (this.mLock) {
            countDownLatch = this.mDiskScanLatches.get(str);
            if (countDownLatch == null) {
                countDownLatch = new CountDownLatch(1);
                this.mDiskScanLatches.put(str, countDownLatch);
            }
        }
        return countDownLatch;
    }

    /* loaded from: classes.dex */
    public class ObbState implements IBinder.DeathRecipient {
        public final String canonicalPath;
        public final int nonce;
        public final int ownerGid;
        public final String rawPath;
        public final IObbActionListener token;
        public String volId;

        public ObbState(String str, String str2, int i, IObbActionListener iObbActionListener, int i2, String str3) {
            this.rawPath = str;
            this.canonicalPath = str2;
            this.ownerGid = UserHandle.getSharedAppGid(i);
            this.token = iObbActionListener;
            this.nonce = i2;
            this.volId = str3;
        }

        public IBinder getBinder() {
            return this.token.asBinder();
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            StorageManagerService.this.mObbActionHandler.sendMessage(StorageManagerService.this.mObbActionHandler.obtainMessage(1, new UnmountObbAction(this, true)));
        }

        public void link() throws RemoteException {
            getBinder().linkToDeath(this, 0);
        }

        public void unlink() {
            getBinder().unlinkToDeath(this, 0);
        }

        public String toString() {
            return "ObbState{rawPath=" + this.rawPath + ",canonicalPath=" + this.canonicalPath + ",ownerGid=" + this.ownerGid + ",token=" + this.token + ",binder=" + getBinder() + ",volId=" + this.volId + '}';
        }
    }

    /* loaded from: classes.dex */
    public class StorageManagerServiceHandler extends Handler {
        public StorageManagerServiceHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            boolean z;
            switch (message.what) {
                case 1:
                    StorageManagerService.this.handleSystemReady();
                    return;
                case 2:
                    StorageManagerService.this.handleDaemonConnected();
                    return;
                case 3:
                    IStorageShutdownObserver iStorageShutdownObserver = (IStorageShutdownObserver) message.obj;
                    try {
                        StorageManagerService.this.mVold.shutdown();
                        z = true;
                    } catch (Exception e) {
                        Slog.wtf("StorageManagerService", e);
                        z = false;
                    }
                    if (iStorageShutdownObserver == null) {
                        return;
                    }
                    try {
                        iStorageShutdownObserver.onShutDownComplete(z ? 0 : -1);
                    } catch (Exception unused) {
                        return;
                    }
                case 4:
                    Slog.i("StorageManagerService", "Running fstrim idle maintenance");
                    try {
                        StorageManagerService.this.mLastMaintenance = System.currentTimeMillis();
                        StorageManagerService.this.mLastMaintenanceFile.setLastModified(StorageManagerService.this.mLastMaintenance);
                    } catch (Exception unused2) {
                        Slog.e("StorageManagerService", "Unable to record last fstrim!");
                    }
                    StorageManagerService.this.fstrim(0, null);
                    Runnable runnable = (Runnable) message.obj;
                    if (runnable != null) {
                        runnable.run();
                        return;
                    }
                    return;
                case 5:
                    VolumeInfo volumeInfo = (VolumeInfo) message.obj;
                    if (StorageManagerService.this.isMountDisallowed(volumeInfo)) {
                        Slog.i("StorageManagerService", "Ignoring mount " + volumeInfo.getId() + " due to policy");
                        return;
                    }
                    StorageManagerService.this.mount(volumeInfo);
                    return;
                case 6:
                    StorageVolume storageVolume = (StorageVolume) message.obj;
                    String state = storageVolume.getState();
                    Slog.d("StorageManagerService", "Volume " + storageVolume.getId() + " broadcasting " + state + " to " + storageVolume.getOwner());
                    String broadcastForEnvironment = VolumeInfo.getBroadcastForEnvironment(state);
                    if (broadcastForEnvironment != null) {
                        Intent intent = new Intent(broadcastForEnvironment, Uri.fromFile(storageVolume.getPathFile()));
                        intent.putExtra("android.os.storage.extra.STORAGE_VOLUME", storageVolume);
                        intent.addFlags(83886080);
                        StorageManagerService.this.mContext.sendBroadcastAsUser(intent, storageVolume.getOwner());
                        return;
                    }
                    return;
                case 7:
                    StorageManagerService.this.mContext.sendBroadcastAsUser((Intent) message.obj, UserHandle.ALL, "android.permission.WRITE_MEDIA_STORAGE");
                    return;
                case 8:
                    StorageManagerService.this.unmount((VolumeInfo) message.obj);
                    return;
                case 9:
                    VolumeRecord volumeRecord = (VolumeRecord) message.obj;
                    StorageManagerService.this.forgetPartition(volumeRecord.partGuid, volumeRecord.fsUuid);
                    return;
                case 10:
                    StorageManagerService.this.resetIfBootedAndConnected();
                    return;
                case 11:
                    Slog.i("StorageManagerService", "Running idle maintenance");
                    StorageManagerService.this.runIdleMaint((Runnable) message.obj);
                    return;
                case 12:
                    Slog.i("StorageManagerService", "Aborting idle maintenance");
                    StorageManagerService.this.abortIdleMaint((Runnable) message.obj);
                    return;
                case 13:
                    StorageManagerService.this.handleBootCompleted();
                    return;
                case 14:
                    StorageManagerService.this.completeUnlockUser(message.arg1);
                    return;
                case 15:
                    SomeArgs someArgs = (SomeArgs) message.obj;
                    StorageManagerService.this.onVolumeStateChangedAsync((VolumeInfo) someArgs.arg1, someArgs.argi1, someArgs.argi2);
                    someArgs.recycle();
                    return;
                case 16:
                    Object obj = message.obj;
                    if (obj instanceof StorageManagerInternal.CloudProviderChangeListener) {
                        StorageManagerService.this.notifyCloudMediaProviderChangedAsync((StorageManagerInternal.CloudProviderChangeListener) obj);
                        return;
                    } else {
                        StorageManagerService.this.onCloudMediaProviderChangedAsync(message.arg1, (String) obj);
                        return;
                    }
                case 17:
                    try {
                        StorageManagerService.this.mVold.onSecureKeyguardStateChanged(((Boolean) message.obj).booleanValue());
                        return;
                    } catch (Exception e2) {
                        Slog.wtf("StorageManagerService", e2);
                        return;
                    }
                default:
                    return;
            }
        }
    }

    public final void waitForLatch(CountDownLatch countDownLatch, String str, long j) throws TimeoutException {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        while (!countDownLatch.await(5000L, TimeUnit.MILLISECONDS)) {
            try {
                Slog.w("StorageManagerService", "Thread " + Thread.currentThread().getName() + " still waiting for " + str + "...");
            } catch (InterruptedException unused) {
                Slog.w("StorageManagerService", "Interrupt while waiting for " + str);
            }
            if (j > 0 && SystemClock.elapsedRealtime() > elapsedRealtime + j) {
                throw new TimeoutException("Thread " + Thread.currentThread().getName() + " gave up waiting for " + str + " after " + j + "ms");
            }
        }
    }

    public final void handleSystemReady() {
        if (prepareSmartIdleMaint()) {
            SmartStorageMaintIdler.scheduleSmartIdlePass(this.mContext, sSmartIdleMaintPeriod);
        }
        MountServiceIdler.scheduleIdlePass(this.mContext);
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("zram_enabled"), false, new ContentObserver(null) { // from class: com.android.server.StorageManagerService.2
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                StorageManagerService.this.refreshZramSettings();
            }
        });
        refreshZramSettings();
        if (!SystemProperties.get("persist.sys.zram_enabled").equals("0") && this.mContext.getResources().getBoolean(17891896)) {
            ZramWriteback.scheduleZramWriteback(this.mContext);
        }
        configureTranscoding();
    }

    public final void refreshZramSettings() {
        String str = SystemProperties.get("persist.sys.zram_enabled");
        if ("".equals(str)) {
            return;
        }
        String str2 = Settings.Global.getInt(this.mContext.getContentResolver(), "zram_enabled", 1) != 0 ? "1" : "0";
        if (str2.equals(str)) {
            return;
        }
        SystemProperties.set("persist.sys.zram_enabled", str2);
        if (str2.equals("1") && this.mContext.getResources().getBoolean(17891896)) {
            ZramWriteback.scheduleZramWriteback(this.mContext);
        }
    }

    public final void configureTranscoding() {
        boolean z;
        if (SystemProperties.getBoolean("persist.sys.fuse.transcode_user_control", false)) {
            z = SystemProperties.getBoolean("persist.sys.fuse.transcode_enabled", true);
        } else {
            z = DeviceConfig.getBoolean("storage_native_boot", "transcode_enabled", true);
        }
        SystemProperties.set("sys.fuse.transcode_enabled", String.valueOf(z));
        if (z) {
            ((ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class)).registerAnrController(new ExternalStorageServiceAnrController());
        }
    }

    /* loaded from: classes.dex */
    public class ExternalStorageServiceAnrController implements AnrController {
        public ExternalStorageServiceAnrController() {
        }

        public long getAnrDelayMillis(String str, int i) {
            if (StorageManagerService.this.isAppIoBlocked(i)) {
                int i2 = DeviceConfig.getInt("storage_native_boot", "anr_delay_millis", 5000);
                Slog.v("StorageManagerService", "getAnrDelayMillis for " + str + ". " + i2 + "ms");
                return i2;
            }
            return 0L;
        }

        public void onAnrDelayStarted(String str, int i) {
            if (StorageManagerService.this.isAppIoBlocked(i) && DeviceConfig.getBoolean("storage_native_boot", "anr_delay_notify_external_storage_service", true)) {
                Slog.d("StorageManagerService", "onAnrDelayStarted for " + str + ". Notifying external storage service");
                try {
                    StorageManagerService.this.mStorageSessionController.notifyAnrDelayStarted(str, i, 0, 1);
                } catch (StorageSessionController.ExternalStorageServiceException e) {
                    Slog.e("StorageManagerService", "Failed to notify ANR delay started for " + str, e);
                }
            }
        }

        public boolean onAnrDelayCompleted(String str, int i) {
            if (StorageManagerService.this.isAppIoBlocked(i)) {
                Slog.d("StorageManagerService", "onAnrDelayCompleted for " + str + ". Showing ANR dialog...");
                return true;
            }
            Slog.d("StorageManagerService", "onAnrDelayCompleted for " + str + ". Skipping ANR dialog...");
            return false;
        }
    }

    @GuardedBy({"mLock"})
    public final void addInternalVolumeLocked() {
        VolumeInfo volumeInfo = new VolumeInfo("private", 1, (DiskInfo) null, (String) null);
        volumeInfo.state = 2;
        volumeInfo.path = Environment.getDataDirectory().getAbsolutePath();
        this.mVolumes.put(volumeInfo.id, volumeInfo);
    }

    public final void resetIfBootedAndConnected() {
        int[] copyOf;
        Slog.d("StorageManagerService", "Thinking about reset, mBootCompleted=" + this.mBootCompleted + ", mDaemonConnected=" + this.mDaemonConnected);
        if (this.mBootCompleted && this.mDaemonConnected) {
            UserManager userManager = (UserManager) this.mContext.getSystemService(UserManager.class);
            List<UserInfo> users = userManager.getUsers();
            this.mStorageSessionController.onReset(this.mVold, new Runnable() { // from class: com.android.server.StorageManagerService$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    StorageManagerService.this.lambda$resetIfBootedAndConnected$0();
                }
            });
            synchronized (this.mLock) {
                int[] iArr = this.mSystemUnlockedUsers;
                copyOf = Arrays.copyOf(iArr, iArr.length);
                this.mDisks.clear();
                this.mVolumes.clear();
                addInternalVolumeLocked();
            }
            try {
                Slog.i("StorageManagerService", "Resetting vold...");
                this.mVold.reset();
                Slog.i("StorageManagerService", "Reset vold");
                for (UserInfo userInfo : users) {
                    if (userInfo.isCloneProfile()) {
                        this.mVold.onUserAdded(userInfo.id, userInfo.serialNumber, userInfo.profileGroupId);
                    } else {
                        this.mVold.onUserAdded(userInfo.id, userInfo.serialNumber, -1);
                    }
                }
                for (int i : copyOf) {
                    this.mVold.onUserStarted(i);
                    this.mStoraged.onUserStarted(i);
                }
                restoreSystemUnlockedUsers(userManager, users, copyOf);
                this.mVold.onSecureKeyguardStateChanged(this.mSecureKeyguardShowing);
                this.mStorageManagerInternal.onReset(this.mVold);
            } catch (Exception e) {
                Slog.wtf("StorageManagerService", e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$resetIfBootedAndConnected$0() {
        this.mHandler.removeCallbacksAndMessages(null);
    }

    public final void restoreSystemUnlockedUsers(UserManager userManager, List<UserInfo> list, int[] iArr) throws Exception {
        Arrays.sort(iArr);
        UserManager.invalidateIsUserUnlockedCache();
        for (UserInfo userInfo : list) {
            int i = userInfo.id;
            if (userManager.isUserRunning(i) && Arrays.binarySearch(iArr, i) < 0 && userManager.isUserUnlockingOrUnlocked(i)) {
                Slog.w("StorageManagerService", "UNLOCK_USER lost from vold reset, will retry, user:" + i);
                this.mVold.onUserStarted(i);
                this.mStoraged.onUserStarted(i);
                this.mHandler.obtainMessage(14, i, 0).sendToTarget();
            }
        }
    }

    public final void restoreLocalUnlockedUsers() {
        try {
            int[] unlockedUsers = this.mVold.getUnlockedUsers();
            if (ArrayUtils.isEmpty(unlockedUsers)) {
                return;
            }
            Slog.d("StorageManagerService", "CE storage for users " + Arrays.toString(unlockedUsers) + " is already unlocked");
            synchronized (this.mLock) {
                this.mLocalUnlockedUsers.appendAll(unlockedUsers);
            }
        } catch (Exception e) {
            Slog.e("StorageManagerService", "Failed to get unlocked users from vold", e);
        }
    }

    public final void onUnlockUser(int i) {
        Slog.d("StorageManagerService", "onUnlockUser " + i);
        if (i != 0) {
            try {
                UserManager userManager = (UserManager) this.mContext.createPackageContextAsUser("system", 0, UserHandle.of(i)).getSystemService(UserManager.class);
                if (userManager != null && userManager.isMediaSharedWithParent()) {
                    int i2 = userManager.getProfileParent(i).id;
                    this.mUserSharesMediaWith.put(i, i2);
                    this.mUserSharesMediaWith.put(i2, i);
                }
            } catch (PackageManager.NameNotFoundException unused) {
                Log.e("StorageManagerService", "Failed to create user context for user " + i);
            }
        }
        try {
            this.mStorageSessionController.onUnlockUser(i);
            this.mVold.onUserStarted(i);
            this.mStoraged.onUserStarted(i);
        } catch (Exception e) {
            Slog.wtf("StorageManagerService", e);
        }
        this.mHandler.obtainMessage(14, i, 0).sendToTarget();
        if (this.mRemountCurrentUserVolumesOnUnlock && i == this.mCurrentUserId) {
            maybeRemountVolumes(i);
            this.mRemountCurrentUserVolumesOnUnlock = false;
        }
    }

    public final void completeUnlockUser(int i) {
        onKeyguardStateChanged(false);
        synchronized (this.mLock) {
            for (int i2 : this.mSystemUnlockedUsers) {
                if (i2 == i) {
                    Log.i("StorageManagerService", "completeUnlockUser called for already unlocked user:" + i);
                    return;
                }
            }
            for (int i3 = 0; i3 < this.mVolumes.size(); i3++) {
                VolumeInfo valueAt = this.mVolumes.valueAt(i3);
                if (valueAt.isVisibleForUser(i) && valueAt.isMountedReadable()) {
                    StorageVolume buildStorageVolume = valueAt.buildStorageVolume(this.mContext, i, false);
                    this.mHandler.obtainMessage(6, buildStorageVolume).sendToTarget();
                    String environmentForState = VolumeInfo.getEnvironmentForState(valueAt.getState());
                    this.mCallbacks.notifyStorageStateChanged(buildStorageVolume.getPath(), environmentForState, environmentForState);
                }
            }
            this.mSystemUnlockedUsers = ArrayUtils.appendInt(this.mSystemUnlockedUsers, i);
        }
    }

    public final void onCleanupUser(int i) {
        Slog.d("StorageManagerService", "onCleanupUser " + i);
        try {
            this.mVold.onUserStopped(i);
            this.mStoraged.onUserStopped(i);
        } catch (Exception e) {
            Slog.wtf("StorageManagerService", e);
        }
        synchronized (this.mLock) {
            this.mSystemUnlockedUsers = ArrayUtils.removeInt(this.mSystemUnlockedUsers, i);
        }
    }

    public final void onStopUser(int i) {
        Slog.i("StorageManagerService", "onStopUser " + i);
        try {
            this.mStorageSessionController.onUserStopping(i);
        } catch (Exception e) {
            Slog.wtf("StorageManagerService", e);
        }
        PackageMonitor packageMonitor = (PackageMonitor) this.mPackageMonitorsForUser.removeReturnOld(i);
        if (packageMonitor != null) {
            packageMonitor.unregister();
        }
    }

    public final void maybeRemountVolumes(int i) {
        ArrayList<VolumeInfo> arrayList = new ArrayList();
        synchronized (this.mLock) {
            for (int i2 = 0; i2 < this.mVolumes.size(); i2++) {
                VolumeInfo valueAt = this.mVolumes.valueAt(i2);
                if (!valueAt.isPrimary() && valueAt.isMountedWritable() && valueAt.isVisible() && valueAt.getMountUserId() != this.mCurrentUserId) {
                    valueAt.mountUserId = this.mCurrentUserId;
                    arrayList.add(valueAt);
                }
            }
        }
        for (VolumeInfo volumeInfo : arrayList) {
            Slog.i("StorageManagerService", "Remounting volume for user: " + i + ". Volume: " + volumeInfo);
            this.mHandler.obtainMessage(8, volumeInfo).sendToTarget();
            this.mHandler.obtainMessage(5, volumeInfo).sendToTarget();
        }
    }

    public final boolean supportsBlockCheckpoint() throws RemoteException {
        enforcePermission("android.permission.MOUNT_FORMAT_FILESYSTEMS");
        return this.mVold.supportsBlockCheckpoint();
    }

    @Override // com.android.server.p014wm.ActivityTaskManagerInternal.ScreenObserver
    public void onKeyguardStateChanged(boolean z) {
        boolean z2 = z && ((KeyguardManager) this.mContext.getSystemService(KeyguardManager.class)).isDeviceSecure(this.mCurrentUserId);
        if (this.mSecureKeyguardShowing != z2) {
            this.mSecureKeyguardShowing = z2;
            this.mHandler.obtainMessage(17, Boolean.valueOf(this.mSecureKeyguardShowing)).sendToTarget();
        }
    }

    public void runIdleMaintenance(Runnable runnable) {
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(4, runnable));
    }

    @EnforcePermission("android.permission.MOUNT_UNMOUNT_FILESYSTEMS")
    public void runMaintenance() {
        super.runMaintenance_enforcePermission();
        runIdleMaintenance(null);
    }

    public long lastMaintenance() {
        return this.mLastMaintenance;
    }

    public void onDaemonConnected() {
        this.mDaemonConnected = true;
        this.mHandler.obtainMessage(2).sendToTarget();
    }

    public final void handleDaemonConnected() {
        resetIfBootedAndConnected();
    }

    @GuardedBy({"mLock"})
    public final void onDiskScannedLocked(DiskInfo diskInfo) {
        int i = 0;
        for (int i2 = 0; i2 < this.mVolumes.size(); i2++) {
            if (Objects.equals(diskInfo.id, this.mVolumes.valueAt(i2).getDiskId())) {
                i++;
            }
        }
        Intent intent = new Intent("android.os.storage.action.DISK_SCANNED");
        intent.addFlags(83886080);
        intent.putExtra("android.os.storage.extra.DISK_ID", diskInfo.id);
        intent.putExtra("android.os.storage.extra.VOLUME_COUNT", i);
        this.mHandler.obtainMessage(7, intent).sendToTarget();
        CountDownLatch remove = this.mDiskScanLatches.remove(diskInfo.id);
        if (remove != null) {
            remove.countDown();
        }
        diskInfo.volumeCount = i;
        this.mCallbacks.notifyDiskScanned(diskInfo, i);
    }

    @GuardedBy({"mLock"})
    public final void onVolumeCreatedLocked(VolumeInfo volumeInfo) {
        ActivityManagerInternal activityManagerInternal = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
        int i = volumeInfo.mountUserId;
        if (i >= 0 && !activityManagerInternal.isUserRunning(i, 0)) {
            Slog.d("StorageManagerService", "Ignoring volume " + volumeInfo.getId() + " because user " + Integer.toString(volumeInfo.mountUserId) + " is no longer running.");
            return;
        }
        int i2 = volumeInfo.type;
        if (i2 == 2) {
            Context createContextAsUser = this.mContext.createContextAsUser(UserHandle.of(volumeInfo.mountUserId), 0);
            if (!(createContextAsUser != null ? ((UserManager) createContextAsUser.getSystemService(UserManager.class)).isMediaSharedWithParent() : false) && !this.mStorageSessionController.supportsExternalStorage(volumeInfo.mountUserId)) {
                Slog.d("StorageManagerService", "Ignoring volume " + volumeInfo.getId() + " because user " + Integer.toString(volumeInfo.mountUserId) + " does not support external storage.");
                return;
            }
            VolumeInfo findPrivateForEmulated = ((StorageManager) this.mContext.getSystemService(StorageManager.class)).findPrivateForEmulated(volumeInfo);
            if ((Objects.equals(StorageManager.UUID_PRIVATE_INTERNAL, this.mPrimaryStorageUuid) && "private".equals(findPrivateForEmulated.id)) || Objects.equals(findPrivateForEmulated.fsUuid, this.mPrimaryStorageUuid)) {
                Slog.v("StorageManagerService", "Found primary storage at " + volumeInfo);
                volumeInfo.mountFlags = volumeInfo.mountFlags | 1 | 4;
                this.mHandler.obtainMessage(5, volumeInfo).sendToTarget();
            }
        } else if (i2 == 0) {
            if ("primary_physical".equals(this.mPrimaryStorageUuid) && volumeInfo.disk.isDefaultPrimary()) {
                Slog.v("StorageManagerService", "Found primary storage at " + volumeInfo);
                volumeInfo.mountFlags = volumeInfo.mountFlags | 1 | 4;
            }
            if (volumeInfo.disk.isAdoptable()) {
                volumeInfo.mountFlags |= 4;
            }
            volumeInfo.mountUserId = this.mCurrentUserId;
            this.mHandler.obtainMessage(5, volumeInfo).sendToTarget();
        } else if (i2 == 1) {
            this.mHandler.obtainMessage(5, volumeInfo).sendToTarget();
        } else if (i2 == 5) {
            if (volumeInfo.disk.isStubVisible()) {
                volumeInfo.mountFlags |= 4;
            } else {
                volumeInfo.mountFlags |= 2;
            }
            volumeInfo.mountUserId = this.mCurrentUserId;
            this.mHandler.obtainMessage(5, volumeInfo).sendToTarget();
        } else {
            Slog.d("StorageManagerService", "Skipping automatic mounting of " + volumeInfo);
        }
    }

    public final boolean isBroadcastWorthy(VolumeInfo volumeInfo) {
        int type = volumeInfo.getType();
        if (type == 0 || type == 1 || type == 2 || type == 5) {
            int state = volumeInfo.getState();
            return state == 0 || state == 8 || state == 2 || state == 3 || state == 5 || state == 6;
        }
        return false;
    }

    @GuardedBy({"mLock"})
    public final void onVolumeStateChangedLocked(final VolumeInfo volumeInfo, int i) {
        if (volumeInfo.type == 2) {
            if (i != 2) {
                this.mFuseMountedUser.remove(Integer.valueOf(volumeInfo.getMountUserId()));
            } else if (this.mVoldAppDataIsolationEnabled) {
                final int mountUserId = volumeInfo.getMountUserId();
                new Thread(new Runnable() { // from class: com.android.server.StorageManagerService$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        StorageManagerService.this.lambda$onVolumeStateChangedLocked$1(mountUserId, volumeInfo);
                    }
                }).start();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onVolumeStateChangedLocked$1(int i, VolumeInfo volumeInfo) {
        Map<Integer, String> map;
        if (i == 0 && Build.VERSION.DEVICE_INITIAL_SDK_INT < 29) {
            this.mPmInternal.migrateLegacyObbData();
        }
        synchronized (this.mLock) {
            this.mFuseMountedUser.add(Integer.valueOf(i));
        }
        int i2 = 0;
        while (true) {
            if (i2 >= 5) {
                map = null;
                break;
            }
            try {
                map = ((ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class)).getProcessesWithPendingBindMounts(volumeInfo.getMountUserId());
                break;
            } catch (IllegalStateException unused) {
                Slog.i("StorageManagerService", "Some processes are starting, retry");
                SystemClock.sleep(100L);
                i2++;
            }
        }
        if (map != null) {
            remountAppStorageDirs(map, i);
        } else {
            Slog.wtf("StorageManagerService", "Not able to getStorageNotOptimizedProcesses() after 5 retries");
        }
    }

    public final void onVolumeStateChangedAsync(VolumeInfo volumeInfo, int i, int i2) {
        int[] iArr;
        if (i2 == 2) {
            try {
                try {
                    prepareUserStorageIfNeeded(volumeInfo);
                } catch (Exception e) {
                    Slog.wtf("StorageManagerService", e);
                    return;
                }
            } catch (Exception unused) {
                this.mVold.unmount(volumeInfo.id);
                return;
            }
        }
        synchronized (this.mLock) {
            if (!TextUtils.isEmpty(volumeInfo.fsUuid)) {
                VolumeRecord volumeRecord = this.mRecords.get(volumeInfo.fsUuid);
                if (volumeRecord == null) {
                    volumeRecord = new VolumeRecord(volumeInfo.type, volumeInfo.fsUuid);
                    volumeRecord.partGuid = volumeInfo.partGuid;
                    volumeRecord.createdMillis = System.currentTimeMillis();
                    if (volumeInfo.type == 1) {
                        volumeRecord.nickname = volumeInfo.disk.getDescription();
                    }
                    this.mRecords.put(volumeRecord.fsUuid, volumeRecord);
                } else if (TextUtils.isEmpty(volumeRecord.partGuid)) {
                    volumeRecord.partGuid = volumeInfo.partGuid;
                }
                volumeRecord.lastSeenMillis = System.currentTimeMillis();
                writeSettingsLocked();
            }
        }
        try {
            this.mStorageSessionController.notifyVolumeStateChanged(volumeInfo);
        } catch (StorageSessionController.ExternalStorageServiceException e2) {
            Log.e("StorageManagerService", "Failed to notify volume state changed to the Storage Service", e2);
        }
        synchronized (this.mLock) {
            this.mCallbacks.notifyVolumeStateChanged(volumeInfo, i, i2);
            if (this.mBootCompleted && isBroadcastWorthy(volumeInfo)) {
                Intent intent = new Intent("android.os.storage.action.VOLUME_STATE_CHANGED");
                intent.putExtra("android.os.storage.extra.VOLUME_ID", volumeInfo.id);
                intent.putExtra("android.os.storage.extra.VOLUME_STATE", i2);
                intent.putExtra("android.os.storage.extra.FS_UUID", volumeInfo.fsUuid);
                intent.addFlags(83886080);
                this.mHandler.obtainMessage(7, intent).sendToTarget();
            }
            String environmentForState = VolumeInfo.getEnvironmentForState(i);
            String environmentForState2 = VolumeInfo.getEnvironmentForState(i2);
            if (!Objects.equals(environmentForState, environmentForState2)) {
                for (int i3 : this.mSystemUnlockedUsers) {
                    if (volumeInfo.isVisibleForUser(i3)) {
                        StorageVolume buildStorageVolume = volumeInfo.buildStorageVolume(this.mContext, i3, false);
                        this.mHandler.obtainMessage(6, buildStorageVolume).sendToTarget();
                        this.mCallbacks.notifyStorageStateChanged(buildStorageVolume.getPath(), environmentForState, environmentForState2);
                    }
                }
            }
            int i4 = volumeInfo.type;
            if ((i4 == 0 || i4 == 5) && volumeInfo.state == 5) {
                ObbActionHandler obbActionHandler = this.mObbActionHandler;
                obbActionHandler.sendMessage(obbActionHandler.obtainMessage(2, volumeInfo.path));
            }
            maybeLogMediaMount(volumeInfo, i2);
        }
    }

    public final void notifyCloudMediaProviderChangedAsync(StorageManagerInternal.CloudProviderChangeListener cloudProviderChangeListener) {
        synchronized (this.mCloudMediaProviders) {
            for (int size = this.mCloudMediaProviders.size() - 1; size >= 0; size--) {
                cloudProviderChangeListener.onCloudProviderChanged(this.mCloudMediaProviders.keyAt(size), this.mCloudMediaProviders.valueAt(size));
            }
        }
    }

    public final void onCloudMediaProviderChangedAsync(int i, String str) {
        Iterator it = this.mStorageManagerInternal.mCloudProviderChangeListeners.iterator();
        while (it.hasNext()) {
            ((StorageManagerInternal.CloudProviderChangeListener) it.next()).onCloudProviderChanged(i, str);
        }
    }

    public final void maybeLogMediaMount(VolumeInfo volumeInfo, int i) {
        DiskInfo disk;
        if (!SecurityLog.isLoggingEnabled() || (disk = volumeInfo.getDisk()) == null || (disk.flags & 12) == 0) {
            return;
        }
        String str = disk.label;
        String trim = str != null ? str.trim() : "";
        if (i == 2 || i == 3) {
            SecurityLog.writeEvent(210013, new Object[]{volumeInfo.path, trim});
        } else if (i == 0 || i == 8) {
            SecurityLog.writeEvent(210014, new Object[]{volumeInfo.path, trim});
        }
    }

    @GuardedBy({"mLock"})
    public final void onMoveStatusLocked(int i) {
        IPackageMoveObserver iPackageMoveObserver = this.mMoveCallback;
        if (iPackageMoveObserver == null) {
            Slog.w("StorageManagerService", "Odd, status but no move requested");
            return;
        }
        try {
            iPackageMoveObserver.onStatusChanged(-1, i, -1L);
        } catch (RemoteException unused) {
        }
        if (i == 82) {
            Slog.d("StorageManagerService", "Move to " + this.mMoveTargetUuid + " copy phase finshed; persisting");
            this.mPrimaryStorageUuid = this.mMoveTargetUuid;
            writeSettingsLocked();
        }
        if (PackageManager.isMoveStatusFinished(i)) {
            Slog.d("StorageManagerService", "Move to " + this.mMoveTargetUuid + " finished with status " + i);
            this.mMoveCallback = null;
            this.mMoveTargetUuid = null;
        }
    }

    public final void enforcePermission(String str) {
        this.mContext.enforceCallingOrSelfPermission(str, str);
    }

    public final boolean isMountDisallowed(VolumeInfo volumeInfo) {
        UserManager userManager = (UserManager) this.mContext.getSystemService(UserManager.class);
        DiskInfo diskInfo = volumeInfo.disk;
        boolean hasUserRestriction = (diskInfo == null || !diskInfo.isUsb()) ? false : userManager.hasUserRestriction("no_usb_file_transfer", Binder.getCallingUserHandle());
        int i = volumeInfo.type;
        return hasUserRestriction || ((i == 0 || i == 1 || i == 5) ? userManager.hasUserRestriction("no_physical_media", Binder.getCallingUserHandle()) : false);
    }

    public final void enforceAdminUser() {
        UserManager userManager = (UserManager) this.mContext.getSystemService("user");
        int callingUserId = UserHandle.getCallingUserId();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            if (!userManager.getUserInfo(callingUserId).isAdmin()) {
                throw new SecurityException("Only admin users can adopt sd cards");
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public StorageManagerService(Context context) {
        sSelf = this;
        this.mVoldAppDataIsolationEnabled = SystemProperties.getBoolean("persist.sys.vold_app_data_isolation_enabled", false);
        this.mContext = context;
        this.mCallbacks = new Callbacks(FgThread.get().getLooper());
        HandlerThread handlerThread = new HandlerThread("StorageManagerService");
        handlerThread.start();
        this.mHandler = new StorageManagerServiceHandler(handlerThread.getLooper());
        this.mObbActionHandler = new ObbActionHandler(IoThread.get().getLooper());
        this.mStorageSessionController = new StorageSessionController(context);
        Installer installer = new Installer(context);
        this.mInstaller = installer;
        installer.onStart();
        File file = new File(new File(Environment.getDataDirectory(), "system"), "last-fstrim");
        this.mLastMaintenanceFile = file;
        if (!file.exists()) {
            try {
                new FileOutputStream(file).close();
            } catch (IOException unused) {
                Slog.e("StorageManagerService", "Unable to create fstrim record " + this.mLastMaintenanceFile.getPath());
            }
        } else {
            this.mLastMaintenance = file.lastModified();
        }
        this.mSettingsFile = new AtomicFile(new File(Environment.getDataSystemDirectory(), "storage.xml"), "storage-settings");
        this.mWriteRecordFile = new AtomicFile(new File(Environment.getDataSystemDirectory(), "storage-write-records"));
        sSmartIdleMaintPeriod = DeviceConfig.getInt("storage_native_boot", "smart_idle_maint_period", 60);
        if (sSmartIdleMaintPeriod < 10) {
            sSmartIdleMaintPeriod = 10;
        } else if (sSmartIdleMaintPeriod > 1440) {
            sSmartIdleMaintPeriod = 1440;
        }
        this.mMaxWriteRecords = 4320 / sSmartIdleMaintPeriod;
        this.mStorageWriteRecords = new int[this.mMaxWriteRecords];
        synchronized (this.mLock) {
            readSettingsLocked();
        }
        LocalServices.addService(StorageManagerInternal.class, this.mStorageManagerInternal);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.USER_ADDED");
        intentFilter.addAction("android.intent.action.USER_REMOVED");
        this.mContext.registerReceiver(this.mUserReceiver, intentFilter, null, this.mHandler);
        synchronized (this.mLock) {
            addInternalVolumeLocked();
        }
        Watchdog.getInstance().addMonitor(this);
    }

    public final void start() {
        lambda$connectStoraged$2();
        lambda$connectVold$3();
    }

    /* renamed from: connectStoraged */
    public final void lambda$connectStoraged$2() {
        IBinder service = ServiceManager.getService("storaged");
        if (service != null) {
            try {
                service.linkToDeath(new IBinder.DeathRecipient() { // from class: com.android.server.StorageManagerService.4
                    @Override // android.os.IBinder.DeathRecipient
                    public void binderDied() {
                        Slog.w("StorageManagerService", "storaged died; reconnecting");
                        StorageManagerService.this.mStoraged = null;
                        StorageManagerService.this.lambda$connectStoraged$2();
                    }
                }, 0);
            } catch (RemoteException unused) {
                service = null;
            }
        }
        if (service != null) {
            this.mStoraged = IStoraged.Stub.asInterface(service);
        } else {
            Slog.w("StorageManagerService", "storaged not found; trying again");
        }
        if (this.mStoraged == null) {
            BackgroundThread.getHandler().postDelayed(new Runnable() { // from class: com.android.server.StorageManagerService$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    StorageManagerService.this.lambda$connectStoraged$2();
                }
            }, 1000L);
        } else {
            onDaemonConnected();
        }
    }

    /* renamed from: connectVold */
    public final void lambda$connectVold$3() {
        IBinder service = ServiceManager.getService("vold");
        if (service != null) {
            try {
                service.linkToDeath(new IBinder.DeathRecipient() { // from class: com.android.server.StorageManagerService.5
                    @Override // android.os.IBinder.DeathRecipient
                    public void binderDied() {
                        Slog.w("StorageManagerService", "vold died; reconnecting");
                        StorageManagerService.this.mVold = null;
                        StorageManagerService.this.lambda$connectVold$3();
                    }
                }, 0);
            } catch (RemoteException unused) {
                service = null;
            }
        }
        if (service != null) {
            this.mVold = IVold.Stub.asInterface(service);
            try {
                this.mVold.setListener(this.mListener);
            } catch (RemoteException e) {
                this.mVold = null;
                Slog.w("StorageManagerService", "vold listener rejected; trying again", e);
            }
        } else {
            Slog.w("StorageManagerService", "vold not found; trying again");
        }
        if (this.mVold == null) {
            BackgroundThread.getHandler().postDelayed(new Runnable() { // from class: com.android.server.StorageManagerService$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    StorageManagerService.this.lambda$connectVold$3();
                }
            }, 1000L);
            return;
        }
        restoreLocalUnlockedUsers();
        onDaemonConnected();
    }

    public final void servicesReady() {
        this.mPmInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        this.mIPackageManager = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
        this.mIAppOpsService = IAppOpsService.Stub.asInterface(ServiceManager.getService("appops"));
        ProviderInfo providerInfo = getProviderInfo("media");
        if (providerInfo != null) {
            this.mMediaStoreAuthorityAppId = UserHandle.getAppId(providerInfo.applicationInfo.uid);
            sMediaStoreAuthorityProcessName = providerInfo.applicationInfo.processName;
        }
        ProviderInfo providerInfo2 = getProviderInfo("downloads");
        if (providerInfo2 != null) {
            this.mDownloadsAuthorityAppId = UserHandle.getAppId(providerInfo2.applicationInfo.uid);
        }
        ProviderInfo providerInfo3 = getProviderInfo("com.android.externalstorage.documents");
        if (providerInfo3 != null) {
            this.mExternalStorageAuthorityAppId = UserHandle.getAppId(providerInfo3.applicationInfo.uid);
        }
    }

    public final ProviderInfo getProviderInfo(String str) {
        return this.mPmInternal.resolveContentProvider(str, 786432L, UserHandle.getUserId(0), 1000);
    }

    public final void updateLegacyStorageApps(String str, int i, boolean z) {
        synchronized (this.mLock) {
            if (z) {
                Slog.v("StorageManagerService", "Package " + str + " has legacy storage");
                this.mUidsWithLegacyExternalStorage.add(Integer.valueOf(i));
            } else {
                Slog.v("StorageManagerService", "Package " + str + " does not have legacy storage");
                this.mUidsWithLegacyExternalStorage.remove(Integer.valueOf(i));
            }
        }
    }

    public final void snapshotAndMonitorLegacyStorageAppOp(UserHandle userHandle) {
        int identifier = userHandle.getIdentifier();
        Iterator<ApplicationInfo> it = this.mPmInternal.getInstalledApplications(4988928L, identifier, Process.myUid()).iterator();
        while (true) {
            boolean z = true;
            if (!it.hasNext()) {
                break;
            }
            ApplicationInfo next = it.next();
            try {
                if (this.mIAppOpsService.checkOperation(87, next.uid, next.packageName) != 0) {
                    z = false;
                }
                updateLegacyStorageApps(next.packageName, next.uid, z);
            } catch (RemoteException e) {
                Slog.e("StorageManagerService", "Failed to check legacy op for package " + next.packageName, e);
            }
        }
        if (this.mPackageMonitorsForUser.get(identifier) == null) {
            PackageMonitor packageMonitor = new PackageMonitor() { // from class: com.android.server.StorageManagerService.6
                public void onPackageRemoved(String str, int i) {
                    StorageManagerService.this.updateLegacyStorageApps(str, i, false);
                }
            };
            packageMonitor.register(this.mContext, userHandle, true, this.mHandler);
            this.mPackageMonitorsForUser.put(identifier, packageMonitor);
            return;
        }
        Slog.w("StorageManagerService", "PackageMonitor is already registered for: " + identifier);
    }

    public final void systemReady() {
        ((ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class)).registerScreenObserver(this);
        this.mHandler.obtainMessage(1).sendToTarget();
    }

    public final void bootCompleted() {
        this.mBootCompleted = true;
        this.mHandler.obtainMessage(13).sendToTarget();
    }

    public final void handleBootCompleted() {
        resetIfBootedAndConnected();
    }

    public final String getDefaultPrimaryStorageUuid() {
        return SystemProperties.getBoolean("ro.vold.primary_physical", false) ? "primary_physical" : StorageManager.UUID_PRIVATE_INTERNAL;
    }

    /* JADX WARN: Code restructure failed: missing block: B:18:0x004b, code lost:
        r11.mPrimaryStorageUuid = com.android.internal.util.XmlUtils.readStringAttribute(r4, "primaryStorageUuid");
     */
    @GuardedBy({"mLock"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void readSettingsLocked() {
        FileInputStream openRead;
        this.mRecords.clear();
        this.mPrimaryStorageUuid = getDefaultPrimaryStorageUuid();
        FileInputStream fileInputStream = null;
        try {
            try {
                openRead = this.mSettingsFile.openRead();
            } catch (Throwable th) {
                th = th;
            }
        } catch (FileNotFoundException unused) {
            IoUtils.closeQuietly(fileInputStream);
        } catch (IOException e) {
            e = e;
        } catch (XmlPullParserException e2) {
            e = e2;
        }
        try {
            TypedXmlPullParser resolvePullParser = Xml.resolvePullParser(openRead);
            while (true) {
                int next = resolvePullParser.next();
                boolean z = true;
                if (next == 1) {
                    IoUtils.closeQuietly(openRead);
                    return;
                } else if (next == 2) {
                    String name = resolvePullParser.getName();
                    if ("volumes".equals(name)) {
                        int attributeInt = resolvePullParser.getAttributeInt((String) null, "version", 1);
                        boolean z2 = SystemProperties.getBoolean("ro.vold.primary_physical", false);
                        if (attributeInt < 3 && (attributeInt < 2 || z2)) {
                            z = false;
                        }
                    } else if ("volume".equals(name)) {
                        VolumeRecord readVolumeRecord = readVolumeRecord(resolvePullParser);
                        this.mRecords.put(readVolumeRecord.fsUuid, readVolumeRecord);
                    }
                }
            }
        } catch (FileNotFoundException unused2) {
            fileInputStream = openRead;
            IoUtils.closeQuietly(fileInputStream);
        } catch (IOException e3) {
            e = e3;
            fileInputStream = openRead;
            Slog.wtf("StorageManagerService", "Failed reading metadata", e);
            IoUtils.closeQuietly(fileInputStream);
        } catch (XmlPullParserException e4) {
            e = e4;
            fileInputStream = openRead;
            Slog.wtf("StorageManagerService", "Failed reading metadata", e);
            IoUtils.closeQuietly(fileInputStream);
        } catch (Throwable th2) {
            th = th2;
            fileInputStream = openRead;
            IoUtils.closeQuietly(fileInputStream);
            throw th;
        }
    }

    @GuardedBy({"mLock"})
    public final void writeSettingsLocked() {
        FileOutputStream startWrite;
        FileOutputStream fileOutputStream = null;
        try {
            startWrite = this.mSettingsFile.startWrite();
        } catch (IOException unused) {
        }
        try {
            TypedXmlSerializer resolveSerializer = Xml.resolveSerializer(startWrite);
            resolveSerializer.startDocument((String) null, Boolean.TRUE);
            resolveSerializer.startTag((String) null, "volumes");
            resolveSerializer.attributeInt((String) null, "version", 3);
            XmlUtils.writeStringAttribute(resolveSerializer, "primaryStorageUuid", this.mPrimaryStorageUuid);
            int size = this.mRecords.size();
            for (int i = 0; i < size; i++) {
                writeVolumeRecord(resolveSerializer, this.mRecords.valueAt(i));
            }
            resolveSerializer.endTag((String) null, "volumes");
            resolveSerializer.endDocument();
            this.mSettingsFile.finishWrite(startWrite);
        } catch (IOException unused2) {
            fileOutputStream = startWrite;
            if (fileOutputStream != null) {
                this.mSettingsFile.failWrite(fileOutputStream);
            }
        }
    }

    public static VolumeRecord readVolumeRecord(TypedXmlPullParser typedXmlPullParser) throws IOException, XmlPullParserException {
        VolumeRecord volumeRecord = new VolumeRecord(typedXmlPullParser.getAttributeInt((String) null, "type"), XmlUtils.readStringAttribute(typedXmlPullParser, "fsUuid"));
        volumeRecord.partGuid = XmlUtils.readStringAttribute(typedXmlPullParser, "partGuid");
        volumeRecord.nickname = XmlUtils.readStringAttribute(typedXmlPullParser, "nickname");
        volumeRecord.userFlags = typedXmlPullParser.getAttributeInt((String) null, "userFlags");
        volumeRecord.createdMillis = typedXmlPullParser.getAttributeLong((String) null, "createdMillis", 0L);
        volumeRecord.lastSeenMillis = typedXmlPullParser.getAttributeLong((String) null, "lastSeenMillis", 0L);
        volumeRecord.lastTrimMillis = typedXmlPullParser.getAttributeLong((String) null, "lastTrimMillis", 0L);
        volumeRecord.lastBenchMillis = typedXmlPullParser.getAttributeLong((String) null, "lastBenchMillis", 0L);
        return volumeRecord;
    }

    public static void writeVolumeRecord(TypedXmlSerializer typedXmlSerializer, VolumeRecord volumeRecord) throws IOException {
        typedXmlSerializer.startTag((String) null, "volume");
        typedXmlSerializer.attributeInt((String) null, "type", volumeRecord.type);
        XmlUtils.writeStringAttribute(typedXmlSerializer, "fsUuid", volumeRecord.fsUuid);
        XmlUtils.writeStringAttribute(typedXmlSerializer, "partGuid", volumeRecord.partGuid);
        XmlUtils.writeStringAttribute(typedXmlSerializer, "nickname", volumeRecord.nickname);
        typedXmlSerializer.attributeInt((String) null, "userFlags", volumeRecord.userFlags);
        typedXmlSerializer.attributeLong((String) null, "createdMillis", volumeRecord.createdMillis);
        typedXmlSerializer.attributeLong((String) null, "lastSeenMillis", volumeRecord.lastSeenMillis);
        typedXmlSerializer.attributeLong((String) null, "lastTrimMillis", volumeRecord.lastTrimMillis);
        typedXmlSerializer.attributeLong((String) null, "lastBenchMillis", volumeRecord.lastBenchMillis);
        typedXmlSerializer.endTag((String) null, "volume");
    }

    public void registerListener(IStorageEventListener iStorageEventListener) {
        this.mCallbacks.register(iStorageEventListener);
    }

    public void unregisterListener(IStorageEventListener iStorageEventListener) {
        this.mCallbacks.unregister(iStorageEventListener);
    }

    @EnforcePermission("android.permission.SHUTDOWN")
    public void shutdown(IStorageShutdownObserver iStorageShutdownObserver) {
        super.shutdown_enforcePermission();
        Slog.i("StorageManagerService", "Shutting down");
        this.mHandler.obtainMessage(3, iStorageShutdownObserver).sendToTarget();
    }

    @EnforcePermission("android.permission.MOUNT_UNMOUNT_FILESYSTEMS")
    public void mount(String str) {
        super.mount_enforcePermission();
        VolumeInfo findVolumeByIdOrThrow = findVolumeByIdOrThrow(str);
        if (isMountDisallowed(findVolumeByIdOrThrow)) {
            throw new SecurityException("Mounting " + str + " restricted by policy");
        }
        mount(findVolumeByIdOrThrow);
    }

    public final void remountAppStorageDirs(Map<Integer, String> map, int i) {
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            int intValue = entry.getKey().intValue();
            String value = entry.getValue();
            Slog.i("StorageManagerService", "Remounting storage for pid: " + intValue);
            String[] sharedUserPackagesForPackage = this.mPmInternal.getSharedUserPackagesForPackage(value, i);
            int packageUid = this.mPmInternal.getPackageUid(value, 0L, i);
            if (sharedUserPackagesForPackage.length == 0) {
                sharedUserPackagesForPackage = new String[]{value};
            }
            try {
                this.mVold.remountAppStorageDirs(packageUid, intValue, sharedUserPackagesForPackage);
            } catch (RemoteException e) {
                throw e.rethrowAsRuntimeException();
            }
        }
    }

    public final void mount(final VolumeInfo volumeInfo) {
        try {
            Slog.i("StorageManagerService", "Mounting volume " + volumeInfo);
            this.mVold.mount(volumeInfo.id, volumeInfo.mountFlags, volumeInfo.mountUserId, new IVoldMountCallback.Stub() { // from class: com.android.server.StorageManagerService.7
                @Override // android.p005os.IVoldMountCallback
                public boolean onVolumeChecking(FileDescriptor fileDescriptor, String str, String str2) {
                    VolumeInfo volumeInfo2 = volumeInfo;
                    volumeInfo2.path = str;
                    volumeInfo2.internalPath = str2;
                    ParcelFileDescriptor parcelFileDescriptor = new ParcelFileDescriptor(fileDescriptor);
                    try {
                        try {
                            StorageManagerService.this.mStorageSessionController.onVolumeMount(parcelFileDescriptor, volumeInfo);
                            try {
                                parcelFileDescriptor.close();
                                return true;
                            } catch (Exception e) {
                                Slog.e("StorageManagerService", "Failed to close FUSE device fd", e);
                                return true;
                            }
                        } catch (Throwable th) {
                            try {
                                parcelFileDescriptor.close();
                            } catch (Exception e2) {
                                Slog.e("StorageManagerService", "Failed to close FUSE device fd", e2);
                            }
                            throw th;
                        }
                    } catch (StorageSessionController.ExternalStorageServiceException e3) {
                        Slog.e("StorageManagerService", "Failed to mount volume " + volumeInfo, e3);
                        Slog.i("StorageManagerService", "Scheduling reset in 10s");
                        StorageManagerService.this.mHandler.removeMessages(10);
                        StorageManagerService.this.mHandler.sendMessageDelayed(StorageManagerService.this.mHandler.obtainMessage(10), TimeUnit.SECONDS.toMillis((long) 10));
                        try {
                            parcelFileDescriptor.close();
                            return false;
                        } catch (Exception e4) {
                            Slog.e("StorageManagerService", "Failed to close FUSE device fd", e4);
                            return false;
                        }
                    }
                }
            });
            Slog.i("StorageManagerService", "Mounted volume " + volumeInfo);
        } catch (Exception e) {
            Slog.wtf("StorageManagerService", e);
        }
    }

    @EnforcePermission("android.permission.MOUNT_UNMOUNT_FILESYSTEMS")
    public void unmount(String str) {
        super.unmount_enforcePermission();
        unmount(findVolumeByIdOrThrow(str));
    }

    public final void unmount(VolumeInfo volumeInfo) {
        try {
            try {
                if (volumeInfo.type == 1) {
                    this.mInstaller.onPrivateVolumeRemoved(volumeInfo.getFsUuid());
                }
            } catch (Installer.InstallerException e) {
                Slog.e("StorageManagerService", "Failed unmount mirror data", e);
            }
            this.mVold.unmount(volumeInfo.id);
            this.mStorageSessionController.onVolumeUnmount(volumeInfo);
        } catch (Exception e2) {
            Slog.wtf("StorageManagerService", e2);
        }
    }

    @EnforcePermission("android.permission.MOUNT_FORMAT_FILESYSTEMS")
    public void format(String str) {
        super.format_enforcePermission();
        VolumeInfo findVolumeByIdOrThrow = findVolumeByIdOrThrow(str);
        String str2 = findVolumeByIdOrThrow.fsUuid;
        try {
            this.mVold.format(findVolumeByIdOrThrow.id, "auto");
            if (TextUtils.isEmpty(str2)) {
                return;
            }
            forgetVolume(str2);
        } catch (Exception e) {
            Slog.wtf("StorageManagerService", e);
        }
    }

    @EnforcePermission("android.permission.MOUNT_FORMAT_FILESYSTEMS")
    public void benchmark(String str, final IVoldTaskListener iVoldTaskListener) {
        super.benchmark_enforcePermission();
        try {
            this.mVold.benchmark(str, new IVoldTaskListener.Stub() { // from class: com.android.server.StorageManagerService.8
                @Override // android.p005os.IVoldTaskListener
                public void onStatus(int i, PersistableBundle persistableBundle) {
                    StorageManagerService.this.dispatchOnStatus(iVoldTaskListener, i, persistableBundle);
                }

                @Override // android.p005os.IVoldTaskListener
                public void onFinished(int i, PersistableBundle persistableBundle) {
                    StorageManagerService.this.dispatchOnFinished(iVoldTaskListener, i, persistableBundle);
                    String string = persistableBundle.getString("path");
                    String string2 = persistableBundle.getString("ident");
                    long j = persistableBundle.getLong("create");
                    long j2 = persistableBundle.getLong("run");
                    long j3 = persistableBundle.getLong("destroy");
                    ((DropBoxManager) StorageManagerService.this.mContext.getSystemService(DropBoxManager.class)).addText("storage_benchmark", StorageManagerService.this.scrubPath(string) + " " + string2 + " " + j + " " + j2 + " " + j3);
                    synchronized (StorageManagerService.this.mLock) {
                        VolumeRecord findRecordForPath = StorageManagerService.this.findRecordForPath(string);
                        if (findRecordForPath != null) {
                            findRecordForPath.lastBenchMillis = System.currentTimeMillis();
                            StorageManagerService.this.writeSettingsLocked();
                        }
                    }
                }
            });
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    @EnforcePermission("android.permission.MOUNT_FORMAT_FILESYSTEMS")
    public void partitionPublic(String str) {
        super.partitionPublic_enforcePermission();
        CountDownLatch findOrCreateDiskScanLatch = findOrCreateDiskScanLatch(str);
        try {
            this.mVold.partition(str, 0, -1);
            waitForLatch(findOrCreateDiskScanLatch, "partitionPublic", 180000L);
        } catch (Exception e) {
            Slog.wtf("StorageManagerService", e);
        }
    }

    @EnforcePermission("android.permission.MOUNT_FORMAT_FILESYSTEMS")
    public void partitionPrivate(String str) {
        super.partitionPrivate_enforcePermission();
        enforceAdminUser();
        CountDownLatch findOrCreateDiskScanLatch = findOrCreateDiskScanLatch(str);
        try {
            this.mVold.partition(str, 1, -1);
            waitForLatch(findOrCreateDiskScanLatch, "partitionPrivate", 180000L);
        } catch (Exception e) {
            Slog.wtf("StorageManagerService", e);
        }
    }

    @EnforcePermission("android.permission.MOUNT_FORMAT_FILESYSTEMS")
    public void partitionMixed(String str, int i) {
        super.partitionMixed_enforcePermission();
        enforceAdminUser();
        CountDownLatch findOrCreateDiskScanLatch = findOrCreateDiskScanLatch(str);
        try {
            this.mVold.partition(str, 2, i);
            waitForLatch(findOrCreateDiskScanLatch, "partitionMixed", 180000L);
        } catch (Exception e) {
            Slog.wtf("StorageManagerService", e);
        }
    }

    @EnforcePermission("android.permission.MOUNT_UNMOUNT_FILESYSTEMS")
    public void setVolumeNickname(String str, String str2) {
        super.setVolumeNickname_enforcePermission();
        Objects.requireNonNull(str);
        synchronized (this.mLock) {
            VolumeRecord volumeRecord = this.mRecords.get(str);
            volumeRecord.nickname = str2;
            this.mCallbacks.notifyVolumeRecordChanged(volumeRecord);
            writeSettingsLocked();
        }
    }

    @EnforcePermission("android.permission.MOUNT_UNMOUNT_FILESYSTEMS")
    public void setVolumeUserFlags(String str, int i, int i2) {
        super.setVolumeUserFlags_enforcePermission();
        Objects.requireNonNull(str);
        synchronized (this.mLock) {
            VolumeRecord volumeRecord = this.mRecords.get(str);
            volumeRecord.userFlags = (i & i2) | (volumeRecord.userFlags & (~i2));
            this.mCallbacks.notifyVolumeRecordChanged(volumeRecord);
            writeSettingsLocked();
        }
    }

    @EnforcePermission("android.permission.MOUNT_UNMOUNT_FILESYSTEMS")
    public void forgetVolume(String str) {
        super.forgetVolume_enforcePermission();
        Objects.requireNonNull(str);
        synchronized (this.mLock) {
            VolumeRecord remove = this.mRecords.remove(str);
            if (remove != null && !TextUtils.isEmpty(remove.partGuid)) {
                this.mHandler.obtainMessage(9, remove).sendToTarget();
            }
            this.mCallbacks.notifyVolumeForgotten(str);
            if (Objects.equals(this.mPrimaryStorageUuid, str)) {
                this.mPrimaryStorageUuid = getDefaultPrimaryStorageUuid();
                this.mHandler.obtainMessage(10).sendToTarget();
            }
            writeSettingsLocked();
        }
    }

    @EnforcePermission("android.permission.MOUNT_UNMOUNT_FILESYSTEMS")
    public void forgetAllVolumes() {
        super.forgetAllVolumes_enforcePermission();
        synchronized (this.mLock) {
            for (int i = 0; i < this.mRecords.size(); i++) {
                String keyAt = this.mRecords.keyAt(i);
                VolumeRecord valueAt = this.mRecords.valueAt(i);
                if (!TextUtils.isEmpty(valueAt.partGuid)) {
                    this.mHandler.obtainMessage(9, valueAt).sendToTarget();
                }
                this.mCallbacks.notifyVolumeForgotten(keyAt);
            }
            this.mRecords.clear();
            if (!Objects.equals(StorageManager.UUID_PRIVATE_INTERNAL, this.mPrimaryStorageUuid)) {
                this.mPrimaryStorageUuid = getDefaultPrimaryStorageUuid();
            }
            writeSettingsLocked();
            this.mHandler.obtainMessage(10).sendToTarget();
        }
    }

    public final void forgetPartition(String str, String str2) {
        try {
            this.mVold.forgetPartition(str, str2);
        } catch (Exception e) {
            Slog.wtf("StorageManagerService", e);
        }
    }

    @EnforcePermission("android.permission.MOUNT_FORMAT_FILESYSTEMS")
    public void fstrim(int i, final IVoldTaskListener iVoldTaskListener) {
        super.fstrim_enforcePermission();
        try {
            if (needsCheckpoint() && supportsBlockCheckpoint()) {
                Slog.i("StorageManagerService", "Skipping fstrim - block based checkpoint in progress");
                return;
            }
            this.mVold.fstrim(i, new IVoldTaskListener.Stub() { // from class: com.android.server.StorageManagerService.9
                @Override // android.p005os.IVoldTaskListener
                public void onStatus(int i2, PersistableBundle persistableBundle) {
                    StorageManagerService.this.dispatchOnStatus(iVoldTaskListener, i2, persistableBundle);
                    if (i2 != 0) {
                        return;
                    }
                    String string = persistableBundle.getString("path");
                    long j = persistableBundle.getLong("bytes");
                    long j2 = persistableBundle.getLong("time");
                    ((DropBoxManager) StorageManagerService.this.mContext.getSystemService(DropBoxManager.class)).addText("storage_trim", StorageManagerService.this.scrubPath(string) + " " + j + " " + j2);
                    synchronized (StorageManagerService.this.mLock) {
                        VolumeRecord findRecordForPath = StorageManagerService.this.findRecordForPath(string);
                        if (findRecordForPath != null) {
                            findRecordForPath.lastTrimMillis = System.currentTimeMillis();
                            StorageManagerService.this.writeSettingsLocked();
                        }
                    }
                }

                @Override // android.p005os.IVoldTaskListener
                public void onFinished(int i2, PersistableBundle persistableBundle) {
                    StorageManagerService.this.dispatchOnFinished(iVoldTaskListener, i2, persistableBundle);
                }
            });
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    public void runIdleMaint(final Runnable runnable) {
        enforcePermission("android.permission.MOUNT_FORMAT_FILESYSTEMS");
        try {
            if (needsCheckpoint() && supportsBlockCheckpoint()) {
                Slog.i("StorageManagerService", "Skipping idle maintenance - block based checkpoint in progress");
            }
            this.mVold.runIdleMaint(this.mNeedGC, new IVoldTaskListener.Stub() { // from class: com.android.server.StorageManagerService.10
                @Override // android.p005os.IVoldTaskListener
                public void onStatus(int i, PersistableBundle persistableBundle) {
                }

                @Override // android.p005os.IVoldTaskListener
                public void onFinished(int i, PersistableBundle persistableBundle) {
                    if (runnable != null) {
                        BackgroundThread.getHandler().post(runnable);
                    }
                }
            });
        } catch (Exception e) {
            Slog.wtf("StorageManagerService", e);
        }
    }

    public void runIdleMaintenance() {
        runIdleMaint(null);
    }

    public void abortIdleMaint(final Runnable runnable) {
        enforcePermission("android.permission.MOUNT_FORMAT_FILESYSTEMS");
        try {
            this.mVold.abortIdleMaint(new IVoldTaskListener.Stub() { // from class: com.android.server.StorageManagerService.11
                @Override // android.p005os.IVoldTaskListener
                public void onStatus(int i, PersistableBundle persistableBundle) {
                }

                @Override // android.p005os.IVoldTaskListener
                public void onFinished(int i, PersistableBundle persistableBundle) {
                    if (runnable != null) {
                        BackgroundThread.getHandler().post(runnable);
                    }
                }
            });
        } catch (Exception e) {
            Slog.wtf("StorageManagerService", e);
        }
    }

    public void abortIdleMaintenance() {
        abortIdleMaint(null);
    }

    public final boolean prepareSmartIdleMaint() {
        boolean z = DeviceConfig.getBoolean("storage_native_boot", "smart_idle_maint_enabled", false);
        if (z) {
            this.mLifetimePercentThreshold = DeviceConfig.getInt("storage_native_boot", "lifetime_threshold", 70);
            this.mMinSegmentsThreshold = DeviceConfig.getInt("storage_native_boot", "min_segments_threshold", 512);
            this.mDirtyReclaimRate = DeviceConfig.getFloat("storage_native_boot", "dirty_reclaim_rate", 0.5f);
            this.mSegmentReclaimWeight = DeviceConfig.getFloat("storage_native_boot", "segment_reclaim_weight", 1.0f);
            this.mLowBatteryLevel = DeviceConfig.getFloat("storage_native_boot", "low_battery_level", 20.0f);
            this.mChargingRequired = DeviceConfig.getBoolean("storage_native_boot", "charging_required", true);
            this.mMinGCSleepTime = DeviceConfig.getInt("storage_native_boot", "min_gc_sleeptime", (int) FrameworkStatsLog.WIFI_BYTES_TRANSFER);
            this.mTargetDirtyRatio = DeviceConfig.getInt("storage_native_boot", "target_dirty_ratio", 80);
            this.mNeedGC = false;
            loadStorageWriteRecords();
            try {
                this.mVold.refreshLatestWrite();
            } catch (Exception e) {
                Slog.wtf("StorageManagerService", e);
            }
            refreshLifetimeConstraint();
        }
        return z;
    }

    public boolean isPassedLifetimeThresh() {
        return this.mPassedLifetimeThresh;
    }

    public final void loadStorageWriteRecords() {
        FileInputStream fileInputStream = null;
        try {
            try {
                fileInputStream = this.mWriteRecordFile.openRead();
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                if (objectInputStream.readInt() == sSmartIdleMaintPeriod) {
                    this.mStorageWriteRecords = (int[]) objectInputStream.readObject();
                }
            } catch (FileNotFoundException unused) {
            } catch (Exception e) {
                Slog.wtf("StorageManagerService", "Failed reading write records", e);
            }
        } finally {
            IoUtils.closeQuietly(fileInputStream);
        }
    }

    public final int getAverageWriteAmount() {
        return Arrays.stream(this.mStorageWriteRecords).sum() / this.mMaxWriteRecords;
    }

    public final void updateStorageWriteRecords(int i) {
        System.arraycopy(this.mStorageWriteRecords, 0, this.mStorageWriteRecords, 1, this.mMaxWriteRecords - 1);
        this.mStorageWriteRecords[0] = i;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = this.mWriteRecordFile.startWrite();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeInt(sSmartIdleMaintPeriod);
            objectOutputStream.writeObject(this.mStorageWriteRecords);
            this.mWriteRecordFile.finishWrite(fileOutputStream);
        } catch (IOException unused) {
            if (fileOutputStream != null) {
                this.mWriteRecordFile.failWrite(fileOutputStream);
            }
        }
    }

    public final boolean checkChargeStatus() {
        int intExtra;
        Intent registerReceiver = this.mContext.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        if (this.mChargingRequired && (intExtra = registerReceiver.getIntExtra("status", -1)) != 2 && intExtra != 5) {
            Slog.w("StorageManagerService", "Battery is not being charged");
            return false;
        }
        float intExtra2 = (registerReceiver.getIntExtra("level", -1) * 100.0f) / registerReceiver.getIntExtra("scale", -1);
        if (intExtra2 < this.mLowBatteryLevel) {
            Slog.w("StorageManagerService", "Battery level is " + intExtra2 + ", which is lower than threshold: " + this.mLowBatteryLevel);
            return false;
        }
        return true;
    }

    public final boolean refreshLifetimeConstraint() {
        try {
            int storageLifeTime = this.mVold.getStorageLifeTime();
            if (storageLifeTime == -1) {
                Slog.w("StorageManagerService", "Failed to get storage lifetime");
                return false;
            } else if (storageLifeTime > this.mLifetimePercentThreshold) {
                Slog.w("StorageManagerService", "Ended smart idle maintenance, because of lifetime(" + storageLifeTime + "), lifetime threshold(" + this.mLifetimePercentThreshold + ")");
                this.mPassedLifetimeThresh = true;
                return false;
            } else {
                Slog.i("StorageManagerService", "Storage lifetime: " + storageLifeTime);
                return true;
            }
        } catch (Exception e) {
            Slog.wtf("StorageManagerService", e);
            return false;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:38:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void runSmartIdleMaint(Runnable runnable) {
        int i;
        int writeAmount;
        int i2;
        enforcePermission("android.permission.MOUNT_FORMAT_FILESYSTEMS");
        try {
            try {
                i = this.mTargetDirtyRatio;
                writeAmount = this.mVold.getWriteAmount();
            } catch (Exception e) {
                Slog.wtf("StorageManagerService", e);
                if (runnable == null) {
                    return;
                }
            }
            if (writeAmount == -1) {
                Slog.w("StorageManagerService", "Failed to get storage write record");
                if (runnable != null) {
                    runnable.run();
                    return;
                }
                return;
            }
            updateStorageWriteRecords(writeAmount);
            if (needsCheckpoint() && supportsBlockCheckpoint()) {
                Slog.i("StorageManagerService", "Skipping smart idle maintenance - block based checkpoint in progress");
                if (runnable == null) {
                    return;
                }
                runnable.run();
            }
            if (refreshLifetimeConstraint() && checkChargeStatus()) {
                i2 = getAverageWriteAmount();
                int i3 = i;
                int i4 = i2;
                Slog.i("StorageManagerService", "Set smart idle maintenance: latest write amount: " + writeAmount + ", average write amount: " + i4 + ", min segment threshold: " + this.mMinSegmentsThreshold + ", dirty reclaim rate: " + this.mDirtyReclaimRate + ", segment reclaim weight: " + this.mSegmentReclaimWeight + ", period(min): " + sSmartIdleMaintPeriod + ", min gc sleep time(ms): " + this.mMinGCSleepTime + ", target dirty ratio: " + i3);
                this.mVold.setGCUrgentPace(i4, this.mMinSegmentsThreshold, this.mDirtyReclaimRate, this.mSegmentReclaimWeight, sSmartIdleMaintPeriod, this.mMinGCSleepTime, i3);
                if (runnable == null) {
                }
                runnable.run();
            }
            Slog.i("StorageManagerService", "Turn off gc_urgent based on checking lifetime and charge status");
            i2 = 0;
            i = 100;
            int i32 = i;
            int i42 = i2;
            Slog.i("StorageManagerService", "Set smart idle maintenance: latest write amount: " + writeAmount + ", average write amount: " + i42 + ", min segment threshold: " + this.mMinSegmentsThreshold + ", dirty reclaim rate: " + this.mDirtyReclaimRate + ", segment reclaim weight: " + this.mSegmentReclaimWeight + ", period(min): " + sSmartIdleMaintPeriod + ", min gc sleep time(ms): " + this.mMinGCSleepTime + ", target dirty ratio: " + i32);
            this.mVold.setGCUrgentPace(i42, this.mMinSegmentsThreshold, this.mDirtyReclaimRate, this.mSegmentReclaimWeight, sSmartIdleMaintPeriod, this.mMinGCSleepTime, i32);
            if (runnable == null) {
            }
            runnable.run();
        } catch (Throwable th) {
            if (runnable != null) {
                runnable.run();
            }
            throw th;
        }
    }

    @EnforcePermission("android.permission.MOUNT_UNMOUNT_FILESYSTEMS")
    public void setDebugFlags(int i, int i2) {
        long clearCallingIdentity;
        super.setDebugFlags_enforcePermission();
        String str = "force_off";
        if ((i2 & 3) != 0) {
            String str2 = (i & 1) != 0 ? "force_on" : (i & 2) != 0 ? "force_off" : "";
            clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                SystemProperties.set("persist.sys.adoptable", str2);
                this.mHandler.obtainMessage(10).sendToTarget();
                Binder.restoreCallingIdentity(clearCallingIdentity);
            } finally {
            }
        }
        if ((i2 & 12) != 0) {
            if ((i & 4) != 0) {
                str = "force_on";
            } else if ((i & 8) == 0) {
                str = "";
            }
            clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                SystemProperties.set("persist.sys.sdcardfs", str);
                this.mHandler.obtainMessage(10).sendToTarget();
            } finally {
            }
        }
        if ((i2 & 16) != 0) {
            boolean z = (i & 16) != 0;
            clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                SystemProperties.set("persist.sys.virtual_disk", Boolean.toString(z));
                this.mHandler.obtainMessage(10).sendToTarget();
            } finally {
            }
        }
    }

    public String getPrimaryStorageUuid() {
        String str;
        synchronized (this.mLock) {
            str = this.mPrimaryStorageUuid;
        }
        return str;
    }

    @EnforcePermission("android.permission.MOUNT_UNMOUNT_FILESYSTEMS")
    public void setPrimaryStorageUuid(String str, IPackageMoveObserver iPackageMoveObserver) {
        super.setPrimaryStorageUuid_enforcePermission();
        synchronized (this.mLock) {
            if (Objects.equals(this.mPrimaryStorageUuid, str)) {
                throw new IllegalArgumentException("Primary storage already at " + str);
            } else if (this.mMoveCallback != null) {
                throw new IllegalStateException("Move already in progress");
            } else {
                this.mMoveCallback = iPackageMoveObserver;
                this.mMoveTargetUuid = str;
                for (UserInfo userInfo : ((UserManager) this.mContext.getSystemService(UserManager.class)).getUsers()) {
                    if (StorageManager.isFileEncrypted() && !isUserKeyUnlocked(userInfo.id)) {
                        Slog.w("StorageManagerService", "Failing move due to locked user " + userInfo.id);
                        onMoveStatusLocked(-10);
                        return;
                    }
                }
                if (!"primary_physical".equals(this.mPrimaryStorageUuid) && !"primary_physical".equals(str)) {
                    int i = this.mCurrentUserId;
                    VolumeInfo findStorageForUuidAsUser = findStorageForUuidAsUser(this.mPrimaryStorageUuid, i);
                    VolumeInfo findStorageForUuidAsUser2 = findStorageForUuidAsUser(str, i);
                    if (findStorageForUuidAsUser == null) {
                        Slog.w("StorageManagerService", "Failing move due to missing from volume " + this.mPrimaryStorageUuid);
                        onMoveStatusLocked(-6);
                        return;
                    } else if (findStorageForUuidAsUser2 == null) {
                        Slog.w("StorageManagerService", "Failing move due to missing to volume " + str);
                        onMoveStatusLocked(-6);
                        return;
                    } else {
                        try {
                            this.mVold.moveStorage(findStorageForUuidAsUser.id, findStorageForUuidAsUser2.id, new IVoldTaskListener.Stub() { // from class: com.android.server.StorageManagerService.12
                                @Override // android.p005os.IVoldTaskListener
                                public void onFinished(int i2, PersistableBundle persistableBundle) {
                                }

                                @Override // android.p005os.IVoldTaskListener
                                public void onStatus(int i2, PersistableBundle persistableBundle) {
                                    synchronized (StorageManagerService.this.mLock) {
                                        StorageManagerService.this.onMoveStatusLocked(i2);
                                    }
                                }
                            });
                            return;
                        } catch (Exception e) {
                            Slog.wtf("StorageManagerService", e);
                            return;
                        }
                    }
                }
                Slog.d("StorageManagerService", "Skipping move to/from primary physical");
                onMoveStatusLocked(82);
                onMoveStatusLocked(-100);
                this.mHandler.obtainMessage(10).sendToTarget();
            }
        }
    }

    public final void warnOnNotMounted() {
        synchronized (this.mLock) {
            for (int i = 0; i < this.mVolumes.size(); i++) {
                VolumeInfo valueAt = this.mVolumes.valueAt(i);
                if (valueAt.isPrimary() && valueAt.isMountedWritable()) {
                    return;
                }
            }
            Slog.w("StorageManagerService", "No primary storage mounted!");
        }
    }

    public final boolean isUidOwnerOfPackageOrSystem(String str, int i) {
        if (i == 1000) {
            return true;
        }
        return this.mPmInternal.isSameApp(str, i, UserHandle.getUserId(i));
    }

    public String getMountedObbPath(String str) {
        ObbState obbState;
        Objects.requireNonNull(str, "rawPath cannot be null");
        warnOnNotMounted();
        synchronized (this.mObbMounts) {
            obbState = this.mObbPathToStateMap.get(str);
        }
        if (obbState == null) {
            Slog.w("StorageManagerService", "Failed to find OBB mounted at " + str);
            return null;
        }
        return findVolumeByIdOrThrow(obbState.volId).getPath().getAbsolutePath();
    }

    public boolean isObbMounted(String str) {
        boolean containsKey;
        Objects.requireNonNull(str, "rawPath cannot be null");
        synchronized (this.mObbMounts) {
            containsKey = this.mObbPathToStateMap.containsKey(str);
        }
        return containsKey;
    }

    public void mountObb(String str, String str2, IObbActionListener iObbActionListener, int i, ObbInfo obbInfo) {
        Objects.requireNonNull(str, "rawPath cannot be null");
        Objects.requireNonNull(str2, "canonicalPath cannot be null");
        Objects.requireNonNull(iObbActionListener, "token cannot be null");
        Objects.requireNonNull(obbInfo, "obbIfno cannot be null");
        int callingUid = Binder.getCallingUid();
        MountObbAction mountObbAction = new MountObbAction(new ObbState(str, str2, callingUid, iObbActionListener, i, null), callingUid, obbInfo);
        ObbActionHandler obbActionHandler = this.mObbActionHandler;
        obbActionHandler.sendMessage(obbActionHandler.obtainMessage(1, mountObbAction));
    }

    public void unmountObb(String str, boolean z, IObbActionListener iObbActionListener, int i) {
        ObbState obbState;
        Objects.requireNonNull(str, "rawPath cannot be null");
        synchronized (this.mObbMounts) {
            obbState = this.mObbPathToStateMap.get(str);
        }
        if (obbState != null) {
            UnmountObbAction unmountObbAction = new UnmountObbAction(new ObbState(str, obbState.canonicalPath, Binder.getCallingUid(), iObbActionListener, i, obbState.volId), z);
            ObbActionHandler obbActionHandler = this.mObbActionHandler;
            obbActionHandler.sendMessage(obbActionHandler.obtainMessage(1, unmountObbAction));
            return;
        }
        Slog.w("StorageManagerService", "Unknown OBB mount at " + str);
    }

    public boolean supportsCheckpoint() throws RemoteException {
        return this.mVold.supportsCheckpoint();
    }

    public void startCheckpoint(int i) throws RemoteException {
        int callingUid = Binder.getCallingUid();
        if (callingUid != 1000 && callingUid != 0 && callingUid != 2000) {
            throw new SecurityException("no permission to start filesystem checkpoint");
        }
        this.mVold.startCheckpoint(i);
    }

    public void commitChanges() throws RemoteException {
        if (Binder.getCallingUid() != 1000) {
            throw new SecurityException("no permission to commit checkpoint changes");
        }
        this.mVold.commitChanges();
    }

    @EnforcePermission("android.permission.MOUNT_FORMAT_FILESYSTEMS")
    public boolean needsCheckpoint() throws RemoteException {
        super.needsCheckpoint_enforcePermission();
        return this.mVold.needsCheckpoint();
    }

    public void abortChanges(String str, boolean z) throws RemoteException {
        if (Binder.getCallingUid() != 1000) {
            throw new SecurityException("no permission to commit checkpoint changes");
        }
        this.mVold.abortChanges(str, z);
    }

    @EnforcePermission("android.permission.STORAGE_INTERNAL")
    public void createUserKey(int i, int i2, boolean z) {
        super.createUserKey_enforcePermission();
        try {
            this.mVold.createUserKey(i, i2, z);
            synchronized (this.mLock) {
                this.mLocalUnlockedUsers.append(i);
            }
        } catch (Exception e) {
            Slog.wtf("StorageManagerService", e);
        }
    }

    @EnforcePermission("android.permission.STORAGE_INTERNAL")
    public void destroyUserKey(int i) {
        super.destroyUserKey_enforcePermission();
        try {
            this.mVold.destroyUserKey(i);
            synchronized (this.mLock) {
                this.mLocalUnlockedUsers.remove(i);
            }
        } catch (Exception e) {
            Slog.wtf("StorageManagerService", e);
        }
    }

    @EnforcePermission("android.permission.STORAGE_INTERNAL")
    public void setUserKeyProtection(int i, byte[] bArr) throws RemoteException {
        super.setUserKeyProtection_enforcePermission();
        this.mVold.setUserKeyProtection(i, HexDump.toHexString(bArr));
    }

    @EnforcePermission("android.permission.STORAGE_INTERNAL")
    public void unlockUserKey(int i, int i2, byte[] bArr) throws RemoteException {
        super.unlockUserKey_enforcePermission();
        if (StorageManager.isFileEncrypted()) {
            this.mVold.unlockUserKey(i, i2, HexDump.toHexString(bArr));
        }
        synchronized (this.mLock) {
            this.mLocalUnlockedUsers.append(i);
        }
    }

    @EnforcePermission("android.permission.STORAGE_INTERNAL")
    public void lockUserKey(int i) {
        super.lockUserKey_enforcePermission();
        if (i == 0 && UserManager.isHeadlessSystemUserMode()) {
            throw new IllegalArgumentException("Headless system user data cannot be locked..");
        }
        if (!isUserKeyUnlocked(i)) {
            Slog.d("StorageManagerService", "User " + i + "'s CE storage is already locked");
            return;
        }
        try {
            this.mVold.lockUserKey(i);
            synchronized (this.mLock) {
                this.mLocalUnlockedUsers.remove(i);
            }
        } catch (Exception e) {
            Slog.wtf("StorageManagerService", e);
        }
    }

    public boolean isUserKeyUnlocked(int i) {
        boolean contains;
        synchronized (this.mLock) {
            contains = this.mLocalUnlockedUsers.contains(i);
        }
        return contains;
    }

    public final boolean isSystemUnlocked(int i) {
        boolean contains;
        synchronized (this.mLock) {
            contains = ArrayUtils.contains(this.mSystemUnlockedUsers, i);
        }
        return contains;
    }

    public final void prepareUserStorageIfNeeded(VolumeInfo volumeInfo) throws Exception {
        int i;
        if (volumeInfo.type != 1) {
            return;
        }
        UserManagerInternal userManagerInternal = (UserManagerInternal) LocalServices.getService(UserManagerInternal.class);
        for (UserInfo userInfo : ((UserManager) this.mContext.getSystemService(UserManager.class)).getUsers()) {
            if (userManagerInternal.isUserUnlockingOrUnlocked(userInfo.id)) {
                i = 3;
            } else if (userManagerInternal.isUserRunning(userInfo.id)) {
                i = 1;
            }
            prepareUserStorageInternal(volumeInfo.fsUuid, userInfo.id, userInfo.serialNumber, i);
        }
    }

    @EnforcePermission("android.permission.STORAGE_INTERNAL")
    public void prepareUserStorage(String str, int i, int i2, int i3) {
        super.prepareUserStorage_enforcePermission();
        try {
            prepareUserStorageInternal(str, i, i2, i3);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public final void prepareUserStorageInternal(String str, int i, int i2, int i3) throws Exception {
        VolumeInfo findVolumeByUuid;
        try {
            this.mVold.prepareUserStorage(str, i, i2, i3);
            if (str == null || (findVolumeByUuid = ((StorageManager) this.mContext.getSystemService(StorageManager.class)).findVolumeByUuid(str)) == null || i != 0 || findVolumeByUuid.type != 1) {
                return;
            }
            this.mInstaller.tryMountDataMirror(str);
        } catch (Exception e) {
            EventLog.writeEvent(1397638484, "224585613", -1, "");
            Slog.wtf("StorageManagerService", e);
            if (((UserManagerInternal) LocalServices.getService(UserManagerInternal.class)).shouldIgnorePrepareStorageErrors(i)) {
                Slog.wtf("StorageManagerService", "ignoring error preparing storage for existing user " + i + "; device may be insecure!");
                return;
            }
            throw e;
        }
    }

    @EnforcePermission("android.permission.STORAGE_INTERNAL")
    public void destroyUserStorage(String str, int i, int i2) {
        super.destroyUserStorage_enforcePermission();
        try {
            this.mVold.destroyUserStorage(str, i, i2);
        } catch (Exception e) {
            Slog.wtf("StorageManagerService", e);
        }
    }

    public void fixupAppDir(String str) {
        Matcher matcher = KNOWN_APP_DIR_PATHS.matcher(str);
        if (matcher.matches()) {
            if (matcher.group(2) == null) {
                Log.e("StorageManagerService", "Asked to fixup an app dir without a userId: " + str);
                return;
            }
            try {
                int parseInt = Integer.parseInt(matcher.group(2));
                String group = matcher.group(3);
                int packageUidAsUser = this.mContext.getPackageManager().getPackageUidAsUser(group, parseInt);
                try {
                    IVold iVold = this.mVold;
                    iVold.fixupAppDir(str + "/", packageUidAsUser);
                    return;
                } catch (RemoteException | ServiceSpecificException e) {
                    Log.e("StorageManagerService", "Failed to fixup app dir for " + group, e);
                    return;
                }
            } catch (PackageManager.NameNotFoundException e2) {
                Log.e("StorageManagerService", "Couldn't find package to fixup app dir " + str, e2);
                return;
            } catch (NumberFormatException e3) {
                Log.e("StorageManagerService", "Invalid userId in path: " + str, e3);
                return;
            }
        }
        Log.e("StorageManagerService", "Path " + str + " is not a valid application-specific directory");
    }

    public void disableAppDataIsolation(String str, int i, int i2) {
        int callingUid = Binder.getCallingUid();
        if (callingUid != 0 && callingUid != 2000) {
            throw new SecurityException("no permission to enable app visibility");
        }
        String[] sharedUserPackagesForPackage = this.mPmInternal.getSharedUserPackagesForPackage(str, i2);
        int packageUid = this.mPmInternal.getPackageUid(str, 0L, i2);
        if (sharedUserPackagesForPackage.length == 0) {
            sharedUserPackagesForPackage = new String[]{str};
        }
        try {
            this.mVold.unmountAppStorageDirs(packageUid, i, sharedUserPackagesForPackage);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    public PendingIntent getManageSpaceActivityIntent(String str, int i) {
        int callingUidOrThrow = Binder.getCallingUidOrThrow();
        try {
            String[] packagesForUid = this.mIPackageManager.getPackagesForUid(callingUidOrThrow);
            if (packagesForUid == null) {
                throw new SecurityException("Unknown uid " + callingUidOrThrow);
            } else if (!this.mStorageManagerInternal.hasExternalStorageAccess(callingUidOrThrow, packagesForUid[0])) {
                throw new SecurityException("Only File Manager Apps permitted");
            } else {
                try {
                    ApplicationInfo applicationInfo = this.mIPackageManager.getApplicationInfo(str, 0L, UserHandle.getUserId(callingUidOrThrow));
                    if (applicationInfo == null) {
                        throw new IllegalArgumentException("Invalid packageName");
                    }
                    if (applicationInfo.manageSpaceActivityName == null) {
                        Log.i("StorageManagerService", str + " doesn't have a manageSpaceActivity");
                        return null;
                    }
                    long clearCallingIdentity = Binder.clearCallingIdentity();
                    try {
                        try {
                            Context createPackageContext = this.mContext.createPackageContext(str, 0);
                            Intent intent = new Intent("android.intent.action.VIEW");
                            intent.setClassName(str, applicationInfo.manageSpaceActivityName);
                            intent.setFlags(268435456);
                            return PendingIntent.getActivity(createPackageContext, i, intent, 1409286144, ActivityOptions.makeBasic().setPendingIntentCreatorBackgroundActivityStartMode(2).toBundle());
                        } catch (PackageManager.NameNotFoundException unused) {
                            throw new IllegalArgumentException("packageName not found");
                        }
                    } finally {
                        Binder.restoreCallingIdentity(clearCallingIdentity);
                    }
                } catch (RemoteException unused2) {
                    throw new SecurityException("Only File Manager Apps permitted");
                }
            }
        } catch (RemoteException e) {
            throw new SecurityException("Unknown uid " + callingUidOrThrow, e);
        }
    }

    public void notifyAppIoBlocked(String str, int i, int i2, int i3) {
        enforceExternalStorageService();
        this.mStorageSessionController.notifyAppIoBlocked(str, i, i2, i3);
    }

    public void notifyAppIoResumed(String str, int i, int i2, int i3) {
        enforceExternalStorageService();
        this.mStorageSessionController.notifyAppIoResumed(str, i, i2, i3);
    }

    public boolean isAppIoBlocked(String str, int i, int i2, int i3) {
        return isAppIoBlocked(i);
    }

    public final boolean isAppIoBlocked(int i) {
        return this.mStorageSessionController.isAppIoBlocked(i);
    }

    public void setCloudMediaProvider(String str) {
        enforceExternalStorageService();
        int userId = UserHandle.getUserId(Binder.getCallingUid());
        synchronized (this.mCloudMediaProviders) {
            if (!Objects.equals(str, this.mCloudMediaProviders.get(userId))) {
                this.mCloudMediaProviders.put(userId, str);
                this.mHandler.obtainMessage(16, userId, 0, str).sendToTarget();
            }
        }
    }

    public String getCloudMediaProvider() {
        String str;
        ProviderInfo resolveContentProvider;
        int callingUid = Binder.getCallingUid();
        int userId = UserHandle.getUserId(callingUid);
        synchronized (this.mCloudMediaProviders) {
            str = this.mCloudMediaProviders.get(userId);
        }
        if (str == null || (resolveContentProvider = this.mPmInternal.resolveContentProvider(str, 0L, userId, callingUid)) == null || this.mPmInternal.filterAppAccess(resolveContentProvider.packageName, callingUid, userId)) {
            return null;
        }
        return str;
    }

    public final void enforceExternalStorageService() {
        enforcePermission("android.permission.WRITE_MEDIA_STORAGE");
        if (UserHandle.getAppId(Binder.getCallingUid()) != this.mMediaStoreAuthorityAppId) {
            throw new SecurityException("Only the ExternalStorageService is permitted");
        }
    }

    /* loaded from: classes.dex */
    public class AppFuseMountScope extends AppFuseBridge.MountScope {
        public boolean mMounted;

        public AppFuseMountScope(int i, int i2) {
            super(i, i2);
            this.mMounted = false;
        }

        @Override // com.android.server.storage.AppFuseBridge.MountScope
        public ParcelFileDescriptor open() throws AppFuseMountException {
            try {
                FileDescriptor mountAppFuse = StorageManagerService.this.mVold.mountAppFuse(this.uid, this.mountId);
                this.mMounted = true;
                return new ParcelFileDescriptor(mountAppFuse);
            } catch (Exception e) {
                throw new AppFuseMountException("Failed to mount", e);
            }
        }

        @Override // com.android.server.storage.AppFuseBridge.MountScope
        public ParcelFileDescriptor openFile(int i, int i2, int i3) throws AppFuseMountException {
            try {
                return new ParcelFileDescriptor(StorageManagerService.this.mVold.openAppFuseFile(this.uid, i, i2, i3));
            } catch (Exception e) {
                throw new AppFuseMountException("Failed to open", e);
            }
        }

        @Override // java.lang.AutoCloseable
        public void close() throws Exception {
            if (this.mMounted) {
                StorageManagerService.this.mVold.unmountAppFuse(this.uid, this.mountId);
                this.mMounted = false;
            }
        }
    }

    public AppFuseMount mountProxyFileDescriptorBridge() {
        boolean z;
        AppFuseMount appFuseMount;
        Slog.v("StorageManagerService", "mountProxyFileDescriptorBridge");
        int callingUid = Binder.getCallingUid();
        while (true) {
            synchronized (this.mAppFuseLock) {
                if (this.mAppFuseBridge == null) {
                    this.mAppFuseBridge = new AppFuseBridge();
                    new Thread(this.mAppFuseBridge, AppFuseBridge.TAG).start();
                    z = true;
                } else {
                    z = false;
                }
                try {
                    int i = this.mNextAppFuseName;
                    this.mNextAppFuseName = i + 1;
                    try {
                        appFuseMount = new AppFuseMount(i, this.mAppFuseBridge.addBridge(new AppFuseMountScope(callingUid, i)));
                    } catch (FuseUnavailableMountException e) {
                        if (z) {
                            Slog.e("StorageManagerService", "", e);
                            return null;
                        }
                        this.mAppFuseBridge = null;
                    }
                } catch (AppFuseMountException e2) {
                    throw e2.rethrowAsParcelableException();
                }
            }
            return appFuseMount;
        }
    }

    public ParcelFileDescriptor openProxyFileDescriptor(int i, int i2, int i3) {
        Slog.v("StorageManagerService", "mountProxyFileDescriptor");
        int i4 = i3 & 805306368;
        try {
            synchronized (this.mAppFuseLock) {
                AppFuseBridge appFuseBridge = this.mAppFuseBridge;
                if (appFuseBridge == null) {
                    Slog.e("StorageManagerService", "FuseBridge has not been created");
                    return null;
                }
                return appFuseBridge.openFile(i, i2, i4);
            }
        } catch (FuseUnavailableMountException | InterruptedException e) {
            Slog.v("StorageManagerService", "The mount point has already been invalid", e);
            return null;
        }
    }

    public void mkdirs(String str, String str2) {
        int callingUid = Binder.getCallingUid();
        int userId = UserHandle.getUserId(callingUid);
        String str3 = "sys.user." + userId + ".ce_available";
        if (!isUserKeyUnlocked(userId)) {
            throw new IllegalStateException("Failed to prepare " + str2);
        } else if (userId == 0 && !SystemProperties.getBoolean(str3, false)) {
            throw new IllegalStateException("Failed to prepare " + str2);
        } else {
            ((AppOpsManager) this.mContext.getSystemService("appops")).checkPackage(callingUid, str);
            try {
                PackageManager.Property propertyAsUser = this.mContext.getPackageManager().getPropertyAsUser("android.internal.PROPERTY_NO_APP_DATA_STORAGE", str, null, userId);
                if (propertyAsUser != null && propertyAsUser.getBoolean()) {
                    throw new SecurityException(str + " should not have " + str2);
                }
            } catch (PackageManager.NameNotFoundException unused) {
            }
            try {
                File canonicalFile = new File(str2).getCanonicalFile();
                String absolutePath = canonicalFile.getAbsolutePath();
                if (!absolutePath.endsWith("/")) {
                    absolutePath = absolutePath + "/";
                }
                Matcher matcher = KNOWN_APP_DIR_PATHS.matcher(absolutePath);
                if (matcher.matches()) {
                    if (!matcher.group(3).equals(str)) {
                        throw new SecurityException("Invalid mkdirs path: " + canonicalFile + " does not contain calling package " + str);
                    } else if ((matcher.group(2) != null && !matcher.group(2).equals(Integer.toString(userId))) || (matcher.group(2) == null && userId != this.mCurrentUserId)) {
                        throw new SecurityException("Invalid mkdirs path: " + canonicalFile + " does not match calling user id " + userId);
                    } else {
                        try {
                            this.mVold.setupAppDir(absolutePath, callingUid);
                            return;
                        } catch (RemoteException e) {
                            throw new IllegalStateException("Failed to prepare " + absolutePath + ": " + e);
                        }
                    }
                }
                throw new SecurityException("Invalid mkdirs path: " + canonicalFile + " is not a known app path.");
            } catch (IOException e2) {
                throw new IllegalStateException("Failed to resolve " + str2 + ": " + e2);
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:59:0x0111, code lost:
        if (r13.getMountUserId() == r7) goto L46;
     */
    /* JADX WARN: Removed duplicated region for block: B:86:0x015b  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public StorageVolume[] getVolumeList(int i, String str, int i2) {
        boolean z;
        boolean z2;
        boolean z3;
        boolean z4;
        StorageVolume buildStorageVolume;
        ArraySet arraySet;
        int callingUid = Binder.getCallingUid();
        int userId = UserHandle.getUserId(callingUid);
        if (!isUidOwnerOfPackageOrSystem(str, callingUid)) {
            throw new SecurityException("callingPackage does not match UID");
        }
        if (userId != i) {
            this.mContext.enforceCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS", "Need INTERACT_ACROSS_USERS to get volumes for another user");
        }
        boolean z5 = (i2 & 256) != 0;
        boolean z6 = (i2 & 512) != 0;
        boolean z7 = (i2 & 1024) != 0;
        boolean z8 = (i2 & IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES) != 0;
        boolean z9 = (i2 & IInstalld.FLAG_USE_QUOTA) != 0;
        boolean isSameApp = UserHandle.isSameApp(callingUid, this.mMediaStoreAuthorityAppId);
        if (z9) {
            try {
                String[] packagesForUid = this.mIPackageManager.getPackagesForUid(callingUid);
                if (packagesForUid == null) {
                    throw new SecurityException("Unknown uid " + callingUid);
                } else if (!isSameApp && !this.mStorageManagerInternal.hasExternalStorageAccess(callingUid, packagesForUid[0])) {
                    throw new SecurityException("Only File Manager Apps permitted");
                }
            } catch (RemoteException e) {
                throw new SecurityException("Unknown uid " + callingUid, e);
            }
        }
        boolean isSystemUnlocked = isSystemUnlocked(0);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            boolean isDemo = ((UserManagerInternal) LocalServices.getService(UserManagerInternal.class)).getUserInfo(i).isDemo();
            boolean hasExternalStorage = this.mStorageManagerInternal.hasExternalStorage(callingUid, str);
            boolean isUserKeyUnlocked = isUserKeyUnlocked(i);
            Binder.restoreCallingIdentity(clearCallingIdentity);
            ArrayList arrayList = new ArrayList();
            ArraySet arraySet2 = new ArraySet();
            int i3 = this.mUserSharesMediaWith.get(i, -1);
            synchronized (this.mLock) {
                boolean z10 = z8;
                int i4 = 0;
                z = false;
                while (i4 < this.mVolumes.size()) {
                    String keyAt = this.mVolumes.keyAt(i4);
                    ArraySet arraySet3 = arraySet2;
                    VolumeInfo valueAt = this.mVolumes.valueAt(i4);
                    int i5 = i4;
                    int type = valueAt.getType();
                    ArrayList arrayList2 = arrayList;
                    if (type != 0) {
                        if (type != 2) {
                            if (type != 5) {
                                arraySet = arraySet3;
                                z3 = z9;
                                arrayList = arrayList2;
                                i4 = i5 + 1;
                                arraySet2 = arraySet;
                                z9 = z3;
                            }
                        } else if (valueAt.getMountUserId() != i) {
                            if (z9) {
                            }
                            arraySet = arraySet3;
                            z3 = z9;
                            arrayList = arrayList2;
                            i4 = i5 + 1;
                            arraySet2 = arraySet;
                            z9 = z3;
                        }
                    }
                    if (!z5 ? !(valueAt.isVisibleForUser(i) || ((!valueAt.isVisible() && z7 && valueAt.getPath() != null) || ((valueAt.getType() == 0 && valueAt.isVisibleForUser(i3)) || (z9 && valueAt.isVisibleForUser(i3))))) : !(valueAt.isVisibleForWrite(i) || (z9 && valueAt.isVisibleForWrite(i3)))) {
                        z2 = false;
                        if (z2) {
                            if (isSameApp) {
                                z3 = z9;
                            } else {
                                if (!isSystemUnlocked) {
                                    StringBuilder sb = new StringBuilder();
                                    z3 = z9;
                                    sb.append("Reporting ");
                                    sb.append(keyAt);
                                    sb.append(" unmounted due to system locked");
                                    Slog.w("StorageManagerService", sb.toString());
                                } else {
                                    z3 = z9;
                                    if (valueAt.getType() == 2 && !isUserKeyUnlocked) {
                                        Slog.w("StorageManagerService", "Reporting " + keyAt + "unmounted due to " + i + " locked");
                                    } else if (!hasExternalStorage && !z6) {
                                        Slog.w("StorageManagerService", "Reporting " + keyAt + "unmounted due to missing permissions");
                                    }
                                }
                                z4 = true;
                                buildStorageVolume = valueAt.buildStorageVolume(this.mContext, (i != valueAt.getMountUserId() || valueAt.getMountUserId() < 0) ? i : valueAt.getMountUserId(), z4);
                                if (!valueAt.isPrimary() && valueAt.getMountUserId() == i) {
                                    arrayList = arrayList2;
                                    arrayList.add(0, buildStorageVolume);
                                    z = true;
                                } else {
                                    arrayList = arrayList2;
                                    arrayList.add(buildStorageVolume);
                                }
                                arraySet = arraySet3;
                                arraySet.add(buildStorageVolume.getUuid());
                                i4 = i5 + 1;
                                arraySet2 = arraySet;
                                z9 = z3;
                            }
                            z4 = false;
                            buildStorageVolume = valueAt.buildStorageVolume(this.mContext, (i != valueAt.getMountUserId() || valueAt.getMountUserId() < 0) ? i : valueAt.getMountUserId(), z4);
                            if (!valueAt.isPrimary()) {
                            }
                            arrayList = arrayList2;
                            arrayList.add(buildStorageVolume);
                            arraySet = arraySet3;
                            arraySet.add(buildStorageVolume.getUuid());
                            i4 = i5 + 1;
                            arraySet2 = arraySet;
                            z9 = z3;
                        }
                        arraySet = arraySet3;
                        z3 = z9;
                        arrayList = arrayList2;
                        i4 = i5 + 1;
                        arraySet2 = arraySet;
                        z9 = z3;
                    }
                    z2 = true;
                    if (z2) {
                    }
                    arraySet = arraySet3;
                    z3 = z9;
                    arrayList = arrayList2;
                    i4 = i5 + 1;
                    arraySet2 = arraySet;
                    z9 = z3;
                }
                ArraySet arraySet4 = arraySet2;
                if (z10) {
                    long currentTimeMillis = System.currentTimeMillis() - 604800000;
                    for (int i6 = 0; i6 < this.mRecords.size(); i6++) {
                        VolumeRecord valueAt2 = this.mRecords.valueAt(i6);
                        if (!arraySet4.contains(valueAt2.fsUuid)) {
                            long j = valueAt2.lastSeenMillis;
                            if (j > 0 && j < currentTimeMillis) {
                                StorageVolume buildStorageVolume2 = valueAt2.buildStorageVolume(this.mContext);
                                arrayList.add(buildStorageVolume2);
                                arraySet4.add(buildStorageVolume2.getUuid());
                            }
                        }
                    }
                }
            }
            if (isDemo) {
                File dataPreloadsMediaDirectory = Environment.getDataPreloadsMediaDirectory();
                arrayList.add(new StorageVolume("demo", dataPreloadsMediaDirectory, dataPreloadsMediaDirectory, this.mContext.getString(17039374), false, false, true, false, false, 0L, new UserHandle(i), null, "demo", "mounted_ro"));
            }
            if (!z) {
                Slog.w("StorageManagerService", "No primary storage defined yet; hacking together a stub");
                boolean z11 = SystemProperties.getBoolean("ro.vold.primary_physical", false);
                File legacyExternalStorageDirectory = Environment.getLegacyExternalStorageDirectory();
                arrayList.add(0, new StorageVolume("stub_primary", legacyExternalStorageDirectory, legacyExternalStorageDirectory, this.mContext.getString(17039374), true, z11, !z11, false, false, 0L, new UserHandle(i), null, null, "removed"));
            }
            return (StorageVolume[]) arrayList.toArray(new StorageVolume[arrayList.size()]);
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public DiskInfo[] getDisks() {
        DiskInfo[] diskInfoArr;
        synchronized (this.mLock) {
            diskInfoArr = new DiskInfo[this.mDisks.size()];
            for (int i = 0; i < this.mDisks.size(); i++) {
                diskInfoArr[i] = this.mDisks.valueAt(i);
            }
        }
        return diskInfoArr;
    }

    public VolumeInfo[] getVolumes(int i) {
        VolumeInfo[] volumeInfoArr;
        synchronized (this.mLock) {
            volumeInfoArr = new VolumeInfo[this.mVolumes.size()];
            for (int i2 = 0; i2 < this.mVolumes.size(); i2++) {
                volumeInfoArr[i2] = this.mVolumes.valueAt(i2);
            }
        }
        return volumeInfoArr;
    }

    public VolumeRecord[] getVolumeRecords(int i) {
        VolumeRecord[] volumeRecordArr;
        synchronized (this.mLock) {
            volumeRecordArr = new VolumeRecord[this.mRecords.size()];
            for (int i2 = 0; i2 < this.mRecords.size(); i2++) {
                volumeRecordArr[i2] = this.mRecords.valueAt(i2);
            }
        }
        return volumeRecordArr;
    }

    public long getCacheQuotaBytes(String str, int i) {
        if (i != Binder.getCallingUid()) {
            this.mContext.enforceCallingPermission("android.permission.STORAGE_INTERNAL", "StorageManagerService");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return ((StorageStatsManager) this.mContext.getSystemService(StorageStatsManager.class)).getCacheQuotaBytes(str, i);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public long getCacheSizeBytes(String str, int i) {
        if (i != Binder.getCallingUid()) {
            this.mContext.enforceCallingPermission("android.permission.STORAGE_INTERNAL", "StorageManagerService");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                return ((StorageStatsManager) this.mContext.getSystemService(StorageStatsManager.class)).queryStatsForUid(str, i).getCacheBytes();
            } catch (IOException e) {
                throw new ParcelableException(e);
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final int adjustAllocateFlags(int i, int i2, String str) {
        if ((i & 1) != 0) {
            this.mContext.enforceCallingOrSelfPermission("android.permission.ALLOCATE_AGGRESSIVE", "StorageManagerService");
        }
        int i3 = i & (-3) & (-5);
        AppOpsManager appOpsManager = (AppOpsManager) this.mContext.getSystemService(AppOpsManager.class);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            if (appOpsManager.isOperationActive(26, i2, str)) {
                Slog.d("StorageManagerService", "UID " + i2 + " is actively using camera; letting them defy reserved cached data");
                i3 |= 4;
            }
            return i3;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public long getAllocatableBytes(String str, int i, String str2) {
        long j;
        long j2;
        long j3;
        int adjustAllocateFlags = adjustAllocateFlags(i, Binder.getCallingUid(), str2);
        StorageManager storageManager = (StorageManager) this.mContext.getSystemService(StorageManager.class);
        StorageStatsManager storageStatsManager = (StorageStatsManager) this.mContext.getSystemService(StorageStatsManager.class);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                File findPathForUuid = storageManager.findPathForUuid(str);
                if ((adjustAllocateFlags & 16) == 0) {
                    j = findPathForUuid.getUsableSpace();
                    j2 = storageManager.getStorageLowBytes(findPathForUuid);
                    j3 = storageManager.getStorageFullBytes(findPathForUuid);
                } else {
                    j = 0;
                    j2 = 0;
                    j3 = 0;
                }
                long max = ((adjustAllocateFlags & 8) == 0 && storageStatsManager.isQuotaSupported(str)) ? Math.max(0L, storageStatsManager.getCacheBytes(str) - storageManager.getStorageCacheBytes(findPathForUuid, adjustAllocateFlags)) : 0L;
                if ((adjustAllocateFlags & 1) != 0) {
                    return Math.max(0L, (j + max) - j3);
                }
                return Math.max(0L, (j + max) - j2);
            } catch (IOException e) {
                throw new ParcelableException(e);
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void allocateBytes(String str, long j, int i, String str2) {
        long storageLowBytes;
        int adjustAllocateFlags = adjustAllocateFlags(i, Binder.getCallingUid(), str2);
        long allocatableBytes = getAllocatableBytes(str, adjustAllocateFlags | 8, str2);
        if (j > allocatableBytes) {
            long allocatableBytes2 = allocatableBytes + getAllocatableBytes(str, adjustAllocateFlags | 16, str2);
            if (j > allocatableBytes2) {
                throw new ParcelableException(new IOException("Failed to allocate " + j + " because only " + allocatableBytes2 + " allocatable"));
            }
        }
        StorageManager storageManager = (StorageManager) this.mContext.getSystemService(StorageManager.class);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                File findPathForUuid = storageManager.findPathForUuid(str);
                if ((adjustAllocateFlags & 1) != 0) {
                    storageLowBytes = storageManager.getStorageFullBytes(findPathForUuid);
                } else {
                    storageLowBytes = storageManager.getStorageLowBytes(findPathForUuid);
                }
                this.mPmInternal.freeStorage(str, j + storageLowBytes, adjustAllocateFlags);
            } catch (IOException e) {
                throw new ParcelableException(e);
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final void addObbStateLocked(ObbState obbState) throws RemoteException {
        IBinder binder = obbState.getBinder();
        List<ObbState> list = this.mObbMounts.get(binder);
        if (list == null) {
            list = new ArrayList<>();
            this.mObbMounts.put(binder, list);
        } else {
            for (ObbState obbState2 : list) {
                if (obbState2.rawPath.equals(obbState.rawPath)) {
                    throw new IllegalStateException("Attempt to add ObbState twice. This indicates an error in the StorageManagerService logic.");
                }
            }
        }
        list.add(obbState);
        try {
            obbState.link();
            this.mObbPathToStateMap.put(obbState.rawPath, obbState);
        } catch (RemoteException e) {
            list.remove(obbState);
            if (list.isEmpty()) {
                this.mObbMounts.remove(binder);
            }
            throw e;
        }
    }

    public final void removeObbStateLocked(ObbState obbState) {
        IBinder binder = obbState.getBinder();
        List<ObbState> list = this.mObbMounts.get(binder);
        if (list != null) {
            if (list.remove(obbState)) {
                obbState.unlink();
            }
            if (list.isEmpty()) {
                this.mObbMounts.remove(binder);
            }
        }
        this.mObbPathToStateMap.remove(obbState.rawPath);
    }

    /* loaded from: classes.dex */
    public class ObbActionHandler extends Handler {
        public ObbActionHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                ((ObbAction) message.obj).execute(this);
            } else if (i == 2) {
                String str = (String) message.obj;
                synchronized (StorageManagerService.this.mObbMounts) {
                    ArrayList<ObbState> arrayList = new ArrayList();
                    for (ObbState obbState : StorageManagerService.this.mObbPathToStateMap.values()) {
                        if (obbState.canonicalPath.startsWith(str)) {
                            arrayList.add(obbState);
                        }
                    }
                    for (ObbState obbState2 : arrayList) {
                        StorageManagerService.this.removeObbStateLocked(obbState2);
                        try {
                            obbState2.token.onObbResult(obbState2.rawPath, obbState2.nonce, 2);
                        } catch (RemoteException unused) {
                            Slog.i("StorageManagerService", "Couldn't send unmount notification for  OBB: " + obbState2.rawPath);
                        }
                    }
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public static class ObbException extends Exception {
        public final int status;

        public ObbException(int i, String str) {
            super(str);
            this.status = i;
        }

        public ObbException(int i, Throwable th) {
            super(th.getMessage(), th);
            this.status = i;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class ObbAction {
        public ObbState mObbState;

        public abstract void handleExecute() throws ObbException;

        public ObbAction(ObbState obbState) {
            this.mObbState = obbState;
        }

        public void execute(ObbActionHandler obbActionHandler) {
            try {
                handleExecute();
            } catch (ObbException e) {
                notifyObbStateChange(e);
            }
        }

        public void notifyObbStateChange(ObbException obbException) {
            Slog.w("StorageManagerService", obbException);
            notifyObbStateChange(obbException.status);
        }

        public void notifyObbStateChange(int i) {
            IObbActionListener iObbActionListener;
            ObbState obbState = this.mObbState;
            if (obbState == null || (iObbActionListener = obbState.token) == null) {
                return;
            }
            try {
                iObbActionListener.onObbResult(obbState.rawPath, obbState.nonce, i);
            } catch (RemoteException unused) {
                Slog.w("StorageManagerService", "StorageEventListener went away while calling onObbStateChanged");
            }
        }
    }

    /* loaded from: classes.dex */
    public class MountObbAction extends ObbAction {
        public final int mCallingUid;
        public ObbInfo mObbInfo;

        public MountObbAction(ObbState obbState, int i, ObbInfo obbInfo) {
            super(obbState);
            this.mCallingUid = i;
            this.mObbInfo = obbInfo;
        }

        @Override // com.android.server.StorageManagerService.ObbAction
        public void handleExecute() throws ObbException {
            boolean containsKey;
            StorageManagerService.this.warnOnNotMounted();
            if (!StorageManagerService.this.isUidOwnerOfPackageOrSystem(this.mObbInfo.packageName, this.mCallingUid)) {
                throw new ObbException(25, "Denied attempt to mount OBB " + this.mObbInfo.filename + " which is owned by " + this.mObbInfo.packageName);
            }
            synchronized (StorageManagerService.this.mObbMounts) {
                containsKey = StorageManagerService.this.mObbPathToStateMap.containsKey(this.mObbState.rawPath);
            }
            if (containsKey) {
                throw new ObbException(24, "Attempt to mount OBB which is already mounted: " + this.mObbInfo.filename);
            }
            try {
                ObbState obbState = this.mObbState;
                IVold iVold = StorageManagerService.this.mVold;
                ObbState obbState2 = this.mObbState;
                obbState.volId = iVold.createObb(obbState2.canonicalPath, obbState2.ownerGid);
                StorageManagerService.this.mVold.mount(this.mObbState.volId, 0, -1, null);
                synchronized (StorageManagerService.this.mObbMounts) {
                    StorageManagerService.this.addObbStateLocked(this.mObbState);
                }
                notifyObbStateChange(1);
            } catch (Exception e) {
                throw new ObbException(21, e);
            }
        }

        public String toString() {
            return "MountObbAction{" + this.mObbState + '}';
        }
    }

    /* loaded from: classes.dex */
    public class UnmountObbAction extends ObbAction {
        public final boolean mForceUnmount;

        public UnmountObbAction(ObbState obbState, boolean z) {
            super(obbState);
            this.mForceUnmount = z;
        }

        @Override // com.android.server.StorageManagerService.ObbAction
        public void handleExecute() throws ObbException {
            ObbState obbState;
            StorageManagerService.this.warnOnNotMounted();
            synchronized (StorageManagerService.this.mObbMounts) {
                obbState = (ObbState) StorageManagerService.this.mObbPathToStateMap.get(this.mObbState.rawPath);
            }
            if (obbState == null) {
                throw new ObbException(23, "Missing existingState");
            }
            if (obbState.ownerGid != this.mObbState.ownerGid) {
                notifyObbStateChange(new ObbException(25, "Permission denied to unmount OBB " + obbState.rawPath + " (owned by GID " + obbState.ownerGid + ")"));
                return;
            }
            try {
                StorageManagerService.this.mVold.unmount(this.mObbState.volId);
                StorageManagerService.this.mVold.destroyObb(this.mObbState.volId);
                this.mObbState.volId = null;
                synchronized (StorageManagerService.this.mObbMounts) {
                    StorageManagerService.this.removeObbStateLocked(obbState);
                }
                notifyObbStateChange(2);
            } catch (Exception e) {
                throw new ObbException(22, e);
            }
        }

        public String toString() {
            return "UnmountObbAction{" + this.mObbState + ",force=" + this.mForceUnmount + '}';
        }
    }

    public final void dispatchOnStatus(IVoldTaskListener iVoldTaskListener, int i, PersistableBundle persistableBundle) {
        if (iVoldTaskListener != null) {
            try {
                iVoldTaskListener.onStatus(i, persistableBundle);
            } catch (RemoteException unused) {
            }
        }
    }

    public final void dispatchOnFinished(IVoldTaskListener iVoldTaskListener, int i, PersistableBundle persistableBundle) {
        if (iVoldTaskListener != null) {
            try {
                iVoldTaskListener.onFinished(i, persistableBundle);
            } catch (RemoteException unused) {
            }
        }
    }

    @EnforcePermission("android.permission.WRITE_MEDIA_STORAGE")
    public int getExternalStorageMountMode(int i, String str) {
        super.getExternalStorageMountMode_enforcePermission();
        return this.mStorageManagerInternal.getExternalStorageMountMode(i, str);
    }

    public final int getMountModeInternal(int i, String str) {
        ApplicationInfo applicationInfo;
        boolean z = false;
        try {
            if (!Process.isIsolated(i) && !Process.isSdkSandboxUid(i)) {
                String[] packagesForUid = this.mIPackageManager.getPackagesForUid(i);
                if (ArrayUtils.isEmpty(packagesForUid)) {
                    return 0;
                }
                if (str == null) {
                    str = packagesForUid[0];
                }
                long clearCallingIdentity = Binder.clearCallingIdentity();
                if (this.mPmInternal.isInstantApp(str, UserHandle.getUserId(i))) {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    return 0;
                }
                Binder.restoreCallingIdentity(clearCallingIdentity);
                if (this.mStorageManagerInternal.isExternalStorageService(i)) {
                    return 3;
                }
                if (this.mDownloadsAuthorityAppId != UserHandle.getAppId(i) && this.mExternalStorageAuthorityAppId != UserHandle.getAppId(i)) {
                    if ((this.mIPackageManager.checkUidPermission("android.permission.ACCESS_MTP", i) == 0) && (applicationInfo = this.mIPackageManager.getApplicationInfo(str, 0L, UserHandle.getUserId(i))) != null && applicationInfo.isSignedWithPlatformKey()) {
                        return 4;
                    }
                    boolean z2 = this.mIPackageManager.checkUidPermission("android.permission.INSTALL_PACKAGES", i) == 0;
                    int length = packagesForUid.length;
                    int i2 = 0;
                    while (true) {
                        if (i2 >= length) {
                            break;
                        } else if (this.mIAppOpsService.checkOperation(66, i, packagesForUid[i2]) == 0) {
                            z = true;
                            break;
                        } else {
                            i2++;
                        }
                    }
                    return (z2 || z) ? 2 : 1;
                }
                return 4;
            }
        } catch (RemoteException unused) {
        }
        return 0;
    }

    /* loaded from: classes.dex */
    public static class Callbacks extends Handler {
        public final RemoteCallbackList<IStorageEventListener> mCallbacks;

        public Callbacks(Looper looper) {
            super(looper);
            this.mCallbacks = new RemoteCallbackList<>();
        }

        public void register(IStorageEventListener iStorageEventListener) {
            this.mCallbacks.register(iStorageEventListener);
        }

        public void unregister(IStorageEventListener iStorageEventListener) {
            this.mCallbacks.unregister(iStorageEventListener);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            SomeArgs someArgs = (SomeArgs) message.obj;
            int beginBroadcast = this.mCallbacks.beginBroadcast();
            for (int i = 0; i < beginBroadcast; i++) {
                try {
                    invokeCallback(this.mCallbacks.getBroadcastItem(i), message.what, someArgs);
                } catch (RemoteException unused) {
                }
            }
            this.mCallbacks.finishBroadcast();
            someArgs.recycle();
        }

        public final void invokeCallback(IStorageEventListener iStorageEventListener, int i, SomeArgs someArgs) throws RemoteException {
            switch (i) {
                case 1:
                    iStorageEventListener.onStorageStateChanged((String) someArgs.arg1, (String) someArgs.arg2, (String) someArgs.arg3);
                    return;
                case 2:
                    iStorageEventListener.onVolumeStateChanged((VolumeInfo) someArgs.arg1, someArgs.argi2, someArgs.argi3);
                    return;
                case 3:
                    iStorageEventListener.onVolumeRecordChanged((VolumeRecord) someArgs.arg1);
                    return;
                case 4:
                    iStorageEventListener.onVolumeForgotten((String) someArgs.arg1);
                    return;
                case 5:
                    iStorageEventListener.onDiskScanned((DiskInfo) someArgs.arg1, someArgs.argi2);
                    return;
                case 6:
                    iStorageEventListener.onDiskDestroyed((DiskInfo) someArgs.arg1);
                    return;
                default:
                    return;
            }
        }

        public final void notifyStorageStateChanged(String str, String str2, String str3) {
            SomeArgs obtain = SomeArgs.obtain();
            obtain.arg1 = str;
            obtain.arg2 = str2;
            obtain.arg3 = str3;
            obtainMessage(1, obtain).sendToTarget();
        }

        public final void notifyVolumeStateChanged(VolumeInfo volumeInfo, int i, int i2) {
            SomeArgs obtain = SomeArgs.obtain();
            obtain.arg1 = volumeInfo.clone();
            obtain.argi2 = i;
            obtain.argi3 = i2;
            obtainMessage(2, obtain).sendToTarget();
        }

        public final void notifyVolumeRecordChanged(VolumeRecord volumeRecord) {
            SomeArgs obtain = SomeArgs.obtain();
            obtain.arg1 = volumeRecord.clone();
            obtainMessage(3, obtain).sendToTarget();
        }

        public final void notifyVolumeForgotten(String str) {
            SomeArgs obtain = SomeArgs.obtain();
            obtain.arg1 = str;
            obtainMessage(4, obtain).sendToTarget();
        }

        public final void notifyDiskScanned(DiskInfo diskInfo, int i) {
            SomeArgs obtain = SomeArgs.obtain();
            obtain.arg1 = diskInfo.clone();
            obtain.argi2 = i;
            obtainMessage(5, obtain).sendToTarget();
        }

        public final void notifyDiskDestroyed(DiskInfo diskInfo) {
            SomeArgs obtain = SomeArgs.obtain();
            obtain.arg1 = diskInfo.clone();
            obtainMessage(6, obtain).sendToTarget();
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (DumpUtils.checkDumpPermission(this.mContext, "StorageManagerService", printWriter)) {
            IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ", (int) FrameworkStatsLog.f418x97ec91aa);
            synchronized (this.mLock) {
                indentingPrintWriter.println("Disks:");
                indentingPrintWriter.increaseIndent();
                for (int i = 0; i < this.mDisks.size(); i++) {
                    this.mDisks.valueAt(i).dump(indentingPrintWriter);
                }
                indentingPrintWriter.decreaseIndent();
                indentingPrintWriter.println();
                indentingPrintWriter.println("Volumes:");
                indentingPrintWriter.increaseIndent();
                for (int i2 = 0; i2 < this.mVolumes.size(); i2++) {
                    VolumeInfo valueAt = this.mVolumes.valueAt(i2);
                    if (!"private".equals(valueAt.id)) {
                        valueAt.dump(indentingPrintWriter);
                    }
                }
                indentingPrintWriter.decreaseIndent();
                indentingPrintWriter.println();
                indentingPrintWriter.println("Records:");
                indentingPrintWriter.increaseIndent();
                for (int i3 = 0; i3 < this.mRecords.size(); i3++) {
                    this.mRecords.valueAt(i3).dump(indentingPrintWriter);
                }
                indentingPrintWriter.decreaseIndent();
                indentingPrintWriter.println();
                indentingPrintWriter.println("Primary storage UUID: " + this.mPrimaryStorageUuid);
                indentingPrintWriter.println();
                Pair primaryStoragePathAndSize = StorageManager.getPrimaryStoragePathAndSize();
                if (primaryStoragePathAndSize == null) {
                    indentingPrintWriter.println("Internal storage total size: N/A");
                } else {
                    indentingPrintWriter.print("Internal storage (");
                    indentingPrintWriter.print((String) primaryStoragePathAndSize.first);
                    indentingPrintWriter.print(") total size: ");
                    indentingPrintWriter.print(primaryStoragePathAndSize.second);
                    indentingPrintWriter.print(" (");
                    indentingPrintWriter.print(DataUnit.MEBIBYTES.toBytes(((Long) primaryStoragePathAndSize.second).longValue()));
                    indentingPrintWriter.println(" MiB)");
                }
                indentingPrintWriter.println();
                indentingPrintWriter.println("Local unlocked users: " + this.mLocalUnlockedUsers);
                indentingPrintWriter.println("System unlocked users: " + Arrays.toString(this.mSystemUnlockedUsers));
            }
            synchronized (this.mObbMounts) {
                indentingPrintWriter.println();
                indentingPrintWriter.println("mObbMounts:");
                indentingPrintWriter.increaseIndent();
                for (Map.Entry<IBinder, List<ObbState>> entry : this.mObbMounts.entrySet()) {
                    indentingPrintWriter.println(entry.getKey() + com.android.internal.util.jobs.XmlUtils.STRING_ARRAY_SEPARATOR);
                    indentingPrintWriter.increaseIndent();
                    for (ObbState obbState : entry.getValue()) {
                        indentingPrintWriter.println(obbState);
                    }
                    indentingPrintWriter.decreaseIndent();
                }
                indentingPrintWriter.decreaseIndent();
                indentingPrintWriter.println();
                indentingPrintWriter.println("mObbPathToStateMap:");
                indentingPrintWriter.increaseIndent();
                for (Map.Entry<String, ObbState> entry2 : this.mObbPathToStateMap.entrySet()) {
                    indentingPrintWriter.print(entry2.getKey());
                    indentingPrintWriter.print(" -> ");
                    indentingPrintWriter.println(entry2.getValue());
                }
                indentingPrintWriter.decreaseIndent();
            }
            synchronized (this.mCloudMediaProviders) {
                indentingPrintWriter.println();
                indentingPrintWriter.print("Media cloud providers: ");
                indentingPrintWriter.println(this.mCloudMediaProviders);
            }
            indentingPrintWriter.println();
            indentingPrintWriter.print("Last maintenance: ");
            indentingPrintWriter.println(TimeUtils.formatForLogging(this.mLastMaintenance));
        }
    }

    @Override // com.android.server.Watchdog.Monitor
    public void monitor() {
        try {
            this.mVold.monitor();
        } catch (Exception e) {
            Slog.wtf("StorageManagerService", e);
        }
    }

    /* loaded from: classes.dex */
    public final class StorageManagerInternalImpl extends StorageManagerInternal {
        public final CopyOnWriteArraySet<StorageManagerInternal.CloudProviderChangeListener> mCloudProviderChangeListeners;
        @GuardedBy({"mResetListeners"})
        public final List<StorageManagerInternal.ResetListener> mResetListeners;

        public StorageManagerInternalImpl() {
            this.mResetListeners = new ArrayList();
            this.mCloudProviderChangeListeners = new CopyOnWriteArraySet<>();
        }

        public boolean isFuseMounted(int i) {
            boolean contains;
            synchronized (StorageManagerService.this.mLock) {
                contains = StorageManagerService.this.mFuseMountedUser.contains(Integer.valueOf(i));
            }
            return contains;
        }

        public boolean prepareStorageDirs(int i, Set<String> set, String str) {
            synchronized (StorageManagerService.this.mLock) {
                if (!StorageManagerService.this.mFuseMountedUser.contains(Integer.valueOf(i))) {
                    Slog.w("StorageManagerService", "User " + i + " is not unlocked yet so skip mounting obb");
                    return false;
                }
                try {
                    IVold asInterface = IVold.Stub.asInterface(ServiceManager.getServiceOrThrow("vold"));
                    for (String str2 : set) {
                        Locale locale = Locale.US;
                        asInterface.ensureAppDirsCreated(new String[]{String.format(locale, "/storage/emulated/%d/Android/obb/%s/", Integer.valueOf(i), str2), String.format(locale, "/storage/emulated/%d/Android/data/%s/", Integer.valueOf(i), str2)}, UserHandle.getUid(i, StorageManagerService.this.mPmInternal.getPackage(str2).getUid()));
                    }
                    return true;
                } catch (ServiceManager.ServiceNotFoundException | RemoteException e) {
                    Slog.e("StorageManagerService", "Unable to create obb and data directories for " + str, e);
                    return false;
                }
            }
        }

        public int getExternalStorageMountMode(int i, String str) {
            int mountModeInternal = StorageManagerService.this.getMountModeInternal(i, str);
            if (StorageManagerService.LOCAL_LOGV) {
                Slog.v("StorageManagerService", "Resolved mode " + mountModeInternal + " for " + str + "/" + UserHandle.formatUid(i));
            }
            return mountModeInternal;
        }

        public boolean hasExternalStorageAccess(int i, String str) {
            try {
                int checkOperation = StorageManagerService.this.mIAppOpsService.checkOperation(92, i, str);
                return checkOperation == 3 ? StorageManagerService.this.mIPackageManager.checkUidPermission("android.permission.MANAGE_EXTERNAL_STORAGE", i) == 0 : checkOperation == 0;
            } catch (RemoteException e) {
                Slog.w("Failed to check MANAGE_EXTERNAL_STORAGE access for " + str, e);
                return false;
            }
        }

        public void addResetListener(StorageManagerInternal.ResetListener resetListener) {
            synchronized (this.mResetListeners) {
                this.mResetListeners.add(resetListener);
            }
        }

        public void onReset(IVold iVold) {
            synchronized (this.mResetListeners) {
                for (StorageManagerInternal.ResetListener resetListener : this.mResetListeners) {
                    resetListener.onReset(iVold);
                }
            }
        }

        public void resetUser(int i) {
            StorageManagerService.this.mHandler.obtainMessage(10).sendToTarget();
        }

        public boolean hasLegacyExternalStorage(int i) {
            boolean contains;
            synchronized (StorageManagerService.this.mLock) {
                contains = StorageManagerService.this.mUidsWithLegacyExternalStorage.contains(Integer.valueOf(i));
            }
            return contains;
        }

        public void prepareAppDataAfterInstall(String str, int i) {
            File[] buildExternalStorageAppObbDirs;
            for (File file : new Environment.UserEnvironment(UserHandle.getUserId(i)).buildExternalStorageAppObbDirs(str)) {
                if (file.getPath().startsWith(Environment.getDataPreloadsMediaDirectory().getPath())) {
                    Slog.i("StorageManagerService", "Skipping app data preparation for " + file);
                } else {
                    try {
                        StorageManagerService.this.mVold.fixupAppDir(file.getCanonicalPath() + "/", i);
                    } catch (RemoteException | ServiceSpecificException e) {
                        Log.e("StorageManagerService", "Failed to fixup app dir for " + str, e);
                    } catch (IOException unused) {
                        Log.e("StorageManagerService", "Failed to get canonical path for " + str);
                    }
                }
            }
        }

        public boolean isExternalStorageService(int i) {
            return StorageManagerService.this.mMediaStoreAuthorityAppId == UserHandle.getAppId(i);
        }

        public void freeCache(String str, long j) {
            try {
                StorageManagerService.this.mStorageSessionController.freeCache(str, j);
            } catch (StorageSessionController.ExternalStorageServiceException e) {
                Log.e("StorageManagerService", "Failed to free cache of vol : " + str, e);
            }
        }

        public boolean hasExternalStorage(int i, String str) {
            return i == 1000 || getExternalStorageMountMode(i, str) != 0;
        }

        public final void killAppForOpChange(int i, int i2) {
            IActivityManager service = ActivityManager.getService();
            try {
                int appId = UserHandle.getAppId(i2);
                service.killUid(appId, -1, AppOpsManager.opToName(i) + " changed.");
            } catch (RemoteException unused) {
            }
        }

        public void onAppOpsChanged(int i, int i2, String str, int i3, int i4) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                if (i == 66) {
                    if (i4 == 0 && i3 != 0) {
                        killAppForOpChange(i, i2);
                    }
                } else if (i == 87) {
                    StorageManagerService.this.updateLegacyStorageApps(str, i2, i3 == 0);
                } else if (i != 92) {
                } else {
                    if (i3 != 0) {
                        killAppForOpChange(i, i2);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public List<String> getPrimaryVolumeIds() {
            ArrayList arrayList = new ArrayList();
            synchronized (StorageManagerService.this.mLock) {
                for (int i = 0; i < StorageManagerService.this.mVolumes.size(); i++) {
                    VolumeInfo volumeInfo = (VolumeInfo) StorageManagerService.this.mVolumes.valueAt(i);
                    if (volumeInfo.isPrimary()) {
                        arrayList.add(volumeInfo.getId());
                    }
                }
            }
            return arrayList;
        }

        public void markCeStoragePrepared(int i) {
            synchronized (StorageManagerService.this.mLock) {
                StorageManagerService.this.mCeStoragePreparedUsers.add(Integer.valueOf(i));
            }
        }

        public boolean isCeStoragePrepared(int i) {
            boolean contains;
            synchronized (StorageManagerService.this.mLock) {
                contains = StorageManagerService.this.mCeStoragePreparedUsers.contains(Integer.valueOf(i));
            }
            return contains;
        }

        public void registerCloudProviderChangeListener(StorageManagerInternal.CloudProviderChangeListener cloudProviderChangeListener) {
            this.mCloudProviderChangeListeners.add(cloudProviderChangeListener);
            StorageManagerService.this.mHandler.obtainMessage(16, cloudProviderChangeListener).sendToTarget();
        }
    }
}
