package android.security.keystore.recovery;

import android.annotation.SystemApi;
import java.security.GeneralSecurityException;
@SystemApi
/* loaded from: classes3.dex */
public class SessionExpiredException extends GeneralSecurityException {
    public SessionExpiredException(String msg) {
        super(msg);
    }
}
