package com.android.internal.telephony.metrics;

import com.android.internal.telephony.uicc.IccCardStatus;
import com.android.internal.telephony.uicc.UiccCard;
import com.android.internal.telephony.uicc.UiccController;
import com.android.internal.telephony.uicc.UiccPort;
import com.android.internal.telephony.uicc.UiccSlot;
import com.android.telephony.Rlog;
/* loaded from: classes.dex */
public class SimSlotState {
    private static final String TAG = "SimSlotState";
    public final int numActiveEsims;
    public final int numActiveSims;
    public final int numActiveSlots;

    public static SimSlotState getCurrentState() {
        UiccPort[] uiccPortList;
        UiccController uiccController = UiccController.getInstance();
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        for (int i4 = 0; i4 < uiccController.getUiccSlots().length; i4++) {
            UiccSlot uiccSlot = uiccController.getUiccSlot(i4);
            if (uiccSlot != null && uiccSlot.isActive()) {
                i++;
                if (uiccSlot.getCardState() == IccCardStatus.CardState.CARDSTATE_PRESENT) {
                    if (uiccSlot.isEuicc()) {
                        UiccCard uiccCard = uiccSlot.getUiccCard();
                        if (uiccCard != null) {
                            for (UiccPort uiccPort : uiccCard.getUiccPortList()) {
                                if (uiccPort != null && uiccPort.getNumApplications() > 0) {
                                    i2++;
                                    i3++;
                                }
                            }
                        }
                    } else {
                        i2++;
                    }
                }
            }
        }
        return new SimSlotState(i, i2, i3);
    }

    private SimSlotState(int i, int i2, int i3) {
        this.numActiveSlots = i;
        this.numActiveSims = i2;
        this.numActiveEsims = i3;
    }

    public static boolean isEsim(int i) {
        UiccSlot uiccSlotForPhone = UiccController.getInstance().getUiccSlotForPhone(i);
        if (uiccSlotForPhone != null) {
            return uiccSlotForPhone.isEuicc();
        }
        String str = TAG;
        Rlog.e(str, "isEsim: slot=null for phone " + i);
        return false;
    }

    public static boolean isMultiSim() {
        return getCurrentState().numActiveSims > 1;
    }
}
