package android.graphics;

import android.graphics.ColorSpace;
import libcore.util.NativeAllocationRegistry;
/* loaded from: classes.dex */
public class RuntimeShader extends Shader {
    private long mNativeInstanceRuntimeShaderBuilder;

    private static native long nativeCreateBuilder(String str);

    private static native long nativeCreateShader(long j, long j2);

    /* JADX INFO: Access modifiers changed from: private */
    public static native long nativeGetFinalizer();

    private static native void nativeUpdateShader(long j, String str, long j2);

    private static native void nativeUpdateUniforms(long j, String str, float f, float f2, float f3, float f4, int i);

    private static native void nativeUpdateUniforms(long j, String str, int i, int i2, int i3, int i4, int i5);

    private static native void nativeUpdateUniforms(long j, String str, float[] fArr, boolean z);

    private static native void nativeUpdateUniforms(long j, String str, int[] iArr);

    /* loaded from: classes.dex */
    private static class NoImagePreloadHolder {
        public static final NativeAllocationRegistry sRegistry = NativeAllocationRegistry.createMalloced(RuntimeShader.class.getClassLoader(), RuntimeShader.nativeGetFinalizer());

        private NoImagePreloadHolder() {
        }
    }

    public RuntimeShader(String shader) {
        super(ColorSpace.get(ColorSpace.Named.SRGB));
        if (shader == null) {
            throw new NullPointerException("RuntimeShader requires a non-null AGSL string");
        }
        this.mNativeInstanceRuntimeShaderBuilder = nativeCreateBuilder(shader);
        NoImagePreloadHolder.sRegistry.registerNativeAllocation(this, this.mNativeInstanceRuntimeShaderBuilder);
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
        nativeUpdateUniforms(this.mNativeInstanceRuntimeShaderBuilder, uniformName, value1, value2, value3, value4, count);
        discardNativeInstance();
    }

    private void setUniform(String uniformName, float[] values, boolean isColor) {
        if (uniformName == null) {
            throw new NullPointerException("The uniformName parameter must not be null");
        }
        if (values == null) {
            throw new NullPointerException("The uniform values parameter must not be null");
        }
        nativeUpdateUniforms(this.mNativeInstanceRuntimeShaderBuilder, uniformName, values, isColor);
        discardNativeInstance();
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
        nativeUpdateUniforms(this.mNativeInstanceRuntimeShaderBuilder, uniformName, values);
        discardNativeInstance();
    }

    private void setIntUniform(String uniformName, int value1, int value2, int value3, int value4, int count) {
        if (uniformName == null) {
            throw new NullPointerException("The uniformName parameter must not be null");
        }
        nativeUpdateUniforms(this.mNativeInstanceRuntimeShaderBuilder, uniformName, value1, value2, value3, value4, count);
        discardNativeInstance();
    }

    public void setInputShader(String shaderName, Shader shader) {
        if (shaderName == null) {
            throw new NullPointerException("The shaderName parameter must not be null");
        }
        if (shader == null) {
            throw new NullPointerException("The shader parameter must not be null");
        }
        nativeUpdateShader(this.mNativeInstanceRuntimeShaderBuilder, shaderName, shader.getNativeInstance());
        discardNativeInstance();
    }

    public void setInputBuffer(String shaderName, BitmapShader shader) {
        if (shaderName == null) {
            throw new NullPointerException("The shaderName parameter must not be null");
        }
        if (shader == null) {
            throw new NullPointerException("The shader parameter must not be null");
        }
        nativeUpdateShader(this.mNativeInstanceRuntimeShaderBuilder, shaderName, shader.getNativeInstanceWithDirectSampling());
        discardNativeInstance();
    }

    @Override // android.graphics.Shader
    protected long createNativeInstance(long nativeMatrix, boolean filterFromPaint) {
        return nativeCreateShader(this.mNativeInstanceRuntimeShaderBuilder, nativeMatrix);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public long getNativeShaderBuilder() {
        return this.mNativeInstanceRuntimeShaderBuilder;
    }
}
