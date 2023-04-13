package com.android.internal.org.bouncycastle.jce.spec;

import com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import com.android.internal.org.bouncycastle.math.field.FiniteField;
import com.android.internal.org.bouncycastle.math.field.Polynomial;
import com.android.internal.org.bouncycastle.math.field.PolynomialExtensionField;
import com.android.internal.org.bouncycastle.math.p025ec.ECAlgorithms;
import com.android.internal.org.bouncycastle.math.p025ec.ECCurve;
import com.android.internal.org.bouncycastle.math.p025ec.ECPoint;
import com.android.internal.org.bouncycastle.util.Arrays;
import java.math.BigInteger;
import java.security.spec.ECField;
import java.security.spec.ECFieldF2m;
import java.security.spec.ECFieldFp;
import java.security.spec.EllipticCurve;
/* loaded from: classes4.dex */
public class ECNamedCurveSpec extends java.security.spec.ECParameterSpec {
    private String name;

    private static EllipticCurve convertCurve(ECCurve curve, byte[] seed) {
        ECField field = convertField(curve.getField());
        BigInteger a = curve.getA().toBigInteger();
        BigInteger b = curve.getB().toBigInteger();
        return new EllipticCurve(field, a, b, seed);
    }

    private static ECField convertField(FiniteField field) {
        if (ECAlgorithms.isFpField(field)) {
            return new ECFieldFp(field.getCharacteristic());
        }
        Polynomial poly = ((PolynomialExtensionField) field).getMinimalPolynomial();
        int[] exponents = poly.getExponentsPresent();
        int[] ks = Arrays.reverse(Arrays.copyOfRange(exponents, 1, exponents.length - 1));
        return new ECFieldF2m(poly.getDegree(), ks);
    }

    public ECNamedCurveSpec(String name, ECCurve curve, ECPoint g, BigInteger n) {
        super(convertCurve(curve, null), EC5Util.convertPoint(g), n, 1);
        this.name = name;
    }

    public ECNamedCurveSpec(String name, EllipticCurve curve, java.security.spec.ECPoint g, BigInteger n) {
        super(curve, g, n, 1);
        this.name = name;
    }

    public ECNamedCurveSpec(String name, ECCurve curve, ECPoint g, BigInteger n, BigInteger h) {
        super(convertCurve(curve, null), EC5Util.convertPoint(g), n, h.intValue());
        this.name = name;
    }

    public ECNamedCurveSpec(String name, EllipticCurve curve, java.security.spec.ECPoint g, BigInteger n, BigInteger h) {
        super(curve, g, n, h.intValue());
        this.name = name;
    }

    public ECNamedCurveSpec(String name, ECCurve curve, ECPoint g, BigInteger n, BigInteger h, byte[] seed) {
        super(convertCurve(curve, seed), EC5Util.convertPoint(g), n, h.intValue());
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
