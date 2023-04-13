package android.graphics;

import android.p008os.SystemProperties;
import android.util.Pools;
import dalvik.annotation.optimization.CriticalNative;
/* loaded from: classes.dex */
public final class RecordingCanvas extends BaseRecordingCanvas {
    private static final int POOL_LIMIT = 25;
    private int mHeight;
    public RenderNode mNode;
    private int mWidth;
    public static final int MAX_BITMAP_SIZE = getPanelFrameSize();
    private static final Pools.SynchronizedPool<RecordingCanvas> sPool = new Pools.SynchronizedPool<>(25);

    @CriticalNative
    private static native long nCreateDisplayListCanvas(long j, int i, int i2);

    @CriticalNative
    private static native void nDrawCircle(long j, long j2, long j3, long j4, long j5);

    @CriticalNative
    private static native void nDrawRenderNode(long j, long j2);

    @CriticalNative
    private static native void nDrawRipple(long j, long j2, long j3, long j4, long j5, long j6, long j7, int i, long j8);

    @CriticalNative
    private static native void nDrawRoundRect(long j, long j2, long j3, long j4, long j5, long j6, long j7, long j8);

    @CriticalNative
    private static native void nDrawTextureLayer(long j, long j2);

    @CriticalNative
    private static native void nDrawWebViewFunctor(long j, int i);

    @CriticalNative
    private static native void nEnableZ(long j, boolean z);

    @CriticalNative
    private static native void nFinishRecording(long j, long j2);

    @CriticalNative
    private static native int nGetMaximumTextureHeight();

    @CriticalNative
    private static native int nGetMaximumTextureWidth();

    @CriticalNative
    private static native void nResetDisplayListCanvas(long j, long j2, int i, int i2);

    private static int getPanelFrameSize() {
        return Math.max(SystemProperties.getInt("ro.hwui.max_texture_allocation_size", 104857600), 104857600);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static RecordingCanvas obtain(RenderNode node, int width, int height) {
        if (node == null) {
            throw new IllegalArgumentException("node cannot be null");
        }
        RecordingCanvas canvas = sPool.acquire();
        if (canvas == null) {
            canvas = new RecordingCanvas(node, width, height);
        } else {
            nResetDisplayListCanvas(canvas.mNativeCanvasWrapper, node.mNativeRenderNode, width, height);
        }
        canvas.mNode = node;
        canvas.mWidth = width;
        canvas.mHeight = height;
        return canvas;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void recycle() {
        this.mNode = null;
        sPool.release(this);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void finishRecording(RenderNode node) {
        nFinishRecording(this.mNativeCanvasWrapper, node.mNativeRenderNode);
    }

    private RecordingCanvas(RenderNode node, int width, int height) {
        super(nCreateDisplayListCanvas(node.mNativeRenderNode, width, height));
        this.mDensity = 0;
    }

    @Override // android.graphics.Canvas
    public void setDensity(int density) {
    }

    @Override // android.graphics.Canvas, android.graphics.BaseCanvas
    public boolean isHardwareAccelerated() {
        return true;
    }

    @Override // android.graphics.Canvas
    public void setBitmap(Bitmap bitmap) {
        throw new UnsupportedOperationException();
    }

    @Override // android.graphics.Canvas
    public boolean isOpaque() {
        return false;
    }

    @Override // android.graphics.Canvas
    public int getWidth() {
        return this.mWidth;
    }

    @Override // android.graphics.Canvas
    public int getHeight() {
        return this.mHeight;
    }

    @Override // android.graphics.Canvas
    public int getMaximumBitmapWidth() {
        return nGetMaximumTextureWidth();
    }

    @Override // android.graphics.Canvas
    public int getMaximumBitmapHeight() {
        return nGetMaximumTextureHeight();
    }

    @Override // android.graphics.Canvas
    public void enableZ() {
        nEnableZ(this.mNativeCanvasWrapper, true);
    }

    @Override // android.graphics.Canvas
    public void disableZ() {
        nEnableZ(this.mNativeCanvasWrapper, false);
    }

    public void drawWebViewFunctor(int functor) {
        nDrawWebViewFunctor(this.mNativeCanvasWrapper, functor);
    }

    @Override // android.graphics.Canvas
    public void drawRenderNode(RenderNode renderNode) {
        nDrawRenderNode(this.mNativeCanvasWrapper, renderNode.mNativeRenderNode);
    }

    public void drawTextureLayer(TextureLayer layer) {
        nDrawTextureLayer(this.mNativeCanvasWrapper, layer.getLayerHandle());
    }

    public void drawCircle(CanvasProperty<Float> cx, CanvasProperty<Float> cy, CanvasProperty<Float> radius, CanvasProperty<Paint> paint) {
        nDrawCircle(this.mNativeCanvasWrapper, cx.getNativeContainer(), cy.getNativeContainer(), radius.getNativeContainer(), paint.getNativeContainer());
    }

    public void drawRipple(CanvasProperty<Float> cx, CanvasProperty<Float> cy, CanvasProperty<Float> radius, CanvasProperty<Paint> paint, CanvasProperty<Float> progress, CanvasProperty<Float> turbulencePhase, int color, RuntimeShader shader) {
        nDrawRipple(this.mNativeCanvasWrapper, cx.getNativeContainer(), cy.getNativeContainer(), radius.getNativeContainer(), paint.getNativeContainer(), progress.getNativeContainer(), turbulencePhase.getNativeContainer(), color, shader.getNativeShaderBuilder());
    }

    public void drawRoundRect(CanvasProperty<Float> left, CanvasProperty<Float> top, CanvasProperty<Float> right, CanvasProperty<Float> bottom, CanvasProperty<Float> rx, CanvasProperty<Float> ry, CanvasProperty<Paint> paint) {
        nDrawRoundRect(this.mNativeCanvasWrapper, left.getNativeContainer(), top.getNativeContainer(), right.getNativeContainer(), bottom.getNativeContainer(), rx.getNativeContainer(), ry.getNativeContainer(), paint.getNativeContainer());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.graphics.BaseCanvas
    public void throwIfCannotDraw(Bitmap bitmap) {
        super.throwIfCannotDraw(bitmap);
        int bitmapSize = bitmap.getByteCount();
        if (bitmapSize > MAX_BITMAP_SIZE) {
            throw new RuntimeException("Canvas: trying to draw too large(" + bitmapSize + "bytes) bitmap.");
        }
    }
}
