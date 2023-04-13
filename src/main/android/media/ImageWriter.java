package android.media;

import android.graphics.GraphicBuffer;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.HardwareBuffer;
import android.hardware.SyncFence;
import android.hardware.camera2.utils.SurfaceUtils;
import android.media.Image;
import android.p008os.Handler;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.ParcelFileDescriptor;
import android.util.Size;
import android.view.Surface;
import dalvik.system.VMRuntime;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.NioUtils;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
/* loaded from: classes2.dex */
public class ImageWriter implements AutoCloseable {
    private final Object mCloseLock;
    private int mDataSpace;
    private List<Image> mDequeuedImages;
    private int mEstimatedNativeAllocBytes;
    private int mHardwareBufferFormat;
    private int mHeight;
    private boolean mIsWriterValid;
    private OnImageReleasedListener mListener;
    private ListenerHandler mListenerHandler;
    private final Object mListenerLock;
    private final int mMaxImages;
    private long mNativeContext;
    private long mUsage;
    private int mWidth;
    private int mWriterFormat;

    /* loaded from: classes2.dex */
    public interface OnImageReleasedListener {
        void onImageReleased(ImageWriter imageWriter);
    }

    private native synchronized void cancelImage(long j, Image image);

    private native synchronized int nativeAttachAndQueueGraphicBuffer(long j, GraphicBuffer graphicBuffer, int i, long j2, int i2, int i3, int i4, int i5, int i6, int i7, int i8);

    private native synchronized int nativeAttachAndQueueImage(long j, long j2, int i, long j3, int i2, int i3, int i4, int i5, int i6, int i7, int i8);

    private static native void nativeClassInit();

    private native synchronized void nativeClose(long j);

    private native synchronized void nativeDequeueInputImage(long j, Image image);

    private native synchronized long nativeInit(Object obj, Surface surface, int i, int i2, int i3, boolean z, int i4, int i5, long j);

    private native synchronized void nativeQueueInputImage(long j, Image image, long j2, int i, int i2, int i3, int i4, int i5, int i6, int i7);

    public static ImageWriter newInstance(Surface surface, int maxImages) {
        return new ImageWriter(surface, maxImages, true, 0, -1, -1);
    }

    public static ImageWriter newInstance(Surface surface, int maxImages, int format, int width, int height) {
        if (!ImageFormat.isPublicFormat(format) && !PixelFormat.isPublicFormat(format)) {
            throw new IllegalArgumentException("Invalid format is specified: " + format);
        }
        return new ImageWriter(surface, maxImages, false, format, width, height);
    }

    public static ImageWriter newInstance(Surface surface, int maxImages, int format) {
        if (!ImageFormat.isPublicFormat(format) && !PixelFormat.isPublicFormat(format)) {
            throw new IllegalArgumentException("Invalid format is specified: " + format);
        }
        return new ImageWriter(surface, maxImages, false, format, -1, -1);
    }

    private void initializeImageWriter(Surface surface, int maxImages, boolean useSurfaceImageFormatInfo, int imageFormat, int hardwareBufferFormat, int dataSpace, int width, int height, long usage) {
        int imageFormat2;
        if (surface != null && maxImages >= 1) {
            this.mNativeContext = nativeInit(new WeakReference(this), surface, maxImages, width, height, useSurfaceImageFormatInfo, hardwareBufferFormat, dataSpace, usage);
            if (!useSurfaceImageFormatInfo) {
                imageFormat2 = imageFormat;
            } else {
                int hardwareBufferFormat2 = SurfaceUtils.getSurfaceFormat(surface);
                this.mHardwareBufferFormat = hardwareBufferFormat2;
                int dataSpace2 = SurfaceUtils.getSurfaceDataspace(surface);
                this.mDataSpace = dataSpace2;
                imageFormat2 = PublicFormatUtils.getPublicFormat(hardwareBufferFormat2, dataSpace2);
            }
            Size surfSize = SurfaceUtils.getSurfaceSize(surface);
            this.mWidth = width == -1 ? surfSize.getWidth() : width;
            int height2 = height == -1 ? surfSize.getHeight() : height;
            this.mHeight = height2;
            this.mEstimatedNativeAllocBytes = ImageUtils.getEstimatedNativeAllocBytes(this.mWidth, height2, imageFormat2, 1);
            VMRuntime.getRuntime().registerNativeAllocation(this.mEstimatedNativeAllocBytes);
            this.mIsWriterValid = true;
            return;
        }
        throw new IllegalArgumentException("Illegal input argument: surface " + surface + ", maxImages: " + maxImages);
    }

