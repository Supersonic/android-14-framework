package com.android.internal.telephony;

import android.app.ActivityManager;
import android.compat.annotation.UnsupportedAppUsage;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelUuid;
import android.os.PersistableBundle;
import android.os.UserHandle;
import android.preference.PreferenceManager;
import android.provider.DeviceConfig;
import android.service.carrier.CarrierIdentifier;
import android.service.euicc.EuiccProfileInfo;
import android.service.euicc.GetEuiccProfileInfoListResult;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.UiccAccessRule;
import android.telephony.euicc.EuiccManager;
import android.text.TextUtils;
import android.util.Pair;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.euicc.EuiccController;
import com.android.internal.telephony.metrics.TelephonyMetrics;
import com.android.internal.telephony.uicc.IccRecords;
import com.android.internal.telephony.uicc.IccUtils;
import com.android.internal.telephony.uicc.UiccCard;
import com.android.internal.telephony.uicc.UiccController;
import com.android.internal.telephony.uicc.UiccPort;
import com.android.internal.telephony.uicc.UiccSlot;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public class SubscriptionInfoUpdater extends Handler {
    public static final String CURR_SUBID = "curr_subid";
    private static final ParcelUuid REMOVE_GROUP_UUID;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private static final int SUPPORTED_MODEM_COUNT;
    private static boolean mIsWorkProfileTelephonyEnabled;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private static Context sContext;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected static String[] sIccId;
    private static String[] sInactiveIccIds;
    private static boolean sIsSubInfoInitialized;
    private static int[] sSimApplicationState;
    private static int[] sSimCardState;
    private Handler mBackgroundHandler;
    private CarrierServiceBindHelper mCarrierServiceBindHelper;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private int mCurrentlyActiveUserId;
    private EuiccManager mEuiccManager;
    protected SubscriptionController mSubscriptionController;
    private SubscriptionManager mSubscriptionManager;
    private final BroadcastReceiver mUserUnlockedReceiver;
    private volatile boolean shouldRetryUpdateEmbeddedSubscriptions = false;
    private final CopyOnWriteArraySet<Integer> retryUpdateEmbeddedSubscriptionCards = new CopyOnWriteArraySet<>();

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public interface UpdateEmbeddedSubsCallback {
        void run(boolean z);
    }

    static {
        int supportedModemCount = TelephonyManager.getDefault().getSupportedModemCount();
        SUPPORTED_MODEM_COUNT = supportedModemCount;
        REMOVE_GROUP_UUID = ParcelUuid.fromString("00000000-0000-0000-0000-000000000000");
        sContext = null;
        sIccId = new String[supportedModemCount];
        sInactiveIccIds = new String[supportedModemCount];
        sSimCardState = new int[supportedModemCount];
        sSimApplicationState = new int[supportedModemCount];
        sIsSubInfoInitialized = false;
        mIsWorkProfileTelephonyEnabled = false;
    }

    @VisibleForTesting
    public SubscriptionInfoUpdater(Looper looper, Context context, SubscriptionController subscriptionController) {
        this.mSubscriptionController = null;
        this.mSubscriptionManager = null;
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.android.internal.telephony.SubscriptionInfoUpdater.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if ("android.intent.action.USER_UNLOCKED".equals(intent.getAction()) && SubscriptionInfoUpdater.this.shouldRetryUpdateEmbeddedSubscriptions) {
                    SubscriptionInfoUpdater.logd("Retrying refresh embedded subscriptions after user unlock.");
                    Iterator it = SubscriptionInfoUpdater.this.retryUpdateEmbeddedSubscriptionCards.iterator();
                    while (it.hasNext()) {
                        SubscriptionInfoUpdater.this.requestEmbeddedSubscriptionInfoListRefresh(((Integer) it.next()).intValue(), null);
                    }
                    SubscriptionInfoUpdater.this.retryUpdateEmbeddedSubscriptionCards.clear();
                    SubscriptionInfoUpdater.sContext.unregisterReceiver(SubscriptionInfoUpdater.this.mUserUnlockedReceiver);
                }
            }
        };
        this.mUserUnlockedReceiver = broadcastReceiver;
        logd("Constructor invoked");
        this.mBackgroundHandler = new Handler(looper);
        sContext = context;
        this.mSubscriptionController = subscriptionController;
        this.mSubscriptionManager = SubscriptionManager.from(context);
        this.mEuiccManager = (EuiccManager) sContext.getSystemService("euicc");
        this.mCarrierServiceBindHelper = new CarrierServiceBindHelper(sContext);
        sContext.registerReceiver(broadcastReceiver, new IntentFilter("android.intent.action.USER_UNLOCKED"));
        initializeCarrierApps();
        PhoneConfigurationManager.registerForMultiSimConfigChange(this, 13, null);
        mIsWorkProfileTelephonyEnabled = DeviceConfig.getBoolean("telephony", "enable_work_profile_telephony", false);
        DeviceConfig.addOnPropertiesChangedListener("telephony", new Executor() { // from class: com.android.internal.telephony.SubscriptionInfoUpdater$$ExternalSyntheticLambda5
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                SubscriptionInfoUpdater.this.post(runnable);
            }
        }, new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.internal.telephony.SubscriptionInfoUpdater$$ExternalSyntheticLambda6
            public final void onPropertiesChanged(DeviceConfig.Properties properties) {
                SubscriptionInfoUpdater.this.lambda$new$0(properties);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(DeviceConfig.Properties properties) {
        if (TextUtils.equals("telephony", properties.getNamespace())) {
            sendEmptyMessage(15);
        }
    }

    private void initializeCarrierApps() {
        this.mCurrentlyActiveUserId = 0;
        sContext.registerReceiverForAllUsers(new BroadcastReceiver() { // from class: com.android.internal.telephony.SubscriptionInfoUpdater.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                if ("android.intent.action.USER_FOREGROUND".equals(intent.getAction())) {
                    UserHandle userHandle = (UserHandle) intent.getParcelableExtra("android.intent.extra.USER");
                    SubscriptionInfoUpdater.this.mCurrentlyActiveUserId = userHandle != null ? userHandle.getIdentifier() : 0;
                    CarrierAppUtils.disableCarrierAppsUntilPrivileged(SubscriptionInfoUpdater.sContext.getOpPackageName(), TelephonyManager.getDefault(), SubscriptionInfoUpdater.this.mCurrentlyActiveUserId, SubscriptionInfoUpdater.sContext);
                }
            }
        }, new IntentFilter("android.intent.action.USER_FOREGROUND"), null, null);
        ActivityManager activityManager = (ActivityManager) sContext.getSystemService("activity");
        this.mCurrentlyActiveUserId = ActivityManager.getCurrentUser();
        CarrierAppUtils.disableCarrierAppsUntilPrivileged(sContext.getOpPackageName(), TelephonyManager.getDefault(), this.mCurrentlyActiveUserId, sContext);
    }

    public void updateInternalIccState(String str, String str2, int i) {
        logd("updateInternalIccState to simStatus " + str + " reason " + str2 + " phoneId " + i);
        int internalIccStateToMessage = internalIccStateToMessage(str);
        if (internalIccStateToMessage != -1) {
            sendMessage(obtainMessage(internalIccStateToMessage, i, 0, str2));
        }
    }

    public void updateInternalIccStateForInactivePort(int i, String str) {
        sendMessage(obtainMessage(14, i, 0, str));
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private int internalIccStateToMessage(String str) {
        char c;
        str.hashCode();
        switch (str.hashCode()) {
            case -2044189691:
                if (str.equals("LOADED")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -2044123382:
                if (str.equals("LOCKED")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -1830845986:
                if (str.equals("CARD_IO_ERROR")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 2251386:
                if (str.equals("IMSI")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 77848963:
                if (str.equals("READY")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 433141802:
                if (str.equals("UNKNOWN")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 1034051831:
                if (str.equals("NOT_READY")) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 1599753450:
                if (str.equals("CARD_RESTRICTED")) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case 1924388665:
                if (str.equals("ABSENT")) {
                    c = '\b';
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
                return 3;
            case 1:
                return 5;
            case 2:
                return 6;
            case 3:
                return 11;
            case 4:
                return 10;
            case 5:
                return 7;
            case 6:
                return 9;
            case 7:
                return 8;
            case '\b':
                return 4;
            default:
                logd("Ignoring simStatus: " + str);
                return -1;
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected boolean isAllIccIdQueryDone() {
        for (int i = 0; i < TelephonyManager.getDefault().getActiveModemCount(); i++) {
            UiccSlot uiccSlotForPhone = UiccController.getInstance().getUiccSlotForPhone(i);
            int slotIdFromPhoneId = UiccController.getInstance().getSlotIdFromPhoneId(i);
            if (sIccId[i] == null || uiccSlotForPhone == null || !uiccSlotForPhone.isActive() || (uiccSlotForPhone.isEuicc() && UiccController.getInstance().getUiccPort(i) == null)) {
                if (sIccId[i] == null) {
                    logd("Wait for SIM " + i + " Iccid");
                } else {
                    logd(String.format("Wait for port corresponding to phone %d to be active, slotId is %d , portIndex is %d", Integer.valueOf(i), Integer.valueOf(slotIdFromPhoneId), Integer.valueOf(uiccSlotForPhone.getPortIndexFromPhoneId(i))));
                }
                return false;
            }
        }
        logd("All IccIds query complete");
        return true;
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        Object obj;
        ArrayList arrayList = new ArrayList();
        switch (message.what) {
            case 2:
                AsyncResult asyncResult = (AsyncResult) message.obj;
                Integer num = (Integer) asyncResult.userObj;
                if (asyncResult.exception == null && (obj = asyncResult.result) != null) {
                    if (((int[]) obj)[0] == 1) {
                        PhoneFactory.getPhone(num.intValue()).setNetworkSelectionModeAutomatic(null);
                        return;
                    }
                    return;
                }
                logd("EVENT_GET_NETWORK_SELECTION_MODE_DONE: error getting network mode.");
                return;
            case 3:
                handleSimLoaded(message.arg1);
                return;
            case 4:
                handleSimAbsent(message.arg1);
                return;
            case 5:
                handleSimLocked(message.arg1, (String) message.obj);
                return;
            case 6:
                handleSimError(message.arg1);
                return;
            case 7:
                broadcastSimStateChanged(message.arg1, "UNKNOWN", null);
                broadcastSimCardStateChanged(message.arg1, 0);
                broadcastSimApplicationStateChanged(message.arg1, 0);
                updateSubscriptionCarrierId(message.arg1, "UNKNOWN");
                updateCarrierServices(message.arg1, "UNKNOWN");
                return;
            case 8:
                broadcastSimStateChanged(message.arg1, "CARD_RESTRICTED", "CARD_RESTRICTED");
                broadcastSimCardStateChanged(message.arg1, 9);
                broadcastSimApplicationStateChanged(message.arg1, 6);
                updateSubscriptionCarrierId(message.arg1, "CARD_RESTRICTED");
                updateCarrierServices(message.arg1, "CARD_RESTRICTED");
                return;
            case 9:
                arrayList.add(Integer.valueOf(getCardIdFromPhoneId(message.arg1)));
                updateEmbeddedSubscriptions(arrayList, new UpdateEmbeddedSubsCallback() { // from class: com.android.internal.telephony.SubscriptionInfoUpdater$$ExternalSyntheticLambda1
                    @Override // com.android.internal.telephony.SubscriptionInfoUpdater.UpdateEmbeddedSubsCallback
                    public final void run(boolean z) {
                        SubscriptionInfoUpdater.this.lambda$handleMessage$1(z);
                    }
                });
                handleSimNotReady(message.arg1);
                return;
            case 10:
                handleSimReady(message.arg1);
                return;
            case 11:
                broadcastSimStateChanged(message.arg1, "IMSI", null);
                return;
            case 12:
                arrayList.add(Integer.valueOf(message.arg1));
                final Runnable runnable = (Runnable) message.obj;
                updateEmbeddedSubscriptions(arrayList, new UpdateEmbeddedSubsCallback() { // from class: com.android.internal.telephony.SubscriptionInfoUpdater$$ExternalSyntheticLambda2
                    @Override // com.android.internal.telephony.SubscriptionInfoUpdater.UpdateEmbeddedSubsCallback
                    public final void run(boolean z) {
                        SubscriptionInfoUpdater.this.lambda$handleMessage$2(runnable, z);
                    }
                });
                return;
            case 13:
                onMultiSimConfigChanged();
                return;
            case 14:
                handleInactivePortIccStateChange(message.arg1, (String) message.obj);
                return;
            case 15:
                boolean z = DeviceConfig.getBoolean("telephony", "enable_work_profile_telephony", false);
                if (z != mIsWorkProfileTelephonyEnabled) {
                    logd("EVENT_DEVICE_CONFIG_CHANGED: isWorkProfileTelephonyEnabled changed from " + mIsWorkProfileTelephonyEnabled + " to " + z);
                    mIsWorkProfileTelephonyEnabled = z;
                    return;
                }
                return;
            default:
                logd("Unknown msg:" + message.what);
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handleMessage$1(boolean z) {
        if (z) {
            this.mSubscriptionController.notifySubscriptionInfoChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handleMessage$2(Runnable runnable, boolean z) {
        if (z) {
            this.mSubscriptionController.notifySubscriptionInfoChanged();
        }
        if (runnable != null) {
            runnable.run();
        }
    }

    private void onMultiSimConfigChanged() {
        for (int activeModemCount = ((TelephonyManager) sContext.getSystemService("phone")).getActiveModemCount(); activeModemCount < SUPPORTED_MODEM_COUNT; activeModemCount++) {
            sIccId[activeModemCount] = null;
            sSimCardState[activeModemCount] = 0;
            sSimApplicationState[activeModemCount] = 0;
        }
    }

    protected int getCardIdFromPhoneId(int i) {
        UiccController uiccController = UiccController.getInstance();
        UiccCard uiccCardForPhone = uiccController.getUiccCardForPhone(i);
        if (uiccCardForPhone != null) {
            return uiccController.convertToPublicCardId(uiccCardForPhone.getCardId());
        }
        return -2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void requestEmbeddedSubscriptionInfoListRefresh(int i, Runnable runnable) {
        sendMessage(obtainMessage(12, i, 0, runnable));
    }

    protected void handleSimLocked(int i, String str) {
        String str2 = sIccId[i];
        if (str2 != null && str2.equals(PhoneConfigurationManager.SSSS)) {
            logd("SIM" + (i + 1) + " hot plug in");
            sIccId[i] = null;
        }
        IccCard iccCard = PhoneFactory.getPhone(i).getIccCard();
        if (iccCard == null) {
            logd("handleSimLocked: IccCard null");
            return;
        }
        IccRecords iccRecords = iccCard.getIccRecords();
        if (iccRecords == null) {
            logd("handleSimLocked: IccRecords null");
        } else if (IccUtils.stripTrailingFs(iccRecords.getFullIccId()) == null) {
            logd("handleSimLocked: IccID null");
        } else {
            sIccId[i] = IccUtils.stripTrailingFs(iccRecords.getFullIccId());
            updateSubscriptionInfoByIccId(i, true);
            broadcastSimStateChanged(i, "LOCKED", str);
            broadcastSimCardStateChanged(i, 11);
            broadcastSimApplicationStateChanged(i, getSimStateFromLockedReason(str));
            updateSubscriptionCarrierId(i, "LOCKED");
            updateCarrierServices(i, "LOCKED");
        }
    }

    private static int getSimStateFromLockedReason(String str) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -1733499378:
                if (str.equals("NETWORK")) {
                    c = 0;
                    break;
                }
                break;
            case 79221:
                if (str.equals("PIN")) {
                    c = 1;
                    break;
                }
                break;
            case 79590:
                if (str.equals("PUK")) {
                    c = 2;
                    break;
                }
                break;
            case 190660331:
                if (str.equals("PERM_DISABLED")) {
                    c = 3;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return 4;
            case 1:
                return 2;
            case 2:
                return 3;
            case 3:
                return 7;
            default:
                Rlog.e("SubscriptionInfoUpdater", "Unexpected SIM locked reason " + str);
                return 0;
        }
    }

    protected void handleSimReady(int i) {
        ArrayList arrayList = new ArrayList();
        logd("handleSimReady: phoneId: " + i);
        String str = sIccId[i];
        if (str != null && str.equals(PhoneConfigurationManager.SSSS)) {
            logd(" SIM" + (i + 1) + " hot plug in");
            sIccId[i] = null;
        }
        UiccPort uiccPort = UiccController.getInstance().getUiccPort(i);
        String stripTrailingFs = uiccPort == null ? null : IccUtils.stripTrailingFs(uiccPort.getIccId());
        if (!TextUtils.isEmpty(stripTrailingFs)) {
            sIccId[i] = stripTrailingFs;
            updateSubscriptionInfoByIccId(i, true);
        }
        arrayList.add(Integer.valueOf(getCardIdFromPhoneId(i)));
        updateEmbeddedSubscriptions(arrayList, new UpdateEmbeddedSubsCallback() { // from class: com.android.internal.telephony.SubscriptionInfoUpdater$$ExternalSyntheticLambda3
            @Override // com.android.internal.telephony.SubscriptionInfoUpdater.UpdateEmbeddedSubsCallback
            public final void run(boolean z) {
                SubscriptionInfoUpdater.this.lambda$handleSimReady$3(z);
            }
        });
        broadcastSimStateChanged(i, "READY", null);
        broadcastSimCardStateChanged(i, 11);
        broadcastSimApplicationStateChanged(i, 6);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handleSimReady$3(boolean z) {
        if (z) {
            this.mSubscriptionController.notifySubscriptionInfoChanged();
        }
    }

    protected void handleSimNotReady(int i) {
        logd("handleSimNotReady: phoneId: " + i);
        IccCard iccCard = PhoneFactory.getPhone(i).getIccCard();
        boolean areUiccAppsDisabledOnCard = areUiccAppsDisabledOnCard(i);
        boolean z = false;
        if (iccCard.isEmptyProfile() || areUiccAppsDisabledOnCard) {
            if (areUiccAppsDisabledOnCard) {
                UiccPort uiccPort = UiccController.getInstance().getUiccPort(i);
                sInactiveIccIds[i] = IccUtils.stripTrailingFs(uiccPort == null ? null : uiccPort.getIccId());
            }
            sIccId[i] = PhoneConfigurationManager.SSSS;
            updateSubscriptionInfoByIccId(i, false);
            z = true;
        } else {
            sIccId[i] = null;
        }
        broadcastSimStateChanged(i, "NOT_READY", null);
        broadcastSimCardStateChanged(i, 11);
        broadcastSimApplicationStateChanged(i, 6);
        if (z) {
            updateCarrierServices(i, "NOT_READY");
        }
    }

    private boolean areUiccAppsDisabledOnCard(int i) {
        SubscriptionInfo subInfoForIccId;
        if (UiccController.getInstance().getUiccSlotForPhone(i) == null) {
            return false;
        }
        UiccPort uiccPort = UiccController.getInstance().getUiccPort(i);
        String iccId = uiccPort == null ? null : uiccPort.getIccId();
        return (iccId == null || (subInfoForIccId = this.mSubscriptionController.getSubInfoForIccId(IccUtils.stripTrailingFs(iccId))) == null || subInfoForIccId.areUiccApplicationsEnabled()) ? false : true;
    }

    protected void handleSimLoaded(int i) {
        logd("handleSimLoaded: phoneId: " + i);
        IccCard iccCard = PhoneFactory.getPhone(i).getIccCard();
        if (iccCard == null) {
            logd("handleSimLoaded: IccCard null");
            return;
        }
        IccRecords iccRecords = iccCard.getIccRecords();
        if (iccRecords == null) {
            logd("handleSimLoaded: IccRecords null");
        } else if (IccUtils.stripTrailingFs(iccRecords.getFullIccId()) == null) {
            logd("handleSimLoaded: IccID null");
        } else {
            String[] strArr = sIccId;
            if (strArr[i] == null) {
                strArr[i] = IccUtils.stripTrailingFs(iccRecords.getFullIccId());
                updateSubscriptionInfoByIccId(i, true);
            }
            List<SubscriptionInfo> subInfoUsingSlotIndexPrivileged = this.mSubscriptionController.getSubInfoUsingSlotIndexPrivileged(i);
            if (subInfoUsingSlotIndexPrivileged == null || subInfoUsingSlotIndexPrivileged.isEmpty()) {
                loge("empty subinfo for phoneId: " + i + "could not update ContentResolver");
            } else {
                for (SubscriptionInfo subscriptionInfo : subInfoUsingSlotIndexPrivileged) {
                    int subscriptionId = subscriptionInfo.getSubscriptionId();
                    TelephonyManager telephonyManager = (TelephonyManager) sContext.getSystemService("phone");
                    String simOperatorNumeric = telephonyManager.getSimOperatorNumeric(subscriptionId);
                    if (!TextUtils.isEmpty(simOperatorNumeric)) {
                        if (subscriptionId == this.mSubscriptionController.getDefaultSubId()) {
                            MccTable.updateMccMncConfiguration(sContext, simOperatorNumeric);
                        }
                        this.mSubscriptionController.setMccMnc(simOperatorNumeric, subscriptionId);
                    } else {
                        logd("EVENT_RECORDS_LOADED Operator name is null");
                    }
                    String simCountryIsoForPhone = TelephonyManager.getSimCountryIsoForPhone(i);
                    if (!TextUtils.isEmpty(simCountryIsoForPhone)) {
                        this.mSubscriptionController.setCountryIso(simCountryIsoForPhone, subscriptionId);
                    } else {
                        logd("EVENT_RECORDS_LOADED sim country iso is null");
                    }
                    String line1Number = telephonyManager.getLine1Number(subscriptionId);
                    if (line1Number != null) {
                        this.mSubscriptionController.setDisplayNumber(line1Number, subscriptionId);
                    }
                    String subscriberId = telephonyManager.createForSubscriptionId(subscriptionId).getSubscriberId();
                    if (subscriberId != null) {
                        this.mSubscriptionController.setImsi(subscriberId, subscriptionId);
                    }
                    String[] ehplmns = iccRecords.getEhplmns();
                    String[] plmnsFromHplmnActRecord = iccRecords.getPlmnsFromHplmnActRecord();
                    if (ehplmns != null || plmnsFromHplmnActRecord != null) {
                        this.mSubscriptionController.setAssociatedPlmns(ehplmns, plmnsFromHplmnActRecord, subscriptionId);
                    }
                    SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(sContext);
                    if (defaultSharedPreferences.getInt(CURR_SUBID + i, -1) != subscriptionId) {
                        PhoneFactory.getPhone(i).getNetworkSelectionMode(obtainMessage(2, new Integer(i)));
                        SharedPreferences.Editor edit = defaultSharedPreferences.edit();
                        edit.putInt(CURR_SUBID + i, subscriptionId);
                        edit.apply();
                    }
                }
            }
            broadcastSimStateChanged(i, "LOADED", null);
            broadcastSimCardStateChanged(i, 11);
            broadcastSimApplicationStateChanged(i, 10);
            updateSubscriptionCarrierId(i, "LOADED");
            restoreSimSpecificSettingsForPhone(i);
            updateCarrierServices(i, "LOADED");
        }
    }

    @VisibleForTesting
    public int calculateUsageSetting(int i, int i2) {
        try {
            sContext.getResources().getInteger(17694809);
            int[] intArray = sContext.getResources().getIntArray(17236145);
            if (intArray != null) {
                if (intArray.length >= 1) {
                    if (i < 0 || i > 2) {
                        logd("Updating usage setting for current subscription");
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
                    loge("Invalid usage setting!" + i2);
                }
            }
            return i;
        } catch (Resources.NotFoundException unused) {
            loge("Failed to load usage setting resources!");
            return i;
        }
    }

    private void restoreSimSpecificSettingsForPhone(int i) {
        sContext.getContentResolver().call(SubscriptionManager.SIM_INFO_BACKUP_AND_RESTORE_CONTENT_URI, "restoreSimSpecificSettings", sIccId[i], (Bundle) null);
    }

    private void updateCarrierServices(int i, String str) {
        if (!SubscriptionManager.isValidPhoneId(i)) {
            logd("Ignore updateCarrierServices request with invalid phoneId " + i);
            return;
        }
        ((CarrierConfigManager) sContext.getSystemService("carrier_config")).updateConfigForPhoneId(i, str);
        this.mCarrierServiceBindHelper.updateForPhoneId(i, str);
    }

    private void updateSubscriptionCarrierId(int i, String str) {
        if (PhoneFactory.getPhone(i) != null) {
            PhoneFactory.getPhone(i).resolveSubscriptionCarrierId(str);
        }
    }

    private void handleInactivePortIccStateChange(int i, String str) {
        if (SubscriptionManager.isValidPhoneId(i)) {
            String str2 = sIccId[i];
            if (str2 != null && !str2.equals(PhoneConfigurationManager.SSSS)) {
                logd("Slot of SIM" + (i + 1) + " becomes inactive");
            }
            cleanSubscriptionInPhone(i, false);
        }
        if (TextUtils.isEmpty(str)) {
            return;
        }
        String stripTrailingFs = IccUtils.stripTrailingFs(str);
        if (this.mSubscriptionController.getSubInfoForIccId(stripTrailingFs) == null) {
            this.mSubscriptionController.insertEmptySubInfoRecord(stripTrailingFs, "CARD", -1, 0);
        }
    }

    private void cleanSubscriptionInPhone(int i, boolean z) {
        String str;
        if (sInactiveIccIds[i] != null || (z && (str = sIccId[i]) != null && !str.equals(PhoneConfigurationManager.SSSS))) {
            logd("cleanSubscriptionInPhone: " + i + ", inactive iccid " + sInactiveIccIds[i]);
            if (sInactiveIccIds[i] == null) {
                logd("cleanSubscriptionInPhone: " + i + ", isSimAbsent=" + z + ", iccid=" + sIccId[i]);
            }
            String str2 = sInactiveIccIds[i];
            if (str2 == null) {
                str2 = sIccId[i];
            }
            ContentValues contentValues = new ContentValues();
            contentValues.put("uicc_applications_enabled", Boolean.TRUE);
            if (z) {
                contentValues.put("port_index", (Integer) (-1));
            }
            ContentResolver contentResolver = sContext.getContentResolver();
            Uri uri = SubscriptionManager.CONTENT_URI;
            contentResolver.update(uri, contentValues, "icc_id='" + str2 + "'", null);
            sInactiveIccIds[i] = null;
        }
        sIccId[i] = PhoneConfigurationManager.SSSS;
        updateSubscriptionInfoByIccId(i, true);
    }

    protected void handleSimAbsent(int i) {
        if (!SubscriptionManager.isValidPhoneId(i)) {
            logd("handleSimAbsent on invalid phoneId");
            return;
        }
        String str = sIccId[i];
        if (str != null && !str.equals(PhoneConfigurationManager.SSSS)) {
            logd("SIM" + (i + 1) + " hot plug out");
        }
        cleanSubscriptionInPhone(i, true);
        broadcastSimStateChanged(i, "ABSENT", null);
        broadcastSimCardStateChanged(i, 1);
        broadcastSimApplicationStateChanged(i, 0);
        updateSubscriptionCarrierId(i, "ABSENT");
        updateCarrierServices(i, "ABSENT");
    }

    protected void handleSimError(int i) {
        String str = sIccId[i];
        if (str != null && !str.equals(PhoneConfigurationManager.SSSS)) {
            logd("SIM" + (i + 1) + " Error ");
        }
        sIccId[i] = PhoneConfigurationManager.SSSS;
        updateSubscriptionInfoByIccId(i, true);
        broadcastSimStateChanged(i, "CARD_IO_ERROR", "CARD_IO_ERROR");
        broadcastSimCardStateChanged(i, 8);
        broadcastSimApplicationStateChanged(i, 6);
        updateSubscriptionCarrierId(i, "CARD_IO_ERROR");
        updateCarrierServices(i, "CARD_IO_ERROR");
    }

    protected synchronized void updateSubscriptionInfoByIccId(int i, boolean z) {
        logd("updateSubscriptionInfoByIccId:+ Start - phoneId: " + i);
        if (!SubscriptionManager.isValidPhoneId(i)) {
            loge("[updateSubscriptionInfoByIccId]- invalid phoneId=" + i);
            return;
        }
        logd("updateSubscriptionInfoByIccId: removing subscription info record: phoneId " + i);
        this.mSubscriptionController.clearSubInfoRecord(i);
        if (!PhoneConfigurationManager.SSSS.equals(sIccId[i]) && sIccId[i] != null) {
            logd("updateSubscriptionInfoByIccId: adding subscription info record: iccid: " + SubscriptionInfo.givePrintableIccid(sIccId[i]) + ", phoneId:" + i);
            this.mSubscriptionManager.addSubscriptionInfoRecord(sIccId[i], i);
        }
        List<SubscriptionInfo> subInfoUsingSlotIndexPrivileged = this.mSubscriptionController.getSubInfoUsingSlotIndexPrivileged(i);
        if (subInfoUsingSlotIndexPrivileged != null) {
            boolean z2 = false;
            for (int i2 = 0; i2 < subInfoUsingSlotIndexPrivileged.size(); i2++) {
                SubscriptionInfo subscriptionInfo = subInfoUsingSlotIndexPrivileged.get(i2);
                ContentValues contentValues = new ContentValues(1);
                String line1Number = TelephonyManager.getDefault().getLine1Number(subscriptionInfo.getSubscriptionId());
                if (!TextUtils.equals(line1Number, subscriptionInfo.getNumber())) {
                    contentValues.put(IccProvider.STR_NUMBER, line1Number);
                    sContext.getContentResolver().update(SubscriptionManager.getUriForSubscriptionId(subscriptionInfo.getSubscriptionId()), contentValues, null, null);
                    z2 = true;
                }
            }
            if (z2) {
                this.mSubscriptionController.refreshCachedActiveSubscriptionInfoList();
            }
        }
        if (isAllIccIdQueryDone()) {
            if (this.mSubscriptionManager.isActiveSubId(SubscriptionManager.getDefaultDataSubscriptionId())) {
                this.mSubscriptionManager.setDefaultDataSubId(SubscriptionManager.getDefaultDataSubscriptionId());
            } else {
                logd("bypass reset default data sub if inactive");
            }
            setSubInfoInitialized();
        }
        UiccController uiccController = UiccController.getInstance();
        UiccSlot[] uiccSlots = uiccController.getUiccSlots();
        if (uiccSlots != null && z) {
            ArrayList arrayList = new ArrayList();
            for (UiccSlot uiccSlot : uiccSlots) {
                if (uiccSlot != null && uiccSlot.getUiccCard() != null) {
                    arrayList.add(Integer.valueOf(uiccController.convertToPublicCardId(uiccSlot.getUiccCard().getCardId())));
                }
            }
            updateEmbeddedSubscriptions(arrayList, new UpdateEmbeddedSubsCallback() { // from class: com.android.internal.telephony.SubscriptionInfoUpdater$$ExternalSyntheticLambda7
                @Override // com.android.internal.telephony.SubscriptionInfoUpdater.UpdateEmbeddedSubsCallback
                public final void run(boolean z3) {
                    SubscriptionInfoUpdater.this.lambda$updateSubscriptionInfoByIccId$4(z3);
                }
            });
        }
        this.mSubscriptionController.notifySubscriptionInfoChanged();
        logd("updateSubscriptionInfoByIccId: SubscriptionInfo update complete");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateSubscriptionInfoByIccId$4(boolean z) {
        if (z) {
            this.mSubscriptionController.notifySubscriptionInfoChanged();
        }
        logd("updateSubscriptionInfoByIccId: SubscriptionInfo update complete");
    }

    private void setSubInfoInitialized() {
        if (!sIsSubInfoInitialized) {
            logd("SubInfo Initialized");
            sIsSubInfoInitialized = true;
            this.mSubscriptionController.notifySubInfoReady();
        }
        MultiSimSettingController.getInstance().notifyAllSubscriptionLoaded();
    }

    public static boolean isSubInfoInitialized() {
        return sIsSubInfoInitialized;
    }

    public static boolean isWorkProfileTelephonyEnabled() {
        return mIsWorkProfileTelephonyEnabled;
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public void updateEmbeddedSubscriptions(final List<Integer> list, final UpdateEmbeddedSubsCallback updateEmbeddedSubsCallback) {
        if (!this.mEuiccManager.isEnabled()) {
            logd("updateEmbeddedSubscriptions: eUICC not enabled");
            updateEmbeddedSubsCallback.run(false);
            return;
        }
        this.mBackgroundHandler.post(new Runnable() { // from class: com.android.internal.telephony.SubscriptionInfoUpdater$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                SubscriptionInfoUpdater.this.lambda$updateEmbeddedSubscriptions$6(list, updateEmbeddedSubsCallback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateEmbeddedSubscriptions$6(List list, final UpdateEmbeddedSubsCallback updateEmbeddedSubsCallback) {
        final ArrayList arrayList = new ArrayList();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            int intValue = ((Integer) it.next()).intValue();
            GetEuiccProfileInfoListResult blockingGetEuiccProfileInfoList = EuiccController.get().blockingGetEuiccProfileInfoList(intValue);
            logd("blockingGetEuiccProfileInfoList cardId " + intValue);
            arrayList.add(Pair.create(Integer.valueOf(intValue), blockingGetEuiccProfileInfoList));
        }
        post(new Runnable() { // from class: com.android.internal.telephony.SubscriptionInfoUpdater$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                SubscriptionInfoUpdater.this.lambda$updateEmbeddedSubscriptions$5(arrayList, updateEmbeddedSubsCallback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateEmbeddedSubscriptions$5(List list, UpdateEmbeddedSubsCallback updateEmbeddedSubsCallback) {
        Iterator it = list.iterator();
        boolean z = false;
        while (it.hasNext()) {
            Pair pair = (Pair) it.next();
            if (updateEmbeddedSubscriptionsCache(((Integer) pair.first).intValue(), (GetEuiccProfileInfoListResult) pair.second)) {
                z = true;
            }
        }
        if (updateEmbeddedSubsCallback != null) {
            updateEmbeddedSubsCallback.run(z);
        }
    }

    private boolean updateEmbeddedSubscriptionsCache(int i, GetEuiccProfileInfoListResult getEuiccProfileInfoListResult) {
        boolean z;
        SubscriptionInfo subscriptionInfo;
        int displayNameSource;
        int carrierId;
        logd("updateEmbeddedSubscriptionsCache");
        boolean z2 = true;
        if (getEuiccProfileInfoListResult == null) {
            logd("updateEmbeddedSubscriptionsCache: IPC to the eUICC controller failed");
            this.retryUpdateEmbeddedSubscriptionCards.add(Integer.valueOf(i));
            this.shouldRetryUpdateEmbeddedSubscriptions = true;
            return false;
        }
        List profiles = getEuiccProfileInfoListResult.getProfiles();
        if (getEuiccProfileInfoListResult.getResult() == 0 && profiles != null) {
            EuiccProfileInfo[] euiccProfileInfoArr = (EuiccProfileInfo[]) profiles.toArray(new EuiccProfileInfo[profiles.size()]);
            logd("blockingGetEuiccProfileInfoList: got " + getEuiccProfileInfoListResult.getProfiles().size() + " profiles");
            boolean isRemovable = getEuiccProfileInfoListResult.getIsRemovable();
            String[] strArr = new String[euiccProfileInfoArr.length];
            for (int i2 = 0; i2 < euiccProfileInfoArr.length; i2++) {
                strArr[i2] = euiccProfileInfoArr[i2].getIccid();
            }
            logd("Get eUICC profile list of size " + euiccProfileInfoArr.length);
            List<SubscriptionInfo> subscriptionInfoListForEmbeddedSubscriptionUpdate = this.mSubscriptionController.getSubscriptionInfoListForEmbeddedSubscriptionUpdate(strArr, isRemovable);
            ContentResolver contentResolver = sContext.getContentResolver();
            int length = euiccProfileInfoArr.length;
            int i3 = 0;
            boolean z3 = false;
            while (i3 < length) {
                EuiccProfileInfo euiccProfileInfo = euiccProfileInfoArr[i3];
                int findSubscriptionInfoForIccid = findSubscriptionInfoForIccid(subscriptionInfoListForEmbeddedSubscriptionUpdate, euiccProfileInfo.getIccid());
                if (findSubscriptionInfoForIccid < 0) {
                    this.mSubscriptionController.insertEmptySubInfoRecord(euiccProfileInfo.getIccid(), -1);
                    carrierId = -1;
                    displayNameSource = 0;
                } else {
                    displayNameSource = subscriptionInfoListForEmbeddedSubscriptionUpdate.get(findSubscriptionInfoForIccid).getDisplayNameSource();
                    carrierId = subscriptionInfoListForEmbeddedSubscriptionUpdate.get(findSubscriptionInfoForIccid).getCarrierId();
                    subscriptionInfoListForEmbeddedSubscriptionUpdate.remove(findSubscriptionInfoForIccid);
                }
                StringBuilder sb = new StringBuilder();
                sb.append("embeddedProfile ");
                sb.append(euiccProfileInfo);
                sb.append(" existing record ");
                sb.append(findSubscriptionInfoForIccid < 0 ? "not found" : "found");
                logd(sb.toString());
                ContentValues contentValues = new ContentValues();
                contentValues.put("is_embedded", (Integer) 1);
                List uiccAccessRules = euiccProfileInfo.getUiccAccessRules();
                contentValues.put("access_rules", uiccAccessRules == null || uiccAccessRules.size() == 0 ? null : UiccAccessRule.encodeRules((UiccAccessRule[]) uiccAccessRules.toArray(new UiccAccessRule[uiccAccessRules.size()])));
                contentValues.put("is_removable", Boolean.valueOf(isRemovable));
                if (SubscriptionController.getNameSourcePriority(displayNameSource) <= SubscriptionController.getNameSourcePriority(3)) {
                    contentValues.put("display_name", euiccProfileInfo.getNickname());
                    contentValues.put("name_source", (Integer) 3);
                }
                contentValues.put("profile_class", Integer.valueOf(euiccProfileInfo.getProfileClass()));
                contentValues.put("port_index", Integer.valueOf(getEmbeddedProfilePortIndex(euiccProfileInfo.getIccid())));
                CarrierIdentifier carrierIdentifier = euiccProfileInfo.getCarrierIdentifier();
                if (carrierIdentifier != null) {
                    if (carrierId == -1) {
                        contentValues.put("carrier_id", Integer.valueOf(CarrierResolver.getCarrierIdFromIdentifier(sContext, carrierIdentifier)));
                    }
                    String mcc = carrierIdentifier.getMcc();
                    String mnc = carrierIdentifier.getMnc();
                    contentValues.put("mcc_string", mcc);
                    contentValues.put("mcc", mcc);
                    contentValues.put("mnc_string", mnc);
                    contentValues.put("mnc", mnc);
                }
                UiccController uiccController = UiccController.getInstance();
                if (i >= 0 && uiccController.getCardIdForDefaultEuicc() != -1) {
                    contentValues.put("card_id", uiccController.convertToCardString(i));
                }
                contentResolver.update(SubscriptionManager.CONTENT_URI, contentValues, "icc_id='" + euiccProfileInfo.getIccid() + "'", null);
                this.mSubscriptionController.refreshCachedActiveSubscriptionInfoList();
                i3++;
                z2 = true;
                z3 = true;
            }
            boolean z4 = z2;
            if (subscriptionInfoListForEmbeddedSubscriptionUpdate.isEmpty()) {
                z = z3;
            } else {
                logd("Removing existing embedded subscriptions of size" + subscriptionInfoListForEmbeddedSubscriptionUpdate.size());
                ArrayList arrayList = new ArrayList();
                for (int i4 = 0; i4 < subscriptionInfoListForEmbeddedSubscriptionUpdate.size(); i4++) {
                    if (subscriptionInfoListForEmbeddedSubscriptionUpdate.get(i4).isEmbedded()) {
                        logd("Removing embedded subscription of IccId " + subscriptionInfo.getIccId());
                        arrayList.add("'" + subscriptionInfo.getIccId() + "'");
                    }
                }
                ContentValues contentValues2 = new ContentValues();
                contentValues2.put("is_embedded", (Integer) 0);
                contentResolver.update(SubscriptionManager.CONTENT_URI, contentValues2, "icc_id IN (" + TextUtils.join(",", arrayList) + ")", null);
                this.mSubscriptionController.refreshCachedActiveSubscriptionInfoList();
                z = z4;
            }
            logd("updateEmbeddedSubscriptions done hasChanges=" + z);
            return z;
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append("blockingGetEuiccProfileInfoList returns an error. Result code=");
        sb2.append(getEuiccProfileInfoListResult.getResult());
        sb2.append(". Null profile list=");
        sb2.append(getEuiccProfileInfoListResult.getProfiles() == null);
        logd(sb2.toString());
        return false;
    }

    private int getEmbeddedProfilePortIndex(String str) {
        UiccSlot[] uiccSlots;
        for (UiccSlot uiccSlot : UiccController.getInstance().getUiccSlots()) {
            if (uiccSlot != null && uiccSlot.isEuicc() && uiccSlot.getPortIndexFromIccId(str) != -1) {
                return uiccSlot.getPortIndexFromIccId(str);
            }
        }
        return -1;
    }

    public void updateSubscriptionByCarrierConfigAndNotifyComplete(final int i, final String str, final PersistableBundle persistableBundle, final Message message) {
        post(new Runnable() { // from class: com.android.internal.telephony.SubscriptionInfoUpdater$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                SubscriptionInfoUpdater.this.lambda$updateSubscriptionByCarrierConfigAndNotifyComplete$7(i, str, persistableBundle, message);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateSubscriptionByCarrierConfigAndNotifyComplete$7(int i, String str, PersistableBundle persistableBundle, Message message) {
        updateSubscriptionByCarrierConfig(i, str, persistableBundle);
        message.sendToTarget();
    }

    private String getDefaultCarrierServicePackageName() {
        return ((CarrierConfigManager) sContext.getSystemService("carrier_config")).getDefaultCarrierServicePackageName();
    }

    private boolean isCarrierServicePackage(int i, String str) {
        if (str.equals(getDefaultCarrierServicePackageName())) {
            return false;
        }
        String carrierServicePackageNameForLogicalSlot = TelephonyManager.from(sContext).getCarrierServicePackageNameForLogicalSlot(i);
        logd("Carrier service package for subscription = " + carrierServicePackageNameForLogicalSlot);
        return str.equals(carrierServicePackageNameForLogicalSlot);
    }

    /* JADX WARN: Removed duplicated region for block: B:46:0x0156  */
    /* JADX WARN: Removed duplicated region for block: B:47:0x0184  */
    /* JADX WARN: Removed duplicated region for block: B:50:0x01ae  */
    /* JADX WARN: Removed duplicated region for block: B:68:? A[ADDED_TO_REGION, RETURN, SYNTHETIC] */
    @VisibleForTesting
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void updateSubscriptionByCarrierConfig(int i, String str, PersistableBundle persistableBundle) {
        ParcelUuid parcelUuid;
        int calculateUsageSetting;
        if (!SubscriptionManager.isValidPhoneId(i) || TextUtils.isEmpty(str) || persistableBundle == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("In updateSubscriptionByCarrierConfig(): phoneId=");
            sb.append(i);
            sb.append(" configPackageName=");
            sb.append(str);
            sb.append(" config=");
            sb.append(persistableBundle == null ? "null" : Integer.valueOf(persistableBundle.hashCode()));
            logd(sb.toString());
            return;
        }
        int subId = this.mSubscriptionController.getSubId(i);
        if (!SubscriptionManager.isValidSubscriptionId(subId) || subId == Integer.MAX_VALUE) {
            logd("No subscription is active for phone being updated");
            return;
        }
        SubscriptionInfo subscriptionInfo = this.mSubscriptionController.getSubscriptionInfo(subId);
        if (subscriptionInfo == null) {
            loge("Couldn't retrieve subscription info for current subscription");
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("access_rules_from_carrier_configs", UiccAccessRule.encodeRules(UiccAccessRule.decodeRulesFromCarrierConfig(persistableBundle.getStringArray("carrier_certificate_string_array"))));
        if (!isCarrierServicePackage(i, str)) {
            loge("Cannot manage subId=" + subId + ", carrierPackage=" + str);
        } else {
            boolean z = persistableBundle.getBoolean("is_opportunistic_subscription_bool", subscriptionInfo.isOpportunistic());
            if (subscriptionInfo.isOpportunistic() != z) {
                logd("Set SubId=" + subId + " isOpportunistic=" + z);
                contentValues.put("is_opportunistic", z ? "1" : "0");
            }
            String string = persistableBundle.getString("subscription_group_uuid_string", PhoneConfigurationManager.SSSS);
            if (!TextUtils.isEmpty(string)) {
                try {
                    parcelUuid = ParcelUuid.fromString(string);
                } catch (IllegalArgumentException unused) {
                    parcelUuid = null;
                }
                try {
                    if (parcelUuid.equals(REMOVE_GROUP_UUID) && subscriptionInfo.getGroupUuid() != null) {
                        contentValues.put("group_uuid", (String) null);
                        logd("Group Removed for" + subId);
                    } else if (this.mSubscriptionController.canPackageManageGroup(parcelUuid, str)) {
                        contentValues.put("group_uuid", parcelUuid.toString());
                        contentValues.put("group_owner", str);
                        logd("Group Added for" + subId);
                    } else {
                        loge("configPackageName " + str + " doesn't own grouUuid " + parcelUuid);
                    }
                } catch (IllegalArgumentException unused2) {
                    loge("Invalid Group UUID=" + string);
                    int i2 = persistableBundle.getInt("cellular_usage_setting_int", -1);
                    calculateUsageSetting = calculateUsageSetting(subscriptionInfo.getUsageSetting(), i2);
                    if (calculateUsageSetting != subscriptionInfo.getUsageSetting()) {
                    }
                    if (contentValues.size() <= 0) {
                    }
                }
                int i22 = persistableBundle.getInt("cellular_usage_setting_int", -1);
                calculateUsageSetting = calculateUsageSetting(subscriptionInfo.getUsageSetting(), i22);
                if (calculateUsageSetting != subscriptionInfo.getUsageSetting()) {
                    contentValues.put("usage_setting", Integer.valueOf(calculateUsageSetting));
                    logd("UsageSetting changed, oldSetting=" + subscriptionInfo.getUsageSetting() + " preferredSetting=" + i22 + " newSetting=" + calculateUsageSetting);
                } else {
                    logd("UsageSetting unchanged, oldSetting=" + subscriptionInfo.getUsageSetting() + " preferredSetting=" + i22 + " newSetting=" + calculateUsageSetting);
                }
                if (contentValues.size() <= 0 || sContext.getContentResolver().update(SubscriptionManager.getUriForSubscriptionId(subId), contentValues, null, null) <= 0) {
                }
                this.mSubscriptionController.refreshCachedActiveSubscriptionInfoList();
                this.mSubscriptionController.notifySubscriptionInfoChanged();
                MultiSimSettingController.getInstance().notifySubscriptionGroupChanged(parcelUuid);
                return;
            }
        }
        parcelUuid = null;
        int i222 = persistableBundle.getInt("cellular_usage_setting_int", -1);
        calculateUsageSetting = calculateUsageSetting(subscriptionInfo.getUsageSetting(), i222);
        if (calculateUsageSetting != subscriptionInfo.getUsageSetting()) {
        }
        if (contentValues.size() <= 0) {
        }
    }

    private static int findSubscriptionInfoForIccid(List<SubscriptionInfo> list, String str) {
        for (int i = 0; i < list.size(); i++) {
            if (TextUtils.equals(str, list.get(i).getIccId())) {
                return i;
            }
        }
        return -1;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected void broadcastSimStateChanged(int i, String str, String str2) {
        Intent intent = new Intent("android.intent.action.SIM_STATE_CHANGED");
        intent.addFlags(67108864);
        intent.putExtra("phoneName", "Phone");
        intent.putExtra("ss", str);
        intent.putExtra("reason", str2);
        SubscriptionManager.putPhoneIdAndSubIdExtra(intent, i);
        logd("Broadcasting intent ACTION_SIM_STATE_CHANGED " + str + " reason " + str2 + " for phone: " + i);
        IntentBroadcaster.getInstance().broadcastStickyIntent(sContext, intent, i);
    }

    protected void broadcastSimCardStateChanged(int i, int i2) {
        int[] iArr = sSimCardState;
        if (i2 != iArr[i]) {
            iArr[i] = i2;
            Intent intent = new Intent("android.telephony.action.SIM_CARD_STATE_CHANGED");
            intent.addFlags(67108864);
            intent.putExtra("android.telephony.extra.SIM_STATE", i2);
            SubscriptionManager.putPhoneIdAndSubIdExtra(intent, i);
            UiccSlot uiccSlotForPhone = UiccController.getInstance().getUiccSlotForPhone(i);
            int slotIdFromPhoneId = UiccController.getInstance().getSlotIdFromPhoneId(i);
            intent.putExtra("slot", slotIdFromPhoneId);
            if (uiccSlotForPhone != null) {
                intent.putExtra("port", uiccSlotForPhone.getPortIndexFromPhoneId(i));
            }
            logd("Broadcasting intent ACTION_SIM_CARD_STATE_CHANGED " + TelephonyManager.simStateToString(i2) + " for phone: " + i + " slot: " + slotIdFromPhoneId + " port: " + uiccSlotForPhone.getPortIndexFromPhoneId(i));
            sContext.sendBroadcast(intent, "android.permission.READ_PRIVILEGED_PHONE_STATE");
            TelephonyMetrics.getInstance().updateSimState(i, i2);
        }
    }

    protected void broadcastSimApplicationStateChanged(int i, int i2) {
        boolean z = true;
        boolean z2 = sSimApplicationState[i] == 0 && i2 == 6;
        IccCard iccCard = PhoneFactory.getPhone(i).getIccCard();
        if (iccCard == null || !iccCard.isEmptyProfile()) {
            z = false;
        }
        int[] iArr = sSimApplicationState;
        if (i2 != iArr[i]) {
            if (!z2 || z) {
                iArr[i] = i2;
                Intent intent = new Intent("android.telephony.action.SIM_APPLICATION_STATE_CHANGED");
                intent.addFlags(67108864);
                intent.putExtra("android.telephony.extra.SIM_STATE", i2);
                SubscriptionManager.putPhoneIdAndSubIdExtra(intent, i);
                UiccSlot uiccSlotForPhone = UiccController.getInstance().getUiccSlotForPhone(i);
                int slotIdFromPhoneId = UiccController.getInstance().getSlotIdFromPhoneId(i);
                intent.putExtra("slot", slotIdFromPhoneId);
                if (uiccSlotForPhone != null) {
                    intent.putExtra("port", uiccSlotForPhone.getPortIndexFromPhoneId(i));
                }
                logd("Broadcasting intent ACTION_SIM_APPLICATION_STATE_CHANGED " + TelephonyManager.simStateToString(i2) + " for phone: " + i + " slot: " + slotIdFromPhoneId + "port: " + uiccSlotForPhone.getPortIndexFromPhoneId(i));
                sContext.sendBroadcast(intent, "android.permission.READ_PRIVILEGED_PHONE_STATE");
                TelephonyMetrics.getInstance().updateSimState(i, i2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public static void logd(String str) {
        Rlog.d("SubscriptionInfoUpdater", str);
    }

    private static void loge(String str) {
        Rlog.e("SubscriptionInfoUpdater", str);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("SubscriptionInfoUpdater:");
        this.mCarrierServiceBindHelper.dump(fileDescriptor, printWriter, strArr);
    }
}
