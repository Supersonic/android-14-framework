package android.nfc;

import android.annotation.SystemApi;
import android.p008os.IBinder;
import android.p008os.ServiceManager;
@SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
/* loaded from: classes2.dex */
public class NfcServiceManager {

    /* loaded from: classes2.dex */
    public static final class ServiceRegisterer {
        private final String mServiceName;

        public ServiceRegisterer(String serviceName) {
            this.mServiceName = serviceName;
        }

        public void register(IBinder service) {
            ServiceManager.addService(this.mServiceName, service);
        }

        public IBinder get() {
            return ServiceManager.getService(this.mServiceName);
        }

        public IBinder getOrThrow() throws ServiceNotFoundException {
            try {
                return ServiceManager.getServiceOrThrow(this.mServiceName);
            } catch (ServiceManager.ServiceNotFoundException e) {
                throw new ServiceNotFoundException(this.mServiceName);
            }
        }

        public IBinder tryGet() {
            return ServiceManager.checkService(this.mServiceName);
        }
    }

    /* loaded from: classes2.dex */
    public static class ServiceNotFoundException extends ServiceManager.ServiceNotFoundException {
        public ServiceNotFoundException(String name) {
            super(name);
        }
    }

    public ServiceRegisterer getNfcManagerServiceRegisterer() {
        return new ServiceRegisterer("nfc");
    }
}
