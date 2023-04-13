package android.p008os;

import android.annotation.SystemApi;
import android.content.Context;
@SystemApi
/* renamed from: android.os.DeviceIdleManager */
/* loaded from: classes3.dex */
public class DeviceIdleManager {
    private final Context mContext;
    private final IDeviceIdleController mService;

    public DeviceIdleManager(Context context, IDeviceIdleController service) {
        this.mContext = context;
        this.mService = service;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public IDeviceIdleController getService() {
        return this.mService;
    }

    public void endIdle(String reason) {
        try {
            this.mService.exitIdle(reason);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public String[] getSystemPowerWhitelistExceptIdle() {
        try {
            return this.mService.getSystemPowerWhitelistExceptIdle();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public String[] getSystemPowerWhitelist() {
        try {
            return this.mService.getSystemPowerWhitelist();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }
}
