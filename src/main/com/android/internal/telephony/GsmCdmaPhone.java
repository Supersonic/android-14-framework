package com.android.internal.telephony;

import android.app.ActivityManager;
import android.compat.annotation.UnsupportedAppUsage;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.hardware.radio.modem.ImeiInfo;
import android.internal.telephony.sysprop.TelephonyProperties;
import android.net.Uri;
import android.os.AsyncResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.os.ResultReceiver;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.WorkSource;
import android.preference.PreferenceManager;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.provider.Telephony;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telecom.VideoProfile;
import android.telephony.AnomalyReporter;
import android.telephony.BarringInfo;
import android.telephony.CarrierConfigManager;
import android.telephony.CellBroadcastIdRange;
import android.telephony.CellIdentity;
import android.telephony.ImsiEncryptionInfo;
import android.telephony.LinkCapacityEstimate;
import android.telephony.NetworkScanRequest;
import android.telephony.PhoneNumberUtils;
import android.telephony.RadioAccessFamily;
import android.telephony.ServiceState;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.UiccAccessRule;
import android.telephony.UssdResponse;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import com.android.ims.ImsManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.Call;
import com.android.internal.telephony.CommandException;
import com.android.internal.telephony.IccCardConstants;
import com.android.internal.telephony.MmiCode;
import com.android.internal.telephony.OperatorInfo;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneConstants;
import com.android.internal.telephony.PhoneInternalInterface;
import com.android.internal.telephony.cdma.CdmaMmiCode;
import com.android.internal.telephony.cdma.CdmaSubscriptionSourceManager;
import com.android.internal.telephony.data.AccessNetworksManager;
import com.android.internal.telephony.data.DataNetworkController;
import com.android.internal.telephony.data.LinkBandwidthEstimator;
import com.android.internal.telephony.domainselection.DomainSelectionResolver;
import com.android.internal.telephony.emergency.EmergencyNumberTracker;
import com.android.internal.telephony.emergency.EmergencyStateTracker;
import com.android.internal.telephony.gsm.GsmMmiCode;
import com.android.internal.telephony.gsm.SsData;
import com.android.internal.telephony.gsm.SuppServiceNotification;
import com.android.internal.telephony.imsphone.ImsPhone;
import com.android.internal.telephony.imsphone.ImsPhoneCallTracker;
import com.android.internal.telephony.imsphone.ImsPhoneMmiCode;
import com.android.internal.telephony.metrics.TelephonyMetrics;
import com.android.internal.telephony.metrics.VoiceCallSessionStats;
import com.android.internal.telephony.nano.TelephonyProto$RilErrno;
import com.android.internal.telephony.subscription.SubscriptionInfoInternal;
import com.android.internal.telephony.subscription.SubscriptionManagerService;
import com.android.internal.telephony.test.SimulatedRadioControl;
import com.android.internal.telephony.uicc.IccCardApplicationStatus;
import com.android.internal.telephony.uicc.IccCardStatus;
import com.android.internal.telephony.uicc.IccException;
import com.android.internal.telephony.uicc.IccRecords;
import com.android.internal.telephony.uicc.IccUtils;
import com.android.internal.telephony.uicc.IccVmNotSupportedException;
import com.android.internal.telephony.uicc.IsimRecords;
import com.android.internal.telephony.uicc.IsimUiccRecords;
import com.android.internal.telephony.uicc.RuimRecords;
import com.android.internal.telephony.uicc.SIMRecords;
import com.android.internal.telephony.uicc.UiccCardApplication;
import com.android.internal.telephony.uicc.UiccController;
import com.android.internal.telephony.uicc.UiccPort;
import com.android.internal.telephony.uicc.UiccProfile;
import com.android.internal.telephony.uicc.UiccSlot;
import com.android.internal.telephony.util.ArrayUtils;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes.dex */
public class GsmCdmaPhone extends Phone {
    public static final int CANCEL_ECM_TIMER = 1;
    @VisibleForTesting
    public static int ENABLE_UICC_APPS_MAX_RETRIES = 3;
    public static final String LOG_TAG = "GsmCdmaPhone";
    public static final String PROPERTY_CDMA_HOME_OPERATOR_NUMERIC = "ro.cdma.home.operator.numeric";
    public static final int RESTART_ECM_TIMER = 0;
    private boolean mBroadcastEmergencyCallStateChanges;
    private BroadcastReceiver mBroadcastReceiver;
    private CarrierKeyDownloadManager mCDM;
    private CarrierInfoManager mCIM;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public GsmCdmaCallTracker mCT;
    private final CallWaitingController mCallWaitingController;
    private String mCarrierOtaSpNumSchema;
    private final CarrierPrivilegesTracker mCarrierPrivilegesTracker;
    private CdmaSubscriptionSourceManager mCdmaSSM;
    public int mCdmaSubscriptionSource;
    @VisibleForTesting
    public CellBroadcastConfigTracker mCellBroadcastConfigTracker;
    private PhoneInternalInterface.DialArgs mDialArgs;
    @UnsupportedAppUsage
    private Registrant mEcmExitRespRegistrant;
    private final RegistrantList mEcmTimerResetRegistrants;
    private final RegistrantList mEmergencyDomainSelectedRegistrants;
    public EmergencyNumberTracker mEmergencyNumberTracker;
    private String mEsn;
    private Runnable mExitEcmRunnable;
    private IccPhoneBookInterfaceManager mIccPhoneBookIntManager;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private IccSmsInterfaceManager mIccSmsInterfaceManager;
    private String mImei;
    private String mImeiSv;
    private int mImeiType;
    private final ImsManagerFactory mImsManagerFactory;
    private boolean mIsNullCipherAndIntegritySupported;
    private boolean mIsTestingEmergencyCallbackMode;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private IsimUiccRecords mIsimUiccRecords;
    private String mManualNetworkSelectionPlmn;
    private String mMeid;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private ArrayList<MmiCode> mPendingMMIs;
    private int mPrecisePhoneType;
    private boolean mResetModemOnRadioTechnologyChange;
    private int mRilVersion;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public ServiceStateTracker mSST;
    private SIMRecords mSimRecords;
    private boolean mSsOverCdmaSupported;
    private RegistrantList mSsnRegistrants;
    private final SubscriptionManager.OnSubscriptionsChangedListener mSubscriptionsChangedListener;
    private int mTelecomVoiceServiceStateOverride;
    private Boolean mUiccApplicationsEnabled;
    private String mVmNumber;
    private final RegistrantList mVolteSilentRedialRegistrants;
    private PowerManager.WakeLock mWakeLock;
    private static Pattern pOtaSpNumSchema = Pattern.compile("[,\\s]+");
    private static final int[] VOICE_PS_CALL_RADIO_TECHNOLOGY = {14, 19, 18, 20};

