package com.android.internal.telephony;

import android.hardware.radio.RadioResponseInfo;
import android.hardware.radio.ims.ConnectionFailureInfo;
import android.hardware.radio.ims.IRadioImsResponse;
/* loaded from: classes.dex */
public class ImsResponse extends IRadioImsResponse.Stub {
    private final RIL mRil;

    public String getInterfaceHash() {
        return "notfrozen";
    }

    public int getInterfaceVersion() {
        return 1;
    }

    public ImsResponse(RIL ril) {
        this.mRil = ril;
    }

    public void setSrvccCallInfoResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(7, this.mRil, radioResponseInfo);
    }

    public void updateImsRegistrationInfoResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(7, this.mRil, radioResponseInfo);
    }

    public void startImsTrafficResponse(RadioResponseInfo radioResponseInfo, ConnectionFailureInfo connectionFailureInfo) {
        RILRequest processResponse = this.mRil.processResponse(7, radioResponseInfo);
        if (processResponse != null) {
            Object[] objArr = {new Integer(-1), null};
            if (radioResponseInfo.error == 0) {
                if (connectionFailureInfo != null) {
                    objArr[1] = new android.telephony.ims.feature.ConnectionFailureInfo(RILUtils.convertHalConnectionFailureReason(connectionFailureInfo.failureReason), connectionFailureInfo.causeCode, connectionFailureInfo.waitTimeMillis);
                }
                RadioResponse.sendMessageResponse(processResponse.mResult, objArr);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, objArr);
        }
    }

    public void stopImsTrafficResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(7, this.mRil, radioResponseInfo);
    }

    public void triggerEpsFallbackResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(7, this.mRil, radioResponseInfo);
    }

    public void sendAnbrQueryResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(7, this.mRil, radioResponseInfo);
    }

    public void updateImsCallStatusResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(7, this.mRil, radioResponseInfo);
    }
}
