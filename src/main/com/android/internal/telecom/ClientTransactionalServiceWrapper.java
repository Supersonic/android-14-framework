package com.android.internal.telecom;

import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.OutcomeReceiver;
import android.p008os.ResultReceiver;
import android.telecom.CallAttributes;
import android.telecom.CallControl;
import android.telecom.CallControlCallback;
import android.telecom.CallEndpoint;
import android.telecom.CallEventCallback;
import android.telecom.CallException;
import android.telecom.DisconnectCause;
import android.telecom.PhoneAccountHandle;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.telecom.ClientTransactionalServiceWrapper;
import com.android.internal.telecom.ICallEventCallback;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
/* loaded from: classes2.dex */
public class ClientTransactionalServiceWrapper {
    private static final String EXECUTOR_FAIL_MSG = "Telecom hit an exception while handling a CallEventCallback on an executor: ";
    private static final String TAG = ClientTransactionalServiceWrapper.class.getSimpleName();
    private final PhoneAccountHandle mPhoneAccountHandle;
    private final ClientTransactionalServiceRepository mRepository;
    private final ConcurrentHashMap<String, TransactionalCall> mCallIdToTransactionalCall = new ConcurrentHashMap<>();
    private final ICallEventCallback mCallEventCallback = new BinderC43621();

    public ClientTransactionalServiceWrapper(PhoneAccountHandle handle, ClientTransactionalServiceRepository repo) {
        this.mPhoneAccountHandle = handle;
        this.mRepository = repo;
    }

    public void untrackCall(String callId) {
        Log.m108i(TAG, TextUtils.formatSimple("removeCall: with id=[%s]", callId));
        if (this.mCallIdToTransactionalCall.containsKey(callId)) {
            TransactionalCall call = this.mCallIdToTransactionalCall.remove(callId);
            CallControl control = call.getCallControl();
            if (control != null) {
                call.setCallControl(null);
            }
        }
        if (this.mCallIdToTransactionalCall.size() == 0) {
            this.mRepository.removeServiceWrapper(this.mPhoneAccountHandle);
        }
    }

    public String trackCall(CallAttributes callAttributes, Executor executor, OutcomeReceiver<CallControl, CallException> pendingControl, CallControlCallback handshakes, CallEventCallback events) {
        String newCallId = UUID.randomUUID().toString();
        this.mCallIdToTransactionalCall.put(newCallId, new TransactionalCall(newCallId, callAttributes, executor, pendingControl, handshakes, events));
        return newCallId;
    }

