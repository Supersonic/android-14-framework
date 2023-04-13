package com.android.internal.telephony.uicc;

import android.text.TextUtils;
import com.android.internal.telephony.uicc.IccCardStatus;
import com.android.internal.telephony.util.TelephonyUtils;
import com.android.telephony.Rlog;
import java.util.Arrays;
/* loaded from: classes.dex */
public class IccSlotStatus {
    public static final int STATE_ACTIVE = 1;
    public String atr;
    public IccCardStatus.CardState cardState;
    public String eid;
    public IccSimPortInfo[] mSimPortInfos;
    public MultipleEnabledProfilesMode mSupportedMepMode = MultipleEnabledProfilesMode.NONE;

    /* loaded from: classes.dex */
    public enum MultipleEnabledProfilesMode {
        NONE,
        MEP_A1,
        MEP_A2,
        MEP_B;

        public boolean isMepAMode() {
            return this == MEP_A1 || this == MEP_A2;
        }

        public boolean isMepA1Mode() {
            return this == MEP_A1;
        }

        public boolean isMepMode() {
            return this != NONE;
        }
    }

    public void setCardState(int i) {
        if (i == 0) {
            this.cardState = IccCardStatus.CardState.CARDSTATE_ABSENT;
        } else if (i == 1) {
            this.cardState = IccCardStatus.CardState.CARDSTATE_PRESENT;
        } else if (i == 2) {
            this.cardState = IccCardStatus.CardState.CARDSTATE_ERROR;
        } else if (i == 3) {
            this.cardState = IccCardStatus.CardState.CARDSTATE_RESTRICTED;
        } else {
            throw new RuntimeException("Unrecognized RIL_CardState: " + i);
        }
    }

    public void setMultipleEnabledProfilesMode(int i) {
        if (i == 0) {
            this.mSupportedMepMode = MultipleEnabledProfilesMode.NONE;
        } else if (i == 1) {
            this.mSupportedMepMode = MultipleEnabledProfilesMode.MEP_A1;
        } else if (i == 2) {
            this.mSupportedMepMode = MultipleEnabledProfilesMode.MEP_A2;
        } else if (i == 3) {
            this.mSupportedMepMode = MultipleEnabledProfilesMode.MEP_B;
        } else {
            throw new RuntimeException("Unrecognized RIL_MultipleEnabledProfilesMode: " + i);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("IccSlotStatus {");
        sb.append(this.cardState);
        sb.append(",");
        sb.append("atr=");
        sb.append(this.atr);
        sb.append(",");
        sb.append("eid=");
        sb.append(Rlog.pii(TelephonyUtils.IS_DEBUGGABLE, this.eid));
        sb.append(",");
        if (this.mSimPortInfos != null) {
            sb.append("num_ports=");
            sb.append(this.mSimPortInfos.length);
            for (int i = 0; i < this.mSimPortInfos.length; i++) {
                sb.append(", IccSimPortInfo-" + i + this.mSimPortInfos[i]);
            }
        } else {
            sb.append("num_ports=null");
        }
        sb.append(", SupportedMepMode=" + this.mSupportedMepMode);
        sb.append("}");
        return sb.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        IccSlotStatus iccSlotStatus = (IccSlotStatus) obj;
        return this.cardState == iccSlotStatus.cardState && TextUtils.equals(this.atr, iccSlotStatus.atr) && TextUtils.equals(this.eid, iccSlotStatus.eid) && Arrays.equals(this.mSimPortInfos, iccSlotStatus.mSimPortInfos);
    }
}
