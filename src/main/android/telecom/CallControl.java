package android.telecom;

import android.media.MediaMetrics;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.OutcomeReceiver;
import android.p008os.ParcelUuid;
import android.p008os.RemoteException;
import android.p008os.ResultReceiver;
import android.telecom.CallControl;
import android.text.TextUtils;
import com.android.internal.telecom.ClientTransactionalServiceRepository;
import com.android.internal.telecom.ICallControl;
import java.util.Objects;
import java.util.concurrent.Executor;
/* loaded from: classes3.dex */
public final class CallControl {
    private static final String INTERFACE_ERROR_MSG = "Call Control is not available";
    private static final String TAG = CallControl.class.getSimpleName();
    private final String mCallId;
    private final PhoneAccountHandle mPhoneAccountHandle;
    private final ClientTransactionalServiceRepository mRepository;
    private final ICallControl mServerInterface;

    public CallControl(String callId, ICallControl serverInterface, ClientTransactionalServiceRepository repository, PhoneAccountHandle pah) {
        this.mCallId = callId;
        this.mServerInterface = serverInterface;
        this.mRepository = repository;
        this.mPhoneAccountHandle = pah;
    }

    public ParcelUuid getCallId() {
        return ParcelUuid.fromString(this.mCallId);
    }

    public void setActive(Executor executor, OutcomeReceiver<Void, CallException> callback) {
        ICallControl iCallControl = this.mServerInterface;
        if (iCallControl != null) {
            try {
                iCallControl.setActive(this.mCallId, new CallControlResultReceiver("setActive", executor, callback));
                return;
            } catch (RemoteException e) {
                throw e.rethrowAsRuntimeException();
            }
        }
        throw new IllegalStateException(INTERFACE_ERROR_MSG);
    }

    public void answer(int videoState, Executor executor, OutcomeReceiver<Void, CallException> callback) {
        validateVideoState(videoState);
        Objects.requireNonNull(executor);
        Objects.requireNonNull(callback);
        ICallControl iCallControl = this.mServerInterface;
        if (iCallControl != null) {
            try {
                iCallControl.answer(videoState, this.mCallId, new CallControlResultReceiver("answer", executor, callback));
                return;
            } catch (RemoteException e) {
                throw e.rethrowAsRuntimeException();
            }
        }
        throw new IllegalStateException(INTERFACE_ERROR_MSG);
    }

    public void setInactive(Executor executor, OutcomeReceiver<Void, CallException> callback) {
        ICallControl iCallControl = this.mServerInterface;
        if (iCallControl != null) {
            try {
                iCallControl.setInactive(this.mCallId, new CallControlResultReceiver("setInactive", executor, callback));
                return;
            } catch (RemoteException e) {
                throw e.rethrowAsRuntimeException();
            }
        }
        throw new IllegalStateException(INTERFACE_ERROR_MSG);
    }

    public void disconnect(DisconnectCause disconnectCause, Executor executor, OutcomeReceiver<Void, CallException> callback) {
        Objects.requireNonNull(disconnectCause);
        Objects.requireNonNull(executor);
        Objects.requireNonNull(callback);
        validateDisconnectCause(disconnectCause);
        ICallControl iCallControl = this.mServerInterface;
        if (iCallControl != null) {
            try {
                iCallControl.disconnect(this.mCallId, disconnectCause, new CallControlResultReceiver(MediaMetrics.Value.DISCONNECT, executor, callback));
                return;
            } catch (RemoteException e) {
                throw e.rethrowAsRuntimeException();
            }
        }
        throw new IllegalStateException(INTERFACE_ERROR_MSG);
    }

    public void startCallStreaming(Executor executor, OutcomeReceiver<Void, CallException> callback) {
        ICallControl iCallControl = this.mServerInterface;
        if (iCallControl != null) {
            try {
                iCallControl.startCallStreaming(this.mCallId, new CallControlResultReceiver("startCallStreaming", executor, callback));
                return;
            } catch (RemoteException e) {
                throw e.rethrowAsRuntimeException();
            }
        }
        throw new IllegalStateException(INTERFACE_ERROR_MSG);
    }

