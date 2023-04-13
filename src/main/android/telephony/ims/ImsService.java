package android.telephony.ims;

import android.annotation.SystemApi;
import android.app.PendingIntent$$ExternalSyntheticLambda1;
import android.app.Service;
import android.content.Intent;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.telephony.ims.ImsService;
import android.telephony.ims.aidl.IImsConfig;
import android.telephony.ims.aidl.IImsMmTelFeature;
import android.telephony.ims.aidl.IImsRcsFeature;
import android.telephony.ims.aidl.IImsRegistration;
import android.telephony.ims.aidl.IImsServiceController;
import android.telephony.ims.aidl.IImsServiceControllerListener;
import android.telephony.ims.aidl.ISipTransport;
import android.telephony.ims.feature.ImsFeature;
import android.telephony.ims.feature.MmTelFeature;
import android.telephony.ims.feature.RcsFeature;
import android.telephony.ims.stub.ImsConfigImplBase;
import android.telephony.ims.stub.ImsFeatureConfiguration;
import android.telephony.ims.stub.ImsRegistrationImplBase;
import android.telephony.ims.stub.SipTransportImplBase;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import com.android.ims.internal.IImsFeatureStatusCallback;
import com.android.internal.telephony.util.TelephonyUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
@SystemApi
/* loaded from: classes3.dex */
public class ImsService extends Service {
    public static final long CAPABILITY_EMERGENCY_OVER_MMTEL = 1;
    public static final long CAPABILITY_SIP_DELEGATE_CREATION = 2;
    public static final long CAPABILITY_TERMINAL_BASED_CALL_WAITING = 4;
    private static final String LOG_TAG = "ImsService";
    public static final String SERVICE_INTERFACE = "android.telephony.ims.ImsService";
    private Executor mExecutor;
    private IImsServiceControllerListener mListener;
    public static final long CAPABILITY_MAX_INDEX = Long.numberOfTrailingZeros(4);
    private static final Map<Long, String> CAPABILITIES_LOG_MAP = Map.of(1L, "EMERGENCY_OVER_MMTEL", 2L, "SIP_DELEGATE_CREATION");
    private final SparseArray<SparseArray<ImsFeature>> mFeaturesBySlot = new SparseArray<>();
    private final SparseArray<SparseBooleanArray> mCreateImsFeatureWithSlotIdFlagMap = new SparseArray<>();
    private final Object mListenerLock = new Object();
    private final Object mExecutorLock = new Object();
    protected final IBinder mImsServiceController = new BinderC32451();
    private final IBinder.DeathRecipient mDeathRecipient = new C32462();

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface ImsServiceCapability {
    }

