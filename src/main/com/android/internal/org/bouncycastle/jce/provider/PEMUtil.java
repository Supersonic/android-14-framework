package com.android.internal.org.bouncycastle.jce.provider;

import com.android.internal.org.bouncycastle.asn1.ASN1InputStream;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.util.encoders.Base64;
import java.io.IOException;
import java.io.InputStream;
/* loaded from: classes4.dex */
public class PEMUtil {
    private final String _footer1;
    private final String _footer2;
    private final String _header1;
    private final String _header2;

    PEMUtil(String type) {
        this._header1 = "-----BEGIN " + type + "-----";
        this._header2 = "-----BEGIN X509 " + type + "-----";
        this._footer1 = "-----END " + type + "-----";
        this._footer2 = "-----END X509 " + type + "-----";
    }

    /* JADX WARN: Code restructure failed: missing block: B:13:0x0022, code lost:
        if (r0.length() == 0) goto L17;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private String readLine(InputStream in) throws IOException {
        int c;
        StringBuffer l = new StringBuffer();
        while (true) {
            c = in.read();
            if (c == 13 || c == 10 || c < 0) {
                break;
            } else if (c != 13) {
                l.append((char) c);
            }
        }
        if (c < 0) {
            return null;
        }
        return l.toString();
    }

    ASN1Sequence readPEMObject(InputStream in) throws IOException {
        String line;
        StringBuffer pemBuf = new StringBuffer();
        do {
            line = readLine(in);
            if (line == null || line.startsWith(this._header1)) {
                break;
            }
        } while (!line.startsWith(this._header2));
        while (true) {
            String line2 = readLine(in);
            if (line2 == null || line2.startsWith(this._footer1) || line2.startsWith(this._footer2)) {
                break;
            }
            pemBuf.append(line2);
        }
        if (pemBuf.length() != 0) {
            ASN1Primitive o = new ASN1InputStream(Base64.decode(pemBuf.toString())).readObject();
            if (!(o instanceof ASN1Sequence)) {
                throw new IOException("malformed PEM data encountered");
            }
            return (ASN1Sequence) o;
        }
        return null;
    }
}
