package android.telecom;

import android.annotation.SystemApi;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.net.Uri;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.OutcomeReceiver;
import android.telecom.Phone;
import android.telecom.VideoProfile;
import android.view.Surface;
import com.android.internal.p028os.SomeArgs;
import com.android.internal.telecom.IInCallAdapter;
import com.android.internal.telecom.IInCallService;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
/* loaded from: classes3.dex */
public abstract class InCallService extends Service {
    private static final int MSG_ADD_CALL = 2;
    private static final int MSG_BRING_TO_FOREGROUND = 6;
    private static final int MSG_ON_AVAILABLE_CALL_ENDPOINTS_CHANGED = 15;
    private static final int MSG_ON_CALL_AUDIO_STATE_CHANGED = 5;
    private static final int MSG_ON_CALL_ENDPOINT_CHANGED = 14;
    private static final int MSG_ON_CAN_ADD_CALL_CHANGED = 7;
    private static final int MSG_ON_CONNECTION_EVENT = 9;
    private static final int MSG_ON_HANDOVER_COMPLETE = 13;
    private static final int MSG_ON_HANDOVER_FAILED = 12;
    private static final int MSG_ON_MUTE_STATE_CHANGED = 16;
    private static final int MSG_ON_RTT_INITIATION_FAILURE = 11;
    private static final int MSG_ON_RTT_UPGRADE_REQUEST = 10;
    private static final int MSG_SET_IN_CALL_ADAPTER = 1;
    private static final int MSG_SET_POST_DIAL_WAIT = 4;
    private static final int MSG_SILENCE_RINGER = 8;
    private static final int MSG_UPDATE_CALL = 3;
    public static final String SERVICE_INTERFACE = "android.telecom.InCallService";
    private CallEndpoint mCallEndpoint;
    private Phone mPhone;
    private final Handler mHandler = new Handler(Looper.getMainLooper()) { // from class: android.telecom.InCallService.1
        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            SomeArgs args;
            if (InCallService.this.mPhone == null && msg.what != 1) {
                return;
            }
            switch (msg.what) {
                case 1:
                    String callingPackage = InCallService.this.getApplicationContext().getOpPackageName();
                    InCallService.this.mPhone = new Phone(new InCallAdapter((IInCallAdapter) msg.obj), callingPackage, InCallService.this.getApplicationContext().getApplicationInfo().targetSdkVersion);
                    InCallService.this.mPhone.addListener(InCallService.this.mPhoneListener);
                    InCallService inCallService = InCallService.this;
                    inCallService.onPhoneCreated(inCallService.mPhone);
                    return;
                case 2:
                    InCallService.this.mPhone.internalAddCall((ParcelableCall) msg.obj);
                    return;
                case 3:
                    InCallService.this.mPhone.internalUpdateCall((ParcelableCall) msg.obj);
                    return;
                case 4:
                    args = (SomeArgs) msg.obj;
                    try {
                        String callId = (String) args.arg1;
                        String remaining = (String) args.arg2;
                        InCallService.this.mPhone.internalSetPostDialWait(callId, remaining);
                        return;
                    } finally {
                    }
                case 5:
                    InCallService.this.mPhone.internalCallAudioStateChanged((CallAudioState) msg.obj);
                    return;
                case 6:
                    InCallService.this.mPhone.internalBringToForeground(msg.arg1 == 1);
                    return;
                case 7:
                    InCallService.this.mPhone.internalSetCanAddCall(msg.arg1 == 1);
                    return;
                case 8:
                    InCallService.this.mPhone.internalSilenceRinger();
                    return;
                case 9:
                    args = (SomeArgs) msg.obj;
                    try {
                        String callId2 = (String) args.arg1;
                        String event = (String) args.arg2;
                        Bundle extras = (Bundle) args.arg3;
                        InCallService.this.mPhone.internalOnConnectionEvent(callId2, event, extras);
                        return;
                    } finally {
                    }
                case 10:
                    String callId3 = (String) msg.obj;
                    int requestId = msg.arg1;
                    InCallService.this.mPhone.internalOnRttUpgradeRequest(callId3, requestId);
                    return;
                case 11:
                    String callId4 = (String) msg.obj;
                    int reason = msg.arg1;
                    InCallService.this.mPhone.internalOnRttInitiationFailure(callId4, reason);
                    return;
                case 12:
                    String callId5 = (String) msg.obj;
                    int error = msg.arg1;
                    InCallService.this.mPhone.internalOnHandoverFailed(callId5, error);
                    return;
                case 13:
                    String callId6 = (String) msg.obj;
                    InCallService.this.mPhone.internalOnHandoverComplete(callId6);
                    return;
                case 14:
                    CallEndpoint endpoint = (CallEndpoint) msg.obj;
                    if (!Objects.equals(InCallService.this.mCallEndpoint, endpoint)) {
                        InCallService.this.mCallEndpoint = endpoint;
                        InCallService inCallService2 = InCallService.this;
                        inCallService2.onCallEndpointChanged(inCallService2.mCallEndpoint);
                        return;
                    }
                    return;
                case 15:
                    InCallService.this.onAvailableCallEndpointsChanged((List) msg.obj);
                    return;
                case 16:
                    InCallService.this.onMuteStateChanged(((Boolean) msg.obj).booleanValue());
                    return;
                default:
                    return;
            }
        }
    };
    private Phone.Listener mPhoneListener = new Phone.Listener() { // from class: android.telecom.InCallService.2
        @Override // android.telecom.Phone.Listener
        public void onAudioStateChanged(Phone phone, AudioState audioState) {
            InCallService.this.onAudioStateChanged(audioState);
        }

        @Override // android.telecom.Phone.Listener
        public void onCallAudioStateChanged(Phone phone, CallAudioState callAudioState) {
            InCallService.this.onCallAudioStateChanged(callAudioState);
        }

        @Override // android.telecom.Phone.Listener
        public void onBringToForeground(Phone phone, boolean showDialpad) {
            InCallService.this.onBringToForeground(showDialpad);
        }

        @Override // android.telecom.Phone.Listener
        public void onCallAdded(Phone phone, Call call) {
            InCallService.this.onCallAdded(call);
        }

        @Override // android.telecom.Phone.Listener
        public void onCallRemoved(Phone phone, Call call) {
            InCallService.this.onCallRemoved(call);
        }

        @Override // android.telecom.Phone.Listener
        public void onCanAddCallChanged(Phone phone, boolean canAddCall) {
            InCallService.this.onCanAddCallChanged(canAddCall);
        }

        @Override // android.telecom.Phone.Listener
        public void onSilenceRinger(Phone phone) {
            InCallService.this.onSilenceRinger();
        }
    };

    /* loaded from: classes3.dex */
    public static abstract class VideoCall {

        /* loaded from: classes3.dex */
        public static abstract class Callback {
            public abstract void onCallDataUsageChanged(long j);

            public abstract void onCallSessionEvent(int i);

            public abstract void onCameraCapabilitiesChanged(VideoProfile.CameraCapabilities cameraCapabilities);

            public abstract void onPeerDimensionsChanged(int i, int i2);

            public abstract void onSessionModifyRequestReceived(VideoProfile videoProfile);

            public abstract void onSessionModifyResponseReceived(int i, VideoProfile videoProfile, VideoProfile videoProfile2);

            public abstract void onVideoQualityChanged(int i);
        }

        public abstract void destroy();

        public abstract void registerCallback(Callback callback);

        public abstract void registerCallback(Callback callback, Handler handler);

        public abstract void requestCallDataUsage();

        public abstract void requestCameraCapabilities();

        public abstract void sendSessionModifyRequest(VideoProfile videoProfile);

        public abstract void sendSessionModifyResponse(VideoProfile videoProfile);

        public abstract void setCamera(String str);

        public abstract void setDeviceOrientation(int i);

        public abstract void setDisplaySurface(Surface surface);

        public abstract void setPauseImage(Uri uri);

        public abstract void setPreviewSurface(Surface surface);

        public abstract void setZoom(float f);

        public abstract void unregisterCallback(Callback callback);
    }

    /* loaded from: classes3.dex */
    private final class InCallServiceBinder extends IInCallService.Stub {
        private InCallServiceBinder() {
        }

        @Override // com.android.internal.telecom.IInCallService
        public void setInCallAdapter(IInCallAdapter inCallAdapter) {
            InCallService.this.mHandler.obtainMessage(1, inCallAdapter).sendToTarget();
        }

        @Override // com.android.internal.telecom.IInCallService
        public void addCall(ParcelableCall call) {
            InCallService.this.mHandler.obtainMessage(2, call).sendToTarget();
        }

        @Override // com.android.internal.telecom.IInCallService
        public void updateCall(ParcelableCall call) {
            InCallService.this.mHandler.obtainMessage(3, call).sendToTarget();
        }

        @Override // com.android.internal.telecom.IInCallService
        public void setPostDial(String callId, String remaining) {
        }

        @Override // com.android.internal.telecom.IInCallService
        public void setPostDialWait(String callId, String remaining) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = callId;
            args.arg2 = remaining;
            InCallService.this.mHandler.obtainMessage(4, args).sendToTarget();
        }

        @Override // com.android.internal.telecom.IInCallService
        public void onCallAudioStateChanged(CallAudioState callAudioState) {
            InCallService.this.mHandler.obtainMessage(5, callAudioState).sendToTarget();
        }

        @Override // com.android.internal.telecom.IInCallService
        public void onCallEndpointChanged(CallEndpoint callEndpoint) {
            InCallService.this.mHandler.obtainMessage(14, callEndpoint).sendToTarget();
        }

        @Override // com.android.internal.telecom.IInCallService
        public void onAvailableCallEndpointsChanged(List<CallEndpoint> availableEndpoints) {
            InCallService.this.mHandler.obtainMessage(15, availableEndpoints).sendToTarget();
        }

        @Override // com.android.internal.telecom.IInCallService
        public void onMuteStateChanged(boolean isMuted) {
            InCallService.this.mHandler.obtainMessage(16, Boolean.valueOf(isMuted)).sendToTarget();
        }

        @Override // com.android.internal.telecom.IInCallService
        public void bringToForeground(boolean showDialpad) {
            InCallService.this.mHandler.obtainMessage(6, showDialpad ? 1 : 0, 0).sendToTarget();
        }

        @Override // com.android.internal.telecom.IInCallService
        public void onCanAddCallChanged(boolean canAddCall) {
            InCallService.this.mHandler.obtainMessage(7, canAddCall ? 1 : 0, 0).sendToTarget();
        }

        @Override // com.android.internal.telecom.IInCallService
        public void silenceRinger() {
            InCallService.this.mHandler.obtainMessage(8).sendToTarget();
        }

        @Override // com.android.internal.telecom.IInCallService
        public void onConnectionEvent(String callId, String event, Bundle extras) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = callId;
            args.arg2 = event;
            args.arg3 = extras;
            InCallService.this.mHandler.obtainMessage(9, args).sendToTarget();
        }

        @Override // com.android.internal.telecom.IInCallService
        public void onRttUpgradeRequest(String callId, int id) {
            InCallService.this.mHandler.obtainMessage(10, id, 0, callId).sendToTarget();
        }

        @Override // com.android.internal.telecom.IInCallService
        public void onRttInitiationFailure(String callId, int reason) {
            InCallService.this.mHandler.obtainMessage(11, reason, 0, callId).sendToTarget();
        }

        @Override // com.android.internal.telecom.IInCallService
        public void onHandoverFailed(String callId, int error) {
            InCallService.this.mHandler.obtainMessage(12, error, 0, callId).sendToTarget();
        }

        @Override // com.android.internal.telecom.IInCallService
        public void onHandoverComplete(String callId) {
            InCallService.this.mHandler.obtainMessage(13, callId).sendToTarget();
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return new InCallServiceBinder();
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        if (this.mPhone != null) {
            Phone oldPhone = this.mPhone;
            this.mPhone = null;
            oldPhone.destroy();
            oldPhone.removeListener(this.mPhoneListener);
            onPhoneDestroyed(oldPhone);
            return false;
        }
        return false;
    }

    @SystemApi
    @Deprecated
    public Phone getPhone() {
        return this.mPhone;
    }

    public final List<Call> getCalls() {
        Phone phone = this.mPhone;
        return phone == null ? Collections.emptyList() : phone.getCalls();
    }

    public final boolean canAddCall() {
        Phone phone = this.mPhone;
        if (phone == null) {
            return false;
        }
        return phone.canAddCall();
    }

    @Deprecated
    public final AudioState getAudioState() {
        Phone phone = this.mPhone;
        if (phone == null) {
            return null;
        }
        return phone.getAudioState();
    }

    @Deprecated
    public final CallAudioState getCallAudioState() {
        Phone phone = this.mPhone;
        if (phone == null) {
            return null;
        }
        return phone.getCallAudioState();
    }

    public final void setMuted(boolean state) {
        Phone phone = this.mPhone;
        if (phone != null) {
            phone.setMuted(state);
        }
    }

    @Deprecated
    public final void setAudioRoute(int route) {
        Phone phone = this.mPhone;
        if (phone != null) {
            phone.setAudioRoute(route);
        }
    }

    @Deprecated
    public final void requestBluetoothAudio(BluetoothDevice bluetoothDevice) {
        Phone phone = this.mPhone;
        if (phone != null) {
            phone.requestBluetoothAudio(bluetoothDevice.getAddress());
        }
    }

    public final void requestCallEndpointChange(CallEndpoint endpoint, Executor executor, OutcomeReceiver<Void, CallEndpointException> callback) {
        Phone phone = this.mPhone;
        if (phone != null) {
            phone.requestCallEndpointChange(endpoint, executor, callback);
        }
    }

    public final CallEndpoint getCurrentCallEndpoint() {
        return this.mCallEndpoint;
    }

    @SystemApi
    @Deprecated
    public void onPhoneCreated(Phone phone) {
    }

    @SystemApi
    @Deprecated
    public void onPhoneDestroyed(Phone phone) {
    }

    @Deprecated
    public void onAudioStateChanged(AudioState audioState) {
    }

    @Deprecated
    public void onCallAudioStateChanged(CallAudioState audioState) {
    }

    public void onCallEndpointChanged(CallEndpoint callEndpoint) {
    }

    public void onAvailableCallEndpointsChanged(List<CallEndpoint> availableEndpoints) {
    }

    public void onMuteStateChanged(boolean isMuted) {
    }

    public void onBringToForeground(boolean showDialpad) {
    }

    public void onCallAdded(Call call) {
    }

    public void onCallRemoved(Call call) {
    }

    public void onCanAddCallChanged(boolean canAddCall) {
    }

    public void onSilenceRinger() {
    }

    public void onConnectionEvent(Call call, String event, Bundle extras) {
    }
}
