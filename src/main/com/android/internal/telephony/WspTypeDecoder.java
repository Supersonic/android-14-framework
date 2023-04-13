package com.android.internal.telephony;

import android.compat.annotation.UnsupportedAppUsage;
import java.util.HashMap;
/* loaded from: classes.dex */
public class WspTypeDecoder {
    public static final String CONTENT_TYPE_B_MMS = "application/vnd.wap.mms-message";
    public static final String CONTENT_TYPE_B_PUSH_CO = "application/vnd.wap.coc";
    public static final String CONTENT_TYPE_B_PUSH_SYNCML_NOTI = "application/vnd.syncml.notification";
    public static final int PARAMETER_ID_X_WAP_APPLICATION_ID = 47;
    public static final int PDU_TYPE_CONFIRMED_PUSH = 7;
    public static final int PDU_TYPE_PUSH = 6;
    private static final HashMap<Integer, String> WELL_KNOWN_MIME_TYPES;
    private static final HashMap<Integer, String> WELL_KNOWN_PARAMETERS;
    HashMap<String, String> mContentParameters;
    int mDataLength;
    String mStringValue;
    long mUnsigned32bit;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    byte[] mWspData;

    static {
        HashMap<Integer, String> hashMap = new HashMap<>();
        WELL_KNOWN_MIME_TYPES = hashMap;
        HashMap<Integer, String> hashMap2 = new HashMap<>();
        WELL_KNOWN_PARAMETERS = hashMap2;
        hashMap.put(0, "*/*");
        hashMap.put(1, "text/*");
        hashMap.put(2, "text/html");
        hashMap.put(3, "text/plain");
        hashMap.put(4, "text/x-hdml");
        hashMap.put(5, "text/x-ttml");
        hashMap.put(6, "text/x-vCalendar");
        hashMap.put(7, "text/x-vCard");
        hashMap.put(8, "text/vnd.wap.wml");
        hashMap.put(9, "text/vnd.wap.wmlscript");
        hashMap.put(10, "text/vnd.wap.wta-event");
        hashMap.put(11, "multipart/*");
        hashMap.put(12, "multipart/mixed");
        hashMap.put(13, "multipart/form-data");
        hashMap.put(14, "multipart/byterantes");
        hashMap.put(15, "multipart/alternative");
        hashMap.put(16, "application/*");
        hashMap.put(17, "application/java-vm");
        hashMap.put(18, "application/x-www-form-urlencoded");
        hashMap.put(19, "application/x-hdmlc");
        hashMap.put(20, "application/vnd.wap.wmlc");
        hashMap.put(21, "application/vnd.wap.wmlscriptc");
        hashMap.put(22, "application/vnd.wap.wta-eventc");
        hashMap.put(23, "application/vnd.wap.uaprof");
        hashMap.put(24, "application/vnd.wap.wtls-ca-certificate");
        hashMap.put(25, "application/vnd.wap.wtls-user-certificate");
        hashMap.put(26, "application/x-x509-ca-cert");
        hashMap.put(27, "application/x-x509-user-cert");
        hashMap.put(28, "image/*");
        hashMap.put(29, "image/gif");
        hashMap.put(30, "image/jpeg");
        hashMap.put(31, "image/tiff");
        hashMap.put(32, "image/png");
        hashMap.put(33, "image/vnd.wap.wbmp");
        hashMap.put(34, "application/vnd.wap.multipart.*");
        hashMap.put(35, "application/vnd.wap.multipart.mixed");
        hashMap.put(36, "application/vnd.wap.multipart.form-data");
        hashMap.put(37, "application/vnd.wap.multipart.byteranges");
        hashMap.put(38, "application/vnd.wap.multipart.alternative");
        hashMap.put(39, "application/xml");
        hashMap.put(40, "text/xml");
        hashMap.put(41, "application/vnd.wap.wbxml");
        hashMap.put(42, "application/x-x968-cross-cert");
        hashMap.put(43, "application/x-x968-ca-cert");
        hashMap.put(44, "application/x-x968-user-cert");
        hashMap.put(45, "text/vnd.wap.si");
        hashMap.put(46, "application/vnd.wap.sic");
        hashMap.put(47, "text/vnd.wap.sl");
        hashMap.put(48, "application/vnd.wap.slc");
        hashMap.put(49, "text/vnd.wap.co");
        hashMap.put(50, CONTENT_TYPE_B_PUSH_CO);
        hashMap.put(51, "application/vnd.wap.multipart.related");
        hashMap.put(52, "application/vnd.wap.sia");
        hashMap.put(53, "text/vnd.wap.connectivity-xml");
        hashMap.put(54, "application/vnd.wap.connectivity-wbxml");
        hashMap.put(55, "application/pkcs7-mime");
        hashMap.put(56, "application/vnd.wap.hashed-certificate");
        hashMap.put(57, "application/vnd.wap.signed-certificate");
        hashMap.put(58, "application/vnd.wap.cert-response");
        hashMap.put(59, "application/xhtml+xml");
        hashMap.put(60, "application/wml+xml");
        hashMap.put(61, "text/css");
        hashMap.put(62, CONTENT_TYPE_B_MMS);
        hashMap.put(63, "application/vnd.wap.rollover-certificate");
        hashMap.put(64, "application/vnd.wap.locc+wbxml");
        hashMap.put(65, "application/vnd.wap.loc+xml");
        hashMap.put(66, "application/vnd.syncml.dm+wbxml");
        hashMap.put(67, "application/vnd.syncml.dm+xml");
        hashMap.put(68, CONTENT_TYPE_B_PUSH_SYNCML_NOTI);
        hashMap.put(69, "application/vnd.wap.xhtml+xml");
        hashMap.put(70, "application/vnd.wv.csp.cir");
        hashMap.put(71, "application/vnd.oma.dd+xml");
        hashMap.put(72, "application/vnd.oma.drm.message");
        hashMap.put(73, "application/vnd.oma.drm.content");
        hashMap.put(74, "application/vnd.oma.drm.rights+xml");
        hashMap.put(75, "application/vnd.oma.drm.rights+wbxml");
        hashMap.put(76, "application/vnd.wv.csp+xml");
        hashMap.put(77, "application/vnd.wv.csp+wbxml");
        hashMap.put(78, "application/vnd.syncml.ds.notification");
        hashMap.put(79, "audio/*");
        hashMap.put(80, "video/*");
        hashMap.put(81, "application/vnd.oma.dd2+xml");
        hashMap.put(82, "application/mikey");
        hashMap.put(83, "application/vnd.oma.dcd");
        hashMap.put(84, "application/vnd.oma.dcdc");
        hashMap.put(513, "application/vnd.uplanet.cacheop-wbxml");
        hashMap.put(514, "application/vnd.uplanet.signal");
        hashMap.put(515, "application/vnd.uplanet.alert-wbxml");
        hashMap.put(516, "application/vnd.uplanet.list-wbxml");
        hashMap.put(517, "application/vnd.uplanet.listcmd-wbxml");
        hashMap.put(518, "application/vnd.uplanet.channel-wbxml");
        hashMap.put(519, "application/vnd.uplanet.provisioning-status-uri");
        hashMap.put(520, "x-wap.multipart/vnd.uplanet.header-set");
        hashMap.put(521, "application/vnd.uplanet.bearer-choice-wbxml");
        hashMap.put(522, "application/vnd.phonecom.mmc-wbxml");
        hashMap.put(523, "application/vnd.nokia.syncset+wbxml");
        hashMap.put(524, "image/x-up-wpng");
        hashMap.put(768, "application/iota.mmc-wbxml");
        hashMap.put(769, "application/iota.mmc-xml");
        hashMap.put(770, "application/vnd.syncml+xml");
        hashMap.put(771, "application/vnd.syncml+wbxml");
        hashMap.put(772, "text/vnd.wap.emn+xml");
        hashMap.put(773, "text/calendar");
        hashMap.put(774, "application/vnd.omads-email+xml");
        hashMap.put(775, "application/vnd.omads-file+xml");
        hashMap.put(776, "application/vnd.omads-folder+xml");
        hashMap.put(777, "text/directory;profile=vCard");
        hashMap.put(778, "application/vnd.wap.emn+wbxml");
        hashMap.put(779, "application/vnd.nokia.ipdc-purchase-response");
        hashMap.put(780, "application/vnd.motorola.screen3+xml");
        hashMap.put(781, "application/vnd.motorola.screen3+gzip");
        hashMap.put(782, "application/vnd.cmcc.setting+wbxml");
        hashMap.put(783, "application/vnd.cmcc.bombing+wbxml");
        hashMap.put(784, "application/vnd.docomo.pf");
        hashMap.put(785, "application/vnd.docomo.ub");
        hashMap.put(786, "application/vnd.omaloc-supl-init");
        hashMap.put(787, "application/vnd.oma.group-usage-list+xml");
        hashMap.put(788, "application/oma-directory+xml");
        hashMap.put(789, "application/vnd.docomo.pf2");
        hashMap.put(790, "application/vnd.oma.drm.roap-trigger+wbxml");
        hashMap.put(791, "application/vnd.sbm.mid2");
        hashMap.put(792, "application/vnd.wmf.bootstrap");
        hashMap.put(793, "application/vnc.cmcc.dcd+xml");
        hashMap.put(794, "application/vnd.sbm.cid");
        hashMap.put(795, "application/vnd.oma.bcast.provisioningtrigger");
        hashMap2.put(0, "Q");
        hashMap2.put(1, "Charset");
        hashMap2.put(2, "Level");
        hashMap2.put(3, "Type");
        hashMap2.put(7, "Differences");
        hashMap2.put(8, "Padding");
        hashMap2.put(9, "Type");
        hashMap2.put(14, "Max-Age");
        hashMap2.put(16, "Secure");
        hashMap2.put(17, "SEC");
        hashMap2.put(18, "MAC");
        hashMap2.put(19, "Creation-date");
        hashMap2.put(20, "Modification-date");
        hashMap2.put(21, "Read-date");
        hashMap2.put(22, "Size");
        hashMap2.put(23, "Name");
        hashMap2.put(24, "Filename");
        hashMap2.put(25, "Start");
        hashMap2.put(26, "Start-info");
        hashMap2.put(27, "Comment");
        hashMap2.put(28, "Domain");
        hashMap2.put(29, "Path");
    }

