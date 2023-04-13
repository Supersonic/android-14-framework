package com.android.internal.telephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.telephony.CarrierConfigManager;
import android.telephony.NetworkRegistrationInfo;
import android.telephony.PhysicalChannelConfig;
import android.telephony.ServiceState;
import android.telephony.TelephonyDisplayInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.android.internal.telephony.data.DataNetworkController;
import com.android.internal.telephony.data.DataUtils;
import com.android.internal.telephony.util.ArrayUtils;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
/* loaded from: classes.dex */
public class NetworkTypeController extends StateMachine {
    private static final String[] ALL_STATES = {"connected_mmwave", PhoneInternalInterface.REASON_CONNECTED, "not_restricted_rrc_idle", "not_restricted_rrc_con", "restricted", "legacy"};
    public static final int EVENT_UPDATE = 0;
    private static final String[] sEvents;
    private int[] mAdditionalNrAdvancedBandsList;
    private final CarrierConfigManager.CarrierConfigChangeListener mCarrierConfigChangeListener;
    private final DisplayInfoController mDisplayInfoController;
    private boolean mEnableNrAdvancedWhileRoaming;
    private final IdleState mIdleState;
    private final BroadcastReceiver mIntentReceiver;
    private boolean mIsDeviceIdleMode;
    private boolean mIsNrAdvancedAllowedByPco;
    private boolean mIsPhysicalChannelConfig16Supported;
    private boolean mIsPhysicalChannelConfigOn;
    private boolean mIsPrimaryTimerActive;
    private boolean mIsSecondaryTimerActive;
    private boolean mIsTimerResetEnabledForLegacyStateRrcIdle;
    private boolean mIsUsingUserDataForRrcDetection;
    private final LegacyState mLegacyState;
    private final LteConnectedState mLteConnectedState;
    private String mLteEnhancedPattern;
    private int mLtePlusThresholdBandwidth;
    private DataNetworkController.DataNetworkControllerCallback mNrAdvancedCapableByPcoChangedCallback;
    private int mNrAdvancedCapablePcoId;
    private int mNrAdvancedThresholdBandwidth;
    private final NrConnectedAdvancedState mNrConnectedAdvancedState;
    private final NrConnectedState mNrConnectedState;
    private DataNetworkController.DataNetworkControllerCallback mNrPhysicalLinkStatusChangedCallback;
    private int mOverrideNetworkType;
    private Map<String, OverrideTimerRule> mOverrideTimerRules;
    private final Phone mPhone;
    private List<PhysicalChannelConfig> mPhysicalChannelConfigs;
    private int mPhysicalLinkStatus;
    private String mPreviousState;
    private String mPrimaryTimerState;
    private String mSecondaryTimerState;
    private ServiceState mServiceState;

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isLte(int i) {
        return i == 13 || i == 19;
    }

    static {
        sEvents = r0;
        String[] strArr = {"EVENT_UPDATE", "EVENT_QUIT", "EVENT_INITIALIZE", "EVENT_SERVICE_STATE_CHANGED", "EVENT_PHYSICAL_LINK_STATUS_CHANGED", "EVENT_PHYSICAL_CHANNEL_CONFIG_NOTIF_CHANGED", "EVENT_CARRIER_CONFIG_CHANGED", "EVENT_PRIMARY_TIMER_EXPIRED", "EVENT_SECONDARY_TIMER_EXPIRED", "EVENT_RADIO_OFF_OR_UNAVAILABLE", "EVENT_PREFERRED_NETWORK_MODE_CHANGED", "EVENT_PHYSICAL_CHANNEL_CONFIG_CHANGED", "EVENT_DEVICE_IDLE_MODE_CHANGED"};
    }

