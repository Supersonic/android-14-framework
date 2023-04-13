package android.telephony.ims.aidl;

import android.p008os.RemoteException;
import android.telephony.ims.ImsException;
import android.telephony.ims.stub.RcsCapabilityExchangeImplBase;
import java.util.List;
/* loaded from: classes3.dex */
public class RcsOptionsResponseAidlWrapper implements RcsCapabilityExchangeImplBase.OptionsResponseCallback {
    private final IOptionsResponseCallback mResponseBinder;

    public RcsOptionsResponseAidlWrapper(IOptionsResponseCallback responseBinder) {
        this.mResponseBinder = responseBinder;
    }

    @Override // android.telephony.ims.stub.RcsCapabilityExchangeImplBase.OptionsResponseCallback
    public void onCommandError(int code) {
        try {
            this.mResponseBinder.onCommandError(code);
        } catch (RemoteException e) {
        }
    }

    @Override // android.telephony.ims.stub.RcsCapabilityExchangeImplBase.OptionsResponseCallback
    public void onNetworkResponse(int code, String reason, List<String> theirCaps) throws ImsException {
        try {
            this.mResponseBinder.onNetworkResponse(code, reason, theirCaps);
        } catch (RemoteException e) {
        }
    }
}
