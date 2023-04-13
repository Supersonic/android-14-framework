package com.android.internal.telephony.uicc;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.UserHandle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.telephony.AndroidUtilIndentingPrintWriter;
import com.android.internal.telephony.CommandsInterface;
import com.android.internal.telephony.IccCardConstants;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.RadioConfig$$ExternalSyntheticLambda0;
import com.android.internal.telephony.nano.TelephonyProto$TelephonyEvent;
import com.android.internal.telephony.uicc.AnswerToReset;
import com.android.internal.telephony.uicc.IccCardStatus;
import com.android.internal.telephony.uicc.IccSlotStatus;
import com.android.internal.telephony.uicc.euicc.EuiccCard;
import com.android.internal.telephony.util.ArrayUtils;
import com.android.internal.telephony.util.TelephonyUtils;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class UiccSlot extends Handler {
    public static final String EXTRA_ICC_CARD_ADDED = "com.android.internal.telephony.uicc.ICC_CARD_ADDED";
    public static final int INVALID_PHONE_ID = -1;
    public static final int VOLTAGE_CLASS_A = 1;
    public static final int VOLTAGE_CLASS_B = 2;
    public static final int VOLTAGE_CLASS_C = 3;
    public static final int VOLTAGE_CLASS_UNKNOWN = 0;
    private boolean mActive;
    private AnswerToReset mAtr;
    private Context mContext;
    private String mEid;
    private boolean mIsEuicc;
    private boolean mIsRemovable;
    private int mMinimumVoltageClass;
    private IccSlotStatus.MultipleEnabledProfilesMode mSupportedMepMode;
    private UiccCard mUiccCard;
    private final Object mLock = new Object();
    private boolean mStateIsUnknown = true;
    private HashMap<Integer, Integer> mPortIdxToPhoneId = new HashMap<>();
    private HashMap<Integer, Integer> mLastRadioState = new HashMap<>();
    private HashMap<Integer, String> mIccIds = new HashMap<>();
    private HashMap<Integer, IccCardStatus.CardState> mCardState = new HashMap<>();

    public UiccSlot(Context context, boolean z) {
        log("Creating");
        this.mContext = context;
        this.mActive = z;
        this.mSupportedMepMode = IccSlotStatus.MultipleEnabledProfilesMode.NONE;
    }

    public void update(CommandsInterface commandsInterface, IccCardStatus iccCardStatus, int i, int i2) {
        synchronized (this.mLock) {
            this.mPortIdxToPhoneId.put(Integer.valueOf(iccCardStatus.mSlotPortMapping.mPortIndex), Integer.valueOf(i));
            IccCardStatus.CardState cardState = this.mCardState.get(Integer.valueOf(iccCardStatus.mSlotPortMapping.mPortIndex));
            this.mCardState.put(Integer.valueOf(iccCardStatus.mSlotPortMapping.mPortIndex), iccCardStatus.mCardState);
            this.mIccIds.put(Integer.valueOf(iccCardStatus.mSlotPortMapping.mPortIndex), iccCardStatus.iccid);
            parseAtr(iccCardStatus.atr);
            this.mIsRemovable = isSlotRemovable(i2);
            if (iccCardStatus.mCardState.isCardPresent()) {
                updateSupportedMepMode(iccCardStatus.mSupportedMepMode);
            }
            int radioState = commandsInterface.getRadioState();
            log("update: radioState=" + radioState + " mLastRadioState=" + this.mLastRadioState);
            if (absentStateUpdateNeeded(cardState, iccCardStatus.mSlotPortMapping.mPortIndex)) {
                updateCardStateAbsent(commandsInterface.getRadioState(), i, iccCardStatus.mSlotPortMapping.mPortIndex);
            } else if ((cardState == null || cardState == IccCardStatus.CardState.CARDSTATE_ABSENT || this.mUiccCard == null) && this.mCardState.get(Integer.valueOf(iccCardStatus.mSlotPortMapping.mPortIndex)) != IccCardStatus.CardState.CARDSTATE_ABSENT) {
                if (radioState != 2 && this.mLastRadioState.getOrDefault(Integer.valueOf(iccCardStatus.mSlotPortMapping.mPortIndex), 2).intValue() != 2) {
                    log("update: notify card added");
                    sendMessage(obtainMessage(14, null));
                }
                UiccCard uiccCard = this.mUiccCard;
                if (uiccCard != null && (!this.mIsEuicc || ArrayUtils.isEmpty(uiccCard.getUiccPortList()))) {
                    loge("update: mUiccCard != null when card was present; disposing it now");
                    this.mUiccCard.dispose();
                    this.mUiccCard = null;
                }
                if (!this.mIsEuicc) {
                    this.mUiccCard = new UiccCard(this.mContext, commandsInterface, iccCardStatus, i, this.mLock, IccSlotStatus.MultipleEnabledProfilesMode.NONE);
                } else {
                    if (TextUtils.isEmpty(iccCardStatus.eid)) {
                        loge("update: eid is missing. ics.eid=" + Rlog.pii(TelephonyUtils.IS_DEBUGGABLE, iccCardStatus.eid));
                    }
                    UiccCard uiccCard2 = this.mUiccCard;
                    if (uiccCard2 == null) {
                        this.mUiccCard = new EuiccCard(this.mContext, commandsInterface, iccCardStatus, i, this.mLock, getSupportedMepMode());
                    } else {
                        uiccCard2.update(this.mContext, commandsInterface, iccCardStatus, i);
                    }
                }
            } else {
                UiccCard uiccCard3 = this.mUiccCard;
                if (uiccCard3 != null) {
                    uiccCard3.update(this.mContext, commandsInterface, iccCardStatus, i);
                }
            }
            this.mLastRadioState.put(Integer.valueOf(iccCardStatus.mSlotPortMapping.mPortIndex), Integer.valueOf(radioState));
        }
    }

    public void update(CommandsInterface[] commandsInterfaceArr, IccSlotStatus iccSlotStatus, int i) {
        synchronized (this.mLock) {
            IccSimPortInfo[] iccSimPortInfoArr = iccSlotStatus.mSimPortInfos;
            parseAtr(iccSlotStatus.atr);
            this.mEid = iccSlotStatus.eid;
            this.mIsRemovable = isSlotRemovable(i);
            for (int i2 = 0; i2 < iccSimPortInfoArr.length; i2++) {
                int i3 = iccSlotStatus.mSimPortInfos[i2].mLogicalSlotIndex;
                IccCardStatus.CardState cardState = this.mCardState.get(Integer.valueOf(i2));
                this.mCardState.put(Integer.valueOf(i2), iccSlotStatus.cardState);
                this.mIccIds.put(Integer.valueOf(i2), iccSimPortInfoArr[i2].mIccId);
                if (!iccSlotStatus.mSimPortInfos[i2].mPortActive) {
                    if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
                        UiccController.getInstance().updateSimStateForInactivePort(this.mPortIdxToPhoneId.getOrDefault(Integer.valueOf(i2), -1).intValue(), iccSlotStatus.mSimPortInfos[i2].mIccId);
                    } else {
                        UiccController.updateInternalIccStateForInactivePort(this.mContext, this.mPortIdxToPhoneId.getOrDefault(Integer.valueOf(i2), -1).intValue(), iccSlotStatus.mSimPortInfos[i2].mIccId);
                    }
                    this.mLastRadioState.put(Integer.valueOf(i2), 2);
                    UiccCard uiccCard = this.mUiccCard;
                    if (uiccCard != null) {
                        uiccCard.disposePort(i2);
                    }
                } else if (absentStateUpdateNeeded(cardState, i2)) {
                    updateCardStateAbsent(SubscriptionManager.isValidPhoneId(i3) ? commandsInterfaceArr[i3].getRadioState() : 2, i3, i2);
                }
            }
            if (!hasActivePort(iccSimPortInfoArr)) {
                if (this.mActive) {
                    this.mActive = false;
                    nullifyUiccCard(true);
                }
            } else {
                this.mActive = true;
            }
            this.mPortIdxToPhoneId.clear();
            for (int i4 = 0; i4 < iccSimPortInfoArr.length; i4++) {
                HashMap<Integer, Integer> hashMap = this.mPortIdxToPhoneId;
                Integer valueOf = Integer.valueOf(i4);
                IccSimPortInfo iccSimPortInfo = iccSimPortInfoArr[i4];
                hashMap.put(valueOf, Integer.valueOf(iccSimPortInfo.mPortActive ? iccSimPortInfo.mLogicalSlotIndex : -1));
            }
            updateSupportedMepMode(iccSlotStatus.mSupportedMepMode);
            UiccCard uiccCard2 = this.mUiccCard;
            if (uiccCard2 != null) {
                uiccCard2.updateSupportedMepMode(getSupportedMepMode());
            }
        }
    }

    private void updateSupportedMepMode(IccSlotStatus.MultipleEnabledProfilesMode multipleEnabledProfilesMode) {
        AnswerToReset answerToReset;
        this.mSupportedMepMode = multipleEnabledProfilesMode;
        if (multipleEnabledProfilesMode != IccSlotStatus.MultipleEnabledProfilesMode.NONE || this.mPortIdxToPhoneId.size() <= 1 || (answerToReset = this.mAtr) == null || !answerToReset.isMultipleEnabledProfilesSupported()) {
            return;
        }
        Log.i("UiccSlot", "Modem does not send proper supported MEP mode or older HAL version");
        this.mSupportedMepMode = IccSlotStatus.MultipleEnabledProfilesMode.MEP_B;
    }

    private boolean hasActivePort(IccSimPortInfo[] iccSimPortInfoArr) {
        for (IccSimPortInfo iccSimPortInfo : iccSimPortInfoArr) {
            if (iccSimPortInfo.mPortActive) {
                return true;
            }
        }
        return false;
    }

    private int getAnyValidPhoneId() {
        for (Integer num : this.mPortIdxToPhoneId.values()) {
            int intValue = num.intValue();
            if (SubscriptionManager.isValidPhoneId(intValue)) {
                return intValue;
            }
        }
        return -1;
    }

    public int[] getPortList() {
        int[] array;
        synchronized (this.mLock) {
            array = this.mPortIdxToPhoneId.keySet().stream().mapToInt(new RadioConfig$$ExternalSyntheticLambda0()).toArray();
        }
        return array;
    }

    public boolean isValidPortIndex(int i) {
        return this.mPortIdxToPhoneId.containsKey(Integer.valueOf(i));
    }

    public int getPortIndexFromPhoneId(int i) {
        synchronized (this.mLock) {
            for (Map.Entry<Integer, Integer> entry : this.mPortIdxToPhoneId.entrySet()) {
                if (entry.getValue().intValue() == i) {
                    return entry.getKey().intValue();
                }
            }
            return 0;
        }
    }

    public int getPortIndexFromIccId(String str) {
        synchronized (this.mLock) {
            for (Map.Entry<Integer, String> entry : this.mIccIds.entrySet()) {
                if (IccUtils.compareIgnoreTrailingFs(entry.getValue(), str)) {
                    return entry.getKey().intValue();
                }
            }
            return -1;
        }
    }

    public int getPhoneIdFromPortIndex(int i) {
        int intValue;
        synchronized (this.mLock) {
            intValue = this.mPortIdxToPhoneId.getOrDefault(Integer.valueOf(i), -1).intValue();
        }
        return intValue;
    }

    public boolean isPortActive(int i) {
        boolean isValidPhoneId;
        synchronized (this.mLock) {
            isValidPhoneId = SubscriptionManager.isValidPhoneId(this.mPortIdxToPhoneId.getOrDefault(Integer.valueOf(i), -1).intValue());
        }
        return isValidPhoneId;
    }

    public boolean isMultipleEnabledProfileSupported() {
        boolean isMepMode;
        synchronized (this.mLock) {
            isMepMode = this.mSupportedMepMode.isMepMode();
        }
        return isMepMode;
    }

    private boolean absentStateUpdateNeeded(IccCardStatus.CardState cardState, int i) {
        IccCardStatus.CardState cardState2 = IccCardStatus.CardState.CARDSTATE_ABSENT;
        return !(cardState == cardState2 && this.mUiccCard == null) && this.mCardState.get(Integer.valueOf(i)) == cardState2;
    }

    private void updateCardStateAbsent(int i, int i2, int i3) {
        if (i != 2 && this.mLastRadioState.getOrDefault(Integer.valueOf(i3), 2).intValue() != 2) {
            log("update: notify card removed");
            sendMessage(obtainMessage(13, null));
        }
        if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
            UiccController.getInstance().updateSimState(i2, IccCardConstants.State.ABSENT, null);
        } else {
            UiccController.updateInternalIccState(this.mContext, IccCardConstants.State.ABSENT, null, i2);
        }
        disposeUiccCardIfNeeded(false, i3);
        this.mLastRadioState.put(Integer.valueOf(i3), 2);
    }

    private void nullifyUiccCard(boolean z) {
        UiccCard uiccCard = this.mUiccCard;
        if (uiccCard != null) {
            uiccCard.dispose();
        }
        this.mStateIsUnknown = z;
        this.mUiccCard = null;
    }

    private void disposeUiccCardIfNeeded(boolean z, int i) {
        UiccCard uiccCard = this.mUiccCard;
        if (uiccCard != null) {
            uiccCard.disposePort(i);
            if (ArrayUtils.isEmpty(this.mUiccCard.getUiccPortList())) {
                nullifyUiccCard(z);
            }
        }
    }

    public boolean isStateUnknown() {
        IccCardStatus.CardState cardState = this.mCardState.get(0);
        if (cardState == null || cardState == IccCardStatus.CardState.CARDSTATE_ABSENT) {
            return this.mStateIsUnknown;
        }
        return this.mUiccCard == null;
    }

    private boolean isSlotRemovable(int i) {
        int[] intArray = this.mContext.getResources().getIntArray(17236189);
        if (intArray == null) {
            return true;
        }
        for (int i2 : intArray) {
            if (i2 == i) {
                return false;
            }
        }
        return true;
    }

    private void checkIsEuiccSupported() {
        AnswerToReset answerToReset = this.mAtr;
        if (answerToReset == null) {
            this.mIsEuicc = false;
            return;
        }
        this.mIsEuicc = answerToReset.isEuiccSupported();
        log(" checkIsEuiccSupported : " + this.mIsEuicc);
    }

    private void checkMinimumVoltageClass() {
        this.mMinimumVoltageClass = 0;
        AnswerToReset answerToReset = this.mAtr;
        if (answerToReset == null) {
            return;
        }
        List<AnswerToReset.InterfaceByte> interfaceBytes = answerToReset.getInterfaceBytes();
        for (int i = 0; i < interfaceBytes.size() - 1; i++) {
            if (interfaceBytes.get(i).getTD() != null && (interfaceBytes.get(i).getTD().byteValue() & 15) == 15) {
                int i2 = i + 1;
                if (interfaceBytes.get(i2).getTA() != null) {
                    byte byteValue = interfaceBytes.get(i2).getTA().byteValue();
                    if ((byteValue & 1) != 0) {
                        this.mMinimumVoltageClass = 1;
                    }
                    if ((byteValue & 2) != 0) {
                        this.mMinimumVoltageClass = 2;
                    }
                    if ((byteValue & 4) != 0) {
                        this.mMinimumVoltageClass = 3;
                        return;
                    }
                    return;
                }
            }
        }
        this.mMinimumVoltageClass = 1;
    }

    private void parseAtr(String str) {
        this.mAtr = AnswerToReset.parseAtr(str);
        checkIsEuiccSupported();
        checkMinimumVoltageClass();
    }

    public boolean isEuicc() {
        return this.mIsEuicc;
    }

    public int getMinimumVoltageClass() {
        return this.mMinimumVoltageClass;
    }

    public boolean isActive() {
        return this.mActive;
    }

    public boolean isRemovable() {
        return this.mIsRemovable;
    }

    public String getIccId(int i) {
        return this.mIccIds.get(Integer.valueOf(i));
    }

    public String getEid() {
        return this.mEid;
    }

    public boolean isExtendedApduSupported() {
        AnswerToReset answerToReset = this.mAtr;
        return answerToReset != null && answerToReset.isExtendedApduSupported();
    }

    protected void finalize() {
        log("UiccSlot finalized");
    }

    private void onIccSwap(boolean z) {
        if (this.mContext.getResources().getBoolean(17891703)) {
            log("onIccSwap: isHotSwapSupported is true, don't prompt for rebooting");
            return;
        }
        Phone phone = PhoneFactory.getPhone(getAnyValidPhoneId());
        if (phone != null && phone.isShuttingDown()) {
            log("onIccSwap: already doing shutdown, no need to prompt");
            return;
        }
        log("onIccSwap: isHotSwapSupported is false, prompt for rebooting");
        promptForRestart(z);
    }

    private void promptForRestart(boolean z) {
        String string;
        String string2;
        synchronized (this.mLock) {
            ComponentName unflattenFromString = ComponentName.unflattenFromString(this.mContext.getResources().getString(17039948));
            if (unflattenFromString != null) {
                try {
                    this.mContext.startActivityAsUser(new Intent().setComponent(unflattenFromString).addFlags(268435456).putExtra("com.android.internal.telephony.uicc.ICC_CARD_ADDED", z), UserHandle.CURRENT);
                    return;
                } catch (ActivityNotFoundException e) {
                    loge("Unable to find ICC hotswap prompt for restart activity: " + e);
                }
            }
            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.android.internal.telephony.uicc.UiccSlot.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    synchronized (UiccSlot.this.mLock) {
                        if (i == -1) {
                            UiccSlot.this.log("Reboot due to SIM swap");
                            ((PowerManager) UiccSlot.this.mContext.getSystemService("power")).reboot("SIM is added.");
                        }
                    }
                }
            };
            Resources system = Resources.getSystem();
            if (z) {
                string = system.getString(17041540);
            } else {
                string = system.getString(17041543);
            }
            if (z) {
                string2 = system.getString(17041539);
            } else {
                string2 = system.getString(17041542);
            }
            AlertDialog create = new AlertDialog.Builder(this.mContext).setTitle(string).setMessage(string2).setPositiveButton(system.getString(17041544), onClickListener).create();
            create.getWindow().setType(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_MIP_FA_MOBILE_NODE_AUTHENTICATION_FAILURE);
            create.show();
        }
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i = message.what;
        if (i == 13) {
            onIccSwap(false);
        } else if (i == 14) {
            onIccSwap(true);
        } else {
            loge("Unknown Event " + message.what);
        }
    }

    public IccCardStatus.CardState getCardState() {
        IccCardStatus.CardState cardState;
        synchronized (this.mLock) {
            cardState = this.mCardState.get(0);
            if (cardState == null) {
                cardState = IccCardStatus.CardState.CARDSTATE_ABSENT;
            }
        }
        return cardState;
    }

    public UiccCard getUiccCard() {
        UiccCard uiccCard;
        synchronized (this.mLock) {
            uiccCard = this.mUiccCard;
        }
        return uiccCard;
    }

    public IccSlotStatus.MultipleEnabledProfilesMode getSupportedMepMode() {
        IccSlotStatus.MultipleEnabledProfilesMode multipleEnabledProfilesMode;
        synchronized (this.mLock) {
            multipleEnabledProfilesMode = this.mSupportedMepMode;
        }
        return multipleEnabledProfilesMode;
    }

    public void onRadioStateUnavailable(int i) {
        int portIndexFromPhoneId = getPortIndexFromPhoneId(i);
        disposeUiccCardIfNeeded(true, portIndexFromPhoneId);
        if (i != -1) {
            if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
                UiccController.getInstance().updateSimState(i, IccCardConstants.State.UNKNOWN, null);
            } else {
                UiccController.updateInternalIccState(this.mContext, IccCardConstants.State.UNKNOWN, null, i);
            }
        }
        this.mLastRadioState.put(Integer.valueOf(portIndexFromPhoneId), 2);
        this.mCardState.put(Integer.valueOf(portIndexFromPhoneId), null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void log(String str) {
        Rlog.d("UiccSlot", str);
    }

    private void loge(String str) {
        Rlog.e("UiccSlot", str);
    }

    private Map<Integer, String> getPrintableIccIds() {
        return (Map) this.mIccIds.entrySet().stream().collect(Collectors.toMap(new Function() { // from class: com.android.internal.telephony.uicc.UiccSlot$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return (Integer) ((Map.Entry) obj).getKey();
            }
        }, new Function() { // from class: com.android.internal.telephony.uicc.UiccSlot$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String lambda$getPrintableIccIds$0;
                lambda$getPrintableIccIds$0 = UiccSlot.lambda$getPrintableIccIds$0((Map.Entry) obj);
                return lambda$getPrintableIccIds$0;
            }
        }));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ String lambda$getPrintableIccIds$0(Map.Entry entry) {
        return SubscriptionInfo.givePrintableIccid((String) entry.getValue());
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        AndroidUtilIndentingPrintWriter androidUtilIndentingPrintWriter = new AndroidUtilIndentingPrintWriter(printWriter, "  ");
        androidUtilIndentingPrintWriter.println("mActive=" + this.mActive);
        androidUtilIndentingPrintWriter.println("mIsEuicc=" + this.mIsEuicc);
        androidUtilIndentingPrintWriter.println("isEuiccSupportsMultipleEnabledProfiles=" + isMultipleEnabledProfileSupported());
        androidUtilIndentingPrintWriter.println("mIsRemovable=" + this.mIsRemovable);
        androidUtilIndentingPrintWriter.println("mLastRadioState=" + this.mLastRadioState);
        androidUtilIndentingPrintWriter.println("mIccIds=" + getPrintableIccIds());
        androidUtilIndentingPrintWriter.println("mPortIdxToPhoneId=" + this.mPortIdxToPhoneId);
        androidUtilIndentingPrintWriter.println("mEid=" + Rlog.pii(TelephonyUtils.IS_DEBUGGABLE, this.mEid));
        androidUtilIndentingPrintWriter.println("mCardState=" + this.mCardState);
        androidUtilIndentingPrintWriter.println("mSupportedMepMode=" + this.mSupportedMepMode);
        if (this.mUiccCard != null) {
            androidUtilIndentingPrintWriter.println("mUiccCard=");
            this.mUiccCard.dump(fileDescriptor, androidUtilIndentingPrintWriter, strArr);
        } else {
            androidUtilIndentingPrintWriter.println("mUiccCard=null");
        }
        androidUtilIndentingPrintWriter.println();
        androidUtilIndentingPrintWriter.flush();
    }

    @Override // android.os.Handler
    public String toString() {
        return "[UiccSlot: mActive=" + this.mActive + ", mIccId=" + getPrintableIccIds() + ", mIsEuicc=" + this.mIsEuicc + ", MEP=" + isMultipleEnabledProfileSupported() + ", mPortIdxToPhoneId=" + this.mPortIdxToPhoneId + ", mEid=" + Rlog.pii(TelephonyUtils.IS_DEBUGGABLE, this.mEid) + ", mCardState=" + this.mCardState + " mSupportedMepMode=" + this.mSupportedMepMode + "]";
    }
}
