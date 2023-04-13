package com.android.server.stats.pull;

import android.app.ActivityManagerInternal;
import android.app.AppOpsManager;
import android.app.INotificationManager;
import android.app.PendingIntentStats;
import android.app.ProcessMemoryState;
import android.app.RuntimeAppOpAccessMessage;
import android.app.StatsManager;
import android.app.usage.NetworkStatsManager;
import android.bluetooth.BluetoothActivityEnergyInfo;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.UidTraffic;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.UserInfo;
import android.hardware.display.DisplayManager;
import android.hardware.face.FaceManager;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.health.HealthInfo;
import android.icu.util.TimeZone;
import android.media.AudioManager;
import android.media.MediaDrm;
import android.media.UnsupportedSchemeException;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.net.NetworkStats;
import android.net.NetworkTemplate;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.BatteryStatsManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.CoolingDevice;
import android.os.Debug;
import android.os.Environment;
import android.os.IBinder;
import android.os.IThermalEventListener;
import android.os.IThermalService;
import android.os.OutcomeReceiver;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.ServiceSpecificException;
import android.os.StatFs;
import android.os.SynchronousResultReceiver;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Temperature;
import android.os.Trace;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.connectivity.WifiActivityEnergyInfo;
import android.os.incremental.IncrementalManager;
import android.os.storage.DiskInfo;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.p005os.BatteryStatsInternal;
import android.p005os.IInstalld;
import android.p005os.IStoraged;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.security.metrics.IKeystoreMetrics;
import android.security.metrics.KeyCreationWithAuthInfo;
import android.security.metrics.KeyCreationWithGeneralInfo;
import android.security.metrics.KeyCreationWithPurposeAndModesInfo;
import android.security.metrics.KeyOperationWithGeneralInfo;
import android.security.metrics.KeyOperationWithPurposeAndModesInfo;
import android.security.metrics.KeystoreAtom;
import android.security.metrics.RkpErrorStats;
import android.security.metrics.RkpPoolStats;
import android.security.metrics.StorageStats;
import android.telephony.ModemActivityInfo;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.util.MathUtils;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.StatsEvent;
import android.util.proto.ProtoOutputStream;
import android.view.Display;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.app.procstats.IProcessStats;
import com.android.internal.app.procstats.ProcessStats;
import com.android.internal.app.procstats.StatsEventOutput;
import com.android.internal.os.BackgroundThread;
import com.android.internal.os.BinderCallsStats;
import com.android.internal.os.KernelAllocationStats;
import com.android.internal.os.KernelCpuBpfTracking;
import com.android.internal.os.KernelCpuThreadReader;
import com.android.internal.os.KernelCpuThreadReaderDiff;
import com.android.internal.os.KernelCpuThreadReaderSettingsObserver;
import com.android.internal.os.KernelCpuTotalBpfMapReader;
import com.android.internal.os.KernelCpuUidTimeReader;
import com.android.internal.os.KernelSingleProcessCpuThreadReader;
import com.android.internal.os.LooperStats;
import com.android.internal.os.PowerProfile;
import com.android.internal.os.ProcessCpuTracker;
import com.android.internal.os.SelectedProcessCpuThreadReader;
import com.android.internal.os.StoragedUidIoStatsReader;
import com.android.internal.util.CollectionUtils;
import com.android.internal.util.ConcurrentUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.net.module.util.NetworkStatsUtils;
import com.android.role.RoleManagerLocal;
import com.android.server.BinderCallsStatsService;
import com.android.server.LocalManagerRegistry;
import com.android.server.LocalServices;
import com.android.server.PinnerService;
import com.android.server.SystemServerInitThreadPool$$ExternalSyntheticLambda0;
import com.android.server.SystemService;
import com.android.server.SystemServiceManager;
import com.android.server.backup.BackupAgentTimeoutParameters;
import com.android.server.health.HealthServiceWrapper;
import com.android.server.p006am.MemoryStatUtil;
import com.android.server.p011pm.UserManagerInternal;
import com.android.server.power.stats.KernelWakelockReader;
import com.android.server.power.stats.KernelWakelockStats;
import com.android.server.power.stats.SystemServerCpuThreadReader;
import com.android.server.stats.pull.IonMemoryUtil;
import com.android.server.stats.pull.ProcfsMemoryUtil;
import com.android.server.stats.pull.StatsPullAtomService;
import com.android.server.stats.pull.SystemMemoryUtil;
import com.android.server.stats.pull.netstats.NetworkStatsExt;
import com.android.server.stats.pull.netstats.SubInfo;
import com.android.server.timezonedetector.MetricsTimeZoneDetectorState;
import com.android.server.timezonedetector.TimeZoneDetectorInternal;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import libcore.io.IoUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes2.dex */
public class StatsPullAtomService extends SystemService {
    public final Object mAppOpsLock;
    @GuardedBy({"mAttributedAppOpsLock"})
    public int mAppOpsSamplingRate;
    public final Object mAppSizeLock;
    public final Object mAppsOnExternalStorageInfoLock;
    public final Object mAttributedAppOpsLock;
    @GuardedBy({"mProcStatsLock"})
    public File mBaseDir;
    public final Object mBinderCallsStatsExceptionsLock;
    public final Object mBinderCallsStatsLock;
    public final Object mBluetoothActivityInfoLock;
    public final Object mBluetoothBytesTransferLock;
    public final Object mBuildInformationLock;
    public final Object mCategorySizeLock;
    public final Context mContext;
    public final Object mCooldownDeviceLock;
    public final Object mCpuActiveTimeLock;
    public final Object mCpuClusterTimeLock;
    public final Object mCpuTimePerClusterFreqLock;
    public final Object mCpuTimePerThreadFreqLock;
    public final Object mCpuTimePerUidFreqLock;
    public final Object mCpuTimePerUidLock;
    @GuardedBy({"mCpuActiveTimeLock"})
    public KernelCpuUidTimeReader.KernelCpuUidActiveTimeReader mCpuUidActiveTimeReader;
    @GuardedBy({"mClusterTimeLock"})
    public KernelCpuUidTimeReader.KernelCpuUidClusterTimeReader mCpuUidClusterTimeReader;
    @GuardedBy({"mCpuTimePerUidFreqLock"})
    public KernelCpuUidTimeReader.KernelCpuUidFreqTimeReader mCpuUidFreqTimeReader;
    @GuardedBy({"mCpuTimePerUidLock"})
    public KernelCpuUidTimeReader.KernelCpuUidUserSysTimeReader mCpuUidUserSysTimeReader;
    @GuardedBy({"mDangerousAppOpsListLock"})
    public final ArraySet<Integer> mDangerousAppOpsList;
    public final Object mDangerousAppOpsListLock;
    public final Object mDangerousPermissionStateLock;
    public final Object mDataBytesTransferLock;
    public final Object mDebugElapsedClockLock;
    @GuardedBy({"mDebugElapsedClockLock"})
    public long mDebugElapsedClockPreviousValue;
    @GuardedBy({"mDebugElapsedClockLock"})
    public long mDebugElapsedClockPullCount;
    public final Object mDebugFailingElapsedClockLock;
    @GuardedBy({"mDebugFailingElapsedClockLock"})
    public long mDebugFailingElapsedClockPreviousValue;
    @GuardedBy({"mDebugFailingElapsedClockLock"})
    public long mDebugFailingElapsedClockPullCount;
    public final Object mDeviceCalculatedPowerUseLock;
    public final Object mDirectoryUsageLock;
    public final Object mDiskIoLock;
    public final Object mDiskStatsLock;
    public final Object mExternalStorageInfoLock;
    public final Object mFaceSettingsLock;
    public final Object mHealthHalLock;
    @GuardedBy({"mHealthHalLock"})
    public HealthServiceWrapper mHealthService;
    @GuardedBy({"mDataBytesTransferLock"})
    public final ArrayList<SubInfo> mHistoricalSubs;
    @GuardedBy({"mKeystoreLock"})
    public IKeystoreMetrics mIKeystoreMetrics;
    public final Object mInstalledIncrementalPackagesLock;
    public final Object mIonHeapSizeLock;
    @GuardedBy({"mCpuTimePerThreadFreqLock"})
    public KernelCpuThreadReaderDiff mKernelCpuThreadReader;
    public final Object mKernelWakelockLock;
    @GuardedBy({"mKernelWakelockLock"})
    public KernelWakelockReader mKernelWakelockReader;
    public final Object mKeystoreLock;
    public final Object mLooperStatsLock;
    public final Object mModemActivityInfoLock;
    @GuardedBy({"mDataBytesTransferLock"})
    public final ArrayList<NetworkStatsExt> mNetworkStatsBaselines;
    public NetworkStatsManager mNetworkStatsManager;
    @GuardedBy({"mNotificationStatsLock"})
    public INotificationManager mNotificationManagerService;
    public final Object mNotificationRemoteViewsLock;
    public final Object mNotificationStatsLock;
    public final Object mNumBiometricsEnrolledLock;
    public final Object mPowerProfileLock;
    public final Object mProcStatsLock;
    public final Object mProcessCpuTimeLock;
    @GuardedBy({"mProcessCpuTimeLock"})
    public ProcessCpuTracker mProcessCpuTracker;
    public final Object mProcessMemoryHighWaterMarkLock;
    public final Object mProcessMemoryStateLock;
    @GuardedBy({"mProcStatsLock"})
    public IProcessStats mProcessStatsService;
    public final Object mProcessSystemIonHeapSizeLock;
    public final Object mRoleHolderLock;
    public final Object mRuntimeAppOpAccessMessageLock;
    public final Object mSettingsStatsLock;
    public StatsPullAtomCallbackImpl mStatsCallbackImpl;
    public StatsManager mStatsManager;
    public StatsSubscriptionsListener mStatsSubscriptionsListener;
    public StorageManager mStorageManager;
    @GuardedBy({"mStoragedLock"})
    public IStoraged mStorageService;
    public final Object mStoragedLock;
    @GuardedBy({"mDiskIoLock"})
    public StoragedUidIoStatsReader mStoragedUidIoStatsReader;
    public SubscriptionManager mSubscriptionManager;
    public SelectedProcessCpuThreadReader mSurfaceFlingerProcessCpuThreadReader;
    public final Object mSystemElapsedRealtimeLock;
    public final Object mSystemIonHeapSizeLock;
    public final Object mSystemUptimeLock;
    public TelephonyManager mTelephony;
    public final Object mTemperatureLock;
    public final Object mThermalLock;
    @GuardedBy({"mThermalLock"})
    public IThermalService mThermalService;
    public final Object mTimeZoneDataInfoLock;
    public final Object mTimeZoneDetectionInfoLock;
    @GuardedBy({"mKernelWakelockLock"})
    public KernelWakelockStats mTmpWakelockStats;
    public final Object mWifiActivityInfoLock;
    public WifiManager mWifiManager;
    public static final int RANDOM_SEED = new Random().nextInt();
    public static final long NETSTATS_UID_DEFAULT_BUCKET_DURATION_MS = TimeUnit.HOURS.toMillis(2);

    public static int convertToMetricsDetectionMode(int i) {
        int i2 = 1;
        if (i != 1) {
            i2 = 3;
            if (i != 2) {
                return i != 3 ? 0 : 2;
            }
        }
        return i2;
    }

    private native void initializeNativePullers();

    public static /* synthetic */ boolean lambda$countAccessibilityServices$25(int i) {
        return i == 58;
    }

    public static /* synthetic */ boolean lambda$hasDolbyVisionIssue$22(int i) {
        return i == 1;
    }

    public final int convertToAccessibilityShortcutType(int i) {
        if (i != 0) {
            if (i != 1) {
                return i != 2 ? 0 : 6;
            }
            return 5;
        }
        return 1;
    }

    public final long milliAmpHrsToNanoAmpSecs(double d) {
        return (long) ((d * 3.6E9d) + 0.5d);
    }

    @Override // com.android.server.SystemService
    public void onStart() {
    }

    public StatsPullAtomService(Context context) {
        super(context);
        this.mThermalLock = new Object();
        this.mStoragedLock = new Object();
        this.mNotificationStatsLock = new Object();
        this.mDebugElapsedClockPreviousValue = 0L;
        this.mDebugElapsedClockPullCount = 0L;
        this.mDebugFailingElapsedClockPreviousValue = 0L;
        this.mDebugFailingElapsedClockPullCount = 0L;
        this.mAppOpsSamplingRate = 0;
        this.mDangerousAppOpsListLock = new Object();
        this.mDangerousAppOpsList = new ArraySet<>();
        this.mNetworkStatsBaselines = new ArrayList<>();
        this.mHistoricalSubs = new ArrayList<>();
        this.mDataBytesTransferLock = new Object();
        this.mBluetoothBytesTransferLock = new Object();
        this.mKernelWakelockLock = new Object();
        this.mCpuTimePerClusterFreqLock = new Object();
        this.mCpuTimePerUidLock = new Object();
        this.mCpuTimePerUidFreqLock = new Object();
        this.mCpuActiveTimeLock = new Object();
        this.mCpuClusterTimeLock = new Object();
        this.mWifiActivityInfoLock = new Object();
        this.mModemActivityInfoLock = new Object();
        this.mBluetoothActivityInfoLock = new Object();
        this.mSystemElapsedRealtimeLock = new Object();
        this.mSystemUptimeLock = new Object();
        this.mProcessMemoryStateLock = new Object();
        this.mProcessMemoryHighWaterMarkLock = new Object();
        this.mSystemIonHeapSizeLock = new Object();
        this.mIonHeapSizeLock = new Object();
        this.mProcessSystemIonHeapSizeLock = new Object();
        this.mTemperatureLock = new Object();
        this.mCooldownDeviceLock = new Object();
        this.mBinderCallsStatsLock = new Object();
        this.mBinderCallsStatsExceptionsLock = new Object();
        this.mLooperStatsLock = new Object();
        this.mDiskStatsLock = new Object();
        this.mDirectoryUsageLock = new Object();
        this.mAppSizeLock = new Object();
        this.mCategorySizeLock = new Object();
        this.mNumBiometricsEnrolledLock = new Object();
        this.mProcStatsLock = new Object();
        this.mDiskIoLock = new Object();
        this.mPowerProfileLock = new Object();
        this.mProcessCpuTimeLock = new Object();
        this.mCpuTimePerThreadFreqLock = new Object();
        this.mDeviceCalculatedPowerUseLock = new Object();
        this.mDebugElapsedClockLock = new Object();
        this.mDebugFailingElapsedClockLock = new Object();
        this.mBuildInformationLock = new Object();
        this.mRoleHolderLock = new Object();
        this.mTimeZoneDataInfoLock = new Object();
        this.mTimeZoneDetectionInfoLock = new Object();
        this.mExternalStorageInfoLock = new Object();
        this.mAppsOnExternalStorageInfoLock = new Object();
        this.mFaceSettingsLock = new Object();
        this.mAppOpsLock = new Object();
        this.mRuntimeAppOpAccessMessageLock = new Object();
        this.mNotificationRemoteViewsLock = new Object();
        this.mDangerousPermissionStateLock = new Object();
        this.mHealthHalLock = new Object();
        this.mAttributedAppOpsLock = new Object();
        this.mSettingsStatsLock = new Object();
        this.mInstalledIncrementalPackagesLock = new Object();
        this.mKeystoreLock = new Object();
        this.mContext = context;
    }

    /* loaded from: classes2.dex */
    public class StatsPullAtomCallbackImpl implements StatsManager.StatsPullAtomCallback {
        public StatsPullAtomCallbackImpl() {
        }

