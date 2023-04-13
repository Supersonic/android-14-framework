package com.android.internal.telephony;

import android.hardware.radio.ims.ConnectionFailureInfo;
import android.hardware.radio.ims.IRadioImsIndication;
import android.os.AsyncResult;
/* loaded from: classes.dex */
public class ImsIndication extends IRadioImsIndication.Stub {
    private final RIL mRil;

    public String getInterfaceHash() {
        return "notfrozen";
    }

    public int getInterfaceVersion() {
        return 1;
    }

    public ImsIndication(RIL ril) {
        this.mRil = ril;
    }

    public void onConnectionSetupFailure(int i, int i2, ConnectionFailureInfo connectionFailureInfo) {
        this.mRil.processIndication(7, i);
        Object[] objArr = {Integer.valueOf(i2), new android.telephony.ims.feature.ConnectionFailureInfo(RILUtils.convertHalConnectionFailureReason(connectionFailureInfo.failureReason), connectionFailureInfo.causeCode, connectionFailureInfo.waitTimeMillis)};
        this.mRil.unsljLogRet(1108, objArr);
        this.mRil.mConnectionSetupFailureRegistrants.notifyRegistrants(new AsyncResult((Object) null, objArr, (Throwable) null));
    }

    public void notifyAnbr(int i, int i2, int i3, int i4) {
        this.mRil.processIndication(7, i);
        int[] iArr = {i2, i3, i4};
        this.mRil.unsljLogRet(1109, iArr);
        this.mRil.mNotifyAnbrRegistrants.notifyRegistrants(new AsyncResult((Object) null, iArr, (Throwable) null));
    }

    public void triggerImsDeregistration(int i, int i2) {
        this.mRil.processIndication(7, i);
        this.mRil.unsljLogRet(1107, Integer.valueOf(i2));
        this.mRil.mTriggerImsDeregistrationRegistrants.notifyRegistrants(new AsyncResult((Object) null, new int[]{RILUtils.convertHalDeregistrationReason(i2)}, (Throwable) null));
    }
}
