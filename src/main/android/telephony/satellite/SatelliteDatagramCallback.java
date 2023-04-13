package android.telephony.satellite;

import android.p008os.Binder;
import android.telephony.satellite.ISatelliteDatagramCallback;
import android.telephony.satellite.SatelliteDatagramCallback;
import com.android.internal.telephony.ILongConsumer;
import java.util.concurrent.Executor;
/* loaded from: classes3.dex */
public class SatelliteDatagramCallback {
    private final CallbackBinder mBinder = new CallbackBinder();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class CallbackBinder extends ISatelliteDatagramCallback.Stub {
        private Executor mExecutor;
        private final SatelliteDatagramCallback mLocalCallback;

        private CallbackBinder(SatelliteDatagramCallback localCallback) {
            this.mLocalCallback = localCallback;
        }

        @Override // android.telephony.satellite.ISatelliteDatagramCallback
        public void onSatelliteDatagramReceived(final long datagramId, final SatelliteDatagram datagram, final int pendingCount, final ILongConsumer callback) {
            long callingIdentity = Binder.clearCallingIdentity();
            try {
                this.mExecutor.execute(new Runnable() { // from class: android.telephony.satellite.SatelliteDatagramCallback$CallbackBinder$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        SatelliteDatagramCallback.CallbackBinder.this.lambda$onSatelliteDatagramReceived$0(datagramId, datagram, pendingCount, callback);
                    }
                });
            } finally {
                restoreCallingIdentity(callingIdentity);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSatelliteDatagramReceived$0(long datagramId, SatelliteDatagram datagram, int pendingCount, ILongConsumer callback) {
            this.mLocalCallback.onSatelliteDatagramReceived(datagramId, datagram, pendingCount, callback);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setExecutor(Executor executor) {
            this.mExecutor = executor;
        }
    }

    public void onSatelliteDatagramReceived(long datagramId, SatelliteDatagram datagram, int pendingCount, ILongConsumer callback) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final ISatelliteDatagramCallback getBinder() {
        return this.mBinder;
    }

    public void setExecutor(Executor executor) {
        this.mBinder.setExecutor(executor);
    }
}
