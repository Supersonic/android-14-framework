package android.media;

import android.annotation.SystemApi;
import android.p008os.IBinder;
import android.p008os.ServiceManager;
@SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
/* loaded from: classes2.dex */
public class MediaServiceManager {
    private static final String MEDIA_COMMUNICATION_SERVICE = "media_communication";
    private static final String MEDIA_SESSION_SERVICE = "media_session";
    private static final String MEDIA_TRANSCODING_SERVICE = "media.transcoding";

    /* loaded from: classes2.dex */
    public static final class ServiceRegisterer {
        private final boolean mLazyStart;
        private final String mServiceName;

        public ServiceRegisterer(String serviceName, boolean lazyStart) {
            this.mServiceName = serviceName;
            this.mLazyStart = lazyStart;
        }

        public ServiceRegisterer(String serviceName) {
            this(serviceName, false);
        }

        public IBinder get() {
            if (this.mLazyStart) {
                return ServiceManager.waitForService(this.mServiceName);
            }
            return ServiceManager.getService(this.mServiceName);
        }
    }

    public ServiceRegisterer getMediaSessionServiceRegisterer() {
        return new ServiceRegisterer("media_session");
    }

    public ServiceRegisterer getMediaTranscodingServiceRegisterer() {
        return new ServiceRegisterer(MEDIA_TRANSCODING_SERVICE, true);
    }

    public ServiceRegisterer getMediaCommunicationServiceRegisterer() {
        return new ServiceRegisterer("media_communication");
    }
}
