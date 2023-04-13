package android.security;

import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.p008os.UserHandle;
import android.service.gatekeeper.IGateKeeperService;
/* loaded from: classes3.dex */
public abstract class GateKeeper {
    public static final long INVALID_SECURE_USER_ID = 0;

    private GateKeeper() {
    }

    public static IGateKeeperService getService() {
        IGateKeeperService service = IGateKeeperService.Stub.asInterface(ServiceManager.getService("android.service.gatekeeper.IGateKeeperService"));
        if (service == null) {
            throw new IllegalStateException("Gatekeeper service not available");
        }
        return service;
    }

    public static long getSecureUserId() throws IllegalStateException {
        try {
            return getService().getSecureUserId(UserHandle.myUserId());
        } catch (RemoteException e) {
            throw new IllegalStateException("Failed to obtain secure user ID from gatekeeper", e);
        }
    }
}
