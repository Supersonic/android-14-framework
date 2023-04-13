package android.telephony.gba;

import android.annotation.SystemApi;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.p008os.Build;
import android.p008os.Handler;
import android.p008os.HandlerThread;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.RemoteException;
import android.telephony.IBootstrapAuthenticationCallback;
import android.telephony.gba.IGbaService;
import android.util.Log;
import android.util.SparseArray;
@SystemApi
/* loaded from: classes3.dex */
public class GbaService extends Service {
    private static final boolean DBG = Build.IS_DEBUGGABLE;
    private static final int EVENT_GBA_AUTH_REQUEST = 1;
    public static final String SERVICE_INTERFACE = "android.telephony.gba.GbaService";
    private static final String TAG = "GbaService";
    private final GbaServiceHandler mHandler;
    private final HandlerThread mHandlerThread;
    private final SparseArray<IBootstrapAuthenticationCallback> mCallbacks = new SparseArray<>();
    private final IGbaServiceWrapper mBinder = new IGbaServiceWrapper();

    public GbaService() {
        HandlerThread handlerThread = new HandlerThread(TAG);
        this.mHandlerThread = handlerThread;
        handlerThread.start();
        this.mHandler = new GbaServiceHandler(handlerThread.getLooper());
        Log.m112d(TAG, "GBA service created");
    }

    /* loaded from: classes3.dex */
    private class GbaServiceHandler extends Handler {
        GbaServiceHandler(Looper looper) {
            super(looper);
        }

        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    GbaAuthRequest req = (GbaAuthRequest) msg.obj;
                    synchronized (GbaService.this.mCallbacks) {
                        GbaService.this.mCallbacks.put(req.getToken(), req.getCallback());
                    }
                    GbaService.this.onAuthenticationRequest(req.getSubId(), req.getToken(), req.getAppType(), req.getNafUrl(), req.getSecurityProtocol(), req.isForceBootStrapping());
                    return;
                default:
                    return;
            }
        }
    }

    public void onAuthenticationRequest(int subscriptionId, int token, int appType, Uri nafUrl, byte[] securityProtocol, boolean forceBootStrapping) {
        reportAuthenticationFailure(token, 1);
    }

    public final void reportKeysAvailable(int token, byte[] gbaKey, String transactionId) throws RuntimeException {
        IBootstrapAuthenticationCallback cb;
        synchronized (this.mCallbacks) {
            cb = this.mCallbacks.get(token);
            this.mCallbacks.remove(token);
        }
        if (cb != null) {
            try {
                cb.onKeysAvailable(token, gbaKey, transactionId);
            } catch (RemoteException exception) {
                throw exception.rethrowAsRuntimeException();
            }
        }
    }

    public final void reportAuthenticationFailure(int token, int reason) throws RuntimeException {
        IBootstrapAuthenticationCallback cb;
        synchronized (this.mCallbacks) {
            cb = this.mCallbacks.get(token);
            this.mCallbacks.remove(token);
        }
        if (cb != null) {
            try {
                cb.onAuthenticationFailure(token, reason);
            } catch (RemoteException exception) {
                throw exception.rethrowAsRuntimeException();
            }
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        if (SERVICE_INTERFACE.equals(intent.getAction())) {
            Log.m112d(TAG, "GbaService Bound.");
            return this.mBinder;
        }
        return null;
    }

    @Override // android.app.Service
    public void onDestroy() {
        this.mHandlerThread.quit();
        super.onDestroy();
    }

    /* loaded from: classes3.dex */
    private class IGbaServiceWrapper extends IGbaService.Stub {
        private IGbaServiceWrapper() {
        }

        @Override // android.telephony.gba.IGbaService
        public void authenticationRequest(GbaAuthRequest request) {
            if (GbaService.DBG) {
                Log.m112d(GbaService.TAG, "receive request: " + request);
            }
            GbaService.this.mHandler.obtainMessage(1, request).sendToTarget();
        }
    }
}
