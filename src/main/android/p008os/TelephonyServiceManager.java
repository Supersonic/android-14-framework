package android.p008os;

import android.content.Context;
import android.p008os.ServiceManager;
/* renamed from: android.os.TelephonyServiceManager */
/* loaded from: classes3.dex */
public class TelephonyServiceManager {

    /* renamed from: android.os.TelephonyServiceManager$ServiceRegisterer */
    /* loaded from: classes3.dex */
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

    /* renamed from: android.os.TelephonyServiceManager$ServiceNotFoundException */
    /* loaded from: classes3.dex */
    public static class ServiceNotFoundException extends ServiceManager.ServiceNotFoundException {
        public ServiceNotFoundException(String name) {
            super(name);
        }
    }

    public ServiceRegisterer getTelephonyServiceRegisterer() {
        return new ServiceRegisterer("phone");
    }

    public ServiceRegisterer getTelephonyImsServiceRegisterer() {
        return new ServiceRegisterer(Context.TELEPHONY_IMS_SERVICE);
    }

    public ServiceRegisterer getTelephonyRcsMessageServiceRegisterer() {
        return new ServiceRegisterer(Context.TELEPHONY_RCS_MESSAGE_SERVICE);
    }

    public ServiceRegisterer getSubscriptionServiceRegisterer() {
        return new ServiceRegisterer("isub");
    }

    public ServiceRegisterer getPhoneSubServiceRegisterer() {
        return new ServiceRegisterer("iphonesubinfo");
    }

    public ServiceRegisterer getOpportunisticNetworkServiceRegisterer() {
        return new ServiceRegisterer("ions");
    }

    public ServiceRegisterer getCarrierConfigServiceRegisterer() {
        return new ServiceRegisterer("carrier_config");
    }

    public ServiceRegisterer getSmsServiceRegisterer() {
        return new ServiceRegisterer("isms");
    }

    public ServiceRegisterer getEuiccControllerService() {
        return new ServiceRegisterer("econtroller");
    }

    public ServiceRegisterer getEuiccCardControllerServiceRegisterer() {
        return new ServiceRegisterer("euicc_card_controller");
    }

    public ServiceRegisterer getIccPhoneBookServiceRegisterer() {
        return new ServiceRegisterer("simphonebook");
    }
}