    /* loaded from: classes3.dex */
    public static class Listener extends IImsServiceControllerListener.Stub {
        @Override // android.telephony.ims.aidl.IImsServiceControllerListener
        public void onUpdateSupportedImsFeatures(ImsFeatureConfiguration c) {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.telephony.ims.ImsService$1 */
    /* loaded from: classes3.dex */
    public class BinderC32451 extends IImsServiceController.Stub {
        BinderC32451() {
        }

        @Override // android.telephony.ims.aidl.IImsServiceController
        public void setListener(IImsServiceControllerListener l) {
            synchronized (ImsService.this.mListenerLock) {
                if (ImsService.this.mListener != null && ImsService.this.mListener.asBinder().isBinderAlive()) {
                    try {
                        ImsService.this.mListener.asBinder().unlinkToDeath(ImsService.this.mDeathRecipient, 0);
                    } catch (NoSuchElementException e) {
                        Log.m104w(ImsService.LOG_TAG, "IImsServiceControllerListener does not exist");
                    }
                }
                ImsService.this.mListener = l;
                if (ImsService.this.mListener == null) {
                    ImsService.this.executeMethodAsync(new Runnable() { // from class: android.telephony.ims.ImsService$1$$ExternalSyntheticLambda10
                        @Override // java.lang.Runnable
                        public final void run() {
                            ImsService.BinderC32451.this.lambda$setListener$0();
                        }
                    }, "releaseResource");
                    return;
                }
                try {
                    ImsService.this.mListener.asBinder().linkToDeath(ImsService.this.mDeathRecipient, 0);
                    Log.m108i(ImsService.LOG_TAG, "setListener: register linkToDeath");
                } catch (RemoteException e2) {
                    ImsService.this.executeMethodAsync(new Runnable() { // from class: android.telephony.ims.ImsService$1$$ExternalSyntheticLambda11
                        @Override // java.lang.Runnable
                        public final void run() {
                            ImsService.BinderC32451.this.lambda$setListener$1();
                        }
                    }, "releaseResource");
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$setListener$0() {
            ImsService.this.releaseResource();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$setListener$1() {
            ImsService.this.releaseResource();
        }

        @Override // android.telephony.ims.aidl.IImsServiceController
        public IImsMmTelFeature createMmTelFeature(final int slotId, final int subId) {
            MmTelFeature f = (MmTelFeature) ImsService.this.getImsFeature(slotId, 1);
            if (f == null) {
                return (IImsMmTelFeature) ImsService.this.executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.ImsService$1$$ExternalSyntheticLambda6
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        IImsMmTelFeature lambda$createMmTelFeature$2;
                        lambda$createMmTelFeature$2 = ImsService.BinderC32451.this.lambda$createMmTelFeature$2(slotId, subId);
                        return lambda$createMmTelFeature$2;
                    }
                }, "createMmTelFeature");
            }
            return f.getBinder();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ IImsMmTelFeature lambda$createMmTelFeature$2(int slotId, int subId) {
            return ImsService.this.createMmTelFeatureInternal(slotId, subId);
        }

        @Override // android.telephony.ims.aidl.IImsServiceController
        public IImsMmTelFeature createEmergencyOnlyMmTelFeature(final int slotId) {
            MmTelFeature f = (MmTelFeature) ImsService.this.getImsFeature(slotId, 1);
            if (f == null) {
                return (IImsMmTelFeature) ImsService.this.executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.ImsService$1$$ExternalSyntheticLambda4
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        IImsMmTelFeature lambda$createEmergencyOnlyMmTelFeature$3;
                        lambda$createEmergencyOnlyMmTelFeature$3 = ImsService.BinderC32451.this.lambda$createEmergencyOnlyMmTelFeature$3(slotId);
                        return lambda$createEmergencyOnlyMmTelFeature$3;
                    }
                }, "createEmergencyOnlyMmTelFeature");
            }
            return f.getBinder();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ IImsMmTelFeature lambda$createEmergencyOnlyMmTelFeature$3(int slotId) {
            return ImsService.this.createEmergencyOnlyMmTelFeatureInternal(slotId);
        }

        @Override // android.telephony.ims.aidl.IImsServiceController
        public IImsRcsFeature createRcsFeature(final int slotId, final int subId) {
            RcsFeature f = (RcsFeature) ImsService.this.getImsFeature(slotId, 2);
            if (f == null) {
                return (IImsRcsFeature) ImsService.this.executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.ImsService$1$$ExternalSyntheticLambda7
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        IImsRcsFeature lambda$createRcsFeature$4;
                        lambda$createRcsFeature$4 = ImsService.BinderC32451.this.lambda$createRcsFeature$4(slotId, subId);
                        return lambda$createRcsFeature$4;
                    }
                }, "createRcsFeature");
            }
            return f.getBinder();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ IImsRcsFeature lambda$createRcsFeature$4(int slotId, int subId) {
            return ImsService.this.createRcsFeatureInternal(slotId, subId);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$addFeatureStatusCallback$5(int slotId, int featureType, IImsFeatureStatusCallback c) {
            ImsService.this.addImsFeatureStatusCallback(slotId, featureType, c);
        }

        @Override // android.telephony.ims.aidl.IImsServiceController
        public void addFeatureStatusCallback(final int slotId, final int featureType, final IImsFeatureStatusCallback c) {
            ImsService.this.executeMethodAsync(new Runnable() { // from class: android.telephony.ims.ImsService$1$$ExternalSyntheticLambda14
                @Override // java.lang.Runnable
                public final void run() {
                    ImsService.BinderC32451.this.lambda$addFeatureStatusCallback$5(slotId, featureType, c);
                }
            }, "addFeatureStatusCallback");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$removeFeatureStatusCallback$6(int slotId, int featureType, IImsFeatureStatusCallback c) {
            ImsService.this.removeImsFeatureStatusCallback(slotId, featureType, c);
        }

        @Override // android.telephony.ims.aidl.IImsServiceController
        public void removeFeatureStatusCallback(final int slotId, final int featureType, final IImsFeatureStatusCallback c) {
            ImsService.this.executeMethodAsync(new Runnable() { // from class: android.telephony.ims.ImsService$1$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    ImsService.BinderC32451.this.lambda$removeFeatureStatusCallback$6(slotId, featureType, c);
                }
            }, "removeFeatureStatusCallback");
        }

        @Override // android.telephony.ims.aidl.IImsServiceController
        public void removeImsFeature(final int slotId, final int featureType, boolean changeSubId) {
            if (changeSubId && ImsService.this.isImsFeatureCreatedForSlot(slotId, featureType)) {
                Log.m104w(ImsService.LOG_TAG, "Do not remove Ims feature for compatibility");
                return;
            }
            ImsService.this.executeMethodAsync(new Runnable() { // from class: android.telephony.ims.ImsService$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ImsService.BinderC32451.this.lambda$removeImsFeature$7(slotId, featureType);
                }
            }, "removeImsFeature");
            ImsService.this.setImsFeatureCreatedForSlot(slotId, featureType, false);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$removeImsFeature$7(int slotId, int featureType) {
            ImsService.this.removeImsFeature(slotId, featureType);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ ImsFeatureConfiguration lambda$querySupportedImsFeatures$8() {
            return ImsService.this.querySupportedImsFeatures();
        }

        @Override // android.telephony.ims.aidl.IImsServiceController
        public ImsFeatureConfiguration querySupportedImsFeatures() {
            return (ImsFeatureConfiguration) ImsService.this.executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.ImsService$1$$ExternalSyntheticLambda2
                @Override // java.util.function.Supplier
                public final Object get() {
                    ImsFeatureConfiguration lambda$querySupportedImsFeatures$8;
                    lambda$querySupportedImsFeatures$8 = ImsService.BinderC32451.this.lambda$querySupportedImsFeatures$8();
                    return lambda$querySupportedImsFeatures$8;
                }
            }, "ImsFeatureConfiguration");
        }

