package android.media;

import android.mtp.MtpConstants;
import android.text.format.DateFormat;
import com.android.internal.widget.MessagingMessage;
import com.google.android.mms.ContentType;
import java.util.HashMap;
import java.util.Locale;
import libcore.content.type.MimeMap;
/* loaded from: classes2.dex */
public class MediaFile {
    @Deprecated
    private static final int FIRST_AUDIO_FILE_TYPE = 1;
    @Deprecated
    private static final int LAST_AUDIO_FILE_TYPE = 10;
    @Deprecated
    private static final HashMap<String, MediaFileType> sFileTypeMap = new HashMap<>();
    @Deprecated
    private static final HashMap<String, Integer> sFileTypeToFormatMap = new HashMap<>();
    private static final HashMap<String, Integer> sMimeTypeToFormatMap = new HashMap<>();
    private static final HashMap<Integer, String> sFormatToMimeTypeMap = new HashMap<>();

    @Deprecated
    /* loaded from: classes2.dex */
    public static class MediaFileType {
        public final int fileType;
        public final String mimeType;

        MediaFileType(int fileType, String mimeType) {
            this.fileType = fileType;
            this.mimeType = mimeType;
        }
    }

    static {
        addFileType(12297, "audio/mpeg");
        addFileType(12296, ContentType.AUDIO_X_WAV);
        addFileType(MtpConstants.FORMAT_WMA, "audio/x-ms-wma");
        addFileType(MtpConstants.FORMAT_OGG, ContentType.AUDIO_OGG2);
        addFileType(MtpConstants.FORMAT_AAC, ContentType.AUDIO_AAC);
        addFileType(MtpConstants.FORMAT_FLAC, MediaFormat.MIMETYPE_AUDIO_FLAC);
        addFileType(12295, "audio/x-aiff");
        addFileType(MtpConstants.FORMAT_MP2, "audio/mpeg");
        addFileType(12299, "video/mpeg");
        addFileType(MtpConstants.FORMAT_MP4_CONTAINER, ContentType.VIDEO_MP4);
        addFileType(MtpConstants.FORMAT_3GP_CONTAINER, "video/3gpp");
        addFileType(MtpConstants.FORMAT_3GP_CONTAINER, ContentType.VIDEO_3G2);
        addFileType(12298, "video/avi");
        addFileType(MtpConstants.FORMAT_WMV, "video/x-ms-wmv");
        addFileType(12300, "video/x-ms-asf");
        addFileType(MtpConstants.FORMAT_EXIF_JPEG, ContentType.IMAGE_JPEG);
        addFileType(MtpConstants.FORMAT_GIF, ContentType.IMAGE_GIF);
        addFileType(MtpConstants.FORMAT_PNG, ContentType.IMAGE_PNG);
        addFileType(MtpConstants.FORMAT_BMP, ContentType.IMAGE_X_MS_BMP);
        addFileType(MtpConstants.FORMAT_HEIF, "image/heif");
        addFileType(MtpConstants.FORMAT_DNG, "image/x-adobe-dng");
        addFileType(MtpConstants.FORMAT_TIFF, "image/tiff");
        addFileType(MtpConstants.FORMAT_TIFF, "image/x-canon-cr2");
        addFileType(MtpConstants.FORMAT_TIFF, "image/x-nikon-nrw");
        addFileType(MtpConstants.FORMAT_TIFF, "image/x-sony-arw");
        addFileType(MtpConstants.FORMAT_TIFF, "image/x-panasonic-rw2");
        addFileType(MtpConstants.FORMAT_TIFF, "image/x-olympus-orf");
        addFileType(MtpConstants.FORMAT_TIFF, "image/x-pentax-pef");
        addFileType(MtpConstants.FORMAT_TIFF, "image/x-samsung-srw");
        addFileType(MtpConstants.FORMAT_TIFF_EP, "image/tiff");
        addFileType(MtpConstants.FORMAT_TIFF_EP, "image/x-nikon-nef");
        addFileType(MtpConstants.FORMAT_JP2, "image/jp2");
        addFileType(MtpConstants.FORMAT_JPX, "image/jpx");
        addFileType(MtpConstants.FORMAT_M3U_PLAYLIST, "audio/x-mpegurl");
        addFileType(MtpConstants.FORMAT_PLS_PLAYLIST, "audio/x-scpls");
        addFileType(MtpConstants.FORMAT_WPL_PLAYLIST, "application/vnd.ms-wpl");
        addFileType(MtpConstants.FORMAT_ASX_PLAYLIST, "video/x-ms-asf");
        addFileType(12292, "text/plain");
        addFileType(12293, "text/html");
        addFileType(MtpConstants.FORMAT_XML_DOCUMENT, "text/xml");
        addFileType(MtpConstants.FORMAT_MS_WORD_DOCUMENT, "application/msword");
        addFileType(MtpConstants.FORMAT_MS_WORD_DOCUMENT, "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        addFileType(MtpConstants.FORMAT_MS_EXCEL_SPREADSHEET, "application/vnd.ms-excel");
        addFileType(MtpConstants.FORMAT_MS_EXCEL_SPREADSHEET, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        addFileType(MtpConstants.FORMAT_MS_POWERPOINT_PRESENTATION, "application/vnd.ms-powerpoint");
        addFileType(MtpConstants.FORMAT_MS_POWERPOINT_PRESENTATION, "application/vnd.openxmlformats-officedocument.presentationml.presentation");
    }

    @Deprecated
    static void addFileType(String extension, int fileType, String mimeType) {
    }

    private static void addFileType(int mtpFormatCode, String mimeType) {
        HashMap<String, Integer> hashMap = sMimeTypeToFormatMap;
        if (!hashMap.containsKey(mimeType)) {
            hashMap.put(mimeType, Integer.valueOf(mtpFormatCode));
        }
        HashMap<Integer, String> hashMap2 = sFormatToMimeTypeMap;
        if (!hashMap2.containsKey(Integer.valueOf(mtpFormatCode))) {
            hashMap2.put(Integer.valueOf(mtpFormatCode), mimeType);
        }
    }

    @Deprecated
    public static boolean isAudioFileType(int fileType) {
        return false;
    }

    @Deprecated
    public static boolean isVideoFileType(int fileType) {
        return false;
    }

    @Deprecated
    public static boolean isImageFileType(int fileType) {
        return false;
    }

    @Deprecated
    public static boolean isPlayListFileType(int fileType) {
        return false;
    }

    @Deprecated
    public static boolean isDrmFileType(int fileType) {
        return false;
    }

    @Deprecated
    public static MediaFileType getFileType(String path) {
        return null;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static boolean isDocumentMimeType(String mimeType) {
        char c;
        if (mimeType == null) {
            return false;
        }
        String normalizedMimeType = normalizeMimeType(mimeType);
        if (normalizedMimeType.startsWith("text/")) {
            return true;
        }
        String lowerCase = normalizedMimeType.toLowerCase(Locale.ROOT);
        switch (lowerCase.hashCode()) {
            case -2135180893:
                if (lowerCase.equals("application/vnd.stardivision.calc")) {
                    c = '$';
                    break;
                }
                c = 65535;
                break;
            case -2135135086:
                if (lowerCase.equals("application/vnd.stardivision.draw")) {
                    c = '&';
                    break;
                }
                c = 65535;
                break;
            case -2134883067:
                if (lowerCase.equals("application/vnd.stardivision.mail")) {
                    c = ')';
                    break;
                }
                c = 65535;
                break;
            case -2134882730:
                if (lowerCase.equals("application/vnd.stardivision.math")) {
                    c = '*';
                    break;
                }
                c = 65535;
                break;
            case -2008589971:
                if (lowerCase.equals("application/epub+zip")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -1782746503:
                if (lowerCase.equals("application/vnd.oasis.opendocument.chart")) {
                    c = 16;
                    break;
                }
                c = 65535;
                break;
            case -1765899696:
                if (lowerCase.equals("application/vnd.stardivision.chart")) {
                    c = '%';
                    break;
                }
                c = 65535;
                break;
            case -1747277413:
                if (lowerCase.equals("application/vnd.sun.xml.writer.template")) {
                    c = '6';
                    break;
                }
                c = 65535;
                break;
            case -1719571662:
                if (lowerCase.equals("application/vnd.oasis.opendocument.text")) {
                    c = 25;
                    break;
                }
                c = 65535;
                break;
            case -1653115602:
                if (lowerCase.equals("application/vnd.ms-excel.sheet.macroenabled.12")) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case -1628346451:
                if (lowerCase.equals("application/vnd.sun.xml.writer")) {
                    c = '4';
                    break;
                }
                c = 65535;
                break;
            case -1590813831:
                if (lowerCase.equals("application/vnd.sun.xml.calc.template")) {
                    c = '.';
                    break;
                }
                c = 65535;
                break;
            case -1506009513:
                if (lowerCase.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.template")) {
                    c = '!';
                    break;
                }
                c = 65535;
                break;
            case -1316922187:
                if (lowerCase.equals("application/vnd.oasis.opendocument.text-template")) {
                    c = 27;
                    break;
                }
                c = 65535;
                break;
            case -1293459259:
                if (lowerCase.equals("application/vnd.ms-word.document.macroenabled.12")) {
                    c = 14;
                    break;
                }
                c = 65535;
                break;
            case -1248334925:
                if (lowerCase.equals("application/pdf")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -1248332507:
                if (lowerCase.equals("application/rtf")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case -1095881539:
                if (lowerCase.equals("application/vnd.ms-powerpoint.slideshow.macroenabled.12")) {
                    c = '\f';
                    break;
                }
                c = 65535;
                break;
            case -1073633483:
                if (lowerCase.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation")) {
                    c = 29;
                    break;
                }
                c = 65535;
                break;
            case -1071817359:
                if (lowerCase.equals("application/vnd.ms-powerpoint")) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            case -1050893613:
                if (lowerCase.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                    c = '\"';
                    break;
                }
                c = 65535;
                break;
            case -1033484950:
                if (lowerCase.equals("application/vnd.sun.xml.draw.template")) {
                    c = '0';
                    break;
                }
                c = 65535;
                break;
            case -943935167:
                if (lowerCase.equals("application/vnd.oasis.opendocument.formula")) {
                    c = 18;
                    break;
                }
                c = 65535;
                break;
            case -878977693:
                if (lowerCase.equals("application/vnd.ms-excel.template.macroenabled.12")) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case -779959281:
                if (lowerCase.equals("application/vnd.sun.xml.calc")) {
                    c = '-';
                    break;
                }
                c = 65535;
                break;
            case -779913474:
                if (lowerCase.equals("application/vnd.sun.xml.draw")) {
                    c = '/';
                    break;
                }
                c = 65535;
                break;
            case -779661118:
                if (lowerCase.equals("application/vnd.sun.xml.math")) {
                    c = '3';
                    break;
                }
                c = 65535;
                break;
            case -676675015:
                if (lowerCase.equals("application/vnd.oasis.opendocument.text-web")) {
                    c = 28;
                    break;
                }
                c = 65535;
                break;
            case -481744190:
                if (lowerCase.equals("application/x-mspublisher")) {
                    c = '7';
                    break;
                }
                c = 65535;
                break;
            case -479218428:
                if (lowerCase.equals("application/vnd.sun.xml.writer.global")) {
                    c = '5';
                    break;
                }
                c = 65535;
                break;
            case -396757341:
                if (lowerCase.equals("application/vnd.sun.xml.impress.template")) {
                    c = '2';
                    break;
                }
                c = 65535;
                break;
            case -366307023:
                if (lowerCase.equals("application/vnd.ms-excel")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case -300783143:
                if (lowerCase.equals("application/vnd.ms-excel.sheet.binary.macroenabled.12")) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case -291851672:
                if (lowerCase.equals("application/vnd.oasis.opendocument.presentation-template")) {
                    c = 22;
                    break;
                }
                c = 65535;
                break;
            case -151068779:
                if (lowerCase.equals("application/vnd.ms-powerpoint.addin.macroenabled.12")) {
                    c = '\n';
                    break;
                }
                c = 65535;
                break;
            case -109382304:
                if (lowerCase.equals("application/vnd.oasis.opendocument.spreadsheet-template")) {
                    c = 24;
                    break;
                }
                c = 65535;
                break;
            case 65167651:
                if (lowerCase.equals("application/vnd.ms-powerpoint.template.macroenabled.12")) {
                    c = '\r';
                    break;
                }
                c = 65535;
                break;
            case 428819984:
                if (lowerCase.equals("application/vnd.oasis.opendocument.graphics")) {
                    c = 19;
                    break;
                }
                c = 65535;
                break;
            case 571050671:
                if (lowerCase.equals("application/vnd.stardivision.writer-global")) {
                    c = ',';
                    break;
                }
                c = 65535;
                break;
            case 669516689:
                if (lowerCase.equals("application/vnd.stardivision.impress")) {
                    c = DateFormat.QUOTE;
                    break;
                }
                c = 65535;
                break;
            case 694663701:
                if (lowerCase.equals("application/vnd.openxmlformats-officedocument.presentationml.template")) {
                    c = 31;
                    break;
                }
                c = 65535;
                break;
            case 776322612:
                if (lowerCase.equals("application/vnd.stardivision.impress-packed")) {
                    c = '(';
                    break;
                }
                c = 65535;
                break;
            case 904647503:
                if (lowerCase.equals("application/msword")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 1060806194:
                if (lowerCase.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.template")) {
                    c = '#';
                    break;
                }
                c = 65535;
                break;
            case 1377360791:
                if (lowerCase.equals("application/vnd.oasis.opendocument.graphics-template")) {
                    c = 20;
                    break;
                }
                c = 65535;
                break;
            case 1383644708:
                if (lowerCase.equals("application/vnd.ms-word.template.macroenabled.12")) {
                    c = 15;
                    break;
                }
                c = 65535;
                break;
            case 1383977381:
                if (lowerCase.equals("application/vnd.sun.xml.impress")) {
                    c = '1';
                    break;
                }
                c = 65535;
                break;
            case 1436962847:
                if (lowerCase.equals("application/vnd.oasis.opendocument.presentation")) {
                    c = 21;
                    break;
                }
                c = 65535;
                break;
            case 1461751133:
                if (lowerCase.equals("application/vnd.oasis.opendocument.text-master")) {
                    c = 26;
                    break;
                }
                c = 65535;
                break;
            case 1577426419:
                if (lowerCase.equals("application/vnd.openxmlformats-officedocument.presentationml.slideshow")) {
                    c = 30;
                    break;
                }
                c = 65535;
                break;
            case 1643664935:
                if (lowerCase.equals("application/vnd.oasis.opendocument.spreadsheet")) {
                    c = 23;
                    break;
                }
                c = 65535;
                break;
            case 1673742401:
                if (lowerCase.equals("application/vnd.stardivision.writer")) {
                    c = '+';
                    break;
                }
                c = 65535;
                break;
            case 1750539587:
                if (lowerCase.equals("application/vnd.ms-powerpoint.presentation.macroenabled.12")) {
                    c = 11;
                    break;
                }
                c = 65535;
                break;
            case 1993842850:
                if (lowerCase.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
                    c = ' ';
                    break;
                }
                c = 65535;
                break;
            case 2094058325:
                if (lowerCase.equals("application/vnd.ms-excel.addin.macroenabled.12")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 2117577984:
                if (lowerCase.equals("application/vnd.oasis.opendocument.database")) {
                    c = 17;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case '\b':
            case '\t':
            case '\n':
            case 11:
            case '\f':
            case '\r':
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case ' ':
            case '!':
            case '\"':
            case '#':
            case '$':
            case '%':
            case '&':
            case '\'':
            case '(':
            case ')':
            case '*':
            case '+':
            case ',':
            case '-':
            case '.':
            case '/':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
                return true;
            default:
                return false;
        }
    }

    public static boolean isExifMimeType(String mimeType) {
        return isImageMimeType(mimeType);
    }

    public static boolean isAudioMimeType(String mimeType) {
        return normalizeMimeType(mimeType).startsWith("audio/");
    }

    public static boolean isVideoMimeType(String mimeType) {
        return normalizeMimeType(mimeType).startsWith("video/");
    }

    public static boolean isImageMimeType(String mimeType) {
        return normalizeMimeType(mimeType).startsWith(MessagingMessage.IMAGE_MIME_TYPE_PREFIX);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static boolean isPlayListMimeType(String mimeType) {
        char c;
        String normalizeMimeType = normalizeMimeType(mimeType);
        switch (normalizeMimeType.hashCode()) {
            case -1165508903:
                if (normalizeMimeType.equals("audio/x-scpls")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case -979095690:
                if (normalizeMimeType.equals("application/x-mpegurl")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case -622808459:
                if (normalizeMimeType.equals("application/vnd.apple.mpegurl")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case -432766831:
                if (normalizeMimeType.equals("audio/mpegurl")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 264230524:
                if (normalizeMimeType.equals("audio/x-mpegurl")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 1872259501:
                if (normalizeMimeType.equals("application/vnd.ms-wpl")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                return true;
            default:
                return false;
        }
    }

    public static boolean isDrmMimeType(String mimeType) {
        return normalizeMimeType(mimeType).equals("application/x-android-drm-fl");
    }

    public static String getFileTitle(String path) {
        int lastSlash;
        int lastSlash2 = path.lastIndexOf(47);
        if (lastSlash2 >= 0 && (lastSlash = lastSlash2 + 1) < path.length()) {
            path = path.substring(lastSlash);
        }
        int lastDot = path.lastIndexOf(46);
        if (lastDot > 0) {
            return path.substring(0, lastDot);
        }
        return path;
    }

    public static String getFileExtension(String path) {
        int lastDot;
        if (path == null || (lastDot = path.lastIndexOf(46)) < 0) {
            return null;
        }
        return path.substring(lastDot + 1);
    }

    @Deprecated
    public static int getFileTypeForMimeType(String mimeType) {
        return 0;
    }

    public static String getMimeType(String path, int formatCode) {
        String mimeType = getMimeTypeForFile(path);
        if (!"application/octet-stream".equals(mimeType)) {
            return mimeType;
        }
        return getMimeTypeForFormatCode(formatCode);
    }

    public static String getMimeTypeForFile(String path) {
        String ext = getFileExtension(path);
        String mimeType = MimeMap.getDefault().guessMimeTypeFromExtension(ext);
        return mimeType != null ? mimeType : "application/octet-stream";
    }

    public static String getMimeTypeForFormatCode(int formatCode) {
        String mimeType = sFormatToMimeTypeMap.get(Integer.valueOf(formatCode));
        return mimeType != null ? mimeType : "application/octet-stream";
    }

    public static int getFormatCode(String path, String mimeType) {
        int formatCode = getFormatCodeForMimeType(mimeType);
        if (formatCode != 12288) {
            return formatCode;
        }
        return getFormatCodeForFile(path);
    }

    public static int getFormatCodeForFile(String path) {
        return getFormatCodeForMimeType(getMimeTypeForFile(path));
    }

    public static int getFormatCodeForMimeType(String mimeType) {
        if (mimeType == null) {
            return 12288;
        }
        HashMap<String, Integer> hashMap = sMimeTypeToFormatMap;
        Integer value = hashMap.get(mimeType);
        if (value != null) {
            return value.intValue();
        }
        String mimeType2 = normalizeMimeType(mimeType);
        Integer value2 = hashMap.get(mimeType2);
        if (value2 != null) {
            return value2.intValue();
        }
        if (mimeType2.startsWith("audio/")) {
            return MtpConstants.FORMAT_UNDEFINED_AUDIO;
        }
        if (mimeType2.startsWith("video/")) {
            return MtpConstants.FORMAT_UNDEFINED_VIDEO;
        }
        if (!mimeType2.startsWith(MessagingMessage.IMAGE_MIME_TYPE_PREFIX)) {
            return 12288;
        }
        return 14336;
    }

    private static String normalizeMimeType(String mimeType) {
        String extensionMimeType;
        MimeMap mimeMap = MimeMap.getDefault();
        String extension = mimeMap.guessExtensionFromMimeType(mimeType);
        if (extension == null || (extensionMimeType = mimeMap.guessMimeTypeFromExtension(extension)) == null) {
            return mimeType != null ? mimeType : "application/octet-stream";
        }
        return extensionMimeType;
    }
}
