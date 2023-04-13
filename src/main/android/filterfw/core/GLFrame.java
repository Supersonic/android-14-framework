package android.filterfw.core;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.opengl.GLES20;
import java.nio.ByteBuffer;
/* loaded from: classes.dex */
public class GLFrame extends Frame {
    public static final int EXISTING_FBO_BINDING = 101;
    public static final int EXISTING_TEXTURE_BINDING = 100;
    public static final int EXTERNAL_TEXTURE = 104;
    public static final int NEW_FBO_BINDING = 103;
    public static final int NEW_TEXTURE_BINDING = 102;
    private int glFrameId;
    private GLEnvironment mGLEnvironment;
    private boolean mOwnsTexture;

    private native boolean generateNativeMipMap();

    private native boolean getNativeBitmap(Bitmap bitmap);

    private native byte[] getNativeData();

    private native int getNativeFboId();

    private native float[] getNativeFloats();

    private native int[] getNativeInts();

    private native int getNativeTextureId();

    private native boolean nativeAllocate(GLEnvironment gLEnvironment, int i, int i2);

    private native boolean nativeAllocateExternal(GLEnvironment gLEnvironment);

    private native boolean nativeAllocateWithFbo(GLEnvironment gLEnvironment, int i, int i2, int i3);

    private native boolean nativeAllocateWithTexture(GLEnvironment gLEnvironment, int i, int i2, int i3);

    private native boolean nativeCopyFromGL(GLFrame gLFrame);

    private native boolean nativeCopyFromNative(NativeFrame nativeFrame);

    private native boolean nativeDeallocate();

    private native boolean nativeDetachTexFromFbo();

    private native boolean nativeFocus();

    private native boolean nativeReattachTexToFbo();

    private native boolean nativeResetParams();

    private native boolean setNativeBitmap(Bitmap bitmap, int i);

    private native boolean setNativeData(byte[] bArr, int i, int i2);

    private native boolean setNativeFloats(float[] fArr);

    private native boolean setNativeInts(int[] iArr);

    private native boolean setNativeTextureParam(int i, int i2);

    private native boolean setNativeViewport(int i, int i2, int i3, int i4);

