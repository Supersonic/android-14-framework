package com.android.internal.telephony;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import android.content.pm.UserInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.telephony.CarrierConfigManager;
import android.telephony.TelephonyManager;
import android.telephony.TelephonyRegistryManager;
import android.telephony.UiccAccessRule;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Pair;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.telephony.CarrierPrivilegesTracker;
import com.android.internal.telephony.uicc.IccUtils;
import com.android.internal.telephony.uicc.UiccPort;
import com.android.internal.telephony.uicc.UiccProfile;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
/* loaded from: classes.dex */
public class CarrierPrivilegesTracker extends Handler {
    private final Map<String, Set<Integer>> mCachedUids;
    private final CarrierConfigManager mCarrierConfigManager;
    private final List<UiccAccessRule> mCarrierConfigRules;
    private long mClearUiccRulesUptimeMillis;
    private final Context mContext;
    private final Map<String, Set<String>> mInstalledPackageCerts;
    private final BroadcastReceiver mIntentReceiver;
    private final LocalLog mLocalLog;
    private final PackageManager mPackageManager;
    private final Phone mPhone;
    @GuardedBy(anyOf = {"mPrivilegedPackageInfoLock.readLock()", "mPrivilegedPackageInfoLock.writeLock()"})
    private PrivilegedPackageInfo mPrivilegedPackageInfo;
    private final ReadWriteLock mPrivilegedPackageInfoLock;
    @GuardedBy(anyOf = {"mPrivilegedPackageInfoLock.readLock()", "mPrivilegedPackageInfoLock.writeLock()"})
    private boolean mSimIsReadyButNotLoaded;
    private final TelephonyManager mTelephonyManager;
    private final TelephonyRegistryManager mTelephonyRegistryManager;
    private List<UiccAccessRule> mTestOverrideRules;
    private final List<UiccAccessRule> mUiccRules;
    private final UserManager mUserManager;
    private static final String TAG = CarrierPrivilegesTracker.class.getSimpleName();
    private static final long CLEAR_UICC_RULES_DELAY_MILLIS = TimeUnit.SECONDS.toMillis(0);

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class PrivilegedPackageInfo {
        final Pair<String, Integer> mCarrierService;
        final Set<String> mPackageNames;
        final Set<Integer> mUids;

        PrivilegedPackageInfo() {
            this.mPackageNames = Collections.emptySet();
            this.mUids = Collections.emptySet();
            this.mCarrierService = new Pair<>(null, -1);
        }

        PrivilegedPackageInfo(Set<String> set, Set<Integer> set2, Pair<String, Integer> pair) {
            this.mPackageNames = set;
            this.mUids = set2;
            this.mCarrierService = pair;
        }

        public String toString() {
            return "{packageNames=" + CarrierPrivilegesTracker.getObfuscatedPackages(this.mPackageNames, new Function() { // from class: com.android.internal.telephony.CarrierPrivilegesTracker$PrivilegedPackageInfo$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    String lambda$toString$0;
                    lambda$toString$0 = CarrierPrivilegesTracker.PrivilegedPackageInfo.lambda$toString$0((String) obj);
                    return lambda$toString$0;
                }
            }) + ", uids=" + this.mUids + ", carrierServicePackageName=" + Rlog.pii(CarrierPrivilegesTracker.TAG, this.mCarrierService.first) + ", carrierServiceUid=" + this.mCarrierService.second + "}";
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ String lambda$toString$0(String str) {
            return Rlog.pii(CarrierPrivilegesTracker.TAG, str);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof PrivilegedPackageInfo) {
                PrivilegedPackageInfo privilegedPackageInfo = (PrivilegedPackageInfo) obj;
                return this.mPackageNames.equals(privilegedPackageInfo.mPackageNames) && this.mUids.equals(privilegedPackageInfo.mUids) && this.mCarrierService.equals(privilegedPackageInfo.mCarrierService);
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(this.mPackageNames, this.mUids, this.mCarrierService);
        }
    }

