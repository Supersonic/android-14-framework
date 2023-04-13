package com.google.android.mms.pdu;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.drm.DrmManagerClient;
import android.net.Uri;
import android.p008os.ParcelFileDescriptor;
import android.provider.Telephony;
import android.telephony.PhoneNumberUtils;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.mms.ContentType;
import com.google.android.mms.InvalidHeaderValueException;
import com.google.android.mms.MmsException;
import com.google.android.mms.util.DownloadDrmHelper;
import com.google.android.mms.util.DrmConvertSession;
import com.google.android.mms.util.PduCache;
import com.google.android.mms.util.PduCacheEntry;
import com.google.android.mms.util.SqliteWrapper;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
/* loaded from: classes5.dex */
public class PduPersister {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final HashMap<Integer, Integer> CHARSET_COLUMN_INDEX_MAP;
    private static final HashMap<Integer, String> CHARSET_COLUMN_NAME_MAP;
    private static final boolean DEBUG = false;
    private static final HashMap<Integer, Integer> ENCODED_STRING_COLUMN_INDEX_MAP;
    private static final HashMap<Integer, String> ENCODED_STRING_COLUMN_NAME_MAP;
    private static final boolean LOCAL_LOGV = false;
    private static final HashMap<Integer, Integer> LONG_COLUMN_INDEX_MAP;
    private static final HashMap<Integer, String> LONG_COLUMN_NAME_MAP;
    private static final HashMap<Uri, Integer> MESSAGE_BOX_MAP;
    private static final HashMap<Integer, Integer> OCTET_COLUMN_INDEX_MAP;
    private static final HashMap<Integer, String> OCTET_COLUMN_NAME_MAP;
    private static final int PART_COLUMN_CHARSET = 1;
    private static final int PART_COLUMN_CONTENT_DISPOSITION = 2;
    private static final int PART_COLUMN_CONTENT_ID = 3;
    private static final int PART_COLUMN_CONTENT_LOCATION = 4;
    private static final int PART_COLUMN_CONTENT_TYPE = 5;
    private static final int PART_COLUMN_FILENAME = 6;
    private static final int PART_COLUMN_ID = 0;
    private static final int PART_COLUMN_NAME = 7;
    private static final int PART_COLUMN_TEXT = 8;
    private static final PduCache PDU_CACHE_INSTANCE;
    private static final int PDU_COLUMN_CONTENT_CLASS = 11;
    private static final int PDU_COLUMN_CONTENT_LOCATION = 5;
    private static final int PDU_COLUMN_CONTENT_TYPE = 6;
    private static final int PDU_COLUMN_DATE = 21;
    private static final int PDU_COLUMN_DELIVERY_REPORT = 12;
    private static final int PDU_COLUMN_DELIVERY_TIME = 22;
    private static final int PDU_COLUMN_EXPIRY = 23;
    private static final int PDU_COLUMN_ID = 0;
    private static final int PDU_COLUMN_MESSAGE_BOX = 1;
    private static final int PDU_COLUMN_MESSAGE_CLASS = 7;
    private static final int PDU_COLUMN_MESSAGE_ID = 8;
    private static final int PDU_COLUMN_MESSAGE_SIZE = 24;
    private static final int PDU_COLUMN_MESSAGE_TYPE = 13;
    private static final int PDU_COLUMN_MMS_VERSION = 14;
    private static final int PDU_COLUMN_PRIORITY = 15;
    private static final int PDU_COLUMN_READ_REPORT = 16;
    private static final int PDU_COLUMN_READ_STATUS = 17;
    private static final int PDU_COLUMN_REPORT_ALLOWED = 18;
    private static final int PDU_COLUMN_RESPONSE_TEXT = 9;
    private static final int PDU_COLUMN_RETRIEVE_STATUS = 19;
    private static final int PDU_COLUMN_RETRIEVE_TEXT = 3;
    private static final int PDU_COLUMN_RETRIEVE_TEXT_CHARSET = 26;
    private static final int PDU_COLUMN_STATUS = 20;
    private static final int PDU_COLUMN_SUBJECT = 4;
    private static final int PDU_COLUMN_SUBJECT_CHARSET = 25;
    private static final int PDU_COLUMN_THREAD_ID = 2;
    private static final int PDU_COLUMN_TRANSACTION_ID = 10;
    private static final long PLACEHOLDER_THREAD_ID = Long.MAX_VALUE;
    public static final int PROC_STATUS_COMPLETED = 3;
    public static final int PROC_STATUS_PERMANENTLY_FAILURE = 2;
    public static final int PROC_STATUS_TRANSIENT_FAILURE = 1;
    private static final String TAG = "PduPersister";
    public static final String TEMPORARY_DRM_OBJECT_URI = "content://mms/9223372036854775807/part";
    private static final HashMap<Integer, Integer> TEXT_STRING_COLUMN_INDEX_MAP;
    private static final HashMap<Integer, String> TEXT_STRING_COLUMN_NAME_MAP;
    private static PduPersister sPersister;
    private final ContentResolver mContentResolver;
    private final Context mContext;
    private final DrmManagerClient mDrmManagerClient;
    private static final int[] ADDRESS_FIELDS = {129, 130, 137, 151};
    private static final String[] PDU_PROJECTION = {"_id", Telephony.BaseMmsColumns.MESSAGE_BOX, "thread_id", Telephony.BaseMmsColumns.RETRIEVE_TEXT, Telephony.BaseMmsColumns.SUBJECT, Telephony.BaseMmsColumns.CONTENT_LOCATION, Telephony.BaseMmsColumns.CONTENT_TYPE, Telephony.BaseMmsColumns.MESSAGE_CLASS, Telephony.BaseMmsColumns.MESSAGE_ID, Telephony.BaseMmsColumns.RESPONSE_TEXT, Telephony.BaseMmsColumns.TRANSACTION_ID, Telephony.BaseMmsColumns.CONTENT_CLASS, Telephony.BaseMmsColumns.DELIVERY_REPORT, Telephony.BaseMmsColumns.MESSAGE_TYPE, "v", Telephony.BaseMmsColumns.PRIORITY, Telephony.BaseMmsColumns.READ_REPORT, Telephony.BaseMmsColumns.READ_STATUS, Telephony.BaseMmsColumns.REPORT_ALLOWED, Telephony.BaseMmsColumns.RETRIEVE_STATUS, Telephony.BaseMmsColumns.STATUS, "date", Telephony.BaseMmsColumns.DELIVERY_TIME, Telephony.BaseMmsColumns.EXPIRY, Telephony.BaseMmsColumns.MESSAGE_SIZE, Telephony.BaseMmsColumns.SUBJECT_CHARSET, Telephony.BaseMmsColumns.RETRIEVE_TEXT_CHARSET};
    private static final String[] PART_PROJECTION = {"_id", Telephony.Mms.Part.CHARSET, Telephony.Mms.Part.CONTENT_DISPOSITION, "cid", Telephony.Mms.Part.CONTENT_LOCATION, "ct", Telephony.Mms.Part.FILENAME, "name", "text"};

