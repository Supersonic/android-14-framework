package com.android.server.locksettings.recoverablekeystore.certificate;

import com.android.internal.annotations.VisibleForTesting;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/* loaded from: classes2.dex */
public final class CertUtils {
    public static X509Certificate decodeCert(byte[] bArr) throws CertParsingException {
        return decodeCert(new ByteArrayInputStream(bArr));
    }

    public static X509Certificate decodeCert(InputStream inputStream) throws CertParsingException {
        try {
            try {
                return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(inputStream);
            } catch (CertificateException e) {
                throw new CertParsingException(e);
            }
        } catch (CertificateException e2) {
            throw new RuntimeException(e2);
        }
    }

    public static Element getXmlRootNode(byte[] bArr) throws CertParsingException {
        try {
            Document parse = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(bArr));
            parse.getDocumentElement().normalize();
            return parse.getDocumentElement();
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new CertParsingException(e);
        }
    }

    public static List<String> getXmlNodeContents(int i, Element element, String... strArr) throws CertParsingException {
        if (strArr.length == 0) {
            throw new CertParsingException("The tag list must not be empty");
        }
        for (int i2 = 0; i2 < strArr.length - 1; i2++) {
            String str = strArr[i2];
            List<Element> xmlDirectChildren = getXmlDirectChildren(element, str);
            if ((xmlDirectChildren.size() == 0 && i != 0) || xmlDirectChildren.size() > 1) {
                throw new CertParsingException("The XML file must contain exactly one path with the tag " + str);
            } else if (xmlDirectChildren.size() == 0) {
                return new ArrayList();
            } else {
                element = xmlDirectChildren.get(0);
            }
        }
        List<Element> xmlDirectChildren2 = getXmlDirectChildren(element, strArr[strArr.length - 1]);
        if (i == 1 && xmlDirectChildren2.size() != 1) {
            throw new CertParsingException("The XML file must contain exactly one node with the path " + String.join("/", strArr));
        } else if (i == 2 && xmlDirectChildren2.size() == 0) {
            throw new CertParsingException("The XML file must contain at least one node with the path " + String.join("/", strArr));
        } else {
            ArrayList arrayList = new ArrayList();
            for (Element element2 : xmlDirectChildren2) {
                arrayList.add(element2.getTextContent().replaceAll("\\s", ""));
            }
            return arrayList;
        }
    }

    public static List<Element> getXmlDirectChildren(Element element, String str) {
        ArrayList arrayList = new ArrayList();
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() == 1 && item.getNodeName().equals(str)) {
                arrayList.add((Element) item);
            }
        }
        return arrayList;
    }

    public static byte[] decodeBase64(String str) throws CertParsingException {
        try {
            return Base64.getDecoder().decode(str);
        } catch (IllegalArgumentException e) {
            throw new CertParsingException(e);
        }
    }

    public static void verifyRsaSha256Signature(PublicKey publicKey, byte[] bArr, byte[] bArr2) throws CertValidationException {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            try {
                signature.initVerify(publicKey);
                signature.update(bArr2);
                if (signature.verify(bArr)) {
                    return;
                }
                throw new CertValidationException("The signature is invalid");
            } catch (InvalidKeyException | SignatureException e) {
                throw new CertValidationException(e);
            }
        } catch (NoSuchAlgorithmException e2) {
            throw new RuntimeException(e2);
        }
    }

    public static CertPath validateCert(Date date, X509Certificate x509Certificate, List<X509Certificate> list, X509Certificate x509Certificate2) throws CertValidationException {
        PKIXParameters buildPkixParams = buildPkixParams(date, x509Certificate, list, x509Certificate2);
        CertPath buildCertPath = buildCertPath(buildPkixParams);
        try {
            try {
                CertPathValidator.getInstance("PKIX").validate(buildCertPath, buildPkixParams);
                return buildCertPath;
            } catch (InvalidAlgorithmParameterException | CertPathValidatorException e) {
                throw new CertValidationException(e);
            }
        } catch (NoSuchAlgorithmException e2) {
            throw new RuntimeException(e2);
        }
    }

    public static void validateCertPath(X509Certificate x509Certificate, CertPath certPath) throws CertValidationException {
        validateCertPath(null, x509Certificate, certPath);
    }

    @VisibleForTesting
    public static void validateCertPath(Date date, X509Certificate x509Certificate, CertPath certPath) throws CertValidationException {
        if (certPath.getCertificates().isEmpty()) {
            throw new CertValidationException("The given certificate path is empty");
        }
        if (!(certPath.getCertificates().get(0) instanceof X509Certificate)) {
            throw new CertValidationException("The given certificate path does not contain X509 certificates");
        }
        List<? extends Certificate> certificates = certPath.getCertificates();
        validateCert(date, x509Certificate, certificates.subList(1, certificates.size()), (X509Certificate) certificates.get(0));
    }

    @VisibleForTesting
    public static CertPath buildCertPath(PKIXParameters pKIXParameters) throws CertValidationException {
        try {
            try {
                return CertPathBuilder.getInstance("PKIX").build(pKIXParameters).getCertPath();
            } catch (InvalidAlgorithmParameterException | CertPathBuilderException e) {
                throw new CertValidationException(e);
            }
        } catch (NoSuchAlgorithmException e2) {
            throw new RuntimeException(e2);
        }
    }

    @VisibleForTesting
    public static PKIXParameters buildPkixParams(Date date, X509Certificate x509Certificate, List<X509Certificate> list, X509Certificate x509Certificate2) throws CertValidationException {
        HashSet hashSet = new HashSet();
        hashSet.add(new TrustAnchor(x509Certificate, null));
        ArrayList arrayList = new ArrayList(list);
        arrayList.add(x509Certificate2);
        try {
            CertStore certStore = CertStore.getInstance("Collection", new CollectionCertStoreParameters(arrayList));
            X509CertSelector x509CertSelector = new X509CertSelector();
            x509CertSelector.setCertificate(x509Certificate2);
            try {
                PKIXBuilderParameters pKIXBuilderParameters = new PKIXBuilderParameters(hashSet, x509CertSelector);
                pKIXBuilderParameters.addCertStore(certStore);
                pKIXBuilderParameters.setDate(date);
                pKIXBuilderParameters.setRevocationEnabled(false);
                return pKIXBuilderParameters;
            } catch (InvalidAlgorithmParameterException e) {
                throw new CertValidationException(e);
            }
        } catch (InvalidAlgorithmParameterException e2) {
            throw new CertValidationException(e2);
        } catch (NoSuchAlgorithmException e3) {
            throw new RuntimeException(e3);
        }
    }
}