        @Override // android.telephony.ims.aidl.IImsServiceController
        public long getImsServiceCapabilities() {
            return ((Long) ImsService.this.executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.ImsService$1$$ExternalSyntheticLambda9
                @Override // java.util.function.Supplier
                public final Object get() {
                    Long lambda$getImsServiceCapabilities$9;
                    lambda$getImsServiceCapabilities$9 = ImsService.BinderC32451.this.lambda$getImsServiceCapabilities$9();
                    return lambda$getImsServiceCapabilities$9;
                }
            }, "getImsServiceCapabilities")).longValue();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ Long lambda$getImsServiceCapabilities$9() {
            long caps = ImsService.this.getImsServiceCapabilities();
            long sanitizedCaps = ImsService.sanitizeCapabilities(caps);
            if (caps != sanitizedCaps) {
                Log.m104w(ImsService.LOG_TAG, "removing invalid bits from field: 0x" + Long.toHexString(caps ^ sanitizedCaps));
            }
            return Long.valueOf(sanitizedCaps);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$notifyImsServiceReadyForFeatureCreation$10() {
            ImsService.this.readyForFeatureCreation();
        }

        @Override // android.telephony.ims.aidl.IImsServiceController
        public void notifyImsServiceReadyForFeatureCreation() {
            ImsService.this.executeMethodAsync(new Runnable() { // from class: android.telephony.ims.ImsService$1$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    ImsService.BinderC32451.this.lambda$notifyImsServiceReadyForFeatureCreation$10();
                }
            }, "notifyImsServiceReadyForFeatureCreation");
        }

