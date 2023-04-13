package com.android.server.location.gnss;

import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.location.GnssCapabilities;
import android.location.GnssStatus;
import android.location.INetInitiatedListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.location.LocationResult;
import android.location.provider.ProviderProperties;
import android.location.provider.ProviderRequest;
import android.location.util.identity.CallerIdentity;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.WorkSource;
import android.provider.Settings;
import android.provider.Telephony;
import android.telephony.CarrierConfigManager;
import android.telephony.CellIdentity;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityNr;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoNr;
import android.telephony.CellInfoWcdma;
import android.telephony.SmsMessage;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TimeUtils;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.app.IBatteryStats;
import com.android.internal.location.GpsNetInitiatedHandler;
import com.android.internal.util.ConcurrentUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.HexDump;
import com.android.server.FgThread;
import com.android.server.backup.BackupAgentTimeoutParameters;
import com.android.server.backup.BackupManagerConstants;
import com.android.server.location.gnss.GnssConfiguration;
import com.android.server.location.gnss.GnssLocationProvider;
import com.android.server.location.gnss.GnssNetworkConnectivityHandler;
import com.android.server.location.gnss.GnssSatelliteBlocklistHelper;
import com.android.server.location.gnss.NetworkTimeHelper;
import com.android.server.location.gnss.hal.GnssNative;
import com.android.server.location.provider.AbstractLocationProvider;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
/* loaded from: classes.dex */
public class GnssLocationProvider extends AbstractLocationProvider implements NetworkTimeHelper.InjectTimeCallback, GnssSatelliteBlocklistHelper.GnssSatelliteBlocklistCallback, GnssNative.BaseCallbacks, GnssNative.LocationCallbacks, GnssNative.SvStatusCallbacks, GnssNative.AGpsCallbacks, GnssNative.PsdsCallbacks, GnssNative.NotificationCallbacks, GnssNative.LocationRequestCallbacks, GnssNative.TimeCallbacks {
    public final AlarmManager mAlarmManager;
    public final AppOpsManager mAppOps;
    @GuardedBy({"mLock"})
    public boolean mAutomotiveSuspend;
    public AlarmManager.OnAlarmListener mBatchingAlarm;
    @GuardedBy({"mLock"})
    public boolean mBatchingEnabled;
    public boolean mBatchingStarted;
    public final IBatteryStats mBatteryStats;
    public String mC2KServerHost;
    public int mC2KServerPort;
    public final WorkSource mClientSource;
    public final Context mContext;
    @GuardedBy({"mLock"})
    public final PowerManager.WakeLock mDownloadPsdsWakeLock;
    public int mFixInterval;
    public long mFixRequestTime;
    @GuardedBy({"mLock"})
    public final ArrayList<Runnable> mFlushListeners;
    public final GnssConfiguration mGnssConfiguration;
    public final GnssMetrics mGnssMetrics;
    public final GnssNative mGnssNative;
    public final GnssSatelliteBlocklistHelper mGnssSatelliteBlocklistHelper;
    public GnssVisibilityControl mGnssVisibilityControl;
    @GuardedBy({"mLock"})
    public boolean mGpsEnabled;
    public final Handler mHandler;
    public BroadcastReceiver mIntentReceiver;
    public long mLastFixTime;
    public GnssPositionMode mLastPositionMode;
    public final LocationExtras mLocationExtras;
    public final Object mLock;
    public final GpsNetInitiatedHandler mNIHandler;
    public final INetInitiatedListener mNetInitiatedListener;
    public final GnssNetworkConnectivityHandler mNetworkConnectivityHandler;
    public final NetworkTimeHelper mNetworkTimeHelper;
    @GuardedBy({"mLock"})
    public final Set<Integer> mPendingDownloadPsdsTypes;
    public int mPositionMode;
    public ProviderRequest mProviderRequest;
    @GuardedBy({"mLock"})
    public final ExponentialBackOff mPsdsBackOff;
    public final Object mPsdsPeriodicDownloadToken;
    public boolean mShutdown;
    public boolean mStarted;
    public long mStartedChangedElapsedRealtime;
    public boolean mSuplEsEnabled;
    public String mSuplServerHost;
    public int mSuplServerPort;
    public boolean mSupportsPsds;
    public int mTimeToFirstFix;
    public final AlarmManager.OnAlarmListener mTimeoutListener;
    public final PowerManager.WakeLock mWakeLock;
    public final AlarmManager.OnAlarmListener mWakeupListener;
    public static final boolean DEBUG = Log.isLoggable("GnssLocationProvider", 3);
    public static final boolean VERBOSE = Log.isLoggable("GnssLocationProvider", 2);
    public static final ProviderProperties PROPERTIES = new ProviderProperties.Builder().setHasSatelliteRequirement(true).setHasAltitudeSupport(true).setHasSpeedSupport(true).setHasBearingSupport(true).setPowerUsage(3).setAccuracy(1).build();

    public static /* synthetic */ void lambda$handleRequestLocation$2(Location location) {
    }

    public final boolean isRequestLocationRateLimited() {
        return false;
    }

    /* loaded from: classes.dex */
    public static class LocationExtras {
        public final Bundle mBundle = new Bundle();
        public int mMaxCn0;
        public int mMeanCn0;
        public int mSvCount;

        public void set(int i, int i2, int i3) {
            synchronized (this) {
                this.mSvCount = i;
                this.mMeanCn0 = i2;
                this.mMaxCn0 = i3;
            }
            setBundle(this.mBundle);
        }

        public void reset() {
            set(0, 0, 0);
        }

        public void setBundle(Bundle bundle) {
            if (bundle != null) {
                synchronized (this) {
                    bundle.putInt("satellites", this.mSvCount);
                    bundle.putInt("meanCn0", this.mMeanCn0);
                    bundle.putInt("maxCn0", this.mMaxCn0);
                }
            }
        }

