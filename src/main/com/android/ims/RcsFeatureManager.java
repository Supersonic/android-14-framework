package com.android.ims;

import android.content.Context;
import android.net.Uri;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.os.ServiceSpecificException;
import android.telephony.BinderCacheManager;
import android.telephony.CarrierConfigManager;
import android.telephony.TelephonyFrameworkInitializer;
import android.telephony.ims.ImsException;
import android.telephony.ims.SipDetails;
import android.telephony.ims.aidl.ICapabilityExchangeEventListener;
import android.telephony.ims.aidl.IImsCapabilityCallback;
import android.telephony.ims.aidl.IImsConfig;
import android.telephony.ims.aidl.IImsRcsController;
import android.telephony.ims.aidl.IImsRcsFeature;
import android.telephony.ims.aidl.IImsRegistration;
import android.telephony.ims.aidl.IImsRegistrationCallback;
import android.telephony.ims.aidl.IOptionsRequestCallback;
import android.telephony.ims.aidl.IOptionsResponseCallback;
import android.telephony.ims.aidl.IPublishResponseCallback;
import android.telephony.ims.aidl.ISipTransport;
import android.telephony.ims.aidl.ISubscribeResponseCallback;
import android.telephony.ims.feature.CapabilityChangeRequest;
import android.telephony.ims.feature.RcsFeature;
import android.util.Log;
import com.android.ims.FeatureConnector;
import com.android.ims.RcsFeatureManager;
import com.android.ims.internal.IImsServiceFeatureCallback;
import com.android.telephony.Rlog;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class RcsFeatureManager implements FeatureUpdates {
    private static final int CAPABILITY_OPTIONS = 1;
    private static final int CAPABILITY_PRESENCE = 2;
    private static boolean DBG = true;
    private static final String TAG = "RcsFeatureManager";
    private final Context mContext;
    public RcsFeatureConnection mRcsFeatureConnection;
    private final int mSlotId;
    private ICapabilityExchangeEventListener mCapabilityEventListener = new C00131();
    private final Set<CapabilityExchangeEventCallback> mCapabilityEventCallback = new CopyOnWriteArraySet();
    private final BinderCacheManager<IImsRcsController> mBinderCache = new BinderCacheManager<>(new BinderCacheManager.BinderInterfaceFactory() { // from class: com.android.ims.RcsFeatureManager$$ExternalSyntheticLambda0
        public final Object create() {
            IImsRcsController iImsRcsControllerInterface;
            iImsRcsControllerInterface = RcsFeatureManager.getIImsRcsControllerInterface();
            return iImsRcsControllerInterface;
        }
    });

    /* loaded from: classes.dex */
    public interface CapabilityExchangeEventCallback {
        void onPublishUpdated(SipDetails sipDetails);

        void onRemoteCapabilityRequest(Uri uri, List<String> list, IOptionsRequestCallback iOptionsRequestCallback);

        void onRequestPublishCapabilities(int i);

        void onUnpublish();
    }

    /* renamed from: $r8$lambda$86rofOLu-ck_1RjX7GPToMN43ZY  reason: not valid java name */
    public static /* synthetic */ RcsFeatureManager m65$r8$lambda$86rofOLuck_1RjX7GPToMN43ZY(Context context, int i) {
        return new RcsFeatureManager(context, i);
    }

    /* renamed from: com.android.ims.RcsFeatureManager$1 */
    /* loaded from: classes.dex */
    class C00131 extends ICapabilityExchangeEventListener.Stub {
        C00131() {
        }

        public void onRequestPublishCapabilities(final int type) {
            RcsFeatureManager.this.mCapabilityEventCallback.forEach(new Consumer() { // from class: com.android.ims.RcsFeatureManager$1$$ExternalSyntheticLambda2
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((RcsFeatureManager.CapabilityExchangeEventCallback) obj).onRequestPublishCapabilities(type);
                }
            });
        }

        public void onUnpublish() {
            RcsFeatureManager.this.mCapabilityEventCallback.forEach(new Consumer() { // from class: com.android.ims.RcsFeatureManager$1$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((RcsFeatureManager.CapabilityExchangeEventCallback) obj).onUnpublish();
                }
            });
        }

        public void onPublishUpdated(final SipDetails details) {
            RcsFeatureManager.this.mCapabilityEventCallback.forEach(new Consumer() { // from class: com.android.ims.RcsFeatureManager$1$$ExternalSyntheticLambda3
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((RcsFeatureManager.CapabilityExchangeEventCallback) obj).onPublishUpdated(details);
                }
            });
        }

        public void onRemoteCapabilityRequest(final Uri contactUri, final List<String> remoteCapabilities, final IOptionsRequestCallback cb) {
            RcsFeatureManager.this.mCapabilityEventCallback.forEach(new Consumer() { // from class: com.android.ims.RcsFeatureManager$1$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((RcsFeatureManager.CapabilityExchangeEventCallback) obj).onRemoteCapabilityRequest(contactUri, remoteCapabilities, cb);
                }
            });
        }
    }

    public static FeatureConnector<RcsFeatureManager> getConnector(Context context, int slotId, FeatureConnector.Listener<RcsFeatureManager> listener, Executor executor, String logPrefix) {
        ArrayList<Integer> filter = new ArrayList<>();
        filter.add(2);
        return new FeatureConnector<>(context, slotId, new FeatureConnector.ManagerFactory() { // from class: com.android.ims.RcsFeatureManager$$ExternalSyntheticLambda2
            @Override // com.android.ims.FeatureConnector.ManagerFactory
            public final FeatureUpdates createManager(Context context2, int i) {
                return RcsFeatureManager.m65$r8$lambda$86rofOLuck_1RjX7GPToMN43ZY(context2, i);
            }
        }, logPrefix, filter, listener, executor);
    }

    private RcsFeatureManager(Context context, int slotId) {
        this.mContext = context;
        this.mSlotId = slotId;
    }

    public void openConnection() throws ImsException {
        try {
            this.mRcsFeatureConnection.setCapabilityExchangeEventListener(this.mCapabilityEventListener);
        } catch (RemoteException e) {
            throw new ImsException("Service is not available.", 1);
        }
    }

    public void releaseConnection() {
        try {
            this.mRcsFeatureConnection.setCapabilityExchangeEventListener(null);
        } catch (RemoteException e) {
        }
        this.mRcsFeatureConnection.close();
        this.mCapabilityEventCallback.clear();
    }

    public void addCapabilityEventCallback(CapabilityExchangeEventCallback listener) {
        this.mCapabilityEventCallback.add(listener);
    }

    public void removeCapabilityEventCallback(CapabilityExchangeEventCallback listener) {
        this.mCapabilityEventCallback.remove(listener);
    }

    public void updateCapabilities(int newSubId) throws ImsException {
        boolean optionsSupport = isOptionsSupported(newSubId);
        boolean presenceSupported = isPresenceSupported(newSubId);
        logi("Update capabilities for slot " + this.mSlotId + " and sub " + newSubId + ": options=" + optionsSupport + ", presence=" + presenceSupported);
        if (optionsSupport || presenceSupported) {
            CapabilityChangeRequest request = new CapabilityChangeRequest();
            if (optionsSupport) {
                addRcsUceCapability(request, 1);
            }
            if (presenceSupported) {
                addRcsUceCapability(request, 2);
            }
            sendCapabilityChangeRequest(request);
            return;
        }
        disableAllRcsUceCapabilities();
    }

    public void registerImsRegistrationCallback(int subId, IImsRegistrationCallback callback) throws ImsException {
        try {
            this.mRcsFeatureConnection.addCallbackForSubscription(subId, callback);
        } catch (IllegalStateException e) {
            loge("registerImsRegistrationCallback error: ", e);
            throw new ImsException("Can not register callback", 1);
        }
    }

    public void registerImsRegistrationCallback(IImsRegistrationCallback callback) throws ImsException {
        try {
            this.mRcsFeatureConnection.addCallback(callback);
        } catch (IllegalStateException e) {
            loge("registerImsRegistrationCallback error: ", e);
            throw new ImsException("Can not register callback", 1);
        }
    }

    public void unregisterImsRegistrationCallback(int subId, IImsRegistrationCallback callback) {
        this.mRcsFeatureConnection.removeCallbackForSubscription(subId, callback);
    }

    public void unregisterImsRegistrationCallback(IImsRegistrationCallback callback) {
        this.mRcsFeatureConnection.removeCallback(callback);
    }

    public void getImsRegistrationTech(Consumer<Integer> callback) {
        try {
            int tech = this.mRcsFeatureConnection.getRegistrationTech();
            callback.accept(Integer.valueOf(tech));
        } catch (RemoteException e) {
            loge("getImsRegistrationTech error: ", e);
            callback.accept(-1);
        }
    }

    public void registerRcsAvailabilityCallback(int subId, IImsCapabilityCallback callback) throws ImsException {
        try {
            this.mRcsFeatureConnection.addCallbackForSubscription(subId, callback);
        } catch (IllegalStateException e) {
            loge("registerRcsAvailabilityCallback: ", e);
            throw new ImsException("Can not register callback", 1);
        }
    }

    public void unregisterRcsAvailabilityCallback(int subId, IImsCapabilityCallback callback) {
        this.mRcsFeatureConnection.removeCallbackForSubscription(subId, callback);
    }

    public boolean isImsServiceCapable(long capabilities) throws ImsException {
        try {
            return this.mRcsFeatureConnection.isCapable(capabilities);
        } catch (RemoteException e) {
            throw new ImsException(e.getMessage(), 1);
        }
    }

    public ISipTransport getSipTransport() throws ImsException {
        if (!isImsServiceCapable(2L)) {
            return null;
        }
        return this.mRcsFeatureConnection.getSipTransport();
    }

    public IImsRegistration getImsRegistration() {
        return this.mRcsFeatureConnection.getRegistration();
    }

    public boolean isCapable(final int capability, final int radioTech) throws ImsException {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Boolean> capableRef = new AtomicReference<>();
        IImsCapabilityCallback callback = new IImsCapabilityCallback.Stub() { // from class: com.android.ims.RcsFeatureManager.2
            public void onQueryCapabilityConfiguration(int resultCapability, int resultRadioTech, boolean enabled) {
                if (capability != resultCapability || radioTech != resultRadioTech) {
                    return;
                }
                if (RcsFeatureManager.DBG) {
                    RcsFeatureManager.this.log("capable result:capability=" + capability + ", enabled=" + enabled);
                }
                capableRef.set(Boolean.valueOf(enabled));
                latch.countDown();
            }

            public void onCapabilitiesStatusChanged(int config) {
            }

            public void onChangeCapabilityConfigurationError(int capability2, int radioTech2, int reason) {
            }
        };
        try {
            if (DBG) {
                log("Query capability: " + capability + ", radioTech=" + radioTech);
            }
            this.mRcsFeatureConnection.queryCapabilityConfiguration(capability, radioTech, callback);
            return ((Boolean) awaitResult(latch, capableRef)).booleanValue();
        } catch (RemoteException e) {
            loge("isCapable error: ", e);
            throw new ImsException("Can not determine capabilities", 1);
        }
    }

    private static <T> T awaitResult(CountDownLatch latch, AtomicReference<T> resultRef) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return resultRef.get();
    }

    public boolean isAvailable(int capability, int radioTech) throws ImsException {
        try {
            if (this.mRcsFeatureConnection.getRegistrationTech() != radioTech) {
                return false;
            }
            int currentStatus = this.mRcsFeatureConnection.queryCapabilityStatus();
            return new RcsFeature.RcsImsCapabilities(currentStatus).isCapable(capability);
        } catch (RemoteException e) {
            loge("isAvailable error: ", e);
            throw new ImsException("Can not determine availability", 1);
        }
    }

    public void addRcsUceCapability(CapabilityChangeRequest request, int capability) {
        request.addCapabilitiesToEnableForTech(capability, 3);
        request.addCapabilitiesToEnableForTech(capability, 0);
        request.addCapabilitiesToEnableForTech(capability, 1);
    }

    public void requestPublication(String pidfXml, IPublishResponseCallback responseCallback) throws RemoteException {
        this.mRcsFeatureConnection.requestPublication(pidfXml, responseCallback);
    }

    public void requestCapabilities(List<Uri> uris, ISubscribeResponseCallback c) throws RemoteException {
        this.mRcsFeatureConnection.requestCapabilities(uris, c);
    }

    public void sendOptionsCapabilityRequest(Uri contactUri, List<String> myCapabilities, IOptionsResponseCallback callback) throws RemoteException {
        this.mRcsFeatureConnection.sendOptionsCapabilityRequest(contactUri, myCapabilities, callback);
    }

    private void disableAllRcsUceCapabilities() throws ImsException {
        CapabilityChangeRequest request = new CapabilityChangeRequest();
        request.addCapabilitiesToDisableForTech(1, 3);
        request.addCapabilitiesToDisableForTech(1, 0);
        request.addCapabilitiesToDisableForTech(1, 1);
        request.addCapabilitiesToDisableForTech(2, 3);
        request.addCapabilitiesToDisableForTech(2, 0);
        request.addCapabilitiesToDisableForTech(2, 1);
        sendCapabilityChangeRequest(request);
    }

    private void sendCapabilityChangeRequest(CapabilityChangeRequest request) throws ImsException {
        try {
            if (DBG) {
                log("sendCapabilityChangeRequest: " + request);
            }
            this.mRcsFeatureConnection.changeEnabledCapabilities(request, null);
        } catch (RemoteException e) {
            throw new ImsException("Can not connect to service", 1);
        }
    }

    private boolean isOptionsSupported(int subId) {
        return isCapabilityTypeSupported(this.mContext, subId, 1);
    }

    private boolean isPresenceSupported(int subId) {
        return isCapabilityTypeSupported(this.mContext, subId, 2);
    }

    private static boolean isCapabilityTypeSupported(Context context, int subId, int capabilityType) {
        if (subId == -1) {
            Log.e(TAG, "isCapabilityTypeSupported: Invalid subId=" + subId);
            return false;
        }
        CarrierConfigManager configManager = (CarrierConfigManager) context.getSystemService("carrier_config");
        if (configManager == null) {
            Log.e(TAG, "isCapabilityTypeSupported: CarrierConfigManager is null, " + subId);
            return false;
        }
        PersistableBundle b = configManager.getConfigForSubId(subId);
        if (b == null) {
            Log.e(TAG, "isCapabilityTypeSupported: PersistableBundle is null, " + subId);
            return false;
        } else if (capabilityType == 1) {
            return b.getBoolean("use_rcs_sip_options_bool", false);
        } else {
            if (capabilityType == 2) {
                return b.getBoolean("ims.enable_presence_publish_bool", false);
            }
            return false;
        }
    }

    @Override // com.android.ims.FeatureUpdates
    public void registerFeatureCallback(int slotId, final IImsServiceFeatureCallback cb) {
        IImsRcsController controller = this.mBinderCache.listenOnBinder(cb, new Runnable() { // from class: com.android.ims.RcsFeatureManager$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                cb.imsFeatureRemoved(3);
            }
        });
        try {
            if (controller == null) {
                Log.e(TAG, "registerRcsFeatureListener: IImsRcsController is null");
                cb.imsFeatureRemoved(3);
                return;
            }
            controller.registerRcsFeatureCallback(slotId, cb);
        } catch (ServiceSpecificException e) {
            try {
                switch (e.errorCode) {
                    case 2:
                        cb.imsFeatureRemoved(2);
                        break;
                    default:
                        cb.imsFeatureRemoved(3);
                        break;
                }
            } catch (RemoteException e2) {
            }
        } catch (RemoteException e3) {
            try {
                cb.imsFeatureRemoved(3);
            } catch (RemoteException e4) {
            }
        }
    }

    @Override // com.android.ims.FeatureUpdates
    public void unregisterFeatureCallback(IImsServiceFeatureCallback cb) {
        try {
            IImsRcsController imsRcsController = this.mBinderCache.removeRunnable(cb);
            if (imsRcsController != null) {
                imsRcsController.unregisterImsFeatureCallback(cb);
            }
        } catch (RemoteException e) {
            Rlog.e(TAG, "unregisterImsFeatureCallback (RCS), RemoteException: " + e.getMessage());
        }
    }

    private IImsRcsController getIImsRcsController() {
        return this.mBinderCache.getBinder();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static IImsRcsController getIImsRcsControllerInterface() {
        IBinder binder = TelephonyFrameworkInitializer.getTelephonyServiceManager().getTelephonyImsServiceRegisterer().get();
        IImsRcsController c = IImsRcsController.Stub.asInterface(binder);
        return c;
    }

    @Override // com.android.ims.FeatureUpdates
    public void associate(ImsFeatureContainer c, int subId) {
        IImsRcsFeature f = IImsRcsFeature.Stub.asInterface(c.imsFeature);
        this.mRcsFeatureConnection = new RcsFeatureConnection(this.mContext, this.mSlotId, subId, f, c.imsConfig, c.imsRegistration, c.sipTransport);
    }

    @Override // com.android.ims.FeatureUpdates
    public void invalidate() {
        this.mRcsFeatureConnection.onRemovedOrDied();
    }

    @Override // com.android.ims.FeatureUpdates
    public void updateFeatureState(int state) {
        this.mRcsFeatureConnection.updateFeatureState(state);
    }

    @Override // com.android.ims.FeatureUpdates
    public void updateFeatureCapabilities(long capabilities) {
        this.mRcsFeatureConnection.updateFeatureCapabilities(capabilities);
    }

    public IImsConfig getConfig() {
        return this.mRcsFeatureConnection.getConfig();
    }

    public int getSubId() {
        return this.mRcsFeatureConnection.getSubId();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void log(String s) {
        Rlog.d("RcsFeatureManager [" + this.mSlotId + "]", s);
    }

    private void logi(String s) {
        Rlog.i("RcsFeatureManager [" + this.mSlotId + "]", s);
    }

    private void loge(String s) {
        Rlog.e("RcsFeatureManager [" + this.mSlotId + "]", s);
    }

    private void loge(String s, Throwable t) {
        Rlog.e("RcsFeatureManager [" + this.mSlotId + "]", s, t);
    }
}
