package com.android.server.locksettings.recoverablekeystore.certificate;

import com.android.internal.annotations.VisibleForTesting;
import java.security.SecureRandom;
import java.security.cert.CertPath;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.w3c.dom.Element;
/* loaded from: classes2.dex */
public final class CertXml {
    public final List<X509Certificate> endpointCerts;
    public final List<X509Certificate> intermediateCerts;
    public final long serial;

    public CertXml(long j, List<X509Certificate> list, List<X509Certificate> list2) {
        this.serial = j;
        this.intermediateCerts = list;
        this.endpointCerts = list2;
    }

    public long getSerial() {
        return this.serial;
    }

    @VisibleForTesting
    public List<X509Certificate> getAllIntermediateCerts() {
        return this.intermediateCerts;
    }

    @VisibleForTesting
    public List<X509Certificate> getAllEndpointCerts() {
        return this.endpointCerts;
    }

    public CertPath getRandomEndpointCert(X509Certificate x509Certificate) throws CertValidationException {
        return getEndpointCert(new SecureRandom().nextInt(this.endpointCerts.size()), null, x509Certificate);
    }

    @VisibleForTesting
    public CertPath getEndpointCert(int i, Date date, X509Certificate x509Certificate) throws CertValidationException {
        return CertUtils.validateCert(date, x509Certificate, this.intermediateCerts, this.endpointCerts.get(i));
    }

    public static CertXml parse(byte[] bArr) throws CertParsingException {
        Element xmlRootNode = CertUtils.getXmlRootNode(bArr);
        return new CertXml(parseSerial(xmlRootNode), parseIntermediateCerts(xmlRootNode), parseEndpointCerts(xmlRootNode));
    }

    public static long parseSerial(Element element) throws CertParsingException {
        return Long.parseLong(CertUtils.getXmlNodeContents(1, element, "metadata", "serial").get(0));
    }

    public static List<X509Certificate> parseIntermediateCerts(Element element) throws CertParsingException {
        List<String> xmlNodeContents = CertUtils.getXmlNodeContents(0, element, "intermediates", "cert");
        ArrayList arrayList = new ArrayList();
        for (String str : xmlNodeContents) {
            arrayList.add(CertUtils.decodeCert(CertUtils.decodeBase64(str)));
        }
        return Collections.unmodifiableList(arrayList);
    }

    public static List<X509Certificate> parseEndpointCerts(Element element) throws CertParsingException {
        List<String> xmlNodeContents = CertUtils.getXmlNodeContents(2, element, "endpoints", "cert");
        ArrayList arrayList = new ArrayList();
        for (String str : xmlNodeContents) {
            arrayList.add(CertUtils.decodeCert(CertUtils.decodeBase64(str)));
        }
        return Collections.unmodifiableList(arrayList);
    }
}
