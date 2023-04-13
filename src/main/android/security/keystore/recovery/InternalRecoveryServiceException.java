package android.security.keystore.recovery;

import android.annotation.SystemApi;
import java.security.GeneralSecurityException;
@SystemApi
/* loaded from: classes3.dex */
public class InternalRecoveryServiceException extends GeneralSecurityException {
    public InternalRecoveryServiceException(String msg) {
        super(msg);
    }

    public InternalRecoveryServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
