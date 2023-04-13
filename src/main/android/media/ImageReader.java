package android.media;

import android.app.compat.CompatChanges;
import android.graphics.GraphicBuffer;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.HardwareBuffer;
import android.hardware.SyncFence;
import android.hardware.camera2.MultiResolutionImageReader;
import android.media.Image;
import android.p008os.Handler;
import android.p008os.Looper;
import android.p008os.ParcelFileDescriptor;
import android.view.Surface;
import dalvik.system.VMRuntime;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.NioUtils;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
/* loaded from: classes2.dex */
public class ImageReader implements AutoCloseable {
    private static final int ACQUIRE_MAX_IMAGES = 2;
    private static final int ACQUIRE_NO_BUFS = 1;
    private static final int ACQUIRE_SUCCESS = 0;
    private static final long DETACH_THROWS_ISE_ONLY = 236825255;
    private List<Image> mAcquiredImages;
    private final Object mCloseLock;
    private final int mDataSpace;
    private final boolean mDetachThrowsIseOnly;
    private int mEstimatedNativeAllocBytes;
    private final int mFormat;
    private final int mHardwareBufferFormat;
    private final int mHeight;
    private boolean mIsReaderValid;
    private OnImageAvailableListener mListener;
    private Executor mListenerExecutor;
    private ListenerHandler mListenerHandler;
    private final Object mListenerLock;
    private final int mMaxImages;
    private long mNativeContext;
    private final int mNumPlanes;
    private final MultiResolutionImageReader mParent;
    private Surface mSurface;
    private final long mUsage;
    private final int mWidth;

    /* loaded from: classes2.dex */
    public interface OnImageAvailableListener {
        void onImageAvailable(ImageReader imageReader);
    }

    private static native void nativeClassInit();

    private native synchronized void nativeClose();

    private static native synchronized ImagePlane[] nativeCreateImagePlanes(int i, GraphicBuffer graphicBuffer, int i2, int i3, int i4, int i5, int i6, int i7);

    private native synchronized int nativeDetachImage(Image image, boolean z);

    private native synchronized void nativeDiscardFreeBuffers();

    private native synchronized Surface nativeGetSurface();

    private native synchronized int nativeImageSetup(Image image);

    private native synchronized void nativeInit(Object obj, int i, int i2, int i3, long j, int i4, int i5);

    private native synchronized void nativeReleaseImage(Image image);

    private static native synchronized void nativeUnlockGraphicBuffer(GraphicBuffer graphicBuffer);

    public static ImageReader newInstance(int width, int height, int format, int maxImages) {
        return new ImageReader(width, height, format, maxImages, format == 34 ? 0L : 3L, null);
    }

    public static ImageReader newInstance(int width, int height, int format, int maxImages, long usage) {
        return new ImageReader(width, height, format, maxImages, usage, null);
    }

    public static ImageReader newInstance(int width, int height, int format, int maxImages, MultiResolutionImageReader parent) {
        return new ImageReader(width, height, format, maxImages, format == 34 ? 0L : 3L, parent);
    }

    private void initializeImageReader(int width, int height, int imageFormat, int maxImages, long usage, int hardwareBufferFormat, int dataSpace) {
        if (width < 1 || height < 1) {
            throw new IllegalArgumentException("The image dimensions must be positive");
        }
        if (maxImages < 1) {
            throw new IllegalArgumentException("Maximum outstanding image count must be at least 1");
        }
        if (imageFormat == 17) {
            throw new IllegalArgumentException("NV21 format is not supported");
        }
        nativeInit(new WeakReference(this), width, height, maxImages, usage, hardwareBufferFormat, dataSpace);
        this.mIsReaderValid = true;
        this.mSurface = nativeGetSurface();
        this.mEstimatedNativeAllocBytes = ImageUtils.getEstimatedNativeAllocBytes(width, height, imageFormat, 1);
        VMRuntime.getRuntime().registerNativeAllocation(this.mEstimatedNativeAllocBytes);
    }

