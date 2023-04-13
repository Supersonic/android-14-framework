package android.p008os;
/* renamed from: android.os.BadTypeParcelableException */
/* loaded from: classes3.dex */
class BadTypeParcelableException extends BadParcelableException {
    /* JADX INFO: Access modifiers changed from: package-private */
    public BadTypeParcelableException(String msg) {
        super(msg);
    }

    BadTypeParcelableException(Exception cause) {
        super(cause);
    }

    BadTypeParcelableException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
