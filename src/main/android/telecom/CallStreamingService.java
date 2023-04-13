package android.telecom;

import android.annotation.SystemApi;
import android.app.Service;
import android.content.Intent;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.RemoteException;
import com.android.internal.telecom.ICallStreamingService;
import com.android.internal.telecom.IStreamingCallAdapter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
/* loaded from: classes3.dex */
public abstract class CallStreamingService extends Service {
    private static final int MSG_CALL_STREAMING_CHANGED_CHANGED = 4;
    private static final int MSG_CALL_STREAMING_STARTED = 2;
    private static final int MSG_CALL_STREAMING_STOPPED = 3;
    private static final int MSG_SET_STREAMING_CALL_ADAPTER = 1;
    public static final String SERVICE_INTERFACE = "android.telecom.CallStreamingService";
    public static final int STREAMING_FAILED_ALREADY_STREAMING = 1;
    public static final int STREAMING_FAILED_NO_SENDER = 2;
    public static final int STREAMING_FAILED_SENDER_BINDING_ERROR = 3;
    public static final int STREAMING_FAILED_UNKNOWN = 0;
    private StreamingCall mCall;
    private final Handler mHandler = new Handler(Looper.getMainLooper()) { // from class: android.telecom.CallStreamingService.1
        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            if (CallStreamingService.this.mStreamingCallAdapter == null && msg.what != 1) {
                return;
            }
            switch (msg.what) {
                case 1:
                    CallStreamingService.this.mStreamingCallAdapter = new StreamingCallAdapter((IStreamingCallAdapter) msg.obj);
                    return;
                case 2:
                    CallStreamingService.this.mCall = (StreamingCall) msg.obj;
                    CallStreamingService.this.mCall.setAdapter(CallStreamingService.this.mStreamingCallAdapter);
                    CallStreamingService callStreamingService = CallStreamingService.this;
                    callStreamingService.onCallStreamingStarted(callStreamingService.mCall);
                    return;
                case 3:
                    CallStreamingService.this.mCall = null;
                    CallStreamingService.this.mStreamingCallAdapter = null;
                    CallStreamingService.this.onCallStreamingStopped();
                    return;
                case 4:
                    int state = ((Integer) msg.obj).intValue();
                    CallStreamingService.this.mCall.requestStreamingState(state);
                    CallStreamingService.this.onCallStreamingStateChanged(state);
                    return;
                default:
                    return;
            }
        }
    };
    private StreamingCallAdapter mStreamingCallAdapter;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface StreamingFailedReason {
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return new CallStreamingServiceBinder();
    }

    /* loaded from: classes3.dex */
    private final class CallStreamingServiceBinder extends ICallStreamingService.Stub {
        private CallStreamingServiceBinder() {
        }

        @Override // com.android.internal.telecom.ICallStreamingService
        public void setStreamingCallAdapter(IStreamingCallAdapter streamingCallAdapter) throws RemoteException {
            CallStreamingService.this.mHandler.obtainMessage(1, CallStreamingService.this.mStreamingCallAdapter).sendToTarget();
        }

        @Override // com.android.internal.telecom.ICallStreamingService
        public void onCallStreamingStarted(StreamingCall call) throws RemoteException {
            CallStreamingService.this.mHandler.obtainMessage(2, call).sendToTarget();
        }

        @Override // com.android.internal.telecom.ICallStreamingService
        public void onCallStreamingStopped() throws RemoteException {
            CallStreamingService.this.mHandler.obtainMessage(3).sendToTarget();
        }

        @Override // com.android.internal.telecom.ICallStreamingService
        public void onCallStreamingStateChanged(int state) throws RemoteException {
            CallStreamingService.this.mHandler.obtainMessage(4, Integer.valueOf(state)).sendToTarget();
        }
    }

    public void onCallStreamingStarted(StreamingCall call) {
    }

    public void onCallStreamingStopped() {
    }

    public void onCallStreamingStateChanged(int state) {
    }
}