    @UnsupportedAppUsage
    public WspTypeDecoder(byte[] bArr) {
        this.mWspData = bArr;
    }

    @UnsupportedAppUsage
    public boolean decodeTextString(int i) {
        byte[] bArr;
        int i2 = i;
        while (true) {
            bArr = this.mWspData;
            if (bArr[i2] == 0) {
                break;
            }
            i2++;
        }
        int i3 = (i2 - i) + 1;
        this.mDataLength = i3;
        if (bArr[i] == Byte.MAX_VALUE) {
            this.mStringValue = new String(bArr, i + 1, i3 - 2);
        } else {
            this.mStringValue = new String(bArr, i, i3 - 1);
        }
        return true;
    }

    public boolean decodeTokenText(int i) {
        int i2 = i;
        while (true) {
            byte[] bArr = this.mWspData;
            if (bArr[i2] == 0) {
                int i3 = (i2 - i) + 1;
                this.mDataLength = i3;
                this.mStringValue = new String(bArr, i, i3 - 1);
                return true;
            }
            i2++;
        }
    }

    @UnsupportedAppUsage
    public boolean decodeShortInteger(int i) {
        byte b = this.mWspData[i];
        if ((b & 128) == 0) {
            return false;
        }
        this.mUnsigned32bit = b & Byte.MAX_VALUE;
        this.mDataLength = 1;
        return true;
    }

