package com.android.internal.telephony;

import android.hardware.radio.config.IRadioConfigIndication;
import android.hardware.radio.config.SimSlotStatus;
import android.os.AsyncResult;
import android.os.Trace;
import com.android.internal.telephony.uicc.IccSlotStatus;
import com.android.telephony.Rlog;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class RadioConfigIndicationAidl extends IRadioConfigIndication.Stub {
    private final RadioConfig mRadioConfig;

    public String getInterfaceHash() {
        return "notfrozen";
    }

    public int getInterfaceVersion() {
        return 2;
    }

    public RadioConfigIndicationAidl(RadioConfig radioConfig) {
        this.mRadioConfig = radioConfig;
    }

    public void simSlotsStatusChanged(int i, SimSlotStatus[] simSlotStatusArr) {
        ArrayList<IccSlotStatus> convertHalSlotStatus = RILUtils.convertHalSlotStatus(simSlotStatusArr);
        logd("UNSOL_SIM_SLOT_STATUS_CHANGED " + convertHalSlotStatus.toString());
        Registrant registrant = this.mRadioConfig.mSimSlotStatusRegistrant;
        if (registrant != null) {
            registrant.notifyRegistrant(new AsyncResult((Object) null, convertHalSlotStatus, (Throwable) null));
        }
    }

    private static void logd(String str) {
        Rlog.d("RadioConfigIndicationAidl", "[UNSL]< " + str);
        Trace.instantForTrack(2097152L, "RIL", str);
    }
}
