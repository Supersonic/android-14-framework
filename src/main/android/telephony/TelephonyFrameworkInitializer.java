package android.telephony;

import android.app.SystemServiceRegistry;
import android.content.Context;
import android.p008os.TelephonyServiceManager;
import android.telephony.euicc.EuiccCardManager;
import android.telephony.euicc.EuiccManager;
import android.telephony.ims.ImsManager;
import android.telephony.satellite.SatelliteManager;
import com.android.internal.util.Preconditions;
/* loaded from: classes3.dex */
public class TelephonyFrameworkInitializer {
    private static volatile TelephonyServiceManager sTelephonyServiceManager;

    private TelephonyFrameworkInitializer() {
    }

    public static void setTelephonyServiceManager(TelephonyServiceManager telephonyServiceManager) {
        Preconditions.checkState(sTelephonyServiceManager == null, "setTelephonyServiceManager called twice!");
        sTelephonyServiceManager = (TelephonyServiceManager) Preconditions.checkNotNull(telephonyServiceManager);
    }

    public static void registerServiceWrappers() {
        SystemServiceRegistry.registerContextAwareService("phone", TelephonyManager.class, new SystemServiceRegistry.ContextAwareServiceProducerWithoutBinder() { // from class: android.telephony.TelephonyFrameworkInitializer$$ExternalSyntheticLambda0
            @Override // android.app.SystemServiceRegistry.ContextAwareServiceProducerWithoutBinder
            public final Object createService(Context context) {
                return TelephonyFrameworkInitializer.lambda$registerServiceWrappers$0(context);
            }
        });
        SystemServiceRegistry.registerContextAwareService(Context.TELEPHONY_SUBSCRIPTION_SERVICE, SubscriptionManager.class, new SystemServiceRegistry.ContextAwareServiceProducerWithoutBinder() { // from class: android.telephony.TelephonyFrameworkInitializer$$ExternalSyntheticLambda1
            @Override // android.app.SystemServiceRegistry.ContextAwareServiceProducerWithoutBinder
            public final Object createService(Context context) {
                return TelephonyFrameworkInitializer.lambda$registerServiceWrappers$1(context);
            }
        });
        SystemServiceRegistry.registerContextAwareService("carrier_config", CarrierConfigManager.class, new SystemServiceRegistry.ContextAwareServiceProducerWithoutBinder() { // from class: android.telephony.TelephonyFrameworkInitializer$$ExternalSyntheticLambda2
            @Override // android.app.SystemServiceRegistry.ContextAwareServiceProducerWithoutBinder
            public final Object createService(Context context) {
                return TelephonyFrameworkInitializer.lambda$registerServiceWrappers$2(context);
            }
        });
        SystemServiceRegistry.registerContextAwareService(Context.EUICC_SERVICE, EuiccManager.class, new SystemServiceRegistry.ContextAwareServiceProducerWithoutBinder() { // from class: android.telephony.TelephonyFrameworkInitializer$$ExternalSyntheticLambda3
            @Override // android.app.SystemServiceRegistry.ContextAwareServiceProducerWithoutBinder
            public final Object createService(Context context) {
                return TelephonyFrameworkInitializer.lambda$registerServiceWrappers$3(context);
            }
        });
        SystemServiceRegistry.registerContextAwareService(Context.EUICC_CARD_SERVICE, EuiccCardManager.class, new SystemServiceRegistry.ContextAwareServiceProducerWithoutBinder() { // from class: android.telephony.TelephonyFrameworkInitializer$$ExternalSyntheticLambda4
            @Override // android.app.SystemServiceRegistry.ContextAwareServiceProducerWithoutBinder
            public final Object createService(Context context) {
                return TelephonyFrameworkInitializer.lambda$registerServiceWrappers$4(context);
            }
        });
        SystemServiceRegistry.registerContextAwareService(Context.TELEPHONY_IMS_SERVICE, ImsManager.class, new SystemServiceRegistry.ContextAwareServiceProducerWithoutBinder() { // from class: android.telephony.TelephonyFrameworkInitializer$$ExternalSyntheticLambda5
            @Override // android.app.SystemServiceRegistry.ContextAwareServiceProducerWithoutBinder
            public final Object createService(Context context) {
                return TelephonyFrameworkInitializer.lambda$registerServiceWrappers$5(context);
            }
        });
        SystemServiceRegistry.registerContextAwareService(Context.SMS_SERVICE, SmsManager.class, new SystemServiceRegistry.ContextAwareServiceProducerWithoutBinder() { // from class: android.telephony.TelephonyFrameworkInitializer$$ExternalSyntheticLambda6
            @Override // android.app.SystemServiceRegistry.ContextAwareServiceProducerWithoutBinder
            public final Object createService(Context context) {
                SmsManager smsManagerForContextAndSubscriptionId;
                smsManagerForContextAndSubscriptionId = SmsManager.getSmsManagerForContextAndSubscriptionId(context, Integer.MAX_VALUE);
                return smsManagerForContextAndSubscriptionId;
            }
        });
        SystemServiceRegistry.registerContextAwareService("satellite", SatelliteManager.class, new SystemServiceRegistry.ContextAwareServiceProducerWithoutBinder() { // from class: android.telephony.TelephonyFrameworkInitializer$$ExternalSyntheticLambda7
            @Override // android.app.SystemServiceRegistry.ContextAwareServiceProducerWithoutBinder
            public final Object createService(Context context) {
                return TelephonyFrameworkInitializer.lambda$registerServiceWrappers$7(context);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ TelephonyManager lambda$registerServiceWrappers$0(Context context) {
        return new TelephonyManager(context);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ SubscriptionManager lambda$registerServiceWrappers$1(Context context) {
        return new SubscriptionManager(context);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ CarrierConfigManager lambda$registerServiceWrappers$2(Context context) {
        return new CarrierConfigManager(context);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ EuiccManager lambda$registerServiceWrappers$3(Context context) {
        return new EuiccManager(context);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ EuiccCardManager lambda$registerServiceWrappers$4(Context context) {
        return new EuiccCardManager(context);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ ImsManager lambda$registerServiceWrappers$5(Context context) {
        return new ImsManager(context);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ SatelliteManager lambda$registerServiceWrappers$7(Context context) {
        return new SatelliteManager(context);
    }

    public static TelephonyServiceManager getTelephonyServiceManager() {
        return sTelephonyServiceManager;
    }
}