    public ICallEventCallback getCallEventCallback() {
        return this.mCallEventCallback;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class ReceiverWrapper implements Consumer<Boolean> {
        private final ResultReceiver mRepeaterReceiver;

        ReceiverWrapper(ResultReceiver resultReceiver) {
            this.mRepeaterReceiver = resultReceiver;
        }

        @Override // java.util.function.Consumer
        public void accept(Boolean clientCompletedCallbackSuccessfully) {
            if (clientCompletedCallbackSuccessfully.booleanValue()) {
                this.mRepeaterReceiver.send(0, null);
            } else {
                this.mRepeaterReceiver.send(1, null);
            }
        }

        @Override // java.util.function.Consumer
        public Consumer<Boolean> andThen(Consumer<? super Boolean> after) {
            return super.andThen(after);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.internal.telecom.ClientTransactionalServiceWrapper$1 */
    /* loaded from: classes2.dex */
    public class BinderC43621 extends ICallEventCallback.Stub {
        private static final String ON_ANSWER = "onAnswer";
        private static final String ON_AVAILABLE_CALL_ENDPOINTS = "onAvailableCallEndpointsChanged";
        private static final String ON_CALL_STREAMING_FAILED = "onCallStreamingFailed";
        private static final String ON_DISCONNECT = "onDisconnect";
        private static final String ON_EVENT = "onEvent";
        private static final String ON_MUTE_STATE_CHANGED = "onMuteStateChanged";
        private static final String ON_REQ_ENDPOINT_CHANGE = "onRequestEndpointChange";
        private static final String ON_SET_ACTIVE = "onSetActive";
        private static final String ON_SET_INACTIVE = "onSetInactive";
        private static final String ON_STREAMING_STARTED = "onStreamingStarted";

        BinderC43621() {
        }

        private void handleCallEventCallback(final String action, final String callId, ResultReceiver ackResultReceiver, final Object... args) {
            Log.m108i(ClientTransactionalServiceWrapper.TAG, TextUtils.formatSimple("hCEC: id=[%s], action=[%s]", callId, action));
            TransactionalCall call = (TransactionalCall) ClientTransactionalServiceWrapper.this.mCallIdToTransactionalCall.get(callId);
            if (call != null) {
                final CallControlCallback callback = call.getCallControlCallback();
                final ReceiverWrapper outcomeReceiverWrapper = new ReceiverWrapper(ackResultReceiver);
                long identity = Binder.clearCallingIdentity();
                try {
                    try {
                        call.getExecutor().execute(new Runnable() { // from class: com.android.internal.telecom.ClientTransactionalServiceWrapper$1$$ExternalSyntheticLambda0
                            @Override // java.lang.Runnable
                            public final void run() {
                                ClientTransactionalServiceWrapper.BinderC43621.this.lambda$handleCallEventCallback$0(action, callback, outcomeReceiverWrapper, args, callId);
                            }
                        });
                    } catch (Exception e) {
                        Log.m110e(ClientTransactionalServiceWrapper.TAG, ClientTransactionalServiceWrapper.EXECUTOR_FAIL_MSG + e);
                    }
                } finally {
                    Binder.restoreCallingIdentity(identity);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        public /* synthetic */ void lambda$handleCallEventCallback$0(String action, CallControlCallback callback, ReceiverWrapper outcomeReceiverWrapper, Object[] args, String callId) {
            char c;
            switch (action.hashCode()) {
                case -1801878642:
                    if (action.equals(ON_SET_INACTIVE)) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case -423970853:
                    if (action.equals(ON_DISCONNECT)) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                case 27661033:
                    if (action.equals(ON_SET_ACTIVE)) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case 985601661:
                    if (action.equals(ON_ANSWER)) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                case 1633591742:
                    if (action.equals(ON_STREAMING_STARTED)) {
                        c = 4;
                        break;
                    }
                    c = 65535;
                    break;
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                    callback.onSetActive(outcomeReceiverWrapper);
                    return;
                case 1:
                    callback.onSetInactive(outcomeReceiverWrapper);
                    return;
                case 2:
                    callback.onDisconnect((DisconnectCause) args[0], outcomeReceiverWrapper);
                    ClientTransactionalServiceWrapper.this.untrackCall(callId);
                    return;
                case 3:
                    callback.onAnswer(((Integer) args[0]).intValue(), outcomeReceiverWrapper);
                    return;
                case 4:
                    callback.onCallStreamingStarted(outcomeReceiverWrapper);
                    return;
                default:
                    return;
            }
        }

        @Override // com.android.internal.telecom.ICallEventCallback
        public void onAddCallControl(String callId, int resultCode, ICallControl callControl, CallException transactionalException) {
            Log.m108i(ClientTransactionalServiceWrapper.TAG, TextUtils.formatSimple("oACC: id=[%s], code=[%d]", callId, Integer.valueOf(resultCode)));
            TransactionalCall call = (TransactionalCall) ClientTransactionalServiceWrapper.this.mCallIdToTransactionalCall.get(callId);
            if (call != null) {
                OutcomeReceiver<CallControl, CallException> pendingControl = call.getPendingControl();
                if (resultCode == 0) {
                    CallControl control = new CallControl(callId, callControl, ClientTransactionalServiceWrapper.this.mRepository, ClientTransactionalServiceWrapper.this.mPhoneAccountHandle);
                    pendingControl.onResult(control);
                    call.setCallControl(control);
                    return;
                }
                pendingControl.onError(transactionalException);
                ClientTransactionalServiceWrapper.this.mCallIdToTransactionalCall.remove(callId);
                return;
            }
            ClientTransactionalServiceWrapper.this.untrackCall(callId);
            Log.m110e(ClientTransactionalServiceWrapper.TAG, "oACC: TransactionalCall object not found for call w/ id=" + callId);
        }

        @Override // com.android.internal.telecom.ICallEventCallback
        public void onSetActive(String callId, ResultReceiver resultReceiver) {
            handleCallEventCallback(ON_SET_ACTIVE, callId, resultReceiver, new Object[0]);
        }

        @Override // com.android.internal.telecom.ICallEventCallback
        public void onSetInactive(String callId, ResultReceiver resultReceiver) {
            handleCallEventCallback(ON_SET_INACTIVE, callId, resultReceiver, new Object[0]);
        }

        @Override // com.android.internal.telecom.ICallEventCallback
        public void onAnswer(String callId, int videoState, ResultReceiver resultReceiver) {
            handleCallEventCallback(ON_ANSWER, callId, resultReceiver, Integer.valueOf(videoState));
        }

        @Override // com.android.internal.telecom.ICallEventCallback
        public void onDisconnect(String callId, DisconnectCause cause, ResultReceiver resultReceiver) {
            handleCallEventCallback(ON_DISCONNECT, callId, resultReceiver, cause);
        }

        @Override // com.android.internal.telecom.ICallEventCallback
        public void onCallEndpointChanged(String callId, CallEndpoint endpoint) {
            handleEventCallback(callId, ON_REQ_ENDPOINT_CHANGE, endpoint);
        }

        @Override // com.android.internal.telecom.ICallEventCallback
        public void onAvailableCallEndpointsChanged(String callId, List<CallEndpoint> endpoints) {
            handleEventCallback(callId, ON_AVAILABLE_CALL_ENDPOINTS, endpoints);
        }

        @Override // com.android.internal.telecom.ICallEventCallback
        public void onMuteStateChanged(String callId, boolean isMuted) {
            handleEventCallback(callId, ON_MUTE_STATE_CHANGED, Boolean.valueOf(isMuted));
        }

        public void handleEventCallback(String callId, final String action, final Object arg) {
            Log.m112d(ClientTransactionalServiceWrapper.TAG, TextUtils.formatSimple("hEC: [%s], callId=[%s]", action, callId));
            TransactionalCall call = (TransactionalCall) ClientTransactionalServiceWrapper.this.mCallIdToTransactionalCall.get(callId);
            if (call != null) {
                final CallEventCallback callback = call.getCallStateCallback();
                Executor executor = call.getExecutor();
                long identity = Binder.clearCallingIdentity();
                try {
                    executor.execute(new Runnable() { // from class: com.android.internal.telecom.ClientTransactionalServiceWrapper$1$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            ClientTransactionalServiceWrapper.BinderC43621.lambda$handleEventCallback$1(action, callback, arg);
                        }
                    });
                } finally {
                    Binder.restoreCallingIdentity(identity);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        public static /* synthetic */ void lambda$handleEventCallback$1(String action, CallEventCallback callback, Object arg) {
            char c;
            switch (action.hashCode()) {
                case -2134827621:
                    if (action.equals(ON_MUTE_STATE_CHANGED)) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                case -564325214:
                    if (action.equals(ON_CALL_STREAMING_FAILED)) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                case -103636834:
                    if (action.equals(ON_AVAILABLE_CALL_ENDPOINTS)) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case 879322485:
                    if (action.equals(ON_REQ_ENDPOINT_CHANGE)) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                    callback.onCallEndpointChanged((CallEndpoint) arg);
                    return;
                case 1:
                    callback.onAvailableCallEndpointsChanged((List) arg);
                    return;
                case 2:
                    callback.onMuteStateChanged(((Boolean) arg).booleanValue());
                    return;
                case 3:
                    callback.onCallStreamingFailed(((Integer) arg).intValue());
                    return;
                default:
                    return;
            }
        }

        @Override // com.android.internal.telecom.ICallEventCallback
        public void removeCallFromTransactionalServiceWrapper(String callId) {
            ClientTransactionalServiceWrapper.this.untrackCall(callId);
        }

        @Override // com.android.internal.telecom.ICallEventCallback
        public void onCallStreamingStarted(String callId, ResultReceiver resultReceiver) {
            handleCallEventCallback(ON_STREAMING_STARTED, callId, resultReceiver, new Object[0]);
        }

        @Override // com.android.internal.telecom.ICallEventCallback
        public void onCallStreamingFailed(String callId, int reason) {
            Log.m108i(ClientTransactionalServiceWrapper.TAG, TextUtils.formatSimple("oCSF: id=[%s], reason=[%s]", callId, Integer.valueOf(reason)));
            handleEventCallback(callId, ON_CALL_STREAMING_FAILED, Integer.valueOf(reason));
        }

        @Override // com.android.internal.telecom.ICallEventCallback
        public void onEvent(String callId, final String event, final Bundle extras) {
            TransactionalCall call = (TransactionalCall) ClientTransactionalServiceWrapper.this.mCallIdToTransactionalCall.get(callId);
            if (call != null) {
                final CallEventCallback callback = call.getCallStateCallback();
                Executor executor = call.getExecutor();
                long identity = Binder.clearCallingIdentity();
                try {
                    executor.execute(new Runnable() { // from class: com.android.internal.telecom.ClientTransactionalServiceWrapper$1$$ExternalSyntheticLambda2
                        @Override // java.lang.Runnable
                        public final void run() {
                            CallEventCallback.this.onEvent(event, extras);
                        }
                    });
                } finally {
                    Binder.restoreCallingIdentity(identity);
                }
            }
        }
    }
}
