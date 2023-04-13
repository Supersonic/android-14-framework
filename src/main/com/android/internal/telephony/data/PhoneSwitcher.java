package com.android.internal.telephony.data;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.compat.annotation.UnsupportedAppUsage;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.MatchAllNetworkSpecifier;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.NetworkSpecifier;
import android.net.TelephonyNetworkSpecifier;
import android.os.AsyncResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.telephony.CarrierConfigManager;
import android.telephony.NetworkRegistrationInfo;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.TelephonyRegistryManager;
import android.telephony.ims.ImsReasonInfo;
import android.telephony.ims.ImsRegistrationAttributes;
import android.telephony.ims.RegistrationManager;
import android.util.ArrayMap;
import com.android.ims.ImsException;
import com.android.ims.ImsManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.CallFailCause;
import com.android.internal.telephony.CommandException;
import com.android.internal.telephony.GbaManager;
import com.android.internal.telephony.ISetOpportunisticDataCallback;
import com.android.internal.telephony.IndentingPrintWriter;
import com.android.internal.telephony.LocalLog;
import com.android.internal.telephony.NetworkFactory;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.RadioConfig;
import com.android.internal.telephony.Registrant;
import com.android.internal.telephony.RegistrantList;
import com.android.internal.telephony.SubscriptionController;
import com.android.internal.telephony.data.CellularNetworkValidator;
import com.android.internal.telephony.data.DataConfigManager;
import com.android.internal.telephony.data.DataNetworkController;
import com.android.internal.telephony.data.DataSettingsManager;
import com.android.internal.telephony.metrics.TelephonyMetrics;
import com.android.internal.telephony.nano.TelephonyProto$TelephonyEvent;
import com.android.internal.telephony.subscription.SubscriptionInfoInternal;
import com.android.internal.telephony.subscription.SubscriptionManagerService;
import com.android.internal.telephony.util.NotificationChannelController;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
/* loaded from: classes.dex */
public class PhoneSwitcher extends Handler {
    protected static final int EVENT_SUBSCRIPTION_CHANGED = 102;
    protected static final int HAL_COMMAND_ALLOW_DATA = 1;
    protected static final int HAL_COMMAND_PREFERRED_DATA = 2;
    protected static final int HAL_COMMAND_UNKNOWN = 0;
    protected static final boolean REQUESTS_CHANGED = true;
    protected static final boolean REQUESTS_UNCHANGED = false;
    protected int mActiveModemCount;
    protected final RegistrantList mActivePhoneRegistrants;
    private long mAutoDataSwitchAvailabilityStabilityTimeThreshold;
    private int mAutoDataSwitchValidationMaxRetry;
    private int mAutoSelectedDataSubId;
    private int mAutoSwitchRetryFailedCount;
    private ConnectivityManager mConnectivityManager;
    protected final Context mContext;
    private List<Set<CommandException.Error>> mCurrentDdsSwitchFailure;
    private final DataConfigManager.DataConfigManagerCallback mDataConfigManagerCallback;
    private final Map<Integer, DataSettingsManager.DataSettingsManagerCallback> mDataSettingsManagerCallbacks;
    private final BroadcastReceiver mDefaultDataChangedReceiver;
    private final DefaultNetworkCallback mDefaultNetworkCallback;
    private boolean mDisplayedAutoSwitchNotification;
    private EmergencyOverrideRequest mEmergencyOverride;
    protected int mHalCommandToUse;
    @VisibleForTesting
    public ImsRegTechProvider mImsRegTechProvider;
    private int mImsRegistrationTech;
    private boolean mIsRegisteredForImsRadioTechChange;
    private int mLastSwitchPreferredDataReason;
    private final LocalLog mLocalLog;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected int mMaxDataAttachModemCount;
    private final DataNetworkController.NetworkRequestList mNetworkRequestList;
    private boolean mPendingSwitchNeedValidation;
    private int mPendingSwitchSubId;
    protected int mPhoneIdInVoiceCall;
    protected PhoneState[] mPhoneStates;
    protected int[] mPhoneSubscriptions;
    @VisibleForTesting
    protected int mPreferredDataPhoneId;
    protected SubscriptionController.WatchedInt mPreferredDataSubId;
    protected int mPrimaryDataSubId;
    protected RadioConfig mRadioConfig;
    private RegistrationManager.RegistrationCallback mRegistrationCallback;
    private boolean mRequirePingTestBeforeDataSwitch;
    private ISetOpportunisticDataCallback mSetOpptSubCallback;
    private BroadcastReceiver mSimStateIntentReceiver;
    protected final SubscriptionController mSubscriptionController;
    private final SubscriptionManagerService mSubscriptionManagerService;
    private final SubscriptionManager.OnSubscriptionsChangedListener mSubscriptionsChangedListener;
    @VisibleForTesting
    public final CellularNetworkValidator.ValidationCallback mValidationCallback;
    @VisibleForTesting
    protected final CellularNetworkValidator mValidator;
    protected static final boolean VDBG = Rlog.isLoggable("PhoneSwitcher", 2);
    @VisibleForTesting
    public static int ECBM_DEFAULT_DATA_SWITCH_BASE_TIME_MS = GbaManager.REQUEST_TIMEOUT_MS;
    @VisibleForTesting
    public static int DEFAULT_DATA_OVERRIDE_TIMEOUT_MS = GbaManager.REQUEST_TIMEOUT_MS;
    protected static PhoneSwitcher sPhoneSwitcher = null;

