package android.graphics.fonts;

import dalvik.annotation.optimization.FastNative;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
/* loaded from: classes.dex */
public class FontFileUtil {
    private static final int ANALYZE_ERROR = -1;
    private static final int OS2_TABLE_TAG = 1330851634;
    private static final int SFNT_VERSION_1 = 65536;
    private static final int SFNT_VERSION_OTTO = 1330926671;
    private static final int TTC_TAG = 1953784678;

    @FastNative
    private static native String nGetFontPostScriptName(ByteBuffer byteBuffer, int i);

    @FastNative
    private static native long nGetFontRevision(ByteBuffer byteBuffer, int i);

    @FastNative
    private static native int nIsPostScriptType1Font(ByteBuffer byteBuffer, int i);

    private FontFileUtil() {
    }

    public static int unpackWeight(int packed) {
        return 65535 & packed;
    }

    public static boolean unpackItalic(int packed) {
        return (65536 & packed) != 0;
    }

    public static boolean isSuccess(int packed) {
        return packed != -1;
    }

    private static int pack(int weight, boolean italic) {
        return (italic ? 65536 : 0) | weight;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static final int analyzeStyle(ByteBuffer buffer, int ttcIndex, FontVariationAxis[] varSettings) {
        int italic;
        int italic2;
        int weight = -1;
        int italic3 = -1;
        if (varSettings != null) {
            int length = varSettings.length;
            int i = 0;
            while (i < length) {
                FontVariationAxis axis = varSettings[i];
                if ("wght".equals(axis.getTag())) {
                    weight = (int) axis.getStyleValue();
                } else if ("ital".equals(axis.getTag())) {
                    italic3 = axis.getStyleValue() == 1.0f ? 1 : 0;
                }
                i++;
                weight = weight;
            }
            italic = italic3;
            italic2 = weight;
        } else {
            italic = -1;
            italic2 = -1;
        }
        if (italic2 != -1 && italic != -1) {
            return pack(italic2, italic == 1);
        }
        ByteOrder originalOrder = buffer.order();
        buffer.order(ByteOrder.BIG_ENDIAN);
        int fontFileOffset = 0;
        try {
            int magicNumber = buffer.getInt(0);
            if (magicNumber == TTC_TAG) {
                if (ttcIndex >= buffer.getInt(8)) {
                    return -1;
                }
                fontFileOffset = buffer.getInt((ttcIndex * 4) + 12);
            }
            int sfntVersion = buffer.getInt(fontFileOffset);
            if (sfntVersion == 65536 || sfntVersion == SFNT_VERSION_OTTO) {
                int numTables = buffer.getShort(fontFileOffset + 4);
                int os2TableOffset = -1;
                int i2 = 0;
                while (true) {
                    if (i2 >= numTables) {
                        break;
                    }
                    int tableOffset = fontFileOffset + 12 + (i2 * 16);
                    if (buffer.getInt(tableOffset) == OS2_TABLE_TAG) {
                        os2TableOffset = buffer.getInt(tableOffset + 8);
                        break;
                    }
                    i2++;
                }
                if (os2TableOffset == -1) {
                    return pack(400, false);
                }
                boolean z = false;
                int weightFromOS2 = buffer.getShort(os2TableOffset + 4);
                boolean italicFromOS2 = (buffer.getShort(os2TableOffset + 62) & 1) != 0;
                int i3 = italic2 == -1 ? weightFromOS2 : italic2;
                if (italic == -1) {
                    z = italicFromOS2;
                } else if (italic == 1) {
                    z = true;
                }
                return pack(i3, z);
            }
            return -1;
        } finally {
            buffer.order(originalOrder);
        }
    }

    public static long getRevision(ByteBuffer buffer, int index) {
        return nGetFontRevision(buffer, index);
    }

    public static String getPostScriptName(ByteBuffer buffer, int index) {
        return nGetFontPostScriptName(buffer, index);
    }

    public static int isPostScriptType1Font(ByteBuffer buffer, int index) {
        return nIsPostScriptType1Font(buffer, index);
    }

    public static int isCollectionFont(ByteBuffer buffer) {
        ByteBuffer copied = buffer.slice();
        copied.order(ByteOrder.BIG_ENDIAN);
        int magicNumber = copied.getInt(0);
        if (magicNumber == TTC_TAG) {
            return 1;
        }
        if (magicNumber == 65536 || magicNumber == SFNT_VERSION_OTTO) {
            return 0;
        }
        return -1;
    }
}
