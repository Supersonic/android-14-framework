package android.hardware.radio.config.V1_0;

import android.hardware.radio.V1_0.RadioResponseInfo;
import android.internal.hidl.base.V1_0.IBase;
import android.os.IHwBinder;
import android.os.RemoteException;
import java.util.ArrayList;
/* loaded from: classes.dex */
public interface IRadioConfigResponse extends IBase {
    IHwBinder asBinder();

    void getSimSlotsStatusResponse(RadioResponseInfo radioResponseInfo, ArrayList<SimSlotStatus> arrayList) throws RemoteException;

    void setSimSlotsMappingResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;
}
