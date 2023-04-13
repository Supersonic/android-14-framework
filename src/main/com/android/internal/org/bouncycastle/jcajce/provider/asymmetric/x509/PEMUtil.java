package com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.x509;

import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.util.encoders.Base64;
import java.io.IOException;
import java.io.InputStream;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes4.dex */
public class PEMUtil {
    private final Boundaries[] _supportedBoundaries;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class Boundaries {
        private final String _footer;
        private final String _header;

        private Boundaries(String type) {
            this._header = "-----BEGIN " + type + "-----";
            this._footer = "-----END " + type + "-----";
        }

        public boolean isTheExpectedHeader(String line) {
            return line.startsWith(this._header);
        }

        public boolean isTheExpectedFooter(String line) {
            return line.startsWith(this._footer);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PEMUtil(String type) {
        this._supportedBoundaries = new Boundaries[]{new Boundaries(type), new Boundaries("X509 " + type), new Boundaries("PKCS7")};
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x001f, code lost:
        if (r0.length() == 0) goto L26;
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
            }
            l.append((char) c);
        }
        if (c < 0) {
            if (l.length() == 0) {
                return null;
            }
            return l.toString();
        }
        if (c == 13) {
            in.mark(1);
            int c2 = in.read();
            if (c2 == 10) {
                in.mark(1);
            }
            if (c2 > 0) {
                in.reset();
            }
        }
        return l.toString();
    }

    private Boundaries getBoundaries(String line) {
        Boundaries boundary;
        int i = 0;
        while (true) {
            Boundaries[] boundariesArr = this._supportedBoundaries;
            if (i != boundariesArr.length) {
                boundary = boundariesArr[i];
                if (boundary.isTheExpectedHeader(line) || boundary.isTheExpectedFooter(line)) {
                    break;
                }
                i++;
            } else {
                return null;
            }
        }
        return boundary;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ASN1Sequence readPEMObject(InputStream in) throws IOException {
        StringBuffer pemBuf = new StringBuffer();
        Boundaries header = null;
        while (header == null) {
            String line = readLine(in);
            if (line == null) {
                break;
            }
            header = getBoundaries(line);
            if (header != null && !header.isTheExpectedHeader(line)) {
                throw new IOException("malformed PEM data: found footer where header was expected");
            }
        }
        if (header == null) {
            throw new IOException("malformed PEM data: no header found");
        }
        Boundaries footer = null;
        while (footer == null) {
            String line2 = readLine(in);
            if (line2 == null) {
                break;
            }
            footer = getBoundaries(line2);
            if (footer != null) {
                if (!header.isTheExpectedFooter(line2)) {
                    throw new IOException("malformed PEM data: header/footer mismatch");
                }
            } else {
                pemBuf.append(line2);
            }
        }
        if (footer == null) {
            throw new IOException("malformed PEM data: no footer found");
        }
        if (pemBuf.length() != 0) {
            try {
                return ASN1Sequence.getInstance(Base64.decode(pemBuf.toString()));
            } catch (Exception e) {
                throw new IOException("malformed PEM data encountered");
            }
        }
        return null;
    }
}
