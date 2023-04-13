package com.android.internal.telephony;

import android.app.Notification;
import android.app.NotificationManager;
import android.compat.annotation.UnsupportedAppUsage;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.internal.telephony.sysprop.TelephonyProperties;
import android.os.AsyncResult;
import android.os.BaseBundle;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.Message;
import android.os.Parcel;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.WorkSource;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.provider.Telephony;
import android.telephony.CarrierConfigManager;
import android.telephony.CellIdentity;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityNr;
import android.telephony.CellIdentityTdscdma;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.DataSpecificRegistrationInfo;
import android.telephony.NetworkRegistrationInfo;
import android.telephony.PhysicalChannelConfig;
import android.telephony.RadioAccessFamily;
import android.telephony.ServiceState;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.VoiceSpecificRegistrationInfo;
import android.text.TextUtils;
import android.util.EventLog;
import android.util.Pair;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.CommandException;
import com.android.internal.telephony.cdma.CdmaSubscriptionSourceManager;
import com.android.internal.telephony.cdma.EriManager;
import com.android.internal.telephony.cdnr.CarrierDisplayNameData;
import com.android.internal.telephony.cdnr.CarrierDisplayNameResolver;
import com.android.internal.telephony.data.AccessNetworksManager;
import com.android.internal.telephony.data.DataNetworkController;
import com.android.internal.telephony.data.KeepaliveStatus;
import com.android.internal.telephony.imsphone.ImsPhone;
import com.android.internal.telephony.metrics.ServiceStateStats;
import com.android.internal.telephony.metrics.TelephonyMetrics;
import com.android.internal.telephony.nano.TelephonyProto$RilErrno;
import com.android.internal.telephony.subscription.SubscriptionInfoInternal;
import com.android.internal.telephony.subscription.SubscriptionManagerService;
import com.android.internal.telephony.uicc.IccCardApplicationStatus;
import com.android.internal.telephony.uicc.IccCardStatus;
import com.android.internal.telephony.uicc.IccRecords;
import com.android.internal.telephony.uicc.RuimRecords;
import com.android.internal.telephony.uicc.SIMRecords;
import com.android.internal.telephony.uicc.UiccCard;
import com.android.internal.telephony.uicc.UiccCardApplication;
import com.android.internal.telephony.uicc.UiccController;
import com.android.internal.telephony.uicc.UiccPort;
import com.android.internal.telephony.uicc.UiccProfile;
import com.android.internal.telephony.util.ArrayUtils;
import com.android.internal.telephony.util.NotificationChannelController;
import com.android.internal.telephony.util.TelephonyUtils;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class ServiceStateTracker extends Handler {
    public static final int CARRIER_NAME_DISPLAY_BITMASK_SHOW_PLMN = 2;
    public static final int CARRIER_NAME_DISPLAY_BITMASK_SHOW_SPN = 1;
    public static final int CS_DISABLED = 1004;
    public static final int CS_EMERGENCY_ENABLED = 1006;
    public static final int CS_ENABLED = 1003;
    public static final int CS_NORMAL_ENABLED = 1005;
    public static final int CS_NOTIFICATION = 999;
    public static final int CS_REJECT_CAUSE_DISABLED = 2002;
    public static final int CS_REJECT_CAUSE_ENABLED = 2001;
    public static final int CS_REJECT_CAUSE_NOTIFICATION = 111;
    public static final int DEFAULT_GPRS_CHECK_PERIOD_MILLIS = 60000;
    public static final String DEFAULT_MNC = "00";
    protected static final int EVENT_ALL_DATA_DISCONNECTED = 49;
    protected static final int EVENT_CDMA_PRL_VERSION_CHANGED = 40;
    protected static final int EVENT_CDMA_SUBSCRIPTION_SOURCE_CHANGED = 39;
    protected static final int EVENT_CELL_LOCATION_RESPONSE = 56;
    protected static final int EVENT_CHANGE_IMS_STATE = 45;
    protected static final int EVENT_CHECK_REPORT_GPRS = 22;
    protected static final int EVENT_GET_ALLOWED_NETWORK_TYPES = 19;
    protected static final int EVENT_GET_CELL_INFO_LIST = 43;
    protected static final int EVENT_GET_LOC_DONE = 15;
    public static final int EVENT_ICC_CHANGED = 42;
    protected static final int EVENT_IMS_CAPABILITY_CHANGED = 48;
    protected static final int EVENT_IMS_SERVICE_STATE_CHANGED = 53;
    protected static final int EVENT_IMS_STATE_CHANGED = 46;
    protected static final int EVENT_IMS_STATE_DONE = 47;
    protected static final int EVENT_LOCATION_UPDATES_ENABLED = 18;
    protected static final int EVENT_NETWORK_STATE_CHANGED = 2;
    protected static final int EVENT_NITZ_TIME = 11;
    protected static final int EVENT_NV_READY = 35;
    protected static final int EVENT_OTA_PROVISION_STATUS_CHANGE = 37;
    protected static final int EVENT_PHONE_TYPE_SWITCHED = 50;
    protected static final int EVENT_PHYSICAL_CHANNEL_CONFIG = 55;
    protected static final int EVENT_POLL_STATE_CDMA_SUBSCRIPTION = 34;
    protected static final int EVENT_POLL_STATE_CS_CELLULAR_REGISTRATION = 4;
    protected static final int EVENT_POLL_STATE_NETWORK_SELECTION_MODE = 14;
    protected static final int EVENT_POLL_STATE_OPERATOR = 7;
    protected static final int EVENT_POLL_STATE_PS_CELLULAR_REGISTRATION = 5;
    protected static final int EVENT_POLL_STATE_PS_IWLAN_REGISTRATION = 6;
    protected static final int EVENT_RADIO_ON = 41;
    protected static final int EVENT_RADIO_POWER_FROM_CARRIER = 51;
    protected static final int EVENT_RADIO_POWER_OFF_DONE = 54;
    protected static final int EVENT_RADIO_STATE_CHANGED = 1;
    protected static final int EVENT_RESET_ALLOWED_NETWORK_TYPES = 21;
    protected static final int EVENT_RESET_LAST_KNOWN_CELL_IDENTITY = 63;
    protected static final int EVENT_RESTRICTED_STATE_CHANGED = 23;
    protected static final int EVENT_RUIM_READY = 26;
    protected static final int EVENT_RUIM_RECORDS_LOADED = 27;
    protected static final int EVENT_SET_ALLOWED_NETWORK_TYPES = 20;
    protected static final int EVENT_SET_RADIO_POWER_OFF = 38;
    protected static final int EVENT_SIM_READY = 17;
    protected static final int EVENT_SIM_RECORDS_LOADED = 16;
    protected static final int EVENT_TELECOM_VOICE_SERVICE_STATE_OVERRIDE_CHANGED = 65;
    protected static final int EVENT_UNSOL_CELL_INFO_LIST = 44;
    public static final String INVALID_MCC = "000";
    private static final long POWER_OFF_ALL_DATA_NETWORKS_DISCONNECTED_TIMEOUT = TimeUnit.SECONDS.toMillis(10);
    public static final int PS_DISABLED = 1002;
    public static final int PS_ENABLED = 1001;
    public static final int PS_NOTIFICATION = 888;
    protected static final String REGISTRATION_DENIED_AUTH = "Authentication Failure";
    protected static final String REGISTRATION_DENIED_GEN = "General";
    public static final String UNACTIVATED_MIN2_VALUE = "000000";
    public static final String UNACTIVATED_MIN_VALUE = "1111110111";
    private final AccessNetworksManager mAccessNetworksManager;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private int mAllowedNetworkTypes;
    private final LocalLog mAttachLog;
    private boolean mCSEmergencyOnly;
    private CarrierServiceStateTracker mCSST;
    private PersistableBundle mCarrierConfig;
    private final CarrierConfigManager.CarrierConfigChangeListener mCarrierConfigChangeListener;
    private RegistrantList mCdmaForSubscriptionInfoReadyRegistrants;
    private CdmaSubscriptionSourceManager mCdmaSSM;
    private CarrierDisplayNameResolver mCdnr;
    private final LocalLog mCdnrLogs;
    private CellIdentity mCellIdentity;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private CommandsInterface mCi;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private final ContentResolver mCr;
    private String mCurrentCarrier;
    private int mCurrentOtaspMode;
    private DataNetworkController.DataNetworkControllerCallback mDataDisconnectedCallback;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private int mDefaultRoamingIndicator;
    @UnsupportedAppUsage
    private boolean mDesiredPowerState;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private boolean mEmergencyOnly;
    private final EriManager mEriManager;
    private String mEriText;
    private boolean mGsmDataRoaming;
    private boolean mGsmVoiceRoaming;
    private HbpcdUtils mHbpcdUtils;
    private int[] mHomeNetworkId;
    private int[] mHomeSystemId;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private BroadcastReceiver mIntentReceiver;
    private boolean mIsEriTextLoaded;
    private boolean mIsInPrl;
    private boolean mIsMinInfoReady;
    private boolean mIsSimReady;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private boolean mIsSubscriptionFromRuim;
    private long mLastCellInfoReqTime;
    private int mLastKnownAreaCode;
    private CellIdentity mLastKnownCellIdentity;
    private String mLastKnownNetworkCountry;
    private NitzData mLastNitzData;
    private final LocaleTracker mLocaleTracker;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private int mMaxDataCalls;
    private String mMdn;
    private String mMin;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private int mNewMaxDataCalls;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private int mNewReasonDataDenied;
    private int mNewRejectCode;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private ServiceState mNewSS;
    private final NitzStateMachine mNitzState;
    private Notification mNotification;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private final SstSubscriptionsChangedListener mOnSubscriptionsChangedListener;
    private Pattern mOperatorNameStringPattern;
    private final ServiceState mOutOfServiceSS;
    private boolean mPSEmergencyOnly;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected final GsmCdmaPhone mPhone;
    private final LocalLog mPhoneTypeLog;
    @VisibleForTesting
    public int[] mPollingContext;
    private String mPrlVersion;
    private final LocalLog mRadioPowerLog;
    private final LocalLog mRatLog;
    private final RatRatcheter mRatRatcheter;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private int mReasonDataDenied;
    private final SparseArray<NetworkRegistrationManager> mRegStateManagers;
    private String mRegistrationDeniedReason;
    private int mRegistrationState;
    private int mRejectCode;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private boolean mReportedGprsNoReg;
    public RestrictedState mRestrictedState;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private int mRoamingIndicator;
    private final LocalLog mRoamingLog;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public ServiceState mSS;
    private ServiceStateStats mServiceStateStats;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private boolean mStartedGprsRegCheck;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private SubscriptionController mSubscriptionController;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private SubscriptionManager mSubscriptionManager;
    private SubscriptionManagerService mSubscriptionManagerService;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private UiccController mUiccController;
    private boolean mVoiceCapable;
    private boolean mWantContinuousLocationUpdates;
    private boolean mWantSingleLocationUpdate;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private UiccCardApplication mUiccApplication = null;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private IccRecords mIccRecords = null;
    private int mCellInfoMinIntervalMs = 2000;
    private List<CellInfo> mLastCellInfoList = null;
    private List<PhysicalChannelConfig> mLastPhysicalChannelConfigList = null;
    private int mLastAnchorNrCellId = -1;
    private final Set<Integer> mRadioPowerOffReasons = new HashSet();
    @UnsupportedAppUsage
    private RegistrantList mVoiceRoamingOnRegistrants = new RegistrantList();
    @UnsupportedAppUsage
    private RegistrantList mVoiceRoamingOffRegistrants = new RegistrantList();
    @UnsupportedAppUsage
    private RegistrantList mDataRoamingOnRegistrants = new RegistrantList();
    @UnsupportedAppUsage
    private RegistrantList mDataRoamingOffRegistrants = new RegistrantList();
    protected SparseArray<RegistrantList> mAttachedRegistrants = new SparseArray<>();
    protected SparseArray<RegistrantList> mDetachedRegistrants = new SparseArray<>();
    private RegistrantList mVoiceRegStateOrRatChangedRegistrants = new RegistrantList();
    private SparseArray<RegistrantList> mDataRegStateOrRatChangedRegistrants = new SparseArray<>();
    @UnsupportedAppUsage
    private RegistrantList mNetworkAttachedRegistrants = new RegistrantList();
    private RegistrantList mNetworkDetachedRegistrants = new RegistrantList();
    private RegistrantList mServiceStateChangedRegistrants = new RegistrantList();
    private RegistrantList mPsRestrictEnabledRegistrants = new RegistrantList();
    private RegistrantList mPsRestrictDisabledRegistrants = new RegistrantList();
    private RegistrantList mImsCapabilityChangedRegistrants = new RegistrantList();
    private RegistrantList mNrStateChangedRegistrants = new RegistrantList();
    private RegistrantList mNrFrequencyChangedRegistrants = new RegistrantList();
    private RegistrantList mCssIndicatorChangedRegistrants = new RegistrantList();
    private final RegistrantList mAirplaneModeChangedRegistrants = new RegistrantList();
    private final RegistrantList mAreaCodeChangedRegistrants = new RegistrantList();
    private volatile boolean mPendingRadioPowerOffAfterDataOff = false;
    private List<Message> mPendingCellInfoRequests = new LinkedList();
    private boolean mIsPendingCellInfoRequest = false;
    private boolean mImsRegistrationOnOff = false;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private boolean mDeviceShuttingDown = false;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private boolean mSpnUpdatePending = false;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private String mCurSpn = null;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private String mCurDataSpn = null;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private String mCurPlmn = null;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private boolean mCurShowPlmn = false;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private boolean mCurShowSpn = false;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    @VisibleForTesting
    public int mSubId = -1;
    private int mPrevSubId = -1;
    private boolean mImsRegistered = false;

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private boolean isGprsConsistent(int i, int i2) {
        return i2 != 0 || i == 0;
    }

    private static boolean isValidLteBandwidthKhz(int i) {
        return i == 1400 || i == 3000 || i == 5000 || i == 10000 || i == 15000 || i == 20000;
    }

    private static boolean isValidNrBandwidthKhz(int i) {
        switch (i) {
            case GbaManager.REQUEST_TIMEOUT_MS /* 5000 */:
            case 10000:
            case 15000:
            case 20000:
            case 25000:
            case 30000:
            case 40000:
            case 50000:
            case DEFAULT_GPRS_CHECK_PERIOD_MILLIS /* 60000 */:
            case 70000:
            case 80000:
            case 90000:
            case 100000:
                return true;
            default:
                return false;
        }
    }

    private boolean regCodeIsRoaming(int i) {
        return 5 == i;
    }

    private int regCodeToServiceState(int i) {
        return (i == 1 || i == 5) ? 0 : 1;
    }

    private int selectResourceForRejectCode(int i, boolean z) {
        if (i == 1) {
            return z ? 17040800 : 17040799;
        } else if (i == 2) {
            return z ? 17040806 : 17040805;
        } else if (i == 3) {
            return z ? 17040804 : 17040803;
        } else if (i != 6) {
            return 0;
        } else {
            return z ? 17040802 : 17040801;
        }
    }

    /* loaded from: classes.dex */
    private class SstSubscriptionsChangedListener extends SubscriptionManager.OnSubscriptionsChangedListener {
        private SstSubscriptionsChangedListener() {
        }

        @Override // android.telephony.SubscriptionManager.OnSubscriptionsChangedListener
        public void onSubscriptionsChanged() {
            ServiceStateTracker.this.log("SubscriptionListener.onSubscriptionInfoChanged");
            int subId = ServiceStateTracker.this.mPhone.getSubId();
            ServiceStateTracker serviceStateTracker = ServiceStateTracker.this;
            int i = serviceStateTracker.mSubId;
            if (i == subId) {
                return;
            }
            serviceStateTracker.mPrevSubId = i;
            ServiceStateTracker serviceStateTracker2 = ServiceStateTracker.this;
            serviceStateTracker2.mSubId = subId;
            serviceStateTracker2.mPhone.updateVoiceMail();
            if (!SubscriptionManager.isValidSubscriptionId(ServiceStateTracker.this.mSubId)) {
                if (SubscriptionManager.isValidSubscriptionId(ServiceStateTracker.this.mPrevSubId)) {
                    ServiceStateTracker serviceStateTracker3 = ServiceStateTracker.this;
                    serviceStateTracker3.mPhone.notifyServiceStateChangedForSubId(serviceStateTracker3.mOutOfServiceSS, ServiceStateTracker.this.mPrevSubId);
                    return;
                }
                return;
            }
            Context context = ServiceStateTracker.this.mPhone.getContext();
            ServiceStateTracker.this.mPhone.notifyPhoneStateChanged();
            if (!SubscriptionManager.isValidSubscriptionId(ServiceStateTracker.this.mPrevSubId)) {
                GsmCdmaPhone gsmCdmaPhone = ServiceStateTracker.this.mPhone;
                gsmCdmaPhone.notifyServiceStateChanged(gsmCdmaPhone.getServiceState());
            }
            ServiceStateTracker.this.mPhone.sendSubscriptionSettings(!context.getResources().getBoolean(17891914));
            ServiceStateTracker serviceStateTracker4 = ServiceStateTracker.this;
            serviceStateTracker4.setDataNetworkTypeForPhone(serviceStateTracker4.mSS.getRilDataRadioTechnology());
            if (ServiceStateTracker.this.mSpnUpdatePending) {
                ServiceStateTracker.this.mSubscriptionController.setPlmnSpn(ServiceStateTracker.this.mPhone.getPhoneId(), ServiceStateTracker.this.mCurShowPlmn, ServiceStateTracker.this.mCurPlmn, ServiceStateTracker.this.mCurShowSpn, ServiceStateTracker.this.mCurSpn);
                ServiceStateTracker.this.mSpnUpdatePending = false;
            }
            SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String string = defaultSharedPreferences.getString(Phone.NETWORK_SELECTION_KEY, PhoneConfigurationManager.SSSS);
            String string2 = defaultSharedPreferences.getString(Phone.NETWORK_SELECTION_NAME_KEY, PhoneConfigurationManager.SSSS);
            String string3 = defaultSharedPreferences.getString(Phone.NETWORK_SELECTION_SHORT_KEY, PhoneConfigurationManager.SSSS);
            if (!TextUtils.isEmpty(string) || !TextUtils.isEmpty(string2) || !TextUtils.isEmpty(string3)) {
                SharedPreferences.Editor edit = defaultSharedPreferences.edit();
                edit.putString(Phone.NETWORK_SELECTION_KEY + ServiceStateTracker.this.mSubId, string);
                edit.putString(Phone.NETWORK_SELECTION_NAME_KEY + ServiceStateTracker.this.mSubId, string2);
                edit.putString(Phone.NETWORK_SELECTION_SHORT_KEY + ServiceStateTracker.this.mSubId, string3);
                edit.remove(Phone.NETWORK_SELECTION_KEY);
                edit.remove(Phone.NETWORK_SELECTION_NAME_KEY);
                edit.remove(Phone.NETWORK_SELECTION_SHORT_KEY);
                edit.commit();
            }
            ServiceStateTracker.this.updateSpnDisplay();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i, int i2, int i3, int i4) {
        onCarrierConfigurationChanged(i);
    }

    public ServiceStateTracker(GsmCdmaPhone gsmCdmaPhone, CommandsInterface commandsInterface) {
        int[] availableTransports;
        this.mUiccController = null;
        SstSubscriptionsChangedListener sstSubscriptionsChangedListener = new SstSubscriptionsChangedListener();
        this.mOnSubscriptionsChangedListener = sstSubscriptionsChangedListener;
        this.mRoamingLog = new LocalLog(8);
        this.mAttachLog = new LocalLog(8);
        this.mPhoneTypeLog = new LocalLog(8);
        this.mRatLog = new LocalLog(16);
        this.mRadioPowerLog = new LocalLog(16);
        this.mCdnrLogs = new LocalLog(64);
        this.mMaxDataCalls = 1;
        this.mNewMaxDataCalls = 1;
        this.mReasonDataDenied = -1;
        this.mNewReasonDataDenied = -1;
        this.mGsmVoiceRoaming = false;
        this.mGsmDataRoaming = false;
        this.mEmergencyOnly = false;
        this.mCSEmergencyOnly = false;
        this.mPSEmergencyOnly = false;
        this.mIsSimReady = false;
        this.mLastKnownNetworkCountry = PhoneConfigurationManager.SSSS;
        this.mIntentReceiver = new BroadcastReceiver() { // from class: com.android.internal.telephony.ServiceStateTracker.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals("android.intent.action.LOCALE_CHANGED")) {
                    ServiceStateTracker.this.pollState();
                } else if (action.equals("android.telephony.action.NETWORK_COUNTRY_CHANGED")) {
                    if (ServiceStateTracker.this.mLastKnownNetworkCountry.equals(intent.getStringExtra("android.telephony.extra.LAST_KNOWN_NETWORK_COUNTRY"))) {
                        return;
                    }
                    ServiceStateTracker.this.updateSpnDisplay();
                }
            }
        };
        CarrierConfigManager.CarrierConfigChangeListener carrierConfigChangeListener = new CarrierConfigManager.CarrierConfigChangeListener() { // from class: com.android.internal.telephony.ServiceStateTracker$$ExternalSyntheticLambda0
            public final void onCarrierConfigChanged(int i, int i2, int i3, int i4) {
                ServiceStateTracker.this.lambda$new$0(i, i2, i3, i4);
            }
        };
        this.mCarrierConfigChangeListener = carrierConfigChangeListener;
        this.mCurrentOtaspMode = 0;
        this.mRegistrationState = -1;
        this.mCdmaForSubscriptionInfoReadyRegistrants = new RegistrantList();
        this.mHomeSystemId = null;
        this.mHomeNetworkId = null;
        this.mIsMinInfoReady = false;
        this.mIsEriTextLoaded = false;
        this.mIsSubscriptionFromRuim = false;
        this.mHbpcdUtils = null;
        this.mCurrentCarrier = null;
        this.mRegStateManagers = new SparseArray<>();
        this.mLastKnownAreaCode = KeepaliveStatus.INVALID_HANDLE;
        this.mNitzState = TelephonyComponentFactory.getInstance().inject(NitzStateMachine.class.getName()).makeNitzStateMachine(gsmCdmaPhone);
        this.mPhone = gsmCdmaPhone;
        this.mCi = commandsInterface;
        this.mServiceStateStats = new ServiceStateStats(gsmCdmaPhone);
        this.mCdnr = new CarrierDisplayNameResolver(gsmCdmaPhone);
        if (UiccController.isCdmaSupported(gsmCdmaPhone.getContext())) {
            this.mEriManager = TelephonyComponentFactory.getInstance().inject(EriManager.class.getName()).makeEriManager(gsmCdmaPhone, 0);
        } else {
            this.mEriManager = null;
        }
        this.mRatRatcheter = new RatRatcheter(gsmCdmaPhone);
        this.mVoiceCapable = ((TelephonyManager) gsmCdmaPhone.getContext().getSystemService("phone")).isVoiceCapable();
        UiccController uiccController = UiccController.getInstance();
        this.mUiccController = uiccController;
        uiccController.registerForIccChanged(this, 42, null);
        this.mCi.registerForCellInfoList(this, 44, null);
        this.mCi.registerForPhysicalChannelConfiguration(this, 55, null);
        if (gsmCdmaPhone.isSubscriptionManagerServiceEnabled()) {
            this.mSubscriptionManagerService = SubscriptionManagerService.getInstance();
        } else {
            this.mSubscriptionController = SubscriptionController.getInstance();
        }
        SubscriptionManager from = SubscriptionManager.from(gsmCdmaPhone.getContext());
        this.mSubscriptionManager = from;
        from.addOnSubscriptionsChangedListener(new HandlerExecutor(this), sstSubscriptionsChangedListener);
        this.mRestrictedState = new RestrictedState();
        this.mCarrierConfig = getCarrierConfig();
        ((CarrierConfigManager) gsmCdmaPhone.getContext().getSystemService(CarrierConfigManager.class)).registerCarrierConfigChangeListener(new Executor() { // from class: com.android.internal.telephony.ServiceStateTracker$$ExternalSyntheticLambda1
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                ServiceStateTracker.this.post(runnable);
            }
        }, carrierConfigChangeListener);
        AccessNetworksManager accessNetworksManager = gsmCdmaPhone.getAccessNetworksManager();
        this.mAccessNetworksManager = accessNetworksManager;
        ServiceState serviceState = new ServiceState();
        this.mOutOfServiceSS = serviceState;
        serviceState.setOutOfService(false);
        for (int i : accessNetworksManager.getAvailableTransports()) {
            this.mRegStateManagers.append(i, new NetworkRegistrationManager(i, gsmCdmaPhone));
            this.mRegStateManagers.get(i).registerForNetworkRegistrationInfoChanged(this, 2, null);
        }
        this.mLocaleTracker = TelephonyComponentFactory.getInstance().inject(LocaleTracker.class.getName()).makeLocaleTracker(this.mPhone, this.mNitzState, getLooper());
        this.mCi.registerForImsNetworkStateChanged(this, 46, null);
        this.mCi.registerForRadioStateChanged(this, 1, null);
        this.mCi.setOnNITZTime(this, 11, null);
        ContentResolver contentResolver = gsmCdmaPhone.getContext().getContentResolver();
        this.mCr = contentResolver;
        int i2 = Settings.Global.getInt(contentResolver, "airplane_mode_on", 0);
        int i3 = Settings.Global.getInt(contentResolver, "enable_cellular_on_boot", 1);
        boolean z = i3 > 0 && i2 <= 0;
        this.mDesiredPowerState = z;
        if (!z) {
            this.mRadioPowerOffReasons.add(0);
        }
        this.mRadioPowerLog.log("init : airplane mode = " + i2 + " enableCellularOnBoot = " + i3);
        this.mPhone.getCarrierActionAgent().registerForCarrierAction(1, this, 51, null, false);
        Context context = this.mPhone.getContext();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.LOCALE_CHANGED");
        intentFilter.addAction("android.telephony.action.NETWORK_COUNTRY_CHANGED");
        context.registerReceiver(this.mIntentReceiver, intentFilter);
        this.mPhone.notifyOtaspChanged(0);
        this.mCi.setOnRestrictedStateChanged(this, 23, null);
        updatePhoneType();
        CarrierServiceStateTracker carrierServiceStateTracker = new CarrierServiceStateTracker(gsmCdmaPhone, this);
        this.mCSST = carrierServiceStateTracker;
        registerForNetworkAttached(carrierServiceStateTracker, 101, null);
        registerForNetworkDetached(this.mCSST, CallFailCause.RECOVERY_ON_TIMER_EXPIRY, null);
        registerForDataConnectionAttached(1, this.mCSST, 103, null);
        registerForDataConnectionDetached(1, this.mCSST, 104, null);
        registerForImsCapabilityChanged(this.mCSST, 105, null);
        this.mDataDisconnectedCallback = new DataNetworkController.DataNetworkControllerCallback(new Executor() { // from class: com.android.internal.telephony.ServiceStateTracker$$ExternalSyntheticLambda1
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                ServiceStateTracker.this.post(runnable);
            }
        }) { // from class: com.android.internal.telephony.ServiceStateTracker.2
            @Override // com.android.internal.telephony.data.DataNetworkController.DataNetworkControllerCallback
            public void onAnyDataNetworkExistingChanged(boolean z2) {
                ServiceStateTracker serviceStateTracker = ServiceStateTracker.this;
                serviceStateTracker.log("onAnyDataNetworkExistingChanged: anyDataExisting=" + z2);
                if (z2) {
                    return;
                }
                ServiceStateTracker.this.sendEmptyMessage(49);
            }
        };
    }

    @VisibleForTesting
    public void updatePhoneType() {
        int[] availableTransports;
        NetworkRegistrationInfo networkRegistrationInfo;
        ServiceState serviceState = this.mSS;
        if (serviceState != null && serviceState.getVoiceRoaming()) {
            this.mVoiceRoamingOffRegistrants.notifyRegistrants();
        }
        ServiceState serviceState2 = this.mSS;
        if (serviceState2 != null && serviceState2.getDataRoaming()) {
            this.mDataRoamingOffRegistrants.notifyRegistrants();
        }
        ServiceState serviceState3 = this.mSS;
        if (serviceState3 != null && serviceState3.getState() == 0) {
            this.mNetworkDetachedRegistrants.notifyRegistrants();
        }
        for (int i : this.mAccessNetworksManager.getAvailableTransports()) {
            ServiceState serviceState4 = this.mSS;
            if (serviceState4 != null && (networkRegistrationInfo = serviceState4.getNetworkRegistrationInfo(2, i)) != null && networkRegistrationInfo.isInService() && this.mDetachedRegistrants.get(i) != null) {
                this.mDetachedRegistrants.get(i).notifyRegistrants();
            }
        }
        ServiceState serviceState5 = new ServiceState();
        this.mSS = serviceState5;
        serviceState5.setOutOfService(false);
        ServiceState serviceState6 = new ServiceState();
        this.mNewSS = serviceState6;
        serviceState6.setOutOfService(false);
        this.mLastCellInfoReqTime = 0L;
        this.mLastCellInfoList = null;
        this.mStartedGprsRegCheck = false;
        this.mReportedGprsNoReg = false;
        this.mMdn = null;
        this.mMin = null;
        this.mPrlVersion = null;
        this.mIsMinInfoReady = false;
        this.mLastNitzData = null;
        this.mNitzState.handleNetworkUnavailable();
        this.mCellIdentity = null;
        this.mPhone.getSignalStrengthController().setSignalStrengthDefaultValues();
        this.mLastKnownCellIdentity = null;
        cancelPollState();
        if (this.mPhone.isPhoneTypeGsm()) {
            CdmaSubscriptionSourceManager cdmaSubscriptionSourceManager = this.mCdmaSSM;
            if (cdmaSubscriptionSourceManager != null) {
                cdmaSubscriptionSourceManager.dispose(this);
            }
            this.mCi.unregisterForCdmaPrlChanged(this);
            this.mCi.unregisterForCdmaOtaProvision(this);
            this.mPhone.unregisterForSimRecordsLoaded(this);
        } else {
            this.mPhone.registerForSimRecordsLoaded(this, 16, null);
            CdmaSubscriptionSourceManager cdmaSubscriptionSourceManager2 = CdmaSubscriptionSourceManager.getInstance(this.mPhone.getContext(), this.mCi, this, 39, null);
            this.mCdmaSSM = cdmaSubscriptionSourceManager2;
            this.mIsSubscriptionFromRuim = cdmaSubscriptionSourceManager2.getCdmaSubscriptionSource() == 0;
            this.mCi.registerForCdmaPrlChanged(this, 40, null);
            this.mCi.registerForCdmaOtaProvision(this, 37, null);
            this.mHbpcdUtils = new HbpcdUtils(this.mPhone.getContext());
            updateOtaspState();
        }
        onUpdateIccAvailability();
        setDataNetworkTypeForPhone(0);
        this.mPhone.getSignalStrengthController().getSignalStrengthFromCi();
        sendMessage(obtainMessage(50));
        logPhoneTypeChange();
        notifyVoiceRegStateRilRadioTechnologyChanged();
        for (int i2 : this.mAccessNetworksManager.getAvailableTransports()) {
            notifyDataRegStateRilRadioTechnologyChanged(i2);
        }
    }

    @VisibleForTesting
    public void requestShutdown() {
        if (this.mDeviceShuttingDown) {
            return;
        }
        this.mDeviceShuttingDown = true;
        this.mDesiredPowerState = false;
        setPowerStateToDesired();
    }

    @VisibleForTesting
    public int getRadioPowerOffDelayTimeoutForImsRegistration() {
        return this.mPhone.getContext().getResources().getInteger(17694810);
    }

    public void dispose() {
        CarrierConfigManager.CarrierConfigChangeListener carrierConfigChangeListener;
        this.mPhone.getSignalStrengthController().dispose();
        this.mUiccController.unregisterForIccChanged(this);
        this.mCi.unregisterForCellInfoList(this);
        this.mCi.unregisterForPhysicalChannelConfiguration(this);
        this.mSubscriptionManager.removeOnSubscriptionsChangedListener(this.mOnSubscriptionsChangedListener);
        this.mCi.unregisterForImsNetworkStateChanged(this);
        this.mPhone.getCarrierActionAgent().unregisterForCarrierAction(this, 1);
        this.mPhone.getContext().unregisterReceiver(this.mIntentReceiver);
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) this.mPhone.getContext().getSystemService(CarrierConfigManager.class);
        if (carrierConfigManager != null && (carrierConfigChangeListener = this.mCarrierConfigChangeListener) != null) {
            carrierConfigManager.unregisterCarrierConfigChangeListener(carrierConfigChangeListener);
        }
        CarrierServiceStateTracker carrierServiceStateTracker = this.mCSST;
        if (carrierServiceStateTracker != null) {
            carrierServiceStateTracker.dispose();
            this.mCSST = null;
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public boolean getDesiredPowerState() {
        return this.mDesiredPowerState;
    }

    public boolean getPowerStateFromCarrier() {
        return !this.mRadioPowerOffReasons.contains(2);
    }

    public List<PhysicalChannelConfig> getPhysicalChannelConfigList() {
        return this.mLastPhysicalChannelConfigList;
    }

    protected void notifyVoiceRegStateRilRadioTechnologyChanged() {
        int rilVoiceRadioTechnology = this.mSS.getRilVoiceRadioTechnology();
        int state = this.mSS.getState();
        log("notifyVoiceRegStateRilRadioTechnologyChanged: vrs=" + state + " rat=" + rilVoiceRadioTechnology);
        this.mVoiceRegStateOrRatChangedRegistrants.notifyResult(new Pair(Integer.valueOf(state), Integer.valueOf(rilVoiceRadioTechnology)));
    }

    private Pair<Integer, Integer> getRegistrationInfo(int i) {
        NetworkRegistrationInfo networkRegistrationInfo = this.mSS.getNetworkRegistrationInfo(2, i);
        if (networkRegistrationInfo != null) {
            return new Pair<>(Integer.valueOf(regCodeToServiceState(networkRegistrationInfo.getNetworkRegistrationState())), Integer.valueOf(ServiceState.networkTypeToRilRadioTechnology(networkRegistrationInfo.getAccessNetworkTechnology())));
        }
        return null;
    }

    protected void notifyDataRegStateRilRadioTechnologyChanged(int i) {
        Pair<Integer, Integer> registrationInfo;
        RegistrantList registrantList = this.mDataRegStateOrRatChangedRegistrants.get(i);
        if (registrantList == null || (registrationInfo = getRegistrationInfo(i)) == null) {
            return;
        }
        registrantList.notifyResult(registrationInfo);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected void useDataRegStateForDataOnlyDevices() {
        if (this.mVoiceCapable) {
            return;
        }
        log("useDataRegStateForDataOnlyDevice: VoiceRegState=" + this.mNewSS.getState() + " DataRegState=" + this.mNewSS.getDataRegistrationState());
        ServiceState serviceState = this.mNewSS;
        serviceState.setVoiceRegState(serviceState.getDataRegistrationState());
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected void updatePhoneObject() {
        if (this.mPhone.getContext().getResources().getBoolean(17891842)) {
            if (!(this.mSS.getState() == 0 || this.mSS.getState() == 2)) {
                log("updatePhoneObject: Ignore update");
            } else {
                this.mPhone.updatePhoneObject(this.mSS.getRilVoiceRadioTechnology());
            }
        }
    }

    public void registerForVoiceRoamingOn(Handler handler, int i, Object obj) {
        Registrant registrant = new Registrant(handler, i, obj);
        this.mVoiceRoamingOnRegistrants.add(registrant);
        if (this.mSS.getVoiceRoaming()) {
            registrant.notifyRegistrant();
        }
    }

    public void unregisterForVoiceRoamingOn(Handler handler) {
        this.mVoiceRoamingOnRegistrants.remove(handler);
    }

    public void registerForVoiceRoamingOff(Handler handler, int i, Object obj) {
        Registrant registrant = new Registrant(handler, i, obj);
        this.mVoiceRoamingOffRegistrants.add(registrant);
        if (this.mSS.getVoiceRoaming()) {
            return;
        }
        registrant.notifyRegistrant();
    }

    public void unregisterForVoiceRoamingOff(Handler handler) {
        this.mVoiceRoamingOffRegistrants.remove(handler);
    }

    public void registerForDataRoamingOn(Handler handler, int i, Object obj) {
        Registrant registrant = new Registrant(handler, i, obj);
        this.mDataRoamingOnRegistrants.add(registrant);
        if (this.mSS.getDataRoaming()) {
            registrant.notifyRegistrant();
        }
    }

    public void unregisterForDataRoamingOn(Handler handler) {
        this.mDataRoamingOnRegistrants.remove(handler);
    }

    public void registerForDataRoamingOff(Handler handler, int i, Object obj, boolean z) {
        Registrant registrant = new Registrant(handler, i, obj);
        this.mDataRoamingOffRegistrants.add(registrant);
        if (!z || this.mSS.getDataRoaming()) {
            return;
        }
        registrant.notifyRegistrant();
    }

    public void unregisterForDataRoamingOff(Handler handler) {
        this.mDataRoamingOffRegistrants.remove(handler);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void reRegisterNetwork(Message message) {
        this.mCi.getAllowedNetworkTypesBitmap(obtainMessage(19, message));
    }

    public Set<Integer> getRadioPowerOffReasons() {
        return Set.copyOf(this.mRadioPowerOffReasons);
    }

    public void clearAllRadioOffReasons() {
        this.mRadioPowerOffReasons.clear();
    }

    public final void setRadioPower(boolean z) {
        setRadioPower(z, false, false, false);
    }

    public void setRadioPower(boolean z, boolean z2, boolean z3, boolean z4) {
        setRadioPowerForReason(z, z2, z3, z4, 0);
    }

    public void setRadioPowerForReason(boolean z, boolean z2, boolean z3, boolean z4, int i) {
        log("setRadioPower power " + z + " forEmergencyCall " + z2 + " forceApply " + z4 + " reason " + i);
        if (!z) {
            this.mRadioPowerOffReasons.add(Integer.valueOf(i));
        } else if (z2) {
            clearAllRadioOffReasons();
        } else {
            this.mRadioPowerOffReasons.remove(Integer.valueOf(i));
        }
        if (z == this.mDesiredPowerState && !z4) {
            log("setRadioPower mDesiredPowerState is already " + z + " Do nothing.");
        } else if (z && !this.mRadioPowerOffReasons.isEmpty()) {
            log("setRadioPowerForReason power: " + z + " forEmergencyCall= " + z2 + " isSelectedPhoneForEmergencyCall: " + z3 + " forceApply " + z4 + " reason: " + i + " will not power on the radio as it is powered off for the following reasons: " + this.mRadioPowerOffReasons + ".");
        } else {
            this.mDesiredPowerState = z;
            setPowerStateToDesired(z2, z3, z4);
        }
    }

    public void enableSingleLocationUpdate(WorkSource workSource) {
        if (this.mWantSingleLocationUpdate || this.mWantContinuousLocationUpdates) {
            return;
        }
        this.mWantSingleLocationUpdate = true;
        this.mCi.setLocationUpdates(true, workSource, obtainMessage(18));
    }

    public void enableLocationUpdates() {
        if (this.mWantSingleLocationUpdate || this.mWantContinuousLocationUpdates) {
            return;
        }
        this.mWantContinuousLocationUpdates = true;
        this.mCi.setLocationUpdates(true, null, obtainMessage(18));
    }

    protected void disableSingleLocationUpdate() {
        this.mWantSingleLocationUpdate = false;
        if (this.mWantContinuousLocationUpdates) {
            return;
        }
        this.mCi.setLocationUpdates(false, null, null);
    }

    public void disableLocationUpdates() {
        this.mWantContinuousLocationUpdates = false;
        if (this.mWantSingleLocationUpdate) {
            return;
        }
        this.mCi.setLocationUpdates(false, null, null);
    }

    /* JADX WARN: Removed duplicated region for block: B:249:0x032a A[EXC_TOP_SPLITTER, SYNTHETIC] */
    @Override // android.os.Handler
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void handleMessage(Message message) {
        Object obj;
        ServiceState serviceState;
        Throwable th;
        Phone[] phones;
        boolean z;
        boolean equals;
        int i = message.what;
        List<CellInfo> list = null;
        switch (i) {
            case 1:
            case 50:
                if (!this.mPhone.isPhoneTypeGsm() && this.mCi.getRadioState() == 1) {
                    handleCdmaSubscriptionSource(this.mCdmaSSM.getCdmaSubscriptionSource());
                }
                setPowerStateToDesired();
                pollStateInternal(true);
                return;
            case 2:
                pollStateInternal(true);
                return;
            case 3:
            case 8:
            case 9:
            case 10:
            case 12:
            case 13:
            case 24:
            case 25:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 36:
            case 41:
            case 52:
            case 57:
            case 59:
            case 60:
            case TelephonyProto$RilErrno.RIL_E_NETWORK_NOT_READY /* 61 */:
            case 64:
            default:
                log("Unhandled message with number: " + message.what);
                return;
            case 4:
            case 5:
            case 6:
            case 7:
                handlePollStateResult(i, (AsyncResult) message.obj);
                return;
            case 11:
                Object[] objArr = (Object[]) ((AsyncResult) message.obj).result;
                setTimeFromNITZString((String) objArr[0], ((Long) objArr[1]).longValue(), objArr.length >= 3 ? ((Long) objArr[2]).longValue() : 0L);
                return;
            case 14:
                log("EVENT_POLL_STATE_NETWORK_SELECTION_MODE");
                AsyncResult asyncResult = (AsyncResult) message.obj;
                if (this.mPhone.isPhoneTypeGsm()) {
                    handlePollStateResult(message.what, asyncResult);
                    return;
                } else if (asyncResult.exception == null && (obj = asyncResult.result) != null) {
                    if (((int[]) obj)[0] == 1) {
                        this.mPhone.setNetworkSelectionModeAutomatic(null);
                        return;
                    }
                    return;
                } else {
                    log("Unable to getNetworkSelectionMode");
                    return;
                }
            case 15:
                AsyncResult asyncResult2 = (AsyncResult) message.obj;
                if (asyncResult2.exception == null) {
                    CellIdentity cellIdentity = ((NetworkRegistrationInfo) asyncResult2.result).getCellIdentity();
                    updateOperatorNameForCellIdentity(cellIdentity);
                    this.mCellIdentity = cellIdentity;
                    this.mPhone.notifyLocationChanged(getCellIdentity());
                }
                disableSingleLocationUpdate();
                return;
            case 16:
                log("EVENT_SIM_RECORDS_LOADED: what=" + message.what);
                updatePhoneObject();
                updateOtaspState();
                if (this.mPhone.isPhoneTypeGsm()) {
                    this.mCdnr.updateEfFromUsim((SIMRecords) this.mIccRecords);
                    updateSpnDisplay();
                    return;
                }
                return;
            case 17:
                this.mPrevSubId = -1;
                this.mIsSimReady = true;
                pollStateInternal(false);
                return;
            case 18:
                if (((AsyncResult) message.obj).exception == null) {
                    this.mRegStateManagers.get(1).requestNetworkRegistrationInfo(1, obtainMessage(15, null));
                    return;
                }
                return;
            case 19:
                AsyncResult asyncResult3 = (AsyncResult) message.obj;
                if (asyncResult3.exception == null) {
                    this.mAllowedNetworkTypes = ((int[]) asyncResult3.result)[0];
                } else {
                    this.mAllowedNetworkTypes = RadioAccessFamily.getRafFromNetworkType(7);
                }
                this.mCi.setAllowedNetworkTypesBitmap(RadioAccessFamily.getRafFromNetworkType(7), obtainMessage(20, asyncResult3.userObj));
                return;
            case 20:
                this.mCi.setAllowedNetworkTypesBitmap(this.mAllowedNetworkTypes, obtainMessage(21, ((AsyncResult) message.obj).userObj));
                return;
            case 21:
                AsyncResult asyncResult4 = (AsyncResult) message.obj;
                Object obj2 = asyncResult4.userObj;
                if (obj2 != null) {
                    AsyncResult.forMessage((Message) obj2).exception = asyncResult4.exception;
                    ((Message) asyncResult4.userObj).sendToTarget();
                    return;
                }
                return;
            case 22:
                if (this.mPhone.isPhoneTypeGsm() && (serviceState = this.mSS) != null && !isGprsConsistent(serviceState.getDataRegistrationState(), this.mSS.getState())) {
                    EventLog.writeEvent((int) EventLogTags.DATA_NETWORK_REGISTRATION_FAIL, this.mSS.getOperatorNumeric(), Long.valueOf(getCidFromCellIdentity(this.mCellIdentity)));
                    this.mReportedGprsNoReg = true;
                }
                this.mStartedGprsRegCheck = false;
                return;
            case 23:
                if (this.mPhone.isPhoneTypeGsm()) {
                    log("EVENT_RESTRICTED_STATE_CHANGED");
                    onRestrictedStateChanged((AsyncResult) message.obj);
                    return;
                }
                return;
            case 26:
                if (this.mPhone.getLteOnCdmaMode() == 1) {
                    log("Receive EVENT_RUIM_READY");
                    pollStateInternal(false);
                } else {
                    log("Receive EVENT_RUIM_READY and Send Request getCDMASubscription.");
                    getSubscriptionInfoAndStartPollingThreads();
                }
                this.mCi.getNetworkSelectionMode(obtainMessage(14));
                return;
            case 27:
                if (this.mPhone.isPhoneTypeGsm()) {
                    return;
                }
                log("EVENT_RUIM_RECORDS_LOADED: what=" + message.what);
                this.mCdnr.updateEfFromRuim((RuimRecords) this.mIccRecords);
                updatePhoneObject();
                if (this.mPhone.isPhoneTypeCdma()) {
                    updateSpnDisplay();
                    return;
                }
                RuimRecords ruimRecords = (RuimRecords) this.mIccRecords;
                if (ruimRecords != null) {
                    this.mMdn = ruimRecords.getMdn();
                    if (ruimRecords.isProvisioned()) {
                        this.mMin = ruimRecords.getMin();
                        parseSidNid(ruimRecords.getSid(), ruimRecords.getNid());
                        this.mPrlVersion = ruimRecords.getPrlVersion();
                        this.mIsMinInfoReady = true;
                    }
                    updateOtaspState();
                    notifyCdmaSubscriptionInfoReady();
                }
                pollStateInternal(false);
                return;
            case 34:
                if (this.mPhone.isPhoneTypeGsm()) {
                    return;
                }
                AsyncResult asyncResult5 = (AsyncResult) message.obj;
                if (asyncResult5.exception == null) {
                    String[] strArr = (String[]) asyncResult5.result;
                    if (strArr != null && strArr.length >= 5) {
                        this.mMdn = strArr[0];
                        parseSidNid(strArr[1], strArr[2]);
                        this.mMin = strArr[3];
                        this.mPrlVersion = strArr[4];
                        log("GET_CDMA_SUBSCRIPTION: MDN=" + this.mMdn);
                        this.mIsMinInfoReady = true;
                        updateOtaspState();
                        notifyCdmaSubscriptionInfoReady();
                        if (!this.mIsSubscriptionFromRuim && this.mIccRecords != null) {
                            log("GET_CDMA_SUBSCRIPTION set imsi in mIccRecords");
                            this.mIccRecords.setImsi(getImsi());
                            return;
                        }
                        log("GET_CDMA_SUBSCRIPTION either mIccRecords is null or NV type device - not setting Imsi in mIccRecords");
                        return;
                    }
                    log("GET_CDMA_SUBSCRIPTION: error parsing cdmaSubscription params num=" + strArr.length);
                    return;
                }
                return;
            case 35:
                updatePhoneObject();
                this.mCi.getNetworkSelectionMode(obtainMessage(14));
                getSubscriptionInfoAndStartPollingThreads();
                return;
            case 37:
                AsyncResult asyncResult6 = (AsyncResult) message.obj;
                if (asyncResult6.exception == null) {
                    int i2 = ((int[]) asyncResult6.result)[0];
                    if (i2 == 8 || i2 == 10) {
                        log("EVENT_OTA_PROVISION_STATUS_CHANGE: Complete, Reload MDN");
                        this.mCi.getCDMASubscription(obtainMessage(34));
                        return;
                    }
                    return;
                }
                return;
            case 38:
                synchronized (this) {
                    this.mPendingRadioPowerOffAfterDataOff = false;
                    log("Wait for all data networks torn down timed out. Power off now.");
                    hangupAndPowerOff();
                }
                return;
            case 39:
                handleCdmaSubscriptionSource(this.mCdmaSSM.getCdmaSubscriptionSource());
                return;
            case 40:
                AsyncResult asyncResult7 = (AsyncResult) message.obj;
                if (asyncResult7.exception == null) {
                    this.mPrlVersion = Integer.toString(((int[]) asyncResult7.result)[0]);
                    return;
                }
                return;
            case 42:
                if (isSimAbsent()) {
                    log("EVENT_ICC_CHANGED: SIM absent");
                    cancelAllNotifications();
                    this.mMdn = null;
                    this.mMin = null;
                    this.mIsMinInfoReady = false;
                    this.mCdnr.updateEfFromRuim(null);
                    this.mCdnr.updateEfFromUsim(null);
                }
                onUpdateIccAvailability();
                UiccCardApplication uiccCardApplication = this.mUiccApplication;
                if (uiccCardApplication == null || uiccCardApplication.getState() != IccCardApplicationStatus.AppState.APPSTATE_READY) {
                    this.mIsSimReady = false;
                    updateSpnDisplay();
                    return;
                }
                return;
            case 43:
            case 44:
                Object obj3 = message.obj;
                if (obj3 != null) {
                    AsyncResult asyncResult8 = (AsyncResult) obj3;
                    if (asyncResult8.exception != null) {
                        log("EVENT_GET_CELL_INFO_LIST: error ret null, e=" + asyncResult8.exception);
                        th = asyncResult8.exception;
                    } else {
                        Object obj4 = asyncResult8.result;
                        if (obj4 == null) {
                            loge("Invalid CellInfo result");
                        } else {
                            List<CellInfo> list2 = (List) obj4;
                            updateOperatorNameForCellInfo(list2);
                            this.mLastCellInfoList = list2;
                            this.mPhone.notifyCellInfo(list2);
                            list = list2;
                            th = null;
                        }
                    }
                    synchronized (this.mPendingCellInfoRequests) {
                        if (this.mIsPendingCellInfoRequest) {
                            this.mIsPendingCellInfoRequest = false;
                            for (Message message2 : this.mPendingCellInfoRequests) {
                                AsyncResult.forMessage(message2, list, th);
                                message2.sendToTarget();
                            }
                            this.mPendingCellInfoRequests.clear();
                        }
                    }
                    return;
                }
                synchronized (this.mPendingCellInfoRequests) {
                    if (!this.mIsPendingCellInfoRequest) {
                        return;
                    }
                    if (SystemClock.elapsedRealtime() - this.mLastCellInfoReqTime < 2000) {
                        return;
                    }
                    loge("Timeout waiting for CellInfo; (everybody panic)!");
                    this.mLastCellInfoList = null;
                }
                th = null;
                synchronized (this.mPendingCellInfoRequests) {
                }
            case 45:
                log("EVENT_CHANGE_IMS_STATE:");
                setPowerStateToDesired();
                return;
            case 46:
                this.mCi.getImsRegistrationState(obtainMessage(47));
                return;
            case 47:
                AsyncResult asyncResult9 = (AsyncResult) message.obj;
                if (asyncResult9.exception == null) {
                    r6 = ((int[]) asyncResult9.result)[0] == 1;
                    this.mPhone.setImsRegistrationState(r6);
                    this.mImsRegistered = r6;
                    return;
                }
                return;
            case 48:
                log("EVENT_IMS_CAPABILITY_CHANGED");
                updateSpnDisplay();
                this.mImsCapabilityChangedRegistrants.notifyRegistrants();
                return;
            case 49:
                log("EVENT_ALL_DATA_DISCONNECTED");
                synchronized (this) {
                    if (this.mPendingRadioPowerOffAfterDataOff) {
                        for (Phone phone : PhoneFactory.getPhones()) {
                            if (phone.getDataNetworkController().areAllDataDisconnected()) {
                                phone.getDataNetworkController().unregisterDataNetworkControllerCallback(this.mDataDisconnectedCallback);
                            } else {
                                log("Still waiting for all data disconnected on phone: " + phone.getSubId());
                                r6 = false;
                            }
                        }
                        if (r6) {
                            this.mPendingRadioPowerOffAfterDataOff = false;
                            removeMessages(38);
                            log("Data disconnected for all phones, turn radio off now.");
                            hangupAndPowerOff();
                        }
                        return;
                    }
                    return;
                }
            case 51:
                AsyncResult asyncResult10 = (AsyncResult) message.obj;
                if (asyncResult10.exception == null) {
                    boolean booleanValue = ((Boolean) asyncResult10.result).booleanValue();
                    log("EVENT_RADIO_POWER_FROM_CARRIER: " + booleanValue);
                    setRadioPowerForReason(booleanValue, false, false, false, 2);
                    return;
                }
                return;
            case 53:
                log("EVENT_IMS_SERVICE_STATE_CHANGED");
                if (this.mSS.getState() != 0) {
                    GsmCdmaPhone gsmCdmaPhone = this.mPhone;
                    gsmCdmaPhone.notifyServiceStateChanged(gsmCdmaPhone.getServiceState());
                    return;
                }
                return;
            case 54:
                log("EVENT_RADIO_POWER_OFF_DONE");
                if (!this.mDeviceShuttingDown || this.mCi.getRadioState() == 2) {
                    return;
                }
                this.mCi.requestShutdown(null);
                return;
            case 55:
                AsyncResult asyncResult11 = (AsyncResult) message.obj;
                if (asyncResult11.exception == null) {
                    List<PhysicalChannelConfig> list3 = (List) asyncResult11.result;
                    if ((list3 == null || list3.isEmpty()) && this.mLastAnchorNrCellId != -1 && ((TelephonyManager) this.mPhone.getContext().getSystemService(TelephonyManager.class)).isRadioInterfaceCapabilitySupported("CAPABILITY_PHYSICAL_CHANNEL_CONFIG_1_6_SUPPORTED") && !this.mCarrierConfig.getBoolean("lte_endc_using_user_data_for_rrc_detection_bool") && this.mCarrierConfig.getBoolean("ratchet_nr_advanced_bandwidth_if_rrc_idle_bool")) {
                        log("Ignore empty PCC list when RRC idle.");
                        return;
                    }
                    this.mLastPhysicalChannelConfigList = list3;
                    if (updateNrStateFromPhysicalChannelConfigs(list3, this.mSS)) {
                        this.mNrStateChangedRegistrants.notifyRegistrants();
                        z = true;
                    } else {
                        z = false;
                    }
                    if (updateNrFrequencyRangeFromPhysicalChannelConfigs(list3, this.mSS)) {
                        this.mNrFrequencyChangedRegistrants.notifyRegistrants();
                        z = true;
                    }
                    int intValue = list3 != null ? ((Integer) list3.stream().filter(new Predicate() { // from class: com.android.internal.telephony.ServiceStateTracker$$ExternalSyntheticLambda8
                        @Override // java.util.function.Predicate
                        public final boolean test(Object obj5) {
                            boolean lambda$handleMessage$1;
                            lambda$handleMessage$1 = ServiceStateTracker.lambda$handleMessage$1((PhysicalChannelConfig) obj5);
                            return lambda$handleMessage$1;
                        }
                    }).map(new Function() { // from class: com.android.internal.telephony.ServiceStateTracker$$ExternalSyntheticLambda9
                        @Override // java.util.function.Function
                        public final Object apply(Object obj5) {
                            return Integer.valueOf(((PhysicalChannelConfig) obj5).getPhysicalCellId());
                        }
                    }).findFirst().orElse(-1)).intValue() : -1;
                    final boolean z2 = this.mCarrierConfig.getBoolean("include_lte_for_nr_advanced_threshold_bandwidth_bool");
                    int[] iArr = new int[0];
                    if (list3 != null) {
                        iArr = list3.stream().filter(new Predicate() { // from class: com.android.internal.telephony.ServiceStateTracker$$ExternalSyntheticLambda10
                            @Override // java.util.function.Predicate
                            public final boolean test(Object obj5) {
                                boolean lambda$handleMessage$2;
                                lambda$handleMessage$2 = ServiceStateTracker.lambda$handleMessage$2(z2, (PhysicalChannelConfig) obj5);
                                return lambda$handleMessage$2;
                            }
                        }).map(new ServiceStateTracker$$ExternalSyntheticLambda2()).mapToInt(new RILUtils$$ExternalSyntheticLambda6()).toArray();
                    }
                    if (intValue == this.mLastAnchorNrCellId && intValue != -1) {
                        log("Ratchet bandwidths since anchor NR cell is the same.");
                        equals = z | RatRatcheter.updateBandwidths(iArr, this.mSS);
                    } else {
                        log("Do not ratchet bandwidths since anchor NR cell is different (" + this.mLastAnchorNrCellId + "->" + intValue + ").");
                        this.mLastAnchorNrCellId = intValue;
                        equals = Arrays.equals(this.mSS.getCellBandwidths(), iArr) ^ true;
                        this.mSS.setCellBandwidths(iArr);
                    }
                    this.mPhone.notifyPhysicalChannelConfig(list3);
                    if (equals) {
                        GsmCdmaPhone gsmCdmaPhone2 = this.mPhone;
                        gsmCdmaPhone2.notifyServiceStateChanged(gsmCdmaPhone2.getServiceState());
                        this.mServiceStateChangedRegistrants.notifyRegistrants();
                        TelephonyMetrics.getInstance().writeServiceStateChanged(this.mPhone.getPhoneId(), this.mSS);
                        this.mPhone.getVoiceCallSessionStats().onServiceStateChanged(this.mSS);
                        ImsPhone imsPhone = (ImsPhone) this.mPhone.getImsPhone();
                        if (imsPhone != null) {
                            imsPhone.getImsStats().onServiceStateChanged(this.mSS);
                        }
                        this.mServiceStateStats.onServiceStateChanged(this.mSS);
                        return;
                    }
                    return;
                }
                return;
            case 56:
                AsyncResult asyncResult12 = (AsyncResult) message.obj;
                if (asyncResult12 == null) {
                    loge("Invalid null response to getCellIdentity!");
                    return;
                }
                Message message3 = (Message) asyncResult12.userObj;
                AsyncResult.forMessage(message3, getCellIdentity(), asyncResult12.exception);
                message3.sendToTarget();
                return;
            case 58:
                pollStateInternal(false);
                return;
            case TelephonyProto$RilErrno.RIL_E_NOT_PROVISIONED /* 62 */:
                log("EVENT_POWER_OFF_RADIO_IMS_DEREG_TIMEOUT triggered");
                powerOffRadioSafely();
                return;
            case 63:
                log("EVENT_RESET_LAST_KNOWN_CELL_IDENTITY triggered");
                this.mLastKnownCellIdentity = null;
                return;
            case 65:
                log("EVENT_TELECOM_VOICE_SERVICE_STATE_OVERRIDE_CHANGED");
                if (this.mSS.getState() != 0) {
                    GsmCdmaPhone gsmCdmaPhone3 = this.mPhone;
                    gsmCdmaPhone3.notifyServiceStateChanged(gsmCdmaPhone3.getServiceState());
                    return;
                }
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$handleMessage$1(PhysicalChannelConfig physicalChannelConfig) {
        return physicalChannelConfig.getNetworkType() == 20 && physicalChannelConfig.getConnectionStatus() == 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$handleMessage$2(boolean z, PhysicalChannelConfig physicalChannelConfig) {
        return z || physicalChannelConfig.getNetworkType() == 20;
    }

    private boolean isSimAbsent() {
        UiccCard uiccCard;
        UiccController uiccController = this.mUiccController;
        return uiccController == null || (uiccCard = uiccController.getUiccCard(this.mPhone.getPhoneId())) == null || uiccCard.getCardState() == IccCardStatus.CardState.CARDSTATE_ABSENT;
    }

    private static int[] getBandwidthsFromConfigs(List<PhysicalChannelConfig> list) {
        return list.stream().map(new ServiceStateTracker$$ExternalSyntheticLambda2()).mapToInt(new RILUtils$$ExternalSyntheticLambda6()).toArray();
    }

    protected boolean isSidsAllZeros() {
        if (this.mHomeSystemId == null) {
            return true;
        }
        int i = 0;
        while (true) {
            int[] iArr = this.mHomeSystemId;
            if (i >= iArr.length) {
                return true;
            }
            if (iArr[i] != 0) {
                return false;
            }
            i++;
        }
    }

    public ServiceState getServiceState() {
        return new ServiceState(this.mSS);
    }

    private boolean isHomeSid(int i) {
        if (this.mHomeSystemId != null) {
            int i2 = 0;
            while (true) {
                int[] iArr = this.mHomeSystemId;
                if (i2 >= iArr.length) {
                    break;
                } else if (i == iArr[i2]) {
                    return true;
                } else {
                    i2++;
                }
            }
        }
        return false;
    }

    public String getMdnNumber() {
        return this.mMdn;
    }

    public String getCdmaMin() {
        return this.mMin;
    }

    public String getPrlVersion() {
        return this.mPrlVersion;
    }

    public String getImsi() {
        String simOperatorNumericForPhone = ((TelephonyManager) this.mPhone.getContext().getSystemService("phone")).getSimOperatorNumericForPhone(this.mPhone.getPhoneId());
        if (TextUtils.isEmpty(simOperatorNumericForPhone) || getCdmaMin() == null) {
            return null;
        }
        return simOperatorNumericForPhone + getCdmaMin();
    }

    public boolean isMinInfoReady() {
        return this.mIsMinInfoReady;
    }

    public int getOtasp() {
        if (!this.mPhone.getIccRecordsLoaded()) {
            log("getOtasp: otasp uninitialized due to sim not loaded");
            return 0;
        }
        int i = 3;
        if (this.mPhone.isPhoneTypeGsm()) {
            log("getOtasp: otasp not needed for GSM");
            return 3;
        } else if (this.mIsSubscriptionFromRuim && this.mMin == null) {
            return 2;
        } else {
            String str = this.mMin;
            if (str == null || str.length() < 6) {
                log("getOtasp: bad mMin='" + this.mMin + "'");
                i = 1;
            } else if (this.mMin.equals(UNACTIVATED_MIN_VALUE) || this.mMin.substring(0, 6).equals(UNACTIVATED_MIN2_VALUE) || SystemProperties.getBoolean("test_cdma_setup", false)) {
                i = 2;
            }
            log("getOtasp: state=" + i);
            return i;
        }
    }

    protected void parseSidNid(String str, String str2) {
        if (str != null) {
            String[] split = str.split(",");
            this.mHomeSystemId = new int[split.length];
            for (int i = 0; i < split.length; i++) {
                try {
                    this.mHomeSystemId[i] = Integer.parseInt(split[i]);
                } catch (NumberFormatException e) {
                    loge("error parsing system id: " + e);
                }
            }
        }
        log("CDMA_SUBSCRIPTION: SID=" + str);
        if (str2 != null) {
            String[] split2 = str2.split(",");
            this.mHomeNetworkId = new int[split2.length];
            for (int i2 = 0; i2 < split2.length; i2++) {
                try {
                    this.mHomeNetworkId[i2] = Integer.parseInt(split2[i2]);
                } catch (NumberFormatException e2) {
                    loge("CDMA_SUBSCRIPTION: error parsing network id: " + e2);
                }
            }
        }
        log("CDMA_SUBSCRIPTION: NID=" + str2);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected void updateOtaspState() {
        int otasp = getOtasp();
        int i = this.mCurrentOtaspMode;
        this.mCurrentOtaspMode = otasp;
        if (i != otasp) {
            log("updateOtaspState: call notifyOtaspChanged old otaspMode=" + i + " new otaspMode=" + this.mCurrentOtaspMode);
            this.mPhone.notifyOtaspChanged(this.mCurrentOtaspMode);
        }
    }

    public void onAirplaneModeChanged(boolean z) {
        this.mLastNitzData = null;
        this.mNitzState.handleAirplaneModeChanged(z);
        this.mAirplaneModeChangedRegistrants.notifyResult(Boolean.valueOf(z));
    }

    protected Phone getPhone() {
        return this.mPhone;
    }

    protected void handlePollStateResult(int i, AsyncResult asyncResult) {
        boolean isRoamingBetweenOperators;
        if (asyncResult.userObj != this.mPollingContext) {
            return;
        }
        Throwable th = asyncResult.exception;
        if (th != null) {
            if (th instanceof IllegalStateException) {
                log("handlePollStateResult exception " + asyncResult.exception);
            }
            Throwable th2 = asyncResult.exception;
            CommandException.Error commandError = th2 instanceof CommandException ? ((CommandException) th2).getCommandError() : null;
            if (this.mCi.getRadioState() != 1) {
                log("handlePollStateResult: Invalid response due to radio off or unavailable. Set ServiceState to out of service.");
                pollStateInternal(false);
                return;
            } else if (commandError == CommandException.Error.RADIO_NOT_AVAILABLE) {
                loge("handlePollStateResult: RIL returned RADIO_NOT_AVAILABLE when radio is on.");
                cancelPollState();
                return;
            } else if (commandError != CommandException.Error.OP_NOT_ALLOWED_BEFORE_REG_NW) {
                loge("handlePollStateResult: RIL returned an error where it must succeed: " + asyncResult.exception);
            }
        } else {
            try {
                handlePollStateResultMessage(i, asyncResult);
            } catch (RuntimeException e) {
                loge("Exception while polling service state. Probably malformed RIL response." + e);
            }
        }
        int[] iArr = this.mPollingContext;
        int i2 = iArr[0] - 1;
        iArr[0] = i2;
        if (i2 == 0) {
            this.mNewSS.setEmergencyOnly(this.mEmergencyOnly);
            combinePsRegistrationStates(this.mNewSS);
            updateOperatorNameForServiceState(this.mNewSS);
            if (this.mPhone.isPhoneTypeGsm()) {
                updateRoamingState();
            } else {
                boolean z = !isSidsAllZeros() && isHomeSid(this.mNewSS.getCdmaSystemId());
                if (this.mIsSubscriptionFromRuim && (isRoamingBetweenOperators = isRoamingBetweenOperators(this.mNewSS.getVoiceRoaming(), this.mNewSS)) != this.mNewSS.getVoiceRoaming()) {
                    log("isRoamingBetweenOperators=" + isRoamingBetweenOperators + ". Override CDMA voice roaming to " + isRoamingBetweenOperators);
                    this.mNewSS.setVoiceRoaming(isRoamingBetweenOperators);
                }
                if (ServiceState.isCdma(getRilDataRadioTechnologyForWwan(this.mNewSS))) {
                    if (this.mNewSS.getState() == 0) {
                        boolean voiceRoaming = this.mNewSS.getVoiceRoaming();
                        if (this.mNewSS.getDataRoaming() != voiceRoaming) {
                            log("Data roaming != Voice roaming. Override data roaming to " + voiceRoaming);
                            this.mNewSS.setDataRoaming(voiceRoaming);
                        }
                    } else {
                        boolean isRoamIndForHomeSystem = isRoamIndForHomeSystem(this.mRoamingIndicator);
                        if (this.mNewSS.getDataRoaming() == isRoamIndForHomeSystem) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("isRoamIndForHomeSystem=");
                            sb.append(isRoamIndForHomeSystem);
                            sb.append(", override data roaming to ");
                            sb.append(!isRoamIndForHomeSystem);
                            log(sb.toString());
                            this.mNewSS.setDataRoaming(!isRoamIndForHomeSystem);
                        }
                    }
                }
                this.mNewSS.setCdmaDefaultRoamingIndicator(this.mDefaultRoamingIndicator);
                this.mNewSS.setCdmaRoamingIndicator(this.mRoamingIndicator);
                boolean z2 = !TextUtils.isEmpty(this.mPrlVersion);
                if (!z2 || this.mNewSS.getRilVoiceRadioTechnology() == 0) {
                    log("Turn off roaming indicator if !isPrlLoaded or voice RAT is unknown");
                    this.mNewSS.setCdmaRoamingIndicator(1);
                } else if (!isSidsAllZeros()) {
                    if (!z && !this.mIsInPrl) {
                        this.mNewSS.setCdmaRoamingIndicator(this.mDefaultRoamingIndicator);
                    } else if (z && !this.mIsInPrl) {
                        if (ServiceState.isPsOnlyTech(this.mNewSS.getRilVoiceRadioTechnology())) {
                            log("Turn off roaming indicator as voice is LTE or NR");
                            this.mNewSS.setCdmaRoamingIndicator(1);
                        } else {
                            this.mNewSS.setCdmaRoamingIndicator(2);
                        }
                    } else if (!z && this.mIsInPrl) {
                        this.mNewSS.setCdmaRoamingIndicator(this.mRoamingIndicator);
                    } else {
                        int i3 = this.mRoamingIndicator;
                        if (i3 <= 2) {
                            this.mNewSS.setCdmaRoamingIndicator(1);
                        } else {
                            this.mNewSS.setCdmaRoamingIndicator(i3);
                        }
                    }
                }
                if (this.mEriManager != null) {
                    int cdmaRoamingIndicator = this.mNewSS.getCdmaRoamingIndicator();
                    this.mNewSS.setCdmaEriIconIndex(this.mEriManager.getCdmaEriIconIndex(cdmaRoamingIndicator, this.mDefaultRoamingIndicator));
                    this.mNewSS.setCdmaEriIconMode(this.mEriManager.getCdmaEriIconMode(cdmaRoamingIndicator, this.mDefaultRoamingIndicator));
                }
                log("Set CDMA Roaming Indicator to: " + this.mNewSS.getCdmaRoamingIndicator() + ". voiceRoaming = " + this.mNewSS.getVoiceRoaming() + ". dataRoaming = " + this.mNewSS.getDataRoaming() + ", isPrlLoaded = " + z2 + ". namMatch = " + z + " , mIsInPrl = " + this.mIsInPrl + ", mRoamingIndicator = " + this.mRoamingIndicator + ", mDefaultRoamingIndicator= " + this.mDefaultRoamingIndicator);
            }
            pollStateDone();
        }
    }

    private boolean isRoamingBetweenOperators(boolean z, ServiceState serviceState) {
        return z && !isSameOperatorNameFromSimAndSS(serviceState);
    }

    private boolean updateNrFrequencyRangeFromPhysicalChannelConfigs(List<PhysicalChannelConfig> list, ServiceState serviceState) {
        int i;
        if (list != null) {
            i = 0;
            for (PhysicalChannelConfig physicalChannelConfig : list) {
                if (isNrPhysicalChannelConfig(physicalChannelConfig) && isInternetPhysicalChannelConfig(physicalChannelConfig)) {
                    i = Math.max(i, physicalChannelConfig.getFrequencyRange());
                }
            }
        } else {
            i = 0;
        }
        boolean z = i != serviceState.getNrFrequencyRange();
        if (z) {
            log(String.format("NR frequency range changed from %s to %s.", ServiceState.frequencyRangeToString(serviceState.getNrFrequencyRange()), ServiceState.frequencyRangeToString(i)));
        }
        serviceState.setNrFrequencyRange(i);
        return z;
    }

    private boolean updateNrStateFromPhysicalChannelConfigs(List<PhysicalChannelConfig> list, ServiceState serviceState) {
        boolean z;
        int nrState;
        NetworkRegistrationInfo networkRegistrationInfo = serviceState.getNetworkRegistrationInfo(2, 1);
        if (networkRegistrationInfo == null || list == null) {
            return false;
        }
        Iterator<PhysicalChannelConfig> it = list.iterator();
        while (true) {
            if (!it.hasNext()) {
                z = false;
                break;
            }
            PhysicalChannelConfig next = it.next();
            if (isNrPhysicalChannelConfig(next) && isInternetPhysicalChannelConfig(next) && next.getConnectionStatus() == 2) {
                z = true;
                break;
            }
        }
        int nrState2 = networkRegistrationInfo.getNrState();
        if (z) {
            nrState = 3;
        } else {
            networkRegistrationInfo.updateNrState();
            nrState = networkRegistrationInfo.getNrState();
        }
        boolean z2 = nrState != nrState2;
        if (z2) {
            log(String.format("NR state changed from %s to %s.", NetworkRegistrationInfo.nrStateToString(nrState2), NetworkRegistrationInfo.nrStateToString(nrState)));
        }
        networkRegistrationInfo.setNrState(nrState);
        serviceState.addNetworkRegistrationInfo(networkRegistrationInfo);
        return z2;
    }

    private boolean isNrPhysicalChannelConfig(PhysicalChannelConfig physicalChannelConfig) {
        return physicalChannelConfig.getNetworkType() == 20;
    }

    private boolean isInternetPhysicalChannelConfig(PhysicalChannelConfig physicalChannelConfig) {
        for (int i : physicalChannelConfig.getContextIds()) {
            if (this.mPhone.getDataNetworkController().isInternetNetwork(i)) {
                return true;
            }
        }
        return false;
    }

    private void combinePsRegistrationStates(ServiceState serviceState) {
        NetworkRegistrationInfo networkRegistrationInfo = serviceState.getNetworkRegistrationInfo(2, 2);
        NetworkRegistrationInfo networkRegistrationInfo2 = serviceState.getNetworkRegistrationInfo(2, 1);
        boolean isAnyApnOnIwlan = this.mAccessNetworksManager.isAnyApnOnIwlan();
        serviceState.setIwlanPreferred(isAnyApnOnIwlan);
        if (networkRegistrationInfo != null && networkRegistrationInfo.getAccessNetworkTechnology() == 18 && networkRegistrationInfo.getNetworkRegistrationState() == 1 && isAnyApnOnIwlan) {
            serviceState.setDataRegState(0);
        } else if (networkRegistrationInfo2 != null) {
            serviceState.setDataRegState(regCodeToServiceState(networkRegistrationInfo2.getNetworkRegistrationState()));
        }
        log("combinePsRegistrationStates: " + serviceState);
    }

    protected void handlePollStateResultMessage(int i, AsyncResult asyncResult) {
        int i2;
        int i3 = 0;
        boolean z = false;
        if (i == 4) {
            NetworkRegistrationInfo networkRegistrationInfo = (NetworkRegistrationInfo) asyncResult.result;
            VoiceSpecificRegistrationInfo voiceSpecificInfo = networkRegistrationInfo.getVoiceSpecificInfo();
            int networkRegistrationState = networkRegistrationInfo.getNetworkRegistrationState();
            boolean z2 = voiceSpecificInfo.cssSupported;
            this.mNewSS.setVoiceRegState(regCodeToServiceState(networkRegistrationState));
            this.mNewSS.setCssIndicator(z2 ? 1 : 0);
            this.mNewSS.addNetworkRegistrationInfo(networkRegistrationInfo);
            setPhyCellInfoFromCellIdentity(this.mNewSS, networkRegistrationInfo.getCellIdentity());
            int rejectCause = networkRegistrationInfo.getRejectCause();
            boolean isEmergencyEnabled = networkRegistrationInfo.isEmergencyEnabled();
            this.mCSEmergencyOnly = isEmergencyEnabled;
            this.mEmergencyOnly = isEmergencyEnabled || this.mPSEmergencyOnly;
            if (this.mPhone.isPhoneTypeGsm()) {
                this.mGsmVoiceRoaming = regCodeIsRoaming(networkRegistrationState);
                this.mNewRejectCode = rejectCause;
            } else {
                int i4 = voiceSpecificInfo.roamingIndicator;
                int i5 = voiceSpecificInfo.systemIsInPrl;
                int i6 = voiceSpecificInfo.defaultRoamingIndicator;
                this.mRegistrationState = networkRegistrationState;
                this.mNewSS.setVoiceRoaming(regCodeIsRoaming(networkRegistrationState) && !isRoamIndForHomeSystem(i4));
                this.mRoamingIndicator = i4;
                this.mIsInPrl = i5 != 0;
                this.mDefaultRoamingIndicator = i6;
                CellIdentity cellIdentity = networkRegistrationInfo.getCellIdentity();
                if (cellIdentity == null || cellIdentity.getType() != 2) {
                    i2 = 0;
                } else {
                    CellIdentityCdma cellIdentityCdma = (CellIdentityCdma) cellIdentity;
                    i3 = cellIdentityCdma.getSystemId();
                    i2 = cellIdentityCdma.getNetworkId();
                }
                this.mNewSS.setCdmaSystemAndNetworkId(i3, i2);
                if (rejectCause == 0) {
                    this.mRegistrationDeniedReason = REGISTRATION_DENIED_GEN;
                } else if (rejectCause == 1) {
                    this.mRegistrationDeniedReason = REGISTRATION_DENIED_AUTH;
                } else {
                    this.mRegistrationDeniedReason = PhoneConfigurationManager.SSSS;
                }
                if (this.mRegistrationState == 3) {
                    log("Registration denied, " + this.mRegistrationDeniedReason);
                }
            }
            log("handlePollStateResultMessage: CS cellular. " + networkRegistrationInfo);
        } else if (i == 5) {
            NetworkRegistrationInfo networkRegistrationInfo2 = (NetworkRegistrationInfo) asyncResult.result;
            this.mNewSS.addNetworkRegistrationInfo(networkRegistrationInfo2);
            DataSpecificRegistrationInfo dataSpecificInfo = networkRegistrationInfo2.getDataSpecificInfo();
            int networkRegistrationState2 = networkRegistrationInfo2.getNetworkRegistrationState();
            int regCodeToServiceState = regCodeToServiceState(networkRegistrationState2);
            int networkTypeToRilRadioTechnology = ServiceState.networkTypeToRilRadioTechnology(networkRegistrationInfo2.getAccessNetworkTechnology());
            log("handlePollStateResultMessage: PS cellular. " + networkRegistrationInfo2);
            if (regCodeToServiceState == 1) {
                this.mLastPhysicalChannelConfigList = null;
            }
            boolean isEmergencyEnabled2 = networkRegistrationInfo2.isEmergencyEnabled();
            this.mPSEmergencyOnly = isEmergencyEnabled2;
            if (this.mCSEmergencyOnly || isEmergencyEnabled2) {
                z = true;
            }
            this.mEmergencyOnly = z;
            if (this.mPhone.isPhoneTypeGsm()) {
                this.mNewReasonDataDenied = networkRegistrationInfo2.getRejectCause();
                this.mNewMaxDataCalls = dataSpecificInfo.maxDataCalls;
                boolean regCodeIsRoaming = regCodeIsRoaming(networkRegistrationState2);
                this.mGsmDataRoaming = regCodeIsRoaming;
                this.mNewSS.setDataRoamingFromRegistration(regCodeIsRoaming);
            } else if (this.mPhone.isPhoneTypeCdma()) {
                boolean regCodeIsRoaming2 = regCodeIsRoaming(networkRegistrationState2);
                this.mNewSS.setDataRoaming(regCodeIsRoaming2);
                this.mNewSS.setDataRoamingFromRegistration(regCodeIsRoaming2);
            } else {
                int rilDataRadioTechnologyForWwan = getRilDataRadioTechnologyForWwan(this.mSS);
                if ((rilDataRadioTechnologyForWwan == 0 && networkTypeToRilRadioTechnology != 0) || ((ServiceState.isCdma(rilDataRadioTechnologyForWwan) && ServiceState.isPsOnlyTech(networkTypeToRilRadioTechnology)) || (ServiceState.isPsOnlyTech(rilDataRadioTechnologyForWwan) && ServiceState.isCdma(networkTypeToRilRadioTechnology)))) {
                    this.mPhone.getSignalStrengthController().getSignalStrengthFromCi();
                }
                boolean regCodeIsRoaming3 = regCodeIsRoaming(networkRegistrationState2);
                this.mNewSS.setDataRoaming(regCodeIsRoaming3);
                this.mNewSS.setDataRoamingFromRegistration(regCodeIsRoaming3);
            }
            this.mPhone.getSignalStrengthController().updateServiceStateArfcnRsrpBoost(this.mNewSS, networkRegistrationInfo2.getCellIdentity());
        } else if (i == 6) {
            NetworkRegistrationInfo networkRegistrationInfo3 = (NetworkRegistrationInfo) asyncResult.result;
            this.mNewSS.addNetworkRegistrationInfo(networkRegistrationInfo3);
            log("handlePollStateResultMessage: PS IWLAN. " + networkRegistrationInfo3);
        } else if (i != 7) {
            if (i == 14) {
                int[] iArr = (int[]) asyncResult.result;
                this.mNewSS.setIsManualSelection(iArr[0] == 1);
                if (iArr[0] == 1 && this.mPhone.shouldForceAutoNetworkSelect()) {
                    this.mPhone.setNetworkSelectionModeAutomatic(null);
                    log(" Forcing Automatic Network Selection, manual selection is not allowed");
                    return;
                }
                return;
            }
            loge("handlePollStateResultMessage: Unexpected RIL response received: " + i);
        } else if (this.mPhone.isPhoneTypeGsm()) {
            String[] strArr = (String[]) asyncResult.result;
            if (strArr == null || strArr.length < 3) {
                return;
            }
            this.mNewSS.setOperatorAlphaLongRaw(strArr[0]);
            this.mNewSS.setOperatorAlphaShortRaw(strArr[1]);
            String operatorBrandOverride = getOperatorBrandOverride();
            this.mCdnr.updateEfForBrandOverride(operatorBrandOverride);
            if (operatorBrandOverride != null) {
                log("EVENT_POLL_STATE_OPERATOR: use brandOverride=" + operatorBrandOverride);
                this.mNewSS.setOperatorName(operatorBrandOverride, operatorBrandOverride, strArr[2]);
                return;
            }
            this.mNewSS.setOperatorName(strArr[0], strArr[1], strArr[2]);
        } else {
            String[] strArr2 = (String[]) asyncResult.result;
            if (strArr2 != null && strArr2.length >= 3) {
                String str = strArr2[2];
                if (str == null || str.length() < 5 || "00000".equals(strArr2[2])) {
                    strArr2[2] = SystemProperties.get(GsmCdmaPhone.PROPERTY_CDMA_HOME_OPERATOR_NUMERIC, "00000");
                    log("RIL_REQUEST_OPERATOR.response[2], the numeric,  is bad. Using SystemProperties 'ro.cdma.home.operator.numeric'= " + strArr2[2]);
                }
                if (!this.mIsSubscriptionFromRuim) {
                    this.mNewSS.setOperatorName(strArr2[0], strArr2[1], strArr2[2]);
                    return;
                }
                String operatorBrandOverride2 = getOperatorBrandOverride();
                this.mCdnr.updateEfForBrandOverride(operatorBrandOverride2);
                if (operatorBrandOverride2 != null) {
                    this.mNewSS.setOperatorName(operatorBrandOverride2, operatorBrandOverride2, strArr2[2]);
                    return;
                } else {
                    this.mNewSS.setOperatorName(strArr2[0], strArr2[1], strArr2[2]);
                    return;
                }
            }
            log("EVENT_POLL_STATE_OPERATOR_CDMA: error parsing opNames");
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:23:0x0048  */
    /* JADX WARN: Removed duplicated region for block: B:24:0x004e  */
    /* JADX WARN: Removed duplicated region for block: B:28:0x0056  */
    /* JADX WARN: Removed duplicated region for block: B:30:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private static long getCidFromCellIdentity(CellIdentity cellIdentity) {
        int cid;
        long j;
        if (cellIdentity == null) {
            return -1L;
        }
        int type = cellIdentity.getType();
        if (type == 1) {
            cid = ((CellIdentityGsm) cellIdentity).getCid();
        } else if (type == 3) {
            cid = ((CellIdentityLte) cellIdentity).getCi();
        } else if (type == 4) {
            cid = ((CellIdentityWcdma) cellIdentity).getCid();
        } else if (type == 5) {
            cid = ((CellIdentityTdscdma) cellIdentity).getCid();
        } else {
            j = type != 6 ? -1L : ((CellIdentityNr) cellIdentity).getNci();
            if (j != (cellIdentity.getType() != 6 ? Long.MAX_VALUE : 2147483647L)) {
                return -1L;
            }
            return j;
        }
        j = cid;
        if (j != (cellIdentity.getType() != 6 ? Long.MAX_VALUE : 2147483647L)) {
        }
    }

    private static int getAreaCodeFromCellIdentity(CellIdentity cellIdentity) {
        if (cellIdentity == null) {
            return KeepaliveStatus.INVALID_HANDLE;
        }
        int type = cellIdentity.getType();
        if (type != 1) {
            if (type != 3) {
                if (type != 4) {
                    if (type != 5) {
                        return type != 6 ? KeepaliveStatus.INVALID_HANDLE : ((CellIdentityNr) cellIdentity).getTac();
                    }
                    return ((CellIdentityTdscdma) cellIdentity).getLac();
                }
                return ((CellIdentityWcdma) cellIdentity).getLac();
            }
            return ((CellIdentityLte) cellIdentity).getTac();
        }
        return ((CellIdentityGsm) cellIdentity).getLac();
    }

    private void setPhyCellInfoFromCellIdentity(ServiceState serviceState, CellIdentity cellIdentity) {
        if (cellIdentity == null) {
            log("Could not set ServiceState channel number. CellIdentity null");
            return;
        }
        serviceState.setChannelNumber(cellIdentity.getChannelNumber());
        PhysicalChannelConfig primaryPhysicalChannelConfigForCell = getPrimaryPhysicalChannelConfigForCell(this.mLastPhysicalChannelConfigList, cellIdentity);
        int i = 0;
        int[] iArr = null;
        if (cellIdentity instanceof CellIdentityLte) {
            CellIdentityLte cellIdentityLte = (CellIdentityLte) cellIdentity;
            if (primaryPhysicalChannelConfigForCell != null) {
                int[] bandwidthsFromConfigs = getBandwidthsFromConfigs(this.mLastPhysicalChannelConfigList);
                int length = bandwidthsFromConfigs.length;
                while (true) {
                    if (i >= length) {
                        iArr = bandwidthsFromConfigs;
                        break;
                    }
                    int i2 = bandwidthsFromConfigs[i];
                    if (!isValidLteBandwidthKhz(i2)) {
                        loge("Invalid LTE Bandwidth in RegistrationState, " + i2);
                        break;
                    }
                    i++;
                }
            }
            if (iArr == null || iArr.length == 1) {
                int bandwidth = cellIdentityLte.getBandwidth();
                if (isValidLteBandwidthKhz(bandwidth)) {
                    iArr = new int[]{bandwidth};
                } else if (bandwidth != Integer.MAX_VALUE) {
                    loge("Invalid LTE Bandwidth in RegistrationState, " + bandwidth);
                }
            }
        } else if ((cellIdentity instanceof CellIdentityNr) && primaryPhysicalChannelConfigForCell != null) {
            int[] bandwidthsFromConfigs2 = getBandwidthsFromConfigs(this.mLastPhysicalChannelConfigList);
            int length2 = bandwidthsFromConfigs2.length;
            while (true) {
                if (i >= length2) {
                    iArr = bandwidthsFromConfigs2;
                    break;
                }
                int i3 = bandwidthsFromConfigs2[i];
                if (!isValidNrBandwidthKhz(i3)) {
                    loge("Invalid NR Bandwidth in RegistrationState, " + i3);
                    break;
                }
                i++;
            }
        }
        if (iArr == null && primaryPhysicalChannelConfigForCell != null && primaryPhysicalChannelConfigForCell.getCellBandwidthDownlinkKhz() != 0) {
            iArr = new int[]{primaryPhysicalChannelConfigForCell.getCellBandwidthDownlinkKhz()};
        }
        if (iArr != null) {
            serviceState.setCellBandwidths(iArr);
        }
    }

    private static PhysicalChannelConfig getPrimaryPhysicalChannelConfigForCell(List<PhysicalChannelConfig> list, CellIdentity cellIdentity) {
        boolean z;
        int pci;
        int i;
        if (!ArrayUtils.isEmpty(list) && (((z = cellIdentity instanceof CellIdentityLte)) || (cellIdentity instanceof CellIdentityNr))) {
            if (z) {
                pci = ((CellIdentityLte) cellIdentity).getPci();
                i = 13;
            } else {
                pci = ((CellIdentityNr) cellIdentity).getPci();
                i = 20;
            }
            for (PhysicalChannelConfig physicalChannelConfig : list) {
                if (physicalChannelConfig.getConnectionStatus() == 1 && physicalChannelConfig.getNetworkType() == i && physicalChannelConfig.getPhysicalCellId() == pci) {
                    return physicalChannelConfig;
                }
            }
        }
        return null;
    }

    private boolean isRoamIndForHomeSystem(int i) {
        int[] intArray = this.mCarrierConfig.getIntArray("cdma_enhanced_roaming_indicator_for_home_network_int_array");
        log("isRoamIndForHomeSystem: homeRoamIndicators=" + Arrays.toString(intArray));
        if (intArray != null) {
            for (int i2 : intArray) {
                if (i2 == i) {
                    return true;
                }
            }
            log("isRoamIndForHomeSystem: No match found against list for roamInd=" + i);
            return false;
        }
        log("isRoamIndForHomeSystem: No list found");
        return false;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected void updateRoamingState() {
        boolean z = true;
        if (this.mPhone.isPhoneTypeGsm()) {
            boolean z2 = this.mGsmVoiceRoaming || this.mGsmDataRoaming;
            if (z2 && !isOperatorConsideredRoaming(this.mNewSS) && (isSameNamedOperators(this.mNewSS) || isOperatorConsideredNonRoaming(this.mNewSS))) {
                log("updateRoamingState: resource override set non roaming.isSameNamedOperators=" + isSameNamedOperators(this.mNewSS) + ",isOperatorConsideredNonRoaming=" + isOperatorConsideredNonRoaming(this.mNewSS));
                z2 = false;
            }
            if (alwaysOnHomeNetwork(this.mCarrierConfig)) {
                log("updateRoamingState: carrier config override always on home network");
            } else if (isNonRoamingInGsmNetwork(this.mCarrierConfig, this.mNewSS.getOperatorNumeric())) {
                log("updateRoamingState: carrier config override set non roaming:" + this.mNewSS.getOperatorNumeric());
            } else {
                if (isRoamingInGsmNetwork(this.mCarrierConfig, this.mNewSS.getOperatorNumeric())) {
                    log("updateRoamingState: carrier config override set roaming:" + this.mNewSS.getOperatorNumeric());
                } else {
                    z = z2;
                }
                this.mNewSS.setRoaming(z);
                return;
            }
            z = false;
            this.mNewSS.setRoaming(z);
            return;
        }
        String num = Integer.toString(this.mNewSS.getCdmaSystemId());
        if (alwaysOnHomeNetwork(this.mCarrierConfig)) {
            log("updateRoamingState: carrier config override always on home network");
            setRoamingOff();
        } else if (isNonRoamingInGsmNetwork(this.mCarrierConfig, this.mNewSS.getOperatorNumeric()) || isNonRoamingInCdmaNetwork(this.mCarrierConfig, num)) {
            log("updateRoamingState: carrier config override set non-roaming:" + this.mNewSS.getOperatorNumeric() + ", " + num);
            setRoamingOff();
        } else if (isRoamingInGsmNetwork(this.mCarrierConfig, this.mNewSS.getOperatorNumeric()) || isRoamingInCdmaNetwork(this.mCarrierConfig, num)) {
            log("updateRoamingState: carrier config override set roaming:" + this.mNewSS.getOperatorNumeric() + ", " + num);
            setRoamingOn();
        }
        if (TelephonyUtils.IS_DEBUGGABLE && SystemProperties.getBoolean("telephony.test.forceRoaming", false)) {
            this.mNewSS.setRoaming(true);
        }
    }

    private void setRoamingOn() {
        this.mNewSS.setRoaming(true);
        this.mNewSS.setCdmaEriIconIndex(0);
        this.mNewSS.setCdmaEriIconMode(0);
    }

    private void setRoamingOff() {
        this.mNewSS.setRoaming(false);
        this.mNewSS.setCdmaEriIconIndex(1);
    }

    private void updateOperatorNameFromCarrierConfig() {
        if (this.mPhone.isPhoneTypeGsm() || this.mSS.getRoaming()) {
            return;
        }
        if (((this.mUiccController.getUiccPort(getPhoneId()) == null || this.mUiccController.getUiccPort(getPhoneId()).getOperatorBrandOverride() == null) ? false : true) || !this.mCarrierConfig.getBoolean("cdma_home_registered_plmn_name_override_bool")) {
            return;
        }
        String string = this.mCarrierConfig.getString("cdma_home_registered_plmn_name_string");
        log("updateOperatorNameFromCarrierConfig: changing from " + this.mSS.getOperatorAlpha() + " to " + string);
        ServiceState serviceState = this.mSS;
        serviceState.setOperatorName(string, string, serviceState.getOperatorNumeric());
    }

    private void notifySpnDisplayUpdate(CarrierDisplayNameData carrierDisplayNameData) {
        int subId = this.mPhone.getSubId();
        if (this.mSubId != subId || carrierDisplayNameData.shouldShowPlmn() != this.mCurShowPlmn || carrierDisplayNameData.shouldShowSpn() != this.mCurShowSpn || !TextUtils.equals(carrierDisplayNameData.getSpn(), this.mCurSpn) || !TextUtils.equals(carrierDisplayNameData.getDataSpn(), this.mCurDataSpn) || !TextUtils.equals(carrierDisplayNameData.getPlmn(), this.mCurPlmn)) {
            String format = String.format("updateSpnDisplay: changed sending intent, rule=%d, showPlmn='%b', plmn='%s', showSpn='%b', spn='%s', dataSpn='%s', subId='%d'", Integer.valueOf(getCarrierNameDisplayBitmask(this.mSS)), Boolean.valueOf(carrierDisplayNameData.shouldShowPlmn()), carrierDisplayNameData.getPlmn(), Boolean.valueOf(carrierDisplayNameData.shouldShowSpn()), carrierDisplayNameData.getSpn(), carrierDisplayNameData.getDataSpn(), Integer.valueOf(subId));
            this.mCdnrLogs.log(format);
            log("updateSpnDisplay: " + format);
            Intent intent = new Intent("android.telephony.action.SERVICE_PROVIDERS_UPDATED");
            intent.putExtra("android.telephony.extra.SHOW_SPN", carrierDisplayNameData.shouldShowSpn());
            intent.putExtra("android.telephony.extra.SPN", carrierDisplayNameData.getSpn());
            intent.putExtra("android.telephony.extra.DATA_SPN", carrierDisplayNameData.getDataSpn());
            intent.putExtra("android.telephony.extra.SHOW_PLMN", carrierDisplayNameData.shouldShowPlmn());
            intent.putExtra("android.telephony.extra.PLMN", carrierDisplayNameData.getPlmn());
            SubscriptionManager.putPhoneIdAndSubIdExtra(intent, this.mPhone.getPhoneId());
            this.mPhone.getContext().sendStickyBroadcastAsUser(intent, UserHandle.ALL);
            if (this.mPhone.isSubscriptionManagerServiceEnabled()) {
                if (SubscriptionManager.isValidSubscriptionId(subId)) {
                    this.mSubscriptionManagerService.setCarrierName(subId, TextUtils.emptyIfNull(getCarrierName(carrierDisplayNameData.shouldShowPlmn(), carrierDisplayNameData.getPlmn(), carrierDisplayNameData.shouldShowSpn(), carrierDisplayNameData.getSpn())));
                }
            } else if (!this.mSubscriptionController.setPlmnSpn(this.mPhone.getPhoneId(), carrierDisplayNameData.shouldShowPlmn(), carrierDisplayNameData.getPlmn(), carrierDisplayNameData.shouldShowSpn(), carrierDisplayNameData.getSpn())) {
                this.mSpnUpdatePending = true;
            }
        }
        this.mSubId = subId;
        this.mCurShowSpn = carrierDisplayNameData.shouldShowSpn();
        this.mCurShowPlmn = carrierDisplayNameData.shouldShowPlmn();
        this.mCurSpn = carrierDisplayNameData.getSpn();
        this.mCurDataSpn = carrierDisplayNameData.getDataSpn();
        this.mCurPlmn = carrierDisplayNameData.getPlmn();
    }

    private String getCarrierName(boolean z, String str, boolean z2, String str2) {
        if (!z) {
            return z2 ? str2 : PhoneConfigurationManager.SSSS;
        } else if (!z2 || Objects.equals(str2, str)) {
            return str;
        } else {
            String str3 = this.mPhone.getContext().getString(17040559).toString();
            return str + str3 + str2;
        }
    }

    private void updateSpnDisplayCdnr() {
        log("updateSpnDisplayCdnr+");
        notifySpnDisplayUpdate(this.mCdnr.getCarrierDisplayNameData());
        log("updateSpnDisplayCdnr-");
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    @VisibleForTesting
    public void updateSpnDisplay() {
        if (this.mCarrierConfig.getBoolean("enable_carrier_display_name_resolver_bool")) {
            updateSpnDisplayCdnr();
        } else {
            updateSpnDisplayLegacy();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:113:0x0299, code lost:
        if (android.text.TextUtils.equals(r10, r0) == false) goto L68;
     */
    /* JADX WARN: Removed duplicated region for block: B:77:0x01ef  */
    /* JADX WARN: Removed duplicated region for block: B:89:0x022d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void updateSpnDisplayLegacy() {
        String str;
        String str2;
        String str3;
        String str4;
        String str5;
        String str6;
        String str7;
        boolean z;
        boolean z2;
        log("updateSpnDisplayLegacy+");
        int combinedRegState = getCombinedRegState(this.mSS);
        String str8 = null;
        boolean z3 = false;
        if (this.mPhone.getImsPhone() != null && this.mPhone.getImsPhone().isWifiCallingEnabled() && this.mPhone.isImsRegistered() && combinedRegState == 0 && this.mSS.getDataNetworkType() == 18) {
            int i = this.mCarrierConfig.getInt("wfc_spn_format_idx_int");
            int i2 = this.mCarrierConfig.getInt("wfc_data_spn_format_idx_int");
            int i3 = this.mCarrierConfig.getInt("wfc_flight_mode_spn_format_idx_int");
            String[] stringArray = SubscriptionManager.getResourcesForSubId(this.mPhone.getContext(), this.mPhone.getSubId(), this.mCarrierConfig.getBoolean("wfc_spn_use_root_locale")).getStringArray(17236216);
            if (i < 0 || i >= stringArray.length) {
                loge("updateSpnDisplay: KEY_WFC_SPN_FORMAT_IDX_INT out of bounds: " + i);
                i = 0;
            }
            if (i2 < 0 || i2 >= stringArray.length) {
                loge("updateSpnDisplay: KEY_WFC_DATA_SPN_FORMAT_IDX_INT out of bounds: " + i2);
                i2 = 0;
            }
            if (i3 < 0 || i3 >= stringArray.length) {
                i3 = i;
            }
            str = stringArray[i];
            str2 = stringArray[i2];
            str3 = stringArray[i3];
        } else {
            str = null;
            str2 = null;
            str3 = null;
        }
        if (this.mPhone.getImsPhone() == null || this.mPhone.getImsPhone() == null || this.mPhone.getImsPhone().getImsRegistrationTech() != 2) {
            str4 = null;
        } else {
            int i4 = this.mCarrierConfig.getInt("cross_sim_spn_format_int");
            String[] stringArray2 = SubscriptionManager.getResourcesForSubId(this.mPhone.getContext(), this.mPhone.getSubId(), this.mCarrierConfig.getBoolean("wfc_spn_use_root_locale")).getStringArray(17236165);
            if (i4 < 0 || i4 >= stringArray2.length) {
                loge("updateSpnDisplay: KEY_CROSS_SIM_SPN_FORMAT_INT out of bounds: " + i4);
                i4 = 0;
            }
            str4 = stringArray2[i4];
        }
        if (this.mPhone.isPhoneTypeGsm()) {
            int carrierNameDisplayBitmask = getCarrierNameDisplayBitmask(this.mSS);
            if (combinedRegState == 1 || combinedRegState == 2) {
                if (!(shouldForceDisplayNoService() && !this.mIsSimReady) && Phone.isEmergencyCallOnly()) {
                    str6 = Resources.getSystem().getText(17040180).toString();
                    z2 = false;
                } else {
                    str6 = Resources.getSystem().getText(17040594).toString();
                    z2 = true;
                }
                log("updateSpnDisplay: radio is on but out of service, set plmn='" + str6 + "'");
            } else if (combinedRegState == 0) {
                str6 = this.mSS.getOperatorAlpha();
                boolean z4 = !TextUtils.isEmpty(str6) && (carrierNameDisplayBitmask & 2) == 2;
                log("updateSpnDisplay: rawPlmn = " + str6);
                z = z4;
                z2 = false;
                str7 = getServiceProviderName();
                boolean z5 = z2 && !TextUtils.isEmpty(str7) && (carrierNameDisplayBitmask & 1) == 1;
                log("updateSpnDisplay: rawSpn = " + str7);
                if (TextUtils.isEmpty(str4)) {
                    if (!TextUtils.isEmpty(str7)) {
                        str8 = String.format(str4, str7.trim());
                        str7 = str8;
                        z = false;
                        z3 = true;
                    } else {
                        if (!TextUtils.isEmpty(str6)) {
                            String trim = str6.trim();
                            if (this.mIccRecords != null && this.mCarrierConfig.getBoolean("wfc_carrier_name_override_by_pnn_bool")) {
                                trim = this.mIccRecords.getPnnHomeName();
                            }
                            str6 = String.format(str4, trim);
                        }
                        z3 = z5;
                        str8 = str7;
                    }
                } else if (!TextUtils.isEmpty(str7) && !TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2)) {
                    if (this.mSS.getState() == 3) {
                        str = str3;
                    }
                    String trim2 = str7.trim();
                    str8 = String.format(str, trim2);
                    str7 = String.format(str2, trim2);
                    z = false;
                    z3 = true;
                } else {
                    if (!TextUtils.isEmpty(str6) && !TextUtils.isEmpty(str)) {
                        String trim3 = str6.trim();
                        if (this.mIccRecords != null && this.mCarrierConfig.getBoolean("wfc_carrier_name_override_by_pnn_bool")) {
                            trim3 = this.mIccRecords.getPnnHomeName();
                        }
                        str6 = String.format(str, trim3);
                    } else if (this.mSS.getState() != 3) {
                        if (z) {
                        }
                    }
                    z3 = z5;
                    str8 = str7;
                }
            } else {
                log("updateSpnDisplay: radio is off w/ showPlmn=true plmn=" + ((String) null));
                str6 = null;
                z2 = false;
            }
            z = true;
            str7 = getServiceProviderName();
            if (z2) {
            }
            log("updateSpnDisplay: rawSpn = " + str7);
            if (TextUtils.isEmpty(str4)) {
            }
        } else {
            String operatorNameFromEri = getOperatorNameFromEri();
            if (operatorNameFromEri != null) {
                this.mSS.setOperatorAlphaLong(operatorNameFromEri);
            }
            updateOperatorNameFromCarrierConfig();
            String operatorAlpha = this.mSS.getOperatorAlpha();
            log("updateSpnDisplay: cdma rawPlmn = " + operatorAlpha);
            boolean z6 = operatorAlpha != null;
            if (!TextUtils.isEmpty(operatorAlpha) && !TextUtils.isEmpty(str)) {
                str5 = String.format(str, operatorAlpha.trim());
            } else if (this.mCi.getRadioState() == 0) {
                log("updateSpnDisplay: overwriting plmn from " + operatorAlpha + " to null as radio state is off");
                str5 = null;
            } else {
                str5 = operatorAlpha;
            }
            if (combinedRegState == 1) {
                str6 = Resources.getSystem().getText(17040594).toString();
                log("updateSpnDisplay: radio is on but out of svc, set plmn='" + str6 + "'");
            } else {
                str6 = str5;
            }
            str7 = null;
            z = z6;
        }
        notifySpnDisplayUpdate(new CarrierDisplayNameData.Builder().setSpn(str8).setDataSpn(str7).setShowSpn(z3).setPlmn(str6).setShowPlmn(z).build());
        log("updateSpnDisplayLegacy-");
    }

    public boolean shouldForceDisplayNoService() {
        String[] stringArray = this.mPhone.getContext().getResources().getStringArray(17236057);
        if (ArrayUtils.isEmpty(stringArray)) {
            return false;
        }
        this.mLastKnownNetworkCountry = this.mLocaleTracker.getLastKnownCountryIso();
        for (String str : stringArray) {
            if (str.equalsIgnoreCase(this.mLastKnownNetworkCountry)) {
                return true;
            }
        }
        return false;
    }

    protected void setPowerStateToDesired() {
        setPowerStateToDesired(false, false, false);
    }

    protected void setPowerStateToDesired(boolean z, boolean z2, boolean z3) {
        String str = "setPowerStateToDesired: mDeviceShuttingDown=" + this.mDeviceShuttingDown + ", mDesiredPowerState=" + this.mDesiredPowerState + ", getRadioState=" + this.mCi.getRadioState() + ", mRadioPowerOffReasons=" + this.mRadioPowerOffReasons + ", IMS reg state=" + this.mImsRegistrationOnOff + ", pending radio off=" + hasMessages(62);
        log(str);
        this.mRadioPowerLog.log(str);
        boolean z4 = this.mDesiredPowerState;
        if (z4 && this.mDeviceShuttingDown) {
            log("setPowerStateToDesired powering on of radio failed because the device is powering off");
            return;
        }
        if (z4 && this.mRadioPowerOffReasons.isEmpty() && (z3 || this.mCi.getRadioState() == 0)) {
            this.mCi.setRadioPower(true, z, z2, null);
        } else if ((!this.mDesiredPowerState || !this.mRadioPowerOffReasons.isEmpty()) && this.mCi.getRadioState() == 1) {
            log("setPowerStateToDesired: powerOffRadioSafely()");
            powerOffRadioSafely();
        } else if (this.mDeviceShuttingDown && this.mCi.getRadioState() != 2) {
            this.mCi.requestShutdown(null);
        }
        cancelDelayRadioOffWaitingForImsDeregTimeout();
    }

    private void cancelDelayRadioOffWaitingForImsDeregTimeout() {
        if (hasMessages(62)) {
            log("cancelDelayRadioOffWaitingForImsDeregTimeout: cancelling.");
            removeMessages(62);
        }
    }

    protected void onUpdateIccAvailability() {
        UiccCardApplication uiccCardApplication;
        if (this.mUiccController == null || this.mUiccApplication == (uiccCardApplication = getUiccCardApplication())) {
            return;
        }
        IccRecords iccRecords = this.mIccRecords;
        if (iccRecords instanceof SIMRecords) {
            this.mCdnr.updateEfFromUsim(null);
        } else if (iccRecords instanceof RuimRecords) {
            this.mCdnr.updateEfFromRuim(null);
        }
        if (this.mUiccApplication != null) {
            log("Removing stale icc objects.");
            this.mUiccApplication.unregisterForReady(this);
            IccRecords iccRecords2 = this.mIccRecords;
            if (iccRecords2 != null) {
                iccRecords2.unregisterForRecordsLoaded(this);
            }
            this.mIccRecords = null;
            this.mUiccApplication = null;
        }
        if (uiccCardApplication != null) {
            log("New card found");
            this.mUiccApplication = uiccCardApplication;
            this.mIccRecords = uiccCardApplication.getIccRecords();
            if (this.mPhone.isPhoneTypeGsm()) {
                this.mUiccApplication.registerForReady(this, 17, null);
                IccRecords iccRecords3 = this.mIccRecords;
                if (iccRecords3 != null) {
                    iccRecords3.registerForRecordsLoaded(this, 16, null);
                }
            } else if (this.mIsSubscriptionFromRuim) {
                this.mUiccApplication.registerForReady(this, 26, null);
                IccRecords iccRecords4 = this.mIccRecords;
                if (iccRecords4 != null) {
                    iccRecords4.registerForRecordsLoaded(this, 27, null);
                }
            }
        }
    }

    private void logRoamingChange() {
        this.mRoamingLog.log(this.mSS.toString());
    }

    private void logAttachChange() {
        this.mAttachLog.log(this.mSS.toString());
    }

    private void logPhoneTypeChange() {
        this.mPhoneTypeLog.log(Integer.toString(this.mPhone.getPhoneType()));
    }

    private void logRatChange() {
        this.mRatLog.log(this.mSS.toString());
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected final void log(String str) {
        Rlog.d("SST", "[" + this.mPhone.getPhoneId() + "] " + str);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected final void loge(String str) {
        Rlog.e("SST", "[" + this.mPhone.getPhoneId() + "] " + str);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public int getCurrentDataConnectionState() {
        return this.mSS.getDataRegistrationState();
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public boolean isConcurrentVoiceAndDataAllowed() {
        if (this.mSS.getCssIndicator() == 1) {
            return true;
        }
        if (this.mPhone.isPhoneTypeGsm()) {
            int rilDataRadioTechnology = this.mSS.getRilDataRadioTechnology();
            if (rilDataRadioTechnology == 0 && this.mSS.getDataRegistrationState() != 0) {
                rilDataRadioTechnology = this.mSS.getRilVoiceRadioTechnology();
            }
            return (rilDataRadioTechnology == 0 || ServiceState.rilRadioTechnologyToAccessNetworkType(rilDataRadioTechnology) == 1) ? false : true;
        }
        return false;
    }

    public void onImsServiceStateChanged() {
        sendMessage(obtainMessage(53));
    }

    public void setImsRegistrationState(boolean z) {
        log("setImsRegistrationState: {registered=" + z + " mImsRegistrationOnOff=" + this.mImsRegistrationOnOff + "}");
        if (this.mImsRegistrationOnOff && !z) {
            if (getRadioPowerOffDelayTimeoutForImsRegistration() > 0) {
                sendMessage(obtainMessage(45));
            } else {
                log("setImsRegistrationState: EVENT_CHANGE_IMS_STATE not sent because power off delay for IMS deregistration is not enabled.");
            }
        }
        this.mImsRegistrationOnOff = z;
        updateSpnDisplay();
    }

    public void onImsCapabilityChanged() {
        sendMessage(obtainMessage(48));
    }

    public boolean isRadioOn() {
        return this.mCi.getRadioState() == 1;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void pollState() {
        sendEmptyMessage(58);
    }

    private void pollStateInternal(boolean z) {
        this.mPollingContext = new int[1];
        log("pollState: modemTriggered=" + z + ", radioState=" + this.mCi.getRadioState());
        int radioState = this.mCi.getRadioState();
        if (radioState == 0) {
            NetworkRegistrationInfo networkRegistrationInfo = this.mNewSS.getNetworkRegistrationInfo(2, 2);
            this.mNewSS.setOutOfService(true);
            if (networkRegistrationInfo != null) {
                this.mNewSS.addNetworkRegistrationInfo(networkRegistrationInfo);
            }
            this.mPhone.getSignalStrengthController().setSignalStrengthDefaultValues();
            this.mLastNitzData = null;
            this.mNitzState.handleNetworkUnavailable();
            if (this.mDeviceShuttingDown || (!z && 18 != this.mSS.getRilDataRadioTechnology())) {
                pollStateDone();
                return;
            }
        } else if (radioState == 2) {
            NetworkRegistrationInfo networkRegistrationInfo2 = this.mNewSS.getNetworkRegistrationInfo(2, 2);
            this.mNewSS.setOutOfService(false);
            if (networkRegistrationInfo2 != null) {
                this.mNewSS.addNetworkRegistrationInfo(networkRegistrationInfo2);
            }
            this.mPhone.getSignalStrengthController().setSignalStrengthDefaultValues();
            this.mLastNitzData = null;
            this.mNitzState.handleNetworkUnavailable();
            pollStateDone();
            return;
        }
        int[] iArr = this.mPollingContext;
        iArr[0] = iArr[0] + 1;
        this.mCi.getOperator(obtainMessage(7, iArr));
        int[] iArr2 = this.mPollingContext;
        iArr2[0] = iArr2[0] + 1;
        this.mRegStateManagers.get(1).requestNetworkRegistrationInfo(2, obtainMessage(5, this.mPollingContext));
        int[] iArr3 = this.mPollingContext;
        iArr3[0] = iArr3[0] + 1;
        this.mRegStateManagers.get(1).requestNetworkRegistrationInfo(1, obtainMessage(4, this.mPollingContext));
        if (this.mRegStateManagers.get(2) != null) {
            int[] iArr4 = this.mPollingContext;
            iArr4[0] = iArr4[0] + 1;
            this.mRegStateManagers.get(2).requestNetworkRegistrationInfo(2, obtainMessage(6, this.mPollingContext));
        }
        if (this.mPhone.isPhoneTypeGsm()) {
            int[] iArr5 = this.mPollingContext;
            iArr5[0] = iArr5[0] + 1;
            this.mCi.getNetworkSelectionMode(obtainMessage(14, iArr5));
        }
    }

    @VisibleForTesting
    public static List<CellIdentity> getPrioritizedCellIdentities(ServiceState serviceState) {
        List<NetworkRegistrationInfo> networkRegistrationInfoList = serviceState.getNetworkRegistrationInfoList();
        return networkRegistrationInfoList.isEmpty() ? Collections.emptyList() : (List) networkRegistrationInfoList.stream().filter(new Predicate() { // from class: com.android.internal.telephony.ServiceStateTracker$$ExternalSyntheticLambda3
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getPrioritizedCellIdentities$3;
                lambda$getPrioritizedCellIdentities$3 = ServiceStateTracker.lambda$getPrioritizedCellIdentities$3((NetworkRegistrationInfo) obj);
                return lambda$getPrioritizedCellIdentities$3;
            }
        }).filter(new Predicate() { // from class: com.android.internal.telephony.ServiceStateTracker$$ExternalSyntheticLambda4
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getPrioritizedCellIdentities$4;
                lambda$getPrioritizedCellIdentities$4 = ServiceStateTracker.lambda$getPrioritizedCellIdentities$4((NetworkRegistrationInfo) obj);
                return lambda$getPrioritizedCellIdentities$4;
            }
        }).sorted(Comparator.comparing(new Function() { // from class: com.android.internal.telephony.ServiceStateTracker$$ExternalSyntheticLambda5
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Boolean.valueOf(((NetworkRegistrationInfo) obj).isRegistered());
            }
        }).thenComparing(new Function() { // from class: com.android.internal.telephony.ServiceStateTracker$$ExternalSyntheticLambda6
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Integer lambda$getPrioritizedCellIdentities$5;
                lambda$getPrioritizedCellIdentities$5 = ServiceStateTracker.lambda$getPrioritizedCellIdentities$5((NetworkRegistrationInfo) obj);
                return lambda$getPrioritizedCellIdentities$5;
            }
        }).reversed()).map(new Function() { // from class: com.android.internal.telephony.ServiceStateTracker$$ExternalSyntheticLambda7
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                CellIdentity cellIdentity;
                cellIdentity = ((NetworkRegistrationInfo) obj).getCellIdentity();
                return cellIdentity;
            }
        }).distinct().collect(Collectors.toList());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getPrioritizedCellIdentities$3(NetworkRegistrationInfo networkRegistrationInfo) {
        return networkRegistrationInfo.getCellIdentity() != null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getPrioritizedCellIdentities$4(NetworkRegistrationInfo networkRegistrationInfo) {
        return networkRegistrationInfo.getTransportType() == 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ Integer lambda$getPrioritizedCellIdentities$5(NetworkRegistrationInfo networkRegistrationInfo) {
        return Integer.valueOf(networkRegistrationInfo.getDomain() & 1);
    }

    private void pollStateDone() {
        List<CellIdentity> list;
        CellIdentity cellIdentity;
        boolean z;
        boolean z2;
        boolean z3;
        boolean z4;
        boolean z5;
        boolean z6;
        boolean z7;
        boolean z8;
        boolean z9;
        boolean z10;
        boolean z11;
        boolean z12;
        boolean z13;
        boolean z14;
        boolean z15;
        SparseBooleanArray sparseBooleanArray;
        boolean z16;
        boolean z17;
        SparseBooleanArray sparseBooleanArray2;
        boolean z18;
        int i;
        int i2;
        boolean z19;
        boolean z20;
        boolean z21;
        boolean z22;
        if (!this.mPhone.isPhoneTypeGsm()) {
            updateRoamingState();
        }
        if (TelephonyUtils.IS_DEBUGGABLE && SystemProperties.getBoolean("telephony.test.forceRoaming", false)) {
            this.mNewSS.setRoaming(true);
        }
        useDataRegStateForDataOnlyDevices();
        processIwlanRegistrationInfo();
        updateNrFrequencyRangeFromPhysicalChannelConfigs(this.mLastPhysicalChannelConfigList, this.mNewSS);
        updateNrStateFromPhysicalChannelConfigs(this.mLastPhysicalChannelConfigList, this.mNewSS);
        if (TelephonyUtils.IS_DEBUGGABLE && this.mPhone.getTelephonyTester() != null) {
            this.mPhone.getTelephonyTester().overrideServiceState(this.mNewSS);
        }
        int i3 = 2;
        setPhyCellInfoFromCellIdentity(this.mNewSS, this.mNewSS.getNetworkRegistrationInfo(2, 1).getCellIdentity());
        log("Poll ServiceState done: oldSS=" + this.mSS);
        log("Poll ServiceState done: newSS=" + this.mNewSS);
        log("Poll ServiceState done: oldMaxDataCalls=" + this.mMaxDataCalls + " mNewMaxDataCalls=" + this.mNewMaxDataCalls + " oldReasonDataDenied=" + this.mReasonDataDenied + " mNewReasonDataDenied=" + this.mNewReasonDataDenied);
        boolean z23 = this.mSS.getState() != 0 && this.mNewSS.getState() == 0;
        boolean z24 = this.mSS.getState() == 0 && this.mNewSS.getState() != 0;
        boolean z25 = this.mSS.getState() != 3 && this.mNewSS.getState() == 3;
        boolean z26 = this.mSS.getState() == 3 && this.mNewSS.getState() != 3;
        SparseBooleanArray sparseBooleanArray3 = new SparseBooleanArray();
        SparseBooleanArray sparseBooleanArray4 = new SparseBooleanArray();
        SparseBooleanArray sparseBooleanArray5 = new SparseBooleanArray();
        SparseBooleanArray sparseBooleanArray6 = new SparseBooleanArray();
        boolean z27 = (TextUtils.equals(this.mSS.getOperatorAlphaLongRaw(), this.mNewSS.getOperatorAlphaLongRaw()) && TextUtils.equals(this.mSS.getOperatorAlphaShortRaw(), this.mNewSS.getOperatorAlphaShortRaw())) ? false : true;
        int[] availableTransports = this.mAccessNetworksManager.getAvailableTransports();
        int length = availableTransports.length;
        int i4 = 0;
        boolean z28 = false;
        boolean z29 = false;
        while (i4 < length) {
            int i5 = availableTransports[i4];
            NetworkRegistrationInfo networkRegistrationInfo = this.mSS.getNetworkRegistrationInfo(i3, i5);
            int[] iArr = availableTransports;
            NetworkRegistrationInfo networkRegistrationInfo2 = this.mNewSS.getNetworkRegistrationInfo(i3, i5);
            sparseBooleanArray3.put(i5, (networkRegistrationInfo == null || !networkRegistrationInfo.isInService() || z25) && networkRegistrationInfo2 != null && networkRegistrationInfo2.isInService());
            sparseBooleanArray4.put(i5, networkRegistrationInfo != null && networkRegistrationInfo.isInService() && (networkRegistrationInfo2 == null || !networkRegistrationInfo2.isInService()));
            int accessNetworkTechnology = networkRegistrationInfo != null ? networkRegistrationInfo.getAccessNetworkTechnology() : 0;
            if (networkRegistrationInfo2 != null) {
                i = length;
                i2 = networkRegistrationInfo2.getAccessNetworkTechnology();
            } else {
                i = length;
                i2 = 0;
            }
            if (networkRegistrationInfo != null) {
                z19 = z26;
                z20 = networkRegistrationInfo.isUsingCarrierAggregation();
            } else {
                z19 = z26;
                z20 = false;
            }
            if (networkRegistrationInfo2 != null) {
                z21 = z25;
                z22 = networkRegistrationInfo2.isUsingCarrierAggregation();
            } else {
                z21 = z25;
                z22 = false;
            }
            sparseBooleanArray5.put(i5, (accessNetworkTechnology == i2 && z20 == z22 && !z27) ? false : true);
            if (accessNetworkTechnology != i2) {
                z28 = true;
            }
            int networkRegistrationState = networkRegistrationInfo != null ? networkRegistrationInfo.getNetworkRegistrationState() : 4;
            int networkRegistrationState2 = networkRegistrationInfo2 != null ? networkRegistrationInfo2.getNetworkRegistrationState() : 4;
            sparseBooleanArray6.put(i5, networkRegistrationState != networkRegistrationState2);
            if (networkRegistrationState != networkRegistrationState2) {
                z29 = true;
            }
            i4++;
            availableTransports = iArr;
            length = i;
            z26 = z19;
            z25 = z21;
            i3 = 2;
        }
        boolean z30 = z25;
        boolean z31 = z26;
        boolean z32 = (z28 || this.mSS.getRilDataRadioTechnology() == this.mNewSS.getRilDataRadioTechnology()) ? false : true;
        boolean z33 = this.mSS.getState() != this.mNewSS.getState();
        boolean z34 = this.mSS.getNrFrequencyRange() != this.mNewSS.getNrFrequencyRange();
        boolean z35 = this.mSS.getNrState() != this.mNewSS.getNrState();
        List<CellIdentity> prioritizedCellIdentities = getPrioritizedCellIdentities(this.mNewSS);
        CellIdentity cellIdentity2 = prioritizedCellIdentities.isEmpty() ? null : prioritizedCellIdentities.get(0);
        CellIdentity cellIdentity3 = this.mCellIdentity;
        boolean z36 = cellIdentity3 != null ? !cellIdentity3.isSameCell(cellIdentity2) : cellIdentity2 != null;
        boolean z37 = false;
        for (NetworkRegistrationInfo networkRegistrationInfo3 : this.mNewSS.getNetworkRegistrationInfoListForTransportType(1)) {
            z37 |= networkRegistrationInfo3.isRegistered();
        }
        if (!z37 || z36) {
            list = prioritizedCellIdentities;
        } else {
            list = prioritizedCellIdentities;
            this.mRatRatcheter.ratchet(this.mSS, this.mNewSS);
        }
        boolean z38 = this.mSS.getRilVoiceRadioTechnology() != this.mNewSS.getRilVoiceRadioTechnology();
        boolean z39 = !this.mNewSS.equals(this.mSS);
        if (this.mSS.getVoiceRoaming() || !this.mNewSS.getVoiceRoaming()) {
            cellIdentity = cellIdentity2;
            z = false;
        } else {
            cellIdentity = cellIdentity2;
            z = true;
        }
        if (!this.mSS.getVoiceRoaming() || this.mNewSS.getVoiceRoaming()) {
            z2 = z33;
            z3 = false;
        } else {
            z2 = z33;
            z3 = true;
        }
        if (this.mSS.getDataRoaming() || !this.mNewSS.getDataRoaming()) {
            z4 = z35;
            z5 = false;
        } else {
            z4 = z35;
            z5 = true;
        }
        if (!this.mSS.getDataRoaming() || this.mNewSS.getDataRoaming()) {
            z6 = z34;
            z7 = false;
        } else {
            z6 = z34;
            z7 = true;
        }
        boolean z40 = z36;
        boolean z41 = this.mRejectCode != this.mNewRejectCode;
        boolean z42 = this.mSS.getCssIndicator() != this.mNewSS.getCssIndicator();
        if (this.mPhone.isPhoneTypeCdmaLte()) {
            int rilDataRadioTechnologyForWwan = getRilDataRadioTechnologyForWwan(this.mSS);
            z8 = z42;
            int rilDataRadioTechnologyForWwan2 = getRilDataRadioTechnologyForWwan(this.mNewSS);
            z9 = z7;
            boolean z43 = this.mNewSS.getDataRegistrationState() == 0 && ((ServiceState.isPsOnlyTech(rilDataRadioTechnologyForWwan) && rilDataRadioTechnologyForWwan2 == 13) || (rilDataRadioTechnologyForWwan == 13 && ServiceState.isPsOnlyTech(rilDataRadioTechnologyForWwan2)));
            z10 = ((!ServiceState.isPsOnlyTech(rilDataRadioTechnologyForWwan2) && rilDataRadioTechnologyForWwan2 != 13) || ServiceState.isPsOnlyTech(rilDataRadioTechnologyForWwan) || rilDataRadioTechnologyForWwan == 13) ? false : true;
            z11 = rilDataRadioTechnologyForWwan2 >= 4 && rilDataRadioTechnologyForWwan2 <= 8;
            z12 = z43;
        } else {
            z8 = z42;
            z9 = z7;
            z10 = false;
            z11 = false;
            z12 = false;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("pollStateDone: hasRegistered = ");
        sb.append(z23);
        sb.append(" hasDeregistered = ");
        sb.append(z24);
        sb.append(" hasDataAttached = ");
        sb.append(sparseBooleanArray3);
        sb.append(" hasDataDetached = ");
        sb.append(sparseBooleanArray4);
        sb.append(" hasDataRegStateChanged = ");
        sb.append(sparseBooleanArray6);
        sb.append(" hasRilVoiceRadioTechnologyChanged = ");
        sb.append(z38);
        sb.append(" hasRilDataRadioTechnologyChanged = ");
        sb.append(sparseBooleanArray5);
        sb.append(" hasDataTransportPreferenceChanged = ");
        sb.append(z32);
        sb.append(" hasChanged = ");
        sb.append(z39);
        sb.append(" hasVoiceRoamingOn = ");
        sb.append(z);
        sb.append(" hasVoiceRoamingOff = ");
        sb.append(z3);
        sb.append(" hasDataRoamingOn =");
        sb.append(z5);
        sb.append(" hasDataRoamingOff = ");
        sb.append(z9);
        sb.append(" hasLocationChanged = ");
        sb.append(z40);
        sb.append(" has4gHandoff = ");
        sb.append(z12);
        sb.append(" hasMultiApnSupport = ");
        sb.append(z10);
        sb.append(" hasLostMultiApnSupport = ");
        sb.append(z11);
        sb.append(" hasCssIndicatorChanged = ");
        boolean z44 = z8;
        sb.append(z44);
        sb.append(" hasNrFrequencyRangeChanged = ");
        sb.append(z6);
        sb.append(" hasNrStateChanged = ");
        sb.append(z4);
        sb.append(" hasAirplaneModeOnlChanged = ");
        sb.append(z30);
        log(sb.toString());
        if (z2 || z29) {
            z13 = z5;
            z14 = z3;
            z15 = z;
            sparseBooleanArray = sparseBooleanArray4;
            EventLog.writeEvent(this.mPhone.isPhoneTypeGsm() ? EventLogTags.GSM_SERVICE_STATE_CHANGE : EventLogTags.CDMA_SERVICE_STATE_CHANGE, Integer.valueOf(this.mSS.getState()), Integer.valueOf(this.mSS.getDataRegistrationState()), Integer.valueOf(this.mNewSS.getState()), Integer.valueOf(this.mNewSS.getDataRegistrationState()));
        } else {
            z13 = z5;
            sparseBooleanArray = sparseBooleanArray4;
            z14 = z3;
            z15 = z;
        }
        if (this.mPhone.isPhoneTypeGsm()) {
            if (z38) {
                long cidFromCellIdentity = getCidFromCellIdentity(cellIdentity);
                EventLog.writeEvent((int) EventLogTags.GSM_RAT_SWITCHED_NEW, Long.valueOf(cidFromCellIdentity), Integer.valueOf(this.mSS.getRilVoiceRadioTechnology()), Integer.valueOf(this.mNewSS.getRilVoiceRadioTechnology()));
                log("RAT switched " + ServiceState.rilRadioTechnologyToString(this.mSS.getRilVoiceRadioTechnology()) + " -> " + ServiceState.rilRadioTechnologyToString(this.mNewSS.getRilVoiceRadioTechnology()) + " at cell " + cidFromCellIdentity);
            }
            this.mReasonDataDenied = this.mNewReasonDataDenied;
            this.mMaxDataCalls = this.mNewMaxDataCalls;
            this.mRejectCode = this.mNewRejectCode;
        }
        if (!Objects.equals(this.mSS, this.mNewSS)) {
            this.mServiceStateChangedRegistrants.notifyRegistrants();
        }
        ServiceState serviceState = new ServiceState(this.mPhone.getServiceState());
        this.mSS = new ServiceState(this.mNewSS);
        this.mNewSS.setOutOfService(false);
        CellIdentity cellIdentity4 = cellIdentity;
        this.mCellIdentity = cellIdentity4;
        if (this.mSS.getState() == 0 && cellIdentity4 != null) {
            this.mLastKnownCellIdentity = this.mCellIdentity;
            removeMessages(63);
        }
        if (z24 && !hasMessages(63)) {
            sendEmptyMessageDelayed(63, TimeUnit.DAYS.toMillis(1L));
        }
        int areaCodeFromCellIdentity = getAreaCodeFromCellIdentity(this.mCellIdentity);
        if (areaCodeFromCellIdentity != this.mLastKnownAreaCode && areaCodeFromCellIdentity != Integer.MAX_VALUE) {
            this.mLastKnownAreaCode = areaCodeFromCellIdentity;
            this.mAreaCodeChangedRegistrants.notifyRegistrants();
        }
        if (z38) {
            updatePhoneObject();
        }
        TelephonyManager telephonyManager = (TelephonyManager) this.mPhone.getContext().getSystemService("phone");
        if (z28) {
            telephonyManager.setDataNetworkTypeForPhone(this.mPhone.getPhoneId(), this.mSS.getRilDataRadioTechnology());
            TelephonyStatsLog.write(76, ServiceState.rilRadioTechnologyToNetworkType(this.mSS.getRilDataRadioTechnology()), this.mPhone.getPhoneId());
        }
        if (z23) {
            this.mNetworkAttachedRegistrants.notifyRegistrants();
            this.mNitzState.handleNetworkAvailable();
        }
        if (z24) {
            this.mNetworkDetachedRegistrants.notifyRegistrants();
            this.mNitzState.handleNetworkUnavailable();
        }
        if (z44) {
            this.mCssIndicatorChangedRegistrants.notifyRegistrants();
        }
        if (z41) {
            setNotification(2001);
        }
        String cdmaEriText = this.mPhone.getCdmaEriText();
        boolean z45 = !TextUtils.equals(this.mEriText, cdmaEriText);
        this.mEriText = cdmaEriText;
        if (z39 || (!this.mPhone.isPhoneTypeGsm() && z45)) {
            updateSpnDisplay();
        }
        if (z39) {
            telephonyManager.setNetworkOperatorNameForPhone(this.mPhone.getPhoneId(), this.mSS.getOperatorAlpha());
            String operatorNumeric = this.mSS.getOperatorNumeric();
            if (!this.mPhone.isPhoneTypeGsm() && isInvalidOperatorNumeric(operatorNumeric)) {
                operatorNumeric = fixUnknownMcc(operatorNumeric, this.mSS.getCdmaSystemId());
            }
            telephonyManager.setNetworkOperatorNumericForPhone(this.mPhone.getPhoneId(), operatorNumeric);
            String str = this.mSS.getDataNetworkType() == 18 ? null : operatorNumeric;
            if (isInvalidOperatorNumeric(str)) {
                Iterator<CellIdentity> it = list.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    CellIdentity next = it.next();
                    if (!TextUtils.isEmpty(next.getPlmn())) {
                        str = next.getPlmn();
                        break;
                    }
                }
            }
            if (isInvalidOperatorNumeric(str)) {
                log("localeOperator " + str + " is invalid");
                this.mLocaleTracker.updateOperatorNumeric(PhoneConfigurationManager.SSSS);
            } else {
                if (!this.mPhone.isPhoneTypeGsm()) {
                    setOperatorIdd(str);
                }
                this.mLocaleTracker.updateOperatorNumeric(str);
            }
            int phoneId = this.mPhone.getPhoneId();
            if (this.mPhone.isPhoneTypeGsm()) {
                z18 = this.mSS.getVoiceRoaming();
            } else {
                z18 = this.mSS.getVoiceRoaming() || this.mSS.getDataRoaming();
            }
            telephonyManager.setNetworkRoamingForPhone(phoneId, z18);
            setRoamingType(this.mSS);
            log("Broadcasting ServiceState : " + this.mSS);
            if (!serviceState.equals(this.mPhone.getServiceState())) {
                GsmCdmaPhone gsmCdmaPhone = this.mPhone;
                gsmCdmaPhone.notifyServiceStateChanged(gsmCdmaPhone.getServiceState());
            }
            this.mPhone.getContext().getContentResolver().insert(Telephony.ServiceStateTable.getUriForSubscriptionId(this.mPhone.getSubId()), getContentValuesForServiceState(this.mSS));
            TelephonyMetrics.getInstance().writeServiceStateChanged(this.mPhone.getPhoneId(), this.mSS);
            this.mPhone.getVoiceCallSessionStats().onServiceStateChanged(this.mSS);
            ImsPhone imsPhone = (ImsPhone) this.mPhone.getImsPhone();
            if (imsPhone != null) {
                imsPhone.getImsStats().onServiceStateChanged(this.mSS);
            }
            this.mServiceStateStats.onServiceStateChanged(this.mSS);
        }
        boolean z46 = z23 || z24;
        if (z12) {
            this.mAttachedRegistrants.get(1).notifyRegistrants();
            z16 = true;
        } else {
            z16 = z46;
        }
        if (z38) {
            this.mPhone.getSignalStrengthController().notifySignalStrength();
            z17 = true;
        } else {
            z17 = false;
        }
        int[] availableTransports2 = this.mAccessNetworksManager.getAvailableTransports();
        int length2 = availableTransports2.length;
        boolean z47 = z17;
        int i6 = 0;
        while (i6 < length2) {
            int i7 = availableTransports2[i6];
            if (sparseBooleanArray5.get(i7)) {
                this.mPhone.getSignalStrengthController().notifySignalStrength();
                z47 = true;
            }
            if (sparseBooleanArray6.get(i7) || sparseBooleanArray5.get(i7) || z32) {
                setDataNetworkTypeForPhone(this.mSS.getRilDataRadioTechnology());
                notifyDataRegStateRilRadioTechnologyChanged(i7);
            }
            if (sparseBooleanArray3.get(i7)) {
                if (this.mAttachedRegistrants.get(i7) != null) {
                    this.mAttachedRegistrants.get(i7).notifyRegistrants();
                }
                sparseBooleanArray2 = sparseBooleanArray;
                z16 = true;
            } else {
                sparseBooleanArray2 = sparseBooleanArray;
            }
            if (sparseBooleanArray2.get(i7)) {
                if (this.mDetachedRegistrants.get(i7) != null) {
                    this.mDetachedRegistrants.get(i7).notifyRegistrants();
                }
                z16 = true;
            }
            i6++;
            sparseBooleanArray = sparseBooleanArray2;
        }
        if (z31) {
            this.mPhone.getSignalStrengthController().getSignalStrengthFromCi();
        }
        if (z16) {
            logAttachChange();
        }
        if (z47) {
            logRatChange();
        }
        if (z2 || z38) {
            notifyVoiceRegStateRilRadioTechnologyChanged();
        }
        if (z15 || z14 || z13 || z9) {
            logRoamingChange();
        }
        if (z15) {
            this.mVoiceRoamingOnRegistrants.notifyRegistrants();
        }
        if (z14) {
            this.mVoiceRoamingOffRegistrants.notifyRegistrants();
        }
        if (z13) {
            this.mDataRoamingOnRegistrants.notifyRegistrants();
        }
        if (z9) {
            this.mDataRoamingOffRegistrants.notifyRegistrants();
        }
        if (z40) {
            this.mPhone.notifyLocationChanged(getCellIdentity());
        }
        if (z4) {
            this.mNrStateChangedRegistrants.notifyRegistrants();
        }
        if (z6) {
            this.mNrFrequencyChangedRegistrants.notifyRegistrants();
        }
        if (this.mPhone.isPhoneTypeGsm()) {
            if (!isGprsConsistent(this.mSS.getDataRegistrationState(), this.mSS.getState())) {
                if (this.mStartedGprsRegCheck || this.mReportedGprsNoReg) {
                    return;
                }
                this.mStartedGprsRegCheck = true;
                sendMessageDelayed(obtainMessage(22), Settings.Global.getInt(this.mPhone.getContext().getContentResolver(), "gprs_register_check_period_ms", DEFAULT_GPRS_CHECK_PERIOD_MILLIS));
                return;
            }
            this.mReportedGprsNoReg = false;
        }
    }

    private String getOperatorNameFromEri() {
        EriManager eriManager;
        String str = null;
        if (this.mPhone.isPhoneTypeCdma()) {
            if (this.mCi.getRadioState() != 1 || this.mIsSubscriptionFromRuim) {
                return null;
            }
            if (this.mSS.getState() == 0) {
                return this.mPhone.getCdmaEriText();
            }
            return this.mPhone.getContext().getText(17041461).toString();
        } else if (this.mPhone.isPhoneTypeCdmaLte()) {
            if (!((this.mUiccController.getUiccPort(getPhoneId()) == null || this.mUiccController.getUiccPort(getPhoneId()).getOperatorBrandOverride() == null) ? false : true) && this.mCi.getRadioState() == 1 && (eriManager = this.mEriManager) != null && eriManager.isEriFileLoaded() && (!ServiceState.isPsOnlyTech(this.mSS.getRilVoiceRadioTechnology()) || this.mPhone.getContext().getResources().getBoolean(17891341))) {
                str = this.mSS.getOperatorAlpha();
                if (this.mSS.getState() == 0) {
                    str = this.mPhone.getCdmaEriText();
                } else if (this.mSS.getState() == 3) {
                    str = getServiceProviderName();
                    if (TextUtils.isEmpty(str)) {
                        str = SystemProperties.get("ro.cdma.home.operator.alpha");
                    }
                } else if (this.mSS.getDataRegistrationState() != 0) {
                    str = this.mPhone.getContext().getText(17041461).toString();
                }
            }
            UiccCardApplication uiccCardApplication = this.mUiccApplication;
            if (uiccCardApplication == null || uiccCardApplication.getState() != IccCardApplicationStatus.AppState.APPSTATE_READY || this.mIccRecords == null || getCombinedRegState(this.mSS) != 0 || ServiceState.isPsOnlyTech(this.mSS.getRilVoiceRadioTechnology())) {
                return str;
            }
            return (((RuimRecords) this.mIccRecords).getCsimSpnDisplayCondition() && this.mSS.getCdmaEriIconIndex() == 1 && isInHomeSidNid(this.mSS.getCdmaSystemId(), this.mSS.getCdmaNetworkId()) && this.mIccRecords != null) ? getServiceProviderName() : str;
        } else {
            return null;
        }
    }

    public String getServiceProviderName() {
        String operatorBrandOverride = getOperatorBrandOverride();
        if (TextUtils.isEmpty(operatorBrandOverride)) {
            IccRecords iccRecords = this.mIccRecords;
            String serviceProviderName = iccRecords != null ? iccRecords.getServiceProviderName() : PhoneConfigurationManager.SSSS;
            return (this.mCarrierConfig.getBoolean("carrier_name_override_bool") || TextUtils.isEmpty(serviceProviderName)) ? this.mCarrierConfig.getString("carrier_name_string") : serviceProviderName;
        }
        return operatorBrandOverride;
    }

    public int getCarrierNameDisplayBitmask(ServiceState serviceState) {
        boolean z;
        if (TextUtils.isEmpty(getOperatorBrandOverride())) {
            if (TextUtils.isEmpty(getServiceProviderName())) {
                return 2;
            }
            boolean z2 = this.mCarrierConfig.getBoolean("spn_display_rule_use_roaming_from_service_state_bool");
            IccRecords iccRecords = this.mIccRecords;
            int carrierNameDisplayCondition = iccRecords == null ? 0 : iccRecords.getCarrierNameDisplayCondition();
            if (z2) {
                z = serviceState.getRoaming();
            } else {
                IccRecords iccRecords2 = this.mIccRecords;
                z = !ArrayUtils.contains(iccRecords2 != null ? iccRecords2.getHomePlmns() : null, serviceState.getOperatorNumeric());
            }
            if (z) {
                if ((carrierNameDisplayCondition & 2) != 2) {
                    return 2;
                }
            } else if ((carrierNameDisplayCondition & 1) != 1) {
                return 1;
            }
            return 3;
        }
        return 1;
    }

    private String getOperatorBrandOverride() {
        UiccProfile uiccProfile;
        UiccPort uiccPort = this.mPhone.getUiccPort();
        if (uiccPort == null || (uiccProfile = uiccPort.getUiccProfile()) == null) {
            return null;
        }
        return uiccProfile.getOperatorBrandOverride();
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private boolean isInHomeSidNid(int i, int i2) {
        int i3;
        if (isSidsAllZeros() || this.mHomeSystemId.length != this.mHomeNetworkId.length || i == 0) {
            return true;
        }
        int i4 = 0;
        while (true) {
            int[] iArr = this.mHomeSystemId;
            if (i4 >= iArr.length) {
                return false;
            }
            if (iArr[i4] != i || ((i3 = this.mHomeNetworkId[i4]) != 0 && i3 != 65535 && i2 != 0 && i2 != 65535 && i3 != i2)) {
                i4++;
            }
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected void setOperatorIdd(String str) {
        if (this.mPhone.getUnitTestMode()) {
            return;
        }
        String iddByMcc = this.mHbpcdUtils.getIddByMcc(Integer.parseInt(str.substring(0, 3)));
        if (iddByMcc != null && !iddByMcc.isEmpty()) {
            TelephonyProperties.operator_idp_string(iddByMcc);
        } else {
            TelephonyProperties.operator_idp_string("+");
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private boolean isInvalidOperatorNumeric(String str) {
        return str == null || str.length() < 5 || str.startsWith(INVALID_MCC);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private String fixUnknownMcc(String str, int i) {
        int i2;
        boolean z;
        if (i <= 0) {
            return str;
        }
        NitzData nitzData = this.mLastNitzData;
        int i3 = 0;
        if (nitzData != null) {
            int localOffsetMillis = nitzData.getLocalOffsetMillis() / 3600000;
            Integer dstAdjustmentMillis = nitzData.getDstAdjustmentMillis();
            z = true;
            if (dstAdjustmentMillis != null && dstAdjustmentMillis.intValue() != 0) {
                i3 = 1;
            }
            i2 = i3;
            i3 = localOffsetMillis;
        } else {
            i2 = 0;
            z = false;
        }
        int mcc = this.mHbpcdUtils.getMcc(i, i3, i2, z);
        if (mcc > 0) {
            return mcc + DEFAULT_MNC;
        }
        return str;
    }

    private boolean isSameOperatorNameFromSimAndSS(ServiceState serviceState) {
        String simOperatorNameForPhone = ((TelephonyManager) this.mPhone.getContext().getSystemService("phone")).getSimOperatorNameForPhone(getPhoneId());
        return (!TextUtils.isEmpty(simOperatorNameForPhone) && simOperatorNameForPhone.equalsIgnoreCase(serviceState.getOperatorAlphaLong())) || (!TextUtils.isEmpty(simOperatorNameForPhone) && simOperatorNameForPhone.equalsIgnoreCase(serviceState.getOperatorAlphaShort()));
    }

    private boolean isSameNamedOperators(ServiceState serviceState) {
        return currentMccEqualsSimMcc(serviceState) && isSameOperatorNameFromSimAndSS(serviceState);
    }

    private boolean currentMccEqualsSimMcc(ServiceState serviceState) {
        try {
            return ((TelephonyManager) this.mPhone.getContext().getSystemService("phone")).getSimOperatorNumericForPhone(getPhoneId()).substring(0, 3).equals(serviceState.getOperatorNumeric().substring(0, 3));
        } catch (Exception unused) {
            return true;
        }
    }

    private boolean isOperatorConsideredNonRoaming(ServiceState serviceState) {
        String operatorNumeric = serviceState.getOperatorNumeric();
        String[] stringArray = this.mCarrierConfig.getStringArray("non_roaming_operator_string_array");
        if (!ArrayUtils.isEmpty(stringArray) && operatorNumeric != null) {
            for (String str : stringArray) {
                if (!TextUtils.isEmpty(str) && operatorNumeric.startsWith(str)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isOperatorConsideredRoaming(ServiceState serviceState) {
        String operatorNumeric = serviceState.getOperatorNumeric();
        String[] stringArray = this.mCarrierConfig.getStringArray("roaming_operator_string_array");
        if (!ArrayUtils.isEmpty(stringArray) && operatorNumeric != null) {
            for (String str : stringArray) {
                if (!TextUtils.isEmpty(str) && operatorNumeric.startsWith(str)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void onRestrictedStateChanged(AsyncResult asyncResult) {
        Object obj;
        RestrictedState restrictedState = new RestrictedState();
        log("onRestrictedStateChanged: E rs " + this.mRestrictedState);
        if (asyncResult.exception == null && (obj = asyncResult.result) != null) {
            int intValue = ((Integer) obj).intValue();
            restrictedState.setCsEmergencyRestricted(((intValue & 1) == 0 && (intValue & 4) == 0) ? false : true);
            UiccCardApplication uiccCardApplication = this.mUiccApplication;
            if (uiccCardApplication != null && uiccCardApplication.getState() == IccCardApplicationStatus.AppState.APPSTATE_READY) {
                restrictedState.setCsNormalRestricted(((intValue & 2) == 0 && (intValue & 4) == 0) ? false : true);
                restrictedState.setPsRestricted((intValue & 16) != 0);
            }
            log("onRestrictedStateChanged: new rs " + restrictedState);
            if (!this.mRestrictedState.isPsRestricted() && restrictedState.isPsRestricted()) {
                this.mPsRestrictEnabledRegistrants.notifyRegistrants();
                setNotification(1001);
            } else if (this.mRestrictedState.isPsRestricted() && !restrictedState.isPsRestricted()) {
                this.mPsRestrictDisabledRegistrants.notifyRegistrants();
                setNotification(1002);
            }
            if (this.mRestrictedState.isCsRestricted()) {
                if (!restrictedState.isAnyCsRestricted()) {
                    setNotification(1004);
                } else if (!restrictedState.isCsNormalRestricted()) {
                    setNotification(1006);
                } else if (!restrictedState.isCsEmergencyRestricted()) {
                    setNotification(1005);
                }
            } else if (this.mRestrictedState.isCsEmergencyRestricted() && !this.mRestrictedState.isCsNormalRestricted()) {
                if (!restrictedState.isAnyCsRestricted()) {
                    setNotification(1004);
                } else if (restrictedState.isCsRestricted()) {
                    setNotification(1003);
                } else if (restrictedState.isCsNormalRestricted()) {
                    setNotification(1005);
                }
            } else if (!this.mRestrictedState.isCsEmergencyRestricted() && this.mRestrictedState.isCsNormalRestricted()) {
                if (!restrictedState.isAnyCsRestricted()) {
                    setNotification(1004);
                } else if (restrictedState.isCsRestricted()) {
                    setNotification(1003);
                } else if (restrictedState.isCsEmergencyRestricted()) {
                    setNotification(1006);
                }
            } else if (restrictedState.isCsRestricted()) {
                setNotification(1003);
            } else if (restrictedState.isCsEmergencyRestricted()) {
                setNotification(1006);
            } else if (restrictedState.isCsNormalRestricted()) {
                setNotification(1005);
            }
            this.mRestrictedState = restrictedState;
        }
        log("onRestrictedStateChanged: X rs " + this.mRestrictedState);
    }

    public CellIdentity getCellIdentity() {
        CellIdentity cellIdentity = this.mCellIdentity;
        if (cellIdentity != null) {
            return cellIdentity;
        }
        CellIdentity cellIdentityFromCellInfo = getCellIdentityFromCellInfo(getAllCellInfo());
        return cellIdentityFromCellInfo != null ? cellIdentityFromCellInfo : this.mPhone.getPhoneType() == 2 ? new CellIdentityCdma() : new CellIdentityGsm();
    }

    public void requestCellIdentity(WorkSource workSource, Message message) {
        CellIdentity cellIdentity = this.mCellIdentity;
        if (cellIdentity != null) {
            AsyncResult.forMessage(message, cellIdentity, (Throwable) null);
            message.sendToTarget();
            return;
        }
        requestAllCellInfo(workSource, obtainMessage(56, message));
    }

    private static CellIdentity getCellIdentityFromCellInfo(List<CellInfo> list) {
        CellIdentity cellIdentity = null;
        if (list == null || list.size() <= 0) {
            return null;
        }
        Iterator<CellInfo> it = list.iterator();
        CellIdentity cellIdentity2 = null;
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            CellIdentity cellIdentity3 = it.next().getCellIdentity();
            if ((cellIdentity3 instanceof CellIdentityLte) && cellIdentity2 == null) {
                if (getCidFromCellIdentity(cellIdentity3) != -1) {
                    cellIdentity2 = cellIdentity3;
                }
            } else if (getCidFromCellIdentity(cellIdentity3) != -1) {
                cellIdentity = cellIdentity3;
                break;
            }
        }
        return (cellIdentity != null || cellIdentity2 == null) ? cellIdentity : cellIdentity2;
    }

    private void setTimeFromNITZString(String str, long j, long j2) {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        Rlog.d("SST", "NITZ: " + str + "," + j + ", ageMs=" + j2 + " start=" + elapsedRealtime + " delay=" + (elapsedRealtime - j));
        NitzData parse = NitzData.parse(str);
        this.mLastNitzData = parse;
        if (parse != null) {
            try {
                this.mNitzState.handleNitzReceived(new NitzSignal(j, parse, j2));
            } finally {
                long elapsedRealtime2 = SystemClock.elapsedRealtime();
                Rlog.d("SST", "NITZ: end=" + elapsedRealtime2 + " dur=" + (elapsedRealtime2 - elapsedRealtime));
            }
        }
    }

    private void cancelAllNotifications() {
        log("cancelAllNotifications: mPrevSubId=" + this.mPrevSubId);
        NotificationManager notificationManager = (NotificationManager) this.mPhone.getContext().getSystemService("notification");
        if (SubscriptionManager.isValidSubscriptionId(this.mPrevSubId)) {
            notificationManager.cancel(Integer.toString(this.mPrevSubId), PS_NOTIFICATION);
            notificationManager.cancel(Integer.toString(this.mPrevSubId), CS_NOTIFICATION);
            notificationManager.cancel(Integer.toString(this.mPrevSubId), 111);
            notificationManager.cancel(CarrierServiceStateTracker.EMERGENCY_NOTIFICATION_TAG, this.mPrevSubId);
            notificationManager.cancel(CarrierServiceStateTracker.PREF_NETWORK_NOTIFICATION_TAG, this.mPrevSubId);
        }
    }

    @VisibleForTesting
    public void setNotification(int i) {
        CharSequence text;
        CharSequence text2;
        int i2 = i;
        log("setNotification: create notification " + i2);
        if (!SubscriptionManager.isValidSubscriptionId(this.mSubId)) {
            loge("cannot setNotification on invalid subid mSubId=" + this.mSubId);
            return;
        }
        Context context = this.mPhone.getContext();
        if (this.mPhone.isSubscriptionManagerServiceEnabled()) {
            SubscriptionInfoInternal subscriptionInfoInternal = this.mSubscriptionManagerService.getSubscriptionInfoInternal(this.mPhone.getSubId());
            if (subscriptionInfoInternal == null || !subscriptionInfoInternal.isVisible()) {
                log("cannot setNotification on invisible subid mSubId=" + this.mSubId);
                return;
            }
        } else {
            SubscriptionInfo activeSubscriptionInfo = this.mSubscriptionController.getActiveSubscriptionInfo(this.mPhone.getSubId(), context.getOpPackageName(), context.getAttributionTag());
            if (activeSubscriptionInfo == null || (activeSubscriptionInfo.isOpportunistic() && activeSubscriptionInfo.getGroupUuid() != null)) {
                log("cannot setNotification on invisible subid mSubId=" + this.mSubId);
                return;
            }
        }
        if (!context.getResources().getBoolean(17891873)) {
            log("Ignore all the notifications");
        } else if (this.mCarrierConfig.getBoolean("disable_voice_barring_notification_bool", false) && (i2 == 1003 || i2 == 1005 || i2 == 1006)) {
            log("Voice/emergency call barred notification disabled");
        } else {
            boolean z = this.mCarrierConfig.getBoolean("carrier_auto_cancel_cs_notification", false);
            boolean z2 = ((TelephonyManager) this.mPhone.getContext().getSystemService("phone")).getPhoneCount() > 1;
            int slotIndex = SubscriptionManager.getSlotIndex(this.mSubId) + 1;
            int i3 = CS_NOTIFICATION;
            int i4 = 17301642;
            if (i2 == 1005) {
                text = context.getText(17039563);
                if (z2) {
                    text2 = context.getString(17039565, Integer.valueOf(slotIndex));
                } else {
                    text2 = context.getText(17039564);
                }
            } else if (i2 != 1006) {
                text = PhoneConfigurationManager.SSSS;
                if (i2 != 2001) {
                    switch (i2) {
                        case 1001:
                            if (SubscriptionManager.getDefaultDataSubscriptionId() == this.mPhone.getSubId()) {
                                text = context.getText(17039561);
                                if (z2) {
                                    text2 = context.getString(17039565, Integer.valueOf(slotIndex));
                                } else {
                                    text2 = context.getText(17039564);
                                }
                                i3 = PS_NOTIFICATION;
                                break;
                            } else {
                                return;
                            }
                        case 1002:
                            text2 = PhoneConfigurationManager.SSSS;
                            i3 = PS_NOTIFICATION;
                            break;
                        case 1003:
                            text = context.getText(17039560);
                            if (z2) {
                                text2 = context.getString(17039565, Integer.valueOf(slotIndex));
                                break;
                            } else {
                                text2 = context.getText(17039564);
                                break;
                            }
                        default:
                            text2 = PhoneConfigurationManager.SSSS;
                            break;
                    }
                } else {
                    int selectResourceForRejectCode = selectResourceForRejectCode(this.mRejectCode, z2);
                    i3 = 111;
                    if (selectResourceForRejectCode != 0) {
                        text = context.getString(selectResourceForRejectCode, Integer.valueOf(slotIndex));
                        i4 = 17303590;
                        text2 = null;
                    } else if (!z) {
                        loge("setNotification: mRejectCode=" + this.mRejectCode + " is not handled.");
                        return;
                    } else {
                        text2 = PhoneConfigurationManager.SSSS;
                        i2 = 2002;
                    }
                }
            } else {
                text = context.getText(17039562);
                if (z2) {
                    text2 = context.getString(17039565, Integer.valueOf(slotIndex));
                } else {
                    text2 = context.getText(17039564);
                }
            }
            log("setNotification, create notification, notifyType: " + i2 + ", title: " + ((Object) text) + ", details: " + ((Object) text2) + ", subId: " + this.mSubId);
            this.mNotification = new Notification.Builder(context).setWhen(System.currentTimeMillis()).setAutoCancel(true).setSmallIcon(i4).setTicker(text).setColor(context.getResources().getColor(17170460)).setContentTitle(text).setStyle(new Notification.BigTextStyle().bigText(text2)).setContentText(text2).setChannelId(NotificationChannelController.CHANNEL_ID_ALERT).build();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
            if (i2 == 1002 || i2 == 1004 || i2 == 2002) {
                notificationManager.cancel(Integer.toString(this.mSubId), i3);
                return;
            }
            if ((this.mSS.isEmergencyOnly() && i2 == 1006) || i2 == 2001 || this.mSS.getState() == 0) {
                notificationManager.notify(Integer.toString(this.mSubId), i3, this.mNotification);
            }
        }
    }

    private UiccCardApplication getUiccCardApplication() {
        if (this.mPhone.isPhoneTypeGsm()) {
            return this.mUiccController.getUiccCardApplication(this.mPhone.getPhoneId(), 1);
        }
        return this.mUiccController.getUiccCardApplication(this.mPhone.getPhoneId(), 2);
    }

    private void notifyCdmaSubscriptionInfoReady() {
        if (this.mCdmaForSubscriptionInfoReadyRegistrants != null) {
            log("CDMA_SUBSCRIPTION: call notifyRegistrants()");
            this.mCdmaForSubscriptionInfoReadyRegistrants.notifyRegistrants();
        }
    }

    public void registerForDataConnectionAttached(int i, Handler handler, int i2, Object obj) {
        Registrant registrant = new Registrant(handler, i2, obj);
        if (this.mAttachedRegistrants.get(i) == null) {
            this.mAttachedRegistrants.put(i, new RegistrantList());
        }
        this.mAttachedRegistrants.get(i).add(registrant);
        ServiceState serviceState = this.mSS;
        if (serviceState != null) {
            NetworkRegistrationInfo networkRegistrationInfo = serviceState.getNetworkRegistrationInfo(2, i);
            if (networkRegistrationInfo == null || networkRegistrationInfo.isInService()) {
                registrant.notifyRegistrant();
            }
        }
    }

    public void unregisterForDataConnectionAttached(int i, Handler handler) {
        if (this.mAttachedRegistrants.get(i) != null) {
            this.mAttachedRegistrants.get(i).remove(handler);
        }
    }

    public void registerForDataConnectionDetached(int i, Handler handler, int i2, Object obj) {
        NetworkRegistrationInfo networkRegistrationInfo;
        Registrant registrant = new Registrant(handler, i2, obj);
        if (this.mDetachedRegistrants.get(i) == null) {
            this.mDetachedRegistrants.put(i, new RegistrantList());
        }
        this.mDetachedRegistrants.get(i).add(registrant);
        ServiceState serviceState = this.mSS;
        if (serviceState == null || (networkRegistrationInfo = serviceState.getNetworkRegistrationInfo(2, i)) == null || networkRegistrationInfo.isInService()) {
            return;
        }
        registrant.notifyRegistrant();
    }

    public void unregisterForDataConnectionDetached(int i, Handler handler) {
        if (this.mDetachedRegistrants.get(i) != null) {
            this.mDetachedRegistrants.get(i).remove(handler);
        }
    }

    public void registerForVoiceRegStateOrRatChanged(Handler handler, int i, Object obj) {
        this.mVoiceRegStateOrRatChangedRegistrants.add(new Registrant(handler, i, obj));
        notifyVoiceRegStateRilRadioTechnologyChanged();
    }

    public void unregisterForVoiceRegStateOrRatChanged(Handler handler) {
        this.mVoiceRegStateOrRatChangedRegistrants.remove(handler);
    }

    public void registerForDataRegStateOrRatChanged(int i, Handler handler, int i2, Object obj) {
        Registrant registrant = new Registrant(handler, i2, obj);
        if (this.mDataRegStateOrRatChangedRegistrants.get(i) == null) {
            this.mDataRegStateOrRatChangedRegistrants.put(i, new RegistrantList());
        }
        this.mDataRegStateOrRatChangedRegistrants.get(i).add(registrant);
        Pair<Integer, Integer> registrationInfo = getRegistrationInfo(i);
        if (registrationInfo != null) {
            registrant.notifyResult(registrationInfo);
        }
    }

    public void unregisterForDataRegStateOrRatChanged(int i, Handler handler) {
        if (this.mDataRegStateOrRatChangedRegistrants.get(i) != null) {
            this.mDataRegStateOrRatChangedRegistrants.get(i).remove(handler);
        }
    }

    public void registerForAirplaneModeChanged(Handler handler, int i, Object obj) {
        this.mAirplaneModeChangedRegistrants.add(handler, i, obj);
    }

    public void unregisterForAirplaneModeChanged(Handler handler) {
        this.mAirplaneModeChangedRegistrants.remove(handler);
    }

    public void registerForNetworkAttached(Handler handler, int i, Object obj) {
        Registrant registrant = new Registrant(handler, i, obj);
        this.mNetworkAttachedRegistrants.add(registrant);
        if (this.mSS.getState() == 0) {
            registrant.notifyRegistrant();
        }
    }

    public void unregisterForNetworkAttached(Handler handler) {
        this.mNetworkAttachedRegistrants.remove(handler);
    }

    public void registerForNetworkDetached(Handler handler, int i, Object obj) {
        Registrant registrant = new Registrant(handler, i, obj);
        this.mNetworkDetachedRegistrants.add(registrant);
        if (this.mSS.getState() != 0) {
            registrant.notifyRegistrant();
        }
    }

    public void unregisterForNetworkDetached(Handler handler) {
        this.mNetworkDetachedRegistrants.remove(handler);
    }

    public void registerForPsRestrictedEnabled(Handler handler, int i, Object obj) {
        Registrant registrant = new Registrant(handler, i, obj);
        this.mPsRestrictEnabledRegistrants.add(registrant);
        if (this.mRestrictedState.isPsRestricted()) {
            registrant.notifyRegistrant();
        }
    }

    public void unregisterForPsRestrictedEnabled(Handler handler) {
        this.mPsRestrictEnabledRegistrants.remove(handler);
    }

    public void registerForPsRestrictedDisabled(Handler handler, int i, Object obj) {
        Registrant registrant = new Registrant(handler, i, obj);
        this.mPsRestrictDisabledRegistrants.add(registrant);
        if (this.mRestrictedState.isPsRestricted()) {
            registrant.notifyRegistrant();
        }
    }

    public void unregisterForPsRestrictedDisabled(Handler handler) {
        this.mPsRestrictDisabledRegistrants.remove(handler);
    }

    public void registerForImsCapabilityChanged(Handler handler, int i, Object obj) {
        this.mImsCapabilityChangedRegistrants.add(new Registrant(handler, i, obj));
    }

    public void unregisterForImsCapabilityChanged(Handler handler) {
        this.mImsCapabilityChangedRegistrants.remove(handler);
    }

    public void registerForServiceStateChanged(Handler handler, int i, Object obj) {
        this.mServiceStateChangedRegistrants.addUnique(handler, i, obj);
    }

    public void unregisterForServiceStateChanged(Handler handler) {
        this.mServiceStateChangedRegistrants.remove(handler);
    }

    public void powerOffRadioSafely() {
        Phone[] phones;
        synchronized (this) {
            if (!this.mPendingRadioPowerOffAfterDataOff) {
                if (this.mPhone.isPhoneTypeGsm() && this.mPhone.isInCall()) {
                    this.mPhone.mCT.mRingingCall.hangupIfAlive();
                    this.mPhone.mCT.mBackgroundCall.hangupIfAlive();
                    this.mPhone.mCT.mForegroundCall.hangupIfAlive();
                }
                for (Phone phone : PhoneFactory.getPhones()) {
                    if (!phone.getDataNetworkController().areAllDataDisconnected()) {
                        log("powerOffRadioSafely: Data is active on phone " + phone.getSubId() + ". Wait for all data disconnect.");
                        this.mPendingRadioPowerOffAfterDataOff = true;
                        phone.getDataNetworkController().registerDataNetworkControllerCallback(this.mDataDisconnectedCallback);
                    }
                }
                this.mPhone.getDataNetworkController().tearDownAllDataNetworks(3);
                if (this.mPendingRadioPowerOffAfterDataOff) {
                    sendEmptyMessageDelayed(38, POWER_OFF_ALL_DATA_NETWORKS_DISCONNECTED_TIMEOUT);
                } else {
                    log("powerOffRadioSafely: No data is connected, turn off radio now.");
                    hangupAndPowerOff();
                }
            }
        }
    }

    public boolean processPendingRadioPowerOffAfterDataOff() {
        synchronized (this) {
            if (this.mPendingRadioPowerOffAfterDataOff) {
                log("Process pending request to turn radio off.");
                hangupAndPowerOff();
                this.mPendingRadioPowerOffAfterDataOff = false;
                return true;
            }
            return false;
        }
    }

    private void onCarrierConfigurationChanged(int i) {
        if (i != this.mPhone.getPhoneId()) {
            return;
        }
        this.mCarrierConfig = getCarrierConfig();
        log("CarrierConfigChange " + this.mCarrierConfig);
        EriManager eriManager = this.mEriManager;
        if (eriManager != null) {
            eriManager.loadEriFile();
            this.mCdnr.updateEfForEri(getOperatorNameFromEri());
        }
        updateOperatorNamePattern(this.mCarrierConfig);
        this.mCdnr.updateEfFromCarrierConfig(this.mCarrierConfig);
        this.mPhone.notifyCallForwardingIndicator();
        pollStateInternal(false);
    }

    protected void hangupAndPowerOff() {
        if (this.mCi.getRadioState() == 0) {
            return;
        }
        if (!this.mPhone.isPhoneTypeGsm() || this.mPhone.isInCall()) {
            this.mPhone.mCT.mRingingCall.hangupIfAlive();
            this.mPhone.mCT.mBackgroundCall.hangupIfAlive();
            this.mPhone.mCT.mForegroundCall.hangupIfAlive();
        }
        this.mCi.setRadioPower(false, obtainMessage(54));
    }

    protected void cancelPollState() {
        this.mPollingContext = new int[1];
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public String getSystemProperty(String str, String str2) {
        return TelephonyManager.getTelephonyProperty(this.mPhone.getPhoneId(), str, str2);
    }

    public List<CellInfo> getAllCellInfo() {
        return this.mLastCellInfoList;
    }

    public void setCellInfoMinInterval(int i) {
        this.mCellInfoMinIntervalMs = i;
    }

    public void requestAllCellInfo(WorkSource workSource, Message message) {
        if (this.mCi.getRilVersion() < 8) {
            AsyncResult.forMessage(message);
            message.sendToTarget();
            log("SST.requestAllCellInfo(): not implemented");
            return;
        }
        synchronized (this.mPendingCellInfoRequests) {
            if (this.mIsPendingCellInfoRequest) {
                if (message != null) {
                    this.mPendingCellInfoRequests.add(message);
                }
                return;
            }
            long elapsedRealtime = SystemClock.elapsedRealtime();
            if (elapsedRealtime - this.mLastCellInfoReqTime < this.mCellInfoMinIntervalMs) {
                if (message != null) {
                    log("SST.requestAllCellInfo(): return last, back to back calls");
                    AsyncResult.forMessage(message, this.mLastCellInfoList, (Throwable) null);
                    message.sendToTarget();
                }
                return;
            }
            if (message != null) {
                this.mPendingCellInfoRequests.add(message);
            }
            this.mLastCellInfoReqTime = elapsedRealtime;
            this.mIsPendingCellInfoRequest = true;
            this.mCi.getCellInfoList(obtainMessage(43), workSource);
            sendMessageDelayed(obtainMessage(43), 2000L);
        }
    }

    public void registerForSubscriptionInfoReady(Handler handler, int i, Object obj) {
        Registrant registrant = new Registrant(handler, i, obj);
        this.mCdmaForSubscriptionInfoReadyRegistrants.add(registrant);
        if (isMinInfoReady()) {
            registrant.notifyRegistrant();
        }
    }

    public void unregisterForSubscriptionInfoReady(Handler handler) {
        this.mCdmaForSubscriptionInfoReadyRegistrants.remove(handler);
    }

    private void saveCdmaSubscriptionSource(int i) {
        log("Storing cdma subscription source: " + i);
        Settings.Global.putInt(this.mPhone.getContext().getContentResolver(), "subscription_mode", i);
        log("Read from settings: " + Settings.Global.getInt(this.mPhone.getContext().getContentResolver(), "subscription_mode", -1));
    }

    private void getSubscriptionInfoAndStartPollingThreads() {
        this.mCi.getCDMASubscription(obtainMessage(34));
        pollStateInternal(false);
    }

    private void handleCdmaSubscriptionSource(int i) {
        log("Subscription Source : " + i);
        this.mIsSubscriptionFromRuim = i == 0;
        log("isFromRuim: " + this.mIsSubscriptionFromRuim);
        saveCdmaSubscriptionSource(i);
        if (this.mIsSubscriptionFromRuim) {
            return;
        }
        sendMessage(obtainMessage(35));
    }

    public void onTelecomVoiceServiceStateOverrideChanged() {
        sendMessage(obtainMessage(65));
    }

    private void dumpCellInfoList(PrintWriter printWriter) {
        printWriter.print(" mLastCellInfoList={");
        List<CellInfo> list = this.mLastCellInfoList;
        if (list != null) {
            boolean z = true;
            for (CellInfo cellInfo : list) {
                if (!z) {
                    printWriter.print(",");
                }
                printWriter.print(cellInfo.toString());
                z = false;
            }
        }
        printWriter.println("}");
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("ServiceStateTracker:");
        printWriter.println(" mSubId=" + this.mSubId);
        printWriter.println(" mSS=" + this.mSS);
        printWriter.println(" mNewSS=" + this.mNewSS);
        printWriter.println(" mVoiceCapable=" + this.mVoiceCapable);
        printWriter.println(" mRestrictedState=" + this.mRestrictedState);
        printWriter.println(" mPollingContext=" + Arrays.toString(this.mPollingContext));
        printWriter.println(" mDesiredPowerState=" + this.mDesiredPowerState);
        printWriter.println(" mRestrictedState=" + this.mRestrictedState);
        printWriter.println(" mPendingRadioPowerOffAfterDataOff=" + this.mPendingRadioPowerOffAfterDataOff);
        printWriter.println(" mCellIdentity=" + Rlog.pii(false, this.mCellIdentity));
        printWriter.println(" mLastCellInfoReqTime=" + this.mLastCellInfoReqTime);
        dumpCellInfoList(printWriter);
        printWriter.flush();
        printWriter.println(" mAllowedNetworkTypes=" + this.mAllowedNetworkTypes);
        printWriter.println(" mMaxDataCalls=" + this.mMaxDataCalls);
        printWriter.println(" mNewMaxDataCalls=" + this.mNewMaxDataCalls);
        printWriter.println(" mReasonDataDenied=" + this.mReasonDataDenied);
        printWriter.println(" mNewReasonDataDenied=" + this.mNewReasonDataDenied);
        printWriter.println(" mGsmVoiceRoaming=" + this.mGsmVoiceRoaming);
        printWriter.println(" mGsmDataRoaming=" + this.mGsmDataRoaming);
        printWriter.println(" mEmergencyOnly=" + this.mEmergencyOnly);
        printWriter.println(" mCSEmergencyOnly=" + this.mCSEmergencyOnly);
        printWriter.println(" mPSEmergencyOnly=" + this.mPSEmergencyOnly);
        printWriter.flush();
        this.mNitzState.dumpState(printWriter);
        printWriter.println(" mLastNitzData=" + this.mLastNitzData);
        printWriter.flush();
        printWriter.println(" mStartedGprsRegCheck=" + this.mStartedGprsRegCheck);
        printWriter.println(" mReportedGprsNoReg=" + this.mReportedGprsNoReg);
        printWriter.println(" mNotification=" + this.mNotification);
        printWriter.println(" mCurSpn=" + this.mCurSpn);
        printWriter.println(" mCurDataSpn=" + this.mCurDataSpn);
        printWriter.println(" mCurShowSpn=" + this.mCurShowSpn);
        printWriter.println(" mCurPlmn=" + this.mCurPlmn);
        printWriter.println(" mCurShowPlmn=" + this.mCurShowPlmn);
        printWriter.flush();
        printWriter.println(" mCurrentOtaspMode=" + this.mCurrentOtaspMode);
        printWriter.println(" mRoamingIndicator=" + this.mRoamingIndicator);
        printWriter.println(" mIsInPrl=" + this.mIsInPrl);
        printWriter.println(" mDefaultRoamingIndicator=" + this.mDefaultRoamingIndicator);
        printWriter.println(" mRegistrationState=" + this.mRegistrationState);
        printWriter.println(" mMdn=" + this.mMdn);
        printWriter.println(" mHomeSystemId=" + Arrays.toString(this.mHomeSystemId));
        printWriter.println(" mHomeNetworkId=" + Arrays.toString(this.mHomeNetworkId));
        printWriter.println(" mMin=" + this.mMin);
        printWriter.println(" mPrlVersion=" + this.mPrlVersion);
        printWriter.println(" mIsMinInfoReady=" + this.mIsMinInfoReady);
        printWriter.println(" mIsEriTextLoaded=" + this.mIsEriTextLoaded);
        printWriter.println(" mIsSubscriptionFromRuim=" + this.mIsSubscriptionFromRuim);
        printWriter.println(" mCdmaSSM=" + this.mCdmaSSM);
        printWriter.println(" mRegistrationDeniedReason=" + this.mRegistrationDeniedReason);
        printWriter.println(" mCurrentCarrier=" + this.mCurrentCarrier);
        printWriter.flush();
        printWriter.println(" mImsRegistered=" + this.mImsRegistered);
        printWriter.println(" mImsRegistrationOnOff=" + this.mImsRegistrationOnOff);
        printWriter.println(" pending radio off event=" + hasMessages(62));
        printWriter.println(" mRadioPowerOffReasons=" + this.mRadioPowerOffReasons);
        printWriter.println(" mDeviceShuttingDown=" + this.mDeviceShuttingDown);
        printWriter.println(" mSpnUpdatePending=" + this.mSpnUpdatePending);
        printWriter.println(" mCellInfoMinIntervalMs=" + this.mCellInfoMinIntervalMs);
        printWriter.println(" mEriManager=" + this.mEriManager);
        this.mLocaleTracker.dump(fileDescriptor, printWriter, strArr);
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "    ");
        this.mCdnr.dump(indentingPrintWriter);
        indentingPrintWriter.println(" Carrier Display Name update records:");
        indentingPrintWriter.increaseIndent();
        this.mCdnrLogs.dump(fileDescriptor, indentingPrintWriter, strArr);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println(" Roaming Log:");
        indentingPrintWriter.increaseIndent();
        this.mRoamingLog.dump(fileDescriptor, indentingPrintWriter, strArr);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println(" Attach Log:");
        indentingPrintWriter.increaseIndent();
        this.mAttachLog.dump(fileDescriptor, indentingPrintWriter, strArr);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println(" Phone Change Log:");
        indentingPrintWriter.increaseIndent();
        this.mPhoneTypeLog.dump(fileDescriptor, indentingPrintWriter, strArr);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println(" Rat Change Log:");
        indentingPrintWriter.increaseIndent();
        this.mRatLog.dump(fileDescriptor, indentingPrintWriter, strArr);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println(" Radio power Log:");
        indentingPrintWriter.increaseIndent();
        this.mRadioPowerLog.dump(fileDescriptor, indentingPrintWriter, strArr);
        indentingPrintWriter.decreaseIndent();
        this.mNitzState.dumpLogs(fileDescriptor, indentingPrintWriter, strArr);
        indentingPrintWriter.flush();
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public boolean isImsRegistered() {
        return this.mImsRegistered;
    }

    protected void checkCorrectThread() {
        if (Thread.currentThread() != getLooper().getThread()) {
            throw new RuntimeException("ServiceStateTracker must be used from within one thread");
        }
    }

    protected boolean isCallerOnDifferentThread() {
        return Thread.currentThread() != getLooper().getThread();
    }

    protected boolean inSameCountry(String str) {
        if (!TextUtils.isEmpty(str) && str.length() >= 5) {
            String homeOperatorNumeric = getHomeOperatorNumeric();
            if (!TextUtils.isEmpty(homeOperatorNumeric) && homeOperatorNumeric.length() >= 5) {
                String substring = str.substring(0, 3);
                String substring2 = homeOperatorNumeric.substring(0, 3);
                String countryCodeForMcc = MccTable.countryCodeForMcc(substring);
                String countryCodeForMcc2 = MccTable.countryCodeForMcc(substring2);
                LocaleTracker localeTracker = this.mLocaleTracker;
                if (localeTracker != null && !TextUtils.isEmpty(localeTracker.getCountryOverride())) {
                    log("inSameCountry:  countryOverride var set.  This should only be set for testing purposes to override the device location.");
                    return this.mLocaleTracker.getCountryOverride().equals(countryCodeForMcc2);
                } else if (!countryCodeForMcc.isEmpty() && !countryCodeForMcc2.isEmpty()) {
                    boolean equals = countryCodeForMcc2.equals(countryCodeForMcc);
                    if (equals) {
                        return equals;
                    }
                    if (("us".equals(countryCodeForMcc2) && "vi".equals(countryCodeForMcc)) || ("vi".equals(countryCodeForMcc2) && "us".equals(countryCodeForMcc))) {
                        return true;
                    }
                    return equals;
                }
            }
        }
        return false;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected void setRoamingType(ServiceState serviceState) {
        boolean z = serviceState.getState() == 0;
        if (z) {
            if (serviceState.getVoiceRoaming()) {
                if (this.mPhone.isPhoneTypeGsm()) {
                    if (inSameCountry(serviceState.getOperatorNumeric())) {
                        serviceState.setVoiceRoamingType(2);
                    } else {
                        serviceState.setVoiceRoamingType(3);
                    }
                } else {
                    int[] intArray = this.mPhone.getContext().getResources().getIntArray(17236011);
                    if (intArray != null && intArray.length > 0) {
                        serviceState.setVoiceRoamingType(2);
                        int cdmaRoamingIndicator = serviceState.getCdmaRoamingIndicator();
                        int i = 0;
                        while (true) {
                            if (i >= intArray.length) {
                                break;
                            } else if (cdmaRoamingIndicator == intArray[i]) {
                                serviceState.setVoiceRoamingType(3);
                                break;
                            } else {
                                i++;
                            }
                        }
                    } else if (inSameCountry(serviceState.getOperatorNumeric())) {
                        serviceState.setVoiceRoamingType(2);
                    } else {
                        serviceState.setVoiceRoamingType(3);
                    }
                }
            } else {
                serviceState.setVoiceRoamingType(0);
            }
        }
        boolean z2 = serviceState.getDataRegistrationState() == 0;
        int rilDataRadioTechnologyForWwan = getRilDataRadioTechnologyForWwan(serviceState);
        if (z2) {
            if (!serviceState.getDataRoaming()) {
                serviceState.setDataRoamingType(0);
            } else if (this.mPhone.isPhoneTypeGsm()) {
                if (!ServiceState.isGsm(rilDataRadioTechnologyForWwan)) {
                    serviceState.setDataRoamingType(1);
                } else if (z) {
                    serviceState.setDataRoamingType(serviceState.getVoiceRoamingType());
                } else {
                    serviceState.setDataRoamingType(1);
                }
            } else if (ServiceState.isCdma(rilDataRadioTechnologyForWwan)) {
                if (z) {
                    serviceState.setDataRoamingType(serviceState.getVoiceRoamingType());
                } else {
                    serviceState.setDataRoamingType(1);
                }
            } else if (inSameCountry(serviceState.getOperatorNumeric())) {
                serviceState.setDataRoamingType(2);
            } else {
                serviceState.setDataRoamingType(3);
            }
        }
    }

    protected String getHomeOperatorNumeric() {
        String simOperatorNumericForPhone = ((TelephonyManager) this.mPhone.getContext().getSystemService("phone")).getSimOperatorNumericForPhone(this.mPhone.getPhoneId());
        return (this.mPhone.isPhoneTypeGsm() || !TextUtils.isEmpty(simOperatorNumericForPhone)) ? simOperatorNumericForPhone : SystemProperties.get(GsmCdmaPhone.PROPERTY_CDMA_HOME_OPERATOR_NUMERIC, PhoneConfigurationManager.SSSS);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected int getPhoneId() {
        return this.mPhone.getPhoneId();
    }

    private void processIwlanRegistrationInfo() {
        boolean z;
        if (this.mCi.getRadioState() == 0) {
            log("set service state as POWER_OFF");
            if (18 == this.mNewSS.getRilDataRadioTechnology()) {
                log("pollStateDone: mNewSS = " + this.mNewSS);
                log("pollStateDone: reset iwlan RAT value");
                z = true;
            } else {
                z = false;
            }
            String operatorAlphaLong = this.mNewSS.getOperatorAlphaLong();
            this.mNewSS.setOutOfService(true);
            if (z) {
                this.mNewSS.setDataRegState(0);
                this.mNewSS.addNetworkRegistrationInfo(new NetworkRegistrationInfo.Builder().setTransportType(2).setDomain(2).setAccessNetworkTechnology(18).setRegistrationState(1).build());
                this.mNewSS.setOperatorAlphaLong(operatorAlphaLong);
                this.mNewSS.setIwlanPreferred(true);
                log("pollStateDone: mNewSS = " + this.mNewSS);
            }
        }
    }

    protected final boolean alwaysOnHomeNetwork(BaseBundle baseBundle) {
        return baseBundle.getBoolean("force_home_network_bool");
    }

    private boolean isInNetwork(BaseBundle baseBundle, String str, String str2) {
        String[] stringArray = baseBundle.getStringArray(str2);
        return stringArray != null && Arrays.asList(stringArray).contains(str);
    }

    protected final boolean isRoamingInGsmNetwork(BaseBundle baseBundle, String str) {
        return isInNetwork(baseBundle, str, "gsm_roaming_networks_string_array");
    }

    protected final boolean isNonRoamingInGsmNetwork(BaseBundle baseBundle, String str) {
        return isInNetwork(baseBundle, str, "gsm_nonroaming_networks_string_array");
    }

    protected final boolean isRoamingInCdmaNetwork(BaseBundle baseBundle, String str) {
        return isInNetwork(baseBundle, str, "cdma_roaming_networks_string_array");
    }

    protected final boolean isNonRoamingInCdmaNetwork(BaseBundle baseBundle, String str) {
        return isInNetwork(baseBundle, str, "cdma_nonroaming_networks_string_array");
    }

    public boolean isDeviceShuttingDown() {
        return this.mDeviceShuttingDown;
    }

    public int getCombinedRegState(ServiceState serviceState) {
        int state = serviceState.getState();
        int dataRegistrationState = serviceState.getDataRegistrationState();
        if ((state == 1 || state == 3) && dataRegistrationState == 0) {
            if (serviceState.getDataNetworkType() == 18) {
                if (this.mPhone.getImsPhone() == null || !this.mPhone.getImsPhone().isWifiCallingEnabled()) {
                    return state;
                }
                log("getCombinedRegState: return STATE_IN_SERVICE for IWLAN as Data is in service and WFC is enabled");
            } else {
                log("getCombinedRegState: return STATE_IN_SERVICE as Data is in service");
            }
            return dataRegistrationState;
        }
        return state;
    }

    private PersistableBundle getCarrierConfig() {
        PersistableBundle configForSubId;
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) this.mPhone.getContext().getSystemService("carrier_config");
        return (carrierConfigManager == null || (configForSubId = carrierConfigManager.getConfigForSubId(this.mPhone.getSubId())) == null) ? CarrierConfigManager.getDefaultConfig() : configForSubId;
    }

    public LocaleTracker getLocaleTracker() {
        return this.mLocaleTracker;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getCdmaEriText(int i, int i2) {
        EriManager eriManager = this.mEriManager;
        return eriManager != null ? eriManager.getCdmaEriText(i, i2) : "no ERI";
    }

    private void updateOperatorNamePattern(PersistableBundle persistableBundle) {
        String string = persistableBundle.getString("operator_name_filter_pattern_string");
        if (TextUtils.isEmpty(string)) {
            return;
        }
        this.mOperatorNameStringPattern = Pattern.compile(string);
        log("mOperatorNameStringPattern: " + this.mOperatorNameStringPattern.toString());
    }

    private void updateOperatorNameForServiceState(ServiceState serviceState) {
        if (serviceState == null) {
            return;
        }
        serviceState.setOperatorName(filterOperatorNameByPattern(serviceState.getOperatorAlphaLong()), filterOperatorNameByPattern(serviceState.getOperatorAlphaShort()), serviceState.getOperatorNumeric());
        List<NetworkRegistrationInfo> networkRegistrationInfoList = serviceState.getNetworkRegistrationInfoList();
        for (int i = 0; i < networkRegistrationInfoList.size(); i++) {
            if (networkRegistrationInfoList.get(i) != null) {
                updateOperatorNameForCellIdentity(networkRegistrationInfoList.get(i).getCellIdentity());
                serviceState.addNetworkRegistrationInfo(networkRegistrationInfoList.get(i));
            }
        }
    }

    private void updateOperatorNameForCellIdentity(CellIdentity cellIdentity) {
        if (cellIdentity == null) {
            return;
        }
        cellIdentity.setOperatorAlphaLong(filterOperatorNameByPattern((String) cellIdentity.getOperatorAlphaLong()));
        cellIdentity.setOperatorAlphaShort(filterOperatorNameByPattern((String) cellIdentity.getOperatorAlphaShort()));
    }

    public void updateOperatorNameForCellInfo(List<CellInfo> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        for (CellInfo cellInfo : list) {
            if (cellInfo.isRegistered()) {
                updateOperatorNameForCellIdentity(cellInfo.getCellIdentity());
            }
        }
    }

    public String filterOperatorNameByPattern(String str) {
        if (this.mOperatorNameStringPattern == null || TextUtils.isEmpty(str)) {
            return str;
        }
        Matcher matcher = this.mOperatorNameStringPattern.matcher(str);
        if (matcher.find()) {
            if (matcher.groupCount() > 0) {
                return matcher.group(1);
            }
            log("filterOperatorNameByPattern: pattern no group");
            return str;
        }
        return str;
    }

    private static int getRilDataRadioTechnologyForWwan(ServiceState serviceState) {
        NetworkRegistrationInfo networkRegistrationInfo = serviceState.getNetworkRegistrationInfo(2, 1);
        return ServiceState.networkTypeToRilRadioTechnology(networkRegistrationInfo != null ? networkRegistrationInfo.getAccessNetworkTechnology() : 0);
    }

    public void registerForNrStateChanged(Handler handler, int i, Object obj) {
        this.mNrStateChangedRegistrants.add(new Registrant(handler, i, obj));
    }

    public void unregisterForNrStateChanged(Handler handler) {
        this.mNrStateChangedRegistrants.remove(handler);
    }

    public void registerForNrFrequencyChanged(Handler handler, int i, Object obj) {
        this.mNrFrequencyChangedRegistrants.add(new Registrant(handler, i, obj));
    }

    public void unregisterForNrFrequencyChanged(Handler handler) {
        this.mNrFrequencyChangedRegistrants.remove(handler);
    }

    public void registerForCssIndicatorChanged(Handler handler, int i, Object obj) {
        this.mCssIndicatorChangedRegistrants.add(new Registrant(handler, i, obj));
    }

    public void unregisterForCssIndicatorChanged(Handler handler) {
        this.mCssIndicatorChangedRegistrants.remove(handler);
    }

    public Set<Integer> getNrContextIds() {
        HashSet hashSet = new HashSet();
        if (!ArrayUtils.isEmpty(this.mLastPhysicalChannelConfigList)) {
            for (PhysicalChannelConfig physicalChannelConfig : this.mLastPhysicalChannelConfigList) {
                if (isNrPhysicalChannelConfig(physicalChannelConfig)) {
                    for (int i : physicalChannelConfig.getContextIds()) {
                        hashSet.add(Integer.valueOf(i));
                    }
                }
            }
        }
        return hashSet;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setDataNetworkTypeForPhone(int i) {
        if (this.mPhone.getUnitTestMode()) {
            return;
        }
        ((TelephonyManager) this.mPhone.getContext().getSystemService("phone")).setDataNetworkTypeForPhone(this.mPhone.getPhoneId(), i);
    }

    public ServiceStateStats getServiceStateStats() {
        return this.mServiceStateStats;
    }

    @VisibleForTesting
    public void setServiceStateStats(ServiceStateStats serviceStateStats) {
        this.mServiceStateStats = serviceStateStats;
    }

    private ContentValues getContentValuesForServiceState(ServiceState serviceState) {
        ContentValues contentValues = new ContentValues();
        Parcel obtain = Parcel.obtain();
        serviceState.writeToParcel(obtain, 0);
        contentValues.put("service_state", obtain.marshall());
        return contentValues;
    }

    public void registerForAreaCodeChanged(Handler handler, int i, Object obj) {
        this.mAreaCodeChangedRegistrants.addUnique(handler, i, obj);
    }

    public void unregisterForAreaCodeChanged(Handler handler) {
        this.mAreaCodeChangedRegistrants.remove(handler);
    }

    public CellIdentity getLastKnownCellIdentity() {
        return this.mLastKnownCellIdentity;
    }
}
