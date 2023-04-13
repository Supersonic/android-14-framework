package com.android.internal.telephony.subscription;

import android.annotation.RequiresPermission;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.app.compat.CompatChanges;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.ParcelUuid;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.os.TelephonyServiceManager;
import android.os.UserHandle;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.provider.Telephony;
import android.service.carrier.CarrierIdentifier;
import android.service.euicc.EuiccProfileInfo;
import android.service.euicc.EuiccService;
import android.service.euicc.GetEuiccProfileInfoListResult;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.RadioAccessFamily;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyFrameworkInitializer;
import android.telephony.TelephonyManager;
import android.telephony.TelephonyRegistryManager;
import android.telephony.UiccAccessRule;
import android.telephony.euicc.EuiccManager;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Base64;
import android.util.EventLog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.AndroidUtilIndentingPrintWriter;
import com.android.internal.telephony.CallFailCause;
import com.android.internal.telephony.CarrierResolver;
import com.android.internal.telephony.ISetOpportunisticDataCallback;
import com.android.internal.telephony.ISub;
import com.android.internal.telephony.IccCard;
import com.android.internal.telephony.IccProvider;
import com.android.internal.telephony.LocalLog;
import com.android.internal.telephony.MccTable;
import com.android.internal.telephony.MultiSimSettingController;
import com.android.internal.telephony.NetworkTypeController$$ExternalSyntheticLambda0;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.RILConstants;
import com.android.internal.telephony.TelephonyPermissions;
import com.android.internal.telephony.data.KeepaliveStatus;
import com.android.internal.telephony.data.PhoneSwitcher;
import com.android.internal.telephony.euicc.EuiccController;
import com.android.internal.telephony.subscription.SubscriptionDatabaseManager;
import com.android.internal.telephony.subscription.SubscriptionInfoInternal;
import com.android.internal.telephony.subscription.SubscriptionManagerService;
import com.android.internal.telephony.uicc.IccRecords;
import com.android.internal.telephony.uicc.IccUtils;
import com.android.internal.telephony.uicc.UiccCard;
import com.android.internal.telephony.uicc.UiccController;
import com.android.internal.telephony.uicc.UiccPort;
import com.android.internal.telephony.uicc.UiccSlot;
import com.android.internal.telephony.util.ArrayUtils;
import com.android.internal.telephony.util.NetworkStackConstants;
import com.android.internal.telephony.util.TelephonyUtils;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
/* loaded from: classes.dex */
public class SubscriptionManagerService extends ISub.Stub {
    private static final Set<String> DIRECT_ACCESS_SUBSCRIPTION_COLUMNS = Set.of((Object[]) new String[]{"volte_vt_enabled", "vt_ims_enabled", "wfc_ims_enabled", "wfc_ims_mode", "wfc_ims_roaming_mode", "wfc_ims_roaming_enabled", "enabled_mobile_data_policies", "ims_rcs_uce_enabled", "cross_sim_calling_enabled", "rcs_config", "allowed_network_types_for_reasons", "d2d_sharing_status", "voims_opt_in_status", "d2d_sharing_contacts", "nr_advanced_calling_enabled", "satellite_enabled"});
    public static final long REQUIRE_DEVICE_IDENTIFIERS_FOR_GROUP_UUID = 213902861;
    private static SubscriptionManagerService sInstance;
    private final AppOpsManager mAppOpsManager;
    private final Handler mBackgroundHandler;
    private final Context mContext;
    private final WatchedInt mDefaultDataSubId;
    private final WatchedInt mDefaultSmsSubId;
    private final WatchedInt mDefaultSubId;
    private final WatchedInt mDefaultVoiceSubId;
    private EuiccController mEuiccController;
    private final EuiccManager mEuiccManager;
    private final Handler mHandler;
    private boolean mIsWorkProfileTelephonyEnabled;
    private final int[] mSimState;
    private final SubscriptionDatabaseManager mSubscriptionDatabaseManager;
    private final SubscriptionManager mSubscriptionManager;
    private final TelephonyManager mTelephonyManager;
    private final UiccController mUiccController;
    private final LocalLog mLocalLog = new LocalLog(CallFailCause.RADIO_UPLINK_FAILURE);
    private final WatchedMap<Integer, Integer> mSlotIndexToSubId = new WatchedMap<>();
    private final Set<SubscriptionManagerServiceCallback> mSubscriptionManagerServiceCallbacks = new ArraySet();

    private void logv(String str) {
    }

    public boolean isSubscriptionManagerServiceEnabled() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class WatchedMap<K, V> extends ConcurrentHashMap<K, V> {
        private WatchedMap() {
        }

        @Override // java.util.concurrent.ConcurrentHashMap, java.util.AbstractMap, java.util.Map
        public void clear() {
            super.clear();
            SubscriptionManager.invalidateSubscriptionManagerServiceCaches();
        }

        @Override // java.util.concurrent.ConcurrentHashMap, java.util.AbstractMap, java.util.Map
        public V put(K k, V v) {
            V v2 = (V) super.put(k, v);
            if (!Objects.equals(v2, v)) {
                SubscriptionManager.invalidateSubscriptionManagerServiceCaches();
            }
            return v2;
        }

        @Override // java.util.concurrent.ConcurrentHashMap, java.util.AbstractMap, java.util.Map
        public V remove(Object obj) {
            V v = (V) super.remove(obj);
            if (v != null) {
                SubscriptionManager.invalidateSubscriptionManagerServiceCaches();
            }
            return v;
        }
    }

    /* loaded from: classes.dex */
    public static class WatchedInt {
        protected int mValue;

        public WatchedInt(int i) {
            this.mValue = i;
        }

        public int get() {
            return this.mValue;
        }

        public boolean set(int i) {
            if (this.mValue != i) {
                this.mValue = i;
                SubscriptionManager.invalidateSubscriptionManagerServiceCaches();
                return true;
            }
            return false;
        }
    }

    /* loaded from: classes.dex */
    public static class SubscriptionManagerServiceCallback {
        private final Executor mExecutor;

        public void onSubscriptionChanged(int i) {
        }

        public void onUiccApplicationsEnabled(int i) {
        }

        public SubscriptionManagerServiceCallback(Executor executor) {
            this.mExecutor = executor;
        }

        @VisibleForTesting
        public Executor getExecutor() {
            return this.mExecutor;
        }

