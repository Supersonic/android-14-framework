package android.security;

import java.security.KeyPair;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
/* loaded from: classes3.dex */
public final class AttestedKeyPair {
    private final List<Certificate> mAttestationRecord;
    private final KeyPair mKeyPair;

    public AttestedKeyPair(KeyPair keyPair, List<Certificate> attestationRecord) {
        this.mKeyPair = keyPair;
        this.mAttestationRecord = attestationRecord;
    }

    public AttestedKeyPair(KeyPair keyPair, Certificate[] attestationRecord) {
        this.mKeyPair = keyPair;
        if (attestationRecord == null) {
            this.mAttestationRecord = new ArrayList();
        } else {
            this.mAttestationRecord = Arrays.asList(attestationRecord);
        }
    }

    public KeyPair getKeyPair() {
        return this.mKeyPair;
    }

    public List<Certificate> getAttestationRecord() {
        return Collections.unmodifiableList(this.mAttestationRecord);
    }
}
