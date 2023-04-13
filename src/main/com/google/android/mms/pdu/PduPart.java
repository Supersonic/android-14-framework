package com.google.android.mms.pdu;

import android.net.Uri;
import android.speech.RecognizerResultsIntent;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes5.dex */
public class PduPart {
    public static final String CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";
    public static final String P_7BIT = "7bit";
    public static final String P_8BIT = "8bit";
    public static final String P_BASE64 = "base64";
    public static final String P_BINARY = "binary";
    public static final int P_CHARSET = 129;
    public static final int P_COMMENT = 155;
    public static final int P_CONTENT_DISPOSITION = 197;
    public static final int P_CONTENT_ID = 192;
    public static final int P_CONTENT_LOCATION = 142;
    public static final int P_CONTENT_TRANSFER_ENCODING = 200;
    public static final int P_CONTENT_TYPE = 145;
    public static final int P_CREATION_DATE = 147;
    public static final int P_CT_MR_TYPE = 137;
    public static final int P_DEP_COMMENT = 140;
    public static final int P_DEP_CONTENT_DISPOSITION = 174;
    public static final int P_DEP_DOMAIN = 141;
    public static final int P_DEP_FILENAME = 134;
    public static final int P_DEP_NAME = 133;
    public static final int P_DEP_PATH = 143;
    public static final int P_DEP_START = 138;
    public static final int P_DEP_START_INFO = 139;
    public static final int P_DIFFERENCES = 135;
    public static final int P_DISPOSITION_ATTACHMENT = 129;
    public static final int P_DISPOSITION_FROM_DATA = 128;
    public static final int P_DISPOSITION_INLINE = 130;
    public static final int P_DOMAIN = 156;
    public static final int P_FILENAME = 152;
    public static final int P_LEVEL = 130;
    public static final int P_MAC = 146;
    public static final int P_MAX_AGE = 142;
    public static final int P_MODIFICATION_DATE = 148;
    public static final int P_NAME = 151;
    public static final int P_PADDING = 136;
    public static final int P_PATH = 157;
    public static final int P_Q = 128;
    public static final String P_QUOTED_PRINTABLE = "quoted-printable";
    public static final int P_READ_DATE = 149;
    public static final int P_SEC = 145;
    public static final int P_SECURE = 144;
    public static final int P_SIZE = 150;
    public static final int P_START = 153;
    public static final int P_START_INFO = 154;
    public static final int P_TYPE = 131;
    private static final String TAG = "PduPart";
    private Map<Integer, Object> mPartHeader;
    static final byte[] DISPOSITION_FROM_DATA = "from-data".getBytes();
    static final byte[] DISPOSITION_ATTACHMENT = "attachment".getBytes();
    static final byte[] DISPOSITION_INLINE = RecognizerResultsIntent.URI_SCHEME_INLINE.getBytes();
    private Uri mUri = null;
    private byte[] mPartData = null;

    public PduPart() {
        this.mPartHeader = null;
        this.mPartHeader = new HashMap();
    }

    public void setData(byte[] data) {
        if (data == null) {
            return;
        }
        byte[] bArr = new byte[data.length];
        this.mPartData = bArr;
        System.arraycopy(data, 0, bArr, 0, data.length);
    }

    public byte[] getData() {
        byte[] bArr = this.mPartData;
        if (bArr == null) {
            return null;
        }
        byte[] byteArray = new byte[bArr.length];
        System.arraycopy(bArr, 0, byteArray, 0, bArr.length);
        return byteArray;
    }

    public int getDataLength() {
        byte[] bArr = this.mPartData;
        if (bArr != null) {
            return bArr.length;
        }
        return 0;
    }

    public void setDataUri(Uri uri) {
        this.mUri = uri;
    }

    public Uri getDataUri() {
        return this.mUri;
    }

    public void setContentId(byte[] contentId) {
        if (contentId == null || contentId.length == 0) {
            throw new IllegalArgumentException("Content-Id may not be null or empty.");
        }
        if (contentId.length > 1 && ((char) contentId[0]) == '<' && ((char) contentId[contentId.length - 1]) == '>') {
            this.mPartHeader.put(192, contentId);
            return;
        }
        byte[] buffer = new byte[contentId.length + 2];
        buffer[0] = 60;
        buffer[buffer.length - 1] = 62;
        System.arraycopy(contentId, 0, buffer, 1, contentId.length);
        this.mPartHeader.put(192, buffer);
    }

    public byte[] getContentId() {
        return (byte[]) this.mPartHeader.get(192);
    }

    public void setCharset(int charset) {
        this.mPartHeader.put(129, Integer.valueOf(charset));
    }

    public int getCharset() {
        Integer charset = (Integer) this.mPartHeader.get(129);
        if (charset == null) {
            return 0;
        }
        return charset.intValue();
    }

    public void setContentLocation(byte[] contentLocation) {
        if (contentLocation == null) {
            throw new NullPointerException("null content-location");
        }
        this.mPartHeader.put(142, contentLocation);
    }

    public byte[] getContentLocation() {
        return (byte[]) this.mPartHeader.get(142);
    }

    public void setContentDisposition(byte[] contentDisposition) {
        if (contentDisposition == null) {
            throw new NullPointerException("null content-disposition");
        }
        this.mPartHeader.put(197, contentDisposition);
    }

    public byte[] getContentDisposition() {
        return (byte[]) this.mPartHeader.get(197);
    }

    public void setContentType(byte[] contentType) {
        if (contentType == null) {
            throw new NullPointerException("null content-type");
        }
        this.mPartHeader.put(145, contentType);
    }

    public byte[] getContentType() {
        return (byte[]) this.mPartHeader.get(145);
    }

    public void setContentTransferEncoding(byte[] contentTransferEncoding) {
        if (contentTransferEncoding == null) {
            throw new NullPointerException("null content-transfer-encoding");
        }
        this.mPartHeader.put(200, contentTransferEncoding);
    }

    public byte[] getContentTransferEncoding() {
        return (byte[]) this.mPartHeader.get(200);
    }

    public void setName(byte[] name) {
        if (name == null) {
            throw new NullPointerException("null content-id");
        }
        this.mPartHeader.put(151, name);
    }

    public byte[] getName() {
        return (byte[]) this.mPartHeader.get(151);
    }

    public void setFilename(byte[] fileName) {
        if (fileName == null) {
            throw new NullPointerException("null content-id");
        }
        this.mPartHeader.put(152, fileName);
    }

    public byte[] getFilename() {
        return (byte[]) this.mPartHeader.get(152);
    }

    public String generateLocation() {
        byte[] location = (byte[]) this.mPartHeader.get(151);
        if (location == null && (location = (byte[]) this.mPartHeader.get(152)) == null) {
            location = (byte[]) this.mPartHeader.get(142);
        }
        if (location == null) {
            byte[] contentId = (byte[]) this.mPartHeader.get(192);
            return "cid:" + new String(contentId);
        }
        return new String(location);
    }
}
