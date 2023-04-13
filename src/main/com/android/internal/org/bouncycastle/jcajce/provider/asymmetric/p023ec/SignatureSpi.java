package com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.p023ec;

import com.android.internal.org.bouncycastle.crypto.CipherParameters;
import com.android.internal.org.bouncycastle.crypto.DSAExt;
import com.android.internal.org.bouncycastle.crypto.Digest;
import com.android.internal.org.bouncycastle.crypto.digests.AndroidDigestFactory;
import com.android.internal.org.bouncycastle.crypto.digests.NullDigest;
import com.android.internal.org.bouncycastle.crypto.params.ParametersWithRandom;
import com.android.internal.org.bouncycastle.crypto.signers.DSAEncoding;
import com.android.internal.org.bouncycastle.crypto.signers.ECDSASigner;
import com.android.internal.org.bouncycastle.crypto.signers.StandardDSAEncoding;
import com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.DSABase;
import com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
/* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi */
/* loaded from: classes4.dex */
public class SignatureSpi extends DSABase {
    SignatureSpi(Digest digest, DSAExt signer, DSAEncoding encoding) {
        super(digest, signer, encoding);
    }

    @Override // java.security.SignatureSpi
    protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        CipherParameters param = ECUtils.generatePublicKeyParameter(publicKey);
        this.digest.reset();
        this.signer.init(false, param);
    }

    @Override // java.security.SignatureSpi
    protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
        CipherParameters param = ECUtil.generatePrivateKeyParameter(privateKey);
        this.digest.reset();
        if (this.appRandom != null) {
            this.signer.init(true, new ParametersWithRandom(param, this.appRandom));
        } else {
            this.signer.init(true, param);
        }
    }

    @Override // java.security.SignatureSpi
    protected AlgorithmParameters engineGetParameters() {
        return null;
    }

    /* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDSA */
    /* loaded from: classes4.dex */
    public static class ecDSA extends SignatureSpi {
        public ecDSA() {
            super(AndroidDigestFactory.getSHA1(), new ECDSASigner(), StandardDSAEncoding.INSTANCE);
        }
    }

    /* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDSAnone */
    /* loaded from: classes4.dex */
    public static class ecDSAnone extends SignatureSpi {
        public ecDSAnone() {
            super(new NullDigest(), new ECDSASigner(), StandardDSAEncoding.INSTANCE);
        }
    }

    /* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDSA224 */
    /* loaded from: classes4.dex */
    public static class ecDSA224 extends SignatureSpi {
        public ecDSA224() {
            super(AndroidDigestFactory.getSHA224(), new ECDSASigner(), StandardDSAEncoding.INSTANCE);
        }
    }

    /* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDSA256 */
    /* loaded from: classes4.dex */
    public static class ecDSA256 extends SignatureSpi {
        public ecDSA256() {
            super(AndroidDigestFactory.getSHA256(), new ECDSASigner(), StandardDSAEncoding.INSTANCE);
        }
    }

    /* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDSA384 */
    /* loaded from: classes4.dex */
    public static class ecDSA384 extends SignatureSpi {
        public ecDSA384() {
            super(AndroidDigestFactory.getSHA384(), new ECDSASigner(), StandardDSAEncoding.INSTANCE);
        }
    }

    /* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDSA512 */
    /* loaded from: classes4.dex */
    public static class ecDSA512 extends SignatureSpi {
        public ecDSA512() {
            super(AndroidDigestFactory.getSHA512(), new ECDSASigner(), StandardDSAEncoding.INSTANCE);
        }
    }
}