    private ImageReader(int width, int height, int imageFormat, int maxImages, long usage, MultiResolutionImageReader parent) {
        this.mDetachThrowsIseOnly = CompatChanges.isChangeEnabled(DETACH_THROWS_ISE_ONLY);
        this.mListenerLock = new Object();
        this.mCloseLock = new Object();
        this.mIsReaderValid = false;
        this.mAcquiredImages = new CopyOnWriteArrayList();
        this.mWidth = width;
        this.mHeight = height;
        this.mFormat = imageFormat;
        this.mUsage = usage;
        this.mMaxImages = maxImages;
        this.mParent = parent;
        int halFormat = PublicFormatUtils.getHalFormat(imageFormat);
        this.mHardwareBufferFormat = halFormat;
        int halDataspace = PublicFormatUtils.getHalDataspace(imageFormat);
        this.mDataSpace = halDataspace;
        this.mNumPlanes = ImageUtils.getNumPlanesForFormat(imageFormat);
        initializeImageReader(width, height, imageFormat, maxImages, usage, halFormat, halDataspace);
    }

    private ImageReader(int width, int height, int maxImages, long usage, MultiResolutionImageReader parent, int hardwareBufferFormat, int dataSpace) {
        this.mDetachThrowsIseOnly = CompatChanges.isChangeEnabled(DETACH_THROWS_ISE_ONLY);
        this.mListenerLock = new Object();
        this.mCloseLock = new Object();
        this.mIsReaderValid = false;
        this.mAcquiredImages = new CopyOnWriteArrayList();
        this.mWidth = width;
        this.mHeight = height;
        this.mUsage = usage;
        this.mMaxImages = maxImages;
        this.mParent = parent;
        this.mHardwareBufferFormat = hardwareBufferFormat;
        this.mDataSpace = dataSpace;
        this.mNumPlanes = ImageUtils.getNumPlanesForHardwareBufferFormat(hardwareBufferFormat);
        int publicFormat = PublicFormatUtils.getPublicFormat(hardwareBufferFormat, dataSpace);
        this.mFormat = publicFormat;
        initializeImageReader(width, height, publicFormat, maxImages, usage, hardwareBufferFormat, dataSpace);
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public int getImageFormat() {
        return this.mFormat;
    }

    public int getHardwareBufferFormat() {
        return this.mHardwareBufferFormat;
    }

    public int getDataSpace() {
        return this.mDataSpace;
    }

    public int getMaxImages() {
        return this.mMaxImages;
    }

    public long getUsage() {
        return this.mUsage;
    }

    public Surface getSurface() {
        return this.mSurface;
    }

    public Image acquireLatestImage() {
        Image image = acquireNextImage();
        if (image == null) {
            return null;
        }
        while (true) {
            try {
                Image next = acquireNextImageNoThrowISE();
                if (next == null) {
                    break;
                }
                image.close();
                image = next;
            } finally {
                if (image != null) {
                    image.close();
                }
                MultiResolutionImageReader multiResolutionImageReader = this.mParent;
                if (multiResolutionImageReader != null) {
                    multiResolutionImageReader.flushOther(this);
                }
            }
        }
        Image result = image;
        image = null;
        return result;
    }

    public Image acquireNextImageNoThrowISE() {
        SurfaceImage si = new SurfaceImage(this.mFormat);
        if (acquireNextSurfaceImage(si) == 0) {
            return si;
        }
        return null;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private int acquireNextSurfaceImage(SurfaceImage si) {
        int status;
        synchronized (this.mCloseLock) {
            status = 1;
            if (this.mIsReaderValid) {
                status = nativeImageSetup(si);
            }
            switch (status) {
                case 0:
                    si.mIsImageValid = true;
                    break;
                case 1:
                case 2:
                    break;
                default:
                    throw new AssertionError("Unknown nativeImageSetup return code " + status);
            }
            if (status == 0) {
                this.mAcquiredImages.add(si);
            }
        }
        return status;
    }

    public Image acquireNextImage() {
        SurfaceImage si = new SurfaceImage(this.mFormat);
        int status = acquireNextSurfaceImage(si);
        switch (status) {
            case 0:
                return si;
            case 1:
                return null;
            case 2:
                throw new IllegalStateException(String.format("maxImages (%d) has already been acquired, call #close before acquiring more.", Integer.valueOf(this.mMaxImages)));
            default:
                throw new AssertionError("Unknown nativeImageSetup return code " + status);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void releaseImage(Image i) {
        if (!(i instanceof SurfaceImage)) {
            throw new IllegalArgumentException("This image was not produced by an ImageReader");
        }
        SurfaceImage si = (SurfaceImage) i;
        if (!si.mIsImageValid) {
            return;
        }
        if (si.getReader() != this || !this.mAcquiredImages.contains(i)) {
            throw new IllegalArgumentException("This image was not produced by this ImageReader");
        }
        si.clearSurfacePlanes();
        nativeReleaseImage(i);
        si.mIsImageValid = false;
        this.mAcquiredImages.remove(i);
    }

    public void setOnImageAvailableListener(OnImageAvailableListener listener, Handler handler) {
        synchronized (this.mListenerLock) {
            if (listener != null) {
                Looper looper = handler != null ? handler.getLooper() : Looper.myLooper();
                if (looper == null) {
                    throw new IllegalArgumentException("handler is null but the current thread is not a looper");
                }
                ListenerHandler listenerHandler = this.mListenerHandler;
                if (listenerHandler == null || listenerHandler.getLooper() != looper) {
                    ListenerHandler listenerHandler2 = new ListenerHandler(looper);
                    this.mListenerHandler = listenerHandler2;
                    this.mListenerExecutor = new HandlerExecutor(listenerHandler2);
                }
            } else {
                this.mListenerHandler = null;
                this.mListenerExecutor = null;
            }
            this.mListener = listener;
        }
    }

    public void setOnImageAvailableListenerWithExecutor(OnImageAvailableListener listener, Executor executor) {
        if (executor == null) {
            throw new IllegalArgumentException("executor must not be null");
        }
        synchronized (this.mListenerLock) {
            this.mListenerExecutor = executor;
            this.mListener = listener;
        }
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        setOnImageAvailableListener(null, null);
        Surface surface = this.mSurface;
        if (surface != null) {
            surface.release();
        }
        synchronized (this.mCloseLock) {
            this.mIsReaderValid = false;
            for (Image image : this.mAcquiredImages) {
                image.close();
            }
            this.mAcquiredImages.clear();
            nativeClose();
            if (this.mEstimatedNativeAllocBytes > 0) {
                VMRuntime.getRuntime().registerNativeFree(this.mEstimatedNativeAllocBytes);
                this.mEstimatedNativeAllocBytes = 0;
            }
        }
    }

    public void discardFreeBuffers() {
        synchronized (this.mCloseLock) {
            nativeDiscardFreeBuffers();
        }
    }

    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    public void detachImage(Image image) {
        if (image == null) {
            throw new IllegalArgumentException("input image must not be null");
        }
        if (!isImageOwnedbyMe(image)) {
            throw new IllegalArgumentException("Trying to detach an image that is not owned by this ImageReader");
        }
        SurfaceImage si = (SurfaceImage) image;
        si.throwISEIfImageIsInvalid();
        if (si.isAttachable()) {
            throw new IllegalStateException("Image was already detached from this ImageReader");
        }
        nativeDetachImage(image, this.mDetachThrowsIseOnly);
        si.clearSurfacePlanes();
        si.mPlanes = null;
        si.setDetached(true);
    }

    private boolean isImageOwnedbyMe(Image image) {
        if (image instanceof SurfaceImage) {
            SurfaceImage si = (SurfaceImage) image;
            return si.getReader() == this;
        }
        return false;
    }

    private static void postEventFromNative(Object selfRef) {
        Executor executor;
        final OnImageAvailableListener listener;
        boolean isReaderValid;
        WeakReference<ImageReader> weakSelf = (WeakReference) selfRef;
        final ImageReader ir = weakSelf.get();
        if (ir == null) {
            return;
        }
        synchronized (ir.mListenerLock) {
            executor = ir.mListenerExecutor;
            listener = ir.mListener;
        }
        synchronized (ir.mCloseLock) {
            isReaderValid = ir.mIsReaderValid;
        }
        if (executor != null && listener != null && isReaderValid) {
            executor.execute(new Runnable() { // from class: android.media.ImageReader.1
                @Override // java.lang.Runnable
                public void run() {
                    OnImageAvailableListener.this.onImageAvailable(ir);
                }
            });
        }
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private int mHeight;
        private int mWidth;
        private int mMaxImages = 1;
        private int mImageFormat = 0;
        private int mHardwareBufferFormat = 1;
        private int mDataSpace = 0;
        private long mUsage = 3;
        private boolean mUseLegacyImageFormat = false;

        public Builder(int width, int height) {
            this.mWidth = width;
            this.mHeight = height;
        }

        public Builder setMaxImages(int maxImages) {
            this.mMaxImages = maxImages;
            return this;
        }

        public Builder setUsage(long usage) {
            this.mUsage = usage;
            return this;
        }

        public Builder setImageFormat(int imageFormat) {
            this.mImageFormat = imageFormat;
            this.mUseLegacyImageFormat = true;
            this.mHardwareBufferFormat = 1;
            this.mDataSpace = 0;
            return this;
        }

        public Builder setDefaultHardwareBufferFormat(int hardwareBufferFormat) {
            this.mHardwareBufferFormat = hardwareBufferFormat;
            this.mUseLegacyImageFormat = false;
            this.mImageFormat = 0;
            return this;
        }

        public Builder setDefaultDataSpace(int dataSpace) {
            this.mDataSpace = dataSpace;
            this.mUseLegacyImageFormat = false;
            this.mImageFormat = 0;
            return this;
        }

        public ImageReader build() {
            if (this.mUseLegacyImageFormat) {
                return new ImageReader(this.mWidth, this.mHeight, this.mImageFormat, this.mMaxImages, this.mUsage, (MultiResolutionImageReader) null);
            }
            return new ImageReader(this.mWidth, this.mHeight, this.mMaxImages, this.mUsage, null, this.mHardwareBufferFormat, this.mDataSpace);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public final class ListenerHandler extends Handler {
        public ListenerHandler(Looper looper) {
            super(looper, null, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public final class HandlerExecutor implements Executor {
        private final Handler mHandler;

        public HandlerExecutor(Handler handler) {
            this.mHandler = (Handler) Objects.requireNonNull(handler);
        }

        @Override // java.util.concurrent.Executor
        public void execute(Runnable command) {
            this.mHandler.post(command);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class SurfaceImage extends Image {
        private int mDataSpace;
        private int mFormat;
        private int mHardwareBufferFormat;
        private long mNativeBuffer;
        private SurfacePlane[] mPlanes;
        private int mScalingMode;
        private long mTimestamp;
        private int mTransform;
        private final Object mCloseLock = new Object();
        private AtomicBoolean mIsDetached = new AtomicBoolean(false);

        private native synchronized SurfacePlane[] nativeCreatePlanes(int i, int i2, long j);

        private native synchronized int nativeGetFenceFd();

        private native synchronized int nativeGetFormat(int i);

        private native synchronized HardwareBuffer nativeGetHardwareBuffer();

        private native synchronized int nativeGetHeight();

        private native synchronized int nativeGetWidth();

        public SurfaceImage(int format) {
            this.mFormat = 0;
            this.mHardwareBufferFormat = 1;
            this.mDataSpace = 0;
            this.mFormat = format;
            this.mHardwareBufferFormat = ImageReader.this.mHardwareBufferFormat;
            this.mDataSpace = ImageReader.this.mDataSpace;
        }

        @Override // android.media.Image, java.lang.AutoCloseable
        public void close() {
            synchronized (this.mCloseLock) {
                ImageReader.this.releaseImage(this);
            }
        }

        public ImageReader getReader() {
            return ImageReader.this;
        }

        @Override // android.media.Image
        public int getFormat() {
            throwISEIfImageIsInvalid();
            int readerFormat = ImageReader.this.getImageFormat();
            int nativeGetFormat = readerFormat == 34 ? readerFormat : nativeGetFormat(readerFormat);
            this.mFormat = nativeGetFormat;
            return nativeGetFormat;
        }

        @Override // android.media.Image
        public int getWidth() {
            throwISEIfImageIsInvalid();
            switch (getFormat()) {
                case 36:
                case 256:
                case 257:
                case 4101:
                case ImageFormat.HEIC /* 1212500294 */:
                case ImageFormat.DEPTH_JPEG /* 1768253795 */:
                    int width = ImageReader.this.getWidth();
                    return width;
                default:
                    int width2 = nativeGetWidth();
                    return width2;
            }
        }

        @Override // android.media.Image
        public int getHeight() {
            throwISEIfImageIsInvalid();
            switch (getFormat()) {
                case 36:
                case 256:
                case 257:
                case 4101:
                case ImageFormat.HEIC /* 1212500294 */:
                case ImageFormat.DEPTH_JPEG /* 1768253795 */:
                    int height = ImageReader.this.getHeight();
                    return height;
                default:
                    int height2 = nativeGetHeight();
                    return height2;
            }
        }

        @Override // android.media.Image
        public long getTimestamp() {
            throwISEIfImageIsInvalid();
            return this.mTimestamp;
        }

        @Override // android.media.Image
        public int getTransform() {
            throwISEIfImageIsInvalid();
            return this.mTransform;
        }

        @Override // android.media.Image
        public int getScalingMode() {
            throwISEIfImageIsInvalid();
            return this.mScalingMode;
        }

        @Override // android.media.Image
        public int getPlaneCount() {
            throwISEIfImageIsInvalid();
            return ImageReader.this.mNumPlanes;
        }

        @Override // android.media.Image
        public SyncFence getFence() throws IOException {
            throwISEIfImageIsInvalid();
            int fence = nativeGetFenceFd();
            if (fence != -1) {
                return SyncFence.create(ParcelFileDescriptor.fromFd(nativeGetFenceFd()));
            }
            return SyncFence.createEmpty();
        }

        @Override // android.media.Image
        public HardwareBuffer getHardwareBuffer() {
            throwISEIfImageIsInvalid();
            return nativeGetHardwareBuffer();
        }

        @Override // android.media.Image
        public int getDataSpace() {
            throwISEIfImageIsInvalid();
            return this.mDataSpace;
        }

        @Override // android.media.Image
        public void setTimestamp(long timestampNs) {
            throwISEIfImageIsInvalid();
            this.mTimestamp = timestampNs;
        }

        @Override // android.media.Image
        public Image.Plane[] getPlanes() {
            throwISEIfImageIsInvalid();
            if (this.mPlanes == null) {
                this.mPlanes = nativeCreatePlanes(ImageReader.this.mNumPlanes, ImageReader.this.mHardwareBufferFormat, ImageReader.this.mUsage);
            }
            return (Image.Plane[]) this.mPlanes.clone();
        }

        protected final void finalize() throws Throwable {
            try {
                close();
            } finally {
                super.finalize();
            }
        }

        @Override // android.media.Image
        public boolean isAttachable() {
            throwISEIfImageIsInvalid();
            return this.mIsDetached.get();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // android.media.Image
        public ImageReader getOwner() {
            throwISEIfImageIsInvalid();
            return ImageReader.this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // android.media.Image
        public long getNativeContext() {
            throwISEIfImageIsInvalid();
            return this.mNativeBuffer;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setDetached(boolean detached) {
            throwISEIfImageIsInvalid();
            this.mIsDetached.getAndSet(detached);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearSurfacePlanes() {
            if (this.mIsImageValid && this.mPlanes != null) {
                int i = 0;
                while (true) {
                    SurfacePlane[] surfacePlaneArr = this.mPlanes;
                    if (i < surfacePlaneArr.length) {
                        SurfacePlane surfacePlane = surfacePlaneArr[i];
                        if (surfacePlane != null) {
                            surfacePlane.clearBuffer();
                            this.mPlanes[i] = null;
                        }
                        i++;
                    } else {
                        return;
                    }
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public class SurfacePlane extends Image.Plane {
            private ByteBuffer mBuffer;
            private final int mPixelStride;
            private final int mRowStride;

            private SurfacePlane(int rowStride, int pixelStride, ByteBuffer buffer) {
                this.mRowStride = rowStride;
                this.mPixelStride = pixelStride;
                this.mBuffer = buffer;
                buffer.order(ByteOrder.nativeOrder());
            }

            @Override // android.media.Image.Plane
            public ByteBuffer getBuffer() {
                SurfaceImage.this.throwISEIfImageIsInvalid();
                return this.mBuffer;
            }

            @Override // android.media.Image.Plane
            public int getPixelStride() {
                SurfaceImage.this.throwISEIfImageIsInvalid();
                if (ImageReader.this.mFormat == 36) {
                    throw new UnsupportedOperationException("getPixelStride is not supported for RAW_PRIVATE plane");
                }
                return this.mPixelStride;
            }

            @Override // android.media.Image.Plane
            public int getRowStride() {
                SurfaceImage.this.throwISEIfImageIsInvalid();
                if (ImageReader.this.mFormat == 36) {
                    throw new UnsupportedOperationException("getRowStride is not supported for RAW_PRIVATE plane");
                }
                return this.mRowStride;
            }

            /* JADX INFO: Access modifiers changed from: private */
            public void clearBuffer() {
                ByteBuffer byteBuffer = this.mBuffer;
                if (byteBuffer == null) {
                    return;
                }
                if (byteBuffer.isDirect()) {
                    NioUtils.freeDirectBuffer(this.mBuffer);
                }
                this.mBuffer = null;
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class ImagePlane extends Image.Plane {
        private ByteBuffer mBuffer;
        private final int mPixelStride;
        private final int mRowStride;

        private ImagePlane(int rowStride, int pixelStride, ByteBuffer buffer) {
            this.mRowStride = rowStride;
            this.mPixelStride = pixelStride;
            this.mBuffer = buffer;
            buffer.order(ByteOrder.nativeOrder());
        }

        @Override // android.media.Image.Plane
        public ByteBuffer getBuffer() {
            return this.mBuffer;
        }

        @Override // android.media.Image.Plane
        public int getPixelStride() {
            return this.mPixelStride;
        }

        @Override // android.media.Image.Plane
        public int getRowStride() {
            return this.mRowStride;
        }
    }

    public static ImagePlane[] initializeImagePlanes(int numPlanes, GraphicBuffer buffer, int fenceFd, int format, long timestamp, int transform, int scalingMode, Rect crop) {
        return nativeCreateImagePlanes(numPlanes, buffer, fenceFd, format, crop.left, crop.top, crop.right, crop.bottom);
    }

    public static void unlockGraphicBuffer(GraphicBuffer buffer) {
        nativeUnlockGraphicBuffer(buffer);
    }

    static {
        System.loadLibrary("media_jni");
        nativeClassInit();
    }
}
