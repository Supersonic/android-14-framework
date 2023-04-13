package android.telephony.ims;

import android.annotation.SystemApi;
import android.content.Context;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Binder;
import android.p008os.RemoteException;
import android.p008os.ServiceSpecificException;
import android.telephony.BinderCacheManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyFrameworkInitializer;
import android.telephony.ims.ImsMmTelManager;
import android.telephony.ims.RegistrationManager;
import android.telephony.ims.aidl.IImsCapabilityCallback;
import android.telephony.ims.feature.MmTelFeature;
import android.util.Log;
import com.android.internal.telephony.IIntegerConsumer;
import com.android.internal.telephony.ITelephony;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
/* loaded from: classes3.dex */
public class ImsMmTelManager implements RegistrationManager {
    private static final String TAG = "ImsMmTelManager";
    public static final int WIFI_MODE_CELLULAR_PREFERRED = 1;
    public static final int WIFI_MODE_UNKNOWN = -1;
    public static final int WIFI_MODE_WIFI_ONLY = 0;
    public static final int WIFI_MODE_WIFI_PREFERRED = 2;
    private static final BinderCacheManager<ITelephony> sTelephonyCache = new BinderCacheManager<>(new BinderCacheManager.BinderInterfaceFactory() { // from class: android.telephony.ims.ImsMmTelManager$$ExternalSyntheticLambda3
        @Override // android.telephony.BinderCacheManager.BinderInterfaceFactory
        public final Object create() {
            ITelephony iTelephonyInterface;
            iTelephonyInterface = ImsMmTelManager.getITelephonyInterface();
            return iTelephonyInterface;
        }
    });
    private final BinderCacheManager<ITelephony> mBinderCache;
    private final Context mContext;
    private final int mSubId;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface WiFiCallingMode {
    }

    @SystemApi
    @Deprecated
    /* loaded from: classes3.dex */
    public static class RegistrationCallback extends RegistrationManager.RegistrationCallback {
        @Override // android.telephony.ims.RegistrationManager.RegistrationCallback
        public void onRegistered(int imsTransportType) {
        }

        @Override // android.telephony.ims.RegistrationManager.RegistrationCallback
        public void onRegistering(int imsTransportType) {
        }

        @Override // android.telephony.ims.RegistrationManager.RegistrationCallback
        public void onUnregistered(ImsReasonInfo info) {
        }

        @Override // android.telephony.ims.RegistrationManager.RegistrationCallback
        public void onTechnologyChangeFailed(int imsTransportType, ImsReasonInfo info) {
        }
    }

    /* loaded from: classes3.dex */
    public static class CapabilityCallback {
        private final CapabilityBinder mBinder = new CapabilityBinder(this);

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public static class CapabilityBinder extends IImsCapabilityCallback.Stub {
            private Executor mExecutor;
            private final CapabilityCallback mLocalCallback;

            CapabilityBinder(CapabilityCallback c) {
                this.mLocalCallback = c;
            }

            @Override // android.telephony.ims.aidl.IImsCapabilityCallback
            public void onCapabilitiesStatusChanged(final int config) {
                if (this.mLocalCallback == null) {
                    return;
                }
                long callingIdentity = Binder.clearCallingIdentity();
                try {
                    this.mExecutor.execute(new Runnable() { // from class: android.telephony.ims.ImsMmTelManager$CapabilityCallback$CapabilityBinder$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            ImsMmTelManager.CapabilityCallback.CapabilityBinder.this.lambda$onCapabilitiesStatusChanged$0(config);
                        }
                    });
                } finally {
                    restoreCallingIdentity(callingIdentity);
                }
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onCapabilitiesStatusChanged$0(int config) {
                this.mLocalCallback.onCapabilitiesStatusChanged(new MmTelFeature.MmTelCapabilities(config));
            }

            @Override // android.telephony.ims.aidl.IImsCapabilityCallback
            public void onQueryCapabilityConfiguration(int capability, int radioTech, boolean isEnabled) {
            }

            @Override // android.telephony.ims.aidl.IImsCapabilityCallback
            public void onChangeCapabilityConfigurationError(int capability, int radioTech, int reason) {
            }

            /* JADX INFO: Access modifiers changed from: private */
            public void setExecutor(Executor executor) {
                this.mExecutor = executor;
            }
        }

