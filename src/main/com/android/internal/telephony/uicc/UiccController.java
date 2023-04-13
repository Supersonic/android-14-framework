package com.android.internal.telephony.uicc;

import android.app.BroadcastOptions;
import android.compat.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.internal.telephony.sysprop.TelephonyProperties;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.telephony.AnomalyReporter;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.UiccCardInfo;
import android.telephony.UiccPortInfo;
import android.telephony.UiccSlotMapping;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.AndroidUtilIndentingPrintWriter;
import com.android.internal.telephony.CallFailCause;
import com.android.internal.telephony.CarrierServiceBindHelper;
import com.android.internal.telephony.CommandException;
import com.android.internal.telephony.CommandsInterface;
import com.android.internal.telephony.IccCard;
import com.android.internal.telephony.IccCardConstants;
import com.android.internal.telephony.IntentBroadcaster;
import com.android.internal.telephony.LocalLog;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.RadioConfig;
import com.android.internal.telephony.RegistrantList;
import com.android.internal.telephony.SubscriptionInfoUpdater;
import com.android.internal.telephony.metrics.TelephonyMetrics;
import com.android.internal.telephony.subscription.SubscriptionManagerService;
import com.android.internal.telephony.uicc.IccSlotStatus;
import com.android.internal.telephony.uicc.euicc.EuiccCard;
import com.android.internal.telephony.util.ArrayUtils;
import com.android.internal.telephony.util.TelephonyUtils;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
/* loaded from: classes.dex */
public class UiccController extends Handler {
    public static final int APP_FAM_3GPP = 1;
    public static final int APP_FAM_3GPP2 = 2;
    public static final int APP_FAM_IMS = 3;
    public static final int INVALID_SLOT_ID = -1;
    @UnsupportedAppUsage
    private static UiccController mInstance;
    @UnsupportedAppUsage
    private static final Object mLock = new Object();
    @VisibleForTesting
    public static ArrayList<IccSlotStatus> sLastSlotStatus;
    private static LocalLog sLocalLog;
    private ArrayList<String> mCardStrings;
    private final CarrierServiceBindHelper mCarrierServiceBindHelper;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private CommandsInterface[] mCis;
    @UnsupportedAppUsage
    @VisibleForTesting
    public Context mContext;
    private int mDefaultEuiccCardId;
    private final int[] mEuiccSlots;
    private boolean mHasBuiltInEuicc;
    private UiccStateChangedLauncher mLauncher;
    private int[] mPhoneIdToSlotId;
    private final PinStorage mPinStorage;
    private RadioConfig mRadioConfig;
    @TelephonyManager.SimState
    private final int[] mSimApplicationState;
    @TelephonyManager.SimState
    private final int[] mSimCardState;
    private final TelephonyManager mTelephonyManager;
    @VisibleForTesting
    public UiccSlot[] mUiccSlots;
    private boolean mUseRemovableEsimAsDefault;
    private boolean mIsSlotStatusSupported = true;
    private boolean mHasActiveBuiltInEuicc = false;
    protected RegistrantList mIccChangedRegistrants = new RegistrantList();

    static {
        sLocalLog = new LocalLog(TelephonyUtils.IS_DEBUGGABLE ? CallFailCause.RADIO_UPLINK_FAILURE : 64);
    }

    public static UiccController make(Context context) {
        UiccController uiccController;
        synchronized (mLock) {
            if (mInstance != null) {
                throw new RuntimeException("UiccController.make() should only be called once");
            }
            uiccController = new UiccController(context);
            mInstance = uiccController;
        }
        return uiccController;
    }

    private UiccController(Context context) {
        this.mHasBuiltInEuicc = false;
        this.mUseRemovableEsimAsDefault = false;
        log("Creating UiccController");
        this.mContext = context;
        this.mCis = PhoneFactory.getCommandsInterfaces();
        int intValue = TelephonyProperties.sim_slots_count().orElse(Integer.valueOf(context.getResources().getInteger(17694925))).intValue();
        logWithLocalLog("config_num_physical_slots = " + intValue);
        CommandsInterface[] commandsInterfaceArr = this.mCis;
        intValue = intValue < commandsInterfaceArr.length ? commandsInterfaceArr.length : intValue;
        TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService(TelephonyManager.class);
        this.mTelephonyManager = telephonyManager;
        this.mUiccSlots = new UiccSlot[intValue];
        this.mPhoneIdToSlotId = new int[this.mCis.length];
        int supportedModemCount = telephonyManager.getSupportedModemCount();
        this.mSimCardState = new int[supportedModemCount];
        this.mSimApplicationState = new int[supportedModemCount];
        Arrays.fill(this.mPhoneIdToSlotId, -1);
        RadioConfig radioConfig = RadioConfig.getInstance();
        this.mRadioConfig = radioConfig;
        radioConfig.registerForSimSlotStatusChanged(this, 2, null);
        int i = 0;
        while (true) {
            CommandsInterface[] commandsInterfaceArr2 = this.mCis;
            if (i >= commandsInterfaceArr2.length) {
                break;
            }
            commandsInterfaceArr2[i].registerForIccStatusChanged(this, 1, Integer.valueOf(i));
            this.mCis[i].registerForAvailable(this, 6, Integer.valueOf(i));
            this.mCis[i].registerForNotAvailable(this, 7, Integer.valueOf(i));
            this.mCis[i].registerForIccRefresh(this, 8, Integer.valueOf(i));
            i++;
        }
        this.mLauncher = new UiccStateChangedLauncher(context, this);
        this.mCardStrings = loadCardStrings();
        this.mDefaultEuiccCardId = -2;
        this.mCarrierServiceBindHelper = new CarrierServiceBindHelper(this.mContext);
        this.mEuiccSlots = this.mContext.getResources().getIntArray(17236189);
        this.mHasBuiltInEuicc = hasBuiltInEuicc();
        PhoneConfigurationManager.registerForMultiSimConfigChange(this, 10, null);
        this.mPinStorage = new PinStorage(this.mContext);
        if (TelephonyUtils.IS_USER) {
            return;
        }
        this.mUseRemovableEsimAsDefault = PreferenceManager.getDefaultSharedPreferences(this.mContext).getBoolean("removable_esim", false);
    }

    public int getPhoneIdFromSlotPortIndex(int i, int i2) {
        UiccSlot uiccSlot = getUiccSlot(i);
        if (uiccSlot == null) {
            return -1;
        }
        return uiccSlot.getPhoneIdFromPortIndex(i2);
    }

    public int getSlotIdFromPhoneId(int i) {
        try {
            return this.mPhoneIdToSlotId[i];
        } catch (ArrayIndexOutOfBoundsException unused) {
            return -1;
        }
    }

    @UnsupportedAppUsage
    public static UiccController getInstance() {
        UiccController uiccController = mInstance;
        if (uiccController != null) {
            return uiccController;
        }
        throw new RuntimeException("UiccController.getInstance can't be called before make()");
    }

