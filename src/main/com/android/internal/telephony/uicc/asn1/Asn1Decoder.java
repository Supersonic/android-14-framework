package com.android.internal.telephony.uicc.asn1;

import com.android.internal.telephony.uicc.IccUtils;
/* loaded from: classes3.dex */
public final class Asn1Decoder {
    private final int mEnd;
    private int mPosition;
    private final byte[] mSrc;

    public Asn1Decoder(String hex) {
        this(IccUtils.hexStringToBytes(hex));
    }

    public Asn1Decoder(byte[] src) {
        this(src, 0, src.length);
    }

    public Asn1Decoder(byte[] bytes, int offset, int length) {
        if (offset < 0 || length < 0 || offset + length > bytes.length) {
            throw new IndexOutOfBoundsException("Out of the bounds: bytes=[" + bytes.length + "], offset=" + offset + ", length=" + length);
        }
        this.mSrc = bytes;
        this.mPosition = offset;
        this.mEnd = offset + length;
    }

    public int getPosition() {
        return this.mPosition;
    }

    public boolean hasNextNode() {
        return this.mPosition < this.mEnd;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public Asn1Node nextNode() throws InvalidAsn1DataException {
        int lenLen;
        if (this.mPosition >= this.mEnd) {
            throw new IllegalStateException("No bytes to parse.");
        }
        int offset = this.mPosition;
        int offset2 = offset + 1;
        if ((this.mSrc[offset] & 31) == 31) {
            while (offset2 < this.mEnd) {
                int i = this.mSrc[offset2] & 128;
                offset2++;
                if (i == 0) {
                    break;
                }
            }
        }
        if (offset2 < this.mEnd) {
            try {
                int tag = IccUtils.bytesToInt(this.mSrc, offset, offset2 - offset);
                byte[] bArr = this.mSrc;
                int offset3 = offset2 + 1;
                int b = bArr[offset2];
                if ((b & 128) == 0) {
                    lenLen = b;
                } else {
                    int dataLen = b & 127;
                    if (offset3 + dataLen > this.mEnd) {
                        throw new InvalidAsn1DataException(tag, "Cannot parse length at position: " + offset3);
                    }
                    try {
                        int dataLen2 = IccUtils.bytesToInt(bArr, offset3, dataLen);
                        offset3 += dataLen;
                        lenLen = dataLen2;
                    } catch (IllegalArgumentException e) {
                        throw new InvalidAsn1DataException(tag, "Cannot parse length at position: " + offset3, e);
                    }
                }
                if (offset3 + lenLen > this.mEnd) {
                    throw new InvalidAsn1DataException(tag, "Incomplete data at position: " + offset3 + ", expected bytes: " + lenLen + ", actual bytes: " + (this.mEnd - offset3));
                }
                Asn1Node root = new Asn1Node(tag, this.mSrc, offset3, lenLen);
                this.mPosition = offset3 + lenLen;
                return root;
            } catch (IllegalArgumentException e2) {
                throw new InvalidAsn1DataException(0, "Cannot parse tag at position: " + offset, e2);
            }
        }
        throw new InvalidAsn1DataException(0, "Invalid length at position: " + offset2);
    }
}
