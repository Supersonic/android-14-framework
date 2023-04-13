package android.opengl;
/* loaded from: classes2.dex */
public class EGLDisplay extends EGLObjectHandle {
    private EGLDisplay(long handle) {
        super(handle);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof EGLDisplay) {
            EGLDisplay that = (EGLDisplay) o;
            return getNativeHandle() == that.getNativeHandle();
        }
        return false;
    }
}
