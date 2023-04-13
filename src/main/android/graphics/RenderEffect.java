package android.graphics;

import android.graphics.Shader;
import libcore.util.NativeAllocationRegistry;
/* loaded from: classes.dex */
public final class RenderEffect {
    private final long mNativeRenderEffect;

    private static native long nativeCreateBitmapEffect(long j, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8);

    private static native long nativeCreateBlendModeEffect(long j, long j2, int i);

    private static native long nativeCreateBlurEffect(float f, float f2, long j, int i);

    private static native long nativeCreateChainEffect(long j, long j2);

    private static native long nativeCreateColorFilterEffect(long j, long j2);

    private static native long nativeCreateOffsetEffect(float f, float f2, long j);

    private static native long nativeCreateRuntimeShaderEffect(long j, String str);

    private static native long nativeCreateShaderEffect(long j);

    /* JADX INFO: Access modifiers changed from: private */
    public static native long nativeGetFinalizer();

    /* loaded from: classes.dex */
    private static class RenderEffectHolder {
        public static final NativeAllocationRegistry RENDER_EFFECT_REGISTRY = NativeAllocationRegistry.createMalloced(RenderEffect.class.getClassLoader(), RenderEffect.nativeGetFinalizer());

        private RenderEffectHolder() {
        }
    }

    public static RenderEffect createOffsetEffect(float offsetX, float offsetY) {
        return new RenderEffect(nativeCreateOffsetEffect(offsetX, offsetY, 0L));
    }

    public static RenderEffect createOffsetEffect(float offsetX, float offsetY, RenderEffect input) {
        return new RenderEffect(nativeCreateOffsetEffect(offsetX, offsetY, input.getNativeInstance()));
    }

    public static RenderEffect createBlurEffect(float radiusX, float radiusY, RenderEffect inputEffect, Shader.TileMode edgeTreatment) {
        long nativeInputEffect = inputEffect != null ? inputEffect.mNativeRenderEffect : 0L;
        return new RenderEffect(nativeCreateBlurEffect(radiusX, radiusY, nativeInputEffect, edgeTreatment.nativeInt));
    }

    public static RenderEffect createBlurEffect(float radiusX, float radiusY, Shader.TileMode edgeTreatment) {
        return new RenderEffect(nativeCreateBlurEffect(radiusX, radiusY, 0L, edgeTreatment.nativeInt));
    }

    public static RenderEffect createBitmapEffect(Bitmap bitmap) {
        float right = bitmap.getWidth();
        float bottom = bitmap.getHeight();
        return new RenderEffect(nativeCreateBitmapEffect(bitmap.getNativeInstance(), 0.0f, 0.0f, right, bottom, 0.0f, 0.0f, right, bottom));
    }

    public static RenderEffect createBitmapEffect(Bitmap bitmap, Rect src, Rect dst) {
        int i;
        long bitmapHandle = bitmap.getNativeInstance();
        int i2 = 0;
        if (src != null) {
            i = src.left;
        } else {
            i = 0;
        }
        int left = i;
        if (src != null) {
            i2 = src.top;
        }
        int top = i2;
        int right = src == null ? bitmap.getWidth() : src.right;
        int bottom = src == null ? bitmap.getHeight() : src.bottom;
        return new RenderEffect(nativeCreateBitmapEffect(bitmapHandle, left, top, right, bottom, dst.left, dst.top, dst.right, dst.bottom));
    }

    public static RenderEffect createColorFilterEffect(ColorFilter colorFilter, RenderEffect renderEffect) {
        return new RenderEffect(nativeCreateColorFilterEffect(colorFilter.getNativeInstance(), renderEffect.getNativeInstance()));
    }

    public static RenderEffect createColorFilterEffect(ColorFilter colorFilter) {
        return new RenderEffect(nativeCreateColorFilterEffect(colorFilter.getNativeInstance(), 0L));
    }

    public static RenderEffect createBlendModeEffect(RenderEffect dst, RenderEffect src, BlendMode blendMode) {
        return new RenderEffect(nativeCreateBlendModeEffect(dst.getNativeInstance(), src.getNativeInstance(), blendMode.getXfermode().porterDuffMode));
    }

    public static RenderEffect createChainEffect(RenderEffect outer, RenderEffect inner) {
        return new RenderEffect(nativeCreateChainEffect(outer.getNativeInstance(), inner.getNativeInstance()));
    }

    public static RenderEffect createShaderEffect(Shader shader) {
        return new RenderEffect(nativeCreateShaderEffect(shader.getNativeInstance()));
    }

    public static RenderEffect createRuntimeShaderEffect(RuntimeShader shader, String uniformShaderName) {
        return new RenderEffect(nativeCreateRuntimeShaderEffect(shader.getNativeShaderBuilder(), uniformShaderName));
    }

    private RenderEffect(long nativeRenderEffect) {
        this.mNativeRenderEffect = nativeRenderEffect;
        RenderEffectHolder.RENDER_EFFECT_REGISTRY.registerNativeAllocation(this, nativeRenderEffect);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long getNativeInstance() {
        return this.mNativeRenderEffect;
    }
}