        @Override // android.telephony.ims.aidl.IImsServiceController
        public IImsConfig getConfig(final int slotId, final int subId) {
            return (IImsConfig) ImsService.this.executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.ImsService$1$$ExternalSyntheticLambda3
                @Override // java.util.function.Supplier
                public final Object get() {
                    IImsConfig lambda$getConfig$11;
                    lambda$getConfig$11 = ImsService.BinderC32451.this.lambda$getConfig$11(slotId, subId);
                    return lambda$getConfig$11;
                }
            }, "getConfig");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ IImsConfig lambda$getConfig$11(int slotId, int subId) {
            ImsConfigImplBase c = ImsService.this.getConfigForSubscription(slotId, subId);
            if (c != null) {
                c.setDefaultExecutor(ImsService.this.getCachedExecutor());
                return c.getIImsConfig();
            }
            return null;
        }

        @Override // android.telephony.ims.aidl.IImsServiceController
        public IImsRegistration getRegistration(final int slotId, final int subId) {
            return (IImsRegistration) ImsService.this.executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.ImsService$1$$ExternalSyntheticLambda13
                @Override // java.util.function.Supplier
                public final Object get() {
                    IImsRegistration lambda$getRegistration$12;
                    lambda$getRegistration$12 = ImsService.BinderC32451.this.lambda$getRegistration$12(slotId, subId);
                    return lambda$getRegistration$12;
                }
            }, "getRegistration");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ IImsRegistration lambda$getRegistration$12(int slotId, int subId) {
            ImsRegistrationImplBase r = ImsService.this.getRegistrationForSubscription(slotId, subId);
            if (r != null) {
                r.setDefaultExecutor(ImsService.this.getCachedExecutor());
                return r.getBinder();
            }
            return null;
        }

        @Override // android.telephony.ims.aidl.IImsServiceController
        public ISipTransport getSipTransport(final int slotId) {
            return (ISipTransport) ImsService.this.executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.ImsService$1$$ExternalSyntheticLambda1
                @Override // java.util.function.Supplier
                public final Object get() {
                    ISipTransport lambda$getSipTransport$13;
                    lambda$getSipTransport$13 = ImsService.BinderC32451.this.lambda$getSipTransport$13(slotId);
                    return lambda$getSipTransport$13;
                }
            }, "getSipTransport");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ ISipTransport lambda$getSipTransport$13(int slotId) {
            SipTransportImplBase s = ImsService.this.getSipTransport(slotId);
            if (s != null) {
                s.setDefaultExecutor(ImsService.this.getCachedExecutor());
                return s.getBinder();
            }
            return null;
        }

        @Override // android.telephony.ims.aidl.IImsServiceController
        public void enableIms(final int slotId, final int subId) {
            ImsService.this.executeMethodAsync(new Runnable() { // from class: android.telephony.ims.ImsService$1$$ExternalSyntheticLambda15
                @Override // java.lang.Runnable
                public final void run() {
                    ImsService.BinderC32451.this.lambda$enableIms$14(slotId, subId);
                }
            }, "enableIms");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$enableIms$14(int slotId, int subId) {
            ImsService.this.enableImsForSubscription(slotId, subId);
        }

        @Override // android.telephony.ims.aidl.IImsServiceController
        public void disableIms(final int slotId, final int subId) {
            ImsService.this.executeMethodAsync(new Runnable() { // from class: android.telephony.ims.ImsService$1$$ExternalSyntheticLambda12
                @Override // java.lang.Runnable
                public final void run() {
                    ImsService.BinderC32451.this.lambda$disableIms$15(slotId, subId);
                }
            }, "disableIms");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$disableIms$15(int slotId, int subId) {
            ImsService.this.disableImsForSubscription(slotId, subId);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.telephony.ims.ImsService$2 */
    /* loaded from: classes3.dex */
    public class C32462 implements IBinder.DeathRecipient {
        C32462() {
        }

        @Override // android.p008os.IBinder.DeathRecipient
        public void binderDied() {
            Log.m104w(ImsService.LOG_TAG, "IImsServiceControllerListener binder to framework has died. Cleaning up");
            ImsService.this.executeMethodAsync(new Runnable() { // from class: android.telephony.ims.ImsService$2$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ImsService.C32462.this.lambda$binderDied$0();
                }
            }, "releaseResource");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$binderDied$0() {
            ImsService.this.releaseResource();
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        if (SERVICE_INTERFACE.equals(intent.getAction())) {
            Log.m108i(LOG_TAG, "ImsService Bound.");
            return this.mImsServiceController;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Executor getCachedExecutor() {
        Executor e;
        synchronized (this.mExecutorLock) {
            if (this.mExecutor == null) {
                Executor e2 = getExecutor();
                this.mExecutor = e2 != null ? e2 : new PendingIntent$$ExternalSyntheticLambda1();
            }
            e = this.mExecutor;
        }
        return e;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public IImsMmTelFeature createMmTelFeatureInternal(int slotId, int subscriptionId) {
        MmTelFeature f = createMmTelFeatureForSubscription(slotId, subscriptionId);
        if (f != null) {
            setupFeature(f, slotId, 1);
            f.setDefaultExecutor(getCachedExecutor());
            return f.getBinder();
        }
        Log.m110e(LOG_TAG, "createMmTelFeatureInternal: null feature returned.");
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public IImsMmTelFeature createEmergencyOnlyMmTelFeatureInternal(int slotId) {
        MmTelFeature f = createEmergencyOnlyMmTelFeature(slotId);
        if (f != null) {
            setupFeature(f, slotId, 1);
            f.setDefaultExecutor(getCachedExecutor());
            return f.getBinder();
        }
        Log.m110e(LOG_TAG, "createEmergencyOnlyMmTelFeatureInternal: null feature returned.");
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public IImsRcsFeature createRcsFeatureInternal(int slotId, int subId) {
        RcsFeature f = createRcsFeatureForSubscription(slotId, subId);
        if (f != null) {
            f.setDefaultExecutor(getCachedExecutor());
            setupFeature(f, slotId, 2);
            return f.getBinder();
        }
        Log.m110e(LOG_TAG, "createRcsFeatureInternal: null feature returned.");
        return null;
    }

    private void setupFeature(ImsFeature f, int slotId, int featureType) {
        f.initialize(this, slotId);
        addImsFeature(slotId, featureType, f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addImsFeatureStatusCallback(int slotId, int featureType, IImsFeatureStatusCallback c) {
        synchronized (this.mFeaturesBySlot) {
            SparseArray<ImsFeature> features = this.mFeaturesBySlot.get(slotId);
            if (features == null) {
                Log.m104w(LOG_TAG, "Can not add ImsFeatureStatusCallback - no features on slot " + slotId);
                return;
            }
            ImsFeature f = features.get(featureType);
            if (f != null) {
                f.addImsFeatureStatusCallback(c);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeImsFeatureStatusCallback(int slotId, int featureType, IImsFeatureStatusCallback c) {
        synchronized (this.mFeaturesBySlot) {
            SparseArray<ImsFeature> features = this.mFeaturesBySlot.get(slotId);
            if (features == null) {
                Log.m104w(LOG_TAG, "Can not remove ImsFeatureStatusCallback - no features on slot " + slotId);
                return;
            }
            ImsFeature f = features.get(featureType);
            if (f != null) {
                f.removeImsFeatureStatusCallback(c);
            }
        }
    }

    private void addImsFeature(int slotId, int featureType, ImsFeature f) {
        synchronized (this.mFeaturesBySlot) {
            SparseArray<ImsFeature> features = this.mFeaturesBySlot.get(slotId);
            if (features == null) {
                features = new SparseArray<>();
                this.mFeaturesBySlot.put(slotId, features);
            }
            features.put(featureType, f);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeImsFeature(int slotId, int featureType) {
        notifySubscriptionRemoved(slotId);
        synchronized (this.mFeaturesBySlot) {
            SparseArray<ImsFeature> features = this.mFeaturesBySlot.get(slotId);
            if (features == null) {
                Log.m104w(LOG_TAG, "Can not remove ImsFeature. No ImsFeatures exist on slot " + slotId);
                return;
            }
            ImsFeature f = features.get(featureType);
            if (f == null) {
                Log.m104w(LOG_TAG, "Can not remove ImsFeature. No feature with type " + featureType + " exists on slot " + slotId);
                return;
            }
            f.onFeatureRemoved();
            features.remove(featureType);
        }
    }

    public ImsFeature getImsFeature(int slotId, int featureType) {
        synchronized (this.mFeaturesBySlot) {
            SparseArray<ImsFeature> features = this.mFeaturesBySlot.get(slotId);
            if (features == null) {
                return null;
            }
            return features.get(featureType);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setImsFeatureCreatedForSlot(int slotId, int featureType, boolean createdForSlot) {
        synchronized (this.mCreateImsFeatureWithSlotIdFlagMap) {
            getImsFeatureCreatedForSlot(slotId).put(featureType, createdForSlot);
        }
    }

    public boolean isImsFeatureCreatedForSlot(int slotId, int featureType) {
        boolean z;
        synchronized (this.mCreateImsFeatureWithSlotIdFlagMap) {
            z = getImsFeatureCreatedForSlot(slotId).get(featureType);
        }
        return z;
    }

    private SparseBooleanArray getImsFeatureCreatedForSlot(int slotId) {
        SparseBooleanArray createFlag = this.mCreateImsFeatureWithSlotIdFlagMap.get(slotId);
        if (createFlag == null) {
            SparseBooleanArray createFlag2 = new SparseBooleanArray();
            this.mCreateImsFeatureWithSlotIdFlagMap.put(slotId, createFlag2);
            return createFlag2;
        }
        return createFlag;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void releaseResource() {
        Log.m104w(LOG_TAG, "cleaning up features");
        synchronized (this.mFeaturesBySlot) {
            for (int i = 0; i < this.mFeaturesBySlot.size(); i++) {
                SparseArray<ImsFeature> features = this.mFeaturesBySlot.valueAt(i);
                if (features != null) {
                    for (int index = 0; index < features.size(); index++) {
                        ImsFeature imsFeature = features.valueAt(index);
                        if (imsFeature != null) {
                            imsFeature.onFeatureRemoved();
                        }
                    }
                    features.clear();
                }
            }
            this.mFeaturesBySlot.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void executeMethodAsync(final Runnable r, String errorLogName) {
        try {
            CompletableFuture.runAsync(new Runnable() { // from class: android.telephony.ims.ImsService$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    TelephonyUtils.runWithCleanCallingIdentity(r);
                }
            }, getCachedExecutor()).join();
        } catch (CancellationException | CompletionException e) {
            Log.m104w(LOG_TAG, "ImsService Binder - " + errorLogName + " exception: " + e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public <T> T executeMethodAsyncForResult(final Supplier<T> r, String errorLogName) {
        CompletableFuture<T> future = CompletableFuture.supplyAsync(new Supplier() { // from class: android.telephony.ims.ImsService$$ExternalSyntheticLambda0
            @Override // java.util.function.Supplier
            public final Object get() {
                Object runWithCleanCallingIdentity;
                runWithCleanCallingIdentity = TelephonyUtils.runWithCleanCallingIdentity(r);
                return runWithCleanCallingIdentity;
            }
        }, getCachedExecutor());
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.m104w(LOG_TAG, "ImsService Binder - " + errorLogName + " exception: " + e.getMessage());
            return null;
        }
    }

    public ImsFeatureConfiguration querySupportedImsFeatures() {
        return new ImsFeatureConfiguration();
    }

    public final void onUpdateSupportedImsFeatures(ImsFeatureConfiguration c) throws RemoteException {
        IImsServiceControllerListener l;
        synchronized (this.mListenerLock) {
            l = this.mListener;
            if (l == null) {
                throw new IllegalStateException("Framework is not ready");
            }
        }
        l.onUpdateSupportedImsFeatures(c);
    }

    public long getImsServiceCapabilities() {
        return 0L;
    }

    public void readyForFeatureCreation() {
    }

    public void enableImsForSubscription(int slotId, int subscriptionId) {
        enableIms(slotId);
    }

    public void disableImsForSubscription(int slotId, int subscriptionId) {
        disableIms(slotId);
    }

    private void notifySubscriptionRemoved(int slotId) {
        ImsRegistrationImplBase registrationImplBase = getRegistration(slotId);
        if (registrationImplBase != null) {
            registrationImplBase.clearRegistrationCache();
        }
        ImsConfigImplBase imsConfigImplBase = getConfig(slotId);
        if (imsConfigImplBase != null) {
            imsConfigImplBase.clearConfigurationCache();
        }
    }

    @Deprecated
    public void enableIms(int slotId) {
    }

    @Deprecated
    public void disableIms(int slotId) {
    }

    public MmTelFeature createMmTelFeatureForSubscription(int slotId, int subscriptionId) {
        setImsFeatureCreatedForSlot(slotId, 1, true);
        return createMmTelFeature(slotId);
    }

    public RcsFeature createRcsFeatureForSubscription(int slotId, int subscriptionId) {
        setImsFeatureCreatedForSlot(slotId, 2, true);
        return createRcsFeature(slotId);
    }

    public MmTelFeature createEmergencyOnlyMmTelFeature(int slotId) {
        setImsFeatureCreatedForSlot(slotId, 1, true);
        return createMmTelFeature(slotId);
    }

    @Deprecated
    public MmTelFeature createMmTelFeature(int slotId) {
        return null;
    }

    @Deprecated
    public RcsFeature createRcsFeature(int slotId) {
        return null;
    }

    public ImsConfigImplBase getConfigForSubscription(int slotId, int subscriptionId) {
        return getConfig(slotId);
    }

    public ImsRegistrationImplBase getRegistrationForSubscription(int slotId, int subscriptionId) {
        return getRegistration(slotId);
    }

    @Deprecated
    public ImsConfigImplBase getConfig(int slotId) {
        return new ImsConfigImplBase();
    }

    @Deprecated
    public ImsRegistrationImplBase getRegistration(int slotId) {
        return new ImsRegistrationImplBase();
    }

    public SipTransportImplBase getSipTransport(int slotId) {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static long sanitizeCapabilities(long caps) {
        long filter = (-1) << ((int) (CAPABILITY_MAX_INDEX + 1));
        return caps & (~filter) & (-2);
    }

    public static String getCapabilitiesString(long caps) {
        StringBuffer result = new StringBuffer();
        result.append("capabilities={ ");
        long filter = -1;
        for (long i = 0; (caps & filter) != 0 && i <= 63; i++) {
            long bitToCheck = 1 << ((int) i);
            if ((caps & bitToCheck) != 0) {
                result.append(CAPABILITIES_LOG_MAP.getOrDefault(Long.valueOf(bitToCheck), bitToCheck + "?"));
                result.append(" ");
            }
            filter <<= 1;
        }
        result.append("}");
        return result.toString();
    }

    public Executor getExecutor() {
        return new PendingIntent$$ExternalSyntheticLambda1();
    }
}
