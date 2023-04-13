package com.android.internal.org.bouncycastle.x509;

import com.android.internal.org.bouncycastle.asn1.ASN1Encodable;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
import com.android.internal.org.bouncycastle.asn1.x509.AttCertIssuer;
import com.android.internal.org.bouncycastle.asn1.x509.GeneralName;
import com.android.internal.org.bouncycastle.asn1.x509.GeneralNames;
import com.android.internal.org.bouncycastle.asn1.x509.V2Form;
import com.android.internal.org.bouncycastle.jce.X509Principal;
import com.android.internal.org.bouncycastle.util.Selector;
import java.io.IOException;
import java.security.Principal;
import java.security.cert.CertSelector;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import javax.security.auth.x500.X500Principal;
/* loaded from: classes4.dex */
public class AttributeCertificateIssuer implements CertSelector, Selector {
    final ASN1Encodable form;

    public AttributeCertificateIssuer(AttCertIssuer issuer) {
        this.form = issuer.getIssuer();
    }

    public AttributeCertificateIssuer(X500Principal principal) throws IOException {
        this(new X509Principal(principal.getEncoded()));
    }

    public AttributeCertificateIssuer(X509Principal principal) {
        this.form = new V2Form(GeneralNames.getInstance(new DERSequence(new GeneralName(principal))));
    }

    private Object[] getNames() {
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
                try {
                    l.add(new X500Principal(names[i].getName().toASN1Primitive().getEncoded()));
                } catch (IOException e) {
                    throw new RuntimeException("badly formed Name object");
                }
            }
        }
        int i2 = l.size();
        return l.toArray(new Object[i2]);
    }

    public Principal[] getPrincipals() {
        Object[] p = getNames();
        List l = new ArrayList();
        for (int i = 0; i != p.length; i++) {
            if (p[i] instanceof Principal) {
                l.add(p[i]);
            }
        }
        int i2 = l.size();
        return (Principal[]) l.toArray(new Principal[i2]);
    }

    private boolean matchesDN(X500Principal subject, GeneralNames targets) {
        GeneralName[] names = targets.getNames();
        for (int i = 0; i != names.length; i++) {
            GeneralName gn = names[i];
            if (gn.getTagNo() == 4) {
                try {
                    if (new X500Principal(gn.getName().toASN1Primitive().getEncoded()).equals(subject)) {
                        return true;
                    }
                } catch (IOException e) {
                }
            }
        }
        return false;
    }

    @Override // java.security.cert.CertSelector, com.android.internal.org.bouncycastle.util.Selector
    public Object clone() {
        return new AttributeCertificateIssuer(AttCertIssuer.getInstance(this.form));
    }

    @Override // java.security.cert.CertSelector
    public boolean match(Certificate cert) {
        if (cert instanceof X509Certificate) {
            X509Certificate x509Cert = (X509Certificate) cert;
            ASN1Encodable aSN1Encodable = this.form;
            if (aSN1Encodable instanceof V2Form) {
                V2Form issuer = (V2Form) aSN1Encodable;
                if (issuer.getBaseCertificateID() != null) {
                    return issuer.getBaseCertificateID().getSerial().hasValue(x509Cert.getSerialNumber()) && matchesDN(x509Cert.getIssuerX500Principal(), issuer.getBaseCertificateID().getIssuer());
                }
                GeneralNames name = issuer.getIssuerName();
                if (matchesDN(x509Cert.getSubjectX500Principal(), name)) {
                    return true;
                }
            } else {
                GeneralNames name2 = (GeneralNames) aSN1Encodable;
                if (matchesDN(x509Cert.getSubjectX500Principal(), name2)) {
                    return true;
                }
            }
            return false;
        }
        return false;
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
        if (!(obj instanceof X509Certificate)) {
            return false;
        }
        return match((Certificate) obj);
    }
}
