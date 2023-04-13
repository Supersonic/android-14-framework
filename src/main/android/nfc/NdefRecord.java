package android.nfc;

import android.content.Intent;
import android.net.Uri;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.proto.ProtoOutputStream;
import android.webkit.WebView;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
/* loaded from: classes2.dex */
public final class NdefRecord implements Parcelable {
    private static final byte FLAG_CF = 32;
    private static final byte FLAG_IL = 8;
    private static final byte FLAG_MB = Byte.MIN_VALUE;
    private static final byte FLAG_ME = 64;
    private static final byte FLAG_SR = 16;
    private static final int MAX_PAYLOAD_SIZE = 10485760;
    public static final short TNF_ABSOLUTE_URI = 3;
    public static final short TNF_EMPTY = 0;
    public static final short TNF_EXTERNAL_TYPE = 4;
    public static final short TNF_MIME_MEDIA = 2;
    public static final short TNF_RESERVED = 7;
    public static final short TNF_UNCHANGED = 6;
    public static final short TNF_UNKNOWN = 5;
    public static final short TNF_WELL_KNOWN = 1;
    private final byte[] mId;
    private final byte[] mPayload;
    private final short mTnf;
    private final byte[] mType;
    public static final byte[] RTD_TEXT = {84};
    public static final byte[] RTD_URI = {85};
    public static final byte[] RTD_SMART_POSTER = {83, 112};
    public static final byte[] RTD_ALTERNATIVE_CARRIER = {97, 99};
    public static final byte[] RTD_HANDOVER_CARRIER = {72, 99};
    public static final byte[] RTD_HANDOVER_REQUEST = {72, 114};
    public static final byte[] RTD_HANDOVER_SELECT = {72, 115};
    public static final byte[] RTD_ANDROID_APP = "android.com:pkg".getBytes();
    private static final String[] URI_PREFIX_MAP = {"", "http://www.", "https://www.", "http://", "https://", WebView.SCHEME_TEL, "mailto:", "ftp://anonymous:anonymous@", "ftp://ftp.", "ftps://", "sftp://", "smb://", "nfs://", "ftp://", "dav://", "news:", "telnet://", "imap:", "rtsp://", "urn:", "pop:", "sip:", "sips:", "tftp:", "btspp://", "btl2cap://", "btgoep://", "tcpobex://", "irdaobex://", "file://", "urn:epc:id:", "urn:epc:tag:", "urn:epc:pat:", "urn:epc:raw:", "urn:epc:", "urn:nfc:"};
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    public static final Parcelable.Creator<NdefRecord> CREATOR = new Parcelable.Creator<NdefRecord>() { // from class: android.nfc.NdefRecord.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NdefRecord createFromParcel(Parcel in) {
            short tnf = (short) in.readInt();
            int typeLength = in.readInt();
            byte[] type = new byte[typeLength];
            in.readByteArray(type);
            int idLength = in.readInt();
            byte[] id = new byte[idLength];
            in.readByteArray(id);
            int payloadLength = in.readInt();
            byte[] payload = new byte[payloadLength];
            in.readByteArray(payload);
            return new NdefRecord(tnf, type, id, payload);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NdefRecord[] newArray(int size) {
            return new NdefRecord[size];
        }
    };

    public static NdefRecord createApplicationRecord(String packageName) {
        if (packageName == null) {
            throw new NullPointerException("packageName is null");
        }
        if (packageName.length() == 0) {
            throw new IllegalArgumentException("packageName is empty");
        }
        return new NdefRecord((short) 4, RTD_ANDROID_APP, null, packageName.getBytes(StandardCharsets.UTF_8));
    }

    public static NdefRecord createUri(Uri uri) {
        if (uri == null) {
            throw new NullPointerException("uri is null");
        }
        String uriString = uri.normalizeScheme().toString();
        if (uriString.length() == 0) {
            throw new IllegalArgumentException("uri is empty");
        }
        byte prefix = 0;
        int i = 1;
        while (true) {
            String[] strArr = URI_PREFIX_MAP;
            if (i >= strArr.length) {
                break;
            } else if (!uriString.startsWith(strArr[i])) {
                i++;
            } else {
                prefix = (byte) i;
                uriString = uriString.substring(strArr[i].length());
                break;
            }
        }
        byte[] uriBytes = uriString.getBytes(StandardCharsets.UTF_8);
        byte[] recordBytes = new byte[uriBytes.length + 1];
        recordBytes[0] = prefix;
        System.arraycopy(uriBytes, 0, recordBytes, 1, uriBytes.length);
        return new NdefRecord((short) 1, RTD_URI, null, recordBytes);
    }