    public void requestCallEndpointChange(CallEndpoint callEndpoint, Executor executor, OutcomeReceiver<Void, CallException> callback) {
        Objects.requireNonNull(callEndpoint);
        Objects.requireNonNull(executor);
        Objects.requireNonNull(callback);
        ICallControl iCallControl = this.mServerInterface;
        if (iCallControl != null) {
            try {
                iCallControl.requestCallEndpointChange(callEndpoint, new CallControlResultReceiver("endpointChange", executor, callback));
                return;
            } catch (RemoteException e) {
                throw e.rethrowAsRuntimeException();
            }
        }
        throw new IllegalStateException(INTERFACE_ERROR_MSG);
    }

    public void sendEvent(String event, Bundle extras) {
        Objects.requireNonNull(event);
        Objects.requireNonNull(extras);
        ICallControl iCallControl = this.mServerInterface;
        if (iCallControl != null) {
            try {
                iCallControl.sendEvent(this.mCallId, event, extras);
                return;
            } catch (RemoteException e) {
                throw e.rethrowAsRuntimeException();
            }
        }
        throw new IllegalStateException(INTERFACE_ERROR_MSG);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class CallControlResultReceiver extends ResultReceiver {
        private final String mCallingMethod;
        private final OutcomeReceiver<Void, CallException> mClientCallback;
        private final Executor mExecutor;

        CallControlResultReceiver(String method, Executor executor, OutcomeReceiver<Void, CallException> clientCallback) {
            super((Handler) null);
            this.mCallingMethod = method;
            this.mExecutor = executor;
            this.mClientCallback = clientCallback;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.p008os.ResultReceiver
        public void onReceiveResult(int resultCode, final Bundle resultData) {
            Log.m138d(CallControl.TAG, "%s: oRR: resultCode=[%s]", this.mCallingMethod, Integer.valueOf(resultCode));
            super.onReceiveResult(resultCode, resultData);
            long identity = Binder.clearCallingIdentity();
            try {
                if (resultCode == 0) {
                    this.mExecutor.execute(new Runnable() { // from class: android.telecom.CallControl$CallControlResultReceiver$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            CallControl.CallControlResultReceiver.this.lambda$onReceiveResult$0();
                        }
                    });
                } else {
                    this.mExecutor.execute(new Runnable() { // from class: android.telecom.CallControl$CallControlResultReceiver$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            CallControl.CallControlResultReceiver.this.lambda$onReceiveResult$1(resultData);
                        }
                    });
                }
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onReceiveResult$0() {
            this.mClientCallback.onResult(null);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onReceiveResult$1(Bundle resultData) {
            this.mClientCallback.onError(CallControl.this.getTransactionException(resultData));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public CallException getTransactionException(Bundle resultData) {
        if (resultData != null && resultData.containsKey(CallException.TRANSACTION_EXCEPTION_KEY)) {
            return (CallException) resultData.getParcelable(CallException.TRANSACTION_EXCEPTION_KEY, CallException.class);
        }
        return new CallException("unknown error", 1);
    }

    private void validateDisconnectCause(DisconnectCause disconnectCause) {
        int code = disconnectCause.getCode();
        if (code != 2 && code != 3 && code != 5 && code != 6) {
            throw new IllegalArgumentException(TextUtils.formatSimple("The DisconnectCause code provided, %d , is not a valid Disconnect code. Valid DisconnectCause codes are limited to [DisconnectCause.LOCAL, DisconnectCause.REMOTE, DisconnectCause.MISSED, or DisconnectCause.REJECTED]", Integer.valueOf(disconnectCause.getCode())));
        }
    }

    private void validateVideoState(int videoState) {
        if (videoState != 1 && videoState != 2) {
            throw new IllegalArgumentException(TextUtils.formatSimple("The VideoState argument passed in, %d , is not a valid VideoState. The VideoState choices are limited to CallAttributes.AUDIO_CALL orCallAttributes.VIDEO_CALL", Integer.valueOf(videoState)));
        }
    }
}
