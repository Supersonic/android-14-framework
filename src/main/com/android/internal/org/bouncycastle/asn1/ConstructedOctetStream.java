package com.android.internal.org.bouncycastle.asn1;

import java.io.IOException;
import java.io.InputStream;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes4.dex */
public class ConstructedOctetStream extends InputStream {
    private InputStream _currentStream;
    private boolean _first = true;
    private final ASN1StreamParser _parser;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConstructedOctetStream(ASN1StreamParser parser) {
        this._parser = parser;
    }

    @Override // java.io.InputStream
    public int read(byte[] b, int off, int len) throws IOException {
        ASN1OctetStringParser next;
        if (this._currentStream == null) {
            if (!this._first || (next = getNextParser()) == null) {
                return -1;
            }
            this._first = false;
            this._currentStream = next.getOctetStream();
        }
        int totalRead = 0;
        while (true) {
            int numRead = this._currentStream.read(b, off + totalRead, len - totalRead);
            if (numRead >= 0) {
                totalRead += numRead;
                if (totalRead == len) {
                    return totalRead;
                }
            } else {
                ASN1OctetStringParser next2 = getNextParser();
                if (next2 == null) {
                    this._currentStream = null;
                    if (totalRead < 1) {
                        return -1;
                    }
                    return totalRead;
                }
                this._currentStream = next2.getOctetStream();
            }
        }
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        ASN1OctetStringParser next;
        if (this._currentStream == null) {
            if (!this._first || (next = getNextParser()) == null) {
                return -1;
            }
            this._first = false;
            this._currentStream = next.getOctetStream();
        }
        while (true) {
            int b = this._currentStream.read();
            if (b >= 0) {
                return b;
            }
            ASN1OctetStringParser next2 = getNextParser();
            if (next2 == null) {
                this._currentStream = null;
                return -1;
            }
            this._currentStream = next2.getOctetStream();
        }
    }

    private ASN1OctetStringParser getNextParser() throws IOException {
        ASN1Encodable asn1Obj = this._parser.readObject();
        if (asn1Obj == null) {
            return null;
        }
        if (asn1Obj instanceof ASN1OctetStringParser) {
            return (ASN1OctetStringParser) asn1Obj;
        }
        throw new IOException("unknown object encountered: " + asn1Obj.getClass());
    }
}
