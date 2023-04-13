package android.provider;

import android.annotation.SystemApi;
import android.p008os.IBinder;
import android.p008os.ServiceManager;
@SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
/* loaded from: classes3.dex */
public class DeviceConfigServiceManager {

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    /* loaded from: classes3.dex */
    public static final class ServiceRegisterer {
        private final String mServiceName;

        public ServiceRegisterer(String serviceName) {
            this.mServiceName = serviceName;
        }

        @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
        public void register(IBinder service) {
            ServiceManager.addService(this.mServiceName, service);
        }

        @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
        public IBinder get() {
            return ServiceManager.getService(this.mServiceName);
        }

        @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
        public IBinder getOrThrow() throws ServiceNotFoundException {
            try {
                return ServiceManager.getServiceOrThrow(this.mServiceName);
            } catch (ServiceManager.ServiceNotFoundException e) {
                throw new ServiceNotFoundException(this.mServiceName);
            }
        }

        @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
        public IBinder tryGet() {
            return ServiceManager.checkService(this.mServiceName);
        }
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    /* loaded from: classes3.dex */
    public static class ServiceNotFoundException extends ServiceManager.ServiceNotFoundException {
        public ServiceNotFoundException(String name) {
            super(name);
        }
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public ServiceRegisterer getDeviceConfigUpdatableServiceRegisterer() {
        return new ServiceRegisterer("device_config_updatable");
    }
}
