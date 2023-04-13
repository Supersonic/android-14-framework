package android.security.keystore;

import android.annotation.SystemApi;
@SystemApi
/* loaded from: classes3.dex */
public class DeviceIdAttestationException extends Exception {
    public DeviceIdAttestationException(String detailMessage) {
        super(detailMessage);
    }

    public DeviceIdAttestationException(String message, Throwable cause) {
        super(message, cause);
    }
}
