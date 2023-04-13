package android.security;

import com.android.internal.org.bouncycastle.util.p027io.pem.PemObject;
import com.android.internal.org.bouncycastle.util.p027io.pem.PemReader;
import com.android.internal.org.bouncycastle.util.p027io.pem.PemWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes3.dex */
public class Credentials {
    public static final String ACTION_MANAGE_CREDENTIALS = "android.security.MANAGE_CREDENTIALS";
    @Deprecated
    public static final String APP_SOURCE_CERTIFICATE = "FSV_";
    @Deprecated
    public static final String CA_CERTIFICATE = "CACERT_";
    public static final String CERTIFICATE_USAGE_APP_SOURCE = "appsrc";
    public static final String CERTIFICATE_USAGE_CA = "ca";
    public static final String CERTIFICATE_USAGE_USER = "user";
    public static final String CERTIFICATE_USAGE_WIFI = "wifi";
    public static final String EXTENSION_CER = ".cer";
    public static final String EXTENSION_CRT = ".crt";
    public static final String EXTENSION_P12 = ".p12";
    public static final String EXTENSION_PFX = ".pfx";
    public static final String EXTRA_CA_CERTIFICATES_DATA = "ca_certificates_data";
    public static final String EXTRA_CERTIFICATE_USAGE = "certificate_install_usage";
    public static final String EXTRA_INSTALL_AS_UID = "install_as_uid";
    public static final String EXTRA_PRIVATE_KEY = "PKEY";
    public static final String EXTRA_PUBLIC_KEY = "KEY";
    public static final String EXTRA_USER_CERTIFICATE_DATA = "user_certificate_data";
    public static final String EXTRA_USER_KEY_ALIAS = "user_key_pair_name";
    public static final String EXTRA_USER_PRIVATE_KEY_DATA = "user_private_key_data";
    public static final String INSTALL_ACTION = "android.credentials.INSTALL";
    public static final String INSTALL_AS_USER_ACTION = "android.credentials.INSTALL_AS_USER";
    public static final String LOCKDOWN_VPN = "LOCKDOWN_VPN";
    private static final String LOGTAG = "Credentials";
    public static final String PLATFORM_VPN = "PLATFORM_VPN_";
    @Deprecated
    public static final String USER_CERTIFICATE = "USRCERT_";
    @Deprecated
    public static final String USER_PRIVATE_KEY = "USRPKEY_";
    @Deprecated
    public static final String USER_SECRET_KEY = "USRSKEY_";
    public static final String VPN = "VPN_";
    public static final String WIFI = "WIFI_";

    public static byte[] convertToPem(Certificate... objects) throws IOException, CertificateEncodingException {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        Writer writer = new OutputStreamWriter(bao, StandardCharsets.US_ASCII);
        PemWriter pw = new PemWriter(writer);
        for (Certificate o : objects) {
            pw.writeObject(new PemObject("CERTIFICATE", o.getEncoded()));
        }
        pw.close();
        return bao.toByteArray();
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x0060, code lost:
        throw new java.lang.IllegalArgumentException("Unknown type " + r5.getType());
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static List<X509Certificate> convertFromPem(byte[] bytes) throws IOException, CertificateException {
        ByteArrayInputStream bai = new ByteArrayInputStream(bytes);
        Reader reader = new InputStreamReader(bai, StandardCharsets.US_ASCII);
        PemReader pr = new PemReader(reader);
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X509");
            List<X509Certificate> result = new ArrayList<>();
            while (true) {
                PemObject o = pr.readPemObject();
                if (o != null) {
                    if (!o.getType().equals("CERTIFICATE")) {
                        break;
                    }
                    Certificate c = cf.generateCertificate(new ByteArrayInputStream(o.getContent()));
                    result.add((X509Certificate) c);
                } else {
                    return result;
                }
            }
        } finally {
            pr.close();
        }
    }
}