        public void invokeFromExecutor(Runnable runnable) {
            this.mExecutor.execute(runnable);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public SubscriptionManagerService(Context context, Looper looper) {
        this.mIsWorkProfileTelephonyEnabled = false;
        logl("Created SubscriptionManagerService");
        sInstance = this;
        this.mContext = context;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        this.mTelephonyManager = telephonyManager;
        this.mSubscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        this.mEuiccManager = (EuiccManager) context.getSystemService(EuiccManager.class);
        this.mAppOpsManager = (AppOpsManager) context.getSystemService(AppOpsManager.class);
        this.mUiccController = UiccController.getInstance();
        Handler handler = new Handler(looper);
        this.mHandler = handler;
        HandlerThread handlerThread = new HandlerThread("SMSVC");
        handlerThread.start();
        this.mBackgroundHandler = new Handler(handlerThread.getLooper());
        this.mDefaultVoiceSubId = new WatchedInt(Settings.Global.getInt(context.getContentResolver(), "multi_sim_voice_call", -1)) { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService.1
            @Override // com.android.internal.telephony.subscription.SubscriptionManagerService.WatchedInt
            public boolean set(int i) {
                int i2 = this.mValue;
                if (super.set(i)) {
                    SubscriptionManagerService subscriptionManagerService = SubscriptionManagerService.this;
                    subscriptionManagerService.logl("Default voice subId changed from " + i2 + " to " + i);
                    Settings.Global.putInt(SubscriptionManagerService.this.mContext.getContentResolver(), "multi_sim_voice_call", i);
                    return true;
                }
                return false;
            }
        };
        this.mDefaultDataSubId = new WatchedInt(Settings.Global.getInt(context.getContentResolver(), "multi_sim_data_call", -1)) { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService.2
            @Override // com.android.internal.telephony.subscription.SubscriptionManagerService.WatchedInt
            public boolean set(int i) {
                int i2 = this.mValue;
                if (super.set(i)) {
                    SubscriptionManagerService subscriptionManagerService = SubscriptionManagerService.this;
                    subscriptionManagerService.logl("Default data subId changed from " + i2 + " to " + i);
                    Settings.Global.putInt(SubscriptionManagerService.this.mContext.getContentResolver(), "multi_sim_data_call", i);
                    return true;
                }
                return false;
            }
        };
        this.mDefaultSmsSubId = new WatchedInt(Settings.Global.getInt(context.getContentResolver(), "multi_sim_sms", -1)) { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService.3
            @Override // com.android.internal.telephony.subscription.SubscriptionManagerService.WatchedInt
            public boolean set(int i) {
                int i2 = this.mValue;
                if (super.set(i)) {
                    SubscriptionManagerService subscriptionManagerService = SubscriptionManagerService.this;
                    subscriptionManagerService.logl("Default SMS subId changed from " + i2 + " to " + i);
                    Settings.Global.putInt(SubscriptionManagerService.this.mContext.getContentResolver(), "multi_sim_sms", i);
                    return true;
                }
                return false;
            }
        };
        this.mDefaultSubId = new WatchedInt(-1);
        int[] iArr = new int[telephonyManager.getSupportedModemCount()];
        this.mSimState = iArr;
        Arrays.fill(iArr, 0);
        this.mIsWorkProfileTelephonyEnabled = DeviceConfig.getBoolean("telephony", "enable_work_profile_telephony", false);
        Objects.requireNonNull(handler);
        DeviceConfig.addOnPropertiesChangedListener("telephony", new NetworkTypeController$$ExternalSyntheticLambda0(handler), new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$$ExternalSyntheticLambda30
            public final void onPropertiesChanged(DeviceConfig.Properties properties) {
                SubscriptionManagerService.this.lambda$new$0(properties);
            }
        });
        HandlerThread handlerThread2 = new HandlerThread("SMSVC");
        handlerThread2.start();
        Looper looper2 = handlerThread2.getLooper();
        Objects.requireNonNull(handler);
        this.mSubscriptionDatabaseManager = new SubscriptionDatabaseManager(context, looper2, new C03224(new NetworkTypeController$$ExternalSyntheticLambda0(handler)));
        updateDefaultSubId();
        TelephonyServiceManager.ServiceRegisterer subscriptionServiceRegisterer = TelephonyFrameworkInitializer.getTelephonyServiceManager().getSubscriptionServiceRegisterer();
        if (subscriptionServiceRegisterer.get() == null) {
            subscriptionServiceRegisterer.register(this);
        }
        handler.post(new Runnable() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$$ExternalSyntheticLambda31
            @Override // java.lang.Runnable
            public final void run() {
                SubscriptionManagerService.this.lambda$new$1();
            }
        });
        SubscriptionManager.invalidateSubscriptionManagerServiceCaches();
        SubscriptionManager.invalidateSubscriptionManagerServiceEnabledCaches();
        context.registerReceiver(new BroadcastReceiver() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService.5
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                SubscriptionManagerService.this.updateEmbeddedSubscriptions();
            }
        }, new IntentFilter("android.intent.action.USER_UNLOCKED"));
        logl("Registered iSub service");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(DeviceConfig.Properties properties) {
        if (TextUtils.equals("telephony", properties.getNamespace())) {
            onDeviceConfigChanged();
        }
    }

    /* renamed from: com.android.internal.telephony.subscription.SubscriptionManagerService$4 */
    /* loaded from: classes.dex */
    class C03224 extends SubscriptionDatabaseManager.SubscriptionDatabaseManagerCallback {
        C03224(Executor executor) {
            super(executor);
        }

        @Override // com.android.internal.telephony.subscription.SubscriptionDatabaseManager.SubscriptionDatabaseManagerCallback
        public void onInitialized() {
            SubscriptionManagerService.this.log("Subscription database has been initialized.");
            for (int i = 0; i < SubscriptionManagerService.this.mTelephonyManager.getActiveModemCount(); i++) {
                SubscriptionManagerService.this.markSubscriptionsInactive(i);
            }
        }

        @Override // com.android.internal.telephony.subscription.SubscriptionDatabaseManager.SubscriptionDatabaseManagerCallback
        public void onSubscriptionChanged(final int i) {
            SubscriptionManagerService.this.mSubscriptionManagerServiceCallbacks.forEach(new Consumer() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$4$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    SubscriptionManagerService.C03224.lambda$onSubscriptionChanged$1(i, (SubscriptionManagerService.SubscriptionManagerServiceCallback) obj);
                }
            });
            MultiSimSettingController.getInstance().notifySubscriptionInfoChanged();
            TelephonyRegistryManager telephonyRegistryManager = (TelephonyRegistryManager) SubscriptionManagerService.this.mContext.getSystemService(TelephonyRegistryManager.class);
            if (telephonyRegistryManager != null) {
                telephonyRegistryManager.notifySubscriptionInfoChanged();
            }
            SubscriptionInfoInternal subscriptionInfoInternal = SubscriptionManagerService.this.mSubscriptionDatabaseManager.getSubscriptionInfoInternal(i);
            if (subscriptionInfoInternal == null || !subscriptionInfoInternal.isOpportunistic() || telephonyRegistryManager == null) {
                return;
            }
            telephonyRegistryManager.notifyOpportunisticSubscriptionInfoChanged();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$onSubscriptionChanged$1(final int i, final SubscriptionManagerServiceCallback subscriptionManagerServiceCallback) {
            subscriptionManagerServiceCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$4$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    SubscriptionManagerService.SubscriptionManagerServiceCallback.this.onSubscriptionChanged(i);
                }
            });
        }

        @Override // com.android.internal.telephony.subscription.SubscriptionDatabaseManager.SubscriptionDatabaseManagerCallback
        public void onUiccApplicationsEnabled(final int i) {
            SubscriptionManagerService subscriptionManagerService = SubscriptionManagerService.this;
            subscriptionManagerService.log("onUiccApplicationsEnabled: subId=" + i);
            SubscriptionManagerService.this.mSubscriptionManagerServiceCallbacks.forEach(new Consumer() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$4$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    SubscriptionManagerService.C03224.lambda$onUiccApplicationsEnabled$3(i, (SubscriptionManagerService.SubscriptionManagerServiceCallback) obj);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$onUiccApplicationsEnabled$3(final int i, final SubscriptionManagerServiceCallback subscriptionManagerServiceCallback) {
            subscriptionManagerServiceCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$4$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    SubscriptionManagerService.SubscriptionManagerServiceCallback.this.onUiccApplicationsEnabled(i);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1() {
        if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.telephony.euicc")) {
            this.mEuiccController = EuiccController.get();
        }
    }

    public static SubscriptionManagerService getInstance() {
        return sInstance;
    }

    private boolean canPackageManageGroup(final ParcelUuid parcelUuid, String str) {
        if (parcelUuid == null) {
            throw new IllegalArgumentException("Invalid groupUuid");
        }
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("Empty callingPackage");
        }
        List list = (List) this.mSubscriptionDatabaseManager.getAllSubscriptions().stream().filter(new Predicate() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$canPackageManageGroup$2;
                lambda$canPackageManageGroup$2 = SubscriptionManagerService.lambda$canPackageManageGroup$2(parcelUuid, (SubscriptionInfoInternal) obj);
                return lambda$canPackageManageGroup$2;
            }
        }).map(new SubscriptionManagerService$$ExternalSyntheticLambda1()).collect(Collectors.toList());
        if (ArrayUtils.isEmpty(list) || str.equals(((SubscriptionInfo) list.get(0)).getGroupOwner())) {
            return true;
        }
        return checkCarrierPrivilegeOnSubList(list.stream().mapToInt(new ToIntFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$$ExternalSyntheticLambda2
            @Override // java.util.function.ToIntFunction
            public final int applyAsInt(Object obj) {
                return ((SubscriptionInfo) obj).getSubscriptionId();
            }
        }).toArray(), str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$canPackageManageGroup$2(ParcelUuid parcelUuid, SubscriptionInfoInternal subscriptionInfoInternal) {
        return subscriptionInfoInternal.getGroupUuid().equals(parcelUuid.toString());
    }

    private boolean checkCarrierPrivilegeOnSubList(int[] iArr, String str) {
        for (int i : iArr) {
            SubscriptionInfoInternal subscriptionInfoInternal = this.mSubscriptionDatabaseManager.getSubscriptionInfoInternal(i);
            if (subscriptionInfoInternal == null) {
                loge("checkCarrierPrivilegeOnSubList: subId " + i + " does not exist.");
                return false;
            }
            if (subscriptionInfoInternal.isActive()) {
                if (!this.mTelephonyManager.hasCarrierPrivileges(i)) {
                    loge("checkCarrierPrivilegeOnSubList: Does not have carrier privilege on sub " + i);
                    return false;
                }
            } else if (!this.mSubscriptionManager.canManageSubscription(subscriptionInfoInternal.toSubscriptionInfo(), str)) {
                loge("checkCarrierPrivilegeOnSubList: cannot manage sub " + i);
                return false;
            }
        }
        return true;
    }

    public void syncGroupedSetting(final int i) {
        this.mHandler.post(new Runnable() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$$ExternalSyntheticLambda35
            @Override // java.lang.Runnable
            public final void run() {
                SubscriptionManagerService.this.lambda$syncGroupedSetting$3(i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$syncGroupedSetting$3(int i) {
        if (this.mSubscriptionDatabaseManager.getSubscriptionInfoInternal(i) == null) {
            loge("syncSettings: Can't find subscription info for sub " + i);
            return;
        }
        this.mSubscriptionDatabaseManager.syncToGroup(i);
    }

    private boolean hasPhoneNumberAccess(int i, String str, String str2, String str3) {
        try {
            return TelephonyPermissions.checkCallingOrSelfReadPhoneNumber(this.mContext, i, str, str2, str3);
        } catch (SecurityException unused) {
            return false;
        }
    }

    private boolean hasSubscriberIdentifierAccess(int i, String str, String str2, String str3, boolean z) {
        try {
            return TelephonyPermissions.checkCallingOrSelfReadSubscriberIdentifiers(this.mContext, i, str, str2, str3, z);
        } catch (SecurityException unused) {
            return false;
        }
    }

    private SubscriptionInfo conditionallyRemoveIdentifiers(SubscriptionInfo subscriptionInfo, String str, String str2, String str3) {
        int subscriptionId = subscriptionInfo.getSubscriptionId();
        boolean hasSubscriberIdentifierAccess = hasSubscriberIdentifierAccess(subscriptionId, str, str2, str3, true);
        boolean hasPhoneNumberAccess = hasPhoneNumberAccess(subscriptionId, str, str2, str3);
        if (hasSubscriberIdentifierAccess && hasPhoneNumberAccess) {
            return subscriptionInfo;
        }
        SubscriptionInfo.Builder builder = new SubscriptionInfo.Builder(subscriptionInfo);
        if (!hasSubscriberIdentifierAccess) {
            builder.setIccId((String) null);
            builder.setCardString((String) null);
            builder.setGroupUuid((String) null);
        }
        if (!hasPhoneNumberAccess) {
            builder.setNumber((String) null);
        }
        return builder.build();
    }

    private List<String> getIccIdsOfInsertedPhysicalSims() {
        ArrayList arrayList = new ArrayList();
        UiccSlot[] uiccSlots = this.mUiccController.getUiccSlots();
        if (uiccSlots == null) {
            return arrayList;
        }
        for (UiccSlot uiccSlot : uiccSlots) {
            if (uiccSlot != null && uiccSlot.getCardState() != null && uiccSlot.getCardState().isCardPresent() && !uiccSlot.isEuicc()) {
                String iccId = uiccSlot.getIccId(0);
                if (!TextUtils.isEmpty(iccId)) {
                    arrayList.add(IccUtils.stripTrailingFs(iccId));
                }
            }
        }
        return arrayList;
    }

    @VisibleForTesting
    public void setWorkProfileTelephonyEnabled(boolean z) {
        this.mIsWorkProfileTelephonyEnabled = z;
    }

    public void setCarrierId(int i, int i2) {
        try {
            this.mSubscriptionDatabaseManager.setCarrierId(i, i2);
        } catch (IllegalArgumentException unused) {
            loge("setCarrierId: invalid subId=" + i);
        }
    }

    public void setMccMnc(int i, String str) {
        try {
            this.mSubscriptionDatabaseManager.setMcc(i, str.substring(0, 3));
            this.mSubscriptionDatabaseManager.setMnc(i, str.substring(3));
        } catch (IllegalArgumentException unused) {
            loge("setMccMnc: invalid subId=" + i);
        }
    }

    public void setCountryIso(int i, String str) {
        try {
            this.mSubscriptionDatabaseManager.setCountryIso(i, str);
        } catch (IllegalArgumentException unused) {
            loge("setCountryIso: invalid subId=" + i);
        }
    }

    public void setCarrierName(int i, String str) {
        try {
            this.mSubscriptionDatabaseManager.setCarrierName(i, str);
        } catch (IllegalArgumentException unused) {
            loge("setCarrierName: invalid subId=" + i);
        }
    }

    public void setLastUsedTPMessageReference(int i, int i2) {
        try {
            this.mSubscriptionDatabaseManager.setLastUsedTPMessageReference(i, i2);
        } catch (IllegalArgumentException unused) {
            loge("setLastUsedTPMessageReference: invalid subId=" + i);
        }
    }

    public void setEnabledMobileDataPolicies(int i, String str) {
        try {
            this.mSubscriptionDatabaseManager.setEnabledMobileDataPolicies(i, str);
        } catch (IllegalArgumentException unused) {
            loge("setEnabledMobileDataPolicies: invalid subId=" + i);
        }
    }

    public void setNumberFromIms(int i, String str) {
        try {
            this.mSubscriptionDatabaseManager.setNumberFromIms(i, str);
        } catch (IllegalArgumentException unused) {
            loge("setNumberFromIms: invalid subId=" + i);
        }
    }

    public void markSubscriptionsInactive(final int i) {
        logl("markSubscriptionsInactive: slot " + i);
        this.mSlotIndexToSubId.remove(Integer.valueOf(i));
        this.mSubscriptionDatabaseManager.getAllSubscriptions().stream().filter(new Predicate() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$$ExternalSyntheticLambda16
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$markSubscriptionsInactive$4;
                lambda$markSubscriptionsInactive$4 = SubscriptionManagerService.lambda$markSubscriptionsInactive$4(i, (SubscriptionInfoInternal) obj);
                return lambda$markSubscriptionsInactive$4;
            }
        }).forEach(new Consumer() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$$ExternalSyntheticLambda17
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                SubscriptionManagerService.this.lambda$markSubscriptionsInactive$5((SubscriptionInfoInternal) obj);
            }
        });
        updateGroupDisabled();
        logl("markSubscriptionsInactive: current mapping " + slotMappingToString());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$markSubscriptionsInactive$4(int i, SubscriptionInfoInternal subscriptionInfoInternal) {
        return subscriptionInfoInternal.getSimSlotIndex() == i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$markSubscriptionsInactive$5(SubscriptionInfoInternal subscriptionInfoInternal) {
        this.mSubscriptionDatabaseManager.setSimSlotIndex(subscriptionInfoInternal.getSubscriptionId(), -1);
        this.mSubscriptionDatabaseManager.setPortIndex(subscriptionInfoInternal.getSubscriptionId(), -1);
    }

    private static int getNameSourcePriority(int i) {
        return Math.max(0, Arrays.asList(-1, 0, 4, 1, 3, 2).indexOf(Integer.valueOf(i)));
    }

    private int getColor() {
        int[] intArray = this.mContext.getResources().getIntArray(17236200);
        if (intArray.length == 0) {
            return -1;
        }
        return intArray[new Random().nextInt(intArray.length)];
    }

    private int getPortIndex(String str) {
        UiccSlot[] uiccSlots;
        int portIndexFromIccId;
        for (UiccSlot uiccSlot : this.mUiccController.getUiccSlots()) {
            if (uiccSlot != null && (portIndexFromIccId = uiccSlot.getPortIndexFromIccId(str)) != -1) {
                return portIndexFromIccId;
            }
        }
        return -1;
    }

    private int insertSubscriptionInfo(String str, int i, String str2, int i2) {
        SubscriptionInfoInternal.Builder allowedNetworkTypesForReasons = new SubscriptionInfoInternal.Builder().setIccId(str).setCardString(str).setSimSlotIndex(i).setType(i2).setIconTint(getColor()).setAllowedNetworkTypesForReasons(Phone.convertAllowedNetworkTypeMapIndexToDbName(0) + "=" + RadioAccessFamily.getRafFromNetworkType(RILConstants.PREFERRED_NETWORK_MODE));
        if (str2 != null) {
            allowedNetworkTypesForReasons.setDisplayName(str2);
        }
        return this.mSubscriptionDatabaseManager.insertSubscriptionInfo(allowedNetworkTypesForReasons.build());
    }

    public void updateEmbeddedSubscriptions(final List<Integer> list, final Runnable runnable) {
        this.mBackgroundHandler.post(new Runnable() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                SubscriptionManagerService.this.lambda$updateEmbeddedSubscriptions$8(runnable, list);
            }
        });
        log("updateEmbeddedSubscriptions: Finished embedded subscription update.");
        if (runnable != null) {
            runnable.run();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateEmbeddedSubscriptions$8(Runnable runnable, List list) {
        UiccSlot[] uiccSlots;
        EuiccManager euiccManager = this.mEuiccManager;
        if (euiccManager == null || !euiccManager.isEnabled() || this.mEuiccController == null) {
            loge("updateEmbeddedSubscriptions: eUICC not enabled");
            if (runnable != null) {
                runnable.run();
                return;
            }
            return;
        }
        final ArraySet arraySet = new ArraySet();
        log("updateEmbeddedSubscriptions: start to get euicc profiles.");
        for (UiccSlot uiccSlot : this.mUiccController.getUiccSlots()) {
            if (uiccSlot != null) {
                log("  " + uiccSlot);
            }
        }
        Iterator it = list.iterator();
        while (it.hasNext()) {
            int intValue = ((Integer) it.next()).intValue();
            GetEuiccProfileInfoListResult blockingGetEuiccProfileInfoList = this.mEuiccController.blockingGetEuiccProfileInfoList(intValue);
            logl("updateEmbeddedSubscriptions: cardId=" + intValue + ", result=" + blockingGetEuiccProfileInfoList);
            if (blockingGetEuiccProfileInfoList == null) {
                loge("Failed to get euicc profiles.");
            } else if (blockingGetEuiccProfileInfoList.getResult() != 0) {
                loge("Failed to get euicc profile info. result=" + EuiccService.resultToString(blockingGetEuiccProfileInfoList.getResult()));
            } else if (blockingGetEuiccProfileInfoList.getProfiles() == null || blockingGetEuiccProfileInfoList.getProfiles().isEmpty()) {
                loge("No profiles returned.");
            } else {
                boolean isRemovable = blockingGetEuiccProfileInfoList.getIsRemovable();
                for (EuiccProfileInfo euiccProfileInfo : blockingGetEuiccProfileInfoList.getProfiles()) {
                    SubscriptionInfoInternal subscriptionInfoInternalByIccId = this.mSubscriptionDatabaseManager.getSubscriptionInfoInternalByIccId(euiccProfileInfo.getIccid());
                    if (subscriptionInfoInternalByIccId == null) {
                        subscriptionInfoInternalByIccId = this.mSubscriptionDatabaseManager.getSubscriptionInfoInternal(insertSubscriptionInfo(euiccProfileInfo.getIccid(), -1, null, 0));
                    }
                    int displayNameSource = subscriptionInfoInternalByIccId.getDisplayNameSource();
                    int carrierId = subscriptionInfoInternalByIccId.getCarrierId();
                    SubscriptionInfoInternal.Builder builder = new SubscriptionInfoInternal.Builder(subscriptionInfoInternalByIccId);
                    builder.setEmbedded(1);
                    List uiccAccessRules = euiccProfileInfo.getUiccAccessRules();
                    if (uiccAccessRules != null && !uiccAccessRules.isEmpty()) {
                        builder.setNativeAccessRules(euiccProfileInfo.getUiccAccessRules());
                    }
                    builder.setRemovableEmbedded(isRemovable);
                    if (getNameSourcePriority(displayNameSource) <= getNameSourcePriority(3)) {
                        builder.setDisplayName(euiccProfileInfo.getNickname());
                        builder.setDisplayNameSource(3);
                    }
                    builder.setProfileClass(euiccProfileInfo.getProfileClass());
                    builder.setPortIndex(getPortIndex(euiccProfileInfo.getIccid()));
                    CarrierIdentifier carrierIdentifier = euiccProfileInfo.getCarrierIdentifier();
                    if (carrierIdentifier != null) {
                        if (carrierId == -1) {
                            builder.setCarrierId(CarrierResolver.getCarrierIdFromIdentifier(this.mContext, carrierIdentifier));
                        }
                        String mcc = carrierIdentifier.getMcc();
                        String mnc = carrierIdentifier.getMnc();
                        builder.setMcc(mcc);
                        builder.setMnc(mnc);
                    }
                    if (intValue >= 0 && this.mUiccController.getCardIdForDefaultEuicc() != -1) {
                        builder.setCardString(this.mUiccController.convertToCardString(intValue));
                    }
                    arraySet.add(Integer.valueOf(subscriptionInfoInternalByIccId.getSubscriptionId()));
                    SubscriptionInfoInternal build = builder.build();
                    log("updateEmbeddedSubscriptions: update subscription " + build);
                    this.mSubscriptionDatabaseManager.updateSubscription(build);
                }
            }
        }
        this.mSubscriptionDatabaseManager.getAllSubscriptions().stream().filter(new SubscriptionManagerService$$ExternalSyntheticLambda21()).filter(new Predicate() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$$ExternalSyntheticLambda39
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$updateEmbeddedSubscriptions$6;
                lambda$updateEmbeddedSubscriptions$6 = SubscriptionManagerService.lambda$updateEmbeddedSubscriptions$6(arraySet, (SubscriptionInfoInternal) obj);
                return lambda$updateEmbeddedSubscriptions$6;
            }
        }).forEach(new Consumer() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$$ExternalSyntheticLambda40
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                SubscriptionManagerService.this.lambda$updateEmbeddedSubscriptions$7((SubscriptionInfoInternal) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateEmbeddedSubscriptions$6(Set set, SubscriptionInfoInternal subscriptionInfoInternal) {
        return !set.contains(Integer.valueOf(subscriptionInfoInternal.getSubscriptionId()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateEmbeddedSubscriptions$7(SubscriptionInfoInternal subscriptionInfoInternal) {
        logl("updateEmbeddedSubscriptions: Mark the deleted sub " + subscriptionInfoInternal.getSubscriptionId() + " as non-embedded.");
        this.mSubscriptionDatabaseManager.setEmbedded(subscriptionInfoInternal.getSubscriptionId(), false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateEmbeddedSubscriptions() {
        UiccSlot[] uiccSlots = this.mUiccController.getUiccSlots();
        if (uiccSlots != null) {
            ArrayList arrayList = new ArrayList();
            for (UiccSlot uiccSlot : uiccSlots) {
                if (uiccSlot != null && uiccSlot.isEuicc() && uiccSlot.getUiccCard() != null) {
                    arrayList.add(Integer.valueOf(this.mUiccController.convertToPublicCardId(uiccSlot.getUiccCard().getCardId())));
                }
            }
            if (arrayList.isEmpty()) {
                return;
            }
            updateEmbeddedSubscriptions(arrayList, null);
        }
    }

    public boolean areUiccAppsEnabledOnCard(int i) {
        SubscriptionInfoInternal subscriptionInfoInternalByIccId;
        if (this.mUiccController.getUiccSlotForPhone(i) == null) {
            return false;
        }
        UiccPort uiccPort = this.mUiccController.getUiccPort(i);
        String iccId = uiccPort == null ? null : uiccPort.getIccId();
        return (iccId == null || (subscriptionInfoInternalByIccId = this.mSubscriptionDatabaseManager.getSubscriptionInfoInternalByIccId(IccUtils.stripTrailingFs(iccId))) == null || !subscriptionInfoInternalByIccId.areUiccApplicationsEnabled()) ? false : true;
    }

    private String getIccId(int i) {
        UiccPort uiccPort = this.mUiccController.getUiccPort(i);
        return uiccPort == null ? PhoneConfigurationManager.SSSS : TextUtils.emptyIfNull(IccUtils.stripTrailingFs(uiccPort.getIccId()));
    }

    private boolean areAllSubscriptionsLoaded() {
        for (int i = 0; i < this.mTelephonyManager.getActiveModemCount(); i++) {
            UiccSlot uiccSlotForPhone = this.mUiccController.getUiccSlotForPhone(i);
            if (uiccSlotForPhone == null) {
                log("areAllSubscriptionsLoaded: slot is null. phoneId=" + i);
                return false;
            } else if (!uiccSlotForPhone.isActive()) {
                log("areAllSubscriptionsLoaded: slot is inactive. phoneId=" + i);
                return false;
            } else if (uiccSlotForPhone.isEuicc() && this.mUiccController.getUiccPort(i) == null) {
                log("Wait for port corresponding to phone " + i + " to be active, portIndex is " + uiccSlotForPhone.getPortIndexFromPhoneId(i));
                return false;
            } else if (this.mSimState[i] == 6 && !PhoneFactory.getPhone(i).getIccCard().isEmptyProfile() && areUiccAppsEnabledOnCard(i)) {
                log("areAllSubscriptionsLoaded: NOT_READY is not a final state.");
                return false;
            } else if (this.mSimState[i] == 0) {
                log("areAllSubscriptionsLoaded: SIM " + i + " state is still unknown.");
                return false;
            }
        }
        return true;
    }

    private void updateSubscription(final int i) {
        int subscriptionId;
        String cardId;
        int i2 = this.mSimState[i];
        log("updateSubscription: phoneId=" + i + ", simState=" + TelephonyManager.simStateToString(i2));
        UiccSlot[] uiccSlots = this.mUiccController.getUiccSlots();
        int length = uiccSlots.length;
        for (int i3 = 0; i3 < length; i3++) {
            UiccSlot uiccSlot = uiccSlots[i3];
            if (uiccSlot != null) {
                log("  " + uiccSlot.toString());
            }
        }
        if (i2 == 1) {
            if (this.mSlotIndexToSubId.containsKey(Integer.valueOf(i))) {
                int intValue = this.mSlotIndexToSubId.get(Integer.valueOf(i)).intValue();
                log("updateSubscription: Re-enable Uicc application on sub " + intValue);
                this.mSubscriptionDatabaseManager.setUiccApplicationsEnabled(intValue, true);
                this.mSubscriptionDatabaseManager.setPortIndex(intValue, -1);
                markSubscriptionsInactive(i);
            }
        } else if (i2 == 6) {
            if (!PhoneFactory.getPhone(i).getIccCard().isEmptyProfile() && areUiccAppsEnabledOnCard(i)) {
                log("updateSubscription: SIM_STATE_NOT_READY is not a final state. Will update subscription later.");
                return;
            } else if (!areUiccAppsEnabledOnCard(i)) {
                logl("updateSubscription: UICC app disabled on slot " + i);
                markSubscriptionsInactive(i);
            }
        }
        final String iccId = getIccId(i);
        log("updateSubscription: Found iccId=" + SubscriptionInfo.givePrintableIccid(iccId) + " on phone " + i);
        SubscriptionInfoInternal orElse = this.mSubscriptionDatabaseManager.getAllSubscriptions().stream().filter(new Predicate() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$$ExternalSyntheticLambda41
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$updateSubscription$9;
                lambda$updateSubscription$9 = SubscriptionManagerService.lambda$updateSubscription$9(i, iccId, (SubscriptionInfoInternal) obj);
                return lambda$updateSubscription$9;
            }
        }).findFirst().orElse(null);
        if (orElse != null) {
            log("updateSubscription: Found previous active sub " + orElse.getSubscriptionId() + " that doesn't match current iccid on slot " + i + ".");
            markSubscriptionsInactive(i);
        }
        if (!TextUtils.isEmpty(iccId)) {
            SubscriptionInfoInternal subscriptionInfoInternalByIccId = this.mSubscriptionDatabaseManager.getSubscriptionInfoInternalByIccId(iccId);
            if (subscriptionInfoInternalByIccId == null) {
                subscriptionId = insertSubscriptionInfo(iccId, i, null, 0);
                logl("updateSubscription: Inserted a new subscription. subId=" + subscriptionId + ", phoneId=" + i);
            } else {
                subscriptionId = subscriptionInfoInternalByIccId.getSubscriptionId();
                log("updateSubscription: Found existing subscription. subId= " + subscriptionId + ", phoneId=" + i);
            }
            SubscriptionInfoInternal subscriptionInfoInternal = this.mSubscriptionDatabaseManager.getSubscriptionInfoInternal(subscriptionId);
            if (subscriptionInfoInternal != null && subscriptionInfoInternal.areUiccApplicationsEnabled()) {
                this.mSlotIndexToSubId.put(Integer.valueOf(i), Integer.valueOf(subscriptionId));
                this.mSubscriptionDatabaseManager.setSimSlotIndex(subscriptionId, i);
                logl("updateSubscription: current mapping " + slotMappingToString());
            }
            UiccCard uiccCardForPhone = this.mUiccController.getUiccCardForPhone(i);
            if (uiccCardForPhone != null && (cardId = uiccCardForPhone.getCardId()) != null) {
                this.mSubscriptionDatabaseManager.setCardString(subscriptionId, cardId);
            }
            this.mSubscriptionDatabaseManager.setPortIndex(subscriptionId, getPortIndex(iccId));
            if (i2 == 10) {
                String simOperatorNumeric = this.mTelephonyManager.getSimOperatorNumeric(subscriptionId);
                if (!TextUtils.isEmpty(simOperatorNumeric)) {
                    if (subscriptionId == getDefaultSubId()) {
                        MccTable.updateMccMncConfiguration(this.mContext, simOperatorNumeric);
                    }
                    setMccMnc(subscriptionId, simOperatorNumeric);
                } else {
                    loge("updateSubscription: mcc/mnc is empty");
                }
                String simCountryIsoForPhone = TelephonyManager.getSimCountryIsoForPhone(i);
                if (!TextUtils.isEmpty(simCountryIsoForPhone)) {
                    setCountryIso(subscriptionId, simCountryIsoForPhone);
                } else {
                    loge("updateSubscription: sim country iso is null");
                }
                String line1Number = this.mTelephonyManager.getLine1Number(subscriptionId);
                if (!TextUtils.isEmpty(line1Number)) {
                    setDisplayNumber(line1Number, subscriptionId);
                }
                String subscriberId = this.mTelephonyManager.createForSubscriptionId(subscriptionId).getSubscriberId();
                if (subscriberId != null) {
                    this.mSubscriptionDatabaseManager.setImsi(subscriptionId, subscriberId);
                }
                IccCard iccCard = PhoneFactory.getPhone(i).getIccCard();
                if (iccCard != null) {
                    IccRecords iccRecords = iccCard.getIccRecords();
                    if (iccRecords != null) {
                        String[] ehplmns = iccRecords.getEhplmns();
                        if (ehplmns != null) {
                            this.mSubscriptionDatabaseManager.setEhplmns(subscriptionId, ehplmns);
                        }
                        String[] plmnsFromHplmnActRecord = iccRecords.getPlmnsFromHplmnActRecord();
                        if (plmnsFromHplmnActRecord != null) {
                            this.mSubscriptionDatabaseManager.setHplmns(subscriptionId, plmnsFromHplmnActRecord);
                        }
                    } else {
                        loge("updateSubscription: ICC records are not available.");
                    }
                } else {
                    loge("updateSubscription: ICC card is not available.");
                }
                this.mContext.getContentResolver().call(SubscriptionManager.SIM_INFO_BACKUP_AND_RESTORE_CONTENT_URI, "restoreSimSpecificSettings", iccId, (Bundle) null);
                log("Reload the database.");
                this.mSubscriptionDatabaseManager.reloadDatabase();
            }
            log("updateSubscription: " + this.mSubscriptionDatabaseManager.getSubscriptionInfoInternal(subscriptionId));
        } else {
            log("updateSubscription: No ICCID available for phone " + i);
            this.mSlotIndexToSubId.remove(Integer.valueOf(i));
            logl("updateSubscription: current mapping " + slotMappingToString());
        }
        if (areAllSubscriptionsLoaded()) {
            log("Notify all subscriptions loaded.");
            MultiSimSettingController.getInstance().notifyAllSubscriptionLoaded();
        }
        updateGroupDisabled();
        updateDefaultSubId();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateSubscription$9(int i, String str, SubscriptionInfoInternal subscriptionInfoInternal) {
        return subscriptionInfoInternal.getSimSlotIndex() == i && !str.equals(subscriptionInfoInternal.getIccId());
    }

    @VisibleForTesting
    public int calculateUsageSetting(int i, int i2) {
        try {
            int[] intArray = this.mContext.getResources().getIntArray(17236145);
            if (intArray != null) {
                if (intArray.length >= 1) {
                    if (i < 0 || i > 2) {
                        log("calculateUsageSetting: Updating usage setting for current subscription");
                        i = 0;
                    }
                    if (i2 >= 0 && i2 <= 2) {
                        if (i2 == 0) {
                            return i2;
                        }
                        for (int i3 : intArray) {
                            if (i2 == i3) {
                                return i2;
                            }
                        }
                        return i;
                    }
                    loge("calculateUsageSetting: Invalid usage setting!" + i2);
                }
            }
            return i;
        } catch (Resources.NotFoundException unused) {
            loge("calculateUsageSetting: Failed to load usage setting resources!");
            return i;
        }
    }

    public void updateSubscriptionByCarrierConfig(final int i, final String str, final PersistableBundle persistableBundle, final Runnable runnable) {
        this.mHandler.post(new Runnable() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$$ExternalSyntheticLambda15
            @Override // java.lang.Runnable
            public final void run() {
                SubscriptionManagerService.this.lambda$updateSubscriptionByCarrierConfig$10(i, str, persistableBundle, runnable);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateSubscriptionByCarrierConfig$10(int i, String str, PersistableBundle persistableBundle, Runnable runnable) {
        updateSubscriptionByCarrierConfigInternal(i, str, persistableBundle);
        runnable.run();
    }

    private void updateSubscriptionByCarrierConfigInternal(int i, String str, PersistableBundle persistableBundle) {
        log("updateSubscriptionByCarrierConfig: phoneId=" + i + ", configPackageName=" + str);
        if (!SubscriptionManager.isValidPhoneId(i) || TextUtils.isEmpty(str) || persistableBundle == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("updateSubscriptionByCarrierConfig: Failed to update the subscription. phoneId=");
            sb.append(i);
            sb.append(" configPackageName=");
            sb.append(str);
            sb.append(" config=");
            sb.append(persistableBundle == null ? "null" : Integer.valueOf(persistableBundle.hashCode()));
            loge(sb.toString());
        } else if (!this.mSlotIndexToSubId.containsKey(Integer.valueOf(i))) {
            log("updateSubscriptionByCarrierConfig: No subscription is active for phone being updated.");
        } else {
            int intValue = this.mSlotIndexToSubId.get(Integer.valueOf(i)).intValue();
            SubscriptionInfoInternal subscriptionInfoInternal = this.mSubscriptionDatabaseManager.getSubscriptionInfoInternal(intValue);
            if (subscriptionInfoInternal == null) {
                loge("updateSubscriptionByCarrierConfig: Couldn't retrieve subscription info for current subscription. subId=" + intValue);
                return;
            }
            UiccAccessRule[] decodeRulesFromCarrierConfig = UiccAccessRule.decodeRulesFromCarrierConfig(persistableBundle.getStringArray("carrier_certificate_string_array"));
            if (decodeRulesFromCarrierConfig != null) {
                this.mSubscriptionDatabaseManager.setCarrierConfigAccessRules(intValue, decodeRulesFromCarrierConfig);
            }
            this.mSubscriptionDatabaseManager.setOpportunistic(intValue, persistableBundle.getBoolean("is_opportunistic_subscription_bool", subscriptionInfoInternal.isOpportunistic()));
            String string = persistableBundle.getString("subscription_group_uuid_string", PhoneConfigurationManager.SSSS);
            String groupUuid = subscriptionInfoInternal.getGroupUuid();
            if (!TextUtils.isEmpty(string)) {
                try {
                    ParcelUuid fromString = ParcelUuid.fromString(string);
                    if (string.equals("00000000-0000-0000-0000-000000000000")) {
                        this.mSubscriptionDatabaseManager.setGroupUuid(intValue, PhoneConfigurationManager.SSSS);
                    } else if (canPackageManageGroup(fromString, str)) {
                        this.mSubscriptionDatabaseManager.setGroupUuid(intValue, string);
                        this.mSubscriptionDatabaseManager.setGroupOwner(intValue, str);
                        log("updateSubscriptionByCarrierConfig: Group added for sub " + intValue);
                    } else {
                        loge("updateSubscriptionByCarrierConfig: configPackageName " + str + " doesn't own groupUuid " + fromString);
                    }
                    if (!string.equals(groupUuid)) {
                        MultiSimSettingController.getInstance().notifySubscriptionGroupChanged(fromString);
                    }
                } catch (IllegalArgumentException unused) {
                    loge("updateSubscriptionByCarrierConfig: Invalid Group UUID=" + string);
                }
            }
            updateGroupDisabled();
            int i2 = persistableBundle.getInt("cellular_usage_setting_int", -1);
            int calculateUsageSetting = calculateUsageSetting(subscriptionInfoInternal.getUsageSetting(), i2);
            if (calculateUsageSetting != subscriptionInfoInternal.getUsageSetting()) {
                this.mSubscriptionDatabaseManager.setUsageSetting(intValue, calculateUsageSetting);
                log("updateSubscriptionByCarrierConfig: UsageSetting changed, oldSetting=" + SubscriptionManager.usageSettingToString(subscriptionInfoInternal.getUsageSetting()) + " preferredSetting=" + SubscriptionManager.usageSettingToString(i2) + " newSetting=" + SubscriptionManager.usageSettingToString(calculateUsageSetting));
            }
        }
    }

    @RequiresPermission(anyOf = {"android.permission.READ_PHONE_STATE", "android.permission.READ_PRIVILEGED_PHONE_STATE", "carrier privileges"})
    public List<SubscriptionInfo> getAllSubInfoList(final String str, final String str2) {
        if (!TelephonyPermissions.checkReadPhoneStateOnAnyActiveSub(this.mContext, Binder.getCallingPid(), Binder.getCallingUid(), str, str2, "getAllSubInfoList")) {
            throw new SecurityException("Need READ_PHONE_STATE, READ_PRIVILEGED_PHONE_STATE, or carrier privilege");
        }
        return (List) this.mSubscriptionDatabaseManager.getAllSubscriptions().stream().filter(new Predicate() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$$ExternalSyntheticLambda28
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getAllSubInfoList$11;
                lambda$getAllSubInfoList$11 = SubscriptionManagerService.this.lambda$getAllSubInfoList$11(str, str2, (SubscriptionInfoInternal) obj);
                return lambda$getAllSubInfoList$11;
            }
        }).map(new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$$ExternalSyntheticLambda29
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                SubscriptionInfo lambda$getAllSubInfoList$12;
                lambda$getAllSubInfoList$12 = SubscriptionManagerService.this.lambda$getAllSubInfoList$12(str, str2, (SubscriptionInfoInternal) obj);
                return lambda$getAllSubInfoList$12;
            }
        }).sorted(Comparator.comparing(new SubscriptionManagerService$$ExternalSyntheticLambda5()).thenComparing(new SubscriptionManagerService$$ExternalSyntheticLambda6())).collect(Collectors.toList());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$getAllSubInfoList$11(String str, String str2, SubscriptionInfoInternal subscriptionInfoInternal) {
        return TelephonyPermissions.checkCallingOrSelfReadPhoneStateNoThrow(this.mContext, subscriptionInfoInternal.getSubscriptionId(), str, str2, "getAllSubInfoList");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ SubscriptionInfo lambda$getAllSubInfoList$12(String str, String str2, SubscriptionInfoInternal subscriptionInfoInternal) {
        return conditionallyRemoveIdentifiers(subscriptionInfoInternal.toSubscriptionInfo(), str, str2, "getAllSubInfoList");
    }

    @RequiresPermission(anyOf = {"android.permission.READ_PHONE_STATE", "android.permission.READ_PRIVILEGED_PHONE_STATE", "carrier privileges"})
    public SubscriptionInfo getActiveSubscriptionInfo(int i, String str, String str2) {
        if (!TelephonyPermissions.checkCallingOrSelfReadPhoneState(this.mContext, i, str, str2, "getActiveSubscriptionInfo")) {
            throw new SecurityException("Need READ_PHONE_STATE, READ_PRIVILEGED_PHONE_STATE, or carrier privilege");
        }
        SubscriptionInfoInternal subscriptionInfoInternal = this.mSubscriptionDatabaseManager.getSubscriptionInfoInternal(i);
        if (subscriptionInfoInternal == null || !subscriptionInfoInternal.isActive()) {
            return null;
        }
        return conditionallyRemoveIdentifiers(subscriptionInfoInternal.toSubscriptionInfo(), str, str2, "getActiveSubscriptionInfo");
    }

    @RequiresPermission("android.permission.READ_PRIVILEGED_PHONE_STATE")
    public SubscriptionInfo getActiveSubscriptionInfoForIccId(String str, String str2, String str3) {
        enforcePermissions("getActiveSubscriptionInfoForIccId", "android.permission.READ_PRIVILEGED_PHONE_STATE");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            SubscriptionInfoInternal subscriptionInfoInternalByIccId = this.mSubscriptionDatabaseManager.getSubscriptionInfoInternalByIccId(IccUtils.stripTrailingFs(str));
            return (subscriptionInfoInternalByIccId == null || !subscriptionInfoInternalByIccId.isActive()) ? null : subscriptionInfoInternalByIccId.toSubscriptionInfo();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @RequiresPermission(anyOf = {"android.permission.READ_PHONE_STATE", "android.permission.READ_PRIVILEGED_PHONE_STATE", "carrier privileges"})
    public SubscriptionInfo getActiveSubscriptionInfoForSimSlotIndex(int i, String str, String str2) {
        int intValue = this.mSlotIndexToSubId.getOrDefault(Integer.valueOf(i), -1).intValue();
        if (!TelephonyPermissions.checkCallingOrSelfReadPhoneState(this.mContext, intValue, str, str2, "getActiveSubscriptionInfoForSimSlotIndex")) {
            throw new SecurityException("Need READ_PHONE_STATE, READ_PRIVILEGED_PHONE_STATE, or carrier privilege");
        }
        if (!SubscriptionManager.isValidSlotIndex(i)) {
            throw new IllegalArgumentException("Invalid slot index " + i);
        }
        SubscriptionInfoInternal subscriptionInfoInternal = this.mSubscriptionDatabaseManager.getSubscriptionInfoInternal(intValue);
        if (subscriptionInfoInternal == null || !subscriptionInfoInternal.isActive()) {
            return null;
        }
        return conditionallyRemoveIdentifiers(subscriptionInfoInternal.toSubscriptionInfo(), str, str2, "getActiveSubscriptionInfoForSimSlotIndex");
    }

    @RequiresPermission(anyOf = {"android.permission.READ_PHONE_STATE", "android.permission.READ_PRIVILEGED_PHONE_STATE", "carrier privileges"})
    public List<SubscriptionInfo> getActiveSubscriptionInfoList(final String str, final String str2) {
        if (!TelephonyPermissions.checkReadPhoneStateOnAnyActiveSub(this.mContext, Binder.getCallingPid(), Binder.getCallingUid(), str, str2, "getAllSubInfoList")) {
            loge("getActiveSubscriptionInfoList: " + str + " does not have enough permission. Returning empty list here.");
            return Collections.emptyList();
        }
        return (List) this.mSubscriptionDatabaseManager.getAllSubscriptions().stream().filter(new SubscriptionManagerService$$ExternalSyntheticLambda3()).map(new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$$ExternalSyntheticLambda4
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                SubscriptionInfo lambda$getActiveSubscriptionInfoList$13;
                lambda$getActiveSubscriptionInfoList$13 = SubscriptionManagerService.this.lambda$getActiveSubscriptionInfoList$13(str, str2, (SubscriptionInfoInternal) obj);
                return lambda$getActiveSubscriptionInfoList$13;
            }
        }).sorted(Comparator.comparing(new SubscriptionManagerService$$ExternalSyntheticLambda5()).thenComparing(new SubscriptionManagerService$$ExternalSyntheticLambda6())).collect(Collectors.toList());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ SubscriptionInfo lambda$getActiveSubscriptionInfoList$13(String str, String str2, SubscriptionInfoInternal subscriptionInfoInternal) {
        return conditionallyRemoveIdentifiers(subscriptionInfoInternal.toSubscriptionInfo(), str, str2, "getAllSubInfoList");
    }

    @RequiresPermission(anyOf = {"android.permission.READ_PHONE_STATE", "android.permission.READ_PRIVILEGED_PHONE_STATE", "carrier privileges"})
    public int getActiveSubInfoCount(String str, String str2) {
        if (!TelephonyPermissions.checkReadPhoneStateOnAnyActiveSub(this.mContext, Binder.getCallingPid(), Binder.getCallingUid(), str, str2, "getAllSubInfoList")) {
            throw new SecurityException("Need READ_PHONE_STATE, READ_PRIVILEGED_PHONE_STATE, or carrier privilege");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return getActiveSubIdList(false).length;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public int getActiveSubInfoCountMax() {
        return this.mTelephonyManager.getActiveModemCount();
    }

    public List<SubscriptionInfo> getAvailableSubscriptionInfoList(String str, String str2) {
        enforcePermissions("getAvailableSubscriptionInfoList", "android.permission.READ_PRIVILEGED_PHONE_STATE");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            final List<String> iccIdsOfInsertedPhysicalSims = getIccIdsOfInsertedPhysicalSims();
            return (List) this.mSubscriptionDatabaseManager.getAllSubscriptions().stream().filter(new Predicate() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$$ExternalSyntheticLambda32
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$getAvailableSubscriptionInfoList$14;
                    lambda$getAvailableSubscriptionInfoList$14 = SubscriptionManagerService.this.lambda$getAvailableSubscriptionInfoList$14(iccIdsOfInsertedPhysicalSims, (SubscriptionInfoInternal) obj);
                    return lambda$getAvailableSubscriptionInfoList$14;
                }
            }).map(new SubscriptionManagerService$$ExternalSyntheticLambda1()).sorted(Comparator.comparing(new SubscriptionManagerService$$ExternalSyntheticLambda5()).thenComparing(new SubscriptionManagerService$$ExternalSyntheticLambda6())).collect(Collectors.toList());
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$getAvailableSubscriptionInfoList$14(List list, SubscriptionInfoInternal subscriptionInfoInternal) {
        EuiccManager euiccManager;
        return subscriptionInfoInternal.isActive() || list.contains(subscriptionInfoInternal.getIccId()) || ((euiccManager = this.mEuiccManager) != null && euiccManager.isEnabled() && subscriptionInfoInternal.isEmbedded());
    }

    public List<SubscriptionInfo> getAccessibleSubscriptionInfoList(final String str) {
        if (this.mEuiccManager.isEnabled()) {
            this.mAppOpsManager.checkPackage(Binder.getCallingUid(), str);
            return (List) this.mSubscriptionDatabaseManager.getAllSubscriptions().stream().map(new SubscriptionManagerService$$ExternalSyntheticLambda1()).filter(new Predicate() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$$ExternalSyntheticLambda14
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$getAccessibleSubscriptionInfoList$15;
                    lambda$getAccessibleSubscriptionInfoList$15 = SubscriptionManagerService.this.lambda$getAccessibleSubscriptionInfoList$15(str, (SubscriptionInfo) obj);
                    return lambda$getAccessibleSubscriptionInfoList$15;
                }
            }).sorted(Comparator.comparing(new SubscriptionManagerService$$ExternalSyntheticLambda5()).thenComparing(new SubscriptionManagerService$$ExternalSyntheticLambda6())).collect(Collectors.toList());
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$getAccessibleSubscriptionInfoList$15(String str, SubscriptionInfo subscriptionInfo) {
        return this.mSubscriptionManager.canManageSubscription(subscriptionInfo, str);
    }

    public void requestEmbeddedSubscriptionInfoListRefresh(int i) {
        updateEmbeddedSubscriptions(List.of(Integer.valueOf(i)), null);
    }

    @RequiresPermission("android.permission.MODIFY_PHONE_STATE")
    public int addSubInfo(String str, String str2, int i, int i2) {
        log("addSubInfo: iccId=" + SubscriptionInfo.givePrintableIccid(str) + ", slotIndex=" + i + ", displayName=" + str2 + ", type=" + SubscriptionManager.subscriptionTypeToString(i2));
        enforcePermissions("addSubInfo", "android.permission.MODIFY_PHONE_STATE");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            if (TextUtils.isEmpty(str)) {
                loge("addSubInfo: null or empty iccId");
                return -1;
            }
            String stripTrailingFs = IccUtils.stripTrailingFs(str);
            if (this.mSubscriptionDatabaseManager.getSubscriptionInfoInternalByIccId(stripTrailingFs) != null) {
                loge("Subscription record already existed.");
                return -1;
            } else if (this.mSlotIndexToSubId.containsKey(Integer.valueOf(i))) {
                loge("Already a subscription on slot " + i);
                return -1;
            } else {
                int insertSubscriptionInfo = insertSubscriptionInfo(stripTrailingFs, i, str2, i2);
                updateGroupDisabled();
                this.mSlotIndexToSubId.put(Integer.valueOf(i), Integer.valueOf(insertSubscriptionInfo));
                logl("addSubInfo: current mapping " + slotMappingToString());
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return 0;
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @RequiresPermission("android.permission.MODIFY_PHONE_STATE")
    public int removeSubInfo(String str, int i) {
        enforcePermissions("removeSubInfo", "android.permission.MODIFY_PHONE_STATE");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            SubscriptionInfoInternal subscriptionInfoInternalByIccId = this.mSubscriptionDatabaseManager.getSubscriptionInfoInternalByIccId(str);
            if (subscriptionInfoInternalByIccId == null) {
                loge("Cannot find subscription with uniqueId " + str);
                return -1;
            } else if (subscriptionInfoInternalByIccId.getSubscriptionType() != i) {
                loge("The subscription type does not match.");
                return -1;
            } else {
                this.mSubscriptionDatabaseManager.removeSubscriptionInfo(subscriptionInfoInternalByIccId.getSubscriptionId());
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return 0;
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @RequiresPermission("android.permission.MODIFY_PHONE_STATE")
    public int setIconTint(int i, int i2) {
        enforcePermissions("setIconTint", "android.permission.MODIFY_PHONE_STATE");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            if (!SubscriptionManager.isValidSubscriptionId(i)) {
                throw new IllegalArgumentException("Invalid sub id passed as parameter");
            }
            this.mSubscriptionDatabaseManager.setIconTint(i, i2);
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return 1;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    @RequiresPermission("android.permission.MODIFY_PHONE_STATE")
    public int setDisplayNameUsingSrc(String str, int i, int i2) {
        enforcePermissions("setDisplayNameUsingSrc", "android.permission.MODIFY_PHONE_STATE");
        String callingPackage = getCallingPackage();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            Objects.requireNonNull(str, "setDisplayNameUsingSrc");
            if (i2 < 0 || i2 > 4) {
                throw new IllegalArgumentException("illegal name source " + i2);
            }
            SubscriptionInfoInternal subscriptionInfoInternal = this.mSubscriptionDatabaseManager.getSubscriptionInfoInternal(i);
            if (subscriptionInfoInternal == null) {
                throw new IllegalArgumentException("Cannot find subscription info with sub id " + i);
            }
            if (getNameSourcePriority(subscriptionInfoInternal.getDisplayNameSource()) <= getNameSourcePriority(i2) && (getNameSourcePriority(subscriptionInfoInternal.getDisplayNameSource()) != getNameSourcePriority(i2) || !TextUtils.equals(str, subscriptionInfoInternal.getDisplayName()))) {
                if (TextUtils.isEmpty(str) || str.trim().length() == 0) {
                    str = this.mTelephonyManager.getSimOperatorName(i);
                    if (TextUtils.isEmpty(str)) {
                        if (i2 == 2 && SubscriptionManager.isValidSlotIndex(getSlotIndex(i))) {
                            str = Resources.getSystem().getString(17040113, Integer.valueOf(getSlotIndex(i) + 1));
                        } else {
                            str = this.mContext.getString(17039374);
                        }
                    }
                }
                logl("setDisplayNameUsingSrc: subId=" + i + ", name=" + str + ", nameSource=" + SubscriptionManager.displayNameSourceToString(i2) + ", calling package=" + callingPackage);
                this.mSubscriptionDatabaseManager.setDisplayName(i, str);
                this.mSubscriptionDatabaseManager.setDisplayNameSource(i, i2);
                SubscriptionInfo subscriptionInfo = getSubscriptionInfo(i);
                if (subscriptionInfo != null && subscriptionInfo.isEmbedded()) {
                    int cardId = subscriptionInfo.getCardId();
                    log("Updating embedded sub nickname on cardId: " + cardId);
                    this.mEuiccManager.updateSubscriptionNickname(i, str, PendingIntent.getService(this.mContext, 0, new Intent(), 67108864));
                }
                return 1;
            }
            log("No need to update the display name. nameSource=" + SubscriptionManager.displayNameSourceToString(i2) + ", existing name=" + subscriptionInfoInternal.getDisplayName() + ", source=" + SubscriptionManager.displayNameSourceToString(subscriptionInfoInternal.getDisplayNameSource()));
            return 0;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @RequiresPermission("android.permission.MODIFY_PHONE_STATE")
    public int setDisplayNumber(String str, int i) {
        enforcePermissions("setDisplayNumber", "android.permission.MODIFY_PHONE_STATE");
        logl("setDisplayNumber: subId=" + i + ", number=" + Rlog.pii(TelephonyUtils.IS_DEBUGGABLE, str) + ", calling package=" + getCallingPackage());
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mSubscriptionDatabaseManager.setNumber(i, str);
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return 1;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    @RequiresPermission("android.permission.MODIFY_PHONE_STATE")
    public int setDataRoaming(int i, int i2) {
        enforcePermissions("setDataRoaming", "android.permission.MODIFY_PHONE_STATE");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            if (i < 0) {
                throw new IllegalArgumentException("Invalid roaming value " + i);
            }
            this.mSubscriptionDatabaseManager.setDataRoaming(i2, i);
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return 1;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    @RequiresPermission(anyOf = {"android.permission.MODIFY_PHONE_STATE", "carrier privileges"})
    public int setOpportunistic(boolean z, int i, String str) {
        TelephonyPermissions.enforceAnyPermissionGrantedOrCarrierPrivileges(this.mContext, Binder.getCallingUid(), i, true, "setOpportunistic", new String[]{"android.permission.MODIFY_PHONE_STATE"});
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mSubscriptionDatabaseManager.setOpportunistic(i, z);
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return 1;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    @RequiresPermission(anyOf = {"android.permission.MODIFY_PHONE_STATE", "carrier privileges"})
    public ParcelUuid createSubscriptionGroup(int[] iArr, String str) {
        this.mAppOpsManager.checkPackage(Binder.getCallingUid(), str);
        Objects.requireNonNull(iArr, "createSubscriptionGroup");
        if (iArr.length == 0) {
            throw new IllegalArgumentException("Invalid subIdList " + Arrays.toString(iArr));
        } else if (this.mContext.checkCallingOrSelfPermission("android.permission.MODIFY_PHONE_STATE") != 0 && !checkCarrierPrivilegeOnSubList(iArr, str)) {
            throw new SecurityException("CreateSubscriptionGroup needs MODIFY_PHONE_STATE or carrier privilege permission on all specified subscriptions");
        } else {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                ParcelUuid parcelUuid = new ParcelUuid(UUID.randomUUID());
                String parcelUuid2 = parcelUuid.toString();
                for (int i : iArr) {
                    this.mSubscriptionDatabaseManager.setGroupUuid(i, parcelUuid2);
                    this.mSubscriptionDatabaseManager.setGroupOwner(i, str);
                }
                updateGroupDisabled();
                MultiSimSettingController.getInstance().notifySubscriptionGroupChanged(parcelUuid);
                return parcelUuid;
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    @RequiresPermission("android.permission.MODIFY_PHONE_STATE")
    public void setPreferredDataSubscriptionId(int i, boolean z, ISetOpportunisticDataCallback iSetOpportunisticDataCallback) {
        enforcePermissions("setPreferredDataSubscriptionId", "android.permission.MODIFY_PHONE_STATE");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            PhoneSwitcher phoneSwitcher = PhoneSwitcher.getInstance();
            if (phoneSwitcher == null) {
                loge("Set preferred data sub: phoneSwitcher is null.");
                if (iSetOpportunisticDataCallback != null) {
                    try {
                        iSetOpportunisticDataCallback.onComplete(4);
                    } catch (RemoteException e) {
                        loge("RemoteException " + e);
                    }
                }
                return;
            }
            phoneSwitcher.trySetOpportunisticDataSubscription(i, z, iSetOpportunisticDataCallback);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @RequiresPermission("android.permission.READ_PRIVILEGED_PHONE_STATE")
    public int getPreferredDataSubscriptionId() {
        enforcePermissions("getPreferredDataSubscriptionId", "android.permission.READ_PRIVILEGED_PHONE_STATE");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            PhoneSwitcher phoneSwitcher = PhoneSwitcher.getInstance();
            if (phoneSwitcher == null) {
                loge("getPreferredDataSubscriptionId: PhoneSwitcher not available. Return the default data sub " + getDefaultDataSubId());
                return getDefaultDataSubId();
            }
            return phoneSwitcher.getAutoSelectedDataSubId();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @RequiresPermission(anyOf = {"android.permission.READ_PHONE_STATE", "android.permission.READ_PRIVILEGED_PHONE_STATE", "carrier privileges"})
    public List<SubscriptionInfo> getOpportunisticSubscriptions(final String str, final String str2) {
        if (!TelephonyPermissions.checkReadPhoneStateOnAnyActiveSub(this.mContext, Binder.getCallingPid(), Binder.getCallingUid(), str, str2, "getOpportunisticSubscriptions")) {
            loge("getOpportunisticSubscriptions: " + str + " does not have enough permission. Returning empty list here.");
            return Collections.emptyList();
        }
        return (List) this.mSubscriptionDatabaseManager.getAllSubscriptions().stream().filter(new Predicate() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$$ExternalSyntheticLambda37
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getOpportunisticSubscriptions$16;
                lambda$getOpportunisticSubscriptions$16 = SubscriptionManagerService.this.lambda$getOpportunisticSubscriptions$16(str, str2, (SubscriptionInfoInternal) obj);
                return lambda$getOpportunisticSubscriptions$16;
            }
        }).map(new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$$ExternalSyntheticLambda38
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                SubscriptionInfo lambda$getOpportunisticSubscriptions$17;
                lambda$getOpportunisticSubscriptions$17 = SubscriptionManagerService.this.lambda$getOpportunisticSubscriptions$17(str, str2, (SubscriptionInfoInternal) obj);
                return lambda$getOpportunisticSubscriptions$17;
            }
        }).sorted(Comparator.comparing(new SubscriptionManagerService$$ExternalSyntheticLambda5()).thenComparing(new SubscriptionManagerService$$ExternalSyntheticLambda6())).collect(Collectors.toList());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$getOpportunisticSubscriptions$16(String str, String str2, SubscriptionInfoInternal subscriptionInfoInternal) {
        return subscriptionInfoInternal.isOpportunistic() && TelephonyPermissions.checkCallingOrSelfReadPhoneStateNoThrow(this.mContext, subscriptionInfoInternal.getSubscriptionId(), str, str2, "getOpportunisticSubscriptions");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ SubscriptionInfo lambda$getOpportunisticSubscriptions$17(String str, String str2, SubscriptionInfoInternal subscriptionInfoInternal) {
        return conditionallyRemoveIdentifiers(subscriptionInfoInternal.toSubscriptionInfo(), str, str2, "getOpportunisticSubscriptions");
    }

    @RequiresPermission("android.permission.MODIFY_PHONE_STATE")
    public void removeSubscriptionsFromGroup(int[] iArr, ParcelUuid parcelUuid, String str) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.MODIFY_PHONE_STATE") != 0 && (!checkCarrierPrivilegeOnSubList(iArr, str) || !canPackageManageGroup(parcelUuid, str))) {
            throw new SecurityException("removeSubscriptionsFromGroup needs MODIFY_PHONE_STATE or carrier privilege permission on all specified subscriptions.");
        }
        Objects.requireNonNull(iArr);
        Objects.requireNonNull(parcelUuid);
        if (iArr.length == 0) {
            throw new IllegalArgumentException("subIdList is empty.");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            for (int i : iArr) {
                SubscriptionInfoInternal subscriptionInfoInternal = this.mSubscriptionDatabaseManager.getSubscriptionInfoInternal(i);
                if (subscriptionInfoInternal == null) {
                    throw new IllegalArgumentException("The provided sub id " + i + " is not valid.");
                } else if (!parcelUuid.toString().equals(subscriptionInfoInternal.getGroupUuid())) {
                    throw new IllegalArgumentException("Subscription " + subscriptionInfoInternal.getSubscriptionId() + " doesn't belong to group " + parcelUuid);
                }
            }
            for (final SubscriptionInfoInternal subscriptionInfoInternal2 : this.mSubscriptionDatabaseManager.getAllSubscriptions()) {
                if (IntStream.of(iArr).anyMatch(new IntPredicate() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$$ExternalSyntheticLambda36
                    @Override // java.util.function.IntPredicate
                    public final boolean test(int i2) {
                        boolean lambda$removeSubscriptionsFromGroup$18;
                        lambda$removeSubscriptionsFromGroup$18 = SubscriptionManagerService.lambda$removeSubscriptionsFromGroup$18(SubscriptionInfoInternal.this, i2);
                        return lambda$removeSubscriptionsFromGroup$18;
                    }
                })) {
                    this.mSubscriptionDatabaseManager.setGroupUuid(subscriptionInfoInternal2.getSubscriptionId(), PhoneConfigurationManager.SSSS);
                    this.mSubscriptionDatabaseManager.setGroupOwner(subscriptionInfoInternal2.getSubscriptionId(), PhoneConfigurationManager.SSSS);
                } else if (subscriptionInfoInternal2.getGroupUuid().equals(parcelUuid.toString())) {
                    this.mSubscriptionDatabaseManager.setGroupOwner(subscriptionInfoInternal2.getSubscriptionId(), str);
                }
            }
            updateGroupDisabled();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$removeSubscriptionsFromGroup$18(SubscriptionInfoInternal subscriptionInfoInternal, int i) {
        return i == subscriptionInfoInternal.getSubscriptionId();
    }

    @RequiresPermission(anyOf = {"android.permission.MODIFY_PHONE_STATE", "carrier privileges"})
    public void addSubscriptionsIntoGroup(int[] iArr, ParcelUuid parcelUuid, String str) {
        Objects.requireNonNull(iArr, "subIdList");
        if (iArr.length == 0) {
            throw new IllegalArgumentException("Invalid subId list");
        }
        Objects.requireNonNull(parcelUuid, "groupUuid");
        String parcelUuid2 = parcelUuid.toString();
        if (parcelUuid2.equals("00000000-0000-0000-0000-000000000000")) {
            throw new IllegalArgumentException("Invalid groupUuid");
        }
        this.mAppOpsManager.checkPackage(Binder.getCallingUid(), str);
        if (this.mContext.checkCallingOrSelfPermission("android.permission.MODIFY_PHONE_STATE") != 0 && (!checkCarrierPrivilegeOnSubList(iArr, str) || !canPackageManageGroup(parcelUuid, str))) {
            throw new SecurityException("Requires MODIFY_PHONE_STATE or carrier privilege permissions on subscriptions and the group.");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            for (int i : iArr) {
                this.mSubscriptionDatabaseManager.setGroupUuid(i, parcelUuid2);
                this.mSubscriptionDatabaseManager.setGroupOwner(i, str);
            }
            updateGroupDisabled();
            MultiSimSettingController.getInstance().notifySubscriptionGroupChanged(parcelUuid);
            logl("addSubscriptionsIntoGroup: add subs " + Arrays.toString(iArr) + " to the group.");
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @RequiresPermission(anyOf = {"android.permission.READ_PHONE_STATE", "android.permission.READ_PRIVILEGED_PHONE_STATE", "carrier privileges"})
    public List<SubscriptionInfo> getSubscriptionsInGroup(final ParcelUuid parcelUuid, final String str, final String str2) {
        if (CompatChanges.isChangeEnabled(213902861L, Binder.getCallingUid())) {
            try {
                if (!TelephonyPermissions.checkCallingOrSelfReadDeviceIdentifiers(this.mContext, str, str2, "getSubscriptionsInGroup")) {
                    EventLog.writeEvent(1397638484, "213902861", Integer.valueOf(Binder.getCallingUid()));
                    throw new SecurityException("Need to have carrier privileges or access to device identifiers to call getSubscriptionsInGroup");
                }
            } catch (SecurityException e) {
                EventLog.writeEvent(1397638484, "213902861", Integer.valueOf(Binder.getCallingUid()));
                throw e;
            }
        }
        return (List) this.mSubscriptionDatabaseManager.getAllSubscriptions().stream().map(new SubscriptionManagerService$$ExternalSyntheticLambda1()).filter(new Predicate() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$$ExternalSyntheticLambda33
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getSubscriptionsInGroup$19;
                lambda$getSubscriptionsInGroup$19 = SubscriptionManagerService.this.lambda$getSubscriptionsInGroup$19(parcelUuid, str, str2, (SubscriptionInfo) obj);
                return lambda$getSubscriptionsInGroup$19;
            }
        }).map(new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$$ExternalSyntheticLambda34
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                SubscriptionInfo lambda$getSubscriptionsInGroup$20;
                lambda$getSubscriptionsInGroup$20 = SubscriptionManagerService.this.lambda$getSubscriptionsInGroup$20(str, str2, (SubscriptionInfo) obj);
                return lambda$getSubscriptionsInGroup$20;
            }
        }).collect(Collectors.toList());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$getSubscriptionsInGroup$19(ParcelUuid parcelUuid, String str, String str2, SubscriptionInfo subscriptionInfo) {
        return parcelUuid.equals(subscriptionInfo.getGroupUuid()) && (this.mSubscriptionManager.canManageSubscription(subscriptionInfo, str) || TelephonyPermissions.checkCallingOrSelfReadPhoneStateNoThrow(this.mContext, subscriptionInfo.getSubscriptionId(), str, str2, "getSubscriptionsInGroup"));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ SubscriptionInfo lambda$getSubscriptionsInGroup$20(String str, String str2, SubscriptionInfo subscriptionInfo) {
        return conditionallyRemoveIdentifiers(subscriptionInfo, str, str2, "getSubscriptionsInGroup");
    }

    public int getSlotIndex(int i) {
        if (i == Integer.MAX_VALUE) {
            i = getDefaultSubId();
        }
        for (Map.Entry<Integer, Integer> entry : this.mSlotIndexToSubId.entrySet()) {
            if (entry.getValue().intValue() == i) {
                return entry.getKey().intValue();
            }
        }
        return -1;
    }

    public int getSubId(int i) {
        if (i == Integer.MAX_VALUE) {
            i = getSlotIndex(getDefaultSubId());
        }
        if (SubscriptionManager.isValidSlotIndex(i) || i == -1) {
            return this.mSlotIndexToSubId.getOrDefault(Integer.valueOf(i), -1).intValue();
        }
        return -1;
    }

    private void updateDefaultSubId() {
        int defaultDataSubId;
        if (this.mTelephonyManager.isVoiceCapable()) {
            defaultDataSubId = getDefaultVoiceSubId();
        } else {
            defaultDataSubId = getDefaultDataSubId();
        }
        if (!this.mSlotIndexToSubId.containsValue(Integer.valueOf(defaultDataSubId))) {
            int[] activeSubIdList = getActiveSubIdList(true);
            if (activeSubIdList.length > 0) {
                defaultDataSubId = activeSubIdList[0];
                log("updateDefaultSubId: First available active sub = " + defaultDataSubId);
            } else {
                defaultDataSubId = -1;
            }
        }
        if (this.mDefaultSubId.get() != defaultDataSubId) {
            int phoneId = getPhoneId(defaultDataSubId);
            logl("updateDefaultSubId: Default sub id updated from " + this.mDefaultSubId.get() + " to " + defaultDataSubId + ", phoneId=" + phoneId);
            this.mDefaultSubId.set(defaultDataSubId);
            Intent intent = new Intent("android.telephony.action.DEFAULT_SUBSCRIPTION_CHANGED");
            intent.addFlags(NetworkStackConstants.NEIGHBOR_ADVERTISEMENT_FLAG_OVERRIDE);
            SubscriptionManager.putPhoneIdAndSubIdExtra(intent, phoneId, defaultDataSubId);
            this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
        }
    }

    public int getDefaultSubId() {
        return this.mDefaultSubId.get();
    }

    public int getPhoneId(int i) {
        if (i == Integer.MAX_VALUE) {
            i = getDefaultSubId();
        }
        if (SubscriptionManager.isValidSubscriptionId(i)) {
            int slotIndex = getSlotIndex(i);
            return SubscriptionManager.isValidSlotIndex(slotIndex) ? slotIndex : KeepaliveStatus.INVALID_HANDLE;
        }
        return -1;
    }

    public int getDefaultDataSubId() {
        return this.mDefaultDataSubId.get();
    }

    @RequiresPermission("android.permission.MODIFY_PHONE_STATE")
    public void setDefaultDataSubId(int i) {
        enforcePermissions("setDefaultDataSubId", "android.permission.MODIFY_PHONE_STATE");
        if (i == Integer.MAX_VALUE) {
            throw new RuntimeException("setDefaultDataSubId called with DEFAULT_SUBSCRIPTION_ID");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            if (this.mDefaultDataSubId.set(i)) {
                MultiSimSettingController.getInstance().notifyDefaultDataSubChanged();
                Intent intent = new Intent("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED");
                intent.addFlags(NetworkStackConstants.NEIGHBOR_ADVERTISEMENT_FLAG_OVERRIDE);
                SubscriptionManager.putSubscriptionIdExtra(intent, i);
                this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
                updateDefaultSubId();
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public int getDefaultVoiceSubId() {
        return this.mDefaultVoiceSubId.get();
    }

    @RequiresPermission("android.permission.MODIFY_PHONE_STATE")
    public void setDefaultVoiceSubId(int i) {
        enforcePermissions("setDefaultVoiceSubId", "android.permission.MODIFY_PHONE_STATE");
        if (i == Integer.MAX_VALUE) {
            throw new RuntimeException("setDefaultVoiceSubId called with DEFAULT_SUB_ID");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            if (this.mDefaultVoiceSubId.set(i)) {
                Intent intent = new Intent("android.intent.action.ACTION_DEFAULT_VOICE_SUBSCRIPTION_CHANGED");
                intent.addFlags(NetworkStackConstants.NEIGHBOR_ADVERTISEMENT_FLAG_OVERRIDE);
                SubscriptionManager.putSubscriptionIdExtra(intent, i);
                this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
                PhoneAccountHandle phoneAccountHandleForSubscriptionId = i == -1 ? null : this.mTelephonyManager.getPhoneAccountHandleForSubscriptionId(i);
                TelecomManager telecomManager = (TelecomManager) this.mContext.getSystemService(TelecomManager.class);
                if (telecomManager != null) {
                    telecomManager.setUserSelectedOutgoingPhoneAccount(phoneAccountHandleForSubscriptionId);
                }
                updateDefaultSubId();
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public int getDefaultSmsSubId() {
        return this.mDefaultSmsSubId.get();
    }

    @RequiresPermission("android.permission.MODIFY_PHONE_STATE")
    public void setDefaultSmsSubId(int i) {
        enforcePermissions("setDefaultSmsSubId", "android.permission.MODIFY_PHONE_STATE");
        if (i == Integer.MAX_VALUE) {
            throw new RuntimeException("setDefaultSmsSubId called with DEFAULT_SUB_ID");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            if (this.mDefaultSmsSubId.set(i)) {
                Intent intent = new Intent("android.telephony.action.DEFAULT_SMS_SUBSCRIPTION_CHANGED");
                intent.addFlags(NetworkStackConstants.NEIGHBOR_ADVERTISEMENT_FLAG_OVERRIDE);
                SubscriptionManager.putSubscriptionIdExtra(intent, i);
                this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @RequiresPermission("android.permission.READ_PRIVILEGED_PHONE_STATE")
    public int[] getActiveSubIdList(final boolean z) {
        enforcePermissions("getActiveSubIdList", "android.permission.READ_PRIVILEGED_PHONE_STATE");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mSlotIndexToSubId.values().stream().filter(new Predicate() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$$ExternalSyntheticLambda7
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$getActiveSubIdList$21;
                    lambda$getActiveSubIdList$21 = SubscriptionManagerService.this.lambda$getActiveSubIdList$21(z, (Integer) obj);
                    return lambda$getActiveSubIdList$21;
                }
            }).mapToInt(new ToIntFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$$ExternalSyntheticLambda8
                @Override // java.util.function.ToIntFunction
                public final int applyAsInt(Object obj) {
                    int intValue;
                    intValue = ((Integer) obj).intValue();
                    return intValue;
                }
            }).toArray();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$getActiveSubIdList$21(boolean z, Integer num) {
        SubscriptionInfoInternal subscriptionInfoInternal = this.mSubscriptionDatabaseManager.getSubscriptionInfoInternal(num.intValue());
        return subscriptionInfoInternal != null && (!z || subscriptionInfoInternal.isVisible());
    }

    @RequiresPermission("android.permission.MODIFY_PHONE_STATE")
    public int setSubscriptionProperty(int i, String str, String str2) {
        enforcePermissions("setSubscriptionProperty", "android.permission.MODIFY_PHONE_STATE");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            logl("setSubscriptionProperty: subId=" + i + ", columnName=" + str + ", value=" + str2 + ", calling package=" + getCallingPackage());
            if (!Telephony.SimInfo.getAllColumns().contains(str)) {
                throw new IllegalArgumentException("Invalid column name " + str);
            } else if (!DIRECT_ACCESS_SUBSCRIPTION_COLUMNS.contains(str)) {
                throw new SecurityException("Column " + str + " is not allowed be directly accessed through setSubscriptionProperty.");
            } else {
                this.mSubscriptionDatabaseManager.setSubscriptionProperty(i, str, str2);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return 1;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    @RequiresPermission(anyOf = {"android.permission.READ_PHONE_STATE", "android.permission.READ_PRIVILEGED_PHONE_STATE", "carrier privileges"})
    public String getSubscriptionProperty(int i, String str, String str2, String str3) {
        Objects.requireNonNull(str);
        if (TelephonyPermissions.checkCallingOrSelfReadPhoneState(this.mContext, i, str2, str3, "getSubscriptionProperty")) {
            if (!Telephony.SimInfo.getAllColumns().contains(str)) {
                throw new IllegalArgumentException("Invalid column name " + str);
            } else if (!DIRECT_ACCESS_SUBSCRIPTION_COLUMNS.contains(str)) {
                throw new SecurityException("Column " + str + " is not allowed be directly accessed through getSubscriptionProperty.");
            } else {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    try {
                        Object subscriptionProperty = this.mSubscriptionDatabaseManager.getSubscriptionProperty(i, str);
                        if (subscriptionProperty instanceof Integer) {
                            return String.valueOf(subscriptionProperty);
                        }
                        if (subscriptionProperty instanceof String) {
                            return (String) subscriptionProperty;
                        }
                        if (subscriptionProperty instanceof byte[]) {
                            return Base64.encodeToString((byte[]) subscriptionProperty, 0);
                        }
                        throw new RuntimeException("Unexpected type " + subscriptionProperty.getClass().getTypeName() + " was returned from SubscriptionDatabaseManager for column " + str);
                    } catch (IllegalArgumentException unused) {
                        logv("getSubscriptionProperty: Invalid subId " + i + ", columnName=" + str);
                        Binder.restoreCallingIdentity(clearCallingIdentity);
                        return null;
                    }
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }
        throw new SecurityException("Need READ_PHONE_STATE, READ_PRIVILEGED_PHONE_STATE, or carrier privilege");
    }

    public boolean setSubscriptionEnabled(boolean z, int i) {
        enforcePermissions("setSubscriptionEnabled", "android.permission.MODIFY_PHONE_STATE");
        return true;
    }

    @RequiresPermission("android.permission.READ_PRIVILEGED_PHONE_STATE")
    public boolean isSubscriptionEnabled(int i) {
        boolean z;
        enforcePermissions("isSubscriptionEnabled", "android.permission.READ_PRIVILEGED_PHONE_STATE");
        if (!SubscriptionManager.isValidSubscriptionId(i)) {
            throw new IllegalArgumentException("Invalid subscription id " + i);
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            SubscriptionInfoInternal subscriptionInfoInternal = this.mSubscriptionDatabaseManager.getSubscriptionInfoInternal(i);
            if (subscriptionInfoInternal != null) {
                if (subscriptionInfoInternal.isActive()) {
                    z = true;
                    return z;
                }
            }
            z = false;
            return z;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @RequiresPermission("android.permission.READ_PRIVILEGED_PHONE_STATE")
    public int getEnabledSubscriptionId(final int i) {
        enforcePermissions("getEnabledSubscriptionId", "android.permission.READ_PRIVILEGED_PHONE_STATE");
        if (!SubscriptionManager.isValidSlotIndex(i)) {
            throw new IllegalArgumentException("Invalid slot index " + i);
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mSubscriptionDatabaseManager.getAllSubscriptions().stream().filter(new Predicate() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$$ExternalSyntheticLambda12
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$getEnabledSubscriptionId$23;
                    lambda$getEnabledSubscriptionId$23 = SubscriptionManagerService.lambda$getEnabledSubscriptionId$23(i, (SubscriptionInfoInternal) obj);
                    return lambda$getEnabledSubscriptionId$23;
                }
            }).mapToInt(new ToIntFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$$ExternalSyntheticLambda13
                @Override // java.util.function.ToIntFunction
                public final int applyAsInt(Object obj) {
                    return ((SubscriptionInfoInternal) obj).getSubscriptionId();
                }
            }).findFirst().orElse(-1);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getEnabledSubscriptionId$23(int i, SubscriptionInfoInternal subscriptionInfoInternal) {
        return subscriptionInfoInternal.isActive() && subscriptionInfoInternal.getSimSlotIndex() == i;
    }

    @RequiresPermission(anyOf = {"android.permission.READ_PHONE_STATE", "android.permission.READ_PRIVILEGED_PHONE_STATE", "carrier privileges"})
    public boolean isActiveSubId(int i, String str, String str2) {
        boolean z;
        if (!TelephonyPermissions.checkCallingOrSelfReadPhoneState(this.mContext, i, str, str2, "isActiveSubId")) {
            throw new SecurityException("Need READ_PHONE_STATE, READ_PRIVILEGED_PHONE_STATE, or carrier privilege");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            SubscriptionInfoInternal subscriptionInfoInternal = this.mSubscriptionDatabaseManager.getSubscriptionInfoInternal(i);
            if (subscriptionInfoInternal != null) {
                if (subscriptionInfoInternal.isActive()) {
                    z = true;
                    return z;
                }
            }
            z = false;
            return z;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public int getActiveDataSubscriptionId() {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            PhoneSwitcher phoneSwitcher = PhoneSwitcher.getInstance();
            if (phoneSwitcher != null) {
                int activeDataSubId = phoneSwitcher.getActiveDataSubId();
                if (SubscriptionManager.isUsableSubscriptionId(activeDataSubId)) {
                    return activeDataSubId;
                }
            }
            return getDefaultDataSubId();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @RequiresPermission("android.permission.READ_PRIVILEGED_PHONE_STATE")
    public boolean canDisablePhysicalSubscription() {
        boolean z;
        enforcePermissions("canDisablePhysicalSubscription", "android.permission.READ_PRIVILEGED_PHONE_STATE");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            Phone defaultPhone = PhoneFactory.getDefaultPhone();
            if (defaultPhone != null) {
                if (defaultPhone.canDisablePhysicalSubscription()) {
                    z = true;
                    return z;
                }
            }
            z = false;
            return z;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @RequiresPermission("android.permission.MODIFY_PHONE_STATE")
    public int setUiccApplicationsEnabled(boolean z, int i) {
        enforcePermissions("setUiccApplicationsEnabled", "android.permission.MODIFY_PHONE_STATE");
        logl("setUiccApplicationsEnabled: subId=" + i + ", enabled=" + z + ", calling package=" + getCallingPackage());
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mSubscriptionDatabaseManager.setUiccApplicationsEnabled(i, z);
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return 1;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    @RequiresPermission("android.permission.MODIFY_PHONE_STATE")
    public int setDeviceToDeviceStatusSharing(int i, int i2) {
        enforcePermissions("setDeviceToDeviceStatusSharing", "android.permission.MODIFY_PHONE_STATE");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            if (i < 0 || i > 3) {
                throw new IllegalArgumentException("invalid sharing " + i);
            }
            this.mSubscriptionDatabaseManager.setDeviceToDeviceStatusSharingPreference(i2, i);
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return 1;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    @RequiresPermission("android.permission.MODIFY_PHONE_STATE")
    public int setDeviceToDeviceStatusSharingContacts(String str, int i) {
        enforcePermissions("setDeviceToDeviceStatusSharingContacts", "android.permission.MODIFY_PHONE_STATE");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            Objects.requireNonNull(str, "contacts");
            this.mSubscriptionDatabaseManager.setDeviceToDeviceStatusSharingContacts(i, str);
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return 1;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    @RequiresPermission(anyOf = {"android.permission.READ_PHONE_NUMBERS", "android.permission.READ_PRIVILEGED_PHONE_STATE", "carrier privileges"})
    public String getPhoneNumber(int i, int i2, String str, String str2) {
        TelephonyPermissions.enforceAnyPermissionGrantedOrCarrierPrivileges(this.mContext, i, Binder.getCallingUid(), "getPhoneNumber", new String[]{"android.permission.READ_PHONE_NUMBERS", "android.permission.READ_PRIVILEGED_PHONE_STATE"});
        long clearCallingIdentity = Binder.clearCallingIdentity();
        SubscriptionInfoInternal subscriptionInfoInternal = this.mSubscriptionDatabaseManager.getSubscriptionInfoInternal(i);
        if (subscriptionInfoInternal == null) {
            loge("Invalid sub id " + i + ", callingPackage=" + str);
            return PhoneConfigurationManager.SSSS;
        }
        try {
            if (i2 == 1) {
                Phone phone = PhoneFactory.getPhone(getPhoneId(i));
                return phone != null ? TextUtils.emptyIfNull(phone.getLine1Number()) : subscriptionInfoInternal.getNumber();
            } else if (i2 != 2) {
                if (i2 == 3) {
                    return subscriptionInfoInternal.getNumberFromIms();
                }
                throw new IllegalArgumentException("Invalid number source " + i2);
            } else {
                return subscriptionInfoInternal.getNumberFromCarrier();
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @RequiresPermission(anyOf = {"android.permission.READ_PHONE_NUMBERS", "android.permission.READ_PRIVILEGED_PHONE_STATE", "carrier privileges"})
    public String getPhoneNumberFromFirstAvailableSource(int i, String str, String str2) {
        TelephonyPermissions.enforceAnyPermissionGrantedOrCarrierPrivileges(this.mContext, i, Binder.getCallingUid(), "getPhoneNumberFromFirstAvailableSource", new String[]{"android.permission.READ_PHONE_NUMBERS", "android.permission.READ_PRIVILEGED_PHONE_STATE"});
        String phoneNumber = getPhoneNumber(i, 2, str, str2);
        if (TextUtils.isEmpty(phoneNumber)) {
            String phoneNumber2 = getPhoneNumber(i, 1, str, str2);
            if (TextUtils.isEmpty(phoneNumber2)) {
                String phoneNumber3 = getPhoneNumber(i, 3, str, str2);
                return !TextUtils.isEmpty(phoneNumber3) ? phoneNumber3 : PhoneConfigurationManager.SSSS;
            }
            return phoneNumber2;
        }
        return phoneNumber;
    }

    @RequiresPermission("carrier privileges")
    public void setPhoneNumber(int i, int i2, String str, String str2, String str3) {
        if (!TelephonyPermissions.checkCarrierPrivilegeForSubId(this.mContext, i)) {
            throw new SecurityException("setPhoneNumber for CARRIER needs carrier privilege.");
        }
        if (i2 != 2) {
            throw new IllegalArgumentException("setPhoneNumber doesn't accept source " + SubscriptionManager.phoneNumberSourceToString(i2));
        }
        Objects.requireNonNull(str, IccProvider.STR_NUMBER);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mSubscriptionDatabaseManager.setNumberFromCarrier(i, str);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @RequiresPermission(anyOf = {"android.permission.MODIFY_PHONE_STATE", "carrier privileges"})
    public int setUsageSetting(int i, int i2, String str) {
        TelephonyPermissions.enforceAnyPermissionGrantedOrCarrierPrivileges(this.mContext, Binder.getCallingUid(), i2, true, "setUsageSetting", new String[]{"android.permission.MODIFY_PHONE_STATE"});
        if (i < 0 || i > 2) {
            throw new IllegalArgumentException("setUsageSetting: Invalid usage setting: " + i);
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mSubscriptionDatabaseManager.setUsageSetting(i2, i);
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return 1;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    @RequiresPermission("android.permission.MANAGE_SUBSCRIPTION_USER_ASSOCIATION")
    public int setSubscriptionUserHandle(UserHandle userHandle, int i) {
        enforcePermissions("setSubscriptionUserHandle", "android.permission.MANAGE_SUBSCRIPTION_USER_ASSOCIATION");
        if (userHandle == null) {
            userHandle = UserHandle.of(-10000);
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mSubscriptionDatabaseManager.setUserId(i, userHandle.getIdentifier());
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return 1;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    @RequiresPermission("android.permission.MANAGE_SUBSCRIPTION_USER_ASSOCIATION")
    public UserHandle getSubscriptionUserHandle(int i) {
        enforcePermissions("getSubscriptionUserHandle", "android.permission.MANAGE_SUBSCRIPTION_USER_ASSOCIATION");
        if (this.mIsWorkProfileTelephonyEnabled) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                SubscriptionInfoInternal subscriptionInfoInternal = this.mSubscriptionDatabaseManager.getSubscriptionInfoInternal(i);
                if (subscriptionInfoInternal == null) {
                    throw new IllegalArgumentException("getSubscriptionUserHandle: Invalid subId: " + i);
                }
                UserHandle of = UserHandle.of(subscriptionInfoInternal.getUserId());
                if (of.getIdentifier() == -10000) {
                    return null;
                }
                return of;
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
        return null;
    }

    public boolean isSubscriptionAssociatedWithUser(int i, UserHandle userHandle) {
        enforcePermissions("isSubscriptionAssociatedWithUser", "android.permission.MANAGE_SUBSCRIPTION_USER_ASSOCIATION");
        if (this.mIsWorkProfileTelephonyEnabled) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                List<SubscriptionInfo> allSubInfoList = getAllSubInfoList(this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
                if (allSubInfoList != null && !allSubInfoList.isEmpty()) {
                    List<SubscriptionInfo> subscriptionInfoListAssociatedWithUser = getSubscriptionInfoListAssociatedWithUser(userHandle);
                    if (subscriptionInfoListAssociatedWithUser.isEmpty()) {
                        return false;
                    }
                    for (SubscriptionInfo subscriptionInfo : subscriptionInfoListAssociatedWithUser) {
                        if (subscriptionInfo.getSubscriptionId() == i) {
                            return true;
                        }
                    }
                    return false;
                }
                return true;
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
        return true;
    }

    public List<SubscriptionInfo> getSubscriptionInfoListAssociatedWithUser(UserHandle userHandle) {
        enforcePermissions("getSubscriptionInfoListAssociatedWithUser", "android.permission.MANAGE_SUBSCRIPTION_USER_ASSOCIATION");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            List<SubscriptionInfo> allSubInfoList = getAllSubInfoList(this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
            if (allSubInfoList != null && !allSubInfoList.isEmpty()) {
                if (this.mIsWorkProfileTelephonyEnabled) {
                    ArrayList arrayList = new ArrayList();
                    ArrayList arrayList2 = new ArrayList();
                    for (SubscriptionInfo subscriptionInfo : allSubInfoList) {
                        UserHandle subscriptionUserHandle = getSubscriptionUserHandle(subscriptionInfo.getSubscriptionId());
                        if (userHandle.equals(subscriptionUserHandle)) {
                            arrayList.add(subscriptionInfo);
                        } else if (subscriptionUserHandle == null) {
                            arrayList2.add(subscriptionInfo);
                        }
                    }
                    if (arrayList.isEmpty()) {
                        arrayList = arrayList2;
                    }
                    return arrayList;
                }
                return allSubInfoList;
            }
            return new ArrayList();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @RequiresPermission("android.permission.MODIFY_PHONE_STATE")
    public void restoreAllSimSpecificSettingsFromBackup(byte[] bArr) {
        enforcePermissions("restoreAllSimSpecificSettingsFromBackup", "android.permission.MODIFY_PHONE_STATE");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            Bundle bundle = new Bundle();
            bundle.putByteArray("KEY_SIM_SPECIFIC_SETTINGS_DATA", bArr);
            logl("restoreAllSimSpecificSettingsFromBackup");
            this.mContext.getContentResolver().call(SubscriptionManager.SIM_INFO_BACKUP_AND_RESTORE_CONTENT_URI, "restoreSimSpecificSettings", (String) null, bundle);
            this.mSubscriptionDatabaseManager.reloadDatabase();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void registerCallback(SubscriptionManagerServiceCallback subscriptionManagerServiceCallback) {
        this.mSubscriptionManagerServiceCallbacks.add(subscriptionManagerServiceCallback);
    }

    public void unregisterCallback(SubscriptionManagerServiceCallback subscriptionManagerServiceCallback) {
        this.mSubscriptionManagerServiceCallbacks.remove(subscriptionManagerServiceCallback);
    }

    private void enforcePermissions(String str, String... strArr) {
        for (String str2 : strArr) {
            if (this.mContext.checkCallingOrSelfPermission(str2) == 0) {
                return;
            }
        }
        throw new SecurityException(str + ". Does not have any of the following permissions. " + Arrays.toString(strArr));
    }

    public SubscriptionInfoInternal getSubscriptionInfoInternal(int i) {
        return this.mSubscriptionDatabaseManager.getSubscriptionInfoInternal(i);
    }

    public SubscriptionInfo getSubscriptionInfo(int i) {
        SubscriptionInfoInternal subscriptionInfoInternal = getSubscriptionInfoInternal(i);
        if (subscriptionInfoInternal != null) {
            return subscriptionInfoInternal.toSubscriptionInfo();
        }
        return null;
    }

    public void updateSimStateForInactivePort(final int i) {
        this.mHandler.post(new Runnable() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$$ExternalSyntheticLambda11
            @Override // java.lang.Runnable
            public final void run() {
                SubscriptionManagerService.this.lambda$updateSimStateForInactivePort$24(i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateSimStateForInactivePort$24(int i) {
        logl("updateSimStateForInactivePort: slotIndex=" + i);
        if (this.mSlotIndexToSubId.containsKey(Integer.valueOf(i))) {
            this.mSubscriptionDatabaseManager.setUiccApplicationsEnabled(this.mSlotIndexToSubId.get(Integer.valueOf(i)).intValue(), true);
            updateSubscription(i);
        }
    }

    public void updateSimState(final int i, @TelephonyManager.SimState final int i2, final Executor executor, final Runnable runnable) {
        this.mHandler.post(new Runnable() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$$ExternalSyntheticLambda18
            @Override // java.lang.Runnable
            public final void run() {
                SubscriptionManagerService.this.lambda$updateSimState$25(i, i2, executor, runnable);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateSimState$25(int i, int i2, Executor executor, Runnable runnable) {
        this.mSimState[i] = i2;
        logl("updateSimState: slot " + i + " " + TelephonyManager.simStateToString(i2));
        switch (i2) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 10:
                updateSubscription(i);
                break;
        }
        if (executor == null || runnable == null) {
            return;
        }
        executor.execute(runnable);
    }

    private void onDeviceConfigChanged() {
        boolean z = DeviceConfig.getBoolean("telephony", "enable_work_profile_telephony", false);
        if (z != this.mIsWorkProfileTelephonyEnabled) {
            log("onDeviceConfigChanged: isWorkProfileTelephonyEnabled changed from " + this.mIsWorkProfileTelephonyEnabled + " to " + z);
            this.mIsWorkProfileTelephonyEnabled = z;
        }
    }

    private String getCallingPackage() {
        return Binder.getCallingUid() == 1001 ? "com.android.phone" : Arrays.toString(this.mContext.getPackageManager().getPackagesForUid(Binder.getCallingUid()));
    }

    @VisibleForTesting
    public void updateGroupDisabled() {
        List<SubscriptionInfo> activeSubscriptionInfoList = getActiveSubscriptionInfoList(this.mContext.getOpPackageName(), this.mContext.getFeatureId());
        for (final SubscriptionInfo subscriptionInfo : getOpportunisticSubscriptions(this.mContext.getOpPackageName(), this.mContext.getFeatureId())) {
            this.mSubscriptionDatabaseManager.setGroupDisabled(subscriptionInfo.getSubscriptionId(), activeSubscriptionInfoList.stream().noneMatch(new Predicate() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$$ExternalSyntheticLambda27
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$updateGroupDisabled$26;
                    lambda$updateGroupDisabled$26 = SubscriptionManagerService.lambda$updateGroupDisabled$26(subscriptionInfo, (SubscriptionInfo) obj);
                    return lambda$updateGroupDisabled$26;
                }
            }));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateGroupDisabled$26(SubscriptionInfo subscriptionInfo, SubscriptionInfo subscriptionInfo2) {
        return !subscriptionInfo2.isOpportunistic() && Objects.equals(subscriptionInfo.getGroupUuid(), subscriptionInfo2.getGroupUuid());
    }

    private String slotMappingToString() {
        return "[" + ((String) this.mSlotIndexToSubId.entrySet().stream().map(new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$$ExternalSyntheticLambda10
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String lambda$slotMappingToString$27;
                lambda$slotMappingToString$27 = SubscriptionManagerService.lambda$slotMappingToString$27((Map.Entry) obj);
                return lambda$slotMappingToString$27;
            }
        }).collect(Collectors.joining(", "))) + "]";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ String lambda$slotMappingToString$27(Map.Entry entry) {
        return "slot " + entry.getKey() + ": subId=" + entry.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void log(String str) {
        Rlog.d("SMSVC", str);
    }

    private void loge(String str) {
        Rlog.e("SMSVC", str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logl(String str) {
        log(str);
        this.mLocalLog.log(str);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        final AndroidUtilIndentingPrintWriter androidUtilIndentingPrintWriter = new AndroidUtilIndentingPrintWriter(printWriter, "  ");
        androidUtilIndentingPrintWriter.println(SubscriptionManagerService.class.getSimpleName() + ":");
        androidUtilIndentingPrintWriter.println("Active modem count=" + this.mTelephonyManager.getActiveModemCount());
        androidUtilIndentingPrintWriter.println("Logical SIM slot sub id mapping:");
        androidUtilIndentingPrintWriter.increaseIndent();
        this.mSlotIndexToSubId.forEach(new BiConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$$ExternalSyntheticLambda19
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                SubscriptionManagerService.lambda$dump$28(AndroidUtilIndentingPrintWriter.this, (Integer) obj, (Integer) obj2);
            }
        });
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.println("ICCID:");
        androidUtilIndentingPrintWriter.increaseIndent();
        for (int i = 0; i < this.mTelephonyManager.getActiveModemCount(); i++) {
            androidUtilIndentingPrintWriter.println("slot " + i + ": " + SubscriptionInfo.givePrintableIccid(getIccId(i)));
        }
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.println();
        androidUtilIndentingPrintWriter.println("defaultSubId=" + getDefaultSubId());
        androidUtilIndentingPrintWriter.println("defaultVoiceSubId=" + getDefaultVoiceSubId());
        androidUtilIndentingPrintWriter.println("defaultDataSubId=" + getDefaultDataSubId());
        androidUtilIndentingPrintWriter.println("activeDataSubId=" + getActiveDataSubscriptionId());
        androidUtilIndentingPrintWriter.println("defaultSmsSubId=" + getDefaultSmsSubId());
        androidUtilIndentingPrintWriter.println("areAllSubscriptionsLoaded=" + areAllSubscriptionsLoaded());
        androidUtilIndentingPrintWriter.println();
        for (int i2 = 0; i2 < this.mSimState.length; i2++) {
            androidUtilIndentingPrintWriter.println("mSimState[" + i2 + "]=" + TelephonyManager.simStateToString(this.mSimState[i2]));
        }
        androidUtilIndentingPrintWriter.println();
        androidUtilIndentingPrintWriter.println("Active subscriptions:");
        androidUtilIndentingPrintWriter.increaseIndent();
        this.mSubscriptionDatabaseManager.getAllSubscriptions().stream().filter(new SubscriptionManagerService$$ExternalSyntheticLambda3()).forEach(new Consumer() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$$ExternalSyntheticLambda20
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                AndroidUtilIndentingPrintWriter.this.println((SubscriptionInfoInternal) obj);
            }
        });
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.println();
        androidUtilIndentingPrintWriter.println("All subscriptions:");
        androidUtilIndentingPrintWriter.increaseIndent();
        this.mSubscriptionDatabaseManager.getAllSubscriptions().forEach(new Consumer() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$$ExternalSyntheticLambda20
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                AndroidUtilIndentingPrintWriter.this.println((SubscriptionInfoInternal) obj);
            }
        });
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.println();
        androidUtilIndentingPrintWriter.print("Embedded subscriptions: [");
        androidUtilIndentingPrintWriter.println(((String) this.mSubscriptionDatabaseManager.getAllSubscriptions().stream().filter(new SubscriptionManagerService$$ExternalSyntheticLambda21()).map(new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$$ExternalSyntheticLambda22
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String lambda$dump$29;
                lambda$dump$29 = SubscriptionManagerService.lambda$dump$29((SubscriptionInfoInternal) obj);
                return lambda$dump$29;
            }
        }).collect(Collectors.joining(", "))) + "]");
        androidUtilIndentingPrintWriter.print("Opportunistic subscriptions: [");
        androidUtilIndentingPrintWriter.println(((String) this.mSubscriptionDatabaseManager.getAllSubscriptions().stream().filter(new Predicate() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$$ExternalSyntheticLambda23
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ((SubscriptionInfoInternal) obj).isOpportunistic();
            }
        }).map(new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$$ExternalSyntheticLambda24
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String lambda$dump$30;
                lambda$dump$30 = SubscriptionManagerService.lambda$dump$30((SubscriptionInfoInternal) obj);
                return lambda$dump$30;
            }
        }).collect(Collectors.joining(", "))) + "]");
        androidUtilIndentingPrintWriter.print("getAvailableSubscriptionInfoList: [");
        androidUtilIndentingPrintWriter.println(((String) getAvailableSubscriptionInfoList(this.mContext.getOpPackageName(), this.mContext.getFeatureId()).stream().map(new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$$ExternalSyntheticLambda25
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String lambda$dump$31;
                lambda$dump$31 = SubscriptionManagerService.lambda$dump$31((SubscriptionInfo) obj);
                return lambda$dump$31;
            }
        }).collect(Collectors.joining(", "))) + "]");
        androidUtilIndentingPrintWriter.print("getSelectableSubscriptionInfoList: [");
        androidUtilIndentingPrintWriter.println(((String) this.mSubscriptionManager.getSelectableSubscriptionInfoList().stream().map(new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionManagerService$$ExternalSyntheticLambda26
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String lambda$dump$32;
                lambda$dump$32 = SubscriptionManagerService.lambda$dump$32((SubscriptionInfo) obj);
                return lambda$dump$32;
            }
        }).collect(Collectors.joining(", "))) + "]");
        if (this.mEuiccManager != null) {
            androidUtilIndentingPrintWriter.println("Euicc enabled=" + this.mEuiccManager.isEnabled());
        }
        androidUtilIndentingPrintWriter.println();
        androidUtilIndentingPrintWriter.println("Local log:");
        androidUtilIndentingPrintWriter.increaseIndent();
        this.mLocalLog.dump(fileDescriptor, androidUtilIndentingPrintWriter, strArr);
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.println();
        this.mSubscriptionDatabaseManager.dump(fileDescriptor, androidUtilIndentingPrintWriter, strArr);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$dump$28(AndroidUtilIndentingPrintWriter androidUtilIndentingPrintWriter, Integer num, Integer num2) {
        androidUtilIndentingPrintWriter.println("Logical SIM slot " + num + ": subId=" + num2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ String lambda$dump$29(SubscriptionInfoInternal subscriptionInfoInternal) {
        return String.valueOf(subscriptionInfoInternal.getSubscriptionId());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ String lambda$dump$30(SubscriptionInfoInternal subscriptionInfoInternal) {
        return String.valueOf(subscriptionInfoInternal.getSubscriptionId());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ String lambda$dump$31(SubscriptionInfo subscriptionInfo) {
        return String.valueOf(subscriptionInfo.getSubscriptionId());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ String lambda$dump$32(SubscriptionInfo subscriptionInfo) {
        return String.valueOf(subscriptionInfo.getSubscriptionId());
    }
}
