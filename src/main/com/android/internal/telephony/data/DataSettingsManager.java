package com.android.internal.telephony.data;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.internal.telephony.sysprop.TelephonyProperties;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyRegistryManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import com.android.internal.telephony.AndroidUtilIndentingPrintWriter;
import com.android.internal.telephony.GlobalSettingsHelper;
import com.android.internal.telephony.LocalLog;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneConstants;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.SettingsObserver;
import com.android.internal.telephony.SubscriptionController;
import com.android.internal.telephony.data.DataConfigManager;
import com.android.internal.telephony.data.DataSettingsManager;
import com.android.internal.telephony.metrics.DeviceTelephonyPropertiesStats;
import com.android.internal.telephony.subscription.SubscriptionInfoInternal;
import com.android.internal.telephony.subscription.SubscriptionManagerService;
import com.android.internal.telephony.util.TelephonyUtils;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class DataSettingsManager extends Handler {
    private final DataConfigManager mDataConfigManager;
    private final Map<Integer, Boolean> mDataEnabledSettings;
    private final Set<DataSettingsManagerCallback> mDataSettingsManagerCallbacks;
    private Set<Integer> mEnabledMobileDataPolicy;
    private boolean mInitialized;
    private boolean mIsDataEnabled;
    private final LocalLog mLocalLog;
    private final String mLogTag;
    private final Phone mPhone;
    private final ContentResolver mResolver;
    private final SettingsObserver mSettingsObserver;
    private int mSubId;

    private static String dataEnabledChangedReasonToString(int i) {
        return i != 0 ? i != 1 ? i != 2 ? i != 3 ? i != 4 ? "UNKNOWN" : "OVERRIDE" : "THERMAL" : "CARRIER" : "POLICY" : "USER";
    }

    /* loaded from: classes.dex */
    public static class DataSettingsManagerCallback extends DataCallback {
        public void onDataEnabledChanged(boolean z, int i, String str) {
        }

        public void onDataEnabledOverrideChanged(boolean z, int i) {
        }

        public void onDataRoamingEnabledChanged(boolean z) {
        }

        public DataSettingsManagerCallback(Executor executor) {
            super(executor);
        }
    }

    public DataSettingsManager(Phone phone, DataNetworkController dataNetworkController, Looper looper, DataSettingsManagerCallback dataSettingsManagerCallback) {
        super(looper);
        this.mLocalLog = new LocalLog(128);
        this.mEnabledMobileDataPolicy = new HashSet();
        this.mDataSettingsManagerCallbacks = new ArraySet();
        ArrayMap arrayMap = new ArrayMap();
        this.mDataEnabledSettings = arrayMap;
        this.mInitialized = false;
        this.mPhone = phone;
        this.mLogTag = "DSMGR-" + phone.getPhoneId();
        log("DataSettingsManager created.");
        this.mSubId = phone.getSubId();
        this.mResolver = phone.getContext().getContentResolver();
        registerCallback(dataSettingsManagerCallback);
        this.mDataConfigManager = dataNetworkController.getDataConfigManager();
        refreshEnabledMobileDataPolicy();
        this.mSettingsObserver = new SettingsObserver(phone.getContext(), this);
        Boolean bool = Boolean.TRUE;
        arrayMap.put(1, bool);
        arrayMap.put(2, bool);
        arrayMap.put(3, bool);
        sendEmptyMessage(11);
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        switch (message.what) {
            case 2:
                updateDataEnabledAndNotify(4);
                return;
            case 3:
            case 8:
            default:
                loge("Unknown msg.what: " + message.what);
                return;
            case 4:
                this.mSubId = ((Integer) message.obj).intValue();
                refreshEnabledMobileDataPolicy();
                updateDataEnabledAndNotify(0);
                this.mPhone.notifyUserMobileDataStateChanged(isUserDataEnabled());
                return;
            case 5:
                String str = (String) message.obj;
                boolean z = message.arg2 == 1;
                int i = message.arg1;
                if (i == 0) {
                    setUserDataEnabled(z, str);
                    return;
                } else if (i == 1) {
                    setPolicyDataEnabled(z, str);
                    return;
                } else if (i == 2) {
                    setCarrierDataEnabled(z, str);
                    return;
                } else if (i == 3) {
                    setThermalDataEnabled(z, str);
                    return;
                } else {
                    log("Cannot set data enabled for reason: " + dataEnabledChangedReasonToString(message.arg1));
                    return;
                }
            case 6:
                setDataRoamingEnabledInternal(((Boolean) message.obj).booleanValue());
                setDataRoamingFromUserAction();
                return;
            case 7:
                onSetMobileDataPolicy(message.arg1, message.arg2 == 1);
                return;
            case 9:
            case 10:
                updateDataEnabledAndNotify(-1);
                return;
            case 11:
                onInitialize();
                return;
        }
    }

    private void onInitialize() {
        this.mDataConfigManager.registerCallback(new DataConfigManager.DataConfigManagerCallback(new Executor() { // from class: com.android.internal.telephony.data.DataSettingsManager$$ExternalSyntheticLambda4
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                DataSettingsManager.this.post(runnable);
            }
        }) { // from class: com.android.internal.telephony.data.DataSettingsManager.1
            @Override // com.android.internal.telephony.data.DataConfigManager.DataConfigManagerCallback
            public void onCarrierConfigChanged() {
                if (DataSettingsManager.this.mDataConfigManager.isConfigCarrierSpecific()) {
                    DataSettingsManager.this.setDefaultDataRoamingEnabled();
                }
            }
        });
        this.mSettingsObserver.observe(Settings.Global.getUriFor("device_provisioned"), 9);
        this.mSettingsObserver.observe(Settings.Global.getUriFor("device_provisioning_mobile_data"), 10);
        this.mPhone.getCallTracker().registerForVoiceCallStarted(this, 2, null);
        this.mPhone.getCallTracker().registerForVoiceCallEnded(this, 2, null);
        if (this.mPhone.getImsPhone() != null) {
            this.mPhone.getImsPhone().getCallTracker().registerForVoiceCallStarted(this, 2, null);
            this.mPhone.getImsPhone().getCallTracker().registerForVoiceCallEnded(this, 2, null);
        }
        ((TelephonyRegistryManager) this.mPhone.getContext().getSystemService(TelephonyRegistryManager.class)).addOnSubscriptionsChangedListener(new SubscriptionManager.OnSubscriptionsChangedListener() { // from class: com.android.internal.telephony.data.DataSettingsManager.2
            @Override // android.telephony.SubscriptionManager.OnSubscriptionsChangedListener
            public void onSubscriptionsChanged() {
                if (DataSettingsManager.this.mSubId != DataSettingsManager.this.mPhone.getSubId()) {
                    DataSettingsManager dataSettingsManager = DataSettingsManager.this;
                    dataSettingsManager.log("onSubscriptionsChanged: " + DataSettingsManager.this.mSubId + " to " + DataSettingsManager.this.mPhone.getSubId());
                    DataSettingsManager dataSettingsManager2 = DataSettingsManager.this;
                    dataSettingsManager2.obtainMessage(4, Integer.valueOf(dataSettingsManager2.mPhone.getSubId())).sendToTarget();
                }
            }
        }, new Executor() { // from class: com.android.internal.telephony.data.DataSettingsManager$$ExternalSyntheticLambda4
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                DataSettingsManager.this.post(runnable);
            }
        });
        updateDataEnabledAndNotify(-1);
    }

    public void setDataEnabled(int i, boolean z, String str) {
        obtainMessage(5, i, z ? 1 : 0, str).sendToTarget();
    }

    public boolean isDataEnabledForReason(int i) {
        if (i == 0) {
            return isUserDataEnabled();
        }
        return this.mDataEnabledSettings.get(Integer.valueOf(i)).booleanValue();
    }

    private void updateDataEnabledAndNotify(int i) {
        updateDataEnabledAndNotify(i, this.mPhone.getContext().getOpPackageName());
    }

    private void updateDataEnabledAndNotify(int i, String str) {
        boolean z = this.mIsDataEnabled;
        this.mIsDataEnabled = isDataEnabled(255);
        log("mIsDataEnabled=" + this.mIsDataEnabled + ", prevDataEnabled=" + z);
        boolean z2 = this.mInitialized;
        if (z2 && z == this.mIsDataEnabled) {
            return;
        }
        if (!z2) {
            this.mInitialized = true;
        }
        notifyDataEnabledChanged(this.mIsDataEnabled, i, str);
    }

    private boolean isProvisioningDataEnabled() {
        String str = SystemProperties.get("ro.com.android.prov_mobiledata", "false");
        int i = Settings.Global.getInt(this.mResolver, "device_provisioning_mobile_data", "true".equalsIgnoreCase(str) ? 1 : 0);
        boolean z = i != 0;
        log("getDataEnabled during provisioning retVal=" + z + " - (" + str + ", " + i + ")");
        return z;
    }

    public boolean isDataEnabled() {
        return this.mIsDataEnabled;
    }

    public boolean isDataInitialized() {
        return this.mInitialized;
    }

    public boolean isDataEnabled(int i) {
        if (Settings.Global.getInt(this.mResolver, "device_provisioned", 0) == 0) {
            return isProvisioningDataEnabled();
        }
        return (isUserDataEnabled() || isDataEnabledOverriddenForApn(i)) && this.mDataEnabledSettings.get(1).booleanValue() && this.mDataEnabledSettings.get(2).booleanValue() && this.mDataEnabledSettings.get(3).booleanValue();
    }

    private boolean isStandAloneOpportunistic(int i) {
        if (this.mPhone.isSubscriptionManagerServiceEnabled()) {
            SubscriptionInfoInternal subscriptionInfoInternal = SubscriptionManagerService.getInstance().getSubscriptionInfoInternal(i);
            return subscriptionInfoInternal != null && subscriptionInfoInternal.isOpportunistic() && TextUtils.isEmpty(subscriptionInfoInternal.getGroupUuid());
        }
        SubscriptionInfo activeSubscriptionInfo = SubscriptionController.getInstance().getActiveSubscriptionInfo(i, this.mPhone.getContext().getOpPackageName(), this.mPhone.getContext().getAttributionTag());
        return activeSubscriptionInfo != null && activeSubscriptionInfo.isOpportunistic() && activeSubscriptionInfo.getGroupUuid() == null;
    }

    private void setUserDataEnabled(boolean z, String str) {
        if (!isStandAloneOpportunistic(this.mSubId) || z) {
            boolean z2 = GlobalSettingsHelper.setInt(this.mPhone.getContext(), "mobile_data", this.mSubId, z ? 1 : 0);
            log("Set user data enabled to " + z + ", changed=" + z2 + ", callingPackage=" + str);
            if (z2) {
                logl("UserDataEnabled changed to " + z);
                this.mPhone.notifyUserMobileDataStateChanged(z);
                updateDataEnabledAndNotify(0, str);
            }
        }
    }

    private boolean isUserDataEnabled() {
        if (Settings.Global.getInt(this.mResolver, "device_provisioned", 0) == 0) {
            return isProvisioningDataEnabled();
        }
        if (isStandAloneOpportunistic(this.mSubId)) {
            return true;
        }
        return GlobalSettingsHelper.getBoolean(this.mPhone.getContext(), "mobile_data", this.mSubId, TelephonyProperties.mobile_data().orElse(Boolean.TRUE).booleanValue());
    }

    private void setPolicyDataEnabled(boolean z, String str) {
        if (this.mDataEnabledSettings.get(1).booleanValue() != z) {
            logl("PolicyDataEnabled changed to " + z + ", callingPackage=" + str);
            this.mDataEnabledSettings.put(1, Boolean.valueOf(z));
            updateDataEnabledAndNotify(1, str);
        }
    }

    private void setCarrierDataEnabled(boolean z, String str) {
        if (this.mDataEnabledSettings.get(2).booleanValue() != z) {
            logl("CarrierDataEnabled changed to " + z + ", callingPackage=" + str);
            this.mDataEnabledSettings.put(2, Boolean.valueOf(z));
            updateDataEnabledAndNotify(2, str);
        }
    }

    private void setThermalDataEnabled(boolean z, String str) {
        if (this.mDataEnabledSettings.get(3).booleanValue() != z) {
            logl("ThermalDataEnabled changed to " + z + ", callingPackage=" + str);
            this.mDataEnabledSettings.put(3, Boolean.valueOf(z));
            updateDataEnabledAndNotify(3, str);
        }
    }

    public void setDataRoamingEnabled(boolean z) {
        obtainMessage(6, Boolean.valueOf(z)).sendToTarget();
    }

    private void setDataRoamingEnabledInternal(final boolean z) {
        if (GlobalSettingsHelper.setBoolean(this.mPhone.getContext(), "data_roaming", this.mSubId, z)) {
            logl("DataRoamingEnabled changed to " + z);
            this.mDataSettingsManagerCallbacks.forEach(new Consumer() { // from class: com.android.internal.telephony.data.DataSettingsManager$$ExternalSyntheticLambda3
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    DataSettingsManager.lambda$setDataRoamingEnabledInternal$1(z, (DataSettingsManager.DataSettingsManagerCallback) obj);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$setDataRoamingEnabledInternal$1(final boolean z, final DataSettingsManagerCallback dataSettingsManagerCallback) {
        dataSettingsManagerCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.DataSettingsManager$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                DataSettingsManager.DataSettingsManagerCallback.this.onDataRoamingEnabledChanged(z);
            }
        });
    }

    public boolean isDataRoamingEnabled() {
        return GlobalSettingsHelper.getBoolean(this.mPhone.getContext(), "data_roaming", this.mSubId, isDefaultDataRoamingEnabled());
    }

    public boolean isDefaultDataRoamingEnabled() {
        return "true".equalsIgnoreCase(SystemProperties.get("ro.com.android.dataroaming", "false")) || this.mPhone.getDataNetworkController().getDataConfigManager().isDataRoamingEnabledByDefault();
    }

    public void setDefaultDataRoamingEnabled() {
        if (isDataRoamingFromUserAction()) {
            return;
        }
        setDataRoamingEnabledInternal(isDefaultDataRoamingEnabled());
    }

    private boolean isDataRoamingFromUserAction() {
        String str = Phone.DATA_ROAMING_IS_USER_SETTING_KEY + this.mPhone.getSubId();
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.mPhone.getContext());
        if (!defaultSharedPreferences.contains(str)) {
            if (defaultSharedPreferences.contains(Phone.DATA_ROAMING_IS_USER_SETTING_KEY)) {
                log("Reusing previous roaming from user action value for backwards compatibility.");
                defaultSharedPreferences.edit().putBoolean(str, true).commit();
            } else {
                log("Clearing roaming from user action value for new or upgrading devices.");
                defaultSharedPreferences.edit().putBoolean(str, false).commit();
            }
        }
        boolean z = defaultSharedPreferences.getBoolean(str, true);
        log("isDataRoamingFromUserAction: key=" + str + ", isUserSetting=" + z);
        return z;
    }

    private void setDataRoamingFromUserAction() {
        String str = Phone.DATA_ROAMING_IS_USER_SETTING_KEY + this.mPhone.getSubId();
        log("setDataRoamingFromUserAction: key=" + str);
        PreferenceManager.getDefaultSharedPreferences(this.mPhone.getContext()).edit().putBoolean(str, true).commit();
    }

    private void refreshEnabledMobileDataPolicy() {
        if (this.mPhone.isSubscriptionManagerServiceEnabled()) {
            SubscriptionInfoInternal subscriptionInfoInternal = SubscriptionManagerService.getInstance().getSubscriptionInfoInternal(this.mSubId);
            if (subscriptionInfoInternal != null) {
                this.mEnabledMobileDataPolicy = getMobileDataPolicyEnabled(subscriptionInfoInternal.getEnabledMobileDataPolicies());
                return;
            }
            return;
        }
        this.mEnabledMobileDataPolicy = getMobileDataPolicyEnabled(SubscriptionController.getInstance().getEnabledMobileDataPolicies(this.mSubId));
    }

    public boolean isMobileDataPolicyEnabled(int i) {
        return this.mEnabledMobileDataPolicy.contains(Integer.valueOf(i));
    }

    public void setMobileDataPolicy(int i, boolean z) {
        obtainMessage(7, i, z ? 1 : 0).sendToTarget();
    }

    private void onSetMobileDataPolicy(int i, boolean z) {
        if (z == isMobileDataPolicyEnabled(i)) {
            return;
        }
        metricsRecordSetMobileDataPolicy(i);
        if (z) {
            this.mEnabledMobileDataPolicy.add(Integer.valueOf(i));
        } else {
            this.mEnabledMobileDataPolicy.remove(Integer.valueOf(i));
        }
        String str = (String) this.mEnabledMobileDataPolicy.stream().map(new Function() { // from class: com.android.internal.telephony.data.DataSettingsManager$$ExternalSyntheticLambda2
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return String.valueOf((Integer) obj);
            }
        }).collect(Collectors.joining(","));
        if (this.mPhone.isSubscriptionManagerServiceEnabled()) {
            SubscriptionManagerService.getInstance().setEnabledMobileDataPolicies(this.mSubId, str);
            logl(TelephonyUtils.mobileDataPolicyToString(i) + " changed to " + z);
            updateDataEnabledAndNotify(4);
            notifyDataEnabledOverrideChanged(z, i);
        } else if (SubscriptionController.getInstance().setEnabledMobileDataPolicies(this.mSubId, str)) {
            logl(TelephonyUtils.mobileDataPolicyToString(i) + " changed to " + z);
            updateDataEnabledAndNotify(4);
            notifyDataEnabledOverrideChanged(z, i);
        } else {
            loge("onSetMobileDataPolicy: failed to set " + str);
        }
    }

    private void metricsRecordSetMobileDataPolicy(int i) {
        if (i == 3) {
            DeviceTelephonyPropertiesStats.recordAutoDataSwitchFeatureToggle();
        }
    }

    public boolean isRecoveryOnBadNetworkEnabled() {
        return Settings.Global.getInt(this.mResolver, "data_stall_recovery_on_bad_network", 1) == 1;
    }

    private void notifyDataEnabledChanged(final boolean z, final int i, final String str) {
        logl("notifyDataEnabledChanged: enabled=" + z + ", reason=" + dataEnabledChangedReasonToString(i) + ", callingPackage=" + str);
        this.mDataSettingsManagerCallbacks.forEach(new Consumer() { // from class: com.android.internal.telephony.data.DataSettingsManager$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DataSettingsManager.lambda$notifyDataEnabledChanged$3(z, i, str, (DataSettingsManager.DataSettingsManagerCallback) obj);
            }
        });
        this.mPhone.notifyDataEnabled(z, i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$notifyDataEnabledChanged$3(final boolean z, final int i, final String str, final DataSettingsManagerCallback dataSettingsManagerCallback) {
        dataSettingsManagerCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.DataSettingsManager$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                DataSettingsManager.DataSettingsManagerCallback.this.onDataEnabledChanged(z, i, str);
            }
        });
    }

    private void notifyDataEnabledOverrideChanged(final boolean z, final int i) {
        logl("notifyDataEnabledOverrideChanged: enabled=" + z);
        this.mDataSettingsManagerCallbacks.forEach(new Consumer() { // from class: com.android.internal.telephony.data.DataSettingsManager$$ExternalSyntheticLambda6
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DataSettingsManager.lambda$notifyDataEnabledOverrideChanged$5(z, i, (DataSettingsManager.DataSettingsManagerCallback) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$notifyDataEnabledOverrideChanged$5(final boolean z, final int i, final DataSettingsManagerCallback dataSettingsManagerCallback) {
        dataSettingsManagerCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.DataSettingsManager$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                DataSettingsManager.DataSettingsManagerCallback.this.onDataEnabledOverrideChanged(z, i);
            }
        });
    }

    public Set<Integer> getMobileDataPolicyEnabled(String str) {
        String[] split;
        int parsePolicyFrom;
        HashSet hashSet = new HashSet();
        for (String str2 : str.trim().split("\\s*,\\s*")) {
            if (!TextUtils.isEmpty(str2) && (parsePolicyFrom = parsePolicyFrom(str2)) != -1) {
                hashSet.add(Integer.valueOf(parsePolicyFrom));
            }
        }
        return hashSet;
    }

    private int parsePolicyFrom(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException unused) {
            this.loge("parsePolicyFrom: invalid mobile data policy format: " + str);
            return -1;
        }
    }

    private boolean isDataEnabledOverriddenForApn(int i) {
        Phone phone;
        boolean z = true;
        boolean z2 = isMobileDataPolicyEnabled(2) && i == 2;
        boolean z3 = !this.mPhone.isSubscriptionManagerServiceEnabled() ? this.mPhone.getSubId() == SubscriptionController.getInstance().getDefaultDataSubId() : this.mPhone.getSubId() == SubscriptionManagerService.getInstance().getDefaultDataSubId();
        if (isMobileDataPolicyEnabled(1)) {
            z2 = z2 || (z3 && this.mPhone.getState() != PhoneConstants.State.IDLE);
        }
        if (isMobileDataPolicyEnabled(3)) {
            if (this.mPhone.isSubscriptionManagerServiceEnabled()) {
                phone = PhoneFactory.getPhone(SubscriptionManagerService.getInstance().getPhoneId(SubscriptionManagerService.getInstance().getDefaultDataSubId()));
            } else {
                phone = PhoneFactory.getPhone(SubscriptionController.getInstance().getPhoneId(SubscriptionController.getInstance().getDefaultDataSubId()));
            }
            if (phone == null) {
                loge("isDataEnabledOverriddenForApn: unexpected defaultDataPhone is null");
                return z2;
            }
            if (!z2 && (!z3 || !phone.isUserDataEnabled())) {
                z = false;
            }
            return z;
        }
        return z2;
    }

    public void registerCallback(DataSettingsManagerCallback dataSettingsManagerCallback) {
        this.mDataSettingsManagerCallbacks.add(dataSettingsManagerCallback);
    }

    public void unregisterCallback(DataSettingsManagerCallback dataSettingsManagerCallback) {
        this.mDataSettingsManagerCallbacks.remove(dataSettingsManagerCallback);
    }

    @Override // android.os.Handler
    public String toString() {
        return "[isUserDataEnabled=" + isUserDataEnabled() + ", isProvisioningDataEnabled=" + isProvisioningDataEnabled() + ", mIsDataEnabled=" + this.mIsDataEnabled + ", mDataEnabledSettings=" + this.mDataEnabledSettings + ", mEnabledMobileDataPolicy=" + ((String) this.mEnabledMobileDataPolicy.stream().map(new DataSettingsManager$$ExternalSyntheticLambda1()).collect(Collectors.joining(","))) + "]";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void log(String str) {
        Rlog.d(this.mLogTag, str);
    }

    private void loge(String str) {
        Rlog.e(this.mLogTag, str);
    }

    private void logl(String str) {
        log(str);
        this.mLocalLog.log(str);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        AndroidUtilIndentingPrintWriter androidUtilIndentingPrintWriter = new AndroidUtilIndentingPrintWriter(printWriter, "  ");
        androidUtilIndentingPrintWriter.println(DataSettingsManager.class.getSimpleName() + "-" + this.mPhone.getPhoneId() + ":");
        androidUtilIndentingPrintWriter.increaseIndent();
        StringBuilder sb = new StringBuilder();
        sb.append("mIsDataEnabled=");
        sb.append(this.mIsDataEnabled);
        androidUtilIndentingPrintWriter.println(sb.toString());
        androidUtilIndentingPrintWriter.println("isDataEnabled(internet)=" + isDataEnabled(17));
        androidUtilIndentingPrintWriter.println("isDataEnabled(mms)=" + isDataEnabled(2));
        androidUtilIndentingPrintWriter.println("isUserDataEnabled=" + isUserDataEnabled());
        androidUtilIndentingPrintWriter.println("isDataRoamingEnabled=" + isDataRoamingEnabled());
        androidUtilIndentingPrintWriter.println("isDefaultDataRoamingEnabled=" + isDefaultDataRoamingEnabled());
        androidUtilIndentingPrintWriter.println("isDataRoamingFromUserAction=" + isDataRoamingFromUserAction());
        androidUtilIndentingPrintWriter.println("device_provisioned=" + Settings.Global.getInt(this.mResolver, "device_provisioned", 0));
        androidUtilIndentingPrintWriter.println("isProvisioningDataEnabled=" + isProvisioningDataEnabled());
        androidUtilIndentingPrintWriter.println("data_stall_recovery_on_bad_network=" + Settings.Global.getInt(this.mResolver, "data_stall_recovery_on_bad_network", 1));
        androidUtilIndentingPrintWriter.println("mDataEnabledSettings=" + ((String) this.mDataEnabledSettings.entrySet().stream().map(new Function() { // from class: com.android.internal.telephony.data.DataSettingsManager$$ExternalSyntheticLambda5
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String lambda$dump$6;
                lambda$dump$6 = DataSettingsManager.lambda$dump$6((Map.Entry) obj);
                return lambda$dump$6;
            }
        }).collect(Collectors.joining(", "))));
        androidUtilIndentingPrintWriter.println("mEnabledMobileDataPolicy=" + ((String) this.mEnabledMobileDataPolicy.stream().map(new DataSettingsManager$$ExternalSyntheticLambda1()).collect(Collectors.joining(","))));
        androidUtilIndentingPrintWriter.println("Local logs:");
        androidUtilIndentingPrintWriter.increaseIndent();
        this.mLocalLog.dump(fileDescriptor, androidUtilIndentingPrintWriter, strArr);
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.decreaseIndent();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ String lambda$dump$6(Map.Entry entry) {
        return dataEnabledChangedReasonToString(((Integer) entry.getKey()).intValue()) + "=" + entry.getValue();
    }
}