        public void onCapabilitiesStatusChanged(MmTelFeature.MmTelCapabilities capabilities) {
        }

        public final IImsCapabilityCallback getBinder() {
            return this.mBinder;
        }

        public final void setExecutor(Executor executor) {
            this.mBinder.setExecutor(executor);
        }
    }

    @SystemApi
    @Deprecated
    public static ImsMmTelManager createForSubscriptionId(int subId) {
        if (!SubscriptionManager.isValidSubscriptionId(subId)) {
            throw new IllegalArgumentException("Invalid subscription ID");
        }
        return new ImsMmTelManager(subId, sTelephonyCache);
    }

    public ImsMmTelManager(int subId, BinderCacheManager<ITelephony> binderCache) {
        this(null, subId, binderCache);
    }

    public ImsMmTelManager(Context context, int subId, BinderCacheManager<ITelephony> binderCache) {
        this.mContext = context;
        this.mSubId = subId;
        this.mBinderCache = binderCache;
    }

    @SystemApi
    @Deprecated
    public void registerImsRegistrationCallback(Executor executor, RegistrationCallback c) throws ImsException {
        if (c == null) {
            throw new IllegalArgumentException("Must include a non-null RegistrationCallback.");
        }
        if (executor == null) {
            throw new IllegalArgumentException("Must include a non-null Executor.");
        }
        c.setExecutor(executor);
        ITelephony iTelephony = getITelephony();
        if (iTelephony == null) {
            throw new ImsException("Could not find Telephony Service.", 1);
        }
        try {
            iTelephony.registerImsRegistrationCallback(this.mSubId, c.getBinder());
        } catch (RemoteException | IllegalStateException e) {
            throw new ImsException(e.getMessage(), 1);
        } catch (ServiceSpecificException e2) {
            if (e2.errorCode == 3) {
                throw new IllegalArgumentException(e2.getMessage());
            }
            throw new ImsException(e2.getMessage(), e2.errorCode);
        }
    }

    @Override // android.telephony.ims.RegistrationManager
    public void registerImsRegistrationCallback(Executor executor, RegistrationManager.RegistrationCallback c) throws ImsException {
        if (c == null) {
            throw new IllegalArgumentException("Must include a non-null RegistrationCallback.");
        }
        if (executor == null) {
            throw new IllegalArgumentException("Must include a non-null Executor.");
        }
        c.setExecutor(executor);
        ITelephony iTelephony = getITelephony();
        if (iTelephony == null) {
            throw new ImsException("Could not find Telephony Service.", 1);
        }
        try {
            iTelephony.registerImsRegistrationCallback(this.mSubId, c.getBinder());
        } catch (RemoteException | IllegalStateException e) {
            throw new ImsException(e.getMessage(), 1);
        } catch (ServiceSpecificException e2) {
            throw new ImsException(e2.getMessage(), e2.errorCode);
        }
    }

