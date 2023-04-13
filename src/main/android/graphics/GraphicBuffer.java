package android.graphics;

import android.hardware.HardwareBuffer;
import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes.dex */
public class GraphicBuffer implements Parcelable {
    public static final Parcelable.Creator<GraphicBuffer> CREATOR = new Parcelable.Creator<GraphicBuffer>() { // from class: android.graphics.GraphicBuffer.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GraphicBuffer createFromParcel(Parcel in) {
            int width = in.readInt();
            int height = in.readInt();
            int format = in.readInt();
            int usage = in.readInt();
            long nativeObject = GraphicBuffer.nReadGraphicBufferFromParcel(in);
            if (nativeObject != 0) {
                return new GraphicBuffer(width, height, format, usage, nativeObject);
            }
            return null;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GraphicBuffer[] newArray(int size) {
            return new GraphicBuffer[size];
        }
    };
    public static final int USAGE_HW_2D = 1024;
    public static final int USAGE_HW_COMPOSER = 2048;
    public static final int USAGE_HW_MASK = 466688;
    public static final int USAGE_HW_RENDER = 512;
    public static final int USAGE_HW_TEXTURE = 256;
    public static final int USAGE_HW_VIDEO_ENCODER = 65536;
    public static final int USAGE_PROTECTED = 16384;
    public static final int USAGE_SOFTWARE_MASK = 255;
    public static final int USAGE_SW_READ_MASK = 15;
    public static final int USAGE_SW_READ_NEVER = 0;
    public static final int USAGE_SW_READ_OFTEN = 3;
    public static final int USAGE_SW_READ_RARELY = 2;
    public static final int USAGE_SW_WRITE_MASK = 240;
    public static final int USAGE_SW_WRITE_NEVER = 0;
    public static final int USAGE_SW_WRITE_OFTEN = 48;
    public static final int USAGE_SW_WRITE_RARELY = 32;
    private Canvas mCanvas;
    private boolean mDestroyed;
    private final int mFormat;
    private final int mHeight;
    private final long mNativeObject;
    private int mSaveCount;
    private final int mUsage;
    private final int mWidth;

    private static native GraphicBuffer nCreateFromHardwareBuffer(HardwareBuffer hardwareBuffer);

    private static native long nCreateGraphicBuffer(int i, int i2, int i3, int i4);

    private static native void nDestroyGraphicBuffer(long j);

    private static native boolean nLockCanvas(long j, Canvas canvas, Rect rect);

    /* JADX INFO: Access modifiers changed from: private */
    public static native long nReadGraphicBufferFromParcel(Parcel parcel);

    private static native boolean nUnlockCanvasAndPost(long j, Canvas canvas);

    private static native void nWriteGraphicBufferToParcel(long j, Parcel parcel);

    public static GraphicBuffer create(int width, int height, int format, int usage) {
        long nativeObject = nCreateGraphicBuffer(width, height, format, usage);
        if (nativeObject != 0) {
            return new GraphicBuffer(width, height, format, usage, nativeObject);
        }
        return null;
    }

    private GraphicBuffer(int width, int height, int format, int usage, long nativeObject) {
        this.mWidth = width;
        this.mHeight = height;
        this.mFormat = format;
        this.mUsage = usage;
        this.mNativeObject = nativeObject;
    }

    public static final GraphicBuffer createFromHardwareBuffer(HardwareBuffer buffer) {
        return nCreateFromHardwareBuffer(buffer);
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public int getFormat() {
        return this.mFormat;
    }

    public int getUsage() {
        return this.mUsage;
    }

    public Canvas lockCanvas() {
        return lockCanvas(null);
    }

    public Canvas lockCanvas(Rect dirty) {
        if (this.mDestroyed) {
            return null;
        }
        if (this.mCanvas == null) {
            this.mCanvas = new Canvas();
        }
        if (nLockCanvas(this.mNativeObject, this.mCanvas, dirty)) {
            this.mSaveCount = this.mCanvas.save();
            return this.mCanvas;
        }
        return null;
    }

    public void unlockCanvasAndPost(Canvas canvas) {
        Canvas canvas2;
        if (!this.mDestroyed && (canvas2 = this.mCanvas) != null && canvas == canvas2) {
            canvas.restoreToCount(this.mSaveCount);
            this.mSaveCount = 0;
            nUnlockCanvasAndPost(this.mNativeObject, this.mCanvas);
        }
    }

    public void destroy() {
        if (!this.mDestroyed) {
            this.mDestroyed = true;
            nDestroyGraphicBuffer(this.mNativeObject);
        }
    }

    public boolean isDestroyed() {
        return this.mDestroyed;
    }

    protected void finalize() throws Throwable {
        try {
            if (!this.mDestroyed) {
                nDestroyGraphicBuffer(this.mNativeObject);
            }
        } finally {
            super.finalize();
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        if (this.mDestroyed) {
            throw new IllegalStateException("This GraphicBuffer has been destroyed and cannot be written to a parcel.");
        }
        dest.writeInt(this.mWidth);
        dest.writeInt(this.mHeight);
        dest.writeInt(this.mFormat);
        dest.writeInt(this.mUsage);
        nWriteGraphicBufferToParcel(this.mNativeObject, dest);
    }
}
