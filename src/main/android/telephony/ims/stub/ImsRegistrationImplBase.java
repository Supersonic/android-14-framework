package android.telephony.ims.stub;

import android.annotation.SystemApi;
import android.net.Uri;
import android.p008os.RemoteException;
import android.telephony.ims.ImsReasonInfo;
import android.telephony.ims.ImsRegistrationAttributes;
import android.telephony.ims.SipDetails;
import android.telephony.ims.aidl.IImsRegistration;
import android.telephony.ims.aidl.IImsRegistrationCallback;
import android.telephony.ims.stub.ImsRegistrationImplBase;
import android.util.Log;
import com.android.internal.telephony.util.RemoteCallbackListExt;
import com.android.internal.telephony.util.TelephonyUtils;
import com.android.internal.util.ArrayUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;
/* loaded from: classes3.dex */
public class ImsRegistrationImplBase {
    private static final String LOG_TAG = "ImsRegistrationImplBase";
    public static final int REASON_ALLOWED_NETWORK_TYPES_CHANGED = 3;
    public static final int REASON_HANDOVER_FAILED = 6;
    public static final int REASON_NON_IMS_CAPABLE_NETWORK = 4;
    public static final int REASON_RADIO_POWER_OFF = 5;
    public static final int REASON_SIM_REFRESH = 2;
    public static final int REASON_SIM_REMOVED = 1;
    public static final int REASON_UNKNOWN = 0;
    public static final int REASON_VOPS_NOT_SUPPORTED = 7;
    private static final int REGISTRATION_STATE_UNKNOWN = -1;
    public static final int REGISTRATION_TECH_3G = 4;
    public static final int REGISTRATION_TECH_CROSS_SIM = 2;
    public static final int REGISTRATION_TECH_IWLAN = 1;
    public static final int REGISTRATION_TECH_LTE = 0;
    public static final int REGISTRATION_TECH_MAX = 5;
    public static final int REGISTRATION_TECH_NONE = -1;
    public static final int REGISTRATION_TECH_NR = 3;
    private final IImsRegistration mBinder;
    private final RemoteCallbackListExt<IImsRegistrationCallback> mCallbacks;
    private Executor mExecutor;
    private ImsReasonInfo mLastDisconnectCause;
    private int mLastDisconnectRadioTech;
    private int mLastDisconnectSuggestedAction;
    private final Object mLock;
    private ImsRegistrationAttributes mRegistrationAttributes;
    private int mRegistrationState;
    private Uri[] mUris;
    private boolean mUrisSet;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface ImsDeregistrationReason {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface ImsRegistrationTech {
    }

    @SystemApi
    public ImsRegistrationImplBase() {
        this.mBinder = new BinderC33051();
        this.mCallbacks = new RemoteCallbackListExt<>();
        this.mLock = new Object();
        this.mRegistrationState = -1;
        this.mLastDisconnectCause = new ImsReasonInfo();
        this.mLastDisconnectSuggestedAction = 0;
        this.mLastDisconnectRadioTech = -1;
        this.mUris = new Uri[0];
        this.mUrisSet = false;
    }

    @SystemApi
    public ImsRegistrationImplBase(Executor executor) {
        this.mBinder = new BinderC33051();
        this.mCallbacks = new RemoteCallbackListExt<>();
        this.mLock = new Object();
        this.mRegistrationState = -1;
        this.mLastDisconnectCause = new ImsReasonInfo();
        this.mLastDisconnectSuggestedAction = 0;
        this.mLastDisconnectRadioTech = -1;
        this.mUris = new Uri[0];
        this.mUrisSet = false;
        this.mExecutor = executor;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.telephony.ims.stub.ImsRegistrationImplBase$1 */
    /* loaded from: classes3.dex */
    public class BinderC33051 extends IImsRegistration.Stub {
        BinderC33051() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ Integer lambda$getRegistrationTechnology$0() {
            return Integer.valueOf(ImsRegistrationImplBase.this.mRegistrationAttributes == null ? -1 : ImsRegistrationImplBase.this.mRegistrationAttributes.getRegistrationTechnology());
        }

        @Override // android.telephony.ims.aidl.IImsRegistration
        public int getRegistrationTechnology() throws RemoteException {
            return ((Integer) executeMethodAsyncForResult(new Supplier() { // from class: android.telephony.ims.stub.ImsRegistrationImplBase$1$$ExternalSyntheticLambda7
                @Override // java.util.function.Supplier
                public final Object get() {
                    Integer lambda$getRegistrationTechnology$0;
                    lambda$getRegistrationTechnology$0 = ImsRegistrationImplBase.BinderC33051.this.lambda$getRegistrationTechnology$0();
                    return lambda$getRegistrationTechnology$0;
                }
            }, "getRegistrationTechnology")).intValue();
        }

        @Override // android.telephony.ims.aidl.IImsRegistration
        public void addRegistrationCallback(final IImsRegistrationCallback c) throws RemoteException {
            final AtomicReference<RemoteException> exceptionRef = new AtomicReference<>();
            executeMethodAsync(new Runnable() { // from class: android.telephony.ims.stub.ImsRegistrationImplBase$1$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    ImsRegistrationImplBase.BinderC33051.this.lambda$addRegistrationCallback$1(c, exceptionRef);
                }
            }, "addRegistrationCallback");
            if (exceptionRef.get() != null) {
                throw exceptionRef.get();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$addRegistrationCallback$1(IImsRegistrationCallback c, AtomicReference exceptionRef) {
            try {
                ImsRegistrationImplBase.this.addRegistrationCallback(c);
            } catch (RemoteException e) {
                exceptionRef.set(e);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$removeRegistrationCallback$2(IImsRegistrationCallback c) {
            ImsRegistrationImplBase.this.removeRegistrationCallback(c);
        }

        @Override // android.telephony.ims.aidl.IImsRegistration
        public void removeRegistrationCallback(final IImsRegistrationCallback c) throws RemoteException {
            executeMethodAsync(new Runnable() { // from class: android.telephony.ims.stub.ImsRegistrationImplBase$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ImsRegistrationImplBase.BinderC33051.this.lambda$removeRegistrationCallback$2(c);
                }
            }, "removeRegistrationCallback");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$triggerFullNetworkRegistration$3(int sipCode, String sipReason) {
            ImsRegistrationImplBase.this.triggerFullNetworkRegistration(sipCode, sipReason);
        }

        @Override // android.telephony.ims.aidl.IImsRegistration
        public void triggerFullNetworkRegistration(final int sipCode, final String sipReason) {
            executeMethodAsyncNoException(new Runnable() { // from class: android.telephony.ims.stub.ImsRegistrationImplBase$1$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    ImsRegistrationImplBase.BinderC33051.this.lambda$triggerFullNetworkRegistration$3(sipCode, sipReason);
                }
            }, "triggerFullNetworkRegistration");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$triggerUpdateSipDelegateRegistration$4() {
            ImsRegistrationImplBase.this.updateSipDelegateRegistration();
        }

        @Override // android.telephony.ims.aidl.IImsRegistration
        public void triggerUpdateSipDelegateRegistration() {
            executeMethodAsyncNoException(new Runnable() { // from class: android.telephony.ims.stub.ImsRegistrationImplBase$1$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    ImsRegistrationImplBase.BinderC33051.this.lambda$triggerUpdateSipDelegateRegistration$4();
                }
            }, "triggerUpdateSipDelegateRegistration");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$triggerSipDelegateDeregistration$5() {
            ImsRegistrationImplBase.this.triggerSipDelegateDeregistration();
        }

        @Override // android.telephony.ims.aidl.IImsRegistration
        public void triggerSipDelegateDeregistration() {
            executeMethodAsyncNoException(new Runnable() { // from class: android.telephony.ims.stub.ImsRegistrationImplBase$1$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    ImsRegistrationImplBase.BinderC33051.this.lambda$triggerSipDelegateDeregistration$5();
                }
            }, "triggerSipDelegateDeregistration");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$triggerDeregistration$6(int reason) {
            ImsRegistrationImplBase.this.triggerDeregistration(reason);
        }

        @Override // android.telephony.ims.aidl.IImsRegistration
        public void triggerDeregistration(final int reason) {
            executeMethodAsyncNoException(new Runnable() { // from class: android.telephony.ims.stub.ImsRegistrationImplBase$1$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    ImsRegistrationImplBase.BinderC33051.this.lambda$triggerDeregistration$6(reason);
                }
            }, "triggerDeregistration");
        }

        private void executeMethodAsync(final Runnable r, String errorLogName) throws RemoteException {
            try {
                CompletableFuture.runAsync(new Runnable() { // from class: android.telephony.ims.stub.ImsRegistrationImplBase$1$$ExternalSyntheticLambda6
                    @Override // java.lang.Runnable
                    public final void run() {
                        TelephonyUtils.runWithCleanCallingIdentity(r);
                    }
                }, ImsRegistrationImplBase.this.mExecutor).join();
            } catch (CancellationException | CompletionException e) {
                Log.m104w(ImsRegistrationImplBase.LOG_TAG, "ImsRegistrationImplBase Binder - " + errorLogName + " exception: " + e.getMessage());
                throw new RemoteException(e.getMessage());
            }
        }

        private void executeMethodAsyncNoException(final Runnable r, String errorLogName) {
            try {
                CompletableFuture.runAsync(new Runnable() { // from class: android.telephony.ims.stub.ImsRegistrationImplBase$1$$ExternalSyntheticLambda8
                    @Override // java.lang.Runnable
                    public final void run() {
                        TelephonyUtils.runWithCleanCallingIdentity(r);
                    }
                }, ImsRegistrationImplBase.this.mExecutor).join();
            } catch (CancellationException | CompletionException e) {
                Log.m104w(ImsRegistrationImplBase.LOG_TAG, "ImsRegistrationImplBase Binder - " + errorLogName + " exception: " + e.getMessage());
            }
        }

        private <T> T executeMethodAsyncForResult(final Supplier<T> r, String errorLogName) throws RemoteException {
            CompletableFuture<T> future = CompletableFuture.supplyAsync(new Supplier() { // from class: android.telephony.ims.stub.ImsRegistrationImplBase$1$$ExternalSyntheticLambda4
                @Override // java.util.function.Supplier
                public final Object get() {
                    Object runWithCleanCallingIdentity;
                    runWithCleanCallingIdentity = TelephonyUtils.runWithCleanCallingIdentity(r);
                    return runWithCleanCallingIdentity;
                }
            }, ImsRegistrationImplBase.this.mExecutor);
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                Log.m104w(ImsRegistrationImplBase.LOG_TAG, "ImsRegistrationImplBase Binder - " + errorLogName + " exception: " + e.getMessage());
                throw new RemoteException(e.getMessage());
            }
        }
    }

    public final IImsRegistration getBinder() {
        return this.mBinder;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addRegistrationCallback(IImsRegistrationCallback c) throws RemoteException {
        this.mCallbacks.register(c);
        updateNewCallbackWithState(c);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeRegistrationCallback(IImsRegistrationCallback c) {
        this.mCallbacks.unregister(c);
    }

    @SystemApi
    public void updateSipDelegateRegistration() {
    }

    @SystemApi
    public void triggerSipDelegateDeregistration() {
    }

    @SystemApi
    public void triggerFullNetworkRegistration(int sipCode, String sipReason) {
    }

    public void triggerDeregistration(int reason) {
    }

    @SystemApi
    public final void onRegistered(int imsRadioTech) {
        onRegistered(new ImsRegistrationAttributes.Builder(imsRadioTech).build());
    }

    @SystemApi
    public final void onRegistered(final ImsRegistrationAttributes attributes) {
        updateToState(attributes, 2);
        this.mCallbacks.broadcastAction(new Consumer() { // from class: android.telephony.ims.stub.ImsRegistrationImplBase$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ImsRegistrationImplBase.lambda$onRegistered$0(ImsRegistrationAttributes.this, (IImsRegistrationCallback) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$onRegistered$0(ImsRegistrationAttributes attributes, IImsRegistrationCallback c) {
        try {
            c.onRegistered(attributes);
        } catch (RemoteException e) {
            Log.m104w(LOG_TAG, e + "onRegistered(int, Set) - Skipping callback.");
        }
    }

    @SystemApi
    public final void onRegistering(int imsRadioTech) {
        onRegistering(new ImsRegistrationAttributes.Builder(imsRadioTech).build());
    }

    @SystemApi
    public final void onRegistering(final ImsRegistrationAttributes attributes) {
        updateToState(attributes, 1);
        this.mCallbacks.broadcastAction(new Consumer() { // from class: android.telephony.ims.stub.ImsRegistrationImplBase$$ExternalSyntheticLambda3
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ImsRegistrationImplBase.lambda$onRegistering$1(ImsRegistrationAttributes.this, (IImsRegistrationCallback) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$onRegistering$1(ImsRegistrationAttributes attributes, IImsRegistrationCallback c) {
        try {
            c.onRegistering(attributes);
        } catch (RemoteException e) {
            Log.m104w(LOG_TAG, e + "onRegistering(int, Set) - Skipping callback.");
        }
    }

    @SystemApi
    public final void onDeregistered(ImsReasonInfo info) {
        onDeregistered(info, 0, -1);
    }

    @SystemApi
    public final void onDeregistered(ImsReasonInfo info, final int suggestedAction, final int imsRadioTech) {
        updateToDisconnectedState(info, suggestedAction, imsRadioTech);
        final ImsReasonInfo reasonInfo = info != null ? info : new ImsReasonInfo();
        this.mCallbacks.broadcastAction(new Consumer() { // from class: android.telephony.ims.stub.ImsRegistrationImplBase$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ImsRegistrationImplBase.lambda$onDeregistered$2(ImsReasonInfo.this, suggestedAction, imsRadioTech, (IImsRegistrationCallback) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$onDeregistered$2(ImsReasonInfo reasonInfo, int suggestedAction, int imsRadioTech, IImsRegistrationCallback c) {
        try {
            c.onDeregistered(reasonInfo, suggestedAction, imsRadioTech);
        } catch (RemoteException e) {
            Log.m104w(LOG_TAG, e + "onDeregistered() - Skipping callback.");
        }
    }

    @SystemApi
    public final void onDeregistered(ImsReasonInfo info, SipDetails details) {
        onDeregistered(info, 0, -1, details);
    }

    @SystemApi
    public final void onDeregistered(ImsReasonInfo info, final int suggestedAction, final int imsRadioTech, final SipDetails details) {
        updateToDisconnectedState(info, suggestedAction, imsRadioTech);
        final ImsReasonInfo reasonInfo = info != null ? info : new ImsReasonInfo();
        this.mCallbacks.broadcastAction(new Consumer() { // from class: android.telephony.ims.stub.ImsRegistrationImplBase$$ExternalSyntheticLambda5
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ImsRegistrationImplBase.lambda$onDeregistered$3(ImsReasonInfo.this, suggestedAction, imsRadioTech, details, (IImsRegistrationCallback) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$onDeregistered$3(ImsReasonInfo reasonInfo, int suggestedAction, int imsRadioTech, SipDetails details, IImsRegistrationCallback c) {
        try {
            c.onDeregisteredWithDetails(reasonInfo, suggestedAction, imsRadioTech, details);
        } catch (RemoteException e) {
            Log.m104w(LOG_TAG, e + "onDeregistered() - Skipping callback.");
        }
    }

    @SystemApi
    public final void onTechnologyChangeFailed(final int imsRadioTech, ImsReasonInfo info) {
        final ImsReasonInfo reasonInfo = info != null ? info : new ImsReasonInfo();
        this.mCallbacks.broadcastAction(new Consumer() { // from class: android.telephony.ims.stub.ImsRegistrationImplBase$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ImsRegistrationImplBase.lambda$onTechnologyChangeFailed$4(imsRadioTech, reasonInfo, (IImsRegistrationCallback) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$onTechnologyChangeFailed$4(int imsRadioTech, ImsReasonInfo reasonInfo, IImsRegistrationCallback c) {
        try {
            c.onTechnologyChangeFailed(imsRadioTech, reasonInfo);
        } catch (RemoteException e) {
            Log.m104w(LOG_TAG, e + "onTechnologyChangeFailed() - Skipping callback.");
        }
    }

    @SystemApi
    public final void onSubscriberAssociatedUriChanged(final Uri[] uris) {
        synchronized (this.mLock) {
            this.mUris = (Uri[]) ArrayUtils.cloneOrNull(uris);
            this.mUrisSet = true;
        }
        this.mCallbacks.broadcastAction(new Consumer() { // from class: android.telephony.ims.stub.ImsRegistrationImplBase$$ExternalSyntheticLambda4
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ImsRegistrationImplBase.this.lambda$onSubscriberAssociatedUriChanged$5(uris, (IImsRegistrationCallback) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onSubscriberAssociatedUriChanged */
    public void lambda$onSubscriberAssociatedUriChanged$5(IImsRegistrationCallback callback, Uri[] uris) {
        try {
            callback.onSubscriberAssociatedUriChanged(uris);
        } catch (RemoteException e) {
            Log.m104w(LOG_TAG, e + "onSubscriberAssociatedUriChanged() - Skipping callback.");
        }
    }

    private void updateToState(ImsRegistrationAttributes attributes, int newState) {
        synchronized (this.mLock) {
            this.mRegistrationAttributes = attributes;
            this.mRegistrationState = newState;
            this.mLastDisconnectCause = null;
            this.mLastDisconnectSuggestedAction = 0;
            this.mLastDisconnectRadioTech = -1;
        }
    }

    private void updateToDisconnectedState(ImsReasonInfo info, int suggestedAction, int imsRadioTech) {
        synchronized (this.mLock) {
            this.mUrisSet = false;
            this.mUris = null;
            updateToState(new ImsRegistrationAttributes.Builder(-1).build(), 0);
            if (info != null) {
                this.mLastDisconnectCause = info;
                this.mLastDisconnectSuggestedAction = suggestedAction;
                this.mLastDisconnectRadioTech = imsRadioTech;
            } else {
                Log.m104w(LOG_TAG, "updateToDisconnectedState: no ImsReasonInfo provided.");
                this.mLastDisconnectCause = new ImsReasonInfo();
            }
        }
    }

    private void updateNewCallbackWithState(IImsRegistrationCallback c) throws RemoteException {
        int state;
        ImsRegistrationAttributes attributes;
        ImsReasonInfo disconnectInfo;
        int suggestedAction;
        int imsDisconnectRadioTech;
        boolean urisSet;
        Uri[] uris;
        synchronized (this.mLock) {
            state = this.mRegistrationState;
            attributes = this.mRegistrationAttributes;
            disconnectInfo = this.mLastDisconnectCause;
            suggestedAction = this.mLastDisconnectSuggestedAction;
            imsDisconnectRadioTech = this.mLastDisconnectRadioTech;
            urisSet = this.mUrisSet;
            uris = this.mUris;
        }
        switch (state) {
            case 0:
                c.onDeregistered(disconnectInfo, suggestedAction, imsDisconnectRadioTech);
                break;
            case 1:
                c.onRegistering(attributes);
                break;
            case 2:
                c.onRegistered(attributes);
                break;
        }
        if (urisSet) {
            lambda$onSubscriberAssociatedUriChanged$5(c, uris);
        }
    }

    public final void setDefaultExecutor(Executor executor) {
        if (this.mExecutor == null) {
            this.mExecutor = executor;
        }
    }

    public final void clearRegistrationCache() {
        synchronized (this.mLock) {
            this.mUris = null;
            this.mUrisSet = false;
        }
    }
}