    public boolean decodeLongInteger(int i) {
        int i2 = this.mWspData[i] & 255;
        if (i2 > 30) {
            return false;
        }
        this.mUnsigned32bit = 0L;
        for (int i3 = 1; i3 <= i2; i3++) {
            this.mUnsigned32bit = (this.mUnsigned32bit << 8) | (this.mWspData[i + i3] & 255);
        }
        this.mDataLength = i2 + 1;
        return true;
    }

    @UnsupportedAppUsage
    public boolean decodeIntegerValue(int i) {
        if (decodeShortInteger(i)) {
            return true;
        }
        return decodeLongInteger(i);
    }

    @UnsupportedAppUsage
    public boolean decodeUintvarInteger(int i) {
        this.mUnsigned32bit = 0L;
        int i2 = i;
        while (true) {
            byte b = this.mWspData[i2];
            if ((b & 128) == 0) {
                this.mUnsigned32bit = (this.mUnsigned32bit << 7) | (b & Byte.MAX_VALUE);
                this.mDataLength = (i2 - i) + 1;
                return true;
            } else if (i2 - i >= 4) {
                return false;
            } else {
                this.mUnsigned32bit = (this.mUnsigned32bit << 7) | (b & Byte.MAX_VALUE);
                i2++;
            }
        }
    }

