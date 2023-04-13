package android.telephony.ims.aidl;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.telephony.ims.DelegateRegistrationState;
import android.telephony.ims.FeatureTagState;
import android.telephony.ims.SipDelegateConfiguration;
import android.telephony.ims.SipDelegateConnection;
import android.telephony.ims.SipDelegateImsConfiguration;
import android.telephony.ims.SipMessage;
import android.telephony.ims.aidl.ISipDelegateConnectionStateCallback;
import android.telephony.ims.aidl.ISipDelegateMessageCallback;
import android.telephony.ims.aidl.SipDelegateConnectionAidlWrapper;
import android.telephony.ims.stub.DelegateConnectionMessageCallback;
import android.telephony.ims.stub.DelegateConnectionStateCallback;
import android.util.ArraySet;
import android.util.Log;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;
/* loaded from: classes3.dex */
public class SipDelegateConnectionAidlWrapper implements SipDelegateConnection, IBinder.DeathRecipient {
    private static final String LOG_TAG = "SipDelegateCAW";
    private final Executor mExecutor;
    private final DelegateConnectionMessageCallback mMessageCallback;
    private final DelegateConnectionStateCallback mStateCallback;
    private final ISipDelegateConnectionStateCallback.Stub mStateBinder = new BinderC32891();
    private final ISipDelegateMessageCallback.Stub mMessageBinder = new BinderC32902();
    private final AtomicReference<ISipDelegate> mDelegateBinder = new AtomicReference<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.telephony.ims.aidl.SipDelegateConnectionAidlWrapper$1 */
    /* loaded from: classes3.dex */
    public class BinderC32891 extends ISipDelegateConnectionStateCallback.Stub {
        BinderC32891() {
        }

