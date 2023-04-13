package android.nfc;

import android.annotation.SystemApi;
import android.app.SystemServiceRegistry;
import android.content.Context;
@SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
/* loaded from: classes2.dex */
public class NfcFrameworkInitializer {
    private static volatile NfcServiceManager sNfcServiceManager;

    private NfcFrameworkInitializer() {
    }

    public static void setNfcServiceManager(NfcServiceManager nfcServiceManager) {
        if (sNfcServiceManager != null) {
            throw new IllegalStateException("setNfcServiceManager called twice!");
        }
        if (nfcServiceManager == null) {
            throw new IllegalArgumentException("nfcServiceManager must not be null");
        }
        sNfcServiceManager = nfcServiceManager;
    }

    public static NfcServiceManager getNfcServiceManager() {
        return sNfcServiceManager;
    }

    public static void registerServiceWrappers() {
        SystemServiceRegistry.registerContextAwareService("nfc", NfcManager.class, new SystemServiceRegistry.ContextAwareServiceProducerWithoutBinder() { // from class: android.nfc.NfcFrameworkInitializer$$ExternalSyntheticLambda0
            @Override // android.app.SystemServiceRegistry.ContextAwareServiceProducerWithoutBinder
            public final Object createService(Context context) {
                return NfcFrameworkInitializer.lambda$registerServiceWrappers$0(context);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ NfcManager lambda$registerServiceWrappers$0(Context context) {
        return new NfcManager(context);
    }
}
