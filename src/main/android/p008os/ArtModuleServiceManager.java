package android.p008os;

import android.annotation.SystemApi;
@SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
/* renamed from: android.os.ArtModuleServiceManager */
/* loaded from: classes3.dex */
public class ArtModuleServiceManager {

    /* renamed from: android.os.ArtModuleServiceManager$ServiceRegisterer */
    /* loaded from: classes3.dex */
    public static final class ServiceRegisterer {
        private final String mServiceName;

        public ServiceRegisterer(String serviceName) {
            this.mServiceName = serviceName;
        }

        public IBinder waitForService() {
            return ServiceManager.waitForService(this.mServiceName);
        }
    }

    public ServiceRegisterer getArtdServiceRegisterer() {
        return new ServiceRegisterer("artd");
    }
}
