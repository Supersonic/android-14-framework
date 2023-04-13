package com.android.internal.telephony.uicc;

import android.compat.annotation.UnsupportedAppUsage;
import android.telephony.SubscriptionInfo;
import com.android.internal.telephony.uicc.IccSlotStatus;
import com.android.internal.telephony.util.TelephonyUtils;
import com.android.telephony.Rlog;
/* loaded from: classes.dex */
public class IccCardStatus {
    public static final int CARD_MAX_APPS = 8;
    public String atr;
    public String eid;
    public String iccid;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public IccCardApplicationStatus[] mApplications;
    @UnsupportedAppUsage
    public CardState mCardState;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public int mCdmaSubscriptionAppIndex;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public int mGsmUmtsSubscriptionAppIndex;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public int mImsSubscriptionAppIndex;
    public IccSlotPortMapping mSlotPortMapping;
    public IccSlotStatus.MultipleEnabledProfilesMode mSupportedMepMode = IccSlotStatus.MultipleEnabledProfilesMode.NONE;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public PinState mUniversalPinState;

    /* loaded from: classes.dex */
    public enum PinState {
        PINSTATE_UNKNOWN,
        PINSTATE_ENABLED_NOT_VERIFIED,
        PINSTATE_ENABLED_VERIFIED,
        PINSTATE_DISABLED,
        PINSTATE_ENABLED_BLOCKED,
        PINSTATE_ENABLED_PERM_BLOCKED
    }

    /* loaded from: classes.dex */
    public enum CardState {
        CARDSTATE_ABSENT,
        CARDSTATE_PRESENT,
        CARDSTATE_ERROR,
        CARDSTATE_RESTRICTED;

        @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
        public boolean isCardPresent() {
            return this == CARDSTATE_PRESENT || this == CARDSTATE_RESTRICTED;
        }
    }

    public void setMultipleEnabledProfilesMode(int i) {
        if (i == 0) {
            this.mSupportedMepMode = IccSlotStatus.MultipleEnabledProfilesMode.NONE;
        } else if (i == 1) {
            this.mSupportedMepMode = IccSlotStatus.MultipleEnabledProfilesMode.MEP_A1;
        } else if (i == 2) {
            this.mSupportedMepMode = IccSlotStatus.MultipleEnabledProfilesMode.MEP_A2;
        } else if (i == 3) {
            this.mSupportedMepMode = IccSlotStatus.MultipleEnabledProfilesMode.MEP_B;
        } else {
            throw new RuntimeException("Unrecognized RIL_MultipleEnabledProfilesMode: " + i);
        }
    }

    public void setCardState(int i) {
        if (i == 0) {
            this.mCardState = CardState.CARDSTATE_ABSENT;
        } else if (i == 1) {
            this.mCardState = CardState.CARDSTATE_PRESENT;
        } else if (i == 2) {
            this.mCardState = CardState.CARDSTATE_ERROR;
        } else if (i == 3) {
            this.mCardState = CardState.CARDSTATE_RESTRICTED;
        } else {
            throw new RuntimeException("Unrecognized RIL_CardState: " + i);
        }
    }

    public void setUniversalPinState(int i) {
        if (i == 0) {
            this.mUniversalPinState = PinState.PINSTATE_UNKNOWN;
        } else if (i == 1) {
            this.mUniversalPinState = PinState.PINSTATE_ENABLED_NOT_VERIFIED;
        } else if (i == 2) {
            this.mUniversalPinState = PinState.PINSTATE_ENABLED_VERIFIED;
        } else if (i == 3) {
            this.mUniversalPinState = PinState.PINSTATE_DISABLED;
        } else if (i == 4) {
            this.mUniversalPinState = PinState.PINSTATE_ENABLED_BLOCKED;
        } else if (i == 5) {
            this.mUniversalPinState = PinState.PINSTATE_ENABLED_PERM_BLOCKED;
        } else {
            throw new RuntimeException("Unrecognized RIL_PinState: " + i);
        }
    }

    public String toString() {
        int i;
        int i2;
        int i3;
        StringBuilder sb = new StringBuilder();
        sb.append("IccCardState {");
        sb.append(this.mCardState);
        sb.append(",");
        sb.append(this.mUniversalPinState);
        if (this.mApplications != null) {
            sb.append(",num_apps=");
            sb.append(this.mApplications.length);
        } else {
            sb.append(",mApplications=null");
        }
        sb.append(",gsm_id=");
        sb.append(this.mGsmUmtsSubscriptionAppIndex);
        IccCardApplicationStatus[] iccCardApplicationStatusArr = this.mApplications;
        if (iccCardApplicationStatusArr != null && (i3 = this.mGsmUmtsSubscriptionAppIndex) >= 0 && i3 < iccCardApplicationStatusArr.length) {
            IccCardApplicationStatus iccCardApplicationStatus = iccCardApplicationStatusArr[i3];
            if (iccCardApplicationStatus == null) {
                iccCardApplicationStatus = "null";
            }
            sb.append(iccCardApplicationStatus);
        }
        sb.append(",cdma_id=");
        sb.append(this.mCdmaSubscriptionAppIndex);
        IccCardApplicationStatus[] iccCardApplicationStatusArr2 = this.mApplications;
        if (iccCardApplicationStatusArr2 != null && (i2 = this.mCdmaSubscriptionAppIndex) >= 0 && i2 < iccCardApplicationStatusArr2.length) {
            IccCardApplicationStatus iccCardApplicationStatus2 = iccCardApplicationStatusArr2[i2];
            if (iccCardApplicationStatus2 == null) {
                iccCardApplicationStatus2 = "null";
            }
            sb.append(iccCardApplicationStatus2);
        }
        sb.append(",ims_id=");
        sb.append(this.mImsSubscriptionAppIndex);
        IccCardApplicationStatus[] iccCardApplicationStatusArr3 = this.mApplications;
        if (iccCardApplicationStatusArr3 != null && (i = this.mImsSubscriptionAppIndex) >= 0 && i < iccCardApplicationStatusArr3.length) {
            IccCardApplicationStatus iccCardApplicationStatus3 = iccCardApplicationStatusArr3[i];
            sb.append(iccCardApplicationStatus3 != null ? iccCardApplicationStatus3 : "null");
        }
        sb.append(",atr=");
        sb.append(this.atr);
        sb.append(",iccid=");
        sb.append(SubscriptionInfo.givePrintableIccid(this.iccid));
        sb.append(",eid=");
        sb.append(Rlog.pii(TelephonyUtils.IS_DEBUGGABLE, this.eid));
        sb.append(",SupportedMepMode=");
        sb.append(this.mSupportedMepMode);
        sb.append(",SlotPortMapping=");
        sb.append(this.mSlotPortMapping);
        sb.append("}");
        return sb.toString();
    }
}