    @SystemApi
    @Deprecated
    public void unregisterImsRegistrationCallback(RegistrationCallback c) {
        if (c == null) {
            throw new IllegalArgumentException("Must include a non-null RegistrationCallback.");
        }
        ITelephony iTelephony = getITelephony();
        if (iTelephony == null) {
            throw new RuntimeException("Could not find Telephony Service.");
        }
        try {
            iTelephony.unregisterImsRegistrationCallback(this.mSubId, c.getBinder());
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    @Override // android.telephony.ims.RegistrationManager
    public void unregisterImsRegistrationCallback(RegistrationManager.RegistrationCallback c) {
        if (c == null) {
            throw new IllegalArgumentException("Must include a non-null RegistrationCallback.");
        }
        ITelephony iTelephony = getITelephony();
        if (iTelephony == null) {
            throw new RuntimeException("Could not find Telephony Service.");
        }
        try {
            iTelephony.unregisterImsRegistrationCallback(this.mSubId, c.getBinder());
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    @Override // android.telephony.ims.RegistrationManager
    @SystemApi
    public void getRegistrationState(Executor executor, final Consumer<Integer> stateCallback) {
        if (stateCallback == null) {
            throw new IllegalArgumentException("Must include a non-null callback.");
        }
        if (executor == null) {
            throw new IllegalArgumentException("Must include a non-null Executor.");
        }
        ITelephony iTelephony = getITelephony();
        if (iTelephony == null) {
            throw new RuntimeException("Could not find Telephony Service.");
        }
        try {
            iTelephony.getImsMmTelRegistrationState(this.mSubId, new BinderC32351(executor, stateCallback));
        } catch (RemoteException | ServiceSpecificException e) {
            Log.m104w(TAG, "Error getting registration state: " + e);
            executor.execute(new Runnable() { // from class: android.telephony.ims.ImsMmTelManager$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    stateCallback.accept(0);
                }
            });
        }
    }

    /* renamed from: android.telephony.ims.ImsMmTelManager$1 */
    /* loaded from: classes3.dex */
    class BinderC32351 extends IIntegerConsumer.Stub {
        final /* synthetic */ Executor val$executor;
        final /* synthetic */ Consumer val$stateCallback;

        BinderC32351(Executor executor, Consumer consumer) {
            this.val$executor = executor;
            this.val$stateCallback = consumer;
        }

        @Override // com.android.internal.telephony.IIntegerConsumer
        public void accept(final int result) {
            long identity = Binder.clearCallingIdentity();
            try {
                Executor executor = this.val$executor;
                final Consumer consumer = this.val$stateCallback;
                executor.execute(new Runnable() { // from class: android.telephony.ims.ImsMmTelManager$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        consumer.accept(Integer.valueOf(result));
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }
    }

    @Override // android.telephony.ims.RegistrationManager
    public void getRegistrationTransportType(Executor executor, final Consumer<Integer> transportTypeCallback) {
        if (transportTypeCallback == null) {
            throw new IllegalArgumentException("Must include a non-null callback.");
        }
        if (executor == null) {
            throw new IllegalArgumentException("Must include a non-null Executor.");
        }
        ITelephony iTelephony = getITelephony();
        if (iTelephony == null) {
            throw new RuntimeException("Could not find Telephony Service.");
        }
        try {
            iTelephony.getImsMmTelRegistrationTransportType(this.mSubId, new BinderC32362(executor, transportTypeCallback));
        } catch (RemoteException | ServiceSpecificException e) {
            Log.m104w(TAG, "Error getting transport type: " + e);
            executor.execute(new Runnable() { // from class: android.telephony.ims.ImsMmTelManager$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    transportTypeCallback.accept(-1);
                }
            });
        }
    }

    /* renamed from: android.telephony.ims.ImsMmTelManager$2 */
    /* loaded from: classes3.dex */
    class BinderC32362 extends IIntegerConsumer.Stub {
        final /* synthetic */ Executor val$executor;
        final /* synthetic */ Consumer val$transportTypeCallback;

        BinderC32362(Executor executor, Consumer consumer) {
            this.val$executor = executor;
            this.val$transportTypeCallback = consumer;
        }

        @Override // com.android.internal.telephony.IIntegerConsumer
        public void accept(final int result) {
            long identity = Binder.clearCallingIdentity();
            try {
                Executor executor = this.val$executor;
                final Consumer consumer = this.val$transportTypeCallback;
                executor.execute(new Runnable() { // from class: android.telephony.ims.ImsMmTelManager$2$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        consumer.accept(Integer.valueOf(result));
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }
    }

    public void registerMmTelCapabilityCallback(Executor executor, CapabilityCallback c) throws ImsException {
        if (c == null) {
            throw new IllegalArgumentException("Must include a non-null RegistrationCallback.");
        }
        if (executor == null) {
            throw new IllegalArgumentException("Must include a non-null Executor.");
        }
        c.setExecutor(executor);
        ITelephony iTelephony = getITelephony();
        if (iTelephony == null) {
            throw new ImsException("Could not find Telephony Service.", 1);
        }
        try {
            iTelephony.registerMmTelCapabilityCallback(this.mSubId, c.getBinder());
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        } catch (ServiceSpecificException e2) {
            throw new ImsException(e2.getMessage(), e2.errorCode);
        } catch (IllegalStateException e3) {
            throw new ImsException(e3.getMessage(), 1);
        }
    }

    public void unregisterMmTelCapabilityCallback(CapabilityCallback c) {
        if (c == null) {
            throw new IllegalArgumentException("Must include a non-null RegistrationCallback.");
        }
        ITelephony iTelephony = getITelephony();
        if (iTelephony == null) {
            Log.m104w(TAG, "Could not find Telephony Service.");
            return;
        }
        try {
            iTelephony.unregisterMmTelCapabilityCallback(this.mSubId, c.getBinder());
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    public boolean isAdvancedCallingSettingEnabled() {
        ITelephony iTelephony = getITelephony();
        if (iTelephony == null) {
            throw new RuntimeException("Could not find Telephony Service.");
        }
        try {
            return iTelephony.isAdvancedCallingSettingEnabled(this.mSubId);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        } catch (ServiceSpecificException e2) {
            if (e2.errorCode == 3) {
                throw new IllegalArgumentException(e2.getMessage());
            }
            throw new RuntimeException(e2.getMessage());
        }
    }

    @SystemApi
    public void setAdvancedCallingSettingEnabled(boolean isEnabled) {
        ITelephony iTelephony = getITelephony();
        if (iTelephony == null) {
            throw new RuntimeException("Could not find Telephony Service.");
        }
        try {
            iTelephony.setAdvancedCallingSettingEnabled(this.mSubId, isEnabled);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        } catch (ServiceSpecificException e2) {
            if (e2.errorCode == 3) {
                throw new IllegalArgumentException(e2.getMessage());
            }
            throw new RuntimeException(e2.getMessage());
        }
    }

    @SystemApi
    public boolean isCapable(int capability, int imsRegTech) {
        ITelephony iTelephony = getITelephony();
        if (iTelephony == null) {
            throw new RuntimeException("Could not find Telephony Service.");
        }
        try {
            return iTelephony.isCapable(this.mSubId, capability, imsRegTech);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    @SystemApi
    public boolean isAvailable(int capability, int imsRegTech) {
        ITelephony iTelephony = getITelephony();
        if (iTelephony == null) {
            throw new RuntimeException("Could not find Telephony Service.");
        }
        try {
            return iTelephony.isAvailable(this.mSubId, capability, imsRegTech);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    @SystemApi
    public void isSupported(int capability, int transportType, Executor executor, Consumer<Boolean> callback) throws ImsException {
        if (callback == null) {
            throw new IllegalArgumentException("Must include a non-null Consumer.");
        }
        if (executor == null) {
            throw new IllegalArgumentException("Must include a non-null Executor.");
        }
        ITelephony iTelephony = getITelephony();
        if (iTelephony == null) {
            throw new ImsException("Could not find Telephony Service.", 1);
        }
        try {
            iTelephony.isMmTelCapabilitySupported(this.mSubId, new BinderC32373(executor, callback), capability, transportType);
        } catch (RemoteException e) {
            e.rethrowAsRuntimeException();
        } catch (ServiceSpecificException sse) {
            throw new ImsException(sse.getMessage(), sse.errorCode);
        }
    }

    /* renamed from: android.telephony.ims.ImsMmTelManager$3 */
    /* loaded from: classes3.dex */
    class BinderC32373 extends IIntegerConsumer.Stub {
        final /* synthetic */ Consumer val$callback;
        final /* synthetic */ Executor val$executor;

        BinderC32373(Executor executor, Consumer consumer) {
            this.val$executor = executor;
            this.val$callback = consumer;
        }

        @Override // com.android.internal.telephony.IIntegerConsumer
        public void accept(final int result) {
            long identity = Binder.clearCallingIdentity();
            try {
                Executor executor = this.val$executor;
                final Consumer consumer = this.val$callback;
                executor.execute(new Runnable() { // from class: android.telephony.ims.ImsMmTelManager$3$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        Consumer consumer2 = consumer;
                        int i = result;
                        consumer2.accept(Boolean.valueOf(result == 1));
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }
    }

    public boolean isVtSettingEnabled() {
        ITelephony iTelephony = getITelephony();
        if (iTelephony == null) {
            throw new RuntimeException("Could not find Telephony Service.");
        }
        try {
            return iTelephony.isVtSettingEnabled(this.mSubId);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        } catch (ServiceSpecificException e2) {
            if (e2.errorCode == 3) {
                throw new IllegalArgumentException(e2.getMessage());
            }
            throw new RuntimeException(e2.getMessage());
        }
    }

    @SystemApi
    public void setVtSettingEnabled(boolean isEnabled) {
        ITelephony iTelephony = getITelephony();
        if (iTelephony == null) {
            throw new RuntimeException("Could not find Telephony Service.");
        }
        try {
            iTelephony.setVtSettingEnabled(this.mSubId, isEnabled);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        } catch (ServiceSpecificException e2) {
            if (e2.errorCode == 3) {
                throw new IllegalArgumentException(e2.getMessage());
            }
            throw new RuntimeException(e2.getMessage());
        }
    }

    public boolean isVoWiFiSettingEnabled() {
        ITelephony iTelephony = getITelephony();
        if (iTelephony == null) {
            throw new RuntimeException("Could not find Telephony Service.");
        }
        try {
            return iTelephony.isVoWiFiSettingEnabled(this.mSubId);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        } catch (ServiceSpecificException e2) {
            if (e2.errorCode == 3) {
                throw new IllegalArgumentException(e2.getMessage());
            }
            throw new RuntimeException(e2.getMessage());
        }
    }

    @SystemApi
    public void setVoWiFiSettingEnabled(boolean isEnabled) {
        ITelephony iTelephony = getITelephony();
        if (iTelephony == null) {
            throw new RuntimeException("Could not find Telephony Service.");
        }
        try {
            iTelephony.setVoWiFiSettingEnabled(this.mSubId, isEnabled);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        } catch (ServiceSpecificException e2) {
            if (e2.errorCode == 3) {
                throw new IllegalArgumentException(e2.getMessage());
            }
            throw new RuntimeException(e2.getMessage());
        }
    }

    public boolean isCrossSimCallingEnabled() throws ImsException {
        ITelephony iTelephony = getITelephony();
        if (iTelephony == null) {
            throw new ImsException("Could not find Telephony Service.", 1);
        }
        try {
            return iTelephony.isCrossSimCallingEnabledByUser(this.mSubId);
        } catch (RemoteException e) {
            e.rethrowAsRuntimeException();
            return false;
        } catch (ServiceSpecificException sse) {
            throw new ImsException(sse.getMessage(), sse.errorCode);
        }
    }

    @SystemApi
    public void setCrossSimCallingEnabled(boolean isEnabled) throws ImsException {
        ITelephony iTelephony = getITelephony();
        if (iTelephony == null) {
            throw new ImsException("Could not find Telephony Service.", 1);
        }
        try {
            iTelephony.setCrossSimCallingEnabled(this.mSubId, isEnabled);
        } catch (RemoteException e) {
            e.rethrowAsRuntimeException();
        } catch (ServiceSpecificException sse) {
            throw new ImsException(sse.getMessage(), sse.errorCode);
        }
    }

    public boolean isVoWiFiRoamingSettingEnabled() {
        ITelephony iTelephony = getITelephony();
        if (iTelephony == null) {
            throw new RuntimeException("Could not find Telephony Service.");
        }
        try {
            return iTelephony.isVoWiFiRoamingSettingEnabled(this.mSubId);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        } catch (ServiceSpecificException e2) {
            if (e2.errorCode == 3) {
                throw new IllegalArgumentException(e2.getMessage());
            }
            throw new RuntimeException(e2.getMessage());
        }
    }

    @SystemApi
    public void setVoWiFiRoamingSettingEnabled(boolean isEnabled) {
        ITelephony iTelephony = getITelephony();
        if (iTelephony == null) {
            throw new RuntimeException("Could not find Telephony Service.");
        }
        try {
            iTelephony.setVoWiFiRoamingSettingEnabled(this.mSubId, isEnabled);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        } catch (ServiceSpecificException e2) {
            if (e2.errorCode == 3) {
                throw new IllegalArgumentException(e2.getMessage());
            }
            throw new RuntimeException(e2.getMessage());
        }
    }

    @SystemApi
    public void setVoWiFiNonPersistent(boolean isCapable, int mode) {
        ITelephony iTelephony = getITelephony();
        if (iTelephony == null) {
            throw new RuntimeException("Could not find Telephony Service.");
        }
        try {
            iTelephony.setVoWiFiNonPersistent(this.mSubId, isCapable, mode);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        } catch (ServiceSpecificException e2) {
            if (e2.errorCode == 3) {
                throw new IllegalArgumentException(e2.getMessage());
            }
            throw new RuntimeException(e2.getMessage());
        }
    }

    public int getVoWiFiModeSetting() {
        ITelephony iTelephony = getITelephony();
        if (iTelephony == null) {
            throw new RuntimeException("Could not find Telephony Service.");
        }
        try {
            return iTelephony.getVoWiFiModeSetting(this.mSubId);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        } catch (ServiceSpecificException e2) {
            if (e2.errorCode == 3) {
                throw new IllegalArgumentException(e2.getMessage());
            }
            throw new RuntimeException(e2.getMessage());
        }
    }

    @SystemApi
    public void setVoWiFiModeSetting(int mode) {
        ITelephony iTelephony = getITelephony();
        if (iTelephony == null) {
            throw new RuntimeException("Could not find Telephony Service.");
        }
        try {
            iTelephony.setVoWiFiModeSetting(this.mSubId, mode);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        } catch (ServiceSpecificException e2) {
            if (e2.errorCode == 3) {
                throw new IllegalArgumentException(e2.getMessage());
            }
            throw new RuntimeException(e2.getMessage());
        }
    }

    @SystemApi
    public int getVoWiFiRoamingModeSetting() {
        ITelephony iTelephony = getITelephony();
        if (iTelephony == null) {
            throw new RuntimeException("Could not find Telephony Service.");
        }
        try {
            return iTelephony.getVoWiFiRoamingModeSetting(this.mSubId);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        } catch (ServiceSpecificException e2) {
            if (e2.errorCode == 3) {
                throw new IllegalArgumentException(e2.getMessage());
            }
            throw new RuntimeException(e2.getMessage());
        }
    }

    @SystemApi
    public void setVoWiFiRoamingModeSetting(int mode) {
        ITelephony iTelephony = getITelephony();
        if (iTelephony == null) {
            throw new RuntimeException("Could not find Telephony Service.");
        }
        try {
            iTelephony.setVoWiFiRoamingModeSetting(this.mSubId, mode);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        } catch (ServiceSpecificException e2) {
            if (e2.errorCode == 3) {
                throw new IllegalArgumentException(e2.getMessage());
            }
            throw new RuntimeException(e2.getMessage());
        }
    }

    @SystemApi
    public void setRttCapabilitySetting(boolean isEnabled) {
        ITelephony iTelephony = getITelephony();
        if (iTelephony == null) {
            throw new RuntimeException("Could not find Telephony Service.");
        }
        try {
            iTelephony.setRttCapabilitySetting(this.mSubId, isEnabled);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        } catch (ServiceSpecificException e2) {
            if (e2.errorCode == 3) {
                throw new IllegalArgumentException(e2.getMessage());
            }
            throw new RuntimeException(e2.getMessage());
        }
    }

    public boolean isTtyOverVolteEnabled() {
        ITelephony iTelephony = getITelephony();
        if (iTelephony == null) {
            throw new RuntimeException("Could not find Telephony Service.");
        }
        try {
            return iTelephony.isTtyOverVolteEnabled(this.mSubId);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        } catch (ServiceSpecificException e2) {
            if (e2.errorCode == 3) {
                throw new IllegalArgumentException(e2.getMessage());
            }
            throw new RuntimeException(e2.getMessage());
        }
    }

    @SystemApi
    public void getFeatureState(Executor executor, Consumer<Integer> callback) throws ImsException {
        if (executor == null) {
            throw new IllegalArgumentException("Must include a non-null Executor.");
        }
        if (callback == null) {
            throw new IllegalArgumentException("Must include a non-null Consumer.");
        }
        ITelephony iTelephony = getITelephony();
        if (iTelephony == null) {
            throw new ImsException("Could not find Telephony Service.", 1);
        }
        try {
            iTelephony.getImsMmTelFeatureState(this.mSubId, new BinderC32384(executor, callback));
        } catch (RemoteException e) {
            e.rethrowAsRuntimeException();
        } catch (ServiceSpecificException sse) {
            throw new ImsException(sse.getMessage(), sse.errorCode);
        }
    }

    /* renamed from: android.telephony.ims.ImsMmTelManager$4 */
    /* loaded from: classes3.dex */
    class BinderC32384 extends IIntegerConsumer.Stub {
        final /* synthetic */ Consumer val$callback;
        final /* synthetic */ Executor val$executor;

        BinderC32384(Executor executor, Consumer consumer) {
            this.val$executor = executor;
            this.val$callback = consumer;
        }

        @Override // com.android.internal.telephony.IIntegerConsumer
        public void accept(final int result) {
            long identity = Binder.clearCallingIdentity();
            try {
                Executor executor = this.val$executor;
                final Consumer consumer = this.val$callback;
                executor.execute(new Runnable() { // from class: android.telephony.ims.ImsMmTelManager$4$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        consumer.accept(Integer.valueOf(result));
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }
    }

    public void registerImsStateCallback(Executor executor, ImsStateCallback callback) throws ImsException {
        Objects.requireNonNull(callback, "Must include a non-null ImsStateCallback.");
        Objects.requireNonNull(executor, "Must include a non-null Executor.");
        callback.init(executor);
        BinderCacheManager<ITelephony> binderCacheManager = this.mBinderCache;
        Objects.requireNonNull(callback);
        ITelephony telephony = binderCacheManager.listenOnBinder(callback, new ImsMmTelManager$$ExternalSyntheticLambda2(callback));
        if (telephony == null) {
            throw new ImsException("Telephony server is down", 1);
        }
        try {
            telephony.registerImsStateCallback(this.mSubId, 1, callback.getCallbackBinder(), getOpPackageName());
        } catch (RemoteException | IllegalStateException e) {
            throw new ImsException(e.getMessage(), 1);
        } catch (ServiceSpecificException e2) {
            throw new ImsException(e2.getMessage(), e2.errorCode);
        }
    }

    public void unregisterImsStateCallback(ImsStateCallback callback) {
        Objects.requireNonNull(callback, "Must include a non-null ImsStateCallback.");
        ITelephony telephony = this.mBinderCache.removeRunnable(callback);
        if (telephony != null) {
            try {
                telephony.unregisterImsStateCallback(callback.getCallbackBinder());
            } catch (RemoteException e) {
            }
        }
    }

    private String getOpPackageName() {
        Context context = this.mContext;
        if (context != null) {
            return context.getOpPackageName();
        }
        return null;
    }

    private ITelephony getITelephony() {
        return this.mBinderCache.getBinder();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static ITelephony getITelephonyInterface() {
        ITelephony binder = ITelephony.Stub.asInterface(TelephonyFrameworkInitializer.getTelephonyServiceManager().getTelephonyServiceRegisterer().get());
        return binder;
    }

    public static String wifiCallingModeToString(int mode) {
        switch (mode) {
            case -1:
                return "UNKNOWN";
            case 0:
                return "WIFI_ONLY";
            case 1:
                return "CELLULAR_PREFERRED";
            case 2:
                return "WIFI_PREFERRED";
            default:
                return "UNKNOWN(" + mode + NavigationBarInflaterView.KEY_CODE_END;
        }
    }
}
