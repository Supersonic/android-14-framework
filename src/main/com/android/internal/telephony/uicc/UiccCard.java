package com.android.internal.telephony.uicc;

import android.compat.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.text.TextUtils;
import com.android.internal.telephony.AndroidUtilIndentingPrintWriter;
import com.android.internal.telephony.CommandsInterface;
import com.android.internal.telephony.uicc.IccCardStatus;
import com.android.internal.telephony.uicc.IccSlotStatus;
import com.android.internal.telephony.uicc.euicc.EuiccCard;
import com.android.internal.telephony.uicc.euicc.EuiccPort;
import com.android.internal.telephony.util.TelephonyUtils;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.function.IntFunction;
/* loaded from: classes.dex */
public class UiccCard {
    protected static final boolean DBG = true;
    public static final String EXTRA_ICC_CARD_ADDED = "com.android.internal.telephony.uicc.ICC_CARD_ADDED";
    protected static final String LOG_TAG = "UiccCard";
    protected String mCardId;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private IccCardStatus.CardState mCardState;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected final Object mLock;
    protected IccSlotStatus.MultipleEnabledProfilesMode mSupportedMepMode;
    protected LinkedHashMap<Integer, UiccPort> mUiccPorts = new LinkedHashMap<>();
    private HashMap<Integer, Integer> mPhoneIdToPortIdx = new HashMap<>();

    public UiccCard(Context context, CommandsInterface commandsInterface, IccCardStatus iccCardStatus, int i, Object obj, IccSlotStatus.MultipleEnabledProfilesMode multipleEnabledProfilesMode) {
        log("Creating");
        this.mCardState = iccCardStatus.mCardState;
        this.mLock = obj;
        this.mSupportedMepMode = multipleEnabledProfilesMode;
        update(context, commandsInterface, iccCardStatus, i);
    }

    public void dispose() {
        synchronized (this.mLock) {
            log("Disposing card");
            for (UiccPort uiccPort : this.mUiccPorts.values()) {
                if (uiccPort != null) {
                    uiccPort.dispose();
                }
            }
            this.mUiccPorts.clear();
            this.mUiccPorts = null;
            this.mPhoneIdToPortIdx.clear();
            this.mPhoneIdToPortIdx = null;
        }
    }

    public void disposePort(int i) {
        synchronized (this.mLock) {
            log("Disposing port for index " + i);
            UiccPort uiccPort = getUiccPort(i);
            if (uiccPort != null) {
                this.mPhoneIdToPortIdx.remove(Integer.valueOf(uiccPort.getPhoneId()));
                uiccPort.dispose();
            }
            this.mUiccPorts.remove(Integer.valueOf(i));
        }
    }

    public void update(Context context, CommandsInterface commandsInterface, IccCardStatus iccCardStatus, int i) {
        UiccPort uiccPort;
        synchronized (this.mLock) {
            this.mCardState = iccCardStatus.mCardState;
            updateCardId(iccCardStatus.iccid);
            if (this.mCardState != IccCardStatus.CardState.CARDSTATE_ABSENT) {
                int i2 = iccCardStatus.mSlotPortMapping.mPortIndex;
                UiccPort uiccPort2 = this.mUiccPorts.get(Integer.valueOf(i2));
                if (uiccPort2 == null) {
                    if (this instanceof EuiccCard) {
                        uiccPort = new EuiccPort(context, commandsInterface, iccCardStatus, i, this.mLock, this, this.mSupportedMepMode);
                    } else {
                        uiccPort = new UiccPort(context, commandsInterface, iccCardStatus, i, this.mLock, this);
                    }
                    this.mUiccPorts.put(Integer.valueOf(i2), uiccPort);
                } else {
                    uiccPort2.update(context, commandsInterface, iccCardStatus, this);
                }
                this.mPhoneIdToPortIdx.put(Integer.valueOf(i), Integer.valueOf(i2));
            } else {
                throw new RuntimeException("Card state is absent when updating!");
            }
        }
    }

    protected void finalize() {
        log("UiccCard finalized");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateCardId(String str) {
        this.mCardId = str;
    }

    public void updateSupportedMepMode(IccSlotStatus.MultipleEnabledProfilesMode multipleEnabledProfilesMode) {
        this.mSupportedMepMode = multipleEnabledProfilesMode;
    }

    @UnsupportedAppUsage
    public IccCardStatus.CardState getCardState() {
        IccCardStatus.CardState cardState;
        synchronized (this.mLock) {
            cardState = this.mCardState;
        }
        return cardState;
    }

    public String getCardId() {
        UiccProfile uiccProfile;
        if (!TextUtils.isEmpty(this.mCardId)) {
            return this.mCardId;
        }
        UiccPort uiccPort = this.mUiccPorts.get(0);
        if (uiccPort == null || (uiccProfile = uiccPort.getUiccProfile()) == null) {
            return null;
        }
        return uiccProfile.getIccId();
    }

    public UiccPort[] getUiccPortList() {
        UiccPort[] uiccPortArr;
        synchronized (this.mLock) {
            uiccPortArr = (UiccPort[]) this.mUiccPorts.values().stream().toArray(new IntFunction() { // from class: com.android.internal.telephony.uicc.UiccCard$$ExternalSyntheticLambda0
                @Override // java.util.function.IntFunction
                public final Object apply(int i) {
                    UiccPort[] lambda$getUiccPortList$0;
                    lambda$getUiccPortList$0 = UiccCard.lambda$getUiccPortList$0(i);
                    return lambda$getUiccPortList$0;
                }
            });
        }
        return uiccPortArr;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ UiccPort[] lambda$getUiccPortList$0(int i) {
        return new UiccPort[i];
    }

    public UiccPort getUiccPortForPhone(int i) {
        UiccPort uiccPort;
        synchronized (this.mLock) {
            uiccPort = this.mUiccPorts.get(this.mPhoneIdToPortIdx.get(Integer.valueOf(i)));
        }
        return uiccPort;
    }

    public UiccPort getUiccPort(int i) {
        UiccPort uiccPort;
        synchronized (this.mLock) {
            uiccPort = this.mUiccPorts.get(Integer.valueOf(i));
        }
        return uiccPort;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private void log(String str) {
        Rlog.d(LOG_TAG, str);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private void loge(String str) {
        Rlog.e(LOG_TAG, str);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        AndroidUtilIndentingPrintWriter androidUtilIndentingPrintWriter = new AndroidUtilIndentingPrintWriter(printWriter, "  ");
        androidUtilIndentingPrintWriter.println("UiccCard:");
        androidUtilIndentingPrintWriter.increaseIndent();
        androidUtilIndentingPrintWriter.println("mCardState=" + this.mCardState);
        androidUtilIndentingPrintWriter.println("mCardId=" + Rlog.pii(TelephonyUtils.IS_DEBUGGABLE, this.mCardId));
        androidUtilIndentingPrintWriter.println("mNumberOfPorts=" + this.mUiccPorts.size());
        androidUtilIndentingPrintWriter.println("mSupportedMepMode=" + this.mSupportedMepMode);
        androidUtilIndentingPrintWriter.println("mUiccPorts= size=" + this.mUiccPorts.size());
        androidUtilIndentingPrintWriter.increaseIndent();
        for (UiccPort uiccPort : this.mUiccPorts.values()) {
            uiccPort.dump(fileDescriptor, androidUtilIndentingPrintWriter, strArr);
        }
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.decreaseIndent();
    }
}
