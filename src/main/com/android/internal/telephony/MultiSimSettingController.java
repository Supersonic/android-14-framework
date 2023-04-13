package com.android.internal.telephony;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelUuid;
import android.provider.Settings;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.euicc.EuiccManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.data.DataSettingsManager;
import com.android.internal.telephony.subscription.SubscriptionInfoInternal;
import com.android.internal.telephony.subscription.SubscriptionManagerService;
import com.android.internal.telephony.util.ArrayUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class MultiSimSettingController extends Handler {
    @VisibleForTesting
    public static final int EVENT_RADIO_STATE_CHANGED = 9;
    protected static MultiSimSettingController sInstance;
    private int mCallbacksCount;
    private int[] mCarrierConfigLoadedSubIds;
    protected final Context mContext;
    private final boolean mIsAskEverytimeSupportedForSms;
    protected final SubscriptionController mSubController;
    private List<Integer> mPrimarySubList = new ArrayList();
    private boolean mSubInfoInitialized = false;
    private boolean mInitialHandling = true;
    private final SubscriptionManagerService mSubscriptionManagerService = SubscriptionManagerService.getInstance();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public interface UpdateDefaultAction {
        void update(int i);
    }

    private boolean isUserVisibleChange(int i) {
        return i == 1 || i == 2 || i == 3;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class DataSettingsControllerCallback extends DataSettingsManager.DataSettingsManagerCallback {
        private final Phone mPhone;

        DataSettingsControllerCallback(Phone phone, Executor executor) {
            super(executor);
            this.mPhone = phone;
        }

        @Override // com.android.internal.telephony.data.DataSettingsManager.DataSettingsManagerCallback
        public void onDataEnabledChanged(boolean z, int i, String str) {
            if (SubscriptionManager.isValidSubscriptionId(this.mPhone.getSubId()) && i == 0 && !MultiSimSettingController.getInstance().mContext.getOpPackageName().equals(str)) {
                MultiSimSettingController.getInstance().notifyUserDataEnabled(this.mPhone.getSubId(), z);
            }
        }

        @Override // com.android.internal.telephony.data.DataSettingsManager.DataSettingsManagerCallback
        public void onDataRoamingEnabledChanged(boolean z) {
            if (SubscriptionManager.isValidSubscriptionId(this.mPhone.getSubId())) {
                MultiSimSettingController.getInstance().notifyRoamingDataEnabled(this.mPhone.getSubId(), z);
            }
        }
    }

    public static MultiSimSettingController getInstance() {
        MultiSimSettingController multiSimSettingController;
        synchronized (MultiSimSettingController.class) {
            if (sInstance == null) {
                Log.wtf("MultiSimSettingController", "getInstance null");
            }
            multiSimSettingController = sInstance;
        }
        return multiSimSettingController;
    }

    public static MultiSimSettingController init(Context context, SubscriptionController subscriptionController) {
        MultiSimSettingController multiSimSettingController;
        synchronized (MultiSimSettingController.class) {
            if (sInstance == null) {
                sInstance = new MultiSimSettingController(context, subscriptionController);
            } else {
                Log.wtf("MultiSimSettingController", "init() called multiple times!  sInstance = " + sInstance);
            }
            multiSimSettingController = sInstance;
        }
        return multiSimSettingController;
    }

    @VisibleForTesting
    public MultiSimSettingController(Context context, SubscriptionController subscriptionController) {
        this.mContext = context;
        this.mSubController = subscriptionController;
        int[] iArr = new int[((TelephonyManager) context.getSystemService("phone")).getSupportedModemCount()];
        this.mCarrierConfigLoadedSubIds = iArr;
        Arrays.fill(iArr, -1);
        PhoneConfigurationManager.registerForMultiSimConfigChange(this, 8, null);
        this.mIsAskEverytimeSupportedForSms = context.getResources().getBoolean(17891803);
        ((CarrierConfigManager) context.getSystemService(CarrierConfigManager.class)).registerCarrierConfigChangeListener(new MultiSimSettingController$$ExternalSyntheticLambda7(this), new CarrierConfigManager.CarrierConfigChangeListener() { // from class: com.android.internal.telephony.MultiSimSettingController$$ExternalSyntheticLambda8
            public final void onCarrierConfigChanged(int i, int i2, int i3, int i4) {
                MultiSimSettingController.this.lambda$new$0(i, i2, i3, i4);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i, int i2, int i3, int i4) {
        onCarrierConfigChanged(i, i2);
    }

    public void notifyUserDataEnabled(int i, boolean z) {
        if (SubscriptionManager.isValidSubscriptionId(i)) {
            obtainMessage(1, i, z ? 1 : 0).sendToTarget();
        }
    }

    public void notifyRoamingDataEnabled(int i, boolean z) {
        if (SubscriptionManager.isValidSubscriptionId(i)) {
            obtainMessage(2, i, z ? 1 : 0).sendToTarget();
        }
    }

    public void notifyAllSubscriptionLoaded() {
        obtainMessage(3).sendToTarget();
    }

    public void notifySubscriptionInfoChanged() {
        log("notifySubscriptionInfoChanged");
        obtainMessage(4).sendToTarget();
    }

    public void notifySubscriptionGroupChanged(ParcelUuid parcelUuid) {
        obtainMessage(5, parcelUuid).sendToTarget();
    }

    public void notifyDefaultDataSubChanged() {
        obtainMessage(6).sendToTarget();
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        switch (message.what) {
            case 1:
                onUserDataEnabled(message.arg1, message.arg2 != 0, true);
                return;
            case 2:
                onRoamingDataEnabled(message.arg1, message.arg2 != 0);
                return;
            case 3:
                onAllSubscriptionsLoaded();
                return;
            case 4:
                onSubscriptionsChanged();
                return;
            case 5:
                onSubscriptionGroupChanged((ParcelUuid) message.obj);
                return;
            case 6:
                onDefaultDataSettingChanged();
                return;
            case 7:
            default:
                return;
            case 8:
                onMultiSimConfigChanged(((Integer) ((AsyncResult) message.obj).result).intValue());
                return;
            case 9:
                for (Phone phone : PhoneFactory.getPhones()) {
                    if (phone.mCi.getRadioState() == 2) {
                        log("Radio unavailable. Clearing sub info initialized flag.");
                        this.mSubInfoInitialized = false;
                        return;
                    }
                }
                return;
        }
    }

    private void onUserDataEnabled(int i, boolean z, boolean z2) {
        SubscriptionInfo subscriptionInfo;
        int defaultDataSubId;
        log("[onUserDataEnabled] subId=" + i + " enable=" + z + " setDefaultData=" + z2);
        setUserDataEnabledForGroup(i, z);
        if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
            subscriptionInfo = this.mSubscriptionManagerService.getSubscriptionInfo(i);
            defaultDataSubId = this.mSubscriptionManagerService.getDefaultDataSubId();
        } else {
            subscriptionInfo = this.mSubController.getSubscriptionInfo(i);
            defaultDataSubId = this.mSubController.getDefaultDataSubId();
        }
        if (defaultDataSubId == i || subscriptionInfo == null || subscriptionInfo.isOpportunistic() || !z || !subscriptionInfo.isActive() || !z2) {
            return;
        }
        Settings.Global.putInt(this.mContext.getContentResolver(), "user_preferred_data_sub", i);
        if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
            this.mSubscriptionManagerService.setDefaultDataSubId(i);
        } else {
            this.mSubController.setDefaultDataSubId(i);
        }
    }

    private void onRoamingDataEnabled(int i, boolean z) {
        log("onRoamingDataEnabled");
        setRoamingDataEnabledForGroup(i, z);
        if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
            this.mSubscriptionManagerService.setDataRoaming(z ? 1 : 0, i);
        } else {
            this.mSubController.setDataRoaming(z ? 1 : 0, i);
        }
    }

    private void onAllSubscriptionsLoaded() {
        log("onAllSubscriptionsLoaded: mSubInfoInitialized=" + this.mSubInfoInitialized);
        if (!this.mSubInfoInitialized) {
            this.mSubInfoInitialized = true;
            for (Phone phone : PhoneFactory.getPhones()) {
                phone.mCi.registerForRadioStateChanged(this, 9, null);
            }
            reEvaluateAll();
        }
        registerDataSettingsControllerCallbackAsNeeded();
    }

    private void onSubscriptionsChanged() {
        log("onSubscriptionsChanged");
        reEvaluateAll();
    }

    public void onPhoneRemoved() {
        log("onPhoneRemoved");
        if (Looper.myLooper() != getLooper()) {
            throw new RuntimeException("This method must be called from the same looper as MultiSimSettingController.");
        }
        reEvaluateAll();
    }

    private void onCarrierConfigChanged(int i, int i2) {
        CarrierConfigManager carrierConfigManager;
        log("onCarrierConfigChanged phoneId " + i + " subId " + i2);
        if (!SubscriptionManager.isValidPhoneId(i)) {
            loge("Carrier config change with invalid phoneId " + i);
            return;
        }
        if (i2 == -1) {
            i2 = SubscriptionManager.getSubscriptionId(i);
            if (SubscriptionManager.isValidSubscriptionId(i2) && (carrierConfigManager = (CarrierConfigManager) this.mContext.getSystemService(CarrierConfigManager.class)) != null && carrierConfigManager.getConfigForSubId(i2) != null) {
                loge("onCarrierConfigChanged with invalid subId while subId " + i2 + " is active and its config is loaded");
            }
        }
        this.mCarrierConfigLoadedSubIds[i] = i2;
        reEvaluateAll();
    }

    @VisibleForTesting
    public boolean isCarrierConfigLoadedForAllSub() {
        int[] activeSubIdList;
        if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
            activeSubIdList = this.mSubscriptionManagerService.getActiveSubIdList(false);
        } else {
            activeSubIdList = this.mSubController.getActiveSubIdList(false);
        }
        int length = activeSubIdList.length;
        int i = 0;
        while (true) {
            boolean z = true;
            if (i >= length) {
                return true;
            }
            int i2 = activeSubIdList[i];
            int[] iArr = this.mCarrierConfigLoadedSubIds;
            int length2 = iArr.length;
            int i3 = 0;
            while (true) {
                if (i3 >= length2) {
                    z = false;
                    break;
                } else if (iArr[i3] == i2) {
                    break;
                } else {
                    i3++;
                }
            }
            if (!z) {
                log("Carrier config subId " + i2 + " is not loaded.");
                return false;
            }
            i++;
        }
    }

    private void onMultiSimConfigChanged(int i) {
        while (true) {
            int[] iArr = this.mCarrierConfigLoadedSubIds;
            if (i >= iArr.length) {
                break;
            }
            iArr[i] = -1;
            i++;
        }
        for (Phone phone : PhoneFactory.getPhones()) {
            phone.mCi.registerForRadioStateChanged(this, 9, null);
        }
        registerDataSettingsControllerCallbackAsNeeded();
    }

    private boolean isReadyToReevaluate() {
        boolean isCarrierConfigLoadedForAllSub = isCarrierConfigLoadedForAllSub();
        log("isReadyToReevaluate: subInfoInitialized=" + this.mSubInfoInitialized + ", carrierConfigsLoaded=" + isCarrierConfigLoadedForAllSub);
        return this.mSubInfoInitialized && isCarrierConfigLoadedForAllSub;
    }

    private void reEvaluateAll() {
        if (isReadyToReevaluate()) {
            updateDefaults();
            disableDataForNonDefaultNonOpportunisticSubscriptions();
            deactivateGroupedOpportunisticSubscriptionIfNeeded();
        }
    }

    private void onDefaultDataSettingChanged() {
        log("onDefaultDataSettingChanged");
        disableDataForNonDefaultNonOpportunisticSubscriptions();
    }

    private void onSubscriptionGroupChanged(ParcelUuid parcelUuid) {
        List<SubscriptionInfo> subscriptionsInGroup;
        boolean z;
        List<SubscriptionInfo> activeSubscriptionInfoList;
        boolean z2;
        log("onSubscriptionGroupChanged");
        if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
            subscriptionsInGroup = this.mSubscriptionManagerService.getSubscriptionsInGroup(parcelUuid, this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
            if (subscriptionsInGroup == null || subscriptionsInGroup.isEmpty()) {
                return;
            }
        } else {
            subscriptionsInGroup = this.mSubController.getSubscriptionsInGroup(parcelUuid, this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
            if (subscriptionsInGroup == null || subscriptionsInGroup.isEmpty()) {
                return;
            }
        }
        boolean z3 = false;
        int subscriptionId = subscriptionsInGroup.get(0).getSubscriptionId();
        Iterator<SubscriptionInfo> it = subscriptionsInGroup.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            SubscriptionInfo next = it.next();
            int subscriptionId2 = next.getSubscriptionId();
            if (next.isActive() && !next.isOpportunistic()) {
                subscriptionId = subscriptionId2;
                break;
            }
        }
        log("refSubId is " + subscriptionId);
        try {
            z = GlobalSettingsHelper.getBoolean(this.mContext, "mobile_data", subscriptionId);
        } catch (Settings.SettingNotFoundException unused) {
            z = GlobalSettingsHelper.getBoolean(this.mContext, "mobile_data", -1, false);
        }
        if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
            activeSubscriptionInfoList = this.mSubscriptionManagerService.getActiveSubscriptionInfoList(this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
        } else {
            activeSubscriptionInfoList = this.mSubController.getActiveSubscriptionInfoList(this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
        }
        Iterator<SubscriptionInfo> it2 = activeSubscriptionInfoList.iterator();
        while (true) {
            if (!it2.hasNext()) {
                z2 = true;
                break;
            } else if (!parcelUuid.equals(it2.next().getGroupUuid())) {
                z2 = false;
                break;
            }
        }
        onUserDataEnabled(subscriptionId, z, z2);
        try {
            z3 = GlobalSettingsHelper.getBoolean(this.mContext, "data_roaming", subscriptionId);
            onRoamingDataEnabled(subscriptionId, z3);
        } catch (Settings.SettingNotFoundException unused2) {
            onRoamingDataEnabled(subscriptionId, GlobalSettingsHelper.getBoolean(this.mContext, "data_roaming", -1, z3));
        }
        if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
            this.mSubscriptionManagerService.syncGroupedSetting(subscriptionId);
        } else {
            this.mSubController.syncGroupedSetting(subscriptionId);
        }
    }

    protected void updateDefaults() {
        List<SubscriptionInfo> activeSubscriptionInfoList;
        boolean updateDefaultValue;
        boolean updateDefaultValue2;
        boolean updateDefaultValue3;
        log("updateDefaults");
        if (isReadyToReevaluate()) {
            if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
                activeSubscriptionInfoList = this.mSubscriptionManagerService.getActiveSubscriptionInfoList(this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
                if (ArrayUtils.isEmpty(activeSubscriptionInfoList)) {
                    this.mPrimarySubList.clear();
                    log("updateDefaults: No active sub. Setting default to INVALID sub.");
                    this.mSubscriptionManagerService.setDefaultDataSubId(-1);
                    this.mSubscriptionManagerService.setDefaultVoiceSubId(-1);
                    this.mSubscriptionManagerService.setDefaultSmsSubId(-1);
                    return;
                }
            } else {
                activeSubscriptionInfoList = this.mSubController.getActiveSubscriptionInfoList(this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
                if (ArrayUtils.isEmpty(activeSubscriptionInfoList)) {
                    this.mPrimarySubList.clear();
                    log("updateDefaultValues: No active sub. Setting default to INVALID sub.");
                    this.mSubController.setDefaultDataSubId(-1);
                    this.mSubController.setDefaultVoiceSubId(-1);
                    this.mSubController.setDefaultSmsSubId(-1);
                    return;
                }
            }
            int updatePrimarySubListAndGetChangeType = updatePrimarySubListAndGetChangeType(activeSubscriptionInfoList);
            log("updateDefaultValues: change: " + updatePrimarySubListAndGetChangeType);
            if (updatePrimarySubListAndGetChangeType == 0) {
                return;
            }
            if (this.mPrimarySubList.size() == 1 && (updatePrimarySubListAndGetChangeType != 2 || ((TelephonyManager) this.mContext.getSystemService("phone")).getActiveModemCount() == 1)) {
                int intValue = this.mPrimarySubList.get(0).intValue();
                log("updateDefaultValues: to only primary sub " + intValue);
                if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
                    this.mSubscriptionManagerService.setDefaultDataSubId(intValue);
                    this.mSubscriptionManagerService.setDefaultVoiceSubId(intValue);
                    this.mSubscriptionManagerService.setDefaultSmsSubId(intValue);
                } else {
                    this.mSubController.setDefaultDataSubId(intValue);
                    this.mSubController.setDefaultVoiceSubId(intValue);
                    this.mSubController.setDefaultSmsSubId(intValue);
                }
                sendDefaultSubConfirmedNotification(intValue);
                return;
            }
            log("updateDefaultValues: records: " + this.mPrimarySubList);
            if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
                log("updateDefaultValues: Update default data subscription");
                List<Integer> list = this.mPrimarySubList;
                int defaultDataSubId = this.mSubscriptionManagerService.getDefaultDataSubId();
                final SubscriptionManagerService subscriptionManagerService = this.mSubscriptionManagerService;
                Objects.requireNonNull(subscriptionManagerService);
                updateDefaultValue = updateDefaultValue(list, defaultDataSubId, new UpdateDefaultAction() { // from class: com.android.internal.telephony.MultiSimSettingController$$ExternalSyntheticLambda1
                    @Override // com.android.internal.telephony.MultiSimSettingController.UpdateDefaultAction
                    public final void update(int i) {
                        SubscriptionManagerService.this.setDefaultDataSubId(i);
                    }
                });
                log("updateDefaultValues: Update default voice subscription");
                List<Integer> list2 = this.mPrimarySubList;
                int defaultVoiceSubId = this.mSubscriptionManagerService.getDefaultVoiceSubId();
                final SubscriptionManagerService subscriptionManagerService2 = this.mSubscriptionManagerService;
                Objects.requireNonNull(subscriptionManagerService2);
                updateDefaultValue2 = updateDefaultValue(list2, defaultVoiceSubId, new UpdateDefaultAction() { // from class: com.android.internal.telephony.MultiSimSettingController$$ExternalSyntheticLambda2
                    @Override // com.android.internal.telephony.MultiSimSettingController.UpdateDefaultAction
                    public final void update(int i) {
                        SubscriptionManagerService.this.setDefaultVoiceSubId(i);
                    }
                });
                log("updateDefaultValues: Update default sms subscription");
                List<Integer> list3 = this.mPrimarySubList;
                int defaultSmsSubId = this.mSubscriptionManagerService.getDefaultSmsSubId();
                final SubscriptionManagerService subscriptionManagerService3 = this.mSubscriptionManagerService;
                Objects.requireNonNull(subscriptionManagerService3);
                updateDefaultValue3 = updateDefaultValue(list3, defaultSmsSubId, new UpdateDefaultAction() { // from class: com.android.internal.telephony.MultiSimSettingController$$ExternalSyntheticLambda3
                    @Override // com.android.internal.telephony.MultiSimSettingController.UpdateDefaultAction
                    public final void update(int i) {
                        SubscriptionManagerService.this.setDefaultSmsSubId(i);
                    }
                }, this.mIsAskEverytimeSupportedForSms);
            } else {
                log("updateDefaultValues: Update default data subscription");
                List<Integer> list4 = this.mPrimarySubList;
                int defaultDataSubId2 = this.mSubController.getDefaultDataSubId();
                final SubscriptionController subscriptionController = this.mSubController;
                Objects.requireNonNull(subscriptionController);
                updateDefaultValue = updateDefaultValue(list4, defaultDataSubId2, new UpdateDefaultAction() { // from class: com.android.internal.telephony.MultiSimSettingController$$ExternalSyntheticLambda4
                    @Override // com.android.internal.telephony.MultiSimSettingController.UpdateDefaultAction
                    public final void update(int i) {
                        SubscriptionController.this.setDefaultDataSubId(i);
                    }
                });
                log("updateDefaultValues: Update default voice subscription");
                List<Integer> list5 = this.mPrimarySubList;
                int defaultVoiceSubId2 = this.mSubController.getDefaultVoiceSubId();
                final SubscriptionController subscriptionController2 = this.mSubController;
                Objects.requireNonNull(subscriptionController2);
                updateDefaultValue2 = updateDefaultValue(list5, defaultVoiceSubId2, new UpdateDefaultAction() { // from class: com.android.internal.telephony.MultiSimSettingController$$ExternalSyntheticLambda5
                    @Override // com.android.internal.telephony.MultiSimSettingController.UpdateDefaultAction
                    public final void update(int i) {
                        SubscriptionController.this.setDefaultVoiceSubId(i);
                    }
                });
                log("updateDefaultValues: Update default sms subscription");
                List<Integer> list6 = this.mPrimarySubList;
                int defaultSmsSubId2 = this.mSubController.getDefaultSmsSubId();
                final SubscriptionController subscriptionController3 = this.mSubController;
                Objects.requireNonNull(subscriptionController3);
                updateDefaultValue3 = updateDefaultValue(list6, defaultSmsSubId2, new UpdateDefaultAction() { // from class: com.android.internal.telephony.MultiSimSettingController$$ExternalSyntheticLambda6
                    @Override // com.android.internal.telephony.MultiSimSettingController.UpdateDefaultAction
                    public final void update(int i) {
                        SubscriptionController.this.setDefaultSmsSubId(i);
                    }
                }, this.mIsAskEverytimeSupportedForSms);
            }
            if (!this.mContext.getResources().getBoolean(17891877)) {
                sendSubChangeNotificationIfNeeded(updatePrimarySubListAndGetChangeType, updateDefaultValue, updateDefaultValue2, updateDefaultValue3);
            } else {
                updateUserPreferences(this.mPrimarySubList, updateDefaultValue, updateDefaultValue2, updateDefaultValue3);
            }
        }
    }

    private int updatePrimarySubListAndGetChangeType(List<SubscriptionInfo> list) {
        SubscriptionInfo subscriptionInfo;
        boolean z;
        List<Integer> list2 = this.mPrimarySubList;
        List<Integer> list3 = (List) list.stream().filter(new Predicate() { // from class: com.android.internal.telephony.MultiSimSettingController$$ExternalSyntheticLambda9
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$updatePrimarySubListAndGetChangeType$1;
                lambda$updatePrimarySubListAndGetChangeType$1 = MultiSimSettingController.lambda$updatePrimarySubListAndGetChangeType$1((SubscriptionInfo) obj);
                return lambda$updatePrimarySubListAndGetChangeType$1;
            }
        }).map(new Function() { // from class: com.android.internal.telephony.MultiSimSettingController$$ExternalSyntheticLambda10
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Integer lambda$updatePrimarySubListAndGetChangeType$2;
                lambda$updatePrimarySubListAndGetChangeType$2 = MultiSimSettingController.lambda$updatePrimarySubListAndGetChangeType$2((SubscriptionInfo) obj);
                return lambda$updatePrimarySubListAndGetChangeType$2;
            }
        }).collect(Collectors.toList());
        this.mPrimarySubList = list3;
        if (this.mInitialHandling) {
            this.mInitialHandling = false;
            return 6;
        } else if (list3.equals(list2)) {
            return 0;
        } else {
            if (this.mPrimarySubList.size() > list2.size()) {
                return 1;
            }
            if (this.mPrimarySubList.size() == list2.size()) {
                for (Integer num : this.mPrimarySubList) {
                    int intValue = num.intValue();
                    Iterator<Integer> it = list2.iterator();
                    while (true) {
                        if (it.hasNext()) {
                            if (areSubscriptionsInSameGroup(intValue, it.next().intValue())) {
                                z = true;
                                continue;
                                break;
                            }
                        } else {
                            z = false;
                            continue;
                            break;
                        }
                    }
                    if (!z) {
                        return 3;
                    }
                }
                return 4;
            }
            for (Integer num2 : list2) {
                int intValue2 = num2.intValue();
                if (!this.mPrimarySubList.contains(Integer.valueOf(intValue2))) {
                    if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
                        subscriptionInfo = this.mSubscriptionManagerService.getSubscriptionInfo(intValue2);
                    } else {
                        subscriptionInfo = this.mSubController.getSubscriptionInfo(intValue2);
                    }
                    if (subscriptionInfo == null || !subscriptionInfo.isActive()) {
                        for (Integer num3 : this.mPrimarySubList) {
                            if (areSubscriptionsInSameGroup(num3.intValue(), intValue2)) {
                                return 7;
                            }
                        }
                        return 2;
                    } else if (!subscriptionInfo.isOpportunistic()) {
                        loge("[updatePrimarySubListAndGetChangeType]: missing active primary subId " + intValue2);
                    }
                }
            }
            return 5;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updatePrimarySubListAndGetChangeType$1(SubscriptionInfo subscriptionInfo) {
        return !subscriptionInfo.isOpportunistic();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ Integer lambda$updatePrimarySubListAndGetChangeType$2(SubscriptionInfo subscriptionInfo) {
        return Integer.valueOf(subscriptionInfo.getSubscriptionId());
    }

    private void sendDefaultSubConfirmedNotification(int i) {
        Intent intent = new Intent();
        intent.setAction("android.telephony.action.PRIMARY_SUBSCRIPTION_LIST_CHANGED");
        intent.setClassName("com.android.settings", "com.android.settings.sim.SimSelectNotification");
        intent.putExtra("android.telephony.extra.DEFAULT_SUBSCRIPTION_SELECT_TYPE", 5);
        intent.putExtra("android.telephony.extra.SUBSCRIPTION_ID", i);
        this.mContext.sendBroadcast(intent);
    }

    private void sendSubChangeNotificationIfNeeded(int i, boolean z, boolean z2, boolean z3) {
        int simSelectDialogType = getSimSelectDialogType(i, z, z2, z3);
        log("sendSubChangeNotificationIfNeeded: simSelectDialogType=" + simSelectDialogType);
        SimCombinationWarningParams simCombinationWarningParams = getSimCombinationWarningParams(i);
        if (simSelectDialogType == 0 && simCombinationWarningParams.mWarningType == 0) {
            return;
        }
        log("[sendSubChangeNotificationIfNeeded] showing dialog type " + simSelectDialogType);
        log("[sendSubChangeNotificationIfNeeded] showing sim warning " + simCombinationWarningParams.mWarningType);
        Intent intent = new Intent();
        intent.setAction("android.telephony.action.PRIMARY_SUBSCRIPTION_LIST_CHANGED");
        intent.setClassName("com.android.settings", "com.android.settings.sim.SimSelectNotification");
        intent.addFlags(268435456);
        intent.putExtra("android.telephony.extra.DEFAULT_SUBSCRIPTION_SELECT_TYPE", simSelectDialogType);
        if (simSelectDialogType == 4) {
            intent.putExtra("android.telephony.extra.SUBSCRIPTION_ID", this.mPrimarySubList.get(0));
        }
        intent.putExtra("android.telephony.extra.SIM_COMBINATION_WARNING_TYPE", simCombinationWarningParams.mWarningType);
        if (simCombinationWarningParams.mWarningType == 1) {
            intent.putExtra("android.telephony.extra.SIM_COMBINATION_NAMES", simCombinationWarningParams.mSimNames);
        }
        this.mContext.sendBroadcast(intent);
    }

    private int getSimSelectDialogType(int i, boolean z, boolean z2, boolean z3) {
        if (Settings.Global.getInt(this.mContext.getContentResolver(), "device_provisioned", 0) == 0) {
            return 0;
        }
        if (this.mPrimarySubList.size() == 1 && i == 2 && (!z || !z3 || !z2)) {
            return 4;
        }
        if (this.mPrimarySubList.size() > 1) {
            return (isUserVisibleChange(i) || (i == 6 && !z)) ? 1 : 0;
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SimCombinationWarningParams {
        String mSimNames;
        int mWarningType;

        private SimCombinationWarningParams() {
            this.mWarningType = 0;
        }
    }

    private SimCombinationWarningParams getSimCombinationWarningParams(int i) {
        String charSequence;
        SimCombinationWarningParams simCombinationWarningParams = new SimCombinationWarningParams();
        if (this.mPrimarySubList.size() > 1 && isUserVisibleChange(i)) {
            ArrayList arrayList = new ArrayList();
            int i2 = 0;
            for (Integer num : this.mPrimarySubList) {
                int intValue = num.intValue();
                Phone phone = PhoneFactory.getPhone(SubscriptionManager.getPhoneId(intValue));
                if (phone != null && phone.isCdmaSubscriptionAppPresent()) {
                    i2++;
                    if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
                        SubscriptionInfoInternal subscriptionInfoInternal = this.mSubscriptionManagerService.getSubscriptionInfoInternal(intValue);
                        charSequence = subscriptionInfoInternal != null ? subscriptionInfoInternal.getDisplayName() : null;
                    } else {
                        charSequence = this.mSubController.getActiveSubscriptionInfo(intValue, this.mContext.getOpPackageName(), this.mContext.getAttributionTag()).getDisplayName().toString();
                    }
                    if (TextUtils.isEmpty(charSequence)) {
                        charSequence = phone.getCarrierName();
                    }
                    arrayList.add(charSequence);
                }
            }
            if (i2 > 1) {
                simCombinationWarningParams.mWarningType = 1;
                simCombinationWarningParams.mSimNames = String.join(" & ", arrayList);
            }
            return simCombinationWarningParams;
        }
        return simCombinationWarningParams;
    }

    protected void disableDataForNonDefaultNonOpportunisticSubscriptions() {
        int defaultDataSubId;
        Phone[] phones;
        boolean isOpportunistic;
        if (isReadyToReevaluate()) {
            if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
                defaultDataSubId = this.mSubscriptionManagerService.getDefaultDataSubId();
            } else {
                defaultDataSubId = this.mSubController.getDefaultDataSubId();
            }
            for (Phone phone : PhoneFactory.getPhones()) {
                if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
                    SubscriptionInfoInternal subscriptionInfoInternal = this.mSubscriptionManagerService.getSubscriptionInfoInternal(phone.getSubId());
                    isOpportunistic = subscriptionInfoInternal != null && subscriptionInfoInternal.isOpportunistic();
                } else {
                    isOpportunistic = this.mSubController.isOpportunistic(phone.getSubId());
                }
                if (phone.getSubId() != defaultDataSubId && SubscriptionManager.isValidSubscriptionId(phone.getSubId()) && !isOpportunistic && phone.isUserDataEnabled() && !areSubscriptionsInSameGroup(defaultDataSubId, phone.getSubId())) {
                    log("setting data to false on " + phone.getSubId());
                    phone.getDataSettingsManager().setDataEnabled(0, false, this.mContext.getOpPackageName());
                }
            }
        }
    }

    private boolean areSubscriptionsInSameGroup(int i, int i2) {
        if (SubscriptionManager.isUsableSubscriptionId(i) && SubscriptionManager.isUsableSubscriptionId(i2)) {
            if (i == i2) {
                return true;
            }
            if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
                SubscriptionInfoInternal subscriptionInfoInternal = this.mSubscriptionManagerService.getSubscriptionInfoInternal(i);
                SubscriptionInfoInternal subscriptionInfoInternal2 = this.mSubscriptionManagerService.getSubscriptionInfoInternal(i2);
                return (subscriptionInfoInternal == null || subscriptionInfoInternal2 == null || TextUtils.isEmpty(subscriptionInfoInternal.getGroupUuid()) || !subscriptionInfoInternal.getGroupUuid().equals(subscriptionInfoInternal2.getGroupUuid())) ? false : true;
            }
            ParcelUuid groupUuid = this.mSubController.getGroupUuid(i);
            return groupUuid != null && groupUuid.equals(this.mSubController.getGroupUuid(i2));
        }
        return false;
    }

    protected void setUserDataEnabledForGroup(int i, boolean z) {
        List<SubscriptionInfo> subscriptionsInGroup;
        Phone phone;
        log("setUserDataEnabledForGroup subId " + i + " enable " + z);
        if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
            SubscriptionInfoInternal subscriptionInfoInternal = this.mSubscriptionManagerService.getSubscriptionInfoInternal(i);
            subscriptionsInGroup = (subscriptionInfoInternal == null || subscriptionInfoInternal.getGroupUuid().isEmpty()) ? null : this.mSubscriptionManagerService.getSubscriptionsInGroup(ParcelUuid.fromString(subscriptionInfoInternal.getGroupUuid()), this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
        } else {
            SubscriptionController subscriptionController = this.mSubController;
            subscriptionsInGroup = subscriptionController.getSubscriptionsInGroup(subscriptionController.getGroupUuid(i), this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
        }
        if (subscriptionsInGroup == null) {
            return;
        }
        for (SubscriptionInfo subscriptionInfo : subscriptionsInGroup) {
            int subscriptionId = subscriptionInfo.getSubscriptionId();
            if (subscriptionInfo.isActive()) {
                if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
                    phone = PhoneFactory.getPhone(this.mSubscriptionManagerService.getPhoneId(subscriptionId));
                } else {
                    phone = PhoneFactory.getPhone(this.mSubController.getPhoneId(subscriptionId));
                }
                if (phone != null) {
                    phone.getDataSettingsManager().setDataEnabled(0, z, this.mContext.getOpPackageName());
                }
            } else {
                GlobalSettingsHelper.setBoolean(this.mContext, "mobile_data", subscriptionId, z);
            }
        }
    }

    private void setRoamingDataEnabledForGroup(int i, boolean z) {
        List<SubscriptionInfo> subscriptionsInGroup;
        if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
            SubscriptionInfoInternal subscriptionInfoInternal = this.mSubscriptionManagerService.getSubscriptionInfoInternal(i);
            if (subscriptionInfoInternal == null || subscriptionInfoInternal.getGroupUuid().isEmpty()) {
                return;
            }
            subscriptionsInGroup = SubscriptionManagerService.getInstance().getSubscriptionsInGroup(ParcelUuid.fromString(subscriptionInfoInternal.getGroupUuid()), this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
        } else {
            subscriptionsInGroup = SubscriptionController.getInstance().getSubscriptionsInGroup(this.mSubController.getGroupUuid(i), this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
        }
        if (subscriptionsInGroup == null) {
            return;
        }
        for (SubscriptionInfo subscriptionInfo : subscriptionsInGroup) {
            GlobalSettingsHelper.setBoolean(this.mContext, "data_roaming", subscriptionInfo.getSubscriptionId(), z);
        }
    }

    private boolean updateDefaultValue(List<Integer> list, int i, UpdateDefaultAction updateDefaultAction) {
        return updateDefaultValue(list, i, updateDefaultAction, true);
    }

    private boolean updateDefaultValue(List<Integer> list, int i, UpdateDefaultAction updateDefaultAction, boolean z) {
        int i2 = -1;
        if (list.size() > 0) {
            for (Integer num : list) {
                int intValue = num.intValue();
                log("[updateDefaultValue] Record.id: " + intValue);
                if (areSubscriptionsInSameGroup(intValue, i) || (!z && i == -1)) {
                    log("[updateDefaultValue] updates to subId=" + intValue);
                    i2 = intValue;
                    break;
                }
            }
        }
        if (i != i2) {
            log("[updateDefaultValue: subId] from " + i + " to " + i2);
            updateDefaultAction.update(i2);
        }
        return SubscriptionManager.isValidSubscriptionId(i2);
    }

    private void deactivateGroupedOpportunisticSubscriptionIfNeeded() {
        List<SubscriptionInfo> opportunisticSubscriptions;
        if (SubscriptionInfoUpdater.isSubInfoInitialized()) {
            if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
                opportunisticSubscriptions = (List) this.mSubscriptionManagerService.getAllSubInfoList(this.mContext.getOpPackageName(), this.mContext.getAttributionTag()).stream().filter(new Predicate() { // from class: com.android.internal.telephony.MultiSimSettingController$$ExternalSyntheticLambda0
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        return ((SubscriptionInfo) obj).isOpportunistic();
                    }
                }).collect(Collectors.toList());
            } else {
                opportunisticSubscriptions = this.mSubController.getOpportunisticSubscriptions(this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
            }
            if (ArrayUtils.isEmpty(opportunisticSubscriptions)) {
                return;
            }
            for (SubscriptionInfo subscriptionInfo : opportunisticSubscriptions) {
                if (subscriptionInfo.isGroupDisabled() && subscriptionInfo.isActive()) {
                    log("deactivateGroupedOpportunisticSubscriptionIfNeeded: Deactivating grouped opportunistic subscription " + subscriptionInfo.getSubscriptionId());
                    deactivateSubscription(subscriptionInfo);
                }
            }
        }
    }

    private void deactivateSubscription(SubscriptionInfo subscriptionInfo) {
        if (subscriptionInfo.isEmbedded()) {
            log("[deactivateSubscription] eSIM profile " + subscriptionInfo.getSubscriptionId());
            ((EuiccManager) this.mContext.getSystemService("euicc")).switchToSubscription(-1, subscriptionInfo.getPortIndex(), PendingIntent.getService(this.mContext, 0, new Intent(), 67108864));
        }
    }

    private void updateUserPreferences(List<Integer> list, boolean z, boolean z2, boolean z3) {
        if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
            if (list.isEmpty() || this.mSubscriptionManagerService.getActiveSubInfoCountMax() == 1) {
                return;
            }
            if (!isRadioAvailableOnAllSubs()) {
                log("Radio is in Invalid state, Ignore Updating User Preference!!!");
                return;
            }
            int defaultDataSubId = this.mSubscriptionManagerService.getDefaultDataSubId();
            log("updateUserPreferences:  dds = " + defaultDataSubId + " voice = " + this.mSubscriptionManagerService.getDefaultVoiceSubId() + " sms = " + this.mSubscriptionManagerService.getDefaultSmsSubId());
            int intValue = list.get(0).intValue();
            if (list.size() == 1 && !z3) {
                this.mSubscriptionManagerService.setDefaultSmsSubId(intValue);
            }
            if (list.size() == 1 && !z2) {
                this.mSubscriptionManagerService.setDefaultVoiceSubId(intValue);
            }
            int userPrefDataSubIdFromDB = getUserPrefDataSubIdFromDB();
            log("User pref subId = " + userPrefDataSubIdFromDB + " current dds " + defaultDataSubId + " next active subId " + intValue);
            if (list.contains(Integer.valueOf(userPrefDataSubIdFromDB)) && SubscriptionManager.isValidSubscriptionId(userPrefDataSubIdFromDB) && defaultDataSubId != userPrefDataSubIdFromDB) {
                this.mSubscriptionManagerService.setDefaultDataSubId(userPrefDataSubIdFromDB);
            } else if (!z) {
                this.mSubscriptionManagerService.setDefaultDataSubId(intValue);
            }
            log("updateUserPreferences: after dds = " + this.mSubscriptionManagerService.getDefaultDataSubId() + " voice = " + this.mSubscriptionManagerService.getDefaultVoiceSubId() + " sms = " + this.mSubscriptionManagerService.getDefaultSmsSubId());
        } else if (list.isEmpty() || this.mSubController.getActiveSubInfoCountMax() == 1) {
        } else {
            if (!isRadioAvailableOnAllSubs()) {
                log("Radio is in Invalid state, Ignore Updating User Preference!!!");
                return;
            }
            int defaultDataSubId2 = this.mSubController.getDefaultDataSubId();
            log("updateUserPreferences:  dds = " + defaultDataSubId2 + " voice = " + this.mSubController.getDefaultVoiceSubId() + " sms = " + this.mSubController.getDefaultSmsSubId());
            int intValue2 = list.get(0).intValue();
            if (list.size() == 1 && !z3) {
                this.mSubController.setDefaultSmsSubId(intValue2);
            }
            if (list.size() == 1 && !z2) {
                this.mSubController.setDefaultVoiceSubId(intValue2);
            }
            int userPrefDataSubIdFromDB2 = getUserPrefDataSubIdFromDB();
            log("User pref subId = " + userPrefDataSubIdFromDB2 + " current dds " + defaultDataSubId2 + " next active subId " + intValue2);
            if (list.contains(Integer.valueOf(userPrefDataSubIdFromDB2)) && SubscriptionManager.isValidSubscriptionId(userPrefDataSubIdFromDB2) && defaultDataSubId2 != userPrefDataSubIdFromDB2) {
                this.mSubController.setDefaultDataSubId(userPrefDataSubIdFromDB2);
            } else if (!z) {
                this.mSubController.setDefaultDataSubId(intValue2);
            }
            log("updateUserPreferences: after dds = " + this.mSubController.getDefaultDataSubId() + " voice = " + this.mSubController.getDefaultVoiceSubId() + " sms = " + this.mSubController.getDefaultSmsSubId());
        }
    }

    private int getUserPrefDataSubIdFromDB() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "user_preferred_data_sub", -1);
    }

    private boolean isRadioAvailableOnAllSubs() {
        Phone[] phones;
        for (Phone phone : PhoneFactory.getPhones()) {
            CommandsInterface commandsInterface = phone.mCi;
            if ((commandsInterface != null && commandsInterface.getRadioState() == 2) || phone.isShuttingDown()) {
                return false;
            }
        }
        return true;
    }

    private void registerDataSettingsControllerCallbackAsNeeded() {
        Phone[] phones = PhoneFactory.getPhones();
        for (int i = this.mCallbacksCount; i < phones.length; i++) {
            phones[i].getDataSettingsManager().registerCallback(new DataSettingsControllerCallback(phones[i], new MultiSimSettingController$$ExternalSyntheticLambda7(this)));
        }
        this.mCallbacksCount = phones.length;
    }

    private void log(String str) {
        Log.d("MultiSimSettingController", str);
    }

    private void loge(String str) {
        Log.e("MultiSimSettingController", str);
    }
}
