package android.filterfw.core;

import android.graphics.Bitmap;
import java.nio.ByteBuffer;
/* loaded from: classes.dex */
public class NativeFrame extends Frame {
    private int nativeFrameId;

    private native boolean getNativeBitmap(Bitmap bitmap, int i, int i2);

    private native boolean getNativeBuffer(NativeBuffer nativeBuffer);

    private native int getNativeCapacity();

    private native byte[] getNativeData(int i);

    private native float[] getNativeFloats(int i);

    private native int[] getNativeInts(int i);

    private native boolean nativeAllocate(int i);

    private native boolean nativeCopyFromGL(GLFrame gLFrame);

    private native boolean nativeCopyFromNative(NativeFrame nativeFrame);

    private native boolean nativeDeallocate();

    private static native int nativeFloatSize();

    private static native int nativeIntSize();

    private native boolean setNativeBitmap(Bitmap bitmap, int i, int i2);

    private native boolean setNativeData(byte[] bArr, int i, int i2);

    private native boolean setNativeFloats(float[] fArr);

    private native boolean setNativeInts(int[] iArr);

    /* JADX INFO: Access modifiers changed from: package-private */
    public NativeFrame(FrameFormat format, FrameManager frameManager) {
        super(format, frameManager);
        this.nativeFrameId = -1;
        int capacity = format.getSize();
        nativeAllocate(capacity);
        setReusable(capacity != 0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.filterfw.core.Frame
    public synchronized void releaseNativeAllocation() {
        nativeDeallocate();
        this.nativeFrameId = -1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.filterfw.core.Frame
    public synchronized boolean hasNativeAllocation() {
        return this.nativeFrameId != -1;
    }

    @Override // android.filterfw.core.Frame
    public int getCapacity() {
        return getNativeCapacity();
    }

    @Override // android.filterfw.core.Frame
    public Object getObjectValue() {
        if (getFormat().getBaseType() != 8) {
            return getData();
        }
        Class structClass = getFormat().getObjectClass();
        if (structClass == null) {
            throw new RuntimeException("Attempting to get object data from frame that does not specify a structure object class!");
        }
        if (!NativeBuffer.class.isAssignableFrom(structClass)) {
            throw new RuntimeException("NativeFrame object class must be a subclass of NativeBuffer!");
        }
        try {
            NativeBuffer structData = (NativeBuffer) structClass.newInstance();
            if (!getNativeBuffer(structData)) {
                throw new RuntimeException("Could not get the native structured data for frame!");
            }
            structData.attachToFrame(this);
            return structData;
        } catch (Exception e) {
            throw new RuntimeException("Could not instantiate new structure instance of type '" + structClass + "'!");
        }
    }

    @Override // android.filterfw.core.Frame
    public void setInts(int[] ints) {
        assertFrameMutable();
        if (ints.length * nativeIntSize() > getFormat().getSize()) {
            throw new RuntimeException("NativeFrame cannot hold " + ints.length + " integers. (Can only hold " + (getFormat().getSize() / nativeIntSize()) + " integers).");
        }
        if (!setNativeInts(ints)) {
            throw new RuntimeException("Could not set int values for native frame!");
        }
    }

    @Override // android.filterfw.core.Frame
    public int[] getInts() {
        return getNativeInts(getFormat().getSize());
    }

    @Override // android.filterfw.core.Frame
    public void setFloats(float[] floats) {
        assertFrameMutable();
        if (floats.length * nativeFloatSize() > getFormat().getSize()) {
            throw new RuntimeException("NativeFrame cannot hold " + floats.length + " floats. (Can only hold " + (getFormat().getSize() / nativeFloatSize()) + " floats).");
        }
        if (!setNativeFloats(floats)) {
            throw new RuntimeException("Could not set int values for native frame!");
        }
    }

    @Override // android.filterfw.core.Frame
    public float[] getFloats() {
        return getNativeFloats(getFormat().getSize());
    }

    @Override // android.filterfw.core.Frame
    public void setData(ByteBuffer buffer, int offset, int length) {
        assertFrameMutable();
        byte[] bytes = buffer.array();
        if (length + offset > buffer.limit()) {
            throw new RuntimeException("Offset and length exceed buffer size in native setData: " + (length + offset) + " bytes given, but only " + buffer.limit() + " bytes available!");
        }
        if (getFormat().getSize() != length) {
            throw new RuntimeException("Data size in setData does not match native frame size: Frame size is " + getFormat().getSize() + " bytes, but " + length + " bytes given!");
        }
        if (!setNativeData(bytes, offset, length)) {
            throw new RuntimeException("Could not set native frame data!");
        }
    }

    @Override // android.filterfw.core.Frame
    public ByteBuffer getData() {
        byte[] data = getNativeData(getFormat().getSize());
        if (data == null) {
            return null;
        }
        return ByteBuffer.wrap(data);
    }

    @Override // android.filterfw.core.Frame
    public void setBitmap(Bitmap bitmap) {
        assertFrameMutable();
        if (getFormat().getNumberOfDimensions() != 2) {
            throw new RuntimeException("Attempting to set Bitmap for non 2-dimensional native frame!");
        }
        if (getFormat().getWidth() != bitmap.getWidth() || getFormat().getHeight() != bitmap.getHeight()) {
            throw new RuntimeException("Bitmap dimensions do not match native frame dimensions!");
        }
        Bitmap rgbaBitmap = convertBitmapToRGBA(bitmap);
        int byteCount = rgbaBitmap.getByteCount();
        int bps = getFormat().getBytesPerSample();
        if (!setNativeBitmap(rgbaBitmap, byteCount, bps)) {
            throw new RuntimeException("Could not set native frame bitmap data!");
        }
    }

    @Override // android.filterfw.core.Frame
    public Bitmap getBitmap() {
        if (getFormat().getNumberOfDimensions() != 2) {
            throw new RuntimeException("Attempting to get Bitmap for non 2-dimensional native frame!");
        }
        Bitmap result = Bitmap.createBitmap(getFormat().getWidth(), getFormat().getHeight(), Bitmap.Config.ARGB_8888);
        int byteCount = result.getByteCount();
        int bps = getFormat().getBytesPerSample();
        if (!getNativeBitmap(result, byteCount, bps)) {
            throw new RuntimeException("Could not get bitmap data from native frame!");
        }
        return result;
    }

    @Override // android.filterfw.core.Frame
    public void setDataFromFrame(Frame frame) {
        if (getFormat().getSize() < frame.getFormat().getSize()) {
            throw new RuntimeException("Attempting to assign frame of size " + frame.getFormat().getSize() + " to smaller native frame of size " + getFormat().getSize() + "!");
        }
        if (frame instanceof NativeFrame) {
            nativeCopyFromNative((NativeFrame) frame);
        } else if (frame instanceof GLFrame) {
            nativeCopyFromGL((GLFrame) frame);
        } else if (frame instanceof SimpleFrame) {
            setObjectValue(frame.getObjectValue());
        } else {
            super.setDataFromFrame(frame);
        }
    }

    public String toString() {
        return "NativeFrame id: " + this.nativeFrameId + " (" + getFormat() + ") of size " + getCapacity();
    }

    static {
        System.loadLibrary("filterfw");
    }
}
