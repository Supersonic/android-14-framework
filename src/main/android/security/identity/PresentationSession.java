package android.security.identity;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.PublicKey;
/* loaded from: classes3.dex */
public abstract class PresentationSession {
    public abstract CredentialDataResult getCredentialData(String str, CredentialDataRequest credentialDataRequest) throws NoAuthenticationKeyAvailableException, InvalidReaderSignatureException, InvalidRequestMessageException, EphemeralPublicKeyNotFoundException;

    public abstract long getCredstoreOperationHandle();

    public abstract KeyPair getEphemeralKeyPair();

    public abstract void setReaderEphemeralPublicKey(PublicKey publicKey) throws InvalidKeyException;

    public abstract void setSessionTranscript(byte[] bArr);
}
