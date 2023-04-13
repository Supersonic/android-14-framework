package com.android.internal.org.bouncycastle.asn1;

import com.android.internal.org.bouncycastle.util.p027io.Streams;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
/* loaded from: classes4.dex */
public class ASN1InputStream extends FilterInputStream implements BERTags {
    private final boolean lazyEvaluate;
    private final int limit;
    private final byte[][] tmpBuffers;

    public ASN1InputStream(InputStream is) {
        this(is, StreamUtil.findLimit(is));
    }

    public ASN1InputStream(byte[] input) {
        this(new ByteArrayInputStream(input), input.length);
    }

    public ASN1InputStream(byte[] input, boolean lazyEvaluate) {
        this(new ByteArrayInputStream(input), input.length, lazyEvaluate);
    }

    public ASN1InputStream(InputStream input, int limit) {
        this(input, limit, false);
    }

    public ASN1InputStream(InputStream input, boolean lazyEvaluate) {
        this(input, StreamUtil.findLimit(input), lazyEvaluate);
    }

    public ASN1InputStream(InputStream input, int limit, boolean lazyEvaluate) {
        super(input);
        this.limit = limit;
        this.lazyEvaluate = lazyEvaluate;
        this.tmpBuffers = new byte[11];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getLimit() {
        return this.limit;
    }

    protected int readLength() throws IOException {
        return readLength(this, this.limit, false);
    }

    protected void readFully(byte[] bytes) throws IOException {
        if (Streams.readFully(this, bytes) != bytes.length) {
            throw new EOFException("EOF encountered in middle of object");
        }
    }

    protected ASN1Primitive buildObject(int tag, int tagNo, int length) throws IOException {
        boolean isConstructed = (tag & 32) != 0;
        DefiniteLengthInputStream defIn = new DefiniteLengthInputStream(this, length, this.limit);
        if ((tag & 64) != 0) {
            return new DLApplicationSpecific(isConstructed, tagNo, defIn.toByteArray());
        }
        if ((tag & 128) != 0) {
            return new ASN1StreamParser(defIn).readTaggedObject(isConstructed, tagNo);
        }
        if (isConstructed) {
            switch (tagNo) {
                case 4:
                    ASN1EncodableVector v = readVector(defIn);
                    ASN1OctetString[] strings = new ASN1OctetString[v.size()];
                    for (int i = 0; i != strings.length; i++) {
                        ASN1Encodable asn1Obj = v.get(i);
                        if (asn1Obj instanceof ASN1OctetString) {
                            strings[i] = (ASN1OctetString) asn1Obj;
                        } else {
                            throw new ASN1Exception("unknown object encountered in constructed OCTET STRING: " + asn1Obj.getClass());
                        }
                    }
                    return new BEROctetString(strings);
                case 8:
                    return new DLExternal(readVector(defIn));
                case 16:
                    if (this.lazyEvaluate) {
                        return new LazyEncodedSequence(defIn.toByteArray());
                    }
                    return DLFactory.createSequence(readVector(defIn));
                case 17:
                    return DLFactory.createSet(readVector(defIn));
                default:
                    throw new IOException("unknown tag " + tagNo + " encountered");
            }
        }
        return createPrimitiveDERObject(tagNo, defIn, this.tmpBuffers);
    }

    ASN1EncodableVector readVector(DefiniteLengthInputStream dIn) throws IOException {
        if (dIn.getRemaining() < 1) {
            return new ASN1EncodableVector(0);
        }
        ASN1InputStream subStream = new ASN1InputStream(dIn);
        ASN1EncodableVector v = new ASN1EncodableVector();
        while (true) {
            ASN1Primitive p = subStream.readObject();
            if (p != null) {
                v.add(p);
            } else {
                return v;
            }
        }
    }

    public ASN1Primitive readObject() throws IOException {
        int tag = read();
        if (tag <= 0) {
            if (tag == 0) {
                throw new IOException("unexpected end-of-contents marker");
            }
            return null;
        }
        int tagNo = readTagNumber(this, tag);
        boolean isConstructed = (tag & 32) != 0;
        int length = readLength();
        if (length < 0) {
            if (!isConstructed) {
                throw new IOException("indefinite-length primitive encoding encountered");
            }
            IndefiniteLengthInputStream indIn = new IndefiniteLengthInputStream(this, this.limit);
            ASN1StreamParser sp = new ASN1StreamParser(indIn, this.limit);
            if ((tag & 64) != 0) {
                return new BERApplicationSpecificParser(tagNo, sp).getLoadedObject();
            }
            if ((tag & 128) != 0) {
                return new BERTaggedObjectParser(true, tagNo, sp).getLoadedObject();
            }
            switch (tagNo) {
                case 4:
                    return new BEROctetStringParser(sp).getLoadedObject();
                case 8:
                    return new DERExternalParser(sp).getLoadedObject();
                case 16:
                    return new BERSequenceParser(sp).getLoadedObject();
                case 17:
                    return new BERSetParser(sp).getLoadedObject();
                default:
                    throw new IOException("unknown BER object encountered");
            }
        }
        try {
            return buildObject(tag, tagNo, length);
        } catch (IllegalArgumentException e) {
            throw new ASN1Exception("corrupted stream detected", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int readTagNumber(InputStream s, int tag) throws IOException {
        int tagNo = tag & 31;
        if (tagNo == 31) {
            int tagNo2 = 0;
            int b = s.read();
            if ((b & 127) == 0) {
                throw new IOException("corrupted stream - invalid high tag number found");
            }
            while (b >= 0 && (b & 128) != 0) {
                tagNo2 = (tagNo2 | (b & 127)) << 7;
                b = s.read();
            }
            if (b < 0) {
                throw new EOFException("EOF found inside tag value.");
            }
            return tagNo2 | (b & 127);
        }
        return tagNo;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int readLength(InputStream s, int limit, boolean isParsing) throws IOException {
        int length = s.read();
        if (length < 0) {
            throw new EOFException("EOF found when length expected");
        }
        if (length == 128) {
            return -1;
        }
        if (length > 127) {
            int size = length & 127;
            if (size > 4) {
                throw new IOException("DER length more than 4 bytes: " + size);
            }
            length = 0;
            for (int i = 0; i < size; i++) {
                int next = s.read();
                if (next < 0) {
                    throw new EOFException("EOF found reading length");
                }
                length = (length << 8) + next;
            }
            if (length < 0) {
                throw new IOException("corrupted stream - negative length found");
            }
            if (length >= limit && !isParsing) {
                throw new IOException("corrupted stream - out of bounds length found: " + length + " >= " + limit);
            }
        }
        return length;
    }

    private static byte[] getBuffer(DefiniteLengthInputStream defIn, byte[][] tmpBuffers) throws IOException {
        int len = defIn.getRemaining();
        if (len >= tmpBuffers.length) {
            return defIn.toByteArray();
        }
        byte[] buf = tmpBuffers[len];
        if (buf == null) {
            byte[] bArr = new byte[len];
            tmpBuffers[len] = bArr;
            buf = bArr;
        }
        defIn.readAllIntoByteArray(buf);
        return buf;
    }

    private static char[] getBMPCharBuffer(DefiniteLengthInputStream defIn) throws IOException {
        int stringPos;
        int remainingBytes = defIn.getRemaining();
        if ((remainingBytes & 1) != 0) {
            throw new IOException("malformed BMPString encoding encountered");
        }
        char[] string = new char[remainingBytes / 2];
        int stringPos2 = 0;
        byte[] buf = new byte[8];
        while (remainingBytes >= 8) {
            if (Streams.readFully(defIn, buf, 0, 8) == 8) {
                string[stringPos2] = (char) ((buf[0] << 8) | (buf[1] & 255));
                string[stringPos2 + 1] = (char) ((buf[2] << 8) | (buf[3] & 255));
                string[stringPos2 + 2] = (char) ((buf[4] << 8) | (buf[5] & 255));
                string[stringPos2 + 3] = (char) ((buf[6] << 8) | (buf[7] & 255));
                stringPos2 += 4;
                remainingBytes -= 8;
            } else {
                throw new EOFException("EOF encountered in middle of BMPString");
            }
        }
        if (remainingBytes > 0) {
            if (Streams.readFully(defIn, buf, 0, remainingBytes) != remainingBytes) {
                throw new EOFException("EOF encountered in middle of BMPString");
            }
            int bufPos = 0;
            while (true) {
                int bufPos2 = bufPos + 1;
                int b1 = buf[bufPos] << 8;
                int bufPos3 = bufPos2 + 1;
                int b2 = buf[bufPos2] & 255;
                stringPos = stringPos2 + 1;
                string[stringPos2] = (char) (b1 | b2);
                if (bufPos3 >= remainingBytes) {
                    break;
                }
                bufPos = bufPos3;
                stringPos2 = stringPos;
            }
            stringPos2 = stringPos;
        }
        if (defIn.getRemaining() != 0 || string.length != stringPos2) {
            throw new IllegalStateException();
        }
        return string;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ASN1Primitive createPrimitiveDERObject(int tagNo, DefiniteLengthInputStream defIn, byte[][] tmpBuffers) throws IOException {
        switch (tagNo) {
            case 1:
                return ASN1Boolean.fromOctetString(getBuffer(defIn, tmpBuffers));
            case 2:
                return new ASN1Integer(defIn.toByteArray(), false);
            case 3:
                return ASN1BitString.fromInputStream(defIn.getRemaining(), defIn);
            case 4:
                return new DEROctetString(defIn.toByteArray());
            case 5:
                return DERNull.INSTANCE;
            case 6:
                return ASN1ObjectIdentifier.fromOctetString(getBuffer(defIn, tmpBuffers));
            case 7:
            case 8:
            case 9:
            case 11:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 29:
            default:
                throw new IOException("unknown tag " + tagNo + " encountered");
            case 10:
                return ASN1Enumerated.fromOctetString(getBuffer(defIn, tmpBuffers));
            case 12:
                return new DERUTF8String(defIn.toByteArray());
            case 18:
                return new DERNumericString(defIn.toByteArray());
            case 19:
                return new DERPrintableString(defIn.toByteArray());
            case 20:
                return new DERT61String(defIn.toByteArray());
            case 21:
                return new DERVideotexString(defIn.toByteArray());
            case 22:
                return new DERIA5String(defIn.toByteArray());
            case 23:
                return new ASN1UTCTime(defIn.toByteArray());
            case 24:
                return new ASN1GeneralizedTime(defIn.toByteArray());
            case 25:
                return new DERGraphicString(defIn.toByteArray());
            case 26:
                return new DERVisibleString(defIn.toByteArray());
            case 27:
                return new DERGeneralString(defIn.toByteArray());
            case 28:
                return new DERUniversalString(defIn.toByteArray());
            case 30:
                return new DERBMPString(getBMPCharBuffer(defIn));
        }
    }
}