    private ImageWriter(Surface surface, int maxImages, boolean useSurfaceImageFormatInfo, int imageFormat, int width, int height) {
        this.mListenerLock = new Object();
        this.mCloseLock = new Object();
        this.mIsWriterValid = false;
        this.mUsage = 48L;
        this.mDequeuedImages = new CopyOnWriteArrayList();
        this.mMaxImages = maxImages;
        if (!useSurfaceImageFormatInfo) {
            this.mHardwareBufferFormat = PublicFormatUtils.getHalFormat(imageFormat);
            this.mDataSpace = PublicFormatUtils.getHalDataspace(imageFormat);
        }
        initializeImageWriter(surface, maxImages, useSurfaceImageFormatInfo, imageFormat, this.mHardwareBufferFormat, this.mDataSpace, width, height, this.mUsage);
    }

    private ImageWriter(Surface surface, int maxImages, boolean useSurfaceImageFormatInfo, int imageFormat, int width, int height, long usage) {
        this.mListenerLock = new Object();
        this.mCloseLock = new Object();
        this.mIsWriterValid = false;
        this.mUsage = 48L;
        this.mDequeuedImages = new CopyOnWriteArrayList();
        this.mMaxImages = maxImages;
        this.mUsage = usage;
        if (!useSurfaceImageFormatInfo) {
            this.mHardwareBufferFormat = PublicFormatUtils.getHalFormat(imageFormat);
            this.mDataSpace = PublicFormatUtils.getHalDataspace(imageFormat);
        }
        initializeImageWriter(surface, maxImages, useSurfaceImageFormatInfo, imageFormat, this.mHardwareBufferFormat, this.mDataSpace, width, height, usage);
    }

    private ImageWriter(Surface surface, int maxImages, boolean useSurfaceImageFormatInfo, int hardwareBufferFormat, int dataSpace, int width, int height, long usage) {
        int imageFormat;
        this.mListenerLock = new Object();
        this.mCloseLock = new Object();
        this.mIsWriterValid = false;
        this.mUsage = 48L;
        this.mDequeuedImages = new CopyOnWriteArrayList();
        this.mMaxImages = maxImages;
        this.mUsage = usage;
        if (useSurfaceImageFormatInfo) {
            imageFormat = 0;
        } else {
            int imageFormat2 = PublicFormatUtils.getPublicFormat(hardwareBufferFormat, dataSpace);
            this.mHardwareBufferFormat = hardwareBufferFormat;
            this.mDataSpace = dataSpace;
            imageFormat = imageFormat2;
        }
        initializeImageWriter(surface, maxImages, useSurfaceImageFormatInfo, imageFormat, hardwareBufferFormat, dataSpace, width, height, usage);
    }

