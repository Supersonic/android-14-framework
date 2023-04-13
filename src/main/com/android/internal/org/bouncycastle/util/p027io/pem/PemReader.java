package com.android.internal.org.bouncycastle.util.p027io.pem;

import com.android.internal.org.bouncycastle.util.encoders.Base64;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
/* renamed from: com.android.internal.org.bouncycastle.util.io.pem.PemReader */
/* loaded from: classes4.dex */
public class PemReader extends BufferedReader {
    private static final String BEGIN = "-----BEGIN ";
    private static final String END = "-----END ";

    public PemReader(Reader reader) {
        super(reader);
    }

    public PemObject readPemObject() throws IOException {
        String line;
        int index;
        String line2 = readLine();
        while (line2 != null && !line2.startsWith(BEGIN)) {
            line2 = readLine();
        }
        if (line2 != null && (index = (line = line2.substring(BEGIN.length())).indexOf(45)) > 0 && line.endsWith("-----") && line.length() - index == 5) {
            String type = line.substring(0, index);
            return loadObject(type);
        }
        return null;
    }

    private PemObject loadObject(String type) throws IOException {
        String line;
        String endMarker = END + type;
        StringBuffer buf = new StringBuffer();
        List headers = new ArrayList();
        while (true) {
            line = readLine();
            if (line == null) {
                break;
            } else if (line.indexOf(":") >= 0) {
                int index = line.indexOf(58);
                String hdr = line.substring(0, index);
                String value = line.substring(index + 1).trim();
                headers.add(new PemHeader(hdr, value));
            } else if (line.indexOf(endMarker) != -1) {
                break;
            } else {
                buf.append(line.trim());
            }
        }
        if (line == null) {
            throw new IOException(endMarker + " not found");
        }
        return new PemObject(type, headers, Base64.decode(buf.toString()));
    }
}
