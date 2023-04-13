package com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util;

import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.crypto.CipherParameters;
import com.android.internal.org.bouncycastle.crypto.PBEParametersGenerator;
import com.android.internal.org.bouncycastle.crypto.params.KeyParameter;
import com.android.internal.org.bouncycastle.crypto.params.ParametersWithIV;
import com.android.internal.org.bouncycastle.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.crypto.interfaces.PBEKey;
import javax.crypto.spec.PBEKeySpec;
import javax.security.auth.Destroyable;
/* loaded from: classes4.dex */
public class BCPBEKey implements PBEKey, Destroyable {
    String algorithm;
    int digest;
    private final AtomicBoolean hasBeenDestroyed;
    private final int iterationCount;
    int ivSize;
    int keySize;
    ASN1ObjectIdentifier oid;
    private final CipherParameters param;
    private final char[] password;
    private final byte[] salt;
    boolean tryWrong;
    int type;

    public BCPBEKey(String algorithm, ASN1ObjectIdentifier oid, int type, int digest, int keySize, int ivSize, PBEKeySpec pbeKeySpec, CipherParameters param) {
        this.hasBeenDestroyed = new AtomicBoolean(false);
        this.tryWrong = false;
        this.algorithm = algorithm;
        this.oid = oid;
        this.type = type;
        this.digest = digest;
        this.keySize = keySize;
        this.ivSize = ivSize;
        this.password = pbeKeySpec.getPassword();
        this.iterationCount = pbeKeySpec.getIterationCount();
        this.salt = pbeKeySpec.getSalt();
        this.param = param;
    }

    public BCPBEKey(String algName, CipherParameters param) {
        this.hasBeenDestroyed = new AtomicBoolean(false);
        this.tryWrong = false;
        this.algorithm = algName;
        this.param = param;
        this.password = null;
        this.iterationCount = -1;
        this.salt = null;
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        checkDestroyed(this);
        return this.algorithm;
    }

    @Override // java.security.Key
    public String getFormat() {
        return "RAW";
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        KeyParameter kParam;
        checkDestroyed(this);
        CipherParameters cipherParameters = this.param;
        if (cipherParameters != null) {
            if (cipherParameters instanceof ParametersWithIV) {
                kParam = (KeyParameter) ((ParametersWithIV) cipherParameters).getParameters();
            } else {
                kParam = (KeyParameter) cipherParameters;
            }
            return kParam.getKey();
        }
        int i = this.type;
        if (i == 2) {
            return PBEParametersGenerator.PKCS12PasswordToBytes(this.password);
        }
        if (i == 5) {
            return PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(this.password);
        }
        return PBEParametersGenerator.PKCS5PasswordToBytes(this.password);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getType() {
        checkDestroyed(this);
        return this.type;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getDigest() {
        checkDestroyed(this);
        return this.digest;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getKeySize() {
        checkDestroyed(this);
        return this.keySize;
    }

    public int getIvSize() {
        checkDestroyed(this);
        return this.ivSize;
    }

    public CipherParameters getParam() {
        checkDestroyed(this);
        return this.param;
    }

    @Override // javax.crypto.interfaces.PBEKey
    public char[] getPassword() {
        checkDestroyed(this);
        char[] cArr = this.password;
        if (cArr == null) {
            throw new IllegalStateException("no password available");
        }
        return Arrays.clone(cArr);
    }

    @Override // javax.crypto.interfaces.PBEKey
    public byte[] getSalt() {
        checkDestroyed(this);
        return Arrays.clone(this.salt);
    }

    @Override // javax.crypto.interfaces.PBEKey
    public int getIterationCount() {
        checkDestroyed(this);
        return this.iterationCount;
    }

    public ASN1ObjectIdentifier getOID() {
        checkDestroyed(this);
        return this.oid;
    }

    public void setTryWrongPKCS12Zero(boolean tryWrong) {
        this.tryWrong = tryWrong;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean shouldTryWrongPKCS12() {
        return this.tryWrong;
    }

    @Override // javax.security.auth.Destroyable
    public void destroy() {
        if (!this.hasBeenDestroyed.getAndSet(true)) {
            char[] cArr = this.password;
            if (cArr != null) {
                Arrays.fill(cArr, (char) 0);
            }
            byte[] bArr = this.salt;
            if (bArr != null) {
                Arrays.fill(bArr, (byte) 0);
            }
        }
    }

    @Override // javax.security.auth.Destroyable
    public boolean isDestroyed() {
        return this.hasBeenDestroyed.get();
    }

    static void checkDestroyed(Destroyable destroyable) {
        if (destroyable.isDestroyed()) {
            throw new IllegalStateException("key has been destroyed");
        }
    }
}
