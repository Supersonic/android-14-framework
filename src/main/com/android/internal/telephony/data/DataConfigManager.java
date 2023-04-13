package com.android.internal.telephony.data;

import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.provider.DeviceConfig;
import android.telephony.CarrierConfigManager;
import android.telephony.ServiceState;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyDisplayInfo;
import android.telephony.data.ApnSetting;
import android.text.TextUtils;
import android.util.ArraySet;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.AndroidUtilIndentingPrintWriter;
import com.android.internal.telephony.NetworkTypeController$$ExternalSyntheticLambda1;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.data.DataConfigManager;
import com.android.internal.telephony.data.DataNetwork;
import com.android.internal.telephony.data.DataNetworkController;
import com.android.internal.telephony.data.DataRetryManager;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
/* loaded from: classes.dex */
public class DataConfigManager extends Handler {
    private final Map<String, DataNetwork.NetworkBandwidth> mBandwidthMap;
    private final Set<Integer> mCapabilitiesExemptFromSingleDataList;
    private PersistableBundle mCarrierConfig;
    private final CarrierConfigManager mCarrierConfigManager;
    private final Set<DataConfigManagerCallback> mDataConfigManagerCallbacks;
    private final List<DataRetryManager.DataHandoverRetryRule> mDataHandoverRetryRules;
    private final List<DataRetryManager.DataSetupRetryRule> mDataSetupRetryRules;
    private final List<DataNetworkController.HandoverRule> mHandoverRuleList;
    private EventFrequency mImsReleaseRequestAnomalyReportThreshold;
    private boolean mIsApnConfigAnomalyReportEnabled;
    private boolean mIsInvalidQnsParamAnomalyReportEnabled;
    private final String mLogTag;
    private final Set<Integer> mMeteredApnTypes;
    private final Map<Integer, Integer> mNetworkCapabilityPriorityMap;
    private int mNetworkConnectingTimeout;
    private int mNetworkDisconnectingTimeout;
    private int mNetworkHandoverTimeout;
    private EventFrequency mNetworkUnwantedAnomalyReportThreshold;
    private final Phone mPhone;
    private boolean mPingTestBeforeDataSwitch;
    private Resources mResources;
    private final Set<Integer> mRoamingMeteredApnTypes;
    private final Set<String> mRoamingUnmeteredNetworkTypes;
    private EventFrequency mSetupDataCallAnomalyReportThreshold;
    private boolean mShouldKeepNetworkUpInNonVops;
    private final List<Integer> mSingleDataNetworkTypeList;
    private final Map<String, String> mTcpBufferSizeMap;
    private final Set<String> mUnmeteredNetworkTypes;

    private static String networkTypeToDataConfigNetworkType(int i) {
        switch (i) {
            case 1:
                return "GPRS";
            case 2:
                return "EDGE";
            case 3:
                return "UMTS";
            case 4:
                return "CDMA";
            case 5:
                return "EvDo_0";
            case 6:
                return "EvDo_A";
            case 7:
                return "1xRTT";
            case 8:
                return "HSDPA";
            case 9:
                return "HSUPA";
            case 10:
                return "HSPA";
            case 11:
                return "iDEN";
            case 12:
                return "EvDo_B";
            case 13:
                return "LTE";
            case 14:
                return "eHRPD";
            case 15:
                return "HSPA+";
            case 16:
                return "GSM";
            case 17:
                return "TD_SCDMA";
            case 18:
                return "IWLAN";
            case 19:
                return "LTE_CA";
            case 20:
                return "NR_SA";
            default:
                return PhoneConfigurationManager.SSSS;
        }
    }

