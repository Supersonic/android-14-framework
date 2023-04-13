package android.hardware;

import android.graphics.GraphicBuffer;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.provider.Telephony;
import dalvik.annotation.optimization.CriticalNative;
import dalvik.annotation.optimization.FastNative;
import dalvik.system.CloseGuard;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import libcore.util.NativeAllocationRegistry;
/* loaded from: classes.dex */
public final class HardwareBuffer implements Parcelable, AutoCloseable {
    public static final int BLOB = 33;
    public static final Parcelable.Creator<HardwareBuffer> CREATOR = new Parcelable.Creator<HardwareBuffer>() { // from class: android.hardware.HardwareBuffer.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public HardwareBuffer createFromParcel(Parcel in) {
            long nativeObject = HardwareBuffer.nReadHardwareBufferFromParcel(in);
            if (nativeObject != 0) {
                return new HardwareBuffer(nativeObject);
            }
            return null;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public HardwareBuffer[] newArray(int size) {
            return new HardwareBuffer[size];
        }
    };
    public static final int DS_24UI8 = 50;
    public static final int DS_FP32UI8 = 52;
    public static final int D_16 = 48;
    public static final int D_24 = 49;
    public static final int D_FP32 = 51;
    public static final int RGBA_1010102 = 43;
    public static final int RGBA_8888 = 1;
    public static final int RGBA_FP16 = 22;
    public static final int RGBX_8888 = 2;
    public static final int RGB_565 = 4;
    public static final int RGB_888 = 3;
    public static final int S_UI8 = 53;
    public static final long USAGE_COMPOSER_OVERLAY = 2048;
    public static final long USAGE_CPU_READ_OFTEN = 3;
    public static final long USAGE_CPU_READ_RARELY = 2;
    public static final long USAGE_CPU_WRITE_OFTEN = 48;
    public static final long USAGE_CPU_WRITE_RARELY = 32;
    public static final long USAGE_FRONT_BUFFER = 4294967296L;
    public static final long USAGE_GPU_COLOR_OUTPUT = 512;
    public static final long USAGE_GPU_CUBE_MAP = 33554432;
    public static final long USAGE_GPU_DATA_BUFFER = 16777216;
    public static final long USAGE_GPU_MIPMAP_COMPLETE = 67108864;
    public static final long USAGE_GPU_SAMPLED_IMAGE = 256;
    public static final long USAGE_PROTECTED_CONTENT = 16384;
    public static final long USAGE_SENSOR_DIRECT_DATA = 8388608;
    public static final long USAGE_VIDEO_ENCODE = 65536;
    public static final int YCBCR_420_888 = 35;
    public static final int YCBCR_P010 = 54;
    private Runnable mCleaner;
    private final CloseGuard mCloseGuard;
    private long mNativeObject;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Format {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Usage {
    }

    private static native long nCreateFromGraphicBuffer(GraphicBuffer graphicBuffer);

    private static native long nCreateHardwareBuffer(int i, int i2, int i3, int i4, long j);

    @CriticalNative
    private static native long nEstimateSize(long j);

    @FastNative
    private static native int nGetFormat(long j);

    @FastNative
    private static native int nGetHeight(long j);

    @CriticalNative
    private static native long nGetId(long j);

    @FastNative
    private static native int nGetLayers(long j);

    private static native long nGetNativeFinalizer();

    @FastNative
    private static native long nGetUsage(long j);

    @FastNative
    private static native int nGetWidth(long j);

    private static native boolean nIsSupported(int i, int i2, int i3, int i4, long j);

    /* JADX INFO: Access modifiers changed from: private */
    public static native long nReadHardwareBufferFromParcel(Parcel parcel);

    private static native void nWriteHardwareBufferToParcel(long j, Parcel parcel);

    public static HardwareBuffer create(int width, int height, int format, int layers, long usage) {
        if (width <= 0) {
            throw new IllegalArgumentException("Invalid width " + width);
        }
        if (height <= 0) {
            throw new IllegalArgumentException("Invalid height " + height);
        }
        if (layers <= 0) {
            throw new IllegalArgumentException("Invalid layer count " + layers);
        }
        if (format == 33 && height != 1) {
            throw new IllegalArgumentException("Height must be 1 when using the BLOB format");
        }
        long nativeObject = nCreateHardwareBuffer(width, height, format, layers, usage);
        if (nativeObject == 0) {
            throw new IllegalArgumentException("Unable to create a HardwareBuffer, either the dimensions passed were too large, too many image layers were requested, or an invalid set of usage flags or invalid format was passed");
        }
        return new HardwareBuffer(nativeObject);
    }

    public static boolean isSupported(int width, int height, int format, int layers, long usage) {
        if (width <= 0) {
            throw new IllegalArgumentException("Invalid width " + width);
        }
        if (height <= 0) {
            throw new IllegalArgumentException("Invalid height " + height);
        }
        if (layers <= 0) {
            throw new IllegalArgumentException("Invalid layer count " + layers);
        }
        if (format == 33 && height != 1) {
            throw new IllegalArgumentException("Height must be 1 when using the BLOB format");
        }
        return nIsSupported(width, height, format, layers, usage);
    }

    public static HardwareBuffer createFromGraphicBuffer(GraphicBuffer graphicBuffer) {
        long nativeObject = nCreateFromGraphicBuffer(graphicBuffer);
        return new HardwareBuffer(nativeObject);
    }

    private HardwareBuffer(long nativeObject) {
        CloseGuard closeGuard = CloseGuard.get();
        this.mCloseGuard = closeGuard;
        this.mNativeObject = nativeObject;
        long bufferSize = nEstimateSize(nativeObject);
        ClassLoader loader = HardwareBuffer.class.getClassLoader();
        NativeAllocationRegistry registry = new NativeAllocationRegistry(loader, nGetNativeFinalizer(), bufferSize);
        this.mCleaner = registry.registerNativeAllocation(this, this.mNativeObject);
        closeGuard.open("HardwareBuffer.close");
    }

    protected void finalize() throws Throwable {
        try {
            this.mCloseGuard.warnIfOpen();
            close();
        } finally {
            super.finalize();
        }
    }

    public int getWidth() {
        checkClosed("width");
        return nGetWidth(this.mNativeObject);
    }

    public int getHeight() {
        checkClosed("height");
        return nGetHeight(this.mNativeObject);
    }

    public int getFormat() {
        checkClosed(Telephony.CellBroadcasts.MESSAGE_FORMAT);
        return nGetFormat(this.mNativeObject);
    }

    public int getLayers() {
        checkClosed("layer count");
        return nGetLayers(this.mNativeObject);
    }

    public long getUsage() {
        checkClosed("usage");
        return nGetUsage(this.mNativeObject);
    }

    public long getId() {
        checkClosed("id");
        return nGetId(this.mNativeObject);
    }

    private void checkClosed(String name) {
        if (isClosed()) {
            throw new IllegalStateException("This HardwareBuffer has been closed and its " + name + " cannot be obtained.");
        }
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        if (!isClosed()) {
            this.mCloseGuard.close();
            this.mNativeObject = 0L;
            this.mCleaner.run();
            this.mCleaner = null;
        }
    }

    public boolean isClosed() {
        return this.mNativeObject == 0;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 1;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        if (isClosed()) {
            throw new IllegalStateException("This HardwareBuffer has been closed and cannot be written to a parcel.");
        }
        nWriteHardwareBufferToParcel(this.mNativeObject, dest);
    }
}
