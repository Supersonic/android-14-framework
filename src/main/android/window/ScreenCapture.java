package android.window;

import android.graphics.Bitmap;
import android.graphics.ColorSpace;
import android.graphics.Rect;
import android.hardware.HardwareBuffer;
import android.p008os.IBinder;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.Log;
import android.util.Pair;
import android.view.SurfaceControl;
import android.window.ScreenCapture;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import libcore.util.NativeAllocationRegistry;
/* loaded from: classes4.dex */
public class ScreenCapture {
    private static final int SCREENSHOT_WAIT_TIME_S = 1;
    private static final String TAG = "ScreenCapture";

    /* JADX INFO: Access modifiers changed from: private */
    public static native long getNativeListenerFinalizer();

    private static native int nativeCaptureDisplay(DisplayCaptureArgs displayCaptureArgs, long j);

    private static native int nativeCaptureLayers(LayerCaptureArgs layerCaptureArgs, long j);

    /* JADX INFO: Access modifiers changed from: private */
    public static native long nativeCreateScreenCaptureListener(Consumer<ScreenshotHardwareBuffer> consumer);

    /* JADX INFO: Access modifiers changed from: private */
    public static native long nativeReadListenerFromParcel(Parcel parcel);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeWriteListenerToParcel(long j, Parcel parcel);

    public static int captureDisplay(DisplayCaptureArgs captureArgs, ScreenCaptureListener captureListener) {
        return nativeCaptureDisplay(captureArgs, captureListener.mNativeObject);
    }

    public static ScreenshotHardwareBuffer captureDisplay(DisplayCaptureArgs captureArgs) {
        Pair<ScreenCaptureListener, ScreenshotSync> syncScreenCapture = createSyncCaptureListener();
        int status = captureDisplay(captureArgs, syncScreenCapture.first);
        if (status != 0) {
            return null;
        }
        try {
            return syncScreenCapture.second.get();
        } catch (Exception e) {
            return null;
        }
    }

    public static ScreenshotHardwareBuffer captureLayers(SurfaceControl layer, Rect sourceCrop, float frameScale) {
        return captureLayers(layer, sourceCrop, frameScale, 1);
    }

    public static ScreenshotHardwareBuffer captureLayers(SurfaceControl layer, Rect sourceCrop, float frameScale, int format) {
        LayerCaptureArgs captureArgs = new LayerCaptureArgs.Builder(layer).setSourceCrop(sourceCrop).setFrameScale(frameScale).setPixelFormat(format).build();
        return captureLayers(captureArgs);
    }

    public static ScreenshotHardwareBuffer captureLayers(LayerCaptureArgs captureArgs) {
        Pair<ScreenCaptureListener, ScreenshotSync> syncScreenCapture = createSyncCaptureListener();
        int status = captureLayers(captureArgs, syncScreenCapture.first);
        if (status != 0) {
            return null;
        }
        try {
            return syncScreenCapture.second.get();
        } catch (Exception e) {
            return null;
        }
    }

    public static ScreenshotHardwareBuffer captureLayersExcluding(SurfaceControl layer, Rect sourceCrop, float frameScale, int format, SurfaceControl[] exclude) {
        LayerCaptureArgs captureArgs = new LayerCaptureArgs.Builder(layer).setSourceCrop(sourceCrop).setFrameScale(frameScale).setPixelFormat(format).setExcludeLayers(exclude).build();
        return captureLayers(captureArgs);
    }

    public static int captureLayers(LayerCaptureArgs captureArgs, ScreenCaptureListener captureListener) {
        return nativeCaptureLayers(captureArgs, captureListener.mNativeObject);
    }

    /* loaded from: classes4.dex */
    public static class ScreenshotHardwareBuffer {
        private final ColorSpace mColorSpace;
        private final boolean mContainsHdrLayers;
        private final boolean mContainsSecureLayers;
        private final HardwareBuffer mHardwareBuffer;

        public ScreenshotHardwareBuffer(HardwareBuffer hardwareBuffer, ColorSpace colorSpace, boolean containsSecureLayers, boolean containsHdrLayers) {
            this.mHardwareBuffer = hardwareBuffer;
            this.mColorSpace = colorSpace;
            this.mContainsSecureLayers = containsSecureLayers;
            this.mContainsHdrLayers = containsHdrLayers;
        }

