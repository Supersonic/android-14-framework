package com.android.server.locksettings.recoverablekeystore.certificate;

import com.android.internal.annotations.VisibleForTesting;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.w3c.dom.Element;
/* loaded from: classes2.dex */
public final class SigXml {
    public final List<X509Certificate> intermediateCerts;
    public final byte[] signature;
    public final X509Certificate signerCert;

    public SigXml(List<X509Certificate> list, X509Certificate x509Certificate, byte[] bArr) {
        this.intermediateCerts = list;
        this.signerCert = x509Certificate;
        this.signature = bArr;
    }

    public void verifyFileSignature(X509Certificate x509Certificate, byte[] bArr) throws CertValidationException {
        verifyFileSignature(x509Certificate, bArr, null);
    }

    @VisibleForTesting
    public void verifyFileSignature(X509Certificate x509Certificate, byte[] bArr, Date date) throws CertValidationException {
        CertUtils.validateCert(date, x509Certificate, this.intermediateCerts, this.signerCert);
        CertUtils.verifyRsaSha256Signature(this.signerCert.getPublicKey(), this.signature, bArr);
    }

    public static SigXml parse(byte[] bArr) throws CertParsingException {
        Element xmlRootNode = CertUtils.getXmlRootNode(bArr);
        return new SigXml(parseIntermediateCerts(xmlRootNode), parseSignerCert(xmlRootNode), parseFileSignature(xmlRootNode));
    }

    public static List<X509Certificate> parseIntermediateCerts(Element element) throws CertParsingException {
        List<String> xmlNodeContents = CertUtils.getXmlNodeContents(0, element, "intermediates", "cert");
        ArrayList arrayList = new ArrayList();
        for (String str : xmlNodeContents) {
            arrayList.add(CertUtils.decodeCert(CertUtils.decodeBase64(str)));
        }
        return Collections.unmodifiableList(arrayList);
    }

    public static X509Certificate parseSignerCert(Element element) throws CertParsingException {
        return CertUtils.decodeCert(CertUtils.decodeBase64(CertUtils.getXmlNodeContents(1, element, "certificate").get(0)));
    }

    public static byte[] parseFileSignature(Element element) throws CertParsingException {
        return CertUtils.decodeBase64(CertUtils.getXmlNodeContents(1, element, "value").get(0));
    }
}