    /* loaded from: classes.dex */
    public interface ImsRegTechProvider {
        int get(Context context, int i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class EmergencyOverrideRequest {
        int mGnssOverrideTimeMs;
        CompletableFuture<Boolean> mOverrideCompleteFuture;
        boolean mPendingOriginatingCall;
        int mPhoneId;
        boolean mRequiresEcmFinish;

        private EmergencyOverrideRequest() {
            this.mPhoneId = -1;
            this.mGnssOverrideTimeMs = -1;
            this.mRequiresEcmFinish = false;
            this.mPendingOriginatingCall = true;
        }

        boolean isCallbackAvailable() {
            return this.mOverrideCompleteFuture != null;
        }

        void sendOverrideCompleteCallbackResultAndClear(boolean z) {
            if (isCallbackAvailable()) {
                this.mOverrideCompleteFuture.complete(Boolean.valueOf(z));
                this.mOverrideCompleteFuture = null;
            }
        }

        public String toString() {
            return String.format("EmergencyOverrideRequest: [phoneId= %d, overrideMs= %d, hasCallback= %b, ecmFinishStatus= %b]", Integer.valueOf(this.mPhoneId), Integer.valueOf(this.mGnssOverrideTimeMs), Boolean.valueOf(isCallbackAvailable()), Boolean.valueOf(this.mRequiresEcmFinish));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class DefaultNetworkCallback extends ConnectivityManager.NetworkCallback {
        public boolean isDefaultNetworkOnCellular;
        public int mExpectedSubId;
        public int mSwitchReason;

        private DefaultNetworkCallback() {
            this.mExpectedSubId = -1;
            this.mSwitchReason = 0;
            this.isDefaultNetworkOnCellular = false;
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            if (networkCapabilities.hasTransport(0)) {
                this.isDefaultNetworkOnCellular = true;
                if (SubscriptionManager.isValidSubscriptionId(this.mExpectedSubId) && this.mExpectedSubId == PhoneSwitcher.this.getSubIdFromNetworkSpecifier(networkCapabilities.getNetworkSpecifier())) {
                    PhoneSwitcher.this.logDataSwitchEvent(this.mExpectedSubId, 2, this.mSwitchReason);
                    this.mExpectedSubId = -1;
                    this.mSwitchReason = 0;
                }
            } else if (this.isDefaultNetworkOnCellular) {
                this.isDefaultNetworkOnCellular = false;
                PhoneSwitcher.this.log("default network is active on non cellular");
                PhoneSwitcher.this.evaluateIfAutoSwitchIsNeeded();
            }
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public void onLost(Network network) {
            if (PhoneSwitcher.this.hasMessages(111)) {
                return;
            }
            PhoneSwitcher.this.sendEmptyMessage(111);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$new$0(Context context, int i) {
        return ImsManager.getInstance(context, i).getRegistrationTech();
    }

    public static PhoneSwitcher getInstance() {
        return sPhoneSwitcher;
    }

    public static PhoneSwitcher make(int i, Context context, Looper looper) {
        if (sPhoneSwitcher == null) {
            sPhoneSwitcher = new PhoneSwitcher(i, context, looper);
            SubscriptionManager.invalidateActiveDataSubIdCaches();
        }
        return sPhoneSwitcher;
    }

    private boolean updatesIfPhoneInVoiceCallChanged() {
        Phone[] phones;
        int i = this.mPhoneIdInVoiceCall;
        this.mPhoneIdInVoiceCall = -1;
        for (Phone phone : PhoneFactory.getPhones()) {
            if (isPhoneInVoiceCall(phone) || isPhoneInVoiceCall(phone.getImsPhone())) {
                this.mPhoneIdInVoiceCall = phone.getPhoneId();
                break;
            }
        }
        if (this.mPhoneIdInVoiceCall != i) {
            logl("isPhoneInVoiceCallChanged from phoneId " + i + " to phoneId " + this.mPhoneIdInVoiceCall);
            return true;
        }
        return false;
    }

    private void registerForImsRadioTechChange(Context context, int i) {
        try {
            ImsManager.getInstance(context, i).addRegistrationCallback(this.mRegistrationCallback, new PhoneSwitcher$$ExternalSyntheticLambda0(this));
            this.mIsRegisteredForImsRadioTechChange = true;
        } catch (ImsException unused) {
            this.mIsRegisteredForImsRadioTechChange = false;
        }
    }

    private void registerForImsRadioTechChange() {
        if (this.mIsRegisteredForImsRadioTechChange) {
            return;
        }
        for (int i = 0; i < this.mActiveModemCount; i++) {
            registerForImsRadioTechChange(this.mContext, i);
        }
    }

    private void evaluateIfImmediateDataSwitchIsNeeded(String str, int i) {
        if (onEvaluate(false, str)) {
            logDataSwitchEvent(this.mPreferredDataSubId.get(), 1, i);
            registerDefaultNetworkChangeCallback(this.mPreferredDataSubId.get(), i);
        }
    }

    @VisibleForTesting
    public PhoneSwitcher(int i, Context context, Looper looper) {
        super(looper);
        int i2;
        this.mNetworkRequestList = new DataNetworkController.NetworkRequestList();
        this.mPendingSwitchSubId = -1;
        this.mLastSwitchPreferredDataReason = -1;
        this.mDisplayedAutoSwitchNotification = false;
        this.mValidationCallback = new CellularNetworkValidator.ValidationCallback() { // from class: com.android.internal.telephony.data.PhoneSwitcher.1
            @Override // com.android.internal.telephony.data.CellularNetworkValidator.ValidationCallback
            public void onValidationDone(boolean z, int i3) {
                Message.obtain(PhoneSwitcher.this, 110, i3, z ? 1 : 0).sendToTarget();
            }

            @Override // com.android.internal.telephony.data.CellularNetworkValidator.ValidationCallback
            public void onNetworkAvailable(Network network, int i3) {
                Message.obtain(PhoneSwitcher.this, TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_COMPANION_IFACE_IN_USE, i3, 0, network).sendToTarget();
            }
        };
        this.mPrimaryDataSubId = -1;
        this.mAutoSelectedDataSubId = KeepaliveStatus.INVALID_HANDLE;
        this.mAutoSwitchRetryFailedCount = 0;
        this.mPhoneIdInVoiceCall = -1;
        this.mPreferredDataPhoneId = -1;
        this.mPreferredDataSubId = new SubscriptionController.WatchedInt(-1) { // from class: com.android.internal.telephony.data.PhoneSwitcher.2
            @Override // com.android.internal.telephony.SubscriptionController.WatchedInt
            public void set(int i3) {
                super.set(i3);
                SubscriptionManager.invalidateActiveDataSubIdCaches();
            }
        };
        this.mDataConfigManagerCallback = new DataConfigManager.DataConfigManagerCallback(new PhoneSwitcher$$ExternalSyntheticLambda0(this)) { // from class: com.android.internal.telephony.data.PhoneSwitcher.3
            @Override // com.android.internal.telephony.data.DataConfigManager.DataConfigManagerCallback
            public void onCarrierConfigChanged() {
                PhoneSwitcher.this.log("onCarrierConfigChanged");
                PhoneSwitcher.this.updateCarrierConfig();
            }
        };
        this.mHalCommandToUse = 0;
        this.mImsRegistrationTech = -1;
        this.mRequirePingTestBeforeDataSwitch = true;
        this.mAutoDataSwitchAvailabilityStabilityTimeThreshold = -1L;
        this.mDataSettingsManagerCallbacks = new ArrayMap();
        this.mRegistrationCallback = new RegistrationManager.RegistrationCallback() { // from class: com.android.internal.telephony.data.PhoneSwitcher.4
            @Override // android.telephony.ims.RegistrationManager.RegistrationCallback
            public void onRegistered(ImsRegistrationAttributes imsRegistrationAttributes) {
                int registrationTechnology = imsRegistrationAttributes.getRegistrationTechnology();
                if (registrationTechnology != PhoneSwitcher.this.mImsRegistrationTech) {
                    PhoneSwitcher.this.mImsRegistrationTech = registrationTechnology;
                    PhoneSwitcher phoneSwitcher = PhoneSwitcher.this;
                    phoneSwitcher.sendMessage(phoneSwitcher.obtainMessage(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_IFACE_AND_POL_FAMILY_MISMATCH));
                }
            }

            @Override // android.telephony.ims.RegistrationManager.RegistrationCallback
            public void onUnregistered(ImsReasonInfo imsReasonInfo) {
                if (PhoneSwitcher.this.mImsRegistrationTech != -1) {
                    PhoneSwitcher.this.mImsRegistrationTech = -1;
                    PhoneSwitcher phoneSwitcher = PhoneSwitcher.this;
                    phoneSwitcher.sendMessage(phoneSwitcher.obtainMessage(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_IFACE_AND_POL_FAMILY_MISMATCH));
                }
            }
        };
        this.mDefaultNetworkCallback = new DefaultNetworkCallback();
        this.mImsRegTechProvider = new ImsRegTechProvider() { // from class: com.android.internal.telephony.data.PhoneSwitcher$$ExternalSyntheticLambda1
            @Override // com.android.internal.telephony.data.PhoneSwitcher.ImsRegTechProvider
            public final int get(Context context2, int i3) {
                int lambda$new$0;
                lambda$new$0 = PhoneSwitcher.lambda$new$0(context2, i3);
                return lambda$new$0;
            }
        };
        this.mDefaultDataChangedReceiver = new BroadcastReceiver() { // from class: com.android.internal.telephony.data.PhoneSwitcher.6
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                PhoneSwitcher.this.obtainMessage(101).sendToTarget();
            }
        };
        this.mSimStateIntentReceiver = new BroadcastReceiver() { // from class: com.android.internal.telephony.data.PhoneSwitcher.7
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (intent.getAction().equals("android.telephony.action.SIM_APPLICATION_STATE_CHANGED")) {
                    int intExtra = intent.getIntExtra("android.telephony.extra.SIM_STATE", 0);
                    int intExtra2 = intent.getIntExtra("android.telephony.extra.SLOT_INDEX", -1);
                    PhoneSwitcher phoneSwitcher = PhoneSwitcher.this;
                    phoneSwitcher.logl("mSimStateIntentReceiver: slotIndex = " + intExtra2 + " state = " + intExtra);
                    PhoneSwitcher.this.obtainMessage(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_IP_ADDRESS_MISMATCH, intExtra2, intExtra).sendToTarget();
                }
            }
        };
        this.mSubscriptionsChangedListener = new SubscriptionManager.OnSubscriptionsChangedListener() { // from class: com.android.internal.telephony.data.PhoneSwitcher.8
            @Override // android.telephony.SubscriptionManager.OnSubscriptionsChangedListener
            public void onSubscriptionsChanged() {
                PhoneSwitcher.this.obtainMessage(102).sendToTarget();
            }
        };
        this.mContext = context;
        int activeModemCount = getTm().getActiveModemCount();
        this.mActiveModemCount = activeModemCount;
        this.mPhoneSubscriptions = new int[activeModemCount];
        this.mPhoneStates = new PhoneState[activeModemCount];
        this.mMaxDataAttachModemCount = i;
        this.mLocalLog = new LocalLog(CallFailCause.RADIO_UPLINK_FAILURE);
        if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
            this.mSubscriptionManagerService = SubscriptionManagerService.getInstance();
            this.mSubscriptionController = null;
        } else {
            this.mSubscriptionController = SubscriptionController.getInstance();
            this.mSubscriptionManagerService = null;
        }
        this.mRadioConfig = RadioConfig.getInstance();
        this.mValidator = CellularNetworkValidator.getInstance();
        this.mCurrentDdsSwitchFailure = new ArrayList();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.telephony.action.SIM_APPLICATION_STATE_CHANGED");
        context.registerReceiver(this.mSimStateIntentReceiver, intentFilter);
        this.mActivePhoneRegistrants = new RegistrantList();
        int i3 = 0;
        while (true) {
            i2 = this.mActiveModemCount;
            if (i3 >= i2) {
                break;
            }
            this.mPhoneStates[i3] = new PhoneState();
            Phone phone = PhoneFactory.getPhone(i3);
            if (phone != null) {
                phone.registerForEmergencyCallToggle(this, 105, null);
                phone.registerForPreciseCallStateChanged(this, 109, null);
                if (phone.getImsPhone() != null) {
                    phone.getImsPhone().registerForPreciseCallStateChanged(this, 109, null);
                }
                this.mDataSettingsManagerCallbacks.computeIfAbsent(Integer.valueOf(i3), new Function() { // from class: com.android.internal.telephony.data.PhoneSwitcher$$ExternalSyntheticLambda2
                    @Override // java.util.function.Function
                    public final Object apply(Object obj) {
                        DataSettingsManager.DataSettingsManagerCallback lambda$new$1;
                        lambda$new$1 = PhoneSwitcher.this.lambda$new$1((Integer) obj);
                        return lambda$new$1;
                    }
                });
                phone.getDataSettingsManager().registerCallback(this.mDataSettingsManagerCallbacks.get(Integer.valueOf(i3)));
                phone.getServiceStateTracker().registerForServiceStateChanged(this, TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_INTERNAL_CALL_PREEMPT_BY_HIGH_PRIO_APN, Integer.valueOf(i3));
                registerForImsRadioTechChange(context, i3);
            }
            this.mCurrentDdsSwitchFailure.add(new HashSet());
            i3++;
        }
        if (i2 > 0) {
            PhoneFactory.getPhone(0).mCi.registerForOn(this, 108, null);
        }
        SubscriptionManager.OnSubscriptionsChangedListener onSubscriptionsChangedListener = this.mSubscriptionsChangedListener;
        ((TelephonyRegistryManager) context.getSystemService("telephony_registry")).addOnSubscriptionsChangedListener(onSubscriptionsChangedListener, onSubscriptionsChangedListener.getHandlerExecutor());
        this.mConnectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        this.mContext.registerReceiver(this.mDefaultDataChangedReceiver, new IntentFilter("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED"));
        PhoneConfigurationManager.registerForMultiSimConfigChange(this, TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_IFACE_MISMATCH, null);
        this.mConnectivityManager.registerDefaultNetworkCallback(this.mDefaultNetworkCallback, this);
        new PhoneSwitcherNetworkRequestListener(looper, context, new NetworkCapabilities.Builder().addTransportType(0).addCapability(0).addCapability(1).addCapability(2).addCapability(3).addCapability(4).addCapability(5).addCapability(7).addCapability(8).addCapability(33).addCapability(9).addCapability(29).addCapability(10).addCapability(13).addCapability(12).addCapability(23).addCapability(34).addCapability(35).addEnterpriseId(1).addEnterpriseId(2).addEnterpriseId(3).addEnterpriseId(4).addEnterpriseId(5).setNetworkSpecifier(new MatchAllNetworkSpecifier()).build(), this).registerIgnoringScore();
        updateHalCommandToUse();
        logl("PhoneSwitcher started");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ DataSettingsManager.DataSettingsManagerCallback lambda$new$1(Integer num) {
        return new DataSettingsManager.DataSettingsManagerCallback(new PhoneSwitcher$$ExternalSyntheticLambda0(this)) { // from class: com.android.internal.telephony.data.PhoneSwitcher.5
            @Override // com.android.internal.telephony.data.DataSettingsManager.DataSettingsManagerCallback
            public void onDataEnabledChanged(boolean z, int i, String str) {
                PhoneSwitcher.this.onDataEnabledChanged();
            }
        };
    }

    private boolean isSimApplicationReady(int i) {
        SubscriptionInfo activeSubscriptionInfoForSimSlotIndex;
        if (SubscriptionManager.isValidSlotIndex(i)) {
            if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
                activeSubscriptionInfoForSimSlotIndex = this.mSubscriptionManagerService.getActiveSubscriptionInfoForSimSlotIndex(i, this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
            } else {
                activeSubscriptionInfoForSimSlotIndex = this.mSubscriptionController.getActiveSubscriptionInfoForSimSlotIndex(i, this.mContext.getOpPackageName(), null);
            }
            boolean z = activeSubscriptionInfoForSimSlotIndex != null && activeSubscriptionInfoForSimSlotIndex.areUiccApplicationsEnabled();
            if (PhoneFactory.getPhone(i).getIccCard().isEmptyProfile() || !z) {
                return false;
            }
            logl("isSimApplicationReady: SIM is ready for slotIndex: " + i);
            return true;
        }
        return false;
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        switch (message.what) {
            case 101:
                evaluateIfImmediateDataSwitchIsNeeded("primary data sub changed", 1);
                return;
            case 102:
                onEvaluate(false, "subscription changed");
                return;
            case 103:
                onRequestNetwork((NetworkRequest) message.obj);
                return;
            case 104:
                onReleaseNetwork((NetworkRequest) message.obj);
                return;
            case 105:
                boolean isInEmergencyCallbackMode = isInEmergencyCallbackMode();
                if (this.mEmergencyOverride != null) {
                    logl("Emergency override - ecbm status = " + isInEmergencyCallbackMode);
                    if (isInEmergencyCallbackMode) {
                        removeMessages(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_EMERGENCY_IFACE_ONLY);
                        this.mEmergencyOverride.mRequiresEcmFinish = true;
                    } else if (this.mEmergencyOverride.mRequiresEcmFinish) {
                        sendMessageDelayed(obtainMessage(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_EMERGENCY_IFACE_ONLY), this.mEmergencyOverride.mGnssOverrideTimeMs);
                    }
                }
                onEvaluate(true, "emergencyToggle");
                return;
            case 106:
                sendRilCommands(message.arg1);
                return;
            case 107:
                setOpportunisticDataSubscription(message.arg1, message.arg2 == 1, (ISetOpportunisticDataCallback) message.obj);
                return;
            case 108:
                updateHalCommandToUse();
                onEvaluate(false, "EVENT_RADIO_ON");
                return;
            case 109:
                registerForImsRadioTechChange();
                if (updatesIfPhoneInVoiceCallChanged()) {
                    if (!isAnyVoiceCallActiveOnDevice()) {
                        for (int i = 0; i < this.mActiveModemCount; i++) {
                            if (this.mCurrentDdsSwitchFailure.get(i).contains(CommandException.Error.OP_NOT_ALLOWED_DURING_VOICE_CALL) && isPhoneIdValidForRetry(i)) {
                                sendRilCommands(i);
                            }
                        }
                    }
                    EmergencyOverrideRequest emergencyOverrideRequest = this.mEmergencyOverride;
                    if (emergencyOverrideRequest != null && emergencyOverrideRequest.mPendingOriginatingCall) {
                        removeMessages(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_EMERGENCY_IFACE_ONLY);
                        if (this.mPhoneIdInVoiceCall == -1) {
                            sendMessageDelayed(obtainMessage(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_EMERGENCY_IFACE_ONLY), this.mEmergencyOverride.mGnssOverrideTimeMs + ECBM_DEFAULT_DATA_SWITCH_BASE_TIME_MS);
                            this.mEmergencyOverride.mPendingOriginatingCall = false;
                        }
                    }
                    evaluateIfImmediateDataSwitchIsNeeded("precise call state changed", 2);
                    if (isAnyVoiceCallActiveOnDevice()) {
                        return;
                    }
                    evaluateIfAutoSwitchIsNeeded();
                    return;
                }
                return;
            case 110:
                onValidationDone(message.arg1, message.arg2 == 1);
                return;
            case 111:
                evaluateIfAutoSwitchIsNeeded();
                return;
            case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_APN_TYPE_CONFLICT /* 112 */:
                onDdsSwitchResponse((AsyncResult) message.obj);
                return;
            case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_INVALID_PCSCF_ADDR /* 113 */:
                int intValue = ((Integer) message.obj).intValue();
                if (isPhoneIdValidForRetry(intValue)) {
                    logl("EVENT_MODEM_COMMAND_RETRY: resend modem command on phone " + intValue);
                    sendRilCommands(intValue);
                    return;
                }
                logl("EVENT_MODEM_COMMAND_RETRY: skip retry as DDS sub changed");
                this.mCurrentDdsSwitchFailure.get(intValue).clear();
                return;
            case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_INTERNAL_CALL_PREEMPT_BY_HIGH_PRIO_APN /* 114 */:
                onServiceStateChanged(((Integer) ((AsyncResult) message.obj).userObj).intValue());
                return;
            case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_EMM_ACCESS_BARRED /* 115 */:
                EmergencyOverrideRequest emergencyOverrideRequest2 = (EmergencyOverrideRequest) message.obj;
                EmergencyOverrideRequest emergencyOverrideRequest3 = this.mEmergencyOverride;
                if (emergencyOverrideRequest3 != null) {
                    if (emergencyOverrideRequest3.mPhoneId != emergencyOverrideRequest2.mPhoneId) {
                        logl("emergency override requested for phone id " + emergencyOverrideRequest2.mPhoneId + " when there is already an override in place for phone id " + this.mEmergencyOverride.mPhoneId + ". Ignoring.");
                        if (emergencyOverrideRequest2.isCallbackAvailable()) {
                            emergencyOverrideRequest2.mOverrideCompleteFuture.complete(Boolean.FALSE);
                            return;
                        }
                        return;
                    }
                    if (emergencyOverrideRequest3.isCallbackAvailable()) {
                        this.mEmergencyOverride.mOverrideCompleteFuture.complete(Boolean.FALSE);
                    }
                    this.mEmergencyOverride = emergencyOverrideRequest2;
                } else {
                    this.mEmergencyOverride = emergencyOverrideRequest2;
                }
                logl("new emergency override - " + this.mEmergencyOverride);
                removeMessages(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_EMERGENCY_IFACE_ONLY);
                sendMessageDelayed(obtainMessage(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_EMERGENCY_IFACE_ONLY), (long) DEFAULT_DATA_OVERRIDE_TIMEOUT_MS);
                if (onEvaluate(false, "emer_override_dds")) {
                    return;
                }
                this.mEmergencyOverride.sendOverrideCompleteCallbackResultAndClear(true);
                return;
            case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_EMERGENCY_IFACE_ONLY /* 116 */:
                logl("Emergency override removed - " + this.mEmergencyOverride);
                this.mEmergencyOverride = null;
                onEvaluate(false, "emer_rm_override_dds");
                return;
            case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_IFACE_MISMATCH /* 117 */:
                onMultiSimConfigChanged(((Integer) ((AsyncResult) message.obj).result).intValue());
                return;
            case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_COMPANION_IFACE_IN_USE /* 118 */:
                onNetworkAvailable(message.arg1, (Network) message.obj);
                return;
            case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_IP_ADDRESS_MISMATCH /* 119 */:
                int i2 = message.arg1;
                int i3 = message.arg2;
                if (!SubscriptionManager.isValidSlotIndex(i2)) {
                    logl("EVENT_PROCESS_SIM_STATE_CHANGE: skip processing due to invalid slotId: " + i2);
                } else if (this.mCurrentDdsSwitchFailure.get(i2).contains(CommandException.Error.INVALID_SIM_STATE) && 10 == i3 && isSimApplicationReady(i2)) {
                    sendRilCommands(i2);
                }
                registerConfigChange();
                return;
            case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_IFACE_AND_POL_FAMILY_MISMATCH /* 120 */:
                registerForImsRadioTechChange();
                if (updatesIfPhoneInVoiceCallChanged() || isAnyVoiceCallActiveOnDevice()) {
                    evaluateIfImmediateDataSwitchIsNeeded("Ims radio tech changed", 2);
                    return;
                }
                return;
            case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_EMM_ACCESS_BARRED_INFINITE_RETRY /* 121 */:
                validate(message.arg1, ((Boolean) message.obj).booleanValue(), 4, null);
                return;
            default:
                return;
        }
    }

    private void registerConfigChange() {
        Phone phoneBySubId = getPhoneBySubId(this.mPrimaryDataSubId);
        if (phoneBySubId != null) {
            phoneBySubId.getDataNetworkController().getDataConfigManager().registerCallback(this.mDataConfigManagerCallback);
            updateCarrierConfig();
            sendEmptyMessage(111);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCarrierConfig() {
        Phone phoneBySubId = getPhoneBySubId(this.mPrimaryDataSubId);
        if (phoneBySubId != null) {
            DataConfigManager dataConfigManager = phoneBySubId.getDataNetworkController().getDataConfigManager();
            this.mRequirePingTestBeforeDataSwitch = dataConfigManager.requirePingTestBeforeDataSwitch();
            this.mAutoDataSwitchAvailabilityStabilityTimeThreshold = dataConfigManager.getAutoDataSwitchAvailabilityStabilityTimeThreshold();
            this.mAutoDataSwitchValidationMaxRetry = dataConfigManager.getAutoDataSwitchValidationMaxRetry();
        }
    }

    private synchronized void onMultiSimConfigChanged(int i) {
        int i2 = this.mActiveModemCount;
        if (i2 == i) {
            return;
        }
        this.mActiveModemCount = i;
        this.mPhoneSubscriptions = Arrays.copyOf(this.mPhoneSubscriptions, i);
        this.mPhoneStates = (PhoneState[]) Arrays.copyOf(this.mPhoneStates, this.mActiveModemCount);
        for (int i3 = i2 - 1; i3 >= this.mActiveModemCount; i3--) {
            this.mCurrentDdsSwitchFailure.remove(i3);
        }
        while (i2 < this.mActiveModemCount) {
            this.mPhoneStates[i2] = new PhoneState();
            Phone phone = PhoneFactory.getPhone(i2);
            if (phone != null) {
                phone.registerForEmergencyCallToggle(this, 105, null);
                phone.registerForPreciseCallStateChanged(this, 109, null);
                if (phone.getImsPhone() != null) {
                    phone.getImsPhone().registerForPreciseCallStateChanged(this, 109, null);
                }
                this.mDataSettingsManagerCallbacks.computeIfAbsent(Integer.valueOf(phone.getPhoneId()), new Function() { // from class: com.android.internal.telephony.data.PhoneSwitcher$$ExternalSyntheticLambda3
                    @Override // java.util.function.Function
                    public final Object apply(Object obj) {
                        DataSettingsManager.DataSettingsManagerCallback lambda$onMultiSimConfigChanged$2;
                        lambda$onMultiSimConfigChanged$2 = PhoneSwitcher.this.lambda$onMultiSimConfigChanged$2((Integer) obj);
                        return lambda$onMultiSimConfigChanged$2;
                    }
                });
                phone.getDataSettingsManager().registerCallback(this.mDataSettingsManagerCallbacks.get(Integer.valueOf(phone.getPhoneId())));
                phone.getServiceStateTracker().registerForServiceStateChanged(this, TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_INTERNAL_CALL_PREEMPT_BY_HIGH_PRIO_APN, Integer.valueOf(i2));
                this.mCurrentDdsSwitchFailure.add(new HashSet());
                registerForImsRadioTechChange(this.mContext, i2);
            }
            i2++;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ DataSettingsManager.DataSettingsManagerCallback lambda$onMultiSimConfigChanged$2(Integer num) {
        return new DataSettingsManager.DataSettingsManagerCallback(new PhoneSwitcher$$ExternalSyntheticLambda0(this)) { // from class: com.android.internal.telephony.data.PhoneSwitcher.9
            @Override // com.android.internal.telephony.data.DataSettingsManager.DataSettingsManagerCallback
            public void onDataEnabledChanged(boolean z, int i, String str) {
                PhoneSwitcher.this.onDataEnabledChanged();
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDataEnabledChanged() {
        logl("user changed data related settings");
        if (isAnyVoiceCallActiveOnDevice()) {
            evaluateIfImmediateDataSwitchIsNeeded("user changed data settings during call", 2);
        } else {
            evaluateIfAutoSwitchIsNeeded();
        }
    }

    private boolean isInEmergencyCallbackMode() {
        Phone[] phones;
        for (Phone phone : PhoneFactory.getPhones()) {
            if (phone != null) {
                if (phone.isInEcm()) {
                    return true;
                }
                Phone imsPhone = phone.getImsPhone();
                if (imsPhone != null && imsPhone.isInEcm()) {
                    return true;
                }
            }
        }
        return false;
    }

    /* loaded from: classes.dex */
    private static class PhoneSwitcherNetworkRequestListener extends NetworkFactory {
        private final PhoneSwitcher mPhoneSwitcher;

        public PhoneSwitcherNetworkRequestListener(Looper looper, Context context, NetworkCapabilities networkCapabilities, PhoneSwitcher phoneSwitcher) {
            super(looper, context, "PhoneSwitcherNetworkRequstListener", networkCapabilities);
            this.mPhoneSwitcher = phoneSwitcher;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.internal.telephony.NetworkFactory
        public void needNetworkFor(NetworkRequest networkRequest) {
            if (PhoneSwitcher.VDBG) {
                log("needNetworkFor " + networkRequest);
            }
            Message obtainMessage = this.mPhoneSwitcher.obtainMessage(103);
            obtainMessage.obj = networkRequest;
            obtainMessage.sendToTarget();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.internal.telephony.NetworkFactory
        public void releaseNetworkFor(NetworkRequest networkRequest) {
            if (PhoneSwitcher.VDBG) {
                log("releaseNetworkFor " + networkRequest);
            }
            Message obtainMessage = this.mPhoneSwitcher.obtainMessage(104);
            obtainMessage.obj = networkRequest;
            obtainMessage.sendToTarget();
        }
    }

    private void onRequestNetwork(NetworkRequest networkRequest) {
        TelephonyNetworkRequest telephonyNetworkRequest = new TelephonyNetworkRequest(networkRequest, PhoneFactory.getDefaultPhone());
        if (this.mNetworkRequestList.contains(telephonyNetworkRequest)) {
            return;
        }
        this.mNetworkRequestList.add(telephonyNetworkRequest);
        onEvaluate(true, "netRequest");
    }

    private void onReleaseNetwork(NetworkRequest networkRequest) {
        if (this.mNetworkRequestList.remove(new TelephonyNetworkRequest(networkRequest, PhoneFactory.getDefaultPhone()))) {
            onEvaluate(true, "netReleased");
            collectReleaseNetworkMetrics(networkRequest);
        }
    }

    private void registerDefaultNetworkChangeCallback(int i, int i2) {
        DefaultNetworkCallback defaultNetworkCallback = this.mDefaultNetworkCallback;
        defaultNetworkCallback.mExpectedSubId = i;
        defaultNetworkCallback.mSwitchReason = i2;
    }

    private void collectReleaseNetworkMetrics(NetworkRequest networkRequest) {
        if (this.mActiveModemCount <= 1 || !networkRequest.hasCapability(0)) {
            return;
        }
        TelephonyProto$TelephonyEvent.OnDemandDataSwitch onDemandDataSwitch = new TelephonyProto$TelephonyEvent.OnDemandDataSwitch();
        onDemandDataSwitch.apn = 2;
        onDemandDataSwitch.state = 2;
        TelephonyMetrics.getInstance().writeOnDemandDataSwitch(onDemandDataSwitch);
    }

    private void onServiceStateChanged(int i) {
        Phone findPhoneById = findPhoneById(i);
        if (findPhoneById != null) {
            int registrationState = findPhoneById.getServiceState().getNetworkRegistrationInfo(2, 1).getRegistrationState();
            PhoneState phoneState = this.mPhoneStates[i];
            if (registrationState != phoneState.dataRegState) {
                phoneState.dataRegState = registrationState;
                logl("onServiceStateChanged: phoneId:" + i + " dataReg-> " + NetworkRegistrationInfo.registrationStateToString(registrationState));
                if (hasMessages(111)) {
                    return;
                }
                sendEmptyMessage(111);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void evaluateIfAutoSwitchIsNeeded() {
        if (this.mAutoDataSwitchAvailabilityStabilityTimeThreshold < 0) {
            return;
        }
        if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
            if (!isActiveSubId(this.mPrimaryDataSubId) || this.mSubscriptionManagerService.getActiveSubIdList(true).length <= 1) {
                return;
            }
        } else if (!isActiveSubId(this.mPrimaryDataSubId) || this.mSubscriptionController.getActiveSubIdList(true).length <= 1) {
            return;
        }
        Phone phoneBySubId = getPhoneBySubId(this.mPrimaryDataSubId);
        if (phoneBySubId == null) {
            loge("evaluateIfAutoSwitchIsNeeded: cannot find primary data phone. subId=" + this.mPrimaryDataSubId);
            return;
        }
        int phoneId = phoneBySubId.getPhoneId();
        log("evaluateIfAutoSwitchIsNeeded: primaryPhoneId: " + phoneId + " preferredPhoneId: " + this.mPreferredDataPhoneId);
        int i = this.mPreferredDataPhoneId;
        if (i == phoneId) {
            int autoSwitchTargetSubIdIfExists = getAutoSwitchTargetSubIdIfExists();
            if (autoSwitchTargetSubIdIfExists != -1) {
                startAutoDataSwitchStabilityCheck(autoSwitchTargetSubIdIfExists, this.mRequirePingTestBeforeDataSwitch);
                return;
            } else {
                cancelPendingAutoDataSwitch();
                return;
            }
        }
        Phone findPhoneById = findPhoneById(i);
        if (findPhoneById != null) {
            if (!phoneBySubId.isUserDataEnabled() || !findPhoneById.isDataAllowed()) {
                this.mAutoSelectedDataSubId = KeepaliveStatus.INVALID_HANDLE;
                evaluateIfImmediateDataSwitchIsNeeded("User disabled data settings", 1);
                return;
            }
            ConnectivityManager connectivityManager = this.mConnectivityManager;
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            if (networkCapabilities != null && !networkCapabilities.hasTransport(0)) {
                log("evaluateIfAutoSwitchIsNeeded: Default network is active on non-cellular transport");
                startAutoDataSwitchStabilityCheck(KeepaliveStatus.INVALID_HANDLE, false);
            } else if (this.mPhoneStates[findPhoneById.getPhoneId()].dataRegState != 1) {
                startAutoDataSwitchStabilityCheck(KeepaliveStatus.INVALID_HANDLE, false);
            } else if (isInService(this.mPhoneStates[phoneId])) {
                startAutoDataSwitchStabilityCheck(KeepaliveStatus.INVALID_HANDLE, this.mRequirePingTestBeforeDataSwitch);
            } else {
                cancelPendingAutoDataSwitch();
            }
        }
    }

    private boolean isInService(PhoneState phoneState) {
        int i = phoneState.dataRegState;
        return i == 1 || i == 5;
    }

    private void startAutoDataSwitchStabilityCheck(int i, boolean z) {
        log("startAutoDataSwitchStabilityCheck: targetSubId=" + i + " needValidation=" + z);
        if (hasMessages(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_EMM_ACCESS_BARRED_INFINITE_RETRY, Boolean.valueOf(z))) {
            return;
        }
        sendMessageDelayed(obtainMessage(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_EMM_ACCESS_BARRED_INFINITE_RETRY, i, 0, Boolean.valueOf(z)), this.mAutoDataSwitchAvailabilityStabilityTimeThreshold);
    }

    private void cancelPendingAutoDataSwitch() {
        this.mAutoSwitchRetryFailedCount = 0;
        removeMessages(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_EMM_ACCESS_BARRED_INFINITE_RETRY);
        if (this.mValidator.isValidating()) {
            this.mValidator.lambda$reportValidationResult$1();
            removeMessages(110);
            removeMessages(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_COMPANION_IFACE_IN_USE);
            this.mPendingSwitchSubId = -1;
            this.mPendingSwitchNeedValidation = false;
        }
    }

    private int getAutoSwitchTargetSubIdIfExists() {
        Phone phoneBySubId = getPhoneBySubId(this.mPrimaryDataSubId);
        if (phoneBySubId == null) {
            log("getAutoSwitchTargetSubId: no sim loaded");
            return -1;
        }
        int phoneId = phoneBySubId.getPhoneId();
        if (!phoneBySubId.isUserDataEnabled()) {
            log("getAutoSwitchTargetSubId: user disabled data");
            return -1;
        }
        ConnectivityManager connectivityManager = this.mConnectivityManager;
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        int i = 0;
        if (networkCapabilities != null && !networkCapabilities.hasTransport(0)) {
            log("getAutoSwitchTargetSubId: Default network is active on non-cellular transport");
            return -1;
        } else if (isInService(this.mPhoneStates[phoneId])) {
            log("getAutoSwitchTargetSubId: primary is in service");
            return -1;
        } else {
            while (true) {
                PhoneState[] phoneStateArr = this.mPhoneStates;
                if (i >= phoneStateArr.length) {
                    return -1;
                }
                if (i != phoneId && phoneStateArr[i].dataRegState == 1) {
                    log("getAutoSwitchTargetSubId: found phone " + i + " in HOME service");
                    Phone findPhoneById = findPhoneById(i);
                    if (findPhoneById != null && findPhoneById.isDataAllowed()) {
                        return findPhoneById.getSubId();
                    }
                }
                i++;
            }
        }
    }

    private TelephonyManager getTm() {
        return (TelephonyManager) this.mContext.getSystemService("phone");
    }

    protected boolean onEvaluate(boolean z, String str) {
        int defaultDataSubId;
        StringBuilder sb = new StringBuilder(str);
        int i = 0;
        boolean z2 = this.mHalCommandToUse != 2 && z;
        if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
            defaultDataSubId = this.mSubscriptionManagerService.getDefaultDataSubId();
        } else {
            defaultDataSubId = this.mSubscriptionController.getDefaultDataSubId();
        }
        if (defaultDataSubId != this.mPrimaryDataSubId) {
            sb.append(" mPrimaryDataSubId ");
            sb.append(this.mPrimaryDataSubId);
            sb.append("->");
            sb.append(defaultDataSubId);
            this.mPrimaryDataSubId = defaultDataSubId;
            this.mLastSwitchPreferredDataReason = 1;
        }
        boolean z3 = false;
        for (int i2 = 0; i2 < this.mActiveModemCount; i2++) {
            int subscriptionId = SubscriptionManager.getSubscriptionId(i2);
            if (SubscriptionManager.isValidSubscriptionId(subscriptionId)) {
                z3 = true;
            }
            if (subscriptionId != this.mPhoneSubscriptions[i2]) {
                sb.append(" phone[");
                sb.append(i2);
                sb.append("] ");
                sb.append(this.mPhoneSubscriptions[i2]);
                sb.append("->");
                sb.append(subscriptionId);
                int i3 = this.mAutoSelectedDataSubId;
                int[] iArr = this.mPhoneSubscriptions;
                if (i3 == iArr[i2]) {
                    this.mAutoSelectedDataSubId = KeepaliveStatus.INVALID_HANDLE;
                }
                iArr[i2] = subscriptionId;
                if (SubscriptionManager.isValidSubscriptionId(subscriptionId)) {
                    registerForImsRadioTechChange(this.mContext, i2);
                }
                z2 = true;
            }
        }
        if (!z3) {
            transitionToEmergencyPhone();
        } else if (VDBG) {
            log("Found an active subscription");
        }
        int i4 = this.mPreferredDataPhoneId;
        int i5 = this.mPreferredDataSubId.get();
        if (z3) {
            updatePreferredDataPhoneId();
        }
        if (i4 != this.mPreferredDataPhoneId) {
            sb.append(" preferred data phoneId ");
            sb.append(i4);
            sb.append("->");
            sb.append(this.mPreferredDataPhoneId);
            z2 = true;
        } else if (i5 != this.mPreferredDataSubId.get()) {
            logl("SIM refresh, notify dds change");
            notifyPreferredDataSubIdChanged();
        }
        if (z2 || "EVENT_RADIO_ON".equals(str)) {
            logl("evaluating due to " + ((Object) sb));
            if (this.mHalCommandToUse == 2) {
                while (i < this.mActiveModemCount) {
                    this.mPhoneStates[i].active = true;
                    i++;
                }
                sendRilCommands(this.mPreferredDataPhoneId);
            } else {
                ArrayList<Integer> arrayList = new ArrayList();
                if (this.mMaxDataAttachModemCount == this.mActiveModemCount) {
                    for (int i6 = 0; i6 < this.mMaxDataAttachModemCount; i6++) {
                        arrayList.add(Integer.valueOf(i6));
                    }
                } else {
                    int i7 = this.mPhoneIdInVoiceCall;
                    if (i7 != -1) {
                        arrayList.add(Integer.valueOf(i7));
                    }
                    if (arrayList.size() < this.mMaxDataAttachModemCount) {
                        Iterator<TelephonyNetworkRequest> it = this.mNetworkRequestList.iterator();
                        while (it.hasNext()) {
                            int phoneIdForRequest = phoneIdForRequest(it.next());
                            if (phoneIdForRequest != -1 && !arrayList.contains(Integer.valueOf(phoneIdForRequest))) {
                                arrayList.add(Integer.valueOf(phoneIdForRequest));
                                if (arrayList.size() >= this.mMaxDataAttachModemCount) {
                                    break;
                                }
                            }
                        }
                    }
                    if (arrayList.size() < this.mMaxDataAttachModemCount && !arrayList.contains(Integer.valueOf(this.mPreferredDataPhoneId)) && SubscriptionManager.isUsableSubIdValue(this.mPreferredDataPhoneId)) {
                        arrayList.add(Integer.valueOf(this.mPreferredDataPhoneId));
                    }
                }
                if (VDBG) {
                    log("mPrimaryDataSubId = " + this.mPrimaryDataSubId);
                    log("mAutoSelectedDataSubId = " + this.mAutoSelectedDataSubId);
                    for (int i8 = 0; i8 < this.mActiveModemCount; i8++) {
                        log(" phone[" + i8 + "] using sub[" + this.mPhoneSubscriptions[i8] + "]");
                    }
                    log(" newActivePhones:");
                    Iterator it2 = arrayList.iterator();
                    while (it2.hasNext()) {
                        log("  " + ((Integer) it2.next()));
                    }
                }
                while (i < this.mActiveModemCount) {
                    if (!arrayList.contains(Integer.valueOf(i))) {
                        deactivate(i);
                    }
                    i++;
                }
                for (Integer num : arrayList) {
                    activate(num.intValue());
                }
            }
        }
        return z2;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class PhoneState {
        public volatile boolean active = false;
        public int dataRegState = 0;
        public long lastRequested = 0;

        protected PhoneState() {
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected void activate(int i) {
        switchPhone(i, true);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected void deactivate(int i) {
        switchPhone(i, false);
    }

    private void switchPhone(int i, boolean z) {
        PhoneState phoneState = this.mPhoneStates[i];
        if (phoneState.active == z) {
            return;
        }
        phoneState.active = z;
        StringBuilder sb = new StringBuilder();
        sb.append(z ? "activate " : "deactivate ");
        sb.append(i);
        logl(sb.toString());
        phoneState.lastRequested = System.currentTimeMillis();
        sendRilCommands(i);
    }

    public void onRadioCapChanged(int i) {
        if (SubscriptionManager.isValidPhoneId(i)) {
            Message obtainMessage = obtainMessage(106);
            obtainMessage.arg1 = i;
            obtainMessage.sendToTarget();
        }
    }

    public void overrideDefaultDataForEmergency(int i, int i2, CompletableFuture<Boolean> completableFuture) {
        if (SubscriptionManager.isValidPhoneId(i)) {
            Message obtainMessage = obtainMessage(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_EMM_ACCESS_BARRED);
            EmergencyOverrideRequest emergencyOverrideRequest = new EmergencyOverrideRequest();
            emergencyOverrideRequest.mPhoneId = i;
            emergencyOverrideRequest.mGnssOverrideTimeMs = i2 * 1000;
            emergencyOverrideRequest.mOverrideCompleteFuture = completableFuture;
            obtainMessage.obj = emergencyOverrideRequest;
            obtainMessage.sendToTarget();
        }
    }

    protected void sendRilCommands(int i) {
        if (!SubscriptionManager.isValidPhoneId(i)) {
            logl("sendRilCommands: skip dds switch due to invalid phoneId=" + i);
            return;
        }
        Message obtain = Message.obtain(this, TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_APN_TYPE_CONFLICT, Integer.valueOf(i));
        int i2 = this.mHalCommandToUse;
        if (i2 == 1 || i2 == 0) {
            if (this.mActiveModemCount > 1) {
                PhoneFactory.getPhone(i).mCi.setDataAllowed(isPhoneActive(i), obtain);
            }
        } else if (i == this.mPreferredDataPhoneId) {
            logl("sendRilCommands: setPreferredDataModem - phoneId: " + i);
            this.mRadioConfig.setPreferredDataModem(this.mPreferredDataPhoneId, obtain);
        }
    }

    private int phoneIdForRequest(TelephonyNetworkRequest telephonyNetworkRequest) {
        NetworkRequest nativeNetworkRequest = telephonyNetworkRequest.getNativeNetworkRequest();
        int subIdFromNetworkSpecifier = getSubIdFromNetworkSpecifier(nativeNetworkRequest.getNetworkSpecifier());
        if (subIdFromNetworkSpecifier == Integer.MAX_VALUE) {
            return this.mPreferredDataPhoneId;
        }
        if (subIdFromNetworkSpecifier == -1) {
            return -1;
        }
        int i = this.mPreferredDataPhoneId;
        int i2 = (i < 0 || i >= this.mActiveModemCount) ? -1 : this.mPhoneSubscriptions[i];
        if (!nativeNetworkRequest.hasCapability(12) || !nativeNetworkRequest.hasCapability(13) || subIdFromNetworkSpecifier == i2 || subIdFromNetworkSpecifier == this.mValidator.getSubIdInValidation()) {
            for (int i3 = 0; i3 < this.mActiveModemCount; i3++) {
                if (this.mPhoneSubscriptions[i3] == subIdFromNetworkSpecifier) {
                    return i3;
                }
            }
            return -1;
        }
        return -1;
    }

    protected int getSubIdFromNetworkSpecifier(NetworkSpecifier networkSpecifier) {
        if (networkSpecifier == null) {
            return KeepaliveStatus.INVALID_HANDLE;
        }
        if (networkSpecifier instanceof TelephonyNetworkSpecifier) {
            return ((TelephonyNetworkSpecifier) networkSpecifier).getSubscriptionId();
        }
        return -1;
    }

    private boolean isActiveSubId(int i) {
        if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
            SubscriptionInfoInternal subscriptionInfoInternal = this.mSubscriptionManagerService.getSubscriptionInfoInternal(i);
            return subscriptionInfoInternal != null && subscriptionInfoInternal.isActive();
        }
        return this.mSubscriptionController.isActiveSubId(i);
    }

    protected void updatePreferredDataPhoneId() {
        EmergencyOverrideRequest emergencyOverrideRequest = this.mEmergencyOverride;
        if (emergencyOverrideRequest != null && findPhoneById(emergencyOverrideRequest.mPhoneId) != null) {
            logl("updatePreferredDataPhoneId: preferred data overridden for emergency. phoneId = " + this.mEmergencyOverride.mPhoneId);
            this.mPreferredDataPhoneId = this.mEmergencyOverride.mPhoneId;
            this.mLastSwitchPreferredDataReason = 0;
        } else {
            int i = this.mImsRegTechProvider.get(this.mContext, this.mPhoneIdInVoiceCall);
            if (!isAnyVoiceCallActiveOnDevice() || i == 1) {
                this.mPreferredDataPhoneId = getFallbackDataPhoneIdForInternetRequests();
            } else if (i != 2) {
                this.mPreferredDataPhoneId = shouldSwitchDataDueToInCall() ? this.mPhoneIdInVoiceCall : getFallbackDataPhoneIdForInternetRequests();
            } else {
                logl("IMS call on cross-SIM, skip switching data to phone " + this.mPhoneIdInVoiceCall);
            }
        }
        this.mPreferredDataSubId.set(SubscriptionManager.getSubscriptionId(this.mPreferredDataPhoneId));
    }

    private int getFallbackDataPhoneIdForInternetRequests() {
        int i = isActiveSubId(this.mAutoSelectedDataSubId) ? this.mAutoSelectedDataSubId : this.mPrimaryDataSubId;
        if (SubscriptionManager.isUsableSubIdValue(i)) {
            for (int i2 = 0; i2 < this.mActiveModemCount; i2++) {
                if (this.mPhoneSubscriptions[i2] == i) {
                    return i2;
                }
            }
            return -1;
        }
        return -1;
    }

    private boolean shouldSwitchDataDueToInCall() {
        Phone findPhoneById = findPhoneById(this.mPhoneIdInVoiceCall);
        Phone phoneBySubId = getPhoneBySubId(this.mPrimaryDataSubId);
        return phoneBySubId != null && phoneBySubId.isUserDataEnabled() && findPhoneById != null && findPhoneById.isDataAllowed();
    }

    protected void transitionToEmergencyPhone() {
        if (this.mActiveModemCount <= 0) {
            logl("No phones: unable to reset preferred phone for emergency");
            return;
        }
        if (this.mPreferredDataPhoneId != 0) {
            logl("No active subscriptions: resetting preferred phone to 0 for emergency");
            this.mPreferredDataPhoneId = 0;
        }
        if (this.mPreferredDataSubId.get() != -1) {
            this.mPreferredDataSubId.set(-1);
            notifyPreferredDataSubIdChanged();
        }
    }

    private Phone getPhoneBySubId(int i) {
        if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
            return findPhoneById(this.mSubscriptionManagerService.getPhoneId(i));
        }
        return findPhoneById(this.mSubscriptionController.getPhoneId(i));
    }

    private Phone findPhoneById(int i) {
        if (SubscriptionManager.isValidPhoneId(i)) {
            return PhoneFactory.getPhone(i);
        }
        return null;
    }

    public synchronized boolean shouldApplyNetworkRequest(TelephonyNetworkRequest telephonyNetworkRequest, int i) {
        if (SubscriptionManager.isValidPhoneId(i)) {
            int subscriptionId = SubscriptionManager.getSubscriptionId(i);
            if (isPhoneActive(i) && (subscriptionId != -1 || isEmergencyNetworkRequest(telephonyNetworkRequest))) {
                int subIdFromNetworkSpecifier = getSubIdFromNetworkSpecifier(telephonyNetworkRequest.getNativeNetworkRequest().getNetworkSpecifier());
                if (isAnyVoiceCallActiveOnDevice() && isEmergencyNetworkRequest(telephonyNetworkRequest) && (subIdFromNetworkSpecifier == Integer.MAX_VALUE || subIdFromNetworkSpecifier == -1)) {
                    return i == this.mPhoneIdInVoiceCall;
                }
                return i == phoneIdForRequest(telephonyNetworkRequest);
            }
            return false;
        }
        return false;
    }

    boolean isEmergencyNetworkRequest(TelephonyNetworkRequest telephonyNetworkRequest) {
        return telephonyNetworkRequest.hasCapability(10);
    }

    @VisibleForTesting
    protected boolean isPhoneActive(int i) {
        if (i >= this.mActiveModemCount) {
            return false;
        }
        return this.mPhoneStates[i].active;
    }

    public void registerForActivePhoneSwitch(Handler handler, int i, Object obj) {
        Registrant registrant = new Registrant(handler, i, obj);
        this.mActivePhoneRegistrants.add(registrant);
        registrant.notifyRegistrant();
    }

    public void unregisterForActivePhoneSwitch(Handler handler) {
        this.mActivePhoneRegistrants.remove(handler);
    }

    private void setOpportunisticDataSubscription(int i, boolean z, ISetOpportunisticDataCallback iSetOpportunisticDataCallback) {
        validate(i, z, 3, iSetOpportunisticDataCallback);
    }

    private void validate(int i, boolean z, int i2, ISetOpportunisticDataCallback iSetOpportunisticDataCallback) {
        logl("Validate subId " + i + " due to " + switchReasonToString(i2) + " needValidation=" + z);
        int i3 = i == Integer.MAX_VALUE ? this.mPrimaryDataSubId : i;
        if (!isActiveSubId(i3)) {
            logl("Can't switch data to inactive subId " + i3);
            if (i == Integer.MAX_VALUE) {
                this.mAutoSelectedDataSubId = KeepaliveStatus.INVALID_HANDLE;
            }
            sendSetOpptCallbackHelper(iSetOpportunisticDataCallback, 2);
            return;
        }
        if (this.mValidator.isValidating()) {
            this.mValidator.lambda$reportValidationResult$1();
            sendSetOpptCallbackHelper(this.mSetOpptSubCallback, 1);
            this.mSetOpptSubCallback = null;
        }
        removeMessages(110);
        removeMessages(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_COMPANION_IFACE_IN_USE);
        this.mPendingSwitchSubId = -1;
        if (i3 == this.mPreferredDataSubId.get()) {
            sendSetOpptCallbackHelper(iSetOpportunisticDataCallback, 0);
            return;
        }
        this.mLastSwitchPreferredDataReason = i2;
        logDataSwitchEvent(i3, 1, i2);
        registerDefaultNetworkChangeCallback(i3, i2);
        if (!this.mValidator.isValidationFeatureSupported()) {
            setAutoSelectedDataSubIdInternal(i3);
            sendSetOpptCallbackHelper(iSetOpportunisticDataCallback, 0);
            return;
        }
        this.mPendingSwitchSubId = i3;
        this.mPendingSwitchNeedValidation = z;
        this.mSetOpptSubCallback = iSetOpportunisticDataCallback;
        this.mValidator.validate(i3, getValidationTimeout(i3, z), false, this.mValidationCallback);
    }

    private long getValidationTimeout(int i, boolean z) {
        CarrierConfigManager carrierConfigManager;
        PersistableBundle configForSubId;
        if (!z || (carrierConfigManager = (CarrierConfigManager) this.mContext.getSystemService("carrier_config")) == null || (configForSubId = carrierConfigManager.getConfigForSubId(i)) == null) {
            return 2000L;
        }
        return configForSubId.getLong("data_switch_validation_timeout_long");
    }

    private void sendSetOpptCallbackHelper(ISetOpportunisticDataCallback iSetOpportunisticDataCallback, int i) {
        if (iSetOpportunisticDataCallback == null) {
            return;
        }
        try {
            iSetOpportunisticDataCallback.onComplete(i);
        } catch (RemoteException e) {
            logl("RemoteException " + e);
        }
    }

    private void setAutoSelectedDataSubIdInternal(int i) {
        if (this.mAutoSelectedDataSubId != i) {
            this.mAutoSelectedDataSubId = i;
            onEvaluate(false, switchReasonToString(this.mLastSwitchPreferredDataReason));
        }
    }

    private void confirmSwitch(int i, boolean z) {
        StringBuilder sb = new StringBuilder();
        sb.append("confirmSwitch: subId ");
        sb.append(i);
        sb.append(z ? " confirmed." : " cancelled.");
        logl(sb.toString());
        int i2 = 0;
        if (!isActiveSubId(i)) {
            logl("confirmSwitch: subId " + i + " is no longer active");
            this.mAutoSwitchRetryFailedCount = 0;
            i2 = 2;
        } else if (!z) {
            i2 = 1;
            if (this.mLastSwitchPreferredDataReason == 4) {
                scheduleAutoSwitchRetryEvaluation();
                this.mAutoSwitchRetryFailedCount++;
            }
        } else {
            if (i == this.mPrimaryDataSubId) {
                setAutoSelectedDataSubIdInternal(KeepaliveStatus.INVALID_HANDLE);
            } else {
                setAutoSelectedDataSubIdInternal(i);
            }
            this.mAutoSwitchRetryFailedCount = 0;
        }
        sendSetOpptCallbackHelper(this.mSetOpptSubCallback, i2);
        this.mSetOpptSubCallback = null;
        this.mPendingSwitchSubId = -1;
    }

    private void scheduleAutoSwitchRetryEvaluation() {
        if (this.mAutoSwitchRetryFailedCount < this.mAutoDataSwitchValidationMaxRetry) {
            if (hasMessages(111)) {
                return;
            }
            sendMessageDelayed(obtainMessage(111), this.mAutoDataSwitchAvailabilityStabilityTimeThreshold << this.mAutoSwitchRetryFailedCount);
            return;
        }
        logl("scheduleAutoSwitchEvaluation: reached max auto switch retry count " + this.mAutoDataSwitchValidationMaxRetry);
        this.mAutoSwitchRetryFailedCount = 0;
    }

    private void onNetworkAvailable(int i, Network network) {
        log("onNetworkAvailable: on subId " + i);
        int i2 = this.mPendingSwitchSubId;
        if (i2 == -1 || i2 != i || this.mPendingSwitchNeedValidation) {
            return;
        }
        confirmSwitch(i, true);
    }

    private void onValidationDone(int i, boolean z) {
        StringBuilder sb = new StringBuilder();
        sb.append("onValidationDone: ");
        sb.append(z ? "passed" : "failed");
        sb.append(" on subId ");
        sb.append(i);
        logl(sb.toString());
        int i2 = this.mPendingSwitchSubId;
        if (i2 == -1 || i2 != i) {
            return;
        }
        confirmSwitch(i, z || !this.mPendingSwitchNeedValidation);
    }

    public void trySetOpportunisticDataSubscription(int i, boolean z, ISetOpportunisticDataCallback iSetOpportunisticDataCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("Try set opportunistic data subscription to subId ");
        sb.append(i);
        sb.append(z ? " with " : " without ");
        sb.append("validation");
        logl(sb.toString());
        obtainMessage(107, i, z ? 1 : 0, iSetOpportunisticDataCallback).sendToTarget();
    }

    protected boolean isPhoneInVoiceCall(Phone phone) {
        if (phone == null) {
            return false;
        }
        return (phone.getBackgroundCall().isIdle() && phone.getForegroundCall().isIdle()) ? false : true;
    }

    private void updateHalCommandToUse() {
        this.mHalCommandToUse = this.mRadioConfig.isSetPreferredDataCommandSupported() ? 2 : 1;
    }

    public int getPreferredDataPhoneId() {
        return this.mPreferredDataPhoneId;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected void logl(String str) {
        log(str);
        this.mLocalLog.log(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void log(String str) {
        Rlog.d("PhoneSwitcher", str);
    }

    private void loge(String str) {
        Rlog.e("PhoneSwitcher", str);
    }

    private static String switchReasonToString(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        if (i != 4) {
                            return "UNKNOWN(" + i + ")";
                        }
                        return "AUTO";
                    }
                    return "CBRS";
                }
                return "IN_CALL";
            }
            return "MANUAL";
        }
        return "UNKNOWN";
    }

    private static String switchStateToString(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    return "UNKNOWN(" + i + ")";
                }
                return "END";
            }
            return "START";
        }
        return "UNKNOWN";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logDataSwitchEvent(int i, int i2, int i3) {
        logl("Data switch state=" + switchStateToString(i2) + " due to reason=" + switchReasonToString(i3) + " on subId " + i);
        TelephonyProto$TelephonyEvent.DataSwitch dataSwitch = new TelephonyProto$TelephonyEvent.DataSwitch();
        dataSwitch.state = i2;
        dataSwitch.reason = i3;
        TelephonyMetrics.getInstance().writeDataSwitch(i, dataSwitch);
    }

    protected void notifyPreferredDataSubIdChanged() {
        logl("notifyPreferredDataSubIdChanged to " + this.mPreferredDataSubId.get());
        ((TelephonyRegistryManager) this.mContext.getSystemService("telephony_registry")).notifyActiveDataSubIdChanged(this.mPreferredDataSubId.get());
    }

    public int getActiveDataSubId() {
        return this.mPreferredDataSubId.get();
    }

    public int getAutoSelectedDataSubId() {
        return this.mAutoSelectedDataSubId;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        indentingPrintWriter.println("PhoneSwitcher:");
        indentingPrintWriter.increaseIndent();
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < this.mActiveModemCount; i++) {
            PhoneState phoneState = this.mPhoneStates[i];
            calendar.setTimeInMillis(phoneState.lastRequested);
            StringBuilder sb = new StringBuilder();
            sb.append("PhoneId(");
            sb.append(i);
            sb.append(") active=");
            sb.append(phoneState.active);
            sb.append(", dataRegState=");
            sb.append(NetworkRegistrationInfo.registrationStateToString(phoneState.dataRegState));
            sb.append(", lastRequest=");
            sb.append(phoneState.lastRequested == 0 ? "never" : String.format("%tm-%td %tH:%tM:%tS.%tL", calendar, calendar, calendar, calendar, calendar, calendar));
            indentingPrintWriter.println(sb.toString());
        }
        indentingPrintWriter.println("mPreferredDataPhoneId=" + this.mPreferredDataPhoneId);
        indentingPrintWriter.println("mPreferredDataSubId=" + this.mPreferredDataSubId.get());
        if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
            indentingPrintWriter.println("DefaultDataSubId=" + this.mSubscriptionManagerService.getDefaultDataSubId());
            StringBuilder sb2 = new StringBuilder();
            sb2.append("DefaultDataPhoneId=");
            SubscriptionManagerService subscriptionManagerService = this.mSubscriptionManagerService;
            sb2.append(subscriptionManagerService.getPhoneId(subscriptionManagerService.getDefaultDataSubId()));
            indentingPrintWriter.println(sb2.toString());
        } else {
            indentingPrintWriter.println("DefaultDataSubId=" + this.mSubscriptionController.getDefaultDataSubId());
            StringBuilder sb3 = new StringBuilder();
            sb3.append("DefaultDataPhoneId=");
            SubscriptionController subscriptionController = this.mSubscriptionController;
            sb3.append(subscriptionController.getPhoneId(subscriptionController.getDefaultDataSubId()));
            indentingPrintWriter.println(sb3.toString());
        }
        indentingPrintWriter.println("mPrimaryDataSubId=" + this.mPrimaryDataSubId);
        indentingPrintWriter.println("mAutoSelectedDataSubId=" + this.mAutoSelectedDataSubId);
        indentingPrintWriter.println("mIsRegisteredForImsRadioTechChange=" + this.mIsRegisteredForImsRadioTechChange);
        indentingPrintWriter.println("mPendingSwitchNeedValidation=" + this.mPendingSwitchNeedValidation);
        indentingPrintWriter.println("mMaxDataAttachModemCount=" + this.mMaxDataAttachModemCount);
        indentingPrintWriter.println("mActiveModemCount=" + this.mActiveModemCount);
        indentingPrintWriter.println("mPhoneIdInVoiceCall=" + this.mPhoneIdInVoiceCall);
        indentingPrintWriter.println("mCurrentDdsSwitchFailure=" + this.mCurrentDdsSwitchFailure);
        indentingPrintWriter.println("mAutoDataSwitchAvailabilityStabilityTimeThreshold=" + this.mAutoDataSwitchAvailabilityStabilityTimeThreshold);
        indentingPrintWriter.println("mAutoDataSwitchValidationMaxRetry=" + this.mAutoDataSwitchValidationMaxRetry);
        indentingPrintWriter.println("mRequirePingTestBeforeDataSwitch=" + this.mRequirePingTestBeforeDataSwitch);
        indentingPrintWriter.println("mLastSwitchPreferredDataReason=" + switchReasonToString(this.mLastSwitchPreferredDataReason));
        indentingPrintWriter.println("mDisplayedAutoSwitchNotification=" + this.mDisplayedAutoSwitchNotification);
        indentingPrintWriter.println("Local logs:");
        indentingPrintWriter.increaseIndent();
        this.mLocalLog.dump(fileDescriptor, indentingPrintWriter, strArr);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.decreaseIndent();
    }

    private boolean isAnyVoiceCallActiveOnDevice() {
        boolean z = this.mPhoneIdInVoiceCall != -1;
        if (VDBG) {
            log("isAnyVoiceCallActiveOnDevice: " + z);
        }
        return z;
    }

    private void onDdsSwitchResponse(AsyncResult asyncResult) {
        boolean z = asyncResult != null && asyncResult.exception == null;
        int intValue = ((Integer) asyncResult.userObj).intValue();
        if (this.mEmergencyOverride != null) {
            logl("Emergency override result sent = " + z);
            this.mEmergencyOverride.sendOverrideCompleteCallbackResultAndClear(z);
        } else if (!z) {
            logl("onDdsSwitchResponse: DDS switch failed. with exception " + asyncResult.exception);
            Throwable th = asyncResult.exception;
            if (th instanceof CommandException) {
                CommandException.Error commandError = ((CommandException) th).getCommandError();
                this.mCurrentDdsSwitchFailure.get(intValue).add(commandError);
                if (commandError == CommandException.Error.OP_NOT_ALLOWED_DURING_VOICE_CALL) {
                    logl("onDdsSwitchResponse: Wait for call end indication");
                    return;
                } else if (commandError == CommandException.Error.INVALID_SIM_STATE) {
                    logl("onDdsSwitchResponse: Wait for SIM to get READY");
                    return;
                }
            }
            logl("onDdsSwitchResponse: Scheduling DDS switch retry");
            sendMessageDelayed(Message.obtain(this, TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_INVALID_PCSCF_ADDR, Integer.valueOf(intValue)), 5000L);
            return;
        }
        if (z) {
            logl("onDdsSwitchResponse: DDS switch success on phoneId = " + intValue);
        }
        this.mCurrentDdsSwitchFailure.get(intValue).clear();
        this.mActivePhoneRegistrants.notifyRegistrants();
        notifyPreferredDataSubIdChanged();
        displayAutoDataSwitchNotification();
    }

    private void displayAutoDataSwitchNotification() {
        SubscriptionInfo subscriptionInfo;
        NotificationManager notificationManager = (NotificationManager) this.mContext.getSystemService("notification");
        if (this.mDisplayedAutoSwitchNotification) {
            log("displayAutoDataSwitchNotification: canceling any notifications for subId " + this.mAutoSelectedDataSubId);
            notificationManager.cancel("auto_data_switch", 1);
        } else if (this.mLastSwitchPreferredDataReason != 4) {
            log("displayAutoDataSwitchNotification: Ignore DDS switch due to " + switchReasonToString(this.mLastSwitchPreferredDataReason));
        } else {
            if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
                subscriptionInfo = this.mSubscriptionManagerService.getSubscriptionInfo(this.mAutoSelectedDataSubId);
            } else {
                subscriptionInfo = this.mSubscriptionController.getSubscriptionInfo(this.mAutoSelectedDataSubId);
            }
            if (subscriptionInfo == null || subscriptionInfo.isOpportunistic()) {
                loge("displayAutoDataSwitchNotification: mAutoSelectedDataSubId=" + this.mAutoSelectedDataSubId + " unexpected subInfo " + subscriptionInfo);
                return;
            }
            logl("displayAutoDataSwitchNotification: display for subId=" + this.mAutoSelectedDataSubId);
            Intent intent = new Intent("android.settings.NETWORK_OPERATOR_SETTINGS");
            Bundle bundle = new Bundle();
            bundle.putString(":settings:fragment_args_key", "auto_data_switch");
            intent.putExtra("android.provider.extra.SUB_ID", this.mAutoSelectedDataSubId);
            intent.putExtra(":settings:show_fragment_args", bundle);
            PendingIntent activity = PendingIntent.getActivity(this.mContext, this.mAutoSelectedDataSubId, intent, 67108864);
            String string = this.mContext.getString(17039707, subscriptionInfo.getDisplayName());
            CharSequence text = this.mContext.getText(17039706);
            notificationManager.notify("auto_data_switch", 1, new Notification.Builder(this.mContext).setContentTitle(string).setContentText(text).setSmallIcon(17301642).setColor(this.mContext.getResources().getColor(17170460)).setChannelId(NotificationChannelController.CHANNEL_ID_MOBILE_DATA_STATUS).setContentIntent(activity).setStyle(new Notification.BigTextStyle().bigText(text)).build());
            this.mDisplayedAutoSwitchNotification = true;
        }
    }

    private boolean isPhoneIdValidForRetry(int i) {
        int phoneId;
        if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
            SubscriptionManagerService subscriptionManagerService = this.mSubscriptionManagerService;
            phoneId = subscriptionManagerService.getPhoneId(subscriptionManagerService.getDefaultDataSubId());
        } else {
            SubscriptionController subscriptionController = this.mSubscriptionController;
            phoneId = subscriptionController.getPhoneId(subscriptionController.getDefaultDataSubId());
        }
        if (phoneId == -1 || phoneId != i) {
            if (this.mNetworkRequestList.isEmpty()) {
                return false;
            }
            Iterator<TelephonyNetworkRequest> it = this.mNetworkRequestList.iterator();
            while (it.hasNext()) {
                if (phoneIdForRequest(it.next()) == i) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
}
