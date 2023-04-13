package com.android.internal.telephony;

import android.hardware.radio.V1_0.ActivityStatsInfo;
import android.hardware.radio.V1_0.CardStatus;
import android.hardware.radio.V1_0.CarrierRestrictions;
import android.hardware.radio.V1_0.CdmaBroadcastSmsConfigInfo;
import android.hardware.radio.V1_0.CellInfo;
import android.hardware.radio.V1_0.DataRegStateResult;
import android.hardware.radio.V1_0.GsmBroadcastSmsConfigInfo;
import android.hardware.radio.V1_0.IccIoResult;
import android.hardware.radio.V1_0.LastCallFailCauseInfo;
import android.hardware.radio.V1_0.LceDataInfo;
import android.hardware.radio.V1_0.LceStatusInfo;
import android.hardware.radio.V1_0.NeighboringCell;
import android.hardware.radio.V1_0.OperatorInfo;
import android.hardware.radio.V1_0.RadioResponseInfo;
import android.hardware.radio.V1_0.SendSmsResult;
import android.hardware.radio.V1_0.SetupDataCallResult;
import android.hardware.radio.V1_0.SignalStrength;
import android.hardware.radio.V1_0.VoiceRegStateResult;
import android.hardware.radio.V1_1.KeepaliveStatus;
import android.hardware.radio.V1_4.CarrierRestrictionsWithPriority;
import android.hardware.radio.V1_5.BarringInfo;
import android.hardware.radio.V1_5.CellIdentity;
import android.hardware.radio.V1_5.RadioAccessSpecifier;
import android.hardware.radio.V1_5.RegStateResult;
import android.hardware.radio.V1_6.IRadioResponse;
import android.hardware.radio.V1_6.PhonebookCapacity;
import android.hardware.radio.V1_6.SlicingConfig;
import android.os.AsyncResult;
import android.os.Message;
import android.os.SystemClock;
import android.telephony.AnomalyReporter;
import android.telephony.CarrierRestrictionRules;
import android.telephony.LinkCapacityEstimate;
import android.telephony.ModemActivityInfo;
import android.telephony.NeighboringCellInfo;
import android.telephony.NetworkScanRequest;
import android.telephony.RadioAccessFamily;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.data.DataCallResponse;
import android.telephony.data.NetworkSlicingConfig;
import android.text.TextUtils;
import com.android.internal.telephony.gsm.SmsBroadcastConfigInfo;
import com.android.internal.telephony.uicc.AdnCapacity;
import com.android.internal.telephony.uicc.IccCardStatus;
import com.android.internal.telephony.uicc.IccSlotPortMapping;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
/* loaded from: classes.dex */
public class RadioResponse extends IRadioResponse.Stub {
    RIL mRil;

    public void sendOemRilRequestRawResponse(RadioResponseInfo radioResponseInfo, ArrayList<Byte> arrayList) {
    }

