package com.google.android.mms.pdu;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
/* loaded from: classes5.dex */
public class CharacterSets {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public static final int ANY_CHARSET = 0;
    public static final int BIG5 = 2026;
    public static final int BIG5_HKSCS = 2101;
    public static final int BOCU_1 = 1020;
    public static final int CESU_8 = 1016;
    public static final int CP864 = 2051;
    public static final int DEFAULT_CHARSET = 106;
    public static final String DEFAULT_CHARSET_NAME = "utf-8";
    public static final int EUC_JP = 18;
    public static final int EUC_KR = 38;
    public static final int GB18030 = 114;
    public static final int GBK = 113;
    public static final int GB_2312 = 2025;
    public static final int HZ_GB_2312 = 2085;
    public static final int ISO_2022_CN = 104;
    public static final int ISO_2022_CN_EXT = 105;
    public static final int ISO_2022_JP = 39;
    public static final int ISO_2022_KR = 37;
    public static final int ISO_8859_1 = 4;
    public static final int ISO_8859_10 = 13;
    public static final int ISO_8859_13 = 109;
    public static final int ISO_8859_14 = 110;
    public static final int ISO_8859_15 = 111;
    public static final int ISO_8859_16 = 112;
    public static final int ISO_8859_2 = 5;
    public static final int ISO_8859_3 = 6;
    public static final int ISO_8859_4 = 7;
    public static final int ISO_8859_5 = 8;
    public static final int ISO_8859_6 = 9;
    public static final int ISO_8859_7 = 10;
    public static final int ISO_8859_8 = 11;
    public static final int ISO_8859_9 = 12;
    public static final int KOI8_R = 2084;
    public static final int KOI8_U = 2088;
    public static final int MACINTOSH = 2027;
    private static final int[] MIBENUM_NUMBERS;
    private static final HashMap<Integer, String> MIBENUM_TO_NAME_MAP;
    public static final String MIMENAME_ANY_CHARSET = "*";
    public static final String MIMENAME_BIG5 = "big5";
    public static final String MIMENAME_BIG5_HKSCS = "Big5-HKSCS";
    public static final String MIMENAME_BOCU_1 = "BOCU-1";
    public static final String MIMENAME_CESU_8 = "CESU-8";
    public static final String MIMENAME_CP864 = "cp864";
    public static final String MIMENAME_EUC_JP = "EUC-JP";
    public static final String MIMENAME_EUC_KR = "EUC-KR";
    public static final String MIMENAME_GB18030 = "GB18030";
    public static final String MIMENAME_GBK = "GBK";
    public static final String MIMENAME_GB_2312 = "GB2312";
    public static final String MIMENAME_HZ_GB_2312 = "HZ-GB-2312";
    public static final String MIMENAME_ISO_2022_CN = "ISO-2022-CN";
    public static final String MIMENAME_ISO_2022_CN_EXT = "ISO-2022-CN-EXT";
    public static final String MIMENAME_ISO_2022_JP = "ISO-2022-JP";
    public static final String MIMENAME_ISO_2022_KR = "ISO-2022-KR";
    public static final String MIMENAME_ISO_8859_1 = "iso-8859-1";
    public static final String MIMENAME_ISO_8859_10 = "ISO-8859-10";
    public static final String MIMENAME_ISO_8859_13 = "ISO-8859-13";
    public static final String MIMENAME_ISO_8859_14 = "ISO-8859-14";
    public static final String MIMENAME_ISO_8859_15 = "ISO-8859-15";
    public static final String MIMENAME_ISO_8859_16 = "ISO-8859-16";
    public static final String MIMENAME_ISO_8859_2 = "iso-8859-2";
    public static final String MIMENAME_ISO_8859_3 = "iso-8859-3";
    public static final String MIMENAME_ISO_8859_4 = "iso-8859-4";
    public static final String MIMENAME_ISO_8859_5 = "iso-8859-5";
    public static final String MIMENAME_ISO_8859_6 = "iso-8859-6";
    public static final String MIMENAME_ISO_8859_7 = "iso-8859-7";
    public static final String MIMENAME_ISO_8859_8 = "iso-8859-8";
    public static final String MIMENAME_ISO_8859_9 = "iso-8859-9";
    public static final String MIMENAME_KOI8_R = "KOI8-R";
    public static final String MIMENAME_KOI8_U = "KOI8-U";
    public static final String MIMENAME_MACINTOSH = "macintosh";
    public static final String MIMENAME_SCSU = "SCSU";
    public static final String MIMENAME_SHIFT_JIS = "shift_JIS";
    public static final String MIMENAME_TIS_620 = "TIS-620";
    public static final String MIMENAME_UCS2 = "iso-10646-ucs-2";
    public static final String MIMENAME_US_ASCII = "us-ascii";
    public static final String MIMENAME_UTF_16 = "utf-16";
    public static final String MIMENAME_UTF_16BE = "UTF-16BE";
    public static final String MIMENAME_UTF_16LE = "UTF-16LE";
    public static final String MIMENAME_UTF_32 = "UTF-32";
    public static final String MIMENAME_UTF_32BE = "UTF-32BE";
    public static final String MIMENAME_UTF_32LE = "UTF-32LE";
    public static final String MIMENAME_UTF_7 = "UTF-7";
    public static final String MIMENAME_UTF_8 = "utf-8";
    public static final String MIMENAME_WINDOWS_1250 = "windows-1250";
    public static final String MIMENAME_WINDOWS_1251 = "windows-1251";
    public static final String MIMENAME_WINDOWS_1252 = "windows-1252";
    public static final String MIMENAME_WINDOWS_1253 = "windows-1253";
    public static final String MIMENAME_WINDOWS_1254 = "windows-1254";
    public static final String MIMENAME_WINDOWS_1255 = "windows-1255";
    public static final String MIMENAME_WINDOWS_1256 = "windows-1256";
    public static final String MIMENAME_WINDOWS_1257 = "windows-1257";
    public static final String MIMENAME_WINDOWS_1258 = "windows-1258";
    private static final String[] MIME_NAMES;
    private static final HashMap<String, Integer> NAME_TO_MIBENUM_MAP;
    public static final int SCSU = 1011;
    public static final int SHIFT_JIS = 17;
    public static final int TIS_620 = 2259;
    public static final int UCS2 = 1000;
    public static final int US_ASCII = 3;
    public static final int UTF_16 = 1015;
    public static final int UTF_16BE = 1013;
    public static final int UTF_16LE = 1014;
    public static final int UTF_32 = 1017;
    public static final int UTF_32BE = 1018;
    public static final int UTF_32LE = 1019;
    public static final int UTF_7 = 1012;
    public static final int UTF_8 = 106;
    public static final int WINDOWS_1250 = 2250;
    public static final int WINDOWS_1251 = 2251;
    public static final int WINDOWS_1252 = 2252;
    public static final int WINDOWS_1253 = 2253;
    public static final int WINDOWS_1254 = 2254;
    public static final int WINDOWS_1255 = 2255;
    public static final int WINDOWS_1256 = 2256;
    public static final int WINDOWS_1257 = 2257;
    public static final int WINDOWS_1258 = 2258;

