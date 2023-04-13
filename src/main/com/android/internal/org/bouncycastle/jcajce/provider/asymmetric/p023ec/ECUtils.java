package com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.p023ec;

import com.android.internal.org.bouncycastle.asn1.ASN1Null;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.DERNull;
import com.android.internal.org.bouncycastle.asn1.p018x9.X962Parameters;
import com.android.internal.org.bouncycastle.asn1.p018x9.X9ECParameters;
import com.android.internal.org.bouncycastle.asn1.p018x9.X9ECPoint;
import com.android.internal.org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import com.android.internal.org.bouncycastle.jce.spec.ECNamedCurveSpec;
import com.android.internal.org.bouncycastle.math.p025ec.ECCurve;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.ec.ECUtils */
/* loaded from: classes4.dex */
public class ECUtils {
    ECUtils() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static AsymmetricKeyParameter generatePublicKeyParameter(PublicKey key) throws InvalidKeyException {
        return key instanceof BCECPublicKey ? ((BCECPublicKey) key).engineGetKeyParameters() : ECUtil.generatePublicKeyParameter(key);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static X9ECParameters getDomainParametersFromGenSpec(ECGenParameterSpec genSpec) {
        return getDomainParametersFromName(genSpec.getName());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static X9ECParameters getDomainParametersFromName(String curveName) {
        X9ECParameters domainParameters;
        try {
            if (curveName.charAt(0) >= '0' && curveName.charAt(0) <= '2') {
                ASN1ObjectIdentifier oidID = new ASN1ObjectIdentifier(curveName);
                domainParameters = ECUtil.getNamedCurveByOid(oidID);
            } else if (curveName.indexOf(32) > 0) {
                curveName = curveName.substring(curveName.indexOf(32) + 1);
                domainParameters = ECUtil.getNamedCurveByName(curveName);
            } else {
                domainParameters = ECUtil.getNamedCurveByName(curveName);
            }
            return domainParameters;
        } catch (IllegalArgumentException e) {
            X9ECParameters domainParameters2 = ECUtil.getNamedCurveByName(curveName);
            return domainParameters2;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static X962Parameters getDomainParametersFromName(ECParameterSpec ecSpec, boolean withCompression) {
        if (ecSpec instanceof ECNamedCurveSpec) {
            ASN1ObjectIdentifier curveOid = ECUtil.getNamedCurveOid(((ECNamedCurveSpec) ecSpec).getName());
            if (curveOid == null) {
                curveOid = new ASN1ObjectIdentifier(((ECNamedCurveSpec) ecSpec).getName());
            }
            X962Parameters params = new X962Parameters(curveOid);
            return params;
        } else if (ecSpec == null) {
            X962Parameters params2 = new X962Parameters((ASN1Null) DERNull.INSTANCE);
            return params2;
        } else {
            ECCurve curve = EC5Util.convertCurve(ecSpec.getCurve());
            X9ECParameters ecP = new X9ECParameters(curve, new X9ECPoint(EC5Util.convertPoint(curve, ecSpec.getGenerator()), withCompression), ecSpec.getOrder(), BigInteger.valueOf(ecSpec.getCofactor()), ecSpec.getCurve().getSeed());
            X962Parameters params3 = new X962Parameters(ecP);
            return params3;
        }
    }
}
