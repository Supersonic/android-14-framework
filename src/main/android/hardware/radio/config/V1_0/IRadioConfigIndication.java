package android.hardware.radio.config.V1_0;

import android.internal.hidl.base.V1_0.IBase;
import android.os.IHwBinder;
import android.os.RemoteException;
import java.util.ArrayList;
/* loaded from: classes.dex */
public interface IRadioConfigIndication extends IBase {
    IHwBinder asBinder();

    void simSlotsStatusChanged(int i, ArrayList<SimSlotStatus> arrayList) throws RemoteException;
}
