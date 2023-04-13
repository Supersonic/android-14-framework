package android.service.autofill;

import android.content.IntentSender;
import android.p008os.RemoteException;
import android.util.Log;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class SaveCallback {
    private static final String TAG = "SaveCallback";
    private final ISaveCallback mCallback;
    private boolean mCalled;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SaveCallback(ISaveCallback callback) {
        this.mCallback = callback;
    }

    public void onSuccess() {
        onSuccessInternal(null);
    }

    public void onSuccess(IntentSender intentSender) {
        onSuccessInternal((IntentSender) Objects.requireNonNull(intentSender));
    }

    private void onSuccessInternal(IntentSender intentSender) {
        assertNotCalled();
        this.mCalled = true;
        try {
            this.mCallback.onSuccess(intentSender);
        } catch (RemoteException e) {
            e.rethrowAsRuntimeException();
        }
    }

    public void onFailure(CharSequence message) {
        Log.m104w(TAG, "onFailure(): " + ((Object) message));
        assertNotCalled();
        this.mCalled = true;
        try {
            this.mCallback.onFailure(message);
        } catch (RemoteException e) {
            e.rethrowAsRuntimeException();
        }
    }

    private void assertNotCalled() {
        if (this.mCalled) {
            throw new IllegalStateException("Already called");
        }
    }
}
