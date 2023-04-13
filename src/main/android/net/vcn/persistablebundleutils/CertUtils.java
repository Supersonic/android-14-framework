package android.net.vcn.persistablebundleutils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Objects;
/* loaded from: classes2.dex */
public class CertUtils {
    private static final String CERT_TYPE_X509 = "X.509";
    private static final String PRIVATE_KEY_TYPE_RSA = "RSA";

    public static X509Certificate certificateFromByteArray(byte[] derEncoded) {
        Objects.requireNonNull(derEncoded, "derEncoded is null");
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance(CERT_TYPE_X509);
            InputStream in = new ByteArrayInputStream(derEncoded);
            return (X509Certificate) certFactory.generateCertificate(in);
        } catch (CertificateException e) {
            throw new IllegalArgumentException("Fail to decode certificate", e);
        }
    }

    public static RSAPrivateKey privateKeyFromByteArray(byte[] pkcs8Encoded) {
        Objects.requireNonNull(pkcs8Encoded, "pkcs8Encoded was null");
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(pkcs8Encoded);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalArgumentException("Fail to decode PrivateKey", e);
        }
    }
}
