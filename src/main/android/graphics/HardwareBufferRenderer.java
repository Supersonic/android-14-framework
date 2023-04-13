package android.graphics;

import android.graphics.ColorSpace;
import android.hardware.HardwareBuffer;
import android.hardware.SyncFence;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import libcore.util.NativeAllocationRegistry;
/* loaded from: classes.dex */
public class HardwareBufferRenderer implements AutoCloseable {
    private static final ColorSpace DEFAULT_COLORSPACE = ColorSpace.get(ColorSpace.Named.SRGB);
    private final Runnable mCleaner;
    private final HardwareBuffer mHardwareBuffer;
    private long mProxy;
    private final RenderRequest mRenderRequest;
    private final RenderNode mRootNode;

    private static native long nCreateHardwareBufferRenderer(HardwareBuffer hardwareBuffer, long j);

    private static native long nCreateRootRenderNode();

    private static native void nDestroyRootRenderNode(long j);

    /* JADX INFO: Access modifiers changed from: private */
    public static native long nGetFinalizer();

    static native int nRender(long j, int i, int i2, int i3, long j2, Consumer<RenderResult> consumer);

    private static native void nSetLightAlpha(long j, float f, float f2);

    private static native void nSetLightGeometry(long j, float f, float f2, float f3, float f4);

    /* loaded from: classes.dex */
    private static class HardwareBufferRendererHolder {
        public static final NativeAllocationRegistry REGISTRY = NativeAllocationRegistry.createMalloced(HardwareBufferRenderer.class.getClassLoader(), HardwareBufferRenderer.nGetFinalizer());

        private HardwareBufferRendererHolder() {
        }
    }

    public HardwareBufferRenderer(HardwareBuffer buffer) {
        RenderNode rootNode = RenderNode.adopt(nCreateRootRenderNode());
        rootNode.setClipToBounds(false);
        this.mProxy = nCreateHardwareBufferRenderer(buffer, rootNode.mNativeRenderNode);
        this.mCleaner = HardwareBufferRendererHolder.REGISTRY.registerNativeAllocation(this, this.mProxy);
        this.mRenderRequest = new RenderRequest();
        this.mRootNode = rootNode;
        this.mHardwareBuffer = buffer;
    }

    public void setContentRoot(RenderNode content) {
        RecordingCanvas canvas = this.mRootNode.beginRecording();
        if (content != null) {
            canvas.drawRenderNode(content);
        }
        this.mRootNode.endRecording();
    }

    public RenderRequest obtainRenderRequest() {
        this.mRenderRequest.reset();
        return this.mRenderRequest;
    }

    public boolean isClosed() {
        return this.mProxy == 0;
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        nDestroyRootRenderNode(this.mRootNode.mNativeRenderNode);
        if (this.mProxy != 0) {
            this.mCleaner.run();
            this.mProxy = 0L;
        }
    }

    public void setLightSourceGeometry(float lightX, float lightY, float lightZ, float lightRadius) {
        validateFinite(lightX, "lightX");
        validateFinite(lightY, "lightY");
        validatePositive(lightZ, "lightZ");
        validatePositive(lightRadius, "lightRadius");
        nSetLightGeometry(this.mProxy, lightX, lightY, lightZ, lightRadius);
    }

    public void setLightSourceAlpha(float ambientShadowAlpha, float spotShadowAlpha) {
        validateAlpha(ambientShadowAlpha, "ambientShadowAlpha");
        validateAlpha(spotShadowAlpha, "spotShadowAlpha");
        nSetLightAlpha(this.mProxy, ambientShadowAlpha, spotShadowAlpha);
    }

    /* loaded from: classes.dex */
    public static final class RenderResult {
        public static final int ERROR_UNKNOWN = 1;
        public static final int SUCCESS = 0;
        private final SyncFence mFence;
        private final int mResultStatus;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes.dex */
        public @interface RenderResultStatus {
        }

        private RenderResult(SyncFence fence, int resultStatus) {
            this.mFence = fence;
            this.mResultStatus = resultStatus;
        }

        public SyncFence getFence() {
            return this.mFence;
        }

        public int getStatus() {
            return this.mResultStatus;
        }
    }

    /* loaded from: classes.dex */
    public final class RenderRequest {
        private ColorSpace mColorSpace;
        private int mTransform;

        private RenderRequest() {
            this.mColorSpace = HardwareBufferRenderer.DEFAULT_COLORSPACE;
            this.mTransform = 0;
        }

        public void draw(final Executor executor, final Consumer<RenderResult> renderCallback) {
            Consumer<RenderResult> wrapped = new Consumer() { // from class: android.graphics.HardwareBufferRenderer$RenderRequest$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    executor.execute(new Runnable() { // from class: android.graphics.HardwareBufferRenderer$RenderRequest$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            r1.accept(r2);
                        }
                    });
                }
            };
            if (!HardwareBufferRenderer.this.isClosed()) {
                HardwareBufferRenderer.nRender(HardwareBufferRenderer.this.mProxy, this.mTransform, HardwareBufferRenderer.this.mHardwareBuffer.getWidth(), HardwareBufferRenderer.this.mHardwareBuffer.getHeight(), this.mColorSpace.getNativeInstance(), wrapped);
                return;
            }
            throw new IllegalStateException("Attempt to draw with a HardwareBufferRenderer instance that has already been closed");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void reset() {
            this.mColorSpace = HardwareBufferRenderer.DEFAULT_COLORSPACE;
            this.mTransform = 0;
        }

        public RenderRequest setColorSpace(ColorSpace colorSpace) {
            if (colorSpace == null) {
                this.mColorSpace = HardwareBufferRenderer.DEFAULT_COLORSPACE;
            } else {
                this.mColorSpace = colorSpace;
            }
            return this;
        }

        public RenderRequest setBufferTransform(int bufferTransform) {
            boolean validTransform = bufferTransform == 0 || bufferTransform == 4 || bufferTransform == 3 || bufferTransform == 7;
            if (validTransform) {
                this.mTransform = bufferTransform;
                return this;
            }
            throw new IllegalArgumentException("Invalid transform provided, must be one ofthe SurfaceControl.BufferTransform values");
        }
    }

    private static void invokeRenderCallback(Consumer<RenderResult> callback, int fd, int status) {
        callback.accept(new RenderResult(SyncFence.adopt(fd), status));
    }

    private static void validateAlpha(float alpha, String argumentName) {
        if (alpha < 0.0f || alpha > 1.0f) {
            throw new IllegalArgumentException(argumentName + " must be a valid alpha, " + alpha + " is not in the range of 0.0f to 1.0f");
        }
    }

    private static void validateFinite(float f, String argumentName) {
        if (!Float.isFinite(f)) {
            throw new IllegalArgumentException(argumentName + " must be finite, given=" + f);
        }
    }

    private static void validatePositive(float f, String argumentName) {
        if (!Float.isFinite(f) || f < 0.0f) {
            throw new IllegalArgumentException(argumentName + " must be a finite positive, given=" + f);
        }
    }
}
