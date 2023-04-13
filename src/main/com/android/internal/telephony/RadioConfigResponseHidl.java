package com.android.internal.telephony;

import android.hardware.radio.V1_0.RadioResponseInfo;
import android.hardware.radio.config.V1_0.SimSlotStatus;
import android.hardware.radio.config.V1_1.ModemsConfig;
import android.hardware.radio.config.V1_1.PhoneCapability;
import android.hardware.radio.config.V1_3.IRadioConfigResponse;
import android.os.RemoteException;
import com.android.internal.telephony.uicc.IccSlotStatus;
import com.android.telephony.Rlog;
import java.util.ArrayList;
import java.util.Set;
/* loaded from: classes.dex */
public class RadioConfigResponseHidl extends IRadioConfigResponse.Stub {
    private final HalVersion mHalVersion;
    private final RadioConfig mRadioConfig;

    public RadioConfigResponseHidl(RadioConfig radioConfig, HalVersion halVersion) {
        this.mRadioConfig = radioConfig;
        this.mHalVersion = halVersion;
    }

    @Override // android.hardware.radio.config.V1_0.IRadioConfigResponse
    public void getSimSlotsStatusResponse(RadioResponseInfo radioResponseInfo, ArrayList<SimSlotStatus> arrayList) throws RemoteException {
        RILRequest processResponse = this.mRadioConfig.processResponse(radioResponseInfo);
        if (processResponse != null) {
            ArrayList<IccSlotStatus> convertHalSlotStatus = RILUtils.convertHalSlotStatus(arrayList);
            int i = radioResponseInfo.error;
            if (i == 0) {
                RadioResponse.sendMessageResponse(processResponse.mResult, convertHalSlotStatus);
                logd(processResponse, RILUtils.requestToString(processResponse.mRequest) + " " + convertHalSlotStatus.toString());
                return;
            }
            processResponse.onError(i, convertHalSlotStatus);
            loge(processResponse, RILUtils.requestToString(processResponse.mRequest) + " error " + radioResponseInfo.error);
            return;
        }
        loge("getSimSlotsStatusResponse: Error " + radioResponseInfo.toString());
    }

    @Override // android.hardware.radio.config.V1_0.IRadioConfigResponse
    public void setSimSlotsMappingResponse(RadioResponseInfo radioResponseInfo) throws RemoteException {
        RILRequest processResponse = this.mRadioConfig.processResponse(radioResponseInfo);
        if (processResponse != null) {
            int i = radioResponseInfo.error;
            if (i == 0) {
                RadioResponse.sendMessageResponse(processResponse.mResult, null);
                logd(processResponse, RILUtils.requestToString(processResponse.mRequest));
                return;
            }
            processResponse.onError(i, null);
            loge(processResponse, RILUtils.requestToString(processResponse.mRequest) + " error " + radioResponseInfo.error);
            return;
        }
        loge("setSimSlotsMappingResponse: Error " + radioResponseInfo.toString());
    }

    @Override // android.hardware.radio.config.V1_1.IRadioConfigResponse
    public void getPhoneCapabilityResponse(RadioResponseInfo radioResponseInfo, PhoneCapability phoneCapability) throws RemoteException {
        RILRequest processResponse = this.mRadioConfig.processResponse(radioResponseInfo);
        if (processResponse != null) {
            android.telephony.PhoneCapability convertHalPhoneCapability = RILUtils.convertHalPhoneCapability(this.mRadioConfig.getDeviceNrCapabilities(), phoneCapability);
            int i = radioResponseInfo.error;
            if (i == 0) {
                RadioResponse.sendMessageResponse(processResponse.mResult, convertHalPhoneCapability);
                logd(processResponse, RILUtils.requestToString(processResponse.mRequest) + " " + convertHalPhoneCapability.toString());
                return;
            }
            processResponse.onError(i, convertHalPhoneCapability);
            loge(processResponse, RILUtils.requestToString(processResponse.mRequest) + " error " + radioResponseInfo.error);
            return;
        }
        loge("getPhoneCapabilityResponse: Error " + radioResponseInfo.toString());
    }

    @Override // android.hardware.radio.config.V1_1.IRadioConfigResponse
    public void setPreferredDataModemResponse(RadioResponseInfo radioResponseInfo) throws RemoteException {
        RILRequest processResponse = this.mRadioConfig.processResponse(radioResponseInfo);
        if (processResponse != null) {
            int i = radioResponseInfo.error;
            if (i == 0) {
                RadioResponse.sendMessageResponse(processResponse.mResult, null);
                logd(processResponse, RILUtils.requestToString(processResponse.mRequest));
                return;
            }
            processResponse.onError(i, null);
            loge(processResponse, RILUtils.requestToString(processResponse.mRequest) + " error " + radioResponseInfo.error);
            return;
        }
        loge("setPreferredDataModemResponse: Error " + radioResponseInfo.toString());
    }

