package com.android.internal.telephony;

import android.hardware.radio.RadioResponseInfo;
import android.hardware.radio.sim.CardStatus;
import android.hardware.radio.sim.CarrierRestrictions;
import android.hardware.radio.sim.IRadioSimResponse;
import android.hardware.radio.sim.IccIoResult;
import android.hardware.radio.sim.PhonebookCapacity;
import android.telephony.CarrierRestrictionRules;
import android.text.TextUtils;
import com.android.internal.telephony.uicc.AdnCapacity;
import com.android.internal.telephony.uicc.IccCardStatus;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class SimResponse extends IRadioSimResponse.Stub {
    private final RIL mRil;

    public String getInterfaceHash() {
        return "notfrozen";
    }

    public int getInterfaceVersion() {
        return 2;
    }

    public SimResponse(RIL ril) {
        this.mRil = ril;
    }

    private void responseIccIo(RadioResponseInfo radioResponseInfo, IccIoResult iccIoResult) {
        RILRequest processResponse = this.mRil.processResponse(5, radioResponseInfo);
        if (processResponse != null) {
            com.android.internal.telephony.uicc.IccIoResult iccIoResult2 = new com.android.internal.telephony.uicc.IccIoResult(iccIoResult.sw1, iccIoResult.sw2, iccIoResult.simResponse);
            if (radioResponseInfo.error == 0) {
                RadioResponse.sendMessageResponse(processResponse.mResult, iccIoResult2);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, iccIoResult2);
        }
    }

    public void acknowledgeRequest(int i) {
        this.mRil.processRequestAck(i);
    }

    public void areUiccApplicationsEnabledResponse(RadioResponseInfo radioResponseInfo, boolean z) {
        RILRequest processResponse = this.mRil.processResponse(5, radioResponseInfo);
        if (processResponse != null) {
            if (radioResponseInfo.error == 0) {
                RadioResponse.sendMessageResponse(processResponse.mResult, Boolean.valueOf(z));
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, Boolean.valueOf(z));
        }
    }

    public void changeIccPin2ForAppResponse(RadioResponseInfo radioResponseInfo, int i) {
        RadioResponse.responseInts(5, this.mRil, radioResponseInfo, i);
    }

    public void changeIccPinForAppResponse(RadioResponseInfo radioResponseInfo, int i) {
        RadioResponse.responseInts(5, this.mRil, radioResponseInfo, i);
    }

    public void enableUiccApplicationsResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(5, this.mRil, radioResponseInfo);
    }

    public void getAllowedCarriersResponse(RadioResponseInfo radioResponseInfo, CarrierRestrictions carrierRestrictions, int i) {
        RILRequest processResponse = this.mRil.processResponse(5, radioResponseInfo);
        if (processResponse == null) {
            return;
        }
        CarrierRestrictionRules build = CarrierRestrictionRules.newBuilder().setAllowedCarriers(RILUtils.convertHalCarrierList(carrierRestrictions.allowedCarriers)).setExcludedCarriers(RILUtils.convertHalCarrierList(carrierRestrictions.excludedCarriers)).setDefaultCarrierRestriction(1 ^ (carrierRestrictions.allowedCarriersPrioritized ? 1 : 0)).setMultiSimPolicy(i == 1 ? 1 : 0).setCarrierRestrictionStatus(carrierRestrictions.status).build();
        if (radioResponseInfo.error == 0) {
            RadioResponse.sendMessageResponse(processResponse.mResult, build);
        }
        this.mRil.processResponseDone(processResponse, radioResponseInfo, build);
    }

    public void getCdmaSubscriptionResponse(RadioResponseInfo radioResponseInfo, String str, String str2, String str3, String str4, String str5) {
        RadioResponse.responseStrings(5, this.mRil, radioResponseInfo, str, str2, str3, str4, str5);
    }

    public void getCdmaSubscriptionSourceResponse(RadioResponseInfo radioResponseInfo, int i) {
        RadioResponse.responseInts(5, this.mRil, radioResponseInfo, i);
    }

    public void getFacilityLockForAppResponse(RadioResponseInfo radioResponseInfo, int i) {
        RadioResponse.responseInts(5, this.mRil, radioResponseInfo, i);
    }

    public void getIccCardStatusResponse(RadioResponseInfo radioResponseInfo, CardStatus cardStatus) {
        RILRequest processResponse = this.mRil.processResponse(5, radioResponseInfo);
        if (processResponse != null) {
            IccCardStatus convertHalCardStatus = RILUtils.convertHalCardStatus(cardStatus);
            RIL ril = this.mRil;
            ril.riljLog("responseIccCardStatus: from AIDL: " + convertHalCardStatus);
            if (radioResponseInfo.error == 0) {
                RadioResponse.sendMessageResponse(processResponse.mResult, convertHalCardStatus);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, convertHalCardStatus);
        }
    }

    public void getImsiForAppResponse(RadioResponseInfo radioResponseInfo, String str) {
        RadioResponse.responseString(5, this.mRil, radioResponseInfo, str);
    }

    public void getSimPhonebookCapacityResponse(RadioResponseInfo radioResponseInfo, PhonebookCapacity phonebookCapacity) {
        AdnCapacity convertHalPhonebookCapacity = RILUtils.convertHalPhonebookCapacity(phonebookCapacity);
        RILRequest processResponse = this.mRil.processResponse(5, radioResponseInfo);
        if (processResponse != null) {
            if (radioResponseInfo.error == 0) {
                RadioResponse.sendMessageResponse(processResponse.mResult, convertHalPhonebookCapacity);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, convertHalPhonebookCapacity);
        }
    }

    public void getSimPhonebookRecordsResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(5, this.mRil, radioResponseInfo);
    }

    public void iccCloseLogicalChannelResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(5, this.mRil, radioResponseInfo);
    }

    public void iccCloseLogicalChannelWithSessionInfoResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(5, this.mRil, radioResponseInfo);
    }

    public void iccIoForAppResponse(RadioResponseInfo radioResponseInfo, IccIoResult iccIoResult) {
        responseIccIo(radioResponseInfo, iccIoResult);
    }

    public void iccOpenLogicalChannelResponse(RadioResponseInfo radioResponseInfo, int i, byte[] bArr) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(Integer.valueOf(i));
        for (byte b : bArr) {
            arrayList.add(Integer.valueOf(b));
        }
        RadioResponse.responseIntArrayList(5, this.mRil, radioResponseInfo, arrayList);
    }

    public void iccTransmitApduBasicChannelResponse(RadioResponseInfo radioResponseInfo, IccIoResult iccIoResult) {
        responseIccIo(radioResponseInfo, iccIoResult);
    }

    public void iccTransmitApduLogicalChannelResponse(RadioResponseInfo radioResponseInfo, IccIoResult iccIoResult) {
        responseIccIo(radioResponseInfo, iccIoResult);
    }

    public void reportStkServiceIsRunningResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(5, this.mRil, radioResponseInfo);
    }

    public void requestIccSimAuthenticationResponse(RadioResponseInfo radioResponseInfo, IccIoResult iccIoResult) {
        RILRequest processResponse = this.mRil.processResponse(5, radioResponseInfo);
        if (processResponse != null) {
            com.android.internal.telephony.uicc.IccIoResult iccIoResult2 = new com.android.internal.telephony.uicc.IccIoResult(iccIoResult.sw1, iccIoResult.sw2, TextUtils.isEmpty(iccIoResult.simResponse) ? null : iccIoResult.simResponse.getBytes());
            if (radioResponseInfo.error == 0) {
                RadioResponse.sendMessageResponse(processResponse.mResult, iccIoResult2);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, iccIoResult2);
        }
    }

    public void requestIsimAuthenticationResponse(RadioResponseInfo radioResponseInfo, String str) {
        throw new RuntimeException("Inexplicable response received for requestIsimAuthentication");
    }

    public void sendEnvelopeResponse(RadioResponseInfo radioResponseInfo, String str) {
        RadioResponse.responseString(5, this.mRil, radioResponseInfo, str);
    }

    public void sendEnvelopeWithStatusResponse(RadioResponseInfo radioResponseInfo, IccIoResult iccIoResult) {
        responseIccIo(radioResponseInfo, iccIoResult);
    }

    public void sendTerminalResponseToSimResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(5, this.mRil, radioResponseInfo);
    }

    public void setAllowedCarriersResponse(RadioResponseInfo radioResponseInfo) {
        int i;
        RILRequest processResponse = this.mRil.processResponse(5, radioResponseInfo);
        if (processResponse != null) {
            RIL ril = this.mRil;
            ril.riljLog("setAllowedCarriersResponse - error = " + radioResponseInfo.error);
            if (radioResponseInfo.error == 0) {
                i = 0;
                RadioResponse.sendMessageResponse(processResponse.mResult, 0);
            } else {
                i = 2;
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, Integer.valueOf(i));
        }
    }

    public void setCarrierInfoForImsiEncryptionResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(5, this.mRil, radioResponseInfo);
    }

    public void setCdmaSubscriptionSourceResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(5, this.mRil, radioResponseInfo);
    }

    public void setFacilityLockForAppResponse(RadioResponseInfo radioResponseInfo, int i) {
        RadioResponse.responseInts(5, this.mRil, radioResponseInfo, i);
    }

    public void setSimCardPowerResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(5, this.mRil, radioResponseInfo);
    }

    public void setUiccSubscriptionResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(5, this.mRil, radioResponseInfo);
    }

    public void supplyIccPin2ForAppResponse(RadioResponseInfo radioResponseInfo, int i) {
        RadioResponse.responseInts(5, this.mRil, radioResponseInfo, i);
    }

    public void supplyIccPinForAppResponse(RadioResponseInfo radioResponseInfo, int i) {
        RadioResponse.responseInts(5, this.mRil, radioResponseInfo, i);
    }

    public void supplyIccPuk2ForAppResponse(RadioResponseInfo radioResponseInfo, int i) {
        RadioResponse.responseInts(5, this.mRil, radioResponseInfo, i);
    }

    public void supplyIccPukForAppResponse(RadioResponseInfo radioResponseInfo, int i) {
        RadioResponse.responseInts(5, this.mRil, radioResponseInfo, i);
    }

    public void supplySimDepersonalizationResponse(RadioResponseInfo radioResponseInfo, int i, int i2) {
        RadioResponse.responseInts(5, this.mRil, radioResponseInfo, i, i2);
    }

    public void updateSimPhonebookRecordsResponse(RadioResponseInfo radioResponseInfo, int i) {
        RadioResponse.responseInts(5, this.mRil, radioResponseInfo, i);
    }
}
