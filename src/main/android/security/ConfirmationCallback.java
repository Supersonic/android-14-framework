package android.security;
/* loaded from: classes3.dex */
public abstract class ConfirmationCallback {
    public void onConfirmed(byte[] dataThatWasConfirmed) {
    }

    public void onDismissed() {
    }

    public void onCanceled() {
    }

    public void onError(Throwable e) {
    }
}
