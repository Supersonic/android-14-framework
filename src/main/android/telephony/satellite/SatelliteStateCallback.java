package android.telephony.satellite;

import android.p008os.Binder;
import android.telephony.satellite.ISatelliteStateCallback;
import android.telephony.satellite.SatelliteStateCallback;
import java.util.concurrent.Executor;
/* loaded from: classes3.dex */
public class SatelliteStateCallback {
    private final CallbackBinder mBinder = new CallbackBinder();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class CallbackBinder extends ISatelliteStateCallback.Stub {
        private Executor mExecutor;
        private final SatelliteStateCallback mLocalCallback;

        private CallbackBinder(SatelliteStateCallback localCallback) {
            this.mLocalCallback = localCallback;
        }

        @Override // android.telephony.satellite.ISatelliteStateCallback
        public void onSatelliteModemStateChanged(final int state) {
            long callingIdentity = Binder.clearCallingIdentity();
            try {
                this.mExecutor.execute(new Runnable() { // from class: android.telephony.satellite.SatelliteStateCallback$CallbackBinder$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        SatelliteStateCallback.CallbackBinder.this.lambda$onSatelliteModemStateChanged$0(state);
                    }
                });
            } finally {
                restoreCallingIdentity(callingIdentity);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSatelliteModemStateChanged$0(int state) {
            this.mLocalCallback.onSatelliteModemStateChanged(state);
        }

        @Override // android.telephony.satellite.ISatelliteStateCallback
        public void onPendingDatagramCount(final int count) {
            long callingIdentity = Binder.clearCallingIdentity();
            try {
                this.mExecutor.execute(new Runnable() { // from class: android.telephony.satellite.SatelliteStateCallback$CallbackBinder$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        SatelliteStateCallback.CallbackBinder.this.lambda$onPendingDatagramCount$1(count);
                    }
                });
            } finally {
                restoreCallingIdentity(callingIdentity);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onPendingDatagramCount$1(int count) {
            this.mLocalCallback.onPendingDatagramCount(count);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setExecutor(Executor executor) {
            this.mExecutor = executor;
        }
    }

    public void onSatelliteModemStateChanged(int state) {
    }

    public void onPendingDatagramCount(int count) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final ISatelliteStateCallback getBinder() {
        return this.mBinder;
    }

    public void setExecutor(Executor executor) {
        this.mBinder.setExecutor(executor);
    }
}
