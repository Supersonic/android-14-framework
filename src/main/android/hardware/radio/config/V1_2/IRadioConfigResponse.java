package android.hardware.radio.config.V1_2;

import android.hardware.radio.V1_0.RadioResponseInfo;
import android.os.RemoteException;
import java.util.ArrayList;
/* loaded from: classes.dex */
public interface IRadioConfigResponse extends android.hardware.radio.config.V1_1.IRadioConfigResponse {
    void getSimSlotsStatusResponse_1_2(RadioResponseInfo radioResponseInfo, ArrayList<SimSlotStatus> arrayList) throws RemoteException;
}
