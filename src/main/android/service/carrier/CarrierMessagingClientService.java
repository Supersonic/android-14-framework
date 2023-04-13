package android.service.carrier;

import android.app.Service;
import android.content.Intent;
import android.p008os.IBinder;
import android.service.carrier.ICarrierMessagingClientService;
/* loaded from: classes3.dex */
public class CarrierMessagingClientService extends Service {
    private final ICarrierMessagingClientServiceImpl mImpl = new ICarrierMessagingClientServiceImpl();

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        return this.mImpl.asBinder();
    }

    /* loaded from: classes3.dex */
    private class ICarrierMessagingClientServiceImpl extends ICarrierMessagingClientService.Stub {
        private ICarrierMessagingClientServiceImpl() {
        }
    }
}
