package com.android.internal.org.bouncycastle.cert;

import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.x509.Extension;
import com.android.internal.org.bouncycastle.asn1.x509.Extensions;
import com.android.internal.org.bouncycastle.asn1.x509.GeneralNames;
import com.android.internal.org.bouncycastle.asn1.x509.TBSCertList;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Set;
/* loaded from: classes4.dex */
public class X509CRLEntryHolder {

    /* renamed from: ca */
    private GeneralNames f651ca;
    private TBSCertList.CRLEntry entry;

    /* JADX INFO: Access modifiers changed from: package-private */
    public X509CRLEntryHolder(TBSCertList.CRLEntry entry, boolean isIndirect, GeneralNames previousCA) {
        Extension currentCaName;
        this.entry = entry;
        this.f651ca = previousCA;
        if (isIndirect && entry.hasExtensions() && (currentCaName = entry.getExtensions().getExtension(Extension.certificateIssuer)) != null) {
            this.f651ca = GeneralNames.getInstance(currentCaName.getParsedValue());
        }
    }

    public BigInteger getSerialNumber() {
        return this.entry.getUserCertificate().getValue();
    }

    public Date getRevocationDate() {
        return this.entry.getRevocationDate().getDate();
    }

    public boolean hasExtensions() {
        return this.entry.hasExtensions();
    }

    public GeneralNames getCertificateIssuer() {
        return this.f651ca;
    }

    public Extension getExtension(ASN1ObjectIdentifier oid) {
        Extensions extensions = this.entry.getExtensions();
        if (extensions != null) {
            return extensions.getExtension(oid);
        }
        return null;
    }

    public Extensions getExtensions() {
        return this.entry.getExtensions();
    }

    public List getExtensionOIDs() {
        return CertUtils.getExtensionOIDs(this.entry.getExtensions());
    }

    public Set getCriticalExtensionOIDs() {
        return CertUtils.getCriticalExtensionOIDs(this.entry.getExtensions());
    }

    public Set getNonCriticalExtensionOIDs() {
        return CertUtils.getNonCriticalExtensionOIDs(this.entry.getExtensions());
    }
}
