package android.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RecordingCanvas;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.TextureLayer;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
/* loaded from: classes4.dex */
public class TextureView extends View {
    private static final String LOG_TAG = "TextureView";
    private Canvas mCanvas;
    private boolean mHadSurface;
    private TextureLayer mLayer;
    private SurfaceTextureListener mListener;
    private final Object[] mLock;
    private final Matrix mMatrix;
    private boolean mMatrixChanged;
    private long mNativeWindow;
    private final Object[] mNativeWindowLock;
    private boolean mOpaque;
    private int mSaveCount;
    private SurfaceTexture mSurface;
    private boolean mUpdateLayer;
    private final SurfaceTexture.OnFrameAvailableListener mUpdateListener;
    private boolean mUpdateSurface;

    /* loaded from: classes4.dex */
    public interface SurfaceTextureListener {
        void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2);

        boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture);

        void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2);

        void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture);
    }

    private native void nCreateNativeWindow(SurfaceTexture surfaceTexture);

    private native void nDestroyNativeWindow();

    private static native boolean nLockCanvas(long j, Canvas canvas, Rect rect);

    private static native void nUnlockCanvasAndPost(long j, Canvas canvas);

    public TextureView(Context context) {
        super(context);
        this.mOpaque = true;
        this.mMatrix = new Matrix();
        this.mLock = new Object[0];
        this.mNativeWindowLock = new Object[0];
        this.mUpdateListener = new SurfaceTexture.OnFrameAvailableListener() { // from class: android.view.TextureView$$ExternalSyntheticLambda0
            @Override // android.graphics.SurfaceTexture.OnFrameAvailableListener
            public final void onFrameAvailable(SurfaceTexture surfaceTexture) {
                TextureView.this.lambda$new$0(surfaceTexture);
            }
        };
    }

    public TextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mOpaque = true;
        this.mMatrix = new Matrix();
        this.mLock = new Object[0];
        this.mNativeWindowLock = new Object[0];
        this.mUpdateListener = new SurfaceTexture.OnFrameAvailableListener() { // from class: android.view.TextureView$$ExternalSyntheticLambda0
            @Override // android.graphics.SurfaceTexture.OnFrameAvailableListener
            public final void onFrameAvailable(SurfaceTexture surfaceTexture) {
                TextureView.this.lambda$new$0(surfaceTexture);
            }
        };
    }

    public TextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mOpaque = true;
        this.mMatrix = new Matrix();
        this.mLock = new Object[0];
        this.mNativeWindowLock = new Object[0];
        this.mUpdateListener = new SurfaceTexture.OnFrameAvailableListener() { // from class: android.view.TextureView$$ExternalSyntheticLambda0
            @Override // android.graphics.SurfaceTexture.OnFrameAvailableListener
            public final void onFrameAvailable(SurfaceTexture surfaceTexture) {
                TextureView.this.lambda$new$0(surfaceTexture);
            }
        };
    }

    public TextureView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mOpaque = true;
        this.mMatrix = new Matrix();
        this.mLock = new Object[0];
        this.mNativeWindowLock = new Object[0];
        this.mUpdateListener = new SurfaceTexture.OnFrameAvailableListener() { // from class: android.view.TextureView$$ExternalSyntheticLambda0
            @Override // android.graphics.SurfaceTexture.OnFrameAvailableListener
            public final void onFrameAvailable(SurfaceTexture surfaceTexture) {
                TextureView.this.lambda$new$0(surfaceTexture);
            }
        };
    }

    @Override // android.view.View
    public boolean isOpaque() {
        return this.mOpaque;
    }

    public void setOpaque(boolean opaque) {
        if (opaque != this.mOpaque) {
            this.mOpaque = opaque;
            if (this.mLayer != null) {
                updateLayerAndInvalidate();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isHardwareAccelerated()) {
            Log.m104w(LOG_TAG, "A TextureView or a subclass can only be used with hardware acceleration enabled.");
        }
        if (this.mHadSurface) {
            invalidate(true);
            this.mHadSurface = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDetachedFromWindowInternal() {
        destroyHardwareLayer();
        releaseSurfaceTexture();
        super.onDetachedFromWindowInternal();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void destroyHardwareResources() {
        super.destroyHardwareResources();
        destroyHardwareLayer();
    }

    private void destroyHardwareLayer() {
        TextureLayer textureLayer = this.mLayer;
        if (textureLayer != null) {
            textureLayer.detachSurfaceTexture();
            this.mLayer.close();
            this.mLayer = null;
            this.mMatrixChanged = true;
        }
    }

    private void releaseSurfaceTexture() {
        SurfaceTexture surfaceTexture = this.mSurface;
        if (surfaceTexture != null) {
            boolean shouldRelease = true;
            SurfaceTextureListener surfaceTextureListener = this.mListener;
            if (surfaceTextureListener != null) {
                shouldRelease = surfaceTextureListener.onSurfaceTextureDestroyed(surfaceTexture);
            }
            synchronized (this.mNativeWindowLock) {
                nDestroyNativeWindow();
            }
            if (shouldRelease) {
                this.mSurface.release();
            }
            this.mSurface = null;
            this.mHadSurface = true;
        }
    }

    @Override // android.view.View
    public void setLayerType(int layerType, Paint paint) {
        setLayerPaint(paint);
    }

    @Override // android.view.View
    public void setLayerPaint(Paint paint) {
        if (paint != this.mLayerPaint) {
            this.mLayerPaint = paint;
            invalidate();
        }
    }

    @Override // android.view.View
    public int getLayerType() {
        return 2;
    }

    @Override // android.view.View
    public void buildLayer() {
    }

    @Override // android.view.View
    public void setForeground(Drawable foreground) {
        if (foreground != null && !sTextureViewIgnoresDrawableSetters) {
            throw new UnsupportedOperationException("TextureView doesn't support displaying a foreground drawable");
        }
    }

    @Override // android.view.View
    public void setBackgroundDrawable(Drawable background) {
        if (background != null && !sTextureViewIgnoresDrawableSetters) {
            throw new UnsupportedOperationException("TextureView doesn't support displaying a background drawable");
        }
    }

    @Override // android.view.View
    public final void draw(Canvas canvas) {
        this.mPrivateFlags = (this.mPrivateFlags & (-2097153)) | 32;
        if (canvas.isHardwareAccelerated()) {
            RecordingCanvas recordingCanvas = (RecordingCanvas) canvas;
            TextureLayer layer = getTextureLayer();
            if (layer != null) {
                applyUpdate();
                applyTransformMatrix();
                this.mLayer.setLayerPaint(this.mLayerPaint);
                recordingCanvas.drawTextureLayer(layer);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public final void onDraw(Canvas canvas) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        SurfaceTexture surfaceTexture = this.mSurface;
        if (surfaceTexture != null) {
            surfaceTexture.setDefaultBufferSize(getWidth(), getHeight());
            updateLayer();
            SurfaceTextureListener surfaceTextureListener = this.mListener;
            if (surfaceTextureListener != null) {
                surfaceTextureListener.onSurfaceTextureSizeChanged(this.mSurface, getWidth(), getHeight());
            }
        }
    }

    TextureLayer getTextureLayer() {
        if (this.mLayer == null) {
            if (this.mAttachInfo == null || this.mAttachInfo.mThreadedRenderer == null) {
                return null;
            }
            this.mLayer = this.mAttachInfo.mThreadedRenderer.createTextureLayer();
            boolean createNewSurface = this.mSurface == null;
            if (createNewSurface) {
                SurfaceTexture surfaceTexture = new SurfaceTexture(false);
                this.mSurface = surfaceTexture;
                nCreateNativeWindow(surfaceTexture);
            }
            this.mLayer.setSurfaceTexture(this.mSurface);
            this.mSurface.setDefaultBufferSize(getWidth(), getHeight());
            this.mSurface.setOnFrameAvailableListener(this.mUpdateListener, this.mAttachInfo.mHandler);
            SurfaceTextureListener surfaceTextureListener = this.mListener;
            if (surfaceTextureListener != null && createNewSurface) {
                surfaceTextureListener.onSurfaceTextureAvailable(this.mSurface, getWidth(), getHeight());
            }
            this.mLayer.setLayerPaint(this.mLayerPaint);
        }
        if (this.mUpdateSurface) {
            this.mUpdateSurface = false;
            updateLayer();
            this.mMatrixChanged = true;
            this.mLayer.setSurfaceTexture(this.mSurface);
            this.mSurface.setDefaultBufferSize(getWidth(), getHeight());
        }
        return this.mLayer;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        SurfaceTexture surfaceTexture = this.mSurface;
        if (surfaceTexture != null) {
            if (visibility == 0) {
                if (this.mLayer != null) {
                    surfaceTexture.setOnFrameAvailableListener(this.mUpdateListener, this.mAttachInfo.mHandler);
                }
                updateLayerAndInvalidate();
                return;
            }
            surfaceTexture.setOnFrameAvailableListener(null);
        }
    }

    private void updateLayer() {
        synchronized (this.mLock) {
            this.mUpdateLayer = true;
        }
    }

    private void updateLayerAndInvalidate() {
        synchronized (this.mLock) {
            this.mUpdateLayer = true;
        }
        invalidate();
    }

    private void applyUpdate() {
        if (this.mLayer == null) {
            return;
        }
        synchronized (this.mLock) {
            if (this.mUpdateLayer) {
                this.mUpdateLayer = false;
                this.mLayer.prepare(getWidth(), getHeight(), this.mOpaque);
                this.mLayer.updateSurfaceTexture();
                SurfaceTextureListener surfaceTextureListener = this.mListener;
                if (surfaceTextureListener != null) {
                    surfaceTextureListener.onSurfaceTextureUpdated(this.mSurface);
                }
            }
        }
    }

    public void setTransform(Matrix transform) {
        this.mMatrix.set(transform);
        this.mMatrixChanged = true;
        invalidateParentIfNeeded();
    }

    public Matrix getTransform(Matrix transform) {
        if (transform == null) {
            transform = new Matrix();
        }
        transform.set(this.mMatrix);
        return transform;
    }

    private void applyTransformMatrix() {
        TextureLayer textureLayer;
        if (this.mMatrixChanged && (textureLayer = this.mLayer) != null) {
            textureLayer.setTransform(this.mMatrix);
            this.mMatrixChanged = false;
        }
    }

    public Bitmap getBitmap() {
        return getBitmap(getWidth(), getHeight());
    }

    public Bitmap getBitmap(int width, int height) {
        if (isAvailable() && width > 0 && height > 0) {
            return getBitmap(Bitmap.createBitmap(getResources().getDisplayMetrics(), width, height, Bitmap.Config.ARGB_8888));
        }
        return null;
    }

    public Bitmap getBitmap(Bitmap bitmap) {
        if (bitmap != null && isAvailable()) {
            applyUpdate();
            applyTransformMatrix();
            if (this.mLayer == null && this.mUpdateSurface) {
                getTextureLayer();
            }
            TextureLayer textureLayer = this.mLayer;
            if (textureLayer != null) {
                textureLayer.copyInto(bitmap);
            }
        }
        return bitmap;
    }

    public boolean isAvailable() {
        return this.mSurface != null;
    }

    public Canvas lockCanvas() {
        return lockCanvas(null);
    }

    public Canvas lockCanvas(Rect dirty) {
        if (isAvailable()) {
            if (this.mCanvas == null) {
                this.mCanvas = new Canvas();
            }
            synchronized (this.mNativeWindowLock) {
                if (nLockCanvas(this.mNativeWindow, this.mCanvas, dirty)) {
                    this.mSaveCount = this.mCanvas.save();
                    return this.mCanvas;
                }
                return null;
            }
        }
        return null;
    }

    public void unlockCanvasAndPost(Canvas canvas) {
        Canvas canvas2 = this.mCanvas;
        if (canvas2 != null && canvas == canvas2) {
            canvas.restoreToCount(this.mSaveCount);
            this.mSaveCount = 0;
            synchronized (this.mNativeWindowLock) {
                nUnlockCanvasAndPost(this.mNativeWindow, this.mCanvas);
            }
        }
    }

    public SurfaceTexture getSurfaceTexture() {
        return this.mSurface;
    }

    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        if (surfaceTexture == null) {
            throw new NullPointerException("surfaceTexture must not be null");
        }
        if (surfaceTexture == this.mSurface) {
            throw new IllegalArgumentException("Trying to setSurfaceTexture to the same SurfaceTexture that's already set.");
        }
        if (surfaceTexture.isReleased()) {
            throw new IllegalArgumentException("Cannot setSurfaceTexture to a released SurfaceTexture");
        }
        if (this.mSurface != null) {
            nDestroyNativeWindow();
            this.mSurface.release();
        }
        this.mSurface = surfaceTexture;
        nCreateNativeWindow(surfaceTexture);
        if ((this.mViewFlags & 12) == 0 && this.mLayer != null) {
            this.mSurface.setOnFrameAvailableListener(this.mUpdateListener, this.mAttachInfo.mHandler);
        }
        this.mUpdateSurface = true;
        invalidateParentIfNeeded();
    }

    public SurfaceTextureListener getSurfaceTextureListener() {
        return this.mListener;
    }

    public void setSurfaceTextureListener(SurfaceTextureListener listener) {
        this.mListener = listener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(SurfaceTexture surfaceTexture) {
        updateLayer();
        invalidate();
    }
}
