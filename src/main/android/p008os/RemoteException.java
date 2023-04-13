package android.p008os;

import android.util.AndroidException;
/* renamed from: android.os.RemoteException */
/* loaded from: classes3.dex */
public class RemoteException extends AndroidException {
    public RemoteException() {
    }

    public RemoteException(String message) {
        super(message);
    }

    public RemoteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public RemoteException(Throwable cause) {
        this(cause.getMessage(), cause, true, false);
    }

    public RuntimeException rethrowAsRuntimeException() {
        throw new RuntimeException(this);
    }

    public RuntimeException rethrowFromSystemServer() {
        if (this instanceof DeadObjectException) {
            throw new DeadSystemRuntimeException();
        }
        throw new RuntimeException(this);
    }
}
