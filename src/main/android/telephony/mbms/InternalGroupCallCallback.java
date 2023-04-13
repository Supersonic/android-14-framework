package android.telephony.mbms;

import android.p008os.Binder;
import android.telephony.mbms.IGroupCallCallback;
import java.util.concurrent.Executor;
/* loaded from: classes3.dex */
public class InternalGroupCallCallback extends IGroupCallCallback.Stub {
    private final GroupCallCallback mAppCallback;
    private final Executor mExecutor;
    private volatile boolean mIsStopped = false;

    public InternalGroupCallCallback(GroupCallCallback appCallback, Executor executor) {
        this.mAppCallback = appCallback;
        this.mExecutor = executor;
    }

    @Override // android.telephony.mbms.IGroupCallCallback
    public void onError(final int errorCode, final String message) {
        if (this.mIsStopped) {
            return;
        }
        long token = Binder.clearCallingIdentity();
        try {
            this.mExecutor.execute(new Runnable() { // from class: android.telephony.mbms.InternalGroupCallCallback.1
                @Override // java.lang.Runnable
                public void run() {
                    InternalGroupCallCallback.this.mAppCallback.onError(errorCode, message);
                }
            });
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    @Override // android.telephony.mbms.IGroupCallCallback
    public void onGroupCallStateChanged(final int state, final int reason) {
        if (this.mIsStopped) {
            return;
        }
        long token = Binder.clearCallingIdentity();
        try {
            this.mExecutor.execute(new Runnable() { // from class: android.telephony.mbms.InternalGroupCallCallback.2
                @Override // java.lang.Runnable
                public void run() {
                    InternalGroupCallCallback.this.mAppCallback.onGroupCallStateChanged(state, reason);
                }
            });
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    @Override // android.telephony.mbms.IGroupCallCallback
    public void onBroadcastSignalStrengthUpdated(final int signalStrength) {
        if (this.mIsStopped) {
            return;
        }
        long token = Binder.clearCallingIdentity();
        try {
            this.mExecutor.execute(new Runnable() { // from class: android.telephony.mbms.InternalGroupCallCallback.3
                @Override // java.lang.Runnable
                public void run() {
                    InternalGroupCallCallback.this.mAppCallback.onBroadcastSignalStrengthUpdated(signalStrength);
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