    @VisibleForTesting
    /* loaded from: classes.dex */
    public interface ImsManagerFactory {
        ImsManager create(Context context, int i);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private boolean isCfEnable(int i) {
        return i == 1 || i == 3;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private boolean isValidCommandInterfaceCFAction(int i) {
        return i == 0 || i == 1 || i == 3 || i == 4;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private boolean isValidCommandInterfaceCFReason(int i) {
        return i == 0 || i == 1 || i == 2 || i == 3 || i == 4 || i == 5;
    }

    private static int telecomModeToPhoneMode(int i) {
        return (i == 1 || i == 2 || i == 3) ? 1 : 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Cfu {
        final Message mOnComplete;
        final String mSetCfNumber;

        @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
        Cfu(String str, Message message) {
            this.mSetCfNumber = str;
            this.mOnComplete = message;
        }
    }

    public GsmCdmaPhone(Context context, CommandsInterface commandsInterface, PhoneNotifier phoneNotifier, int i, int i2, TelephonyComponentFactory telephonyComponentFactory) {
        this(context, commandsInterface, phoneNotifier, false, i, i2, telephonyComponentFactory);
    }

    public GsmCdmaPhone(Context context, CommandsInterface commandsInterface, PhoneNotifier phoneNotifier, boolean z, int i, int i2, TelephonyComponentFactory telephonyComponentFactory) {
        this(context, commandsInterface, phoneNotifier, z, i, i2, telephonyComponentFactory, new ImsManagerFactory() { // from class: com.android.internal.telephony.GsmCdmaPhone$$ExternalSyntheticLambda3
            @Override // com.android.internal.telephony.GsmCdmaPhone.ImsManagerFactory
            public final ImsManager create(Context context2, int i3) {
                return ImsManager.getInstance(context2, i3);
            }
        });
    }

    public GsmCdmaPhone(Context context, CommandsInterface commandsInterface, PhoneNotifier phoneNotifier, boolean z, int i, int i2, TelephonyComponentFactory telephonyComponentFactory, ImsManagerFactory imsManagerFactory) {
        super(i2 == 1 ? "GSM" : "CDMA", phoneNotifier, context, commandsInterface, z, i, telephonyComponentFactory);
        this.mSsnRegistrants = new RegistrantList();
        this.mCdmaSubscriptionSource = -1;
        this.mUiccApplicationsEnabled = null;
        this.mIsTestingEmergencyCallbackMode = false;
        this.mExitEcmRunnable = new Runnable() { // from class: com.android.internal.telephony.GsmCdmaPhone.1
            @Override // java.lang.Runnable
            public void run() {
                GsmCdmaPhone.this.exitEmergencyCallbackMode();
            }
        };
        this.mPendingMMIs = new ArrayList<>();
        this.mEcmTimerResetRegistrants = new RegistrantList();
        this.mVolteSilentRedialRegistrants = new RegistrantList();
        this.mDialArgs = null;
        this.mEmergencyDomainSelectedRegistrants = new RegistrantList();
        this.mImeiType = -1;
        this.mCellBroadcastConfigTracker = CellBroadcastConfigTracker.make(this, null, true);
        this.mIsNullCipherAndIntegritySupported = false;
        this.mResetModemOnRadioTechnologyChange = false;
        this.mSsOverCdmaSupported = false;
        this.mBroadcastEmergencyCallStateChanges = false;
        this.mTelecomVoiceServiceStateOverride = 1;
        this.mBroadcastReceiver = new BroadcastReceiver() { // from class: com.android.internal.telephony.GsmCdmaPhone.4
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                Rlog.d(GsmCdmaPhone.LOG_TAG, "mBroadcastReceiver: action " + intent.getAction());
                String action = intent.getAction();
                if ("android.telephony.action.CARRIER_CONFIG_CHANGED".equals(action)) {
                    if (GsmCdmaPhone.this.mPhoneId == intent.getIntExtra("android.telephony.extra.SLOT_INDEX", -1)) {
                        GsmCdmaPhone gsmCdmaPhone = GsmCdmaPhone.this;
                        gsmCdmaPhone.sendMessage(gsmCdmaPhone.obtainMessage(43));
                    }
                } else if ("android.telecom.action.CURRENT_TTY_MODE_CHANGED".equals(action)) {
                    GsmCdmaPhone.this.updateTtyMode(intent.getIntExtra("android.telecom.extra.CURRENT_TTY_MODE", 0));
                } else if ("android.telecom.action.TTY_PREFERRED_MODE_CHANGED".equals(action)) {
                    GsmCdmaPhone.this.updateUiTtyMode(intent.getIntExtra("android.telecom.extra.TTY_PREFERRED_MODE", 0));
                }
            }
        };
        this.mPrecisePhoneType = i2;
        this.mVoiceCallSessionStats = new VoiceCallSessionStats(this.mPhoneId, this);
        this.mImsManagerFactory = imsManagerFactory;
        initOnce(commandsInterface);
        initRatSpecific(i2);
        this.mCarrierActionAgent = this.mTelephonyComponentFactory.inject(CarrierActionAgent.class.getName()).makeCarrierActionAgent(this);
        this.mCarrierSignalAgent = this.mTelephonyComponentFactory.inject(CarrierSignalAgent.class.getName()).makeCarrierSignalAgent(this);
        this.mAccessNetworksManager = this.mTelephonyComponentFactory.inject(AccessNetworksManager.class.getName()).makeAccessNetworksManager(this, getLooper());
        this.mSignalStrengthController = this.mTelephonyComponentFactory.inject(SignalStrengthController.class.getName()).makeSignalStrengthController(this);
        this.mSST = this.mTelephonyComponentFactory.inject(ServiceStateTracker.class.getName()).makeServiceStateTracker(this, this.mCi);
        this.mEmergencyNumberTracker = this.mTelephonyComponentFactory.inject(EmergencyNumberTracker.class.getName()).makeEmergencyNumberTracker(this, this.mCi);
        this.mDeviceStateMonitor = this.mTelephonyComponentFactory.inject(DeviceStateMonitor.class.getName()).makeDeviceStateMonitor(this);
        this.mDisplayInfoController = this.mTelephonyComponentFactory.inject(DisplayInfoController.class.getName()).makeDisplayInfoController(this);
        this.mDataNetworkController = this.mTelephonyComponentFactory.inject(DataNetworkController.class.getName()).makeDataNetworkController(this, getLooper());
        this.mCarrierResolver = this.mTelephonyComponentFactory.inject(CarrierResolver.class.getName()).makeCarrierResolver(this);
        this.mCarrierPrivilegesTracker = new CarrierPrivilegesTracker(Looper.myLooper(), this, context);
        getCarrierActionAgent().registerForCarrierAction(0, this, 48, null, false);
        this.mSST.registerForNetworkAttached(this, 19, null);
        this.mSST.registerForVoiceRegStateOrRatChanged(this, 46, null);
        this.mSST.getServiceStateStats().registerDataNetworkControllerCallback();
        if (isSubscriptionManagerServiceEnabled()) {
            this.mSubscriptionManagerService.registerCallback(new SubscriptionManagerService.SubscriptionManagerServiceCallback(new Executor() { // from class: com.android.internal.telephony.GsmCdmaPhone$$ExternalSyntheticLambda2
                @Override // java.util.concurrent.Executor
                public final void execute(Runnable runnable) {
                    GsmCdmaPhone.this.post(runnable);
                }
            }) { // from class: com.android.internal.telephony.GsmCdmaPhone.2
                @Override // com.android.internal.telephony.subscription.SubscriptionManagerService.SubscriptionManagerServiceCallback
                public void onUiccApplicationsEnabled(int i3) {
                    GsmCdmaPhone.this.reapplyUiccAppsEnablementIfNeeded(GsmCdmaPhone.ENABLE_UICC_APPS_MAX_RETRIES);
                }
            });
        } else {
            SubscriptionController.getInstance().registerForUiccAppsEnabled(this, 54, null, false);
        }
        this.mLinkBandwidthEstimator = this.mTelephonyComponentFactory.inject(LinkBandwidthEstimator.class.getName()).makeLinkBandwidthEstimator(this);
        this.mCallWaitingController = new CallWaitingController(this);
        loadTtyMode();
        CallManager.getInstance().registerPhone(this);
        SubscriptionManager.OnSubscriptionsChangedListener onSubscriptionsChangedListener = new SubscriptionManager.OnSubscriptionsChangedListener() { // from class: com.android.internal.telephony.GsmCdmaPhone.3
            @Override // android.telephony.SubscriptionManager.OnSubscriptionsChangedListener
            public void onSubscriptionsChanged() {
                GsmCdmaPhone.this.sendEmptyMessage(62);
            }
        };
        this.mSubscriptionsChangedListener = onSubscriptionsChangedListener;
        ((SubscriptionManager) context.getSystemService(SubscriptionManager.class)).addOnSubscriptionsChangedListener(new HandlerExecutor(this), onSubscriptionsChangedListener);
        logd("GsmCdmaPhone: constructor: sub = " + this.mPhoneId);
    }

    private void initOnce(CommandsInterface commandsInterface) {
        if (commandsInterface instanceof SimulatedRadioControl) {
            this.mSimulatedRadioControl = (SimulatedRadioControl) commandsInterface;
        }
        this.mCT = this.mTelephonyComponentFactory.inject(GsmCdmaCallTracker.class.getName()).makeGsmCdmaCallTracker(this);
        this.mIccPhoneBookIntManager = this.mTelephonyComponentFactory.inject(IccPhoneBookInterfaceManager.class.getName()).makeIccPhoneBookInterfaceManager(this);
        this.mWakeLock = ((PowerManager) this.mContext.getSystemService("power")).newWakeLock(1, LOG_TAG);
        this.mIccSmsInterfaceManager = this.mTelephonyComponentFactory.inject(IccSmsInterfaceManager.class.getName()).makeIccSmsInterfaceManager(this);
        this.mCi.registerForAvailable(this, 1, null);
        this.mCi.registerForOffOrNotAvailable(this, 8, null);
        this.mCi.registerForOn(this, 5, null);
        this.mCi.registerForRadioStateChanged(this, 47, null);
        this.mCi.registerUiccApplicationEnablementChanged(this, 53, null);
        this.mCi.setOnSuppServiceNotification(this, 2, null);
        this.mCi.setOnRegistrationFailed(this, 57, null);
        this.mCi.registerForBarringInfoChanged(this, 58, null);
        this.mCi.setOnUSSD(this, 7, null);
        this.mCi.setOnSs(this, 36, null);
        this.mCdmaSSM = this.mTelephonyComponentFactory.inject(CdmaSubscriptionSourceManager.class.getName()).getCdmaSubscriptionSourceManagerInstance(this.mContext, this.mCi, this, 27, null);
        this.mCi.setEmergencyCallbackMode(this, 25, null);
        this.mCi.registerForExitEmergencyCallbackMode(this, 26, null);
        this.mCi.registerForModemReset(this, 45, null);
        this.mCarrierOtaSpNumSchema = TelephonyManager.from(this.mContext).getOtaSpNumberSchemaForPhone(getPhoneId(), PhoneConfigurationManager.SSSS);
        this.mResetModemOnRadioTechnologyChange = TelephonyProperties.reset_on_radio_tech_change().orElse(Boolean.FALSE).booleanValue();
        this.mCi.registerForRilConnected(this, 41, null);
        this.mCi.registerForVoiceRadioTechChanged(this, 39, null);
        this.mCi.registerForLceInfo(this, 59, null);
        this.mCi.registerForCarrierInfoForImsiEncryption(this, 60, null);
        this.mCi.registerForTriggerImsDeregistration(this, 65, null);
        this.mCi.registerForNotifyAnbr(this, 68, null);
        IntentFilter intentFilter = new IntentFilter("android.telephony.action.CARRIER_CONFIG_CHANGED");
        intentFilter.addAction("android.telecom.action.CURRENT_TTY_MODE_CHANGED");
        intentFilter.addAction("android.telecom.action.TTY_PREFERRED_MODE_CHANGED");
        this.mContext.registerReceiver(this.mBroadcastReceiver, intentFilter, "android.permission.MODIFY_PHONE_STATE", null, 2);
        this.mCDM = new CarrierKeyDownloadManager(this);
        this.mCIM = new CarrierInfoManager();
        if (isSubscriptionManagerServiceEnabled()) {
            initializeCarrierApps();
        }
    }

    private void initRatSpecific(int i) {
        String str;
        this.mPendingMMIs.clear();
        this.mIccPhoneBookIntManager.updateIccRecords(null);
        this.mPrecisePhoneType = i;
        logd("Precise phone type " + this.mPrecisePhoneType);
        TelephonyManager from = TelephonyManager.from(this.mContext);
        UiccProfile uiccProfile = getUiccProfile();
        if (isPhoneTypeGsm()) {
            this.mCi.setPhoneType(1);
            from.setPhoneType(getPhoneId(), 1);
            if (uiccProfile != null) {
                uiccProfile.setVoiceRadioTech(3);
                return;
            }
            return;
        }
        this.mCdmaSubscriptionSource = this.mCdmaSSM.getCdmaSubscriptionSource();
        boolean inEcmMode = Phone.getInEcmMode();
        this.mIsPhoneInEcmState = inEcmMode;
        if (inEcmMode) {
            if (DomainSelectionResolver.getInstance().isDomainSelectionSupported()) {
                EmergencyStateTracker.getInstance().exitEmergencyCallbackMode();
            } else {
                this.mCi.exitEmergencyCallbackMode(null);
            }
        }
        this.mCi.setPhoneType(2);
        from.setPhoneType(getPhoneId(), 2);
        if (uiccProfile != null) {
            uiccProfile.setVoiceRadioTech(6);
        }
        String str2 = SystemProperties.get("ro.cdma.home.operator.alpha");
        String str3 = SystemProperties.get(PROPERTY_CDMA_HOME_OPERATOR_NUMERIC);
        logd("init: operatorAlpha='" + str2 + "' operatorNumeric='" + str3 + "'");
        if (!TextUtils.isEmpty(str2)) {
            logd("init: set 'gsm.sim.operator.alpha' to operator='" + str2 + "'");
            from.setSimOperatorNameForPhone(this.mPhoneId, str2);
        }
        if (!TextUtils.isEmpty(str3)) {
            logd("init: set 'gsm.sim.operator.numeric' to operator='" + str3 + "'");
            StringBuilder sb = new StringBuilder();
            sb.append("update icc_operator_numeric=");
            sb.append(str3);
            logd(sb.toString());
            from.setSimOperatorNumericForPhone(this.mPhoneId, str3);
            if (isSubscriptionManagerServiceEnabled()) {
                this.mSubscriptionManagerService.setMccMnc(getSubId(), str3);
            } else {
                SubscriptionController.getInstance().setMccMnc(str3, getSubId());
            }
            try {
                str = MccTable.countryCodeForMcc(str3.substring(0, 3));
            } catch (StringIndexOutOfBoundsException e) {
                Rlog.e(LOG_TAG, "init: countryCodeForMcc error", e);
                str = PhoneConfigurationManager.SSSS;
            }
            logd("init: set 'gsm.sim.operator.iso-country' to iso=" + str);
            from.setSimCountryIsoForPhone(this.mPhoneId, str);
            if (isSubscriptionManagerServiceEnabled()) {
                this.mSubscriptionManagerService.setCountryIso(getSubId(), str);
            } else {
                SubscriptionController.getInstance().setCountryIso(str, getSubId());
            }
            logd("update mccmnc=" + str3);
            MccTable.updateMccMncConfiguration(this.mContext, str3);
        }
        updateCurrentCarrierInProvider(str3);
    }

    private void initializeCarrierApps() {
        if (this.mPhoneId != 0) {
            return;
        }
        logd("initializeCarrierApps");
        this.mContext.registerReceiverForAllUsers(new BroadcastReceiver() { // from class: com.android.internal.telephony.GsmCdmaPhone.5
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                if ("android.intent.action.USER_FOREGROUND".equals(intent.getAction())) {
                    UserHandle userHandle = (UserHandle) intent.getParcelableExtra("android.intent.extra.USER");
                    CarrierAppUtils.disableCarrierAppsUntilPrivileged(GsmCdmaPhone.this.mContext.getOpPackageName(), TelephonyManager.getDefault(), userHandle != null ? userHandle.getIdentifier() : 0, GsmCdmaPhone.this.mContext);
                }
            }
        }, new IntentFilter("android.intent.action.USER_FOREGROUND"), null, null);
        CarrierAppUtils.disableCarrierAppsUntilPrivileged(this.mContext.getOpPackageName(), TelephonyManager.getDefault(), ActivityManager.getCurrentUser(), this.mContext);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public boolean isPhoneTypeGsm() {
        return this.mPrecisePhoneType == 1;
    }

    public boolean isPhoneTypeCdma() {
        return this.mPrecisePhoneType == 2;
    }

    public boolean isPhoneTypeCdmaLte() {
        return this.mPrecisePhoneType == 6;
    }

    private void switchPhoneType(int i) {
        removeCallbacks(this.mExitEcmRunnable);
        initRatSpecific(i);
        this.mSST.updatePhoneType();
        setPhoneName(i == 1 ? "GSM" : "CDMA");
        onUpdateIccAvailability();
        unregisterForIccRecordEvents();
        registerForIccRecordEvents();
        this.mCT.updatePhoneType();
        int radioState = this.mCi.getRadioState();
        if (radioState != 2) {
            handleRadioAvailable();
            if (radioState == 1) {
                handleRadioOn();
            }
        }
        if (radioState != 1) {
            handleRadioOffOrNotAvailable();
        }
    }

    private void updateLinkCapacityEstimate(List<LinkCapacityEstimate> list) {
        logd("updateLinkCapacityEstimate: lce list=" + list);
        if (list == null) {
            return;
        }
        notifyLinkCapacityEstimateChanged(list);
    }

    protected void finalize() {
        logd("GsmCdmaPhone finalized");
        PowerManager.WakeLock wakeLock = this.mWakeLock;
        if (wakeLock == null || !wakeLock.isHeld()) {
            return;
        }
        Rlog.e(LOG_TAG, "UNEXPECTED; mWakeLock is held when finalizing.");
        this.mWakeLock.release();
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public ServiceState getServiceState() {
        ServiceStateTracker serviceStateTracker = this.mSST;
        ServiceState serviceState = serviceStateTracker != null ? serviceStateTracker.getServiceState() : new ServiceState();
        Phone phone = this.mImsPhone;
        return mergeVoiceServiceStates(serviceState, phone != null ? phone.getServiceState() : new ServiceState(), this.mTelecomVoiceServiceStateOverride);
    }

    @Override // com.android.internal.telephony.Phone
    public void setVoiceServiceStateOverride(boolean z) {
        ServiceStateTracker serviceStateTracker;
        int i = !z ? 1 : 0;
        boolean z2 = i != this.mTelecomVoiceServiceStateOverride;
        this.mTelecomVoiceServiceStateOverride = i;
        if (!z2 || (serviceStateTracker = this.mSST) == null) {
            return;
        }
        serviceStateTracker.onTelecomVoiceServiceStateOverrideChanged();
    }

    @Override // com.android.internal.telephony.Phone
    public void getCellIdentity(WorkSource workSource, Message message) {
        this.mSST.requestCellIdentity(workSource, message);
    }

    @Override // com.android.internal.telephony.Phone
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public PhoneConstants.State getState() {
        PhoneConstants.State state;
        Phone phone = this.mImsPhone;
        return (phone == null || (state = phone.getState()) == PhoneConstants.State.IDLE) ? this.mCT.mState : state;
    }

    @Override // com.android.internal.telephony.Phone
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public int getPhoneType() {
        return this.mPrecisePhoneType == 1 ? 1 : 2;
    }

    @Override // com.android.internal.telephony.Phone
    public ServiceStateTracker getServiceStateTracker() {
        return this.mSST;
    }

    @Override // com.android.internal.telephony.Phone
    public EmergencyNumberTracker getEmergencyNumberTracker() {
        return this.mEmergencyNumberTracker;
    }

    @Override // com.android.internal.telephony.Phone
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public CallTracker getCallTracker() {
        return this.mCT;
    }

    @Override // com.android.internal.telephony.Phone
    public AccessNetworksManager getAccessNetworksManager() {
        return this.mAccessNetworksManager;
    }

    @Override // com.android.internal.telephony.Phone
    public DeviceStateMonitor getDeviceStateMonitor() {
        return this.mDeviceStateMonitor;
    }

    @Override // com.android.internal.telephony.Phone
    public DisplayInfoController getDisplayInfoController() {
        return this.mDisplayInfoController;
    }

    @Override // com.android.internal.telephony.Phone
    public SignalStrengthController getSignalStrengthController() {
        return this.mSignalStrengthController;
    }

    @Override // com.android.internal.telephony.Phone
    public void updateVoiceMail() {
        if (isPhoneTypeGsm()) {
            IccRecords iccRecords = this.mIccRecords.get();
            int voiceMessageCount = iccRecords != null ? iccRecords.getVoiceMessageCount() : 0;
            if (voiceMessageCount == -2) {
                voiceMessageCount = getStoredVoiceMessageCount();
            }
            logd("updateVoiceMail countVoiceMessages = " + voiceMessageCount + " subId " + getSubId());
            setVoiceMessageCount(voiceMessageCount);
            return;
        }
        setVoiceMessageCount(getStoredVoiceMessageCount());
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public List<? extends MmiCode> getPendingMmiCodes() {
        return this.mPendingMMIs;
    }

    @Override // com.android.internal.telephony.Phone
    public boolean isDataSuspended() {
        return (this.mCT.mState == PhoneConstants.State.IDLE || this.mSST.isConcurrentVoiceAndDataAllowed()) ? false : true;
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public int getDataActivityState() {
        return getDataNetworkController().getDataActivity();
    }

    public void notifyPhoneStateChanged() {
        this.mNotifier.notifyPhoneState(this);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void notifyPreciseCallStateChanged() {
        this.mPreciseCallStateRegistrants.notifyRegistrants(new AsyncResult((Object) null, this, (Throwable) null));
        this.mNotifier.notifyPreciseCallState(this, null, null, null);
    }

    public void notifyNewRingingConnection(Connection connection) {
        super.notifyNewRingingConnectionP(connection);
    }

    public void notifyDisconnect(Connection connection) {
        this.mDisconnectRegistrants.notifyResult(connection);
        this.mNotifier.notifyDisconnectCause(this, connection.getDisconnectCause(), connection.getPreciseDisconnectCause());
    }

    public void notifyUnknownConnection(Connection connection) {
        super.notifyUnknownConnectionP(connection);
    }

    @Override // com.android.internal.telephony.Phone
    public boolean isInEmergencyCall() {
        if (isPhoneTypeGsm()) {
            return false;
        }
        return this.mCT.isInEmergencyCall();
    }

    @Override // com.android.internal.telephony.Phone
    protected void setIsInEmergencyCall() {
        if (isPhoneTypeGsm()) {
            return;
        }
        this.mCT.setIsInEmergencyCall();
    }

    @Override // com.android.internal.telephony.Phone
    public boolean isInEmergencySmsMode() {
        Phone phone;
        return super.isInEmergencySmsMode() || ((phone = this.mImsPhone) != null && phone.isInEmergencySmsMode());
    }

    private void sendEmergencyCallbackModeChange() {
        Intent intent = new Intent("android.intent.action.EMERGENCY_CALLBACK_MODE_CHANGED");
        intent.putExtra("android.telephony.extra.PHONE_IN_ECM_STATE", isInEcm());
        SubscriptionManager.putPhoneIdAndSubIdExtra(intent, getPhoneId());
        this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
        logi("sendEmergencyCallbackModeChange");
    }

    @Override // com.android.internal.telephony.Phone
    public void sendEmergencyCallStateChange(boolean z) {
        if (!isPhoneTypeCdma()) {
            logi("sendEmergencyCallStateChange - skip for non-cdma");
        } else if (this.mBroadcastEmergencyCallStateChanges) {
            Intent intent = new Intent("android.intent.action.EMERGENCY_CALL_STATE_CHANGED");
            intent.putExtra("android.telephony.extra.PHONE_IN_EMERGENCY_CALL", z);
            SubscriptionManager.putPhoneIdAndSubIdExtra(intent, getPhoneId());
            this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
            Rlog.d(LOG_TAG, "sendEmergencyCallStateChange: callActive " + z);
        }
    }

    @Override // com.android.internal.telephony.Phone
    public void setBroadcastEmergencyCallStateChanges(boolean z) {
        this.mBroadcastEmergencyCallStateChanges = z;
    }

    public void notifySuppServiceFailed(PhoneInternalInterface.SuppService suppService) {
        this.mSuppServiceFailedRegistrants.notifyResult(suppService);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void notifyServiceStateChanged(ServiceState serviceState) {
        super.notifyServiceStateChangedP(serviceState);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void notifyServiceStateChangedForSubId(ServiceState serviceState, int i) {
        super.notifyServiceStateChangedPForSubId(serviceState, i);
    }

    public void notifyLocationChanged(CellIdentity cellIdentity) {
        this.mNotifier.notifyCellLocation(this, cellIdentity);
    }

    @Override // com.android.internal.telephony.Phone
    public void notifyCallForwardingIndicator() {
        this.mNotifier.notifyCallForwardingChanged(this);
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void registerForSuppServiceNotification(Handler handler, int i, Object obj) {
        this.mSsnRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void unregisterForSuppServiceNotification(Handler handler) {
        this.mSsnRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.Phone
    public void registerForSimRecordsLoaded(Handler handler, int i, Object obj) {
        this.mSimRecordsLoadedRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.Phone
    public void unregisterForSimRecordsLoaded(Handler handler) {
        this.mSimRecordsLoadedRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void acceptCall(int i) throws CallStateException {
        Phone phone = this.mImsPhone;
        if (phone != null && phone.getRingingCall().isRinging()) {
            phone.acceptCall(i);
        } else {
            this.mCT.acceptCall();
        }
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void rejectCall() throws CallStateException {
        this.mCT.rejectCall();
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void switchHoldingAndActive() throws CallStateException {
        this.mCT.switchWaitingOrHoldingAndActive();
    }

    @Override // com.android.internal.telephony.Phone
    public String getIccSerialNumber() {
        IccRecords iccRecords = this.mIccRecords.get();
        if (!isPhoneTypeGsm() && iccRecords == null) {
            iccRecords = this.mUiccController.getIccRecords(this.mPhoneId, 1);
        }
        if (iccRecords != null) {
            return iccRecords.getIccId();
        }
        return null;
    }

    @Override // com.android.internal.telephony.Phone
    public String getFullIccSerialNumber() {
        IccRecords iccRecords = this.mIccRecords.get();
        if (!isPhoneTypeGsm() && iccRecords == null) {
            iccRecords = this.mUiccController.getIccRecords(this.mPhoneId, 1);
        }
        if (iccRecords != null) {
            return iccRecords.getFullIccId();
        }
        return null;
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public boolean canConference() {
        Phone phone = this.mImsPhone;
        if (phone == null || !phone.canConference()) {
            if (isPhoneTypeGsm()) {
                return this.mCT.canConference();
            }
            loge("canConference: not possible in CDMA");
            return false;
        }
        return true;
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void conference() {
        Phone phone = this.mImsPhone;
        if (phone != null && phone.canConference()) {
            logd("conference() - delegated to IMS phone");
            try {
                this.mImsPhone.conference();
            } catch (CallStateException e) {
                loge(e.toString());
            }
        } else if (isPhoneTypeGsm()) {
            this.mCT.conference();
        } else {
            loge("conference: not possible in CDMA");
        }
    }

    @Override // com.android.internal.telephony.Phone
    public void enableEnhancedVoicePrivacy(boolean z, Message message) {
        if (isPhoneTypeGsm()) {
            loge("enableEnhancedVoicePrivacy: not expected on GSM");
        } else {
            this.mCi.setPreferredVoicePrivacy(z, message);
        }
    }

    @Override // com.android.internal.telephony.Phone
    public void getEnhancedVoicePrivacy(Message message) {
        if (isPhoneTypeGsm()) {
            loge("getEnhancedVoicePrivacy: not expected on GSM");
        } else {
            this.mCi.getPreferredVoicePrivacy(message);
        }
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void clearDisconnected() {
        this.mCT.clearDisconnected();
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public boolean canTransfer() {
        if (isPhoneTypeGsm()) {
            return this.mCT.canTransfer();
        }
        loge("canTransfer: not possible in CDMA");
        return false;
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void explicitCallTransfer() {
        if (isPhoneTypeGsm()) {
            this.mCT.explicitCallTransfer();
        } else {
            loge("explicitCallTransfer: not possible in CDMA");
        }
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public GsmCdmaCall getForegroundCall() {
        return this.mCT.mForegroundCall;
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public GsmCdmaCall getBackgroundCall() {
        return this.mCT.mBackgroundCall;
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public Call getRingingCall() {
        Phone phone = this.mImsPhone;
        if (phone != null && phone.getRingingCall().isRinging()) {
            return phone.getRingingCall();
        }
        if (!this.mCT.mRingingCall.isRinging() && this.mCT.getRingingHandoverConnection() != null && this.mCT.getRingingHandoverConnection().getCall() != null && this.mCT.getRingingHandoverConnection().getCall().isRinging()) {
            return this.mCT.getRingingHandoverConnection().getCall();
        }
        return this.mCT.mRingingCall;
    }

    @Override // com.android.internal.telephony.Phone
    public CarrierPrivilegesTracker getCarrierPrivilegesTracker() {
        return this.mCarrierPrivilegesTracker;
    }

    private static ServiceState mergeVoiceServiceStates(ServiceState serviceState, ServiceState serviceState2, int i) {
        if (serviceState.getState() == 0) {
            return serviceState;
        }
        if (i != 0) {
            i = serviceState2.getState() == 0 ? serviceState.getDataRegistrationState() : 1;
        }
        if (i != 0) {
            return serviceState;
        }
        ServiceState serviceState3 = new ServiceState(serviceState);
        serviceState3.setVoiceRegState(i);
        serviceState3.setEmergencyOnly(false);
        return serviceState3;
    }

    private boolean handleCallDeflectionIncallSupplementaryService(String str) {
        if (str.length() > 1) {
            return false;
        }
        Call.State state = getRingingCall().getState();
        Call.State state2 = Call.State.IDLE;
        if (state != state2) {
            logd("MmiCode 0: rejectCall");
            try {
                this.mCT.rejectCall();
            } catch (CallStateException e) {
                Rlog.d(LOG_TAG, "reject failed", e);
                notifySuppServiceFailed(PhoneInternalInterface.SuppService.REJECT);
            }
        } else if (getBackgroundCall().getState() != state2) {
            logd("MmiCode 0: hangupWaitingOrBackground");
            this.mCT.hangupWaitingOrBackground();
        }
        return true;
    }

    private boolean handleCallWaitingIncallSupplementaryService(String str) {
        int length = str.length();
        if (length > 2) {
            return false;
        }
        GsmCdmaCall foregroundCall = getForegroundCall();
        try {
            if (length > 1) {
                int charAt = str.charAt(1) - '0';
                if (charAt >= 1 && charAt <= 19) {
                    logd("MmiCode 1: hangupConnectionByIndex " + charAt);
                    this.mCT.hangupConnectionByIndex(foregroundCall, charAt);
                }
            } else if (foregroundCall.getState() != Call.State.IDLE) {
                logd("MmiCode 1: hangup foreground");
                this.mCT.hangup(foregroundCall);
            } else {
                logd("MmiCode 1: switchWaitingOrHoldingAndActive");
                this.mCT.switchWaitingOrHoldingAndActive();
            }
        } catch (CallStateException e) {
            Rlog.d(LOG_TAG, "hangup failed", e);
            notifySuppServiceFailed(PhoneInternalInterface.SuppService.HANGUP);
        }
        return true;
    }

    private boolean handleCallHoldIncallSupplementaryService(String str) {
        int length = str.length();
        if (length > 2) {
            return false;
        }
        GsmCdmaCall foregroundCall = getForegroundCall();
        if (length > 1) {
            try {
                int charAt = str.charAt(1) - '0';
                GsmCdmaConnection connectionByIndex = this.mCT.getConnectionByIndex(foregroundCall, charAt);
                if (connectionByIndex != null && charAt >= 1 && charAt <= 19) {
                    logd("MmiCode 2: separate call " + charAt);
                    this.mCT.separate(connectionByIndex);
                } else {
                    logd("separate: invalid call index " + charAt);
                    notifySuppServiceFailed(PhoneInternalInterface.SuppService.SEPARATE);
                }
            } catch (CallStateException e) {
                Rlog.d(LOG_TAG, "separate failed", e);
                notifySuppServiceFailed(PhoneInternalInterface.SuppService.SEPARATE);
            }
        } else {
            try {
                if (getRingingCall().getState() != Call.State.IDLE) {
                    logd("MmiCode 2: accept ringing call");
                    this.mCT.acceptCall();
                } else {
                    logd("MmiCode 2: switchWaitingOrHoldingAndActive");
                    this.mCT.switchWaitingOrHoldingAndActive();
                }
            } catch (CallStateException e2) {
                Rlog.d(LOG_TAG, "switch failed", e2);
                notifySuppServiceFailed(PhoneInternalInterface.SuppService.SWITCH);
            }
        }
        return true;
    }

    private boolean handleMultipartyIncallSupplementaryService(String str) {
        if (str.length() > 1) {
            return false;
        }
        logd("MmiCode 3: merge calls");
        conference();
        return true;
    }

    private boolean handleEctIncallSupplementaryService(String str) {
        if (str.length() != 1) {
            return false;
        }
        logd("MmiCode 4: explicit call transfer");
        explicitCallTransfer();
        return true;
    }

    private boolean handleCcbsIncallSupplementaryService(String str) {
        if (str.length() > 1) {
            return false;
        }
        Rlog.i(LOG_TAG, "MmiCode 5: CCBS not supported!");
        notifySuppServiceFailed(PhoneInternalInterface.SuppService.UNKNOWN);
        return true;
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public boolean handleInCallMmiCommands(String str) throws CallStateException {
        if (!isPhoneTypeGsm()) {
            loge("method handleInCallMmiCommands is NOT supported in CDMA!");
            return false;
        }
        Phone phone = this.mImsPhone;
        if (phone != null && phone.getServiceState().getState() == 0) {
            return phone.handleInCallMmiCommands(str);
        }
        if (isInCall() && !TextUtils.isEmpty(str)) {
            switch (str.charAt(0)) {
                case '0':
                    return handleCallDeflectionIncallSupplementaryService(str);
                case '1':
                    return handleCallWaitingIncallSupplementaryService(str);
                case '2':
                    return handleCallHoldIncallSupplementaryService(str);
                case '3':
                    return handleMultipartyIncallSupplementaryService(str);
                case '4':
                    return handleEctIncallSupplementaryService(str);
                case '5':
                    return handleCcbsIncallSupplementaryService(str);
                default:
                    return false;
            }
        }
        return false;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public boolean isInCall() {
        return getForegroundCall().getState().isAlive() || getBackgroundCall().getState().isAlive() || getRingingCall().getState().isAlive();
    }

    private boolean useImsForCall(PhoneInternalInterface.DialArgs dialArgs) {
        Phone phone;
        return isImsUseEnabled() && (phone = this.mImsPhone) != null && (phone.isVoiceOverCellularImsEnabled() || this.mImsPhone.isWifiCallingEnabled() || (this.mImsPhone.isVideoEnabled() && VideoProfile.isVideo(dialArgs.videoState))) && this.mImsPhone.getServiceState().getState() == 0;
    }

    public boolean useImsForEmergency() {
        return this.mImsPhone != null && ((CarrierConfigManager) this.mContext.getSystemService("carrier_config")).getConfigForSubId(getSubId()).getBoolean("carrier_use_ims_first_for_emergency_bool") && ImsManager.getInstance(this.mContext, this.mPhoneId).isNonTtyOrTtyOnVolteEnabled() && this.mImsPhone.isImsAvailable();
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public Connection startConference(String[] strArr, PhoneInternalInterface.DialArgs dialArgs) throws CallStateException {
        Phone phone = this.mImsPhone;
        boolean useImsForCall = useImsForCall(dialArgs);
        logd("useImsForCall=" + useImsForCall);
        if (useImsForCall) {
            try {
                logd("Trying IMS PS Conference call");
                return phone.startConference(strArr, dialArgs);
            } catch (CallStateException e) {
                this.logd("IMS PS conference call exception " + e + "useImsForCall =" + useImsForCall + ", imsPhone =" + phone);
                CallStateException callStateException = new CallStateException(e.getError(), e.getMessage());
                callStateException.setStackTrace(e.getStackTrace());
                throw callStateException;
            }
        }
        throw new CallStateException(1, "cannot dial conference call in out of service");
    }

    /* JADX WARN: Removed duplicated region for block: B:105:0x029d  */
    /* JADX WARN: Removed duplicated region for block: B:106:0x02a6  */
    /* JADX WARN: Removed duplicated region for block: B:109:0x02b1  */
    /* JADX WARN: Removed duplicated region for block: B:110:0x02ba  */
    /* JADX WARN: Removed duplicated region for block: B:113:0x02c5  */
    /* JADX WARN: Removed duplicated region for block: B:114:0x02ce  */
    /* JADX WARN: Removed duplicated region for block: B:117:0x02d9  */
    /* JADX WARN: Removed duplicated region for block: B:120:0x02f1  */
    /* JADX WARN: Removed duplicated region for block: B:125:0x030a  */
    /* JADX WARN: Removed duplicated region for block: B:127:0x030e  */
    /* JADX WARN: Removed duplicated region for block: B:164:0x03c9  */
    /* JADX WARN: Removed duplicated region for block: B:174:0x03eb  */
    /* JADX WARN: Removed duplicated region for block: B:187:0x0412  */
    /* JADX WARN: Removed duplicated region for block: B:202:0x0450 A[ADDED_TO_REGION] */
    @Override // com.android.internal.telephony.PhoneInternalInterface
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public Connection dial(String str, PhoneInternalInterface.DialArgs dialArgs, Consumer<Phone> consumer) throws CallStateException {
        boolean isEmergencyNumber;
        ImsPhone.ImsDialArgs imsDialArgs;
        String str2;
        boolean z;
        boolean z2;
        Phone phone;
        boolean z3;
        boolean z4;
        Phone phone2;
        String str3;
        Consumer<Phone> consumer2;
        ImsPhone.ImsDialArgs imsDialArgs2;
        ServiceStateTracker serviceStateTracker;
        ServiceStateTracker serviceStateTracker2;
        ServiceStateTracker serviceStateTracker3;
        boolean z5;
        if (!isPhoneTypeGsm() && dialArgs.uusInfo != null) {
            throw new CallStateException("Sending UUS information NOT supported in CDMA!");
        }
        String checkForTestEmergencyNumber = checkForTestEmergencyNumber(str);
        boolean z6 = !TextUtils.equals(str, checkForTestEmergencyNumber);
        if (z6) {
            logi("dialString replaced for possible emergency number: " + str + " -> " + checkForTestEmergencyNumber);
        } else {
            checkForTestEmergencyNumber = str;
        }
        PersistableBundle configForSubId = ((CarrierConfigManager) this.mContext.getSystemService("carrier_config")).getConfigForSubId(getSubId());
        boolean z7 = configForSubId.getBoolean("support_wps_over_ims_bool");
        boolean z8 = configForSubId.getBoolean("use_only_dialed_sim_ecc_list_bool");
        TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService(TelephonyManager.class);
        if (z8) {
            isEmergencyNumber = getEmergencyNumberTracker().isEmergencyNumber(checkForTestEmergencyNumber);
            logi("dial; isEmergency=" + isEmergencyNumber + " (based on this phone only); globalIsEmergency=" + telephonyManager.isEmergencyNumber(checkForTestEmergencyNumber));
        } else {
            isEmergencyNumber = telephonyManager.isEmergencyNumber(checkForTestEmergencyNumber);
            logi("dial; isEmergency=" + isEmergencyNumber + " (based on all phones)");
        }
        if (dialArgs.isEmergency) {
            logi("dial; isEmergency=" + isEmergencyNumber + " (domain selection module)");
            isEmergencyNumber = true;
        }
        boolean z9 = checkForTestEmergencyNumber != null && (checkForTestEmergencyNumber.startsWith("*272") || checkForTestEmergencyNumber.startsWith("*31#*272") || checkForTestEmergencyNumber.startsWith("#31#*272"));
        ImsPhone.ImsDialArgs build = ImsPhone.ImsDialArgs.Builder.from(dialArgs).setIsEmergency(isEmergencyNumber).setIsWpsCall(z9).build();
        this.mDialArgs = build;
        Phone phone3 = this.mImsPhone;
        boolean z10 = isEmergencyNumber && useImsForEmergency();
        String extractNetworkPortionAlt = PhoneNumberUtils.extractNetworkPortionAlt(PhoneNumberUtils.stripSeparators(checkForTestEmergencyNumber));
        boolean z11 = (extractNetworkPortionAlt.startsWith("*") || extractNetworkPortionAlt.startsWith("#")) && extractNetworkPortionAlt.endsWith("#");
        boolean isSuppServiceCodes = ImsPhoneMmiCode.isSuppServiceCodes(extractNetworkPortionAlt, this);
        boolean z12 = z11 && !isSuppServiceCodes;
        boolean z13 = phone3 != null && phone3.isUtEnabled();
        boolean z14 = useImsForCall(build) && (!z9 || z7);
        Bundle bundle = build.intentExtras;
        boolean z15 = z10;
        if (bundle != null) {
            z = z6;
            if (bundle.containsKey("compare_domain")) {
                imsDialArgs = build;
                int i = bundle.getInt("dial_domain");
                if (isEmergencyNumber || ((z11 && !z12) || ((i != 2 || z14) && !((i == 1 && z14) || i == 0 || i == 3)))) {
                    str2 = checkForTestEmergencyNumber;
                    z2 = z7;
                    phone = phone3;
                } else {
                    StringBuilder sb = new StringBuilder();
                    str2 = checkForTestEmergencyNumber;
                    sb.append("[Anomaly] legacy-useImsForCall:");
                    sb.append(z14);
                    sb.append(", NCDS-domain:");
                    sb.append(i);
                    loge(sb.toString());
                    UUID fromString = UUID.fromString("bfae6c2e-ca2f-4121-b167-9cad26a3b353");
                    phone = phone3;
                    StringBuilder sb2 = new StringBuilder();
                    z2 = z7;
                    sb2.append("Domain selection results don't match. useImsForCall:");
                    sb2.append(z14);
                    sb2.append(", NCDS-domain:");
                    sb2.append(i);
                    AnomalyReporter.reportAnomaly(fromString, sb2.toString());
                }
                bundle.remove("compare_domain");
                if (bundle == null && bundle.containsKey("dial_domain")) {
                    int i2 = bundle.getInt("dial_domain");
                    logi("dial domain=" + i2);
                    if (i2 == 2) {
                        if (!isEmergencyNumber) {
                            if (z11 && !z12) {
                                loge("dial unexpected Ut domain selection, ignored");
                                z5 = false;
                            } else {
                                z5 = false;
                                z14 = true;
                                bundle.remove("dial_domain");
                                z3 = z5;
                                z4 = false;
                            }
                        }
                        z5 = true;
                    } else {
                        if (i2 == 4) {
                            if (isEmergencyNumber) {
                                bundle.putString("CallRadioTech", String.valueOf(18));
                                z5 = true;
                            } else {
                                loge("dial DOMAIN_NON_3GPP_PS should be used only for emergency calls");
                            }
                        }
                        z5 = false;
                    }
                    z14 = false;
                    bundle.remove("dial_domain");
                    z3 = z5;
                    z4 = false;
                } else {
                    z3 = z15;
                    z4 = z13;
                }
                StringBuilder sb3 = new StringBuilder();
                sb3.append("useImsForCall=");
                sb3.append(z14);
                sb3.append(", useOnlyDialedSimEccList=");
                sb3.append(z8);
                sb3.append(", isEmergency=");
                sb3.append(isEmergencyNumber);
                sb3.append(", useImsForEmergency=");
                sb3.append(z3);
                sb3.append(", useImsForUt=");
                sb3.append(z4);
                sb3.append(", isUt=");
                sb3.append(z11);
                sb3.append(", isSuppServiceCode=");
                sb3.append(isSuppServiceCodes);
                sb3.append(", isPotentialUssdCode=");
                sb3.append(z12);
                sb3.append(", isWpsCall=");
                sb3.append(z9);
                sb3.append(", allowWpsOverIms=");
                boolean z16 = z2;
                sb3.append(z16);
                sb3.append(", imsPhone=");
                phone2 = phone;
                sb3.append(phone2);
                sb3.append(", imsPhone.isVoiceOverCellularImsEnabled()=");
                sb3.append(phone2 == null ? Boolean.valueOf(phone2.isVoiceOverCellularImsEnabled()) : "N/A");
                sb3.append(", imsPhone.isVowifiEnabled()=");
                sb3.append(phone2 == null ? Boolean.valueOf(phone2.isWifiCallingEnabled()) : "N/A");
                sb3.append(", imsPhone.isVideoEnabled()=");
                sb3.append(phone2 == null ? Boolean.valueOf(phone2.isVideoEnabled()) : "N/A");
                sb3.append(", imsPhone.getServiceState().getState()=");
                sb3.append(phone2 != null ? Integer.valueOf(phone2.getServiceState().getState()) : "N/A");
                logi(sb3.toString());
                if (isEmergencyNumber) {
                    str3 = str2;
                    if (FdnUtils.isNumberBlockedByFDN(this.mPhoneId, str3, getCountryIso())) {
                        throw new CallStateException(8, "cannot dial number blocked by FDN");
                    }
                } else {
                    str3 = str2;
                }
                if (!isEmergencyNumber) {
                    Phone.checkWfcWifiOnlyModeBeforeDial(this.mImsPhone, this.mPhoneId, this.mContext);
                }
                if (phone2 != null && !z16 && !z14 && z9 && (phone2.getCallTracker() instanceof ImsPhoneCallTracker)) {
                    logi("WPS call placed over CS; disconnecting all IMS calls..");
                    ((ImsPhoneCallTracker) phone2.getCallTracker()).hangupAllConnections();
                }
                if ((z14 || (z11 && !z12)) && !((z11 && z4) || z3)) {
                    consumer2 = consumer;
                    imsDialArgs2 = imsDialArgs;
                } else {
                    try {
                        logd("Trying IMS PS call");
                        consumer2 = consumer;
                        try {
                            consumer2.accept(phone2);
                            imsDialArgs2 = imsDialArgs;
                            try {
                                return phone2.dial(str3, imsDialArgs2);
                            } catch (CallStateException e) {
                                e = e;
                                logd("IMS PS call exception " + e + "useImsForCall =" + z14 + ", imsPhone =" + phone2);
                                if (!Phone.CS_FALLBACK.equals(e.getMessage()) || isEmergencyNumber) {
                                    logi("IMS call failed with Exception: " + e.getMessage() + ". Falling back to CS.");
                                    serviceStateTracker = this.mSST;
                                    if (serviceStateTracker == null) {
                                    }
                                    serviceStateTracker2 = this.mSST;
                                    if (serviceStateTracker2 == null) {
                                    }
                                    serviceStateTracker3 = this.mSST;
                                    if (serviceStateTracker3 == null) {
                                    }
                                    logd("Trying (non-IMS) CS call");
                                    if (z) {
                                    }
                                    consumer2.accept(this);
                                    return dialInternal(str3, imsDialArgs2);
                                }
                                CallStateException callStateException = new CallStateException(e.getError(), e.getMessage());
                                callStateException.setStackTrace(e.getStackTrace());
                                throw callStateException;
                            }
                        } catch (CallStateException e2) {
                            e = e2;
                            imsDialArgs2 = imsDialArgs;
                            logd("IMS PS call exception " + e + "useImsForCall =" + z14 + ", imsPhone =" + phone2);
                            if (!Phone.CS_FALLBACK.equals(e.getMessage())) {
                            }
                            logi("IMS call failed with Exception: " + e.getMessage() + ". Falling back to CS.");
                            serviceStateTracker = this.mSST;
                            if (serviceStateTracker == null) {
                            }
                            serviceStateTracker2 = this.mSST;
                            if (serviceStateTracker2 == null) {
                            }
                            serviceStateTracker3 = this.mSST;
                            if (serviceStateTracker3 == null) {
                            }
                            logd("Trying (non-IMS) CS call");
                            if (z) {
                            }
                            consumer2.accept(this);
                            return dialInternal(str3, imsDialArgs2);
                        }
                    } catch (CallStateException e3) {
                        e = e3;
                        consumer2 = consumer;
                    }
                }
                serviceStateTracker = this.mSST;
                if (serviceStateTracker == null && serviceStateTracker.mSS.getState() == 1 && this.mSST.mSS.getDataRegistrationState() != 0 && !isEmergencyNumber) {
                    throw new CallStateException("cannot dial in current state");
                }
                serviceStateTracker2 = this.mSST;
                if (serviceStateTracker2 == null && serviceStateTracker2.mSS.getState() == 3 && !VideoProfile.isVideo(imsDialArgs2.videoState) && !isEmergencyNumber && ((!z11 || !z4) && !z12)) {
                    throw new CallStateException(2, "cannot dial voice call in airplane mode");
                }
                serviceStateTracker3 = this.mSST;
                if (serviceStateTracker3 == null && serviceStateTracker3.mSS.getState() == 1 && ((this.mSST.mSS.getDataRegistrationState() != 0 || !ServiceState.isPsOnlyTech(this.mSST.mSS.getRilDataRadioTechnology())) && !VideoProfile.isVideo(imsDialArgs2.videoState) && !isEmergencyNumber && !z12)) {
                    throw new CallStateException(1, "cannot dial voice call in out of service");
                }
                logd("Trying (non-IMS) CS call");
                if (z && isEmergencyNumber && !DomainSelectionResolver.getInstance().isDomainSelectionSupported()) {
                    this.mIsTestingEmergencyCallbackMode = true;
                    this.mCi.testingEmergencyCall();
                }
                consumer2.accept(this);
                return dialInternal(str3, imsDialArgs2);
            }
            imsDialArgs = build;
            str2 = checkForTestEmergencyNumber;
        } else {
            imsDialArgs = build;
            str2 = checkForTestEmergencyNumber;
            z = z6;
        }
        z2 = z7;
        phone = phone3;
        if (bundle == null) {
        }
        z3 = z15;
        z4 = z13;
        StringBuilder sb32 = new StringBuilder();
        sb32.append("useImsForCall=");
        sb32.append(z14);
        sb32.append(", useOnlyDialedSimEccList=");
        sb32.append(z8);
        sb32.append(", isEmergency=");
        sb32.append(isEmergencyNumber);
        sb32.append(", useImsForEmergency=");
        sb32.append(z3);
        sb32.append(", useImsForUt=");
        sb32.append(z4);
        sb32.append(", isUt=");
        sb32.append(z11);
        sb32.append(", isSuppServiceCode=");
        sb32.append(isSuppServiceCodes);
        sb32.append(", isPotentialUssdCode=");
        sb32.append(z12);
        sb32.append(", isWpsCall=");
        sb32.append(z9);
        sb32.append(", allowWpsOverIms=");
        boolean z162 = z2;
        sb32.append(z162);
        sb32.append(", imsPhone=");
        phone2 = phone;
        sb32.append(phone2);
        sb32.append(", imsPhone.isVoiceOverCellularImsEnabled()=");
        sb32.append(phone2 == null ? Boolean.valueOf(phone2.isVoiceOverCellularImsEnabled()) : "N/A");
        sb32.append(", imsPhone.isVowifiEnabled()=");
        sb32.append(phone2 == null ? Boolean.valueOf(phone2.isWifiCallingEnabled()) : "N/A");
        sb32.append(", imsPhone.isVideoEnabled()=");
        sb32.append(phone2 == null ? Boolean.valueOf(phone2.isVideoEnabled()) : "N/A");
        sb32.append(", imsPhone.getServiceState().getState()=");
        sb32.append(phone2 != null ? Integer.valueOf(phone2.getServiceState().getState()) : "N/A");
        logi(sb32.toString());
        if (isEmergencyNumber) {
        }
        if (!isEmergencyNumber) {
        }
        if (phone2 != null) {
            logi("WPS call placed over CS; disconnecting all IMS calls..");
            ((ImsPhoneCallTracker) phone2.getCallTracker()).hangupAllConnections();
        }
        if (z14) {
        }
        consumer2 = consumer;
        imsDialArgs2 = imsDialArgs;
        serviceStateTracker = this.mSST;
        if (serviceStateTracker == null) {
        }
        serviceStateTracker2 = this.mSST;
        if (serviceStateTracker2 == null) {
        }
        serviceStateTracker3 = this.mSST;
        if (serviceStateTracker3 == null) {
        }
        logd("Trying (non-IMS) CS call");
        if (z) {
            this.mIsTestingEmergencyCallbackMode = true;
            this.mCi.testingEmergencyCall();
        }
        consumer2.accept(this);
        return dialInternal(str3, imsDialArgs2);
    }

    public boolean isNotificationOfWfcCallRequired(String str) {
        PersistableBundle configForSubId = ((CarrierConfigManager) this.mContext.getSystemService("carrier_config")).getConfigForSubId(getSubId());
        if (configForSubId != null && configForSubId.getBoolean("notify_international_call_on_wfc_bool")) {
            Phone phone = this.mImsPhone;
            return isImsUseEnabled() && phone != null && !phone.isVoiceOverCellularImsEnabled() && phone.isWifiCallingEnabled() && !((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).isEmergencyNumber(str) && PhoneNumberUtils.isInternationalNumber(str, getCountryIso());
        }
        return false;
    }

    @Override // com.android.internal.telephony.Phone
    protected Connection dialInternal(String str, PhoneInternalInterface.DialArgs dialArgs) throws CallStateException {
        return dialInternal(str, dialArgs, null);
    }

    protected Connection dialInternal(String str, PhoneInternalInterface.DialArgs dialArgs, ResultReceiver resultReceiver) throws CallStateException {
        String stripSeparators = PhoneNumberUtils.stripSeparators(str);
        if (isPhoneTypeGsm()) {
            if (handleInCallMmiCommands(stripSeparators)) {
                return null;
            }
            GsmMmiCode newFromDialString = GsmMmiCode.newFromDialString(PhoneNumberUtils.extractNetworkPortionAlt(stripSeparators), this, this.mUiccApplication.get(), resultReceiver);
            logd("dialInternal: dialing w/ mmi '" + newFromDialString + "'...");
            if (newFromDialString == null) {
                return this.mCT.dialGsm(stripSeparators, dialArgs);
            }
            if (newFromDialString.isTemporaryModeCLIR()) {
                return this.mCT.dialGsm(newFromDialString.mDialingNumber, newFromDialString.getCLIRMode(), dialArgs.uusInfo, dialArgs.intentExtras);
            }
            this.mPendingMMIs.add(newFromDialString);
            this.mMmiRegistrants.notifyRegistrants(new AsyncResult((Object) null, newFromDialString, (Throwable) null));
            newFromDialString.processCode();
            return null;
        }
        return this.mCT.dial(stripSeparators, dialArgs);
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public boolean handlePinMmi(String str) {
        MmiCode newFromDialString;
        if (isPhoneTypeGsm()) {
            newFromDialString = GsmMmiCode.newFromDialString(str, this, this.mUiccApplication.get());
        } else {
            newFromDialString = CdmaMmiCode.newFromDialString(str, this, this.mUiccApplication.get());
        }
        if (newFromDialString != null && newFromDialString.isPinPukCommand()) {
            this.mPendingMMIs.add(newFromDialString);
            this.mMmiRegistrants.notifyRegistrants(new AsyncResult((Object) null, newFromDialString, (Throwable) null));
            try {
                newFromDialString.processCode();
                return true;
            } catch (CallStateException unused) {
                return true;
            }
        }
        loge("Mmi is null or unrecognized!");
        return false;
    }

    private void sendUssdResponse(String str, CharSequence charSequence, int i, ResultReceiver resultReceiver) {
        UssdResponse ussdResponse = new UssdResponse(str, charSequence);
        Bundle bundle = new Bundle();
        bundle.putParcelable("USSD_RESPONSE", ussdResponse);
        resultReceiver.send(i, bundle);
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public boolean handleUssdRequest(String str, ResultReceiver resultReceiver) {
        if (!isPhoneTypeGsm() || this.mPendingMMIs.size() > 0) {
            sendUssdResponse(str, null, -1, resultReceiver);
            return true;
        } else if (FdnUtils.isNumberBlockedByFDN(this.mPhoneId, str, getCountryIso())) {
            sendUssdResponse(str, null, -1, resultReceiver);
            return true;
        } else {
            Phone phone = this.mImsPhone;
            if (phone != null && (phone.getServiceState().getState() == 0 || phone.isUtEnabled())) {
                try {
                    logd("handleUssdRequest: attempting over IMS");
                    return phone.handleUssdRequest(str, resultReceiver);
                } catch (CallStateException e) {
                    if (!Phone.CS_FALLBACK.equals(e.getMessage())) {
                        return false;
                    }
                    this.logd("handleUssdRequest: fallback to CS required");
                }
            }
            try {
                this.dialInternal(str, new PhoneInternalInterface.DialArgs.Builder().build(), resultReceiver);
                return true;
            } catch (Exception e2) {
                this.logd("handleUssdRequest: exception" + e2);
                return false;
            }
        }
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void sendUssdResponse(String str) {
        if (isPhoneTypeGsm()) {
            GsmMmiCode newFromUssdUserInput = GsmMmiCode.newFromUssdUserInput(str, this, this.mUiccApplication.get());
            this.mPendingMMIs.add(newFromUssdUserInput);
            this.mMmiRegistrants.notifyRegistrants(new AsyncResult((Object) null, newFromUssdUserInput, (Throwable) null));
            newFromUssdUserInput.sendUssd(str);
            return;
        }
        loge("sendUssdResponse: not possible in CDMA");
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void sendDtmf(char c) {
        if (!PhoneNumberUtils.is12Key(c)) {
            loge("sendDtmf called with invalid character '" + c + "'");
        } else if (this.mCT.mState == PhoneConstants.State.OFFHOOK) {
            this.mCi.sendDtmf(c, null);
        }
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void startDtmf(char c) {
        if (!PhoneNumberUtils.is12Key(c)) {
            loge("startDtmf called with invalid character '" + c + "'");
            return;
        }
        this.mCi.startDtmf(c, null);
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void stopDtmf() {
        this.mCi.stopDtmf(null);
    }

    @Override // com.android.internal.telephony.Phone
    public void sendBurstDtmf(String str, int i, int i2, Message message) {
        if (isPhoneTypeGsm()) {
            loge("[GsmCdmaPhone] sendBurstDtmf() is a CDMA method");
            return;
        }
        boolean z = false;
        int i3 = 0;
        while (true) {
            if (i3 >= str.length()) {
                z = true;
                break;
            } else if (!PhoneNumberUtils.is12Key(str.charAt(i3))) {
                Rlog.e(LOG_TAG, "sendDtmf called with invalid character '" + str.charAt(i3) + "'");
                break;
            } else {
                i3++;
            }
        }
        if (this.mCT.mState == PhoneConstants.State.OFFHOOK && z) {
            this.mCi.sendBurstDtmf(str, i, i2, message);
        }
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void setRadioPowerOnForTestEmergencyCall(boolean z) {
        this.mSST.clearAllRadioOffReasons();
        setRadioPower(true, false, z, false);
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void setRadioPower(boolean z, boolean z2, boolean z3, boolean z4) {
        setRadioPowerForReason(z, z2, z3, z4, 0);
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void setRadioPowerForReason(boolean z, boolean z2, boolean z3, boolean z4, int i) {
        this.mSST.setRadioPowerForReason(z, z2, z3, z4, i);
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public Set<Integer> getRadioPowerOffReasons() {
        return this.mSST.getRadioPowerOffReasons();
    }

    private void storeVoiceMailNumber(String str) {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        setVmSimImsi(getSubscriberId());
        logd("storeVoiceMailNumber: mPrecisePhoneType=" + this.mPrecisePhoneType + " vmNumber=" + Rlog.pii(LOG_TAG, str));
        if (isPhoneTypeGsm()) {
            edit.putString("vm_number_key" + getPhoneId(), str);
            edit.apply();
            return;
        }
        edit.putString("vm_number_key_cdma" + getPhoneId(), str);
        edit.apply();
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public String getVoiceMailNumber() {
        String voiceMailNumber;
        PersistableBundle configForSubId;
        PersistableBundle configForSubId2;
        if (isPhoneTypeGsm() || this.mSimRecords != null) {
            IccRecords iccRecords = isPhoneTypeGsm() ? this.mIccRecords.get() : this.mSimRecords;
            voiceMailNumber = iccRecords != null ? iccRecords.getVoiceMailNumber() : PhoneConfigurationManager.SSSS;
            if (TextUtils.isEmpty(voiceMailNumber)) {
                SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                String str = isPhoneTypeGsm() ? "vm_number_key" : "vm_number_key_cdma";
                voiceMailNumber = defaultSharedPreferences.getString(str + getPhoneId(), null);
                logd("getVoiceMailNumber: from " + str + " number=" + Rlog.pii(LOG_TAG, voiceMailNumber));
            } else {
                logd("getVoiceMailNumber: from IccRecords number=" + Rlog.pii(LOG_TAG, voiceMailNumber));
            }
        } else {
            voiceMailNumber = null;
        }
        if (!isPhoneTypeGsm() && TextUtils.isEmpty(voiceMailNumber)) {
            voiceMailNumber = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("vm_number_key_cdma" + getPhoneId(), null);
            logd("getVoiceMailNumber: from VM_NUMBER_CDMA number=" + voiceMailNumber);
        }
        if (TextUtils.isEmpty(voiceMailNumber) && (configForSubId2 = ((CarrierConfigManager) getContext().getSystemService("carrier_config")).getConfigForSubId(getSubId())) != null) {
            String string = configForSubId2.getString("default_vm_number_string");
            String string2 = configForSubId2.getString("default_vm_number_roaming_string");
            String string3 = configForSubId2.getString("default_vm_number_roaming_and_ims_unregistered_string");
            if (!TextUtils.isEmpty(string)) {
                voiceMailNumber = string;
            }
            if (this.mSST.mSS.getRoaming()) {
                if (!TextUtils.isEmpty(string3) && !this.mSST.isImsRegistered()) {
                    voiceMailNumber = string3;
                } else if (!TextUtils.isEmpty(string2)) {
                    voiceMailNumber = string2;
                }
            }
        }
        return (TextUtils.isEmpty(voiceMailNumber) && (configForSubId = ((CarrierConfigManager) getContext().getSystemService("carrier_config")).getConfigForSubId(getSubId())) != null && configForSubId.getBoolean("config_telephony_use_own_number_for_voicemail_bool")) ? getLine1Number() : voiceMailNumber;
    }

    private String getVmSimImsi() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        return defaultSharedPreferences.getString("vm_sim_imsi_key" + getPhoneId(), null);
    }

    private void setVmSimImsi(String str) {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        edit.putString("vm_sim_imsi_key" + getPhoneId(), str);
        edit.apply();
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public String getVoiceMailAlphaTag() {
        boolean isPhoneTypeGsm = isPhoneTypeGsm();
        String str = PhoneConfigurationManager.SSSS;
        if (isPhoneTypeGsm || this.mSimRecords != null) {
            IccRecords iccRecords = isPhoneTypeGsm() ? this.mIccRecords.get() : this.mSimRecords;
            if (iccRecords != null) {
                str = iccRecords.getVoiceMailAlphaTag();
            }
        }
        return (str == null || str.length() == 0) ? this.mContext.getText(17039364).toString() : str;
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public String getDeviceId() {
        if (isPhoneTypeGsm()) {
            return this.mImei;
        }
        if (((CarrierConfigManager) this.mContext.getSystemService("carrier_config")).getConfigForSubId(getSubId()).getBoolean("force_imei_bool")) {
            return this.mImei;
        }
        String meid = getMeid();
        if (meid == null || meid.matches("^0*$")) {
            loge("getDeviceId(): MEID is not initialized use ESN");
            return getEsn();
        }
        return meid;
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public String getDeviceSvn() {
        if (isPhoneTypeGsm() || isPhoneTypeCdmaLte()) {
            return this.mImeiSv;
        }
        loge("getDeviceSvn(): return 0");
        return "0";
    }

    @Override // com.android.internal.telephony.Phone
    public IsimRecords getIsimRecords() {
        return this.mIsimUiccRecords;
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public String getImei() {
        return this.mImei;
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public int getImeiType() {
        return this.mImeiType;
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public String getEsn() {
        if (isPhoneTypeGsm()) {
            loge("[GsmCdmaPhone] getEsn() is a CDMA method");
            return "0";
        }
        return this.mEsn;
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public String getMeid() {
        return this.mMeid;
    }

    @Override // com.android.internal.telephony.Phone
    public String getNai() {
        IccRecords iccRecords = this.mUiccController.getIccRecords(this.mPhoneId, 2);
        if (Log.isLoggable(LOG_TAG, 2)) {
            Rlog.v(LOG_TAG, "IccRecords is " + iccRecords);
        }
        if (iccRecords != null) {
            return iccRecords.getNAI();
        }
        return null;
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public String getSubscriberId() {
        if (isPhoneTypeCdma()) {
            return this.mSST.getImsi();
        }
        IccRecords iccRecords = this.mUiccController.getIccRecords(this.mPhoneId, 1);
        if (iccRecords != null) {
            return iccRecords.getIMSI();
        }
        return null;
    }

    @Override // com.android.internal.telephony.Phone, com.android.internal.telephony.PhoneInternalInterface
    public ImsiEncryptionInfo getCarrierInfoForImsiEncryption(int i, boolean z) {
        TelephonyManager createForSubscriptionId = ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).createForSubscriptionId(getSubId());
        return CarrierInfoManager.getCarrierInfoForImsiEncryption(i, this.mContext, createForSubscriptionId.getSimOperator(), createForSubscriptionId.getSimCarrierId(), z, getSubId());
    }

    @Override // com.android.internal.telephony.Phone, com.android.internal.telephony.PhoneInternalInterface
    public void setCarrierInfoForImsiEncryption(ImsiEncryptionInfo imsiEncryptionInfo) {
        CarrierInfoManager.setCarrierInfoForImsiEncryption(imsiEncryptionInfo, this.mContext, this.mPhoneId);
        this.mCi.setCarrierInfoForImsiEncryption(imsiEncryptionInfo, null);
    }

    @Override // com.android.internal.telephony.Phone
    public void deleteCarrierInfoForImsiEncryption(int i) {
        CarrierInfoManager.deleteCarrierInfoForImsiEncryption(this.mContext, getSubId(), i);
    }

    @Override // com.android.internal.telephony.Phone
    public int getCarrierId() {
        CarrierResolver carrierResolver = this.mCarrierResolver;
        return carrierResolver != null ? carrierResolver.getCarrierId() : super.getCarrierId();
    }

    @Override // com.android.internal.telephony.Phone
    public String getCarrierName() {
        CarrierResolver carrierResolver = this.mCarrierResolver;
        return carrierResolver != null ? carrierResolver.getCarrierName() : super.getCarrierName();
    }

    @Override // com.android.internal.telephony.Phone
    public int getMNOCarrierId() {
        CarrierResolver carrierResolver = this.mCarrierResolver;
        return carrierResolver != null ? carrierResolver.getMnoCarrierId() : super.getMNOCarrierId();
    }

    @Override // com.android.internal.telephony.Phone
    public int getSpecificCarrierId() {
        CarrierResolver carrierResolver = this.mCarrierResolver;
        return carrierResolver != null ? carrierResolver.getSpecificCarrierId() : super.getSpecificCarrierId();
    }

    @Override // com.android.internal.telephony.Phone
    public String getSpecificCarrierName() {
        CarrierResolver carrierResolver = this.mCarrierResolver;
        return carrierResolver != null ? carrierResolver.getSpecificCarrierName() : super.getSpecificCarrierName();
    }

    @Override // com.android.internal.telephony.Phone
    public void resolveSubscriptionCarrierId(String str) {
        CarrierResolver carrierResolver = this.mCarrierResolver;
        if (carrierResolver != null) {
            carrierResolver.resolveSubscriptionCarrierId(str);
        }
    }

    @Override // com.android.internal.telephony.Phone
    public int getCarrierIdListVersion() {
        CarrierResolver carrierResolver = this.mCarrierResolver;
        return carrierResolver != null ? carrierResolver.getCarrierListVersion() : super.getCarrierIdListVersion();
    }

    @Override // com.android.internal.telephony.Phone
    public int getEmergencyNumberDbVersion() {
        return getEmergencyNumberTracker().getEmergencyNumberDbVersion();
    }

    @Override // com.android.internal.telephony.Phone, com.android.internal.telephony.PhoneInternalInterface
    public void resetCarrierKeysForImsiEncryption() {
        this.mCIM.resetCarrierKeysForImsiEncryption(this.mContext, this.mPhoneId);
    }

    @Override // com.android.internal.telephony.Phone
    public void setCarrierTestOverride(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9) {
        List<UiccAccessRule> singletonList;
        this.mCarrierResolver.setTestOverrideApn(str9);
        UiccProfile uiccProfileForPhone = this.mUiccController.getUiccProfileForPhone(getPhoneId());
        IccRecords iccRecords = null;
        if (uiccProfileForPhone != null) {
            if (str8 == null) {
                singletonList = null;
            } else if (str8.isEmpty()) {
                singletonList = Collections.emptyList();
            } else {
                singletonList = Collections.singletonList(new UiccAccessRule(IccUtils.hexStringToBytes(str8), (String) null, 0L));
            }
            uiccProfileForPhone.setTestOverrideCarrierPrivilegeRules(singletonList);
        } else {
            this.mCarrierResolver.setTestOverrideCarrierPriviledgeRule(str8);
        }
        if (isPhoneTypeGsm()) {
            iccRecords = this.mIccRecords.get();
        } else if (isPhoneTypeCdmaLte()) {
            iccRecords = this.mSimRecords;
        } else {
            loge("setCarrierTestOverride fails in CDMA only");
        }
        IccRecords iccRecords2 = iccRecords;
        if (iccRecords2 != null) {
            iccRecords2.setCarrierTestOverride(str, str2, str3, str4, str5, str6, str7);
        }
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public String getGroupIdLevel1() {
        if (isPhoneTypeGsm()) {
            IccRecords iccRecords = this.mIccRecords.get();
            if (iccRecords != null) {
                return iccRecords.getGid1();
            }
            return null;
        } else if (isPhoneTypeCdma()) {
            loge("GID1 is not available in CDMA");
            return null;
        } else {
            SIMRecords sIMRecords = this.mSimRecords;
            return sIMRecords != null ? sIMRecords.getGid1() : PhoneConfigurationManager.SSSS;
        }
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public String getGroupIdLevel2() {
        if (isPhoneTypeGsm()) {
            IccRecords iccRecords = this.mIccRecords.get();
            if (iccRecords != null) {
                return iccRecords.getGid2();
            }
            return null;
        } else if (isPhoneTypeCdma()) {
            loge("GID2 is not available in CDMA");
            return null;
        } else {
            SIMRecords sIMRecords = this.mSimRecords;
            return sIMRecords != null ? sIMRecords.getGid2() : PhoneConfigurationManager.SSSS;
        }
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public String getLine1Number() {
        if (isPhoneTypeGsm()) {
            IccRecords iccRecords = this.mIccRecords.get();
            if (iccRecords != null) {
                return iccRecords.getMsisdnNumber();
            }
            return null;
        } else if (((CarrierConfigManager) this.mContext.getSystemService("carrier_config")).getConfigForSubId(getSubId()).getBoolean("use_usim_bool")) {
            SIMRecords sIMRecords = this.mSimRecords;
            if (sIMRecords != null) {
                return sIMRecords.getMsisdnNumber();
            }
            return null;
        } else {
            return this.mSST.getMdnNumber();
        }
    }

    @Override // com.android.internal.telephony.Phone
    public String getPlmn() {
        if (isPhoneTypeGsm()) {
            IccRecords iccRecords = this.mIccRecords.get();
            if (iccRecords != null) {
                return iccRecords.getPnnHomeName();
            }
            return null;
        } else if (isPhoneTypeCdma()) {
            loge("Plmn is not available in CDMA");
            return null;
        } else {
            SIMRecords sIMRecords = this.mSimRecords;
            if (sIMRecords != null) {
                return sIMRecords.getPnnHomeName();
            }
            return null;
        }
    }

    @Override // com.android.internal.telephony.Phone
    protected void updateManualNetworkSelection(Phone.NetworkSelectMessage networkSelectMessage) {
        int subId = getSubId();
        if (SubscriptionManager.isValidSubscriptionId(subId)) {
            this.mManualNetworkSelectionPlmn = networkSelectMessage.operatorNumeric;
            return;
        }
        this.mManualNetworkSelectionPlmn = null;
        Rlog.e(LOG_TAG, "Cannot update network selection due to invalid subId " + subId);
    }

    @Override // com.android.internal.telephony.Phone
    public String getManualNetworkSelectionPlmn() {
        String str = this.mManualNetworkSelectionPlmn;
        return str == null ? PhoneConfigurationManager.SSSS : str;
    }

    @Override // com.android.internal.telephony.Phone
    public String getCdmaPrlVersion() {
        return this.mSST.getPrlVersion();
    }

    @Override // com.android.internal.telephony.Phone
    public String getCdmaMin() {
        return this.mSST.getCdmaMin();
    }

    @Override // com.android.internal.telephony.Phone
    public boolean isMinInfoReady() {
        return this.mSST.isMinInfoReady();
    }

    @Override // com.android.internal.telephony.Phone
    public String getMsisdn() {
        if (isPhoneTypeGsm()) {
            IccRecords iccRecords = this.mIccRecords.get();
            if (iccRecords != null) {
                return iccRecords.getMsisdnNumber();
            }
            return null;
        } else if (isPhoneTypeCdmaLte()) {
            SIMRecords sIMRecords = this.mSimRecords;
            if (sIMRecords != null) {
                return sIMRecords.getMsisdnNumber();
            }
            return null;
        } else {
            loge("getMsisdn: not expected on CDMA");
            return null;
        }
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public String getLine1AlphaTag() {
        if (isPhoneTypeGsm()) {
            IccRecords iccRecords = this.mIccRecords.get();
            if (iccRecords != null) {
                return iccRecords.getMsisdnAlphaTag();
            }
            return null;
        }
        loge("getLine1AlphaTag: not possible in CDMA");
        return null;
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public boolean setLine1Number(String str, String str2, Message message) {
        if (isPhoneTypeGsm()) {
            IccRecords iccRecords = this.mIccRecords.get();
            if (iccRecords != null) {
                iccRecords.setMsisdnNumber(str, str2, message);
                return true;
            }
            return false;
        }
        loge("setLine1Number: not possible in CDMA");
        return false;
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void setVoiceMailNumber(String str, String str2, Message message) {
        SIMRecords sIMRecords;
        this.mVmNumber = str2;
        Message obtainMessage = obtainMessage(20, 0, 0, message);
        IccRecords iccRecords = this.mIccRecords.get();
        if (!isPhoneTypeGsm() && (sIMRecords = this.mSimRecords) != null) {
            iccRecords = sIMRecords;
        }
        if (iccRecords != null) {
            iccRecords.setVoiceMailNumber(str, this.mVmNumber, obtainMessage);
        }
    }

    @Override // com.android.internal.telephony.Phone
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public String getSystemProperty(String str, String str2) {
        if (getUnitTestMode()) {
            return null;
        }
        return TelephonyManager.getTelephonyProperty(this.mPhoneId, str, str2);
    }

    private boolean isCsRetry(Message message) {
        if (message != null) {
            return message.getData().getBoolean(Phone.CS_FALLBACK_SS, false);
        }
        return false;
    }

    private void updateSsOverCdmaSupported(PersistableBundle persistableBundle) {
        if (persistableBundle == null) {
            return;
        }
        this.mSsOverCdmaSupported = persistableBundle.getBoolean("support_ss_over_cdma_bool");
    }

    @Override // com.android.internal.telephony.Phone
    public boolean useSsOverIms(Message message) {
        boolean isUtEnabled = isUtEnabled();
        Rlog.d(LOG_TAG, "useSsOverIms: isUtEnabled()= " + isUtEnabled + " isCsRetry(onComplete))= " + isCsRetry(message));
        return isUtEnabled && !isCsRetry(message);
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void getCallForwardingOption(int i, Message message) {
        getCallForwardingOption(i, 1, message);
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void getCallForwardingOption(int i, int i2, Message message) {
        if (isRequestBlockedByFDN(SsData.RequestType.SS_INTERROGATION, GsmMmiCode.cfReasonToServiceType(i))) {
            AsyncResult.forMessage(message, (Object) null, new CommandException(CommandException.Error.FDN_CHECK_FAILURE));
            message.sendToTarget();
            return;
        }
        Phone phone = this.mImsPhone;
        if (useSsOverIms(message)) {
            phone.getCallForwardingOption(i, i2, message);
        } else if (isPhoneTypeGsm()) {
            if (isValidCommandInterfaceCFReason(i)) {
                logd("requesting call forwarding query.");
                if (i == 0) {
                    message = obtainMessage(13, message);
                }
                this.mCi.queryCallForwardStatus(i, i2, null, message);
            }
        } else {
            if (!this.mSsOverCdmaSupported) {
                AsyncResult.forMessage(message, (Object) null, new CommandException(CommandException.Error.INVALID_STATE, "Call Forwarding over CDMA unavailable"));
            } else {
                loge("getCallForwardingOption: not possible in CDMA, just return empty result");
                AsyncResult.forMessage(message, makeEmptyCallForward(), (Throwable) null);
            }
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void setCallForwardingOption(int i, int i2, String str, int i3, Message message) {
        setCallForwardingOption(i, i2, str, 1, i3, message);
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void setCallForwardingOption(int i, int i2, String str, int i3, int i4, Message message) {
        if (isRequestBlockedByFDN(GsmMmiCode.cfActionToRequestType(i), GsmMmiCode.cfReasonToServiceType(i2))) {
            AsyncResult.forMessage(message, (Object) null, new CommandException(CommandException.Error.FDN_CHECK_FAILURE));
            message.sendToTarget();
            return;
        }
        Phone phone = this.mImsPhone;
        if (useSsOverIms(message)) {
            phone.setCallForwardingOption(i, i2, str, i3, i4, message);
        } else if (isPhoneTypeGsm()) {
            if (isValidCommandInterfaceCFAction(i) && isValidCommandInterfaceCFReason(i2)) {
                if (i2 == 0) {
                    message = obtainMessage(12, isCfEnable(i) ? 1 : 0, 0, new Cfu(str, message));
                }
                this.mCi.setCallForward(i, i2, i3, str, i4, message);
            }
        } else if (this.mSsOverCdmaSupported) {
            String callForwardingPrefixAndNumber = CdmaMmiCode.getCallForwardingPrefixAndNumber(i, i2, GsmCdmaConnection.formatDialString(str));
            loge("setCallForwardingOption: dial for set call forwarding prefixWithNumber= " + callForwardingPrefixAndNumber + " number= " + str);
            PhoneAccountHandle subscriptionIdToPhoneAccountHandle = subscriptionIdToPhoneAccountHandle(getSubId());
            Bundle bundle = new Bundle();
            bundle.putParcelable("android.telecom.extra.PHONE_ACCOUNT_HANDLE", subscriptionIdToPhoneAccountHandle);
            TelecomManager.from(this.mContext).placeCall(Uri.fromParts("tel", callForwardingPrefixAndNumber, null), bundle);
            AsyncResult.forMessage(message, 255, (Throwable) null);
            message.sendToTarget();
        } else {
            loge("setCallForwardingOption: SS over CDMA not supported, can not complete");
            AsyncResult.forMessage(message, 255, (Throwable) null);
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void getCallBarring(String str, String str2, Message message, int i) {
        if (isRequestBlockedByFDN(SsData.RequestType.SS_INTERROGATION, GsmMmiCode.cbFacilityToServiceType(str))) {
            AsyncResult.forMessage(message, (Object) null, new CommandException(CommandException.Error.FDN_CHECK_FAILURE));
            message.sendToTarget();
            return;
        }
        Phone phone = this.mImsPhone;
        if (useSsOverIms(message)) {
            phone.getCallBarring(str, str2, message, i);
        } else if (isPhoneTypeGsm()) {
            this.mCi.queryFacilityLock(str, str2, i, message);
        } else {
            loge("getCallBarringOption: not possible in CDMA");
        }
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void setCallBarring(String str, boolean z, String str2, Message message, int i) {
        SsData.RequestType requestType;
        if (z) {
            requestType = SsData.RequestType.SS_ACTIVATION;
        } else {
            requestType = SsData.RequestType.SS_DEACTIVATION;
        }
        if (isRequestBlockedByFDN(requestType, GsmMmiCode.cbFacilityToServiceType(str))) {
            AsyncResult.forMessage(message, (Object) null, new CommandException(CommandException.Error.FDN_CHECK_FAILURE));
            message.sendToTarget();
            return;
        }
        Phone phone = this.mImsPhone;
        if (useSsOverIms(message)) {
            phone.setCallBarring(str, z, str2, message, i);
        } else if (isPhoneTypeGsm()) {
            this.mCi.setFacilityLock(str, z, str2, i, message);
        } else {
            loge("setCallBarringOption: not possible in CDMA");
        }
    }

    public void changeCallBarringPassword(String str, String str2, String str3, Message message) {
        if (FdnUtils.isSuppServiceRequestBlockedByFdn(this.mPhoneId, GsmMmiCode.getControlStringsForPwd(SsData.RequestType.SS_REGISTRATION, GsmMmiCode.cbFacilityToServiceType(str)), getCountryIso())) {
            AsyncResult.forMessage(message, (Object) null, new CommandException(CommandException.Error.FDN_CHECK_FAILURE));
            message.sendToTarget();
        } else if (isPhoneTypeGsm()) {
            this.mCi.changeBarringPassword(str, str2, str3, message);
        } else {
            loge("changeCallBarringPassword: not possible in CDMA");
        }
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void getOutgoingCallerIdDisplay(Message message) {
        if (isRequestBlockedByFDN(SsData.RequestType.SS_INTERROGATION, SsData.ServiceType.SS_CLIR)) {
            AsyncResult.forMessage(message, (Object) null, new CommandException(CommandException.Error.FDN_CHECK_FAILURE));
            message.sendToTarget();
            return;
        }
        Phone phone = this.mImsPhone;
        if (useSsOverIms(message)) {
            phone.getOutgoingCallerIdDisplay(message);
        } else if (isPhoneTypeGsm()) {
            this.mCi.getCLIR(message);
        } else {
            loge("getOutgoingCallerIdDisplay: not possible in CDMA");
            AsyncResult.forMessage(message, (Object) null, new CommandException(CommandException.Error.REQUEST_NOT_SUPPORTED));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void setOutgoingCallerIdDisplay(int i, Message message) {
        if (isRequestBlockedByFDN(GsmMmiCode.clirModeToRequestType(i), SsData.ServiceType.SS_CLIR)) {
            AsyncResult.forMessage(message, (Object) null, new CommandException(CommandException.Error.FDN_CHECK_FAILURE));
            message.sendToTarget();
            return;
        }
        Phone phone = this.mImsPhone;
        if (useSsOverIms(message)) {
            phone.setOutgoingCallerIdDisplay(i, message);
        } else if (isPhoneTypeGsm()) {
            this.mCi.setCLIR(i, obtainMessage(18, i, 0, message));
        } else {
            loge("setOutgoingCallerIdDisplay: not possible in CDMA");
            AsyncResult.forMessage(message, (Object) null, new CommandException(CommandException.Error.REQUEST_NOT_SUPPORTED));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.Phone
    public void queryCLIP(Message message) {
        if (isRequestBlockedByFDN(SsData.RequestType.SS_INTERROGATION, SsData.ServiceType.SS_CLIP)) {
            AsyncResult.forMessage(message, (Object) null, new CommandException(CommandException.Error.FDN_CHECK_FAILURE));
            message.sendToTarget();
            return;
        }
        Phone phone = this.mImsPhone;
        if (useSsOverIms(message)) {
            phone.queryCLIP(message);
        } else if (isPhoneTypeGsm()) {
            this.mCi.queryCLIP(message);
        } else {
            loge("queryCLIP: not possible in CDMA");
            AsyncResult.forMessage(message, (Object) null, new CommandException(CommandException.Error.REQUEST_NOT_SUPPORTED));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void getCallWaiting(Message message) {
        if (isRequestBlockedByFDN(SsData.RequestType.SS_INTERROGATION, SsData.ServiceType.SS_WAIT)) {
            AsyncResult.forMessage(message, (Object) null, new CommandException(CommandException.Error.FDN_CHECK_FAILURE));
            message.sendToTarget();
        } else if (this.mCallWaitingController.getCallWaiting(message)) {
        } else {
            Phone phone = this.mImsPhone;
            if (useSsOverIms(message)) {
                phone.getCallWaiting(message);
            } else if (isPhoneTypeGsm()) {
                this.mCi.queryCallWaiting(0, message);
            } else {
                if (!this.mSsOverCdmaSupported) {
                    AsyncResult.forMessage(message, (Object) null, new CommandException(CommandException.Error.INVALID_STATE, "Call Waiting over CDMA unavailable"));
                } else {
                    AsyncResult.forMessage(message, new int[]{255, 0}, (Throwable) null);
                }
                message.sendToTarget();
            }
        }
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void setCallWaiting(boolean z, Message message) {
        PersistableBundle configForSubId = ((CarrierConfigManager) getContext().getSystemService("carrier_config")).getConfigForSubId(getSubId());
        setCallWaiting(z, configForSubId != null ? configForSubId.getInt("call_waiting_service_class_int", 1) : 1, message);
    }

    @Override // com.android.internal.telephony.Phone
    public void setCallWaiting(boolean z, int i, Message message) {
        SsData.RequestType requestType;
        if (z) {
            requestType = SsData.RequestType.SS_ACTIVATION;
        } else {
            requestType = SsData.RequestType.SS_DEACTIVATION;
        }
        if (isRequestBlockedByFDN(requestType, SsData.ServiceType.SS_WAIT)) {
            AsyncResult.forMessage(message, (Object) null, new CommandException(CommandException.Error.FDN_CHECK_FAILURE));
            message.sendToTarget();
        } else if (this.mCallWaitingController.setCallWaiting(z, i, message)) {
        } else {
            Phone phone = this.mImsPhone;
            if (useSsOverIms(message)) {
                phone.setCallWaiting(z, message);
            } else if (isPhoneTypeGsm()) {
                this.mCi.setCallWaiting(z, i, message);
            } else if (this.mSsOverCdmaSupported) {
                String callWaitingPrefix = CdmaMmiCode.getCallWaitingPrefix(z);
                Rlog.i(LOG_TAG, "setCallWaiting in CDMA : dial for set call waiting prefix= " + callWaitingPrefix);
                PhoneAccountHandle subscriptionIdToPhoneAccountHandle = subscriptionIdToPhoneAccountHandle(getSubId());
                Bundle bundle = new Bundle();
                bundle.putParcelable("android.telecom.extra.PHONE_ACCOUNT_HANDLE", subscriptionIdToPhoneAccountHandle);
                TelecomManager.from(this.mContext).placeCall(Uri.fromParts("tel", callWaitingPrefix, null), bundle);
                AsyncResult.forMessage(message, 255, (Throwable) null);
                message.sendToTarget();
            } else {
                loge("setCallWaiting: SS over CDMA not supported, can not complete");
                AsyncResult.forMessage(message, 255, (Throwable) null);
                message.sendToTarget();
            }
        }
    }

    @Override // com.android.internal.telephony.Phone
    public int getTerminalBasedCallWaitingState(boolean z) {
        return this.mCallWaitingController.getTerminalBasedCallWaitingState(z);
    }

    @Override // com.android.internal.telephony.Phone
    public void setTerminalBasedCallWaitingStatus(int i) {
        Phone phone = this.mImsPhone;
        if (phone != null) {
            phone.setTerminalBasedCallWaitingStatus(i);
        }
    }

    @Override // com.android.internal.telephony.Phone
    public void setTerminalBasedCallWaitingSupported(boolean z) {
        this.mCallWaitingController.setTerminalBasedCallWaitingSupported(z);
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void getAvailableNetworks(Message message) {
        if (isPhoneTypeGsm() || isPhoneTypeCdmaLte()) {
            this.mCi.getAvailableNetworks(obtainMessage(51, message));
            return;
        }
        loge("getAvailableNetworks: not possible in CDMA");
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void startNetworkScan(NetworkScanRequest networkScanRequest, Message message) {
        this.mCi.startNetworkScan(networkScanRequest, message);
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void stopNetworkScan(Message message) {
        this.mCi.stopNetworkScan(message);
    }

    @Override // com.android.internal.telephony.Phone
    public void setTTYMode(int i, Message message) {
        super.setTTYMode(i, message);
        Phone phone = this.mImsPhone;
        if (phone != null) {
            phone.setTTYMode(i, message);
        }
    }

    @Override // com.android.internal.telephony.Phone
    public void setUiTTYMode(int i, Message message) {
        Phone phone = this.mImsPhone;
        if (phone != null) {
            phone.setUiTTYMode(i, message);
        }
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void setMute(boolean z) {
        this.mCT.setMute(z);
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public boolean getMute() {
        return this.mCT.getMute();
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void updateServiceLocation(WorkSource workSource) {
        this.mSST.enableSingleLocationUpdate(workSource);
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void enableLocationUpdates() {
        this.mSST.enableLocationUpdates();
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void disableLocationUpdates() {
        this.mSST.disableLocationUpdates();
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public boolean getDataRoamingEnabled() {
        return getDataSettingsManager().isDataRoamingEnabled();
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void setDataRoamingEnabled(boolean z) {
        getDataSettingsManager().setDataRoamingEnabled(z);
    }

    @Override // com.android.internal.telephony.Phone
    public void registerForCdmaOtaStatusChange(Handler handler, int i, Object obj) {
        this.mCi.registerForCdmaOtaProvision(handler, i, obj);
    }

    @Override // com.android.internal.telephony.Phone
    public void unregisterForCdmaOtaStatusChange(Handler handler) {
        this.mCi.unregisterForCdmaOtaProvision(handler);
    }

    @Override // com.android.internal.telephony.Phone
    public void registerForSubscriptionInfoReady(Handler handler, int i, Object obj) {
        this.mSST.registerForSubscriptionInfoReady(handler, i, obj);
    }

    @Override // com.android.internal.telephony.Phone
    public void unregisterForSubscriptionInfoReady(Handler handler) {
        this.mSST.unregisterForSubscriptionInfoReady(handler);
    }

    @Override // com.android.internal.telephony.Phone
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void setOnEcbModeExitResponse(Handler handler, int i, Object obj) {
        this.mEcmExitRespRegistrant = new Registrant(handler, i, obj);
    }

    @Override // com.android.internal.telephony.Phone
    public void unsetOnEcbModeExitResponse(Handler handler) {
        this.mEcmExitRespRegistrant.clear();
    }

    @Override // com.android.internal.telephony.Phone
    public void registerForCallWaiting(Handler handler, int i, Object obj) {
        this.mCT.registerForCallWaiting(handler, i, obj);
    }

    @Override // com.android.internal.telephony.Phone
    public void unregisterForCallWaiting(Handler handler) {
        this.mCT.unregisterForCallWaiting(handler);
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public boolean isUserDataEnabled() {
        return getDataSettingsManager().isDataEnabledForReason(0);
    }

    public void onMMIDone(MmiCode mmiCode) {
        if (this.mPendingMMIs.remove(mmiCode) || (isPhoneTypeGsm() && (mmiCode.isUssdRequest() || ((GsmMmiCode) mmiCode).isSsInfo()))) {
            ResultReceiver ussdCallbackReceiver = mmiCode.getUssdCallbackReceiver();
            if (ussdCallbackReceiver != null) {
                Rlog.i(LOG_TAG, "onMMIDone: invoking callback: " + mmiCode);
                sendUssdResponse(mmiCode.getDialString(), mmiCode.getMessage(), mmiCode.getState() == MmiCode.State.COMPLETE ? 100 : -1, ussdCallbackReceiver);
                return;
            }
            Rlog.i(LOG_TAG, "onMMIDone: notifying registrants: " + mmiCode);
            this.mMmiCompleteRegistrants.notifyRegistrants(new AsyncResult((Object) null, mmiCode, (Throwable) null));
            return;
        }
        Rlog.i(LOG_TAG, "onMMIDone: invalid response or already handled; ignoring: " + mmiCode);
    }

    public boolean supports3gppCallForwardingWhileRoaming() {
        PersistableBundle configForSubId = ((CarrierConfigManager) getContext().getSystemService("carrier_config")).getConfigForSubId(getSubId());
        if (configForSubId != null) {
            return configForSubId.getBoolean("support_3gpp_call_forwarding_while_roaming_bool", true);
        }
        return true;
    }

    private void onNetworkInitiatedUssd(MmiCode mmiCode) {
        Rlog.v(LOG_TAG, "onNetworkInitiatedUssd: mmi=" + mmiCode);
        this.mMmiCompleteRegistrants.notifyRegistrants(new AsyncResult((Object) null, mmiCode, (Throwable) null));
    }

    private void onIncomingUSSD(int i, String str) {
        GsmMmiCode gsmMmiCode;
        if (!isPhoneTypeGsm()) {
            loge("onIncomingUSSD: not expected on GSM");
        }
        int i2 = 0;
        boolean z = i == 1;
        boolean z2 = (i == 0 || i == 1) ? false : true;
        boolean z3 = i == 2;
        int size = this.mPendingMMIs.size();
        while (true) {
            if (i2 >= size) {
                gsmMmiCode = null;
                break;
            } else if (((GsmMmiCode) this.mPendingMMIs.get(i2)).isPendingUSSD()) {
                gsmMmiCode = (GsmMmiCode) this.mPendingMMIs.get(i2);
                break;
            } else {
                i2++;
            }
        }
        if (gsmMmiCode != null) {
            if (z3) {
                gsmMmiCode.onUssdRelease();
            } else if (z2) {
                gsmMmiCode.onUssdFinishedError();
            } else {
                gsmMmiCode.onUssdFinished(str, z);
            }
        } else if (!z2 && !TextUtils.isEmpty(str)) {
            onNetworkInitiatedUssd(GsmMmiCode.newNetworkInitiatedUssd(str, z, this, this.mUiccApplication.get()));
        } else if (!z2 || z3) {
        } else {
            GsmMmiCode.newNetworkInitiatedUssd(str, true, this, this.mUiccApplication.get()).onUssdFinishedError();
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private void syncClirSetting() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        migrateClirSettingIfNeeded(defaultSharedPreferences);
        int i = defaultSharedPreferences.getInt(Phone.CLIR_KEY + getSubId(), -1);
        Rlog.i(LOG_TAG, "syncClirSetting: clir_sub_key" + getSubId() + "=" + i);
        if (i >= 0) {
            this.mCi.setCLIR(i, null);
        } else {
            this.mCi.setCLIR(0, null);
        }
    }

    private void migrateClirSettingIfNeeded(SharedPreferences sharedPreferences) {
        int i = sharedPreferences.getInt("clir_key" + getPhoneId(), -1);
        if (i >= 0) {
            Rlog.i(LOG_TAG, "Migrate CLIR setting: value=" + i + ", clir_key" + getPhoneId() + " -> " + Phone.CLIR_KEY + getSubId());
            SharedPreferences.Editor edit = sharedPreferences.edit();
            StringBuilder sb = new StringBuilder();
            sb.append(Phone.CLIR_KEY);
            sb.append(getSubId());
            edit.putInt(sb.toString(), i);
            edit.remove("clir_key" + getPhoneId()).commit();
        }
    }

    private void handleRadioAvailable() {
        this.mCi.getBasebandVersion(obtainMessage(6));
        this.mCi.getImei(obtainMessage(67));
        this.mCi.getDeviceIdentity(obtainMessage(21));
        this.mCi.getRadioCapability(obtainMessage(35));
        this.mCi.areUiccApplicationsEnabled(obtainMessage(55));
        handleNullCipherEnabledChange();
        startLceAfterRadioIsAvailable();
    }

    private void handleRadioOn() {
        this.mCi.getVoiceRadioTechnology(obtainMessage(40));
        if (isPhoneTypeGsm()) {
            return;
        }
        this.mCdmaSubscriptionSource = this.mCdmaSSM.getCdmaSubscriptionSource();
    }

    private void handleRadioOffOrNotAvailable() {
        if (isPhoneTypeGsm()) {
            for (int size = this.mPendingMMIs.size() - 1; size >= 0; size--) {
                if (((GsmMmiCode) this.mPendingMMIs.get(size)).isPendingUSSD()) {
                    ((GsmMmiCode) this.mPendingMMIs.get(size)).onUssdFinishedError();
                }
            }
        }
        this.mRadioOffOrNotAvailableRegistrants.notifyRegistrants();
    }

    private void handleRadioPowerStateChange() {
        int radioState = this.mCi.getRadioState();
        Rlog.d(LOG_TAG, "handleRadioPowerStateChange, state= " + radioState);
        this.mNotifier.notifyRadioPowerStateChanged(this, radioState);
        TelephonyMetrics.getInstance().writeRadioState(this.mPhoneId, radioState);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.android.internal.telephony.Phone, android.os.Handler
    public void handleMessage(Message message) {
        Object obj;
        Object obj2;
        Pair pair;
        Object obj3;
        Throwable th;
        Object obj4;
        Phone phone;
        int i = message.what;
        switch (i) {
            case 1:
                handleRadioAvailable();
                return;
            case 2:
                logd("Event EVENT_SSN Received");
                if (isPhoneTypeGsm()) {
                    AsyncResult asyncResult = (AsyncResult) message.obj;
                    SuppServiceNotification suppServiceNotification = (SuppServiceNotification) asyncResult.result;
                    this.mSsnRegistrants.notifyRegistrants(asyncResult);
                    return;
                }
                return;
            case 3:
                updateCurrentCarrierInProvider();
                String vmSimImsi = getVmSimImsi();
                String subscriberId = getSubscriberId();
                if ((!isPhoneTypeGsm() || vmSimImsi != null) && subscriberId != null && !subscriberId.equals(vmSimImsi)) {
                    storeVoiceMailNumber(null);
                    setVmSimImsi(null);
                }
                updateVoiceMail();
                this.mSimRecordsLoadedRegistrants.notifyRegistrants();
                return;
            case 4:
            case 9:
            case 10:
            case 11:
            case 14:
            case 15:
            case 16:
            case 17:
            case 23:
            case 24:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 37:
            case 38:
            case 49:
            case 50:
            case 52:
            case 63:
            case 64:
            default:
                super.handleMessage(message);
                return;
            case 5:
                logd("Event EVENT_RADIO_ON Received");
                handleRadioOn();
                return;
            case 6:
                AsyncResult asyncResult2 = (AsyncResult) message.obj;
                if (asyncResult2.exception != null) {
                    return;
                }
                logd("Baseband version: " + asyncResult2.result);
                String str = (String) asyncResult2.result;
                if (str != null) {
                    int length = str.length();
                    TelephonyManager from = TelephonyManager.from(this.mContext);
                    int phoneId = getPhoneId();
                    if (length > 45) {
                        str = str.substring(length - 45, length);
                    }
                    from.setBasebandVersionForPhone(phoneId, str);
                    return;
                }
                return;
            case 7:
                String[] strArr = (String[]) ((AsyncResult) message.obj).result;
                if (strArr.length > 1) {
                    try {
                        onIncomingUSSD(Integer.parseInt(strArr[0]), strArr[1]);
                        return;
                    } catch (NumberFormatException unused) {
                        Rlog.w(LOG_TAG, "error parsing USSD");
                        return;
                    }
                }
                return;
            case 8:
                logd("Event EVENT_RADIO_OFF_OR_NOT_AVAILABLE Received");
                handleRadioOffOrNotAvailable();
                return;
            case 12:
                AsyncResult asyncResult3 = (AsyncResult) message.obj;
                Cfu cfu = (Cfu) asyncResult3.userObj;
                if (asyncResult3.exception == null) {
                    setVoiceCallForwardingFlag(1, message.arg1 == 1, cfu.mSetCfNumber);
                }
                Message message2 = cfu.mOnComplete;
                if (message2 != null) {
                    AsyncResult.forMessage(message2, asyncResult3.result, asyncResult3.exception);
                    cfu.mOnComplete.sendToTarget();
                    return;
                }
                return;
            case 13:
                AsyncResult asyncResult4 = (AsyncResult) message.obj;
                if (asyncResult4.exception == null) {
                    handleCfuQueryResult((CallForwardInfo[]) asyncResult4.result);
                }
                Message message3 = (Message) asyncResult4.userObj;
                if (message3 != null) {
                    AsyncResult.forMessage(message3, asyncResult4.result, asyncResult4.exception);
                    message3.sendToTarget();
                    return;
                }
                return;
            case 18:
                AsyncResult asyncResult5 = (AsyncResult) message.obj;
                if (asyncResult5.exception == null) {
                    saveClirSetting(message.arg1);
                }
                Message message4 = (Message) asyncResult5.userObj;
                if (message4 != null) {
                    AsyncResult.forMessage(message4, asyncResult5.result, asyncResult5.exception);
                    message4.sendToTarget();
                    return;
                }
                return;
            case 19:
                logd("Event EVENT_REGISTERED_TO_NETWORK Received");
                if (isPhoneTypeGsm()) {
                    syncClirSetting();
                    return;
                }
                return;
            case 20:
                AsyncResult asyncResult6 = (AsyncResult) message.obj;
                if (((isPhoneTypeGsm() || this.mSimRecords != null) && IccVmNotSupportedException.class.isInstance(asyncResult6.exception)) || (!isPhoneTypeGsm() && this.mSimRecords == null && IccException.class.isInstance(asyncResult6.exception))) {
                    storeVoiceMailNumber(this.mVmNumber);
                    asyncResult6.exception = null;
                }
                Message message5 = (Message) asyncResult6.userObj;
                if (message5 != null) {
                    AsyncResult.forMessage(message5, asyncResult6.result, asyncResult6.exception);
                    message5.sendToTarget();
                    return;
                }
                return;
            case 21:
                AsyncResult asyncResult7 = (AsyncResult) message.obj;
                if (asyncResult7.exception != null) {
                    return;
                }
                String[] strArr2 = (String[]) asyncResult7.result;
                if (TextUtils.isEmpty(this.mImei)) {
                    this.mImei = strArr2[0];
                    this.mImeiSv = strArr2[1];
                }
                this.mEsn = strArr2[2];
                String str2 = strArr2[3];
                this.mMeid = str2;
                if (TextUtils.isEmpty(str2) || !this.mMeid.matches("^0*$")) {
                    return;
                }
                logd("EVENT_GET_DEVICE_IDENTITY_DONE: set mMeid to null");
                this.mMeid = null;
                return;
            case 22:
                logd("Event EVENT_RUIM_RECORDS_LOADED Received");
                updateCurrentCarrierInProvider();
                return;
            case 25:
                handleEnterEmergencyCallbackMode(message);
                return;
            case 26:
                handleExitEmergencyCallbackMode(message);
                return;
            case 27:
                logd("EVENT_CDMA_SUBSCRIPTION_SOURCE_CHANGED");
                this.mCdmaSubscriptionSource = this.mCdmaSSM.getCdmaSubscriptionSource();
                return;
            case 28:
                AsyncResult asyncResult8 = (AsyncResult) message.obj;
                if (this.mSST.mSS.getIsManualSelection()) {
                    setNetworkSelectionModeAutomatic((Message) asyncResult8.result);
                    logd("SET_NETWORK_SELECTION_AUTOMATIC: set to automatic");
                    return;
                }
                logd("SET_NETWORK_SELECTION_AUTOMATIC: already automatic, ignore");
                return;
            case 29:
                processIccRecordEvents(((Integer) ((AsyncResult) message.obj).result).intValue());
                return;
            case 35:
                AsyncResult asyncResult9 = (AsyncResult) message.obj;
                RadioCapability radioCapability = (RadioCapability) asyncResult9.result;
                if (asyncResult9.exception != null) {
                    Rlog.d(LOG_TAG, "get phone radio capability fail, no need to change mRadioCapability");
                } else {
                    radioCapabilityUpdated(radioCapability, false);
                }
                Rlog.d(LOG_TAG, "EVENT_GET_RADIO_CAPABILITY: phone rc: " + radioCapability);
                return;
            case 36:
                AsyncResult asyncResult10 = (AsyncResult) message.obj;
                logd("Event EVENT_SS received");
                if (isPhoneTypeGsm()) {
                    new GsmMmiCode(this, this.mUiccApplication.get()).processSsData(asyncResult10);
                    return;
                }
                return;
            case 39:
            case 40:
                String str3 = i == 39 ? "EVENT_VOICE_RADIO_TECH_CHANGED" : "EVENT_REQUEST_VOICE_RADIO_TECH_DONE";
                AsyncResult asyncResult11 = (AsyncResult) message.obj;
                if (asyncResult11.exception == null) {
                    Object obj5 = asyncResult11.result;
                    if (obj5 != null && ((int[]) obj5).length != 0) {
                        int i2 = ((int[]) obj5)[0];
                        logd(str3 + ": newVoiceTech=" + i2);
                        phoneObjectUpdater(i2);
                        return;
                    }
                    loge(str3 + ": has no tech!");
                    return;
                }
                loge(str3 + ": exception=" + asyncResult11.exception);
                return;
            case 41:
                AsyncResult asyncResult12 = (AsyncResult) message.obj;
                if (asyncResult12.exception == null && (obj = asyncResult12.result) != null) {
                    this.mRilVersion = ((Integer) obj).intValue();
                    return;
                }
                logd("Unexpected exception on EVENT_RIL_CONNECTED");
                this.mRilVersion = -1;
                return;
            case 42:
                phoneObjectUpdater(message.arg1);
                return;
            case 43:
                if (!this.mContext.getResources().getBoolean(17891842)) {
                    this.mCi.getVoiceRadioTechnology(obtainMessage(40));
                }
                PersistableBundle configForSubId = ((CarrierConfigManager) getContext().getSystemService("carrier_config")).getConfigForSubId(getSubId());
                m7xf0516ded(configForSubId);
                updateCdmaRoamingSettingsAfterCarrierConfigChanged(configForSubId);
                updateNrSettingsAfterCarrierConfigChanged(configForSubId);
                updateVoNrSettings(configForSubId);
                updateSsOverCdmaSupported(configForSubId);
                loadAllowedNetworksFromSubscriptionDatabase();
                this.mCi.getRadioCapability(obtainMessage(35));
                return;
            case 44:
                logd("cdma_roaming_mode change is done");
                return;
            case 45:
                logd("Event EVENT_MODEM_RESET Received isInEcm = " + isInEcm() + " isPhoneTypeGsm = " + isPhoneTypeGsm() + " mImsPhone = " + this.mImsPhone);
                if (isInEcm()) {
                    if (DomainSelectionResolver.getInstance().isDomainSelectionSupported()) {
                        EmergencyStateTracker.getInstance().exitEmergencyCallbackMode();
                        return;
                    } else if (isPhoneTypeGsm()) {
                        Phone phone2 = this.mImsPhone;
                        if (phone2 != null) {
                            phone2.handleExitEmergencyCallbackMode();
                            return;
                        }
                        return;
                    } else {
                        handleExitEmergencyCallbackMode(message);
                        return;
                    }
                }
                return;
            case 46:
                Pair pair2 = (Pair) ((AsyncResult) message.obj).result;
                onVoiceRegStateOrRatChanged(((Integer) pair2.first).intValue(), ((Integer) pair2.second).intValue());
                return;
            case 47:
                logd("EVENT EVENT_RADIO_STATE_CHANGED");
                handleRadioPowerStateChange();
                return;
            case 48:
                getDataSettingsManager().setDataEnabled(2, ((Boolean) ((AsyncResult) message.obj).result).booleanValue(), this.mContext.getOpPackageName());
                return;
            case 51:
                AsyncResult asyncResult13 = (AsyncResult) message.obj;
                if (asyncResult13.exception == null && (obj2 = asyncResult13.result) != null && this.mSST != null) {
                    ArrayList arrayList = new ArrayList();
                    for (OperatorInfo operatorInfo : (List) obj2) {
                        if (OperatorInfo.State.CURRENT == operatorInfo.getState()) {
                            arrayList.add(new OperatorInfo(this.mSST.filterOperatorNameByPattern(operatorInfo.getOperatorAlphaLong()), this.mSST.filterOperatorNameByPattern(operatorInfo.getOperatorAlphaShort()), operatorInfo.getOperatorNumeric(), operatorInfo.getState()));
                        } else {
                            arrayList.add(operatorInfo);
                        }
                    }
                    asyncResult13.result = arrayList;
                }
                Message message6 = (Message) asyncResult13.userObj;
                if (message6 != null) {
                    AsyncResult.forMessage(message6, asyncResult13.result, asyncResult13.exception);
                    message6.sendToTarget();
                    return;
                }
                return;
            case 53:
            case 55:
                AsyncResult asyncResult14 = (AsyncResult) message.obj;
                if (asyncResult14 != null) {
                    if (asyncResult14.exception != null) {
                        logd("Received exception on event" + message.what + " : " + asyncResult14.exception);
                        return;
                    }
                    this.mUiccApplicationsEnabled = (Boolean) asyncResult14.result;
                    break;
                } else {
                    return;
                }
            case 54:
                break;
            case 56:
                AsyncResult asyncResult15 = (AsyncResult) message.obj;
                if (asyncResult15 == null || asyncResult15.exception == null || (pair = (Pair) asyncResult15.userObj) == null) {
                    return;
                }
                boolean booleanValue = ((Boolean) pair.first).booleanValue();
                final int intValue = ((Integer) pair.second).intValue();
                CommandException.Error commandError = ((CommandException) asyncResult15.exception).getCommandError();
                loge("Error received when re-applying uicc application setting to " + booleanValue + " on phone " + this.mPhoneId + " Error code: " + commandError + " retry count left: " + intValue);
                if (intValue > 0) {
                    if (commandError == CommandException.Error.GENERIC_FAILURE || commandError == CommandException.Error.SIM_BUSY) {
                        postDelayed(new Runnable() { // from class: com.android.internal.telephony.GsmCdmaPhone$$ExternalSyntheticLambda0
                            @Override // java.lang.Runnable
                            public final void run() {
                                GsmCdmaPhone.this.lambda$handleMessage$0(intValue);
                            }
                        }, 5000L);
                        return;
                    }
                    return;
                }
                return;
            case 57:
                logd("Event RegistrationFailed Received");
                RegistrationFailedEvent registrationFailedEvent = (RegistrationFailedEvent) ((AsyncResult) message.obj).result;
                this.mNotifier.notifyRegistrationFailed(this, registrationFailedEvent.cellIdentity, registrationFailedEvent.chosenPlmn, registrationFailedEvent.domain, registrationFailedEvent.causeCode, registrationFailedEvent.additionalCauseCode);
                return;
            case 58:
                logd("Event BarringInfoChanged Received");
                this.mNotifier.notifyBarringInfoChanged(this, (BarringInfo) ((AsyncResult) message.obj).result);
                return;
            case 59:
                AsyncResult asyncResult16 = (AsyncResult) message.obj;
                if (asyncResult16.exception == null && (obj3 = asyncResult16.result) != null) {
                    updateLinkCapacityEstimate((List) obj3);
                    return;
                } else {
                    logd("Unexpected exception on EVENT_LINK_CAPACITY_CHANGED");
                    return;
                }
            case 60:
                resetCarrierKeysForImsiEncryption();
                return;
            case TelephonyProto$RilErrno.RIL_E_NETWORK_NOT_READY /* 61 */:
                logd("EVENT_SET_VONR_ENABLED_DONE is done");
                return;
            case TelephonyProto$RilErrno.RIL_E_NOT_PROVISIONED /* 62 */:
                logd("EVENT_SUBSCRIPTIONS_CHANGED");
                updateUsageSetting();
                return;
            case 65:
                logd("EVENT_IMS_DEREGISTRATION_TRIGGERED");
                AsyncResult asyncResult17 = (AsyncResult) message.obj;
                Throwable th2 = asyncResult17.exception;
                if (th2 == null) {
                    this.mImsPhone.triggerImsDeregistration(((int[]) asyncResult17.result)[0]);
                    return;
                } else {
                    Rlog.e(LOG_TAG, "Unexpected unsol with exception", th2);
                    return;
                }
            case 66:
                logd("EVENT_SET_NULL_CIPHER_AND_INTEGRITY_DONE");
                AsyncResult asyncResult18 = (AsyncResult) message.obj;
                if (asyncResult18 == null || (th = asyncResult18.exception) == null) {
                    this.mIsNullCipherAndIntegritySupported = true;
                    return;
                } else {
                    this.mIsNullCipherAndIntegritySupported = !((CommandException) th).getCommandError().equals(CommandException.Error.REQUEST_NOT_SUPPORTED);
                    return;
                }
            case TelephonyProto$RilErrno.RIL_E_INVALID_RESPONSE /* 67 */:
                AsyncResult asyncResult19 = (AsyncResult) message.obj;
                if (asyncResult19.exception != null || (obj4 = asyncResult19.result) == null) {
                    loge("Exception received : " + asyncResult19.exception);
                    return;
                }
                ImeiInfo imeiInfo = (ImeiInfo) obj4;
                if (TextUtils.isEmpty(imeiInfo.imei)) {
                    return;
                }
                this.mImeiType = imeiInfo.type;
                this.mImei = imeiInfo.imei;
                this.mImeiSv = imeiInfo.svn;
                return;
            case 68:
                logd("EVENT_TRIGGER_NOTIFY_ANBR");
                AsyncResult asyncResult20 = (AsyncResult) message.obj;
                if (asyncResult20.exception != null || (phone = this.mImsPhone) == null) {
                    return;
                }
                Object obj6 = asyncResult20.result;
                phone.triggerNotifyAnbr(((int[]) obj6)[0], ((int[]) obj6)[1], ((int[]) obj6)[2]);
                return;
        }
        reapplyUiccAppsEnablementIfNeeded(ENABLE_UICC_APPS_MAX_RETRIES);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handleMessage$0(int i) {
        reapplyUiccAppsEnablementIfNeeded(i - 1);
    }

    public UiccCardApplication getUiccCardApplication() {
        if (isPhoneTypeGsm()) {
            return this.mUiccController.getUiccCardApplication(this.mPhoneId, 1);
        }
        return this.mUiccController.getUiccCardApplication(this.mPhoneId, 2);
    }

    @Override // com.android.internal.telephony.Phone
    protected void onUpdateIccAvailability() {
        IsimUiccRecords isimUiccRecords;
        if (this.mUiccController == null) {
            return;
        }
        if (isPhoneTypeGsm() || isPhoneTypeCdmaLte()) {
            UiccCardApplication uiccCardApplication = this.mUiccController.getUiccCardApplication(this.mPhoneId, 3);
            if (uiccCardApplication != null) {
                isimUiccRecords = (IsimUiccRecords) uiccCardApplication.getIccRecords();
                logd("New ISIM application found");
            } else {
                isimUiccRecords = null;
            }
            this.mIsimUiccRecords = isimUiccRecords;
        }
        SIMRecords sIMRecords = this.mSimRecords;
        if (sIMRecords != null) {
            sIMRecords.unregisterForRecordsLoaded(this);
        }
        if (isPhoneTypeCdmaLte() || isPhoneTypeCdma()) {
            UiccCardApplication uiccCardApplication2 = this.mUiccController.getUiccCardApplication(this.mPhoneId, 1);
            SIMRecords sIMRecords2 = uiccCardApplication2 != null ? (SIMRecords) uiccCardApplication2.getIccRecords() : null;
            this.mSimRecords = sIMRecords2;
            if (sIMRecords2 != null) {
                sIMRecords2.registerForRecordsLoaded(this, 3, null);
            }
        } else {
            this.mSimRecords = null;
        }
        UiccCardApplication uiccCardApplication3 = getUiccCardApplication();
        if (!isPhoneTypeGsm() && uiccCardApplication3 == null) {
            logd("can't find 3GPP2 application; trying APP_FAM_3GPP");
            uiccCardApplication3 = this.mUiccController.getUiccCardApplication(this.mPhoneId, 1);
        }
        UiccCardApplication uiccCardApplication4 = this.mUiccApplication.get();
        if (uiccCardApplication4 != uiccCardApplication3) {
            if (uiccCardApplication4 != null) {
                logd("Removing stale icc objects.");
                if (this.mIccRecords.get() != null) {
                    unregisterForIccRecordEvents();
                    this.mIccPhoneBookIntManager.updateIccRecords(null);
                }
                this.mIccRecords.set(null);
                this.mUiccApplication.set(null);
            }
            if (uiccCardApplication3 != null) {
                logd("New Uicc application found. type = " + uiccCardApplication3.getType());
                IccRecords iccRecords = uiccCardApplication3.getIccRecords();
                this.mUiccApplication.set(uiccCardApplication3);
                this.mIccRecords.set(iccRecords);
                registerForIccRecordEvents();
                this.mIccPhoneBookIntManager.updateIccRecords(iccRecords);
                if (iccRecords != null) {
                    String operatorNumeric = iccRecords.getOperatorNumeric();
                    logd("New simOperatorNumeric = " + operatorNumeric);
                    if (!TextUtils.isEmpty(operatorNumeric)) {
                        TelephonyManager.from(this.mContext).setSimOperatorNumericForPhone(this.mPhoneId, operatorNumeric);
                    }
                }
                updateCurrentCarrierInProvider();
            }
        }
        reapplyUiccAppsEnablementIfNeeded(ENABLE_UICC_APPS_MAX_RETRIES);
    }

    private void processIccRecordEvents(int i) {
        if (i != 1) {
            return;
        }
        logi("processIccRecordEvents: EVENT_CFI");
        notifyCallForwardingIndicator();
    }

    @Override // com.android.internal.telephony.Phone
    public boolean updateCurrentCarrierInProvider() {
        long defaultDataSubscriptionId = SubscriptionManager.getDefaultDataSubscriptionId();
        String operatorNumeric = getOperatorNumeric();
        logd("updateCurrentCarrierInProvider: mSubId = " + getSubId() + " currentDds = " + defaultDataSubscriptionId + " operatorNumeric = " + operatorNumeric);
        if (TextUtils.isEmpty(operatorNumeric) || getSubId() != defaultDataSubscriptionId) {
            return false;
        }
        try {
            Uri withAppendedPath = Uri.withAppendedPath(Telephony.Carriers.CONTENT_URI, "current");
            ContentValues contentValues = new ContentValues();
            contentValues.put("numeric", operatorNumeric);
            this.mContext.getContentResolver().insert(withAppendedPath, contentValues);
            return true;
        } catch (SQLException e) {
            Rlog.e(LOG_TAG, "Can't store current operator", e);
            return false;
        }
    }

    private boolean updateCurrentCarrierInProvider(String str) {
        if (isPhoneTypeCdma() || (isPhoneTypeCdmaLte() && this.mUiccController.getUiccCardApplication(this.mPhoneId, 1) == null)) {
            logd("CDMAPhone: updateCurrentCarrierInProvider called");
            if (TextUtils.isEmpty(str)) {
                return false;
            }
            try {
                Uri withAppendedPath = Uri.withAppendedPath(Telephony.Carriers.CONTENT_URI, "current");
                ContentValues contentValues = new ContentValues();
                contentValues.put("numeric", str);
                logd("updateCurrentCarrierInProvider from system: numeric=" + str);
                getContext().getContentResolver().insert(withAppendedPath, contentValues);
                logd("update mccmnc=" + str);
                MccTable.updateMccMncConfiguration(this.mContext, str);
                return true;
            } catch (SQLException e) {
                Rlog.e(LOG_TAG, "Can't store current operator", e);
                return false;
            }
        }
        logd("updateCurrentCarrierInProvider not updated X retVal=true");
        return true;
    }

    private void handleCfuQueryResult(CallForwardInfo[] callForwardInfoArr) {
        if (callForwardInfoArr == null || callForwardInfoArr.length == 0) {
            setVoiceCallForwardingFlag(1, false, null);
            return;
        }
        for (CallForwardInfo callForwardInfo : callForwardInfoArr) {
            if ((callForwardInfo.serviceClass & 1) != 0) {
                setVoiceCallForwardingFlag(1, callForwardInfo.status == 1, callForwardInfo.number);
                return;
            }
        }
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public IccPhoneBookInterfaceManager getIccPhoneBookInterfaceManager() {
        return this.mIccPhoneBookIntManager;
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void activateCellBroadcastSms(int i, Message message) {
        loge("[GsmCdmaPhone] activateCellBroadcastSms() is obsolete; use SmsManager");
        message.sendToTarget();
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void getCellBroadcastSmsConfig(Message message) {
        loge("[GsmCdmaPhone] getCellBroadcastSmsConfig() is obsolete; use SmsManager");
        message.sendToTarget();
    }

    @Override // com.android.internal.telephony.PhoneInternalInterface
    public void setCellBroadcastSmsConfig(int[] iArr, Message message) {
        loge("[GsmCdmaPhone] setCellBroadcastSmsConfig() is obsolete; use SmsManager");
        message.sendToTarget();
    }

    @Override // com.android.internal.telephony.Phone
    public boolean needsOtaServiceProvisioning() {
        return (isPhoneTypeGsm() || this.mSST.getOtasp() == 3) ? false : true;
    }

    @Override // com.android.internal.telephony.Phone
    public boolean isCspPlmnEnabled() {
        IccRecords iccRecords = this.mIccRecords.get();
        if (iccRecords != null) {
            return iccRecords.isCspPlmnEnabled();
        }
        return false;
    }

    public boolean shouldForceAutoNetworkSelect() {
        RadioAccessFamily.getRafFromNetworkType(RILConstants.PREFERRED_NETWORK_MODE);
        if (SubscriptionManager.isValidSubscriptionId(getSubId())) {
            int allowedNetworkTypes = (int) getAllowedNetworkTypes(0);
            logd("shouldForceAutoNetworkSelect in mode = " + allowedNetworkTypes);
            if (isManualSelProhibitedInGlobalMode() && (allowedNetworkTypes == RadioAccessFamily.getRafFromNetworkType(10) || allowedNetworkTypes == RadioAccessFamily.getRafFromNetworkType(7))) {
                logd("Should force auto network select mode = " + allowedNetworkTypes);
                return true;
            }
            logd("Should not force auto network select mode = " + allowedNetworkTypes);
            return false;
        }
        return false;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private boolean isManualSelProhibitedInGlobalMode() {
        String[] split;
        String string = getContext().getResources().getString(17041375);
        boolean z = false;
        if (!TextUtils.isEmpty(string) && (split = string.split(";")) != null && ((split.length == 1 && split[0].equalsIgnoreCase("true")) || (split.length == 2 && !TextUtils.isEmpty(split[1]) && split[0].equalsIgnoreCase("true") && isMatchGid(split[1])))) {
            z = true;
        }
        logd("isManualNetSelAllowedInGlobal in current carrier is " + z);
        return z;
    }

    private void registerForIccRecordEvents() {
        IccRecords iccRecords = this.mIccRecords.get();
        if (iccRecords == null) {
            return;
        }
        if (isPhoneTypeGsm()) {
            iccRecords.registerForNetworkSelectionModeAutomatic(this, 28, null);
            iccRecords.registerForRecordsEvents(this, 29, null);
            iccRecords.registerForRecordsLoaded(this, 3, null);
            return;
        }
        iccRecords.registerForRecordsLoaded(this, 22, null);
        if (isPhoneTypeCdmaLte()) {
            iccRecords.registerForRecordsLoaded(this, 3, null);
        }
    }

    private void unregisterForIccRecordEvents() {
        IccRecords iccRecords = this.mIccRecords.get();
        if (iccRecords == null) {
            return;
        }
        iccRecords.unregisterForNetworkSelectionModeAutomatic(this);
        iccRecords.unregisterForRecordsEvents(this);
        iccRecords.unregisterForRecordsLoaded(this);
    }

    @Override // com.android.internal.telephony.Phone
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void exitEmergencyCallbackMode() {
        Rlog.d(LOG_TAG, "exitEmergencyCallbackMode: mImsPhone=" + this.mImsPhone + " isPhoneTypeGsm=" + isPhoneTypeGsm());
        Phone phone = this.mImsPhone;
        if (phone != null && phone.isInImsEcm()) {
            this.mImsPhone.exitEmergencyCallbackMode();
            return;
        }
        if (this.mWakeLock.isHeld()) {
            this.mWakeLock.release();
        }
        this.mCi.exitEmergencyCallbackMode(this.mIsTestingEmergencyCallbackMode ? obtainMessage(26) : null);
    }

    private void handleEnterEmergencyCallbackMode(Message message) {
        if (DomainSelectionResolver.getInstance().isDomainSelectionSupported()) {
            Rlog.d(LOG_TAG, "DomainSelection enabled: ignore ECBM enter event.");
            return;
        }
        Rlog.d(LOG_TAG, "handleEnterEmergencyCallbackMode, isInEcm()=" + isInEcm());
        if (isInEcm()) {
            return;
        }
        setIsInEcm(true);
        sendEmergencyCallbackModeChange();
        postDelayed(this.mExitEcmRunnable, TelephonyProperties.ecm_exit_timer().orElse(300000L).longValue());
        this.mWakeLock.acquire();
    }

    private void handleExitEmergencyCallbackMode(Message message) {
        if (DomainSelectionResolver.getInstance().isDomainSelectionSupported()) {
            Rlog.d(LOG_TAG, "DomainSelection enabled: ignore ECBM exit event.");
            return;
        }
        AsyncResult asyncResult = (AsyncResult) message.obj;
        Rlog.d(LOG_TAG, "handleExitEmergencyCallbackMode,ar.exception , isInEcm=" + asyncResult.exception + isInEcm());
        removeCallbacks(this.mExitEcmRunnable);
        Registrant registrant = this.mEcmExitRespRegistrant;
        if (registrant != null) {
            registrant.notifyRegistrant(asyncResult);
        }
        if (asyncResult.exception == null || this.mIsTestingEmergencyCallbackMode) {
            if (isInEcm()) {
                setIsInEcm(false);
            }
            if (this.mWakeLock.isHeld()) {
                this.mWakeLock.release();
            }
            sendEmergencyCallbackModeChange();
            notifyEmergencyCallRegistrants(false);
        }
        this.mIsTestingEmergencyCallbackMode = false;
    }

    public void notifyEmergencyCallRegistrants(boolean z) {
        this.mEmergencyCallToggledRegistrants.notifyResult(Integer.valueOf(z ? 1 : 0));
    }

    public void handleTimerInEmergencyCallbackMode(int i) {
        if (DomainSelectionResolver.getInstance().isDomainSelectionSupported()) {
            return;
        }
        if (i == 0) {
            postDelayed(this.mExitEcmRunnable, TelephonyProperties.ecm_exit_timer().orElse(300000L).longValue());
            this.mEcmTimerResetRegistrants.notifyResult(Boolean.FALSE);
            setEcmCanceledForEmergency(false);
        } else if (i == 1) {
            removeCallbacks(this.mExitEcmRunnable);
            this.mEcmTimerResetRegistrants.notifyResult(Boolean.TRUE);
            setEcmCanceledForEmergency(true);
        } else {
            Rlog.e(LOG_TAG, "handleTimerInEmergencyCallbackMode, unsupported action " + i);
        }
    }

    private static boolean isIs683OtaSpDialStr(String str) {
        if (str.length() != 4) {
            switch (extractSelCodeFromOtaSpNum(str)) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    return true;
            }
        } else if (str.equals("*228")) {
            return true;
        }
        return false;
    }

    private static int extractSelCodeFromOtaSpNum(String str) {
        int parseInt = (!str.regionMatches(0, "*228", 0, 4) || str.length() < 6) ? -1 : Integer.parseInt(str.substring(4, 6));
        Rlog.d(LOG_TAG, "extractSelCodeFromOtaSpNum " + parseInt);
        return parseInt;
    }

    private static boolean checkOtaSpNumBasedOnSysSelCode(int i, String[] strArr) {
        boolean z = true;
        try {
            int parseInt = Integer.parseInt(strArr[1]);
            int i2 = 0;
            while (true) {
                if (i2 >= parseInt) {
                    z = false;
                    break;
                }
                int i3 = i2 * 2;
                int i4 = i3 + 2;
                if (!TextUtils.isEmpty(strArr[i4])) {
                    int i5 = i3 + 3;
                    if (!TextUtils.isEmpty(strArr[i5])) {
                        int parseInt2 = Integer.parseInt(strArr[i4]);
                        int parseInt3 = Integer.parseInt(strArr[i5]);
                        if (i >= parseInt2 && i <= parseInt3) {
                            break;
                        }
                    } else {
                        continue;
                    }
                }
                i2++;
            }
            return z;
        } catch (NumberFormatException e) {
            Rlog.e(LOG_TAG, "checkOtaSpNumBasedOnSysSelCode, error", e);
            return false;
        }
    }

    private boolean isCarrierOtaSpNum(String str) {
        int extractSelCodeFromOtaSpNum = extractSelCodeFromOtaSpNum(str);
        if (extractSelCodeFromOtaSpNum == -1) {
            return false;
        }
        if (!TextUtils.isEmpty(this.mCarrierOtaSpNumSchema)) {
            Matcher matcher = pOtaSpNumSchema.matcher(this.mCarrierOtaSpNumSchema);
            Rlog.d(LOG_TAG, "isCarrierOtaSpNum,schema" + this.mCarrierOtaSpNumSchema);
            if (matcher.find()) {
                String[] split = pOtaSpNumSchema.split(this.mCarrierOtaSpNumSchema);
                if (!TextUtils.isEmpty(split[0]) && split[0].equals("SELC")) {
                    if (extractSelCodeFromOtaSpNum != -1) {
                        return checkOtaSpNumBasedOnSysSelCode(extractSelCodeFromOtaSpNum, split);
                    }
                    Rlog.d(LOG_TAG, "isCarrierOtaSpNum,sysSelCodeInt is invalid");
                    return false;
                } else if (!TextUtils.isEmpty(split[0]) && split[0].equals("FC")) {
                    if (str.regionMatches(0, split[2], 0, Integer.parseInt(split[1]))) {
                        return true;
                    }
                    Rlog.d(LOG_TAG, "isCarrierOtaSpNum,not otasp number");
                    return false;
                } else {
                    Rlog.d(LOG_TAG, "isCarrierOtaSpNum,ota schema not supported" + split[0]);
                    return false;
                }
            }
            Rlog.d(LOG_TAG, "isCarrierOtaSpNum,ota schema pattern not right" + this.mCarrierOtaSpNumSchema);
            return false;
        }
        Rlog.d(LOG_TAG, "isCarrierOtaSpNum,ota schema pattern empty");
        return false;
    }

    @Override // com.android.internal.telephony.Phone
    public boolean isOtaSpNumber(String str) {
        boolean z;
        if (isPhoneTypeGsm()) {
            return super.isOtaSpNumber(str);
        }
        String extractNetworkPortionAlt = PhoneNumberUtils.extractNetworkPortionAlt(str);
        if (extractNetworkPortionAlt != null) {
            z = isIs683OtaSpDialStr(extractNetworkPortionAlt);
            if (!z) {
                z = isCarrierOtaSpNum(extractNetworkPortionAlt);
            }
        } else {
            z = false;
        }
        Rlog.d(LOG_TAG, "isOtaSpNumber " + z);
        return z;
    }

    @Override // com.android.internal.telephony.Phone
    public int getOtasp() {
        return this.mSST.getOtasp();
    }

    @Override // com.android.internal.telephony.Phone
    public int getCdmaEriIconIndex() {
        if (isPhoneTypeGsm()) {
            return super.getCdmaEriIconIndex();
        }
        return getServiceState().getCdmaEriIconIndex();
    }

    @Override // com.android.internal.telephony.Phone
    public int getCdmaEriIconMode() {
        if (isPhoneTypeGsm()) {
            return super.getCdmaEriIconMode();
        }
        return getServiceState().getCdmaEriIconMode();
    }

    @Override // com.android.internal.telephony.Phone
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public String getCdmaEriText() {
        if (isPhoneTypeGsm()) {
            return super.getCdmaEriText();
        }
        return this.mSST.getCdmaEriText(getServiceState().getCdmaRoamingIndicator(), getServiceState().getCdmaDefaultRoamingIndicator());
    }

    @Override // com.android.internal.telephony.Phone
    public boolean isCdmaSubscriptionAppPresent() {
        UiccCardApplication uiccCardApplication = this.mUiccController.getUiccCardApplication(this.mPhoneId, 2);
        return uiccCardApplication != null && (uiccCardApplication.getType() == IccCardApplicationStatus.AppType.APPTYPE_CSIM || uiccCardApplication.getType() == IccCardApplicationStatus.AppType.APPTYPE_RUIM);
    }

    protected void phoneObjectUpdater(int i) {
        logd("phoneObjectUpdater: newVoiceRadioTech=" + i);
        if (ServiceState.isPsOnlyTech(i) || i == 0) {
            PersistableBundle configForSubId = ((CarrierConfigManager) getContext().getSystemService("carrier_config")).getConfigForSubId(getSubId());
            if (configForSubId != null) {
                int i2 = configForSubId.getInt("volte_replacement_rat_int");
                logd("phoneObjectUpdater: volteReplacementRat=" + i2);
                if (i2 != 0 && (ServiceState.isGsm(i2) || isCdmaSubscriptionAppPresent())) {
                    i = i2;
                }
            } else {
                loge("phoneObjectUpdater: didn't get volteReplacementRat from carrier config");
            }
        }
        boolean z = true;
        if (this.mRilVersion == 6 && getLteOnCdmaMode() == 1) {
            if (getPhoneType() == 2) {
                logd("phoneObjectUpdater: LTE ON CDMA property is set. Use CDMA Phone newVoiceRadioTech=" + i + " mActivePhone=" + getPhoneName());
                return;
            }
            logd("phoneObjectUpdater: LTE ON CDMA property is set. Switch to CDMALTEPhone newVoiceRadioTech=" + i + " mActivePhone=" + getPhoneName());
            i = 6;
        } else if (isShuttingDown()) {
            logd("Device is shutting down. No need to switch phone now.");
            return;
        } else {
            boolean isCdma = ServiceState.isCdma(i);
            boolean isGsm = ServiceState.isGsm(i);
            if ((isCdma && getPhoneType() == 2) || (isGsm && getPhoneType() == 1)) {
                logd("phoneObjectUpdater: No change ignore, newVoiceRadioTech=" + i + " mActivePhone=" + getPhoneName());
                return;
            } else if (!isCdma && !isGsm) {
                loge("phoneObjectUpdater: newVoiceRadioTech=" + i + " doesn't match either CDMA or GSM - error! No phone change");
                return;
            }
        }
        if (i == 0) {
            logd("phoneObjectUpdater: Unknown rat ignore,  newVoiceRadioTech=Unknown. mActivePhone=" + getPhoneName());
            return;
        }
        if (this.mResetModemOnRadioTechnologyChange && this.mCi.getRadioState() == 1) {
            logd("phoneObjectUpdater: Setting Radio Power to Off");
            this.mCi.setRadioPower(false, null);
        } else {
            z = false;
        }
        switchVoiceRadioTech(i);
        if (this.mResetModemOnRadioTechnologyChange && z) {
            logd("phoneObjectUpdater: Resetting Radio");
            this.mCi.setRadioPower(z, null);
        }
        UiccProfile uiccProfile = getUiccProfile();
        if (uiccProfile != null) {
            uiccProfile.setVoiceRadioTech(i);
        }
        Intent intent = new Intent("android.intent.action.RADIO_TECHNOLOGY");
        intent.putExtra("phoneName", getPhoneName());
        SubscriptionManager.putPhoneIdAndSubIdExtra(intent, this.mPhoneId);
        this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
    }

    private void switchVoiceRadioTech(int i) {
        String phoneName = getPhoneName();
        StringBuilder sb = new StringBuilder();
        sb.append("Switching Voice Phone : ");
        sb.append(phoneName);
        sb.append(" >>> ");
        sb.append(ServiceState.isGsm(i) ? "GSM" : "CDMA");
        logd(sb.toString());
        if (ServiceState.isCdma(i)) {
            UiccCardApplication uiccCardApplication = this.mUiccController.getUiccCardApplication(this.mPhoneId, 2);
            if (uiccCardApplication != null && uiccCardApplication.getType() == IccCardApplicationStatus.AppType.APPTYPE_RUIM) {
                switchPhoneType(2);
            } else {
                switchPhoneType(6);
            }
        } else if (ServiceState.isGsm(i)) {
            switchPhoneType(1);
        } else {
            loge("deleteAndCreatePhone: newVoiceRadioTech=" + i + " is not CDMA or GSM (error) - aborting!");
        }
    }

    @Override // com.android.internal.telephony.Phone
    public void setLinkCapacityReportingCriteria(int[] iArr, int[] iArr2, int i) {
        this.mCi.setLinkCapacityReportingCriteria(GbaManager.RETRY_TIME_MS, 50, 50, iArr, iArr2, i, null);
    }

    @Override // com.android.internal.telephony.Phone
    public IccSmsInterfaceManager getIccSmsInterfaceManager() {
        return this.mIccSmsInterfaceManager;
    }

    @Override // com.android.internal.telephony.Phone
    public void updatePhoneObject(int i) {
        logd("updatePhoneObject: radioTechnology=" + i);
        sendMessage(obtainMessage(42, i, 0, null));
    }

    @Override // com.android.internal.telephony.Phone
    public void setImsRegistrationState(boolean z) {
        this.mSST.setImsRegistrationState(z);
        this.mCallWaitingController.setImsRegistrationState(z);
    }

    @Override // com.android.internal.telephony.Phone
    public boolean getIccRecordsLoaded() {
        UiccProfile uiccProfile = getUiccProfile();
        return uiccProfile != null && uiccProfile.getIccRecordsLoaded();
    }

    @Override // com.android.internal.telephony.Phone
    public IccCard getIccCard() {
        UiccProfile uiccProfile = getUiccProfile();
        if (uiccProfile != null) {
            return uiccProfile;
        }
        UiccSlot uiccSlotForPhone = this.mUiccController.getUiccSlotForPhone(this.mPhoneId);
        if (uiccSlotForPhone == null || uiccSlotForPhone.isStateUnknown()) {
            return new IccCard(IccCardConstants.State.UNKNOWN);
        }
        return new IccCard(IccCardConstants.State.ABSENT);
    }

    private UiccProfile getUiccProfile() {
        return UiccController.getInstance().getUiccProfileForPhone(this.mPhoneId);
    }

    @Override // com.android.internal.telephony.Phone
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("GsmCdmaPhone extends:");
        super.dump(fileDescriptor, printWriter, strArr);
        printWriter.println(" mPrecisePhoneType=" + this.mPrecisePhoneType);
        printWriter.println(" mCT=" + this.mCT);
        printWriter.println(" mSST=" + this.mSST);
        printWriter.println(" mPendingMMIs=" + this.mPendingMMIs);
        printWriter.println(" mIccPhoneBookIntManager=" + this.mIccPhoneBookIntManager);
        printWriter.println(" mImei=" + pii(this.mImei));
        printWriter.println(" mImeiSv=" + pii(this.mImeiSv));
        printWriter.println(" mVmNumber=" + pii(this.mVmNumber));
        printWriter.println(" mCdmaSSM=" + this.mCdmaSSM);
        printWriter.println(" mCdmaSubscriptionSource=" + this.mCdmaSubscriptionSource);
        printWriter.println(" mWakeLock=" + this.mWakeLock);
        printWriter.println(" isInEcm()=" + isInEcm());
        printWriter.println(" mEsn=" + pii(this.mEsn));
        printWriter.println(" mMeid=" + pii(this.mMeid));
        printWriter.println(" mCarrierOtaSpNumSchema=" + this.mCarrierOtaSpNumSchema);
        if (!isPhoneTypeGsm()) {
            printWriter.println(" getCdmaEriIconIndex()=" + getCdmaEriIconIndex());
            printWriter.println(" getCdmaEriIconMode()=" + getCdmaEriIconMode());
            printWriter.println(" getCdmaEriText()=" + getCdmaEriText());
            printWriter.println(" isMinInfoReady()=" + isMinInfoReady());
        }
        printWriter.println(" isCspPlmnEnabled()=" + isCspPlmnEnabled());
        printWriter.println(" mManualNetworkSelectionPlmn=" + this.mManualNetworkSelectionPlmn);
        printWriter.println(" mTelecomVoiceServiceStateOverride=" + this.mTelecomVoiceServiceStateOverride + "(" + ServiceState.rilServiceStateToString(this.mTelecomVoiceServiceStateOverride) + ")");
        printWriter.flush();
        try {
            this.mCallWaitingController.dump(printWriter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        printWriter.flush();
    }

    @Override // com.android.internal.telephony.Phone
    public boolean setOperatorBrandOverride(String str) {
        UiccPort uiccPort;
        UiccController uiccController = this.mUiccController;
        if (uiccController == null || (uiccPort = uiccController.getUiccPort(getPhoneId())) == null) {
            return false;
        }
        boolean operatorBrandOverride = uiccPort.setOperatorBrandOverride(str);
        if (operatorBrandOverride) {
            TelephonyManager.from(this.mContext).setSimOperatorNameForPhone(getPhoneId(), this.mSST.getServiceProviderName());
            this.mSST.pollState();
        }
        return operatorBrandOverride;
    }

    private String checkForTestEmergencyNumber(String str) {
        String str2 = SystemProperties.get("ril.test.emergencynumber");
        if (TextUtils.isEmpty(str2)) {
            return str;
        }
        String[] split = str2.split(":");
        logd("checkForTestEmergencyNumber: values.length=" + split.length);
        if (split.length == 2 && split[0].equals(PhoneNumberUtils.stripSeparators(str))) {
            logd("checkForTestEmergencyNumber: remap " + str + " to " + split[1]);
            return split[1];
        }
        return str;
    }

    @Override // com.android.internal.telephony.Phone
    public String getOperatorNumeric() {
        IccRecords iccRecords;
        String str;
        String str2 = null;
        if (isPhoneTypeGsm()) {
            IccRecords iccRecords2 = this.mIccRecords.get();
            if (iccRecords2 != null) {
                str2 = iccRecords2.getOperatorNumeric();
            }
        } else {
            int i = this.mCdmaSubscriptionSource;
            if (i == 1) {
                str = SystemProperties.get(PROPERTY_CDMA_HOME_OPERATOR_NUMERIC);
                iccRecords = null;
            } else if (i == 0) {
                UiccCardApplication uiccCardApplication = this.mUiccApplication.get();
                if (uiccCardApplication != null && uiccCardApplication.getType() == IccCardApplicationStatus.AppType.APPTYPE_RUIM) {
                    logd("Legacy RUIM app present");
                    iccRecords = this.mIccRecords.get();
                } else {
                    iccRecords = this.mSimRecords;
                }
                if (iccRecords != null && iccRecords == this.mSimRecords) {
                    str = iccRecords.getOperatorNumeric();
                } else {
                    iccRecords = this.mIccRecords.get();
                    str = (iccRecords == null || !(iccRecords instanceof RuimRecords)) ? null : ((RuimRecords) iccRecords).getRUIMOperatorNumeric();
                }
            } else {
                iccRecords = null;
                str = null;
            }
            if (str == null) {
                StringBuilder sb = new StringBuilder();
                sb.append("getOperatorNumeric: Cannot retrieve operatorNumeric: mCdmaSubscriptionSource = ");
                sb.append(this.mCdmaSubscriptionSource);
                sb.append(" mIccRecords = ");
                sb.append(iccRecords != null ? Boolean.valueOf(iccRecords.getRecordsLoaded()) : null);
                loge(sb.toString());
            }
            logd("getOperatorNumeric: mCdmaSubscriptionSource = " + this.mCdmaSubscriptionSource + " operatorNumeric = " + str);
            str2 = str;
        }
        return TextUtils.emptyIfNull(str2);
    }

    public String getCountryIso() {
        SubscriptionInfo activeSubscriptionInfo = SubscriptionManager.from(getContext()).getActiveSubscriptionInfo(getSubId());
        if (activeSubscriptionInfo == null || TextUtils.isEmpty(activeSubscriptionInfo.getCountryIso())) {
            return null;
        }
        return activeSubscriptionInfo.getCountryIso().toUpperCase(Locale.ROOT);
    }

    public void notifyEcbmTimerReset(Boolean bool) {
        this.mEcmTimerResetRegistrants.notifyResult(bool);
    }

    public int getCsCallRadioTech() {
        ServiceStateTracker serviceStateTracker = this.mSST;
        if (serviceStateTracker != null) {
            return getCsCallRadioTech(serviceStateTracker.mSS.getState(), this.mSST.mSS.getRilVoiceRadioTechnology());
        }
        return 0;
    }

    private int getCsCallRadioTech(int i, int i2) {
        logd("getCsCallRadioTech, current vrs=" + i + ", vrat=" + i2);
        i2 = (i != 0 || ArrayUtils.contains(VOICE_PS_CALL_RADIO_TECHNOLOGY, i2)) ? 0 : 0;
        logd("getCsCallRadioTech, result calcVrat=" + i2);
        return i2;
    }

    private void onVoiceRegStateOrRatChanged(int i, int i2) {
        logd("onVoiceRegStateOrRatChanged");
        this.mCT.dispatchCsCallRadioTech(getCsCallRadioTech(i, i2));
    }

    @Override // com.android.internal.telephony.Phone
    public void registerForEcmTimerReset(Handler handler, int i, Object obj) {
        this.mEcmTimerResetRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.Phone
    public void unregisterForEcmTimerReset(Handler handler) {
        this.mEcmTimerResetRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.Phone
    public void registerForVolteSilentRedial(Handler handler, int i, Object obj) {
        this.mVolteSilentRedialRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.Phone
    public void unregisterForVolteSilentRedial(Handler handler) {
        this.mVolteSilentRedialRegistrants.remove(handler);
    }

    public void notifyVolteSilentRedial(String str, int i) {
        logd("notifyVolteSilentRedial: dialString=" + str + " causeCode=" + i);
        this.mVolteSilentRedialRegistrants.notifyRegistrants(new AsyncResult((Object) null, new Phone.SilentRedialParam(str, i, this.mDialArgs), (Throwable) null));
    }

    @Override // com.android.internal.telephony.Phone
    public void registerForEmergencyDomainSelected(Handler handler, int i, Object obj) {
        this.mEmergencyDomainSelectedRegistrants.addUnique(handler, i, obj);
    }

    @Override // com.android.internal.telephony.Phone
    public void unregisterForEmergencyDomainSelected(Handler handler) {
        this.mEmergencyDomainSelectedRegistrants.remove(handler);
    }

    @Override // com.android.internal.telephony.Phone
    public void notifyEmergencyDomainSelected(int i) {
        logd("notifyEmergencyDomainSelected transportType=" + i);
        this.mEmergencyDomainSelectedRegistrants.notifyRegistrants(new AsyncResult((Object) null, Integer.valueOf(i), (Throwable) null));
    }

    @Override // com.android.internal.telephony.Phone
    public void setVoiceMessageWaiting(int i, int i2) {
        if (isPhoneTypeGsm()) {
            IccRecords iccRecords = this.mIccRecords.get();
            if (iccRecords != null) {
                iccRecords.setVoiceMessageWaiting(i, i2);
                return;
            } else {
                logd("SIM Records not found, MWI not updated");
                return;
            }
        }
        setVoiceMessageCount(i2);
    }

    private CallForwardInfo[] makeEmptyCallForward() {
        CallForwardInfo[] callForwardInfoArr = {new CallForwardInfo()};
        CallForwardInfo callForwardInfo = callForwardInfoArr[0];
        callForwardInfo.status = 255;
        callForwardInfo.reason = 0;
        callForwardInfo.serviceClass = 1;
        callForwardInfo.toa = 129;
        callForwardInfo.number = PhoneConfigurationManager.SSSS;
        callForwardInfo.timeSeconds = 0;
        return callForwardInfoArr;
    }

    private PhoneAccountHandle subscriptionIdToPhoneAccountHandle(int i) {
        TelecomManager from = TelecomManager.from(this.mContext);
        TelephonyManager from2 = TelephonyManager.from(this.mContext);
        ListIterator listIterator = from.getCallCapablePhoneAccounts(true).listIterator();
        while (listIterator.hasNext()) {
            PhoneAccountHandle phoneAccountHandle = (PhoneAccountHandle) listIterator.next();
            if (i == from2.getSubIdForPhoneAccount(from.getPhoneAccount(phoneAccountHandle))) {
                return phoneAccountHandle;
            }
        }
        return null;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private void logd(String str) {
        Rlog.d(LOG_TAG, "[" + this.mPhoneId + "] " + str);
    }

    private void logi(String str) {
        Rlog.i(LOG_TAG, "[" + this.mPhoneId + "] " + str);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private void loge(String str) {
        Rlog.e(LOG_TAG, "[" + this.mPhoneId + "] " + str);
    }

    private static String pii(String str) {
        return Rlog.pii(LOG_TAG, str);
    }

    @Override // com.android.internal.telephony.Phone
    public boolean isUtEnabled() {
        Phone phone = this.mImsPhone;
        if (phone != null) {
            return phone.isUtEnabled();
        }
        logd("isUtEnabled: called for GsmCdma");
        return false;
    }

    public String getDtmfToneDelayKey() {
        return isPhoneTypeGsm() ? "gsm_dtmf_tone_delay_int" : "cdma_dtmf_tone_delay_int";
    }

    @VisibleForTesting
    public PowerManager.WakeLock getWakeLock() {
        return this.mWakeLock;
    }

    public int getLteOnCdmaMode() {
        int intValue = TelephonyProperties.lte_on_cdma_device().orElse(0).intValue();
        UiccCardApplication uiccCardApplication = this.mUiccController.getUiccCardApplication(this.mPhoneId, 2);
        if (uiccCardApplication != null && uiccCardApplication.getType() == IccCardApplicationStatus.AppType.APPTYPE_RUIM && intValue == 1) {
            return 0;
        }
        return intValue;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTtyMode(int i) {
        logi(String.format("updateTtyMode ttyMode=%d", Integer.valueOf(i)));
        setTTYMode(telecomModeToPhoneMode(i), null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateUiTtyMode(int i) {
        logi(String.format("updateUiTtyMode ttyMode=%d", Integer.valueOf(i)));
        setUiTTYMode(telecomModeToPhoneMode(i), null);
    }

    private void loadTtyMode() {
        TelecomManager telecomManager = (TelecomManager) this.mContext.getSystemService(TelecomManager.class);
        updateTtyMode(telecomManager != null ? telecomManager.getCurrentTtyMode() : 0);
        updateUiTtyMode(Settings.Secure.getInt(this.mContext.getContentResolver(), "preferred_tty_mode", 0));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reapplyUiccAppsEnablementIfNeeded(int i) {
        SubscriptionInfo subInfoForIccId;
        UiccSlot uiccSlotForPhone = this.mUiccController.getUiccSlotForPhone(this.mPhoneId);
        if (uiccSlotForPhone == null || uiccSlotForPhone.getCardState() != IccCardStatus.CardState.CARDSTATE_PRESENT || this.mUiccApplicationsEnabled == null) {
            return;
        }
        final String iccId = uiccSlotForPhone.getIccId(uiccSlotForPhone.getPortIndexFromPhoneId(this.mPhoneId));
        if (iccId == null) {
            loge("reapplyUiccAppsEnablementIfNeeded iccId is null, phoneId: " + this.mPhoneId + " portIndex: " + uiccSlotForPhone.getPortIndexFromPhoneId(this.mPhoneId));
            return;
        }
        if (isSubscriptionManagerServiceEnabled()) {
            subInfoForIccId = this.mSubscriptionManagerService.getAllSubInfoList(this.mContext.getOpPackageName(), this.mContext.getAttributionTag()).stream().filter(new Predicate() { // from class: com.android.internal.telephony.GsmCdmaPhone$$ExternalSyntheticLambda1
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$reapplyUiccAppsEnablementIfNeeded$1;
                    lambda$reapplyUiccAppsEnablementIfNeeded$1 = GsmCdmaPhone.lambda$reapplyUiccAppsEnablementIfNeeded$1(iccId, (SubscriptionInfo) obj);
                    return lambda$reapplyUiccAppsEnablementIfNeeded$1;
                }
            }).findFirst().orElse(null);
        } else {
            subInfoForIccId = SubscriptionController.getInstance().getSubInfoForIccId(IccUtils.stripTrailingFs(iccId));
        }
        logd("reapplyUiccAppsEnablementIfNeeded: retries=" + i + ", subInfo=" + subInfoForIccId);
        boolean z = subInfoForIccId == null || subInfoForIccId.areUiccApplicationsEnabled();
        if (z != this.mUiccApplicationsEnabled.booleanValue()) {
            this.mCi.enableUiccApplications(z, Message.obtain(this, 56, new Pair(Boolean.valueOf(z), Integer.valueOf(i))));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$reapplyUiccAppsEnablementIfNeeded$1(String str, SubscriptionInfo subscriptionInfo) {
        return subscriptionInfo.getIccId().equals(IccUtils.stripTrailingFs(str));
    }

    @Override // com.android.internal.telephony.Phone
    public void enableUiccApplications(boolean z, Message message) {
        UiccSlot uiccSlotForPhone = this.mUiccController.getUiccSlotForPhone(this.mPhoneId);
        if (uiccSlotForPhone != null && uiccSlotForPhone.getCardState() == IccCardStatus.CardState.CARDSTATE_PRESENT) {
            this.mCi.enableUiccApplications(z, message);
        } else if (message != null) {
            AsyncResult.forMessage(message, (Object) null, new IllegalStateException("No SIM card is present"));
            message.sendToTarget();
        }
    }

    @Override // com.android.internal.telephony.Phone
    public boolean canDisablePhysicalSubscription() {
        return this.mCi.canToggleUiccApplicationsEnablement();
    }

    @Override // com.android.internal.telephony.Phone
    public List<String> getEquivalentHomePlmns() {
        if (isPhoneTypeGsm()) {
            IccRecords iccRecords = this.mIccRecords.get();
            if (iccRecords != null && iccRecords.getEhplmns() != null) {
                return Arrays.asList(iccRecords.getEhplmns());
            }
        } else if (isPhoneTypeCdma()) {
            loge("EHPLMN is not available in CDMA");
        }
        return Collections.emptyList();
    }

    @Override // com.android.internal.telephony.Phone
    public List<String> getDataServicePackages() {
        return getDataNetworkController().getDataServicePackages();
    }

    /* renamed from: updateBroadcastEmergencyCallStateChangesAfterCarrierConfigChanged */
    private void m7xf0516ded(PersistableBundle persistableBundle) {
        if (persistableBundle == null) {
            loge("didn't get broadcastEmergencyCallStateChanges from carrier config");
            return;
        }
        boolean z = persistableBundle.getBoolean("broadcast_emergency_call_state_changes_bool");
        logd("broadcastEmergencyCallStateChanges = " + z);
        setBroadcastEmergencyCallStateChanges(z);
    }

    private void updateNrSettingsAfterCarrierConfigChanged(PersistableBundle persistableBundle) {
        if (persistableBundle == null) {
            loge("didn't get the carrier_nr_availability_int from the carrier config.");
        } else {
            this.mIsCarrierNrSupported = !ArrayUtils.isEmpty(persistableBundle.getIntArray("carrier_nr_availabilities_int_array"));
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:24:0x0081, code lost:
        if (r6 == false) goto L22;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void updateVoNrSettings(PersistableBundle persistableBundle) {
        int parseInt;
        boolean z;
        UiccSlot uiccSlotForPhone = this.mUiccController.getUiccSlotForPhone(this.mPhoneId);
        if (uiccSlotForPhone == null || uiccSlotForPhone.getCardState() != IccCardStatus.CardState.CARDSTATE_PRESENT) {
            return;
        }
        if (persistableBundle == null) {
            loge("didn't get the vonr_enabled_bool from the carrier config.");
            return;
        }
        boolean z2 = persistableBundle.getBoolean("vonr_enabled_bool");
        boolean z3 = persistableBundle.getBoolean("vonr_on_by_default_bool");
        if (isSubscriptionManagerServiceEnabled()) {
            SubscriptionInfoInternal subscriptionInfoInternal = this.mSubscriptionManagerService.getSubscriptionInfoInternal(getSubId());
            if (subscriptionInfoInternal != null) {
                parseInt = subscriptionInfoInternal.getNrAdvancedCallingEnabled();
            }
            parseInt = -1;
        } else {
            String subscriptionProperty = SubscriptionController.getInstance().getSubscriptionProperty(getSubId(), "nr_advanced_calling_enabled");
            if (subscriptionProperty != null) {
                parseInt = Integer.parseInt(subscriptionProperty);
            }
            parseInt = -1;
        }
        logd("VoNR setting from telephony.db:" + parseInt + " ,vonr_enabled_bool:" + z2 + " ,vonr_on_by_default_bool:" + z3);
        if (z2) {
            z = true;
            if (parseInt != 1) {
                if (parseInt == -1) {
                }
            }
            this.mCi.setVoNrEnabled(z, obtainMessage(61), null);
        }
        z = false;
        this.mCi.setVoNrEnabled(z, obtainMessage(61), null);
    }

    private void updateCdmaRoamingSettingsAfterCarrierConfigChanged(PersistableBundle persistableBundle) {
        if (persistableBundle == null) {
            loge("didn't get the cdma_roaming_mode changes from the carrier config.");
            return;
        }
        int i = persistableBundle.getInt("cdma_roaming_mode_int");
        int i2 = Settings.Global.getInt(getContext().getContentResolver(), "roaming_settings", -1);
        if (i == -1) {
            if (i2 != i) {
                logd("cdma_roaming_mode is going to changed to " + i2);
                setCdmaRoamingPreference(i2, obtainMessage(44));
            }
        } else if (i == 0 || i == 1 || i == 2) {
            logd("cdma_roaming_mode is going to changed to " + i);
            setCdmaRoamingPreference(i, obtainMessage(44));
        } else {
            loge("Invalid cdma_roaming_mode settings: " + i);
        }
    }

    public boolean isImsUseEnabled() {
        ImsManager create = this.mImsManagerFactory.create(this.mContext, this.mPhoneId);
        return (create.isVolteEnabledByPlatform() && create.isEnhanced4gLteModeSettingEnabledByUser()) || (create.isWfcEnabledByPlatform() && create.isWfcEnabledByUser() && create.isNonTtyOrTtyOnVolteEnabled());
    }

    @Override // com.android.internal.telephony.Phone
    public InboundSmsHandler getInboundSmsHandler(boolean z) {
        return this.mIccSmsInterfaceManager.getInboundSmsHandler(z);
    }

    @Override // com.android.internal.telephony.Phone
    public List<CellBroadcastIdRange> getCellBroadcastIdRanges() {
        return this.mCellBroadcastConfigTracker.getCellBroadcastIdRanges();
    }

    @Override // com.android.internal.telephony.Phone
    public void setCellBroadcastIdRanges(List<CellBroadcastIdRange> list, Consumer<Integer> consumer) {
        this.mCellBroadcastConfigTracker.setCellBroadcastIdRanges(list, consumer);
    }

    private boolean isRequestBlockedByFDN(SsData.RequestType requestType, SsData.ServiceType serviceType) {
        return FdnUtils.isSuppServiceRequestBlockedByFdn(this.mPhoneId, GsmMmiCode.getControlStrings(requestType, serviceType), getCountryIso());
    }

    @Override // com.android.internal.telephony.Phone
    public void handleNullCipherEnabledChange() {
        if (!DeviceConfig.getBoolean("cellular_security", "enable_null_cipher_toggle", false)) {
            logi("Not handling null cipher update. Feature disabled by DeviceConfig.");
        } else {
            this.mCi.setNullCipherAndIntegrityEnabled(getNullCipherAndIntegrityEnabledPreference(), obtainMessage(66));
        }
    }

    @Override // com.android.internal.telephony.Phone
    public boolean isNullCipherAndIntegritySupported() {
        return this.mIsNullCipherAndIntegritySupported;
    }
}
