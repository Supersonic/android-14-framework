package android.graphics;

import android.graphics.ColorSpace;
import java.io.OutputStream;
/* loaded from: classes.dex */
public class YuvImage {
    private static final int WORKING_COMPRESS_STORAGE = 4096;
    private static final String[] sSupportedFormats = {"NV21", "YUY2", "YCBCR_P010", "YUV_420_888"};
    private static final ColorSpace.Named[] sSupportedJpegRHdrColorSpaces = {ColorSpace.Named.BT2020_HLG, ColorSpace.Named.BT2020_PQ};
    private static final ColorSpace.Named[] sSupportedJpegRSdrColorSpaces = {ColorSpace.Named.SRGB, ColorSpace.Named.DISPLAY_P3};
    private ColorSpace mColorSpace;
    private byte[] mData;
    private int mFormat;
    private int mHeight;
    private int[] mStrides;
    private int mWidth;

    private static native boolean nativeCompressToJpeg(byte[] bArr, int i, int i2, int i3, int[] iArr, int[] iArr2, int i4, OutputStream outputStream, byte[] bArr2);

    private static native boolean nativeCompressToJpegR(byte[] bArr, int i, byte[] bArr2, int i2, int i3, int i4, int i5, OutputStream outputStream, byte[] bArr3);