        @Override // android.telephony.ims.aidl.ISipDelegateConnectionStateCallback
        public void onCreated(ISipDelegate c) {
            SipDelegateConnectionAidlWrapper.this.associateSipDelegate(c);
            long token = Binder.clearCallingIdentity();
            try {
                SipDelegateConnectionAidlWrapper.this.mExecutor.execute(new Runnable() { // from class: android.telephony.ims.aidl.SipDelegateConnectionAidlWrapper$1$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        SipDelegateConnectionAidlWrapper.BinderC32891.this.lambda$onCreated$0();
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreated$0() {
            SipDelegateConnectionAidlWrapper.this.mStateCallback.onCreated(SipDelegateConnectionAidlWrapper.this);
        }

        @Override // android.telephony.ims.aidl.ISipDelegateConnectionStateCallback
        public void onFeatureTagStatusChanged(final DelegateRegistrationState registrationState, final List<FeatureTagState> deniedFeatureTags) {
            long token = Binder.clearCallingIdentity();
            try {
                SipDelegateConnectionAidlWrapper.this.mExecutor.execute(new Runnable() { // from class: android.telephony.ims.aidl.SipDelegateConnectionAidlWrapper$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        SipDelegateConnectionAidlWrapper.BinderC32891.this.lambda$onFeatureTagStatusChanged$1(registrationState, deniedFeatureTags);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onFeatureTagStatusChanged$1(DelegateRegistrationState registrationState, List deniedFeatureTags) {
            SipDelegateConnectionAidlWrapper.this.mStateCallback.onFeatureTagStatusChanged(registrationState, new ArraySet(deniedFeatureTags));
        }

        @Override // android.telephony.ims.aidl.ISipDelegateConnectionStateCallback
        public void onImsConfigurationChanged(final SipDelegateImsConfiguration registeredSipConfig) {
            long token = Binder.clearCallingIdentity();
            try {
                SipDelegateConnectionAidlWrapper.this.mExecutor.execute(new Runnable() { // from class: android.telephony.ims.aidl.SipDelegateConnectionAidlWrapper$1$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        SipDelegateConnectionAidlWrapper.BinderC32891.this.lambda$onImsConfigurationChanged$2(registeredSipConfig);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onImsConfigurationChanged$2(SipDelegateImsConfiguration registeredSipConfig) {
            SipDelegateConnectionAidlWrapper.this.mStateCallback.onImsConfigurationChanged(registeredSipConfig);
        }

        @Override // android.telephony.ims.aidl.ISipDelegateConnectionStateCallback
        public void onConfigurationChanged(final SipDelegateConfiguration registeredSipConfig) {
            long token = Binder.clearCallingIdentity();
            try {
                SipDelegateConnectionAidlWrapper.this.mExecutor.execute(new Runnable() { // from class: android.telephony.ims.aidl.SipDelegateConnectionAidlWrapper$1$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        SipDelegateConnectionAidlWrapper.BinderC32891.this.lambda$onConfigurationChanged$3(registeredSipConfig);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onConfigurationChanged$3(SipDelegateConfiguration registeredSipConfig) {
            SipDelegateConnectionAidlWrapper.this.mStateCallback.onConfigurationChanged(registeredSipConfig);
        }

        @Override // android.telephony.ims.aidl.ISipDelegateConnectionStateCallback
        public void onDestroyed(final int reason) {
            SipDelegateConnectionAidlWrapper.this.invalidateSipDelegateBinder();
            long token = Binder.clearCallingIdentity();
            try {
                SipDelegateConnectionAidlWrapper.this.mExecutor.execute(new Runnable() { // from class: android.telephony.ims.aidl.SipDelegateConnectionAidlWrapper$1$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        SipDelegateConnectionAidlWrapper.BinderC32891.this.lambda$onDestroyed$4(reason);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onDestroyed$4(int reason) {
            SipDelegateConnectionAidlWrapper.this.mStateCallback.onDestroyed(reason);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.telephony.ims.aidl.SipDelegateConnectionAidlWrapper$2 */
    /* loaded from: classes3.dex */
    public class BinderC32902 extends ISipDelegateMessageCallback.Stub {
        BinderC32902() {
        }

        @Override // android.telephony.ims.aidl.ISipDelegateMessageCallback
        public void onMessageReceived(final SipMessage message) {
            long token = Binder.clearCallingIdentity();
            try {
                SipDelegateConnectionAidlWrapper.this.mExecutor.execute(new Runnable() { // from class: android.telephony.ims.aidl.SipDelegateConnectionAidlWrapper$2$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        SipDelegateConnectionAidlWrapper.BinderC32902.this.lambda$onMessageReceived$0(message);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onMessageReceived$0(SipMessage message) {
            SipDelegateConnectionAidlWrapper.this.mMessageCallback.onMessageReceived(message);
        }

        @Override // android.telephony.ims.aidl.ISipDelegateMessageCallback
        public void onMessageSent(final String viaTransactionId) {
            long token = Binder.clearCallingIdentity();
            try {
                SipDelegateConnectionAidlWrapper.this.mExecutor.execute(new Runnable() { // from class: android.telephony.ims.aidl.SipDelegateConnectionAidlWrapper$2$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        SipDelegateConnectionAidlWrapper.BinderC32902.this.lambda$onMessageSent$1(viaTransactionId);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onMessageSent$1(String viaTransactionId) {
            SipDelegateConnectionAidlWrapper.this.mMessageCallback.onMessageSent(viaTransactionId);
        }

        @Override // android.telephony.ims.aidl.ISipDelegateMessageCallback
        public void onMessageSendFailure(final String viaTransactionId, final int reason) {
            long token = Binder.clearCallingIdentity();
            try {
                SipDelegateConnectionAidlWrapper.this.mExecutor.execute(new Runnable() { // from class: android.telephony.ims.aidl.SipDelegateConnectionAidlWrapper$2$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        SipDelegateConnectionAidlWrapper.BinderC32902.this.lambda$onMessageSendFailure$2(viaTransactionId, reason);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onMessageSendFailure$2(String viaTransactionId, int reason) {
            SipDelegateConnectionAidlWrapper.this.mMessageCallback.onMessageSendFailure(viaTransactionId, reason);
        }
    }

    public SipDelegateConnectionAidlWrapper(Executor executor, DelegateConnectionStateCallback stateCallback, DelegateConnectionMessageCallback messageCallback) {
        this.mExecutor = executor;
        this.mStateCallback = stateCallback;
        this.mMessageCallback = messageCallback;
    }

    @Override // android.telephony.ims.SipDelegateConnection
    public void sendMessage(SipMessage sipMessage, long configVersion) {
        try {
            ISipDelegate conn = getSipDelegateBinder();
            if (conn == null) {
                notifyLocalMessageFailedToSend(sipMessage, 2);
            } else {
                conn.sendMessage(sipMessage, configVersion);
            }
        } catch (RemoteException e) {
            notifyLocalMessageFailedToSend(sipMessage, 1);
        }
    }

    @Override // android.telephony.ims.SipDelegateConnection
    public void notifyMessageReceived(String viaTransactionId) {
        try {
            ISipDelegate conn = getSipDelegateBinder();
            if (conn == null) {
                return;
            }
            conn.notifyMessageReceived(viaTransactionId);
        } catch (RemoteException e) {
        }
    }

    @Override // android.telephony.ims.SipDelegateConnection
    public void notifyMessageReceiveError(String viaTransactionId, int reason) {
        try {
            ISipDelegate conn = getSipDelegateBinder();
            if (conn == null) {
                return;
            }
            conn.notifyMessageReceiveError(viaTransactionId, reason);
        } catch (RemoteException e) {
        }
    }

    @Override // android.telephony.ims.SipDelegateConnection
    public void cleanupSession(String callId) {
        try {
            ISipDelegate conn = getSipDelegateBinder();
            if (conn == null) {
                return;
            }
            conn.cleanupSession(callId);
        } catch (RemoteException e) {
        }
    }

    @Override // android.p008os.IBinder.DeathRecipient
    public void binderDied() {
        invalidateSipDelegateBinder();
        this.mExecutor.execute(new Runnable() { // from class: android.telephony.ims.aidl.SipDelegateConnectionAidlWrapper$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                SipDelegateConnectionAidlWrapper.this.lambda$binderDied$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$binderDied$0() {
        this.mStateCallback.onDestroyed(1);
    }

    public ISipDelegateConnectionStateCallback getStateCallbackBinder() {
        return this.mStateBinder;
    }

    public ISipDelegateMessageCallback getMessageCallbackBinder() {
        return this.mMessageBinder;
    }

    public ISipDelegate getSipDelegateBinder() {
        return this.mDelegateBinder.get();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void associateSipDelegate(ISipDelegate c) {
        if (c != null) {
            try {
                c.asBinder().linkToDeath(this, 0);
            } catch (RemoteException e) {
                c = null;
            }
        }
        this.mDelegateBinder.set(c);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void invalidateSipDelegateBinder() {
        ISipDelegate oldVal = this.mDelegateBinder.getAndUpdate(new UnaryOperator() { // from class: android.telephony.ims.aidl.SipDelegateConnectionAidlWrapper$$ExternalSyntheticLambda2
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return SipDelegateConnectionAidlWrapper.lambda$invalidateSipDelegateBinder$1((ISipDelegate) obj);
            }
        });
        if (oldVal != null) {
            try {
                oldVal.asBinder().unlinkToDeath(this, 0);
            } catch (NoSuchElementException e) {
                Log.m108i(LOG_TAG, "invalidateSipDelegateBinder: " + e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ ISipDelegate lambda$invalidateSipDelegateBinder$1(ISipDelegate unused) {
        return null;
    }

    private void notifyLocalMessageFailedToSend(SipMessage m, final int reason) {
        final String transactionId = m.getViaBranchParameter();
        this.mExecutor.execute(new Runnable() { // from class: android.telephony.ims.aidl.SipDelegateConnectionAidlWrapper$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                SipDelegateConnectionAidlWrapper.this.lambda$notifyLocalMessageFailedToSend$2(transactionId, reason);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$notifyLocalMessageFailedToSend$2(String transactionId, int reason) {
        this.mMessageCallback.onMessageSendFailure(transactionId, reason);
    }
}
