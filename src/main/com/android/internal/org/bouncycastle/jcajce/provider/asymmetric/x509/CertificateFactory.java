package com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.x509;

import com.android.internal.org.bouncycastle.asn1.ASN1InputStream;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.ASN1Set;
import com.android.internal.org.bouncycastle.asn1.ASN1TaggedObject;
import com.android.internal.org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.pkcs.SignedData;
import com.android.internal.org.bouncycastle.asn1.x509.CertificateList;
import com.android.internal.org.bouncycastle.jcajce.util.BCJcaJceHelper;
import com.android.internal.org.bouncycastle.jcajce.util.JcaJceHelper;
import com.android.internal.org.bouncycastle.util.p027io.Streams;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactorySpi;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes4.dex */
public class CertificateFactory extends CertificateFactorySpi {
    private static final PEMUtil PEM_CERT_PARSER = new PEMUtil("CERTIFICATE");
    private static final PEMUtil PEM_CRL_PARSER = new PEMUtil("CRL");
    private static final PEMUtil PEM_PKCS7_PARSER = new PEMUtil("PKCS7");
    private final JcaJceHelper bcHelper = new BCJcaJceHelper();
    private ASN1Set sData = null;
    private int sDataObjectCount = 0;
    private InputStream currentStream = null;
    private ASN1Set sCrlData = null;
    private int sCrlDataObjectCount = 0;
    private InputStream currentCrlStream = null;

    private Certificate readDERCertificate(ASN1InputStream dIn) throws IOException, CertificateParsingException {
        return getCertificate(ASN1Sequence.getInstance(dIn.readObject()));
    }

    private Certificate readPEMCertificate(InputStream in) throws IOException, CertificateParsingException {
        return getCertificate(PEM_CERT_PARSER.readPEMObject(in));
    }

