package com.android.internal.telephony;

import android.hardware.radio.satellite.IRadioSatelliteIndication;
import android.hardware.radio.satellite.PointingInfo;
import android.os.AsyncResult;
import android.telephony.satellite.SatelliteDatagram;
import android.util.Pair;
/* loaded from: classes.dex */
public class SatelliteIndication extends IRadioSatelliteIndication.Stub {
    private final RIL mRil;

    public String getInterfaceHash() {
        return "notfrozen";
    }

    public int getInterfaceVersion() {
        return 1;
    }

    public SatelliteIndication(RIL ril) {
        this.mRil = ril;
    }

    public void onPendingMessageCount(int i, int i2) {
        this.mRil.processIndication(8, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLog(1056);
        }
        RegistrantList registrantList = this.mRil.mPendingSatelliteMessageCountRegistrants;
        if (registrantList != null) {
            registrantList.notifyRegistrants(new AsyncResult((Object) null, Integer.valueOf(i2), (Throwable) null));
        }
    }

    public void onNewMessages(int i, String[] strArr) {
        this.mRil.processIndication(8, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLog(1057);
        }
        if (this.mRil.mNewSatelliteMessagesRegistrants != null) {
            for (int i2 = 0; i2 < strArr.length; i2++) {
                this.mRil.mNewSatelliteMessagesRegistrants.notifyRegistrants(new AsyncResult((Object) null, new Pair(new SatelliteDatagram(strArr[i2].getBytes()), Integer.valueOf((strArr.length - i2) - 1)), (Throwable) null));
            }
        }
    }

    public void onMessagesTransferComplete(int i, boolean z) {
        this.mRil.processIndication(8, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLog(1058);
        }
        RegistrantList registrantList = this.mRil.mSatelliteMessagesTransferCompleteRegistrants;
        if (registrantList != null) {
            registrantList.notifyRegistrants(new AsyncResult((Object) null, Boolean.valueOf(z), (Throwable) null));
        }
    }

    public void onSatellitePointingInfoChanged(int i, PointingInfo pointingInfo) {
        this.mRil.processIndication(8, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLog(1059);
        }
        RegistrantList registrantList = this.mRil.mSatellitePointingInfoChangedRegistrants;
        if (registrantList != null) {
            registrantList.notifyRegistrants(new AsyncResult((Object) null, RILUtils.convertHalSatellitePointingInfo(pointingInfo), (Throwable) null));
        }
    }

    public void onSatelliteModeChanged(int i, int i2) {
        this.mRil.processIndication(8, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLog(1060);
        }
        RegistrantList registrantList = this.mRil.mSatelliteModeChangedRegistrants;
        if (registrantList != null) {
            registrantList.notifyRegistrants(new AsyncResult((Object) null, Integer.valueOf(i2), (Throwable) null));
        }
    }

    public void onSatelliteRadioTechnologyChanged(int i, int i2) {
        this.mRil.processIndication(8, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLog(1061);
        }
        RegistrantList registrantList = this.mRil.mSatelliteRadioTechnologyChangedRegistrants;
        if (registrantList != null) {
            registrantList.notifyRegistrants(new AsyncResult((Object) null, Integer.valueOf(i2), (Throwable) null));
        }
    }

    public void onProvisionStateChanged(int i, boolean z, int[] iArr) {
        this.mRil.processIndication(8, i);
        if (this.mRil.isLogOrTrace()) {
            this.mRil.unsljLog(1062);
        }
        RegistrantList registrantList = this.mRil.mSatelliteProvisionStateChangedRegistrants;
        if (registrantList != null) {
            registrantList.notifyRegistrants(new AsyncResult(Boolean.valueOf(z), (Object) null, (Throwable) null));
        }
    }
}