        /* JADX WARN: Removed duplicated region for block: B:392:0x03d9 A[Catch: all -> 0x04d5, TryCatch #2 {all -> 0x04d5, blocks: (B:30:0x0054, B:31:0x006a, B:32:0x006b, B:33:0x0071, B:41:0x0080, B:42:0x0086, B:50:0x0095, B:51:0x009b, B:59:0x00aa, B:60:0x00b0, B:68:0x00bf, B:69:0x00c5, B:77:0x00d4, B:78:0x00da, B:86:0x00e9, B:87:0x00ef, B:95:0x00fe, B:96:0x0104, B:104:0x0114, B:105:0x011a, B:113:0x0129, B:114:0x012f, B:122:0x013e, B:123:0x0144, B:131:0x0153, B:132:0x0159, B:140:0x0168, B:143:0x0172, B:144:0x0178, B:152:0x0187, B:153:0x018d, B:161:0x019c, B:164:0x01a6, B:167:0x01b0, B:170:0x01ba, B:173:0x01c4, B:176:0x01ce, B:179:0x01d8, B:180:0x01de, B:188:0x01ed, B:191:0x01f7, B:194:0x0201, B:195:0x0207, B:203:0x0216, B:206:0x0220, B:207:0x0226, B:215:0x0235, B:216:0x023b, B:224:0x024a, B:227:0x0254, B:228:0x025a, B:236:0x0269, B:237:0x026f, B:245:0x027e, B:248:0x0288, B:249:0x028e, B:257:0x029d, B:258:0x02a3, B:266:0x02b2, B:267:0x02b8, B:275:0x02c7, B:276:0x02cd, B:284:0x02dc, B:285:0x02e2, B:293:0x02f1, B:294:0x02f7, B:302:0x0306, B:303:0x030c, B:311:0x031c, B:312:0x0322, B:320:0x0331, B:321:0x0337, B:329:0x0346, B:330:0x034c, B:338:0x035b, B:339:0x0361, B:347:0x0370, B:348:0x0376, B:356:0x0385, B:357:0x038b, B:365:0x039a, B:366:0x03a0, B:374:0x03af, B:375:0x03b5, B:383:0x03c4, B:384:0x03ca, B:392:0x03d9, B:393:0x03df, B:401:0x03ee, B:402:0x03f4, B:410:0x0403, B:411:0x0409, B:419:0x0418, B:420:0x041e, B:428:0x042d, B:429:0x0433, B:437:0x0442, B:438:0x0448, B:446:0x0457, B:447:0x045d, B:455:0x046c, B:456:0x0472, B:464:0x0481, B:465:0x0487, B:473:0x0496, B:474:0x049c, B:482:0x04ab, B:483:0x04b1, B:491:0x04c0, B:492:0x04c6, B:502:0x04d8, B:503:0x04de, B:511:0x04ed, B:512:0x04f3, B:520:0x0502, B:521:0x0508, B:529:0x0517, B:530:0x051d, B:538:0x052c, B:539:0x0532, B:547:0x0541, B:548:0x0547, B:556:0x0556, B:557:0x055c, B:565:0x056b, B:566:0x0571, B:106:0x011b, B:107:0x0121, B:52:0x009c, B:53:0x00a2, B:475:0x049d, B:476:0x04a3, B:531:0x051e, B:532:0x0524, B:421:0x041f, B:422:0x0425, B:367:0x03a1, B:368:0x03a7, B:313:0x0323, B:314:0x0329, B:259:0x02a4, B:260:0x02aa, B:208:0x0227, B:209:0x022d, B:115:0x0130, B:116:0x0136, B:61:0x00b1, B:62:0x00b7, B:484:0x04b2, B:485:0x04b8, B:540:0x0533, B:541:0x0539, B:430:0x0434, B:431:0x043a, B:376:0x03b6, B:377:0x03bc, B:322:0x0338, B:323:0x033e, B:268:0x02b9, B:269:0x02bf, B:217:0x023c, B:218:0x0242, B:124:0x0145, B:125:0x014b, B:70:0x00c6, B:71:0x00cc, B:493:0x04c7, B:494:0x04cd, B:549:0x0548, B:550:0x054e, B:439:0x0449, B:440:0x044f, B:385:0x03cb, B:386:0x03d1, B:331:0x034d, B:332:0x0353, B:277:0x02ce, B:278:0x02d4, B:133:0x015a, B:134:0x0160, B:229:0x025b, B:230:0x0261, B:79:0x00db, B:80:0x00e1, B:558:0x055d, B:559:0x0563, B:448:0x045e, B:449:0x0464, B:504:0x04df, B:505:0x04e5, B:181:0x01df, B:182:0x01e5, B:394:0x03e0, B:395:0x03e6, B:340:0x0362, B:341:0x0368, B:286:0x02e3, B:287:0x02e9, B:238:0x0270, B:239:0x0276, B:88:0x00f0, B:89:0x00f6, B:567:0x0572, B:568:0x0578, B:34:0x0072, B:35:0x0078, B:457:0x0473, B:458:0x0479, B:513:0x04f4, B:514:0x04fa, B:403:0x03f5, B:404:0x03fb, B:349:0x0377, B:350:0x037d, B:295:0x02f8, B:296:0x02fe, B:145:0x0179, B:146:0x017f, B:97:0x0105, B:98:0x010c, B:43:0x0087, B:44:0x008d, B:466:0x0488, B:467:0x048e, B:196:0x0208, B:197:0x020e, B:522:0x0509, B:523:0x050f, B:412:0x040a, B:413:0x0410, B:358:0x038c, B:359:0x0392, B:304:0x030d, B:305:0x0314, B:154:0x018e, B:155:0x0194, B:250:0x028f, B:251:0x0295), top: B:576:0x001f }] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public int onPullAtom(int i, List<StatsEvent> list) {
            int pullBluetoothBytesTransferLocked;
            int pullBluetoothActivityInfoLocked;
            int pullTimeZoneDataInfoLocked;
            int pullExternalStorageInfoLocked;
            int pullNotificationRemoteViewsLocked;
            int pullDangerousPermissionStateLocked;
            int pullRuntimeAppOpAccessMessageLocked;
            int pullIonHeapSizeLocked;
            int pullDataBytesTransferLocked;
            int pullKernelWakelockLocked;
            int pullCpuTimePerUidLocked;
            int pullCpuTimePerUidFreqLocked;
            int pullWifiActivityInfoLocked;
            int pullModemActivityInfoLocked;
            int pullProcessMemoryStateLocked;
            int pullSystemElapsedRealtimeLocked;
            int pullSystemUptimeLocked;
            int pullCpuActiveTimeLocked;
            int pullCpuClusterTimeLocked;
            int pullHealthHalLocked;
            int pullTemperatureLocked;
            int pullBinderCallsStatsLocked;
            int pullBinderCallsStatsExceptionsLocked;
            int pullLooperStatsLocked;
            int pullDiskStatsLocked;
            int pullDirectoryUsageLocked;
            int pullAppSizeLocked;
            int pullCategorySizeLocked;
            int pullProcStatsLocked;
            int pullNumBiometricsEnrolledLocked;
            int pullDiskIOLocked;
            int pullPowerProfileLocked;
            int pullProcStatsLocked2;
            int pullProcessCpuTimeLocked;
            int pullCpuTimePerThreadFreqLocked;
            int pullDeviceCalculatedPowerUseLocked;
            int pullAttributedAppOpsLocked;
            int pullSettingsStatsLocked;
            int pullCpuTimePerClusterFreqLocked;
            int pullCpuCyclesPerUidClusterLocked;
            int pullTimeZoneDetectorStateLocked;
            int pullInstalledIncrementalPackagesLocked;
            int pullProcessStateLocked;
            int pullProcessAssociationLocked;
            int pullProcessMemoryHighWaterMarkLocked;
            int pullBuildInformationLocked;
            int pullDebugElapsedClockLocked;
            int pullDebugFailingElapsedClockLocked;
            int pullNumBiometricsEnrolledLocked2;
            int pullRoleHolderLocked;
            int pullSystemIonHeapSizeLocked;
            int pullAppsOnExternalStorageInfoLocked;
            int pullFaceSettingsLocked;
            int pullCooldownDeviceLocked;
            int pullAppOpsLocked;
            int pullProcessSystemIonHeapSizeLocked;
            if (Trace.isTagEnabled(524288L)) {
                Trace.traceBegin(524288L, "StatsPull-" + i);
            }
            try {
                if (i == 10006) {
                    synchronized (StatsPullAtomService.this.mBluetoothBytesTransferLock) {
                        pullBluetoothBytesTransferLocked = StatsPullAtomService.this.pullBluetoothBytesTransferLocked(i, list);
                    }
                    Trace.traceEnd(524288L);
                    return pullBluetoothBytesTransferLocked;
                } else if (i == 10007) {
                    synchronized (StatsPullAtomService.this.mBluetoothActivityInfoLock) {
                        pullBluetoothActivityInfoLocked = StatsPullAtomService.this.pullBluetoothActivityInfoLocked(i, list);
                    }
                    Trace.traceEnd(524288L);
                    return pullBluetoothActivityInfoLocked;
                } else if (i == 10052) {
                    synchronized (StatsPullAtomService.this.mTimeZoneDataInfoLock) {
                        pullTimeZoneDataInfoLocked = StatsPullAtomService.this.pullTimeZoneDataInfoLocked(i, list);
                    }
                    Trace.traceEnd(524288L);
                    return pullTimeZoneDataInfoLocked;
                } else if (i == 10053) {
                    synchronized (StatsPullAtomService.this.mExternalStorageInfoLock) {
                        pullExternalStorageInfoLocked = StatsPullAtomService.this.pullExternalStorageInfoLocked(i, list);
                    }
                    Trace.traceEnd(524288L);
                    return pullExternalStorageInfoLocked;
                } else if (i == 10066) {
                    synchronized (StatsPullAtomService.this.mNotificationRemoteViewsLock) {
                        pullNotificationRemoteViewsLocked = StatsPullAtomService.this.pullNotificationRemoteViewsLocked(i, list);
                    }
                    Trace.traceEnd(524288L);
                    return pullNotificationRemoteViewsLocked;
                } else {
                    if (i != 10067) {
                        if (i == 10069) {
                            synchronized (StatsPullAtomService.this.mRuntimeAppOpAccessMessageLock) {
                                pullRuntimeAppOpAccessMessageLocked = StatsPullAtomService.this.pullRuntimeAppOpAccessMessageLocked(i, list);
                            }
                            Trace.traceEnd(524288L);
                            return pullRuntimeAppOpAccessMessageLocked;
                        } else if (i == 10070) {
                            synchronized (StatsPullAtomService.this.mIonHeapSizeLock) {
                                pullIonHeapSizeLocked = StatsPullAtomService.this.pullIonHeapSizeLocked(i, list);
                            }
                            Trace.traceEnd(524288L);
                            return pullIonHeapSizeLocked;
                        } else {
                            if (i != 10082 && i != 10083) {
                                switch (i) {
                                    case FrameworkStatsLog.WIFI_BYTES_TRANSFER /* 10000 */:
                                    case FrameworkStatsLog.WIFI_BYTES_TRANSFER_BY_FG_BG /* 10001 */:
                                    case FrameworkStatsLog.MOBILE_BYTES_TRANSFER /* 10002 */:
                                    case FrameworkStatsLog.MOBILE_BYTES_TRANSFER_BY_FG_BG /* 10003 */:
                                        break;
                                    case FrameworkStatsLog.KERNEL_WAKELOCK /* 10004 */:
                                        synchronized (StatsPullAtomService.this.mKernelWakelockLock) {
                                            pullKernelWakelockLocked = StatsPullAtomService.this.pullKernelWakelockLocked(i, list);
                                        }
                                        Trace.traceEnd(524288L);
                                        return pullKernelWakelockLocked;
                                    default:
                                        switch (i) {
                                            case FrameworkStatsLog.CPU_TIME_PER_UID /* 10009 */:
                                                synchronized (StatsPullAtomService.this.mCpuTimePerUidLock) {
                                                    pullCpuTimePerUidLocked = StatsPullAtomService.this.pullCpuTimePerUidLocked(i, list);
                                                }
                                                Trace.traceEnd(524288L);
                                                return pullCpuTimePerUidLocked;
                                            case FrameworkStatsLog.CPU_TIME_PER_UID_FREQ /* 10010 */:
                                                synchronized (StatsPullAtomService.this.mCpuTimePerUidFreqLock) {
                                                    pullCpuTimePerUidFreqLocked = StatsPullAtomService.this.pullCpuTimePerUidFreqLocked(i, list);
                                                }
                                                Trace.traceEnd(524288L);
                                                return pullCpuTimePerUidFreqLocked;
                                            case FrameworkStatsLog.WIFI_ACTIVITY_INFO /* 10011 */:
                                                synchronized (StatsPullAtomService.this.mWifiActivityInfoLock) {
                                                    pullWifiActivityInfoLocked = StatsPullAtomService.this.pullWifiActivityInfoLocked(i, list);
                                                }
                                                Trace.traceEnd(524288L);
                                                return pullWifiActivityInfoLocked;
                                            case FrameworkStatsLog.MODEM_ACTIVITY_INFO /* 10012 */:
                                                synchronized (StatsPullAtomService.this.mModemActivityInfoLock) {
                                                    pullModemActivityInfoLocked = StatsPullAtomService.this.pullModemActivityInfoLocked(i, list);
                                                }
                                                Trace.traceEnd(524288L);
                                                return pullModemActivityInfoLocked;
                                            case FrameworkStatsLog.PROCESS_MEMORY_STATE /* 10013 */:
                                                synchronized (StatsPullAtomService.this.mProcessMemoryStateLock) {
                                                    pullProcessMemoryStateLocked = StatsPullAtomService.this.pullProcessMemoryStateLocked(i, list);
                                                }
                                                Trace.traceEnd(524288L);
                                                return pullProcessMemoryStateLocked;
                                            case FrameworkStatsLog.SYSTEM_ELAPSED_REALTIME /* 10014 */:
                                                synchronized (StatsPullAtomService.this.mSystemElapsedRealtimeLock) {
                                                    pullSystemElapsedRealtimeLocked = StatsPullAtomService.this.pullSystemElapsedRealtimeLocked(i, list);
                                                }
                                                Trace.traceEnd(524288L);
                                                return pullSystemElapsedRealtimeLocked;
                                            case FrameworkStatsLog.SYSTEM_UPTIME /* 10015 */:
                                                synchronized (StatsPullAtomService.this.mSystemUptimeLock) {
                                                    pullSystemUptimeLocked = StatsPullAtomService.this.pullSystemUptimeLocked(i, list);
                                                }
                                                Trace.traceEnd(524288L);
                                                return pullSystemUptimeLocked;
                                            case FrameworkStatsLog.CPU_ACTIVE_TIME /* 10016 */:
                                                synchronized (StatsPullAtomService.this.mCpuActiveTimeLock) {
                                                    pullCpuActiveTimeLocked = StatsPullAtomService.this.pullCpuActiveTimeLocked(i, list);
                                                }
                                                Trace.traceEnd(524288L);
                                                return pullCpuActiveTimeLocked;
                                            case FrameworkStatsLog.CPU_CLUSTER_TIME /* 10017 */:
                                                synchronized (StatsPullAtomService.this.mCpuClusterTimeLock) {
                                                    pullCpuClusterTimeLocked = StatsPullAtomService.this.pullCpuClusterTimeLocked(i, list);
                                                }
                                                Trace.traceEnd(524288L);
                                                return pullCpuClusterTimeLocked;
                                            default:
                                                switch (i) {
                                                    case FrameworkStatsLog.REMAINING_BATTERY_CAPACITY /* 10019 */:
                                                    case FrameworkStatsLog.FULL_BATTERY_CAPACITY /* 10020 */:
                                                    case FrameworkStatsLog.BATTERY_VOLTAGE /* 10030 */:
                                                        synchronized (StatsPullAtomService.this.mHealthHalLock) {
                                                            pullHealthHalLocked = StatsPullAtomService.this.pullHealthHalLocked(i, list);
                                                        }
                                                        Trace.traceEnd(524288L);
                                                        return pullHealthHalLocked;
                                                    case FrameworkStatsLog.TEMPERATURE /* 10021 */:
                                                        synchronized (StatsPullAtomService.this.mTemperatureLock) {
                                                            pullTemperatureLocked = StatsPullAtomService.this.pullTemperatureLocked(i, list);
                                                        }
                                                        Trace.traceEnd(524288L);
                                                        return pullTemperatureLocked;
                                                    case FrameworkStatsLog.BINDER_CALLS /* 10022 */:
                                                        synchronized (StatsPullAtomService.this.mBinderCallsStatsLock) {
                                                            pullBinderCallsStatsLocked = StatsPullAtomService.this.pullBinderCallsStatsLocked(i, list);
                                                        }
                                                        Trace.traceEnd(524288L);
                                                        return pullBinderCallsStatsLocked;
                                                    case FrameworkStatsLog.BINDER_CALLS_EXCEPTIONS /* 10023 */:
                                                        synchronized (StatsPullAtomService.this.mBinderCallsStatsExceptionsLock) {
                                                            pullBinderCallsStatsExceptionsLocked = StatsPullAtomService.this.pullBinderCallsStatsExceptionsLocked(i, list);
                                                        }
                                                        Trace.traceEnd(524288L);
                                                        return pullBinderCallsStatsExceptionsLocked;
                                                    case FrameworkStatsLog.LOOPER_STATS /* 10024 */:
                                                        synchronized (StatsPullAtomService.this.mLooperStatsLock) {
                                                            pullLooperStatsLocked = StatsPullAtomService.this.pullLooperStatsLocked(i, list);
                                                        }
                                                        Trace.traceEnd(524288L);
                                                        return pullLooperStatsLocked;
                                                    case FrameworkStatsLog.DISK_STATS /* 10025 */:
                                                        synchronized (StatsPullAtomService.this.mDiskStatsLock) {
                                                            pullDiskStatsLocked = StatsPullAtomService.this.pullDiskStatsLocked(i, list);
                                                        }
                                                        Trace.traceEnd(524288L);
                                                        return pullDiskStatsLocked;
                                                    case FrameworkStatsLog.DIRECTORY_USAGE /* 10026 */:
                                                        synchronized (StatsPullAtomService.this.mDirectoryUsageLock) {
                                                            pullDirectoryUsageLocked = StatsPullAtomService.this.pullDirectoryUsageLocked(i, list);
                                                        }
                                                        Trace.traceEnd(524288L);
                                                        return pullDirectoryUsageLocked;
                                                    case FrameworkStatsLog.APP_SIZE /* 10027 */:
                                                        synchronized (StatsPullAtomService.this.mAppSizeLock) {
                                                            pullAppSizeLocked = StatsPullAtomService.this.pullAppSizeLocked(i, list);
                                                        }
                                                        Trace.traceEnd(524288L);
                                                        return pullAppSizeLocked;
                                                    case FrameworkStatsLog.CATEGORY_SIZE /* 10028 */:
                                                        synchronized (StatsPullAtomService.this.mCategorySizeLock) {
                                                            pullCategorySizeLocked = StatsPullAtomService.this.pullCategorySizeLocked(i, list);
                                                        }
                                                        Trace.traceEnd(524288L);
                                                        return pullCategorySizeLocked;
                                                    case FrameworkStatsLog.PROC_STATS /* 10029 */:
                                                        synchronized (StatsPullAtomService.this.mProcStatsLock) {
                                                            pullProcStatsLocked = StatsPullAtomService.this.pullProcStatsLocked(i, list);
                                                        }
                                                        Trace.traceEnd(524288L);
                                                        return pullProcStatsLocked;
                                                    case FrameworkStatsLog.NUM_FINGERPRINTS_ENROLLED /* 10031 */:
                                                        synchronized (StatsPullAtomService.this.mNumBiometricsEnrolledLock) {
                                                            pullNumBiometricsEnrolledLocked = StatsPullAtomService.this.pullNumBiometricsEnrolledLocked(1, i, list);
                                                        }
                                                        Trace.traceEnd(524288L);
                                                        return pullNumBiometricsEnrolledLocked;
                                                    case FrameworkStatsLog.DISK_IO /* 10032 */:
                                                        synchronized (StatsPullAtomService.this.mDiskIoLock) {
                                                            pullDiskIOLocked = StatsPullAtomService.this.pullDiskIOLocked(i, list);
                                                        }
                                                        Trace.traceEnd(524288L);
                                                        return pullDiskIOLocked;
                                                    case FrameworkStatsLog.POWER_PROFILE /* 10033 */:
                                                        synchronized (StatsPullAtomService.this.mPowerProfileLock) {
                                                            pullPowerProfileLocked = StatsPullAtomService.this.pullPowerProfileLocked(i, list);
                                                        }
                                                        Trace.traceEnd(524288L);
                                                        return pullPowerProfileLocked;
                                                    case FrameworkStatsLog.PROC_STATS_PKG_PROC /* 10034 */:
                                                        synchronized (StatsPullAtomService.this.mProcStatsLock) {
                                                            pullProcStatsLocked2 = StatsPullAtomService.this.pullProcStatsLocked(i, list);
                                                        }
                                                        Trace.traceEnd(524288L);
                                                        return pullProcStatsLocked2;
                                                    case FrameworkStatsLog.PROCESS_CPU_TIME /* 10035 */:
                                                        synchronized (StatsPullAtomService.this.mProcessCpuTimeLock) {
                                                            pullProcessCpuTimeLocked = StatsPullAtomService.this.pullProcessCpuTimeLocked(i, list);
                                                        }
                                                        Trace.traceEnd(524288L);
                                                        return pullProcessCpuTimeLocked;
                                                    case FrameworkStatsLog.CPU_TIME_PER_THREAD_FREQ /* 10037 */:
                                                        synchronized (StatsPullAtomService.this.mCpuTimePerThreadFreqLock) {
                                                            pullCpuTimePerThreadFreqLocked = StatsPullAtomService.this.pullCpuTimePerThreadFreqLocked(i, list);
                                                        }
                                                        Trace.traceEnd(524288L);
                                                        return pullCpuTimePerThreadFreqLocked;
                                                    case FrameworkStatsLog.DEVICE_CALCULATED_POWER_USE /* 10039 */:
                                                        synchronized (StatsPullAtomService.this.mDeviceCalculatedPowerUseLock) {
                                                            pullDeviceCalculatedPowerUseLocked = StatsPullAtomService.this.pullDeviceCalculatedPowerUseLocked(i, list);
                                                        }
                                                        Trace.traceEnd(524288L);
                                                        return pullDeviceCalculatedPowerUseLocked;
                                                    case FrameworkStatsLog.PROCESS_MEMORY_SNAPSHOT /* 10064 */:
                                                        int pullProcessMemorySnapshot = StatsPullAtomService.this.pullProcessMemorySnapshot(i, list);
                                                        Trace.traceEnd(524288L);
                                                        return pullProcessMemorySnapshot;
                                                    case FrameworkStatsLog.ATTRIBUTED_APP_OPS /* 10075 */:
                                                        synchronized (StatsPullAtomService.this.mAttributedAppOpsLock) {
                                                            pullAttributedAppOpsLocked = StatsPullAtomService.this.pullAttributedAppOpsLocked(i, list);
                                                        }
                                                        Trace.traceEnd(524288L);
                                                        return pullAttributedAppOpsLocked;
                                                    case FrameworkStatsLog.SETTING_SNAPSHOT /* 10080 */:
                                                        synchronized (StatsPullAtomService.this.mSettingsStatsLock) {
                                                            pullSettingsStatsLocked = StatsPullAtomService.this.pullSettingsStatsLocked(i, list);
                                                        }
                                                        Trace.traceEnd(524288L);
                                                        return pullSettingsStatsLocked;
                                                    case FrameworkStatsLog.SYSTEM_MEMORY /* 10092 */:
                                                        int pullSystemMemory = StatsPullAtomService.this.pullSystemMemory(i, list);
                                                        Trace.traceEnd(524288L);
                                                        return pullSystemMemory;
                                                    case FrameworkStatsLog.CPU_TIME_PER_CLUSTER_FREQ /* 10095 */:
                                                        synchronized (StatsPullAtomService.this.mCpuTimePerClusterFreqLock) {
                                                            pullCpuTimePerClusterFreqLocked = StatsPullAtomService.this.pullCpuTimePerClusterFreqLocked(i, list);
                                                        }
                                                        Trace.traceEnd(524288L);
                                                        return pullCpuTimePerClusterFreqLocked;
                                                    case FrameworkStatsLog.CPU_CYCLES_PER_UID_CLUSTER /* 10096 */:
                                                        synchronized (StatsPullAtomService.this.mCpuTimePerUidFreqLock) {
                                                            pullCpuCyclesPerUidClusterLocked = StatsPullAtomService.this.pullCpuCyclesPerUidClusterLocked(i, list);
                                                        }
                                                        Trace.traceEnd(524288L);
                                                        return pullCpuCyclesPerUidClusterLocked;
                                                    case FrameworkStatsLog.CPU_CYCLES_PER_THREAD_GROUP_CLUSTER /* 10098 */:
                                                        int pullCpuCyclesPerThreadGroupCluster = StatsPullAtomService.this.pullCpuCyclesPerThreadGroupCluster(i, list);
                                                        Trace.traceEnd(524288L);
                                                        return pullCpuCyclesPerThreadGroupCluster;
                                                    case FrameworkStatsLog.OEM_MANAGED_BYTES_TRANSFER /* 10100 */:
                                                        break;
                                                    case FrameworkStatsLog.TIME_ZONE_DETECTOR_STATE /* 10102 */:
                                                        synchronized (StatsPullAtomService.this.mTimeZoneDetectionInfoLock) {
                                                            pullTimeZoneDetectorStateLocked = StatsPullAtomService.this.pullTimeZoneDetectorStateLocked(i, list);
                                                        }
                                                        Trace.traceEnd(524288L);
                                                        return pullTimeZoneDetectorStateLocked;
                                                    case FrameworkStatsLog.KEYSTORE2_STORAGE_STATS /* 10103 */:
                                                    case FrameworkStatsLog.RKP_POOL_STATS /* 10104 */:
                                                    case FrameworkStatsLog.KEYSTORE2_KEY_CREATION_WITH_GENERAL_INFO /* 10118 */:
                                                    case FrameworkStatsLog.KEYSTORE2_KEY_CREATION_WITH_AUTH_INFO /* 10119 */:
                                                    case FrameworkStatsLog.KEYSTORE2_KEY_CREATION_WITH_PURPOSE_AND_MODES_INFO /* 10120 */:
                                                    case FrameworkStatsLog.KEYSTORE2_ATOM_WITH_OVERFLOW /* 10121 */:
                                                    case FrameworkStatsLog.KEYSTORE2_KEY_OPERATION_WITH_PURPOSE_AND_MODES_INFO /* 10122 */:
                                                    case FrameworkStatsLog.KEYSTORE2_KEY_OPERATION_WITH_GENERAL_INFO /* 10123 */:
                                                    case FrameworkStatsLog.RKP_ERROR_STATS /* 10124 */:
                                                    case FrameworkStatsLog.KEYSTORE2_CRASH_STATS /* 10125 */:
                                                        int pullKeystoreAtoms = StatsPullAtomService.this.pullKeystoreAtoms(i, list);
                                                        Trace.traceEnd(524288L);
                                                        return pullKeystoreAtoms;
                                                    case FrameworkStatsLog.PROCESS_DMABUF_MEMORY /* 10105 */:
                                                        int pullProcessDmabufMemory = StatsPullAtomService.this.pullProcessDmabufMemory(i, list);
                                                        Trace.traceEnd(524288L);
                                                        return pullProcessDmabufMemory;
                                                    case FrameworkStatsLog.INSTALLED_INCREMENTAL_PACKAGE /* 10114 */:
                                                        synchronized (StatsPullAtomService.this.mInstalledIncrementalPackagesLock) {
                                                            pullInstalledIncrementalPackagesLocked = StatsPullAtomService.this.pullInstalledIncrementalPackagesLocked(i, list);
                                                        }
                                                        Trace.traceEnd(524288L);
                                                        return pullInstalledIncrementalPackagesLocked;
                                                    case FrameworkStatsLog.VMSTAT /* 10117 */:
                                                        int pullVmStat = StatsPullAtomService.this.pullVmStat(i, list);
                                                        Trace.traceEnd(524288L);
                                                        return pullVmStat;
                                                    case FrameworkStatsLog.ACCESSIBILITY_SHORTCUT_STATS /* 10127 */:
                                                        int pullAccessibilityShortcutStatsLocked = StatsPullAtomService.this.pullAccessibilityShortcutStatsLocked(i, list);
                                                        Trace.traceEnd(524288L);
                                                        return pullAccessibilityShortcutStatsLocked;
                                                    case FrameworkStatsLog.ACCESSIBILITY_FLOATING_MENU_STATS /* 10128 */:
                                                        int pullAccessibilityFloatingMenuStatsLocked = StatsPullAtomService.this.pullAccessibilityFloatingMenuStatsLocked(i, list);
                                                        Trace.traceEnd(524288L);
                                                        return pullAccessibilityFloatingMenuStatsLocked;
                                                    case FrameworkStatsLog.MEDIA_CAPABILITIES /* 10130 */:
                                                        int pullMediaCapabilitiesStats = StatsPullAtomService.this.pullMediaCapabilitiesStats(i, list);
                                                        Trace.traceEnd(524288L);
                                                        return pullMediaCapabilitiesStats;
                                                    case FrameworkStatsLog.PINNED_FILE_SIZES_PER_PACKAGE /* 10150 */:
                                                        int pullSystemServerPinnerStats = StatsPullAtomService.this.pullSystemServerPinnerStats(i, list);
                                                        Trace.traceEnd(524288L);
                                                        return pullSystemServerPinnerStats;
                                                    case FrameworkStatsLog.PENDING_INTENTS_PER_PACKAGE /* 10151 */:
                                                        int pullPendingIntentsPerPackage = StatsPullAtomService.this.pullPendingIntentsPerPackage(i, list);
                                                        Trace.traceEnd(524288L);
                                                        return pullPendingIntentsPerPackage;
                                                    case FrameworkStatsLog.PROCESS_STATE /* 10171 */:
                                                        synchronized (StatsPullAtomService.this.mProcStatsLock) {
                                                            pullProcessStateLocked = StatsPullAtomService.this.pullProcessStateLocked(i, list);
                                                        }
                                                        Trace.traceEnd(524288L);
                                                        return pullProcessStateLocked;
                                                    case FrameworkStatsLog.PROCESS_ASSOCIATION /* 10172 */:
                                                        synchronized (StatsPullAtomService.this.mProcStatsLock) {
                                                            pullProcessAssociationLocked = StatsPullAtomService.this.pullProcessAssociationLocked(i, list);
                                                        }
                                                        Trace.traceEnd(524288L);
                                                        return pullProcessAssociationLocked;
                                                    case FrameworkStatsLog.HDR_CAPABILITIES /* 10175 */:
                                                        int pullHdrCapabilities = StatsPullAtomService.this.pullHdrCapabilities(i, list);
                                                        Trace.traceEnd(524288L);
                                                        return pullHdrCapabilities;
                                                    default:
                                                        switch (i) {
                                                            case FrameworkStatsLog.PROCESS_MEMORY_HIGH_WATER_MARK /* 10042 */:
                                                                synchronized (StatsPullAtomService.this.mProcessMemoryHighWaterMarkLock) {
                                                                    pullProcessMemoryHighWaterMarkLocked = StatsPullAtomService.this.pullProcessMemoryHighWaterMarkLocked(i, list);
                                                                }
                                                                Trace.traceEnd(524288L);
                                                                return pullProcessMemoryHighWaterMarkLocked;
                                                            case FrameworkStatsLog.BATTERY_LEVEL /* 10043 */:
                                                            case FrameworkStatsLog.BATTERY_CYCLE_COUNT /* 10045 */:
                                                                break;
                                                            case FrameworkStatsLog.BUILD_INFORMATION /* 10044 */:
                                                                synchronized (StatsPullAtomService.this.mBuildInformationLock) {
                                                                    pullBuildInformationLocked = StatsPullAtomService.this.pullBuildInformationLocked(i, list);
                                                                }
                                                                Trace.traceEnd(524288L);
                                                                return pullBuildInformationLocked;
                                                            case FrameworkStatsLog.DEBUG_ELAPSED_CLOCK /* 10046 */:
                                                                synchronized (StatsPullAtomService.this.mDebugElapsedClockLock) {
                                                                    pullDebugElapsedClockLocked = StatsPullAtomService.this.pullDebugElapsedClockLocked(i, list);
                                                                }
                                                                Trace.traceEnd(524288L);
                                                                return pullDebugElapsedClockLocked;
                                                            case FrameworkStatsLog.DEBUG_FAILING_ELAPSED_CLOCK /* 10047 */:
                                                                synchronized (StatsPullAtomService.this.mDebugFailingElapsedClockLock) {
                                                                    pullDebugFailingElapsedClockLocked = StatsPullAtomService.this.pullDebugFailingElapsedClockLocked(i, list);
                                                                }
                                                                Trace.traceEnd(524288L);
                                                                return pullDebugFailingElapsedClockLocked;
                                                            case FrameworkStatsLog.NUM_FACES_ENROLLED /* 10048 */:
                                                                synchronized (StatsPullAtomService.this.mNumBiometricsEnrolledLock) {
                                                                    pullNumBiometricsEnrolledLocked2 = StatsPullAtomService.this.pullNumBiometricsEnrolledLocked(4, i, list);
                                                                }
                                                                Trace.traceEnd(524288L);
                                                                return pullNumBiometricsEnrolledLocked2;
                                                            case FrameworkStatsLog.ROLE_HOLDER /* 10049 */:
                                                                synchronized (StatsPullAtomService.this.mRoleHolderLock) {
                                                                    pullRoleHolderLocked = StatsPullAtomService.this.pullRoleHolderLocked(i, list);
                                                                }
                                                                Trace.traceEnd(524288L);
                                                                return pullRoleHolderLocked;
                                                            case FrameworkStatsLog.DANGEROUS_PERMISSION_STATE /* 10050 */:
                                                                break;
                                                            default:
                                                                switch (i) {
                                                                    case FrameworkStatsLog.SYSTEM_ION_HEAP_SIZE /* 10056 */:
                                                                        synchronized (StatsPullAtomService.this.mSystemIonHeapSizeLock) {
                                                                            pullSystemIonHeapSizeLocked = StatsPullAtomService.this.pullSystemIonHeapSizeLocked(i, list);
                                                                        }
                                                                        Trace.traceEnd(524288L);
                                                                        return pullSystemIonHeapSizeLocked;
                                                                    case FrameworkStatsLog.APPS_ON_EXTERNAL_STORAGE_INFO /* 10057 */:
                                                                        synchronized (StatsPullAtomService.this.mAppsOnExternalStorageInfoLock) {
                                                                            pullAppsOnExternalStorageInfoLocked = StatsPullAtomService.this.pullAppsOnExternalStorageInfoLocked(i, list);
                                                                        }
                                                                        Trace.traceEnd(524288L);
                                                                        return pullAppsOnExternalStorageInfoLocked;
                                                                    case FrameworkStatsLog.FACE_SETTINGS /* 10058 */:
                                                                        synchronized (StatsPullAtomService.this.mFaceSettingsLock) {
                                                                            pullFaceSettingsLocked = StatsPullAtomService.this.pullFaceSettingsLocked(i, list);
                                                                        }
                                                                        Trace.traceEnd(524288L);
                                                                        return pullFaceSettingsLocked;
                                                                    case FrameworkStatsLog.COOLING_DEVICE /* 10059 */:
                                                                        synchronized (StatsPullAtomService.this.mCooldownDeviceLock) {
                                                                            pullCooldownDeviceLocked = StatsPullAtomService.this.pullCooldownDeviceLocked(i, list);
                                                                        }
                                                                        Trace.traceEnd(524288L);
                                                                        return pullCooldownDeviceLocked;
                                                                    case FrameworkStatsLog.APP_OPS /* 10060 */:
                                                                        synchronized (StatsPullAtomService.this.mAppOpsLock) {
                                                                            pullAppOpsLocked = StatsPullAtomService.this.pullAppOpsLocked(i, list);
                                                                        }
                                                                        Trace.traceEnd(524288L);
                                                                        return pullAppOpsLocked;
                                                                    case FrameworkStatsLog.PROCESS_SYSTEM_ION_HEAP_SIZE /* 10061 */:
                                                                        synchronized (StatsPullAtomService.this.mProcessSystemIonHeapSizeLock) {
                                                                            pullProcessSystemIonHeapSizeLocked = StatsPullAtomService.this.pullProcessSystemIonHeapSizeLocked(i, list);
                                                                        }
                                                                        Trace.traceEnd(524288L);
                                                                        return pullProcessSystemIonHeapSizeLocked;
                                                                    default:
                                                                        throw new UnsupportedOperationException("Unknown tagId=" + i);
                                                                }
                                                        }
                                                }
                                        }
                                }
                            }
                            synchronized (StatsPullAtomService.this.mDataBytesTransferLock) {
                                pullDataBytesTransferLocked = StatsPullAtomService.this.pullDataBytesTransferLocked(i, list);
                            }
                            Trace.traceEnd(524288L);
                            return pullDataBytesTransferLocked;
                        }
                    }
                    synchronized (StatsPullAtomService.this.mDangerousPermissionStateLock) {
                        pullDangerousPermissionStateLocked = StatsPullAtomService.this.pullDangerousPermissionStateLocked(i, list);
                    }
                    Trace.traceEnd(524288L);
                    return pullDangerousPermissionStateLocked;
                }
            } catch (Throwable th) {
                Trace.traceEnd(524288L);
                throw th;
            }
        }
    }

    @Override // com.android.server.SystemService
    public void onBootPhase(int i) {
        super.onBootPhase(i);
        if (i == 500) {
            BackgroundThread.getHandler().post(new Runnable() { // from class: com.android.server.stats.pull.StatsPullAtomService$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    StatsPullAtomService.this.lambda$onBootPhase$0();
                }
            });
        } else if (i == 600) {
            BackgroundThread.getHandler().post(new Runnable() { // from class: com.android.server.stats.pull.StatsPullAtomService$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    StatsPullAtomService.this.lambda$onBootPhase$1();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBootPhase$0() {
        initializeNativePullers();
        initializePullersState();
        registerPullers();
        registerEventListeners();
    }

    public void initializePullersState() {
        this.mStatsManager = (StatsManager) this.mContext.getSystemService("stats");
        this.mWifiManager = (WifiManager) this.mContext.getSystemService("wifi");
        this.mTelephony = (TelephonyManager) this.mContext.getSystemService("phone");
        this.mSubscriptionManager = (SubscriptionManager) this.mContext.getSystemService("telephony_subscription_service");
        this.mStatsSubscriptionsListener = new StatsSubscriptionsListener(this.mSubscriptionManager);
        this.mStorageManager = (StorageManager) this.mContext.getSystemService(StorageManager.class);
        this.mNetworkStatsManager = (NetworkStatsManager) this.mContext.getSystemService(NetworkStatsManager.class);
        this.mStoragedUidIoStatsReader = new StoragedUidIoStatsReader();
        File file = new File(SystemServiceManager.ensureSystemDir(), "stats_pull");
        this.mBaseDir = file;
        file.mkdirs();
        this.mCpuUidUserSysTimeReader = new KernelCpuUidTimeReader.KernelCpuUidUserSysTimeReader(false);
        this.mCpuUidFreqTimeReader = new KernelCpuUidTimeReader.KernelCpuUidFreqTimeReader(false);
        this.mCpuUidActiveTimeReader = new KernelCpuUidTimeReader.KernelCpuUidActiveTimeReader(false);
        this.mCpuUidClusterTimeReader = new KernelCpuUidTimeReader.KernelCpuUidClusterTimeReader(false);
        this.mKernelWakelockReader = new KernelWakelockReader();
        this.mTmpWakelockStats = new KernelWakelockStats();
        this.mKernelCpuThreadReader = KernelCpuThreadReaderSettingsObserver.getSettingsModifiedReader(this.mContext);
        try {
            this.mHealthService = HealthServiceWrapper.create(null);
        } catch (RemoteException | NoSuchElementException unused) {
            Slog.e("StatsPullAtomService", "failed to initialize healthHalWrapper");
        }
        PackageManager packageManager = this.mContext.getPackageManager();
        for (int i = 0; i < 134; i++) {
            String opToPermission = AppOpsManager.opToPermission(i);
            if (opToPermission != null) {
                try {
                    if (packageManager.getPermissionInfo(opToPermission, 0).getProtection() == 1) {
                        this.mDangerousAppOpsList.add(Integer.valueOf(i));
                    }
                } catch (PackageManager.NameNotFoundException unused2) {
                }
            }
        }
        this.mSurfaceFlingerProcessCpuThreadReader = new SelectedProcessCpuThreadReader("/system/bin/surfaceflinger");
        getIKeystoreMetricsService();
    }

    public void registerEventListeners() {
        ((ConnectivityManager) this.mContext.getSystemService("connectivity")).registerNetworkCallback(new NetworkRequest.Builder().build(), new ConnectivityStatsCallback());
        IThermalService iThermalService = getIThermalService();
        if (iThermalService != null) {
            try {
                iThermalService.registerThermalEventListener(new ThermalEventListener());
                Slog.i("StatsPullAtomService", "register thermal listener successfully");
            } catch (RemoteException unused) {
                Slog.i("StatsPullAtomService", "failed to register thermal listener");
            }
        }
    }

    public void registerPullers() {
        Slog.d("StatsPullAtomService", "Registering pullers with statsd");
        this.mStatsCallbackImpl = new StatsPullAtomCallbackImpl();
        registerBluetoothBytesTransfer();
        registerKernelWakelock();
        registerCpuTimePerClusterFreq();
        registerCpuTimePerUid();
        registerCpuCyclesPerUidCluster();
        registerCpuTimePerUidFreq();
        registerCpuCyclesPerThreadGroupCluster();
        registerCpuActiveTime();
        registerCpuClusterTime();
        registerWifiActivityInfo();
        registerModemActivityInfo();
        registerBluetoothActivityInfo();
        registerSystemElapsedRealtime();
        registerSystemUptime();
        registerProcessMemoryState();
        registerProcessMemoryHighWaterMark();
        registerProcessMemorySnapshot();
        registerSystemIonHeapSize();
        registerIonHeapSize();
        registerProcessSystemIonHeapSize();
        registerSystemMemory();
        registerProcessDmabufMemory();
        registerVmStat();
        registerTemperature();
        registerCoolingDevice();
        registerBinderCallsStats();
        registerBinderCallsStatsExceptions();
        registerLooperStats();
        registerDiskStats();
        registerDirectoryUsage();
        registerAppSize();
        registerCategorySize();
        registerNumFingerprintsEnrolled();
        registerNumFacesEnrolled();
        registerProcStats();
        registerProcStatsPkgProc();
        registerProcessState();
        registerProcessAssociation();
        registerDiskIO();
        registerPowerProfile();
        registerProcessCpuTime();
        registerCpuTimePerThreadFreq();
        registerDeviceCalculatedPowerUse();
        registerDebugElapsedClock();
        registerDebugFailingElapsedClock();
        registerBuildInformation();
        registerRoleHolder();
        registerTimeZoneDataInfo();
        registerTimeZoneDetectorState();
        registerExternalStorageInfo();
        registerAppsOnExternalStorageInfo();
        registerFaceSettings();
        registerAppOps();
        registerAttributedAppOps();
        registerRuntimeAppOpAccessMessage();
        registerNotificationRemoteViews();
        registerDangerousPermissionState();
        registerDangerousPermissionStateSampled();
        registerBatteryLevel();
        registerRemainingBatteryCapacity();
        registerFullBatteryCapacity();
        registerBatteryVoltage();
        registerBatteryCycleCount();
        registerSettingsStats();
        registerInstalledIncrementalPackages();
        registerKeystoreStorageStats();
        registerRkpPoolStats();
        registerKeystoreKeyCreationWithGeneralInfo();
        registerKeystoreKeyCreationWithAuthInfo();
        registerKeystoreKeyCreationWithPurposeModesInfo();
        registerKeystoreAtomWithOverflow();
        registerKeystoreKeyOperationWithPurposeAndModesInfo();
        registerKeystoreKeyOperationWithGeneralInfo();
        registerRkpErrorStats();
        registerKeystoreCrashStats();
        registerAccessibilityShortcutStats();
        registerAccessibilityFloatingMenuStats();
        registerMediaCapabilitiesStats();
        registerPendingIntentsPerPackagePuller();
        registerPinnerServiceStats();
        registerHdrCapabilitiesPuller();
    }

    /* renamed from: initAndRegisterNetworkStatsPullers */
    public final void lambda$onBootPhase$1() {
        Slog.d("StatsPullAtomService", "Registering NetworkStats pullers with statsd");
        this.mNetworkStatsBaselines.addAll(collectNetworkStatsSnapshotForAtom(FrameworkStatsLog.WIFI_BYTES_TRANSFER));
        this.mNetworkStatsBaselines.addAll(collectNetworkStatsSnapshotForAtom(FrameworkStatsLog.WIFI_BYTES_TRANSFER_BY_FG_BG));
        this.mNetworkStatsBaselines.addAll(collectNetworkStatsSnapshotForAtom(FrameworkStatsLog.MOBILE_BYTES_TRANSFER));
        this.mNetworkStatsBaselines.addAll(collectNetworkStatsSnapshotForAtom(FrameworkStatsLog.MOBILE_BYTES_TRANSFER_BY_FG_BG));
        this.mNetworkStatsBaselines.addAll(collectNetworkStatsSnapshotForAtom(FrameworkStatsLog.BYTES_TRANSFER_BY_TAG_AND_METERED));
        this.mNetworkStatsBaselines.addAll(collectNetworkStatsSnapshotForAtom(FrameworkStatsLog.DATA_USAGE_BYTES_TRANSFER));
        this.mNetworkStatsBaselines.addAll(collectNetworkStatsSnapshotForAtom(FrameworkStatsLog.OEM_MANAGED_BYTES_TRANSFER));
        this.mSubscriptionManager.addOnSubscriptionsChangedListener(BackgroundThread.getExecutor(), this.mStatsSubscriptionsListener);
        registerWifiBytesTransfer();
        registerWifiBytesTransferBackground();
        registerMobileBytesTransfer();
        registerMobileBytesTransferBackground();
        registerBytesTransferByTagAndMetered();
        registerDataUsageBytesTransfer();
        registerOemManagedBytesTransfer();
    }

    public final IThermalService getIThermalService() {
        IThermalService iThermalService;
        synchronized (this.mThermalLock) {
            if (this.mThermalService == null) {
                IThermalService asInterface = IThermalService.Stub.asInterface(ServiceManager.getService("thermalservice"));
                this.mThermalService = asInterface;
                if (asInterface != null) {
                    try {
                        asInterface.asBinder().linkToDeath(new IBinder.DeathRecipient() { // from class: com.android.server.stats.pull.StatsPullAtomService$$ExternalSyntheticLambda7
                            @Override // android.os.IBinder.DeathRecipient
                            public final void binderDied() {
                                StatsPullAtomService.this.lambda$getIThermalService$2();
                            }
                        }, 0);
                    } catch (RemoteException e) {
                        Slog.e("StatsPullAtomService", "linkToDeath with thermalService failed", e);
                        this.mThermalService = null;
                    }
                }
            }
            iThermalService = this.mThermalService;
        }
        return iThermalService;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getIThermalService$2() {
        synchronized (this.mThermalLock) {
            this.mThermalService = null;
        }
    }

    public final IKeystoreMetrics getIKeystoreMetricsService() {
        IKeystoreMetrics iKeystoreMetrics;
        synchronized (this.mKeystoreLock) {
            if (this.mIKeystoreMetrics == null) {
                IKeystoreMetrics asInterface = IKeystoreMetrics.Stub.asInterface(ServiceManager.getService("android.security.metrics"));
                this.mIKeystoreMetrics = asInterface;
                if (asInterface != null) {
                    try {
                        asInterface.asBinder().linkToDeath(new IBinder.DeathRecipient() { // from class: com.android.server.stats.pull.StatsPullAtomService$$ExternalSyntheticLambda6
                            @Override // android.os.IBinder.DeathRecipient
                            public final void binderDied() {
                                StatsPullAtomService.this.lambda$getIKeystoreMetricsService$3();
                            }
                        }, 0);
                    } catch (RemoteException e) {
                        Slog.e("StatsPullAtomService", "linkToDeath with IKeystoreMetrics failed", e);
                        this.mIKeystoreMetrics = null;
                    }
                }
            }
            iKeystoreMetrics = this.mIKeystoreMetrics;
        }
        return iKeystoreMetrics;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getIKeystoreMetricsService$3() {
        synchronized (this.mKeystoreLock) {
            this.mIKeystoreMetrics = null;
        }
    }

    public final IStoraged getIStoragedService() {
        synchronized (this.mStoragedLock) {
            if (this.mStorageService == null) {
                this.mStorageService = IStoraged.Stub.asInterface(ServiceManager.getService("storaged"));
            }
            IStoraged iStoraged = this.mStorageService;
            if (iStoraged != null) {
                try {
                    iStoraged.asBinder().linkToDeath(new IBinder.DeathRecipient() { // from class: com.android.server.stats.pull.StatsPullAtomService$$ExternalSyntheticLambda20
                        @Override // android.os.IBinder.DeathRecipient
                        public final void binderDied() {
                            StatsPullAtomService.this.lambda$getIStoragedService$4();
                        }
                    }, 0);
                } catch (RemoteException e) {
                    Slog.e("StatsPullAtomService", "linkToDeath with storagedService failed", e);
                    this.mStorageService = null;
                }
            }
        }
        return this.mStorageService;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getIStoragedService$4() {
        synchronized (this.mStoragedLock) {
            this.mStorageService = null;
        }
    }

    public final INotificationManager getINotificationManagerService() {
        synchronized (this.mNotificationStatsLock) {
            if (this.mNotificationManagerService == null) {
                this.mNotificationManagerService = INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));
            }
            INotificationManager iNotificationManager = this.mNotificationManagerService;
            if (iNotificationManager != null) {
                try {
                    iNotificationManager.asBinder().linkToDeath(new IBinder.DeathRecipient() { // from class: com.android.server.stats.pull.StatsPullAtomService$$ExternalSyntheticLambda21
                        @Override // android.os.IBinder.DeathRecipient
                        public final void binderDied() {
                            StatsPullAtomService.this.lambda$getINotificationManagerService$5();
                        }
                    }, 0);
                } catch (RemoteException e) {
                    Slog.e("StatsPullAtomService", "linkToDeath with notificationManager failed", e);
                    this.mNotificationManagerService = null;
                }
            }
        }
        return this.mNotificationManagerService;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getINotificationManagerService$5() {
        synchronized (this.mNotificationStatsLock) {
            this.mNotificationManagerService = null;
        }
    }

    public final IProcessStats getIProcessStatsService() {
        synchronized (this.mProcStatsLock) {
            if (this.mProcessStatsService == null) {
                this.mProcessStatsService = IProcessStats.Stub.asInterface(ServiceManager.getService("procstats"));
            }
            IProcessStats iProcessStats = this.mProcessStatsService;
            if (iProcessStats != null) {
                try {
                    iProcessStats.asBinder().linkToDeath(new IBinder.DeathRecipient() { // from class: com.android.server.stats.pull.StatsPullAtomService$$ExternalSyntheticLambda24
                        @Override // android.os.IBinder.DeathRecipient
                        public final void binderDied() {
                            StatsPullAtomService.this.lambda$getIProcessStatsService$6();
                        }
                    }, 0);
                } catch (RemoteException e) {
                    Slog.e("StatsPullAtomService", "linkToDeath with ProcessStats failed", e);
                    this.mProcessStatsService = null;
                }
            }
        }
        return this.mProcessStatsService;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getIProcessStatsService$6() {
        synchronized (this.mProcStatsLock) {
            this.mProcessStatsService = null;
        }
    }

    public final void registerWifiBytesTransfer() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.WIFI_BYTES_TRANSFER, new StatsManager.PullAtomMetadata.Builder().setAdditiveFields(new int[]{2, 3, 4, 5}).build(), ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public final List<NetworkStatsExt> collectNetworkStatsSnapshotForAtom(int i) {
        ArrayList arrayList = new ArrayList();
        if (i == 10082) {
            Iterator<SubInfo> it = this.mHistoricalSubs.iterator();
            while (it.hasNext()) {
                arrayList.addAll(getDataUsageBytesTransferSnapshotForSub(it.next()));
            }
        } else if (i == 10083) {
            NetworkStats uidNetworkStatsSnapshotForTemplate = getUidNetworkStatsSnapshotForTemplate(new NetworkTemplate.Builder(4).build(), true);
            NetworkStats uidNetworkStatsSnapshotForTemplate2 = getUidNetworkStatsSnapshotForTemplate(new NetworkTemplate.Builder(1).setMeteredness(1).build(), true);
            if (uidNetworkStatsSnapshotForTemplate != null && uidNetworkStatsSnapshotForTemplate2 != null) {
                arrayList.add(new NetworkStatsExt(sliceNetworkStatsByUidTagAndMetered(uidNetworkStatsSnapshotForTemplate.add(uidNetworkStatsSnapshotForTemplate2)), new int[]{1, 0}, false, true, true, 0, null, -1));
            }
        } else if (i != 10100) {
            switch (i) {
                case FrameworkStatsLog.WIFI_BYTES_TRANSFER /* 10000 */:
                    NetworkStats uidNetworkStatsSnapshotForTransport = getUidNetworkStatsSnapshotForTransport(1);
                    if (uidNetworkStatsSnapshotForTransport != null) {
                        arrayList.add(new NetworkStatsExt(sliceNetworkStatsByUid(uidNetworkStatsSnapshotForTransport), new int[]{1}, false));
                        break;
                    }
                    break;
                case FrameworkStatsLog.WIFI_BYTES_TRANSFER_BY_FG_BG /* 10001 */:
                    NetworkStats uidNetworkStatsSnapshotForTransport2 = getUidNetworkStatsSnapshotForTransport(1);
                    if (uidNetworkStatsSnapshotForTransport2 != null) {
                        arrayList.add(new NetworkStatsExt(sliceNetworkStatsByUidAndFgbg(uidNetworkStatsSnapshotForTransport2), new int[]{1}, true));
                        break;
                    }
                    break;
                case FrameworkStatsLog.MOBILE_BYTES_TRANSFER /* 10002 */:
                    NetworkStats uidNetworkStatsSnapshotForTransport3 = getUidNetworkStatsSnapshotForTransport(0);
                    if (uidNetworkStatsSnapshotForTransport3 != null) {
                        arrayList.add(new NetworkStatsExt(sliceNetworkStatsByUid(uidNetworkStatsSnapshotForTransport3), new int[]{0}, false));
                        break;
                    }
                    break;
                case FrameworkStatsLog.MOBILE_BYTES_TRANSFER_BY_FG_BG /* 10003 */:
                    NetworkStats uidNetworkStatsSnapshotForTransport4 = getUidNetworkStatsSnapshotForTransport(0);
                    if (uidNetworkStatsSnapshotForTransport4 != null) {
                        arrayList.add(new NetworkStatsExt(sliceNetworkStatsByUidAndFgbg(uidNetworkStatsSnapshotForTransport4), new int[]{0}, true));
                        break;
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unknown atomTag " + i);
            }
        } else {
            arrayList.addAll(getDataUsageBytesTransferSnapshotForOemManaged());
        }
        return arrayList;
    }

    public final int pullDataBytesTransferLocked(int i, List<StatsEvent> list) {
        List<NetworkStatsExt> collectNetworkStatsSnapshotForAtom = collectNetworkStatsSnapshotForAtom(i);
        int i2 = 1;
        if (collectNetworkStatsSnapshotForAtom == null) {
            Slog.e("StatsPullAtomService", "current snapshot is null for " + i + ", return.");
            return 1;
        }
        for (final NetworkStatsExt networkStatsExt : collectNetworkStatsSnapshotForAtom) {
            NetworkStatsExt networkStatsExt2 = (NetworkStatsExt) CollectionUtils.find(this.mNetworkStatsBaselines, new Predicate() { // from class: com.android.server.stats.pull.StatsPullAtomService$$ExternalSyntheticLambda8
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean hasSameSlicing;
                    hasSameSlicing = ((NetworkStatsExt) obj).hasSameSlicing(NetworkStatsExt.this);
                    return hasSameSlicing;
                }
            });
            if (networkStatsExt2 == null) {
                Slog.e("StatsPullAtomService", "baseline is null for " + i + ", return.");
                return i2;
            }
            NetworkStatsExt networkStatsExt3 = new NetworkStatsExt(removeEmptyEntries(networkStatsExt.stats.subtract(networkStatsExt2.stats)), networkStatsExt.transports, networkStatsExt.slicedByFgbg, networkStatsExt.slicedByTag, networkStatsExt.slicedByMetered, networkStatsExt.ratType, networkStatsExt.subInfo, networkStatsExt.oemManaged);
            if (networkStatsExt3.stats.iterator().hasNext()) {
                if (i == 10082) {
                    addDataUsageBytesTransferAtoms(networkStatsExt3, list);
                } else if (i == 10083) {
                    addBytesTransferByTagAndMeteredAtoms(networkStatsExt3, list);
                } else if (i == 10100) {
                    addOemDataUsageBytesTransferAtoms(networkStatsExt3, list);
                } else {
                    addNetworkStats(i, list, networkStatsExt3);
                }
            }
            i2 = 1;
        }
        return 0;
    }

    public static NetworkStats removeEmptyEntries(NetworkStats networkStats) {
        NetworkStats networkStats2 = new NetworkStats(0L, 1);
        Iterator it = networkStats.iterator();
        while (it.hasNext()) {
            NetworkStats.Entry entry = (NetworkStats.Entry) it.next();
            if (entry.getRxBytes() != 0 || entry.getRxPackets() != 0 || entry.getTxBytes() != 0 || entry.getTxPackets() != 0 || entry.getOperations() != 0) {
                networkStats2 = networkStats2.addEntry(entry);
            }
        }
        return networkStats2;
    }

    public final void addNetworkStats(int i, List<StatsEvent> list, NetworkStatsExt networkStatsExt) {
        StatsEvent buildStatsEvent;
        Iterator it = networkStatsExt.stats.iterator();
        while (it.hasNext()) {
            NetworkStats.Entry entry = (NetworkStats.Entry) it.next();
            if (networkStatsExt.slicedByFgbg) {
                buildStatsEvent = FrameworkStatsLog.buildStatsEvent(i, entry.getUid(), entry.getSet() > 0, entry.getRxBytes(), entry.getRxPackets(), entry.getTxBytes(), entry.getTxPackets());
            } else {
                buildStatsEvent = FrameworkStatsLog.buildStatsEvent(i, entry.getUid(), entry.getRxBytes(), entry.getRxPackets(), entry.getTxBytes(), entry.getTxPackets());
            }
            list.add(buildStatsEvent);
        }
    }

    public final void addBytesTransferByTagAndMeteredAtoms(NetworkStatsExt networkStatsExt, List<StatsEvent> list) {
        Iterator it = networkStatsExt.stats.iterator();
        while (it.hasNext()) {
            NetworkStats.Entry entry = (NetworkStats.Entry) it.next();
            list.add(FrameworkStatsLog.buildStatsEvent((int) FrameworkStatsLog.BYTES_TRANSFER_BY_TAG_AND_METERED, entry.getUid(), entry.getMetered() == 1, entry.getTag(), entry.getRxBytes(), entry.getRxPackets(), entry.getTxBytes(), entry.getTxPackets()));
        }
    }

    public final void addDataUsageBytesTransferAtoms(NetworkStatsExt networkStatsExt, List<StatsEvent> list) {
        int i = networkStatsExt.ratType;
        boolean z = true;
        boolean z2 = i == -2;
        if (!z2 && i != 20) {
            z = false;
        }
        Iterator it = networkStatsExt.stats.iterator();
        while (it.hasNext()) {
            NetworkStats.Entry entry = (NetworkStats.Entry) it.next();
            int set = entry.getSet();
            long rxBytes = entry.getRxBytes();
            long rxPackets = entry.getRxPackets();
            long txBytes = entry.getTxBytes();
            long txPackets = entry.getTxPackets();
            int i2 = z2 ? 13 : networkStatsExt.ratType;
            SubInfo subInfo = networkStatsExt.subInfo;
            list.add(FrameworkStatsLog.buildStatsEvent((int) FrameworkStatsLog.DATA_USAGE_BYTES_TRANSFER, set, rxBytes, rxPackets, txBytes, txPackets, i2, subInfo.mcc, subInfo.mnc, subInfo.carrierId, subInfo.isOpportunistic ? 2 : 3, z));
        }
    }

    public final void addOemDataUsageBytesTransferAtoms(NetworkStatsExt networkStatsExt, List<StatsEvent> list) {
        int i = networkStatsExt.oemManaged;
        int[] iArr = networkStatsExt.transports;
        int length = iArr.length;
        int i2 = 0;
        while (i2 < length) {
            int i3 = iArr[i2];
            Iterator it = networkStatsExt.stats.iterator();
            while (it.hasNext()) {
                NetworkStats.Entry entry = (NetworkStats.Entry) it.next();
                list.add(FrameworkStatsLog.buildStatsEvent(FrameworkStatsLog.OEM_MANAGED_BYTES_TRANSFER, entry.getUid(), entry.getSet() > 0, i, i3, entry.getRxBytes(), entry.getRxPackets(), entry.getTxBytes(), entry.getTxPackets()));
                length = length;
                i2 = i2;
            }
            i2++;
        }
    }

    public final List<NetworkStatsExt> getDataUsageBytesTransferSnapshotForOemManaged() {
        int i = 3;
        List<Pair> of = List.of(new Pair(5, 3), new Pair(1, 0), new Pair(4, 1));
        int[] iArr = {3, 1, 2};
        ArrayList arrayList = new ArrayList();
        for (Pair pair : of) {
            Integer num = (Integer) pair.first;
            int i2 = 0;
            while (i2 < i) {
                int i3 = iArr[i2];
                NetworkStats uidNetworkStatsSnapshotForTemplate = getUidNetworkStatsSnapshotForTemplate(new NetworkTemplate.Builder(num.intValue()).setOemManaged(i3).build(), false);
                Integer num2 = (Integer) pair.second;
                if (uidNetworkStatsSnapshotForTemplate != null) {
                    arrayList.add(new NetworkStatsExt(sliceNetworkStatsByUidAndFgbg(uidNetworkStatsSnapshotForTemplate), new int[]{num2.intValue()}, true, false, false, 0, null, i3));
                }
                i2++;
                i = 3;
            }
        }
        return arrayList;
    }

    public final NetworkStats getUidNetworkStatsSnapshotForTransport(int i) {
        NetworkTemplate build;
        if (i == 0) {
            build = new NetworkTemplate.Builder(1).setMeteredness(1).build();
        } else if (i == 1) {
            build = new NetworkTemplate.Builder(4).build();
        } else {
            Log.wtf("StatsPullAtomService", "Unexpected transport.");
            build = null;
        }
        return getUidNetworkStatsSnapshotForTemplate(build, false);
    }

    public final NetworkStats getUidNetworkStatsSnapshotForTemplate(NetworkTemplate networkTemplate, boolean z) {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long millis = TimeUnit.MICROSECONDS.toMillis(SystemClock.currentTimeMicro());
        long j = Settings.Global.getLong(this.mContext.getContentResolver(), "netstats_uid_bucket_duration", NETSTATS_UID_DEFAULT_BUCKET_DURATION_MS);
        if (networkTemplate.getMatchRule() == 4 && networkTemplate.getSubscriberIds().isEmpty()) {
            this.mNetworkStatsManager.forceUpdate();
        }
        long j2 = (millis - elapsedRealtime) - j;
        NetworkStats fromPublicNetworkStats = NetworkStatsUtils.fromPublicNetworkStats(this.mNetworkStatsManager.querySummary(networkTemplate, j2, millis));
        return !z ? fromPublicNetworkStats : fromPublicNetworkStats.add(NetworkStatsUtils.fromPublicNetworkStats(this.mNetworkStatsManager.queryTaggedSummary(networkTemplate, j2, millis)));
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r4v0 */
    /* JADX WARN: Type inference failed for: r4v2 */
    public final List<NetworkStatsExt> getDataUsageBytesTransferSnapshotForSub(SubInfo subInfo) {
        ArrayList arrayList = new ArrayList();
        int[] allCollapsedRatTypes = getAllCollapsedRatTypes();
        int length = allCollapsedRatTypes.length;
        int i = 0;
        int i2 = 0;
        while (i2 < length) {
            int i3 = allCollapsedRatTypes[i2];
            NetworkStats uidNetworkStatsSnapshotForTemplate = getUidNetworkStatsSnapshotForTemplate(new NetworkTemplate.Builder(1).setSubscriberIds(Set.of(subInfo.subscriberId)).setRatType(i3).setMeteredness(1).build(), i);
            if (uidNetworkStatsSnapshotForTemplate != null) {
                arrayList.add(new NetworkStatsExt(sliceNetworkStatsByFgbg(uidNetworkStatsSnapshotForTemplate), new int[]{i}, true, false, false, i3, subInfo, -1));
            }
            i2++;
            i = 0;
        }
        return arrayList;
    }

    public static int[] getAllCollapsedRatTypes() {
        int[] allNetworkTypes = TelephonyManager.getAllNetworkTypes();
        HashSet hashSet = new HashSet();
        for (int i : allNetworkTypes) {
            hashSet.add(Integer.valueOf(NetworkStatsManager.getCollapsedRatType(i)));
        }
        hashSet.add(Integer.valueOf(NetworkStatsManager.getCollapsedRatType(-2)));
        hashSet.add(0);
        return com.android.net.module.util.CollectionUtils.toIntArray(hashSet);
    }

    public final NetworkStats sliceNetworkStatsByUid(NetworkStats networkStats) {
        return sliceNetworkStats(networkStats, new Function() { // from class: com.android.server.stats.pull.StatsPullAtomService$$ExternalSyntheticLambda4
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                NetworkStats.Entry lambda$sliceNetworkStatsByUid$8;
                lambda$sliceNetworkStatsByUid$8 = StatsPullAtomService.lambda$sliceNetworkStatsByUid$8((NetworkStats.Entry) obj);
                return lambda$sliceNetworkStatsByUid$8;
            }
        });
    }

    public static /* synthetic */ NetworkStats.Entry lambda$sliceNetworkStatsByUid$8(NetworkStats.Entry entry) {
        return new NetworkStats.Entry((String) null, entry.getUid(), -1, 0, -1, -1, -1, entry.getRxBytes(), entry.getRxPackets(), entry.getTxBytes(), entry.getTxPackets(), 0L);
    }

    public final NetworkStats sliceNetworkStatsByFgbg(NetworkStats networkStats) {
        return sliceNetworkStats(networkStats, new Function() { // from class: com.android.server.stats.pull.StatsPullAtomService$$ExternalSyntheticLambda2
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                NetworkStats.Entry lambda$sliceNetworkStatsByFgbg$9;
                lambda$sliceNetworkStatsByFgbg$9 = StatsPullAtomService.lambda$sliceNetworkStatsByFgbg$9((NetworkStats.Entry) obj);
                return lambda$sliceNetworkStatsByFgbg$9;
            }
        });
    }

    public static /* synthetic */ NetworkStats.Entry lambda$sliceNetworkStatsByFgbg$9(NetworkStats.Entry entry) {
        return new NetworkStats.Entry((String) null, -1, entry.getSet(), 0, -1, -1, -1, entry.getRxBytes(), entry.getRxPackets(), entry.getTxBytes(), entry.getTxPackets(), 0L);
    }

    public final NetworkStats sliceNetworkStatsByUidAndFgbg(NetworkStats networkStats) {
        return sliceNetworkStats(networkStats, new Function() { // from class: com.android.server.stats.pull.StatsPullAtomService$$ExternalSyntheticLambda3
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                NetworkStats.Entry lambda$sliceNetworkStatsByUidAndFgbg$10;
                lambda$sliceNetworkStatsByUidAndFgbg$10 = StatsPullAtomService.lambda$sliceNetworkStatsByUidAndFgbg$10((NetworkStats.Entry) obj);
                return lambda$sliceNetworkStatsByUidAndFgbg$10;
            }
        });
    }

    public static /* synthetic */ NetworkStats.Entry lambda$sliceNetworkStatsByUidAndFgbg$10(NetworkStats.Entry entry) {
        return new NetworkStats.Entry((String) null, entry.getUid(), entry.getSet(), 0, -1, -1, -1, entry.getRxBytes(), entry.getRxPackets(), entry.getTxBytes(), entry.getTxPackets(), 0L);
    }

    public final NetworkStats sliceNetworkStatsByUidTagAndMetered(NetworkStats networkStats) {
        return sliceNetworkStats(networkStats, new Function() { // from class: com.android.server.stats.pull.StatsPullAtomService$$ExternalSyntheticLambda5
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                NetworkStats.Entry lambda$sliceNetworkStatsByUidTagAndMetered$11;
                lambda$sliceNetworkStatsByUidTagAndMetered$11 = StatsPullAtomService.lambda$sliceNetworkStatsByUidTagAndMetered$11((NetworkStats.Entry) obj);
                return lambda$sliceNetworkStatsByUidTagAndMetered$11;
            }
        });
    }

    public static /* synthetic */ NetworkStats.Entry lambda$sliceNetworkStatsByUidTagAndMetered$11(NetworkStats.Entry entry) {
        return new NetworkStats.Entry((String) null, entry.getUid(), -1, entry.getTag(), entry.getMetered(), -1, -1, entry.getRxBytes(), entry.getRxPackets(), entry.getTxBytes(), entry.getTxPackets(), 0L);
    }

    public final NetworkStats sliceNetworkStats(NetworkStats networkStats, Function<NetworkStats.Entry, NetworkStats.Entry> function) {
        NetworkStats networkStats2 = new NetworkStats(0L, 1);
        Iterator it = networkStats.iterator();
        while (it.hasNext()) {
            networkStats2 = networkStats2.addEntry(function.apply((NetworkStats.Entry) it.next()));
        }
        return networkStats2;
    }

    public final void registerWifiBytesTransferBackground() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.WIFI_BYTES_TRANSFER_BY_FG_BG, new StatsManager.PullAtomMetadata.Builder().setAdditiveFields(new int[]{3, 4, 5, 6}).build(), ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public final void registerMobileBytesTransfer() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.MOBILE_BYTES_TRANSFER, new StatsManager.PullAtomMetadata.Builder().setAdditiveFields(new int[]{2, 3, 4, 5}).build(), ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public final void registerMobileBytesTransferBackground() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.MOBILE_BYTES_TRANSFER_BY_FG_BG, new StatsManager.PullAtomMetadata.Builder().setAdditiveFields(new int[]{3, 4, 5, 6}).build(), ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public final void registerBytesTransferByTagAndMetered() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.BYTES_TRANSFER_BY_TAG_AND_METERED, new StatsManager.PullAtomMetadata.Builder().setAdditiveFields(new int[]{4, 5, 6, 7}).build(), BackgroundThread.getExecutor(), this.mStatsCallbackImpl);
    }

    public final void registerDataUsageBytesTransfer() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.DATA_USAGE_BYTES_TRANSFER, new StatsManager.PullAtomMetadata.Builder().setAdditiveFields(new int[]{2, 3, 4, 5}).build(), BackgroundThread.getExecutor(), this.mStatsCallbackImpl);
    }

    public final void registerOemManagedBytesTransfer() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.OEM_MANAGED_BYTES_TRANSFER, new StatsManager.PullAtomMetadata.Builder().setAdditiveFields(new int[]{5, 6, 7, 8}).build(), BackgroundThread.getExecutor(), this.mStatsCallbackImpl);
    }

    public final void registerBluetoothBytesTransfer() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.BLUETOOTH_BYTES_TRANSFER, new StatsManager.PullAtomMetadata.Builder().setAdditiveFields(new int[]{2, 3}).build(), ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public static <T extends Parcelable> T awaitControllerInfo(SynchronousResultReceiver synchronousResultReceiver) {
        if (synchronousResultReceiver == null) {
            return null;
        }
        try {
            SynchronousResultReceiver.Result awaitResult = synchronousResultReceiver.awaitResult(2000L);
            Bundle bundle = awaitResult.bundle;
            if (bundle != null) {
                bundle.setDefusable(true);
                T t = (T) awaitResult.bundle.getParcelable("controller_activity");
                if (t != null) {
                    return t;
                }
            }
        } catch (TimeoutException unused) {
            Slog.w("StatsPullAtomService", "timeout reading " + synchronousResultReceiver.getName() + " stats");
        }
        return null;
    }

    public final BluetoothActivityEnergyInfo fetchBluetoothData() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null) {
            final SynchronousResultReceiver synchronousResultReceiver = new SynchronousResultReceiver("bluetooth");
            defaultAdapter.requestControllerActivityEnergyInfo(new SystemServerInitThreadPool$$ExternalSyntheticLambda0(), new BluetoothAdapter.OnBluetoothActivityEnergyInfoCallback() { // from class: com.android.server.stats.pull.StatsPullAtomService.1
                public void onBluetoothActivityEnergyInfoAvailable(BluetoothActivityEnergyInfo bluetoothActivityEnergyInfo) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("controller_activity", bluetoothActivityEnergyInfo);
                    synchronousResultReceiver.send(0, bundle);
                }

                public void onBluetoothActivityEnergyInfoError(int i) {
                    Slog.w("StatsPullAtomService", "error reading Bluetooth stats: " + i);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("controller_activity", null);
                    synchronousResultReceiver.send(0, bundle);
                }
            });
            return awaitControllerInfo(synchronousResultReceiver);
        }
        Slog.e("StatsPullAtomService", "Failed to get bluetooth adapter!");
        return null;
    }

    public int pullBluetoothBytesTransferLocked(int i, List<StatsEvent> list) {
        BluetoothActivityEnergyInfo fetchBluetoothData = fetchBluetoothData();
        if (fetchBluetoothData == null) {
            return 1;
        }
        for (UidTraffic uidTraffic : fetchBluetoothData.getUidTraffic()) {
            list.add(FrameworkStatsLog.buildStatsEvent(i, uidTraffic.getUid(), uidTraffic.getRxBytes(), uidTraffic.getTxBytes()));
        }
        return 0;
    }

    public final void registerKernelWakelock() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.KERNEL_WAKELOCK, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullKernelWakelockLocked(int i, List<StatsEvent> list) {
        for (Map.Entry<String, KernelWakelockStats.Entry> entry : this.mKernelWakelockReader.readKernelWakelockStats(this.mTmpWakelockStats).entrySet()) {
            KernelWakelockStats.Entry value = entry.getValue();
            list.add(FrameworkStatsLog.buildStatsEvent(i, entry.getKey(), value.mCount, value.mVersion, value.mTotalTime));
        }
        return 0;
    }

    public final void registerCpuTimePerClusterFreq() {
        if (KernelCpuBpfTracking.isSupported()) {
            this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.CPU_TIME_PER_CLUSTER_FREQ, new StatsManager.PullAtomMetadata.Builder().setAdditiveFields(new int[]{3}).build(), ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
        }
    }

    public int pullCpuTimePerClusterFreqLocked(int i, List<StatsEvent> list) {
        int[] freqsClusters = KernelCpuBpfTracking.getFreqsClusters();
        long[] freqs = KernelCpuBpfTracking.getFreqs();
        long[] read = KernelCpuTotalBpfMapReader.read();
        if (read == null) {
            return 1;
        }
        for (int i2 = 0; i2 < read.length; i2++) {
            list.add(FrameworkStatsLog.buildStatsEvent(i, freqsClusters[i2], (int) freqs[i2], read[i2]));
        }
        return 0;
    }

    public final void registerCpuTimePerUid() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.CPU_TIME_PER_UID, new StatsManager.PullAtomMetadata.Builder().setAdditiveFields(new int[]{2, 3}).build(), ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullCpuTimePerUidLocked(final int i, final List<StatsEvent> list) {
        this.mCpuUidUserSysTimeReader.readAbsolute(new KernelCpuUidTimeReader.Callback() { // from class: com.android.server.stats.pull.StatsPullAtomService$$ExternalSyntheticLambda22
            public final void onUidCpuTime(int i2, Object obj) {
                StatsPullAtomService.lambda$pullCpuTimePerUidLocked$12(list, i, i2, (long[]) obj);
            }
        });
        return 0;
    }

    public static /* synthetic */ void lambda$pullCpuTimePerUidLocked$12(List list, int i, int i2, long[] jArr) {
        list.add(FrameworkStatsLog.buildStatsEvent(i, i2, jArr[0], jArr[1]));
    }

    public final void registerCpuCyclesPerUidCluster() {
        if (KernelCpuBpfTracking.isSupported() || KernelCpuBpfTracking.getClusters() > 0) {
            this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.CPU_CYCLES_PER_UID_CLUSTER, new StatsManager.PullAtomMetadata.Builder().setAdditiveFields(new int[]{3, 4, 5}).build(), ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
        }
    }

    public int pullCpuCyclesPerUidClusterLocked(int i, List<StatsEvent> list) {
        PowerProfile powerProfile = new PowerProfile(this.mContext);
        final int[] freqsClusters = KernelCpuBpfTracking.getFreqsClusters();
        final int clusters = KernelCpuBpfTracking.getClusters();
        final long[] freqs = KernelCpuBpfTracking.getFreqs();
        final double[] dArr = new double[freqs.length];
        int i2 = -1;
        int i3 = 0;
        int i4 = 0;
        while (i3 < freqs.length) {
            int i5 = freqsClusters[i3];
            if (i5 != i2) {
                i4 = 0;
            }
            dArr[i3] = powerProfile.getAveragePowerForCpuCore(i5, i4);
            i3++;
            i4++;
            i2 = i5;
        }
        final SparseArray sparseArray = new SparseArray();
        this.mCpuUidFreqTimeReader.readAbsolute(new KernelCpuUidTimeReader.Callback() { // from class: com.android.server.stats.pull.StatsPullAtomService$$ExternalSyntheticLambda16
            public final void onUidCpuTime(int i6, Object obj) {
                StatsPullAtomService.lambda$pullCpuCyclesPerUidClusterLocked$13(sparseArray, clusters, freqsClusters, freqs, dArr, i6, (long[]) obj);
            }
        });
        int size = sparseArray.size();
        for (int i6 = 0; i6 < size; i6++) {
            int keyAt = sparseArray.keyAt(i6);
            double[] dArr2 = (double[]) sparseArray.valueAt(i6);
            for (int i7 = 0; i7 < clusters; i7++) {
                int i8 = i7 * 3;
                list.add(FrameworkStatsLog.buildStatsEvent(i, keyAt, i7, (long) (dArr2[i8] / 1000000.0d), (long) dArr2[i8 + 1], (long) (dArr2[i8 + 2] / 1000.0d)));
            }
        }
        return 0;
    }

    public static /* synthetic */ void lambda$pullCpuCyclesPerUidClusterLocked$13(SparseArray sparseArray, int i, int[] iArr, long[] jArr, double[] dArr, int i2, long[] jArr2) {
        if (UserHandle.isIsolated(i2)) {
            return;
        }
        int appId = UserHandle.isSharedAppGid(i2) ? 59999 : UserHandle.getAppId(i2);
        double[] dArr2 = (double[]) sparseArray.get(appId);
        if (dArr2 == null) {
            dArr2 = new double[i * 3];
            sparseArray.put(appId, dArr2);
        }
        for (int i3 = 0; i3 < jArr2.length; i3++) {
            int i4 = iArr[i3];
            long j = jArr2[i3];
            int i5 = i4 * 3;
            dArr2[i5] = dArr2[i5] + (jArr[i3] * j);
            int i6 = i5 + 1;
            double d = j;
            dArr2[i6] = dArr2[i6] + d;
            int i7 = i5 + 2;
            dArr2[i7] = dArr2[i7] + (dArr[i3] * d);
        }
    }

    public final void registerCpuTimePerUidFreq() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.CPU_TIME_PER_UID_FREQ, new StatsManager.PullAtomMetadata.Builder().setAdditiveFields(new int[]{3}).build(), ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullCpuTimePerUidFreqLocked(int i, List<StatsEvent> list) {
        final SparseArray sparseArray = new SparseArray();
        this.mCpuUidFreqTimeReader.readAbsolute(new KernelCpuUidTimeReader.Callback() { // from class: com.android.server.stats.pull.StatsPullAtomService$$ExternalSyntheticLambda15
            public final void onUidCpuTime(int i2, Object obj) {
                StatsPullAtomService.lambda$pullCpuTimePerUidFreqLocked$14(sparseArray, i2, (long[]) obj);
            }
        });
        int size = sparseArray.size();
        for (int i2 = 0; i2 < size; i2++) {
            int keyAt = sparseArray.keyAt(i2);
            long[] jArr = (long[]) sparseArray.valueAt(i2);
            for (int i3 = 0; i3 < jArr.length; i3++) {
                long j = jArr[i3];
                if (j >= 10) {
                    list.add(FrameworkStatsLog.buildStatsEvent(i, keyAt, i3, j));
                }
            }
        }
        return 0;
    }

    public static /* synthetic */ void lambda$pullCpuTimePerUidFreqLocked$14(SparseArray sparseArray, int i, long[] jArr) {
        if (UserHandle.isIsolated(i)) {
            return;
        }
        int appId = UserHandle.isSharedAppGid(i) ? 59999 : UserHandle.getAppId(i);
        long[] jArr2 = (long[]) sparseArray.get(appId);
        if (jArr2 == null) {
            jArr2 = new long[jArr.length];
            sparseArray.put(appId, jArr2);
        }
        for (int i2 = 0; i2 < jArr.length; i2++) {
            jArr2[i2] = jArr2[i2] + jArr[i2];
        }
    }

    public final void registerCpuCyclesPerThreadGroupCluster() {
        if (KernelCpuBpfTracking.isSupported()) {
            this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.CPU_CYCLES_PER_THREAD_GROUP_CLUSTER, new StatsManager.PullAtomMetadata.Builder().setAdditiveFields(new int[]{3, 4}).build(), ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
        }
    }

    public int pullCpuCyclesPerThreadGroupCluster(int i, List<StatsEvent> list) {
        long[] jArr;
        SystemServerCpuThreadReader.SystemServiceCpuThreadTimes systemServiceCpuThreadTimes = ((BatteryStatsInternal) LocalServices.getService(BatteryStatsInternal.class)).getSystemServiceCpuThreadTimes();
        if (systemServiceCpuThreadTimes == null) {
            return 1;
        }
        addCpuCyclesPerThreadGroupClusterAtoms(i, list, 2, systemServiceCpuThreadTimes.threadCpuTimesUs);
        addCpuCyclesPerThreadGroupClusterAtoms(i, list, 1, systemServiceCpuThreadTimes.binderThreadCpuTimesUs);
        KernelSingleProcessCpuThreadReader.ProcessCpuUsage readAbsolute = this.mSurfaceFlingerProcessCpuThreadReader.readAbsolute();
        if (readAbsolute != null && (jArr = readAbsolute.threadCpuTimesMillis) != null) {
            int length = jArr.length;
            long[] jArr2 = new long[length];
            for (int i2 = 0; i2 < length; i2++) {
                jArr2[i2] = readAbsolute.threadCpuTimesMillis[i2] * 1000;
            }
            addCpuCyclesPerThreadGroupClusterAtoms(i, list, 3, jArr2);
        }
        return 0;
    }

    public static void addCpuCyclesPerThreadGroupClusterAtoms(int i, List<StatsEvent> list, int i2, long[] jArr) {
        int[] freqsClusters = KernelCpuBpfTracking.getFreqsClusters();
        int clusters = KernelCpuBpfTracking.getClusters();
        long[] freqs = KernelCpuBpfTracking.getFreqs();
        long[] jArr2 = new long[clusters];
        long[] jArr3 = new long[clusters];
        for (int i3 = 0; i3 < jArr.length; i3++) {
            int i4 = freqsClusters[i3];
            jArr2[i4] = jArr2[i4] + ((freqs[i3] * jArr[i3]) / 1000);
            jArr3[i4] = jArr3[i4] + jArr[i3];
        }
        for (int i5 = 0; i5 < clusters; i5++) {
            list.add(FrameworkStatsLog.buildStatsEvent(i, i2, i5, jArr2[i5] / 1000000, jArr3[i5] / 1000));
        }
    }

    public final void registerCpuActiveTime() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.CPU_ACTIVE_TIME, new StatsManager.PullAtomMetadata.Builder().setAdditiveFields(new int[]{2}).build(), ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullCpuActiveTimeLocked(final int i, final List<StatsEvent> list) {
        this.mCpuUidActiveTimeReader.readAbsolute(new KernelCpuUidTimeReader.Callback() { // from class: com.android.server.stats.pull.StatsPullAtomService$$ExternalSyntheticLambda14
            public final void onUidCpuTime(int i2, Object obj) {
                StatsPullAtomService.lambda$pullCpuActiveTimeLocked$15(list, i, i2, (Long) obj);
            }
        });
        return 0;
    }

    public static /* synthetic */ void lambda$pullCpuActiveTimeLocked$15(List list, int i, int i2, Long l) {
        list.add(FrameworkStatsLog.buildStatsEvent(i, i2, l.longValue()));
    }

    public final void registerCpuClusterTime() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.CPU_CLUSTER_TIME, new StatsManager.PullAtomMetadata.Builder().setAdditiveFields(new int[]{3}).build(), ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullCpuClusterTimeLocked(final int i, final List<StatsEvent> list) {
        this.mCpuUidClusterTimeReader.readAbsolute(new KernelCpuUidTimeReader.Callback() { // from class: com.android.server.stats.pull.StatsPullAtomService$$ExternalSyntheticLambda25
            public final void onUidCpuTime(int i2, Object obj) {
                StatsPullAtomService.lambda$pullCpuClusterTimeLocked$16(list, i, i2, (long[]) obj);
            }
        });
        return 0;
    }

    public static /* synthetic */ void lambda$pullCpuClusterTimeLocked$16(List list, int i, int i2, long[] jArr) {
        for (int i3 = 0; i3 < jArr.length; i3++) {
            list.add(FrameworkStatsLog.buildStatsEvent(i, i2, i3, jArr[i3]));
        }
    }

    public final void registerWifiActivityInfo() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.WIFI_ACTIVITY_INFO, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullWifiActivityInfoLocked(int i, List<StatsEvent> list) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            final SynchronousResultReceiver synchronousResultReceiver = new SynchronousResultReceiver("wifi");
            this.mWifiManager.getWifiActivityEnergyInfoAsync(new Executor() { // from class: com.android.server.stats.pull.StatsPullAtomService.2
                @Override // java.util.concurrent.Executor
                public void execute(Runnable runnable) {
                    runnable.run();
                }
            }, new WifiManager.OnWifiActivityEnergyInfoListener() { // from class: com.android.server.stats.pull.StatsPullAtomService$$ExternalSyntheticLambda26
                public final void onWifiActivityEnergyInfo(WifiActivityEnergyInfo wifiActivityEnergyInfo) {
                    StatsPullAtomService.lambda$pullWifiActivityInfoLocked$17(synchronousResultReceiver, wifiActivityEnergyInfo);
                }
            });
            WifiActivityEnergyInfo awaitControllerInfo = awaitControllerInfo(synchronousResultReceiver);
            if (awaitControllerInfo == null) {
                return 1;
            }
            list.add(FrameworkStatsLog.buildStatsEvent(i, awaitControllerInfo.getTimeSinceBootMillis(), awaitControllerInfo.getStackState(), awaitControllerInfo.getControllerTxDurationMillis(), awaitControllerInfo.getControllerRxDurationMillis(), awaitControllerInfo.getControllerIdleDurationMillis(), awaitControllerInfo.getControllerEnergyUsedMicroJoules()));
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return 0;
        } catch (RuntimeException e) {
            Slog.e("StatsPullAtomService", "failed to getWifiActivityEnergyInfoAsync", e);
            return 1;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public static /* synthetic */ void lambda$pullWifiActivityInfoLocked$17(SynchronousResultReceiver synchronousResultReceiver, WifiActivityEnergyInfo wifiActivityEnergyInfo) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("controller_activity", wifiActivityEnergyInfo);
        synchronousResultReceiver.send(0, bundle);
    }

    public final void registerModemActivityInfo() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.MODEM_ACTIVITY_INFO, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullModemActivityInfoLocked(int i, List<StatsEvent> list) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            final CompletableFuture completableFuture = new CompletableFuture();
            this.mTelephony.requestModemActivityInfo(new SystemServerInitThreadPool$$ExternalSyntheticLambda0(), new OutcomeReceiver<ModemActivityInfo, TelephonyManager.ModemActivityInfoException>() { // from class: com.android.server.stats.pull.StatsPullAtomService.3
                @Override // android.os.OutcomeReceiver
                public void onResult(ModemActivityInfo modemActivityInfo) {
                    completableFuture.complete(modemActivityInfo);
                }

                @Override // android.os.OutcomeReceiver
                public void onError(TelephonyManager.ModemActivityInfoException modemActivityInfoException) {
                    Slog.w("StatsPullAtomService", "error reading modem stats:" + modemActivityInfoException);
                    completableFuture.complete(null);
                }
            });
            ModemActivityInfo modemActivityInfo = (ModemActivityInfo) completableFuture.get(2000L, TimeUnit.MILLISECONDS);
            if (modemActivityInfo == null) {
                return 1;
            }
            list.add(FrameworkStatsLog.buildStatsEvent(i, modemActivityInfo.getTimestampMillis(), modemActivityInfo.getSleepTimeMillis(), modemActivityInfo.getIdleTimeMillis(), modemActivityInfo.getTransmitDurationMillisAtPowerLevel(0), modemActivityInfo.getTransmitDurationMillisAtPowerLevel(1), modemActivityInfo.getTransmitDurationMillisAtPowerLevel(2), modemActivityInfo.getTransmitDurationMillisAtPowerLevel(3), modemActivityInfo.getTransmitDurationMillisAtPowerLevel(4), modemActivityInfo.getReceiveTimeMillis(), -1L));
            return 0;
        } catch (InterruptedException | TimeoutException e) {
            Slog.w("StatsPullAtomService", "timeout or interrupt reading modem stats: " + e);
            return 1;
        } catch (ExecutionException e2) {
            Slog.w("StatsPullAtomService", "exception reading modem stats: " + e2.getCause());
            return 1;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final void registerBluetoothActivityInfo() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.BLUETOOTH_ACTIVITY_INFO, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullBluetoothActivityInfoLocked(int i, List<StatsEvent> list) {
        BluetoothActivityEnergyInfo fetchBluetoothData = fetchBluetoothData();
        if (fetchBluetoothData == null) {
            return 1;
        }
        list.add(FrameworkStatsLog.buildStatsEvent(i, fetchBluetoothData.getTimestampMillis(), fetchBluetoothData.getBluetoothStackState(), fetchBluetoothData.getControllerTxTimeMillis(), fetchBluetoothData.getControllerRxTimeMillis(), fetchBluetoothData.getControllerIdleTimeMillis(), fetchBluetoothData.getControllerEnergyUsed()));
        return 0;
    }

    public final void registerSystemElapsedRealtime() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.SYSTEM_ELAPSED_REALTIME, new StatsManager.PullAtomMetadata.Builder().setCoolDownMillis(1000L).setTimeoutMillis(500L).build(), ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullSystemElapsedRealtimeLocked(int i, List<StatsEvent> list) {
        list.add(FrameworkStatsLog.buildStatsEvent(i, SystemClock.elapsedRealtime()));
        return 0;
    }

    public final void registerSystemUptime() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.SYSTEM_UPTIME, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullSystemUptimeLocked(int i, List<StatsEvent> list) {
        list.add(FrameworkStatsLog.buildStatsEvent(i, SystemClock.uptimeMillis()));
        return 0;
    }

    public final void registerProcessMemoryState() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.PROCESS_MEMORY_STATE, new StatsManager.PullAtomMetadata.Builder().setAdditiveFields(new int[]{4, 5, 6, 7, 8}).build(), ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullProcessMemoryStateLocked(int i, List<StatsEvent> list) {
        for (ProcessMemoryState processMemoryState : ((ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class)).getMemoryStateForProcesses()) {
            MemoryStatUtil.MemoryStat readMemoryStatFromFilesystem = MemoryStatUtil.readMemoryStatFromFilesystem(processMemoryState.uid, processMemoryState.pid);
            if (readMemoryStatFromFilesystem != null) {
                list.add(FrameworkStatsLog.buildStatsEvent(i, processMemoryState.uid, processMemoryState.processName, processMemoryState.oomScore, readMemoryStatFromFilesystem.pgfault, readMemoryStatFromFilesystem.pgmajfault, readMemoryStatFromFilesystem.rssInBytes, readMemoryStatFromFilesystem.cacheInBytes, readMemoryStatFromFilesystem.swapInBytes, -1L, -1L, -1));
            }
        }
        return 0;
    }

    public final void registerProcessMemoryHighWaterMark() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.PROCESS_MEMORY_HIGH_WATER_MARK, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullProcessMemoryHighWaterMarkLocked(int i, List<StatsEvent> list) {
        List<ProcessMemoryState> memoryStateForProcesses = ((ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class)).getMemoryStateForProcesses();
        for (ProcessMemoryState processMemoryState : memoryStateForProcesses) {
            ProcfsMemoryUtil.MemorySnapshot readMemorySnapshotFromProcfs = ProcfsMemoryUtil.readMemorySnapshotFromProcfs(processMemoryState.pid);
            if (readMemorySnapshotFromProcfs != null) {
                int i2 = processMemoryState.uid;
                String str = processMemoryState.processName;
                int i3 = readMemorySnapshotFromProcfs.rssHighWaterMarkInKilobytes;
                list.add(FrameworkStatsLog.buildStatsEvent(i, i2, str, i3 * 1024, i3));
            }
        }
        final SparseArray<String> processCmdlines = ProcfsMemoryUtil.getProcessCmdlines();
        memoryStateForProcesses.forEach(new Consumer() { // from class: com.android.server.stats.pull.StatsPullAtomService$$ExternalSyntheticLambda23
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                StatsPullAtomService.lambda$pullProcessMemoryHighWaterMarkLocked$18(processCmdlines, (ProcessMemoryState) obj);
            }
        });
        int size = processCmdlines.size();
        for (int i4 = 0; i4 < size; i4++) {
            ProcfsMemoryUtil.MemorySnapshot readMemorySnapshotFromProcfs2 = ProcfsMemoryUtil.readMemorySnapshotFromProcfs(processCmdlines.keyAt(i4));
            if (readMemorySnapshotFromProcfs2 != null) {
                int i5 = readMemorySnapshotFromProcfs2.rssHighWaterMarkInKilobytes;
                list.add(FrameworkStatsLog.buildStatsEvent(i, readMemorySnapshotFromProcfs2.uid, processCmdlines.valueAt(i4), i5 * 1024, i5));
            }
        }
        SystemProperties.set("sys.rss_hwm_reset.on", "1");
        return 0;
    }

    public static /* synthetic */ void lambda$pullProcessMemoryHighWaterMarkLocked$18(SparseArray sparseArray, ProcessMemoryState processMemoryState) {
        sparseArray.delete(processMemoryState.pid);
    }

    public final void registerProcessMemorySnapshot() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.PROCESS_MEMORY_SNAPSHOT, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullProcessMemorySnapshot(int i, List<StatsEvent> list) {
        List<ProcessMemoryState> memoryStateForProcesses = ((ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class)).getMemoryStateForProcesses();
        KernelAllocationStats.ProcessGpuMem[] gpuAllocations = KernelAllocationStats.getGpuAllocations();
        SparseIntArray sparseIntArray = new SparseIntArray(gpuAllocations.length);
        for (KernelAllocationStats.ProcessGpuMem processGpuMem : gpuAllocations) {
            sparseIntArray.put(processGpuMem.pid, processGpuMem.gpuMemoryKb);
        }
        for (ProcessMemoryState processMemoryState : memoryStateForProcesses) {
            ProcfsMemoryUtil.MemorySnapshot readMemorySnapshotFromProcfs = ProcfsMemoryUtil.readMemorySnapshotFromProcfs(processMemoryState.pid);
            if (readMemorySnapshotFromProcfs != null) {
                int i2 = processMemoryState.uid;
                String str = processMemoryState.processName;
                int i3 = processMemoryState.pid;
                int i4 = processMemoryState.oomScore;
                int i5 = readMemorySnapshotFromProcfs.rssInKilobytes;
                int i6 = readMemorySnapshotFromProcfs.anonRssInKilobytes;
                int i7 = readMemorySnapshotFromProcfs.swapInKilobytes;
                list.add(FrameworkStatsLog.buildStatsEvent(i, i2, str, i3, i4, i5, i6, i7, i6 + i7, sparseIntArray.get(i3), processMemoryState.hasForegroundServices));
            }
        }
        final SparseArray<String> processCmdlines = ProcfsMemoryUtil.getProcessCmdlines();
        memoryStateForProcesses.forEach(new Consumer() { // from class: com.android.server.stats.pull.StatsPullAtomService$$ExternalSyntheticLambda12
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                StatsPullAtomService.lambda$pullProcessMemorySnapshot$19(processCmdlines, (ProcessMemoryState) obj);
            }
        });
        int size = processCmdlines.size();
        for (int i8 = 0; i8 < size; i8++) {
            int keyAt = processCmdlines.keyAt(i8);
            ProcfsMemoryUtil.MemorySnapshot readMemorySnapshotFromProcfs2 = ProcfsMemoryUtil.readMemorySnapshotFromProcfs(keyAt);
            if (readMemorySnapshotFromProcfs2 != null) {
                int i9 = readMemorySnapshotFromProcfs2.rssInKilobytes;
                int i10 = readMemorySnapshotFromProcfs2.anonRssInKilobytes;
                int i11 = readMemorySnapshotFromProcfs2.swapInKilobytes;
                list.add(FrameworkStatsLog.buildStatsEvent(i, readMemorySnapshotFromProcfs2.uid, processCmdlines.valueAt(i8), keyAt, -1001, i9, i10, i11, i10 + i11, sparseIntArray.get(keyAt), false));
            }
        }
        return 0;
    }

    public static /* synthetic */ void lambda$pullProcessMemorySnapshot$19(SparseArray sparseArray, ProcessMemoryState processMemoryState) {
        sparseArray.delete(processMemoryState.pid);
    }

    public final void registerSystemIonHeapSize() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.SYSTEM_ION_HEAP_SIZE, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullSystemIonHeapSizeLocked(int i, List<StatsEvent> list) {
        list.add(FrameworkStatsLog.buildStatsEvent(i, IonMemoryUtil.readSystemIonHeapSizeFromDebugfs()));
        return 0;
    }

    public final void registerIonHeapSize() {
        if (new File("/sys/kernel/ion/total_heaps_kb").exists()) {
            this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.ION_HEAP_SIZE, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
        }
    }

    public int pullIonHeapSizeLocked(int i, List<StatsEvent> list) {
        list.add(FrameworkStatsLog.buildStatsEvent(i, (int) Debug.getIonHeapsSizeKb()));
        return 0;
    }

    public final void registerProcessSystemIonHeapSize() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.PROCESS_SYSTEM_ION_HEAP_SIZE, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullProcessSystemIonHeapSizeLocked(int i, List<StatsEvent> list) {
        for (IonMemoryUtil.IonAllocations ionAllocations : IonMemoryUtil.readProcessSystemIonHeapSizesFromDebugfs()) {
            list.add(FrameworkStatsLog.buildStatsEvent(i, Process.getUidForPid(ionAllocations.pid), ProcfsMemoryUtil.readCmdlineFromProcfs(ionAllocations.pid), (int) (ionAllocations.totalSizeInBytes / 1024), ionAllocations.count, (int) (ionAllocations.maxSizeInBytes / 1024)));
        }
        return 0;
    }

    public final void registerProcessDmabufMemory() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.PROCESS_DMABUF_MEMORY, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullProcessDmabufMemory(int i, List<StatsEvent> list) {
        KernelAllocationStats.ProcessDmabuf[] dmabufAllocations = KernelAllocationStats.getDmabufAllocations();
        if (dmabufAllocations == null) {
            return 1;
        }
        for (KernelAllocationStats.ProcessDmabuf processDmabuf : dmabufAllocations) {
            list.add(FrameworkStatsLog.buildStatsEvent(i, processDmabuf.uid, processDmabuf.processName, processDmabuf.oomScore, processDmabuf.retainedSizeKb, processDmabuf.retainedBuffersCount, 0, 0, processDmabuf.surfaceFlingerSizeKb, processDmabuf.surfaceFlingerCount));
        }
        return 0;
    }

    public final void registerSystemMemory() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.SYSTEM_MEMORY, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullSystemMemory(int i, List<StatsEvent> list) {
        SystemMemoryUtil.Metrics metrics = SystemMemoryUtil.getMetrics();
        list.add(FrameworkStatsLog.buildStatsEvent(i, metrics.unreclaimableSlabKb, metrics.vmallocUsedKb, metrics.pageTablesKb, metrics.kernelStackKb, metrics.totalIonKb, metrics.unaccountedKb, metrics.gpuTotalUsageKb, metrics.gpuPrivateAllocationsKb, metrics.dmaBufTotalExportedKb, metrics.shmemKb, metrics.totalKb, metrics.freeKb, metrics.availableKb, metrics.activeKb, metrics.inactiveKb, metrics.activeAnonKb, metrics.inactiveAnonKb, metrics.activeFileKb, metrics.inactiveFileKb, metrics.swapTotalKb, metrics.swapFreeKb, metrics.cmaTotalKb, metrics.cmaFreeKb));
        return 0;
    }

    public final void registerVmStat() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.VMSTAT, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullVmStat(int i, List<StatsEvent> list) {
        ProcfsMemoryUtil.VmStat readVmStat = ProcfsMemoryUtil.readVmStat();
        if (readVmStat != null) {
            list.add(FrameworkStatsLog.buildStatsEvent(i, readVmStat.oomKillCount));
            return 0;
        }
        return 0;
    }

    public final void registerTemperature() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.TEMPERATURE, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullTemperatureLocked(int i, List<StatsEvent> list) {
        Temperature[] currentTemperatures;
        IThermalService iThermalService = getIThermalService();
        if (iThermalService == null) {
            return 1;
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            for (Temperature temperature : iThermalService.getCurrentTemperatures()) {
                list.add(FrameworkStatsLog.buildStatsEvent(i, temperature.getType(), temperature.getName(), (int) (temperature.getValue() * 10.0f), temperature.getStatus()));
            }
            return 0;
        } catch (RemoteException unused) {
            Slog.e("StatsPullAtomService", "Disconnected from thermal service. Cannot pull temperatures.");
            return 1;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final void registerCoolingDevice() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.COOLING_DEVICE, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullCooldownDeviceLocked(int i, List<StatsEvent> list) {
        CoolingDevice[] currentCoolingDevices;
        IThermalService iThermalService = getIThermalService();
        if (iThermalService == null) {
            return 1;
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            for (CoolingDevice coolingDevice : iThermalService.getCurrentCoolingDevices()) {
                list.add(FrameworkStatsLog.buildStatsEvent(i, coolingDevice.getType(), coolingDevice.getName(), (int) coolingDevice.getValue()));
            }
            return 0;
        } catch (RemoteException unused) {
            Slog.e("StatsPullAtomService", "Disconnected from thermal service. Cannot pull temperatures.");
            return 1;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final void registerBinderCallsStats() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.BINDER_CALLS, new StatsManager.PullAtomMetadata.Builder().setAdditiveFields(new int[]{4, 5, 6, 8, 12}).build(), ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullBinderCallsStatsLocked(int i, List<StatsEvent> list) {
        BinderCallsStatsService.Internal internal = (BinderCallsStatsService.Internal) LocalServices.getService(BinderCallsStatsService.Internal.class);
        if (internal == null) {
            Slog.e("StatsPullAtomService", "failed to get binderStats");
            return 1;
        }
        ArrayList<BinderCallsStats.ExportedCallStat> exportedCallStats = internal.getExportedCallStats();
        internal.reset();
        for (BinderCallsStats.ExportedCallStat exportedCallStat : exportedCallStats) {
            list.add(FrameworkStatsLog.buildStatsEvent(i, exportedCallStat.workSourceUid, exportedCallStat.className, exportedCallStat.methodName, exportedCallStat.callCount, exportedCallStat.exceptionCount, exportedCallStat.latencyMicros, exportedCallStat.maxLatencyMicros, exportedCallStat.cpuTimeMicros, exportedCallStat.maxCpuTimeMicros, exportedCallStat.maxReplySizeBytes, exportedCallStat.maxRequestSizeBytes, exportedCallStat.recordedCallCount, exportedCallStat.screenInteractive, exportedCallStat.callingUid));
        }
        return 0;
    }

    public final void registerBinderCallsStatsExceptions() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.BINDER_CALLS_EXCEPTIONS, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullBinderCallsStatsExceptionsLocked(int i, List<StatsEvent> list) {
        BinderCallsStatsService.Internal internal = (BinderCallsStatsService.Internal) LocalServices.getService(BinderCallsStatsService.Internal.class);
        if (internal == null) {
            Slog.e("StatsPullAtomService", "failed to get binderStats");
            return 1;
        }
        for (Map.Entry<String, Integer> entry : internal.getExportedExceptionStats().entrySet()) {
            list.add(FrameworkStatsLog.buildStatsEvent(i, entry.getKey(), entry.getValue().intValue()));
        }
        return 0;
    }

    public final void registerLooperStats() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.LOOPER_STATS, new StatsManager.PullAtomMetadata.Builder().setAdditiveFields(new int[]{5, 6, 7, 8, 9}).build(), ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullLooperStatsLocked(int i, List<StatsEvent> list) {
        LooperStats looperStats = (LooperStats) LocalServices.getService(LooperStats.class);
        if (looperStats == null) {
            return 1;
        }
        List<LooperStats.ExportedEntry> entries = looperStats.getEntries();
        looperStats.reset();
        for (LooperStats.ExportedEntry exportedEntry : entries) {
            list.add(FrameworkStatsLog.buildStatsEvent(i, exportedEntry.workSourceUid, exportedEntry.handlerClassName, exportedEntry.threadName, exportedEntry.messageName, exportedEntry.messageCount, exportedEntry.exceptionCount, exportedEntry.recordedMessageCount, exportedEntry.totalLatencyMicros, exportedEntry.cpuUsageMicros, exportedEntry.isInteractive, exportedEntry.maxCpuUsageMicros, exportedEntry.maxLatencyMicros, exportedEntry.recordedDelayMessageCount, exportedEntry.delayMillis, exportedEntry.maxDelayMillis));
        }
        return 0;
    }

    public final void registerDiskStats() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.DISK_STATS, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    /* JADX WARN: Code restructure failed: missing block: B:21:0x003a, code lost:
        if (r6 == null) goto L15;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v1, types: [java.io.IOException] */
    /* JADX WARN: Type inference failed for: r1v2 */
    /* JADX WARN: Type inference failed for: r1v6, types: [java.io.IOException] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public int pullDiskStatsLocked(int i, List<StatsEvent> list) {
        FileOutputStream fileOutputStream;
        int i2;
        byte[] bArr = new byte[512];
        for (int i3 = 0; i3 < 512; i3++) {
            bArr[i3] = (byte) i3;
        }
        File file = new File(Environment.getDataDirectory(), "system/statsdperftest.tmp");
        long elapsedRealtime = SystemClock.elapsedRealtime();
        FileOutputStream fileOutputStream2 = null;
        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (IOException e) {
            e = e;
            fileOutputStream = null;
        } catch (Throwable th) {
            th = th;
        }
        try {
            fileOutputStream.write(bArr);
        } catch (IOException e2) {
            e = e2;
            fileOutputStream2 = e;
        } catch (Throwable th2) {
            th = th2;
            fileOutputStream2 = fileOutputStream;
            if (fileOutputStream2 != null) {
                try {
                    fileOutputStream2.close();
                } catch (IOException unused) {
                }
            }
            throw th;
        }
        try {
            fileOutputStream.close();
        } catch (IOException unused2) {
            long elapsedRealtime2 = SystemClock.elapsedRealtime() - elapsedRealtime;
            if (file.exists()) {
                file.delete();
            }
            if (fileOutputStream2 != null) {
                Slog.e("StatsPullAtomService", "Error performing diskstats latency test");
                elapsedRealtime2 = -1;
            }
            boolean isFileEncrypted = StorageManager.isFileEncrypted();
            IStoraged iStoragedService = getIStoragedService();
            if (iStoragedService == null) {
                return 1;
            }
            try {
                i2 = iStoragedService.getRecentPerf();
            } catch (RemoteException unused3) {
                Slog.e("StatsPullAtomService", "storaged not found");
                i2 = -1;
            }
            list.add(FrameworkStatsLog.buildStatsEvent(i, elapsedRealtime2, isFileEncrypted, i2));
            return 0;
        }
    }

    public final void registerDirectoryUsage() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.DIRECTORY_USAGE, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullDirectoryUsageLocked(int i, List<StatsEvent> list) {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
        StatFs statFs2 = new StatFs(Environment.getRootDirectory().getAbsolutePath());
        StatFs statFs3 = new StatFs(Environment.getDownloadCacheDirectory().getAbsolutePath());
        StatFs statFs4 = new StatFs(Environment.getMetadataDirectory().getAbsolutePath());
        list.add(FrameworkStatsLog.buildStatsEvent(i, 1, statFs.getAvailableBytes(), statFs.getTotalBytes()));
        list.add(FrameworkStatsLog.buildStatsEvent(i, 2, statFs3.getAvailableBytes(), statFs3.getTotalBytes()));
        list.add(FrameworkStatsLog.buildStatsEvent(i, 3, statFs2.getAvailableBytes(), statFs2.getTotalBytes()));
        list.add(FrameworkStatsLog.buildStatsEvent(i, 4, statFs4.getAvailableBytes(), statFs4.getTotalBytes()));
        return 0;
    }

    public final void registerAppSize() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.APP_SIZE, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullAppSizeLocked(int i, List<StatsEvent> list) {
        try {
            JSONObject jSONObject = new JSONObject(IoUtils.readFileAsString("/data/system/diskstats_cache.json"));
            long optLong = jSONObject.optLong("queryTime", -1L);
            JSONArray jSONArray = jSONObject.getJSONArray("packageNames");
            JSONArray jSONArray2 = jSONObject.getJSONArray("appSizes");
            JSONArray jSONArray3 = jSONObject.getJSONArray("appDataSizes");
            JSONArray jSONArray4 = jSONObject.getJSONArray("cacheSizes");
            int length = jSONArray.length();
            if (jSONArray2.length() == length && jSONArray3.length() == length && jSONArray4.length() == length) {
                int i2 = 0;
                while (i2 < length) {
                    list.add(FrameworkStatsLog.buildStatsEvent(i, jSONArray.getString(i2), jSONArray2.optLong(i2, -1L), jSONArray3.optLong(i2, -1L), jSONArray4.optLong(i2, -1L), optLong));
                    i2++;
                    jSONArray2 = jSONArray2;
                    jSONArray3 = jSONArray3;
                    length = length;
                }
                return 0;
            }
            Slog.e("StatsPullAtomService", "formatting error in diskstats cache file!");
            return 1;
        } catch (IOException | JSONException unused) {
            Slog.w("StatsPullAtomService", "Unable to read diskstats cache file within pullAppSize");
            return 1;
        }
    }

    public final void registerCategorySize() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.CATEGORY_SIZE, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullCategorySizeLocked(int i, List<StatsEvent> list) {
        try {
            JSONObject jSONObject = new JSONObject(IoUtils.readFileAsString("/data/system/diskstats_cache.json"));
            long optLong = jSONObject.optLong("queryTime", -1L);
            list.add(FrameworkStatsLog.buildStatsEvent(i, 1, jSONObject.optLong("appSize", -1L), optLong));
            list.add(FrameworkStatsLog.buildStatsEvent(i, 2, jSONObject.optLong("appDataSize", -1L), optLong));
            list.add(FrameworkStatsLog.buildStatsEvent(i, 3, jSONObject.optLong("cacheSize", -1L), optLong));
            list.add(FrameworkStatsLog.buildStatsEvent(i, 4, jSONObject.optLong("photosSize", -1L), optLong));
            list.add(FrameworkStatsLog.buildStatsEvent(i, 5, jSONObject.optLong("videosSize", -1L), optLong));
            list.add(FrameworkStatsLog.buildStatsEvent(i, 6, jSONObject.optLong("audioSize", -1L), optLong));
            list.add(FrameworkStatsLog.buildStatsEvent(i, 7, jSONObject.optLong("downloadsSize", -1L), optLong));
            list.add(FrameworkStatsLog.buildStatsEvent(i, 8, jSONObject.optLong("systemSize", -1L), optLong));
            list.add(FrameworkStatsLog.buildStatsEvent(i, 9, jSONObject.optLong("otherSize", -1L), optLong));
            return 0;
        } catch (IOException | JSONException unused) {
            Slog.w("StatsPullAtomService", "Unable to read diskstats cache file within pullCategorySize");
            return 1;
        }
    }

    public final void registerNumFingerprintsEnrolled() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.NUM_FINGERPRINTS_ENROLLED, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public final void registerNumFacesEnrolled() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.NUM_FACES_ENROLLED, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public final int pullNumBiometricsEnrolledLocked(int i, int i2, List<StatsEvent> list) {
        UserManager userManager;
        int size;
        PackageManager packageManager = this.mContext.getPackageManager();
        FingerprintManager fingerprintManager = packageManager.hasSystemFeature("android.hardware.fingerprint") ? (FingerprintManager) this.mContext.getSystemService(FingerprintManager.class) : null;
        FaceManager faceManager = packageManager.hasSystemFeature("android.hardware.biometrics.face") ? (FaceManager) this.mContext.getSystemService(FaceManager.class) : null;
        if (i == 1 && fingerprintManager == null) {
            return 1;
        }
        if ((i == 4 && faceManager == null) || (userManager = (UserManager) this.mContext.getSystemService(UserManager.class)) == null) {
            return 1;
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            for (UserInfo userInfo : userManager.getUsers()) {
                int identifier = userInfo.getUserHandle().getIdentifier();
                if (i == 1) {
                    size = fingerprintManager.getEnrolledFingerprints(identifier).size();
                } else if (i != 4) {
                    return 1;
                } else {
                    size = faceManager.getEnrolledFaces(identifier).size();
                }
                list.add(FrameworkStatsLog.buildStatsEvent(i2, identifier, size));
            }
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return 0;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final void registerProcStats() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.PROC_STATS, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public final void registerProcStatsPkgProc() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.PROC_STATS_PKG_PROC, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public final void registerProcessState() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.PROCESS_STATE, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public final void registerProcessAssociation() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.PROCESS_ASSOCIATION, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    @GuardedBy({"mProcStatsLock"})
    public final ProcessStats getStatsFromProcessStatsService(int i) {
        IProcessStats iProcessStatsService = getIProcessStatsService();
        if (iProcessStatsService == null) {
            return null;
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            long readProcStatsHighWaterMark = readProcStatsHighWaterMark(i);
            ProcessStats processStats = new ProcessStats(false);
            long committedStatsMerged = iProcessStatsService.getCommittedStatsMerged(readProcStatsHighWaterMark, 31, true, (List) null, processStats);
            new File(this.mBaseDir.getAbsolutePath() + "/" + highWaterMarkFilePrefix(i) + "_" + readProcStatsHighWaterMark).delete();
            new File(this.mBaseDir.getAbsolutePath() + "/" + highWaterMarkFilePrefix(i) + "_" + committedStatsMerged).createNewFile();
            return processStats;
        } catch (RemoteException | IOException e) {
            Slog.e("StatsPullAtomService", "Getting procstats failed: ", e);
            return null;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @GuardedBy({"mProcStatsLock"})
    public final int pullProcStatsLocked(int i, List<StatsEvent> list) {
        ProcessStats statsFromProcessStatsService = getStatsFromProcessStatsService(i);
        if (statsFromProcessStatsService == null) {
            return 1;
        }
        ProtoOutputStream[] protoOutputStreamArr = new ProtoOutputStream[5];
        for (int i2 = 0; i2 < 5; i2++) {
            protoOutputStreamArr[i2] = new ProtoOutputStream();
        }
        statsFromProcessStatsService.dumpAggregatedProtoForStatsd(protoOutputStreamArr, 58982L);
        for (int i3 = 0; i3 < 5; i3++) {
            byte[] bytes = protoOutputStreamArr[i3].getBytes();
            if (bytes.length > 0) {
                list.add(FrameworkStatsLog.buildStatsEvent(i, bytes, i3));
            }
        }
        return 0;
    }

    @GuardedBy({"mProcStatsLock"})
    public final int pullProcessStateLocked(int i, List<StatsEvent> list) {
        ProcessStats statsFromProcessStatsService = getStatsFromProcessStatsService(i);
        if (statsFromProcessStatsService == null) {
            return 1;
        }
        statsFromProcessStatsService.dumpProcessState(i, new StatsEventOutput(list));
        return 0;
    }

    @GuardedBy({"mProcStatsLock"})
    public final int pullProcessAssociationLocked(int i, List<StatsEvent> list) {
        ProcessStats statsFromProcessStatsService = getStatsFromProcessStatsService(i);
        if (statsFromProcessStatsService == null) {
            return 1;
        }
        statsFromProcessStatsService.dumpProcessAssociation(i, new StatsEventOutput(list));
        return 0;
    }

    public final String highWaterMarkFilePrefix(int i) {
        if (i == 10029) {
            return String.valueOf(31);
        }
        if (i == 10034) {
            return String.valueOf(2);
        }
        return "atom-" + i;
    }

    public final long readProcStatsHighWaterMark(final int i) {
        try {
            File[] listFiles = this.mBaseDir.listFiles(new FilenameFilter() { // from class: com.android.server.stats.pull.StatsPullAtomService$$ExternalSyntheticLambda10
                @Override // java.io.FilenameFilter
                public final boolean accept(File file, String str) {
                    boolean lambda$readProcStatsHighWaterMark$20;
                    lambda$readProcStatsHighWaterMark$20 = StatsPullAtomService.this.lambda$readProcStatsHighWaterMark$20(i, file, str);
                    return lambda$readProcStatsHighWaterMark$20;
                }
            });
            if (listFiles != null && listFiles.length != 0) {
                if (listFiles.length > 1) {
                    Slog.e("StatsPullAtomService", "Only 1 file expected for high water mark. Found " + listFiles.length);
                }
                return Long.valueOf(listFiles[0].getName().split("_")[1]).longValue();
            }
            return 0L;
        } catch (NumberFormatException e) {
            Slog.e("StatsPullAtomService", "Failed to parse file name.", e);
            return 0L;
        } catch (SecurityException e2) {
            Slog.e("StatsPullAtomService", "Failed to get procstats high watermark file.", e2);
            return 0L;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$readProcStatsHighWaterMark$20(int i, File file, String str) {
        String lowerCase = str.toLowerCase();
        return lowerCase.startsWith(highWaterMarkFilePrefix(i) + '_');
    }

    public final void registerDiskIO() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.DISK_IO, new StatsManager.PullAtomMetadata.Builder().setAdditiveFields(new int[]{2, 3, 4, 5, 6, 7, 8, 9, 10, 11}).setCoolDownMillis((long) BackupAgentTimeoutParameters.DEFAULT_QUOTA_EXCEEDED_TIMEOUT_MILLIS).build(), ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullDiskIOLocked(final int i, final List<StatsEvent> list) {
        this.mStoragedUidIoStatsReader.readAbsolute(new StoragedUidIoStatsReader.Callback() { // from class: com.android.server.stats.pull.StatsPullAtomService$$ExternalSyntheticLambda9
            public final void onUidStorageStats(int i2, long j, long j2, long j3, long j4, long j5, long j6, long j7, long j8, long j9, long j10) {
                StatsPullAtomService.lambda$pullDiskIOLocked$21(list, i, i2, j, j2, j3, j4, j5, j6, j7, j8, j9, j10);
            }
        });
        return 0;
    }

    public static /* synthetic */ void lambda$pullDiskIOLocked$21(List list, int i, int i2, long j, long j2, long j3, long j4, long j5, long j6, long j7, long j8, long j9, long j10) {
        list.add(FrameworkStatsLog.buildStatsEvent(i, i2, j, j2, j3, j4, j5, j6, j7, j8, j9, j10));
    }

    public final void registerPowerProfile() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.POWER_PROFILE, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullPowerProfileLocked(int i, List<StatsEvent> list) {
        PowerProfile powerProfile = new PowerProfile(this.mContext);
        ProtoOutputStream protoOutputStream = new ProtoOutputStream();
        powerProfile.dumpDebug(protoOutputStream);
        protoOutputStream.flush();
        list.add(FrameworkStatsLog.buildStatsEvent(i, protoOutputStream.getBytes()));
        return 0;
    }

    public final void registerProcessCpuTime() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.PROCESS_CPU_TIME, new StatsManager.PullAtomMetadata.Builder().setCoolDownMillis(5000L).build(), ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullProcessCpuTimeLocked(int i, List<StatsEvent> list) {
        if (this.mProcessCpuTracker == null) {
            ProcessCpuTracker processCpuTracker = new ProcessCpuTracker(false);
            this.mProcessCpuTracker = processCpuTracker;
            processCpuTracker.init();
        }
        this.mProcessCpuTracker.update();
        for (int i2 = 0; i2 < this.mProcessCpuTracker.countStats(); i2++) {
            ProcessCpuTracker.Stats stats = this.mProcessCpuTracker.getStats(i2);
            list.add(FrameworkStatsLog.buildStatsEvent(i, stats.uid, stats.name, stats.base_utime, stats.base_stime));
        }
        return 0;
    }

    public final void registerCpuTimePerThreadFreq() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.CPU_TIME_PER_THREAD_FREQ, new StatsManager.PullAtomMetadata.Builder().setAdditiveFields(new int[]{7, 9, 11, 13, 15, 17, 19, 21}).build(), ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullCpuTimePerThreadFreqLocked(int i, List<StatsEvent> list) {
        KernelCpuThreadReaderDiff kernelCpuThreadReaderDiff = this.mKernelCpuThreadReader;
        if (kernelCpuThreadReaderDiff == null) {
            Slog.e("StatsPullAtomService", "mKernelCpuThreadReader is null");
            return 1;
        }
        ArrayList processCpuUsageDiffed = kernelCpuThreadReaderDiff.getProcessCpuUsageDiffed();
        if (processCpuUsageDiffed == null) {
            Slog.e("StatsPullAtomService", "processCpuUsages is null");
            return 1;
        }
        int[] cpuFrequenciesKhz = this.mKernelCpuThreadReader.getCpuFrequenciesKhz();
        if (cpuFrequenciesKhz.length > 8) {
            Slog.w("StatsPullAtomService", "Expected maximum 8 frequencies, but got " + cpuFrequenciesKhz.length);
            return 1;
        }
        for (int i2 = 0; i2 < processCpuUsageDiffed.size(); i2++) {
            KernelCpuThreadReader.ProcessCpuUsage processCpuUsage = (KernelCpuThreadReader.ProcessCpuUsage) processCpuUsageDiffed.get(i2);
            ArrayList arrayList = processCpuUsage.threadCpuUsages;
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                KernelCpuThreadReader.ThreadCpuUsage threadCpuUsage = (KernelCpuThreadReader.ThreadCpuUsage) arrayList.get(i3);
                if (threadCpuUsage.usageTimesMillis.length != cpuFrequenciesKhz.length) {
                    Slog.w("StatsPullAtomService", "Unexpected number of usage times, expected " + cpuFrequenciesKhz.length + " but got " + threadCpuUsage.usageTimesMillis.length);
                    return 1;
                }
                int[] iArr = new int[8];
                int[] iArr2 = new int[8];
                for (int i4 = 0; i4 < 8; i4++) {
                    if (i4 < cpuFrequenciesKhz.length) {
                        iArr[i4] = cpuFrequenciesKhz[i4];
                        iArr2[i4] = threadCpuUsage.usageTimesMillis[i4];
                    } else {
                        iArr[i4] = 0;
                        iArr2[i4] = 0;
                    }
                }
                list.add(FrameworkStatsLog.buildStatsEvent(i, processCpuUsage.uid, processCpuUsage.processId, threadCpuUsage.threadId, processCpuUsage.processName, threadCpuUsage.threadName, iArr[0], iArr2[0], iArr[1], iArr2[1], iArr[2], iArr2[2], iArr[3], iArr2[3], iArr[4], iArr2[4], iArr[5], iArr2[5], iArr[6], iArr2[6], iArr[7], iArr2[7]));
            }
        }
        return 0;
    }

    public final void registerDeviceCalculatedPowerUse() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.DEVICE_CALCULATED_POWER_USE, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullDeviceCalculatedPowerUseLocked(int i, List<StatsEvent> list) {
        try {
            list.add(FrameworkStatsLog.buildStatsEvent(i, milliAmpHrsToNanoAmpSecs(((BatteryStatsManager) this.mContext.getSystemService(BatteryStatsManager.class)).getBatteryUsageStats().getConsumedPower())));
            return 0;
        } catch (Exception e) {
            Log.e("StatsPullAtomService", "Could not obtain battery usage stats", e);
            return 1;
        }
    }

    public final void registerDebugElapsedClock() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.DEBUG_ELAPSED_CLOCK, new StatsManager.PullAtomMetadata.Builder().setAdditiveFields(new int[]{1, 2, 3, 4}).build(), ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullDebugElapsedClockLocked(int i, List<StatsEvent> list) {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long j = this.mDebugElapsedClockPreviousValue;
        long j2 = j == 0 ? 0L : elapsedRealtime - j;
        list.add(FrameworkStatsLog.buildStatsEvent(i, this.mDebugElapsedClockPullCount, elapsedRealtime, elapsedRealtime, j2, 1));
        long j3 = this.mDebugElapsedClockPullCount;
        if (j3 % 2 == 1) {
            list.add(FrameworkStatsLog.buildStatsEvent(i, j3, elapsedRealtime, elapsedRealtime, j2, 2));
        }
        this.mDebugElapsedClockPullCount++;
        this.mDebugElapsedClockPreviousValue = elapsedRealtime;
        return 0;
    }

    public final void registerDebugFailingElapsedClock() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.DEBUG_FAILING_ELAPSED_CLOCK, new StatsManager.PullAtomMetadata.Builder().setAdditiveFields(new int[]{1, 2, 3, 4}).build(), ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullDebugFailingElapsedClockLocked(int i, List<StatsEvent> list) {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long j = this.mDebugFailingElapsedClockPullCount;
        long j2 = 1 + j;
        this.mDebugFailingElapsedClockPullCount = j2;
        if (j % 5 == 0) {
            this.mDebugFailingElapsedClockPreviousValue = elapsedRealtime;
            Slog.e("StatsPullAtomService", "Failing debug elapsed clock");
            return 1;
        }
        long j3 = this.mDebugFailingElapsedClockPreviousValue;
        list.add(FrameworkStatsLog.buildStatsEvent(i, j2, elapsedRealtime, elapsedRealtime, j3 == 0 ? 0L : elapsedRealtime - j3));
        this.mDebugFailingElapsedClockPreviousValue = elapsedRealtime;
        return 0;
    }

    public final void registerBuildInformation() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.BUILD_INFORMATION, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullBuildInformationLocked(int i, List<StatsEvent> list) {
        list.add(FrameworkStatsLog.buildStatsEvent(i, Build.FINGERPRINT, Build.BRAND, Build.PRODUCT, Build.DEVICE, Build.VERSION.RELEASE_OR_CODENAME, Build.ID, Build.VERSION.INCREMENTAL, Build.TYPE, Build.TAGS));
        return 0;
    }

    public final void registerRoleHolder() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.ROLE_HOLDER, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullRoleHolderLocked(int i, List<StatsEvent> list) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            PackageManager packageManager = this.mContext.getPackageManager();
            RoleManagerLocal roleManagerLocal = (RoleManagerLocal) LocalManagerRegistry.getManager(RoleManagerLocal.class);
            List users = ((UserManager) this.mContext.getSystemService(UserManager.class)).getUsers();
            int size = users.size();
            for (int i2 = 0; i2 < size; i2++) {
                int identifier = ((UserInfo) users.get(i2)).getUserHandle().getIdentifier();
                for (Map.Entry entry : roleManagerLocal.getRolesAndHolders(identifier).entrySet()) {
                    String str = (String) entry.getKey();
                    for (String str2 : (Set) entry.getValue()) {
                        try {
                            list.add(FrameworkStatsLog.buildStatsEvent(i, packageManager.getPackageInfoAsUser(str2, 0, identifier).applicationInfo.uid, str2, str));
                        } catch (PackageManager.NameNotFoundException unused) {
                            Slog.w("StatsPullAtomService", "Role holder " + str2 + " not found");
                            Binder.restoreCallingIdentity(clearCallingIdentity);
                            return 1;
                        }
                    }
                }
            }
            return 0;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final void registerDangerousPermissionState() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.DANGEROUS_PERMISSION_STATE, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullDangerousPermissionStateLocked(int i, List<StatsEvent> list) {
        PackageInfo packageInfo;
        int i2;
        List list2;
        UserHandle userHandle;
        int i3;
        int i4;
        StatsEvent buildStatsEvent;
        int i5 = i;
        long clearCallingIdentity = Binder.clearCallingIdentity();
        float f = DeviceConfig.getFloat("permissions", "dangerous_permission_state_sample_rate", 0.015f);
        HashSet hashSet = new HashSet();
        try {
            PackageManager packageManager = this.mContext.getPackageManager();
            List users = ((UserManager) this.mContext.getSystemService(UserManager.class)).getUsers();
            int size = users.size();
            int i6 = 0;
            while (i6 < size) {
                UserHandle userHandle2 = ((UserInfo) users.get(i6)).getUserHandle();
                List installedPackagesAsUser = packageManager.getInstalledPackagesAsUser(IInstalld.FLAG_USE_QUOTA, userHandle2.getIdentifier());
                int size2 = installedPackagesAsUser.size();
                int i7 = 0;
                while (i7 < size2) {
                    PackageInfo packageInfo2 = (PackageInfo) installedPackagesAsUser.get(i7);
                    if (packageInfo2.requestedPermissions != null && !hashSet.contains(Integer.valueOf(packageInfo2.applicationInfo.uid))) {
                        hashSet.add(Integer.valueOf(packageInfo2.applicationInfo.uid));
                        if (i5 != 10067 || ThreadLocalRandom.current().nextFloat() <= f) {
                            int length = packageInfo2.requestedPermissions.length;
                            int i8 = 0;
                            while (i8 < length) {
                                int i9 = i7;
                                String str = packageInfo2.requestedPermissions[i8];
                                float f2 = f;
                                try {
                                    PermissionInfo permissionInfo = packageManager.getPermissionInfo(str, 0);
                                    try {
                                        int permissionFlags = packageManager.getPermissionFlags(str, packageInfo2.packageName, userHandle2);
                                        i2 = size2;
                                        if (str.startsWith("android.permission.")) {
                                            str = str.substring(19);
                                        }
                                        if (i5 == 10050) {
                                            int i10 = packageInfo2.applicationInfo.uid;
                                            list2 = installedPackagesAsUser;
                                            packageInfo = packageInfo2;
                                            userHandle = userHandle2;
                                            boolean z = (packageInfo2.requestedPermissionsFlags[i8] & 2) != 0;
                                            i3 = i6;
                                            i4 = i8;
                                            buildStatsEvent = FrameworkStatsLog.buildStatsEvent(i, str, i10, "", z, permissionFlags, permissionInfo.getProtection() | permissionInfo.getProtectionFlags());
                                        } else {
                                            packageInfo = packageInfo2;
                                            list2 = installedPackagesAsUser;
                                            userHandle = userHandle2;
                                            i3 = i6;
                                            i4 = i8;
                                            buildStatsEvent = FrameworkStatsLog.buildStatsEvent(i, str, packageInfo.applicationInfo.uid, (packageInfo.requestedPermissionsFlags[i4] & 2) != 0, permissionFlags, permissionInfo.getProtection() | permissionInfo.getProtectionFlags());
                                        }
                                        list.add(buildStatsEvent);
                                    } catch (PackageManager.NameNotFoundException unused) {
                                        packageInfo = packageInfo2;
                                        i2 = size2;
                                        list2 = installedPackagesAsUser;
                                        userHandle = userHandle2;
                                        i3 = i6;
                                        i4 = i8;
                                    }
                                } catch (PackageManager.NameNotFoundException unused2) {
                                    packageInfo = packageInfo2;
                                    i2 = size2;
                                    list2 = installedPackagesAsUser;
                                    userHandle = userHandle2;
                                    i3 = i6;
                                    i4 = i8;
                                }
                                i8 = i4 + 1;
                                packageInfo2 = packageInfo;
                                i7 = i9;
                                f = f2;
                                size2 = i2;
                                userHandle2 = userHandle;
                                installedPackagesAsUser = list2;
                                i6 = i3;
                                i5 = i;
                            }
                        }
                    }
                    i5 = i;
                    i7++;
                    f = f;
                    size2 = size2;
                    userHandle2 = userHandle2;
                    installedPackagesAsUser = installedPackagesAsUser;
                    i6 = i6;
                }
                i6++;
                i5 = i;
            }
            return 0;
        } catch (Throwable th) {
            try {
                Log.e("StatsPullAtomService", "Could not read permissions", th);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return 1;
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    public final void registerTimeZoneDataInfo() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.TIME_ZONE_DATA_INFO, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullTimeZoneDataInfoLocked(int i, List<StatsEvent> list) {
        try {
            list.add(FrameworkStatsLog.buildStatsEvent(i, TimeZone.getTZDataVersion()));
            return 0;
        } catch (MissingResourceException e) {
            Slog.e("StatsPullAtomService", "Getting tzdb version failed: ", e);
            return 1;
        }
    }

    public final void registerTimeZoneDetectorState() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.TIME_ZONE_DETECTOR_STATE, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullTimeZoneDetectorStateLocked(int i, List<StatsEvent> list) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                MetricsTimeZoneDetectorState generateMetricsState = ((TimeZoneDetectorInternal) LocalServices.getService(TimeZoneDetectorInternal.class)).generateMetricsState();
                list.add(FrameworkStatsLog.buildStatsEvent(i, generateMetricsState.isTelephonyDetectionSupported(), generateMetricsState.isGeoDetectionSupported(), generateMetricsState.getUserLocationEnabledSetting(), generateMetricsState.getAutoDetectionEnabledSetting(), generateMetricsState.getGeoDetectionEnabledSetting(), convertToMetricsDetectionMode(generateMetricsState.getDetectionMode()), generateMetricsState.getDeviceTimeZoneIdOrdinal(), convertTimeZoneSuggestionToProtoBytes(generateMetricsState.getLatestManualSuggestion()), convertTimeZoneSuggestionToProtoBytes(generateMetricsState.getLatestTelephonySuggestion()), convertTimeZoneSuggestionToProtoBytes(generateMetricsState.getLatestGeolocationSuggestion()), generateMetricsState.isTelephonyTimeZoneFallbackSupported(), generateMetricsState.getDeviceTimeZoneId(), generateMetricsState.isEnhancedMetricsCollectionEnabled(), generateMetricsState.getGeoDetectionRunInBackgroundEnabled()));
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return 0;
            } catch (RuntimeException e) {
                Slog.e("StatsPullAtomService", "Getting time zone detection state failed: ", e);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return 1;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public static byte[] convertTimeZoneSuggestionToProtoBytes(MetricsTimeZoneDetectorState.MetricsTimeZoneSuggestion metricsTimeZoneSuggestion) {
        if (metricsTimeZoneSuggestion == null) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ProtoOutputStream protoOutputStream = new ProtoOutputStream(byteArrayOutputStream);
        protoOutputStream.write(1159641169921L, metricsTimeZoneSuggestion.isCertain() ? 1 : 2);
        if (metricsTimeZoneSuggestion.isCertain()) {
            for (int i : metricsTimeZoneSuggestion.getZoneIdOrdinals()) {
                protoOutputStream.write(2220498092034L, i);
            }
            String[] zoneIds = metricsTimeZoneSuggestion.getZoneIds();
            if (zoneIds != null) {
                for (String str : zoneIds) {
                    protoOutputStream.write(2237677961219L, str);
                }
            }
        }
        protoOutputStream.flush();
        IoUtils.closeQuietly(byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public final void registerExternalStorageInfo() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.EXTERNAL_STORAGE_INFO, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullExternalStorageInfoLocked(int i, List<StatsEvent> list) {
        int i2;
        StorageManager storageManager = this.mStorageManager;
        if (storageManager == null) {
            return 1;
        }
        for (VolumeInfo volumeInfo : storageManager.getVolumes()) {
            String environmentForState = VolumeInfo.getEnvironmentForState(volumeInfo.getState());
            DiskInfo disk = volumeInfo.getDisk();
            if (disk != null && environmentForState.equals("mounted")) {
                int i3 = 2;
                if (volumeInfo.getType() == 0) {
                    i2 = 1;
                } else {
                    i2 = volumeInfo.getType() == 1 ? 2 : 3;
                }
                if (disk.isSd()) {
                    i3 = 1;
                } else if (!disk.isUsb()) {
                    i3 = 3;
                }
                list.add(FrameworkStatsLog.buildStatsEvent(i, i3, i2, disk.size));
            }
        }
        return 0;
    }

    public final void registerAppsOnExternalStorageInfo() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.APPS_ON_EXTERNAL_STORAGE_INFO, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullAppsOnExternalStorageInfoLocked(int i, List<StatsEvent> list) {
        VolumeInfo findVolumeByUuid;
        DiskInfo disk;
        int i2;
        if (this.mStorageManager == null) {
            return 1;
        }
        for (ApplicationInfo applicationInfo : this.mContext.getPackageManager().getInstalledApplications(0)) {
            UUID uuid = applicationInfo.storageUuid;
            if (uuid != null && (findVolumeByUuid = this.mStorageManager.findVolumeByUuid(uuid.toString())) != null && (disk = findVolumeByUuid.getDisk()) != null) {
                if (disk.isSd()) {
                    i2 = 1;
                } else if (disk.isUsb()) {
                    i2 = 2;
                } else {
                    i2 = applicationInfo.isExternal() ? 3 : -1;
                }
                if (i2 != -1) {
                    list.add(FrameworkStatsLog.buildStatsEvent(i, i2, applicationInfo.packageName));
                }
            }
        }
        return 0;
    }

    public final void registerFaceSettings() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.FACE_SETTINGS, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullFaceSettingsLocked(int i, List<StatsEvent> list) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            UserManager userManager = (UserManager) this.mContext.getSystemService(UserManager.class);
            if (userManager == null) {
                return 1;
            }
            List users = userManager.getUsers();
            int size = users.size();
            for (int i2 = 0; i2 < size; i2++) {
                int identifier = ((UserInfo) users.get(i2)).getUserHandle().getIdentifier();
                list.add(FrameworkStatsLog.buildStatsEvent(i, Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "face_unlock_keyguard_enabled", 1, identifier) != 0, Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "face_unlock_dismisses_keyguard", 1, identifier) != 0, Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "face_unlock_attention_required", 0, identifier) != 0, Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "face_unlock_app_enabled", 1, identifier) != 0, Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "face_unlock_always_require_confirmation", 0, identifier) != 0, Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "face_unlock_diversity_required", 1, identifier) != 0));
            }
            return 0;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final void registerAppOps() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.APP_OPS, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public final void registerRuntimeAppOpAccessMessage() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.RUNTIME_APP_OP_ACCESS, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    /* loaded from: classes2.dex */
    public class AppOpEntry {
        public final String mAttributionTag;
        public final int mHash;
        public final AppOpsManager.HistoricalOp mOp;
        public final String mPackageName;
        public final int mUid;

        public AppOpEntry(String str, String str2, AppOpsManager.HistoricalOp historicalOp, int i) {
            this.mPackageName = str;
            this.mAttributionTag = str2;
            this.mUid = i;
            this.mOp = historicalOp;
            this.mHash = ((str.hashCode() + StatsPullAtomService.RANDOM_SEED) & Integer.MAX_VALUE) % 100;
        }
    }

    public int pullAppOpsLocked(int i, List<StatsEvent> list) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            CompletableFuture completableFuture = new CompletableFuture();
            ((AppOpsManager) this.mContext.getSystemService(AppOpsManager.class)).getHistoricalOps(new AppOpsManager.HistoricalOpsRequest.Builder(0L, Long.MAX_VALUE).setFlags(9).build(), AsyncTask.THREAD_POOL_EXECUTOR, new StatsPullAtomService$$ExternalSyntheticLambda11(completableFuture));
            if (sampleAppOps(list, processHistoricalOps((AppOpsManager.HistoricalOps) completableFuture.get(2000L, TimeUnit.MILLISECONDS), i, 100), i, 100) != 100) {
                Slog.e("StatsPullAtomService", "Atom 10060 downsampled - too many dimensions");
            }
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return 0;
        } catch (Throwable th) {
            try {
                Slog.e("StatsPullAtomService", "Could not read appops", th);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return 1;
            } catch (Throwable th2) {
                Binder.restoreCallingIdentity(clearCallingIdentity);
                throw th2;
            }
        }
    }

    public final int sampleAppOps(List<StatsEvent> list, List<AppOpEntry> list2, int i, int i2) {
        int i3;
        int i4;
        StatsEvent buildStatsEvent;
        List<StatsEvent> list3 = list;
        List<AppOpEntry> list4 = list2;
        int i5 = i;
        int i6 = i2;
        int size = list2.size();
        int i7 = 0;
        while (i7 < size) {
            AppOpEntry appOpEntry = list4.get(i7);
            if (appOpEntry.mHash >= i6) {
                i3 = i7;
                i4 = size;
            } else {
                if (i5 == 10075) {
                    i3 = i7;
                    i4 = size;
                    buildStatsEvent = FrameworkStatsLog.buildStatsEvent(i, appOpEntry.mUid, appOpEntry.mPackageName, appOpEntry.mAttributionTag, appOpEntry.mOp.getOpCode(), appOpEntry.mOp.getForegroundAccessCount(9), appOpEntry.mOp.getBackgroundAccessCount(9), appOpEntry.mOp.getForegroundRejectCount(9), appOpEntry.mOp.getBackgroundRejectCount(9), appOpEntry.mOp.getForegroundAccessDuration(9), appOpEntry.mOp.getBackgroundAccessDuration(9), this.mDangerousAppOpsList.contains(Integer.valueOf(appOpEntry.mOp.getOpCode())), i2);
                } else {
                    i3 = i7;
                    i4 = size;
                    buildStatsEvent = FrameworkStatsLog.buildStatsEvent(i, appOpEntry.mUid, appOpEntry.mPackageName, appOpEntry.mOp.getOpCode(), appOpEntry.mOp.getForegroundAccessCount(9), appOpEntry.mOp.getBackgroundAccessCount(9), appOpEntry.mOp.getForegroundRejectCount(9), appOpEntry.mOp.getBackgroundRejectCount(9), appOpEntry.mOp.getForegroundAccessDuration(9), appOpEntry.mOp.getBackgroundAccessDuration(9), this.mDangerousAppOpsList.contains(Integer.valueOf(appOpEntry.mOp.getOpCode())));
                }
                list3 = list;
                list3.add(buildStatsEvent);
            }
            i7 = i3 + 1;
            list4 = list2;
            i5 = i;
            i6 = i2;
            size = i4;
        }
        if (list.size() > 800) {
            int constrain = MathUtils.constrain((i2 * 500) / list.size(), 0, i2 - 1);
            list.clear();
            return sampleAppOps(list3, list2, i, constrain);
        }
        return i2;
    }

    public final void registerAttributedAppOps() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.ATTRIBUTED_APP_OPS, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullAttributedAppOpsLocked(int i, List<StatsEvent> list) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            CompletableFuture completableFuture = new CompletableFuture();
            ((AppOpsManager) this.mContext.getSystemService(AppOpsManager.class)).getHistoricalOps(new AppOpsManager.HistoricalOpsRequest.Builder(0L, Long.MAX_VALUE).setFlags(9).build(), AsyncTask.THREAD_POOL_EXECUTOR, new StatsPullAtomService$$ExternalSyntheticLambda11(completableFuture));
            AppOpsManager.HistoricalOps historicalOps = (AppOpsManager.HistoricalOps) completableFuture.get(2000L, TimeUnit.MILLISECONDS);
            if (this.mAppOpsSamplingRate == 0) {
                this.mContext.getMainThreadHandler().postDelayed(new Runnable() { // from class: com.android.server.stats.pull.StatsPullAtomService.4
                    @Override // java.lang.Runnable
                    public void run() {
                        try {
                            StatsPullAtomService.this.estimateAppOpsSamplingRate();
                        } finally {
                        }
                    }
                }, 45000L);
                this.mAppOpsSamplingRate = 100;
            }
            this.mAppOpsSamplingRate = Math.min(this.mAppOpsSamplingRate, sampleAppOps(list, processHistoricalOps(historicalOps, i, this.mAppOpsSamplingRate), i, this.mAppOpsSamplingRate));
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return 0;
        } catch (Throwable th) {
            try {
                Slog.e("StatsPullAtomService", "Could not read appops", th);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return 1;
            } catch (Throwable th2) {
                Binder.restoreCallingIdentity(clearCallingIdentity);
                throw th2;
            }
        }
    }

    public final void estimateAppOpsSamplingRate() throws Exception {
        int i = DeviceConfig.getInt("permissions", "app_ops_target_collection_size", 2000);
        CompletableFuture completableFuture = new CompletableFuture();
        long j = 0;
        ((AppOpsManager) this.mContext.getSystemService(AppOpsManager.class)).getHistoricalOps(new AppOpsManager.HistoricalOpsRequest.Builder(Math.max(Instant.now().minus(1L, (TemporalUnit) ChronoUnit.DAYS).toEpochMilli(), 0L), Long.MAX_VALUE).setFlags(9).build(), AsyncTask.THREAD_POOL_EXECUTOR, new StatsPullAtomService$$ExternalSyntheticLambda11(completableFuture));
        List<AppOpEntry> processHistoricalOps = processHistoricalOps((AppOpsManager.HistoricalOps) completableFuture.get(2000L, TimeUnit.MILLISECONDS), FrameworkStatsLog.ATTRIBUTED_APP_OPS, 100);
        int size = processHistoricalOps.size();
        for (int i2 = 0; i2 < size; i2++) {
            AppOpEntry appOpEntry = processHistoricalOps.get(i2);
            int length = appOpEntry.mPackageName.length() + 32;
            String str = appOpEntry.mAttributionTag;
            j += length + (str == null ? 1 : str.length());
        }
        int constrain = (int) MathUtils.constrain((i * 100) / j, 0L, 100L);
        synchronized (this.mAttributedAppOpsLock) {
            this.mAppOpsSamplingRate = Math.min(this.mAppOpsSamplingRate, constrain);
        }
    }

    public final List<AppOpEntry> processHistoricalOps(AppOpsManager.HistoricalOps historicalOps, int i, int i2) {
        ArrayList arrayList = new ArrayList();
        for (int i3 = 0; i3 < historicalOps.getUidCount(); i3++) {
            AppOpsManager.HistoricalUidOps uidOpsAt = historicalOps.getUidOpsAt(i3);
            int uid = uidOpsAt.getUid();
            for (int i4 = 0; i4 < uidOpsAt.getPackageCount(); i4++) {
                AppOpsManager.HistoricalPackageOps packageOpsAt = uidOpsAt.getPackageOpsAt(i4);
                if (i == 10075) {
                    int i5 = 0;
                    while (i5 < packageOpsAt.getAttributedOpsCount()) {
                        int i6 = 0;
                        for (AppOpsManager.AttributedHistoricalOps attributedOpsAt = packageOpsAt.getAttributedOpsAt(i5); i6 < attributedOpsAt.getOpCount(); attributedOpsAt = attributedOpsAt) {
                            processHistoricalOp(attributedOpsAt.getOpAt(i6), arrayList, uid, i2, packageOpsAt.getPackageName(), attributedOpsAt.getTag());
                            i6++;
                            i5 = i5;
                        }
                        i5++;
                    }
                } else if (i == 10060) {
                    for (int i7 = 0; i7 < packageOpsAt.getOpCount(); i7++) {
                        processHistoricalOp(packageOpsAt.getOpAt(i7), arrayList, uid, i2, packageOpsAt.getPackageName(), null);
                    }
                }
            }
        }
        return arrayList;
    }

    public final void processHistoricalOp(AppOpsManager.HistoricalOp historicalOp, List<AppOpEntry> list, int i, int i2, String str, String str2) {
        int i3;
        if (str2 == null || !str2.startsWith(str)) {
            i3 = 0;
        } else {
            i3 = str.length();
            if (i3 < str2.length() && str2.charAt(i3) == '.') {
                i3++;
            }
        }
        AppOpEntry appOpEntry = new AppOpEntry(str, str2 == null ? null : str2.substring(i3), historicalOp, i);
        if (appOpEntry.mHash < i2) {
            list.add(appOpEntry);
        }
    }

    public int pullRuntimeAppOpAccessMessageLocked(int i, List<StatsEvent> list) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            RuntimeAppOpAccessMessage collectRuntimeAppOpAccessMessage = ((AppOpsManager) this.mContext.getSystemService(AppOpsManager.class)).collectRuntimeAppOpAccessMessage();
            if (collectRuntimeAppOpAccessMessage == null) {
                Slog.i("StatsPullAtomService", "No runtime appop access message collected");
                return 0;
            }
            list.add(FrameworkStatsLog.buildStatsEvent(i, collectRuntimeAppOpAccessMessage.getUid(), collectRuntimeAppOpAccessMessage.getPackageName(), "", collectRuntimeAppOpAccessMessage.getAttributionTag() == null ? "" : collectRuntimeAppOpAccessMessage.getAttributionTag(), collectRuntimeAppOpAccessMessage.getMessage(), collectRuntimeAppOpAccessMessage.getSamplingStrategy(), AppOpsManager.strOpToOp(collectRuntimeAppOpAccessMessage.getOp())));
            return 0;
        } catch (Throwable th) {
            try {
                Slog.e("StatsPullAtomService", "Could not read runtime appop access message", th);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return 1;
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    public static void unpackStreamedData(int i, List<StatsEvent> list, List<ParcelFileDescriptor> list2) throws IOException {
        ParcelFileDescriptor.AutoCloseInputStream autoCloseInputStream = new ParcelFileDescriptor.AutoCloseInputStream(list2.get(0));
        int[] iArr = new int[1];
        list.add(FrameworkStatsLog.buildStatsEvent(i, Arrays.copyOf(readFully(autoCloseInputStream, iArr), iArr[0])));
    }

    public static byte[] readFully(InputStream inputStream, int[] iArr) throws IOException {
        int available = inputStream.available();
        byte[] bArr = new byte[available > 0 ? available + 1 : 16384];
        int i = 0;
        while (true) {
            int read = inputStream.read(bArr, i, bArr.length - i);
            Slog.i("StatsPullAtomService", "Read " + read + " bytes at " + i + " of avail " + bArr.length);
            if (read < 0) {
                Slog.i("StatsPullAtomService", "**** FINISHED READING: pos=" + i + " len=" + bArr.length);
                iArr[0] = i;
                return bArr;
            }
            i += read;
            if (i >= bArr.length) {
                int i2 = i + 16384;
                byte[] bArr2 = new byte[i2];
                Slog.i("StatsPullAtomService", "Copying " + i + " bytes to new array len " + i2);
                System.arraycopy(bArr, 0, bArr2, 0, i);
                bArr = bArr2;
            }
        }
    }

    public final void registerNotificationRemoteViews() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.NOTIFICATION_REMOTE_VIEWS, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullNotificationRemoteViewsLocked(int i, List<StatsEvent> list) {
        INotificationManager iNotificationManagerService = getINotificationManagerService();
        if (iNotificationManagerService == null) {
            return 1;
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            ArrayList arrayList = new ArrayList();
            iNotificationManagerService.pullStats((SystemClock.currentTimeMicro() * 1000) - TimeUnit.NANOSECONDS.convert(1L, TimeUnit.DAYS), 1, true, arrayList);
            if (arrayList.size() != 1) {
                return 1;
            }
            unpackStreamedData(i, list, arrayList);
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return 0;
        } catch (RemoteException e) {
            Slog.e("StatsPullAtomService", "Getting notistats failed: ", e);
            return 1;
        } catch (IOException e2) {
            Slog.e("StatsPullAtomService", "Getting notistats failed: ", e2);
            return 1;
        } catch (SecurityException e3) {
            Slog.e("StatsPullAtomService", "Getting notistats failed: ", e3);
            return 1;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final void registerDangerousPermissionStateSampled() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.DANGEROUS_PERMISSION_STATE_SAMPLED, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public final void registerBatteryLevel() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.BATTERY_LEVEL, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public final void registerRemainingBatteryCapacity() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.REMAINING_BATTERY_CAPACITY, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public final void registerFullBatteryCapacity() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.FULL_BATTERY_CAPACITY, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public final void registerBatteryVoltage() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.BATTERY_VOLTAGE, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public final void registerBatteryCycleCount() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.BATTERY_CYCLE_COUNT, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullHealthHalLocked(int i, List<StatsEvent> list) {
        int i2;
        HealthServiceWrapper healthServiceWrapper = this.mHealthService;
        if (healthServiceWrapper == null) {
            return 1;
        }
        try {
            HealthInfo healthInfo = healthServiceWrapper.getHealthInfo();
            if (healthInfo == null) {
                return 1;
            }
            if (i == 10019) {
                i2 = healthInfo.batteryChargeCounterUah;
            } else if (i == 10020) {
                i2 = healthInfo.batteryFullChargeUah;
            } else if (i == 10030) {
                i2 = healthInfo.batteryVoltageMillivolts;
            } else if (i == 10043) {
                i2 = healthInfo.batteryLevel;
            } else if (i != 10045) {
                return 1;
            } else {
                i2 = healthInfo.batteryCycleCount;
            }
            list.add(FrameworkStatsLog.buildStatsEvent(i, i2));
            return 0;
        } catch (RemoteException | IllegalStateException unused) {
            return 1;
        }
    }

    public final void registerSettingsStats() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.SETTING_SNAPSHOT, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullSettingsStatsLocked(int i, List<StatsEvent> list) {
        UserManager userManager = (UserManager) this.mContext.getSystemService(UserManager.class);
        if (userManager == null) {
            return 1;
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            for (UserInfo userInfo : userManager.getUsers()) {
                int identifier = userInfo.getUserHandle().getIdentifier();
                if (identifier == 0) {
                    list.addAll(SettingsStatsUtil.logGlobalSettings(this.mContext, i, 0));
                }
                list.addAll(SettingsStatsUtil.logSystemSettings(this.mContext, i, identifier));
                list.addAll(SettingsStatsUtil.logSecureSettings(this.mContext, i, identifier));
            }
            return 0;
        } catch (Exception e) {
            Slog.e("StatsPullAtomService", "failed to pullSettingsStats", e);
            return 1;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final void registerInstalledIncrementalPackages() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.INSTALLED_INCREMENTAL_PACKAGE, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullInstalledIncrementalPackagesLocked(int i, List<StatsEvent> list) {
        PackageManager packageManager = this.mContext.getPackageManager();
        if (packageManager.hasSystemFeature("android.software.incremental_delivery")) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                try {
                    for (int i2 : ((UserManagerInternal) LocalServices.getService(UserManagerInternal.class)).getUserIds()) {
                        for (PackageInfo packageInfo : packageManager.getInstalledPackagesAsUser(0, i2)) {
                            if (IncrementalManager.isIncrementalPath(packageInfo.applicationInfo.getBaseCodePath())) {
                                list.add(FrameworkStatsLog.buildStatsEvent(i, packageInfo.applicationInfo.uid));
                            }
                        }
                    }
                    return 0;
                } catch (Exception e) {
                    Slog.e("StatsPullAtomService", "failed to pullInstalledIncrementalPackagesLocked", e);
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    return 1;
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
        return 0;
    }

    public final void registerKeystoreStorageStats() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.KEYSTORE2_STORAGE_STATS, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public final void registerRkpPoolStats() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.RKP_POOL_STATS, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public final void registerKeystoreKeyCreationWithGeneralInfo() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.KEYSTORE2_KEY_CREATION_WITH_GENERAL_INFO, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public final void registerKeystoreKeyCreationWithAuthInfo() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.KEYSTORE2_KEY_CREATION_WITH_AUTH_INFO, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public final void registerKeystoreKeyCreationWithPurposeModesInfo() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.KEYSTORE2_KEY_CREATION_WITH_PURPOSE_AND_MODES_INFO, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public final void registerKeystoreAtomWithOverflow() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.KEYSTORE2_ATOM_WITH_OVERFLOW, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public final void registerKeystoreKeyOperationWithPurposeAndModesInfo() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.KEYSTORE2_KEY_OPERATION_WITH_PURPOSE_AND_MODES_INFO, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public final void registerKeystoreKeyOperationWithGeneralInfo() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.KEYSTORE2_KEY_OPERATION_WITH_GENERAL_INFO, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public final void registerRkpErrorStats() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.RKP_ERROR_STATS, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public final void registerKeystoreCrashStats() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.KEYSTORE2_CRASH_STATS, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public final void registerAccessibilityShortcutStats() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.ACCESSIBILITY_SHORTCUT_STATS, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public final void registerAccessibilityFloatingMenuStats() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.ACCESSIBILITY_FLOATING_MENU_STATS, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public final void registerMediaCapabilitiesStats() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.MEDIA_CAPABILITIES, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int parseKeystoreStorageStats(KeystoreAtom[] keystoreAtomArr, List<StatsEvent> list) {
        for (KeystoreAtom keystoreAtom : keystoreAtomArr) {
            if (keystoreAtom.payload.getTag() != 0) {
                return 1;
            }
            StorageStats storageStats = keystoreAtom.payload.getStorageStats();
            list.add(FrameworkStatsLog.buildStatsEvent((int) FrameworkStatsLog.KEYSTORE2_STORAGE_STATS, storageStats.storage_type, storageStats.size, storageStats.unused_size));
        }
        return 0;
    }

    public int parseRkpPoolStats(KeystoreAtom[] keystoreAtomArr, List<StatsEvent> list) {
        for (KeystoreAtom keystoreAtom : keystoreAtomArr) {
            if (keystoreAtom.payload.getTag() != 1) {
                return 1;
            }
            RkpPoolStats rkpPoolStats = keystoreAtom.payload.getRkpPoolStats();
            list.add(FrameworkStatsLog.buildStatsEvent((int) FrameworkStatsLog.RKP_POOL_STATS, rkpPoolStats.security_level, rkpPoolStats.expiring, rkpPoolStats.unassigned, rkpPoolStats.attested, rkpPoolStats.total));
        }
        return 0;
    }

    public int parseKeystoreKeyCreationWithGeneralInfo(KeystoreAtom[] keystoreAtomArr, List<StatsEvent> list) {
        for (KeystoreAtom keystoreAtom : keystoreAtomArr) {
            if (keystoreAtom.payload.getTag() != 2) {
                return 1;
            }
            KeyCreationWithGeneralInfo keyCreationWithGeneralInfo = keystoreAtom.payload.getKeyCreationWithGeneralInfo();
            list.add(FrameworkStatsLog.buildStatsEvent((int) FrameworkStatsLog.KEYSTORE2_KEY_CREATION_WITH_GENERAL_INFO, keyCreationWithGeneralInfo.algorithm, keyCreationWithGeneralInfo.key_size, keyCreationWithGeneralInfo.ec_curve, keyCreationWithGeneralInfo.key_origin, keyCreationWithGeneralInfo.error_code, keyCreationWithGeneralInfo.attestation_requested, keystoreAtom.count));
        }
        return 0;
    }

    public int parseKeystoreKeyCreationWithAuthInfo(KeystoreAtom[] keystoreAtomArr, List<StatsEvent> list) {
        for (KeystoreAtom keystoreAtom : keystoreAtomArr) {
            if (keystoreAtom.payload.getTag() != 3) {
                return 1;
            }
            KeyCreationWithAuthInfo keyCreationWithAuthInfo = keystoreAtom.payload.getKeyCreationWithAuthInfo();
            list.add(FrameworkStatsLog.buildStatsEvent((int) FrameworkStatsLog.KEYSTORE2_KEY_CREATION_WITH_AUTH_INFO, keyCreationWithAuthInfo.user_auth_type, keyCreationWithAuthInfo.log10_auth_key_timeout_seconds, keyCreationWithAuthInfo.security_level, keystoreAtom.count));
        }
        return 0;
    }

    public int parseKeystoreKeyCreationWithPurposeModesInfo(KeystoreAtom[] keystoreAtomArr, List<StatsEvent> list) {
        for (KeystoreAtom keystoreAtom : keystoreAtomArr) {
            if (keystoreAtom.payload.getTag() != 4) {
                return 1;
            }
            KeyCreationWithPurposeAndModesInfo keyCreationWithPurposeAndModesInfo = keystoreAtom.payload.getKeyCreationWithPurposeAndModesInfo();
            list.add(FrameworkStatsLog.buildStatsEvent((int) FrameworkStatsLog.KEYSTORE2_KEY_CREATION_WITH_PURPOSE_AND_MODES_INFO, keyCreationWithPurposeAndModesInfo.algorithm, keyCreationWithPurposeAndModesInfo.purpose_bitmap, keyCreationWithPurposeAndModesInfo.padding_mode_bitmap, keyCreationWithPurposeAndModesInfo.digest_bitmap, keyCreationWithPurposeAndModesInfo.block_mode_bitmap, keystoreAtom.count));
        }
        return 0;
    }

    public int parseKeystoreAtomWithOverflow(KeystoreAtom[] keystoreAtomArr, List<StatsEvent> list) {
        for (KeystoreAtom keystoreAtom : keystoreAtomArr) {
            if (keystoreAtom.payload.getTag() != 5) {
                return 1;
            }
            list.add(FrameworkStatsLog.buildStatsEvent((int) FrameworkStatsLog.KEYSTORE2_ATOM_WITH_OVERFLOW, keystoreAtom.payload.getKeystore2AtomWithOverflow().atom_id, keystoreAtom.count));
        }
        return 0;
    }

    public int parseKeystoreKeyOperationWithPurposeModesInfo(KeystoreAtom[] keystoreAtomArr, List<StatsEvent> list) {
        for (KeystoreAtom keystoreAtom : keystoreAtomArr) {
            if (keystoreAtom.payload.getTag() != 6) {
                return 1;
            }
            KeyOperationWithPurposeAndModesInfo keyOperationWithPurposeAndModesInfo = keystoreAtom.payload.getKeyOperationWithPurposeAndModesInfo();
            list.add(FrameworkStatsLog.buildStatsEvent((int) FrameworkStatsLog.KEYSTORE2_KEY_OPERATION_WITH_PURPOSE_AND_MODES_INFO, keyOperationWithPurposeAndModesInfo.purpose, keyOperationWithPurposeAndModesInfo.padding_mode_bitmap, keyOperationWithPurposeAndModesInfo.digest_bitmap, keyOperationWithPurposeAndModesInfo.block_mode_bitmap, keystoreAtom.count));
        }
        return 0;
    }

    public int parseKeystoreKeyOperationWithGeneralInfo(KeystoreAtom[] keystoreAtomArr, List<StatsEvent> list) {
        for (KeystoreAtom keystoreAtom : keystoreAtomArr) {
            if (keystoreAtom.payload.getTag() != 7) {
                return 1;
            }
            KeyOperationWithGeneralInfo keyOperationWithGeneralInfo = keystoreAtom.payload.getKeyOperationWithGeneralInfo();
            list.add(FrameworkStatsLog.buildStatsEvent((int) FrameworkStatsLog.KEYSTORE2_KEY_OPERATION_WITH_GENERAL_INFO, keyOperationWithGeneralInfo.outcome, keyOperationWithGeneralInfo.error_code, keyOperationWithGeneralInfo.key_upgraded, keyOperationWithGeneralInfo.security_level, keystoreAtom.count));
        }
        return 0;
    }

    public int parseRkpErrorStats(KeystoreAtom[] keystoreAtomArr, List<StatsEvent> list) {
        for (KeystoreAtom keystoreAtom : keystoreAtomArr) {
            if (keystoreAtom.payload.getTag() != 8) {
                return 1;
            }
            RkpErrorStats rkpErrorStats = keystoreAtom.payload.getRkpErrorStats();
            list.add(FrameworkStatsLog.buildStatsEvent((int) FrameworkStatsLog.RKP_ERROR_STATS, rkpErrorStats.rkpError, keystoreAtom.count, rkpErrorStats.security_level));
        }
        return 0;
    }

    public int parseKeystoreCrashStats(KeystoreAtom[] keystoreAtomArr, List<StatsEvent> list) {
        for (KeystoreAtom keystoreAtom : keystoreAtomArr) {
            if (keystoreAtom.payload.getTag() != 9) {
                return 1;
            }
            list.add(FrameworkStatsLog.buildStatsEvent((int) FrameworkStatsLog.KEYSTORE2_CRASH_STATS, keystoreAtom.payload.getCrashStats().count_of_crash_events));
        }
        return 0;
    }

    public int pullKeystoreAtoms(int i, List<StatsEvent> list) {
        IKeystoreMetrics iKeystoreMetricsService = getIKeystoreMetricsService();
        if (iKeystoreMetricsService == null) {
            Slog.w("StatsPullAtomService", "Keystore service is null");
            return 1;
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            KeystoreAtom[] pullMetrics = iKeystoreMetricsService.pullMetrics(i);
            if (i != 10103) {
                if (i != 10104) {
                    switch (i) {
                        case FrameworkStatsLog.KEYSTORE2_KEY_CREATION_WITH_GENERAL_INFO /* 10118 */:
                            return parseKeystoreKeyCreationWithGeneralInfo(pullMetrics, list);
                        case FrameworkStatsLog.KEYSTORE2_KEY_CREATION_WITH_AUTH_INFO /* 10119 */:
                            return parseKeystoreKeyCreationWithAuthInfo(pullMetrics, list);
                        case FrameworkStatsLog.KEYSTORE2_KEY_CREATION_WITH_PURPOSE_AND_MODES_INFO /* 10120 */:
                            return parseKeystoreKeyCreationWithPurposeModesInfo(pullMetrics, list);
                        case FrameworkStatsLog.KEYSTORE2_ATOM_WITH_OVERFLOW /* 10121 */:
                            return parseKeystoreAtomWithOverflow(pullMetrics, list);
                        case FrameworkStatsLog.KEYSTORE2_KEY_OPERATION_WITH_PURPOSE_AND_MODES_INFO /* 10122 */:
                            return parseKeystoreKeyOperationWithPurposeModesInfo(pullMetrics, list);
                        case FrameworkStatsLog.KEYSTORE2_KEY_OPERATION_WITH_GENERAL_INFO /* 10123 */:
                            return parseKeystoreKeyOperationWithGeneralInfo(pullMetrics, list);
                        case FrameworkStatsLog.RKP_ERROR_STATS /* 10124 */:
                            return parseRkpErrorStats(pullMetrics, list);
                        case FrameworkStatsLog.KEYSTORE2_CRASH_STATS /* 10125 */:
                            return parseKeystoreCrashStats(pullMetrics, list);
                        default:
                            Slog.w("StatsPullAtomService", "Unsupported keystore atom: " + i);
                            return 1;
                    }
                }
                return parseRkpPoolStats(pullMetrics, list);
            }
            return parseKeystoreStorageStats(pullMetrics, list);
        } catch (RemoteException e) {
            Slog.e("StatsPullAtomService", "Disconnected from keystore service. Cannot pull.", e);
            return 1;
        } catch (ServiceSpecificException e2) {
            Slog.e("StatsPullAtomService", "pulling keystore metrics failed", e2);
            return 1;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public int pullAccessibilityShortcutStatsLocked(int i, List<StatsEvent> list) {
        UserManager userManager = (UserManager) this.mContext.getSystemService(UserManager.class);
        if (userManager == null) {
            return 1;
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            ContentResolver contentResolver = this.mContext.getContentResolver();
            for (UserInfo userInfo : userManager.getUsers()) {
                int identifier = userInfo.getUserHandle().getIdentifier();
                if (isAccessibilityShortcutUser(this.mContext, identifier)) {
                    list.add(FrameworkStatsLog.buildStatsEvent(i, convertToAccessibilityShortcutType(Settings.Secure.getIntForUser(contentResolver, "accessibility_button_mode", 0, identifier)), countAccessibilityServices(Settings.Secure.getStringForUser(contentResolver, "accessibility_button_targets", identifier)), 2, countAccessibilityServices(Settings.Secure.getStringForUser(contentResolver, "accessibility_shortcut_target_service", identifier)), 3, Settings.Secure.getIntForUser(contentResolver, "accessibility_display_magnification_enabled", 0, identifier)));
                }
            }
            return 0;
        } catch (RuntimeException e) {
            Slog.e("StatsPullAtomService", "pulling accessibility shortcuts stats failed at getUsers", e);
            return 1;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public int pullAccessibilityFloatingMenuStatsLocked(int i, List<StatsEvent> list) {
        UserManager userManager = (UserManager) this.mContext.getSystemService(UserManager.class);
        if (userManager == null) {
            return 1;
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            ContentResolver contentResolver = this.mContext.getContentResolver();
            Iterator it = userManager.getUsers().iterator();
            while (true) {
                if (!it.hasNext()) {
                    return 0;
                }
                int identifier = ((UserInfo) it.next()).getUserHandle().getIdentifier();
                if (isAccessibilityFloatingMenuUser(this.mContext, identifier)) {
                    list.add(FrameworkStatsLog.buildStatsEvent(i, Settings.Secure.getIntForUser(contentResolver, "accessibility_floating_menu_size", 0, identifier), Settings.Secure.getIntForUser(contentResolver, "accessibility_floating_menu_icon_type", 0, identifier), Settings.Secure.getIntForUser(contentResolver, "accessibility_floating_menu_fade_enabled", 1, identifier) == 1, Settings.Secure.getFloatForUser(contentResolver, "accessibility_floating_menu_opacity", 0.55f, identifier)));
                }
            }
        } catch (RuntimeException e) {
            Slog.e("StatsPullAtomService", "pulling accessibility floating menu stats failed at getUsers", e);
            return 1;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public int pullMediaCapabilitiesStats(int i, List<StatsEvent> list) {
        AudioManager audioManager;
        int i2;
        boolean z;
        if (this.mContext.getPackageManager().hasSystemFeature("android.software.leanback") && (audioManager = (AudioManager) this.mContext.getSystemService(AudioManager.class)) != null) {
            Map surroundFormats = audioManager.getSurroundFormats();
            byte[] bytes = toBytes(new ArrayList(surroundFormats.keySet()));
            byte[] bytes2 = toBytes(audioManager.getReportedSurroundFormats());
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            for (Integer num : surroundFormats.keySet()) {
                int intValue = num.intValue();
                if (!((Boolean) surroundFormats.get(Integer.valueOf(intValue))).booleanValue()) {
                    arrayList.add(Integer.valueOf(intValue));
                } else {
                    arrayList2.add(Integer.valueOf(intValue));
                }
            }
            byte[] bytes3 = toBytes(arrayList);
            byte[] bytes4 = toBytes(arrayList2);
            int encodedSurroundMode = audioManager.getEncodedSurroundMode();
            DisplayManager displayManager = (DisplayManager) this.mContext.getSystemService(DisplayManager.class);
            Display display = displayManager.getDisplay(0);
            Display.HdrCapabilities hdrCapabilities = display.getHdrCapabilities();
            byte[] bytes5 = hdrCapabilities != null ? toBytes(hdrCapabilities.getSupportedHdrTypes()) : new byte[0];
            byte[] bytes6 = toBytes(display.getSupportedModes());
            List<UUID> supportedCryptoSchemes = MediaDrm.getSupportedCryptoSchemes();
            try {
                i2 = !supportedCryptoSchemes.isEmpty() ? new MediaDrm(supportedCryptoSchemes.get(0)).getConnectedHdcpLevel() : -1;
            } catch (UnsupportedSchemeException e) {
                Slog.e("StatsPullAtomService", "pulling hdcp level failed.", e);
                i2 = -1;
            }
            int matchContentFrameRateUserPreference = displayManager.getMatchContentFrameRateUserPreference();
            byte[] bytes7 = toBytes(displayManager.getUserDisabledHdrTypes());
            Display.Mode globalUserPreferredDisplayMode = displayManager.getGlobalUserPreferredDisplayMode();
            int physicalWidth = globalUserPreferredDisplayMode != null ? globalUserPreferredDisplayMode.getPhysicalWidth() : -1;
            int physicalHeight = globalUserPreferredDisplayMode != null ? globalUserPreferredDisplayMode.getPhysicalHeight() : -1;
            float refreshRate = globalUserPreferredDisplayMode != null ? globalUserPreferredDisplayMode.getRefreshRate() : 0.0f;
            try {
                z = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "minimal_post_processing_allowed", 1) == 0;
            } catch (Settings.SettingNotFoundException e2) {
                Slog.e("StatsPullAtomService", "unable to find setting for MINIMAL_POST_PROCESSING_ALLOWED.", e2);
                z = false;
            }
            list.add(FrameworkStatsLog.buildStatsEvent(i, bytes, bytes2, bytes3, bytes4, encodedSurroundMode, bytes5, bytes6, i2, matchContentFrameRateUserPreference, bytes7, physicalWidth, physicalHeight, refreshRate, z));
            return 0;
        }
        return 1;
    }

    public final void registerPendingIntentsPerPackagePuller() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.PENDING_INTENTS_PER_PACKAGE, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public final int pullHdrCapabilities(int i, List<StatsEvent> list) {
        DisplayManager displayManager = (DisplayManager) this.mContext.getSystemService(DisplayManager.class);
        Display display = displayManager.getDisplay(0);
        int conversionMode = displayManager.getHdrConversionMode().getConversionMode();
        int preferredHdrOutputType = displayManager.getHdrConversionMode().getPreferredHdrOutputType();
        boolean z = conversionMode == 1;
        if (preferredHdrOutputType == -1) {
            preferredHdrOutputType = 0;
        }
        list.add(FrameworkStatsLog.buildStatsEvent(i, new byte[0], z, preferredHdrOutputType, hasDolbyVisionIssue(display)));
        return 0;
    }

    public final boolean hasDolbyVisionIssue(Display display) {
        final AtomicInteger atomicInteger = new AtomicInteger();
        Arrays.stream(display.getSupportedModes()).map(new Function() { // from class: com.android.server.stats.pull.StatsPullAtomService$$ExternalSyntheticLambda17
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((Display.Mode) obj).getSupportedHdrTypes();
            }
        }).filter(new Predicate() { // from class: com.android.server.stats.pull.StatsPullAtomService$$ExternalSyntheticLambda18
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$hasDolbyVisionIssue$23;
                lambda$hasDolbyVisionIssue$23 = StatsPullAtomService.lambda$hasDolbyVisionIssue$23((int[]) obj);
                return lambda$hasDolbyVisionIssue$23;
            }
        }).forEach(new Consumer() { // from class: com.android.server.stats.pull.StatsPullAtomService$$ExternalSyntheticLambda19
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                int[] iArr = (int[]) obj;
                atomicInteger.incrementAndGet();
            }
        });
        return atomicInteger.get() != 0 && atomicInteger.get() < display.getSupportedModes().length;
    }

    public static /* synthetic */ boolean lambda$hasDolbyVisionIssue$23(int[] iArr) {
        return Arrays.stream(iArr).anyMatch(new IntPredicate() { // from class: com.android.server.stats.pull.StatsPullAtomService$$ExternalSyntheticLambda27
            @Override // java.util.function.IntPredicate
            public final boolean test(int i) {
                boolean lambda$hasDolbyVisionIssue$22;
                lambda$hasDolbyVisionIssue$22 = StatsPullAtomService.lambda$hasDolbyVisionIssue$22(i);
                return lambda$hasDolbyVisionIssue$22;
            }
        });
    }

    public final int pullPendingIntentsPerPackage(int i, List<StatsEvent> list) {
        for (PendingIntentStats pendingIntentStats : ((ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class)).getPendingIntentStats()) {
            list.add(FrameworkStatsLog.buildStatsEvent(i, pendingIntentStats.uid, pendingIntentStats.count, pendingIntentStats.sizeKb));
        }
        return 0;
    }

    public final void registerPinnerServiceStats() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.PINNED_FILE_SIZES_PER_PACKAGE, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public final void registerHdrCapabilitiesPuller() {
        this.mStatsManager.setPullAtomCallback((int) FrameworkStatsLog.HDR_CAPABILITIES, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this.mStatsCallbackImpl);
    }

    public int pullSystemServerPinnerStats(int i, List<StatsEvent> list) {
        for (PinnerService.PinnedFileStats pinnedFileStats : ((PinnerService) LocalServices.getService(PinnerService.class)).dumpDataForStatsd()) {
            list.add(FrameworkStatsLog.buildStatsEvent(i, pinnedFileStats.uid, pinnedFileStats.filename, pinnedFileStats.sizeKb));
        }
        return 0;
    }

    public final byte[] toBytes(List<Integer> list) {
        ProtoOutputStream protoOutputStream = new ProtoOutputStream();
        for (Integer num : list) {
            protoOutputStream.write(2259152797697L, num.intValue());
        }
        return protoOutputStream.getBytes();
    }

    public final byte[] toBytes(int[] iArr) {
        ProtoOutputStream protoOutputStream = new ProtoOutputStream();
        for (int i : iArr) {
            protoOutputStream.write(2259152797697L, i);
        }
        return protoOutputStream.getBytes();
    }

    public final byte[] toBytes(Display.Mode[] modeArr) {
        Map<Integer, Integer> createModeGroups = createModeGroups(modeArr);
        ProtoOutputStream protoOutputStream = new ProtoOutputStream();
        for (Display.Mode mode : modeArr) {
            ProtoOutputStream protoOutputStream2 = new ProtoOutputStream();
            protoOutputStream2.write(1120986464257L, mode.getPhysicalHeight());
            protoOutputStream2.write(1120986464258L, mode.getPhysicalWidth());
            protoOutputStream2.write(1108101562371L, mode.getRefreshRate());
            protoOutputStream2.write(1120986464260L, createModeGroups.get(Integer.valueOf(mode.getModeId())).intValue());
            protoOutputStream.write(2246267895809L, protoOutputStream2.getBytes());
        }
        return protoOutputStream.getBytes();
    }

    public final Map<Integer, Integer> createModeGroups(Display.Mode[] modeArr) {
        ArrayMap arrayMap = new ArrayMap();
        int i = 1;
        for (Display.Mode mode : modeArr) {
            if (!arrayMap.containsKey(Integer.valueOf(mode.getModeId()))) {
                arrayMap.put(Integer.valueOf(mode.getModeId()), Integer.valueOf(i));
                for (float f : mode.getAlternativeRefreshRates()) {
                    int findModeId = findModeId(modeArr, mode.getPhysicalWidth(), mode.getPhysicalHeight(), f);
                    if (findModeId != -1 && !arrayMap.containsKey(Integer.valueOf(findModeId))) {
                        arrayMap.put(Integer.valueOf(findModeId), Integer.valueOf(i));
                    }
                }
                i++;
            }
        }
        return arrayMap;
    }

    public final int findModeId(Display.Mode[] modeArr, int i, int i2, float f) {
        for (Display.Mode mode : modeArr) {
            if (mode.matches(i, i2, f)) {
                return mode.getModeId();
            }
        }
        return -1;
    }

    public final int countAccessibilityServices(String str) {
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        int count = (int) str.chars().filter(new IntPredicate() { // from class: com.android.server.stats.pull.StatsPullAtomService$$ExternalSyntheticLambda13
            @Override // java.util.function.IntPredicate
            public final boolean test(int i) {
                boolean lambda$countAccessibilityServices$25;
                lambda$countAccessibilityServices$25 = StatsPullAtomService.lambda$countAccessibilityServices$25(i);
                return lambda$countAccessibilityServices$25;
            }
        }).count();
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        return count + 1;
    }

    public final boolean isAccessibilityShortcutUser(Context context, int i) {
        ContentResolver contentResolver = context.getContentResolver();
        String stringForUser = Settings.Secure.getStringForUser(contentResolver, "accessibility_button_targets", i);
        String stringForUser2 = Settings.Secure.getStringForUser(contentResolver, "accessibility_shortcut_target_service", i);
        return (TextUtils.isEmpty(stringForUser) ^ true) || ((Settings.Secure.getIntForUser(contentResolver, "accessibility_shortcut_dialog_shown", 0, i) == 1) && !TextUtils.isEmpty(stringForUser2)) || (Settings.Secure.getIntForUser(contentResolver, "accessibility_display_magnification_enabled", 0, i) == 1);
    }

    public final boolean isAccessibilityFloatingMenuUser(Context context, int i) {
        ContentResolver contentResolver = context.getContentResolver();
        return Settings.Secure.getIntForUser(contentResolver, "accessibility_button_mode", 0, i) == 1 && !TextUtils.isEmpty(Settings.Secure.getStringForUser(contentResolver, "accessibility_button_targets", i));
    }

    /* loaded from: classes2.dex */
    public static final class ThermalEventListener extends IThermalEventListener.Stub {
        public ThermalEventListener() {
        }

        public void notifyThrottling(Temperature temperature) {
            FrameworkStatsLog.write(189, temperature.getType(), temperature.getName(), (int) (temperature.getValue() * 10.0f), temperature.getStatus());
        }
    }

    /* loaded from: classes2.dex */
    public static final class ConnectivityStatsCallback extends ConnectivityManager.NetworkCallback {
        public ConnectivityStatsCallback() {
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public void onAvailable(Network network) {
            FrameworkStatsLog.write(98, network.getNetId(), 1);
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public void onLost(Network network) {
            FrameworkStatsLog.write(98, network.getNetId(), 2);
        }
    }

    /* loaded from: classes2.dex */
    public final class StatsSubscriptionsListener extends SubscriptionManager.OnSubscriptionsChangedListener {
        public final SubscriptionManager mSm;

        public StatsSubscriptionsListener(SubscriptionManager subscriptionManager) {
            this.mSm = subscriptionManager;
        }

        @Override // android.telephony.SubscriptionManager.OnSubscriptionsChangedListener
        public void onSubscriptionsChanged() {
            for (final SubscriptionInfo subscriptionInfo : this.mSm.getCompleteActiveSubscriptionInfoList()) {
                if (((SubInfo) CollectionUtils.find(StatsPullAtomService.this.mHistoricalSubs, new Predicate() { // from class: com.android.server.stats.pull.StatsPullAtomService$StatsSubscriptionsListener$$ExternalSyntheticLambda0
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$onSubscriptionsChanged$0;
                        lambda$onSubscriptionsChanged$0 = StatsPullAtomService.StatsSubscriptionsListener.lambda$onSubscriptionsChanged$0(subscriptionInfo, (SubInfo) obj);
                        return lambda$onSubscriptionsChanged$0;
                    }
                })) == null) {
                    int subscriptionId = subscriptionInfo.getSubscriptionId();
                    String mccString = subscriptionInfo.getMccString();
                    String mncString = subscriptionInfo.getMncString();
                    String subscriberId = StatsPullAtomService.this.mTelephony.getSubscriberId(subscriptionId);
                    if (TextUtils.isEmpty(subscriberId) || TextUtils.isEmpty(mccString) || TextUtils.isEmpty(mncString) || subscriptionInfo.getCarrierId() == -1) {
                        Slog.e("StatsPullAtomService", "subInfo of subId " + subscriptionId + " is invalid, ignored.");
                    } else {
                        SubInfo subInfo = new SubInfo(subscriptionId, subscriptionInfo.getCarrierId(), mccString, mncString, subscriberId, subscriptionInfo.isOpportunistic());
                        Slog.i("StatsPullAtomService", "subId " + subscriptionId + " added into historical sub list");
                        synchronized (StatsPullAtomService.this.mDataBytesTransferLock) {
                            StatsPullAtomService.this.mHistoricalSubs.add(subInfo);
                            StatsPullAtomService.this.mNetworkStatsBaselines.addAll(StatsPullAtomService.this.getDataUsageBytesTransferSnapshotForSub(subInfo));
                        }
                    }
                }
            }
        }

        public static /* synthetic */ boolean lambda$onSubscriptionsChanged$0(SubscriptionInfo subscriptionInfo, SubInfo subInfo) {
            return subInfo.subId == subscriptionInfo.getSubscriptionId();
        }
    }
}