        private static ScreenshotHardwareBuffer createFromNative(HardwareBuffer hardwareBuffer, int namedColorSpace, boolean containsSecureLayers, boolean containsHdrLayers) {
            ColorSpace colorSpace = ColorSpace.get(ColorSpace.Named.values()[namedColorSpace]);
            return new ScreenshotHardwareBuffer(hardwareBuffer, colorSpace, containsSecureLayers, containsHdrLayers);
        }

        public ColorSpace getColorSpace() {
            return this.mColorSpace;
        }

        public HardwareBuffer getHardwareBuffer() {
            return this.mHardwareBuffer;
        }

        public boolean containsSecureLayers() {
            return this.mContainsSecureLayers;
        }

        public boolean containsHdrLayers() {
            return this.mContainsHdrLayers;
        }

        public Bitmap asBitmap() {
            HardwareBuffer hardwareBuffer = this.mHardwareBuffer;
            if (hardwareBuffer == null) {
                Log.m104w(ScreenCapture.TAG, "Failed to take screenshot. Null screenshot object");
                return null;
            }
            return Bitmap.wrapHardwareBuffer(hardwareBuffer, this.mColorSpace);
        }
    }

    /* loaded from: classes4.dex */
    public static class CaptureArgs implements Parcelable {
        public static final Parcelable.Creator<CaptureArgs> CREATOR = new Parcelable.Creator<CaptureArgs>() { // from class: android.window.ScreenCapture.CaptureArgs.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public CaptureArgs createFromParcel(Parcel in) {
                return new CaptureArgs(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public CaptureArgs[] newArray(int size) {
                return new CaptureArgs[size];
            }
        };
        public final boolean mAllowProtected;
        public final boolean mCaptureSecureLayers;
        public final float mFrameScaleX;
        public final float mFrameScaleY;
        public final boolean mGrayscale;
        public final int mPixelFormat;
        public final Rect mSourceCrop;
        public final long mUid;

        private CaptureArgs(Builder<? extends Builder<?>> builder) {
            Rect rect = new Rect();
            this.mSourceCrop = rect;
            this.mPixelFormat = ((Builder) builder).mPixelFormat;
            rect.set(((Builder) builder).mSourceCrop);
            this.mFrameScaleX = ((Builder) builder).mFrameScaleX;
            this.mFrameScaleY = ((Builder) builder).mFrameScaleY;
            this.mCaptureSecureLayers = ((Builder) builder).mCaptureSecureLayers;
            this.mAllowProtected = ((Builder) builder).mAllowProtected;
            this.mUid = ((Builder) builder).mUid;
            this.mGrayscale = ((Builder) builder).mGrayscale;
        }

        private CaptureArgs(Parcel in) {
            Rect rect = new Rect();
            this.mSourceCrop = rect;
            this.mPixelFormat = in.readInt();
            rect.readFromParcel(in);
            this.mFrameScaleX = in.readFloat();
            this.mFrameScaleY = in.readFloat();
            this.mCaptureSecureLayers = in.readBoolean();
            this.mAllowProtected = in.readBoolean();
            this.mUid = in.readLong();
            this.mGrayscale = in.readBoolean();
        }

        /* loaded from: classes4.dex */
        public static class Builder<T extends Builder<T>> {
            private boolean mAllowProtected;
            private boolean mCaptureSecureLayers;
            private boolean mGrayscale;
            private int mPixelFormat = 1;
            private final Rect mSourceCrop = new Rect();
            private float mFrameScaleX = 1.0f;
            private float mFrameScaleY = 1.0f;
            private long mUid = -1;

            public CaptureArgs build() {
                return new CaptureArgs(this);
            }

            public T setPixelFormat(int pixelFormat) {
                this.mPixelFormat = pixelFormat;
                return getThis();
            }

            public T setSourceCrop(Rect sourceCrop) {
                if (sourceCrop == null) {
                    this.mSourceCrop.setEmpty();
                } else {
                    this.mSourceCrop.set(sourceCrop);
                }
                return getThis();
            }

            public T setFrameScale(float frameScale) {
                this.mFrameScaleX = frameScale;
                this.mFrameScaleY = frameScale;
                return getThis();
            }

            public T setFrameScale(float frameScaleX, float frameScaleY) {
                this.mFrameScaleX = frameScaleX;
                this.mFrameScaleY = frameScaleY;
                return getThis();
            }

            public T setCaptureSecureLayers(boolean captureSecureLayers) {
                this.mCaptureSecureLayers = captureSecureLayers;
                return getThis();
            }

            public T setAllowProtected(boolean allowProtected) {
                this.mAllowProtected = allowProtected;
                return getThis();
            }

            public T setUid(long uid) {
                this.mUid = uid;
                return getThis();
            }

            public T setGrayscale(boolean grayscale) {
                this.mGrayscale = grayscale;
                return getThis();
            }

            T getThis() {
                return this;
            }
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.mPixelFormat);
            this.mSourceCrop.writeToParcel(dest, flags);
            dest.writeFloat(this.mFrameScaleX);
            dest.writeFloat(this.mFrameScaleY);
            dest.writeBoolean(this.mCaptureSecureLayers);
            dest.writeBoolean(this.mAllowProtected);
            dest.writeLong(this.mUid);
            dest.writeBoolean(this.mGrayscale);
        }
    }

