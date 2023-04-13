package com.android.internal.org.bouncycastle.crypto.agreement;

import com.android.internal.org.bouncycastle.crypto.BasicAgreement;
import com.android.internal.org.bouncycastle.crypto.CipherParameters;
import com.android.internal.org.bouncycastle.crypto.params.ECDomainParameters;
import com.android.internal.org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import com.android.internal.org.bouncycastle.crypto.params.ECPublicKeyParameters;
import com.android.internal.org.bouncycastle.math.p025ec.ECAlgorithms;
import com.android.internal.org.bouncycastle.math.p025ec.ECConstants;
import com.android.internal.org.bouncycastle.math.p025ec.ECPoint;
import java.math.BigInteger;
/* loaded from: classes4.dex */
public class ECDHBasicAgreement implements BasicAgreement {
    private ECPrivateKeyParameters key;

    @Override // com.android.internal.org.bouncycastle.crypto.BasicAgreement
    public void init(CipherParameters key) {
        this.key = (ECPrivateKeyParameters) key;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.BasicAgreement
    public int getFieldSize() {
        return (this.key.getParameters().getCurve().getFieldSize() + 7) / 8;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.BasicAgreement
    public BigInteger calculateAgreement(CipherParameters pubKey) {
        ECPublicKeyParameters pub = (ECPublicKeyParameters) pubKey;
        ECDomainParameters params = this.key.getParameters();
        if (!params.equals(pub.getParameters())) {
            throw new IllegalStateException("ECDH public key has wrong domain parameters");
        }
        BigInteger d = this.key.getD();
        ECPoint Q = ECAlgorithms.cleanPoint(params.getCurve(), pub.getQ());
        if (Q.isInfinity()) {
            throw new IllegalStateException("Infinity is not a valid public key for ECDH");
        }
        BigInteger h = params.getH();
        if (!h.equals(ECConstants.ONE)) {
            d = params.getHInv().multiply(d).mod(params.getN());
            Q = ECAlgorithms.referenceMultiply(Q, h);
        }
        ECPoint P = Q.multiply(d).normalize();
        if (P.isInfinity()) {
            throw new IllegalStateException("Infinity is not a valid agreement value for ECDH");
        }
        return P.getAffineXCoord().toBigInteger();
    }
}
