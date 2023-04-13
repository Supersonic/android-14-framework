package android.filterfw.core;
/* loaded from: classes.dex */
public class NativeBuffer {
    private Frame mAttachedFrame;
    private long mDataPointer;
    private boolean mOwnsData;
    private int mRefCount;
    private int mSize;

    private native boolean allocate(int i);

    private native boolean deallocate(boolean z);

    private native boolean nativeCopyTo(NativeBuffer nativeBuffer);

    public NativeBuffer() {
        this.mDataPointer = 0L;
        this.mSize = 0;
        this.mOwnsData = false;
        this.mRefCount = 1;
    }

    public NativeBuffer(int count) {
        this.mDataPointer = 0L;
        this.mSize = 0;
        this.mOwnsData = false;
        this.mRefCount = 1;
        allocate(getElementSize() * count);
        this.mOwnsData = true;
    }

    public NativeBuffer mutableCopy() {
        try {
            Class myClass = getClass();
            NativeBuffer result = (NativeBuffer) myClass.newInstance();
            if (this.mSize <= 0 || nativeCopyTo(result)) {
                return result;
            }
            throw new RuntimeException("Failed to copy NativeBuffer to mutable instance!");
        } catch (Exception e) {
            throw new RuntimeException("Unable to allocate a copy of " + getClass() + "! Make sure the class has a default constructor!");
        }
    }

    public int size() {
        return this.mSize;
    }

    public int count() {
        if (this.mDataPointer != 0) {
            return this.mSize / getElementSize();
        }
        return 0;
    }

    public int getElementSize() {
        return 1;
    }

    public NativeBuffer retain() {
        Frame frame = this.mAttachedFrame;
        if (frame != null) {
            frame.retain();
        } else if (this.mOwnsData) {
            this.mRefCount++;
        }
        return this;
    }

    public NativeBuffer release() {
        boolean doDealloc = false;
        Frame frame = this.mAttachedFrame;
        if (frame != null) {
            doDealloc = frame.release() == null;
        } else if (this.mOwnsData) {
            int i = this.mRefCount - 1;
            this.mRefCount = i;
            doDealloc = i == 0;
        }
        if (doDealloc) {
            deallocate(this.mOwnsData);
            return null;
        }
        return this;
    }

    public boolean isReadOnly() {
        Frame frame = this.mAttachedFrame;
        if (frame != null) {
            return frame.isReadOnly();
        }
        return false;
    }

    static {
        System.loadLibrary("filterfw");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void attachToFrame(Frame frame) {
        this.mAttachedFrame = frame;
    }

    protected void assertReadable() {
        Frame frame;
        if (this.mDataPointer == 0 || this.mSize == 0 || ((frame = this.mAttachedFrame) != null && !frame.hasNativeAllocation())) {
            throw new NullPointerException("Attempting to read from null data frame!");
        }
    }

    protected void assertWritable() {
        if (isReadOnly()) {
            throw new RuntimeException("Attempting to modify read-only native (structured) data!");
        }
    }
}
