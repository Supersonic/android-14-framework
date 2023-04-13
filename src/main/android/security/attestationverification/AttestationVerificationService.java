package android.security.attestationverification;

import android.app.Service;
import android.p008os.Bundle;
/* loaded from: classes3.dex */
public abstract class AttestationVerificationService extends Service {
    public static final String SERVICE_INTERFACE = "android.security.attestationverification.AttestationVerificationService";

    public abstract int onVerifyPeerDeviceAttestation(Bundle bundle, byte[] bArr);
}
