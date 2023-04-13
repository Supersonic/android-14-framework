package android.service.translation;

import android.p008os.DeadObjectException;
import android.p008os.RemoteException;
import android.util.Log;
import android.view.translation.TranslationResponse;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
/* loaded from: classes3.dex */
final class OnTranslationResultCallbackWrapper implements Consumer<TranslationResponse> {
    private static final String TAG = "OnTranslationResultCallback";
    private final ITranslationCallback mCallback;
    private final AtomicBoolean mCalled = new AtomicBoolean();

    public OnTranslationResultCallbackWrapper(ITranslationCallback callback) {
        this.mCallback = (ITranslationCallback) Objects.requireNonNull(callback);
    }

    @Override // java.util.function.Consumer
    public void accept(TranslationResponse response) {
        assertNotCalled();
        if (this.mCalled.getAndSet(response.isFinalResponse())) {
            throw new IllegalStateException("Already called with complete response");
        }
        try {
            this.mCallback.onTranslationResponse(response);
        } catch (RemoteException e) {
            if (e instanceof DeadObjectException) {
                Log.m104w(TAG, "Process is dead, ignore.");
                return;
            }
            throw e.rethrowAsRuntimeException();
        }
    }

    private void assertNotCalled() {
        if (this.mCalled.get()) {
            throw new IllegalStateException("Already called");
        }
    }
}