    static {
        int[] iArr = {0, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 17, 106, 2026, 1000, 1015, 2101, 1020, 1016, 2051, 18, 38, 114, 113, 2085, 2025, 104, 105, 39, 37, 13, 109, 110, 111, 112, 2084, 2088, 2027, 1011, TIS_620, 1013, 1014, 1017, 1018, 1019, 1012, 2250, 2251, 2252, 2253, 2254, WINDOWS_1255, WINDOWS_1256, WINDOWS_1257, WINDOWS_1258};
        MIBENUM_NUMBERS = iArr;
        MIME_NAMES = new String[]{"*", MIMENAME_US_ASCII, MIMENAME_ISO_8859_1, MIMENAME_ISO_8859_2, MIMENAME_ISO_8859_3, MIMENAME_ISO_8859_4, MIMENAME_ISO_8859_5, MIMENAME_ISO_8859_6, MIMENAME_ISO_8859_7, MIMENAME_ISO_8859_8, MIMENAME_ISO_8859_9, MIMENAME_SHIFT_JIS, "utf-8", MIMENAME_BIG5, MIMENAME_UCS2, MIMENAME_UTF_16, MIMENAME_BIG5_HKSCS, MIMENAME_BOCU_1, MIMENAME_CESU_8, MIMENAME_CP864, MIMENAME_EUC_JP, MIMENAME_EUC_KR, MIMENAME_GB18030, MIMENAME_GBK, MIMENAME_HZ_GB_2312, MIMENAME_GB_2312, MIMENAME_ISO_2022_CN, MIMENAME_ISO_2022_CN_EXT, MIMENAME_ISO_2022_JP, MIMENAME_ISO_2022_KR, MIMENAME_ISO_8859_10, MIMENAME_ISO_8859_13, MIMENAME_ISO_8859_14, MIMENAME_ISO_8859_15, MIMENAME_ISO_8859_16, MIMENAME_KOI8_R, MIMENAME_KOI8_U, MIMENAME_MACINTOSH, MIMENAME_SCSU, MIMENAME_TIS_620, MIMENAME_UTF_16BE, MIMENAME_UTF_16LE, MIMENAME_UTF_32, MIMENAME_UTF_32BE, MIMENAME_UTF_32LE, MIMENAME_UTF_7, MIMENAME_WINDOWS_1250, MIMENAME_WINDOWS_1251, MIMENAME_WINDOWS_1252, MIMENAME_WINDOWS_1253, MIMENAME_WINDOWS_1254, MIMENAME_WINDOWS_1255, MIMENAME_WINDOWS_1256, MIMENAME_WINDOWS_1257, MIMENAME_WINDOWS_1258};
        MIBENUM_TO_NAME_MAP = new HashMap<>();
        NAME_TO_MIBENUM_MAP = new HashMap<>();
        int count = iArr.length - 1;
        for (int i = 0; i <= count; i++) {
            HashMap<Integer, String> hashMap = MIBENUM_TO_NAME_MAP;
            int[] iArr2 = MIBENUM_NUMBERS;
            Integer valueOf = Integer.valueOf(iArr2[i]);
            String[] strArr = MIME_NAMES;
            hashMap.put(valueOf, strArr[i]);
            NAME_TO_MIBENUM_MAP.put(strArr[i], Integer.valueOf(iArr2[i]));
        }
    }

    private CharacterSets() {
    }

    public static String getMimeName(int mibEnumValue) throws UnsupportedEncodingException {
        String name = MIBENUM_TO_NAME_MAP.get(Integer.valueOf(mibEnumValue));
        if (name == null) {
            throw new UnsupportedEncodingException();
        }
        return name;
    }

    public static int getMibEnumValue(String mimeName) throws UnsupportedEncodingException {
        if (mimeName == null) {
            return -1;
        }
        Integer mibEnumValue = NAME_TO_MIBENUM_MAP.get(mimeName);
        if (mibEnumValue == null) {
            throw new UnsupportedEncodingException();
        }
        return mibEnumValue.intValue();
    }
}