    @UnsupportedAppUsage
    public boolean decodeValueLength(int i) {
        byte b = this.mWspData[i];
        if ((b & 255) > 31) {
            return false;
        }
        if (b < 31) {
            this.mUnsigned32bit = b;
            this.mDataLength = 1;
        } else {
            decodeUintvarInteger(i + 1);
            this.mDataLength++;
        }
        return true;
    }

    public boolean decodeExtensionMedia(int i) {
        this.mDataLength = 0;
        this.mStringValue = null;
        int length = this.mWspData.length;
        boolean z = i < length;
        int i2 = i;
        while (i2 < length && this.mWspData[i2] != 0) {
            i2++;
        }
        int i3 = (i2 - i) + 1;
        this.mDataLength = i3;
        this.mStringValue = new String(this.mWspData, i, i3 - 1);
        return z;
    }

    public boolean decodeConstrainedEncoding(int i) {
        if (decodeShortInteger(i)) {
            this.mStringValue = null;
            return true;
        }
        return decodeExtensionMedia(i);
    }

    @UnsupportedAppUsage
    public boolean decodeContentType(int i) {
        this.mContentParameters = new HashMap<>();
        if (!decodeValueLength(i)) {
            boolean decodeConstrainedEncoding = decodeConstrainedEncoding(i);
            if (decodeConstrainedEncoding) {
                expandWellKnownMimeType();
            }
            return decodeConstrainedEncoding;
        }
        int i2 = (int) this.mUnsigned32bit;
        int decodedDataLength = getDecodedDataLength();
        int i3 = i + decodedDataLength;
        if (decodeIntegerValue(i3)) {
            int i4 = this.mDataLength + decodedDataLength;
            this.mDataLength = i4;
            this.mStringValue = null;
            expandWellKnownMimeType();
            long j = this.mUnsigned32bit;
            String str = this.mStringValue;
            int i5 = this.mDataLength;
            if (readContentParameters(i + i5, i2 - (i5 - decodedDataLength), 0)) {
                this.mDataLength += i4;
                this.mUnsigned32bit = j;
                this.mStringValue = str;
                return true;
            }
            return false;
        }
        if (decodeExtensionMedia(i3)) {
            int i6 = this.mDataLength + decodedDataLength;
            this.mDataLength = i6;
            expandWellKnownMimeType();
            long j2 = this.mUnsigned32bit;
            String str2 = this.mStringValue;
            int i7 = this.mDataLength;
            if (readContentParameters(i + i7, i2 - (i7 - decodedDataLength), 0)) {
                this.mDataLength += i6;
                this.mUnsigned32bit = j2;
                this.mStringValue = str2;
                return true;
            }
        }
        return false;
    }

