package android.graphics;

import android.graphics.ColorSpace;
import java.nio.Buffer;
import java.nio.ShortBuffer;
import libcore.util.NativeAllocationRegistry;
/* loaded from: classes.dex */
public class Mesh {
    public static final int TRIANGLES = 0;
    public static final int TRIANGLE_STRIP = 1;
    private boolean mIsIndexed;
    private long mNativeMeshWrapper;

    /* loaded from: classes.dex */
    private @interface Mode {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static native long nativeGetFinalizer();

    private static native long nativeMake(long j, int i, Buffer buffer, boolean z, int i2, int i3, float f, float f2, float f3, float f4);

    private static native long nativeMakeIndexed(long j, int i, Buffer buffer, boolean z, int i2, int i3, ShortBuffer shortBuffer, boolean z2, int i4, int i5, float f, float f2, float f3, float f4);

    private static native void nativeUpdateUniforms(long j, String str, float f, float f2, float f3, float f4, int i);

    private static native void nativeUpdateUniforms(long j, String str, int i, int i2, int i3, int i4, int i5);

    private static native void nativeUpdateUniforms(long j, String str, float[] fArr, boolean z);

    private static native void nativeUpdateUniforms(long j, String str, int[] iArr);

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class MeshHolder {
        public static final NativeAllocationRegistry MESH_SPECIFICATION_REGISTRY = NativeAllocationRegistry.createMalloced(MeshSpecification.class.getClassLoader(), Mesh.nativeGetFinalizer());

        private MeshHolder() {
        }
    }

    public Mesh(MeshSpecification meshSpec, int mode, Buffer vertexBuffer, int vertexCount, RectF bounds) {
        if (mode != 0 && mode != 1) {
            throw new IllegalArgumentException("Invalid value passed in for mode parameter");
        }
        long nativeMesh = nativeMake(meshSpec.mNativeMeshSpec, mode, vertexBuffer, vertexBuffer.isDirect(), vertexCount, vertexBuffer.position(), bounds.left, bounds.top, bounds.right, bounds.bottom);
        if (nativeMesh == 0) {
            throw new IllegalArgumentException("Mesh construction failed.");
        }
        meshSetup(nativeMesh, false);
    }

    public Mesh(MeshSpecification meshSpec, int mode, Buffer vertexBuffer, int vertexCount, ShortBuffer indexBuffer, RectF bounds) {
        if (mode != 0 && mode != 1) {
            throw new IllegalArgumentException("Invalid value passed in for mode parameter");
        }
        long nativeMesh = nativeMakeIndexed(meshSpec.mNativeMeshSpec, mode, vertexBuffer, vertexBuffer.isDirect(), vertexCount, vertexBuffer.position(), indexBuffer, indexBuffer.isDirect(), indexBuffer.capacity(), indexBuffer.position(), bounds.left, bounds.top, bounds.right, bounds.bottom);
        if (nativeMesh == 0) {
            throw new IllegalArgumentException("Mesh construction failed.");
        }
        meshSetup(nativeMesh, true);
    }

    public void setColorUniform(String uniformName, int color) {
        setUniform(uniformName, Color.valueOf(color).getComponents(), true);
    }

    public void setColorUniform(String uniformName, long color) {
        Color exSRGB = Color.valueOf(color).convert(ColorSpace.get(ColorSpace.Named.EXTENDED_SRGB));
        setUniform(uniformName, exSRGB.getComponents(), true);
    }

    public void setColorUniform(String uniformName, Color color) {
        if (color == null) {
            throw new NullPointerException("The color parameter must not be null");
        }
        Color exSRGB = color.convert(ColorSpace.get(ColorSpace.Named.EXTENDED_SRGB));
        setUniform(uniformName, exSRGB.getComponents(), true);
    }

    public void setFloatUniform(String uniformName, float value) {
        setFloatUniform(uniformName, value, 0.0f, 0.0f, 0.0f, 1);
    }

    public void setFloatUniform(String uniformName, float value1, float value2) {
        setFloatUniform(uniformName, value1, value2, 0.0f, 0.0f, 2);
    }

    public void setFloatUniform(String uniformName, float value1, float value2, float value3) {
        setFloatUniform(uniformName, value1, value2, value3, 0.0f, 3);
    }

    public void setFloatUniform(String uniformName, float value1, float value2, float value3, float value4) {
        setFloatUniform(uniformName, value1, value2, value3, value4, 4);
    }

    public void setFloatUniform(String uniformName, float[] values) {
        setUniform(uniformName, values, false);
    }

    private void setFloatUniform(String uniformName, float value1, float value2, float value3, float value4, int count) {
        if (uniformName == null) {
            throw new NullPointerException("The uniformName parameter must not be null");
        }
        nativeUpdateUniforms(this.mNativeMeshWrapper, uniformName, value1, value2, value3, value4, count);
    }

    private void setUniform(String uniformName, float[] values, boolean isColor) {
        if (uniformName == null) {
            throw new NullPointerException("The uniformName parameter must not be null");
        }
        if (values == null) {
            throw new NullPointerException("The uniform values parameter must not be null");
        }
        nativeUpdateUniforms(this.mNativeMeshWrapper, uniformName, values, isColor);
    }

    public void setIntUniform(String uniformName, int value) {
        setIntUniform(uniformName, value, 0, 0, 0, 1);
    }

    public void setIntUniform(String uniformName, int value1, int value2) {
        setIntUniform(uniformName, value1, value2, 0, 0, 2);
    }

    public void setIntUniform(String uniformName, int value1, int value2, int value3) {
        setIntUniform(uniformName, value1, value2, value3, 0, 3);
    }

    public void setIntUniform(String uniformName, int value1, int value2, int value3, int value4) {
        setIntUniform(uniformName, value1, value2, value3, value4, 4);
    }

    public void setIntUniform(String uniformName, int[] values) {
        if (uniformName == null) {
            throw new NullPointerException("The uniformName parameter must not be null");
        }
        if (values == null) {
            throw new NullPointerException("The uniform values parameter must not be null");
        }
        nativeUpdateUniforms(this.mNativeMeshWrapper, uniformName, values);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long getNativeWrapperInstance() {
        return this.mNativeMeshWrapper;
    }

    private void setIntUniform(String uniformName, int value1, int value2, int value3, int value4, int count) {
        if (uniformName == null) {
            throw new NullPointerException("The uniformName parameter must not be null");
        }
        nativeUpdateUniforms(this.mNativeMeshWrapper, uniformName, value1, value2, value3, value4, count);
    }

    private void meshSetup(long nativeMeshWrapper, boolean isIndexed) {
        this.mNativeMeshWrapper = nativeMeshWrapper;
        this.mIsIndexed = isIndexed;
        MeshHolder.MESH_SPECIFICATION_REGISTRY.registerNativeAllocation(this, this.mNativeMeshWrapper);
    }
}
