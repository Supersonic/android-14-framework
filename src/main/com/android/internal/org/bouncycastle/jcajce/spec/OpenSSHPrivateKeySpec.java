package com.android.internal.org.bouncycastle.jcajce.spec;

import java.security.spec.EncodedKeySpec;
/* loaded from: classes4.dex */
public class OpenSSHPrivateKeySpec extends EncodedKeySpec {
    private final String format;

    public OpenSSHPrivateKeySpec(byte[] encodedKey) {
        super(encodedKey);
        if (encodedKey[0] == 48) {
            this.format = "ASN.1";
        } else if (encodedKey[0] == 111) {
            this.format = "OpenSSH";
        } else {
            throw new IllegalArgumentException("unknown byte encoding");
        }
    }

    @Override // java.security.spec.EncodedKeySpec
    public String getFormat() {
        return this.format;
    }
}