    public static NdefRecord createUri(String uriString) {
        return createUri(Uri.parse(uriString));
    }

    public static NdefRecord createMime(String mimeType, byte[] mimeData) {
        if (mimeType == null) {
            throw new NullPointerException("mimeType is null");
        }
        String mimeType2 = Intent.normalizeMimeType(mimeType);
        if (mimeType2.length() == 0) {
            throw new IllegalArgumentException("mimeType is empty");
        }
        int slashIndex = mimeType2.indexOf(47);
        if (slashIndex == 0) {
            throw new IllegalArgumentException("mimeType must have major type");
        }
        if (slashIndex == mimeType2.length() - 1) {
            throw new IllegalArgumentException("mimeType must have minor type");
        }
        byte[] typeBytes = mimeType2.getBytes(StandardCharsets.US_ASCII);
        return new NdefRecord((short) 2, typeBytes, null, mimeData);
    }

    public static NdefRecord createExternal(String domain, String type, byte[] data) {
        if (domain == null) {
            throw new NullPointerException("domain is null");
        }
        if (type == null) {
            throw new NullPointerException("type is null");
        }
        String domain2 = domain.trim().toLowerCase(Locale.ROOT);
        String type2 = type.trim().toLowerCase(Locale.ROOT);
        if (domain2.length() == 0) {
            throw new IllegalArgumentException("domain is empty");
        }
        if (type2.length() == 0) {
            throw new IllegalArgumentException("type is empty");
        }
        byte[] byteDomain = domain2.getBytes(StandardCharsets.UTF_8);
        byte[] byteType = type2.getBytes(StandardCharsets.UTF_8);
        byte[] b = new byte[byteDomain.length + 1 + byteType.length];
        System.arraycopy(byteDomain, 0, b, 0, byteDomain.length);
        b[byteDomain.length] = 58;
        System.arraycopy(byteType, 0, b, byteDomain.length + 1, byteType.length);
        return new NdefRecord((short) 4, b, null, data);
    }

    public static NdefRecord createTextRecord(String languageCode, String text) {
        byte[] languageCodeBytes;
        if (text == null) {
            throw new NullPointerException("text is null");
        }
        byte[] textBytes = text.getBytes(StandardCharsets.UTF_8);
        if (languageCode != null && !languageCode.isEmpty()) {
            languageCodeBytes = languageCode.getBytes(StandardCharsets.US_ASCII);
        } else {
            languageCodeBytes = Locale.getDefault().getLanguage().getBytes(StandardCharsets.US_ASCII);
        }
        if (languageCodeBytes.length >= 64) {
            throw new IllegalArgumentException("language code is too long, must be <64 bytes.");
        }
        ByteBuffer buffer = ByteBuffer.allocate(languageCodeBytes.length + 1 + textBytes.length);
        byte status = (byte) (languageCodeBytes.length & 255);
        buffer.put(status);
        buffer.put(languageCodeBytes);
        buffer.put(textBytes);
        return new NdefRecord((short) 1, RTD_TEXT, null, buffer.array());
    }

    public NdefRecord(short tnf, byte[] type, byte[] id, byte[] payload) {
        type = type == null ? EMPTY_BYTE_ARRAY : type;
        id = id == null ? EMPTY_BYTE_ARRAY : id;
        payload = payload == null ? EMPTY_BYTE_ARRAY : payload;
        String message = validateTnf(tnf, type, id, payload);
        if (message != null) {
            throw new IllegalArgumentException(message);
        }
        this.mTnf = tnf;
        this.mType = type;
        this.mId = id;
        this.mPayload = payload;
    }