    static {
        HashMap<Uri, Integer> hashMap = new HashMap<>();
        MESSAGE_BOX_MAP = hashMap;
        hashMap.put(Telephony.Mms.Inbox.CONTENT_URI, 1);
        hashMap.put(Telephony.Mms.Sent.CONTENT_URI, 2);
        hashMap.put(Telephony.Mms.Draft.CONTENT_URI, 3);
        hashMap.put(Telephony.Mms.Outbox.CONTENT_URI, 4);
        HashMap<Integer, Integer> hashMap2 = new HashMap<>();
        CHARSET_COLUMN_INDEX_MAP = hashMap2;
        hashMap2.put(150, 25);
        hashMap2.put(154, 26);
        HashMap<Integer, String> hashMap3 = new HashMap<>();
        CHARSET_COLUMN_NAME_MAP = hashMap3;
        hashMap3.put(150, Telephony.BaseMmsColumns.SUBJECT_CHARSET);
        hashMap3.put(154, Telephony.BaseMmsColumns.RETRIEVE_TEXT_CHARSET);
        HashMap<Integer, Integer> hashMap4 = new HashMap<>();
        ENCODED_STRING_COLUMN_INDEX_MAP = hashMap4;
        hashMap4.put(154, 3);
        hashMap4.put(150, 4);
        HashMap<Integer, String> hashMap5 = new HashMap<>();
        ENCODED_STRING_COLUMN_NAME_MAP = hashMap5;
        hashMap5.put(154, Telephony.BaseMmsColumns.RETRIEVE_TEXT);
        hashMap5.put(150, Telephony.BaseMmsColumns.SUBJECT);
        HashMap<Integer, Integer> hashMap6 = new HashMap<>();
        TEXT_STRING_COLUMN_INDEX_MAP = hashMap6;
        hashMap6.put(131, 5);
        hashMap6.put(132, 6);
        hashMap6.put(138, 7);
        hashMap6.put(139, 8);
        hashMap6.put(147, 9);
        hashMap6.put(152, 10);
        HashMap<Integer, String> hashMap7 = new HashMap<>();
        TEXT_STRING_COLUMN_NAME_MAP = hashMap7;
        hashMap7.put(131, Telephony.BaseMmsColumns.CONTENT_LOCATION);
        hashMap7.put(132, Telephony.BaseMmsColumns.CONTENT_TYPE);
        hashMap7.put(138, Telephony.BaseMmsColumns.MESSAGE_CLASS);
        hashMap7.put(139, Telephony.BaseMmsColumns.MESSAGE_ID);
        hashMap7.put(147, Telephony.BaseMmsColumns.RESPONSE_TEXT);
        hashMap7.put(152, Telephony.BaseMmsColumns.TRANSACTION_ID);
        HashMap<Integer, Integer> hashMap8 = new HashMap<>();
        OCTET_COLUMN_INDEX_MAP = hashMap8;
        hashMap8.put(186, 11);
        hashMap8.put(134, 12);
        hashMap8.put(140, 13);
        hashMap8.put(141, 14);
        hashMap8.put(143, 15);
        hashMap8.put(144, 16);
        hashMap8.put(155, 17);
        hashMap8.put(145, 18);
        hashMap8.put(153, 19);
        hashMap8.put(149, 20);
        HashMap<Integer, String> hashMap9 = new HashMap<>();
        OCTET_COLUMN_NAME_MAP = hashMap9;
        hashMap9.put(186, Telephony.BaseMmsColumns.CONTENT_CLASS);
        hashMap9.put(134, Telephony.BaseMmsColumns.DELIVERY_REPORT);
        hashMap9.put(140, Telephony.BaseMmsColumns.MESSAGE_TYPE);
        hashMap9.put(141, "v");
        hashMap9.put(143, Telephony.BaseMmsColumns.PRIORITY);
        hashMap9.put(144, Telephony.BaseMmsColumns.READ_REPORT);
        hashMap9.put(155, Telephony.BaseMmsColumns.READ_STATUS);
        hashMap9.put(145, Telephony.BaseMmsColumns.REPORT_ALLOWED);
        hashMap9.put(153, Telephony.BaseMmsColumns.RETRIEVE_STATUS);
        hashMap9.put(149, Telephony.BaseMmsColumns.STATUS);
        HashMap<Integer, Integer> hashMap10 = new HashMap<>();
        LONG_COLUMN_INDEX_MAP = hashMap10;
        hashMap10.put(133, 21);
        hashMap10.put(135, 22);
        hashMap10.put(136, 23);
        hashMap10.put(142, 24);
        HashMap<Integer, String> hashMap11 = new HashMap<>();
        LONG_COLUMN_NAME_MAP = hashMap11;
        hashMap11.put(133, "date");
        hashMap11.put(135, Telephony.BaseMmsColumns.DELIVERY_TIME);
        hashMap11.put(136, Telephony.BaseMmsColumns.EXPIRY);
        hashMap11.put(142, Telephony.BaseMmsColumns.MESSAGE_SIZE);
        PDU_CACHE_INSTANCE = PduCache.getInstance();
    }

    private PduPersister(Context context) {
        this.mContext = context;
        this.mContentResolver = context.getContentResolver();
        this.mDrmManagerClient = new DrmManagerClient(context);
    }

    public static PduPersister getPduPersister(Context context) {
        PduPersister pduPersister = sPersister;
        if (pduPersister == null) {
            sPersister = new PduPersister(context);
        } else if (!context.equals(pduPersister.mContext)) {
            sPersister.release();
            sPersister = new PduPersister(context);
        }
        return sPersister;
    }

    private void setEncodedStringValueToHeaders(Cursor c, int columnIndex, PduHeaders headers, int mapColumn) {
        String s = c.getString(columnIndex);
        if (s != null && s.length() > 0) {
            int charsetColumnIndex = CHARSET_COLUMN_INDEX_MAP.get(Integer.valueOf(mapColumn)).intValue();
            int charset = c.getInt(charsetColumnIndex);
            EncodedStringValue value = new EncodedStringValue(charset, getBytes(s));
            headers.setEncodedStringValue(value, mapColumn);
        }
    }

    private void setTextStringToHeaders(Cursor c, int columnIndex, PduHeaders headers, int mapColumn) {
        String s = c.getString(columnIndex);
        if (s != null) {
            headers.setTextString(getBytes(s), mapColumn);
        }
    }

    private void setOctetToHeaders(Cursor c, int columnIndex, PduHeaders headers, int mapColumn) throws InvalidHeaderValueException {
        if (!c.isNull(columnIndex)) {
            int b = c.getInt(columnIndex);
            headers.setOctet(b, mapColumn);
        }
    }

    private void setLongToHeaders(Cursor c, int columnIndex, PduHeaders headers, int mapColumn) {
        if (!c.isNull(columnIndex)) {
            long l = c.getLong(columnIndex);
            headers.setLongInteger(l, mapColumn);
        }
    }

    private Integer getIntegerFromPartColumn(Cursor c, int columnIndex) {
        if (!c.isNull(columnIndex)) {
            return Integer.valueOf(c.getInt(columnIndex));
        }
        return null;
    }

    private byte[] getByteArrayFromPartColumn(Cursor c, int columnIndex) {
        if (!c.isNull(columnIndex)) {
            return getBytes(c.getString(columnIndex));
        }
        return null;
    }

