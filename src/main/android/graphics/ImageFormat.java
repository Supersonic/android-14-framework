package android.graphics;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes.dex */
public class ImageFormat {
    public static final int DEPTH16 = 1144402265;
    public static final int DEPTH_JPEG = 1768253795;
    public static final int DEPTH_POINT_CLOUD = 257;
    public static final int FLEX_RGBA_8888 = 42;
    public static final int FLEX_RGB_888 = 41;
    public static final int HEIC = 1212500294;
    public static final int JPEG = 256;
    public static final int JPEG_R = 4101;
    public static final int NV16 = 16;
    public static final int NV21 = 17;
    public static final int PRIVATE = 34;
    public static final int RAW10 = 37;
    public static final int RAW12 = 38;
    public static final int RAW_DEPTH = 4098;
    public static final int RAW_DEPTH10 = 4099;
    public static final int RAW_PRIVATE = 36;
    public static final int RAW_SENSOR = 32;
    public static final int RGB_565 = 4;
    public static final int UNKNOWN = 0;
    public static final int Y16 = 540422489;

    /* renamed from: Y8 */
    public static final int f74Y8 = 538982489;
    public static final int YCBCR_P010 = 54;
    public static final int YUV_420_888 = 35;
    public static final int YUV_422_888 = 39;
    public static final int YUV_444_888 = 40;
    public static final int YUY2 = 20;
    public static final int YV12 = 842094169;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Format {
    }

    public static int getBitsPerPixel(int format) {
        switch (format) {
            case 4:
                return 16;
            case 16:
                return 16;
            case 17:
                return 12;
            case 20:
                return 16;
            case 32:
            case 4098:
                return 16;
            case 35:
                return 12;
            case 37:
            case 4099:
                return 10;
            case 38:
                return 12;
            case 39:
                return 16;
            case 40:
                return 24;
            case 41:
                return 24;
            case 42:
                return 32;
            case 54:
                return 24;
            case 538982489:
                return 8;
            case 540422489:
            case DEPTH16 /* 1144402265 */:
                return 16;
            case 842094169:
                return 12;
            default:
                return -1;
        }
    }

    public static boolean isPublicFormat(int format) {
        switch (format) {
            case 4:
            case 16:
            case 17:
            case 20:
            case 32:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 54:
            case 256:
            case 257:
            case 4098:
            case 4099:
            case 4101:
            case 538982489:
            case 842094169:
            case DEPTH16 /* 1144402265 */:
            case HEIC /* 1212500294 */:
            case DEPTH_JPEG /* 1768253795 */:
                return true;
            default:
                return false;
        }
    }
}