    @UnsupportedAppUsage
    public UiccCard getUiccCard(int i) {
        UiccCard uiccCardForPhone;
        synchronized (mLock) {
            uiccCardForPhone = getUiccCardForPhone(i);
        }
        return uiccCardForPhone;
    }

    public UiccPort getUiccPort(int i) {
        UiccPort uiccPortForPhone;
        synchronized (mLock) {
            uiccPortForPhone = getUiccPortForPhone(i);
        }
        return uiccPortForPhone;
    }

    public UiccPort getUiccPortForSlot(int i, int i2) {
        UiccCard uiccCard;
        synchronized (mLock) {
            UiccSlot uiccSlot = getUiccSlot(i);
            if (uiccSlot == null || (uiccCard = uiccSlot.getUiccCard()) == null) {
                return null;
            }
            return uiccCard.getUiccPort(i2);
        }
    }

    public UiccCard getUiccCardForSlot(int i) {
        synchronized (mLock) {
            UiccSlot uiccSlot = getUiccSlot(i);
            if (uiccSlot != null) {
                return uiccSlot.getUiccCard();
            }
            return null;
        }
    }

    public UiccCard getUiccCardForPhone(int i) {
        UiccSlot uiccSlotForPhone;
        synchronized (mLock) {
            if (!isValidPhoneIndex(i) || (uiccSlotForPhone = getUiccSlotForPhone(i)) == null) {
                return null;
            }
            return uiccSlotForPhone.getUiccCard();
        }
    }

    public UiccPort getUiccPortForPhone(int i) {
        UiccSlot uiccSlotForPhone;
        UiccCard uiccCard;
        synchronized (mLock) {
            if (!isValidPhoneIndex(i) || (uiccSlotForPhone = getUiccSlotForPhone(i)) == null || (uiccCard = uiccSlotForPhone.getUiccCard()) == null) {
                return null;
            }
            return uiccCard.getUiccPortForPhone(i);
        }
    }

    public UiccProfile getUiccProfileForPhone(int i) {
        synchronized (mLock) {
            if (isValidPhoneIndex(i)) {
                UiccPort uiccPortForPhone = getUiccPortForPhone(i);
                return uiccPortForPhone != null ? uiccPortForPhone.getUiccProfile() : null;
            }
            return null;
        }
    }

    public UiccSlot[] getUiccSlots() {
        UiccSlot[] uiccSlotArr;
        synchronized (mLock) {
            uiccSlotArr = this.mUiccSlots;
        }
        return uiccSlotArr;
    }

    public void switchSlots(List<UiccSlotMapping> list, Message message) {
        logWithLocalLog("switchSlots: " + list);
        this.mRadioConfig.setSimSlotsMapping(list, message);
    }

    public UiccSlot getUiccSlot(int i) {
        synchronized (mLock) {
            if (isValidSlotIndex(i)) {
                return this.mUiccSlots[i];
            }
            return null;
        }
    }

    public UiccSlot getUiccSlotForPhone(int i) {
        synchronized (mLock) {
            if (isValidPhoneIndex(i)) {
                int slotIdFromPhoneId = getSlotIdFromPhoneId(i);
                if (isValidSlotIndex(slotIdFromPhoneId)) {
                    return this.mUiccSlots[slotIdFromPhoneId];
                }
            }
            return null;
        }
    }

