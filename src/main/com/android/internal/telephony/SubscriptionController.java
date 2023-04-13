package com.android.internal.telephony;

import android.annotation.RequiresPermission;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.app.compat.CompatChanges;
import android.compat.annotation.UnsupportedAppUsage;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.os.TelephonyServiceManager;
import android.os.UserHandle;
import android.provider.Settings;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.AnomalyReporter;
import android.telephony.CarrierConfigManager;
import android.telephony.RadioAccessFamily;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyFrameworkInitializer;
import android.telephony.TelephonyManager;
import android.telephony.TelephonyRegistryManager;
import android.telephony.UiccAccessRule;
import android.telephony.UiccPortInfo;
import android.telephony.UiccSlotInfo;
import android.telephony.UiccSlotMapping;
import android.telephony.euicc.EuiccManager;
import android.text.TextUtils;
import android.util.EventLog;
import android.util.Log;
import com.android.ims.ImsManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.ISub;
import com.android.internal.telephony.data.KeepaliveStatus;
import com.android.internal.telephony.data.PhoneSwitcher;
import com.android.internal.telephony.metrics.TelephonyMetrics;
import com.android.internal.telephony.uicc.IccUtils;
import com.android.internal.telephony.uicc.UiccCard;
import com.android.internal.telephony.uicc.UiccController;
import com.android.internal.telephony.uicc.UiccProfile;
import com.android.internal.telephony.uicc.UiccSlot;
import com.android.internal.telephony.util.ArrayUtils;
import com.android.internal.telephony.util.NetworkStackConstants;
import com.android.internal.telephony.util.TelephonyUtils;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class SubscriptionController extends ISub.Stub {
    public static final long REQUIRE_DEVICE_IDENTIFIERS_FOR_GROUP_UUID = 213902861;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private int[] colorArr;
    private AppOpsManager mAppOps;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected Context mContext;
    private long mLastISubServiceRegTime;
    protected TelephonyManager mTelephonyManager;
    protected UiccController mUiccController;
    private static final boolean VDBG = Rlog.isLoggable("SubscriptionController", 2);
    private static final ParcelUuid INVALID_GROUP_UUID = ParcelUuid.fromString("00000000-0000-0000-0000-000000000000");
    private static final Comparator<SubscriptionInfo> SUBSCRIPTION_INFO_COMPARATOR = new Comparator() { // from class: com.android.internal.telephony.SubscriptionController$$ExternalSyntheticLambda10
        @Override // java.util.Comparator
        public final int compare(Object obj, Object obj2) {
            int lambda$static$0;
            lambda$static$0 = SubscriptionController.lambda$static$0((SubscriptionInfo) obj, (SubscriptionInfo) obj2);
            return lambda$static$0;
        }
    };
    protected static SubscriptionController sInstance = null;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private static int mDefaultPhoneId = KeepaliveStatus.INVALID_HANDLE;
    private static final Set<String> GROUP_SHARING_PROPERTIES = new HashSet(Arrays.asList("volte_vt_enabled", "vt_ims_enabled", "wfc_ims_enabled", "wfc_ims_mode", "wfc_ims_roaming_mode", "wfc_ims_roaming_enabled", "data_roaming", "display_name", "enabled_mobile_data_policies", "uicc_applications_enabled", "ims_rcs_uce_enabled", "cross_sim_calling_enabled", "nr_advanced_calling_enabled", "user_handle"));
    private final LocalLog mLocalLog = new LocalLog(128);
    private Object mSubInfoListLock = new Object();
    private final List<SubscriptionInfo> mCacheActiveSubInfoList = new ArrayList();
    private List<SubscriptionInfo> mCacheOpportunisticSubInfoList = new ArrayList();
    private AtomicBoolean mOpptSubInfoListChangedDirtyBit = new AtomicBoolean();
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected final Object mLock = new Object();
    private final WatchedSlotIndexToSubIds mSlotIndexToSubIds = new WatchedSlotIndexToSubIds();
    private final WatchedInt mDefaultFallbackSubId = new WatchedInt(-1) { // from class: com.android.internal.telephony.SubscriptionController.1
        @Override // com.android.internal.telephony.SubscriptionController.WatchedInt
        public void set(int i) {
            super.set(i);
            SubscriptionController.invalidateDefaultSubIdCaches();
            SubscriptionController.invalidateSlotIndexCaches();
        }
    };
    private RegistrantList mUiccAppsEnableChangeRegList = new RegistrantList();

    private boolean isSubscriptionForRemoteSim(int i) {
        return i == 1;
    }

    public boolean isSubscriptionManagerServiceEnabled() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$static$0(SubscriptionInfo subscriptionInfo, SubscriptionInfo subscriptionInfo2) {
        int simSlotIndex = subscriptionInfo.getSimSlotIndex() - subscriptionInfo2.getSimSlotIndex();
        return simSlotIndex == 0 ? subscriptionInfo.getSubscriptionId() - subscriptionInfo2.getSubscriptionId() : simSlotIndex;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class WatchedSlotIndexToSubIds {
        private final Map<Integer, ArrayList<Integer>> mSlotIndexToSubIds;

        private WatchedSlotIndexToSubIds() {
            this.mSlotIndexToSubIds = new ConcurrentHashMap();
        }

        public void clear() {
            this.mSlotIndexToSubIds.clear();
            SubscriptionController.invalidateDefaultSubIdCaches();
            SubscriptionController.invalidateSlotIndexCaches();
        }

        public Set<Map.Entry<Integer, ArrayList<Integer>>> entrySet() {
            return this.mSlotIndexToSubIds.entrySet();
        }

        public ArrayList<Integer> getCopy(int i) {
            ArrayList<Integer> arrayList = this.mSlotIndexToSubIds.get(Integer.valueOf(i));
            if (arrayList == null) {
                return null;
            }
            return new ArrayList<>(arrayList);
        }

        public void put(int i, ArrayList<Integer> arrayList) {
            this.mSlotIndexToSubIds.put(Integer.valueOf(i), arrayList);
            SubscriptionController.invalidateDefaultSubIdCaches();
            SubscriptionController.invalidateSlotIndexCaches();
        }

        public void remove(int i) {
            this.mSlotIndexToSubIds.remove(Integer.valueOf(i));
            SubscriptionController.invalidateDefaultSubIdCaches();
            SubscriptionController.invalidateSlotIndexCaches();
        }

        public int size() {
            return this.mSlotIndexToSubIds.size();
        }

        @VisibleForTesting
        public Map<Integer, ArrayList<Integer>> getMap() {
            return this.mSlotIndexToSubIds;
        }

        public int removeFromSubIdList(int i, int i2) {
            ArrayList<Integer> arrayList = this.mSlotIndexToSubIds.get(Integer.valueOf(i));
            if (arrayList == null) {
                return -1;
            }
            if (arrayList.contains(Integer.valueOf(i2))) {
                arrayList.remove(new Integer(i2));
                if (arrayList.isEmpty()) {
                    this.mSlotIndexToSubIds.remove(Integer.valueOf(i));
                }
                SubscriptionController.invalidateDefaultSubIdCaches();
                SubscriptionController.invalidateSlotIndexCaches();
                return 1;
            }
            return -2;
        }

        public void addToSubIdList(int i, Integer num) {
            ArrayList<Integer> arrayList = this.mSlotIndexToSubIds.get(Integer.valueOf(i));
            if (arrayList == null) {
                ArrayList<Integer> arrayList2 = new ArrayList<>();
                arrayList2.add(num);
                this.mSlotIndexToSubIds.put(Integer.valueOf(i), arrayList2);
            } else {
                arrayList.add(num);
            }
            SubscriptionController.invalidateDefaultSubIdCaches();
            SubscriptionController.invalidateSlotIndexCaches();
        }

        public void clearSubIdList(int i) {
            ArrayList<Integer> arrayList = this.mSlotIndexToSubIds.get(Integer.valueOf(i));
            if (arrayList != null) {
                arrayList.clear();
                SubscriptionController.invalidateDefaultSubIdCaches();
                SubscriptionController.invalidateSlotIndexCaches();
            }
        }
    }

    /* loaded from: classes.dex */
    public static class WatchedInt {
        private int mValue;

        public WatchedInt(int i) {
            this.mValue = i;
        }

        public int get() {
            return this.mValue;
        }

        public void set(int i) {
            this.mValue = i;
        }
    }

    public static SubscriptionController init(Context context) {
        SubscriptionController subscriptionController;
        synchronized (SubscriptionController.class) {
            if (sInstance == null) {
                sInstance = new SubscriptionController(context);
            } else {
                Log.wtf("SubscriptionController", "init() called multiple times!  sInstance = " + sInstance);
            }
            subscriptionController = sInstance;
        }
        return subscriptionController;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public static SubscriptionController getInstance() {
        if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
            throw new RuntimeException("getInstance should not be called.");
        }
        if (sInstance == null) {
            Log.wtf("SubscriptionController", "getInstance null");
        }
        return sInstance;
    }

    protected SubscriptionController(Context context) {
        internalInit(context);
        migrateImsSettings();
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected void internalInit(Context context) {
        this.mContext = context;
        this.mTelephonyManager = TelephonyManager.from(context);
        try {
            this.mUiccController = UiccController.getInstance();
            this.mAppOps = (AppOpsManager) this.mContext.getSystemService("appops");
            TelephonyServiceManager.ServiceRegisterer subscriptionServiceRegisterer = TelephonyFrameworkInitializer.getTelephonyServiceManager().getSubscriptionServiceRegisterer();
            if (subscriptionServiceRegisterer.get() == null) {
                subscriptionServiceRegisterer.register(this);
                this.mLastISubServiceRegTime = System.currentTimeMillis();
            }
            clearSlotIndexForSubInfoRecords();
            cacheSettingValues();
            invalidateDefaultSubIdCaches();
            invalidateDefaultDataSubIdCaches();
            invalidateDefaultSmsSubIdCaches();
            invalidateActiveDataSubIdCaches();
            invalidateSlotIndexCaches();
            this.mContext.getContentResolver().registerContentObserver(SubscriptionManager.SIM_INFO_SUW_RESTORE_CONTENT_URI, false, new ContentObserver(new Handler()) { // from class: com.android.internal.telephony.SubscriptionController.2
                @Override // android.database.ContentObserver
                public void onChange(boolean z, Uri uri) {
                    if (uri.equals(SubscriptionManager.SIM_INFO_SUW_RESTORE_CONTENT_URI)) {
                        SubscriptionController.this.refreshCachedActiveSubscriptionInfoList();
                        SubscriptionController.this.notifySubscriptionInfoChanged();
                        SubscriptionManager.from(SubscriptionController.this.mContext);
                        SubscriptionController subscriptionController = SubscriptionController.this;
                        for (SubscriptionInfo subscriptionInfo : subscriptionController.getActiveSubscriptionInfoList(subscriptionController.mContext.getOpPackageName(), SubscriptionController.this.mContext.getAttributionTag())) {
                            if (SubscriptionController.getInstance().isActiveSubId(subscriptionInfo.getSubscriptionId())) {
                                ImsManager.getInstance(SubscriptionController.this.mContext, subscriptionInfo.getSimSlotIndex()).updateImsServiceConfig();
                            }
                        }
                    }
                }
            });
            SubscriptionManager.invalidateSubscriptionManagerServiceEnabledCaches();
        } catch (RuntimeException unused) {
            throw new RuntimeException("UiccController has to be initialised before SubscriptionController init");
        }
    }

    public void notifySubInfoReady() {
        sendDefaultChangedBroadcast(SubscriptionManager.getDefaultSubscriptionId());
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private boolean isSubInfoReady() {
        return SubscriptionInfoUpdater.isSubInfoInitialized();
    }

    private void clearSlotIndexForSubInfoRecords() {
        if (this.mContext == null) {
            logel("[clearSlotIndexForSubInfoRecords] TelephonyManager or mContext is null");
            return;
        }
        ContentValues contentValues = new ContentValues(1);
        contentValues.put("sim_id", (Integer) (-1));
        this.mContext.getContentResolver().update(SubscriptionManager.CONTENT_URI, contentValues, null, null);
    }

    private void cacheSettingValues() {
        Settings.Global.getInt(this.mContext.getContentResolver(), "multi_sim_sms", -1);
        Settings.Global.getInt(this.mContext.getContentResolver(), "multi_sim_voice_call", -1);
        Settings.Global.getInt(this.mContext.getContentResolver(), "multi_sim_data_call", -1);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected void enforceModifyPhoneState(String str) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MODIFY_PHONE_STATE", str);
    }

    private void enforceReadPrivilegedPhoneState(String str) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.READ_PRIVILEGED_PHONE_STATE", str);
    }

    private void enforceManageSubscriptionUserAssociation(String str) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_SUBSCRIPTION_USER_ASSOCIATION", str);
    }

    private boolean hasSubscriberIdentifierAccess(int i, String str, String str2, String str3, boolean z) {
        try {
            return TelephonyPermissions.checkCallingOrSelfReadSubscriberIdentifiers(this.mContext, i, str, str2, str3, z);
        } catch (SecurityException unused) {
            return false;
        }
    }

    private boolean hasPhoneNumberAccess(int i, String str, String str2, String str3) {
        try {
            return TelephonyPermissions.checkCallingOrSelfReadPhoneNumber(this.mContext, i, str, str2, str3);
        } catch (SecurityException unused) {
            return false;
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void notifySubscriptionInfoChanged() {
        ArrayList arrayList;
        ((TelephonyRegistryManager) this.mContext.getSystemService("telephony_registry")).notifySubscriptionInfoChanged();
        MultiSimSettingController.getInstance().notifySubscriptionInfoChanged();
        TelephonyMetrics telephonyMetrics = TelephonyMetrics.getInstance();
        synchronized (this.mSubInfoListLock) {
            arrayList = new ArrayList(this.mCacheActiveSubInfoList);
        }
        if (this.mOpptSubInfoListChangedDirtyBit.getAndSet(false)) {
            notifyOpportunisticSubscriptionInfoChanged();
        }
        telephonyMetrics.updateActiveSubscriptionInfoList(arrayList);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private SubscriptionInfo getSubInfoRecord(Cursor cursor) {
        SubscriptionInfo.Builder builder = new SubscriptionInfo.Builder();
        int i = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
        builder.setId(i).setIccId(cursor.getString(cursor.getColumnIndexOrThrow("icc_id"))).setSimSlotIndex(cursor.getInt(cursor.getColumnIndexOrThrow("sim_id"))).setDisplayName(cursor.getString(cursor.getColumnIndexOrThrow("display_name"))).setCarrierName(cursor.getString(cursor.getColumnIndexOrThrow("carrier_name"))).setDisplayNameSource(cursor.getInt(cursor.getColumnIndexOrThrow("name_source"))).setIconTint(cursor.getInt(cursor.getColumnIndexOrThrow("color"))).setDataRoaming(cursor.getInt(cursor.getColumnIndexOrThrow("data_roaming"))).setMcc(cursor.getString(cursor.getColumnIndexOrThrow("mcc_string"))).setMnc(cursor.getString(cursor.getColumnIndexOrThrow("mnc_string")));
        String string = cursor.getString(cursor.getColumnIndexOrThrow("ehplmns"));
        String string2 = cursor.getString(cursor.getColumnIndexOrThrow("hplmns"));
        builder.setEhplmns(string == null ? null : string.split(",")).setHplmns(string2 == null ? null : string2.split(","));
        String string3 = cursor.getString(cursor.getColumnIndexOrThrow("card_id"));
        builder.setCardString(string3);
        builder.setCardId(this.mUiccController.convertToPublicCardId(string3));
        builder.setCountryIso(cursor.getString(cursor.getColumnIndexOrThrow("iso_country_code"))).setCarrierId(cursor.getInt(cursor.getColumnIndexOrThrow("carrier_id")));
        boolean z = cursor.getInt(cursor.getColumnIndexOrThrow("is_embedded")) == 1;
        builder.setEmbedded(z);
        if (z) {
            builder.setNativeAccessRules(UiccAccessRule.decodeRules(cursor.getBlob(cursor.getColumnIndexOrThrow("access_rules"))));
        }
        builder.setCarrierConfigAccessRules(UiccAccessRule.decodeRules(cursor.getBlob(cursor.getColumnIndexOrThrow("access_rules_from_carrier_configs")))).setOpportunistic(cursor.getInt(cursor.getColumnIndexOrThrow("is_opportunistic")) == 1).setGroupUuid(cursor.getString(cursor.getColumnIndexOrThrow("group_uuid"))).setProfileClass(cursor.getInt(cursor.getColumnIndexOrThrow("profile_class"))).setPortIndex(cursor.getInt(cursor.getColumnIndexOrThrow("port_index"))).setType(cursor.getInt(cursor.getColumnIndexOrThrow("subscription_type"))).setGroupOwner(getOptionalStringFromCursor(cursor, "group_owner", null)).setUiccApplicationsEnabled(cursor.getInt(cursor.getColumnIndexOrThrow("uicc_applications_enabled")) == 1).setUsageSetting(cursor.getInt(cursor.getColumnIndexOrThrow("usage_setting")));
        String string4 = cursor.getString(cursor.getColumnIndexOrThrow(IccProvider.STR_NUMBER));
        String line1Number = this.mTelephonyManager.getLine1Number(i);
        if (!TextUtils.isEmpty(line1Number) && !line1Number.equals(string4)) {
            string4 = line1Number;
        }
        builder.setNumber(string4);
        return builder.build();
    }

    private String getOptionalStringFromCursor(Cursor cursor, String str, String str2) {
        int columnIndex = cursor.getColumnIndex(str);
        return columnIndex == -1 ? str2 : cursor.getString(columnIndex);
    }

    public SubscriptionInfo getSubInfoForIccId(String str) {
        List<SubscriptionInfo> subInfo = getSubInfo("icc_id='" + str + "'", null);
        if (subInfo == null || subInfo.size() == 0) {
            return null;
        }
        return subInfo.get(0);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public List<SubscriptionInfo> getSubInfo(String str, Object obj) {
        if (VDBG) {
            logd("selection:" + str + ", querykey: " + obj);
        }
        ArrayList arrayList = null;
        Cursor query = this.mContext.getContentResolver().query(SubscriptionManager.CONTENT_URI, null, str, obj != null ? new String[]{obj.toString()} : null, null);
        if (query != null) {
            while (query.moveToNext()) {
                try {
                    SubscriptionInfo subInfoRecord = getSubInfoRecord(query);
                    if (subInfoRecord != null) {
                        if (arrayList == null) {
                            arrayList = new ArrayList();
                        }
                        arrayList.add(subInfoRecord);
                    }
                } finally {
                    query.close();
                }
            }
        }
        if (query != null) {
        }
        return arrayList;
    }

    private int getUnusedColor(String str, String str2) {
        List<SubscriptionInfo> activeSubscriptionInfoList = getActiveSubscriptionInfoList(str, str2);
        this.colorArr = this.mContext.getResources().getIntArray(17236200);
        int i = 0;
        if (activeSubscriptionInfoList != null) {
            for (int i2 = 0; i2 < this.colorArr.length; i2++) {
                int i3 = 0;
                while (i3 < activeSubscriptionInfoList.size() && this.colorArr[i2] != activeSubscriptionInfoList.get(i3).getIconTint()) {
                    i3++;
                }
                if (i3 == activeSubscriptionInfoList.size()) {
                    return this.colorArr[i2];
                }
            }
            i = activeSubscriptionInfoList.size() % this.colorArr.length;
        }
        return this.colorArr[i];
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    @Deprecated
    public SubscriptionInfo getActiveSubscriptionInfo(int i, String str) {
        return getActiveSubscriptionInfo(i, str, null);
    }

    public SubscriptionInfo getActiveSubscriptionInfo(int i, String str, String str2) {
        if (TelephonyPermissions.checkCallingOrSelfReadPhoneState(this.mContext, i, str, str2, "getActiveSubscriptionInfo")) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                List<SubscriptionInfo> activeSubscriptionInfoList = getActiveSubscriptionInfoList(this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
                if (activeSubscriptionInfoList != null) {
                    for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
                        if (subscriptionInfo.getSubscriptionId() == i) {
                            if (VDBG) {
                                logd("[getActiveSubscriptionInfo]+ subId=" + i + " subInfo=" + subscriptionInfo);
                            }
                            return conditionallyRemoveIdentifiers(subscriptionInfo, str, str2, "getActiveSubscriptionInfo");
                        }
                    }
                }
                return null;
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
        return null;
    }

    public SubscriptionInfo getSubscriptionInfo(int i) {
        synchronized (this.mSubInfoListLock) {
            for (SubscriptionInfo subscriptionInfo : this.mCacheActiveSubInfoList) {
                if (subscriptionInfo.getSubscriptionId() == i) {
                    return subscriptionInfo;
                }
            }
            for (SubscriptionInfo subscriptionInfo2 : this.mCacheOpportunisticSubInfoList) {
                if (subscriptionInfo2.getSubscriptionId() == i) {
                    return subscriptionInfo2;
                }
            }
            List<SubscriptionInfo> subInfo = getSubInfo("_id=" + i, null);
            if (subInfo == null || subInfo.isEmpty()) {
                return null;
            }
            return subInfo.get(0);
        }
    }

    public SubscriptionInfo getActiveSubscriptionInfoForIccId(String str, String str2, String str3) {
        enforceReadPrivilegedPhoneState("getActiveSubscriptionInfoForIccId");
        return getActiveSubscriptionInfoForIccIdInternal(str);
    }

    private SubscriptionInfo getActiveSubscriptionInfoForIccIdInternal(String str) {
        if (str == null) {
            return null;
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            List<SubscriptionInfo> activeSubscriptionInfoList = getActiveSubscriptionInfoList(this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
            if (activeSubscriptionInfoList != null) {
                for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
                    if (str.equals(subscriptionInfo.getIccId())) {
                        return subscriptionInfo;
                    }
                }
            }
            return null;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public SubscriptionInfo getActiveSubscriptionInfoForSimSlotIndex(int i, String str, String str2) {
        Phone phone = PhoneFactory.getPhone(i);
        if (phone != null && TelephonyPermissions.checkCallingOrSelfReadPhoneState(this.mContext, phone.getSubId(), str, str2, "getActiveSubscriptionInfoForSimSlotIndex")) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                List<SubscriptionInfo> activeSubscriptionInfoList = getActiveSubscriptionInfoList(this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
                if (activeSubscriptionInfoList != null) {
                    for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
                        if (subscriptionInfo.getSimSlotIndex() == i) {
                            logd("[getActiveSubscriptionInfoForSimSlotIndex]+ slotIndex=" + i + " subId=" + conditionallyRemoveIdentifiers(subscriptionInfo, false, false));
                            return conditionallyRemoveIdentifiers(subscriptionInfo, str, str2, "getActiveSubscriptionInfoForSimSlotIndex");
                        }
                    }
                }
                return null;
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
        return null;
    }

    public List<SubscriptionInfo> getAllSubInfoList(String str, String str2) {
        return getAllSubInfoList(str, str2, false);
    }

    public List<SubscriptionInfo> getAllSubInfoList(final String str, final String str2, boolean z) {
        boolean z2 = VDBG;
        if (z2) {
            logd("[getAllSubInfoList]+");
        }
        if (TelephonyPermissions.checkCallingOrSelfReadPhoneState(this.mContext, -1, str, str2, "getAllSubInfoList")) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                List<SubscriptionInfo> subInfo = getSubInfo(null, null);
                if (subInfo == null || z) {
                    if (z2) {
                        logd("[getAllSubInfoList]- no info return");
                        return subInfo;
                    }
                    return subInfo;
                }
                if (z2) {
                    logd("[getAllSubInfoList]- " + subInfo.size() + " infos return");
                }
                return (List) subInfo.stream().map(new Function() { // from class: com.android.internal.telephony.SubscriptionController$$ExternalSyntheticLambda0
                    @Override // java.util.function.Function
                    public final Object apply(Object obj) {
                        SubscriptionInfo lambda$getAllSubInfoList$1;
                        lambda$getAllSubInfoList$1 = SubscriptionController.this.lambda$getAllSubInfoList$1(str, str2, (SubscriptionInfo) obj);
                        return lambda$getAllSubInfoList$1;
                    }
                }).collect(Collectors.toList());
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ SubscriptionInfo lambda$getAllSubInfoList$1(String str, String str2, SubscriptionInfo subscriptionInfo) {
        return conditionallyRemoveIdentifiers(subscriptionInfo, str, str2, "getAllSubInfoList");
    }

    private List<SubscriptionInfo> makeCacheListCopyWithLock(List<SubscriptionInfo> list) {
        ArrayList arrayList;
        synchronized (this.mSubInfoListLock) {
            arrayList = new ArrayList(list);
        }
        return arrayList;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    @Deprecated
    public List<SubscriptionInfo> getActiveSubscriptionInfoList(String str) {
        return getSubscriptionInfoListFromCacheHelper(str, null, makeCacheListCopyWithLock(this.mCacheActiveSubInfoList));
    }

    public List<SubscriptionInfo> getActiveSubscriptionInfoList(String str, String str2) {
        return getSubscriptionInfoListFromCacheHelper(str, str2, makeCacheListCopyWithLock(this.mCacheActiveSubInfoList));
    }

    @VisibleForTesting
    public void refreshCachedActiveSubscriptionInfoList() {
        List<SubscriptionInfo> subInfo = getSubInfo("sim_id>=0 OR subscription_type=1", null);
        synchronized (this.mSubInfoListLock) {
            if (subInfo != null) {
                if (this.mCacheActiveSubInfoList.size() != subInfo.size() || !this.mCacheActiveSubInfoList.containsAll(subInfo)) {
                    logdl("Active subscription info list changed. " + subInfo);
                }
                this.mCacheActiveSubInfoList.clear();
                subInfo.sort(SUBSCRIPTION_INFO_COMPARATOR);
                this.mCacheActiveSubInfoList.addAll(subInfo);
            } else {
                logd("activeSubscriptionInfoList is null.");
                this.mCacheActiveSubInfoList.clear();
            }
        }
        refreshCachedOpportunisticSubscriptionInfoList();
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    @Deprecated
    public int getActiveSubInfoCount(String str) {
        return getActiveSubInfoCount(str, null);
    }

    public int getActiveSubInfoCount(String str, String str2) {
        List<SubscriptionInfo> activeSubscriptionInfoList = getActiveSubscriptionInfoList(str, str2);
        if (activeSubscriptionInfoList == null) {
            if (VDBG) {
                logd("[getActiveSubInfoCount] records null");
                return 0;
            }
            return 0;
        }
        if (VDBG) {
            logd("[getActiveSubInfoCount]- count: " + activeSubscriptionInfoList.size());
        }
        return activeSubscriptionInfoList.size();
    }

    public int getAllSubInfoCount(String str, String str2) {
        if (TelephonyPermissions.checkCallingOrSelfReadPhoneState(this.mContext, -1, str, str2, "getAllSubInfoCount")) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                Cursor query = this.mContext.getContentResolver().query(SubscriptionManager.CONTENT_URI, null, null, null, null);
                if (query != null) {
                    int count = query.getCount();
                    query.close();
                    return count;
                }
                if (query != null) {
                    query.close();
                }
                return 0;
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
        return 0;
    }

    public int getActiveSubInfoCountMax() {
        return this.mTelephonyManager.getSimCount();
    }

    public List<SubscriptionInfo> getAvailableSubscriptionInfoList(String str, String str2) {
        List<String> iccIdsOfInsertedPhysicalSims;
        try {
            try {
                enforceReadPrivilegedPhoneState("getAvailableSubscriptionInfoList");
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    String str3 = "sim_id>=0 OR subscription_type=1";
                    if (((EuiccManager) this.mContext.getSystemService("euicc")).isEnabled()) {
                        str3 = "sim_id>=0 OR subscription_type=1 OR is_embedded=1";
                    }
                    if (!getIccIdsOfInsertedPhysicalSims().isEmpty()) {
                        str3 = str3 + " OR (" + getSelectionForIccIdList((String[]) iccIdsOfInsertedPhysicalSims.toArray(new String[0])) + ")";
                    }
                    List<SubscriptionInfo> subInfo = getSubInfo(str3, null);
                    if (subInfo != null) {
                        subInfo.sort(SUBSCRIPTION_INFO_COMPARATOR);
                        if (VDBG) {
                            logdl("[getAvailableSubInfoList]- " + subInfo.size() + " infos return");
                        }
                    }
                    return subInfo;
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            } catch (SecurityException unused) {
                throw new SecurityException("Need READ_PRIVILEGED_PHONE_STATE to call  getAvailableSubscriptionInfoList");
            }
        } catch (SecurityException unused2) {
            this.mContext.enforceCallingOrSelfPermission("android.permission.READ_PHONE_STATE", null);
            EventLog.writeEvent(1397638484, "185235454", Integer.valueOf(Binder.getCallingUid()));
            throw new SecurityException("Need READ_PRIVILEGED_PHONE_STATE to call  getAvailableSubscriptionInfoList");
        }
    }

    private List<String> getIccIdsOfInsertedPhysicalSims() {
        ArrayList arrayList = new ArrayList();
        UiccSlot[] uiccSlots = UiccController.getInstance().getUiccSlots();
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

    public List<SubscriptionInfo> getAccessibleSubscriptionInfoList(final String str) {
        if (((EuiccManager) this.mContext.getSystemService("euicc")).isEnabled()) {
            this.mAppOps.checkPackage(Binder.getCallingUid(), str);
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                List<SubscriptionInfo> subInfo = getSubInfo("is_embedded=1", null);
                if (subInfo == null) {
                    return null;
                }
                List<SubscriptionInfo> list = (List) subInfo.stream().filter(new Predicate() { // from class: com.android.internal.telephony.SubscriptionController$$ExternalSyntheticLambda11
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$getAccessibleSubscriptionInfoList$2;
                        lambda$getAccessibleSubscriptionInfoList$2 = SubscriptionController.this.lambda$getAccessibleSubscriptionInfoList$2(str, (SubscriptionInfo) obj);
                        return lambda$getAccessibleSubscriptionInfoList$2;
                    }
                }).sorted(SUBSCRIPTION_INFO_COMPARATOR).collect(Collectors.toList());
                if (VDBG) {
                    logdl("[getAccessibleSubInfoList] " + list.size() + " infos returned");
                }
                return list;
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$getAccessibleSubscriptionInfoList$2(String str, SubscriptionInfo subscriptionInfo) {
        return subscriptionInfo.canManageSubscription(this.mContext, str);
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    public List<SubscriptionInfo> getSubscriptionInfoListForEmbeddedSubscriptionUpdate(String[] strArr, boolean z) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append("is_embedded");
        sb.append("=1");
        if (z) {
            sb.append(" AND ");
            sb.append("is_removable");
            sb.append("=1");
        }
        sb.append(") OR ");
        sb.append("icc_id");
        sb.append(" IN (");
        for (int i = 0; i < strArr.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append("'");
            sb.append(strArr[i]);
            sb.append("'");
        }
        sb.append(")");
        List<SubscriptionInfo> subInfo = getSubInfo(sb.toString(), null);
        return subInfo == null ? Collections.emptyList() : subInfo;
    }

    public void requestEmbeddedSubscriptionInfoListRefresh(int i) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.WRITE_EMBEDDED_SUBSCRIPTIONS", "requestEmbeddedSubscriptionInfoListRefresh");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            PhoneFactory.requestEmbeddedSubscriptionInfoListRefresh(i, null);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void requestEmbeddedSubscriptionInfoListRefresh(int i, Runnable runnable) {
        PhoneFactory.requestEmbeddedSubscriptionInfoListRefresh(i, runnable);
    }

    public void requestEmbeddedSubscriptionInfoListRefresh(Runnable runnable) {
        PhoneFactory.requestEmbeddedSubscriptionInfoListRefresh(this.mTelephonyManager.getCardIdForDefaultEuicc(), runnable);
    }

    /* JADX WARN: Removed duplicated region for block: B:108:0x027b A[Catch: all -> 0x027f, TRY_ENTER, TryCatch #4 {all -> 0x027f, blocks: (B:60:0x0175, B:61:0x0178, B:63:0x018a, B:65:0x01a2, B:90:0x021b, B:91:0x021e, B:93:0x0227, B:94:0x022b, B:99:0x023c, B:103:0x025e, B:102:0x0249, B:108:0x027b, B:109:0x027e, B:67:0x01af, B:69:0x01b5, B:71:0x01c5, B:73:0x01d3, B:76:0x01db, B:80:0x01fd, B:78:0x01e4, B:81:0x0207, B:82:0x020a), top: B:120:0x00b6 }] */
    /* JADX WARN: Removed duplicated region for block: B:121:0x0219 A[EDGE_INSN: B:121:0x0219->B:89:0x0219 ?: BREAK  , SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:26:0x00cd  */
    /* JADX WARN: Removed duplicated region for block: B:29:0x00e2  */
    /* JADX WARN: Removed duplicated region for block: B:60:0x0175 A[Catch: all -> 0x027f, TRY_ENTER, TryCatch #4 {all -> 0x027f, blocks: (B:60:0x0175, B:61:0x0178, B:63:0x018a, B:65:0x01a2, B:90:0x021b, B:91:0x021e, B:93:0x0227, B:94:0x022b, B:99:0x023c, B:103:0x025e, B:102:0x0249, B:108:0x027b, B:109:0x027e, B:67:0x01af, B:69:0x01b5, B:71:0x01c5, B:73:0x01d3, B:76:0x01db, B:80:0x01fd, B:78:0x01e4, B:81:0x0207, B:82:0x020a), top: B:120:0x00b6 }] */
    /* JADX WARN: Removed duplicated region for block: B:63:0x018a A[Catch: all -> 0x027f, TryCatch #4 {all -> 0x027f, blocks: (B:60:0x0175, B:61:0x0178, B:63:0x018a, B:65:0x01a2, B:90:0x021b, B:91:0x021e, B:93:0x0227, B:94:0x022b, B:99:0x023c, B:103:0x025e, B:102:0x0249, B:108:0x027b, B:109:0x027e, B:67:0x01af, B:69:0x01b5, B:71:0x01c5, B:73:0x01d3, B:76:0x01db, B:80:0x01fd, B:78:0x01e4, B:81:0x0207, B:82:0x020a), top: B:120:0x00b6 }] */
    /* JADX WARN: Removed duplicated region for block: B:64:0x019d  */
    /* JADX WARN: Removed duplicated region for block: B:67:0x01af A[Catch: all -> 0x0214, TRY_ENTER, TryCatch #4 {all -> 0x027f, blocks: (B:60:0x0175, B:61:0x0178, B:63:0x018a, B:65:0x01a2, B:90:0x021b, B:91:0x021e, B:93:0x0227, B:94:0x022b, B:99:0x023c, B:103:0x025e, B:102:0x0249, B:108:0x027b, B:109:0x027e, B:67:0x01af, B:69:0x01b5, B:71:0x01c5, B:73:0x01d3, B:76:0x01db, B:80:0x01fd, B:78:0x01e4, B:81:0x0207, B:82:0x020a), top: B:120:0x00b6 }] */
    /* JADX WARN: Removed duplicated region for block: B:71:0x01c5 A[Catch: all -> 0x0214, TryCatch #4 {all -> 0x027f, blocks: (B:60:0x0175, B:61:0x0178, B:63:0x018a, B:65:0x01a2, B:90:0x021b, B:91:0x021e, B:93:0x0227, B:94:0x022b, B:99:0x023c, B:103:0x025e, B:102:0x0249, B:108:0x027b, B:109:0x027e, B:67:0x01af, B:69:0x01b5, B:71:0x01c5, B:73:0x01d3, B:76:0x01db, B:80:0x01fd, B:78:0x01e4, B:81:0x0207, B:82:0x020a), top: B:120:0x00b6 }] */
    /* JADX WARN: Removed duplicated region for block: B:85:0x0211 A[LOOP:0: B:69:0x01b5->B:85:0x0211, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:90:0x021b A[Catch: all -> 0x027f, TryCatch #4 {all -> 0x027f, blocks: (B:60:0x0175, B:61:0x0178, B:63:0x018a, B:65:0x01a2, B:90:0x021b, B:91:0x021e, B:93:0x0227, B:94:0x022b, B:99:0x023c, B:103:0x025e, B:102:0x0249, B:108:0x027b, B:109:0x027e, B:67:0x01af, B:69:0x01b5, B:71:0x01c5, B:73:0x01d3, B:76:0x01db, B:80:0x01fd, B:78:0x01e4, B:81:0x0207, B:82:0x020a), top: B:120:0x00b6 }] */
    /* JADX WARN: Removed duplicated region for block: B:93:0x0227 A[Catch: all -> 0x027f, TryCatch #4 {all -> 0x027f, blocks: (B:60:0x0175, B:61:0x0178, B:63:0x018a, B:65:0x01a2, B:90:0x021b, B:91:0x021e, B:93:0x0227, B:94:0x022b, B:99:0x023c, B:103:0x025e, B:102:0x0249, B:108:0x027b, B:109:0x027e, B:67:0x01af, B:69:0x01b5, B:71:0x01c5, B:73:0x01d3, B:76:0x01db, B:80:0x01fd, B:78:0x01e4, B:81:0x0207, B:82:0x020a), top: B:120:0x00b6 }] */
    /* JADX WARN: Removed duplicated region for block: B:94:0x022b A[Catch: all -> 0x027f, TRY_LEAVE, TryCatch #4 {all -> 0x027f, blocks: (B:60:0x0175, B:61:0x0178, B:63:0x018a, B:65:0x01a2, B:90:0x021b, B:91:0x021e, B:93:0x0227, B:94:0x022b, B:99:0x023c, B:103:0x025e, B:102:0x0249, B:108:0x027b, B:109:0x027e, B:67:0x01af, B:69:0x01b5, B:71:0x01c5, B:73:0x01d3, B:76:0x01db, B:80:0x01fd, B:78:0x01e4, B:81:0x0207, B:82:0x020a), top: B:120:0x00b6 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public int addSubInfo(String str, String str2, int i, int i2) {
        ContentResolver contentResolver;
        String str3;
        String[] strArr;
        Cursor query;
        boolean z;
        long j;
        String str4;
        int portIndexFromIccId;
        String cardId;
        boolean z2;
        int i3;
        String str5;
        String[] strArr2;
        Cursor query2;
        int i4;
        int i5 = i;
        enforceModifyPhoneState("addSubInfo");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        if (str == null) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return -1;
        }
        try {
            contentResolver = this.mContext.getContentResolver();
            if (!isSubscriptionForRemoteSim(i2)) {
                str3 = "icc_id=? OR icc_id=?";
                strArr = new String[]{str, IccUtils.getDecimalSubstring(str)};
            } else if (!this.mContext.getPackageManager().hasSystemFeature("android.hardware.type.automotive")) {
                logel("[addSubInfo] Remote SIM can only be added when FEATURE_AUTOMOTIVE is supported");
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return -1;
            } else {
                str3 = "icc_id=? AND subscription_type=?";
                strArr = new String[]{str, Integer.toString(i2)};
            }
            query = contentResolver.query(SubscriptionManager.CONTENT_URI, new String[]{"_id", "sim_id", "name_source", "icc_id", "card_id", "port_index"}, str3, strArr, null);
        } catch (Throwable th) {
            th = th;
        }
        try {
            if (query != null) {
                try {
                    if (query.moveToFirst()) {
                        z = false;
                        if (isSubscriptionForRemoteSim(i2)) {
                            if (z) {
                                insertEmptySubInfoRecord(str, i5);
                                str4 = "_id";
                                j = clearCallingIdentity;
                                z2 = true;
                                if (query != null) {
                                    query.close();
                                }
                                String[] strArr3 = {String.valueOf(i5)};
                                if (isSubscriptionForRemoteSim(i2)) {
                                    i3 = 1;
                                    str5 = "icc_id=? AND subscription_type=?";
                                    strArr2 = new String[]{str, Integer.toString(i2)};
                                } else {
                                    i3 = 1;
                                    str5 = "sim_id=?";
                                    strArr2 = strArr3;
                                }
                                int i6 = i3;
                                query2 = contentResolver.query(SubscriptionManager.CONTENT_URI, null, str5, strArr2, null);
                                if (query2 != null && query2.moveToFirst()) {
                                    while (true) {
                                        String str6 = str4;
                                        i4 = query2.getInt(query2.getColumnIndexOrThrow(str6));
                                        if (addToSubIdList(i5, i4, i2)) {
                                            int activeSubInfoCountMax = getActiveSubInfoCountMax();
                                            int defaultSubId = getDefaultSubId();
                                            if (isSubscriptionForRemoteSim(i2)) {
                                                updateDefaultSubIdsIfNeeded(i4, i2);
                                            } else {
                                                if (!SubscriptionManager.isValidSubscriptionId(defaultSubId) || activeSubInfoCountMax == i6 || this.mDefaultFallbackSubId.get() == -1) {
                                                    logdl("setting default fallback subid to " + i4);
                                                    setDefaultFallbackSubId(i4, i2);
                                                }
                                                if (activeSubInfoCountMax == i6) {
                                                    setDefaultDataSubId(i4);
                                                    setDefaultSmsSubId(i4);
                                                    setDefaultVoiceSubId(i4);
                                                }
                                            }
                                        }
                                        if (query2.moveToNext()) {
                                            break;
                                        }
                                        str4 = str6;
                                    }
                                }
                                if (query2 != null) {
                                    query2.close();
                                }
                                refreshCachedActiveSubscriptionInfoList();
                                if (isSubscriptionForRemoteSim(i2)) {
                                    notifySubscriptionInfoChanged();
                                } else {
                                    int subId = getSubId(i5);
                                    if (!SubscriptionManager.isValidSubscriptionId(subId)) {
                                        Binder.restoreCallingIdentity(j);
                                        return -1;
                                    } else if (z2) {
                                        String simOperatorName = this.mTelephonyManager.getSimOperatorName(subId);
                                        if (TextUtils.isEmpty(simOperatorName)) {
                                            Resources system = Resources.getSystem();
                                            Object[] objArr = new Object[i6];
                                            objArr[0] = Integer.valueOf(i5 + i6);
                                            simOperatorName = system.getString(17040113, objArr);
                                        }
                                        ContentValues contentValues = new ContentValues();
                                        contentValues.put("display_name", simOperatorName);
                                        contentResolver.update(SubscriptionManager.getUriForSubscriptionId(subId), contentValues, null, null);
                                        refreshCachedActiveSubscriptionInfoList();
                                    }
                                }
                                Binder.restoreCallingIdentity(j);
                                return 0;
                            }
                            int i7 = query.getInt(0);
                            int i8 = query.getInt(1);
                            query.getInt(2);
                            String string = query.getString(3);
                            j = clearCallingIdentity;
                            try {
                                String string2 = query.getString(4);
                                int i9 = query.getInt(5);
                                str4 = "_id";
                                ContentValues contentValues2 = new ContentValues();
                                if (i5 != i8) {
                                    contentValues2.put("sim_id", Integer.valueOf(i));
                                }
                                if (string != null && string.length() < str.length() && string.equals(IccUtils.getDecimalSubstring(str))) {
                                    contentValues2.put("icc_id", str);
                                }
                                UiccCard uiccCardForPhone = this.mUiccController.getUiccCardForPhone(i5);
                                if (uiccCardForPhone != null && (cardId = uiccCardForPhone.getCardId()) != null && cardId != string2) {
                                    contentValues2.put("card_id", cardId);
                                }
                                UiccSlot uiccSlotForPhone = this.mUiccController.getUiccSlotForPhone(i5);
                                if (uiccSlotForPhone != null && !uiccSlotForPhone.isEuicc() && (portIndexFromIccId = uiccSlotForPhone.getPortIndexFromIccId(str)) != i9) {
                                    contentValues2.put("port_index", Integer.valueOf(portIndexFromIccId));
                                }
                                if (contentValues2.size() > 0) {
                                    contentResolver.update(SubscriptionManager.getUriForSubscriptionId(i7), contentValues2, null, null);
                                }
                            } catch (Throwable th2) {
                                th = th2;
                                if (query != null) {
                                }
                                throw th;
                            }
                        } else if (z) {
                            insertEmptySubInfoRecord(str, str2, -1, i2);
                            str4 = "_id";
                            j = clearCallingIdentity;
                            i5 = -1;
                        } else {
                            str4 = "_id";
                            j = clearCallingIdentity;
                        }
                        z2 = false;
                        if (query != null) {
                        }
                        String[] strArr32 = {String.valueOf(i5)};
                        if (isSubscriptionForRemoteSim(i2)) {
                        }
                        int i62 = i3;
                        query2 = contentResolver.query(SubscriptionManager.CONTENT_URI, null, str5, strArr2, null);
                        if (query2 != null) {
                            while (true) {
                                String str62 = str4;
                                i4 = query2.getInt(query2.getColumnIndexOrThrow(str62));
                                if (addToSubIdList(i5, i4, i2)) {
                                }
                                if (query2.moveToNext()) {
                                }
                                str4 = str62;
                            }
                        }
                        if (query2 != null) {
                        }
                        refreshCachedActiveSubscriptionInfoList();
                        if (isSubscriptionForRemoteSim(i2)) {
                        }
                        Binder.restoreCallingIdentity(j);
                        return 0;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    if (query != null) {
                        query.close();
                    }
                    throw th;
                }
            }
            z = true;
            if (isSubscriptionForRemoteSim(i2)) {
            }
            z2 = false;
            if (query != null) {
            }
            String[] strArr322 = {String.valueOf(i5)};
            if (isSubscriptionForRemoteSim(i2)) {
            }
            int i622 = i3;
            query2 = contentResolver.query(SubscriptionManager.CONTENT_URI, null, str5, strArr2, null);
            if (query2 != null) {
            }
            if (query2 != null) {
            }
            refreshCachedActiveSubscriptionInfoList();
            if (isSubscriptionForRemoteSim(i2)) {
            }
            Binder.restoreCallingIdentity(j);
            return 0;
        } catch (Throwable th4) {
            th = th4;
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    private void updateDefaultSubIdsIfNeeded(int i, int i2) {
        if (!isActiveSubscriptionId(getDefaultSubId())) {
            setDefaultFallbackSubId(i, i2);
        }
        if (!isActiveSubscriptionId(getDefaultSmsSubId())) {
            setDefaultSmsSubId(i);
        }
        if (!isActiveSubscriptionId(getDefaultDataSubId())) {
            setDefaultDataSubId(i);
        }
        if (isActiveSubscriptionId(getDefaultVoiceSubId())) {
            return;
        }
        setDefaultVoiceSubId(i);
    }

    private boolean isActiveSubscriptionId(int i) {
        if (SubscriptionManager.isValidSubscriptionId(i)) {
            ArrayList<Integer> activeSubIdArrayList = getActiveSubIdArrayList();
            if (activeSubIdArrayList.isEmpty()) {
                return false;
            }
            return activeSubIdArrayList.contains(new Integer(i));
        }
        return false;
    }

    public int removeSubInfo(String str, int i) {
        int i2;
        int i3;
        enforceModifyPhoneState("removeSubInfo");
        synchronized (this.mSubInfoListLock) {
            Iterator<SubscriptionInfo> it = this.mCacheActiveSubInfoList.iterator();
            while (true) {
                if (!it.hasNext()) {
                    i2 = -1;
                    i3 = -1;
                    break;
                }
                SubscriptionInfo next = it.next();
                if (next.getSubscriptionType() == i && next.getIccId().equalsIgnoreCase(str)) {
                    i2 = next.getSubscriptionId();
                    i3 = next.getSimSlotIndex();
                    break;
                }
            }
        }
        if (i2 == -1) {
            return -1;
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            if (this.mContext.getContentResolver().delete(SubscriptionManager.CONTENT_URI, "_id=? AND subscription_type=?", new String[]{Integer.toString(i2), Integer.toString(i)}) != 1) {
                return -1;
            }
            refreshCachedActiveSubscriptionInfoList();
            int removeFromSubIdList = this.mSlotIndexToSubIds.removeFromSubIdList(i3, i2);
            if (removeFromSubIdList == -1) {
                loge("sSlotIndexToSubIds has no entry for slotIndex = " + i3);
            } else if (removeFromSubIdList == -2) {
                loge("sSlotIndexToSubIds has no subid: " + i2 + ", in index: " + i3);
            }
            List<SubscriptionInfo> activeSubscriptionInfoList = getActiveSubscriptionInfoList(this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
            SubscriptionInfo subscriptionInfo = !activeSubscriptionInfoList.isEmpty() ? activeSubscriptionInfoList.get(0) : null;
            updateDefaultSubIdsIfNeeded(subscriptionInfo.getSubscriptionId(), subscriptionInfo.getSubscriptionType());
            notifySubscriptionInfoChanged();
            return removeFromSubIdList;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void clearSubInfoRecord(int i) {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        boolean z = true;
        ContentValues contentValues = new ContentValues(1);
        contentValues.put("sim_id", (Integer) (-1));
        contentResolver.update(SubscriptionManager.CONTENT_URI, contentValues, "(sim_id=" + i + ")", null);
        refreshCachedActiveSubscriptionInfoList();
        z = (this.mDefaultFallbackSubId.get() <= -1 || this.mSlotIndexToSubIds.getCopy(i) == null || !this.mSlotIndexToSubIds.getCopy(i).contains(Integer.valueOf(this.mDefaultFallbackSubId.get()))) ? false : false;
        this.mSlotIndexToSubIds.remove(i);
        if (this.mSlotIndexToSubIds.size() == 0) {
            this.mDefaultFallbackSubId.set(-1);
        } else if (z) {
            for (int i2 = 0; i2 < getActiveSubIdArrayList().size(); i2++) {
                int intValue = getActiveSubIdArrayList().get(i2).intValue();
                if (intValue > -1) {
                    this.mDefaultFallbackSubId.set(intValue);
                    return;
                }
            }
        }
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    public Uri insertEmptySubInfoRecord(String str, int i) {
        if (getSubInfoForIccId(str) != null) {
            loge("insertEmptySubInfoRecord: Found existing record by ICCID. Do not create a new empty entry.");
            return null;
        }
        return insertEmptySubInfoRecord(str, null, i, 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Uri insertEmptySubInfoRecord(String str, String str2, int i, int i2) {
        String cardId;
        ContentResolver contentResolver = this.mContext.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put("icc_id", str);
        contentValues.put("color", Integer.valueOf(getUnusedColor(this.mContext.getOpPackageName(), this.mContext.getAttributionTag())));
        contentValues.put("sim_id", Integer.valueOf(i));
        contentValues.put("carrier_name", PhoneConfigurationManager.SSSS);
        contentValues.put("card_id", str);
        contentValues.put("subscription_type", Integer.valueOf(i2));
        if (!TextUtils.isEmpty(str2)) {
            contentValues.put("display_name", str2);
        }
        if (!isSubscriptionForRemoteSim(i2)) {
            UiccCard uiccCardForPhone = this.mUiccController.getUiccCardForPhone(i);
            if (uiccCardForPhone != null && (cardId = uiccCardForPhone.getCardId()) != null) {
                contentValues.put("card_id", cardId);
            }
            UiccSlot uiccSlotForPhone = this.mUiccController.getUiccSlotForPhone(i);
            if (uiccSlotForPhone != null) {
                contentValues.put("port_index", Integer.valueOf(uiccSlotForPhone.getPortIndexFromIccId(str)));
            }
        }
        contentValues.put("allowed_network_types_for_reasons", "user=" + RadioAccessFamily.getRafFromNetworkType(RILConstants.PREFERRED_NETWORK_MODE));
        contentValues.put("usage_setting", (Integer) (-1));
        Uri insert = contentResolver.insert(SubscriptionManager.CONTENT_URI, contentValues);
        refreshCachedActiveSubscriptionInfoList();
        return insert;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public boolean setPlmnSpn(int i, boolean z, String str, boolean z2, String str2) {
        synchronized (this.mLock) {
            int subId = getSubId(i);
            if (this.mContext.getPackageManager().resolveContentProvider(SubscriptionManager.CONTENT_URI.getAuthority(), 0) != null && SubscriptionManager.isValidSubscriptionId(subId)) {
                if (!z) {
                    str = z2 ? str2 : PhoneConfigurationManager.SSSS;
                } else if (z2 && !Objects.equals(str2, str)) {
                    str = str + this.mContext.getString(17040559).toString() + str2;
                }
                setCarrierText(str, subId);
                return true;
            }
            notifySubscriptionInfoChanged();
            return false;
        }
    }

    private int setCarrierText(String str, int i) {
        int i2;
        enforceModifyPhoneState("setCarrierText");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            SubscriptionInfo subscriptionInfo = getSubscriptionInfo(i);
            if (subscriptionInfo != null ? !TextUtils.equals(str, subscriptionInfo.getCarrierName()) : true) {
                ContentValues contentValues = new ContentValues(1);
                contentValues.put("carrier_name", str);
                i2 = this.mContext.getContentResolver().update(SubscriptionManager.getUriForSubscriptionId(i), contentValues, null, null);
                refreshCachedActiveSubscriptionInfoList();
                notifySubscriptionInfoChanged();
            } else {
                i2 = 0;
            }
            return i2;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public int setIconTint(int i, int i2) {
        enforceModifyPhoneState("setIconTint");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            validateSubId(i);
            ContentValues contentValues = new ContentValues(1);
            contentValues.put("color", Integer.valueOf(i2));
            int update = this.mContext.getContentResolver().update(SubscriptionManager.getUriForSubscriptionId(i), contentValues, null, null);
            refreshCachedActiveSubscriptionInfoList();
            notifySubscriptionInfoChanged();
            return update;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public static int getNameSourcePriority(int i) {
        int indexOf = Arrays.asList(0, 4, 1, 3, 2).indexOf(Integer.valueOf(i));
        if (indexOf < 0) {
            return 0;
        }
        return indexOf;
    }

    @VisibleForTesting
    public boolean isExistingNameSourceStillValid(SubscriptionInfo subscriptionInfo) {
        int displayNameSource;
        PersistableBundle configForSubId;
        int subscriptionId = subscriptionInfo.getSubscriptionId();
        int phoneId = getPhoneId(subscriptionInfo.getSubscriptionId());
        Phone phone = PhoneFactory.getPhone(phoneId);
        if (phone != null && (displayNameSource = subscriptionInfo.getDisplayNameSource()) != 0) {
            if (displayNameSource == 1) {
                return !TextUtils.isEmpty(getServiceProviderName(phoneId));
            }
            if (displayNameSource != 2) {
                if (displayNameSource != 3) {
                    if (displayNameSource != 4) {
                        return false;
                    }
                    return !TextUtils.isEmpty(phone.getPlmn());
                } else if (subscriptionInfo.isEmbedded() || (configForSubId = ((CarrierConfigManager) this.mContext.getSystemService(CarrierConfigManager.class)).getConfigForSubId(subscriptionId)) == null) {
                    return true;
                } else {
                    boolean z = configForSubId.getBoolean("carrier_name_override_bool", false);
                    String string = configForSubId.getString("carrier_name_string");
                    String serviceProviderName = getServiceProviderName(phoneId);
                    if (z) {
                        return true;
                    }
                    return TextUtils.isEmpty(serviceProviderName) && !TextUtils.isEmpty(string);
                }
            }
        }
        return true;
    }

    @VisibleForTesting
    public String getServiceProviderName(int i) {
        UiccProfile uiccProfileForPhone = this.mUiccController.getUiccProfileForPhone(i);
        if (uiccProfileForPhone == null) {
            return null;
        }
        return uiccProfileForPhone.getServiceProviderName();
    }

    /* JADX WARN: Code restructure failed: missing block: B:27:0x0066, code lost:
        logd("Name source " + r7 + "'s priority " + getNameSourcePriority(r7) + " is greater than name source " + r14 + "'s priority " + getNameSourcePriority(r14) + ", return now.");
     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x009e, code lost:
        return 0;
     */
    /* JADX WARN: Removed duplicated region for block: B:46:0x00f9 A[Catch: all -> 0x0142, TryCatch #0 {all -> 0x0142, blocks: (B:3:0x000b, B:5:0x0016, B:8:0x001e, B:9:0x0022, B:11:0x0029, B:15:0x0040, B:17:0x004a, B:21:0x0056, B:23:0x005c, B:27:0x0066, B:30:0x009f, B:32:0x00a5, B:44:0x00ed, B:46:0x00f9, B:47:0x0102, B:49:0x0108, B:51:0x010e, B:52:0x0130, B:36:0x00b2, B:40:0x00c1, B:42:0x00cb, B:43:0x00e4), top: B:60:0x000b }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public int setDisplayNameUsingSrc(String str, int i, int i2) {
        String simOperatorName;
        SubscriptionInfo subscriptionInfo;
        enforceModifyPhoneState("setDisplayNameUsingSrc");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            validateSubId(i);
            List<SubscriptionInfo> subInfo = getSubInfo(null, null);
            if (subInfo != null && !subInfo.isEmpty()) {
                Iterator<SubscriptionInfo> it = subInfo.iterator();
                while (true) {
                    boolean z = true;
                    if (it.hasNext()) {
                        SubscriptionInfo next = it.next();
                        int displayNameSource = next.getDisplayNameSource();
                        boolean z2 = getNameSourcePriority(displayNameSource) > getNameSourcePriority(i2);
                        if (getNameSourcePriority(displayNameSource) != getNameSourcePriority(i2) || !TextUtils.equals(str, next.getDisplayName())) {
                            z = false;
                        }
                        if (next.getSubscriptionId() != i || !isExistingNameSourceStillValid(next) || (!z2 && !z)) {
                        }
                    } else {
                        if (!TextUtils.isEmpty(str) && str.trim().length() != 0) {
                            simOperatorName = str;
                            ContentValues contentValues = new ContentValues(1);
                            contentValues.put("display_name", simOperatorName);
                            if (i2 >= 0) {
                                contentValues.put("name_source", Integer.valueOf(i2));
                            }
                            subscriptionInfo = getSubscriptionInfo(i);
                            if (subscriptionInfo != null && subscriptionInfo.isEmbedded()) {
                                ((EuiccManager) this.mContext.getSystemService("euicc")).createForCardId(subscriptionInfo.getCardId()).updateSubscriptionNickname(i, str, PendingIntent.getService(this.mContext, 0, new Intent(), 67108864));
                            }
                            int updateDatabase = updateDatabase(contentValues, i, true);
                            refreshCachedActiveSubscriptionInfoList();
                            notifySubscriptionInfoChanged();
                            return updateDatabase;
                        }
                        simOperatorName = this.mTelephonyManager.getSimOperatorName(i);
                        if (TextUtils.isEmpty(simOperatorName)) {
                            if (i2 == 2 && SubscriptionManager.isValidSlotIndex(getSlotIndex(i))) {
                                simOperatorName = Resources.getSystem().getString(17040113, Integer.valueOf(getSlotIndex(i) + 1));
                            } else {
                                simOperatorName = this.mContext.getString(17039374);
                            }
                        }
                        ContentValues contentValues2 = new ContentValues(1);
                        contentValues2.put("display_name", simOperatorName);
                        if (i2 >= 0) {
                        }
                        subscriptionInfo = getSubscriptionInfo(i);
                        if (subscriptionInfo != null) {
                            ((EuiccManager) this.mContext.getSystemService("euicc")).createForCardId(subscriptionInfo.getCardId()).updateSubscriptionNickname(i, str, PendingIntent.getService(this.mContext, 0, new Intent(), 67108864));
                        }
                        int updateDatabase2 = updateDatabase(contentValues2, i, true);
                        refreshCachedActiveSubscriptionInfoList();
                        notifySubscriptionInfoChanged();
                        return updateDatabase2;
                    }
                }
            }
            return 0;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public int setDisplayNumber(String str, int i) {
        int i2;
        enforceModifyPhoneState("setDisplayNumber");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            validateSubId(i);
            int phoneId = getPhoneId(i);
            if (str != null && phoneId >= 0 && phoneId < this.mTelephonyManager.getPhoneCount()) {
                SubscriptionInfo subscriptionInfo = getSubscriptionInfo(i);
                if (subscriptionInfo != null ? !TextUtils.equals(subscriptionInfo.getNumber(), str) : true) {
                    ContentValues contentValues = new ContentValues(1);
                    contentValues.put(IccProvider.STR_NUMBER, str);
                    i2 = this.mContext.getContentResolver().update(SubscriptionManager.getUriForSubscriptionId(i), contentValues, null, null);
                    refreshCachedActiveSubscriptionInfoList();
                    notifySubscriptionInfoChanged();
                } else {
                    i2 = 0;
                }
                return i2;
            }
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return -1;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void setAssociatedPlmns(String[] strArr, String[] strArr2, int i) {
        validateSubId(i);
        int phoneId = getPhoneId(i);
        if (phoneId < 0 || phoneId >= this.mTelephonyManager.getPhoneCount()) {
            return;
        }
        String str = PhoneConfigurationManager.SSSS;
        String str2 = strArr == null ? PhoneConfigurationManager.SSSS : (String) Arrays.stream(strArr).filter(new Predicate() { // from class: com.android.internal.telephony.SubscriptionController$$ExternalSyntheticLambda8
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$setAssociatedPlmns$3;
                lambda$setAssociatedPlmns$3 = SubscriptionController.lambda$setAssociatedPlmns$3((String) obj);
                return lambda$setAssociatedPlmns$3;
            }
        }).collect(Collectors.joining(","));
        if (strArr2 != null) {
            str = (String) Arrays.stream(strArr2).filter(new Predicate() { // from class: com.android.internal.telephony.SubscriptionController$$ExternalSyntheticLambda9
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$setAssociatedPlmns$4;
                    lambda$setAssociatedPlmns$4 = SubscriptionController.lambda$setAssociatedPlmns$4((String) obj);
                    return lambda$setAssociatedPlmns$4;
                }
            }).collect(Collectors.joining(","));
        }
        SubscriptionInfo subscriptionInfo = getSubscriptionInfo(i);
        boolean z = false;
        if (subscriptionInfo != null) {
            if ((((strArr == null && subscriptionInfo.getEhplmns().isEmpty()) || String.join(",", subscriptionInfo.getEhplmns()).equals(str2)) && strArr2 == null && subscriptionInfo.getHplmns().isEmpty()) || String.join(",", subscriptionInfo.getHplmns()).equals(str)) {
                z = true;
            }
        }
        if (z) {
            return;
        }
        ContentValues contentValues = new ContentValues(2);
        contentValues.put("ehplmns", str2);
        contentValues.put("hplmns", str);
        this.mContext.getContentResolver().update(SubscriptionManager.getUriForSubscriptionId(i), contentValues, null, null);
        refreshCachedActiveSubscriptionInfoList();
        notifySubscriptionInfoChanged();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$setAssociatedPlmns$3(String str) {
        return (str == null || str.isEmpty()) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$setAssociatedPlmns$4(String str) {
        return (str == null || str.isEmpty()) ? false : true;
    }

    public int setDataRoaming(int i, int i2) {
        enforceModifyPhoneState("setDataRoaming");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            validateSubId(i2);
            if (i >= 0) {
                ContentValues contentValues = new ContentValues(1);
                contentValues.put("data_roaming", Integer.valueOf(i));
                int updateDatabase = updateDatabase(contentValues, i2, true);
                refreshCachedActiveSubscriptionInfoList();
                notifySubscriptionInfoChanged();
                return updateDatabase;
            }
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return -1;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public int setDeviceToDeviceStatusSharing(int i, int i2) {
        enforceModifyPhoneState("setDeviceToDeviceStatusSharing");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            validateSubId(i2);
            if (i >= 0) {
                ContentValues contentValues = new ContentValues(1);
                contentValues.put("d2d_sharing_status", Integer.valueOf(i));
                int updateDatabase = updateDatabase(contentValues, i2, true);
                refreshCachedActiveSubscriptionInfoList();
                notifySubscriptionInfoChanged();
                return updateDatabase;
            }
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return -1;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public int setDeviceToDeviceStatusSharingContacts(String str, int i) {
        enforceModifyPhoneState("setDeviceToDeviceStatusSharingContacts");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            validateSubId(i);
            ContentValues contentValues = new ContentValues(1);
            contentValues.put("d2d_sharing_contacts", str);
            int updateDatabase = updateDatabase(contentValues, i, true);
            refreshCachedActiveSubscriptionInfoList();
            notifySubscriptionInfoChanged();
            return updateDatabase;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void syncGroupedSetting(int i) {
        logd("syncGroupedSetting");
        Cursor query = this.mContext.getContentResolver().query(SubscriptionManager.CONTENT_URI, null, InboundSmsHandler.SELECT_BY_ID, new String[]{String.valueOf(i)}, null);
        if (query != null) {
            try {
                if (query.moveToFirst()) {
                    Set<String> set = GROUP_SHARING_PROPERTIES;
                    ContentValues contentValues = new ContentValues(set.size());
                    for (String str : set) {
                        copyDataFromCursorToContentValue(str, query, contentValues);
                    }
                    updateDatabase(contentValues, i, true);
                    query.close();
                    return;
                }
            } catch (Throwable th) {
                if (query != null) {
                    try {
                        query.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }
        logd("[syncGroupedSetting] failed. Can't find refSubId " + i);
        if (query != null) {
            query.close();
        }
    }

    private void copyDataFromCursorToContentValue(String str, Cursor cursor, ContentValues contentValues) {
        int columnIndex = cursor.getColumnIndex(str);
        char c = 65535;
        if (columnIndex == -1) {
            logd("[copyDataFromCursorToContentValue] can't find column " + str);
            return;
        }
        str.hashCode();
        switch (str.hashCode()) {
            case -1958907444:
                if (str.equals("ims_rcs_uce_enabled")) {
                    c = 0;
                    break;
                }
                break;
            case -1950380197:
                if (str.equals("volte_vt_enabled")) {
                    c = 1;
                    break;
                }
                break;
            case -1811700826:
                if (str.equals("enabled_mobile_data_policies")) {
                    c = 2;
                    break;
                }
                break;
            case -1489974588:
                if (str.equals("nr_advanced_calling_enabled")) {
                    c = 3;
                    break;
                }
                break;
            case -1339240225:
                if (str.equals("cross_sim_calling_enabled")) {
                    c = 4;
                    break;
                }
                break;
            case -1218173306:
                if (str.equals("wfc_ims_enabled")) {
                    c = 5;
                    break;
                }
                break;
            case -482626276:
                if (str.equals("user_handle")) {
                    c = 6;
                    break;
                }
                break;
            case -420099376:
                if (str.equals("vt_ims_enabled")) {
                    c = 7;
                    break;
                }
                break;
            case 180938212:
                if (str.equals("wfc_ims_roaming_mode")) {
                    c = '\b';
                    break;
                }
                break;
            case 692824196:
                if (str.equals("data_roaming")) {
                    c = '\t';
                    break;
                }
                break;
            case 1334635646:
                if (str.equals("wfc_ims_mode")) {
                    c = '\n';
                    break;
                }
                break;
            case 1604840288:
                if (str.equals("wfc_ims_roaming_enabled")) {
                    c = 11;
                    break;
                }
                break;
            case 1615086568:
                if (str.equals("display_name")) {
                    c = '\f';
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
            case 1:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case '\b':
            case '\t':
            case '\n':
            case 11:
                contentValues.put(str, Integer.valueOf(cursor.getInt(columnIndex)));
                return;
            case 2:
            case '\f':
                contentValues.put(str, cursor.getString(columnIndex));
                return;
            default:
                loge("[copyDataFromCursorToContentValue] invalid propKey " + str);
                return;
        }
    }

    private int updateDatabase(ContentValues contentValues, int i, boolean z) {
        List<SubscriptionInfo> subscriptionsInGroup = getSubscriptionsInGroup(getGroupUuid(i), this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
        if (!z || subscriptionsInGroup == null || subscriptionsInGroup.size() == 0) {
            return this.mContext.getContentResolver().update(SubscriptionManager.getUriForSubscriptionId(i), contentValues, null, null);
        }
        int[] iArr = new int[subscriptionsInGroup.size()];
        for (int i2 = 0; i2 < subscriptionsInGroup.size(); i2++) {
            iArr[i2] = subscriptionsInGroup.get(i2).getSubscriptionId();
        }
        return this.mContext.getContentResolver().update(SubscriptionManager.CONTENT_URI, contentValues, getSelectionForSubIdList(iArr), null);
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x0020 A[Catch: all -> 0x0047, TRY_LEAVE, TryCatch #0 {all -> 0x0047, blocks: (B:3:0x0009, B:5:0x0014, B:11:0x0020), top: B:17:0x0009 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public int setCarrierId(int i, int i2) {
        boolean z;
        enforceModifyPhoneState("setCarrierId");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            validateSubId(i2);
            SubscriptionInfo subscriptionInfo = getSubscriptionInfo(i2);
            int i3 = 0;
            if (subscriptionInfo != null && subscriptionInfo.getCarrierId() == i) {
                z = false;
                if (z) {
                    ContentValues contentValues = new ContentValues(1);
                    contentValues.put("carrier_id", Integer.valueOf(i));
                    i3 = this.mContext.getContentResolver().update(SubscriptionManager.getUriForSubscriptionId(i2), contentValues, null, null);
                    refreshCachedActiveSubscriptionInfoList();
                    notifySubscriptionInfoChanged();
                }
                return i3;
            }
            z = true;
            if (z) {
            }
            return i3;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:21:0x0054  */
    /* JADX WARN: Removed duplicated region for block: B:28:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public int setMccMnc(String str, int i) {
        int i2;
        int i3;
        SubscriptionInfo subscriptionInfo;
        boolean z;
        String substring = str.substring(0, 3);
        String substring2 = str.substring(3);
        try {
            i2 = Integer.parseInt(substring);
        } catch (NumberFormatException unused) {
            i2 = 0;
        }
        try {
            i3 = Integer.parseInt(substring2);
        } catch (NumberFormatException unused2) {
            loge("[setMccMnc] - couldn't parse mcc/mnc: " + str);
            i3 = 0;
            subscriptionInfo = getSubscriptionInfo(i);
            z = true;
            if (subscriptionInfo != null) {
                z = false;
            }
            if (z) {
            }
        }
        subscriptionInfo = getSubscriptionInfo(i);
        z = true;
        if (subscriptionInfo != null && subscriptionInfo.getMcc() == i2 && subscriptionInfo.getMnc() == i3 && substring.equals(subscriptionInfo.getMccString()) && substring2.equals(subscriptionInfo.getMncString())) {
            z = false;
        }
        if (z) {
            return 0;
        }
        ContentValues contentValues = new ContentValues(4);
        contentValues.put("mcc", Integer.valueOf(i2));
        contentValues.put("mnc", Integer.valueOf(i3));
        contentValues.put("mcc_string", substring);
        contentValues.put("mnc_string", substring2);
        int update = this.mContext.getContentResolver().update(SubscriptionManager.getUriForSubscriptionId(i), contentValues, null, null);
        refreshCachedActiveSubscriptionInfoList();
        notifySubscriptionInfoChanged();
        return update;
    }

    public int setImsi(String str, int i) {
        if (getSubscriptionInfo(i) != null ? !TextUtils.equals(getImsiPrivileged(i), str) : true) {
            ContentValues contentValues = new ContentValues(1);
            contentValues.put("imsi", str);
            int update = this.mContext.getContentResolver().update(SubscriptionManager.getUriForSubscriptionId(i), contentValues, null, null);
            refreshCachedActiveSubscriptionInfoList();
            notifySubscriptionInfoChanged();
            return update;
        }
        return 0;
    }

    public int setUiccApplicationsEnabled(boolean z, int i) {
        enforceModifyPhoneState("setUiccApplicationsEnabled");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            ContentValues contentValues = new ContentValues(1);
            contentValues.put("uicc_applications_enabled", Boolean.valueOf(z));
            int update = this.mContext.getContentResolver().update(SubscriptionManager.getUriForSubscriptionId(i), contentValues, null, null);
            refreshCachedActiveSubscriptionInfoList();
            notifyUiccAppsEnableChanged();
            notifySubscriptionInfoChanged();
            return update;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void registerForUiccAppsEnabled(Handler handler, int i, Object obj, boolean z) {
        this.mUiccAppsEnableChangeRegList.addUnique(handler, i, obj);
        if (z) {
            handler.obtainMessage(i, obj).sendToTarget();
        }
    }

    public void unregisterForUiccAppsEnabled(Handler handler) {
        this.mUiccAppsEnableChangeRegList.remove(handler);
    }

    private void notifyUiccAppsEnableChanged() {
        this.mUiccAppsEnableChangeRegList.notifyRegistrants();
    }

    public String getImsiPrivileged(int i) {
        Cursor query = this.mContext.getContentResolver().query(SubscriptionManager.CONTENT_URI, null, InboundSmsHandler.SELECT_BY_ID, new String[]{String.valueOf(i)}, null);
        String str = null;
        try {
            if (query != null) {
                if (query.moveToNext()) {
                    str = getOptionalStringFromCursor(query, "imsi", null);
                }
            } else {
                logd("getImsiPrivileged: failed to retrieve imsi.");
            }
            if (query != null) {
                query.close();
            }
            return str;
        } catch (Throwable th) {
            if (query != null) {
                try {
                    query.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public int setCountryIso(String str, int i) {
        SubscriptionInfo subscriptionInfo = getSubscriptionInfo(i);
        if (subscriptionInfo != null ? true ^ TextUtils.equals(subscriptionInfo.getCountryIso(), str) : true) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("iso_country_code", str);
            int update = this.mContext.getContentResolver().update(SubscriptionManager.getUriForSubscriptionId(i), contentValues, null, null);
            refreshCachedActiveSubscriptionInfoList();
            notifySubscriptionInfoChanged();
            return update;
        }
        return 0;
    }

    public int getSlotIndex(int i) {
        if (VDBG) {
            printStackTrace("[getSlotIndex] subId=" + i);
        }
        if (i == Integer.MAX_VALUE) {
            i = getDefaultSubId();
        }
        if (SubscriptionManager.isValidSubscriptionId(i) && this.mSlotIndexToSubIds.size() != 0) {
            for (Map.Entry<Integer, ArrayList<Integer>> entry : this.mSlotIndexToSubIds.entrySet()) {
                int intValue = entry.getKey().intValue();
                ArrayList<Integer> value = entry.getValue();
                if (value != null && value.contains(Integer.valueOf(i))) {
                    if (VDBG) {
                        logv("[getSlotIndex]- return = " + intValue);
                    }
                    return intValue;
                }
            }
            return -1;
        }
        return -1;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    @Deprecated
    public int[] getSubIds(int i) {
        boolean z = VDBG;
        if (z) {
            printStackTrace("[getSubId]+ slotIndex=" + i);
        }
        if (i == Integer.MAX_VALUE) {
            i = getSlotIndex(getDefaultSubId());
            if (z) {
                logd("[getSubId] map default slotIndex=" + i);
            }
        }
        if (SubscriptionManager.isValidSlotIndex(i) || i == -1) {
            if (this.mSlotIndexToSubIds.size() == 0) {
                if (z) {
                    logd("[getSubId]- sSlotIndexToSubIds.size == 0, return null slotIndex=" + i);
                }
                return null;
            }
            ArrayList<Integer> copy = this.mSlotIndexToSubIds.getCopy(i);
            if (copy == null || copy.size() <= 0) {
                return null;
            }
            int[] iArr = new int[copy.size()];
            for (int i2 = 0; i2 < copy.size(); i2++) {
                iArr[i2] = copy.get(i2).intValue();
            }
            if (VDBG) {
                logd("[getSubId]- subIdArr=" + Arrays.toString(iArr));
            }
            return iArr;
        }
        return null;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public int getPhoneId(int i) {
        boolean z = VDBG;
        if (z) {
            printStackTrace("[getPhoneId] subId=" + i);
        }
        if (i == Integer.MAX_VALUE) {
            i = getDefaultSubId();
        }
        if (!SubscriptionManager.isValidSubscriptionId(i)) {
            if (z) {
                logdl("[getPhoneId]- invalid subId return=-1");
                return -1;
            }
            return -1;
        } else if (this.mSlotIndexToSubIds.size() == 0) {
            int i2 = mDefaultPhoneId;
            if (z) {
                logdl("[getPhoneId]- no sims, returning default phoneId=" + i2);
            }
            return i2;
        } else {
            for (Map.Entry<Integer, ArrayList<Integer>> entry : this.mSlotIndexToSubIds.entrySet()) {
                int intValue = entry.getKey().intValue();
                ArrayList<Integer> value = entry.getValue();
                if (value != null && value.contains(Integer.valueOf(i))) {
                    if (VDBG) {
                        logdl("[getPhoneId]- found subId=" + i + " phoneId=" + intValue);
                    }
                    return intValue;
                }
            }
            int i3 = mDefaultPhoneId;
            if (VDBG) {
                logd("[getPhoneId]- subId=" + i + " not found return default phoneId=" + i3);
            }
            return i3;
        }
    }

    public int clearSubInfo() {
        enforceModifyPhoneState("clearSubInfo");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            int size = this.mSlotIndexToSubIds.size();
            if (size != 0) {
                this.mSlotIndexToSubIds.clear();
                return size;
            }
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return 0;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    private void logv(String str) {
        Rlog.v("SubscriptionController", str);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected void logdl(String str) {
        logd(str);
        this.mLocalLog.log(str);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private void logd(String str) {
        Rlog.d("SubscriptionController", str);
    }

    private void logel(String str) {
        loge(str);
        this.mLocalLog.log(str);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private void loge(String str) {
        Rlog.e("SubscriptionController", str);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public int getDefaultSubId() {
        int defaultDataSubId;
        if (this.mTelephonyManager.isVoiceCapable()) {
            defaultDataSubId = getDefaultVoiceSubId();
            if (VDBG) {
                logdl("[getDefaultSubId] isVoiceCapable subId=" + defaultDataSubId);
            }
        } else {
            defaultDataSubId = getDefaultDataSubId();
            if (VDBG) {
                logdl("[getDefaultSubId] NOT VoiceCapable subId=" + defaultDataSubId);
            }
        }
        if (!isActiveSubId(defaultDataSubId)) {
            defaultDataSubId = this.mDefaultFallbackSubId.get();
            if (VDBG) {
                logdl("[getDefaultSubId] NOT active use fall back subId=" + defaultDataSubId);
            }
        }
        if (VDBG) {
            logv("[getDefaultSubId]- value = " + defaultDataSubId);
        }
        return defaultDataSubId;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void setDefaultSmsSubId(int i) {
        enforceModifyPhoneState("setDefaultSmsSubId");
        if (i == Integer.MAX_VALUE) {
            throw new RuntimeException("setDefaultSmsSubId called with DEFAULT_SUB_ID");
        }
        setGlobalSetting("multi_sim_sms", i);
        broadcastDefaultSmsSubIdChanged(i);
    }

    private void broadcastDefaultSmsSubIdChanged(int i) {
        Intent intent = new Intent("android.telephony.action.DEFAULT_SMS_SUBSCRIPTION_CHANGED");
        intent.addFlags(NetworkStackConstants.NEIGHBOR_ADVERTISEMENT_FLAG_OVERRIDE);
        SubscriptionManager.putSubscriptionIdExtra(intent, i);
        this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public int getDefaultSmsSubId() {
        int i = Settings.Global.getInt(this.mContext.getContentResolver(), "multi_sim_sms", -1);
        if (VDBG) {
            logd("[getDefaultSmsSubId] subId=" + i);
        }
        return i;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void setDefaultVoiceSubId(int i) {
        enforceModifyPhoneState("setDefaultVoiceSubId");
        if (i == Integer.MAX_VALUE) {
            throw new RuntimeException("setDefaultVoiceSubId called with DEFAULT_SUB_ID");
        }
        logdl("[setDefaultVoiceSubId] subId=" + i);
        int defaultSubId = getDefaultSubId();
        setGlobalSetting("multi_sim_voice_call", i);
        broadcastDefaultVoiceSubIdChanged(i);
        PhoneAccountHandle phoneAccountHandleForSubscriptionId = i == -1 ? null : this.mTelephonyManager.getPhoneAccountHandleForSubscriptionId(i);
        ((TelecomManager) this.mContext.getSystemService(TelecomManager.class)).setUserSelectedOutgoingPhoneAccount(phoneAccountHandleForSubscriptionId);
        logd("[setDefaultVoiceSubId] requesting change to phoneAccountHandle=" + phoneAccountHandleForSubscriptionId);
        if (defaultSubId != getDefaultSubId()) {
            sendDefaultChangedBroadcast(getDefaultSubId());
            logd(String.format("[setDefaultVoiceSubId] change to subId=%d", Integer.valueOf(getDefaultSubId())));
            return;
        }
        logd(String.format("[setDefaultVoiceSubId] default subId not changed. subId=%d", Integer.valueOf(defaultSubId)));
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public void broadcastDefaultVoiceSubIdChanged(int i) {
        Intent intent = new Intent("android.intent.action.ACTION_DEFAULT_VOICE_SUBSCRIPTION_CHANGED");
        intent.addFlags(NetworkStackConstants.NEIGHBOR_ADVERTISEMENT_FLAG_OVERRIDE);
        SubscriptionManager.putSubscriptionIdExtra(intent, i);
        this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public int getDefaultVoiceSubId() {
        int i = Settings.Global.getInt(this.mContext.getContentResolver(), "multi_sim_voice_call", -1);
        if (VDBG) {
            logd("[getDefaultVoiceSubId] subId=" + i);
        }
        return i;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public int getDefaultDataSubId() {
        int i = Settings.Global.getInt(this.mContext.getContentResolver(), "multi_sim_data_call", -1);
        if (VDBG) {
            logd("[getDefaultDataSubId] subId=" + i);
        }
        return i;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void setDefaultDataSubId(int i) {
        boolean z;
        int minRafSupported;
        enforceModifyPhoneState("setDefaultDataSubId");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            if (i == Integer.MAX_VALUE) {
                throw new RuntimeException("setDefaultDataSubId called with DEFAULT_SUB_ID");
            }
            ProxyController proxyController = ProxyController.getInstance();
            int activeModemCount = TelephonyManager.from(this.mContext).getActiveModemCount();
            logdl("[setDefaultDataSubId] num phones=" + activeModemCount + ", subId=" + i);
            if (SubscriptionManager.isValidSubscriptionId(i)) {
                RadioAccessFamily[] radioAccessFamilyArr = new RadioAccessFamily[activeModemCount];
                int i2 = 0;
                boolean z2 = false;
                while (i2 < activeModemCount) {
                    int subId = PhoneFactory.getPhone(i2).getSubId();
                    if (subId == i) {
                        minRafSupported = proxyController.getMaxRafSupported();
                        z = true;
                    } else {
                        z = z2;
                        minRafSupported = proxyController.getMinRafSupported();
                    }
                    logdl("[setDefaultDataSubId] phoneId=" + i2 + " subId=" + subId + " RAF=" + minRafSupported);
                    radioAccessFamilyArr[i2] = new RadioAccessFamily(i2, minRafSupported);
                    i2++;
                    z2 = z;
                }
                if (z2) {
                    proxyController.setRadioCapability(radioAccessFamilyArr);
                }
            }
            int defaultSubId = getDefaultSubId();
            setGlobalSetting("multi_sim_data_call", i);
            MultiSimSettingController.getInstance().notifyDefaultDataSubChanged();
            broadcastDefaultDataSubIdChanged(i);
            if (defaultSubId != getDefaultSubId()) {
                sendDefaultChangedBroadcast(getDefaultSubId());
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private void broadcastDefaultDataSubIdChanged(int i) {
        Intent intent = new Intent("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED");
        intent.addFlags(NetworkStackConstants.NEIGHBOR_ADVERTISEMENT_FLAG_OVERRIDE);
        SubscriptionManager.putSubscriptionIdExtra(intent, i);
        this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected void setDefaultFallbackSubId(int i, int i2) {
        int phoneId;
        if (i == Integer.MAX_VALUE) {
            throw new RuntimeException("setDefaultSubId called with DEFAULT_SUB_ID");
        }
        int defaultSubId = getDefaultSubId();
        if (isSubscriptionForRemoteSim(i2)) {
            this.mDefaultFallbackSubId.set(i);
            return;
        }
        if (SubscriptionManager.isValidSubscriptionId(i) && (phoneId = getPhoneId(i)) >= 0 && (phoneId < this.mTelephonyManager.getPhoneCount() || this.mTelephonyManager.getSimCount() == 1)) {
            this.mDefaultFallbackSubId.set(i);
            MccTable.updateMccMncConfiguration(this.mContext, this.mTelephonyManager.getSimOperatorNumericForPhone(phoneId));
        }
        if (defaultSubId != getDefaultSubId()) {
            sendDefaultChangedBroadcast(getDefaultSubId());
        }
    }

    public void sendDefaultChangedBroadcast(int i) {
        int phoneId = SubscriptionManager.getPhoneId(i);
        Intent intent = new Intent("android.telephony.action.DEFAULT_SUBSCRIPTION_CHANGED");
        intent.addFlags(NetworkStackConstants.NEIGHBOR_ADVERTISEMENT_FLAG_OVERRIDE);
        SubscriptionManager.putPhoneIdAndSubIdExtra(intent, phoneId, i);
        this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
    }

    public boolean isOpportunistic(int i) {
        SubscriptionInfo activeSubscriptionInfo = getActiveSubscriptionInfo(i, this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
        return activeSubscriptionInfo != null && activeSubscriptionInfo.isOpportunistic();
    }

    public int getSubId(int i) {
        int[] subIds = getSubIds(i);
        if (subIds == null || subIds.length == 0) {
            return -1;
        }
        return subIds[0];
    }

    @VisibleForTesting
    public List<SubscriptionInfo> getSubInfoUsingSlotIndexPrivileged(int i) {
        if (i == Integer.MAX_VALUE) {
            i = getSlotIndex(getDefaultSubId());
        }
        ArrayList arrayList = null;
        if (SubscriptionManager.isValidSlotIndex(i)) {
            Cursor query = this.mContext.getContentResolver().query(SubscriptionManager.CONTENT_URI, null, "sim_id=?", new String[]{String.valueOf(i)}, null);
            if (query != null) {
                while (query.moveToNext()) {
                    try {
                        SubscriptionInfo subInfoRecord = getSubInfoRecord(query);
                        if (subInfoRecord != null) {
                            if (arrayList == null) {
                                arrayList = new ArrayList();
                            }
                            arrayList.add(subInfoRecord);
                        }
                    } finally {
                        query.close();
                    }
                }
            }
            if (query != null) {
            }
            return arrayList;
        }
        return null;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private void validateSubId(int i) {
        if (!SubscriptionManager.isValidSubscriptionId(i)) {
            throw new IllegalArgumentException("Invalid sub id passed as parameter");
        }
        if (i == Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Default sub id passed as parameter");
        }
    }

    private synchronized ArrayList<Integer> getActiveSubIdArrayList() {
        ArrayList<Integer> arrayList;
        ArrayList<Map.Entry> arrayList2 = new ArrayList(this.mSlotIndexToSubIds.entrySet());
        Collections.sort(arrayList2, new Comparator() { // from class: com.android.internal.telephony.SubscriptionController$$ExternalSyntheticLambda3
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                int lambda$getActiveSubIdArrayList$5;
                lambda$getActiveSubIdArrayList$5 = SubscriptionController.lambda$getActiveSubIdArrayList$5((Map.Entry) obj, (Map.Entry) obj2);
                return lambda$getActiveSubIdArrayList$5;
            }
        });
        arrayList = new ArrayList<>();
        for (Map.Entry entry : arrayList2) {
            arrayList.addAll((Collection) entry.getValue());
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$getActiveSubIdArrayList$5(Map.Entry entry, Map.Entry entry2) {
        return ((Integer) entry.getKey()).compareTo((Integer) entry2.getKey());
    }

    private boolean isSubscriptionVisible(int i) {
        boolean z;
        SubscriptionInfo next;
        synchronized (this.mSubInfoListLock) {
            Iterator<SubscriptionInfo> it = this.mCacheOpportunisticSubInfoList.iterator();
            do {
                z = true;
                if (!it.hasNext()) {
                    return true;
                }
                next = it.next();
            } while (next.getSubscriptionId() != i);
            if (next.getGroupUuid() != null) {
                z = false;
            }
            return z;
        }
    }

    public int[] getActiveSubIdList(boolean z) {
        enforceReadPrivilegedPhoneState("getActiveSubIdList");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            List<Integer> activeSubIdArrayList = getActiveSubIdArrayList();
            if (z) {
                activeSubIdArrayList = (List) activeSubIdArrayList.stream().filter(new Predicate() { // from class: com.android.internal.telephony.SubscriptionController$$ExternalSyntheticLambda7
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$getActiveSubIdList$6;
                        lambda$getActiveSubIdList$6 = SubscriptionController.this.lambda$getActiveSubIdList$6((Integer) obj);
                        return lambda$getActiveSubIdList$6;
                    }
                }).collect(Collectors.toList());
            }
            int size = activeSubIdArrayList.size();
            int[] iArr = new int[size];
            int i = 0;
            for (Integer num : activeSubIdArrayList) {
                iArr[i] = num.intValue();
                i++;
            }
            if (VDBG) {
                logdl("[getActiveSubIdList] allSubs=" + activeSubIdArrayList + " subIdArr.length=" + size);
            }
            return iArr;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$getActiveSubIdList$6(Integer num) {
        return isSubscriptionVisible(num.intValue());
    }

    public boolean isActiveSubId(int i, String str, String str2) {
        if (!TelephonyPermissions.checkCallingOrSelfReadPhoneState(this.mContext, i, str, str2, "isActiveSubId")) {
            throw new SecurityException("Requires READ_PHONE_STATE permission.");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return isActiveSubId(i);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    @Deprecated
    public boolean isActiveSubId(int i) {
        boolean z = SubscriptionManager.isValidSubscriptionId(i) && getActiveSubIdArrayList().contains(Integer.valueOf(i));
        if (VDBG) {
            logdl("[isActiveSubId]- " + z);
        }
        return z;
    }

    public int setSubscriptionProperty(int i, String str, String str2) {
        enforceModifyPhoneState("setSubscriptionProperty");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            validateSubId(i);
            int subscriptionPropertyIntoContentResolver = setSubscriptionPropertyIntoContentResolver(i, str, str2, this.mContext.getContentResolver());
            refreshCachedActiveSubscriptionInfoList();
            return subscriptionPropertyIntoContentResolver;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    private int setSubscriptionPropertyIntoContentResolver(int i, String str, String str2, ContentResolver contentResolver) {
        ContentValues contentValues = new ContentValues();
        boolean contains = GROUP_SHARING_PROPERTIES.contains(str);
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -2000412720:
                if (str.equals("enable_alert_vibrate")) {
                    c = 0;
                    break;
                }
                break;
            case -1958907444:
                if (str.equals("ims_rcs_uce_enabled")) {
                    c = 1;
                    break;
                }
                break;
            case -1950380197:
                if (str.equals("volte_vt_enabled")) {
                    c = 2;
                    break;
                }
                break;
            case -1819373132:
                if (str.equals("is_opportunistic")) {
                    c = 3;
                    break;
                }
                break;
            case -1555340190:
                if (str.equals("enable_cmas_extreme_threat_alerts")) {
                    c = 4;
                    break;
                }
                break;
            case -1489974588:
                if (str.equals("nr_advanced_calling_enabled")) {
                    c = 5;
                    break;
                }
                break;
            case -1433878403:
                if (str.equals("enable_cmas_test_alerts")) {
                    c = 6;
                    break;
                }
                break;
            case -1395523150:
                if (str.equals("usage_setting")) {
                    c = 7;
                    break;
                }
                break;
            case -1390801311:
                if (str.equals("enable_alert_speech")) {
                    c = '\b';
                    break;
                }
                break;
            case -1383812487:
                if (str.equals("phone_number_source_carrier")) {
                    c = '\t';
                    break;
                }
                break;
            case -1339240225:
                if (str.equals("cross_sim_calling_enabled")) {
                    c = '\n';
                    break;
                }
                break;
            case -1218173306:
                if (str.equals("wfc_ims_enabled")) {
                    c = 11;
                    break;
                }
                break;
            case -714305475:
                if (str.equals("satellite_enabled")) {
                    c = '\f';
                    break;
                }
                break;
            case -482626276:
                if (str.equals("user_handle")) {
                    c = '\r';
                    break;
                }
                break;
            case -461686719:
                if (str.equals("enable_emergency_alerts")) {
                    c = 14;
                    break;
                }
                break;
            case -420099376:
                if (str.equals("vt_ims_enabled")) {
                    c = 15;
                    break;
                }
                break;
            case -349439993:
                if (str.equals("alert_sound_duration")) {
                    c = 16;
                    break;
                }
                break;
            case 180938212:
                if (str.equals("wfc_ims_roaming_mode")) {
                    c = 17;
                    break;
                }
                break;
            case 203677434:
                if (str.equals("enable_cmas_amber_alerts")) {
                    c = 18;
                    break;
                }
                break;
            case 240841894:
                if (str.equals("show_cmas_opt_out_dialog")) {
                    c = 19;
                    break;
                }
                break;
            case 407275608:
                if (str.equals("enable_cmas_severe_threat_alerts")) {
                    c = 20;
                    break;
                }
                break;
            case 410371787:
                if (str.equals("allowed_network_types_for_reasons")) {
                    c = 21;
                    break;
                }
                break;
            case 462555599:
                if (str.equals("alert_reminder_interval")) {
                    c = 22;
                    break;
                }
                break;
            case 501936055:
                if (str.equals("voims_opt_in_status")) {
                    c = 23;
                    break;
                }
                break;
            case 799757264:
                if (str.equals("phone_number_source_ims")) {
                    c = 24;
                    break;
                }
                break;
            case 1270593452:
                if (str.equals("enable_etws_test_alerts")) {
                    c = 25;
                    break;
                }
                break;
            case 1288054979:
                if (str.equals("enable_channel_50_alerts")) {
                    c = 26;
                    break;
                }
                break;
            case 1334635646:
                if (str.equals("wfc_ims_mode")) {
                    c = 27;
                    break;
                }
                break;
            case 1604840288:
                if (str.equals("wfc_ims_roaming_enabled")) {
                    c = 28;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case '\b':
            case '\n':
            case 11:
            case '\f':
            case '\r':
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 22:
            case 23:
            case 25:
            case 26:
            case 27:
            case 28:
                contentValues.put(str, Integer.valueOf(Integer.parseInt(str2)));
                break;
            case '\t':
            case 21:
            case 24:
                contentValues.put(str, str2);
                break;
        }
        return updateDatabase(contentValues, i, contains);
    }

    public String getSubscriptionProperty(int i, String str, String str2, String str3) {
        str.hashCode();
        if (str.equals("group_uuid")) {
            if (this.mContext.checkCallingOrSelfPermission("android.permission.READ_PRIVILEGED_PHONE_STATE") != 0) {
                EventLog.writeEvent(1397638484, "213457638", Integer.valueOf(Binder.getCallingUid()));
                return null;
            }
        } else if (!TelephonyPermissions.checkCallingOrSelfReadPhoneState(this.mContext, i, str2, str3, "getSubscriptionProperty")) {
            return null;
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return getSubscriptionProperty(i, str);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:121:0x01cc  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public String getSubscriptionProperty(int i, String str) {
        char c;
        String string;
        Cursor query = this.mContext.getContentResolver().query(SubscriptionManager.CONTENT_URI, new String[]{str}, InboundSmsHandler.SELECT_BY_ID, new String[]{i + PhoneConfigurationManager.SSSS}, null);
        if (query != null) {
            try {
                if (query.moveToFirst()) {
                    switch (str.hashCode()) {
                        case -2000412720:
                            if (str.equals("enable_alert_vibrate")) {
                                c = 6;
                                break;
                            }
                            c = 65535;
                            break;
                        case -1958907444:
                            if (str.equals("ims_rcs_uce_enabled")) {
                                c = 18;
                                break;
                            }
                            c = 65535;
                            break;
                        case -1950380197:
                            if (str.equals("volte_vt_enabled")) {
                                c = '\f';
                                break;
                            }
                            c = 65535;
                            break;
                        case -1819373132:
                            if (str.equals("is_opportunistic")) {
                                c = 20;
                                break;
                            }
                            c = 65535;
                            break;
                        case -1811700826:
                            if (str.equals("enabled_mobile_data_policies")) {
                                c = 22;
                                break;
                            }
                            c = 65535;
                            break;
                        case -1555340190:
                            if (str.equals("enable_cmas_extreme_threat_alerts")) {
                                c = 0;
                                break;
                            }
                            c = 65535;
                            break;
                        case -1489974588:
                            if (str.equals("nr_advanced_calling_enabled")) {
                                c = 27;
                                break;
                            }
                            c = 65535;
                            break;
                        case -1433878403:
                            if (str.equals("enable_cmas_test_alerts")) {
                                c = '\n';
                                break;
                            }
                            c = 65535;
                            break;
                        case -1395523150:
                            if (str.equals("usage_setting")) {
                                c = 30;
                                break;
                            }
                            c = 65535;
                            break;
                        case -1390801311:
                            if (str.equals("enable_alert_speech")) {
                                c = 7;
                                break;
                            }
                            c = 65535;
                            break;
                        case -1383812487:
                            if (str.equals("phone_number_source_carrier")) {
                                c = 28;
                                break;
                            }
                            c = 65535;
                            break;
                        case -1339240225:
                            if (str.equals("cross_sim_calling_enabled")) {
                                c = 19;
                                break;
                            }
                            c = 65535;
                            break;
                        case -1292749250:
                            if (str.equals("d2d_sharing_status")) {
                                c = 24;
                                break;
                            }
                            c = 65535;
                            break;
                        case -1218173306:
                            if (str.equals("wfc_ims_enabled")) {
                                c = 14;
                                break;
                            }
                            c = 65535;
                            break;
                        case -714305475:
                            if (str.equals("satellite_enabled")) {
                                c = ' ';
                                break;
                            }
                            c = 65535;
                            break;
                        case -482626276:
                            if (str.equals("user_handle")) {
                                c = 31;
                                break;
                            }
                            c = 65535;
                            break;
                        case -461686719:
                            if (str.equals("enable_emergency_alerts")) {
                                c = 3;
                                break;
                            }
                            c = 65535;
                            break;
                        case -420099376:
                            if (str.equals("vt_ims_enabled")) {
                                c = '\r';
                                break;
                            }
                            c = 65535;
                            break;
                        case -349439993:
                            if (str.equals("alert_sound_duration")) {
                                c = 4;
                                break;
                            }
                            c = 65535;
                            break;
                        case 180938212:
                            if (str.equals("wfc_ims_roaming_mode")) {
                                c = 16;
                                break;
                            }
                            c = 65535;
                            break;
                        case 203677434:
                            if (str.equals("enable_cmas_amber_alerts")) {
                                c = 2;
                                break;
                            }
                            c = 65535;
                            break;
                        case 240841894:
                            if (str.equals("show_cmas_opt_out_dialog")) {
                                c = 11;
                                break;
                            }
                            c = 65535;
                            break;
                        case 407275608:
                            if (str.equals("enable_cmas_severe_threat_alerts")) {
                                c = 1;
                                break;
                            }
                            c = 65535;
                            break;
                        case 410371787:
                            if (str.equals("allowed_network_types_for_reasons")) {
                                c = 23;
                                break;
                            }
                            c = 65535;
                            break;
                        case 462555599:
                            if (str.equals("alert_reminder_interval")) {
                                c = 5;
                                break;
                            }
                            c = 65535;
                            break;
                        case 501936055:
                            if (str.equals("voims_opt_in_status")) {
                                c = 25;
                                break;
                            }
                            c = 65535;
                            break;
                        case 799757264:
                            if (str.equals("phone_number_source_ims")) {
                                c = 29;
                                break;
                            }
                            c = 65535;
                            break;
                        case 1270593452:
                            if (str.equals("enable_etws_test_alerts")) {
                                c = '\b';
                                break;
                            }
                            c = 65535;
                            break;
                        case 1282534779:
                            if (str.equals("group_uuid")) {
                                c = 21;
                                break;
                            }
                            c = 65535;
                            break;
                        case 1288054979:
                            if (str.equals("enable_channel_50_alerts")) {
                                c = '\t';
                                break;
                            }
                            c = 65535;
                            break;
                        case 1322345375:
                            if (str.equals("d2d_sharing_contacts")) {
                                c = 26;
                                break;
                            }
                            c = 65535;
                            break;
                        case 1334635646:
                            if (str.equals("wfc_ims_mode")) {
                                c = 15;
                                break;
                            }
                            c = 65535;
                            break;
                        case 1604840288:
                            if (str.equals("wfc_ims_roaming_enabled")) {
                                c = 17;
                                break;
                            }
                            c = 65535;
                            break;
                        default:
                            c = 65535;
                            break;
                    }
                    switch (c) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                        case '\b':
                        case '\t':
                        case '\n':
                        case 11:
                        case '\f':
                        case '\r':
                        case 14:
                        case 15:
                        case 16:
                        case 17:
                        case 18:
                        case 19:
                        case 20:
                        case 21:
                        case 22:
                        case 23:
                        case 24:
                        case 25:
                        case 26:
                        case 27:
                        case 28:
                        case 29:
                        case 30:
                        case 31:
                        case ' ':
                            string = query.getString(0);
                            break;
                    }
                    if (query != null) {
                        query.close();
                    }
                    return string;
                }
            } catch (Throwable th) {
                try {
                    query.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        }
        string = null;
        if (query != null) {
        }
        return string;
    }

    private void printStackTrace(String str) {
        StackTraceElement[] stackTrace;
        RuntimeException runtimeException = new RuntimeException();
        logd("StackTrace - " + str);
        boolean z = true;
        for (StackTraceElement stackTraceElement : runtimeException.getStackTrace()) {
            if (z) {
                z = false;
            } else {
                logd(stackTraceElement.toString());
            }
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.DUMP", "Requires DUMP");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            printWriter.println("SubscriptionController:");
            printWriter.println(" mLastISubServiceRegTime=" + this.mLastISubServiceRegTime);
            printWriter.println(" defaultSubId=" + getDefaultSubId());
            printWriter.println(" defaultDataSubId=" + getDefaultDataSubId());
            printWriter.println(" defaultVoiceSubId=" + getDefaultVoiceSubId());
            printWriter.println(" defaultSmsSubId=" + getDefaultSmsSubId());
            printWriter.println(" defaultVoicePhoneId=" + SubscriptionManager.getDefaultVoicePhoneId());
            printWriter.flush();
            for (Map.Entry<Integer, ArrayList<Integer>> entry : this.mSlotIndexToSubIds.entrySet()) {
                printWriter.println(" sSlotIndexToSubId[" + entry.getKey() + "]: subIds=" + entry);
            }
            printWriter.flush();
            printWriter.println("++++++++++++++++++++++++++++++++");
            List<SubscriptionInfo> activeSubscriptionInfoList = getActiveSubscriptionInfoList(this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
            if (activeSubscriptionInfoList != null) {
                printWriter.println(" ActiveSubInfoList:");
                Iterator<SubscriptionInfo> it = activeSubscriptionInfoList.iterator();
                while (it.hasNext()) {
                    printWriter.println("  " + it.next().toString());
                }
            } else {
                printWriter.println(" ActiveSubInfoList: is null");
            }
            printWriter.flush();
            printWriter.println("++++++++++++++++++++++++++++++++");
            List<SubscriptionInfo> allSubInfoList = getAllSubInfoList(this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
            if (allSubInfoList != null) {
                printWriter.println(" AllSubInfoList:");
                Iterator<SubscriptionInfo> it2 = allSubInfoList.iterator();
                while (it2.hasNext()) {
                    printWriter.println("  " + it2.next().toString());
                }
            } else {
                printWriter.println(" AllSubInfoList: is null");
            }
            printWriter.flush();
            printWriter.println("++++++++++++++++++++++++++++++++");
            this.mLocalLog.dump(fileDescriptor, printWriter, strArr);
            printWriter.flush();
            printWriter.println("++++++++++++++++++++++++++++++++");
            printWriter.flush();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public void migrateImsSettings() {
        migrateImsSettingHelper("volte_vt_enabled", "volte_vt_enabled");
        migrateImsSettingHelper("vt_ims_enabled", "vt_ims_enabled");
        migrateImsSettingHelper("wfc_ims_enabled", "wfc_ims_enabled");
        migrateImsSettingHelper("wfc_ims_mode", "wfc_ims_mode");
        migrateImsSettingHelper("wfc_ims_roaming_mode", "wfc_ims_roaming_mode");
        migrateImsSettingHelper("wfc_ims_roaming_enabled", "wfc_ims_roaming_enabled");
    }

    private void migrateImsSettingHelper(String str, String str2) {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        int defaultVoiceSubId = getDefaultVoiceSubId();
        if (defaultVoiceSubId == -1) {
            return;
        }
        try {
            int i = Settings.Global.getInt(contentResolver, str);
            if (i != -1) {
                setSubscriptionPropertyIntoContentResolver(defaultVoiceSubId, str2, Integer.toString(i), contentResolver);
                Settings.Global.putInt(contentResolver, str, -1);
            }
        } catch (Settings.SettingNotFoundException unused) {
        }
    }

    public int setOpportunistic(boolean z, int i, String str) {
        try {
            TelephonyPermissions.enforceCallingOrSelfModifyPermissionOrCarrierPrivilege(this.mContext, i, str);
        } catch (SecurityException unused) {
            enforceCarrierPrivilegeOnInactiveSub(i, str, "Caller requires permission on sub " + i);
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            int subscriptionProperty = setSubscriptionProperty(i, "is_opportunistic", String.valueOf(z ? 1 : 0));
            if (subscriptionProperty != 0) {
                notifySubscriptionInfoChanged();
            }
            return subscriptionProperty;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    private void enforceCarrierPrivilegeOnInactiveSub(int i, String str, String str2) {
        this.mAppOps.checkPackage(Binder.getCallingUid(), str);
        SubscriptionManager subscriptionManager = (SubscriptionManager) this.mContext.getSystemService("telephony_subscription_service");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            List<SubscriptionInfo> subInfo = getSubInfo("_id=" + i, null);
            try {
                if (isActiveSubId(i) || subInfo == null || subInfo.size() != 1 || !subscriptionManager.canManageSubscription(subInfo.get(0), str)) {
                    throw new SecurityException(str2);
                }
            } catch (IllegalArgumentException unused) {
                throw new SecurityException(str2);
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void setPreferredDataSubscriptionId(int i, boolean z, ISetOpportunisticDataCallback iSetOpportunisticDataCallback) {
        enforceModifyPhoneState("setPreferredDataSubscriptionId");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            PhoneSwitcher phoneSwitcher = PhoneSwitcher.getInstance();
            if (phoneSwitcher == null) {
                logd("Set preferred data sub: phoneSwitcher is null.");
                AnomalyReporter.reportAnomaly(UUID.fromString("a73fe57f-4178-4bc3-a7ae-9d7354939274"), "Set preferred data sub: phoneSwitcher is null.");
                if (iSetOpportunisticDataCallback != null) {
                    try {
                        iSetOpportunisticDataCallback.onComplete(4);
                    } catch (RemoteException e) {
                        logd("RemoteException " + e);
                    }
                }
                return;
            }
            phoneSwitcher.trySetOpportunisticDataSubscription(i, z, iSetOpportunisticDataCallback);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public int getPreferredDataSubscriptionId() {
        enforceReadPrivilegedPhoneState("getPreferredDataSubscriptionId");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            PhoneSwitcher phoneSwitcher = PhoneSwitcher.getInstance();
            if (phoneSwitcher == null) {
                AnomalyReporter.reportAnomaly(UUID.fromString("e72747ab-d0aa-4b0e-9dd5-cb99365c6d58"), "Get preferred data sub: phoneSwitcher is null.");
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return KeepaliveStatus.INVALID_HANDLE;
            }
            return phoneSwitcher.getAutoSelectedDataSubId();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public List<SubscriptionInfo> getOpportunisticSubscriptions(String str, String str2) {
        return getSubscriptionInfoListFromCacheHelper(str, str2, makeCacheListCopyWithLock(this.mCacheOpportunisticSubInfoList));
    }

    public ParcelUuid createSubscriptionGroup(int[] iArr, String str) {
        if (iArr == null || iArr.length == 0) {
            throw new IllegalArgumentException("Invalid subIdList " + Arrays.toString(iArr));
        }
        this.mAppOps.checkPackage(Binder.getCallingUid(), str);
        if (this.mContext.checkCallingOrSelfPermission("android.permission.MODIFY_PHONE_STATE") != 0 && !checkCarrierPrivilegeOnSubList(iArr, str)) {
            throw new SecurityException("CreateSubscriptionGroup needs MODIFY_PHONE_STATE or carrier privilege permission on all specified subscriptions");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            ParcelUuid parcelUuid = new ParcelUuid(UUID.randomUUID());
            ContentValues contentValues = new ContentValues();
            contentValues.put("group_uuid", parcelUuid.toString());
            contentValues.put("group_owner", str);
            this.mContext.getContentResolver().update(SubscriptionManager.CONTENT_URI, contentValues, getSelectionForSubIdList(iArr), null);
            refreshCachedActiveSubscriptionInfoList();
            notifySubscriptionInfoChanged();
            MultiSimSettingController.getInstance().notifySubscriptionGroupChanged(parcelUuid);
            return parcelUuid;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean canPackageManageGroup(ParcelUuid parcelUuid, String str) {
        if (parcelUuid == null) {
            throw new IllegalArgumentException("Invalid groupUuid");
        }
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("Empty callingPackage");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            List<SubscriptionInfo> subInfo = getSubInfo("group_uuid='" + parcelUuid.toString() + "'", null);
            Binder.restoreCallingIdentity(clearCallingIdentity);
            if (ArrayUtils.isEmpty(subInfo) || str.equals(subInfo.get(0).getGroupOwner())) {
                return true;
            }
            return checkCarrierPrivilegeOnSubList(subInfo.stream().mapToInt(new ToIntFunction() { // from class: com.android.internal.telephony.SubscriptionController$$ExternalSyntheticLambda1
                @Override // java.util.function.ToIntFunction
                public final int applyAsInt(Object obj) {
                    int subscriptionId;
                    subscriptionId = ((SubscriptionInfo) obj).getSubscriptionId();
                    return subscriptionId;
                }
            }).toArray(), str);
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    private int updateGroupOwner(ParcelUuid parcelUuid, String str) {
        ContentValues contentValues = new ContentValues(1);
        contentValues.put("group_owner", str);
        ContentResolver contentResolver = this.mContext.getContentResolver();
        Uri uri = SubscriptionManager.CONTENT_URI;
        return contentResolver.update(uri, contentValues, "group_uuid=\"" + parcelUuid + "\"", null);
    }

    public void addSubscriptionsIntoGroup(int[] iArr, ParcelUuid parcelUuid, String str) {
        if (iArr == null || iArr.length == 0) {
            throw new IllegalArgumentException("Invalid subId list");
        }
        if (parcelUuid == null || parcelUuid.equals(INVALID_GROUP_UUID)) {
            throw new IllegalArgumentException("Invalid groupUuid");
        }
        this.mAppOps.checkPackage(Binder.getCallingUid(), str);
        if (this.mContext.checkCallingOrSelfPermission("android.permission.MODIFY_PHONE_STATE") != 0 && (!checkCarrierPrivilegeOnSubList(iArr, str) || !canPackageManageGroup(parcelUuid, str))) {
            throw new SecurityException("Requires MODIFY_PHONE_STATE or carrier privilege permissions on subscriptions and the group.");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("group_uuid", parcelUuid.toString());
            if (this.mContext.getContentResolver().update(SubscriptionManager.CONTENT_URI, contentValues, getSelectionForSubIdList(iArr), null) > 0) {
                updateGroupOwner(parcelUuid, str);
                refreshCachedActiveSubscriptionInfoList();
                notifySubscriptionInfoChanged();
                MultiSimSettingController.getInstance().notifySubscriptionGroupChanged(parcelUuid);
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void removeSubscriptionsFromGroup(int[] iArr, ParcelUuid parcelUuid, String str) {
        if (iArr == null || iArr.length == 0) {
            return;
        }
        this.mAppOps.checkPackage(Binder.getCallingUid(), str);
        if (this.mContext.checkCallingOrSelfPermission("android.permission.MODIFY_PHONE_STATE") != 0 && (!checkCarrierPrivilegeOnSubList(iArr, str) || !canPackageManageGroup(parcelUuid, str))) {
            throw new SecurityException("removeSubscriptionsFromGroup needs MODIFY_PHONE_STATE or carrier privilege permission on all specified subscriptions");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            for (SubscriptionInfo subscriptionInfo : getSubInfo(getSelectionForSubIdList(iArr), null)) {
                if (!parcelUuid.equals(subscriptionInfo.getGroupUuid())) {
                    throw new IllegalArgumentException("Subscription " + subscriptionInfo.getSubscriptionId() + " doesn't belong to group " + parcelUuid);
                }
            }
            ContentValues contentValues = new ContentValues();
            contentValues.put("group_uuid", (String) null);
            contentValues.put("group_owner", (String) null);
            if (this.mContext.getContentResolver().update(SubscriptionManager.CONTENT_URI, contentValues, getSelectionForSubIdList(iArr), null) > 0) {
                updateGroupOwner(parcelUuid, str);
                refreshCachedActiveSubscriptionInfoList();
                notifySubscriptionInfoChanged();
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    private boolean checkCarrierPrivilegeOnSubList(int[] iArr, String str) {
        HashSet hashSet = new HashSet();
        for (int i : iArr) {
            if (isActiveSubId(i)) {
                if (!this.mTelephonyManager.hasCarrierPrivileges(i)) {
                    return false;
                }
            } else {
                hashSet.add(Integer.valueOf(i));
            }
        }
        if (hashSet.isEmpty()) {
            return true;
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            SubscriptionManager subscriptionManager = (SubscriptionManager) this.mContext.getSystemService("telephony_subscription_service");
            List<SubscriptionInfo> subInfo = getSubInfo(getSelectionForSubIdList(iArr), null);
            if (subInfo == null || subInfo.size() != iArr.length) {
                throw new IllegalArgumentException("Invalid subInfoList.");
            }
            for (SubscriptionInfo subscriptionInfo : subInfo) {
                if (hashSet.contains(Integer.valueOf(subscriptionInfo.getSubscriptionId()))) {
                    if (!subscriptionInfo.isEmbedded() || !subscriptionManager.canManageSubscription(subscriptionInfo, str)) {
                        return false;
                    }
                    hashSet.remove(Integer.valueOf(subscriptionInfo.getSubscriptionId()));
                }
            }
            return hashSet.isEmpty();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public static String getSelectionForSubIdList(int[] iArr) {
        StringBuilder sb = new StringBuilder();
        sb.append("_id");
        sb.append(" IN (");
        for (int i = 0; i < iArr.length - 1; i++) {
            sb.append(iArr[i] + ", ");
        }
        sb.append(iArr[iArr.length - 1]);
        sb.append(")");
        return sb.toString();
    }

    private String getSelectionForIccIdList(String[] strArr) {
        StringBuilder sb = new StringBuilder();
        sb.append("icc_id");
        sb.append(" IN (");
        for (int i = 0; i < strArr.length - 1; i++) {
            sb.append("'" + strArr[i] + "', ");
        }
        sb.append("'" + strArr[strArr.length - 1] + "'");
        sb.append(")");
        return sb.toString();
    }

    public List<SubscriptionInfo> getSubscriptionsInGroup(final ParcelUuid parcelUuid, final String str, final String str2) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            List<SubscriptionInfo> allSubInfoList = getAllSubInfoList(this.mContext.getOpPackageName(), this.mContext.getAttributionTag(), true);
            if (parcelUuid == null || allSubInfoList == null || allSubInfoList.isEmpty()) {
                return new ArrayList();
            }
            Binder.restoreCallingIdentity(clearCallingIdentity);
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
            return (List) allSubInfoList.stream().filter(new Predicate() { // from class: com.android.internal.telephony.SubscriptionController$$ExternalSyntheticLambda4
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$getSubscriptionsInGroup$8;
                    lambda$getSubscriptionsInGroup$8 = SubscriptionController.this.lambda$getSubscriptionsInGroup$8(parcelUuid, str, str2, (SubscriptionInfo) obj);
                    return lambda$getSubscriptionsInGroup$8;
                }
            }).map(new Function() { // from class: com.android.internal.telephony.SubscriptionController$$ExternalSyntheticLambda5
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    SubscriptionInfo lambda$getSubscriptionsInGroup$9;
                    lambda$getSubscriptionsInGroup$9 = SubscriptionController.this.lambda$getSubscriptionsInGroup$9(str, str2, (SubscriptionInfo) obj);
                    return lambda$getSubscriptionsInGroup$9;
                }
            }).collect(Collectors.toList());
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$getSubscriptionsInGroup$8(ParcelUuid parcelUuid, String str, String str2, SubscriptionInfo subscriptionInfo) {
        if (parcelUuid.equals(subscriptionInfo.getGroupUuid())) {
            return TelephonyPermissions.checkCallingOrSelfReadPhoneState(this.mContext, subscriptionInfo.getSubscriptionId(), str, str2, "getSubscriptionsInGroup") || subscriptionInfo.canManageSubscription(this.mContext, str);
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ SubscriptionInfo lambda$getSubscriptionsInGroup$9(String str, String str2, SubscriptionInfo subscriptionInfo) {
        return conditionallyRemoveIdentifiers(subscriptionInfo, str, str2, "getSubscriptionsInGroup");
    }

    public boolean checkPhoneIdAndIccIdMatch(int i, final String str) {
        List<SubscriptionInfo> subInfo;
        int subId = getSubId(i);
        if (SubscriptionManager.isUsableSubIdValue(subId)) {
            ParcelUuid groupUuid = getGroupUuid(subId);
            if (groupUuid != null) {
                subInfo = getSubInfo("group_uuid='" + groupUuid.toString() + "'", null);
            } else {
                subInfo = getSubInfo("_id=" + subId, null);
            }
            return subInfo != null && subInfo.stream().anyMatch(new Predicate() { // from class: com.android.internal.telephony.SubscriptionController$$ExternalSyntheticLambda6
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$checkPhoneIdAndIccIdMatch$10;
                    lambda$checkPhoneIdAndIccIdMatch$10 = SubscriptionController.lambda$checkPhoneIdAndIccIdMatch$10(str, (SubscriptionInfo) obj);
                    return lambda$checkPhoneIdAndIccIdMatch$10;
                }
            });
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$checkPhoneIdAndIccIdMatch$10(String str, SubscriptionInfo subscriptionInfo) {
        return IccUtils.stripTrailingFs(subscriptionInfo.getIccId()).equals(IccUtils.stripTrailingFs(str));
    }

    public ParcelUuid getGroupUuid(int i) {
        List<SubscriptionInfo> subInfo = getSubInfo("_id=" + i, null);
        if (subInfo == null || subInfo.size() == 0) {
            return null;
        }
        return subInfo.get(0).getGroupUuid();
    }

    public boolean setSubscriptionEnabled(boolean z, final int i) {
        enforceModifyPhoneState("setSubscriptionEnabled");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("setSubscriptionEnabled");
            sb.append(z ? " enable " : " disable ");
            sb.append(" subId ");
            sb.append(i);
            logd(sb.toString());
            if (!SubscriptionManager.isUsableSubscriptionId(i)) {
                throw new IllegalArgumentException("setSubscriptionEnabled not usable subId " + i);
            } else if (z == isActiveSubscriptionId(i)) {
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return true;
            } else {
                SubscriptionInfo subscriptionInfo = getInstance().getAllSubInfoList(this.mContext.getOpPackageName(), this.mContext.getAttributionTag()).stream().filter(new Predicate() { // from class: com.android.internal.telephony.SubscriptionController$$ExternalSyntheticLambda2
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$setSubscriptionEnabled$11;
                        lambda$setSubscriptionEnabled$11 = SubscriptionController.lambda$setSubscriptionEnabled$11(i, (SubscriptionInfo) obj);
                        return lambda$setSubscriptionEnabled$11;
                    }
                }).findFirst().get();
                if (subscriptionInfo != null) {
                    return subscriptionInfo.isEmbedded() ? enableEmbeddedSubscription(subscriptionInfo, z) : enablePhysicalSubscription(subscriptionInfo, z);
                }
                logd("setSubscriptionEnabled subId " + i + " doesn't exist.");
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return false;
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$setSubscriptionEnabled$11(int i, SubscriptionInfo subscriptionInfo) {
        return subscriptionInfo.getSubscriptionId() == i;
    }

    private boolean enableEmbeddedSubscription(SubscriptionInfo subscriptionInfo, boolean z) {
        enableSubscriptionOverEuiccManager(subscriptionInfo.getSubscriptionId(), z, -1);
        return false;
    }

    private boolean enablePhysicalSubscription(SubscriptionInfo subscriptionInfo, boolean z) {
        UiccSlotInfo uiccSlotInfo;
        if (subscriptionInfo == null || !SubscriptionManager.isValidSubscriptionId(subscriptionInfo.getSubscriptionId())) {
            return false;
        }
        int subscriptionId = subscriptionInfo.getSubscriptionId();
        UiccSlotInfo[] uiccSlotsInfo = this.mTelephonyManager.getUiccSlotsInfo();
        if (uiccSlotsInfo == null) {
            return false;
        }
        int i = 0;
        while (true) {
            if (i >= uiccSlotsInfo.length) {
                i = -1;
                uiccSlotInfo = null;
                break;
            }
            uiccSlotInfo = uiccSlotsInfo[i];
            if (uiccSlotInfo.getCardStateInfo() == 2 && TextUtils.equals(IccUtils.stripTrailingFs(uiccSlotInfo.getCardId()), IccUtils.stripTrailingFs(subscriptionInfo.getCardString()))) {
                break;
            }
            i++;
        }
        if (uiccSlotInfo == null) {
            loge("Can't find the existing SIM.");
            return false;
        } else if (z && !((UiccPortInfo) uiccSlotInfo.getPorts().stream().findFirst().get()).isActive()) {
            EuiccManager euiccManager = (EuiccManager) this.mContext.getSystemService("euicc");
            if (euiccManager != null && euiccManager.isEnabled()) {
                enableSubscriptionOverEuiccManager(subscriptionId, z, i);
            } else {
                if (!subscriptionInfo.areUiccApplicationsEnabled()) {
                    setUiccApplicationsEnabled(z, subscriptionId);
                }
                if (this.mTelephonyManager.isMultiSimSupported() == 0) {
                    PhoneConfigurationManager.getInstance().switchMultiSimConfig(this.mTelephonyManager.getSupportedModemCount());
                } else {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(new UiccSlotMapping(0, i, 0));
                    UiccController.getInstance().switchSlots(arrayList, null);
                }
            }
            return true;
        } else {
            setUiccApplicationsEnabled(z, subscriptionId);
            return true;
        }
    }

    private void enableSubscriptionOverEuiccManager(int i, boolean z, int i2) {
        StringBuilder sb = new StringBuilder();
        sb.append("enableSubscriptionOverEuiccManager");
        sb.append(z ? " enable " : " disable ");
        sb.append("subId ");
        sb.append(i);
        sb.append(" on slotIndex ");
        sb.append(i2);
        logdl(sb.toString());
        Intent intent = new Intent("android.telephony.euicc.action.TOGGLE_SUBSCRIPTION_PRIVILEGED");
        intent.addFlags(268435456);
        intent.putExtra("android.telephony.euicc.extra.SUBSCRIPTION_ID", i);
        intent.putExtra("android.telephony.euicc.extra.ENABLE_SUBSCRIPTION", z);
        if (i2 != -1) {
            intent.putExtra("android.telephony.euicc.extra.PHYSICAL_SLOT_ID", i2);
        }
        this.mContext.startActivity(intent);
    }

    private int getPhysicalSlotIndexFromLogicalSlotIndex(int i) {
        UiccSlotInfo[] uiccSlotsInfo = this.mTelephonyManager.getUiccSlotsInfo();
        int i2 = -1;
        for (int i3 = 0; i3 < uiccSlotsInfo.length; i3++) {
            Iterator it = uiccSlotsInfo[i3].getPorts().iterator();
            while (true) {
                if (it.hasNext()) {
                    if (((UiccPortInfo) it.next()).getLogicalSlotIndex() == i) {
                        i2 = i3;
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        return i2;
    }

    public boolean isSubscriptionEnabled(int i) {
        enforceReadPrivilegedPhoneState("isSubscriptionEnabled");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            if (!SubscriptionManager.isUsableSubscriptionId(i)) {
                throw new IllegalArgumentException("isSubscriptionEnabled not usable subId " + i);
            }
            List<SubscriptionInfo> subInfo = getSubInfo("_id=" + i, null);
            boolean z = false;
            if (subInfo != null && !subInfo.isEmpty()) {
                if (subInfo.get(0).isEmbedded()) {
                    return isActiveSubId(i);
                }
                if (isActiveSubId(i) && PhoneConfigurationManager.getInstance().getPhoneStatus(PhoneFactory.getPhone(getPhoneId(i)))) {
                    z = true;
                }
                return z;
            }
            return false;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public int getEnabledSubscriptionId(int i) {
        int subId;
        enforceReadPrivilegedPhoneState("getEnabledSubscriptionId");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            if (!SubscriptionManager.isValidPhoneId(i)) {
                throw new IllegalArgumentException("getEnabledSubscriptionId with invalid logicalSlotIndex " + i);
            }
            int physicalSlotIndexFromLogicalSlotIndex = getPhysicalSlotIndexFromLogicalSlotIndex(i);
            if (physicalSlotIndexFromLogicalSlotIndex == -1) {
                return -1;
            }
            ContentResolver contentResolver = this.mContext.getContentResolver();
            if (Settings.Global.getInt(contentResolver, "modem_stack_enabled_for_slot" + physicalSlotIndexFromLogicalSlotIndex, 1) != 1) {
                return -1;
            }
            try {
                ContentResolver contentResolver2 = this.mContext.getContentResolver();
                subId = Settings.Global.getInt(contentResolver2, "enabled_subscription_for_slot" + physicalSlotIndexFromLogicalSlotIndex);
            } catch (Settings.SettingNotFoundException unused) {
                subId = this.getSubId(i);
            }
            return subId;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x0033 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:19:0x003e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private List<SubscriptionInfo> getSubscriptionInfoListFromCacheHelper(String str, String str2, List<SubscriptionInfo> list) {
        boolean z;
        boolean z2;
        boolean z3;
        int size;
        boolean z4 = false;
        try {
            z = TelephonyPermissions.checkReadPhoneState(this.mContext, -1, Binder.getCallingPid(), Binder.getCallingUid(), str, str2, "getSubscriptionInfoList");
        } catch (SecurityException unused) {
            z = false;
            z2 = false;
        }
        if (z) {
            try {
                z2 = hasSubscriberIdentifierAccess(-1, str, str2, "getSubscriptionInfoList", false);
            } catch (SecurityException unused2) {
                z2 = false;
            }
            try {
                z3 = hasPhoneNumberAccess(-1, str, str2, "getSubscriptionInfoList");
            } catch (SecurityException unused3) {
                z3 = false;
                z4 = z2;
                if (z4) {
                }
                while (size >= 0) {
                }
                return list;
            }
            z4 = z2;
            if (z4 || !z3) {
                for (size = list.size() - 1; size >= 0; size--) {
                    SubscriptionInfo subscriptionInfo = list.get(size);
                    if (!TelephonyPermissions.checkCarrierPrivilegeForSubId(this.mContext, subscriptionInfo.getSubscriptionId())) {
                        list.remove(size);
                        if (z) {
                            list.add(size, conditionallyRemoveIdentifiers(subscriptionInfo, z4, z3));
                        }
                    }
                }
                return list;
            }
            return list;
        }
        z3 = false;
        if (z4) {
        }
        while (size >= 0) {
        }
        return list;
    }

    private SubscriptionInfo conditionallyRemoveIdentifiers(SubscriptionInfo subscriptionInfo, String str, String str2, String str3) {
        int subscriptionId = subscriptionInfo.getSubscriptionId();
        return conditionallyRemoveIdentifiers(subscriptionInfo, hasSubscriberIdentifierAccess(subscriptionId, str, str2, str3, true), hasPhoneNumberAccess(subscriptionId, str, str2, str3));
    }

    private SubscriptionInfo conditionallyRemoveIdentifiers(SubscriptionInfo subscriptionInfo, boolean z, boolean z2) {
        if (z && z2) {
            return subscriptionInfo;
        }
        SubscriptionInfo.Builder builder = new SubscriptionInfo.Builder(subscriptionInfo);
        if (!z) {
            builder.setIccId((String) null);
            builder.setCardString((String) null);
            builder.setGroupUuid((String) null);
        }
        if (!z2) {
            builder.setNumber((String) null);
        }
        return builder.build();
    }

    private synchronized boolean addToSubIdList(int i, int i2, int i3) {
        ArrayList<Integer> copy = this.mSlotIndexToSubIds.getCopy(i);
        if (copy == null) {
            copy = new ArrayList<>();
            this.mSlotIndexToSubIds.put(i, copy);
        }
        if (copy.contains(Integer.valueOf(i2))) {
            logdl("slotIndex, subId combo already exists in the map. Not adding it again.");
            return false;
        }
        if (isSubscriptionForRemoteSim(i3)) {
            this.mSlotIndexToSubIds.addToSubIdList(i, Integer.valueOf(i2));
        } else {
            this.mSlotIndexToSubIds.clearSubIdList(i);
            this.mSlotIndexToSubIds.addToSubIdList(i, Integer.valueOf(i2));
        }
        for (Map.Entry<Integer, ArrayList<Integer>> entry : this.mSlotIndexToSubIds.entrySet()) {
            if (entry.getKey().intValue() != i && entry.getValue() != null && entry.getValue().contains(Integer.valueOf(i2))) {
                logdl("addToSubIdList - remove " + entry.getKey());
                this.mSlotIndexToSubIds.remove(entry.getKey().intValue());
            }
        }
        return true;
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public Map<Integer, ArrayList<Integer>> getSlotIndexToSubIdsMap() {
        return this.mSlotIndexToSubIds.getMap();
    }

    private void notifyOpportunisticSubscriptionInfoChanged() {
        ((TelephonyRegistryManager) this.mContext.getSystemService("telephony_registry")).notifyOpportunisticSubscriptionInfoChanged();
    }

    private void refreshCachedOpportunisticSubscriptionInfoList() {
        synchronized (this.mSubInfoListLock) {
            List<SubscriptionInfo> subInfo = getSubInfo("is_opportunistic=1 AND (sim_id>=0 OR is_embedded=1)", null);
            List<SubscriptionInfo> list = this.mCacheOpportunisticSubInfoList;
            if (subInfo != null) {
                subInfo.sort(SUBSCRIPTION_INFO_COMPARATOR);
            } else {
                subInfo = new ArrayList<>();
            }
            this.mCacheOpportunisticSubInfoList = subInfo;
            for (int i = 0; i < this.mCacheOpportunisticSubInfoList.size(); i++) {
                SubscriptionInfo subscriptionInfo = this.mCacheOpportunisticSubInfoList.get(i);
                if (shouldDisableSubGroup(subscriptionInfo.getGroupUuid())) {
                    SubscriptionInfo.Builder builder = new SubscriptionInfo.Builder(subscriptionInfo);
                    builder.setGroupDisabled(true);
                    this.mCacheOpportunisticSubInfoList.set(i, builder.build());
                }
            }
            if (!list.equals(this.mCacheOpportunisticSubInfoList)) {
                this.mOpptSubInfoListChangedDirtyBit.set(true);
            }
        }
    }

    private boolean shouldDisableSubGroup(ParcelUuid parcelUuid) {
        if (parcelUuid == null) {
            return false;
        }
        synchronized (this.mSubInfoListLock) {
            for (SubscriptionInfo subscriptionInfo : this.mCacheActiveSubInfoList) {
                if (!subscriptionInfo.isOpportunistic() && parcelUuid.equals(subscriptionInfo.getGroupUuid())) {
                    return false;
                }
            }
            return true;
        }
    }

    public boolean setEnabledMobileDataPolicies(int i, String str) {
        validateSubId(i);
        ContentValues contentValues = new ContentValues(1);
        contentValues.put("enabled_mobile_data_policies", str);
        boolean z = updateDatabase(contentValues, i, true) > 0;
        if (z) {
            refreshCachedActiveSubscriptionInfoList();
            notifySubscriptionInfoChanged();
        }
        return z;
    }

    public String getEnabledMobileDataPolicies(int i) {
        return TelephonyUtils.emptyIfNull(getSubscriptionProperty(i, "enabled_mobile_data_policies"));
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

    public boolean canDisablePhysicalSubscription() {
        boolean z;
        enforceReadPrivilegedPhoneState("canToggleUiccApplicationsEnablement");
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

    public String getPhoneNumber(int i, int i2, String str, String str2) {
        TelephonyPermissions.enforceAnyPermissionGrantedOrCarrierPrivileges(this.mContext, i, Binder.getCallingUid(), "getPhoneNumber", new String[]{"android.permission.READ_PHONE_NUMBERS", "android.permission.READ_PRIVILEGED_PHONE_STATE"});
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            String phoneNumber = getPhoneNumber(i, i2);
            if (phoneNumber == null) {
                phoneNumber = PhoneConfigurationManager.SSSS;
            }
            return phoneNumber;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public String getPhoneNumberFromFirstAvailableSource(int i, String str, String str2) {
        TelephonyPermissions.enforceAnyPermissionGrantedOrCarrierPrivileges(this.mContext, i, Binder.getCallingUid(), "getPhoneNumberFromFirstAvailableSource", new String[]{"android.permission.READ_PHONE_NUMBERS", "android.permission.READ_PRIVILEGED_PHONE_STATE"});
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            String phoneNumber = getPhoneNumber(i, 2);
            if (TextUtils.isEmpty(phoneNumber)) {
                String phoneNumber2 = getPhoneNumber(i, 1);
                if (TextUtils.isEmpty(phoneNumber2)) {
                    String phoneNumber3 = getPhoneNumber(i, 3);
                    return !TextUtils.isEmpty(phoneNumber3) ? phoneNumber3 : PhoneConfigurationManager.SSSS;
                }
                return phoneNumber2;
            }
            return phoneNumber;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    private String getPhoneNumber(int i, int i2) {
        if (i2 == 1) {
            Phone phone = PhoneFactory.getPhone(getPhoneId(i));
            if (phone != null) {
                return phone.getLine1Number();
            }
            return null;
        } else if (i2 == 2) {
            return getSubscriptionProperty(i, "phone_number_source_carrier");
        } else {
            if (i2 == 3) {
                return getSubscriptionProperty(i, "phone_number_source_ims");
            }
            throw new IllegalArgumentException("setPhoneNumber doesn't accept source " + i2);
        }
    }

    public void setPhoneNumber(int i, int i2, String str, String str2, String str3) {
        if (i2 != 2) {
            throw new IllegalArgumentException("setPhoneNumber doesn't accept source " + i2);
        } else if (!TelephonyPermissions.checkCarrierPrivilegeForSubId(this.mContext, i)) {
            throw new SecurityException("setPhoneNumber for CARRIER needs carrier privilege");
        } else {
            if (str == null) {
                throw new NullPointerException("invalid number null");
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                setSubscriptionProperty(i, "phone_number_source_carrier", str);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    public int setUsageSetting(int i, int i2, String str) {
        try {
            TelephonyPermissions.enforceCallingOrSelfModifyPermissionOrCarrierPrivilege(this.mContext, i2, str);
        } catch (SecurityException unused) {
            enforceCarrierPrivilegeOnInactiveSub(i2, str, "Caller requires permission on sub " + i2);
        }
        if (i < 0 || i > 2) {
            throw new IllegalArgumentException("setUsageSetting: Invalid usage setting: " + i);
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            int subscriptionProperty = setSubscriptionProperty(i2, "usage_setting", String.valueOf(i));
            if (subscriptionProperty == 1) {
                return subscriptionProperty;
            }
            throw new IllegalArgumentException("Invalid SubscriptionId for setUsageSetting");
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:13:0x0046, code lost:
        if (r7 != null) goto L5;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public int getMessageRef(int i) {
        Cursor query = this.mContext.getContentResolver().query(SubscriptionManager.CONTENT_URI, new String[]{"tp_message_ref"}, "_id=\"" + i + "\"", null, null);
        try {
            if (query != null) {
                try {
                    if (query.moveToFirst()) {
                        int i2 = query.getInt(query.getColumnIndexOrThrow("tp_message_ref"));
                        query.close();
                        query.close();
                        return i2;
                    }
                } catch (Exception unused) {
                    query.close();
                    if (query != null) {
                        query.close();
                        return -1;
                    }
                    return -1;
                } catch (Throwable th) {
                    query.close();
                    throw th;
                }
            }
        } catch (Throwable th2) {
            if (query != null) {
                try {
                    query.close();
                } catch (Throwable th3) {
                    th2.addSuppressed(th3);
                }
            }
            throw th2;
        }
    }

    public void updateMessageRef(int i, int i2) {
        Context context = this.mContext;
        TelephonyPermissions.enforceCallingOrSelfModifyPermissionOrCarrierPrivilege(context, i, context.getOpPackageName());
        if (this.mContext == null) {
            logel("[updateMessageRef] mContext is null");
        } else if (SubscriptionManager.CONTENT_URI != null) {
            ContentValues contentValues = new ContentValues(1);
            contentValues.put("tp_message_ref", Integer.valueOf(i2));
            ContentResolver contentResolver = this.mContext.getContentResolver();
            Uri uri = SubscriptionManager.CONTENT_URI;
            contentResolver.update(uri, contentValues, "_id=\"" + i + "\"", null);
        }
    }

    public int setSubscriptionUserHandle(UserHandle userHandle, int i) {
        enforceManageSubscriptionUserAssociation("setSubscriptionUserHandle");
        if (userHandle == null) {
            userHandle = UserHandle.of(-10000);
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            int subscriptionProperty = setSubscriptionProperty(i, "user_handle", String.valueOf(userHandle.getIdentifier()));
            if (subscriptionProperty != 0) {
                notifySubscriptionInfoChanged();
                return subscriptionProperty;
            }
            throw new IllegalArgumentException("[setSubscriptionUserHandle]: Invalid subId: " + i);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public UserHandle getSubscriptionUserHandle(int i) {
        enforceManageSubscriptionUserAssociation("getSubscriptionUserHandle");
        if (SubscriptionInfoUpdater.isWorkProfileTelephonyEnabled()) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                String subscriptionProperty = getSubscriptionProperty(i, "user_handle");
                if (subscriptionProperty == null) {
                    throw new IllegalArgumentException("[getSubscriptionUserHandle]: Invalid subId: " + i);
                }
                UserHandle of = UserHandle.of(Integer.parseInt(subscriptionProperty));
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
        enforceManageSubscriptionUserAssociation("isSubscriptionAssociatedWithUser");
        if (SubscriptionInfoUpdater.isWorkProfileTelephonyEnabled()) {
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
        enforceManageSubscriptionUserAssociation("getActiveSubscriptionInfoListAssociatedWithUser");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            List<SubscriptionInfo> allSubInfoList = getAllSubInfoList(this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
            if (allSubInfoList != null && !allSubInfoList.isEmpty()) {
                if (SubscriptionInfoUpdater.isWorkProfileTelephonyEnabled()) {
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
        enforceModifyPhoneState("restoreAllSimSpecificSettingsFromBackup");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            Bundle bundle = new Bundle();
            bundle.putByteArray("KEY_SIM_SPECIFIC_SETTINGS_DATA", bArr);
            this.mContext.getContentResolver().call(SubscriptionManager.SIM_INFO_BACKUP_AND_RESTORE_CONTENT_URI, "restoreSimSpecificSettings", (String) null, bundle);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    private void setGlobalSetting(String str, int i) {
        Settings.Global.putInt(this.mContext.getContentResolver(), str, i);
        if (TextUtils.equals(str, "multi_sim_data_call")) {
            invalidateDefaultDataSubIdCaches();
            invalidateActiveDataSubIdCaches();
            invalidateDefaultSubIdCaches();
            invalidateSlotIndexCaches();
        } else if (TextUtils.equals(str, "multi_sim_voice_call")) {
            invalidateDefaultSubIdCaches();
            invalidateSlotIndexCaches();
        } else if (TextUtils.equals(str, "multi_sim_sms")) {
            invalidateDefaultSmsSubIdCaches();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void invalidateDefaultSubIdCaches() {
        SubscriptionManager.invalidateDefaultSubIdCaches();
    }

    private static void invalidateDefaultDataSubIdCaches() {
        SubscriptionManager.invalidateDefaultDataSubIdCaches();
    }

    private static void invalidateDefaultSmsSubIdCaches() {
        SubscriptionManager.invalidateDefaultSmsSubIdCaches();
    }

    private static void invalidateActiveDataSubIdCaches() {
        SubscriptionManager.invalidateActiveDataSubIdCaches();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void invalidateSlotIndexCaches() {
        SubscriptionManager.invalidateSlotIndexCaches();
    }
}
