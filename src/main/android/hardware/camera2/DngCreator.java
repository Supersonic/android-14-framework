package android.hardware.camera2;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.camera2.impl.CameraMetadataNative;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.location.Location;
import android.media.Image;
import android.p008os.SystemClock;
import android.text.format.Time;
import android.util.Log;
import android.util.Size;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
/* loaded from: classes.dex */
public final class DngCreator implements AutoCloseable {
    private static final int BYTES_PER_RGB_PIX = 3;
    private static final int DEFAULT_PIXEL_STRIDE = 2;
    private static final String GPS_DATE_FORMAT_STR = "yyyy:MM:dd";
    private static final String GPS_LAT_REF_NORTH = "N";
    private static final String GPS_LAT_REF_SOUTH = "S";
    private static final String GPS_LONG_REF_EAST = "E";
    private static final String GPS_LONG_REF_WEST = "W";
    public static final int MAX_THUMBNAIL_DIMENSION = 256;
    private static final String TAG = "DngCreator";
    private static final int TAG_ORIENTATION_UNKNOWN = 9;
    private static final String TIFF_DATETIME_FORMAT = "yyyy:MM:dd HH:mm:ss";
    private static final DateFormat sExifGPSDateStamp;
    private final Calendar mGPSTimeStampCalendar = Calendar.getInstance(TimeZone.getTimeZone(Time.TIMEZONE_UTC));
    private long mNativeContext;

    private static native void nativeClassInit();

    private native synchronized void nativeDestroy();

    private native synchronized void nativeInit(CameraMetadataNative cameraMetadataNative, CameraMetadataNative cameraMetadataNative2, String str);

    private native synchronized void nativeSetDescription(String str);

    private native synchronized void nativeSetGpsTags(int[] iArr, String str, int[] iArr2, String str2, String str3, int[] iArr3);

    private native synchronized void nativeSetOrientation(int i);

    private native synchronized void nativeSetThumbnail(ByteBuffer byteBuffer, int i, int i2);

    private native synchronized void nativeWriteImage(OutputStream outputStream, int i, int i2, ByteBuffer byteBuffer, int i3, int i4, long j, boolean z) throws IOException;

    private native synchronized void nativeWriteInputStream(OutputStream outputStream, InputStream inputStream, int i, int i2, long j) throws IOException;

    public DngCreator(CameraCharacteristics characteristics, CaptureResult metadata) {
        long timeOffset;
        if (characteristics == null || metadata == null) {
            throw new IllegalArgumentException("Null argument to DngCreator constructor");
        }
        long currentTime = System.currentTimeMillis();
        int timestampSource = ((Integer) characteristics.get(CameraCharacteristics.SENSOR_INFO_TIMESTAMP_SOURCE)).intValue();
        if (timestampSource == 1) {
            timeOffset = currentTime - SystemClock.elapsedRealtime();
        } else if (timestampSource == 0) {
            timeOffset = currentTime - SystemClock.uptimeMillis();
        } else {
            Log.m104w(TAG, "Sensor timestamp source is unexpected: " + timestampSource);
            timeOffset = currentTime - SystemClock.uptimeMillis();
        }
        Long timestamp = (Long) metadata.get(CaptureResult.SENSOR_TIMESTAMP);
        long captureTime = currentTime;
        captureTime = timestamp != null ? (timestamp.longValue() / 1000000) + timeOffset : captureTime;
        DateFormat dateTimeStampFormat = new SimpleDateFormat(TIFF_DATETIME_FORMAT, Locale.US);
        dateTimeStampFormat.setTimeZone(TimeZone.getDefault());
        String formattedCaptureTime = dateTimeStampFormat.format(Long.valueOf(captureTime));
        nativeInit(characteristics.getNativeCopy(), metadata.getNativeCopy(), formattedCaptureTime);
    }

    public DngCreator setOrientation(int orientation) {
        if (orientation < 0 || orientation > 8) {
            throw new IllegalArgumentException("Orientation " + orientation + " is not a valid EXIF orientation value");
        }
        if (orientation == 0) {
            orientation = 9;
        }
        nativeSetOrientation(orientation);
        return this;
    }

    public DngCreator setThumbnail(Bitmap pixels) {
        if (pixels == null) {
            throw new IllegalArgumentException("Null argument to setThumbnail");
        }
        int width = pixels.getWidth();
        int height = pixels.getHeight();
        if (width > 256 || height > 256) {
            throw new IllegalArgumentException("Thumbnail dimensions width,height (" + width + "," + height + ") too large, dimensions must be smaller than 256");
        }
        ByteBuffer rgbBuffer = convertToRGB(pixels);
        nativeSetThumbnail(rgbBuffer, width, height);
        return this;
    }