    @Override // android.hardware.radio.config.V1_1.IRadioConfigResponse
    public void setModemsConfigResponse(RadioResponseInfo radioResponseInfo) throws RemoteException {
        RILRequest processResponse = this.mRadioConfig.processResponse(radioResponseInfo);
        if (processResponse != null) {
            int i = radioResponseInfo.error;
            if (i == 0) {
                RadioResponse.sendMessageResponse(processResponse.mResult, Integer.valueOf(processResponse.mRequest));
                logd(processResponse, RILUtils.requestToString(processResponse.mRequest));
                return;
            }
            processResponse.onError(i, null);
            loge(processResponse, RILUtils.requestToString(processResponse.mRequest) + " error " + radioResponseInfo.error);
            return;
        }
        loge("setModemsConfigResponse: Error " + radioResponseInfo.toString());
    }

    @Override // android.hardware.radio.config.V1_1.IRadioConfigResponse
    public void getModemsConfigResponse(RadioResponseInfo radioResponseInfo, ModemsConfig modemsConfig) throws RemoteException {
        RILRequest processResponse = this.mRadioConfig.processResponse(radioResponseInfo);
        if (processResponse != null) {
            int i = radioResponseInfo.error;
            if (i == 0) {
                RadioResponse.sendMessageResponse(processResponse.mResult, modemsConfig);
                logd(processResponse, RILUtils.requestToString(processResponse.mRequest));
                return;
            }
            processResponse.onError(i, modemsConfig);
            loge(processResponse, RILUtils.requestToString(processResponse.mRequest) + " error " + radioResponseInfo.error);
            return;
        }
        loge("getModemsConfigResponse: Error " + radioResponseInfo.toString());
    }

    @Override // android.hardware.radio.config.V1_2.IRadioConfigResponse
    public void getSimSlotsStatusResponse_1_2(RadioResponseInfo radioResponseInfo, ArrayList<android.hardware.radio.config.V1_2.SimSlotStatus> arrayList) throws RemoteException {
        RILRequest processResponse = this.mRadioConfig.processResponse(radioResponseInfo);
        if (processResponse != null) {
            ArrayList<IccSlotStatus> convertHalSlotStatus = RILUtils.convertHalSlotStatus(arrayList);
            int i = radioResponseInfo.error;
            if (i == 0) {
                RadioResponse.sendMessageResponse(processResponse.mResult, convertHalSlotStatus);
                logd(processResponse, RILUtils.requestToString(processResponse.mRequest) + " " + convertHalSlotStatus.toString());
                return;
            }
            processResponse.onError(i, convertHalSlotStatus);
            loge(processResponse, RILUtils.requestToString(processResponse.mRequest) + " error " + radioResponseInfo.error);
            return;
        }
        loge("getSimSlotsStatusResponse_1_2: Error " + radioResponseInfo.toString());
    }

    @Override // android.hardware.radio.config.V1_3.IRadioConfigResponse
    public void getHalDeviceCapabilitiesResponse(android.hardware.radio.V1_6.RadioResponseInfo radioResponseInfo, boolean z) throws RemoteException {
        RILRequest processResponse_1_6 = this.mRadioConfig.processResponse_1_6(radioResponseInfo);
        if (processResponse_1_6 != null) {
            Set<String> caps = RILUtils.getCaps(this.mHalVersion, z);
            int i = radioResponseInfo.error;
            if (i == 0) {
                RadioResponse.sendMessageResponse(processResponse_1_6.mResult, caps);
                logd(processResponse_1_6, RILUtils.requestToString(processResponse_1_6.mRequest));
                return;
            }
            processResponse_1_6.onError(i, caps);
            loge(processResponse_1_6, RILUtils.requestToString(processResponse_1_6.mRequest) + " error " + radioResponseInfo.error);
            return;
        }
        loge("getHalDeviceCapabilities: Error " + radioResponseInfo.toString());
    }

    private static void logd(String str) {
        Rlog.d("RadioConfigResponse", str);
    }

    private static void loge(String str) {
        Rlog.e("RadioConfigResponse", str);
    }

    private static void logd(RILRequest rILRequest, String str) {
        logd(rILRequest.serialString() + "< " + str);
    }

    private static void loge(RILRequest rILRequest, String str) {
        loge(rILRequest.serialString() + "< " + str);
    }
}