    public int getUiccSlotForCardId(final String str) {
        UiccCard uiccCard;
        synchronized (mLock) {
            int i = 0;
            int i2 = 0;
            while (true) {
                UiccSlot[] uiccSlotArr = this.mUiccSlots;
                if (i2 < uiccSlotArr.length) {
                    UiccSlot uiccSlot = uiccSlotArr[i2];
                    if (uiccSlot != null && (uiccCard = uiccSlot.getUiccCard()) != null && str.equals(uiccCard.getCardId())) {
                        return i2;
                    }
                    i2++;
                } else {
                    while (true) {
                        UiccSlot[] uiccSlotArr2 = this.mUiccSlots;
                        if (i >= uiccSlotArr2.length) {
                            return -1;
                        }
                        final UiccSlot uiccSlot2 = uiccSlotArr2[i];
                        if (uiccSlot2 != null && IntStream.of(uiccSlot2.getPortList()).anyMatch(new IntPredicate() { // from class: com.android.internal.telephony.uicc.UiccController$$ExternalSyntheticLambda0
                            @Override // java.util.function.IntPredicate
                            public final boolean test(int i3) {
                                boolean lambda$getUiccSlotForCardId$0;
                                lambda$getUiccSlotForCardId$0 = UiccController.lambda$getUiccSlotForCardId$0(str, uiccSlot2, i3);
                                return lambda$getUiccSlotForCardId$0;
                            }
                        })) {
                            return i;
                        }
                        i++;
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getUiccSlotForCardId$0(String str, UiccSlot uiccSlot, int i) {
        return str.equals(uiccSlot.getIccId(i));
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public IccRecords getIccRecords(int i, int i2) {
        synchronized (mLock) {
            UiccCardApplication uiccCardApplication = getUiccCardApplication(i, i2);
            if (uiccCardApplication != null) {
                return uiccCardApplication.getIccRecords();
            }
            return null;
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public IccFileHandler getIccFileHandler(int i, int i2) {
        synchronized (mLock) {
            UiccCardApplication uiccCardApplication = getUiccCardApplication(i, i2);
            if (uiccCardApplication != null) {
                return uiccCardApplication.getIccFileHandler();
            }
            return null;
        }
    }

    @UnsupportedAppUsage
    public void registerForIccChanged(Handler handler, int i, Object obj) {
        synchronized (mLock) {
            this.mIccChangedRegistrants.addUnique(handler, i, obj);
        }
        Message.obtain(handler, i, new AsyncResult(obj, (Object) null, (Throwable) null)).sendToTarget();
    }

    public void unregisterForIccChanged(Handler handler) {
        synchronized (mLock) {
            this.mIccChangedRegistrants.remove(handler);
        }
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        synchronized (mLock) {
            Integer ciIndex = getCiIndex(message);
            String eventToString = eventToString(message.what);
            if (ciIndex.intValue() >= 0 && ciIndex.intValue() < this.mCis.length) {
                logWithLocalLog("handleMessage: Received " + eventToString + " for phoneId " + ciIndex);
                AsyncResult asyncResult = (AsyncResult) message.obj;
                switch (message.what) {
                    case 1:
                        log("Received EVENT_ICC_STATUS_CHANGED, calling getIccCardStatus");
                        this.mCis[ciIndex.intValue()].getIccCardStatus(obtainMessage(3, ciIndex));
                        break;
                    case 2:
                    case 4:
                        log("Received EVENT_SLOT_STATUS_CHANGED or EVENT_GET_SLOT_STATUS_DONE");
                        onGetSlotStatusDone(asyncResult);
                        break;
                    case 3:
                        log("Received EVENT_GET_ICC_STATUS_DONE");
                        onGetIccCardStatusDone(asyncResult, ciIndex);
                        break;
                    case 5:
                    case 6:
                        log("Received EVENT_RADIO_AVAILABLE/EVENT_RADIO_ON, calling getIccCardStatus");
                        this.mCis[ciIndex.intValue()].getIccCardStatus(obtainMessage(3, ciIndex));
                        if (ciIndex.intValue() == 0) {
                            log("Received EVENT_RADIO_AVAILABLE/EVENT_RADIO_ON for phoneId 0, calling getIccSlotsStatus");
                            this.mRadioConfig.getSimSlotsStatus(obtainMessage(4, ciIndex));
                            break;
                        }
                        break;
                    case 7:
                        log("EVENT_RADIO_UNAVAILABLE, dispose card");
                        sLastSlotStatus = null;
                        UiccSlot uiccSlotForPhone = getUiccSlotForPhone(ciIndex.intValue());
                        if (uiccSlotForPhone != null) {
                            uiccSlotForPhone.onRadioStateUnavailable(ciIndex.intValue());
                        }
                        this.mIccChangedRegistrants.notifyRegistrants(new AsyncResult((Object) null, ciIndex, (Throwable) null));
                        break;
                    case 8:
                        log("Received EVENT_SIM_REFRESH");
                        onSimRefresh(asyncResult, ciIndex);
                        break;
                    case 9:
                        log("Received EVENT_EID_READY");
                        onEidReady(asyncResult, ciIndex);
                        break;
                    case 10:
                        log("Received EVENT_MULTI_SIM_CONFIG_CHANGED");
                        onMultiSimConfigChanged(((Integer) ((AsyncResult) message.obj).result).intValue());
                        break;
                    default:
                        Rlog.e("UiccController", " Unknown Event " + message.what);
                        break;
                }
                return;
            }
            Rlog.e("UiccController", "Invalid phoneId : " + ciIndex + " received with event " + eventToString);
        }
    }

    private void onMultiSimConfigChanged(int i) {
        int length = this.mCis.length;
        this.mCis = PhoneFactory.getCommandsInterfaces();
        logWithLocalLog("onMultiSimConfigChanged: prevActiveModemCount " + length + ", newActiveModemCount " + i);
        this.mPhoneIdToSlotId = Arrays.copyOf(this.mPhoneIdToSlotId, i);
        while (length < i) {
            this.mPhoneIdToSlotId[length] = -1;
            this.mCis[length].registerForIccStatusChanged(this, 1, Integer.valueOf(length));
            this.mCis[length].registerForAvailable(this, 6, Integer.valueOf(length));
            this.mCis[length].registerForNotAvailable(this, 7, Integer.valueOf(length));
            this.mCis[length].registerForIccRefresh(this, 8, Integer.valueOf(length));
            length++;
        }
    }

    private Integer getCiIndex(Message message) {
        Object obj;
        Integer num = new Integer(0);
        if (message != null) {
            Object obj2 = message.obj;
            if (obj2 == null || !(obj2 instanceof Integer)) {
                return (obj2 == null || !(obj2 instanceof AsyncResult) || (obj = ((AsyncResult) obj2).userObj) == null || !(obj instanceof Integer)) ? num : (Integer) obj;
            }
            return (Integer) obj2;
        }
        return num;
    }

    private static String eventToString(int i) {
        switch (i) {
            case 1:
                return "ICC_STATUS_CHANGED";
            case 2:
                return "SLOT_STATUS_CHANGED";
            case 3:
                return "GET_ICC_STATUS_DONE";
            case 4:
                return "GET_SLOT_STATUS_DONE";
            case 5:
                return "RADIO_ON";
            case 6:
                return "RADIO_AVAILABLE";
            case 7:
                return "RADIO_UNAVAILABLE";
            case 8:
                return "SIM_REFRESH";
            case 9:
                return "EID_READY";
            case 10:
                return "MULTI_SIM_CONFIG_CHANGED";
            default:
                return "UNKNOWN(" + i + ")";
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public UiccCardApplication getUiccCardApplication(int i, int i2) {
        synchronized (mLock) {
            UiccPort uiccPortForPhone = getUiccPortForPhone(i);
            if (uiccPortForPhone != null) {
                return uiccPortForPhone.getApplication(i2);
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.internal.telephony.uicc.UiccController$1 */
    /* loaded from: classes.dex */
    public static /* synthetic */ class C03331 {
        static final /* synthetic */ int[] $SwitchMap$com$android$internal$telephony$IccCardConstants$State;

        static {
            int[] iArr = new int[IccCardConstants.State.values().length];
            $SwitchMap$com$android$internal$telephony$IccCardConstants$State = iArr;
            try {
                iArr[IccCardConstants.State.ABSENT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$IccCardConstants$State[IccCardConstants.State.PIN_REQUIRED.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$IccCardConstants$State[IccCardConstants.State.PUK_REQUIRED.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$IccCardConstants$State[IccCardConstants.State.NETWORK_LOCKED.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$IccCardConstants$State[IccCardConstants.State.READY.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$IccCardConstants$State[IccCardConstants.State.NOT_READY.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$IccCardConstants$State[IccCardConstants.State.PERM_DISABLED.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$IccCardConstants$State[IccCardConstants.State.CARD_IO_ERROR.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$IccCardConstants$State[IccCardConstants.State.CARD_RESTRICTED.ordinal()] = 9;
            } catch (NoSuchFieldError unused9) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$IccCardConstants$State[IccCardConstants.State.LOADED.ordinal()] = 10;
            } catch (NoSuchFieldError unused10) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$IccCardConstants$State[IccCardConstants.State.UNKNOWN.ordinal()] = 11;
            } catch (NoSuchFieldError unused11) {
            }
        }
    }

    public static String getIccStateIntentString(IccCardConstants.State state) {
        switch (C03331.$SwitchMap$com$android$internal$telephony$IccCardConstants$State[state.ordinal()]) {
            case 1:
                return "ABSENT";
            case 2:
            case 3:
            case 4:
                return "LOCKED";
            case 5:
                return "READY";
            case 6:
                return "NOT_READY";
            case 7:
                return "LOCKED";
            case 8:
                return "CARD_IO_ERROR";
            case 9:
                return "CARD_RESTRICTED";
            case 10:
                return "LOADED";
            default:
                return "UNKNOWN";
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void updateInternalIccStateForInactivePort(Context context, int i, String str) {
        if (SubscriptionManager.isValidPhoneId(i)) {
            ((TelephonyManager) context.getSystemService("phone")).setSimStateForPhone(i, IccCardConstants.State.ABSENT.toString());
        }
        SubscriptionInfoUpdater subscriptionInfoUpdater = PhoneFactory.getSubscriptionInfoUpdater();
        if (subscriptionInfoUpdater != null) {
            subscriptionInfoUpdater.updateInternalIccStateForInactivePort(i, str);
        } else {
            Rlog.e("UiccController", "subInfoUpdate is null.");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void updateInternalIccState(Context context, IccCardConstants.State state, String str, int i) {
        ((TelephonyManager) context.getSystemService("phone")).setSimStateForPhone(i, state.toString());
        SubscriptionInfoUpdater subscriptionInfoUpdater = PhoneFactory.getSubscriptionInfoUpdater();
        if (subscriptionInfoUpdater != null) {
            subscriptionInfoUpdater.updateInternalIccState(getIccStateIntentString(state), str, i);
        } else {
            Rlog.e("UiccController", "subInfoUpdate is null.");
        }
    }

    public void updateSimStateForInactivePort(final int i, String str) {
        post(new Runnable() { // from class: com.android.internal.telephony.uicc.UiccController$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                UiccController.this.lambda$updateSimStateForInactivePort$1(i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateSimStateForInactivePort$1(int i) {
        if (SubscriptionManager.isValidPhoneId(i)) {
            this.mTelephonyManager.setSimStateForPhone(i, IccCardConstants.State.ABSENT.toString());
        }
        SubscriptionManagerService.getInstance().updateSimStateForInactivePort(i);
    }

    private void broadcastSimStateChanged(int i, String str, String str2) {
        Intent intent = new Intent("android.intent.action.SIM_STATE_CHANGED");
        intent.addFlags(67108864);
        intent.putExtra("phoneName", "Phone");
        intent.putExtra("ss", str);
        intent.putExtra("reason", str2);
        SubscriptionManager.putPhoneIdAndSubIdExtra(intent, i);
        Rlog.d("UiccController", "Broadcasting intent ACTION_SIM_STATE_CHANGED " + str + " reason " + str2 + " for phone: " + i);
        IntentBroadcaster.getInstance().broadcastStickyIntent(this.mContext, intent, i);
    }

    private void broadcastSimCardStateChanged(int i, @TelephonyManager.SimState int i2) {
        int[] iArr = this.mSimCardState;
        if (i2 != iArr[i]) {
            iArr[i] = i2;
            Intent intent = new Intent("android.telephony.action.SIM_CARD_STATE_CHANGED");
            intent.addFlags(67108864);
            intent.putExtra("android.telephony.extra.SIM_STATE", i2);
            SubscriptionManager.putPhoneIdAndSubIdExtra(intent, i);
            UiccSlot uiccSlotForPhone = getInstance().getUiccSlotForPhone(i);
            int slotIdFromPhoneId = getInstance().getSlotIdFromPhoneId(i);
            intent.putExtra("slot", slotIdFromPhoneId);
            if (uiccSlotForPhone != null) {
                intent.putExtra("port", uiccSlotForPhone.getPortIndexFromPhoneId(i));
            }
            Rlog.d("UiccController", "Broadcasting intent ACTION_SIM_CARD_STATE_CHANGED " + TelephonyManager.simStateToString(i2) + " for phone: " + i + " slot: " + slotIdFromPhoneId + " port: " + uiccSlotForPhone.getPortIndexFromPhoneId(i));
            this.mContext.sendBroadcast(intent, "android.permission.READ_PRIVILEGED_PHONE_STATE");
            TelephonyMetrics.getInstance().updateSimState(i, i2);
        }
    }

    private void broadcastSimApplicationStateChanged(int i, @TelephonyManager.SimState int i2) {
        boolean z = true;
        boolean z2 = this.mSimApplicationState[i] == 0 && i2 == 6;
        IccCard iccCard = PhoneFactory.getPhone(i).getIccCard();
        if (iccCard == null || !iccCard.isEmptyProfile()) {
            z = false;
        }
        int[] iArr = this.mSimApplicationState;
        if (i2 != iArr[i]) {
            if (!z2 || z) {
                iArr[i] = i2;
                Intent intent = new Intent("android.telephony.action.SIM_APPLICATION_STATE_CHANGED");
                intent.addFlags(67108864);
                intent.putExtra("android.telephony.extra.SIM_STATE", i2);
                SubscriptionManager.putPhoneIdAndSubIdExtra(intent, i);
                UiccSlot uiccSlotForPhone = getInstance().getUiccSlotForPhone(i);
                int slotIdFromPhoneId = getInstance().getSlotIdFromPhoneId(i);
                intent.putExtra("slot", slotIdFromPhoneId);
                if (uiccSlotForPhone != null) {
                    intent.putExtra("port", uiccSlotForPhone.getPortIndexFromPhoneId(i));
                }
                Rlog.d("UiccController", "Broadcasting intent ACTION_SIM_APPLICATION_STATE_CHANGED " + TelephonyManager.simStateToString(i2) + " for phone: " + i + " slot: " + slotIdFromPhoneId + "port: " + uiccSlotForPhone.getPortIndexFromPhoneId(i));
                this.mContext.sendBroadcast(intent, "android.permission.READ_PRIVILEGED_PHONE_STATE");
                TelephonyMetrics.getInstance().updateSimState(i, i2);
            }
        }
    }

    @TelephonyManager.SimState
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
                Rlog.e("UiccController", "Unexpected SIM locked reason " + str);
                return 0;
        }
    }

    private void broadcastSimStateEvents(int i, IccCardConstants.State state, String str) {
        String iccStateIntentString = getIccStateIntentString(state);
        int i2 = 6;
        int i3 = 11;
        switch (C03331.$SwitchMap$com$android$internal$telephony$IccCardConstants$State[state.ordinal()]) {
            case 1:
                i3 = 1;
                i2 = 0;
                break;
            case 2:
            case 3:
            case 4:
            case 7:
                i2 = getSimStateFromLockedReason(str);
                break;
            case 5:
            case 6:
                break;
            case 8:
                i3 = 8;
                break;
            case 9:
                i3 = 9;
                break;
            case 10:
                i2 = 10;
                break;
            default:
                i2 = 0;
                i3 = 0;
                break;
        }
        broadcastSimStateChanged(i, iccStateIntentString, str);
        broadcastSimCardStateChanged(i, i3);
        broadcastSimApplicationStateChanged(i, i2);
    }

    private void updateCarrierServices(int i, String str) {
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) this.mContext.getSystemService(CarrierConfigManager.class);
        if (carrierConfigManager != null) {
            carrierConfigManager.updateConfigForPhoneId(i, str);
        }
        this.mCarrierServiceBindHelper.updateForPhoneId(i, str);
    }

    public void updateSimState(final int i, final IccCardConstants.State state, final String str) {
        post(new Runnable() { // from class: com.android.internal.telephony.uicc.UiccController$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                UiccController.this.lambda$updateSimState$3(i, state, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateSimState$3(final int i, final IccCardConstants.State state, final String str) {
        log("updateSimState: phoneId=" + i + ", state=" + state + ", reason=" + str);
        if (!SubscriptionManager.isValidPhoneId(i)) {
            Rlog.e("UiccController", "updateInternalIccState: Invalid phone id " + i);
            return;
        }
        this.mTelephonyManager.setSimStateForPhone(i, state.toString());
        final String iccStateIntentString = getIccStateIntentString(state);
        final int ordinal = state.ordinal();
        SubscriptionManagerService.getInstance().updateSimState(i, ordinal, new Executor() { // from class: com.android.internal.telephony.uicc.UiccController$$ExternalSyntheticLambda4
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                UiccController.this.post(runnable);
            }
        }, new Runnable() { // from class: com.android.internal.telephony.uicc.UiccController$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                UiccController.this.lambda$updateSimState$2(i, state, str, ordinal, iccStateIntentString);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateSimState$2(int i, IccCardConstants.State state, String str, int i2, String str2) {
        broadcastSimStateEvents(i, state, str);
        UiccProfile uiccProfileForPhone = getUiccProfileForPhone(i);
        if (i2 == 5) {
            return;
        }
        if (i2 == 6 && uiccProfileForPhone != null && !uiccProfileForPhone.isEmptyProfile() && SubscriptionManagerService.getInstance().areUiccAppsEnabledOnCard(i)) {
            log("updateSimState: SIM_STATE_NOT_READY is not a final state.");
            return;
        }
        log("updateSimState: resolve carrier id and update carrier services.");
        PhoneFactory.getPhone(i).resolveSubscriptionCarrierId(str2);
        updateCarrierServices(i, str2);
    }

    private synchronized void onGetIccCardStatusDone(AsyncResult asyncResult, Integer num) {
        String iccId;
        int i;
        Throwable th = asyncResult.exception;
        if (th != null) {
            Rlog.e("UiccController", "Error getting ICC status. RIL_REQUEST_GET_ICC_STATUS should never return an error", th);
        } else if (!isValidPhoneIndex(num.intValue())) {
            Rlog.e("UiccController", "onGetIccCardStatusDone: invalid index : " + num);
        } else if (isShuttingDown()) {
            log("onGetIccCardStatusDone: shudown in progress ignore event");
        } else {
            IccCardStatus iccCardStatus = (IccCardStatus) asyncResult.result;
            logWithLocalLog("onGetIccCardStatusDone: phoneId-" + num + " IccCardStatus: " + iccCardStatus);
            int i2 = iccCardStatus.mSlotPortMapping.mPhysicalSlotIndex;
            if (i2 == -1) {
                i2 = num.intValue();
            }
            if (!this.mCis[0].supportsEid()) {
                log("eid is not supported");
                this.mDefaultEuiccCardId = -1;
            }
            this.mPhoneIdToSlotId[num.intValue()] = i2;
            UiccSlot[] uiccSlotArr = this.mUiccSlots;
            if (uiccSlotArr[i2] == null) {
                uiccSlotArr[i2] = new UiccSlot(this.mContext, true);
            }
            this.mUiccSlots[i2].update(this.mCis[num.intValue()], iccCardStatus, num.intValue(), i2);
            UiccCard uiccCard = this.mUiccSlots[i2].getUiccCard();
            if (uiccCard == null) {
                log("mUiccSlots[" + i2 + "] has no card. Notifying IccChangedRegistrants");
                this.mIccChangedRegistrants.notifyRegistrants(new AsyncResult((Object) null, num, (Throwable) null));
            } else if (uiccCard.getUiccPort(iccCardStatus.mSlotPortMapping.mPortIndex) == null) {
                log("mUiccSlots[" + i2 + "] has no UiccPort with index[" + iccCardStatus.mSlotPortMapping.mPortIndex + "]. Notifying IccChangedRegistrants");
                this.mIccChangedRegistrants.notifyRegistrants(new AsyncResult((Object) null, num, (Throwable) null));
            } else {
                boolean isEuicc = this.mUiccSlots[i2].isEuicc();
                if (isEuicc) {
                    iccId = ((EuiccCard) uiccCard).getEid();
                } else {
                    iccId = uiccCard.getUiccPort(iccCardStatus.mSlotPortMapping.mPortIndex).getIccId();
                }
                if (iccId != null) {
                    addCardId(iccId);
                }
                if (isEuicc && (i = this.mDefaultEuiccCardId) != -1) {
                    if (iccId == null) {
                        ((EuiccCard) uiccCard).registerForEidReady(this, 9, num);
                    } else if (i == -2 || i == -3) {
                        this.mDefaultEuiccCardId = convertToPublicCardId(iccId);
                        logWithLocalLog("IccCardStatus eid=" + Rlog.pii(TelephonyUtils.IS_DEBUGGABLE, iccId) + " slot=" + i2 + " mDefaultEuiccCardId=" + this.mDefaultEuiccCardId);
                    }
                }
                log("Notifying IccChangedRegistrants");
                this.mIccChangedRegistrants.notifyRegistrants(new AsyncResult((Object) null, num, (Throwable) null));
            }
        }
    }

    private void addCardId(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        if (str.length() < 32) {
            str = IccUtils.stripTrailingFs(str);
        }
        if (this.mCardStrings.contains(str)) {
            return;
        }
        this.mCardStrings.add(str);
        saveCardStrings();
    }

    public String convertToCardString(int i) {
        if (i < 0 || i >= this.mCardStrings.size()) {
            log("convertToCardString: cardId " + i + " is not valid");
            return null;
        }
        return this.mCardStrings.get(i);
    }

    public int convertToPublicCardId(String str) {
        if (this.mDefaultEuiccCardId == -1) {
            return -1;
        }
        if (TextUtils.isEmpty(str)) {
            return -2;
        }
        if (str.length() < 32) {
            str = IccUtils.stripTrailingFs(str);
        }
        int indexOf = this.mCardStrings.indexOf(str);
        if (indexOf == -1) {
            return -2;
        }
        return indexOf;
    }

    public ArrayList<UiccCardInfo> getAllUiccCardInfos() {
        int convertToPublicCardId;
        UiccController uiccController = this;
        ArrayList<UiccCardInfo> arrayList = new ArrayList<>();
        int i = 0;
        int i2 = 0;
        while (true) {
            UiccSlot[] uiccSlotArr = uiccController.mUiccSlots;
            if (i2 >= uiccSlotArr.length) {
                return arrayList;
            }
            UiccSlot uiccSlot = uiccSlotArr[i2];
            if (uiccSlot != null) {
                boolean isEuicc = uiccSlot.isEuicc();
                UiccCard uiccCard = uiccSlot.getUiccCard();
                boolean isRemovable = uiccSlot.isRemovable();
                String str = null;
                if (uiccCard == null) {
                    String iccId = uiccSlot.getIccId(i);
                    convertToPublicCardId = (isEuicc || TextUtils.isEmpty(iccId)) ? -2 : uiccController.convertToPublicCardId(iccId);
                } else if (isEuicc) {
                    String eid = ((EuiccCard) uiccCard).getEid();
                    str = eid;
                    convertToPublicCardId = uiccController.convertToPublicCardId(eid);
                } else {
                    UiccPort uiccPort = uiccCard.getUiccPort(i);
                    if (uiccPort == null) {
                        AnomalyReporter.reportAnomaly(UUID.fromString("92885ba7-98bb-490a-ba19-987b1c8b2055"), "UiccController: Found UiccPort Null object.");
                    }
                    convertToPublicCardId = uiccController.convertToPublicCardId(uiccPort != null ? uiccPort.getIccId() : null);
                }
                ArrayList arrayList2 = new ArrayList();
                int[] portList = uiccSlot.getPortList();
                int length = portList.length;
                for (int i3 = i; i3 < length; i3++) {
                    int i4 = portList[i3];
                    arrayList2.add(new UiccPortInfo(IccUtils.stripTrailingFs(uiccSlot.getIccId(i4)), i4, uiccSlot.getPhoneIdFromPortIndex(i4), uiccSlot.isPortActive(i4)));
                }
                arrayList.add(new UiccCardInfo(isEuicc, convertToPublicCardId, str, i2, isRemovable, uiccSlot.isMultipleEnabledProfileSupported(), arrayList2));
            }
            i2++;
            uiccController = this;
            i = 0;
        }
    }

    public int getCardIdForDefaultEuicc() {
        UiccSlot[] uiccSlotArr;
        if (this.mDefaultEuiccCardId == -3) {
            return -1;
        }
        if (this.mUseRemovableEsimAsDefault && !TelephonyUtils.IS_USER) {
            for (UiccSlot uiccSlot : this.mUiccSlots) {
                if (uiccSlot != null && uiccSlot.isRemovable() && uiccSlot.isEuicc() && uiccSlot.isActive()) {
                    int convertToPublicCardId = convertToPublicCardId(uiccSlot.getEid());
                    Rlog.d("UiccController", "getCardIdForDefaultEuicc: Removable eSIM is default, cardId: " + convertToPublicCardId);
                    return convertToPublicCardId;
                }
            }
            Rlog.d("UiccController", "getCardIdForDefaultEuicc: No removable eSIM slot is found");
        }
        return this.mDefaultEuiccCardId;
    }

    public PinStorage getPinStorage() {
        return this.mPinStorage;
    }

    private ArrayList<String> loadCardStrings() {
        String string = PreferenceManager.getDefaultSharedPreferences(this.mContext).getString("card_strings", PhoneConfigurationManager.SSSS);
        if (TextUtils.isEmpty(string)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(string.split(",")));
    }

    private void saveCardStrings() {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this.mContext).edit();
        edit.putString("card_strings", TextUtils.join(",", this.mCardStrings));
        edit.commit();
    }

    private synchronized void onGetSlotStatusDone(AsyncResult asyncResult) {
        UiccSlot[] uiccSlotArr;
        if (this.mIsSlotStatusSupported) {
            Throwable th = asyncResult.exception;
            if (th != null) {
                if ((th instanceof CommandException) && ((CommandException) th).getCommandError() == CommandException.Error.REQUEST_NOT_SUPPORTED) {
                    logWithLocalLog("onGetSlotStatusDone: request not supported; marking mIsSlotStatusSupported to false");
                    this.mIsSlotStatusSupported = false;
                }
                logeWithLocalLog("Unexpected error getting slot status: " + asyncResult.exception);
            } else if (isShuttingDown()) {
                log("onGetSlotStatusDone: shudown in progress ignore event");
            } else {
                ArrayList<IccSlotStatus> arrayList = (ArrayList) asyncResult.result;
                if (!slotStatusChanged(arrayList)) {
                    log("onGetSlotStatusDone: No change in slot status");
                    return;
                }
                logWithLocalLog("onGetSlotStatusDone: " + arrayList);
                sLastSlotStatus = arrayList;
                this.mHasActiveBuiltInEuicc = false;
                int size = arrayList.size();
                if (this.mUiccSlots.length < size) {
                    logeWithLocalLog("The number of the physical slots reported " + size + " is greater than the expectation " + this.mUiccSlots.length);
                    size = this.mUiccSlots.length;
                }
                boolean z = false;
                int i = 0;
                boolean z2 = false;
                for (int i2 = 0; i2 < size; i2++) {
                    IccSlotStatus iccSlotStatus = arrayList.get(i2);
                    boolean hasActivePort = hasActivePort(iccSlotStatus.mSimPortInfos);
                    UiccSlot[] uiccSlotArr2 = this.mUiccSlots;
                    if (uiccSlotArr2[i2] == null) {
                        uiccSlotArr2[i2] = new UiccSlot(this.mContext, hasActivePort);
                    }
                    if (hasActivePort) {
                        int i3 = 0;
                        while (true) {
                            IccSimPortInfo[] iccSimPortInfoArr = iccSlotStatus.mSimPortInfos;
                            if (i3 >= iccSimPortInfoArr.length) {
                                break;
                            }
                            IccSimPortInfo iccSimPortInfo = iccSimPortInfoArr[i3];
                            if (iccSimPortInfo.mPortActive) {
                                int i4 = iccSimPortInfo.mLogicalSlotIndex;
                                if (isValidPhoneIndex(i4)) {
                                    this.mPhoneIdToSlotId[i4] = i2;
                                } else {
                                    Rlog.e("UiccController", "Skipping slot " + i2 + " portIndex " + i3 + " as phone " + i4 + " is not available to communicate with this slot");
                                }
                                i++;
                            }
                            i3++;
                        }
                    }
                    this.mUiccSlots[i2].update(this.mCis, iccSlotStatus, i2);
                    if (this.mUiccSlots[i2].isEuicc()) {
                        if (hasActivePort) {
                            if (isBuiltInEuiccSlot(i2)) {
                                this.mHasActiveBuiltInEuicc = true;
                            }
                            z2 = true;
                        }
                        String str = iccSlotStatus.eid;
                        if (!TextUtils.isEmpty(str)) {
                            addCardId(str);
                            if (!this.mUiccSlots[i2].isRemovable() && !z) {
                                this.mDefaultEuiccCardId = convertToPublicCardId(str);
                                logWithLocalLog("Using eid=" + Rlog.pii(TelephonyUtils.IS_DEBUGGABLE, str) + " in slot=" + i2 + " to set mDefaultEuiccCardId=" + this.mDefaultEuiccCardId);
                                z = true;
                            }
                        }
                    }
                }
                if (!this.mHasActiveBuiltInEuicc && !z) {
                    int i5 = 0;
                    while (true) {
                        if (i5 >= size) {
                            break;
                        }
                        if (this.mUiccSlots[i5].isEuicc()) {
                            String str2 = arrayList.get(i5).eid;
                            if (!TextUtils.isEmpty(str2)) {
                                this.mDefaultEuiccCardId = convertToPublicCardId(str2);
                                logWithLocalLog("Using eid=" + Rlog.pii(TelephonyUtils.IS_DEBUGGABLE, str2) + " from removable eUICC in slot=" + i5 + " to set mDefaultEuiccCardId=" + this.mDefaultEuiccCardId);
                                z = true;
                                break;
                            }
                        }
                        i5++;
                    }
                }
                if (this.mHasBuiltInEuicc && !z2 && !z) {
                    logWithLocalLog("onGetSlotStatusDone: mDefaultEuiccCardId=TEMPORARILY_UNSUPPORTED_CARD_ID");
                    this.mDefaultEuiccCardId = -3;
                    z = true;
                }
                if (!z) {
                    int i6 = this.mDefaultEuiccCardId;
                    if (i6 >= 0) {
                        String str3 = this.mCardStrings.get(i6);
                        boolean z3 = false;
                        for (UiccSlot uiccSlot : this.mUiccSlots) {
                            if (uiccSlot.getUiccCard() != null && str3.equals(IccUtils.stripTrailingFs(uiccSlot.getUiccCard().getCardId()))) {
                                z3 = true;
                            }
                        }
                        if (!z3) {
                            logWithLocalLog("onGetSlotStatusDone: mDefaultEuiccCardId=" + this.mDefaultEuiccCardId + " is no longer inserted. Setting mDefaultEuiccCardId=UNINITIALIZED");
                            this.mDefaultEuiccCardId = -2;
                        }
                    } else {
                        logWithLocalLog("onGetSlotStatusDone: mDefaultEuiccCardId=UNINITIALIZED");
                        this.mDefaultEuiccCardId = -2;
                    }
                }
                if (i != this.mPhoneIdToSlotId.length) {
                    Rlog.e("UiccController", "Number of active ports " + i + " does not match the number of Phones" + this.mPhoneIdToSlotId.length);
                }
                BroadcastOptions makeBasic = BroadcastOptions.makeBasic();
                makeBasic.setBackgroundActivityStartsAllowed(true);
                Intent intent = new Intent("android.telephony.action.SIM_SLOT_STATUS_CHANGED");
                intent.addFlags(67108864);
                this.mContext.sendBroadcast(intent, "android.permission.READ_PRIVILEGED_PHONE_STATE", makeBasic.toBundle());
            }
        }
    }

    private boolean hasActivePort(IccSimPortInfo[] iccSimPortInfoArr) {
        for (IccSimPortInfo iccSimPortInfo : iccSimPortInfoArr) {
            if (iccSimPortInfo.mPortActive) {
                return true;
            }
        }
        return false;
    }

    @VisibleForTesting
    public boolean slotStatusChanged(ArrayList<IccSlotStatus> arrayList) {
        ArrayList<IccSlotStatus> arrayList2 = sLastSlotStatus;
        if (arrayList2 == null || arrayList2.size() != arrayList.size()) {
            return true;
        }
        for (int i = 0; i < arrayList.size(); i++) {
            if (!sLastSlotStatus.get(i).equals(arrayList.get(i))) {
                return true;
            }
        }
        return false;
    }

    private void onSimRefresh(AsyncResult asyncResult, Integer num) {
        boolean resetAppWithAid;
        if (asyncResult.exception != null) {
            Rlog.e("UiccController", "onSimRefresh: Sim REFRESH with exception: " + asyncResult.exception);
        } else if (!isValidPhoneIndex(num.intValue())) {
            Rlog.e("UiccController", "onSimRefresh: invalid index : " + num);
        } else {
            IccRefreshResponse iccRefreshResponse = (IccRefreshResponse) asyncResult.result;
            logWithLocalLog("onSimRefresh: index " + num + ", " + iccRefreshResponse);
            if (iccRefreshResponse == null) {
                Rlog.e("UiccController", "onSimRefresh: received without input");
            } else if (getUiccCardForPhone(num.intValue()) == null) {
                Rlog.e("UiccController", "onSimRefresh: refresh on null card : " + num);
            } else {
                UiccPort uiccPortForPhone = getUiccPortForPhone(num.intValue());
                if (uiccPortForPhone == null) {
                    Rlog.e("UiccController", "onSimRefresh: refresh on null port : " + num);
                    return;
                }
                int i = iccRefreshResponse.refreshResult;
                if (i == 1) {
                    resetAppWithAid = uiccPortForPhone.resetAppWithAid(iccRefreshResponse.aid, false);
                } else if (i != 2) {
                    return;
                } else {
                    resetAppWithAid = uiccPortForPhone.resetAppWithAid(iccRefreshResponse.aid, true);
                }
                if (resetAppWithAid && iccRefreshResponse.refreshResult == 2) {
                    ((CarrierConfigManager) this.mContext.getSystemService("carrier_config")).updateConfigForPhoneId(num.intValue(), "UNKNOWN");
                }
                this.mCis[num.intValue()].getIccCardStatus(obtainMessage(3, num));
            }
        }
    }

    private void onEidReady(AsyncResult asyncResult, Integer num) {
        if (asyncResult.exception != null) {
            Rlog.e("UiccController", "onEidReady: exception: " + asyncResult.exception);
        } else if (!isValidPhoneIndex(num.intValue())) {
            Rlog.e("UiccController", "onEidReady: invalid index: " + num);
        } else {
            int i = this.mPhoneIdToSlotId[num.intValue()];
            EuiccCard euiccCard = (EuiccCard) this.mUiccSlots[i].getUiccCard();
            if (euiccCard == null) {
                Rlog.e("UiccController", "onEidReady: UiccCard in slot " + i + " is null");
                return;
            }
            String eid = euiccCard.getEid();
            addCardId(eid);
            int i2 = this.mDefaultEuiccCardId;
            if (i2 == -2 || i2 == -3) {
                if (!this.mUiccSlots[i].isRemovable()) {
                    this.mDefaultEuiccCardId = convertToPublicCardId(eid);
                    logWithLocalLog("onEidReady: eid=" + Rlog.pii(TelephonyUtils.IS_DEBUGGABLE, eid) + " slot=" + i + " mDefaultEuiccCardId=" + this.mDefaultEuiccCardId);
                } else if (!this.mHasActiveBuiltInEuicc) {
                    this.mDefaultEuiccCardId = convertToPublicCardId(eid);
                    logWithLocalLog("onEidReady: eid=" + Rlog.pii(TelephonyUtils.IS_DEBUGGABLE, eid) + " from removable eUICC in slot=" + i + " mDefaultEuiccCardId=" + this.mDefaultEuiccCardId);
                }
            }
            euiccCard.unregisterForEidReady(this);
        }
    }

    private boolean hasBuiltInEuicc() {
        int[] iArr = this.mEuiccSlots;
        return iArr != null && iArr.length > 0;
    }

    private boolean isBuiltInEuiccSlot(int i) {
        if (this.mHasBuiltInEuicc) {
            for (int i2 : this.mEuiccSlots) {
                if (i2 == i) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public static boolean isCdmaSupported(Context context) {
        return context.getPackageManager().hasSystemFeature("android.hardware.telephony.cdma");
    }

    private boolean isValidPhoneIndex(int i) {
        return i >= 0 && i < TelephonyManager.getDefault().getPhoneCount();
    }

    private boolean isValidSlotIndex(int i) {
        return i >= 0 && i < this.mUiccSlots.length;
    }

    private boolean isShuttingDown() {
        for (int i = 0; i < TelephonyManager.getDefault().getActiveModemCount(); i++) {
            if (PhoneFactory.getPhone(i) != null && PhoneFactory.getPhone(i).isShuttingDown()) {
                return true;
            }
        }
        return false;
    }

    private static boolean iccidMatches(String str, String str2) {
        for (String str3 : str.split(",")) {
            if (str2.startsWith(str3)) {
                Log.d("UiccController", "mvno icc id match found");
                return true;
            }
        }
        return false;
    }

    private static boolean imsiMatches(String str, String str2) {
        int length = str.length();
        if (length > 0 && length <= str2.length()) {
            for (int i = 0; i < length; i++) {
                char charAt = str.charAt(i);
                if (charAt != 'x' && charAt != 'X' && charAt != str2.charAt(i)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public boolean mvnoMatches(int i, int i2, String str) {
        String iccId;
        IccRecords iccRecords = getIccRecords(i, 1);
        if (iccRecords == null) {
            Log.d("UiccController", "isMvnoMatched# IccRecords is null");
            return false;
        }
        if (i2 == 0) {
            String serviceProviderNameWithBrandOverride = iccRecords.getServiceProviderNameWithBrandOverride();
            if (serviceProviderNameWithBrandOverride != null && serviceProviderNameWithBrandOverride.equalsIgnoreCase(str)) {
                return true;
            }
        } else if (i2 == 1) {
            String imsi = iccRecords.getIMSI();
            if (imsi != null && imsiMatches(str, imsi)) {
                return true;
            }
        } else if (i2 == 2) {
            String gid1 = iccRecords.getGid1();
            int length = str.length();
            if (gid1 != null && gid1.length() >= length && gid1.substring(0, length).equalsIgnoreCase(str)) {
                return true;
            }
        } else if (i2 == 3 && (iccId = iccRecords.getIccId()) != null && iccidMatches(str, iccId)) {
            return true;
        }
        return false;
    }

    public void setRemovableEsimAsDefaultEuicc(boolean z) {
        this.mUseRemovableEsimAsDefault = z;
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this.mContext).edit();
        edit.putBoolean("removable_esim", z);
        edit.apply();
        Rlog.d("UiccController", "setRemovableEsimAsDefaultEuicc isDefault: " + z);
    }

    public boolean isRemovableEsimDefaultEuicc() {
        Rlog.d("UiccController", "mUseRemovableEsimAsDefault: " + this.mUseRemovableEsimAsDefault);
        return this.mUseRemovableEsimAsDefault;
    }

    public IccSlotStatus.MultipleEnabledProfilesMode getSupportedMepMode(int i) {
        IccSlotStatus.MultipleEnabledProfilesMode multipleEnabledProfilesMode;
        synchronized (mLock) {
            UiccSlot uiccSlot = getUiccSlot(i);
            if (uiccSlot != null) {
                multipleEnabledProfilesMode = uiccSlot.getSupportedMepMode();
            } else {
                multipleEnabledProfilesMode = IccSlotStatus.MultipleEnabledProfilesMode.NONE;
            }
        }
        return multipleEnabledProfilesMode;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private void log(String str) {
        Rlog.d("UiccController", str);
    }

    private void logWithLocalLog(String str) {
        Rlog.d("UiccController", str);
        LocalLog localLog = sLocalLog;
        localLog.log("UiccController: " + str);
    }

    private void logeWithLocalLog(String str) {
        Rlog.e("UiccController", str);
        LocalLog localLog = sLocalLog;
        localLog.log("UiccController: " + str);
    }

    public static void addLocalLog(String str) {
        sLocalLog.log(str);
    }

    private List<String> getPrintableCardStrings() {
        if (!ArrayUtils.isEmpty(this.mCardStrings)) {
            return (List) this.mCardStrings.stream().map(new Function() { // from class: com.android.internal.telephony.uicc.UiccController$$ExternalSyntheticLambda1
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return SubscriptionInfo.givePrintableIccid((String) obj);
                }
            }).collect(Collectors.toList());
        }
        return this.mCardStrings;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        AndroidUtilIndentingPrintWriter androidUtilIndentingPrintWriter = new AndroidUtilIndentingPrintWriter(printWriter, "  ");
        androidUtilIndentingPrintWriter.println("mIsCdmaSupported=" + isCdmaSupported(this.mContext));
        androidUtilIndentingPrintWriter.println("mHasBuiltInEuicc=" + this.mHasBuiltInEuicc);
        androidUtilIndentingPrintWriter.println("mHasActiveBuiltInEuicc=" + this.mHasActiveBuiltInEuicc);
        androidUtilIndentingPrintWriter.println("mCardStrings=" + getPrintableCardStrings());
        androidUtilIndentingPrintWriter.println("mDefaultEuiccCardId=" + this.mDefaultEuiccCardId);
        androidUtilIndentingPrintWriter.println("mPhoneIdToSlotId=" + Arrays.toString(this.mPhoneIdToSlotId));
        androidUtilIndentingPrintWriter.println("mUseRemovableEsimAsDefault=" + this.mUseRemovableEsimAsDefault);
        androidUtilIndentingPrintWriter.println("mUiccSlots: size=" + this.mUiccSlots.length);
        androidUtilIndentingPrintWriter.increaseIndent();
        int i = 0;
        while (true) {
            UiccSlot[] uiccSlotArr = this.mUiccSlots;
            if (i < uiccSlotArr.length) {
                if (uiccSlotArr[i] == null) {
                    androidUtilIndentingPrintWriter.println("mUiccSlots[" + i + "]=null");
                } else {
                    androidUtilIndentingPrintWriter.println("mUiccSlots[" + i + "]:");
                    androidUtilIndentingPrintWriter.increaseIndent();
                    this.mUiccSlots[i].dump(fileDescriptor, androidUtilIndentingPrintWriter, strArr);
                    androidUtilIndentingPrintWriter.decreaseIndent();
                }
                i++;
            } else {
                androidUtilIndentingPrintWriter.decreaseIndent();
                androidUtilIndentingPrintWriter.println();
                androidUtilIndentingPrintWriter.println("sLocalLog= ");
                androidUtilIndentingPrintWriter.increaseIndent();
                this.mPinStorage.dump(fileDescriptor, androidUtilIndentingPrintWriter, strArr);
                sLocalLog.dump(fileDescriptor, androidUtilIndentingPrintWriter, strArr);
                return;
            }
        }
    }
}
