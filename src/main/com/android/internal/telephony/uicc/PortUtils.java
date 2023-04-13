package com.android.internal.telephony.uicc;

import com.android.internal.telephony.uicc.IccCardStatus;
import com.android.internal.telephony.uicc.IccSlotStatus;
/* loaded from: classes.dex */
public class PortUtils {
    public static int convertToHalPortIndex(IccSlotStatus.MultipleEnabledProfilesMode multipleEnabledProfilesMode, int i) {
        return multipleEnabledProfilesMode.isMepAMode() ? i + 1 : i;
    }

    public static int convertToHalPortIndex(int i, int i2) {
        return convertToHalPortIndex(UiccController.getInstance().getSupportedMepMode(i), i2);
    }

    public static int convertFromHalPortIndex(int i, int i2, IccCardStatus.CardState cardState, IccSlotStatus.MultipleEnabledProfilesMode multipleEnabledProfilesMode) {
        if (!cardState.isCardPresent()) {
            multipleEnabledProfilesMode = UiccController.getInstance().getSupportedMepMode(i);
        }
        return multipleEnabledProfilesMode.isMepAMode() ? i2 - 1 : i2;
    }
}
