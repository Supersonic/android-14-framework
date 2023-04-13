package com.android.internal.org.bouncycastle.crypto.macs;

import android.security.keystore.KeyProperties;
import com.android.internal.org.bouncycastle.crypto.CipherParameters;
import com.android.internal.org.bouncycastle.crypto.Digest;
import com.android.internal.org.bouncycastle.crypto.ExtendedDigest;
import com.android.internal.org.bouncycastle.crypto.Mac;
import com.android.internal.org.bouncycastle.crypto.params.KeyParameter;
import com.android.internal.org.bouncycastle.util.Integers;
import com.android.internal.org.bouncycastle.util.Memoable;
import java.util.Hashtable;
/* loaded from: classes4.dex */
public class HMac implements Mac {
    private static final byte IPAD = 54;
    private static final byte OPAD = 92;
    private static Hashtable blockLengths;
    private int blockLength;
    private Digest digest;
    private int digestSize;
    private byte[] inputPad;
    private Memoable ipadState;
    private Memoable opadState;
    private byte[] outputBuf;

    static {
        Hashtable hashtable = new Hashtable();
        blockLengths = hashtable;
        hashtable.put(KeyProperties.DIGEST_MD5, Integers.valueOf(64));
        blockLengths.put("SHA-1", Integers.valueOf(64));
        blockLengths.put(KeyProperties.DIGEST_SHA224, Integers.valueOf(64));
        blockLengths.put("SHA-256", Integers.valueOf(64));
        blockLengths.put(KeyProperties.DIGEST_SHA384, Integers.valueOf(128));
        blockLengths.put(KeyProperties.DIGEST_SHA512, Integers.valueOf(128));
    }

    private static int getByteLength(Digest digest) {
        if (digest instanceof ExtendedDigest) {
            return ((ExtendedDigest) digest).getByteLength();
        }
        Integer b = (Integer) blockLengths.get(digest.getAlgorithmName());
        if (b == null) {
            throw new IllegalArgumentException("unknown digest passed: " + digest.getAlgorithmName());
        }
        return b.intValue();
    }

    public HMac(Digest digest) {
        this(digest, getByteLength(digest));
    }

    private HMac(Digest digest, int byteLength) {
        this.digest = digest;
        int digestSize = digest.getDigestSize();
        this.digestSize = digestSize;
        this.blockLength = byteLength;
        this.inputPad = new byte[byteLength];
        this.outputBuf = new byte[digestSize + byteLength];
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Mac
    public String getAlgorithmName() {
        return this.digest.getAlgorithmName() + "/HMAC";
    }

    public Digest getUnderlyingDigest() {
        return this.digest;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Mac
    public void init(CipherParameters params) {
        byte[] bArr;
        this.digest.reset();
        byte[] key = ((KeyParameter) params).getKey();
        int keyLength = key.length;
        if (keyLength > this.blockLength) {
            this.digest.update(key, 0, keyLength);
            this.digest.doFinal(this.inputPad, 0);
            keyLength = this.digestSize;
        } else {
            System.arraycopy(key, 0, this.inputPad, 0, keyLength);
        }
        int i = keyLength;
        while (true) {
            bArr = this.inputPad;
            if (i >= bArr.length) {
                break;
            }
            bArr[i] = 0;
            i++;
        }
        System.arraycopy(bArr, 0, this.outputBuf, 0, this.blockLength);
        xorPad(this.inputPad, this.blockLength, IPAD);
        xorPad(this.outputBuf, this.blockLength, OPAD);
        Digest digest = this.digest;
        if (digest instanceof Memoable) {
            Memoable copy = ((Memoable) digest).copy();
            this.opadState = copy;
            ((Digest) copy).update(this.outputBuf, 0, this.blockLength);
        }
        Digest digest2 = this.digest;
        byte[] bArr2 = this.inputPad;
        digest2.update(bArr2, 0, bArr2.length);
        Digest digest3 = this.digest;
        if (digest3 instanceof Memoable) {
            this.ipadState = ((Memoable) digest3).copy();
        }
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Mac
    public int getMacSize() {
        return this.digestSize;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Mac
    public void update(byte in) {
        this.digest.update(in);
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Mac
    public void update(byte[] in, int inOff, int len) {
        this.digest.update(in, inOff, len);
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Mac
    public int doFinal(byte[] out, int outOff) {
        this.digest.doFinal(this.outputBuf, this.blockLength);
        Memoable memoable = this.opadState;
        if (memoable != null) {
            ((Memoable) this.digest).reset(memoable);
            Digest digest = this.digest;
            digest.update(this.outputBuf, this.blockLength, digest.getDigestSize());
        } else {
            Digest digest2 = this.digest;
            byte[] bArr = this.outputBuf;
            digest2.update(bArr, 0, bArr.length);
        }
        int len = this.digest.doFinal(out, outOff);
        int i = this.blockLength;
        while (true) {
            byte[] bArr2 = this.outputBuf;
            if (i >= bArr2.length) {
                break;
            }
            bArr2[i] = 0;
            i++;
        }
        Memoable memoable2 = this.ipadState;
        if (memoable2 != null) {
            ((Memoable) this.digest).reset(memoable2);
        } else {
            Digest digest3 = this.digest;
            byte[] bArr3 = this.inputPad;
            digest3.update(bArr3, 0, bArr3.length);
        }
        return len;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Mac
    public void reset() {
        this.digest.reset();
        Digest digest = this.digest;
        byte[] bArr = this.inputPad;
        digest.update(bArr, 0, bArr.length);
    }

    private static void xorPad(byte[] pad, int len, byte n) {
        for (int i = 0; i < len; i++) {
            pad[i] = (byte) (pad[i] ^ n);
        }
    }
}
