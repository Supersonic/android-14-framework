package android.telephony.ims.stub;

import android.annotation.SystemApi;
import android.app.PendingIntent$$ExternalSyntheticLambda1;
import android.p008os.RemoteException;
import android.telephony.ims.stub.ImsEcbmImplBase;
import android.util.Log;
import com.android.ims.internal.IImsEcbm;
import com.android.ims.internal.IImsEcbmListener;
import com.android.internal.telephony.util.TelephonyUtils;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
@SystemApi
/* loaded from: classes3.dex */
public class ImsEcbmImplBase {
    private static final String TAG = "ImsEcbmImplBase";
    private IImsEcbmListener mListener;
    private final Object mLock = new Object();
    private Executor mExecutor = new PendingIntent$$ExternalSyntheticLambda1();
    private final IImsEcbm mImsEcbm = new BinderC33021();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.telephony.ims.stub.ImsEcbmImplBase$1 */
    /* loaded from: classes3.dex */
    public class BinderC33021 extends IImsEcbm.Stub {
        BinderC33021() {
        }

        @Override // com.android.ims.internal.IImsEcbm
        public void setListener(final IImsEcbmListener listener) {
            executeMethodAsync(new Runnable() { // from class: android.telephony.ims.stub.ImsEcbmImplBase$1$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    ImsEcbmImplBase.BinderC33021.this.lambda$setListener$0(listener);
                }
            }, "setListener");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$setListener$0(IImsEcbmListener listener) {
            if (ImsEcbmImplBase.this.mListener != null && !ImsEcbmImplBase.this.mListener.asBinder().isBinderAlive()) {
                Log.m104w(ImsEcbmImplBase.TAG, "setListener: discarding dead Binder");
                ImsEcbmImplBase.this.mListener = null;
            }
            if (ImsEcbmImplBase.this.mListener != null && listener != null && Objects.equals(ImsEcbmImplBase.this.mListener.asBinder(), listener.asBinder())) {
                return;
            }
            if (listener == null) {
                ImsEcbmImplBase.this.mListener = null;
            } else if (listener != null && ImsEcbmImplBase.this.mListener == null) {
                ImsEcbmImplBase.this.mListener = listener;
            } else {
                Log.m104w(ImsEcbmImplBase.TAG, "setListener is being called when there is already an active listener");
                ImsEcbmImplBase.this.mListener = listener;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$exitEmergencyCallbackMode$1() {
            ImsEcbmImplBase.this.exitEmergencyCallbackMode();
        }

        @Override // com.android.ims.internal.IImsEcbm
        public void exitEmergencyCallbackMode() {
            executeMethodAsync(new Runnable() { // from class: android.telephony.ims.stub.ImsEcbmImplBase$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ImsEcbmImplBase.BinderC33021.this.lambda$exitEmergencyCallbackMode$1();
                }
            }, "exitEmergencyCallbackMode");
        }

        private void executeMethodAsync(final Runnable r, String errorLogName) {
            try {
                CompletableFuture.runAsync(new Runnable() { // from class: android.telephony.ims.stub.ImsEcbmImplBase$1$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        TelephonyUtils.runWithCleanCallingIdentity(r);
                    }
                }, ImsEcbmImplBase.this.mExecutor).join();
            } catch (CancellationException | CompletionException e) {
                Log.m104w(ImsEcbmImplBase.TAG, "ImsEcbmImplBase Binder - " + errorLogName + " exception: " + e.getMessage());
            }
        }
    }

    public IImsEcbm getImsEcbm() {
        return this.mImsEcbm;
    }

    public void exitEmergencyCallbackMode() {
        Log.m112d(TAG, "exitEmergencyCallbackMode() not implemented");
    }

    public final void enteredEcbm() {
        IImsEcbmListener listener;
        Log.m112d(TAG, "Entered ECBM.");
        synchronized (this.mLock) {
            listener = this.mListener;
        }
        if (listener != null) {
            try {
                listener.enteredECBM();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public final void exitedEcbm() {
        IImsEcbmListener listener;
        Log.m112d(TAG, "Exited ECBM.");
        synchronized (this.mLock) {
            listener = this.mListener;
        }
        if (listener != null) {
            try {
                listener.exitedECBM();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public final void setDefaultExecutor(Executor executor) {
        this.mExecutor = executor;
    }
}
