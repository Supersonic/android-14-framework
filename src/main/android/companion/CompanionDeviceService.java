package android.companion;

import android.app.Service;
import android.companion.CompanionDeviceService;
import android.companion.ICompanionDeviceService;
import android.content.Intent;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.util.Log;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
/* loaded from: classes.dex */
public abstract class CompanionDeviceService extends Service {
    private static final String LOG_TAG = "CDM_CompanionDeviceService";
    public static final String SERVICE_INTERFACE = "android.companion.CompanionDeviceService";
    private final Stub mRemote = new Stub();

    @Deprecated
    public void onDeviceAppeared(String address) {
    }

    @Deprecated
    public void onDeviceDisappeared(String address) {
    }

    @Deprecated
    public void onMessageDispatchedFromSystem(int messageId, int associationId, byte[] message) {
        Log.m104w(LOG_TAG, "Replaced by attachSystemDataTransport");
    }

    @Deprecated
    public final void dispatchMessageToSystem(int messageId, int associationId, byte[] message) throws DeviceNotAssociatedException {
        Log.m104w(LOG_TAG, "Replaced by attachSystemDataTransport");
    }

    public final void attachSystemDataTransport(int associationId, InputStream in, OutputStream out) throws DeviceNotAssociatedException {
        ((CompanionDeviceManager) getSystemService(CompanionDeviceManager.class)).attachSystemDataTransport(associationId, (InputStream) Objects.requireNonNull(in), (OutputStream) Objects.requireNonNull(out));
    }

    public final void detachSystemDataTransport(int associationId) throws DeviceNotAssociatedException {
        ((CompanionDeviceManager) getSystemService(CompanionDeviceManager.class)).detachSystemDataTransport(associationId);
    }

    public void onDeviceAppeared(AssociationInfo associationInfo) {
        if (!associationInfo.isSelfManaged()) {
            onDeviceAppeared(associationInfo.getDeviceMacAddressAsString());
        }
    }

    public void onDeviceDisappeared(AssociationInfo associationInfo) {
        if (!associationInfo.isSelfManaged()) {
            onDeviceDisappeared(associationInfo.getDeviceMacAddressAsString());
        }
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        if (Objects.equals(intent.getAction(), SERVICE_INTERFACE)) {
            onBindCompanionDeviceService(intent);
            return this.mRemote;
        }
        Log.m104w(LOG_TAG, "Tried to bind to wrong intent (should be android.companion.CompanionDeviceService): " + intent);
        return null;
    }

    public void onBindCompanionDeviceService(Intent intent) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class Stub extends ICompanionDeviceService.Stub {
        final Handler mMainHandler;
        final CompanionDeviceService mService;

        private Stub() {
            this.mMainHandler = Handler.getMain();
            this.mService = CompanionDeviceService.this;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onDeviceAppeared$0(AssociationInfo associationInfo) {
            this.mService.onDeviceAppeared(associationInfo);
        }

        @Override // android.companion.ICompanionDeviceService
        public void onDeviceAppeared(final AssociationInfo associationInfo) {
            this.mMainHandler.postAtFrontOfQueue(new Runnable() { // from class: android.companion.CompanionDeviceService$Stub$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    CompanionDeviceService.Stub.this.lambda$onDeviceAppeared$0(associationInfo);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onDeviceDisappeared$1(AssociationInfo associationInfo) {
            this.mService.onDeviceDisappeared(associationInfo);
        }

        @Override // android.companion.ICompanionDeviceService
        public void onDeviceDisappeared(final AssociationInfo associationInfo) {
            this.mMainHandler.postAtFrontOfQueue(new Runnable() { // from class: android.companion.CompanionDeviceService$Stub$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    CompanionDeviceService.Stub.this.lambda$onDeviceDisappeared$1(associationInfo);
                }
            });
        }
    }
}
