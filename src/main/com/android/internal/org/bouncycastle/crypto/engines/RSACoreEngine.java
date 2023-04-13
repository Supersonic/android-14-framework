package com.android.internal.org.bouncycastle.crypto.engines;

import com.android.internal.org.bouncycastle.crypto.CipherParameters;
import com.android.internal.org.bouncycastle.crypto.DataLengthException;
import com.android.internal.org.bouncycastle.crypto.params.ParametersWithRandom;
import com.android.internal.org.bouncycastle.crypto.params.RSAKeyParameters;
import com.android.internal.org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import com.android.internal.org.bouncycastle.util.Arrays;
import java.math.BigInteger;
/* loaded from: classes4.dex */
class RSACoreEngine {
    private boolean forEncryption;
    private RSAKeyParameters key;

    public void init(boolean forEncryption, CipherParameters param) {
        if (param instanceof ParametersWithRandom) {
            ParametersWithRandom rParam = (ParametersWithRandom) param;
            this.key = (RSAKeyParameters) rParam.getParameters();
        } else {
            this.key = (RSAKeyParameters) param;
        }
        this.forEncryption = forEncryption;
    }

    public int getInputBlockSize() {
        int bitSize = this.key.getModulus().bitLength();
        if (this.forEncryption) {
            return ((bitSize + 7) / 8) - 1;
        }
        return (bitSize + 7) / 8;
    }

    public int getOutputBlockSize() {
        int bitSize = this.key.getModulus().bitLength();
        if (this.forEncryption) {
            return (bitSize + 7) / 8;
        }
        return ((bitSize + 7) / 8) - 1;
    }

    public BigInteger convertInput(byte[] in, int inOff, int inLen) {
        byte[] block;
        if (inLen > getInputBlockSize() + 1) {
            throw new DataLengthException("input too large for RSA cipher.");
        }
        if (inLen == getInputBlockSize() + 1 && !this.forEncryption) {
            throw new DataLengthException("input too large for RSA cipher.");
        }
        if (inOff != 0 || inLen != in.length) {
            block = new byte[inLen];
            System.arraycopy(in, inOff, block, 0, inLen);
        } else {
            block = in;
        }
        BigInteger res = new BigInteger(1, block);
        if (res.compareTo(this.key.getModulus()) < 0) {
            return res;
        }
        throw new DataLengthException("input too large for RSA cipher.");
    }

    public byte[] convertOutput(BigInteger result) {
        byte[] rv;
        byte[] output = result.toByteArray();
        if (this.forEncryption) {
            if (output[0] == 0 && output.length > getOutputBlockSize()) {
                byte[] tmp = new byte[output.length - 1];
                System.arraycopy(output, 1, tmp, 0, tmp.length);
                return tmp;
            } else if (output.length < getOutputBlockSize()) {
                byte[] tmp2 = new byte[getOutputBlockSize()];
                System.arraycopy(output, 0, tmp2, tmp2.length - output.length, output.length);
                return tmp2;
            } else {
                return output;
            }
        }
        if (output[0] == 0) {
            rv = new byte[output.length - 1];
            System.arraycopy(output, 1, rv, 0, rv.length);
        } else {
            rv = new byte[output.length];
            System.arraycopy(output, 0, rv, 0, rv.length);
        }
        Arrays.fill(output, (byte) 0);
        return rv;
    }

    public BigInteger processBlock(BigInteger input) {
        RSAKeyParameters rSAKeyParameters = this.key;
        if (rSAKeyParameters instanceof RSAPrivateCrtKeyParameters) {
            RSAPrivateCrtKeyParameters crtKey = (RSAPrivateCrtKeyParameters) rSAKeyParameters;
            BigInteger p = crtKey.getP();
            BigInteger q = crtKey.getQ();
            BigInteger dP = crtKey.getDP();
            BigInteger dQ = crtKey.getDQ();
            BigInteger qInv = crtKey.getQInv();
            BigInteger mP = input.remainder(p).modPow(dP, p);
            BigInteger mQ = input.remainder(q).modPow(dQ, q);
            BigInteger h = mP.subtract(mQ);
            BigInteger m = h.multiply(qInv).mod(p).multiply(q);
            return m.add(mQ);
        }
        return input.modPow(rSAKeyParameters.getExponent(), this.key.getModulus());
    }
}
