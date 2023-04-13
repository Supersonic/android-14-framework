package com.android.internal.org.bouncycastle.asn1;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
/* loaded from: classes4.dex */
public class ASN1StreamParser {
    private final InputStream _in;
    private final int _limit;
    private final byte[][] tmpBuffers;

    public ASN1StreamParser(InputStream in) {
        this(in, StreamUtil.findLimit(in));
    }

    public ASN1StreamParser(InputStream in, int limit) {
        this._in = in;
        this._limit = limit;
        this.tmpBuffers = new byte[11];
    }

    public ASN1StreamParser(byte[] encoding) {
        this(new ByteArrayInputStream(encoding), encoding.length);
    }

    ASN1Encodable readIndef(int tagValue) throws IOException {
        switch (tagValue) {
            case 4:
                return new BEROctetStringParser(this);
            case 8:
                return new DERExternalParser(this);
            case 16:
                return new BERSequenceParser(this);
            case 17:
                return new BERSetParser(this);
            default:
                throw new ASN1Exception("unknown BER object encountered: 0x" + Integer.toHexString(tagValue));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ASN1Encodable readImplicit(boolean constructed, int tag) throws IOException {
        InputStream inputStream = this._in;
        if (inputStream instanceof IndefiniteLengthInputStream) {
            if (!constructed) {
                throw new IOException("indefinite-length primitive encoding encountered");
            }
            return readIndef(tag);
        }
        if (constructed) {
            switch (tag) {
                case 4:
                    return new BEROctetStringParser(this);
                case 16:
                    return new DLSequenceParser(this);
                case 17:
                    return new DLSetParser(this);
            }
        }
        switch (tag) {
            case 4:
                return new DEROctetStringParser((DefiniteLengthInputStream) inputStream);
            case 16:
                throw new ASN1Exception("sets must use constructed encoding (see X.690 8.11.1/8.12.1)");
            case 17:
                throw new ASN1Exception("sequences must use constructed encoding (see X.690 8.9.1/8.10.1)");
        }
        throw new ASN1Exception("implicit tagging not implemented");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ASN1Primitive readTaggedObject(boolean constructed, int tag) throws IOException {
        if (!constructed) {
            DefiniteLengthInputStream defIn = (DefiniteLengthInputStream) this._in;
            return new DLTaggedObject(false, tag, new DEROctetString(defIn.toByteArray()));
        }
        ASN1EncodableVector v = readVector();
        if (this._in instanceof IndefiniteLengthInputStream) {
            if (v.size() == 1) {
                return new BERTaggedObject(true, tag, v.get(0));
            }
            return new BERTaggedObject(false, tag, BERFactory.createSequence(v));
        } else if (v.size() == 1) {
            return new DLTaggedObject(true, tag, v.get(0));
        } else {
            return new DLTaggedObject(false, tag, DLFactory.createSequence(v));
        }
    }

    public ASN1Encodable readObject() throws IOException {
        int tag = this._in.read();
        if (tag == -1) {
            return null;
        }
        boolean z = false;
        set00Check(false);
        int tagNo = ASN1InputStream.readTagNumber(this._in, tag);
        boolean isConstructed = (tag & 32) != 0;
        InputStream inputStream = this._in;
        int i = this._limit;
        if (tagNo == 4 || tagNo == 16 || tagNo == 17 || tagNo == 8) {
            z = true;
        }
        int length = ASN1InputStream.readLength(inputStream, i, z);
        if (length < 0) {
            if (!isConstructed) {
                throw new IOException("indefinite-length primitive encoding encountered");
            }
            IndefiniteLengthInputStream indIn = new IndefiniteLengthInputStream(this._in, this._limit);
            ASN1StreamParser sp = new ASN1StreamParser(indIn, this._limit);
            if ((tag & 64) != 0) {
                return new BERApplicationSpecificParser(tagNo, sp);
            }
            if ((tag & 128) != 0) {
                return new BERTaggedObjectParser(true, tagNo, sp);
            }
            return sp.readIndef(tagNo);
        }
        DefiniteLengthInputStream defIn = new DefiniteLengthInputStream(this._in, length, this._limit);
        if ((tag & 64) != 0) {
            return new DLApplicationSpecific(isConstructed, tagNo, defIn.toByteArray());
        }
        if ((tag & 128) != 0) {
            return new BERTaggedObjectParser(isConstructed, tagNo, new ASN1StreamParser(defIn));
        }
        if (isConstructed) {
            switch (tagNo) {
                case 4:
                    return new BEROctetStringParser(new ASN1StreamParser(defIn));
                case 8:
                    return new DERExternalParser(new ASN1StreamParser(defIn));
                case 16:
                    return new DLSequenceParser(new ASN1StreamParser(defIn));
                case 17:
                    return new DLSetParser(new ASN1StreamParser(defIn));
                default:
                    throw new IOException("unknown tag " + tagNo + " encountered");
            }
        }
        switch (tagNo) {
            case 4:
                return new DEROctetStringParser(defIn);
            default:
                try {
                    return ASN1InputStream.createPrimitiveDERObject(tagNo, defIn, this.tmpBuffers);
                } catch (IllegalArgumentException e) {
                    throw new ASN1Exception("corrupted stream detected", e);
                }
        }
    }

    private void set00Check(boolean enabled) {
        InputStream inputStream = this._in;
        if (inputStream instanceof IndefiniteLengthInputStream) {
            ((IndefiniteLengthInputStream) inputStream).setEofOn00(enabled);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ASN1EncodableVector readVector() throws IOException {
        ASN1Encodable readObject;
        ASN1Encodable obj = readObject();
        if (obj == null) {
            return new ASN1EncodableVector(0);
        }
        ASN1EncodableVector v = new ASN1EncodableVector();
        do {
            if (obj instanceof InMemoryRepresentable) {
                v.add(((InMemoryRepresentable) obj).getLoadedObject());
            } else {
                v.add(obj.toASN1Primitive());
            }
            readObject = readObject();
            obj = readObject;
        } while (readObject != null);
        return v;
    }
}
