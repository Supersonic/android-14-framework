package com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.p018x9.ECNamedCurveTable;
import com.android.internal.org.bouncycastle.asn1.p018x9.X962Parameters;
import com.android.internal.org.bouncycastle.asn1.p018x9.X9ECParameters;
import com.android.internal.org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import com.android.internal.org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import com.android.internal.org.bouncycastle.crypto.p019ec.CustomNamedCurves;
import com.android.internal.org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import com.android.internal.org.bouncycastle.crypto.params.ECDomainParameters;
import com.android.internal.org.bouncycastle.crypto.params.ECNamedDomainParameters;
import com.android.internal.org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import com.android.internal.org.bouncycastle.crypto.params.ECPublicKeyParameters;
import com.android.internal.org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import com.android.internal.org.bouncycastle.jce.interfaces.ECPrivateKey;
import com.android.internal.org.bouncycastle.jce.interfaces.ECPublicKey;
import com.android.internal.org.bouncycastle.jce.provider.BouncyCastleProvider;
import com.android.internal.org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import com.android.internal.org.bouncycastle.jce.spec.ECParameterSpec;
import com.android.internal.org.bouncycastle.math.p025ec.ECCurve;
import com.android.internal.org.bouncycastle.math.p025ec.ECPoint;
import com.android.internal.org.bouncycastle.math.p025ec.FixedPointCombMultiplier;
import com.android.internal.org.bouncycastle.util.Arrays;
import com.android.internal.org.bouncycastle.util.Fingerprint;
import com.android.internal.org.bouncycastle.util.Strings;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.AccessController;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PrivilegedAction;
import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Enumeration;
import java.util.Map;
/* loaded from: classes4.dex */
public class ECUtil {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static int[] convertMidTerms(int[] k) {
        int[] res = new int[3];
        if (k.length != 1) {
            if (k.length != 3) {
                throw new IllegalArgumentException("Only Trinomials and pentanomials supported");
            }
            if (k[0] < k[1] && k[0] < k[2]) {
                res[0] = k[0];
                if (k[1] < k[2]) {
                    res[1] = k[1];
                    res[2] = k[2];
                } else {
                    res[1] = k[2];
                    res[2] = k[1];
                }
            } else if (k[1] < k[2]) {
                res[0] = k[1];
                if (k[0] < k[2]) {
                    res[1] = k[0];
                    res[2] = k[2];
                } else {
                    res[1] = k[2];
                    res[2] = k[0];
                }
            } else {
                res[0] = k[2];
                if (k[0] < k[1]) {
                    res[1] = k[0];
                    res[2] = k[1];
                } else {
                    res[1] = k[1];
                    res[2] = k[0];
                }
            }
        } else {
            res[0] = k[0];
        }
        return res;
    }

    public static ECDomainParameters getDomainParameters(ProviderConfiguration configuration, ECParameterSpec params) {
        if (params instanceof ECNamedCurveParameterSpec) {
            ECNamedCurveParameterSpec nParams = (ECNamedCurveParameterSpec) params;
            ASN1ObjectIdentifier nameOid = getNamedCurveOid(nParams.getName());
            ECDomainParameters domainParameters = new ECNamedDomainParameters(nameOid, nParams.getCurve(), nParams.getG(), nParams.getN(), nParams.getH(), nParams.getSeed());
            return domainParameters;
        } else if (params == null) {
            ECParameterSpec iSpec = configuration.getEcImplicitlyCa();
            ECDomainParameters domainParameters2 = new ECDomainParameters(iSpec.getCurve(), iSpec.getG(), iSpec.getN(), iSpec.getH(), iSpec.getSeed());
            return domainParameters2;
        } else {
            ECDomainParameters domainParameters3 = new ECDomainParameters(params.getCurve(), params.getG(), params.getN(), params.getH(), params.getSeed());
            return domainParameters3;
        }
    }

