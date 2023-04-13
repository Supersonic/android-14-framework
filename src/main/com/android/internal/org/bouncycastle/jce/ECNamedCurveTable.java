package com.android.internal.org.bouncycastle.jce;

import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.p018x9.X9ECParameters;
import com.android.internal.org.bouncycastle.crypto.p019ec.CustomNamedCurves;
import com.android.internal.org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import java.util.Enumeration;
/* loaded from: classes4.dex */
public class ECNamedCurveTable {
    public static ECNamedCurveParameterSpec getParameterSpec(String name) {
        X9ECParameters ecP = CustomNamedCurves.getByName(name);
        if (ecP == null) {
            try {
                ecP = CustomNamedCurves.getByOID(new ASN1ObjectIdentifier(name));
            } catch (IllegalArgumentException e) {
            }
            if (ecP == null && (ecP = com.android.internal.org.bouncycastle.asn1.p018x9.ECNamedCurveTable.getByName(name)) == null) {
                try {
                    ecP = com.android.internal.org.bouncycastle.asn1.p018x9.ECNamedCurveTable.getByOID(new ASN1ObjectIdentifier(name));
                } catch (IllegalArgumentException e2) {
                }
            }
        }
        if (ecP == null) {
            return null;
        }
        return new ECNamedCurveParameterSpec(name, ecP.getCurve(), ecP.getG(), ecP.getN(), ecP.getH(), ecP.getSeed());
    }

    public static Enumeration getNames() {
        return com.android.internal.org.bouncycastle.asn1.p018x9.ECNamedCurveTable.getNames();
    }
}
