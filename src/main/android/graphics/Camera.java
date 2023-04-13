package android.graphics;
/* loaded from: classes.dex */
public class Camera {
    private long native_instance;

    private native void nativeApplyToCanvas(long j);

    private native void nativeConstructor();

    private native void nativeDestructor();

    private native void nativeGetMatrix(long j);

    public native float dotWithNormal(float f, float f2, float f3);

    public native float getLocationX();

    public native float getLocationY();

    public native float getLocationZ();

    public native void restore();

    public native void rotate(float f, float f2, float f3);

    public native void rotateX(float f);

    public native void rotateY(float f);

    public native void rotateZ(float f);

    public native void save();

    public native void setLocation(float f, float f2, float f3);

    public native void translate(float f, float f2, float f3);

    public Camera() {
        nativeConstructor();
    }

    public void getMatrix(Matrix matrix) {
        nativeGetMatrix(matrix.m184ni());
    }

    public void applyToCanvas(Canvas canvas) {
        nativeApplyToCanvas(canvas.getNativeCanvasWrapper());
    }

    protected void finalize() throws Throwable {
        try {
            nativeDestructor();
            this.native_instance = 0L;
        } finally {
            super.finalize();
        }
    }
}