    public CarrierPrivilegesTracker(Looper looper, Phone phone, Context context) {
        super(looper);
        this.mLocalLog = new LocalLog(64);
        this.mCarrierConfigRules = new ArrayList();
        this.mUiccRules = new ArrayList();
        this.mTestOverrideRules = null;
        this.mInstalledPackageCerts = new ArrayMap();
        this.mCachedUids = new ArrayMap();
        this.mPrivilegedPackageInfoLock = new ReentrantReadWriteLock();
        this.mPrivilegedPackageInfo = new PrivilegedPackageInfo();
        this.mClearUiccRulesUptimeMillis = -1L;
        this.mSimIsReadyButNotLoaded = false;
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.android.internal.telephony.CarrierPrivilegesTracker.1
            /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
            /* JADX WARN: Code restructure failed: missing block: B:47:0x008d, code lost:
                if (r11.this$0.mPackageManager.getApplicationEnabledSetting(r13) == 3) goto L23;
             */
            @Override // android.content.BroadcastReceiver
            /*
                Code decompiled incorrectly, please refer to instructions dump.
            */
            public void onReceive(Context context2, Intent intent) {
                char c;
                String action = intent.getAction();
                if (action == null) {
                    return;
                }
                int i = 5;
                boolean z = true;
                boolean z2 = false;
                switch (action.hashCode()) {
                    case -1157582292:
                        if (action.equals("android.telephony.action.SIM_APPLICATION_STATE_CHANGED")) {
                            c = 0;
                            break;
                        }
                        c = 65535;
                        break;
                    case -810471698:
                        if (action.equals("android.intent.action.PACKAGE_REPLACED")) {
                            c = 1;
                            break;
                        }
                        c = 65535;
                        break;
                    case 172491798:
                        if (action.equals("android.intent.action.PACKAGE_CHANGED")) {
                            c = 2;
                            break;
                        }
                        c = 65535;
                        break;
                    case 525384130:
                        if (action.equals("android.intent.action.PACKAGE_REMOVED")) {
                            c = 3;
                            break;
                        }
                        c = 65535;
                        break;
                    case 657207618:
                        if (action.equals("android.telephony.action.SIM_CARD_STATE_CHANGED")) {
                            c = 4;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1544582882:
                        if (action.equals("android.intent.action.PACKAGE_ADDED")) {
                            c = 5;
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
                    case 4:
                        Bundle extras = intent.getExtras();
                        int i2 = extras.getInt("android.telephony.extra.SIM_STATE", 0);
                        int i3 = extras.getInt("phone", -1);
                        if (i2 == 1 || i2 == 6 || i2 == 5 || i2 == 10) {
                            CarrierPrivilegesTracker carrierPrivilegesTracker = CarrierPrivilegesTracker.this;
                            carrierPrivilegesTracker.sendMessage(carrierPrivilegesTracker.obtainMessage(4, i3, i2));
                            return;
                        }
                        return;
                    case 1:
                    case 2:
                    case 3:
                    case 5:
                        Uri data = intent.getData();
                        String schemeSpecificPart = data != null ? data.getSchemeSpecificPart() : null;
                        if (TextUtils.isEmpty(schemeSpecificPart)) {
                            Rlog.e(CarrierPrivilegesTracker.TAG, "Failed to get package from Intent");
                            return;
                        }
                        boolean equals = action.equals("android.intent.action.PACKAGE_REMOVED");
                        try {
                            if (action.equals("android.intent.action.PACKAGE_CHANGED")) {
                                break;
                            }
                            z = false;
                            z2 = z;
                            z = false;
                        } catch (IllegalArgumentException unused) {
                            Rlog.w(CarrierPrivilegesTracker.TAG, "Package does not exist: " + schemeSpecificPart);
                        }
                        if (equals || z2 || z) {
                            i = 6;
                        }
                        CarrierPrivilegesTracker carrierPrivilegesTracker2 = CarrierPrivilegesTracker.this;
                        carrierPrivilegesTracker2.sendMessage(carrierPrivilegesTracker2.obtainMessage(i, schemeSpecificPart));
                        return;
                    default:
                        return;
                }
            }
        };
        this.mIntentReceiver = broadcastReceiver;
        this.mContext = context;
        this.mPhone = phone;
        this.mPackageManager = context.getPackageManager();
        this.mUserManager = (UserManager) context.getSystemService("user");
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) context.getSystemService("carrier_config");
        this.mCarrierConfigManager = carrierConfigManager;
        carrierConfigManager.registerCarrierConfigChangeListener(new Executor() { // from class: com.android.internal.telephony.CarrierPrivilegesTracker$$ExternalSyntheticLambda0
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                CarrierPrivilegesTracker.this.post(runnable);
            }
        }, new CarrierConfigManager.CarrierConfigChangeListener() { // from class: com.android.internal.telephony.CarrierPrivilegesTracker$$ExternalSyntheticLambda1
            public final void onCarrierConfigChanged(int i, int i2, int i3, int i4) {
                CarrierPrivilegesTracker.this.lambda$new$0(i, i2, i3, i4);
            }
        });
        this.mTelephonyManager = (TelephonyManager) context.getSystemService("phone");
        this.mTelephonyRegistryManager = (TelephonyRegistryManager) context.getSystemService("telephony_registry");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.telephony.action.SIM_CARD_STATE_CHANGED");
        intentFilter.addAction("android.telephony.action.SIM_APPLICATION_STATE_CHANGED");
        context.registerReceiver(broadcastReceiver, intentFilter);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter2.addAction("android.intent.action.PACKAGE_REPLACED");
        intentFilter2.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter2.addAction("android.intent.action.PACKAGE_CHANGED");
        intentFilter2.addDataScheme("package");
        context.registerReceiver(broadcastReceiver, intentFilter2);
        sendMessage(obtainMessage(7));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i, int i2, int i3, int i4) {
        handleCarrierConfigUpdated(i2, i);
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        switch (message.what) {
            case 4:
                handleSimStateChanged(message.arg1, message.arg2);
                return;
            case 5:
                handlePackageAddedReplacedOrChanged((String) message.obj);
                return;
            case 6:
                handlePackageRemovedOrDisabledByUser((String) message.obj);
                return;
            case 7:
                handleInitializeTracker();
                return;
            case 8:
                handleSetTestOverrideRules((String) message.obj);
                return;
            case 9:
                handleClearUiccRules();
                return;
            case 10:
                handleUiccAccessRulesLoaded();
                return;
            default:
                String str = TAG;
                Rlog.e(str, "Received unknown msg type: " + message.what);
                return;
        }
    }

    private void handleCarrierConfigUpdated(int i, int i2) {
        if (i2 != this.mPhone.getPhoneId()) {
            return;
        }
        List<UiccAccessRule> list = Collections.EMPTY_LIST;
        if (i != -1) {
            list = getCarrierConfigRules(i);
        }
        LocalLog localLog = this.mLocalLog;
        localLog.log("CarrierConfigUpdated: subId=" + i + " slotIndex=" + i2 + " updated CarrierConfig rules=" + list);
        maybeUpdateRulesAndNotifyRegistrants(this.mCarrierConfigRules, list);
    }

    private List<UiccAccessRule> getCarrierConfigRules(int i) {
        PersistableBundle carrierConfigSubset = CarrierConfigManager.getCarrierConfigSubset(this.mContext, i, new String[]{"carrier_certificate_string_array"});
        if (!CarrierConfigManager.isConfigForIdentifiedCarrier(carrierConfigSubset)) {
            return Collections.EMPTY_LIST;
        }
        String[] stringArray = carrierConfigSubset.getStringArray("carrier_certificate_string_array");
        if (stringArray == null) {
            return Collections.EMPTY_LIST;
        }
        return Arrays.asList(UiccAccessRule.decodeRulesFromCarrierConfig(stringArray));
    }

    private void handleSimStateChanged(int i, int i2) {
        if (i != this.mPhone.getPhoneId()) {
            return;
        }
        List list = Collections.EMPTY_LIST;
        this.mPrivilegedPackageInfoLock.writeLock().lock();
        try {
            this.mSimIsReadyButNotLoaded = i2 == 5;
            this.mPrivilegedPackageInfoLock.writeLock().unlock();
            if (i2 == 10) {
                this.mLocalLog.log("SIM fully loaded, handleUiccAccessRulesLoaded.");
                handleUiccAccessRulesLoaded();
            } else if (!this.mUiccRules.isEmpty() && this.mClearUiccRulesUptimeMillis == -1) {
                long uptimeMillis = SystemClock.uptimeMillis();
                long j = CLEAR_UICC_RULES_DELAY_MILLIS;
                this.mClearUiccRulesUptimeMillis = uptimeMillis + j;
                sendMessageAtTime(obtainMessage(9), this.mClearUiccRulesUptimeMillis);
                LocalLog localLog = this.mLocalLog;
                localLog.log("SIM is gone, simState=" + TelephonyManager.simStateToString(i2) + ". Delay " + TimeUnit.MILLISECONDS.toSeconds(j) + " seconds to clear UICC rules.");
            } else {
                this.mLocalLog.log("Ignore SIM gone event while UiccRules is empty or waiting to be emptied.");
            }
        } catch (Throwable th) {
            this.mPrivilegedPackageInfoLock.writeLock().unlock();
            throw th;
        }
    }

    private void handleUiccAccessRulesLoaded() {
        this.mClearUiccRulesUptimeMillis = -1L;
        removeMessages(9);
        List<UiccAccessRule> simRules = getSimRules();
        LocalLog localLog = this.mLocalLog;
        localLog.log("UiccAccessRules loaded: updated SIM-loaded rules=" + simRules);
        maybeUpdateRulesAndNotifyRegistrants(this.mUiccRules, simRules);
    }

    public void onUiccAccessRulesLoaded() {
        sendEmptyMessage(10);
    }

    private void handleClearUiccRules() {
        this.mClearUiccRulesUptimeMillis = -1L;
        removeMessages(9);
        maybeUpdateRulesAndNotifyRegistrants(this.mUiccRules, Collections.EMPTY_LIST);
    }

    private List<UiccAccessRule> getSimRules() {
        if (!this.mTelephonyManager.hasIccCard(this.mPhone.getPhoneId())) {
            return Collections.EMPTY_LIST;
        }
        UiccPort uiccPort = this.mPhone.getUiccPort();
        if (uiccPort == null) {
            String str = TAG;
            Rlog.w(str, "Null UiccPort, but hasIccCard was present for phoneId " + this.mPhone.getPhoneId());
            return Collections.EMPTY_LIST;
        }
        UiccProfile uiccProfile = uiccPort.getUiccProfile();
        if (uiccProfile == null) {
            String str2 = TAG;
            Rlog.w(str2, "Null UiccProfile, but hasIccCard was true for phoneId " + this.mPhone.getPhoneId());
            return Collections.EMPTY_LIST;
        }
        return uiccProfile.getCarrierPrivilegeAccessRules();
    }

    private void handlePackageAddedReplacedOrChanged(String str) {
        if (str == null) {
            return;
        }
        try {
            PackageInfo packageInfo = this.mPackageManager.getPackageInfo(str, 671121408);
            updateCertsForPackage(packageInfo);
            getUidsForPackage(packageInfo.packageName, true);
            maybeUpdatePrivilegedPackagesAndNotifyRegistrants();
        } catch (PackageManager.NameNotFoundException e) {
            String str2 = TAG;
            Rlog.e(str2, "Error getting installed package: " + str, e);
        }
    }

    private void updateCertsForPackage(PackageInfo packageInfo) {
        ArraySet arraySet = new ArraySet();
        for (Signature signature : UiccAccessRule.getSignatures(packageInfo)) {
            String bytesToHexString = IccUtils.bytesToHexString(UiccAccessRule.getCertHash(signature, "SHA-1"));
            Locale locale = Locale.ROOT;
            arraySet.add(bytesToHexString.toUpperCase(locale));
            arraySet.add(IccUtils.bytesToHexString(UiccAccessRule.getCertHash(signature, "SHA-256")).toUpperCase(locale));
        }
        this.mInstalledPackageCerts.put(packageInfo.packageName, arraySet);
    }

    private void handlePackageRemovedOrDisabledByUser(String str) {
        if (str == null) {
            return;
        }
        if (this.mInstalledPackageCerts.remove(str) == null || this.mCachedUids.remove(str) == null) {
            String str2 = TAG;
            Rlog.e(str2, "Unknown package was uninstalled or disabled by user: " + str);
            return;
        }
        maybeUpdatePrivilegedPackagesAndNotifyRegistrants();
    }

    private void handleInitializeTracker() {
        this.mCarrierConfigRules.addAll(getCarrierConfigRules(this.mPhone.getSubId()));
        this.mUiccRules.addAll(getSimRules());
        refreshInstalledPackageCache();
        maybeUpdatePrivilegedPackagesAndNotifyRegistrants();
        this.mLocalLog.log("Initializing state: CarrierConfig rules=" + this.mCarrierConfigRules + " SIM-loaded rules=" + this.mUiccRules);
    }

    private void refreshInstalledPackageCache() {
        for (PackageInfo packageInfo : this.mPackageManager.getInstalledPackagesAsUser(671121408, UserHandle.SYSTEM.getIdentifier())) {
            updateCertsForPackage(packageInfo);
            getUidsForPackage(packageInfo.packageName, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static <T> String getObfuscatedPackages(Collection<T> collection, Function<T, String> function) {
        StringJoiner stringJoiner = new StringJoiner(", ", "{", "}");
        for (T t : collection) {
            stringJoiner.add(function.apply(t));
        }
        return stringJoiner.toString();
    }

    private void maybeUpdateRulesAndNotifyRegistrants(List<UiccAccessRule> list, List<UiccAccessRule> list2) {
        if (list.equals(list2)) {
            return;
        }
        list.clear();
        list.addAll(list2);
        maybeUpdatePrivilegedPackagesAndNotifyRegistrants();
    }

    private void maybeUpdatePrivilegedPackagesAndNotifyRegistrants() {
        PrivilegedPackageInfo currentPrivilegedPackagesForAllUsers = getCurrentPrivilegedPackagesForAllUsers();
        this.mPrivilegedPackageInfoLock.readLock().lock();
        try {
            if (this.mPrivilegedPackageInfo.equals(currentPrivilegedPackagesForAllUsers)) {
                return;
            }
            LocalLog localLog = this.mLocalLog;
            localLog.log("Privileged packages info changed. New state = " + currentPrivilegedPackagesForAllUsers);
            boolean z = !currentPrivilegedPackagesForAllUsers.mPackageNames.equals(this.mPrivilegedPackageInfo.mPackageNames);
            boolean z2 = currentPrivilegedPackagesForAllUsers.mUids.equals(this.mPrivilegedPackageInfo.mUids) ? false : true;
            boolean equals = true ^ currentPrivilegedPackagesForAllUsers.mCarrierService.equals(this.mPrivilegedPackageInfo.mCarrierService);
            this.mPrivilegedPackageInfoLock.readLock().unlock();
            this.mPrivilegedPackageInfoLock.writeLock().lock();
            try {
                this.mPrivilegedPackageInfo = currentPrivilegedPackagesForAllUsers;
                this.mPrivilegedPackageInfoLock.writeLock().unlock();
                this.mPrivilegedPackageInfoLock.readLock().lock();
                if (z || z2) {
                    try {
                        this.mTelephonyRegistryManager.notifyCarrierPrivilegesChanged(this.mPhone.getPhoneId(), Collections.unmodifiableSet(this.mPrivilegedPackageInfo.mPackageNames), Collections.unmodifiableSet(this.mPrivilegedPackageInfo.mUids));
                    } finally {
                    }
                }
                if (equals) {
                    TelephonyRegistryManager telephonyRegistryManager = this.mTelephonyRegistryManager;
                    int phoneId = this.mPhone.getPhoneId();
                    Pair<String, Integer> pair = this.mPrivilegedPackageInfo.mCarrierService;
                    telephonyRegistryManager.notifyCarrierServiceChanged(phoneId, (String) pair.first, ((Integer) pair.second).intValue());
                }
                this.mPrivilegedPackageInfoLock.readLock().unlock();
                ActivityManager activityManager = (ActivityManager) this.mContext.getSystemService(ActivityManager.class);
                CarrierAppUtils.disableCarrierAppsUntilPrivileged(this.mContext.getOpPackageName(), this.mTelephonyManager, ActivityManager.getCurrentUser(), this.mContext);
            } catch (Throwable th) {
                this.mPrivilegedPackageInfoLock.writeLock().unlock();
                throw th;
            }
        } finally {
        }
    }

    private PrivilegedPackageInfo getCurrentPrivilegedPackagesForAllUsers() {
        ArraySet arraySet = new ArraySet();
        ArraySet arraySet2 = new ArraySet();
        for (Map.Entry<String, Set<String>> entry : this.mInstalledPackageCerts.entrySet()) {
            if (isPackagePrivileged(entry.getKey(), entry.getValue())) {
                arraySet.add(entry.getKey());
                arraySet2.addAll(getUidsForPackage(entry.getKey(), false));
            }
        }
        return new PrivilegedPackageInfo(arraySet, arraySet2, getCarrierService(arraySet));
    }

    private boolean isPackagePrivileged(String str, Set<String> set) {
        for (String str2 : set) {
            List<UiccAccessRule> list = this.mTestOverrideRules;
            if (list != null) {
                for (UiccAccessRule uiccAccessRule : list) {
                    if (uiccAccessRule.matches(str2, str)) {
                        return true;
                    }
                }
                continue;
            } else {
                for (UiccAccessRule uiccAccessRule2 : this.mCarrierConfigRules) {
                    if (uiccAccessRule2.matches(str2, str)) {
                        return true;
                    }
                }
                for (UiccAccessRule uiccAccessRule3 : this.mUiccRules) {
                    if (uiccAccessRule3.matches(str2, str)) {
                        return true;
                    }
                }
                continue;
            }
        }
        return false;
    }

    private Set<Integer> getUidsForPackage(String str, boolean z) {
        if (z) {
            this.mCachedUids.remove(str);
        }
        if (this.mCachedUids.containsKey(str)) {
            return this.mCachedUids.get(str);
        }
        ArraySet arraySet = new ArraySet();
        for (UserInfo userInfo : this.mUserManager.getUsers()) {
            int identifier = userInfo.getUserHandle().getIdentifier();
            try {
                arraySet.add(Integer.valueOf(this.mPackageManager.getPackageUidAsUser(str, identifier)));
            } catch (PackageManager.NameNotFoundException unused) {
                String str2 = TAG;
                Rlog.e(str2, "Unable to find uid for package " + str + " and user " + identifier);
            }
        }
        this.mCachedUids.put(str, arraySet);
        return arraySet;
    }

    private int getPackageUid(String str) {
        try {
            return this.mPackageManager.getPackageUid(str, 0);
        } catch (PackageManager.NameNotFoundException unused) {
            String str2 = TAG;
            Rlog.e(str2, "Unable to find uid for package " + str);
            return -1;
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("CarrierPrivilegesTracker - phoneId: " + this.mPhone.getPhoneId());
        printWriter.println("CarrierPrivilegesTracker - Log Begin ----");
        this.mLocalLog.dump(fileDescriptor, printWriter, strArr);
        printWriter.println("CarrierPrivilegesTracker - Log End ----");
        this.mPrivilegedPackageInfoLock.readLock().lock();
        try {
            printWriter.println("CarrierPrivilegesTracker - Privileged package info: " + this.mPrivilegedPackageInfo);
            printWriter.println("mSimIsReadyButNotLoaded: " + this.mSimIsReadyButNotLoaded);
            this.mPrivilegedPackageInfoLock.readLock().unlock();
            printWriter.println("CarrierPrivilegesTracker - Test-override rules: " + this.mTestOverrideRules);
            printWriter.println("CarrierPrivilegesTracker - SIM-loaded rules: " + this.mUiccRules);
            printWriter.println("CarrierPrivilegesTracker - Carrier config rules: " + this.mCarrierConfigRules);
            printWriter.println("mClearUiccRulesUptimeMillis: " + this.mClearUiccRulesUptimeMillis);
        } catch (Throwable th) {
            this.mPrivilegedPackageInfoLock.readLock().unlock();
            throw th;
        }
    }

    public void setTestOverrideCarrierPrivilegeRules(String str) {
        sendMessage(obtainMessage(8, str));
    }

    private void handleSetTestOverrideRules(String str) {
        if (str == null) {
            this.mTestOverrideRules = null;
        } else if (str.isEmpty()) {
            this.mTestOverrideRules = Collections.emptyList();
        } else {
            this.mTestOverrideRules = Arrays.asList(UiccAccessRule.decodeRulesFromCarrierConfig(new String[]{str}));
            refreshInstalledPackageCache();
        }
        maybeUpdatePrivilegedPackagesAndNotifyRegistrants();
    }

    public int getCarrierPrivilegeStatusForPackage(String str) {
        if (str == null) {
            return 0;
        }
        this.mPrivilegedPackageInfoLock.readLock().lock();
        try {
            if (!this.mSimIsReadyButNotLoaded) {
                if (this.mPrivilegedPackageInfo.mPackageNames.contains(str)) {
                    this.mPrivilegedPackageInfoLock.readLock().unlock();
                    return 1;
                }
                return 0;
            }
            this.mPrivilegedPackageInfoLock.readLock().unlock();
            return -1;
        } finally {
            this.mPrivilegedPackageInfoLock.readLock().unlock();
        }
    }

    public Set<String> getPackagesWithCarrierPrivileges() {
        this.mPrivilegedPackageInfoLock.readLock().lock();
        try {
            return this.mSimIsReadyButNotLoaded ? Collections.emptySet() : Collections.unmodifiableSet(this.mPrivilegedPackageInfo.mPackageNames);
        } finally {
            this.mPrivilegedPackageInfoLock.readLock().unlock();
        }
    }

    public int getCarrierPrivilegeStatusForUid(int i) {
        this.mPrivilegedPackageInfoLock.readLock().lock();
        try {
            if (!this.mSimIsReadyButNotLoaded) {
                if (this.mPrivilegedPackageInfo.mUids.contains(Integer.valueOf(i))) {
                    this.mPrivilegedPackageInfoLock.readLock().unlock();
                    return 1;
                }
                this.mPrivilegedPackageInfoLock.readLock().unlock();
                return 0;
            }
            this.mPrivilegedPackageInfoLock.readLock().unlock();
            return -1;
        } catch (Throwable th) {
            this.mPrivilegedPackageInfoLock.readLock().unlock();
            throw th;
        }
    }

    public String getCarrierServicePackageName() {
        this.mPrivilegedPackageInfoLock.readLock().lock();
        try {
            if (!this.mSimIsReadyButNotLoaded) {
                return (String) this.mPrivilegedPackageInfo.mCarrierService.first;
            }
            this.mPrivilegedPackageInfoLock.readLock().unlock();
            return null;
        } finally {
            this.mPrivilegedPackageInfoLock.readLock().unlock();
        }
    }

    public int getCarrierServicePackageUid() {
        this.mPrivilegedPackageInfoLock.readLock().lock();
        try {
            if (!this.mSimIsReadyButNotLoaded) {
                return ((Integer) this.mPrivilegedPackageInfo.mCarrierService.second).intValue();
            }
            this.mPrivilegedPackageInfoLock.readLock().unlock();
            return -1;
        } finally {
            this.mPrivilegedPackageInfoLock.readLock().unlock();
        }
    }

    public List<String> getCarrierPackageNamesForIntent(Intent intent) {
        this.mPrivilegedPackageInfoLock.readLock().lock();
        try {
            if (this.mSimIsReadyButNotLoaded) {
                return Collections.emptyList();
            }
            this.mPrivilegedPackageInfoLock.readLock().unlock();
            ArrayList<ResolveInfo> arrayList = new ArrayList();
            arrayList.addAll(this.mPackageManager.queryBroadcastReceivers(intent, 0));
            arrayList.addAll(this.mPackageManager.queryIntentActivities(intent, 0));
            arrayList.addAll(this.mPackageManager.queryIntentServices(intent, 0));
            arrayList.addAll(this.mPackageManager.queryIntentContentProviders(intent, 0));
            this.mPrivilegedPackageInfoLock.readLock().lock();
            try {
                if (this.mSimIsReadyButNotLoaded) {
                    return Collections.emptyList();
                }
                ArraySet arraySet = new ArraySet();
                for (ResolveInfo resolveInfo : arrayList) {
                    String packageName = getPackageName(resolveInfo);
                    if (packageName != null && 1 == getCarrierPrivilegeStatusForPackage(packageName)) {
                        arraySet.add(packageName);
                    }
                }
                return new ArrayList(arraySet);
            } finally {
            }
        } finally {
        }
    }

    private static String getPackageName(ResolveInfo resolveInfo) {
        ActivityInfo activityInfo = resolveInfo.activityInfo;
        if (activityInfo != null) {
            return activityInfo.packageName;
        }
        ServiceInfo serviceInfo = resolveInfo.serviceInfo;
        if (serviceInfo != null) {
            return serviceInfo.packageName;
        }
        ProviderInfo providerInfo = resolveInfo.providerInfo;
        if (providerInfo != null) {
            return providerInfo.packageName;
        }
        return null;
    }

    private Pair<String, Integer> getCarrierService(Set<String> set) {
        String str;
        Iterator<ResolveInfo> it = this.mPackageManager.queryIntentServices(new Intent("android.service.carrier.CarrierService"), 0).iterator();
        while (true) {
            if (!it.hasNext()) {
                str = null;
                break;
            }
            str = getPackageName(it.next());
            if (set.contains(str)) {
                break;
            }
        }
        if (str == null) {
            return new Pair<>(null, -1);
        }
        return new Pair<>(str, Integer.valueOf(getPackageUid(str)));
    }
}