    public static ECDomainParameters getDomainParameters(ProviderConfiguration configuration, X962Parameters params) {
        if (params.isNamedCurve()) {
            ASN1ObjectIdentifier oid = ASN1ObjectIdentifier.getInstance(params.getParameters());
            X9ECParameters ecP = getNamedCurveByOid(oid);
            if (ecP == null) {
                Map extraCurves = configuration.getAdditionalECParameters();
                ecP = (X9ECParameters) extraCurves.get(oid);
            }
            ECDomainParameters domainParameters = new ECNamedDomainParameters(oid, ecP);
            return domainParameters;
        } else if (params.isImplicitlyCA()) {
            ECParameterSpec iSpec = configuration.getEcImplicitlyCa();
            ECDomainParameters domainParameters2 = new ECDomainParameters(iSpec.getCurve(), iSpec.getG(), iSpec.getN(), iSpec.getH(), iSpec.getSeed());
            return domainParameters2;
        } else {
            X9ECParameters ecP2 = X9ECParameters.getInstance(params.getParameters());
            ECDomainParameters domainParameters3 = new ECDomainParameters(ecP2.getCurve(), ecP2.getG(), ecP2.getN(), ecP2.getH(), ecP2.getSeed());
            return domainParameters3;
        }
    }

    public static AsymmetricKeyParameter generatePublicKeyParameter(PublicKey key) throws InvalidKeyException {
        if (key instanceof ECPublicKey) {
            ECPublicKey k = (ECPublicKey) key;
            ECParameterSpec s = k.getParameters();
            return new ECPublicKeyParameters(k.getQ(), new ECDomainParameters(s.getCurve(), s.getG(), s.getN(), s.getH(), s.getSeed()));
        } else if (key instanceof java.security.interfaces.ECPublicKey) {
            java.security.interfaces.ECPublicKey pubKey = (java.security.interfaces.ECPublicKey) key;
            ECParameterSpec s2 = EC5Util.convertSpec(pubKey.getParams());
            return new ECPublicKeyParameters(EC5Util.convertPoint(pubKey.getParams(), pubKey.getW()), new ECDomainParameters(s2.getCurve(), s2.getG(), s2.getN(), s2.getH(), s2.getSeed()));
        } else {
            try {
                byte[] bytes = key.getEncoded();
                if (bytes == null) {
                    throw new InvalidKeyException("no encoding for EC public key");
                }
                PublicKey publicKey = BouncyCastleProvider.getPublicKey(SubjectPublicKeyInfo.getInstance(bytes));
                if (publicKey instanceof java.security.interfaces.ECPublicKey) {
                    return generatePublicKeyParameter(publicKey);
                }
                throw new InvalidKeyException("cannot identify EC public key.");
            } catch (Exception e) {
                throw new InvalidKeyException("cannot identify EC public key: " + e.toString());
            }
        }
    }

    public static AsymmetricKeyParameter generatePrivateKeyParameter(PrivateKey key) throws InvalidKeyException {
        if (key instanceof ECPrivateKey) {
            ECPrivateKey k = (ECPrivateKey) key;
            ECParameterSpec s = k.getParameters();
            if (s == null) {
                s = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
            }
            if (k.getParameters() instanceof ECNamedCurveParameterSpec) {
                String name = ((ECNamedCurveParameterSpec) k.getParameters()).getName();
                return new ECPrivateKeyParameters(k.getD(), new ECNamedDomainParameters(ECNamedCurveTable.getOID(name), s.getCurve(), s.getG(), s.getN(), s.getH(), s.getSeed()));
            }
            return new ECPrivateKeyParameters(k.getD(), new ECDomainParameters(s.getCurve(), s.getG(), s.getN(), s.getH(), s.getSeed()));
        } else if (key instanceof java.security.interfaces.ECPrivateKey) {
            java.security.interfaces.ECPrivateKey privKey = (java.security.interfaces.ECPrivateKey) key;
            ECParameterSpec s2 = EC5Util.convertSpec(privKey.getParams());
            return new ECPrivateKeyParameters(privKey.getS(), new ECDomainParameters(s2.getCurve(), s2.getG(), s2.getN(), s2.getH(), s2.getSeed()));
        } else {
            try {
                byte[] bytes = key.getEncoded();
                if (bytes == null) {
                    throw new InvalidKeyException("no encoding for EC private key");
                }
                PrivateKey privateKey = BouncyCastleProvider.getPrivateKey(PrivateKeyInfo.getInstance(bytes));
                if (privateKey instanceof java.security.interfaces.ECPrivateKey) {
                    return generatePrivateKeyParameter(privateKey);
                }
                throw new InvalidKeyException("can't identify EC private key.");
            } catch (Exception e) {
                throw new InvalidKeyException("cannot identify EC private key: " + e.toString());
            }
        }
    }