        public Bundle getBundle() {
            Bundle bundle;
            synchronized (this) {
                bundle = new Bundle(this.mBundle);
            }
            return bundle;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onUpdateSatelliteBlocklist$0(int[] iArr, int[] iArr2) {
        this.mGnssConfiguration.setSatelliteBlocklist(iArr, iArr2);
    }

    @Override // com.android.server.location.gnss.GnssSatelliteBlocklistHelper.GnssSatelliteBlocklistCallback
    public void onUpdateSatelliteBlocklist(final int[] iArr, final int[] iArr2) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.location.gnss.GnssLocationProvider$$ExternalSyntheticLambda22
            @Override // java.lang.Runnable
            public final void run() {
                GnssLocationProvider.this.lambda$onUpdateSatelliteBlocklist$0(iArr, iArr2);
            }
        });
        this.mGnssMetrics.resetConstellationTypes();
    }

    /* JADX WARN: Removed duplicated region for block: B:23:0x006d  */
    /* JADX WARN: Removed duplicated region for block: B:26:0x007f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void subscriptionOrCarrierConfigChanged() {
        boolean z;
        boolean z2 = DEBUG;
        if (z2) {
            Log.d("GnssLocationProvider", "received SIM related action: ");
        }
        TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) this.mContext.getSystemService("carrier_config");
        int defaultDataSubscriptionId = SubscriptionManager.getDefaultDataSubscriptionId();
        if (SubscriptionManager.isValidSubscriptionId(defaultDataSubscriptionId)) {
            telephonyManager = telephonyManager.createForSubscriptionId(defaultDataSubscriptionId);
        }
        String simOperator = telephonyManager.getSimOperator();
        if (!TextUtils.isEmpty(simOperator)) {
            if (z2) {
                Log.d("GnssLocationProvider", "SIM MCC/MNC is available: " + simOperator);
            }
            if (carrierConfigManager != null) {
                PersistableBundle configForSubId = SubscriptionManager.isValidSubscriptionId(defaultDataSubscriptionId) ? carrierConfigManager.getConfigForSubId(defaultDataSubscriptionId) : null;
                if (configForSubId != null) {
                    z = configForSubId.getBoolean("gps.persist_lpp_mode_bool");
                    if (!z) {
                        this.mGnssConfiguration.loadPropertiesFromCarrierConfig(false, -1);
                        String lppProfile = this.mGnssConfiguration.getLppProfile();
                        if (lppProfile != null) {
                            SystemProperties.set("persist.sys.gps.lpp", lppProfile);
                        }
                    } else {
                        SystemProperties.set("persist.sys.gps.lpp", "");
                    }
                    reloadGpsProperties();
                    return;
                }
            }
            z = false;
            if (!z) {
            }
            reloadGpsProperties();
            return;
        }
        if (z2) {
            Log.d("GnssLocationProvider", "SIM MCC/MNC is still not available");
        }
        this.mGnssConfiguration.reloadGpsProperties();
    }

    public final void reloadGpsProperties() {
        this.mGnssConfiguration.reloadGpsProperties();
        setSuplHostPort();
        this.mC2KServerHost = this.mGnssConfiguration.getC2KHost();
        this.mC2KServerPort = this.mGnssConfiguration.getC2KPort(0);
        this.mNIHandler.setEmergencyExtensionSeconds(this.mGnssConfiguration.getEsExtensionSec());
        boolean z = this.mGnssConfiguration.getSuplEs(0) == 1;
        this.mSuplEsEnabled = z;
        this.mNIHandler.setSuplEsEnabled(z);
        GnssVisibilityControl gnssVisibilityControl = this.mGnssVisibilityControl;
        if (gnssVisibilityControl != null) {
            gnssVisibilityControl.onConfigurationUpdated(this.mGnssConfiguration);
        }
    }

    public GnssLocationProvider(Context context, GnssNative gnssNative, GnssMetrics gnssMetrics) {
        super(FgThread.getExecutor(), CallerIdentity.fromContext(context), PROPERTIES, Collections.emptySet());
        this.mLock = new Object();
        this.mPsdsBackOff = new ExponentialBackOff(BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS, BackupManagerConstants.DEFAULT_KEY_VALUE_BACKUP_INTERVAL_MILLISECONDS);
        this.mFixInterval = 1000;
        this.mFixRequestTime = 0L;
        this.mTimeToFirstFix = 0;
        this.mClientSource = new WorkSource();
        this.mPsdsPeriodicDownloadToken = new Object();
        HashSet hashSet = new HashSet();
        this.mPendingDownloadPsdsTypes = hashSet;
        this.mSuplServerPort = 0;
        this.mSuplEsEnabled = false;
        this.mLocationExtras = new LocationExtras();
        this.mWakeupListener = new AlarmManager.OnAlarmListener() { // from class: com.android.server.location.gnss.GnssLocationProvider$$ExternalSyntheticLambda12
            @Override // android.app.AlarmManager.OnAlarmListener
            public final void onAlarm() {
                GnssLocationProvider.this.startNavigating();
            }
        };
        this.mTimeoutListener = new AlarmManager.OnAlarmListener() { // from class: com.android.server.location.gnss.GnssLocationProvider$$ExternalSyntheticLambda13
            @Override // android.app.AlarmManager.OnAlarmListener
            public final void onAlarm() {
                GnssLocationProvider.this.hibernate();
            }
        };
        this.mFlushListeners = new ArrayList<>(0);
        this.mIntentReceiver = new BroadcastReceiver() { // from class: com.android.server.location.gnss.GnssLocationProvider.4
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                if (GnssLocationProvider.DEBUG) {
                    Log.d("GnssLocationProvider", "receive broadcast intent, action: " + action);
                }
                if (action == null) {
                    return;
                }
                char c = 65535;
                switch (action.hashCode()) {
                    case -1138588223:
                        if (action.equals("android.telephony.action.CARRIER_CONFIG_CHANGED")) {
                            c = 0;
                            break;
                        }
                        break;
                    case -873963303:
                        if (action.equals("android.provider.Telephony.WAP_PUSH_RECEIVED")) {
                            c = 1;
                            break;
                        }
                        break;
                    case -25388475:
                        if (action.equals("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED")) {
                            c = 2;
                            break;
                        }
                        break;
                    case 2142067319:
                        if (action.equals("android.intent.action.DATA_SMS_RECEIVED")) {
                            c = 3;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                    case 2:
                        GnssLocationProvider.this.subscriptionOrCarrierConfigChanged();
                        return;
                    case 1:
                    case 3:
                        GnssLocationProvider.this.injectSuplInit(intent);
                        return;
                    default:
                        return;
                }
            }
        };
        INetInitiatedListener.Stub stub = new INetInitiatedListener.Stub() { // from class: com.android.server.location.gnss.GnssLocationProvider.5
            public boolean sendNiResponse(int i, int i2) {
                if (GnssLocationProvider.DEBUG) {
                    Log.d("GnssLocationProvider", "sendNiResponse, notifId: " + i + ", response: " + i2);
                }
                GnssLocationProvider.this.mGnssNative.sendNiResponse(i, i2);
                FrameworkStatsLog.write(124, 2, i, 0, false, false, false, 0, 0, (String) null, (String) null, 0, 0, GnssLocationProvider.this.mSuplEsEnabled, GnssLocationProvider.this.isGpsEnabled(), i2);
                return true;
            }
        };
        this.mNetInitiatedListener = stub;
        this.mContext = context;
        this.mGnssNative = gnssNative;
        this.mGnssMetrics = gnssMetrics;
        PowerManager powerManager = (PowerManager) context.getSystemService(PowerManager.class);
        Objects.requireNonNull(powerManager);
        PowerManager.WakeLock newWakeLock = powerManager.newWakeLock(1, "*location*:GnssLocationProvider");
        this.mWakeLock = newWakeLock;
        newWakeLock.setReferenceCounted(true);
        PowerManager.WakeLock newWakeLock2 = powerManager.newWakeLock(1, "*location*:PsdsDownload");
        this.mDownloadPsdsWakeLock = newWakeLock2;
        newWakeLock2.setReferenceCounted(true);
        this.mAlarmManager = (AlarmManager) context.getSystemService("alarm");
        this.mAppOps = (AppOpsManager) context.getSystemService(AppOpsManager.class);
        this.mBatteryStats = IBatteryStats.Stub.asInterface(ServiceManager.getService("batterystats"));
        Handler handler = FgThread.getHandler();
        this.mHandler = handler;
        this.mGnssConfiguration = gnssNative.getConfiguration();
        GpsNetInitiatedHandler gpsNetInitiatedHandler = new GpsNetInitiatedHandler(context, stub, new C10651(), this.mSuplEsEnabled);
        this.mNIHandler = gpsNetInitiatedHandler;
        hashSet.add(1);
        this.mNetworkConnectivityHandler = new GnssNetworkConnectivityHandler(context, new GnssNetworkConnectivityHandler.GnssNetworkListener() { // from class: com.android.server.location.gnss.GnssLocationProvider$$ExternalSyntheticLambda14
            @Override // com.android.server.location.gnss.GnssNetworkConnectivityHandler.GnssNetworkListener
            public final void onNetworkAvailable() {
                GnssLocationProvider.this.onNetworkAvailable();
            }
        }, handler.getLooper(), gpsNetInitiatedHandler);
        this.mNetworkTimeHelper = NetworkTimeHelper.create(context, handler.getLooper(), this);
        this.mGnssSatelliteBlocklistHelper = new GnssSatelliteBlocklistHelper(context, handler.getLooper(), this);
        setAllowed(true);
        gnssNative.addBaseCallbacks(this);
        gnssNative.addLocationCallbacks(this);
        gnssNative.addSvStatusCallbacks(this);
        gnssNative.setAGpsCallbacks(this);
        gnssNative.setPsdsCallbacks(this);
        gnssNative.setNotificationCallbacks(this);
        gnssNative.setLocationRequestCallbacks(this);
        gnssNative.setTimeCallbacks(this);
    }

    /* renamed from: com.android.server.location.gnss.GnssLocationProvider$1 */
    /* loaded from: classes.dex */
    public class C10651 implements GpsNetInitiatedHandler.EmergencyCallCallback {
        public C10651() {
        }

        public void onEmergencyCallStart(final int i) {
            if (GnssLocationProvider.this.mGnssConfiguration.isActiveSimEmergencySuplEnabled()) {
                GnssLocationProvider.this.mHandler.post(new Runnable() { // from class: com.android.server.location.gnss.GnssLocationProvider$1$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        GnssLocationProvider.C10651.this.lambda$onEmergencyCallStart$0(i);
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onEmergencyCallStart$0(int i) {
            GnssLocationProvider.this.mGnssConfiguration.reloadGpsProperties(GnssLocationProvider.this.mNIHandler.getInEmergency(), i);
        }

        public void onEmergencyCallEnd() {
            if (GnssLocationProvider.this.mGnssConfiguration.isActiveSimEmergencySuplEnabled()) {
                GnssLocationProvider.this.mHandler.postDelayed(new Runnable() { // from class: com.android.server.location.gnss.GnssLocationProvider$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        GnssLocationProvider.C10651.this.lambda$onEmergencyCallEnd$1();
                    }
                }, TimeUnit.SECONDS.toMillis(GnssLocationProvider.this.mGnssConfiguration.getEsExtensionSec()));
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onEmergencyCallEnd$1() {
            GnssLocationProvider.this.mGnssConfiguration.reloadGpsProperties(false, SubscriptionManager.getDefaultDataSubscriptionId());
        }
    }

    public synchronized void onSystemReady() {
        this.mContext.registerReceiverAsUser(new BroadcastReceiver() { // from class: com.android.server.location.gnss.GnssLocationProvider.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                if (getSendingUserId() == -1) {
                    GnssLocationProvider.this.mShutdown = true;
                    GnssLocationProvider.this.updateEnabled();
                }
            }
        }, UserHandle.ALL, new IntentFilter("android.intent.action.ACTION_SHUTDOWN"), null, this.mHandler);
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("location_mode"), true, new ContentObserver(this.mHandler) { // from class: com.android.server.location.gnss.GnssLocationProvider.3
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                GnssLocationProvider.this.updateEnabled();
            }
        }, -1);
        this.mHandler.post(new Runnable() { // from class: com.android.server.location.gnss.GnssLocationProvider$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                GnssLocationProvider.this.handleInitialize();
            }
        });
        Handler handler = this.mHandler;
        final GnssSatelliteBlocklistHelper gnssSatelliteBlocklistHelper = this.mGnssSatelliteBlocklistHelper;
        Objects.requireNonNull(gnssSatelliteBlocklistHelper);
        handler.post(new Runnable() { // from class: com.android.server.location.gnss.GnssLocationProvider$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                GnssSatelliteBlocklistHelper.this.updateSatelliteBlocklist();
            }
        });
    }

    public final void handleInitialize() {
        if (this.mGnssNative.isGnssVisibilityControlSupported()) {
            this.mGnssVisibilityControl = new GnssVisibilityControl(this.mContext, this.mHandler.getLooper(), this.mNIHandler);
        }
        reloadGpsProperties();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.telephony.action.CARRIER_CONFIG_CHANGED");
        intentFilter.addAction("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED");
        this.mContext.registerReceiver(this.mIntentReceiver, intentFilter, null, this.mHandler);
        if (this.mNetworkConnectivityHandler.isNativeAgpsRilSupported() && this.mGnssConfiguration.isNiSuplMessageInjectionEnabled()) {
            IntentFilter intentFilter2 = new IntentFilter();
            intentFilter2.addAction("android.provider.Telephony.WAP_PUSH_RECEIVED");
            try {
                intentFilter2.addDataType("application/vnd.omaloc-supl-init");
            } catch (IntentFilter.MalformedMimeTypeException unused) {
                Log.w("GnssLocationProvider", "Malformed SUPL init mime type");
            }
            this.mContext.registerReceiver(this.mIntentReceiver, intentFilter2, null, this.mHandler);
            IntentFilter intentFilter3 = new IntentFilter();
            intentFilter3.addAction("android.intent.action.DATA_SMS_RECEIVED");
            intentFilter3.addDataScheme("sms");
            intentFilter3.addDataAuthority("localhost", "7275");
            this.mContext.registerReceiver(this.mIntentReceiver, intentFilter3, null, this.mHandler);
        }
        this.mNetworkConnectivityHandler.registerNetworkCallbacks();
        LocationManager locationManager = (LocationManager) this.mContext.getSystemService(LocationManager.class);
        Objects.requireNonNull(locationManager);
        if (locationManager.getAllProviders().contains("network")) {
            locationManager.requestLocationUpdates("network", new LocationRequest.Builder(Long.MAX_VALUE).setMinUpdateIntervalMillis(0L).setHiddenFromAppOps(true).build(), ConcurrentUtils.DIRECT_EXECUTOR, new LocationListener() { // from class: com.android.server.location.gnss.GnssLocationProvider$$ExternalSyntheticLambda16
                @Override // android.location.LocationListener
                public final void onLocationChanged(Location location) {
                    GnssLocationProvider.this.injectLocation(location);
                }
            });
        }
        updateEnabled();
    }

    public final void injectSuplInit(Intent intent) {
        if (!isNfwLocationAccessAllowed()) {
            Log.w("GnssLocationProvider", "Reject SUPL INIT as no NFW location access");
            return;
        }
        int intExtra = intent.getIntExtra("android.telephony.extra.SLOT_INDEX", -1);
        if (intExtra == -1) {
            Log.e("GnssLocationProvider", "Invalid slot index");
            return;
        }
        String action = intent.getAction();
        if (action.equals("android.intent.action.DATA_SMS_RECEIVED")) {
            SmsMessage[] messagesFromIntent = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            if (messagesFromIntent == null) {
                Log.e("GnssLocationProvider", "Message does not exist in the intent");
                return;
            }
            for (SmsMessage smsMessage : messagesFromIntent) {
                injectSuplInit(smsMessage.getUserData(), intExtra);
            }
        } else if (action.equals("android.provider.Telephony.WAP_PUSH_RECEIVED")) {
            injectSuplInit(intent.getByteArrayExtra("data"), intExtra);
        }
    }

    public final void injectSuplInit(byte[] bArr, int i) {
        if (bArr != null) {
            if (DEBUG) {
                Log.d("GnssLocationProvider", "suplInit = " + HexDump.toHexString(bArr) + " slotIndex = " + i);
            }
            this.mGnssNative.injectNiSuplMessageData(bArr, bArr.length, i);
        }
    }

    public final boolean isNfwLocationAccessAllowed() {
        if (this.mGnssNative.isInEmergencySession()) {
            return true;
        }
        GnssVisibilityControl gnssVisibilityControl = this.mGnssVisibilityControl;
        return gnssVisibilityControl != null && gnssVisibilityControl.hasLocationPermissionEnabledProxyApps();
    }

    @Override // com.android.server.location.gnss.NetworkTimeHelper.InjectTimeCallback
    public void injectTime(long j, long j2, int i) {
        this.mGnssNative.injectTime(j, j2, i);
    }

    public final void onNetworkAvailable() {
        this.mNetworkTimeHelper.onNetworkAvailable();
        if (this.mSupportsPsds) {
            synchronized (this.mLock) {
                for (Integer num : this.mPendingDownloadPsdsTypes) {
                    final int intValue = num.intValue();
                    postWithWakeLockHeld(new Runnable() { // from class: com.android.server.location.gnss.GnssLocationProvider$$ExternalSyntheticLambda17
                        @Override // java.lang.Runnable
                        public final void run() {
                            GnssLocationProvider.this.lambda$onNetworkAvailable$1(intValue);
                        }
                    });
                }
                this.mPendingDownloadPsdsTypes.clear();
            }
        }
    }

    /* renamed from: handleRequestLocation */
    public final void lambda$onRequestLocation$15(boolean z, boolean z2) {
        LocationListener locationListener;
        String str;
        if (isRequestLocationRateLimited()) {
            if (DEBUG) {
                Log.d("GnssLocationProvider", "RequestLocation is denied due to too frequent requests.");
                return;
            }
            return;
        }
        long j = Settings.Global.getLong(this.mContext.getContentResolver(), "gnss_hal_location_request_duration_millis", 10000L);
        if (j == 0) {
            Log.i("GnssLocationProvider", "GNSS HAL location request is disabled by Settings.");
            return;
        }
        LocationManager locationManager = (LocationManager) this.mContext.getSystemService("location");
        LocationRequest.Builder maxUpdates = new LocationRequest.Builder(1000L).setMaxUpdates(1);
        if (z) {
            locationListener = new LocationListener() { // from class: com.android.server.location.gnss.GnssLocationProvider$$ExternalSyntheticLambda19
                @Override // android.location.LocationListener
                public final void onLocationChanged(Location location) {
                    GnssLocationProvider.lambda$handleRequestLocation$2(location);
                }
            };
            maxUpdates.setQuality(104);
            str = "network";
        } else {
            locationListener = new LocationListener() { // from class: com.android.server.location.gnss.GnssLocationProvider$$ExternalSyntheticLambda20
                @Override // android.location.LocationListener
                public final void onLocationChanged(Location location) {
                    GnssLocationProvider.this.injectBestLocation(location);
                }
            };
            maxUpdates.setQuality(100);
            str = "fused";
        }
        if (this.mNIHandler.getInEmergency()) {
            GnssConfiguration.HalInterfaceVersion halInterfaceVersion = this.mGnssConfiguration.getHalInterfaceVersion();
            if (z2 || halInterfaceVersion.mMajor < 2) {
                maxUpdates.setLocationSettingsIgnored(true);
                j *= 3;
            }
        }
        maxUpdates.setDurationMillis(j);
        Log.i("GnssLocationProvider", String.format("GNSS HAL Requesting location updates from %s provider for %d millis.", str, Long.valueOf(j)));
        if (locationManager.getProvider(str) != null) {
            locationManager.requestLocationUpdates(str, maxUpdates.build(), ConcurrentUtils.DIRECT_EXECUTOR, locationListener);
        }
    }

    public final void injectBestLocation(Location location) {
        if (DEBUG) {
            Log.d("GnssLocationProvider", "injectBestLocation: " + location);
        }
        if (location.isMock()) {
            return;
        }
        this.mGnssNative.injectBestLocation(location);
    }

    /* renamed from: handleDownloadPsdsData */
    public final void lambda$onRequestPsdsDownload$14(final int i) {
        if (!this.mSupportsPsds) {
            Log.d("GnssLocationProvider", "handleDownloadPsdsData() called when PSDS not supported");
        } else if (!this.mNetworkConnectivityHandler.isDataNetworkConnected()) {
            synchronized (this.mLock) {
                this.mPendingDownloadPsdsTypes.add(Integer.valueOf(i));
            }
        } else {
            synchronized (this.mLock) {
                this.mDownloadPsdsWakeLock.acquire(60000L);
            }
            Log.i("GnssLocationProvider", "WakeLock acquired by handleDownloadPsdsData()");
            Executors.newSingleThreadExecutor().execute(new Runnable() { // from class: com.android.server.location.gnss.GnssLocationProvider$$ExternalSyntheticLambda18
                @Override // java.lang.Runnable
                public final void run() {
                    GnssLocationProvider.this.lambda$handleDownloadPsdsData$6(i);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handleDownloadPsdsData$6(final int i) {
        long nextBackoffMillis;
        final byte[] downloadPsdsData = new GnssPsdsDownloader(this.mGnssConfiguration.getProperties()).downloadPsdsData(i);
        if (downloadPsdsData != null) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.location.gnss.GnssLocationProvider$$ExternalSyntheticLambda24
                @Override // java.lang.Runnable
                public final void run() {
                    GnssLocationProvider.this.lambda$handleDownloadPsdsData$3(i, downloadPsdsData);
                }
            });
            PackageManager packageManager = this.mContext.getPackageManager();
            if (packageManager != null && packageManager.hasSystemFeature("android.hardware.type.watch") && i == 1 && this.mGnssConfiguration.isPsdsPeriodicDownloadEnabled()) {
                if (DEBUG) {
                    Log.d("GnssLocationProvider", "scheduling next long term Psds download");
                }
                this.mHandler.removeCallbacksAndMessages(this.mPsdsPeriodicDownloadToken);
                this.mHandler.postDelayed(new Runnable() { // from class: com.android.server.location.gnss.GnssLocationProvider$$ExternalSyntheticLambda25
                    @Override // java.lang.Runnable
                    public final void run() {
                        GnssLocationProvider.this.lambda$handleDownloadPsdsData$4(i);
                    }
                }, this.mPsdsPeriodicDownloadToken, BackupManagerConstants.DEFAULT_FULL_BACKUP_INTERVAL_MILLISECONDS);
            }
        } else {
            synchronized (this.mLock) {
                nextBackoffMillis = this.mPsdsBackOff.nextBackoffMillis();
            }
            this.mHandler.postDelayed(new Runnable() { // from class: com.android.server.location.gnss.GnssLocationProvider$$ExternalSyntheticLambda26
                @Override // java.lang.Runnable
                public final void run() {
                    GnssLocationProvider.this.lambda$handleDownloadPsdsData$5(i);
                }
            }, nextBackoffMillis);
        }
        synchronized (this.mLock) {
            if (this.mDownloadPsdsWakeLock.isHeld()) {
                this.mDownloadPsdsWakeLock.release();
                if (DEBUG) {
                    Log.d("GnssLocationProvider", "WakeLock released by handleDownloadPsdsData()");
                }
            } else {
                Log.e("GnssLocationProvider", "WakeLock expired before release in handleDownloadPsdsData()");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handleDownloadPsdsData$3(int i, byte[] bArr) {
        FrameworkStatsLog.write((int) FrameworkStatsLog.GNSS_PSDS_DOWNLOAD_REPORTED, i);
        if (DEBUG) {
            Log.d("GnssLocationProvider", "calling native_inject_psds_data");
        }
        this.mGnssNative.injectPsdsData(bArr, bArr.length, i);
        synchronized (this.mLock) {
            this.mPsdsBackOff.reset();
        }
    }

    public final void injectLocation(Location location) {
        if (location.isMock()) {
            return;
        }
        this.mGnssNative.injectLocation(location);
    }

    public final void setSuplHostPort() {
        this.mSuplServerHost = this.mGnssConfiguration.getSuplHost();
        int suplPort = this.mGnssConfiguration.getSuplPort(0);
        this.mSuplServerPort = suplPort;
        String str = this.mSuplServerHost;
        if (str == null || suplPort <= 0 || suplPort > 65535) {
            return;
        }
        this.mGnssNative.setAgpsServer(1, str, suplPort);
    }

    public final int getSuplMode(boolean z) {
        int suplMode;
        return (!z || (suplMode = this.mGnssConfiguration.getSuplMode(0)) == 0 || !this.mGnssNative.getCapabilities().hasMsb() || (suplMode & 1) == 0) ? 0 : 1;
    }

    public final void setGpsEnabled(boolean z) {
        synchronized (this.mLock) {
            this.mGpsEnabled = z;
        }
    }

    public void setAutomotiveGnssSuspended(boolean z) {
        synchronized (this.mLock) {
            this.mAutomotiveSuspend = z;
        }
        this.mHandler.post(new Runnable() { // from class: com.android.server.location.gnss.GnssLocationProvider$$ExternalSyntheticLambda15
            @Override // java.lang.Runnable
            public final void run() {
                GnssLocationProvider.this.updateEnabled();
            }
        });
    }

    public boolean isAutomotiveGnssSuspended() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mAutomotiveSuspend && !this.mGpsEnabled;
        }
        return z;
    }

    public final void handleEnable() {
        if (DEBUG) {
            Log.d("GnssLocationProvider", "handleEnable");
        }
        boolean z = false;
        if (this.mGnssNative.init()) {
            setGpsEnabled(true);
            this.mSupportsPsds = this.mGnssNative.isPsdsSupported();
            String str = this.mSuplServerHost;
            if (str != null) {
                this.mGnssNative.setAgpsServer(1, str, this.mSuplServerPort);
            }
            String str2 = this.mC2KServerHost;
            if (str2 != null) {
                this.mGnssNative.setAgpsServer(2, str2, this.mC2KServerPort);
            }
            if (this.mGnssNative.initBatching() && this.mGnssNative.getBatchSize() > 1) {
                z = true;
            }
            this.mBatchingEnabled = z;
            GnssVisibilityControl gnssVisibilityControl = this.mGnssVisibilityControl;
            if (gnssVisibilityControl != null) {
                gnssVisibilityControl.onGpsEnabledChanged(true);
                return;
            }
            return;
        }
        setGpsEnabled(false);
        Log.w("GnssLocationProvider", "Failed to enable location provider");
    }

    public final void handleDisable() {
        if (DEBUG) {
            Log.d("GnssLocationProvider", "handleDisable");
        }
        setGpsEnabled(false);
        updateClientUids(new WorkSource());
        stopNavigating();
        stopBatching();
        GnssVisibilityControl gnssVisibilityControl = this.mGnssVisibilityControl;
        if (gnssVisibilityControl != null) {
            gnssVisibilityControl.onGpsEnabledChanged(false);
        }
        this.mGnssNative.cleanupBatching();
        this.mGnssNative.cleanup();
    }

    public final void updateEnabled() {
        boolean z;
        LocationManager locationManager = (LocationManager) this.mContext.getSystemService(LocationManager.class);
        boolean z2 = false;
        for (UserHandle userHandle : ((UserManager) this.mContext.getSystemService(UserManager.class)).getVisibleUsers()) {
            z2 |= locationManager.isLocationEnabledForUser(userHandle);
        }
        ProviderRequest providerRequest = this.mProviderRequest;
        boolean z3 = (providerRequest != null && providerRequest.isActive() && this.mProviderRequest.isBypass()) | z2;
        synchronized (this.mLock) {
            z = z3 & (this.mAutomotiveSuspend ? false : true);
        }
        boolean z4 = z & (true ^ this.mShutdown);
        if (z4 == isGpsEnabled()) {
            return;
        }
        if (z4) {
            handleEnable();
        } else {
            handleDisable();
        }
    }

    public final boolean isGpsEnabled() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mGpsEnabled;
        }
        return z;
    }

    public int getBatchSize() {
        return this.mGnssNative.getBatchSize();
    }

    @Override // com.android.server.location.provider.AbstractLocationProvider
    public void onFlush(Runnable runnable) {
        boolean add;
        synchronized (this.mLock) {
            add = this.mBatchingEnabled ? this.mFlushListeners.add(runnable) : false;
        }
        if (!add) {
            runnable.run();
        } else {
            this.mGnssNative.flushBatch();
        }
    }

    @Override // com.android.server.location.provider.AbstractLocationProvider
    public void onSetRequest(ProviderRequest providerRequest) {
        this.mProviderRequest = providerRequest;
        updateEnabled();
        updateRequirements();
    }

    public final void updateRequirements() {
        ProviderRequest providerRequest = this.mProviderRequest;
        if (providerRequest == null || providerRequest.getWorkSource() == null) {
            return;
        }
        if (DEBUG) {
            Log.d("GnssLocationProvider", "setRequest " + this.mProviderRequest);
        }
        if (this.mProviderRequest.isActive() && isGpsEnabled()) {
            updateClientUids(this.mProviderRequest.getWorkSource());
            if (this.mProviderRequest.getIntervalMillis() <= 2147483647L) {
                this.mFixInterval = (int) this.mProviderRequest.getIntervalMillis();
            } else {
                Log.w("GnssLocationProvider", "interval overflow: " + this.mProviderRequest.getIntervalMillis());
                this.mFixInterval = Integer.MAX_VALUE;
            }
            int max = Math.max(this.mFixInterval, 1000);
            long min = Math.min(this.mProviderRequest.getMaxUpdateDelayMillis(), (long) BackupManagerConstants.DEFAULT_FULL_BACKUP_INTERVAL_MILLISECONDS);
            if (this.mBatchingEnabled && min / 2 >= max) {
                stopNavigating();
                this.mFixInterval = max;
                startBatching(min);
                return;
            }
            stopBatching();
            if (this.mStarted && this.mGnssNative.getCapabilities().hasScheduling()) {
                if (setPositionMode(this.mPositionMode, 0, this.mFixInterval, this.mProviderRequest.isLowPower())) {
                    return;
                }
                Log.e("GnssLocationProvider", "set_position_mode failed in updateRequirements");
                return;
            } else if (!this.mStarted) {
                startNavigating();
                return;
            } else {
                this.mAlarmManager.cancel(this.mTimeoutListener);
                if (this.mFixInterval >= 60000) {
                    this.mAlarmManager.set(2, 60000 + SystemClock.elapsedRealtime(), "GnssLocationProvider", this.mTimeoutListener, this.mHandler);
                    return;
                }
                return;
            }
        }
        updateClientUids(new WorkSource());
        stopNavigating();
        stopBatching();
    }

    public final boolean setPositionMode(int i, int i2, int i3, boolean z) {
        GnssPositionMode gnssPositionMode = new GnssPositionMode(i, i2, i3, 0, 0, z);
        GnssPositionMode gnssPositionMode2 = this.mLastPositionMode;
        if (gnssPositionMode2 == null || !gnssPositionMode2.equals(gnssPositionMode)) {
            boolean positionMode = this.mGnssNative.setPositionMode(i, i2, i3, 0, 0, z);
            if (positionMode) {
                this.mLastPositionMode = gnssPositionMode;
            } else {
                this.mLastPositionMode = null;
            }
            return positionMode;
        }
        return true;
    }

    public final void updateClientUids(WorkSource workSource) {
        if (workSource.equals(this.mClientSource)) {
            return;
        }
        try {
            this.mBatteryStats.noteGpsChanged(this.mClientSource, workSource);
        } catch (RemoteException e) {
            Log.w("GnssLocationProvider", "RemoteException", e);
        }
        ArrayList[] diffChains = WorkSource.diffChains(this.mClientSource, workSource);
        if (diffChains != null) {
            ArrayList<WorkSource.WorkChain> arrayList = diffChains[0];
            ArrayList<WorkSource.WorkChain> arrayList2 = diffChains[1];
            if (arrayList != null) {
                for (WorkSource.WorkChain workChain : arrayList) {
                    this.mAppOps.startOpNoThrow(2, workChain.getAttributionUid(), workChain.getAttributionTag());
                }
            }
            if (arrayList2 != null) {
                for (WorkSource.WorkChain workChain2 : arrayList2) {
                    this.mAppOps.finishOp(2, workChain2.getAttributionUid(), workChain2.getAttributionTag());
                }
            }
            this.mClientSource.transferWorkChains(workSource);
        }
        WorkSource[] returningDiffs = this.mClientSource.setReturningDiffs(workSource);
        if (returningDiffs != null) {
            WorkSource workSource2 = returningDiffs[0];
            WorkSource workSource3 = returningDiffs[1];
            if (workSource2 != null) {
                for (int i = 0; i < workSource2.size(); i++) {
                    this.mAppOps.startOpNoThrow(2, workSource2.getUid(i), workSource2.getPackageName(i));
                }
            }
            if (workSource3 != null) {
                for (int i2 = 0; i2 < workSource3.size(); i2++) {
                    this.mAppOps.finishOp(2, workSource3.getUid(i2), workSource3.getPackageName(i2));
                }
            }
        }
    }

    @Override // com.android.server.location.provider.AbstractLocationProvider
    public void onExtraCommand(int i, int i2, String str, Bundle bundle) {
        if ("delete_aiding_data".equals(str)) {
            deleteAidingData(bundle);
        } else if ("force_time_injection".equals(str)) {
            demandUtcTimeInjection();
        } else if ("force_psds_injection".equals(str)) {
            if (this.mSupportsPsds) {
                postWithWakeLockHeld(new Runnable() { // from class: com.android.server.location.gnss.GnssLocationProvider$$ExternalSyntheticLambda23
                    @Override // java.lang.Runnable
                    public final void run() {
                        GnssLocationProvider.this.lambda$onExtraCommand$7();
                    }
                });
            }
        } else if ("request_power_stats".equals(str)) {
            this.mGnssNative.requestPowerStats();
        } else {
            Log.w("GnssLocationProvider", "sendExtraCommand: unknown command " + str);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onExtraCommand$7() {
        lambda$onRequestPsdsDownload$14(1);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v0 */
    /* JADX WARN: Type inference failed for: r0v1, types: [int] */
    /* JADX WARN: Type inference failed for: r0v2 */
    /* JADX WARN: Type inference failed for: r0v4 */
    /* JADX WARN: Type inference failed for: r3v1, types: [com.android.server.location.gnss.hal.GnssNative] */
    public final void deleteAidingData(Bundle bundle) {
        ?? r0 = 65535;
        if (bundle != null) {
            boolean z = bundle.getBoolean("ephemeris");
            if (bundle.getBoolean("almanac")) {
                z |= true;
            }
            if (bundle.getBoolean("position")) {
                z |= true;
            }
            if (bundle.getBoolean("time")) {
                z |= true;
            }
            if (bundle.getBoolean("iono")) {
                z |= true;
            }
            if (bundle.getBoolean("utc")) {
                z |= true;
            }
            if (bundle.getBoolean("health")) {
                z |= true;
            }
            if (bundle.getBoolean("svdir")) {
                z |= true;
            }
            if (bundle.getBoolean("svsteer")) {
                z |= true;
            }
            if (bundle.getBoolean("sadata")) {
                z |= true;
            }
            if (bundle.getBoolean("rti")) {
                z |= true;
            }
            if (bundle.getBoolean("celldb-info")) {
                z |= true;
            }
            r0 = bundle.getBoolean("all") ? true | z : z;
        }
        if (r0 != 0) {
            this.mGnssNative.deleteAidingData(r0);
        }
    }

    public final void startNavigating() {
        if (this.mStarted) {
            return;
        }
        boolean z = DEBUG;
        if (z) {
            Log.d("GnssLocationProvider", "startNavigating");
        }
        this.mTimeToFirstFix = 0;
        this.mLastFixTime = 0L;
        setStarted(true);
        this.mPositionMode = 0;
        int suplMode = getSuplMode(Settings.Global.getInt(this.mContext.getContentResolver(), "assisted_gps_enabled", 1) != 0);
        this.mPositionMode = suplMode;
        if (z) {
            String str = suplMode != 0 ? suplMode != 1 ? suplMode != 2 ? "unknown" : "MS_ASSISTED" : "MS_BASED" : "standalone";
            Log.d("GnssLocationProvider", "setting position_mode to " + str);
        }
        if (!setPositionMode(this.mPositionMode, 0, this.mGnssNative.getCapabilities().hasScheduling() ? this.mFixInterval : 1000, this.mProviderRequest.isLowPower())) {
            setStarted(false);
            Log.e("GnssLocationProvider", "set_position_mode failed in startNavigating()");
        } else if (!this.mGnssNative.start()) {
            setStarted(false);
            Log.e("GnssLocationProvider", "native_start failed in startNavigating()");
        } else {
            this.mLocationExtras.reset();
            this.mFixRequestTime = SystemClock.elapsedRealtime();
            if (this.mGnssNative.getCapabilities().hasScheduling() || this.mFixInterval < 60000) {
                return;
            }
            this.mAlarmManager.set(2, 60000 + SystemClock.elapsedRealtime(), "GnssLocationProvider", this.mTimeoutListener, this.mHandler);
        }
    }

    public final void stopNavigating() {
        if (DEBUG) {
            Log.d("GnssLocationProvider", "stopNavigating");
        }
        if (this.mStarted) {
            setStarted(false);
            this.mGnssNative.stop();
            this.mLastFixTime = 0L;
            this.mLastPositionMode = null;
            this.mLocationExtras.reset();
        }
        this.mAlarmManager.cancel(this.mTimeoutListener);
        this.mAlarmManager.cancel(this.mWakeupListener);
    }

    public final void startBatching(final long j) {
        long j2 = j / this.mFixInterval;
        if (DEBUG) {
            Log.d("GnssLocationProvider", "startBatching " + this.mFixInterval + " " + j);
        }
        if (this.mGnssNative.startBatch(TimeUnit.MILLISECONDS.toNanos(this.mFixInterval), 0.0f, true)) {
            this.mBatchingStarted = true;
            if (j2 < getBatchSize()) {
                this.mBatchingAlarm = new AlarmManager.OnAlarmListener() { // from class: com.android.server.location.gnss.GnssLocationProvider$$ExternalSyntheticLambda1
                    @Override // android.app.AlarmManager.OnAlarmListener
                    public final void onAlarm() {
                        GnssLocationProvider.this.lambda$startBatching$8(j);
                    }
                };
                this.mAlarmManager.setExact(2, SystemClock.elapsedRealtime() + j, "GnssLocationProvider", this.mBatchingAlarm, FgThread.getHandler());
                return;
            }
            return;
        }
        Log.e("GnssLocationProvider", "native_start_batch failed in startBatching()");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startBatching$8(long j) {
        boolean z;
        synchronized (this.mLock) {
            if (this.mBatchingAlarm != null) {
                this.mAlarmManager.setExact(2, SystemClock.elapsedRealtime() + j, "GnssLocationProvider", this.mBatchingAlarm, FgThread.getHandler());
                z = true;
            } else {
                z = false;
            }
        }
        if (z) {
            this.mGnssNative.flushBatch();
        }
    }

    public final void stopBatching() {
        if (DEBUG) {
            Log.d("GnssLocationProvider", "stopBatching");
        }
        if (this.mBatchingStarted) {
            AlarmManager.OnAlarmListener onAlarmListener = this.mBatchingAlarm;
            if (onAlarmListener != null) {
                this.mAlarmManager.cancel(onAlarmListener);
                this.mBatchingAlarm = null;
            }
            this.mGnssNative.flushBatch();
            this.mGnssNative.stopBatch();
            this.mBatchingStarted = false;
        }
    }

    public final void setStarted(boolean z) {
        if (this.mStarted != z) {
            this.mStarted = z;
            this.mStartedChangedElapsedRealtime = SystemClock.elapsedRealtime();
        }
    }

    public final void hibernate() {
        stopNavigating();
        this.mAlarmManager.set(2, this.mFixInterval + SystemClock.elapsedRealtime(), "GnssLocationProvider", this.mWakeupListener, this.mHandler);
    }

    /* renamed from: handleReportLocation */
    public final void lambda$onReportLocation$12(boolean z, Location location) {
        if (VERBOSE) {
            Log.v("GnssLocationProvider", "reportLocation " + location.toString());
        }
        location.setExtras(this.mLocationExtras.getBundle());
        reportLocation(LocationResult.wrap(new Location[]{location}).validate());
        if (this.mStarted) {
            this.mGnssMetrics.logReceivedLocationStatus(z);
            if (z) {
                if (location.hasAccuracy()) {
                    this.mGnssMetrics.logPositionAccuracyMeters(location.getAccuracy());
                }
                if (this.mTimeToFirstFix > 0) {
                    this.mGnssMetrics.logMissedReports(this.mFixInterval, (int) (SystemClock.elapsedRealtime() - this.mLastFixTime));
                }
            }
        } else {
            long elapsedRealtime = SystemClock.elapsedRealtime() - this.mStartedChangedElapsedRealtime;
            if (elapsedRealtime > 2000) {
                String str = "Unexpected GNSS Location report " + TimeUtils.formatDuration(elapsedRealtime) + " after location turned off";
                if (elapsedRealtime > 15000) {
                    Log.e("GnssLocationProvider", str);
                } else {
                    Log.w("GnssLocationProvider", str);
                }
            }
        }
        long elapsedRealtime2 = SystemClock.elapsedRealtime();
        this.mLastFixTime = elapsedRealtime2;
        if (this.mTimeToFirstFix == 0 && z) {
            this.mTimeToFirstFix = (int) (elapsedRealtime2 - this.mFixRequestTime);
            if (DEBUG) {
                Log.d("GnssLocationProvider", "TTFF: " + this.mTimeToFirstFix);
            }
            if (this.mStarted) {
                this.mGnssMetrics.logTimeToFirstFixMilliSecs(this.mTimeToFirstFix);
            }
        }
        if (this.mStarted && !this.mGnssNative.getCapabilities().hasScheduling() && this.mFixInterval < 60000) {
            this.mAlarmManager.cancel(this.mTimeoutListener);
        }
        if (this.mGnssNative.getCapabilities().hasScheduling() || !this.mStarted || this.mFixInterval <= 10000) {
            return;
        }
        if (DEBUG) {
            Log.d("GnssLocationProvider", "got fix, hibernating");
        }
        hibernate();
    }

    /* renamed from: handleReportSvStatus */
    public final void lambda$onReportSvStatus$13(GnssStatus gnssStatus) {
        this.mGnssMetrics.logCn0(gnssStatus);
        if (VERBOSE) {
            Log.v("GnssLocationProvider", "SV count: " + gnssStatus.getSatelliteCount());
        }
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        for (int i4 = 0; i4 < gnssStatus.getSatelliteCount(); i4++) {
            if (gnssStatus.usedInFix(i4)) {
                i++;
                if (gnssStatus.getCn0DbHz(i4) > i3) {
                    i3 = (int) gnssStatus.getCn0DbHz(i4);
                }
                i2 = (int) (i2 + gnssStatus.getCn0DbHz(i4));
                this.mGnssMetrics.logConstellationType(gnssStatus.getConstellationType(i4));
            }
        }
        if (i > 0) {
            i2 /= i;
        }
        this.mLocationExtras.set(i, i2, i3);
        this.mGnssMetrics.logSvStatus(gnssStatus);
    }

    public final void restartLocationRequest() {
        if (DEBUG) {
            Log.d("GnssLocationProvider", "restartLocationRequest");
        }
        setStarted(false);
        updateRequirements();
    }

    public INetInitiatedListener getNetInitiatedListener() {
        return this.mNetInitiatedListener;
    }

    public final void reportNiNotification(int i, int i2, int i3, int i4, int i5, String str, String str2, int i6, int i7) {
        Log.i("GnssLocationProvider", "reportNiNotification: entered");
        Log.i("GnssLocationProvider", "notificationId: " + i + ", niType: " + i2 + ", notifyFlags: " + i3 + ", timeout: " + i4 + ", defaultResponse: " + i5);
        StringBuilder sb = new StringBuilder();
        sb.append("requestorId: ");
        sb.append(str);
        sb.append(", text: ");
        sb.append(str2);
        sb.append(", requestorIdEncoding: ");
        sb.append(i6);
        sb.append(", textEncoding: ");
        sb.append(i7);
        Log.i("GnssLocationProvider", sb.toString());
        GpsNetInitiatedHandler.GpsNiNotification gpsNiNotification = new GpsNetInitiatedHandler.GpsNiNotification();
        gpsNiNotification.notificationId = i;
        gpsNiNotification.niType = i2;
        gpsNiNotification.needNotify = (i3 & 1) != 0;
        gpsNiNotification.needVerify = (i3 & 2) != 0;
        gpsNiNotification.privacyOverride = (i3 & 4) != 0;
        gpsNiNotification.timeout = i4;
        gpsNiNotification.defaultResponse = i5;
        gpsNiNotification.requestorId = str;
        gpsNiNotification.text = str2;
        gpsNiNotification.requestorIdEncoding = i6;
        gpsNiNotification.textEncoding = i7;
        this.mNIHandler.handleNiNotification(gpsNiNotification);
        FrameworkStatsLog.write(124, 1, gpsNiNotification.notificationId, gpsNiNotification.niType, gpsNiNotification.needNotify, gpsNiNotification.needVerify, gpsNiNotification.privacyOverride, gpsNiNotification.timeout, gpsNiNotification.defaultResponse, gpsNiNotification.requestorId, gpsNiNotification.text, gpsNiNotification.requestorIdEncoding, gpsNiNotification.textEncoding, this.mSuplEsEnabled, isGpsEnabled(), 0);
    }

    public final void demandUtcTimeInjection() {
        if (DEBUG) {
            Log.d("GnssLocationProvider", "demandUtcTimeInjection");
        }
        final NetworkTimeHelper networkTimeHelper = this.mNetworkTimeHelper;
        Objects.requireNonNull(networkTimeHelper);
        postWithWakeLockHeld(new Runnable() { // from class: com.android.server.location.gnss.GnssLocationProvider$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                NetworkTimeHelper.this.demandUtcTimeInjection();
            }
        });
    }

    public static int getCellType(CellInfo cellInfo) {
        if (cellInfo instanceof CellInfoGsm) {
            return 1;
        }
        if (cellInfo instanceof CellInfoWcdma) {
            return 4;
        }
        if (cellInfo instanceof CellInfoLte) {
            return 3;
        }
        return cellInfo instanceof CellInfoNr ? 6 : 0;
    }

    /* JADX WARN: Removed duplicated region for block: B:20:0x003d  */
    /* JADX WARN: Removed duplicated region for block: B:21:0x0043  */
    /* JADX WARN: Removed duplicated region for block: B:25:0x004b  */
    /* JADX WARN: Removed duplicated region for block: B:27:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static long getCidFromCellIdentity(CellIdentity cellIdentity) {
        int cid;
        long j;
        if (cellIdentity == null) {
            return -1L;
        }
        int type = cellIdentity.getType();
        if (type != 1) {
            if (type == 6) {
                j = ((CellIdentityNr) cellIdentity).getNci();
            } else if (type == 3) {
                cid = ((CellIdentityLte) cellIdentity).getCi();
            } else if (type != 4) {
                j = -1;
            } else {
                cid = ((CellIdentityWcdma) cellIdentity).getCid();
            }
            if (j != (cellIdentity.getType() != 6 ? Long.MAX_VALUE : 2147483647L)) {
                return -1L;
            }
            return j;
        }
        cid = ((CellIdentityGsm) cellIdentity).getCid();
        j = cid;
        if (j != (cellIdentity.getType() != 6 ? Long.MAX_VALUE : 2147483647L)) {
        }
    }

    public final void setRefLocation(int i, CellIdentity cellIdentity) {
        long cid;
        int lac;
        int i2;
        long j;
        int i3;
        int i4;
        int i5;
        String mccString = cellIdentity.getMccString();
        String mncString = cellIdentity.getMncString();
        int parseInt = mccString != null ? Integer.parseInt(mccString) : Integer.MAX_VALUE;
        int parseInt2 = mncString != null ? Integer.parseInt(mncString) : Integer.MAX_VALUE;
        if (i == 1) {
            CellIdentityGsm cellIdentityGsm = (CellIdentityGsm) cellIdentity;
            cid = cellIdentityGsm.getCid();
            lac = cellIdentityGsm.getLac();
        } else if (i == 2) {
            CellIdentityWcdma cellIdentityWcdma = (CellIdentityWcdma) cellIdentity;
            cid = cellIdentityWcdma.getCid();
            lac = cellIdentityWcdma.getLac();
        } else {
            if (i == 4) {
                CellIdentityLte cellIdentityLte = (CellIdentityLte) cellIdentity;
                int tac = cellIdentityLte.getTac();
                i5 = cellIdentityLte.getPci();
                j = cellIdentityLte.getCi();
                i2 = Integer.MAX_VALUE;
                i4 = Integer.MAX_VALUE;
                i3 = tac;
            } else if (i != 8) {
                j = Long.MAX_VALUE;
                i2 = Integer.MAX_VALUE;
                i3 = Integer.MAX_VALUE;
                i5 = i3;
                i4 = i5;
            } else {
                CellIdentityNr cellIdentityNr = (CellIdentityNr) cellIdentity;
                long nci = cellIdentityNr.getNci();
                int tac2 = cellIdentityNr.getTac();
                int pci = cellIdentityNr.getPci();
                i4 = cellIdentityNr.getNrarfcn();
                j = nci;
                i2 = Integer.MAX_VALUE;
                i3 = tac2;
                i5 = pci;
            }
            this.mGnssNative.setAgpsReferenceLocationCellId(i, parseInt, parseInt2, i2, j, i3, i5, i4);
        }
        i2 = lac;
        j = cid;
        i3 = Integer.MAX_VALUE;
        i5 = i3;
        i4 = i5;
        this.mGnssNative.setAgpsReferenceLocationCellId(i, parseInt, parseInt2, i2, j, i3, i5, i4);
    }

    public final void requestRefLocation() {
        TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
        int phoneType = telephonyManager.getPhoneType();
        if (phoneType != 1) {
            if (phoneType == 2) {
                Log.e("GnssLocationProvider", "CDMA not supported.");
                return;
            }
            return;
        }
        List<CellInfo> allCellInfo = telephonyManager.getAllCellInfo();
        if (allCellInfo != null) {
            HashMap hashMap = new HashMap();
            allCellInfo.sort(Comparator.comparingInt(new ToIntFunction() { // from class: com.android.server.location.gnss.GnssLocationProvider$$ExternalSyntheticLambda5
                @Override // java.util.function.ToIntFunction
                public final int applyAsInt(Object obj) {
                    int lambda$requestRefLocation$9;
                    lambda$requestRefLocation$9 = GnssLocationProvider.lambda$requestRefLocation$9((CellInfo) obj);
                    return lambda$requestRefLocation$9;
                }
            }).reversed());
            for (CellInfo cellInfo : allCellInfo) {
                int cellConnectionStatus = cellInfo.getCellConnectionStatus();
                if (cellInfo.isRegistered() || cellConnectionStatus == 1 || cellConnectionStatus == 2) {
                    CellIdentity cellIdentity = cellInfo.getCellIdentity();
                    int cellType = getCellType(cellInfo);
                    if (getCidFromCellIdentity(cellIdentity) != -1 && !hashMap.containsKey(Integer.valueOf(cellType))) {
                        hashMap.put(Integer.valueOf(cellType), cellIdentity);
                    }
                }
            }
            if (hashMap.containsKey(1)) {
                setRefLocation(1, (CellIdentity) hashMap.get(1));
                return;
            } else if (hashMap.containsKey(4)) {
                setRefLocation(2, (CellIdentity) hashMap.get(4));
                return;
            } else if (hashMap.containsKey(3)) {
                setRefLocation(4, (CellIdentity) hashMap.get(3));
                return;
            } else if (hashMap.containsKey(6)) {
                setRefLocation(8, (CellIdentity) hashMap.get(6));
                return;
            } else {
                Log.e("GnssLocationProvider", "No available serving cell information.");
                return;
            }
        }
        Log.e("GnssLocationProvider", "Error getting cell location info.");
    }

    public static /* synthetic */ int lambda$requestRefLocation$9(CellInfo cellInfo) {
        return cellInfo.getCellSignalStrength().getAsuLevel();
    }

    public final void postWithWakeLockHeld(final Runnable runnable) {
        this.mWakeLock.acquire(30000L);
        if (this.mHandler.post(new Runnable() { // from class: com.android.server.location.gnss.GnssLocationProvider$$ExternalSyntheticLambda21
            @Override // java.lang.Runnable
            public final void run() {
                GnssLocationProvider.this.lambda$postWithWakeLockHeld$10(runnable);
            }
        })) {
            return;
        }
        this.mWakeLock.release();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$postWithWakeLockHeld$10(Runnable runnable) {
        try {
            runnable.run();
        } finally {
            this.mWakeLock.release();
        }
    }

    @Override // com.android.server.location.provider.AbstractLocationProvider
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        String str;
        boolean z = false;
        int i = 0;
        while (true) {
            if (i >= strArr.length || (str = strArr[i]) == null || str.length() <= 0 || str.charAt(0) != '-') {
                break;
            }
            i++;
            if ("-a".equals(str)) {
                z = true;
                break;
            }
        }
        printWriter.print("mStarted=" + this.mStarted + "   (changed ");
        TimeUtils.formatDuration(SystemClock.elapsedRealtime() - this.mStartedChangedElapsedRealtime, printWriter);
        printWriter.println(" ago)");
        printWriter.println("mBatchingEnabled=" + this.mBatchingEnabled);
        printWriter.println("mBatchingStarted=" + this.mBatchingStarted);
        printWriter.println("mBatchSize=" + getBatchSize());
        printWriter.println("mFixInterval=" + this.mFixInterval);
        printWriter.print(this.mGnssMetrics.dumpGnssMetricsAsText());
        if (z) {
            this.mNetworkTimeHelper.dump(printWriter);
            printWriter.println("mSupportsPsds=" + this.mSupportsPsds);
            printWriter.println("PsdsServerConfigured=" + this.mGnssConfiguration.isLongTermPsdsServerConfigured());
            printWriter.println("native internal state: ");
            printWriter.println("  " + this.mGnssNative.getInternalState());
        }
    }

    @Override // com.android.server.location.gnss.hal.GnssNative.BaseCallbacks
    public void onHalRestarted() {
        reloadGpsProperties();
        if (isGpsEnabled()) {
            setGpsEnabled(false);
            updateEnabled();
            restartLocationRequest();
        }
        this.mNetworkConnectivityHandler.unregisterNetworkCallbacks();
        this.mNetworkConnectivityHandler.registerNetworkCallbacks();
    }

    @Override // com.android.server.location.gnss.hal.GnssNative.BaseCallbacks
    public void onCapabilitiesChanged(GnssCapabilities gnssCapabilities, GnssCapabilities gnssCapabilities2) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.location.gnss.GnssLocationProvider$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                GnssLocationProvider.this.lambda$onCapabilitiesChanged$11();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCapabilitiesChanged$11() {
        boolean hasOnDemandTime = this.mGnssNative.getCapabilities().hasOnDemandTime();
        this.mNetworkTimeHelper.setPeriodicTimeInjectionMode(hasOnDemandTime);
        if (hasOnDemandTime) {
            demandUtcTimeInjection();
        }
        restartLocationRequest();
    }

    @Override // com.android.server.location.gnss.hal.GnssNative.LocationCallbacks
    public void onReportLocation(final boolean z, final Location location) {
        postWithWakeLockHeld(new Runnable() { // from class: com.android.server.location.gnss.GnssLocationProvider$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                GnssLocationProvider.this.lambda$onReportLocation$12(z, location);
            }
        });
    }

    @Override // com.android.server.location.gnss.hal.GnssNative.LocationCallbacks
    public void onReportLocations(Location[] locationArr) {
        Runnable[] runnableArr;
        boolean z;
        if (DEBUG) {
            Log.d("GnssLocationProvider", "Location batch of size " + locationArr.length + " reported");
        }
        if (locationArr.length > 0) {
            if (locationArr.length > 1) {
                int length = locationArr.length - 2;
                while (true) {
                    if (length < 0) {
                        z = false;
                        break;
                    }
                    int i = length + 1;
                    if (Math.abs((locationArr[i].getTime() - locationArr[length].getTime()) - (locationArr[i].getElapsedRealtimeMillis() - locationArr[length].getElapsedRealtimeMillis())) > 500) {
                        z = true;
                        break;
                    }
                    length--;
                }
                if (z) {
                    Arrays.sort(locationArr, Comparator.comparingLong(new ToLongFunction() { // from class: com.android.server.location.gnss.GnssLocationProvider$$ExternalSyntheticLambda2
                        @Override // java.util.function.ToLongFunction
                        public final long applyAsLong(Object obj) {
                            return ((Location) obj).getTime();
                        }
                    }));
                    long time = locationArr[locationArr.length - 1].getTime() - locationArr[locationArr.length - 1].getElapsedRealtimeMillis();
                    for (int length2 = locationArr.length - 2; length2 >= 0; length2--) {
                        Location location = locationArr[length2];
                        location.setElapsedRealtimeNanos(TimeUnit.MILLISECONDS.toNanos(Math.max(location.getTime() - time, 0L)));
                    }
                } else {
                    Arrays.sort(locationArr, Comparator.comparingLong(new ToLongFunction() { // from class: com.android.server.location.gnss.GnssLocationProvider$$ExternalSyntheticLambda3
                        @Override // java.util.function.ToLongFunction
                        public final long applyAsLong(Object obj) {
                            return ((Location) obj).getElapsedRealtimeNanos();
                        }
                    }));
                }
            }
            reportLocation(LocationResult.wrap(locationArr).validate());
        }
        synchronized (this.mLock) {
            runnableArr = (Runnable[]) this.mFlushListeners.toArray(new Runnable[0]);
            this.mFlushListeners.clear();
        }
        for (Runnable runnable : runnableArr) {
            runnable.run();
        }
    }

    @Override // com.android.server.location.gnss.hal.GnssNative.SvStatusCallbacks
    public void onReportSvStatus(final GnssStatus gnssStatus) {
        postWithWakeLockHeld(new Runnable() { // from class: com.android.server.location.gnss.GnssLocationProvider$$ExternalSyntheticLambda11
            @Override // java.lang.Runnable
            public final void run() {
                GnssLocationProvider.this.lambda$onReportSvStatus$13(gnssStatus);
            }
        });
    }

    @Override // com.android.server.location.gnss.hal.GnssNative.AGpsCallbacks
    public void onReportAGpsStatus(int i, int i2, byte[] bArr) {
        this.mNetworkConnectivityHandler.onReportAGpsStatus(i, i2, bArr);
    }

    @Override // com.android.server.location.gnss.hal.GnssNative.PsdsCallbacks
    public void onRequestPsdsDownload(final int i) {
        postWithWakeLockHeld(new Runnable() { // from class: com.android.server.location.gnss.GnssLocationProvider$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                GnssLocationProvider.this.lambda$onRequestPsdsDownload$14(i);
            }
        });
    }

    @Override // com.android.server.location.gnss.hal.GnssNative.NotificationCallbacks
    public void onReportNiNotification(int i, int i2, int i3, int i4, int i5, String str, String str2, int i6, int i7) {
        reportNiNotification(i, i2, i3, i4, i5, str, str2, i6, i7);
    }

    /* JADX WARN: Code restructure failed: missing block: B:15:0x0041, code lost:
        if (r5 != null) goto L16;
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x004c, code lost:
        if (r5 != null) goto L16;
     */
    @Override // com.android.server.location.gnss.hal.GnssNative.AGpsCallbacks
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void onRequestSetID(int i) {
        String str;
        TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
        int defaultDataSubscriptionId = SubscriptionManager.getDefaultDataSubscriptionId();
        if (this.mGnssConfiguration.isActiveSimEmergencySuplEnabled() && this.mNIHandler.getInEmergency() && this.mNetworkConnectivityHandler.getActiveSubId() >= 0) {
            defaultDataSubscriptionId = this.mNetworkConnectivityHandler.getActiveSubId();
        }
        if (SubscriptionManager.isValidSubscriptionId(defaultDataSubscriptionId)) {
            telephonyManager = telephonyManager.createForSubscriptionId(defaultDataSubscriptionId);
        }
        int i2 = 1;
        if ((i & 1) == 1) {
            str = telephonyManager.getSubscriberId();
        } else {
            i2 = 2;
            if ((i & 2) == 2) {
                str = telephonyManager.getLine1Number();
            } else {
                str = null;
            }
            i2 = 0;
        }
        GnssNative gnssNative = this.mGnssNative;
        if (str == null) {
            str = "";
        }
        gnssNative.setAgpsSetId(i2, str);
    }

    @Override // com.android.server.location.gnss.hal.GnssNative.LocationRequestCallbacks
    public void onRequestLocation(final boolean z, final boolean z2) {
        if (DEBUG) {
            Log.d("GnssLocationProvider", "requestLocation. independentFromGnss: " + z + ", isUserEmergency: " + z2);
        }
        postWithWakeLockHeld(new Runnable() { // from class: com.android.server.location.gnss.GnssLocationProvider$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                GnssLocationProvider.this.lambda$onRequestLocation$15(z, z2);
            }
        });
    }

    @Override // com.android.server.location.gnss.hal.GnssNative.TimeCallbacks
    public void onRequestUtcTime() {
        demandUtcTimeInjection();
    }

    @Override // com.android.server.location.gnss.hal.GnssNative.LocationRequestCallbacks
    public void onRequestRefLocation() {
        requestRefLocation();
    }

    @Override // com.android.server.location.gnss.hal.GnssNative.NotificationCallbacks
    public void onReportNfwNotification(String str, byte b, String str2, byte b2, String str3, byte b3, boolean z, boolean z2) {
        GnssVisibilityControl gnssVisibilityControl = this.mGnssVisibilityControl;
        if (gnssVisibilityControl == null) {
            Log.e("GnssLocationProvider", "reportNfwNotification: mGnssVisibilityControl uninitialized.");
        } else {
            gnssVisibilityControl.reportNfwNotification(str, b, str2, b2, str3, b3, z, z2);
        }
    }
}
