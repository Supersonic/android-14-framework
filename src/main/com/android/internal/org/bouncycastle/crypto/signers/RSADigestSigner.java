package com.android.internal.org.bouncycastle.crypto.signers;

import android.security.keystore.KeyProperties;
import com.android.internal.org.bouncycastle.asn1.ASN1Encoding;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.DERNull;
import com.android.internal.org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import com.android.internal.org.bouncycastle.asn1.x509.DigestInfo;
import com.android.internal.org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import com.android.internal.org.bouncycastle.crypto.AsymmetricBlockCipher;
import com.android.internal.org.bouncycastle.crypto.CipherParameters;
import com.android.internal.org.bouncycastle.crypto.CryptoException;
import com.android.internal.org.bouncycastle.crypto.DataLengthException;
import com.android.internal.org.bouncycastle.crypto.Digest;
import com.android.internal.org.bouncycastle.crypto.Signer;
import com.android.internal.org.bouncycastle.crypto.encodings.PKCS1Encoding;
import com.android.internal.org.bouncycastle.crypto.engines.RSABlindedEngine;
import com.android.internal.org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import com.android.internal.org.bouncycastle.crypto.params.ParametersWithRandom;
import com.android.internal.org.bouncycastle.util.Arrays;
import java.io.IOException;
import java.util.Hashtable;
/* loaded from: classes4.dex */
public class RSADigestSigner implements Signer {
    private static final Hashtable oidMap;
    private final AlgorithmIdentifier algId;
    private final Digest digest;
    private boolean forSigning;
    private final AsymmetricBlockCipher rsaEngine;

    static {
        Hashtable hashtable = new Hashtable();
        oidMap = hashtable;
        hashtable.put("SHA-1", X509ObjectIdentifiers.id_SHA1);
        hashtable.put(KeyProperties.DIGEST_SHA224, NISTObjectIdentifiers.id_sha224);
        hashtable.put("SHA-256", NISTObjectIdentifiers.id_sha256);
        hashtable.put(KeyProperties.DIGEST_SHA384, NISTObjectIdentifiers.id_sha384);
        hashtable.put(KeyProperties.DIGEST_SHA512, NISTObjectIdentifiers.id_sha512);
        hashtable.put("SHA-512/224", NISTObjectIdentifiers.id_sha512_224);
        hashtable.put("SHA-512/256", NISTObjectIdentifiers.id_sha512_256);
        hashtable.put(KeyProperties.DIGEST_MD5, PKCSObjectIdentifiers.md5);
    }

    public RSADigestSigner(Digest digest) {
        this(digest, (ASN1ObjectIdentifier) oidMap.get(digest.getAlgorithmName()));
    }

    public RSADigestSigner(Digest digest, ASN1ObjectIdentifier digestOid) {
        this.rsaEngine = new PKCS1Encoding(new RSABlindedEngine());
        this.digest = digest;
        if (digestOid != null) {
            this.algId = new AlgorithmIdentifier(digestOid, DERNull.INSTANCE);
        } else {
            this.algId = null;
        }
    }

    public String getAlgorithmName() {
        return this.digest.getAlgorithmName() + "withRSA";
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Signer
    public void init(boolean forSigning, CipherParameters parameters) {
        AsymmetricKeyParameter k;
        this.forSigning = forSigning;
        if (parameters instanceof ParametersWithRandom) {
            k = (AsymmetricKeyParameter) ((ParametersWithRandom) parameters).getParameters();
        } else {
            k = (AsymmetricKeyParameter) parameters;
        }
        if (forSigning && !k.isPrivate()) {
            throw new IllegalArgumentException("signing requires private key");
        }
        if (!forSigning && k.isPrivate()) {
            throw new IllegalArgumentException("verification requires public key");
        }
        reset();
        this.rsaEngine.init(forSigning, parameters);
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Signer
    public void update(byte input) {
        this.digest.update(input);
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Signer
    public void update(byte[] input, int inOff, int length) {
        this.digest.update(input, inOff, length);
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Signer
    public byte[] generateSignature() throws CryptoException, DataLengthException {
        if (!this.forSigning) {
            throw new IllegalStateException("RSADigestSigner not initialised for signature generation.");
        }
        byte[] hash = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(hash, 0);
        try {
            byte[] data = derEncode(hash);
            return this.rsaEngine.processBlock(data, 0, data.length);
        } catch (IOException e) {
            throw new CryptoException("unable to encode signature: " + e.getMessage(), e);
        }
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Signer
    public boolean verifySignature(byte[] signature) {
        if (this.forSigning) {
            throw new IllegalStateException("RSADigestSigner not initialised for verification");
        }
        byte[] hash = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(hash, 0);
        try {
            byte[] sig = this.rsaEngine.processBlock(signature, 0, signature.length);
            byte[] expected = derEncode(hash);
            if (sig.length == expected.length) {
                return Arrays.constantTimeAreEqual(sig, expected);
            }
            if (sig.length == expected.length - 2) {
                int sigOffset = (sig.length - hash.length) - 2;
                int expectedOffset = (expected.length - hash.length) - 2;
                expected[1] = (byte) (expected[1] - 2);
                expected[3] = (byte) (expected[3] - 2);
                int nonEqual = 0;
                for (int i = 0; i < hash.length; i++) {
                    nonEqual |= sig[sigOffset + i] ^ expected[expectedOffset + i];
                }
                for (int i2 = 0; i2 < sigOffset; i2++) {
                    nonEqual |= sig[i2] ^ expected[i2];
                }
                return nonEqual == 0;
            }
            Arrays.constantTimeAreEqual(expected, expected);
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override // com.android.internal.org.bouncycastle.crypto.Signer
    public void reset() {
        this.digest.reset();
    }

    private byte[] derEncode(byte[] hash) throws IOException {
        AlgorithmIdentifier algorithmIdentifier = this.algId;
        if (algorithmIdentifier == null) {
            try {
                DigestInfo.getInstance(hash);
                return hash;
            } catch (IllegalArgumentException e) {
                throw new IOException("malformed DigestInfo for NONEwithRSA hash: " + e.getMessage());
            }
        }
        DigestInfo dInfo = new DigestInfo(algorithmIdentifier, hash);
        return dInfo.getEncoded(ASN1Encoding.DER);
    }
}
