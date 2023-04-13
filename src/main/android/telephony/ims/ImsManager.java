package android.telephony.ims;

import android.annotation.SystemApi;
import android.content.Context;
import android.telephony.BinderCacheManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyFrameworkInitializer;
import android.telephony.ims.aidl.IImsRcsController;
import com.android.internal.telephony.ITelephony;
/* loaded from: classes3.dex */
public class ImsManager {
    public static final String ACTION_FORBIDDEN_NO_SERVICE_AUTHORIZATION = "com.android.internal.intent.action.ACTION_FORBIDDEN_NO_SERVICE_AUTHORIZATION";
    public static final String ACTION_WFC_IMS_REGISTRATION_ERROR = "android.telephony.ims.action.WFC_IMS_REGISTRATION_ERROR";
    public static final String EXTRA_WFC_REGISTRATION_FAILURE_MESSAGE = "android.telephony.ims.extra.WFC_REGISTRATION_FAILURE_MESSAGE";
    public static final String EXTRA_WFC_REGISTRATION_FAILURE_TITLE = "android.telephony.ims.extra.WFC_REGISTRATION_FAILURE_TITLE";
    private final Context mContext;
    private static final BinderCacheManager<ITelephony> sTelephonyCache = new BinderCacheManager<>(new BinderCacheManager.BinderInterfaceFactory() { // from class: android.telephony.ims.ImsManager$$ExternalSyntheticLambda0
        @Override // android.telephony.BinderCacheManager.BinderInterfaceFactory
        public final Object create() {
            ITelephony iTelephonyInterface;
            iTelephonyInterface = ImsManager.getITelephonyInterface();
            return iTelephonyInterface;
        }
    });
    private static final BinderCacheManager<IImsRcsController> sRcsCache = new BinderCacheManager<>(new BinderCacheManager.BinderInterfaceFactory() { // from class: android.telephony.ims.ImsManager$$ExternalSyntheticLambda1
        @Override // android.telephony.BinderCacheManager.BinderInterfaceFactory
        public final Object create() {
            IImsRcsController iImsRcsControllerInterface;
            iImsRcsControllerInterface = ImsManager.getIImsRcsControllerInterface();
            return iImsRcsControllerInterface;
        }
    });

    public ImsManager(Context context) {
        this.mContext = context;
    }

    public ImsRcsManager getImsRcsManager(int subscriptionId) {
        if (!SubscriptionManager.isValidSubscriptionId(subscriptionId)) {
            throw new IllegalArgumentException("Invalid subscription ID: " + subscriptionId);
        }
        return new ImsRcsManager(this.mContext, subscriptionId, sRcsCache, sTelephonyCache);
    }

    public ImsMmTelManager getImsMmTelManager(int subscriptionId) {
        if (!SubscriptionManager.isValidSubscriptionId(subscriptionId)) {
            throw new IllegalArgumentException("Invalid subscription ID: " + subscriptionId);
        }
        return new ImsMmTelManager(this.mContext, subscriptionId, sTelephonyCache);
    }

    @SystemApi
    public SipDelegateManager getSipDelegateManager(int subscriptionId) {
        if (!SubscriptionManager.isValidSubscriptionId(subscriptionId)) {
            throw new IllegalArgumentException("Invalid subscription ID: " + subscriptionId);
        }
        return new SipDelegateManager(this.mContext, subscriptionId, sRcsCache, sTelephonyCache);
    }

    public ProvisioningManager getProvisioningManager(int subscriptionId) {
        if (!SubscriptionManager.isValidSubscriptionId(subscriptionId)) {
            throw new IllegalArgumentException("Invalid subscription ID: " + subscriptionId);
        }
        return new ProvisioningManager(subscriptionId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static IImsRcsController getIImsRcsControllerInterface() {
        return IImsRcsController.Stub.asInterface(TelephonyFrameworkInitializer.getTelephonyServiceManager().getTelephonyImsServiceRegisterer().get());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static ITelephony getITelephonyInterface() {
        return ITelephony.Stub.asInterface(TelephonyFrameworkInitializer.getTelephonyServiceManager().getTelephonyServiceRegisterer().get());
    }
}