    /* JADX INFO: Access modifiers changed from: package-private */
    public GLFrame(FrameFormat format, FrameManager frameManager) {
        super(format, frameManager);
        this.glFrameId = -1;
        this.mOwnsTexture = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public GLFrame(FrameFormat format, FrameManager frameManager, int bindingType, long bindingId) {
        super(format, frameManager, bindingType, bindingId);
        this.glFrameId = -1;
        this.mOwnsTexture = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void init(GLEnvironment glEnv) {
        FrameFormat format = getFormat();
        this.mGLEnvironment = glEnv;
        if (format.getBytesPerSample() != 4) {
            throw new IllegalArgumentException("GL frames must have 4 bytes per sample!");
        }
        if (format.getDimensionCount() != 2) {
            throw new IllegalArgumentException("GL frames must be 2-dimensional!");
        }
        if (getFormat().getSize() < 0) {
            throw new IllegalArgumentException("Initializing GL frame with zero size!");
        }
        int bindingType = getBindingType();
        boolean reusable = true;
        if (bindingType == 0) {
            initNew(false);
        } else if (bindingType == 104) {
            initNew(true);
            reusable = false;
        } else if (bindingType == 100) {
            initWithTexture((int) getBindingId());
        } else if (bindingType == 101) {
            initWithFbo((int) getBindingId());
        } else if (bindingType == 102) {
            initWithTexture((int) getBindingId());
        } else if (bindingType == 103) {
            initWithFbo((int) getBindingId());
        } else {
            throw new RuntimeException("Attempting to create GL frame with unknown binding type " + bindingType + "!");
        }
        setReusable(reusable);
    }

    private void initNew(boolean isExternal) {
        if (isExternal) {
            if (!nativeAllocateExternal(this.mGLEnvironment)) {
                throw new RuntimeException("Could not allocate external GL frame!");
            }
        } else if (!nativeAllocate(this.mGLEnvironment, getFormat().getWidth(), getFormat().getHeight())) {
            throw new RuntimeException("Could not allocate GL frame!");
        }
    }

    private void initWithTexture(int texId) {
        int width = getFormat().getWidth();
        int height = getFormat().getHeight();
        if (!nativeAllocateWithTexture(this.mGLEnvironment, texId, width, height)) {
            throw new RuntimeException("Could not allocate texture backed GL frame!");
        }
        this.mOwnsTexture = false;
        markReadOnly();
    }

    private void initWithFbo(int fboId) {
        int width = getFormat().getWidth();
        int height = getFormat().getHeight();
        if (!nativeAllocateWithFbo(this.mGLEnvironment, fboId, width, height)) {
            throw new RuntimeException("Could not allocate FBO backed GL frame!");
        }
    }

    void flushGPU(String message) {
        StopWatchMap timer = GLFrameTimer.get();
        if (timer.LOG_MFF_RUNNING_TIMES) {
            timer.start("glFinish " + message);
            GLES20.glFinish();
            timer.stop("glFinish " + message);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.filterfw.core.Frame
    public synchronized boolean hasNativeAllocation() {
        return this.glFrameId != -1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.filterfw.core.Frame
    public synchronized void releaseNativeAllocation() {
        nativeDeallocate();
        this.glFrameId = -1;
    }

    public GLEnvironment getGLEnvironment() {
        return this.mGLEnvironment;
    }

    @Override // android.filterfw.core.Frame
    public Object getObjectValue() {
        assertGLEnvValid();
        return ByteBuffer.wrap(getNativeData());
    }

    @Override // android.filterfw.core.Frame
    public void setInts(int[] ints) {
        assertFrameMutable();
        assertGLEnvValid();
        if (!setNativeInts(ints)) {
            throw new RuntimeException("Could not set int values for GL frame!");
        }
    }

    @Override // android.filterfw.core.Frame
    public int[] getInts() {
        assertGLEnvValid();
        flushGPU("getInts");
        return getNativeInts();
    }

    @Override // android.filterfw.core.Frame
    public void setFloats(float[] floats) {
        assertFrameMutable();
        assertGLEnvValid();
        if (!setNativeFloats(floats)) {
            throw new RuntimeException("Could not set int values for GL frame!");
        }
    }

    @Override // android.filterfw.core.Frame
    public float[] getFloats() {
        assertGLEnvValid();
        flushGPU("getFloats");
        return getNativeFloats();
    }

    @Override // android.filterfw.core.Frame
    public void setData(ByteBuffer buffer, int offset, int length) {
        assertFrameMutable();
        assertGLEnvValid();
        byte[] bytes = buffer.array();
        if (getFormat().getSize() != bytes.length) {
            throw new RuntimeException("Data size in setData does not match GL frame size!");
        }
        if (!setNativeData(bytes, offset, length)) {
            throw new RuntimeException("Could not set GL frame data!");
        }
    }

    @Override // android.filterfw.core.Frame
    public ByteBuffer getData() {
        assertGLEnvValid();
        flushGPU("getData");
        return ByteBuffer.wrap(getNativeData());
    }

    @Override // android.filterfw.core.Frame
    public void setBitmap(Bitmap bitmap) {
        assertFrameMutable();
        assertGLEnvValid();
        if (getFormat().getWidth() != bitmap.getWidth() || getFormat().getHeight() != bitmap.getHeight()) {
            throw new RuntimeException("Bitmap dimensions do not match GL frame dimensions!");
        }
        Bitmap rgbaBitmap = convertBitmapToRGBA(bitmap);
        if (!setNativeBitmap(rgbaBitmap, rgbaBitmap.getByteCount())) {
            throw new RuntimeException("Could not set GL frame bitmap data!");
        }
    }

    @Override // android.filterfw.core.Frame
    public Bitmap getBitmap() {
        assertGLEnvValid();
        flushGPU("getBitmap");
        Bitmap result = Bitmap.createBitmap(getFormat().getWidth(), getFormat().getHeight(), Bitmap.Config.ARGB_8888);
        if (!getNativeBitmap(result)) {
            throw new RuntimeException("Could not get bitmap data from GL frame!");
        }
        return result;
    }

    @Override // android.filterfw.core.Frame
    public void setDataFromFrame(Frame frame) {
        assertGLEnvValid();
        if (getFormat().getSize() < frame.getFormat().getSize()) {
            throw new RuntimeException("Attempting to assign frame of size " + frame.getFormat().getSize() + " to smaller GL frame of size " + getFormat().getSize() + "!");
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

    public void setViewport(int x, int y, int width, int height) {
        assertFrameMutable();
        setNativeViewport(x, y, width, height);
    }

    public void setViewport(Rect rect) {
        assertFrameMutable();
        setNativeViewport(rect.left, rect.top, rect.right - rect.left, rect.bottom - rect.top);
    }

    public void generateMipMap() {
        assertFrameMutable();
        assertGLEnvValid();
        if (!generateNativeMipMap()) {
            throw new RuntimeException("Could not generate mip-map for GL frame!");
        }
    }

    public void setTextureParameter(int param, int value) {
        assertFrameMutable();
        assertGLEnvValid();
        if (!setNativeTextureParam(param, value)) {
            throw new RuntimeException("Could not set texture value " + param + " = " + value + " for GLFrame!");
        }
    }

    public int getTextureId() {
        return getNativeTextureId();
    }

    public int getFboId() {
        return getNativeFboId();
    }

    public void focus() {
        if (!nativeFocus()) {
            throw new RuntimeException("Could not focus on GLFrame for drawing!");
        }
    }

    public String toString() {
        return "GLFrame id: " + this.glFrameId + " (" + getFormat() + ") with texture ID " + getTextureId() + ", FBO ID " + getFboId();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.filterfw.core.Frame
    public void reset(FrameFormat newFormat) {
        if (!nativeResetParams()) {
            throw new RuntimeException("Could not reset GLFrame texture parameters!");
        }
        super.reset(newFormat);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.filterfw.core.Frame
    public void onFrameStore() {
        if (!this.mOwnsTexture) {
            nativeDetachTexFromFbo();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.filterfw.core.Frame
    public void onFrameFetch() {
        if (!this.mOwnsTexture) {
            nativeReattachTexToFbo();
        }
    }

    private void assertGLEnvValid() {
        if (!this.mGLEnvironment.isContextActive()) {
            if (GLEnvironment.isAnyContextActive()) {
                throw new RuntimeException("Attempting to access " + this + " with foreign GL context active!");
            }
            throw new RuntimeException("Attempting to access " + this + " with no GL context  active!");
        }
    }

    static {
        System.loadLibrary("filterfw");
    }
}
