package com.android.internal.org.bouncycastle.cert.selector;

import com.android.internal.org.bouncycastle.asn1.ASN1OctetString;
import com.android.internal.org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import com.android.internal.org.bouncycastle.asn1.x500.X500Name;
import com.android.internal.org.bouncycastle.asn1.x509.Extension;
import com.android.internal.org.bouncycastle.cert.X509CertificateHolder;
import com.android.internal.org.bouncycastle.util.Arrays;
import com.android.internal.org.bouncycastle.util.Selector;
import java.math.BigInteger;
/* loaded from: classes4.dex */
public class X509CertificateHolderSelector implements Selector {
    private X500Name issuer;
    private BigInteger serialNumber;
    private byte[] subjectKeyId;

    public X509CertificateHolderSelector(byte[] subjectKeyId) {
        this(null, null, subjectKeyId);
    }

    public X509CertificateHolderSelector(X500Name issuer, BigInteger serialNumber) {
        this(issuer, serialNumber, null);
    }

    public X509CertificateHolderSelector(X500Name issuer, BigInteger serialNumber, byte[] subjectKeyId) {
        this.issuer = issuer;
        this.serialNumber = serialNumber;
        this.subjectKeyId = subjectKeyId;
    }

    public X500Name getIssuer() {
        return this.issuer;
    }

    public BigInteger getSerialNumber() {
        return this.serialNumber;
    }

    public byte[] getSubjectKeyIdentifier() {
        return Arrays.clone(this.subjectKeyId);
    }

    public int hashCode() {
        int code = Arrays.hashCode(this.subjectKeyId);
        BigInteger bigInteger = this.serialNumber;
        if (bigInteger != null) {
            code ^= bigInteger.hashCode();
        }
        X500Name x500Name = this.issuer;
        if (x500Name != null) {
            return code ^ x500Name.hashCode();
        }
        return code;
    }

    public boolean equals(Object o) {
        if (o instanceof X509CertificateHolderSelector) {
            X509CertificateHolderSelector id = (X509CertificateHolderSelector) o;
            return Arrays.areEqual(this.subjectKeyId, id.subjectKeyId) && equalsObj(this.serialNumber, id.serialNumber) && equalsObj(this.issuer, id.issuer);
        }
        return false;
    }

    private boolean equalsObj(Object a, Object b) {
        return a != null ? a.equals(b) : b == null;
    }

    @Override // com.android.internal.org.bouncycastle.util.Selector
    public boolean match(Object obj) {
        if (obj instanceof X509CertificateHolder) {
            X509CertificateHolder certHldr = (X509CertificateHolder) obj;
            if (getSerialNumber() != null) {
                IssuerAndSerialNumber iAndS = new IssuerAndSerialNumber(certHldr.toASN1Structure());
                return iAndS.getName().equals(this.issuer) && iAndS.getSerialNumber().hasValue(this.serialNumber);
            } else if (this.subjectKeyId != null) {
                Extension ext = certHldr.getExtension(Extension.subjectKeyIdentifier);
                if (ext == null) {
                    return Arrays.areEqual(this.subjectKeyId, MSOutlookKeyIdCalculator.calculateKeyId(certHldr.getSubjectPublicKeyInfo()));
                }
                byte[] subKeyID = ASN1OctetString.getInstance(ext.getParsedValue()).getOctets();
                return Arrays.areEqual(this.subjectKeyId, subKeyID);
            }
        } else if (obj instanceof byte[]) {
            return Arrays.areEqual(this.subjectKeyId, (byte[]) obj);
        }
        return false;
    }

    @Override // com.android.internal.org.bouncycastle.util.Selector
    public Object clone() {
        return new X509CertificateHolderSelector(this.issuer, this.serialNumber, this.subjectKeyId);
    }
}