    public DataConfigManager(Phone phone, Looper looper) {
        super(looper);
        this.mDataConfigManagerCallbacks = new ArraySet();
        this.mCarrierConfig = null;
        this.mResources = null;
        this.mNetworkCapabilityPriorityMap = new ConcurrentHashMap();
        this.mDataSetupRetryRules = new ArrayList();
        this.mDataHandoverRetryRules = new ArrayList();
        this.mMeteredApnTypes = new HashSet();
        this.mRoamingMeteredApnTypes = new HashSet();
        this.mSingleDataNetworkTypeList = new ArrayList();
        this.mCapabilitiesExemptFromSingleDataList = new HashSet();
        this.mUnmeteredNetworkTypes = new HashSet();
        this.mRoamingUnmeteredNetworkTypes = new HashSet();
        this.mBandwidthMap = new ConcurrentHashMap();
        this.mTcpBufferSizeMap = new ConcurrentHashMap();
        this.mHandoverRuleList = new ArrayList();
        this.mShouldKeepNetworkUpInNonVops = false;
        this.mPingTestBeforeDataSwitch = true;
        this.mPhone = phone;
        this.mLogTag = "DCM-" + phone.getPhoneId();
        log("DataConfigManager created.");
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) phone.getContext().getSystemService(CarrierConfigManager.class);
        this.mCarrierConfigManager = carrierConfigManager;
        carrierConfigManager.registerCarrierConfigChangeListener(new NetworkTypeController$$ExternalSyntheticLambda1(), new CarrierConfigManager.CarrierConfigChangeListener() { // from class: com.android.internal.telephony.data.DataConfigManager$$ExternalSyntheticLambda12
            public final void onCarrierConfigChanged(int i, int i2, int i3, int i4) {
                DataConfigManager.this.lambda$new$0(i, i2, i3, i4);
            }
        });
        DeviceConfig.addOnPropertiesChangedListener("telephony", new Executor() { // from class: com.android.internal.telephony.data.DataConfigManager$$ExternalSyntheticLambda13
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                DataConfigManager.this.post(runnable);
            }
        }, new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.internal.telephony.data.DataConfigManager$$ExternalSyntheticLambda14
            public final void onPropertiesChanged(DeviceConfig.Properties properties) {
                DataConfigManager.this.lambda$new$1(properties);
            }
        });
        updateCarrierConfig();
        updateDeviceConfig();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i, int i2, int i3, int i4) {
        if (i == this.mPhone.getPhoneId()) {
            sendEmptyMessage(1);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(DeviceConfig.Properties properties) {
        if (TextUtils.equals("telephony", properties.getNamespace())) {
            sendEmptyMessage(2);
        }
    }

    /* loaded from: classes.dex */
    public static class DataConfigManagerCallback extends DataCallback {
        public void onCarrierConfigChanged() {
        }

        public void onDeviceConfigChanged() {
        }

        public DataConfigManagerCallback(Executor executor) {
            super(executor);
        }
    }

    public void registerCallback(DataConfigManagerCallback dataConfigManagerCallback) {
        this.mDataConfigManagerCallbacks.add(dataConfigManagerCallback);
    }

    public void unregisterCallback(DataConfigManagerCallback dataConfigManagerCallback) {
        this.mDataConfigManagerCallbacks.remove(dataConfigManagerCallback);
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i = message.what;
        if (i == 1) {
            log("EVENT_CARRIER_CONFIG_CHANGED");
            updateCarrierConfig();
            this.mDataConfigManagerCallbacks.forEach(new Consumer() { // from class: com.android.internal.telephony.data.DataConfigManager$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    DataConfigManager.lambda$handleMessage$2((DataConfigManager.DataConfigManagerCallback) obj);
                }
            });
        } else if (i == 2) {
            log("EVENT_DEVICE_CONFIG_CHANGED");
            updateDeviceConfig();
            this.mDataConfigManagerCallbacks.forEach(new Consumer() { // from class: com.android.internal.telephony.data.DataConfigManager$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    DataConfigManager.lambda$handleMessage$3((DataConfigManager.DataConfigManagerCallback) obj);
                }
            });
        } else {
            loge("Unexpected message " + message.what);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$handleMessage$2(final DataConfigManagerCallback dataConfigManagerCallback) {
        Objects.requireNonNull(dataConfigManagerCallback);
        dataConfigManagerCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.DataConfigManager$$ExternalSyntheticLambda19
            @Override // java.lang.Runnable
            public final void run() {
                DataConfigManager.DataConfigManagerCallback.this.onCarrierConfigChanged();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$handleMessage$3(final DataConfigManagerCallback dataConfigManagerCallback) {
        Objects.requireNonNull(dataConfigManagerCallback);
        dataConfigManagerCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.DataConfigManager$$ExternalSyntheticLambda22
            @Override // java.lang.Runnable
            public final void run() {
                DataConfigManager.DataConfigManagerCallback.this.onDeviceConfigChanged();
            }
        });
    }

    private void updateDeviceConfig() {
        DeviceConfig.Properties properties = DeviceConfig.getProperties("telephony", new String[0]);
        this.mImsReleaseRequestAnomalyReportThreshold = parseSlidingWindowCounterThreshold(properties.getString("anomaly_ims_release_request", (String) null), 0L, 2);
        this.mNetworkUnwantedAnomalyReportThreshold = parseSlidingWindowCounterThreshold(properties.getString("anomaly_network_unwanted", (String) null), 0L, 12);
        this.mSetupDataCallAnomalyReportThreshold = parseSlidingWindowCounterThreshold(properties.getString("anomaly_setup_data_call_failure", (String) null), 0L, 12);
        this.mIsInvalidQnsParamAnomalyReportEnabled = properties.getBoolean("anomaly_qns_param", false);
        this.mNetworkConnectingTimeout = properties.getInt("anomaly_network_connecting_timeout", 300000);
        this.mNetworkDisconnectingTimeout = properties.getInt("anomaly_network_disconnecting_timeout", 300000);
        this.mNetworkHandoverTimeout = properties.getInt("anomaly_network_handover_timeout", 300000);
        this.mIsApnConfigAnomalyReportEnabled = properties.getBoolean("anomaly_apn_config_enabled", false);
    }

    public boolean isConfigCarrierSpecific() {
        return this.mCarrierConfig.getBoolean("carrier_config_applied_bool");
    }

    private void updateCarrierConfig() {
        CarrierConfigManager carrierConfigManager = this.mCarrierConfigManager;
        if (carrierConfigManager != null) {
            this.mCarrierConfig = carrierConfigManager.getConfigForSubId(this.mPhone.getSubId());
        }
        if (this.mCarrierConfig == null) {
            this.mCarrierConfig = CarrierConfigManager.getDefaultConfig();
        }
        this.mResources = SubscriptionManager.getResourcesForSubId(this.mPhone.getContext(), this.mPhone.getSubId());
        updateNetworkCapabilityPriority();
        updateDataRetryRules();
        updateMeteredApnTypes();
        updateSingleDataNetworkTypeAndCapabilityExemption();
        updateVopsConfig();
        updateDataSwitchConfig();
        updateUnmeteredNetworkTypes();
        updateBandwidths();
        updateTcpBuffers();
        updateHandoverRules();
        StringBuilder sb = new StringBuilder();
        sb.append("Carrier config updated. Config is ");
        sb.append(isConfigCarrierSpecific() ? PhoneConfigurationManager.SSSS : "not ");
        sb.append("carrier specific.");
        log(sb.toString());
    }

    private void updateNetworkCapabilityPriority() {
        synchronized (this) {
            this.mNetworkCapabilityPriorityMap.clear();
            String[] stringArray = this.mCarrierConfig.getStringArray("telephony_network_capability_priorities_string_array");
            if (stringArray != null) {
                for (String str : stringArray) {
                    String upperCase = str.trim().toUpperCase(Locale.ROOT);
                    String[] split = upperCase.split(":");
                    if (split.length != 2) {
                        loge("Invalid config \"" + upperCase + "\"");
                    } else {
                        int networkCapabilityFromString = DataUtils.getNetworkCapabilityFromString(split[0]);
                        if (networkCapabilityFromString < 0) {
                            loge("Invalid config \"" + upperCase + "\"");
                        } else {
                            this.mNetworkCapabilityPriorityMap.put(Integer.valueOf(networkCapabilityFromString), Integer.valueOf(Integer.parseInt(split[1])));
                        }
                    }
                }
            }
        }
    }

    public int getNetworkCapabilityPriority(int i) {
        if (this.mNetworkCapabilityPriorityMap.containsKey(Integer.valueOf(i))) {
            return this.mNetworkCapabilityPriorityMap.get(Integer.valueOf(i)).intValue();
        }
        return 0;
    }

    private void updateDataRetryRules() {
        synchronized (this) {
            this.mDataSetupRetryRules.clear();
            String[] stringArray = this.mCarrierConfig.getStringArray("telephony_data_setup_retry_rules_string_array");
            if (stringArray != null) {
                for (String str : stringArray) {
                    try {
                        this.mDataSetupRetryRules.add(new DataRetryManager.DataSetupRetryRule(str));
                    } catch (IllegalArgumentException e) {
                        loge("updateDataRetryRules: " + e.getMessage());
                    }
                }
            }
            this.mDataHandoverRetryRules.clear();
            String[] stringArray2 = this.mCarrierConfig.getStringArray("telephony_data_handover_retry_rules_string_array");
            if (stringArray2 != null) {
                for (String str2 : stringArray2) {
                    try {
                        this.mDataHandoverRetryRules.add(new DataRetryManager.DataHandoverRetryRule(str2));
                    } catch (IllegalArgumentException e2) {
                        loge("updateDataRetryRules: " + e2.getMessage());
                    }
                }
            }
        }
    }

    public List<DataRetryManager.DataSetupRetryRule> getDataSetupRetryRules() {
        return Collections.unmodifiableList(this.mDataSetupRetryRules);
    }

    public List<DataRetryManager.DataHandoverRetryRule> getDataHandoverRetryRules() {
        return Collections.unmodifiableList(this.mDataHandoverRetryRules);
    }

    public boolean isDataRoamingEnabledByDefault() {
        return this.mCarrierConfig.getBoolean("carrier_default_data_roaming_enabled_bool");
    }

    private void updateMeteredApnTypes() {
        synchronized (this) {
            this.mMeteredApnTypes.clear();
            String[] stringArray = this.mCarrierConfig.getStringArray("carrier_metered_apn_types_strings");
            if (stringArray != null) {
                Stream map = Arrays.stream(stringArray).map(new Function() { // from class: com.android.internal.telephony.data.DataConfigManager$$ExternalSyntheticLambda20
                    @Override // java.util.function.Function
                    public final Object apply(Object obj) {
                        return Integer.valueOf(ApnSetting.getApnTypeInt((String) obj));
                    }
                });
                final Set<Integer> set = this.mMeteredApnTypes;
                Objects.requireNonNull(set);
                map.forEach(new Consumer() { // from class: com.android.internal.telephony.data.DataConfigManager$$ExternalSyntheticLambda21
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        set.add((Integer) obj);
                    }
                });
            }
            this.mRoamingMeteredApnTypes.clear();
            String[] stringArray2 = this.mCarrierConfig.getStringArray("carrier_metered_roaming_apn_types_strings");
            if (stringArray2 != null) {
                Stream map2 = Arrays.stream(stringArray2).map(new Function() { // from class: com.android.internal.telephony.data.DataConfigManager$$ExternalSyntheticLambda20
                    @Override // java.util.function.Function
                    public final Object apply(Object obj) {
                        return Integer.valueOf(ApnSetting.getApnTypeInt((String) obj));
                    }
                });
                final Set<Integer> set2 = this.mRoamingMeteredApnTypes;
                Objects.requireNonNull(set2);
                map2.forEach(new Consumer() { // from class: com.android.internal.telephony.data.DataConfigManager$$ExternalSyntheticLambda21
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        set2.add((Integer) obj);
                    }
                });
            }
        }
    }

    public Set<Integer> getMeteredNetworkCapabilities(boolean z) {
        return (Set) (z ? this.mRoamingMeteredApnTypes : this.mMeteredApnTypes).stream().map(new DataConfigManager$$ExternalSyntheticLambda15()).filter(new Predicate() { // from class: com.android.internal.telephony.data.DataConfigManager$$ExternalSyntheticLambda16
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getMeteredNetworkCapabilities$4;
                lambda$getMeteredNetworkCapabilities$4 = DataConfigManager.lambda$getMeteredNetworkCapabilities$4((Integer) obj);
                return lambda$getMeteredNetworkCapabilities$4;
            }
        }).collect(Collectors.toUnmodifiableSet());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getMeteredNetworkCapabilities$4(Integer num) {
        return num.intValue() >= 0;
    }

    public boolean isTetheringProfileDisabledForRoaming() {
        return this.mCarrierConfig.getBoolean("disable_dun_apn_while_roaming_with_preset_apn_bool");
    }

    public boolean isMeteredCapability(int i, boolean z) {
        return getMeteredNetworkCapabilities(z).contains(Integer.valueOf(i));
    }

    public boolean isAnyMeteredCapability(int[] iArr, final boolean z) {
        return Arrays.stream(iArr).boxed().anyMatch(new Predicate() { // from class: com.android.internal.telephony.data.DataConfigManager$$ExternalSyntheticLambda3
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$isAnyMeteredCapability$5;
                lambda$isAnyMeteredCapability$5 = DataConfigManager.this.lambda$isAnyMeteredCapability$5(z, (Integer) obj);
                return lambda$isAnyMeteredCapability$5;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$isAnyMeteredCapability$5(boolean z, Integer num) {
        return isMeteredCapability(num.intValue(), z);
    }

    public boolean shouldUseDataActivityForRrcDetection() {
        return this.mCarrierConfig.getBoolean("lte_endc_using_user_data_for_rrc_detection_bool");
    }

    private void updateSingleDataNetworkTypeAndCapabilityExemption() {
        synchronized (this) {
            this.mSingleDataNetworkTypeList.clear();
            this.mCapabilitiesExemptFromSingleDataList.clear();
            int[] intArray = this.mCarrierConfig.getIntArray("only_single_dc_allowed_int_array");
            if (intArray != null) {
                IntStream stream = Arrays.stream(intArray);
                final List<Integer> list = this.mSingleDataNetworkTypeList;
                Objects.requireNonNull(list);
                stream.forEach(new IntConsumer() { // from class: com.android.internal.telephony.data.DataConfigManager$$ExternalSyntheticLambda17
                    @Override // java.util.function.IntConsumer
                    public final void accept(int i) {
                        list.add(Integer.valueOf(i));
                    }
                });
            }
            int[] intArray2 = this.mCarrierConfig.getIntArray("capabilities_exempt_from_single_dc_check_int_array");
            if (intArray2 != null) {
                IntStream stream2 = Arrays.stream(intArray2);
                final Set<Integer> set = this.mCapabilitiesExemptFromSingleDataList;
                Objects.requireNonNull(set);
                stream2.forEach(new IntConsumer() { // from class: com.android.internal.telephony.data.DataConfigManager$$ExternalSyntheticLambda18
                    @Override // java.util.function.IntConsumer
                    public final void accept(int i) {
                        set.add(Integer.valueOf(i));
                    }
                });
            }
        }
    }

    private void updateVopsConfig() {
        synchronized (this) {
            this.mShouldKeepNetworkUpInNonVops = this.mCarrierConfig.getBoolean("ims.keep_pdn_up_in_no_vops_bool");
        }
    }

    private void updateDataSwitchConfig() {
        synchronized (this) {
            this.mPingTestBeforeDataSwitch = this.mCarrierConfig.getBoolean("ping_test_before_data_switch_bool", true);
        }
    }

    public List<Integer> getNetworkTypesOnlySupportSingleDataNetwork() {
        return Collections.unmodifiableList(this.mSingleDataNetworkTypeList);
    }

    public Set<Integer> getCapabilitiesExemptFromSingleDataNetwork() {
        return Collections.unmodifiableSet(this.mCapabilitiesExemptFromSingleDataList);
    }

    public boolean shouldKeepNetworkUpInNonVops() {
        return this.mShouldKeepNetworkUpInNonVops;
    }

    public boolean requirePingTestBeforeDataSwitch() {
        return this.mPingTestBeforeDataSwitch;
    }

    public boolean isTempNotMeteredSupportedByCarrier() {
        return this.mCarrierConfig.getBoolean("network_temp_not_metered_supported_bool");
    }

    private void updateUnmeteredNetworkTypes() {
        synchronized (this) {
            this.mUnmeteredNetworkTypes.clear();
            String[] stringArray = this.mCarrierConfig.getStringArray("unmetered_network_types_string_array");
            if (stringArray != null) {
                this.mUnmeteredNetworkTypes.addAll(Arrays.asList(stringArray));
            }
            this.mRoamingUnmeteredNetworkTypes.clear();
            String[] stringArray2 = this.mCarrierConfig.getStringArray("roaming_unmetered_network_types_string_array");
            if (stringArray2 != null) {
                this.mRoamingUnmeteredNetworkTypes.addAll(Arrays.asList(stringArray2));
            }
        }
    }

    public boolean isNetworkTypeUnmetered(TelephonyDisplayInfo telephonyDisplayInfo, ServiceState serviceState) {
        String dataConfigNetworkType = getDataConfigNetworkType(telephonyDisplayInfo);
        if (serviceState.getDataRoaming()) {
            return this.mRoamingUnmeteredNetworkTypes.contains(dataConfigNetworkType);
        }
        return this.mUnmeteredNetworkTypes.contains(dataConfigNetworkType);
    }

    private void updateBandwidths() {
        synchronized (this) {
            this.mBandwidthMap.clear();
            String[] stringArray = this.mCarrierConfig.getStringArray("bandwidth_string_array");
            boolean z = this.mCarrierConfig.getBoolean("bandwidth_nr_nsa_use_lte_value_for_uplink_bool");
            if (stringArray != null) {
                for (String str : stringArray) {
                    String[] split = str.split(":");
                    if (split.length != 2) {
                        loge("Invalid bandwidth: " + str);
                    } else {
                        String[] split2 = split[1].split(",");
                        if (split2.length != 2) {
                            loge("Invalid bandwidth values: " + Arrays.toString(split2));
                        } else {
                            try {
                                int parseInt = Integer.parseInt(split2[0]);
                                int parseInt2 = Integer.parseInt(split2[1]);
                                if (z && split[0].startsWith("NR")) {
                                    parseInt2 = this.mBandwidthMap.get("LTE").uplinkBandwidthKbps;
                                }
                                this.mBandwidthMap.put(split[0], new DataNetwork.NetworkBandwidth(parseInt, parseInt2));
                            } catch (NumberFormatException e) {
                                loge("Exception parsing bandwidth values for network type " + split[0] + ": " + e);
                            }
                        }
                    }
                }
            }
        }
    }

    public DataNetwork.NetworkBandwidth getBandwidthForNetworkType(TelephonyDisplayInfo telephonyDisplayInfo) {
        DataNetwork.NetworkBandwidth networkBandwidth = this.mBandwidthMap.get(getDataConfigNetworkType(telephonyDisplayInfo));
        return networkBandwidth != null ? networkBandwidth : new DataNetwork.NetworkBandwidth(14, 14);
    }

    public boolean shouldResetDataThrottlingWhenTacChanges() {
        return this.mCarrierConfig.getBoolean("unthrottle_data_retry_when_tac_changes_bool");
    }

    public String getDataServicePackageName() {
        return this.mCarrierConfig.getString("carrier_data_service_wwan_package_override_string");
    }

    public int getDefaultMtu() {
        return this.mCarrierConfig.getInt("default_mtu_int");
    }

    private void updateTcpBuffers() {
        synchronized (this) {
            this.mTcpBufferSizeMap.clear();
            String[] stringArray = this.mResources.getStringArray(17236103);
            if (stringArray != null) {
                for (String str : stringArray) {
                    String[] split = str.split(":");
                    if (split.length != 2) {
                        loge("Invalid TCP buffer sizes entry: " + str);
                    } else if (split[1].split(",").length != 6) {
                        loge("Invalid TCP buffer sizes for " + split[0] + ": " + split[1]);
                    } else {
                        this.mTcpBufferSizeMap.put(split[0], split[1]);
                    }
                }
            }
        }
    }

    public EventFrequency getAnomalySetupDataCallThreshold() {
        return this.mSetupDataCallAnomalyReportThreshold;
    }

    public EventFrequency getAnomalyNetworkUnwantedThreshold() {
        return this.mNetworkUnwantedAnomalyReportThreshold;
    }

    public EventFrequency getAnomalyImsReleaseRequestThreshold() {
        return this.mImsReleaseRequestAnomalyReportThreshold;
    }

    public boolean isInvalidQnsParamAnomalyReportEnabled() {
        return this.mIsInvalidQnsParamAnomalyReportEnabled;
    }

    public int getAnomalyNetworkConnectingTimeoutMs() {
        return this.mNetworkConnectingTimeout;
    }

    public int getAnomalyNetworkDisconnectingTimeoutMs() {
        return this.mNetworkDisconnectingTimeout;
    }

    public int getNetworkHandoverTimeoutMs() {
        return this.mNetworkHandoverTimeout;
    }

    public boolean isApnConfigAnomalyReportEnabled() {
        return this.mIsApnConfigAnomalyReportEnabled;
    }

    public int getAutoDataSwitchValidationMaxRetry() {
        return this.mResources.getInteger(17694725);
    }

    public long getAutoDataSwitchAvailabilityStabilityTimeThreshold() {
        return this.mResources.getInteger(17694724);
    }

    public String getTcpConfigString(TelephonyDisplayInfo telephonyDisplayInfo) {
        String str = this.mTcpBufferSizeMap.get(getDataConfigNetworkType(telephonyDisplayInfo));
        return TextUtils.isEmpty(str) ? getDefaultTcpConfigString() : str;
    }

    public String getDefaultTcpConfigString() {
        return this.mResources.getString(17040014);
    }

    public long getImsDeregistrationDelay() {
        return this.mResources.getInteger(17694810);
    }

    public boolean shouldPersistIwlanDataNetworksWhenDataServiceRestarted() {
        return this.mResources.getBoolean(17891895);
    }

    public boolean isIwlanHandoverPolicyEnabled() {
        return this.mResources.getBoolean(17891675);
    }

    public boolean isImsDelayTearDownUntilVoiceCallEndEnabled() {
        return this.mCarrierConfig.getBoolean("delay_ims_tear_down_until_call_end_bool");
    }

    public int getBandwidthEstimateSource() {
        String string = this.mResources.getString(17039834);
        string.hashCode();
        char c = 65535;
        switch (string.hashCode()) {
            case -1217242519:
                if (string.equals("carrier_config")) {
                    c = 0;
                    break;
                }
                break;
            case 104069930:
                if (string.equals("modem")) {
                    c = 1;
                    break;
                }
                break;
            case 203019122:
                if (string.equals("bandwidth_estimator")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return 2;
            case 1:
                return 1;
            case 2:
                return 3;
            default:
                loge("Invalid bandwidth estimation source config: " + string);
                return 0;
        }
    }

    private static String getDataConfigNetworkType(TelephonyDisplayInfo telephonyDisplayInfo) {
        int networkType = telephonyDisplayInfo.getNetworkType();
        int overrideNetworkType = telephonyDisplayInfo.getOverrideNetworkType();
        if (overrideNetworkType == 1 || overrideNetworkType == 2) {
            return "LTE_CA";
        }
        if (overrideNetworkType != 3) {
            if (overrideNetworkType != 5) {
                return networkTypeToDataConfigNetworkType(networkType);
            }
            return networkType == 20 ? "NR_SA_MMWAVE" : "NR_NSA_MMWAVE";
        }
        return "NR_NSA";
    }

    private void updateHandoverRules() {
        synchronized (this) {
            this.mHandoverRuleList.clear();
            String[] stringArray = this.mCarrierConfig.getStringArray("iwlan_handover_policy_string_array");
            if (stringArray != null) {
                for (String str : stringArray) {
                    try {
                        this.mHandoverRuleList.add(new DataNetworkController.HandoverRule(str));
                    } catch (IllegalArgumentException e) {
                        loge("updateHandoverRules: " + e.getMessage());
                    }
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public static class EventFrequency {
        public final int eventNumOccurrence;
        public final long timeWindow;

        public EventFrequency(long j, int i) {
            this.timeWindow = j;
            this.eventNumOccurrence = i;
        }

        public String toString() {
            return String.format("EventFrequency=[timeWindow=%d, eventNumOccurrence=%d]", Long.valueOf(this.timeWindow), Integer.valueOf(this.eventNumOccurrence));
        }
    }

    @VisibleForTesting
    public EventFrequency parseSlidingWindowCounterThreshold(String str, long j, int i) {
        EventFrequency eventFrequency = new EventFrequency(j, i);
        if (TextUtils.isEmpty(str)) {
            return eventFrequency;
        }
        String[] split = str.split(",");
        if (split.length != 2) {
            loge("Invalid format: " + str + "Format should be in \"time window in ms,occurrences\". Using default instead.");
            return eventFrequency;
        }
        try {
            try {
                return new EventFrequency(Long.parseLong(split[0].trim()), Integer.parseInt(split[1].trim()));
            } catch (NumberFormatException e) {
                this.loge("Exception parsing SlidingWindow occurrence as integer " + split[1] + ": " + e);
                return eventFrequency;
            }
        } catch (NumberFormatException e2) {
            loge("Exception parsing SlidingWindow window span " + split[0] + ": " + e2);
            return eventFrequency;
        }
    }

    public List<DataNetworkController.HandoverRule> getHandoverRules() {
        return Collections.unmodifiableList(this.mHandoverRuleList);
    }

    public long getRetrySetupAfterDisconnectMillis() {
        return this.mCarrierConfig.getLong("carrier_data_call_apn_retry_after_disconnect_long");
    }

    public long[] getDataStallRecoveryDelayMillis() {
        return this.mCarrierConfig.getLongArray("data_stall_recovery_timers_long_array");
    }

    public boolean[] getDataStallRecoveryShouldSkipArray() {
        return this.mCarrierConfig.getBooleanArray("data_stall_recovery_should_skip_bool_array");
    }

    public String getDefaultPreferredApn() {
        return TextUtils.emptyIfNull(this.mCarrierConfig.getString("default_preferred_apn_name_string"));
    }

    public int getNrAdvancedCapablePcoId() {
        return this.mCarrierConfig.getInt("nr_advanced_capable_pco_id_int");
    }

    public List<Integer> getAllowedInitialAttachApnTypes() {
        String[] stringArray = this.mCarrierConfig.getStringArray("allowed_initial_attach_apn_types_string_array");
        if (stringArray != null) {
            return (List) Arrays.stream(stringArray).map(new Function() { // from class: com.android.internal.telephony.data.DataConfigManager$$ExternalSyntheticLambda2
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return Integer.valueOf(ApnSetting.getApnTypesBitmaskFromString((String) obj));
                }
            }).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public boolean isEnhancedIwlanHandoverCheckEnabled() {
        return this.mResources.getBoolean(17891679);
    }

    private void log(String str) {
        Rlog.d(this.mLogTag, str);
    }

    private void loge(String str) {
        Rlog.e(this.mLogTag, str);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        final AndroidUtilIndentingPrintWriter androidUtilIndentingPrintWriter = new AndroidUtilIndentingPrintWriter(printWriter, "  ");
        androidUtilIndentingPrintWriter.println(DataConfigManager.class.getSimpleName() + "-" + this.mPhone.getPhoneId() + ":");
        androidUtilIndentingPrintWriter.increaseIndent();
        StringBuilder sb = new StringBuilder();
        sb.append("isConfigCarrierSpecific=");
        sb.append(isConfigCarrierSpecific());
        androidUtilIndentingPrintWriter.println(sb.toString());
        androidUtilIndentingPrintWriter.println("Network capability priority:");
        androidUtilIndentingPrintWriter.increaseIndent();
        this.mNetworkCapabilityPriorityMap.forEach(new BiConsumer() { // from class: com.android.internal.telephony.data.DataConfigManager$$ExternalSyntheticLambda4
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                DataConfigManager.lambda$dump$6(AndroidUtilIndentingPrintWriter.this, (Integer) obj, (Integer) obj2);
            }
        });
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.println();
        androidUtilIndentingPrintWriter.println("Data setup retry rules:");
        androidUtilIndentingPrintWriter.increaseIndent();
        this.mDataSetupRetryRules.forEach(new DataConfigManager$$ExternalSyntheticLambda5(androidUtilIndentingPrintWriter));
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.println("isIwlanHandoverPolicyEnabled=" + isIwlanHandoverPolicyEnabled());
        androidUtilIndentingPrintWriter.println("Data handover retry rules:");
        androidUtilIndentingPrintWriter.increaseIndent();
        this.mDataHandoverRetryRules.forEach(new DataConfigManager$$ExternalSyntheticLambda6(androidUtilIndentingPrintWriter));
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.println("mSetupDataCallAnomalyReport=" + this.mSetupDataCallAnomalyReportThreshold);
        androidUtilIndentingPrintWriter.println("mNetworkUnwantedAnomalyReport=" + this.mNetworkUnwantedAnomalyReportThreshold);
        androidUtilIndentingPrintWriter.println("mImsReleaseRequestAnomalyReport=" + this.mImsReleaseRequestAnomalyReportThreshold);
        androidUtilIndentingPrintWriter.println("mIsInvalidQnsParamAnomalyReportEnabled=" + this.mIsInvalidQnsParamAnomalyReportEnabled);
        androidUtilIndentingPrintWriter.println("mNetworkConnectingTimeout=" + this.mNetworkConnectingTimeout);
        androidUtilIndentingPrintWriter.println("mNetworkDisconnectingTimeout=" + this.mNetworkDisconnectingTimeout);
        androidUtilIndentingPrintWriter.println("mNetworkHandoverTimeout=" + this.mNetworkHandoverTimeout);
        androidUtilIndentingPrintWriter.println("mIsApnConfigAnomalyReportEnabled=" + this.mIsApnConfigAnomalyReportEnabled);
        androidUtilIndentingPrintWriter.println("getAutoDataSwitchAvailabilityStabilityTimeThreshold=" + getAutoDataSwitchAvailabilityStabilityTimeThreshold());
        androidUtilIndentingPrintWriter.println("getAutoDataSwitchValidationMaxRetry=" + getAutoDataSwitchValidationMaxRetry());
        androidUtilIndentingPrintWriter.println("Metered APN types=" + ((String) this.mMeteredApnTypes.stream().map(new Function() { // from class: com.android.internal.telephony.data.DataConfigManager$$ExternalSyntheticLambda7
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ApnSetting.getApnTypeString(((Integer) obj).intValue());
            }
        }).collect(Collectors.joining(","))));
        androidUtilIndentingPrintWriter.println("Roaming metered APN types=" + ((String) this.mRoamingMeteredApnTypes.stream().map(new Function() { // from class: com.android.internal.telephony.data.DataConfigManager$$ExternalSyntheticLambda7
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ApnSetting.getApnTypeString(((Integer) obj).intValue());
            }
        }).collect(Collectors.joining(","))));
        androidUtilIndentingPrintWriter.println("Single data network types=" + ((String) this.mSingleDataNetworkTypeList.stream().map(new DataConfigManager$$ExternalSyntheticLambda8()).collect(Collectors.joining(","))));
        androidUtilIndentingPrintWriter.println("Capabilities exempt from single PDN=" + ((String) this.mCapabilitiesExemptFromSingleDataList.stream().map(new DataConfigManager$$ExternalSyntheticLambda9()).collect(Collectors.joining(","))));
        androidUtilIndentingPrintWriter.println("mShouldKeepNetworkUpInNoVops=" + this.mShouldKeepNetworkUpInNonVops);
        androidUtilIndentingPrintWriter.println("mPingTestBeforeDataSwitch=" + this.mPingTestBeforeDataSwitch);
        androidUtilIndentingPrintWriter.println("Unmetered network types=" + String.join(",", this.mUnmeteredNetworkTypes));
        androidUtilIndentingPrintWriter.println("Roaming unmetered network types=" + String.join(",", this.mRoamingUnmeteredNetworkTypes));
        androidUtilIndentingPrintWriter.println("Bandwidths:");
        androidUtilIndentingPrintWriter.increaseIndent();
        this.mBandwidthMap.forEach(new BiConsumer() { // from class: com.android.internal.telephony.data.DataConfigManager$$ExternalSyntheticLambda10
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                DataConfigManager.lambda$dump$7(AndroidUtilIndentingPrintWriter.this, (String) obj, (DataNetwork.NetworkBandwidth) obj2);
            }
        });
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.println("shouldUseDataActivityForRrcDetection=" + shouldUseDataActivityForRrcDetection());
        androidUtilIndentingPrintWriter.println("isTempNotMeteredSupportedByCarrier=" + isTempNotMeteredSupportedByCarrier());
        androidUtilIndentingPrintWriter.println("shouldResetDataThrottlingWhenTacChanges=" + shouldResetDataThrottlingWhenTacChanges());
        androidUtilIndentingPrintWriter.println("Data service package name=" + getDataServicePackageName());
        androidUtilIndentingPrintWriter.println("Default MTU=" + getDefaultMtu());
        androidUtilIndentingPrintWriter.println("TCP buffer sizes by RAT:");
        androidUtilIndentingPrintWriter.increaseIndent();
        this.mTcpBufferSizeMap.forEach(new BiConsumer() { // from class: com.android.internal.telephony.data.DataConfigManager$$ExternalSyntheticLambda11
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                DataConfigManager.lambda$dump$8(AndroidUtilIndentingPrintWriter.this, (String) obj, (String) obj2);
            }
        });
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.println("Default TCP buffer sizes=" + getDefaultTcpConfigString());
        androidUtilIndentingPrintWriter.println("getImsDeregistrationDelay=" + getImsDeregistrationDelay());
        androidUtilIndentingPrintWriter.println("shouldPersistIwlanDataNetworksWhenDataServiceRestarted=" + shouldPersistIwlanDataNetworksWhenDataServiceRestarted());
        androidUtilIndentingPrintWriter.println("Bandwidth estimation source=" + this.mResources.getString(17039834));
        androidUtilIndentingPrintWriter.println("isImsDelayTearDownUntilVoiceCallEndEnabled=" + isImsDelayTearDownUntilVoiceCallEndEnabled());
        androidUtilIndentingPrintWriter.println("isEnhancedIwlanHandoverCheckEnabled=" + isEnhancedIwlanHandoverCheckEnabled());
        androidUtilIndentingPrintWriter.println("isTetheringProfileDisabledForRoaming=" + isTetheringProfileDisabledForRoaming());
        androidUtilIndentingPrintWriter.decreaseIndent();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$dump$6(AndroidUtilIndentingPrintWriter androidUtilIndentingPrintWriter, Integer num, Integer num2) {
        androidUtilIndentingPrintWriter.print(DataUtils.networkCapabilityToString(num.intValue()) + ":" + num2 + " ");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$dump$7(AndroidUtilIndentingPrintWriter androidUtilIndentingPrintWriter, String str, DataNetwork.NetworkBandwidth networkBandwidth) {
        androidUtilIndentingPrintWriter.println(str + ":" + networkBandwidth);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$dump$8(AndroidUtilIndentingPrintWriter androidUtilIndentingPrintWriter, String str, String str2) {
        androidUtilIndentingPrintWriter.println(str + ":" + str2);
    }
}