    /* loaded from: classes4.dex */
    public static class DisplayCaptureArgs extends CaptureArgs {
        private final IBinder mDisplayToken;
        private final int mHeight;
        private final boolean mUseIdentityTransform;
        private final int mWidth;

        private DisplayCaptureArgs(Builder builder) {
            super(builder);
            this.mDisplayToken = builder.mDisplayToken;
            this.mWidth = builder.mWidth;
            this.mHeight = builder.mHeight;
            this.mUseIdentityTransform = builder.mUseIdentityTransform;
        }

        /* loaded from: classes4.dex */
        public static class Builder extends CaptureArgs.Builder<Builder> {
            private IBinder mDisplayToken;
            private int mHeight;
            private boolean mUseIdentityTransform;
            private int mWidth;

            @Override // android.window.ScreenCapture.CaptureArgs.Builder
            public DisplayCaptureArgs build() {
                if (this.mDisplayToken == null) {
                    throw new IllegalStateException("Can't take screenshot with null display token");
                }
                return new DisplayCaptureArgs(this);
            }

            public Builder(IBinder displayToken) {
                setDisplayToken(displayToken);
            }

            public Builder setDisplayToken(IBinder displayToken) {
                this.mDisplayToken = displayToken;
                return this;
            }

            public Builder setSize(int width, int height) {
                this.mWidth = width;
                this.mHeight = height;
                return this;
            }

            public Builder setUseIdentityTransform(boolean useIdentityTransform) {
                this.mUseIdentityTransform = useIdentityTransform;
                return this;
            }

            /* JADX INFO: Access modifiers changed from: package-private */
            @Override // android.window.ScreenCapture.CaptureArgs.Builder
            public Builder getThis() {
                return this;
            }
        }
    }

    /* loaded from: classes4.dex */
    public static class LayerCaptureArgs extends CaptureArgs {
        private final boolean mChildrenOnly;
        private final long[] mNativeExcludeLayers;
        private final long mNativeLayer;

        private LayerCaptureArgs(Builder builder) {
            super(builder);
            this.mChildrenOnly = builder.mChildrenOnly;
            this.mNativeLayer = builder.mLayer.mNativeObject;
            if (builder.mExcludeLayers != null) {
                this.mNativeExcludeLayers = new long[builder.mExcludeLayers.length];
                for (int i = 0; i < builder.mExcludeLayers.length; i++) {
                    this.mNativeExcludeLayers[i] = builder.mExcludeLayers[i].mNativeObject;
                }
                return;
            }
            this.mNativeExcludeLayers = null;
        }

        /* loaded from: classes4.dex */
        public static class Builder extends CaptureArgs.Builder<Builder> {
            private boolean mChildrenOnly = true;
            private SurfaceControl[] mExcludeLayers;
            private SurfaceControl mLayer;

            @Override // android.window.ScreenCapture.CaptureArgs.Builder
            public LayerCaptureArgs build() {
                if (this.mLayer == null) {
                    throw new IllegalStateException("Can't take screenshot with null layer");
                }
                return new LayerCaptureArgs(this);
            }

