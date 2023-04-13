package android.p008os;

import android.util.AndroidRuntimeException;
/* renamed from: android.os.BadParcelableException */
/* loaded from: classes3.dex */
public class BadParcelableException extends AndroidRuntimeException {
    public BadParcelableException(String msg) {
        super(msg);
    }

    public BadParcelableException(Exception cause) {
        super(cause);
    }

    public BadParcelableException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
