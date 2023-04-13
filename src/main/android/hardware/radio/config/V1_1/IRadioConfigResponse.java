package android.hardware.radio.config.V1_1;

import android.hardware.radio.V1_0.RadioResponseInfo;
import android.os.RemoteException;
/* loaded from: classes.dex */
public interface IRadioConfigResponse extends android.hardware.radio.config.V1_0.IRadioConfigResponse {
    void getModemsConfigResponse(RadioResponseInfo radioResponseInfo, ModemsConfig modemsConfig) throws RemoteException;

    void getPhoneCapabilityResponse(RadioResponseInfo radioResponseInfo, PhoneCapability phoneCapability) throws RemoteException;

    void setModemsConfigResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setPreferredDataModemResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;
}
