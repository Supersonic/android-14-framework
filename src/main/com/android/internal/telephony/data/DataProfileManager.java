package com.android.internal.telephony.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Telephony;
import android.telephony.AnomalyReporter;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.data.ApnSetting;
import android.telephony.data.DataProfile;
import android.telephony.data.TrafficDescriptor;
import android.text.TextUtils;
import android.util.ArraySet;
import com.android.internal.telephony.AndroidUtilIndentingPrintWriter;
import com.android.internal.telephony.LocalLog;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.data.DataConfigManager;
import com.android.internal.telephony.data.DataNetworkController;
import com.android.internal.telephony.data.DataProfileManager;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class DataProfileManager extends Handler {
    private final boolean FORCED_UPDATE_IA;
    private final boolean ONLY_UPDATE_IA_IF_CHANGED;
    private final List<DataProfile> mAllDataProfiles;
    private final DataConfigManager mDataConfigManager;
    private final DataNetworkController mDataNetworkController;
    private final Set<DataProfileManagerCallback> mDataProfileManagerCallbacks;
    private DataProfile mInitialAttachDataProfile;
    private DataProfile mLastInternetDataProfile;
    private final LocalLog mLocalLog;
    private final String mLogTag;
    private final Phone mPhone;
    private DataProfile mPreferredDataProfile;
    private int mPreferredDataProfileSetId;
    private final DataServiceManager mWwanDataServiceManager;

    /* loaded from: classes.dex */
    public static abstract class DataProfileManagerCallback extends DataCallback {
        public abstract void onDataProfilesChanged();

        public DataProfileManagerCallback(Executor executor) {
            super(executor);
        }
    }

    public DataProfileManager(Phone phone, DataNetworkController dataNetworkController, DataServiceManager dataServiceManager, Looper looper, DataProfileManagerCallback dataProfileManagerCallback) {
        super(looper);
        this.mLocalLog = new LocalLog(128);
        this.FORCED_UPDATE_IA = true;
        this.ONLY_UPDATE_IA_IF_CHANGED = false;
        this.mAllDataProfiles = new ArrayList();
        this.mInitialAttachDataProfile = null;
        this.mPreferredDataProfile = null;
        this.mLastInternetDataProfile = null;
        this.mPreferredDataProfileSetId = 0;
        ArraySet arraySet = new ArraySet();
        this.mDataProfileManagerCallbacks = arraySet;
        this.mPhone = phone;
        this.mLogTag = "DPM-" + phone.getPhoneId();
        this.mDataNetworkController = dataNetworkController;
        this.mWwanDataServiceManager = dataServiceManager;
        this.mDataConfigManager = dataNetworkController.getDataConfigManager();
        arraySet.add(dataProfileManagerCallback);
        registerAllEvents();
    }

    private void registerAllEvents() {
        this.mDataNetworkController.registerDataNetworkControllerCallback(new DataNetworkController.DataNetworkControllerCallback(new Executor() { // from class: com.android.internal.telephony.data.DataProfileManager$$ExternalSyntheticLambda4
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                DataProfileManager.this.post(runnable);
            }
        }) { // from class: com.android.internal.telephony.data.DataProfileManager.1
            @Override // com.android.internal.telephony.data.DataNetworkController.DataNetworkControllerCallback
            public void onInternetDataNetworkConnected(List<DataProfile> list) {
                DataProfileManager.this.onInternetDataNetworkConnected(list);
            }
        });
        this.mDataConfigManager.registerCallback(new DataConfigManager.DataConfigManagerCallback(new Executor() { // from class: com.android.internal.telephony.data.DataProfileManager$$ExternalSyntheticLambda4
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                DataProfileManager.this.post(runnable);
            }
        }) { // from class: com.android.internal.telephony.data.DataProfileManager.2
            @Override // com.android.internal.telephony.data.DataConfigManager.DataConfigManagerCallback
            public void onCarrierConfigChanged() {
                DataProfileManager.this.onCarrierConfigUpdated();
            }
        });
        this.mPhone.getContext().getContentResolver().registerContentObserver(Telephony.Carriers.CONTENT_URI, true, new ContentObserver(this) { // from class: com.android.internal.telephony.data.DataProfileManager.3
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                super.onChange(z);
                DataProfileManager.this.sendEmptyMessage(2);
            }
        });
        this.mPhone.mCi.registerForIccRefresh(this, 3, null);
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i = message.what;
        if (i == 2) {
            log("Update data profiles due to APN db updated.");
            updateDataProfiles(false);
        } else if (i == 3) {
            log("Update data profiles due to SIM refresh.");
            updateDataProfiles(true);
        } else {
            loge("Unexpected event " + message);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onCarrierConfigUpdated() {
        log("Update data profiles due to carrier config updated.");
        updateDataProfiles(true);
    }

    private DataProfile getEnterpriseDataProfile() {
        Cursor query = this.mPhone.getContext().getContentResolver().query(Telephony.Carriers.DPC_URI, null, null, null, null);
        if (query == null) {
            loge("Cannot access APN database through telephony provider.");
            return null;
        }
        DataProfile dataProfile = null;
        while (query.moveToNext()) {
            ApnSetting makeApnSetting = ApnSetting.makeApnSetting(query);
            if (makeApnSetting != null) {
                dataProfile = new DataProfile.Builder().setApnSetting(makeApnSetting).setTrafficDescriptor(new TrafficDescriptor(makeApnSetting.getApnName(), null)).setPreferred(false).build();
                if (dataProfile.canSatisfy(29)) {
                    break;
                }
            }
        }
        query.close();
        return dataProfile;
    }

    private void updateDataProfiles(boolean z) {
        DataProfile enterpriseDataProfile;
        ArrayList arrayList = new ArrayList();
        boolean z2 = false;
        if (this.mDataConfigManager.isConfigCarrierSpecific()) {
            Cursor query = this.mPhone.getContext().getContentResolver().query(Uri.withAppendedPath(Telephony.Carriers.SIM_APN_URI, "filtered/subId/" + this.mPhone.getSubId()), null, null, null, "_id");
            if (query == null) {
                loge("Cannot access APN database through telephony provider.");
                return;
            }
            boolean z3 = false;
            while (query.moveToNext()) {
                ApnSetting makeApnSetting = ApnSetting.makeApnSetting(query);
                if (makeApnSetting != null) {
                    DataProfile build = new DataProfile.Builder().setApnSetting(makeApnSetting).setTrafficDescriptor(new TrafficDescriptor(makeApnSetting.getApnName(), null)).setPreferred(false).build();
                    arrayList.add(build);
                    log("Added " + build);
                    z3 |= makeApnSetting.canHandleType(17);
                    if (this.mDataConfigManager.isApnConfigAnomalyReportEnabled()) {
                        checkApnSetting(makeApnSetting);
                    }
                }
            }
            query.close();
            if (!z3 && !arrayList.isEmpty() && this.mDataConfigManager.isApnConfigAnomalyReportEnabled()) {
                reportAnomaly("Carrier doesn't support internet.", "9af73e18-b523-4dc5-adab-363eb6613305");
            }
        }
        if (!arrayList.isEmpty() && arrayList.stream().filter(new Predicate() { // from class: com.android.internal.telephony.data.DataProfileManager$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean canSatisfy;
                canSatisfy = ((DataProfile) obj).canSatisfy(4);
                return canSatisfy;
            }
        }).findFirst().orElse(null) == null) {
            arrayList.add(new DataProfile.Builder().setApnSetting(buildDefaultApnSetting("DEFAULT IMS", "ims", 64)).setTrafficDescriptor(new TrafficDescriptor("ims", null)).build());
            log("Added default IMS data profile.");
        }
        if (arrayList.stream().filter(new Predicate() { // from class: com.android.internal.telephony.data.DataProfileManager$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean canSatisfy;
                canSatisfy = ((DataProfile) obj).canSatisfy(29);
                return canSatisfy;
            }
        }).findFirst().orElse(null) == null && (enterpriseDataProfile = getEnterpriseDataProfile()) != null) {
            arrayList.add(enterpriseDataProfile);
            log("Added enterprise profile " + enterpriseDataProfile);
        }
        if (arrayList.stream().filter(new Predicate() { // from class: com.android.internal.telephony.data.DataProfileManager$$ExternalSyntheticLambda2
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean canSatisfy;
                canSatisfy = ((DataProfile) obj).canSatisfy(10);
                return canSatisfy;
            }
        }).findFirst().orElse(null) == null) {
            arrayList.add(new DataProfile.Builder().setApnSetting(buildDefaultApnSetting("DEFAULT EIMS", "sos", 512)).setTrafficDescriptor(new TrafficDescriptor("sos", null)).build());
            log("Added default EIMS data profile.");
        }
        dedupeDataProfiles(arrayList);
        if (this.mDataConfigManager.isApnConfigAnomalyReportEnabled()) {
            checkDataProfiles(arrayList);
        }
        log("Found " + arrayList.size() + " data profiles. profiles = " + arrayList);
        boolean z4 = true;
        if (this.mAllDataProfiles.size() != arrayList.size() || !this.mAllDataProfiles.containsAll(arrayList)) {
            log("Data profiles changed.");
            this.mAllDataProfiles.clear();
            this.mAllDataProfiles.addAll(arrayList);
            z2 = true;
        }
        boolean updatePreferredDataProfile = updatePreferredDataProfile() | z2;
        int preferredDataProfileSetId = getPreferredDataProfileSetId();
        if (preferredDataProfileSetId != this.mPreferredDataProfileSetId) {
            logl("Changed preferred data profile set id to " + preferredDataProfileSetId);
            this.mPreferredDataProfileSetId = preferredDataProfileSetId;
        } else {
            z4 = updatePreferredDataProfile;
        }
        updateDataProfilesAtModem();
        updateInitialAttachDataProfileAtModem(z);
        if (z4) {
            this.mDataProfileManagerCallbacks.forEach(new Consumer() { // from class: com.android.internal.telephony.data.DataProfileManager$$ExternalSyntheticLambda3
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    DataProfileManager.lambda$updateDataProfiles$3((DataProfileManager.DataProfileManagerCallback) obj);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$updateDataProfiles$3(final DataProfileManagerCallback dataProfileManagerCallback) {
        Objects.requireNonNull(dataProfileManagerCallback);
        dataProfileManagerCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.data.DataProfileManager$$ExternalSyntheticLambda20
            @Override // java.lang.Runnable
            public final void run() {
                DataProfileManager.DataProfileManagerCallback.this.onDataProfilesChanged();
            }
        });
    }

    private int getPreferredDataProfileSetId() {
        Cursor query = this.mPhone.getContext().getContentResolver().query(Uri.withAppendedPath(Telephony.Carriers.PREFERRED_APN_SET_URI, String.valueOf(this.mPhone.getSubId())), new String[]{"apn_set_id"}, null, null, null);
        int i = 0;
        if (query == null) {
            log("getPreferredDataProfileSetId: cursor is null");
            return 0;
        }
        if (query.getCount() < 1) {
            loge("getPreferredDataProfileSetId: no APNs found");
        } else {
            query.moveToFirst();
            i = query.getInt(query.getColumnIndexOrThrow("apn_set_id"));
        }
        query.close();
        return i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onInternetDataNetworkConnected(List<DataProfile> list) {
        DataProfile orElse = list.stream().max(Comparator.comparingLong(new ToLongFunction() { // from class: com.android.internal.telephony.data.DataProfileManager$$ExternalSyntheticLambda18
            @Override // java.util.function.ToLongFunction
            public final long applyAsLong(Object obj) {
                return ((DataProfile) obj).getLastSetupTimestamp();
            }
        }).reversed()).orElse(null);
        this.mLastInternetDataProfile = orElse;
        if (this.mPreferredDataProfile != null) {
            return;
        }
        setPreferredDataProfile(orElse);
        updateDataProfiles(false);
    }

    private DataProfile getPreferredDataProfileFromDb() {
        Cursor query = this.mPhone.getContext().getContentResolver().query(Uri.withAppendedPath(Telephony.Carriers.PREFERRED_APN_URI, String.valueOf(this.mPhone.getSubId())), null, null, null, "name ASC");
        DataProfile dataProfile = null;
        if (query != null) {
            if (query.getCount() > 0) {
                query.moveToFirst();
                final int i = query.getInt(query.getColumnIndexOrThrow("_id"));
                dataProfile = this.mAllDataProfiles.stream().filter(new Predicate() { // from class: com.android.internal.telephony.data.DataProfileManager$$ExternalSyntheticLambda14
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$getPreferredDataProfileFromDb$4;
                        lambda$getPreferredDataProfileFromDb$4 = DataProfileManager.lambda$getPreferredDataProfileFromDb$4(i, (DataProfile) obj);
                        return lambda$getPreferredDataProfileFromDb$4;
                    }
                }).findFirst().orElse(null);
            }
            query.close();
        }
        log("getPreferredDataProfileFromDb: " + dataProfile);
        return dataProfile;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getPreferredDataProfileFromDb$4(int i, DataProfile dataProfile) {
        return dataProfile.getApnSetting() != null && dataProfile.getApnSetting().getId() == i;
    }

    private DataProfile getPreferredDataProfileFromConfig() {
        final String defaultPreferredApn = this.mDataConfigManager.getDefaultPreferredApn();
        if (TextUtils.isEmpty(defaultPreferredApn)) {
            return null;
        }
        return this.mAllDataProfiles.stream().filter(new Predicate() { // from class: com.android.internal.telephony.data.DataProfileManager$$ExternalSyntheticLambda15
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getPreferredDataProfileFromConfig$5;
                lambda$getPreferredDataProfileFromConfig$5 = DataProfileManager.lambda$getPreferredDataProfileFromConfig$5(defaultPreferredApn, (DataProfile) obj);
                return lambda$getPreferredDataProfileFromConfig$5;
            }
        }).findFirst().orElse(null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getPreferredDataProfileFromConfig$5(String str, DataProfile dataProfile) {
        return dataProfile.getApnSetting() != null && str.equals(dataProfile.getApnSetting().getApnName());
    }

    private void setPreferredDataProfile(DataProfile dataProfile) {
        log("setPreferredDataProfile: " + dataProfile);
        Uri withAppendedPath = Uri.withAppendedPath(Telephony.Carriers.PREFERRED_APN_URI, Long.toString((long) this.mPhone.getSubId()));
        ContentResolver contentResolver = this.mPhone.getContext().getContentResolver();
        contentResolver.delete(withAppendedPath, null, null);
        if (dataProfile == null || dataProfile.getApnSetting() == null) {
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("apn_id", Integer.valueOf(dataProfile.getApnSetting().getId()));
        contentResolver.insert(withAppendedPath, contentValues);
    }

    private boolean updatePreferredDataProfile() {
        DataProfile dataProfile = null;
        if (SubscriptionManager.isValidSubscriptionId(this.mPhone.getSubId())) {
            DataProfile preferredDataProfileFromDb = getPreferredDataProfileFromDb();
            if (preferredDataProfileFromDb == null) {
                preferredDataProfileFromDb = getPreferredDataProfileFromConfig();
                if (preferredDataProfileFromDb != null) {
                    setPreferredDataProfile(preferredDataProfileFromDb);
                } else {
                    dataProfile = this.mAllDataProfiles.stream().filter(new Predicate() { // from class: com.android.internal.telephony.data.DataProfileManager$$ExternalSyntheticLambda19
                        @Override // java.util.function.Predicate
                        public final boolean test(Object obj) {
                            boolean lambda$updatePreferredDataProfile$6;
                            lambda$updatePreferredDataProfile$6 = DataProfileManager.this.lambda$updatePreferredDataProfile$6((DataProfile) obj);
                            return lambda$updatePreferredDataProfile$6;
                        }
                    }).findFirst().orElse(null);
                    if (dataProfile != null) {
                        log("updatePreferredDataProfile: preferredDB is empty and no carrier default configured, setting preferred to be prev internet DP.");
                        setPreferredDataProfile(dataProfile);
                    }
                }
            }
            dataProfile = preferredDataProfileFromDb;
        }
        for (DataProfile dataProfile2 : this.mAllDataProfiles) {
            dataProfile2.setPreferred(dataProfile2.equals(dataProfile));
        }
        if (Objects.equals(this.mPreferredDataProfile, dataProfile)) {
            return false;
        }
        this.mPreferredDataProfile = dataProfile;
        logl("Changed preferred data profile to " + this.mPreferredDataProfile);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$updatePreferredDataProfile$6(DataProfile dataProfile) {
        return lambda$isDataProfileCompatible$13(dataProfile, this.mLastInternetDataProfile);
    }

    private void updateInitialAttachDataProfileAtModem(boolean z) {
        List list = (List) this.mAllDataProfiles.stream().sorted(Comparator.comparing(new Function() { // from class: com.android.internal.telephony.data.DataProfileManager$$ExternalSyntheticLambda16
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Boolean lambda$updateInitialAttachDataProfileAtModem$7;
                lambda$updateInitialAttachDataProfileAtModem$7 = DataProfileManager.this.lambda$updateInitialAttachDataProfileAtModem$7((DataProfile) obj);
                return lambda$updateInitialAttachDataProfileAtModem$7;
            }
        })).collect(Collectors.toList());
        DataProfile dataProfile = null;
        for (Integer num : this.mDataConfigManager.getAllowedInitialAttachApnTypes()) {
            final int intValue = num.intValue();
            dataProfile = (DataProfile) list.stream().filter(new Predicate() { // from class: com.android.internal.telephony.data.DataProfileManager$$ExternalSyntheticLambda17
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$updateInitialAttachDataProfileAtModem$8;
                    lambda$updateInitialAttachDataProfileAtModem$8 = DataProfileManager.lambda$updateInitialAttachDataProfileAtModem$8(intValue, (DataProfile) obj);
                    return lambda$updateInitialAttachDataProfileAtModem$8;
                }
            }).findFirst().orElse(null);
            if (dataProfile != null) {
                break;
            }
        }
        if (z || !Objects.equals(this.mInitialAttachDataProfile, dataProfile)) {
            this.mInitialAttachDataProfile = dataProfile;
            logl("Initial attach data profile updated as " + this.mInitialAttachDataProfile + " or forceUpdateIa= " + z);
            DataProfile dataProfile2 = this.mInitialAttachDataProfile;
            if (dataProfile2 != null) {
                this.mWwanDataServiceManager.setInitialAttachApn(dataProfile2, this.mPhone.getServiceState().getDataRoamingFromRegistration(), null);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$updateInitialAttachDataProfileAtModem$7(DataProfile dataProfile) {
        return Boolean.valueOf(!dataProfile.equals(this.mPreferredDataProfile));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateInitialAttachDataProfileAtModem$8(int i, DataProfile dataProfile) {
        return dataProfile.canSatisfy(DataUtils.apnTypeToNetworkCapability(i));
    }

    private void updateDataProfilesAtModem() {
        log("updateDataProfilesAtModem: set " + this.mAllDataProfiles.size() + " data profiles.");
        this.mWwanDataServiceManager.setDataProfile(this.mAllDataProfiles, this.mPhone.getServiceState().getDataRoamingFromRegistration(), null);
    }

    private ApnSetting buildDefaultApnSetting(String str, String str2, int i) {
        return new ApnSetting.Builder().setEntryName(str).setProtocol(2).setRoamingProtocol(2).setApnName(str2).setApnTypeBitmask(i).setCarrierEnabled(true).setApnSetId(-1).build();
    }

    public DataProfile getDataProfileForNetworkRequest(TelephonyNetworkRequest telephonyNetworkRequest, int i, boolean z) {
        TrafficDescriptor.OsAppId osAppId;
        ApnSetting apnSettingForNetworkRequest = telephonyNetworkRequest.hasAttribute(1) ? getApnSettingForNetworkRequest(telephonyNetworkRequest, i, z) : null;
        TrafficDescriptor.Builder builder = new TrafficDescriptor.Builder();
        if (telephonyNetworkRequest.hasAttribute(2) && apnSettingForNetworkRequest != null) {
            builder.setDataNetworkName(apnSettingForNetworkRequest.getApnName());
        }
        if (telephonyNetworkRequest.hasAttribute(4) && (osAppId = telephonyNetworkRequest.getOsAppId()) != null) {
            builder.setOsAppId(osAppId.getBytes());
        }
        try {
            TrafficDescriptor build = builder.build();
            for (DataProfile dataProfile : this.mAllDataProfiles) {
                if (Objects.equals(apnSettingForNetworkRequest, dataProfile.getApnSetting()) && build.equals(dataProfile.getTrafficDescriptor())) {
                    return dataProfile;
                }
            }
            DataProfile.Builder builder2 = new DataProfile.Builder();
            if (apnSettingForNetworkRequest != null) {
                builder2.setApnSetting(apnSettingForNetworkRequest);
            }
            builder2.setTrafficDescriptor(build);
            DataProfile build2 = builder2.build();
            log("Created data profile " + build2 + " for " + telephonyNetworkRequest);
            return build2;
        } catch (IllegalArgumentException unused) {
            log("Unable to find a data profile for " + telephonyNetworkRequest);
            return null;
        }
    }

    private ApnSetting getApnSettingForNetworkRequest(final TelephonyNetworkRequest telephonyNetworkRequest, final int i, final boolean z) {
        if (!telephonyNetworkRequest.hasAttribute(1)) {
            loge("Network request does not have APN setting attribute.");
            return null;
        }
        DataProfile dataProfile = this.mPreferredDataProfile;
        if (dataProfile != null && telephonyNetworkRequest.canBeSatisfiedBy(dataProfile) && this.mPreferredDataProfile.getApnSetting() != null && this.mPreferredDataProfile.getApnSetting().canSupportNetworkType(i)) {
            if (z || !this.mPreferredDataProfile.getApnSetting().getPermanentFailed()) {
                return this.mPreferredDataProfile.getApnSetting();
            }
            log("The preferred data profile is permanently failed. Only condition based retry can happen.");
            return null;
        }
        List<DataProfile> list = (List) this.mAllDataProfiles.stream().filter(new Predicate() { // from class: com.android.internal.telephony.data.DataProfileManager$$ExternalSyntheticLambda5
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return TelephonyNetworkRequest.this.canBeSatisfiedBy((DataProfile) obj);
            }
        }).sorted(Comparator.comparing(new Function() { // from class: com.android.internal.telephony.data.DataProfileManager$$ExternalSyntheticLambda6
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Long.valueOf(((DataProfile) obj).getLastSetupTimestamp());
            }
        })).collect(Collectors.toList());
        for (DataProfile dataProfile2 : list) {
            logv("Satisfied profile: " + dataProfile2 + ", last setup=" + DataUtils.elapsedTimeToString(dataProfile2.getLastSetupTimestamp()));
        }
        if (list.size() == 0) {
            log("Can't find any data profile that can satisfy " + telephonyNetworkRequest);
            return null;
        }
        List list2 = (List) list.stream().filter(new Predicate() { // from class: com.android.internal.telephony.data.DataProfileManager$$ExternalSyntheticLambda7
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getApnSettingForNetworkRequest$9;
                lambda$getApnSettingForNetworkRequest$9 = DataProfileManager.lambda$getApnSettingForNetworkRequest$9(i, (DataProfile) obj);
                return lambda$getApnSettingForNetworkRequest$9;
            }
        }).collect(Collectors.toList());
        if (list2.size() == 0) {
            log("Can't find any data profile for network type " + TelephonyManager.getNetworkTypeName(i));
            return null;
        }
        List list3 = (List) list2.stream().filter(new Predicate() { // from class: com.android.internal.telephony.data.DataProfileManager$$ExternalSyntheticLambda8
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getApnSettingForNetworkRequest$10;
                lambda$getApnSettingForNetworkRequest$10 = DataProfileManager.this.lambda$getApnSettingForNetworkRequest$10((DataProfile) obj);
                return lambda$getApnSettingForNetworkRequest$10;
            }
        }).collect(Collectors.toList());
        if (list3.size() == 0) {
            log("Can't find any data profile has APN set id matched. mPreferredDataProfileSetId=" + this.mPreferredDataProfileSetId);
            return null;
        }
        List list4 = (List) list3.stream().filter(new Predicate() { // from class: com.android.internal.telephony.data.DataProfileManager$$ExternalSyntheticLambda9
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getApnSettingForNetworkRequest$11;
                lambda$getApnSettingForNetworkRequest$11 = DataProfileManager.lambda$getApnSettingForNetworkRequest$11(z, (DataProfile) obj);
                return lambda$getApnSettingForNetworkRequest$11;
            }
        }).collect(Collectors.toList());
        if (list4.size() == 0) {
            log("The suitable data profiles are all in permanent failed state.");
            return null;
        }
        return ((DataProfile) list4.get(0)).getApnSetting();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getApnSettingForNetworkRequest$9(int i, DataProfile dataProfile) {
        return dataProfile.getApnSetting() != null && dataProfile.getApnSetting().canSupportNetworkType(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$getApnSettingForNetworkRequest$10(DataProfile dataProfile) {
        return dataProfile.getApnSetting() != null && (dataProfile.getApnSetting().getApnSetId() == -1 || dataProfile.getApnSetting().getApnSetId() == this.mPreferredDataProfileSetId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getApnSettingForNetworkRequest$11(boolean z, DataProfile dataProfile) {
        return z || !dataProfile.getApnSetting().getPermanentFailed();
    }

    public boolean isDataProfilePreferred(DataProfile dataProfile) {
        return lambda$isDataProfileCompatible$13(dataProfile, this.mPreferredDataProfile);
    }

    public boolean isTetheringDataProfileExisting(int i) {
        return ((this.mDataConfigManager.isTetheringProfileDisabledForRoaming() && this.mPhone.getServiceState().getDataRoaming()) || getDataProfileForNetworkRequest(new TelephonyNetworkRequest(new NetworkRequest.Builder().addCapability(2).build(), this.mPhone), i, true) == null) ? false : true;
    }

    public boolean isAnyPreferredDataProfileExisting() {
        for (DataProfile dataProfile : this.mAllDataProfiles) {
            if (dataProfile.isPreferred()) {
                return true;
            }
        }
        return false;
    }

    private void dedupeDataProfiles(List<DataProfile> list) {
        int i = 0;
        while (i < list.size() - 1) {
            DataProfile dataProfile = list.get(i);
            int i2 = i + 1;
            int i3 = i2;
            while (i3 < list.size()) {
                DataProfile dataProfile2 = list.get(i3);
                DataProfile mergeDataProfiles = mergeDataProfiles(dataProfile, dataProfile2);
                if (mergeDataProfiles != null) {
                    log("Created a merged profile " + mergeDataProfiles + " from " + dataProfile + " and " + dataProfile2);
                    StringBuilder sb = new StringBuilder();
                    sb.append("Merging data profiles will not be supported anymore. Please directly configure the merged profile ");
                    sb.append(mergeDataProfiles);
                    sb.append(" in the APN config.");
                    loge(sb.toString());
                    list.set(i, mergeDataProfiles);
                    list.remove(i3);
                } else {
                    i3++;
                }
            }
            i = i2;
        }
    }

    private void checkApnSetting(ApnSetting apnSetting) {
        if (apnSetting.canHandleType(2)) {
            if (apnSetting.getMmsc() == null) {
                reportAnomaly("MMS is supported but no MMSC configured " + apnSetting, "9af73e18-b523-4dc5-adab-19d86c6a3685");
            } else if (!apnSetting.getMmsc().toString().matches("^https?:\\/\\/.+")) {
                reportAnomaly("Apn config mmsc should start with http but is " + apnSetting.getMmsc(), "9af73e18-b523-4dc5-adab-ec754d959d4d");
            }
            if (TextUtils.isEmpty(apnSetting.getMmsProxyAddressAsString()) || !apnSetting.getMmsProxyAddressAsString().matches("^https?:\\/\\/.+")) {
                return;
            }
            reportAnomaly("Apn config mmsc_proxy should NOT start with http but is " + apnSetting.getMmsc(), "9af73e18-b523-4dc5-adab-ec754d959d4d");
        }
    }

    private void checkDataProfiles(List<DataProfile> list) {
        for (int i = 0; i < list.size(); i++) {
            ApnSetting apnSetting = list.get(i).getApnSetting();
            if (apnSetting != null) {
                if (0 != apnSetting.getLingeringNetworkTypeBitmask() && (apnSetting.getNetworkTypeBitmask() | apnSetting.getLingeringNetworkTypeBitmask()) != apnSetting.getLingeringNetworkTypeBitmask()) {
                    reportAnomaly("Apn[" + apnSetting.getApnName() + "] network " + TelephonyManager.convertNetworkTypeBitmaskToString(apnSetting.getNetworkTypeBitmask()) + " should be a subset of the lingering network " + TelephonyManager.convertNetworkTypeBitmaskToString(apnSetting.getLingeringNetworkTypeBitmask()), "9af73e18-b523-4dc5-adab-4bb24355d838");
                }
                for (int i2 = i + 1; i2 < list.size(); i2++) {
                    ApnSetting apnSetting2 = list.get(i2).getApnSetting();
                    if (apnSetting2 != null && TextUtils.equals(apnSetting.getApnName(), apnSetting2.getApnName())) {
                        if (apnSetting.getNetworkTypeBitmask() != 0 && apnSetting2.getNetworkTypeBitmask() != 0) {
                            if ((apnSetting2.getNetworkTypeBitmask() & apnSetting.getNetworkTypeBitmask()) == 0) {
                            }
                        }
                        reportAnomaly("Found overlapped network type under the APN name " + apnSetting.getApnName(), "9af73e18-b523-4dc5-adab-4bb24555d839");
                    }
                }
            }
        }
    }

    private static DataProfile mergeDataProfiles(DataProfile dataProfile, DataProfile dataProfile2) {
        Objects.requireNonNull(dataProfile);
        Objects.requireNonNull(dataProfile2);
        if (Objects.equals(dataProfile.getTrafficDescriptor(), dataProfile2.getTrafficDescriptor()) && dataProfile.getApnSetting() != null && dataProfile2.getApnSetting() != null && dataProfile.getApnSetting().similar(dataProfile2.getApnSetting())) {
            ApnSetting apnSetting = dataProfile.getApnSetting();
            ApnSetting apnSetting2 = dataProfile2.getApnSetting();
            ApnSetting.Builder builder = new ApnSetting.Builder();
            builder.setId(apnSetting.getId());
            builder.setEntryName(apnSetting.getEntryName());
            if (apnSetting2.canHandleType(17) && !apnSetting.canHandleType(17)) {
                builder.setId(apnSetting2.getId());
                builder.setEntryName(apnSetting2.getEntryName());
            }
            builder.setProxyAddress(TextUtils.isEmpty(apnSetting2.getProxyAddressAsString()) ? apnSetting.getProxyAddressAsString() : apnSetting2.getProxyAddressAsString());
            builder.setProxyPort(apnSetting2.getProxyPort() == -1 ? apnSetting.getProxyPort() : apnSetting2.getProxyPort());
            builder.setMmsc(apnSetting2.getMmsc() == null ? apnSetting.getMmsc() : apnSetting2.getMmsc());
            builder.setMmsProxyAddress(TextUtils.isEmpty(apnSetting2.getMmsProxyAddressAsString()) ? apnSetting.getMmsProxyAddressAsString() : apnSetting2.getMmsProxyAddressAsString());
            builder.setMmsProxyPort(apnSetting2.getMmsProxyPort() == -1 ? apnSetting.getMmsProxyPort() : apnSetting2.getMmsProxyPort());
            builder.setUser(TextUtils.isEmpty(apnSetting2.getUser()) ? apnSetting.getUser() : apnSetting2.getUser());
            builder.setPassword(TextUtils.isEmpty(apnSetting2.getPassword()) ? apnSetting.getPassword() : apnSetting2.getPassword());
            builder.setAuthType(apnSetting2.getAuthType() == -1 ? apnSetting.getAuthType() : apnSetting2.getAuthType());
            builder.setApnTypeBitmask(apnSetting.getApnTypeBitmask() | apnSetting2.getApnTypeBitmask());
            builder.setMtuV4(apnSetting2.getMtuV4() <= 0 ? apnSetting.getMtuV4() : apnSetting2.getMtuV4());
            builder.setMtuV6(apnSetting2.getMtuV6() <= 0 ? apnSetting.getMtuV6() : apnSetting2.getMtuV6());
            builder.setMvnoType(apnSetting.getMvnoType());
            builder.setMvnoMatchData(apnSetting.getMvnoMatchData());
            builder.setApnName(apnSetting.getApnName());
            builder.setProtocol(apnSetting.getProtocol());
            builder.setRoamingProtocol(apnSetting.getRoamingProtocol());
            builder.setCarrierEnabled(apnSetting.isEnabled());
            builder.setNetworkTypeBitmask(apnSetting.getNetworkTypeBitmask());
            builder.setLingeringNetworkTypeBitmask(apnSetting.getLingeringNetworkTypeBitmask());
            builder.setProfileId(apnSetting.getProfileId());
            builder.setPersistent(apnSetting.isPersistent());
            builder.setMaxConns(apnSetting.getMaxConns());
            builder.setWaitTime(apnSetting.getWaitTime());
            builder.setMaxConnsTime(apnSetting.getMaxConnsTime());
            builder.setApnSetId(apnSetting.getApnSetId());
            builder.setCarrierId(apnSetting.getCarrierId());
            builder.setSkip464Xlat(apnSetting.getSkip464Xlat());
            builder.setAlwaysOn(apnSetting.isAlwaysOn());
            return new DataProfile.Builder().setApnSetting(builder.build()).setTrafficDescriptor(dataProfile.getTrafficDescriptor()).build();
        }
        return null;
    }

    public boolean isDataProfileCompatible(final DataProfile dataProfile) {
        if (dataProfile == null) {
            return false;
        }
        if (dataProfile.getApnSetting() != null || dataProfile.getTrafficDescriptor() == null) {
            return this.mAllDataProfiles.stream().filter(new Predicate() { // from class: com.android.internal.telephony.data.DataProfileManager$$ExternalSyntheticLambda10
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$isDataProfileCompatible$12;
                    lambda$isDataProfileCompatible$12 = DataProfileManager.this.lambda$isDataProfileCompatible$12((DataProfile) obj);
                    return lambda$isDataProfileCompatible$12;
                }
            }).anyMatch(new Predicate() { // from class: com.android.internal.telephony.data.DataProfileManager$$ExternalSyntheticLambda11
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$isDataProfileCompatible$13;
                    lambda$isDataProfileCompatible$13 = DataProfileManager.this.lambda$isDataProfileCompatible$13(dataProfile, (DataProfile) obj);
                    return lambda$isDataProfileCompatible$13;
                }
            });
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$isDataProfileCompatible$12(DataProfile dataProfile) {
        return dataProfile.getApnSetting() != null && (dataProfile.getApnSetting().getApnSetId() == -1 || dataProfile.getApnSetting().getApnSetId() == this.mPreferredDataProfileSetId);
    }

    /* renamed from: areDataProfilesSharingApn */
    public boolean lambda$isDataProfileCompatible$13(DataProfile dataProfile, DataProfile dataProfile2) {
        return (dataProfile == null || dataProfile2 == null || dataProfile.getApnSetting() == null || !dataProfile.getApnSetting().equals(dataProfile2.getApnSetting(), this.mPhone.getServiceState().getDataRoamingFromRegistration())) ? false : true;
    }

    public void registerCallback(DataProfileManagerCallback dataProfileManagerCallback) {
        this.mDataProfileManagerCallbacks.add(dataProfileManagerCallback);
    }

    public void unregisterCallback(DataProfileManagerCallback dataProfileManagerCallback) {
        this.mDataProfileManagerCallbacks.remove(dataProfileManagerCallback);
    }

    private void reportAnomaly(String str, String str2) {
        logl(str);
        AnomalyReporter.reportAnomaly(UUID.fromString(str2), str, this.mPhone.getCarrierId());
    }

    private void log(String str) {
        Rlog.d(this.mLogTag, str);
    }

    private void loge(String str) {
        Rlog.e(this.mLogTag, str);
    }

    private void logv(String str) {
        Rlog.v(this.mLogTag, str);
    }

    private void logl(String str) {
        log(str);
        this.mLocalLog.log(str);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        final AndroidUtilIndentingPrintWriter androidUtilIndentingPrintWriter = new AndroidUtilIndentingPrintWriter(printWriter, "  ");
        androidUtilIndentingPrintWriter.println(DataProfileManager.class.getSimpleName() + "-" + this.mPhone.getPhoneId() + ":");
        androidUtilIndentingPrintWriter.increaseIndent();
        androidUtilIndentingPrintWriter.println("Data profiles for the current carrier:");
        androidUtilIndentingPrintWriter.increaseIndent();
        for (DataProfile dataProfile : this.mAllDataProfiles) {
            androidUtilIndentingPrintWriter.print(dataProfile);
            androidUtilIndentingPrintWriter.println(", last setup time: " + DataUtils.elapsedTimeToString(dataProfile.getLastSetupTimestamp()));
        }
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.println("Preferred data profile=" + this.mPreferredDataProfile);
        androidUtilIndentingPrintWriter.println("Preferred data profile from db=" + getPreferredDataProfileFromDb());
        androidUtilIndentingPrintWriter.println("Preferred data profile from config=" + getPreferredDataProfileFromConfig());
        androidUtilIndentingPrintWriter.println("Preferred data profile set id=" + this.mPreferredDataProfileSetId);
        androidUtilIndentingPrintWriter.println("Initial attach data profile=" + this.mInitialAttachDataProfile);
        androidUtilIndentingPrintWriter.println("isTetheringDataProfileExisting=" + isTetheringDataProfileExisting(13));
        androidUtilIndentingPrintWriter.println("Permanent failed profiles=");
        androidUtilIndentingPrintWriter.increaseIndent();
        this.mAllDataProfiles.stream().filter(new Predicate() { // from class: com.android.internal.telephony.data.DataProfileManager$$ExternalSyntheticLambda12
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$dump$14;
                lambda$dump$14 = DataProfileManager.lambda$dump$14((DataProfile) obj);
                return lambda$dump$14;
            }
        }).forEach(new Consumer() { // from class: com.android.internal.telephony.data.DataProfileManager$$ExternalSyntheticLambda13
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                AndroidUtilIndentingPrintWriter.this.println((DataProfile) obj);
            }
        });
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.println("Local logs:");
        androidUtilIndentingPrintWriter.increaseIndent();
        this.mLocalLog.dump(fileDescriptor, androidUtilIndentingPrintWriter, strArr);
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.decreaseIndent();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$dump$14(DataProfile dataProfile) {
        return dataProfile.getApnSetting() != null && dataProfile.getApnSetting().getPermanentFailed();
    }
}
