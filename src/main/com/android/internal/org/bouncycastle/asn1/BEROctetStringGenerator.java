package com.android.internal.org.bouncycastle.asn1;

import java.io.IOException;
import java.io.OutputStream;
/* loaded from: classes4.dex */
public class BEROctetStringGenerator extends BERGenerator {
    public BEROctetStringGenerator(OutputStream out) throws IOException {
        super(out);
        writeBERHeader(36);
    }

    public BEROctetStringGenerator(OutputStream out, int tagNo, boolean isExplicit) throws IOException {
        super(out, tagNo, isExplicit);
        writeBERHeader(36);
    }

    public OutputStream getOctetOutputStream() {
        return getOctetOutputStream(new byte[1000]);
    }

    public OutputStream getOctetOutputStream(byte[] buf) {
        return new BufferedBEROctetStream(buf);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class BufferedBEROctetStream extends OutputStream {
        private byte[] _buf;
        private DEROutputStream _derOut;
        private int _off = 0;

        BufferedBEROctetStream(byte[] buf) {
            this._buf = buf;
            this._derOut = new DEROutputStream(BEROctetStringGenerator.this._out);
        }

        @Override // java.io.OutputStream
        public void write(int b) throws IOException {
            byte[] bArr = this._buf;
            int i = this._off;
            int i2 = i + 1;
            this._off = i2;
            bArr[i] = (byte) b;
            if (i2 == bArr.length) {
                DEROctetString.encode(this._derOut, true, bArr, 0, bArr.length);
                this._off = 0;
            }
        }

        @Override // java.io.OutputStream
        public void write(byte[] b, int off, int len) throws IOException {
            while (len > 0) {
                int numToCopy = Math.min(len, this._buf.length - this._off);
                System.arraycopy(b, off, this._buf, this._off, numToCopy);
                int i = this._off + numToCopy;
                this._off = i;
                byte[] bArr = this._buf;
                if (i >= bArr.length) {
                    DEROctetString.encode(this._derOut, true, bArr, 0, bArr.length);
                    this._off = 0;
                    off += numToCopy;
                    len -= numToCopy;
                } else {
                    return;
                }
            }
        }

        @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            int i = this._off;
            if (i != 0) {
                DEROctetString.encode(this._derOut, true, this._buf, 0, i);
            }
            this._derOut.flushInternal();
            BEROctetStringGenerator.this.writeBEREnd();
        }
    }
}