            public Builder(SurfaceControl layer, CaptureArgs args) {
                setLayer(layer);
                setPixelFormat(args.mPixelFormat);
                setSourceCrop(args.mSourceCrop);
                setFrameScale(args.mFrameScaleX, args.mFrameScaleY);
                setCaptureSecureLayers(args.mCaptureSecureLayers);
                setAllowProtected(args.mAllowProtected);
                setUid(args.mUid);
                setGrayscale(args.mGrayscale);
            }

            public Builder(SurfaceControl layer) {
                setLayer(layer);
            }

            public Builder setLayer(SurfaceControl layer) {
                this.mLayer = layer;
                return this;
            }

            public Builder setExcludeLayers(SurfaceControl[] excludeLayers) {
                this.mExcludeLayers = excludeLayers;
                return this;
            }

            public Builder setChildrenOnly(boolean childrenOnly) {
                this.mChildrenOnly = childrenOnly;
                return this;
            }

            /* JADX INFO: Access modifiers changed from: package-private */
            @Override // android.window.ScreenCapture.CaptureArgs.Builder
            public Builder getThis() {
                return this;
            }
        }
    }

    /* loaded from: classes4.dex */
    public static class ScreenCaptureListener implements Parcelable {
        private final long mNativeObject;
        private static final NativeAllocationRegistry sRegistry = NativeAllocationRegistry.createMalloced(ScreenCaptureListener.class.getClassLoader(), ScreenCapture.getNativeListenerFinalizer());
        public static final Parcelable.Creator<ScreenCaptureListener> CREATOR = new Parcelable.Creator<ScreenCaptureListener>() { // from class: android.window.ScreenCapture.ScreenCaptureListener.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public ScreenCaptureListener createFromParcel(Parcel in) {
                return new ScreenCaptureListener(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public ScreenCaptureListener[] newArray(int size) {
                return new ScreenCaptureListener[0];
            }
        };

        public ScreenCaptureListener(Consumer<ScreenshotHardwareBuffer> consumer) {
            long nativeCreateScreenCaptureListener = ScreenCapture.nativeCreateScreenCaptureListener(consumer);
            this.mNativeObject = nativeCreateScreenCaptureListener;
            sRegistry.registerNativeAllocation(this, nativeCreateScreenCaptureListener);
        }

        private ScreenCaptureListener(Parcel in) {
            if (in.readBoolean()) {
                long nativeReadListenerFromParcel = ScreenCapture.nativeReadListenerFromParcel(in);
                this.mNativeObject = nativeReadListenerFromParcel;
                sRegistry.registerNativeAllocation(this, nativeReadListenerFromParcel);
                return;
            }
            this.mNativeObject = 0L;
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            if (this.mNativeObject == 0) {
                dest.writeBoolean(false);
                return;
            }
            dest.writeBoolean(true);
            ScreenCapture.nativeWriteListenerToParcel(this.mNativeObject, dest);
        }
    }

    public static Pair<ScreenCaptureListener, ScreenshotSync> createSyncCaptureListener() {
        final ScreenshotSync screenshotSync = new ScreenshotSync();
        Objects.requireNonNull(screenshotSync);
        ScreenCaptureListener screenCaptureListener = new ScreenCaptureListener(new Consumer() { // from class: android.window.ScreenCapture$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ScreenCapture.ScreenshotSync.this.setScreenshotHardwareBuffer((ScreenCapture.ScreenshotHardwareBuffer) obj);
            }
        });
        return new Pair<>(screenCaptureListener, screenshotSync);
    }

    /* loaded from: classes4.dex */
    public static class ScreenshotSync {
        private final CountDownLatch mCountDownLatch = new CountDownLatch(1);
        private ScreenshotHardwareBuffer mScreenshotHardwareBuffer;

        /* JADX INFO: Access modifiers changed from: private */
        public void setScreenshotHardwareBuffer(ScreenshotHardwareBuffer screenshotHardwareBuffer) {
            this.mScreenshotHardwareBuffer = screenshotHardwareBuffer;
            this.mCountDownLatch.countDown();
        }

        public ScreenshotHardwareBuffer get() {
            try {
                this.mCountDownLatch.await(1L, TimeUnit.SECONDS);
                return this.mScreenshotHardwareBuffer;
            } catch (Exception e) {
                Log.m109e(ScreenCapture.TAG, "Failed to wait for screen capture result", e);
                return null;
            }
        }
    }
}
