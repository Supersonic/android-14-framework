package android.view;

import android.graphics.Canvas;
import android.graphics.Rect;
/* loaded from: classes4.dex */
public interface SurfaceHolder {
    @Deprecated
    public static final int SURFACE_TYPE_GPU = 2;
    @Deprecated
    public static final int SURFACE_TYPE_HARDWARE = 1;
    @Deprecated
    public static final int SURFACE_TYPE_NORMAL = 0;
    @Deprecated
    public static final int SURFACE_TYPE_PUSH_BUFFERS = 3;

    /* loaded from: classes4.dex */
    public interface Callback {
        void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3);

        void surfaceCreated(SurfaceHolder surfaceHolder);

        void surfaceDestroyed(SurfaceHolder surfaceHolder);
    }

    void addCallback(Callback callback);

    Surface getSurface();

    Rect getSurfaceFrame();

    boolean isCreating();

    Canvas lockCanvas();

    Canvas lockCanvas(Rect rect);

    void removeCallback(Callback callback);

    void setFixedSize(int i, int i2);

    void setFormat(int i);

    void setKeepScreenOn(boolean z);

    void setSizeFromLayout();

    @Deprecated
    void setType(int i);

    void unlockCanvasAndPost(Canvas canvas);

    /* loaded from: classes4.dex */
    public static class BadSurfaceTypeException extends RuntimeException {
        public BadSurfaceTypeException() {
        }

        public BadSurfaceTypeException(String name) {
            super(name);
        }
    }

    /* loaded from: classes4.dex */
    public interface Callback2 extends Callback {
        void surfaceRedrawNeeded(SurfaceHolder surfaceHolder);

        default void surfaceRedrawNeededAsync(SurfaceHolder holder, Runnable drawingFinished) {
            surfaceRedrawNeeded(holder);
            drawingFinished.run();
        }
    }

    default Canvas lockHardwareCanvas() {
        throw new IllegalStateException("This SurfaceHolder doesn't support lockHardwareCanvas");
    }
}