    public int getMaxImages() {
        return this.mMaxImages;
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public Image dequeueInputImage() {
        if (this.mDequeuedImages.size() >= this.mMaxImages) {
            throw new IllegalStateException("Already dequeued max number of Images " + this.mMaxImages);
        }
        WriterSurfaceImage newImage = new WriterSurfaceImage(this);
        nativeDequeueInputImage(this.mNativeContext, newImage);
        this.mDequeuedImages.add(newImage);
        newImage.mIsImageValid = true;
        return newImage;
    }

    public void queueInputImage(Image image) {
        if (image == null) {
            throw new IllegalArgumentException("image shouldn't be null");
        }
        boolean ownedByMe = isImageOwnedByMe(image);
        if (ownedByMe && !((WriterSurfaceImage) image).mIsImageValid) {
            throw new IllegalStateException("Image from ImageWriter is invalid");
        }
        if (!ownedByMe) {
            if (image.getOwner() instanceof ImageReader) {
                ImageReader prevOwner = (ImageReader) image.getOwner();
                prevOwner.detachImage(image);
            } else if (image.getOwner() != null) {
                throw new IllegalArgumentException("Only images from ImageReader can be queued to ImageWriter, other image source is not supported yet!");
            }
            attachAndQueueInputImage(image);
            image.close();
            return;
        }
        Rect crop = image.getCropRect();
        nativeQueueInputImage(this.mNativeContext, image, image.getTimestamp(), image.getDataSpace(), crop.left, crop.top, crop.right, crop.bottom, image.getTransform(), image.getScalingMode());
        if (ownedByMe) {
            this.mDequeuedImages.remove(image);
            WriterSurfaceImage wi = (WriterSurfaceImage) image;
            wi.clearSurfacePlanes();
            wi.mIsImageValid = false;
        }
    }

    public int getFormat() {
        return this.mWriterFormat;
    }

    public long getUsage() {
        return this.mUsage;
    }

    public int getHardwareBufferFormat() {
        return this.mHardwareBufferFormat;
    }

    public int getDataSpace() {
        return this.mDataSpace;
    }

    public void setOnImageReleasedListener(OnImageReleasedListener listener, Handler handler) {
        synchronized (this.mListenerLock) {
            if (listener != null) {
                Looper looper = handler != null ? handler.getLooper() : Looper.myLooper();
                if (looper == null) {
                    throw new IllegalArgumentException("handler is null but the current thread is not a looper");
                }
                ListenerHandler listenerHandler = this.mListenerHandler;
                if (listenerHandler == null || listenerHandler.getLooper() != looper) {
                    this.mListenerHandler = new ListenerHandler(looper);
                }
                this.mListener = listener;
            } else {
                this.mListener = null;
                this.mListenerHandler = null;
            }
        }
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        setOnImageReleasedListener(null, null);
        synchronized (this.mCloseLock) {
            if (this.mIsWriterValid) {
                for (Image image : this.mDequeuedImages) {
                    image.close();
                }
                this.mDequeuedImages.clear();
                nativeClose(this.mNativeContext);
                this.mNativeContext = 0L;
                if (this.mEstimatedNativeAllocBytes > 0) {
                    VMRuntime.getRuntime().registerNativeFree(this.mEstimatedNativeAllocBytes);
                    this.mEstimatedNativeAllocBytes = 0;
                }
                this.mIsWriterValid = false;
            }
        }
    }

    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    private void attachAndQueueInputImage(Image image) {
        if (image == null) {
            throw new IllegalArgumentException("image shouldn't be null");
        }
        if (isImageOwnedByMe(image)) {
            throw new IllegalArgumentException("Can not attach an image that is owned ImageWriter already");
        }
        if (!image.isAttachable()) {
            throw new IllegalStateException("Image was not detached from last owner, or image  is not detachable");
        }
        Rect crop = image.getCropRect();
        int hardwareBufferFormat = PublicFormatUtils.getHalFormat(image.getFormat());
        if (image.getNativeContext() != 0) {
            nativeAttachAndQueueImage(this.mNativeContext, image.getNativeContext(), hardwareBufferFormat, image.getTimestamp(), image.getDataSpace(), crop.left, crop.top, crop.right, crop.bottom, image.getTransform(), image.getScalingMode());
            return;
        }
        GraphicBuffer gb = GraphicBuffer.createFromHardwareBuffer(image.getHardwareBuffer());
        nativeAttachAndQueueGraphicBuffer(this.mNativeContext, gb, hardwareBufferFormat, image.getTimestamp(), image.getDataSpace(), crop.left, crop.top, crop.right, crop.bottom, image.getTransform(), image.getScalingMode());
        gb.destroy();
        image.close();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public final class ListenerHandler extends Handler {
        public ListenerHandler(Looper looper) {
            super(looper, null, true);
        }

        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            OnImageReleasedListener listener;
            boolean isWriterValid;
            synchronized (ImageWriter.this.mListenerLock) {
                listener = ImageWriter.this.mListener;
            }
            synchronized (ImageWriter.this.mCloseLock) {
                isWriterValid = ImageWriter.this.mIsWriterValid;
            }
            if (listener != null && isWriterValid) {
                listener.onImageReleased(ImageWriter.this);
            }
        }
    }

    private static void postEventFromNative(Object selfRef) {
        Handler handler;
        WeakReference<ImageWriter> weakSelf = (WeakReference) selfRef;
        ImageWriter iw = weakSelf.get();
        if (iw == null) {
            return;
        }
        synchronized (iw.mListenerLock) {
            handler = iw.mListenerHandler;
        }
        if (handler != null) {
            handler.sendEmptyMessage(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void abortImage(Image image) {
        if (image == null) {
            throw new IllegalArgumentException("image shouldn't be null");
        }
        if (!this.mDequeuedImages.contains(image)) {
            throw new IllegalStateException("It is illegal to abort some image that is not dequeued yet");
        }
        WriterSurfaceImage wi = (WriterSurfaceImage) image;
        if (!wi.mIsImageValid) {
            return;
        }
        cancelImage(this.mNativeContext, image);
        this.mDequeuedImages.remove(image);
        wi.clearSurfacePlanes();
        wi.mIsImageValid = false;
    }

    private boolean isImageOwnedByMe(Image image) {
        if (image instanceof WriterSurfaceImage) {
            WriterSurfaceImage wi = (WriterSurfaceImage) image;
            return wi.getOwner() == this;
        }
        return false;
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private Surface mSurface;
        private int mWidth = -1;
        private int mHeight = -1;
        private int mMaxImages = 1;
        private int mImageFormat = 0;
        private long mUsage = -1;
        private int mHardwareBufferFormat = 1;
        private int mDataSpace = 0;
        private boolean mUseSurfaceImageFormatInfo = true;
        private boolean mUseLegacyImageFormat = false;

        public Builder(Surface surface) {
            this.mSurface = surface;
        }

        public Builder setWidthAndHeight(int width, int height) {
            this.mWidth = width;
            this.mHeight = height;
            return this;
        }

        public Builder setMaxImages(int maxImages) {
            this.mMaxImages = maxImages;
            return this;
        }

        public Builder setImageFormat(int imageFormat) {
            if (!ImageFormat.isPublicFormat(imageFormat) && !PixelFormat.isPublicFormat(imageFormat)) {
                throw new IllegalArgumentException("Invalid imageFormat is specified: " + imageFormat);
            }
            this.mImageFormat = imageFormat;
            this.mUseLegacyImageFormat = true;
            this.mHardwareBufferFormat = 1;
            this.mDataSpace = 0;
            this.mUseSurfaceImageFormatInfo = false;
            return this;
        }

        public Builder setHardwareBufferFormat(int hardwareBufferFormat) {
            this.mHardwareBufferFormat = hardwareBufferFormat;
            this.mImageFormat = 0;
            this.mUseLegacyImageFormat = false;
            this.mUseSurfaceImageFormatInfo = false;
            return this;
        }

        public Builder setDataSpace(int dataSpace) {
            this.mDataSpace = dataSpace;
            this.mImageFormat = 0;
            this.mUseLegacyImageFormat = false;
            this.mUseSurfaceImageFormatInfo = false;
            return this;
        }

        public Builder setUsage(long usage) {
            this.mUsage = usage;
            return this;
        }

        public ImageWriter build() {
            if (this.mUseLegacyImageFormat) {
                return new ImageWriter(this.mSurface, this.mMaxImages, this.mUseSurfaceImageFormatInfo, this.mImageFormat, this.mWidth, this.mHeight, this.mUsage);
            }
            return new ImageWriter(this.mSurface, this.mMaxImages, this.mUseSurfaceImageFormatInfo, this.mHardwareBufferFormat, this.mDataSpace, this.mWidth, this.mHeight, this.mUsage);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class WriterSurfaceImage extends Image {
        private int mDataSpace;
        private int mHeight;
        private long mNativeBuffer;
        private ImageWriter mOwner;
        private SurfacePlane[] mPlanes;
        private int mWidth;
        private int mNativeFenceFd = -1;
        private int mFormat = -1;
        private final long DEFAULT_TIMESTAMP = Long.MIN_VALUE;
        private long mTimestamp = Long.MIN_VALUE;
        private int mTransform = 0;
        private int mScalingMode = 0;
        private final Object mCloseLock = new Object();

        private native synchronized SurfacePlane[] nativeCreatePlanes(int i, int i2);

        private native synchronized int nativeGetFormat(int i);

        private native synchronized HardwareBuffer nativeGetHardwareBuffer();

        private native synchronized int nativeGetHeight();

        private native synchronized int nativeGetWidth();

        private native synchronized void nativeSetFenceFd(int i);

        public WriterSurfaceImage(ImageWriter writer) {
            this.mHeight = -1;
            this.mWidth = -1;
            this.mDataSpace = 0;
            this.mOwner = writer;
            this.mWidth = writer.mWidth;
            this.mHeight = writer.mHeight;
            this.mDataSpace = writer.mDataSpace;
        }

        @Override // android.media.Image
        public int getDataSpace() {
            throwISEIfImageIsInvalid();
            return this.mDataSpace;
        }

        @Override // android.media.Image
        public void setDataSpace(int dataSpace) {
            throwISEIfImageIsInvalid();
            this.mDataSpace = dataSpace;
        }

        @Override // android.media.Image
        public int getFormat() {
            throwISEIfImageIsInvalid();
            if (this.mFormat == -1) {
                this.mFormat = nativeGetFormat(this.mDataSpace);
            }
            return this.mFormat;
        }

        @Override // android.media.Image
        public int getWidth() {
            throwISEIfImageIsInvalid();
            if (this.mWidth == -1) {
                this.mWidth = nativeGetWidth();
            }
            return this.mWidth;
        }

        @Override // android.media.Image
        public int getHeight() {
            throwISEIfImageIsInvalid();
            if (this.mHeight == -1) {
                this.mHeight = nativeGetHeight();
            }
            return this.mHeight;
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
        public long getTimestamp() {
            throwISEIfImageIsInvalid();
            return this.mTimestamp;
        }

        @Override // android.media.Image
        public void setTimestamp(long timestamp) {
            throwISEIfImageIsInvalid();
            this.mTimestamp = timestamp;
        }

        @Override // android.media.Image
        public HardwareBuffer getHardwareBuffer() {
            throwISEIfImageIsInvalid();
            return nativeGetHardwareBuffer();
        }

        @Override // android.media.Image
        public SyncFence getFence() throws IOException {
            throwISEIfImageIsInvalid();
            int i = this.mNativeFenceFd;
            if (i != -1) {
                return SyncFence.create(ParcelFileDescriptor.fromFd(i));
            }
            return SyncFence.createEmpty();
        }

        @Override // android.media.Image
        public void setFence(SyncFence fence) throws IOException {
            throwISEIfImageIsInvalid();
            nativeSetFenceFd(fence.getFdDup().detachFd());
        }

        @Override // android.media.Image
        public Image.Plane[] getPlanes() {
            throwISEIfImageIsInvalid();
            if (this.mPlanes == null) {
                int numPlanes = ImageUtils.getNumPlanesForFormat(getFormat());
                this.mPlanes = nativeCreatePlanes(numPlanes, getOwner().getFormat());
            }
            return (Image.Plane[]) this.mPlanes.clone();
        }

        @Override // android.media.Image
        public boolean isAttachable() {
            throwISEIfImageIsInvalid();
            return false;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // android.media.Image
        public ImageWriter getOwner() {
            throwISEIfImageIsInvalid();
            return this.mOwner;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // android.media.Image
        public long getNativeContext() {
            throwISEIfImageIsInvalid();
            return this.mNativeBuffer;
        }

        @Override // android.media.Image, java.lang.AutoCloseable
        public void close() {
            synchronized (this.mCloseLock) {
                if (this.mIsImageValid) {
                    getOwner().abortImage(this);
                }
            }
        }

        protected final void finalize() throws Throwable {
            try {
                close();
            } finally {
                super.finalize();
            }
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
            public int getRowStride() {
                WriterSurfaceImage.this.throwISEIfImageIsInvalid();
                return this.mRowStride;
            }

            @Override // android.media.Image.Plane
            public int getPixelStride() {
                WriterSurfaceImage.this.throwISEIfImageIsInvalid();
                return this.mPixelStride;
            }

            @Override // android.media.Image.Plane
            public ByteBuffer getBuffer() {
                WriterSurfaceImage.this.throwISEIfImageIsInvalid();
                return this.mBuffer;
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

    static {
        System.loadLibrary("media_jni");
        nativeClassInit();
    }
}
