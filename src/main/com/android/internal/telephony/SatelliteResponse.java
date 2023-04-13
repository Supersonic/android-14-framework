package com.android.internal.telephony;

import android.hardware.radio.RadioResponseInfo;
import android.hardware.radio.satellite.IRadioSatelliteResponse;
import android.hardware.radio.satellite.SatelliteCapabilities;
/* loaded from: classes.dex */
public class SatelliteResponse extends IRadioSatelliteResponse.Stub {
    private final RIL mRil;

    public String getInterfaceHash() {
        return "notfrozen";
    }

    public int getInterfaceVersion() {
        return 1;
    }

    public SatelliteResponse(RIL ril) {
        this.mRil = ril;
    }

    public void acknowledgeRequest(int i) {
        this.mRil.processRequestAck(i);
    }

    public void getCapabilitiesResponse(RadioResponseInfo radioResponseInfo, SatelliteCapabilities satelliteCapabilities) {
        RILRequest processResponse = this.mRil.processResponse(8, radioResponseInfo);
        if (processResponse != null) {
            android.telephony.satellite.SatelliteCapabilities convertHalSatelliteCapabilities = RILUtils.convertHalSatelliteCapabilities(satelliteCapabilities);
            if (radioResponseInfo.error == 0) {
                RadioResponse.sendMessageResponse(processResponse.mResult, convertHalSatelliteCapabilities);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, convertHalSatelliteCapabilities);
        }
    }

    public void setPowerResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(8, this.mRil, radioResponseInfo);
    }

    public void getPowerStateResponse(RadioResponseInfo radioResponseInfo, boolean z) {
        RadioResponse.responseInts(8, this.mRil, radioResponseInfo, z ? 1 : 0);
    }

    public void provisionServiceResponse(RadioResponseInfo radioResponseInfo, boolean z) {
        RadioResponse.responseInts(8, this.mRil, radioResponseInfo, z ? 1 : 0);
    }

    public void addAllowedSatelliteContactsResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(8, this.mRil, radioResponseInfo);
    }

    public void removeAllowedSatelliteContactsResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(8, this.mRil, radioResponseInfo);
    }

    public void sendMessagesResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(8, this.mRil, radioResponseInfo);
    }

    public void getPendingMessagesResponse(RadioResponseInfo radioResponseInfo, String[] strArr) {
        RILRequest processResponse = this.mRil.processResponse(8, radioResponseInfo);
        if (processResponse != null) {
            if (radioResponseInfo.error == 0) {
                RadioResponse.sendMessageResponse(processResponse.mResult, strArr);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, strArr);
        }
    }

    public void getSatelliteModeResponse(RadioResponseInfo radioResponseInfo, int i, int i2) {
        RILRequest processResponse = this.mRil.processResponse(8, radioResponseInfo);
        if (processResponse != null) {
            int[] iArr = {i, i2};
            if (radioResponseInfo.error == 0) {
                RadioResponse.sendMessageResponse(processResponse.mResult, iArr);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, iArr);
        }
    }

    public void setIndicationFilterResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(8, this.mRil, radioResponseInfo);
    }

    public void startSendingSatellitePointingInfoResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(8, this.mRil, radioResponseInfo);
    }

    public void stopSendingSatellitePointingInfoResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(8, this.mRil, radioResponseInfo);
    }

    public void getMaxCharactersPerTextMessageResponse(RadioResponseInfo radioResponseInfo, int i) {
        RadioResponse.responseInts(8, this.mRil, radioResponseInfo, i);
    }

    public void getTimeForNextSatelliteVisibilityResponse(RadioResponseInfo radioResponseInfo, int i) {
        RadioResponse.responseInts(8, this.mRil, radioResponseInfo, i);
    }
}
