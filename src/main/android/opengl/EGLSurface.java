package android.opengl;
/* loaded from: classes2.dex */
public class EGLSurface extends EGLObjectHandle {
    private EGLSurface(long handle) {
        super(handle);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof EGLSurface) {
            EGLSurface that = (EGLSurface) o;
            return getNativeHandle() == that.getNativeHandle();
        }
        return false;
    }
}
