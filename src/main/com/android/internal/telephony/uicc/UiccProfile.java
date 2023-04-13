package com.android.internal.telephony.uicc;

import android.app.ActivityManager;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.os.AsyncResult;
import android.os.Binder;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.os.PersistableBundle;
import android.os.UserManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.CarrierConfigManager;
import android.telephony.ServiceState;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.UiccAccessRule;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.AndroidUtilIndentingPrintWriter;
import com.android.internal.telephony.CarrierAppUtils;
import com.android.internal.telephony.CarrierPrivilegesTracker;
import com.android.internal.telephony.CommandsInterface;
import com.android.internal.telephony.IccCard;
import com.android.internal.telephony.IccCardConstants;
import com.android.internal.telephony.MccTable;
import com.android.internal.telephony.NetworkTypeController$$ExternalSyntheticLambda0;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.Registrant;
import com.android.internal.telephony.RegistrantList;
import com.android.internal.telephony.SubscriptionController;
import com.android.internal.telephony.TelephonyStatsLog;
import com.android.internal.telephony.cat.CatService;
import com.android.internal.telephony.subscription.SubscriptionInfoInternal;
import com.android.internal.telephony.subscription.SubscriptionManagerService;
import com.android.internal.telephony.uicc.IccCardApplicationStatus;
import com.android.internal.telephony.uicc.IccCardStatus;
import com.android.internal.telephony.uicc.euicc.EuiccCard;
import com.android.internal.telephony.util.NetworkStackConstants;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class UiccProfile extends IccCard {
    protected static final boolean DBG = true;
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public static final int EVENT_APP_READY = 3;
    protected static final String LOG_TAG = "UiccProfile";
    private final CarrierConfigManager.CarrierConfigChangeListener mCarrierConfigChangeListener;
    private final CarrierConfigManager mCarrierConfigManager;
    private UiccCarrierPrivilegeRules mCarrierPrivilegeRules;
    private CatService mCatService;
    private int mCdmaSubscriptionAppIndex;
    private CommandsInterface mCi;
    private Context mContext;
    private int mGsmUmtsSubscriptionAppIndex;
    @VisibleForTesting
    public final Handler mHandler;
    private int mImsSubscriptionAppIndex;
    private int mLastReportedNumOfUiccApplications;
    private final Object mLock;
    private final int mPhoneId;
    private final PinStorage mPinStorage;
    private boolean mProvisionCompleteContentObserverRegistered;
    private TelephonyManager mTelephonyManager;
    private UiccCarrierPrivilegeRules mTestOverrideCarrierPrivilegeRules;
    private final UiccCard mUiccCard;
    private IccCardStatus.PinState mUniversalPinState;
    private boolean mUserUnlockReceiverRegistered;
    private UiccCardApplication[] mUiccApplications = new UiccCardApplication[8];
    private boolean mDisposed = false;
    private RegistrantList mOperatorBrandOverrideRegistrants = new RegistrantList();
    private RegistrantList mNetworkLockedRegistrants = new RegistrantList();
    @VisibleForTesting
    public int mCurrentAppType = 1;
    private int mRadioTech = 0;
    private UiccCardApplication mUiccApplication = null;
    private IccRecords mIccRecords = null;
    private IccCardConstants.State mExternalState = IccCardConstants.State.UNKNOWN;
    private final ContentObserver mProvisionCompleteContentObserver = new ContentObserver(new Handler()) { // from class: com.android.internal.telephony.uicc.UiccProfile.1
        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            synchronized (UiccProfile.this.mLock) {
                UiccProfile.this.mContext.getContentResolver().unregisterContentObserver(this);
                UiccProfile.this.mProvisionCompleteContentObserverRegistered = false;
                UiccProfile.this.showCarrierAppNotificationsIfPossible();
            }
        }
    };
    private final BroadcastReceiver mUserUnlockReceiver = new BroadcastReceiver() { // from class: com.android.internal.telephony.uicc.UiccProfile.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            synchronized (UiccProfile.this.mLock) {
                UiccProfile.this.mContext.unregisterReceiver(this);
                UiccProfile.this.mUserUnlockReceiverRegistered = false;
                UiccProfile.this.showCarrierAppNotificationsIfPossible();
            }
        }
    };

    public UiccProfile(Context context, CommandsInterface commandsInterface, IccCardStatus iccCardStatus, int i, UiccCard uiccCard, Object obj) {
        CarrierConfigManager.CarrierConfigChangeListener carrierConfigChangeListener = new CarrierConfigManager.CarrierConfigChangeListener() { // from class: com.android.internal.telephony.uicc.UiccProfile.3
            public void onCarrierConfigChanged(int i2, int i3, int i4, int i5) {
                if (i2 == UiccProfile.this.mPhoneId) {
                    UiccProfile.log("onCarrierConfigChanged: slotIndex=" + i2 + ", subId=" + i3 + ", carrierId=" + i4);
                    UiccProfile.this.handleCarrierNameOverride();
                    UiccProfile.this.handleSimCountryIsoOverride();
                }
            }
        };
        this.mCarrierConfigChangeListener = carrierConfigChangeListener;
        Handler handler = new Handler() { // from class: com.android.internal.telephony.uicc.UiccProfile.4
            /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                CarrierPrivilegesTracker carrierPrivilegesTracker;
                int i2;
                String eventToString = UiccProfile.eventToString(message.what);
                if (UiccProfile.this.mDisposed && (i2 = message.what) != 8 && i2 != 9 && i2 != 10 && i2 != 11 && i2 != 12) {
                    UiccProfile.loge("handleMessage: Received " + eventToString + " after dispose(); ignoring the message");
                    return;
                }
                UiccProfile uiccProfile = UiccProfile.this;
                uiccProfile.logWithLocalLog("handleMessage: Received " + eventToString + " for phoneId " + UiccProfile.this.mPhoneId);
                switch (message.what) {
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 6:
                        break;
                    case 5:
                        if (UiccProfile.this.mUiccApplication != null) {
                            UiccProfile.this.mNetworkLockedRegistrants.notifyRegistrants(new AsyncResult((Object) null, Integer.valueOf(UiccProfile.this.mUiccApplication.getPersoSubState().ordinal()), (Throwable) null));
                            break;
                        } else {
                            UiccProfile.log("EVENT_NETWORK_LOCKED: mUiccApplication is NULL, mNetworkLockedRegistrants not notified.");
                            break;
                        }
                    case 7:
                        UiccProfile uiccProfile2 = UiccProfile.this;
                        if (uiccProfile2.mCurrentAppType == 1 && uiccProfile2.mIccRecords != null && ((Integer) ((AsyncResult) message.obj).result).intValue() == 2) {
                            UiccProfile.this.mTelephonyManager.setSimOperatorNameForPhone(UiccProfile.this.mPhoneId, UiccProfile.this.mIccRecords.getServiceProviderName());
                            return;
                        }
                        return;
                    case 8:
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                        AsyncResult asyncResult = (AsyncResult) message.obj;
                        if (asyncResult.exception != null) {
                            UiccProfile uiccProfile3 = UiccProfile.this;
                            uiccProfile3.logWithLocalLog("handleMessage: Error in SIM access with exception " + asyncResult.exception);
                        }
                        Object obj2 = asyncResult.userObj;
                        if (obj2 != null) {
                            AsyncResult.forMessage((Message) obj2, asyncResult.result, asyncResult.exception);
                            ((Message) asyncResult.userObj).sendToTarget();
                            return;
                        }
                        UiccProfile.loge("handleMessage: ar.userObj is null in event:" + eventToString + ", failed to post status back to caller");
                        return;
                    case 13:
                        Phone phone = PhoneFactory.getPhone(UiccProfile.this.mPhoneId);
                        if (phone != null && (carrierPrivilegesTracker = phone.getCarrierPrivilegesTracker()) != null) {
                            carrierPrivilegesTracker.onUiccAccessRulesLoaded();
                        }
                        UiccProfile.this.onCarrierPrivilegesLoadedMessage();
                        UiccProfile.this.updateExternalState();
                        return;
                    case 14:
                        UiccProfile.this.handleCarrierNameOverride();
                        UiccProfile.this.handleSimCountryIsoOverride();
                        return;
                    case 15:
                        Object obj3 = message.obj;
                        if (obj3 == null) {
                            UiccProfile.this.mTestOverrideCarrierPrivilegeRules = null;
                        } else {
                            UiccProfile.this.mTestOverrideCarrierPrivilegeRules = new UiccCarrierPrivilegeRules((List) obj3);
                        }
                        UiccProfile.this.refresh();
                        return;
                    case 16:
                        AsyncResult asyncResult2 = (AsyncResult) message.obj;
                        if (asyncResult2.exception != null) {
                            UiccProfile.loge("An error occurred during internal PIN verification");
                            UiccProfile.this.mPinStorage.clearPin(UiccProfile.this.mPhoneId);
                            UiccProfile.this.updateExternalState();
                        } else {
                            UiccProfile.log("Internal PIN verification was successful!");
                        }
                        TelephonyStatsLog.write(336, asyncResult2.exception == null ? 1 : 2, 1);
                        return;
                    default:
                        UiccProfile.loge("handleMessage: Unhandled message with number: " + message.what);
                        return;
                }
                UiccProfile.this.updateExternalState();
            }
        };
        this.mHandler = handler;
        log("Creating profile");
        this.mLock = obj;
        this.mUiccCard = uiccCard;
        this.mPhoneId = i;
        Phone phone = PhoneFactory.getPhone(i);
        if (phone != null) {
            setCurrentAppType(phone.getPhoneType() == 1);
        }
        if (uiccCard instanceof EuiccCard) {
            ((EuiccCard) uiccCard).registerForEidReady(handler, 6, null);
        }
        this.mPinStorage = UiccController.getInstance().getPinStorage();
        update(context, commandsInterface, iccCardStatus);
        commandsInterface.registerForOffOrNotAvailable(handler, 1, null);
        resetProperties();
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
        this.mCarrierConfigManager = carrierConfigManager;
        Objects.requireNonNull(handler);
        carrierConfigManager.registerCarrierConfigChangeListener(new NetworkTypeController$$ExternalSyntheticLambda0(handler), carrierConfigChangeListener);
    }

    public void dispose() {
        UiccCardApplication[] uiccCardApplicationArr;
        CarrierConfigManager.CarrierConfigChangeListener carrierConfigChangeListener;
        log("Disposing profile");
        UiccCard uiccCard = this.mUiccCard;
        if (uiccCard instanceof EuiccCard) {
            ((EuiccCard) uiccCard).unregisterForEidReady(this.mHandler);
        }
        synchronized (this.mLock) {
            unregisterAllAppEvents();
            unregisterCurrAppEvents();
            if (this.mProvisionCompleteContentObserverRegistered) {
                this.mContext.getContentResolver().unregisterContentObserver(this.mProvisionCompleteContentObserver);
                this.mProvisionCompleteContentObserverRegistered = false;
            }
            if (this.mUserUnlockReceiverRegistered) {
                this.mContext.unregisterReceiver(this.mUserUnlockReceiver);
                this.mUserUnlockReceiverRegistered = false;
            }
            InstallCarrierAppUtils.hideAllNotifications(this.mContext);
            InstallCarrierAppUtils.unregisterPackageInstallReceiver(this.mContext);
            this.mCi.unregisterForOffOrNotAvailable(this.mHandler);
            CarrierConfigManager carrierConfigManager = this.mCarrierConfigManager;
            if (carrierConfigManager != null && (carrierConfigChangeListener = this.mCarrierConfigChangeListener) != null) {
                carrierConfigManager.unregisterCarrierConfigChangeListener(carrierConfigChangeListener);
            }
            CatService catService = this.mCatService;
            if (catService != null) {
                catService.dispose();
            }
            for (UiccCardApplication uiccCardApplication : this.mUiccApplications) {
                if (uiccCardApplication != null) {
                    uiccCardApplication.dispose();
                }
            }
            this.mCatService = null;
            this.mUiccApplications = null;
            this.mRadioTech = 0;
            this.mCarrierPrivilegeRules = null;
            this.mContext.getContentResolver().unregisterContentObserver(this.mProvisionCompleteContentObserver);
            this.mDisposed = true;
        }
    }

    public void setVoiceRadioTech(int i) {
        synchronized (this.mLock) {
            log("Setting radio tech " + ServiceState.rilRadioTechnologyToString(i));
            this.mRadioTech = i;
            setCurrentAppType(ServiceState.isGsm(i));
            updateIccAvailability(false);
        }
    }

    private void setCurrentAppType(boolean z) {
        int i = 1;
        int i2 = 2;
        if (!z) {
            i2 = 1;
            i = 2;
        }
        synchronized (this.mLock) {
            if (getApplication(i) == null && getApplication(i2) != null) {
                this.mCurrentAppType = i2;
            }
            this.mCurrentAppType = i;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleCarrierNameOverride() {
        Phone phone;
        int subscriptionId = SubscriptionManager.getSubscriptionId(this.mPhoneId);
        if (subscriptionId == -1) {
            loge("subId not valid for Phone " + this.mPhoneId);
        } else if (((CarrierConfigManager) this.mContext.getSystemService("carrier_config")) == null) {
            loge("Failed to load a Carrier Config");
        } else {
            PersistableBundle carrierConfigSubset = CarrierConfigManager.getCarrierConfigSubset(this.mContext, subscriptionId, new String[]{"carrier_name_override_bool", "carrier_name_string"});
            if (carrierConfigSubset.isEmpty()) {
                loge("handleCarrierNameOverride: fail to get carrier configs.");
                return;
            }
            int i = 0;
            boolean z = carrierConfigSubset.getBoolean("carrier_name_override_bool", false);
            String string = carrierConfigSubset.getString("carrier_name_string");
            String serviceProviderName = getServiceProviderName();
            if (z || (TextUtils.isEmpty(serviceProviderName) && !TextUtils.isEmpty(string))) {
                i = 3;
            } else if (!TextUtils.isEmpty(serviceProviderName) || (phone = PhoneFactory.getPhone(this.mPhoneId)) == null) {
                string = null;
                i = 1;
            } else {
                String plmn = phone.getPlmn();
                if (TextUtils.isEmpty(plmn)) {
                    string = phone.getCarrierName();
                } else {
                    i = 4;
                    string = plmn;
                }
            }
            if (!TextUtils.isEmpty(string)) {
                this.mTelephonyManager.setSimOperatorNameForPhone(this.mPhoneId, string);
                this.mOperatorBrandOverrideRegistrants.notifyRegistrants();
            }
            updateCarrierNameForSubscription(subscriptionId, i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSimCountryIsoOverride() {
        int subscriptionId = SubscriptionManager.getSubscriptionId(this.mPhoneId);
        if (subscriptionId == -1) {
            loge("subId not valid for Phone " + this.mPhoneId);
        } else if (((CarrierConfigManager) this.mContext.getSystemService("carrier_config")) == null) {
            loge("Failed to load a Carrier Config");
        } else {
            PersistableBundle carrierConfigSubset = CarrierConfigManager.getCarrierConfigSubset(this.mContext, subscriptionId, new String[]{"sim_country_iso_override_string"});
            if (carrierConfigSubset.isEmpty()) {
                loge("handleSimCountryIsoOverride: fail to get carrier configs.");
                return;
            }
            String string = carrierConfigSubset.getString("sim_country_iso_override_string");
            if (TextUtils.isEmpty(string) || string.equals(TelephonyManager.getSimCountryIsoForPhone(this.mPhoneId))) {
                return;
            }
            this.mTelephonyManager.setSimCountryIsoForPhone(this.mPhoneId, string);
            if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
                SubscriptionManagerService.getInstance().setCountryIso(subscriptionId, string);
            } else {
                SubscriptionController.getInstance().setCountryIso(string, subscriptionId);
            }
        }
    }

    private void updateCarrierNameForSubscription(int i, int i2) {
        SubscriptionInfo activeSubscriptionInfo;
        if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
            activeSubscriptionInfo = SubscriptionManagerService.getInstance().getActiveSubscriptionInfo(i, this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
        } else {
            activeSubscriptionInfo = SubscriptionController.getInstance().getActiveSubscriptionInfo(i, this.mContext.getOpPackageName(), this.mContext.getAttributionTag());
        }
        if (activeSubscriptionInfo == null) {
            return;
        }
        CharSequence displayName = activeSubscriptionInfo.getDisplayName();
        String simOperatorName = this.mTelephonyManager.getSimOperatorName(i);
        if (TextUtils.isEmpty(simOperatorName) || simOperatorName.equals(displayName)) {
            return;
        }
        log("sim name[" + this.mPhoneId + "] = " + simOperatorName);
        if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
            SubscriptionManagerService.getInstance().setDisplayNameUsingSrc(simOperatorName, i, i2);
        } else {
            SubscriptionController.getInstance().setDisplayNameUsingSrc(simOperatorName, i, i2);
        }
    }

    private void updateIccAvailability(boolean z) {
        synchronized (this.mLock) {
            UiccCardApplication application = getApplication(this.mCurrentAppType);
            IccRecords iccRecords = application != null ? application.getIccRecords() : null;
            if (z) {
                unregisterAllAppEvents();
                registerAllAppEvents();
            }
            if (this.mIccRecords != iccRecords || this.mUiccApplication != application) {
                log("Icc changed. Reregistering.");
                unregisterCurrAppEvents();
                this.mUiccApplication = application;
                this.mIccRecords = iccRecords;
                registerCurrAppEvents();
            }
            updateExternalState();
        }
    }

    void resetProperties() {
        if (this.mCurrentAppType == 1) {
            log("update icc_operator_numeric=");
            this.mTelephonyManager.setSimOperatorNumericForPhone(this.mPhoneId, PhoneConfigurationManager.SSSS);
            this.mTelephonyManager.setSimCountryIsoForPhone(this.mPhoneId, PhoneConfigurationManager.SSSS);
            this.mTelephonyManager.setSimOperatorNameForPhone(this.mPhoneId, PhoneConfigurationManager.SSSS);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:37:0x007c  */
    /* JADX WARN: Removed duplicated region for block: B:52:0x00d9  */
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void updateExternalState() {
        boolean z;
        IccCardConstants.State state;
        IccCardConstants.State state2;
        if (this.mUiccCard.getCardState() == IccCardStatus.CardState.CARDSTATE_ERROR) {
            setExternalState(IccCardConstants.State.CARD_IO_ERROR);
        } else if (this.mUiccCard.getCardState() == IccCardStatus.CardState.CARDSTATE_RESTRICTED) {
            setExternalState(IccCardConstants.State.CARD_RESTRICTED);
        } else {
            UiccCard uiccCard = this.mUiccCard;
            if ((uiccCard instanceof EuiccCard) && ((EuiccCard) uiccCard).getEid() == null) {
                log("EID is not ready yet.");
                return;
            }
            UiccCardApplication uiccCardApplication = this.mUiccApplication;
            if (uiccCardApplication == null) {
                loge("updateExternalState: setting state to NOT_READY because mUiccApplication is null");
                setExternalState(IccCardConstants.State.NOT_READY);
                return;
            }
            IccCardApplicationStatus.AppState state3 = uiccCardApplication.getState();
            if (this.mUiccApplication.getPin1State() == IccCardStatus.PinState.PINSTATE_ENABLED_PERM_BLOCKED) {
                state2 = IccCardConstants.State.PERM_DISABLED;
            } else if (state3 == IccCardApplicationStatus.AppState.APPSTATE_PIN) {
                state2 = IccCardConstants.State.PIN_REQUIRED;
            } else if (state3 == IccCardApplicationStatus.AppState.APPSTATE_PUK) {
                state2 = IccCardConstants.State.PUK_REQUIRED;
            } else if (state3 != IccCardApplicationStatus.AppState.APPSTATE_SUBSCRIPTION_PERSO || !IccCardApplicationStatus.PersoSubState.isPersoLocked(this.mUiccApplication.getPersoSubState())) {
                z = false;
                state = null;
                if (!z) {
                    IccRecords iccRecords = this.mIccRecords;
                    if (iccRecords != null && (iccRecords.getLockedRecordsLoaded() || this.mIccRecords.getNetworkLockedRecordsLoaded())) {
                        if (state == IccCardConstants.State.PIN_REQUIRED) {
                            String pin = this.mPinStorage.getPin(this.mPhoneId, this.mIccRecords.getFullIccId());
                            if (!pin.isEmpty()) {
                                log("PIN_REQUIRED[" + this.mPhoneId + "] - Cache present");
                                this.mCi.supplyIccPin(pin, this.mHandler.obtainMessage(16));
                                return;
                            }
                        }
                        setExternalState(state);
                        return;
                    }
                    setExternalState(IccCardConstants.State.NOT_READY);
                    return;
                }
                int i = C03385.f26xec503f36[state3.ordinal()];
                if (i == 1) {
                    setExternalState(IccCardConstants.State.NOT_READY);
                    return;
                } else if (i == 2) {
                    setExternalState(IccCardConstants.State.NOT_READY);
                    return;
                } else if (i != 3) {
                    return;
                } else {
                    checkAndUpdateIfAnyAppToBeIgnored();
                    if (areAllApplicationsReady()) {
                        if (areAllRecordsLoaded() && areCarrierPrivilegeRulesLoaded()) {
                            setExternalState(IccCardConstants.State.LOADED);
                            return;
                        } else {
                            setExternalState(IccCardConstants.State.READY);
                            return;
                        }
                    }
                    setExternalState(IccCardConstants.State.NOT_READY);
                    return;
                }
            } else {
                state2 = IccCardConstants.State.NETWORK_LOCKED;
            }
            state = state2;
            z = true;
            if (!z) {
            }
        }
    }

    private void registerAllAppEvents() {
        UiccCardApplication[] uiccCardApplicationArr;
        for (UiccCardApplication uiccCardApplication : this.mUiccApplications) {
            if (uiccCardApplication != null) {
                uiccCardApplication.registerForReady(this.mHandler, 3, null);
                IccRecords iccRecords = uiccCardApplication.getIccRecords();
                if (iccRecords != null) {
                    iccRecords.registerForRecordsLoaded(this.mHandler, 4, null);
                    iccRecords.registerForRecordsEvents(this.mHandler, 7, null);
                }
            }
        }
    }

    private void unregisterAllAppEvents() {
        UiccCardApplication[] uiccCardApplicationArr;
        for (UiccCardApplication uiccCardApplication : this.mUiccApplications) {
            if (uiccCardApplication != null) {
                uiccCardApplication.unregisterForReady(this.mHandler);
                IccRecords iccRecords = uiccCardApplication.getIccRecords();
                if (iccRecords != null) {
                    iccRecords.unregisterForRecordsLoaded(this.mHandler);
                    iccRecords.unregisterForRecordsEvents(this.mHandler);
                }
            }
        }
    }

    private void registerCurrAppEvents() {
        IccRecords iccRecords = this.mIccRecords;
        if (iccRecords != null) {
            iccRecords.registerForLockedRecordsLoaded(this.mHandler, 2, null);
            this.mIccRecords.registerForNetworkLockedRecordsLoaded(this.mHandler, 5, null);
        }
    }

    private void unregisterCurrAppEvents() {
        IccRecords iccRecords = this.mIccRecords;
        if (iccRecords != null) {
            iccRecords.unregisterForLockedRecordsLoaded(this.mHandler);
            this.mIccRecords.unregisterForNetworkLockedRecordsLoaded(this.mHandler);
        }
    }

    private void setExternalState(IccCardConstants.State state, boolean z) {
        IccRecords iccRecords;
        synchronized (this.mLock) {
            if (!SubscriptionManager.isValidSlotIndex(this.mPhoneId)) {
                loge("setExternalState: mPhoneId=" + this.mPhoneId + " is invalid; Return!!");
            } else if (!z && state == this.mExternalState) {
                log("setExternalState: !override and newstate unchanged from " + state);
            } else {
                this.mExternalState = state;
                if (state == IccCardConstants.State.LOADED && (iccRecords = this.mIccRecords) != null) {
                    String operatorNumeric = iccRecords.getOperatorNumeric();
                    log("setExternalState: operator=" + operatorNumeric + " mPhoneId=" + this.mPhoneId);
                    if (!TextUtils.isEmpty(operatorNumeric)) {
                        this.mTelephonyManager.setSimOperatorNumericForPhone(this.mPhoneId, operatorNumeric);
                        String substring = operatorNumeric.substring(0, 3);
                        if (substring != null) {
                            this.mTelephonyManager.setSimCountryIsoForPhone(this.mPhoneId, MccTable.countryCodeForMcc(substring));
                        } else {
                            loge("setExternalState: state LOADED; Country code is null");
                        }
                    } else {
                        loge("setExternalState: state LOADED; Operator name is null");
                    }
                }
                log("setExternalState: set mPhoneId=" + this.mPhoneId + " mExternalState=" + this.mExternalState);
                if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
                    UiccController uiccController = UiccController.getInstance();
                    int i = this.mPhoneId;
                    IccCardConstants.State state2 = this.mExternalState;
                    uiccController.updateSimState(i, state2, getIccStateReason(state2));
                } else {
                    Context context = this.mContext;
                    IccCardConstants.State state3 = this.mExternalState;
                    UiccController.updateInternalIccState(context, state3, getIccStateReason(state3), this.mPhoneId);
                }
            }
        }
    }

    private void setExternalState(IccCardConstants.State state) {
        setExternalState(state, false);
    }

    public boolean getIccRecordsLoaded() {
        synchronized (this.mLock) {
            IccRecords iccRecords = this.mIccRecords;
            if (iccRecords != null) {
                return iccRecords.getRecordsLoaded();
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.internal.telephony.uicc.UiccProfile$5 */
    /* loaded from: classes.dex */
    public static /* synthetic */ class C03385 {
        static final /* synthetic */ int[] $SwitchMap$com$android$internal$telephony$IccCardConstants$State;

        /* renamed from: $SwitchMap$com$android$internal$telephony$uicc$IccCardApplicationStatus$AppState */
        static final /* synthetic */ int[] f26xec503f36;

        static {
            int[] iArr = new int[IccCardConstants.State.values().length];
            $SwitchMap$com$android$internal$telephony$IccCardConstants$State = iArr;
            try {
                iArr[IccCardConstants.State.PIN_REQUIRED.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$IccCardConstants$State[IccCardConstants.State.PUK_REQUIRED.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$IccCardConstants$State[IccCardConstants.State.NETWORK_LOCKED.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$IccCardConstants$State[IccCardConstants.State.PERM_DISABLED.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$IccCardConstants$State[IccCardConstants.State.CARD_IO_ERROR.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$IccCardConstants$State[IccCardConstants.State.CARD_RESTRICTED.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            int[] iArr2 = new int[IccCardApplicationStatus.AppState.values().length];
            f26xec503f36 = iArr2;
            try {
                iArr2[IccCardApplicationStatus.AppState.APPSTATE_UNKNOWN.ordinal()] = 1;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                f26xec503f36[IccCardApplicationStatus.AppState.APPSTATE_DETECTED.ordinal()] = 2;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                f26xec503f36[IccCardApplicationStatus.AppState.APPSTATE_READY.ordinal()] = 3;
            } catch (NoSuchFieldError unused9) {
            }
        }
    }

    private String getIccStateReason(IccCardConstants.State state) {
        switch (C03385.$SwitchMap$com$android$internal$telephony$IccCardConstants$State[state.ordinal()]) {
            case 1:
                return "PIN";
            case 2:
                return "PUK";
            case 3:
                return "NETWORK";
            case 4:
                return "PERM_DISABLED";
            case 5:
                return "CARD_IO_ERROR";
            case 6:
                return "CARD_RESTRICTED";
            default:
                return null;
        }
    }

    @Override // com.android.internal.telephony.IccCard
    public IccCardConstants.State getState() {
        IccCardConstants.State state;
        synchronized (this.mLock) {
            state = this.mExternalState;
        }
        return state;
    }

    @Override // com.android.internal.telephony.IccCard
    public IccRecords getIccRecords() {
        IccRecords iccRecords;
        synchronized (this.mLock) {
            iccRecords = this.mIccRecords;
        }
        return iccRecords;
    }

    @Override // com.android.internal.telephony.IccCard
    public void registerForNetworkLocked(Handler handler, int i, Object obj) {
        synchronized (this.mLock) {
            Registrant registrant = new Registrant(handler, i, obj);
            this.mNetworkLockedRegistrants.add(registrant);
            if (getState() == IccCardConstants.State.NETWORK_LOCKED) {
                if (this.mUiccApplication != null) {
                    registrant.notifyRegistrant(new AsyncResult((Object) null, Integer.valueOf(this.mUiccApplication.getPersoSubState().ordinal()), (Throwable) null));
                } else {
                    log("registerForNetworkLocked: not notifying registrants, mUiccApplication == null");
                }
            }
        }
    }

    @Override // com.android.internal.telephony.IccCard
    public void unregisterForNetworkLocked(Handler handler) {
        synchronized (this.mLock) {
            this.mNetworkLockedRegistrants.remove(handler);
        }
    }

    @Override // com.android.internal.telephony.IccCard
    public void supplyPin(String str, Message message) {
        synchronized (this.mLock) {
            UiccCardApplication uiccCardApplication = this.mUiccApplication;
            if (uiccCardApplication != null) {
                uiccCardApplication.supplyPin(str, message);
            } else if (message != null) {
                AsyncResult.forMessage(message).exception = new RuntimeException("ICC card is absent.");
                message.sendToTarget();
            }
        }
    }

    @Override // com.android.internal.telephony.IccCard
    public void supplyPuk(String str, String str2, Message message) {
        synchronized (this.mLock) {
            UiccCardApplication uiccCardApplication = this.mUiccApplication;
            if (uiccCardApplication != null) {
                uiccCardApplication.supplyPuk(str, str2, message);
            } else if (message != null) {
                AsyncResult.forMessage(message).exception = new RuntimeException("ICC card is absent.");
                message.sendToTarget();
            }
        }
    }

    @Override // com.android.internal.telephony.IccCard
    public void supplyPin2(String str, Message message) {
        synchronized (this.mLock) {
            UiccCardApplication uiccCardApplication = this.mUiccApplication;
            if (uiccCardApplication != null) {
                uiccCardApplication.supplyPin2(str, message);
            } else if (message != null) {
                AsyncResult.forMessage(message).exception = new RuntimeException("ICC card is absent.");
                message.sendToTarget();
            }
        }
    }

    @Override // com.android.internal.telephony.IccCard
    public void supplyPuk2(String str, String str2, Message message) {
        synchronized (this.mLock) {
            UiccCardApplication uiccCardApplication = this.mUiccApplication;
            if (uiccCardApplication != null) {
                uiccCardApplication.supplyPuk2(str, str2, message);
            } else if (message != null) {
                AsyncResult.forMessage(message).exception = new RuntimeException("ICC card is absent.");
                message.sendToTarget();
            }
        }
    }

    @Override // com.android.internal.telephony.IccCard
    public void supplyNetworkDepersonalization(String str, Message message) {
        synchronized (this.mLock) {
            UiccCardApplication uiccCardApplication = this.mUiccApplication;
            if (uiccCardApplication != null) {
                uiccCardApplication.supplyNetworkDepersonalization(str, message);
            } else if (message != null) {
                AsyncResult.forMessage(message).exception = new RuntimeException("CommandsInterface is not set.");
                message.sendToTarget();
            }
        }
    }

    @Override // com.android.internal.telephony.IccCard
    public void supplySimDepersonalization(IccCardApplicationStatus.PersoSubState persoSubState, String str, Message message) {
        synchronized (this.mLock) {
            UiccCardApplication uiccCardApplication = this.mUiccApplication;
            if (uiccCardApplication != null) {
                uiccCardApplication.supplySimDepersonalization(persoSubState, str, message);
            } else if (message != null) {
                AsyncResult.forMessage(message).exception = new RuntimeException("CommandsInterface is not set.");
                message.sendToTarget();
            }
        }
    }

    @Override // com.android.internal.telephony.IccCard
    public boolean getIccLockEnabled() {
        boolean z;
        synchronized (this.mLock) {
            UiccCardApplication uiccCardApplication = this.mUiccApplication;
            z = uiccCardApplication != null && uiccCardApplication.getIccLockEnabled();
        }
        return z;
    }

    @Override // com.android.internal.telephony.IccCard
    public boolean getIccFdnEnabled() {
        boolean z;
        synchronized (this.mLock) {
            UiccCardApplication uiccCardApplication = this.mUiccApplication;
            z = uiccCardApplication != null && uiccCardApplication.getIccFdnEnabled();
        }
        return z;
    }

    @Override // com.android.internal.telephony.IccCard
    public boolean getIccFdnAvailable() {
        boolean z;
        synchronized (this.mLock) {
            UiccCardApplication uiccCardApplication = this.mUiccApplication;
            z = uiccCardApplication != null && uiccCardApplication.getIccFdnAvailable();
        }
        return z;
    }

    @Override // com.android.internal.telephony.IccCard
    public boolean getIccPin2Blocked() {
        UiccCardApplication uiccCardApplication = this.mUiccApplication;
        return uiccCardApplication != null && uiccCardApplication.getIccPin2Blocked();
    }

    @Override // com.android.internal.telephony.IccCard
    public boolean getIccPuk2Blocked() {
        UiccCardApplication uiccCardApplication = this.mUiccApplication;
        return uiccCardApplication != null && uiccCardApplication.getIccPuk2Blocked();
    }

    @Override // com.android.internal.telephony.IccCard
    public boolean isEmptyProfile() {
        return this.mLastReportedNumOfUiccApplications == 0;
    }

    @Override // com.android.internal.telephony.IccCard
    public void setIccLockEnabled(boolean z, String str, Message message) {
        synchronized (this.mLock) {
            UiccCardApplication uiccCardApplication = this.mUiccApplication;
            if (uiccCardApplication != null) {
                uiccCardApplication.setIccLockEnabled(z, str, message);
            } else if (message != null) {
                AsyncResult.forMessage(message).exception = new RuntimeException("ICC card is absent.");
                message.sendToTarget();
            }
        }
    }

    @Override // com.android.internal.telephony.IccCard
    public void setIccFdnEnabled(boolean z, String str, Message message) {
        synchronized (this.mLock) {
            UiccCardApplication uiccCardApplication = this.mUiccApplication;
            if (uiccCardApplication != null) {
                uiccCardApplication.setIccFdnEnabled(z, str, message);
            } else if (message != null) {
                AsyncResult.forMessage(message).exception = new RuntimeException("ICC card is absent.");
                message.sendToTarget();
            }
        }
    }

    @Override // com.android.internal.telephony.IccCard
    public void changeIccLockPassword(String str, String str2, Message message) {
        synchronized (this.mLock) {
            UiccCardApplication uiccCardApplication = this.mUiccApplication;
            if (uiccCardApplication != null) {
                uiccCardApplication.changeIccLockPassword(str, str2, message);
            } else if (message != null) {
                AsyncResult.forMessage(message).exception = new RuntimeException("ICC card is absent.");
                message.sendToTarget();
            }
        }
    }

    @Override // com.android.internal.telephony.IccCard
    public void changeIccFdnPassword(String str, String str2, Message message) {
        synchronized (this.mLock) {
            UiccCardApplication uiccCardApplication = this.mUiccApplication;
            if (uiccCardApplication != null) {
                uiccCardApplication.changeIccFdnPassword(str, str2, message);
            } else if (message != null) {
                AsyncResult.forMessage(message).exception = new RuntimeException("ICC card is absent.");
                message.sendToTarget();
            }
        }
    }

    @Override // com.android.internal.telephony.IccCard
    public String getServiceProviderName() {
        synchronized (this.mLock) {
            IccRecords iccRecords = this.mIccRecords;
            if (iccRecords != null) {
                return iccRecords.getServiceProviderName();
            }
            return null;
        }
    }

    @Override // com.android.internal.telephony.IccCard
    public boolean hasIccCard() {
        if (this.mUiccCard.getCardState() != IccCardStatus.CardState.CARDSTATE_ABSENT) {
            return true;
        }
        loge("hasIccCard: UiccProfile is not null but UiccCard is null or card state is ABSENT");
        return false;
    }

    public void update(Context context, CommandsInterface commandsInterface, IccCardStatus iccCardStatus) {
        synchronized (this.mLock) {
            this.mUniversalPinState = iccCardStatus.mUniversalPinState;
            this.mGsmUmtsSubscriptionAppIndex = iccCardStatus.mGsmUmtsSubscriptionAppIndex;
            this.mCdmaSubscriptionAppIndex = iccCardStatus.mCdmaSubscriptionAppIndex;
            this.mImsSubscriptionAppIndex = iccCardStatus.mImsSubscriptionAppIndex;
            this.mContext = context;
            this.mCi = commandsInterface;
            this.mTelephonyManager = (TelephonyManager) context.getSystemService("phone");
            log(iccCardStatus.mApplications.length + " applications");
            this.mLastReportedNumOfUiccApplications = iccCardStatus.mApplications.length;
            int i = 0;
            while (true) {
                UiccCardApplication[] uiccCardApplicationArr = this.mUiccApplications;
                if (i >= uiccCardApplicationArr.length) {
                    break;
                }
                UiccCardApplication uiccCardApplication = uiccCardApplicationArr[i];
                if (uiccCardApplication == null) {
                    IccCardApplicationStatus[] iccCardApplicationStatusArr = iccCardStatus.mApplications;
                    if (i < iccCardApplicationStatusArr.length) {
                        uiccCardApplicationArr[i] = new UiccCardApplication(this, iccCardApplicationStatusArr[i], this.mContext, this.mCi);
                    }
                } else {
                    IccCardApplicationStatus[] iccCardApplicationStatusArr2 = iccCardStatus.mApplications;
                    if (i >= iccCardApplicationStatusArr2.length) {
                        uiccCardApplication.dispose();
                        this.mUiccApplications[i] = null;
                    } else {
                        uiccCardApplication.update(iccCardApplicationStatusArr2[i], this.mContext, this.mCi);
                    }
                }
                i++;
            }
            createAndUpdateCatServiceLocked();
            log("Before privilege rules: " + this.mCarrierPrivilegeRules + " : " + iccCardStatus.mCardState);
            UiccCarrierPrivilegeRules uiccCarrierPrivilegeRules = this.mCarrierPrivilegeRules;
            if (uiccCarrierPrivilegeRules == null && iccCardStatus.mCardState == IccCardStatus.CardState.CARDSTATE_PRESENT) {
                this.mCarrierPrivilegeRules = new UiccCarrierPrivilegeRules(this, this.mHandler.obtainMessage(13));
            } else if (uiccCarrierPrivilegeRules != null && iccCardStatus.mCardState != IccCardStatus.CardState.CARDSTATE_PRESENT) {
                this.mCarrierPrivilegeRules = null;
                this.mContext.getContentResolver().unregisterContentObserver(this.mProvisionCompleteContentObserver);
            }
            sanitizeApplicationIndexesLocked();
            int i2 = this.mRadioTech;
            if (i2 != 0) {
                setCurrentAppType(ServiceState.isGsm(i2));
            }
            updateIccAvailability(true);
        }
    }

    private void createAndUpdateCatServiceLocked() {
        UiccCardApplication[] uiccCardApplicationArr = this.mUiccApplications;
        if (uiccCardApplicationArr.length > 0 && uiccCardApplicationArr[0] != null) {
            CatService catService = this.mCatService;
            if (catService == null) {
                this.mCatService = CatService.getInstance(this.mCi, this.mContext, this, this.mPhoneId);
                return;
            } else {
                catService.update(this.mCi, this.mContext, this);
                return;
            }
        }
        CatService catService2 = this.mCatService;
        if (catService2 != null) {
            catService2.dispose();
        }
        this.mCatService = null;
    }

    protected void finalize() {
        log("UiccProfile finalized");
    }

    private void sanitizeApplicationIndexesLocked() {
        this.mGsmUmtsSubscriptionAppIndex = checkIndexLocked(this.mGsmUmtsSubscriptionAppIndex, IccCardApplicationStatus.AppType.APPTYPE_SIM, IccCardApplicationStatus.AppType.APPTYPE_USIM);
        this.mCdmaSubscriptionAppIndex = checkIndexLocked(this.mCdmaSubscriptionAppIndex, IccCardApplicationStatus.AppType.APPTYPE_RUIM, IccCardApplicationStatus.AppType.APPTYPE_CSIM);
        this.mImsSubscriptionAppIndex = checkIndexLocked(this.mImsSubscriptionAppIndex, IccCardApplicationStatus.AppType.APPTYPE_ISIM, null);
    }

    private boolean isSupportedApplication(UiccCardApplication uiccCardApplication) {
        if (uiccCardApplication.getType() == IccCardApplicationStatus.AppType.APPTYPE_USIM || uiccCardApplication.getType() == IccCardApplicationStatus.AppType.APPTYPE_SIM) {
            return true;
        }
        if (UiccController.isCdmaSupported(this.mContext)) {
            return uiccCardApplication.getType() == IccCardApplicationStatus.AppType.APPTYPE_CSIM || uiccCardApplication.getType() == IccCardApplicationStatus.AppType.APPTYPE_RUIM;
        }
        return false;
    }

    private void checkAndUpdateIfAnyAppToBeIgnored() {
        UiccCardApplication[] uiccCardApplicationArr;
        UiccCardApplication[] uiccCardApplicationArr2;
        boolean[] zArr = new boolean[IccCardApplicationStatus.AppType.APPTYPE_ISIM.ordinal() + 1];
        for (UiccCardApplication uiccCardApplication : this.mUiccApplications) {
            if (uiccCardApplication != null && isSupportedApplication(uiccCardApplication) && uiccCardApplication.isReady()) {
                zArr[uiccCardApplication.getType().ordinal()] = true;
            }
        }
        for (UiccCardApplication uiccCardApplication2 : this.mUiccApplications) {
            if (uiccCardApplication2 != null && isSupportedApplication(uiccCardApplication2) && !uiccCardApplication2.isReady() && zArr[uiccCardApplication2.getType().ordinal()]) {
                uiccCardApplication2.setAppIgnoreState(true);
            }
        }
    }

    private boolean areAllApplicationsReady() {
        UiccCardApplication[] uiccCardApplicationArr;
        for (UiccCardApplication uiccCardApplication : this.mUiccApplications) {
            if (uiccCardApplication != null && isSupportedApplication(uiccCardApplication) && !uiccCardApplication.isReady() && !uiccCardApplication.isAppIgnored()) {
                return false;
            }
        }
        return this.mUiccApplication != null;
    }

    private boolean areAllRecordsLoaded() {
        UiccCardApplication[] uiccCardApplicationArr;
        IccRecords iccRecords;
        for (UiccCardApplication uiccCardApplication : this.mUiccApplications) {
            if (uiccCardApplication != null && isSupportedApplication(uiccCardApplication) && !uiccCardApplication.isAppIgnored() && ((iccRecords = uiccCardApplication.getIccRecords()) == null || !iccRecords.isLoaded())) {
                return false;
            }
        }
        return this.mUiccApplication != null;
    }

    private int checkIndexLocked(int i, IccCardApplicationStatus.AppType appType, IccCardApplicationStatus.AppType appType2) {
        UiccCardApplication[] uiccCardApplicationArr = this.mUiccApplications;
        if (uiccCardApplicationArr == null || i >= uiccCardApplicationArr.length) {
            loge("App index " + i + " is invalid since there are no applications");
            return -1;
        } else if (i < 0) {
            return -1;
        } else {
            if (uiccCardApplicationArr[i].getType() == appType || this.mUiccApplications[i].getType() == appType2) {
                return i;
            }
            loge("App index " + i + " is invalid since it's not " + appType + " and not " + appType2);
            return -1;
        }
    }

    public void registerForOpertorBrandOverride(Handler handler, int i, Object obj) {
        synchronized (this.mLock) {
            this.mOperatorBrandOverrideRegistrants.add(new Registrant(handler, i, obj));
        }
    }

    public void unregisterForOperatorBrandOverride(Handler handler) {
        synchronized (this.mLock) {
            this.mOperatorBrandOverrideRegistrants.remove(handler);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isPackageBundled(Context context, String str) {
        try {
            context.getPackageManager().getApplicationInfo(str, NetworkStackConstants.NEIGHBOR_ADVERTISEMENT_FLAG_OVERRIDE);
            log(str + " is installed.");
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            log(str + " is not installed.");
            return false;
        }
    }

    private void promptInstallCarrierApp(String str) {
        Intent intent = InstallCarrierAppTrampolineActivity.get(this.mContext, str);
        intent.addFlags(268435456);
        this.mContext.startActivity(intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onCarrierPrivilegesLoadedMessage() {
        ActivityManager activityManager = (ActivityManager) this.mContext.getSystemService(ActivityManager.class);
        CarrierAppUtils.disableCarrierAppsUntilPrivileged(this.mContext.getOpPackageName(), this.mTelephonyManager, ActivityManager.getCurrentUser(), this.mContext);
        UsageStatsManager usageStatsManager = (UsageStatsManager) this.mContext.getSystemService("usagestats");
        if (usageStatsManager != null) {
            usageStatsManager.onCarrierPrivilegedAppsChanged();
        }
        InstallCarrierAppUtils.hideAllNotifications(this.mContext);
        InstallCarrierAppUtils.unregisterPackageInstallReceiver(this.mContext);
        synchronized (this.mLock) {
            boolean isProvisioned = isProvisioned();
            boolean isUserUnlocked = isUserUnlocked();
            if (isProvisioned && isUserUnlocked) {
                for (String str : getUninstalledCarrierPackages()) {
                    promptInstallCarrierApp(str);
                }
            } else {
                if (!isProvisioned) {
                    this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("device_provisioned"), false, this.mProvisionCompleteContentObserver);
                    this.mProvisionCompleteContentObserverRegistered = true;
                }
                if (!isUserUnlocked) {
                    this.mContext.registerReceiver(this.mUserUnlockReceiver, new IntentFilter("android.intent.action.USER_UNLOCKED"));
                    this.mUserUnlockReceiverRegistered = true;
                }
            }
        }
    }

    private boolean isProvisioned() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "device_provisioned", 1) == 1;
    }

    private boolean isUserUnlocked() {
        return ((UserManager) this.mContext.getSystemService(UserManager.class)).isUserUnlocked();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showCarrierAppNotificationsIfPossible() {
        if (isProvisioned() && isUserUnlocked()) {
            for (String str : getUninstalledCarrierPackages()) {
                InstallCarrierAppUtils.showNotification(this.mContext, str);
                InstallCarrierAppUtils.registerPackageInstallReceiver(this.mContext);
            }
        }
    }

    private Set<String> getUninstalledCarrierPackages() {
        String string = Settings.Global.getString(this.mContext.getContentResolver(), "carrier_app_whitelist");
        if (TextUtils.isEmpty(string)) {
            return Collections.emptySet();
        }
        Map<String, String> parseToCertificateToPackageMap = parseToCertificateToPackageMap(string);
        if (parseToCertificateToPackageMap.isEmpty()) {
            return Collections.emptySet();
        }
        UiccCarrierPrivilegeRules carrierPrivilegeRules = getCarrierPrivilegeRules();
        if (carrierPrivilegeRules == null) {
            return Collections.emptySet();
        }
        ArraySet arraySet = new ArraySet();
        for (UiccAccessRule uiccAccessRule : carrierPrivilegeRules.getAccessRules()) {
            String str = parseToCertificateToPackageMap.get(uiccAccessRule.getCertificateHexString().toUpperCase(Locale.ROOT));
            if (!TextUtils.isEmpty(str) && !isPackageBundled(this.mContext, str)) {
                arraySet.add(str);
            }
        }
        return arraySet;
    }

    @VisibleForTesting
    public static Map<String, String> parseToCertificateToPackageMap(String str) {
        List<String> asList = Arrays.asList(str.split("\\s*;\\s*"));
        if (asList.isEmpty()) {
            return Collections.emptyMap();
        }
        ArrayMap arrayMap = new ArrayMap(asList.size());
        for (String str2 : asList) {
            String[] split = str2.split("\\s*:\\s*");
            if (split.length == 2) {
                arrayMap.put(split[0].toUpperCase(Locale.ROOT), split[1]);
            } else {
                loge("Incorrect length of key-value pair in carrier app allow list map.  Length should be exactly 2");
            }
        }
        return arrayMap;
    }

    @Override // com.android.internal.telephony.IccCard
    public boolean isApplicationOnIcc(IccCardApplicationStatus.AppType appType) {
        synchronized (this.mLock) {
            int i = 0;
            while (true) {
                UiccCardApplication[] uiccCardApplicationArr = this.mUiccApplications;
                if (i >= uiccCardApplicationArr.length) {
                    return false;
                }
                UiccCardApplication uiccCardApplication = uiccCardApplicationArr[i];
                if (uiccCardApplication != null && uiccCardApplication.getType() == appType) {
                    return true;
                }
                i++;
            }
        }
    }

    public IccCardStatus.PinState getUniversalPinState() {
        IccCardStatus.PinState pinState;
        synchronized (this.mLock) {
            pinState = this.mUniversalPinState;
        }
        return pinState;
    }

    public UiccCardApplication getApplication(int i) {
        int i2;
        synchronized (this.mLock) {
            if (i == 1) {
                i2 = this.mGsmUmtsSubscriptionAppIndex;
            } else if (i == 2) {
                i2 = this.mCdmaSubscriptionAppIndex;
            } else {
                i2 = i != 3 ? 8 : this.mImsSubscriptionAppIndex;
            }
            if (i2 >= 0) {
                UiccCardApplication[] uiccCardApplicationArr = this.mUiccApplications;
                if (i2 < uiccCardApplicationArr.length) {
                    return uiccCardApplicationArr[i2];
                }
            }
            return null;
        }
    }

    public UiccCardApplication getApplicationIndex(int i) {
        synchronized (this.mLock) {
            if (i >= 0) {
                UiccCardApplication[] uiccCardApplicationArr = this.mUiccApplications;
                if (i < uiccCardApplicationArr.length) {
                    return uiccCardApplicationArr[i];
                }
            }
            return null;
        }
    }

    public UiccCardApplication getApplicationByType(int i) {
        synchronized (this.mLock) {
            int i2 = 0;
            while (true) {
                UiccCardApplication[] uiccCardApplicationArr = this.mUiccApplications;
                if (i2 >= uiccCardApplicationArr.length) {
                    return null;
                }
                UiccCardApplication uiccCardApplication = uiccCardApplicationArr[i2];
                if (uiccCardApplication != null && uiccCardApplication.getType().ordinal() == i) {
                    return this.mUiccApplications[i2];
                }
                i2++;
            }
        }
    }

    @VisibleForTesting
    public boolean resetAppWithAid(String str, boolean z) {
        boolean z2;
        synchronized (this.mLock) {
            z2 = false;
            int i = 0;
            boolean z3 = false;
            boolean z4 = false;
            while (true) {
                UiccCardApplication[] uiccCardApplicationArr = this.mUiccApplications;
                if (i >= uiccCardApplicationArr.length) {
                    break;
                }
                if (uiccCardApplicationArr[i] != null && (TextUtils.isEmpty(str) || str.equals(this.mUiccApplications[i].getAid()))) {
                    if (!TextUtils.isEmpty(str) && this.mUiccApplications[i].getType() == IccCardApplicationStatus.AppType.APPTYPE_ISIM) {
                        z4 = true;
                    }
                    this.mUiccApplications[i].dispose();
                    this.mUiccApplications[i] = null;
                    z3 = true;
                }
                i++;
            }
            if (z && TextUtils.isEmpty(str)) {
                if (this.mCarrierPrivilegeRules != null) {
                    this.mCarrierPrivilegeRules = null;
                    this.mContext.getContentResolver().unregisterContentObserver(this.mProvisionCompleteContentObserver);
                    z3 = true;
                }
                CatService catService = this.mCatService;
                if (catService != null) {
                    catService.dispose();
                    this.mCatService = null;
                    z3 = true;
                }
            }
            if (z3 && !z4) {
                z2 = true;
            }
        }
        return z2;
    }

    public void iccOpenLogicalChannel(String str, int i, Message message) {
        logWithLocalLog("iccOpenLogicalChannel: " + str + " , " + i + " by pid:" + Binder.getCallingPid() + " uid:" + Binder.getCallingUid());
        this.mCi.iccOpenLogicalChannel(str, i, this.mHandler.obtainMessage(8, message));
    }

    public void iccCloseLogicalChannel(int i, boolean z, Message message) {
        logWithLocalLog("iccCloseLogicalChannel: " + i);
        this.mCi.iccCloseLogicalChannel(i, z, this.mHandler.obtainMessage(9, message));
    }

    public void iccTransmitApduLogicalChannel(int i, int i2, int i3, int i4, int i5, int i6, String str, boolean z, Message message) {
        this.mCi.iccTransmitApduLogicalChannel(i, i2, i3, i4, i5, i6, str, z, this.mHandler.obtainMessage(10, message));
    }

    public void iccTransmitApduBasicChannel(int i, int i2, int i3, int i4, int i5, String str, Message message) {
        this.mCi.iccTransmitApduBasicChannel(i, i2, i3, i4, i5, str, this.mHandler.obtainMessage(11, message));
    }

    public void iccExchangeSimIO(int i, int i2, int i3, int i4, int i5, String str, Message message) {
        this.mCi.iccIO(i2, i, str, i3, i4, i5, null, null, this.mHandler.obtainMessage(12, message));
    }

    public void sendEnvelopeWithStatus(String str, Message message) {
        this.mCi.sendEnvelopeWithStatus(str, message);
    }

    public int getNumApplications() {
        return this.mLastReportedNumOfUiccApplications;
    }

    public int getPhoneId() {
        return this.mPhoneId;
    }

    @VisibleForTesting
    public boolean areCarrierPrivilegeRulesLoaded() {
        UiccCarrierPrivilegeRules carrierPrivilegeRules = getCarrierPrivilegeRules();
        return carrierPrivilegeRules == null || carrierPrivilegeRules.areCarrierPriviligeRulesLoaded();
    }

    public List<String> getCertsFromCarrierPrivilegeAccessRules() {
        ArrayList arrayList = new ArrayList();
        UiccCarrierPrivilegeRules carrierPrivilegeRules = getCarrierPrivilegeRules();
        if (carrierPrivilegeRules != null) {
            for (UiccAccessRule uiccAccessRule : carrierPrivilegeRules.getAccessRules()) {
                arrayList.add(uiccAccessRule.getCertificateHexString());
            }
        }
        if (arrayList.isEmpty()) {
            return null;
        }
        return arrayList;
    }

    public List<UiccAccessRule> getCarrierPrivilegeAccessRules() {
        UiccCarrierPrivilegeRules carrierPrivilegeRules = getCarrierPrivilegeRules();
        if (carrierPrivilegeRules == null) {
            return Collections.EMPTY_LIST;
        }
        return new ArrayList(carrierPrivilegeRules.getAccessRules());
    }

    private UiccCarrierPrivilegeRules getCarrierPrivilegeRules() {
        synchronized (this.mLock) {
            UiccCarrierPrivilegeRules uiccCarrierPrivilegeRules = this.mTestOverrideCarrierPrivilegeRules;
            if (uiccCarrierPrivilegeRules != null) {
                return uiccCarrierPrivilegeRules;
            }
            return this.mCarrierPrivilegeRules;
        }
    }

    public boolean setOperatorBrandOverride(String str) {
        log("setOperatorBrandOverride: " + str);
        log("current iccId: " + SubscriptionInfo.givePrintableIccid(getIccId()));
        final String iccId = getIccId();
        if (TextUtils.isEmpty(iccId)) {
            return false;
        }
        if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
            int subscriptionId = SubscriptionManager.getSubscriptionId(getPhoneId());
            SubscriptionInfoInternal subscriptionInfoInternal = SubscriptionManagerService.getInstance().getSubscriptionInfoInternal(subscriptionId);
            if (subscriptionInfoInternal == null) {
                loge("setOperatorBrandOverride: Cannot find subscription info for sub " + subscriptionId);
                return false;
            }
            ArrayList arrayList = new ArrayList();
            arrayList.add(subscriptionInfoInternal.toSubscriptionInfo());
            String groupUuid = subscriptionInfoInternal.getGroupUuid();
            if (!TextUtils.isEmpty(groupUuid)) {
                arrayList.addAll(SubscriptionManagerService.getInstance().getSubscriptionsInGroup(ParcelUuid.fromString(groupUuid), this.mContext.getOpPackageName(), this.mContext.getFeatureId()));
            }
            if (arrayList.stream().noneMatch(new Predicate() { // from class: com.android.internal.telephony.uicc.UiccProfile$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$setOperatorBrandOverride$0;
                    lambda$setOperatorBrandOverride$0 = UiccProfile.lambda$setOperatorBrandOverride$0(iccId, (SubscriptionInfo) obj);
                    return lambda$setOperatorBrandOverride$0;
                }
            })) {
                loge("iccId doesn't match current active subId.");
                return false;
            }
        } else if (!SubscriptionController.getInstance().checkPhoneIdAndIccIdMatch(getPhoneId(), iccId)) {
            loge("iccId doesn't match current active subId.");
            return false;
        }
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this.mContext).edit();
        String str2 = "operator_branding_" + iccId;
        if (str == null) {
            edit.remove(str2).commit();
        } else {
            edit.putString(str2, str).commit();
        }
        this.mOperatorBrandOverrideRegistrants.notifyRegistrants();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$setOperatorBrandOverride$0(String str, SubscriptionInfo subscriptionInfo) {
        return TextUtils.equals(IccUtils.stripTrailingFs(subscriptionInfo.getIccId()), IccUtils.stripTrailingFs(str));
    }

    public String getOperatorBrandOverride() {
        String iccId = getIccId();
        if (TextUtils.isEmpty(iccId)) {
            return null;
        }
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        return defaultSharedPreferences.getString("operator_branding_" + iccId, null);
    }

    public String getIccId() {
        UiccCardApplication[] uiccCardApplicationArr;
        IccRecords iccRecords;
        for (UiccCardApplication uiccCardApplication : this.mUiccApplications) {
            if (uiccCardApplication != null && (iccRecords = uiccCardApplication.getIccRecords()) != null && iccRecords.getIccId() != null) {
                return iccRecords.getIccId();
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String eventToString(int i) {
        switch (i) {
            case 1:
                return "RADIO_OFF_OR_UNAVAILABLE";
            case 2:
                return "ICC_LOCKED";
            case 3:
                return "APP_READY";
            case 4:
                return "RECORDS_LOADED";
            case 5:
                return "NETWORK_LOCKED";
            case 6:
                return "EID_READY";
            case 7:
                return "ICC_RECORD_EVENTS";
            case 8:
                return "OPEN_LOGICAL_CHANNEL_DONE";
            case 9:
                return "CLOSE_LOGICAL_CHANNEL_DONE";
            case 10:
                return "TRANSMIT_APDU_LOGICAL_CHANNEL_DONE";
            case 11:
                return "TRANSMIT_APDU_BASIC_CHANNEL_DONE";
            case 12:
                return "SIM_IO_DONE";
            case 13:
                return "CARRIER_PRIVILEGES_LOADED";
            case 14:
                return "CARRIER_CONFIG_CHANGED";
            case 15:
                return "CARRIER_PRIVILEGES_TEST_OVERRIDE_SET";
            case 16:
                return "SUPPLY_ICC_PIN_DONE";
            default:
                return "UNKNOWN(" + i + ")";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void log(String str) {
        Rlog.d(LOG_TAG, str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void loge(String str) {
        Rlog.e(LOG_TAG, str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logWithLocalLog(String str) {
        Rlog.d(LOG_TAG, str);
        UiccController.addLocalLog("UiccProfile[" + this.mPhoneId + "]: " + str);
    }

    @VisibleForTesting
    public void refresh() {
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(13));
    }

    public void setTestOverrideCarrierPrivilegeRules(List<UiccAccessRule> list) {
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(15, list));
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        UiccCardApplication[] uiccCardApplicationArr;
        UiccCardApplication[] uiccCardApplicationArr2;
        IccRecords iccRecords;
        AndroidUtilIndentingPrintWriter androidUtilIndentingPrintWriter = new AndroidUtilIndentingPrintWriter(printWriter, "  ");
        androidUtilIndentingPrintWriter.increaseIndent();
        androidUtilIndentingPrintWriter.println("mCatService=" + this.mCatService);
        for (int i = 0; i < this.mOperatorBrandOverrideRegistrants.size(); i++) {
            androidUtilIndentingPrintWriter.println("mOperatorBrandOverrideRegistrants[" + i + "]=" + ((Registrant) this.mOperatorBrandOverrideRegistrants.get(i)).getHandler());
        }
        androidUtilIndentingPrintWriter.println("mUniversalPinState=" + this.mUniversalPinState);
        androidUtilIndentingPrintWriter.println("mGsmUmtsSubscriptionAppIndex=" + this.mGsmUmtsSubscriptionAppIndex);
        androidUtilIndentingPrintWriter.println("mCdmaSubscriptionAppIndex=" + this.mCdmaSubscriptionAppIndex);
        androidUtilIndentingPrintWriter.println("mImsSubscriptionAppIndex=" + this.mImsSubscriptionAppIndex);
        androidUtilIndentingPrintWriter.println("mUiccApplications: length=" + this.mUiccApplications.length);
        androidUtilIndentingPrintWriter.increaseIndent();
        int i2 = 0;
        while (true) {
            UiccCardApplication[] uiccCardApplicationArr3 = this.mUiccApplications;
            if (i2 >= uiccCardApplicationArr3.length) {
                break;
            }
            if (uiccCardApplicationArr3[i2] == null) {
                androidUtilIndentingPrintWriter.println("mUiccApplications[" + i2 + "]=" + ((Object) null));
            } else {
                androidUtilIndentingPrintWriter.println("mUiccApplications[" + i2 + "]=" + this.mUiccApplications[i2].getType() + " " + this.mUiccApplications[i2]);
            }
            i2++;
        }
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.println();
        for (UiccCardApplication uiccCardApplication : this.mUiccApplications) {
            if (uiccCardApplication != null) {
                uiccCardApplication.dump(fileDescriptor, androidUtilIndentingPrintWriter, strArr);
                androidUtilIndentingPrintWriter.println();
            }
        }
        for (UiccCardApplication uiccCardApplication2 : this.mUiccApplications) {
            if (uiccCardApplication2 != null && (iccRecords = uiccCardApplication2.getIccRecords()) != null) {
                iccRecords.dump(fileDescriptor, androidUtilIndentingPrintWriter, strArr);
                androidUtilIndentingPrintWriter.println();
            }
        }
        if (this.mCarrierPrivilegeRules == null) {
            androidUtilIndentingPrintWriter.println("mCarrierPrivilegeRules: null");
        } else {
            androidUtilIndentingPrintWriter.println("mCarrierPrivilegeRules: ");
            androidUtilIndentingPrintWriter.increaseIndent();
            this.mCarrierPrivilegeRules.dump(fileDescriptor, androidUtilIndentingPrintWriter, strArr);
            androidUtilIndentingPrintWriter.decreaseIndent();
        }
        if (this.mTestOverrideCarrierPrivilegeRules != null) {
            androidUtilIndentingPrintWriter.println("mTestOverrideCarrierPrivilegeRules: " + this.mTestOverrideCarrierPrivilegeRules);
            this.mTestOverrideCarrierPrivilegeRules.dump(fileDescriptor, androidUtilIndentingPrintWriter, strArr);
        }
        androidUtilIndentingPrintWriter.flush();
        androidUtilIndentingPrintWriter.println("mNetworkLockedRegistrants: size=" + this.mNetworkLockedRegistrants.size());
        for (int i3 = 0; i3 < this.mNetworkLockedRegistrants.size(); i3++) {
            androidUtilIndentingPrintWriter.println("  mNetworkLockedRegistrants[" + i3 + "]=" + ((Registrant) this.mNetworkLockedRegistrants.get(i3)).getHandler());
        }
        androidUtilIndentingPrintWriter.println("mCurrentAppType=" + this.mCurrentAppType);
        androidUtilIndentingPrintWriter.println("mUiccCard=" + this.mUiccCard);
        androidUtilIndentingPrintWriter.println("mUiccApplication=" + this.mUiccApplication);
        androidUtilIndentingPrintWriter.println("mIccRecords=" + this.mIccRecords);
        androidUtilIndentingPrintWriter.println("mExternalState=" + this.mExternalState);
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.flush();
    }
}
