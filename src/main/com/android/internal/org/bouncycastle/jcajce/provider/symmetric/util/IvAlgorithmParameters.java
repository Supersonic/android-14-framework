package com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util;

import com.android.internal.org.bouncycastle.asn1.ASN1OctetString;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.DEROctetString;
import com.android.internal.org.bouncycastle.util.Arrays;
import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.IvParameterSpec;
/* loaded from: classes4.dex */
public class IvAlgorithmParameters extends BaseAlgorithmParameters {

    /* renamed from: iv */
    private byte[] f814iv;

    @Override // java.security.AlgorithmParametersSpi
    protected byte[] engineGetEncoded() throws IOException {
        return engineGetEncoded("ASN.1");
    }

    @Override // java.security.AlgorithmParametersSpi
    protected byte[] engineGetEncoded(String format) throws IOException {
        if (isASN1FormatString(format)) {
            return new DEROctetString(engineGetEncoded("RAW")).getEncoded();
        }
        if (format.equals("RAW")) {
            return Arrays.clone(this.f814iv);
        }
        return null;
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters
    protected AlgorithmParameterSpec localEngineGetParameterSpec(Class paramSpec) throws InvalidParameterSpecException {
        if (paramSpec == IvParameterSpec.class || paramSpec == AlgorithmParameterSpec.class) {
            return new IvParameterSpec(this.f814iv);
        }
        throw new InvalidParameterSpecException("unknown parameter spec passed to IV parameters object.");
    }

    @Override // java.security.AlgorithmParametersSpi
    protected void engineInit(AlgorithmParameterSpec paramSpec) throws InvalidParameterSpecException {
        if (!(paramSpec instanceof IvParameterSpec)) {
            throw new InvalidParameterSpecException("IvParameterSpec required to initialise a IV parameters algorithm parameters object");
        }
        this.f814iv = ((IvParameterSpec) paramSpec).getIV();
    }

    @Override // java.security.AlgorithmParametersSpi
    protected void engineInit(byte[] params) throws IOException {
        if (params.length % 8 != 0 && params[0] == 4 && params[1] == params.length - 2) {
            ASN1OctetString oct = (ASN1OctetString) ASN1Primitive.fromByteArray(params);
            params = oct.getOctets();
        }
        this.f814iv = Arrays.clone(params);
    }

    @Override // java.security.AlgorithmParametersSpi
    protected void engineInit(byte[] params, String format) throws IOException {
        if (isASN1FormatString(format)) {
            try {
                ASN1OctetString oct = (ASN1OctetString) ASN1Primitive.fromByteArray(params);
                engineInit(oct.getOctets());
            } catch (Exception e) {
                throw new IOException("Exception decoding: " + e);
            }
        } else if (format.equals("RAW")) {
            engineInit(params);
        } else {
            throw new IOException("Unknown parameters format in IV parameters object");
        }
    }

    @Override // java.security.AlgorithmParametersSpi
    protected String engineToString() {
        return "IV Parameters";
    }
}
