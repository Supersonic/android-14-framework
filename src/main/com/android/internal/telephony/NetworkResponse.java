package com.android.internal.telephony;

import android.hardware.radio.RadioResponseInfo;
import android.hardware.radio.network.BarringInfo;
import android.hardware.radio.network.CellIdentity;
import android.hardware.radio.network.CellInfo;
import android.hardware.radio.network.EmergencyRegResult;
import android.hardware.radio.network.IRadioNetworkResponse;
import android.hardware.radio.network.LceDataInfo;
import android.hardware.radio.network.OperatorInfo;
import android.hardware.radio.network.RadioAccessSpecifier;
import android.hardware.radio.network.RegStateResult;
import android.hardware.radio.network.SignalStrength;
import android.os.AsyncResult;
import android.telephony.LinkCapacityEstimate;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class NetworkResponse extends IRadioNetworkResponse.Stub {
    private final RIL mRil;

    public String getInterfaceHash() {
        return "notfrozen";
    }

    public int getInterfaceVersion() {
        return 2;
    }

    public NetworkResponse(RIL ril) {
        this.mRil = ril;
    }

    public void acknowledgeRequest(int i) {
        this.mRil.processRequestAck(i);
    }

    public void getAllowedNetworkTypesBitmapResponse(RadioResponseInfo radioResponseInfo, int i) {
        int convertHalNetworkTypeBitMask = RILUtils.convertHalNetworkTypeBitMask(i);
        RIL ril = this.mRil;
        ril.mAllowedNetworkTypesBitmask = convertHalNetworkTypeBitMask;
        RadioResponse.responseInts(4, ril, radioResponseInfo, convertHalNetworkTypeBitMask);
    }

    public void getAvailableBandModesResponse(RadioResponseInfo radioResponseInfo, int[] iArr) {
        RadioResponse.responseIntArrayList(4, this.mRil, radioResponseInfo, RILUtils.primitiveArrayToArrayList(iArr));
    }

    public void getAvailableNetworksResponse(RadioResponseInfo radioResponseInfo, OperatorInfo[] operatorInfoArr) {
        RILRequest processResponse = this.mRil.processResponse(4, radioResponseInfo);
        if (processResponse != null) {
            ArrayList arrayList = new ArrayList();
            for (OperatorInfo operatorInfo : operatorInfoArr) {
                arrayList.add(new OperatorInfo(operatorInfo.alphaLong, operatorInfo.alphaShort, operatorInfo.operatorNumeric, RILUtils.convertHalOperatorStatus(operatorInfo.status)));
            }
            if (radioResponseInfo.error == 0) {
                RadioResponse.sendMessageResponse(processResponse.mResult, arrayList);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, arrayList);
        }
    }

    public void getBarringInfoResponse(RadioResponseInfo radioResponseInfo, CellIdentity cellIdentity, BarringInfo[] barringInfoArr) {
        RILRequest processResponse = this.mRil.processResponse(4, radioResponseInfo);
        if (processResponse != null) {
            android.telephony.BarringInfo barringInfo = new android.telephony.BarringInfo(RILUtils.convertHalCellIdentity(cellIdentity), RILUtils.convertHalBarringInfoList(barringInfoArr));
            if (radioResponseInfo.error == 0) {
                RadioResponse.sendMessageResponse(processResponse.mResult, barringInfo);
                this.mRil.mBarringInfoChangedRegistrants.notifyRegistrants(new AsyncResult((Object) null, barringInfo, (Throwable) null));
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, barringInfo);
        }
    }

    public void getCdmaRoamingPreferenceResponse(RadioResponseInfo radioResponseInfo, int i) {
        RadioResponse.responseInts(4, this.mRil, radioResponseInfo, i);
    }

    public void getCellInfoListResponse(RadioResponseInfo radioResponseInfo, CellInfo[] cellInfoArr) {
        RILRequest processResponse = this.mRil.processResponse(4, radioResponseInfo);
        if (processResponse != null) {
            ArrayList<android.telephony.CellInfo> convertHalCellInfoList = RILUtils.convertHalCellInfoList(cellInfoArr);
            if (radioResponseInfo.error == 0) {
                RadioResponse.sendMessageResponse(processResponse.mResult, convertHalCellInfoList);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, convertHalCellInfoList);
        }
    }

    public void getDataRegistrationStateResponse(RadioResponseInfo radioResponseInfo, RegStateResult regStateResult) {
        RILRequest processResponse = this.mRil.processResponse(4, radioResponseInfo);
        if (processResponse != null) {
            if (radioResponseInfo.error == 0) {
                RadioResponse.sendMessageResponse(processResponse.mResult, regStateResult);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, regStateResult);
        }
    }

    public void getImsRegistrationStateResponse(RadioResponseInfo radioResponseInfo, boolean z, int i) {
        RIL ril = this.mRil;
        int[] iArr = new int[2];
        iArr[0] = z ? 1 : 0;
        iArr[1] = i == 0 ? (char) 1 : (char) 2;
        RadioResponse.responseInts(4, ril, radioResponseInfo, iArr);
    }

    public void getNetworkSelectionModeResponse(RadioResponseInfo radioResponseInfo, boolean z) {
        RadioResponse.responseInts(4, this.mRil, radioResponseInfo, z ? 1 : 0);
    }

    public void getOperatorResponse(RadioResponseInfo radioResponseInfo, String str, String str2, String str3) {
        RadioResponse.responseStrings(4, this.mRil, radioResponseInfo, str, str2, str3);
    }

    public void getSignalStrengthResponse(RadioResponseInfo radioResponseInfo, SignalStrength signalStrength) {
        RILRequest processResponse = this.mRil.processResponse(4, radioResponseInfo);
        if (processResponse != null) {
            android.telephony.SignalStrength convertHalSignalStrength = RILUtils.convertHalSignalStrength(signalStrength);
            if (radioResponseInfo.error == 0) {
                RadioResponse.sendMessageResponse(processResponse.mResult, convertHalSignalStrength);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, convertHalSignalStrength);
        }
    }

    public void getSystemSelectionChannelsResponse(RadioResponseInfo radioResponseInfo, RadioAccessSpecifier[] radioAccessSpecifierArr) {
        RILRequest processResponse = this.mRil.processResponse(4, radioResponseInfo);
        if (processResponse != null) {
            ArrayList arrayList = new ArrayList();
            for (RadioAccessSpecifier radioAccessSpecifier : radioAccessSpecifierArr) {
                arrayList.add(RILUtils.convertHalRadioAccessSpecifier(radioAccessSpecifier));
            }
            this.mRil.riljLog("getSystemSelectionChannelsResponse: from AIDL: " + arrayList);
            if (radioResponseInfo.error == 0) {
                RadioResponse.sendMessageResponse(processResponse.mResult, arrayList);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, arrayList);
        }
    }

    public void getVoiceRadioTechnologyResponse(RadioResponseInfo radioResponseInfo, int i) {
        RadioResponse.responseInts(4, this.mRil, radioResponseInfo, i);
    }

    public void getVoiceRegistrationStateResponse(RadioResponseInfo radioResponseInfo, RegStateResult regStateResult) {
        RILRequest processResponse = this.mRil.processResponse(4, radioResponseInfo);
        if (processResponse != null) {
            if (radioResponseInfo.error == 0) {
                RadioResponse.sendMessageResponse(processResponse.mResult, regStateResult);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, regStateResult);
        }
    }

    public void isNrDualConnectivityEnabledResponse(RadioResponseInfo radioResponseInfo, boolean z) {
        RILRequest processResponse = this.mRil.processResponse(4, radioResponseInfo);
        if (processResponse != null) {
            if (radioResponseInfo.error == 0) {
                RadioResponse.sendMessageResponse(processResponse.mResult, Boolean.valueOf(z));
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, Boolean.valueOf(z));
        }
    }

    public void pullLceDataResponse(RadioResponseInfo radioResponseInfo, LceDataInfo lceDataInfo) {
        RILRequest processResponse = this.mRil.processResponse(4, radioResponseInfo);
        if (processResponse != null) {
            List<LinkCapacityEstimate> convertHalLceData = RILUtils.convertHalLceData(lceDataInfo);
            if (radioResponseInfo.error == 0) {
                RadioResponse.sendMessageResponse(processResponse.mResult, convertHalLceData);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, convertHalLceData);
        }
    }

    public void setAllowedNetworkTypesBitmapResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(4, this.mRil, radioResponseInfo);
    }

    public void setBandModeResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(4, this.mRil, radioResponseInfo);
    }

    public void setBarringPasswordResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(4, this.mRil, radioResponseInfo);
    }

    public void setCdmaRoamingPreferenceResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(4, this.mRil, radioResponseInfo);
    }

    public void setCellInfoListRateResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(4, this.mRil, radioResponseInfo);
    }

    public void setIndicationFilterResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(4, this.mRil, radioResponseInfo);
    }

    public void setLinkCapacityReportingCriteriaResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(4, this.mRil, radioResponseInfo);
    }

    public void setLocationUpdatesResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(4, this.mRil, radioResponseInfo);
    }

    public void setNetworkSelectionModeAutomaticResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(4, this.mRil, radioResponseInfo);
    }

    public void setNetworkSelectionModeManualResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(4, this.mRil, radioResponseInfo);
    }

    public void setNrDualConnectivityStateResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(4, this.mRil, radioResponseInfo);
    }

    public void setSignalStrengthReportingCriteriaResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(4, this.mRil, radioResponseInfo);
    }

    public void setSuppServiceNotificationsResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(4, this.mRil, radioResponseInfo);
    }

    public void setSystemSelectionChannelsResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(4, this.mRil, radioResponseInfo);
    }

    public void startNetworkScanResponse(RadioResponseInfo radioResponseInfo) {
        RILRequest processResponse = this.mRil.processResponse(4, radioResponseInfo);
        if (processResponse != null) {
            NetworkScanResult networkScanResult = null;
            if (radioResponseInfo.error == 0) {
                NetworkScanResult networkScanResult2 = new NetworkScanResult(1, 0, (List) null);
                RadioResponse.sendMessageResponse(processResponse.mResult, networkScanResult2);
                networkScanResult = networkScanResult2;
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, networkScanResult);
        }
    }

    public void stopNetworkScanResponse(RadioResponseInfo radioResponseInfo) {
        RILRequest processResponse = this.mRil.processResponse(4, radioResponseInfo);
        if (processResponse != null) {
            NetworkScanResult networkScanResult = null;
            if (radioResponseInfo.error == 0) {
                NetworkScanResult networkScanResult2 = new NetworkScanResult(1, 0, (List) null);
                RadioResponse.sendMessageResponse(processResponse.mResult, networkScanResult2);
                networkScanResult = networkScanResult2;
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, networkScanResult);
        }
    }

    public void supplyNetworkDepersonalizationResponse(RadioResponseInfo radioResponseInfo, int i) {
        RadioResponse.responseInts(4, this.mRil, radioResponseInfo, i);
    }

    public void setUsageSettingResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(4, this.mRil, radioResponseInfo);
    }

    public void getUsageSettingResponse(RadioResponseInfo radioResponseInfo, int i) {
        RadioResponse.responseInts(4, this.mRil, radioResponseInfo, i);
    }

    public void setEmergencyModeResponse(RadioResponseInfo radioResponseInfo, EmergencyRegResult emergencyRegResult) {
        RILRequest processResponse = this.mRil.processResponse(4, radioResponseInfo);
        if (processResponse != null) {
            android.telephony.EmergencyRegResult convertHalEmergencyRegResult = RILUtils.convertHalEmergencyRegResult(emergencyRegResult);
            if (radioResponseInfo.error == 0) {
                RadioResponse.sendMessageResponse(processResponse.mResult, convertHalEmergencyRegResult);
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, convertHalEmergencyRegResult);
        }
    }

    public void triggerEmergencyNetworkScanResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(4, this.mRil, radioResponseInfo);
    }

    public void exitEmergencyModeResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(4, this.mRil, radioResponseInfo);
    }

    public void cancelEmergencyNetworkScanResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(4, this.mRil, radioResponseInfo);
    }

    public void setNullCipherAndIntegrityEnabledResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(4, this.mRil, radioResponseInfo);
    }

    public void isNullCipherAndIntegrityEnabledResponse(RadioResponseInfo radioResponseInfo, boolean z) {
        RILRequest processResponse = this.mRil.processResponse(4, radioResponseInfo);
        if (processResponse != null) {
            if (radioResponseInfo.error == 0) {
                RadioResponse.sendMessageResponse(processResponse.mResult, Boolean.valueOf(z));
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, Boolean.valueOf(z));
        }
    }

    public void isN1ModeEnabledResponse(RadioResponseInfo radioResponseInfo, boolean z) {
        RILRequest processResponse = this.mRil.processResponse(4, radioResponseInfo);
        if (processResponse != null) {
            if (radioResponseInfo.error == 0) {
                RadioResponse.sendMessageResponse(processResponse.mResult, Boolean.valueOf(z));
            }
            this.mRil.processResponseDone(processResponse, radioResponseInfo, Boolean.valueOf(z));
        }
    }

    public void setN1ModeEnabledResponse(RadioResponseInfo radioResponseInfo) {
        RadioResponse.responseVoid(4, this.mRil, radioResponseInfo);
    }
}
