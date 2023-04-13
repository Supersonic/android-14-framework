package android.graphics;

import android.graphics.PorterDuff;
/* loaded from: classes.dex */
public class ComposeShader extends Shader {
    private long mNativeInstanceShaderA;
    private long mNativeInstanceShaderB;
    private int mPorterDuffMode;
    Shader mShaderA;
    Shader mShaderB;

    private static native long nativeCreate(long j, long j2, long j3, int i);

    @Deprecated
    public ComposeShader(Shader shaderA, Shader shaderB, Xfermode mode) {
        this(shaderA, shaderB, mode.porterDuffMode);
    }

    public ComposeShader(Shader shaderA, Shader shaderB, PorterDuff.Mode mode) {
        this(shaderA, shaderB, mode.nativeInt);
    }

    public ComposeShader(Shader shaderA, Shader shaderB, BlendMode blendMode) {
        this(shaderA, shaderB, blendMode.getXfermode().porterDuffMode);
    }

    private ComposeShader(Shader shaderA, Shader shaderB, int nativeMode) {
        if (shaderA == null || shaderB == null) {
            throw new IllegalArgumentException("Shader parameters must not be null");
        }
        this.mShaderA = shaderA;
        this.mShaderB = shaderB;
        this.mPorterDuffMode = nativeMode;
    }

    @Override // android.graphics.Shader
    protected long createNativeInstance(long nativeMatrix, boolean filterFromPaint) {
        this.mNativeInstanceShaderA = this.mShaderA.getNativeInstance(filterFromPaint);
        this.mNativeInstanceShaderB = this.mShaderB.getNativeInstance(filterFromPaint);
        return nativeCreate(nativeMatrix, this.mShaderA.getNativeInstance(), this.mShaderB.getNativeInstance(), this.mPorterDuffMode);
    }

    @Override // android.graphics.Shader
    protected boolean shouldDiscardNativeInstance(boolean filterFromPaint) {
        return (this.mShaderA.getNativeInstance(filterFromPaint) == this.mNativeInstanceShaderA && this.mShaderB.getNativeInstance(filterFromPaint) == this.mNativeInstanceShaderB) ? false : true;
    }
}