    public static int getOrderBitLength(ProviderConfiguration configuration, BigInteger order, BigInteger privateValue) {
        if (order == null) {
            ECParameterSpec implicitCA = configuration.getEcImplicitlyCa();
            if (implicitCA == null) {
                return privateValue.bitLength();
            }
            return implicitCA.getN().bitLength();
        }
        return order.bitLength();
    }

    public static ASN1ObjectIdentifier getNamedCurveOid(String curveName) {
        String name = curveName;
        int spacePos = name.indexOf(32);
        if (spacePos > 0) {
            name = name.substring(spacePos + 1);
        }
        try {
            if (name.charAt(0) >= '0' && name.charAt(0) <= '2') {
                return new ASN1ObjectIdentifier(name);
            }
        } catch (IllegalArgumentException e) {
        }
        return ECNamedCurveTable.getOID(name);
    }

    public static ASN1ObjectIdentifier getNamedCurveOid(ECParameterSpec ecParameterSpec) {
        Enumeration names = ECNamedCurveTable.getNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            X9ECParameters params = ECNamedCurveTable.getByName(name);
            if (params.getN().equals(ecParameterSpec.getN()) && params.getH().equals(ecParameterSpec.getH()) && params.getCurve().equals(ecParameterSpec.getCurve()) && params.getG().equals(ecParameterSpec.getG())) {
                return ECNamedCurveTable.getOID(name);
            }
        }
        return null;
    }

    public static X9ECParameters getNamedCurveByOid(ASN1ObjectIdentifier oid) {
        X9ECParameters params = CustomNamedCurves.getByOID(oid);
        if (params == null) {
            return ECNamedCurveTable.getByOID(oid);
        }
        return params;
    }

    public static X9ECParameters getNamedCurveByName(String curveName) {
        X9ECParameters params = CustomNamedCurves.getByName(curveName);
        if (params == null) {
            return ECNamedCurveTable.getByName(curveName);
        }
        return params;
    }

    public static String getCurveName(ASN1ObjectIdentifier oid) {
        return ECNamedCurveTable.getName(oid);
    }

    public static String privateKeyToString(String algorithm, BigInteger d, ECParameterSpec spec) {
        StringBuffer buf = new StringBuffer();
        String nl = Strings.lineSeparator();
        ECPoint q = new FixedPointCombMultiplier().multiply(spec.getG(), d).normalize();
        buf.append(algorithm);
        buf.append(" Private Key [").append(generateKeyFingerprint(q, spec)).append(NavigationBarInflaterView.SIZE_MOD_END).append(nl);
        buf.append("            X: ").append(q.getAffineXCoord().toBigInteger().toString(16)).append(nl);
        buf.append("            Y: ").append(q.getAffineYCoord().toBigInteger().toString(16)).append(nl);
        return buf.toString();
    }

    public static String publicKeyToString(String algorithm, ECPoint q, ECParameterSpec spec) {
        StringBuffer buf = new StringBuffer();
        String nl = Strings.lineSeparator();
        buf.append(algorithm);
        buf.append(" Public Key [").append(generateKeyFingerprint(q, spec)).append(NavigationBarInflaterView.SIZE_MOD_END).append(nl);
        buf.append("            X: ").append(q.getAffineXCoord().toBigInteger().toString(16)).append(nl);
        buf.append("            Y: ").append(q.getAffineYCoord().toBigInteger().toString(16)).append(nl);
        return buf.toString();
    }

    public static String generateKeyFingerprint(ECPoint publicPoint, ECParameterSpec spec) {
        ECCurve curve = spec.getCurve();
        ECPoint g = spec.getG();
        if (curve != null) {
            return new Fingerprint(Arrays.concatenate(publicPoint.getEncoded(false), curve.getA().getEncoded(), curve.getB().getEncoded(), g.getEncoded(false))).toString();
        }
        return new Fingerprint(publicPoint.getEncoded(false)).toString();
    }

    public static String getNameFrom(final AlgorithmParameterSpec paramSpec) {
        return (String) AccessController.doPrivileged(new PrivilegedAction() { // from class: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil.1
            @Override // java.security.PrivilegedAction
            public Object run() {
                try {
                    Method m = paramSpec.getClass().getMethod("getName", new Class[0]);
                    return m.invoke(paramSpec, new Object[0]);
                } catch (Exception e) {
                    return null;
                }
            }
        });
    }
}
