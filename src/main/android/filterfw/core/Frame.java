package android.filterfw.core;

import android.graphics.Bitmap;
import java.nio.ByteBuffer;
/* loaded from: classes.dex */
public abstract class Frame {
    public static final int NO_BINDING = 0;
    public static final long TIMESTAMP_NOT_SET = -2;
    public static final long TIMESTAMP_UNKNOWN = -1;
    private long mBindingId;
    private int mBindingType;
    private FrameFormat mFormat;
    private FrameManager mFrameManager;
    private boolean mReadOnly;
    private int mRefCount;
    private boolean mReusable;
    private long mTimestamp;

    public abstract Bitmap getBitmap();

    public abstract ByteBuffer getData();

    public abstract float[] getFloats();

    public abstract int[] getInts();

    public abstract Object getObjectValue();

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract boolean hasNativeAllocation();

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void releaseNativeAllocation();

    public abstract void setBitmap(Bitmap bitmap);

    public abstract void setData(ByteBuffer byteBuffer, int i, int i2);

    public abstract void setFloats(float[] fArr);

    public abstract void setInts(int[] iArr);

    /* JADX INFO: Access modifiers changed from: package-private */
    public Frame(FrameFormat format, FrameManager frameManager) {
        this.mReadOnly = false;
        this.mReusable = false;
        this.mRefCount = 1;
        this.mBindingType = 0;
        this.mBindingId = 0L;
        this.mTimestamp = -2L;
        this.mFormat = format.mutableCopy();
        this.mFrameManager = frameManager;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Frame(FrameFormat format, FrameManager frameManager, int bindingType, long bindingId) {
        this.mReadOnly = false;
        this.mReusable = false;
        this.mRefCount = 1;
        this.mBindingType = 0;
        this.mBindingId = 0L;
        this.mTimestamp = -2L;
        this.mFormat = format.mutableCopy();
        this.mFrameManager = frameManager;
        this.mBindingType = bindingType;
        this.mBindingId = bindingId;
    }

    public FrameFormat getFormat() {
        return this.mFormat;
    }

    public int getCapacity() {
        return getFormat().getSize();
    }

    public boolean isReadOnly() {
        return this.mReadOnly;
    }

    public int getBindingType() {
        return this.mBindingType;
    }

    public long getBindingId() {
        return this.mBindingId;
    }

    public void setObjectValue(Object object) {
        assertFrameMutable();
        if (object instanceof int[]) {
            setInts((int[]) object);
        } else if (object instanceof float[]) {
            setFloats((float[]) object);
        } else if (object instanceof ByteBuffer) {
            setData((ByteBuffer) object);
        } else if (object instanceof Bitmap) {
            setBitmap((Bitmap) object);
        } else {
            setGenericObjectValue(object);
        }
    }

    public void setData(ByteBuffer buffer) {
        setData(buffer, 0, buffer.limit());
    }

    public void setData(byte[] bytes, int offset, int length) {
        setData(ByteBuffer.wrap(bytes, offset, length));
    }

    public void setTimestamp(long timestamp) {
        this.mTimestamp = timestamp;
    }

    public long getTimestamp() {
        return this.mTimestamp;
    }

    public void setDataFromFrame(Frame frame) {
        setData(frame.getData());
    }

    protected boolean requestResize(int[] newDimensions) {
        return false;
    }

    public int getRefCount() {
        return this.mRefCount;
    }

    public Frame release() {
        FrameManager frameManager = this.mFrameManager;
        if (frameManager != null) {
            return frameManager.releaseFrame(this);
        }
        return this;
    }

    public Frame retain() {
        FrameManager frameManager = this.mFrameManager;
        if (frameManager != null) {
            return frameManager.retainFrame(this);
        }
        return this;
    }

    public FrameManager getFrameManager() {
        return this.mFrameManager;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void assertFrameMutable() {
        if (isReadOnly()) {
            throw new RuntimeException("Attempting to modify read-only frame!");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setReusable(boolean reusable) {
        this.mReusable = reusable;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setFormat(FrameFormat format) {
        this.mFormat = format.mutableCopy();
    }

    protected void setGenericObjectValue(Object value) {
        throw new RuntimeException("Cannot set object value of unsupported type: " + value.getClass());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static Bitmap convertBitmapToRGBA(Bitmap bitmap) {
        if (bitmap.getConfig() == Bitmap.Config.ARGB_8888) {
            return bitmap;
        }
        Bitmap result = bitmap.copy(Bitmap.Config.ARGB_8888, false);
        if (result == null) {
            throw new RuntimeException("Error converting bitmap to RGBA!");
        }
        if (result.getRowBytes() != result.getWidth() * 4) {
            throw new RuntimeException("Unsupported row byte count in bitmap!");
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void reset(FrameFormat newFormat) {
        this.mFormat = newFormat.mutableCopy();
        this.mReadOnly = false;
        this.mRefCount = 1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onFrameStore() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onFrameFetch() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int incRefCount() {
        int i = this.mRefCount + 1;
        this.mRefCount = i;
        return i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int decRefCount() {
        int i = this.mRefCount - 1;
        this.mRefCount = i;
        return i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isReusable() {
        return this.mReusable;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void markReadOnly() {
        this.mReadOnly = true;
    }
}
