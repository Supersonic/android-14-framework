package com.android.internal.telephony;

import android.hardware.radio.RadioResponseInfo;
import android.hardware.radio.voice.IRadioVoiceResponse;
import android.hardware.radio.voice.LastCallFailCauseInfo;
import java.util.ArrayList;
import java.util.Collections;
/* loaded from: classes.dex */
public class VoiceResponse extends IRadioVoiceResponse.Stub {
    private final RIL mRil;

    public String getInterfaceHash() {
        return "notfrozen";
    }

    public int getInterfaceVersion() {
        return 2;
    }

    public VoiceResponse(RIL ril) {
        this.mRil = ril;
    }

    public void acknowledgeRequest(int i) {
        this.mRil.processRequestAck(i);
    }

    public void acceptCallResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(6, this.mRil, radioResponseInfo);
    }

    public void cancelPendingUssdResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(6, this.mRil, radioResponseInfo);
    }

    public void conferenceResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(6, this.mRil, radioResponseInfo);
    }

    public void dialResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(6, this.mRil, radioResponseInfo);
    }

    public void emergencyDialResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(6, this.mRil, radioResponseInfo);
    }

    public void exitEmergencyCallbackModeResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(6, this.mRil, radioResponseInfo);
    }

    public void explicitCallTransferResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(6, this.mRil, radioResponseInfo);
    }

    public void getCallForwardStatusResponse(RadioResponseInfo radioResponseInfo, android.hardware.radio.voice.CallForwardInfo[] callForwardInfoArr) {
        RILRequest processResponse = this.mRil.processResponse(6, radioResponseInfo);
        if (processResponse != null) {
            CallForwardInfo[] callForwardInfoArr2 = new CallForwardInfo[callForwardInfoArr.length];
            for (int i = 0; i < callForwardInfoArr.length; i++) {
                CallForwardInfo callForwardInfo = new CallForwardInfo();
                callForwardInfoArr2[i] = callForwardInfo;
                android.hardware.radio.voice.CallForwardInfo callForwardInfo2 = callForwardInfoArr[i];
                callForwardInfo.status = callForwardInfo2.status;
                callForwardInfo.reason = callForwardInfo2.reason;
                callForwardInfo.serviceClass = callForwardInfo2.serviceClass;
                callForwardInfo.toa = callForwardInfo2.toa;
                callForwardInfo.number = callForwardInfo2.number;
                callForwardInfo.timeSeconds = callForwardInfo2.timeSeconds;
            }
            if (radioResponseInfo.error == 0) {
                RadioResponse.sendMessageResponse(processResponse.mResult, callForwardInfoArr2);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, callForwardInfoArr2);
        }
    }

    public void getCallWaitingResponse(RadioResponseInfo radioResponseInfo, boolean z, int i) {
        RadioResponse.responseInts(6, this.mRil, radioResponseInfo, z ? 1 : 0, i);
    }

    public void getClipResponse(RadioResponseInfo radioResponseInfo, int i) {
        RadioResponse.responseInts(6, this.mRil, radioResponseInfo, i);
    }

    public void getClirResponse(RadioResponseInfo radioResponseInfo, int i, int i2) {
        RadioResponse.responseInts(6, this.mRil, radioResponseInfo, i, i2);
    }

    public void getCurrentCallsResponse(RadioResponseInfo radioResponseInfo, android.hardware.radio.voice.Call[] callArr) {
        RILRequest processResponse = this.mRil.processResponse(6, radioResponseInfo);
        if (processResponse != null) {
            int length = callArr.length;
            ArrayList arrayList = new ArrayList(length);
            for (android.hardware.radio.voice.Call call : callArr) {
                DriverCall convertToDriverCall = RILUtils.convertToDriverCall(call);
                arrayList.add(convertToDriverCall);
                if (convertToDriverCall.isVoicePrivacy) {
                    this.mRil.mVoicePrivacyOnRegistrants.notifyRegistrants();
                    this.mRil.riljLog("InCall VoicePrivacy is enabled");
                } else {
                    this.mRil.mVoicePrivacyOffRegistrants.notifyRegistrants();
                    this.mRil.riljLog("InCall VoicePrivacy is disabled");
                }
            }
            Collections.sort(arrayList);
            if (length == 0 && this.mRil.mTestingEmergencyCall.getAndSet(false)) {
                RIL ril = this.mRil;
                if (ril.mEmergencyCallbackModeRegistrant != null) {
                    ril.riljLog("responseCurrentCalls: call ended, testing emergency call, notify ECM Registrants");
                    this.mRil.mEmergencyCallbackModeRegistrant.notifyRegistrant();
                }
            }
            if (radioResponseInfo.error == 0) {
                RadioResponse.sendMessageResponse(processResponse.mResult, arrayList);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, arrayList);
        }
    }

    public void getLastCallFailCauseResponse(RadioResponseInfo radioResponseInfo, LastCallFailCauseInfo lastCallFailCauseInfo) {
        RILRequest processResponse = this.mRil.processResponse(6, radioResponseInfo);
        if (processResponse != null) {
            LastCallFailCause lastCallFailCause = new LastCallFailCause();
            lastCallFailCause.causeCode = lastCallFailCauseInfo.causeCode;
            lastCallFailCause.vendorCause = lastCallFailCauseInfo.vendorCause;
            if (radioResponseInfo.error == 0) {
                RadioResponse.sendMessageResponse(processResponse.mResult, lastCallFailCause);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, lastCallFailCause);
        }
    }

    public void getMuteResponse(RadioResponseInfo radioResponseInfo, boolean z) {
        RadioResponse.responseInts(6, this.mRil, radioResponseInfo, z ? 1 : 0);
    }

    public void getPreferredVoicePrivacyResponse(RadioResponseInfo radioResponseInfo, boolean z) {
        RadioResponse.responseInts(6, this.mRil, radioResponseInfo, z ? 1 : 0);
    }

    public void getTtyModeResponse(RadioResponseInfo radioResponseInfo, int i) {
        RadioResponse.responseInts(6, this.mRil, radioResponseInfo, i);
    }

    public void handleStkCallSetupRequestFromSimResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(6, this.mRil, radioResponseInfo);
    }

    public void hangupConnectionResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(6, this.mRil, radioResponseInfo);
    }

    public void hangupForegroundResumeBackgroundResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(6, this.mRil, radioResponseInfo);
    }

    public void hangupWaitingOrBackgroundResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(6, this.mRil, radioResponseInfo);
    }

    public void isVoNrEnabledResponse(RadioResponseInfo radioResponseInfo, boolean z) {
        RILRequest processResponse = this.mRil.processResponse(6, radioResponseInfo);
        if (processResponse != null) {
            if (radioResponseInfo.error == 0) {
                RadioResponse.sendMessageResponse(processResponse.mResult, Boolean.valueOf(z));
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, Boolean.valueOf(z));
        }
    }

    public void rejectCallResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(6, this.mRil, radioResponseInfo);
    }

    public void sendBurstDtmfResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(6, this.mRil, radioResponseInfo);
    }

    public void sendCdmaFeatureCodeResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(6, this.mRil, radioResponseInfo);
    }

    public void sendDtmfResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(6, this.mRil, radioResponseInfo);
    }

    public void sendUssdResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(6, this.mRil, radioResponseInfo);
    }

    public void separateConnectionResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(6, this.mRil, radioResponseInfo);
    }

    public void setCallForwardResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(6, this.mRil, radioResponseInfo);
    }

    public void setCallWaitingResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(6, this.mRil, radioResponseInfo);
    }

    public void setClirResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(6, this.mRil, radioResponseInfo);
    }

    public void setMuteResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(6, this.mRil, radioResponseInfo);
    }

    public void setPreferredVoicePrivacyResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(6, this.mRil, radioResponseInfo);
    }

    public void setTtyModeResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(6, this.mRil, radioResponseInfo);
    }

    public void setVoNrEnabledResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(6, this.mRil, radioResponseInfo);
    }

    public void startDtmfResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(6, this.mRil, radioResponseInfo);
    }

    public void stopDtmfResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(6, this.mRil, radioResponseInfo);
    }

    public void switchWaitingOrHoldingAndActiveResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(6, this.mRil, radioResponseInfo);
    }
}