    /* JADX WARN: Removed duplicated region for block: B:109:0x01a1 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private PduPart[] loadParts(long msgId) throws MmsException {
        ByteArrayOutputStream baos;
        IOException iOException;
        PduPersister pduPersister = this;
        Cursor c = SqliteWrapper.query(pduPersister.mContext, pduPersister.mContentResolver, Uri.parse("content://mms/" + msgId + "/part"), PART_PROJECTION, null, null, null);
        if (c != null) {
            try {
                if (c.getCount() != 0) {
                    int partCount = c.getCount();
                    PduPart[] parts = new PduPart[partCount];
                    int partIdx = 0;
                    while (c.moveToNext()) {
                        PduPart part = new PduPart();
                        Integer charset = pduPersister.getIntegerFromPartColumn(c, 1);
                        if (charset != null) {
                            part.setCharset(charset.intValue());
                        }
                        byte[] contentDisposition = pduPersister.getByteArrayFromPartColumn(c, 2);
                        if (contentDisposition != null) {
                            part.setContentDisposition(contentDisposition);
                        }
                        byte[] contentId = pduPersister.getByteArrayFromPartColumn(c, 3);
                        if (contentId != null) {
                            part.setContentId(contentId);
                        }
                        byte[] contentLocation = pduPersister.getByteArrayFromPartColumn(c, 4);
                        if (contentLocation != null) {
                            part.setContentLocation(contentLocation);
                        }
                        byte[] contentType = pduPersister.getByteArrayFromPartColumn(c, 5);
                        if (contentType == null) {
                            throw new MmsException("Content-Type must be set.");
                        }
                        part.setContentType(contentType);
                        byte[] fileName = pduPersister.getByteArrayFromPartColumn(c, 6);
                        int partCount2 = partCount;
                        if (fileName != null) {
                            part.setFilename(fileName);
                        }
                        byte[] name = pduPersister.getByteArrayFromPartColumn(c, 7);
                        if (name != null) {
                            part.setName(name);
                        }
                        long partId = c.getLong(0);
                        Uri partURI = Uri.parse("content://mms/part/" + partId);
                        part.setDataUri(partURI);
                        String type = toIsoString(contentType);
                        if (!ContentType.isImageType(type) && !ContentType.isAudioType(type) && !ContentType.isVideoType(type)) {
                            ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                            InputStream is = null;
                            String type2 = type;
                            if ("text/plain".equals(type2) || ContentType.APP_SMIL.equals(type2)) {
                                baos = baos2;
                            } else if ("text/html".equals(type2)) {
                                baos = baos2;
                            } else {
                                try {
                                    InputStream is2 = pduPersister.mContentResolver.openInputStream(partURI);
                                    try {
                                        byte[] buffer = new byte[256];
                                        int len = is2.read(buffer);
                                        while (len >= 0) {
                                            Uri partURI2 = partURI;
                                            ByteArrayOutputStream baos3 = baos2;
                                            String type3 = type2;
                                            try {
                                                baos3.write(buffer, 0, len);
                                                len = is2.read(buffer);
                                                type2 = type3;
                                                baos2 = baos3;
                                                partURI = partURI2;
                                            } catch (IOException e) {
                                                e = e;
                                                is = is2;
                                                try {
                                                    Log.m109e(TAG, "Failed to load part data", e);
                                                    c.close();
                                                    throw new MmsException(e);
                                                } catch (Throwable e2) {
                                                    iOException = e2;
                                                    if (is != null) {
                                                        try {
                                                            is.close();
                                                        } catch (IOException e3) {
                                                            Log.m109e(TAG, "Failed to close stream", e3);
                                                        }
                                                    }
                                                    throw iOException;
                                                }
                                            } catch (Throwable th) {
                                                iOException = th;
                                                is = is2;
                                                if (is != null) {
                                                }
                                                throw iOException;
                                            }
                                        }
                                        baos = baos2;
                                        if (is2 != null) {
                                            try {
                                                is2.close();
                                            } catch (IOException e4) {
                                                Log.m109e(TAG, "Failed to close stream", e4);
                                            }
                                        }
                                        part.setData(baos.toByteArray());
                                    } catch (IOException e5) {
                                        e = e5;
                                        is = is2;
                                    } catch (Throwable th2) {
                                        iOException = th2;
                                        is = is2;
                                    }
                                } catch (IOException e6) {
                                    e = e6;
                                } catch (Throwable th3) {
                                    iOException = th3;
                                }
                            }
                            String text = c.getString(8);
                            byte[] blob = new EncodedStringValue(text != null ? text : "").getTextString();
                            baos.write(blob, 0, blob.length);
                            part.setData(baos.toByteArray());
                        }
                        parts[partIdx] = part;
                        pduPersister = this;
                        partIdx++;
                        partCount = partCount2;
                    }
                    if (c != null) {
                        c.close();
                    }
                    return parts;
                }
            } finally {
                if (c != null) {
                    c.close();
                }
            }
        }
    }

    private void loadAddress(long msgId, PduHeaders headers) {
        Cursor c = SqliteWrapper.query(this.mContext, this.mContentResolver, Uri.parse("content://mms/" + msgId + "/addr"), new String[]{"address", Telephony.Mms.Addr.CHARSET, "type"}, null, null, null);
        if (c != null) {
            while (c.moveToNext()) {
                try {
                    String addr = c.getString(0);
                    if (!TextUtils.isEmpty(addr)) {
                        int addrType = c.getInt(2);
                        switch (addrType) {
                            case 129:
                            case 130:
                            case 151:
                                headers.appendEncodedStringValue(new EncodedStringValue(c.getInt(1), getBytes(addr)), addrType);
                                continue;
                            case 137:
                                headers.setEncodedStringValue(new EncodedStringValue(c.getInt(1), getBytes(addr)), addrType);
                                continue;
                            default:
                                Log.m110e(TAG, "Unknown address type: " + addrType);
                                continue;
                        }
                    }
                } finally {
                    c.close();
                }
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:125:0x01e7 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:69:0x018a A[Catch: all -> 0x0243, TryCatch #9 {all -> 0x0243, blocks: (B:29:0x0053, B:53:0x0149, B:56:0x0152, B:68:0x0187, B:69:0x018a, B:89:0x01fd, B:90:0x0217, B:70:0x018e, B:71:0x0195, B:72:0x019c, B:73:0x01a3, B:74:0x01aa, B:75:0x01b1, B:76:0x01b8, B:77:0x01bf, B:78:0x01db, B:79:0x01dc, B:62:0x016c, B:64:0x0172, B:66:0x017b, B:91:0x0218, B:92:0x0221, B:31:0x0071, B:33:0x0077, B:35:0x007d, B:36:0x0092, B:38:0x0098, B:39:0x00b7, B:40:0x00c2, B:42:0x00c8, B:43:0x00e7, B:44:0x00f2, B:46:0x00f8, B:47:0x0117, B:48:0x0122, B:50:0x0128, B:93:0x0222, B:94:0x023a), top: B:131:0x0053 }] */
    /* JADX WARN: Removed duplicated region for block: B:70:0x018e A[Catch: all -> 0x0243, TryCatch #9 {all -> 0x0243, blocks: (B:29:0x0053, B:53:0x0149, B:56:0x0152, B:68:0x0187, B:69:0x018a, B:89:0x01fd, B:90:0x0217, B:70:0x018e, B:71:0x0195, B:72:0x019c, B:73:0x01a3, B:74:0x01aa, B:75:0x01b1, B:76:0x01b8, B:77:0x01bf, B:78:0x01db, B:79:0x01dc, B:62:0x016c, B:64:0x0172, B:66:0x017b, B:91:0x0218, B:92:0x0221, B:31:0x0071, B:33:0x0077, B:35:0x007d, B:36:0x0092, B:38:0x0098, B:39:0x00b7, B:40:0x00c2, B:42:0x00c8, B:43:0x00e7, B:44:0x00f2, B:46:0x00f8, B:47:0x0117, B:48:0x0122, B:50:0x0128, B:93:0x0222, B:94:0x023a), top: B:131:0x0053 }] */
    /* JADX WARN: Removed duplicated region for block: B:71:0x0195 A[Catch: all -> 0x0243, TryCatch #9 {all -> 0x0243, blocks: (B:29:0x0053, B:53:0x0149, B:56:0x0152, B:68:0x0187, B:69:0x018a, B:89:0x01fd, B:90:0x0217, B:70:0x018e, B:71:0x0195, B:72:0x019c, B:73:0x01a3, B:74:0x01aa, B:75:0x01b1, B:76:0x01b8, B:77:0x01bf, B:78:0x01db, B:79:0x01dc, B:62:0x016c, B:64:0x0172, B:66:0x017b, B:91:0x0218, B:92:0x0221, B:31:0x0071, B:33:0x0077, B:35:0x007d, B:36:0x0092, B:38:0x0098, B:39:0x00b7, B:40:0x00c2, B:42:0x00c8, B:43:0x00e7, B:44:0x00f2, B:46:0x00f8, B:47:0x0117, B:48:0x0122, B:50:0x0128, B:93:0x0222, B:94:0x023a), top: B:131:0x0053 }] */
    /* JADX WARN: Removed duplicated region for block: B:72:0x019c A[Catch: all -> 0x0243, TryCatch #9 {all -> 0x0243, blocks: (B:29:0x0053, B:53:0x0149, B:56:0x0152, B:68:0x0187, B:69:0x018a, B:89:0x01fd, B:90:0x0217, B:70:0x018e, B:71:0x0195, B:72:0x019c, B:73:0x01a3, B:74:0x01aa, B:75:0x01b1, B:76:0x01b8, B:77:0x01bf, B:78:0x01db, B:79:0x01dc, B:62:0x016c, B:64:0x0172, B:66:0x017b, B:91:0x0218, B:92:0x0221, B:31:0x0071, B:33:0x0077, B:35:0x007d, B:36:0x0092, B:38:0x0098, B:39:0x00b7, B:40:0x00c2, B:42:0x00c8, B:43:0x00e7, B:44:0x00f2, B:46:0x00f8, B:47:0x0117, B:48:0x0122, B:50:0x0128, B:93:0x0222, B:94:0x023a), top: B:131:0x0053 }] */
    /* JADX WARN: Removed duplicated region for block: B:73:0x01a3 A[Catch: all -> 0x0243, TryCatch #9 {all -> 0x0243, blocks: (B:29:0x0053, B:53:0x0149, B:56:0x0152, B:68:0x0187, B:69:0x018a, B:89:0x01fd, B:90:0x0217, B:70:0x018e, B:71:0x0195, B:72:0x019c, B:73:0x01a3, B:74:0x01aa, B:75:0x01b1, B:76:0x01b8, B:77:0x01bf, B:78:0x01db, B:79:0x01dc, B:62:0x016c, B:64:0x0172, B:66:0x017b, B:91:0x0218, B:92:0x0221, B:31:0x0071, B:33:0x0077, B:35:0x007d, B:36:0x0092, B:38:0x0098, B:39:0x00b7, B:40:0x00c2, B:42:0x00c8, B:43:0x00e7, B:44:0x00f2, B:46:0x00f8, B:47:0x0117, B:48:0x0122, B:50:0x0128, B:93:0x0222, B:94:0x023a), top: B:131:0x0053 }] */
    /* JADX WARN: Removed duplicated region for block: B:74:0x01aa A[Catch: all -> 0x0243, TryCatch #9 {all -> 0x0243, blocks: (B:29:0x0053, B:53:0x0149, B:56:0x0152, B:68:0x0187, B:69:0x018a, B:89:0x01fd, B:90:0x0217, B:70:0x018e, B:71:0x0195, B:72:0x019c, B:73:0x01a3, B:74:0x01aa, B:75:0x01b1, B:76:0x01b8, B:77:0x01bf, B:78:0x01db, B:79:0x01dc, B:62:0x016c, B:64:0x0172, B:66:0x017b, B:91:0x0218, B:92:0x0221, B:31:0x0071, B:33:0x0077, B:35:0x007d, B:36:0x0092, B:38:0x0098, B:39:0x00b7, B:40:0x00c2, B:42:0x00c8, B:43:0x00e7, B:44:0x00f2, B:46:0x00f8, B:47:0x0117, B:48:0x0122, B:50:0x0128, B:93:0x0222, B:94:0x023a), top: B:131:0x0053 }] */
    /* JADX WARN: Removed duplicated region for block: B:75:0x01b1 A[Catch: all -> 0x0243, TryCatch #9 {all -> 0x0243, blocks: (B:29:0x0053, B:53:0x0149, B:56:0x0152, B:68:0x0187, B:69:0x018a, B:89:0x01fd, B:90:0x0217, B:70:0x018e, B:71:0x0195, B:72:0x019c, B:73:0x01a3, B:74:0x01aa, B:75:0x01b1, B:76:0x01b8, B:77:0x01bf, B:78:0x01db, B:79:0x01dc, B:62:0x016c, B:64:0x0172, B:66:0x017b, B:91:0x0218, B:92:0x0221, B:31:0x0071, B:33:0x0077, B:35:0x007d, B:36:0x0092, B:38:0x0098, B:39:0x00b7, B:40:0x00c2, B:42:0x00c8, B:43:0x00e7, B:44:0x00f2, B:46:0x00f8, B:47:0x0117, B:48:0x0122, B:50:0x0128, B:93:0x0222, B:94:0x023a), top: B:131:0x0053 }] */
    /* JADX WARN: Removed duplicated region for block: B:76:0x01b8 A[Catch: all -> 0x0243, TryCatch #9 {all -> 0x0243, blocks: (B:29:0x0053, B:53:0x0149, B:56:0x0152, B:68:0x0187, B:69:0x018a, B:89:0x01fd, B:90:0x0217, B:70:0x018e, B:71:0x0195, B:72:0x019c, B:73:0x01a3, B:74:0x01aa, B:75:0x01b1, B:76:0x01b8, B:77:0x01bf, B:78:0x01db, B:79:0x01dc, B:62:0x016c, B:64:0x0172, B:66:0x017b, B:91:0x0218, B:92:0x0221, B:31:0x0071, B:33:0x0077, B:35:0x007d, B:36:0x0092, B:38:0x0098, B:39:0x00b7, B:40:0x00c2, B:42:0x00c8, B:43:0x00e7, B:44:0x00f2, B:46:0x00f8, B:47:0x0117, B:48:0x0122, B:50:0x0128, B:93:0x0222, B:94:0x023a), top: B:131:0x0053 }] */
    /* JADX WARN: Removed duplicated region for block: B:77:0x01bf A[Catch: all -> 0x0243, TryCatch #9 {all -> 0x0243, blocks: (B:29:0x0053, B:53:0x0149, B:56:0x0152, B:68:0x0187, B:69:0x018a, B:89:0x01fd, B:90:0x0217, B:70:0x018e, B:71:0x0195, B:72:0x019c, B:73:0x01a3, B:74:0x01aa, B:75:0x01b1, B:76:0x01b8, B:77:0x01bf, B:78:0x01db, B:79:0x01dc, B:62:0x016c, B:64:0x0172, B:66:0x017b, B:91:0x0218, B:92:0x0221, B:31:0x0071, B:33:0x0077, B:35:0x007d, B:36:0x0092, B:38:0x0098, B:39:0x00b7, B:40:0x00c2, B:42:0x00c8, B:43:0x00e7, B:44:0x00f2, B:46:0x00f8, B:47:0x0117, B:48:0x0122, B:50:0x0128, B:93:0x0222, B:94:0x023a), top: B:131:0x0053 }] */
    /* JADX WARN: Removed duplicated region for block: B:79:0x01dc A[Catch: all -> 0x0243, TRY_LEAVE, TryCatch #9 {all -> 0x0243, blocks: (B:29:0x0053, B:53:0x0149, B:56:0x0152, B:68:0x0187, B:69:0x018a, B:89:0x01fd, B:90:0x0217, B:70:0x018e, B:71:0x0195, B:72:0x019c, B:73:0x01a3, B:74:0x01aa, B:75:0x01b1, B:76:0x01b8, B:77:0x01bf, B:78:0x01db, B:79:0x01dc, B:62:0x016c, B:64:0x0172, B:66:0x017b, B:91:0x0218, B:92:0x0221, B:31:0x0071, B:33:0x0077, B:35:0x007d, B:36:0x0092, B:38:0x0098, B:39:0x00b7, B:40:0x00c2, B:42:0x00c8, B:43:0x00e7, B:44:0x00f2, B:46:0x00f8, B:47:0x0117, B:48:0x0122, B:50:0x0128, B:93:0x0222, B:94:0x023a), top: B:131:0x0053 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public GenericPdu load(Uri uri) throws MmsException {
        PduCacheEntry cacheEntry;
        GenericPdu pdu;
        PduCache pduCache;
        try {
            PduCache pduCache2 = PDU_CACHE_INSTANCE;
            synchronized (pduCache2) {
                try {
                    if (pduCache2.isUpdating(uri)) {
                        try {
                            pduCache2.wait();
                        } catch (InterruptedException e) {
                            Log.m109e(TAG, "load: ", e);
                        }
                        PduCache pduCache3 = PDU_CACHE_INSTANCE;
                        PduCacheEntry cacheEntry2 = pduCache3.get(uri);
                        if (cacheEntry2 != null) {
                            GenericPdu pdu2 = cacheEntry2.getPdu();
                            synchronized (pduCache3) {
                                if (0 != 0) {
                                    pduCache3.put(uri, new PduCacheEntry(null, 0, -1L));
                                }
                                pduCache3.setUpdating(uri, false);
                                pduCache3.notifyAll();
                            }
                            return pdu2;
                        }
                        cacheEntry = cacheEntry2;
                    } else {
                        cacheEntry = null;
                    }
                    try {
                        PDU_CACHE_INSTANCE.setUpdating(uri, true);
                        try {
                            Cursor c = SqliteWrapper.query(this.mContext, this.mContentResolver, uri, PDU_PROJECTION, null, null, null);
                            PduHeaders headers = new PduHeaders();
                            long msgId = ContentUris.parseId(uri);
                            if (c == null || c.getCount() != 1 || !c.moveToFirst()) {
                                throw new MmsException("Bad uri: " + uri);
                            }
                            int msgBox = c.getInt(1);
                            long threadId = c.getLong(2);
                            Set<Map.Entry<Integer, Integer>> set = ENCODED_STRING_COLUMN_INDEX_MAP.entrySet();
                            for (Map.Entry<Integer, Integer> e2 : set) {
                                setEncodedStringValueToHeaders(c, e2.getValue().intValue(), headers, e2.getKey().intValue());
                            }
                            Set<Map.Entry<Integer, Integer>> set2 = TEXT_STRING_COLUMN_INDEX_MAP.entrySet();
                            for (Map.Entry<Integer, Integer> e3 : set2) {
                                setTextStringToHeaders(c, e3.getValue().intValue(), headers, e3.getKey().intValue());
                            }
                            Set<Map.Entry<Integer, Integer>> set3 = OCTET_COLUMN_INDEX_MAP.entrySet();
                            for (Map.Entry<Integer, Integer> e4 : set3) {
                                setOctetToHeaders(c, e4.getValue().intValue(), headers, e4.getKey().intValue());
                            }
                            Set<Map.Entry<Integer, Integer>> set4 = LONG_COLUMN_INDEX_MAP.entrySet();
                            for (Map.Entry<Integer, Integer> e5 : set4) {
                                setLongToHeaders(c, e5.getValue().intValue(), headers, e5.getKey().intValue());
                            }
                            if (c != null) {
                                c.close();
                            }
                            if (msgId == -1) {
                                throw new MmsException("Error! ID of the message: -1.");
                            }
                            loadAddress(msgId, headers);
                            int msgType = headers.getOctet(140);
                            PduBody body = new PduBody();
                            if (msgType != 132 && msgType != 128) {
                                switch (msgType) {
                                    case 128:
                                        GenericPdu pdu3 = new SendReq(headers, body);
                                        pdu = pdu3;
                                        break;
                                    case 129:
                                    case 137:
                                    case 138:
                                    case 139:
                                    case 140:
                                    case 141:
                                    case 142:
                                    case 143:
                                    case 144:
                                    case 145:
                                    case 146:
                                    case 147:
                                    case 148:
                                    case 149:
                                    case 150:
                                    case 151:
                                        throw new MmsException("Unsupported PDU type: " + Integer.toHexString(msgType));
                                    case 130:
                                        GenericPdu pdu4 = new NotificationInd(headers);
                                        pdu = pdu4;
                                        break;
                                    case 131:
                                        GenericPdu pdu5 = new NotifyRespInd(headers);
                                        pdu = pdu5;
                                        break;
                                    case 132:
                                        GenericPdu pdu6 = new RetrieveConf(headers, body);
                                        pdu = pdu6;
                                        break;
                                    case 133:
                                        GenericPdu pdu7 = new AcknowledgeInd(headers);
                                        pdu = pdu7;
                                        break;
                                    case 134:
                                        GenericPdu pdu8 = new DeliveryInd(headers);
                                        pdu = pdu8;
                                        break;
                                    case 135:
                                        GenericPdu pdu9 = new ReadRecInd(headers);
                                        pdu = pdu9;
                                        break;
                                    case 136:
                                        GenericPdu pdu10 = new ReadOrigInd(headers);
                                        pdu = pdu10;
                                        break;
                                    default:
                                        throw new MmsException("Unrecognized PDU type: " + Integer.toHexString(msgType));
                                }
                                pduCache = PDU_CACHE_INSTANCE;
                                synchronized (pduCache) {
                                    pduCache.put(uri, new PduCacheEntry(pdu, msgBox, threadId));
                                    pduCache.setUpdating(uri, false);
                                    pduCache.notifyAll();
                                }
                                return pdu;
                            }
                            PduPart[] parts = loadParts(msgId);
                            if (parts != null) {
                                for (PduPart pduPart : parts) {
                                    body.addPart(pduPart);
                                }
                            }
                            switch (msgType) {
                            }
                            pduCache = PDU_CACHE_INSTANCE;
                            synchronized (pduCache) {
                            }
                        } catch (Throwable th) {
                            th = th;
                            PduCache pduCache4 = PDU_CACHE_INSTANCE;
                            synchronized (pduCache4) {
                                if (0 != 0) {
                                    pduCache4.put(uri, new PduCacheEntry(null, 0, -1L));
                                }
                                pduCache4.setUpdating(uri, false);
                                pduCache4.notifyAll();
                            }
                            throw th;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                }
            }
        } catch (Throwable th4) {
            th = th4;
        }
    }

    private void persistAddress(long msgId, int type, EncodedStringValue[] array) {
        ContentValues values = new ContentValues(3);
        for (EncodedStringValue addr : array) {
            values.clear();
            values.put("address", toIsoString(addr.getTextString()));
            values.put(Telephony.Mms.Addr.CHARSET, Integer.valueOf(addr.getCharacterSet()));
            values.put("type", Integer.valueOf(type));
            Uri uri = Uri.parse("content://mms/" + msgId + "/addr");
            SqliteWrapper.insert(this.mContext, this.mContentResolver, uri, values);
        }
    }

    private static String getPartContentType(PduPart part) {
        if (part.getContentType() == null) {
            return null;
        }
        return toIsoString(part.getContentType());
    }

    public Uri persistPart(PduPart part, long msgId, HashMap<Uri, InputStream> preOpenedFiles) throws MmsException {
        Uri uri = Uri.parse("content://mms/" + msgId + "/part");
        ContentValues values = new ContentValues(8);
        int charset = part.getCharset();
        if (charset != 0) {
            values.put(Telephony.Mms.Part.CHARSET, Integer.valueOf(charset));
        }
        String contentType = getPartContentType(part);
        if (contentType != null) {
            if (ContentType.IMAGE_JPG.equals(contentType)) {
                contentType = ContentType.IMAGE_JPEG;
            }
            values.put("ct", contentType);
            if (ContentType.APP_SMIL.equals(contentType)) {
                values.put("seq", (Integer) (-1));
            }
            if (part.getFilename() != null) {
                String fileName = new String(part.getFilename());
                values.put(Telephony.Mms.Part.FILENAME, fileName);
            }
            if (part.getName() != null) {
                String name = new String(part.getName());
                values.put("name", name);
            }
            if (part.getContentDisposition() != null) {
                Object value = toIsoString(part.getContentDisposition());
                values.put(Telephony.Mms.Part.CONTENT_DISPOSITION, (String) value);
            }
            if (part.getContentId() != null) {
                Object value2 = toIsoString(part.getContentId());
                values.put("cid", (String) value2);
            }
            if (part.getContentLocation() != null) {
                Object value3 = toIsoString(part.getContentLocation());
                values.put(Telephony.Mms.Part.CONTENT_LOCATION, (String) value3);
            }
            Uri res = SqliteWrapper.insert(this.mContext, this.mContentResolver, uri, values);
            if (res == null) {
                throw new MmsException("Failed to persist part, return null.");
            }
            persistData(part, res, contentType, preOpenedFiles);
            part.setDataUri(res);
            return res;
        }
        throw new MmsException("MIME type of the part must be set.");
    }

    /* JADX WARN: Code restructure failed: missing block: B:98:0x01bc, code lost:
        r17 = r9;
     */
    /* JADX WARN: Code restructure failed: missing block: B:99:0x01c5, code lost:
        throw new com.google.android.mms.MmsException("Error converting drm data.");
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Not initialized variable reg: 16, insn: 0x0368: MOVE  (r11 I:??[OBJECT, ARRAY]) = (r16 I:??[OBJECT, ARRAY] A[D('dataUri' android.net.Uri)]), block:B:171:0x0367 */
    /* JADX WARN: Not initialized variable reg: 16, insn: 0x036e: MOVE  (r11 I:??[OBJECT, ARRAY]) = (r16 I:??[OBJECT, ARRAY] A[D('dataUri' android.net.Uri)]), block:B:173:0x036e */
    /* JADX WARN: Not initialized variable reg: 16, insn: 0x0374: MOVE  (r11 I:??[OBJECT, ARRAY]) = (r16 I:??[OBJECT, ARRAY] A[D('dataUri' android.net.Uri)]), block:B:175:0x0374 */
    /* JADX WARN: Not initialized variable reg: 17, insn: 0x036a: MOVE  (r8 I:??[OBJECT, ARRAY]) = (r17 I:??[OBJECT, ARRAY] A[D('os' java.io.OutputStream)]), block:B:171:0x0367 */
    /* JADX WARN: Not initialized variable reg: 17, insn: 0x0370: MOVE  (r8 I:??[OBJECT, ARRAY]) = (r17 I:??[OBJECT, ARRAY] A[D('os' java.io.OutputStream)]), block:B:173:0x036e */
    /* JADX WARN: Not initialized variable reg: 17, insn: 0x0376: MOVE  (r8 I:??[OBJECT, ARRAY]) = (r17 I:??[OBJECT, ARRAY] A[D('os' java.io.OutputStream)]), block:B:175:0x0374 */
    /* JADX WARN: Removed duplicated region for block: B:166:0x030f  */
    /* JADX WARN: Removed duplicated region for block: B:199:0x03de  */
    /* JADX WARN: Removed duplicated region for block: B:204:0x02f2 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:210:0x03c1 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:216:0x02d5 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:226:0x03a4 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:245:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r16v18 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void persistData(PduPart part, Uri uri, String contentType, HashMap<Uri, InputStream> preOpenedFiles) throws MmsException {
        FileNotFoundException fileNotFoundException;
        OutputStream os;
        OutputStream os2;
        Uri dataUri;
        Object obj;
        InputStream is;
        OutputStream os3 = null;
        InputStream is2 = null;
        DrmConvertSession drmConvertSession = null;
        try {
            try {
                try {
                    byte[] data = part.getData();
                    try {
                        try {
                            if ("text/plain".equals(contentType)) {
                                os2 = null;
                                dataUri = null;
                            } else if (ContentType.APP_SMIL.equals(contentType)) {
                                os2 = null;
                                dataUri = null;
                            } else if ("text/html".equals(contentType)) {
                                os2 = null;
                                dataUri = null;
                            } else {
                                boolean isDrm = DownloadDrmHelper.isDrmConvertNeeded(contentType);
                                if (isDrm) {
                                    if (uri != null) {
                                        try {
                                            ParcelFileDescriptor pfd = this.mContentResolver.openFileDescriptor(uri, "r");
                                            try {
                                                if (pfd.getStatSize() > 0) {
                                                    if (pfd != null) {
                                                        try {
                                                            pfd.close();
                                                        } catch (Exception e) {
                                                            e = e;
                                                            obj = null;
                                                        }
                                                    }
                                                    if (0 != 0) {
                                                        try {
                                                            os3.close();
                                                        } catch (IOException e2) {
                                                            Log.m109e(TAG, "IOException while closing: " + ((Object) null), e2);
                                                        }
                                                    }
                                                    if (0 != 0) {
                                                        try {
                                                            is2.close();
                                                        } catch (IOException e3) {
                                                            Log.m109e(TAG, "IOException while closing: " + ((Object) null), e3);
                                                        }
                                                    }
                                                    if (0 != 0) {
                                                        drmConvertSession.close(null);
                                                        File f = new File((String) null);
                                                        ContentValues values = new ContentValues(0);
                                                        SqliteWrapper.update(this.mContext, this.mContentResolver, Uri.parse("content://mms/resetFilePerm/" + f.getName()), values, null, null);
                                                        return;
                                                    }
                                                    return;
                                                }
                                                obj = null;
                                                if (pfd != null) {
                                                    try {
                                                        try {
                                                            pfd.close();
                                                        } catch (Throwable th) {
                                                            fileNotFoundException = th;
                                                            if (os3 != null) {
                                                                try {
                                                                    os3.close();
                                                                } catch (IOException e4) {
                                                                    Log.m109e(TAG, "IOException while closing: " + os3, e4);
                                                                }
                                                            }
                                                            if (is2 != null) {
                                                                try {
                                                                    is2.close();
                                                                } catch (IOException e5) {
                                                                    Log.m109e(TAG, "IOException while closing: " + is2, e5);
                                                                }
                                                            }
                                                            if (drmConvertSession != null) {
                                                                drmConvertSession.close(null);
                                                                File f2 = new File((String) null);
                                                                ContentValues values2 = new ContentValues(0);
                                                                SqliteWrapper.update(this.mContext, this.mContentResolver, Uri.parse("content://mms/resetFilePerm/" + f2.getName()), values2, null, null);
                                                            }
                                                            throw fileNotFoundException;
                                                        }
                                                    } catch (Exception e6) {
                                                        e = e6;
                                                    }
                                                }
                                            } catch (Throwable th2) {
                                                obj = null;
                                                if (pfd != null) {
                                                    try {
                                                        pfd.close();
                                                    } catch (Throwable th3) {
                                                        try {
                                                            th2.addSuppressed(th3);
                                                        } catch (Exception e7) {
                                                            e = e7;
                                                        }
                                                    }
                                                }
                                                throw th2;
                                            }
                                        } catch (Exception e8) {
                                            e = e8;
                                            obj = null;
                                        }
                                        Log.m109e(TAG, "Can't get file info for: " + part.getDataUri(), e);
                                    } else {
                                        obj = null;
                                    }
                                    drmConvertSession = DrmConvertSession.open(this.mContext, contentType);
                                    if (drmConvertSession == null) {
                                        throw new MmsException("Mimetype " + contentType + " can not be converted.");
                                    }
                                } else {
                                    obj = null;
                                }
                                os3 = this.mContentResolver.openOutputStream(uri);
                                try {
                                    try {
                                        if (data == null) {
                                            Uri dataUri2 = part.getDataUri();
                                            if (dataUri2 != null && !dataUri2.equals(uri)) {
                                                if (preOpenedFiles != null && preOpenedFiles.containsKey(dataUri2)) {
                                                    is2 = preOpenedFiles.get(dataUri2);
                                                }
                                                if (is2 == null) {
                                                    is2 = this.mContentResolver.openInputStream(dataUri2);
                                                }
                                                try {
                                                    byte[] buffer = new byte[8192];
                                                    while (true) {
                                                        int len = is2.read(buffer);
                                                        if (len == -1) {
                                                            obj = len;
                                                            break;
                                                        }
                                                        if (isDrm) {
                                                            byte[] convertedData = drmConvertSession.convert(buffer, len);
                                                            if (convertedData == null) {
                                                                break;
                                                            }
                                                            is = is2;
                                                            try {
                                                                os3.write(convertedData, 0, convertedData.length);
                                                            } catch (FileNotFoundException e9) {
                                                                e = e9;
                                                                Log.m109e(TAG, "Failed to open Input/Output stream.", e);
                                                                throw new MmsException(e);
                                                            } catch (IOException e10) {
                                                                e = e10;
                                                                Log.m109e(TAG, "Failed to read/write data.", e);
                                                                throw new MmsException(e);
                                                            } catch (Throwable th4) {
                                                                fileNotFoundException = th4;
                                                                is2 = is;
                                                                if (os3 != null) {
                                                                }
                                                                if (is2 != null) {
                                                                }
                                                                if (drmConvertSession != null) {
                                                                }
                                                                throw fileNotFoundException;
                                                            }
                                                        } else {
                                                            os3.write(buffer, 0, len);
                                                            is = is2;
                                                        }
                                                        is2 = is;
                                                    }
                                                } catch (FileNotFoundException e11) {
                                                    e = e11;
                                                } catch (IOException e12) {
                                                    e = e12;
                                                } catch (Throwable th5) {
                                                    fileNotFoundException = th5;
                                                }
                                            }
                                            Log.m104w(TAG, "Can't find data for this part.");
                                            if (os3 != null) {
                                                try {
                                                    os3.close();
                                                } catch (IOException e13) {
                                                    Log.m109e(TAG, "IOException while closing: " + os3, e13);
                                                }
                                            }
                                            if (0 != 0) {
                                                try {
                                                    is2.close();
                                                } catch (IOException e14) {
                                                    Log.m109e(TAG, "IOException while closing: " + ((Object) null), e14);
                                                }
                                            }
                                            if (drmConvertSession != null) {
                                                drmConvertSession.close(null);
                                                File f3 = new File((String) null);
                                                ContentValues values3 = new ContentValues(0);
                                                SqliteWrapper.update(this.mContext, this.mContentResolver, Uri.parse("content://mms/resetFilePerm/" + f3.getName()), values3, null, null);
                                                return;
                                            }
                                            return;
                                        } else if (isDrm) {
                                            byte[] convertedData2 = drmConvertSession.convert(data, data.length);
                                            if (convertedData2 == null) {
                                                throw new MmsException("Error converting drm data.");
                                            }
                                            os3.write(convertedData2, 0, convertedData2.length);
                                            if (os3 != null) {
                                                try {
                                                    os3.close();
                                                } catch (IOException e15) {
                                                    Log.m109e(TAG, "IOException while closing: " + os3, e15);
                                                }
                                            }
                                            if (is2 != null) {
                                                try {
                                                    is2.close();
                                                } catch (IOException e16) {
                                                    Log.m109e(TAG, "IOException while closing: " + is2, e16);
                                                }
                                            }
                                            if (drmConvertSession != null) {
                                                drmConvertSession.close(null);
                                                File f4 = new File((String) null);
                                                ContentValues values4 = new ContentValues(0);
                                                SqliteWrapper.update(this.mContext, this.mContentResolver, Uri.parse("content://mms/resetFilePerm/" + f4.getName()), values4, null, null);
                                                return;
                                            }
                                            return;
                                        } else {
                                            os3.write(data);
                                            obj = obj;
                                        }
                                        if (os3 != null) {
                                        }
                                        if (is2 != null) {
                                        }
                                        if (drmConvertSession != null) {
                                        }
                                    } catch (FileNotFoundException e17) {
                                        e = e17;
                                    } catch (IOException e18) {
                                        e = e18;
                                    }
                                } catch (FileNotFoundException e19) {
                                    e = e19;
                                } catch (IOException e20) {
                                    e = e20;
                                }
                            }
                            ContentValues cv = new ContentValues();
                            if (data == null) {
                                data = new String("").getBytes("utf-8");
                            }
                            cv.put("text", new EncodedStringValue(data).getString());
                            if (this.mContentResolver.update(uri, cv, null, null) != 1) {
                                throw new MmsException("unable to update " + uri.toString());
                            }
                            os3 = os2;
                            if (os3 != null) {
                            }
                            if (is2 != null) {
                            }
                            if (drmConvertSession != null) {
                            }
                        } catch (Throwable th6) {
                            fileNotFoundException = th6;
                            os3 = os;
                        }
                    } catch (FileNotFoundException e21) {
                        e = e21;
                    } catch (IOException e22) {
                        e = e22;
                    }
                } catch (Throwable e23) {
                    fileNotFoundException = e23;
                }
            } catch (Throwable th7) {
                fileNotFoundException = th7;
            }
        } catch (FileNotFoundException e24) {
            e = e24;
        } catch (IOException e25) {
            e = e25;
        }
    }

    private void updateAddress(long msgId, int type, EncodedStringValue[] array) {
        SqliteWrapper.delete(this.mContext, this.mContentResolver, Uri.parse("content://mms/" + msgId + "/addr"), "type=" + type, null);
        persistAddress(msgId, type, array);
    }

    public void updateHeaders(Uri uri, SendReq sendReq) {
        int i;
        int i2;
        EncodedStringValue[] array;
        PduHeaders headers;
        EncodedStringValue[] array2;
        PduCache pduCache = PDU_CACHE_INSTANCE;
        synchronized (pduCache) {
            if (pduCache.isUpdating(uri)) {
                try {
                    pduCache.wait();
                } catch (InterruptedException e) {
                    Log.m109e(TAG, "updateHeaders: ", e);
                }
            }
        }
        PDU_CACHE_INSTANCE.purge(uri);
        ContentValues values = new ContentValues(10);
        byte[] contentType = sendReq.getContentType();
        if (contentType != null) {
            values.put(Telephony.BaseMmsColumns.CONTENT_TYPE, toIsoString(contentType));
        }
        long date = sendReq.getDate();
        if (date != -1) {
            values.put("date", Long.valueOf(date));
        }
        int deliveryReport = sendReq.getDeliveryReport();
        if (deliveryReport != 0) {
            values.put(Telephony.BaseMmsColumns.DELIVERY_REPORT, Integer.valueOf(deliveryReport));
        }
        long expiry = sendReq.getExpiry();
        if (expiry != -1) {
            values.put(Telephony.BaseMmsColumns.EXPIRY, Long.valueOf(expiry));
        }
        byte[] msgClass = sendReq.getMessageClass();
        if (msgClass != null) {
            values.put(Telephony.BaseMmsColumns.MESSAGE_CLASS, toIsoString(msgClass));
        }
        int priority = sendReq.getPriority();
        if (priority != 0) {
            values.put(Telephony.BaseMmsColumns.PRIORITY, Integer.valueOf(priority));
        }
        int readReport = sendReq.getReadReport();
        if (readReport != 0) {
            values.put(Telephony.BaseMmsColumns.READ_REPORT, Integer.valueOf(readReport));
        }
        byte[] transId = sendReq.getTransactionId();
        if (transId != null) {
            values.put(Telephony.BaseMmsColumns.TRANSACTION_ID, toIsoString(transId));
        }
        EncodedStringValue subject = sendReq.getSubject();
        if (subject != null) {
            values.put(Telephony.BaseMmsColumns.SUBJECT, toIsoString(subject.getTextString()));
            values.put(Telephony.BaseMmsColumns.SUBJECT_CHARSET, Integer.valueOf(subject.getCharacterSet()));
        } else {
            values.put(Telephony.BaseMmsColumns.SUBJECT, "");
        }
        long messageSize = sendReq.getMessageSize();
        if (messageSize > 0) {
            values.put(Telephony.BaseMmsColumns.MESSAGE_SIZE, Long.valueOf(messageSize));
        }
        PduHeaders headers2 = sendReq.getPduHeaders();
        HashSet<String> recipients = new HashSet<>();
        int[] iArr = ADDRESS_FIELDS;
        int length = iArr.length;
        int i3 = 0;
        while (i3 < length) {
            int addrType = iArr[i3];
            EncodedStringValue[] array3 = null;
            int[] iArr2 = iArr;
            if (addrType == 137) {
                EncodedStringValue v = headers2.getEncodedStringValue(addrType);
                if (v == null) {
                    i = length;
                    i2 = 0;
                } else {
                    i = length;
                    i2 = 0;
                    EncodedStringValue[] array4 = {v};
                    array3 = array4;
                }
                array = array3;
            } else {
                i = length;
                i2 = 0;
                EncodedStringValue[] array5 = headers2.getEncodedStringValues(addrType);
                array = array5;
            }
            if (array != null) {
                headers = headers2;
                long msgId = ContentUris.parseId(uri);
                updateAddress(msgId, addrType, array);
                if (addrType == 151) {
                    int length2 = array.length;
                    int addrType2 = i2;
                    while (addrType2 < length2) {
                        EncodedStringValue v2 = array[addrType2];
                        if (v2 == null) {
                            array2 = array;
                        } else {
                            array2 = array;
                            recipients.add(v2.getString());
                        }
                        addrType2++;
                        array = array2;
                    }
                }
            } else {
                headers = headers2;
            }
            i3++;
            headers2 = headers;
            iArr = iArr2;
            length = i;
        }
        if (!recipients.isEmpty()) {
            long threadId = Telephony.Threads.getOrCreateThreadId(this.mContext, recipients);
            values.put("thread_id", Long.valueOf(threadId));
        }
        SqliteWrapper.update(this.mContext, this.mContentResolver, uri, values, null, null);
    }

    private void updatePart(Uri uri, PduPart part, HashMap<Uri, InputStream> preOpenedFiles) throws MmsException {
        ContentValues values = new ContentValues(7);
        int charset = part.getCharset();
        if (charset != 0) {
            values.put(Telephony.Mms.Part.CHARSET, Integer.valueOf(charset));
        }
        if (part.getContentType() != null) {
            String contentType = toIsoString(part.getContentType());
            values.put("ct", contentType);
            if (part.getFilename() != null) {
                String fileName = new String(part.getFilename());
                values.put(Telephony.Mms.Part.FILENAME, fileName);
            }
            if (part.getName() != null) {
                String name = new String(part.getName());
                values.put("name", name);
            }
            String name2 = null;
            if (part.getContentDisposition() != null) {
                name2 = toIsoString(part.getContentDisposition());
                values.put(Telephony.Mms.Part.CONTENT_DISPOSITION, name2);
            }
            if (part.getContentId() != null) {
                name2 = toIsoString(part.getContentId());
                values.put("cid", name2);
            }
            if (part.getContentLocation() != null) {
                Object value = toIsoString(part.getContentLocation());
                values.put(Telephony.Mms.Part.CONTENT_LOCATION, (String) value);
            }
            SqliteWrapper.update(this.mContext, this.mContentResolver, uri, values, null, null);
            if (part.getData() != null || !uri.equals(part.getDataUri())) {
                persistData(part, uri, contentType, preOpenedFiles);
                return;
            }
            return;
        }
        throw new MmsException("MIME type of the part must be set.");
    }

    public void updateParts(Uri uri, PduBody body, HashMap<Uri, InputStream> preOpenedFiles) throws MmsException {
        try {
            PduCache pduCache = PDU_CACHE_INSTANCE;
            synchronized (pduCache) {
                if (pduCache.isUpdating(uri)) {
                    try {
                        pduCache.wait();
                    } catch (InterruptedException e) {
                        Log.m109e(TAG, "updateParts: ", e);
                    }
                    PduCacheEntry cacheEntry = PDU_CACHE_INSTANCE.get(uri);
                    if (cacheEntry != null) {
                        ((MultimediaMessagePdu) cacheEntry.getPdu()).setBody(body);
                    }
                }
                PDU_CACHE_INSTANCE.setUpdating(uri, true);
            }
            ArrayList<PduPart> toBeCreated = new ArrayList<>();
            HashMap<Uri, PduPart> toBeUpdated = new HashMap<>();
            int partsNum = body.getPartsNum();
            StringBuilder filter = new StringBuilder().append('(');
            for (int i = 0; i < partsNum; i++) {
                PduPart part = body.getPart(i);
                Uri partUri = part.getDataUri();
                if (partUri != null && !TextUtils.isEmpty(partUri.getAuthority()) && partUri.getAuthority().startsWith("mms")) {
                    toBeUpdated.put(partUri, part);
                    if (filter.length() > 1) {
                        filter.append(" AND ");
                    }
                    filter.append("_id");
                    filter.append("!=");
                    DatabaseUtils.appendEscapedSQLString(filter, partUri.getLastPathSegment());
                }
                toBeCreated.add(part);
            }
            filter.append(')');
            long msgId = ContentUris.parseId(uri);
            SqliteWrapper.delete(this.mContext, this.mContentResolver, Uri.parse(Telephony.Mms.CONTENT_URI + "/" + msgId + "/part"), filter.length() > 2 ? filter.toString() : null, null);
            Iterator<PduPart> it = toBeCreated.iterator();
            while (it.hasNext()) {
                persistPart(it.next(), msgId, preOpenedFiles);
            }
            for (Map.Entry<Uri, PduPart> e2 : toBeUpdated.entrySet()) {
                updatePart(e2.getKey(), e2.getValue(), preOpenedFiles);
            }
            PduCache pduCache2 = PDU_CACHE_INSTANCE;
            synchronized (pduCache2) {
                pduCache2.setUpdating(uri, false);
                pduCache2.notifyAll();
            }
        } catch (Throwable th) {
            PduCache pduCache3 = PDU_CACHE_INSTANCE;
            synchronized (pduCache3) {
                pduCache3.setUpdating(uri, false);
                pduCache3.notifyAll();
                throw th;
            }
        }
    }

    public Uri persist(GenericPdu pdu, Uri uri, boolean createThreadId, boolean groupMmsEnabled, HashMap<Uri, InputStream> preOpenedFiles) throws MmsException {
        long msgId;
        int messageSize;
        int i;
        int i2;
        long placeholderId;
        Uri uri2;
        Uri res;
        long msgId2;
        int i3;
        int[] iArr;
        if (uri == null) {
            throw new MmsException("Uri may not be null.");
        }
        try {
            long msgId3 = ContentUris.parseId(uri);
            msgId = msgId3;
        } catch (NumberFormatException e) {
            msgId = -1;
        }
        boolean existingUri = msgId != -1;
        if (!existingUri && MESSAGE_BOX_MAP.get(uri) == null) {
            throw new MmsException("Bad destination, must be one of content://mms/inbox, content://mms/sent, content://mms/drafts, content://mms/outbox, content://mms/temp.");
        }
        PduCache pduCache = PDU_CACHE_INSTANCE;
        synchronized (pduCache) {
            try {
                if (pduCache.isUpdating(uri)) {
                    try {
                        try {
                            pduCache.wait();
                        } catch (InterruptedException e2) {
                            Log.m109e(TAG, "persist1: ", e2);
                        }
                    } catch (Throwable th) {
                        th = th;
                        while (true) {
                            try {
                                break;
                            } catch (Throwable th2) {
                                th = th2;
                            }
                        }
                        throw th;
                    }
                }
                PDU_CACHE_INSTANCE.purge(uri);
                PduHeaders header = pdu.getPduHeaders();
                ContentValues values = new ContentValues();
                Set<Map.Entry<Integer, String>> set = ENCODED_STRING_COLUMN_NAME_MAP.entrySet();
                for (Map.Entry<Integer, String> e3 : set) {
                    int field = e3.getKey().intValue();
                    EncodedStringValue encodedString = header.getEncodedStringValue(field);
                    if (encodedString != null) {
                        String charsetColumn = CHARSET_COLUMN_NAME_MAP.get(Integer.valueOf(field));
                        values.put(e3.getValue(), toIsoString(encodedString.getTextString()));
                        values.put(charsetColumn, Integer.valueOf(encodedString.getCharacterSet()));
                    }
                }
                Set<Map.Entry<Integer, String>> set2 = TEXT_STRING_COLUMN_NAME_MAP.entrySet();
                for (Map.Entry<Integer, String> e4 : set2) {
                    byte[] text = header.getTextString(e4.getKey().intValue());
                    if (text != null) {
                        values.put(e4.getValue(), toIsoString(text));
                    }
                }
                Set<Map.Entry<Integer, String>> set3 = OCTET_COLUMN_NAME_MAP.entrySet();
                for (Map.Entry<Integer, String> e5 : set3) {
                    int b = header.getOctet(e5.getKey().intValue());
                    if (b != 0) {
                        values.put(e5.getValue(), Integer.valueOf(b));
                    }
                }
                Set<Map.Entry<Integer, String>> set4 = LONG_COLUMN_NAME_MAP.entrySet();
                for (Map.Entry<Integer, String> e6 : set4) {
                    long l = header.getLongInteger(e6.getKey().intValue());
                    if (l != -1) {
                        values.put(e6.getValue(), Long.valueOf(l));
                    }
                }
                int[] iArr2 = ADDRESS_FIELDS;
                HashMap<Integer, EncodedStringValue[]> addressMap = new HashMap<>(iArr2.length);
                int length = iArr2.length;
                int i4 = 0;
                while (i4 < length) {
                    int addrType = iArr2[i4];
                    EncodedStringValue[] array = null;
                    if (addrType == 137) {
                        EncodedStringValue v = header.getEncodedStringValue(addrType);
                        if (v == null) {
                            i3 = length;
                            iArr = iArr2;
                        } else {
                            i3 = length;
                            iArr = iArr2;
                            array = new EncodedStringValue[]{v};
                        }
                    } else {
                        i3 = length;
                        iArr = iArr2;
                        array = header.getEncodedStringValues(addrType);
                    }
                    addressMap.put(Integer.valueOf(addrType), array);
                    i4++;
                    length = i3;
                    iArr2 = iArr;
                }
                HashSet<String> recipients = new HashSet<>();
                int msgType = pdu.getMessageType();
                if (msgType == 130 || msgType == 132 || msgType == 128) {
                    switch (msgType) {
                        case 128:
                            loadRecipients(151, recipients, addressMap, false);
                            break;
                        case 130:
                        case 132:
                            loadRecipients(137, recipients, addressMap, false);
                            if (groupMmsEnabled) {
                                loadRecipients(151, recipients, addressMap, true);
                                loadRecipients(130, recipients, addressMap, true);
                                break;
                            }
                            break;
                    }
                    long threadId = 0;
                    if (createThreadId && !recipients.isEmpty()) {
                        threadId = Telephony.Threads.getOrCreateThreadId(this.mContext, recipients);
                    }
                    values.put("thread_id", Long.valueOf(threadId));
                }
                long placeholderId2 = System.currentTimeMillis();
                int i5 = 1;
                int messageSize2 = 0;
                if (!(pdu instanceof MultimediaMessagePdu)) {
                    messageSize = 0;
                    i = 1;
                } else {
                    PduBody body = ((MultimediaMessagePdu) pdu).getBody();
                    if (body == null) {
                        messageSize = 0;
                        i = 1;
                    } else {
                        int partsNum = body.getPartsNum();
                        if (partsNum > 2) {
                            i5 = 0;
                        }
                        int i6 = 0;
                        while (i6 < partsNum) {
                            PduPart part = body.getPart(i6);
                            int messageSize3 = messageSize2 + part.getDataLength();
                            persistPart(part, placeholderId2, preOpenedFiles);
                            int partsNum2 = partsNum;
                            String contentType = getPartContentType(part);
                            if (contentType != null && !ContentType.APP_SMIL.equals(contentType) && !"text/plain".equals(contentType)) {
                                i5 = 0;
                            }
                            i6++;
                            messageSize2 = messageSize3;
                            partsNum = partsNum2;
                        }
                        messageSize = messageSize2;
                        i = i5;
                    }
                }
                values.put(Telephony.BaseMmsColumns.TEXT_ONLY, Integer.valueOf(i));
                if (values.getAsInteger(Telephony.BaseMmsColumns.MESSAGE_SIZE) == null) {
                    values.put(Telephony.BaseMmsColumns.MESSAGE_SIZE, Integer.valueOf(messageSize));
                }
                if (!existingUri) {
                    i2 = 0;
                    placeholderId = placeholderId2;
                    uri2 = uri;
                    res = SqliteWrapper.insert(this.mContext, this.mContentResolver, uri2, values);
                    if (res == null) {
                        throw new MmsException("persist() failed: return null.");
                    }
                    msgId2 = ContentUris.parseId(res);
                } else {
                    i2 = 0;
                    placeholderId = placeholderId2;
                    SqliteWrapper.update(this.mContext, this.mContentResolver, uri, values, null, null);
                    uri2 = uri;
                    res = uri;
                    msgId2 = msgId;
                }
                ContentValues values2 = new ContentValues(1);
                values2.put(Telephony.Mms.Part.MSG_ID, Long.valueOf(msgId2));
                SqliteWrapper.update(this.mContext, this.mContentResolver, Uri.parse("content://mms/" + placeholderId + "/part"), values2, null, null);
                if (!existingUri) {
                    res = Uri.parse(uri2 + "/" + msgId2);
                }
                int[] iArr3 = ADDRESS_FIELDS;
                int length2 = iArr3.length;
                int i7 = i2;
                while (i7 < length2) {
                    int addrType2 = iArr3[i7];
                    int[] iArr4 = iArr3;
                    EncodedStringValue[] array2 = addressMap.get(Integer.valueOf(addrType2));
                    if (array2 != null) {
                        persistAddress(msgId2, addrType2, array2);
                    }
                    i7++;
                    iArr3 = iArr4;
                }
                return res;
            } catch (Throwable th3) {
                th = th3;
            }
        }
    }

    private void loadRecipients(int addressType, HashSet<String> recipients, HashMap<Integer, EncodedStringValue[]> addressMap, boolean excludeMyNumber) {
        EncodedStringValue[] array = addressMap.get(Integer.valueOf(addressType));
        if (array == null) {
            return;
        }
        if (excludeMyNumber && array.length == 1) {
            return;
        }
        SubscriptionManager subscriptionManager = SubscriptionManager.from(this.mContext);
        Set<String> myPhoneNumbers = new HashSet<>();
        if (excludeMyNumber) {
            for (SubscriptionInfo subInfo : subscriptionManager.getActiveSubscriptionInfoList()) {
                String myNumber = ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).createForSubscriptionId(subInfo.getSubscriptionId()).getLine1Number();
                if (myNumber != null) {
                    myPhoneNumbers.add(myNumber);
                }
            }
        }
        for (EncodedStringValue v : array) {
            if (v != null) {
                String number = v.getString();
                if (excludeMyNumber) {
                    Iterator<String> it = myPhoneNumbers.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        } else if (!PhoneNumberUtils.compare(number, it.next()) && !recipients.contains(number)) {
                            recipients.add(number);
                            break;
                        }
                    }
                } else if (!recipients.contains(number)) {
                    recipients.add(number);
                }
            }
        }
    }

    public Uri move(Uri from, Uri to) throws MmsException {
        long msgId = ContentUris.parseId(from);
        if (msgId == -1) {
            throw new MmsException("Error! ID of the message: -1.");
        }
        Integer msgBox = MESSAGE_BOX_MAP.get(to);
        if (msgBox == null) {
            throw new MmsException("Bad destination, must be one of content://mms/inbox, content://mms/sent, content://mms/drafts, content://mms/outbox, content://mms/temp.");
        }
        ContentValues values = new ContentValues(1);
        values.put(Telephony.BaseMmsColumns.MESSAGE_BOX, msgBox);
        SqliteWrapper.update(this.mContext, this.mContentResolver, from, values, null, null);
        return ContentUris.withAppendedId(to, msgId);
    }

    public static String toIsoString(byte[] bytes) {
        try {
            return new String(bytes, CharacterSets.MIMENAME_ISO_8859_1);
        } catch (UnsupportedEncodingException e) {
            Log.m109e(TAG, "ISO_8859_1 must be supported!", e);
            return "";
        }
    }

    public static byte[] getBytes(String data) {
        try {
            return data.getBytes(CharacterSets.MIMENAME_ISO_8859_1);
        } catch (UnsupportedEncodingException e) {
            Log.m109e(TAG, "ISO_8859_1 must be supported!", e);
            return new byte[0];
        }
    }

    public void release() {
        Uri uri = Uri.parse(TEMPORARY_DRM_OBJECT_URI);
        SqliteWrapper.delete(this.mContext, this.mContentResolver, uri, null, null);
        this.mDrmManagerClient.release();
    }

    public Cursor getPendingMessages(long dueTime) {
        Uri.Builder uriBuilder = Telephony.MmsSms.PendingMessages.CONTENT_URI.buildUpon();
        uriBuilder.appendQueryParameter("protocol", "mms");
        String[] selectionArgs = {String.valueOf(10), String.valueOf(dueTime)};
        return SqliteWrapper.query(this.mContext, this.mContentResolver, uriBuilder.build(), null, "err_type < ? AND due_time <= ?", selectionArgs, Telephony.MmsSms.PendingMessages.DUE_TIME);
    }
}
