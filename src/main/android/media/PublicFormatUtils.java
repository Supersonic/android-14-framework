package android.media;
/* loaded from: classes2.dex */
class PublicFormatUtils {
    private static native int nativeGetHalDataspace(int i);

    private static native int nativeGetHalFormat(int i);

    private static native int nativeGetPublicFormat(int i, int i2);

    PublicFormatUtils() {
    }

    public static int getHalFormat(int imageFormat) {
        return nativeGetHalFormat(imageFormat);
    }

    public static int getHalDataspace(int imageFormat) {
        return nativeGetHalDataspace(imageFormat);
    }

    public static int getPublicFormat(int imageFormat, int dataspace) {
        return nativeGetPublicFormat(imageFormat, dataspace);
    }
}
