package com.android.internal.org.bouncycastle.jcajce.provider.digest;

import com.android.internal.org.bouncycastle.crypto.Digest;
import java.security.MessageDigest;
/* loaded from: classes4.dex */
public class BCMessageDigest extends MessageDigest {
    protected Digest digest;
    protected int digestSize;

    /* JADX INFO: Access modifiers changed from: protected */
    public BCMessageDigest(Digest digest) {
        super(digest.getAlgorithmName());
        this.digest = digest;
        this.digestSize = digest.getDigestSize();
    }

    @Override // java.security.MessageDigestSpi
    public void engineReset() {
        this.digest.reset();
    }

    @Override // java.security.MessageDigestSpi
    public void engineUpdate(byte input) {
        this.digest.update(input);
    }

    @Override // java.security.MessageDigestSpi
    public void engineUpdate(byte[] input, int offset, int len) {
        this.digest.update(input, offset, len);
    }

    @Override // java.security.MessageDigestSpi
    public int engineGetDigestLength() {
        return this.digestSize;
    }

    @Override // java.security.MessageDigestSpi
    public byte[] engineDigest() {
        byte[] digestBytes = new byte[this.digestSize];
        this.digest.doFinal(digestBytes, 0);
        return digestBytes;
    }
}
