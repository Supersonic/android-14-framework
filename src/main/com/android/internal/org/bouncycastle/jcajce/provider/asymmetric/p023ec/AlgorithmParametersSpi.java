package com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.p023ec;

import com.android.internal.org.bouncycastle.asn1.ASN1Null;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.DERNull;
import com.android.internal.org.bouncycastle.asn1.p018x9.ECNamedCurveTable;
import com.android.internal.org.bouncycastle.asn1.p018x9.X962Parameters;
import com.android.internal.org.bouncycastle.asn1.p018x9.X9ECParameters;
import com.android.internal.org.bouncycastle.asn1.p018x9.X9ECPoint;
import com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import com.android.internal.org.bouncycastle.jce.provider.BouncyCastleProvider;
import com.android.internal.org.bouncycastle.jce.spec.ECNamedCurveSpec;
import com.android.internal.org.bouncycastle.math.p025ec.ECCurve;
import java.io.IOException;
import java.math.BigInteger;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.InvalidParameterSpecException;
/* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.ec.AlgorithmParametersSpi */
/* loaded from: classes4.dex */
public class AlgorithmParametersSpi extends java.security.AlgorithmParametersSpi {
    private String curveName;
    private ECParameterSpec ecParameterSpec;

    protected boolean isASN1FormatString(String format) {
        return format == null || format.equals("ASN.1");
    }

    @Override // java.security.AlgorithmParametersSpi
    protected void engineInit(AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException {
        if (algorithmParameterSpec instanceof ECGenParameterSpec) {
            ECGenParameterSpec ecGenParameterSpec = (ECGenParameterSpec) algorithmParameterSpec;
            X9ECParameters params = ECUtils.getDomainParametersFromGenSpec(ecGenParameterSpec);
            if (params == null) {
                throw new InvalidParameterSpecException("EC curve name not recognized: " + ecGenParameterSpec.getName());
            }
            this.curveName = ecGenParameterSpec.getName();
            ECParameterSpec baseSpec = EC5Util.convertToSpec(params);
            this.ecParameterSpec = new ECNamedCurveSpec(this.curveName, baseSpec.getCurve(), baseSpec.getGenerator(), baseSpec.getOrder(), BigInteger.valueOf(baseSpec.getCofactor()));
        } else if (algorithmParameterSpec instanceof ECParameterSpec) {
            if (algorithmParameterSpec instanceof ECNamedCurveSpec) {
                this.curveName = ((ECNamedCurveSpec) algorithmParameterSpec).getName();
            } else {
                this.curveName = null;
            }
            this.ecParameterSpec = (ECParameterSpec) algorithmParameterSpec;
        } else {
            throw new InvalidParameterSpecException("AlgorithmParameterSpec class not recognized: " + algorithmParameterSpec.getClass().getName());
        }
    }

    @Override // java.security.AlgorithmParametersSpi
    protected void engineInit(byte[] bytes) throws IOException {
        engineInit(bytes, "ASN.1");
    }

    @Override // java.security.AlgorithmParametersSpi
    protected void engineInit(byte[] bytes, String format) throws IOException {
        if (isASN1FormatString(format)) {
            X962Parameters params = X962Parameters.getInstance(bytes);
            ECCurve curve = EC5Util.getCurve(BouncyCastleProvider.CONFIGURATION, params);
            if (params.isNamedCurve()) {
                ASN1ObjectIdentifier curveId = ASN1ObjectIdentifier.getInstance(params.getParameters());
                String name = ECNamedCurveTable.getName(curveId);
                this.curveName = name;
                if (name == null) {
                    this.curveName = curveId.getId();
                }
            }
            this.ecParameterSpec = EC5Util.convertToSpec(params, curve);
            return;
        }
        throw new IOException("Unknown encoded parameters format in AlgorithmParameters object: " + format);
    }

    @Override // java.security.AlgorithmParametersSpi
    protected <T extends AlgorithmParameterSpec> T engineGetParameterSpec(Class<T> paramSpec) throws InvalidParameterSpecException {
        if (ECParameterSpec.class.isAssignableFrom(paramSpec) || paramSpec == AlgorithmParameterSpec.class) {
            return this.ecParameterSpec;
        }
        if (ECGenParameterSpec.class.isAssignableFrom(paramSpec)) {
            String str = this.curveName;
            if (str != null) {
                ASN1ObjectIdentifier namedCurveOid = ECUtil.getNamedCurveOid(str);
                if (namedCurveOid != null) {
                    return new ECGenParameterSpec(namedCurveOid.getId());
                }
                return new ECGenParameterSpec(this.curveName);
            }
            ASN1ObjectIdentifier namedCurveOid2 = ECUtil.getNamedCurveOid(EC5Util.convertSpec(this.ecParameterSpec));
            if (namedCurveOid2 != null) {
                return new ECGenParameterSpec(namedCurveOid2.getId());
            }
        }
        throw new InvalidParameterSpecException("EC AlgorithmParameters cannot convert to " + paramSpec.getName());
    }

    @Override // java.security.AlgorithmParametersSpi
    protected byte[] engineGetEncoded() throws IOException {
        return engineGetEncoded("ASN.1");
    }

    @Override // java.security.AlgorithmParametersSpi
    protected byte[] engineGetEncoded(String format) throws IOException {
        X962Parameters params;
        if (isASN1FormatString(format)) {
            ECParameterSpec eCParameterSpec = this.ecParameterSpec;
            if (eCParameterSpec == null) {
                params = new X962Parameters((ASN1Null) DERNull.INSTANCE);
            } else {
                String str = this.curveName;
                if (str != null) {
                    params = new X962Parameters(ECUtil.getNamedCurveOid(str));
                } else {
                    com.android.internal.org.bouncycastle.jce.spec.ECParameterSpec ecSpec = EC5Util.convertSpec(eCParameterSpec);
                    X9ECParameters ecP = new X9ECParameters(ecSpec.getCurve(), new X9ECPoint(ecSpec.getG(), false), ecSpec.getN(), ecSpec.getH(), ecSpec.getSeed());
                    params = new X962Parameters(ecP);
                }
            }
            return params.getEncoded();
        }
        throw new IOException("Unknown parameters format in AlgorithmParameters object: " + format);
    }

    @Override // java.security.AlgorithmParametersSpi
    protected String engineToString() {
        return "EC Parameters";
    }
}