    private Certificate getCertificate(ASN1Sequence seq) throws CertificateParsingException {
        if (seq == null) {
            return null;
        }
        if (seq.size() > 1 && (seq.getObjectAt(0) instanceof ASN1ObjectIdentifier) && seq.getObjectAt(0).equals(PKCSObjectIdentifiers.signedData)) {
            this.sData = SignedData.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject) seq.getObjectAt(1), true)).getCertificates();
            return getCertificate();
        }
        return new X509CertificateObject(this.bcHelper, com.android.internal.org.bouncycastle.asn1.x509.Certificate.getInstance(seq));
    }

    private Certificate getCertificate() throws CertificateParsingException {
        if (this.sData != null) {
            while (this.sDataObjectCount < this.sData.size()) {
                ASN1Set aSN1Set = this.sData;
                int i = this.sDataObjectCount;
                this.sDataObjectCount = i + 1;
                Object obj = aSN1Set.getObjectAt(i);
                if (obj instanceof ASN1Sequence) {
                    return new X509CertificateObject(this.bcHelper, com.android.internal.org.bouncycastle.asn1.x509.Certificate.getInstance(obj));
                }
            }
            return null;
        }
        return null;
    }

    protected CRL createCRL(CertificateList c) throws CRLException {
        return new X509CRLObject(this.bcHelper, c);
    }

    private CRL readPEMCRL(InputStream in) throws IOException, CRLException {
        return getCRL(PEM_CRL_PARSER.readPEMObject(in));
    }

    private CRL readDERCRL(ASN1InputStream aIn) throws IOException, CRLException {
        return getCRL(ASN1Sequence.getInstance(aIn.readObject()));
    }

    private CRL getCRL(ASN1Sequence seq) throws CRLException {
        if (seq == null) {
            return null;
        }
        if (seq.size() > 1 && (seq.getObjectAt(0) instanceof ASN1ObjectIdentifier) && seq.getObjectAt(0).equals(PKCSObjectIdentifiers.signedData)) {
            this.sCrlData = SignedData.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject) seq.getObjectAt(1), true)).getCRLs();
            return getCRL();
        }
        return createCRL(CertificateList.getInstance(seq));
    }

    private CRL getCRL() throws CRLException {
        ASN1Set aSN1Set = this.sCrlData;
        if (aSN1Set == null || this.sCrlDataObjectCount >= aSN1Set.size()) {
            return null;
        }
        ASN1Set aSN1Set2 = this.sCrlData;
        int i = this.sCrlDataObjectCount;
        this.sCrlDataObjectCount = i + 1;
        return createCRL(CertificateList.getInstance(aSN1Set2.getObjectAt(i)));
    }

    @Override // java.security.cert.CertificateFactorySpi
    public Certificate engineGenerateCertificate(InputStream in) throws CertificateException {
        PushbackInputStream pis;
        InputStream inputStream = this.currentStream;
        if (inputStream == null) {
            this.currentStream = in;
            this.sData = null;
            this.sDataObjectCount = 0;
        } else if (inputStream != in) {
            this.currentStream = in;
            this.sData = null;
            this.sDataObjectCount = 0;
        }
        try {
            ASN1Set aSN1Set = this.sData;
            if (aSN1Set != null) {
                if (this.sDataObjectCount != aSN1Set.size()) {
                    return getCertificate();
                }
                this.sData = null;
                this.sDataObjectCount = 0;
                return null;
            }
            if (in.markSupported()) {
                pis = in;
            } else {
                pis = new PushbackInputStream(in);
            }
            if (in.markSupported()) {
                pis.mark(1);
            }
            int tag = pis.read();
            if (tag == -1) {
                return null;
            }
            if (in.markSupported()) {
                pis.reset();
            } else {
                pis.unread(tag);
            }
            if (tag != 48) {
                return readPEMCertificate(pis);
            }
            return readDERCertificate(new ASN1InputStream(pis));
        } catch (Exception e) {
            throw new ExCertificateException("parsing issue: " + e.getMessage(), e);
        }
    }

    @Override // java.security.cert.CertificateFactorySpi
    public Collection engineGenerateCertificates(InputStream inStream) throws CertificateException {
        List certs = new ArrayList();
        while (true) {
            Certificate cert = engineGenerateCertificate(inStream);
            if (cert != null) {
                certs.add(cert);
            } else {
                return certs;
            }
        }
    }

    @Override // java.security.cert.CertificateFactorySpi
    public CRL engineGenerateCRL(InputStream in) throws CRLException {
        InputStream pis;
        InputStream inputStream = this.currentCrlStream;
        if (inputStream == null) {
            this.currentCrlStream = in;
            this.sCrlData = null;
            this.sCrlDataObjectCount = 0;
        } else if (inputStream != in) {
            this.currentCrlStream = in;
            this.sCrlData = null;
            this.sCrlDataObjectCount = 0;
        }
        try {
            ASN1Set aSN1Set = this.sCrlData;
            if (aSN1Set != null) {
                if (this.sCrlDataObjectCount != aSN1Set.size()) {
                    return getCRL();
                }
                this.sCrlData = null;
                this.sCrlDataObjectCount = 0;
                return null;
            }
            if (in.markSupported()) {
                pis = in;
            } else {
                pis = new ByteArrayInputStream(Streams.readAll(in));
            }
            pis.mark(1);
            int tag = pis.read();
            if (tag == -1) {
                return null;
            }
            pis.reset();
            if (tag != 48) {
                return readPEMCRL(pis);
            }
            return readDERCRL(new ASN1InputStream(pis, true));
        } catch (CRLException e) {
            throw e;
        } catch (Exception e2) {
            throw new CRLException(e2.toString());
        }
    }

    @Override // java.security.cert.CertificateFactorySpi
    public Collection engineGenerateCRLs(InputStream inStream) throws CRLException {
        List crls = new ArrayList();
        BufferedInputStream in = new BufferedInputStream(inStream);
        while (true) {
            CRL crl = engineGenerateCRL(in);
            if (crl != null) {
                crls.add(crl);
            } else {
                return crls;
            }
        }
    }

    @Override // java.security.cert.CertificateFactorySpi
    public Iterator engineGetCertPathEncodings() {
        return PKIXCertPath.certPathEncodings.iterator();
    }

    @Override // java.security.cert.CertificateFactorySpi
    public CertPath engineGenerateCertPath(InputStream inStream) throws CertificateException {
        return engineGenerateCertPath(inStream, "PkiPath");
    }

    @Override // java.security.cert.CertificateFactorySpi
    public CertPath engineGenerateCertPath(InputStream inStream, String encoding) throws CertificateException {
        return new PKIXCertPath(inStream, encoding);
    }

    @Override // java.security.cert.CertificateFactorySpi
    public CertPath engineGenerateCertPath(List certificates) throws CertificateException {
        for (Object obj : certificates) {
            if (obj != null && !(obj instanceof X509Certificate)) {
                throw new CertificateException("list contains non X509Certificate object while creating CertPath\n" + obj.toString());
            }
        }
        return new PKIXCertPath(certificates);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class ExCertificateException extends CertificateException {
        private Throwable cause;

        public ExCertificateException(Throwable cause) {
            this.cause = cause;
        }

        public ExCertificateException(String msg, Throwable cause) {
            super(msg);
            this.cause = cause;
        }

        @Override // java.lang.Throwable
        public Throwable getCause() {
            return this.cause;
        }
    }
}