    public NetworkTypeController(Phone phone, DisplayInfoController displayInfoController) {
        super("NetworkTypeController", displayInfoController);
        this.mIntentReceiver = new BroadcastReceiver() { // from class: com.android.internal.telephony.NetworkTypeController.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                action.hashCode();
                if (action.equals("android.os.action.DEVICE_IDLE_MODE_CHANGED")) {
                    NetworkTypeController.this.sendMessage(12);
                }
            }
        };
        this.mCarrierConfigChangeListener = new CarrierConfigManager.CarrierConfigChangeListener() { // from class: com.android.internal.telephony.NetworkTypeController.2
            public void onCarrierConfigChanged(int i, int i2, int i3, int i4) {
                if (i == NetworkTypeController.this.mPhone.getPhoneId()) {
                    NetworkTypeController.this.sendMessage(6);
                }
            }
        };
        this.mOverrideTimerRules = new HashMap();
        this.mLteEnhancedPattern = PhoneConfigurationManager.SSSS;
        this.mIsNrAdvancedAllowedByPco = false;
        this.mNrAdvancedCapablePcoId = 0;
        this.mIsUsingUserDataForRrcDetection = false;
        this.mEnableNrAdvancedWhileRoaming = true;
        this.mIsDeviceIdleMode = false;
        this.mNrAdvancedCapableByPcoChangedCallback = null;
        this.mNrPhysicalLinkStatusChangedCallback = null;
        LegacyState legacyState = new LegacyState();
        this.mLegacyState = legacyState;
        IdleState idleState = new IdleState();
        this.mIdleState = idleState;
        LteConnectedState lteConnectedState = new LteConnectedState();
        this.mLteConnectedState = lteConnectedState;
        NrConnectedState nrConnectedState = new NrConnectedState();
        this.mNrConnectedState = nrConnectedState;
        NrConnectedAdvancedState nrConnectedAdvancedState = new NrConnectedAdvancedState();
        this.mNrConnectedAdvancedState = nrConnectedAdvancedState;
        this.mPhone = phone;
        this.mDisplayInfoController = displayInfoController;
        this.mOverrideNetworkType = 0;
        this.mIsPhysicalChannelConfigOn = true;
        this.mPrimaryTimerState = PhoneConfigurationManager.SSSS;
        this.mSecondaryTimerState = PhoneConfigurationManager.SSSS;
        this.mPreviousState = PhoneConfigurationManager.SSSS;
        State defaultState = new DefaultState();
        addState(defaultState);
        addState(legacyState, defaultState);
        addState(idleState, defaultState);
        addState(lteConnectedState, defaultState);
        addState(nrConnectedState, defaultState);
        addState(nrConnectedAdvancedState, defaultState);
        setInitialState(defaultState);
        start();
        this.mServiceState = phone.getServiceStateTracker().getServiceState();
        this.mPhysicalChannelConfigs = phone.getServiceStateTracker().getPhysicalChannelConfigList();
        sendMessage(2);
    }

    public int getOverrideNetworkType() {
        return this.mOverrideNetworkType;
    }

    public int getDataNetworkType() {
        NetworkRegistrationInfo networkRegistrationInfo = this.mServiceState.getNetworkRegistrationInfo(2, 1);
        if (networkRegistrationInfo == null) {
            return 0;
        }
        return networkRegistrationInfo.getAccessNetworkTechnology();
    }

    public boolean areAnyTimersActive() {
        return this.mIsPrimaryTimerActive || this.mIsSecondaryTimerActive;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerForAllEvents() {
        this.mPhone.registerForRadioOffOrNotAvailable(getHandler(), 9, null);
        this.mPhone.registerForPreferredNetworkTypeChanged(getHandler(), 10, null);
        this.mPhone.registerForPhysicalChannelConfig(getHandler(), 11, null);
        this.mPhone.getServiceStateTracker().registerForServiceStateChanged(getHandler(), 3, null);
        this.mIsPhysicalChannelConfig16Supported = ((TelephonyManager) this.mPhone.getContext().getSystemService(TelephonyManager.class)).isRadioInterfaceCapabilitySupported("CAPABILITY_PHYSICAL_CHANNEL_CONFIG_1_6_SUPPORTED");
        this.mPhone.getDeviceStateMonitor().registerForPhysicalChannelConfigNotifChanged(getHandler(), 5, null);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.os.action.DEVICE_IDLE_MODE_CHANGED");
        this.mPhone.getContext().registerReceiver(this.mIntentReceiver, intentFilter, null, this.mPhone);
        ((CarrierConfigManager) this.mPhone.getContext().getSystemService(CarrierConfigManager.class)).registerCarrierConfigChangeListener(new NetworkTypeController$$ExternalSyntheticLambda1(), this.mCarrierConfigChangeListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unRegisterForAllEvents() {
        this.mPhone.unregisterForRadioOffOrNotAvailable(getHandler());
        this.mPhone.unregisterForPreferredNetworkTypeChanged(getHandler());
        this.mPhone.getServiceStateTracker().unregisterForServiceStateChanged(getHandler());
        this.mPhone.getDeviceStateMonitor().unregisterForPhysicalChannelConfigNotifChanged(getHandler());
        this.mPhone.getContext().unregisterReceiver(this.mIntentReceiver);
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) this.mPhone.getContext().getSystemService(CarrierConfigManager.class);
        CarrierConfigManager.CarrierConfigChangeListener carrierConfigChangeListener = this.mCarrierConfigChangeListener;
        if (carrierConfigChangeListener != null) {
            carrierConfigManager.unregisterCarrierConfigChangeListener(carrierConfigChangeListener);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void parseCarrierConfigs() {
        PersistableBundle configForSubId;
        PersistableBundle defaultConfig = CarrierConfigManager.getDefaultConfig();
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) this.mPhone.getContext().getSystemService(CarrierConfigManager.class);
        if (carrierConfigManager != null && (configForSubId = carrierConfigManager.getConfigForSubId(this.mPhone.getSubId())) != null) {
            defaultConfig = configForSubId;
        }
        this.mLteEnhancedPattern = defaultConfig.getString("show_carrier_data_icon_pattern_string");
        this.mIsTimerResetEnabledForLegacyStateRrcIdle = defaultConfig.getBoolean("nr_timers_reset_if_non_endc_and_rrc_idle_bool");
        this.mLtePlusThresholdBandwidth = defaultConfig.getInt("lte_plus_threshold_bandwidth_khz_int");
        this.mNrAdvancedThresholdBandwidth = defaultConfig.getInt("nr_advanced_threshold_bandwidth_khz_int");
        this.mEnableNrAdvancedWhileRoaming = defaultConfig.getBoolean("enable_nr_advanced_for_roaming_bool");
        this.mAdditionalNrAdvancedBandsList = defaultConfig.getIntArray("additional_nr_advanced_bands_int_array");
        int i = defaultConfig.getInt("nr_advanced_capable_pco_id_int");
        this.mNrAdvancedCapablePcoId = i;
        if (i > 0 && this.mNrAdvancedCapableByPcoChangedCallback == null) {
            Handler handler = getHandler();
            Objects.requireNonNull(handler);
            this.mNrAdvancedCapableByPcoChangedCallback = new DataNetworkController.DataNetworkControllerCallback(new NetworkTypeController$$ExternalSyntheticLambda0(handler)) { // from class: com.android.internal.telephony.NetworkTypeController.3
                @Override // com.android.internal.telephony.data.DataNetworkController.DataNetworkControllerCallback
                public void onNrAdvancedCapableByPcoChanged(boolean z) {
                    NetworkTypeController networkTypeController = NetworkTypeController.this;
                    networkTypeController.log("mIsNrAdvancedAllowedByPco=" + z);
                    NetworkTypeController.this.mIsNrAdvancedAllowedByPco = z;
                    NetworkTypeController.this.sendMessage(0);
                }
            };
            this.mPhone.getDataNetworkController().registerDataNetworkControllerCallback(this.mNrAdvancedCapableByPcoChangedCallback);
        } else if (i == 0 && this.mNrAdvancedCapableByPcoChangedCallback != null) {
            this.mPhone.getDataNetworkController().unregisterDataNetworkControllerCallback(this.mNrAdvancedCapableByPcoChangedCallback);
            this.mNrAdvancedCapableByPcoChangedCallback = null;
        }
        this.mIsUsingUserDataForRrcDetection = defaultConfig.getBoolean("lte_endc_using_user_data_for_rrc_detection_bool");
        if (!isUsingPhysicalChannelConfigForRrcDetection()) {
            if (this.mNrPhysicalLinkStatusChangedCallback == null) {
                Handler handler2 = getHandler();
                Objects.requireNonNull(handler2);
                this.mNrPhysicalLinkStatusChangedCallback = new DataNetworkController.DataNetworkControllerCallback(new NetworkTypeController$$ExternalSyntheticLambda0(handler2)) { // from class: com.android.internal.telephony.NetworkTypeController.4
                    @Override // com.android.internal.telephony.data.DataNetworkController.DataNetworkControllerCallback
                    public void onPhysicalLinkStatusChanged(int i2) {
                        NetworkTypeController networkTypeController = NetworkTypeController.this;
                        networkTypeController.sendMessage(networkTypeController.obtainMessage(4, new AsyncResult((Object) null, Integer.valueOf(i2), (Throwable) null)));
                    }
                };
                this.mPhone.getDataNetworkController().registerDataNetworkControllerCallback(this.mNrPhysicalLinkStatusChangedCallback);
            }
        } else if (this.mNrPhysicalLinkStatusChangedCallback != null) {
            this.mPhone.getDataNetworkController().unregisterDataNetworkControllerCallback(this.mNrPhysicalLinkStatusChangedCallback);
            this.mNrPhysicalLinkStatusChangedCallback = null;
        }
        createTimerRules(defaultConfig.getString("5g_icon_configuration_string"), defaultConfig.getString("5g_icon_display_grace_period_string"), defaultConfig.getString("5g_icon_display_secondary_grace_period_string"));
    }

    private void createTimerRules(String str, String str2, String str3) {
        String[] strArr;
        String[] split;
        int parseInt;
        String[] split2;
        int i;
        HashMap hashMap = new HashMap();
        int i2 = 3;
        char c = 2;
        int i3 = 0;
        if (!TextUtils.isEmpty(str)) {
            for (String str4 : str.trim().split(",")) {
                String[] split3 = str4.trim().toLowerCase(Locale.ROOT).split(":");
                if (split3.length != 2) {
                    loge("Invalid 5G icon configuration, config = " + str4);
                } else {
                    if (split3[1].equals("5g")) {
                        i = 3;
                    } else if (split3[1].equals("5g_plus")) {
                        i = 5;
                    } else {
                        loge("Invalid 5G icon = " + split3[1]);
                        i = 0;
                    }
                    String str5 = split3[0];
                    hashMap.put(str5, new OverrideTimerRule(str5, i));
                }
            }
        }
        for (String str6 : ALL_STATES) {
            if (!hashMap.containsKey(str6)) {
                hashMap.put(str6, new OverrideTimerRule(str6, 0));
            }
        }
        if (!TextUtils.isEmpty(str2)) {
            String[] split4 = str2.trim().split(";");
            int length = split4.length;
            int i4 = 0;
            while (i4 < length) {
                String str7 = split4[i4];
                String[] split5 = str7.trim().toLowerCase(Locale.ROOT).split(",");
                if (split5.length != i2) {
                    loge("Invalid 5G icon timer configuration, config = " + str7);
                } else {
                    try {
                        int parseInt2 = Integer.parseInt(split5[c]);
                        if (split5[i3].equals("any")) {
                            String[] strArr2 = ALL_STATES;
                            int length2 = strArr2.length;
                            for (int i5 = i3; i5 < length2; i5++) {
                                ((OverrideTimerRule) hashMap.get(strArr2[i5])).addTimer(split5[1], parseInt2);
                            }
                        } else {
                            ((OverrideTimerRule) hashMap.get(split5[i3])).addTimer(split5[1], parseInt2);
                        }
                    } catch (NumberFormatException unused) {
                    }
                }
                i4++;
                i2 = 3;
                c = 2;
                i3 = 0;
            }
        }
        if (!TextUtils.isEmpty(str3)) {
            for (String str8 : str3.trim().split(";")) {
                String[] split6 = str8.trim().toLowerCase(Locale.ROOT).split(",");
                if (split6.length != 3) {
                    loge("Invalid 5G icon secondary timer configuration, config = " + str8);
                } else {
                    try {
                        parseInt = Integer.parseInt(split6[2]);
                    } catch (NumberFormatException unused2) {
                    }
                    if (split6[0].equals("any")) {
                        for (String str9 : ALL_STATES) {
                            ((OverrideTimerRule) hashMap.get(str9)).addSecondaryTimer(split6[1], parseInt);
                        }
                    } else {
                        ((OverrideTimerRule) hashMap.get(split6[0])).addSecondaryTimer(split6[1], parseInt);
                    }
                }
            }
        }
        this.mOverrideTimerRules = hashMap;
        log("mOverrideTimerRules: " + this.mOverrideTimerRules);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateOverrideNetworkType() {
        if (this.mIsPrimaryTimerActive || this.mIsSecondaryTimerActive) {
            log("Skip updating override network type since timer is active.");
            return;
        }
        this.mOverrideNetworkType = getCurrentOverrideNetworkType();
        this.mDisplayInfoController.updateTelephonyDisplayInfo();
    }

    private int getCurrentOverrideNetworkType() {
        int dataNetworkType = getDataNetworkType();
        boolean z = isLte(dataNetworkType) && this.mServiceState.getNrState() != 0;
        boolean z2 = dataNetworkType == 20;
        if (this.mIsPhysicalChannelConfigOn && (z || z2)) {
            int nrDisplayType = getNrDisplayType(z2);
            return (nrDisplayType != 0 || z2) ? nrDisplayType : getLteDisplayType();
        } else if (isLte(dataNetworkType)) {
            return getLteDisplayType();
        } else {
            return 0;
        }
    }

    private int getNrDisplayType(boolean z) {
        int i;
        if ((this.mPhone.getCachedAllowedNetworkTypesBitmask() & 524288) == 0) {
            return 0;
        }
        ArrayList<String> arrayList = new ArrayList();
        if (z) {
            if (isNrAdvanced()) {
                arrayList.add("connected_mmwave");
            }
        } else {
            int nrState = this.mServiceState.getNrState();
            if (nrState == 1) {
                arrayList.add("restricted");
            } else if (nrState == 2) {
                arrayList.add(isPhysicalLinkActive() ? "not_restricted_rrc_con" : "not_restricted_rrc_idle");
            } else if (nrState == 3) {
                if (isNrAdvanced()) {
                    arrayList.add("connected_mmwave");
                }
                arrayList.add(PhoneInternalInterface.REASON_CONNECTED);
            }
        }
        for (String str : arrayList) {
            OverrideTimerRule overrideTimerRule = this.mOverrideTimerRules.get(str);
            if (overrideTimerRule != null && (i = overrideTimerRule.mOverrideType) != 0) {
                return i;
            }
        }
        return 0;
    }

    private int getLteDisplayType() {
        int i = ((getDataNetworkType() == 19 || this.mServiceState.isUsingCarrierAggregation()) && IntStream.of(this.mServiceState.getCellBandwidths()).sum() > this.mLtePlusThresholdBandwidth) ? 1 : 0;
        if (isLteEnhancedAvailable()) {
            return 2;
        }
        return i;
    }

    private boolean isLteEnhancedAvailable() {
        if (TextUtils.isEmpty(this.mLteEnhancedPattern)) {
            return false;
        }
        Pattern compile = Pattern.compile(this.mLteEnhancedPattern);
        String[] strArr = {this.mServiceState.getOperatorAlphaLongRaw(), this.mServiceState.getOperatorAlphaShortRaw()};
        for (int i = 0; i < 2; i++) {
            String str = strArr[i];
            if (!TextUtils.isEmpty(str) && compile.matcher(str).find()) {
                return true;
            }
        }
        return false;
    }

    /* loaded from: classes.dex */
    private final class DefaultState extends State {
        private DefaultState() {
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public boolean processMessage(Message message) {
            NetworkTypeController networkTypeController = NetworkTypeController.this;
            networkTypeController.log("DefaultState: process " + NetworkTypeController.this.getEventName(message.what));
            switch (message.what) {
                case 0:
                    NetworkTypeController.this.resetAllTimers();
                    NetworkTypeController.this.transitionToCurrentState();
                    return true;
                case 1:
                    NetworkTypeController.this.log("Reset timers on state machine quitting.");
                    NetworkTypeController.this.resetAllTimers();
                    NetworkTypeController.this.unRegisterForAllEvents();
                    NetworkTypeController.this.quit();
                    return true;
                case 2:
                    NetworkTypeController.this.registerForAllEvents();
                    NetworkTypeController.this.parseCarrierConfigs();
                    return true;
                case 3:
                    NetworkTypeController networkTypeController2 = NetworkTypeController.this;
                    networkTypeController2.mServiceState = networkTypeController2.mPhone.getServiceStateTracker().getServiceState();
                    NetworkTypeController networkTypeController3 = NetworkTypeController.this;
                    networkTypeController3.log("ServiceState updated: " + NetworkTypeController.this.mServiceState);
                    NetworkTypeController.this.transitionToCurrentState();
                    return true;
                case 4:
                    NetworkTypeController.this.mPhysicalLinkStatus = ((Integer) ((AsyncResult) message.obj).result).intValue();
                    return true;
                case 5:
                    NetworkTypeController.this.mIsPhysicalChannelConfigOn = ((Boolean) ((AsyncResult) message.obj).result).booleanValue();
                    NetworkTypeController networkTypeController4 = NetworkTypeController.this;
                    networkTypeController4.log("mIsPhysicalChannelConfigOn changed to: " + NetworkTypeController.this.mIsPhysicalChannelConfigOn);
                    if (!NetworkTypeController.this.mIsPhysicalChannelConfigOn) {
                        NetworkTypeController.this.log("Reset timers since physical channel config indications are off.");
                        NetworkTypeController.this.resetAllTimers();
                    }
                    NetworkTypeController.this.transitionToCurrentState();
                    return true;
                case 6:
                    NetworkTypeController.this.parseCarrierConfigs();
                    NetworkTypeController.this.log("Reset timers since carrier configurations changed.");
                    NetworkTypeController.this.resetAllTimers();
                    NetworkTypeController.this.transitionToCurrentState();
                    return true;
                case 7:
                    NetworkTypeController networkTypeController5 = NetworkTypeController.this;
                    networkTypeController5.log("Primary timer expired for state: " + NetworkTypeController.this.mPrimaryTimerState);
                    NetworkTypeController.this.transitionWithSecondaryTimerTo((IState) message.obj);
                    return true;
                case 8:
                    NetworkTypeController networkTypeController6 = NetworkTypeController.this;
                    networkTypeController6.log("Secondary timer expired for state: " + NetworkTypeController.this.mSecondaryTimerState);
                    NetworkTypeController.this.mIsSecondaryTimerActive = false;
                    NetworkTypeController.this.mSecondaryTimerState = PhoneConfigurationManager.SSSS;
                    NetworkTypeController.this.updateTimers();
                    NetworkTypeController.this.updateOverrideNetworkType();
                    return true;
                case 9:
                    NetworkTypeController.this.log("Reset timers since radio is off or unavailable.");
                    NetworkTypeController.this.resetAllTimers();
                    NetworkTypeController networkTypeController7 = NetworkTypeController.this;
                    networkTypeController7.transitionTo(networkTypeController7.mLegacyState);
                    return true;
                case 10:
                    NetworkTypeController.this.log("Reset timers since preferred network mode changed.");
                    NetworkTypeController.this.resetAllTimers();
                    NetworkTypeController.this.transitionToCurrentState();
                    return true;
                case 11:
                    NetworkTypeController networkTypeController8 = NetworkTypeController.this;
                    networkTypeController8.mPhysicalChannelConfigs = networkTypeController8.mPhone.getServiceStateTracker().getPhysicalChannelConfigList();
                    NetworkTypeController networkTypeController9 = NetworkTypeController.this;
                    networkTypeController9.log("Physical channel configs updated: " + NetworkTypeController.this.mPhysicalChannelConfigs);
                    if (NetworkTypeController.this.isUsingPhysicalChannelConfigForRrcDetection()) {
                        NetworkTypeController networkTypeController10 = NetworkTypeController.this;
                        networkTypeController10.mPhysicalLinkStatus = networkTypeController10.getPhysicalLinkStatusFromPhysicalChannelConfig();
                    }
                    NetworkTypeController.this.transitionToCurrentState();
                    return true;
                case 12:
                    NetworkTypeController.this.mIsDeviceIdleMode = ((PowerManager) NetworkTypeController.this.mPhone.getContext().getSystemService(PowerManager.class)).isDeviceIdleMode();
                    NetworkTypeController networkTypeController11 = NetworkTypeController.this;
                    networkTypeController11.log("mIsDeviceIdleMode changed to: " + NetworkTypeController.this.mIsDeviceIdleMode);
                    if (NetworkTypeController.this.mIsDeviceIdleMode) {
                        NetworkTypeController.this.log("Reset timers since device is in idle mode.");
                        NetworkTypeController.this.resetAllTimers();
                    }
                    NetworkTypeController.this.transitionToCurrentState();
                    return true;
                default:
                    throw new RuntimeException("Received invalid event: " + message.what);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class LegacyState extends State {
        private boolean mIsNrRestricted;

        private LegacyState() {
            this.mIsNrRestricted = false;
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public void enter() {
            NetworkTypeController.this.log("Entering LegacyState");
            NetworkTypeController.this.updateTimers();
            NetworkTypeController.this.updateOverrideNetworkType();
            if (NetworkTypeController.this.mIsPrimaryTimerActive || NetworkTypeController.this.mIsSecondaryTimerActive) {
                return;
            }
            this.mIsNrRestricted = NetworkTypeController.this.isNrRestricted();
            NetworkTypeController.this.mPreviousState = getName();
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public boolean processMessage(Message message) {
            NetworkTypeController networkTypeController = NetworkTypeController.this;
            networkTypeController.log("LegacyState: process " + NetworkTypeController.this.getEventName(message.what));
            NetworkTypeController.this.updateTimers();
            int i = message.what;
            if (i != 0) {
                if (i == 11) {
                    NetworkTypeController networkTypeController2 = NetworkTypeController.this;
                    networkTypeController2.mPhysicalChannelConfigs = networkTypeController2.mPhone.getServiceStateTracker().getPhysicalChannelConfigList();
                    NetworkTypeController networkTypeController3 = NetworkTypeController.this;
                    networkTypeController3.log("Physical channel configs updated: " + NetworkTypeController.this.mPhysicalChannelConfigs);
                    if (NetworkTypeController.this.isUsingPhysicalChannelConfigForRrcDetection()) {
                        NetworkTypeController networkTypeController4 = NetworkTypeController.this;
                        networkTypeController4.mPhysicalLinkStatus = networkTypeController4.getPhysicalLinkStatusFromPhysicalChannelConfig();
                        if (NetworkTypeController.this.mIsTimerResetEnabledForLegacyStateRrcIdle && !NetworkTypeController.this.isPhysicalLinkActive()) {
                            NetworkTypeController.this.log("Reset timers since timer reset is enabled for RRC idle.");
                            NetworkTypeController.this.resetAllTimers();
                        }
                    }
                } else if (i == 3) {
                    NetworkTypeController networkTypeController5 = NetworkTypeController.this;
                    networkTypeController5.mServiceState = networkTypeController5.mPhone.getServiceStateTracker().getServiceState();
                    NetworkTypeController networkTypeController6 = NetworkTypeController.this;
                    networkTypeController6.log("ServiceState updated: " + NetworkTypeController.this.mServiceState);
                } else if (i != 4) {
                    return false;
                } else {
                    NetworkTypeController.this.mPhysicalLinkStatus = ((Integer) ((AsyncResult) message.obj).result).intValue();
                    if (NetworkTypeController.this.mIsTimerResetEnabledForLegacyStateRrcIdle && !NetworkTypeController.this.isPhysicalLinkActive()) {
                        NetworkTypeController.this.log("Reset timers since timer reset is enabled for RRC idle.");
                        NetworkTypeController.this.resetAllTimers();
                        NetworkTypeController.this.updateOverrideNetworkType();
                    }
                }
                if (NetworkTypeController.this.mIsPrimaryTimerActive && !NetworkTypeController.this.mIsSecondaryTimerActive) {
                    NetworkTypeController.this.mPreviousState = getName();
                    return true;
                }
            }
            int dataNetworkType = NetworkTypeController.this.getDataNetworkType();
            if (dataNetworkType == 20 || (NetworkTypeController.this.isLte(dataNetworkType) && NetworkTypeController.this.isNrConnected())) {
                if (NetworkTypeController.this.isNrAdvanced()) {
                    NetworkTypeController networkTypeController7 = NetworkTypeController.this;
                    networkTypeController7.transitionTo(networkTypeController7.mNrConnectedAdvancedState);
                } else {
                    NetworkTypeController networkTypeController8 = NetworkTypeController.this;
                    networkTypeController8.transitionTo(networkTypeController8.mNrConnectedState);
                }
            } else if (NetworkTypeController.this.isLte(dataNetworkType) && NetworkTypeController.this.isNrNotRestricted()) {
                NetworkTypeController networkTypeController9 = NetworkTypeController.this;
                networkTypeController9.transitionWithTimerTo(networkTypeController9.isPhysicalLinkActive() ? NetworkTypeController.this.mLteConnectedState : NetworkTypeController.this.mIdleState);
            } else {
                if (!NetworkTypeController.this.isLte(dataNetworkType)) {
                    NetworkTypeController.this.log("Reset timers since 2G and 3G don't need NR timers.");
                    NetworkTypeController.this.resetAllTimers();
                }
                NetworkTypeController.this.updateOverrideNetworkType();
            }
            this.mIsNrRestricted = NetworkTypeController.this.isNrRestricted();
            return NetworkTypeController.this.mIsPrimaryTimerActive ? true : true;
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public String getName() {
            return this.mIsNrRestricted ? "restricted" : "legacy";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class IdleState extends State {
        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public String getName() {
            return "not_restricted_rrc_idle";
        }

        private IdleState() {
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public void enter() {
            NetworkTypeController.this.log("Entering IdleState");
            NetworkTypeController.this.updateTimers();
            NetworkTypeController.this.updateOverrideNetworkType();
            if (NetworkTypeController.this.mIsPrimaryTimerActive || NetworkTypeController.this.mIsSecondaryTimerActive) {
                return;
            }
            NetworkTypeController.this.mPreviousState = getName();
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public boolean processMessage(Message message) {
            NetworkTypeController networkTypeController = NetworkTypeController.this;
            networkTypeController.log("IdleState: process " + NetworkTypeController.this.getEventName(message.what));
            NetworkTypeController.this.updateTimers();
            int i = message.what;
            if (i != 0) {
                if (i == 11) {
                    NetworkTypeController networkTypeController2 = NetworkTypeController.this;
                    networkTypeController2.mPhysicalChannelConfigs = networkTypeController2.mPhone.getServiceStateTracker().getPhysicalChannelConfigList();
                    NetworkTypeController networkTypeController3 = NetworkTypeController.this;
                    networkTypeController3.log("Physical channel configs updated: " + NetworkTypeController.this.mPhysicalChannelConfigs);
                    if (NetworkTypeController.this.isUsingPhysicalChannelConfigForRrcDetection()) {
                        NetworkTypeController networkTypeController4 = NetworkTypeController.this;
                        networkTypeController4.mPhysicalLinkStatus = networkTypeController4.getPhysicalLinkStatusFromPhysicalChannelConfig();
                        if (NetworkTypeController.this.isPhysicalLinkActive()) {
                            NetworkTypeController networkTypeController5 = NetworkTypeController.this;
                            networkTypeController5.transitionWithTimerTo(networkTypeController5.mLteConnectedState);
                        } else {
                            NetworkTypeController.this.log("Reevaluating state due to link status changed.");
                            NetworkTypeController.this.sendMessage(0);
                        }
                    }
                } else if (i == 3) {
                    NetworkTypeController networkTypeController6 = NetworkTypeController.this;
                    networkTypeController6.mServiceState = networkTypeController6.mPhone.getServiceStateTracker().getServiceState();
                    NetworkTypeController networkTypeController7 = NetworkTypeController.this;
                    networkTypeController7.log("ServiceState updated: " + NetworkTypeController.this.mServiceState);
                } else if (i != 4) {
                    return false;
                } else {
                    NetworkTypeController.this.mPhysicalLinkStatus = ((Integer) ((AsyncResult) message.obj).result).intValue();
                    if (NetworkTypeController.this.isPhysicalLinkActive()) {
                        NetworkTypeController networkTypeController8 = NetworkTypeController.this;
                        networkTypeController8.transitionWithTimerTo(networkTypeController8.mLteConnectedState);
                    } else {
                        NetworkTypeController.this.log("Reevaluating state due to link status changed.");
                        NetworkTypeController.this.sendMessage(0);
                    }
                }
                if (NetworkTypeController.this.mIsPrimaryTimerActive && !NetworkTypeController.this.mIsSecondaryTimerActive) {
                    NetworkTypeController.this.mPreviousState = getName();
                    return true;
                }
            }
            int dataNetworkType = NetworkTypeController.this.getDataNetworkType();
            if (dataNetworkType == 20 || (NetworkTypeController.this.isLte(dataNetworkType) && NetworkTypeController.this.isNrConnected())) {
                if (NetworkTypeController.this.isNrAdvanced()) {
                    NetworkTypeController networkTypeController9 = NetworkTypeController.this;
                    networkTypeController9.transitionTo(networkTypeController9.mNrConnectedAdvancedState);
                } else {
                    NetworkTypeController networkTypeController10 = NetworkTypeController.this;
                    networkTypeController10.transitionTo(networkTypeController10.mNrConnectedState);
                }
            } else if (!NetworkTypeController.this.isLte(dataNetworkType) || !NetworkTypeController.this.isNrNotRestricted()) {
                NetworkTypeController networkTypeController11 = NetworkTypeController.this;
                networkTypeController11.transitionWithTimerTo(networkTypeController11.mLegacyState);
            } else if (NetworkTypeController.this.isPhysicalLinkActive()) {
                NetworkTypeController networkTypeController12 = NetworkTypeController.this;
                networkTypeController12.transitionWithTimerTo(networkTypeController12.mLteConnectedState);
            } else {
                NetworkTypeController.this.updateOverrideNetworkType();
            }
            return NetworkTypeController.this.mIsPrimaryTimerActive ? true : true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class LteConnectedState extends State {
        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public String getName() {
            return "not_restricted_rrc_con";
        }

        private LteConnectedState() {
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public void enter() {
            NetworkTypeController.this.log("Entering LteConnectedState");
            NetworkTypeController.this.updateTimers();
            NetworkTypeController.this.updateOverrideNetworkType();
            if (NetworkTypeController.this.mIsPrimaryTimerActive || NetworkTypeController.this.mIsSecondaryTimerActive) {
                return;
            }
            NetworkTypeController.this.mPreviousState = getName();
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public boolean processMessage(Message message) {
            NetworkTypeController networkTypeController = NetworkTypeController.this;
            networkTypeController.log("LteConnectedState: process " + NetworkTypeController.this.getEventName(message.what));
            NetworkTypeController.this.updateTimers();
            int i = message.what;
            if (i != 0) {
                if (i == 11) {
                    NetworkTypeController networkTypeController2 = NetworkTypeController.this;
                    networkTypeController2.mPhysicalChannelConfigs = networkTypeController2.mPhone.getServiceStateTracker().getPhysicalChannelConfigList();
                    NetworkTypeController networkTypeController3 = NetworkTypeController.this;
                    networkTypeController3.log("Physical channel configs updated: " + NetworkTypeController.this.mPhysicalChannelConfigs);
                    if (NetworkTypeController.this.isUsingPhysicalChannelConfigForRrcDetection()) {
                        NetworkTypeController networkTypeController4 = NetworkTypeController.this;
                        networkTypeController4.mPhysicalLinkStatus = networkTypeController4.getPhysicalLinkStatusFromPhysicalChannelConfig();
                        if (!NetworkTypeController.this.isPhysicalLinkActive()) {
                            NetworkTypeController networkTypeController5 = NetworkTypeController.this;
                            networkTypeController5.transitionWithTimerTo(networkTypeController5.mIdleState);
                        } else {
                            NetworkTypeController.this.log("Reevaluating state due to link status changed.");
                            NetworkTypeController.this.sendMessage(0);
                        }
                    }
                } else if (i == 3) {
                    NetworkTypeController networkTypeController6 = NetworkTypeController.this;
                    networkTypeController6.mServiceState = networkTypeController6.mPhone.getServiceStateTracker().getServiceState();
                    NetworkTypeController networkTypeController7 = NetworkTypeController.this;
                    networkTypeController7.log("ServiceState updated: " + NetworkTypeController.this.mServiceState);
                } else if (i != 4) {
                    return false;
                } else {
                    NetworkTypeController.this.mPhysicalLinkStatus = ((Integer) ((AsyncResult) message.obj).result).intValue();
                    if (!NetworkTypeController.this.isPhysicalLinkActive()) {
                        NetworkTypeController networkTypeController8 = NetworkTypeController.this;
                        networkTypeController8.transitionWithTimerTo(networkTypeController8.mIdleState);
                    } else {
                        NetworkTypeController.this.log("Reevaluating state due to link status changed.");
                        NetworkTypeController.this.sendMessage(0);
                    }
                }
                if (NetworkTypeController.this.mIsPrimaryTimerActive && !NetworkTypeController.this.mIsSecondaryTimerActive) {
                    NetworkTypeController.this.mPreviousState = getName();
                    return true;
                }
            }
            int dataNetworkType = NetworkTypeController.this.getDataNetworkType();
            if (dataNetworkType == 20 || (NetworkTypeController.this.isLte(dataNetworkType) && NetworkTypeController.this.isNrConnected())) {
                if (NetworkTypeController.this.isNrAdvanced()) {
                    NetworkTypeController networkTypeController9 = NetworkTypeController.this;
                    networkTypeController9.transitionTo(networkTypeController9.mNrConnectedAdvancedState);
                } else {
                    NetworkTypeController networkTypeController10 = NetworkTypeController.this;
                    networkTypeController10.transitionTo(networkTypeController10.mNrConnectedState);
                }
            } else if (!NetworkTypeController.this.isLte(dataNetworkType) || !NetworkTypeController.this.isNrNotRestricted()) {
                NetworkTypeController networkTypeController11 = NetworkTypeController.this;
                networkTypeController11.transitionWithTimerTo(networkTypeController11.mLegacyState);
            } else if (!NetworkTypeController.this.isPhysicalLinkActive()) {
                NetworkTypeController networkTypeController12 = NetworkTypeController.this;
                networkTypeController12.transitionWithTimerTo(networkTypeController12.mIdleState);
            } else {
                NetworkTypeController.this.updateOverrideNetworkType();
            }
            return NetworkTypeController.this.mIsPrimaryTimerActive ? true : true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class NrConnectedState extends State {
        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public String getName() {
            return PhoneInternalInterface.REASON_CONNECTED;
        }

        private NrConnectedState() {
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public void enter() {
            NetworkTypeController.this.log("Entering NrConnectedState");
            NetworkTypeController.this.updateTimers();
            NetworkTypeController.this.updateOverrideNetworkType();
            if (NetworkTypeController.this.mIsPrimaryTimerActive || NetworkTypeController.this.mIsSecondaryTimerActive) {
                return;
            }
            NetworkTypeController.this.mPreviousState = getName();
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public boolean processMessage(Message message) {
            NetworkTypeController networkTypeController = NetworkTypeController.this;
            networkTypeController.log("NrConnectedState: process " + NetworkTypeController.this.getEventName(message.what));
            NetworkTypeController.this.updateTimers();
            int i = message.what;
            if (i != 0) {
                if (i == 11) {
                    NetworkTypeController networkTypeController2 = NetworkTypeController.this;
                    networkTypeController2.mPhysicalChannelConfigs = networkTypeController2.mPhone.getServiceStateTracker().getPhysicalChannelConfigList();
                    NetworkTypeController networkTypeController3 = NetworkTypeController.this;
                    networkTypeController3.log("Physical channel configs updated: " + NetworkTypeController.this.mPhysicalChannelConfigs);
                    if (NetworkTypeController.this.isUsingPhysicalChannelConfigForRrcDetection()) {
                        NetworkTypeController networkTypeController4 = NetworkTypeController.this;
                        networkTypeController4.mPhysicalLinkStatus = networkTypeController4.getPhysicalLinkStatusFromPhysicalChannelConfig();
                    }
                    if (NetworkTypeController.this.isNrAdvanced()) {
                        NetworkTypeController networkTypeController5 = NetworkTypeController.this;
                        networkTypeController5.transitionTo(networkTypeController5.mNrConnectedAdvancedState);
                    }
                } else if (i == 3) {
                    NetworkTypeController networkTypeController6 = NetworkTypeController.this;
                    networkTypeController6.mServiceState = networkTypeController6.mPhone.getServiceStateTracker().getServiceState();
                    NetworkTypeController networkTypeController7 = NetworkTypeController.this;
                    networkTypeController7.log("ServiceState updated: " + NetworkTypeController.this.mServiceState);
                } else if (i != 4) {
                    return false;
                } else {
                    NetworkTypeController.this.mPhysicalLinkStatus = ((Integer) ((AsyncResult) message.obj).result).intValue();
                }
                if (NetworkTypeController.this.mIsPrimaryTimerActive && !NetworkTypeController.this.mIsSecondaryTimerActive) {
                    NetworkTypeController.this.mPreviousState = getName();
                    return true;
                }
            }
            int dataNetworkType = NetworkTypeController.this.getDataNetworkType();
            if (dataNetworkType == 20 || (NetworkTypeController.this.isLte(dataNetworkType) && NetworkTypeController.this.isNrConnected())) {
                if (NetworkTypeController.this.isNrAdvanced()) {
                    NetworkTypeController networkTypeController8 = NetworkTypeController.this;
                    networkTypeController8.transitionTo(networkTypeController8.mNrConnectedAdvancedState);
                } else {
                    NetworkTypeController.this.updateOverrideNetworkType();
                }
            } else if (NetworkTypeController.this.isLte(dataNetworkType) && NetworkTypeController.this.isNrNotRestricted()) {
                NetworkTypeController networkTypeController9 = NetworkTypeController.this;
                networkTypeController9.transitionWithTimerTo(networkTypeController9.isPhysicalLinkActive() ? NetworkTypeController.this.mLteConnectedState : NetworkTypeController.this.mIdleState);
            } else {
                NetworkTypeController networkTypeController10 = NetworkTypeController.this;
                networkTypeController10.transitionWithTimerTo(networkTypeController10.mLegacyState);
            }
            return NetworkTypeController.this.mIsPrimaryTimerActive ? true : true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class NrConnectedAdvancedState extends State {
        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public String getName() {
            return "connected_mmwave";
        }

        private NrConnectedAdvancedState() {
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public void enter() {
            NetworkTypeController.this.log("Entering NrConnectedAdvancedState");
            NetworkTypeController.this.updateTimers();
            NetworkTypeController.this.updateOverrideNetworkType();
            if (NetworkTypeController.this.mIsPrimaryTimerActive || NetworkTypeController.this.mIsSecondaryTimerActive) {
                return;
            }
            NetworkTypeController.this.mPreviousState = getName();
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public boolean processMessage(Message message) {
            NetworkTypeController networkTypeController = NetworkTypeController.this;
            networkTypeController.log("NrConnectedAdvancedState: process " + NetworkTypeController.this.getEventName(message.what));
            NetworkTypeController.this.updateTimers();
            int i = message.what;
            if (i != 0) {
                if (i == 11) {
                    NetworkTypeController networkTypeController2 = NetworkTypeController.this;
                    networkTypeController2.mPhysicalChannelConfigs = networkTypeController2.mPhone.getServiceStateTracker().getPhysicalChannelConfigList();
                    NetworkTypeController networkTypeController3 = NetworkTypeController.this;
                    networkTypeController3.log("Physical channel configs updated: " + NetworkTypeController.this.mPhysicalChannelConfigs);
                    if (NetworkTypeController.this.isUsingPhysicalChannelConfigForRrcDetection()) {
                        NetworkTypeController networkTypeController4 = NetworkTypeController.this;
                        networkTypeController4.mPhysicalLinkStatus = networkTypeController4.getPhysicalLinkStatusFromPhysicalChannelConfig();
                    }
                    if (!NetworkTypeController.this.isNrAdvanced()) {
                        NetworkTypeController networkTypeController5 = NetworkTypeController.this;
                        networkTypeController5.transitionWithTimerTo(networkTypeController5.mNrConnectedState);
                    }
                } else if (i == 3) {
                    NetworkTypeController networkTypeController6 = NetworkTypeController.this;
                    networkTypeController6.mServiceState = networkTypeController6.mPhone.getServiceStateTracker().getServiceState();
                    NetworkTypeController networkTypeController7 = NetworkTypeController.this;
                    networkTypeController7.log("ServiceState updated: " + NetworkTypeController.this.mServiceState);
                } else if (i != 4) {
                    return false;
                } else {
                    NetworkTypeController.this.mPhysicalLinkStatus = ((Integer) ((AsyncResult) message.obj).result).intValue();
                }
                if (NetworkTypeController.this.mIsPrimaryTimerActive && !NetworkTypeController.this.mIsSecondaryTimerActive) {
                    NetworkTypeController.this.mPreviousState = getName();
                    return true;
                }
            }
            int dataNetworkType = NetworkTypeController.this.getDataNetworkType();
            if (dataNetworkType == 20 || (NetworkTypeController.this.isLte(dataNetworkType) && NetworkTypeController.this.isNrConnected())) {
                if (NetworkTypeController.this.isNrAdvanced()) {
                    NetworkTypeController.this.updateOverrideNetworkType();
                } else {
                    if (dataNetworkType == 20 && NetworkTypeController.this.mOverrideNetworkType != 5) {
                        NetworkTypeController.this.mOverrideNetworkType = 0;
                    }
                    NetworkTypeController networkTypeController8 = NetworkTypeController.this;
                    networkTypeController8.transitionWithTimerTo(networkTypeController8.mNrConnectedState);
                }
            } else if (NetworkTypeController.this.isLte(dataNetworkType) && NetworkTypeController.this.isNrNotRestricted()) {
                NetworkTypeController networkTypeController9 = NetworkTypeController.this;
                networkTypeController9.transitionWithTimerTo(networkTypeController9.isPhysicalLinkActive() ? NetworkTypeController.this.mLteConnectedState : NetworkTypeController.this.mIdleState);
            } else {
                NetworkTypeController networkTypeController10 = NetworkTypeController.this;
                networkTypeController10.transitionWithTimerTo(networkTypeController10.mLegacyState);
            }
            return NetworkTypeController.this.mIsPrimaryTimerActive ? true : true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void transitionWithTimerTo(IState iState) {
        String name = iState.getName();
        log("Transition with primary timer from " + this.mPreviousState + " to " + name);
        OverrideTimerRule overrideTimerRule = this.mOverrideTimerRules.get(this.mPreviousState);
        if (!this.mIsDeviceIdleMode && overrideTimerRule != null && overrideTimerRule.getTimer(name) > 0) {
            int timer = overrideTimerRule.getTimer(name);
            log(timer + "s primary timer started for state: " + this.mPreviousState);
            this.mPrimaryTimerState = this.mPreviousState;
            this.mPreviousState = getCurrentState().getName();
            this.mIsPrimaryTimerActive = true;
            sendMessageDelayed(7, iState, ((long) timer) * 1000);
        }
        transitionTo(iState);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void transitionWithSecondaryTimerTo(IState iState) {
        String name = getCurrentState().getName();
        OverrideTimerRule overrideTimerRule = this.mOverrideTimerRules.get(this.mPrimaryTimerState);
        log("Transition with secondary timer from " + name + " to " + iState.getName());
        if (!this.mIsDeviceIdleMode && overrideTimerRule != null && overrideTimerRule.getSecondaryTimer(name) > 0) {
            int secondaryTimer = overrideTimerRule.getSecondaryTimer(name);
            log(secondaryTimer + "s secondary timer started for state: " + name);
            this.mSecondaryTimerState = name;
            this.mPreviousState = name;
            this.mIsSecondaryTimerActive = true;
            sendMessageDelayed(8, iState, ((long) secondaryTimer) * 1000);
        }
        this.mIsPrimaryTimerActive = false;
        transitionTo(getCurrentState());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void transitionToCurrentState() {
        IState iState;
        int dataNetworkType = getDataNetworkType();
        if (dataNetworkType == 20 || (isLte(dataNetworkType) && isNrConnected())) {
            if (isNrAdvanced()) {
                iState = this.mNrConnectedAdvancedState;
            } else {
                iState = this.mNrConnectedState;
            }
        } else if (isLte(dataNetworkType) && isNrNotRestricted()) {
            if (isPhysicalLinkActive()) {
                iState = this.mLteConnectedState;
            } else {
                iState = this.mIdleState;
            }
        } else {
            iState = this.mLegacyState;
        }
        if (!iState.equals(getCurrentState())) {
            this.mPreviousState = getCurrentState().getName();
            transitionTo(iState);
            return;
        }
        updateOverrideNetworkType();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTimers() {
        if ((this.mPhone.getCachedAllowedNetworkTypesBitmask() & 524288) == 0) {
            log("Reset timers since NR is not allowed.");
            resetAllTimers();
            return;
        }
        String name = getCurrentState().getName();
        if (this.mIsPrimaryTimerActive && getOverrideNetworkType() == getCurrentOverrideNetworkType()) {
            log("Remove primary timer since icon of primary state and current icon equal: " + this.mPrimaryTimerState);
            removeMessages(7);
            this.mIsPrimaryTimerActive = false;
            this.mPrimaryTimerState = PhoneConfigurationManager.SSSS;
            transitionToCurrentState();
        } else if (this.mIsSecondaryTimerActive && !this.mSecondaryTimerState.equals(name)) {
            log("Remove secondary timer since current state (" + name + ") is no longer secondary timer state (" + this.mSecondaryTimerState + ").");
            removeMessages(8);
            this.mIsSecondaryTimerActive = false;
            this.mSecondaryTimerState = PhoneConfigurationManager.SSSS;
            transitionToCurrentState();
        } else if (this.mIsPrimaryTimerActive || this.mIsSecondaryTimerActive) {
            if (name.equals("connected_mmwave")) {
                log("Reset timers since state is NR_ADVANCED.");
                resetAllTimers();
            } else if (name.equals(PhoneInternalInterface.REASON_CONNECTED) && !this.mPrimaryTimerState.equals("connected_mmwave") && !this.mSecondaryTimerState.equals("connected_mmwave")) {
                log("Reset non-NR_ADVANCED timers since state is NR_CONNECTED");
                resetAllTimers();
            } else {
                int dataNetworkType = getDataNetworkType();
                if (isLte(dataNetworkType) || dataNetworkType == 20) {
                    return;
                }
                log("Reset timers since 2G and 3G don't need NR timers.");
                resetAllTimers();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetAllTimers() {
        removeMessages(7);
        removeMessages(8);
        this.mIsPrimaryTimerActive = false;
        this.mIsSecondaryTimerActive = false;
        this.mPrimaryTimerState = PhoneConfigurationManager.SSSS;
        this.mSecondaryTimerState = PhoneConfigurationManager.SSSS;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class OverrideTimerRule {
        final int mOverrideType;
        final Map<String, Integer> mPrimaryTimers = new HashMap();
        final Map<String, Integer> mSecondaryTimers = new HashMap();
        final String mState;

        OverrideTimerRule(String str, int i) {
            this.mState = str;
            this.mOverrideType = i;
        }

        public void addTimer(String str, int i) {
            this.mPrimaryTimers.put(str, Integer.valueOf(i));
        }

        public void addSecondaryTimer(String str, int i) {
            this.mSecondaryTimers.put(str, Integer.valueOf(i));
        }

        public int getTimer(String str) {
            Integer num = this.mPrimaryTimers.get(str);
            if (num == null) {
                num = this.mPrimaryTimers.get("any");
            }
            if (num == null) {
                return 0;
            }
            return num.intValue();
        }

        public int getSecondaryTimer(String str) {
            Integer num = this.mSecondaryTimers.get(str);
            if (num == null) {
                num = this.mSecondaryTimers.get("any");
            }
            if (num == null) {
                return 0;
            }
            return num.intValue();
        }

        public String toString() {
            return "{mState=" + this.mState + ", mOverrideType=" + TelephonyDisplayInfo.overrideNetworkTypeToString(this.mOverrideType) + ", mPrimaryTimers=" + this.mPrimaryTimers + ", mSecondaryTimers=" + this.mSecondaryTimers + "}";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isNrConnected() {
        return this.mServiceState.getNrState() == 3;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isNrNotRestricted() {
        return this.mServiceState.getNrState() == 2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isNrRestricted() {
        return this.mServiceState.getNrState() == 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isNrAdvanced() {
        if (this.mNrAdvancedCapablePcoId <= 0 || this.mIsNrAdvancedAllowedByPco) {
            if (!this.mServiceState.getDataRoaming() || this.mEnableNrAdvancedWhileRoaming) {
                if (this.mNrAdvancedThresholdBandwidth <= 0 || IntStream.of(this.mPhone.getServiceState().getCellBandwidths()).sum() >= this.mNrAdvancedThresholdBandwidth) {
                    return isNrMmwave() || isAdditionalNrAdvancedBand();
                }
                return false;
            }
            return false;
        }
        return false;
    }

    private boolean isNrMmwave() {
        return this.mServiceState.getNrFrequencyRange() == 4;
    }

    private boolean isAdditionalNrAdvancedBand() {
        List<PhysicalChannelConfig> list;
        if (!ArrayUtils.isEmpty(this.mAdditionalNrAdvancedBandsList) && (list = this.mPhysicalChannelConfigs) != null) {
            for (PhysicalChannelConfig physicalChannelConfig : list) {
                if (physicalChannelConfig.getNetworkType() == 20 && ArrayUtils.contains(this.mAdditionalNrAdvancedBandsList, physicalChannelConfig.getBand())) {
                    return true;
                }
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isPhysicalLinkActive() {
        return this.mPhysicalLinkStatus == 2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getPhysicalLinkStatusFromPhysicalChannelConfig() {
        List<PhysicalChannelConfig> list = this.mPhysicalChannelConfigs;
        return (list == null || list.isEmpty()) ? 1 : 2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getEventName(int i) {
        try {
            return sEvents[i];
        } catch (ArrayIndexOutOfBoundsException unused) {
            return "EVENT_NOT_DEFINED";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isUsingPhysicalChannelConfigForRrcDetection() {
        return this.mIsPhysicalChannelConfig16Supported && !this.mIsUsingUserDataForRrcDetection;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.StateMachine
    public void log(String str) {
        Rlog.d("NetworkTypeController", "[" + this.mPhone.getPhoneId() + "] " + str);
    }

    @Override // com.android.internal.telephony.StateMachine
    protected void loge(String str) {
        Rlog.e("NetworkTypeController", "[" + this.mPhone.getPhoneId() + "] " + str);
    }

    @Override // com.android.internal.telephony.StateMachine
    public String toString() {
        return "mOverrideTimerRules=" + this.mOverrideTimerRules.toString() + ", mLteEnhancedPattern=" + this.mLteEnhancedPattern + ", mIsPhysicalChannelConfigOn=" + this.mIsPhysicalChannelConfigOn + ", mIsPrimaryTimerActive=" + this.mIsPrimaryTimerActive + ", mIsSecondaryTimerActive=" + this.mIsSecondaryTimerActive + ", mPrimaryTimerState=" + this.mPrimaryTimerState + ", mSecondaryTimerState=" + this.mSecondaryTimerState + ", mPreviousState=" + this.mPreviousState + ", mIsNrAdvanced=" + isNrAdvanced();
    }

    @Override // com.android.internal.telephony.StateMachine
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, " ");
        indentingPrintWriter.print("NetworkTypeController: ");
        super.dump(fileDescriptor, indentingPrintWriter, strArr);
        indentingPrintWriter.flush();
        indentingPrintWriter.increaseIndent();
        indentingPrintWriter.println("mSubId=" + this.mPhone.getSubId());
        indentingPrintWriter.println("mOverrideTimerRules=" + this.mOverrideTimerRules.toString());
        indentingPrintWriter.println("mLteEnhancedPattern=" + this.mLteEnhancedPattern);
        indentingPrintWriter.println("mIsPhysicalChannelConfigOn=" + this.mIsPhysicalChannelConfigOn);
        indentingPrintWriter.println("mIsPrimaryTimerActive=" + this.mIsPrimaryTimerActive);
        indentingPrintWriter.println("mIsSecondaryTimerActive=" + this.mIsSecondaryTimerActive);
        indentingPrintWriter.println("mIsTimerResetEnabledForLegacyStateRrcIdle=" + this.mIsTimerResetEnabledForLegacyStateRrcIdle);
        indentingPrintWriter.println("mLtePlusThresholdBandwidth=" + this.mLtePlusThresholdBandwidth);
        indentingPrintWriter.println("mNrAdvancedThresholdBandwidth=" + this.mNrAdvancedThresholdBandwidth);
        indentingPrintWriter.println("mAdditionalNrAdvancedBandsList=" + Arrays.toString(this.mAdditionalNrAdvancedBandsList));
        indentingPrintWriter.println("mPrimaryTimerState=" + this.mPrimaryTimerState);
        indentingPrintWriter.println("mSecondaryTimerState=" + this.mSecondaryTimerState);
        indentingPrintWriter.println("mPreviousState=" + this.mPreviousState);
        indentingPrintWriter.println("mPhysicalLinkStatus=" + DataUtils.linkStatusToString(this.mPhysicalLinkStatus));
        indentingPrintWriter.println("mIsPhysicalChannelConfig16Supported=" + this.mIsPhysicalChannelConfig16Supported);
        indentingPrintWriter.println("mIsNrAdvancedAllowedByPco=" + this.mIsNrAdvancedAllowedByPco);
        indentingPrintWriter.println("mNrAdvancedCapablePcoId=" + this.mNrAdvancedCapablePcoId);
        indentingPrintWriter.println("mIsUsingUserDataForRrcDetection=" + this.mIsUsingUserDataForRrcDetection);
        indentingPrintWriter.println("mEnableNrAdvancedWhileRoaming=" + this.mEnableNrAdvancedWhileRoaming);
        indentingPrintWriter.println("mIsDeviceIdleMode=" + this.mIsDeviceIdleMode);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.flush();
    }
}
