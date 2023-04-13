package com.android.internal.org.bouncycastle.util.encoders;

import java.io.IOException;
import java.io.OutputStream;
/* loaded from: classes4.dex */
public class HexEncoder implements Encoder {
    protected final byte[] encodingTable = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};
    protected final byte[] decodingTable = new byte[128];

    protected void initialiseDecodingTable() {
        int i = 0;
        while (true) {
            byte[] bArr = this.decodingTable;
            if (i >= bArr.length) {
                break;
            }
            bArr[i] = -1;
            i++;
        }
        int i2 = 0;
        while (true) {
            byte[] bArr2 = this.encodingTable;
            if (i2 < bArr2.length) {
                this.decodingTable[bArr2[i2]] = (byte) i2;
                i2++;
            } else {
                byte[] bArr3 = this.decodingTable;
                bArr3[65] = bArr3[97];
                bArr3[66] = bArr3[98];
                bArr3[67] = bArr3[99];
                bArr3[68] = bArr3[100];
                bArr3[69] = bArr3[101];
                bArr3[70] = bArr3[102];
                return;
            }
        }
    }

    public HexEncoder() {
        initialiseDecodingTable();
    }

    public int encode(byte[] inBuf, int inOff, int inLen, byte[] outBuf, int outOff) throws IOException {
        int b = inOff;
        int inEnd = inOff + inLen;
        int outPos = outOff;
        while (b < inEnd) {
            int inPos = b + 1;
            int inPos2 = inBuf[b];
            int b2 = inPos2 & 255;
            int outPos2 = outPos + 1;
            byte[] bArr = this.encodingTable;
            outBuf[outPos] = bArr[b2 >>> 4];
            outPos = outPos2 + 1;
            outBuf[outPos2] = bArr[b2 & 15];
            b = inPos;
        }
        int inPos3 = outPos - outOff;
        return inPos3;
    }

    @Override // com.android.internal.org.bouncycastle.util.encoders.Encoder
    public int encode(byte[] buf, int off, int len, OutputStream out) throws IOException {
        byte[] tmp = new byte[72];
        while (len > 0) {
            int inLen = Math.min(36, len);
            int outLen = encode(buf, off, inLen, tmp, 0);
            out.write(tmp, 0, outLen);
            off += inLen;
            len -= inLen;
        }
        return len * 2;
    }

    private static boolean ignore(char c) {
        return c == '\n' || c == '\r' || c == '\t' || c == ' ';
    }

    @Override // com.android.internal.org.bouncycastle.util.encoders.Encoder
    public int decode(byte[] data, int off, int length, OutputStream out) throws IOException {
        int outLen = 0;
        byte[] buf = new byte[36];
        int bufOff = 0;
        int end = off + length;
        while (end > off && ignore((char) data[end - 1])) {
            end--;
        }
        int i = off;
        while (i < end) {
            while (i < end && ignore((char) data[i])) {
                i++;
            }
            int i2 = i + 1;
            byte b1 = this.decodingTable[data[i]];
            while (i2 < end && ignore((char) data[i2])) {
                i2++;
            }
            int i3 = i2 + 1;
            byte b2 = this.decodingTable[data[i2]];
            if ((b1 | b2) < 0) {
                throw new IOException("invalid characters encountered in Hex data");
            }
            int bufOff2 = bufOff + 1;
            buf[bufOff] = (byte) ((b1 << 4) | b2);
            if (bufOff2 != buf.length) {
                bufOff = bufOff2;
            } else {
                out.write(buf);
                bufOff = 0;
            }
            outLen++;
            i = i3;
        }
        if (bufOff > 0) {
            out.write(buf, 0, bufOff);
        }
        return outLen;
    }

    @Override // com.android.internal.org.bouncycastle.util.encoders.Encoder
    public int decode(String data, OutputStream out) throws IOException {
        int length = 0;
        byte[] buf = new byte[36];
        int bufOff = 0;
        int end = data.length();
        while (end > 0 && ignore(data.charAt(end - 1))) {
            end--;
        }
        int i = 0;
        while (i < end) {
            while (i < end && ignore(data.charAt(i))) {
                i++;
            }
            int i2 = i + 1;
            byte b1 = this.decodingTable[data.charAt(i)];
            while (i2 < end && ignore(data.charAt(i2))) {
                i2++;
            }
            int i3 = i2 + 1;
            byte b2 = this.decodingTable[data.charAt(i2)];
            if ((b1 | b2) < 0) {
                throw new IOException("invalid characters encountered in Hex string");
            }
            int bufOff2 = bufOff + 1;
            buf[bufOff] = (byte) ((b1 << 4) | b2);
            if (bufOff2 != buf.length) {
                bufOff = bufOff2;
            } else {
                out.write(buf);
                bufOff = 0;
            }
            length++;
            i = i3;
        }
        if (bufOff > 0) {
            out.write(buf, 0, bufOff);
        }
        return length;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public byte[] decodeStrict(String str, int off, int len) throws IOException {
        if (str == null) {
            throw new NullPointerException("'str' cannot be null");
        }
        if (off < 0 || len < 0 || off > str.length() - len) {
            throw new IndexOutOfBoundsException("invalid offset and/or length specified");
        }
        if ((len & 1) != 0) {
            throw new IOException("a hexadecimal encoding must have an even number of characters");
        }
        int resultLen = len >>> 1;
        byte[] result = new byte[resultLen];
        int strPos = off;
        int i = 0;
        while (i < resultLen) {
            int strPos2 = strPos + 1;
            byte b1 = this.decodingTable[str.charAt(strPos)];
            int strPos3 = strPos2 + 1;
            byte b2 = this.decodingTable[str.charAt(strPos2)];
            int n = (b1 << 4) | b2;
            if (n < 0) {
                throw new IOException("invalid characters encountered in Hex string");
            }
            result[i] = (byte) n;
            i++;
            strPos = strPos3;
        }
        return result;
    }
}