    private boolean readContentParameters(int i, int i2, int i3) {
        int i4;
        String str;
        int i5;
        String substring;
        if (i2 > 0) {
            byte b = this.mWspData[i];
            if ((b & 128) == 0 && b > 31) {
                decodeTokenText(i);
                str = this.mStringValue;
                i4 = this.mDataLength + 0;
            } else if (!decodeIntegerValue(i)) {
                return false;
            } else {
                i4 = 0 + this.mDataLength;
                int i6 = (int) this.mUnsigned32bit;
                String str2 = WELL_KNOWN_PARAMETERS.get(Integer.valueOf(i6));
                if (str2 == null) {
                    str2 = "unassigned/0x" + Long.toHexString(i6);
                }
                if (i6 == 0) {
                    if (decodeUintvarInteger(i + i4)) {
                        int i7 = i4 + this.mDataLength;
                        this.mContentParameters.put(str2, String.valueOf(this.mUnsigned32bit));
                        return readContentParameters(i + i7, i2 - i7, i3 + i7);
                    }
                    return false;
                }
                str = str2;
            }
            int i8 = i + i4;
            if (decodeNoValue(i8)) {
                i5 = i4 + this.mDataLength;
                substring = null;
            } else if (decodeIntegerValue(i8)) {
                i5 = i4 + this.mDataLength;
                substring = String.valueOf((int) this.mUnsigned32bit);
            } else {
                decodeTokenText(i8);
                i5 = i4 + this.mDataLength;
                String str3 = this.mStringValue;
                substring = str3.startsWith("\"") ? str3.substring(1) : str3;
            }
            this.mContentParameters.put(str, substring);
            return readContentParameters(i + i5, i2 - i5, i3 + i5);
        }
        this.mDataLength = i3;
        return true;
    }

    private boolean decodeNoValue(int i) {
        if (this.mWspData[i] == 0) {
            this.mDataLength = 1;
            return true;
        }
        return false;
    }

    private void expandWellKnownMimeType() {
        if (this.mStringValue == null) {
            this.mStringValue = WELL_KNOWN_MIME_TYPES.get(Integer.valueOf((int) this.mUnsigned32bit));
        } else {
            this.mUnsigned32bit = -1L;
        }
    }

    public boolean decodeContentLength(int i) {
        return decodeIntegerValue(i);
    }

    public boolean decodeContentLocation(int i) {
        return decodeTextString(i);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public boolean decodeXWapApplicationId(int i) {
        if (decodeIntegerValue(i)) {
            this.mStringValue = null;
            return true;
        }
        return decodeTextString(i);
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public boolean seekXWapApplicationId(int i, int i2) {
        int decodedDataLength;
        while (i <= i2) {
            try {
                if (decodeIntegerValue(i)) {
                    if (((int) getValue32()) == 47) {
                        this.mUnsigned32bit = i + 1;
                        return true;
                    }
                } else if (!decodeTextString(i)) {
                    return false;
                }
                int decodedDataLength2 = i + getDecodedDataLength();
                if (decodedDataLength2 > i2) {
                    return false;
                }
                byte b = this.mWspData[decodedDataLength2];
                if (b < 0 || b > 30) {
                    if (b == 31) {
                        decodedDataLength2++;
                        if (decodedDataLength2 >= i2 || !decodeUintvarInteger(decodedDataLength2)) {
                            return false;
                        }
                        decodedDataLength = getDecodedDataLength();
                    } else if (31 >= b || b > Byte.MAX_VALUE) {
                        i = decodedDataLength2 + 1;
                    } else if (!decodeTextString(decodedDataLength2)) {
                        return false;
                    } else {
                        decodedDataLength = getDecodedDataLength();
                    }
                    i = decodedDataLength2 + decodedDataLength;
                } else {
                    i = decodedDataLength2 + b + 1;
                }
            } catch (ArrayIndexOutOfBoundsException unused) {
            }
        }
        return false;
    }

    public boolean decodeXWapContentURI(int i) {
        return decodeTextString(i);
    }

    public boolean decodeXWapInitiatorURI(int i) {
        return decodeTextString(i);
    }

    @UnsupportedAppUsage
    public int getDecodedDataLength() {
        return this.mDataLength;
    }

    @UnsupportedAppUsage
    public long getValue32() {
        return this.mUnsigned32bit;
    }

    @UnsupportedAppUsage
    public String getValueString() {
        return this.mStringValue;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public HashMap<String, String> getContentParameters() {
        return this.mContentParameters;
    }
}
