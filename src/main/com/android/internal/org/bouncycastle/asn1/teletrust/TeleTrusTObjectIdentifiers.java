package com.android.internal.org.bouncycastle.asn1.teletrust;

import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
/* loaded from: classes4.dex */
public interface TeleTrusTObjectIdentifiers {
    public static final ASN1ObjectIdentifier brainpoolP160r1;
    public static final ASN1ObjectIdentifier brainpoolP160t1;
    public static final ASN1ObjectIdentifier brainpoolP192r1;
    public static final ASN1ObjectIdentifier brainpoolP192t1;
    public static final ASN1ObjectIdentifier brainpoolP224r1;
    public static final ASN1ObjectIdentifier brainpoolP224t1;
    public static final ASN1ObjectIdentifier brainpoolP256r1;
    public static final ASN1ObjectIdentifier brainpoolP256t1;
    public static final ASN1ObjectIdentifier brainpoolP320r1;
    public static final ASN1ObjectIdentifier brainpoolP320t1;
    public static final ASN1ObjectIdentifier brainpoolP384r1;
    public static final ASN1ObjectIdentifier brainpoolP384t1;
    public static final ASN1ObjectIdentifier brainpoolP512r1;
    public static final ASN1ObjectIdentifier brainpoolP512t1;
    public static final ASN1ObjectIdentifier ecSign;
    public static final ASN1ObjectIdentifier ecSignWithRipemd160;
    public static final ASN1ObjectIdentifier ecSignWithSha1;
    public static final ASN1ObjectIdentifier ecc_brainpool;
    public static final ASN1ObjectIdentifier ellipticCurve;
    public static final ASN1ObjectIdentifier ripemd128;
    public static final ASN1ObjectIdentifier ripemd160;
    public static final ASN1ObjectIdentifier ripemd256;
    public static final ASN1ObjectIdentifier rsaSignatureWithripemd128;
    public static final ASN1ObjectIdentifier rsaSignatureWithripemd160;
    public static final ASN1ObjectIdentifier rsaSignatureWithripemd256;
    public static final ASN1ObjectIdentifier teleTrusTAlgorithm;
    public static final ASN1ObjectIdentifier teleTrusTRSAsignatureAlgorithm;
    public static final ASN1ObjectIdentifier versionOne;

    static {
        ASN1ObjectIdentifier aSN1ObjectIdentifier = new ASN1ObjectIdentifier("1.3.36.3");
        teleTrusTAlgorithm = aSN1ObjectIdentifier;
        ripemd160 = aSN1ObjectIdentifier.branch("2.1");
        ripemd128 = aSN1ObjectIdentifier.branch("2.2");
        ripemd256 = aSN1ObjectIdentifier.branch("2.3");
        ASN1ObjectIdentifier branch = aSN1ObjectIdentifier.branch("3.1");
        teleTrusTRSAsignatureAlgorithm = branch;
        rsaSignatureWithripemd160 = branch.branch("2");
        rsaSignatureWithripemd128 = branch.branch("3");
        rsaSignatureWithripemd256 = branch.branch("4");
        ASN1ObjectIdentifier branch2 = aSN1ObjectIdentifier.branch("3.2");
        ecSign = branch2;
        ecSignWithSha1 = branch2.branch("1");
        ecSignWithRipemd160 = branch2.branch("2");
        ASN1ObjectIdentifier branch3 = aSN1ObjectIdentifier.branch("3.2.8");
        ecc_brainpool = branch3;
        ASN1ObjectIdentifier branch4 = branch3.branch("1");
        ellipticCurve = branch4;
        ASN1ObjectIdentifier branch5 = branch4.branch("1");
        versionOne = branch5;
        brainpoolP160r1 = branch5.branch("1");
        brainpoolP160t1 = branch5.branch("2");
        brainpoolP192r1 = branch5.branch("3");
        brainpoolP192t1 = branch5.branch("4");
        brainpoolP224r1 = branch5.branch("5");
        brainpoolP224t1 = branch5.branch("6");
        brainpoolP256r1 = branch5.branch("7");
        brainpoolP256t1 = branch5.branch("8");
        brainpoolP320r1 = branch5.branch("9");
        brainpoolP320t1 = branch5.branch("10");
        brainpoolP384r1 = branch5.branch("11");
        brainpoolP384t1 = branch5.branch("12");
        brainpoolP512r1 = branch5.branch("13");
        brainpoolP512t1 = branch5.branch("14");
    }
}
