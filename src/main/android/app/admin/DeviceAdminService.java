package android.app.admin;

import android.app.Service;
import android.app.admin.IDeviceAdminService;
import android.content.Intent;
import android.p008os.IBinder;
/* loaded from: classes.dex */
public class DeviceAdminService extends Service {
    private final IDeviceAdminServiceImpl mImpl = new IDeviceAdminServiceImpl();

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        return this.mImpl.asBinder();
    }

    /* loaded from: classes.dex */
    private class IDeviceAdminServiceImpl extends IDeviceAdminService.Stub {
        private IDeviceAdminServiceImpl() {
        }
    }
}
