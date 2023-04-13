package com.android.internal.telephony;

import android.hardware.radio.modem.IRadioModemIndication;
import android.os.AsyncResult;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class ModemIndication extends IRadioModemIndication.Stub {
    private final RIL mRil;

    public String getInterfaceHash() {
        return "notfrozen";
    }

    public int getInterfaceVersion() {
        return 2;
    }

    public ModemIndication(RIL ril) {
        this.mRil = ril;
    }

    public void hardwareConfigChanged(int i, android.hardware.radio.modem.HardwareConfig[] hardwareConfigArr) {
        this.mRil.processIndication(3, i);
        ArrayList<HardwareConfig> convertHalHardwareConfigList = RILUtils.convertHalHardwareConfigList(hardwareConfigArr);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1040, convertHalHardwareConfigList);
        }
        this.mRil.mHardwareConfigChangeRegistrants.notifyRegistrants(new AsyncResult((Object) null, convertHalHardwareConfigList, (Throwable) null));
    }

    public void modemReset(int i, String str) {
        this.mRil.processIndication(3, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1047, str);
        }
        this.mRil.writeMetricsModemRestartEvent(str);
        this.mRil.mModemResetRegistrants.notifyRegistrants(new AsyncResult((Object) null, str, (Throwable) null));
    }

    public void radioCapabilityIndication(int i, android.hardware.radio.modem.RadioCapability radioCapability) {
        this.mRil.processIndication(3, i);
        RadioCapability convertHalRadioCapability = RILUtils.convertHalRadioCapability(radioCapability, this.mRil);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLogRet(1042, convertHalRadioCapability);
        }
        this.mRil.mPhoneRadioCapabilityChangedRegistrants.notifyRegistrants(new AsyncResult((Object) null, convertHalRadioCapability, (Throwable) null));
    }

    public void radioStateChanged(int i, int i2) {
        this.mRil.processIndication(3, i);
        int convertHalRadioState = RILUtils.convertHalRadioState(i2);
        if (this.mRil.isLogOrTrace()) {
            RIL ril = this.mRil;
            ril.unsljLogMore(1000, "radioStateChanged: " + convertHalRadioState);
        }
        this.mRil.setRadioState(convertHalRadioState, false);
    }

    public void rilConnected(int i) {
        this.mRil.processIndication(3, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLog(1034);
        }
        this.mRil.setRadioPower(false, null);
        RIL ril = this.mRil;
        ril.setCdmaSubscriptionSource(ril.mCdmaSubscription, null);
        this.mRil.notifyRegistrantsRilConnectionChanged(15);
    }
}