    public RadioResponse(RIL ril) {
        this.mRil = ril;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void sendMessageResponse(Message message, Object obj) {
        if (message != null) {
            AsyncResult.forMessage(message, obj, (Throwable) null);
            message.sendToTarget();
        }
    }

    public void acknowledgeRequest(int i) {
        this.mRil.processRequestAck(i);
    }

    public void getIccCardStatusResponse(RadioResponseInfo radioResponseInfo, CardStatus cardStatus) {
        responseIccCardStatus(radioResponseInfo, cardStatus);
    }

    public void getIccCardStatusResponse_1_2(RadioResponseInfo radioResponseInfo, android.hardware.radio.V1_2.CardStatus cardStatus) {
        responseIccCardStatus_1_2(radioResponseInfo, cardStatus);
    }

    public void getIccCardStatusResponse_1_4(RadioResponseInfo radioResponseInfo, android.hardware.radio.V1_4.CardStatus cardStatus) {
        responseIccCardStatus_1_4(radioResponseInfo, cardStatus);
    }

    public void getIccCardStatusResponse_1_5(RadioResponseInfo radioResponseInfo, android.hardware.radio.V1_5.CardStatus cardStatus) {
        responseIccCardStatus_1_5(radioResponseInfo, cardStatus);
    }

    public void supplyIccPinForAppResponse(RadioResponseInfo radioResponseInfo, int i) {
        responseInts(radioResponseInfo, i);
    }

    public void supplyIccPukForAppResponse(RadioResponseInfo radioResponseInfo, int i) {
        responseInts(radioResponseInfo, i);
    }

    public void supplyIccPin2ForAppResponse(RadioResponseInfo radioResponseInfo, int i) {
        responseInts(radioResponseInfo, i);
    }

    public void supplyIccPuk2ForAppResponse(RadioResponseInfo radioResponseInfo, int i) {
        responseInts(radioResponseInfo, i);
    }

    public void changeIccPinForAppResponse(RadioResponseInfo radioResponseInfo, int i) {
        responseInts(radioResponseInfo, i);
    }

    public void changeIccPin2ForAppResponse(RadioResponseInfo radioResponseInfo, int i) {
        responseInts(radioResponseInfo, i);
    }

    public void supplyNetworkDepersonalizationResponse(RadioResponseInfo radioResponseInfo, int i) {
        responseInts(radioResponseInfo, i);
    }

    public void supplySimDepersonalizationResponse(RadioResponseInfo radioResponseInfo, int i, int i2) {
        responseInts(radioResponseInfo, i, i2);
    }

    public void getCurrentCallsResponse(RadioResponseInfo radioResponseInfo, ArrayList<android.hardware.radio.V1_0.Call> arrayList) {
        responseCurrentCalls(radioResponseInfo, arrayList);
    }

    public void getCurrentCallsResponse_1_2(RadioResponseInfo radioResponseInfo, ArrayList<android.hardware.radio.V1_2.Call> arrayList) {
        responseCurrentCalls_1_2(radioResponseInfo, arrayList);
    }

    public void getCurrentCallsResponse_1_6(android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo, ArrayList<android.hardware.radio.V1_6.Call> arrayList) {
        responseCurrentCalls_1_6(radioResponseInfo, arrayList);
    }

    public void dialResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void getIMSIForAppResponse(RadioResponseInfo radioResponseInfo, String str) {
        responseString(radioResponseInfo, str);
    }

    public void hangupConnectionResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void hangupWaitingOrBackgroundResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void hangupForegroundResumeBackgroundResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void switchWaitingOrHoldingAndActiveResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void conferenceResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void rejectCallResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void getLastCallFailCauseResponse(RadioResponseInfo radioResponseInfo, LastCallFailCauseInfo lastCallFailCauseInfo) {
        responseLastCallFailCauseInfo(radioResponseInfo, lastCallFailCauseInfo);
    }

    public void getSignalStrengthResponse(RadioResponseInfo radioResponseInfo, SignalStrength signalStrength) {
        responseSignalStrength(radioResponseInfo, signalStrength);
    }

    public void getSignalStrengthResponse_1_2(RadioResponseInfo radioResponseInfo, android.hardware.radio.V1_2.SignalStrength signalStrength) {
        responseSignalStrength_1_2(radioResponseInfo, signalStrength);
    }

    public void getSignalStrengthResponse_1_4(RadioResponseInfo radioResponseInfo, android.hardware.radio.V1_4.SignalStrength signalStrength) {
        responseSignalStrength_1_4(radioResponseInfo, signalStrength);
    }

    public void getSignalStrengthResponse_1_6(android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo, android.hardware.radio.V1_6.SignalStrength signalStrength) {
        responseSignalStrength_1_6(radioResponseInfo, signalStrength);
    }

    public void getVoiceRegistrationStateResponse(RadioResponseInfo radioResponseInfo, VoiceRegStateResult voiceRegStateResult) {
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse != null) {
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, voiceRegStateResult);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, voiceRegStateResult);
        }
    }

    public void getVoiceRegistrationStateResponse_1_2(RadioResponseInfo radioResponseInfo, android.hardware.radio.V1_2.VoiceRegStateResult voiceRegStateResult) {
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse != null) {
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, voiceRegStateResult);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, voiceRegStateResult);
        }
    }

    public void getVoiceRegistrationStateResponse_1_5(RadioResponseInfo radioResponseInfo, RegStateResult regStateResult) {
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse == null) {
            return;
        }
        int i = radioResponseInfo.error;
        if (i == 6) {
            final int request = processResponse.getRequest();
            final Message result = processResponse.getResult();
            this.mRil.mRilHandler.post(new Runnable() { // from class: com.android.internal.telephony.RadioResponse$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    RadioResponse.this.lambda$getVoiceRegistrationStateResponse_1_5$0(request, result);
                }
            });
            this.mRil.processResponseFallback(processResponse, radioResponseInfo, regStateResult);
            return;
        }
        if (i == 0) {
            sendMessageResponse(processResponse.mResult, regStateResult);
        }
        this.mRil.processResponseDone(processResponse, radioResponseInfo, regStateResult);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getVoiceRegistrationStateResponse_1_5$0(int i, Message message) {
        this.mRil.setCompatVersion(i, RIL.RADIO_HAL_VERSION_1_4);
        this.mRil.getVoiceRegistrationState(message);
    }

    public void getVoiceRegistrationStateResponse_1_6(android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo, android.hardware.radio.V1_6.RegStateResult regStateResult) {
        RILRequest processResponse_1_6 = this.mRil.processResponse_1_6(radioResponseInfo);
        if (processResponse_1_6 != null) {
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse_1_6.mResult, regStateResult);
            }
            this.mRil.processResponseDone_1_6(processResponse_1_6, radioResponseInfo, regStateResult);
        }
    }

    public void getDataRegistrationStateResponse(RadioResponseInfo radioResponseInfo, DataRegStateResult dataRegStateResult) {
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse != null) {
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, dataRegStateResult);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, dataRegStateResult);
        }
    }

    public void getDataRegistrationStateResponse_1_2(RadioResponseInfo radioResponseInfo, android.hardware.radio.V1_2.DataRegStateResult dataRegStateResult) {
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse != null) {
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, dataRegStateResult);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, dataRegStateResult);
        }
    }

    public void getDataRegistrationStateResponse_1_4(RadioResponseInfo radioResponseInfo, android.hardware.radio.V1_4.DataRegStateResult dataRegStateResult) {
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse != null) {
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, dataRegStateResult);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, dataRegStateResult);
        }
    }

    public void getDataRegistrationStateResponse_1_5(RadioResponseInfo radioResponseInfo, RegStateResult regStateResult) {
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse == null) {
            return;
        }
        int i = radioResponseInfo.error;
        if (i == 6) {
            final int request = processResponse.getRequest();
            final Message result = processResponse.getResult();
            this.mRil.mRilHandler.post(new Runnable() { // from class: com.android.internal.telephony.RadioResponse$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    RadioResponse.this.lambda$getDataRegistrationStateResponse_1_5$1(request, result);
                }
            });
            this.mRil.processResponseFallback(processResponse, radioResponseInfo, regStateResult);
            return;
        }
        if (i == 0) {
            sendMessageResponse(processResponse.mResult, regStateResult);
        }
        this.mRil.processResponseDone(processResponse, radioResponseInfo, regStateResult);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getDataRegistrationStateResponse_1_5$1(int i, Message message) {
        this.mRil.setCompatVersion(i, RIL.RADIO_HAL_VERSION_1_4);
        this.mRil.getDataRegistrationState(message);
    }

    public void getDataRegistrationStateResponse_1_6(android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo, android.hardware.radio.V1_6.RegStateResult regStateResult) {
        RILRequest processResponse_1_6 = this.mRil.processResponse_1_6(radioResponseInfo);
        if (processResponse_1_6 != null) {
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse_1_6.mResult, regStateResult);
            }
            this.mRil.processResponseDone_1_6(processResponse_1_6, radioResponseInfo, regStateResult);
        }
    }

    public void getOperatorResponse(RadioResponseInfo radioResponseInfo, String str, String str2, String str3) {
        responseStrings(radioResponseInfo, str, str2, str3);
    }

    public void setRadioPowerResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
        this.mRil.mLastRadioPowerResult = radioResponseInfo.error;
    }

    public void sendDtmfResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void sendSmsResponse(RadioResponseInfo radioResponseInfo, SendSmsResult sendSmsResult) {
        responseSms(radioResponseInfo, sendSmsResult);
    }

    public void sendSmsResponse_1_6(android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo, SendSmsResult sendSmsResult) {
        responseSms_1_6(radioResponseInfo, sendSmsResult);
    }

    public void sendSMSExpectMoreResponse(RadioResponseInfo radioResponseInfo, SendSmsResult sendSmsResult) {
        responseSms(radioResponseInfo, sendSmsResult);
    }

    public void sendSmsExpectMoreResponse_1_6(android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo, SendSmsResult sendSmsResult) {
        responseSms_1_6(radioResponseInfo, sendSmsResult);
    }

    public void setupDataCallResponse(RadioResponseInfo radioResponseInfo, SetupDataCallResult setupDataCallResult) {
        responseSetupDataCall(radioResponseInfo, setupDataCallResult);
    }

    public void setupDataCallResponse_1_4(RadioResponseInfo radioResponseInfo, android.hardware.radio.V1_4.SetupDataCallResult setupDataCallResult) {
        responseSetupDataCall(radioResponseInfo, setupDataCallResult);
    }

    public void setupDataCallResponse_1_5(RadioResponseInfo radioResponseInfo, android.hardware.radio.V1_5.SetupDataCallResult setupDataCallResult) {
        responseSetupDataCall(radioResponseInfo, setupDataCallResult);
    }

    public void setupDataCallResponse_1_6(android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo, android.hardware.radio.V1_6.SetupDataCallResult setupDataCallResult) {
        responseSetupDataCall_1_6(radioResponseInfo, setupDataCallResult);
    }

    public void getDataCallListResponse_1_6(android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo, ArrayList<android.hardware.radio.V1_6.SetupDataCallResult> arrayList) {
        responseDataCallList(radioResponseInfo, arrayList);
    }

    public void setSimCardPowerResponse_1_6(android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo) {
        responseVoid_1_6(radioResponseInfo);
    }

    public void setAllowedNetworkTypesBitmapResponse(android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo) {
        responseVoid_1_6(radioResponseInfo);
    }

    public void getAllowedNetworkTypesBitmapResponse(android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo, int i) {
        int convertHalNetworkTypeBitMask = RILUtils.convertHalNetworkTypeBitMask(i);
        this.mRil.mAllowedNetworkTypesBitmask = convertHalNetworkTypeBitMask;
        responseInts_1_6(radioResponseInfo, convertHalNetworkTypeBitMask);
    }

    public void iccIOForAppResponse(RadioResponseInfo radioResponseInfo, IccIoResult iccIoResult) {
        responseIccIo(radioResponseInfo, iccIoResult);
    }

    public void sendUssdResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void cancelPendingUssdResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void getClirResponse(RadioResponseInfo radioResponseInfo, int i, int i2) {
        responseInts(radioResponseInfo, i, i2);
    }

    public void setClirResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void getCallForwardStatusResponse(RadioResponseInfo radioResponseInfo, ArrayList<android.hardware.radio.V1_0.CallForwardInfo> arrayList) {
        responseCallForwardInfo(radioResponseInfo, arrayList);
    }

    public void setCallForwardResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void getCallWaitingResponse(RadioResponseInfo radioResponseInfo, boolean z, int i) {
        responseInts(radioResponseInfo, z ? 1 : 0, i);
    }

    public void setCallWaitingResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void acknowledgeLastIncomingGsmSmsResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void acceptCallResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void deactivateDataCallResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void getFacilityLockForAppResponse(RadioResponseInfo radioResponseInfo, int i) {
        responseInts(radioResponseInfo, i);
    }

    public void setFacilityLockForAppResponse(RadioResponseInfo radioResponseInfo, int i) {
        responseInts(radioResponseInfo, i);
    }

    public void setBarringPasswordResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void getNetworkSelectionModeResponse(RadioResponseInfo radioResponseInfo, boolean z) {
        responseInts(radioResponseInfo, z ? 1 : 0);
    }

    public void setNetworkSelectionModeAutomaticResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void setNetworkSelectionModeManualResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void setNetworkSelectionModeManualResponse_1_5(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void getAvailableNetworksResponse(RadioResponseInfo radioResponseInfo, ArrayList<OperatorInfo> arrayList) {
        responseOperatorInfos(radioResponseInfo, arrayList);
    }

    public void startNetworkScanResponse(RadioResponseInfo radioResponseInfo) {
        responseScanStatus(radioResponseInfo, null);
    }

    public void startNetworkScanResponse_1_4(RadioResponseInfo radioResponseInfo) {
        responseScanStatus(radioResponseInfo, null);
    }

    public void startNetworkScanResponse_1_5(RadioResponseInfo radioResponseInfo) {
        responseScanStatus(radioResponseInfo, RIL.RADIO_HAL_VERSION_1_4);
    }

    public void stopNetworkScanResponse(RadioResponseInfo radioResponseInfo) {
        responseScanStatus(radioResponseInfo, null);
    }

    public void startDtmfResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void stopDtmfResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void getBasebandVersionResponse(RadioResponseInfo radioResponseInfo, String str) {
        responseString(radioResponseInfo, str);
    }

    public void separateConnectionResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void setMuteResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void getMuteResponse(RadioResponseInfo radioResponseInfo, boolean z) {
        responseInts(radioResponseInfo, z ? 1 : 0);
    }

    public void getClipResponse(RadioResponseInfo radioResponseInfo, int i) {
        responseInts(radioResponseInfo, i);
    }

    public void getDataCallListResponse(RadioResponseInfo radioResponseInfo, ArrayList<SetupDataCallResult> arrayList) {
        responseDataCallList(radioResponseInfo, arrayList);
    }

    public void getDataCallListResponse_1_4(RadioResponseInfo radioResponseInfo, ArrayList<android.hardware.radio.V1_4.SetupDataCallResult> arrayList) {
        responseDataCallList(radioResponseInfo, arrayList);
    }

    public void getDataCallListResponse_1_5(RadioResponseInfo radioResponseInfo, ArrayList<android.hardware.radio.V1_5.SetupDataCallResult> arrayList) {
        responseDataCallList(radioResponseInfo, arrayList);
    }

    public void setSuppServiceNotificationsResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void writeSmsToSimResponse(RadioResponseInfo radioResponseInfo, int i) {
        responseInts(radioResponseInfo, i);
    }

    public void deleteSmsOnSimResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void setBandModeResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void getAvailableBandModesResponse(RadioResponseInfo radioResponseInfo, ArrayList<Integer> arrayList) {
        responseIntArrayList(radioResponseInfo, arrayList);
    }

    public void sendEnvelopeResponse(RadioResponseInfo radioResponseInfo, String str) {
        responseString(radioResponseInfo, str);
    }

    public void sendTerminalResponseToSimResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void handleStkCallSetupRequestFromSimResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void explicitCallTransferResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void setPreferredNetworkTypeResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void setPreferredNetworkTypeBitmapResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void getPreferredNetworkTypeResponse(RadioResponseInfo radioResponseInfo, int i) {
        this.mRil.mAllowedNetworkTypesBitmask = RadioAccessFamily.getRafFromNetworkType(i);
        responseInts(radioResponseInfo, RadioAccessFamily.getRafFromNetworkType(i));
    }

    public void getPreferredNetworkTypeBitmapResponse(RadioResponseInfo radioResponseInfo, int i) {
        int convertHalNetworkTypeBitMask = RILUtils.convertHalNetworkTypeBitMask(i);
        this.mRil.mAllowedNetworkTypesBitmask = convertHalNetworkTypeBitMask;
        responseInts(radioResponseInfo, convertHalNetworkTypeBitMask);
    }

    public void getNeighboringCidsResponse(RadioResponseInfo radioResponseInfo, ArrayList<NeighboringCell> arrayList) {
        responseCellList(radioResponseInfo, arrayList);
    }

    public void setLocationUpdatesResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void setCdmaSubscriptionSourceResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void setCdmaRoamingPreferenceResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void getCdmaRoamingPreferenceResponse(RadioResponseInfo radioResponseInfo, int i) {
        responseInts(radioResponseInfo, i);
    }

    public void setTTYModeResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void getTTYModeResponse(RadioResponseInfo radioResponseInfo, int i) {
        responseInts(radioResponseInfo, i);
    }

    public void setPreferredVoicePrivacyResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void getPreferredVoicePrivacyResponse(RadioResponseInfo radioResponseInfo, boolean z) {
        responseInts(radioResponseInfo, z ? 1 : 0);
    }

    public void sendCDMAFeatureCodeResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void sendBurstDtmfResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void sendCdmaSmsResponse(RadioResponseInfo radioResponseInfo, SendSmsResult sendSmsResult) {
        responseSms(radioResponseInfo, sendSmsResult);
    }

    public void sendCdmaSmsResponse_1_6(android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo, SendSmsResult sendSmsResult) {
        responseSms_1_6(radioResponseInfo, sendSmsResult);
    }

    public void sendCdmaSmsExpectMoreResponse(RadioResponseInfo radioResponseInfo, SendSmsResult sendSmsResult) {
        responseSms(radioResponseInfo, sendSmsResult);
    }

    public void sendCdmaSmsExpectMoreResponse_1_6(android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo, SendSmsResult sendSmsResult) {
        responseSms_1_6(radioResponseInfo, sendSmsResult);
    }

    public void setDataThrottlingResponse(android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo) {
        responseVoid_1_6(radioResponseInfo);
    }

    public void acknowledgeLastIncomingCdmaSmsResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void getGsmBroadcastConfigResponse(RadioResponseInfo radioResponseInfo, ArrayList<GsmBroadcastSmsConfigInfo> arrayList) {
        responseGmsBroadcastConfig(radioResponseInfo, arrayList);
    }

    public void setGsmBroadcastConfigResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void setGsmBroadcastActivationResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void getCdmaBroadcastConfigResponse(RadioResponseInfo radioResponseInfo, ArrayList<CdmaBroadcastSmsConfigInfo> arrayList) {
        responseCdmaBroadcastConfig(radioResponseInfo, arrayList);
    }

    public void setCdmaBroadcastConfigResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void setCdmaBroadcastActivationResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void getCDMASubscriptionResponse(RadioResponseInfo radioResponseInfo, String str, String str2, String str3, String str4, String str5) {
        responseStrings(radioResponseInfo, str, str2, str3, str4, str5);
    }

    public void writeSmsToRuimResponse(RadioResponseInfo radioResponseInfo, int i) {
        responseInts(radioResponseInfo, i);
    }

    public void deleteSmsOnRuimResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void getDeviceIdentityResponse(RadioResponseInfo radioResponseInfo, String str, String str2, String str3, String str4) {
        responseStrings(radioResponseInfo, str, str2, str3, str4);
    }

    public void exitEmergencyCallbackModeResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void getSmscAddressResponse(RadioResponseInfo radioResponseInfo, String str) {
        responseString(radioResponseInfo, str);
    }

    public void setSmscAddressResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void reportSmsMemoryStatusResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void reportStkServiceIsRunningResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void getCdmaSubscriptionSourceResponse(RadioResponseInfo radioResponseInfo, int i) {
        responseInts(radioResponseInfo, i);
    }

    public void requestIsimAuthenticationResponse(RadioResponseInfo radioResponseInfo, String str) {
        throw new RuntimeException("Inexplicable response received for requestIsimAuthentication");
    }

    public void acknowledgeIncomingGsmSmsWithPduResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void sendEnvelopeWithStatusResponse(RadioResponseInfo radioResponseInfo, IccIoResult iccIoResult) {
        responseIccIo(radioResponseInfo, iccIoResult);
    }

    public void getVoiceRadioTechnologyResponse(RadioResponseInfo radioResponseInfo, int i) {
        responseInts(radioResponseInfo, i);
    }

    public void getCellInfoListResponse(RadioResponseInfo radioResponseInfo, ArrayList<CellInfo> arrayList) {
        responseCellInfoList(radioResponseInfo, arrayList);
    }

    public void getCellInfoListResponse_1_2(RadioResponseInfo radioResponseInfo, ArrayList<android.hardware.radio.V1_2.CellInfo> arrayList) {
        responseCellInfoList(radioResponseInfo, arrayList);
    }

    public void getCellInfoListResponse_1_4(RadioResponseInfo radioResponseInfo, ArrayList<android.hardware.radio.V1_4.CellInfo> arrayList) {
        responseCellInfoList(radioResponseInfo, arrayList);
    }

    public void getCellInfoListResponse_1_5(RadioResponseInfo radioResponseInfo, ArrayList<android.hardware.radio.V1_5.CellInfo> arrayList) {
        responseCellInfoList(radioResponseInfo, arrayList);
    }

    public void getCellInfoListResponse_1_6(android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo, ArrayList<android.hardware.radio.V1_6.CellInfo> arrayList) {
        responseCellInfoList_1_6(radioResponseInfo, arrayList);
    }

    public void setCellInfoListRateResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void setInitialAttachApnResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void setInitialAttachApnResponse_1_5(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void getImsRegistrationStateResponse(RadioResponseInfo radioResponseInfo, boolean z, int i) {
        int[] iArr = new int[2];
        iArr[0] = z ? 1 : 0;
        iArr[1] = i == 0 ? (char) 1 : (char) 2;
        responseInts(radioResponseInfo, iArr);
    }

    public void sendImsSmsResponse(RadioResponseInfo radioResponseInfo, SendSmsResult sendSmsResult) {
        responseSms(radioResponseInfo, sendSmsResult);
    }

    public void iccTransmitApduBasicChannelResponse(RadioResponseInfo radioResponseInfo, IccIoResult iccIoResult) {
        responseIccIo(radioResponseInfo, iccIoResult);
    }

    public void iccOpenLogicalChannelResponse(RadioResponseInfo radioResponseInfo, int i, ArrayList<Byte> arrayList) {
        ArrayList<Integer> arrayList2 = new ArrayList<>();
        arrayList2.add(Integer.valueOf(i));
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            arrayList2.add(Integer.valueOf(arrayList.get(i2).byteValue()));
        }
        responseIntArrayList(radioResponseInfo, arrayList2);
    }

    public void iccCloseLogicalChannelResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void iccTransmitApduLogicalChannelResponse(RadioResponseInfo radioResponseInfo, IccIoResult iccIoResult) {
        responseIccIo(radioResponseInfo, iccIoResult);
    }

    public void nvReadItemResponse(RadioResponseInfo radioResponseInfo, String str) {
        responseString(radioResponseInfo, str);
    }

    public void nvWriteItemResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void nvWriteCdmaPrlResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void nvResetConfigResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void setUiccSubscriptionResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void setDataAllowedResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void getHardwareConfigResponse(RadioResponseInfo radioResponseInfo, ArrayList<android.hardware.radio.V1_0.HardwareConfig> arrayList) {
        responseHardwareConfig(radioResponseInfo, arrayList);
    }

    public void requestIccSimAuthenticationResponse(RadioResponseInfo radioResponseInfo, IccIoResult iccIoResult) {
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse != null) {
            com.android.internal.telephony.uicc.IccIoResult iccIoResult2 = new com.android.internal.telephony.uicc.IccIoResult(iccIoResult.sw1, iccIoResult.sw2, TextUtils.isEmpty(iccIoResult.simResponse) ? null : iccIoResult.simResponse.getBytes());
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, iccIoResult2);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, iccIoResult2);
        }
    }

    public void setDataProfileResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void setDataProfileResponse_1_5(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void requestShutdownResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void getRadioCapabilityResponse(RadioResponseInfo radioResponseInfo, android.hardware.radio.V1_0.RadioCapability radioCapability) {
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse != null) {
            RadioCapability convertHalRadioCapability = RILUtils.convertHalRadioCapability(radioCapability, this.mRil);
            int i = radioResponseInfo.error;
            if (i == 6 || i == 2) {
                convertHalRadioCapability = this.mRil.makeStaticRadioCapability();
                radioResponseInfo.error = 0;
            }
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, convertHalRadioCapability);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, convertHalRadioCapability);
        }
    }

    public void setRadioCapabilityResponse(RadioResponseInfo radioResponseInfo, android.hardware.radio.V1_0.RadioCapability radioCapability) {
        responseRadioCapability(radioResponseInfo, radioCapability);
    }

    public void startLceServiceResponse(RadioResponseInfo radioResponseInfo, LceStatusInfo lceStatusInfo) {
        responseLceStatus(radioResponseInfo, lceStatusInfo);
    }

    public void stopLceServiceResponse(RadioResponseInfo radioResponseInfo, LceStatusInfo lceStatusInfo) {
        responseLceStatus(radioResponseInfo, lceStatusInfo);
    }

    public void pullLceDataResponse(RadioResponseInfo radioResponseInfo, LceDataInfo lceDataInfo) {
        responseLceData(radioResponseInfo, lceDataInfo);
    }

    public void getModemActivityInfoResponse(RadioResponseInfo radioResponseInfo, ActivityStatsInfo activityStatsInfo) {
        responseActivityData(radioResponseInfo, activityStatsInfo);
    }

    public void isNrDualConnectivityEnabledResponse(android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo, boolean z) {
        RILRequest processResponse_1_6 = this.mRil.processResponse_1_6(radioResponseInfo);
        if (processResponse_1_6 != null) {
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse_1_6.mResult, Boolean.valueOf(z));
            }
            this.mRil.processResponseDone_1_6(processResponse_1_6, radioResponseInfo, Boolean.valueOf(z));
        }
    }

    public void setNrDualConnectivityStateResponse(android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo) {
        responseVoid_1_6(radioResponseInfo);
    }

    public void setAllowedCarriersResponse(RadioResponseInfo radioResponseInfo, int i) {
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse != null) {
            RIL ril = this.mRil;
            ril.riljLog("setAllowedCarriersResponse - error = " + radioResponseInfo.error);
            int i2 = radioResponseInfo.error;
            int i3 = 0;
            if (i2 == 0) {
                sendMessageResponse(processResponse.mResult, 0);
            } else if (i2 == 6) {
                radioResponseInfo.error = 0;
                i3 = 1;
                sendMessageResponse(processResponse.mResult, 1);
            } else {
                i3 = 2;
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, Integer.valueOf(i3));
        }
    }

    public void setAllowedCarriersResponse_1_4(RadioResponseInfo radioResponseInfo) {
        int i;
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse != null) {
            RIL ril = this.mRil;
            ril.riljLog("setAllowedCarriersResponse_1_4 - error = " + radioResponseInfo.error);
            if (radioResponseInfo.error == 0) {
                i = 0;
                sendMessageResponse(processResponse.mResult, 0);
            } else {
                i = 2;
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, Integer.valueOf(i));
        }
    }

    public void getAllowedCarriersResponse(RadioResponseInfo radioResponseInfo, boolean z, CarrierRestrictions carrierRestrictions) {
        CarrierRestrictionsWithPriority carrierRestrictionsWithPriority = new CarrierRestrictionsWithPriority();
        carrierRestrictionsWithPriority.allowedCarriers = carrierRestrictions.allowedCarriers;
        carrierRestrictionsWithPriority.excludedCarriers = carrierRestrictions.excludedCarriers;
        carrierRestrictionsWithPriority.allowedCarriersPrioritized = true;
        responseCarrierRestrictions(radioResponseInfo, z, carrierRestrictionsWithPriority, 0);
    }

    public void getAllowedCarriersResponse_1_4(RadioResponseInfo radioResponseInfo, CarrierRestrictionsWithPriority carrierRestrictionsWithPriority, int i) {
        responseCarrierRestrictions(radioResponseInfo, false, carrierRestrictionsWithPriority, i);
    }

    public void sendDeviceStateResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void setCarrierInfoForImsiEncryptionResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void setIndicationFilterResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void setIndicationFilterResponse_1_5(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void setSimCardPowerResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void setSignalStrengthReportingCriteriaResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void setSignalStrengthReportingCriteriaResponse_1_5(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void setLinkCapacityReportingCriteriaResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void setLinkCapacityReportingCriteriaResponse_1_5(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void setSimCardPowerResponse_1_1(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void startKeepaliveResponse(RadioResponseInfo radioResponseInfo, KeepaliveStatus keepaliveStatus) {
        com.android.internal.telephony.data.KeepaliveStatus keepaliveStatus2;
        com.android.internal.telephony.data.KeepaliveStatus keepaliveStatus3;
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse == null) {
            return;
        }
        try {
            int i = radioResponseInfo.error;
            if (i == 0) {
                int convertHalKeepaliveStatusCode = RILUtils.convertHalKeepaliveStatusCode(keepaliveStatus.code);
                if (convertHalKeepaliveStatusCode < 0) {
                    keepaliveStatus2 = new com.android.internal.telephony.data.KeepaliveStatus(1);
                } else {
                    keepaliveStatus2 = new com.android.internal.telephony.data.KeepaliveStatus(keepaliveStatus.sessionHandle, convertHalKeepaliveStatusCode);
                }
                sendMessageResponse(processResponse.mResult, keepaliveStatus2);
                keepaliveStatus3 = keepaliveStatus2;
            } else if (i == 6) {
                keepaliveStatus3 = new com.android.internal.telephony.data.KeepaliveStatus(1);
            } else if (i == 42) {
                keepaliveStatus3 = new com.android.internal.telephony.data.KeepaliveStatus(2);
            } else {
                keepaliveStatus3 = new com.android.internal.telephony.data.KeepaliveStatus(3);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, keepaliveStatus3);
        } catch (Throwable th) {
            this.mRil.processResponseDone(processResponse, radioResponseInfo, (Object) null);
            throw th;
        }
    }

    public void stopKeepaliveResponse(RadioResponseInfo radioResponseInfo) {
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse == null) {
            return;
        }
        try {
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, null);
            }
        } finally {
            this.mRil.processResponseDone(processResponse, radioResponseInfo, (Object) null);
        }
    }

    public void getSimPhonebookRecordsResponse(android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo) {
        responseVoid_1_6(radioResponseInfo);
    }

    public void getSimPhonebookCapacityResponse(android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo, PhonebookCapacity phonebookCapacity) {
        responseAdnCapacity(radioResponseInfo, RILUtils.convertHalPhonebookCapacity(phonebookCapacity));
    }

    public void updateSimPhonebookRecordsResponse(android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo, int i) {
        responseInts_1_6(radioResponseInfo, i);
    }

    private void responseAdnCapacity(android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo, AdnCapacity adnCapacity) {
        RILRequest processResponse_1_6 = this.mRil.processResponse_1_6(radioResponseInfo);
        if (processResponse_1_6 != null) {
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse_1_6.mResult, adnCapacity);
            }
            this.mRil.processResponseDone_1_6(processResponse_1_6, radioResponseInfo, adnCapacity);
        }
    }

    private void responseIccCardStatus(RadioResponseInfo radioResponseInfo, CardStatus cardStatus) {
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse != null) {
            IccCardStatus convertHalCardStatus = RILUtils.convertHalCardStatus(cardStatus);
            RIL ril = this.mRil;
            ril.riljLog("responseIccCardStatus: from HIDL: " + convertHalCardStatus);
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, convertHalCardStatus);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, convertHalCardStatus);
        }
    }

    private void responseIccCardStatus_1_2(RadioResponseInfo radioResponseInfo, android.hardware.radio.V1_2.CardStatus cardStatus) {
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse != null) {
            IccCardStatus convertHalCardStatus = RILUtils.convertHalCardStatus(cardStatus.base);
            IccSlotPortMapping iccSlotPortMapping = new IccSlotPortMapping();
            iccSlotPortMapping.mPhysicalSlotIndex = cardStatus.physicalSlotId;
            convertHalCardStatus.mSlotPortMapping = iccSlotPortMapping;
            convertHalCardStatus.atr = cardStatus.atr;
            convertHalCardStatus.iccid = cardStatus.iccid;
            RIL ril = this.mRil;
            ril.riljLog("responseIccCardStatus: from HIDL: " + convertHalCardStatus);
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, convertHalCardStatus);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, convertHalCardStatus);
        }
    }

    private void responseIccCardStatus_1_4(RadioResponseInfo radioResponseInfo, android.hardware.radio.V1_4.CardStatus cardStatus) {
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse != null) {
            IccCardStatus convertHalCardStatus = RILUtils.convertHalCardStatus(cardStatus.base.base);
            IccSlotPortMapping iccSlotPortMapping = new IccSlotPortMapping();
            android.hardware.radio.V1_2.CardStatus cardStatus2 = cardStatus.base;
            iccSlotPortMapping.mPhysicalSlotIndex = cardStatus2.physicalSlotId;
            convertHalCardStatus.mSlotPortMapping = iccSlotPortMapping;
            convertHalCardStatus.atr = cardStatus2.atr;
            convertHalCardStatus.iccid = cardStatus2.iccid;
            convertHalCardStatus.eid = cardStatus.eid;
            RIL ril = this.mRil;
            ril.riljLog("responseIccCardStatus: from HIDL: " + convertHalCardStatus);
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, convertHalCardStatus);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, convertHalCardStatus);
        }
    }

    private void responseIccCardStatus_1_5(RadioResponseInfo radioResponseInfo, android.hardware.radio.V1_5.CardStatus cardStatus) {
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse != null) {
            IccCardStatus convertHalCardStatus = RILUtils.convertHalCardStatus(cardStatus);
            RIL ril = this.mRil;
            ril.riljLog("responseIccCardStatus: from HIDL: " + convertHalCardStatus);
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, convertHalCardStatus);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, convertHalCardStatus);
        }
    }

    public void emergencyDialResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    private void responseInts(RadioResponseInfo radioResponseInfo, int... iArr) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int i : iArr) {
            arrayList.add(Integer.valueOf(i));
        }
        responseIntArrayList(radioResponseInfo, arrayList);
    }

    private void responseInts_1_6(android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo, int... iArr) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int i : iArr) {
            arrayList.add(Integer.valueOf(i));
        }
        responseIntArrayList_1_6(radioResponseInfo, arrayList);
    }

    public static void responseInts(int i, RIL ril, android.hardware.radio.RadioResponseInfo radioResponseInfo, int... iArr) {
        ArrayList arrayList = new ArrayList();
        for (int i2 : iArr) {
            arrayList.add(Integer.valueOf(i2));
        }
        responseIntArrayList(i, ril, radioResponseInfo, arrayList);
    }

    private void responseIntArrayList(RadioResponseInfo radioResponseInfo, ArrayList<Integer> arrayList) {
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse != null) {
            int[] iArr = new int[arrayList.size()];
            for (int i = 0; i < arrayList.size(); i++) {
                iArr[i] = arrayList.get(i).intValue();
            }
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, iArr);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, iArr);
        }
    }

    private void responseIntArrayList_1_6(android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo, ArrayList<Integer> arrayList) {
        RILRequest processResponse_1_6 = this.mRil.processResponse_1_6(radioResponseInfo);
        if (processResponse_1_6 != null) {
            int[] iArr = new int[arrayList.size()];
            for (int i = 0; i < arrayList.size(); i++) {
                iArr[i] = arrayList.get(i).intValue();
            }
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse_1_6.mResult, iArr);
            }
            this.mRil.processResponseDone_1_6(processResponse_1_6, radioResponseInfo, iArr);
        }
    }

    public static void responseIntArrayList(int i, RIL ril, android.hardware.radio.RadioResponseInfo radioResponseInfo, ArrayList<Integer> arrayList) {
        RILRequest processResponse = ril.processResponse(i, radioResponseInfo);
        if (processResponse != null) {
            int[] iArr = new int[arrayList.size()];
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                iArr[i2] = arrayList.get(i2).intValue();
            }
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, iArr);
            }
            ril.processResponseDone(processResponse, radioResponseInfo, iArr);
        }
    }

    private void responseCurrentCalls(RadioResponseInfo radioResponseInfo, ArrayList<android.hardware.radio.V1_0.Call> arrayList) {
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse != null) {
            int size = arrayList.size();
            ArrayList arrayList2 = new ArrayList(size);
            for (int i = 0; i < size; i++) {
                DriverCall convertToDriverCall = RILUtils.convertToDriverCall(arrayList.get(i));
                arrayList2.add(convertToDriverCall);
                if (convertToDriverCall.isVoicePrivacy) {
                    this.mRil.mVoicePrivacyOnRegistrants.notifyRegistrants();
                    this.mRil.riljLog("InCall VoicePrivacy is enabled");
                } else {
                    this.mRil.mVoicePrivacyOffRegistrants.notifyRegistrants();
                    this.mRil.riljLog("InCall VoicePrivacy is disabled");
                }
            }
            Collections.sort(arrayList2);
            if (size == 0 && this.mRil.mTestingEmergencyCall.getAndSet(false)) {
                RIL ril = this.mRil;
                if (ril.mEmergencyCallbackModeRegistrant != null) {
                    ril.riljLog("responseCurrentCalls: call ended, testing emergency call, notify ECM Registrants");
                    this.mRil.mEmergencyCallbackModeRegistrant.notifyRegistrant();
                }
            }
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, arrayList2);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, arrayList2);
        }
    }

    private void responseCurrentCalls_1_2(RadioResponseInfo radioResponseInfo, ArrayList<android.hardware.radio.V1_2.Call> arrayList) {
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse != null) {
            int size = arrayList.size();
            ArrayList arrayList2 = new ArrayList(size);
            for (int i = 0; i < size; i++) {
                DriverCall convertToDriverCall = RILUtils.convertToDriverCall(arrayList.get(i));
                arrayList2.add(convertToDriverCall);
                if (convertToDriverCall.isVoicePrivacy) {
                    this.mRil.mVoicePrivacyOnRegistrants.notifyRegistrants();
                    this.mRil.riljLog("InCall VoicePrivacy is enabled");
                } else {
                    this.mRil.mVoicePrivacyOffRegistrants.notifyRegistrants();
                    this.mRil.riljLog("InCall VoicePrivacy is disabled");
                }
            }
            Collections.sort(arrayList2);
            if (size == 0 && this.mRil.mTestingEmergencyCall.getAndSet(false)) {
                RIL ril = this.mRil;
                if (ril.mEmergencyCallbackModeRegistrant != null) {
                    ril.riljLog("responseCurrentCalls: call ended, testing emergency call, notify ECM Registrants");
                    this.mRil.mEmergencyCallbackModeRegistrant.notifyRegistrant();
                }
            }
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, arrayList2);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, arrayList2);
        }
    }

    private void responseCurrentCalls_1_6(android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo, ArrayList<android.hardware.radio.V1_6.Call> arrayList) {
        RILRequest processResponse_1_6 = this.mRil.processResponse_1_6(radioResponseInfo);
        if (processResponse_1_6 != null) {
            int size = arrayList.size();
            ArrayList arrayList2 = new ArrayList(size);
            for (int i = 0; i < size; i++) {
                DriverCall convertToDriverCall = RILUtils.convertToDriverCall(arrayList.get(i));
                arrayList2.add(convertToDriverCall);
                if (convertToDriverCall.isVoicePrivacy) {
                    this.mRil.mVoicePrivacyOnRegistrants.notifyRegistrants();
                    this.mRil.riljLog("InCall VoicePrivacy is enabled");
                } else {
                    this.mRil.mVoicePrivacyOffRegistrants.notifyRegistrants();
                    this.mRil.riljLog("InCall VoicePrivacy is disabled");
                }
            }
            Collections.sort(arrayList2);
            if (size == 0 && this.mRil.mTestingEmergencyCall.getAndSet(false)) {
                RIL ril = this.mRil;
                if (ril.mEmergencyCallbackModeRegistrant != null) {
                    ril.riljLog("responseCurrentCalls: call ended, testing emergency call, notify ECM Registrants");
                    this.mRil.mEmergencyCallbackModeRegistrant.notifyRegistrant();
                }
            }
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse_1_6.mResult, arrayList2);
            }
            this.mRil.processResponseDone_1_6(processResponse_1_6, radioResponseInfo, arrayList2);
        }
    }

    private void responseVoid(RadioResponseInfo radioResponseInfo) {
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse != null) {
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, null);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, (Object) null);
        }
    }

    private void responseVoid_1_6(android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo) {
        RILRequest processResponse_1_6 = this.mRil.processResponse_1_6(radioResponseInfo);
        if (processResponse_1_6 != null) {
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse_1_6.mResult, null);
            }
            this.mRil.processResponseDone_1_6(processResponse_1_6, radioResponseInfo, null);
        }
    }

    public static void responseVoid(int i, RIL ril, android.hardware.radio.RadioResponseInfo radioResponseInfo) {
        RILRequest processResponse = ril.processResponse(i, radioResponseInfo);
        if (processResponse != null) {
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, null);
            }
            ril.processResponseDone(processResponse, radioResponseInfo, (Object) null);
        }
    }

    private void responseString(RadioResponseInfo radioResponseInfo, String str) {
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse != null) {
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, str);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, str);
        }
    }

    public static void responseString(int i, RIL ril, android.hardware.radio.RadioResponseInfo radioResponseInfo, String str) {
        RILRequest processResponse = ril.processResponse(i, radioResponseInfo);
        if (processResponse != null) {
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, str);
            }
            ril.processResponseDone(processResponse, radioResponseInfo, str);
        }
    }

    private void responseStrings(RadioResponseInfo radioResponseInfo, String... strArr) {
        ArrayList arrayList = new ArrayList();
        for (String str : strArr) {
            arrayList.add(str);
        }
        responseStringArrayList(this.mRil, radioResponseInfo, arrayList);
    }

    public static void responseStrings(int i, RIL ril, android.hardware.radio.RadioResponseInfo radioResponseInfo, String... strArr) {
        ArrayList arrayList = new ArrayList();
        for (String str : strArr) {
            arrayList.add(str);
        }
        responseStringArrayList(i, ril, radioResponseInfo, arrayList);
    }

    static void responseStringArrayList(RIL ril, RadioResponseInfo radioResponseInfo, ArrayList<String> arrayList) {
        RILRequest processResponse = ril.processResponse(radioResponseInfo);
        if (processResponse != null) {
            String[] strArr = new String[arrayList.size()];
            for (int i = 0; i < arrayList.size(); i++) {
                strArr[i] = arrayList.get(i);
            }
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, strArr);
            }
            ril.processResponseDone(processResponse, radioResponseInfo, strArr);
        }
    }

    private static void responseStringArrayList(int i, RIL ril, android.hardware.radio.RadioResponseInfo radioResponseInfo, ArrayList<String> arrayList) {
        RILRequest processResponse = ril.processResponse(i, radioResponseInfo);
        if (processResponse != null) {
            String[] strArr = new String[arrayList.size()];
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                strArr[i2] = arrayList.get(i2);
            }
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, strArr);
            }
            ril.processResponseDone(processResponse, radioResponseInfo, strArr);
        }
    }

    private void responseLastCallFailCauseInfo(RadioResponseInfo radioResponseInfo, LastCallFailCauseInfo lastCallFailCauseInfo) {
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse != null) {
            LastCallFailCause lastCallFailCause = new LastCallFailCause();
            lastCallFailCause.causeCode = lastCallFailCauseInfo.causeCode;
            lastCallFailCause.vendorCause = lastCallFailCauseInfo.vendorCause;
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, lastCallFailCause);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, lastCallFailCause);
        }
    }

    private void responseSignalStrength(RadioResponseInfo radioResponseInfo, SignalStrength signalStrength) {
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse != null) {
            android.telephony.SignalStrength convertHalSignalStrength = RILUtils.convertHalSignalStrength(signalStrength);
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, convertHalSignalStrength);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, convertHalSignalStrength);
        }
    }

    private void responseSignalStrength_1_2(RadioResponseInfo radioResponseInfo, android.hardware.radio.V1_2.SignalStrength signalStrength) {
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse != null) {
            android.telephony.SignalStrength convertHalSignalStrength = RILUtils.convertHalSignalStrength(signalStrength);
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, convertHalSignalStrength);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, convertHalSignalStrength);
        }
    }

    private void responseSignalStrength_1_4(RadioResponseInfo radioResponseInfo, android.hardware.radio.V1_4.SignalStrength signalStrength) {
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse != null) {
            android.telephony.SignalStrength convertHalSignalStrength = RILUtils.convertHalSignalStrength(signalStrength);
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, convertHalSignalStrength);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, convertHalSignalStrength);
        }
    }

    private void responseSignalStrength_1_6(android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo, android.hardware.radio.V1_6.SignalStrength signalStrength) {
        RILRequest processResponse_1_6 = this.mRil.processResponse_1_6(radioResponseInfo);
        if (processResponse_1_6 != null) {
            android.telephony.SignalStrength convertHalSignalStrength = RILUtils.convertHalSignalStrength(signalStrength);
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse_1_6.mResult, convertHalSignalStrength);
            }
            this.mRil.processResponseDone_1_6(processResponse_1_6, radioResponseInfo, convertHalSignalStrength);
        }
    }

    private void responseSms(RadioResponseInfo radioResponseInfo, SendSmsResult sendSmsResult) {
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse != null) {
            SmsResponse smsResponse = new SmsResponse(sendSmsResult.messageRef, sendSmsResult.ackPDU, sendSmsResult.errorCode, RIL.getOutgoingSmsMessageId(processResponse.mResult));
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, smsResponse);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, smsResponse);
        }
    }

    private void responseSms_1_6(android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo, SendSmsResult sendSmsResult) {
        RILRequest processResponse_1_6 = this.mRil.processResponse_1_6(radioResponseInfo);
        if (processResponse_1_6 != null) {
            SmsResponse smsResponse = new SmsResponse(sendSmsResult.messageRef, sendSmsResult.ackPDU, sendSmsResult.errorCode, RIL.getOutgoingSmsMessageId(processResponse_1_6.mResult));
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse_1_6.mResult, smsResponse);
            }
            this.mRil.processResponseDone_1_6(processResponse_1_6, radioResponseInfo, smsResponse);
        }
    }

    private void responseSetupDataCall(RadioResponseInfo radioResponseInfo, Object obj) {
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse != null) {
            DataCallResponse convertHalDataCallResult = RILUtils.convertHalDataCallResult(obj);
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, convertHalDataCallResult);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, convertHalDataCallResult);
        }
    }

    private void responseSetupDataCall_1_6(android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo, Object obj) {
        RILRequest processResponse_1_6 = this.mRil.processResponse_1_6(radioResponseInfo);
        if (processResponse_1_6 != null) {
            DataCallResponse convertHalDataCallResult = RILUtils.convertHalDataCallResult(obj);
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse_1_6.mResult, convertHalDataCallResult);
            }
            this.mRil.processResponseDone_1_6(processResponse_1_6, radioResponseInfo, convertHalDataCallResult);
        }
    }

    private void responseIccIo(RadioResponseInfo radioResponseInfo, IccIoResult iccIoResult) {
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse != null) {
            com.android.internal.telephony.uicc.IccIoResult iccIoResult2 = new com.android.internal.telephony.uicc.IccIoResult(iccIoResult.sw1, iccIoResult.sw2, iccIoResult.simResponse);
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, iccIoResult2);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, iccIoResult2);
        }
    }

    private void responseCallForwardInfo(RadioResponseInfo radioResponseInfo, ArrayList<android.hardware.radio.V1_0.CallForwardInfo> arrayList) {
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse != null) {
            CallForwardInfo[] callForwardInfoArr = new CallForwardInfo[arrayList.size()];
            for (int i = 0; i < arrayList.size(); i++) {
                CallForwardInfo callForwardInfo = new CallForwardInfo();
                callForwardInfoArr[i] = callForwardInfo;
                callForwardInfo.status = arrayList.get(i).status;
                callForwardInfoArr[i].reason = arrayList.get(i).reason;
                callForwardInfoArr[i].serviceClass = arrayList.get(i).serviceClass;
                callForwardInfoArr[i].toa = arrayList.get(i).toa;
                callForwardInfoArr[i].number = arrayList.get(i).number;
                callForwardInfoArr[i].timeSeconds = arrayList.get(i).timeSeconds;
            }
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, callForwardInfoArr);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, callForwardInfoArr);
        }
    }

    private void responseOperatorInfos(RadioResponseInfo radioResponseInfo, ArrayList<OperatorInfo> arrayList) {
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse != null) {
            ArrayList arrayList2 = new ArrayList();
            for (int i = 0; i < arrayList.size(); i++) {
                arrayList2.add(new OperatorInfo(arrayList.get(i).alphaLong, arrayList.get(i).alphaShort, arrayList.get(i).operatorNumeric, RILUtils.convertHalOperatorStatus(arrayList.get(i).status)));
            }
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, arrayList2);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, arrayList2);
        }
    }

    private void responseScanStatus(RadioResponseInfo radioResponseInfo, HalVersion halVersion) {
        Object[] objArr;
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse == null) {
            return;
        }
        int i = radioResponseInfo.error;
        NetworkScanResult networkScanResult = null;
        if (i == 6 && halVersion != null && (objArr = processResponse.mArguments) != null && objArr.length > 0 && (objArr[0] instanceof NetworkScanRequest)) {
            final int request = processResponse.getRequest();
            final Message result = processResponse.getResult();
            final NetworkScanRequest networkScanRequest = (NetworkScanRequest) processResponse.mArguments[0];
            this.mRil.mRilHandler.post(new Runnable() { // from class: com.android.internal.telephony.RadioResponse$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    RadioResponse.this.lambda$responseScanStatus$2(request, networkScanRequest, result);
                }
            });
            this.mRil.processResponseFallback(processResponse, radioResponseInfo, null);
            return;
        }
        if (i == 0) {
            NetworkScanResult networkScanResult2 = new NetworkScanResult(1, 0, (List) null);
            sendMessageResponse(processResponse.mResult, networkScanResult2);
            networkScanResult = networkScanResult2;
        }
        this.mRil.processResponseDone(processResponse, radioResponseInfo, networkScanResult);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$responseScanStatus$2(int i, NetworkScanRequest networkScanRequest, Message message) {
        this.mRil.setCompatVersion(i, RIL.RADIO_HAL_VERSION_1_4);
        this.mRil.startNetworkScan(networkScanRequest, message);
    }

    private void responseDataCallList(RadioResponseInfo radioResponseInfo, List<? extends Object> list) {
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse != null) {
            ArrayList<DataCallResponse> convertHalDataCallResultList = RILUtils.convertHalDataCallResultList(list);
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, convertHalDataCallResultList);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, convertHalDataCallResultList);
        }
    }

    private void responseDataCallList(android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo, List<? extends Object> list) {
        RILRequest processResponse_1_6 = this.mRil.processResponse_1_6(radioResponseInfo);
        if (processResponse_1_6 != null) {
            ArrayList<DataCallResponse> convertHalDataCallResultList = RILUtils.convertHalDataCallResultList(list);
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse_1_6.mResult, convertHalDataCallResultList);
            }
            this.mRil.processResponseDone_1_6(processResponse_1_6, radioResponseInfo, convertHalDataCallResultList);
        }
    }

    private void responseCellList(RadioResponseInfo radioResponseInfo, ArrayList<NeighboringCell> arrayList) {
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse != null) {
            ArrayList arrayList2 = new ArrayList();
            int dataNetworkType = ((TelephonyManager) this.mRil.mContext.getSystemService("phone")).getDataNetworkType(SubscriptionManager.getSubscriptionId(this.mRil.mPhoneId.intValue()));
            if (dataNetworkType != 0) {
                for (int i = 0; i < arrayList.size(); i++) {
                    arrayList2.add(new NeighboringCellInfo(arrayList.get(i).rssi, arrayList.get(i).cid, dataNetworkType));
                }
            }
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, arrayList2);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, arrayList2);
        }
    }

    private void responseGmsBroadcastConfig(RadioResponseInfo radioResponseInfo, ArrayList<GsmBroadcastSmsConfigInfo> arrayList) {
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse != null) {
            ArrayList arrayList2 = new ArrayList();
            for (int i = 0; i < arrayList.size(); i++) {
                arrayList2.add(new SmsBroadcastConfigInfo(arrayList.get(i).fromServiceId, arrayList.get(i).toServiceId, arrayList.get(i).fromCodeScheme, arrayList.get(i).toCodeScheme, arrayList.get(i).selected));
            }
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, arrayList2);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, arrayList2);
        }
    }

    private void responseCdmaBroadcastConfig(RadioResponseInfo radioResponseInfo, ArrayList<CdmaBroadcastSmsConfigInfo> arrayList) {
        int[] iArr;
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse != null) {
            int size = arrayList.size();
            int i = 0;
            int i2 = 1;
            if (size == 0) {
                iArr = new int[94];
                iArr[0] = 31;
                for (int i3 = 1; i3 < 94; i3 += 3) {
                    iArr[i3 + 0] = i3 / 3;
                    iArr[i3 + 1] = 1;
                    iArr[i3 + 2] = 0;
                }
            } else {
                int[] iArr2 = new int[(size * 3) + 1];
                iArr2[0] = size;
                while (i < arrayList.size()) {
                    iArr2[i2] = arrayList.get(i).serviceCategory;
                    iArr2[i2 + 1] = arrayList.get(i).language;
                    iArr2[i2 + 2] = arrayList.get(i).selected ? 1 : 0;
                    i++;
                    i2 += 3;
                }
                iArr = iArr2;
            }
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, iArr);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, iArr);
        }
    }

    private void responseCellInfoList(RadioResponseInfo radioResponseInfo, ArrayList<? extends Object> arrayList) {
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse != null) {
            ArrayList<android.telephony.CellInfo> convertHalCellInfoList = RILUtils.convertHalCellInfoList(arrayList);
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, convertHalCellInfoList);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, convertHalCellInfoList);
        }
    }

    private void responseCellInfoList_1_6(android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo, ArrayList<? extends Object> arrayList) {
        RILRequest processResponse_1_6 = this.mRil.processResponse_1_6(radioResponseInfo);
        if (processResponse_1_6 != null) {
            ArrayList<android.telephony.CellInfo> convertHalCellInfoList = RILUtils.convertHalCellInfoList(arrayList);
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse_1_6.mResult, convertHalCellInfoList);
            }
            this.mRil.processResponseDone_1_6(processResponse_1_6, radioResponseInfo, convertHalCellInfoList);
        }
    }

    private void responseActivityData(RadioResponseInfo radioResponseInfo, ActivityStatsInfo activityStatsInfo) {
        ModemActivityInfo modemActivityInfo;
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse != null) {
            if (radioResponseInfo.error == 0) {
                int i = activityStatsInfo.sleepModeTimeMs;
                int i2 = activityStatsInfo.idleModeTimeMs;
                int[] iArr = new int[ModemActivityInfo.getNumTxPowerLevels()];
                for (int i3 = 0; i3 < ModemActivityInfo.getNumTxPowerLevels(); i3++) {
                    iArr[i3] = activityStatsInfo.txmModetimeMs[i3];
                }
                modemActivityInfo = new ModemActivityInfo(SystemClock.elapsedRealtime(), i, i2, iArr, activityStatsInfo.rxModeTimeMs);
            } else {
                modemActivityInfo = new ModemActivityInfo(0L, 0, 0, new int[ModemActivityInfo.getNumTxPowerLevels()], 0);
                radioResponseInfo.error = 0;
            }
            sendMessageResponse(processResponse.mResult, modemActivityInfo);
            this.mRil.processResponseDone(processResponse, radioResponseInfo, modemActivityInfo);
        }
    }

    private void responseHardwareConfig(RadioResponseInfo radioResponseInfo, ArrayList<android.hardware.radio.V1_0.HardwareConfig> arrayList) {
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse != null) {
            ArrayList<HardwareConfig> convertHalHardwareConfigList = RILUtils.convertHalHardwareConfigList(arrayList);
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, convertHalHardwareConfigList);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, convertHalHardwareConfigList);
        }
    }

    private void responseRadioCapability(RadioResponseInfo radioResponseInfo, android.hardware.radio.V1_0.RadioCapability radioCapability) {
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse != null) {
            RadioCapability convertHalRadioCapability = RILUtils.convertHalRadioCapability(radioCapability, this.mRil);
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, convertHalRadioCapability);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, convertHalRadioCapability);
        }
    }

    private void responseLceStatus(RadioResponseInfo radioResponseInfo, LceStatusInfo lceStatusInfo) {
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse != null) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(Integer.valueOf(lceStatusInfo.lceStatus));
            arrayList.add(Integer.valueOf(Byte.toUnsignedInt(lceStatusInfo.actualIntervalMs)));
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, arrayList);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, arrayList);
        }
    }

    private void responseLceData(RadioResponseInfo radioResponseInfo, LceDataInfo lceDataInfo) {
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse != null) {
            List<LinkCapacityEstimate> convertHalLceData = RILUtils.convertHalLceData(lceDataInfo);
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, convertHalLceData);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, convertHalLceData);
        }
    }

    private void responseCarrierRestrictions(RadioResponseInfo radioResponseInfo, boolean z, CarrierRestrictionsWithPriority carrierRestrictionsWithPriority, int i) {
        CarrierRestrictionRules build;
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse == null) {
            return;
        }
        if (z) {
            build = CarrierRestrictionRules.newBuilder().setAllCarriersAllowed().build();
        } else {
            build = CarrierRestrictionRules.newBuilder().setAllowedCarriers(RILUtils.convertHalCarrierList(carrierRestrictionsWithPriority.allowedCarriers)).setExcludedCarriers(RILUtils.convertHalCarrierList(carrierRestrictionsWithPriority.excludedCarriers)).setDefaultCarrierRestriction(1 ^ (carrierRestrictionsWithPriority.allowedCarriersPrioritized ? 1 : 0)).setMultiSimPolicy(i == 1 ? 1 : 0).build();
        }
        if (radioResponseInfo.error == 0) {
            sendMessageResponse(processResponse.mResult, build);
        }
        this.mRil.processResponseDone(processResponse, radioResponseInfo, build);
    }

    public void enableModemResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void getModemStackStatusResponse(RadioResponseInfo radioResponseInfo, boolean z) {
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse != null) {
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, Boolean.valueOf(z));
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, Boolean.valueOf(z));
        }
    }

    public void setSystemSelectionChannelsResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void enableUiccApplicationsResponse(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void areUiccApplicationsEnabledResponse(RadioResponseInfo radioResponseInfo, boolean z) {
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse != null) {
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, Boolean.valueOf(z));
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, Boolean.valueOf(z));
        }
    }

    public void setRadioPowerResponse_1_5(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
        RIL ril = this.mRil;
        int i = radioResponseInfo.error;
        ril.mLastRadioPowerResult = i;
        if (i == 1 || i == 0) {
            return;
        }
        AnomalyReporter.reportAnomaly(UUID.fromString(RILUtils.RADIO_POWER_FAILURE_BUGREPORT_UUID), "Radio power failure");
    }

    public void setRadioPowerResponse_1_6(android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo) {
        responseVoid_1_6(radioResponseInfo);
        RIL ril = this.mRil;
        int i = radioResponseInfo.error;
        ril.mLastRadioPowerResult = i;
        if (i == 70) {
            AnomalyReporter.reportAnomaly(UUID.fromString(RILUtils.RADIO_POWER_FAILURE_RF_HARDWARE_ISSUE_UUID), "RF HW damaged");
        } else if (i == 71) {
            AnomalyReporter.reportAnomaly(UUID.fromString(RILUtils.RADIO_POWER_FAILURE_NO_RF_CALIBRATION_UUID), "No RF calibration data");
        } else if (i == 1 || i == 0) {
        } else {
            AnomalyReporter.reportAnomaly(UUID.fromString(RILUtils.RADIO_POWER_FAILURE_BUGREPORT_UUID), "Radio power failure");
        }
    }

    public void setSystemSelectionChannelsResponse_1_5(RadioResponseInfo radioResponseInfo) {
        responseVoid(radioResponseInfo);
    }

    public void getSystemSelectionChannelsResponse(android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo, ArrayList<RadioAccessSpecifier> arrayList) {
        RILRequest processResponse_1_6 = this.mRil.processResponse_1_6(radioResponseInfo);
        if (processResponse_1_6 != null) {
            ArrayList arrayList2 = new ArrayList();
            Iterator<RadioAccessSpecifier> it = arrayList.iterator();
            while (it.hasNext()) {
                arrayList2.add(RILUtils.convertHalRadioAccessSpecifier(it.next()));
            }
            RIL ril = this.mRil;
            ril.riljLog("getSystemSelectionChannelsResponse: from HIDL: " + arrayList2);
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse_1_6.mResult, arrayList2);
            }
            this.mRil.processResponseDone_1_6(processResponse_1_6, radioResponseInfo, arrayList2);
        }
    }

    public void getBarringInfoResponse(RadioResponseInfo radioResponseInfo, CellIdentity cellIdentity, ArrayList<BarringInfo> arrayList) {
        RILRequest processResponse = this.mRil.processResponse(radioResponseInfo);
        if (processResponse != null) {
            android.telephony.BarringInfo barringInfo = new android.telephony.BarringInfo(RILUtils.convertHalCellIdentity(cellIdentity), RILUtils.convertHalBarringInfoList(arrayList));
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse.mResult, barringInfo);
                this.mRil.mBarringInfoChangedRegistrants.notifyRegistrants(new AsyncResult((Object) null, barringInfo, (Throwable) null));
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, barringInfo);
        }
    }

    public void allocatePduSessionIdResponse(android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo, int i) {
        RILRequest processResponse_1_6 = this.mRil.processResponse_1_6(radioResponseInfo);
        if (processResponse_1_6 != null) {
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse_1_6.mResult, Integer.valueOf(i));
            }
            this.mRil.processResponseDone_1_6(processResponse_1_6, radioResponseInfo, Integer.valueOf(i));
        }
    }

    public void releasePduSessionIdResponse(android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo) {
        responseVoid_1_6(radioResponseInfo);
    }

    public void startHandoverResponse(android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo) {
        responseVoid_1_6(radioResponseInfo);
    }

    public void cancelHandoverResponse(android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo) {
        responseVoid_1_6(radioResponseInfo);
    }

    public void getSlicingConfigResponse(android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo, SlicingConfig slicingConfig) {
        RILRequest processResponse_1_6 = this.mRil.processResponse_1_6(radioResponseInfo);
        if (processResponse_1_6 != null) {
            NetworkSlicingConfig convertHalSlicingConfig = RILUtils.convertHalSlicingConfig(slicingConfig);
            if (radioResponseInfo.error == 0) {
                sendMessageResponse(processResponse_1_6.mResult, convertHalSlicingConfig);
            }
            this.mRil.processResponseDone_1_6(processResponse_1_6, radioResponseInfo, convertHalSlicingConfig);
        }
    }
}
