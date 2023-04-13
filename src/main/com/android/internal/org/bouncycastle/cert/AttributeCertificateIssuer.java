package com.android.internal.org.bouncycastle.cert;

import com.android.internal.org.bouncycastle.asn1.ASN1Encodable;
import com.android.internal.org.bouncycastle.asn1.x500.X500Name;
import com.android.internal.org.bouncycastle.asn1.x509.AttCertIssuer;
import com.android.internal.org.bouncycastle.asn1.x509.GeneralName;
import com.android.internal.org.bouncycastle.asn1.x509.GeneralNames;
import com.android.internal.org.bouncycastle.asn1.x509.V2Form;
import com.android.internal.org.bouncycastle.util.Selector;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes4.dex */
public class AttributeCertificateIssuer implements Selector {
    final ASN1Encodable form;

    public AttributeCertificateIssuer(AttCertIssuer issuer) {
        this.form = issuer.getIssuer();
    }

    public AttributeCertificateIssuer(X500Name principal) {
        this.form = new V2Form(new GeneralNames(new GeneralName(principal)));
    }

    public X500Name[] getNames() {
        GeneralNames name;
        ASN1Encodable aSN1Encodable = this.form;
        if (aSN1Encodable instanceof V2Form) {
            name = ((V2Form) aSN1Encodable).getIssuerName();
        } else {
            name = (GeneralNames) aSN1Encodable;
        }
        GeneralName[] names = name.getNames();
        List l = new ArrayList(names.length);
        for (int i = 0; i != names.length; i++) {
            if (names[i].getTagNo() == 4) {
                l.add(X500Name.getInstance(names[i].getName()));
            }
        }
        int i2 = l.size();
        return (X500Name[]) l.toArray(new X500Name[i2]);
    }

    private boolean matchesDN(X500Name subject, GeneralNames targets) {
        GeneralName[] names = targets.getNames();
        for (int i = 0; i != names.length; i++) {
            GeneralName gn = names[i];
            if (gn.getTagNo() == 4 && X500Name.getInstance(gn.getName()).equals(subject)) {
                return true;
            }
        }
        return false;
    }

    @Override // com.android.internal.org.bouncycastle.util.Selector
    public Object clone() {
        return new AttributeCertificateIssuer(AttCertIssuer.getInstance(this.form));
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AttributeCertificateIssuer)) {
            return false;
        }
        AttributeCertificateIssuer other = (AttributeCertificateIssuer) obj;
        return this.form.equals(other.form);
    }

    public int hashCode() {
        return this.form.hashCode();
    }

    @Override // com.android.internal.org.bouncycastle.util.Selector
    public boolean match(Object obj) {
        if (obj instanceof X509CertificateHolder) {
            X509CertificateHolder x509Cert = (X509CertificateHolder) obj;
            ASN1Encodable aSN1Encodable = this.form;
            if (aSN1Encodable instanceof V2Form) {
                V2Form issuer = (V2Form) aSN1Encodable;
                if (issuer.getBaseCertificateID() != null) {
                    return issuer.getBaseCertificateID().getSerial().hasValue(x509Cert.getSerialNumber()) && matchesDN(x509Cert.getIssuer(), issuer.getBaseCertificateID().getIssuer());
                }
                GeneralNames name = issuer.getIssuerName();
                if (matchesDN(x509Cert.getSubject(), name)) {
                    return true;
                }
            } else {
                GeneralNames name2 = (GeneralNames) aSN1Encodable;
                if (matchesDN(x509Cert.getSubject(), name2)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
}