    @Deprecated
    public NdefRecord(byte[] data) throws FormatException {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        NdefRecord[] rs = parse(buffer, true);
        if (buffer.remaining() > 0) {
            throw new FormatException("data too long");
        }
        this.mTnf = rs[0].mTnf;
        this.mType = rs[0].mType;
        this.mId = rs[0].mId;
        this.mPayload = rs[0].mPayload;
    }

    public short getTnf() {
        return this.mTnf;
    }

    public byte[] getType() {
        return (byte[]) this.mType.clone();
    }

    public byte[] getId() {
        return (byte[]) this.mId.clone();
    }

    public byte[] getPayload() {
        return (byte[]) this.mPayload.clone();
    }

    @Deprecated
    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(getByteLength());
        writeToByteBuffer(buffer, true, true);
        return buffer.array();
    }

    public String toMimeType() {
        switch (this.mTnf) {
            case 1:
                if (Arrays.equals(this.mType, RTD_TEXT)) {
                    return "text/plain";
                }
                return null;
            case 2:
                String mimeType = new String(this.mType, StandardCharsets.US_ASCII);
                return Intent.normalizeMimeType(mimeType);
            default:
                return null;
        }
    }

    public Uri toUri() {
        return toUri(false);
    }

    private Uri toUri(boolean inSmartPoster) {
        Uri wktUri;
        NdefRecord[] records;
        switch (this.mTnf) {
            case 1:
                if (Arrays.equals(this.mType, RTD_SMART_POSTER) && !inSmartPoster) {
                    try {
                        NdefMessage nestedMessage = new NdefMessage(this.mPayload);
                        for (NdefRecord nestedRecord : nestedMessage.getRecords()) {
                            Uri uri = nestedRecord.toUri(true);
                            if (uri != null) {
                                return uri;
                            }
                        }
                        break;
                    } catch (FormatException e) {
                        break;
                    }
                } else if (!Arrays.equals(this.mType, RTD_URI) || (wktUri = parseWktUri()) == null) {
                    return null;
                } else {
                    return wktUri.normalizeScheme();
                }
                break;
            case 3:
                return Uri.parse(new String(this.mType, StandardCharsets.UTF_8)).normalizeScheme();
            case 4:
                if (!inSmartPoster) {
                    return Uri.parse("vnd.android.nfc://ext/" + new String(this.mType, StandardCharsets.US_ASCII));
                }
                break;
        }
        return null;
    }

    private Uri parseWktUri() {
        int prefixIndex;
        byte[] bArr = this.mPayload;
        if (bArr.length >= 2 && (prefixIndex = bArr[0] & (-1)) >= 0) {
            String[] strArr = URI_PREFIX_MAP;
            if (prefixIndex < strArr.length) {
                String prefix = strArr[prefixIndex];
                String suffix = new String(Arrays.copyOfRange(bArr, 1, bArr.length), StandardCharsets.UTF_8);
                return Uri.parse(prefix + suffix);
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x0072, code lost:
        if (r11 != false) goto L40;
     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x007c, code lost:
        throw new android.nfc.FormatException("unexpected IL flag in non-leading chunk");
     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x009a, code lost:
        if (r15 == 6) goto L59;
     */
    /* JADX WARN: Code restructure failed: missing block: B:63:0x00a4, code lost:
        throw new android.nfc.FormatException("unexpected TNF_UNCHANGED in first chunk or unchunked record");
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static NdefRecord[] parse(ByteBuffer buffer, boolean ignoreMbMe) throws FormatException {
        byte[] type;
        byte[] id;
        long payloadLength;
        short chunkTnf;
        boolean me;
        byte[] type2;
        byte[] id2;
        short chunkTnf2;
        byte[] payload;
        ByteBuffer byteBuffer = buffer;
        List<NdefRecord> records = new ArrayList<>();
        byte[] type3 = null;
        byte[] id3 = null;
        byte[] payload2 = null;
        try {
            ArrayList<byte[]> chunks = new ArrayList<>();
            boolean inChunk = false;
            short chunkTnf3 = -1;
            boolean id4 = false;
            while (!id4) {
                byte flag = buffer.get();
                boolean il = true;
                boolean mb = (flag & Byte.MIN_VALUE) != 0;
                boolean me2 = (flag & 64) != 0;
                boolean cf = (flag & 32) != 0;
                boolean sr = (flag & 16) != 0;
                if ((flag & 8) == 0) {
                    il = false;
                }
                short tnf = (short) (flag & 7);
                if (mb || records.size() != 0 || inChunk) {
                    type = type3;
                } else if (!ignoreMbMe) {
                    throw new FormatException("expected MB flag");
                } else {
                    type = type3;
                }
                if (mb && ((records.size() != 0 || inChunk) && !ignoreMbMe)) {
                    throw new FormatException("unexpected MB flag");
                }
                if (cf && me2) {
                    throw new FormatException("unexpected ME flag in non-trailing chunk");
                }
                if (inChunk && tnf != 6) {
                    throw new FormatException("expected TNF_UNCHANGED in non-leading chunk");
                }
                int typeLength = buffer.get() & 255;
                if (sr) {
                    id = id3;
                    payloadLength = buffer.get() & 255;
                } else {
                    id = id3;
                    payloadLength = buffer.getInt() & 4294967295L;
                }
                int idLength = il ? buffer.get() & 255 : 0;
                if (!inChunk) {
                    chunkTnf = chunkTnf3;
                    me = me2;
                } else if (typeLength != 0) {
                    throw new FormatException("expected zero-length type in non-leading chunk");
                } else {
                    chunkTnf = chunkTnf3;
                    me = me2;
                }
                if (inChunk) {
                    type2 = type;
                    id2 = id;
                } else {
                    type2 = typeLength > 0 ? new byte[typeLength] : EMPTY_BYTE_ARRAY;
                    id2 = idLength > 0 ? new byte[idLength] : EMPTY_BYTE_ARRAY;
                    byteBuffer.get(type2);
                    byteBuffer.get(id2);
                }
                ensureSanePayloadSize(payloadLength);
                byte[] payload3 = payloadLength > 0 ? new byte[(int) payloadLength] : EMPTY_BYTE_ARRAY;
                byteBuffer.get(payload3);
                if (cf && !inChunk) {
                    if (typeLength == 0 && tnf != 5) {
                        throw new FormatException("expected non-zero type length in first chunk");
                    }
                    chunks.clear();
                    chunkTnf2 = tnf;
                } else {
                    chunkTnf2 = chunkTnf;
                }
                if (cf || inChunk) {
                    chunks.add(payload3);
                }
                if (!cf && inChunk) {
                    long payloadLength2 = 0;
                    Iterator<byte[]> it = chunks.iterator();
                    while (it.hasNext()) {
                        payloadLength2 += it.next().length;
                        it = it;
                        payload3 = payload3;
                        inChunk = inChunk;
                        mb = mb;
                    }
                    ensureSanePayloadSize(payloadLength2);
                    payload = new byte[(int) payloadLength2];
                    int i = 0;
                    Iterator<byte[]> it2 = chunks.iterator();
                    while (it2.hasNext()) {
                        byte[] p = it2.next();
                        System.arraycopy(p, 0, payload, i, p.length);
                        i += p.length;
                        payloadLength2 = payloadLength2;
                    }
                    tnf = chunkTnf2;
                } else {
                    payload = payload3;
                }
                if (cf) {
                    inChunk = true;
                    payload2 = payload;
                    id3 = id2;
                    id4 = me;
                    byteBuffer = buffer;
                    byte[] bArr = type2;
                    chunkTnf3 = chunkTnf2;
                    type3 = bArr;
                } else {
                    inChunk = false;
                    String error = validateTnf(tnf, type2, id2, payload);
                    if (error != null) {
                        throw new FormatException(error);
                    }
                    records.add(new NdefRecord(tnf, type2, id2, payload));
                    if (ignoreMbMe) {
                        break;
                    }
                    payload2 = payload;
                    id3 = id2;
                    id4 = me;
                    byteBuffer = buffer;
                    byte[] bArr2 = type2;
                    chunkTnf3 = chunkTnf2;
                    type3 = bArr2;
                }
            }
            return (NdefRecord[]) records.toArray(new NdefRecord[records.size()]);
        } catch (BufferUnderflowException e) {
            throw new FormatException("expected more data", e);
        }
    }

    private static void ensureSanePayloadSize(long size) throws FormatException {
        if (size > 10485760) {
            throw new FormatException("payload above max limit: " + size + " > " + MAX_PAYLOAD_SIZE);
        }
    }

    static String validateTnf(short tnf, byte[] type, byte[] id, byte[] payload) {
        switch (tnf) {
            case 0:
                if (type.length == 0 && id.length == 0 && payload.length == 0) {
                    return null;
                }
                return "unexpected data in TNF_EMPTY record";
            case 1:
            case 2:
            case 3:
            case 4:
                return null;
            case 5:
            case 7:
                if (type.length == 0) {
                    return null;
                }
                return "unexpected type field in TNF_UNKNOWN or TNF_RESERVEd record";
            case 6:
                return "unexpected TNF_UNCHANGED in first chunk or logical record";
            default:
                return String.format("unexpected tnf value: 0x%02x", Short.valueOf(tnf));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writeToByteBuffer(ByteBuffer buffer, boolean mb, boolean me) {
        boolean il = true;
        boolean sr = this.mPayload.length < 256;
        short s = this.mTnf;
        if (s != 0 && this.mId.length <= 0) {
            il = false;
        }
        byte flags = (byte) (s | (il ? 8 : 0) | (mb ? -128 : 0) | (me ? 64 : 0) | (sr ? 16 : 0));
        buffer.put(flags);
        buffer.put((byte) this.mType.length);
        if (sr) {
            buffer.put((byte) this.mPayload.length);
        } else {
            buffer.putInt(this.mPayload.length);
        }
        if (il) {
            buffer.put((byte) this.mId.length);
        }
        buffer.put(this.mType);
        buffer.put(this.mId);
        buffer.put(this.mPayload);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getByteLength() {
        byte[] bArr = this.mId;
        int length = this.mType.length + 3 + bArr.length;
        byte[] bArr2 = this.mPayload;
        int length2 = length + bArr2.length;
        boolean sr = bArr2.length < 256;
        boolean il = this.mTnf == 0 || bArr.length > 0;
        if (!sr) {
            length2 += 3;
        }
        return il ? length2 + 1 : length2;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mTnf);
        dest.writeInt(this.mType.length);
        dest.writeByteArray(this.mType);
        dest.writeInt(this.mId.length);
        dest.writeByteArray(this.mId);
        dest.writeInt(this.mPayload.length);
        dest.writeByteArray(this.mPayload);
    }

    public int hashCode() {
        int result = (1 * 31) + Arrays.hashCode(this.mId);
        return (((((result * 31) + Arrays.hashCode(this.mPayload)) * 31) + this.mTnf) * 31) + Arrays.hashCode(this.mType);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        NdefRecord other = (NdefRecord) obj;
        if (!Arrays.equals(this.mId, other.mId) || !Arrays.equals(this.mPayload, other.mPayload) || this.mTnf != other.mTnf) {
            return false;
        }
        return Arrays.equals(this.mType, other.mType);
    }

    public String toString() {
        StringBuilder b = new StringBuilder(String.format("NdefRecord tnf=%X", Short.valueOf(this.mTnf)));
        if (this.mType.length > 0) {
            b.append(" type=").append((CharSequence) bytesToString(this.mType));
        }
        if (this.mId.length > 0) {
            b.append(" id=").append((CharSequence) bytesToString(this.mId));
        }
        if (this.mPayload.length > 0) {
            b.append(" payload=").append((CharSequence) bytesToString(this.mPayload));
        }
        return b.toString();
    }

    public void dumpDebug(ProtoOutputStream proto) {
        proto.write(1151051235329L, this.mType);
        proto.write(1151051235330L, this.mId);
        proto.write(1120986464259L, this.mPayload.length);
    }

    private static StringBuilder bytesToString(byte[] bs) {
        StringBuilder s = new StringBuilder();
        for (byte b : bs) {
            s.append(String.format("%02X", Byte.valueOf(b)));
        }
        return s;
    }
}
