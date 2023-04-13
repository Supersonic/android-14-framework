package android.telephony.mbms;

import android.p008os.Binder;
import android.telephony.mbms.IMbmsGroupCallSessionCallback;
import java.util.List;
import java.util.concurrent.Executor;
/* loaded from: classes3.dex */
public class InternalGroupCallSessionCallback extends IMbmsGroupCallSessionCallback.Stub {
    private final MbmsGroupCallSessionCallback mAppCallback;
    private final Executor mExecutor;
    private volatile boolean mIsStopped = false;

    public InternalGroupCallSessionCallback(MbmsGroupCallSessionCallback appCallback, Executor executor) {
        this.mAppCallback = appCallback;
        this.mExecutor = executor;
    }

    @Override // android.telephony.mbms.IMbmsGroupCallSessionCallback
    public void onError(final int errorCode, final String message) {
        if (this.mIsStopped) {
            return;
        }
        long token = Binder.clearCallingIdentity();
        try {
            this.mExecutor.execute(new Runnable() { // from class: android.telephony.mbms.InternalGroupCallSessionCallback.1
                @Override // java.lang.Runnable
                public void run() {
                    InternalGroupCallSessionCallback.this.mAppCallback.onError(errorCode, message);
                }
            });
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    @Override // android.telephony.mbms.IMbmsGroupCallSessionCallback
    public void onAvailableSaisUpdated(final List currentSais, final List availableSais) {
        if (this.mIsStopped) {
            return;
        }
        long token = Binder.clearCallingIdentity();
        try {
            this.mExecutor.execute(new Runnable() { // from class: android.telephony.mbms.InternalGroupCallSessionCallback.2
                @Override // java.lang.Runnable
                public void run() {
                    InternalGroupCallSessionCallback.this.mAppCallback.onAvailableSaisUpdated(currentSais, availableSais);
                }
            });
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    @Override // android.telephony.mbms.IMbmsGroupCallSessionCallback
    public void onServiceInterfaceAvailable(final String interfaceName, final int index) {
        if (this.mIsStopped) {
            return;
        }
        long token = Binder.clearCallingIdentity();
        try {
            this.mExecutor.execute(new Runnable() { // from class: android.telephony.mbms.InternalGroupCallSessionCallback.3
                @Override // java.lang.Runnable
                public void run() {
                    InternalGroupCallSessionCallback.this.mAppCallback.onServiceInterfaceAvailable(interfaceName, index);
                }
            });
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    @Override // android.telephony.mbms.IMbmsGroupCallSessionCallback
    public void onMiddlewareReady() {
        if (this.mIsStopped) {
            return;
        }
        long token = Binder.clearCallingIdentity();
        try {
            this.mExecutor.execute(new Runnable() { // from class: android.telephony.mbms.InternalGroupCallSessionCallback.4
                @Override // java.lang.Runnable
                public void run() {
                    InternalGroupCallSessionCallback.this.mAppCallback.onMiddlewareReady();
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
