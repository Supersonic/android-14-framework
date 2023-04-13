package com.android.internal.org.bouncycastle.crypto.digests;

import android.security.keystore.KeyProperties;
import com.android.internal.org.bouncycastle.crypto.ExtendedDigest;
import java.security.DigestException;
import java.security.MessageDigest;
/* loaded from: classes4.dex */
public class OpenSSLDigest implements ExtendedDigest {
    private final int byteSize;
    private final MessageDigest delegate;

    public OpenSSLDigest(String algorithm, int byteSize) {
        try {
            this.delegate = MessageDigest.getInstance(algorithm, "AndroidOpenSSL");
            this.byteSize = byteSize;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Digest
    public String getAlgorithmName() {
        return this.delegate.getAlgorithm();
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Digest
    public int getDigestSize() {
        return this.delegate.getDigestLength();
    }

    @Override // com.android.internal.org.bouncycastle.crypto.ExtendedDigest
    public int getByteLength() {
        return this.byteSize;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Digest
    public void reset() {
        this.delegate.reset();
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Digest
    public void update(byte in) {
        this.delegate.update(in);
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Digest
    public void update(byte[] in, int inOff, int len) {
        this.delegate.update(in, inOff, len);
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Digest
    public int doFinal(byte[] out, int outOff) {
        try {
            return this.delegate.digest(out, outOff, out.length - outOff);
        } catch (DigestException e) {
            throw new RuntimeException(e);
        }
    }

    /* loaded from: classes4.dex */
    public static class MD5 extends OpenSSLDigest {
        public MD5() {
            super(KeyProperties.DIGEST_MD5, 64);
        }
    }

    /* loaded from: classes4.dex */
    public static class SHA1 extends OpenSSLDigest {
        public SHA1() {
            super("SHA-1", 64);
        }
    }

    /* loaded from: classes4.dex */
    public static class SHA224 extends OpenSSLDigest {
        public SHA224() {
            super(KeyProperties.DIGEST_SHA224, 64);
        }
    }

    /* loaded from: classes4.dex */
    public static class SHA256 extends OpenSSLDigest {
        public SHA256() {
            super("SHA-256", 64);
        }
    }

    /* loaded from: classes4.dex */
    public static class SHA384 extends OpenSSLDigest {
        public SHA384() {
            super(KeyProperties.DIGEST_SHA384, 128);
        }
    }

    /* loaded from: classes4.dex */
    public static class SHA512 extends OpenSSLDigest {
        public SHA512() {
            super(KeyProperties.DIGEST_SHA512, 128);
        }
    }
}
