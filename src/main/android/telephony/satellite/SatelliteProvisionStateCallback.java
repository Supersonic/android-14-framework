package android.telephony.satellite;

import android.p008os.Binder;
import android.telephony.satellite.ISatelliteProvisionStateCallback;
import android.telephony.satellite.SatelliteProvisionStateCallback;
import java.util.concurrent.Executor;
/* loaded from: classes3.dex */
public class SatelliteProvisionStateCallback {
    private final CallbackBinder mBinder = new CallbackBinder();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class CallbackBinder extends ISatelliteProvisionStateCallback.Stub {
        private Executor mExecutor;
        private final SatelliteProvisionStateCallback mLocalCallback;

        private CallbackBinder(SatelliteProvisionStateCallback localCallback) {
            this.mLocalCallback = localCallback;
        }

        @Override // android.telephony.satellite.ISatelliteProvisionStateCallback
        public void onSatelliteProvisionStateChanged(final boolean provisioned) {
            long callingIdentity = Binder.clearCallingIdentity();
            try {
                this.mExecutor.execute(new Runnable() { // from class: android.telephony.satellite.SatelliteProvisionStateCallback$CallbackBinder$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        SatelliteProvisionStateCallback.CallbackBinder.this.lambda$onSatelliteProvisionStateChanged$0(provisioned);
                    }
                });
            } finally {
                restoreCallingIdentity(callingIdentity);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSatelliteProvisionStateChanged$0(boolean provisioned) {
            this.mLocalCallback.onSatelliteProvisionStateChanged(provisioned);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setExecutor(Executor executor) {
            this.mExecutor = executor;
        }
    }

    public void onSatelliteProvisionStateChanged(boolean provisioned) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final ISatelliteProvisionStateCallback getBinder() {
        return this.mBinder;
    }

    public void setExecutor(Executor executor) {
        this.mBinder.setExecutor(executor);
    }
}