    public DngCreator setThumbnail(Image pixels) {
        if (pixels == null) {
            throw new IllegalArgumentException("Null argument to setThumbnail");
        }
        int format = pixels.getFormat();
        if (format != 35) {
            throw new IllegalArgumentException("Unsupported Image format " + format);
        }
        int width = pixels.getWidth();
        int height = pixels.getHeight();
        if (width > 256 || height > 256) {
            throw new IllegalArgumentException("Thumbnail dimensions width,height (" + width + "," + height + ") too large, dimensions must be smaller than 256");
        }
        ByteBuffer rgbBuffer = convertToRGB(pixels);
        nativeSetThumbnail(rgbBuffer, width, height);
        return this;
    }

    public DngCreator setLocation(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Null location passed to setLocation");
        }
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        long time = location.getTime();
        int[] latTag = toExifLatLong(latitude);
        int[] longTag = toExifLatLong(longitude);
        String latRef = latitude >= 0.0d ? "N" : "S";
        String longRef = longitude >= 0.0d ? GPS_LONG_REF_EAST : "W";
        String dateTag = sExifGPSDateStamp.format(Long.valueOf(time));
        this.mGPSTimeStampCalendar.setTimeInMillis(time);
        int[] timeTag = {this.mGPSTimeStampCalendar.get(11), 1, this.mGPSTimeStampCalendar.get(12), 1, this.mGPSTimeStampCalendar.get(13), 1};
        nativeSetGpsTags(latTag, latRef, longTag, longRef, dateTag, timeTag);
        return this;
    }

    public DngCreator setDescription(String description) {
        if (description == null) {
            throw new IllegalArgumentException("Null description passed to setDescription.");
        }
        nativeSetDescription(description);
        return this;
    }

    public void writeInputStream(OutputStream dngOutput, Size size, InputStream pixels, long offset) throws IOException {
        if (dngOutput == null) {
            throw new IllegalArgumentException("Null dngOutput passed to writeInputStream");
        }
        if (size == null) {
            throw new IllegalArgumentException("Null size passed to writeInputStream");
        }
        if (pixels == null) {
            throw new IllegalArgumentException("Null pixels passed to writeInputStream");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Negative offset passed to writeInputStream");
        }
        int width = size.getWidth();
        int height = size.getHeight();
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Size with invalid width, height: (" + width + "," + height + ") passed to writeInputStream");
        }
        nativeWriteInputStream(dngOutput, pixels, width, height, offset);
    }

    public void writeByteBuffer(OutputStream dngOutput, Size size, ByteBuffer pixels, long offset) throws IOException {
        if (dngOutput == null) {
            throw new IllegalArgumentException("Null dngOutput passed to writeByteBuffer");
        }
        if (size == null) {
            throw new IllegalArgumentException("Null size passed to writeByteBuffer");
        }
        if (pixels == null) {
            throw new IllegalArgumentException("Null pixels passed to writeByteBuffer");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Negative offset passed to writeByteBuffer");
        }
        int width = size.getWidth();
        int height = size.getHeight();
        writeByteBuffer(width, height, pixels, dngOutput, 2, width * 2, offset);
    }

    public void writeImage(OutputStream dngOutput, Image pixels) throws IOException {
        if (dngOutput == null) {
            throw new IllegalArgumentException("Null dngOutput to writeImage");
        }
        if (pixels == null) {
            throw new IllegalArgumentException("Null pixels to writeImage");
        }
        int format = pixels.getFormat();
        if (format != 32) {
            throw new IllegalArgumentException("Unsupported image format " + format);
        }
        Image.Plane[] planes = pixels.getPlanes();
        if (planes == null || planes.length <= 0) {
            throw new IllegalArgumentException("Image with no planes passed to writeImage");
        }
        ByteBuffer buf = planes[0].getBuffer();
        writeByteBuffer(pixels.getWidth(), pixels.getHeight(), buf, dngOutput, planes[0].getPixelStride(), planes[0].getRowStride(), 0L);
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        nativeDestroy();
    }

    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    static {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(GPS_DATE_FORMAT_STR, Locale.US);
        sExifGPSDateStamp = simpleDateFormat;
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(Time.TIMEZONE_UTC));
        nativeClassInit();
    }

    private void writeByteBuffer(int width, int height, ByteBuffer pixels, OutputStream dngOutput, int pixelStride, int rowStride, long offset) throws IOException {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Image with invalid width, height: (" + width + "," + height + ") passed to write");
        }
        long capacity = pixels.capacity();
        long totalSize = (rowStride * height) + offset;
        if (capacity < totalSize) {
            throw new IllegalArgumentException("Image size " + capacity + " is too small (must be larger than " + totalSize + NavigationBarInflaterView.KEY_CODE_END);
        }
        int minRowStride = pixelStride * width;
        if (minRowStride > rowStride) {
            throw new IllegalArgumentException("Invalid image pixel stride, row byte width " + minRowStride + " is too large, expecting " + rowStride);
        }
        pixels.clear();
        nativeWriteImage(dngOutput, width, height, pixels, rowStride, pixelStride, offset, pixels.isDirect());
        pixels.clear();
    }

    private static void yuvToRgb(byte[] yuvData, int outOffset, byte[] rgbOut) {
        float y = yuvData[0] & 255;
        float cb = yuvData[1] & 255;
        float cr = yuvData[2] & 255;
        float r = ((cr - 128.0f) * 1.402f) + y;
        float g = (y - ((cb - 128.0f) * 0.34414f)) - ((cr - 128.0f) * 0.71414f);
        float b = ((cb - 128.0f) * 1.772f) + y;
        rgbOut[outOffset] = (byte) Math.max(0.0f, Math.min(255.0f, r));
        rgbOut[outOffset + 1] = (byte) Math.max(0.0f, Math.min(255.0f, g));
        rgbOut[outOffset + 2] = (byte) Math.max(0.0f, Math.min(255.0f, b));
    }

    private static void colorToRgb(int color, int outOffset, byte[] rgbOut) {
        rgbOut[outOffset] = (byte) Color.red(color);
        rgbOut[outOffset + 1] = (byte) Color.green(color);
        rgbOut[outOffset + 2] = (byte) Color.blue(color);
    }

    private static ByteBuffer convertToRGB(Image yuvImage) {
        int width = yuvImage.getWidth();
        int height = yuvImage.getHeight();
        ByteBuffer buf = ByteBuffer.allocateDirect(width * 3 * height);
        Image.Plane yPlane = yuvImage.getPlanes()[0];
        Image.Plane uPlane = yuvImage.getPlanes()[1];
        Image.Plane vPlane = yuvImage.getPlanes()[2];
        ByteBuffer yBuf = yPlane.getBuffer();
        ByteBuffer uBuf = uPlane.getBuffer();
        ByteBuffer vBuf = vPlane.getBuffer();
        yBuf.rewind();
        uBuf.rewind();
        vBuf.rewind();
        int yRowStride = yPlane.getRowStride();
        int vRowStride = vPlane.getRowStride();
        int uRowStride = uPlane.getRowStride();
        int yPixStride = yPlane.getPixelStride();
        int vPixStride = vPlane.getPixelStride();
        int uPixStride = uPlane.getPixelStride();
        byte[] yuvPixel = {0, 0, 0};
        byte[] yFullRow = new byte[((width - 1) * yPixStride) + 1];
        byte[] uFullRow = new byte[(((width / 2) - 1) * uPixStride) + 1];
        byte[] vFullRow = new byte[(((width / 2) - 1) * vPixStride) + 1];
        byte[] finalRow = new byte[width * 3];
        int i = 0;
        while (i < height) {
            int halfH = i / 2;
            int height2 = height;
            yBuf.position(yRowStride * i);
            yBuf.get(yFullRow);
            uBuf.position(uRowStride * halfH);
            uBuf.get(uFullRow);
            vBuf.position(vRowStride * halfH);
            vBuf.get(vFullRow);
            int j = 0;
            while (j < width) {
                int halfW = j / 2;
                yuvPixel[0] = yFullRow[yPixStride * j];
                yuvPixel[1] = uFullRow[uPixStride * halfW];
                yuvPixel[2] = vFullRow[vPixStride * halfW];
                yuvToRgb(yuvPixel, j * 3, finalRow);
                j++;
                width = width;
            }
            buf.put(finalRow);
            i++;
            height = height2;
        }
        yBuf.rewind();
        uBuf.rewind();
        vBuf.rewind();
        buf.rewind();
        return buf;
    }

    private static ByteBuffer convertToRGB(Bitmap argbBitmap) {
        int width = argbBitmap.getWidth();
        int height = argbBitmap.getHeight();
        ByteBuffer buf = ByteBuffer.allocateDirect(width * 3 * height);
        int[] pixelRow = new int[width];
        byte[] finalRow = new byte[width * 3];
        for (int i = 0; i < height; i++) {
            argbBitmap.getPixels(pixelRow, 0, width, 0, i, width, 1);
            for (int j = 0; j < width; j++) {
                colorToRgb(pixelRow[j], j * 3, finalRow);
            }
            buf.put(finalRow);
        }
        buf.rewind();
        return buf;
    }

    private static int[] toExifLatLong(double value) {
        double value2 = Math.abs(value);
        int degrees = (int) value2;
        double value3 = (value2 - degrees) * 60.0d;
        int minutes = (int) value3;
        int seconds = (int) ((value3 - minutes) * 6000.0d);
        return new int[]{degrees, 1, minutes, 1, seconds, 100};
    }
}
