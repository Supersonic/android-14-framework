package com.android.server.power.stats;

import android.app.ActivityManager;
import android.app.usage.NetworkStatsManager;
import android.bluetooth.BluetoothActivityEnergyInfo;
import android.bluetooth.UidTraffic;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.NetworkStats;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.BatteryConsumer;
import android.os.BatteryStats;
import android.os.BatteryUsageStats;
import android.os.BatteryUsageStatsQuery;
import android.os.Binder;
import android.os.BluetoothBatteryStats;
import android.os.Build;
import android.os.Handler;
import android.os.IBatteryPropertiesRegistrar;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.ParcelFormatException;
import android.os.Parcelable;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.WakeLockStats;
import android.os.WorkSource;
import android.os.connectivity.CellularBatteryStats;
import android.os.connectivity.GpsBatteryStats;
import android.os.connectivity.WifiActivityEnergyInfo;
import android.os.connectivity.WifiBatteryStats;
import android.p005os.IInstalld;
import android.provider.Settings;
import android.telephony.CellSignalStrength;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthNr;
import android.telephony.ModemActivityInfo;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.IndentingPrintWriter;
import android.util.KeyValueListParser;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.LongSparseLongArray;
import android.util.MutableInt;
import android.util.Printer;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseDoubleArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import android.util.TimeUtils;
import android.util.Xml;
import android.view.Display;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.EventLogTags;
import com.android.internal.os.BackgroundThread;
import com.android.internal.os.BatteryStatsHistory;
import com.android.internal.os.BatteryStatsHistoryIterator;
import com.android.internal.os.BinderCallsStats;
import com.android.internal.os.BinderTransactionNameResolver;
import com.android.internal.os.Clock;
import com.android.internal.os.KernelCpuSpeedReader;
import com.android.internal.os.KernelCpuUidTimeReader;
import com.android.internal.os.KernelMemoryBandwidthStats;
import com.android.internal.os.KernelSingleUidTimeReader;
import com.android.internal.os.LongArrayMultiStateCounter;
import com.android.internal.os.LongMultiStateCounter;
import com.android.internal.os.PowerProfile;
import com.android.internal.os.RailStats;
import com.android.internal.os.RpmStats;
import com.android.internal.power.EnergyConsumerStats;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.XmlUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.net.module.util.NetworkCapabilitiesUtils;
import com.android.server.backup.BackupAgentTimeoutParameters;
import com.android.server.backup.BackupManagerConstants;
import com.android.server.clipboard.ClipboardService;
import com.android.server.power.stats.KernelWakelockStats;
import com.android.server.power.stats.SystemServerCpuThreadReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import libcore.util.EmptyArray;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes2.dex */
public class BatteryStatsImpl extends BatteryStats {
    public static final int MAX_WAKELOCKS_PER_UID;
    @VisibleForTesting
    protected static final long MOBILE_RADIO_POWER_STATE_UPDATE_FREQ_MS = 600000;
    public static final int[] SUPPORTED_PER_PROCESS_STATE_STANDARD_ENERGY_BUCKETS;
    @VisibleForTesting
    public static final int WAKE_LOCK_WEIGHT = 50;
    public static final BatteryStats.LongCounter ZERO_LONG_COUNTER;
    public static final BatteryStats.LongCounter[] ZERO_LONG_COUNTER_ARRAY;
    public final BatteryStats.HistoryEventTracker mActiveEvents;
    public int mActiveRat;
    public int mAudioOnNesting;
    public StopwatchTimer mAudioOnTimer;
    public final ArrayList<StopwatchTimer> mAudioTurnedOnTimers;
    public int mBatteryChargeUah;
    public int mBatteryHealth;
    public int mBatteryLevel;
    public int mBatteryPlugType;
    public boolean mBatteryPluggedIn;
    public long mBatteryPluggedInRealTimeMs;
    public BatteryResetListener mBatteryResetListener;
    @GuardedBy({"this"})
    @VisibleForTesting
    protected BatteryStatsConfig mBatteryStatsConfig;
    public int mBatteryStatus;
    public int mBatteryTemperature;
    public long mBatteryTimeToFullSeconds;
    public int mBatteryVoltageMv;
    public LongSamplingCounterArray mBinderThreadCpuTimesUs;
    public ControllerActivityCounterImpl mBluetoothActivity;
    public BluetoothPowerCalculator mBluetoothPowerCalculator;
    public int mBluetoothScanNesting;
    public final ArrayList<StopwatchTimer> mBluetoothScanOnTimers;
    public StopwatchTimer mBluetoothScanTimer;
    public BatteryCallback mCallback;
    public int mCameraOnNesting;
    public StopwatchTimer mCameraOnTimer;
    public final ArrayList<StopwatchTimer> mCameraTurnedOnTimers;
    public final BatteryStats.LevelStepTracker mChargeStepTracker;
    public boolean mCharging;
    public final AtomicFile mCheckinFile;
    public Clock mClock;
    @GuardedBy({"this"})
    @VisibleForTesting
    protected final Constants mConstants;
    public long[] mCpuFreqs;
    public boolean mCpuFreqsInitialized;
    public int[] mCpuPowerBracketMap;
    public CpuPowerCalculator mCpuPowerCalculator;
    @GuardedBy({"this"})
    public long mCpuTimeReadsTrackingStartTimeMs;
    @VisibleForTesting
    protected KernelCpuUidTimeReader.KernelCpuUidActiveTimeReader mCpuUidActiveTimeReader;
    @VisibleForTesting
    protected KernelCpuUidTimeReader.KernelCpuUidClusterTimeReader mCpuUidClusterTimeReader;
    @VisibleForTesting
    protected KernelCpuUidTimeReader.KernelCpuUidFreqTimeReader mCpuUidFreqTimeReader;
    @VisibleForTesting
    protected KernelCpuUidTimeReader.KernelCpuUidUserSysTimeReader mCpuUidUserSysTimeReader;
    public final BatteryStats.CpuUsageDetails mCpuUsageDetails;
    public int mCurStepMode;
    public final BatteryStats.LevelStepTracker mDailyChargeStepTracker;
    public final BatteryStats.LevelStepTracker mDailyDischargeStepTracker;
    public final AtomicFile mDailyFile;
    public final ArrayList<BatteryStats.DailyItem> mDailyItems;
    public ArrayList<BatteryStats.PackageChange> mDailyPackageChanges;
    public long mDailyStartTimeMs;
    public final Runnable mDeferSetCharging;
    public int mDeviceIdleMode;
    public StopwatchTimer mDeviceIdleModeFullTimer;
    public StopwatchTimer mDeviceIdleModeLightTimer;
    public boolean mDeviceIdling;
    public StopwatchTimer mDeviceIdlingTimer;
    public boolean mDeviceLightIdling;
    public StopwatchTimer mDeviceLightIdlingTimer;
    public int mDischargeAmountScreenDoze;
    public int mDischargeAmountScreenDozeSinceCharge;
    public int mDischargeAmountScreenOff;
    public int mDischargeAmountScreenOffSinceCharge;
    public int mDischargeAmountScreenOn;
    public int mDischargeAmountScreenOnSinceCharge;
    public LongSamplingCounter mDischargeCounter;
    public int mDischargeCurrentLevel;
    public LongSamplingCounter mDischargeDeepDozeCounter;
    public LongSamplingCounter mDischargeLightDozeCounter;
    public int mDischargePlugLevel;
    public LongSamplingCounter mDischargeScreenDozeCounter;
    public int mDischargeScreenDozeUnplugLevel;
    public LongSamplingCounter mDischargeScreenOffCounter;
    public int mDischargeScreenOffUnplugLevel;
    public int mDischargeScreenOnUnplugLevel;
    public final BatteryStats.LevelStepTracker mDischargeStepTracker;
    public int mDischargeUnplugLevel;
    public int mDisplayMismatchWtfCount;
    public final ArrayList<StopwatchTimer> mDrawTimers;
    public String mEndPlatformVersion;
    public final EnergyStatsRetriever mEnergyConsumerRetriever;
    @GuardedBy({"this"})
    @VisibleForTesting
    protected EnergyConsumerStats.Config mEnergyConsumerStatsConfig;
    public int mEstimatedBatteryCapacityMah;
    public ExternalStatsSync mExternalSync;
    public int mFlashlightOnNesting;
    public StopwatchTimer mFlashlightOnTimer;
    public final ArrayList<StopwatchTimer> mFlashlightTurnedOnTimers;
    public final ArrayList<StopwatchTimer> mFullTimers;
    public final ArrayList<StopwatchTimer> mFullWifiLockTimers;
    @GuardedBy({"this"})
    @VisibleForTesting
    protected EnergyConsumerStats mGlobalEnergyConsumerStats;
    public boolean mGlobalWifiRunning;
    public StopwatchTimer mGlobalWifiRunningTimer;
    public int mGpsNesting;
    public int mGpsSignalQualityBin;
    public final StopwatchTimer[] mGpsSignalQualityTimer;
    public Handler mHandler;
    public boolean mHasBluetoothReporting;
    public boolean mHasModemReporting;
    public boolean mHasWifiReporting;
    public boolean mHaveBatteryLevel;
    public int mHighDischargeAmountSinceCharge;
    public final BatteryStatsHistory mHistory;
    @GuardedBy({"this"})
    public boolean mIgnoreNextExternalStats;
    public int mInitStepMode;
    public boolean mInteractive;
    public StopwatchTimer mInteractiveTimer;
    public final SparseIntArray mIsolatedUidRefCounts;
    public final SparseIntArray mIsolatedUids;
    @VisibleForTesting
    protected KernelCpuSpeedReader[] mKernelCpuSpeedReaders;
    public final KernelMemoryBandwidthStats mKernelMemoryBandwidthStats;
    public final LongSparseArray<SamplingTimer> mKernelMemoryStats;
    @VisibleForTesting
    protected KernelSingleUidTimeReader mKernelSingleUidTimeReader;
    public final KernelWakelockReader mKernelWakelockReader;
    public final HashMap<String, SamplingTimer> mKernelWakelockStats;
    public final BluetoothActivityInfoCache mLastBluetoothActivityInfo;
    public int mLastChargeStepLevel;
    public int mLastDischargeStepLevel;
    public long mLastIdleTimeStartMs;
    public int mLastLearnedBatteryCapacityUah;
    public ModemActivityInfo mLastModemActivityInfo;
    @GuardedBy({"mModemNetworkLock"})
    public NetworkStats mLastModemNetworkStats;
    @VisibleForTesting
    protected ArrayList<StopwatchTimer> mLastPartialTimers;
    public long mLastRpmStatsUpdateTimeMs;
    public String mLastWakeupReason;
    public long mLastWakeupUptimeMs;
    @GuardedBy({"mWifiNetworkLock"})
    public NetworkStats mLastWifiNetworkStats;
    public long mLastWriteTimeMs;
    @VisibleForTesting
    protected AlarmInterface mLongPlugInAlarmInterface;
    public long mLongestFullIdleTimeMs;
    public long mLongestLightIdleTimeMs;
    public int mLowDischargeAmountSinceCharge;
    public int mMaxChargeStepLevel;
    public int mMaxLearnedBatteryCapacityUah;
    public int mMinDischargeStepLevel;
    public int mMinLearnedBatteryCapacityUah;
    public LongSamplingCounter mMobileRadioActiveAdjustedTime;
    public StopwatchTimer mMobileRadioActivePerAppTimer;
    public long mMobileRadioActiveStartTimeMs;
    public StopwatchTimer mMobileRadioActiveTimer;
    public LongSamplingCounter mMobileRadioActiveUnknownCount;
    public LongSamplingCounter mMobileRadioActiveUnknownTime;
    public MobileRadioPowerCalculator mMobileRadioPowerCalculator;
    public int mMobileRadioPowerState;
    public int mModStepMode;
    public ControllerActivityCounterImpl mModemActivity;
    @GuardedBy({"mModemNetworkLock"})
    public String[] mModemIfaces;
    public final Object mModemNetworkLock;
    public final LongSamplingCounter[] mNetworkByteActivityCounters;
    public final LongSamplingCounter[] mNetworkPacketActivityCounters;
    public long mNextMaxDailyDeadlineMs;
    public long mNextMinDailyDeadlineMs;
    public boolean mNoAutoReset;
    @GuardedBy({"this"})
    public int mNumAllUidCpuTimeReads;
    public int mNumConnectivityChange;
    @GuardedBy({"this"})
    public long mNumSingleUidCpuTimeReads;
    @GuardedBy({"this"})
    public int mNumUidsRemoved;
    public boolean mOnBattery;
    @VisibleForTesting
    protected boolean mOnBatteryInternal;
    public final TimeBase mOnBatteryScreenOffTimeBase;
    public final TimeBase mOnBatteryTimeBase;
    @VisibleForTesting
    protected ArrayList<StopwatchTimer> mPartialTimers;
    @GuardedBy({"this"})
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    protected Queue<UidToRemove> mPendingRemovedUids;
    public DisplayBatteryStats[] mPerDisplayBatteryStats;
    @GuardedBy({"this"})
    public boolean mPerProcStateCpuTimesAvailable;
    public RadioAccessTechnologyBatteryStats[] mPerRatBatteryStats;
    public int mPhoneDataConnectionType;
    public final StopwatchTimer[] mPhoneDataConnectionsTimer;
    public boolean mPhoneOn;
    public StopwatchTimer mPhoneOnTimer;
    public int mPhoneServiceState;
    public int mPhoneServiceStateRaw;
    public StopwatchTimer mPhoneSignalScanningTimer;
    public int mPhoneSignalStrengthBin;
    public int mPhoneSignalStrengthBinRaw;
    public final StopwatchTimer[] mPhoneSignalStrengthsTimer;
    public int mPhoneSimStateRaw;
    public final PlatformIdleStateCallback mPlatformIdleStateCallback;
    @VisibleForTesting
    protected PowerProfile mPowerProfile;
    public boolean mPowerSaveModeEnabled;
    public StopwatchTimer mPowerSaveModeEnabledTimer;
    public boolean mPretendScreenOff;
    public long mRealtimeStartUs;
    public long mRealtimeUs;
    public boolean mRecordAllHistory;
    public final HashMap<String, SamplingTimer> mRpmStats;
    public int mScreenBrightnessBin;
    public final StopwatchTimer[] mScreenBrightnessTimer;
    public StopwatchTimer mScreenDozeTimer;
    public final HashMap<String, SamplingTimer> mScreenOffRpmStats;
    public StopwatchTimer mScreenOnTimer;
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    protected int mScreenState;
    public int mSensorNesting;
    public final SparseArray<ArrayList<StopwatchTimer>> mSensorTimers;
    public boolean mShuttingDown;
    public long mStartClockTimeMs;
    public int mStartCount;
    public String mStartPlatformVersion;
    public final AtomicFile mStatsFile;
    public final HistoryStepDetailsCalculatorImpl mStepDetailsCalculator;
    public boolean mSystemReady;
    @VisibleForTesting
    protected SystemServerCpuThreadReader mSystemServerCpuThreadReader;
    public long mTempTotalCpuSystemTimeUs;
    public long mTempTotalCpuUserTimeUs;
    public LongArrayMultiStateCounter.LongArrayContainer mTmpCpuTimeInFreq;
    public final RailStats mTmpRailStats;
    public RpmStats mTmpRpmStats;
    public final KernelWakelockStats mTmpWakelockStats;
    public final SparseArray<Uid> mUidStats;
    public long mUptimeStartUs;
    public long mUptimeUs;
    public int mUsbDataState;
    @VisibleForTesting
    protected UserInfoProvider mUserInfoProvider;
    public int mVideoOnNesting;
    public StopwatchTimer mVideoOnTimer;
    public final ArrayList<StopwatchTimer> mVideoTurnedOnTimers;
    public long[][] mWakeLockAllocationsUs;
    public boolean mWakeLockImportant;
    public int mWakeLockNesting;
    public final HashMap<String, SamplingTimer> mWakeupReasonStats;
    public StopwatchTimer mWifiActiveTimer;
    public ControllerActivityCounterImpl mWifiActivity;
    public final SparseArray<ArrayList<StopwatchTimer>> mWifiBatchedScanTimers;
    public int mWifiFullLockNesting;
    @GuardedBy({"mWifiNetworkLock"})
    public String[] mWifiIfaces;
    public int mWifiMulticastNesting;
    public final ArrayList<StopwatchTimer> mWifiMulticastTimers;
    public StopwatchTimer mWifiMulticastWakelockTimer;
    public final Object mWifiNetworkLock;
    public boolean mWifiOn;
    public StopwatchTimer mWifiOnTimer;
    public WifiPowerCalculator mWifiPowerCalculator;
    public int mWifiRadioPowerState;
    public final ArrayList<StopwatchTimer> mWifiRunningTimers;
    public int mWifiScanNesting;
    public final ArrayList<StopwatchTimer> mWifiScanTimers;
    public int mWifiSignalStrengthBin;
    public final StopwatchTimer[] mWifiSignalStrengthsTimer;
    public int mWifiState;
    public final StopwatchTimer[] mWifiStateTimer;
    public int mWifiSupplState;
    public final StopwatchTimer[] mWifiSupplStateTimer;
    public final ArrayList<StopwatchTimer> mWindowTimers;
    public final Runnable mWriteAsyncRunnable;
    public final ReentrantLock mWriteLock;

    /* loaded from: classes2.dex */
    public interface AlarmInterface {
        void cancel();

        void schedule(long j, long j2);
    }

    /* loaded from: classes2.dex */
    public interface BatteryCallback {
        void batteryNeedsCpuUpdate();

        void batteryPowerChanged(boolean z);

        void batterySendBroadcast(Intent intent);

        void batteryStatsReset();
    }

    /* loaded from: classes2.dex */
    public interface BatteryResetListener {
        void prepareForBatteryStatsReset(int i);
    }

    /* loaded from: classes2.dex */
    public interface EnergyStatsRetriever {
        void fillRailDataStats(RailStats railStats);
    }

    /* loaded from: classes2.dex */
    public interface ExternalStatsSync {
        void cancelCpuSyncDueToWakelockChange();

        Future<?> scheduleCleanupDueToRemovedUser(int i);

        Future<?> scheduleCpuSyncDueToRemovedUid(int i);

        Future<?> scheduleCpuSyncDueToWakelockChange(long j);

        Future<?> scheduleSync(String str, int i);

        Future<?> scheduleSyncDueToBatteryLevelChange(long j);

        void scheduleSyncDueToProcessStateChange(int i, long j);

        Future<?> scheduleSyncDueToScreenStateChange(int i, boolean z, boolean z2, int i2, int[] iArr);
    }

    /* loaded from: classes2.dex */
    public interface PlatformIdleStateCallback {
        void fillLowPowerStats(RpmStats rpmStats);

        String getSubsystemLowPowerStats();
    }

    /* loaded from: classes2.dex */
    public interface TimeBaseObs {
        void detach();

        void onTimeStarted(long j, long j2, long j3);

        void onTimeStopped(long j, long j2, long j3);

        boolean reset(boolean z, long j);
    }

    public static boolean isActiveRadioPowerState(int i) {
        return i == 2 || i == 3;
    }

    public static boolean isOnBattery(int i, int i2) {
        return i == 0 && i2 != 1;
    }

    public int getParcelVersion() {
        return FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__ROLE_HOLDER_UPDATER_UPDATE_FAILED;
    }

    static {
        if (ActivityManager.isLowRamDeviceStatic()) {
            MAX_WAKELOCKS_PER_UID = 40;
        } else {
            MAX_WAKELOCKS_PER_UID = 200;
        }
        BatteryStats.LongCounter longCounter = new BatteryStats.LongCounter() { // from class: com.android.server.power.stats.BatteryStatsImpl.1
            public long getCountForProcessState(int i) {
                return 0L;
            }

            public long getCountLocked(int i) {
                return 0L;
            }

            public void logState(Printer printer, String str) {
                printer.println(str + "mCount=0");
            }
        };
        ZERO_LONG_COUNTER = longCounter;
        ZERO_LONG_COUNTER_ARRAY = new BatteryStats.LongCounter[]{longCounter};
        SUPPORTED_PER_PROCESS_STATE_STANDARD_ENERGY_BUCKETS = new int[]{3, 7, 4, 5};
    }

    public LongSparseArray<SamplingTimer> getKernelMemoryStats() {
        return this.mKernelMemoryStats;
    }

    public BatteryStatsHistory copyHistory() {
        return this.mHistory.copy();
    }

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public final class UidToRemove {
        public final int mEndUid;
        public final int mStartUid;
        public final long mUidRemovalTimestamp;

        public UidToRemove(BatteryStatsImpl batteryStatsImpl, int i, long j) {
            this(i, i, j);
        }

        public UidToRemove(int i, int i2, long j) {
            this.mStartUid = i;
            this.mEndUid = i2;
            this.mUidRemovalTimestamp = j;
        }

        public long getUidRemovalTimestamp() {
            return this.mUidRemovalTimestamp;
        }

        @GuardedBy({"BatteryStatsImpl.this"})
        public void removeLocked() {
            BatteryStatsImpl.this.removeCpuStatsForUidRangeLocked(this.mStartUid, this.mEndUid);
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class UserInfoProvider {
        public int[] userIds;

        public abstract int[] getUserIds();

        @VisibleForTesting
        public final void refreshUserIds() {
            this.userIds = getUserIds();
        }

        @VisibleForTesting
        public boolean exists(int i) {
            int[] iArr = this.userIds;
            if (iArr != null) {
                return ArrayUtils.contains(iArr, i);
            }
            return true;
        }
    }

    /* loaded from: classes2.dex */
    public static class BatteryStatsConfig {
        public final int mFlags;

        /* JADX WARN: Multi-variable type inference failed */
        public BatteryStatsConfig(Builder builder) {
            boolean z = builder.mResetOnUnplugHighBatteryLevel;
            this.mFlags = builder.mResetOnUnplugAfterSignificantCharge ? (z ? 1 : 0) | true : z;
        }

        public boolean shouldResetOnUnplugHighBatteryLevel() {
            return (this.mFlags & 1) == 1;
        }

        public boolean shouldResetOnUnplugAfterSignificantCharge() {
            return (this.mFlags & 2) == 2;
        }

        /* loaded from: classes2.dex */
        public static class Builder {
            public boolean mResetOnUnplugHighBatteryLevel = true;
            public boolean mResetOnUnplugAfterSignificantCharge = true;

            public BatteryStatsConfig build() {
                return new BatteryStatsConfig(this);
            }

            public Builder setResetOnUnplugHighBatteryLevel(boolean z) {
                this.mResetOnUnplugHighBatteryLevel = z;
                return this;
            }

            public Builder setResetOnUnplugAfterSignificantCharge(boolean z) {
                this.mResetOnUnplugAfterSignificantCharge = z;
                return this;
            }
        }
    }

    /* loaded from: classes2.dex */
    public final class MyHandler extends Handler {
        public MyHandler(Looper looper) {
            super(looper, null, true);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            String str;
            BatteryCallback batteryCallback = BatteryStatsImpl.this.mCallback;
            int i = message.what;
            if (i == 1) {
                if (batteryCallback != null) {
                    batteryCallback.batteryNeedsCpuUpdate();
                }
            } else if (i == 2) {
                if (batteryCallback != null) {
                    batteryCallback.batteryPowerChanged(message.arg1 != 0);
                }
            } else if (i != 3) {
                if (i == 4 && batteryCallback != null) {
                    batteryCallback.batteryStatsReset();
                }
            } else if (batteryCallback != null) {
                synchronized (BatteryStatsImpl.this) {
                    str = BatteryStatsImpl.this.mCharging ? "android.os.action.CHARGING" : "android.os.action.DISCHARGING";
                }
                Intent intent = new Intent(str);
                intent.addFlags(67108864);
                batteryCallback.batterySendBroadcast(intent);
            }
        }
    }

    public void postBatteryNeedsCpuUpdateMsg() {
        this.mHandler.sendEmptyMessage(1);
    }

    @GuardedBy({"this"})
    @VisibleForTesting
    public void updateProcStateCpuTimesLocked(int i, long j, long j2) {
        if (initKernelSingleUidTimeReaderLocked()) {
            Uid uidStatsLocked = getUidStatsLocked(i);
            this.mNumSingleUidCpuTimeReads++;
            LongArrayMultiStateCounter counter = uidStatsLocked.getProcStateTimeCounter(j).getCounter();
            LongArrayMultiStateCounter counter2 = uidStatsLocked.getProcStateScreenOffTimeCounter(j).getCounter();
            if (isUsageHistoryEnabled()) {
                LongArrayMultiStateCounter.LongArrayContainer cpuTimeInFreqContainer = getCpuTimeInFreqContainer();
                this.mKernelSingleUidTimeReader.addDelta(i, counter, j, cpuTimeInFreqContainer);
                recordCpuUsage(i, cpuTimeInFreqContainer, j, j2);
            } else {
                this.mKernelSingleUidTimeReader.addDelta(i, counter, j);
            }
            this.mKernelSingleUidTimeReader.addDelta(i, counter2, j);
            if (uidStatsLocked.mChildUids != null) {
                LongArrayMultiStateCounter.LongArrayContainer cpuTimeInFreqContainer2 = getCpuTimeInFreqContainer();
                for (int size = uidStatsLocked.mChildUids.size() - 1; size >= 0; size--) {
                    LongArrayMultiStateCounter longArrayMultiStateCounter = uidStatsLocked.mChildUids.valueAt(size).cpuTimeInFreqCounter;
                    if (longArrayMultiStateCounter != null) {
                        this.mKernelSingleUidTimeReader.addDelta(uidStatsLocked.mChildUids.keyAt(size), longArrayMultiStateCounter, j, cpuTimeInFreqContainer2);
                        counter.addCounts(cpuTimeInFreqContainer2);
                        if (isUsageHistoryEnabled()) {
                            recordCpuUsage(i, cpuTimeInFreqContainer2, j, j2);
                        }
                        counter2.addCounts(cpuTimeInFreqContainer2);
                    }
                }
            }
        }
    }

    public final void recordCpuUsage(int i, LongArrayMultiStateCounter.LongArrayContainer longArrayContainer, long j, long j2) {
        if (longArrayContainer.combineValues(this.mCpuUsageDetails.cpuUsageMs, this.mCpuPowerBracketMap)) {
            BatteryStats.CpuUsageDetails cpuUsageDetails = this.mCpuUsageDetails;
            cpuUsageDetails.uid = i;
            this.mHistory.recordCpuUsage(j, j2, cpuUsageDetails);
        }
    }

    @GuardedBy({"this"})
    public void clearPendingRemovedUidsLocked() {
        long elapsedRealtime = this.mClock.elapsedRealtime() - this.mConstants.UID_REMOVE_DELAY_MS;
        while (!this.mPendingRemovedUids.isEmpty() && this.mPendingRemovedUids.peek().getUidRemovalTimestamp() < elapsedRealtime) {
            this.mPendingRemovedUids.poll().removeLocked();
        }
    }

    public void updateCpuTimesForAllUids() {
        SparseArray sparseArray;
        LongArrayMultiStateCounter longArrayMultiStateCounter;
        synchronized (this) {
            if (trackPerProcStateCpuTimes()) {
                if (initKernelSingleUidTimeReaderLocked()) {
                    SparseArray allUidCpuFreqTimeMs = this.mCpuUidFreqTimeReader.getAllUidCpuFreqTimeMs();
                    int size = allUidCpuFreqTimeMs.size() - 1;
                    while (size >= 0) {
                        int keyAt = allUidCpuFreqTimeMs.keyAt(size);
                        int mapUid = mapUid(keyAt);
                        Uid availableUidStatsLocked = getAvailableUidStatsLocked(mapUid);
                        if (availableUidStatsLocked != null && availableUidStatsLocked.mProcessState != 7) {
                            long elapsedRealtime = this.mClock.elapsedRealtime();
                            long uptimeMillis = this.mClock.uptimeMillis();
                            LongArrayMultiStateCounter counter = availableUidStatsLocked.getProcStateTimeCounter(elapsedRealtime).getCounter();
                            LongArrayMultiStateCounter counter2 = availableUidStatsLocked.getProcStateScreenOffTimeCounter(elapsedRealtime).getCounter();
                            if (keyAt != mapUid && !Process.isSdkSandboxUid(keyAt)) {
                                Uid.ChildUid childUid = availableUidStatsLocked.getChildUid(keyAt);
                                if (childUid != null && (longArrayMultiStateCounter = childUid.cpuTimeInFreqCounter) != null) {
                                    LongArrayMultiStateCounter.LongArrayContainer cpuTimeInFreqContainer = getCpuTimeInFreqContainer();
                                    sparseArray = allUidCpuFreqTimeMs;
                                    this.mKernelSingleUidTimeReader.addDelta(keyAt, longArrayMultiStateCounter, elapsedRealtime, cpuTimeInFreqContainer);
                                    counter.addCounts(cpuTimeInFreqContainer);
                                    if (isUsageHistoryEnabled()) {
                                        recordCpuUsage(keyAt, cpuTimeInFreqContainer, elapsedRealtime, uptimeMillis);
                                    }
                                    counter2.addCounts(cpuTimeInFreqContainer);
                                    size--;
                                    allUidCpuFreqTimeMs = sparseArray;
                                }
                            }
                            sparseArray = allUidCpuFreqTimeMs;
                            if (isUsageHistoryEnabled()) {
                                LongArrayMultiStateCounter.LongArrayContainer cpuTimeInFreqContainer2 = getCpuTimeInFreqContainer();
                                this.mKernelSingleUidTimeReader.addDelta(mapUid, counter, elapsedRealtime, cpuTimeInFreqContainer2);
                                recordCpuUsage(mapUid, cpuTimeInFreqContainer2, elapsedRealtime, uptimeMillis);
                            } else {
                                this.mKernelSingleUidTimeReader.addDelta(mapUid, counter, elapsedRealtime);
                            }
                            this.mKernelSingleUidTimeReader.addDelta(mapUid, counter2, elapsedRealtime);
                            size--;
                            allUidCpuFreqTimeMs = sparseArray;
                        }
                        sparseArray = allUidCpuFreqTimeMs;
                        size--;
                        allUidCpuFreqTimeMs = sparseArray;
                    }
                }
            }
        }
    }

    @GuardedBy({"this"})
    public final boolean initKernelSingleUidTimeReaderLocked() {
        boolean z = false;
        if (this.mKernelSingleUidTimeReader == null) {
            PowerProfile powerProfile = this.mPowerProfile;
            if (powerProfile == null) {
                return false;
            }
            if (this.mCpuFreqs == null) {
                this.mCpuFreqs = this.mCpuUidFreqTimeReader.readFreqs(powerProfile);
            }
            if (this.mCpuFreqs != null) {
                this.mKernelSingleUidTimeReader = new KernelSingleUidTimeReader(this.mCpuFreqs.length);
            } else {
                this.mPerProcStateCpuTimesAvailable = this.mCpuUidFreqTimeReader.allUidTimesAvailable();
                return false;
            }
        }
        if (this.mCpuUidFreqTimeReader.allUidTimesAvailable() && this.mKernelSingleUidTimeReader.singleUidCpuTimesAvailable()) {
            z = true;
        }
        this.mPerProcStateCpuTimesAvailable = z;
        return true;
    }

    /* loaded from: classes2.dex */
    public static class DisplayBatteryStats {
        public StopwatchTimer screenDozeTimer;
        public StopwatchTimer screenOnTimer;
        public int screenState = 0;
        public int screenBrightnessBin = -1;
        public StopwatchTimer[] screenBrightnessTimers = new StopwatchTimer[5];
        public int screenStateAtLastEnergyMeasurement = 0;

        public DisplayBatteryStats(Clock clock, TimeBase timeBase) {
            this.screenOnTimer = new StopwatchTimer(clock, null, -1, null, timeBase);
            this.screenDozeTimer = new StopwatchTimer(clock, null, -1, null, timeBase);
            for (int i = 0; i < 5; i++) {
                this.screenBrightnessTimers[i] = new StopwatchTimer(clock, null, (-100) - i, null, timeBase);
            }
        }

        public void reset(long j) {
            this.screenOnTimer.reset(false, j);
            this.screenDozeTimer.reset(false, j);
            for (int i = 0; i < 5; i++) {
                this.screenBrightnessTimers[i].reset(false, j);
            }
        }

        public void writeSummaryToParcel(Parcel parcel, long j) {
            this.screenOnTimer.writeSummaryFromParcelLocked(parcel, j);
            this.screenDozeTimer.writeSummaryFromParcelLocked(parcel, j);
            for (int i = 0; i < 5; i++) {
                this.screenBrightnessTimers[i].writeSummaryFromParcelLocked(parcel, j);
            }
        }

        public void readSummaryFromParcel(Parcel parcel) {
            this.screenOnTimer.readSummaryFromParcelLocked(parcel);
            this.screenDozeTimer.readSummaryFromParcelLocked(parcel);
            for (int i = 0; i < 5; i++) {
                this.screenBrightnessTimers[i].readSummaryFromParcelLocked(parcel);
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class RadioAccessTechnologyBatteryStats {
        public final StopwatchTimer[][] perStateTimers;
        public boolean mActive = false;
        public int mFrequencyRange = 0;
        public int mSignalStrength = 0;
        public LongSamplingCounter[][] mPerStateTxDurationMs = null;
        public LongSamplingCounter[] mPerFrequencyRxDurationMs = null;

        public RadioAccessTechnologyBatteryStats(int i, Clock clock, TimeBase timeBase) {
            this.perStateTimers = (StopwatchTimer[][]) Array.newInstance(StopwatchTimer.class, i, 5);
            for (int i2 = 0; i2 < i; i2++) {
                for (int i3 = 0; i3 < 5; i3++) {
                    this.perStateTimers[i2][i3] = new StopwatchTimer(clock, null, -1, null, timeBase);
                }
            }
        }

        public void noteActive(boolean z, long j) {
            if (this.mActive == z) {
                return;
            }
            this.mActive = z;
            if (z) {
                this.perStateTimers[this.mFrequencyRange][this.mSignalStrength].startRunningLocked(j);
            } else {
                this.perStateTimers[this.mFrequencyRange][this.mSignalStrength].stopRunningLocked(j);
            }
        }

        public void noteFrequencyRange(int i, long j) {
            int i2 = this.mFrequencyRange;
            if (i2 == i) {
                return;
            }
            if (!this.mActive) {
                this.mFrequencyRange = i;
                return;
            }
            this.perStateTimers[i2][this.mSignalStrength].stopRunningLocked(j);
            this.perStateTimers[i][this.mSignalStrength].startRunningLocked(j);
            this.mFrequencyRange = i;
        }

        public void noteSignalStrength(int i, long j) {
            int i2 = this.mSignalStrength;
            if (i2 == i) {
                return;
            }
            if (!this.mActive) {
                this.mSignalStrength = i;
                return;
            }
            this.perStateTimers[this.mFrequencyRange][i2].stopRunningLocked(j);
            this.perStateTimers[this.mFrequencyRange][i].startRunningLocked(j);
            this.mSignalStrength = i;
        }

        public long getTimeSinceMark(int i, int i2, long j) {
            return this.perStateTimers[i][i2].getTimeSinceMarkLocked(j * 1000) / 1000;
        }

        public void setMark(long j) {
            int length = this.perStateTimers.length;
            for (int i = 0; i < length; i++) {
                for (int i2 = 0; i2 < 5; i2++) {
                    this.perStateTimers[i][i2].setMark(j);
                }
            }
        }

        public int getFrequencyRangeCount() {
            return this.perStateTimers.length;
        }

        public void incrementTxDuration(int i, int i2, long j) {
            getTxDurationCounter(i, i2, true).addCountLocked(j);
        }

        public void incrementRxDuration(int i, long j) {
            getRxDurationCounter(i, true).addCountLocked(j);
        }

        public void reset(long j) {
            int length = this.perStateTimers.length;
            for (int i = 0; i < length; i++) {
                for (int i2 = 0; i2 < 5; i2++) {
                    this.perStateTimers[i][i2].reset(false, j);
                    LongSamplingCounter[][] longSamplingCounterArr = this.mPerStateTxDurationMs;
                    if (longSamplingCounterArr != null) {
                        longSamplingCounterArr[i][i2].reset(false, j);
                    }
                }
                LongSamplingCounter[] longSamplingCounterArr2 = this.mPerFrequencyRxDurationMs;
                if (longSamplingCounterArr2 != null) {
                    longSamplingCounterArr2[i].reset(false, j);
                }
            }
        }

        public void writeSummaryToParcel(Parcel parcel, long j) {
            int length = this.perStateTimers.length;
            parcel.writeInt(length);
            parcel.writeInt(5);
            for (int i = 0; i < length; i++) {
                for (int i2 = 0; i2 < 5; i2++) {
                    this.perStateTimers[i][i2].writeSummaryFromParcelLocked(parcel, j);
                }
            }
            if (this.mPerStateTxDurationMs == null) {
                parcel.writeInt(0);
            } else {
                parcel.writeInt(1);
                for (int i3 = 0; i3 < length; i3++) {
                    for (int i4 = 0; i4 < 5; i4++) {
                        this.mPerStateTxDurationMs[i3][i4].writeSummaryFromParcelLocked(parcel);
                    }
                }
            }
            if (this.mPerFrequencyRxDurationMs == null) {
                parcel.writeInt(0);
                return;
            }
            parcel.writeInt(1);
            for (int i5 = 0; i5 < length; i5++) {
                this.mPerFrequencyRxDurationMs[i5].writeSummaryFromParcelLocked(parcel);
            }
        }

        public void readSummaryFromParcel(Parcel parcel) {
            int readInt = parcel.readInt();
            int readInt2 = parcel.readInt();
            int length = this.perStateTimers.length;
            for (int i = 0; i < readInt; i++) {
                for (int i2 = 0; i2 < readInt2; i2++) {
                    if (i >= length || i2 >= 5) {
                        new StopwatchTimer(null, null, -1, null, new TimeBase()).readSummaryFromParcelLocked(parcel);
                    } else {
                        this.perStateTimers[i][i2].readSummaryFromParcelLocked(parcel);
                    }
                }
            }
            if (parcel.readInt() == 1) {
                for (int i3 = 0; i3 < readInt; i3++) {
                    for (int i4 = 0; i4 < readInt2; i4++) {
                        if (i3 >= length || i4 >= 5) {
                            new StopwatchTimer(null, null, -1, null, new TimeBase()).readSummaryFromParcelLocked(parcel);
                        }
                        getTxDurationCounter(i3, i4, true).readSummaryFromParcelLocked(parcel);
                    }
                }
            }
            if (parcel.readInt() == 1) {
                for (int i5 = 0; i5 < readInt; i5++) {
                    if (i5 >= length) {
                        new StopwatchTimer(null, null, -1, null, new TimeBase()).readSummaryFromParcelLocked(parcel);
                    } else {
                        getRxDurationCounter(i5, true).readSummaryFromParcelLocked(parcel);
                    }
                }
            }
        }

        public final LongSamplingCounter getTxDurationCounter(int i, int i2, boolean z) {
            if (this.mPerStateTxDurationMs == null) {
                if (!z) {
                    return null;
                }
                int frequencyRangeCount = getFrequencyRangeCount();
                StopwatchTimer[] stopwatchTimerArr = this.perStateTimers[0];
                int length = stopwatchTimerArr.length;
                TimeBase timeBase = stopwatchTimerArr[0].mTimeBase;
                this.mPerStateTxDurationMs = (LongSamplingCounter[][]) Array.newInstance(LongSamplingCounter.class, frequencyRangeCount, length);
                for (int i3 = 0; i3 < frequencyRangeCount; i3++) {
                    for (int i4 = 0; i4 < length; i4++) {
                        this.mPerStateTxDurationMs[i3][i4] = new LongSamplingCounter(timeBase);
                    }
                }
            }
            if (i < 0 || i >= getFrequencyRangeCount()) {
                Slog.w("BatteryStatsImpl", "Unexpected frequency range (" + i + ") requested in getTxDurationCounter");
                return null;
            } else if (i2 < 0 || i2 >= this.perStateTimers[0].length) {
                Slog.w("BatteryStatsImpl", "Unexpected signal strength (" + i2 + ") requested in getTxDurationCounter");
                return null;
            } else {
                return this.mPerStateTxDurationMs[i][i2];
            }
        }

        public final LongSamplingCounter getRxDurationCounter(int i, boolean z) {
            if (this.mPerFrequencyRxDurationMs == null) {
                if (!z) {
                    return null;
                }
                int frequencyRangeCount = getFrequencyRangeCount();
                TimeBase timeBase = this.perStateTimers[0][0].mTimeBase;
                this.mPerFrequencyRxDurationMs = new LongSamplingCounter[frequencyRangeCount];
                for (int i2 = 0; i2 < frequencyRangeCount; i2++) {
                    this.mPerFrequencyRxDurationMs[i2] = new LongSamplingCounter(timeBase);
                }
            }
            if (i < 0 || i >= getFrequencyRangeCount()) {
                Slog.w("BatteryStatsImpl", "Unexpected frequency range (" + i + ") requested in getRxDurationCounter");
                return null;
            }
            return this.mPerFrequencyRxDurationMs[i];
        }
    }

    @GuardedBy({"this"})
    public final RadioAccessTechnologyBatteryStats getRatBatteryStatsLocked(int i) {
        RadioAccessTechnologyBatteryStats radioAccessTechnologyBatteryStats = this.mPerRatBatteryStats[i];
        if (radioAccessTechnologyBatteryStats == null) {
            RadioAccessTechnologyBatteryStats radioAccessTechnologyBatteryStats2 = new RadioAccessTechnologyBatteryStats(i == 2 ? 5 : 1, this.mClock, this.mOnBatteryTimeBase);
            this.mPerRatBatteryStats[i] = radioAccessTechnologyBatteryStats2;
            return radioAccessTechnologyBatteryStats2;
        }
        return radioAccessTechnologyBatteryStats;
    }

    public Map<String, ? extends Timer> getRpmStats() {
        return this.mRpmStats;
    }

    public Map<String, ? extends Timer> getScreenOffRpmStats() {
        return this.mScreenOffRpmStats;
    }

    public Map<String, ? extends Timer> getKernelWakelockStats() {
        return this.mKernelWakelockStats;
    }

    public WakeLockStats getWakeLockStats() {
        long elapsedRealtime = this.mClock.elapsedRealtime();
        long j = elapsedRealtime * 1000;
        ArrayList arrayList = new ArrayList();
        for (int size = this.mUidStats.size() - 1; size >= 0; size--) {
            Uid valueAt = this.mUidStats.valueAt(size);
            ArrayMap<String, Uid.Wakelock> map = valueAt.mWakelockStats.getMap();
            for (int size2 = map.size() - 1; size2 >= 0; size2--) {
                String keyAt = map.keyAt(size2);
                DualTimer dualTimer = map.valueAt(size2).mTimerPartial;
                if (dualTimer != null) {
                    long totalTimeLocked = dualTimer.getTotalTimeLocked(j, 0) / 1000;
                    if (totalTimeLocked != 0) {
                        arrayList.add(new WakeLockStats.WakeLock(valueAt.getUid(), keyAt, dualTimer.getCountLocked(0), totalTimeLocked, dualTimer.isRunningLocked() ? dualTimer.getCurrentDurationMsLocked(elapsedRealtime) : 0L));
                    }
                }
            }
        }
        return new WakeLockStats(arrayList);
    }

    @GuardedBy({"this"})
    public BluetoothBatteryStats getBluetoothBatteryStats() {
        long elapsedRealtime = this.mClock.elapsedRealtime() * 1000;
        ArrayList arrayList = new ArrayList();
        for (int size = this.mUidStats.size() - 1; size >= 0; size--) {
            Uid valueAt = this.mUidStats.valueAt(size);
            Timer bluetoothScanTimer = valueAt.getBluetoothScanTimer();
            long totalTimeLocked = bluetoothScanTimer != null ? bluetoothScanTimer.getTotalTimeLocked(elapsedRealtime, 0) / 1000 : 0L;
            Timer bluetoothUnoptimizedScanTimer = valueAt.getBluetoothUnoptimizedScanTimer();
            long totalTimeLocked2 = bluetoothUnoptimizedScanTimer != null ? bluetoothUnoptimizedScanTimer.getTotalTimeLocked(elapsedRealtime, 0) / 1000 : 0L;
            Counter bluetoothScanResultCounter = valueAt.getBluetoothScanResultCounter();
            int countLocked = bluetoothScanResultCounter != null ? bluetoothScanResultCounter.getCountLocked(0) : 0;
            ControllerActivityCounterImpl bluetoothControllerActivity = valueAt.getBluetoothControllerActivity();
            long countLocked2 = bluetoothControllerActivity != null ? bluetoothControllerActivity.getRxTimeCounter().getCountLocked(0) : 0L;
            long countLocked3 = bluetoothControllerActivity != null ? bluetoothControllerActivity.getTxTimeCounters()[0].getCountLocked(0) : 0L;
            if (totalTimeLocked != 0 || totalTimeLocked2 != 0 || countLocked != 0 || countLocked2 != 0 || countLocked3 != 0) {
                arrayList.add(new BluetoothBatteryStats.UidStats(valueAt.getUid(), totalTimeLocked, totalTimeLocked2, countLocked, countLocked2, countLocked3));
            }
        }
        return new BluetoothBatteryStats(arrayList);
    }

    public Map<String, ? extends Timer> getWakeupReasonStats() {
        return this.mWakeupReasonStats;
    }

    public long getUahDischarge(int i) {
        return this.mDischargeCounter.getCountLocked(i);
    }

    public long getUahDischargeScreenOff(int i) {
        return this.mDischargeScreenOffCounter.getCountLocked(i);
    }

    public long getUahDischargeScreenDoze(int i) {
        return this.mDischargeScreenDozeCounter.getCountLocked(i);
    }

    public long getUahDischargeLightDoze(int i) {
        return this.mDischargeLightDozeCounter.getCountLocked(i);
    }

    public long getUahDischargeDeepDoze(int i) {
        return this.mDischargeDeepDozeCounter.getCountLocked(i);
    }

    public int getEstimatedBatteryCapacity() {
        return this.mEstimatedBatteryCapacityMah;
    }

    public int getLearnedBatteryCapacity() {
        return this.mLastLearnedBatteryCapacityUah;
    }

    public int getMinLearnedBatteryCapacity() {
        return this.mMinLearnedBatteryCapacityUah;
    }

    public int getMaxLearnedBatteryCapacity() {
        return this.mMaxLearnedBatteryCapacityUah;
    }

    public BatteryStatsImpl() {
        this(Clock.SYSTEM_CLOCK);
    }

    public BatteryStatsImpl(Clock clock) {
        this(clock, null);
    }

    public BatteryStatsImpl(Clock clock, File file) {
        this.mKernelWakelockReader = new KernelWakelockReader();
        this.mTmpWakelockStats = new KernelWakelockStats();
        this.mSystemServerCpuThreadReader = SystemServerCpuThreadReader.create();
        this.mKernelMemoryBandwidthStats = new KernelMemoryBandwidthStats();
        this.mKernelMemoryStats = new LongSparseArray<>();
        this.mCpuUsageDetails = new BatteryStats.CpuUsageDetails();
        this.mPerProcStateCpuTimesAvailable = true;
        this.mCpuTimeReadsTrackingStartTimeMs = SystemClock.uptimeMillis();
        this.mTmpRpmStats = null;
        this.mLastRpmStatsUpdateTimeMs = -1000L;
        this.mTmpRailStats = new RailStats();
        this.mPendingRemovedUids = new LinkedList();
        this.mDeferSetCharging = new Runnable() { // from class: com.android.server.power.stats.BatteryStatsImpl.2
            @Override // java.lang.Runnable
            public void run() {
                synchronized (BatteryStatsImpl.this) {
                    BatteryStatsImpl batteryStatsImpl = BatteryStatsImpl.this;
                    if (batteryStatsImpl.mOnBattery) {
                        return;
                    }
                    if (batteryStatsImpl.setChargingLocked(true)) {
                        long uptimeMillis = BatteryStatsImpl.this.mClock.uptimeMillis();
                        BatteryStatsImpl.this.mHistory.writeHistoryItem(BatteryStatsImpl.this.mClock.elapsedRealtime(), uptimeMillis);
                    }
                }
            }
        };
        this.mExternalSync = null;
        this.mUserInfoProvider = null;
        this.mIsolatedUids = new SparseIntArray();
        this.mIsolatedUidRefCounts = new SparseIntArray();
        this.mUidStats = new SparseArray<>();
        this.mPartialTimers = new ArrayList<>();
        this.mFullTimers = new ArrayList<>();
        this.mWindowTimers = new ArrayList<>();
        this.mDrawTimers = new ArrayList<>();
        this.mSensorTimers = new SparseArray<>();
        this.mWifiRunningTimers = new ArrayList<>();
        this.mFullWifiLockTimers = new ArrayList<>();
        this.mWifiMulticastTimers = new ArrayList<>();
        this.mWifiScanTimers = new ArrayList<>();
        this.mWifiBatchedScanTimers = new SparseArray<>();
        this.mAudioTurnedOnTimers = new ArrayList<>();
        this.mVideoTurnedOnTimers = new ArrayList<>();
        this.mFlashlightTurnedOnTimers = new ArrayList<>();
        this.mCameraTurnedOnTimers = new ArrayList<>();
        this.mBluetoothScanOnTimers = new ArrayList<>();
        this.mLastPartialTimers = new ArrayList<>();
        this.mOnBatteryTimeBase = new TimeBase(true);
        this.mOnBatteryScreenOffTimeBase = new TimeBase(true);
        this.mActiveEvents = new BatteryStats.HistoryEventTracker();
        HistoryStepDetailsCalculatorImpl historyStepDetailsCalculatorImpl = new HistoryStepDetailsCalculatorImpl();
        this.mStepDetailsCalculator = historyStepDetailsCalculatorImpl;
        this.mHaveBatteryLevel = false;
        this.mBatteryPluggedInRealTimeMs = 0L;
        this.mBatteryVoltageMv = -1;
        this.mIgnoreNextExternalStats = false;
        this.mScreenState = 0;
        this.mScreenBrightnessBin = -1;
        this.mScreenBrightnessTimer = new StopwatchTimer[5];
        this.mDisplayMismatchWtfCount = 0;
        this.mUsbDataState = 0;
        this.mGpsSignalQualityBin = -1;
        this.mGpsSignalQualityTimer = new StopwatchTimer[2];
        this.mPhoneSignalStrengthBin = -1;
        this.mPhoneSignalStrengthBinRaw = -1;
        this.mPhoneSignalStrengthsTimer = new StopwatchTimer[CellSignalStrength.getNumSignalStrengthLevels()];
        this.mPhoneDataConnectionType = -1;
        this.mPhoneDataConnectionsTimer = new StopwatchTimer[BatteryStats.NUM_DATA_CONNECTION_TYPES];
        this.mActiveRat = 0;
        this.mPerRatBatteryStats = new RadioAccessTechnologyBatteryStats[3];
        this.mNetworkByteActivityCounters = new LongSamplingCounter[10];
        this.mNetworkPacketActivityCounters = new LongSamplingCounter[10];
        this.mHasWifiReporting = false;
        this.mHasBluetoothReporting = false;
        this.mHasModemReporting = false;
        this.mWifiState = -1;
        this.mWifiStateTimer = new StopwatchTimer[8];
        this.mWifiSupplState = -1;
        this.mWifiSupplStateTimer = new StopwatchTimer[13];
        this.mWifiSignalStrengthBin = -1;
        this.mWifiSignalStrengthsTimer = new StopwatchTimer[5];
        this.mMobileRadioPowerState = 1;
        this.mWifiRadioPowerState = 1;
        this.mBluetoothPowerCalculator = null;
        this.mCpuPowerCalculator = null;
        this.mMobileRadioPowerCalculator = null;
        this.mWifiPowerCalculator = null;
        this.mCharging = true;
        this.mInitStepMode = 0;
        this.mCurStepMode = 0;
        this.mModStepMode = 0;
        this.mDischargeStepTracker = new BatteryStats.LevelStepTracker(200);
        this.mDailyDischargeStepTracker = new BatteryStats.LevelStepTracker((int) FrameworkStatsLog.APP_PROCESS_DIED__IMPORTANCE__IMPORTANCE_BACKGROUND);
        this.mChargeStepTracker = new BatteryStats.LevelStepTracker(200);
        this.mDailyChargeStepTracker = new BatteryStats.LevelStepTracker((int) FrameworkStatsLog.APP_PROCESS_DIED__IMPORTANCE__IMPORTANCE_BACKGROUND);
        this.mDailyStartTimeMs = 0L;
        this.mNextMinDailyDeadlineMs = 0L;
        this.mNextMaxDailyDeadlineMs = 0L;
        this.mDailyItems = new ArrayList<>();
        this.mLastWriteTimeMs = 0L;
        this.mPhoneServiceState = -1;
        this.mPhoneServiceStateRaw = -1;
        this.mPhoneSimStateRaw = -1;
        this.mEstimatedBatteryCapacityMah = -1;
        this.mLastLearnedBatteryCapacityUah = -1;
        this.mMinLearnedBatteryCapacityUah = -1;
        this.mMaxLearnedBatteryCapacityUah = -1;
        this.mBatteryTimeToFullSeconds = -1L;
        this.mBatteryStatsConfig = new BatteryStatsConfig.Builder().build();
        this.mLongPlugInAlarmInterface = null;
        this.mRpmStats = new HashMap<>();
        this.mScreenOffRpmStats = new HashMap<>();
        this.mKernelWakelockStats = new HashMap<>();
        this.mLastWakeupReason = null;
        this.mLastWakeupUptimeMs = 0L;
        this.mWakeupReasonStats = new HashMap<>();
        this.mWifiFullLockNesting = 0;
        this.mWifiScanNesting = 0;
        this.mWifiMulticastNesting = 0;
        this.mWifiNetworkLock = new Object();
        this.mWifiIfaces = EmptyArray.STRING;
        this.mLastWifiNetworkStats = new NetworkStats(0L, -1);
        this.mModemNetworkLock = new Object();
        this.mModemIfaces = EmptyArray.STRING;
        this.mLastModemNetworkStats = new NetworkStats(0L, -1);
        this.mLastModemActivityInfo = null;
        this.mLastBluetoothActivityInfo = new BluetoothActivityInfoCache();
        this.mWriteAsyncRunnable = new Runnable() { // from class: com.android.server.power.stats.BatteryStatsImpl$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                BatteryStatsImpl.this.lambda$new$4();
            }
        };
        this.mWriteLock = new ReentrantLock();
        init(clock);
        this.mHandler = null;
        Constants constants = new Constants(this.mHandler);
        this.mConstants = constants;
        this.mStartClockTimeMs = clock.currentTimeMillis();
        this.mDailyFile = null;
        if (file == null) {
            this.mCheckinFile = null;
            this.mStatsFile = null;
            this.mHistory = new BatteryStatsHistory(constants.MAX_HISTORY_FILES, constants.MAX_HISTORY_BUFFER, historyStepDetailsCalculatorImpl, this.mClock);
        } else {
            this.mCheckinFile = new AtomicFile(new File(file, "batterystats-checkin.bin"));
            this.mStatsFile = new AtomicFile(new File(file, "batterystats.bin"));
            this.mHistory = new BatteryStatsHistory(file, constants.MAX_HISTORY_FILES, constants.MAX_HISTORY_BUFFER, historyStepDetailsCalculatorImpl, this.mClock);
        }
        this.mPlatformIdleStateCallback = null;
        this.mEnergyConsumerRetriever = null;
        this.mUserInfoProvider = null;
    }

    public final void init(Clock clock) {
        this.mClock = clock;
        this.mCpuUidUserSysTimeReader = new KernelCpuUidTimeReader.KernelCpuUidUserSysTimeReader(true, clock);
        this.mCpuUidFreqTimeReader = new KernelCpuUidTimeReader.KernelCpuUidFreqTimeReader(true, clock);
        this.mCpuUidActiveTimeReader = new KernelCpuUidTimeReader.KernelCpuUidActiveTimeReader(true, clock);
        this.mCpuUidClusterTimeReader = new KernelCpuUidTimeReader.KernelCpuUidClusterTimeReader(true, clock);
    }

    /* loaded from: classes2.dex */
    public static class TimeBase {
        public final Collection<TimeBaseObs> mObservers;
        public long mPastRealtimeUs;
        public long mPastUptimeUs;
        public long mRealtimeStartUs;
        public long mRealtimeUs;
        public boolean mRunning;
        public long mUnpluggedRealtimeUs;
        public long mUnpluggedUptimeUs;
        public long mUptimeStartUs;
        public long mUptimeUs;

        public TimeBase(boolean z) {
            this.mObservers = z ? new HashSet<>() : new ArrayList<>();
        }

        public TimeBase() {
            this(false);
        }

        public void add(TimeBaseObs timeBaseObs) {
            this.mObservers.add(timeBaseObs);
        }

        public void remove(TimeBaseObs timeBaseObs) {
            this.mObservers.remove(timeBaseObs);
        }

        public void init(long j, long j2) {
            this.mRealtimeUs = 0L;
            this.mUptimeUs = 0L;
            this.mPastUptimeUs = 0L;
            this.mPastRealtimeUs = 0L;
            this.mUptimeStartUs = j;
            this.mRealtimeStartUs = j2;
            this.mUnpluggedUptimeUs = getUptime(j);
            this.mUnpluggedRealtimeUs = getRealtime(this.mRealtimeStartUs);
        }

        public void reset(long j, long j2) {
            if (!this.mRunning) {
                this.mPastUptimeUs = 0L;
                this.mPastRealtimeUs = 0L;
                return;
            }
            this.mUptimeStartUs = j;
            this.mRealtimeStartUs = j2;
            this.mUnpluggedUptimeUs = getUptime(j);
            this.mUnpluggedRealtimeUs = getRealtime(j2);
        }

        public long computeUptime(long j, int i) {
            return this.mUptimeUs + getUptime(j);
        }

        public long computeRealtime(long j, int i) {
            return this.mRealtimeUs + getRealtime(j);
        }

        public long getUptime(long j) {
            long j2 = this.mPastUptimeUs;
            return this.mRunning ? j2 + (j - this.mUptimeStartUs) : j2;
        }

        public long getRealtime(long j) {
            long j2 = this.mPastRealtimeUs;
            return this.mRunning ? j2 + (j - this.mRealtimeStartUs) : j2;
        }

        public boolean isRunning() {
            return this.mRunning;
        }

        public boolean setRunning(boolean z, long j, long j2) {
            if (this.mRunning != z) {
                this.mRunning = z;
                if (z) {
                    this.mUptimeStartUs = j;
                    this.mRealtimeStartUs = j2;
                    long uptime = getUptime(j);
                    this.mUnpluggedUptimeUs = uptime;
                    long realtime = getRealtime(j2);
                    this.mUnpluggedRealtimeUs = realtime;
                    for (TimeBaseObs timeBaseObs : this.mObservers) {
                        timeBaseObs.onTimeStarted(j2, uptime, realtime);
                    }
                    return true;
                }
                this.mPastUptimeUs += j - this.mUptimeStartUs;
                this.mPastRealtimeUs += j2 - this.mRealtimeStartUs;
                long uptime2 = getUptime(j);
                long realtime2 = getRealtime(j2);
                for (TimeBaseObs timeBaseObs2 : this.mObservers) {
                    timeBaseObs2.onTimeStopped(j2, uptime2, realtime2);
                }
                return true;
            }
            return false;
        }

        public void readSummaryFromParcel(Parcel parcel) {
            this.mUptimeUs = parcel.readLong();
            this.mRealtimeUs = parcel.readLong();
        }

        public void writeSummaryToParcel(Parcel parcel, long j, long j2) {
            parcel.writeLong(computeUptime(j, 0));
            parcel.writeLong(computeRealtime(j2, 0));
        }
    }

    /* loaded from: classes2.dex */
    public static class Counter extends BatteryStats.Counter implements TimeBaseObs {
        public final AtomicInteger mCount = new AtomicInteger();
        public final TimeBase mTimeBase;

        @Override // com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
        public void onTimeStarted(long j, long j2, long j3) {
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
        public void onTimeStopped(long j, long j2, long j3) {
        }

        public Counter(TimeBase timeBase) {
            this.mTimeBase = timeBase;
            timeBase.add(this);
        }

        public int getCountLocked(int i) {
            return this.mCount.get();
        }

        public void logState(Printer printer, String str) {
            printer.println(str + "mCount=" + this.mCount.get());
        }

        @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
        public void stepAtomic() {
            if (this.mTimeBase.isRunning()) {
                this.mCount.incrementAndGet();
            }
        }

        public void addAtomic(int i) {
            if (this.mTimeBase.isRunning()) {
                this.mCount.addAndGet(i);
            }
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
        public boolean reset(boolean z, long j) {
            this.mCount.set(0);
            if (z) {
                detach();
                return true;
            }
            return true;
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
        public void detach() {
            this.mTimeBase.remove(this);
        }

        @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
        public void writeSummaryFromParcelLocked(Parcel parcel) {
            parcel.writeInt(this.mCount.get());
        }

        @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
        public void readSummaryFromParcelLocked(Parcel parcel) {
            this.mCount.set(parcel.readInt());
        }
    }

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public static class LongSamplingCounterArray extends BatteryStats.LongCounterArray implements TimeBaseObs {
        public long[] mCounts;
        public final TimeBase mTimeBase;

        @Override // com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
        public void onTimeStarted(long j, long j2, long j3) {
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
        public void onTimeStopped(long j, long j2, long j3) {
        }

        public LongSamplingCounterArray(TimeBase timeBase) {
            this.mTimeBase = timeBase;
            timeBase.add(this);
        }

        public long[] getCountsLocked(int i) {
            long[] jArr = this.mCounts;
            if (jArr == null) {
                return null;
            }
            return Arrays.copyOf(jArr, jArr.length);
        }

        public void logState(Printer printer, String str) {
            printer.println(str + "mCounts=" + Arrays.toString(this.mCounts));
        }

        public void addCountLocked(long[] jArr) {
            addCountLocked(jArr, this.mTimeBase.isRunning());
        }

        public void addCountLocked(long[] jArr, boolean z) {
            if (jArr != null && z) {
                if (this.mCounts == null) {
                    this.mCounts = new long[jArr.length];
                }
                for (int i = 0; i < jArr.length; i++) {
                    long[] jArr2 = this.mCounts;
                    jArr2[i] = jArr2[i] + jArr[i];
                }
            }
        }

        public int getSize() {
            long[] jArr = this.mCounts;
            if (jArr == null) {
                return 0;
            }
            return jArr.length;
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
        public boolean reset(boolean z, long j) {
            long[] jArr = this.mCounts;
            if (jArr != null) {
                Arrays.fill(jArr, 0L);
            }
            if (z) {
                detach();
                return true;
            }
            return true;
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
        public void detach() {
            this.mTimeBase.remove(this);
        }

        public final void writeSummaryToParcelLocked(Parcel parcel) {
            parcel.writeLongArray(this.mCounts);
        }

        public final void readSummaryFromParcelLocked(Parcel parcel) {
            this.mCounts = parcel.createLongArray();
        }

        public static void writeSummaryToParcelLocked(Parcel parcel, LongSamplingCounterArray longSamplingCounterArray) {
            if (longSamplingCounterArray != null) {
                parcel.writeInt(1);
                longSamplingCounterArray.writeSummaryToParcelLocked(parcel);
                return;
            }
            parcel.writeInt(0);
        }

        public static LongSamplingCounterArray readSummaryFromParcelLocked(Parcel parcel, TimeBase timeBase) {
            if (parcel.readInt() != 0) {
                LongSamplingCounterArray longSamplingCounterArray = new LongSamplingCounterArray(timeBase);
                longSamplingCounterArray.readSummaryFromParcelLocked(parcel);
                return longSamplingCounterArray;
            }
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static class TimeMultiStateCounter extends BatteryStats.LongCounter implements TimeBaseObs {
        public final LongMultiStateCounter mCounter;
        public final TimeBase mTimeBase;

        public TimeMultiStateCounter(TimeBase timeBase, int i, long j) {
            this(timeBase, new LongMultiStateCounter(i), j);
        }

        public TimeMultiStateCounter(TimeBase timeBase, LongMultiStateCounter longMultiStateCounter, long j) {
            this.mTimeBase = timeBase;
            this.mCounter = longMultiStateCounter;
            longMultiStateCounter.setEnabled(timeBase.isRunning(), j);
            timeBase.add(this);
        }

        public static TimeMultiStateCounter readFromParcel(Parcel parcel, TimeBase timeBase, int i, long j) {
            LongMultiStateCounter longMultiStateCounter = (LongMultiStateCounter) LongMultiStateCounter.CREATOR.createFromParcel(parcel);
            if (longMultiStateCounter.getStateCount() != i) {
                return null;
            }
            return new TimeMultiStateCounter(timeBase, longMultiStateCounter, j);
        }

        public final void writeToParcel(Parcel parcel) {
            this.mCounter.writeToParcel(parcel, 0);
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
        public void onTimeStarted(long j, long j2, long j3) {
            this.mCounter.setEnabled(true, j / 1000);
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
        public void onTimeStopped(long j, long j2, long j3) {
            this.mCounter.setEnabled(false, j / 1000);
        }

        public int getStateCount() {
            return this.mCounter.getStateCount();
        }

        public final void setState(int i, long j) {
            this.mCounter.setState(i, j);
        }

        public final long update(long j, long j2) {
            return this.mCounter.updateValue(j, j2);
        }

        public final void increment(long j, long j2) {
            this.mCounter.incrementValue(j, j2);
        }

        public long getCountForProcessState(int i) {
            return this.mCounter.getCount(i);
        }

        public long getTotalCountLocked() {
            return this.mCounter.getTotalCount();
        }

        public long getCountLocked(int i) {
            return getTotalCountLocked();
        }

        public void logState(Printer printer, String str) {
            printer.println(str + "mCounter=" + this.mCounter);
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
        public boolean reset(boolean z, long j) {
            this.mCounter.reset();
            if (z) {
                detach();
                return true;
            }
            return true;
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
        public void detach() {
            this.mTimeBase.remove(this);
        }
    }

    /* loaded from: classes2.dex */
    public static class TimeInFreqMultiStateCounter implements TimeBaseObs {
        public final LongArrayMultiStateCounter mCounter;
        public final TimeBase mTimeBase;

        public TimeInFreqMultiStateCounter(TimeBase timeBase, int i, int i2, long j) {
            this(timeBase, new LongArrayMultiStateCounter(i, i2), j);
        }

        public TimeInFreqMultiStateCounter(TimeBase timeBase, LongArrayMultiStateCounter longArrayMultiStateCounter, long j) {
            this.mTimeBase = timeBase;
            this.mCounter = longArrayMultiStateCounter;
            longArrayMultiStateCounter.setEnabled(timeBase.isRunning(), j);
            timeBase.add(this);
        }

        public final void writeToParcel(Parcel parcel) {
            this.mCounter.writeToParcel(parcel, 0);
        }

        public static TimeInFreqMultiStateCounter readFromParcel(Parcel parcel, TimeBase timeBase, int i, int i2, long j) {
            LongArrayMultiStateCounter longArrayMultiStateCounter = (LongArrayMultiStateCounter) LongArrayMultiStateCounter.CREATOR.createFromParcel(parcel);
            if (longArrayMultiStateCounter.getStateCount() == i && longArrayMultiStateCounter.getArrayLength() == i2) {
                return new TimeInFreqMultiStateCounter(timeBase, longArrayMultiStateCounter, j);
            }
            return null;
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
        public void onTimeStarted(long j, long j2, long j3) {
            this.mCounter.setEnabled(true, j / 1000);
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
        public void onTimeStopped(long j, long j2, long j3) {
            this.mCounter.setEnabled(false, j / 1000);
        }

        public LongArrayMultiStateCounter getCounter() {
            return this.mCounter;
        }

        public int getStateCount() {
            return this.mCounter.getStateCount();
        }

        public final void setState(int i, long j) {
            this.mCounter.setState(i, j);
        }

        public boolean getCountsLocked(long[] jArr, int i) {
            if (jArr.length != this.mCounter.getArrayLength()) {
                return false;
            }
            this.mCounter.getCounts(jArr, i);
            for (int length = jArr.length - 1; length >= 0; length--) {
                if (jArr[length] != 0) {
                    return true;
                }
            }
            return false;
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
        public boolean reset(boolean z, long j) {
            this.mCounter.reset();
            if (z) {
                detach();
                return true;
            }
            return true;
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
        public void detach() {
            this.mTimeBase.remove(this);
        }
    }

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public static class LongSamplingCounter extends BatteryStats.LongCounter implements TimeBaseObs {
        public long mCount;
        public final TimeBase mTimeBase;

        @Override // com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
        public void onTimeStarted(long j, long j2, long j3) {
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
        public void onTimeStopped(long j, long j2, long j3) {
        }

        public LongSamplingCounter(TimeBase timeBase) {
            this.mTimeBase = timeBase;
            timeBase.add(this);
        }

        public void writeToParcel(Parcel parcel) {
            parcel.writeLong(this.mCount);
        }

        public long getCountLocked(int i) {
            return this.mCount;
        }

        public long getCountForProcessState(int i) {
            if (i == 0) {
                return getCountLocked(0);
            }
            return 0L;
        }

        public void logState(Printer printer, String str) {
            printer.println(str + "mCount=" + this.mCount);
        }

        public void addCountLocked(long j) {
            addCountLocked(j, this.mTimeBase.isRunning());
        }

        public void addCountLocked(long j, boolean z) {
            if (z) {
                this.mCount += j;
            }
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
        public boolean reset(boolean z, long j) {
            this.mCount = 0L;
            if (z) {
                detach();
                return true;
            }
            return true;
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
        public void detach() {
            this.mTimeBase.remove(this);
        }

        public void writeSummaryFromParcelLocked(Parcel parcel) {
            parcel.writeLong(this.mCount);
        }

        public void readSummaryFromParcelLocked(Parcel parcel) {
            this.mCount = parcel.readLong();
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Timer extends BatteryStats.Timer implements TimeBaseObs {
        public final Clock mClock;
        public int mCount;
        public final TimeBase mTimeBase;
        public long mTimeBeforeMarkUs;
        public long mTotalTimeUs;
        public final int mType;

        public abstract int computeCurrentCountLocked();

        public abstract long computeRunTimeLocked(long j, long j2);

        public void onTimeStarted(long j, long j2, long j3) {
        }

        public Timer(Clock clock, int i, TimeBase timeBase, Parcel parcel) {
            this.mClock = clock;
            this.mType = i;
            this.mTimeBase = timeBase;
            this.mCount = parcel.readInt();
            this.mTotalTimeUs = parcel.readLong();
            this.mTimeBeforeMarkUs = parcel.readLong();
            timeBase.add(this);
        }

        public Timer(Clock clock, int i, TimeBase timeBase) {
            this.mClock = clock;
            this.mType = i;
            this.mTimeBase = timeBase;
            timeBase.add(this);
        }

        public boolean reset(boolean z, long j) {
            this.mTimeBeforeMarkUs = 0L;
            this.mTotalTimeUs = 0L;
            this.mCount = 0;
            if (z) {
                detach();
                return true;
            }
            return true;
        }

        public void detach() {
            this.mTimeBase.remove(this);
        }

        public void onTimeStopped(long j, long j2, long j3) {
            this.mTotalTimeUs = computeRunTimeLocked(j3, j);
            this.mCount = computeCurrentCountLocked();
        }

        public long getTotalTimeLocked(long j, int i) {
            return computeRunTimeLocked(this.mTimeBase.getRealtime(j), j);
        }

        public int getCountLocked(int i) {
            return computeCurrentCountLocked();
        }

        public long getTimeSinceMarkLocked(long j) {
            return computeRunTimeLocked(this.mTimeBase.getRealtime(j), j) - this.mTimeBeforeMarkUs;
        }

        public void logState(Printer printer, String str) {
            printer.println(str + "mCount=" + this.mCount);
            printer.println(str + "mTotalTime=" + this.mTotalTimeUs);
        }

        public void writeSummaryFromParcelLocked(Parcel parcel, long j) {
            parcel.writeLong(computeRunTimeLocked(this.mTimeBase.getRealtime(j), j));
            parcel.writeInt(computeCurrentCountLocked());
        }

        public void readSummaryFromParcelLocked(Parcel parcel) {
            this.mTotalTimeUs = parcel.readLong();
            this.mCount = parcel.readInt();
            this.mTimeBeforeMarkUs = this.mTotalTimeUs;
        }
    }

    /* loaded from: classes2.dex */
    public static class SamplingTimer extends Timer {
        public int mCurrentReportedCount;
        public long mCurrentReportedTotalTimeUs;
        public boolean mTimeBaseRunning;
        public boolean mTrackingReportedValues;
        public int mUnpluggedReportedCount;
        public long mUnpluggedReportedTotalTimeUs;
        public int mUpdateVersion;

        @VisibleForTesting
        public SamplingTimer(Clock clock, TimeBase timeBase, Parcel parcel) {
            super(clock, 0, timeBase, parcel);
            this.mCurrentReportedCount = parcel.readInt();
            this.mUnpluggedReportedCount = parcel.readInt();
            this.mCurrentReportedTotalTimeUs = parcel.readLong();
            this.mUnpluggedReportedTotalTimeUs = parcel.readLong();
            this.mTrackingReportedValues = parcel.readInt() == 1;
            this.mTimeBaseRunning = timeBase.isRunning();
        }

        @VisibleForTesting
        public SamplingTimer(Clock clock, TimeBase timeBase) {
            super(clock, 0, timeBase);
            this.mTrackingReportedValues = false;
            this.mTimeBaseRunning = timeBase.isRunning();
        }

        public void endSample(long j) {
            this.mTotalTimeUs = computeRunTimeLocked(0L, j);
            this.mCount = computeCurrentCountLocked();
            this.mCurrentReportedTotalTimeUs = 0L;
            this.mUnpluggedReportedTotalTimeUs = 0L;
            this.mCurrentReportedCount = 0;
            this.mUnpluggedReportedCount = 0;
            this.mTrackingReportedValues = false;
        }

        public void setUpdateVersion(int i) {
            this.mUpdateVersion = i;
        }

        public int getUpdateVersion() {
            return this.mUpdateVersion;
        }

        public void update(long j, int i, long j2) {
            if (this.mTimeBaseRunning && !this.mTrackingReportedValues) {
                this.mUnpluggedReportedTotalTimeUs = j;
                this.mUnpluggedReportedCount = i;
            }
            this.mTrackingReportedValues = true;
            if (j < this.mCurrentReportedTotalTimeUs || i < this.mCurrentReportedCount) {
                endSample(j2);
            }
            this.mCurrentReportedTotalTimeUs = j;
            this.mCurrentReportedCount = i;
        }

        public void add(long j, int i, long j2) {
            update(this.mCurrentReportedTotalTimeUs + j, this.mCurrentReportedCount + i, j2);
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.Timer, com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
        public void onTimeStarted(long j, long j2, long j3) {
            super.onTimeStarted(j, j2, j3);
            if (this.mTrackingReportedValues) {
                this.mUnpluggedReportedTotalTimeUs = this.mCurrentReportedTotalTimeUs;
                this.mUnpluggedReportedCount = this.mCurrentReportedCount;
            }
            this.mTimeBaseRunning = true;
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.Timer, com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
        public void onTimeStopped(long j, long j2, long j3) {
            super.onTimeStopped(j, j2, j3);
            this.mTimeBaseRunning = false;
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.Timer
        public void logState(Printer printer, String str) {
            super.logState(printer, str);
            printer.println(str + "mCurrentReportedCount=" + this.mCurrentReportedCount + " mUnpluggedReportedCount=" + this.mUnpluggedReportedCount + " mCurrentReportedTotalTime=" + this.mCurrentReportedTotalTimeUs + " mUnpluggedReportedTotalTime=" + this.mUnpluggedReportedTotalTimeUs);
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.Timer
        public long computeRunTimeLocked(long j, long j2) {
            return this.mTotalTimeUs + ((this.mTimeBaseRunning && this.mTrackingReportedValues) ? this.mCurrentReportedTotalTimeUs - this.mUnpluggedReportedTotalTimeUs : 0L);
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.Timer
        public int computeCurrentCountLocked() {
            return this.mCount + ((this.mTimeBaseRunning && this.mTrackingReportedValues) ? this.mCurrentReportedCount - this.mUnpluggedReportedCount : 0);
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.Timer, com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
        public boolean reset(boolean z, long j) {
            super.reset(z, j);
            this.mTrackingReportedValues = false;
            this.mUnpluggedReportedTotalTimeUs = 0L;
            this.mUnpluggedReportedCount = 0;
            return true;
        }
    }

    /* loaded from: classes2.dex */
    public static class BatchTimer extends Timer {
        public boolean mInDischarge;
        public long mLastAddedDurationUs;
        public long mLastAddedTimeUs;
        public final Uid mUid;

        public BatchTimer(Clock clock, Uid uid, int i, TimeBase timeBase) {
            super(clock, i, timeBase);
            this.mUid = uid;
            this.mInDischarge = timeBase.isRunning();
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.Timer, com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
        public void onTimeStopped(long j, long j2, long j3) {
            recomputeLastDuration(j, false);
            this.mInDischarge = false;
            super.onTimeStopped(j, j2, j3);
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.Timer, com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
        public void onTimeStarted(long j, long j2, long j3) {
            recomputeLastDuration(j, false);
            this.mInDischarge = true;
            if (this.mLastAddedTimeUs == j) {
                this.mTotalTimeUs += this.mLastAddedDurationUs;
            }
            super.onTimeStarted(j, j2, j3);
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.Timer
        public void logState(Printer printer, String str) {
            super.logState(printer, str);
            printer.println(str + "mLastAddedTime=" + this.mLastAddedTimeUs + " mLastAddedDuration=" + this.mLastAddedDurationUs);
        }

        public final long computeOverage(long j) {
            if (this.mLastAddedTimeUs > 0) {
                return this.mLastAddedDurationUs - j;
            }
            return 0L;
        }

        public final void recomputeLastDuration(long j, boolean z) {
            long computeOverage = computeOverage(j);
            if (computeOverage > 0) {
                if (this.mInDischarge) {
                    this.mTotalTimeUs -= computeOverage;
                }
                if (z) {
                    this.mLastAddedTimeUs = 0L;
                    return;
                }
                this.mLastAddedTimeUs = j;
                this.mLastAddedDurationUs -= computeOverage;
            }
        }

        public void addDuration(long j, long j2) {
            long j3 = j2 * 1000;
            recomputeLastDuration(j3, true);
            this.mLastAddedTimeUs = j3;
            long j4 = j * 1000;
            this.mLastAddedDurationUs = j4;
            if (this.mInDischarge) {
                this.mTotalTimeUs += j4;
                this.mCount++;
            }
        }

        public void abortLastDuration(long j) {
            recomputeLastDuration(j * 1000, true);
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.Timer
        public int computeCurrentCountLocked() {
            return this.mCount;
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.Timer
        public long computeRunTimeLocked(long j, long j2) {
            long computeOverage = computeOverage(j2);
            if (computeOverage > 0) {
                this.mTotalTimeUs = computeOverage;
                return computeOverage;
            }
            return this.mTotalTimeUs;
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.Timer, com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
        public boolean reset(boolean z, long j) {
            recomputeLastDuration(j, true);
            boolean z2 = false;
            boolean z3 = this.mLastAddedTimeUs == j;
            if (!z3 && z) {
                z2 = true;
            }
            super.reset(z2, j);
            return !z3;
        }
    }

    /* loaded from: classes2.dex */
    public static class DurationTimer extends StopwatchTimer {
        public long mCurrentDurationMs;
        public long mMaxDurationMs;
        public long mStartTimeMs;
        public long mTotalDurationMs;

        public DurationTimer(Clock clock, Uid uid, int i, ArrayList<StopwatchTimer> arrayList, TimeBase timeBase) {
            super(clock, uid, i, arrayList, timeBase);
            this.mStartTimeMs = -1L;
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.Timer
        public void writeSummaryFromParcelLocked(Parcel parcel, long j) {
            super.writeSummaryFromParcelLocked(parcel, j);
            long j2 = j / 1000;
            parcel.writeLong(getMaxDurationMsLocked(j2));
            parcel.writeLong(getTotalDurationMsLocked(j2));
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.StopwatchTimer, com.android.server.power.stats.BatteryStatsImpl.Timer
        public void readSummaryFromParcelLocked(Parcel parcel) {
            super.readSummaryFromParcelLocked(parcel);
            this.mMaxDurationMs = parcel.readLong();
            this.mTotalDurationMs = parcel.readLong();
            this.mStartTimeMs = -1L;
            this.mCurrentDurationMs = 0L;
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.Timer, com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
        public void onTimeStarted(long j, long j2, long j3) {
            super.onTimeStarted(j, j2, j3);
            if (this.mNesting > 0) {
                this.mStartTimeMs = j3 / 1000;
            }
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.StopwatchTimer, com.android.server.power.stats.BatteryStatsImpl.Timer, com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
        public void onTimeStopped(long j, long j2, long j3) {
            super.onTimeStopped(j, j2, j3);
            if (this.mNesting > 0) {
                this.mCurrentDurationMs += (j3 / 1000) - this.mStartTimeMs;
            }
            this.mStartTimeMs = -1L;
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.StopwatchTimer, com.android.server.power.stats.BatteryStatsImpl.Timer
        public void logState(Printer printer, String str) {
            super.logState(printer, str);
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.StopwatchTimer
        public void startRunningLocked(long j) {
            super.startRunningLocked(j);
            if (this.mNesting == 1 && this.mTimeBase.isRunning()) {
                this.mStartTimeMs = this.mTimeBase.getRealtime(j * 1000) / 1000;
            }
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.StopwatchTimer
        public void stopRunningLocked(long j) {
            if (this.mNesting == 1) {
                long currentDurationMsLocked = getCurrentDurationMsLocked(j);
                this.mTotalDurationMs += currentDurationMsLocked;
                if (currentDurationMsLocked > this.mMaxDurationMs) {
                    this.mMaxDurationMs = currentDurationMsLocked;
                }
                this.mStartTimeMs = -1L;
                this.mCurrentDurationMs = 0L;
            }
            super.stopRunningLocked(j);
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.StopwatchTimer, com.android.server.power.stats.BatteryStatsImpl.Timer, com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
        public boolean reset(boolean z, long j) {
            boolean reset = super.reset(z, j);
            this.mMaxDurationMs = 0L;
            this.mTotalDurationMs = 0L;
            this.mCurrentDurationMs = 0L;
            if (this.mNesting > 0) {
                this.mStartTimeMs = this.mTimeBase.getRealtime(j) / 1000;
            } else {
                this.mStartTimeMs = -1L;
            }
            return reset;
        }

        public long getMaxDurationMsLocked(long j) {
            if (this.mNesting > 0) {
                long currentDurationMsLocked = getCurrentDurationMsLocked(j);
                if (currentDurationMsLocked > this.mMaxDurationMs) {
                    return currentDurationMsLocked;
                }
            }
            return this.mMaxDurationMs;
        }

        public long getCurrentDurationMsLocked(long j) {
            long j2 = this.mCurrentDurationMs;
            return (this.mNesting <= 0 || !this.mTimeBase.isRunning()) ? j2 : j2 + ((this.mTimeBase.getRealtime(j * 1000) / 1000) - this.mStartTimeMs);
        }

        public long getTotalDurationMsLocked(long j) {
            return this.mTotalDurationMs + getCurrentDurationMsLocked(j);
        }
    }

    /* loaded from: classes2.dex */
    public static class StopwatchTimer extends Timer {
        public long mAcquireTimeUs;
        @VisibleForTesting
        public boolean mInList;
        public int mNesting;
        public long mTimeoutUs;
        public final ArrayList<StopwatchTimer> mTimerPool;
        public final Uid mUid;
        public long mUpdateTimeUs;

        public StopwatchTimer(Clock clock, Uid uid, int i, ArrayList<StopwatchTimer> arrayList, TimeBase timeBase, Parcel parcel) {
            super(clock, i, timeBase, parcel);
            this.mAcquireTimeUs = -1L;
            this.mUid = uid;
            this.mTimerPool = arrayList;
            this.mUpdateTimeUs = parcel.readLong();
        }

        public StopwatchTimer(Clock clock, Uid uid, int i, ArrayList<StopwatchTimer> arrayList, TimeBase timeBase) {
            super(clock, i, timeBase);
            this.mAcquireTimeUs = -1L;
            this.mUid = uid;
            this.mTimerPool = arrayList;
        }

        public void setTimeout(long j) {
            this.mTimeoutUs = j;
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.Timer, com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
        public void onTimeStopped(long j, long j2, long j3) {
            if (this.mNesting > 0) {
                super.onTimeStopped(j, j2, j3);
                this.mUpdateTimeUs = j3;
            }
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.Timer
        public void logState(Printer printer, String str) {
            super.logState(printer, str);
            printer.println(str + "mNesting=" + this.mNesting + " mUpdateTime=" + this.mUpdateTimeUs + " mAcquireTime=" + this.mAcquireTimeUs);
        }

        public void startRunningLocked(long j) {
            int i = this.mNesting;
            this.mNesting = i + 1;
            if (i == 0) {
                long realtime = this.mTimeBase.getRealtime(j * 1000);
                this.mUpdateTimeUs = realtime;
                ArrayList<StopwatchTimer> arrayList = this.mTimerPool;
                if (arrayList != null) {
                    refreshTimersLocked(realtime, arrayList, null);
                    this.mTimerPool.add(this);
                }
                if (this.mTimeBase.isRunning()) {
                    this.mCount++;
                    this.mAcquireTimeUs = this.mTotalTimeUs;
                    return;
                }
                this.mAcquireTimeUs = -1L;
            }
        }

        public boolean isRunningLocked() {
            return this.mNesting > 0;
        }

        public void stopRunningLocked(long j) {
            int i = this.mNesting;
            if (i == 0) {
                return;
            }
            int i2 = i - 1;
            this.mNesting = i2;
            if (i2 == 0) {
                long j2 = j * 1000;
                long realtime = this.mTimeBase.getRealtime(j2);
                ArrayList<StopwatchTimer> arrayList = this.mTimerPool;
                if (arrayList != null) {
                    refreshTimersLocked(realtime, arrayList, null);
                    this.mTimerPool.remove(this);
                } else {
                    this.mNesting = 1;
                    this.mTotalTimeUs = computeRunTimeLocked(realtime, j2);
                    this.mNesting = 0;
                }
                long j3 = this.mAcquireTimeUs;
                if (j3 < 0 || this.mTotalTimeUs != j3) {
                    return;
                }
                this.mCount--;
            }
        }

        public void stopAllRunningLocked(long j) {
            if (this.mNesting > 0) {
                this.mNesting = 1;
                stopRunningLocked(j);
            }
        }

        public static long refreshTimersLocked(long j, ArrayList<StopwatchTimer> arrayList, StopwatchTimer stopwatchTimer) {
            int size = arrayList.size();
            long j2 = 0;
            for (int i = size - 1; i >= 0; i--) {
                StopwatchTimer stopwatchTimer2 = arrayList.get(i);
                long j3 = j - stopwatchTimer2.mUpdateTimeUs;
                if (j3 > 0) {
                    long j4 = j3 / size;
                    if (stopwatchTimer2 == stopwatchTimer) {
                        j2 = j4;
                    }
                    stopwatchTimer2.mTotalTimeUs += j4;
                }
                stopwatchTimer2.mUpdateTimeUs = j;
            }
            return j2;
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.Timer
        public long computeRunTimeLocked(long j, long j2) {
            long j3 = this.mTimeoutUs;
            long j4 = 0;
            if (j3 > 0) {
                long j5 = this.mUpdateTimeUs;
                if (j > j5 + j3) {
                    j = j5 + j3;
                }
            }
            long j6 = this.mTotalTimeUs;
            if (this.mNesting > 0) {
                long j7 = j - this.mUpdateTimeUs;
                ArrayList<StopwatchTimer> arrayList = this.mTimerPool;
                j4 = j7 / (arrayList != null ? arrayList.size() : 1);
            }
            return j6 + j4;
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.Timer
        public int computeCurrentCountLocked() {
            return this.mCount;
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.Timer, com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
        public boolean reset(boolean z, long j) {
            boolean z2 = true;
            boolean z3 = this.mNesting <= 0;
            if (!z3 || !z) {
                z2 = false;
            }
            super.reset(z2, j);
            if (this.mNesting > 0) {
                this.mUpdateTimeUs = this.mTimeBase.getRealtime(j);
            }
            this.mAcquireTimeUs = -1L;
            return z3;
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.Timer, com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
        public void detach() {
            super.detach();
            ArrayList<StopwatchTimer> arrayList = this.mTimerPool;
            if (arrayList != null) {
                arrayList.remove(this);
            }
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.Timer
        public void readSummaryFromParcelLocked(Parcel parcel) {
            super.readSummaryFromParcelLocked(parcel);
            this.mNesting = 0;
        }

        public void setMark(long j) {
            long realtime = this.mTimeBase.getRealtime(j * 1000);
            if (this.mNesting > 0) {
                ArrayList<StopwatchTimer> arrayList = this.mTimerPool;
                if (arrayList != null) {
                    refreshTimersLocked(realtime, arrayList, this);
                } else {
                    this.mTotalTimeUs += realtime - this.mUpdateTimeUs;
                    this.mUpdateTimeUs = realtime;
                }
            }
            this.mTimeBeforeMarkUs = this.mTotalTimeUs;
        }
    }

    /* loaded from: classes2.dex */
    public static class DualTimer extends DurationTimer {
        public final DurationTimer mSubTimer;

        public DualTimer(Clock clock, Uid uid, int i, ArrayList<StopwatchTimer> arrayList, TimeBase timeBase, TimeBase timeBase2) {
            super(clock, uid, i, arrayList, timeBase);
            this.mSubTimer = new DurationTimer(clock, uid, i, null, timeBase2);
        }

        public DurationTimer getSubTimer() {
            return this.mSubTimer;
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.DurationTimer, com.android.server.power.stats.BatteryStatsImpl.StopwatchTimer
        public void startRunningLocked(long j) {
            super.startRunningLocked(j);
            this.mSubTimer.startRunningLocked(j);
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.DurationTimer, com.android.server.power.stats.BatteryStatsImpl.StopwatchTimer
        public void stopRunningLocked(long j) {
            super.stopRunningLocked(j);
            this.mSubTimer.stopRunningLocked(j);
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.StopwatchTimer
        public void stopAllRunningLocked(long j) {
            super.stopAllRunningLocked(j);
            this.mSubTimer.stopAllRunningLocked(j);
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.DurationTimer, com.android.server.power.stats.BatteryStatsImpl.StopwatchTimer, com.android.server.power.stats.BatteryStatsImpl.Timer, com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
        public boolean reset(boolean z, long j) {
            return !((!super.reset(z, j)) | (!this.mSubTimer.reset(false, j)) | false);
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.StopwatchTimer, com.android.server.power.stats.BatteryStatsImpl.Timer, com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
        public void detach() {
            this.mSubTimer.detach();
            super.detach();
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.DurationTimer, com.android.server.power.stats.BatteryStatsImpl.Timer
        public void writeSummaryFromParcelLocked(Parcel parcel, long j) {
            super.writeSummaryFromParcelLocked(parcel, j);
            this.mSubTimer.writeSummaryFromParcelLocked(parcel, j);
        }

        @Override // com.android.server.power.stats.BatteryStatsImpl.DurationTimer, com.android.server.power.stats.BatteryStatsImpl.StopwatchTimer, com.android.server.power.stats.BatteryStatsImpl.Timer
        public void readSummaryFromParcelLocked(Parcel parcel) {
            super.readSummaryFromParcelLocked(parcel);
            this.mSubTimer.readSummaryFromParcelLocked(parcel);
        }
    }

    /* loaded from: classes2.dex */
    public abstract class OverflowArrayMap<T> {
        public ArrayMap<String, MutableInt> mActiveOverflow;
        public T mCurOverflow;
        public long mLastCleanupTimeMs;
        public long mLastClearTimeMs;
        public long mLastOverflowFinishTimeMs;
        public long mLastOverflowTimeMs;
        public final ArrayMap<String, T> mMap = new ArrayMap<>();
        public final int mUid;

        public abstract T instantiateObject();

        public OverflowArrayMap(int i) {
            this.mUid = i;
        }

        public ArrayMap<String, T> getMap() {
            return this.mMap;
        }

        public void add(String str, T t) {
            if (str == null) {
                str = "";
            }
            this.mMap.put(str, t);
            if ("*overflow*".equals(str)) {
                this.mCurOverflow = t;
            }
        }

        public void cleanup(long j) {
            this.mLastCleanupTimeMs = j;
            ArrayMap<String, MutableInt> arrayMap = this.mActiveOverflow;
            if (arrayMap != null && arrayMap.size() == 0) {
                this.mActiveOverflow = null;
            }
            if (this.mActiveOverflow == null) {
                if (this.mMap.containsKey("*overflow*")) {
                    Slog.wtf("BatteryStatsImpl", "Cleaning up with no active overflow, but have overflow entry " + this.mMap.get("*overflow*"));
                    this.mMap.remove("*overflow*");
                }
                this.mCurOverflow = null;
            } else if (this.mCurOverflow == null || !this.mMap.containsKey("*overflow*")) {
                Slog.wtf("BatteryStatsImpl", "Cleaning up with active overflow, but no overflow entry: cur=" + this.mCurOverflow + " map=" + this.mMap.get("*overflow*"));
            }
        }

        public T startObject(String str, long j) {
            MutableInt mutableInt;
            if (str == null) {
                str = "";
            }
            T t = this.mMap.get(str);
            if (t != null) {
                return t;
            }
            ArrayMap<String, MutableInt> arrayMap = this.mActiveOverflow;
            if (arrayMap != null && (mutableInt = arrayMap.get(str)) != null) {
                T t2 = this.mCurOverflow;
                if (t2 == null) {
                    Slog.wtf("BatteryStatsImpl", "Have active overflow " + str + " but null overflow");
                    t2 = instantiateObject();
                    this.mCurOverflow = t2;
                    this.mMap.put("*overflow*", t2);
                }
                mutableInt.value++;
                return t2;
            } else if (this.mMap.size() >= BatteryStatsImpl.MAX_WAKELOCKS_PER_UID) {
                T t3 = this.mCurOverflow;
                if (t3 == null) {
                    t3 = instantiateObject();
                    this.mCurOverflow = t3;
                    this.mMap.put("*overflow*", t3);
                }
                if (this.mActiveOverflow == null) {
                    this.mActiveOverflow = new ArrayMap<>();
                }
                this.mActiveOverflow.put(str, new MutableInt(1));
                this.mLastOverflowTimeMs = j;
                return t3;
            } else {
                T instantiateObject = instantiateObject();
                this.mMap.put(str, instantiateObject);
                return instantiateObject;
            }
        }

        public T stopObject(String str, long j) {
            MutableInt mutableInt;
            T t;
            if (str == null) {
                str = "";
            }
            T t2 = this.mMap.get(str);
            if (t2 != null) {
                return t2;
            }
            ArrayMap<String, MutableInt> arrayMap = this.mActiveOverflow;
            if (arrayMap != null && (mutableInt = arrayMap.get(str)) != null && (t = this.mCurOverflow) != null) {
                int i = mutableInt.value - 1;
                mutableInt.value = i;
                if (i <= 0) {
                    this.mActiveOverflow.remove(str);
                    this.mLastOverflowFinishTimeMs = j;
                }
                return t;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Unable to find object for ");
            sb.append(str);
            sb.append(" in uid ");
            sb.append(this.mUid);
            sb.append(" mapsize=");
            sb.append(this.mMap.size());
            sb.append(" activeoverflow=");
            sb.append(this.mActiveOverflow);
            sb.append(" curoverflow=");
            sb.append(this.mCurOverflow);
            if (this.mLastOverflowTimeMs != 0) {
                sb.append(" lastOverflowTime=");
                TimeUtils.formatDuration(this.mLastOverflowTimeMs - j, sb);
            }
            if (this.mLastOverflowFinishTimeMs != 0) {
                sb.append(" lastOverflowFinishTime=");
                TimeUtils.formatDuration(this.mLastOverflowFinishTimeMs - j, sb);
            }
            if (this.mLastClearTimeMs != 0) {
                sb.append(" lastClearTime=");
                TimeUtils.formatDuration(this.mLastClearTimeMs - j, sb);
            }
            if (this.mLastCleanupTimeMs != 0) {
                sb.append(" lastCleanupTime=");
                TimeUtils.formatDuration(this.mLastCleanupTimeMs - j, sb);
            }
            Slog.wtf("BatteryStatsImpl", sb.toString());
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static class ControllerActivityCounterImpl extends BatteryStats.ControllerActivityCounter implements Parcelable {
        public final Clock mClock;
        public TimeMultiStateCounter mIdleTimeMillis;
        public final LongSamplingCounter mMonitoredRailChargeConsumedMaMs;
        public int mNumTxStates;
        public final LongSamplingCounter mPowerDrainMaMs;
        public int mProcessState;
        public TimeMultiStateCounter mRxTimeMillis;
        public final LongSamplingCounter mScanTimeMillis;
        public final LongSamplingCounter mSleepTimeMillis;
        public final TimeBase mTimeBase;
        public TimeMultiStateCounter[] mTxTimeMillis;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public ControllerActivityCounterImpl(Clock clock, TimeBase timeBase, int i) {
            this.mClock = clock;
            this.mTimeBase = timeBase;
            this.mNumTxStates = i;
            this.mScanTimeMillis = new LongSamplingCounter(timeBase);
            this.mSleepTimeMillis = new LongSamplingCounter(timeBase);
            this.mPowerDrainMaMs = new LongSamplingCounter(timeBase);
            this.mMonitoredRailChargeConsumedMaMs = new LongSamplingCounter(timeBase);
        }

        public void readSummaryFromParcel(Parcel parcel) {
            this.mIdleTimeMillis = readTimeMultiStateCounter(parcel, this.mTimeBase);
            this.mScanTimeMillis.readSummaryFromParcelLocked(parcel);
            this.mSleepTimeMillis.readSummaryFromParcelLocked(parcel);
            this.mRxTimeMillis = readTimeMultiStateCounter(parcel, this.mTimeBase);
            this.mTxTimeMillis = readTimeMultiStateCounters(parcel, this.mTimeBase, this.mNumTxStates);
            this.mPowerDrainMaMs.readSummaryFromParcelLocked(parcel);
            this.mMonitoredRailChargeConsumedMaMs.readSummaryFromParcelLocked(parcel);
        }

        public void writeSummaryToParcel(Parcel parcel) {
            writeTimeMultiStateCounter(parcel, this.mIdleTimeMillis);
            this.mScanTimeMillis.writeSummaryFromParcelLocked(parcel);
            this.mSleepTimeMillis.writeSummaryFromParcelLocked(parcel);
            writeTimeMultiStateCounter(parcel, this.mRxTimeMillis);
            writeTimeMultiStateCounters(parcel, this.mTxTimeMillis);
            this.mPowerDrainMaMs.writeSummaryFromParcelLocked(parcel);
            this.mMonitoredRailChargeConsumedMaMs.writeSummaryFromParcelLocked(parcel);
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            writeTimeMultiStateCounter(parcel, this.mIdleTimeMillis);
            this.mScanTimeMillis.writeToParcel(parcel);
            this.mSleepTimeMillis.writeToParcel(parcel);
            writeTimeMultiStateCounter(parcel, this.mRxTimeMillis);
            writeTimeMultiStateCounters(parcel, this.mTxTimeMillis);
            this.mPowerDrainMaMs.writeToParcel(parcel);
            this.mMonitoredRailChargeConsumedMaMs.writeToParcel(parcel);
        }

        public final TimeMultiStateCounter readTimeMultiStateCounter(Parcel parcel, TimeBase timeBase) {
            if (parcel.readBoolean()) {
                return TimeMultiStateCounter.readFromParcel(parcel, timeBase, 5, this.mClock.elapsedRealtime());
            }
            return null;
        }

        public final void writeTimeMultiStateCounter(Parcel parcel, TimeMultiStateCounter timeMultiStateCounter) {
            if (timeMultiStateCounter != null) {
                parcel.writeBoolean(true);
                timeMultiStateCounter.writeToParcel(parcel);
                return;
            }
            parcel.writeBoolean(false);
        }

        public final TimeMultiStateCounter[] readTimeMultiStateCounters(Parcel parcel, TimeBase timeBase, int i) {
            if (parcel.readBoolean()) {
                int readInt = parcel.readInt();
                boolean z = readInt == i;
                TimeMultiStateCounter[] timeMultiStateCounterArr = new TimeMultiStateCounter[readInt];
                for (int i2 = 0; i2 < readInt; i2++) {
                    TimeMultiStateCounter readFromParcel = TimeMultiStateCounter.readFromParcel(parcel, timeBase, 5, this.mClock.elapsedRealtime());
                    if (readFromParcel != null) {
                        timeMultiStateCounterArr[i2] = readFromParcel;
                    } else {
                        z = false;
                    }
                }
                if (z) {
                    return timeMultiStateCounterArr;
                }
                return null;
            }
            return null;
        }

        public final void writeTimeMultiStateCounters(Parcel parcel, TimeMultiStateCounter[] timeMultiStateCounterArr) {
            if (timeMultiStateCounterArr != null) {
                parcel.writeBoolean(true);
                parcel.writeInt(timeMultiStateCounterArr.length);
                for (TimeMultiStateCounter timeMultiStateCounter : timeMultiStateCounterArr) {
                    timeMultiStateCounter.writeToParcel(parcel);
                }
                return;
            }
            parcel.writeBoolean(false);
        }

        public void reset(boolean z, long j) {
            BatteryStatsImpl.resetIfNotNull(this.mIdleTimeMillis, z, j);
            this.mScanTimeMillis.reset(z, j);
            this.mSleepTimeMillis.reset(z, j);
            BatteryStatsImpl.resetIfNotNull(this.mRxTimeMillis, z, j);
            BatteryStatsImpl.resetIfNotNull(this.mTxTimeMillis, z, j);
            this.mPowerDrainMaMs.reset(z, j);
            this.mMonitoredRailChargeConsumedMaMs.reset(z, j);
        }

        public void detach() {
            BatteryStatsImpl.detachIfNotNull(this.mIdleTimeMillis);
            this.mScanTimeMillis.detach();
            this.mSleepTimeMillis.detach();
            BatteryStatsImpl.detachIfNotNull(this.mRxTimeMillis);
            BatteryStatsImpl.detachIfNotNull(this.mTxTimeMillis);
            this.mPowerDrainMaMs.detach();
            this.mMonitoredRailChargeConsumedMaMs.detach();
        }

        public BatteryStats.LongCounter getIdleTimeCounter() {
            TimeMultiStateCounter timeMultiStateCounter = this.mIdleTimeMillis;
            return timeMultiStateCounter == null ? BatteryStatsImpl.ZERO_LONG_COUNTER : timeMultiStateCounter;
        }

        public final TimeMultiStateCounter getOrCreateIdleTimeCounter() {
            if (this.mIdleTimeMillis == null) {
                this.mIdleTimeMillis = createTimeMultiStateCounter();
            }
            return this.mIdleTimeMillis;
        }

        public LongSamplingCounter getScanTimeCounter() {
            return this.mScanTimeMillis;
        }

        public LongSamplingCounter getSleepTimeCounter() {
            return this.mSleepTimeMillis;
        }

        public BatteryStats.LongCounter getRxTimeCounter() {
            TimeMultiStateCounter timeMultiStateCounter = this.mRxTimeMillis;
            return timeMultiStateCounter == null ? BatteryStatsImpl.ZERO_LONG_COUNTER : timeMultiStateCounter;
        }

        public final TimeMultiStateCounter getOrCreateRxTimeCounter() {
            if (this.mRxTimeMillis == null) {
                this.mRxTimeMillis = createTimeMultiStateCounter();
            }
            return this.mRxTimeMillis;
        }

        public BatteryStats.LongCounter[] getTxTimeCounters() {
            TimeMultiStateCounter[] timeMultiStateCounterArr = this.mTxTimeMillis;
            return timeMultiStateCounterArr == null ? BatteryStatsImpl.ZERO_LONG_COUNTER_ARRAY : timeMultiStateCounterArr;
        }

        public final TimeMultiStateCounter[] getOrCreateTxTimeCounters() {
            if (this.mTxTimeMillis == null) {
                this.mTxTimeMillis = new TimeMultiStateCounter[this.mNumTxStates];
                for (int i = 0; i < this.mNumTxStates; i++) {
                    this.mTxTimeMillis[i] = createTimeMultiStateCounter();
                }
            }
            return this.mTxTimeMillis;
        }

        public final TimeMultiStateCounter createTimeMultiStateCounter() {
            long elapsedRealtime = this.mClock.elapsedRealtime();
            TimeMultiStateCounter timeMultiStateCounter = new TimeMultiStateCounter(this.mTimeBase, 5, elapsedRealtime);
            timeMultiStateCounter.setState(BatteryStats.mapUidProcessStateToBatteryConsumerProcessState(this.mProcessState), elapsedRealtime);
            timeMultiStateCounter.update(0L, elapsedRealtime);
            return timeMultiStateCounter;
        }

        public LongSamplingCounter getPowerCounter() {
            return this.mPowerDrainMaMs;
        }

        public LongSamplingCounter getMonitoredRailChargeConsumedMaMs() {
            return this.mMonitoredRailChargeConsumedMaMs;
        }

        public final void setState(int i, long j) {
            this.mProcessState = i;
            TimeMultiStateCounter timeMultiStateCounter = this.mIdleTimeMillis;
            if (timeMultiStateCounter != null) {
                timeMultiStateCounter.setState(i, j);
            }
            TimeMultiStateCounter timeMultiStateCounter2 = this.mRxTimeMillis;
            if (timeMultiStateCounter2 != null) {
                timeMultiStateCounter2.setState(i, j);
            }
            if (this.mTxTimeMillis == null) {
                return;
            }
            int i2 = 0;
            while (true) {
                TimeMultiStateCounter[] timeMultiStateCounterArr = this.mTxTimeMillis;
                if (i2 >= timeMultiStateCounterArr.length) {
                    return;
                }
                timeMultiStateCounterArr[i2].setState(i, j);
                i2++;
            }
        }
    }

    public SamplingTimer getRpmTimerLocked(String str) {
        SamplingTimer samplingTimer = this.mRpmStats.get(str);
        if (samplingTimer == null) {
            SamplingTimer samplingTimer2 = new SamplingTimer(this.mClock, this.mOnBatteryTimeBase);
            this.mRpmStats.put(str, samplingTimer2);
            return samplingTimer2;
        }
        return samplingTimer;
    }

    public SamplingTimer getScreenOffRpmTimerLocked(String str) {
        SamplingTimer samplingTimer = this.mScreenOffRpmStats.get(str);
        if (samplingTimer == null) {
            SamplingTimer samplingTimer2 = new SamplingTimer(this.mClock, this.mOnBatteryScreenOffTimeBase);
            this.mScreenOffRpmStats.put(str, samplingTimer2);
            return samplingTimer2;
        }
        return samplingTimer;
    }

    public SamplingTimer getWakeupReasonTimerLocked(String str) {
        SamplingTimer samplingTimer = this.mWakeupReasonStats.get(str);
        if (samplingTimer == null) {
            SamplingTimer samplingTimer2 = new SamplingTimer(this.mClock, this.mOnBatteryTimeBase);
            this.mWakeupReasonStats.put(str, samplingTimer2);
            return samplingTimer2;
        }
        return samplingTimer;
    }

    public SamplingTimer getKernelWakelockTimerLocked(String str) {
        SamplingTimer samplingTimer = this.mKernelWakelockStats.get(str);
        if (samplingTimer == null) {
            SamplingTimer samplingTimer2 = new SamplingTimer(this.mClock, this.mOnBatteryScreenOffTimeBase);
            this.mKernelWakelockStats.put(str, samplingTimer2);
            return samplingTimer2;
        }
        return samplingTimer;
    }

    public SamplingTimer getKernelMemoryTimerLocked(long j) {
        SamplingTimer samplingTimer = this.mKernelMemoryStats.get(j);
        if (samplingTimer == null) {
            SamplingTimer samplingTimer2 = new SamplingTimer(this.mClock, this.mOnBatteryTimeBase);
            this.mKernelMemoryStats.put(j, samplingTimer2);
            return samplingTimer2;
        }
        return samplingTimer;
    }

    /* loaded from: classes2.dex */
    public class HistoryStepDetailsCalculatorImpl implements BatteryStatsHistory.HistoryStepDetailsCalculator {
        public long mCurStepCpuSystemTimeMs;
        public long mCurStepCpuUserTimeMs;
        public long mCurStepStatIOWaitTimeMs;
        public long mCurStepStatIdleTimeMs;
        public long mCurStepStatIrqTimeMs;
        public long mCurStepStatSoftIrqTimeMs;
        public long mCurStepStatSystemTimeMs;
        public long mCurStepStatUserTimeMs;
        public final BatteryStats.HistoryStepDetails mDetails;
        public boolean mHasHistoryStepDetails;
        public long mLastStepCpuSystemTimeMs;
        public long mLastStepCpuUserTimeMs;
        public long mLastStepStatIOWaitTimeMs;
        public long mLastStepStatIdleTimeMs;
        public long mLastStepStatIrqTimeMs;
        public long mLastStepStatSoftIrqTimeMs;
        public long mLastStepStatSystemTimeMs;
        public long mLastStepStatUserTimeMs;
        public boolean mUpdateRequested;

        public HistoryStepDetailsCalculatorImpl() {
            this.mDetails = new BatteryStats.HistoryStepDetails();
        }

        public BatteryStats.HistoryStepDetails getHistoryStepDetails() {
            if (!this.mUpdateRequested) {
                this.mUpdateRequested = true;
                BatteryStatsImpl.this.requestImmediateCpuUpdate();
                if (BatteryStatsImpl.this.mPlatformIdleStateCallback != null) {
                    this.mDetails.statSubsystemPowerState = BatteryStatsImpl.this.mPlatformIdleStateCallback.getSubsystemLowPowerStats();
                }
            }
            int i = 0;
            if (!this.mHasHistoryStepDetails) {
                int size = BatteryStatsImpl.this.mUidStats.size();
                while (i < size) {
                    Uid uid = (Uid) BatteryStatsImpl.this.mUidStats.valueAt(i);
                    uid.mLastStepUserTimeMs = uid.mCurStepUserTimeMs;
                    uid.mLastStepSystemTimeMs = uid.mCurStepSystemTimeMs;
                    i++;
                }
                this.mLastStepCpuUserTimeMs = this.mCurStepCpuUserTimeMs;
                this.mLastStepCpuSystemTimeMs = this.mCurStepCpuSystemTimeMs;
                this.mLastStepStatUserTimeMs = this.mCurStepStatUserTimeMs;
                this.mLastStepStatSystemTimeMs = this.mCurStepStatSystemTimeMs;
                this.mLastStepStatIOWaitTimeMs = this.mCurStepStatIOWaitTimeMs;
                this.mLastStepStatIrqTimeMs = this.mCurStepStatIrqTimeMs;
                this.mLastStepStatSoftIrqTimeMs = this.mCurStepStatSoftIrqTimeMs;
                this.mLastStepStatIdleTimeMs = this.mCurStepStatIdleTimeMs;
                return null;
            }
            BatteryStats.HistoryStepDetails historyStepDetails = this.mDetails;
            historyStepDetails.userTime = (int) (this.mCurStepCpuUserTimeMs - this.mLastStepCpuUserTimeMs);
            historyStepDetails.systemTime = (int) (this.mCurStepCpuSystemTimeMs - this.mLastStepCpuSystemTimeMs);
            historyStepDetails.statUserTime = (int) (this.mCurStepStatUserTimeMs - this.mLastStepStatUserTimeMs);
            historyStepDetails.statSystemTime = (int) (this.mCurStepStatSystemTimeMs - this.mLastStepStatSystemTimeMs);
            historyStepDetails.statIOWaitTime = (int) (this.mCurStepStatIOWaitTimeMs - this.mLastStepStatIOWaitTimeMs);
            historyStepDetails.statIrqTime = (int) (this.mCurStepStatIrqTimeMs - this.mLastStepStatIrqTimeMs);
            historyStepDetails.statSoftIrqTime = (int) (this.mCurStepStatSoftIrqTimeMs - this.mLastStepStatSoftIrqTimeMs);
            historyStepDetails.statIdlTime = (int) (this.mCurStepStatIdleTimeMs - this.mLastStepStatIdleTimeMs);
            historyStepDetails.appCpuUid3 = -1;
            historyStepDetails.appCpuUid2 = -1;
            historyStepDetails.appCpuUid1 = -1;
            historyStepDetails.appCpuUTime3 = 0;
            historyStepDetails.appCpuUTime2 = 0;
            historyStepDetails.appCpuUTime1 = 0;
            historyStepDetails.appCpuSTime3 = 0;
            historyStepDetails.appCpuSTime2 = 0;
            historyStepDetails.appCpuSTime1 = 0;
            int size2 = BatteryStatsImpl.this.mUidStats.size();
            while (i < size2) {
                Uid uid2 = (Uid) BatteryStatsImpl.this.mUidStats.valueAt(i);
                long j = uid2.mCurStepUserTimeMs;
                int i2 = (int) (j - uid2.mLastStepUserTimeMs);
                long j2 = uid2.mCurStepSystemTimeMs;
                int i3 = (int) (j2 - uid2.mLastStepSystemTimeMs);
                int i4 = i2 + i3;
                uid2.mLastStepUserTimeMs = j;
                uid2.mLastStepSystemTimeMs = j2;
                BatteryStats.HistoryStepDetails historyStepDetails2 = this.mDetails;
                if (i4 > historyStepDetails2.appCpuUTime3 + historyStepDetails2.appCpuSTime3) {
                    int i5 = historyStepDetails2.appCpuUTime2;
                    int i6 = historyStepDetails2.appCpuSTime2;
                    if (i4 <= i5 + i6) {
                        historyStepDetails2.appCpuUid3 = uid2.mUid;
                        historyStepDetails2.appCpuUTime3 = i2;
                        historyStepDetails2.appCpuSTime3 = i3;
                    } else {
                        historyStepDetails2.appCpuUid3 = historyStepDetails2.appCpuUid2;
                        historyStepDetails2.appCpuUTime3 = i5;
                        historyStepDetails2.appCpuSTime3 = i6;
                        int i7 = historyStepDetails2.appCpuUTime1;
                        int i8 = historyStepDetails2.appCpuSTime1;
                        if (i4 <= i7 + i8) {
                            historyStepDetails2.appCpuUid2 = uid2.mUid;
                            historyStepDetails2.appCpuUTime2 = i2;
                            historyStepDetails2.appCpuSTime2 = i3;
                        } else {
                            historyStepDetails2.appCpuUid2 = historyStepDetails2.appCpuUid1;
                            historyStepDetails2.appCpuUTime2 = i7;
                            historyStepDetails2.appCpuSTime2 = i8;
                            historyStepDetails2.appCpuUid1 = uid2.mUid;
                            historyStepDetails2.appCpuUTime1 = i2;
                            historyStepDetails2.appCpuSTime1 = i3;
                        }
                    }
                }
                i++;
            }
            this.mLastStepCpuUserTimeMs = this.mCurStepCpuUserTimeMs;
            this.mLastStepCpuSystemTimeMs = this.mCurStepCpuSystemTimeMs;
            this.mLastStepStatUserTimeMs = this.mCurStepStatUserTimeMs;
            this.mLastStepStatSystemTimeMs = this.mCurStepStatSystemTimeMs;
            this.mLastStepStatIOWaitTimeMs = this.mCurStepStatIOWaitTimeMs;
            this.mLastStepStatIrqTimeMs = this.mCurStepStatIrqTimeMs;
            this.mLastStepStatSoftIrqTimeMs = this.mCurStepStatSoftIrqTimeMs;
            this.mLastStepStatIdleTimeMs = this.mCurStepStatIdleTimeMs;
            return this.mDetails;
        }

        public void addCpuStats(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            this.mCurStepCpuUserTimeMs += i;
            this.mCurStepCpuSystemTimeMs += i2;
            this.mCurStepStatUserTimeMs += i3;
            this.mCurStepStatSystemTimeMs += i4;
            this.mCurStepStatIOWaitTimeMs += i5;
            this.mCurStepStatIrqTimeMs += i6;
            this.mCurStepStatSoftIrqTimeMs += i7;
            this.mCurStepStatIdleTimeMs += i8;
        }

        public void finishAddingCpuLocked() {
            this.mHasHistoryStepDetails = true;
            this.mUpdateRequested = false;
        }

        public void clear() {
            this.mHasHistoryStepDetails = false;
            this.mCurStepCpuUserTimeMs = 0L;
            this.mLastStepCpuUserTimeMs = 0L;
            this.mCurStepCpuSystemTimeMs = 0L;
            this.mLastStepCpuSystemTimeMs = 0L;
            this.mCurStepStatUserTimeMs = 0L;
            this.mLastStepStatUserTimeMs = 0L;
            this.mCurStepStatSystemTimeMs = 0L;
            this.mLastStepStatSystemTimeMs = 0L;
            this.mCurStepStatIOWaitTimeMs = 0L;
            this.mLastStepStatIOWaitTimeMs = 0L;
            this.mCurStepStatIrqTimeMs = 0L;
            this.mLastStepStatIrqTimeMs = 0L;
            this.mCurStepStatSoftIrqTimeMs = 0L;
            this.mLastStepStatSoftIrqTimeMs = 0L;
            this.mCurStepStatIdleTimeMs = 0L;
            this.mLastStepStatIdleTimeMs = 0L;
        }
    }

    @GuardedBy({"this"})
    public void commitCurrentHistoryBatchLocked() {
        this.mHistory.commitCurrentHistoryBatchLocked();
    }

    @GuardedBy({"this"})
    public void createFakeHistoryEvents(long j) {
        long elapsedRealtime = this.mClock.elapsedRealtime();
        long uptimeMillis = this.mClock.uptimeMillis();
        for (long j2 = 0; j2 < j; j2++) {
            noteLongPartialWakelockStart("name1", "historyName1", 1000, elapsedRealtime, uptimeMillis);
            noteLongPartialWakelockFinish("name1", "historyName1", 1000, elapsedRealtime, uptimeMillis);
        }
    }

    @GuardedBy({"this"})
    public void recordHistoryEventLocked(long j, long j2, int i, String str, int i2) {
        this.mHistory.recordEvent(j, j2, i, str, i2);
    }

    @GuardedBy({"this"})
    public void updateTimeBasesLocked(boolean z, int i, long j, long j2) {
        boolean z2 = !Display.isOnState(i);
        boolean z3 = z != this.mOnBatteryTimeBase.isRunning();
        boolean z4 = (z && z2) != this.mOnBatteryScreenOffTimeBase.isRunning();
        if (z4 || z3) {
            if (z4) {
                updateKernelWakelocksLocked(j2);
                updateBatteryPropertiesLocked();
            }
            if (z3) {
                updateRpmStatsLocked(j2);
            }
            this.mOnBatteryTimeBase.setRunning(z, j, j2);
            if (z3) {
                for (int size = this.mUidStats.size() - 1; size >= 0; size--) {
                    this.mUidStats.valueAt(size).updateOnBatteryBgTimeBase(j, j2);
                }
            }
            if (z4) {
                this.mOnBatteryScreenOffTimeBase.setRunning(z && z2, j, j2);
                for (int size2 = this.mUidStats.size() - 1; size2 >= 0; size2--) {
                    this.mUidStats.valueAt(size2).updateOnBatteryScreenOffBgTimeBase(j, j2);
                }
            }
        }
    }

    @GuardedBy({"this"})
    public final void updateBatteryPropertiesLocked() {
        try {
            IBatteryPropertiesRegistrar asInterface = IBatteryPropertiesRegistrar.Stub.asInterface(ServiceManager.getService("batteryproperties"));
            if (asInterface != null) {
                asInterface.scheduleUpdate();
            }
        } catch (RemoteException unused) {
        }
    }

    @GuardedBy({"this"})
    public void addIsolatedUidLocked(int i, int i2, long j, long j2) {
        this.mIsolatedUids.put(i, i2);
        this.mIsolatedUidRefCounts.put(i, 1);
        getUidStatsLocked(i2, j, j2).addIsolatedUid(i);
    }

    public void scheduleRemoveIsolatedUidLocked(int i, int i2) {
        ExternalStatsSync externalStatsSync;
        if (this.mIsolatedUids.get(i, -1) != i2 || (externalStatsSync = this.mExternalSync) == null) {
            return;
        }
        externalStatsSync.scheduleCpuSyncDueToRemovedUid(i);
    }

    @GuardedBy({"this"})
    public boolean maybeRemoveIsolatedUidLocked(int i, long j, long j2) {
        int i2 = this.mIsolatedUidRefCounts.get(i, 0) - 1;
        if (i2 > 0) {
            this.mIsolatedUidRefCounts.put(i, i2);
            return false;
        }
        int indexOfKey = this.mIsolatedUids.indexOfKey(i);
        if (indexOfKey >= 0) {
            getUidStatsLocked(this.mIsolatedUids.valueAt(indexOfKey), j, j2).removeIsolatedUid(i);
            this.mIsolatedUids.removeAt(indexOfKey);
            this.mIsolatedUidRefCounts.delete(i);
        } else {
            Slog.w("BatteryStatsImpl", "Attempted to remove untracked isolated uid (" + i + ")");
        }
        this.mPendingRemovedUids.add(new UidToRemove(this, i, j));
        return true;
    }

    public void incrementIsolatedUidRefCount(int i) {
        int i2 = this.mIsolatedUidRefCounts.get(i, 0);
        if (i2 <= 0) {
            Slog.w("BatteryStatsImpl", "Attempted to increment ref counted of untracked isolated uid (" + i + ")");
            return;
        }
        this.mIsolatedUidRefCounts.put(i, i2 + 1);
    }

    public final int mapUid(int i) {
        if (Process.isSdkSandboxUid(i)) {
            return Process.getAppUidForSdkSandboxUid(i);
        }
        return mapIsolatedUid(i);
    }

    public final int mapIsolatedUid(int i) {
        return this.mIsolatedUids.get(i, i);
    }

    @GuardedBy({"this"})
    public void noteEventLocked(int i, String str, int i2, long j, long j2) {
        int mapUid = mapUid(i2);
        if (this.mActiveEvents.updateState(i, str, mapUid, 0)) {
            this.mHistory.recordEvent(j, j2, i, str, mapUid);
        }
    }

    @GuardedBy({"this"})
    public void noteCurrentTimeChangedLocked(long j, long j2, long j3) {
        this.mHistory.recordCurrentTimeChange(j2, j3, j);
    }

    @GuardedBy({"this"})
    public void noteProcessStartLocked(String str, int i, long j, long j2) {
        int mapUid = mapUid(i);
        if (isOnBattery()) {
            getUidStatsLocked(mapUid, j, j2).getProcessStatsLocked(str).incStartsLocked();
        }
        if (this.mActiveEvents.updateState(32769, str, mapUid, 0) && this.mRecordAllHistory) {
            this.mHistory.recordEvent(j, j2, 32769, str, mapUid);
        }
    }

    @GuardedBy({"this"})
    public void noteProcessCrashLocked(String str, int i, long j, long j2) {
        int mapUid = mapUid(i);
        if (isOnBattery()) {
            getUidStatsLocked(mapUid, j, j2).getProcessStatsLocked(str).incNumCrashesLocked();
        }
    }

    @GuardedBy({"this"})
    public void noteProcessAnrLocked(String str, int i, long j, long j2) {
        int mapUid = mapUid(i);
        if (isOnBattery()) {
            getUidStatsLocked(mapUid, j, j2).getProcessStatsLocked(str).incNumAnrsLocked();
        }
    }

    @GuardedBy({"this"})
    public void noteUidProcessStateLocked(int i, int i2, long j, long j2) {
        int mapUid = mapUid(i);
        if (i == mapUid || !Process.isIsolated(i)) {
            FrameworkStatsLog.write(27, i, ActivityManager.processStateAmToProto(i2));
            getUidStatsLocked(mapUid, j, j2).updateUidProcessStateLocked(i2, j, j2);
        }
    }

    @GuardedBy({"this"})
    public void noteProcessFinishLocked(String str, int i, long j, long j2) {
        int mapUid = mapUid(i);
        if (this.mActiveEvents.updateState(16385, str, mapUid, 0) && this.mRecordAllHistory) {
            this.mHistory.recordEvent(j, j2, 16385, str, mapUid);
        }
    }

    @GuardedBy({"this"})
    public void noteSyncStartLocked(String str, int i, long j, long j2) {
        int mapUid = mapUid(i);
        getUidStatsLocked(mapUid, j, j2).noteStartSyncLocked(str, j);
        if (this.mActiveEvents.updateState(32772, str, mapUid, 0)) {
            this.mHistory.recordEvent(j, j2, 32772, str, mapUid);
        }
    }

    @GuardedBy({"this"})
    public void noteSyncFinishLocked(String str, int i, long j, long j2) {
        int mapUid = mapUid(i);
        getUidStatsLocked(mapUid, j, j2).noteStopSyncLocked(str, j);
        if (this.mActiveEvents.updateState(16388, str, mapUid, 0)) {
            this.mHistory.recordEvent(j, j2, 16388, str, mapUid);
        }
    }

    @GuardedBy({"this"})
    public void noteJobStartLocked(String str, int i, long j, long j2) {
        int mapUid = mapUid(i);
        getUidStatsLocked(mapUid, j, j2).noteStartJobLocked(str, j);
        if (this.mActiveEvents.updateState(32774, str, mapUid, 0)) {
            this.mHistory.recordEvent(j, j2, 32774, str, mapUid);
        }
    }

    @GuardedBy({"this"})
    public void noteJobFinishLocked(String str, int i, int i2, long j, long j2) {
        int mapUid = mapUid(i);
        getUidStatsLocked(mapUid, j, j2).noteStopJobLocked(str, j, i2);
        if (this.mActiveEvents.updateState(16390, str, mapUid, 0)) {
            this.mHistory.recordEvent(j, j2, 16390, str, mapUid);
        }
    }

    @GuardedBy({"this"})
    public void noteJobsDeferredLocked(int i, int i2, long j, long j2, long j3) {
        getUidStatsLocked(mapUid(i), j2, j3).noteJobsDeferredLocked(i2, j);
    }

    @GuardedBy({"this"})
    public void noteAlarmStartLocked(String str, WorkSource workSource, int i, long j, long j2) {
        noteAlarmStartOrFinishLocked(32781, str, workSource, i, j, j2);
    }

    @GuardedBy({"this"})
    public void noteAlarmFinishLocked(String str, WorkSource workSource, int i, long j, long j2) {
        noteAlarmStartOrFinishLocked(16397, str, workSource, i, j, j2);
    }

    @GuardedBy({"this"})
    public final void noteAlarmStartOrFinishLocked(int i, String str, WorkSource workSource, int i2, long j, long j2) {
        if (this.mRecordAllHistory) {
            if (workSource != null) {
                for (int i3 = 0; i3 < workSource.size(); i3++) {
                    int mapUid = mapUid(workSource.getUid(i3));
                    if (this.mActiveEvents.updateState(i, str, mapUid, 0)) {
                        this.mHistory.recordEvent(j, j2, i, str, mapUid);
                    }
                }
                List workChains = workSource.getWorkChains();
                if (workChains != null) {
                    for (int i4 = 0; i4 < workChains.size(); i4++) {
                        int mapUid2 = mapUid(((WorkSource.WorkChain) workChains.get(i4)).getAttributionUid());
                        if (this.mActiveEvents.updateState(i, str, mapUid2, 0)) {
                            this.mHistory.recordEvent(j, j2, i, str, mapUid2);
                        }
                    }
                    return;
                }
                return;
            }
            int mapUid3 = mapUid(i2);
            if (this.mActiveEvents.updateState(i, str, mapUid3, 0)) {
                this.mHistory.recordEvent(j, j2, i, str, mapUid3);
            }
        }
    }

    @GuardedBy({"this"})
    public void noteWakupAlarmLocked(String str, int i, WorkSource workSource, String str2, long j, long j2) {
        if (workSource != null) {
            for (int i2 = 0; i2 < workSource.size(); i2++) {
                int uid = workSource.getUid(i2);
                String packageName = workSource.getPackageName(i2);
                if (isOnBattery()) {
                    getPackageStatsLocked(uid, packageName != null ? packageName : str, j, j2).noteWakeupAlarmLocked(str2);
                }
            }
            List workChains = workSource.getWorkChains();
            if (workChains != null) {
                for (int i3 = 0; i3 < workChains.size(); i3++) {
                    int attributionUid = ((WorkSource.WorkChain) workChains.get(i3)).getAttributionUid();
                    if (isOnBattery()) {
                        getPackageStatsLocked(attributionUid, str, j, j2).noteWakeupAlarmLocked(str2);
                    }
                }
            }
        } else if (isOnBattery()) {
            getPackageStatsLocked(i, str, j, j2).noteWakeupAlarmLocked(str2);
        }
    }

    public final void requestWakelockCpuUpdate() {
        this.mExternalSync.scheduleCpuSyncDueToWakelockChange(60000L);
    }

    public final void requestImmediateCpuUpdate() {
        this.mExternalSync.scheduleCpuSyncDueToWakelockChange(0L);
    }

    @GuardedBy({"this"})
    public void setRecordAllHistoryLocked(boolean z) {
        this.mRecordAllHistory = z;
        if (!z) {
            this.mActiveEvents.removeEvents(5);
            this.mActiveEvents.removeEvents(13);
            HashMap stateForEvent = this.mActiveEvents.getStateForEvent(1);
            if (stateForEvent != null) {
                long elapsedRealtime = this.mClock.elapsedRealtime();
                long uptimeMillis = this.mClock.uptimeMillis();
                for (Map.Entry entry : stateForEvent.entrySet()) {
                    int i = 0;
                    for (SparseIntArray sparseIntArray = (SparseIntArray) entry.getValue(); i < sparseIntArray.size(); sparseIntArray = sparseIntArray) {
                        this.mHistory.recordEvent(elapsedRealtime, uptimeMillis, 16385, (String) entry.getKey(), sparseIntArray.keyAt(i));
                        i++;
                    }
                }
                return;
            }
            return;
        }
        HashMap stateForEvent2 = this.mActiveEvents.getStateForEvent(1);
        if (stateForEvent2 != null) {
            long elapsedRealtime2 = this.mClock.elapsedRealtime();
            long uptimeMillis2 = this.mClock.uptimeMillis();
            for (Map.Entry entry2 : stateForEvent2.entrySet()) {
                int i2 = 0;
                for (SparseIntArray sparseIntArray2 = (SparseIntArray) entry2.getValue(); i2 < sparseIntArray2.size(); sparseIntArray2 = sparseIntArray2) {
                    this.mHistory.recordEvent(elapsedRealtime2, uptimeMillis2, 32769, (String) entry2.getKey(), sparseIntArray2.keyAt(i2));
                    i2++;
                }
            }
        }
    }

    public void setNoAutoReset(boolean z) {
        this.mNoAutoReset = z;
    }

    @GuardedBy({"this"})
    public void setPretendScreenOff(boolean z) {
        if (this.mPretendScreenOff != z) {
            this.mPretendScreenOff = z;
            noteScreenStateLocked(0, this.mPerDisplayBatteryStats[0].screenState, this.mClock.elapsedRealtime(), this.mClock.uptimeMillis(), this.mClock.currentTimeMillis());
        }
    }

    @GuardedBy({"this"})
    public void noteStartWakeLocked(int i, int i2, WorkSource.WorkChain workChain, String str, String str2, int i3, boolean z, long j, long j2) {
        int mapUid = mapUid(i);
        if (i3 == 0) {
            aggregateLastWakeupUptimeLocked(j, j2);
            String str3 = str2 == null ? str : str2;
            if (this.mRecordAllHistory && this.mActiveEvents.updateState(32773, str3, mapUid, 0)) {
                this.mHistory.recordEvent(j, j2, 32773, str3, mapUid);
            }
            if (this.mWakeLockNesting == 0) {
                this.mWakeLockImportant = !z;
                this.mHistory.recordWakelockStartEvent(j, j2, str3, mapUid);
            } else if (!this.mWakeLockImportant && !z && this.mHistory.maybeUpdateWakelockTag(j, j2, str3, mapUid)) {
                this.mWakeLockImportant = true;
            }
            this.mWakeLockNesting++;
        }
        if (mapUid >= 0) {
            if (mapUid != i) {
                incrementIsolatedUidRefCount(i);
            }
            if (this.mOnBatteryScreenOffTimeBase.isRunning()) {
                requestWakelockCpuUpdate();
            }
            getUidStatsLocked(mapUid, j, j2).noteStartWakeLocked(i2, str, i3, j);
            if (workChain != null) {
                FrameworkStatsLog.write(10, workChain.getUids(), workChain.getTags(), getPowerManagerWakeLockLevel(i3), str, 1);
            } else {
                FrameworkStatsLog.write_non_chained(10, mapIsolatedUid(i), (String) null, getPowerManagerWakeLockLevel(i3), str, 1);
            }
        }
    }

    @GuardedBy({"this"})
    public void noteStopWakeLocked(int i, int i2, WorkSource.WorkChain workChain, String str, String str2, int i3, long j, long j2) {
        int mapUid = mapUid(i);
        if (i3 == 0) {
            this.mWakeLockNesting--;
            String str3 = str2 == null ? str : str2;
            if (this.mRecordAllHistory && this.mActiveEvents.updateState(16389, str3, mapUid, 0)) {
                this.mHistory.recordEvent(j, j2, 16389, str3, mapUid);
            }
            if (this.mWakeLockNesting == 0) {
                this.mHistory.recordWakelockStopEvent(j, j2, str3, mapUid);
            }
        }
        if (mapUid >= 0) {
            if (this.mOnBatteryScreenOffTimeBase.isRunning()) {
                requestWakelockCpuUpdate();
            }
            getUidStatsLocked(mapUid, j, j2).noteStopWakeLocked(i2, str, i3, j);
            if (workChain != null) {
                FrameworkStatsLog.write(10, workChain.getUids(), workChain.getTags(), getPowerManagerWakeLockLevel(i3), str, 0);
            } else {
                FrameworkStatsLog.write_non_chained(10, mapIsolatedUid(i), (String) null, getPowerManagerWakeLockLevel(i3), str, 0);
            }
            if (mapUid != i) {
                maybeRemoveIsolatedUidLocked(i, j, j2);
            }
        }
    }

    public final int getPowerManagerWakeLockLevel(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i == 2) {
                    Slog.e("BatteryStatsImpl", "Illegal window wakelock type observed in batterystats.");
                    return -1;
                } else if (i != 18) {
                    Slog.e("BatteryStatsImpl", "Illegal wakelock type in batterystats: " + i);
                    return -1;
                } else {
                    return 128;
                }
            }
            return 26;
        }
        return 1;
    }

    @GuardedBy({"this"})
    public void noteStartWakeFromSourceLocked(WorkSource workSource, int i, String str, String str2, int i2, boolean z, long j, long j2) {
        int size = workSource.size();
        for (int i3 = 0; i3 < size; i3++) {
            noteStartWakeLocked(workSource.getUid(i3), i, null, str, str2, i2, z, j, j2);
        }
        List workChains = workSource.getWorkChains();
        if (workChains != null) {
            for (int i4 = 0; i4 < workChains.size(); i4++) {
                WorkSource.WorkChain workChain = (WorkSource.WorkChain) workChains.get(i4);
                noteStartWakeLocked(workChain.getAttributionUid(), i, workChain, str, str2, i2, z, j, j2);
            }
        }
    }

    @GuardedBy({"this"})
    public void noteChangeWakelockFromSourceLocked(WorkSource workSource, int i, String str, String str2, int i2, WorkSource workSource2, int i3, String str3, String str4, int i4, boolean z, long j, long j2) {
        ArrayList arrayList;
        ArrayList arrayList2;
        ArrayList[] diffChains = WorkSource.diffChains(workSource, workSource2);
        int size = workSource2.size();
        for (int i5 = 0; i5 < size; i5++) {
            noteStartWakeLocked(workSource2.getUid(i5), i3, null, str3, str4, i4, z, j, j2);
        }
        if (diffChains != null && (arrayList2 = diffChains[0]) != null) {
            for (int i6 = 0; i6 < arrayList2.size(); i6++) {
                WorkSource.WorkChain workChain = (WorkSource.WorkChain) arrayList2.get(i6);
                noteStartWakeLocked(workChain.getAttributionUid(), i3, workChain, str3, str4, i4, z, j, j2);
            }
        }
        int size2 = workSource.size();
        for (int i7 = 0; i7 < size2; i7++) {
            noteStopWakeLocked(workSource.getUid(i7), i, null, str, str2, i2, j, j2);
        }
        if (diffChains == null || (arrayList = diffChains[1]) == null) {
            return;
        }
        for (int i8 = 0; i8 < arrayList.size(); i8++) {
            WorkSource.WorkChain workChain2 = (WorkSource.WorkChain) arrayList.get(i8);
            noteStopWakeLocked(workChain2.getAttributionUid(), i, workChain2, str, str2, i2, j, j2);
        }
    }

    @GuardedBy({"this"})
    public void noteStopWakeFromSourceLocked(WorkSource workSource, int i, String str, String str2, int i2, long j, long j2) {
        int size = workSource.size();
        for (int i3 = 0; i3 < size; i3++) {
            noteStopWakeLocked(workSource.getUid(i3), i, null, str, str2, i2, j, j2);
        }
        List workChains = workSource.getWorkChains();
        if (workChains != null) {
            for (int i4 = 0; i4 < workChains.size(); i4++) {
                WorkSource.WorkChain workChain = (WorkSource.WorkChain) workChains.get(i4);
                noteStopWakeLocked(workChain.getAttributionUid(), i, workChain, str, str2, i2, j, j2);
            }
        }
    }

    @GuardedBy({"this"})
    public void noteLongPartialWakelockStart(String str, String str2, int i, long j, long j2) {
        noteLongPartialWakeLockStartInternal(str, str2, i, j, j2);
    }

    @GuardedBy({"this"})
    public void noteLongPartialWakelockStartFromSource(String str, String str2, WorkSource workSource, long j, long j2) {
        int size = workSource.size();
        for (int i = 0; i < size; i++) {
            noteLongPartialWakeLockStartInternal(str, str2, mapUid(workSource.getUid(i)), j, j2);
        }
        List workChains = workSource.getWorkChains();
        if (workChains != null) {
            for (int i2 = 0; i2 < workChains.size(); i2++) {
                noteLongPartialWakeLockStartInternal(str, str2, ((WorkSource.WorkChain) workChains.get(i2)).getAttributionUid(), j, j2);
            }
        }
    }

    @GuardedBy({"this"})
    public final void noteLongPartialWakeLockStartInternal(String str, String str2, int i, long j, long j2) {
        int mapUid = mapUid(i);
        String str3 = str2 == null ? str : str2;
        if (this.mActiveEvents.updateState(32788, str3, mapUid, 0)) {
            this.mHistory.recordEvent(j, j2, 32788, str3, mapUid);
            if (mapUid != i) {
                incrementIsolatedUidRefCount(i);
            }
        }
    }

    @GuardedBy({"this"})
    public void noteLongPartialWakelockFinish(String str, String str2, int i, long j, long j2) {
        noteLongPartialWakeLockFinishInternal(str, str2, i, j, j2);
    }

    @GuardedBy({"this"})
    public void noteLongPartialWakelockFinishFromSource(String str, String str2, WorkSource workSource, long j, long j2) {
        int size = workSource.size();
        for (int i = 0; i < size; i++) {
            noteLongPartialWakeLockFinishInternal(str, str2, mapUid(workSource.getUid(i)), j, j2);
        }
        List workChains = workSource.getWorkChains();
        if (workChains != null) {
            for (int i2 = 0; i2 < workChains.size(); i2++) {
                noteLongPartialWakeLockFinishInternal(str, str2, ((WorkSource.WorkChain) workChains.get(i2)).getAttributionUid(), j, j2);
            }
        }
    }

    @GuardedBy({"this"})
    public final void noteLongPartialWakeLockFinishInternal(String str, String str2, int i, long j, long j2) {
        int mapUid = mapUid(i);
        String str3 = str2 == null ? str : str2;
        if (this.mActiveEvents.updateState(16404, str3, mapUid, 0)) {
            this.mHistory.recordEvent(j, j2, 16404, str3, mapUid);
            if (mapUid != i) {
                maybeRemoveIsolatedUidLocked(i, j, j2);
            }
        }
    }

    @GuardedBy({"this"})
    public void aggregateLastWakeupUptimeLocked(long j, long j2) {
        String str = this.mLastWakeupReason;
        if (str != null) {
            long j3 = (j2 - this.mLastWakeupUptimeMs) * 1000;
            getWakeupReasonTimerLocked(str).add(j3, 1, j);
            FrameworkStatsLog.write(36, this.mLastWakeupReason, j3);
            this.mLastWakeupReason = null;
        }
    }

    @GuardedBy({"this"})
    public void noteWakeupReasonLocked(String str, long j, long j2) {
        aggregateLastWakeupUptimeLocked(j, j2);
        this.mHistory.recordWakeupEvent(j, j2, str);
        this.mLastWakeupReason = str;
        this.mLastWakeupUptimeMs = j2;
    }

    @GuardedBy({"this"})
    public boolean startAddingCpuStatsLocked() {
        this.mExternalSync.cancelCpuSyncDueToWakelockChange();
        return this.mOnBatteryInternal;
    }

    @GuardedBy({"this"})
    public void addCpuStatsLocked(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        this.mStepDetailsCalculator.addCpuStats(i, i2, i3, i4, i5, i6, i7, i8);
    }

    @GuardedBy({"this"})
    public void finishAddingCpuStatsLocked() {
        this.mStepDetailsCalculator.finishAddingCpuLocked();
    }

    public void noteProcessDiedLocked(int i, int i2) {
        Uid uid = this.mUidStats.get(mapUid(i));
        if (uid != null) {
            uid.mPids.remove(i2);
        }
    }

    public void reportExcessiveCpuLocked(int i, String str, long j, long j2) {
        Uid uid = this.mUidStats.get(mapUid(i));
        if (uid != null) {
            uid.reportExcessiveCpuLocked(str, j, j2);
        }
    }

    @GuardedBy({"this"})
    public void noteStartSensorLocked(int i, int i2, long j, long j2) {
        int mapUid = mapUid(i);
        if (this.mSensorNesting == 0) {
            this.mHistory.recordStateStartEvent(j, j2, 8388608);
        }
        this.mSensorNesting++;
        getUidStatsLocked(mapUid, j, j2).noteStartSensor(i2, j);
    }

    @GuardedBy({"this"})
    public void noteStopSensorLocked(int i, int i2, long j, long j2) {
        int mapUid = mapUid(i);
        int i3 = this.mSensorNesting - 1;
        this.mSensorNesting = i3;
        if (i3 == 0) {
            this.mHistory.recordStateStopEvent(j, j2, 8388608);
        }
        getUidStatsLocked(mapUid, j, j2).noteStopSensor(i2, j);
    }

    @GuardedBy({"this"})
    public void noteGpsChangedLocked(WorkSource workSource, WorkSource workSource2, long j, long j2) {
        for (int i = 0; i < workSource2.size(); i++) {
            noteStartGpsLocked(workSource2.getUid(i), null, j, j2);
        }
        for (int i2 = 0; i2 < workSource.size(); i2++) {
            noteStopGpsLocked(workSource.getUid(i2), null, j, j2);
        }
        ArrayList[] diffChains = WorkSource.diffChains(workSource, workSource2);
        if (diffChains != null) {
            ArrayList arrayList = diffChains[0];
            if (arrayList != null) {
                for (int i3 = 0; i3 < arrayList.size(); i3++) {
                    noteStartGpsLocked(-1, (WorkSource.WorkChain) arrayList.get(i3), j, j2);
                }
            }
            ArrayList arrayList2 = diffChains[1];
            if (arrayList2 != null) {
                for (int i4 = 0; i4 < arrayList2.size(); i4++) {
                    noteStopGpsLocked(-1, (WorkSource.WorkChain) arrayList2.get(i4), j, j2);
                }
            }
        }
    }

    @GuardedBy({"this"})
    public final void noteStartGpsLocked(int i, WorkSource.WorkChain workChain, long j, long j2) {
        if (workChain != null) {
            i = workChain.getAttributionUid();
        }
        int mapUid = mapUid(i);
        if (this.mGpsNesting == 0) {
            this.mHistory.recordStateStartEvent(j, j2, 536870912);
        }
        this.mGpsNesting++;
        if (workChain == null) {
            FrameworkStatsLog.write_non_chained(6, mapIsolatedUid(i), null, 1);
        } else {
            FrameworkStatsLog.write(6, workChain.getUids(), workChain.getTags(), 1);
        }
        getUidStatsLocked(mapUid, j, j2).noteStartGps(j);
    }

    @GuardedBy({"this"})
    public final void noteStopGpsLocked(int i, WorkSource.WorkChain workChain, long j, long j2) {
        if (workChain != null) {
            i = workChain.getAttributionUid();
        }
        int mapUid = mapUid(i);
        int i2 = this.mGpsNesting - 1;
        this.mGpsNesting = i2;
        if (i2 == 0) {
            this.mHistory.recordStateStopEvent(j, j2, 536870912);
            stopAllGpsSignalQualityTimersLocked(-1, j);
            this.mGpsSignalQualityBin = -1;
        }
        if (workChain == null) {
            FrameworkStatsLog.write_non_chained(6, mapIsolatedUid(i), null, 0);
        } else {
            FrameworkStatsLog.write(6, workChain.getUids(), workChain.getTags(), 0);
        }
        getUidStatsLocked(mapUid, j, j2).noteStopGps(j);
    }

    @GuardedBy({"this"})
    public void noteGpsSignalQualityLocked(int i, long j, long j2) {
        if (this.mGpsNesting == 0) {
            return;
        }
        if (i >= 0) {
            StopwatchTimer[] stopwatchTimerArr = this.mGpsSignalQualityTimer;
            if (i < stopwatchTimerArr.length) {
                int i2 = this.mGpsSignalQualityBin;
                if (i2 != i) {
                    if (i2 >= 0) {
                        stopwatchTimerArr[i2].stopRunningLocked(j);
                    }
                    if (!this.mGpsSignalQualityTimer[i].isRunningLocked()) {
                        this.mGpsSignalQualityTimer[i].startRunningLocked(j);
                    }
                    this.mHistory.recordGpsSignalQualityEvent(j, j2, i);
                    this.mGpsSignalQualityBin = i;
                    return;
                }
                return;
            }
        }
        stopAllGpsSignalQualityTimersLocked(-1, j);
    }

    /* JADX WARN: Removed duplicated region for block: B:44:0x00ab A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:46:0x00af  */
    /* JADX WARN: Removed duplicated region for block: B:55:0x00e2  */
    /* JADX WARN: Removed duplicated region for block: B:61:0x00f9  */
    /* JADX WARN: Removed duplicated region for block: B:70:0x010d  */
    @GuardedBy({"this"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void noteScreenStateLocked(int i, int i2, long j, long j2, long j3) {
        boolean z;
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        int i8;
        EnergyConsumerStats energyConsumerStats;
        int i9;
        int i10;
        int i11;
        boolean z2;
        int i12;
        int i13 = i2;
        if (i13 > 4) {
            if (Display.isOnState(i2)) {
                i13 = 2;
            } else if (Display.isDozeState(i2)) {
                i13 = Display.isSuspendedState(i2) ? 4 : 3;
            } else if (Display.isOffState(i2)) {
                i13 = 1;
            } else {
                Slog.wtf("BatteryStatsImpl", "Unknown screen state (not mapped): " + i13);
                i13 = 0;
            }
        }
        int i14 = this.mScreenBrightnessBin;
        DisplayBatteryStats[] displayBatteryStatsArr = this.mPerDisplayBatteryStats;
        int length = displayBatteryStatsArr.length;
        if (i < 0 || i >= length) {
            Slog.wtf("BatteryStatsImpl", "Unexpected note screen state for display " + i + " (only " + this.mPerDisplayBatteryStats.length + " displays exist...)");
            return;
        }
        DisplayBatteryStats displayBatteryStats = displayBatteryStatsArr[i];
        int i15 = displayBatteryStats.screenState;
        if (i15 == i13) {
            i7 = this.mScreenState;
            i8 = i14;
            i6 = 0;
            z = false;
            i3 = 0;
        } else {
            displayBatteryStats.screenState = i13;
            if (i15 != 0 && i15 != 1) {
                if (i15 != 2) {
                    if (i15 != 3) {
                        if (i15 != 4) {
                            Slog.wtf("BatteryStatsImpl", "Attempted to stop timer for unexpected display state " + i);
                        } else if (i13 != 3) {
                            displayBatteryStats.screenDozeTimer.stopRunningLocked(j);
                        }
                    } else if (i13 != 4) {
                        displayBatteryStats.screenDozeTimer.stopRunningLocked(j);
                    }
                    if (i13 != 0 && i13 != 1) {
                        if (i13 != 2) {
                            displayBatteryStats.screenOnTimer.startRunningLocked(j);
                            int i16 = displayBatteryStats.screenBrightnessBin;
                            if (i16 >= 0) {
                                displayBatteryStats.screenBrightnessTimers[i16].startRunningLocked(j);
                            }
                            i14 = evaluateOverallScreenBrightnessBinLocked();
                        } else if (i13 != 3) {
                            if (i13 != 4) {
                                Slog.wtf("BatteryStatsImpl", "Attempted to start timer for unexpected display state " + i13 + " for display " + i);
                            } else if (i15 != 3) {
                                displayBatteryStats.screenDozeTimer.startRunningLocked(j);
                            }
                        } else if (i15 != 4) {
                            displayBatteryStats.screenDozeTimer.startRunningLocked(j);
                        }
                        z = true;
                    }
                    if (z || (energyConsumerStats = this.mGlobalEnergyConsumerStats) == null) {
                        i3 = 0;
                    } else {
                        i3 = 0;
                        if (energyConsumerStats.isStandardBucketSupported(0)) {
                            i4 = 32;
                            int i17 = i3;
                            for (i5 = i17; i5 < length; i5++) {
                                int i18 = this.mPerDisplayBatteryStats[i5].screenState;
                                if (i18 == 2 || i17 == 2) {
                                    i17 = 2;
                                } else if (i18 == 3 || i17 == 3) {
                                    i17 = 3;
                                } else if (i18 == 4 || i17 == 4) {
                                    i17 = 4;
                                } else if (i18 == 1 || i17 == 1) {
                                    i17 = 1;
                                }
                            }
                            i6 = i4;
                            i7 = i17;
                            i8 = i14;
                        }
                    }
                    i4 = i3;
                    int i172 = i3;
                    while (i5 < length) {
                    }
                    i6 = i4;
                    i7 = i172;
                    i8 = i14;
                } else {
                    displayBatteryStats.screenOnTimer.stopRunningLocked(j);
                    int i19 = displayBatteryStats.screenBrightnessBin;
                    if (i19 >= 0) {
                        displayBatteryStats.screenBrightnessTimers[i19].stopRunningLocked(j);
                    }
                    i14 = evaluateOverallScreenBrightnessBinLocked();
                }
                z = true;
                if (i13 != 0) {
                    if (i13 != 2) {
                    }
                    z = true;
                }
                if (z) {
                }
                i3 = 0;
                i4 = i3;
                int i1722 = i3;
                while (i5 < length) {
                }
                i6 = i4;
                i7 = i1722;
                i8 = i14;
            }
            z = false;
            if (i13 != 0) {
            }
            if (z) {
            }
            i3 = 0;
            i4 = i3;
            int i17222 = i3;
            while (i5 < length) {
            }
            i6 = i4;
            i7 = i17222;
            i8 = i14;
        }
        boolean isRunning = this.mOnBatteryTimeBase.isRunning();
        boolean isRunning2 = this.mOnBatteryScreenOffTimeBase.isRunning();
        int i20 = this.mPretendScreenOff ? 1 : i7;
        if (this.mScreenState != i20) {
            recordDailyStatsIfNeededLocked(true, j3);
            int i21 = this.mScreenState;
            this.mScreenState = i20;
            if (i20 != 0) {
                int i22 = i20 - 1;
                if ((i22 & 3) == i22) {
                    int i23 = this.mModStepMode;
                    int i24 = this.mCurStepMode;
                    this.mModStepMode = i23 | ((i24 & 3) ^ i22);
                    this.mCurStepMode = i22 | (i24 & (-4));
                } else {
                    Slog.wtf("BatteryStatsImpl", "Unexpected screen state: " + i20);
                }
            }
            int i25 = 262144;
            if (Display.isDozeState(i20) && !Display.isDozeState(i21)) {
                this.mScreenDozeTimer.startRunningLocked(j);
                i10 = i3;
            } else if (!Display.isDozeState(i21) || Display.isDozeState(i20)) {
                i10 = i3;
                i25 = i10;
            } else {
                this.mScreenDozeTimer.stopRunningLocked(j);
                i10 = 262144;
                i25 = i3;
            }
            if (Display.isOnState(i20)) {
                i25 |= 1048576;
                this.mScreenOnTimer.startRunningLocked(j);
                int i26 = this.mScreenBrightnessBin;
                if (i26 >= 0) {
                    this.mScreenBrightnessTimer[i26].startRunningLocked(j);
                }
            } else if (Display.isOnState(i21)) {
                i10 |= 1048576;
                this.mScreenOnTimer.stopRunningLocked(j);
                int i27 = this.mScreenBrightnessBin;
                if (i27 >= 0) {
                    this.mScreenBrightnessTimer[i27].stopRunningLocked(j);
                }
            }
            int i28 = i10;
            int i29 = i25;
            if (i29 != 0 || i28 != 0) {
                this.mHistory.recordStateChangeEvent(j, j2, i29, i28);
            }
            int i30 = i6 | 1;
            if (Display.isOnState(i20)) {
                updateTimeBasesLocked(this.mOnBatteryTimeBase.isRunning(), i20, j2 * 1000, j * 1000);
                i11 = i20;
                z2 = true;
                i12 = i21;
                noteStartWakeLocked(-1, -1, null, "screen", null, 0, false, j, j2);
            } else {
                i11 = i20;
                z2 = true;
                i12 = i21;
                if (Display.isOnState(i12)) {
                    noteStopWakeLocked(-1, -1, null, "screen", "screen", 0, j, j2);
                    updateTimeBasesLocked(this.mOnBatteryTimeBase.isRunning(), i11, j2 * 1000, j * 1000);
                }
            }
            if (this.mOnBatteryInternal) {
                i9 = i11;
                updateDischargeScreenLevelsLocked(i12, i9);
            } else {
                i9 = i11;
            }
            i6 = i30;
            z = z2;
        } else {
            i9 = i20;
        }
        maybeUpdateOverallScreenBrightness(i8, j, j2);
        if (z) {
            int length2 = this.mPerDisplayBatteryStats.length;
            int[] iArr = new int[length2];
            while (i3 < length2) {
                iArr[i3] = this.mPerDisplayBatteryStats[i3].screenState;
                i3++;
            }
            this.mExternalSync.scheduleSyncDueToScreenStateChange(i6, isRunning, isRunning2, i9, iArr);
        }
    }

    @GuardedBy({"this"})
    public void noteScreenBrightnessLocked(int i, int i2, long j, long j2) {
        int evaluateOverallScreenBrightnessBinLocked;
        int i3 = i2 / 51;
        if (i3 < 0) {
            i3 = 0;
        } else if (i3 >= 5) {
            i3 = 4;
        }
        DisplayBatteryStats[] displayBatteryStatsArr = this.mPerDisplayBatteryStats;
        int length = displayBatteryStatsArr.length;
        if (i < 0 || i >= length) {
            Slog.wtf("BatteryStatsImpl", "Unexpected note screen brightness for display " + i + " (only " + this.mPerDisplayBatteryStats.length + " displays exist...)");
            return;
        }
        DisplayBatteryStats displayBatteryStats = displayBatteryStatsArr[i];
        int i4 = displayBatteryStats.screenBrightnessBin;
        if (i4 == i3) {
            evaluateOverallScreenBrightnessBinLocked = this.mScreenBrightnessBin;
        } else {
            displayBatteryStats.screenBrightnessBin = i3;
            if (displayBatteryStats.screenState == 2) {
                if (i4 >= 0) {
                    displayBatteryStats.screenBrightnessTimers[i4].stopRunningLocked(j);
                }
                displayBatteryStats.screenBrightnessTimers[i3].startRunningLocked(j);
            }
            evaluateOverallScreenBrightnessBinLocked = evaluateOverallScreenBrightnessBinLocked();
        }
        maybeUpdateOverallScreenBrightness(evaluateOverallScreenBrightnessBinLocked, j, j2);
    }

    @GuardedBy({"this"})
    public final int evaluateOverallScreenBrightnessBinLocked() {
        int displayCount = getDisplayCount();
        int i = -1;
        for (int i2 = 0; i2 < displayCount; i2++) {
            DisplayBatteryStats displayBatteryStats = this.mPerDisplayBatteryStats[i2];
            int i3 = displayBatteryStats.screenState == 2 ? displayBatteryStats.screenBrightnessBin : -1;
            if (i3 > i) {
                i = i3;
            }
        }
        return i;
    }

    @GuardedBy({"this"})
    public final void maybeUpdateOverallScreenBrightness(int i, long j, long j2) {
        if (this.mScreenBrightnessBin != i) {
            if (i >= 0) {
                this.mHistory.recordScreenBrightnessEvent(j, j2, i);
            }
            if (this.mScreenState == 2) {
                int i2 = this.mScreenBrightnessBin;
                if (i2 >= 0) {
                    this.mScreenBrightnessTimer[i2].stopRunningLocked(j);
                }
                if (i >= 0) {
                    this.mScreenBrightnessTimer[i].startRunningLocked(j);
                }
            }
            this.mScreenBrightnessBin = i;
        }
    }

    @GuardedBy({"this"})
    public void noteUserActivityLocked(int i, int i2, long j, long j2) {
        if (this.mOnBatteryInternal) {
            getUidStatsLocked(mapUid(i), j, j2).noteUserActivityLocked(i2);
        }
    }

    @GuardedBy({"this"})
    public void noteWakeUpLocked(String str, int i, long j, long j2) {
        this.mHistory.recordEvent(j, j2, 18, str, i);
    }

    @GuardedBy({"this"})
    public void noteInteractiveLocked(boolean z, long j) {
        if (this.mInteractive != z) {
            this.mInteractive = z;
            if (z) {
                this.mInteractiveTimer.startRunningLocked(j);
            } else {
                this.mInteractiveTimer.stopRunningLocked(j);
            }
        }
    }

    @GuardedBy({"this"})
    public void noteConnectivityChangedLocked(int i, String str, long j, long j2) {
        this.mHistory.recordEvent(j, j2, 9, str, i);
        this.mNumConnectivityChange++;
    }

    @GuardedBy({"this"})
    public final void noteMobileRadioApWakeupLocked(long j, long j2, int i) {
        int mapUid = mapUid(i);
        this.mHistory.recordEvent(j, j2, 19, "", mapUid);
        getUidStatsLocked(mapUid, j, j2).noteMobileRadioApWakeupLocked();
    }

    @GuardedBy({"this"})
    public boolean noteMobileRadioPowerStateLocked(int i, long j, int i2, long j2, long j3) {
        long j4;
        if (this.mMobileRadioPowerState != i) {
            boolean isActiveRadioPowerState = isActiveRadioPowerState(i);
            if (isActiveRadioPowerState) {
                if (i2 > 0) {
                    noteMobileRadioApWakeupLocked(j2, j3, i2);
                }
                j4 = j / 1000000;
                this.mMobileRadioActiveStartTimeMs = j4;
                this.mHistory.recordStateStartEvent(j2, j3, 33554432);
            } else {
                long j5 = j / 1000000;
                long j6 = this.mMobileRadioActiveStartTimeMs;
                if (j5 < j6) {
                    Slog.wtf("BatteryStatsImpl", "Data connection inactive timestamp " + j5 + " is before start time " + j6);
                    j4 = j2;
                } else {
                    if (j5 < j2) {
                        this.mMobileRadioActiveAdjustedTime.addCountLocked(j2 - j5);
                    }
                    j4 = j5;
                }
                this.mHistory.recordStateStopEvent(j2, j3, 33554432);
            }
            this.mMobileRadioPowerState = i;
            getRatBatteryStatsLocked(this.mActiveRat).noteActive(isActiveRadioPowerState, j2);
            if (isActiveRadioPowerState) {
                this.mMobileRadioActiveTimer.startRunningLocked(j2);
                this.mMobileRadioActivePerAppTimer.startRunningLocked(j2);
            } else {
                this.mMobileRadioActiveTimer.stopRunningLocked(j4);
                this.mMobileRadioActivePerAppTimer.stopRunningLocked(j4);
                ModemActivityInfo modemActivityInfo = this.mLastModemActivityInfo;
                return modemActivityInfo == null || j2 >= modemActivityInfo.getTimestampMillis() + 600000;
            }
        }
        return false;
    }

    @GuardedBy({"this"})
    public void notePowerSaveModeLockedInit(boolean z, long j, long j2) {
        if (this.mPowerSaveModeEnabled != z) {
            notePowerSaveModeLocked(z, j, j2);
        } else {
            FrameworkStatsLog.write(20, z ? 1 : 0);
        }
    }

    @GuardedBy({"this"})
    public void notePowerSaveModeLocked(boolean z, long j, long j2) {
        if (this.mPowerSaveModeEnabled != z) {
            int i = z ? 4 : 0;
            int i2 = this.mModStepMode;
            int i3 = this.mCurStepMode;
            this.mModStepMode = i2 | ((i3 & 4) ^ i);
            this.mCurStepMode = i | (i3 & (-5));
            this.mPowerSaveModeEnabled = z;
            if (z) {
                this.mHistory.recordState2StartEvent(j, j2, Integer.MIN_VALUE);
                this.mPowerSaveModeEnabledTimer.startRunningLocked(j);
            } else {
                this.mHistory.recordState2StopEvent(j, j2, Integer.MIN_VALUE);
                this.mPowerSaveModeEnabledTimer.stopRunningLocked(j);
            }
            FrameworkStatsLog.write(20, z ? 1 : 0);
        }
    }

    @GuardedBy({"this"})
    public void noteDeviceIdleModeLocked(int i, String str, int i2, long j, long j2) {
        boolean z;
        boolean z2 = i == 2;
        boolean z3 = this.mDeviceIdling;
        if (z3 && !z2 && str == null) {
            z2 = true;
        }
        boolean z4 = i == 1;
        boolean z5 = this.mDeviceLightIdling;
        boolean z6 = (!z5 || z4 || z2 || str != null) ? z4 : true;
        if (str == null || !(z3 || z5)) {
            z = z6;
        } else {
            z = z6;
            this.mHistory.recordEvent(j, j2, 10, str, i2);
        }
        if (this.mDeviceIdling != z2 || this.mDeviceLightIdling != z) {
            FrameworkStatsLog.write(22, z2 ? 2 : z ? 1 : 0);
        }
        if (this.mDeviceIdling != z2) {
            this.mDeviceIdling = z2;
            int i3 = z2 ? 8 : 0;
            int i4 = this.mModStepMode;
            int i5 = this.mCurStepMode;
            this.mModStepMode = i4 | ((i5 & 8) ^ i3);
            this.mCurStepMode = i3 | (i5 & (-9));
            if (z2) {
                this.mDeviceIdlingTimer.startRunningLocked(j);
            } else {
                this.mDeviceIdlingTimer.stopRunningLocked(j);
            }
        }
        if (this.mDeviceLightIdling != z) {
            this.mDeviceLightIdling = z;
            if (z) {
                this.mDeviceLightIdlingTimer.startRunningLocked(j);
            } else {
                this.mDeviceLightIdlingTimer.stopRunningLocked(j);
            }
        }
        if (this.mDeviceIdleMode != i) {
            this.mHistory.recordDeviceIdleEvent(j, j2, i);
            long j3 = j - this.mLastIdleTimeStartMs;
            this.mLastIdleTimeStartMs = j;
            int i6 = this.mDeviceIdleMode;
            if (i6 == 1) {
                if (j3 > this.mLongestLightIdleTimeMs) {
                    this.mLongestLightIdleTimeMs = j3;
                }
                this.mDeviceIdleModeLightTimer.stopRunningLocked(j);
            } else if (i6 == 2) {
                if (j3 > this.mLongestFullIdleTimeMs) {
                    this.mLongestFullIdleTimeMs = j3;
                }
                this.mDeviceIdleModeFullTimer.stopRunningLocked(j);
            }
            if (i == 1) {
                this.mDeviceIdleModeLightTimer.startRunningLocked(j);
            } else if (i == 2) {
                this.mDeviceIdleModeFullTimer.startRunningLocked(j);
            }
            this.mDeviceIdleMode = i;
            FrameworkStatsLog.write(21, i);
        }
    }

    @GuardedBy({"this"})
    public void notePackageInstalledLocked(String str, long j, long j2, long j3) {
        this.mHistory.recordEvent(j2, j3, 11, str, (int) j);
        BatteryStats.PackageChange packageChange = new BatteryStats.PackageChange();
        packageChange.mPackageName = str;
        packageChange.mUpdate = true;
        packageChange.mVersionCode = j;
        addPackageChange(packageChange);
    }

    @GuardedBy({"this"})
    public void notePackageUninstalledLocked(String str, long j, long j2) {
        this.mHistory.recordEvent(j, j2, 12, str, 0);
        BatteryStats.PackageChange packageChange = new BatteryStats.PackageChange();
        packageChange.mPackageName = str;
        packageChange.mUpdate = true;
        addPackageChange(packageChange);
    }

    public final void addPackageChange(BatteryStats.PackageChange packageChange) {
        if (this.mDailyPackageChanges == null) {
            this.mDailyPackageChanges = new ArrayList<>();
        }
        this.mDailyPackageChanges.add(packageChange);
    }

    @GuardedBy({"this"})
    public void stopAllGpsSignalQualityTimersLocked(int i, long j) {
        for (int i2 = 0; i2 < this.mGpsSignalQualityTimer.length; i2++) {
            if (i2 != i) {
                while (this.mGpsSignalQualityTimer[i2].isRunningLocked()) {
                    this.mGpsSignalQualityTimer[i2].stopRunningLocked(j);
                }
            }
        }
    }

    @GuardedBy({"this"})
    public void notePhoneOnLocked(long j, long j2) {
        if (this.mPhoneOn) {
            return;
        }
        this.mHistory.recordState2StartEvent(j, j2, 8388608);
        this.mPhoneOn = true;
        this.mPhoneOnTimer.startRunningLocked(j);
        if (this.mConstants.PHONE_ON_EXTERNAL_STATS_COLLECTION) {
            scheduleSyncExternalStatsLocked("phone-on", 4);
        }
    }

    @GuardedBy({"this"})
    public void notePhoneOffLocked(long j, long j2) {
        if (this.mPhoneOn) {
            this.mHistory.recordState2StopEvent(j, j2, 8388608);
            this.mPhoneOn = false;
            this.mPhoneOnTimer.stopRunningLocked(j);
            scheduleSyncExternalStatsLocked("phone-off", 4);
        }
    }

    @GuardedBy({"this"})
    public final void registerUsbStateReceiver(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.hardware.usb.action.USB_STATE");
        context.registerReceiver(new BroadcastReceiver() { // from class: com.android.server.power.stats.BatteryStatsImpl.3
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                boolean booleanExtra = intent.getBooleanExtra("connected", false);
                synchronized (BatteryStatsImpl.this) {
                    BatteryStatsImpl batteryStatsImpl = BatteryStatsImpl.this;
                    batteryStatsImpl.noteUsbConnectionStateLocked(booleanExtra, batteryStatsImpl.mClock.elapsedRealtime(), BatteryStatsImpl.this.mClock.uptimeMillis());
                }
            }
        }, intentFilter);
        synchronized (this) {
            if (this.mUsbDataState == 0) {
                Intent registerReceiver = context.registerReceiver(null, intentFilter);
                boolean z = false;
                if (registerReceiver != null && registerReceiver.getBooleanExtra("connected", false)) {
                    z = true;
                }
                noteUsbConnectionStateLocked(z, this.mClock.elapsedRealtime(), this.mClock.uptimeMillis());
            }
        }
    }

    @GuardedBy({"this"})
    public final void noteUsbConnectionStateLocked(boolean z, long j, long j2) {
        int i = z ? 2 : 1;
        if (this.mUsbDataState != i) {
            this.mUsbDataState = i;
            if (z) {
                this.mHistory.recordState2StartEvent(j, j2, 262144);
            } else {
                this.mHistory.recordState2StopEvent(j, j2, 262144);
            }
        }
    }

    @GuardedBy({"this"})
    public void stopAllPhoneSignalStrengthTimersLocked(int i, long j) {
        for (int i2 = 0; i2 < CellSignalStrength.getNumSignalStrengthLevels(); i2++) {
            if (i2 != i) {
                while (this.mPhoneSignalStrengthsTimer[i2].isRunningLocked()) {
                    this.mPhoneSignalStrengthsTimer[i2].stopRunningLocked(j);
                }
            }
        }
    }

    @GuardedBy({"this"})
    public final void updateAllPhoneStateLocked(int i, int i2, int i3, long j, long j2) {
        int i4;
        boolean z;
        boolean z2;
        int i5;
        int i6 = i;
        int i7 = i3;
        this.mPhoneServiceStateRaw = i6;
        this.mPhoneSimStateRaw = i2;
        this.mPhoneSignalStrengthBinRaw = i7;
        int i8 = 0;
        boolean z3 = true;
        if (i2 == 1 && i6 == 1 && i7 > 0) {
            i6 = 0;
        }
        int i9 = -1;
        if (i6 == 3) {
            i4 = 0;
            z = false;
            z2 = false;
            i7 = -1;
        } else if (i6 == 0 || i6 != 1) {
            i4 = 0;
            z = false;
            z2 = false;
        } else if (this.mPhoneSignalScanningTimer.isRunningLocked()) {
            i7 = 0;
            i4 = 0;
            z2 = false;
            z = true;
        } else {
            this.mPhoneSignalScanningTimer.startRunningLocked(j);
            FrameworkStatsLog.write(94, i6, i2, 0);
            i7 = 0;
            z = true;
            z2 = true;
            i4 = 2097152;
        }
        if (!z && this.mPhoneSignalScanningTimer.isRunningLocked()) {
            this.mPhoneSignalScanningTimer.stopRunningLocked(j);
            FrameworkStatsLog.write(94, i6, i2, i7);
            z2 = true;
            i8 = 2097152;
        }
        if (this.mPhoneServiceState != i6) {
            this.mPhoneServiceState = i6;
            i5 = i6;
            z2 = true;
        } else {
            i5 = -1;
        }
        int i10 = this.mPhoneSignalStrengthBin;
        if (i10 != i7) {
            if (i10 >= 0) {
                this.mPhoneSignalStrengthsTimer[i10].stopRunningLocked(j);
            }
            if (i7 >= 0) {
                if (!this.mPhoneSignalStrengthsTimer[i7].isRunningLocked()) {
                    this.mPhoneSignalStrengthsTimer[i7].startRunningLocked(j);
                }
                FrameworkStatsLog.write(40, i7);
                i9 = i7;
            } else {
                stopAllPhoneSignalStrengthTimersLocked(-1, j);
                z3 = z2;
            }
            this.mPhoneSignalStrengthBin = i7;
            z2 = z3;
        }
        if (z2) {
            this.mHistory.recordPhoneStateChangeEvent(j, j2, i4, i8, i5, i9);
        }
    }

    @GuardedBy({"this"})
    public void notePhoneStateLocked(int i, int i2, long j, long j2) {
        updateAllPhoneStateLocked(i, i2, this.mPhoneSignalStrengthBinRaw, j, j2);
    }

    @GuardedBy({"this"})
    public void notePhoneSignalStrengthLocked(SignalStrength signalStrength, long j, long j2) {
        int level;
        int i;
        int level2 = signalStrength.getLevel();
        SparseIntArray sparseIntArray = new SparseIntArray(3);
        List<CellSignalStrength> cellSignalStrengths = signalStrength.getCellSignalStrengths();
        int size = cellSignalStrengths.size();
        for (int i2 = 0; i2 < size; i2++) {
            CellSignalStrength cellSignalStrength = cellSignalStrengths.get(i2);
            if (cellSignalStrength instanceof CellSignalStrengthNr) {
                level = cellSignalStrength.getLevel();
                i = 2;
            } else if (cellSignalStrength instanceof CellSignalStrengthLte) {
                level = cellSignalStrength.getLevel();
                i = 1;
            } else {
                level = cellSignalStrength.getLevel();
                i = 0;
            }
            if (sparseIntArray.get(i, -1) < level) {
                sparseIntArray.put(i, level);
            }
        }
        notePhoneSignalStrengthLocked(level2, sparseIntArray, j, j2);
    }

    @GuardedBy({"this"})
    public void notePhoneSignalStrengthLocked(int i, SparseIntArray sparseIntArray, long j, long j2) {
        int size = sparseIntArray.size();
        for (int i2 = 0; i2 < size; i2++) {
            int keyAt = sparseIntArray.keyAt(i2);
            getRatBatteryStatsLocked(keyAt).noteSignalStrength(sparseIntArray.valueAt(i2), j);
        }
        updateAllPhoneStateLocked(this.mPhoneServiceStateRaw, this.mPhoneSimStateRaw, i, j, j2);
    }

    /* JADX WARN: Removed duplicated region for block: B:17:0x0026  */
    /* JADX WARN: Removed duplicated region for block: B:20:0x0033  */
    /* JADX WARN: Removed duplicated region for block: B:28:? A[RETURN, SYNTHETIC] */
    @GuardedBy({"this"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void notePhoneDataConnectionStateLocked(int i, boolean z, int i2, int i3, long j, long j2) {
        int i4;
        int mapNetworkTypeToRadioAccessTechnology;
        int i5 = i;
        if (z) {
            if (i5 <= 0 || i5 > TelephonyManager.getAllNetworkTypes().length) {
                if (i2 != 1) {
                    if (i2 == 2) {
                        i5 = BatteryStats.DATA_CONNECTION_EMERGENCY_SERVICE;
                    } else {
                        i5 = BatteryStats.DATA_CONNECTION_OTHER;
                    }
                }
            }
            i4 = i5;
            mapNetworkTypeToRadioAccessTechnology = mapNetworkTypeToRadioAccessTechnology(i4);
            if (mapNetworkTypeToRadioAccessTechnology == 2) {
                getRatBatteryStatsLocked(mapNetworkTypeToRadioAccessTechnology).noteFrequencyRange(i3, j);
            }
            if (this.mPhoneDataConnectionType == i4) {
                this.mHistory.recordDataConnectionTypeChangeEvent(j, j2, i4);
                int i6 = this.mPhoneDataConnectionType;
                if (i6 >= 0) {
                    this.mPhoneDataConnectionsTimer[i6].stopRunningLocked(j);
                }
                this.mPhoneDataConnectionType = i4;
                this.mPhoneDataConnectionsTimer[i4].startRunningLocked(j);
                int i7 = this.mActiveRat;
                if (i7 != mapNetworkTypeToRadioAccessTechnology) {
                    getRatBatteryStatsLocked(i7).noteActive(false, j);
                    this.mActiveRat = mapNetworkTypeToRadioAccessTechnology;
                }
                getRatBatteryStatsLocked(mapNetworkTypeToRadioAccessTechnology).noteActive(this.mMobileRadioActiveTimer.isRunningLocked(), j);
                return;
            }
            return;
        }
        i4 = 0;
        mapNetworkTypeToRadioAccessTechnology = mapNetworkTypeToRadioAccessTechnology(i4);
        if (mapNetworkTypeToRadioAccessTechnology == 2) {
        }
        if (this.mPhoneDataConnectionType == i4) {
        }
    }

    public static int mapNetworkTypeToRadioAccessTechnology(int i) {
        switch (i) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
                return 0;
            case 13:
                return 1;
            case 19:
            default:
                Slog.w("BatteryStatsImpl", "Unhandled NetworkType (" + i + "), mapping to OTHER");
                return 0;
            case 20:
                return 2;
        }
    }

    public static int mapRadioAccessNetworkTypeToRadioAccessTechnology(int i) {
        switch (i) {
            case 0:
            case 1:
            case 2:
            case 4:
            case 5:
                return 0;
            case 3:
                return 1;
            case 6:
                return 2;
            default:
                Slog.w("BatteryStatsImpl", "Unhandled RadioAccessNetworkType (" + i + "), mapping to OTHER");
                return 0;
        }
    }

    @GuardedBy({"this"})
    public void noteWifiOnLocked(long j, long j2) {
        if (this.mWifiOn) {
            return;
        }
        this.mHistory.recordState2StartEvent(j, j2, 268435456);
        this.mWifiOn = true;
        this.mWifiOnTimer.startRunningLocked(j);
        scheduleSyncExternalStatsLocked("wifi-off", 2);
    }

    @GuardedBy({"this"})
    public void noteWifiOffLocked(long j, long j2) {
        if (this.mWifiOn) {
            this.mHistory.recordState2StopEvent(j, j2, 268435456);
            this.mWifiOn = false;
            this.mWifiOnTimer.stopRunningLocked(j);
            scheduleSyncExternalStatsLocked("wifi-on", 2);
        }
    }

    @GuardedBy({"this"})
    public void noteAudioOnLocked(int i, long j, long j2) {
        int mapUid = mapUid(i);
        if (this.mAudioOnNesting == 0) {
            this.mHistory.recordStateStartEvent(j, j2, 4194304);
            this.mAudioOnTimer.startRunningLocked(j);
        }
        this.mAudioOnNesting++;
        getUidStatsLocked(mapUid, j, j2).noteAudioTurnedOnLocked(j);
    }

    @GuardedBy({"this"})
    public void noteAudioOffLocked(int i, long j, long j2) {
        if (this.mAudioOnNesting == 0) {
            return;
        }
        int mapUid = mapUid(i);
        int i2 = this.mAudioOnNesting - 1;
        this.mAudioOnNesting = i2;
        if (i2 == 0) {
            this.mHistory.recordStateStopEvent(j, j2, 4194304);
            this.mAudioOnTimer.stopRunningLocked(j);
        }
        getUidStatsLocked(mapUid, j, j2).noteAudioTurnedOffLocked(j);
    }

    @GuardedBy({"this"})
    public void noteVideoOnLocked(int i, long j, long j2) {
        int mapUid = mapUid(i);
        if (this.mVideoOnNesting == 0) {
            this.mHistory.recordState2StartEvent(j, j2, 1073741824);
            this.mVideoOnTimer.startRunningLocked(j);
        }
        this.mVideoOnNesting++;
        getUidStatsLocked(mapUid, j, j2).noteVideoTurnedOnLocked(j);
    }

    @GuardedBy({"this"})
    public void noteVideoOffLocked(int i, long j, long j2) {
        if (this.mVideoOnNesting == 0) {
            return;
        }
        int mapUid = mapUid(i);
        int i2 = this.mVideoOnNesting - 1;
        this.mVideoOnNesting = i2;
        if (i2 == 0) {
            this.mHistory.recordState2StopEvent(j, j2, 1073741824);
            this.mVideoOnTimer.stopRunningLocked(j);
        }
        getUidStatsLocked(mapUid, j, j2).noteVideoTurnedOffLocked(j);
    }

    @GuardedBy({"this"})
    public void noteResetAudioLocked(long j, long j2) {
        if (this.mAudioOnNesting > 0) {
            this.mAudioOnNesting = 0;
            this.mHistory.recordStateStopEvent(j, j2, 4194304);
            this.mAudioOnTimer.stopAllRunningLocked(j);
            for (int i = 0; i < this.mUidStats.size(); i++) {
                this.mUidStats.valueAt(i).noteResetAudioLocked(j);
            }
        }
    }

    @GuardedBy({"this"})
    public void noteResetVideoLocked(long j, long j2) {
        if (this.mVideoOnNesting > 0) {
            this.mVideoOnNesting = 0;
            this.mHistory.recordState2StopEvent(j, j2, 1073741824);
            this.mVideoOnTimer.stopAllRunningLocked(j);
            for (int i = 0; i < this.mUidStats.size(); i++) {
                this.mUidStats.valueAt(i).noteResetVideoLocked(j);
            }
        }
    }

    @GuardedBy({"this"})
    public void noteActivityResumedLocked(int i, long j, long j2) {
        getUidStatsLocked(mapUid(i), j, j2).noteActivityResumedLocked(j);
    }

    @GuardedBy({"this"})
    public void noteActivityPausedLocked(int i, long j, long j2) {
        getUidStatsLocked(mapUid(i), j, j2).noteActivityPausedLocked(j);
    }

    @GuardedBy({"this"})
    public void noteVibratorOnLocked(int i, long j, long j2, long j3) {
        getUidStatsLocked(mapUid(i), j2, j3).noteVibratorOnLocked(j, j2);
    }

    @GuardedBy({"this"})
    public void noteVibratorOffLocked(int i, long j, long j2) {
        getUidStatsLocked(mapUid(i), j, j2).noteVibratorOffLocked(j);
    }

    @GuardedBy({"this"})
    public void noteFlashlightOnLocked(int i, long j, long j2) {
        int mapUid = mapUid(i);
        int i2 = this.mFlashlightOnNesting;
        this.mFlashlightOnNesting = i2 + 1;
        if (i2 == 0) {
            this.mHistory.recordState2StartEvent(j, j2, 134217728);
            this.mFlashlightOnTimer.startRunningLocked(j);
        }
        getUidStatsLocked(mapUid, j, j2).noteFlashlightTurnedOnLocked(j);
    }

    @GuardedBy({"this"})
    public void noteFlashlightOffLocked(int i, long j, long j2) {
        if (this.mFlashlightOnNesting == 0) {
            return;
        }
        int mapUid = mapUid(i);
        int i2 = this.mFlashlightOnNesting - 1;
        this.mFlashlightOnNesting = i2;
        if (i2 == 0) {
            this.mHistory.recordState2StopEvent(j, j2, 134217728);
            this.mFlashlightOnTimer.stopRunningLocked(j);
        }
        getUidStatsLocked(mapUid, j, j2).noteFlashlightTurnedOffLocked(j);
    }

    @GuardedBy({"this"})
    public void noteCameraOnLocked(int i, long j, long j2) {
        int mapUid = mapUid(i);
        int i2 = this.mCameraOnNesting;
        this.mCameraOnNesting = i2 + 1;
        if (i2 == 0) {
            this.mHistory.recordState2StartEvent(j, j2, 2097152);
            this.mCameraOnTimer.startRunningLocked(j);
        }
        getUidStatsLocked(mapUid, j, j2).noteCameraTurnedOnLocked(j);
        scheduleSyncExternalStatsLocked("camera-on", 64);
    }

    @GuardedBy({"this"})
    public void noteCameraOffLocked(int i, long j, long j2) {
        if (this.mCameraOnNesting == 0) {
            return;
        }
        int mapUid = mapUid(i);
        int i2 = this.mCameraOnNesting - 1;
        this.mCameraOnNesting = i2;
        if (i2 == 0) {
            this.mHistory.recordState2StopEvent(j, j2, 2097152);
            this.mCameraOnTimer.stopRunningLocked(j);
        }
        getUidStatsLocked(mapUid, j, j2).noteCameraTurnedOffLocked(j);
        scheduleSyncExternalStatsLocked("camera-off", 64);
    }

    @GuardedBy({"this"})
    public void noteResetCameraLocked(long j, long j2) {
        if (this.mCameraOnNesting > 0) {
            this.mCameraOnNesting = 0;
            this.mHistory.recordState2StopEvent(j, j2, 2097152);
            this.mCameraOnTimer.stopAllRunningLocked(j);
            for (int i = 0; i < this.mUidStats.size(); i++) {
                this.mUidStats.valueAt(i).noteResetCameraLocked(j);
            }
        }
        scheduleSyncExternalStatsLocked("camera-reset", 64);
    }

    @GuardedBy({"this"})
    public void noteResetFlashlightLocked(long j, long j2) {
        if (this.mFlashlightOnNesting > 0) {
            this.mFlashlightOnNesting = 0;
            this.mHistory.recordState2StopEvent(j, j2, 134217728);
            this.mFlashlightOnTimer.stopAllRunningLocked(j);
            for (int i = 0; i < this.mUidStats.size(); i++) {
                this.mUidStats.valueAt(i).noteResetFlashlightLocked(j);
            }
        }
    }

    @GuardedBy({"this"})
    public final void noteBluetoothScanStartedLocked(WorkSource.WorkChain workChain, int i, boolean z, long j, long j2) {
        if (workChain != null) {
            i = workChain.getAttributionUid();
        }
        int mapUid = mapUid(i);
        if (this.mBluetoothScanNesting == 0) {
            this.mHistory.recordState2StartEvent(j, j2, 1048576);
            this.mBluetoothScanTimer.startRunningLocked(j);
        }
        this.mBluetoothScanNesting++;
        getUidStatsLocked(mapUid, j, j2).noteBluetoothScanStartedLocked(j, z);
    }

    @GuardedBy({"this"})
    public void noteBluetoothScanStartedFromSourceLocked(WorkSource workSource, boolean z, long j, long j2) {
        int size = workSource.size();
        for (int i = 0; i < size; i++) {
            noteBluetoothScanStartedLocked(null, workSource.getUid(i), z, j, j2);
        }
        List workChains = workSource.getWorkChains();
        if (workChains != null) {
            for (int i2 = 0; i2 < workChains.size(); i2++) {
                noteBluetoothScanStartedLocked((WorkSource.WorkChain) workChains.get(i2), -1, z, j, j2);
            }
        }
    }

    @GuardedBy({"this"})
    public final void noteBluetoothScanStoppedLocked(WorkSource.WorkChain workChain, int i, boolean z, long j, long j2) {
        if (workChain != null) {
            i = workChain.getAttributionUid();
        }
        int mapUid = mapUid(i);
        int i2 = this.mBluetoothScanNesting - 1;
        this.mBluetoothScanNesting = i2;
        if (i2 == 0) {
            this.mHistory.recordState2StopEvent(j, j2, 1048576);
            this.mBluetoothScanTimer.stopRunningLocked(j);
        }
        getUidStatsLocked(mapUid, j, j2).noteBluetoothScanStoppedLocked(j, z);
    }

    @GuardedBy({"this"})
    public void noteBluetoothScanStoppedFromSourceLocked(WorkSource workSource, boolean z, long j, long j2) {
        int size = workSource.size();
        for (int i = 0; i < size; i++) {
            noteBluetoothScanStoppedLocked(null, workSource.getUid(i), z, j, j2);
        }
        List workChains = workSource.getWorkChains();
        if (workChains != null) {
            for (int i2 = 0; i2 < workChains.size(); i2++) {
                noteBluetoothScanStoppedLocked((WorkSource.WorkChain) workChains.get(i2), -1, z, j, j2);
            }
        }
    }

    @GuardedBy({"this"})
    public void noteResetBluetoothScanLocked(long j, long j2) {
        if (this.mBluetoothScanNesting > 0) {
            this.mBluetoothScanNesting = 0;
            this.mHistory.recordState2StopEvent(j, j2, 1048576);
            this.mBluetoothScanTimer.stopAllRunningLocked(j);
            for (int i = 0; i < this.mUidStats.size(); i++) {
                this.mUidStats.valueAt(i).noteResetBluetoothScanLocked(j);
            }
        }
    }

    @GuardedBy({"this"})
    public void noteBluetoothScanResultsFromSourceLocked(WorkSource workSource, int i, long j, long j2) {
        int size = workSource.size();
        for (int i2 = 0; i2 < size; i2++) {
            getUidStatsLocked(mapUid(workSource.getUid(i2)), j, j2).noteBluetoothScanResultsLocked(i);
        }
        List workChains = workSource.getWorkChains();
        if (workChains != null) {
            for (int i3 = 0; i3 < workChains.size(); i3++) {
                getUidStatsLocked(mapUid(((WorkSource.WorkChain) workChains.get(i3)).getAttributionUid()), j, j2).noteBluetoothScanResultsLocked(i);
            }
        }
    }

    @GuardedBy({"this"})
    public final void noteWifiRadioApWakeupLocked(long j, long j2, int i) {
        int mapUid = mapUid(i);
        this.mHistory.recordEvent(j, j2, 19, "", mapUid);
        getUidStatsLocked(mapUid, j, j2).noteWifiRadioApWakeupLocked();
    }

    @GuardedBy({"this"})
    public void noteWifiRadioPowerState(int i, long j, int i2, long j2, long j3) {
        if (this.mWifiRadioPowerState != i) {
            if (i == 2 || i == 3) {
                if (i2 > 0) {
                    noteWifiRadioApWakeupLocked(j2, j3, i2);
                }
                this.mHistory.recordStateStartEvent(j2, j3, 67108864);
                this.mWifiActiveTimer.startRunningLocked(j2);
            } else {
                this.mHistory.recordStateStopEvent(j2, j3, 67108864);
                this.mWifiActiveTimer.stopRunningLocked(j / 1000000);
            }
            this.mWifiRadioPowerState = i;
        }
    }

    @GuardedBy({"this"})
    public void noteWifiRunningLocked(WorkSource workSource, long j, long j2) {
        if (!this.mGlobalWifiRunning) {
            this.mHistory.recordState2StartEvent(j, j2, 536870912);
            this.mGlobalWifiRunning = true;
            this.mGlobalWifiRunningTimer.startRunningLocked(j);
            int size = workSource.size();
            for (int i = 0; i < size; i++) {
                getUidStatsLocked(mapUid(workSource.getUid(i)), j, j2).noteWifiRunningLocked(j);
            }
            List workChains = workSource.getWorkChains();
            if (workChains != null) {
                for (int i2 = 0; i2 < workChains.size(); i2++) {
                    getUidStatsLocked(mapUid(((WorkSource.WorkChain) workChains.get(i2)).getAttributionUid()), j, j2).noteWifiRunningLocked(j);
                }
            }
            scheduleSyncExternalStatsLocked("wifi-running", 2);
            return;
        }
        Log.w("BatteryStatsImpl", "noteWifiRunningLocked -- called while WIFI running");
    }

    @GuardedBy({"this"})
    public void noteWifiRunningChangedLocked(WorkSource workSource, WorkSource workSource2, long j, long j2) {
        if (this.mGlobalWifiRunning) {
            int size = workSource.size();
            for (int i = 0; i < size; i++) {
                getUidStatsLocked(mapUid(workSource.getUid(i)), j, j2).noteWifiStoppedLocked(j);
            }
            List workChains = workSource.getWorkChains();
            if (workChains != null) {
                for (int i2 = 0; i2 < workChains.size(); i2++) {
                    getUidStatsLocked(mapUid(((WorkSource.WorkChain) workChains.get(i2)).getAttributionUid()), j, j2).noteWifiStoppedLocked(j);
                }
            }
            int size2 = workSource2.size();
            for (int i3 = 0; i3 < size2; i3++) {
                getUidStatsLocked(mapUid(workSource2.getUid(i3)), j, j2).noteWifiRunningLocked(j);
            }
            List workChains2 = workSource2.getWorkChains();
            if (workChains2 != null) {
                for (int i4 = 0; i4 < workChains2.size(); i4++) {
                    getUidStatsLocked(mapUid(((WorkSource.WorkChain) workChains2.get(i4)).getAttributionUid()), j, j2).noteWifiRunningLocked(j);
                }
                return;
            }
            return;
        }
        Log.w("BatteryStatsImpl", "noteWifiRunningChangedLocked -- called while WIFI not running");
    }

    @GuardedBy({"this"})
    public void noteWifiStoppedLocked(WorkSource workSource, long j, long j2) {
        if (this.mGlobalWifiRunning) {
            this.mHistory.recordState2StopEvent(j, j2, 536870912);
            this.mGlobalWifiRunning = false;
            this.mGlobalWifiRunningTimer.stopRunningLocked(j);
            int size = workSource.size();
            for (int i = 0; i < size; i++) {
                getUidStatsLocked(mapUid(workSource.getUid(i)), j, j2).noteWifiStoppedLocked(j);
            }
            List workChains = workSource.getWorkChains();
            if (workChains != null) {
                for (int i2 = 0; i2 < workChains.size(); i2++) {
                    getUidStatsLocked(mapUid(((WorkSource.WorkChain) workChains.get(i2)).getAttributionUid()), j, j2).noteWifiStoppedLocked(j);
                }
            }
            scheduleSyncExternalStatsLocked("wifi-stopped", 2);
            return;
        }
        Log.w("BatteryStatsImpl", "noteWifiStoppedLocked -- called while WIFI not running");
    }

    @GuardedBy({"this"})
    public void noteWifiStateLocked(int i, String str, long j) {
        int i2 = this.mWifiState;
        if (i2 != i) {
            if (i2 >= 0) {
                this.mWifiStateTimer[i2].stopRunningLocked(j);
            }
            this.mWifiState = i;
            this.mWifiStateTimer[i].startRunningLocked(j);
            scheduleSyncExternalStatsLocked("wifi-state", 2);
        }
    }

    @GuardedBy({"this"})
    public void noteWifiSupplicantStateChangedLocked(int i, boolean z, long j, long j2) {
        int i2 = this.mWifiSupplState;
        if (i2 != i) {
            if (i2 >= 0) {
                this.mWifiSupplStateTimer[i2].stopRunningLocked(j);
            }
            this.mWifiSupplState = i;
            this.mWifiSupplStateTimer[i].startRunningLocked(j);
            this.mHistory.recordWifiSupplicantStateChangeEvent(j, j2, i);
        }
    }

    @GuardedBy({"this"})
    public void stopAllWifiSignalStrengthTimersLocked(int i, long j) {
        for (int i2 = 0; i2 < 5; i2++) {
            if (i2 != i) {
                while (this.mWifiSignalStrengthsTimer[i2].isRunningLocked()) {
                    this.mWifiSignalStrengthsTimer[i2].stopRunningLocked(j);
                }
            }
        }
    }

    @GuardedBy({"this"})
    public void noteWifiRssiChangedLocked(int i, long j, long j2) {
        int calculateSignalLevel = WifiManager.calculateSignalLevel(i, 5);
        int i2 = this.mWifiSignalStrengthBin;
        if (i2 != calculateSignalLevel) {
            if (i2 >= 0) {
                this.mWifiSignalStrengthsTimer[i2].stopRunningLocked(j);
            }
            if (calculateSignalLevel >= 0) {
                if (!this.mWifiSignalStrengthsTimer[calculateSignalLevel].isRunningLocked()) {
                    this.mWifiSignalStrengthsTimer[calculateSignalLevel].startRunningLocked(j);
                }
                this.mHistory.recordWifiSignalStrengthChangeEvent(j, j2, calculateSignalLevel);
            } else {
                stopAllWifiSignalStrengthTimersLocked(-1, j);
            }
            this.mWifiSignalStrengthBin = calculateSignalLevel;
        }
    }

    @GuardedBy({"this"})
    public void noteFullWifiLockAcquiredLocked(int i, long j, long j2) {
        if (this.mWifiFullLockNesting == 0) {
            this.mHistory.recordStateStartEvent(j, j2, 268435456);
        }
        this.mWifiFullLockNesting++;
        getUidStatsLocked(i, j, j2).noteFullWifiLockAcquiredLocked(j);
    }

    @GuardedBy({"this"})
    public void noteFullWifiLockReleasedLocked(int i, long j, long j2) {
        int i2 = this.mWifiFullLockNesting - 1;
        this.mWifiFullLockNesting = i2;
        if (i2 == 0) {
            this.mHistory.recordStateStopEvent(j, j2, 268435456);
        }
        getUidStatsLocked(i, j, j2).noteFullWifiLockReleasedLocked(j);
    }

    @GuardedBy({"this"})
    public void noteWifiScanStartedLocked(int i, long j, long j2) {
        if (this.mWifiScanNesting == 0) {
            this.mHistory.recordStateStartEvent(j, j2, 134217728);
        }
        this.mWifiScanNesting++;
        getUidStatsLocked(i, j, j2).noteWifiScanStartedLocked(j);
    }

    @GuardedBy({"this"})
    public void noteWifiScanStoppedLocked(int i, long j, long j2) {
        int i2 = this.mWifiScanNesting - 1;
        this.mWifiScanNesting = i2;
        if (i2 == 0) {
            this.mHistory.recordStateStopEvent(j, j2, 134217728);
        }
        getUidStatsLocked(i, j, j2).noteWifiScanStoppedLocked(j);
    }

    public void noteWifiBatchedScanStartedLocked(int i, int i2, long j, long j2) {
        getUidStatsLocked(mapUid(i), j, j2).noteWifiBatchedScanStartedLocked(i2, j);
    }

    public void noteWifiBatchedScanStoppedLocked(int i, long j, long j2) {
        getUidStatsLocked(mapUid(i), j, j2).noteWifiBatchedScanStoppedLocked(j);
    }

    @GuardedBy({"this"})
    public void noteWifiMulticastEnabledLocked(int i, long j, long j2) {
        int mapUid = mapUid(i);
        if (this.mWifiMulticastNesting == 0) {
            this.mHistory.recordStateStartEvent(j, j2, 65536);
            if (!this.mWifiMulticastWakelockTimer.isRunningLocked()) {
                this.mWifiMulticastWakelockTimer.startRunningLocked(j);
            }
        }
        this.mWifiMulticastNesting++;
        getUidStatsLocked(mapUid, j, j2).noteWifiMulticastEnabledLocked(j);
    }

    @GuardedBy({"this"})
    public void noteWifiMulticastDisabledLocked(int i, long j, long j2) {
        int mapUid = mapUid(i);
        int i2 = this.mWifiMulticastNesting - 1;
        this.mWifiMulticastNesting = i2;
        if (i2 == 0) {
            this.mHistory.recordStateStopEvent(j, j2, 65536);
            if (this.mWifiMulticastWakelockTimer.isRunningLocked()) {
                this.mWifiMulticastWakelockTimer.stopRunningLocked(j);
            }
        }
        getUidStatsLocked(mapUid, j, j2).noteWifiMulticastDisabledLocked(j);
    }

    @GuardedBy({"this"})
    public void noteFullWifiLockAcquiredFromSourceLocked(WorkSource workSource, long j, long j2) {
        int size = workSource.size();
        for (int i = 0; i < size; i++) {
            noteFullWifiLockAcquiredLocked(mapUid(workSource.getUid(i)), j, j2);
        }
        List workChains = workSource.getWorkChains();
        if (workChains != null) {
            for (int i2 = 0; i2 < workChains.size(); i2++) {
                noteFullWifiLockAcquiredLocked(mapUid(((WorkSource.WorkChain) workChains.get(i2)).getAttributionUid()), j, j2);
            }
        }
    }

    @GuardedBy({"this"})
    public void noteFullWifiLockReleasedFromSourceLocked(WorkSource workSource, long j, long j2) {
        int size = workSource.size();
        for (int i = 0; i < size; i++) {
            noteFullWifiLockReleasedLocked(mapUid(workSource.getUid(i)), j, j2);
        }
        List workChains = workSource.getWorkChains();
        if (workChains != null) {
            for (int i2 = 0; i2 < workChains.size(); i2++) {
                noteFullWifiLockReleasedLocked(mapUid(((WorkSource.WorkChain) workChains.get(i2)).getAttributionUid()), j, j2);
            }
        }
    }

    @GuardedBy({"this"})
    public void noteWifiScanStartedFromSourceLocked(WorkSource workSource, long j, long j2) {
        int size = workSource.size();
        for (int i = 0; i < size; i++) {
            noteWifiScanStartedLocked(mapUid(workSource.getUid(i)), j, j2);
        }
        List workChains = workSource.getWorkChains();
        if (workChains != null) {
            for (int i2 = 0; i2 < workChains.size(); i2++) {
                noteWifiScanStartedLocked(mapUid(((WorkSource.WorkChain) workChains.get(i2)).getAttributionUid()), j, j2);
            }
        }
    }

    @GuardedBy({"this"})
    public void noteWifiScanStoppedFromSourceLocked(WorkSource workSource, long j, long j2) {
        int size = workSource.size();
        for (int i = 0; i < size; i++) {
            noteWifiScanStoppedLocked(mapUid(workSource.getUid(i)), j, j2);
        }
        List workChains = workSource.getWorkChains();
        if (workChains != null) {
            for (int i2 = 0; i2 < workChains.size(); i2++) {
                noteWifiScanStoppedLocked(mapUid(((WorkSource.WorkChain) workChains.get(i2)).getAttributionUid()), j, j2);
            }
        }
    }

    @GuardedBy({"this"})
    public void noteWifiBatchedScanStartedFromSourceLocked(WorkSource workSource, int i, long j, long j2) {
        int size = workSource.size();
        for (int i2 = 0; i2 < size; i2++) {
            noteWifiBatchedScanStartedLocked(workSource.getUid(i2), i, j, j2);
        }
        List workChains = workSource.getWorkChains();
        if (workChains != null) {
            for (int i3 = 0; i3 < workChains.size(); i3++) {
                noteWifiBatchedScanStartedLocked(((WorkSource.WorkChain) workChains.get(i3)).getAttributionUid(), i, j, j2);
            }
        }
    }

    @GuardedBy({"this"})
    public void noteWifiBatchedScanStoppedFromSourceLocked(WorkSource workSource, long j, long j2) {
        int size = workSource.size();
        for (int i = 0; i < size; i++) {
            noteWifiBatchedScanStoppedLocked(workSource.getUid(i), j, j2);
        }
        List workChains = workSource.getWorkChains();
        if (workChains != null) {
            for (int i2 = 0; i2 < workChains.size(); i2++) {
                noteWifiBatchedScanStoppedLocked(((WorkSource.WorkChain) workChains.get(i2)).getAttributionUid(), j, j2);
            }
        }
    }

    public static String[] includeInStringArray(String[] strArr, String str) {
        if (ArrayUtils.indexOf(strArr, str) >= 0) {
            return strArr;
        }
        String[] strArr2 = new String[strArr.length + 1];
        System.arraycopy(strArr, 0, strArr2, 0, strArr.length);
        strArr2[strArr.length] = str;
        return strArr2;
    }

    public static String[] excludeFromStringArray(String[] strArr, String str) {
        int indexOf = ArrayUtils.indexOf(strArr, str);
        if (indexOf >= 0) {
            String[] strArr2 = new String[strArr.length - 1];
            if (indexOf > 0) {
                System.arraycopy(strArr, 0, strArr2, 0, indexOf);
            }
            if (indexOf < strArr.length - 1) {
                System.arraycopy(strArr, indexOf + 1, strArr2, indexOf, (strArr.length - indexOf) - 1);
            }
            return strArr2;
        }
        return strArr;
    }

    public void noteNetworkInterfaceForTransports(String str, int[] iArr) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        int displayTransport = NetworkCapabilitiesUtils.getDisplayTransport(iArr);
        synchronized (this.mModemNetworkLock) {
            if (displayTransport == 0) {
                this.mModemIfaces = includeInStringArray(this.mModemIfaces, str);
            } else {
                this.mModemIfaces = excludeFromStringArray(this.mModemIfaces, str);
            }
        }
        synchronized (this.mWifiNetworkLock) {
            if (displayTransport == 1) {
                this.mWifiIfaces = includeInStringArray(this.mWifiIfaces, str);
            } else {
                this.mWifiIfaces = excludeFromStringArray(this.mWifiIfaces, str);
            }
        }
    }

    public void noteBinderCallStats(int i, long j, Collection<BinderCallsStats.CallStat> collection, long j2, long j3) {
        synchronized (this) {
            getUidStatsLocked(i, j2, j3).noteBinderCallStatsLocked(j, collection);
        }
    }

    public void noteBinderThreadNativeIds(int[] iArr) {
        this.mSystemServerCpuThreadReader.setBinderThreadNativeTids(iArr);
    }

    @VisibleForTesting
    public void updateSystemServiceCallStats() {
        long j;
        long j2;
        BatteryStatsImpl batteryStatsImpl = this;
        int i = 0;
        long j3 = 0;
        for (int i2 = 0; i2 < batteryStatsImpl.mUidStats.size(); i2++) {
            ArraySet arraySet = batteryStatsImpl.mUidStats.valueAt(i2).mBinderCallStats;
            for (int size = arraySet.size() - 1; size >= 0; size--) {
                BinderCallStats binderCallStats = (BinderCallStats) arraySet.valueAt(size);
                i = (int) (i + binderCallStats.recordedCallCount);
                j3 += binderCallStats.recordedCpuTimeMicros;
            }
        }
        int i3 = 0;
        long j4 = 0;
        while (i3 < batteryStatsImpl.mUidStats.size()) {
            Uid valueAt = batteryStatsImpl.mUidStats.valueAt(i3);
            ArraySet arraySet2 = valueAt.mBinderCallStats;
            int size2 = arraySet2.size() - 1;
            int i4 = 0;
            long j5 = 0;
            while (size2 >= 0) {
                BinderCallStats binderCallStats2 = (BinderCallStats) arraySet2.valueAt(size2);
                long j6 = j4;
                long j7 = binderCallStats2.callCount;
                i4 = (int) (i4 + j7);
                long j8 = binderCallStats2.recordedCallCount;
                if (j8 > 0) {
                    j2 = (j7 * binderCallStats2.recordedCpuTimeMicros) / j8;
                } else if (i > 0) {
                    j2 = (j7 * j3) / i;
                } else {
                    size2--;
                    j4 = j6;
                }
                j5 += j2;
                size2--;
                j4 = j6;
            }
            long j9 = j4;
            long j10 = i4;
            if (j10 < valueAt.mBinderCallCount && i > 0) {
                j5 += ((valueAt.mBinderCallCount - j10) * j3) / i;
            }
            valueAt.mSystemServiceTimeUs = j5;
            j4 = j9 + j5;
            i3++;
            batteryStatsImpl = this;
        }
        long j11 = j4;
        int i5 = 0;
        while (i5 < this.mUidStats.size()) {
            Uid valueAt2 = this.mUidStats.valueAt(i5);
            if (j11 > 0) {
                j = j11;
                valueAt2.mProportionalSystemServiceUsage = valueAt2.mSystemServiceTimeUs / j;
            } else {
                j = j11;
                valueAt2.mProportionalSystemServiceUsage = 0.0d;
            }
            i5++;
            j11 = j;
        }
    }

    public String[] getWifiIfaces() {
        String[] strArr;
        synchronized (this.mWifiNetworkLock) {
            strArr = this.mWifiIfaces;
        }
        return strArr;
    }

    public String[] getMobileIfaces() {
        String[] strArr;
        synchronized (this.mModemNetworkLock) {
            strArr = this.mModemIfaces;
        }
        return strArr;
    }

    public long getScreenOnTime(long j, int i) {
        return this.mScreenOnTimer.getTotalTimeLocked(j, i);
    }

    public int getScreenOnCount(int i) {
        return this.mScreenOnTimer.getCountLocked(i);
    }

    public long getScreenDozeTime(long j, int i) {
        return this.mScreenDozeTimer.getTotalTimeLocked(j, i);
    }

    public int getScreenDozeCount(int i) {
        return this.mScreenDozeTimer.getCountLocked(i);
    }

    public long getScreenBrightnessTime(int i, long j, int i2) {
        return this.mScreenBrightnessTimer[i].getTotalTimeLocked(j, i2);
    }

    public Timer getScreenBrightnessTimer(int i) {
        return this.mScreenBrightnessTimer[i];
    }

    public int getDisplayCount() {
        return this.mPerDisplayBatteryStats.length;
    }

    public long getDisplayScreenOnTime(int i, long j) {
        return this.mPerDisplayBatteryStats[i].screenOnTimer.getTotalTimeLocked(j, 0);
    }

    public long getDisplayScreenDozeTime(int i, long j) {
        return this.mPerDisplayBatteryStats[i].screenDozeTimer.getTotalTimeLocked(j, 0);
    }

    public long getDisplayScreenBrightnessTime(int i, int i2, long j) {
        return this.mPerDisplayBatteryStats[i].screenBrightnessTimers[i2].getTotalTimeLocked(j, 0);
    }

    public long getInteractiveTime(long j, int i) {
        return this.mInteractiveTimer.getTotalTimeLocked(j, i);
    }

    public long getPowerSaveModeEnabledTime(long j, int i) {
        return this.mPowerSaveModeEnabledTimer.getTotalTimeLocked(j, i);
    }

    public int getPowerSaveModeEnabledCount(int i) {
        return this.mPowerSaveModeEnabledTimer.getCountLocked(i);
    }

    public long getDeviceIdleModeTime(int i, long j, int i2) {
        if (i != 1) {
            if (i != 2) {
                return 0L;
            }
            return this.mDeviceIdleModeFullTimer.getTotalTimeLocked(j, i2);
        }
        return this.mDeviceIdleModeLightTimer.getTotalTimeLocked(j, i2);
    }

    public int getDeviceIdleModeCount(int i, int i2) {
        if (i != 1) {
            if (i != 2) {
                return 0;
            }
            return this.mDeviceIdleModeFullTimer.getCountLocked(i2);
        }
        return this.mDeviceIdleModeLightTimer.getCountLocked(i2);
    }

    public long getLongestDeviceIdleModeTime(int i) {
        if (i != 1) {
            if (i != 2) {
                return 0L;
            }
            return this.mLongestFullIdleTimeMs;
        }
        return this.mLongestLightIdleTimeMs;
    }

    public long getDeviceIdlingTime(int i, long j, int i2) {
        if (i != 1) {
            if (i != 2) {
                return 0L;
            }
            return this.mDeviceIdlingTimer.getTotalTimeLocked(j, i2);
        }
        return this.mDeviceLightIdlingTimer.getTotalTimeLocked(j, i2);
    }

    public int getDeviceIdlingCount(int i, int i2) {
        if (i != 1) {
            if (i != 2) {
                return 0;
            }
            return this.mDeviceIdlingTimer.getCountLocked(i2);
        }
        return this.mDeviceLightIdlingTimer.getCountLocked(i2);
    }

    public int getNumConnectivityChange(int i) {
        return this.mNumConnectivityChange;
    }

    public long getGpsSignalQualityTime(int i, long j, int i2) {
        if (i >= 0) {
            StopwatchTimer[] stopwatchTimerArr = this.mGpsSignalQualityTimer;
            if (i >= stopwatchTimerArr.length) {
                return 0L;
            }
            return stopwatchTimerArr[i].getTotalTimeLocked(j, i2);
        }
        return 0L;
    }

    public long getGpsBatteryDrainMaMs() {
        double d = 0.0d;
        if (this.mPowerProfile.getAveragePower("gps.voltage") / 1000.0d == 0.0d) {
            return 0L;
        }
        long elapsedRealtime = SystemClock.elapsedRealtime() * 1000;
        for (int i = 0; i < this.mGpsSignalQualityTimer.length; i++) {
            d += this.mPowerProfile.getAveragePower("gps.signalqualitybased", i) * (getGpsSignalQualityTime(i, elapsedRealtime, 0) / 1000);
        }
        return (long) d;
    }

    public long getPhoneOnTime(long j, int i) {
        return this.mPhoneOnTimer.getTotalTimeLocked(j, i);
    }

    public int getPhoneOnCount(int i) {
        return this.mPhoneOnTimer.getCountLocked(i);
    }

    public long getPhoneSignalStrengthTime(int i, long j, int i2) {
        return this.mPhoneSignalStrengthsTimer[i].getTotalTimeLocked(j, i2);
    }

    public long getPhoneSignalScanningTime(long j, int i) {
        return this.mPhoneSignalScanningTimer.getTotalTimeLocked(j, i);
    }

    public Timer getPhoneSignalScanningTimer() {
        return this.mPhoneSignalScanningTimer;
    }

    public int getPhoneSignalStrengthCount(int i, int i2) {
        return this.mPhoneSignalStrengthsTimer[i].getCountLocked(i2);
    }

    public Timer getPhoneSignalStrengthTimer(int i) {
        return this.mPhoneSignalStrengthsTimer[i];
    }

    public long getPhoneDataConnectionTime(int i, long j, int i2) {
        return this.mPhoneDataConnectionsTimer[i].getTotalTimeLocked(j, i2);
    }

    public int getPhoneDataConnectionCount(int i, int i2) {
        return this.mPhoneDataConnectionsTimer[i].getCountLocked(i2);
    }

    public Timer getPhoneDataConnectionTimer(int i) {
        return this.mPhoneDataConnectionsTimer[i];
    }

    public long getActiveRadioDurationMs(int i, int i2, int i3, long j) {
        RadioAccessTechnologyBatteryStats radioAccessTechnologyBatteryStats = this.mPerRatBatteryStats[i];
        if (radioAccessTechnologyBatteryStats == null) {
            return 0L;
        }
        StopwatchTimer[][] stopwatchTimerArr = radioAccessTechnologyBatteryStats.perStateTimers;
        int length = stopwatchTimerArr.length;
        if (i2 >= 0 && i2 < length) {
            StopwatchTimer[] stopwatchTimerArr2 = stopwatchTimerArr[i2];
            int length2 = stopwatchTimerArr2.length;
            if (i3 >= 0 && i3 < length2) {
                return stopwatchTimerArr2[i3].getTotalTimeLocked(j * 1000, 0) / 1000;
            }
        }
        return 0L;
    }

    public long getActiveTxRadioDurationMs(int i, int i2, int i3, long j) {
        LongSamplingCounter txDurationCounter;
        RadioAccessTechnologyBatteryStats radioAccessTechnologyBatteryStats = this.mPerRatBatteryStats[i];
        if (radioAccessTechnologyBatteryStats == null || (txDurationCounter = radioAccessTechnologyBatteryStats.getTxDurationCounter(i2, i3, false)) == null) {
            return -1L;
        }
        return txDurationCounter.getCountLocked(0);
    }

    public long getActiveRxRadioDurationMs(int i, int i2, long j) {
        LongSamplingCounter rxDurationCounter;
        RadioAccessTechnologyBatteryStats radioAccessTechnologyBatteryStats = this.mPerRatBatteryStats[i];
        if (radioAccessTechnologyBatteryStats == null || (rxDurationCounter = radioAccessTechnologyBatteryStats.getRxDurationCounter(i2, false)) == null) {
            return -1L;
        }
        return rxDurationCounter.getCountLocked(0);
    }

    public long getMobileRadioActiveTime(long j, int i) {
        return this.mMobileRadioActiveTimer.getTotalTimeLocked(j, i);
    }

    public int getMobileRadioActiveCount(int i) {
        return this.mMobileRadioActiveTimer.getCountLocked(i);
    }

    public long getMobileRadioActiveAdjustedTime(int i) {
        return this.mMobileRadioActiveAdjustedTime.getCountLocked(i);
    }

    public long getMobileRadioActiveUnknownTime(int i) {
        return this.mMobileRadioActiveUnknownTime.getCountLocked(i);
    }

    public int getMobileRadioActiveUnknownCount(int i) {
        return (int) this.mMobileRadioActiveUnknownCount.getCountLocked(i);
    }

    public long getWifiMulticastWakelockTime(long j, int i) {
        return this.mWifiMulticastWakelockTimer.getTotalTimeLocked(j, i);
    }

    public int getWifiMulticastWakelockCount(int i) {
        return this.mWifiMulticastWakelockTimer.getCountLocked(i);
    }

    public long getWifiOnTime(long j, int i) {
        return this.mWifiOnTimer.getTotalTimeLocked(j, i);
    }

    public long getWifiActiveTime(long j, int i) {
        return this.mWifiActiveTimer.getTotalTimeLocked(j, i);
    }

    public long getGlobalWifiRunningTime(long j, int i) {
        return this.mGlobalWifiRunningTimer.getTotalTimeLocked(j, i);
    }

    public long getWifiStateTime(int i, long j, int i2) {
        return this.mWifiStateTimer[i].getTotalTimeLocked(j, i2);
    }

    public int getWifiStateCount(int i, int i2) {
        return this.mWifiStateTimer[i].getCountLocked(i2);
    }

    public Timer getWifiStateTimer(int i) {
        return this.mWifiStateTimer[i];
    }

    public long getWifiSupplStateTime(int i, long j, int i2) {
        return this.mWifiSupplStateTimer[i].getTotalTimeLocked(j, i2);
    }

    public int getWifiSupplStateCount(int i, int i2) {
        return this.mWifiSupplStateTimer[i].getCountLocked(i2);
    }

    public Timer getWifiSupplStateTimer(int i) {
        return this.mWifiSupplStateTimer[i];
    }

    public long getWifiSignalStrengthTime(int i, long j, int i2) {
        return this.mWifiSignalStrengthsTimer[i].getTotalTimeLocked(j, i2);
    }

    public int getWifiSignalStrengthCount(int i, int i2) {
        return this.mWifiSignalStrengthsTimer[i].getCountLocked(i2);
    }

    public Timer getWifiSignalStrengthTimer(int i) {
        return this.mWifiSignalStrengthsTimer[i];
    }

    public BatteryStats.ControllerActivityCounter getBluetoothControllerActivity() {
        return this.mBluetoothActivity;
    }

    public BatteryStats.ControllerActivityCounter getWifiControllerActivity() {
        return this.mWifiActivity;
    }

    public BatteryStats.ControllerActivityCounter getModemControllerActivity() {
        return this.mModemActivity;
    }

    public boolean hasBluetoothActivityReporting() {
        return this.mHasBluetoothReporting;
    }

    public boolean hasWifiActivityReporting() {
        return this.mHasWifiReporting;
    }

    public boolean hasModemActivityReporting() {
        return this.mHasModemReporting;
    }

    public long getFlashlightOnTime(long j, int i) {
        return this.mFlashlightOnTimer.getTotalTimeLocked(j, i);
    }

    public long getFlashlightOnCount(int i) {
        return this.mFlashlightOnTimer.getCountLocked(i);
    }

    public long getCameraOnTime(long j, int i) {
        return this.mCameraOnTimer.getTotalTimeLocked(j, i);
    }

    public long getBluetoothScanTime(long j, int i) {
        return this.mBluetoothScanTimer.getTotalTimeLocked(j, i);
    }

    public long getNetworkActivityBytes(int i, int i2) {
        if (i >= 0) {
            LongSamplingCounter[] longSamplingCounterArr = this.mNetworkByteActivityCounters;
            if (i < longSamplingCounterArr.length) {
                return longSamplingCounterArr[i].getCountLocked(i2);
            }
            return 0L;
        }
        return 0L;
    }

    public long getNetworkActivityPackets(int i, int i2) {
        if (i >= 0) {
            LongSamplingCounter[] longSamplingCounterArr = this.mNetworkPacketActivityCounters;
            if (i < longSamplingCounterArr.length) {
                return longSamplingCounterArr[i].getCountLocked(i2);
            }
            return 0L;
        }
        return 0L;
    }

    @GuardedBy({"this"})
    public long getBluetoothEnergyConsumptionUC() {
        return getPowerBucketConsumptionUC(5);
    }

    @GuardedBy({"this"})
    public long getCpuEnergyConsumptionUC() {
        return getPowerBucketConsumptionUC(3);
    }

    @GuardedBy({"this"})
    public long getGnssEnergyConsumptionUC() {
        return getPowerBucketConsumptionUC(6);
    }

    @GuardedBy({"this"})
    public long getMobileRadioEnergyConsumptionUC() {
        return getPowerBucketConsumptionUC(7);
    }

    @GuardedBy({"this"})
    public long getPhoneEnergyConsumptionUC() {
        return getPowerBucketConsumptionUC(9);
    }

    @GuardedBy({"this"})
    public long getScreenOnEnergyConsumptionUC() {
        return getPowerBucketConsumptionUC(0);
    }

    @GuardedBy({"this"})
    public long getScreenDozeEnergyConsumptionUC() {
        return getPowerBucketConsumptionUC(1);
    }

    @GuardedBy({"this"})
    public long getWifiEnergyConsumptionUC() {
        return getPowerBucketConsumptionUC(4);
    }

    @GuardedBy({"this"})
    public long getCameraEnergyConsumptionUC() {
        return getPowerBucketConsumptionUC(8);
    }

    @GuardedBy({"this"})
    public final long getPowerBucketConsumptionUC(int i) {
        EnergyConsumerStats energyConsumerStats = this.mGlobalEnergyConsumerStats;
        if (energyConsumerStats == null) {
            return -1L;
        }
        return energyConsumerStats.getAccumulatedStandardBucketCharge(i);
    }

    @GuardedBy({"this"})
    public long[] getCustomEnergyConsumerBatteryConsumptionUC() {
        EnergyConsumerStats energyConsumerStats = this.mGlobalEnergyConsumerStats;
        if (energyConsumerStats == null) {
            return null;
        }
        return energyConsumerStats.getAccumulatedCustomBucketCharges();
    }

    @GuardedBy({"this"})
    public String[] getCustomEnergyConsumerNames() {
        EnergyConsumerStats.Config config = this.mEnergyConsumerStatsConfig;
        if (config == null) {
            return new String[0];
        }
        String[] customBucketNames = config.getCustomBucketNames();
        for (int i = 0; i < customBucketNames.length; i++) {
            if (TextUtils.isEmpty(customBucketNames[i])) {
                customBucketNames[i] = "CUSTOM_1000" + i;
            }
        }
        return customBucketNames;
    }

    @GuardedBy({"this"})
    public void recordEnergyConsumerDetailsLocked(long j, long j2, BatteryStats.EnergyConsumerDetails energyConsumerDetails) {
        if (isUsageHistoryEnabled()) {
            this.mHistory.recordEnergyConsumerDetails(j, j2, energyConsumerDetails);
        }
    }

    @GuardedBy({"this"})
    public long getStartClockTime() {
        long currentTimeMillis = this.mClock.currentTimeMillis();
        if (currentTimeMillis <= 31536000000L || this.mStartClockTimeMs >= currentTimeMillis - 31536000000L) {
            long j = this.mStartClockTimeMs;
            if (j <= currentTimeMillis) {
                return j;
            }
        }
        this.mHistory.recordCurrentTimeChange(this.mClock.elapsedRealtime(), this.mClock.uptimeMillis(), currentTimeMillis);
        return currentTimeMillis - (this.mClock.elapsedRealtime() - (this.mRealtimeStartUs / 1000));
    }

    public String getStartPlatformVersion() {
        return this.mStartPlatformVersion;
    }

    public String getEndPlatformVersion() {
        return this.mEndPlatformVersion;
    }

    public boolean getIsOnBattery() {
        return this.mOnBattery;
    }

    public long getStatsStartRealtime() {
        return this.mRealtimeStartUs;
    }

    public SparseArray<? extends BatteryStats.Uid> getUidStats() {
        return this.mUidStats;
    }

    public static <T extends TimeBaseObs> boolean resetIfNotNull(T t, boolean z, long j) {
        if (t != null) {
            return t.reset(z, j);
        }
        return true;
    }

    public static <T extends TimeBaseObs> boolean resetIfNotNull(T[] tArr, boolean z, long j) {
        boolean z2 = true;
        if (tArr != null) {
            for (T t : tArr) {
                z2 &= resetIfNotNull(t, z, j);
            }
        }
        return z2;
    }

    public static <T extends TimeBaseObs> boolean resetIfNotNull(T[][] tArr, boolean z, long j) {
        boolean z2 = true;
        if (tArr != null) {
            for (T[] tArr2 : tArr) {
                z2 &= resetIfNotNull(tArr2, z, j);
            }
        }
        return z2;
    }

    public static boolean resetIfNotNull(ControllerActivityCounterImpl controllerActivityCounterImpl, boolean z, long j) {
        if (controllerActivityCounterImpl != null) {
            controllerActivityCounterImpl.reset(z, j);
            return true;
        }
        return true;
    }

    public static <T extends TimeBaseObs> void detachIfNotNull(T t) {
        if (t != null) {
            t.detach();
        }
    }

    public static <T extends TimeBaseObs> void detachIfNotNull(T[] tArr) {
        if (tArr != null) {
            for (T t : tArr) {
                detachIfNotNull(t);
            }
        }
    }

    public static <T extends TimeBaseObs> void detachIfNotNull(T[][] tArr) {
        if (tArr != null) {
            for (T[] tArr2 : tArr) {
                detachIfNotNull(tArr2);
            }
        }
    }

    public static void detachIfNotNull(ControllerActivityCounterImpl controllerActivityCounterImpl) {
        if (controllerActivityCounterImpl != null) {
            controllerActivityCounterImpl.detach();
        }
    }

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public static class BinderCallStats {
        public Class<? extends Binder> binderClass;
        public long callCount;
        public String methodName;
        public long recordedCallCount;
        public long recordedCpuTimeMicros;
        public int transactionCode;

        public int hashCode() {
            return (this.binderClass.hashCode() * 31) + this.transactionCode;
        }

        public boolean equals(Object obj) {
            if (obj instanceof BinderCallStats) {
                BinderCallStats binderCallStats = (BinderCallStats) obj;
                return this.binderClass.equals(binderCallStats.binderClass) && this.transactionCode == binderCallStats.transactionCode;
            }
            return false;
        }

        @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
        public void ensureMethodName(BinderTransactionNameResolver binderTransactionNameResolver) {
            if (this.methodName == null) {
                this.methodName = binderTransactionNameResolver.getMethodName(this.binderClass, this.transactionCode);
            }
        }

        public String toString() {
            return "BinderCallStats{" + this.binderClass + " transaction=" + this.transactionCode + " callCount=" + this.callCount + " recordedCallCount=" + this.recordedCallCount + " recorderCpuTimeMicros=" + this.recordedCpuTimeMicros + "}";
        }
    }

    /* loaded from: classes2.dex */
    public static class Uid extends BatteryStats.Uid {
        public static BinderCallStats sTempBinderCallStats = new BinderCallStats();
        public DualTimer mAggregatedPartialWakelockTimer;
        public StopwatchTimer mAudioTurnedOnTimer;
        public long mBinderCallCount;
        public ControllerActivityCounterImpl mBluetoothControllerActivity;
        public Counter mBluetoothScanResultBgCounter;
        public Counter mBluetoothScanResultCounter;
        public DualTimer mBluetoothScanTimer;
        public DualTimer mBluetoothUnoptimizedScanTimer;
        public BatteryStatsImpl mBsi;
        public StopwatchTimer mCameraTurnedOnTimer;
        public SparseArray<ChildUid> mChildUids;
        public TimeMultiStateCounter mCpuActiveTimeMs;
        public LongSamplingCounter[][] mCpuClusterSpeedTimesUs;
        public LongSamplingCounterArray mCpuClusterTimesMs;
        public LongSamplingCounterArray mCpuFreqTimeMs;
        public long mCurStepSystemTimeMs;
        public long mCurStepUserTimeMs;
        public StopwatchTimer mFlashlightTurnedOnTimer;
        public StopwatchTimer mForegroundActivityTimer;
        public StopwatchTimer mForegroundServiceTimer;
        public boolean mFullWifiLockOut;
        public StopwatchTimer mFullWifiLockTimer;
        public final OverflowArrayMap<DualTimer> mJobStats;
        public Counter mJobsDeferredCount;
        public Counter mJobsDeferredEventCount;
        public final Counter[] mJobsFreshnessBuckets;
        public LongSamplingCounter mJobsFreshnessTimeMs;
        public long mLastStepSystemTimeMs;
        public long mLastStepUserTimeMs;
        public LongSamplingCounter mMobileRadioActiveCount;
        public TimeMultiStateCounter mMobileRadioActiveTime;
        public LongSamplingCounter mMobileRadioApWakeupCount;
        public ControllerActivityCounterImpl mModemControllerActivity;
        public LongSamplingCounter[] mNetworkByteActivityCounters;
        public LongSamplingCounter[] mNetworkPacketActivityCounters;
        @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
        public final TimeBase mOnBatteryBackgroundTimeBase;
        @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
        public final TimeBase mOnBatteryScreenOffBackgroundTimeBase;
        public TimeInFreqMultiStateCounter mProcStateScreenOffTimeMs;
        public TimeInFreqMultiStateCounter mProcStateTimeMs;
        public StopwatchTimer[] mProcessStateTimer;
        public double mProportionalSystemServiceUsage;
        public LongSamplingCounterArray mScreenOffCpuFreqTimeMs;
        public final OverflowArrayMap<DualTimer> mSyncStats;
        public LongSamplingCounter mSystemCpuTime;
        public long mSystemServiceTimeUs;
        public final int mUid;
        public EnergyConsumerStats mUidEnergyConsumerStats;
        public Counter[] mUserActivityCounters;
        public LongSamplingCounter mUserCpuTime;
        public BatchTimer mVibratorOnTimer;
        public StopwatchTimer mVideoTurnedOnTimer;
        public final OverflowArrayMap<Wakelock> mWakelockStats;
        public StopwatchTimer[] mWifiBatchedScanTimer;
        public ControllerActivityCounterImpl mWifiControllerActivity;
        public StopwatchTimer mWifiMulticastTimer;
        public int mWifiMulticastWakelockCount;
        public LongSamplingCounter mWifiRadioApWakeupCount;
        public boolean mWifiRunning;
        public StopwatchTimer mWifiRunningTimer;
        public boolean mWifiScanStarted;
        public DualTimer mWifiScanTimer;
        public int mWifiBatchedScanBinStarted = -1;
        public int mProcessState = 7;
        public boolean mInForegroundService = false;
        public final ArrayMap<String, SparseIntArray> mJobCompletions = new ArrayMap<>();
        public final SparseArray<Sensor> mSensorStats = new SparseArray<>();
        public final ArrayMap<String, Proc> mProcessStats = new ArrayMap<>();
        public final ArrayMap<String, Pkg> mPackageStats = new ArrayMap<>();
        public final SparseArray<BatteryStats.Uid.Pid> mPids = new SparseArray<>();
        public final ArraySet<BinderCallStats> mBinderCallStats = new ArraySet<>();

        public Uid(BatteryStatsImpl batteryStatsImpl, int i, long j, long j2) {
            this.mBsi = batteryStatsImpl;
            this.mUid = i;
            TimeBase timeBase = new TimeBase(false);
            this.mOnBatteryBackgroundTimeBase = timeBase;
            long j3 = j2 * 1000;
            long j4 = 1000 * j;
            timeBase.init(j3, j4);
            TimeBase timeBase2 = new TimeBase(false);
            this.mOnBatteryScreenOffBackgroundTimeBase = timeBase2;
            timeBase2.init(j3, j4);
            this.mUserCpuTime = new LongSamplingCounter(this.mBsi.mOnBatteryTimeBase);
            this.mSystemCpuTime = new LongSamplingCounter(this.mBsi.mOnBatteryTimeBase);
            this.mCpuClusterTimesMs = new LongSamplingCounterArray(this.mBsi.mOnBatteryTimeBase);
            BatteryStatsImpl batteryStatsImpl2 = this.mBsi;
            Objects.requireNonNull(batteryStatsImpl2);
            this.mWakelockStats = new OverflowArrayMap<Wakelock>(batteryStatsImpl2, i) { // from class: com.android.server.power.stats.BatteryStatsImpl.Uid.1
                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                {
                    super(i);
                    Objects.requireNonNull(batteryStatsImpl2);
                }

                /* JADX WARN: Can't rename method to resolve collision */
                @Override // com.android.server.power.stats.BatteryStatsImpl.OverflowArrayMap
                public Wakelock instantiateObject() {
                    Uid uid = Uid.this;
                    return new Wakelock(uid.mBsi, uid);
                }
            };
            BatteryStatsImpl batteryStatsImpl3 = this.mBsi;
            Objects.requireNonNull(batteryStatsImpl3);
            this.mSyncStats = new OverflowArrayMap<DualTimer>(batteryStatsImpl3, i) { // from class: com.android.server.power.stats.BatteryStatsImpl.Uid.2
                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                {
                    super(i);
                    Objects.requireNonNull(batteryStatsImpl3);
                }

                /* JADX WARN: Can't rename method to resolve collision */
                @Override // com.android.server.power.stats.BatteryStatsImpl.OverflowArrayMap
                public DualTimer instantiateObject() {
                    Uid uid = Uid.this;
                    BatteryStatsImpl batteryStatsImpl4 = uid.mBsi;
                    return new DualTimer(batteryStatsImpl4.mClock, uid, 13, null, batteryStatsImpl4.mOnBatteryTimeBase, uid.mOnBatteryBackgroundTimeBase);
                }
            };
            BatteryStatsImpl batteryStatsImpl4 = this.mBsi;
            Objects.requireNonNull(batteryStatsImpl4);
            this.mJobStats = new OverflowArrayMap<DualTimer>(batteryStatsImpl4, i) { // from class: com.android.server.power.stats.BatteryStatsImpl.Uid.3
                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                {
                    super(i);
                    Objects.requireNonNull(batteryStatsImpl4);
                }

                /* JADX WARN: Can't rename method to resolve collision */
                @Override // com.android.server.power.stats.BatteryStatsImpl.OverflowArrayMap
                public DualTimer instantiateObject() {
                    Uid uid = Uid.this;
                    BatteryStatsImpl batteryStatsImpl5 = uid.mBsi;
                    return new DualTimer(batteryStatsImpl5.mClock, uid, 14, null, batteryStatsImpl5.mOnBatteryTimeBase, uid.mOnBatteryBackgroundTimeBase);
                }
            };
            BatteryStatsImpl batteryStatsImpl5 = this.mBsi;
            this.mWifiRunningTimer = new StopwatchTimer(batteryStatsImpl5.mClock, this, 4, batteryStatsImpl5.mWifiRunningTimers, this.mBsi.mOnBatteryTimeBase);
            BatteryStatsImpl batteryStatsImpl6 = this.mBsi;
            this.mFullWifiLockTimer = new StopwatchTimer(batteryStatsImpl6.mClock, this, 5, batteryStatsImpl6.mFullWifiLockTimers, this.mBsi.mOnBatteryTimeBase);
            BatteryStatsImpl batteryStatsImpl7 = this.mBsi;
            this.mWifiScanTimer = new DualTimer(batteryStatsImpl7.mClock, this, 6, batteryStatsImpl7.mWifiScanTimers, this.mBsi.mOnBatteryTimeBase, timeBase);
            this.mWifiBatchedScanTimer = new StopwatchTimer[5];
            BatteryStatsImpl batteryStatsImpl8 = this.mBsi;
            this.mWifiMulticastTimer = new StopwatchTimer(batteryStatsImpl8.mClock, this, 7, batteryStatsImpl8.mWifiMulticastTimers, this.mBsi.mOnBatteryTimeBase);
            this.mProcessStateTimer = new StopwatchTimer[7];
            this.mJobsDeferredEventCount = new Counter(this.mBsi.mOnBatteryTimeBase);
            this.mJobsDeferredCount = new Counter(this.mBsi.mOnBatteryTimeBase);
            this.mJobsFreshnessTimeMs = new LongSamplingCounter(this.mBsi.mOnBatteryTimeBase);
            this.mJobsFreshnessBuckets = new Counter[BatteryStats.JOB_FRESHNESS_BUCKETS.length];
        }

        @GuardedBy({"mBsi"})
        @VisibleForTesting
        public void setProcessStateForTest(int i, long j) {
            this.mProcessState = i;
            getProcStateTimeCounter(j).setState(i, j);
            getProcStateScreenOffTimeCounter(j).setState(i, j);
            int mapUidProcessStateToBatteryConsumerProcessState = BatteryStats.mapUidProcessStateToBatteryConsumerProcessState(i);
            getCpuActiveTimeCounter().setState(mapUidProcessStateToBatteryConsumerProcessState, j);
            getMobileRadioActiveTimeCounter().setState(mapUidProcessStateToBatteryConsumerProcessState, j);
            ControllerActivityCounterImpl wifiControllerActivity = getWifiControllerActivity();
            if (wifiControllerActivity != null) {
                wifiControllerActivity.setState(mapUidProcessStateToBatteryConsumerProcessState, j);
            }
            ControllerActivityCounterImpl bluetoothControllerActivity = getBluetoothControllerActivity();
            if (bluetoothControllerActivity != null) {
                bluetoothControllerActivity.setState(mapUidProcessStateToBatteryConsumerProcessState, j);
            }
            EnergyConsumerStats orCreateEnergyConsumerStatsIfSupportedLocked = getOrCreateEnergyConsumerStatsIfSupportedLocked();
            if (orCreateEnergyConsumerStatsIfSupportedLocked != null) {
                orCreateEnergyConsumerStatsIfSupportedLocked.setState(mapUidProcessStateToBatteryConsumerProcessState, j);
            }
        }

        public long[] getCpuFreqTimes(int i) {
            return nullIfAllZeros(this.mCpuFreqTimeMs, i);
        }

        public long[] getScreenOffCpuFreqTimes(int i) {
            return nullIfAllZeros(this.mScreenOffCpuFreqTimeMs, i);
        }

        public final TimeMultiStateCounter getCpuActiveTimeCounter() {
            if (this.mCpuActiveTimeMs == null) {
                long elapsedRealtime = this.mBsi.mClock.elapsedRealtime();
                TimeMultiStateCounter timeMultiStateCounter = new TimeMultiStateCounter(this.mBsi.mOnBatteryTimeBase, 5, elapsedRealtime);
                this.mCpuActiveTimeMs = timeMultiStateCounter;
                timeMultiStateCounter.setState(BatteryStats.mapUidProcessStateToBatteryConsumerProcessState(this.mProcessState), elapsedRealtime);
            }
            return this.mCpuActiveTimeMs;
        }

        public long getCpuActiveTime() {
            long j = 0;
            if (this.mCpuActiveTimeMs == null) {
                return 0L;
            }
            for (int i = 0; i < 5; i++) {
                j += this.mCpuActiveTimeMs.getCountForProcessState(i);
            }
            return j;
        }

        public long getCpuActiveTime(int i) {
            TimeMultiStateCounter timeMultiStateCounter = this.mCpuActiveTimeMs;
            if (timeMultiStateCounter == null || i < 0 || i >= 5) {
                return 0L;
            }
            return timeMultiStateCounter.getCountForProcessState(i);
        }

        public long[] getCpuClusterTimes() {
            return nullIfAllZeros(this.mCpuClusterTimesMs, 0);
        }

        @GuardedBy({"mBsi"})
        public boolean getCpuFreqTimes(long[] jArr, int i) {
            TimeInFreqMultiStateCounter timeInFreqMultiStateCounter;
            if (i < 0 || i >= 7 || (timeInFreqMultiStateCounter = this.mProcStateTimeMs) == null) {
                return false;
            }
            if (!this.mBsi.mPerProcStateCpuTimesAvailable) {
                this.mProcStateTimeMs = null;
                return false;
            }
            return timeInFreqMultiStateCounter.getCountsLocked(jArr, i);
        }

        @GuardedBy({"mBsi"})
        public boolean getScreenOffCpuFreqTimes(long[] jArr, int i) {
            TimeInFreqMultiStateCounter timeInFreqMultiStateCounter;
            if (i < 0 || i >= 7 || (timeInFreqMultiStateCounter = this.mProcStateScreenOffTimeMs) == null) {
                return false;
            }
            if (!this.mBsi.mPerProcStateCpuTimesAvailable) {
                this.mProcStateScreenOffTimeMs = null;
                return false;
            }
            return timeInFreqMultiStateCounter.getCountsLocked(jArr, i);
        }

        @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
        public ArraySet<BinderCallStats> getBinderCallStats() {
            return this.mBinderCallStats;
        }

        public double getProportionalSystemServiceUsage() {
            return this.mProportionalSystemServiceUsage;
        }

        @GuardedBy({"mBsi"})
        public void addIsolatedUid(int i) {
            SparseArray<ChildUid> sparseArray = this.mChildUids;
            if (sparseArray == null) {
                this.mChildUids = new SparseArray<>();
            } else if (sparseArray.indexOfKey(i) >= 0) {
                return;
            }
            this.mChildUids.put(i, new ChildUid());
        }

        public void removeIsolatedUid(int i) {
            SparseArray<ChildUid> sparseArray = this.mChildUids;
            int indexOfKey = sparseArray == null ? -1 : sparseArray.indexOfKey(i);
            if (indexOfKey < 0) {
                return;
            }
            this.mChildUids.remove(indexOfKey);
        }

        @GuardedBy({"mBsi"})
        public ChildUid getChildUid(int i) {
            SparseArray<ChildUid> sparseArray = this.mChildUids;
            if (sparseArray == null) {
                return null;
            }
            return sparseArray.get(i);
        }

        public final long[] nullIfAllZeros(LongSamplingCounterArray longSamplingCounterArray, int i) {
            long[] countsLocked;
            if (longSamplingCounterArray == null || (countsLocked = longSamplingCounterArray.getCountsLocked(i)) == null) {
                return null;
            }
            for (int length = countsLocked.length - 1; length >= 0; length--) {
                if (countsLocked[length] != 0) {
                    return countsLocked;
                }
            }
            return null;
        }

        @GuardedBy({"mBsi"})
        public final void ensureMultiStateCounters(long j) {
            if (this.mProcStateTimeMs != null) {
                return;
            }
            BatteryStatsImpl batteryStatsImpl = this.mBsi;
            this.mProcStateTimeMs = new TimeInFreqMultiStateCounter(batteryStatsImpl.mOnBatteryTimeBase, 8, batteryStatsImpl.getCpuFreqCount(), j);
            BatteryStatsImpl batteryStatsImpl2 = this.mBsi;
            this.mProcStateScreenOffTimeMs = new TimeInFreqMultiStateCounter(batteryStatsImpl2.mOnBatteryScreenOffTimeBase, 8, batteryStatsImpl2.getCpuFreqCount(), j);
        }

        @GuardedBy({"mBsi"})
        public final TimeInFreqMultiStateCounter getProcStateTimeCounter(long j) {
            ensureMultiStateCounters(j);
            return this.mProcStateTimeMs;
        }

        @GuardedBy({"mBsi"})
        public final TimeInFreqMultiStateCounter getProcStateScreenOffTimeCounter(long j) {
            ensureMultiStateCounters(j);
            return this.mProcStateScreenOffTimeMs;
        }

        public Timer getAggregatedPartialWakelockTimer() {
            return this.mAggregatedPartialWakelockTimer;
        }

        public ArrayMap<String, ? extends BatteryStats.Uid.Wakelock> getWakelockStats() {
            return this.mWakelockStats.getMap();
        }

        public Timer getMulticastWakelockStats() {
            return this.mWifiMulticastTimer;
        }

        public ArrayMap<String, ? extends BatteryStats.Timer> getSyncStats() {
            return this.mSyncStats.getMap();
        }

        public ArrayMap<String, ? extends BatteryStats.Timer> getJobStats() {
            return this.mJobStats.getMap();
        }

        public ArrayMap<String, SparseIntArray> getJobCompletionStats() {
            return this.mJobCompletions;
        }

        public SparseArray<? extends BatteryStats.Uid.Sensor> getSensorStats() {
            return this.mSensorStats;
        }

        public ArrayMap<String, ? extends BatteryStats.Uid.Proc> getProcessStats() {
            return this.mProcessStats;
        }

        public ArrayMap<String, ? extends BatteryStats.Uid.Pkg> getPackageStats() {
            return this.mPackageStats;
        }

        public int getUid() {
            return this.mUid;
        }

        public void noteWifiRunningLocked(long j) {
            if (this.mWifiRunning) {
                return;
            }
            this.mWifiRunning = true;
            if (this.mWifiRunningTimer == null) {
                BatteryStatsImpl batteryStatsImpl = this.mBsi;
                this.mWifiRunningTimer = new StopwatchTimer(batteryStatsImpl.mClock, this, 4, batteryStatsImpl.mWifiRunningTimers, this.mBsi.mOnBatteryTimeBase);
            }
            this.mWifiRunningTimer.startRunningLocked(j);
        }

        public void noteWifiStoppedLocked(long j) {
            if (this.mWifiRunning) {
                this.mWifiRunning = false;
                this.mWifiRunningTimer.stopRunningLocked(j);
            }
        }

        public void noteFullWifiLockAcquiredLocked(long j) {
            if (this.mFullWifiLockOut) {
                return;
            }
            this.mFullWifiLockOut = true;
            if (this.mFullWifiLockTimer == null) {
                BatteryStatsImpl batteryStatsImpl = this.mBsi;
                this.mFullWifiLockTimer = new StopwatchTimer(batteryStatsImpl.mClock, this, 5, batteryStatsImpl.mFullWifiLockTimers, this.mBsi.mOnBatteryTimeBase);
            }
            this.mFullWifiLockTimer.startRunningLocked(j);
        }

        public void noteFullWifiLockReleasedLocked(long j) {
            if (this.mFullWifiLockOut) {
                this.mFullWifiLockOut = false;
                this.mFullWifiLockTimer.stopRunningLocked(j);
            }
        }

        public void noteWifiScanStartedLocked(long j) {
            if (this.mWifiScanStarted) {
                return;
            }
            this.mWifiScanStarted = true;
            if (this.mWifiScanTimer == null) {
                BatteryStatsImpl batteryStatsImpl = this.mBsi;
                this.mWifiScanTimer = new DualTimer(batteryStatsImpl.mClock, this, 6, batteryStatsImpl.mWifiScanTimers, this.mBsi.mOnBatteryTimeBase, this.mOnBatteryBackgroundTimeBase);
            }
            this.mWifiScanTimer.startRunningLocked(j);
        }

        public void noteWifiScanStoppedLocked(long j) {
            if (this.mWifiScanStarted) {
                this.mWifiScanStarted = false;
                this.mWifiScanTimer.stopRunningLocked(j);
            }
        }

        public void noteWifiBatchedScanStartedLocked(int i, long j) {
            int i2 = 0;
            while (i > 8 && i2 < 4) {
                i >>= 3;
                i2++;
            }
            int i3 = this.mWifiBatchedScanBinStarted;
            if (i3 == i2) {
                return;
            }
            if (i3 != -1) {
                this.mWifiBatchedScanTimer[i3].stopRunningLocked(j);
            }
            this.mWifiBatchedScanBinStarted = i2;
            if (this.mWifiBatchedScanTimer[i2] == null) {
                makeWifiBatchedScanBin(i2, null);
            }
            this.mWifiBatchedScanTimer[i2].startRunningLocked(j);
        }

        public void noteWifiBatchedScanStoppedLocked(long j) {
            int i = this.mWifiBatchedScanBinStarted;
            if (i != -1) {
                this.mWifiBatchedScanTimer[i].stopRunningLocked(j);
                this.mWifiBatchedScanBinStarted = -1;
            }
        }

        public void noteWifiMulticastEnabledLocked(long j) {
            if (this.mWifiMulticastWakelockCount == 0) {
                if (this.mWifiMulticastTimer == null) {
                    BatteryStatsImpl batteryStatsImpl = this.mBsi;
                    this.mWifiMulticastTimer = new StopwatchTimer(batteryStatsImpl.mClock, this, 7, batteryStatsImpl.mWifiMulticastTimers, this.mBsi.mOnBatteryTimeBase);
                }
                this.mWifiMulticastTimer.startRunningLocked(j);
            }
            this.mWifiMulticastWakelockCount++;
        }

        public void noteWifiMulticastDisabledLocked(long j) {
            int i = this.mWifiMulticastWakelockCount;
            if (i == 0) {
                return;
            }
            int i2 = i - 1;
            this.mWifiMulticastWakelockCount = i2;
            if (i2 == 0) {
                this.mWifiMulticastTimer.stopRunningLocked(j);
            }
        }

        public ControllerActivityCounterImpl getWifiControllerActivity() {
            return this.mWifiControllerActivity;
        }

        public ControllerActivityCounterImpl getBluetoothControllerActivity() {
            return this.mBluetoothControllerActivity;
        }

        public BatteryStats.ControllerActivityCounter getModemControllerActivity() {
            return this.mModemControllerActivity;
        }

        public ControllerActivityCounterImpl getOrCreateWifiControllerActivityLocked() {
            if (this.mWifiControllerActivity == null) {
                BatteryStatsImpl batteryStatsImpl = this.mBsi;
                this.mWifiControllerActivity = new ControllerActivityCounterImpl(batteryStatsImpl.mClock, batteryStatsImpl.mOnBatteryTimeBase, 1);
            }
            return this.mWifiControllerActivity;
        }

        public ControllerActivityCounterImpl getOrCreateBluetoothControllerActivityLocked() {
            if (this.mBluetoothControllerActivity == null) {
                BatteryStatsImpl batteryStatsImpl = this.mBsi;
                this.mBluetoothControllerActivity = new ControllerActivityCounterImpl(batteryStatsImpl.mClock, batteryStatsImpl.mOnBatteryTimeBase, 1);
            }
            return this.mBluetoothControllerActivity;
        }

        public ControllerActivityCounterImpl getOrCreateModemControllerActivityLocked() {
            if (this.mModemControllerActivity == null) {
                BatteryStatsImpl batteryStatsImpl = this.mBsi;
                this.mModemControllerActivity = new ControllerActivityCounterImpl(batteryStatsImpl.mClock, batteryStatsImpl.mOnBatteryTimeBase, ModemActivityInfo.getNumTxPowerLevels());
            }
            return this.mModemControllerActivity;
        }

        @GuardedBy({"mBsi"})
        public final EnergyConsumerStats getOrCreateEnergyConsumerStatsLocked() {
            if (this.mUidEnergyConsumerStats == null) {
                this.mUidEnergyConsumerStats = new EnergyConsumerStats(this.mBsi.mEnergyConsumerStatsConfig);
            }
            return this.mUidEnergyConsumerStats;
        }

        @GuardedBy({"mBsi"})
        public final EnergyConsumerStats getOrCreateEnergyConsumerStatsIfSupportedLocked() {
            if (this.mUidEnergyConsumerStats == null && this.mBsi.mEnergyConsumerStatsConfig != null) {
                this.mUidEnergyConsumerStats = new EnergyConsumerStats(this.mBsi.mEnergyConsumerStatsConfig);
            }
            return this.mUidEnergyConsumerStats;
        }

        @GuardedBy({"mBsi"})
        public final void addChargeToStandardBucketLocked(long j, int i, long j2) {
            getOrCreateEnergyConsumerStatsLocked().updateStandardBucket(i, j, j2);
        }

        @GuardedBy({"mBsi"})
        public final void addChargeToCustomBucketLocked(long j, int i) {
            getOrCreateEnergyConsumerStatsLocked().updateCustomBucket(i, j, this.mBsi.mClock.elapsedRealtime());
        }

        @GuardedBy({"mBsi"})
        public long getEnergyConsumptionUC(int i) {
            EnergyConsumerStats energyConsumerStats = this.mBsi.mGlobalEnergyConsumerStats;
            if (energyConsumerStats == null || !energyConsumerStats.isStandardBucketSupported(i)) {
                return -1L;
            }
            EnergyConsumerStats energyConsumerStats2 = this.mUidEnergyConsumerStats;
            if (energyConsumerStats2 == null) {
                return 0L;
            }
            return energyConsumerStats2.getAccumulatedStandardBucketCharge(i);
        }

        @GuardedBy({"mBsi"})
        public long getEnergyConsumptionUC(int i, int i2) {
            EnergyConsumerStats energyConsumerStats = this.mBsi.mGlobalEnergyConsumerStats;
            if (energyConsumerStats == null || !energyConsumerStats.isStandardBucketSupported(i)) {
                return -1L;
            }
            EnergyConsumerStats energyConsumerStats2 = this.mUidEnergyConsumerStats;
            if (energyConsumerStats2 == null) {
                return 0L;
            }
            return energyConsumerStats2.getAccumulatedStandardBucketCharge(i, i2);
        }

        @GuardedBy({"mBsi"})
        public long[] getCustomEnergyConsumerBatteryConsumptionUC() {
            EnergyConsumerStats energyConsumerStats = this.mBsi.mGlobalEnergyConsumerStats;
            if (energyConsumerStats == null) {
                return null;
            }
            EnergyConsumerStats energyConsumerStats2 = this.mUidEnergyConsumerStats;
            if (energyConsumerStats2 == null) {
                return new long[energyConsumerStats.getNumberCustomPowerBuckets()];
            }
            return energyConsumerStats2.getAccumulatedCustomBucketCharges();
        }

        @GuardedBy({"mBsi"})
        public long getBluetoothEnergyConsumptionUC() {
            return getEnergyConsumptionUC(5);
        }

        @GuardedBy({"mBsi"})
        public long getBluetoothEnergyConsumptionUC(int i) {
            return getEnergyConsumptionUC(5, i);
        }

        @GuardedBy({"mBsi"})
        public long getCpuEnergyConsumptionUC() {
            return getEnergyConsumptionUC(3);
        }

        @GuardedBy({"mBsi"})
        public long getCpuEnergyConsumptionUC(int i) {
            return getEnergyConsumptionUC(3, i);
        }

        @GuardedBy({"mBsi"})
        public long getGnssEnergyConsumptionUC() {
            return getEnergyConsumptionUC(6);
        }

        @GuardedBy({"mBsi"})
        public long getMobileRadioEnergyConsumptionUC() {
            return getEnergyConsumptionUC(7);
        }

        @GuardedBy({"mBsi"})
        public long getMobileRadioEnergyConsumptionUC(int i) {
            return getEnergyConsumptionUC(7, i);
        }

        @GuardedBy({"mBsi"})
        public long getScreenOnEnergyConsumptionUC() {
            return getEnergyConsumptionUC(0);
        }

        @GuardedBy({"mBsi"})
        public long getWifiEnergyConsumptionUC() {
            return getEnergyConsumptionUC(4);
        }

        @GuardedBy({"mBsi"})
        public long getWifiEnergyConsumptionUC(int i) {
            return getEnergyConsumptionUC(4, i);
        }

        @GuardedBy({"mBsi"})
        public long getCameraEnergyConsumptionUC() {
            return getEnergyConsumptionUC(8);
        }

        public final long markProcessForegroundTimeUs(long j, boolean z) {
            long j2;
            StopwatchTimer stopwatchTimer = this.mForegroundActivityTimer;
            if (stopwatchTimer != null) {
                j2 = z ? stopwatchTimer.getTimeSinceMarkLocked(j * 1000) : 0L;
                stopwatchTimer.setMark(j);
            } else {
                j2 = 0;
            }
            StopwatchTimer stopwatchTimer2 = this.mProcessStateTimer[0];
            if (stopwatchTimer2 != null) {
                r3 = z ? stopwatchTimer2.getTimeSinceMarkLocked(1000 * j) : 0L;
                stopwatchTimer2.setMark(j);
            }
            return r3 < j2 ? r3 : j2;
        }

        public final long markGnssTimeUs(long j) {
            DualTimer dualTimer;
            Sensor sensor = this.mSensorStats.get(-10000);
            if (sensor == null || (dualTimer = sensor.mTimer) == null) {
                return 0L;
            }
            long timeSinceMarkLocked = dualTimer.getTimeSinceMarkLocked(1000 * j);
            dualTimer.setMark(j);
            return timeSinceMarkLocked;
        }

        public final long markCameraTimeUs(long j) {
            StopwatchTimer stopwatchTimer = this.mCameraTurnedOnTimer;
            if (stopwatchTimer == null) {
                return 0L;
            }
            long timeSinceMarkLocked = stopwatchTimer.getTimeSinceMarkLocked(1000 * j);
            stopwatchTimer.setMark(j);
            return timeSinceMarkLocked;
        }

        public StopwatchTimer createAudioTurnedOnTimerLocked() {
            if (this.mAudioTurnedOnTimer == null) {
                BatteryStatsImpl batteryStatsImpl = this.mBsi;
                this.mAudioTurnedOnTimer = new StopwatchTimer(batteryStatsImpl.mClock, this, 15, batteryStatsImpl.mAudioTurnedOnTimers, this.mBsi.mOnBatteryTimeBase);
            }
            return this.mAudioTurnedOnTimer;
        }

        public void noteAudioTurnedOnLocked(long j) {
            createAudioTurnedOnTimerLocked().startRunningLocked(j);
        }

        public void noteAudioTurnedOffLocked(long j) {
            StopwatchTimer stopwatchTimer = this.mAudioTurnedOnTimer;
            if (stopwatchTimer != null) {
                stopwatchTimer.stopRunningLocked(j);
            }
        }

        public void noteResetAudioLocked(long j) {
            StopwatchTimer stopwatchTimer = this.mAudioTurnedOnTimer;
            if (stopwatchTimer != null) {
                stopwatchTimer.stopAllRunningLocked(j);
            }
        }

        public StopwatchTimer createVideoTurnedOnTimerLocked() {
            if (this.mVideoTurnedOnTimer == null) {
                BatteryStatsImpl batteryStatsImpl = this.mBsi;
                this.mVideoTurnedOnTimer = new StopwatchTimer(batteryStatsImpl.mClock, this, 8, batteryStatsImpl.mVideoTurnedOnTimers, this.mBsi.mOnBatteryTimeBase);
            }
            return this.mVideoTurnedOnTimer;
        }

        public void noteVideoTurnedOnLocked(long j) {
            createVideoTurnedOnTimerLocked().startRunningLocked(j);
        }

        public void noteVideoTurnedOffLocked(long j) {
            StopwatchTimer stopwatchTimer = this.mVideoTurnedOnTimer;
            if (stopwatchTimer != null) {
                stopwatchTimer.stopRunningLocked(j);
            }
        }

        public void noteResetVideoLocked(long j) {
            StopwatchTimer stopwatchTimer = this.mVideoTurnedOnTimer;
            if (stopwatchTimer != null) {
                stopwatchTimer.stopAllRunningLocked(j);
            }
        }

        public StopwatchTimer createFlashlightTurnedOnTimerLocked() {
            if (this.mFlashlightTurnedOnTimer == null) {
                BatteryStatsImpl batteryStatsImpl = this.mBsi;
                this.mFlashlightTurnedOnTimer = new StopwatchTimer(batteryStatsImpl.mClock, this, 16, batteryStatsImpl.mFlashlightTurnedOnTimers, this.mBsi.mOnBatteryTimeBase);
            }
            return this.mFlashlightTurnedOnTimer;
        }

        public void noteFlashlightTurnedOnLocked(long j) {
            createFlashlightTurnedOnTimerLocked().startRunningLocked(j);
        }

        public void noteFlashlightTurnedOffLocked(long j) {
            StopwatchTimer stopwatchTimer = this.mFlashlightTurnedOnTimer;
            if (stopwatchTimer != null) {
                stopwatchTimer.stopRunningLocked(j);
            }
        }

        public void noteResetFlashlightLocked(long j) {
            StopwatchTimer stopwatchTimer = this.mFlashlightTurnedOnTimer;
            if (stopwatchTimer != null) {
                stopwatchTimer.stopAllRunningLocked(j);
            }
        }

        public StopwatchTimer createCameraTurnedOnTimerLocked() {
            if (this.mCameraTurnedOnTimer == null) {
                BatteryStatsImpl batteryStatsImpl = this.mBsi;
                this.mCameraTurnedOnTimer = new StopwatchTimer(batteryStatsImpl.mClock, this, 17, batteryStatsImpl.mCameraTurnedOnTimers, this.mBsi.mOnBatteryTimeBase);
            }
            return this.mCameraTurnedOnTimer;
        }

        public void noteCameraTurnedOnLocked(long j) {
            createCameraTurnedOnTimerLocked().startRunningLocked(j);
        }

        public void noteCameraTurnedOffLocked(long j) {
            StopwatchTimer stopwatchTimer = this.mCameraTurnedOnTimer;
            if (stopwatchTimer != null) {
                stopwatchTimer.stopRunningLocked(j);
            }
        }

        public void noteResetCameraLocked(long j) {
            StopwatchTimer stopwatchTimer = this.mCameraTurnedOnTimer;
            if (stopwatchTimer != null) {
                stopwatchTimer.stopAllRunningLocked(j);
            }
        }

        public StopwatchTimer createForegroundActivityTimerLocked() {
            if (this.mForegroundActivityTimer == null) {
                BatteryStatsImpl batteryStatsImpl = this.mBsi;
                this.mForegroundActivityTimer = new StopwatchTimer(batteryStatsImpl.mClock, this, 10, null, batteryStatsImpl.mOnBatteryTimeBase);
            }
            return this.mForegroundActivityTimer;
        }

        public StopwatchTimer createForegroundServiceTimerLocked() {
            if (this.mForegroundServiceTimer == null) {
                BatteryStatsImpl batteryStatsImpl = this.mBsi;
                this.mForegroundServiceTimer = new StopwatchTimer(batteryStatsImpl.mClock, this, 22, null, batteryStatsImpl.mOnBatteryTimeBase);
            }
            return this.mForegroundServiceTimer;
        }

        public DualTimer createAggregatedPartialWakelockTimerLocked() {
            if (this.mAggregatedPartialWakelockTimer == null) {
                BatteryStatsImpl batteryStatsImpl = this.mBsi;
                this.mAggregatedPartialWakelockTimer = new DualTimer(batteryStatsImpl.mClock, this, 20, null, batteryStatsImpl.mOnBatteryScreenOffTimeBase, this.mOnBatteryScreenOffBackgroundTimeBase);
            }
            return this.mAggregatedPartialWakelockTimer;
        }

        public DualTimer createBluetoothScanTimerLocked() {
            if (this.mBluetoothScanTimer == null) {
                BatteryStatsImpl batteryStatsImpl = this.mBsi;
                this.mBluetoothScanTimer = new DualTimer(batteryStatsImpl.mClock, this, 19, batteryStatsImpl.mBluetoothScanOnTimers, this.mBsi.mOnBatteryTimeBase, this.mOnBatteryBackgroundTimeBase);
            }
            return this.mBluetoothScanTimer;
        }

        public DualTimer createBluetoothUnoptimizedScanTimerLocked() {
            if (this.mBluetoothUnoptimizedScanTimer == null) {
                BatteryStatsImpl batteryStatsImpl = this.mBsi;
                this.mBluetoothUnoptimizedScanTimer = new DualTimer(batteryStatsImpl.mClock, this, 21, null, batteryStatsImpl.mOnBatteryTimeBase, this.mOnBatteryBackgroundTimeBase);
            }
            return this.mBluetoothUnoptimizedScanTimer;
        }

        public void noteBluetoothScanStartedLocked(long j, boolean z) {
            createBluetoothScanTimerLocked().startRunningLocked(j);
            if (z) {
                createBluetoothUnoptimizedScanTimerLocked().startRunningLocked(j);
            }
        }

        public void noteBluetoothScanStoppedLocked(long j, boolean z) {
            DualTimer dualTimer;
            DualTimer dualTimer2 = this.mBluetoothScanTimer;
            if (dualTimer2 != null) {
                dualTimer2.stopRunningLocked(j);
            }
            if (!z || (dualTimer = this.mBluetoothUnoptimizedScanTimer) == null) {
                return;
            }
            dualTimer.stopRunningLocked(j);
        }

        public void noteResetBluetoothScanLocked(long j) {
            DualTimer dualTimer = this.mBluetoothScanTimer;
            if (dualTimer != null) {
                dualTimer.stopAllRunningLocked(j);
            }
            DualTimer dualTimer2 = this.mBluetoothUnoptimizedScanTimer;
            if (dualTimer2 != null) {
                dualTimer2.stopAllRunningLocked(j);
            }
        }

        public Counter createBluetoothScanResultCounterLocked() {
            if (this.mBluetoothScanResultCounter == null) {
                this.mBluetoothScanResultCounter = new Counter(this.mBsi.mOnBatteryTimeBase);
            }
            return this.mBluetoothScanResultCounter;
        }

        public Counter createBluetoothScanResultBgCounterLocked() {
            if (this.mBluetoothScanResultBgCounter == null) {
                this.mBluetoothScanResultBgCounter = new Counter(this.mOnBatteryBackgroundTimeBase);
            }
            return this.mBluetoothScanResultBgCounter;
        }

        public void noteBluetoothScanResultsLocked(int i) {
            createBluetoothScanResultCounterLocked().addAtomic(i);
            createBluetoothScanResultBgCounterLocked().addAtomic(i);
        }

        public void noteActivityResumedLocked(long j) {
            createForegroundActivityTimerLocked().startRunningLocked(j);
        }

        public void noteActivityPausedLocked(long j) {
            StopwatchTimer stopwatchTimer = this.mForegroundActivityTimer;
            if (stopwatchTimer != null) {
                stopwatchTimer.stopRunningLocked(j);
            }
        }

        public void noteForegroundServiceResumedLocked(long j) {
            createForegroundServiceTimerLocked().startRunningLocked(j);
        }

        public void noteForegroundServicePausedLocked(long j) {
            StopwatchTimer stopwatchTimer = this.mForegroundServiceTimer;
            if (stopwatchTimer != null) {
                stopwatchTimer.stopRunningLocked(j);
            }
        }

        public BatchTimer createVibratorOnTimerLocked() {
            if (this.mVibratorOnTimer == null) {
                BatteryStatsImpl batteryStatsImpl = this.mBsi;
                this.mVibratorOnTimer = new BatchTimer(batteryStatsImpl.mClock, this, 9, batteryStatsImpl.mOnBatteryTimeBase);
            }
            return this.mVibratorOnTimer;
        }

        public void noteVibratorOnLocked(long j, long j2) {
            createVibratorOnTimerLocked().addDuration(j, j2);
        }

        public void noteVibratorOffLocked(long j) {
            BatchTimer batchTimer = this.mVibratorOnTimer;
            if (batchTimer != null) {
                batchTimer.abortLastDuration(j);
            }
        }

        public long getWifiRunningTime(long j, int i) {
            StopwatchTimer stopwatchTimer = this.mWifiRunningTimer;
            if (stopwatchTimer == null) {
                return 0L;
            }
            return stopwatchTimer.getTotalTimeLocked(j, i);
        }

        public long getFullWifiLockTime(long j, int i) {
            StopwatchTimer stopwatchTimer = this.mFullWifiLockTimer;
            if (stopwatchTimer == null) {
                return 0L;
            }
            return stopwatchTimer.getTotalTimeLocked(j, i);
        }

        public long getWifiScanTime(long j, int i) {
            DualTimer dualTimer = this.mWifiScanTimer;
            if (dualTimer == null) {
                return 0L;
            }
            return dualTimer.getTotalTimeLocked(j, i);
        }

        public int getWifiScanCount(int i) {
            DualTimer dualTimer = this.mWifiScanTimer;
            if (dualTimer == null) {
                return 0;
            }
            return dualTimer.getCountLocked(i);
        }

        public Timer getWifiScanTimer() {
            return this.mWifiScanTimer;
        }

        public int getWifiScanBackgroundCount(int i) {
            DualTimer dualTimer = this.mWifiScanTimer;
            if (dualTimer == null || dualTimer.getSubTimer() == null) {
                return 0;
            }
            return this.mWifiScanTimer.getSubTimer().getCountLocked(i);
        }

        public long getWifiScanActualTime(long j) {
            DualTimer dualTimer = this.mWifiScanTimer;
            if (dualTimer == null) {
                return 0L;
            }
            return dualTimer.getTotalDurationMsLocked((j + 500) / 1000) * 1000;
        }

        public long getWifiScanBackgroundTime(long j) {
            DualTimer dualTimer = this.mWifiScanTimer;
            if (dualTimer == null || dualTimer.getSubTimer() == null) {
                return 0L;
            }
            return this.mWifiScanTimer.getSubTimer().getTotalDurationMsLocked((j + 500) / 1000) * 1000;
        }

        public Timer getWifiScanBackgroundTimer() {
            DualTimer dualTimer = this.mWifiScanTimer;
            if (dualTimer == null) {
                return null;
            }
            return dualTimer.getSubTimer();
        }

        public long getWifiBatchedScanTime(int i, long j, int i2) {
            StopwatchTimer stopwatchTimer;
            if (i < 0 || i >= 5 || (stopwatchTimer = this.mWifiBatchedScanTimer[i]) == null) {
                return 0L;
            }
            return stopwatchTimer.getTotalTimeLocked(j, i2);
        }

        public int getWifiBatchedScanCount(int i, int i2) {
            StopwatchTimer stopwatchTimer;
            if (i < 0 || i >= 5 || (stopwatchTimer = this.mWifiBatchedScanTimer[i]) == null) {
                return 0;
            }
            return stopwatchTimer.getCountLocked(i2);
        }

        public long getWifiMulticastTime(long j, int i) {
            StopwatchTimer stopwatchTimer = this.mWifiMulticastTimer;
            if (stopwatchTimer == null) {
                return 0L;
            }
            return stopwatchTimer.getTotalTimeLocked(j, i);
        }

        public Timer getAudioTurnedOnTimer() {
            return this.mAudioTurnedOnTimer;
        }

        public Timer getVideoTurnedOnTimer() {
            return this.mVideoTurnedOnTimer;
        }

        public Timer getFlashlightTurnedOnTimer() {
            return this.mFlashlightTurnedOnTimer;
        }

        public Timer getCameraTurnedOnTimer() {
            return this.mCameraTurnedOnTimer;
        }

        public Timer getForegroundActivityTimer() {
            return this.mForegroundActivityTimer;
        }

        public Timer getForegroundServiceTimer() {
            return this.mForegroundServiceTimer;
        }

        public Timer getBluetoothScanTimer() {
            return this.mBluetoothScanTimer;
        }

        public Timer getBluetoothScanBackgroundTimer() {
            DualTimer dualTimer = this.mBluetoothScanTimer;
            if (dualTimer == null) {
                return null;
            }
            return dualTimer.getSubTimer();
        }

        public Timer getBluetoothUnoptimizedScanTimer() {
            return this.mBluetoothUnoptimizedScanTimer;
        }

        public Timer getBluetoothUnoptimizedScanBackgroundTimer() {
            DualTimer dualTimer = this.mBluetoothUnoptimizedScanTimer;
            if (dualTimer == null) {
                return null;
            }
            return dualTimer.getSubTimer();
        }

        public Counter getBluetoothScanResultCounter() {
            return this.mBluetoothScanResultCounter;
        }

        public Counter getBluetoothScanResultBgCounter() {
            return this.mBluetoothScanResultBgCounter;
        }

        public void makeProcessState(int i, Parcel parcel) {
            if (i < 0 || i >= 7) {
                return;
            }
            BatteryStatsImpl.detachIfNotNull(this.mProcessStateTimer[i]);
            if (parcel == null) {
                StopwatchTimer[] stopwatchTimerArr = this.mProcessStateTimer;
                BatteryStatsImpl batteryStatsImpl = this.mBsi;
                stopwatchTimerArr[i] = new StopwatchTimer(batteryStatsImpl.mClock, this, 12, null, batteryStatsImpl.mOnBatteryTimeBase);
                return;
            }
            StopwatchTimer[] stopwatchTimerArr2 = this.mProcessStateTimer;
            BatteryStatsImpl batteryStatsImpl2 = this.mBsi;
            stopwatchTimerArr2[i] = new StopwatchTimer(batteryStatsImpl2.mClock, this, 12, null, batteryStatsImpl2.mOnBatteryTimeBase, parcel);
        }

        public long getProcessStateTime(int i, long j, int i2) {
            StopwatchTimer stopwatchTimer;
            if (i < 0 || i >= 7 || (stopwatchTimer = this.mProcessStateTimer[i]) == null) {
                return 0L;
            }
            return stopwatchTimer.getTotalTimeLocked(j, i2);
        }

        public Timer getProcessStateTimer(int i) {
            if (i < 0 || i >= 7) {
                return null;
            }
            return this.mProcessStateTimer[i];
        }

        public Timer getVibratorOnTimer() {
            return this.mVibratorOnTimer;
        }

        public void noteUserActivityLocked(int i) {
            if (this.mUserActivityCounters == null) {
                initUserActivityLocked();
            }
            if (i >= 0 && i < BatteryStats.Uid.NUM_USER_ACTIVITY_TYPES) {
                this.mUserActivityCounters[i].stepAtomic();
                return;
            }
            Slog.w("BatteryStatsImpl", "Unknown user activity type " + i + " was specified.", new Throwable());
        }

        public boolean hasUserActivity() {
            return this.mUserActivityCounters != null;
        }

        public int getUserActivityCount(int i, int i2) {
            Counter[] counterArr = this.mUserActivityCounters;
            if (counterArr == null) {
                return 0;
            }
            return counterArr[i].getCountLocked(i2);
        }

        public void makeWifiBatchedScanBin(int i, Parcel parcel) {
            if (i < 0 || i >= 5) {
                return;
            }
            ArrayList arrayList = (ArrayList) this.mBsi.mWifiBatchedScanTimers.get(i);
            if (arrayList == null) {
                arrayList = new ArrayList();
                this.mBsi.mWifiBatchedScanTimers.put(i, arrayList);
            }
            ArrayList arrayList2 = arrayList;
            BatteryStatsImpl.detachIfNotNull(this.mWifiBatchedScanTimer[i]);
            if (parcel == null) {
                StopwatchTimer[] stopwatchTimerArr = this.mWifiBatchedScanTimer;
                BatteryStatsImpl batteryStatsImpl = this.mBsi;
                stopwatchTimerArr[i] = new StopwatchTimer(batteryStatsImpl.mClock, this, 11, arrayList2, batteryStatsImpl.mOnBatteryTimeBase);
                return;
            }
            StopwatchTimer[] stopwatchTimerArr2 = this.mWifiBatchedScanTimer;
            BatteryStatsImpl batteryStatsImpl2 = this.mBsi;
            stopwatchTimerArr2[i] = new StopwatchTimer(batteryStatsImpl2.mClock, this, 11, arrayList2, batteryStatsImpl2.mOnBatteryTimeBase, parcel);
        }

        public void initUserActivityLocked() {
            BatteryStatsImpl.detachIfNotNull(this.mUserActivityCounters);
            this.mUserActivityCounters = new Counter[BatteryStats.Uid.NUM_USER_ACTIVITY_TYPES];
            for (int i = 0; i < BatteryStats.Uid.NUM_USER_ACTIVITY_TYPES; i++) {
                this.mUserActivityCounters[i] = new Counter(this.mBsi.mOnBatteryTimeBase);
            }
        }

        public void noteNetworkActivityLocked(int i, long j, long j2) {
            ensureNetworkActivityLocked();
            if (i >= 0 && i < 10) {
                this.mNetworkByteActivityCounters[i].addCountLocked(j);
                this.mNetworkPacketActivityCounters[i].addCountLocked(j2);
                return;
            }
            Slog.w("BatteryStatsImpl", "Unknown network activity type " + i + " was specified.", new Throwable());
        }

        public void noteMobileRadioActiveTimeLocked(long j, long j2) {
            ensureNetworkActivityLocked();
            getMobileRadioActiveTimeCounter().increment(j, j2);
            this.mMobileRadioActiveCount.addCountLocked(1L);
        }

        public final TimeMultiStateCounter getMobileRadioActiveTimeCounter() {
            if (this.mMobileRadioActiveTime == null) {
                long elapsedRealtime = this.mBsi.mClock.elapsedRealtime();
                TimeMultiStateCounter timeMultiStateCounter = new TimeMultiStateCounter(this.mBsi.mOnBatteryTimeBase, 5, elapsedRealtime);
                this.mMobileRadioActiveTime = timeMultiStateCounter;
                timeMultiStateCounter.setState(BatteryStats.mapUidProcessStateToBatteryConsumerProcessState(this.mProcessState), elapsedRealtime);
                this.mMobileRadioActiveTime.update(0L, elapsedRealtime);
            }
            return this.mMobileRadioActiveTime;
        }

        public boolean hasNetworkActivity() {
            return this.mNetworkByteActivityCounters != null;
        }

        public long getNetworkActivityBytes(int i, int i2) {
            LongSamplingCounter[] longSamplingCounterArr = this.mNetworkByteActivityCounters;
            if (longSamplingCounterArr == null || i < 0 || i >= longSamplingCounterArr.length) {
                return 0L;
            }
            return longSamplingCounterArr[i].getCountLocked(i2);
        }

        public long getNetworkActivityPackets(int i, int i2) {
            LongSamplingCounter[] longSamplingCounterArr = this.mNetworkPacketActivityCounters;
            if (longSamplingCounterArr == null || i < 0 || i >= longSamplingCounterArr.length) {
                return 0L;
            }
            return longSamplingCounterArr[i].getCountLocked(i2);
        }

        public long getMobileRadioActiveTime(int i) {
            return getMobileRadioActiveTimeInProcessState(0);
        }

        public long getMobileRadioActiveTimeInProcessState(int i) {
            TimeMultiStateCounter timeMultiStateCounter = this.mMobileRadioActiveTime;
            if (timeMultiStateCounter == null) {
                return 0L;
            }
            if (i == 0) {
                return timeMultiStateCounter.getTotalCountLocked();
            }
            return timeMultiStateCounter.getCountForProcessState(i);
        }

        public int getMobileRadioActiveCount(int i) {
            LongSamplingCounter longSamplingCounter = this.mMobileRadioActiveCount;
            if (longSamplingCounter != null) {
                return (int) longSamplingCounter.getCountLocked(i);
            }
            return 0;
        }

        public long getUserCpuTimeUs(int i) {
            return this.mUserCpuTime.getCountLocked(i);
        }

        public long getSystemCpuTimeUs(int i) {
            return this.mSystemCpuTime.getCountLocked(i);
        }

        public long getTimeAtCpuSpeed(int i, int i2, int i3) {
            LongSamplingCounter[] longSamplingCounterArr;
            LongSamplingCounter longSamplingCounter;
            LongSamplingCounter[][] longSamplingCounterArr2 = this.mCpuClusterSpeedTimesUs;
            if (longSamplingCounterArr2 == null || i < 0 || i >= longSamplingCounterArr2.length || (longSamplingCounterArr = longSamplingCounterArr2[i]) == null || i2 < 0 || i2 >= longSamplingCounterArr.length || (longSamplingCounter = longSamplingCounterArr[i2]) == null) {
                return 0L;
            }
            return longSamplingCounter.getCountLocked(i3);
        }

        public void noteMobileRadioApWakeupLocked() {
            if (this.mMobileRadioApWakeupCount == null) {
                this.mMobileRadioApWakeupCount = new LongSamplingCounter(this.mBsi.mOnBatteryTimeBase);
            }
            this.mMobileRadioApWakeupCount.addCountLocked(1L);
        }

        public long getMobileRadioApWakeupCount(int i) {
            LongSamplingCounter longSamplingCounter = this.mMobileRadioApWakeupCount;
            if (longSamplingCounter != null) {
                return longSamplingCounter.getCountLocked(i);
            }
            return 0L;
        }

        public void noteWifiRadioApWakeupLocked() {
            if (this.mWifiRadioApWakeupCount == null) {
                this.mWifiRadioApWakeupCount = new LongSamplingCounter(this.mBsi.mOnBatteryTimeBase);
            }
            this.mWifiRadioApWakeupCount.addCountLocked(1L);
        }

        public long getWifiRadioApWakeupCount(int i) {
            LongSamplingCounter longSamplingCounter = this.mWifiRadioApWakeupCount;
            if (longSamplingCounter != null) {
                return longSamplingCounter.getCountLocked(i);
            }
            return 0L;
        }

        public void getDeferredJobsCheckinLineLocked(StringBuilder sb, int i) {
            sb.setLength(0);
            int countLocked = this.mJobsDeferredEventCount.getCountLocked(i);
            if (countLocked == 0) {
                return;
            }
            int countLocked2 = this.mJobsDeferredCount.getCountLocked(i);
            long countLocked3 = this.mJobsFreshnessTimeMs.getCountLocked(i);
            sb.append(countLocked);
            sb.append(',');
            sb.append(countLocked2);
            sb.append(',');
            sb.append(countLocked3);
            for (int i2 = 0; i2 < BatteryStats.JOB_FRESHNESS_BUCKETS.length; i2++) {
                if (this.mJobsFreshnessBuckets[i2] == null) {
                    sb.append(",0");
                } else {
                    sb.append(",");
                    sb.append(this.mJobsFreshnessBuckets[i2].getCountLocked(i));
                }
            }
        }

        public void getDeferredJobsLineLocked(StringBuilder sb, int i) {
            sb.setLength(0);
            int countLocked = this.mJobsDeferredEventCount.getCountLocked(i);
            if (countLocked == 0) {
                return;
            }
            int countLocked2 = this.mJobsDeferredCount.getCountLocked(i);
            long countLocked3 = this.mJobsFreshnessTimeMs.getCountLocked(i);
            sb.append("times=");
            sb.append(countLocked);
            sb.append(", ");
            sb.append("count=");
            sb.append(countLocked2);
            sb.append(", ");
            sb.append("totalLatencyMs=");
            sb.append(countLocked3);
            sb.append(", ");
            for (int i2 = 0; i2 < BatteryStats.JOB_FRESHNESS_BUCKETS.length; i2++) {
                sb.append("<");
                sb.append(BatteryStats.JOB_FRESHNESS_BUCKETS[i2]);
                sb.append("ms=");
                Counter counter = this.mJobsFreshnessBuckets[i2];
                if (counter == null) {
                    sb.append("0");
                } else {
                    sb.append(counter.getCountLocked(i));
                }
                sb.append(" ");
            }
        }

        public void ensureNetworkActivityLocked() {
            if (this.mNetworkByteActivityCounters != null) {
                return;
            }
            this.mNetworkByteActivityCounters = new LongSamplingCounter[10];
            this.mNetworkPacketActivityCounters = new LongSamplingCounter[10];
            for (int i = 0; i < 10; i++) {
                this.mNetworkByteActivityCounters[i] = new LongSamplingCounter(this.mBsi.mOnBatteryTimeBase);
                this.mNetworkPacketActivityCounters[i] = new LongSamplingCounter(this.mBsi.mOnBatteryTimeBase);
            }
            this.mMobileRadioActiveCount = new LongSamplingCounter(this.mBsi.mOnBatteryTimeBase);
        }

        @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
        public boolean reset(long j, long j2, int i) {
            StopwatchTimer stopwatchTimer;
            StopwatchTimer stopwatchTimer2;
            DualTimer dualTimer;
            StopwatchTimer stopwatchTimer3;
            StopwatchTimer stopwatchTimer4;
            this.mOnBatteryBackgroundTimeBase.init(j, j2);
            this.mOnBatteryScreenOffBackgroundTimeBase.init(j, j2);
            boolean z = this.mWifiRunningTimer != null ? (!stopwatchTimer.reset(false, j2)) | false | this.mWifiRunning : false;
            if (this.mFullWifiLockTimer != null) {
                z = z | (!stopwatchTimer2.reset(false, j2)) | this.mFullWifiLockOut;
            }
            if (this.mWifiScanTimer != null) {
                z = z | (!dualTimer.reset(false, j2)) | this.mWifiScanStarted;
            }
            if (this.mWifiBatchedScanTimer != null) {
                for (int i2 = 0; i2 < 5; i2++) {
                    if (this.mWifiBatchedScanTimer[i2] != null) {
                        z |= !stopwatchTimer4.reset(false, j2);
                    }
                }
                z |= this.mWifiBatchedScanBinStarted != -1;
            }
            if (this.mWifiMulticastTimer != null) {
                z = z | (!stopwatchTimer3.reset(false, j2)) | (this.mWifiMulticastWakelockCount > 0);
            }
            boolean z2 = z | (!BatteryStatsImpl.resetIfNotNull(this.mAudioTurnedOnTimer, false, j2)) | (!BatteryStatsImpl.resetIfNotNull(this.mVideoTurnedOnTimer, false, j2)) | (!BatteryStatsImpl.resetIfNotNull(this.mFlashlightTurnedOnTimer, false, j2)) | (!BatteryStatsImpl.resetIfNotNull(this.mCameraTurnedOnTimer, false, j2)) | (!BatteryStatsImpl.resetIfNotNull(this.mForegroundActivityTimer, false, j2)) | (!BatteryStatsImpl.resetIfNotNull(this.mForegroundServiceTimer, false, j2)) | (!BatteryStatsImpl.resetIfNotNull(this.mAggregatedPartialWakelockTimer, false, j2)) | (!BatteryStatsImpl.resetIfNotNull(this.mBluetoothScanTimer, false, j2)) | (!BatteryStatsImpl.resetIfNotNull(this.mBluetoothUnoptimizedScanTimer, false, j2));
            BatteryStatsImpl.resetIfNotNull(this.mBluetoothScanResultCounter, false, j2);
            BatteryStatsImpl.resetIfNotNull(this.mBluetoothScanResultBgCounter, false, j2);
            if (this.mProcessStateTimer != null) {
                for (int i3 = 0; i3 < 7; i3++) {
                    z2 |= !BatteryStatsImpl.resetIfNotNull(this.mProcessStateTimer[i3], false, j2);
                }
                z2 |= this.mProcessState != 7;
            }
            BatchTimer batchTimer = this.mVibratorOnTimer;
            if (batchTimer != null) {
                if (batchTimer.reset(false, j2)) {
                    this.mVibratorOnTimer.detach();
                    this.mVibratorOnTimer = null;
                } else {
                    z2 = true;
                }
            }
            BatteryStatsImpl.resetIfNotNull((TimeBaseObs[]) this.mUserActivityCounters, false, j2);
            BatteryStatsImpl.resetIfNotNull((TimeBaseObs[]) this.mNetworkByteActivityCounters, false, j2);
            BatteryStatsImpl.resetIfNotNull((TimeBaseObs[]) this.mNetworkPacketActivityCounters, false, j2);
            BatteryStatsImpl.resetIfNotNull(this.mMobileRadioActiveTime, false, j2);
            BatteryStatsImpl.resetIfNotNull(this.mMobileRadioActiveCount, false, j2);
            BatteryStatsImpl.resetIfNotNull(this.mWifiControllerActivity, false, j2);
            BatteryStatsImpl.resetIfNotNull(this.mBluetoothControllerActivity, false, j2);
            BatteryStatsImpl.resetIfNotNull(this.mModemControllerActivity, false, j2);
            if (i == 4) {
                this.mUidEnergyConsumerStats = null;
            } else {
                EnergyConsumerStats.resetIfNotNull(this.mUidEnergyConsumerStats);
            }
            BatteryStatsImpl.resetIfNotNull(this.mUserCpuTime, false, j2);
            BatteryStatsImpl.resetIfNotNull(this.mSystemCpuTime, false, j2);
            BatteryStatsImpl.resetIfNotNull((TimeBaseObs[][]) this.mCpuClusterSpeedTimesUs, false, j2);
            BatteryStatsImpl.resetIfNotNull(this.mCpuFreqTimeMs, false, j2);
            BatteryStatsImpl.resetIfNotNull(this.mScreenOffCpuFreqTimeMs, false, j2);
            BatteryStatsImpl.resetIfNotNull(this.mCpuActiveTimeMs, false, j2);
            BatteryStatsImpl.resetIfNotNull(this.mCpuClusterTimesMs, false, j2);
            BatteryStatsImpl.resetIfNotNull(this.mProcStateTimeMs, false, j2);
            BatteryStatsImpl.resetIfNotNull(this.mProcStateScreenOffTimeMs, false, j2);
            BatteryStatsImpl.resetIfNotNull(this.mMobileRadioApWakeupCount, false, j2);
            BatteryStatsImpl.resetIfNotNull(this.mWifiRadioApWakeupCount, false, j2);
            ArrayMap<String, Wakelock> map = this.mWakelockStats.getMap();
            for (int size = map.size() - 1; size >= 0; size--) {
                if (map.valueAt(size).reset(j2)) {
                    map.removeAt(size);
                } else {
                    z2 = true;
                }
            }
            long j3 = j2 / 1000;
            this.mWakelockStats.cleanup(j3);
            ArrayMap<String, DualTimer> map2 = this.mSyncStats.getMap();
            for (int size2 = map2.size() - 1; size2 >= 0; size2--) {
                DualTimer valueAt = map2.valueAt(size2);
                if (valueAt.reset(false, j2)) {
                    map2.removeAt(size2);
                    valueAt.detach();
                } else {
                    z2 = true;
                }
            }
            this.mSyncStats.cleanup(j3);
            ArrayMap<String, DualTimer> map3 = this.mJobStats.getMap();
            for (int size3 = map3.size() - 1; size3 >= 0; size3--) {
                DualTimer valueAt2 = map3.valueAt(size3);
                if (valueAt2.reset(false, j2)) {
                    map3.removeAt(size3);
                    valueAt2.detach();
                } else {
                    z2 = true;
                }
            }
            this.mJobStats.cleanup(j3);
            this.mJobCompletions.clear();
            BatteryStatsImpl.resetIfNotNull(this.mJobsDeferredEventCount, false, j2);
            BatteryStatsImpl.resetIfNotNull(this.mJobsDeferredCount, false, j2);
            BatteryStatsImpl.resetIfNotNull(this.mJobsFreshnessTimeMs, false, j2);
            BatteryStatsImpl.resetIfNotNull((TimeBaseObs[]) this.mJobsFreshnessBuckets, false, j2);
            for (int size4 = this.mSensorStats.size() - 1; size4 >= 0; size4--) {
                if (this.mSensorStats.valueAt(size4).reset(j2)) {
                    this.mSensorStats.removeAt(size4);
                } else {
                    z2 = true;
                }
            }
            for (int size5 = this.mProcessStats.size() - 1; size5 >= 0; size5--) {
                this.mProcessStats.valueAt(size5).detach();
            }
            this.mProcessStats.clear();
            for (int size6 = this.mPids.size() - 1; size6 >= 0; size6--) {
                if (this.mPids.valueAt(size6).mWakeNesting > 0) {
                    z2 = true;
                } else {
                    this.mPids.removeAt(size6);
                }
            }
            for (int size7 = this.mPackageStats.size() - 1; size7 >= 0; size7--) {
                this.mPackageStats.valueAt(size7).detach();
            }
            this.mPackageStats.clear();
            this.mBinderCallCount = 0L;
            this.mBinderCallStats.clear();
            this.mProportionalSystemServiceUsage = 0.0d;
            this.mLastStepSystemTimeMs = 0L;
            this.mLastStepUserTimeMs = 0L;
            this.mCurStepSystemTimeMs = 0L;
            this.mCurStepUserTimeMs = 0L;
            return !z2;
        }

        public void detachFromTimeBase() {
            BatteryStatsImpl.detachIfNotNull(this.mWifiRunningTimer);
            BatteryStatsImpl.detachIfNotNull(this.mFullWifiLockTimer);
            BatteryStatsImpl.detachIfNotNull(this.mWifiScanTimer);
            BatteryStatsImpl.detachIfNotNull(this.mWifiBatchedScanTimer);
            BatteryStatsImpl.detachIfNotNull(this.mWifiMulticastTimer);
            BatteryStatsImpl.detachIfNotNull(this.mAudioTurnedOnTimer);
            BatteryStatsImpl.detachIfNotNull(this.mVideoTurnedOnTimer);
            BatteryStatsImpl.detachIfNotNull(this.mFlashlightTurnedOnTimer);
            BatteryStatsImpl.detachIfNotNull(this.mCameraTurnedOnTimer);
            BatteryStatsImpl.detachIfNotNull(this.mForegroundActivityTimer);
            BatteryStatsImpl.detachIfNotNull(this.mForegroundServiceTimer);
            BatteryStatsImpl.detachIfNotNull(this.mAggregatedPartialWakelockTimer);
            BatteryStatsImpl.detachIfNotNull(this.mBluetoothScanTimer);
            BatteryStatsImpl.detachIfNotNull(this.mBluetoothUnoptimizedScanTimer);
            BatteryStatsImpl.detachIfNotNull(this.mBluetoothScanResultCounter);
            BatteryStatsImpl.detachIfNotNull(this.mBluetoothScanResultBgCounter);
            BatteryStatsImpl.detachIfNotNull(this.mProcessStateTimer);
            BatteryStatsImpl.detachIfNotNull(this.mVibratorOnTimer);
            BatteryStatsImpl.detachIfNotNull(this.mUserActivityCounters);
            BatteryStatsImpl.detachIfNotNull(this.mNetworkByteActivityCounters);
            BatteryStatsImpl.detachIfNotNull(this.mNetworkPacketActivityCounters);
            BatteryStatsImpl.detachIfNotNull(this.mMobileRadioActiveTime);
            BatteryStatsImpl.detachIfNotNull(this.mMobileRadioActiveCount);
            BatteryStatsImpl.detachIfNotNull(this.mMobileRadioApWakeupCount);
            BatteryStatsImpl.detachIfNotNull(this.mWifiRadioApWakeupCount);
            BatteryStatsImpl.detachIfNotNull(this.mWifiControllerActivity);
            BatteryStatsImpl.detachIfNotNull(this.mBluetoothControllerActivity);
            BatteryStatsImpl.detachIfNotNull(this.mModemControllerActivity);
            this.mPids.clear();
            BatteryStatsImpl.detachIfNotNull(this.mUserCpuTime);
            BatteryStatsImpl.detachIfNotNull(this.mSystemCpuTime);
            BatteryStatsImpl.detachIfNotNull(this.mCpuClusterSpeedTimesUs);
            BatteryStatsImpl.detachIfNotNull(this.mCpuActiveTimeMs);
            BatteryStatsImpl.detachIfNotNull(this.mCpuFreqTimeMs);
            BatteryStatsImpl.detachIfNotNull(this.mScreenOffCpuFreqTimeMs);
            BatteryStatsImpl.detachIfNotNull(this.mCpuClusterTimesMs);
            BatteryStatsImpl.detachIfNotNull(this.mProcStateTimeMs);
            BatteryStatsImpl.detachIfNotNull(this.mProcStateScreenOffTimeMs);
            ArrayMap<String, Wakelock> map = this.mWakelockStats.getMap();
            for (int size = map.size() - 1; size >= 0; size--) {
                map.valueAt(size).detachFromTimeBase();
            }
            ArrayMap<String, DualTimer> map2 = this.mSyncStats.getMap();
            for (int size2 = map2.size() - 1; size2 >= 0; size2--) {
                BatteryStatsImpl.detachIfNotNull(map2.valueAt(size2));
            }
            ArrayMap<String, DualTimer> map3 = this.mJobStats.getMap();
            for (int size3 = map3.size() - 1; size3 >= 0; size3--) {
                BatteryStatsImpl.detachIfNotNull(map3.valueAt(size3));
            }
            BatteryStatsImpl.detachIfNotNull(this.mJobsDeferredEventCount);
            BatteryStatsImpl.detachIfNotNull(this.mJobsDeferredCount);
            BatteryStatsImpl.detachIfNotNull(this.mJobsFreshnessTimeMs);
            BatteryStatsImpl.detachIfNotNull(this.mJobsFreshnessBuckets);
            for (int size4 = this.mSensorStats.size() - 1; size4 >= 0; size4--) {
                this.mSensorStats.valueAt(size4).detachFromTimeBase();
            }
            for (int size5 = this.mProcessStats.size() - 1; size5 >= 0; size5--) {
                this.mProcessStats.valueAt(size5).detach();
            }
            this.mProcessStats.clear();
            for (int size6 = this.mPackageStats.size() - 1; size6 >= 0; size6--) {
                this.mPackageStats.valueAt(size6).detach();
            }
            this.mPackageStats.clear();
        }

        public void writeJobCompletionsToParcelLocked(Parcel parcel) {
            int size = this.mJobCompletions.size();
            parcel.writeInt(size);
            for (int i = 0; i < size; i++) {
                parcel.writeString(this.mJobCompletions.keyAt(i));
                SparseIntArray valueAt = this.mJobCompletions.valueAt(i);
                int size2 = valueAt.size();
                parcel.writeInt(size2);
                for (int i2 = 0; i2 < size2; i2++) {
                    parcel.writeInt(valueAt.keyAt(i2));
                    parcel.writeInt(valueAt.valueAt(i2));
                }
            }
        }

        public void readJobCompletionsFromParcelLocked(Parcel parcel) {
            int readInt = parcel.readInt();
            this.mJobCompletions.clear();
            for (int i = 0; i < readInt; i++) {
                String readString = parcel.readString();
                int readInt2 = parcel.readInt();
                if (readInt2 > 0) {
                    SparseIntArray sparseIntArray = new SparseIntArray();
                    for (int i2 = 0; i2 < readInt2; i2++) {
                        sparseIntArray.put(parcel.readInt(), parcel.readInt());
                    }
                    this.mJobCompletions.put(readString, sparseIntArray);
                }
            }
        }

        public void noteJobsDeferredLocked(int i, long j) {
            this.mJobsDeferredEventCount.addAtomic(1);
            this.mJobsDeferredCount.addAtomic(i);
            if (j == 0) {
                return;
            }
            this.mJobsFreshnessTimeMs.addCountLocked(j);
            int i2 = 0;
            while (true) {
                long[] jArr = BatteryStats.JOB_FRESHNESS_BUCKETS;
                if (i2 >= jArr.length) {
                    return;
                }
                if (j < jArr[i2]) {
                    Counter[] counterArr = this.mJobsFreshnessBuckets;
                    if (counterArr[i2] == null) {
                        counterArr[i2] = new Counter(this.mBsi.mOnBatteryTimeBase);
                    }
                    this.mJobsFreshnessBuckets[i2].addAtomic(1);
                    return;
                }
                i2++;
            }
        }

        public void noteBinderCallStatsLocked(long j, Collection<BinderCallsStats.CallStat> collection) {
            BinderCallStats binderCallStats;
            this.mBinderCallCount += j;
            for (BinderCallsStats.CallStat callStat : collection) {
                BinderCallStats binderCallStats2 = sTempBinderCallStats;
                binderCallStats2.binderClass = callStat.binderClass;
                binderCallStats2.transactionCode = callStat.transactionCode;
                int indexOf = this.mBinderCallStats.indexOf(binderCallStats2);
                if (indexOf >= 0) {
                    binderCallStats = this.mBinderCallStats.valueAt(indexOf);
                } else {
                    binderCallStats = new BinderCallStats();
                    binderCallStats.binderClass = callStat.binderClass;
                    binderCallStats.transactionCode = callStat.transactionCode;
                    this.mBinderCallStats.add(binderCallStats);
                }
                binderCallStats.callCount += callStat.incrementalCallCount;
                binderCallStats.recordedCallCount = callStat.recordedCallCount;
                binderCallStats.recordedCpuTimeMicros = callStat.cpuTimeMicros;
            }
        }

        /* loaded from: classes2.dex */
        public static class Wakelock extends BatteryStats.Uid.Wakelock {
            public BatteryStatsImpl mBsi;
            public StopwatchTimer mTimerDraw;
            public StopwatchTimer mTimerFull;
            public DualTimer mTimerPartial;
            public StopwatchTimer mTimerWindow;
            public Uid mUid;

            public Wakelock(BatteryStatsImpl batteryStatsImpl, Uid uid) {
                this.mBsi = batteryStatsImpl;
                this.mUid = uid;
            }

            public boolean reset(long j) {
                boolean z = (!BatteryStatsImpl.resetIfNotNull(this.mTimerDraw, false, j)) | (!BatteryStatsImpl.resetIfNotNull(this.mTimerFull, false, j)) | false | (!BatteryStatsImpl.resetIfNotNull(this.mTimerPartial, false, j)) | (!BatteryStatsImpl.resetIfNotNull(this.mTimerWindow, false, j));
                if (!z) {
                    BatteryStatsImpl.detachIfNotNull(this.mTimerFull);
                    this.mTimerFull = null;
                    BatteryStatsImpl.detachIfNotNull(this.mTimerPartial);
                    this.mTimerPartial = null;
                    BatteryStatsImpl.detachIfNotNull(this.mTimerWindow);
                    this.mTimerWindow = null;
                    BatteryStatsImpl.detachIfNotNull(this.mTimerDraw);
                    this.mTimerDraw = null;
                }
                return !z;
            }

            public Timer getWakeTime(int i) {
                if (i != 0) {
                    if (i != 1) {
                        if (i != 2) {
                            if (i == 18) {
                                return this.mTimerDraw;
                            }
                            throw new IllegalArgumentException("type = " + i);
                        }
                        return this.mTimerWindow;
                    }
                    return this.mTimerFull;
                }
                return this.mTimerPartial;
            }

            public void detachFromTimeBase() {
                BatteryStatsImpl.detachIfNotNull(this.mTimerPartial);
                BatteryStatsImpl.detachIfNotNull(this.mTimerFull);
                BatteryStatsImpl.detachIfNotNull(this.mTimerWindow);
                BatteryStatsImpl.detachIfNotNull(this.mTimerDraw);
            }
        }

        /* loaded from: classes2.dex */
        public static class Sensor extends BatteryStats.Uid.Sensor {
            public BatteryStatsImpl mBsi;
            public final int mHandle;
            public DualTimer mTimer;
            public Uid mUid;

            public Sensor(BatteryStatsImpl batteryStatsImpl, Uid uid, int i) {
                this.mBsi = batteryStatsImpl;
                this.mUid = uid;
                this.mHandle = i;
            }

            public boolean reset(long j) {
                if (this.mTimer.reset(true, j)) {
                    this.mTimer = null;
                    return true;
                }
                return false;
            }

            public Timer getSensorTime() {
                return this.mTimer;
            }

            public Timer getSensorBackgroundTime() {
                DualTimer dualTimer = this.mTimer;
                if (dualTimer == null) {
                    return null;
                }
                return dualTimer.getSubTimer();
            }

            public int getHandle() {
                return this.mHandle;
            }

            public void detachFromTimeBase() {
                BatteryStatsImpl.detachIfNotNull(this.mTimer);
            }
        }

        /* loaded from: classes2.dex */
        public static class Proc extends BatteryStats.Uid.Proc implements TimeBaseObs {
            public boolean mActive = true;
            public BatteryStatsImpl mBsi;
            public ArrayList<BatteryStats.Uid.Proc.ExcessivePower> mExcessivePower;
            public long mForegroundTimeMs;
            public final String mName;
            public int mNumAnrs;
            public int mNumCrashes;
            public int mStarts;
            public long mSystemTimeMs;
            public long mUserTimeMs;

            @Override // com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
            public void onTimeStarted(long j, long j2, long j3) {
            }

            @Override // com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
            public void onTimeStopped(long j, long j2, long j3) {
            }

            public Proc(BatteryStatsImpl batteryStatsImpl, String str) {
                this.mBsi = batteryStatsImpl;
                this.mName = str;
                batteryStatsImpl.mOnBatteryTimeBase.add(this);
            }

            @Override // com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
            public boolean reset(boolean z, long j) {
                if (z) {
                    detach();
                    return true;
                }
                return true;
            }

            @Override // com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
            public void detach() {
                this.mActive = false;
                this.mBsi.mOnBatteryTimeBase.remove(this);
            }

            public int countExcessivePowers() {
                ArrayList<BatteryStats.Uid.Proc.ExcessivePower> arrayList = this.mExcessivePower;
                if (arrayList != null) {
                    return arrayList.size();
                }
                return 0;
            }

            public BatteryStats.Uid.Proc.ExcessivePower getExcessivePower(int i) {
                ArrayList<BatteryStats.Uid.Proc.ExcessivePower> arrayList = this.mExcessivePower;
                if (arrayList != null) {
                    return arrayList.get(i);
                }
                return null;
            }

            public void addExcessiveCpu(long j, long j2) {
                if (this.mExcessivePower == null) {
                    this.mExcessivePower = new ArrayList<>();
                }
                BatteryStats.Uid.Proc.ExcessivePower excessivePower = new BatteryStats.Uid.Proc.ExcessivePower();
                excessivePower.type = 2;
                excessivePower.overTime = j;
                excessivePower.usedTime = j2;
                this.mExcessivePower.add(excessivePower);
            }

            public void writeExcessivePowerToParcelLocked(Parcel parcel) {
                ArrayList<BatteryStats.Uid.Proc.ExcessivePower> arrayList = this.mExcessivePower;
                if (arrayList == null) {
                    parcel.writeInt(0);
                    return;
                }
                int size = arrayList.size();
                parcel.writeInt(size);
                for (int i = 0; i < size; i++) {
                    BatteryStats.Uid.Proc.ExcessivePower excessivePower = this.mExcessivePower.get(i);
                    parcel.writeInt(excessivePower.type);
                    parcel.writeLong(excessivePower.overTime);
                    parcel.writeLong(excessivePower.usedTime);
                }
            }

            public void readExcessivePowerFromParcelLocked(Parcel parcel) {
                int readInt = parcel.readInt();
                if (readInt == 0) {
                    this.mExcessivePower = null;
                } else if (readInt > 10000) {
                    throw new ParcelFormatException("File corrupt: too many excessive power entries " + readInt);
                } else {
                    this.mExcessivePower = new ArrayList<>();
                    for (int i = 0; i < readInt; i++) {
                        BatteryStats.Uid.Proc.ExcessivePower excessivePower = new BatteryStats.Uid.Proc.ExcessivePower();
                        excessivePower.type = parcel.readInt();
                        excessivePower.overTime = parcel.readLong();
                        excessivePower.usedTime = parcel.readLong();
                        this.mExcessivePower.add(excessivePower);
                    }
                }
            }

            public void addCpuTimeLocked(int i, int i2) {
                addCpuTimeLocked(i, i2, this.mBsi.mOnBatteryTimeBase.isRunning());
            }

            public void addCpuTimeLocked(int i, int i2, boolean z) {
                if (z) {
                    this.mUserTimeMs += i;
                    this.mSystemTimeMs += i2;
                }
            }

            public void addForegroundTimeLocked(long j) {
                this.mForegroundTimeMs += j;
            }

            public void incStartsLocked() {
                this.mStarts++;
            }

            public void incNumCrashesLocked() {
                this.mNumCrashes++;
            }

            public void incNumAnrsLocked() {
                this.mNumAnrs++;
            }

            public boolean isActive() {
                return this.mActive;
            }

            public long getUserTime(int i) {
                return this.mUserTimeMs;
            }

            public long getSystemTime(int i) {
                return this.mSystemTimeMs;
            }

            public long getForegroundTime(int i) {
                return this.mForegroundTimeMs;
            }

            public int getStarts(int i) {
                return this.mStarts;
            }

            public int getNumCrashes(int i) {
                return this.mNumCrashes;
            }

            public int getNumAnrs(int i) {
                return this.mNumAnrs;
            }
        }

        /* loaded from: classes2.dex */
        public static class Pkg extends BatteryStats.Uid.Pkg implements TimeBaseObs {
            public BatteryStatsImpl mBsi;
            public ArrayMap<String, Counter> mWakeupAlarms = new ArrayMap<>();
            public final ArrayMap<String, Serv> mServiceStats = new ArrayMap<>();

            @Override // com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
            public void onTimeStarted(long j, long j2, long j3) {
            }

            @Override // com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
            public void onTimeStopped(long j, long j2, long j3) {
            }

            public Pkg(BatteryStatsImpl batteryStatsImpl) {
                this.mBsi = batteryStatsImpl;
                batteryStatsImpl.mOnBatteryScreenOffTimeBase.add(this);
            }

            @Override // com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
            public boolean reset(boolean z, long j) {
                if (z) {
                    detach();
                    return true;
                }
                return true;
            }

            @Override // com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
            public void detach() {
                this.mBsi.mOnBatteryScreenOffTimeBase.remove(this);
                for (int size = this.mWakeupAlarms.size() - 1; size >= 0; size--) {
                    BatteryStatsImpl.detachIfNotNull(this.mWakeupAlarms.valueAt(size));
                }
                for (int size2 = this.mServiceStats.size() - 1; size2 >= 0; size2--) {
                    BatteryStatsImpl.detachIfNotNull(this.mServiceStats.valueAt(size2));
                }
            }

            public ArrayMap<String, ? extends BatteryStats.Counter> getWakeupAlarmStats() {
                return this.mWakeupAlarms;
            }

            public void noteWakeupAlarmLocked(String str) {
                Counter counter = this.mWakeupAlarms.get(str);
                if (counter == null) {
                    counter = new Counter(this.mBsi.mOnBatteryScreenOffTimeBase);
                    this.mWakeupAlarms.put(str, counter);
                }
                counter.stepAtomic();
            }

            public ArrayMap<String, ? extends BatteryStats.Uid.Pkg.Serv> getServiceStats() {
                return this.mServiceStats;
            }

            /* loaded from: classes2.dex */
            public static class Serv extends BatteryStats.Uid.Pkg.Serv implements TimeBaseObs {
                public BatteryStatsImpl mBsi;
                public boolean mLaunched;
                public long mLaunchedSinceMs;
                public long mLaunchedTimeMs;
                public int mLaunches;
                public boolean mRunning;
                public long mRunningSinceMs;
                public long mStartTimeMs;
                public int mStarts;

                @Override // com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
                public void onTimeStarted(long j, long j2, long j3) {
                }

                @Override // com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
                public void onTimeStopped(long j, long j2, long j3) {
                }

                public Serv(BatteryStatsImpl batteryStatsImpl) {
                    this.mBsi = batteryStatsImpl;
                    batteryStatsImpl.mOnBatteryTimeBase.add(this);
                }

                @Override // com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
                public boolean reset(boolean z, long j) {
                    if (z) {
                        detach();
                        return true;
                    }
                    return true;
                }

                @Override // com.android.server.power.stats.BatteryStatsImpl.TimeBaseObs
                public void detach() {
                    this.mBsi.mOnBatteryTimeBase.remove(this);
                }

                public long getStartTimeToNowLocked(long j) {
                    return !this.mRunning ? this.mStartTimeMs : (this.mStartTimeMs + j) - this.mRunningSinceMs;
                }

                public void startLaunchedLocked(long j) {
                    if (this.mLaunched) {
                        return;
                    }
                    this.mLaunches++;
                    this.mLaunchedSinceMs = this.mBsi.getBatteryUptimeLocked(j) / 1000;
                    this.mLaunched = true;
                }

                public void stopLaunchedLocked(long j) {
                    if (this.mLaunched) {
                        long batteryUptimeLocked = (this.mBsi.getBatteryUptimeLocked(j) / 1000) - this.mLaunchedSinceMs;
                        if (batteryUptimeLocked > 0) {
                            this.mLaunchedTimeMs += batteryUptimeLocked;
                        } else {
                            this.mLaunches--;
                        }
                        this.mLaunched = false;
                    }
                }

                public void startRunningLocked(long j) {
                    if (this.mRunning) {
                        return;
                    }
                    this.mStarts++;
                    this.mRunningSinceMs = this.mBsi.getBatteryUptimeLocked(j) / 1000;
                    this.mRunning = true;
                }

                public void stopRunningLocked(long j) {
                    if (this.mRunning) {
                        long batteryUptimeLocked = (this.mBsi.getBatteryUptimeLocked(j) / 1000) - this.mRunningSinceMs;
                        if (batteryUptimeLocked > 0) {
                            this.mStartTimeMs += batteryUptimeLocked;
                        } else {
                            this.mStarts--;
                        }
                        this.mRunning = false;
                    }
                }

                public int getLaunches(int i) {
                    return this.mLaunches;
                }

                public long getStartTime(long j, int i) {
                    return getStartTimeToNowLocked(j);
                }

                public int getStarts(int i) {
                    return this.mStarts;
                }
            }

            public final Serv newServiceStatsLocked() {
                return new Serv(this.mBsi);
            }
        }

        /* loaded from: classes2.dex */
        public class ChildUid {
            public final TimeMultiStateCounter cpuActiveCounter;
            public final LongArrayMultiStateCounter cpuTimeInFreqCounter;

            public ChildUid() {
                long elapsedRealtime = Uid.this.mBsi.mClock.elapsedRealtime();
                TimeMultiStateCounter timeMultiStateCounter = new TimeMultiStateCounter(Uid.this.mBsi.mOnBatteryTimeBase, 1, elapsedRealtime);
                this.cpuActiveCounter = timeMultiStateCounter;
                timeMultiStateCounter.setState(0, elapsedRealtime);
                if (Uid.this.mBsi.trackPerProcStateCpuTimes()) {
                    int cpuFreqCount = Uid.this.mBsi.getCpuFreqCount();
                    LongArrayMultiStateCounter longArrayMultiStateCounter = new LongArrayMultiStateCounter(1, cpuFreqCount);
                    this.cpuTimeInFreqCounter = longArrayMultiStateCounter;
                    longArrayMultiStateCounter.updateValues(new LongArrayMultiStateCounter.LongArrayContainer(cpuFreqCount), elapsedRealtime);
                    return;
                }
                this.cpuTimeInFreqCounter = null;
            }
        }

        public Proc getProcessStatsLocked(String str) {
            Proc proc = this.mProcessStats.get(str);
            if (proc == null) {
                Proc proc2 = new Proc(this.mBsi, str);
                this.mProcessStats.put(str, proc2);
                return proc2;
            }
            return proc;
        }

        @GuardedBy({"mBsi"})
        public void updateUidProcessStateLocked(int i, long j, long j2) {
            boolean isForegroundService = ActivityManager.isForegroundService(i);
            int mapToInternalProcessState = BatteryStats.mapToInternalProcessState(i);
            int i2 = this.mProcessState;
            if (i2 == mapToInternalProcessState && isForegroundService == this.mInForegroundService) {
                return;
            }
            if (i2 != mapToInternalProcessState) {
                if (i2 != 7) {
                    this.mProcessStateTimer[i2].stopRunningLocked(j);
                }
                if (mapToInternalProcessState != 7) {
                    if (this.mProcessStateTimer[mapToInternalProcessState] == null) {
                        makeProcessState(mapToInternalProcessState, null);
                    }
                    this.mProcessStateTimer[mapToInternalProcessState].startRunningLocked(j);
                }
                if (this.mBsi.trackPerProcStateCpuTimes()) {
                    this.mBsi.updateProcStateCpuTimesLocked(this.mUid, j, j2);
                    LongArrayMultiStateCounter counter = getProcStateTimeCounter(j).getCounter();
                    LongArrayMultiStateCounter counter2 = getProcStateScreenOffTimeCounter(j).getCounter();
                    counter.setState(mapToInternalProcessState, j);
                    counter2.setState(mapToInternalProcessState, j);
                }
                int mapUidProcessStateToBatteryConsumerProcessState = BatteryStats.mapUidProcessStateToBatteryConsumerProcessState(this.mProcessState);
                this.mProcessState = mapToInternalProcessState;
                long j3 = j2 * 1000;
                long j4 = 1000 * j;
                updateOnBatteryBgTimeBase(j3, j4);
                updateOnBatteryScreenOffBgTimeBase(j3, j4);
                int mapUidProcessStateToBatteryConsumerProcessState2 = BatteryStats.mapUidProcessStateToBatteryConsumerProcessState(mapToInternalProcessState);
                getCpuActiveTimeCounter().setState(mapUidProcessStateToBatteryConsumerProcessState2, j);
                getMobileRadioActiveTimeCounter().setState(mapUidProcessStateToBatteryConsumerProcessState2, j);
                ControllerActivityCounterImpl wifiControllerActivity = getWifiControllerActivity();
                if (wifiControllerActivity != null) {
                    wifiControllerActivity.setState(mapUidProcessStateToBatteryConsumerProcessState2, j);
                }
                ControllerActivityCounterImpl bluetoothControllerActivity = getBluetoothControllerActivity();
                if (bluetoothControllerActivity != null) {
                    bluetoothControllerActivity.setState(mapUidProcessStateToBatteryConsumerProcessState2, j);
                }
                EnergyConsumerStats orCreateEnergyConsumerStatsIfSupportedLocked = getOrCreateEnergyConsumerStatsIfSupportedLocked();
                if (orCreateEnergyConsumerStatsIfSupportedLocked != null) {
                    orCreateEnergyConsumerStatsIfSupportedLocked.setState(mapUidProcessStateToBatteryConsumerProcessState2, j);
                }
                maybeScheduleExternalStatsSync(mapUidProcessStateToBatteryConsumerProcessState, mapUidProcessStateToBatteryConsumerProcessState2);
            }
            if (isForegroundService != this.mInForegroundService) {
                if (isForegroundService) {
                    noteForegroundServiceResumedLocked(j);
                } else {
                    noteForegroundServicePausedLocked(j);
                }
                this.mInForegroundService = isForegroundService;
            }
        }

        @GuardedBy({"mBsi"})
        public final void maybeScheduleExternalStatsSync(int i, int i2) {
            if (i == i2) {
                return;
            }
            if (i == 0 && i2 == 2) {
                return;
            }
            if (i == 2 && i2 == 0) {
                return;
            }
            this.mBsi.mExternalSync.scheduleSyncDueToProcessStateChange(!BatteryStatsImpl.isActiveRadioPowerState(this.mBsi.mMobileRadioPowerState) ? 10 : 14, this.mBsi.mConstants.PROC_STATE_CHANGE_COLLECTION_DELAY_MS);
        }

        public boolean isInBackground() {
            return this.mProcessState >= 3;
        }

        public boolean updateOnBatteryBgTimeBase(long j, long j2) {
            return this.mOnBatteryBackgroundTimeBase.setRunning(this.mBsi.mOnBatteryTimeBase.isRunning() && isInBackground(), j, j2);
        }

        public boolean updateOnBatteryScreenOffBgTimeBase(long j, long j2) {
            return this.mOnBatteryScreenOffBackgroundTimeBase.setRunning(this.mBsi.mOnBatteryScreenOffTimeBase.isRunning() && isInBackground(), j, j2);
        }

        public SparseArray<? extends BatteryStats.Uid.Pid> getPidStats() {
            return this.mPids;
        }

        public BatteryStats.Uid.Pid getPidStatsLocked(int i) {
            BatteryStats.Uid.Pid pid = this.mPids.get(i);
            if (pid == null) {
                BatteryStats.Uid.Pid pid2 = new BatteryStats.Uid.Pid(this);
                this.mPids.put(i, pid2);
                return pid2;
            }
            return pid;
        }

        public Pkg getPackageStatsLocked(String str) {
            Pkg pkg = this.mPackageStats.get(str);
            if (pkg == null) {
                Pkg pkg2 = new Pkg(this.mBsi);
                this.mPackageStats.put(str, pkg2);
                return pkg2;
            }
            return pkg;
        }

        public Pkg.Serv getServiceStatsLocked(String str, String str2) {
            Pkg packageStatsLocked = getPackageStatsLocked(str);
            Pkg.Serv serv = packageStatsLocked.mServiceStats.get(str2);
            if (serv == null) {
                Pkg.Serv newServiceStatsLocked = packageStatsLocked.newServiceStatsLocked();
                packageStatsLocked.mServiceStats.put(str2, newServiceStatsLocked);
                return newServiceStatsLocked;
            }
            return serv;
        }

        public void readSyncSummaryFromParcelLocked(String str, Parcel parcel) {
            DualTimer instantiateObject = this.mSyncStats.instantiateObject();
            instantiateObject.readSummaryFromParcelLocked(parcel);
            this.mSyncStats.add(str, instantiateObject);
        }

        public void readJobSummaryFromParcelLocked(String str, Parcel parcel) {
            DualTimer instantiateObject = this.mJobStats.instantiateObject();
            instantiateObject.readSummaryFromParcelLocked(parcel);
            this.mJobStats.add(str, instantiateObject);
        }

        public void readWakeSummaryFromParcelLocked(String str, Parcel parcel) {
            Wakelock wakelock = new Wakelock(this.mBsi, this);
            this.mWakelockStats.add(str, wakelock);
            if (parcel.readInt() != 0) {
                getWakelockTimerLocked(wakelock, 1).readSummaryFromParcelLocked(parcel);
            }
            if (parcel.readInt() != 0) {
                getWakelockTimerLocked(wakelock, 0).readSummaryFromParcelLocked(parcel);
            }
            if (parcel.readInt() != 0) {
                getWakelockTimerLocked(wakelock, 2).readSummaryFromParcelLocked(parcel);
            }
            if (parcel.readInt() != 0) {
                getWakelockTimerLocked(wakelock, 18).readSummaryFromParcelLocked(parcel);
            }
        }

        public DualTimer getSensorTimerLocked(int i, boolean z) {
            Sensor sensor = this.mSensorStats.get(i);
            if (sensor == null) {
                if (!z) {
                    return null;
                }
                sensor = new Sensor(this.mBsi, this, i);
                this.mSensorStats.put(i, sensor);
            }
            DualTimer dualTimer = sensor.mTimer;
            if (dualTimer != null) {
                return dualTimer;
            }
            ArrayList arrayList = (ArrayList) this.mBsi.mSensorTimers.get(i);
            if (arrayList == null) {
                arrayList = new ArrayList();
                this.mBsi.mSensorTimers.put(i, arrayList);
            }
            ArrayList arrayList2 = arrayList;
            BatteryStatsImpl batteryStatsImpl = this.mBsi;
            DualTimer dualTimer2 = new DualTimer(batteryStatsImpl.mClock, this, 3, arrayList2, batteryStatsImpl.mOnBatteryTimeBase, this.mOnBatteryBackgroundTimeBase);
            sensor.mTimer = dualTimer2;
            return dualTimer2;
        }

        public void noteStartSyncLocked(String str, long j) {
            DualTimer startObject = this.mSyncStats.startObject(str, j);
            if (startObject != null) {
                startObject.startRunningLocked(j);
            }
        }

        public void noteStopSyncLocked(String str, long j) {
            DualTimer stopObject = this.mSyncStats.stopObject(str, j);
            if (stopObject != null) {
                stopObject.stopRunningLocked(j);
            }
        }

        public void noteStartJobLocked(String str, long j) {
            DualTimer startObject = this.mJobStats.startObject(str, j);
            if (startObject != null) {
                startObject.startRunningLocked(j);
            }
        }

        public void noteStopJobLocked(String str, long j, int i) {
            DualTimer stopObject = this.mJobStats.stopObject(str, j);
            if (stopObject != null) {
                stopObject.stopRunningLocked(j);
            }
            if (this.mBsi.mOnBatteryTimeBase.isRunning()) {
                SparseIntArray sparseIntArray = this.mJobCompletions.get(str);
                if (sparseIntArray == null) {
                    sparseIntArray = new SparseIntArray();
                    this.mJobCompletions.put(str, sparseIntArray);
                }
                sparseIntArray.put(i, sparseIntArray.get(i, 0) + 1);
            }
        }

        public StopwatchTimer getWakelockTimerLocked(Wakelock wakelock, int i) {
            if (wakelock == null) {
                return null;
            }
            if (i == 0) {
                DualTimer dualTimer = wakelock.mTimerPartial;
                if (dualTimer == null) {
                    BatteryStatsImpl batteryStatsImpl = this.mBsi;
                    DualTimer dualTimer2 = new DualTimer(batteryStatsImpl.mClock, this, 0, batteryStatsImpl.mPartialTimers, batteryStatsImpl.mOnBatteryScreenOffTimeBase, this.mOnBatteryScreenOffBackgroundTimeBase);
                    wakelock.mTimerPartial = dualTimer2;
                    return dualTimer2;
                }
                return dualTimer;
            } else if (i == 1) {
                StopwatchTimer stopwatchTimer = wakelock.mTimerFull;
                if (stopwatchTimer == null) {
                    BatteryStatsImpl batteryStatsImpl2 = this.mBsi;
                    StopwatchTimer stopwatchTimer2 = new StopwatchTimer(batteryStatsImpl2.mClock, this, 1, batteryStatsImpl2.mFullTimers, this.mBsi.mOnBatteryTimeBase);
                    wakelock.mTimerFull = stopwatchTimer2;
                    return stopwatchTimer2;
                }
                return stopwatchTimer;
            } else if (i == 2) {
                StopwatchTimer stopwatchTimer3 = wakelock.mTimerWindow;
                if (stopwatchTimer3 == null) {
                    BatteryStatsImpl batteryStatsImpl3 = this.mBsi;
                    StopwatchTimer stopwatchTimer4 = new StopwatchTimer(batteryStatsImpl3.mClock, this, 2, batteryStatsImpl3.mWindowTimers, this.mBsi.mOnBatteryTimeBase);
                    wakelock.mTimerWindow = stopwatchTimer4;
                    return stopwatchTimer4;
                }
                return stopwatchTimer3;
            } else if (i == 18) {
                StopwatchTimer stopwatchTimer5 = wakelock.mTimerDraw;
                if (stopwatchTimer5 == null) {
                    BatteryStatsImpl batteryStatsImpl4 = this.mBsi;
                    StopwatchTimer stopwatchTimer6 = new StopwatchTimer(batteryStatsImpl4.mClock, this, 18, batteryStatsImpl4.mDrawTimers, this.mBsi.mOnBatteryTimeBase);
                    wakelock.mTimerDraw = stopwatchTimer6;
                    return stopwatchTimer6;
                }
                return stopwatchTimer5;
            } else {
                throw new IllegalArgumentException("type=" + i);
            }
        }

        public void noteStartWakeLocked(int i, String str, int i2, long j) {
            Wakelock startObject = this.mWakelockStats.startObject(str, j);
            if (startObject != null) {
                getWakelockTimerLocked(startObject, i2).startRunningLocked(j);
            }
            if (i2 == 0) {
                createAggregatedPartialWakelockTimerLocked().startRunningLocked(j);
                if (i >= 0) {
                    BatteryStats.Uid.Pid pidStatsLocked = getPidStatsLocked(i);
                    int i3 = pidStatsLocked.mWakeNesting;
                    pidStatsLocked.mWakeNesting = i3 + 1;
                    if (i3 == 0) {
                        pidStatsLocked.mWakeStartMs = j;
                    }
                }
            }
        }

        public void noteStopWakeLocked(int i, String str, int i2, long j) {
            BatteryStats.Uid.Pid pid;
            int i3;
            Wakelock stopObject = this.mWakelockStats.stopObject(str, j);
            if (stopObject != null) {
                getWakelockTimerLocked(stopObject, i2).stopRunningLocked(j);
            }
            if (i2 == 0) {
                DualTimer dualTimer = this.mAggregatedPartialWakelockTimer;
                if (dualTimer != null) {
                    dualTimer.stopRunningLocked(j);
                }
                if (i < 0 || (pid = this.mPids.get(i)) == null || (i3 = pid.mWakeNesting) <= 0) {
                    return;
                }
                pid.mWakeNesting = i3 - 1;
                if (i3 == 1) {
                    pid.mWakeSumMs += j - pid.mWakeStartMs;
                    pid.mWakeStartMs = 0L;
                }
            }
        }

        public void reportExcessiveCpuLocked(String str, long j, long j2) {
            Proc processStatsLocked = getProcessStatsLocked(str);
            if (processStatsLocked != null) {
                processStatsLocked.addExcessiveCpu(j, j2);
            }
        }

        public void noteStartSensor(int i, long j) {
            getSensorTimerLocked(i, true).startRunningLocked(j);
        }

        public void noteStopSensor(int i, long j) {
            DualTimer sensorTimerLocked = getSensorTimerLocked(i, false);
            if (sensorTimerLocked != null) {
                sensorTimerLocked.stopRunningLocked(j);
            }
        }

        public void noteStartGps(long j) {
            noteStartSensor(-10000, j);
        }

        public void noteStopGps(long j) {
            noteStopSensor(-10000, j);
        }
    }

    @GuardedBy({"this"})
    public long[] getCpuFreqs() {
        if (!this.mCpuFreqsInitialized) {
            this.mCpuFreqs = this.mCpuUidFreqTimeReader.readFreqs(this.mPowerProfile);
            this.mCpuFreqsInitialized = true;
        }
        return this.mCpuFreqs;
    }

    @GuardedBy({"this"})
    public int getCpuFreqCount() {
        long[] cpuFreqs = getCpuFreqs();
        if (cpuFreqs != null) {
            return cpuFreqs.length;
        }
        return 0;
    }

    @GuardedBy({"this"})
    public final LongArrayMultiStateCounter.LongArrayContainer getCpuTimeInFreqContainer() {
        if (this.mTmpCpuTimeInFreq == null) {
            this.mTmpCpuTimeInFreq = new LongArrayMultiStateCounter.LongArrayContainer(getCpuFreqCount());
        }
        return this.mTmpCpuTimeInFreq;
    }

    public BatteryStatsImpl(File file, Handler handler, PlatformIdleStateCallback platformIdleStateCallback, EnergyStatsRetriever energyStatsRetriever, UserInfoProvider userInfoProvider) {
        this(Clock.SYSTEM_CLOCK, file, handler, platformIdleStateCallback, energyStatsRetriever, userInfoProvider);
    }

    public BatteryStatsImpl(Clock clock, File file, Handler handler, PlatformIdleStateCallback platformIdleStateCallback, EnergyStatsRetriever energyStatsRetriever, UserInfoProvider userInfoProvider) {
        this.mKernelWakelockReader = new KernelWakelockReader();
        this.mTmpWakelockStats = new KernelWakelockStats();
        this.mSystemServerCpuThreadReader = SystemServerCpuThreadReader.create();
        this.mKernelMemoryBandwidthStats = new KernelMemoryBandwidthStats();
        this.mKernelMemoryStats = new LongSparseArray<>();
        this.mCpuUsageDetails = new BatteryStats.CpuUsageDetails();
        this.mPerProcStateCpuTimesAvailable = true;
        this.mCpuTimeReadsTrackingStartTimeMs = SystemClock.uptimeMillis();
        this.mTmpRpmStats = null;
        this.mLastRpmStatsUpdateTimeMs = -1000L;
        this.mTmpRailStats = new RailStats();
        this.mPendingRemovedUids = new LinkedList();
        this.mDeferSetCharging = new Runnable() { // from class: com.android.server.power.stats.BatteryStatsImpl.2
            @Override // java.lang.Runnable
            public void run() {
                synchronized (BatteryStatsImpl.this) {
                    BatteryStatsImpl batteryStatsImpl = BatteryStatsImpl.this;
                    if (batteryStatsImpl.mOnBattery) {
                        return;
                    }
                    if (batteryStatsImpl.setChargingLocked(true)) {
                        long uptimeMillis = BatteryStatsImpl.this.mClock.uptimeMillis();
                        BatteryStatsImpl.this.mHistory.writeHistoryItem(BatteryStatsImpl.this.mClock.elapsedRealtime(), uptimeMillis);
                    }
                }
            }
        };
        this.mExternalSync = null;
        this.mUserInfoProvider = null;
        this.mIsolatedUids = new SparseIntArray();
        this.mIsolatedUidRefCounts = new SparseIntArray();
        this.mUidStats = new SparseArray<>();
        this.mPartialTimers = new ArrayList<>();
        this.mFullTimers = new ArrayList<>();
        this.mWindowTimers = new ArrayList<>();
        this.mDrawTimers = new ArrayList<>();
        this.mSensorTimers = new SparseArray<>();
        this.mWifiRunningTimers = new ArrayList<>();
        this.mFullWifiLockTimers = new ArrayList<>();
        this.mWifiMulticastTimers = new ArrayList<>();
        this.mWifiScanTimers = new ArrayList<>();
        this.mWifiBatchedScanTimers = new SparseArray<>();
        this.mAudioTurnedOnTimers = new ArrayList<>();
        this.mVideoTurnedOnTimers = new ArrayList<>();
        this.mFlashlightTurnedOnTimers = new ArrayList<>();
        this.mCameraTurnedOnTimers = new ArrayList<>();
        this.mBluetoothScanOnTimers = new ArrayList<>();
        this.mLastPartialTimers = new ArrayList<>();
        this.mOnBatteryTimeBase = new TimeBase(true);
        this.mOnBatteryScreenOffTimeBase = new TimeBase(true);
        this.mActiveEvents = new BatteryStats.HistoryEventTracker();
        HistoryStepDetailsCalculatorImpl historyStepDetailsCalculatorImpl = new HistoryStepDetailsCalculatorImpl();
        this.mStepDetailsCalculator = historyStepDetailsCalculatorImpl;
        this.mHaveBatteryLevel = false;
        this.mBatteryPluggedInRealTimeMs = 0L;
        this.mBatteryVoltageMv = -1;
        this.mIgnoreNextExternalStats = false;
        this.mScreenState = 0;
        this.mScreenBrightnessBin = -1;
        this.mScreenBrightnessTimer = new StopwatchTimer[5];
        this.mDisplayMismatchWtfCount = 0;
        this.mUsbDataState = 0;
        this.mGpsSignalQualityBin = -1;
        this.mGpsSignalQualityTimer = new StopwatchTimer[2];
        this.mPhoneSignalStrengthBin = -1;
        this.mPhoneSignalStrengthBinRaw = -1;
        this.mPhoneSignalStrengthsTimer = new StopwatchTimer[CellSignalStrength.getNumSignalStrengthLevels()];
        this.mPhoneDataConnectionType = -1;
        this.mPhoneDataConnectionsTimer = new StopwatchTimer[BatteryStats.NUM_DATA_CONNECTION_TYPES];
        this.mActiveRat = 0;
        this.mPerRatBatteryStats = new RadioAccessTechnologyBatteryStats[3];
        this.mNetworkByteActivityCounters = new LongSamplingCounter[10];
        this.mNetworkPacketActivityCounters = new LongSamplingCounter[10];
        this.mHasWifiReporting = false;
        this.mHasBluetoothReporting = false;
        this.mHasModemReporting = false;
        this.mWifiState = -1;
        this.mWifiStateTimer = new StopwatchTimer[8];
        this.mWifiSupplState = -1;
        this.mWifiSupplStateTimer = new StopwatchTimer[13];
        this.mWifiSignalStrengthBin = -1;
        this.mWifiSignalStrengthsTimer = new StopwatchTimer[5];
        this.mMobileRadioPowerState = 1;
        this.mWifiRadioPowerState = 1;
        this.mBluetoothPowerCalculator = null;
        this.mCpuPowerCalculator = null;
        this.mMobileRadioPowerCalculator = null;
        this.mWifiPowerCalculator = null;
        this.mCharging = true;
        this.mInitStepMode = 0;
        this.mCurStepMode = 0;
        this.mModStepMode = 0;
        this.mDischargeStepTracker = new BatteryStats.LevelStepTracker(200);
        this.mDailyDischargeStepTracker = new BatteryStats.LevelStepTracker((int) FrameworkStatsLog.APP_PROCESS_DIED__IMPORTANCE__IMPORTANCE_BACKGROUND);
        this.mChargeStepTracker = new BatteryStats.LevelStepTracker(200);
        this.mDailyChargeStepTracker = new BatteryStats.LevelStepTracker((int) FrameworkStatsLog.APP_PROCESS_DIED__IMPORTANCE__IMPORTANCE_BACKGROUND);
        this.mDailyStartTimeMs = 0L;
        this.mNextMinDailyDeadlineMs = 0L;
        this.mNextMaxDailyDeadlineMs = 0L;
        this.mDailyItems = new ArrayList<>();
        this.mLastWriteTimeMs = 0L;
        this.mPhoneServiceState = -1;
        this.mPhoneServiceStateRaw = -1;
        this.mPhoneSimStateRaw = -1;
        this.mEstimatedBatteryCapacityMah = -1;
        this.mLastLearnedBatteryCapacityUah = -1;
        this.mMinLearnedBatteryCapacityUah = -1;
        this.mMaxLearnedBatteryCapacityUah = -1;
        this.mBatteryTimeToFullSeconds = -1L;
        this.mBatteryStatsConfig = new BatteryStatsConfig.Builder().build();
        this.mLongPlugInAlarmInterface = null;
        this.mRpmStats = new HashMap<>();
        this.mScreenOffRpmStats = new HashMap<>();
        this.mKernelWakelockStats = new HashMap<>();
        this.mLastWakeupReason = null;
        this.mLastWakeupUptimeMs = 0L;
        this.mWakeupReasonStats = new HashMap<>();
        this.mWifiFullLockNesting = 0;
        this.mWifiScanNesting = 0;
        this.mWifiMulticastNesting = 0;
        this.mWifiNetworkLock = new Object();
        this.mWifiIfaces = EmptyArray.STRING;
        this.mLastWifiNetworkStats = new NetworkStats(0L, -1);
        this.mModemNetworkLock = new Object();
        this.mModemIfaces = EmptyArray.STRING;
        this.mLastModemNetworkStats = new NetworkStats(0L, -1);
        this.mLastModemActivityInfo = null;
        this.mLastBluetoothActivityInfo = new BluetoothActivityInfoCache();
        this.mWriteAsyncRunnable = new Runnable() { // from class: com.android.server.power.stats.BatteryStatsImpl$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                BatteryStatsImpl.this.lambda$new$4();
            }
        };
        this.mWriteLock = new ReentrantLock();
        init(clock);
        this.mHandler = new MyHandler(handler.getLooper());
        Constants constants = new Constants(this.mHandler);
        this.mConstants = constants;
        if (file == null) {
            this.mStatsFile = null;
            this.mCheckinFile = null;
            this.mDailyFile = null;
            this.mHistory = new BatteryStatsHistory(constants.MAX_HISTORY_FILES, constants.MAX_HISTORY_BUFFER, historyStepDetailsCalculatorImpl, this.mClock);
        } else {
            this.mStatsFile = new AtomicFile(new File(file, "batterystats.bin"));
            this.mCheckinFile = new AtomicFile(new File(file, "batterystats-checkin.bin"));
            this.mDailyFile = new AtomicFile(new File(file, "batterystats-daily.xml"));
            this.mHistory = new BatteryStatsHistory(file, constants.MAX_HISTORY_FILES, constants.MAX_HISTORY_BUFFER, historyStepDetailsCalculatorImpl, this.mClock);
        }
        this.mStartCount++;
        initTimersAndCounters();
        this.mOnBatteryInternal = false;
        this.mOnBattery = false;
        long elapsedRealtime = this.mClock.elapsedRealtime() * 1000;
        initTimes(this.mClock.uptimeMillis() * 1000, elapsedRealtime);
        String str = Build.ID;
        this.mEndPlatformVersion = str;
        this.mStartPlatformVersion = str;
        initDischarge(elapsedRealtime);
        updateDailyDeadlineLocked();
        this.mPlatformIdleStateCallback = platformIdleStateCallback;
        this.mEnergyConsumerRetriever = energyStatsRetriever;
        this.mUserInfoProvider = userInfoProvider;
        this.mDeviceIdleMode = 0;
        FrameworkStatsLog.write(21, 0);
    }

    @VisibleForTesting
    public void initTimersAndCounters() {
        this.mScreenOnTimer = new StopwatchTimer(this.mClock, null, -1, null, this.mOnBatteryTimeBase);
        this.mScreenDozeTimer = new StopwatchTimer(this.mClock, null, -1, null, this.mOnBatteryTimeBase);
        for (int i = 0; i < 5; i++) {
            this.mScreenBrightnessTimer[i] = new StopwatchTimer(this.mClock, null, (-100) - i, null, this.mOnBatteryTimeBase);
        }
        this.mPerDisplayBatteryStats = r4;
        DisplayBatteryStats[] displayBatteryStatsArr = {new DisplayBatteryStats(this.mClock, this.mOnBatteryTimeBase)};
        this.mInteractiveTimer = new StopwatchTimer(this.mClock, null, -10, null, this.mOnBatteryTimeBase);
        this.mPowerSaveModeEnabledTimer = new StopwatchTimer(this.mClock, null, -2, null, this.mOnBatteryTimeBase);
        this.mDeviceIdleModeLightTimer = new StopwatchTimer(this.mClock, null, -11, null, this.mOnBatteryTimeBase);
        this.mDeviceIdleModeFullTimer = new StopwatchTimer(this.mClock, null, -14, null, this.mOnBatteryTimeBase);
        this.mDeviceLightIdlingTimer = new StopwatchTimer(this.mClock, null, -15, null, this.mOnBatteryTimeBase);
        this.mDeviceIdlingTimer = new StopwatchTimer(this.mClock, null, -12, null, this.mOnBatteryTimeBase);
        this.mPhoneOnTimer = new StopwatchTimer(this.mClock, null, -3, null, this.mOnBatteryTimeBase);
        for (int i2 = 0; i2 < CellSignalStrength.getNumSignalStrengthLevels(); i2++) {
            this.mPhoneSignalStrengthsTimer[i2] = new StopwatchTimer(this.mClock, null, (-200) - i2, null, this.mOnBatteryTimeBase);
        }
        this.mPhoneSignalScanningTimer = new StopwatchTimer(this.mClock, null, -199, null, this.mOnBatteryTimeBase);
        for (int i3 = 0; i3 < BatteryStats.NUM_DATA_CONNECTION_TYPES; i3++) {
            this.mPhoneDataConnectionsTimer[i3] = new StopwatchTimer(this.mClock, null, (-300) - i3, null, this.mOnBatteryTimeBase);
        }
        for (int i4 = 0; i4 < 10; i4++) {
            this.mNetworkByteActivityCounters[i4] = new LongSamplingCounter(this.mOnBatteryTimeBase);
            this.mNetworkPacketActivityCounters[i4] = new LongSamplingCounter(this.mOnBatteryTimeBase);
        }
        this.mWifiActivity = new ControllerActivityCounterImpl(this.mClock, this.mOnBatteryTimeBase, 1);
        this.mBluetoothActivity = new ControllerActivityCounterImpl(this.mClock, this.mOnBatteryTimeBase, 1);
        this.mModemActivity = new ControllerActivityCounterImpl(this.mClock, this.mOnBatteryTimeBase, ModemActivityInfo.getNumTxPowerLevels());
        this.mMobileRadioActiveTimer = new StopwatchTimer(this.mClock, null, -400, null, this.mOnBatteryTimeBase);
        this.mMobileRadioActivePerAppTimer = new StopwatchTimer(this.mClock, null, -401, null, this.mOnBatteryTimeBase);
        this.mMobileRadioActiveAdjustedTime = new LongSamplingCounter(this.mOnBatteryTimeBase);
        this.mMobileRadioActiveUnknownTime = new LongSamplingCounter(this.mOnBatteryTimeBase);
        this.mMobileRadioActiveUnknownCount = new LongSamplingCounter(this.mOnBatteryTimeBase);
        this.mWifiMulticastWakelockTimer = new StopwatchTimer(this.mClock, null, 23, null, this.mOnBatteryTimeBase);
        this.mWifiOnTimer = new StopwatchTimer(this.mClock, null, -4, null, this.mOnBatteryTimeBase);
        this.mGlobalWifiRunningTimer = new StopwatchTimer(this.mClock, null, -5, null, this.mOnBatteryTimeBase);
        for (int i5 = 0; i5 < 8; i5++) {
            this.mWifiStateTimer[i5] = new StopwatchTimer(this.mClock, null, (-600) - i5, null, this.mOnBatteryTimeBase);
        }
        for (int i6 = 0; i6 < 13; i6++) {
            this.mWifiSupplStateTimer[i6] = new StopwatchTimer(this.mClock, null, (-700) - i6, null, this.mOnBatteryTimeBase);
        }
        for (int i7 = 0; i7 < 5; i7++) {
            this.mWifiSignalStrengthsTimer[i7] = new StopwatchTimer(this.mClock, null, (-800) - i7, null, this.mOnBatteryTimeBase);
        }
        this.mWifiActiveTimer = new StopwatchTimer(this.mClock, null, -900, null, this.mOnBatteryTimeBase);
        int i8 = 0;
        while (true) {
            StopwatchTimer[] stopwatchTimerArr = this.mGpsSignalQualityTimer;
            if (i8 < stopwatchTimerArr.length) {
                stopwatchTimerArr[i8] = new StopwatchTimer(this.mClock, null, (-1000) - i8, null, this.mOnBatteryTimeBase);
                i8++;
            } else {
                this.mAudioOnTimer = new StopwatchTimer(this.mClock, null, -7, null, this.mOnBatteryTimeBase);
                this.mVideoOnTimer = new StopwatchTimer(this.mClock, null, -8, null, this.mOnBatteryTimeBase);
                this.mFlashlightOnTimer = new StopwatchTimer(this.mClock, null, -9, null, this.mOnBatteryTimeBase);
                this.mCameraOnTimer = new StopwatchTimer(this.mClock, null, -13, null, this.mOnBatteryTimeBase);
                this.mBluetoothScanTimer = new StopwatchTimer(this.mClock, null, -14, null, this.mOnBatteryTimeBase);
                this.mDischargeScreenOffCounter = new LongSamplingCounter(this.mOnBatteryScreenOffTimeBase);
                this.mDischargeScreenDozeCounter = new LongSamplingCounter(this.mOnBatteryTimeBase);
                this.mDischargeLightDozeCounter = new LongSamplingCounter(this.mOnBatteryTimeBase);
                this.mDischargeDeepDozeCounter = new LongSamplingCounter(this.mOnBatteryTimeBase);
                this.mDischargeCounter = new LongSamplingCounter(this.mOnBatteryTimeBase);
                this.mDischargeUnplugLevel = 0;
                this.mDischargePlugLevel = -1;
                this.mDischargeCurrentLevel = 0;
                this.mBatteryLevel = 0;
                return;
            }
        }
    }

    @GuardedBy({"this"})
    public void setPowerProfileLocked(PowerProfile powerProfile) {
        this.mPowerProfile = powerProfile;
        int numCpuClusters = powerProfile.getNumCpuClusters();
        this.mKernelCpuSpeedReaders = new KernelCpuSpeedReader[numCpuClusters];
        int i = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < numCpuClusters; i3++) {
            int numSpeedStepsInCpuCluster = this.mPowerProfile.getNumSpeedStepsInCpuCluster(i3);
            this.mKernelCpuSpeedReaders[i3] = new KernelCpuSpeedReader(i2, numSpeedStepsInCpuCluster);
            i2 += this.mPowerProfile.getNumCoresInCpuCluster(i3);
            i += numSpeedStepsInCpuCluster;
        }
        this.mCpuPowerBracketMap = new int[i];
        int numCpuClusters2 = this.mPowerProfile.getNumCpuClusters();
        int i4 = 0;
        for (int i5 = 0; i5 < numCpuClusters2; i5++) {
            int numSpeedStepsInCpuCluster2 = this.mPowerProfile.getNumSpeedStepsInCpuCluster(i5);
            int i6 = 0;
            while (i6 < numSpeedStepsInCpuCluster2) {
                this.mCpuPowerBracketMap[i4] = this.mPowerProfile.getPowerBracketForCpuCore(i5, i6);
                i6++;
                i4++;
            }
        }
        int cpuPowerBracketCount = this.mPowerProfile.getCpuPowerBracketCount();
        BatteryStats.CpuUsageDetails cpuUsageDetails = this.mCpuUsageDetails;
        cpuUsageDetails.cpuBracketDescriptions = new String[cpuPowerBracketCount];
        cpuUsageDetails.cpuUsageMs = new long[cpuPowerBracketCount];
        for (int i7 = 0; i7 < cpuPowerBracketCount; i7++) {
            this.mCpuUsageDetails.cpuBracketDescriptions[i7] = this.mPowerProfile.getCpuPowerBracketDescription(i7);
        }
        if (this.mEstimatedBatteryCapacityMah == -1) {
            this.mEstimatedBatteryCapacityMah = (int) this.mPowerProfile.getBatteryCapacity();
        }
        setDisplayCountLocked(this.mPowerProfile.getNumDisplays());
    }

    public PowerProfile getPowerProfile() {
        return this.mPowerProfile;
    }

    public void setBatteryStatsConfig(BatteryStatsConfig batteryStatsConfig) {
        synchronized (this) {
            this.mBatteryStatsConfig = batteryStatsConfig;
        }
    }

    public void setLongPlugInAlarmInterface(AlarmInterface alarmInterface) {
        synchronized (this) {
            this.mLongPlugInAlarmInterface = alarmInterface;
            if (this.mBatteryPluggedIn) {
                scheduleNextResetWhilePluggedInCheck();
            }
        }
    }

    public void startTrackingSystemServerCpuTime() {
        this.mSystemServerCpuThreadReader.startTrackingThreadCpuTime();
    }

    public SystemServerCpuThreadReader.SystemServiceCpuThreadTimes getSystemServiceCpuThreadTimes() {
        return this.mSystemServerCpuThreadReader.readAbsolute();
    }

    public void setCallback(BatteryCallback batteryCallback) {
        this.mCallback = batteryCallback;
    }

    public void setRadioScanningTimeoutLocked(long j) {
        StopwatchTimer stopwatchTimer = this.mPhoneSignalScanningTimer;
        if (stopwatchTimer != null) {
            stopwatchTimer.setTimeout(j);
        }
    }

    public void setExternalStatsSyncLocked(ExternalStatsSync externalStatsSync) {
        this.mExternalSync = externalStatsSync;
    }

    public void setDisplayCountLocked(int i) {
        this.mPerDisplayBatteryStats = new DisplayBatteryStats[i];
        for (int i2 = 0; i2 < i; i2++) {
            this.mPerDisplayBatteryStats[i2] = new DisplayBatteryStats(this.mClock, this.mOnBatteryTimeBase);
        }
    }

    public void updateDailyDeadlineLocked() {
        long currentTimeMillis = this.mClock.currentTimeMillis();
        this.mDailyStartTimeMs = currentTimeMillis;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTimeMillis);
        calendar.set(6, calendar.get(6) + 1);
        calendar.set(14, 0);
        calendar.set(13, 0);
        calendar.set(12, 0);
        calendar.set(11, 1);
        this.mNextMinDailyDeadlineMs = calendar.getTimeInMillis();
        calendar.set(11, 3);
        this.mNextMaxDailyDeadlineMs = calendar.getTimeInMillis();
    }

    public void recordDailyStatsIfNeededLocked(boolean z, long j) {
        if (j >= this.mNextMaxDailyDeadlineMs) {
            recordDailyStatsLocked();
        } else if (z && j >= this.mNextMinDailyDeadlineMs) {
            recordDailyStatsLocked();
        } else if (j < this.mDailyStartTimeMs - BackupManagerConstants.DEFAULT_FULL_BACKUP_INTERVAL_MILLISECONDS) {
            recordDailyStatsLocked();
        }
    }

    public void recordDailyStatsLocked() {
        boolean z;
        BatteryStats.DailyItem dailyItem = new BatteryStats.DailyItem();
        dailyItem.mStartTime = this.mDailyStartTimeMs;
        dailyItem.mEndTime = this.mClock.currentTimeMillis();
        boolean z2 = true;
        if (this.mDailyDischargeStepTracker.mNumStepDurations > 0) {
            BatteryStats.LevelStepTracker levelStepTracker = this.mDailyDischargeStepTracker;
            dailyItem.mDischargeSteps = new BatteryStats.LevelStepTracker(levelStepTracker.mNumStepDurations, levelStepTracker.mStepDurations);
            z = true;
        } else {
            z = false;
        }
        if (this.mDailyChargeStepTracker.mNumStepDurations > 0) {
            BatteryStats.LevelStepTracker levelStepTracker2 = this.mDailyChargeStepTracker;
            dailyItem.mChargeSteps = new BatteryStats.LevelStepTracker(levelStepTracker2.mNumStepDurations, levelStepTracker2.mStepDurations);
            z = true;
        }
        ArrayList<BatteryStats.PackageChange> arrayList = this.mDailyPackageChanges;
        if (arrayList != null) {
            dailyItem.mPackageChanges = arrayList;
            this.mDailyPackageChanges = null;
        } else {
            z2 = z;
        }
        this.mDailyDischargeStepTracker.init();
        this.mDailyChargeStepTracker.init();
        updateDailyDeadlineLocked();
        if (z2) {
            long uptimeMillis = SystemClock.uptimeMillis();
            this.mDailyItems.add(dailyItem);
            while (this.mDailyItems.size() > 10) {
                this.mDailyItems.remove(0);
            }
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                writeDailyItemsLocked(Xml.resolveSerializer(byteArrayOutputStream));
                final long uptimeMillis2 = SystemClock.uptimeMillis() - uptimeMillis;
                BackgroundThread.getHandler().post(new Runnable() { // from class: com.android.server.power.stats.BatteryStatsImpl.4
                    @Override // java.lang.Runnable
                    public void run() {
                        synchronized (BatteryStatsImpl.this.mCheckinFile) {
                            long uptimeMillis3 = SystemClock.uptimeMillis();
                            FileOutputStream fileOutputStream = null;
                            try {
                                fileOutputStream = BatteryStatsImpl.this.mDailyFile.startWrite();
                                byteArrayOutputStream.writeTo(fileOutputStream);
                                fileOutputStream.flush();
                                BatteryStatsImpl.this.mDailyFile.finishWrite(fileOutputStream);
                                EventLogTags.writeCommitSysConfigFile("batterystats-daily", (uptimeMillis2 + SystemClock.uptimeMillis()) - uptimeMillis3);
                            } catch (IOException e) {
                                Slog.w("BatteryStats", "Error writing battery daily items", e);
                                BatteryStatsImpl.this.mDailyFile.failWrite(fileOutputStream);
                            }
                        }
                    }
                });
            } catch (IOException unused) {
            }
        }
    }

    public final void writeDailyItemsLocked(TypedXmlSerializer typedXmlSerializer) throws IOException {
        StringBuilder sb = new StringBuilder(64);
        typedXmlSerializer.startDocument((String) null, Boolean.TRUE);
        typedXmlSerializer.startTag((String) null, "daily-items");
        for (int i = 0; i < this.mDailyItems.size(); i++) {
            BatteryStats.DailyItem dailyItem = this.mDailyItems.get(i);
            typedXmlSerializer.startTag((String) null, "item");
            typedXmlSerializer.attributeLong((String) null, "start", dailyItem.mStartTime);
            typedXmlSerializer.attributeLong((String) null, "end", dailyItem.mEndTime);
            writeDailyLevelSteps(typedXmlSerializer, "dis", dailyItem.mDischargeSteps, sb);
            writeDailyLevelSteps(typedXmlSerializer, "chg", dailyItem.mChargeSteps, sb);
            if (dailyItem.mPackageChanges != null) {
                for (int i2 = 0; i2 < dailyItem.mPackageChanges.size(); i2++) {
                    BatteryStats.PackageChange packageChange = (BatteryStats.PackageChange) dailyItem.mPackageChanges.get(i2);
                    if (packageChange.mUpdate) {
                        typedXmlSerializer.startTag((String) null, "upd");
                        typedXmlSerializer.attribute((String) null, "pkg", packageChange.mPackageName);
                        typedXmlSerializer.attributeLong((String) null, "ver", packageChange.mVersionCode);
                        typedXmlSerializer.endTag((String) null, "upd");
                    } else {
                        typedXmlSerializer.startTag((String) null, "rem");
                        typedXmlSerializer.attribute((String) null, "pkg", packageChange.mPackageName);
                        typedXmlSerializer.endTag((String) null, "rem");
                    }
                }
            }
            typedXmlSerializer.endTag((String) null, "item");
        }
        typedXmlSerializer.endTag((String) null, "daily-items");
        typedXmlSerializer.endDocument();
    }

    public final void writeDailyLevelSteps(TypedXmlSerializer typedXmlSerializer, String str, BatteryStats.LevelStepTracker levelStepTracker, StringBuilder sb) throws IOException {
        if (levelStepTracker != null) {
            typedXmlSerializer.startTag((String) null, str);
            typedXmlSerializer.attributeInt((String) null, "n", levelStepTracker.mNumStepDurations);
            for (int i = 0; i < levelStepTracker.mNumStepDurations; i++) {
                typedXmlSerializer.startTag((String) null, "s");
                sb.setLength(0);
                levelStepTracker.encodeEntryAt(i, sb);
                typedXmlSerializer.attribute((String) null, "v", sb.toString());
                typedXmlSerializer.endTag((String) null, "s");
            }
            typedXmlSerializer.endTag((String) null, str);
        }
    }

    @GuardedBy({"this"})
    public void readDailyStatsLocked() {
        Slog.d("BatteryStatsImpl", "Reading daily items from " + this.mDailyFile.getBaseFile());
        this.mDailyItems.clear();
        try {
            FileInputStream openRead = this.mDailyFile.openRead();
            try {
                readDailyItemsLocked(Xml.resolvePullParser(openRead));
            } catch (IOException unused) {
            } catch (Throwable th) {
                try {
                    openRead.close();
                } catch (IOException unused2) {
                }
                throw th;
            }
            openRead.close();
        } catch (FileNotFoundException | IOException unused3) {
        }
    }

    public final void readDailyItemsLocked(TypedXmlPullParser typedXmlPullParser) {
        int next;
        while (true) {
            try {
                next = typedXmlPullParser.next();
                if (next == 2 || next == 1) {
                    break;
                }
            } catch (IOException e) {
                Slog.w("BatteryStatsImpl", "Failed parsing daily " + e);
                return;
            } catch (IllegalStateException e2) {
                Slog.w("BatteryStatsImpl", "Failed parsing daily " + e2);
                return;
            } catch (IndexOutOfBoundsException e3) {
                Slog.w("BatteryStatsImpl", "Failed parsing daily " + e3);
                return;
            } catch (NullPointerException e4) {
                Slog.w("BatteryStatsImpl", "Failed parsing daily " + e4);
                return;
            } catch (NumberFormatException e5) {
                Slog.w("BatteryStatsImpl", "Failed parsing daily " + e5);
                return;
            } catch (XmlPullParserException e6) {
                Slog.w("BatteryStatsImpl", "Failed parsing daily " + e6);
                return;
            }
        }
        if (next != 2) {
            throw new IllegalStateException("no start tag found");
        }
        int depth = typedXmlPullParser.getDepth();
        while (true) {
            int next2 = typedXmlPullParser.next();
            if (next2 == 1) {
                return;
            }
            if (next2 == 3 && typedXmlPullParser.getDepth() <= depth) {
                return;
            }
            if (next2 != 3 && next2 != 4) {
                if (typedXmlPullParser.getName().equals("item")) {
                    readDailyItemTagLocked(typedXmlPullParser);
                } else {
                    Slog.w("BatteryStatsImpl", "Unknown element under <daily-items>: " + typedXmlPullParser.getName());
                    XmlUtils.skipCurrentTag(typedXmlPullParser);
                }
            }
        }
    }

    public void readDailyItemTagLocked(TypedXmlPullParser typedXmlPullParser) throws NumberFormatException, XmlPullParserException, IOException {
        BatteryStats.DailyItem dailyItem = new BatteryStats.DailyItem();
        dailyItem.mStartTime = typedXmlPullParser.getAttributeLong((String) null, "start", 0L);
        dailyItem.mEndTime = typedXmlPullParser.getAttributeLong((String) null, "end", 0L);
        int depth = typedXmlPullParser.getDepth();
        while (true) {
            int next = typedXmlPullParser.next();
            if (next == 1 || (next == 3 && typedXmlPullParser.getDepth() <= depth)) {
                break;
            } else if (next != 3 && next != 4) {
                String name = typedXmlPullParser.getName();
                if (name.equals("dis")) {
                    readDailyItemTagDetailsLocked(typedXmlPullParser, dailyItem, false, "dis");
                } else if (name.equals("chg")) {
                    readDailyItemTagDetailsLocked(typedXmlPullParser, dailyItem, true, "chg");
                } else if (name.equals("upd")) {
                    if (dailyItem.mPackageChanges == null) {
                        dailyItem.mPackageChanges = new ArrayList();
                    }
                    BatteryStats.PackageChange packageChange = new BatteryStats.PackageChange();
                    packageChange.mUpdate = true;
                    packageChange.mPackageName = typedXmlPullParser.getAttributeValue((String) null, "pkg");
                    packageChange.mVersionCode = typedXmlPullParser.getAttributeLong((String) null, "ver", 0L);
                    dailyItem.mPackageChanges.add(packageChange);
                    XmlUtils.skipCurrentTag(typedXmlPullParser);
                } else if (name.equals("rem")) {
                    if (dailyItem.mPackageChanges == null) {
                        dailyItem.mPackageChanges = new ArrayList();
                    }
                    BatteryStats.PackageChange packageChange2 = new BatteryStats.PackageChange();
                    packageChange2.mUpdate = false;
                    packageChange2.mPackageName = typedXmlPullParser.getAttributeValue((String) null, "pkg");
                    dailyItem.mPackageChanges.add(packageChange2);
                    XmlUtils.skipCurrentTag(typedXmlPullParser);
                } else {
                    Slog.w("BatteryStatsImpl", "Unknown element under <item>: " + typedXmlPullParser.getName());
                    XmlUtils.skipCurrentTag(typedXmlPullParser);
                }
            }
        }
        this.mDailyItems.add(dailyItem);
    }

    public void readDailyItemTagDetailsLocked(TypedXmlPullParser typedXmlPullParser, BatteryStats.DailyItem dailyItem, boolean z, String str) throws NumberFormatException, XmlPullParserException, IOException {
        String attributeValue;
        int attributeInt = typedXmlPullParser.getAttributeInt((String) null, "n", -1);
        if (attributeInt == -1) {
            Slog.w("BatteryStatsImpl", "Missing 'n' attribute at " + typedXmlPullParser.getPositionDescription());
            XmlUtils.skipCurrentTag(typedXmlPullParser);
            return;
        }
        BatteryStats.LevelStepTracker levelStepTracker = new BatteryStats.LevelStepTracker(attributeInt);
        if (z) {
            dailyItem.mChargeSteps = levelStepTracker;
        } else {
            dailyItem.mDischargeSteps = levelStepTracker;
        }
        int depth = typedXmlPullParser.getDepth();
        int i = 0;
        while (true) {
            int next = typedXmlPullParser.next();
            if (next == 1 || (next == 3 && typedXmlPullParser.getDepth() <= depth)) {
                break;
            } else if (next != 3 && next != 4) {
                if (!"s".equals(typedXmlPullParser.getName())) {
                    Slog.w("BatteryStatsImpl", "Unknown element under <" + str + ">: " + typedXmlPullParser.getName());
                    XmlUtils.skipCurrentTag(typedXmlPullParser);
                } else if (i < attributeInt && (attributeValue = typedXmlPullParser.getAttributeValue((String) null, "v")) != null) {
                    levelStepTracker.decodeEntryAt(i, attributeValue);
                    i++;
                }
            }
        }
        levelStepTracker.mNumStepDurations = i;
    }

    public BatteryStats.DailyItem getDailyItemLocked(int i) {
        int size = (this.mDailyItems.size() - 1) - i;
        if (size >= 0) {
            return this.mDailyItems.get(size);
        }
        return null;
    }

    public long getCurrentDailyStartTime() {
        return this.mDailyStartTimeMs;
    }

    public long getNextMinDailyDeadline() {
        return this.mNextMinDailyDeadlineMs;
    }

    public long getNextMaxDailyDeadline() {
        return this.mNextMaxDailyDeadlineMs;
    }

    @GuardedBy({"this"})
    public int getHistoryTotalSize() {
        Constants constants = this.mConstants;
        return constants.MAX_HISTORY_BUFFER * constants.MAX_HISTORY_FILES;
    }

    public int getHistoryUsedSize() {
        return this.mHistory.getHistoryUsedSize();
    }

    public BatteryStatsHistoryIterator iterateBatteryStatsHistory() {
        return this.mHistory.copy().iterate();
    }

    public int getHistoryStringPoolSize() {
        return this.mHistory.getHistoryStringPoolSize();
    }

    public int getHistoryStringPoolBytes() {
        return this.mHistory.getHistoryStringPoolBytes();
    }

    public String getHistoryTagPoolString(int i) {
        return this.mHistory.getHistoryTagPoolString(i);
    }

    public int getHistoryTagPoolUid(int i) {
        return this.mHistory.getHistoryTagPoolUid(i);
    }

    public int getStartCount() {
        return this.mStartCount;
    }

    public boolean isOnBattery() {
        return this.mOnBattery;
    }

    public boolean isCharging() {
        return this.mCharging;
    }

    public void initTimes(long j, long j2) {
        this.mStartClockTimeMs = this.mClock.currentTimeMillis();
        this.mOnBatteryTimeBase.init(j, j2);
        this.mOnBatteryScreenOffTimeBase.init(j, j2);
        this.mRealtimeUs = 0L;
        this.mUptimeUs = 0L;
        this.mRealtimeStartUs = j2;
        this.mUptimeStartUs = j;
    }

    public void initDischarge(long j) {
        this.mLowDischargeAmountSinceCharge = 0;
        this.mHighDischargeAmountSinceCharge = 0;
        this.mDischargeAmountScreenOn = 0;
        this.mDischargeAmountScreenOnSinceCharge = 0;
        this.mDischargeAmountScreenOff = 0;
        this.mDischargeAmountScreenOffSinceCharge = 0;
        this.mDischargeAmountScreenDoze = 0;
        this.mDischargeAmountScreenDozeSinceCharge = 0;
        this.mDischargeStepTracker.init();
        this.mChargeStepTracker.init();
        this.mDischargeScreenOffCounter.reset(false, j);
        this.mDischargeScreenDozeCounter.reset(false, j);
        this.mDischargeLightDozeCounter.reset(false, j);
        this.mDischargeDeepDozeCounter.reset(false, j);
        this.mDischargeCounter.reset(false, j);
    }

    public void setBatteryResetListener(BatteryResetListener batteryResetListener) {
        this.mBatteryResetListener = batteryResetListener;
    }

    @GuardedBy({"this"})
    public void resetAllStatsAndHistoryLocked(int i) {
        long uptimeMillis = this.mClock.uptimeMillis();
        long j = uptimeMillis * 1000;
        long elapsedRealtime = this.mClock.elapsedRealtime();
        long j2 = elapsedRealtime * 1000;
        resetAllStatsLocked(uptimeMillis, elapsedRealtime, i);
        pullPendingStateUpdatesLocked();
        this.mHistory.writeHistoryItem(elapsedRealtime, uptimeMillis);
        int i2 = this.mBatteryLevel;
        this.mDischargePlugLevel = i2;
        this.mDischargeUnplugLevel = i2;
        this.mDischargeCurrentLevel = i2;
        this.mOnBatteryTimeBase.reset(j, j2);
        this.mOnBatteryScreenOffTimeBase.reset(j, j2);
        if (!this.mBatteryPluggedIn) {
            if (Display.isOnState(this.mScreenState)) {
                this.mDischargeScreenOnUnplugLevel = this.mBatteryLevel;
                this.mDischargeScreenDozeUnplugLevel = 0;
                this.mDischargeScreenOffUnplugLevel = 0;
            } else if (Display.isDozeState(this.mScreenState)) {
                this.mDischargeScreenOnUnplugLevel = 0;
                this.mDischargeScreenDozeUnplugLevel = this.mBatteryLevel;
                this.mDischargeScreenOffUnplugLevel = 0;
            } else {
                this.mDischargeScreenOnUnplugLevel = 0;
                this.mDischargeScreenDozeUnplugLevel = 0;
                this.mDischargeScreenOffUnplugLevel = this.mBatteryLevel;
            }
            this.mDischargeAmountScreenOn = 0;
            this.mDischargeAmountScreenOff = 0;
            this.mDischargeAmountScreenDoze = 0;
        }
        initActiveHistoryEventsLocked(elapsedRealtime, uptimeMillis);
    }

    @GuardedBy({"this"})
    public final void resetAllStatsLocked(long j, long j2, int i) {
        BatteryResetListener batteryResetListener = this.mBatteryResetListener;
        if (batteryResetListener != null) {
            batteryResetListener.prepareForBatteryStatsReset(i);
        }
        long j3 = j * 1000;
        long j4 = 1000 * j2;
        this.mStartCount = 0;
        initTimes(j3, j4);
        this.mScreenOnTimer.reset(false, j4);
        this.mScreenDozeTimer.reset(false, j4);
        for (int i2 = 0; i2 < 5; i2++) {
            this.mScreenBrightnessTimer[i2].reset(false, j4);
        }
        int length = this.mPerDisplayBatteryStats.length;
        for (int i3 = 0; i3 < length; i3++) {
            this.mPerDisplayBatteryStats[i3].reset(j4);
        }
        PowerProfile powerProfile = this.mPowerProfile;
        if (powerProfile != null) {
            this.mEstimatedBatteryCapacityMah = (int) powerProfile.getBatteryCapacity();
        } else {
            this.mEstimatedBatteryCapacityMah = -1;
        }
        this.mLastLearnedBatteryCapacityUah = -1;
        this.mMinLearnedBatteryCapacityUah = -1;
        this.mMaxLearnedBatteryCapacityUah = -1;
        this.mInteractiveTimer.reset(false, j4);
        this.mPowerSaveModeEnabledTimer.reset(false, j4);
        this.mLastIdleTimeStartMs = j2;
        this.mLongestLightIdleTimeMs = 0L;
        this.mLongestFullIdleTimeMs = 0L;
        this.mDeviceIdleModeLightTimer.reset(false, j4);
        this.mDeviceIdleModeFullTimer.reset(false, j4);
        this.mDeviceLightIdlingTimer.reset(false, j4);
        this.mDeviceIdlingTimer.reset(false, j4);
        this.mPhoneOnTimer.reset(false, j4);
        this.mAudioOnTimer.reset(false, j4);
        this.mVideoOnTimer.reset(false, j4);
        this.mFlashlightOnTimer.reset(false, j4);
        this.mCameraOnTimer.reset(false, j4);
        this.mBluetoothScanTimer.reset(false, j4);
        for (int i4 = 0; i4 < CellSignalStrength.getNumSignalStrengthLevels(); i4++) {
            this.mPhoneSignalStrengthsTimer[i4].reset(false, j4);
        }
        this.mPhoneSignalScanningTimer.reset(false, j4);
        for (int i5 = 0; i5 < BatteryStats.NUM_DATA_CONNECTION_TYPES; i5++) {
            this.mPhoneDataConnectionsTimer[i5].reset(false, j4);
        }
        for (int i6 = 0; i6 < 10; i6++) {
            this.mNetworkByteActivityCounters[i6].reset(false, j4);
            this.mNetworkPacketActivityCounters[i6].reset(false, j4);
        }
        for (int i7 = 0; i7 < 3; i7++) {
            RadioAccessTechnologyBatteryStats radioAccessTechnologyBatteryStats = this.mPerRatBatteryStats[i7];
            if (radioAccessTechnologyBatteryStats != null) {
                radioAccessTechnologyBatteryStats.reset(j4);
            }
        }
        this.mMobileRadioActiveTimer.reset(false, j4);
        this.mMobileRadioActivePerAppTimer.reset(false, j4);
        this.mMobileRadioActiveAdjustedTime.reset(false, j4);
        this.mMobileRadioActiveUnknownTime.reset(false, j4);
        this.mMobileRadioActiveUnknownCount.reset(false, j4);
        this.mWifiOnTimer.reset(false, j4);
        this.mGlobalWifiRunningTimer.reset(false, j4);
        for (int i8 = 0; i8 < 8; i8++) {
            this.mWifiStateTimer[i8].reset(false, j4);
        }
        for (int i9 = 0; i9 < 13; i9++) {
            this.mWifiSupplStateTimer[i9].reset(false, j4);
        }
        for (int i10 = 0; i10 < 5; i10++) {
            this.mWifiSignalStrengthsTimer[i10].reset(false, j4);
        }
        this.mWifiMulticastWakelockTimer.reset(false, j4);
        this.mWifiActiveTimer.reset(false, j4);
        this.mWifiActivity.reset(false, j4);
        int i11 = 0;
        while (true) {
            StopwatchTimer[] stopwatchTimerArr = this.mGpsSignalQualityTimer;
            if (i11 >= stopwatchTimerArr.length) {
                break;
            }
            stopwatchTimerArr[i11].reset(false, j4);
            i11++;
        }
        this.mBluetoothActivity.reset(false, j4);
        this.mModemActivity.reset(false, j4);
        this.mNumConnectivityChange = 0;
        int i12 = 0;
        while (i12 < this.mUidStats.size()) {
            if (this.mUidStats.valueAt(i12).reset(j3, j4, i)) {
                this.mUidStats.valueAt(i12).detachFromTimeBase();
                SparseArray<Uid> sparseArray = this.mUidStats;
                sparseArray.remove(sparseArray.keyAt(i12));
                i12--;
            }
            i12++;
        }
        if (this.mRpmStats.size() > 0) {
            for (SamplingTimer samplingTimer : this.mRpmStats.values()) {
                this.mOnBatteryTimeBase.remove(samplingTimer);
            }
            this.mRpmStats.clear();
        }
        if (this.mScreenOffRpmStats.size() > 0) {
            for (SamplingTimer samplingTimer2 : this.mScreenOffRpmStats.values()) {
                this.mOnBatteryScreenOffTimeBase.remove(samplingTimer2);
            }
            this.mScreenOffRpmStats.clear();
        }
        if (this.mKernelWakelockStats.size() > 0) {
            for (SamplingTimer samplingTimer3 : this.mKernelWakelockStats.values()) {
                this.mOnBatteryScreenOffTimeBase.remove(samplingTimer3);
            }
            this.mKernelWakelockStats.clear();
        }
        if (this.mKernelMemoryStats.size() > 0) {
            for (int i13 = 0; i13 < this.mKernelMemoryStats.size(); i13++) {
                this.mOnBatteryTimeBase.remove(this.mKernelMemoryStats.valueAt(i13));
            }
            this.mKernelMemoryStats.clear();
        }
        if (this.mWakeupReasonStats.size() > 0) {
            for (SamplingTimer samplingTimer4 : this.mWakeupReasonStats.values()) {
                this.mOnBatteryTimeBase.remove(samplingTimer4);
            }
            this.mWakeupReasonStats.clear();
        }
        this.mTmpRailStats.reset();
        EnergyConsumerStats.resetIfNotNull(this.mGlobalEnergyConsumerStats);
        resetIfNotNull(this.mBinderThreadCpuTimesUs, false, j4);
        this.mNumAllUidCpuTimeReads = 0;
        this.mNumUidsRemoved = 0;
        initDischarge(j4);
        this.mHistory.reset();
        writeSyncLocked();
        this.mIgnoreNextExternalStats = true;
        this.mExternalSync.scheduleSync("reset", 255);
        this.mHandler.sendEmptyMessage(4);
    }

    @GuardedBy({"this"})
    public final void initActiveHistoryEventsLocked(long j, long j2) {
        HashMap stateForEvent;
        for (int i = 0; i < 22; i++) {
            if ((this.mRecordAllHistory || i != 1) && (stateForEvent = this.mActiveEvents.getStateForEvent(i)) != null) {
                for (Map.Entry entry : stateForEvent.entrySet()) {
                    SparseIntArray sparseIntArray = (SparseIntArray) entry.getValue();
                    for (int i2 = 0; i2 < sparseIntArray.size(); i2++) {
                        this.mHistory.recordEvent(j, j2, i, (String) entry.getKey(), sparseIntArray.keyAt(i2));
                    }
                }
            }
        }
    }

    @GuardedBy({"this"})
    public void updateDischargeScreenLevelsLocked(int i, int i2) {
        updateOldDischargeScreenLevelLocked(i);
        updateNewDischargeScreenLevelLocked(i2);
    }

    @GuardedBy({"this"})
    public final void updateOldDischargeScreenLevelLocked(int i) {
        int i2;
        if (Display.isOnState(i)) {
            int i3 = this.mDischargeScreenOnUnplugLevel - this.mDischargeCurrentLevel;
            if (i3 > 0) {
                this.mDischargeAmountScreenOn += i3;
                this.mDischargeAmountScreenOnSinceCharge += i3;
            }
        } else if (Display.isDozeState(i)) {
            int i4 = this.mDischargeScreenDozeUnplugLevel - this.mDischargeCurrentLevel;
            if (i4 > 0) {
                this.mDischargeAmountScreenDoze += i4;
                this.mDischargeAmountScreenDozeSinceCharge += i4;
            }
        } else if (!Display.isOffState(i) || (i2 = this.mDischargeScreenOffUnplugLevel - this.mDischargeCurrentLevel) <= 0) {
        } else {
            this.mDischargeAmountScreenOff += i2;
            this.mDischargeAmountScreenOffSinceCharge += i2;
        }
    }

    @GuardedBy({"this"})
    public final void updateNewDischargeScreenLevelLocked(int i) {
        if (Display.isOnState(i)) {
            this.mDischargeScreenOnUnplugLevel = this.mDischargeCurrentLevel;
            this.mDischargeScreenOffUnplugLevel = 0;
            this.mDischargeScreenDozeUnplugLevel = 0;
        } else if (Display.isDozeState(i)) {
            this.mDischargeScreenOnUnplugLevel = 0;
            this.mDischargeScreenDozeUnplugLevel = this.mDischargeCurrentLevel;
            this.mDischargeScreenOffUnplugLevel = 0;
        } else if (Display.isOffState(i)) {
            this.mDischargeScreenOnUnplugLevel = 0;
            this.mDischargeScreenDozeUnplugLevel = 0;
            this.mDischargeScreenOffUnplugLevel = this.mDischargeCurrentLevel;
        }
    }

    @GuardedBy({"this"})
    public void pullPendingStateUpdatesLocked() {
        if (this.mOnBatteryInternal) {
            int i = this.mScreenState;
            updateDischargeScreenLevelsLocked(i, i);
        }
    }

    @VisibleForTesting
    public NetworkStats readMobileNetworkStatsLocked(NetworkStatsManager networkStatsManager) {
        return networkStatsManager.getMobileUidStats();
    }

    @VisibleForTesting
    public NetworkStats readWifiNetworkStatsLocked(NetworkStatsManager networkStatsManager) {
        return networkStatsManager.getWifiUidStats();
    }

    @GuardedBy({"this"})
    public void updateWifiState(WifiActivityEnergyInfo wifiActivityEnergyInfo, long j, long j2, long j3, NetworkStatsManager networkStatsManager) {
        NetworkStats networkStats;
        SparseLongArray sparseLongArray;
        SparseLongArray sparseLongArray2;
        long j4;
        long j5;
        long j6;
        double d;
        double d2;
        long j7;
        SparseLongArray sparseLongArray3;
        int i;
        SparseLongArray sparseLongArray4;
        long j8;
        long j9;
        long j10;
        int i2;
        BatteryStatsImpl batteryStatsImpl = this;
        synchronized (batteryStatsImpl.mWifiNetworkLock) {
            NetworkStats readWifiNetworkStatsLocked = batteryStatsImpl.readWifiNetworkStatsLocked(networkStatsManager);
            if (readWifiNetworkStatsLocked != null) {
                networkStats = readWifiNetworkStatsLocked.subtract(batteryStatsImpl.mLastWifiNetworkStats);
                batteryStatsImpl.mLastWifiNetworkStats = readWifiNetworkStatsLocked;
            } else {
                networkStats = null;
            }
        }
        synchronized (this) {
            try {
                if (batteryStatsImpl.mOnBatteryInternal && !batteryStatsImpl.mIgnoreNextExternalStats) {
                    long j11 = 0;
                    SparseDoubleArray sparseDoubleArray = (batteryStatsImpl.mGlobalEnergyConsumerStats == null || batteryStatsImpl.mWifiPowerCalculator == null || j <= 0) ? null : new SparseDoubleArray();
                    SparseLongArray sparseLongArray5 = new SparseLongArray();
                    SparseLongArray sparseLongArray6 = new SparseLongArray();
                    SparseLongArray sparseLongArray7 = new SparseLongArray();
                    SparseLongArray sparseLongArray8 = new SparseLongArray();
                    if (networkStats != null) {
                        Iterator it = networkStats.iterator();
                        j4 = 0;
                        j5 = 0;
                        while (it.hasNext()) {
                            NetworkStats.Entry entry = (NetworkStats.Entry) it.next();
                            if (entry.getRxBytes() != j11 || entry.getTxBytes() != j11) {
                                int mapUid = batteryStatsImpl.mapUid(entry.getUid());
                                SparseLongArray sparseLongArray9 = sparseLongArray7;
                                SparseLongArray sparseLongArray10 = sparseLongArray8;
                                Uid uidStatsLocked = getUidStatsLocked(mapUid, j2, j3);
                                if (entry.getRxBytes() != j11) {
                                    uidStatsLocked.noteNetworkActivityLocked(2, entry.getRxBytes(), entry.getRxPackets());
                                    if (entry.getSet() == 0) {
                                        uidStatsLocked.noteNetworkActivityLocked(8, entry.getRxBytes(), entry.getRxPackets());
                                    }
                                    batteryStatsImpl.mNetworkByteActivityCounters[2].addCountLocked(entry.getRxBytes());
                                    batteryStatsImpl.mNetworkPacketActivityCounters[2].addCountLocked(entry.getRxPackets());
                                    i2 = mapUid;
                                    sparseLongArray5.incrementValue(i2, entry.getRxPackets());
                                    j5 += entry.getRxPackets();
                                } else {
                                    i2 = mapUid;
                                }
                                if (entry.getTxBytes() != j11) {
                                    uidStatsLocked.noteNetworkActivityLocked(3, entry.getTxBytes(), entry.getTxPackets());
                                    if (entry.getSet() == 0) {
                                        uidStatsLocked.noteNetworkActivityLocked(9, entry.getTxBytes(), entry.getTxPackets());
                                    }
                                    batteryStatsImpl.mNetworkByteActivityCounters[3].addCountLocked(entry.getTxBytes());
                                    batteryStatsImpl.mNetworkPacketActivityCounters[3].addCountLocked(entry.getTxPackets());
                                    sparseLongArray6.incrementValue(i2, entry.getTxPackets());
                                    j4 += entry.getTxPackets();
                                }
                                if (sparseDoubleArray != null && wifiActivityEnergyInfo == null && !batteryStatsImpl.mHasWifiReporting) {
                                    long j12 = j2 * 1000;
                                    long timeSinceMarkLocked = uidStatsLocked.mWifiRunningTimer.getTimeSinceMarkLocked(j12) / 1000;
                                    if (timeSinceMarkLocked > j11) {
                                        uidStatsLocked.mWifiRunningTimer.setMark(j2);
                                    }
                                    long timeSinceMarkLocked2 = uidStatsLocked.mWifiScanTimer.getTimeSinceMarkLocked(j12) / 1000;
                                    if (timeSinceMarkLocked2 > j11) {
                                        uidStatsLocked.mWifiScanTimer.setMark(j2);
                                    }
                                    long j13 = j11;
                                    int i3 = 0;
                                    while (i3 < 5) {
                                        StopwatchTimer stopwatchTimer = uidStatsLocked.mWifiBatchedScanTimer[i3];
                                        if (stopwatchTimer != null) {
                                            long timeSinceMarkLocked3 = stopwatchTimer.getTimeSinceMarkLocked(j12) / 1000;
                                            if (timeSinceMarkLocked3 > j11) {
                                                uidStatsLocked.mWifiBatchedScanTimer[i3].setMark(j2);
                                            }
                                            j13 += timeSinceMarkLocked3;
                                        }
                                        i3++;
                                        j11 = 0;
                                    }
                                    sparseDoubleArray.incrementValue(uidStatsLocked.getUid(), batteryStatsImpl.mWifiPowerCalculator.calcPowerWithoutControllerDataMah(entry.getRxPackets(), entry.getTxPackets(), timeSinceMarkLocked, timeSinceMarkLocked2, j13));
                                }
                                sparseLongArray7 = sparseLongArray9;
                                sparseLongArray8 = sparseLongArray10;
                                j11 = 0;
                            }
                        }
                        sparseLongArray = sparseLongArray7;
                        sparseLongArray2 = sparseLongArray8;
                    } else {
                        sparseLongArray = sparseLongArray7;
                        sparseLongArray2 = sparseLongArray8;
                        j4 = 0;
                        j5 = 0;
                    }
                    double d3 = 0.0d;
                    if (wifiActivityEnergyInfo != null) {
                        batteryStatsImpl.mHasWifiReporting = true;
                        long controllerTxDurationMillis = wifiActivityEnergyInfo.getControllerTxDurationMillis();
                        long controllerRxDurationMillis = wifiActivityEnergyInfo.getControllerRxDurationMillis();
                        wifiActivityEnergyInfo.getControllerScanDurationMillis();
                        long controllerIdleDurationMillis = wifiActivityEnergyInfo.getControllerIdleDurationMillis();
                        int size = batteryStatsImpl.mUidStats.size();
                        long j14 = 0;
                        long j15 = 0;
                        for (int i4 = 0; i4 < size; i4++) {
                            Uid valueAt = batteryStatsImpl.mUidStats.valueAt(i4);
                            long j16 = j2 * 1000;
                            j14 += valueAt.mWifiScanTimer.getTimeSinceMarkLocked(j16) / 1000;
                            j15 += valueAt.mFullWifiLockTimer.getTimeSinceMarkLocked(j16) / 1000;
                        }
                        int i5 = 0;
                        long j17 = controllerRxDurationMillis;
                        long j18 = controllerTxDurationMillis;
                        while (i5 < size) {
                            int i6 = size;
                            Uid valueAt2 = batteryStatsImpl.mUidStats.valueAt(i5);
                            SparseLongArray sparseLongArray11 = sparseLongArray5;
                            long j19 = j15;
                            long j20 = j2 * 1000;
                            long timeSinceMarkLocked4 = valueAt2.mWifiScanTimer.getTimeSinceMarkLocked(j20) / 1000;
                            if (timeSinceMarkLocked4 > 0) {
                                try {
                                    valueAt2.mWifiScanTimer.setMark(j2);
                                    long j21 = j14 > controllerRxDurationMillis ? (controllerRxDurationMillis * timeSinceMarkLocked4) / j14 : timeSinceMarkLocked4;
                                    if (j14 > controllerTxDurationMillis) {
                                        timeSinceMarkLocked4 = (timeSinceMarkLocked4 * controllerTxDurationMillis) / j14;
                                    }
                                    j7 = j14;
                                    sparseLongArray3 = sparseLongArray6;
                                    long j22 = timeSinceMarkLocked4;
                                    i = i5;
                                    SparseLongArray sparseLongArray12 = sparseLongArray;
                                    sparseLongArray12.incrementValue(valueAt2.getUid(), j21);
                                    sparseLongArray = sparseLongArray12;
                                    sparseLongArray4 = sparseLongArray2;
                                    sparseLongArray4.incrementValue(valueAt2.getUid(), j22);
                                    j17 -= j21;
                                    j18 -= j22;
                                    j8 = j22;
                                    j9 = j21;
                                } catch (Throwable th) {
                                    th = th;
                                    throw th;
                                }
                            } else {
                                i = i5;
                                j7 = j14;
                                sparseLongArray3 = sparseLongArray6;
                                sparseLongArray4 = sparseLongArray2;
                                j9 = timeSinceMarkLocked4;
                                j8 = j9;
                            }
                            long timeSinceMarkLocked5 = valueAt2.mFullWifiLockTimer.getTimeSinceMarkLocked(j20) / 1000;
                            if (timeSinceMarkLocked5 > 0) {
                                valueAt2.mFullWifiLockTimer.setMark(j2);
                                long j23 = (timeSinceMarkLocked5 * controllerIdleDurationMillis) / j19;
                                valueAt2.getOrCreateWifiControllerActivityLocked().getOrCreateIdleTimeCounter().increment(j23, j2);
                                j10 = j23;
                            } else {
                                j10 = 0;
                            }
                            if (sparseDoubleArray != null) {
                                batteryStatsImpl = this;
                                sparseDoubleArray.incrementValue(valueAt2.getUid(), batteryStatsImpl.mWifiPowerCalculator.calcPowerFromControllerDataMah(j9, j8, j10));
                            } else {
                                batteryStatsImpl = this;
                            }
                            sparseLongArray2 = sparseLongArray4;
                            sparseLongArray5 = sparseLongArray11;
                            sparseLongArray6 = sparseLongArray3;
                            j15 = j19;
                            j14 = j7;
                            i5 = i + 1;
                            size = i6;
                        }
                        SparseLongArray sparseLongArray13 = sparseLongArray5;
                        SparseLongArray sparseLongArray14 = sparseLongArray6;
                        SparseLongArray sparseLongArray15 = sparseLongArray2;
                        int i7 = 0;
                        while (i7 < sparseLongArray14.size()) {
                            SparseLongArray sparseLongArray16 = sparseLongArray14;
                            sparseLongArray15.incrementValue(sparseLongArray16.keyAt(i7), (sparseLongArray16.valueAt(i7) * j18) / j4);
                            i7++;
                            sparseLongArray14 = sparseLongArray16;
                        }
                        int i8 = 0;
                        while (i8 < sparseLongArray13.size()) {
                            SparseLongArray sparseLongArray17 = sparseLongArray13;
                            SparseLongArray sparseLongArray18 = sparseLongArray;
                            sparseLongArray18.incrementValue(sparseLongArray17.keyAt(i8), (sparseLongArray17.valueAt(i8) * j17) / j5);
                            i8++;
                            sparseLongArray13 = sparseLongArray17;
                            sparseLongArray = sparseLongArray18;
                        }
                        SparseLongArray sparseLongArray19 = sparseLongArray;
                        int i9 = 0;
                        while (i9 < sparseLongArray15.size()) {
                            int keyAt = sparseLongArray15.keyAt(i9);
                            long valueAt3 = sparseLongArray15.valueAt(i9);
                            SparseLongArray sparseLongArray20 = sparseLongArray15;
                            getUidStatsLocked(keyAt, j2, j3).getOrCreateWifiControllerActivityLocked().getOrCreateTxTimeCounters()[0].increment(valueAt3, j2);
                            if (sparseDoubleArray != null) {
                                sparseDoubleArray.incrementValue(keyAt, batteryStatsImpl.mWifiPowerCalculator.calcPowerFromControllerDataMah(0L, valueAt3, 0L));
                            }
                            i9++;
                            sparseLongArray15 = sparseLongArray20;
                        }
                        for (int i10 = 0; i10 < sparseLongArray19.size(); i10++) {
                            int keyAt2 = sparseLongArray19.keyAt(i10);
                            long valueAt4 = sparseLongArray19.valueAt(i10);
                            getUidStatsLocked(sparseLongArray19.keyAt(i10), j2, j3).getOrCreateWifiControllerActivityLocked().getOrCreateRxTimeCounter().increment(valueAt4, j2);
                            if (sparseDoubleArray != null) {
                                sparseDoubleArray.incrementValue(keyAt2, batteryStatsImpl.mWifiPowerCalculator.calcPowerFromControllerDataMah(valueAt4, 0L, 0L));
                            }
                        }
                        batteryStatsImpl.mWifiActivity.getOrCreateRxTimeCounter().increment(wifiActivityEnergyInfo.getControllerRxDurationMillis(), j2);
                        batteryStatsImpl.mWifiActivity.getOrCreateTxTimeCounters()[0].increment(wifiActivityEnergyInfo.getControllerTxDurationMillis(), j2);
                        batteryStatsImpl.mWifiActivity.getScanTimeCounter().addCountLocked(wifiActivityEnergyInfo.getControllerScanDurationMillis());
                        batteryStatsImpl.mWifiActivity.getOrCreateIdleTimeCounter().increment(wifiActivityEnergyInfo.getControllerIdleDurationMillis(), j2);
                        double averagePower = batteryStatsImpl.mPowerProfile.getAveragePower("wifi.controller.voltage") / 1000.0d;
                        if (averagePower != 0.0d) {
                            d2 = wifiActivityEnergyInfo.getControllerEnergyUsedMicroJoules() / averagePower;
                            batteryStatsImpl.mWifiActivity.getPowerCounter().addCountLocked((long) d2);
                        } else {
                            d2 = 0.0d;
                        }
                        long wifiTotalEnergyUseduWs = (long) (batteryStatsImpl.mTmpRailStats.getWifiTotalEnergyUseduWs() / averagePower);
                        batteryStatsImpl.mWifiActivity.getMonitoredRailChargeConsumedMaMs().addCountLocked(wifiTotalEnergyUseduWs);
                        j6 = j2;
                        batteryStatsImpl.mHistory.recordWifiConsumedCharge(j2, j3, wifiTotalEnergyUseduWs / 3600000.0d);
                        batteryStatsImpl.mTmpRailStats.resetWifiTotalEnergyUsed();
                        if (sparseDoubleArray != null) {
                            d3 = Math.max(d2 / 3600000.0d, batteryStatsImpl.mWifiPowerCalculator.calcPowerFromControllerDataMah(controllerRxDurationMillis, controllerTxDurationMillis, controllerIdleDurationMillis));
                        }
                    } else {
                        j6 = j2;
                    }
                    if (sparseDoubleArray != null) {
                        batteryStatsImpl.mGlobalEnergyConsumerStats.updateStandardBucket(4, j);
                        if (batteryStatsImpl.mHasWifiReporting) {
                            d = d3;
                        } else {
                            batteryStatsImpl.mGlobalWifiRunningTimer.setMark(j6);
                            d = batteryStatsImpl.mWifiPowerCalculator.calcGlobalPowerWithoutControllerDataMah(batteryStatsImpl.mGlobalWifiRunningTimer.getTimeSinceMarkLocked(j6 * 1000) / 1000);
                        }
                        distributeEnergyToUidsLocked(4, j, sparseDoubleArray, d, j2);
                    }
                }
            } catch (Throwable th2) {
                th = th2;
            }
        }
    }

    public void noteModemControllerActivity(ModemActivityInfo modemActivityInfo, long j, long j2, long j3, NetworkStatsManager networkStatsManager) {
        NetworkStats networkStats;
        long j4;
        SparseDoubleArray sparseDoubleArray;
        long j5;
        SparseDoubleArray sparseDoubleArray2;
        char c;
        char c2;
        RxTxConsumption rxTxConsumption;
        RxTxConsumption rxTxConsumption2;
        long j6;
        SparseDoubleArray sparseDoubleArray3;
        long j7;
        double calcPowerFromRadioActiveDurationMah;
        double calcScanTimePowerMah;
        RxTxConsumption rxTxConsumption3;
        long j8;
        Uid uid;
        long j9;
        SparseDoubleArray sparseDoubleArray4;
        double calcPowerFromRadioActiveDurationMah2;
        ModemActivityInfo modemActivityInfo2 = this.mLastModemActivityInfo;
        ModemActivityInfo delta = modemActivityInfo2 == null ? modemActivityInfo : modemActivityInfo2.getDelta(modemActivityInfo);
        this.mLastModemActivityInfo = modemActivityInfo;
        addModemTxPowerToHistory(delta, j2, j3);
        synchronized (this.mModemNetworkLock) {
            NetworkStats readMobileNetworkStatsLocked = readMobileNetworkStatsLocked(networkStatsManager);
            if (readMobileNetworkStatsLocked != null) {
                NetworkStats subtract = readMobileNetworkStatsLocked.subtract(this.mLastModemNetworkStats);
                this.mLastModemNetworkStats = readMobileNetworkStatsLocked;
                networkStats = subtract;
            } else {
                networkStats = null;
            }
        }
        synchronized (this) {
            long j10 = j2 * 1000;
            long timeSinceMarkLocked = this.mMobileRadioActiveTimer.getTimeSinceMarkLocked(j10) / 1000;
            this.mMobileRadioActiveTimer.setMark(j2);
            long min = Math.min(timeSinceMarkLocked, this.mPhoneOnTimer.getTimeSinceMarkLocked(j10) / 1000);
            this.mPhoneOnTimer.setMark(j2);
            if (this.mOnBatteryInternal && !this.mIgnoreNextExternalStats) {
                if (j <= 0 || !isMobileRadioEnergyConsumerSupportedLocked()) {
                    j4 = -1;
                    sparseDoubleArray = null;
                } else {
                    long j11 = timeSinceMarkLocked == 0 ? 0L : ((min * j) + (timeSinceMarkLocked / 2)) / timeSinceMarkLocked;
                    long j12 = j - j11;
                    this.mGlobalEnergyConsumerStats.updateStandardBucket(9, j11);
                    this.mGlobalEnergyConsumerStats.updateStandardBucket(7, j12);
                    sparseDoubleArray = new SparseDoubleArray();
                    j4 = j12;
                }
                if (delta != null) {
                    this.mHasModemReporting = true;
                    this.mModemActivity.getOrCreateIdleTimeCounter().increment(delta.getIdleTimeMillis(), j2);
                    this.mModemActivity.getSleepTimeCounter().addCountLocked(delta.getSleepTimeMillis());
                    this.mModemActivity.getOrCreateRxTimeCounter().increment(delta.getReceiveTimeMillis(), j2);
                    for (int i = 0; i < ModemActivityInfo.getNumTxPowerLevels(); i++) {
                        this.mModemActivity.getOrCreateTxTimeCounters()[i].increment(delta.getTransmitDurationMillisAtPowerLevel(i), j2);
                    }
                    double averagePower = this.mPowerProfile.getAveragePower("modem.controller.voltage") / 1000.0d;
                    if (averagePower != 0.0d) {
                        double sleepTimeMillis = (delta.getSleepTimeMillis() * this.mPowerProfile.getAveragePower("modem.controller.sleep")) + (delta.getIdleTimeMillis() * this.mPowerProfile.getAveragePower("modem.controller.idle")) + (delta.getReceiveTimeMillis() * this.mPowerProfile.getAveragePower("modem.controller.rx"));
                        int i2 = 0;
                        while (i2 < Math.min(ModemActivityInfo.getNumTxPowerLevels(), CellSignalStrength.getNumSignalStrengthLevels())) {
                            sleepTimeMillis += delta.getTransmitDurationMillisAtPowerLevel(i2) * this.mPowerProfile.getAveragePower("modem.controller.tx", i2);
                            i2++;
                            sparseDoubleArray = sparseDoubleArray;
                        }
                        sparseDoubleArray2 = sparseDoubleArray;
                        this.mModemActivity.getPowerCounter().addCountLocked((long) sleepTimeMillis);
                        long cellularTotalEnergyUseduWs = (long) (this.mTmpRailStats.getCellularTotalEnergyUseduWs() / averagePower);
                        this.mModemActivity.getMonitoredRailChargeConsumedMaMs().addCountLocked(cellularTotalEnergyUseduWs);
                        c = 1;
                        j5 = timeSinceMarkLocked;
                        this.mHistory.recordWifiConsumedCharge(j2, j3, cellularTotalEnergyUseduWs / 3600000.0d);
                        this.mTmpRailStats.resetCellularTotalEnergyUsed();
                    } else {
                        j5 = timeSinceMarkLocked;
                        sparseDoubleArray2 = sparseDoubleArray;
                        c = 1;
                    }
                    RxTxConsumption incrementPerRatDataLocked = incrementPerRatDataLocked(delta, j2);
                    rxTxConsumption = incrementPerRatDataLocked;
                    c2 = (this.mConstants.PER_UID_MODEM_MODEL != 2 || incrementPerRatDataLocked == null) ? (char) 0 : c;
                } else {
                    j5 = timeSinceMarkLocked;
                    sparseDoubleArray2 = sparseDoubleArray;
                    c = 1;
                    c2 = 0;
                    rxTxConsumption = null;
                }
                long timeSinceMarkLocked2 = this.mMobileRadioActivePerAppTimer.getTimeSinceMarkLocked(j10);
                this.mMobileRadioActivePerAppTimer.setMark(j2);
                if (networkStats != null) {
                    Iterator it = networkStats.iterator();
                    long j13 = 0;
                    long j14 = 0;
                    while (it.hasNext()) {
                        NetworkStats.Entry entry = (NetworkStats.Entry) it.next();
                        if (entry.getRxPackets() != 0 || entry.getTxPackets() != 0) {
                            j13 += entry.getRxPackets();
                            j14 += entry.getTxPackets();
                            Uid uidStatsLocked = getUidStatsLocked(mapUid(entry.getUid()), j2, j3);
                            uidStatsLocked.noteNetworkActivityLocked(0, entry.getRxBytes(), entry.getRxPackets());
                            uidStatsLocked.noteNetworkActivityLocked(1, entry.getTxBytes(), entry.getTxPackets());
                            if (entry.getSet() == 0) {
                                uidStatsLocked.noteNetworkActivityLocked(6, entry.getRxBytes(), entry.getRxPackets());
                                uidStatsLocked.noteNetworkActivityLocked(7, entry.getTxBytes(), entry.getTxPackets());
                            }
                            this.mNetworkByteActivityCounters[0].addCountLocked(entry.getRxBytes());
                            this.mNetworkByteActivityCounters[c].addCountLocked(entry.getTxBytes());
                            this.mNetworkPacketActivityCounters[0].addCountLocked(entry.getRxPackets());
                            this.mNetworkPacketActivityCounters[c].addCountLocked(entry.getTxPackets());
                        }
                    }
                    long j15 = j13 + j14;
                    if (j15 > 0) {
                        Iterator it2 = networkStats.iterator();
                        long j16 = timeSinceMarkLocked2;
                        long j17 = j15;
                        while (it2.hasNext()) {
                            NetworkStats.Entry entry2 = (NetworkStats.Entry) it2.next();
                            if (entry2.getRxPackets() != 0 || entry2.getTxPackets() != 0) {
                                Uid uidStatsLocked2 = getUidStatsLocked(mapUid(entry2.getUid()), j2, j3);
                                long rxPackets = entry2.getRxPackets() + entry2.getTxPackets();
                                long j18 = (j16 * rxPackets) / j17;
                                uidStatsLocked2.noteMobileRadioActiveTimeLocked(j18, j2);
                                if (sparseDoubleArray2 != null) {
                                    if (c2 != 0) {
                                        RxTxConsumption rxTxConsumption4 = rxTxConsumption;
                                        j8 = j18;
                                        rxTxConsumption3 = rxTxConsumption;
                                        uid = uidStatsLocked2;
                                        sparseDoubleArray4 = sparseDoubleArray2;
                                        j9 = j10;
                                        calcPowerFromRadioActiveDurationMah2 = smearModemActivityInfoRxTxConsumptionMah(rxTxConsumption4, entry2.getRxPackets(), entry2.getTxPackets(), j13, j14);
                                    } else {
                                        rxTxConsumption3 = rxTxConsumption;
                                        j8 = j18;
                                        uid = uidStatsLocked2;
                                        j9 = j10;
                                        sparseDoubleArray4 = sparseDoubleArray2;
                                        calcPowerFromRadioActiveDurationMah2 = this.mMobileRadioPowerCalculator.calcPowerFromRadioActiveDurationMah(j8 / 1000);
                                    }
                                    sparseDoubleArray4.incrementValue(uid.getUid(), calcPowerFromRadioActiveDurationMah2);
                                } else {
                                    rxTxConsumption3 = rxTxConsumption;
                                    j8 = j18;
                                    uid = uidStatsLocked2;
                                    j9 = j10;
                                    sparseDoubleArray4 = sparseDoubleArray2;
                                }
                                j16 -= j8;
                                j17 -= rxPackets;
                                if (delta != null) {
                                    ControllerActivityCounterImpl orCreateModemControllerActivityLocked = uid.getOrCreateModemControllerActivityLocked();
                                    if (j13 > 0 && entry2.getRxPackets() > 0) {
                                        orCreateModemControllerActivityLocked.getOrCreateRxTimeCounter().increment((entry2.getRxPackets() * delta.getReceiveTimeMillis()) / j13, j2);
                                    }
                                    if (j14 > 0 && entry2.getTxPackets() > 0) {
                                        for (int i3 = 0; i3 < ModemActivityInfo.getNumTxPowerLevels(); i3++) {
                                            orCreateModemControllerActivityLocked.getOrCreateTxTimeCounters()[i3].increment((entry2.getTxPackets() * delta.getTransmitDurationMillisAtPowerLevel(i3)) / j14, j2);
                                        }
                                    }
                                }
                                sparseDoubleArray2 = sparseDoubleArray4;
                                j10 = j9;
                                rxTxConsumption = rxTxConsumption3;
                            }
                        }
                        rxTxConsumption2 = rxTxConsumption;
                        j6 = j10;
                        sparseDoubleArray3 = sparseDoubleArray2;
                        j7 = j16;
                    } else {
                        rxTxConsumption2 = rxTxConsumption;
                        j6 = j10;
                        sparseDoubleArray3 = sparseDoubleArray2;
                        j7 = timeSinceMarkLocked2;
                    }
                    if (j7 > 0) {
                        this.mMobileRadioActiveUnknownTime.addCountLocked(j7);
                        this.mMobileRadioActiveUnknownCount.addCountLocked(1L);
                    }
                    if (sparseDoubleArray3 != null) {
                        if (c2 != 0) {
                            RxTxConsumption rxTxConsumption5 = rxTxConsumption2;
                            calcPowerFromRadioActiveDurationMah = this.mMobileRadioPowerCalculator.calcInactiveStatePowerMah(delta.getSleepTimeMillis(), delta.getIdleTimeMillis()) + 0.0d + rxTxConsumption5.rxConsumptionMah;
                            calcScanTimePowerMah = rxTxConsumption5.txConsumptionMah;
                        } else {
                            calcPowerFromRadioActiveDurationMah = this.mMobileRadioPowerCalculator.calcPowerFromRadioActiveDurationMah(j5) + 0.0d;
                            int length = this.mPhoneSignalStrengthsTimer.length;
                            int i4 = 0;
                            while (i4 < length) {
                                long j19 = j6;
                                this.mPhoneSignalStrengthsTimer[i4].setMark(j2);
                                calcPowerFromRadioActiveDurationMah += this.mMobileRadioPowerCalculator.calcIdlePowerAtSignalStrengthMah(this.mPhoneSignalStrengthsTimer[i4].getTimeSinceMarkLocked(j19) / 1000, i4);
                                i4++;
                                j6 = j19;
                            }
                            StopwatchTimer stopwatchTimer = this.mPhoneSignalScanningTimer;
                            this.mPhoneSignalScanningTimer.setMark(j2);
                            calcScanTimePowerMah = this.mMobileRadioPowerCalculator.calcScanTimePowerMah(stopwatchTimer.getTimeSinceMarkLocked(j6) / 1000);
                        }
                        distributeEnergyToUidsLocked(7, j4, sparseDoubleArray3, calcPowerFromRadioActiveDurationMah + calcScanTimePowerMah, j2);
                    }
                }
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class RxTxConsumption {
        public final double rxConsumptionMah;
        public final long rxDurationMs;
        public final double txConsumptionMah;
        public final long txDurationMs;
        public final double txToTotalRatio;

        public RxTxConsumption(double d, long j, double d2, long j2) {
            this.rxConsumptionMah = d;
            this.rxDurationMs = j;
            this.txConsumptionMah = d2;
            this.txDurationMs = j2;
            long j3 = j + j2;
            if (j3 == 0) {
                this.txToTotalRatio = 0.0d;
            } else {
                this.txToTotalRatio = j2 / j3;
            }
        }
    }

    @GuardedBy({"this"})
    public final RxTxConsumption incrementPerRatDataLocked(ModemActivityInfo modemActivityInfo, long j) {
        long j2;
        double d;
        double d2;
        int i;
        long[] jArr;
        long j3;
        long[] jArr2;
        int i2;
        ModemActivityInfo modemActivityInfo2 = modemActivityInfo;
        int specificInfoLength = modemActivityInfo.getSpecificInfoLength();
        long j4 = 0;
        if (specificInfoLength == 1 && modemActivityInfo2.getSpecificInfoRat(0) == 0 && modemActivityInfo2.getSpecificInfoFrequencyRange(0) == 0) {
            int numSignalStrengthLevels = CellSignalStrength.getNumSignalStrengthLevels();
            long[] jArr3 = new long[numSignalStrengthLevels];
            long j5 = 0;
            for (int i3 = 0; i3 < 3; i3++) {
                RadioAccessTechnologyBatteryStats radioAccessTechnologyBatteryStats = this.mPerRatBatteryStats[i3];
                if (radioAccessTechnologyBatteryStats != null) {
                    int frequencyRangeCount = radioAccessTechnologyBatteryStats.getFrequencyRangeCount();
                    for (int i4 = 0; i4 < frequencyRangeCount; i4++) {
                        for (int i5 = 0; i5 < numSignalStrengthLevels; i5++) {
                            long timeSinceMark = radioAccessTechnologyBatteryStats.getTimeSinceMark(i4, i5, j);
                            jArr3[i5] = jArr3[i5] + timeSinceMark;
                            j5 += timeSinceMark;
                        }
                    }
                }
            }
            if (j5 != 0) {
                long j6 = 0;
                long j7 = 0;
                int i6 = 0;
                d = 0.0d;
                d2 = 0.0d;
                for (int i7 = 3; i6 < i7; i7 = 3) {
                    RadioAccessTechnologyBatteryStats radioAccessTechnologyBatteryStats2 = this.mPerRatBatteryStats[i6];
                    if (radioAccessTechnologyBatteryStats2 == null) {
                        i = numSignalStrengthLevels;
                        jArr = jArr3;
                        j3 = j4;
                    } else {
                        int frequencyRangeCount2 = radioAccessTechnologyBatteryStats2.getFrequencyRangeCount();
                        long j8 = j6;
                        int i8 = 0;
                        while (i8 < frequencyRangeCount2) {
                            long j9 = j4;
                            long j10 = j7;
                            int i9 = 0;
                            while (i9 < numSignalStrengthLevels) {
                                long timeSinceMark2 = radioAccessTechnologyBatteryStats2.getTimeSinceMark(i8, i9, j);
                                long j11 = jArr3[i9];
                                if (j11 == 0) {
                                    i2 = numSignalStrengthLevels;
                                    jArr2 = jArr3;
                                } else {
                                    int i10 = numSignalStrengthLevels;
                                    jArr2 = jArr3;
                                    long transmitDurationMillisAtPowerLevel = ((modemActivityInfo2.getTransmitDurationMillisAtPowerLevel(i9) * timeSinceMark2) + (j11 / 2)) / j11;
                                    radioAccessTechnologyBatteryStats2.incrementTxDuration(i8, i9, transmitDurationMillisAtPowerLevel);
                                    long j12 = j9 + timeSinceMark2;
                                    i2 = i10;
                                    if (isMobileRadioEnergyConsumerSupportedLocked()) {
                                        d2 += this.mMobileRadioPowerCalculator.calcTxStatePowerMah(i6, i8, i9, transmitDurationMillisAtPowerLevel);
                                        j10 += transmitDurationMillisAtPowerLevel;
                                    }
                                    j9 = j12;
                                }
                                i9++;
                                numSignalStrengthLevels = i2;
                                jArr3 = jArr2;
                            }
                            int i11 = numSignalStrengthLevels;
                            long[] jArr4 = jArr3;
                            long receiveTimeMillis = ((j9 * modemActivityInfo.getReceiveTimeMillis()) + (j5 / 2)) / j5;
                            radioAccessTechnologyBatteryStats2.incrementRxDuration(i8, receiveTimeMillis);
                            if (isMobileRadioEnergyConsumerSupportedLocked()) {
                                d += this.mMobileRadioPowerCalculator.calcRxStatePowerMah(i6, i8, receiveTimeMillis);
                                j8 += receiveTimeMillis;
                            }
                            i8++;
                            j7 = j10;
                            j4 = 0;
                            numSignalStrengthLevels = i11;
                            jArr3 = jArr4;
                        }
                        i = numSignalStrengthLevels;
                        jArr = jArr3;
                        j3 = j4;
                        j6 = j8;
                    }
                    i6++;
                    j4 = j3;
                    numSignalStrengthLevels = i;
                    jArr3 = jArr;
                }
                j4 = j6;
                j2 = j7;
            } else {
                j2 = 0;
                d = 0.0d;
                d2 = 0.0d;
            }
        } else {
            j2 = 0;
            j4 = 0;
            int i12 = 0;
            d = 0.0d;
            d2 = 0.0d;
            while (i12 < specificInfoLength) {
                int specificInfoRat = modemActivityInfo2.getSpecificInfoRat(i12);
                int specificInfoFrequencyRange = modemActivityInfo2.getSpecificInfoFrequencyRange(i12);
                int mapRadioAccessNetworkTypeToRadioAccessTechnology = mapRadioAccessNetworkTypeToRadioAccessTechnology(specificInfoRat);
                RadioAccessTechnologyBatteryStats ratBatteryStatsLocked = getRatBatteryStatsLocked(mapRadioAccessNetworkTypeToRadioAccessTechnology);
                long receiveTimeMillis2 = modemActivityInfo2.getReceiveTimeMillis(specificInfoRat, specificInfoFrequencyRange);
                int[] transmitTimeMillis = modemActivityInfo2.getTransmitTimeMillis(specificInfoRat, specificInfoFrequencyRange);
                ratBatteryStatsLocked.incrementRxDuration(specificInfoFrequencyRange, receiveTimeMillis2);
                if (isMobileRadioEnergyConsumerSupportedLocked()) {
                    d += this.mMobileRadioPowerCalculator.calcRxStatePowerMah(mapRadioAccessNetworkTypeToRadioAccessTechnology, specificInfoFrequencyRange, receiveTimeMillis2);
                    j4 += receiveTimeMillis2;
                }
                int length = transmitTimeMillis.length;
                int i13 = 0;
                while (i13 < length) {
                    double d3 = d;
                    long j13 = transmitTimeMillis[i13];
                    ratBatteryStatsLocked.incrementTxDuration(specificInfoFrequencyRange, i13, j13);
                    if (isMobileRadioEnergyConsumerSupportedLocked()) {
                        d2 += this.mMobileRadioPowerCalculator.calcTxStatePowerMah(mapRadioAccessNetworkTypeToRadioAccessTechnology, specificInfoFrequencyRange, i13, j13);
                        j2 += j13;
                    }
                    i13++;
                    d = d3;
                }
                i12++;
                modemActivityInfo2 = modemActivityInfo;
            }
        }
        double d4 = d;
        long j14 = j4;
        double d5 = d2;
        long j15 = j2;
        for (int i14 = 0; i14 < 3; i14++) {
            RadioAccessTechnologyBatteryStats radioAccessTechnologyBatteryStats3 = this.mPerRatBatteryStats[i14];
            if (radioAccessTechnologyBatteryStats3 != null) {
                radioAccessTechnologyBatteryStats3.setMark(j);
            }
        }
        if (isMobileRadioEnergyConsumerSupportedLocked()) {
            return new RxTxConsumption(d4, j14, d5, j15);
        }
        return null;
    }

    public final double smearModemActivityInfoRxTxConsumptionMah(RxTxConsumption rxTxConsumption, long j, long j2, long j3, long j4) {
        int i = (j3 > 0L ? 1 : (j3 == 0L ? 0 : -1));
        double d = i != 0 ? ((rxTxConsumption.rxConsumptionMah * j) / j3) + 0.0d : 0.0d;
        if (j4 == 0 && (i == 0 || rxTxConsumption.txToTotalRatio == 0.0d)) {
            return d;
        }
        double d2 = rxTxConsumption.txToTotalRatio;
        return d + ((rxTxConsumption.txConsumptionMah * (j2 + (d2 * j))) / (j4 + (j3 * d2)));
    }

    public final synchronized void addModemTxPowerToHistory(ModemActivityInfo modemActivityInfo, long j, long j2) {
        if (modemActivityInfo == null) {
            return;
        }
        int i = 0;
        for (int i2 = 1; i2 < ModemActivityInfo.getNumTxPowerLevels(); i2++) {
            if (modemActivityInfo.getTransmitDurationMillisAtPowerLevel(i2) > modemActivityInfo.getTransmitDurationMillisAtPowerLevel(i)) {
                i = i2;
            }
        }
        if (i == ModemActivityInfo.getNumTxPowerLevels() - 1) {
            this.mHistory.recordState2StartEvent(j, j2, 524288);
        }
    }

    /* loaded from: classes2.dex */
    public final class BluetoothActivityInfoCache {
        public long energy;
        public long idleTimeMs;
        public long rxTimeMs;
        public long txTimeMs;
        public SparseLongArray uidRxBytes;
        public SparseLongArray uidTxBytes;

        public BluetoothActivityInfoCache() {
            this.uidRxBytes = new SparseLongArray();
            this.uidTxBytes = new SparseLongArray();
        }

        public void set(BluetoothActivityEnergyInfo bluetoothActivityEnergyInfo) {
            this.idleTimeMs = bluetoothActivityEnergyInfo.getControllerIdleTimeMillis();
            this.rxTimeMs = bluetoothActivityEnergyInfo.getControllerRxTimeMillis();
            this.txTimeMs = bluetoothActivityEnergyInfo.getControllerTxTimeMillis();
            this.energy = bluetoothActivityEnergyInfo.getControllerEnergyUsed();
            if (bluetoothActivityEnergyInfo.getUidTraffic().isEmpty()) {
                return;
            }
            for (UidTraffic uidTraffic : bluetoothActivityEnergyInfo.getUidTraffic()) {
                this.uidRxBytes.put(uidTraffic.getUid(), uidTraffic.getRxBytes());
                this.uidTxBytes.put(uidTraffic.getUid(), uidTraffic.getTxBytes());
            }
        }

        public void reset() {
            this.idleTimeMs = 0L;
            this.rxTimeMs = 0L;
            this.txTimeMs = 0L;
            this.energy = 0L;
            this.uidRxBytes.clear();
            this.uidTxBytes.clear();
        }
    }

    @GuardedBy({"this"})
    public void updateBluetoothStateLocked(BluetoothActivityEnergyInfo bluetoothActivityEnergyInfo, long j, long j2, long j3) {
        SparseDoubleArray sparseDoubleArray;
        SparseDoubleArray sparseDoubleArray2;
        double d;
        boolean z;
        SparseLongArray sparseLongArray;
        int i;
        if (bluetoothActivityEnergyInfo == null) {
            return;
        }
        if (!this.mOnBatteryInternal || this.mIgnoreNextExternalStats) {
            this.mLastBluetoothActivityInfo.set(bluetoothActivityEnergyInfo);
            return;
        }
        this.mHasBluetoothReporting = true;
        if (bluetoothActivityEnergyInfo.getControllerRxTimeMillis() < this.mLastBluetoothActivityInfo.rxTimeMs || bluetoothActivityEnergyInfo.getControllerTxTimeMillis() < this.mLastBluetoothActivityInfo.txTimeMs || bluetoothActivityEnergyInfo.getControllerIdleTimeMillis() < this.mLastBluetoothActivityInfo.idleTimeMs || bluetoothActivityEnergyInfo.getControllerEnergyUsed() < this.mLastBluetoothActivityInfo.energy) {
            this.mLastBluetoothActivityInfo.reset();
        }
        long controllerRxTimeMillis = bluetoothActivityEnergyInfo.getControllerRxTimeMillis() - this.mLastBluetoothActivityInfo.rxTimeMs;
        long controllerTxTimeMillis = bluetoothActivityEnergyInfo.getControllerTxTimeMillis() - this.mLastBluetoothActivityInfo.txTimeMs;
        long controllerIdleTimeMillis = bluetoothActivityEnergyInfo.getControllerIdleTimeMillis() - this.mLastBluetoothActivityInfo.idleTimeMs;
        SparseDoubleArray sparseDoubleArray3 = (this.mGlobalEnergyConsumerStats == null || this.mBluetoothPowerCalculator == null || j <= 0) ? null : new SparseDoubleArray();
        int size = this.mUidStats.size();
        long j4 = 0;
        int i2 = 0;
        while (i2 < size) {
            DualTimer dualTimer = this.mUidStats.valueAt(i2).mBluetoothScanTimer;
            if (dualTimer == null) {
                i = size;
            } else {
                i = size;
                j4 += dualTimer.getTimeSinceMarkLocked(j2 * 1000) / 1000;
            }
            i2++;
            size = i;
        }
        int i3 = size;
        boolean z2 = j4 > controllerRxTimeMillis;
        boolean z3 = j4 > controllerTxTimeMillis;
        int i4 = i3;
        SparseLongArray sparseLongArray2 = new SparseLongArray(i4);
        SparseLongArray sparseLongArray3 = new SparseLongArray(i4);
        long j5 = controllerRxTimeMillis;
        long j6 = controllerTxTimeMillis;
        int i5 = 0;
        while (i5 < i4) {
            int i6 = i4;
            Uid valueAt = this.mUidStats.valueAt(i5);
            long j7 = controllerIdleTimeMillis;
            DualTimer dualTimer2 = valueAt.mBluetoothScanTimer;
            if (dualTimer2 != null) {
                long timeSinceMarkLocked = dualTimer2.getTimeSinceMarkLocked(j2 * 1000) / 1000;
                if (timeSinceMarkLocked > 0) {
                    valueAt.mBluetoothScanTimer.setMark(j2);
                    long j8 = z2 ? (controllerRxTimeMillis * timeSinceMarkLocked) / j4 : timeSinceMarkLocked;
                    if (z3) {
                        timeSinceMarkLocked = (timeSinceMarkLocked * controllerTxTimeMillis) / j4;
                    }
                    z = z2;
                    sparseLongArray2.incrementValue(valueAt.getUid(), j8);
                    sparseLongArray3.incrementValue(valueAt.getUid(), timeSinceMarkLocked);
                    if (sparseDoubleArray3 != null) {
                        sparseLongArray = sparseLongArray2;
                        sparseDoubleArray3.incrementValue(valueAt.getUid(), this.mBluetoothPowerCalculator.calculatePowerMah(j8, timeSinceMarkLocked, 0L));
                    } else {
                        sparseLongArray = sparseLongArray2;
                    }
                    j5 -= j8;
                    j6 -= timeSinceMarkLocked;
                    i5++;
                    i4 = i6;
                    controllerIdleTimeMillis = j7;
                    z2 = z;
                    sparseLongArray2 = sparseLongArray;
                }
            }
            z = z2;
            sparseLongArray = sparseLongArray2;
            i5++;
            i4 = i6;
            controllerIdleTimeMillis = j7;
            z2 = z;
            sparseLongArray2 = sparseLongArray;
        }
        SparseLongArray sparseLongArray4 = sparseLongArray2;
        long j9 = controllerIdleTimeMillis;
        List uidTraffic = bluetoothActivityEnergyInfo.getUidTraffic();
        int size2 = uidTraffic.size();
        long j10 = 0;
        long j11 = 0;
        int i7 = 0;
        while (i7 < size2) {
            UidTraffic uidTraffic2 = (UidTraffic) uidTraffic.get(i7);
            long rxBytes = uidTraffic2.getRxBytes() - this.mLastBluetoothActivityInfo.uidRxBytes.get(uidTraffic2.getUid());
            long txBytes = uidTraffic2.getTxBytes() - this.mLastBluetoothActivityInfo.uidTxBytes.get(uidTraffic2.getUid());
            this.mNetworkByteActivityCounters[4].addCountLocked(rxBytes);
            this.mNetworkByteActivityCounters[5].addCountLocked(txBytes);
            Uid uidStatsLocked = getUidStatsLocked(mapUid(uidTraffic2.getUid()), j2, j3);
            uidStatsLocked.noteNetworkActivityLocked(4, rxBytes, 0L);
            uidStatsLocked.noteNetworkActivityLocked(5, txBytes, 0L);
            j11 += rxBytes;
            j10 += txBytes;
            i7++;
            sparseLongArray3 = sparseLongArray3;
            controllerTxTimeMillis = controllerTxTimeMillis;
        }
        SparseLongArray sparseLongArray5 = sparseLongArray3;
        long j12 = controllerTxTimeMillis;
        SparseLongArray sparseLongArray6 = sparseLongArray4;
        char c = 5;
        int i8 = (j10 > 0L ? 1 : (j10 == 0L ? 0 : -1));
        if ((i8 == 0 && j11 == 0) || (j5 == 0 && j6 == 0)) {
            sparseDoubleArray = sparseDoubleArray3;
        } else {
            int i9 = 0;
            while (i9 < size2) {
                UidTraffic uidTraffic3 = (UidTraffic) uidTraffic.get(i9);
                int uid = uidTraffic3.getUid();
                long rxBytes2 = uidTraffic3.getRxBytes() - this.mLastBluetoothActivityInfo.uidRxBytes.get(uid);
                long txBytes2 = uidTraffic3.getTxBytes() - this.mLastBluetoothActivityInfo.uidTxBytes.get(uid);
                List list = uidTraffic;
                int i10 = i9;
                int i11 = size2;
                char c2 = c;
                getUidStatsLocked(mapUid(uid), j2, j3).getOrCreateBluetoothControllerActivityLocked();
                if (j11 > 0 && rxBytes2 > 0) {
                    sparseLongArray6.incrementValue(uid, (rxBytes2 * j5) / j11);
                }
                if (i8 > 0 && txBytes2 > 0) {
                    sparseLongArray5.incrementValue(uid, (txBytes2 * j6) / j10);
                }
                i9 = i10 + 1;
                c = c2;
                uidTraffic = list;
                size2 = i11;
            }
            int i12 = 0;
            while (i12 < sparseLongArray5.size()) {
                int keyAt = sparseLongArray5.keyAt(i12);
                long valueAt2 = sparseLongArray5.valueAt(i12);
                SparseDoubleArray sparseDoubleArray4 = sparseDoubleArray3;
                SparseLongArray sparseLongArray7 = sparseLongArray5;
                getUidStatsLocked(keyAt, j2, j3).getOrCreateBluetoothControllerActivityLocked().getOrCreateTxTimeCounters()[0].increment(valueAt2, j2);
                if (sparseDoubleArray4 != null) {
                    sparseDoubleArray2 = sparseDoubleArray4;
                    sparseDoubleArray2.incrementValue(keyAt, this.mBluetoothPowerCalculator.calculatePowerMah(0L, valueAt2, 0L));
                } else {
                    sparseDoubleArray2 = sparseDoubleArray4;
                }
                i12++;
                sparseDoubleArray3 = sparseDoubleArray2;
                sparseLongArray5 = sparseLongArray7;
            }
            sparseDoubleArray = sparseDoubleArray3;
            int i13 = 0;
            while (i13 < sparseLongArray6.size()) {
                int keyAt2 = sparseLongArray6.keyAt(i13);
                long valueAt3 = sparseLongArray6.valueAt(i13);
                SparseLongArray sparseLongArray8 = sparseLongArray6;
                long j13 = controllerRxTimeMillis;
                getUidStatsLocked(sparseLongArray6.keyAt(i13), j2, j3).getOrCreateBluetoothControllerActivityLocked().getOrCreateRxTimeCounter().increment(valueAt3, j2);
                if (sparseDoubleArray != null) {
                    sparseDoubleArray.incrementValue(keyAt2, this.mBluetoothPowerCalculator.calculatePowerMah(valueAt3, 0L, 0L));
                }
                i13++;
                controllerRxTimeMillis = j13;
                sparseLongArray6 = sparseLongArray8;
            }
        }
        long j14 = controllerRxTimeMillis;
        this.mBluetoothActivity.getOrCreateRxTimeCounter().increment(j14, j2);
        this.mBluetoothActivity.getOrCreateTxTimeCounters()[0].increment(j12, j2);
        this.mBluetoothActivity.getOrCreateIdleTimeCounter().increment(j9, j2);
        double averagePower = this.mPowerProfile.getAveragePower("bluetooth.controller.voltage") / 1000.0d;
        if (averagePower != 0.0d) {
            d = (bluetoothActivityEnergyInfo.getControllerEnergyUsed() - this.mLastBluetoothActivityInfo.energy) / averagePower;
            this.mBluetoothActivity.getPowerCounter().addCountLocked((long) d);
        } else {
            d = 0.0d;
        }
        if (sparseDoubleArray != null) {
            this.mGlobalEnergyConsumerStats.updateStandardBucket(5, j);
            distributeEnergyToUidsLocked(5, j, sparseDoubleArray, Math.max(this.mBluetoothPowerCalculator.calculatePowerMah(j14, j12, j9), d / 3600000.0d), j2);
        }
        this.mLastBluetoothActivityInfo.set(bluetoothActivityEnergyInfo);
    }

    public void fillLowPowerStats() {
        if (this.mPlatformIdleStateCallback == null) {
            return;
        }
        RpmStats rpmStats = new RpmStats();
        long elapsedRealtime = SystemClock.elapsedRealtime();
        if (elapsedRealtime - this.mLastRpmStatsUpdateTimeMs >= 1000) {
            this.mPlatformIdleStateCallback.fillLowPowerStats(rpmStats);
            synchronized (this) {
                this.mTmpRpmStats = rpmStats;
                this.mLastRpmStatsUpdateTimeMs = elapsedRealtime;
            }
        }
    }

    public void updateRpmStatsLocked(long j) {
        RpmStats rpmStats = this.mTmpRpmStats;
        if (rpmStats == null) {
            return;
        }
        for (Map.Entry entry : rpmStats.mPlatformLowPowerStats.entrySet()) {
            String str = (String) entry.getKey();
            getRpmTimerLocked(str).update(((RpmStats.PowerStatePlatformSleepState) entry.getValue()).mTimeMs * 1000, ((RpmStats.PowerStatePlatformSleepState) entry.getValue()).mCount, j);
            for (Map.Entry entry2 : ((RpmStats.PowerStatePlatformSleepState) entry.getValue()).mVoters.entrySet()) {
                int i = ((RpmStats.PowerStateElement) entry2.getValue()).mCount;
                getRpmTimerLocked(str + "." + ((String) entry2.getKey())).update(((RpmStats.PowerStateElement) entry2.getValue()).mTimeMs * 1000, i, j);
            }
        }
        for (Map.Entry entry3 : this.mTmpRpmStats.mSubsystemLowPowerStats.entrySet()) {
            String str2 = (String) entry3.getKey();
            for (Map.Entry entry4 : ((RpmStats.PowerStateSubsystem) entry3.getValue()).mStates.entrySet()) {
                int i2 = ((RpmStats.PowerStateElement) entry4.getValue()).mCount;
                getRpmTimerLocked(str2 + "." + ((String) entry4.getKey())).update(((RpmStats.PowerStateElement) entry4.getValue()).mTimeMs * 1000, i2, j);
            }
        }
    }

    @GuardedBy({"this"})
    public final void updateCpuEnergyConsumerStatsLocked(long[] jArr, CpuDeltaPowerAccumulator cpuDeltaPowerAccumulator) {
        if (this.mGlobalEnergyConsumerStats == null) {
            return;
        }
        int length = jArr.length;
        long j = 0;
        long j2 = 0;
        for (long j3 : jArr) {
            j2 += j3;
        }
        if (j2 <= 0) {
            return;
        }
        long elapsedRealtime = this.mClock.elapsedRealtime();
        this.mGlobalEnergyConsumerStats.updateStandardBucket(3, j2, elapsedRealtime);
        double[] dArr = new double[length];
        for (int i = 0; i < length; i++) {
            double d = cpuDeltaPowerAccumulator.totalClusterChargesMah[i];
            if (d <= 0.0d) {
                dArr[i] = 0.0d;
            } else {
                dArr[i] = jArr[i] / d;
            }
        }
        long size = cpuDeltaPowerAccumulator.perUidCpuClusterChargesMah.size();
        int i2 = 0;
        while (i2 < size) {
            Uid keyAt = cpuDeltaPowerAccumulator.perUidCpuClusterChargesMah.keyAt(i2);
            double[] valueAt = cpuDeltaPowerAccumulator.perUidCpuClusterChargesMah.valueAt(i2);
            long j4 = j;
            int i3 = 0;
            while (i3 < length) {
                j4 += (long) ((valueAt[i3] * dArr[i3]) + 0.5d);
                i3++;
                j = 0;
            }
            long j5 = j;
            if (j4 < j5) {
                Slog.wtf("BatteryStatsImpl", "Unexpected proportional EnergyConsumer charge (" + j4 + ") for uid " + keyAt.mUid);
            } else {
                keyAt.addChargeToStandardBucketLocked(j4, 3, elapsedRealtime);
            }
            i2++;
            j = j5;
        }
    }

    @GuardedBy({"this"})
    public void updateDisplayEnergyConsumerStatsLocked(long[] jArr, int[] iArr, long j) {
        int length;
        if (this.mGlobalEnergyConsumerStats == null) {
            return;
        }
        if (this.mPerDisplayBatteryStats.length == iArr.length) {
            length = iArr.length;
        } else {
            int i = this.mDisplayMismatchWtfCount;
            this.mDisplayMismatchWtfCount = i + 1;
            if (i % 100 == 0) {
                Slog.wtf("BatteryStatsImpl", "Mismatch between PowerProfile reported display count (" + this.mPerDisplayBatteryStats.length + ") and PowerStatsHal reported display count (" + iArr.length + ")");
            }
            DisplayBatteryStats[] displayBatteryStatsArr = this.mPerDisplayBatteryStats;
            length = displayBatteryStatsArr.length < iArr.length ? displayBatteryStatsArr.length : iArr.length;
        }
        int[] iArr2 = new int[length];
        for (int i2 = 0; i2 < length; i2++) {
            int i3 = iArr[i2];
            DisplayBatteryStats displayBatteryStats = this.mPerDisplayBatteryStats[i2];
            iArr2[i2] = displayBatteryStats.screenStateAtLastEnergyMeasurement;
            displayBatteryStats.screenStateAtLastEnergyMeasurement = i3;
        }
        if (this.mOnBatteryInternal) {
            if (this.mIgnoreNextExternalStats) {
                int size = this.mUidStats.size();
                for (int i4 = 0; i4 < size; i4++) {
                    this.mUidStats.valueAt(i4).markProcessForegroundTimeUs(j, false);
                }
                return;
            }
            long j2 = 0;
            for (int i5 = 0; i5 < length; i5++) {
                long j3 = jArr[i5];
                if (j3 > 0) {
                    int displayPowerBucket = EnergyConsumerStats.getDisplayPowerBucket(iArr2[i5]);
                    this.mGlobalEnergyConsumerStats.updateStandardBucket(displayPowerBucket, j3);
                    if (displayPowerBucket == 0) {
                        j2 += j3;
                    }
                }
            }
            if (j2 <= 0) {
                return;
            }
            SparseDoubleArray sparseDoubleArray = new SparseDoubleArray();
            int size2 = this.mUidStats.size();
            for (int i6 = 0; i6 < size2; i6++) {
                Uid valueAt = this.mUidStats.valueAt(i6);
                long markProcessForegroundTimeUs = valueAt.markProcessForegroundTimeUs(j, true);
                if (markProcessForegroundTimeUs != 0) {
                    sparseDoubleArray.put(valueAt.getUid(), markProcessForegroundTimeUs);
                }
            }
            distributeEnergyToUidsLocked(0, j2, sparseDoubleArray, 0.0d, j);
        }
    }

    @GuardedBy({"this"})
    public void updateGnssEnergyConsumerStatsLocked(long j, long j2) {
        EnergyConsumerStats energyConsumerStats = this.mGlobalEnergyConsumerStats;
        if (energyConsumerStats != null && this.mOnBatteryInternal && j > 0) {
            int i = 0;
            if (this.mIgnoreNextExternalStats) {
                int size = this.mUidStats.size();
                while (i < size) {
                    this.mUidStats.valueAt(i).markGnssTimeUs(j2);
                    i++;
                }
                return;
            }
            energyConsumerStats.updateStandardBucket(6, j);
            SparseDoubleArray sparseDoubleArray = new SparseDoubleArray();
            int size2 = this.mUidStats.size();
            while (i < size2) {
                Uid valueAt = this.mUidStats.valueAt(i);
                long markGnssTimeUs = valueAt.markGnssTimeUs(j2);
                if (markGnssTimeUs != 0) {
                    sparseDoubleArray.put(valueAt.getUid(), markGnssTimeUs);
                }
                i++;
            }
            distributeEnergyToUidsLocked(6, j, sparseDoubleArray, 0.0d, j2);
        }
    }

    @GuardedBy({"this"})
    public void updateCameraEnergyConsumerStatsLocked(long j, long j2) {
        EnergyConsumerStats energyConsumerStats = this.mGlobalEnergyConsumerStats;
        if (energyConsumerStats != null && this.mOnBatteryInternal && j > 0) {
            int i = 0;
            if (this.mIgnoreNextExternalStats) {
                int size = this.mUidStats.size();
                while (i < size) {
                    this.mUidStats.valueAt(i).markCameraTimeUs(j2);
                    i++;
                }
                return;
            }
            energyConsumerStats.updateStandardBucket(8, j);
            SparseDoubleArray sparseDoubleArray = new SparseDoubleArray();
            int size2 = this.mUidStats.size();
            while (i < size2) {
                Uid valueAt = this.mUidStats.valueAt(i);
                long markCameraTimeUs = valueAt.markCameraTimeUs(j2);
                if (markCameraTimeUs != 0) {
                    sparseDoubleArray.put(valueAt.getUid(), markCameraTimeUs);
                }
                i++;
            }
            distributeEnergyToUidsLocked(8, j, sparseDoubleArray, 0.0d, j2);
        }
    }

    @GuardedBy({"this"})
    public void updateCustomEnergyConsumerStatsLocked(int i, long j, SparseLongArray sparseLongArray) {
        EnergyConsumerStats energyConsumerStats = this.mGlobalEnergyConsumerStats;
        if (energyConsumerStats != null && this.mOnBatteryInternal && !this.mIgnoreNextExternalStats && j > 0) {
            energyConsumerStats.updateCustomBucket(i, j, this.mClock.elapsedRealtime());
            if (sparseLongArray == null) {
                return;
            }
            int size = sparseLongArray.size();
            for (int i2 = 0; i2 < size; i2++) {
                int mapUid = mapUid(sparseLongArray.keyAt(i2));
                long valueAt = sparseLongArray.valueAt(i2);
                if (valueAt != 0) {
                    Uid availableUidStatsLocked = getAvailableUidStatsLocked(mapUid);
                    if (availableUidStatsLocked != null) {
                        availableUidStatsLocked.addChargeToCustomBucketLocked(valueAt, i);
                    } else if (!Process.isIsolated(mapUid)) {
                        Slog.w("BatteryStatsImpl", "Received EnergyConsumer charge " + j + " for custom bucket " + i + " for non-existent uid " + mapUid);
                    }
                }
            }
        }
    }

    @GuardedBy({"this"})
    public final void distributeEnergyToUidsLocked(int i, long j, SparseDoubleArray sparseDoubleArray, double d, long j2) {
        double d2 = 0.0d;
        for (int size = sparseDoubleArray.size() - 1; size >= 0; size--) {
            d2 += sparseDoubleArray.valueAt(size);
        }
        double max = Math.max(d2, d);
        if (max <= 0.0d) {
            return;
        }
        for (int size2 = sparseDoubleArray.size() - 1; size2 >= 0; size2--) {
            getAvailableUidStatsLocked(sparseDoubleArray.keyAt(size2)).addChargeToStandardBucketLocked((long) (((j * sparseDoubleArray.valueAt(size2)) / max) + 0.5d), i, j2);
        }
    }

    public void updateRailStatsLocked() {
        if (this.mEnergyConsumerRetriever == null || !this.mTmpRailStats.isRailStatsAvailable()) {
            return;
        }
        this.mEnergyConsumerRetriever.fillRailDataStats(this.mTmpRailStats);
    }

    public void informThatAllExternalStatsAreFlushed() {
        synchronized (this) {
            this.mIgnoreNextExternalStats = false;
        }
    }

    public void updateKernelWakelocksLocked(long j) {
        KernelWakelockStats readKernelWakelockStats = this.mKernelWakelockReader.readKernelWakelockStats(this.mTmpWakelockStats);
        if (readKernelWakelockStats == null) {
            Slog.w("BatteryStatsImpl", "Couldn't get kernel wake lock stats");
            return;
        }
        for (Map.Entry<String, KernelWakelockStats.Entry> entry : readKernelWakelockStats.entrySet()) {
            String key = entry.getKey();
            KernelWakelockStats.Entry value = entry.getValue();
            SamplingTimer samplingTimer = this.mKernelWakelockStats.get(key);
            if (samplingTimer == null) {
                samplingTimer = new SamplingTimer(this.mClock, this.mOnBatteryScreenOffTimeBase);
                this.mKernelWakelockStats.put(key, samplingTimer);
            }
            samplingTimer.update(value.mTotalTime, value.mCount, j);
            samplingTimer.setUpdateVersion(value.mVersion);
        }
        int i = 0;
        for (Map.Entry<String, SamplingTimer> entry2 : this.mKernelWakelockStats.entrySet()) {
            SamplingTimer value2 = entry2.getValue();
            if (value2.getUpdateVersion() != readKernelWakelockStats.kernelWakelockVersion) {
                value2.endSample(j);
                i++;
            }
        }
        if (readKernelWakelockStats.isEmpty()) {
            Slog.wtf("BatteryStatsImpl", "All kernel wakelocks had time of zero");
        }
        if (i == this.mKernelWakelockStats.size()) {
            Slog.wtf("BatteryStatsImpl", "All kernel wakelocks were set stale. new version=" + readKernelWakelockStats.kernelWakelockVersion);
        }
    }

    public void updateKernelMemoryBandwidthLocked(long j) {
        SamplingTimer samplingTimer;
        this.mKernelMemoryBandwidthStats.updateStats();
        LongSparseLongArray bandwidthEntries = this.mKernelMemoryBandwidthStats.getBandwidthEntries();
        int size = bandwidthEntries.size();
        for (int i = 0; i < size; i++) {
            int indexOfKey = this.mKernelMemoryStats.indexOfKey(bandwidthEntries.keyAt(i));
            if (indexOfKey >= 0) {
                samplingTimer = this.mKernelMemoryStats.valueAt(indexOfKey);
            } else {
                samplingTimer = new SamplingTimer(this.mClock, this.mOnBatteryTimeBase);
                this.mKernelMemoryStats.put(bandwidthEntries.keyAt(i), samplingTimer);
            }
            samplingTimer.update(bandwidthEntries.valueAt(i), 1, j);
        }
    }

    public boolean isOnBatteryLocked() {
        return this.mOnBatteryTimeBase.isRunning();
    }

    public boolean isOnBatteryScreenOffLocked() {
        return this.mOnBatteryScreenOffTimeBase.isRunning();
    }

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public static class CpuDeltaPowerAccumulator {
        public final CpuPowerCalculator mCalculator;
        public final double[] totalClusterChargesMah;
        public Uid mCachedUid = null;
        public double[] mUidClusterCache = null;
        public final ArrayMap<Uid, double[]> perUidCpuClusterChargesMah = new ArrayMap<>();

        public CpuDeltaPowerAccumulator(CpuPowerCalculator cpuPowerCalculator, int i) {
            this.mCalculator = cpuPowerCalculator;
            this.totalClusterChargesMah = new double[i];
        }

        public void addCpuClusterDurationsMs(Uid uid, long[] jArr) {
            double[] orCreateUidCpuClusterCharges = getOrCreateUidCpuClusterCharges(uid);
            for (int i = 0; i < jArr.length; i++) {
                double calculatePerCpuClusterPowerMah = this.mCalculator.calculatePerCpuClusterPowerMah(i, jArr[i]);
                orCreateUidCpuClusterCharges[i] = orCreateUidCpuClusterCharges[i] + calculatePerCpuClusterPowerMah;
                double[] dArr = this.totalClusterChargesMah;
                dArr[i] = dArr[i] + calculatePerCpuClusterPowerMah;
            }
        }

        public void addCpuClusterSpeedDurationsMs(Uid uid, int i, int i2, long j) {
            double[] orCreateUidCpuClusterCharges = getOrCreateUidCpuClusterCharges(uid);
            double calculatePerCpuFreqPowerMah = this.mCalculator.calculatePerCpuFreqPowerMah(i, i2, j);
            orCreateUidCpuClusterCharges[i] = orCreateUidCpuClusterCharges[i] + calculatePerCpuFreqPowerMah;
            double[] dArr = this.totalClusterChargesMah;
            dArr[i] = dArr[i] + calculatePerCpuFreqPowerMah;
        }

        public final double[] getOrCreateUidCpuClusterCharges(Uid uid) {
            if (uid == this.mCachedUid) {
                return this.mUidClusterCache;
            }
            double[] dArr = this.perUidCpuClusterChargesMah.get(uid);
            if (dArr == null) {
                dArr = new double[this.totalClusterChargesMah.length];
                this.perUidCpuClusterChargesMah.put(uid, dArr);
            }
            this.mCachedUid = uid;
            this.mUidClusterCache = dArr;
            return dArr;
        }
    }

    @GuardedBy({"this"})
    public void updateCpuTimeLocked(boolean z, boolean z2, long[] jArr) {
        ArrayList<StopwatchTimer> arrayList;
        Uid uid;
        PowerProfile powerProfile = this.mPowerProfile;
        if (powerProfile == null) {
            return;
        }
        if (this.mCpuFreqs == null) {
            this.mCpuFreqs = this.mCpuUidFreqTimeReader.readFreqs(powerProfile);
        }
        CpuDeltaPowerAccumulator cpuDeltaPowerAccumulator = null;
        if (z2) {
            arrayList = new ArrayList<>();
            for (int size = this.mPartialTimers.size() - 1; size >= 0; size--) {
                StopwatchTimer stopwatchTimer = this.mPartialTimers.get(size);
                if (stopwatchTimer.mInList && (uid = stopwatchTimer.mUid) != null && uid.mUid != 1000) {
                    arrayList.add(stopwatchTimer);
                }
            }
        } else {
            arrayList = null;
        }
        markPartialTimersAsEligible();
        if (!z) {
            this.mCpuUidUserSysTimeReader.readDelta(false, (KernelCpuUidTimeReader.Callback) null);
            this.mCpuUidFreqTimeReader.readDelta(false, (KernelCpuUidTimeReader.Callback) null);
            this.mNumAllUidCpuTimeReads += 2;
            if (this.mConstants.TRACK_CPU_ACTIVE_CLUSTER_TIME) {
                this.mCpuUidActiveTimeReader.readDelta(false, (KernelCpuUidTimeReader.Callback) null);
                this.mCpuUidClusterTimeReader.readDelta(false, (KernelCpuUidTimeReader.Callback) null);
                this.mNumAllUidCpuTimeReads += 2;
            }
            for (int length = this.mKernelCpuSpeedReaders.length - 1; length >= 0; length--) {
                this.mKernelCpuSpeedReaders[length].readDelta();
            }
            this.mSystemServerCpuThreadReader.readDelta();
            return;
        }
        this.mUserInfoProvider.refreshUserIds();
        SparseLongArray sparseLongArray = this.mCpuUidFreqTimeReader.perClusterTimesAvailable() ? null : new SparseLongArray();
        EnergyConsumerStats energyConsumerStats = this.mGlobalEnergyConsumerStats;
        if (energyConsumerStats != null && energyConsumerStats.isStandardBucketSupported(3) && this.mCpuPowerCalculator != null) {
            if (jArr == null) {
                Slog.wtf("BatteryStatsImpl", "POWER_BUCKET_CPU supported but no EnergyConsumer Cpu Cluster charge reported on updateCpuTimeLocked!");
            } else {
                cpuDeltaPowerAccumulator = new CpuDeltaPowerAccumulator(this.mCpuPowerCalculator, this.mPowerProfile.getNumCpuClusters());
            }
        }
        readKernelUidCpuTimesLocked(arrayList, sparseLongArray, z);
        if (sparseLongArray != null) {
            updateClusterSpeedTimes(sparseLongArray, z, cpuDeltaPowerAccumulator);
        }
        readKernelUidCpuFreqTimesLocked(arrayList, z, z2, cpuDeltaPowerAccumulator);
        this.mNumAllUidCpuTimeReads += 2;
        if (this.mConstants.TRACK_CPU_ACTIVE_CLUSTER_TIME) {
            readKernelUidCpuActiveTimesLocked(z);
            readKernelUidCpuClusterTimesLocked(z, cpuDeltaPowerAccumulator);
            this.mNumAllUidCpuTimeReads += 2;
        }
        updateSystemServerThreadStats();
        if (cpuDeltaPowerAccumulator != null) {
            updateCpuEnergyConsumerStatsLocked(jArr, cpuDeltaPowerAccumulator);
        }
    }

    @VisibleForTesting
    public void updateSystemServerThreadStats() {
        SystemServerCpuThreadReader.SystemServiceCpuThreadTimes readDelta = this.mSystemServerCpuThreadReader.readDelta();
        if (readDelta == null) {
            return;
        }
        if (this.mBinderThreadCpuTimesUs == null) {
            this.mBinderThreadCpuTimesUs = new LongSamplingCounterArray(this.mOnBatteryTimeBase);
        }
        this.mBinderThreadCpuTimesUs.addCountLocked(readDelta.binderThreadCpuTimesUs);
    }

    @VisibleForTesting
    public void markPartialTimersAsEligible() {
        int i;
        if (ArrayUtils.referenceEquals(this.mPartialTimers, this.mLastPartialTimers)) {
            for (int size = this.mPartialTimers.size() - 1; size >= 0; size--) {
                this.mPartialTimers.get(size).mInList = true;
            }
            return;
        }
        int size2 = this.mLastPartialTimers.size() - 1;
        while (true) {
            if (size2 < 0) {
                break;
            }
            this.mLastPartialTimers.get(size2).mInList = false;
            size2--;
        }
        this.mLastPartialTimers.clear();
        int size3 = this.mPartialTimers.size();
        for (i = 0; i < size3; i++) {
            StopwatchTimer stopwatchTimer = this.mPartialTimers.get(i);
            stopwatchTimer.mInList = true;
            this.mLastPartialTimers.add(stopwatchTimer);
        }
    }

    @VisibleForTesting
    public void updateClusterSpeedTimes(SparseLongArray sparseLongArray, boolean z, CpuDeltaPowerAccumulator cpuDeltaPowerAccumulator) {
        int i;
        SparseLongArray sparseLongArray2 = sparseLongArray;
        int length = this.mKernelCpuSpeedReaders.length;
        long[][] jArr = new long[length];
        long j = 0;
        int i2 = 0;
        while (true) {
            KernelCpuSpeedReader[] kernelCpuSpeedReaderArr = this.mKernelCpuSpeedReaders;
            if (i2 >= kernelCpuSpeedReaderArr.length) {
                break;
            }
            long[] readDelta = kernelCpuSpeedReaderArr[i2].readDelta();
            jArr[i2] = readDelta;
            if (readDelta != null) {
                for (int length2 = readDelta.length - 1; length2 >= 0; length2--) {
                    j += jArr[i2][length2];
                }
            }
            i2++;
        }
        if (j != 0) {
            int size = sparseLongArray.size();
            long elapsedRealtime = this.mClock.elapsedRealtime();
            long uptimeMillis = this.mClock.uptimeMillis();
            int i3 = 0;
            while (i3 < size) {
                int i4 = i3;
                Uid uidStatsLocked = getUidStatsLocked(sparseLongArray2.keyAt(i3), elapsedRealtime, uptimeMillis);
                long valueAt = sparseLongArray2.valueAt(i4);
                int numCpuClusters = this.mPowerProfile.getNumCpuClusters();
                LongSamplingCounter[][] longSamplingCounterArr = uidStatsLocked.mCpuClusterSpeedTimesUs;
                if (longSamplingCounterArr == null || longSamplingCounterArr.length != numCpuClusters) {
                    uidStatsLocked.mCpuClusterSpeedTimesUs = new LongSamplingCounter[numCpuClusters];
                }
                int i5 = 0;
                while (i5 < length) {
                    int length3 = jArr[i5].length;
                    LongSamplingCounter[][] longSamplingCounterArr2 = uidStatsLocked.mCpuClusterSpeedTimesUs;
                    LongSamplingCounter[] longSamplingCounterArr3 = longSamplingCounterArr2[i5];
                    if (longSamplingCounterArr3 == null || length3 != longSamplingCounterArr3.length) {
                        longSamplingCounterArr2[i5] = new LongSamplingCounter[length3];
                    }
                    LongSamplingCounter[] longSamplingCounterArr4 = longSamplingCounterArr2[i5];
                    int i6 = 0;
                    while (i6 < length3) {
                        int i7 = length3;
                        if (longSamplingCounterArr4[i6] == null) {
                            i = length;
                            longSamplingCounterArr4[i6] = new LongSamplingCounter(this.mOnBatteryTimeBase);
                        } else {
                            i = length;
                        }
                        long j2 = valueAt;
                        long j3 = (jArr[i5][i6] * valueAt) / j;
                        longSamplingCounterArr4[i6].addCountLocked(j3, z);
                        if (cpuDeltaPowerAccumulator != null) {
                            cpuDeltaPowerAccumulator.addCpuClusterSpeedDurationsMs(uidStatsLocked, i5, i6, j3);
                        }
                        i6++;
                        length3 = i7;
                        length = i;
                        valueAt = j2;
                    }
                    i5++;
                    length = length;
                }
                i3 = i4 + 1;
                sparseLongArray2 = sparseLongArray;
                length = length;
            }
        }
    }

    @VisibleForTesting
    public void readKernelUidCpuTimesLocked(ArrayList<StopwatchTimer> arrayList, final SparseLongArray sparseLongArray, final boolean z) {
        this.mTempTotalCpuSystemTimeUs = 0L;
        this.mTempTotalCpuUserTimeUs = 0L;
        int size = arrayList == null ? 0 : arrayList.size();
        final long uptimeMillis = this.mClock.uptimeMillis();
        final long elapsedRealtime = this.mClock.elapsedRealtime();
        final int i = size;
        int i2 = size;
        this.mCpuUidUserSysTimeReader.readDelta(false, new KernelCpuUidTimeReader.Callback() { // from class: com.android.server.power.stats.BatteryStatsImpl$$ExternalSyntheticLambda0
            public final void onUidCpuTime(int i3, Object obj) {
                BatteryStatsImpl.this.lambda$readKernelUidCpuTimesLocked$0(elapsedRealtime, uptimeMillis, i, z, sparseLongArray, i3, (long[]) obj);
            }
        });
        long uptimeMillis2 = this.mClock.uptimeMillis() - uptimeMillis;
        if (uptimeMillis2 >= 100) {
            Slog.d("BatteryStatsImpl", "Reading cpu stats took " + uptimeMillis2 + "ms");
        }
        if (i2 > 0) {
            this.mTempTotalCpuUserTimeUs = (this.mTempTotalCpuUserTimeUs * 50) / 100;
            this.mTempTotalCpuSystemTimeUs = (this.mTempTotalCpuSystemTimeUs * 50) / 100;
            for (int i3 = 0; i3 < i2; i3++) {
                StopwatchTimer stopwatchTimer = arrayList.get(i3);
                long j = i2 - i3;
                int i4 = (int) (this.mTempTotalCpuUserTimeUs / j);
                int i5 = (int) (this.mTempTotalCpuSystemTimeUs / j);
                long j2 = i4;
                stopwatchTimer.mUid.mUserCpuTime.addCountLocked(j2, z);
                long j3 = i5;
                stopwatchTimer.mUid.mSystemCpuTime.addCountLocked(j3, z);
                if (sparseLongArray != null) {
                    int uid = stopwatchTimer.mUid.getUid();
                    sparseLongArray.put(uid, sparseLongArray.get(uid, 0L) + j2 + j3);
                }
                stopwatchTimer.mUid.getProcessStatsLocked("*wakelock*").addCpuTimeLocked(i4 / 1000, i5 / 1000, z);
                this.mTempTotalCpuUserTimeUs -= j2;
                this.mTempTotalCpuSystemTimeUs -= j3;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$readKernelUidCpuTimesLocked$0(long j, long j2, int i, boolean z, SparseLongArray sparseLongArray, int i2, long[] jArr) {
        long j3 = jArr[0];
        long j4 = jArr[1];
        int mapUid = mapUid(i2);
        if (!Process.isIsolated(mapUid) && this.mUserInfoProvider.exists(UserHandle.getUserId(mapUid))) {
            Uid uidStatsLocked = getUidStatsLocked(mapUid, j, j2);
            this.mTempTotalCpuUserTimeUs += j3;
            this.mTempTotalCpuSystemTimeUs += j4;
            if (i > 0) {
                j3 = (j3 * 50) / 100;
                j4 = (j4 * 50) / 100;
            }
            uidStatsLocked.mUserCpuTime.addCountLocked(j3, z);
            uidStatsLocked.mSystemCpuTime.addCountLocked(j4, z);
            if (sparseLongArray != null) {
                sparseLongArray.put(uidStatsLocked.getUid(), j3 + j4);
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:20:0x008d, code lost:
        if (r0.length != r10) goto L40;
     */
    @VisibleForTesting
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void readKernelUidCpuFreqTimesLocked(ArrayList<StopwatchTimer> arrayList, final boolean z, final boolean z2, final CpuDeltaPowerAccumulator cpuDeltaPowerAccumulator) {
        int i;
        final boolean perClusterTimesAvailable = this.mCpuUidFreqTimeReader.perClusterTimesAvailable();
        int size = arrayList == null ? 0 : arrayList.size();
        final int numCpuClusters = this.mPowerProfile.getNumCpuClusters();
        this.mWakeLockAllocationsUs = null;
        final long uptimeMillis = this.mClock.uptimeMillis();
        final long elapsedRealtime = this.mClock.elapsedRealtime();
        final int i2 = size;
        int i3 = numCpuClusters;
        this.mCpuUidFreqTimeReader.readDelta(cpuDeltaPowerAccumulator != null, new KernelCpuUidTimeReader.Callback() { // from class: com.android.server.power.stats.BatteryStatsImpl$$ExternalSyntheticLambda3
            public final void onUidCpuTime(int i4, Object obj) {
                BatteryStatsImpl.this.lambda$readKernelUidCpuFreqTimesLocked$1(elapsedRealtime, uptimeMillis, z, z2, perClusterTimesAvailable, numCpuClusters, i2, cpuDeltaPowerAccumulator, i4, (long[]) obj);
            }
        });
        long uptimeMillis2 = this.mClock.uptimeMillis() - uptimeMillis;
        if (uptimeMillis2 >= 100) {
            Slog.d("BatteryStatsImpl", "Reading cpu freq times took " + uptimeMillis2 + "ms");
        }
        if (this.mWakeLockAllocationsUs != null) {
            int i4 = 0;
            while (i4 < size) {
                Uid uid = arrayList.get(i4).mUid;
                LongSamplingCounter[][] longSamplingCounterArr = uid.mCpuClusterSpeedTimesUs;
                if (longSamplingCounterArr != null) {
                    i = i3;
                } else {
                    i = i3;
                }
                detachIfNotNull(longSamplingCounterArr);
                uid.mCpuClusterSpeedTimesUs = new LongSamplingCounter[i];
                for (int i5 = 0; i5 < i; i5++) {
                    int numSpeedStepsInCpuCluster = this.mPowerProfile.getNumSpeedStepsInCpuCluster(i5);
                    LongSamplingCounter[] longSamplingCounterArr2 = uid.mCpuClusterSpeedTimesUs[i5];
                    if (longSamplingCounterArr2 == null || longSamplingCounterArr2.length != numSpeedStepsInCpuCluster) {
                        detachIfNotNull(longSamplingCounterArr2);
                        uid.mCpuClusterSpeedTimesUs[i5] = new LongSamplingCounter[numSpeedStepsInCpuCluster];
                    }
                    LongSamplingCounter[] longSamplingCounterArr3 = uid.mCpuClusterSpeedTimesUs[i5];
                    for (int i6 = 0; i6 < numSpeedStepsInCpuCluster; i6++) {
                        if (longSamplingCounterArr3[i6] == null) {
                            longSamplingCounterArr3[i6] = new LongSamplingCounter(this.mOnBatteryTimeBase);
                        }
                        long j = this.mWakeLockAllocationsUs[i5][i6] / (size - i4);
                        longSamplingCounterArr3[i6].addCountLocked(j, z);
                        long[] jArr = this.mWakeLockAllocationsUs[i5];
                        jArr[i6] = jArr[i6] - j;
                        if (cpuDeltaPowerAccumulator != null) {
                            cpuDeltaPowerAccumulator.addCpuClusterSpeedDurationsMs(uid, i5, i6, j / 1000);
                        }
                    }
                }
                i4++;
                i3 = i;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$readKernelUidCpuFreqTimesLocked$1(long j, long j2, boolean z, boolean z2, boolean z3, int i, int i2, CpuDeltaPowerAccumulator cpuDeltaPowerAccumulator, int i3, long[] jArr) {
        long j3;
        int i4;
        int mapUid = mapUid(i3);
        if (!Process.isIsolated(mapUid) && this.mUserInfoProvider.exists(UserHandle.getUserId(mapUid))) {
            Uid uidStatsLocked = getUidStatsLocked(mapUid, j, j2);
            LongSamplingCounterArray longSamplingCounterArray = uidStatsLocked.mCpuFreqTimeMs;
            if (longSamplingCounterArray == null || longSamplingCounterArray.getSize() != jArr.length) {
                detachIfNotNull(uidStatsLocked.mCpuFreqTimeMs);
                uidStatsLocked.mCpuFreqTimeMs = new LongSamplingCounterArray(this.mOnBatteryTimeBase);
            }
            uidStatsLocked.mCpuFreqTimeMs.addCountLocked(jArr, z);
            LongSamplingCounterArray longSamplingCounterArray2 = uidStatsLocked.mScreenOffCpuFreqTimeMs;
            if (longSamplingCounterArray2 == null || longSamplingCounterArray2.getSize() != jArr.length) {
                detachIfNotNull(uidStatsLocked.mScreenOffCpuFreqTimeMs);
                uidStatsLocked.mScreenOffCpuFreqTimeMs = new LongSamplingCounterArray(this.mOnBatteryScreenOffTimeBase);
            }
            uidStatsLocked.mScreenOffCpuFreqTimeMs.addCountLocked(jArr, z2);
            if (z3) {
                LongSamplingCounter[][] longSamplingCounterArr = uidStatsLocked.mCpuClusterSpeedTimesUs;
                if (longSamplingCounterArr == null || longSamplingCounterArr.length != i) {
                    detachIfNotNull(longSamplingCounterArr);
                    uidStatsLocked.mCpuClusterSpeedTimesUs = new LongSamplingCounter[i];
                }
                if (i2 > 0 && this.mWakeLockAllocationsUs == null) {
                    this.mWakeLockAllocationsUs = new long[i];
                }
                int i5 = 0;
                int i6 = 0;
                while (i5 < i) {
                    int numSpeedStepsInCpuCluster = this.mPowerProfile.getNumSpeedStepsInCpuCluster(i5);
                    LongSamplingCounter[] longSamplingCounterArr2 = uidStatsLocked.mCpuClusterSpeedTimesUs[i5];
                    if (longSamplingCounterArr2 == null || longSamplingCounterArr2.length != numSpeedStepsInCpuCluster) {
                        detachIfNotNull(longSamplingCounterArr2);
                        uidStatsLocked.mCpuClusterSpeedTimesUs[i5] = new LongSamplingCounter[numSpeedStepsInCpuCluster];
                    }
                    if (i2 > 0) {
                        long[][] jArr2 = this.mWakeLockAllocationsUs;
                        if (jArr2[i5] == null) {
                            jArr2[i5] = new long[numSpeedStepsInCpuCluster];
                        }
                    }
                    LongSamplingCounter[] longSamplingCounterArr3 = uidStatsLocked.mCpuClusterSpeedTimesUs[i5];
                    int i7 = 0;
                    while (i7 < numSpeedStepsInCpuCluster) {
                        if (longSamplingCounterArr3[i7] == null) {
                            longSamplingCounterArr3[i7] = new LongSamplingCounter(this.mOnBatteryTimeBase);
                        }
                        long[][] jArr3 = this.mWakeLockAllocationsUs;
                        if (jArr3 != null) {
                            long j4 = jArr[i6];
                            j3 = ((j4 * 1000) * 50) / 100;
                            long[] jArr4 = jArr3[i5];
                            jArr4[i7] = jArr4[i7] + ((j4 * 1000) - j3);
                        } else {
                            j3 = jArr[i6] * 1000;
                        }
                        int i8 = i5;
                        long j5 = j3;
                        longSamplingCounterArr3[i7].addCountLocked(j5, z);
                        if (cpuDeltaPowerAccumulator != null) {
                            i4 = i7;
                            cpuDeltaPowerAccumulator.addCpuClusterSpeedDurationsMs(uidStatsLocked, i8, i7, j5 / 1000);
                        } else {
                            i4 = i7;
                        }
                        i6++;
                        i7 = i4 + 1;
                        i5 = i8;
                    }
                    i5++;
                }
            }
        }
    }

    @VisibleForTesting
    public void readKernelUidCpuActiveTimesLocked(boolean z) {
        final long uptimeMillis = this.mClock.uptimeMillis();
        final long elapsedRealtime = this.mClock.elapsedRealtime();
        this.mCpuUidActiveTimeReader.readAbsolute(new KernelCpuUidTimeReader.Callback() { // from class: com.android.server.power.stats.BatteryStatsImpl$$ExternalSyntheticLambda1
            public final void onUidCpuTime(int i, Object obj) {
                BatteryStatsImpl.this.lambda$readKernelUidCpuActiveTimesLocked$2(elapsedRealtime, uptimeMillis, i, (Long) obj);
            }
        });
        long uptimeMillis2 = this.mClock.uptimeMillis() - uptimeMillis;
        if (uptimeMillis2 >= 100) {
            Slog.d("BatteryStatsImpl", "Reading cpu active times took " + uptimeMillis2 + "ms");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$readKernelUidCpuActiveTimesLocked$2(long j, long j2, int i, Long l) {
        Uid.ChildUid childUid;
        int mapUid = mapUid(i);
        if (!Process.isIsolated(mapUid) && this.mUserInfoProvider.exists(UserHandle.getUserId(i))) {
            Uid uidStatsLocked = getUidStatsLocked(mapUid, j, j2);
            if (mapUid == i) {
                uidStatsLocked.getCpuActiveTimeCounter().update(l.longValue(), j);
                return;
            }
            SparseArray<Uid.ChildUid> sparseArray = uidStatsLocked.mChildUids;
            if (sparseArray == null || (childUid = sparseArray.get(i)) == null) {
                return;
            }
            uidStatsLocked.getCpuActiveTimeCounter().increment(childUid.cpuActiveCounter.update(l.longValue(), j), j);
        }
    }

    @VisibleForTesting
    public void readKernelUidCpuClusterTimesLocked(final boolean z, final CpuDeltaPowerAccumulator cpuDeltaPowerAccumulator) {
        final long uptimeMillis = this.mClock.uptimeMillis();
        final long elapsedRealtime = this.mClock.elapsedRealtime();
        this.mCpuUidClusterTimeReader.readDelta(cpuDeltaPowerAccumulator != null, new KernelCpuUidTimeReader.Callback() { // from class: com.android.server.power.stats.BatteryStatsImpl$$ExternalSyntheticLambda4
            public final void onUidCpuTime(int i, Object obj) {
                BatteryStatsImpl.this.lambda$readKernelUidCpuClusterTimesLocked$3(elapsedRealtime, uptimeMillis, z, cpuDeltaPowerAccumulator, i, (long[]) obj);
            }
        });
        long uptimeMillis2 = this.mClock.uptimeMillis() - uptimeMillis;
        if (uptimeMillis2 >= 100) {
            Slog.d("BatteryStatsImpl", "Reading cpu cluster times took " + uptimeMillis2 + "ms");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$readKernelUidCpuClusterTimesLocked$3(long j, long j2, boolean z, CpuDeltaPowerAccumulator cpuDeltaPowerAccumulator, int i, long[] jArr) {
        int mapUid = mapUid(i);
        if (!Process.isIsolated(mapUid) && this.mUserInfoProvider.exists(UserHandle.getUserId(mapUid))) {
            Uid uidStatsLocked = getUidStatsLocked(mapUid, j, j2);
            uidStatsLocked.mCpuClusterTimesMs.addCountLocked(jArr, z);
            if (cpuDeltaPowerAccumulator != null) {
                cpuDeltaPowerAccumulator.addCpuClusterDurationsMs(uidStatsLocked, jArr);
            }
        }
    }

    public boolean setChargingLocked(boolean z) {
        this.mHandler.removeCallbacks(this.mDeferSetCharging);
        if (this.mCharging != z) {
            this.mCharging = z;
            this.mHistory.setChargingState(z);
            this.mHandler.sendEmptyMessage(3);
            return true;
        }
        return false;
    }

    public void onSystemReady() {
        this.mSystemReady = true;
    }

    @VisibleForTesting
    public void forceRecordAllHistory() {
        this.mHistory.forceRecordAllHistory();
        this.mRecordAllHistory = true;
    }

    @GuardedBy({"this"})
    public void maybeResetWhilePluggedInLocked() {
        long elapsedRealtime = this.mClock.elapsedRealtime();
        if (shouldResetWhilePluggedInLocked(elapsedRealtime)) {
            Slog.i("BatteryStatsImpl", "Resetting due to long plug in duration. elapsed time = " + elapsedRealtime + " ms, last plug in time = " + this.mBatteryPluggedInRealTimeMs + " ms, last reset time = " + (this.mRealtimeStartUs / 1000));
            resetAllStatsAndHistoryLocked(5);
        }
        scheduleNextResetWhilePluggedInCheck();
    }

    @GuardedBy({"this"})
    public final void scheduleNextResetWhilePluggedInCheck() {
        if (this.mLongPlugInAlarmInterface != null) {
            long currentTimeMillis = this.mClock.currentTimeMillis() + (this.mConstants.RESET_WHILE_PLUGGED_IN_MINIMUM_DURATION_HOURS * ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(currentTimeMillis);
            calendar.set(14, 0);
            calendar.set(13, 0);
            calendar.set(12, 0);
            calendar.set(11, 2);
            long timeInMillis = calendar.getTimeInMillis();
            if (timeInMillis < currentTimeMillis) {
                timeInMillis += BackupManagerConstants.DEFAULT_FULL_BACKUP_INTERVAL_MILLISECONDS;
            }
            this.mLongPlugInAlarmInterface.schedule(timeInMillis, ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS);
        }
    }

    @GuardedBy({"this"})
    public final boolean shouldResetWhilePluggedInLocked(long j) {
        if (!this.mNoAutoReset && this.mSystemReady && this.mHistory.isResetEnabled()) {
            long j2 = this.mBatteryPluggedInRealTimeMs;
            int i = this.mConstants.RESET_WHILE_PLUGGED_IN_MINIMUM_DURATION_HOURS;
            return j >= j2 + (((long) i) * ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS) && j >= (this.mRealtimeStartUs / 1000) + (((long) i) * ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS);
        }
        return false;
    }

    @GuardedBy({"this"})
    public final boolean shouldResetOnUnplugLocked(int i, int i2) {
        if (!this.mNoAutoReset && this.mSystemReady && this.mHistory.isResetEnabled()) {
            if (!this.mBatteryStatsConfig.shouldResetOnUnplugHighBatteryLevel() || (i != 5 && i2 < 90)) {
                return (this.mBatteryStatsConfig.shouldResetOnUnplugAfterSignificantCharge() && this.mDischargePlugLevel < 20 && i2 >= 80) || getHighDischargeAmountSinceCharge() >= 200;
            }
            return true;
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r8v2 */
    /* JADX WARN: Type inference failed for: r8v3, types: [int, boolean] */
    /* JADX WARN: Type inference failed for: r8v4 */
    @GuardedBy({"this"})
    public void setOnBatteryLocked(long j, long j2, boolean z, int i, int i2, int i3) {
        boolean z2;
        boolean z3;
        ?? r8;
        boolean z4;
        Message obtainMessage = this.mHandler.obtainMessage(2);
        obtainMessage.arg1 = z ? 1 : 0;
        this.mHandler.sendMessage(obtainMessage);
        long j3 = j2 * 1000;
        long j4 = j * 1000;
        int i4 = this.mScreenState;
        if (z) {
            if (shouldResetOnUnplugLocked(i, i2)) {
                Slog.i("BatteryStatsImpl", "Resetting battery stats: level=" + i2 + " status=" + i + " dischargeLevel=" + this.mDischargePlugLevel + " lowAmount=" + getLowDischargeAmountSinceCharge() + " highAmount=" + getHighDischargeAmountSinceCharge());
                if (getLowDischargeAmountSinceCharge() >= 20) {
                    long uptimeMillis = SystemClock.uptimeMillis();
                    final Parcel obtain = Parcel.obtain();
                    writeSummaryToParcel(obtain, true);
                    final long uptimeMillis2 = SystemClock.uptimeMillis() - uptimeMillis;
                    BackgroundThread.getHandler().post(new Runnable() { // from class: com.android.server.power.stats.BatteryStatsImpl.5
                        @Override // java.lang.Runnable
                        public void run() {
                            Parcel parcel;
                            synchronized (BatteryStatsImpl.this.mCheckinFile) {
                                long uptimeMillis3 = SystemClock.uptimeMillis();
                                FileOutputStream fileOutputStream = null;
                                try {
                                    fileOutputStream = BatteryStatsImpl.this.mCheckinFile.startWrite();
                                    fileOutputStream.write(obtain.marshall());
                                    fileOutputStream.flush();
                                    BatteryStatsImpl.this.mCheckinFile.finishWrite(fileOutputStream);
                                    EventLogTags.writeCommitSysConfigFile("batterystats-checkin", (uptimeMillis2 + SystemClock.uptimeMillis()) - uptimeMillis3);
                                    parcel = obtain;
                                } catch (IOException e) {
                                    Slog.w("BatteryStats", "Error writing checkin battery statistics", e);
                                    BatteryStatsImpl.this.mCheckinFile.failWrite(fileOutputStream);
                                    parcel = obtain;
                                }
                                parcel.recycle();
                            }
                        }
                    });
                }
                r8 = 0;
                z3 = true;
                resetAllStatsLocked(j2, j, 3);
                if (i3 > 0 && i2 > 0) {
                    this.mEstimatedBatteryCapacityMah = (int) ((i3 / 1000) / (i2 / 100.0d));
                }
                this.mDischargeStepTracker.init();
                z4 = true;
            } else {
                z3 = true;
                r8 = 0;
                z4 = false;
            }
            boolean z5 = z4;
            if (this.mCharging) {
                setChargingLocked(r8);
            }
            this.mOnBatteryInternal = z3;
            this.mOnBattery = z3;
            this.mLastDischargeStepLevel = i2;
            this.mMinDischargeStepLevel = i2;
            this.mDischargeStepTracker.clearTime();
            this.mDailyDischargeStepTracker.clearTime();
            this.mInitStepMode = this.mCurStepMode;
            this.mModStepMode = r8;
            pullPendingStateUpdatesLocked();
            if (z4) {
                this.mHistory.startRecordingHistory(j, j2, z4);
                initActiveHistoryEventsLocked(j, j2);
            }
            this.mBatteryPluggedIn = r8;
            AlarmInterface alarmInterface = this.mLongPlugInAlarmInterface;
            if (alarmInterface != null) {
                alarmInterface.cancel();
            }
            this.mHistory.recordBatteryState(j, j2, i2, this.mBatteryPluggedIn);
            this.mDischargeUnplugLevel = i2;
            this.mDischargeCurrentLevel = i2;
            if (Display.isOnState(i4)) {
                this.mDischargeScreenOnUnplugLevel = i2;
                this.mDischargeScreenDozeUnplugLevel = r8;
                this.mDischargeScreenOffUnplugLevel = r8;
            } else if (Display.isDozeState(i4)) {
                this.mDischargeScreenOnUnplugLevel = r8;
                this.mDischargeScreenDozeUnplugLevel = i2;
                this.mDischargeScreenOffUnplugLevel = r8;
            } else {
                this.mDischargeScreenOnUnplugLevel = r8;
                this.mDischargeScreenDozeUnplugLevel = r8;
                this.mDischargeScreenOffUnplugLevel = i2;
            }
            this.mDischargeAmountScreenOn = r8;
            this.mDischargeAmountScreenDoze = r8;
            this.mDischargeAmountScreenOff = r8;
            updateTimeBasesLocked(true, i4, j3, j4);
            z2 = z5;
        } else {
            this.mOnBatteryInternal = false;
            this.mOnBattery = false;
            pullPendingStateUpdatesLocked();
            this.mBatteryPluggedIn = true;
            this.mBatteryPluggedInRealTimeMs = j;
            this.mHistory.recordBatteryState(j, j2, i2, true);
            this.mDischargePlugLevel = i2;
            this.mDischargeCurrentLevel = i2;
            int i5 = this.mDischargeUnplugLevel;
            if (i2 < i5) {
                this.mLowDischargeAmountSinceCharge += (i5 - i2) - 1;
                this.mHighDischargeAmountSinceCharge += i5 - i2;
            }
            updateDischargeScreenLevelsLocked(i4, i4);
            updateTimeBasesLocked(false, i4, j3, j4);
            this.mChargeStepTracker.init();
            this.mLastChargeStepLevel = i2;
            this.mMaxChargeStepLevel = i2;
            this.mInitStepMode = this.mCurStepMode;
            this.mModStepMode = 0;
            scheduleNextResetWhilePluggedInCheck();
            z2 = false;
        }
        if ((!z2 && this.mLastWriteTimeMs + 60000 >= j) || this.mStatsFile == null || this.mHistory.isReadOnly()) {
            return;
        }
        writeAsyncLocked();
    }

    public final void scheduleSyncExternalStatsLocked(String str, int i) {
        ExternalStatsSync externalStatsSync = this.mExternalSync;
        if (externalStatsSync != null) {
            externalStatsSync.scheduleSync(str, i);
        }
    }

    @GuardedBy({"this"})
    public void setBatteryStateLocked(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, long j, long j2, long j3, long j4) {
        long j5;
        boolean z;
        boolean z2;
        int max = Math.max(0, i5);
        reportChangesToStatsLog(i, i3, i4);
        boolean isOnBattery = isOnBattery(i3, i);
        if (!this.mHaveBatteryLevel) {
            this.mHaveBatteryLevel = true;
            if (isOnBattery == this.mOnBattery) {
                this.mHistory.setPluggedInState(!isOnBattery);
            }
            this.mBatteryStatus = i;
            this.mBatteryLevel = i4;
            this.mBatteryChargeUah = i7;
            this.mHistory.setBatteryState(true, i, i4, i7);
            this.mLastDischargeStepLevel = i4;
            this.mLastChargeStepLevel = i4;
            this.mMinDischargeStepLevel = i4;
            this.mMaxChargeStepLevel = i4;
        } else if (this.mBatteryLevel != i4 || this.mOnBattery != isOnBattery) {
            if (i4 < 100 || !isOnBattery) {
                j5 = j4;
                z = false;
            } else {
                j5 = j4;
                z = true;
            }
            recordDailyStatsIfNeededLocked(z, j5);
        }
        int i9 = this.mBatteryStatus;
        if (isOnBattery) {
            this.mDischargeCurrentLevel = i4;
            if (!this.mHistory.isRecordingHistory()) {
                this.mHistory.startRecordingHistory(j2, j3, true);
            }
        } else if (i4 < 96 && i != 1 && !this.mHistory.isRecordingHistory()) {
            this.mHistory.startRecordingHistory(j2, j3, true);
        }
        if (this.mDischargePlugLevel < 0) {
            this.mDischargePlugLevel = i4;
        }
        if (isOnBattery != this.mOnBattery) {
            this.mBatteryLevel = i4;
            this.mBatteryStatus = i;
            this.mBatteryHealth = i2;
            this.mBatteryPlugType = i3;
            this.mBatteryTemperature = max;
            this.mBatteryVoltageMv = i6;
            this.mHistory.setBatteryState(i, i4, i2, i3, max, i6, i7);
            int i10 = this.mBatteryChargeUah;
            if (i7 < i10) {
                long j6 = i10 - i7;
                this.mDischargeCounter.addCountLocked(j6);
                this.mDischargeScreenOffCounter.addCountLocked(j6);
                if (Display.isDozeState(this.mScreenState)) {
                    this.mDischargeScreenDozeCounter.addCountLocked(j6);
                }
                int i11 = this.mDeviceIdleMode;
                if (i11 == 1) {
                    this.mDischargeLightDozeCounter.addCountLocked(j6);
                } else if (i11 == 2) {
                    this.mDischargeDeepDozeCounter.addCountLocked(j6);
                }
            }
            this.mBatteryChargeUah = i7;
            setOnBatteryLocked(j2, j3, isOnBattery, i9, i4, i7);
        } else {
            if (this.mBatteryLevel != i4) {
                this.mBatteryLevel = i4;
                this.mExternalSync.scheduleSyncDueToBatteryLevelChange(this.mConstants.BATTERY_LEVEL_COLLECTION_DELAY_MS);
                z2 = true;
            } else {
                z2 = false;
            }
            if (this.mBatteryStatus != i) {
                this.mBatteryStatus = i;
                z2 = true;
            }
            if (this.mBatteryHealth != i2) {
                this.mBatteryHealth = i2;
                z2 = true;
            }
            if (this.mBatteryPlugType != i3) {
                this.mBatteryPlugType = i3;
                z2 = true;
            }
            int i12 = this.mBatteryTemperature;
            if (max >= i12 + 10 || max <= i12 - 10) {
                this.mBatteryTemperature = max;
                z2 = true;
            }
            int i13 = this.mBatteryVoltageMv;
            if (i6 > i13 + 20 || i6 < i13 - 20) {
                this.mBatteryVoltageMv = i6;
                z2 = true;
            }
            int i14 = this.mBatteryChargeUah;
            if (i7 >= i14 + 10 || i7 <= i14 - 10) {
                if (i7 < i14) {
                    long j7 = i14 - i7;
                    this.mDischargeCounter.addCountLocked(j7);
                    this.mDischargeScreenOffCounter.addCountLocked(j7);
                    if (Display.isDozeState(this.mScreenState)) {
                        this.mDischargeScreenDozeCounter.addCountLocked(j7);
                    }
                    int i15 = this.mDeviceIdleMode;
                    if (i15 == 1) {
                        this.mDischargeLightDozeCounter.addCountLocked(j7);
                    } else if (i15 == 2) {
                        this.mDischargeDeepDozeCounter.addCountLocked(j7);
                    }
                }
                this.mBatteryChargeUah = i7;
                z2 = true;
            }
            long j8 = (this.mInitStepMode << 48) | (this.mModStepMode << 56) | ((i4 & 255) << 40);
            if (isOnBattery) {
                z2 |= setChargingLocked(false);
                int i16 = this.mLastDischargeStepLevel;
                if (i16 != i4 && this.mMinDischargeStepLevel > i4) {
                    this.mDischargeStepTracker.addLevelSteps(i16 - i4, j8, j2);
                    this.mDailyDischargeStepTracker.addLevelSteps(this.mLastDischargeStepLevel - i4, j8, j2);
                    this.mLastDischargeStepLevel = i4;
                    this.mMinDischargeStepLevel = i4;
                    this.mInitStepMode = this.mCurStepMode;
                    this.mModStepMode = 0;
                }
            } else {
                if (i4 >= 90) {
                    z2 |= setChargingLocked(true);
                } else if (!this.mCharging) {
                    int i17 = this.mLastChargeStepLevel;
                    if (i17 < i4) {
                        if (!this.mHandler.hasCallbacks(this.mDeferSetCharging)) {
                            this.mHandler.postDelayed(this.mDeferSetCharging, this.mConstants.BATTERY_CHARGED_DELAY_MS);
                        }
                    } else if (i17 > i4) {
                        this.mHandler.removeCallbacks(this.mDeferSetCharging);
                    }
                } else if (this.mLastChargeStepLevel > i4) {
                    z2 |= setChargingLocked(false);
                }
                int i18 = this.mLastChargeStepLevel;
                if (i18 != i4 && this.mMaxChargeStepLevel < i4) {
                    this.mChargeStepTracker.addLevelSteps(i4 - i18, j8, j2);
                    this.mDailyChargeStepTracker.addLevelSteps(i4 - this.mLastChargeStepLevel, j8, j2);
                    this.mMaxChargeStepLevel = i4;
                    this.mInitStepMode = this.mCurStepMode;
                    this.mModStepMode = 0;
                }
                this.mLastChargeStepLevel = i4;
            }
            if (z2) {
                this.mHistory.setBatteryState(this.mBatteryStatus, this.mBatteryLevel, this.mBatteryHealth, this.mBatteryPlugType, this.mBatteryTemperature, this.mBatteryVoltageMv, this.mBatteryChargeUah);
                this.mHistory.writeHistoryItem(j2, j3);
            }
        }
        if (!isOnBattery && (i == 5 || i == 1)) {
            this.mHistory.setHistoryRecordingEnabled(false);
        }
        this.mLastLearnedBatteryCapacityUah = i8;
        int i19 = this.mMinLearnedBatteryCapacityUah;
        if (i19 == -1) {
            this.mMinLearnedBatteryCapacityUah = i8;
        } else {
            this.mMinLearnedBatteryCapacityUah = Math.min(i19, i8);
        }
        this.mMaxLearnedBatteryCapacityUah = Math.max(this.mMaxLearnedBatteryCapacityUah, i8);
        this.mBatteryTimeToFullSeconds = j;
    }

    public final void reportChangesToStatsLog(int i, int i2, int i3) {
        if (this.mHaveBatteryLevel) {
            if (this.mBatteryStatus != i) {
                FrameworkStatsLog.write(31, i);
            }
            if (this.mBatteryPlugType != i2) {
                FrameworkStatsLog.write(32, i2);
            }
            if (this.mBatteryLevel != i3) {
                FrameworkStatsLog.write(30, i3);
            }
        }
    }

    public long getAwakeTimeBattery() {
        return getBatteryUptimeLocked(this.mClock.uptimeMillis());
    }

    public long getAwakeTimePlugged() {
        return (this.mClock.uptimeMillis() * 1000) - getAwakeTimeBattery();
    }

    public long computeUptime(long j, int i) {
        return this.mUptimeUs + (j - this.mUptimeStartUs);
    }

    public long computeRealtime(long j, int i) {
        return this.mRealtimeUs + (j - this.mRealtimeStartUs);
    }

    public long computeBatteryUptime(long j, int i) {
        return this.mOnBatteryTimeBase.computeUptime(j, i);
    }

    public long computeBatteryRealtime(long j, int i) {
        return this.mOnBatteryTimeBase.computeRealtime(j, i);
    }

    public long computeBatteryScreenOffUptime(long j, int i) {
        return this.mOnBatteryScreenOffTimeBase.computeUptime(j, i);
    }

    public long computeBatteryScreenOffRealtime(long j, int i) {
        return this.mOnBatteryScreenOffTimeBase.computeRealtime(j, i);
    }

    public long computeBatteryTimeRemaining(long j) {
        if (this.mOnBattery) {
            BatteryStats.LevelStepTracker levelStepTracker = this.mDischargeStepTracker;
            if (levelStepTracker.mNumStepDurations < 1) {
                return -1L;
            }
            long computeTimePerLevel = levelStepTracker.computeTimePerLevel();
            if (computeTimePerLevel <= 0) {
                return -1L;
            }
            return computeTimePerLevel * this.mBatteryLevel * 1000;
        }
        return -1L;
    }

    public BatteryStats.LevelStepTracker getDischargeLevelStepTracker() {
        return this.mDischargeStepTracker;
    }

    public BatteryStats.LevelStepTracker getDailyDischargeLevelStepTracker() {
        return this.mDailyDischargeStepTracker;
    }

    public long computeChargeTimeRemaining(long j) {
        long j2;
        if (this.mOnBattery) {
            return -1L;
        }
        long j3 = this.mBatteryTimeToFullSeconds;
        if (j3 >= 0) {
            j2 = 1000000;
        } else {
            BatteryStats.LevelStepTracker levelStepTracker = this.mChargeStepTracker;
            if (levelStepTracker.mNumStepDurations < 1) {
                return -1L;
            }
            long computeTimePerLevel = levelStepTracker.computeTimePerLevel();
            if (computeTimePerLevel <= 0) {
                return -1L;
            }
            j3 = computeTimePerLevel * (100 - this.mBatteryLevel);
            j2 = 1000;
        }
        return j3 * j2;
    }

    public CellularBatteryStats getCellularBatteryStats() {
        long elapsedRealtime = SystemClock.elapsedRealtime() * 1000;
        BatteryStats.ControllerActivityCounter modemControllerActivity = getModemControllerActivity();
        long countLocked = modemControllerActivity.getSleepTimeCounter().getCountLocked(0);
        long countLocked2 = modemControllerActivity.getIdleTimeCounter().getCountLocked(0);
        long countLocked3 = modemControllerActivity.getRxTimeCounter().getCountLocked(0);
        long countLocked4 = modemControllerActivity.getPowerCounter().getCountLocked(0);
        long countLocked5 = modemControllerActivity.getMonitoredRailChargeConsumedMaMs().getCountLocked(0);
        int i = BatteryStats.NUM_DATA_CONNECTION_TYPES;
        long[] jArr = new long[i];
        for (int i2 = 0; i2 < i; i2++) {
            jArr[i2] = getPhoneDataConnectionTime(i2, elapsedRealtime, 0) / 1000;
        }
        int numSignalStrengthLevels = CellSignalStrength.getNumSignalStrengthLevels();
        long[] jArr2 = new long[numSignalStrengthLevels];
        for (int i3 = 0; i3 < numSignalStrengthLevels; i3++) {
            jArr2[i3] = getPhoneSignalStrengthTime(i3, elapsedRealtime, 0) / 1000;
        }
        int min = Math.min(ModemActivityInfo.getNumTxPowerLevels(), modemControllerActivity.getTxTimeCounters().length);
        long[] jArr3 = new long[min];
        for (int i4 = 0; i4 < min; i4++) {
            jArr3[i4] = modemControllerActivity.getTxTimeCounters()[i4].getCountLocked(0);
        }
        return new CellularBatteryStats(computeBatteryRealtime(elapsedRealtime, 0) / 1000, getMobileRadioActiveTime(elapsedRealtime, 0) / 1000, getNetworkActivityPackets(1, 0), getNetworkActivityBytes(1, 0), getNetworkActivityPackets(0, 0), getNetworkActivityBytes(0, 0), countLocked, countLocked2, countLocked3, Long.valueOf(countLocked4), jArr, jArr2, jArr3, countLocked5);
    }

    public WifiBatteryStats getWifiBatteryStats() {
        long elapsedRealtime = SystemClock.elapsedRealtime() * 1000;
        BatteryStats.ControllerActivityCounter wifiControllerActivity = getWifiControllerActivity();
        long countLocked = wifiControllerActivity.getIdleTimeCounter().getCountLocked(0);
        long countLocked2 = wifiControllerActivity.getScanTimeCounter().getCountLocked(0);
        long countLocked3 = wifiControllerActivity.getRxTimeCounter().getCountLocked(0);
        long countLocked4 = wifiControllerActivity.getTxTimeCounters()[0].getCountLocked(0);
        long computeBatteryRealtime = (computeBatteryRealtime(SystemClock.elapsedRealtime() * 1000, 0) / 1000) - ((countLocked + countLocked3) + countLocked4);
        long countLocked5 = wifiControllerActivity.getPowerCounter().getCountLocked(0);
        long countLocked6 = wifiControllerActivity.getMonitoredRailChargeConsumedMaMs().getCountLocked(0);
        long j = 0;
        for (int i = 0; i < this.mUidStats.size(); i++) {
            j += this.mUidStats.valueAt(i).mWifiScanTimer.getCountLocked(0);
        }
        long[] jArr = new long[8];
        for (int i2 = 0; i2 < 8; i2++) {
            jArr[i2] = getWifiStateTime(i2, elapsedRealtime, 0) / 1000;
        }
        long[] jArr2 = new long[13];
        for (int i3 = 0; i3 < 13; i3++) {
            jArr2[i3] = getWifiSupplStateTime(i3, elapsedRealtime, 0) / 1000;
        }
        long[] jArr3 = new long[5];
        for (int i4 = 0; i4 < 5; i4++) {
            jArr3[i4] = getWifiSignalStrengthTime(i4, elapsedRealtime, 0) / 1000;
        }
        return new WifiBatteryStats(computeBatteryRealtime(elapsedRealtime, 0) / 1000, getWifiActiveTime(elapsedRealtime, 0) / 1000, getNetworkActivityPackets(3, 0), getNetworkActivityBytes(3, 0), getNetworkActivityPackets(2, 0), getNetworkActivityBytes(2, 0), computeBatteryRealtime, countLocked2, countLocked, countLocked3, countLocked4, countLocked5, j, jArr, jArr3, jArr2, countLocked6);
    }

    public GpsBatteryStats getGpsBatteryStats() {
        GpsBatteryStats gpsBatteryStats = new GpsBatteryStats();
        long elapsedRealtime = SystemClock.elapsedRealtime() * 1000;
        gpsBatteryStats.setLoggingDurationMs(computeBatteryRealtime(elapsedRealtime, 0) / 1000);
        gpsBatteryStats.setEnergyConsumedMaMs(getGpsBatteryDrainMaMs());
        int length = this.mGpsSignalQualityTimer.length;
        long[] jArr = new long[length];
        for (int i = 0; i < length; i++) {
            jArr[i] = getGpsSignalQualityTime(i, elapsedRealtime, 0) / 1000;
        }
        gpsBatteryStats.setTimeInGpsSignalQualityLevel(jArr);
        return gpsBatteryStats;
    }

    public BatteryStats.LevelStepTracker getChargeLevelStepTracker() {
        return this.mChargeStepTracker;
    }

    public BatteryStats.LevelStepTracker getDailyChargeLevelStepTracker() {
        return this.mDailyChargeStepTracker;
    }

    public ArrayList<BatteryStats.PackageChange> getDailyPackageChanges() {
        return this.mDailyPackageChanges;
    }

    public long getBatteryUptimeLocked(long j) {
        return this.mOnBatteryTimeBase.getUptime(j * 1000);
    }

    public long getBatteryUptime(long j) {
        return this.mOnBatteryTimeBase.getUptime(j);
    }

    public long getBatteryRealtime(long j) {
        return this.mOnBatteryTimeBase.getRealtime(j);
    }

    public int getDischargeStartLevel() {
        int dischargeStartLevelLocked;
        synchronized (this) {
            dischargeStartLevelLocked = getDischargeStartLevelLocked();
        }
        return dischargeStartLevelLocked;
    }

    public int getDischargeStartLevelLocked() {
        return this.mDischargeUnplugLevel;
    }

    public int getDischargeCurrentLevel() {
        int dischargeCurrentLevelLocked;
        synchronized (this) {
            dischargeCurrentLevelLocked = getDischargeCurrentLevelLocked();
        }
        return dischargeCurrentLevelLocked;
    }

    public int getDischargeCurrentLevelLocked() {
        return this.mDischargeCurrentLevel;
    }

    public int getLowDischargeAmountSinceCharge() {
        int i;
        int i2;
        int i3;
        synchronized (this) {
            i = this.mLowDischargeAmountSinceCharge;
            if (this.mOnBattery && (i2 = this.mDischargeCurrentLevel) < (i3 = this.mDischargeUnplugLevel)) {
                i += (i3 - i2) - 1;
            }
        }
        return i;
    }

    public int getHighDischargeAmountSinceCharge() {
        int i;
        int i2;
        int i3;
        synchronized (this) {
            i = this.mHighDischargeAmountSinceCharge;
            if (this.mOnBattery && (i2 = this.mDischargeCurrentLevel) < (i3 = this.mDischargeUnplugLevel)) {
                i += i3 - i2;
            }
        }
        return i;
    }

    public int getDischargeAmount(int i) {
        int dischargeStartLevel;
        if (i == 0) {
            dischargeStartLevel = getHighDischargeAmountSinceCharge();
        } else {
            dischargeStartLevel = getDischargeStartLevel() - getDischargeCurrentLevel();
        }
        if (dischargeStartLevel < 0) {
            return 0;
        }
        return dischargeStartLevel;
    }

    public int getDischargeAmountScreenOn() {
        int i;
        int i2;
        int i3;
        synchronized (this) {
            i = this.mDischargeAmountScreenOn;
            if (this.mOnBattery && Display.isOnState(this.mScreenState) && (i2 = this.mDischargeCurrentLevel) < (i3 = this.mDischargeScreenOnUnplugLevel)) {
                i += i3 - i2;
            }
        }
        return i;
    }

    public int getDischargeAmountScreenOnSinceCharge() {
        int i;
        int i2;
        int i3;
        synchronized (this) {
            i = this.mDischargeAmountScreenOnSinceCharge;
            if (this.mOnBattery && Display.isOnState(this.mScreenState) && (i2 = this.mDischargeCurrentLevel) < (i3 = this.mDischargeScreenOnUnplugLevel)) {
                i += i3 - i2;
            }
        }
        return i;
    }

    public int getDischargeAmountScreenOff() {
        int dischargeAmountScreenDoze;
        int i;
        int i2;
        synchronized (this) {
            int i3 = this.mDischargeAmountScreenOff;
            if (this.mOnBattery && Display.isOffState(this.mScreenState) && (i = this.mDischargeCurrentLevel) < (i2 = this.mDischargeScreenOffUnplugLevel)) {
                i3 += i2 - i;
            }
            dischargeAmountScreenDoze = i3 + getDischargeAmountScreenDoze();
        }
        return dischargeAmountScreenDoze;
    }

    public int getDischargeAmountScreenOffSinceCharge() {
        int dischargeAmountScreenDozeSinceCharge;
        int i;
        int i2;
        synchronized (this) {
            int i3 = this.mDischargeAmountScreenOffSinceCharge;
            if (this.mOnBattery && Display.isOffState(this.mScreenState) && (i = this.mDischargeCurrentLevel) < (i2 = this.mDischargeScreenOffUnplugLevel)) {
                i3 += i2 - i;
            }
            dischargeAmountScreenDozeSinceCharge = i3 + getDischargeAmountScreenDozeSinceCharge();
        }
        return dischargeAmountScreenDozeSinceCharge;
    }

    public int getDischargeAmountScreenDoze() {
        int i;
        int i2;
        int i3;
        synchronized (this) {
            i = this.mDischargeAmountScreenDoze;
            if (this.mOnBattery && Display.isDozeState(this.mScreenState) && (i2 = this.mDischargeCurrentLevel) < (i3 = this.mDischargeScreenDozeUnplugLevel)) {
                i += i3 - i2;
            }
        }
        return i;
    }

    public int getDischargeAmountScreenDozeSinceCharge() {
        int i;
        int i2;
        int i3;
        synchronized (this) {
            i = this.mDischargeAmountScreenDozeSinceCharge;
            if (this.mOnBattery && Display.isDozeState(this.mScreenState) && (i2 = this.mDischargeCurrentLevel) < (i3 = this.mDischargeScreenDozeUnplugLevel)) {
                i += i3 - i2;
            }
        }
        return i;
    }

    public long[] getSystemServiceTimeAtCpuSpeeds() {
        LongSamplingCounterArray longSamplingCounterArray = this.mBinderThreadCpuTimesUs;
        if (longSamplingCounterArray == null) {
            return null;
        }
        return longSamplingCounterArray.getCountsLocked(0);
    }

    public Uid getUidStatsLocked(int i) {
        return getUidStatsLocked(i, this.mClock.elapsedRealtime(), this.mClock.uptimeMillis());
    }

    public Uid getUidStatsLocked(int i, long j, long j2) {
        Uid uid = this.mUidStats.get(i);
        if (uid == null) {
            if (Process.isSdkSandboxUid(i)) {
                Log.wtf("BatteryStatsImpl", "Tracking an SDK Sandbox UID");
            }
            Uid uid2 = new Uid(this, i, j, j2);
            this.mUidStats.put(i, uid2);
            return uid2;
        }
        return uid;
    }

    public Uid getAvailableUidStatsLocked(int i) {
        return this.mUidStats.get(i);
    }

    @GuardedBy({"this"})
    public void onCleanupUserLocked(int i, long j) {
        this.mPendingRemovedUids.add(new UidToRemove(UserHandle.getUid(i, 0), UserHandle.getUid(i, 99999), j));
    }

    @GuardedBy({"this"})
    public void onUserRemovedLocked(int i) {
        ExternalStatsSync externalStatsSync = this.mExternalSync;
        if (externalStatsSync != null) {
            externalStatsSync.scheduleCleanupDueToRemovedUser(i);
        }
    }

    @GuardedBy({"this"})
    public void clearRemovedUserUidsLocked(int i) {
        int uid = UserHandle.getUid(i, 0);
        int uid2 = UserHandle.getUid(i, 99999);
        this.mUidStats.put(uid, null);
        this.mUidStats.put(uid2, null);
        int indexOfKey = this.mUidStats.indexOfKey(uid);
        int indexOfKey2 = this.mUidStats.indexOfKey(uid2);
        for (int i2 = indexOfKey; i2 <= indexOfKey2; i2++) {
            Uid valueAt = this.mUidStats.valueAt(i2);
            if (valueAt != null) {
                valueAt.detachFromTimeBase();
            }
        }
        this.mUidStats.removeAtRange(indexOfKey, (indexOfKey2 - indexOfKey) + 1);
        removeCpuStatsForUidRangeLocked(uid, uid2);
    }

    @GuardedBy({"this"})
    public void removeUidStatsLocked(int i, long j) {
        Uid uid = this.mUidStats.get(i);
        if (uid != null) {
            uid.detachFromTimeBase();
        }
        this.mUidStats.remove(i);
        this.mPendingRemovedUids.add(new UidToRemove(this, i, j));
    }

    @GuardedBy({"this"})
    public final void removeCpuStatsForUidRangeLocked(int i, int i2) {
        if (i == i2) {
            this.mCpuUidUserSysTimeReader.removeUid(i);
            this.mCpuUidFreqTimeReader.removeUid(i);
            if (this.mConstants.TRACK_CPU_ACTIVE_CLUSTER_TIME) {
                this.mCpuUidActiveTimeReader.removeUid(i);
                this.mCpuUidClusterTimeReader.removeUid(i);
            }
            KernelSingleUidTimeReader kernelSingleUidTimeReader = this.mKernelSingleUidTimeReader;
            if (kernelSingleUidTimeReader != null) {
                kernelSingleUidTimeReader.removeUid(i);
            }
            this.mNumUidsRemoved++;
        } else if (i < i2) {
            this.mCpuUidFreqTimeReader.removeUidsInRange(i, i2);
            this.mCpuUidUserSysTimeReader.removeUidsInRange(i, i2);
            if (this.mConstants.TRACK_CPU_ACTIVE_CLUSTER_TIME) {
                this.mCpuUidActiveTimeReader.removeUidsInRange(i, i2);
                this.mCpuUidClusterTimeReader.removeUidsInRange(i, i2);
            }
            KernelSingleUidTimeReader kernelSingleUidTimeReader2 = this.mKernelSingleUidTimeReader;
            if (kernelSingleUidTimeReader2 != null) {
                kernelSingleUidTimeReader2.removeUidsInRange(i, i2);
            }
            this.mNumUidsRemoved++;
        } else {
            Slog.w("BatteryStatsImpl", "End UID " + i2 + " is smaller than start UID " + i);
        }
    }

    public Uid.Proc getProcessStatsLocked(int i, String str, long j, long j2) {
        return getUidStatsLocked(mapUid(i), j, j2).getProcessStatsLocked(str);
    }

    public Uid.Pkg getPackageStatsLocked(int i, String str, long j, long j2) {
        return getUidStatsLocked(mapUid(i), j, j2).getPackageStatsLocked(str);
    }

    public Uid.Pkg.Serv getServiceStatsLocked(int i, String str, String str2, long j, long j2) {
        return getUidStatsLocked(mapUid(i), j, j2).getServiceStatsLocked(str, str2);
    }

    @GuardedBy({"this"})
    public void shutdownLocked() {
        this.mHistory.recordShutdownEvent(this.mClock.elapsedRealtime(), this.mClock.uptimeMillis(), this.mClock.currentTimeMillis());
        writeSyncLocked();
        this.mShuttingDown = true;
    }

    @GuardedBy({"this"})
    public boolean isProcessStateDataAvailable() {
        return trackPerProcStateCpuTimes();
    }

    @GuardedBy({"this"})
    public final boolean trackPerProcStateCpuTimes() {
        return this.mCpuUidFreqTimeReader.isFastCpuTimesReader();
    }

    @GuardedBy({"this"})
    public boolean isUsageHistoryEnabled() {
        return this.mConstants.RECORD_USAGE_HISTORY;
    }

    @GuardedBy({"this"})
    public void systemServicesReady(Context context) {
        this.mConstants.startObserving(context.getContentResolver());
        registerUsbStateReceiver(context);
    }

    @GuardedBy({"this"})
    public void initEnergyConsumerStatsLocked(boolean[] zArr, String[] strArr) {
        boolean z;
        int length = this.mPerDisplayBatteryStats.length;
        for (int i = 0; i < length; i++) {
            DisplayBatteryStats displayBatteryStats = this.mPerDisplayBatteryStats[i];
            displayBatteryStats.screenStateAtLastEnergyMeasurement = displayBatteryStats.screenState;
        }
        if (zArr != null) {
            EnergyConsumerStats.Config config = new EnergyConsumerStats.Config(zArr, strArr, SUPPORTED_PER_PROCESS_STATE_STANDARD_ENERGY_BUCKETS, getBatteryConsumerProcessStateNames());
            EnergyConsumerStats.Config config2 = this.mEnergyConsumerStatsConfig;
            z = config2 != null ? config2.isCompatible(config) : true;
            this.mEnergyConsumerStatsConfig = config;
            this.mGlobalEnergyConsumerStats = new EnergyConsumerStats(config);
            if (zArr[5]) {
                this.mBluetoothPowerCalculator = new BluetoothPowerCalculator(this.mPowerProfile);
            }
            if (zArr[3]) {
                this.mCpuPowerCalculator = new CpuPowerCalculator(this.mPowerProfile);
            }
            if (zArr[7]) {
                this.mMobileRadioPowerCalculator = new MobileRadioPowerCalculator(this.mPowerProfile);
            }
            if (zArr[4]) {
                this.mWifiPowerCalculator = new WifiPowerCalculator(this.mPowerProfile);
            }
        } else {
            boolean z2 = this.mEnergyConsumerStatsConfig == null;
            this.mEnergyConsumerStatsConfig = null;
            this.mGlobalEnergyConsumerStats = null;
            z = z2;
        }
        if (z) {
            return;
        }
        resetAllStatsLocked(SystemClock.uptimeMillis(), SystemClock.elapsedRealtime(), 4);
    }

    @GuardedBy({"this"})
    public final boolean isMobileRadioEnergyConsumerSupportedLocked() {
        EnergyConsumerStats energyConsumerStats = this.mGlobalEnergyConsumerStats;
        if (energyConsumerStats == null) {
            return false;
        }
        return energyConsumerStats.isStandardBucketSupported(7);
    }

    public static String[] getBatteryConsumerProcessStateNames() {
        String[] strArr = new String[5];
        for (int i = 0; i < 5; i++) {
            strArr[i] = BatteryConsumer.processStateToString(i);
        }
        return strArr;
    }

    @GuardedBy({"this"})
    public int getBatteryVoltageMvLocked() {
        return this.mBatteryVoltageMv;
    }

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public final class Constants extends ContentObserver {
        public int BATTERY_CHARGED_DELAY_MS;
        public long BATTERY_LEVEL_COLLECTION_DELAY_MS;
        public long EXTERNAL_STATS_COLLECTION_RATE_LIMIT_MS;
        public long KERNEL_UID_READERS_THROTTLE_TIME;
        public int MAX_HISTORY_BUFFER;
        public int MAX_HISTORY_FILES;
        public int PER_UID_MODEM_MODEL;
        public boolean PHONE_ON_EXTERNAL_STATS_COLLECTION;
        public long PROC_STATE_CHANGE_COLLECTION_DELAY_MS;
        public boolean RECORD_USAGE_HISTORY;
        public int RESET_WHILE_PLUGGED_IN_MINIMUM_DURATION_HOURS;
        public boolean TRACK_CPU_ACTIVE_CLUSTER_TIME;
        public long UID_REMOVE_DELAY_MS;
        public final KeyValueListParser mParser;
        public ContentResolver mResolver;

        public String getPerUidModemModelName(int i) {
            if (i != 1) {
                if (i != 2) {
                    Slog.w("BatteryStatsImpl", "Unexpected per uid modem model (" + i + ")");
                    return "unknown_" + i;
                }
                return "modem_activity_info_rx_tx";
            }
            return "mobile_radio_active_time";
        }

        public int getPerUidModemModel(String str) {
            str.hashCode();
            if (str.equals("modem_activity_info_rx_tx")) {
                return 2;
            }
            if (str.equals("mobile_radio_active_time")) {
                return 1;
            }
            Slog.w("BatteryStatsImpl", "Unexpected per uid modem model name (" + str + ")");
            return 2;
        }

        public Constants(Handler handler) {
            super(handler);
            this.TRACK_CPU_ACTIVE_CLUSTER_TIME = true;
            this.UID_REMOVE_DELAY_MS = BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS;
            this.EXTERNAL_STATS_COLLECTION_RATE_LIMIT_MS = 600000L;
            this.BATTERY_LEVEL_COLLECTION_DELAY_MS = BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS;
            this.PROC_STATE_CHANGE_COLLECTION_DELAY_MS = 60000L;
            this.BATTERY_CHARGED_DELAY_MS = 900000;
            this.RECORD_USAGE_HISTORY = false;
            this.PER_UID_MODEM_MODEL = 2;
            this.PHONE_ON_EXTERNAL_STATS_COLLECTION = true;
            this.RESET_WHILE_PLUGGED_IN_MINIMUM_DURATION_HOURS = 47;
            this.mParser = new KeyValueListParser(',');
            if (ActivityManager.isLowRamDeviceStatic()) {
                this.MAX_HISTORY_FILES = 64;
                this.MAX_HISTORY_BUFFER = 65536;
                return;
            }
            this.MAX_HISTORY_FILES = 32;
            this.MAX_HISTORY_BUFFER = IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES;
        }

        public void startObserving(ContentResolver contentResolver) {
            this.mResolver = contentResolver;
            contentResolver.registerContentObserver(Settings.Global.getUriFor("battery_stats_constants"), false, this);
            this.mResolver.registerContentObserver(Settings.Global.getUriFor("battery_charging_state_update_delay"), false, this);
            updateConstants();
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            if (uri.equals(Settings.Global.getUriFor("battery_charging_state_update_delay"))) {
                synchronized (BatteryStatsImpl.this) {
                    updateBatteryChargedDelayMsLocked();
                }
                return;
            }
            updateConstants();
        }

        public final void updateConstants() {
            synchronized (BatteryStatsImpl.this) {
                try {
                    this.mParser.setString(Settings.Global.getString(this.mResolver, "battery_stats_constants"));
                } catch (IllegalArgumentException e) {
                    Slog.e("BatteryStatsImpl", "Bad batterystats settings", e);
                }
                this.TRACK_CPU_ACTIVE_CLUSTER_TIME = this.mParser.getBoolean("track_cpu_active_cluster_time", true);
                updateKernelUidReadersThrottleTime(this.KERNEL_UID_READERS_THROTTLE_TIME, this.mParser.getLong("kernel_uid_readers_throttle_time", 1000L));
                updateUidRemoveDelay(this.mParser.getLong("uid_remove_delay_ms", (long) BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS));
                this.EXTERNAL_STATS_COLLECTION_RATE_LIMIT_MS = this.mParser.getLong("external_stats_collection_rate_limit_ms", 600000L);
                this.BATTERY_LEVEL_COLLECTION_DELAY_MS = this.mParser.getLong("battery_level_collection_delay_ms", (long) BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS);
                this.PROC_STATE_CHANGE_COLLECTION_DELAY_MS = this.mParser.getLong("procstate_change_collection_delay_ms", 60000L);
                int i = 64;
                this.MAX_HISTORY_FILES = this.mParser.getInt("max_history_files", ActivityManager.isLowRamDeviceStatic() ? 64 : 32);
                KeyValueListParser keyValueListParser = this.mParser;
                if (!ActivityManager.isLowRamDeviceStatic()) {
                    i = 128;
                }
                this.MAX_HISTORY_BUFFER = keyValueListParser.getInt("max_history_buffer_kb", i) * 1024;
                this.RECORD_USAGE_HISTORY = this.mParser.getBoolean("record_usage_history", false);
                this.PER_UID_MODEM_MODEL = getPerUidModemModel(this.mParser.getString("per_uid_modem_power_model", ""));
                this.PHONE_ON_EXTERNAL_STATS_COLLECTION = this.mParser.getBoolean("phone_on_external_stats_collection", true);
                this.RESET_WHILE_PLUGGED_IN_MINIMUM_DURATION_HOURS = this.mParser.getInt("reset_while_plugged_in_minimum_duration_hours", 47);
                updateBatteryChargedDelayMsLocked();
                onChange();
            }
        }

        @VisibleForTesting
        public void onChange() {
            BatteryStatsImpl.this.mHistory.setMaxHistoryFiles(this.MAX_HISTORY_FILES);
            BatteryStatsImpl.this.mHistory.setMaxHistoryBufferSize(this.MAX_HISTORY_BUFFER);
        }

        public final void updateBatteryChargedDelayMsLocked() {
            int i = Settings.Global.getInt(this.mResolver, "battery_charging_state_update_delay", -1);
            if (i < 0) {
                i = this.mParser.getInt("battery_charged_delay_ms", 900000);
            }
            this.BATTERY_CHARGED_DELAY_MS = i;
            BatteryStatsImpl batteryStatsImpl = BatteryStatsImpl.this;
            if (batteryStatsImpl.mHandler.hasCallbacks(batteryStatsImpl.mDeferSetCharging)) {
                BatteryStatsImpl batteryStatsImpl2 = BatteryStatsImpl.this;
                batteryStatsImpl2.mHandler.removeCallbacks(batteryStatsImpl2.mDeferSetCharging);
                BatteryStatsImpl batteryStatsImpl3 = BatteryStatsImpl.this;
                batteryStatsImpl3.mHandler.postDelayed(batteryStatsImpl3.mDeferSetCharging, this.BATTERY_CHARGED_DELAY_MS);
            }
        }

        public final void updateKernelUidReadersThrottleTime(long j, long j2) {
            this.KERNEL_UID_READERS_THROTTLE_TIME = j2;
            if (j != j2) {
                BatteryStatsImpl.this.mCpuUidUserSysTimeReader.setThrottle(j2);
                BatteryStatsImpl.this.mCpuUidFreqTimeReader.setThrottle(this.KERNEL_UID_READERS_THROTTLE_TIME);
                BatteryStatsImpl.this.mCpuUidActiveTimeReader.setThrottle(this.KERNEL_UID_READERS_THROTTLE_TIME);
                BatteryStatsImpl.this.mCpuUidClusterTimeReader.setThrottle(this.KERNEL_UID_READERS_THROTTLE_TIME);
            }
        }

        @GuardedBy({"BatteryStatsImpl.this"})
        public final void updateUidRemoveDelay(long j) {
            this.UID_REMOVE_DELAY_MS = j;
            BatteryStatsImpl.this.clearPendingRemovedUidsLocked();
        }

        public void dumpLocked(PrintWriter printWriter) {
            printWriter.print("track_cpu_active_cluster_time");
            printWriter.print("=");
            printWriter.println(this.TRACK_CPU_ACTIVE_CLUSTER_TIME);
            printWriter.print("kernel_uid_readers_throttle_time");
            printWriter.print("=");
            printWriter.println(this.KERNEL_UID_READERS_THROTTLE_TIME);
            printWriter.print("external_stats_collection_rate_limit_ms");
            printWriter.print("=");
            printWriter.println(this.EXTERNAL_STATS_COLLECTION_RATE_LIMIT_MS);
            printWriter.print("battery_level_collection_delay_ms");
            printWriter.print("=");
            printWriter.println(this.BATTERY_LEVEL_COLLECTION_DELAY_MS);
            printWriter.print("procstate_change_collection_delay_ms");
            printWriter.print("=");
            printWriter.println(this.PROC_STATE_CHANGE_COLLECTION_DELAY_MS);
            printWriter.print("max_history_files");
            printWriter.print("=");
            printWriter.println(this.MAX_HISTORY_FILES);
            printWriter.print("max_history_buffer_kb");
            printWriter.print("=");
            printWriter.println(this.MAX_HISTORY_BUFFER / 1024);
            printWriter.print("battery_charged_delay_ms");
            printWriter.print("=");
            printWriter.println(this.BATTERY_CHARGED_DELAY_MS);
            printWriter.print("record_usage_history");
            printWriter.print("=");
            printWriter.println(this.RECORD_USAGE_HISTORY);
            printWriter.print("per_uid_modem_power_model");
            printWriter.print("=");
            printWriter.println(getPerUidModemModelName(this.PER_UID_MODEM_MODEL));
            printWriter.print("phone_on_external_stats_collection");
            printWriter.print("=");
            printWriter.println(this.PHONE_ON_EXTERNAL_STATS_COLLECTION);
            printWriter.print("reset_while_plugged_in_minimum_duration_hours");
            printWriter.print("=");
            printWriter.println(this.RESET_WHILE_PLUGGED_IN_MINIMUM_DURATION_HOURS);
        }
    }

    public long getExternalStatsCollectionRateLimitMs() {
        long j;
        synchronized (this) {
            j = this.mConstants.EXTERNAL_STATS_COLLECTION_RATE_LIMIT_MS;
        }
        return j;
    }

    @GuardedBy({"this"})
    public void dumpConstantsLocked(PrintWriter printWriter) {
        PrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "    ");
        indentingPrintWriter.println("BatteryStats constants:");
        indentingPrintWriter.increaseIndent();
        this.mConstants.dumpLocked(indentingPrintWriter);
        indentingPrintWriter.decreaseIndent();
    }

    @GuardedBy({"this"})
    public void dumpCpuStatsLocked(PrintWriter printWriter) {
        long j;
        int size = this.mUidStats.size();
        printWriter.println("Per UID CPU user & system time in ms:");
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            int keyAt = this.mUidStats.keyAt(i2);
            Uid uid = this.mUidStats.get(keyAt);
            printWriter.print("  ");
            printWriter.print(keyAt);
            printWriter.print(": ");
            printWriter.print(uid.getUserCpuTimeUs(0) / 1000);
            printWriter.print(" ");
            printWriter.println(uid.getSystemCpuTimeUs(0) / 1000);
        }
        printWriter.println("Per UID CPU active time in ms:");
        int i3 = 0;
        while (true) {
            j = 0;
            if (i3 >= size) {
                break;
            }
            int keyAt2 = this.mUidStats.keyAt(i3);
            Uid uid2 = this.mUidStats.get(keyAt2);
            if (uid2.getCpuActiveTime() > 0) {
                printWriter.print("  ");
                printWriter.print(keyAt2);
                printWriter.print(": ");
                printWriter.println(uid2.getCpuActiveTime());
            }
            i3++;
        }
        printWriter.println("Per UID CPU cluster time in ms:");
        for (int i4 = 0; i4 < size; i4++) {
            int keyAt3 = this.mUidStats.keyAt(i4);
            long[] cpuClusterTimes = this.mUidStats.get(keyAt3).getCpuClusterTimes();
            if (cpuClusterTimes != null) {
                printWriter.print("  ");
                printWriter.print(keyAt3);
                printWriter.print(": ");
                printWriter.println(Arrays.toString(cpuClusterTimes));
            }
        }
        printWriter.println("Per UID CPU frequency time in ms:");
        for (int i5 = 0; i5 < size; i5++) {
            int keyAt4 = this.mUidStats.keyAt(i5);
            long[] cpuFreqTimes = this.mUidStats.get(keyAt4).getCpuFreqTimes(0);
            if (cpuFreqTimes != null) {
                printWriter.print("  ");
                printWriter.print(keyAt4);
                printWriter.print(": ");
                printWriter.println(Arrays.toString(cpuFreqTimes));
            }
        }
        updateSystemServiceCallStats();
        if (this.mBinderThreadCpuTimesUs != null) {
            printWriter.println("Per UID System server binder time in ms:");
            long[] systemServiceTimeAtCpuSpeeds = getSystemServiceTimeAtCpuSpeeds();
            while (i < size) {
                int keyAt5 = this.mUidStats.keyAt(i);
                double proportionalSystemServiceUsage = this.mUidStats.get(keyAt5).getProportionalSystemServiceUsage();
                for (int length = systemServiceTimeAtCpuSpeeds.length - 1; length >= 0; length--) {
                    j = (long) (j + (systemServiceTimeAtCpuSpeeds[length] * proportionalSystemServiceUsage));
                }
                printWriter.print("  ");
                printWriter.print(keyAt5);
                printWriter.print(": ");
                printWriter.println(j / 1000);
                i++;
                j = 0;
            }
        }
    }

    @GuardedBy({"this"})
    public final void dumpCpuPowerBracketsLocked(PrintWriter printWriter) {
        printWriter.println("CPU power brackets; cluster/freq in MHz(avg current in mA):");
        int cpuPowerBracketCount = this.mPowerProfile.getCpuPowerBracketCount();
        for (int i = 0; i < cpuPowerBracketCount; i++) {
            printWriter.print("    ");
            printWriter.print(i);
            printWriter.print(": ");
            printWriter.println(this.mPowerProfile.getCpuPowerBracketDescription(i));
        }
    }

    @GuardedBy({"this"})
    public void dumpEnergyConsumerStatsLocked(PrintWriter printWriter) {
        printWriter.printf("On-battery energy consumer stats (microcoulombs) \n", new Object[0]);
        EnergyConsumerStats energyConsumerStats = this.mGlobalEnergyConsumerStats;
        if (energyConsumerStats == null) {
            printWriter.printf("    Not supported on this device.\n", new Object[0]);
            return;
        }
        dumpEnergyConsumerStatsLocked(printWriter, "global usage", energyConsumerStats);
        int size = this.mUidStats.size();
        for (int i = 0; i < size; i++) {
            Uid uid = this.mUidStats.get(this.mUidStats.keyAt(i));
            dumpEnergyConsumerStatsLocked(printWriter, "uid " + uid.mUid, uid.mUidEnergyConsumerStats);
        }
    }

    @GuardedBy({"this"})
    public final void dumpEnergyConsumerStatsLocked(PrintWriter printWriter, String str, EnergyConsumerStats energyConsumerStats) {
        if (energyConsumerStats == null) {
            return;
        }
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "    ");
        indentingPrintWriter.increaseIndent();
        indentingPrintWriter.printf("%s:\n", new Object[]{str});
        indentingPrintWriter.increaseIndent();
        energyConsumerStats.dump(indentingPrintWriter);
        indentingPrintWriter.decreaseIndent();
    }

    @GuardedBy({"this"})
    public void dumpPowerProfileLocked(PrintWriter printWriter) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "    ");
        indentingPrintWriter.printf("Power Profile: \n", new Object[0]);
        indentingPrintWriter.increaseIndent();
        this.mPowerProfile.dump(indentingPrintWriter);
        indentingPrintWriter.decreaseIndent();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$4() {
        synchronized (this) {
            writeSyncLocked();
        }
    }

    @GuardedBy({"this"})
    public void writeAsyncLocked() {
        BackgroundThread.getHandler().removeCallbacks(this.mWriteAsyncRunnable);
        BackgroundThread.getHandler().post(this.mWriteAsyncRunnable);
    }

    @GuardedBy({"this"})
    public void writeSyncLocked() {
        BackgroundThread.getHandler().removeCallbacks(this.mWriteAsyncRunnable);
        writeStatsLocked();
        writeHistoryLocked();
    }

    @GuardedBy({"this"})
    public final void writeStatsLocked() {
        if (this.mStatsFile == null) {
            Slog.w("BatteryStatsImpl", "writeStatsLocked: no file associated with this instance");
        } else if (this.mShuttingDown) {
        } else {
            Parcel obtain = Parcel.obtain();
            try {
                SystemClock.uptimeMillis();
                writeSummaryToParcel(obtain, false);
                this.mLastWriteTimeMs = this.mClock.elapsedRealtime();
                writeParcelToFileLocked(obtain, this.mStatsFile);
            } finally {
                obtain.recycle();
            }
        }
    }

    public final void writeHistoryLocked() {
        if (this.mShuttingDown) {
            return;
        }
        this.mHistory.writeHistory();
    }

    public final void writeParcelToFileLocked(Parcel parcel, AtomicFile atomicFile) {
        this.mWriteLock.lock();
        FileOutputStream fileOutputStream = null;
        try {
            try {
                long uptimeMillis = SystemClock.uptimeMillis();
                fileOutputStream = atomicFile.startWrite();
                fileOutputStream.write(parcel.marshall());
                fileOutputStream.flush();
                atomicFile.finishWrite(fileOutputStream);
                EventLogTags.writeCommitSysConfigFile("batterystats", SystemClock.uptimeMillis() - uptimeMillis);
            } catch (IOException e) {
                Slog.w("BatteryStatsImpl", "Error writing battery statistics", e);
                atomicFile.failWrite(fileOutputStream);
            }
        } finally {
            this.mWriteLock.unlock();
        }
    }

    @GuardedBy({"this"})
    public void readLocked() {
        if (this.mDailyFile != null) {
            readDailyStatsLocked();
        }
        if (this.mStatsFile == null) {
            Slog.w("BatteryStatsImpl", "readLocked: no file associated with this instance");
            return;
        }
        this.mUidStats.clear();
        Parcel obtain = Parcel.obtain();
        try {
            try {
                SystemClock.uptimeMillis();
                if (this.mStatsFile.exists()) {
                    byte[] readFully = this.mStatsFile.readFully();
                    obtain.unmarshall(readFully, 0, readFully.length);
                    obtain.setDataPosition(0);
                    readSummaryFromParcel(obtain);
                }
            } catch (Exception e) {
                Slog.e("BatteryStatsImpl", "Error reading battery statistics", e);
                resetAllStatsLocked(SystemClock.uptimeMillis(), SystemClock.elapsedRealtime(), 1);
            }
            if (!this.mHistory.readSummary()) {
                resetAllStatsLocked(SystemClock.uptimeMillis(), SystemClock.elapsedRealtime(), 1);
            }
            this.mEndPlatformVersion = Build.ID;
            this.mHistory.continueRecordingHistory();
            recordDailyStatsIfNeededLocked(false, this.mClock.currentTimeMillis());
        } finally {
            obtain.recycle();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @GuardedBy({"this"})
    public void readSummaryFromParcel(Parcel parcel) throws ParcelFormatException {
        long j;
        long j2;
        int readInt = parcel.readInt();
        if (readInt != 212) {
            Slog.w("BatteryStats", "readFromParcel: version got " + readInt + ", expected " + FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__ROLE_HOLDER_UPDATER_UPDATE_FAILED + "; erasing old stats");
            return;
        }
        this.mHistory.readSummaryFromParcel(parcel);
        this.mStartCount = parcel.readInt();
        this.mUptimeUs = parcel.readLong();
        this.mRealtimeUs = parcel.readLong();
        this.mStartClockTimeMs = parcel.readLong();
        this.mStartPlatformVersion = parcel.readString();
        this.mEndPlatformVersion = parcel.readString();
        this.mOnBatteryTimeBase.readSummaryFromParcel(parcel);
        this.mOnBatteryScreenOffTimeBase.readSummaryFromParcel(parcel);
        this.mDischargeUnplugLevel = parcel.readInt();
        this.mDischargePlugLevel = parcel.readInt();
        this.mDischargeCurrentLevel = parcel.readInt();
        this.mBatteryLevel = parcel.readInt();
        this.mEstimatedBatteryCapacityMah = parcel.readInt();
        this.mLastLearnedBatteryCapacityUah = parcel.readInt();
        this.mMinLearnedBatteryCapacityUah = parcel.readInt();
        this.mMaxLearnedBatteryCapacityUah = parcel.readInt();
        this.mLowDischargeAmountSinceCharge = parcel.readInt();
        this.mHighDischargeAmountSinceCharge = parcel.readInt();
        this.mDischargeAmountScreenOnSinceCharge = parcel.readInt();
        this.mDischargeAmountScreenOffSinceCharge = parcel.readInt();
        this.mDischargeAmountScreenDozeSinceCharge = parcel.readInt();
        this.mDischargeStepTracker.readFromParcel(parcel);
        this.mChargeStepTracker.readFromParcel(parcel);
        this.mDailyDischargeStepTracker.readFromParcel(parcel);
        this.mDailyChargeStepTracker.readFromParcel(parcel);
        this.mDischargeCounter.readSummaryFromParcelLocked(parcel);
        this.mDischargeScreenOffCounter.readSummaryFromParcelLocked(parcel);
        this.mDischargeScreenDozeCounter.readSummaryFromParcelLocked(parcel);
        this.mDischargeLightDozeCounter.readSummaryFromParcelLocked(parcel);
        this.mDischargeDeepDozeCounter.readSummaryFromParcelLocked(parcel);
        int readInt2 = parcel.readInt();
        boolean z = 0;
        if (readInt2 > 0) {
            this.mDailyPackageChanges = new ArrayList<>(readInt2);
            while (readInt2 > 0) {
                readInt2--;
                BatteryStats.PackageChange packageChange = new BatteryStats.PackageChange();
                packageChange.mPackageName = parcel.readString();
                packageChange.mUpdate = parcel.readInt() != 0;
                packageChange.mVersionCode = parcel.readLong();
                this.mDailyPackageChanges.add(packageChange);
            }
        } else {
            this.mDailyPackageChanges = null;
        }
        this.mDailyStartTimeMs = parcel.readLong();
        this.mNextMinDailyDeadlineMs = parcel.readLong();
        this.mNextMaxDailyDeadlineMs = parcel.readLong();
        this.mBatteryTimeToFullSeconds = parcel.readLong();
        EnergyConsumerStats.Config createFromParcel = EnergyConsumerStats.Config.createFromParcel(parcel);
        EnergyConsumerStats createAndReadSummaryFromParcel = EnergyConsumerStats.createAndReadSummaryFromParcel(this.mEnergyConsumerStatsConfig, parcel);
        if (createFromParcel != null && Arrays.equals(createFromParcel.getStateNames(), getBatteryConsumerProcessStateNames())) {
            this.mEnergyConsumerStatsConfig = createFromParcel;
            this.mGlobalEnergyConsumerStats = createAndReadSummaryFromParcel;
        }
        this.mStartCount++;
        this.mScreenState = 0;
        this.mScreenOnTimer.readSummaryFromParcelLocked(parcel);
        this.mScreenDozeTimer.readSummaryFromParcelLocked(parcel);
        for (int i = 0; i < 5; i++) {
            this.mScreenBrightnessTimer[i].readSummaryFromParcelLocked(parcel);
        }
        int readInt3 = parcel.readInt();
        for (int i2 = 0; i2 < readInt3; i2++) {
            this.mPerDisplayBatteryStats[i2].readSummaryFromParcel(parcel);
        }
        this.mInteractive = false;
        this.mInteractiveTimer.readSummaryFromParcelLocked(parcel);
        this.mPhoneOn = false;
        this.mPowerSaveModeEnabledTimer.readSummaryFromParcelLocked(parcel);
        this.mLongestLightIdleTimeMs = parcel.readLong();
        this.mLongestFullIdleTimeMs = parcel.readLong();
        this.mDeviceIdleModeLightTimer.readSummaryFromParcelLocked(parcel);
        this.mDeviceIdleModeFullTimer.readSummaryFromParcelLocked(parcel);
        this.mDeviceLightIdlingTimer.readSummaryFromParcelLocked(parcel);
        this.mDeviceIdlingTimer.readSummaryFromParcelLocked(parcel);
        this.mPhoneOnTimer.readSummaryFromParcelLocked(parcel);
        for (int i3 = 0; i3 < CellSignalStrength.getNumSignalStrengthLevels(); i3++) {
            this.mPhoneSignalStrengthsTimer[i3].readSummaryFromParcelLocked(parcel);
        }
        this.mPhoneSignalScanningTimer.readSummaryFromParcelLocked(parcel);
        for (int i4 = 0; i4 < BatteryStats.NUM_DATA_CONNECTION_TYPES; i4++) {
            this.mPhoneDataConnectionsTimer[i4].readSummaryFromParcelLocked(parcel);
        }
        for (int i5 = 0; i5 < 10; i5++) {
            this.mNetworkByteActivityCounters[i5].readSummaryFromParcelLocked(parcel);
            this.mNetworkPacketActivityCounters[i5].readSummaryFromParcelLocked(parcel);
        }
        int readInt4 = parcel.readInt();
        for (int i6 = 0; i6 < readInt4; i6++) {
            if (parcel.readInt() != 0) {
                getRatBatteryStatsLocked(i6).readSummaryFromParcel(parcel);
            }
        }
        this.mMobileRadioPowerState = 1;
        this.mMobileRadioActiveTimer.readSummaryFromParcelLocked(parcel);
        this.mMobileRadioActivePerAppTimer.readSummaryFromParcelLocked(parcel);
        this.mMobileRadioActiveAdjustedTime.readSummaryFromParcelLocked(parcel);
        this.mMobileRadioActiveUnknownTime.readSummaryFromParcelLocked(parcel);
        this.mMobileRadioActiveUnknownCount.readSummaryFromParcelLocked(parcel);
        this.mWifiMulticastWakelockTimer.readSummaryFromParcelLocked(parcel);
        this.mWifiRadioPowerState = 1;
        this.mWifiOn = false;
        this.mWifiOnTimer.readSummaryFromParcelLocked(parcel);
        this.mGlobalWifiRunning = false;
        this.mGlobalWifiRunningTimer.readSummaryFromParcelLocked(parcel);
        for (int i7 = 0; i7 < 8; i7++) {
            this.mWifiStateTimer[i7].readSummaryFromParcelLocked(parcel);
        }
        for (int i8 = 0; i8 < 13; i8++) {
            this.mWifiSupplStateTimer[i8].readSummaryFromParcelLocked(parcel);
        }
        for (int i9 = 0; i9 < 5; i9++) {
            this.mWifiSignalStrengthsTimer[i9].readSummaryFromParcelLocked(parcel);
        }
        this.mWifiActiveTimer.readSummaryFromParcelLocked(parcel);
        this.mWifiActivity.readSummaryFromParcel(parcel);
        int i10 = 0;
        while (true) {
            StopwatchTimer[] stopwatchTimerArr = this.mGpsSignalQualityTimer;
            if (i10 >= stopwatchTimerArr.length) {
                break;
            }
            stopwatchTimerArr[i10].readSummaryFromParcelLocked(parcel);
            i10++;
        }
        this.mBluetoothActivity.readSummaryFromParcel(parcel);
        this.mModemActivity.readSummaryFromParcel(parcel);
        this.mHasWifiReporting = parcel.readInt() != 0;
        this.mHasBluetoothReporting = parcel.readInt() != 0;
        this.mHasModemReporting = parcel.readInt() != 0;
        this.mNumConnectivityChange = parcel.readInt();
        this.mFlashlightOnNesting = 0;
        this.mFlashlightOnTimer.readSummaryFromParcelLocked(parcel);
        this.mCameraOnNesting = 0;
        this.mCameraOnTimer.readSummaryFromParcelLocked(parcel);
        this.mBluetoothScanNesting = 0;
        this.mBluetoothScanTimer.readSummaryFromParcelLocked(parcel);
        int readInt5 = parcel.readInt();
        if (readInt5 > 10000) {
            throw new ParcelFormatException("File corrupt: too many rpm stats " + readInt5);
        }
        for (int i11 = 0; i11 < readInt5; i11++) {
            if (parcel.readInt() != 0) {
                getRpmTimerLocked(parcel.readString()).readSummaryFromParcelLocked(parcel);
            }
        }
        int readInt6 = parcel.readInt();
        if (readInt6 > 10000) {
            throw new ParcelFormatException("File corrupt: too many screen-off rpm stats " + readInt6);
        }
        for (int i12 = 0; i12 < readInt6; i12++) {
            if (parcel.readInt() != 0) {
                getScreenOffRpmTimerLocked(parcel.readString()).readSummaryFromParcelLocked(parcel);
            }
        }
        int readInt7 = parcel.readInt();
        if (readInt7 > 10000) {
            throw new ParcelFormatException("File corrupt: too many kernel wake locks " + readInt7);
        }
        for (int i13 = 0; i13 < readInt7; i13++) {
            if (parcel.readInt() != 0) {
                getKernelWakelockTimerLocked(parcel.readString()).readSummaryFromParcelLocked(parcel);
            }
        }
        int readInt8 = parcel.readInt();
        if (readInt8 > 10000) {
            throw new ParcelFormatException("File corrupt: too many wakeup reasons " + readInt8);
        }
        for (int i14 = 0; i14 < readInt8; i14++) {
            if (parcel.readInt() != 0) {
                getWakeupReasonTimerLocked(parcel.readString()).readSummaryFromParcelLocked(parcel);
            }
        }
        int readInt9 = parcel.readInt();
        for (int i15 = 0; i15 < readInt9; i15++) {
            if (parcel.readInt() != 0) {
                getKernelMemoryTimerLocked(parcel.readLong()).readSummaryFromParcelLocked(parcel);
            }
        }
        int readInt10 = parcel.readInt();
        if (readInt10 > 10000) {
            throw new ParcelFormatException("File corrupt: too many uids " + readInt10);
        }
        long elapsedRealtime = this.mClock.elapsedRealtime();
        long uptimeMillis = this.mClock.uptimeMillis();
        int i16 = 0;
        while (i16 < readInt10) {
            int readInt11 = parcel.readInt();
            int i17 = i16;
            long j3 = elapsedRealtime;
            Uid uid = new Uid(this, readInt11, elapsedRealtime, uptimeMillis);
            this.mUidStats.put(readInt11, uid);
            uid.mOnBatteryBackgroundTimeBase.readSummaryFromParcel(parcel);
            uid.mOnBatteryScreenOffBackgroundTimeBase.readSummaryFromParcel(parcel);
            uid.mWifiRunning = z;
            if (parcel.readInt() != 0) {
                uid.mWifiRunningTimer.readSummaryFromParcelLocked(parcel);
            }
            uid.mFullWifiLockOut = z;
            if (parcel.readInt() != 0) {
                uid.mFullWifiLockTimer.readSummaryFromParcelLocked(parcel);
            }
            uid.mWifiScanStarted = z;
            if (parcel.readInt() != 0) {
                uid.mWifiScanTimer.readSummaryFromParcelLocked(parcel);
            }
            uid.mWifiBatchedScanBinStarted = -1;
            for (int i18 = z ? 1 : 0; i18 < 5; i18++) {
                if (parcel.readInt() != 0) {
                    uid.makeWifiBatchedScanBin(i18, null);
                    uid.mWifiBatchedScanTimer[i18].readSummaryFromParcelLocked(parcel);
                }
            }
            uid.mWifiMulticastWakelockCount = z ? 1 : 0;
            if (parcel.readInt() != 0) {
                uid.mWifiMulticastTimer.readSummaryFromParcelLocked(parcel);
            }
            if (parcel.readInt() != 0) {
                uid.createAudioTurnedOnTimerLocked().readSummaryFromParcelLocked(parcel);
            }
            if (parcel.readInt() != 0) {
                uid.createVideoTurnedOnTimerLocked().readSummaryFromParcelLocked(parcel);
            }
            if (parcel.readInt() != 0) {
                uid.createFlashlightTurnedOnTimerLocked().readSummaryFromParcelLocked(parcel);
            }
            if (parcel.readInt() != 0) {
                uid.createCameraTurnedOnTimerLocked().readSummaryFromParcelLocked(parcel);
            }
            if (parcel.readInt() != 0) {
                uid.createForegroundActivityTimerLocked().readSummaryFromParcelLocked(parcel);
            }
            if (parcel.readInt() != 0) {
                uid.createForegroundServiceTimerLocked().readSummaryFromParcelLocked(parcel);
            }
            if (parcel.readInt() != 0) {
                uid.createAggregatedPartialWakelockTimerLocked().readSummaryFromParcelLocked(parcel);
            }
            if (parcel.readInt() != 0) {
                uid.createBluetoothScanTimerLocked().readSummaryFromParcelLocked(parcel);
            }
            if (parcel.readInt() != 0) {
                uid.createBluetoothUnoptimizedScanTimerLocked().readSummaryFromParcelLocked(parcel);
            }
            if (parcel.readInt() != 0) {
                uid.createBluetoothScanResultCounterLocked().readSummaryFromParcelLocked(parcel);
            }
            if (parcel.readInt() != 0) {
                uid.createBluetoothScanResultBgCounterLocked().readSummaryFromParcelLocked(parcel);
            }
            uid.mProcessState = 7;
            for (int i19 = z ? 1 : 0; i19 < 7; i19++) {
                if (parcel.readInt() != 0) {
                    uid.makeProcessState(i19, null);
                    uid.mProcessStateTimer[i19].readSummaryFromParcelLocked(parcel);
                }
            }
            if (parcel.readInt() != 0) {
                uid.createVibratorOnTimerLocked().readSummaryFromParcelLocked(parcel);
            }
            if (parcel.readInt() != 0) {
                if (uid.mUserActivityCounters == null) {
                    uid.initUserActivityLocked();
                }
                for (int i20 = z ? 1 : 0; i20 < BatteryStats.Uid.NUM_USER_ACTIVITY_TYPES; i20++) {
                    uid.mUserActivityCounters[i20].readSummaryFromParcelLocked(parcel);
                }
            }
            if (parcel.readInt() != 0) {
                uid.ensureNetworkActivityLocked();
                for (int i21 = z ? 1 : 0; i21 < 10; i21++) {
                    uid.mNetworkByteActivityCounters[i21].readSummaryFromParcelLocked(parcel);
                    uid.mNetworkPacketActivityCounters[i21].readSummaryFromParcelLocked(parcel);
                }
                if (parcel.readBoolean()) {
                    j = j3;
                    uid.mMobileRadioActiveTime = TimeMultiStateCounter.readFromParcel(parcel, this.mOnBatteryTimeBase, 5, j);
                } else {
                    j = j3;
                }
                uid.mMobileRadioActiveCount.readSummaryFromParcelLocked(parcel);
            } else {
                j = j3;
            }
            uid.mUserCpuTime.readSummaryFromParcelLocked(parcel);
            uid.mSystemCpuTime.readSummaryFromParcelLocked(parcel);
            if (parcel.readInt() != 0) {
                int readInt12 = parcel.readInt();
                PowerProfile powerProfile = this.mPowerProfile;
                if (powerProfile != null && powerProfile.getNumCpuClusters() != readInt12) {
                    throw new ParcelFormatException("Incompatible cpu cluster arrangement");
                }
                detachIfNotNull(uid.mCpuClusterSpeedTimesUs);
                uid.mCpuClusterSpeedTimesUs = new LongSamplingCounter[readInt12];
                int i22 = z ? 1 : 0;
                int i23 = z;
                while (i22 < readInt12) {
                    if (parcel.readInt() != 0) {
                        int readInt13 = parcel.readInt();
                        PowerProfile powerProfile2 = this.mPowerProfile;
                        if (powerProfile2 != null && powerProfile2.getNumSpeedStepsInCpuCluster(i22) != readInt13) {
                            throw new ParcelFormatException("File corrupt: too many speed bins " + readInt13);
                        }
                        uid.mCpuClusterSpeedTimesUs[i22] = new LongSamplingCounter[readInt13];
                        for (int i24 = i23; i24 < readInt13; i24++) {
                            if (parcel.readInt() != 0) {
                                uid.mCpuClusterSpeedTimesUs[i22][i24] = new LongSamplingCounter(this.mOnBatteryTimeBase);
                                uid.mCpuClusterSpeedTimesUs[i22][i24].readSummaryFromParcelLocked(parcel);
                            }
                        }
                    } else {
                        uid.mCpuClusterSpeedTimesUs[i22] = null;
                    }
                    i22++;
                    i23 = 0;
                }
            } else {
                detachIfNotNull(uid.mCpuClusterSpeedTimesUs);
                uid.mCpuClusterSpeedTimesUs = null;
            }
            detachIfNotNull(uid.mCpuFreqTimeMs);
            uid.mCpuFreqTimeMs = LongSamplingCounterArray.readSummaryFromParcelLocked(parcel, this.mOnBatteryTimeBase);
            detachIfNotNull(uid.mScreenOffCpuFreqTimeMs);
            uid.mScreenOffCpuFreqTimeMs = LongSamplingCounterArray.readSummaryFromParcelLocked(parcel, this.mOnBatteryScreenOffTimeBase);
            if (parcel.readInt() != 0) {
                uid.mCpuActiveTimeMs = TimeMultiStateCounter.readFromParcel(parcel, this.mOnBatteryTimeBase, 5, this.mClock.elapsedRealtime());
            }
            uid.mCpuClusterTimesMs.readSummaryFromParcelLocked(parcel);
            detachIfNotNull(uid.mProcStateTimeMs);
            uid.mProcStateTimeMs = null;
            if (parcel.readInt() != 0) {
                detachIfNotNull(uid.mProcStateTimeMs);
                j2 = j;
                uid.mProcStateTimeMs = TimeInFreqMultiStateCounter.readFromParcel(parcel, this.mOnBatteryTimeBase, 8, getCpuFreqCount(), this.mClock.elapsedRealtime());
            } else {
                j2 = j;
            }
            detachIfNotNull(uid.mProcStateScreenOffTimeMs);
            uid.mProcStateScreenOffTimeMs = null;
            if (parcel.readInt() != 0) {
                detachIfNotNull(uid.mProcStateScreenOffTimeMs);
                uid.mProcStateScreenOffTimeMs = TimeInFreqMultiStateCounter.readFromParcel(parcel, this.mOnBatteryScreenOffTimeBase, 8, getCpuFreqCount(), this.mClock.elapsedRealtime());
            }
            if (parcel.readInt() != 0) {
                detachIfNotNull(uid.mMobileRadioApWakeupCount);
                uid.mMobileRadioApWakeupCount = new LongSamplingCounter(this.mOnBatteryTimeBase);
                uid.mMobileRadioApWakeupCount.readSummaryFromParcelLocked(parcel);
            } else {
                detachIfNotNull(uid.mMobileRadioApWakeupCount);
                uid.mMobileRadioApWakeupCount = null;
            }
            if (parcel.readInt() != 0) {
                detachIfNotNull(uid.mWifiRadioApWakeupCount);
                uid.mWifiRadioApWakeupCount = new LongSamplingCounter(this.mOnBatteryTimeBase);
                uid.mWifiRadioApWakeupCount.readSummaryFromParcelLocked(parcel);
            } else {
                detachIfNotNull(uid.mWifiRadioApWakeupCount);
                uid.mWifiRadioApWakeupCount = null;
            }
            uid.mUidEnergyConsumerStats = EnergyConsumerStats.createAndReadSummaryFromParcel(this.mEnergyConsumerStatsConfig, parcel);
            int readInt14 = parcel.readInt();
            if (readInt14 > MAX_WAKELOCKS_PER_UID + 1) {
                throw new ParcelFormatException("File corrupt: too many wake locks " + readInt14);
            }
            for (int i25 = 0; i25 < readInt14; i25++) {
                uid.readWakeSummaryFromParcelLocked(parcel.readString(), parcel);
            }
            int readInt15 = parcel.readInt();
            if (readInt15 > MAX_WAKELOCKS_PER_UID + 1) {
                throw new ParcelFormatException("File corrupt: too many syncs " + readInt15);
            }
            for (int i26 = 0; i26 < readInt15; i26++) {
                uid.readSyncSummaryFromParcelLocked(parcel.readString(), parcel);
            }
            int readInt16 = parcel.readInt();
            if (readInt16 > MAX_WAKELOCKS_PER_UID + 1) {
                throw new ParcelFormatException("File corrupt: too many job timers " + readInt16);
            }
            for (int i27 = 0; i27 < readInt16; i27++) {
                uid.readJobSummaryFromParcelLocked(parcel.readString(), parcel);
            }
            uid.readJobCompletionsFromParcelLocked(parcel);
            uid.mJobsDeferredEventCount.readSummaryFromParcelLocked(parcel);
            uid.mJobsDeferredCount.readSummaryFromParcelLocked(parcel);
            uid.mJobsFreshnessTimeMs.readSummaryFromParcelLocked(parcel);
            detachIfNotNull(uid.mJobsFreshnessBuckets);
            for (int i28 = 0; i28 < BatteryStats.JOB_FRESHNESS_BUCKETS.length; i28++) {
                if (parcel.readInt() != 0) {
                    uid.mJobsFreshnessBuckets[i28] = new Counter(uid.mBsi.mOnBatteryTimeBase);
                    uid.mJobsFreshnessBuckets[i28].readSummaryFromParcelLocked(parcel);
                }
            }
            int readInt17 = parcel.readInt();
            if (readInt17 > 1000) {
                throw new ParcelFormatException("File corrupt: too many sensors " + readInt17);
            }
            for (int i29 = 0; i29 < readInt17; i29++) {
                int readInt18 = parcel.readInt();
                if (parcel.readInt() != 0) {
                    uid.getSensorTimerLocked(readInt18, true).readSummaryFromParcelLocked(parcel);
                }
            }
            int readInt19 = parcel.readInt();
            if (readInt19 > 1000) {
                throw new ParcelFormatException("File corrupt: too many processes " + readInt19);
            }
            for (int i30 = 0; i30 < readInt19; i30++) {
                Uid.Proc processStatsLocked = uid.getProcessStatsLocked(parcel.readString());
                processStatsLocked.mUserTimeMs = parcel.readLong();
                processStatsLocked.mSystemTimeMs = parcel.readLong();
                processStatsLocked.mForegroundTimeMs = parcel.readLong();
                processStatsLocked.mStarts = parcel.readInt();
                processStatsLocked.mNumCrashes = parcel.readInt();
                processStatsLocked.mNumAnrs = parcel.readInt();
                processStatsLocked.readExcessivePowerFromParcelLocked(parcel);
            }
            int readInt20 = parcel.readInt();
            int i31 = FrameworkStatsLog.WIFI_BYTES_TRANSFER;
            if (readInt20 > 10000) {
                throw new ParcelFormatException("File corrupt: too many packages " + readInt20);
            }
            int i32 = 0;
            while (i32 < readInt20) {
                String readString = parcel.readString();
                detachIfNotNull(uid.mPackageStats.get(readString));
                Uid.Pkg packageStatsLocked = uid.getPackageStatsLocked(readString);
                int readInt21 = parcel.readInt();
                if (readInt21 > i31) {
                    throw new ParcelFormatException("File corrupt: too many wakeup alarms " + readInt21);
                }
                packageStatsLocked.mWakeupAlarms.clear();
                for (int i33 = 0; i33 < readInt21; i33++) {
                    String readString2 = parcel.readString();
                    Counter counter = new Counter(this.mOnBatteryScreenOffTimeBase);
                    counter.readSummaryFromParcelLocked(parcel);
                    packageStatsLocked.mWakeupAlarms.put(readString2, counter);
                }
                int readInt22 = parcel.readInt();
                if (readInt22 > 10000) {
                    throw new ParcelFormatException("File corrupt: too many services " + readInt22);
                }
                for (int i34 = 0; i34 < readInt22; i34++) {
                    Uid.Pkg.Serv serviceStatsLocked = uid.getServiceStatsLocked(readString, parcel.readString());
                    serviceStatsLocked.mStartTimeMs = parcel.readLong();
                    serviceStatsLocked.mStarts = parcel.readInt();
                    serviceStatsLocked.mLaunches = parcel.readInt();
                }
                i32++;
                i31 = 10000;
            }
            i16 = i17 + 1;
            elapsedRealtime = j2;
            z = 0;
        }
        this.mBinderThreadCpuTimesUs = LongSamplingCounterArray.readSummaryFromParcelLocked(parcel, this.mOnBatteryTimeBase);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r14v0 */
    /* JADX WARN: Type inference failed for: r14v1, types: [int, boolean] */
    /* JADX WARN: Type inference failed for: r14v8 */
    @GuardedBy({"this"})
    public void writeSummaryToParcel(Parcel parcel, boolean z) {
        boolean z2;
        int i;
        int i2;
        int i3;
        pullPendingStateUpdatesLocked();
        getStartClockTime();
        long uptimeMillis = this.mClock.uptimeMillis() * 1000;
        long elapsedRealtime = this.mClock.elapsedRealtime() * 1000;
        parcel.writeInt(FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__ROLE_HOLDER_UPDATER_UPDATE_FAILED);
        this.mHistory.writeSummaryToParcel(parcel, z);
        parcel.writeInt(this.mStartCount);
        ?? r14 = 0;
        parcel.writeLong(computeUptime(uptimeMillis, 0));
        parcel.writeLong(computeRealtime(elapsedRealtime, 0));
        parcel.writeLong(this.mStartClockTimeMs);
        parcel.writeString(this.mStartPlatformVersion);
        parcel.writeString(this.mEndPlatformVersion);
        this.mOnBatteryTimeBase.writeSummaryToParcel(parcel, uptimeMillis, elapsedRealtime);
        this.mOnBatteryScreenOffTimeBase.writeSummaryToParcel(parcel, uptimeMillis, elapsedRealtime);
        parcel.writeInt(this.mDischargeUnplugLevel);
        parcel.writeInt(this.mDischargePlugLevel);
        parcel.writeInt(this.mDischargeCurrentLevel);
        parcel.writeInt(this.mBatteryLevel);
        parcel.writeInt(this.mEstimatedBatteryCapacityMah);
        parcel.writeInt(this.mLastLearnedBatteryCapacityUah);
        parcel.writeInt(this.mMinLearnedBatteryCapacityUah);
        parcel.writeInt(this.mMaxLearnedBatteryCapacityUah);
        parcel.writeInt(getLowDischargeAmountSinceCharge());
        parcel.writeInt(getHighDischargeAmountSinceCharge());
        parcel.writeInt(getDischargeAmountScreenOnSinceCharge());
        parcel.writeInt(getDischargeAmountScreenOffSinceCharge());
        parcel.writeInt(getDischargeAmountScreenDozeSinceCharge());
        this.mDischargeStepTracker.writeToParcel(parcel);
        this.mChargeStepTracker.writeToParcel(parcel);
        this.mDailyDischargeStepTracker.writeToParcel(parcel);
        this.mDailyChargeStepTracker.writeToParcel(parcel);
        this.mDischargeCounter.writeSummaryFromParcelLocked(parcel);
        this.mDischargeScreenOffCounter.writeSummaryFromParcelLocked(parcel);
        this.mDischargeScreenDozeCounter.writeSummaryFromParcelLocked(parcel);
        this.mDischargeLightDozeCounter.writeSummaryFromParcelLocked(parcel);
        this.mDischargeDeepDozeCounter.writeSummaryFromParcelLocked(parcel);
        ArrayList<BatteryStats.PackageChange> arrayList = this.mDailyPackageChanges;
        if (arrayList != null) {
            int size = arrayList.size();
            parcel.writeInt(size);
            for (int i4 = 0; i4 < size; i4++) {
                BatteryStats.PackageChange packageChange = this.mDailyPackageChanges.get(i4);
                parcel.writeString(packageChange.mPackageName);
                parcel.writeInt(packageChange.mUpdate ? 1 : 0);
                parcel.writeLong(packageChange.mVersionCode);
            }
        } else {
            parcel.writeInt(0);
        }
        parcel.writeLong(this.mDailyStartTimeMs);
        parcel.writeLong(this.mNextMinDailyDeadlineMs);
        parcel.writeLong(this.mNextMaxDailyDeadlineMs);
        parcel.writeLong(this.mBatteryTimeToFullSeconds);
        EnergyConsumerStats.Config.writeToParcel(this.mEnergyConsumerStatsConfig, parcel);
        EnergyConsumerStats.writeSummaryToParcel(this.mGlobalEnergyConsumerStats, parcel);
        this.mScreenOnTimer.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
        this.mScreenDozeTimer.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
        for (int i5 = 0; i5 < 5; i5++) {
            this.mScreenBrightnessTimer[i5].writeSummaryFromParcelLocked(parcel, elapsedRealtime);
        }
        int length = this.mPerDisplayBatteryStats.length;
        parcel.writeInt(length);
        for (int i6 = 0; i6 < length; i6++) {
            this.mPerDisplayBatteryStats[i6].writeSummaryToParcel(parcel, elapsedRealtime);
        }
        this.mInteractiveTimer.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
        this.mPowerSaveModeEnabledTimer.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
        parcel.writeLong(this.mLongestLightIdleTimeMs);
        parcel.writeLong(this.mLongestFullIdleTimeMs);
        this.mDeviceIdleModeLightTimer.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
        this.mDeviceIdleModeFullTimer.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
        this.mDeviceLightIdlingTimer.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
        this.mDeviceIdlingTimer.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
        this.mPhoneOnTimer.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
        for (int i7 = 0; i7 < CellSignalStrength.getNumSignalStrengthLevels(); i7++) {
            this.mPhoneSignalStrengthsTimer[i7].writeSummaryFromParcelLocked(parcel, elapsedRealtime);
        }
        this.mPhoneSignalScanningTimer.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
        for (int i8 = 0; i8 < BatteryStats.NUM_DATA_CONNECTION_TYPES; i8++) {
            this.mPhoneDataConnectionsTimer[i8].writeSummaryFromParcelLocked(parcel, elapsedRealtime);
        }
        for (int i9 = 0; i9 < 10; i9++) {
            this.mNetworkByteActivityCounters[i9].writeSummaryFromParcelLocked(parcel);
            this.mNetworkPacketActivityCounters[i9].writeSummaryFromParcelLocked(parcel);
        }
        int length2 = this.mPerRatBatteryStats.length;
        parcel.writeInt(length2);
        int i10 = 0;
        while (true) {
            z2 = true;
            if (i10 >= length2) {
                break;
            }
            RadioAccessTechnologyBatteryStats radioAccessTechnologyBatteryStats = this.mPerRatBatteryStats[i10];
            if (radioAccessTechnologyBatteryStats == null) {
                parcel.writeInt(0);
            } else {
                parcel.writeInt(1);
                radioAccessTechnologyBatteryStats.writeSummaryToParcel(parcel, elapsedRealtime);
            }
            i10++;
        }
        this.mMobileRadioActiveTimer.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
        this.mMobileRadioActivePerAppTimer.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
        this.mMobileRadioActiveAdjustedTime.writeSummaryFromParcelLocked(parcel);
        this.mMobileRadioActiveUnknownTime.writeSummaryFromParcelLocked(parcel);
        this.mMobileRadioActiveUnknownCount.writeSummaryFromParcelLocked(parcel);
        this.mWifiMulticastWakelockTimer.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
        this.mWifiOnTimer.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
        this.mGlobalWifiRunningTimer.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
        for (int i11 = 0; i11 < 8; i11++) {
            this.mWifiStateTimer[i11].writeSummaryFromParcelLocked(parcel, elapsedRealtime);
        }
        for (int i12 = 0; i12 < 13; i12++) {
            this.mWifiSupplStateTimer[i12].writeSummaryFromParcelLocked(parcel, elapsedRealtime);
        }
        for (int i13 = 0; i13 < 5; i13++) {
            this.mWifiSignalStrengthsTimer[i13].writeSummaryFromParcelLocked(parcel, elapsedRealtime);
        }
        this.mWifiActiveTimer.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
        this.mWifiActivity.writeSummaryToParcel(parcel);
        int i14 = 0;
        while (true) {
            StopwatchTimer[] stopwatchTimerArr = this.mGpsSignalQualityTimer;
            if (i14 >= stopwatchTimerArr.length) {
                break;
            }
            stopwatchTimerArr[i14].writeSummaryFromParcelLocked(parcel, elapsedRealtime);
            i14++;
        }
        this.mBluetoothActivity.writeSummaryToParcel(parcel);
        this.mModemActivity.writeSummaryToParcel(parcel);
        parcel.writeInt(this.mHasWifiReporting ? 1 : 0);
        parcel.writeInt(this.mHasBluetoothReporting ? 1 : 0);
        parcel.writeInt(this.mHasModemReporting ? 1 : 0);
        parcel.writeInt(this.mNumConnectivityChange);
        this.mFlashlightOnTimer.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
        this.mCameraOnTimer.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
        this.mBluetoothScanTimer.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
        parcel.writeInt(this.mRpmStats.size());
        for (Map.Entry<String, SamplingTimer> entry : this.mRpmStats.entrySet()) {
            SamplingTimer value = entry.getValue();
            if (value != null) {
                parcel.writeInt(1);
                parcel.writeString(entry.getKey());
                value.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
            } else {
                parcel.writeInt(0);
            }
        }
        parcel.writeInt(this.mScreenOffRpmStats.size());
        for (Map.Entry<String, SamplingTimer> entry2 : this.mScreenOffRpmStats.entrySet()) {
            SamplingTimer value2 = entry2.getValue();
            if (value2 != null) {
                parcel.writeInt(1);
                parcel.writeString(entry2.getKey());
                value2.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
            } else {
                parcel.writeInt(0);
            }
        }
        parcel.writeInt(this.mKernelWakelockStats.size());
        for (Map.Entry<String, SamplingTimer> entry3 : this.mKernelWakelockStats.entrySet()) {
            SamplingTimer value3 = entry3.getValue();
            if (value3 != null) {
                parcel.writeInt(1);
                parcel.writeString(entry3.getKey());
                value3.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
            } else {
                parcel.writeInt(0);
            }
        }
        parcel.writeInt(this.mWakeupReasonStats.size());
        for (Map.Entry<String, SamplingTimer> entry4 : this.mWakeupReasonStats.entrySet()) {
            SamplingTimer value4 = entry4.getValue();
            if (value4 != null) {
                parcel.writeInt(1);
                parcel.writeString(entry4.getKey());
                value4.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
            } else {
                parcel.writeInt(0);
            }
        }
        parcel.writeInt(this.mKernelMemoryStats.size());
        for (int i15 = 0; i15 < this.mKernelMemoryStats.size(); i15++) {
            SamplingTimer valueAt = this.mKernelMemoryStats.valueAt(i15);
            if (valueAt != null) {
                parcel.writeInt(1);
                parcel.writeLong(this.mKernelMemoryStats.keyAt(i15));
                valueAt.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
            } else {
                parcel.writeInt(0);
            }
        }
        int size2 = this.mUidStats.size();
        parcel.writeInt(size2);
        int i16 = 0;
        while (i16 < size2) {
            parcel.writeInt(this.mUidStats.keyAt(i16));
            Uid valueAt2 = this.mUidStats.valueAt(i16);
            int i17 = size2;
            int i18 = i16;
            boolean z3 = z2;
            valueAt2.mOnBatteryBackgroundTimeBase.writeSummaryToParcel(parcel, uptimeMillis, elapsedRealtime);
            valueAt2.mOnBatteryScreenOffBackgroundTimeBase.writeSummaryToParcel(parcel, uptimeMillis, elapsedRealtime);
            if (valueAt2.mWifiRunningTimer != null) {
                parcel.writeInt(z3 ? 1 : 0);
                valueAt2.mWifiRunningTimer.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
            } else {
                parcel.writeInt(r14);
            }
            if (valueAt2.mFullWifiLockTimer != null) {
                parcel.writeInt(z3 ? 1 : 0);
                valueAt2.mFullWifiLockTimer.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
            } else {
                parcel.writeInt(r14);
            }
            if (valueAt2.mWifiScanTimer != null) {
                parcel.writeInt(z3 ? 1 : 0);
                valueAt2.mWifiScanTimer.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
            } else {
                parcel.writeInt(r14);
            }
            for (int i19 = r14; i19 < 5; i19++) {
                if (valueAt2.mWifiBatchedScanTimer[i19] != null) {
                    parcel.writeInt(z3 ? 1 : 0);
                    valueAt2.mWifiBatchedScanTimer[i19].writeSummaryFromParcelLocked(parcel, elapsedRealtime);
                } else {
                    parcel.writeInt(r14);
                }
            }
            if (valueAt2.mWifiMulticastTimer != null) {
                parcel.writeInt(z3 ? 1 : 0);
                valueAt2.mWifiMulticastTimer.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
            } else {
                parcel.writeInt(r14);
            }
            if (valueAt2.mAudioTurnedOnTimer != null) {
                parcel.writeInt(z3 ? 1 : 0);
                valueAt2.mAudioTurnedOnTimer.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
            } else {
                parcel.writeInt(r14);
            }
            if (valueAt2.mVideoTurnedOnTimer != null) {
                parcel.writeInt(z3 ? 1 : 0);
                valueAt2.mVideoTurnedOnTimer.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
            } else {
                parcel.writeInt(r14);
            }
            if (valueAt2.mFlashlightTurnedOnTimer != null) {
                parcel.writeInt(z3 ? 1 : 0);
                valueAt2.mFlashlightTurnedOnTimer.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
            } else {
                parcel.writeInt(r14);
            }
            if (valueAt2.mCameraTurnedOnTimer != null) {
                parcel.writeInt(z3 ? 1 : 0);
                valueAt2.mCameraTurnedOnTimer.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
            } else {
                parcel.writeInt(r14);
            }
            if (valueAt2.mForegroundActivityTimer != null) {
                parcel.writeInt(z3 ? 1 : 0);
                valueAt2.mForegroundActivityTimer.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
            } else {
                parcel.writeInt(r14);
            }
            if (valueAt2.mForegroundServiceTimer != null) {
                parcel.writeInt(z3 ? 1 : 0);
                valueAt2.mForegroundServiceTimer.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
            } else {
                parcel.writeInt(r14);
            }
            if (valueAt2.mAggregatedPartialWakelockTimer != null) {
                parcel.writeInt(z3 ? 1 : 0);
                valueAt2.mAggregatedPartialWakelockTimer.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
            } else {
                parcel.writeInt(r14);
            }
            if (valueAt2.mBluetoothScanTimer != null) {
                parcel.writeInt(z3 ? 1 : 0);
                valueAt2.mBluetoothScanTimer.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
            } else {
                parcel.writeInt(r14);
            }
            if (valueAt2.mBluetoothUnoptimizedScanTimer != null) {
                parcel.writeInt(z3 ? 1 : 0);
                valueAt2.mBluetoothUnoptimizedScanTimer.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
            } else {
                parcel.writeInt(r14);
            }
            if (valueAt2.mBluetoothScanResultCounter != null) {
                parcel.writeInt(z3 ? 1 : 0);
                valueAt2.mBluetoothScanResultCounter.writeSummaryFromParcelLocked(parcel);
            } else {
                parcel.writeInt(r14);
            }
            if (valueAt2.mBluetoothScanResultBgCounter != null) {
                parcel.writeInt(z3 ? 1 : 0);
                valueAt2.mBluetoothScanResultBgCounter.writeSummaryFromParcelLocked(parcel);
            } else {
                parcel.writeInt(r14);
            }
            for (int i20 = r14; i20 < 7; i20++) {
                if (valueAt2.mProcessStateTimer[i20] != null) {
                    parcel.writeInt(z3 ? 1 : 0);
                    valueAt2.mProcessStateTimer[i20].writeSummaryFromParcelLocked(parcel, elapsedRealtime);
                } else {
                    parcel.writeInt(r14);
                }
            }
            if (valueAt2.mVibratorOnTimer != null) {
                parcel.writeInt(z3 ? 1 : 0);
                valueAt2.mVibratorOnTimer.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
            } else {
                parcel.writeInt(r14);
            }
            if (valueAt2.mUserActivityCounters == null) {
                parcel.writeInt(r14);
            } else {
                parcel.writeInt(z3 ? 1 : 0);
                for (int i21 = r14; i21 < BatteryStats.Uid.NUM_USER_ACTIVITY_TYPES; i21++) {
                    valueAt2.mUserActivityCounters[i21].writeSummaryFromParcelLocked(parcel);
                }
            }
            if (valueAt2.mNetworkByteActivityCounters == null) {
                parcel.writeInt(r14);
            } else {
                parcel.writeInt(z3 ? 1 : 0);
                for (int i22 = r14; i22 < 10; i22++) {
                    valueAt2.mNetworkByteActivityCounters[i22].writeSummaryFromParcelLocked(parcel);
                    valueAt2.mNetworkPacketActivityCounters[i22].writeSummaryFromParcelLocked(parcel);
                }
                if (valueAt2.mMobileRadioActiveTime != null) {
                    parcel.writeBoolean(z3);
                    valueAt2.mMobileRadioActiveTime.writeToParcel(parcel);
                } else {
                    parcel.writeBoolean(r14);
                }
                valueAt2.mMobileRadioActiveCount.writeSummaryFromParcelLocked(parcel);
            }
            valueAt2.mUserCpuTime.writeSummaryFromParcelLocked(parcel);
            valueAt2.mSystemCpuTime.writeSummaryFromParcelLocked(parcel);
            if (valueAt2.mCpuClusterSpeedTimesUs != null) {
                parcel.writeInt(z3 ? 1 : 0);
                parcel.writeInt(valueAt2.mCpuClusterSpeedTimesUs.length);
                LongSamplingCounter[][] longSamplingCounterArr = valueAt2.mCpuClusterSpeedTimesUs;
                int length3 = longSamplingCounterArr.length;
                int i23 = r14;
                int i24 = r14;
                while (i23 < length3) {
                    LongSamplingCounter[] longSamplingCounterArr2 = longSamplingCounterArr[i23];
                    if (longSamplingCounterArr2 != null) {
                        parcel.writeInt(z3 ? 1 : 0);
                        parcel.writeInt(longSamplingCounterArr2.length);
                        int length4 = longSamplingCounterArr2.length;
                        int i25 = i24;
                        i24 = i24;
                        while (i25 < length4) {
                            LongSamplingCounter longSamplingCounter = longSamplingCounterArr2[i25];
                            if (longSamplingCounter != null) {
                                parcel.writeInt(z3 ? 1 : 0);
                                longSamplingCounter.writeSummaryFromParcelLocked(parcel);
                                i3 = 0;
                            } else {
                                i3 = 0;
                                parcel.writeInt(0);
                            }
                            i25++;
                            i24 = i3;
                        }
                    } else {
                        parcel.writeInt(i24);
                    }
                    i23++;
                    i24 = i24;
                }
            } else {
                parcel.writeInt(r14);
            }
            LongSamplingCounterArray.writeSummaryToParcelLocked(parcel, valueAt2.mCpuFreqTimeMs);
            LongSamplingCounterArray.writeSummaryToParcelLocked(parcel, valueAt2.mScreenOffCpuFreqTimeMs);
            TimeMultiStateCounter timeMultiStateCounter = valueAt2.mCpuActiveTimeMs;
            if (timeMultiStateCounter != null) {
                parcel.writeInt(timeMultiStateCounter.getStateCount());
                valueAt2.mCpuActiveTimeMs.writeToParcel(parcel);
                i = 0;
            } else {
                i = 0;
                parcel.writeInt(0);
            }
            valueAt2.mCpuClusterTimesMs.writeSummaryToParcelLocked(parcel);
            TimeInFreqMultiStateCounter timeInFreqMultiStateCounter = valueAt2.mProcStateTimeMs;
            if (timeInFreqMultiStateCounter != null) {
                parcel.writeInt(timeInFreqMultiStateCounter.getStateCount());
                valueAt2.mProcStateTimeMs.writeToParcel(parcel);
            } else {
                parcel.writeInt(i);
            }
            TimeInFreqMultiStateCounter timeInFreqMultiStateCounter2 = valueAt2.mProcStateScreenOffTimeMs;
            if (timeInFreqMultiStateCounter2 != null) {
                parcel.writeInt(timeInFreqMultiStateCounter2.getStateCount());
                valueAt2.mProcStateScreenOffTimeMs.writeToParcel(parcel);
            } else {
                parcel.writeInt(i);
            }
            if (valueAt2.mMobileRadioApWakeupCount != null) {
                parcel.writeInt(z3 ? 1 : 0);
                valueAt2.mMobileRadioApWakeupCount.writeSummaryFromParcelLocked(parcel);
            } else {
                parcel.writeInt(i);
            }
            if (valueAt2.mWifiRadioApWakeupCount != null) {
                parcel.writeInt(z3 ? 1 : 0);
                valueAt2.mWifiRadioApWakeupCount.writeSummaryFromParcelLocked(parcel);
            } else {
                parcel.writeInt(i);
            }
            EnergyConsumerStats.writeSummaryToParcel(valueAt2.mUidEnergyConsumerStats, parcel);
            ArrayMap<String, Uid.Wakelock> map = valueAt2.mWakelockStats.getMap();
            int size3 = map.size();
            parcel.writeInt(size3);
            for (int i26 = 0; i26 < size3; i26++) {
                parcel.writeString(map.keyAt(i26));
                Uid.Wakelock valueAt3 = map.valueAt(i26);
                if (valueAt3.mTimerFull != null) {
                    parcel.writeInt(z3 ? 1 : 0);
                    valueAt3.mTimerFull.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
                    i2 = 0;
                } else {
                    i2 = 0;
                    parcel.writeInt(0);
                }
                if (valueAt3.mTimerPartial != null) {
                    parcel.writeInt(z3 ? 1 : 0);
                    valueAt3.mTimerPartial.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
                } else {
                    parcel.writeInt(i2);
                }
                if (valueAt3.mTimerWindow != null) {
                    parcel.writeInt(z3 ? 1 : 0);
                    valueAt3.mTimerWindow.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
                } else {
                    parcel.writeInt(i2);
                }
                if (valueAt3.mTimerDraw != null) {
                    parcel.writeInt(z3 ? 1 : 0);
                    valueAt3.mTimerDraw.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
                } else {
                    parcel.writeInt(i2);
                }
            }
            ArrayMap<String, DualTimer> map2 = valueAt2.mSyncStats.getMap();
            int size4 = map2.size();
            parcel.writeInt(size4);
            for (int i27 = 0; i27 < size4; i27++) {
                parcel.writeString(map2.keyAt(i27));
                map2.valueAt(i27).writeSummaryFromParcelLocked(parcel, elapsedRealtime);
            }
            ArrayMap<String, DualTimer> map3 = valueAt2.mJobStats.getMap();
            int size5 = map3.size();
            parcel.writeInt(size5);
            for (int i28 = 0; i28 < size5; i28++) {
                parcel.writeString(map3.keyAt(i28));
                map3.valueAt(i28).writeSummaryFromParcelLocked(parcel, elapsedRealtime);
            }
            valueAt2.writeJobCompletionsToParcelLocked(parcel);
            valueAt2.mJobsDeferredEventCount.writeSummaryFromParcelLocked(parcel);
            valueAt2.mJobsDeferredCount.writeSummaryFromParcelLocked(parcel);
            valueAt2.mJobsFreshnessTimeMs.writeSummaryFromParcelLocked(parcel);
            for (int i29 = 0; i29 < BatteryStats.JOB_FRESHNESS_BUCKETS.length; i29++) {
                if (valueAt2.mJobsFreshnessBuckets[i29] != null) {
                    parcel.writeInt(z3 ? 1 : 0);
                    valueAt2.mJobsFreshnessBuckets[i29].writeSummaryFromParcelLocked(parcel);
                } else {
                    parcel.writeInt(0);
                }
            }
            int size6 = valueAt2.mSensorStats.size();
            parcel.writeInt(size6);
            for (int i30 = 0; i30 < size6; i30++) {
                parcel.writeInt(valueAt2.mSensorStats.keyAt(i30));
                Uid.Sensor valueAt4 = valueAt2.mSensorStats.valueAt(i30);
                if (valueAt4.mTimer != null) {
                    parcel.writeInt(z3 ? 1 : 0);
                    valueAt4.mTimer.writeSummaryFromParcelLocked(parcel, elapsedRealtime);
                } else {
                    parcel.writeInt(0);
                }
            }
            int i31 = 0;
            int size7 = valueAt2.mProcessStats.size();
            parcel.writeInt(size7);
            for (int i32 = 0; i32 < size7; i32++) {
                parcel.writeString(valueAt2.mProcessStats.keyAt(i32));
                Uid.Proc valueAt5 = valueAt2.mProcessStats.valueAt(i32);
                parcel.writeLong(valueAt5.mUserTimeMs);
                parcel.writeLong(valueAt5.mSystemTimeMs);
                parcel.writeLong(valueAt5.mForegroundTimeMs);
                parcel.writeInt(valueAt5.mStarts);
                parcel.writeInt(valueAt5.mNumCrashes);
                parcel.writeInt(valueAt5.mNumAnrs);
                valueAt5.writeExcessivePowerToParcelLocked(parcel);
            }
            int size8 = valueAt2.mPackageStats.size();
            parcel.writeInt(size8);
            if (size8 > 0) {
                for (Map.Entry<String, Uid.Pkg> entry5 : valueAt2.mPackageStats.entrySet()) {
                    parcel.writeString(entry5.getKey());
                    Uid.Pkg value5 = entry5.getValue();
                    int size9 = value5.mWakeupAlarms.size();
                    parcel.writeInt(size9);
                    for (int i33 = i31; i33 < size9; i33++) {
                        parcel.writeString(value5.mWakeupAlarms.keyAt(i33));
                        value5.mWakeupAlarms.valueAt(i33).writeSummaryFromParcelLocked(parcel);
                    }
                    int size10 = value5.mServiceStats.size();
                    parcel.writeInt(size10);
                    int i34 = i31;
                    while (i34 < size10) {
                        parcel.writeString(value5.mServiceStats.keyAt(i34));
                        Uid.Pkg.Serv valueAt6 = value5.mServiceStats.valueAt(i34);
                        parcel.writeLong(valueAt6.getStartTimeToNowLocked(this.mOnBatteryTimeBase.getUptime(uptimeMillis) / 1000));
                        parcel.writeInt(valueAt6.mStarts);
                        parcel.writeInt(valueAt6.mLaunches);
                        i34++;
                        value5 = value5;
                        i31 = 0;
                    }
                }
            }
            i16 = i18 + 1;
            size2 = i17;
            z2 = z3 ? 1 : 0;
            r14 = 0;
        }
        LongSamplingCounterArray.writeSummaryToParcelLocked(parcel, this.mBinderThreadCpuTimesUs);
    }

    @GuardedBy({"this"})
    public void prepareForDumpLocked() {
        pullPendingStateUpdatesLocked();
        getStartClockTime();
        updateSystemServiceCallStats();
    }

    @GuardedBy({"this"})
    public void dump(Context context, PrintWriter printWriter, int i, int i2, long j) {
        super.dump(context, printWriter, i, i2, j);
        synchronized (this) {
            printWriter.print("Per process state tracking available: ");
            printWriter.println(trackPerProcStateCpuTimes());
            printWriter.print("Total cpu time reads: ");
            printWriter.println(this.mNumSingleUidCpuTimeReads);
            printWriter.print("Batching Duration (min): ");
            printWriter.println((this.mClock.uptimeMillis() - this.mCpuTimeReadsTrackingStartTimeMs) / 60000);
            printWriter.print("All UID cpu time reads since the later of device start or stats reset: ");
            printWriter.println(this.mNumAllUidCpuTimeReads);
            printWriter.print("UIDs removed since the later of device start or stats reset: ");
            printWriter.println(this.mNumUidsRemoved);
            printWriter.println("Currently mapped isolated uids:");
            int size = this.mIsolatedUids.size();
            for (int i3 = 0; i3 < size; i3++) {
                int keyAt = this.mIsolatedUids.keyAt(i3);
                int valueAt = this.mIsolatedUids.valueAt(i3);
                int i4 = this.mIsolatedUidRefCounts.get(keyAt);
                printWriter.println("  " + keyAt + "->" + valueAt + " (ref count = " + i4 + ")");
            }
            printWriter.println();
            dumpConstantsLocked(printWriter);
            printWriter.println();
            dumpCpuPowerBracketsLocked(printWriter);
            printWriter.println();
            dumpEnergyConsumerStatsLocked(printWriter);
        }
    }

    public BatteryUsageStats getBatteryUsageStats(Context context, boolean z) {
        BatteryUsageStatsProvider batteryUsageStatsProvider = new BatteryUsageStatsProvider(context, this);
        BatteryUsageStatsQuery.Builder maxStatsAgeMs = new BatteryUsageStatsQuery.Builder().setMaxStatsAgeMs(0L);
        if (z) {
            maxStatsAgeMs.includePowerModels().includeProcessStateData().includeVirtualUids();
        }
        return batteryUsageStatsProvider.getBatteryUsageStats(maxStatsAgeMs.build());
    }
}
