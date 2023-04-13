package android.telephony.ims.aidl;

import android.p008os.Binder;
import android.p008os.RemoteException;
import android.telephony.ims.DelegateMessageCallback;
import android.telephony.ims.DelegateRegistrationState;
import android.telephony.ims.DelegateStateCallback;
import android.telephony.ims.FeatureTagState;
import android.telephony.ims.SipDelegateConfiguration;
import android.telephony.ims.SipDelegateImsConfiguration;
import android.telephony.ims.SipMessage;
import android.telephony.ims.aidl.ISipDelegate;
import android.telephony.ims.stub.SipDelegate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Executor;
/* loaded from: classes3.dex */
public class SipDelegateAidlWrapper implements DelegateStateCallback, DelegateMessageCallback {
    private static final String LOG_TAG = "SipDelegateAW";
    private volatile SipDelegate mDelegate;
    private final ISipDelegate.Stub mDelegateBinder = new BinderC32881();
    private final Executor mExecutor;
    private final ISipDelegateMessageCallback mMessageBinder;
    private final ISipDelegateStateCallback mStateBinder;

    /* renamed from: android.telephony.ims.aidl.SipDelegateAidlWrapper$1 */
    /* loaded from: classes3.dex */
    class BinderC32881 extends ISipDelegate.Stub {
        BinderC32881() {
        }

        @Override // android.telephony.ims.aidl.ISipDelegate
        public void sendMessage(final SipMessage sipMessage, final long configVersion) {
            final SipDelegate d = SipDelegateAidlWrapper.this.mDelegate;
            if (d == null) {
                return;
            }
            long token = Binder.clearCallingIdentity();
            try {
                SipDelegateAidlWrapper.this.mExecutor.execute(new Runnable() { // from class: android.telephony.ims.aidl.SipDelegateAidlWrapper$1$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        SipDelegate.this.sendMessage(sipMessage, configVersion);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        @Override // android.telephony.ims.aidl.ISipDelegate
        public void notifyMessageReceived(final String viaTransactionId) {
            final SipDelegate d = SipDelegateAidlWrapper.this.mDelegate;
            if (d == null) {
                return;
            }
            long token = Binder.clearCallingIdentity();
            try {
                SipDelegateAidlWrapper.this.mExecutor.execute(new Runnable() { // from class: android.telephony.ims.aidl.SipDelegateAidlWrapper$1$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        SipDelegate.this.notifyMessageReceived(viaTransactionId);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        @Override // android.telephony.ims.aidl.ISipDelegate
        public void notifyMessageReceiveError(final String viaTransactionId, final int reason) {
            final SipDelegate d = SipDelegateAidlWrapper.this.mDelegate;
            if (d == null) {
                return;
            }
            long token = Binder.clearCallingIdentity();
            try {
                SipDelegateAidlWrapper.this.mExecutor.execute(new Runnable() { // from class: android.telephony.ims.aidl.SipDelegateAidlWrapper$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        SipDelegate.this.notifyMessageReceiveError(viaTransactionId, reason);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        @Override // android.telephony.ims.aidl.ISipDelegate
        public void cleanupSession(final String callId) {
            final SipDelegate d = SipDelegateAidlWrapper.this.mDelegate;
            if (d == null) {
                return;
            }
            long token = Binder.clearCallingIdentity();
            try {
                SipDelegateAidlWrapper.this.mExecutor.execute(new Runnable() { // from class: android.telephony.ims.aidl.SipDelegateAidlWrapper$1$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        SipDelegate.this.cleanupSession(callId);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }
    }

    public SipDelegateAidlWrapper(Executor executor, ISipDelegateStateCallback stateBinder, ISipDelegateMessageCallback messageBinder) {
        this.mExecutor = executor;
        this.mStateBinder = stateBinder;
        this.mMessageBinder = messageBinder;
    }

    @Override // android.telephony.ims.DelegateMessageCallback
    public void onMessageReceived(SipMessage message) {
        try {
            this.mMessageBinder.onMessageReceived(message);
        } catch (RemoteException e) {
            SipDelegate d = this.mDelegate;
            if (d != null) {
                notifyLocalMessageFailedToBeReceived(message, 1);
            }
        }
    }

    @Override // android.telephony.ims.DelegateMessageCallback
    public void onMessageSent(String viaTransactionId) {
        try {
            this.mMessageBinder.onMessageSent(viaTransactionId);
        } catch (RemoteException e) {
        }
    }

    @Override // android.telephony.ims.DelegateMessageCallback
    public void onMessageSendFailure(String viaTransactionId, int reason) {
        try {
            this.mMessageBinder.onMessageSendFailure(viaTransactionId, reason);
        } catch (RemoteException e) {
        }
    }

    @Override // android.telephony.ims.DelegateStateCallback
    public void onCreated(SipDelegate delegate, Set<FeatureTagState> deniedTags) {
        this.mDelegate = delegate;
        try {
            this.mStateBinder.onCreated(this.mDelegateBinder, new ArrayList(deniedTags == null ? Collections.emptySet() : deniedTags));
        } catch (RemoteException e) {
        }
    }

    @Override // android.telephony.ims.DelegateStateCallback
    public void onFeatureTagRegistrationChanged(DelegateRegistrationState registrationState) {
        try {
            this.mStateBinder.onFeatureTagRegistrationChanged(registrationState);
        } catch (RemoteException e) {
        }
    }

    @Override // android.telephony.ims.DelegateStateCallback
    public void onImsConfigurationChanged(SipDelegateImsConfiguration config) {
        try {
            this.mStateBinder.onImsConfigurationChanged(config);
        } catch (RemoteException e) {
        }
    }

    @Override // android.telephony.ims.DelegateStateCallback
    public void onConfigurationChanged(SipDelegateConfiguration config) {
        try {
            this.mStateBinder.onConfigurationChanged(config);
        } catch (RemoteException e) {
        }
    }

    @Override // android.telephony.ims.DelegateStateCallback
    public void onDestroyed(int reasonCode) {
        this.mDelegate = null;
        try {
            this.mStateBinder.onDestroyed(reasonCode);
        } catch (RemoteException e) {
        }
    }

    public SipDelegate getDelegate() {
        return this.mDelegate;
    }

    public ISipDelegate getDelegateBinder() {
        return this.mDelegateBinder;
    }

    public ISipDelegateStateCallback getStateCallbackBinder() {
        return this.mStateBinder;
    }

    private void notifyLocalMessageFailedToBeReceived(SipMessage m, final int reason) {
        final String transactionId = m.getViaBranchParameter();
        final SipDelegate d = this.mDelegate;
        if (d != null) {
            this.mExecutor.execute(new Runnable() { // from class: android.telephony.ims.aidl.SipDelegateAidlWrapper$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    SipDelegate.this.notifyMessageReceiveError(transactionId, reason);
                }
            });
        }
    }
}
