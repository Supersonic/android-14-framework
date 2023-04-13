package com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.x509;

import com.android.internal.org.bouncycastle.asn1.ASN1BitString;
import com.android.internal.org.bouncycastle.asn1.ASN1Encodable;
import com.android.internal.org.bouncycastle.asn1.ASN1Encoding;
import com.android.internal.org.bouncycastle.asn1.x509.CertificateList;
import com.android.internal.org.bouncycastle.asn1.x509.Extension;
import com.android.internal.org.bouncycastle.asn1.x509.IssuingDistributionPoint;
import com.android.internal.org.bouncycastle.jcajce.util.JcaJceHelper;
import java.security.cert.CRLException;
/* loaded from: classes4.dex */
class X509CRLObject extends X509CRLImpl {
    private final Object cacheLock;
    private volatile int hashValue;
    private volatile boolean hashValueSet;
    private X509CRLInternal internalCRLValue;

    /* JADX INFO: Access modifiers changed from: package-private */
    public X509CRLObject(JcaJceHelper bcHelper, CertificateList c) throws CRLException {
        super(bcHelper, c, createSigAlgName(c), createSigAlgParams(c), isIndirectCRL(c));
        this.cacheLock = new Object();
    }

    @Override // java.security.cert.X509CRL
    public boolean equals(Object other) {
        ASN1BitString signature;
        if (this == other) {
            return true;
        }
        if (other instanceof X509CRLObject) {
            X509CRLObject otherBC = (X509CRLObject) other;
            if (this.hashValueSet && otherBC.hashValueSet) {
                if (this.hashValue != otherBC.hashValue) {
                    return false;
                }
            } else if ((this.internalCRLValue == null || otherBC.internalCRLValue == null) && (signature = this.f809c.getSignature()) != null && !signature.equals(otherBC.f809c.getSignature())) {
                return false;
            }
        }
        return getInternalCRL().equals(other);
    }

    @Override // java.security.cert.X509CRL
    public int hashCode() {
        if (!this.hashValueSet) {
            this.hashValue = getInternalCRL().hashCode();
            this.hashValueSet = true;
        }
        return this.hashValue;
    }

    private X509CRLInternal getInternalCRL() {
        byte[] encoding;
        X509CRLInternal x509CRLInternal;
        synchronized (this.cacheLock) {
            X509CRLInternal x509CRLInternal2 = this.internalCRLValue;
            if (x509CRLInternal2 != null) {
                return x509CRLInternal2;
            }
            try {
                byte[] encoding2 = getEncoded();
                encoding = encoding2;
            } catch (CRLException e) {
                encoding = null;
            }
            X509CRLInternal temp = new X509CRLInternal(this.bcHelper, this.f809c, this.sigAlgName, this.sigAlgParams, this.isIndirect, encoding);
            synchronized (this.cacheLock) {
                if (this.internalCRLValue == null) {
                    this.internalCRLValue = temp;
                }
                x509CRLInternal = this.internalCRLValue;
            }
            return x509CRLInternal;
        }
    }

    private static String createSigAlgName(CertificateList c) throws CRLException {
        try {
            return X509SignatureUtil.getSignatureName(c.getSignatureAlgorithm());
        } catch (Exception e) {
            throw new CRLException("CRL contents invalid: " + e);
        }
    }

    private static byte[] createSigAlgParams(CertificateList c) throws CRLException {
        try {
            ASN1Encodable parameters = c.getSignatureAlgorithm().getParameters();
            if (parameters == null) {
                return null;
            }
            return parameters.toASN1Primitive().getEncoded(ASN1Encoding.DER);
        } catch (Exception e) {
            throw new CRLException("CRL contents invalid: " + e);
        }
    }

    private static boolean isIndirectCRL(CertificateList c) throws CRLException {
        try {
            byte[] extOctets = getExtensionOctets(c, Extension.issuingDistributionPoint.getId());
            if (extOctets == null) {
                return false;
            }
            return IssuingDistributionPoint.getInstance(extOctets).isIndirectCRL();
        } catch (Exception e) {
            throw new ExtCRLException("Exception reading IssuingDistributionPoint", e);
        }
    }
}
