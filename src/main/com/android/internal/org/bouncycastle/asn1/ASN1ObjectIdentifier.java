package com.android.internal.org.bouncycastle.asn1;

import android.media.MediaMetrics;
import com.android.internal.org.bouncycastle.util.Arrays;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
/* loaded from: classes4.dex */
public class ASN1ObjectIdentifier extends ASN1Primitive {
    private static final long LONG_LIMIT = 72057594037927808L;
    private static final ConcurrentMap<OidHandle, ASN1ObjectIdentifier> pool = new ConcurrentHashMap();
    private byte[] body;
    private final String identifier;

    public static ASN1ObjectIdentifier getInstance(Object obj) {
        if (obj == null || (obj instanceof ASN1ObjectIdentifier)) {
            return (ASN1ObjectIdentifier) obj;
        }
        if (obj instanceof ASN1Encodable) {
            ASN1Primitive primitive = ((ASN1Encodable) obj).toASN1Primitive();
            if (primitive instanceof ASN1ObjectIdentifier) {
                return (ASN1ObjectIdentifier) primitive;
            }
        }
        if (obj instanceof byte[]) {
            byte[] enc = (byte[]) obj;
            try {
                return (ASN1ObjectIdentifier) fromByteArray(enc);
            } catch (IOException e) {
                throw new IllegalArgumentException("failed to construct object identifier from byte[]: " + e.getMessage());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    public static ASN1ObjectIdentifier getInstance(ASN1TaggedObject obj, boolean explicit) {
        ASN1Primitive o = obj.getObject();
        if (explicit || (o instanceof ASN1ObjectIdentifier)) {
            return getInstance(o);
        }
        return fromOctetString(ASN1OctetString.getInstance(o).getOctets());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ASN1ObjectIdentifier(byte[] bytes) {
        StringBuffer objId = new StringBuffer();
        long value = 0;
        BigInteger bigValue = null;
        boolean first = true;
        for (int i = 0; i != bytes.length; i++) {
            int b = bytes[i] & 255;
            if (value <= LONG_LIMIT) {
                long value2 = value + (b & 127);
                if ((b & 128) == 0) {
                    if (first) {
                        if (value2 < 40) {
                            objId.append('0');
                        } else if (value2 < 80) {
                            objId.append('1');
                            value2 -= 40;
                        } else {
                            objId.append('2');
                            value2 -= 80;
                        }
                        first = false;
                    }
                    objId.append('.');
                    objId.append(value2);
                    value = 0;
                } else {
                    value = value2 << 7;
                }
            } else {
                BigInteger bigValue2 = (bigValue == null ? BigInteger.valueOf(value) : bigValue).or(BigInteger.valueOf(b & 127));
                if ((b & 128) == 0) {
                    if (first) {
                        objId.append('2');
                        bigValue2 = bigValue2.subtract(BigInteger.valueOf(80L));
                        first = false;
                    }
                    objId.append('.');
                    objId.append(bigValue2);
                    bigValue = null;
                    value = 0;
                } else {
                    bigValue = bigValue2.shiftLeft(7);
                }
            }
        }
        this.identifier = objId.toString().intern();
        this.body = Arrays.clone(bytes);
    }

    public ASN1ObjectIdentifier(String identifier) {
        if (identifier == null) {
            throw new NullPointerException("'identifier' cannot be null");
        }
        if (!isValidIdentifier(identifier)) {
            throw new IllegalArgumentException("string " + identifier + " not an OID");
        }
        this.identifier = identifier.intern();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ASN1ObjectIdentifier(ASN1ObjectIdentifier oid, String branchID) {
        if (!isValidBranchID(branchID, 0)) {
            throw new IllegalArgumentException("string " + branchID + " not a valid OID branch");
        }
        this.identifier = oid.getId() + MediaMetrics.SEPARATOR + branchID;
    }

    public String getId() {
        return this.identifier;
    }

    public ASN1ObjectIdentifier branch(String branchID) {
        return new ASN1ObjectIdentifier(this, branchID);
    }

    /* renamed from: on */
    public boolean m69on(ASN1ObjectIdentifier stem) {
        String id = getId();
        String stemId = stem.getId();
        return id.length() > stemId.length() && id.charAt(stemId.length()) == '.' && id.startsWith(stemId);
    }

    private void writeField(ByteArrayOutputStream out, long fieldValue) {
        byte[] result = new byte[9];
        int pos = 8;
        result[8] = (byte) (((int) fieldValue) & 127);
        while (fieldValue >= 128) {
            fieldValue >>= 7;
            pos--;
            result[pos] = (byte) ((((int) fieldValue) & 127) | 128);
        }
        out.write(result, pos, 9 - pos);
    }

    private void writeField(ByteArrayOutputStream out, BigInteger fieldValue) {
        int byteCount = (fieldValue.bitLength() + 6) / 7;
        if (byteCount == 0) {
            out.write(0);
            return;
        }
        BigInteger tmpValue = fieldValue;
        byte[] tmp = new byte[byteCount];
        for (int i = byteCount - 1; i >= 0; i--) {
            tmp[i] = (byte) ((tmpValue.intValue() & 127) | 128);
            tmpValue = tmpValue.shiftRight(7);
        }
        int i2 = byteCount - 1;
        tmp[i2] = (byte) (tmp[i2] & Byte.MAX_VALUE);
        out.write(tmp, 0, tmp.length);
    }

    private void doOutput(ByteArrayOutputStream aOut) {
        OIDTokenizer tok = new OIDTokenizer(this.identifier);
        int first = Integer.parseInt(tok.nextToken()) * 40;
        String secondToken = tok.nextToken();
        if (secondToken.length() <= 18) {
            writeField(aOut, first + Long.parseLong(secondToken));
        } else {
            writeField(aOut, new BigInteger(secondToken).add(BigInteger.valueOf(first)));
        }
        while (tok.hasMoreTokens()) {
            String token = tok.nextToken();
            if (token.length() <= 18) {
                writeField(aOut, Long.parseLong(token));
            } else {
                writeField(aOut, new BigInteger(token));
            }
        }
    }

    private synchronized byte[] getBody() {
        if (this.body == null) {
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            doOutput(bOut);
            this.body = bOut.toByteArray();
        }
        return this.body;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public boolean isConstructed() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public int encodedLength() throws IOException {
        int length = getBody().length;
        return StreamUtil.calculateBodyLength(length) + 1 + length;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public void encode(ASN1OutputStream out, boolean withTag) throws IOException {
        out.writeEncoded(withTag, 6, getBody());
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive, com.android.internal.org.bouncycastle.asn1.ASN1Object
    public int hashCode() {
        return this.identifier.hashCode();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public boolean asn1Equals(ASN1Primitive o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ASN1ObjectIdentifier)) {
            return false;
        }
        return this.identifier.equals(((ASN1ObjectIdentifier) o).identifier);
    }

    public String toString() {
        return getId();
    }

    /* JADX WARN: Code restructure failed: missing block: B:19:0x002e, code lost:
        return false;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private static boolean isValidBranchID(String branchID, int start) {
        int digitCount = 0;
        int pos = branchID.length();
        while (true) {
            pos--;
            if (pos < start) {
                return digitCount != 0 && (digitCount <= 1 || branchID.charAt(pos + 1) != '0');
            }
            char ch = branchID.charAt(pos);
            if (ch == '.') {
                if (digitCount == 0 || (digitCount > 1 && branchID.charAt(pos + 1) == '0')) {
                    break;
                }
                digitCount = 0;
            } else if ('0' > ch || ch > '9') {
                break;
            } else {
                digitCount++;
            }
        }
        return false;
    }

    private static boolean isValidIdentifier(String identifier) {
        char first;
        if (identifier.length() < 3 || identifier.charAt(1) != '.' || (first = identifier.charAt(0)) < '0' || first > '2') {
            return false;
        }
        return isValidBranchID(identifier, 2);
    }

    public ASN1ObjectIdentifier intern() {
        OidHandle hdl = new OidHandle(getBody());
        ConcurrentMap<OidHandle, ASN1ObjectIdentifier> concurrentMap = pool;
        ASN1ObjectIdentifier oid = concurrentMap.get(hdl);
        if (oid == null) {
            ASN1ObjectIdentifier oid2 = concurrentMap.putIfAbsent(hdl, this);
            if (oid2 == null) {
                return this;
            }
            return oid2;
        }
        return oid;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class OidHandle {
        private final byte[] enc;
        private final int key;

        OidHandle(byte[] enc) {
            this.key = Arrays.hashCode(enc);
            this.enc = enc;
        }

        public int hashCode() {
            return this.key;
        }

        public boolean equals(Object o) {
            if (o instanceof OidHandle) {
                return Arrays.areEqual(this.enc, ((OidHandle) o).enc);
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ASN1ObjectIdentifier fromOctetString(byte[] enc) {
        OidHandle hdl = new OidHandle(enc);
        ASN1ObjectIdentifier oid = pool.get(hdl);
        if (oid == null) {
            return new ASN1ObjectIdentifier(enc);
        }
        return oid;
    }
}
