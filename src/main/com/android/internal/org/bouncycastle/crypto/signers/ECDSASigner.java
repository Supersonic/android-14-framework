package com.android.internal.org.bouncycastle.crypto.signers;

import com.android.internal.org.bouncycastle.crypto.CipherParameters;
import com.android.internal.org.bouncycastle.crypto.CryptoServicesRegistrar;
import com.android.internal.org.bouncycastle.crypto.DSAExt;
import com.android.internal.org.bouncycastle.crypto.params.ECDomainParameters;
import com.android.internal.org.bouncycastle.crypto.params.ECKeyParameters;
import com.android.internal.org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import com.android.internal.org.bouncycastle.crypto.params.ECPublicKeyParameters;
import com.android.internal.org.bouncycastle.crypto.params.ParametersWithRandom;
import com.android.internal.org.bouncycastle.math.p025ec.ECAlgorithms;
import com.android.internal.org.bouncycastle.math.p025ec.ECConstants;
import com.android.internal.org.bouncycastle.math.p025ec.ECCurve;
import com.android.internal.org.bouncycastle.math.p025ec.ECFieldElement;
import com.android.internal.org.bouncycastle.math.p025ec.ECMultiplier;
import com.android.internal.org.bouncycastle.math.p025ec.ECPoint;
import com.android.internal.org.bouncycastle.math.p025ec.FixedPointCombMultiplier;
import com.android.internal.org.bouncycastle.util.BigIntegers;
import java.math.BigInteger;
import java.security.SecureRandom;
/* loaded from: classes4.dex */
public class ECDSASigner implements ECConstants, DSAExt {
    private final DSAKCalculator kCalculator;
    private ECKeyParameters key;
    private SecureRandom random;

    public ECDSASigner() {
        this.kCalculator = new RandomDSAKCalculator();
    }

    public ECDSASigner(DSAKCalculator kCalculator) {
        this.kCalculator = kCalculator;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.DSA
    public void init(boolean forSigning, CipherParameters param) {
        SecureRandom providedRandom = null;
        if (forSigning) {
            if (param instanceof ParametersWithRandom) {
                ParametersWithRandom rParam = (ParametersWithRandom) param;
                this.key = (ECPrivateKeyParameters) rParam.getParameters();
                providedRandom = rParam.getRandom();
            } else {
                this.key = (ECPrivateKeyParameters) param;
            }
        } else {
            this.key = (ECPublicKeyParameters) param;
        }
        this.random = initSecureRandom(forSigning && !this.kCalculator.isDeterministic(), providedRandom);
    }

    @Override // com.android.internal.org.bouncycastle.crypto.DSAExt
    public BigInteger getOrder() {
        return this.key.getParameters().getN();
    }

    @Override // com.android.internal.org.bouncycastle.crypto.DSA
    public BigInteger[] generateSignature(byte[] message) {
        ECDomainParameters ec = this.key.getParameters();
        BigInteger n = ec.getN();
        BigInteger e = calculateE(n, message);
        BigInteger d = ((ECPrivateKeyParameters) this.key).getD();
        if (this.kCalculator.isDeterministic()) {
            this.kCalculator.init(n, d, message);
        } else {
            this.kCalculator.init(n, this.random);
        }
        ECMultiplier basePointMultiplier = createBasePointMultiplier();
        while (true) {
            BigInteger k = this.kCalculator.nextK();
            ECPoint p = basePointMultiplier.multiply(ec.getG(), k).normalize();
            BigInteger r = p.getAffineXCoord().toBigInteger().mod(n);
            if (!r.equals(ZERO)) {
                BigInteger k2 = BigIntegers.modOddInverse(n, k).multiply(e.add(d.multiply(r))).mod(n);
                if (!k2.equals(ZERO)) {
                    return new BigInteger[]{r, k2};
                }
            }
        }
    }

    @Override // com.android.internal.org.bouncycastle.crypto.DSA
    public boolean verifySignature(byte[] message, BigInteger r, BigInteger s) {
        BigInteger cofactor;
        ECFieldElement D;
        BigInteger r2 = r;
        ECDomainParameters ec = this.key.getParameters();
        BigInteger n = ec.getN();
        BigInteger e = calculateE(n, message);
        if (r2.compareTo(ONE) < 0 || r2.compareTo(n) >= 0 || s.compareTo(ONE) < 0 || s.compareTo(n) >= 0) {
            return false;
        }
        BigInteger c = BigIntegers.modOddInverseVar(n, s);
        BigInteger u1 = e.multiply(c).mod(n);
        BigInteger u2 = r2.multiply(c).mod(n);
        ECPoint G = ec.getG();
        ECPoint Q = ((ECPublicKeyParameters) this.key).getQ();
        ECPoint point = ECAlgorithms.sumOfTwoMultiplies(G, u1, Q, u2);
        if (point.isInfinity()) {
            return false;
        }
        ECCurve curve = point.getCurve();
        if (curve != null && (cofactor = curve.getCofactor()) != null && cofactor.compareTo(EIGHT) <= 0 && (D = getDenominator(curve.getCoordinateSystem(), point)) != null && !D.isZero()) {
            ECFieldElement X = point.getXCoord();
            while (curve.isValidFieldElement(r2)) {
                ECFieldElement R = curve.fromBigInteger(r2).multiply(D);
                if (R.equals(X)) {
                    return true;
                }
                r2 = r2.add(n);
            }
            return false;
        }
        BigInteger v = point.normalize().getAffineXCoord().toBigInteger().mod(n);
        return v.equals(r2);
    }

    protected BigInteger calculateE(BigInteger n, byte[] message) {
        int log2n = n.bitLength();
        int messageBitLength = message.length * 8;
        BigInteger e = new BigInteger(1, message);
        if (log2n < messageBitLength) {
            return e.shiftRight(messageBitLength - log2n);
        }
        return e;
    }

    protected ECMultiplier createBasePointMultiplier() {
        return new FixedPointCombMultiplier();
    }

    protected ECFieldElement getDenominator(int coordinateSystem, ECPoint p) {
        switch (coordinateSystem) {
            case 1:
            case 6:
            case 7:
                return p.getZCoord(0);
            case 2:
            case 3:
            case 4:
                return p.getZCoord(0).square();
            case 5:
            default:
                return null;
        }
    }

    protected SecureRandom initSecureRandom(boolean needed, SecureRandom provided) {
        if (needed) {
            return CryptoServicesRegistrar.getSecureRandom(provided);
        }
        return null;
    }
}