    private static String printSupportedFormats() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (true) {
            String[] strArr = sSupportedFormats;
            if (i < strArr.length) {
                sb.append(strArr[i]);
                if (i != strArr.length - 1) {
                    sb.append(", ");
                }
                i++;
            } else {
                return sb.toString();
            }
        }
    }

    private static String printSupportedJpegRColorSpaces(boolean isHdr) {
        ColorSpace.Named[] colorSpaces = isHdr ? sSupportedJpegRHdrColorSpaces : sSupportedJpegRSdrColorSpaces;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < colorSpaces.length; i++) {
            sb.append(ColorSpace.get(colorSpaces[i]).getName());
            if (i != colorSpaces.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    private static boolean isSupportedJpegRColorSpace(boolean isHdr, int colorSpace) {
        ColorSpace.Named[] colorSpaces = isHdr ? sSupportedJpegRHdrColorSpaces : sSupportedJpegRSdrColorSpaces;
        for (ColorSpace.Named cs : colorSpaces) {
            if (cs.ordinal() == colorSpace) {
                return true;
            }
        }
        return false;
    }

    public YuvImage(byte[] yuv, int format, int width, int height, int[] strides) {
        this(yuv, format, width, height, strides, ColorSpace.get(ColorSpace.Named.SRGB));
    }

    public YuvImage(byte[] yuv, int format, int width, int height, int[] strides, ColorSpace colorSpace) {
        if (format != 17 && format != 20 && format != 54 && format != 35) {
            throw new IllegalArgumentException("only supports the following ImageFormat:" + printSupportedFormats());
        }
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("width and height must large than 0");
        }
        if (yuv == null) {
            throw new IllegalArgumentException("yuv cannot be null");
        }
        if (colorSpace == null) {
            throw new IllegalArgumentException("ColorSpace cannot be null");
        }
        if (strides == null) {
            this.mStrides = calculateStrides(width, format);
        } else {
            this.mStrides = strides;
        }
        this.mData = yuv;
        this.mFormat = format;
        this.mWidth = width;
        this.mHeight = height;
        this.mColorSpace = colorSpace;
    }

    public boolean compressToJpeg(Rect rectangle, int quality, OutputStream stream) {
        int i = this.mFormat;
        if (i != 17 && i != 20) {
            throw new IllegalArgumentException("Only ImageFormat.NV21 and ImageFormat.YUY2 are supported.");
        }
        if (this.mColorSpace.getId() != ColorSpace.Named.SRGB.ordinal()) {
            throw new IllegalArgumentException("Only SRGB color space is supported.");
        }
        Rect wholeImage = new Rect(0, 0, this.mWidth, this.mHeight);
        if (!wholeImage.contains(rectangle)) {
            throw new IllegalArgumentException("rectangle is not inside the image");
        }
        if (quality < 0 || quality > 100) {
            throw new IllegalArgumentException("quality must be 0..100");
        }
        if (stream == null) {
            throw new IllegalArgumentException("stream cannot be null");
        }
        adjustRectangle(rectangle);
        int[] offsets = calculateOffsets(rectangle.left, rectangle.top);
        return nativeCompressToJpeg(this.mData, this.mFormat, rectangle.width(), rectangle.height(), offsets, this.mStrides, quality, stream, new byte[4096]);
    }

    public boolean compressToJpegR(YuvImage sdr, int quality, OutputStream stream) {
        if (sdr == null) {
            throw new IllegalArgumentException("SDR input cannot be null");
        }
        if (this.mData.length == 0 || sdr.getYuvData().length == 0) {
            throw new IllegalArgumentException("Input images cannot be empty");
        }
        if (this.mFormat != 54 || sdr.getYuvFormat() != 35) {
            throw new IllegalArgumentException("only support ImageFormat.YCBCR_P010 and ImageFormat.YUV_420_888");
        }
        if (sdr.getWidth() != this.mWidth || sdr.getHeight() != this.mHeight) {
            throw new IllegalArgumentException("HDR and SDR resolution mismatch");
        }
        if (quality < 0 || quality > 100) {
            throw new IllegalArgumentException("quality must be 0..100");
        }
        if (stream == null) {
            throw new IllegalArgumentException("stream cannot be null");
        }
        if (!isSupportedJpegRColorSpace(true, this.mColorSpace.getId()) || !isSupportedJpegRColorSpace(false, sdr.getColorSpace().getId())) {
            throw new IllegalArgumentException("Not supported color space. SDR only supports: " + printSupportedJpegRColorSpaces(false) + "HDR only supports: " + printSupportedJpegRColorSpaces(true));
        }
        return nativeCompressToJpegR(this.mData, this.mColorSpace.getDataSpace(), sdr.getYuvData(), sdr.getColorSpace().getDataSpace(), this.mWidth, this.mHeight, quality, stream, new byte[4096]);
    }

    public byte[] getYuvData() {
        return this.mData;
    }

    public int getYuvFormat() {
        return this.mFormat;
    }

    public int[] getStrides() {
        return this.mStrides;
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public ColorSpace getColorSpace() {
        return this.mColorSpace;
    }

    int[] calculateOffsets(int left, int top) {
        int i = this.mFormat;
        if (i == 17) {
            int[] iArr = this.mStrides;
            int i2 = iArr[0];
            int[] offsets = {(top * i2) + left, (this.mHeight * i2) + ((top / 2) * iArr[1]) + ((left / 2) * 2)};
            return offsets;
        } else if (i != 20) {
            return null;
        } else {
            int[] offsets2 = {(this.mStrides[0] * top) + ((left / 2) * 4)};
            return offsets2;
        }
    }

    private int[] calculateStrides(int width, int format) {
        switch (format) {
            case 17:
                int[] strides = {width, width};
                return strides;
            case 20:
                int[] strides2 = {width * 2};
                return strides2;
            case 35:
                int[] strides3 = {width, (width + 1) / 2, (width + 1) / 2};
                return strides3;
            case 54:
                int[] strides4 = {width * 2, width * 2};
                return strides4;
            default:
                throw new IllegalArgumentException("only supports the following ImageFormat:" + printSupportedFormats());
        }
    }

    private void adjustRectangle(Rect rect) {
        int width = rect.width();
        int height = rect.height();
        if (this.mFormat == 17) {
            width &= -2;
            rect.left &= -2;
            rect.top &= -2;
            rect.right = rect.left + width;
            rect.bottom = rect.top + (height & (-2));
        }
        if (this.mFormat == 20) {
            rect.left &= -2;
            rect.right = rect.left + (width & (-2));
        }
    }
}
