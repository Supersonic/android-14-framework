package com.android.internal.telephony;

import android.hardware.radio.RadioResponseInfo;
import android.hardware.radio.config.IRadioConfigResponse;
import android.hardware.radio.config.PhoneCapability;
import android.hardware.radio.config.SimSlotStatus;
import android.os.RemoteException;
import com.android.internal.telephony.uicc.IccSlotStatus;
import com.android.telephony.Rlog;
import java.util.ArrayList;
import java.util.Set;
/* loaded from: classes.dex */
public class RadioConfigResponseAidl extends IRadioConfigResponse.Stub {
    private final HalVersion mHalVersion;
    private final RadioConfig mRadioConfig;

    public String getInterfaceHash() {
        return "notfrozen";
    }

    public int getInterfaceVersion() {
        return 2;
    }

    public RadioConfigResponseAidl(RadioConfig radioConfig, HalVersion halVersion) {
        this.mRadioConfig = radioConfig;
        this.mHalVersion = halVersion;
    }

    public void getHalDeviceCapabilitiesResponse(RadioResponseInfo radioResponseInfo, boolean z) throws RemoteException {
        RILRequest processResponse = this.mRadioConfig.processResponse(radioResponseInfo);
        if (processResponse != null) {
            Set<String> caps = RILUtils.getCaps(this.mHalVersion, z);
            int i = radioResponseInfo.error;
            if (i == 0) {
                RadioResponse.sendMessageResponse(processResponse.mResult, caps);
                logd(processResponse, RILUtils.requestToString(processResponse.mRequest));
                return;
            }
            processResponse.onError(i, caps);
            loge(processResponse, RILUtils.requestToString(processResponse.mRequest) + " error " + radioResponseInfo.error);
            return;
        }
        loge("getHalDeviceCapabilities: Error " + radioResponseInfo.toString());
    }

    public void getNumOfLiveModemsResponse(RadioResponseInfo radioResponseInfo, byte b) throws RemoteException {
        RILRequest processResponse = this.mRadioConfig.processResponse(radioResponseInfo);
        if (processResponse != null) {
            int i = radioResponseInfo.error;
            if (i == 0) {
                RadioResponse.sendMessageResponse(processResponse.mResult, Byte.valueOf(b));
                logd(processResponse, RILUtils.requestToString(processResponse.mRequest));
                return;
            }
            processResponse.onError(i, Byte.valueOf(b));
            loge(processResponse, RILUtils.requestToString(processResponse.mRequest) + " error " + radioResponseInfo.error);
            return;
        }
        loge("getNumOfLiveModemsResponse: Error " + radioResponseInfo.toString());
    }

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

    public void getSimSlotsStatusResponse(RadioResponseInfo radioResponseInfo, SimSlotStatus[] simSlotStatusArr) throws RemoteException {
        RILRequest processResponse = this.mRadioConfig.processResponse(radioResponseInfo);
        if (processResponse != null) {
            ArrayList<IccSlotStatus> convertHalSlotStatus = RILUtils.convertHalSlotStatus(simSlotStatusArr);
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

    public void setNumOfLiveModemsResponse(RadioResponseInfo radioResponseInfo) throws RemoteException {
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

    private static void logd(String str) {
        Rlog.d("RadioConfigResponseAidl", str);
    }

    private static void loge(String str) {
        Rlog.e("RadioConfigResponseAidl", str);
    }

    private static void logd(RILRequest rILRequest, String str) {
        logd(rILRequest.serialString() + "< " + str);
    }

    private static void loge(RILRequest rILRequest, String str) {
        loge(rILRequest.serialString() + "< " + str);
    }
}
