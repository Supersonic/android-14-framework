package android.service.voice;

import android.annotation.SystemApi;
import android.p008os.Bundle;
import android.p008os.IRemoteCallback;
import android.p008os.PersistableBundle;
import android.p008os.RemoteException;
import android.p008os.SharedMemory;
import java.util.function.IntConsumer;
@SystemApi
/* loaded from: classes3.dex */
public interface SandboxedDetectionInitializer {
    public static final int INITIALIZATION_STATUS_SUCCESS = 0;
    public static final int INITIALIZATION_STATUS_UNKNOWN = 100;
    public static final String KEY_INITIALIZATION_STATUS = "initialization_status";
    public static final int MAXIMUM_NUMBER_OF_INITIALIZATION_STATUS_CUSTOM_ERROR = 2;

    void onUpdateState(PersistableBundle persistableBundle, SharedMemory sharedMemory, long j, IntConsumer intConsumer);

    static int getMaxCustomInitializationStatus() {
        return 2;
    }

    static IntConsumer createInitializationStatusConsumer(final IRemoteCallback callback) {
        if (callback == null) {
            return null;
        }
        IntConsumer intConsumer = new IntConsumer() { // from class: android.service.voice.SandboxedDetectionInitializer$$ExternalSyntheticLambda0
            @Override // java.util.function.IntConsumer
            public final void accept(int i) {
                SandboxedDetectionInitializer.lambda$createInitializationStatusConsumer$0(IRemoteCallback.this, i);
            }
        };
        return intConsumer;
    }

    static /* synthetic */ void lambda$createInitializationStatusConsumer$0(IRemoteCallback callback, int value) {
        if (value > getMaxCustomInitializationStatus()) {
            throw new IllegalArgumentException("The initialization status is invalid for " + value);
        }
        try {
            Bundle status = new Bundle();
            status.putInt("initialization_status", value);
            callback.sendResult(status);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }
}
