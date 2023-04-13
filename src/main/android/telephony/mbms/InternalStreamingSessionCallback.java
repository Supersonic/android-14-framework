package android.telephony.mbms;

import android.p008os.Binder;
import android.p008os.RemoteException;
import android.telephony.mbms.IMbmsStreamingSessionCallback;
import java.util.List;
import java.util.concurrent.Executor;
/* loaded from: classes3.dex */
public class InternalStreamingSessionCallback extends IMbmsStreamingSessionCallback.Stub {
    private final MbmsStreamingSessionCallback mAppCallback;
    private final Executor mExecutor;
    private volatile boolean mIsStopped = false;

    public InternalStreamingSessionCallback(MbmsStreamingSessionCallback appCallback, Executor executor) {
        this.mAppCallback = appCallback;
        this.mExecutor = executor;
    }

    @Override // android.telephony.mbms.IMbmsStreamingSessionCallback
    public void onError(final int errorCode, final String message) throws RemoteException {
        if (this.mIsStopped) {
            return;
        }
        long token = Binder.clearCallingIdentity();
        try {
            this.mExecutor.execute(new Runnable() { // from class: android.telephony.mbms.InternalStreamingSessionCallback.1
                @Override // java.lang.Runnable
                public void run() {
                    InternalStreamingSessionCallback.this.mAppCallback.onError(errorCode, message);
                }
            });
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    @Override // android.telephony.mbms.IMbmsStreamingSessionCallback
    public void onStreamingServicesUpdated(final List<StreamingServiceInfo> services) throws RemoteException {
        if (this.mIsStopped) {
            return;
        }
        long token = Binder.clearCallingIdentity();
        try {
            this.mExecutor.execute(new Runnable() { // from class: android.telephony.mbms.InternalStreamingSessionCallback.2
                @Override // java.lang.Runnable
                public void run() {
                    InternalStreamingSessionCallback.this.mAppCallback.onStreamingServicesUpdated(services);
                }
            });
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    @Override // android.telephony.mbms.IMbmsStreamingSessionCallback
    public void onMiddlewareReady() throws RemoteException {
        if (this.mIsStopped) {
            return;
        }
        long token = Binder.clearCallingIdentity();
        try {
            this.mExecutor.execute(new Runnable() { // from class: android.telephony.mbms.InternalStreamingSessionCallback.3
                @Override // java.lang.Runnable
                public void run() {
                    InternalStreamingSessionCallback.this.mAppCallback.onMiddlewareReady();
                }
            });
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    public void stop() {
        this.mIsStopped = true;
    }
}
