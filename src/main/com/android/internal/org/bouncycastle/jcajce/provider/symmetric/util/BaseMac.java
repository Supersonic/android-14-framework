package com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util;

import android.security.keystore.KeyProperties;
import com.android.internal.org.bouncycastle.crypto.CipherParameters;
import com.android.internal.org.bouncycastle.crypto.Mac;
import com.android.internal.org.bouncycastle.crypto.macs.HMac;
import com.android.internal.org.bouncycastle.crypto.params.AEADParameters;
import com.android.internal.org.bouncycastle.crypto.params.KeyParameter;
import com.android.internal.org.bouncycastle.crypto.params.ParametersWithIV;
import com.android.internal.org.bouncycastle.jcajce.PKCS12Key;
import com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.PBE;
import com.android.internal.org.bouncycastle.jcajce.spec.AEADParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Hashtable;
import java.util.Map;
import javax.crypto.MacSpi;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.PBEKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
/* loaded from: classes4.dex */
public class BaseMac extends MacSpi implements PBE {
    private static final Class gcmSpecClass = ClassUtil.loadClass(BaseMac.class, "javax.crypto.spec.GCMParameterSpec");
    private int keySize;
    private Mac macEngine;
    private int pbeHash;
    private int scheme;

    /* JADX INFO: Access modifiers changed from: protected */
    public BaseMac(Mac macEngine) {
        this.scheme = 2;
        this.pbeHash = 1;
        this.keySize = 160;
        this.macEngine = macEngine;
    }

    protected BaseMac(Mac macEngine, int scheme, int pbeHash, int keySize) {
        this.scheme = 2;
        this.pbeHash = 1;
        this.keySize = 160;
        this.macEngine = macEngine;
        this.scheme = scheme;
        this.pbeHash = pbeHash;
        this.keySize = keySize;
    }

    @Override // javax.crypto.MacSpi
    protected void engineInit(Key key, AlgorithmParameterSpec params) throws InvalidKeyException, InvalidAlgorithmParameterException {
        CipherParameters param;
        KeyParameter keyParam;
        if (key == null) {
            throw new InvalidKeyException("key is null");
        }
        if (key instanceof PKCS12Key) {
            try {
                SecretKey k = (SecretKey) key;
                try {
                    PBEParameterSpec pbeSpec = (PBEParameterSpec) params;
                    if ((k instanceof PBEKey) && pbeSpec == null) {
                        pbeSpec = new PBEParameterSpec(((PBEKey) k).getSalt(), ((PBEKey) k).getIterationCount());
                    }
                    int digest = 1;
                    int keySize = 160;
                    Mac mac = this.macEngine;
                    if ((mac instanceof HMac) && !mac.getAlgorithmName().startsWith("SHA-1")) {
                        if (this.macEngine.getAlgorithmName().startsWith(KeyProperties.DIGEST_SHA224)) {
                            digest = 7;
                            keySize = 224;
                        } else if (this.macEngine.getAlgorithmName().startsWith("SHA-256")) {
                            digest = 4;
                            keySize = 256;
                        } else if (this.macEngine.getAlgorithmName().startsWith(KeyProperties.DIGEST_SHA384)) {
                            digest = 8;
                            keySize = 384;
                        } else if (this.macEngine.getAlgorithmName().startsWith(KeyProperties.DIGEST_SHA512)) {
                            digest = 9;
                            keySize = 512;
                        } else {
                            throw new InvalidAlgorithmParameterException("no PKCS12 mapping for HMAC: " + this.macEngine.getAlgorithmName());
                        }
                    }
                    param = PBE.Util.makePBEMacParameters(k, 2, digest, keySize, pbeSpec);
                } catch (Exception e) {
                    throw new InvalidAlgorithmParameterException("PKCS12 requires a PBEParameterSpec");
                }
            } catch (Exception e2) {
                throw new InvalidKeyException("PKCS12 requires a SecretKey/PBEKey");
            }
        } else if (key instanceof BCPBEKey) {
            BCPBEKey k2 = (BCPBEKey) key;
            if (k2.getParam() != null) {
                param = k2.getParam();
            } else if (params instanceof PBEParameterSpec) {
                param = PBE.Util.makePBEMacParameters(k2, params);
            } else {
                throw new InvalidAlgorithmParameterException("PBE requires PBE parameters to be set.");
            }
        } else if (params instanceof PBEParameterSpec) {
            throw new InvalidAlgorithmParameterException("inappropriate parameter type: " + params.getClass().getName());
        } else {
            param = new KeyParameter(key.getEncoded());
        }
        if (param instanceof ParametersWithIV) {
            keyParam = (KeyParameter) param.getParameters();
        } else {
            KeyParameter keyParam2 = param;
            keyParam = keyParam2;
        }
        if (params instanceof AEADParameterSpec) {
            AEADParameterSpec aeadSpec = (AEADParameterSpec) params;
            param = new AEADParameters(keyParam, aeadSpec.getMacSizeInBits(), aeadSpec.getNonce(), aeadSpec.getAssociatedData());
        } else if (params instanceof IvParameterSpec) {
            param = new ParametersWithIV(keyParam, ((IvParameterSpec) params).getIV());
        } else if (params == null) {
            param = new KeyParameter(key.getEncoded());
        } else {
            Class cls = gcmSpecClass;
            if (cls != null && cls.isAssignableFrom(params.getClass())) {
                param = GcmSpecUtil.extractAeadParameters(keyParam, params);
            } else if (!(params instanceof PBEParameterSpec)) {
                throw new InvalidAlgorithmParameterException("unknown parameter type: " + params.getClass().getName());
            }
        }
        try {
            this.macEngine.init(param);
        } catch (Exception e3) {
            throw new InvalidAlgorithmParameterException("cannot initialize MAC: " + e3.getMessage());
        }
    }

    @Override // javax.crypto.MacSpi
    protected int engineGetMacLength() {
        return this.macEngine.getMacSize();
    }

    @Override // javax.crypto.MacSpi
    protected void engineReset() {
        this.macEngine.reset();
    }

    @Override // javax.crypto.MacSpi
    protected void engineUpdate(byte input) {
        this.macEngine.update(input);
    }

    @Override // javax.crypto.MacSpi
    protected void engineUpdate(byte[] input, int offset, int len) {
        this.macEngine.update(input, offset, len);
    }

    @Override // javax.crypto.MacSpi
    protected byte[] engineDoFinal() {
        byte[] out = new byte[engineGetMacLength()];
        this.macEngine.doFinal(out, 0);
        return out;
    }

    private static Hashtable copyMap(Map paramsMap) {
        Hashtable newTable = new Hashtable();
        for (Object key : paramsMap.keySet()) {
            newTable.put(key, paramsMap.get(key));
        }
        return newTable;
    }
}
