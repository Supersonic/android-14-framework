package android.telephony.satellite;

import android.p008os.Binder;
import android.telephony.satellite.ISatellitePositionUpdateCallback;
import android.telephony.satellite.SatellitePositionUpdateCallback;
import java.util.concurrent.Executor;
/* loaded from: classes3.dex */
public class SatellitePositionUpdateCallback {
    private final CallbackBinder mBinder = new CallbackBinder();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class CallbackBinder extends ISatellitePositionUpdateCallback.Stub {
        private Executor mExecutor;
        private final SatellitePositionUpdateCallback mLocalCallback;

        private CallbackBinder(SatellitePositionUpdateCallback localCallback) {
            this.mLocalCallback = localCallback;
        }

        @Override // android.telephony.satellite.ISatellitePositionUpdateCallback
        public void onSatellitePositionChanged(final PointingInfo pointingInfo) {
            long callingIdentity = Binder.clearCallingIdentity();
            try {
                this.mExecutor.execute(new Runnable() { // from class: android.telephony.satellite.SatellitePositionUpdateCallback$CallbackBinder$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        SatellitePositionUpdateCallback.CallbackBinder.this.lambda$onSatellitePositionChanged$0(pointingInfo);
                    }
                });
            } finally {
                restoreCallingIdentity(callingIdentity);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSatellitePositionChanged$0(PointingInfo pointingInfo) {
            this.mLocalCallback.onSatellitePositionChanged(pointingInfo);
        }

        @Override // android.telephony.satellite.ISatellitePositionUpdateCallback
        public void onDatagramTransferStateChanged(final int state, final int sendPendingCount, final int receivePendingCount, final int errorCode) {
            long callingIdentity = Binder.clearCallingIdentity();
            try {
                this.mExecutor.execute(new Runnable() { // from class: android.telephony.satellite.SatellitePositionUpdateCallback$CallbackBinder$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        SatellitePositionUpdateCallback.CallbackBinder.this.lambda$onDatagramTransferStateChanged$1(state, sendPendingCount, receivePendingCount, errorCode);
                    }
                });
            } finally {
                restoreCallingIdentity(callingIdentity);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onDatagramTransferStateChanged$1(int state, int sendPendingCount, int receivePendingCount, int errorCode) {
            this.mLocalCallback.onDatagramTransferStateChanged(state, sendPendingCount, receivePendingCount, errorCode);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setExecutor(Executor executor) {
            this.mExecutor = executor;
        }
    }

    public void onSatellitePositionChanged(PointingInfo pointingInfo) {
    }

    public void onDatagramTransferStateChanged(int state, int sendPendingCount, int receivePendingCount, int errorCode) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final ISatellitePositionUpdateCallback getBinder() {
        return this.mBinder;
    }

    public void setExecutor(Executor executor) {
        this.mBinder.setExecutor(executor);
    }
}
